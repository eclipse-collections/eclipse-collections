/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable.sorted;

import java.util.Map;

import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.map.sorted.mutable.UnmodifiableTreeMap;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class UnmodifiableTreeMapTest implements UnmodifiableMutableSortedMapTestCase
{
    @Override
    public <T> MutableSortedMap<Object, T> newWith(T... elements)
    {
        int i = elements.length;
        MutableSortedMap<Object, T> result = new TreeSortedMap<>(Comparators.reverseNaturalOrder());
        for (T each : elements)
        {
            assertNull(result.put(i, each));
            i--;
        }
        return UnmodifiableTreeMap.of(result);
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        MutableSortedMap<K, V> result = new TreeSortedMap<>(Comparators.reverseNaturalOrder());
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return UnmodifiableTreeMap.of(result);
    }

    @Override
    @Test
    public void Map_clear()
    {
        MutableSortedMap<Object, String> map = this.newWith("Three", "Two", "One");
        assertThrows(UnsupportedOperationException.class, map::clear);
    }

    @Override
    @Test
    public void Map_remove()
    {
        MutableSortedMap<Object, Object> map = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> map.remove(2));
    }

    @Override
    @Test
    public void Map_entrySet_remove()
    {
        MutableSortedMap<Object, Object> map = this.newWithKeysValues();
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().remove(ImmutableEntry.of(null, null)));
    }

    @Override
    @Test
    public void Map_entrySet_setValue()
    {
        Map<String, Integer> map = this.newWithKeysValues("3", 3, "2", 2, "1", 1);
        map.entrySet().forEach(each -> assertThrows(UnsupportedOperationException.class, () -> each.setValue(each.getValue() + 1)));
        assertIterablesEqual(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);
    }

    @Override
    @Test
    public void Map_put()
    {
        MutableSortedMap<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, "Four"));
        assertThrows(UnsupportedOperationException.class, () -> map.put(1, "One"));
        assertThrows(UnsupportedOperationException.class, () -> map.put(5, null));
        assertThrows(UnsupportedOperationException.class, () -> map.put(null, "Six"));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Override
    @Test
    public void Map_putAll()
    {
        MutableSortedMap<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "2");
        MutableSortedMap<Integer, String> toAdd = this.newWithKeysValues(2, "Two", 1, "One");

        assertThrows(UnsupportedOperationException.class, () -> map.putAll(toAdd));

        MutableSortedMap<Integer, String> expected = this.newWithKeysValues(3, "Three", 2, "2");
        assertIterablesEqual(expected, map);

        assertThrows(UnsupportedOperationException.class, () -> map.putAll(null));
        assertThrows(UnsupportedOperationException.class, () -> map.putAll(Map.of()));
    }

    @Override
    @Test
    public void Map_merge()
    {
        MutableSortedMap<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
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
    @Test
    public void Map_compute()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertThrows(UnsupportedOperationException.class, () -> map.compute(1, null));

        assertThrows(UnsupportedOperationException.class, () -> map.compute(1, (k, v) -> {
            fail("Expected lambda not to be called for existing key");
            return "Should not be returned";
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        assertThrows(UnsupportedOperationException.class, () -> map.compute(4, (k, v) -> {
            fail("Expected lambda not to be called for non-existing key");
            return "Should not be returned";
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    @Test
    public void Map_computeIfAbsent()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertThrows(UnsupportedOperationException.class, () -> map.computeIfAbsent(1, null));

        assertThrows(UnsupportedOperationException.class, () -> map.computeIfAbsent(1, k -> {
            fail("Expected lambda not to be called for existing key");
            return "Should not be returned";
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        assertThrows(UnsupportedOperationException.class, () -> map.computeIfAbsent(4, k -> {
            fail("Expected lambda not to be called for non-existing key");
            return "Should not be returned";
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    @Test
    public void Map_computeIfPresent()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertThrows(UnsupportedOperationException.class, () -> map.computeIfPresent(1, null));

        assertThrows(UnsupportedOperationException.class, () -> map.computeIfPresent(1, (k, v) -> {
            fail("Expected lambda not to be called for existing key");
            return "Should not be returned";
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        assertThrows(UnsupportedOperationException.class, () -> map.computeIfPresent(4, (k, v) -> {
            fail("Expected lambda not to be called for non-existing key");
            return "Should not be returned";
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    @Test
    public void Map_replaceAll()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertThrows(UnsupportedOperationException.class, () -> map.replaceAll(null));

        assertThrows(UnsupportedOperationException.class, () -> map.replaceAll((k, v) -> {
            fail("Expected lambda not to be called for existing key");
            return "Should not be returned";
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);

        Map<Integer, String> emptyMap = this.newWithKeysValues();
        assertThrows(UnsupportedOperationException.class, () -> emptyMap.replaceAll((k, v) -> {
            fail("Expected lambda not to be called for empty map");
            return "Should not be returned";
        }));
        assertEquals(this.newWithKeysValues(), emptyMap);
    }
}
