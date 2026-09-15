/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Random;
import java.util.RandomAccess;
import java.util.concurrent.ExecutorService;
import java.util.function.UnaryOperator;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.ParallelListIterable;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.AbstractSynchronizedMutableCollection;
import org.eclipse.collections.impl.collection.mutable.SynchronizedCollectionSerializationProxy;
import org.eclipse.collections.impl.lazy.ReverseIterable;
import org.eclipse.collections.impl.lazy.parallel.list.SynchronizedParallelListIterable;

/**
 * A synchronized view of a {@link MutableList}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableList#asSynchronized()
 */
public class SynchronizedMutableList<T>
        extends AbstractSynchronizedMutableCollection<T>
        implements MutableList<T>, Serializable
{
    private static final long serialVersionUID = 2L;

    SynchronizedMutableList(MutableList<T> newCollection)
    {
        super(newCollection);
    }

    SynchronizedMutableList(MutableList<T> newCollection, Object newLock)
    {
        super(newCollection, newLock);
    }

    /**
     * This method will take a MutableList and wrap it directly in a SynchronizedMutableList. It will
     * take any other non-Eclipse-Collections collection and first adapt it will a ListAdapter, and then return a
     * SynchronizedMutableList that wraps the adapter.
     */
    public static <E, L extends List<E>> SynchronizedMutableList<E> of(L list)
    {
        MutableList<E> mutableList =
                list instanceof MutableList ? (MutableList<E>) list : ListAdapter.adapt(list);
        return new SynchronizedMutableList<>(mutableList);
    }

    /**
     * This method will take a MutableList and wrap it directly in a SynchronizedMutableList. It will
     * take any other non-Eclipse-Collections collection and first adapt it will a ListAdapter, and then return a
     * SynchronizedMutableList that wraps the adapter. Additionally, a developer specifies which lock to use
     * with the collection.
     */
    public static <E, L extends List<E>> SynchronizedMutableList<E> of(L list, Object lock)
    {
        MutableList<E> mutableList =
                list instanceof MutableList ? (MutableList<E>) list : ListAdapter.adapt(list);
        return new SynchronizedMutableList<>(mutableList, lock);
    }

    @Override
    protected MutableList<T> getDelegate()
    {
        return (MutableList<T>) super.getDelegate();
    }

    @Override
    public MutableList<T> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().newEmpty().asSynchronized();
        }
    }

    @Override
    public MutableList<T> clone()
    {
        synchronized (this.getLock())
        {
            return SynchronizedMutableList.of(this.getDelegate().clone());
        }
    }

    protected Object writeReplace()
    {
        return new SynchronizedCollectionSerializationProxy<>(this.getDelegate());
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().addAll(index, collection);
        }
    }

    @Override
    public T set(int index, T element)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().set(index, element);
        }
    }

    @Override
    public void add(int index, T element)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().add(index, element);
        }
    }

    @Override
    public T remove(int index)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().remove(index);
        }
    }

    @Override
    public MutableList<T> subList(int fromIndex, int toIndex)
    {
        synchronized (this.getLock())
        {
            return SynchronizedMutableList.of(this.getDelegate().subList(fromIndex, toIndex), this.getLock());
        }
    }

    @Override
    public T get(int index)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().get(index);
        }
    }

    /**
     * @since 10.0 - Overridden for correctness
     */
    @Override
    public void replaceAll(UnaryOperator<T> operator)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().replaceAll(operator);
        }
    }

    /**
     * @since 10.0 - Overridden for correctness
     */
    @Override
    public void sort(Comparator<? super T> comparator)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sort(comparator);
        }
    }

    @Override
    public Optional<T> getFirstOptional()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().getFirstOptional();
        }
    }

    @Override
    public Optional<T> getLastOptional()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().getLastOptional();
        }
    }

    @Override
    public int lastIndexOf(Object o)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().lastIndexOf(o);
        }
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

    @Override
    public ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new SynchronizedParallelListIterable<>(this.getDelegate().asParallel(executorService, batchSize), this.getLock());
    }

    @Override
    public int binarySearch(T key, Comparator<? super T> comparator)
    {
        synchronized (this.getLock())
        {
            return Collections.binarySearch(this, key, comparator);
        }
    }

    @Override
    public int binarySearch(T key)
    {
        synchronized (this.getLock())
        {
            return Collections.binarySearch((List<? extends Comparable<? super T>>) this, key);
        }
    }

    @Override
    public int indexOf(Object o)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().indexOf(o);
        }
    }

    @Override
    public MutableList<T> distinct()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().distinct();
        }
    }

    @Override
    public MutableList<T> distinct(HashingStrategy<? super T> hashingStrategy)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().distinct(hashingStrategy);
        }
    }

    /**
     * @since 9.0.
     */
    @Override
    public <V> MutableList<T> distinctBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().distinctBy(function);
        }
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().corresponds(other, predicate);
        }
    }

    @Override
    public <T2> void forEachInBoth(
            ListIterable<T2> other, Procedure2<? super T, ? super T2> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEachInBoth(other, procedure);
        }
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEach(fromIndex, toIndex, procedure);
        }
    }

    @Override
    public MutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().takeWhile(predicate);
        }
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
        }
    }

    @Override
    public MutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().dropWhile(predicate);
        }
    }

    @Override
    public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partitionWhile(predicate);
        }
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().detectIndex(predicate);
        }
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().detectLastIndex(predicate);
        }
    }

    @Override
    public MutableList<T> take(int count)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().take(count);
        }
    }

    @Override
    public MutableList<T> drop(int count)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().drop(count);
        }
    }

    @Override
    public void reverseForEach(Procedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().reverseForEach(procedure);
        }
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().reverseForEachWithIndex(procedure);
        }
    }

    @Override
    public MutableList<T> sortThis(Comparator<? super T> comparator)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThis(comparator);
            return this;
        }
    }

    @Override
    public MutableList<T> sortThis()
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThis();
            return this;
        }
    }

    @Override
    public <V extends Comparable<? super V>> MutableList<T> sortThisBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisBy(function);
            return this;
        }
    }

    @Override
    public MutableList<T> sortThisByInt(IntFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByInt(function);
            return this;
        }
    }

    @Override
    public MutableList<T> sortThisByBoolean(BooleanFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByBoolean(function);
            return this;
        }
    }

    @Override
    public MutableList<T> sortThisByChar(CharFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByChar(function);
            return this;
        }
    }

    @Override
    public MutableList<T> sortThisByByte(ByteFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByByte(function);
            return this;
        }
    }

    @Override
    public MutableList<T> sortThisByShort(ShortFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByShort(function);
            return this;
        }
    }

    @Override
    public MutableList<T> sortThisByFloat(FloatFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByFloat(function);
            return this;
        }
    }

    @Override
    public MutableList<T> sortThisByLong(LongFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByLong(function);
            return this;
        }
    }

    @Override
    public MutableList<T> sortThisByDouble(DoubleFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByDouble(function);
            return this;
        }
    }

    @Override
    public MutableList<T> reverseThis()
    {
        synchronized (this.getLock())
        {
            this.getDelegate().reverseThis();
            return this;
        }
    }

    @Override
    public MutableList<T> shuffleThis()
    {
        synchronized (this.getLock())
        {
            this.getDelegate().shuffleThis();
            return this;
        }
    }

    @Override
    public MutableList<T> shuffleThis(Random rnd)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().shuffleThis(rnd);
            return this;
        }
    }

    @Override
    public LazyIterable<T> asReversed()
    {
        synchronized (this.getLock())
        {
            return ReverseIterable.adapt(this);
        }
    }

    @Override
    public MutableList<T> reversed()
    {
        synchronized (this.getLock())
        {
            if (this instanceof RandomAccess)
            {
                return new ReversedRandomAccessMutableList<>(this);
            }
            return new ReversedMutableList<>(this);
        }
    }

    @Override
    public MutableList<T> toReversed()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toReversed();
        }
    }

    @Override
    public MutableStack<T> toStack()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toStack();
        }
    }

    @Override
    public ImmutableList<T> toImmutable()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toImmutable();
        }
    }

    @Override
    public MutableList<T> tap(Procedure<? super T> procedure)
    {
        return (MutableList<T>) super.tap(procedure);
    }

    @Override
    public MutableList<T> select(Predicate<? super T> predicate)
    {
        return (MutableList<T>) super.select(predicate);
    }

    @Override
    public <P> MutableList<T> selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return (MutableList<T>) super.selectWith(predicate, parameter);
    }

    @Override
    public MutableList<T> reject(Predicate<? super T> predicate)
    {
        return (MutableList<T>) super.reject(predicate);
    }

    @Override
    public <P> MutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (MutableList<T>) super.rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableList<T> partition(Predicate<? super T> predicate)
    {
        return (PartitionMutableList<T>) super.partition(predicate);
    }

    @Override
    public <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (PartitionMutableList<T>) super.partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return (MutableList<S>) super.selectInstancesOf(clazz);
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return (MutableBooleanList) super.collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        return (MutableByteList) super.collectByte(byteFunction);
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        return (MutableCharList) super.collectChar(charFunction);
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return (MutableDoubleList) super.collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        return (MutableFloatList) super.collectFloat(floatFunction);
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        return (MutableIntList) super.collectInt(intFunction);
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        return (MutableLongList) super.collectLong(longFunction);
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        return (MutableShortList) super.collectShort(shortFunction);
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return (MutableList<V>) super.<V>collect(function);
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> MutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWithIndex(function);
        }
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWithIndex(function, target);
        }
    }

    /**
     * @since 11.0.
     */
    @Override
    public MutableList<T> selectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectWithIndex(predicate);
        }
    }

    /**
     * @since 11.0.
     */
    @Override
    public MutableList<T> rejectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().rejectWithIndex(predicate);
        }
    }

    /**
     * @since 11.0.
     */
    @Override
    public <R extends Collection<T>> R selectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectWithIndex(predicate, target);
        }
    }

    /**
     * @since 11.0.
     */
    @Override
    public <R extends Collection<T>> R rejectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().rejectWithIndex(predicate, target);
        }
    }

    @Override
    public <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return (MutableList<V>) super.<P, V>collectWith(function, parameter);
    }

    @Override
    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return (MutableList<V>) super.<V>collectIf(predicate, function);
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableList<V>) super.flatCollect(function);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return (MutableListMultimap<V, T>) super.<V>groupBy(function);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableListMultimap<V, T>) super.groupByEach(function);
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return (MutableList<Pair<T, S>>) super.zip(that);
    }

    @Override
    public MutableList<Pair<T, Integer>> zipWithIndex()
    {
        return (MutableList<Pair<T, Integer>>) super.zipWithIndex();
    }

    @Override
    public MutableList<T> asUnmodifiable()
    {
        synchronized (this.getLock())
        {
            return UnmodifiableMutableList.of(this);
        }
    }

    @Override
    public MutableList<T> asSynchronized()
    {
        return this;
    }
}
