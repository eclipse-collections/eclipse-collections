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

import java.lang.reflect.Method;

import org.eclipse.collections.api.annotation.category.Filtering;
import org.eclipse.collections.api.annotation.category.Transforming;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.SetIterable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CategoryIndexTest
{
    private static final String SOURCE = """
            package example;

            /**
             * A sample type.
             * <p>
             * The methods in Sample are organized into method categories via category annotations.
             * Links are provided below as a convenience to help discover specific methods in Javadoc.
             *
             * <ul>
             * <li><b>Filtering \uD83D\uDEB0</b>
             * <ul><li>
             * {@link #select()}, {@link #collectIf()}
             * </li></ul>
             * <li><b>Transforming \uD83E\uDD8B</b>
             * <ul><li>
             * {@link #collectIf()}
             * </li></ul>
             * </ul>
             */
            interface Sample
            {
                @Filtering
                void select();

                @Filtering
                @Transforming
                void collectIf();

                void unannotated();
            }
            """;

    private static final String WRONG_ICON_SOURCE = """
            package example;

            /**
             * A sample type.
             * <p>
             * The methods in Sample are organized into method categories via category annotations.
             * Links are provided below as a convenience to help discover specific methods in Javadoc.
             *
             * <ul>
             * <li><b>Filtering \uD83D\uDEB1</b>
             * <ul><li>
             * {@link #select()}
             * </li></ul>
             * </ul>
             */
            interface Sample
            {
                @Filtering
                void select();

                @Filtering
                @Transforming
                void collectIf();

                void unannotated();
            }
            """;

    private static final String COLLISION_SOURCE = """
            package example;

            /**
             * The methods in CollisionSample are organized into method categories via category annotations.
             * <ul>
             * <li><b>Filtering \uD83D\uDEB0</b>
             * <ul><li>
             * {@link #collide(Predicate)}
             * </li></ul>
             * </ul>
             */
            interface CollisionSample
            {
            }
            """;

    private static final MethodCategory FILTERING = new MethodCategory("Filtering", "\uD83D\uDEB0");
    private static final MethodCategory TRANSFORMING = new MethodCategory("Transforming", "\uD83E\uDD8B");

    @Test
    public void fromClassReadsAnnotations() throws Exception
    {
        CategoryIndex index = CategoryIndex.fromClass(Sample.class);

        assertEquals(Sets.immutable.with(FILTERING, TRANSFORMING), index.sortedCategories());
        assertTrue(index.categoryMethods().get(FILTERING).contains(Sample.class.getDeclaredMethod("select")));
        assertTrue(index.categoryMethods().get(FILTERING).contains(Sample.class.getDeclaredMethod("collectIf")));
        assertTrue(index.categoryMethods().get(TRANSFORMING).contains(Sample.class.getDeclaredMethod("collectIf")));
        assertTrue(index.unresolvedLinks().isEmpty());
    }

    @Test
    public void methodsWithoutCategory() throws Exception
    {
        CategoryIndex index = CategoryIndex.fromClass(Sample.class);
        SetIterable<Method> missing = index.methodsWithoutCategory();
        assertEquals(Sets.immutable.with(Sample.class.getDeclaredMethod("unannotated")), missing);
    }

    @Test
    public void fromSourceMatchesReflection()
    {
        CategoryIndex parsed = CategoryIndex.fromSource(Sample.class, SOURCE);
        CategoryIndex reflected = CategoryIndex.fromClass(Sample.class);
        assertTrue(parsed.isEqualTo(reflected));
        assertTrue(parsed.unresolvedLinks().isEmpty());
    }

    @Test
    public void equalityDetectsIconMismatch()
    {
        CategoryIndex parsed = CategoryIndex.fromSource(Sample.class, WRONG_ICON_SOURCE);
        CategoryIndex reflected = CategoryIndex.fromClass(Sample.class);
        assertFalse(parsed.isEqualTo(reflected));
        assertEquals(Sets.immutable.with(FILTERING, TRANSFORMING), reflected.sortedCategories());
        assertEquals(Sets.immutable.with(new MethodCategory("Filtering", "\uD83D\uDEB1")), parsed.sortedCategories());
    }

    @Test
    public void subset()
    {
        CategoryIndex parsed = CategoryIndex.fromSource(Sample.class, WRONG_ICON_SOURCE);
        CategoryIndex reflected = CategoryIndex.fromClass(Sample.class);

        assertTrue(reflected.isSubsetOf(reflected));
        assertFalse(reflected.isSubsetOf(parsed));
        assertFalse(parsed.isSubsetOf(reflected));
    }

    @Test
    public void unresolvedLinksAreReported()
    {
        String source = SOURCE.replace("{@link #select()}", "{@link #doesNotExist()}");
        CategoryIndex parsed = CategoryIndex.fromSource(Sample.class, source);
        CategoryIndex reflected = CategoryIndex.fromClass(Sample.class);

        assertFalse(parsed.unresolvedLinks().isEmpty());
        assertTrue(parsed.unresolvedLinks().contains("doesNotExist()"));
        assertFalse(parsed.isEqualTo(reflected));
    }

    @Test
    public void differenceAndSymmetricDifference()
    {
        CategoryIndex parsed = CategoryIndex.fromSource(Sample.class, WRONG_ICON_SOURCE);
        CategoryIndex reflected = CategoryIndex.fromClass(Sample.class);

        assertEquals(
                Sets.immutable.with(
                        "Filtering \uD83D\uDEB0: collectIf()",
                        "Filtering \uD83D\uDEB0: select()",
                        "Transforming \uD83E\uDD8B: collectIf()"),
                reflected.difference(parsed));
        assertEquals(
                Sets.immutable.with(
                        "Filtering \uD83D\uDEB0: collectIf()",
                        "Filtering \uD83D\uDEB0: select()",
                        "Filtering \uD83D\uDEB1: select()",
                        "Transforming \uD83E\uDD8B: collectIf()"),
                reflected.symmetricDifference(parsed));
    }

    @Test
    public void fromSourceResolvesErasureCollisionsToOneSignature() throws Exception
    {
        CategoryIndex parsed = CategoryIndex.fromSource(CollisionSample.class, COLLISION_SOURCE);

        SetIterable<Method> methods = parsed.categoryMethods().get(FILTERING);
        assertEquals(2, methods.size());
        assertTrue(methods.contains(CollisionSample.class.getDeclaredMethod(
                "collide", org.eclipse.collections.api.block.predicate.Predicate.class)));
        assertTrue(methods.contains(CollisionSample.class.getDeclaredMethod(
                "collide", java.util.function.Predicate.class)));
        assertTrue(parsed.unresolvedLinks().isEmpty());
    }

    @Test
    public void methodCategoryCompareToAndToString()
    {
        assertEquals(0, FILTERING.compareTo(new MethodCategory("Filtering", "\uD83D\uDEB0")));
        assertTrue(FILTERING.compareTo(TRANSFORMING) < 0);
        assertEquals("Filtering \uD83D\uDEB0", FILTERING.toString());
    }

    @Test
    public void toStringContainsTypeAndCategories()
    {
        String toString = CategoryIndex.fromClass(Sample.class).toString();
        assertTrue(toString.contains("Sample"));
        assertTrue(toString.contains("Filtering \uD83D\uDEB0"));
        assertTrue(toString.contains("Transforming \uD83E\uDD8B"));
    }

    private interface Sample
    {
        @Filtering
        void select();

        @Filtering
        @Transforming
        void collectIf();

        void unannotated();
    }

    private interface CollisionSample
    {
        @Filtering
        void collide(org.eclipse.collections.api.block.predicate.Predicate<?> predicate);

        @Filtering
        void collide(java.util.function.Predicate<?> predicate);
    }
}
