/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.list.mutable;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MultiReaderList;
import org.eclipse.collections.impl.list.mutable.MultiReaderFastList;
import org.eclipse.collections.test.IterableTestCase;
import org.eclipse.collections.test.collection.mutable.MultiReaderMutableCollectionTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiReaderFastListTest implements MutableListTestCase, MultiReaderMutableCollectionTestCase
{
    @SafeVarargs
    @Override
    public final <T> MultiReaderFastList<T> newWith(T... elements)
    {
        MultiReaderFastList<T> result = MultiReaderFastList.newList();
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
    public void RichIterable_iterator_iterationOrder()
    {
        MutableCollection<Integer> iterationOrder = this.newMutableForFilter();
        MultiReaderFastList<Integer> instanceUnderTest = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        instanceUnderTest.withReadLockAndDelegate(delegate -> {
            Iterator<Integer> iterator = delegate.iterator();
            while (iterator.hasNext())
            {
                iterationOrder.add(iterator.next());
            }
        });
        assertIterablesEqual(this.expectedIterationOrder(), iterationOrder);
    }

    @Override
    @Test
    public void Iterable_next()
    {
        // Does not support iterator outside withReadLockAndDelegate

        MultiReaderFastList<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);

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
    }

    @Test
    public void MultiReaderFastList_hasNext()
    {
        MultiReaderFastList<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);
        iterable.withReadLockAndDelegate(delegate -> assertTrue(delegate.iterator().hasNext()));
        MultiReaderFastList<?> emptyIterable = this.newWith();
        emptyIterable.withReadLockAndDelegate(delegate -> assertFalse(delegate.iterator().hasNext()));
    }

    @Test
    public void MultiReaderFastList_next_throws_at_end()
    {
        MultiReaderFastList<Integer> iterable = this.newWith(3, 2, 1);
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
    public void MultiReaderFastList_next_throws_on_empty()
    {
        MultiReaderFastList<Object> iterable = this.newWith();
        assertThrows(
                NoSuchElementException.class,
                () -> iterable.withReadLockAndDelegate(delegate -> delegate.iterator().next()));
    }

    @Override
    @Test
    public void List_subList_subList_iterator_add_remove()
    {
        MultiReaderList<String> list = this.newWith("A", "B", "C", "D");

        list.withWriteLockAndDelegate(delegate -> {
            List<String> sublist = delegate.subList(0, 3);
            List<String> sublist2 = sublist.subList(0, 2);
            ListIterator<String> iterator = sublist2.listIterator();
            iterator.add("X");
            assertIterablesEqual(Lists.immutable.with("X", "A", "B", "C"), sublist);
            assertIterablesEqual(Lists.immutable.with("X", "A", "B"), sublist2);

            ListIterator<String> iterator2 = sublist2.listIterator();
            iterator2.next();
            iterator2.remove();
            assertIterablesEqual(Lists.immutable.with("A", "B", "C"), sublist);
            assertIterablesEqual(Lists.immutable.with("A", "B"), sublist2);
        });

        assertIterablesEqual(Lists.immutable.with("A", "B", "C", "D"), list);
    }
}
