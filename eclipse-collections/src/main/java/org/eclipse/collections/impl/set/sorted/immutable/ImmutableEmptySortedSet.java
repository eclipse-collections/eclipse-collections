/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.EmptyIterator;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.utility.ListIterate;

/**
 * This is a zero element {@link ImmutableSortedSet} which is created by calling the SortedSets.immutable.empty() method.
 */
final class ImmutableEmptySortedSet<T>
        extends AbstractImmutableSortedSet<T>
        implements Serializable
{
    static final ImmutableSortedSet<?> INSTANCE = new ImmutableEmptySortedSet<>();

    private static final long serialVersionUID = 2L;

    private final Comparator<? super T> comparator;

    ImmutableEmptySortedSet()
    {
        this.comparator = null;
    }

    ImmutableEmptySortedSet(Comparator<? super T> comparator)
    {
        this.comparator = comparator;
    }

    private Object writeReplace()
    {
        return new ImmutableSortedSetSerializationProxy<>(this);
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        return other instanceof Set && ((Collection<?>) other).isEmpty();
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public ImmutableSortedSet<T> newWith(T element)
    {
        return SortedSets.immutable.with(this.comparator, element);
    }

    @Override
    public ImmutableSortedSet<T> newWithAll(Iterable<? extends T> elements)
    {
        return SortedSets.immutable.withAll(this.comparator, elements);
    }

    @Override
    public ImmutableSortedSet<T> newWithout(T element)
    {
        return this;
    }

    @Override
    public ImmutableSortedSet<T> newWithoutAll(Iterable<? extends T> elements)
    {
        return this;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean contains(Object object)
    {
        if (object == null)
        {
            throw new NullPointerException();
        }
        return false;
    }

    @Override
    public ImmutableSortedSet<T> tap(Procedure<? super T> procedure)
    {
        return this;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        ListIterate.rangeCheck(startIndex, endIndex, 0);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, 0);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
    }

    @Override
    public ImmutableSortedSet<T> select(Predicate<? super T> predicate)
    {
        return this;
    }

    @Override
    public <P> ImmutableSortedSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this;
    }

    @Override
    public ImmutableSortedSet<T> reject(Predicate<? super T> predicate)
    {
        return this;
    }

    @Override
    public <P> ImmutableSortedSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this;
    }

    @Override
    public <S> ImmutableSortedSet<S> selectInstancesOf(Class<S> clazz)
    {
        return (ImmutableSortedSet<S>) this;
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> ImmutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return Lists.immutable.empty();
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        return target;
    }

    @Override
    public Iterator<T> iterator()
    {
        return EmptyIterator.getInstance();
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        throw new NoSuchElementException();
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        throw new NoSuchElementException();
    }

    @Override
    public T min()
    {
        throw new NoSuchElementException();
    }

    @Override
    public T max()
    {
        throw new NoSuchElementException();
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        throw new NoSuchElementException();
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        throw new NoSuchElementException();
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return target;
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return target;
    }

    @Override
    public MutableStack<T> toStack()
    {
        return ArrayStack.newStack();
    }

    @Override
    public T first()
    {
        throw new NoSuchElementException();
    }

    @Override
    public T last()
    {
        throw new NoSuchElementException();
    }

    @Override
    public Comparator<? super T> comparator()
    {
        return this.comparator;
    }

    @Override
    public int compareTo(SortedSetIterable<T> o)
    {
        return o.size() * -1;
    }

    @Override
    public ImmutableSortedSet<T> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return this;
    }

    @Override
    public ImmutableSortedSet<T> drop(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return this;
    }

    @Override
    public T lower(T e)
    {
        return null;
    }

    @Override
    public T floor(T e)
    {
        return null;
    }

    @Override
    public T ceiling(T e)
    {
        return null;
    }

    @Override
    public T higher(T e)
    {
        return null;
    }

    @Override
    public Iterator<T> descendingIterator()
    {
        return EmptyIterator.getInstance();
    }

    @Override
    public ImmutableEmptySortedSet<T> descendingSet()
    {
        return new ImmutableEmptySortedSet<>(Collections.reverseOrder(this.comparator));
    }

    @Override
    public ImmutableEmptySortedSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive)
    {
        return this;
    }

    @Override
    public ImmutableEmptySortedSet<T> headSet(T toElement, boolean inclusive)
    {
        return this;
    }

    @Override
    public ImmutableEmptySortedSet<T> tailSet(T fromElement, boolean inclusive)
    {
        return this;
    }

    @Override
    public T getOnly()
    {
        throw new IllegalStateException("Size must be 1 but was " + this.size());
    }
}
