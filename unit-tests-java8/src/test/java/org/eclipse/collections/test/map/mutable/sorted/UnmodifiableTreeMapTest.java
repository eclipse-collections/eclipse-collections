/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable.sorted;

import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.map.sorted.mutable.UnmodifiableTreeMap;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class UnmodifiableTreeMapTest implements MutableSortedMapIterableTestCase, FixedSizeIterableTestCase
{
    @Override
    public <T> MutableSortedMap<Object, T> newWith(T... elements)
    {
        int i = elements.length;
        MutableSortedMap<Object, T> result = new TreeSortedMap<>(Comparators.reverseNaturalOrder());
        for (T each : elements)
        {
            assertNull(result.put(i, each));
            i--;
        }
        return UnmodifiableTreeMap.of(result);
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        MutableSortedMap<K, V> result = new TreeSortedMap<>(Comparators.reverseNaturalOrder());
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return UnmodifiableTreeMap.of(result);
    }

    @Override
    @Test
    public void Iterable_remove()
    {
        FixedSizeIterableTestCase.super.Iterable_remove();
    }
}
