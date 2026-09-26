/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.ordered.mutable;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.collections.impl.JolMemoryTestUtil;
import org.junit.jupiter.api.Test;

/**
 * Compares the memory overhead of {@link OrderedHashMap} with {@link LinkedHashMap} at various sizes.
 * The overhead is the memory retained by the map excluding its keys and values.
 */
public class OrderedHashMapMemoryTest
{
    private static void assertOverheadEquals(long expected, long expectedCOH, Map<Integer, Integer> map, int size)
    {
        Object[] keys = new Object[size];
        for (int i = 0; i < size; i++)
        {
            Integer each = i;
            keys[i] = each;
            map.put(each, each);
        }
        JolMemoryTestUtil.assertGraphMemoryExcludingEquals(expected, expectedCOH, map, keys);
    }

    @Test
    public void size0()
    {
        // 32 bytes for the map, plus 40 bytes for the two unallocated arrays that are shared by all empty maps.
        assertOverheadEquals(72L, 72L, new OrderedHashMap<>(), 0);
        JolMemoryTestUtil.assertClassMemoryEquals(32L, 32L, new OrderedHashMap<>());
        assertOverheadEquals(64L, 56L, new LinkedHashMap<>(), 0);
    }

    @Test
    public void size1()
    {
        assertOverheadEquals(136L, 136L, new OrderedHashMap<>(), 1);
        assertOverheadEquals(184L, 168L, new LinkedHashMap<>(), 1);
    }

    @Test
    public void size10()
    {
        assertOverheadEquals(208L, 208L, new OrderedHashMap<>(), 10);
        assertOverheadEquals(544L, 456L, new LinkedHashMap<>(), 10);
    }

    @Test
    public void size100()
    {
        assertOverheadEquals(2_448L, 2_448L, new OrderedHashMap<>(), 100);
        assertOverheadEquals(5_104L, 4_296L, new LinkedHashMap<>(), 100);
    }

    @Test
    public void size1_000()
    {
        assertOverheadEquals(19_176L, 19_176L, new OrderedHashMap<>(), 1_000);
        assertOverheadEquals(48_272L, 40_264L, new LinkedHashMap<>(), 1_000);
    }

    @Test
    public void size10_000()
    {
        assertOverheadEquals(152_976L, 152_976L, new OrderedHashMap<>(), 10_000);
        assertOverheadEquals(465_616L, 385_608L, new LinkedHashMap<>(), 10_000);
    }
}
