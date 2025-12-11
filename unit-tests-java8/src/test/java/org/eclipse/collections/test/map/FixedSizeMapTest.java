/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map;

import java.util.Random;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.UnsortedMapIterable;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class FixedSizeMapTest implements UnsortedMapIterableTestCase, FixedSizeIterableTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    @Test
    public void Iterable_remove()
    {
        FixedSizeIterableTestCase.super.Iterable_remove();
    }

    @Override
    public <T> UnsortedMapIterable<Object, T> newWith(T... elements)
    {
        Random random = new Random(CURRENT_TIME_MILLIS);

        if (elements.length == 0)
        {
            return Maps.fixedSize.of();
        }
        if (elements.length == 1)
        {
            return Maps.fixedSize.of(random.nextDouble(), elements[0]);
        }
        if (elements.length == 2)
        {
            return Maps.fixedSize.of(random.nextDouble(), elements[0], random.nextDouble(), elements[1]);
        }
        if (elements.length == 3)
        {
            return Maps.fixedSize.of(random.nextDouble(), elements[0], random.nextDouble(), elements[1], random.nextDouble(), elements[2]);
        }

        MutableMap<Object, T> result = new UnifiedMap<>();
        for (T each : elements)
        {
            assertNull(result.put(random.nextDouble(), each));
        }
        return result.toImmutable();
    }

    @Override
    public <K, V> MutableMap<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        if (elements.length == 0)
        {
            return Maps.fixedSize.of();
        }
        if (elements.length == 2)
        {
            return Maps.fixedSize.of((K) elements[0], (V) elements[1]);
        }
        if (elements.length == 4)
        {
            return Maps.fixedSize.of((K) elements[0], (V) elements[1], (K) elements[2], (V) elements[3]);
        }
        if (elements.length == 6)
        {
            return Maps.fixedSize.of((K) elements[0], (V) elements[1], (K) elements[2], (V) elements[3], (K) elements[4], (V) elements[5]);
        }

        MutableMap<K, V> result = new UnifiedMap<>();
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return result;
    }
}
