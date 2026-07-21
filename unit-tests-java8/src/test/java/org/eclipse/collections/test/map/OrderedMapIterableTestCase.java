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
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.test.OrderedIterableTestCase;
import org.eclipse.collections.test.list.TransformsToListTrait;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    default void ReversibleIterable_take()
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
    default void ReversibleIterable_drop()
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

    @Test
    default void MapIterable_keysView_valuesView_keyValuesView()
    {
        OrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2, "C", 3);

        assertEquals(Lists.mutable.with("A", "B", "C"), map.keysView().toList());
        assertEquals(Lists.mutable.with(1, 2, 3), map.valuesView().toList());
        assertEquals(
                Lists.mutable.with(Tuples.pair("A", 1), Tuples.pair("B", 2), Tuples.pair("C", 3)),
                map.keyValuesView().toList());
    }

    @Override
    @Test
    default void MapIterable_select_reject()
    {
        MapIterableTestCase.super.MapIterable_select_reject();

        OrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2, "C", 3, "D", 4);

        OrderedMap<String, Integer> selected = map.select((key, value) -> value % 2 != 0);
        assertIterablesEqual(this.newWithKeysValues("A", 1, "C", 3), selected);

        OrderedMap<String, Integer> rejected = map.reject((key, value) -> value % 2 != 0);
        assertIterablesEqual(this.newWithKeysValues("B", 2, "D", 4), rejected);
    }

    @Override
    @Test
    default void MapIterable_collectValues()
    {
        MapIterableTestCase.super.MapIterable_collectValues();

        OrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2, "C", 3, "D", 4);

        OrderedMap<String, Integer> collected = map.collectValues((key, value) -> value * 10);
        assertIterablesEqual(this.newWithKeysValues("A", 10, "B", 20, "C", 30, "D", 40), collected);
    }

    @Test
    default void OrderedIterable_forEachWithIndex_from_to()
    {
        OrderedMap<Object, Integer> integers = this.newWith(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        MutableList<Integer> result = Lists.mutable.empty();
        MutableList<Integer> indexes = Lists.mutable.empty();
        integers.forEachWithIndex(5, 7, (each, index) ->
        {
            result.add(each);
            indexes.add(index);
        });
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(5, 6, 7);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(4, 3, 2);
                },
                result);
        assertIterablesEqual(Lists.immutable.with(5, 6, 7), indexes);

        MutableList<Integer> reverseResult = Lists.mutable.empty();
        MutableList<Integer> reverseIndexes = Lists.mutable.empty();
        integers.forEachWithIndex(7, 5, (each, index) ->
        {
            reverseResult.add(each);
            reverseIndexes.add(index);
        });
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(7, 6, 5);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(2, 3, 4);
                },
                reverseResult);
        assertIterablesEqual(Lists.immutable.with(7, 6, 5), reverseIndexes);

        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(-1, 0, (each, index) -> { }));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, -1, (each, index) -> { }));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, 10, (each, index) -> { }));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(10, 0, (each, index) -> { }));
    }

    @Test
    default void OrderedIterable_corresponds()
    {
        OrderedMap<Object, Integer> map = this.newWith(3, 2, 1);
        MutableList<Integer> expected = switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> Lists.mutable.with(1, 2, 3);
            case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.mutable.with(3, 2, 1);
        };
        MutableList<Integer> differentOrder = switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> Lists.mutable.with(3, 2, 1);
            case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.mutable.with(1, 2, 3);
        };

        assertTrue(map.corresponds(expected, Integer::equals));
        assertFalse(map.corresponds(differentOrder, Integer::equals));
        assertFalse(map.corresponds(Lists.mutable.with(1, 2), Integer::equals));
        assertTrue(this.<Integer>newWith().corresponds(Lists.mutable.empty(), Integer::equals));

        if (!this.allowsDuplicates())
        {
            return;
        }

        OrderedMap<Object, Integer> mapWithDuplicates = this.newWith(3, 3, 3, 2, 2, 1);
        MutableList<Integer> expectedWithDuplicates = switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> Lists.mutable.with(1, 2, 2, 3, 3, 3);
            case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.mutable.with(3, 3, 3, 2, 2, 1);
        };
        assertTrue(mapWithDuplicates.corresponds(expectedWithDuplicates, Integer::equals));
        assertFalse(mapWithDuplicates.corresponds(expected, Integer::equals));
    }

    @Test
    default void OrderedIterable_distinct()
    {
        OrderedMap<Object, Integer> map = this.newWith(3, 2, 1);
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(1, 2, 3);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(3, 2, 1);
                },
                map.distinct());

        if (!this.allowsDuplicates())
        {
            return;
        }

        OrderedMap<Object, Integer> mapWithDuplicates = this.newWith(3, 3, 3, 2, 2, 1, 0, 0);
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(0, 1, 2, 3);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(3, 2, 1, 0);
                },
                mapWithDuplicates.distinct());
    }

    @Test
    default void OrderedIterable_toStack()
    {
        OrderedMap<Object, Integer> map = this.newWith(3, 2, 1);
        MutableStack<Integer> stack = map.toStack();
        assertEquals(map.size(), stack.size());
        assertEquals(map.toBag(), stack.toBag());

        if (!this.allowsDuplicates())
        {
            return;
        }

        OrderedMap<Object, Integer> mapWithDuplicates = this.newWith(3, 3, 2, 1, 1);
        MutableStack<Integer> stackWithDuplicates = mapWithDuplicates.toStack();
        assertEquals(mapWithDuplicates.size(), stackWithDuplicates.size());
        assertEquals(mapWithDuplicates.toBag(), stackWithDuplicates.toBag());
    }

    @Test
    default void OrderedIterable_detectIndex()
    {
        OrderedMap<Object, Integer> map = this.newWith(3, 2, 1);
        switch (this.getOrderingType())
        {
            case SORTED_NATURAL ->
            {
                assertEquals(0, map.detectIndex(each -> each == 1));
                assertEquals(1, map.detectIndex(each -> each == 2));
                assertEquals(2, map.detectIndex(each -> each == 3));
            }
            case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL ->
            {
                assertEquals(0, map.detectIndex(each -> each == 3));
                assertEquals(1, map.detectIndex(each -> each == 2));
                assertEquals(2, map.detectIndex(each -> each == 1));
            }
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
        assertEquals(-1, map.detectIndex(each -> each == 99));
        assertEquals(-1, this.<Integer>newWith().detectIndex(each -> true));

        if (!this.allowsDuplicates())
        {
            return;
        }

        OrderedMap<Object, Integer> mapWithDuplicates = this.newWith(3, 3, 3, 2, 2, 1);
        switch (this.getOrderingType())
        {
            case SORTED_NATURAL ->
            {
                assertEquals(0, mapWithDuplicates.detectIndex(each -> each == 1));
                assertEquals(1, mapWithDuplicates.detectIndex(each -> each == 2));
                assertEquals(3, mapWithDuplicates.detectIndex(each -> each == 3));
            }
            case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL ->
            {
                assertEquals(0, mapWithDuplicates.detectIndex(each -> each == 3));
                assertEquals(3, mapWithDuplicates.detectIndex(each -> each == 2));
                assertEquals(5, mapWithDuplicates.detectIndex(each -> each == 1));
            }
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }

    @Test
    default void ReversibleIterable_detectLastIndex()
    {
        OrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2, "C", 1, "D", 3);

        assertEquals(2, map.detectLastIndex(value -> value == 1));
        assertEquals(1, map.detectLastIndex(value -> value == 2));
        assertEquals(-1, map.detectLastIndex(value -> value == 99));
        assertEquals(-1, this.<String, Integer>newWithKeysValues().detectLastIndex(value -> true));
    }

    @Test
    default void ReversibleIterable_toReversed()
    {
        OrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2, "C", 3);

        OrderedMap<String, Integer> reversed = map.toReversed();
        assertIterablesEqual(this.newWithKeysValues("C", 3, "B", 2, "A", 1), reversed);
        assertIterablesEqual(this.newWithKeysValues(), this.<String, Integer>newWithKeysValues().toReversed());
        assertIterablesEqual(map, reversed.toReversed());
    }

    @Test
    default void ReversibleIterable_reverseForEach()
    {
        OrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2, "C", 3);

        MutableList<Integer> values = Lists.mutable.empty();
        map.reverseForEach(values::add);
        assertIterablesEqual(Lists.immutable.with(3, 2, 1), values);

        MutableList<Integer> empty = Lists.mutable.empty();
        this.<String, Integer>newWithKeysValues().reverseForEach(empty::add);
        assertIterablesEqual(Lists.immutable.empty(), empty);
    }

    @Test
    default void ReversibleIterable_asReversed()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1).asReversed());
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
