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

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.SynchronizedRichIterable;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * JUnit test for {@link SynchronizedSortedBag}.
 */
public class SynchronizedSortedBagTest extends AbstractMutableSortedBagTestCase
{
    @Override
    protected <T> MutableSortedBag<T> newWith(T... littleElements)
    {
        return new SynchronizedSortedBag<>(TreeBag.newBagWith(littleElements));
    }

    @SafeVarargs
    @Override
    protected final <T> MutableSortedBag<T> newWithOccurrences(ObjectIntPair<T>... elementsWithOccurrences)
    {
        return super.newWithOccurrences(elementsWithOccurrences).asSynchronized();
    }

    @Override
    protected <T> MutableSortedBag<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return new SynchronizedSortedBag<>(TreeBag.newBagWith(comparator, elements));
    }

    @Override
    @Test
    public void asSynchronized()
    {
        MutableSortedBag<Object> synchronizedBag = this.newWith();
        assertSame(synchronizedBag, synchronizedBag.asSynchronized());
    }

    @Override
    @Test
    public void topOccurrences()
    {
        super.topOccurrences();

        MutableSortedBag<String> mutable = TreeBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        MutableSortedBag<String> strings = mutable.asSynchronized();
        MutableList<ObjectIntPair<String>> top5 = strings.topOccurrences(5);
        Verify.assertSize(5, top5);
        assertEquals("ten", top5.getFirst().getOne());
        assertEquals(10, top5.getFirst().getTwo());
        assertEquals("six", top5.getLast().getOne());
        assertEquals(6, top5.getLast().getTwo());
    }

    @Override
    @Test
    public void bottomOccurrences()
    {
        super.bottomOccurrences();

        MutableSortedBag<String> mutable = TreeBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        MutableSortedBag<String> strings = mutable.asSynchronized();
        MutableList<ObjectIntPair<String>> bottom5 = strings.bottomOccurrences(5);
        Verify.assertSize(5, bottom5);
        assertEquals("one", bottom5.getFirst().getOne());
        assertEquals(1, bottom5.getFirst().getTwo());
        assertEquals("five", bottom5.getLast().getOne());
        assertEquals(5, bottom5.getLast().getTwo());
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

        Bag<Integer> bag2 = this.newWith(3, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 4, 5, 7);
        assertEquals(
                Lists.mutable.with(6, 5, 8, 5, 6, 8),
                bag2.collectWithOccurrences((each, index) -> each + index));
    }

    @Override
    @Test
    public void distinctView()
    {
        Comparator<String> comparator = Collections.reverseOrder();
        MutableSortedBag<String> bag = this.newWith(comparator, "1", "2", "2", "3", "3", "3", "3", "4", "5", "5", "6");
        RichIterable<String> expected = bag.toSortedSet(comparator);
        RichIterable<String> actual = bag.distinctView();
        Verify.assertInstanceOf(SynchronizedRichIterable.class, actual);
        // not using Assert.assertEquals as actual is not a Set
        Verify.assertIterablesEqual(expected, actual);
    }
}
