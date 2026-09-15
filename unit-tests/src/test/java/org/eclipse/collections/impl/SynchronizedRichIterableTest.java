/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.lazy.LazyIterableAdapter;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SynchronizedRichIterableTest extends AbstractRichIterableTestCase
{
    @Override
    protected <T> RichIterable<T> newWith(T... littleElements)
    {
        return SynchronizedRichIterable.of(Lists.mutable.of(littleElements));
    }

    @Override
    @Test
    public void chunk_large_size()
    {
        RichIterable<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        Verify.assertIterablesEqual(collection, collection.chunk(10).getOnly());
    }

    @Override
    @Test
    public void partition()
    {
        RichIterable<Integer> integers = this.newWith(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PartitionIterable<Integer> result = integers.partition(IntegerPredicates.isEven());
        assertEquals(iList(-2, 0, 2, 4, 6, 8), result.getSelected());
        assertEquals(iList(-3, -1, 1, 3, 5, 7, 9), result.getRejected());
    }

    @Override
    @Test
    public void partitionWith()
    {
        RichIterable<Integer> integers = this.newWith(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PartitionIterable<Integer> result = integers.partitionWith(Predicates2.in(), FastList.newListWith(-2, 0, 2, 4, 6, 8));
        assertEquals(iList(-2, 0, 2, 4, 6, 8), result.getSelected());
        assertEquals(iList(-3, -1, 1, 3, 5, 7, 9), result.getRejected());
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

    @Test
    public void groupByWithTarget()
    {
        RichIterable<Integer> list = this.newWith(1, 2, 3, 4, 5, 6, 7);
        MutableMultimap<Boolean, Integer> multimap = new FastListMultimap<>();
        list.groupBy(object -> IntegerPredicates.isOdd().accept(object), multimap);

        assertEquals(FastList.newListWith(1, 3, 5, 7), multimap.get(Boolean.TRUE));
        assertEquals(FastList.newListWith(2, 4, 6), multimap.get(Boolean.FALSE));
    }

    @Test
    public void asLazy()
    {
        RichIterable<Integer> integers = this.newWith(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9).asLazy();
        Verify.assertInstanceOf(LazyIterableAdapter.class, integers);
        PartitionIterable<Integer> result = integers.partitionWith(Predicates2.in(), FastList.newListWith(-2, 0, 2, 4, 6, 8));
        assertEquals(iList(-2, 0, 2, 4, 6, 8), result.getSelected());
        assertEquals(iList(-3, -1, 1, 3, 5, 7, 9), result.getRejected());
    }

    @Test
    public void nullCheck()
    {
        assertThrows(IllegalArgumentException.class, () -> SynchronizedRichIterable.of(null, null));
    }
}
