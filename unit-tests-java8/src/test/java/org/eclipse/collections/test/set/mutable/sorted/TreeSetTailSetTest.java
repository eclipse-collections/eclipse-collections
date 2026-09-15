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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeSetTailSetTest implements SortedSetTestCase
{
    @SafeVarargs
    @Override
    public final <T> SortedSet<T> newWith(T... elements)
    {
        if (elements.length == 0)
        {
            TreeSet<Integer> emptyTreeSet = new TreeSet<>(Comparators.reverseNaturalOrder());
            emptyTreeSet.add(0);
            @SuppressWarnings("unchecked")
            SortedSet<T> result = (SortedSet<T>) emptyTreeSet.tailSet(Integer.MIN_VALUE, false);
            return result;
        }

        TreeSet<T> treeSet = new TreeSet<>(Comparators.reverseNaturalOrder());
        Collections.addAll(treeSet, elements);

        T largest = treeSet.first();

        @SuppressWarnings("unchecked")
        T sentinelHigh = (T) createSentinelHigher(largest);

        treeSet.add(sentinelHigh);

        return treeSet.tailSet(sentinelHigh, false);
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
    @Test
    public void Collection_add()
    {
        SortedSet<Integer> set = this.newWith(4, 2, 1);
        assertTrue(set.add(5));
        assertFalse(set.add(4));
        assertTrue(set.add(3));
        assertFalse(set.add(2));
        assertFalse(set.add(1));
        assertTrue(set.add(0));
        assertEquals(this.newWith(0, 1, 2, 3, 4, 5), set);

        assertThrows(NullPointerException.class, () -> set.add(null));
    }
}
