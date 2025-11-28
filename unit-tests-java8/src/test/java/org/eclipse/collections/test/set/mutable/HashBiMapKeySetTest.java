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

import java.util.Set;

import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.test.set.SetTestCase;

public class HashBiMapKeySetTest implements SetTestCase
{
    @Override
    public boolean allowsAdd()
    {
        return false;
    }

    @SafeVarargs
    @Override
    public final <T> Set<T> newWith(T... elements)
    {
        MutableBiMap<T, T> result = new HashBiMap<>();
        for (T element : elements)
        {
            if (result.containsKey(element))
            {
                throw new IllegalStateException();
            }
            result.put(element, element);
        }
        return result.keySet();
    }
}
