/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable.ordered;

import java.util.List;

import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedHashMap;
import org.eclipse.collections.impl.map.ordered.mutable.UnmodifiableMutableOrderedMap;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.eclipse.collections.test.map.mutable.UnmodifiableMutableMapIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class UnmodifiableOrderedHashMapTest
        implements MutableOrderedMapTestCase, FixedSizeIterableTestCase, UnmodifiableMutableMapIterableTestCase
{
    @Override
    public <T> MutableOrderedMap<Object, T> newWith(T... elements)
    {
        int i = elements.length;
        MutableOrderedMap<Object, T> result = new OrderedHashMap<>();
        for (T each : elements)
        {
            assertNull(result.put(i, each));
            i--;
        }
        return UnmodifiableMutableOrderedMap.of(result);
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        MutableOrderedMap<K, V> result = new OrderedHashMap<>();
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return UnmodifiableMutableOrderedMap.of(result);
    }

    @Override
    @Test
    public void Iterable_remove()
    {
        UnmodifiableMutableMapIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    public void MutableMapIterable_removeKey()
    {
        UnmodifiableMutableMapIterableTestCase.super.MutableMapIterable_removeKey();
    }

    @Override
    @Test
    public void Map_put()
    {
        UnmodifiableMutableMapIterableTestCase.super.Map_put();
    }

    @Override
    @Test
    public void Map_putAll()
    {
        UnmodifiableMutableMapIterableTestCase.super.Map_putAll();
    }

    @Override
    @Test
    public void Map_compute()
    {
        UnmodifiableMutableMapIterableTestCase.super.Map_compute();
    }

    @Override
    @Test
    public void Map_computeIfAbsent()
    {
        UnmodifiableMutableMapIterableTestCase.super.Map_computeIfAbsent();
    }

    @Override
    @Test
    public void Map_computeIfPresent()
    {
        UnmodifiableMutableMapIterableTestCase.super.Map_computeIfPresent();
    }

    @Override
    @Test
    public void Map_replaceAll()
    {
        UnmodifiableMutableMapIterableTestCase.super.Map_replaceAll();
    }

    @Override
    @Test
    public void Map_entrySet_setValue()
    {
        UnmodifiableMutableMapIterableTestCase.super.Map_entrySet_setValue();
    }

    @Override
    @Test
    public void Map_entrySet_remove()
    {
        UnmodifiableMutableMapIterableTestCase.super.Map_entrySet_remove();
    }

    @Override
    @Test
    public void Map_keySet_remove()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1).keySet().remove("A"));
    }

    @Override
    @Test
    public void Map_values_remove()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1).values().remove(1));
    }

    @Override
    @Test
    public void MutableMapIterable_getIfAbsentPut()
    {
        UnmodifiableMutableMapIterableTestCase.super.MutableMapIterable_getIfAbsentPut();
    }

    @Override
    @Test
    public void MutableMapIterable_updateValue()
    {
        UnmodifiableMutableMapIterableTestCase.super.MutableMapIterable_updateValue();
    }

    @Override
    @Test
    public void MutableMapIterable_removeIf()
    {
        UnmodifiableMutableMapIterableTestCase.super.MutableMapIterable_removeIf();
    }

    @Override
    @Test
    public void MutableMapIterable_withKeyValue()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1).withKeyValue("B", 2));
    }

    @Override
    @Test
    public void MutableMapIterable_withoutKey()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1).withoutKey("A"));
    }

    @Override
    @Test
    public void MutableMapIterable_withAllKeyValues()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1).withAllKeyValues(List.of(Tuples.pair("B", 2))));
    }

    @Override
    @Test
    public void MutableMapIterable_withAllKeyValueArguments()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1).withAllKeyValueArguments(Tuples.pair("B", 2)));
    }

    @Override
    @Test
    public void MutableMapIterable_withoutAllKeys()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1, "B", 2).withoutAllKeys(List.of("A")));
    }

    @Override
    @Test
    public void OrderedIterable_getFirst_getLast_afterMutableOrderedMapMutations()
    {
        MutableOrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2);
        assertEquals(Integer.valueOf(1), map.getFirst());
        assertEquals(Integer.valueOf(2), map.getLast());
        assertThrows(UnsupportedOperationException.class, () -> map.removeKey("A"));
        assertThrows(UnsupportedOperationException.class, () -> map.put("C", 3));
    }

    @Override
    @Test
    public void Map_merge()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1).merge("B", 2, (a, b) -> b));
    }

    @Override
    @Test
    public void Map_clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWithKeysValues("A", 1).clear());
    }

    @Override
    @Test
    public void MutableOrderedMap_toImmutable()
    {
        MutableOrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2);
        assertEquals(2, map.toImmutable().size());
    }
}
