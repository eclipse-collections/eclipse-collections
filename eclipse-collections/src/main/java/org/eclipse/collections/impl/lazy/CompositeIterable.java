/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.Counter;
import org.eclipse.collections.impl.EmptyIterator;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.utility.Iterate;

public final class CompositeIterable<E>
        extends AbstractLazyIterable<E>
{
    private final MutableList<Iterable<E>> iterables;

    private CompositeIterable(MutableList<Iterable<E>> newIterables)
    {
        this.iterables = newIterables;
    }

    public CompositeIterable()
    {
        this(FastList.newList());
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> CompositeIterable<T> with(Iterable<T>... iterables)
    {
        return new CompositeIterable<>(FastList.newListWith(iterables));
    }

    @Override
    public void each(Procedure<? super E> procedure)
    {
        this.iterables.each(iterable -> Iterate.forEach(iterable, procedure));
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super E> objectIntProcedure)
    {
        Counter index = new Counter();
        this.iterables.each(iterable -> Iterate.forEach(iterable, object ->
        {
            objectIntProcedure.value(object, index.getCount());
            index.increment();
        }));
    }

    @Override
    public <P> void forEachWith(Procedure2<? super E, ? super P> procedure, P parameter)
    {
        this.iterables.each(iterable -> Iterate.forEachWith(iterable, procedure, parameter));
    }

    @Override
    public boolean anySatisfy(Predicate<? super E> predicate)
    {
        return this.iterables.anySatisfy(each -> Iterate.anySatisfy(each, predicate));
    }

    @Override
    public boolean allSatisfy(Predicate<? super E> predicate)
    {
        return this.iterables.allSatisfy(each -> Iterate.allSatisfy(each, predicate));
    }

    @Override
    public boolean noneSatisfy(Predicate<? super E> predicate)
    {
        return this.iterables.noneSatisfy(each -> Iterate.anySatisfy(each, predicate));
    }

    @Override
    public E detect(Predicate<? super E> predicate)
    {
        for (int i = 0; i < this.iterables.size(); i++)
        {
            Iterable<E> eachIterable = this.iterables.get(i);
            E result = Iterate.detect(eachIterable, predicate);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }

    @Override
    public Optional<E> detectOptional(Predicate<? super E> predicate)
    {
        for (int i = 0; i < this.iterables.size(); i++)
        {
            Iterable<E> eachIterable = this.iterables.get(i);
            Optional<E> result = Iterate.detectOptional(eachIterable, predicate);
            if (result.isPresent())
            {
                return result;
            }
        }
        return Optional.empty();
    }

    public void add(Iterable<E> iterable)
    {
        this.iterables.add(iterable);
    }

    @Override
    public int size()
    {
        return (int) this.iterables.sumOfInt(Iterate::sizeOf);
    }

    @Override
    public Iterator<E> iterator()
    {
        return new CompositeIterator(this.iterables);
    }

    private final class CompositeIterator
            implements Iterator<E>
    {
        private final Iterator<Iterable<E>> iterablesIterator;
        private Iterator<E> innerIterator;

        private CompositeIterator(MutableList<Iterable<E>> iterables)
        {
            this.iterablesIterator = iterables.listIterator();
            this.innerIterator = EmptyIterator.getInstance();
        }

        @Override
        public boolean hasNext()
        {
            while (true)
            {
                if (this.innerIterator.hasNext())
                {
                    return true;
                }
                if (!this.iterablesIterator.hasNext())
                {
                    return false;
                }
                this.innerIterator = this.iterablesIterator.next().iterator();
            }
        }

        @Override
        public E next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            return this.innerIterator.next();
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Cannot remove from a composite iterator");
        }
    }
}
