/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bimap.mutable;

import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.junit.jupiter.api.Test;

public class HashBiMapInverseTest extends AbstractMutableBiMapTestCase
{
    @Override
    public MutableBiMap<Integer, Character> classUnderTest()
    {
        HashBiMap<Character, Integer> biMap = HashBiMap.newMap();
        biMap.put(null, 1);
        biMap.put('b', null);
        biMap.put('c', 3);
        return biMap.inverse();
    }

    @Override
    public MutableBiMap<Integer, Character> getEmptyMap()
    {
        return HashBiMap.<Character, Integer>newMap().inverse();
    }

    @Override
    protected <K, V> MutableBiMap<K, V> newMap()
    {
        return HashBiMap.<V, K>newMap().inverse();
    }

    @Override
    protected <K, V> MutableBiMap<K, V> newMapWithKeyValue(K key, V value)
    {
        return HashBiMap.newWithKeysValues(value, key).inverse();
    }

    @Override
    protected <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return HashBiMap.newWithKeysValues(value1, key1, value2, key2).inverse();
    }

    @Override
    protected <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return HashBiMap.newWithKeysValues(value1, key1, value2, key2, value3, key3).inverse();
    }

    @Override
    protected <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return HashBiMap.newWithKeysValues(value1, key1, value2, key2, value3, key3, value4, key4).inverse();
    }

    @Override
    @Test
    public void keyPreservation()
    {
        Holder<String> key = new Holder<>("key");

        Holder<String> duplicateKey1 = new Holder<>("key");
        MutableBiMap<Integer, Holder<String>> map1 = HashBiMap.newWithKeysValues(key, 1, duplicateKey1, 2).inverse();
        Verify.assertSize(1, map1);
        Verify.assertContainsKeyValue(2, key, map1);

        Holder<String> duplicateKey2 = new Holder<>("key");
        MutableBiMap<Integer, Holder<String>> map2 = HashBiMap.newWithKeysValues(key, 1, duplicateKey1, 2, duplicateKey2, 3).inverse();
        Verify.assertSize(1, map2);
        Verify.assertContainsKeyValue(3, key, map2);
    }
}
