/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.LazyIterable;
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
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.sorted.PartitionMutableSortedSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.ParallelSortedSetIterable;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.AbstractSynchronizedMutableCollection;
import org.eclipse.collections.impl.collection.mutable.SynchronizedCollectionSerializationProxy;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.SynchronizedParallelSortedSetIterable;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;

/**
 * A synchronized view of a {@link MutableSortedSet}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableSortedSet#asSynchronized()
 */
public class SynchronizedSortedSet<T>
        extends AbstractSynchronizedMutableCollection<T>
        implements MutableSortedSet<T>, Serializable
{
    private static final long serialVersionUID = 2L;

    SynchronizedSortedSet(MutableSortedSet<T> set)
    {
        super(set);
    }

    SynchronizedSortedSet(MutableSortedSet<T> set, Object newLock)
    {
        super(set, newLock);
    }

    /**
     * This method will take a MutableSortedSet and wrap it directly in a SynchronizedSortedSet. It will
     * take any other non-Eclipse-Collections collection and first adapt it will a SortedSetAdapter, and then return a
     * SynchronizedSortedSet that wraps the adapter.
     */
    public static <E, S extends SortedSet<E>> SynchronizedSortedSet<E> of(S set)
    {
        return new SynchronizedSortedSet<>(SortedSetAdapter.adapt(set));
    }

    /**
     * This method will take a MutableSortedSet and wrap it directly in a SynchronizedSortedSet. It will
     * take any other non-Eclipse-Collections collection and first adapt it will a SortedSetAdapter, and then return a
     * SynchronizedSortedSet that wraps the adapter. Additionally, a developer specifies which lock to use
     * with the collection.
     */
    public static <E, S extends SortedSet<E>> MutableSortedSet<E> of(S set, Object lock)
    {
        return new SynchronizedSortedSet<>(SortedSetAdapter.adapt(set), lock);
    }

    @Override
    protected MutableSortedSet<T> getDelegate()
    {
        return (MutableSortedSet<T>) super.getDelegate();
    }

    @Override
    public MutableSortedSet<T> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().newEmpty().asSynchronized();
        }
    }

    @Override
    public MutableSortedSet<T> clone()
    {
        synchronized (this.getLock())
        {
            return SynchronizedSortedSet.of(this.getDelegate().clone());
        }
    }

    protected Object writeReplace()
    {
        return new SynchronizedCollectionSerializationProxy<>(this.getDelegate());
    }

    @Override
    public Comparator<? super T> comparator()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().comparator();
        }
    }

    @Override
    public int compareTo(SortedSetIterable<T> o)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().compareTo(o);
        }
    }

    @Override
    public T first()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().first();
        }
    }

    @Override
    public T last()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().last();
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
    public int indexOf(Object object)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().indexOf(object);
        }
    }

    @Override
    public MutableStack<T> toStack()
    {
        synchronized (this.getLock())
        {
            return ArrayStack.newStack(this);
        }
    }

    @Override
    public PartitionMutableSortedSet<T> partitionWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partitionWhile(predicate);
        }
    }

    @Override
    public MutableSortedSet<T> distinct()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().distinct();
        }
    }

    @Override
    public MutableSortedSet<T> takeWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().takeWhile(predicate);
        }
    }

    @Override
    public MutableSortedSet<T> dropWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().dropWhile(predicate);
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
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEach(startIndex, endIndex, procedure);
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
    public int detectIndex(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().detectIndex(predicate);
        }
    }

    @Override
    public MutableSortedSet<T> tap(Procedure<? super T> procedure)
    {
        return (MutableSortedSet<T>) super.tap(procedure);
    }

    @Override
    public MutableSortedSet<T> select(Predicate<? super T> predicate)
    {
        return (MutableSortedSet<T>) super.select(predicate);
    }

    @Override
    public <P> MutableSortedSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (MutableSortedSet<T>) super.selectWith(predicate, parameter);
    }

    @Override
    public MutableSortedSet<T> reject(Predicate<? super T> predicate)
    {
        return (MutableSortedSet<T>) super.reject(predicate);
    }

    @Override
    public <P> MutableSortedSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (MutableSortedSet<T>) super.rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableSortedSet<T> partition(Predicate<? super T> predicate)
    {
        return (PartitionMutableSortedSet<T>) super.partition(predicate);
    }

    @Override
    public <P> PartitionMutableSortedSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (PartitionMutableSortedSet<T>) super.partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableSortedSet<S> selectInstancesOf(Class<S> clazz)
    {
        return (MutableSortedSet<S>) super.selectInstancesOf(clazz);
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
    public <V> MutableSortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return (MutableSortedSetMultimap<V, T>) super.<V>groupBy(function);
    }

    @Override
    public <V> MutableSortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableSortedSetMultimap<V, T>) super.groupByEach(function);
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return (MutableList<Pair<T, S>>) super.zip(that);
    }

    @Override
    public MutableSortedSet<Pair<T, Integer>> zipWithIndex()
    {
        return (MutableSortedSet<Pair<T, Integer>>) super.zipWithIndex();
    }

    @Override
    public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().unionInto(set, targetSet);
        }
    }

    @Override
    public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().intersectInto(set, targetSet);
        }
    }

    @Override
    public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().differenceInto(subtrahendSet, targetSet);
        }
    }

    @Override
    public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().symmetricDifferenceInto(set, targetSet);
        }
    }

    @Override
    public boolean isSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().isSubsetOf(candidateSuperset);
        }
    }

    @Override
    public boolean isProperSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().isProperSubsetOf(candidateSuperset);
        }
    }

    @Override
    public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().cartesianProduct(set);
        }
    }

    @Override
    public MutableSortedSet<T> union(SetIterable<? extends T> set)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().union(set);
        }
    }

    @Override
    public MutableSortedSet<T> intersect(SetIterable<? extends T> set)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().intersect(set);
        }
    }

    @Override
    public MutableSortedSet<T> difference(SetIterable<? extends T> subtrahendSet)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().difference(subtrahendSet);
        }
    }

    @Override
    public MutableSortedSet<T> symmetricDifference(SetIterable<? extends T> setB)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().symmetricDifference(setB);
        }
    }

    @Override
    public MutableSortedSet<SortedSetIterable<T>> powerSet()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().powerSet();
        }
    }

    @Override
    public ParallelSortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new SynchronizedParallelSortedSetIterable<>(this.getDelegate().asParallel(executorService, batchSize), this);
    }

    @Override
    public ImmutableSortedSet<T> toImmutable()
    {
        synchronized (this.getLock())
        {
            return SortedSets.immutable.withSortedSet(this.getDelegate());
        }
    }

    @Override
    public MutableSortedSet<T> asUnmodifiable()
    {
        synchronized (this.getLock())
        {
            return UnmodifiableSortedSet.of(this);
        }
    }

    @Override
    public MutableSortedSet<T> asSynchronized()
    {
        return this;
    }

    @Override
    public MutableSortedSet<T> subSet(T fromElement, T toElement)
    {
        synchronized (this.getLock())
        {
            return SynchronizedSortedSet.of(this.getDelegate().subSet(fromElement, toElement), this.getLock());
        }
    }

    @Override
    public MutableSortedSet<T> headSet(T toElement)
    {
        synchronized (this.getLock())
        {
            return SynchronizedSortedSet.of(this.getDelegate().headSet(toElement), this.getLock());
        }
    }

    @Override
    public MutableSortedSet<T> tailSet(T fromElement)
    {
        synchronized (this.getLock())
        {
            return SynchronizedSortedSet.of(this.getDelegate().tailSet(fromElement), this.getLock());
        }
    }

    @Override
    public T lower(T e)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().lower(e);
        }
    }

    @Override
    public T floor(T e)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().floor(e);
        }
    }

    @Override
    public T ceiling(T e)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().ceiling(e);
        }
    }

    @Override
    public T higher(T e)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().higher(e);
        }
    }

    @Override
    public T pollFirst()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().pollFirst();
        }
    }

    @Override
    public T pollLast()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().pollLast();
        }
    }

    @Override
    public Iterator<T> descendingIterator()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().descendingIterator();
        }
    }

    @Override
    public MutableSortedSet<T> descendingSet()
    {
        synchronized (this.getLock())
        {
            return SynchronizedSortedSet.of(this.getDelegate().descendingSet(), this.getLock());
        }
    }

    @Override
    public MutableSortedSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive)
    {
        synchronized (this.getLock())
        {
            return SynchronizedSortedSet.of(this.getDelegate().subSet(fromElement, fromInclusive, toElement, toInclusive), this.getLock());
        }
    }

    @Override
    public MutableSortedSet<T> headSet(T toElement, boolean inclusive)
    {
        synchronized (this.getLock())
        {
            return SynchronizedSortedSet.of(this.getDelegate().headSet(toElement, inclusive), this.getLock());
        }
    }

    @Override
    public MutableSortedSet<T> tailSet(T fromElement, boolean inclusive)
    {
        synchronized (this.getLock())
        {
            return SynchronizedSortedSet.of(this.getDelegate().tailSet(fromElement, inclusive), this.getLock());
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
    public MutableSortedSet<T> toReversed()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toReversed();
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
    public MutableSortedSet<T> take(int count)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().take(count);
        }
    }

    @Override
    public MutableSortedSet<T> drop(int count)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().drop(count);
        }
    }
}
