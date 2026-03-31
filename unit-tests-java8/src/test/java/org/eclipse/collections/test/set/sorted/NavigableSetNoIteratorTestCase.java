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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link NavigableSet} implementations that should not delegate to
 * {@code iterator()} or {@code descendingIterator()}.
 *
 * <p>Asserts that calling {@code iterator()} and {@code descendingIterator()} throws
 * {@link AssertionError}. Also asserts that JDK methods which delegate to {@code iterator()}
 * ({@code toString()}, {@code hashCode()}, {@code equals()}, {@code clear()}) throw.
 *
 * <p>Overrides parent test methods that use {@code assertIterablesEqual} (which triggers
 * {@code equals()}) with versions that verify behavior using {@code contains()} and
 * {@code size()} instead.
 */
public interface NavigableSetNoIteratorTestCase extends NavigableSetTestCase
{
    @Override
    @Test
    default void Iterable_next()
    {
        assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator());
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator());
    }

    @Override
    @Test
    default void Iterable_hasNext()
    {
        assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator());
    }

    @Override
    @Test
    default void NavigableSet_descendingIterator()
    {
        assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).descendingIterator());
    }

    /**
     * {@link java.util.AbstractCollection#toString()} delegates to iterator().
     */
    @Override
    @Test
    default void Iterable_toString()
    {
        assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).toString());
    }

    /**
     * {@link java.util.AbstractSet#hashCode()} delegates to iterator().
     * {@link java.util.AbstractSet#equals(Object)} delegates to iterator() via containsAll().
     */
    @Override
    @Test
    default void Object_equalsAndHashCode()
    {
        assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).hashCode());
        assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).equals(this.newWith(3, 2, 1)));
    }

    /**
     * Overrides parent which uses assertIterablesEqual. JDK TreeSet's
     * {@code clear()} does not delegate to iterator(), but equals() on the
     * NoIterator set argument does.
     */
    @Override
    @Test
    default void Collection_clear()
    {
        NavigableSet<Integer> set = this.newWith(3, 2, 1);
        set.clear();
        assertEquals(0, set.size());
    }

    /**
     * Overrides parent which uses assertIterablesEqual (triggers equals/iterator).
     * Verifies add() works without iteration.
     */
    @Override
    @Test
    default void Collection_add()
    {
        NavigableSet<Integer> collection = this.newWith(3, 2, 1);
        assertTrue(collection.add(4));
        assertEquals(4, collection.size());
        assertTrue(collection.contains(4));
        assertFalse(collection.add(4));
        assertEquals(4, collection.size());
    }

    /**
     * Overrides parent which uses assertIterablesEqual (triggers equals/iterator).
     * Verifies remove() works without iteration.
     */
    @Override
    @Test
    default void Collection_remove_removeAll()
    {
        NavigableSet<Integer> collection = this.newWith(3, 2, 1);
        assertTrue(collection.remove(2));
        assertEquals(2, collection.size());
        assertFalse(collection.contains(2));
        assertTrue(collection.contains(1));
        assertTrue(collection.contains(3));
    }

    /**
     * Overrides parent which uses assertIterablesEqual (triggers equals/iterator).
     * Verifies comparator() works without iteration.
     */
    @Override
    @Test
    default void SortedSet_comparator()
    {
        NavigableSet<Integer> set = this.newWith(3, 2, 1);
        Comparator<? super Integer> comparator = set.comparator();
        assertTrue(this.isNaturalOrder(comparator) || this.isReverseOrder(comparator));
    }

    /**
     * Overrides parent which uses assertIterablesEqual (triggers equals/iterator).
     * Verifies subSet/headSet/tailSet work without iteration.
     */
    @Override
    @Test
    default void SortedSet_subSet_headSet_tailSet()
    {
        NavigableSet<Integer> set = this.newWith(2, 4, 6, 8);
        Comparator<? super Integer> comparator = set.comparator();

        if (this.isNaturalOrder(comparator))
        {
            NavigableSet<Integer> subSet = (NavigableSet<Integer>) set.subSet(2, 8);
            assertEquals(3, subSet.size());
            assertTrue(subSet.contains(2));
            assertTrue(subSet.contains(4));
            assertTrue(subSet.contains(6));
            assertFalse(subSet.contains(8));
        }
        if (this.isReverseOrder(comparator))
        {
            NavigableSet<Integer> subSet = (NavigableSet<Integer>) set.subSet(8, 2);
            assertEquals(3, subSet.size());
            assertTrue(subSet.contains(8));
            assertTrue(subSet.contains(6));
            assertTrue(subSet.contains(4));
            assertFalse(subSet.contains(2));
        }
    }

    /**
     * Overrides parent which uses assertIterablesEqual (triggers equals/iterator).
     * Verifies pollFirst/pollLast work without iteration.
     */
    @Override
    @Test
    default void NavigableSet_pollFirst_pollLast()
    {
        NavigableSet<Integer> set = this.newWith(2, 4);
        Comparator<? super Integer> comparator = set.comparator();

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Integer.valueOf(2), set.pollFirst());
            assertEquals(1, set.size());
            assertTrue(set.contains(4));
            assertEquals(Integer.valueOf(4), set.pollLast());
            assertEquals(0, set.size());
            assertNull(set.pollFirst());
            assertNull(set.pollLast());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Integer.valueOf(4), set.pollFirst());
            assertEquals(1, set.size());
            assertTrue(set.contains(2));
            assertEquals(Integer.valueOf(2), set.pollLast());
            assertEquals(0, set.size());
            assertNull(set.pollFirst());
            assertNull(set.pollLast());
        }
    }

    /**
     * Overrides parent which uses assertIterablesEqual (triggers equals/iterator).
     * Verifies NavigableSet subSet/headSet/tailSet work without iteration.
     */
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

            NavigableSet<Integer> headSet = set.headSet(6, true);
            assertEquals(3, headSet.size());
            assertTrue(headSet.contains(2));
            assertTrue(headSet.contains(4));
            assertTrue(headSet.contains(6));

            NavigableSet<Integer> tailSet = set.tailSet(6, true);
            assertEquals(3, tailSet.size());
            assertTrue(tailSet.contains(6));
            assertTrue(tailSet.contains(8));
            assertTrue(tailSet.contains(10));
        }
        if (this.isReverseOrder(comparator))
        {
            NavigableSet<Integer> subSet = set.subSet(8, true, 4, true);
            assertEquals(3, subSet.size());
            assertTrue(subSet.contains(8));
            assertTrue(subSet.contains(6));
            assertTrue(subSet.contains(4));

            NavigableSet<Integer> headSet = set.headSet(6, true);
            assertEquals(3, headSet.size());
            assertTrue(headSet.contains(10));
            assertTrue(headSet.contains(8));
            assertTrue(headSet.contains(6));

            NavigableSet<Integer> tailSet = set.tailSet(6, true);
            assertEquals(3, tailSet.size());
            assertTrue(tailSet.contains(6));
            assertTrue(tailSet.contains(4));
            assertTrue(tailSet.contains(2));
        }
    }

    /**
     * Overrides parent which uses assertIterablesEqual/Verify.assertIterablesEqual
     * (triggers equals/iterator). Verifies descendingSet() works without iteration.
     */
    @Override
    @Test
    default void NavigableSet_descendingSet()
    {
        NavigableSet<Integer> set = this.newWith(2, 4);
        NavigableSet<Integer> descending = set.descendingSet();
        assertEquals(2, descending.size());
        assertTrue(descending.contains(2));
        assertTrue(descending.contains(4));
    }
}
