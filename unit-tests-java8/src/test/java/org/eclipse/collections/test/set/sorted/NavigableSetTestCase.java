/*
 * Copyright (c) 2025 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.sorted;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface NavigableSetTestCase extends SortedSetTestCase
{
    @Override
    <T> NavigableSet<T> newWith(T... elements);

    @Test
    default void NavigableSet_lower_higher_floor_ceiling()
    {
        NavigableSet<Integer> set1 = this.newWith();
        assertNull(set1.lower(3));
        assertNull(set1.higher(3));
        assertNull(set1.floor(3));
        assertNull(set1.ceiling(3));

        NavigableSet<Integer> set2 = this.newWith(2, 4);
        Comparator<? super Integer> comparator = set2.comparator();
        assertTrue(this.isNaturalOrder(comparator) || this.isReverseOrder(comparator),
                "Comparator must be either null (natural order) or reverse order");

        if (this.isNaturalOrder(comparator))
        {
            // lower: greatest element strictly less than given
            assertNull(set2.lower(1));
            assertNull(set2.lower(2));
            assertEquals(Integer.valueOf(2), set2.lower(3));
            assertEquals(Integer.valueOf(2), set2.lower(4));
            assertEquals(Integer.valueOf(4), set2.lower(5));

            // higher: least element strictly greater than given
            assertEquals(Integer.valueOf(2), set2.higher(1));
            assertEquals(Integer.valueOf(4), set2.higher(2));
            assertEquals(Integer.valueOf(4), set2.higher(3));
            assertNull(set2.higher(4));
            assertNull(set2.higher(5));

            // floor: greatest element less than or equal to given
            assertNull(set2.floor(1));
            assertEquals(Integer.valueOf(2), set2.floor(2));
            assertEquals(Integer.valueOf(2), set2.floor(3));
            assertEquals(Integer.valueOf(4), set2.floor(4));
            assertEquals(Integer.valueOf(4), set2.floor(5));

            // ceiling: least element greater than or equal to given
            assertEquals(Integer.valueOf(2), set2.ceiling(1));
            assertEquals(Integer.valueOf(2), set2.ceiling(2));
            assertEquals(Integer.valueOf(4), set2.ceiling(3));
            assertEquals(Integer.valueOf(4), set2.ceiling(4));
            assertNull(set2.ceiling(5));
        }
        else
        {
            // In reverse order, {2, 4} iterates as [4, 2]
            // lower/higher/floor/ceiling use comparator ordering

            // lower: greatest element strictly less than given (in comparator order)
            assertNull(set2.lower(5));
            assertNull(set2.lower(4));
            assertEquals(Integer.valueOf(4), set2.lower(3));
            assertEquals(Integer.valueOf(4), set2.lower(2));
            assertEquals(Integer.valueOf(2), set2.lower(1));

            // higher: least element strictly greater than given (in comparator order)
            assertEquals(Integer.valueOf(4), set2.higher(5));
            assertEquals(Integer.valueOf(2), set2.higher(4));
            assertEquals(Integer.valueOf(2), set2.higher(3));
            assertNull(set2.higher(2));
            assertNull(set2.higher(1));

            // floor: greatest element less than or equal to given (in comparator order)
            assertNull(set2.floor(5));
            assertEquals(Integer.valueOf(4), set2.floor(4));
            assertEquals(Integer.valueOf(4), set2.floor(3));
            assertEquals(Integer.valueOf(2), set2.floor(2));
            assertEquals(Integer.valueOf(2), set2.floor(1));

            // ceiling: least element greater than or equal to given (in comparator order)
            assertEquals(Integer.valueOf(4), set2.ceiling(5));
            assertEquals(Integer.valueOf(4), set2.ceiling(4));
            assertEquals(Integer.valueOf(2), set2.ceiling(3));
            assertEquals(Integer.valueOf(2), set2.ceiling(2));
            assertNull(set2.ceiling(1));
        }
    }

    @Test
    default void NavigableSet_pollFirst_pollLast()
    {
        NavigableSet<Integer> set1 = this.newWith();
        assertNull(set1.pollFirst());
        assertNull(set1.pollLast());

        NavigableSet<Integer> set2 = this.newWith(2, 4);
        Comparator<? super Integer> comparator = set2.comparator();
        assertTrue(this.isNaturalOrder(comparator) || this.isReverseOrder(comparator),
                "Comparator must be either null (natural order) or reverse order");

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Integer.valueOf(2), set2.pollFirst());
            assertEquals(1, set2.size());
            assertFalse(set2.contains(2));
            assertTrue(set2.contains(4));

            assertEquals(Integer.valueOf(4), set2.pollLast());
            assertEquals(0, set2.size());

            assertNull(set2.pollFirst());
            assertNull(set2.pollLast());
        }
        else
        {
            // In reverse order, first is 4, last is 2
            assertEquals(Integer.valueOf(4), set2.pollFirst());
            assertEquals(1, set2.size());
            assertFalse(set2.contains(4));
            assertTrue(set2.contains(2));

            assertEquals(Integer.valueOf(2), set2.pollLast());
            assertEquals(0, set2.size());

            assertNull(set2.pollFirst());
            assertNull(set2.pollLast());
        }
    }

    @Test
    default void NavigableSet_descendingIterator()
    {
        NavigableSet<Integer> set1 = this.newWith();
        assertFalse(set1.descendingIterator().hasNext());

        NavigableSet<Integer> set2 = this.newWith(2, 4);
        Comparator<? super Integer> comparator = set2.comparator();
        assertTrue(this.isNaturalOrder(comparator) || this.isReverseOrder(comparator),
                "Comparator must be either null (natural order) or reverse order");

        Iterator<Integer> descendingIterator = set2.descendingIterator();
        assertTrue(descendingIterator.hasNext());

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Integer.valueOf(4), descendingIterator.next());
            assertTrue(descendingIterator.hasNext());
            assertEquals(Integer.valueOf(2), descendingIterator.next());
        }
        else
        {
            assertEquals(Integer.valueOf(2), descendingIterator.next());
            assertTrue(descendingIterator.hasNext());
            assertEquals(Integer.valueOf(4), descendingIterator.next());
        }
        assertFalse(descendingIterator.hasNext());
    }

    @Test
    default void NavigableSet_subSet_headSet_tailSet()
    {
        NavigableSet<Integer> set1 = this.newWith(1, 2, 3, 4, 5);
        Comparator<? super Integer> comparator = set1.comparator();
        assertTrue(this.isNaturalOrder(comparator) || this.isReverseOrder(comparator),
                "Comparator must be either null (natural order) or reverse order");

        if (this.isNaturalOrder(comparator))
        {
            // subSet(from, fromInclusive, to, toInclusive)
            assertIterablesEqual(this.newWith(2, 3, 4), set1.subSet(2, true, 4, true));
            assertIterablesEqual(this.newWith(3), set1.subSet(2, false, 4, false));
            assertIterablesEqual(this.newWith(2, 3), set1.subSet(2, true, 4, false));
            assertIterablesEqual(this.newWith(3, 4), set1.subSet(2, false, 4, true));
            assertIterablesEqual(this.newWith(3), set1.subSet(3, true, 3, true));
            assertIterablesEqual(this.newWith(), set1.subSet(3, false, 3, false));

            // headSet(to, inclusive)
            assertIterablesEqual(this.newWith(1, 2, 3), set1.headSet(3, true));
            assertIterablesEqual(this.newWith(1, 2), set1.headSet(3, false));
            assertIterablesEqual(this.newWith(), set1.headSet(1, false));
            assertIterablesEqual(this.newWith(1, 2, 3, 4, 5), set1.headSet(5, true));

            // tailSet(from, inclusive)
            assertIterablesEqual(this.newWith(3, 4, 5), set1.tailSet(3, true));
            assertIterablesEqual(this.newWith(4, 5), set1.tailSet(3, false));
            assertIterablesEqual(this.newWith(1, 2, 3, 4, 5), set1.tailSet(1, true));
            assertIterablesEqual(this.newWith(), set1.tailSet(5, false));
        }
        else
        {
            // In reverse order, {1,2,3,4,5} iterates as [5,4,3,2,1]
            // subSet bounds must respect comparator order: from > to in natural terms

            assertIterablesEqual(this.newWith(4, 3, 2), set1.subSet(4, true, 2, true));
            assertIterablesEqual(this.newWith(3), set1.subSet(4, false, 2, false));
            assertIterablesEqual(this.newWith(4, 3), set1.subSet(4, true, 2, false));
            assertIterablesEqual(this.newWith(3, 2), set1.subSet(4, false, 2, true));
            assertIterablesEqual(this.newWith(3), set1.subSet(3, true, 3, true));
            assertIterablesEqual(this.newWith(), set1.subSet(3, false, 3, false));

            // headSet: elements before 'to' in iteration order
            assertIterablesEqual(this.newWith(5, 4, 3), set1.headSet(3, true));
            assertIterablesEqual(this.newWith(5, 4), set1.headSet(3, false));
            assertIterablesEqual(this.newWith(), set1.headSet(5, false));
            assertIterablesEqual(this.newWith(5, 4, 3, 2, 1), set1.headSet(1, true));

            // tailSet: elements from 'from' onwards in iteration order
            assertIterablesEqual(this.newWith(3, 2, 1), set1.tailSet(3, true));
            assertIterablesEqual(this.newWith(2, 1), set1.tailSet(3, false));
            assertIterablesEqual(this.newWith(5, 4, 3, 2, 1), set1.tailSet(5, true));
            assertIterablesEqual(this.newWith(), set1.tailSet(1, false));
        }
    }

    @Test
    default void NavigableSet_descendingSet()
    {
        NavigableSet<Integer> set1 = this.newWith();
        NavigableSet<Integer> descending1 = set1.descendingSet();
        assertTrue(descending1.isEmpty());

        NavigableSet<Integer> set2 = this.newWith(2, 4);
        NavigableSet<Integer> descending2 = set2.descendingSet();
        Comparator<? super Integer> comparator = set2.comparator();
        assertTrue(this.isNaturalOrder(comparator) || this.isReverseOrder(comparator),
                "Comparator must be either null (natural order) or reverse order");

        assertEquals(2, descending2.size());
        assertTrue(descending2.contains(2));
        assertTrue(descending2.contains(4));

        if (this.isNaturalOrder(comparator))
        {
            // Original: [2, 4], descending: [4, 2]
            assertEquals(Integer.valueOf(2), set2.first());
            assertEquals(Integer.valueOf(4), set2.last());
            assertEquals(Integer.valueOf(4), descending2.first());
            assertEquals(Integer.valueOf(2), descending2.last());

            Iterator<Integer> descendingIterator = descending2.iterator();
            assertEquals(Integer.valueOf(4), descendingIterator.next());
            assertEquals(Integer.valueOf(2), descendingIterator.next());
            assertFalse(descendingIterator.hasNext());
        }
        else
        {
            // Original: [4, 2], descending: [2, 4]
            assertEquals(Integer.valueOf(4), set2.first());
            assertEquals(Integer.valueOf(2), set2.last());
            assertEquals(Integer.valueOf(2), descending2.first());
            assertEquals(Integer.valueOf(4), descending2.last());

            Iterator<Integer> descendingIterator = descending2.iterator();
            assertEquals(Integer.valueOf(2), descendingIterator.next());
            assertEquals(Integer.valueOf(4), descendingIterator.next());
            assertFalse(descendingIterator.hasNext());
        }

        // Verify view semantics: modifications reflect in both
        descending2.add(3);
        assertEquals(3, set2.size());
        assertTrue(set2.contains(3));

        set2.remove(3);
        assertEquals(2, descending2.size());
        assertFalse(descending2.contains(3));

        // Double descending returns to original order
        NavigableSet<Integer> doubleDescending = descending2.descendingSet();
        assertIterablesEqual(set2, doubleDescending);
    }
}
