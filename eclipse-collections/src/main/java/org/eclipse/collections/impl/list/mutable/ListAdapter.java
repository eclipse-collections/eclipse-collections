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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.ParallelListIterable;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.lazy.parallel.list.NonParallelListIterable;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.eclipse.collections.impl.utility.OrderedIterate;

/**
 * This class provides a MutableList wrapper around a JDK Collections List interface instance. All the MutableList
 * interface methods are supported in addition to the JDK List interface methods.
 * <p>
 * To create a new wrapper around an existing List instance, use the {@link #adapt(List)} factory method.
 */
public final class ListAdapter<T>
        extends AbstractListAdapter<T>
        implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final List<T> delegate;

    ListAdapter(List<T> newDelegate)
    {
        if (newDelegate == null)
        {
            throw new NullPointerException("ListAdapter may not wrap null");
        }
        if (newDelegate instanceof RandomAccess)
        {
            throw new IllegalArgumentException("Use RandomAccessListAdapter instead");
        }
        this.delegate = newDelegate;
    }

    @Override
    protected List<T> getDelegate()
    {
        return this.delegate;
    }

    public static <E> MutableList<E> adapt(List<E> list)
    {
        if (list instanceof MutableList)
        {
            return (MutableList<E>) list;
        }
        if (list instanceof ArrayList)
        {
            return ArrayListAdapter.adapt((ArrayList<E>) list);
        }
        if (list instanceof RandomAccess)
        {
            return new RandomAccessListAdapter<>(list);
        }
        return new ListAdapter<>(list);
    }

    @Override
    public ImmutableList<T> toImmutable()
    {
        return Lists.immutable.withAll(this.delegate);
    }

    @Override
    public MutableList<T> asUnmodifiable()
    {
        return UnmodifiableMutableList.of(this);
    }

    @Override
    public MutableList<T> asSynchronized()
    {
        return SynchronizedMutableList.of(this);
    }

    @Override
    public MutableList<T> reversed()
    {
        return new ReversedMutableList<>(this);
    }

    @Override
    public MutableList<T> clone()
    {
        return Lists.mutable.withAll(this.delegate);
    }

    /**
     * @deprecated use {@link FastList#newList()} instead (inlineable)
     */
    @Override
    @Deprecated
    public MutableList<T> newEmpty()
    {
        return FastList.newList();
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        ListIterate.forEach(this.delegate, procedure);
    }

    @Override
    public void reverseForEach(Procedure<? super T> procedure)
    {
        ListIterate.reverseForEach(this.delegate, procedure);
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        ListIterate.reverseForEachWithIndex(this.delegate, procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.forEachWithIndex(this.delegate, objectIntProcedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.forEachWithIndex(this.delegate, fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return ListIterate.detect(this.delegate, predicate);
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        T result = this.detect(predicate);
        return result == null ? function.value() : result;
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return ListIterate.detectIndex(this.delegate, predicate);
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        return ListIterate.detectLastIndex(this.delegate, predicate);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return ListIterate.count(this.delegate, predicate);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return OrderedIterate.corresponds(this, other, predicate);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return ListIterate.anySatisfy(this.delegate, predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return ListIterate.allSatisfy(this.delegate, predicate);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return ListIterate.noneSatisfy(this.delegate, predicate);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return ListIterate.injectInto(injectedValue, this.delegate, function);
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        ListIterate.forEach(this.delegate, fromIndex, toIndex, procedure);
    }

    /**
     * @since 10.0
     */
    @Override
    public void sort(Comparator<? super T> comparator)
    {
        this.delegate.sort(comparator);
    }

    /**
     * @since 10.0
     */
    @Override
    public ListAdapter<T> sortThis(Comparator<? super T> comparator)
    {
        return (ListAdapter<T>) super.sortThis(comparator);
    }

    /**
     * @since 10.0
     */
    @Override
    public ListAdapter<T> sortThis()
    {
        return (ListAdapter<T>) super.sortThis();
    }

    @Override
    public ListAdapter<T> with(T element)
    {
        this.add(element);
        return this;
    }

    public ListAdapter<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public ListAdapter<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public ListAdapter<T> with(T... elements)
    {
        ArrayIterate.forEach(elements, CollectionAddProcedure.on(this.delegate));
        return this;
    }

    @Override
    public ListAdapter<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public ListAdapter<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    public ListAdapter<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return ListIterate.selectInstancesOf(this.delegate, clazz);
    }

    @Override
    public MutableList<T> distinct()
    {
        return ListIterate.distinct(this.delegate);
    }

    @Override
    public MutableList<T> distinct(HashingStrategy<? super T> hashingStrategy)
    {
        return ListIterate.distinct(this.delegate, hashingStrategy);
    }

    @Override
    public MutableList<T> take(int count)
    {
        return ListIterate.take(this, count);
    }

    @Override
    public MutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        return ListIterate.takeWhile(this.delegate, predicate);
    }

    @Override
    public MutableList<T> drop(int count)
    {
        return ListIterate.drop(this, count);
    }

    @Override
    public MutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        return ListIterate.dropWhile(this.delegate, predicate);
    }

    @Override
    public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        return ListIterate.partitionWhile(this.delegate, predicate);
    }

    @Override
    public ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new NonParallelListIterable<>(this);
    }
}
