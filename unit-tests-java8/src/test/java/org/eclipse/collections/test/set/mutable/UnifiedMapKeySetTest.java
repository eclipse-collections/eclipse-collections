/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable;

import java.util.Random;
import java.util.Set;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.test.set.SetTestCase;

import static org.junit.jupiter.api.Assertions.assertNull;

// TODO MapIterable.keySet() should return SetIterable, and use SetIterableTestCase here

public class UnifiedMapKeySetTest implements SetTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    public boolean allowsAdd()
    {
        return false;
    }

    @SafeVarargs
    @Override
    public final <T> Set<T> newWith(T... elements)
    {
        Random random = new Random(CURRENT_TIME_MILLIS);

        MutableMap<T, Double> result = new UnifiedMap<>();
        for (T element : elements)
        {
            assertNull(result.put(element, random.nextDouble()));
        }
        return result.keySet();
    }
}
