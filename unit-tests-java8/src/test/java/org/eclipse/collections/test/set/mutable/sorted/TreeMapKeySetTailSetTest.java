/*
 * Copyright (c) 2025 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable.sorted;

import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.test.set.sorted.SortedSetTestCase;

public class TreeMapKeySetTailSetTest implements SortedSetTestCase
{
    @SafeVarargs
    @Override
    public final <T> SortedSet<T> newWith(T... elements)
    {
        if (elements.length == 0)
        {
            TreeMap<Integer, String> emptyTreeMap = new TreeMap<>(Comparators.reverseNaturalOrder());
            emptyTreeMap.put(0, "sentinel");
            NavigableMap<Integer, String> subMap = emptyTreeMap.subMap(0, false, 0, false);
            @SuppressWarnings("unchecked")
            SortedSet<T> result = (SortedSet<T>) subMap.navigableKeySet();
            return result;
        }

        TreeMap<T, String> treeMap = new TreeMap<>(Comparators.reverseNaturalOrder());
        for (T element : elements)
        {
            treeMap.put(element, String.valueOf(element));
        }

        T largest = treeMap.firstKey();

        @SuppressWarnings("unchecked")
        T sentinelHigh = (T) createSentinelHigher(largest);

        treeMap.put(sentinelHigh, "sentinelHigh");

        NavigableMap<T, String> tailMap = treeMap.tailMap(sentinelHigh, false);
        return tailMap.navigableKeySet();
    }

    private Object createSentinelHigher(Object element)
    {
        if (element instanceof Integer)
        {
            return Integer.MAX_VALUE;
        }
        if (element instanceof String)
        {
            return "\uffff";
        }
        throw new UnsupportedOperationException("Sentinel creation not implemented for type: " + element.getClass());
    }

    @Override
    public boolean allowsDuplicates()
    {
        return false;
    }

    @Override
    public boolean allowsAdd()
    {
        return false;
    }

    @Override
    public boolean allowsSerialization()
    {
        return false;
    }
}
