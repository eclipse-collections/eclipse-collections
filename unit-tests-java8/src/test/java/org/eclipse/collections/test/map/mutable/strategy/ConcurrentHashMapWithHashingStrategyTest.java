/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable.strategy;

import java.util.Map;
import java.util.Random;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.map.strategy.mutable.ConcurrentHashMapWithHashingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ConcurrentHashMapWithHashingStrategyTest implements HashingStrategyMapTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    public <T> MutableMap<Object, T> newWith(T... elements)
    {
        Random random = new Random(CURRENT_TIME_MILLIS);
        MutableMap<Object, T> result = ConcurrentHashMapWithHashingStrategy.newMap(new DefaultHashingStrategy<>());
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

        MutableMap<K, V> result = ConcurrentHashMapWithHashingStrategy.newMap(new DefaultHashingStrategy<>());
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return result;
    }

    @Override
    public MutableMap<Integer, Integer> newMapWithConstantStrategy()
    {
        return ConcurrentHashMapWithHashingStrategy.newMap(new ConstantHashingStrategy());
    }

    @Override
    public boolean supportsNullKeys()
    {
        return false;
    }

    @Override
    @Test
    public void Map_entrySet_setValue()
    {
        MutableMapIterable<String, Integer> map = this.newWithKeysValues("3", 3, "2", 2, "1", 1);
        map.entrySet().forEach(each -> assertThrows(UnsupportedOperationException.class, () -> each.setValue(each.getValue() + 1)));
        assertIterableEquals(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);
    }

    @Override
    @Test
    public void MutableMapIterable_entrySet_setValue()
    {
        this.Map_entrySet_setValue();
    }

    /**
     * TODO: Implement a custom replaceAll() using the JDK's retry-loop approach with replace().
     * ConcurrentHashMapWithHashingStrategy's Entry.setValue() throws UnsupportedOperationException,
     * so the default replaceAll (which uses setValue internally) cannot work.
     */
    @Override
    @Test
    public void Map_replaceAll()
    {
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.replaceAll((k, v) -> v + k));
        assertIterableEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), (Iterable<?>) map);
    }
}
