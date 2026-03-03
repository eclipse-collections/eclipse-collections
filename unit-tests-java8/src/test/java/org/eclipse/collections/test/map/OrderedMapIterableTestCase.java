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
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.eclipse.collections.test.OrderedIterableTestCase;
import org.eclipse.collections.test.list.TransformsToListTrait;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
