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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMapIterable;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.IntegerWithCast;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.AbstractSynchronizedMapIterable;
import org.eclipse.collections.impl.map.MapIterableTestCase;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iMap;
import static org.eclipse.collections.impl.factory.Iterables.mList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract JUnit TestCase for {@link MutableMapIterable}s.
 */
public abstract class MutableMapIterableTestCase extends MapIterableTestCase
{
    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMap();

    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMapWithKeyValue(K key, V value);

    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2);

    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3);

    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4);

    @Test
    public void toImmutable()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeyValue(1, "One");
        ImmutableMapIterable<Integer, String> immutable = map.toImmutable();
        assertEquals(Maps.immutable.with(1, "One"), immutable);
    }

    @Test
    public void clear()
    {
        MutableMapIterable<Integer, Object> map =
                this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three");
        map.clear();
        Verify.assertEmpty(map);

        MutableMapIterable<Object, Object> map2 = this.newMap();
        map2.clear();
        Verify.assertEmpty(map2);
    }

    @Test
    public void removeObject()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        map.remove("Two");
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void removeFromEntrySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertTrue(map.entrySet().remove(ImmutableEntry.of("Two", 2)));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);

        assertFalse(map.entrySet().remove(ImmutableEntry.of("Four", 4)));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);

        assertFalse(map.entrySet().remove(null));

        MutableMapIterable<String, Integer> mapWithNullKey = this.newMapWithKeysValues("One", 1, null, 2, "Three", 3);
        assertTrue(mapWithNullKey.entrySet().remove(new ImmutableEntry<String, Integer>(null, 2)));
    }

    @Test
    public void removeAllFromEntrySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertTrue(map.entrySet().removeAll(FastList.newListWith(
                ImmutableEntry.of("One", 1),
                ImmutableEntry.of("Three", 3))));
        assertEquals(UnifiedMap.newWithKeysValues("Two", 2), map);

        assertFalse(map.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of("Four", 4))));
        assertEquals(UnifiedMap.newWithKeysValues("Two", 2), map);

        assertFalse(map.entrySet().remove(null));

        MutableMapIterable<String, Integer> mapWithNullKey = this.newMapWithKeysValues("One", 1, null, 2, "Three", 3);
        assertTrue(mapWithNullKey.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of(null, 2))));
    }

    @Test
    public void retainAllFromEntrySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertFalse(map.entrySet().retainAll(FastList.newListWith(
                ImmutableEntry.of("One", 1),
                ImmutableEntry.of("Two", 2),
                ImmutableEntry.of("Three", 3),
                ImmutableEntry.of("Four", 4))));

        assertTrue(map.entrySet().retainAll(FastList.newListWith(
                ImmutableEntry.of("One", 1),
                ImmutableEntry.of("Three", 3),
                ImmutableEntry.of("Four", 4))));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);

        MutableMapIterable<Holder<Integer>, Holder<Integer>> holders =
                this.newMapWithKeysValues(new Holder<>(1), new Holder<>(1), new Holder<>(2), new Holder<>(2), new Holder<>(3), new Holder<>(3));
        Holder<Integer> copy = new Holder<>(1);
        assertTrue(holders.entrySet().retainAll(mList(ImmutableEntry.of(copy, copy))));
        assertEquals(iMap(copy, copy), holders);
        assertNotSame(copy, Iterate.getOnly(holders.entrySet()).getKey());
        assertNotSame(copy, Iterate.getOnly(holders.entrySet()).getValue());
    }

    @Test
    public void clearEntrySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        map.entrySet().clear();
        Verify.assertEmpty(map);
    }

    @Test
    public void entrySetEqualsAndHashCode()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Verify.assertEqualsAndHashCode(
                UnifiedSet.newSetWith(
                        ImmutableEntry.of("One", 1),
                        ImmutableEntry.of("Two", 2),
                        ImmutableEntry.of("Three", 3)),
                map.entrySet());
    }

    @Test
    public void removeFromKeySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertFalse(map.keySet().remove("Four"));

        assertTrue(map.keySet().remove("Two"));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void removeNullFromKeySet()
    {
        if (this.newMap() instanceof ConcurrentMap || this.newMap() instanceof SortedMap)
        {
            return;
        }

        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertFalse(map.keySet().remove(null));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2, "Three", 3), map);
        map.put(null, 4);
        assertTrue(map.keySet().remove(null));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2, "Three", 3), map);
    }

    @Test
    public void removeAllFromKeySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertFalse(map.keySet().removeAll(FastList.newListWith("Four")));

        assertTrue(map.keySet().removeAll(FastList.newListWith("Two", "Four")));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void retainAllFromKeySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertFalse(map.keySet().retainAll(FastList.newListWith("One", "Two", "Three", "Four")));

        assertTrue(map.keySet().retainAll(FastList.newListWith("One", "Three")));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void clearKeySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        map.keySet().clear();
        Verify.assertEmpty(map);
    }

    @Test
    public void keySetEqualsAndHashCode()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3, null, null);
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("One", "Two", "Three", null), map.keySet());
    }

    @Test
    public void keySetToArray()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        MutableList<String> expected = FastList.newListWith("One", "Two", "Three").toSortedList();
        Set<String> keySet = map.keySet();
        assertEquals(expected, FastList.newListWith(keySet.toArray()).toSortedList());
        assertEquals(expected, FastList.newListWith(keySet.toArray(new String[keySet.size()])).toSortedList());
    }

    @Test
    public void removeFromValues()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertFalse(map.values().remove(4));

        assertTrue(map.values().remove(2));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void removeNullFromValues()
    {
        if (this.newMap() instanceof ConcurrentMap)
        {
            return;
        }

        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertFalse(map.values().remove(null));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2, "Three", 3), map);
        map.put("Four", null);
        assertTrue(map.values().remove(null));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2, "Three", 3), map);
    }

    @Test
    public void removeAllFromValues()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertFalse(map.values().removeAll(FastList.newListWith(4)));

        assertTrue(map.values().removeAll(FastList.newListWith(2, 4)));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void retainAllFromValues()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        assertFalse(map.values().retainAll(FastList.newListWith(1, 2, 3, 4)));

        assertTrue(map.values().retainAll(FastList.newListWith(1, 3)));
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void putAll()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        MutableMapIterable<Integer, String> toAdd = this.newMapWithKeysValues(2, "Two", 3, "Three");

        map.putAll(toAdd);
        Verify.assertSize(3, map);
        Verify.assertContainsAllKeyValues(map, 1, "One", 2, "Two", 3, "Three");

        //Testing JDK map
        MutableMapIterable<Integer, String> map2 = this.newMapWithKeysValues(1, "One", 2, "2");
        Map<Integer, String> hashMaptoAdd = new HashMap<>(toAdd);
        map2.putAll(hashMaptoAdd);
        Verify.assertSize(3, map2);
        Verify.assertContainsAllKeyValues(map2, 1, "One", 2, "Two", 3, "Three");
    }

    @Test
    public void removeKey()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");

        assertEquals("1", map.removeKey(1));
        Verify.assertSize(1, map);
        Verify.denyContainsKey(1, map);

        assertNull(map.removeKey(42));
        Verify.assertSize(1, map);

        assertEquals("Two", map.removeKey(2));
        Verify.assertEmpty(map);
    }

    @Test
    public void removeAllKeys()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two", 3, "Three");

        assertThrows(NullPointerException.class, () -> map.removeAllKeys(null));
        assertFalse(map.removeAllKeys(Sets.mutable.with(4)));
        assertFalse(map.removeAllKeys(Sets.mutable.with(4, 5, 6)));
        assertFalse(map.removeAllKeys(Sets.mutable.with(4, 5, 6, 7, 8, 9)));

        assertTrue(map.removeAllKeys(Sets.mutable.with(1)));
        Verify.denyContainsKey(1, map);
        assertTrue(map.removeAllKeys(Sets.mutable.with(3, 4, 5, 6, 7)));
        Verify.denyContainsKey(3, map);

        map.putAll(Maps.mutable.with(4, "Four", 5, "Five", 6, "Six", 7, "Seven"));
        assertTrue(map.removeAllKeys(Sets.mutable.with(2, 3, 9, 10)));
        Verify.denyContainsKey(2, map);
        assertTrue(map.removeAllKeys(Sets.mutable.with(5, 3, 7, 8, 9)));
        assertEquals(Maps.mutable.with(4, "Four", 6, "Six"), map);
    }

    @Test
    public void removeIf()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");

        assertFalse(map.removeIf(Predicates2.alwaysFalse()));
        assertEquals(this.newMapWithKeysValues(1, "1", 2, "Two"), map);
        assertTrue(map.removeIf(Predicates2.alwaysTrue()));
        Verify.assertEmpty(map);

        map.putAll(Maps.mutable.with(1, "One", 2, "TWO", 3, "THREE", 4, "four"));
        map.putAll(Maps.mutable.with(5, "Five", 6, "Six", 7, "Seven", 8, "Eight"));
        assertTrue(map.removeIf((each, value) -> each % 2 == 0 && value.length() < 4));
        Verify.denyContainsKey(2, map);
        Verify.denyContainsKey(6, map);
        MutableMapIterable<Integer, String> expected = this.newMapWithKeysValues(1, "One", 3, "THREE", 4, "four", 5, "Five");
        expected.put(7, "Seven");
        expected.put(8, "Eight");
        assertEquals(expected, map);

        assertTrue(map.removeIf((each, value) -> each % 2 != 0 && value.equals("THREE")));
        Verify.denyContainsKey(3, map);
        Verify.assertSize(5, map);

        assertTrue(map.removeIf((each, value) -> each % 2 != 0));
        assertFalse(map.removeIf((each, value) -> each % 2 != 0));
        assertEquals(this.newMapWithKeysValues(4, "four", 8, "Eight"), map);
    }

    @Test
    public void getIfAbsentPut()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsentPut(4, new PassThruFunction0<>("4")));
        assertEquals("3", map.getIfAbsentPut(3, new PassThruFunction0<>("3")));
        Verify.assertContainsKeyValue(4, "4", map);

        MutableMapIterable<Integer, String> map2 = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        RuntimeException factoryException = new RuntimeException("Factory exception");
        RuntimeException actualException = assertThrows(
                RuntimeException.class,
                () -> map2.getIfAbsentPut(5, () -> { throw factoryException; }));
        assertSame(factoryException, actualException);
        assertEquals(UnifiedMap.newWithKeysValues(1, "1", 2, "2", 3, "3"), map2);
        assertFalse(map2.containsKey(5));
        assertNull(map2.get(5));
        assertEquals(3, map2.size());

        MutableMapIterable<Integer, String> emptyMap = this.newMap();
        RuntimeException emptyMapException = assertThrows(
                RuntimeException.class,
                () -> emptyMap.getIfAbsentPut(1, () -> { throw factoryException; }));
        assertSame(factoryException, emptyMapException);
        assertTrue(emptyMap.isEmpty());

        String result = map2.getIfAbsentPut(1, () -> { throw new RuntimeException("Should not be called"); });
        assertEquals("1", result);
        assertEquals(3, map2.size());
    }

    @Test
    public void getIfAbsentPutValue()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsentPut(4, "4"));
        assertEquals("3", map.getIfAbsentPut(3, "5"));
        Verify.assertContainsKeyValue(1, "1", map);
        Verify.assertContainsKeyValue(2, "2", map);
        Verify.assertContainsKeyValue(3, "3", map);
        Verify.assertContainsKeyValue(4, "4", map);
    }

    @Test
    public void getIfAbsentPutWithKey()
    {
        MutableMapIterable<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        assertNull(map.get(4));
        assertEquals(Integer.valueOf(4), map.getIfAbsentPutWithKey(4, Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(3), map.getIfAbsentPutWithKey(3, Functions.getIntegerPassThru()));
        Verify.assertContainsKeyValue(Integer.valueOf(4), Integer.valueOf(4), map);

        MutableMapIterable<Integer, Integer> map2 = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        RuntimeException functionException = new RuntimeException("Function exception");
        RuntimeException actualException = assertThrows(
                RuntimeException.class,
                () -> map2.getIfAbsentPutWithKey(5, k -> { throw functionException; }));
        assertSame(functionException, actualException);
        assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), map2);
        assertFalse(map2.containsKey(5));
        assertNull(map2.get(5));
        assertEquals(3, map2.size());

        MutableMapIterable<Integer, Integer> emptyMap = this.newMap();
        RuntimeException emptyMapException = assertThrows(
                RuntimeException.class,
                () -> emptyMap.getIfAbsentPutWithKey(1, k -> { throw functionException; }));
        assertSame(functionException, emptyMapException);
        assertTrue(emptyMap.isEmpty());

        Integer result = map2.getIfAbsentPutWithKey(1, k -> { throw new RuntimeException("Should not be called"); });
        assertEquals(Integer.valueOf(1), result);
        assertEquals(3, map2.size());
    }

    @Test
    public void getIfAbsentPutWith()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsentPutWith(4, String::valueOf, 4));
        assertEquals("3", map.getIfAbsentPutWith(3, String::valueOf, 3));
        Verify.assertContainsKeyValue(4, "4", map);

        MutableMapIterable<Integer, String> map2 = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        RuntimeException functionException = new RuntimeException("Function exception");
        RuntimeException actualException = assertThrows(
                RuntimeException.class,
                () -> map2.getIfAbsentPutWith(5, p -> { throw functionException; }, "param"));
        assertSame(functionException, actualException);
        assertEquals(UnifiedMap.newWithKeysValues(1, "1", 2, "2", 3, "3"), map2);
        assertFalse(map2.containsKey(5));
        assertNull(map2.get(5));
        assertEquals(3, map2.size());

        MutableMapIterable<Integer, String> emptyMap = this.newMap();
        RuntimeException emptyMapException = assertThrows(
                RuntimeException.class,
                () -> emptyMap.getIfAbsentPutWith(1, p -> { throw functionException; }, "param"));
        assertSame(functionException, emptyMapException);
        assertTrue(emptyMap.isEmpty());

        String result = map2.getIfAbsentPutWith(1, p -> { throw new RuntimeException("Should not be called"); }, "param");
        assertEquals("1", result);
        assertEquals(3, map2.size());
    }

    @Test
    public void getKeysAndGetValues()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Verify.assertContainsAll(map.keySet(), 1, 2, 3);
        Verify.assertContainsAll(map.values(), "1", "2", "3");
    }

    @Test
    public void newEmpty()
    {
        MutableMapIterable<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        Verify.assertEmpty(map.newEmpty());
    }

    @Test
    public void keysAndValues_toString()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2");
        Verify.assertContains(map.keySet().toString(), FastList.newListWith("[1, 2]", "[2, 1]"));
        Verify.assertContains(map.values().toString(), FastList.newListWith("[1, 2]", "[2, 1]"));
        Verify.assertContains(map.keysView().toString(), FastList.newListWith("[1, 2]", "[2, 1]"));
        Verify.assertContains(map.valuesView().toString(), FastList.newListWith("[1, 2]", "[2, 1]"));
    }

    @Test
    public void keyPreservation()
    {
        Holder<String> key = new Holder<>("key");

        Holder<String> duplicateKey1 = new Holder<>("key");
        MapIterable<Holder<String>, Integer> map1 = this.newMapWithKeysValues(key, 1, duplicateKey1, 2);
        Verify.assertSize(1, map1);
        Verify.assertContainsKeyValue(key, 2, map1);
        assertSame(key, map1.keysView().getFirst());

        Holder<String> duplicateKey2 = new Holder<>("key");
        MapIterable<Holder<String>, Integer> map2 = this.newMapWithKeysValues(key, 1, duplicateKey1, 2, duplicateKey2, 3);
        Verify.assertSize(1, map2);
        Verify.assertContainsKeyValue(key, 3, map2);
        assertSame(key, map1.keysView().getFirst());

        Holder<String> duplicateKey3 = new Holder<>("key");
        MapIterable<Holder<String>, Integer> map3 = this.newMapWithKeysValues(key, 1, new Holder<>("not a dupe"), 2, duplicateKey3, 3);
        Verify.assertSize(2, map3);
        Verify.assertContainsAllKeyValues(map3, key, 3, new Holder<>("not a dupe"), 2);
        assertSame(key, map3.keysView().detect(key::equals));

        Holder<String> duplicateKey4 = new Holder<>("key");
        MapIterable<Holder<String>, Integer> map4 = this.newMapWithKeysValues(key, 1, new Holder<>("still not a dupe"), 2, new Holder<>("me neither"), 3, duplicateKey4, 4);
        Verify.assertSize(3, map4);
        Verify.assertContainsAllKeyValues(map4, key, 4, new Holder<>("still not a dupe"), 2, new Holder<>("me neither"), 3);
        assertSame(key, map4.keysView().detect(key::equals));

        MapIterable<Holder<String>, Integer> map5 = this.newMapWithKeysValues(key, 1, duplicateKey1, 2, duplicateKey3, 3, duplicateKey4, 4);
        Verify.assertSize(1, map5);
        Verify.assertContainsKeyValue(key, 4, map5);
        assertSame(key, map5.keysView().getFirst());
    }

    @Test
    public void asUnmodifiable()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, 1, 2, 2).asUnmodifiable().put(3, 3));
    }

    @Test
    public void asSynchronized()
    {
        MapIterable<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2).asSynchronized();
        Verify.assertInstanceOf(AbstractSynchronizedMapIterable.class, map);
    }

    @Test
    public void add()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeyValue("A", 1);

        assertEquals(Integer.valueOf(1), map.add(Tuples.pair("A", 3)));
        assertNull(map.add(Tuples.pair("B", 2)));
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 3, "B", 2), map);
    }

    @Test
    public void putPair()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeyValue("A", 1);

        assertEquals(Integer.valueOf(1), map.putPair(Tuples.pair("A", 3)));
        assertNull(map.putPair(Tuples.pair("B", 2)));
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 3, "B", 2), map);
    }

    @Test
    public void withKeyValue()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeyValue("A", 1);

        MutableMapIterable<String, Integer> mapWith = map.withKeyValue("B", 2);
        assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1, "B", 2), mapWith);

        MutableMapIterable<String, Integer> mapWith2 = mapWith.withKeyValue("A", 11);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 11, "B", 2), mapWith2);
        assertSame(mapWith, mapWith2);
    }

    @Test
    public void withMap()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        Map<String, Integer> simpleMap = Maps.mutable.with("B", 22, "C", 3);
        map.putAll(simpleMap);
        MutableMapIterable<String, Integer> mapWith = map.withMap(simpleMap);
        assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1, "B", 22, "C", 3), mapWith);
    }

    @Test
    public void withMapEmpty()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> mapWith = map.withMap(Maps.mutable.empty());
        assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1, "B", 2), mapWith);
    }

    @Test
    public void withMapTargetEmpty()
    {
        MutableMapIterable<String, Integer> map = this.newMap();
        Map<String, Integer> simpleMap = Maps.mutable.with("A", 1, "B", 2);
        MutableMapIterable<String, Integer> mapWith = map.withMap(simpleMap);
        assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1, "B", 2), mapWith);
    }

    @Test
    public void withMapEmptyAndTargetEmpty()
    {
        MutableMapIterable<String, Integer> map = this.newMap();
        MutableMapIterable<String, Integer> mapWith = map.withMap(Maps.mutable.empty());
        assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newMap(), mapWith);
    }

    @Test
    public void withMapNull()
    {
        assertThrows(NullPointerException.class, () -> this.newMap().withMap(null));
    }

    @Test
    public void withMapIterable()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> simpleMap = Maps.mutable.with("B", 22, "C", 3);
        map.putAll(simpleMap);
        MutableMapIterable<String, Integer> mapWith = map.withMapIterable(simpleMap);
        assertSame(map, mapWith);
        Verify.assertMapsEqual(Maps.mutable.with("A", 1, "B", 22, "C", 3), mapWith);
    }

    @Test
    public void withMapIterableEmpty()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> mapWith = map.withMapIterable(Maps.mutable.empty());
        assertSame(map, mapWith);
        Verify.assertMapsEqual(Maps.mutable.with("A", 1, "B", 2), mapWith);
    }

    @Test
    public void withMapIterableTargetEmpty()
    {
        MutableMapIterable<String, Integer> map = this.newMap();
        MutableMapIterable<String, Integer> mapWith = map.withMapIterable(Maps.mutable.with("A", 1, "B", 2));
        assertSame(map, mapWith);
        Verify.assertMapsEqual(Maps.mutable.with("A", 1, "B", 2), mapWith);
    }

    @Test
    public void withMapIterableEmptyAndTargetEmpty()
    {
        MutableMapIterable<String, Integer> map = this.newMap();
        MutableMapIterable<String, Integer> mapWith = map.withMapIterable(Maps.mutable.empty());
        assertSame(map, mapWith);
        Verify.assertMapsEqual(Maps.mutable.withMapIterable(map), mapWith);
    }

    @Test
    public void withMapIterableNull()
    {
        assertThrows(NullPointerException.class, () -> this.newMap().withMapIterable(null));
    }

    @Test
    public void putAllMapIterable()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> simpleMap = Maps.mutable.with("B", 22, "C", 3);
        map.putAllMapIterable(simpleMap);
        Verify.assertMapsEqual(Maps.mutable.with("A", 1, "B", 22, "C", 3), map);
    }

    @Test
    public void putAllMapIterableEmpty()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        map.putAllMapIterable(Maps.mutable.empty());
        Verify.assertMapsEqual(Maps.mutable.with("A", 1, "B", 2), map);
    }

    @Test
    public void putAllMapIterableTargetEmpty()
    {
        MutableMapIterable<String, Integer> map = this.newMap();
        map.putAllMapIterable(Maps.mutable.with("A", 1, "B", 2));
        Verify.assertMapsEqual(Maps.mutable.with("A", 1, "B", 2), map);
    }

    @Test
    public void putAllMapIterableEmptyAndTargetEmpty()
    {
        MutableMapIterable<String, Integer> map = this.newMap();
        map.putAllMapIterable(Maps.mutable.empty());
        Verify.assertMapsEqual(Maps.mutable.withMapIterable(map), map);
    }

    @Test
    public void putAllMapIterableNull()
    {
        assertThrows(NullPointerException.class, () -> this.newMap().putAllMapIterable(null));
    }

    @Test
    public void withAllKeyValues()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> mapWith = map.withAllKeyValues(
                FastList.newListWith(Tuples.pair("B", 22), Tuples.pair("C", 3)));
        assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1, "B", 22, "C", 3), mapWith);
    }

    @Test
    public void withAllKeyValueArguments()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> mapWith = map.withAllKeyValueArguments(Tuples.pair("B", 22), Tuples.pair("C", 3));
        assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1, "B", 22, "C", 3), mapWith);
    }

    @Test
    public void withoutKey()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> mapWithout = map.withoutKey("B");
        assertSame(map, mapWithout);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1), mapWithout);
    }

    @Test
    public void withoutAllKeys()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2, "C", 3);
        MutableMapIterable<String, Integer> mapWithout = map.withoutAllKeys(FastList.newListWith("A", "C"));
        assertSame(map, mapWithout);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("B", 2), mapWithout);
    }

    @Test
    public void retainAllFromKeySet_null_collision()
    {
        if (this.newMap() instanceof ConcurrentMap || this.newMap() instanceof SortedMap)
        {
            return;
        }

        IntegerWithCast key = new IntegerWithCast(0);
        MutableMapIterable<IntegerWithCast, String> mutableMapIterable = this.newMapWithKeysValues(
                null, "Test 1",
                key, "Test 2");

        assertFalse(mutableMapIterable.keySet().retainAll(FastList.newListWith(key, null)));

        assertEquals(
                this.newMapWithKeysValues(
                        null, "Test 1",
                        key, "Test 2"),
                mutableMapIterable);
    }

    @Test
    public void rehash_null_collision()
    {
        if (this.newMap() instanceof ConcurrentMap || this.newMap() instanceof SortedMap)
        {
            return;
        }
        MutableMapIterable<IntegerWithCast, String> mutableMapIterable = this.newMapWithKeyValue(null, null);

        for (int i = 0; i < 256; i++)
        {
            mutableMapIterable.put(new IntegerWithCast(i), String.valueOf(i));
        }
    }

    @Test
    public void updateValue()
    {
        MutableMapIterable<Integer, Integer> map = this.newMap();
        Iterate.forEach(Interval.oneTo(1000), each -> map.updateValue(each % 10, () -> 0, integer -> integer + 1));
        assertEquals(Interval.zeroTo(9).toSet(), map.keySet());
        assertEquals(FastList.newList(Collections.nCopies(10, 100)), FastList.newList(map.values()));

        MutableMapIterable<Integer, Integer> map2 = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        RuntimeException factoryException = new RuntimeException("Factory exception");
        RuntimeException functionException = new RuntimeException("Function exception");

        RuntimeException actualException1 = assertThrows(
                RuntimeException.class,
                () -> map2.updateValue(4, () -> { throw factoryException; }, v -> v + 1));
        assertSame(factoryException, actualException1);
        assertEquals(this.newMapWithKeysValues(1, 1, 2, 2, 3, 3), map2);
        assertFalse(map2.containsKey(4));
        assertEquals(3, map2.size());

        RuntimeException actualException2 = assertThrows(
                RuntimeException.class,
                () -> map2.updateValue(2, () -> 0, v -> { throw functionException; }));
        assertSame(functionException, actualException2);
        assertEquals(this.newMapWithKeysValues(1, 1, 2, 2, 3, 3), map2);
        assertEquals(Integer.valueOf(2), map2.get(2));
        assertEquals(3, map2.size());
    }

    @Test
    public void updateValue_collisions()
    {
        MutableMapIterable<Integer, Integer> map = this.newMap();
        MutableList<Integer> list = Interval.oneTo(2000).toList().shuffleThis();
        Iterate.forEach(list, each -> map.updateValue(each % 1000, () -> 0, integer -> integer + 1));
        assertEquals(Interval.zeroTo(999).toSet(), map.keySet());
        assertEquals(
                FastList.newList(Collections.nCopies(1000, 2)),
                FastList.newList(map.values()),
                HashBag.newBag(map.values()).toStringOfItemToCount());
    }

    @Test
    public void updateValueWith()
    {
        MutableMapIterable<Integer, Integer> map = this.newMap();
        Iterate.forEach(Interval.oneTo(1000), each -> map.updateValueWith(each % 10, () -> 0, (integer, parameter) ->
        {
            assertEquals("test", parameter);
            return integer + 1;
        }, "test"));
        assertEquals(Interval.zeroTo(9).toSet(), map.keySet());
        assertEquals(FastList.newList(Collections.nCopies(10, 100)), FastList.newList(map.values()));

        MutableMapIterable<Integer, Integer> map2 = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        RuntimeException factoryException = new RuntimeException("Factory exception");
        RuntimeException functionException = new RuntimeException("Function exception");

        RuntimeException actualException1 = assertThrows(
                RuntimeException.class,
                () -> map2.updateValueWith(4, () -> { throw factoryException; }, (v, p) -> v + 1, "param"));
        assertSame(factoryException, actualException1);
        assertEquals(this.newMapWithKeysValues(1, 1, 2, 2, 3, 3), map2);
        assertFalse(map2.containsKey(4));
        assertEquals(3, map2.size());

        RuntimeException actualException2 = assertThrows(
                RuntimeException.class,
                () -> map2.updateValueWith(2, () -> 0, (v, p) -> { throw functionException; }, "param"));
        assertSame(functionException, actualException2);
        assertEquals(this.newMapWithKeysValues(1, 1, 2, 2, 3, 3), map2);
        assertEquals(Integer.valueOf(2), map2.get(2));
        assertEquals(3, map2.size());
    }

    @Test
    public void updateValueWith_collisions()
    {
        MutableMapIterable<Integer, Integer> map = this.newMap();
        MutableList<Integer> list = Interval.oneTo(2000).toList().shuffleThis();
        Iterate.forEach(list, each -> map.updateValueWith(each % 1000, () -> 0, (integer, parameter) ->
        {
            assertEquals("test", parameter);
            return integer + 1;
        }, "test"));
        assertEquals(Interval.zeroTo(999).toSet(), map.keySet());
        assertEquals(
                FastList.newList(Collections.nCopies(1000, 2)),
                FastList.newList(map.values()),
                HashBag.newBag(map.values()).toStringOfItemToCount());
    }
}
