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
import org.eclipse.collections.api.partition.ordered.PartitionOrderedIterable;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.eclipse.collections.test.OrderedIterableTestCase;
import org.eclipse.collections.test.list.TransformsToListTrait;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.fail;

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

    // Cannot call super tests for takeWhile/dropWhile/partitionWhile because:
    // 1. The predicate operates on values, but the super test uses each % 2 == 0 assuming
    //    insertion order; sorted maps reorder by key, so we need monotonic predicates
    //    that respect the sort order.
    // 2. Map equals compares entries (keys + values), not just values, so takeWhile/dropWhile
    //    results must be compared against maps built with matching keys via newWithKeysValues.

    @Override
    @Test
    default void OrderedIterable_takeWhile()
    {
        // Keys sorted in reverse natural order: iteration is (5,5), (4,4), (3,3), (2,2), (1,1)
        SortedMapIterable<Integer, Integer> map = this.newWithKeysValues(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
        assertIterablesEqual(this.newWithKeysValues(5, 5, 4, 4, 3, 3), map.takeWhile(each -> each > 2));
        assertIterablesEqual(map, map.takeWhile(each -> true));
        assertIterablesEqual(this.newWithKeysValues(), map.takeWhile(each -> false));

        SortedMapIterable<Object, Object> empty = this.newWithKeysValues();
        assertIterablesEqual(this.newWithKeysValues(), empty.takeWhile(each -> {
            fail("Should not evaluate the predicate");
            return true;
        }));
    }

    @Override
    @Test
    default void OrderedIterable_dropWhile()
    {
        SortedMapIterable<Integer, Integer> map = this.newWithKeysValues(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
        assertIterablesEqual(this.newWithKeysValues(2, 2, 1, 1), map.dropWhile(each -> each > 2));
        assertIterablesEqual(this.newWithKeysValues(), map.dropWhile(each -> true));
        assertIterablesEqual(map, map.dropWhile(each -> false));

        SortedMapIterable<Object, Object> empty = this.newWithKeysValues();
        assertIterablesEqual(this.newWithKeysValues(), empty.dropWhile(each -> {
            fail("Should not evaluate the predicate");
            return true;
        }));
    }

    @Override
    @Test
    default void OrderedIterable_partitionWhile()
    {
        SortedMapIterable<Integer, Integer> map = this.newWithKeysValues(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
        PartitionOrderedIterable<Integer> partition1 = map.partitionWhile(each -> each > 2);
        assertIterablesEqual(Lists.immutable.with(5, 4, 3), partition1.getSelected());
        assertIterablesEqual(Lists.immutable.with(2, 1), partition1.getRejected());

        PartitionOrderedIterable<Integer> partition2 = map.partitionWhile(each -> true);
        assertIterablesEqual(Lists.immutable.with(5, 4, 3, 2, 1), partition2.getSelected());
        assertIterablesEqual(Lists.immutable.empty(), partition2.getRejected());

        PartitionOrderedIterable<Integer> partition3 = map.partitionWhile(each -> false);
        assertIterablesEqual(Lists.immutable.empty(), partition3.getSelected());
        assertIterablesEqual(Lists.immutable.with(5, 4, 3, 2, 1), partition3.getRejected());

        SortedMapIterable<Object, Object> empty = this.newWithKeysValues();
        PartitionOrderedIterable<Object> partition4 = empty.partitionWhile(each -> {
            fail("Should not evaluate the predicate");
            return true;
        });
        assertIterablesEqual(Lists.immutable.empty(), partition4.getSelected());
        assertIterablesEqual(Lists.immutable.empty(), partition4.getRejected());
    }
}
