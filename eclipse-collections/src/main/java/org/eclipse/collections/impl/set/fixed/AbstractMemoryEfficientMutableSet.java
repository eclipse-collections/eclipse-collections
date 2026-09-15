/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.fixed;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.FixedSizeSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.ParallelUnsortedSetIterable;
import org.eclipse.collections.impl.set.mutable.AbstractMutableSet;
import org.eclipse.collections.impl.utility.Iterate;

abstract class AbstractMemoryEfficientMutableSet<T>
        extends AbstractMutableSet<T>
        implements FixedSizeSet<T>
{
    protected int nullSafeHashCode(Object element)
    {
        return element == null ? 0 : element.hashCode();
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size set: " + this.getClass());
    }

    @Override
    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size set: " + this.getClass());
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size set: " + this.getClass());
    }

    @Override
    public boolean retainAllIterable(Iterable<?> iterable)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size set: " + this.getClass());
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size set: " + this.getClass());
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size set: " + this.getClass());
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size set: " + this.getClass());
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size set: " + this.getClass());
    }

    @Override
    public boolean removeIf(java.util.function.Predicate<? super T> filter)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size set: " + this.getClass());
    }

    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        throw new UnsupportedOperationException("Cannot removeIfWith from a fixed size set: " + this.getClass());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot clear a fixed size set: " + this.getClass());
    }

    @Override
    public MutableSet<T> withAll(Iterable<? extends T> elements)
    {
        if (Iterate.isEmpty(elements))
        {
            return this;
        }
        return Sets.fixedSize.ofAll(this.toList().withAll(elements));
    }

    @Override
    public MutableSet<T> withoutAll(Iterable<? extends T> elements)
    {
        if (Iterate.isEmpty(elements))
        {
            return this;
        }
        return Sets.fixedSize.ofAll(this.toList().withoutAll(elements));
    }

    @Override
    public FixedSizeSet<T> tap(Procedure<? super T> procedure)
    {
        this.each(procedure);
        return this;
    }

    protected abstract class MemoryEfficientSetIterator
            implements Iterator<T>
    {
        private int next;    // next entry to return, defaults to 0

        protected abstract T getElement(int i);

        @Override
        public boolean hasNext()
        {
            return this.next < AbstractMemoryEfficientMutableSet.this.size();
        }

        @Override
        public T next()
        {
            return this.getElement(this.next++);
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size set: " + this.getClass());
        }
    }

    @Override
    public ParallelUnsortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return this.toSet().asParallel(executorService, batchSize);
    }
}
