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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.ImmutableList;
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
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.AbstractUnmodifiableMutableCollection;
import org.eclipse.collections.impl.collection.mutable.UnmodifiableCollectionSerializationProxy;
import org.eclipse.collections.impl.lazy.ReverseIterable;

/**
 * An unmodifiable view of a list.
 *
 * @see MutableList#asUnmodifiable()
 */
public class UnmodifiableMutableList<T>
        extends AbstractUnmodifiableMutableCollection<T>
        implements MutableList<T>, Serializable
{
    private UnmodifiableMutableList(MutableList<? extends T> mutableList)
    {
        super(mutableList);
    }

    /**
     * This method will take a MutableList and wrap it directly in a UnmodifiableMutableList. It will
     * take any other non-Eclipse-Collections list and first adapt it will a ListAdapter, and then return a
     * UnmodifiableMutableList that wraps the adapter.
     */
    public static <E, L extends List<E>> UnmodifiableMutableList<E> of(L list)
    {
        if (list == null)
        {
            throw new IllegalArgumentException("cannot create an UnmodifiableMutableList for null");
        }
        if (list instanceof RandomAccess)
        {
            return new RandomAccessUnmodifiableMutableList<>(RandomAccessListAdapter.adapt(list));
        }
        return new UnmodifiableMutableList<>(ListAdapter.adapt(list));
    }

    protected MutableList<T> getMutableList()
    {
        return (MutableList<T>) this.getMutableCollection();
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.getMutableList().equals(obj);
    }

    @Override
    public int hashCode()
    {
        return this.getMutableList().hashCode();
    }

    @Override
    public MutableList<T> asUnmodifiable()
    {
        return this;
    }

    @Override
    public ImmutableList<T> toImmutable()
    {
        return this.getMutableList().toImmutable();
    }

    @Override
    public MutableList<T> asSynchronized()
    {
        return SynchronizedMutableList.of(this);
    }

    @Override
    public UnmodifiableMutableList<T> clone()
    {
        return this;
    }

    @Override
    public MutableList<T> newEmpty()
    {
        return this.getMutableList().newEmpty();
    }

    @Override
    public MutableList<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return this.getMutableList().corresponds(other, predicate);
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        this.getMutableList().forEach(fromIndex, toIndex, procedure);
    }

    @Override
    public void reverseForEach(Procedure<? super T> procedure)
    {
        this.getMutableList().reverseForEach(procedure);
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        this.getMutableList().reverseForEachWithIndex(procedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.getMutableList().forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public UnmodifiableMutableList<T> sortThis(Comparator<? super T> comparator)
    {
        throw new UnsupportedOperationException("Cannot call sortThis() on " + this.getClass().getSimpleName());
    }

    @Override
    public UnmodifiableMutableList<T> sortThis()
    {
        throw new UnsupportedOperationException("Cannot call sortThis() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> toReversed()
    {
        return this.getMutableList().toReversed();
    }

    @Override
    public MutableList<T> reverseThis()
    {
        throw new UnsupportedOperationException("Cannot call reverseThis() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> shuffleThis()
    {
        throw new UnsupportedOperationException("Cannot call shuffleThis() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> shuffleThis(Random rnd)
    {
        throw new UnsupportedOperationException("Cannot call shuffleThis() on " + this.getClass().getSimpleName());
    }

    @Override
    public <V extends Comparable<? super V>> MutableList<T> sortThisBy(Function<? super T, ? extends V> function)
    {
        throw new UnsupportedOperationException("Cannot call sortThisBy() on " + this.getClass().getSimpleName());
    }

    /**
     * @since 10.0 - Overridden for correctness
     */
    @Override
    public void replaceAll(UnaryOperator<T> operator)
    {
        throw new UnsupportedOperationException("Cannot call replaceAll() on " + this.getClass().getSimpleName());
    }

    /**
     * @since 10.0 - Overridden for correctness
     */
    @Override
    public void sort(Comparator<? super T> comparator)
    {
        throw new UnsupportedOperationException("Cannot call sort() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> sortThisByInt(IntFunction<? super T> function)
    {
        throw new UnsupportedOperationException("Cannot call sortThisByInt() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> sortThisByBoolean(BooleanFunction<? super T> function)
    {
        throw new UnsupportedOperationException("Cannot call sortThisByBoolean() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> sortThisByChar(CharFunction<? super T> function)
    {
        throw new UnsupportedOperationException("Cannot call sortThisByChar() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> sortThisByByte(ByteFunction<? super T> function)
    {
        throw new UnsupportedOperationException("Cannot call sortThisByByte() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> sortThisByShort(ShortFunction<? super T> function)
    {
        throw new UnsupportedOperationException("Cannot call sortThisByShort() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> sortThisByFloat(FloatFunction<? super T> function)
    {
        throw new UnsupportedOperationException("Cannot call sortThisByFloat() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> sortThisByLong(LongFunction<? super T> function)
    {
        throw new UnsupportedOperationException("Cannot call sortThisByLong() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> sortThisByDouble(DoubleFunction<? super T> function)
    {
        throw new UnsupportedOperationException("Cannot call sortThisByDouble() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public T get(int index)
    {
        return this.getMutableList().get(index);
    }

    @Override
    public T set(int index, T element)
    {
        throw new UnsupportedOperationException("Cannot call set() on " + this.getClass().getSimpleName());
    }

    @Override
    public void add(int index, T element)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }

    @Override
    public T remove(int index)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    @Override
    public int indexOf(Object o)
    {
        return this.getMutableList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return this.getMutableList().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator()
    {
        return this.listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index)
    {
        return new UnmodifiableListIteratorAdapter<>(this.getMutableList().listIterator(index));
    }

    @Override
    public UnmodifiableMutableList<T> subList(int fromIndex, int toIndex)
    {
        MutableList<T> subList = this.getMutableList().subList(fromIndex, toIndex);
        return UnmodifiableMutableList.of(subList);
    }

    @Override
    public <P, A> MutableList<A> collectWith(Function2<? super T, ? super P, ? extends A> function, P parameter)
    {
        return this.getMutableList().collectWith(function, parameter);
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return this.getMutableList().collect(function);
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.getMutableList().collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.getMutableList().collectByte(byteFunction);
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        return this.getMutableList().collectChar(charFunction);
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.getMutableList().collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.getMutableList().collectFloat(floatFunction);
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        return this.getMutableList().collectInt(intFunction);
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        return this.getMutableList().collectLong(longFunction);
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.getMutableList().collectShort(shortFunction);
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getMutableList().flatCollect(function);
    }

    @Override
    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return this.getMutableList().collectIf(predicate, function);
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return this.getMutableList().detectIndex(predicate);
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        return this.getMutableList().detectLastIndex(predicate);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.getMutableList().groupBy(function);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getMutableList().groupByEach(function);
    }

    @Override
    public MutableList<T> reject(Predicate<? super T> predicate)
    {
        return this.getMutableList().reject(predicate);
    }

    @Override
    public <P> MutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableList().rejectWith(predicate, parameter);
    }

    @Override
    public MutableList<T> select(Predicate<? super T> predicate)
    {
        return this.getMutableList().select(predicate);
    }

    @Override
    public <P> MutableList<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableList().selectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableList<T> partition(Predicate<? super T> predicate)
    {
        return this.getMutableList().partition(predicate);
    }

    @Override
    public <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableList().partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return this.getMutableList().selectInstancesOf(clazz);
    }

    @Override
    public MutableList<T> distinct()
    {
        return this.getMutableList().distinct();
    }

    @Override
    public MutableList<T> distinct(HashingStrategy<? super T> hashingStrategy)
    {
        return this.getMutableList().distinct(hashingStrategy);
    }

    /**
     * @since 9.0.
     */
    @Override
    public <V> MutableList<T> distinctBy(Function<? super T, ? extends V> function)
    {
        return this.getMutableList().distinctBy(function);
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.getMutableList().zip(that);
    }

    @Override
    public MutableList<Pair<T, Integer>> zipWithIndex()
    {
        return this.getMutableList().zipWithIndex();
    }

    @Override
    public MutableList<T> take(int count)
    {
        return this.getMutableList().take(count);
    }

    @Override
    public MutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        return this.getMutableList().takeWhile(predicate);
    }

    @Override
    public MutableList<T> drop(int count)
    {
        return this.getMutableList().drop(count);
    }

    @Override
    public MutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        return this.getMutableList().dropWhile(predicate);
    }

    @Override
    public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        return this.getMutableList().partitionWhile(predicate);
    }

    @Override
    public LazyIterable<T> asReversed()
    {
        return ReverseIterable.adapt(this);
    }

    public UnmodifiableMutableList<T> asReversedList()
    {
        return new RandomAccessReversedUnmodifiableMutableList<>(this.getMutableList());
    }

    @Override
    public ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return this.getMutableList().asParallel(executorService, batchSize);
    }

    @Override
    public MutableList<T> with(T element)
    {
        throw new UnsupportedOperationException("Cannot call with() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> without(T element)
    {
        throw new UnsupportedOperationException("Cannot call without() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> withAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableList<T> withoutAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withoutAll() on " + this.getClass().getSimpleName());
    }

    private static final class RandomAccessUnmodifiableMutableList<T> extends UnmodifiableMutableList<T> implements RandomAccess
    {
        private static final long serialVersionUID = 1L;

        RandomAccessUnmodifiableMutableList(MutableList<? extends T> mutableList)
        {
            super(mutableList);
        }

        @Override
        public RandomAccessUnmodifiableMutableList<T> clone()
        {
            return this;
        }
    }

    private static final class RandomAccessReversedUnmodifiableMutableList<T>
            extends UnmodifiableMutableList<T>
            implements RandomAccess
    {
        private static final long serialVersionUID = 1L;

        private RandomAccessReversedUnmodifiableMutableList(MutableList<? extends T> mutableList)
        {
            super(mutableList);
        }

        public UnmodifiableMutableList<T> asReversedList()
        {
            return new UnmodifiableMutableList<>(this.getMutableList());
        }

        @Override
        public T get(int index)
        {
            return this.getMutableList().get(this.reverseIndex(index));
        }

        private int reverseIndex(int index)
        {
            return this.size() - 1 - index;
        }

        @Override
        public Iterator<T> iterator()
        {
            return ReverseIterable.adapt(this.getMutableList()).iterator();
        }

        @Override
        public RandomAccessReversedUnmodifiableMutableList<T> clone()
        {
            return this;
        }
    }

    protected Object writeReplace()
    {
        return new UnmodifiableCollectionSerializationProxy<>(this.getMutableList());
    }
}
