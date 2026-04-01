/*
 * Copyright (c) 2025 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.immutable.sorted;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface ImmutableNavigableSetTestCase
{
    <T> ImmutableSortedSet<T> newWith(T... elements);

    default boolean isNaturalOrder(Comparator<?> comparator)
    {
        return comparator == null || comparator == Comparator.naturalOrder() || comparator == Comparators.naturalOrder();
    }

    default boolean isReverseOrder(Comparator<?> comparator)
    {
        return comparator == Comparator.reverseOrder() || comparator == Comparators.reverseNaturalOrder();
    }

    @Test
    default void NavigableSet_lower_higher_floor_ceiling()
    {
        ImmutableSortedSet<Integer> set1 = this.newWith();
        assertNull(set1.lower(3));
        assertNull(set1.higher(3));
        assertNull(set1.floor(3));
        assertNull(set1.ceiling(3));

        ImmutableSortedSet<Integer> set2 = this.newWith(2, 4);
        Comparator<? super Integer> comparator = set2.comparator();

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
        if (this.isReverseOrder(comparator))
        {
            // lower: greatest element strictly less than given (reverse semantics)
            assertNull(set2.lower(5));
            assertNull(set2.lower(4));
            assertEquals(Integer.valueOf(4), set2.lower(3));
            assertEquals(Integer.valueOf(4), set2.lower(2));
            assertEquals(Integer.valueOf(2), set2.lower(1));

            // higher: least element strictly greater than given (reverse semantics)
            assertEquals(Integer.valueOf(4), set2.higher(5));
            assertEquals(Integer.valueOf(2), set2.higher(4));
            assertEquals(Integer.valueOf(2), set2.higher(3));
            assertNull(set2.higher(2));
            assertNull(set2.higher(1));

            // floor: greatest element less than or equal to given (reverse semantics)
            assertNull(set2.floor(5));
            assertEquals(Integer.valueOf(4), set2.floor(4));
            assertEquals(Integer.valueOf(4), set2.floor(3));
            assertEquals(Integer.valueOf(2), set2.floor(2));
            assertEquals(Integer.valueOf(2), set2.floor(1));

            // ceiling: least element greater than or equal to given (reverse semantics)
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
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().castToNavigableSet().pollFirst());
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().castToNavigableSet().pollLast());
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(2, 4).castToNavigableSet().pollFirst());
        assertThrows(UnsupportedOperationException.class, () -> this.newWith(2, 4).castToNavigableSet().pollLast());
    }

    @Test
    default void NavigableSet_descendingIterator()
    {
        ImmutableSortedSet<Integer> set1 = this.newWith();
        assertFalse(set1.descendingIterator().hasNext());

        ImmutableSortedSet<Integer> set2 = this.newWith(2, 4);
        Comparator<? super Integer> comparator = set2.comparator();

        Iterator<Integer> descendingIterator = set2.descendingIterator();
        assertTrue(descendingIterator.hasNext());
        assertTrue(descendingIterator.hasNext());

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Integer.valueOf(4), descendingIterator.next());
            assertTrue(descendingIterator.hasNext());
            assertTrue(descendingIterator.hasNext());
            assertEquals(Integer.valueOf(2), descendingIterator.next());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Integer.valueOf(2), descendingIterator.next());
            assertTrue(descendingIterator.hasNext());
            assertTrue(descendingIterator.hasNext());
            assertEquals(Integer.valueOf(4), descendingIterator.next());
        }
        assertFalse(descendingIterator.hasNext());
        assertFalse(descendingIterator.hasNext());
        assertThrows(NoSuchElementException.class, descendingIterator::next);
    }

    @Test
    default void NavigableSet_subSet_headSet_tailSet()
    {
        ImmutableSortedSet<Integer> set1 = this.newWith(2, 4, 6, 8, 10);
        Comparator<? super Integer> comparator = set1.comparator();

        if (this.isNaturalOrder(comparator))
        {
            // subSet(from, fromInclusive, to, toInclusive)
            assertIterablesEqual(this.newWith(4, 6, 8), set1.subSet(4, true, 8, true));
            assertIterablesEqual(this.newWith(6), set1.subSet(4, false, 8, false));
            assertIterablesEqual(this.newWith(4, 6), set1.subSet(4, true, 8, false));
            assertIterablesEqual(this.newWith(6, 8), set1.subSet(4, false, 8, true));
            assertIterablesEqual(this.newWith(6), set1.subSet(6, true, 6, true));
            assertIterablesEqual(this.newWith(), set1.subSet(6, false, 6, false));
            assertIterablesEqual(this.newWith(4, 6), set1.subSet(3, true, 7, true));
            assertIterablesEqual(this.newWith(4, 6), set1.subSet(3, false, 7, false));

            // headSet(to, inclusive) - test boundaries: 1, 2, 3, 9, 10, 11
            assertIterablesEqual(this.newWith(), set1.headSet(1, true));
            assertIterablesEqual(this.newWith(), set1.headSet(1, false));
            assertIterablesEqual(this.newWith(2), set1.headSet(2, true));
            assertIterablesEqual(this.newWith(), set1.headSet(2, false));
            assertIterablesEqual(this.newWith(2), set1.headSet(3, true));
            assertIterablesEqual(this.newWith(2), set1.headSet(3, false));
            assertIterablesEqual(this.newWith(2, 4, 6, 8), set1.headSet(9, true));
            assertIterablesEqual(this.newWith(2, 4, 6, 8), set1.headSet(9, false));
            assertIterablesEqual(this.newWith(2, 4, 6, 8, 10), set1.headSet(10, true));
            assertIterablesEqual(this.newWith(2, 4, 6, 8), set1.headSet(10, false));
            assertIterablesEqual(this.newWith(2, 4, 6, 8, 10), set1.headSet(11, true));
            assertIterablesEqual(this.newWith(2, 4, 6, 8, 10), set1.headSet(11, false));

            // tailSet(from, inclusive) - test boundaries: 1, 2, 3, 9, 10, 11
            assertIterablesEqual(this.newWith(2, 4, 6, 8, 10), set1.tailSet(1, true));
            assertIterablesEqual(this.newWith(2, 4, 6, 8, 10), set1.tailSet(1, false));
            assertIterablesEqual(this.newWith(2, 4, 6, 8, 10), set1.tailSet(2, true));
            assertIterablesEqual(this.newWith(4, 6, 8, 10), set1.tailSet(2, false));
            assertIterablesEqual(this.newWith(4, 6, 8, 10), set1.tailSet(3, true));
            assertIterablesEqual(this.newWith(4, 6, 8, 10), set1.tailSet(3, false));
            assertIterablesEqual(this.newWith(10), set1.tailSet(9, true));
            assertIterablesEqual(this.newWith(10), set1.tailSet(9, false));
            assertIterablesEqual(this.newWith(10), set1.tailSet(10, true));
            assertIterablesEqual(this.newWith(), set1.tailSet(10, false));
            assertIterablesEqual(this.newWith(), set1.tailSet(11, true));
            assertIterablesEqual(this.newWith(), set1.tailSet(11, false));

            // subSet with crossed indices (from > to) should throw
            assertThrows(IllegalArgumentException.class, () -> set1.subSet(8, true, 4, true));
            assertThrows(IllegalArgumentException.class, () -> set1.subSet(8, false, 4, false));
        }
        if (this.isReverseOrder(comparator))
        {
            // subSet(from, fromInclusive, to, toInclusive) with reverse semantics
            assertIterablesEqual(this.newWith(8, 6, 4), set1.subSet(8, true, 4, true));
            assertIterablesEqual(this.newWith(6), set1.subSet(8, false, 4, false));
            assertIterablesEqual(this.newWith(8, 6), set1.subSet(8, true, 4, false));
            assertIterablesEqual(this.newWith(6, 4), set1.subSet(8, false, 4, true));
            assertIterablesEqual(this.newWith(6), set1.subSet(6, true, 6, true));
            assertIterablesEqual(this.newWith(), set1.subSet(6, false, 6, false));
            assertIterablesEqual(this.newWith(6, 4), set1.subSet(7, true, 3, true));
            assertIterablesEqual(this.newWith(6, 4), set1.subSet(7, false, 3, false));

            // headSet(to, inclusive) with reverse semantics
            assertIterablesEqual(this.newWith(10, 8, 6, 4, 2), set1.headSet(1, true));
            assertIterablesEqual(this.newWith(10, 8, 6, 4, 2), set1.headSet(1, false));
            assertIterablesEqual(this.newWith(10, 8, 6, 4, 2), set1.headSet(2, true));
            assertIterablesEqual(this.newWith(10, 8, 6, 4), set1.headSet(2, false));
            assertIterablesEqual(this.newWith(10, 8, 6, 4), set1.headSet(3, true));
            assertIterablesEqual(this.newWith(10, 8, 6, 4), set1.headSet(3, false));
            assertIterablesEqual(this.newWith(10), set1.headSet(9, true));
            assertIterablesEqual(this.newWith(10), set1.headSet(9, false));
            assertIterablesEqual(this.newWith(10), set1.headSet(10, true));
            assertIterablesEqual(this.newWith(), set1.headSet(10, false));
            assertIterablesEqual(this.newWith(), set1.headSet(11, true));
            assertIterablesEqual(this.newWith(), set1.headSet(11, false));

            // tailSet(from, inclusive) with reverse semantics
            assertIterablesEqual(this.newWith(), set1.tailSet(1, true));
            assertIterablesEqual(this.newWith(), set1.tailSet(1, false));
            assertIterablesEqual(this.newWith(2), set1.tailSet(2, true));
            assertIterablesEqual(this.newWith(), set1.tailSet(2, false));
            assertIterablesEqual(this.newWith(2), set1.tailSet(3, true));
            assertIterablesEqual(this.newWith(2), set1.tailSet(3, false));
            assertIterablesEqual(this.newWith(8, 6, 4, 2), set1.tailSet(9, true));
            assertIterablesEqual(this.newWith(8, 6, 4, 2), set1.tailSet(9, false));
            assertIterablesEqual(this.newWith(10, 8, 6, 4, 2), set1.tailSet(10, true));
            assertIterablesEqual(this.newWith(8, 6, 4, 2), set1.tailSet(10, false));
            assertIterablesEqual(this.newWith(10, 8, 6, 4, 2), set1.tailSet(11, true));
            assertIterablesEqual(this.newWith(10, 8, 6, 4, 2), set1.tailSet(11, false));

            // subSet with crossed indices (from < to in reverse-order semantics) should throw
            assertThrows(IllegalArgumentException.class, () -> set1.subSet(4, true, 8, true));
            assertThrows(IllegalArgumentException.class, () -> set1.subSet(4, false, 8, false));
        }
    }

    @Test
    default void NavigableSet_descendingSet()
    {
        ImmutableSortedSet<Integer> set1 = this.newWith();
        assertTrue(set1.descendingSet().isEmpty());

        ImmutableSortedSet<Integer> set2 = this.newWith(2, 4);
        ImmutableSortedSet<Integer> descending2 = set2.descendingSet();

        // Descending set iterates in the reverse order of the original
        assertEquals(set2.toList().reverseThis(), descending2.toList());

        // Double descending returns to original order
        assertEquals(set2.toList(), descending2.descendingSet().toList());
    }
}
