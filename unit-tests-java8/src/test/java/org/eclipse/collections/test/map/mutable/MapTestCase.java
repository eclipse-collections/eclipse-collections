/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.test.Verify.assertSize;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public interface MapTestCase
{
    <T> Map<Object, T> newWith(T... elements);

    <K, V> Map<K, V> newWithKeysValues(Object... elements);

    default boolean supportsNullKeys()
    {
        return true;
    }

    default boolean supportsNullValues()
    {
        return true;
    }

    default void Iterable_toString()
    {
        Map<String, Integer> map = this.newWithKeysValues("Two", 2, "One", 1);
        assertEquals("[Two, One]", map.keySet().toString());
        assertEquals("[2, 1]", map.values().toString());
        assertEquals("[Two=2, One=1]", map.entrySet().toString());
    }

    @Test
    default void Map_clear()
    {
        Map<Object, String> map = this.newWith("Three", "Two", "One");
        map.clear();
        assertIterablesEqual(this.newWith(), map);

        Map<Object, Object> map2 = this.newWith();
        map2.clear();
        assertIterablesEqual(this.newWith(), map2);
    }

    @Test
    default void Map_remove()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        assertEquals("Two", map.remove(2));
        assertIterablesEqual(
                this.newWithKeysValues(3, "Three", 1, "One"),
                map);

        if (this.supportsNullKeys())
        {
            assertNull(map.remove(null));
            assertIterablesEqual(
                    this.newWithKeysValues(3, "Three", 1, "One"),
                    map);

            Map<Integer, String> map2 = this.newWithKeysValues(3, "Three", null, "Two", 1, "One");
            assertEquals("Two", map2.remove(null));
            assertIterablesEqual(
                    this.newWithKeysValues(3, "Three", 1, "One"),
                    map2);
        }
    }

    @Test
    default void Map_keySet_equals()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        Set<Integer> expected = new HashSetNoIterator<>();
        expected.add(3);
        expected.add(2);
        expected.add(1);
        assertEquals(expected, map.keySet());
    }

    @Test
    default void Map_keySet_forEach()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        MutableSet<Integer> actual = Sets.mutable.with();
        map.keySet().forEach(actual::add);
        assertEquals(Sets.immutable.with(3, 2, 1), actual);
    }

    @Test
    default void Map_keySet()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        Set<Integer> keySet = map.keySet();

        assertEquals(3, keySet.size());
        assertFalse(keySet.isEmpty());

        assertTrue(keySet.contains(1));
        assertTrue(keySet.contains(2));
        assertTrue(keySet.contains(3));
        assertFalse(keySet.contains(4));

        assertTrue(keySet.containsAll(List.of(1, 2, 3)));
        assertFalse(keySet.containsAll(List.of(1, 4)));

        Map<Object, Object> empty = this.newWith();
        assertEquals(0, empty.keySet().size());
        assertTrue(empty.keySet().isEmpty());

        if (this.supportsNullKeys())
        {
            assertFalse(keySet.contains(null));

            Map<Integer, String> map2 = this.newWithKeysValues(null, "Null", 1, "One");
            assertTrue(map2.keySet().contains(null));
        }
    }

    @Test
    default void Map_entrySet_equals()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "One", 2, "Two", 3, "Three");
        Set<Map.Entry<Integer, String>> expected = new HashSetNoIterator<>();
        expected.add(ImmutableEntry.of(1, "One"));
        expected.add(ImmutableEntry.of(2, "Two"));
        expected.add(ImmutableEntry.of(3, "Three"));
        assertEquals(expected, map.entrySet());
    }

    @Test
    default void Map_entrySet_forEach()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        MutableSet<String> actual = Sets.mutable.with();
        map.entrySet().forEach(each -> actual.add(each.getValue()));
        assertEquals(Sets.immutable.with("Three", "Two", "One"), actual);
    }

    @Test
    default void Map_entrySet_remove()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        assertTrue(map.entrySet().remove(ImmutableEntry.of(2, "Two")));
        assertIterablesEqual(
                this.newWithKeysValues(3, "Three", 1, "One"),
                map);

        assertFalse(map.entrySet().remove(ImmutableEntry.of(4, "Four")));
        assertIterablesEqual(
                this.newWithKeysValues(3, "Three", 1, "One"),
                map);

        if (this.supportsNullKeys())
        {
            assertFalse(map.entrySet().remove(null));
            assertIterablesEqual(
                    this.newWithKeysValues(3, "Three", 1, "One"),
                    map);

            Map<Integer, String> map2 = this.newWithKeysValues(3, "Three", null, "Two", 1, "One");
            assertTrue(map2.entrySet().remove(ImmutableEntry.of(null, "Two")));
            assertIterablesEqual(
                    this.newWithKeysValues(3, "Three", 1, "One"),
                    map2);
        }
    }

    @Test
    default void Map_entrySet_setValue()
    {
        Map<String, Integer> map = this.newWithKeysValues("3", 3, "2", 2, "1", 1);
        map.entrySet().forEach(each -> {
            Integer currentValue = each.getValue();
            Integer oldValue = each.setValue(currentValue + 1);
            assertEquals(currentValue, oldValue);
        });
        assertIterablesEqual(this.newWithKeysValues("3", 4, "2", 3, "1", 2), map);
    }

    @Test
    default void Map_entrySet()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();

        assertEquals(3, entrySet.size());
        assertFalse(entrySet.isEmpty());

        assertTrue(entrySet.contains(ImmutableEntry.of(1, "One")));
        assertTrue(entrySet.contains(ImmutableEntry.of(2, "Two")));
        assertTrue(entrySet.contains(ImmutableEntry.of(3, "Three")));
        assertFalse(entrySet.contains(ImmutableEntry.of(4, "Four")));
        assertFalse(entrySet.contains(ImmutableEntry.of(1, "Wrong")));

        assertTrue(entrySet.containsAll(List.of(
                ImmutableEntry.of(1, "One"),
                ImmutableEntry.of(2, "Two"),
                ImmutableEntry.of(3, "Three"))));
        assertFalse(entrySet.containsAll(List.of(
                ImmutableEntry.of(1, "One"),
                ImmutableEntry.of(4, "Four"))));

        Map<Object, Object> empty = this.newWith();
        assertEquals(0, empty.entrySet().size());
        assertTrue(empty.entrySet().isEmpty());

        if (this.supportsNullKeys())
        {
            Map<Integer, String> map2 = this.newWithKeysValues(null, "Null", 1, "One");
            assertTrue(map2.entrySet().contains(ImmutableEntry.of(null, "Null")));
            assertFalse(map2.entrySet().contains(ImmutableEntry.of(null, "Wrong")));
        }

        if (this.supportsNullValues())
        {
            Map<Integer, String> map3 = this.newWithKeysValues(1, null, 2, "Two");
            assertTrue(map3.entrySet().contains(ImmutableEntry.of(1, null)));
            assertFalse(map3.entrySet().contains(ImmutableEntry.of(1, "Wrong")));
        }
    }

    @Test
    default void Map_put()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        assertNull(map.put(4, "Four"));
        assertIterablesEqual(
                this.newWithKeysValues(3, "Three", 2, "Two", 1, "One", 4, "Four"),
                map);
        assertEquals("Three", map.put(3, "Three3"));
        assertIterablesEqual(
                this.newWithKeysValues(3, "Three3", 2, "Two", 1, "One", 4, "Four"),
                map);

        if (this.supportsNullValues())
        {
            assertNull(map.put(5, null));
            assertIterablesEqual(
                    this.newWithKeysValues(3, "Three3", 2, "Two", 1, "One", 4, "Four", 5, null),
                    map);
            assertNull(map.put(5, "Five"));
            assertIterablesEqual(
                    this.newWithKeysValues(3, "Three3", 2, "Two", 1, "One", 4, "Four", 5, "Five"),
                    map);
        }

        if (this.supportsNullKeys())
        {
            assertNull(map.put(null, "Six"));
            assertIterablesEqual(
                    this.newWithKeysValues(3, "Three3", 2, "Two", 1, "One", 4, "Four", 5, "Five", null, "Six"),
                    map);
            assertEquals("Six", map.put(null, "Seven"));
            assertIterablesEqual(
                    this.newWithKeysValues(3, "Three3", 2, "Two", 1, "One", 4, "Four", 5, "Five", null, "Seven"),
                    map);
        }

        AlwaysEqual key1 = new AlwaysEqual();
        AlwaysEqual key2 = new AlwaysEqual();
        Object value1 = new Object();
        Object value2 = new Object();
        Map<AlwaysEqual, Object> map2 = this.newWithKeysValues(key1, value1);
        Object previousValue = map2.put(key2, value2);
        assertSame(value1, previousValue);
        map2.forEach((key, value) -> assertSame(key1, key));
        map2.forEach((key, value) -> assertSame(value2, value));
    }

    @Test
    default void Map_putAll()
    {
        Map<Integer, String> map = this.newWithKeysValues(
                3, "Three",
                2, "2");
        Map<Integer, String> toAdd = this.newWithKeysValues(
                2, "Two",
                1, "One");

        map.putAll(toAdd);

        Map<Integer, String> expected = this.newWithKeysValues(
                3, "Three",
                2, "Two",
                1, "One");
        assertIterablesEqual(expected, map);

        assertThrows(NullPointerException.class, () -> map.putAll(null));
        map.putAll(Map.of());
        assertIterablesEqual(expected, map);

        //Testing JDK map
        Map<Integer, String> map2 = this.newWithKeysValues(
                3, "Three",
                2, "2");
        Map<Integer, String> hashMapToAdd = new LinkedHashMap<>();
        hashMapToAdd.put(2, "Two");
        hashMapToAdd.put(1, "One");
        map2.putAll(hashMapToAdd);

        assertIterablesEqual(expected, map2);
    }

    @Test
    default void Map_merge()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        // null value
        assertThrows(NullPointerException.class, () -> map.merge(1, null, (v1, v2) -> {
            fail("Expected no merge to be performed since the value is null");
            return null;
        }));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        // null remapping function
        assertThrows(NullPointerException.class, () -> map.merge(1, "4", null));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        // new key, remapping function isn't called
        String value1 = map.merge(4, "4", (v1, v2) -> {
            fail("Expected no merge to be performed since the key is not present in the map");
            return null;
        });
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4"), map);
        assertEquals("4", value1);

        // existing key
        String value2 = map.merge(2, "Two", (v1, v2) -> {
            assertEquals("2", v1);
            assertEquals("Two", v2);
            return v1 + v2;
        });
        assertEquals("2Two", value2);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Two", 3, "3", 4, "4"), map);

        // existing key, null returned from remapping function, removes key
        String value3 = map.merge(3, "Three", (v1, v2) -> null);
        assertNull(value3);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Two", 4, "4"), map);

        // new key, null returned from remapping function
        String value4 = map.merge(5, "5", (v1, v2) -> null);
        assertEquals("5", value4);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Two", 4, "4", 5, "5"), map);

        // existing key, remapping function throws exception
        RuntimeException exception = new RuntimeException("Test exception");
        RuntimeException actualException1 = assertThrows(RuntimeException.class, () -> map.merge(4, "Four", (v1, v2) -> {
            assertEquals("4", v1);
            assertEquals("Four", v2);
            throw exception;
        }));
        assertSame(exception, actualException1);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Two", 4, "4", 5, "5"), map);

        // existing key with null value, remapping function is not called and new value is used
        if (this.supportsNullValues())
        {
            map.put(2, null);
            String value5 = map.merge(2, "Two", (oldValue, newValue) -> {
                fail("Should not be called for null value key. But was invoked for old value: " + oldValue + ", new value: " + newValue);
                return null;
            });
            assertEquals("Two", value5);
        }
    }

    @Test
    default void Map_compute()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertThrows(NullPointerException.class, () -> map.compute(1, null));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        String value1 = map.compute(4, (k, v) -> {
            assertEquals(Integer.valueOf(4), k);
            assertNull(v);
            return "4";
        });
        assertEquals("4", value1);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4"), map);

        String value2 = map.compute(2, (k, v) -> {
            assertEquals(Integer.valueOf(2), k);
            assertEquals("2", v);
            return v + "Modified";
        });
        assertEquals("2Modified", value2);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Modified", 3, "3", 4, "4"), map);

        String value3 = map.compute(3, (k, v) -> null);
        assertNull(value3);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Modified", 4, "4"), map);

        String value4 = map.compute(5, (k, v) -> null);
        assertNull(value4);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Modified", 4, "4"), map);

        RuntimeException exception = new RuntimeException("Test exception");
        RuntimeException actualException = assertThrows(RuntimeException.class, () -> map.compute(4, (k, v) -> {
            throw exception;
        }));
        assertSame(exception, actualException);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Modified", 4, "4"), map);

        RuntimeException actualException2 = assertThrows(RuntimeException.class, () -> map.compute(6, (k, v) -> {
            throw exception;
        }));
        assertSame(exception, actualException2);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Modified", 4, "4"), map);
    }

    @Test
    default void Map_computeIfAbsent()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertThrows(NullPointerException.class, () -> map.computeIfAbsent(1, null));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        String value1 = map.computeIfAbsent(2, k -> {
            fail("Expected mapping function not to be called for existing key");
            return "Should not be returned";
        });
        assertEquals("2", value1);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        String value2 = map.computeIfAbsent(4, k -> {
            assertEquals(Integer.valueOf(4), k);
            return "4";
        });
        assertEquals("4", value2);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4"), map);

        String value3 = map.computeIfAbsent(5, k -> null);
        assertNull(value3);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4"), map);

        RuntimeException exception = new RuntimeException("Test exception");
        RuntimeException actualException = assertThrows(RuntimeException.class, () -> map.computeIfAbsent(6, k -> {
            throw exception;
        }));
        assertSame(exception, actualException);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4"), map);

        if (this.supportsNullValues())
        {
            Map<Integer, String> map2 = this.newWithKeysValues(1, null, 2, "2");
            String value4 = map2.computeIfAbsent(1, k -> "One");
            assertEquals("One", value4);
            assertEquals("One", map2.get(1));
        }
    }

    @Test
    default void Map_computeIfPresent()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertThrows(NullPointerException.class, () -> map.computeIfPresent(1, null));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        String value1 = map.computeIfPresent(4, (k, v) -> {
            fail("Expected remapping function not to be called for non-existing key");
            return "Should not be returned";
        });
        assertNull(value1);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        String value2 = map.computeIfPresent(2, (k, v) -> {
            assertEquals(Integer.valueOf(2), k);
            assertEquals("2", v);
            return v + "Modified";
        });
        assertEquals("2Modified", value2);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Modified", 3, "3"), map);

        String value3 = map.computeIfPresent(3, (k, v) -> null);
        assertNull(value3);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Modified"), map);

        map.put(3, "3");
        RuntimeException exception = new RuntimeException("Test exception");
        RuntimeException actualException = assertThrows(RuntimeException.class, () -> map.computeIfPresent(3, (k, v) -> {
            assertEquals(Integer.valueOf(3), k);
            assertEquals("3", v);
            throw exception;
        }));
        assertSame(exception, actualException);
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2Modified", 3, "3"), map);

        if (this.supportsNullValues())
        {
            Map<Integer, String> map2 = this.newWithKeysValues(1, null, 2, "2");
            String value4 = map2.computeIfPresent(1, (k, v) -> {
                fail("Expected remapping function not to be called for null value");
                return "Should not be returned";
            });
            assertNull(value4);
            assertNull(map2.get(1));
            assertTrue(map2.containsKey(1));
        }
    }

    @Test
    default void Map_replaceAll()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertThrows(NullPointerException.class, () -> map.replaceAll(null));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        map.replaceAll((k, v) -> {
            assertNotNull(k);
            assertNotNull(v);
            return v + k;
        });
        assertIterablesEqual(this.newWithKeysValues(1, "11", 2, "22", 3, "33"), map);

        if (this.supportsNullValues())
        {
            Map<Integer, String> map2 = this.newWithKeysValues(1, "1", 2, "2");
            map2.replaceAll((k, v) -> null);
            assertEquals(2, map2.size());
            assertNull(map2.get(1));
            assertTrue(map2.containsKey(1));
            assertNull(map2.get(2));
            assertTrue(map2.containsKey(2));
        }

        Map<Integer, String> map3 = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        RuntimeException exception = new RuntimeException("Test exception");
        RuntimeException actualException = assertThrows(RuntimeException.class, () -> map3.replaceAll((k, v) -> {
            if (k.equals(2))
            {
                throw exception;
            }
            return v + "Modified";
        }));
        assertSame(exception, actualException);
        assertTrue(map3.containsKey(1));
        assertTrue(map3.containsKey(2));
        assertTrue(map3.containsKey(3));
        assertEquals("2", map3.get(2));
        assertSize(3, map3);
    }

    @Test
    default void Map_replace()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        // replace existing key
        assertEquals("1", map.replace(1, "One"));
        assertIterablesEqual(this.newWithKeysValues(1, "One", 2, "2", 3, "3"), map);

        // replace non-existing key
        assertNull(map.replace(4, "Four"));
        assertIterablesEqual(this.newWithKeysValues(1, "One", 2, "2", 3, "3"), map);

        // replace with oldValue match
        assertTrue(map.replace(2, "2", "Two"));
        assertIterablesEqual(this.newWithKeysValues(1, "One", 2, "Two", 3, "3"), map);

        // replace with oldValue mismatch
        assertFalse(map.replace(3, "wrong", "Three"));
        assertIterablesEqual(this.newWithKeysValues(1, "One", 2, "Two", 3, "3"), map);

        if (this.supportsNullKeys())
        {
            Map<Integer, String> map2 = this.newWithKeysValues(null, "Null", 1, "1");
            assertEquals("Null", map2.replace(null, "NullReplaced"));
            assertIterablesEqual(this.newWithKeysValues(null, "NullReplaced", 1, "1"), map2);
        }

        if (this.supportsNullValues())
        {
            Map<Integer, String> map3 = this.newWithKeysValues(1, null, 2, "2");
            assertNull(map3.replace(1, "One"));
            assertIterablesEqual(this.newWithKeysValues(1, "One", 2, "2"), map3);
        }
    }

    @Test
    default void Map_putIfAbsent()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        // existing key
        assertEquals("1", map.putIfAbsent(1, "One"));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        // new key
        assertNull(map.putIfAbsent(4, "4"));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4"), map);

        if (this.supportsNullKeys())
        {
            assertNull(map.putIfAbsent(null, "Null"));
            assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4", null, "Null"), map);
        }

        if (this.supportsNullValues())
        {
            Map<Integer, String> map2 = this.newWithKeysValues(1, "1", 2, "2");
            assertNull(map2.putIfAbsent(5, null));
            assertTrue(map2.containsKey(5));

            Map<Integer, String> map3 = this.newWithKeysValues(1, null, 2, "2");
            assertNull(map3.putIfAbsent(1, "One"));
            assertEquals("One", map3.get(1));
        }
    }

    @Test
    default void Map_remove_key_value()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        // matching key and value
        assertTrue(map.remove(1, "1"));
        assertIterablesEqual(this.newWithKeysValues(2, "2", 3, "3"), map);

        // matching key, wrong value
        assertFalse(map.remove(2, "wrong"));
        assertIterablesEqual(this.newWithKeysValues(2, "2", 3, "3"), map);

        // non-existing key
        assertFalse(map.remove(4, "4"));
        assertIterablesEqual(this.newWithKeysValues(2, "2", 3, "3"), map);

        if (this.supportsNullKeys())
        {
            Map<Integer, String> map2 = this.newWithKeysValues(null, "Null", 1, "1");
            assertTrue(map2.remove(null, "Null"));
            assertIterablesEqual(this.newWithKeysValues(1, "1"), map2);
        }
    }

    @Test
    default void Map_forEach_BiConsumer()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        MutableSet<String> actual = Sets.mutable.with();
        map.forEach((BiConsumer<Integer, String>) (key, value) -> actual.add(key + "=" + value));
        assertEquals(Sets.immutable.with("1=1", "2=2", "3=3"), actual);
    }

    @Test
    default void Map_values()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        Collection<String> values = map.values();

        assertEquals(3, values.size());
        assertFalse(values.isEmpty());

        assertTrue(values.contains("One"));
        assertTrue(values.contains("Two"));
        assertTrue(values.contains("Three"));
        assertFalse(values.contains("Four"));

        assertTrue(values.containsAll(List.of("One", "Two", "Three")));
        assertFalse(values.containsAll(List.of("One", "Four")));

        Map<Object, Object> empty = this.newWith();
        assertEquals(0, empty.values().size());
        assertTrue(empty.values().isEmpty());

        if (this.supportsNullValues())
        {
            assertFalse(values.contains(null));

            Map<Integer, String> map2 = this.newWithKeysValues(1, null, 2, "2");
            assertTrue(map2.values().contains(null));
            assertFalse(map2.values().spliterator().hasCharacteristics(Spliterator.NONNULL));
        }
    }

    class AlwaysEqual
            implements Comparable<AlwaysEqual>
    {
        @Override
        public boolean equals(Object obj)
        {
            return obj != null;
        }

        @Override
        public int hashCode()
        {
            return 0;
        }

        @Override
        public int compareTo(AlwaysEqual o)
        {
            return 0;
        }
    }

    class HashSetNoIterator<T> extends HashSet<T>
    {
        @Override
        public Iterator<T> iterator()
        {
            throw new AssertionError();
        }
    }
}
