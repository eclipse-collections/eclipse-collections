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

    // The expected values are for Java 21 and later. A LinkedHashMap instance is 8 bytes smaller before Java 21.
    private static void assertLinkedHashMapOverheadEquals(long expected, long expectedCOH, int size)
    {
        long bytesSmallerBeforeJava21 = Runtime.version().feature() < 21 ? 8L : 0L;
        assertOverheadEquals(expected - bytesSmallerBeforeJava21, expectedCOH - bytesSmallerBeforeJava21, new LinkedHashMap<>(), size);
    }

    @Test
    public void size0()
    {
        assertOverheadEquals(32L, 32L, new OrderedHashMap<>(), 0);
        assertLinkedHashMapOverheadEquals(64L, 56L, 0);
    }

    @Test
    public void size1()
    {
        assertOverheadEquals(136L, 136L, new OrderedHashMap<>(), 1);
        assertLinkedHashMapOverheadEquals(184L, 168L, 1);
    }

    @Test
    public void size10()
    {
        assertOverheadEquals(208L, 208L, new OrderedHashMap<>(), 10);
        assertLinkedHashMapOverheadEquals(544L, 456L, 10);
    }

    @Test
    public void size100()
    {
        assertOverheadEquals(2_448L, 2_448L, new OrderedHashMap<>(), 100);
        assertLinkedHashMapOverheadEquals(5_104L, 4_296L, 100);
    }

    @Test
    public void size1_000()
    {
        assertOverheadEquals(19_176L, 19_176L, new OrderedHashMap<>(), 1_000);
        assertLinkedHashMapOverheadEquals(48_272L, 40_264L, 1_000);
    }

    @Test
    public void size10_000()
    {
        assertOverheadEquals(152_976L, 152_976L, new OrderedHashMap<>(), 10_000);
        assertLinkedHashMapOverheadEquals(465_616L, 385_608L, 10_000);
    }
}
