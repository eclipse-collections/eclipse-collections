/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.test.category;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.collections.api.annotation.category.Category;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.test.ClassComparer;
import org.eclipse.collections.impl.utility.ArrayIterate;

/**
 * A method-category index for a type, backed by a {@code Class} and a {@code Multimap} of {@link MethodCategory} to
 * {@link Method}.
 * <p>
 * An index can be built in two ways:
 * <ul>
 * <li>{@link #fromClass(Class)} reflects over the declared methods and reads the category annotations.</li>
 * <li>{@link #fromSource(Class, Path)} parses the category index at the top of the type's Javadoc and resolves each
 * Javadoc link back to a declared method.</li>
 * </ul>
 * Two indexes can then be compared for equality or containment, in the spirit of {@link ClassComparer}.
 */
public final class CategoryIndex
{
    private final Class<?> type;
    private final ImmutableSetMultimap<MethodCategory, Method> categories;
    private final ImmutableSet<String> unresolvedLinks;
    private final ImmutableSet<Method> methodsWithoutCategory;

    private CategoryIndex(
            Class<?> type,
            ImmutableSetMultimap<MethodCategory, Method> categories,
            ImmutableSet<String> unresolvedLinks)
    {
        this.type = type;
        this.categories = categories;
        this.unresolvedLinks = unresolvedLinks;
        this.methodsWithoutCategory = CategoryIndex.declaredMethods(type)
                .reject(CategoryIndex::isCategorized)
                .toImmutable();
    }

    /**
     * Builds a category index by reflecting over the declared methods of {@code type} and reading the annotations that
     * are themselves annotated with {@code org.eclipse.collections.api.annotation.category.Category}.
     *
     * @param type the class or interface under test (e.g. {@code ImmutableList})
     */
    public static CategoryIndex fromClass(Class<?> type)
    {
        ImmutableSetMultimap<MethodCategory, Method> categories = CategoryIndex.declaredMethods(type)
                .groupByEach(CategoryIndex::categoriesOf, Multimaps.mutable.set.empty())
                .toImmutable();
        return new CategoryIndex(type, categories, Sets.immutable.empty());
    }

    /**
     * Builds a category index by reading the given Java source file and parsing the category index at the top of the
     * type's Javadoc.
     */
    public static CategoryIndex fromSource(Class<?> type, Path sourceFile) throws IOException
    {
        return CategoryIndex.fromSource(type, Files.readString(sourceFile));
    }

    static CategoryIndex fromSource(Class<?> type, CharSequence source)
    {
        ImmutableSetMultimap<MethodCategory, String> parsed = JavadocCategoryIndexParser.parse(type, source);

        // A Javadoc signature can map to more than one method because it is formatted with simple names only.
        // For example Iterate.removeIf(Iterable, Predicate) matches both the Eclipse Collections
        // org.eclipse.collections.api.block.predicate.Predicate and java.util.function.Predicate overloads.
        MutableSetMultimap<String, Method> methodsBySignature = CategoryIndex.declaredMethods(type)
                .groupBy(method -> JavadocMethodLink.of(method).signature(), Multimaps.mutable.set.empty());

        MutableSetMultimap<MethodCategory, Method> categories = Multimaps.mutable.set.empty();
        MutableSet<String> unresolvedLinks = Sets.mutable.empty();
        parsed.forEachKeyMultiValues((category, signatures) ->
        {
            categories.putAll(category, signatures.flatCollect(methodsBySignature::get));
            unresolvedLinks.addAllIterable(signatures.reject(methodsBySignature::containsKey));
        });

        return new CategoryIndex(type, categories.toImmutable(), unresolvedLinks.toImmutable());
    }

    public Class<?> getType()
    {
        return this.type;
    }

    /**
     * The categorized methods grouped by category.
     */
    ImmutableSetMultimap<MethodCategory, Method> categoryMethods()
    {
        return this.categories;
    }

    /**
     * The categories of this index, sorted lexicographically.
     */
    public ImmutableSortedSet<MethodCategory> sortedCategories()
    {
        return this.categories.keySet().toImmutableSortedSet();
    }

    /**
     * The declared, non-private, non-bridge, non-synthetic methods of the type that carry no category annotation.
     */
    public SetIterable<Method> methodsWithoutCategory()
    {
        return this.methodsWithoutCategory;
    }

    /**
     * Javadoc links that could not be resolved to a declared method.
     */
    public ImmutableSet<String> unresolvedLinks()
    {
        return this.unresolvedLinks;
    }

    /**
     * Exact match: same type, same category/method pairs (comparing both the category name and icon), and no
     * unresolved links on either side.
     */
    public boolean isEqualTo(CategoryIndex other)
    {
        return this.type.equals(other.type)
                && this.categories.equals(other.categories)
                && this.unresolvedLinks.equals(other.unresolvedLinks);
    }

    /**
     * Non-proper containment: every (category, method) pair of this index is present in {@code other}. Equality is
     * allowed, and {@code other} may contain additional pairs.
     */
    public boolean isSubsetOf(CategoryIndex other)
    {
        return this.categories.keyValuePairsView()
                .allSatisfy(pair -> other.categories.containsKeyAndValue(pair.getOne(), pair.getTwo()));
    }

    @Override
    public String toString()
    {
        return "CategoryIndex{" + this.type.getSimpleName()
                + ": " + this.categories
                + ", unresolvedLinks=" + this.unresolvedLinks + '}';
    }

    public SetIterable<String> difference(CategoryIndex other)
    {
        return this.categoryMethodPairs().difference(other.categoryMethodPairs());
    }

    public SetIterable<String> symmetricDifference(CategoryIndex other)
    {
        return this.categoryMethodPairs().symmetricDifference(other.categoryMethodPairs());
    }

    private SetIterable<String> categoryMethodPairs()
    {
        MutableSortedSet<String> pairs = TreeSortedSet.newSet();
        this.categories.forEachKeyValue((category, method) ->
                pairs.add(category + ": " + JavadocMethodLink.of(method).signature()));
        return pairs;
    }

    private static MutableSet<Method> declaredMethods(Class<?> type)
    {
        return ArrayIterate.reject(type.getDeclaredMethods(), CategoryIndex::isIgnored, Sets.mutable.empty());
    }

    private static MutableList<MethodCategory> categoriesOf(AnnotatedElement method)
    {
        return ArrayIterate.collectIf(
                method.getAnnotations(),
                CategoryIndex::isCategoryAnnotation,
                CategoryIndex::toMethodCategory,
                FastList.newList());
    }

    private static boolean isIgnored(Method method)
    {
        return method.isBridge() || method.isSynthetic() || Modifier.isPrivate(method.getModifiers());
    }

    private static boolean isCategorized(AnnotatedElement method)
    {
        return ArrayIterate.anySatisfy(method.getAnnotations(), CategoryIndex::isCategoryAnnotation);
    }

    private static boolean isCategoryAnnotation(Annotation annotation)
    {
        return annotation.annotationType().isAnnotationPresent(Category.class);
    }

    private static MethodCategory toMethodCategory(Annotation annotation)
    {
        Category category = annotation.annotationType().getAnnotation(Category.class);
        return new MethodCategory(category.value(), category.icon());
    }
}
