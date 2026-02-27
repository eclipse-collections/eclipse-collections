/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.strategy.mutable;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMapTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrentHashMapWithHashingStrategyTest extends ConcurrentHashMapTest
{
    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMap()
    {
        return ConcurrentHashMapWithHashingStrategy.newMap(HashingStrategies.defaultStrategy());
    }

    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMapWithKeyValue(K key, V value)
    {
        return ConcurrentHashMapWithHashingStrategy.<K, V>newMap(HashingStrategies.defaultStrategy()).withKeyValue(key, value);
    }

    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return ConcurrentHashMapWithHashingStrategy.<K, V>newMap(HashingStrategies.defaultStrategy()).withKeyValue(key1, value1).withKeyValue(key2, value2);
    }

    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return ConcurrentHashMapWithHashingStrategy.<K, V>newMap(HashingStrategies.defaultStrategy())
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2)
                .withKeyValue(key3, value3);
    }

    @Override
    public <K, V> ConcurrentMutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return ConcurrentHashMapWithHashingStrategy.<K, V>newMap(HashingStrategies.defaultStrategy())
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2)
                .withKeyValue(key3, value3)
                .withKeyValue(key4, value4);
    }

    //Using the StringWrapper guarantees a new fresh instance, even when the string is identical
    static class StringWrapper
    {
        private String s;

        StringWrapper(String s)
        {
            this.s = s;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof StringWrapper)
            {
                return s.equals(((StringWrapper) obj).s);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return s.hashCode();
        }
    }

    static class CaseInsensitiveStringStrategy implements HashingStrategy<String>
    {
        @Override
        public int computeHashCode(String object)
        {
            return object.toLowerCase().hashCode();
        }

        @Override
        public boolean equals(String object1, String object2)
        {
            return object1.equalsIgnoreCase(object2);
        }
    }

    @Test
    public void testIdentityMap()
    {
        StringWrapper instance1 = new StringWrapper("abc");
        StringWrapper instance2 = new StringWrapper("abc");
        assertEquals(instance1, instance1);

        final HashingStrategy<Object> strategy = HashingStrategies.identityStrategy();
        assertFalse(strategy.equals(instance1, instance2));

        ConcurrentHashMapWithHashingStrategy<StringWrapper, String> map = ConcurrentHashMapWithHashingStrategy.<StringWrapper, String>newMap(strategy).withKeyValue(instance1, "b");
        assertFalse(map.containsKey(instance2));
        assertNull(map.putIfAbsent(instance2, "c"));
        assertTrue(map.containsKey(instance2));
        assertEquals("b", map.get(instance1));
    }

    @Test
    public void testCaseInsensitivity()
    {
        ConcurrentHashMapWithHashingStrategy<String, String> map = ConcurrentHashMapWithHashingStrategy.<String, String>newMap(new CaseInsensitiveStringStrategy()).withKeyValue("A", "b");
        assertEquals("b", map.get("a"));
        assertEquals("b", map.putIfAbsent("a", "x"));
        map.putIfAbsent("x", "z");
        assertEquals("z", map.get("X"));
        assertTrue(map.containsKey("X"));
        map.computeIfAbsent("r", (a) -> a + "_");
        assertEquals("r_", map.get("R"));
        map.computeIfPresent("R", (a, b) -> a + "_" + b);
        assertEquals("R_r_", map.get("R"));
        assertEquals("R_r_", map.get("r"));
    }
}
