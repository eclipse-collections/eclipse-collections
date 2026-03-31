/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.sorted.immutable;

import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.SortedBag;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.impl.factory.SortedBags;
import org.junit.jupiter.api.Test;

public class ImmutableSortedBagImplNoIteratorTest extends ImmutableSortedBagImplTest
{
    @Override
    protected ImmutableSortedBag<Integer> classUnderTest()
    {
        return new ImmutableSortedBagImplNoIterator<>(SortedBags.immutable.with(1, 1, 1, 2));
    }

    @Override
    protected <T> MutableCollection<T> newMutable()
    {
        return SortedBags.mutable.empty();
    }

    @Override
    protected ImmutableSortedBag<Integer> classUnderTest(Comparator<? super Integer> comparator)
    {
        return new ImmutableSortedBagImplNoIterator<>(SortedBags.immutable.with(comparator, 1, 1, 1, 2));
    }

    @Override
    protected <T> ImmutableSortedBag<T> newWith(T... elements)
    {
        ImmutableSortedBag<T> bag = SortedBags.immutable.with(elements);
        if (bag.isEmpty())
        {
            return new ImmutableEmptySortedBagImplNoIterator<>(bag.comparator());
        }
        return new ImmutableSortedBagImplNoIterator<>(bag);
    }

    @Override
    protected <T> ImmutableSortedBag<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return new ImmutableSortedBagImplNoIterator<>(SortedBags.immutable.with(comparator, elements));
    }

    @Override
    @Test
    public void forLoop()
    {
        //not applicable
    }

    @Override
    @Test
    public void iteratorRemove()
    {
        //not applicable
    }

    @Override
    @Test
    public void stream()
    {
        //not applicable
    }

    @Override
    @Test
    public void parallelStream()
    {
        //not applicable
    }

    private static final class ImmutableSortedBagImplNoIterator<T> extends ImmutableSortedBagImpl<T>
    {
        ImmutableSortedBagImplNoIterator(SortedBag<T> sortedBag)
        {
            super(sortedBag);
        }

        @Override
        public Iterator<T> iterator()
        {
            throw new AssertionError("No methods should delegate to iterator");
        }
    }

    private static final class ImmutableEmptySortedBagImplNoIterator<T> extends ImmutableEmptySortedBag<T>
    {
        ImmutableEmptySortedBagImplNoIterator(Comparator<? super T> comparator)
        {
            super(comparator);
        }

        @Override
        public Iterator<T> iterator()
        {
            throw new AssertionError("No methods should delegate to iterator");
        }
    }
}
