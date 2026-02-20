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
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.test.Verify.assertNotSerializable;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TreeMapKeySetSubSetTest implements SortedSetTestCase
{
    @SafeVarargs
    @Override
    public final <T> SortedSet<T> newWith(T... elements)
    {
        if (elements.length == 0)
        {
            TreeMap<Integer, String> emptyTreeMap = new TreeMap<>(Comparators.reverseNaturalOrder());
            emptyTreeMap.put(Integer.MIN_VALUE, "sentinel");
            NavigableMap<Integer, String> subMap = emptyTreeMap.subMap(Integer.MIN_VALUE, false, Integer.MIN_VALUE, false);
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
        T smallest = treeMap.lastKey();

        @SuppressWarnings("unchecked")
        T sentinelHigh = (T) createSentinelHigher(largest);
        @SuppressWarnings("unchecked")
        T sentinelLow = (T) createSentinelLower(smallest);

        treeMap.put(sentinelHigh, "sentinelHigh");
        treeMap.put(sentinelLow, "sentinelLow");

        NavigableMap<T, String> subMap = treeMap.subMap(sentinelHigh, false, sentinelLow, false);
        return subMap.navigableKeySet();
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

    private Object createSentinelLower(Object element)
    {
        if (element instanceof Integer)
        {
            return Integer.MIN_VALUE;
        }
        if (element instanceof String)
        {
            return "\0";
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
    public boolean allowsRemove()
    {
        return true;
    }

    @Override
    @Test
    public void Object_PostSerializedEqualsAndHashCode()
    {
        assertNotSerializable(this.newWith(1, 2, 3));
    }

    @Override
    @Test
    public void Object_equalsAndHashCode()
    {
        assertEquals(this.newWith(4, 3, 2, 1), this.newWith(4, 3, 2, 1));
        assertEquals(this.newWith(3, 2, 1), this.newWith(3, 2, 1));
    }

    @Override
    @Test
    public void Iterable_toString()
    {
        assertEquals("[3, 2, 1]", this.newWith(3, 2, 1).toString());
    }

    @Override
    @Test
    public void Collection_size()
    {
        assertEquals(3, this.newWith(3, 2, 1).size());
        assertEquals(0, this.newWith().size());
        assertEquals(1, this.newWith(1).size());
        assertEquals(2, this.newWith(1, 2).size());
        assertEquals(5, this.newWith(5, 4, 3, 2, 1).size());
    }
}
