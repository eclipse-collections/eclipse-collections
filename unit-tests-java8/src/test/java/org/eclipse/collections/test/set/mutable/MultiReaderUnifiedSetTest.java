/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.impl.set.mutable.MultiReaderUnifiedSet;
import org.eclipse.collections.test.IterableTestCase;
import org.eclipse.collections.test.collection.mutable.MultiReaderMutableCollectionTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiReaderUnifiedSetTest implements MutableSetTestCase, MultiReaderMutableCollectionTestCase
{
    @SafeVarargs
    @Override
    public final <T> MultiReaderUnifiedSet<T> newWith(T... elements)
    {
        MultiReaderUnifiedSet<T> result = MultiReaderUnifiedSet.newSet();
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

    // TODO Is it possible to pull with withReadLockAndDelegate to MultiReaderMutableCollection?
    // TODO Is it possible to pull with withWriteLockAndDelegate to MultiReaderMutableCollection?
    @Override
    @Test
    public void RichIterable_iterator_iterationOrder()
    {
        MutableCollection<Integer> iterationOrder = this.newMutableForFilter();
        MultiReaderUnifiedSet<Integer> instanceUnderTest = this.newWith(4, 3, 2, 1);
        instanceUnderTest.withReadLockAndDelegate(delegate -> {
            Iterator<Integer> iterator = delegate.iterator();
            while (iterator.hasNext())
            {
                iterationOrder.add(iterator.next());
            }
        });
        assertIterablesEqual(this.expectedIterationOrder(), iterationOrder);
    }

    @Test
    public void MultiReaderUnifiedSet_next()
    {
        MultiReaderUnifiedSet<Integer> iterable = this.newWith(3, 2, 1);

        MutableCollection<Integer> mutableCollection = this.newMutableForFilter();

        iterable.withReadLockAndDelegate(delegate -> {
            Iterator<Integer> iterator = delegate.iterator();
            while (iterator.hasNext())
            {
                Integer integer = iterator.next();
                mutableCollection.add(integer);
            }

            assertIterablesEqual(this.getExpectedFiltered(3, 2, 1), mutableCollection);
            assertFalse(iterator.hasNext());
        });
    }

    @Test
    public void MultiReaderUnifiedSet_hasNext()
    {
        MultiReaderUnifiedSet<Integer> iterable = this.newWith(3, 2, 1);
        iterable.withReadLockAndDelegate(delegate -> assertTrue(delegate.iterator().hasNext()));
        MultiReaderUnifiedSet<?> emptyIterable = this.newWith();
        emptyIterable.withReadLockAndDelegate(delegate -> assertFalse(delegate.iterator().hasNext()));
    }

    @Test
    public void MultiReaderUnifiedSet_next_throws_at_end()
    {
        MultiReaderUnifiedSet<Integer> iterable = this.newWith(3, 2, 1);
        iterable.withReadLockAndDelegate(delegate -> {
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
    }

    @Test
    public void MultiReaderUnifiedSet_next_throws_on_empty()
    {
        MultiReaderUnifiedSet<Object> iterable = this.newWith();
        assertThrows(
                NoSuchElementException.class,
                () -> iterable.withReadLockAndDelegate(delegate -> delegate.iterator().next()));
    }
}
