/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.immutable;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class ImmutableMapFactoryTest
{
    @Test
    public void create0()
    {
        ImmutableMap<String, String> map = Maps.immutable.of();
        Verify.assertEmpty(map);
    }

    @Test
    public void create1()
    {
        ImmutableMap<String, String> map1 = Maps.immutable.of("key1", "value1");
        Verify.assertSize(1, map1);
        Verify.assertContainsKeyValue("key1", "value1", map1);

        ImmutableMap<String, String> map2 = Maps.immutable.of(null, null);
        Verify.assertSize(1, map2);
        Verify.assertContainsKeyValue(null, null, map2);

        ImmutableMap<String, String> map3 = Maps.immutable.of(null, "value1");
        Verify.assertSize(1, map3);
        Verify.assertContainsKeyValue(null, "value1", map3);
    }

    @Test
    public void create2()
    {
        ImmutableMap<String, String> map1 = Maps.immutable.of("key1", "value1", "key2", "value2");
        Verify.assertSize(2, map1);
        Verify.assertContainsAllKeyValues(map1, "key1", "value1", "key2", "value2");

        ImmutableMap<String, String> map2 = Maps.immutable.of(null, "value1", "key2", "value2");
        Verify.assertContainsKeyValue(null, "value1", map2);
        Verify.assertContainsKeyValue("key2", "value2", map2);
    }

    @Test
    public void create3()
    {
        ImmutableMap<String, String> map1 = Maps.immutable.of("key1", "value1", "key2", "value2", "key3", "value3");
        Verify.assertSize(3, map1);
        Verify.assertContainsAllKeyValues(map1, "key1", "value1", "key2", "value2", "key3", "value3");

        ImmutableMap<String, String> map2 = Maps.immutable.of("key1", "value1", "key2", null, null, "value3");
        Verify.assertContainsKeyValue("key2", null, map2);
        Verify.assertContainsKeyValue(null, "value3", map2);
    }

    @Test
    public void createWithDuplicates()
    {
        ImmutableMap<String, String> map1 = Maps.immutable.of("k1", "v1", "k1", "v2");
        Verify.assertSize(1, map1);
        Verify.assertContainsKey("k1", map1);
        Verify.assertContainsKeyValue("k1", "v2", map1);

        ImmutableMap<String, String> map2 = Maps.immutable.of("k1", "v1", "k1", "v2", "k1", "v3");
        Verify.assertSize(1, map2);
        Verify.assertContainsKey("k1", map2);
        Verify.assertContainsKeyValue("k1", "v3", map2);

        ImmutableMap<String, String> map3 = Maps.immutable.of("k2", "v1", "k3", "v2", "k2", "v3");
        Verify.assertSize(2, map3);
        Verify.assertContainsKey("k2", map3);
        Verify.assertContainsKey("k3", map3);
        Verify.assertContainsKeyValue("k2", "v3", map3);

        ImmutableMap<String, String> map4 = Maps.immutable.of("k3", "v1", "k4", "v2", "k4", "v3");
        Verify.assertSize(2, map4);
        Verify.assertContainsKey("k3", map4);
        Verify.assertContainsKey("k4", map4);
        Verify.assertContainsKeyValue("k4", "v3", map4);
    }

    @Test
    public void keyPreservation()
    {
        Holder<String> key = new Holder<>("key");
        Holder<String> duplicateKey = new Holder<>("key");
        ImmutableMap<Holder<String>, Integer> map4 = ImmutableMapFactoryImpl.INSTANCE.of(key, 1, new Holder<>("still not a dupe"), 2, new Holder<>("me neither"), 3, duplicateKey, 4);
        Verify.assertSize(3, map4);
        Verify.assertContainsAllKeyValues(map4, key, 4, new Holder<>("still not a dupe"), 2, new Holder<>("me neither"), 3);
        assertSame(key, map4.keysView().detect(key::equals));
    }
}
