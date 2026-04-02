/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.function.NegativeIntervalFunction;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.SynchronizedMutableList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * JUnit test for {@link SynchronizedMutableCollection}.
 */
public class SynchronizedMutableCollectionTest extends AbstractSynchronizedCollectionTestCase
{
    @Override
    protected <T> MutableCollection<T> newWith(T... littleElements)
    {
        return new SynchronizedMutableCollection<>(FastList.newListWith(littleElements));
    }

    @Override
    @Test
    public void newEmpty()
    {
        super.newEmpty();

        Verify.assertInstanceOf(SynchronizedMutableList.class, this.newWith().newEmpty());
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        assertNotEquals(this.newWith(), this.newWith());
    }

    @Override
    @Test
    public void groupBy()
    {
        RichIterable<Integer> list = this.newWith(1, 2, 3, 4, 5, 6, 7);
        Multimap<Boolean, Integer> multimap = list.groupBy(object -> IntegerPredicates.isOdd().accept(object));

        assertEquals(FastList.newListWith(1, 3, 5, 7), multimap.get(Boolean.TRUE));
        assertEquals(FastList.newListWith(2, 4, 6), multimap.get(Boolean.FALSE));
    }

    @Override
    @Test
    public void groupByEach()
    {
        RichIterable<Integer> underTest = this.newWith(1, 2, 3, 4, 5, 6, 7);
        MutableMultimap<Integer, Integer> expected = FastListMultimap.newMultimap();
        for (int i = 1; i < 8; i++)
        {
            expected.putAll(-i, Interval.fromTo(i, 7));
        }

        Multimap<Integer, Integer> actual =
                underTest.groupByEach(new NegativeIntervalFunction());
        assertEquals(expected, actual);

        Multimap<Integer, Integer> actualWithTarget =
                underTest.groupByEach(new NegativeIntervalFunction(), FastListMultimap.newMultimap());
        assertEquals(expected, actualWithTarget);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedMutableCollection.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableMutableCollection.class, this.newWith().asUnmodifiable());
    }

    @Override
    @Test
    public void partition()
    {
        MutableCollection<Integer> integers = this.newWith(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PartitionMutableCollection<Integer> result = integers.partition(IntegerPredicates.isEven());
        assertEquals(iList(-2, 0, 2, 4, 6, 8), result.getSelected());
        assertEquals(iList(-3, -1, 1, 3, 5, 7, 9), result.getRejected());
    }

    @Override
    @Test
    public void partitionWith()
    {
        MutableCollection<Integer> integers = this.newWith(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PartitionMutableCollection<Integer> result = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isEven()));
        assertEquals(iList(-2, 0, 2, 4, 6, 8), result.getSelected());
        assertEquals(iList(-3, -1, 1, 3, 5, 7, 9), result.getRejected());
    }

    @Override
    @Test
    public void with()
    {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3);
        MutableCollection<Integer> collWith = coll.with(4);
        assertSame(coll, collWith);
        assertEquals(this.newWith(1, 2, 3, 4).toList(), collWith.toList());
    }

    @Override
    @Test
    public void withAll()
    {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3);
        MutableCollection<Integer> collWith = coll.withAll(FastList.newListWith(4, 5));
        assertSame(coll, collWith);
        assertEquals(this.newWith(1, 2, 3, 4, 5).toList(), collWith.toList());
    }

    @Override
    @Test
    public void without()
    {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3);
        MutableCollection<Integer> collWithout = coll.without(2);
        assertSame(coll, collWithout);
        MutableCollection<Integer> expectedSet = this.newWith(1, 3);
        assertEquals(expectedSet.toList(), collWithout.toList());
        assertEquals(expectedSet.toList(), collWithout.without(4).toList());
    }

    @Override
    @Test
    public void withoutAll()
    {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3, 4, 5);
        MutableCollection<Integer> collWithout = coll.withoutAll(FastList.newListWith(2, 4));
        assertSame(coll, collWithout);
        MutableCollection<Integer> expectedSet = this.newWith(1, 3, 5);
        assertEquals(expectedSet.toList(), collWithout.toList());
        assertEquals(expectedSet.toList(), collWithout.withoutAll(FastList.newListWith(2, 4)).toList());
    }
}
