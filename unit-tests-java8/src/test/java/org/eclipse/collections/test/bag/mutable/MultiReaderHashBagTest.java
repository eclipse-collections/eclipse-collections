/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.bag.mutable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.impl.bag.mutable.MultiReaderHashBag;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.test.IterableTestCase;
import org.eclipse.collections.test.collection.mutable.MultiReaderMutableCollectionTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiReaderHashBagTest implements MutableBagTestCase, MultiReaderMutableCollectionTestCase
{
    @SafeVarargs
    @Override
    public final <T> MultiReaderHashBag<T> newWith(T... elements)
    {
        MultiReaderHashBag<T> result = MultiReaderHashBag.newBag();
        IterableTestCase.addAllTo(elements, result);
        return result;
    }

    @Override
    @Test
    public void Iterable_remove()
    {
        MultiReaderMutableCollectionTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    public void Iterable_next()
    {
        MultiReaderMutableCollectionTestCase.super.Iterable_next();
    }

    @Override
    @Test
    public void RichIterable_getFirst()
    {
        MultiReaderMutableCollectionTestCase.super.RichIterable_getFirst();
    }

    @Override
    @Test
    public void RichIterable_getLast()
    {
        MultiReaderMutableCollectionTestCase.super.RichIterable_getLast();
    }

    @Override
    @Test
    public void RichIterable_iterator_iterationOrder()
    {
        MutableCollection<Integer> iterationOrder = this.newMutableForFilter();
        MultiReaderHashBag<Integer> instanceUnderTest = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        MutableCollection<Integer> expectedIterationOrder = this.newMutableForFilter();
        instanceUnderTest.forEach(Procedures.cast(expectedIterationOrder::add));

        instanceUnderTest.withReadLockAndDelegate(delegate -> {
            Iterator<Integer> iterator = delegate.iterator();
            while (iterator.hasNext())
            {
                iterationOrder.add(iterator.next());
            }
        });

        // TODO Report to JetBrains
        // assertEquals(MultiReaderMutableCollectionTestCase.super.expectedIterationOrder(), iterationOrder);
        assertIterablesEqual(expectedIterationOrder, iterationOrder);
    }

    @Test
    public void MultiReaderHashBag_next()
    {
        MultiReaderHashBag<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);

        MutableCollection<Integer> mutableCollection = this.newMutableForFilter();

        iterable.withReadLockAndDelegate(delegate -> {
            Iterator<Integer> iterator = delegate.iterator();
            while (iterator.hasNext())
            {
                Integer integer = iterator.next();
                mutableCollection.add(integer);
            }

            assertIterablesEqual(this.getExpectedFiltered(3, 3, 3, 2, 2, 1), mutableCollection);
            assertFalse(iterator.hasNext());
        });

        MultiReaderHashBag<Integer> iterable2 = this.newWith(3, 2, 1);
        iterable2.withReadLockAndDelegate(delegate -> {
            Iterator<Integer> iterator = delegate.iterator();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertFalse(iterator.hasNext());
            assertThrows(NoSuchElementException.class, iterator::next);
        });

        assertThrows(
                NoSuchElementException.class,
                () -> this.newWith().withReadLockAndDelegate(delegate -> delegate.iterator().next()));
    }

    @Test
    public void MultiReaderHashBag_hasNext()
    {
        MultiReaderHashBag<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);
        iterable.withReadLockAndDelegate(delegate -> assertTrue(delegate.iterator().hasNext()));
        MultiReaderHashBag<?> emptyIterable = this.newWith();
        emptyIterable.withReadLockAndDelegate(delegate -> assertFalse(delegate.iterator().hasNext()));
    }
}
