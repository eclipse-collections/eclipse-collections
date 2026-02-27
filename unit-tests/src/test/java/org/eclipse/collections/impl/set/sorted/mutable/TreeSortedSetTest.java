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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

    @Test
    public void testNavigationMethods()
    {
        TreeSortedSet<Integer> set = TreeSortedSet.newSetWith(1, 3, 5, 7, 9);
        assertEquals(Integer.valueOf(3), set.lower(5));
        assertEquals(Integer.valueOf(5), set.floor(5));
        assertEquals(Integer.valueOf(5), set.ceiling(5));
        assertEquals(Integer.valueOf(7), set.higher(5));

        assertEquals(Integer.valueOf(5), set.lower(6));
        assertEquals(Integer.valueOf(5), set.floor(6));
        assertEquals(Integer.valueOf(7), set.ceiling(6));
        assertEquals(Integer.valueOf(7), set.higher(6));

        assertNull(set.lower(1));
        assertNull(set.higher(9));
    }

    @Test
    public void testPollFirstAndPollLast()
    {
        TreeSortedSet<Integer> set = TreeSortedSet.newSetWith(1, 2, 3, 4, 5);
        assertEquals(Integer.valueOf(1), set.pollFirst());
        assertEquals(Integer.valueOf(5), set.pollLast());
        assertEquals(3, set.size());
    }

    @Test
    public void testDescendingIterator()
    {
        TreeSortedSet<Integer> set = TreeSortedSet.newSetWith(1, 2, 3);
        Iterator<Integer> iterator = set.descendingIterator();
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(1), iterator.next());
    }

    @Test
    public void testDescendingSet()
    {
        TreeSortedSet<Integer> set = TreeSortedSet.newSetWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> desc = set.descendingSet();
        assertEquals(Integer.valueOf(5), desc.first());
        assertEquals(Integer.valueOf(1), desc.last());
        List<Integer> list = new ArrayList<>();
        desc.forEach(list::add);
        assertEquals(List.of(5, 4, 3, 2, 1), list);
    }

    @Test
    public void testSubSetWithBooleans()
    {
        TreeSortedSet<Integer> set = TreeSortedSet.newSetWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> sub = set.subSet(2, true, 4, true);
        assertEquals(3, sub.size());
        assertTrue(sub.contains(2));
        assertTrue(sub.contains(3));
        assertTrue(sub.contains(4));

        MutableSortedSet<Integer> subExcl = set.subSet(2, false, 4, false);
        assertEquals(1, subExcl.size());
        assertTrue(subExcl.contains(3));
    }

    @Test
    public void testHeadSetWithBoolean()
    {
        TreeSortedSet<Integer> set = TreeSortedSet.newSetWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> head = set.headSet(3, true);
        assertEquals(3, head.size());
        MutableSortedSet<Integer> headExcl = set.headSet(3, false);
        assertEquals(2, headExcl.size());
    }

    @Test
    public void testTailSetWithBoolean()
    {
        TreeSortedSet<Integer> set = TreeSortedSet.newSetWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> tail = set.tailSet(3, true);
        assertEquals(3, tail.size());
        MutableSortedSet<Integer> tailExcl = set.tailSet(3, false);
        assertEquals(2, tailExcl.size());
    }

    @Test
    public void testToReversedSubSet()
    {
        TreeSortedSet<Integer> set = TreeSortedSet.newSetWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> reversed = set.toReversed();
        assertEquals(Integer.valueOf(5), reversed.first());
        assertEquals(Integer.valueOf(1), reversed.last());
    }
}
