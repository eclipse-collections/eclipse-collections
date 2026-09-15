/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.sorted.mutable;

import java.util.Comparator;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link UnmodifiableTreeMap}.
 */
public class UnmodifiableTreeMapTest extends MutableSortedMapTestCase
{
    @Override
    public <K, V> MutableSortedMap<K, V> newMap()
    {
        return new UnmodifiableTreeMap<>(new TreeSortedMap<>());
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newMapWithKeyValue(K key, V value)
    {
        return new UnmodifiableTreeMap<>(TreeSortedMap.newMapWith(key, value));
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return new UnmodifiableTreeMap<>(TreeSortedMap.newMapWith(key1, value1, key2, value2));
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return new UnmodifiableTreeMap<>(TreeSortedMap.newMapWith(
                key1, value1, key2, value2, key3, value3));
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newMapWithKeysValues(
            K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return new UnmodifiableTreeMap<>(TreeSortedMap.newMapWith(
                key1, value1, key2, value2, key3, value3, key4, value4));
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newMap(Comparator<? super K> comparator)
    {
        return new UnmodifiableTreeMap<>(new TreeSortedMap<>(comparator));
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newMapWithKeyValue(Comparator<? super K> comparator, K key, V value)
    {
        return new UnmodifiableTreeMap<>(TreeSortedMap.newMapWith(key, value));
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newMapWithKeysValues(Comparator<? super K> comparator, K key1, V value1, K key2, V value2)
    {
        return new UnmodifiableTreeMap<>(TreeSortedMap.newMapWith(comparator, key1, value1, key2, value2));
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newMapWithKeysValues(Comparator<? super K> comparator, K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return new UnmodifiableTreeMap<>(TreeSortedMap.newMapWith(
                comparator,
                key1, value1,
                key2, value2,
                key3, value3));
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newMapWithKeysValues(
            Comparator<? super K> comparator,
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        return new UnmodifiableTreeMap<>(TreeSortedMap.newMapWith(
                comparator,
                key1, value1,
                key2, value2,
                key3, value3,
                key4, value4));
    }

    @Override
    @Test
    public void removeObject()
    {
        MutableSortedMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertThrows(UnsupportedOperationException.class, () -> map.remove("One"));
    }

    @Override
    @Test
    public void removeKey()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.removeKey(1));
    }

    @Override
    @Test
    public void removeAllKeys()
    {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.newMapWithKeysValues(1, "1", 2, "Two").removeAllKeys(null));
    }

    @Override
    @Test
    public void removeIf()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.removeIf(null));
    }

    @Override
    @Test
    public void removeFromEntrySet()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().remove(ImmutableEntry.of(2, "Two")));
    }

