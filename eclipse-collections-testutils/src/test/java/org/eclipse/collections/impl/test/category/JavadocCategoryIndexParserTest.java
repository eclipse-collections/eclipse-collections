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

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JavadocCategoryIndexParserTest
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
                void select();

                void collectIf();

                void unannotated();
            }
            """;

    private static final String SOURCE_WITH_EARLY_LIST = """
            package example;

            /**
             * A sample type.
             * <ul>
             * <li><b>Ignored \uD83D\uDEAB</b>
             * <ul><li>
             * {@link #unannotated()}
             * </li></ul>
             * </ul>
             * The methods in Sample are organized into method categories via category annotations.
             * <ul>
             * <li><b>Filtering \uD83D\uDEB0</b>
             * <ul><li>
             * {@link #select()}
             * </li></ul>
             * </ul>
             */
            interface Sample
            {
                void select();

                void collectIf();

                void unannotated();
            }
            """;

    private static final MethodCategory FILTERING = new MethodCategory("Filtering", "\uD83D\uDEB0");
    private static final MethodCategory TRANSFORMING = new MethodCategory("Transforming", "\uD83E\uDD8B");

    @Test
    public void parseExtractsCategoriesAndLinks()
    {
        ImmutableSetMultimap<MethodCategory, String> parsed = JavadocCategoryIndexParser.parse(Sample.class, SOURCE);

        assertEquals(Sets.immutable.with(FILTERING, TRANSFORMING), parsed.keysView().toSet());
        assertEquals(Sets.immutable.with("select()", "collectIf()"), parsed.get(FILTERING).toSet());
        assertEquals(Sets.immutable.with("collectIf()"), parsed.get(TRANSFORMING).toSet());
    }

    @Test
    public void parseIgnoresContentBeforeTheIntroduction()
    {
        ImmutableSetMultimap<MethodCategory, String> parsed =
                JavadocCategoryIndexParser.parse(Sample.class, SOURCE_WITH_EARLY_LIST);

        assertEquals(Sets.immutable.with(FILTERING), parsed.keysView().toSet());
        assertEquals(Sets.immutable.with("select()"), parsed.get(FILTERING).toSet());
    }

    @Test
    public void classJavadocReturnsCommentBeforeDeclaration()
    {
        String javadoc = JavadocCategoryIndexParser.classJavadoc(Sample.class, SOURCE);

        assertTrue(javadoc.startsWith("/**"));
        assertTrue(javadoc.endsWith("*/"));
        assertTrue(javadoc.contains("A sample type."));
        assertFalse(javadoc.contains("interface Sample"));
    }

    @Test
    public void classJavadocHandlesAnnotationsBeforeDeclaration()
    {
        String source = """
                package example;

                /** A sample type. */
                @SuppressWarnings("unused")
                public interface Sample
                {
                }
                """;

        assertEquals("/** A sample type. */", JavadocCategoryIndexParser.classJavadoc(Sample.class, source));
    }

    @Test(expected = IllegalArgumentException.class)
    public void classJavadocThrowsWhenDeclarationMissing()
    {
        JavadocCategoryIndexParser.classJavadoc(Sample.class, "package example;\n\nfinal class Other {}\n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void classJavadocThrowsWhenJavadocMissing()
    {
        JavadocCategoryIndexParser.classJavadoc(Sample.class, "package example;\n\ninterface Sample {}\n");
    }

    @Test
    public void categoryIndexStartsAfterIntroduction()
    {
        String categoryIndex = JavadocCategoryIndexParser.categoryIndex(Sample.class, SOURCE);

        assertTrue(categoryIndex.contains("<b>Filtering"));
        assertTrue(categoryIndex.contains("<b>Transforming"));
        assertFalse(categoryIndex.contains("A sample type."));
    }

    @Test(expected = IllegalArgumentException.class)
    public void categoryIndexThrowsWhenIntroductionMissing()
    {
        String source = """
                package example;

                /** A sample type. */
                interface Sample
                {
                }
                """;
        JavadocCategoryIndexParser.categoryIndex(Sample.class, source);
    }

    private interface Sample
    {
        void select();

        void collectIf();

        void unannotated();
    }
}
