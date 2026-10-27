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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.impl.map.ordered.immutable.ImmutableOrderedMapAdapter;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.eclipse.collections.test.map.OrderedMapIterableTestCase;
import org.eclipse.collections.test.map.UnmodifiableMapKeySetTestCase;
import org.eclipse.collections.test.map.UnmodifiableMapValuesCollectionTestCase;
import org.eclipse.collections.test.map.mutable.UnmodifiableMapTestCase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ImmutableOrderedMapTest
        implements OrderedMapIterableTestCase, FixedSizeIterableTestCase, UnmodifiableMapTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

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
    public boolean allowsPut()
    {
        return false;
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
    public boolean supportsNonComparableKeys()
    {
        return true;
    }

    @Override
    @Test
    public void Iterable_toString()
    {
        OrderedMapIterableTestCase.super.Iterable_toString();
        UnmodifiableMapTestCase.super.Iterable_toString();
    }

    @Override
    @Test
    public void Object_equalsAndHashCode()
    {
        OrderedMapIterableTestCase.super.Object_equalsAndHashCode();
        UnmodifiableMapTestCase.super.Object_equalsAndHashCode();
    }

    @Override
    @Test
    public void Iterable_remove()
    {
        FixedSizeIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    public void Map_compute()
    {
        // TODO: Throw before calling the function, like the unmodifiable maps do; ImmutableOrderedMapAdapter inherits the default java.util.Map method, which calls the function before put() throws
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
    @Test
    public void Map_computeIfAbsent()
    {
        // TODO: Throw before calling the function, like the unmodifiable maps do; ImmutableOrderedMapAdapter inherits the default java.util.Map method, which calls the function before put() throws
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertEquals("1", map.computeIfAbsent(1, k -> {
            fail("Expected lambda not to be called for existing key");
            return "modified";
        }));
        assertThrows(UnsupportedOperationException.class, () -> map.computeIfAbsent(4, k -> "new"));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    @Test
    public void Map_computeIfPresent()
    {
        // TODO: Throw before calling the function, like the unmodifiable maps do; ImmutableOrderedMapAdapter inherits the default java.util.Map method, which calls the function before put() throws
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
    @Test
    public void Map_replaceAll()
    {
        // TODO: Throw before calling the function, like the unmodifiable maps do; ImmutableOrderedMapAdapter inherits the default java.util.Map method, which calls the function before put() throws
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");

        assertThrows(UnsupportedOperationException.class, () -> map.replaceAll((k, v) -> v + "modified"));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    @Test
    public void ReversibleIterable_detectLastIndex()
    {
        // TODO Support detectLastIndex for ImmutableOrderedMapAdapter.
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(3, 2, 1).detectLastIndex(each -> true));
    }

    @Override
    @Test
    public void OrderedIterable_forEach_from_to()
    {
        // TODO Support indexed traversal for ImmutableOrderedMapAdapter.
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(3, 2, 1).forEach(0, 1, each -> { }));
    }

    @Override
    @Test
    public void OrderedIterable_forEachWithIndex_from_to()
    {
        // TODO Support indexed traversal for ImmutableOrderedMapAdapter.
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(3, 2, 1).forEachWithIndex(0, 1, (each, index) -> { }));
    }

    @Override
    @Test
    public void ReversibleIterable_reverseForEach()
    {
        // TODO Support reverseForEach for ImmutableOrderedMapAdapter (depends on forEach(int, int)).
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(3, 2, 1).reverseForEach(each -> { }));
    }

    @Nested
    public class KeySetView implements UnmodifiableMapKeySetTestCase
    {
        @Override
        public boolean allowsSerialization()
        {
            return false;
        }

        @SafeVarargs
        @Override
        public final <T> Set<T> newWith(T... elements)
        {
            Random random = new Random(CURRENT_TIME_MILLIS);
            MutableOrderedMap<T, Object> result = OrderedMapAdapter.adapt(new LinkedHashMap<>());
            for (T element : elements)
            {
                assertNull(result.put(element, random.nextDouble()));
            }
            return ((ImmutableOrderedMapAdapter<T, Object>) result.toImmutable()).keySet();
        }
    }

    @Nested
    public class ValuesCollectionView implements UnmodifiableMapValuesCollectionTestCase
    {
        @Override
        public OrderingType getOrderingType()
        {
            return OrderingType.INSERTION_ORDER;
        }

        @Override
        public boolean allowsSerialization()
        {
            return false;
        }

        @SafeVarargs
        @Override
        public final <T> Collection<T> newWith(T... elements)
        {
            return ImmutableOrderedMapTest.this.newWith(elements).values();
        }
    }
}
