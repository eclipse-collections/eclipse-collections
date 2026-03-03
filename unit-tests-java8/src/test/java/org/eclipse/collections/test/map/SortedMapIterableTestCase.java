/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map;

import java.util.LinkedHashMap;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.map.OrderedMap;
import org.eclipse.collections.api.map.sorted.SortedMapIterable;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.eclipse.collections.test.OrderedIterableTestCase;
import org.eclipse.collections.test.list.TransformsToListTrait;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SortedMapIterableTestCase extends MapIterableTestCase, OrderedIterableTestCase, TransformsToListTrait
{
    @Override
    <T> SortedMapIterable<Object, T> newWith(T... elements);

    @Override
    <K, V> SortedMapIterable<K, V> newWithKeysValues(Object... elements);

    @Override
    default <K, V> OrderedMap<K, V> newWithTransformedKeysValues(Object... elements)
    {
        MutableOrderedMap<K, V> result = OrderedMapAdapter.adapt(new LinkedHashMap<>());
        for (int i = 0; i < elements.length; i += 2)
        {
            result.put((K) elements[i], (V) elements[i + 1]);
        }
        return result.asUnmodifiable();
    }

    @Override
    default boolean supportsNullKeys()
    {
        return false;
    }

    @Override
    default <T> ListIterable<T> getExpectedFiltered(T... elements)
    {
        return Lists.immutable.with(elements);
    }

    @Override
    default <T> MutableList<T> newMutableForFilter(T... elements)
    {
        return Lists.mutable.with(elements);
    }

    @Override
    @Test
    default void OrderedIterable_takeWhile()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(1, 2, 3).takeWhile(each -> true));
    }

    @Override
    @Test
    default void OrderedIterable_dropWhile()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(1, 2, 3).dropWhile(each -> true));
    }

    @Override
    @Test
    default void OrderedIterable_partitionWhile()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(1, 2, 3).partitionWhile(each -> true));
    }
}
