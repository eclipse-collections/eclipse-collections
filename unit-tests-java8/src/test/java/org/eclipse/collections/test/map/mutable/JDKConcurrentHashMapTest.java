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
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.collections.test.map.MapKeySetTestCase;
import org.eclipse.collections.test.map.MapValuesCollectionTestCase;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class JDKConcurrentHashMapTest implements MapTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    public <T> Map<Object, T> newWith(T... elements)
    {
        Random random = new Random(CURRENT_TIME_MILLIS);
        Map<Object, T> result = new ConcurrentHashMap<>();
        for (T each : elements)
        {
            assertNull(result.put(random.nextDouble(), each));
        }
        return result;
    }

    @Override
    public <K, V> Map<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        Map<K, V> result = new ConcurrentHashMap<>();
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

    @Override
    public boolean supportsNullValues()
    {
        return false;
    }

    @Nested
    public class KeySetView implements MapKeySetTestCase
    {
        @SafeVarargs
        @Override
        public final <T> Set<T> newWith(T... elements)
        {
            Random random = new Random(CURRENT_TIME_MILLIS);
            Map<T, Object> result = new ConcurrentHashMap<>();
            for (T element : elements)
            {
                assertNull(result.put(element, random.nextDouble()));
            }
            return result.keySet();
        }
    }

    @Nested
    public class ValuesCollectionView implements MapValuesCollectionTestCase
    {
        @SafeVarargs
        @Override
        public final <T> Collection<T> newWith(T... elements)
        {
            return JDKConcurrentHashMapTest.this.newWith(elements).values();
        }
    }
}
