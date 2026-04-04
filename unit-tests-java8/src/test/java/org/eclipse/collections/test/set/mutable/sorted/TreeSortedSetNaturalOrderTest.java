/*
 * Copyright (c) 2025 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable.sorted;

import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.test.IterableTestCase;

public class TreeSortedSetNaturalOrderTest
        implements MutableSortedSetTestCase
{
    @Override
    public OrderingType getOrderingType()
    {
        return OrderingType.SORTED_NATURAL;
    }

    @SafeVarargs
    @Override
    public final <T> MutableSortedSet<T> newWith(T... elements)
    {
        TreeSortedSet<T> result = TreeSortedSet.newSet();
        IterableTestCase.addAllTo(elements, result);
        return result;
    }
}
