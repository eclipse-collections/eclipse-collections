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

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * Parses the method-category index at the top of a type's Javadoc.
 * <p>
 * Parsing starts at the sentence "The methods in [type] are organized into ...", so only the category index is scanned.
 */
public final class JavadocCategoryIndexParser
{
    private static final Pattern TYPE_DECLARATION = Pattern.compile(
            "\\b(?:public\\s+)?(?:final\\s+|abstract\\s+|sealed\\s+|non-sealed\\s+)*"
                    + "(?:interface|class|enum)\\s+(\\w+)");
    private static final Pattern CATEGORY_INDEX_INTRODUCTION = Pattern.compile(
            "The methods in\\s+(\\w+)\\s+are organized into");
    private static final Pattern CATEGORY_BLOCK = Pattern.compile(
            "<li><b>[\\s*]*([A-Za-z]+)[\\s*]+([^<]*?)[\\s*]*</b>[\\s*]*<ul><li>(.*?)</li></ul>",
            Pattern.DOTALL);
    private static final Pattern METHOD_LINK = Pattern.compile("\\{@link #([^}]+)}");

    private JavadocCategoryIndexParser()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    static ImmutableSetMultimap<MethodCategory, String> parse(Class<?> type, CharSequence source)
    {
        String categoryIndex = JavadocCategoryIndexParser.categoryIndex(type, source.toString());
        MutableSetMultimap<MethodCategory, String> result = Multimaps.mutable.set.empty();

        JavadocCategoryIndexParser.matches(CATEGORY_BLOCK, categoryIndex).forEach(categoryMatch ->
        {
            String categoryName = categoryMatch.group(1);
            String icon = categoryMatch.group(2).trim();
            String categoryLinks = categoryMatch.group(3);

            MethodCategory category = new MethodCategory(categoryName, icon);
            JavadocCategoryIndexParser.matches(METHOD_LINK, categoryLinks)
                    .forEach(linkMatch -> result.put(category, linkMatch.group(1).trim()));
        });
        return result.toImmutable();
    }

    /**
     * Returns the Javadoc comment that immediately precedes the declaration of {@code type} in {@code source}.
     *
     * @throws IllegalArgumentException if the declaration or its Javadoc comment cannot be located
     */
    public static String classJavadoc(Class<?> type, String source)
    {
        int declarationStart = JavadocCategoryIndexParser.matches(TYPE_DECLARATION, source)
                .detectOptional(declaration -> declaration.group(1).equals(type.getSimpleName()))
                .map(MatchResult::start)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Could not locate the declaration of " + type.getName() + " in the source"));

        String beforeDeclaration = source.substring(0, declarationStart);
        int javadocStart = beforeDeclaration.lastIndexOf("/**");
        if (javadocStart < 0)
        {
            throw new IllegalArgumentException("Could not locate the class Javadoc for " + type.getName());
        }
        int javadocEnd = beforeDeclaration.indexOf("*/", javadocStart);
        if (javadocEnd < 0)
        {
            throw new IllegalArgumentException("Could not locate the class Javadoc for " + type.getName());
        }
        return beforeDeclaration.substring(javadocStart, javadocEnd + 2);
    }

    /**
     * Returns the category-index section of the class Javadoc, starting right after the sentence
     * "The methods in [type] are organized into ...".
     *
     * @throws IllegalArgumentException if the introduction sentence cannot be located
     */
    static String categoryIndex(Class<?> type, String source)
    {
        String javadoc = JavadocCategoryIndexParser.classJavadoc(type, source);
        return JavadocCategoryIndexParser.matches(CATEGORY_INDEX_INTRODUCTION, javadoc)
                .detectOptional(introduction -> introduction.group(1).equals(type.getSimpleName()))
                .map(introduction -> javadoc.substring(introduction.end()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Could not locate the category index introduction for " + type.getName()));
    }

    private static LazyIterable<MatchResult> matches(Pattern pattern, CharSequence input)
    {
        return LazyIterate.adapt(pattern.matcher(input).results().toList());
    }
}
