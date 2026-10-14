/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.immutable.sorted;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.map.sorted.immutable.AbstractImmutableSortedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.test.map.UnmodifiableMapKeySetTestCase;
import org.eclipse.collections.test.map.UnmodifiableMapValuesCollectionTestCase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ImmutableTreeMapTest implements ImmutableSortedMapIterableTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    public <T> AbstractImmutableSortedMap<Object, T> newWith(T... elements)
    {
        int i = elements.length;
        MutableSortedMap<Object, T> result = new TreeSortedMap<>(Comparators.reverseNaturalOrder());
        for (T each : elements)
        {
            assertNull(result.put(i, each));
            i--;
        }
        return (AbstractImmutableSortedMap<Object, T>) result.toImmutable();
    }

    @Override
    public <K, V> AbstractImmutableSortedMap<K, V> newWithKeysValues(Object... elements)
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
        return (AbstractImmutableSortedMap<K, V>) result.toImmutable();
    }

    @Override
    @Test
    public void Map_entrySet()
    {
        Set<Map.Entry<Integer, String>> entrySet = this.<Integer, String>newWithKeysValues(3, "Three", 2, "Two", 1, "One").entrySet();
        assertEquals(3, entrySet.size());
        assertTrue(entrySet.containsAll(List.of(
                ImmutableEntry.of(1, "One"),
                ImmutableEntry.of(2, "Two"),
                ImmutableEntry.of(3, "Three"))));
        assertFalse(entrySet.contains(ImmutableEntry.of(4, "Four")));
        assertFalse(entrySet.contains(ImmutableEntry.of(1, "Wrong")));
    }

    @Nested
    public class KeySetView implements UnmodifiableMapKeySetTestCase
    {
        @SafeVarargs
        @Override
        public final <T> Set<T> newWith(T... elements)
        {
            Random random = new Random(CURRENT_TIME_MILLIS);
            MutableSortedMap<T, Object> result = new TreeSortedMap<>(Comparators.reverseNaturalOrder());
            for (T element : elements)
            {
                assertNull(result.put(element, random.nextDouble()));
            }
            return result.toImmutable().castToMap().keySet();
        }
    }

    @Nested
    public class ValuesCollectionView implements UnmodifiableMapValuesCollectionTestCase
    {
        @Override
        public OrderingType getOrderingType()
        {
            return OrderingType.SORTED_REVERSE_NATURAL;
        }

        @SafeVarargs
        @Override
        public final <T> Collection<T> newWith(T... elements)
        {
            return ImmutableTreeMapTest.this.newWith(elements).castToMap().values();
        }
    }
}
