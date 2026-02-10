/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.factory;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FixedSizeMapFactoryTest
{
    @Test
    public void create0()
    {
        MutableMap<String, String> map = Maps.fixedSize.of();
        Verify.assertEmpty(map);
    }

    @Test
    public void create1()
    {
        MutableMap<String, String> map1 = Maps.fixedSize.of("key1", "value1");
        Verify.assertSize(1, map1);
        Verify.assertContainsKeyValue("key1", "value1", map1);

        MutableMap<String, String> map2 = Maps.fixedSize.of(null, null);
        Verify.assertSize(1, map2);
        Verify.assertContainsKeyValue(null, null, map2);

        MutableMap<String, String> map3 = Maps.fixedSize.of(null, "value1");
        Verify.assertSize(1, map3);
        Verify.assertContainsKeyValue(null, "value1", map3);
    }

    @Test
    public void create2()
    {
        MutableMap<String, String> map1 = Maps.fixedSize.of("key1", "value1", "key2", "value2");
        Verify.assertSize(2, map1);
        Verify.assertContainsAllKeyValues(map1, "key1", "value1", "key2", "value2");

        MutableMap<String, String> map2 = Maps.fixedSize.of(null, "value1", "key2", "value2");
        Verify.assertSize(2, map2);
        Verify.assertContainsKeyValue(null, "value1", map2);
        Verify.assertContainsKeyValue("key2", "value2", map2);
    }

    @Test
    public void create3()
    {
        MutableMap<String, String> map1 = Maps.fixedSize.of("key1", "value1", "key2", "value2", "key3", "value3");
        Verify.assertSize(3, map1);
        Verify.assertContainsAllKeyValues(map1, "key1", "value1", "key2", "value2", "key3", "value3");

        MutableMap<String, String> map2 = Maps.fixedSize.of("key1", "value1", "key2", null, null, "value3");
        Verify.assertContainsKeyValue("key2", null, map2);
        Verify.assertContainsKeyValue(null, "value3", map2);
    }

    @Test
    public void createWithDuplicates()
    {
        MutableMap<String, String> map1 = Maps.fixedSize.of("k1", "v1", "k1", "v2");
        Verify.assertSize(1, map1);
        Verify.assertContainsKey("k1", map1);
        Verify.assertContainsKeyValue("k1", "v2", map1);

        MutableMap<String, String> map2 = Maps.fixedSize.of("k1", "v1", "k1", "v2", "k1", "v3");
        Verify.assertSize(1, map2);
        Verify.assertContainsKey("k1", map2);
        Verify.assertContainsKeyValue("k1", "v3", map2);

        MutableMap<String, String> map3 = Maps.fixedSize.of("k2", "v1", "k3", "v2", "k2", "v3");
        Verify.assertSize(2, map3);
        Verify.assertContainsKey("k2", map3);
        Verify.assertContainsKey("k3", map3);
        Verify.assertContainsKeyValue("k2", "v3", map3);

        MutableMap<String, String> map4 = Maps.fixedSize.of("k3", "v1", "k4", "v2", "k4", "v3");
        Verify.assertSize(2, map4);
        Verify.assertContainsKey("k3", map4);
        Verify.assertContainsKey("k4", map4);
        Verify.assertContainsKeyValue("k4", "v3", map4);
    }

    @Test
    public void keyPreservation()
    {
        Holder<String> key = new Holder<>("key");

        Holder<String> duplicateKey1 = new Holder<>("key");
        MutableMap<Holder<String>, Integer> map1 = Maps.fixedSize.of(key, 1, duplicateKey1, 2);
        Verify.assertSize(1, map1);
        Verify.assertContainsKeyValue(key, 2, map1);
        assertSame(key, map1.keysView().getFirst());

        Holder<String> duplicateKey2 = new Holder<>("key");
        MutableMap<Holder<String>, Integer> map2 = Maps.fixedSize.of(key, 1, duplicateKey1, 2, duplicateKey2, 3);
        Verify.assertSize(1, map2);
        Verify.assertContainsKeyValue(key, 3, map2);
        assertSame(key, map1.keysView().getFirst());

        Holder<String> duplicateKey3 = new Holder<>("key");
        MutableMap<Holder<String>, Integer> map3 = Maps.fixedSize.of(key, 1, new Holder<>("not a dupe"), 2, duplicateKey3, 3);
        Verify.assertSize(2, map3);
        Verify.assertContainsAllKeyValues(map3, key, 3, new Holder<>("not a dupe"), 2);
        assertSame(key, map3.keysView().detect(key::equals));
    }

    @Test
    public void testWithMap()

    {
        MutableMap<String, String> map = Maps.fixedSize.of("key1", "value1", "key2", "value2", "key3", "value3");
        assertThrows(UnsupportedOperationException.class, () -> map.withMap(Maps.fixedSize.of("key4", "value4")));
    }
}
