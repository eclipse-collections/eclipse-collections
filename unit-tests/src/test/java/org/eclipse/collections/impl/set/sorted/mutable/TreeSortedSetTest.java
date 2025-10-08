/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.mutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeSortedSetTest extends AbstractSortedSetTestCase
{
    @Override
    protected <T> TreeSortedSet<T> newWith(T... elements)
    {
        return TreeSortedSet.newSetWith(elements);
    }

    @Override
    protected <T> TreeSortedSet<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return TreeSortedSet.newSetWith(comparator, elements);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedSortedSet.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedSet.class, this.newWith().asUnmodifiable());
    }

    @Test
    public void sortedSetIterableConstructor()
    {
        TreeSortedSet<Integer> sortedSetA = TreeSortedSet.newSet(Collections.reverseOrder());
        TreeSortedSet<Integer> sortedSetB = TreeSortedSet.newSet(sortedSetA.with(1).with(2, 3).with(4, 5, 6));
        Verify.assertSortedSetsEqual(sortedSetA, sortedSetB);
        assertTrue(sortedSetA.first().equals(sortedSetB.first()) && sortedSetB.first() == 6);
        Verify.assertSortedSetsEqual(sortedSetB, new TreeSortedSet<>(sortedSetB));
    }

    @Test
    public void sortedSetConstructor()
    {
        SortedSet<String> setA = new TreeSet<>(FastList.newListWith("a", "c", "b", "d"));
        Verify.assertSortedSetsEqual(setA, TreeSortedSet.newSet(setA));
        Verify.assertSortedSetsEqual(setA, new TreeSortedSet<>(setA));
    }

    @Test
    public void iterableConstructor()
    {
        LazyIterable<Integer> integerLazyIterable = FastList.newListWith(2, 4, 1, 3).asLazy();
        TreeSortedSet<Integer> sortedSet = TreeSortedSet.newSet(integerLazyIterable);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), sortedSet);
    }

    @Test
    public void serialization()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        Verify.assertPostSerializedEqualsAndHashCode(set);
    }

    @Override
    @Test
    public void detectLastIndex()
    {
        assertEquals(1, this.newWith(1, 2, 3).detectLastIndex(each -> each % 2 == 0));
        assertEquals(3, this.newWith(1, 2, 3, 4).detectLastIndex(each -> each % 2 == 0));
        assertEquals(-1, this.newWith(1, 3, 5).detectLastIndex(each -> each % 2 == 0));
    }

    @Override
    @Test
    public void reverseForEach()
    {
        MutableList<Integer> result = FastList.newList();
        this.newWith(1, 2, 3).reverseForEach(result::add);
        assertEquals(FastList.newListWith(3, 2, 1), result);
    }

    @Override
    @Test
    public void reverseForEachWithIndex()
    {
        MutableList<String> result = FastList.newList();
        this.newWith(1, 2, 3).reverseForEachWithIndex((each, index) -> result.add(each + ":" + index));
        assertEquals(FastList.newListWith("3:0", "2:1", "1:2"), result);
    }

    @Override
    @Test
    public void toReversed()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> reversed = set.toReversed();
        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), reversed.toList());

        // Test with custom comparator
        MutableSortedSet<Integer> reverseSet = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5);
        MutableSortedSet<Integer> doubleReversed = reverseSet.toReversed();
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), doubleReversed.toList());
    }

    @Test
    public void testNavigationMethods()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 3, 5, 7, 9);

        // lower
        assertNull(set.lower(1));
        assertEquals(Integer.valueOf(1), set.lower(2));
        assertEquals(Integer.valueOf(1), set.lower(3));
        assertEquals(Integer.valueOf(7), set.lower(9));

        // floor
        assertEquals(Integer.valueOf(1), set.floor(1));
        assertEquals(Integer.valueOf(1), set.floor(2));
        assertEquals(Integer.valueOf(3), set.floor(3));
        assertEquals(Integer.valueOf(9), set.floor(9));
        assertEquals(Integer.valueOf(9), set.floor(10));

        // ceiling
        assertEquals(Integer.valueOf(1), set.ceiling(0));
        assertEquals(Integer.valueOf(1), set.ceiling(1));
        assertEquals(Integer.valueOf(3), set.ceiling(2));
        assertEquals(Integer.valueOf(9), set.ceiling(9));
        assertNull(set.ceiling(10));

        // higher
        assertEquals(Integer.valueOf(1), set.higher(0));
        assertEquals(Integer.valueOf(3), set.higher(1));
        assertEquals(Integer.valueOf(3), set.higher(2));
        assertNull(set.higher(9));
    }

    @Test
    public void testPollFirstAndPollLast()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);

        assertEquals(Integer.valueOf(1), set.pollFirst());
        assertEquals(4, set.size());
        assertEquals(FastList.newListWith(2, 3, 4, 5), set.toList());

        assertEquals(Integer.valueOf(5), set.pollLast());
        assertEquals(3, set.size());
        assertEquals(FastList.newListWith(2, 3, 4), set.toList());

        // Empty set
        MutableSortedSet<Integer> emptySet = this.newWith();
        assertNull(emptySet.pollFirst());
        assertNull(emptySet.pollLast());
    }

    @Test
    public void testDescendingIterator()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        Iterator<Integer> desc = set.descendingIterator();

        MutableList<Integer> result = FastList.newList();
        desc.forEachRemaining(result::add);
        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), result);
    }

    @Test
    public void testDescendingSet()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> descending = set.descendingSet();

        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), descending.toList());
        assertEquals(5, descending.size());

        // Mutations on descending set should reflect on original
        descending.add(0);
        assertTrue(set.contains(0));
        assertEquals(6, set.size());
    }

    @Test
    public void testSubSetWithBooleans()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Inclusive both
        MutableSortedSet<Integer> sub1 = set.subSet(3, true, 7, true);
        assertEquals(FastList.newListWith(3, 4, 5, 6, 7), sub1.toList());

        // Exclusive from
        MutableSortedSet<Integer> sub2 = set.subSet(3, false, 7, true);
        assertEquals(FastList.newListWith(4, 5, 6, 7), sub2.toList());

        // Exclusive to
        MutableSortedSet<Integer> sub3 = set.subSet(3, true, 7, false);
        assertEquals(FastList.newListWith(3, 4, 5, 6), sub3.toList());

        // Exclusive both
        MutableSortedSet<Integer> sub4 = set.subSet(3, false, 7, false);
        assertEquals(FastList.newListWith(4, 5, 6), sub4.toList());
    }

    @Test
    public void testHeadSetWithBoolean()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);

        // Exclusive (default)
        MutableSortedSet<Integer> head1 = set.headSet(3, false);
        assertEquals(FastList.newListWith(1, 2), head1.toList());

        // Inclusive
        MutableSortedSet<Integer> head2 = set.headSet(3, true);
        assertEquals(FastList.newListWith(1, 2, 3), head2.toList());
    }

    @Test
    public void testTailSetWithBoolean()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);

        // Inclusive (default)
        MutableSortedSet<Integer> tail1 = set.tailSet(3, true);
        assertEquals(FastList.newListWith(3, 4, 5), tail1.toList());

        // Exclusive
        MutableSortedSet<Integer> tail2 = set.tailSet(3, false);
        assertEquals(FastList.newListWith(4, 5), tail2.toList());
    }

    @Test
    public void testDoubleToReversed()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> reversed = set.toReversed();
        MutableSortedSet<Integer> doubleReversed = reversed.toReversed();

        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), reversed.toList());
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), doubleReversed.toList());

        // Verify they're independent copies
        set.add(6);
        assertEquals(6, set.size());
        assertEquals(5, reversed.size());
        assertEquals(5, doubleReversed.size());
    }

    @Test
    public void testToReversedSubSet()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        MutableSortedSet<Integer> reversed = set.toReversed();

        // Verify reversed order
        assertEquals(FastList.newListWith(10, 9, 8, 7, 6, 5, 4, 3, 2, 1), reversed.toList());

        // SubSet on reversed set: from 8 to 4 (in reversed comparator order: 10,9,8 > 7,6,5 > 4,3,2,1)
        MutableSortedSet<Integer> sub = reversed.subSet(8, 4);
        assertEquals(FastList.newListWith(8, 7, 6, 5), sub.toList());

        // HeadSet on reversed set: elements < 5 in reversed order
        MutableSortedSet<Integer> head = reversed.headSet(5);
        assertEquals(FastList.newListWith(10, 9, 8, 7, 6), head.toList());

        // TailSet on reversed set: elements >= 5 in reversed order
        MutableSortedSet<Integer> tail = reversed.tailSet(5);
        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), tail.toList());
    }
}
