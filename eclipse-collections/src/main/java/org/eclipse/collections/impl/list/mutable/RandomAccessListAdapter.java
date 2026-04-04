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

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.eclipse.collections.impl.utility.internal.RandomAccessListIterate;

/**
 * This class provides a MutableList wrapper around a JDK Collections List interface instance. All the MutableList
 * interface methods are supported in addition to the JDK List interface methods.
 * <p>
 * To create a new wrapper around an existing List instance, use the {@link #adapt(List)} factory method.
 */
public class RandomAccessListAdapter<T>
        extends AbstractListAdapter<T>
        implements RandomAccess, Serializable
{
    private static final long serialVersionUID = 1L;
    private final List<T> delegate;

    protected RandomAccessListAdapter(List<T> newDelegate)
    {
        if (newDelegate == null)
        {
            throw new NullPointerException("RandomAccessListAdapter may not wrap null");
        }
        if (!(newDelegate instanceof RandomAccess))
        {
            throw new IllegalArgumentException("RandomAccessListAdapter may not wrap a non RandomAccess list");
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
        return new RandomAccessListAdapter<>(list);
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
        return new ReversedRandomAccessMutableList<>(this);
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
        return Lists.mutable.empty();
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        RandomAccessListIterate.forEach(this.delegate, procedure);
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
        RandomAccessListIterate.forEachWithIndex(this.delegate, objectIntProcedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        RandomAccessListIterate.forEachWithIndex(this.delegate, fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.detect(this.delegate, predicate);
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
        return RandomAccessListIterate.detectIndex(this.delegate, predicate);
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.detectLastIndex(this.delegate, predicate);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.count(this.delegate, predicate);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return RandomAccessListIterate.corresponds(this.delegate, other, predicate);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.anySatisfy(this.delegate, predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.allSatisfy(this.delegate, predicate);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.noneSatisfy(this.delegate, predicate);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return RandomAccessListIterate.injectInto(injectedValue, this.delegate, function);
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        RandomAccessListIterate.forEach(this.delegate, fromIndex, toIndex, procedure);
    }

    @Override
    public void sort(Comparator<? super T> comparator)
    {
        this.delegate.sort(comparator);
    }

    @Override
    public RandomAccessListAdapter<T> sortThis(Comparator<? super T> comparator)
    {
        return (RandomAccessListAdapter<T>) super.sortThis(comparator);
    }

    @Override
    public RandomAccessListAdapter<T> sortThis()
    {
        return (RandomAccessListAdapter<T>) super.sortThis();
    }

    @Override
    public RandomAccessListAdapter<T> with(T element)
    {
        this.add(element);
        return this;
    }

    public RandomAccessListAdapter<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public RandomAccessListAdapter<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public RandomAccessListAdapter<T> with(T... elements)
    {
        ArrayIterate.forEach(elements, CollectionAddProcedure.on(this.delegate));
        return this;
    }

    @Override
    public RandomAccessListAdapter<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public RandomAccessListAdapter<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    public RandomAccessListAdapter<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    public MutableList<T> select(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.select(this.delegate, predicate, FastList.newList());
    }

    @Override
    public MutableList<T> reject(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.reject(this.delegate, predicate, FastList.newList());
    }

    @Override
    public PartitionMutableList<T> partition(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.partition(this.delegate, predicate);
    }

    @Override
    public <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return RandomAccessListIterate.partitionWith(this.delegate, predicate, parameter);
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return RandomAccessListIterate.selectInstancesOf(this.delegate, clazz);
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return RandomAccessListIterate.collect(this.delegate, function, Lists.mutable.withInitialCapacity(this.delegate.size()));
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return RandomAccessListIterate.flatCollect(this.delegate, function, Lists.mutable.withInitialCapacity(this.delegate.size()));
    }

    @Override
    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return RandomAccessListIterate.collectIf(this.delegate, predicate, function, FastList.newList());
    }

    @Override
    public <V> FastListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return RandomAccessListIterate.groupBy(this.delegate, function, FastListMultimap.newMultimap());
    }

    @Override
    public <V> FastListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return RandomAccessListIterate.groupByEach(this.delegate, function, FastListMultimap.newMultimap());
    }

    @Override
    public <P> MutableList<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return RandomAccessListIterate.selectWith(this.delegate, predicate, parameter, FastList.newList());
    }

    @Override
    public <P> MutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return RandomAccessListIterate.rejectWith(this.delegate, predicate, parameter, FastList.newList());
    }

    @Override
    public <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return RandomAccessListIterate.collectWith(this.delegate, function, parameter, Lists.mutable.withInitialCapacity(this.delegate.size()));
    }

    @Override
    public MutableList<T> distinct()
    {
        return RandomAccessListIterate.distinct(this.delegate);
    }

    @Override
    public MutableList<T> distinct(HashingStrategy<? super T> hashingStrategy)
    {
        return RandomAccessListIterate.distinct(this.delegate, hashingStrategy);
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return RandomAccessListIterate.zip(this.delegate, that, Lists.mutable.withInitialCapacity(this.delegate.size()));
    }

    @Override
    public MutableList<Pair<T, Integer>> zipWithIndex()
    {
        return RandomAccessListIterate.zipWithIndex(this.delegate, Lists.mutable.withInitialCapacity(this.delegate.size()));
    }

    @Override
    public MutableList<T> take(int count)
    {
        return RandomAccessListIterate.take(this.delegate, count);
    }

    @Override
    public MutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.takeWhile(this.delegate, predicate);
    }

    @Override
    public MutableList<T> drop(int count)
    {
        return RandomAccessListIterate.drop(this.delegate, count);
    }

    @Override
    public MutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.dropWhile(this.delegate, predicate);
    }

    @Override
    public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.partitionWhile(this.delegate, predicate);
    }
}
