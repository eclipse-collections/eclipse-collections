/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.fixed;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.RandomAccess;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.list.mutable.AbstractMutableList;
import org.eclipse.collections.impl.utility.Iterate;

public abstract class AbstractMemoryEfficientMutableList<T>
        extends AbstractMutableList<T>
        implements FixedSizeList<T>, RandomAccess
{
    @Override
    public FixedSizeList<T> clone()
    {
        return (FixedSizeList<T>) super.clone();
    }

    @Override
    public FixedSizeList<T> tap(Procedure<? super T> procedure)
    {
        this.each(procedure);
        return this;
    }

    @Override
    public boolean add(T o)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    @Override
    public void add(int index, T element)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    @Override
    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    @Override
    public T remove(int index)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    @Override
    public boolean removeIf(java.util.function.Predicate<? super T> filter)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        throw new UnsupportedOperationException("Cannot removeIfWith from a fixed size list: " + this.getClass());
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    @Override
    public boolean retainAllIterable(Iterable<?> iterable)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot clear a fixed size list: " + this.getClass());
    }

    /**
     * This method checks if comparator is null and use a ComparableComparator if it is.
     *
     * @since 10.0
     */
    @Override
    public void sort(Comparator<? super T> comparator)
    {
        this.insertionSort(Comparators.comparableComparatorIfNull(comparator));
    }

    private void insertionSort(Comparator<? super T> comparator)
    {
        for (int i = 0; i < this.size(); i++)
        {
            for (int j = i; this.isPreviousGreaterThanCurrent(comparator, j); j--)
            {
                Collections.swap(this, j - 1, j);
            }
        }
    }

    private boolean isPreviousGreaterThanCurrent(Comparator<? super T> comparator, int index)
    {
        return index > 0 && comparator.compare(this.get(index - 1), this.get(index)) > 0;
    }

    @Override
    public FixedSizeList<T> toReversed()
    {
        FixedSizeList<T> result = Lists.fixedSize.withAll(this);
        result.reverseThis();
        return result;
    }

    @Override
    public MutableList<T> subList(int fromIndex, int toIndex)
    {
        return new SubList<>(this, fromIndex, toIndex);
    }

    @Override
    public MutableList<T> without(T element)
    {
        if (this.contains(element))
        {
            return Lists.fixedSize.ofAll(this.toList().without(element));
        }
        return this;
    }

    @Override
    public MutableList<T> withAll(Iterable<? extends T> elements)
    {
        if (Iterate.isEmpty(elements))
        {
            return this;
        }
        return Lists.fixedSize.ofAll(this.toList().withAll(elements));
    }

    @Override
    public MutableList<T> withoutAll(Iterable<? extends T> elements)
    {
        if (Iterate.isEmpty(elements))
        {
            return this;
        }
        return Lists.fixedSize.ofAll(this.toList().withoutAll(elements));
    }

    private static class SubList<T>
            extends AbstractMutableList.SubList<T>
    {
        // Not important since it uses writeReplace()
        private static final long serialVersionUID = 1L;

        protected SubList(AbstractMutableList<T> list, int fromIndex, int toIndex)
        {
            super(list, fromIndex, toIndex);
        }

        @Override
        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean removeAllIterable(Iterable<?> iterable)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean retainAllIterable(Iterable<?> iterable)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean removeIf(Predicate<? super T> predicate)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean removeIf(java.util.function.Predicate<? super T> filter)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
        {
            throw new UnsupportedOperationException("Cannot removeIfWith from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean addAll(Collection<? extends T> collection)
        {
            throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
        }

        @Override
        public boolean addAllIterable(Iterable<? extends T> iterable)
        {
            throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
        }
    }

    @Override
    public ListIterator<T> listIterator(int index)
    {
        return new FixedSizeListIteratorAdapter<>(super.listIterator(index));
    }

    @Override
    public ListIterator<T> listIterator()
    {
        return new FixedSizeListIteratorAdapter<>(super.listIterator());
    }
}
