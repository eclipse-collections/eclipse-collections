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

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.test.set.sorted.SortedSetTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TreeSetSubSetTest implements SortedSetTestCase
{
    @SafeVarargs
    @Override
    public final <T> SortedSet<T> newWith(T... elements)
    {
        if (elements.length == 0)
        {
            TreeSet<Integer> emptyTreeSet = new TreeSet<>(Comparators.reverseNaturalOrder());
            emptyTreeSet.add(Integer.MIN_VALUE);
            @SuppressWarnings("unchecked")
            SortedSet<T> result = (SortedSet<T>) emptyTreeSet.subSet(Integer.MAX_VALUE, false, Integer.MIN_VALUE, false);
            return result;
        }

        TreeSet<T> treeSet = new TreeSet<>(Comparators.reverseNaturalOrder());
        Collections.addAll(treeSet, elements);

        T largest = treeSet.first();
        T smallest = treeSet.last();

        @SuppressWarnings("unchecked")
        T sentinelHigh = (T) createSentinelHigher(largest);
        @SuppressWarnings("unchecked")
        T sentinelLow = (T) createSentinelLower(smallest);

        treeSet.add(sentinelHigh);
        treeSet.add(sentinelLow);

        return treeSet.subSet(sentinelHigh, false, sentinelLow, false);
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
    @Test
    public void Object_equalsAndHashCode()
    {
        org.eclipse.collections.impl.test.Verify.assertPostSerializedEqualsAndHashCode(this.newWith(3, 2, 1));

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
    public void Collection_add()
    {
        SortedSet<Integer> set = this.newWith(3, 2, 1);
        assertFalse(set.add(3));
        assertFalse(set.add(2));
        assertFalse(set.add(1));
        assertEquals(this.newWith(1, 2, 3), set);
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
