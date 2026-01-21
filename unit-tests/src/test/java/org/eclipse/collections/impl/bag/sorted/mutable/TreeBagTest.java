/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.sorted.mutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link TreeBag}.
 *
 * @since 4.2
 */
public class TreeBagTest extends AbstractMutableSortedBagTestCase
{
    @Override
    protected <T> MutableSortedBag<T> newWith(T... littleElements)
    {
        return TreeBag.newBagWith(littleElements);
    }

    @Override
    protected <T> MutableSortedBag<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return TreeBag.newBagWith(comparator, elements);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedSortedBag.class, this.newWith().asSynchronized());
    }

    @Test
    public void sortedBagIterableConstructor()
    {
        TreeBag<Integer> sortedBagA = TreeBag.newBag(Collections.reverseOrder());
        TreeBag<Integer> sortedBagB = TreeBag.newBag(sortedBagA.with(1).with(2, 3).with(4, 5, 6).with(1, 1, 1, 1));
        Verify.assertSortedBagsEqual(sortedBagA, sortedBagB);
        assertTrue(sortedBagA.getFirst().equals(sortedBagB.getFirst()) && sortedBagB.getFirst() == 6);
        Verify.assertSortedBagsEqual(sortedBagB, TreeBag.newBag(sortedBagB));
    }

    @Override
    @Test
    public void selectDuplicates()
    {
        TreeBag<Integer> sortedBagA = TreeBag.newBag(Collections.reverseOrder());
        TreeBag<Integer> sortedBagB = TreeBag.newBag(sortedBagA.with(1).with(2, 3).with(4, 5, 6).with(1, 1, 1, 1));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 1, 1, 1, 1), sortedBagB.selectDuplicates());
    }

    @Override
    @Test
    public void collectWithIndex()
    {
        TreeBag<Integer> sortedBagA = TreeBag.newBag(Collections.reverseOrder());
        TreeBag<Integer> sortedBagB = TreeBag.newBag(sortedBagA.with(1).with(2, 3).with(4, 5, 6).with(1, 1, 1, 1));

        assertEquals(Lists.mutable.of(
                PrimitiveTuples.pair((Integer) 6, 0),
                PrimitiveTuples.pair((Integer) 5, 1),
                PrimitiveTuples.pair((Integer) 4, 2),
                PrimitiveTuples.pair((Integer) 3, 3),
                PrimitiveTuples.pair((Integer) 2, 4),
                PrimitiveTuples.pair((Integer) 1, 5),
                PrimitiveTuples.pair((Integer) 1, 6),
                PrimitiveTuples.pair((Integer) 1, 7),
                PrimitiveTuples.pair((Integer) 1, 8),
                PrimitiveTuples.pair((Integer) 1, 9)), sortedBagB.collectWithIndex(PrimitiveTuples::pair));
    }

    @Override
    @Test
    public void flatCollectWith()
    {
        TreeBag<String> sortedBagA = TreeBag.newBag(Collections.reverseOrder());
        TreeBag<String> sortedBagB = TreeBag.newBag(sortedBagA.with("1").with("2", "3").with("1"));
        String s = "Alex";
        assertEquals(Lists.mutable.of("3", s, "2", s, "1", s, "1", s),
                sortedBagB.flatCollectWith(Lists.mutable::of, s));
    }

    @Test
    public void sortedBagConstructor()
    {
        MutableSortedBag<String> bagA = TreeBag.newBag(FastList.newListWith("a", "c", "b", "d"));
        Verify.assertSortedBagsEqual(bagA, TreeBag.newBag(bagA));
        Verify.assertSortedBagsEqual(bagA, TreeBag.newBag(bagA));
    }

    @Test
    public void iterableConstructor()
    {
        LazyIterable<Integer> integerLazyIterable = FastList.newListWith(2, 4, 1, 3).asLazy();
        TreeBag<Integer> sortedBag = TreeBag.newBag(Comparators.reverseNaturalOrder(), integerLazyIterable);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4), sortedBag);
    }

    @Override
    @Test
    public void serialization()
    {
        MutableSortedBag<Integer> bag = this.newWith(1, 2, 3, 4, 5);
        Verify.assertPostSerializedEqualsAndHashCode(bag);
    }

    @Override
    @Test
    public void collectWithOccurrences()
    {
        Bag<Integer> bag1 = this.newWith(3, 3, 3, 2, 2, 1);
        Bag<ObjectIntPair<Integer>> actual1 =
                bag1.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
        assertEquals(
                Bags.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1)),
                actual1);
        assertEquals(
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(1), 1),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(3), 3)),
                bag1.collectWithOccurrences(PrimitiveTuples::pair));

        Set<ObjectIntPair<Integer>> actual2 =
                bag1.collectWithOccurrences(PrimitiveTuples::pair, Sets.mutable.empty());
        assertEquals(
                Sets.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1)),
                actual2);

        Bag<Integer> bag2 = this.newWith(Comparator.reverseOrder(), 3, 3, 3, 2, 2, 1);
        assertEquals(
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1)),
                bag2.collectWithOccurrences(PrimitiveTuples::pair));

        Bag<Integer> bag3 = this.newWith(3, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 4, 5, 7);
        assertEquals(
                Lists.mutable.with(6, 5, 8, 5, 6, 8),
                bag3.collectWithOccurrences((each, index) -> each + index));
    }
}
