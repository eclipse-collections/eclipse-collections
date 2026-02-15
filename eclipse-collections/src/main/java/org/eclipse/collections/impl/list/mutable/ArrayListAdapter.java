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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.ArrayListIterate;
import org.eclipse.collections.impl.utility.internal.RandomAccessListIterate;

/**
 * This class provides a MutableList wrapper around a JDK Collections ArrayList instance. All the MutableList
 * interface methods are supported in addition to the JDK ArrayList methods.
 * <p>
 * To create a new wrapper around an existing ArrayList instance, use the {@link #adapt(ArrayList)} factory method. To
 * create a new empty wrapper, use the {@link #newList()} or {@link #newList(int)} factory methods.
 */
public final class ArrayListAdapter<T>
        extends AbstractListAdapter<T>
        implements RandomAccess, Serializable
{
    private static final long serialVersionUID = 1L;
    private final ArrayList<T> delegate;

    private ArrayListAdapter(ArrayList<T> newDelegate)
    {
        if (newDelegate == null)
        {
            throw new NullPointerException("ArrayListAdapter may not wrap null");
        }
        this.delegate = newDelegate;
    }

    @Override
    protected ArrayList<T> getDelegate()
    {
        return this.delegate;
    }

    public static <E> ArrayListAdapter<E> newList()
    {
        return new ArrayListAdapter<>(new ArrayList<>());
    }

    public static <E> ArrayListAdapter<E> newList(int size)
    {
        return new ArrayListAdapter<>(new ArrayList<>(size));
    }

    public static <E> ArrayListAdapter<E> adapt(ArrayList<E> newDelegate)
    {
        return new ArrayListAdapter<>(newDelegate);
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
    public ArrayListAdapter<T> clone()
    {
        return new ArrayListAdapter<>((ArrayList<T>) this.delegate.clone());
    }

    @Override
    public ArrayListAdapter<T> newEmpty()
    {
        return ArrayListAdapter.newList();
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        ArrayListIterate.forEach(this.delegate, procedure);
    }

    @Override
    public void reverseForEach(Procedure<? super T> procedure)
    {
        ArrayListIterate.reverseForEach(this.delegate, procedure);
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        ArrayListIterate.reverseForEachWithIndex(this.delegate, procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ArrayListIterate.forEachWithIndex(this.delegate, objectIntProcedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ArrayListIterate.forEachWithIndex(this.delegate, fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return ArrayListIterate.detect(this.delegate, predicate);
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        T result = this.detect(predicate);
        return result == null ? function.value() : result;
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return ArrayListIterate.count(this.delegate, predicate);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return RandomAccessListIterate.corresponds(this.delegate, other, predicate);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return ArrayListIterate.anySatisfy(this.delegate, predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return ArrayListIterate.allSatisfy(this.delegate, predicate);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return ArrayListIterate.noneSatisfy(this.delegate, predicate);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return ArrayListIterate.injectInto(injectedValue, this.delegate, function);
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        ArrayListIterate.forEach(this.delegate, fromIndex, toIndex, procedure);
    }

    /**
     * @since 10.0 - Override for correctness
     */
    @Override
    public void sort(Comparator<? super T> comparator)
    {
        this.delegate.sort(comparator);
    }

    /**
     * @since 10.0 - Override for backwards compatibility
     */
    @Override
    public ArrayListAdapter<T> sortThis(Comparator<? super T> comparator)
    {
        return (ArrayListAdapter<T>) super.sortThis(comparator);
    }

    /**
     * @since 10.0 - Override for backwards compatibility
     */
    @Override
    public ArrayListAdapter<T> sortThis()
    {
        return (ArrayListAdapter<T>) super.sortThis();
    }

    @Override
    public ArrayListAdapter<T> with(T element)
    {
        this.add(element);
        return this;
    }

    public ArrayListAdapter<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public ArrayListAdapter<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public ArrayListAdapter<T> with(T... elements)
    {
        ArrayIterate.forEach(elements, CollectionAddProcedure.on(this.delegate));
        return this;
    }

    @Override
    public ArrayListAdapter<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public ArrayListAdapter<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    public ArrayListAdapter<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    private <E> ArrayListAdapter<E> wrap(ArrayList<E> list)
    {
        return ArrayListAdapter.adapt(list);
    }

    @Override
    public ArrayListAdapter<T> select(Predicate<? super T> predicate)
    {
        return this.wrap(ArrayListIterate.select(this.delegate, predicate));
    }

    @Override
    public ArrayListAdapter<T> reject(Predicate<? super T> predicate)
    {
        return this.wrap(ArrayListIterate.reject(this.delegate, predicate));
    }

    @Override
    public PartitionMutableList<T> partition(Predicate<? super T> predicate)
    {
        return ArrayListIterate.partition(this.delegate, predicate);
    }

    @Override
    public <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ArrayListIterate.partitionWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return ArrayListIterate.selectInstancesOf(this.delegate, clazz);
    }

    @Override
    public <V> ArrayListAdapter<V> collect(Function<? super T, ? extends V> function)
    {
        return this.wrap(ArrayListIterate.collect(this.delegate, function));
    }

    @Override
    public <V> ArrayListAdapter<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.wrap(ArrayListIterate.flatCollect(this.delegate, function));
    }

    @Override
    public <V> ArrayListAdapter<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return this.wrap(ArrayListIterate.collectIf(this.delegate, predicate, function));
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return ArrayListIterate.detectIndex(this.delegate, predicate);
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        return ArrayListIterate.detectLastIndex(this.delegate, predicate);
    }

    @Override
    public <V> FastListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return ArrayListIterate.groupBy(this.delegate, function);
    }

    @Override
    public <V> FastListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return ArrayListIterate.groupByEach(this.delegate, function);
    }

    @Override
    public <P> ArrayListAdapter<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.wrap(ArrayListIterate.selectWith(this.delegate, predicate, parameter));
    }

    @Override
    public <P> ArrayListAdapter<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.wrap(ArrayListIterate.rejectWith(this.delegate, predicate, parameter));
    }

    @Override
    public <P, A> ArrayListAdapter<A> collectWith(Function2<? super T, ? super P, ? extends A> function, P parameter)
    {
        return this.wrap(ArrayListIterate.collectWith(this.delegate, function, parameter));
    }

    @Override
    public ArrayListAdapter<T> distinct()
    {
        return this.wrap(ArrayListIterate.distinct(this.delegate));
    }

    @Override
    public ArrayListAdapter<T> distinct(HashingStrategy<? super T> hashingStrategy)
    {
        return this.wrap(ArrayListIterate.distinct(this.delegate, hashingStrategy));
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> ArrayListAdapter<T> distinctBy(Function<? super T, ? extends V> function)
    {
        return this.wrap(ArrayListIterate.distinctBy(this.delegate, function));
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return ArrayListIterate.zip(this.delegate, that);
    }

    @Override
    public MutableList<Pair<T, Integer>> zipWithIndex()
    {
        return ArrayListIterate.zipWithIndex(this.delegate);
    }

    @Override
    public MutableList<T> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return ArrayListIterate.take(this.delegate, count, Lists.mutable.withInitialCapacity(Math.min(this.size(), count)));
    }

    @Override
    public MutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        return ArrayListIterate.takeWhile(this.delegate, predicate);
    }

    @Override
    public MutableList<T> drop(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return ArrayListIterate.drop(this.delegate, count, Lists.mutable.withInitialCapacity(this.size() - Math.min(this.size(), count)));
    }

    @Override
    public MutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        return ArrayListIterate.dropWhile(this.delegate, predicate);
    }

    @Override
    public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        return ArrayListIterate.partitionWhile(this.delegate, predicate);
    }
}
