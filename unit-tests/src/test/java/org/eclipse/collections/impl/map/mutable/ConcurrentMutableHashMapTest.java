/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link ConcurrentMutableHashMap}.
 */
public class ConcurrentMutableHashMapTest extends ConcurrentHashMapTestCase
{
    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMap()
    {
        return ConcurrentMutableHashMap.newMap();
    }

    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMapWithKeyValue(K key, V value)
    {
        return ConcurrentMutableHashMap.<K, V>newMap().withKeyValue(key, value);
    }

    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return ConcurrentMutableHashMap.<K, V>newMap().withKeyValue(key1, value1).withKeyValue(key2, value2);
    }

    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return ConcurrentMutableHashMap.<K, V>newMap()
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2)
                .withKeyValue(key3, value3);
    }

    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return ConcurrentMutableHashMap.<K, V>newMap()
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2)
                .withKeyValue(key3, value3)
                .withKeyValue(key4, value4);
    }

    @Test
    public void putIfAbsent()
    {
        ConcurrentMutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        assertEquals(Integer.valueOf(1), map.putIfAbsent(1, 1));
        assertNull(map.putIfAbsent(3, 3));
    }

    @Test
    public void replace()
    {
        ConcurrentMutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        assertEquals(Integer.valueOf(1), map.replace(1, 1));
        assertNull(map.replace(3, 3));
    }

    @Test
    public void replaceWithOldValue()
    {
        ConcurrentMutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        assertTrue(map.replace(1, 1, 1));
        assertFalse(map.replace(2, 3, 3));
    }

    @Test
    public void removeWithKeyValue()
    {
        ConcurrentMutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        assertTrue(map.remove(1, 1));
        assertFalse(map.remove(2, 3));
    }

    @Override
    @Test
    public void removeFromEntrySet()
    {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertTrue(map.entrySet().remove(ImmutableEntry.of("Two", 2)));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);

        assertFalse(map.entrySet().remove(ImmutableEntry.of("Four", 4)));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Override
    @Test
    public void removeAllFromEntrySet()
    {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertTrue(map.entrySet().removeAll(FastList.newListWith(
                ImmutableEntry.of("One", 1),
                ImmutableEntry.of("Three", 3))));
        assertEquals(UnifiedMap.newWithKeysValues("Two", 2), map);

        assertFalse(map.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of("Four", 4))));
        assertEquals(UnifiedMap.newWithKeysValues("Two", 2), map);
    }

    @Override
    @Test
    public void keySetEqualsAndHashCode()
    {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("One", "Two", "Three"), map.keySet());
    }

    @Override
    @Test
    public void partition_value()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues(
                "A", 1,
                "B", 2,
                "C", 3,
                "D", 4);
        PartitionIterable<Integer> partition = map.partition(IntegerPredicates.isEven());
        assertEquals(iSet(2, 4), partition.getSelected().toSet());
        assertEquals(iSet(1, 3), partition.getRejected().toSet());
    }

    @Override
    @Test
    public void partitionWith_value()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues(
                "A", 1,
                "B", 2,
                "C", 3,
                "D", 4);
        PartitionIterable<Integer> partition = map.partitionWith(Predicates2.in(), map.select(IntegerPredicates.isEven()));
        assertEquals(iSet(2, 4), partition.getSelected().toSet());
        assertEquals(iSet(1, 3), partition.getRejected().toSet());
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        // java.util.concurrent.ConcurrentHashMap doesn't support null keys OR values
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Verify.assertPostSerializedEqualsAndHashCode(map);
        Verify.assertEqualsAndHashCode(Maps.mutable.of(1, "1", 2, "2", 3, "3"), map);
        Verify.assertEqualsAndHashCode(Maps.immutable.of(1, "1", 2, "2", 3, "3"), map);

        assertNotEquals(map, this.newMapWithKeysValues(1, "1", 2, "2"));
        assertNotEquals(map, this.newMapWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4"));
        assertNotEquals(map, this.newMapWithKeysValues(1, "1", 2, "2", 4, "4"));
    }
}
