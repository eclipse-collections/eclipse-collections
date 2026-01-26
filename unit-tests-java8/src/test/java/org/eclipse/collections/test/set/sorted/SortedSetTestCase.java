/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.sorted;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.test.CollectionTestCase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface SortedSetTestCase extends CollectionTestCase
{
    @Override
    <T> SortedSet<T> newWith(T... elements);

    default boolean isNaturalOrder(Comparator<?> comparator)
    {
        return comparator == null || comparator == Comparator.naturalOrder() || comparator == Comparators.naturalOrder();
    }

    default boolean isReverseOrder(Comparator<?> comparator)
    {
        return comparator == Comparator.reverseOrder() || comparator == Comparators.reverseNaturalOrder();
    }

    @Test
    default void SortedSet_comparator_isOneOfExpected()
    {
        SortedSet<Integer> set = this.newWith(1, 2, 3);
        Comparator<? super Integer> comparator = set.comparator();

        assertThat(this.isNaturalOrder(comparator) || this.isReverseOrder(comparator))
                .as("Comparator must be either null (natural order) or reverse order, but was: %s", comparator)
                .isTrue();
    }

    @Override
    @Test
    default void Iterable_next()
    {
        SortedSet<Integer> iterable = this.newWith(2, 4, 6);
        Comparator<? super Integer> comparator = iterable.comparator();

        Iterator<Integer> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Integer.valueOf(2), iterator.next());
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals(Integer.valueOf(4), iterator.next());
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals(Integer.valueOf(6), iterator.next());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Integer.valueOf(6), iterator.next());
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals(Integer.valueOf(4), iterator.next());
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals(Integer.valueOf(2), iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        if (!this.allowsRemove())
        {
            Iterable<Integer> iterable = this.newWith(2, 4, 6);
            Iterator<Integer> iterator = iterable.iterator();
            iterator.next();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
            return;
        }

        SortedSet<Integer> iterable = this.newWith(2, 4, 6);
        Comparator<? super Integer> comparator = iterable.comparator();

        Iterator<Integer> iterator = iterable.iterator();
        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Integer.valueOf(2), iterator.next());
            iterator.remove();
            assertIterablesEqual(this.newWith(4, 6), iterable);
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Integer.valueOf(6), iterator.next());
            iterator.remove();
            assertIterablesEqual(this.newWith(4, 2), iterable);
        }
    }

    @Override
    @Test
    default void Collection_add()
    {
        CollectionTestCase.super.Collection_add();

        if (!this.allowsAdd())
        {
            return;
        }

        Collection<Integer> collection = this.newWith(1, 2, 3);
        assertFalse(collection.add(3));
    }

    @Override
    @Test
    default void Collection_size()
    {
        assertThat(this.newWith(3, 2, 1)).hasSize(3);
        assertThat(this.newWith()).hasSize(0);
    }

    @Test
    default void SortedSet_comparator()
    {
        SortedSet<Integer> set = this.newWith(2, 4, 6);
        Comparator<? super Integer> comparator = set.comparator();

        if (this.isNaturalOrder(comparator))
        {
            assertIterablesEqual(this.newWith(2, 4, 6), set);
        }
        if (this.isReverseOrder(comparator))
        {
            assertIterablesEqual(this.newWith(6, 4, 2), set);
        }
    }

    @Test
    default void SortedSet_subSet_headSet_tailSet()
    {
        SortedSet<Integer> set = this.newWith(2, 4, 6, 8);
        Comparator<? super Integer> comparator = set.comparator();

        if (this.isNaturalOrder(comparator))
        {
            // subSet(fromInclusive, toExclusive)
            assertIterablesEqual(this.newWith(2, 4, 6, 8), set.subSet(1, 9));
            assertIterablesEqual(this.newWith(2, 4, 6), set.subSet(2, 8));
            assertIterablesEqual(this.newWith(4, 6), set.subSet(3, 7));
            assertIterablesEqual(this.newWith(4), set.subSet(4, 6));
            assertIterablesEqual(this.newWith(), set.subSet(5, 5));
            assertIterablesEqual(this.newWith(), set.subSet(6, 6));
            assertThrows(IllegalArgumentException.class, () -> set.subSet(7, 3));

            // headSet(toExclusive)
            assertIterablesEqual(this.newWith(), set.headSet(1));
            assertIterablesEqual(this.newWith(), set.headSet(2));
            assertIterablesEqual(this.newWith(2, 4), set.headSet(5));
            assertIterablesEqual(this.newWith(2, 4), set.headSet(6));
            assertIterablesEqual(this.newWith(2, 4, 6, 8), set.headSet(9));

            // tailSet(fromInclusive)
            assertIterablesEqual(this.newWith(2, 4, 6, 8), set.tailSet(1));
            assertIterablesEqual(this.newWith(2, 4, 6, 8), set.tailSet(2));
            assertIterablesEqual(this.newWith(6, 8), set.tailSet(5));
            assertIterablesEqual(this.newWith(6, 8), set.tailSet(6));
            assertIterablesEqual(this.newWith(), set.tailSet(9));
        }

        if (this.isReverseOrder(comparator))
        {
            // subSet(fromInclusive, toExclusive)
            assertIterablesEqual(this.newWith(8, 6, 4, 2), set.subSet(9, 1));
            assertIterablesEqual(this.newWith(8, 6, 4), set.subSet(8, 2));
            assertIterablesEqual(this.newWith(6, 4), set.subSet(7, 3));
            assertIterablesEqual(this.newWith(6), set.subSet(6, 4));
            assertIterablesEqual(this.newWith(), set.subSet(5, 5));
            assertIterablesEqual(this.newWith(), set.subSet(6, 6));
            assertThrows(IllegalArgumentException.class, () -> set.subSet(3, 7));

            // headSet(toExclusive)
            assertIterablesEqual(this.newWith(), set.headSet(9));
            assertIterablesEqual(this.newWith(), set.headSet(8));
            assertIterablesEqual(this.newWith(8, 6), set.headSet(5));
            assertIterablesEqual(this.newWith(8, 6), set.headSet(4));
            assertIterablesEqual(this.newWith(8, 6, 4, 2), set.headSet(1));

            // tailSet(fromInclusive)
            assertIterablesEqual(this.newWith(8, 6, 4, 2), set.tailSet(9));
            assertIterablesEqual(this.newWith(8, 6, 4, 2), set.tailSet(8));
            assertIterablesEqual(this.newWith(4, 2), set.tailSet(5));
            assertIterablesEqual(this.newWith(4, 2), set.tailSet(4));
            assertIterablesEqual(this.newWith(), set.tailSet(1));
        }
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        SortedSet<Integer> set = this.newWith(3, 2, 1);
        Comparator<? super Integer> comparator = set.comparator();

        if (this.isNaturalOrder(comparator))
        {
            assertEquals("[1, 2, 3]", set.toString());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals("[3, 2, 1]", set.toString());
        }
    }

    @Test
    default void SortedSet_first_last()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().first());
        assertThrows(NoSuchElementException.class, () -> this.newWith().last());

        SortedSet<Integer> set1 = this.newWith(42);
        assertEquals(Integer.valueOf(42), set1.first());
        assertEquals(Integer.valueOf(42), set1.last());

        SortedSet<Integer> set3 = this.newWith(2, 4, 6);
        Comparator<? super Integer> comparator = set3.comparator();

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Integer.valueOf(2), set3.first());
            assertEquals(Integer.valueOf(6), set3.last());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Integer.valueOf(6), set3.first());
            assertEquals(Integer.valueOf(2), set3.last());
        }
    }
}
