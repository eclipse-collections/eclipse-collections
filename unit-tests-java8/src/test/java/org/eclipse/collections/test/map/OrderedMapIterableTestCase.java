/*
 * Copyright (c) 2021 Two Sigma.
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
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.map.OrderedMap;
import org.eclipse.collections.api.partition.ordered.PartitionOrderedIterable;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.eclipse.collections.test.OrderedIterableTestCase;
import org.eclipse.collections.test.list.TransformsToListTrait;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public interface OrderedMapIterableTestCase extends MapIterableTestCase, OrderedIterableTestCase, TransformsToListTrait
{
    @Override
    <T> OrderedMap<Object, T> newWith(T... elements);

    @Override
    <K, V> OrderedMap<K, V> newWithKeysValues(Object... elements);

    @Override
    default <K, V> MapIterable<K, V> newWithTransformedKeysValues(Object... elements)
    {
        MutableOrderedMap<K, V> result = OrderedMapAdapter.adapt(new LinkedHashMap<>());
        for (int i = 0; i < elements.length; i += 2)
        {
            result.put((K) elements[i], (V) elements[i + 1]);
        }
        return result.asUnmodifiable();
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

    @Test
    default void take()
    {
        OrderedMap<Integer, String> orderedMap = this.newWithKeysValues(3, "Three", 2, "Two", 1, "Three");
        assertIterablesEqual(this.newWithKeysValues(), orderedMap.take(0));
        assertIterablesEqual(this.newWithKeysValues(3, "Three"), orderedMap.take(1));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two"), orderedMap.take(2));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "Three"), orderedMap.take(3));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "Three"), orderedMap.take(4));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "Three"), orderedMap.take(Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class, () -> orderedMap.take(-1));
    }

    @Test
    default void drop()
    {
        OrderedMap<Integer, String> orderedMap = this.newWithKeysValues(3, "Three", 2, "Two", 1, "Three");
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "Three"), orderedMap.drop(0));
        assertIterablesEqual(this.newWithKeysValues(2, "Two", 1, "Three"), orderedMap.drop(1));
        assertIterablesEqual(this.newWithKeysValues(1, "Three"), orderedMap.drop(2));
        assertIterablesEqual(this.newWithKeysValues(), orderedMap.drop(3));
        assertIterablesEqual(this.newWithKeysValues(), orderedMap.drop(4));
        assertIterablesEqual(this.newWithKeysValues(), orderedMap.drop(Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class, () -> orderedMap.drop(-1));
    }

    @Override
    @Test
    default void MapIterable_flipUniqueValues()
    {
        MapIterable<String, Integer> map = this.newWithKeysValues("Three", 3, "Two", 2, "One", 1);
        MapIterable<Integer, String> result = map.flipUniqueValues();

        // TODO: Set up methods like getExpectedTransformed, but for maps. Delete overrides of this method.
        assertIterablesEqual(
                this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"),
                result);

        assertThrows(
                IllegalStateException.class,
                () -> this.newWithKeysValues(1, "2", 2, "2").flipUniqueValues());
    }

    // Cannot call super tests for takeWhile/dropWhile/partitionWhile because map equals
    // compares entries (keys + values). The super test uses newWith() which assigns
    // auto-generated keys, so the expected and actual maps have different keys even
    // when values match. We use newWithKeysValues for takeWhile/dropWhile to ensure
    // key-value pairs match, and Lists for partitionWhile which returns value-only lists.

    @Override
    @Test
    default void OrderedIterable_takeWhile()
    {
        // Insertion order: (1,6), (2,6), (3,4), (4,4), (5,5), (6,5), (7,3), (8,3), (9,2), (10,2), (11,1), (12,1)
        OrderedMap<Integer, Integer> map = this.newWithKeysValues(
                1, 6, 2, 6, 3, 4, 4, 4, 5, 5, 6, 5, 7, 3, 8, 3, 9, 2, 10, 2, 11, 1, 12, 1);
        assertIterablesEqual(
                this.newWithKeysValues(1, 6, 2, 6, 3, 4, 4, 4),
                map.takeWhile(each -> each % 2 == 0));
        assertIterablesEqual(map, map.takeWhile(each -> true));
        assertIterablesEqual(this.newWithKeysValues(), map.takeWhile(each -> false));

        OrderedMap<Object, Object> empty = this.newWithKeysValues();
        assertIterablesEqual(this.newWithKeysValues(), empty.takeWhile(each -> {
            fail("Should not evaluate the predicate");
            return true;
        }));
    }

    @Override
    @Test
    default void OrderedIterable_dropWhile()
    {
        OrderedMap<Integer, Integer> map = this.newWithKeysValues(
                1, 6, 2, 6, 3, 4, 4, 4, 5, 5, 6, 5, 7, 3, 8, 3, 9, 2, 10, 2, 11, 1, 12, 1);
        assertIterablesEqual(
                this.newWithKeysValues(5, 5, 6, 5, 7, 3, 8, 3, 9, 2, 10, 2, 11, 1, 12, 1),
                map.dropWhile(each -> each % 2 == 0));
        assertIterablesEqual(this.newWithKeysValues(), map.dropWhile(each -> true));
        assertIterablesEqual(map, map.dropWhile(each -> false));

        OrderedMap<Object, Object> empty = this.newWithKeysValues();
        assertIterablesEqual(this.newWithKeysValues(), empty.dropWhile(each -> {
            fail("Should not evaluate the predicate");
            return true;
        }));
    }

    @Override
    @Test
    default void OrderedIterable_partitionWhile()
    {
        OrderedMap<Integer, Integer> map = this.newWithKeysValues(
                1, 6, 2, 6, 3, 4, 4, 4, 5, 5, 6, 5, 7, 3, 8, 3, 9, 2, 10, 2, 11, 1, 12, 1);
        PartitionOrderedIterable<Integer> partition1 = map.partitionWhile(each -> each % 2 == 0);
        assertIterablesEqual(Lists.immutable.with(6, 6, 4, 4), partition1.getSelected());
        assertIterablesEqual(Lists.immutable.with(5, 5, 3, 3, 2, 2, 1, 1), partition1.getRejected());

        PartitionOrderedIterable<Integer> partition2 = map.partitionWhile(each -> true);
        assertIterablesEqual(Lists.immutable.with(6, 6, 4, 4, 5, 5, 3, 3, 2, 2, 1, 1), partition2.getSelected());
        assertIterablesEqual(Lists.immutable.empty(), partition2.getRejected());

        PartitionOrderedIterable<Integer> partition3 = map.partitionWhile(each -> false);
        assertIterablesEqual(Lists.immutable.empty(), partition3.getSelected());
        assertIterablesEqual(Lists.immutable.with(6, 6, 4, 4, 5, 5, 3, 3, 2, 2, 1, 1), partition3.getRejected());

        OrderedMap<Object, Object> empty = this.newWithKeysValues();
        PartitionOrderedIterable<Object> partition4 = empty.partitionWhile(each -> {
            fail("Should not evaluate the predicate");
            return true;
        });
        assertIterablesEqual(Lists.immutable.empty(), partition4.getSelected());
        assertIterablesEqual(Lists.immutable.empty(), partition4.getRejected());
    }
}
