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

import java.util.Comparator;

/**
 * A method category, identified by its name and icon. Categories are discovered either from the
 * {@code org.eclipse.collections.api.annotation.category.Category} meta-annotation (via reflection) or from the
 * category index in a type's Javadoc.
 * <p>
 * Equality is based on both {@link #name()} and {@link #icon()}, so a mismatch in either is detected when comparing
 * a reflected category index with one parsed from source.
 */
public record MethodCategory(String name, String icon) implements Comparable<MethodCategory>
{
    private static final Comparator<MethodCategory> COMPARATOR =
            Comparator.comparing(MethodCategory::name).thenComparing(MethodCategory::icon);

    @Override
    public int compareTo(MethodCategory other)
    {
        return MethodCategory.COMPARATOR.compare(this, other);
    }

    @Override
    public String toString()
    {
        return this.name + ' ' + this.icon;
    }
}
