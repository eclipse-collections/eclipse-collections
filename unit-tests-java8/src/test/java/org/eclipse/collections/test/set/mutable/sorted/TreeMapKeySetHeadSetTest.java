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

public class TreeMapKeySetHeadSetTest implements SortedSetTestCase
{
    @SafeVarargs
    @Override
    public final <T> SortedSet<T> newWith(T... elements)
    {
        if (elements.length == 0)
        {
            TreeMap<Integer, String> emptyTreeMap = new TreeMap<>(Comparators.reverseNaturalOrder());
            emptyTreeMap.put(Integer.MIN_VALUE, "sentinel");
            NavigableMap<Integer, String> headMap = emptyTreeMap.headMap(Integer.MIN_VALUE, false);
            @SuppressWarnings("unchecked")
            SortedSet<T> result = (SortedSet<T>) headMap.navigableKeySet();
            return result;
        }

        TreeMap<T, String> treeMap = new TreeMap<>(Comparators.reverseNaturalOrder());
        for (T element : elements)
        {
            treeMap.put(element, String.valueOf(element));
        }

        T smallest = treeMap.lastKey();

        @SuppressWarnings("unchecked")
        T sentinel = (T) createSentinel(smallest);

        treeMap.put(sentinel, "sentinel");

        NavigableMap<T, String> headMap = treeMap.headMap(sentinel, false);
        return headMap.navigableKeySet();
    }

    private Object createSentinel(Object element)
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
}
