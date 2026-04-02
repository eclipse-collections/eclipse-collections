/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable.sorted;

import java.util.TreeSet;

import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.set.sorted.mutable.SortedSetAdapter;
import org.eclipse.collections.test.IterableTestCase;

public class SortedSetAdapterNaturalOrderTest implements MutableSortedSetTestCase
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
        MutableSortedSet<T> result = SortedSetAdapter.adapt(new TreeSet<>());
        IterableTestCase.addAllTo(elements, result);
        return result;
    }
}
