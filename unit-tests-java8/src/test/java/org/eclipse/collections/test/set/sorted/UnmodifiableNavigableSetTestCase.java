/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.sorted;

import java.util.Comparator;
import java.util.NavigableSet;

import org.eclipse.collections.test.FixedSizeCollectionTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface UnmodifiableNavigableSetTestCase extends FixedSizeCollectionTestCase, NavigableSetTestCase
{
    @Override
    @Test
    default void Iterable_remove()
    {
        NavigableSetTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void NavigableSet_pollFirst_pollLast()
    {
        NavigableSet<Integer> set = this.newWith(2, 4);
        assertThrows(UnsupportedOperationException.class, () -> set.pollFirst());
        assertThrows(UnsupportedOperationException.class, () -> set.pollLast());
        assertEquals(2, set.size());
        assertTrue(set.contains(2));
        assertTrue(set.contains(4));
    }

    @Override
    @Test
    default void NavigableSet_subSet_headSet_tailSet()
    {
        NavigableSet<Integer> set = this.newWith(2, 4, 6, 8, 10);
        Comparator<? super Integer> comparator = set.comparator();

        if (this.isNaturalOrder(comparator))
        {
            NavigableSet<Integer> subSet = set.subSet(4, true, 8, true);
            assertEquals(3, subSet.size());
            assertTrue(subSet.contains(4));
            assertTrue(subSet.contains(6));
            assertTrue(subSet.contains(8));
            assertThrows(UnsupportedOperationException.class, () -> subSet.add(5));
            assertThrows(UnsupportedOperationException.class, () -> subSet.remove(6));
            assertEquals(3, subSet.size());
            assertFalse(subSet.contains(5));
            assertTrue(subSet.contains(6));

            NavigableSet<Integer> headSet = set.headSet(6, true);
            assertEquals(3, headSet.size());
            assertTrue(headSet.contains(2));
            assertTrue(headSet.contains(4));
            assertTrue(headSet.contains(6));
            assertThrows(UnsupportedOperationException.class, () -> headSet.add(1));
            assertEquals(3, headSet.size());
            assertFalse(headSet.contains(1));

            NavigableSet<Integer> tailSet = set.tailSet(6, true);
            assertEquals(3, tailSet.size());
            assertTrue(tailSet.contains(6));
            assertTrue(tailSet.contains(8));
            assertTrue(tailSet.contains(10));
            assertThrows(UnsupportedOperationException.class, () -> tailSet.add(12));
            assertEquals(3, tailSet.size());
            assertFalse(tailSet.contains(12));
        }
        if (this.isReverseOrder(comparator))
        {
            NavigableSet<Integer> subSet = set.subSet(8, true, 4, true);
            assertEquals(3, subSet.size());
            assertTrue(subSet.contains(8));
            assertTrue(subSet.contains(6));
            assertTrue(subSet.contains(4));
            assertThrows(UnsupportedOperationException.class, () -> subSet.add(5));
            assertThrows(UnsupportedOperationException.class, () -> subSet.remove(6));
            assertEquals(3, subSet.size());
            assertFalse(subSet.contains(5));
            assertTrue(subSet.contains(6));

            NavigableSet<Integer> headSet = set.headSet(6, true);
            assertEquals(3, headSet.size());
            assertTrue(headSet.contains(10));
            assertTrue(headSet.contains(8));
            assertTrue(headSet.contains(6));
            assertThrows(UnsupportedOperationException.class, () -> headSet.add(12));
            assertEquals(3, headSet.size());
            assertFalse(headSet.contains(12));

            NavigableSet<Integer> tailSet = set.tailSet(6, true);
            assertEquals(3, tailSet.size());
            assertTrue(tailSet.contains(6));
            assertTrue(tailSet.contains(4));
            assertTrue(tailSet.contains(2));
            assertThrows(UnsupportedOperationException.class, () -> tailSet.add(1));
            assertEquals(3, tailSet.size());
            assertFalse(tailSet.contains(1));
        }

        assertEquals(5, set.size());
    }

    @Override
    @Test
    default void NavigableSet_descendingSet()
    {
        NavigableSet<Integer> set = this.newWith(2, 4);
        NavigableSet<Integer> descending = set.descendingSet();
        assertEquals(2, descending.size());
        assertTrue(descending.contains(2));
        assertTrue(descending.contains(4));
        assertThrows(UnsupportedOperationException.class, () -> descending.add(3));
        assertThrows(UnsupportedOperationException.class, () -> descending.remove(2));
        assertEquals(2, descending.size());
        assertFalse(descending.contains(3));
        assertTrue(descending.contains(2));
        assertEquals(2, set.size());
    }
}
