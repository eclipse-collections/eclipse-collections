/*
 * Copyright (c) 2021 Two Sigma and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.immutable.ordered;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.impl.map.ordered.immutable.ImmutableOrderedMapAdapter;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.eclipse.collections.test.map.OrderedMapIterableTestCase;
import org.eclipse.collections.test.map.mutable.MapTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ImmutableOrderedMapTest
        implements OrderedMapIterableTestCase, FixedSizeIterableTestCase, MapTestCase
{
    @Override
    public <T> ImmutableOrderedMapAdapter<Object, T> newWith(T... elements)
    {
        int i = elements.length;
        MutableOrderedMap<Object, T> result = OrderedMapAdapter.adapt(new LinkedHashMap<>());
        for (T each : elements)
        {
            assertNull(result.put(i, each));
            i--;
        }

        return (ImmutableOrderedMapAdapter<Object, T>) result.toImmutable();
    }

    @Override
    public <K, V> ImmutableOrderedMapAdapter<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        MutableOrderedMap<K, V> result = OrderedMapAdapter.adapt(new LinkedHashMap<>());
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return (ImmutableOrderedMapAdapter<K, V>) result.toImmutable();
    }

    @Override
    public boolean supportsNullKeys()
    {
        return true;
    }

    @Override
    public boolean supportsNullValues()
    {
        return true;
    }

    @Override
    public void Iterable_toString()
    {
        OrderedMapIterableTestCase.super.Iterable_toString();
        MapTestCase.super.Iterable_toString();
    }

    @Override
    public void Iterable_remove()
    {
        OrderedMapIterableTestCase.super.Iterable_remove();
    }

    @Override
    public void Map_remove()
    {
        Map<Object, Object> map = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> map.remove(2));
    }

    @Override
    public void Map_entrySet_remove()
    {
        Map<Object, Object> map = this.newWithKeysValues();
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().remove(ImmutableEntry.of(null, null)));
    }

    @Override
    public void Map_clear()
    {
        Map<Object, String> map = this.newWith("Three", "Two", "One");
        assertThrows(UnsupportedOperationException.class, map::clear);
    }

    @Override
    @Test
    public void Map_put()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, "Four"));
        assertThrows(UnsupportedOperationException.class, () -> map.put(1, "One"));
        assertThrows(UnsupportedOperationException.class, () -> map.put(5, null));
        assertThrows(UnsupportedOperationException.class, () -> map.put(null, "Six"));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Test
    @Override
    public void Map_putAll()
    {
        Map<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "2");
        Map<Integer, String> toAdd = this.newWithKeysValues(2, "Two", 1, "One");

        assertThrows(UnsupportedOperationException.class, () -> map.putAll(toAdd));

        Map<Integer, String> expected = this.newWithKeysValues(3, "Three", 2, "2");
        assertIterablesEqual(expected, map);

        assertThrows(UnsupportedOperationException.class, () -> map.putAll(null));
        assertThrows(UnsupportedOperationException.class, () -> map.putAll(Map.of()));
    }

    @Override
    public void Map_merge()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.merge(3, "4", (v1, v2) -> {
            fail("Expected lambda not to be called on unmodifiable map");
            return null;
        }));
        assertThrows(UnsupportedOperationException.class, () -> map.merge(4, "4", (v1, v2) -> {
            fail("Expected lambda not to be called on unmodifiable map");
            return null;
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    public void Map_compute()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.compute(1, (k, v) -> {
            assertEquals(Integer.valueOf(1), k);
            assertEquals("1", v);
            return "modified";
        }));
        assertThrows(UnsupportedOperationException.class, () -> map.compute(4, (k, v) -> {
            assertEquals(Integer.valueOf(4), k);
            assertNull(v);
            return "new";
        }));
        assertThrows(UnsupportedOperationException.class, () -> map.compute(2, (k, v) -> null));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    public void Map_computeIfAbsent()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertEquals("1", map.computeIfAbsent(1, k -> {
            fail("Expected lambda not to be called for existing key");
            return "modified";
        }));
        assertThrows(UnsupportedOperationException.class, () -> map.computeIfAbsent(4, k -> "new"));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    public void Map_computeIfPresent()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.computeIfPresent(1, (k, v) -> {
            assertEquals(Integer.valueOf(1), k);
            assertEquals("1", v);
            return "modified";
        }));
        assertNull(map.computeIfPresent(4, (k, v) -> {
            fail("Expected lambda not to be called for non-existing key");
            return "new";
        }));
        assertThrows(UnsupportedOperationException.class, () -> map.computeIfPresent(2, (k, v) -> null));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    public void Map_replaceAll()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        // Note: replaceAll may call the lambda for at least one entry before attempting setValue
        assertThrows(UnsupportedOperationException.class, () -> map.replaceAll((k, v) -> {
            // Lambda may be called for first entry before UnsupportedOperationException is thrown
            assertNotNull(k);
            assertNotNull(v);
            return v + "modified";
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }
}
