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

import org.eclipse.collections.api.annotation.category.Filtering;
import org.eclipse.collections.api.annotation.category.Transforming;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavadocCategoryIndexRendererTest
{
    @Test
    public void render()
    {
        CategoryIndex index = CategoryIndex.fromClass(Sample.class);

        String expected = String.join("\n",
                " * The methods in Sample are organized into method categories via category annotations.",
                " * Links are provided below as a convenience to help discover specific methods in Javadoc.",
                " *",
                " * <ul>",
                " * <li><b>Filtering \uD83D\uDEB0</b>",
                " * <ul><li>",
                " * {@link #collectIf()}, {@link #select()}",
                " * </li></ul>",
                " * <li><b>Transforming \uD83E\uDD8B</b>",
                " * <ul><li>",
                " * {@link #collectIf()}",
                " * </li></ul>",
                " * </ul>");
        assertEquals(expected, JavadocCategoryIndexRenderer.render(index));
    }

    @Test
    public void renderWrapsAtMaxLineLength()
    {
        CategoryIndex index = CategoryIndex.fromClass(Sample.class);

        String expected = String.join("\n",
                " * The methods in Sample are organized into method categories via category annotations.",
                " * Links are provided below as a convenience to help discover specific methods in Javadoc.",
                " *",
                " * <ul>",
                " * <li><b>Filtering \uD83D\uDEB0</b>",
                " * <ul><li>",
                " * {@link #collectIf()},",
                " * {@link #select()}",
                " * </li></ul>",
                " * <li><b>Transforming \uD83E\uDD8B</b>",
                " * <ul><li>",
                " * {@link #collectIf()}",
                " * </li></ul>",
                " * </ul>");
        assertEquals(expected, JavadocCategoryIndexRenderer.render(index, 40));
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
}
