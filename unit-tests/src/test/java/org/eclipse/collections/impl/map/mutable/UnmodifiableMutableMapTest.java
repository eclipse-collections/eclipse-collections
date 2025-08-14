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

import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link UnmodifiableMutableMap}.
 */
public class UnmodifiableMutableMapTest extends MutableMapTestCase
{
    @Override
    public <K, V> MutableMap<K, V> newMap()
    {
        return new UnmodifiableMutableMap<>(new UnifiedMap<>());
    }

    @Override
    protected <K, V> MutableMap<K, V> newMapWithKeyValue(K key, V value)
    {
        return new UnmodifiableMutableMap<>(UnifiedMap.newWithKeysValues(key, value));
    }

    @Override
    public <K, V> MutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return new UnmodifiableMutableMap<>(UnifiedMap.newWithKeysValues(key1, value1, key2, value2));
    }

    @Override
    public <K, V> MutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return new UnmodifiableMutableMap<>(UnifiedMap.newWithKeysValues(
                key1, value1, key2, value2, key3, value3));
    }

    @Override
    public <K, V> MutableMap<K, V> newMapWithKeysValues(
            K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return new UnmodifiableMutableMap<>(UnifiedMap.newWithKeysValues(
                key1, value1, key2, value2, key3, value3, key4, value4));
    }

    @Override
    @Test
    public void removeObject()
    {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertThrows(UnsupportedOperationException.class, () -> map.remove("One"));
    }

    @Override
    @Test
    public void removeKey()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.removeKey(1));
    }

    @Override
    @Test
    public void removeAllKeys()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, "1", 2, "Two").removeAllKeys(null));
    }

    @Override
    @Test
    public void removeIf()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.removeIf(null));
    }

    @Override
    @Test
    public void removeFromEntrySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().remove(ImmutableEntry.of(2, "Two")));
    }

    @Override
    @Test
    public void removeAllFromEntrySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of(2, "Two"))));
    }

    @Override
    @Test
    public void retainAllFromEntrySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().retainAll(FastList.newListWith(ImmutableEntry.of(2, "Two"))));
    }

    @Override
    @Test
    public void clearEntrySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().clear());
    }

    @Override
    @Test
    public void removeFromKeySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().remove(2));
    }

    @Override
    @Test
    public void removeNullFromKeySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().remove(null));
    }

    @Override
    @Test
    public void removeAllFromKeySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().removeAll(FastList.newListWith(1, 2)));
    }

    @Override
    @Test
    public void retainAllFromKeySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().retainAll(Lists.mutable.of()));
    }

    @Override
    @Test
    public void clearKeySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().clear());
    }

    @Override
    @Test
    public void removeFromValues()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.values().remove("Two"));
    }

    @Override
    @Test
    public void removeAllFromValues()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.values().removeAll(FastList.newListWith("One", "Two")));
    }

    @Override
    @Test
    public void removeNullFromValues()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.values().remove(null));
    }

    @Override
    @Test
    public void retainAllFromValues()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        assertThrows(UnsupportedOperationException.class, () -> map.values().retainAll(Lists.mutable.of()));
    }

    @Override
    @Test
    public void getIfAbsentPut()
    {
        assertEquals("3", this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPut(3, (Function0<String>) () -> ""));
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPut(4, (Function0<String>) () -> ""));
    }

    @Override
    @Test
    public void getIfAbsentPutValue()
    {
        assertEquals("3", this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPut(3, ""));
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPut(4, ""));
    }

    @Override
    @Test
    public void getIfAbsentPutWithKey()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, 1, 2, 2, 3, 3).getIfAbsentPutWithKey(4, Functions.getIntegerPassThru()));
    }

    @Override
    @Test
    public void getIfAbsentPutWith()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPutWith(4, String::valueOf, 4));
    }

    @Override
    @Test
    public void putAll()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        assertThrows(UnsupportedOperationException.class, () -> map.putAll(null));
    }

    @Override
    @Test
    public void collectKeysAndValues()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        assertThrows(UnsupportedOperationException.class, () -> map.collectKeysAndValues(null, null, null));
    }

    @Override
    @Test
    public void clear()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        assertThrows(UnsupportedOperationException.class, map::clear);
    }

    @Override
    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, "One", 2, "Two").add(null));
    }

    @Override
    @Test
    public void putPair()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, "One", 2, "Two").putPair(Tuples.pair(null, null)));
    }

    @Override
    @Test
    public void withKeyValue()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, "One", 2, "2").withKeyValue(null, null));
    }

    @Override
    @Test
    public void withMap()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeyValue(1, 'a').withMap(Maps.mutable.with(1, Character.valueOf('a'))));
    }

    @Override
    @Test
    public void withMapEmpty()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeyValue(1, 'a').withMap(Maps.mutable.empty()));
    }

    @Override
    @Test
    public void withMapTargetEmpty()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().withMap(Maps.mutable.with(1, Character.valueOf('a'))));
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

    @Test
    @Override
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

    @Test
    @Override
    public void putAllMapIterableNull()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMap().putAllMapIterable(null));
    }

    @Override
    @Test
    public void withAllKeyValues()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues("A", 1, "B", 2).withAllKeyValues(
                FastList.newListWith(Tuples.pair("B", 22), Tuples.pair("C", 3))));
    }

    @Override
    @Test
    public void withAllKeyValueArguments()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues("A", 1, "B", 2).withAllKeyValueArguments(Tuples.pair("B", 22), Tuples.pair("C", 3)));
    }

    @Override
    @Test
    public void withoutKey()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues("A", 1, "B", 2).withoutKey("B"));
    }

    @Override
    @Test
    public void withoutAllKeys()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues("A", 1, "B", 2, "C", 3).withoutAllKeys(FastList.newListWith("A", "C")));
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        assertSame(map, map.asUnmodifiable());
    }

    @Test
    public void entrySet()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2").asUnmodifiable();

        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().remove(null));

        assertThrows(UnsupportedOperationException.class, () -> Iterate.getFirst(map.entrySet()).setValue("Three"));

        assertEquals(this.newMapWithKeysValues(1, "One", 2, "2"), map);
    }

    @Test
    public void entrySetToArray()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One").asUnmodifiable();
        Object[] entries = map.entrySet().toArray();
        assertEquals(ImmutableEntry.of(1, "One"), entries[0]);
    }

    @Test
    public void entrySetToArrayWithTarget()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One").asUnmodifiable();
        Object[] entries = map.entrySet().toArray(new Object[]{});
        assertEquals(ImmutableEntry.of(1, "One"), entries[0]);
    }

    @Override
    @Test
    public void testClone()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        MutableMap<Integer, String> clone = map.clone();
        assertSame(map, clone);
    }

    @Test
    @Override
    public void updateValue()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.<Integer, Integer>newMap().updateValue(0, () -> 0, Functions.identity()));
    }

    @Test
    @Override
    public void updateValue_collisions()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.<Integer, Integer>newMap().updateValue(0, () -> 0, Functions.identity()));
    }

    @Test
    @Override
    public void updateValueWith()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.<Integer, Integer>newMap().updateValueWith(0, () -> 0, (integer, parameter) -> 0, "test"));
    }

    @Test
    @Override
    public void updateValueWith_collisions()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.<Integer, Integer>newMap().updateValueWith(0, () -> 0, (integer, parameter) -> 0, "test"));
    }

    @Override
    public void retainAllFromKeySet_null_collision()
    {
        // Not applicable for unmodifiable maps
    }

    @Override
    public void rehash_null_collision()
    {
        // Not applicable for unmodifiable maps
    }
}
