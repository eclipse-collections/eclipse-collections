/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.bimap.mutable;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.test.bimap.BiMapTestCase;
import org.eclipse.collections.test.map.BiMapValuesCollectionTestCase;
import org.eclipse.collections.test.map.MapKeySetTestCase;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class HashBiMapInverseTest implements MutableBiMapTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    public <T> MutableBiMap<Object, T> newWith(T... elements)
    {
        Random random = new Random(CURRENT_TIME_MILLIS);
        MutableBiMap<T, Object> result = new HashBiMap<>();
        for (T each : elements)
        {
            assertNull(result.put(each, random.nextDouble()));
        }
        return result.inverse();
    }

    @Override
    public <K, V> MutableBiMap<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        MutableBiMap<V, K> result = new HashBiMap<>();
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((V) elements[i + 1], (K) elements[i]));
        }
        return result.inverse();
    }

    @Nested
    public class KeySetView implements MapKeySetTestCase
    {
        @SafeVarargs
        @Override
        public final <T> Set<T> newWith(T... elements)
        {
            MutableBiMap<T, T> result = new HashBiMap<>();
            BiMapTestCase.populateBiMapWithSameKeyAndValue(result, elements);
            return result.inverse().keySet();
        }
    }

    @Nested
    public class ValuesCollectionView implements BiMapValuesCollectionTestCase
    {
        @Override
        public boolean allowsSerialization()
        {
            return false;
        }

        @SafeVarargs
        @Override
        public final <T> Collection<T> newWith(T... elements)
        {
            return HashBiMapInverseTest.this.newWith(elements).values();
        }
    }
}
