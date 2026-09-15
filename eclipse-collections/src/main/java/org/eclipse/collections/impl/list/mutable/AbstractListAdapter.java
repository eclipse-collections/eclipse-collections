/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.ParallelListIterable;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.collection.mutable.AbstractCollectionAdapter;
import org.eclipse.collections.impl.lazy.ReverseIterable;
import org.eclipse.collections.impl.lazy.parallel.list.ListIterableParallelIterable;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.utility.ListIterate;

@SuppressWarnings("AbstractMethodOverridesConcreteMethod")
public abstract class AbstractListAdapter<T>
        extends AbstractCollectionAdapter<T>
        implements MutableList<T>
{
    @Override
    public MutableList<T> clone()
    {
        try
        {
            return (MutableList<T>) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new AssertionError(e);
        }
    }

    @Override
    protected abstract List<T> getDelegate();

    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        return this.getDelegate().addAll(index, collection);
    }

    @Override
    public T get(int index)
    {
        return this.getDelegate().get(index);
    }

    @Override
    public T set(int index, T element)
    {
        return this.getDelegate().set(index, element);
    }

    @Override
    public void add(int index, T element)
    {
        this.getDelegate().add(index, element);
    }

    @Override
    public T remove(int index)
    {
        return this.getDelegate().remove(index);
    }

    @Override
    public int indexOf(Object o)
    {
        return this.getDelegate().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return this.getDelegate().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator()
    {
        return this.getDelegate().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index)
    {
        return this.getDelegate().listIterator(index);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> MutableList<T> distinctBy(Function<? super T, ? extends V> function)
    {
        return this.distinct(HashingStrategies.fromFunction(function));
    }

    @Override
    public MutableList<T> sortThisByInt(IntFunction<? super T> function)
    {
        return this.sortThis(Functions.toIntComparator(function));
    }

    @Override
    public MutableList<T> sortThisByBoolean(BooleanFunction<? super T> function)
    {
        return this.sortThis(Functions.toBooleanComparator(function));
    }

    @Override
    public MutableList<T> sortThisByChar(CharFunction<? super T> function)
    {
        return this.sortThis(Functions.toCharComparator(function));
    }

    @Override
    public MutableList<T> sortThisByByte(ByteFunction<? super T> function)
    {
        return this.sortThis(Functions.toByteComparator(function));
    }

    @Override
    public MutableList<T> sortThisByShort(ShortFunction<? super T> function)
    {
        return this.sortThis(Functions.toShortComparator(function));
    }

    @Override
    public MutableList<T> sortThisByFloat(FloatFunction<? super T> function)
    {
        return this.sortThis(Functions.toFloatComparator(function));
    }

    @Override
    public MutableList<T> sortThisByLong(LongFunction<? super T> function)
    {
        return this.sortThis(Functions.toLongComparator(function));
    }

    @Override
    public MutableList<T> sortThisByDouble(DoubleFunction<? super T> function)
    {
        return this.sortThis(Functions.toDoubleComparator(function));
    }

    @Override
    public MutableList<T> subList(int fromIndex, int toIndex)
    {
        return ListAdapter.adapt(this.getDelegate().subList(fromIndex, toIndex));
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        return this.getDelegate().equals(o);
    }

    @Override
    public int hashCode()
    {
        return this.getDelegate().hashCode();
    }

    @Override
    public MutableStack<T> toStack()
    {
        return ArrayStack.newStack(this.getDelegate());
    }

    @Override
    public ReverseIterable<T> asReversed()
    {
        return ReverseIterable.adapt(this);
    }

    @Override
    public ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new ListIterableParallelIterable<>(this, executorService, batchSize);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return ListIterate.groupBy(this.getDelegate(), function);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return ListIterate.groupByEach(this.getDelegate(), function);
    }

    @Override
    public MutableList<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public MutableList<T> select(Predicate<? super T> predicate)
    {
        return ListIterate.select(this.getDelegate(), predicate);
    }

    @Override
    public MutableList<T> reject(Predicate<? super T> predicate)
    {
        return ListIterate.reject(this.getDelegate(), predicate);
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return ListIterate.selectInstancesOf(this.getDelegate(), clazz);
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return ListIterate.collect(this.getDelegate(), function);
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return ListIterate.flatCollect(this.getDelegate(), function);
    }

    @Override
    public <V> MutableList<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return ListIterate.collectIf(this.getDelegate(), predicate, function);
    }

    @Override
    public PartitionMutableList<T> partition(Predicate<? super T> predicate)
    {
        return ListIterate.partition(this.getDelegate(), predicate);
    }

    @Override
    public <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.partitionWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public <P> MutableList<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.selectWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public <P> MutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.rejectWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return ListIterate.collectWith(this.getDelegate(), function, parameter);
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return ListIterate.zip(this.getDelegate(), that);
    }

    @Override
    public MutableList<Pair<T, Integer>> zipWithIndex()
    {
        return ListIterate.zipWithIndex(this.getDelegate());
    }
}
