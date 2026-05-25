/*
 * Copyright (c) 2021 Two Sigma and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable.ordered;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.eclipse.collections.impl.map.ordered.mutable.UnmodifiableMutableOrderedMap;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.eclipse.collections.test.map.UnmodifiableMapKeySetTestCase;
import org.eclipse.collections.test.map.UnmodifiableMapValuesCollectionTestCase;
import org.eclipse.collections.test.map.mutable.UnmodifiableMutableMapIterableTestCase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class UnmodifiableMutableOrderedMapTest
        implements MutableOrderedMapTestCase, FixedSizeIterableTestCase, UnmodifiableMutableMapIterableTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    public <T> MutableOrderedMap<Object, T> newWith(T... elements)
    {
        int i = elements.length;
        MutableOrderedMap<Object, T> result = OrderedMapAdapter.adapt(new LinkedHashMap<>());
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

        MutableOrderedMap<K, V> result = OrderedMapAdapter.adapt(new LinkedHashMap<>());
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
            return UnmodifiableMutableOrderedMap.of(result).keySet();
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
            return UnmodifiableMutableOrderedMapTest.this.newWith(elements).values();
        }
    }
}