    @Override
    @Test
    public void removeAllFromEntrySet()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of(2, "Two"))));
    }

    @Override
    @Test
    public void retainAllFromEntrySet()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.entrySet().retainAll(FastList.newListWith(ImmutableEntry.of(2, "Two"))));
    }

    @Override
    @Test
    public void clearEntrySet()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, map.entrySet()::clear);
    }

    @Override
    @Test
    public void removeFromKeySet()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().remove(2));
    }

    @Override
    @Test
    public void removeAllFromKeySet()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().removeAll(FastList.newListWith(1, 2)));
    }

    @Override
    @Test
    public void retainAllFromKeySet()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().retainAll(Lists.mutable.of()));
    }

    @Override
    @Test
    public void clearKeySet()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, map.keySet()::clear);
    }

    @Override
    @Test
    public void removeFromValues()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.values().remove("Two"));
    }

    @Override
    @Test
    public void removeAllFromValues()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.values().removeAll(FastList.newListWith("One", "Two")));
    }

    @Override
    @Test
    public void removeNullFromValues()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.values().remove(null));
    }

    @Override
    @Test
    public void retainAllFromValues()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.values().retainAll(Lists.mutable.of()));
    }

    @Override
    @Test
    public void getIfAbsentPut()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut(4, new PassThruFunction0<>("4")));
    }

    @Override
    @Test
    public void getIfAbsentPutValue()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut(4, "4"));
    }

    @Override
    @Test
    public void getIfAbsentPutWithKey()
    {
        MutableSortedMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.getIfAbsentPutWithKey(4, Functions.getIntegerPassThru()));
    }

    @Override
    @Test
    public void getIfAbsentPutWith()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPutWith(4, String::valueOf, 4));
    }

    @Override
    @Test
    public void putAll()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        assertThrows(UnsupportedOperationException.class, () -> map.putAll(null));
    }

    @Override
    @Test
    public void putAllFromCollection()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        assertThrows(UnsupportedOperationException.class, () -> map.collectKeysAndValues(null, null, null));
    }

    @Override
    @Test
    public void clear()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        assertThrows(UnsupportedOperationException.class, map::clear);
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        assertSame(map, map.asUnmodifiable());
    }

    @Test
    public void entrySet()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2").asUnmodifiable();

        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().remove(null));

        assertThrows(UnsupportedOperationException.class, () -> Iterate.getFirst(map.entrySet()).setValue("Three"));

        assertEquals(this.newMapWithKeysValues(1, "One", 2, "2"), map);
    }

    @Test
    public void entrySetToArray()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeyValue(1, "One").asUnmodifiable();
        Object[] entries = map.entrySet().toArray();
        assertEquals(ImmutableEntry.of(1, "One"), entries[0]);
    }

    @Test
    public void entrySetToArrayWithTarget()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeyValue(1, "One").asUnmodifiable();
        Object[] entries = map.entrySet().toArray(new Object[]{});
        assertEquals(ImmutableEntry.of(1, "One"), entries[0]);
    }

    @Override
    @Test
    public void add()
    {
        MutableSortedMap<String, Integer> map = this.newMapWithKeyValue("A", 1);
        assertThrows(UnsupportedOperationException.class, () -> map.add(Tuples.pair("A", 3)));
    }

    @Override
    @Test
    public void putPair()
    {
        MutableSortedMap<String, Integer> map = this.newMapWithKeyValue("A", 1);
        assertThrows(UnsupportedOperationException.class, () -> map.putPair(Tuples.pair("A", 3)));
    }

    @Override
    @Test
    public void withKeyValue()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.withKeyValue(null, null));
    }

    @Override
    @Test
    public void withMap()
    {
        MutableSortedMap<Integer, Character> map = this.newMapWithKeyValue(1, 'a');
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.withMap(Maps.mutable.with(1, Character.valueOf('a'))));
    }

    @Override
    @Test
    public void withMapEmpty()
    {
        MutableSortedMap<Integer, Character> map = this.newMapWithKeyValue(1, 'a');
        assertThrows(UnsupportedOperationException.class, () -> map.withMap(Maps.mutable.empty()));
    }

    @Override
    @Test
    public void withMapTargetEmpty()
    {
        MutableSortedMap<Object, Object> map = this.newMap();
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.withMap(Maps.mutable.with(1, Character.valueOf('a'))));
    }

    @Override
    @Test
    public void withMapEmptyAndTargetEmpty()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().withMap(Maps.mutable.empty()));
    }

    @Override
    @Test
    public void withMapNull()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().withMap(null));
    }

    @Override
    @Test
    public void withMapIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeyValue(1, 'a').withMapIterable(Maps.mutable.with(1, Character.valueOf('a'))));
    }

    @Override
    @Test
    public void withMapIterableEmpty()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeyValue(1, 'a').withMapIterable(Maps.mutable.empty()));
    }

    @Override
    @Test
    public void withMapIterableTargetEmpty()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().withMapIterable(Maps.mutable.with(1, Character.valueOf('a'))));
    }

    @Override
    @Test
    public void withMapIterableEmptyAndTargetEmpty()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().withMapIterable(Maps.mutable.empty()));
    }

    @Override
    @Test
    public void withMapIterableNull()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().withMapIterable(null));
    }

    @Override
    @Test
    public void putAllMapIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeyValue(1, 'a').putAllMapIterable(Maps.mutable.with(1, Character.valueOf('a'))));
    }

    @Override
    @Test
    public void putAllMapIterableEmpty()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeyValue(1, 'a').putAllMapIterable(Maps.mutable.empty()));
    }

    @Override
    @Test
    public void putAllMapIterableTargetEmpty()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().putAllMapIterable(Maps.mutable.with(1, Character.valueOf('a'))));
    }

    @Override
    @Test
    public void putAllMapIterableEmptyAndTargetEmpty()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().putAllMapIterable(Maps.mutable.empty()));
    }

    @Override
    @Test
    public void putAllMapIterableNull()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().putAllMapIterable(null));
    }

    @Override
    @Test
    public void withAllKeyValues()
    {
        MutableSortedMap<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.withAllKeyValues(FastList.newListWith(Tuples.pair("B", 22), Tuples.pair("C", 3))));
    }

    @Override
    @Test
    public void withAllKeyValueArguments()
    {
        MutableSortedMap<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.withAllKeyValueArguments(Tuples.pair("B", 22), Tuples.pair("C", 3)));
    }

    @Override
    @Test
    public void withoutKey()
    {
        MutableSortedMap<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.withoutKey("B"));
    }

    @Override
    @Test
    public void withoutAllKeys()
    {
        MutableSortedMap<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2, "C", 3);
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.withoutAllKeys(FastList.newListWith("A", "C")));
    }

    @Override
    @Test
    public void with()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2");
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.with(Tuples.pair(3, "3")));
    }

    @Override
    @Test
    public void headMap()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4");

        Verify.assertInstanceOf(UnmodifiableTreeMap.class, map.headMap(3));
        this.checkMutability(map.headMap(3));
    }

    @Override
    @Test
    public void tailMap()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4");

        Verify.assertInstanceOf(UnmodifiableTreeMap.class, map.tailMap(2));
        this.checkMutability(map.tailMap(2));
    }

    @Override
    @Test
    public void subMap()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4");

        Verify.assertInstanceOf(UnmodifiableTreeMap.class, map.subMap(1, 3));
        this.checkMutability(map.subMap(1, 3));
    }

    @Override
    @Test
    public void testClone()
    {
        MutableSortedMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4");
        assertSame(map, map.clone());
        Verify.assertInstanceOf(UnmodifiableTreeMap.class, map.clone());
    }

    private void checkMutability(MutableSortedMap<Integer, String> map)
    {
        assertThrows(UnsupportedOperationException.class, () -> map.put(3, "3"));

        assertThrows(UnsupportedOperationException.class, () -> map.putAll(TreeSortedMap.newMapWith(1, "1", 2, "2")));

        assertThrows(UnsupportedOperationException.class, () -> map.remove(2));

        assertThrows(UnsupportedOperationException.class, map::clear);

        assertThrows(UnsupportedOperationException.class, () -> map.with(Tuples.pair(1, "1")));
    }

    @Test
    @Override
    public void updateValue()
    {
        MutableSortedMap<Integer, Integer> map = this.newMap();
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValue(0, () -> 0, Functions.identity()));

        MutableMapIterable<Integer, Integer> map2 = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        assertThrows(UnsupportedOperationException.class, () -> map2.updateValue(4, () -> 4, v -> v + 1));
        assertEquals(this.newMapWithKeysValues(1, 1, 2, 2, 3, 3), map2);
        assertFalse(map2.containsKey(4));
        assertEquals(3, map2.size());

        assertThrows(UnsupportedOperationException.class, () -> map2.updateValue(2, () -> 0, v -> v + 1));
        assertEquals(this.newMapWithKeysValues(1, 1, 2, 2, 3, 3), map2);
        assertEquals(Integer.valueOf(2), map2.get(2));
        assertEquals(3, map2.size());
    }

    @Test
    @Override
    public void updateValue_collisions()
    {
        MutableSortedMap<Integer, Integer> map = this.newMap();
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValue(0, () -> 0, Functions.identity()));
    }

    @Test
    @Override
    public void updateValueWith()
    {
        MutableSortedMap<Integer, Integer> map = this.newMap();
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValueWith(0, () -> 0, (integer, parameter) -> 0, "test"));

        MutableMapIterable<Integer, Integer> map2 = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        assertThrows(UnsupportedOperationException.class, () -> map2.updateValueWith(4, () -> 4, (v, p) -> v + 1, "param"));
        assertEquals(this.newMapWithKeysValues(1, 1, 2, 2, 3, 3), map2);
        assertFalse(map2.containsKey(4));
        assertEquals(3, map2.size());

        assertThrows(UnsupportedOperationException.class, () -> map2.updateValueWith(2, () -> 0, (v, p) -> v + 1, "param"));
        assertEquals(this.newMapWithKeysValues(1, 1, 2, 2, 3, 3), map2);
        assertEquals(Integer.valueOf(2), map2.get(2));
        assertEquals(3, map2.size());
    }

    @Test
    @Override
    public void updateValueWith_collisions()
    {
        MutableSortedMap<Integer, Integer> map = this.newMap();
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValueWith(0, () -> 0, (integer, parameter) -> 0, "test"));
    }
}
