/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.annotation.category;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.collections.impl.test.category.CategoryIndex;
import org.eclipse.collections.impl.test.category.JavadocCategoryIndexParser;
import org.eclipse.collections.impl.test.category.JavadocCategoryIndexRenderer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the Javadoc category index against the category annotations via reflection.
 * <p>
 * For automatically categorised types the index must be exactly the one rendered from reflection, so it stays in sync
 * with the annotations. For manually categorised types the index only needs to contain every reflected link in its
 * correct category (it may contain extra links), and its categories must be the ones present in reflection.
 */
class JavadocCategoryIndexTest
{
    @ParameterizedTest
    @MethodSource("org.eclipse.collections.api.annotation.category.CategoryTestSources#automatic")
    void automaticIndexIsInSyncWithReflection(Class<?> type, Path sourcePath) throws IOException
    {
        CategoryIndex reflected = CategoryIndex.fromClass(type);
        CategoryIndex parsed = CategoryIndex.fromSource(type, sourcePath);
        assertTrue(
                parsed.isEqualTo(reflected),
                () -> "The Javadoc index for " + type.getSimpleName() + " does not match reflection. Symmetric difference: "
                        + parsed.symmetricDifference(reflected)
                        + " (unresolved links: " + parsed.unresolvedLinks() + ')');

        String javadoc = JavadocCategoryIndexParser.classJavadoc(type, Files.readString(sourcePath));
        String actual = javadoc.substring(
                javadoc.indexOf(" * The methods in"),
                javadoc.lastIndexOf(" * </ul>") + " * </ul>".length());
        assertEquals(
                JavadocCategoryIndexRenderer.render(reflected),
                actual,
                () -> "The Javadoc index does not match the one rendered from reflection in " + sourcePath);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.collections.api.annotation.category.CategoryTestSources#manual")
    void manualIndexContainsAllReflectedLinksInTheirCategories(Class<?> type, Path sourcePath) throws IOException
    {
        CategoryIndex reflected = CategoryIndex.fromClass(type);
        CategoryIndex parsed = CategoryIndex.fromSource(type, sourcePath);
        assertEquals(
                reflected.sortedCategories(),
                parsed.sortedCategories(),
                () -> "The Javadoc index for " + type.getSimpleName() + " has categories that differ from reflection: "
                        + parsed.sortedCategories().symmetricDifference(reflected.sortedCategories()));
        assertTrue(
                reflected.isSubsetOf(parsed),
                () -> "The Javadoc index for " + type.getSimpleName() + " is missing reflected methods: "
                        + reflected.difference(parsed));
    }
}
