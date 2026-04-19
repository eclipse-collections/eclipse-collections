/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
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
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.ParallelUnsortedSetIterable;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.UnsortedSetIterable;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.set.primitive.MutableByteSet;
import org.eclipse.collections.api.set.primitive.MutableCharSet;
import org.eclipse.collections.api.set.primitive.MutableDoubleSet;
import org.eclipse.collections.api.set.primitive.MutableFloatSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.api.set.primitive.MutableShortSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.AbstractSynchronizedMutableCollection;
import org.eclipse.collections.impl.collection.mutable.SynchronizedCollectionSerializationProxy;
import org.eclipse.collections.impl.lazy.parallel.set.SynchronizedParallelUnsortedSetIterable;

/**
 * A synchronized view of a {@link MutableSet}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableSet#asSynchronized()
 */
public class SynchronizedMutableSet<T>
        extends AbstractSynchronizedMutableCollection<T>
        implements MutableSet<T>, Serializable
{
    private static final long serialVersionUID = 2L;

    protected SynchronizedMutableSet(MutableSet<T> set)
    {
        super(set);
    }

    protected SynchronizedMutableSet(MutableSet<T> set, Object newLock)
    {
        super(set, newLock);
    }

    /**
     * This method will take a MutableSet and wrap it directly in a SynchronizedMutableSet. It will
     * take any other non-Eclipse-Collections collection and first adapt it will a SetAdapter, and then return a
     * SynchronizedMutableSet that wraps the adapter.
     */
    public static <E, S extends Set<E>> SynchronizedMutableSet<E> of(S set)
    {
        return new SynchronizedMutableSet<>(SetAdapter.adapt(set));
    }

    /**
     * This method will take a MutableSet and wrap it directly in a SynchronizedMutableSet. It will
     * take any other non-Eclipse-Collections collection and first adapt it will a SetAdapter, and then return a
     * SynchronizedMutableSet that wraps the adapter. Additionally, a developer specifies which lock to use
     * with the collection.
     */
    public static <E, S extends Set<E>> SynchronizedMutableSet<E> of(S set, Object lock)
    {
        return new SynchronizedMutableSet<>(SetAdapter.adapt(set), lock);
    }

    private MutableSet<T> getMutableSet()
    {
        return (MutableSet<T>) this.getDelegate();
    }

    @Override
    public MutableSet<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    public MutableSet<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public MutableSet<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    public MutableSet<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    public MutableSet<T> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().newEmpty().asSynchronized();
        }
    }

    @Override
    public MutableSet<T> clone()
    {
        synchronized (this.getLock())
        {
            return SynchronizedMutableSet.of(this.getMutableSet().clone());
        }
    }

    protected Object writeReplace()
    {
        return new SynchronizedCollectionSerializationProxy<>(this.getMutableSet());
    }

    @Override
    public MutableSet<T> tap(Procedure<? super T> procedure)
    {
        return (MutableSet<T>) super.tap(procedure);
    }

    @Override
    public MutableSet<T> select(Predicate<? super T> predicate)
    {
        return (MutableSet<T>) super.select(predicate);
    }

    @Override
    public <P> MutableSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (MutableSet<T>) super.selectWith(predicate, parameter);
    }

    @Override
    public MutableSet<T> reject(Predicate<? super T> predicate)
    {
        return (MutableSet<T>) super.reject(predicate);
    }

    @Override
    public <P> MutableSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (MutableSet<T>) super.rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableSet<T> partition(Predicate<? super T> predicate)
    {
        return (PartitionMutableSet<T>) super.partition(predicate);
    }

    @Override
    public <P> PartitionMutableSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (PartitionMutableSet<T>) super.partitionWith(predicate, parameter);
    }

    @Override
    public MutableBooleanSet collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return (MutableBooleanSet) super.collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteSet collectByte(ByteFunction<? super T> byteFunction)
    {
        return (MutableByteSet) super.collectByte(byteFunction);
    }

    @Override
    public MutableCharSet collectChar(CharFunction<? super T> charFunction)
    {
        return (MutableCharSet) super.collectChar(charFunction);
    }

    @Override
    public MutableDoubleSet collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return (MutableDoubleSet) super.collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatSet collectFloat(FloatFunction<? super T> floatFunction)
    {
        return (MutableFloatSet) super.collectFloat(floatFunction);
    }

    @Override
    public MutableIntSet collectInt(IntFunction<? super T> intFunction)
    {
        return (MutableIntSet) super.collectInt(intFunction);
    }

    @Override
    public MutableLongSet collectLong(LongFunction<? super T> longFunction)
    {
        return (MutableLongSet) super.collectLong(longFunction);
    }

    @Override
    public MutableShortSet collectShort(ShortFunction<? super T> shortFunction)
    {
        return (MutableShortSet) super.collectShort(shortFunction);
    }

    @Override
    public <S> MutableSet<S> selectInstancesOf(Class<S> clazz)
    {
        return (MutableSet<S>) super.selectInstancesOf(clazz);
    }

    @Override
    public <V> MutableSet<V> collect(Function<? super T, ? extends V> function)
    {
        return (MutableSet<V>) super.<V>collect(function);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public MutableSet<Pair<T, Integer>> zipWithIndex()
    {
        return (MutableSet<Pair<T, Integer>>) super.zipWithIndex();
    }

    @Override
    public <P, V> MutableSet<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return (MutableSet<V>) super.<P, V>collectWith(function, parameter);
    }

    @Override
    public <V> MutableSet<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return (MutableSet<V>) super.<V>collectIf(predicate, function);
    }

    @Override
    public <V> MutableSet<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableSet<V>) super.flatCollect(function);
    }

    @Override
    public <V> MutableSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return (MutableSetMultimap<V, T>) super.<V>groupBy(function);
    }

    @Override
    public <V> MutableSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableSetMultimap<V, T>) super.groupByEach(function);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> MutableSet<Pair<T, S>> zip(Iterable<S> that)
    {
        return (MutableSet<Pair<T, S>>) super.zip(that);
    }

    @Override
    public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().unionInto(set, targetSet);
        }
    }

    @Override
    public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().intersectInto(set, targetSet);
        }
    }

    @Override
    public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().differenceInto(subtrahendSet, targetSet);
        }
    }

    @Override
    public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().symmetricDifferenceInto(set, targetSet);
        }
    }

    @Override
    public boolean isSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().isSubsetOf(candidateSuperset);
        }
    }

    @Override
    public boolean isProperSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().isProperSubsetOf(candidateSuperset);
        }
    }

    @Override
    public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().cartesianProduct(set);
        }
    }

    @Override
    public MutableSet<T> union(SetIterable<? extends T> set)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().union(set);
        }
    }

    @Override
    public MutableSet<T> intersect(SetIterable<? extends T> set)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().intersect(set);
        }
    }

    @Override
    public MutableSet<T> difference(SetIterable<? extends T> subtrahendSet)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().difference(subtrahendSet);
        }
    }

    @Override
    public MutableSet<T> symmetricDifference(SetIterable<? extends T> setB)
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().symmetricDifference(setB);
        }
    }

    @Override
    public MutableSet<UnsortedSetIterable<T>> powerSet()
    {
        synchronized (this.getLock())
        {
            return this.getMutableSet().powerSet();
        }
    }

    @Override
    public ParallelUnsortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new SynchronizedParallelUnsortedSetIterable<>(this.getMutableSet().asParallel(executorService, batchSize), this.getLock());
    }

    @Override
    public MutableSet<T> asUnmodifiable()
    {
        synchronized (this.getLock())
        {
            return UnmodifiableMutableSet.of(this);
        }
    }

    @Override
    public MutableSet<T> asSynchronized()
    {
        return this;
    }

    @Override
    public ImmutableSet<T> toImmutable()
    {
        synchronized (this.getLock())
        {
            return Sets.immutable.withAll(this.getMutableSet());
        }
    }
}
