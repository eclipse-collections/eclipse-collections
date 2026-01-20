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
import java.util.Objects;
import java.util.Set;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.test.map.mutable.MutableMapTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface HashingStrategyMapTestCase extends MutableMapTestCase
{
    MutableMap<Integer, Integer> newMapWithConstantStrategy();

    /**
     * Override to add hashing strategy-specific hashCode tests.
     * Tests that Entry.hashCode() and Map.hashCode() use strategy.computeHashCode().
     */
    @Override
    @Test
    default void Object_equalsAndHashCode()
    {
        MutableMapTestCase.super.Object_equalsAndHashCode();

        MutableMap<Integer, Integer> map = this.newMapWithConstantStrategy();
        map.put(3, 2);
        Set<Map.Entry<Integer, Integer>> entries = map.entrySet();

        // strategy.computeHashCode(key) ^ value.hashCode()
        int expectedCombinedHashCode = 1 ^ 2;

        assertEquals(expectedCombinedHashCode, entries.hashCode());
        assertEquals(expectedCombinedHashCode, map.hashCode());

        entries.forEach(entry -> assertEquals(expectedCombinedHashCode, entry.hashCode()));
        for (Map.Entry<Integer, Integer> entry : entries)
        {
            assertEquals(expectedCombinedHashCode, entry.hashCode());
        }

        for (Object entry : entries.toArray())
        {
            assertEquals(expectedCombinedHashCode, entry.hashCode());
        }

        for (Object entry : entries.toArray(new Object[]{}))
        {
            assertEquals(expectedCombinedHashCode, entry.hashCode());
        }

        for (Object entry : entries.toArray(new Object[]{2}))
        {
            assertEquals(expectedCombinedHashCode, entry.hashCode());
        }

        if (entries instanceof BatchIterable<?> batchIterable)
        {
            batchIterable.batchForEach(entry -> assertEquals(expectedCombinedHashCode, entry.hashCode()), 0, 1);
        }

        assertTrue(entries.contains(new ImmutableEntry<>(3, 2)));
        assertTrue(entries.contains(new ImmutableEntry<>(4, 2)));
        assertFalse(entries.contains(new ImmutableEntry<>(3, 3)));
        assertFalse(entries.contains(new ImmutableEntry<>(4, 3)));

        Set<Integer> keys = map.keySet();

        // strategy.computeHashCode(key) for single key
        int expectedKeyHashCode = 1;

        assertEquals(expectedKeyHashCode, keys.hashCode());

        assertTrue(keys.contains(3));
        assertTrue(keys.contains(4));
    }

    class ConstantHashingStrategy implements HashingStrategy<Object>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int computeHashCode(Object object)
        {
            return 1;
        }

        @Override
        public boolean equals(Object object1, Object object2)
        {
            return true;
        }
    }

    class DefaultHashingStrategy<T> implements HashingStrategy<T>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int computeHashCode(T object)
        {
            return Objects.hashCode(object);
        }

        @Override
        public boolean equals(T object1, T object2)
        {
            return Objects.equals(object1, object2);
        }
    }
}
