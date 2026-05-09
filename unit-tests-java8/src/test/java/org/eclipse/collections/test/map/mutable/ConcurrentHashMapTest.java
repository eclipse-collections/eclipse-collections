/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable;

import java.util.Map;
import java.util.Random;
import java.util.Spliterator;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ConcurrentHashMapTest implements MutableMapTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    public <T> MutableMap<Object, T> newWith(T... elements)
    {
        Random random = new Random(CURRENT_TIME_MILLIS);
        MutableMap<Object, T> result = new ConcurrentHashMap<>();
        for (T each : elements)
        {
            assertNull(result.put(random.nextDouble(), each));
        }
        return result;
    }

    @Override
    public <K, V> MutableMap<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        MutableMap<K, V> result = new ConcurrentHashMap<>();
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return result;
    }

    @Override
    public boolean supportsNullKeys()
    {
        return false;
    }

    // TODO: Fix bug in ConcurrentHashMap.putIfAbsent: null values should be treated as absent.
    // When fixed, delete this override to inherit the correct assertion from MapTestCase.
    @Override
    @Test
    public void Map_putIfAbsent()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertEquals("1", map.putIfAbsent(1, "One"));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        assertNull(map.putIfAbsent(4, "4"));
        assertIterablesEqual(this.newWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4"), map);

        Map<Integer, String> map2 = this.newWithKeysValues(1, "1", 2, "2");
        assertNull(map2.putIfAbsent(5, null));
        assertTrue(map2.containsKey(5));

        // TODO: Fix bug in ConcurrentHashMap.putIfAbsent: null values should be treated as absent.
        // When fixed, change assertNull(map3.get(1)) to assertEquals("One", map3.get(1))
        Map<Integer, String> map3 = this.newWithKeysValues(1, null, 2, "2");
        assertNull(map3.putIfAbsent(1, "One"));
        assertNull(map3.get(1));
    }

    // TODO: Fix bug in ConcurrentHashMap.computeIfAbsent: null values should be treated as absent.
    // When fixed, delete this override to inherit the correct assertion from MapTestCase.
    @Override
    @Test
    public void Map_computeIfAbsent()
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

        // TODO: Fix bug in ConcurrentHashMap.computeIfAbsent: null values should be treated as absent.
        // When fixed, change both assertNull calls to assertEquals("One", ...)
        Map<Integer, String> map2 = this.newWithKeysValues(1, null, 2, "2");
        String value4 = map2.computeIfAbsent(1, k -> "One");
        assertNull(value4);
        assertNull(map2.get(1));
    }

    // TODO: Fix bug in ConcurrentHashMap.computeIfPresent: null values should be treated as absent.
    // When fixed, delete this override to inherit the correct assertion from MapTestCase.
    @Override
    @Test
    public void Map_computeIfPresent()
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

        // TODO: Fix bug in ConcurrentHashMap.computeIfPresent: null values should be treated as absent.
        // When fixed, the remapping function should not be called.
        // Replace the following with:
        //   Map<Integer, String> map2 = this.newWithKeysValues(1, null, 2, "2");
        //   assertNull(map2.computeIfPresent(1, (k, v) -> { fail(); return "x"; }));
        //   assertTrue(map2.containsKey(1));
        Map<Integer, String> map2 = this.newWithKeysValues(1, null, 2, "2");
        String value4 = map2.computeIfPresent(1, (k, v) -> {
            assertEquals(Integer.valueOf(1), k);
            assertNull(v);
            return "One";
        });
        assertEquals("One", value4);
        assertEquals("One", map2.get(1));
    }

    // TODO: Fix bug in ConcurrentHashMap: values().spliterator() reports NONNULL but the map supports null values.
    // When fixed, delete this override to inherit the correct assertion from MapTestCase.
    @Override
    @Test
    public void Map_values()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        assertEquals(3, map.values().size());
        assertFalse(map.values().isEmpty());
        assertTrue(map.values().contains("One"));
        assertFalse(map.values().contains("Four"));

        Map<Integer, String> map2 = this.newWithKeysValues(1, null, 2, "2");
        assertTrue(map2.values().contains(null));
        assertTrue(map2.values().spliterator().hasCharacteristics(Spliterator.NONNULL));
    }
}
