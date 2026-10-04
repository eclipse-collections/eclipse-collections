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

import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;

/**
 * Renders a {@link CategoryIndex} as the category index of a type's Javadoc,
 * <p>
 * It is the counterpart of {@link JavadocCategoryIndexParser}.
 */
public final class JavadocCategoryIndexRenderer
{
    private static final int DEFAULT_MAX_LINE_LENGTH = 120;
    private static final String LINE_PREFIX = " * ";
    private static final String LINK_SEPARATOR = ", ";

    private JavadocCategoryIndexRenderer()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Renders the category index of {@code index}, using the generator's default line length.
     */
    public static String render(CategoryIndex index)
    {
        return JavadocCategoryIndexRenderer.render(index, DEFAULT_MAX_LINE_LENGTH);
    }

    /**
     * Renders the category index of {@code index}, wrapping link lines at {@code maxLineLength}.
     */
    public static String render(CategoryIndex index, int maxLineLength)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(" * The methods in ")
                .append(index.getType().getSimpleName())
                .append(" are organized into method categories via category annotations.\n");
        builder.append(" * Links are provided below as a convenience to help discover specific methods in Javadoc.\n");
        builder.append(" *\n");
        builder.append(" * <ul>\n");
        index.sortedCategories().forEach(category ->
        {
            builder.append(" * <li><b>")
                    .append(category.name())
                    .append(' ')
                    .append(category.icon())
                    .append("</b>\n");
            builder.append(" * <ul><li>\n");

            // Collect into a set to deduplicate: distinct methods can produce the same link because a Javadoc
            // signature uses simple type names only (for example, Iterate.removeIf(Iterable, Predicate) matches both
            // the Eclipse Collections and java.util.function.Predicate overloads). The set is sorted to give
            // the rendered links a stable order.
            ImmutableSortedSet<String> links = index.categoryMethods().get(category)
                    .collect(method -> JavadocMethodLink.of(method).link())
                    .toImmutableSortedSet();
            JavadocCategoryIndexRenderer.appendWrappedLinks(builder, links, maxLineLength);
            builder.append(" * </li></ul>\n");
        });
        builder.append(" * </ul>");
        return builder.toString();
    }

    private static void appendWrappedLinks(StringBuilder builder, Iterable<String> links, int maxLineLength)
    {
        int maxContentLength = maxLineLength - LINE_PREFIX.length();
        StringBuilder line = new StringBuilder();
        links.forEach(link ->
        {
            if (!line.isEmpty())
            {
                if (line.length() + LINK_SEPARATOR.length() + link.length() > maxContentLength)
                {
                    builder.append(LINE_PREFIX).append(line).append(",\n");
                    line.setLength(0);
                }
                else
                {
                    line.append(LINK_SEPARATOR);
                }
            }
            line.append(link);
        });
        builder.append(LINE_PREFIX).append(line).append('\n');
    }
}
