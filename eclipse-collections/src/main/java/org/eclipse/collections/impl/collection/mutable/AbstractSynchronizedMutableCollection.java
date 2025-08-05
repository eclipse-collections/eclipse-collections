/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable;

import java.util.Collection;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.Function3;
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
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.collection.AbstractSynchronizedRichIterable;

public abstract class AbstractSynchronizedMutableCollection<T>
        extends AbstractSynchronizedRichIterable<T> implements MutableCollection<T>
{
    protected AbstractSynchronizedMutableCollection(MutableCollection<T> delegate)
    {
        this(delegate, null);
    }

    protected AbstractSynchronizedMutableCollection(MutableCollection<T> delegate, Object lock)
    {
        super(delegate, lock);
    }

    @Override
    protected MutableCollection<T> getDelegate()
    {
        return (MutableCollection<T>) super.getDelegate();
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<T> select(Predicate<? super T> predicate)
    {
        return (MutableCollection<T>) super.select(predicate);
    }

    /**
     * @since 9.0
     */
    @Override
    public <S> MutableCollection<S> selectInstancesOf(Class<S> clazz)
    {
        return (MutableCollection<S>) super.selectInstancesOf(clazz);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<T> reject(Predicate<? super T> predicate)
    {
        return (MutableCollection<T>) super.reject(predicate);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> MutableCollection<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableCollection<V>) super.flatCollect(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> MutableCollection<V> collect(Function<? super T, ? extends V> function)
    {
        return (MutableCollection<V>) super.<V>collect(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <P, V> MutableCollection<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return (MutableCollection<V>) super.<P, V>collectWith(function, parameter);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableBooleanCollection collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return (MutableBooleanCollection) super.collectBoolean(booleanFunction);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableByteCollection collectByte(ByteFunction<? super T> byteFunction)
    {
        return (MutableByteCollection) super.collectByte(byteFunction);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCharCollection collectChar(CharFunction<? super T> charFunction)
    {
        return (MutableCharCollection) super.collectChar(charFunction);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableDoubleCollection collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return (MutableDoubleCollection) super.collectDouble(doubleFunction);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableFloatCollection collectFloat(FloatFunction<? super T> floatFunction)
    {
        return (MutableFloatCollection) super.collectFloat(floatFunction);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableIntCollection collectInt(IntFunction<? super T> intFunction)
    {
        return (MutableIntCollection) super.collectInt(intFunction);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableLongCollection collectLong(LongFunction<? super T> longFunction)
    {
        return (MutableLongCollection) super.collectLong(longFunction);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableShortCollection collectShort(ShortFunction<? super T> shortFunction)
    {
        return (MutableShortCollection) super.collectShort(shortFunction);
    }

    /**
     * @since 9.0
     */
    @Override
    public <P> MutableCollection<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (MutableCollection<T>) super.selectWith(predicate, parameter);
    }

    /**
     * @since 9.0
     */
    @Override
    public <P> MutableCollection<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (MutableCollection<T>) super.rejectWith(predicate, parameter);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> MutableCollection<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return (MutableCollection<V>) super.collectIf(predicate, function);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<T> tap(Procedure<? super T> procedure)
    {
        return (MutableCollection<T>) super.tap(procedure);
    }

    /**
     * @since 9.0
     */
    @Override
    public <S> MutableCollection<Pair<T, S>> zip(Iterable<S> that)
    {
        return (MutableCollection<Pair<T, S>>) super.zip(that);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<Pair<T, Integer>> zipWithIndex()
    {
        return (MutableCollection<Pair<T, Integer>>) super.zipWithIndex();
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> MutableMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return (MutableMultimap<V, T>) super.groupBy(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> MutableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableMultimap<V, T>) super.groupByEach(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> MutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return (MutableBag<V>) super.countBy(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V, P> MutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return (MutableBag<V>) super.countByWith(function, parameter);
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V> MutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableBag<V>) super.countByEach(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public PartitionMutableCollection<T> partition(Predicate<? super T> predicate)
    {
        return (PartitionMutableCollection<T>) super.partition(predicate);
    }

    /**
     * @since 9.0
     */
    @Override
    public <P> PartitionMutableCollection<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (PartitionMutableCollection<T>) super.partitionWith(predicate, parameter);
    }

    @Override
    public boolean add(T o)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().add(o);
        }
    }

    @Override
    public boolean remove(Object o)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().remove(o);
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> coll)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().addAll(coll);
        }
    }

    @Override
    public boolean removeAll(Collection<?> coll)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().removeAll(coll);
        }
    }

    @Override
    public boolean retainAll(Collection<?> coll)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().retainAll(coll);
        }
    }

    @Override
    public void clear()
    {
        synchronized (this.getLock())
        {
            this.getDelegate().clear();
        }
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().removeIf(predicate);
        }
    }

    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().removeIfWith(predicate, parameter);
        }
    }

    @Override
    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().addAllIterable(iterable);
        }
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().removeAllIterable(iterable);
        }
    }

    @Override
    public boolean retainAllIterable(Iterable<?> iterable)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().retainAllIterable(iterable);
        }
    }

    @Override
    public <P> Twin<MutableList<T>> selectAndRejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().selectAndRejectWith(predicate, parameter);
        }
    }

    @Override
    public <IV, P> IV injectIntoWith(
            IV injectValue,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().injectIntoWith(injectValue, function, parameter);
        }
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().groupByUniqueKey(function);
        }
    }

    @Override
    public <K, V> MutableMap<K, V> aggregateInPlaceBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
        }
    }

    @Override
    public <K, V> MutableMap<K, V> aggregateBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
        }
    }

    @Override
    public <K> MutableMapIterable<K, T> reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reduceBy(groupBy, reduceFunction);
        }
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByInt(groupBy, function);
        }
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByFloat(groupBy, function);
        }
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByLong(groupBy, function);
        }
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByDouble(groupBy, function);
        }
    }
}
