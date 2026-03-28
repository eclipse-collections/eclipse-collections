/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.AbstractSynchronizedRichIterable;
import org.eclipse.collections.impl.tuple.AbstractImmutableEntry;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * A synchronized view of a map.
 */
public abstract class AbstractSynchronizedMapIterable<K, V>
        extends AbstractSynchronizedRichIterable<V>
        implements MutableMapIterable<K, V>
{
    protected AbstractSynchronizedMapIterable(MutableMapIterable<K, V> delegate)
    {
        super(delegate, null);
    }

    protected AbstractSynchronizedMapIterable(MutableMapIterable<K, V> delegate, Object lock)
    {
        super(delegate, lock);
    }

    @Override
    protected MutableMapIterable<K, V> getDelegate()
    {
        return (MutableMapIterable<K, V>) super.getDelegate();
    }

    @Override
    public V get(Object key)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().get(key);
        }
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsent(key, function);
        }
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentValue(key, value);
        }
    }

    @Override
    public <P> V getIfAbsentWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentWith(key, function, parameter);
        }
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().ifPresentApply(key, function);
        }
    }

    @Override
    public boolean containsKey(Object key)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().containsValue(value);
        }
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().forEachValue(procedure);
        }
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().forEachKey(procedure);
        }
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure2)
    {
        synchronized (this.lock)
        {
            this.getDelegate().forEachKeyValue(procedure2);
        }
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().detect(predicate);
        }
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().detectOptional(predicate);
        }
    }

    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentPut(key, function);
        }
    }

    @Override
    public V getIfAbsentPut(K key, V value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentPut(key, value);
        }
    }

    @Override
    public V getIfAbsentPutWithKey(K key, Function<? super K, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentPutWithKey(key, function);
        }
    }

    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentPutWith(key, function, parameter);
        }
    }

    @Override
    public V put(K key, V value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().put(key, value);
        }
    }

    @Override
    public V remove(Object key)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().remove(key);
        }
    }

    @Override
    public V removeKey(K key)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().removeKey(key);
        }
    }

    @Override
    public boolean removeAllKeys(Set<? extends K> keys)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().removeAllKeys(keys);
        }
    }

    @Override
    public boolean removeIf(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().removeIf(predicate);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        synchronized (this.lock)
        {
            this.getDelegate().putAll(map);
        }
    }

    @Override
    public void clear()
    {
        synchronized (this.lock)
        {
            this.getDelegate().clear();
        }
    }

    @Override
    public V putPair(Pair<? extends K, ? extends V> keyValuePair)
    {
        synchronized (this.lock)
        {
            return this.put(keyValuePair.getOne(), keyValuePair.getTwo());
        }
    }

    @Override
    public V add(Pair<? extends K, ? extends V> keyValuePair)
    {
        synchronized (this.lock)
        {
            return this.putPair(keyValuePair);
        }
    }

    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().updateValue(key, factory, function);
        }
    }

    @Override
    public <P> V updateValueWith(
            K key,
            Function0<? extends V> factory,
            Function2<? super V, ? super P, ? extends V> function,
            P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().updateValueWith(key, factory, function, parameter);
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().merge(key, value, remappingFunction);
        }
    }

    @Override
    public <VV> MutableMapIterable<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().groupByUniqueKey(function);
        }
    }

    @Override
    public <KK, VV> MutableMapIterable<KK, VV> aggregateInPlaceBy(Function<? super V, ? extends KK> groupBy, Function0<? extends VV> zeroValueFactory, Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
        }
    }

    @Override
    public <KK, VV> MutableMapIterable<KK, VV> aggregateBy(Function<? super V, ? extends KK> groupBy, Function0<? extends VV> zeroValueFactory, Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
        }
    }

    @Override
    public <K1, V1, V2> MutableMapIterable<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().aggregateBy(keyFunction, valueFunction, zeroValueFactory, nonMutatingAggregator);
        }
    }

    @Override
    public <KK> MutableMapIterable<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reduceBy(groupBy, reduceFunction);
        }
    }

    @Override
    public RichIterable<K> keysView()
    {
        return LazyIterate.adapt(this.keySet());
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return LazyIterate.adapt(this.values());
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return LazyIterate.adapt(this.entrySet()).collect(AbstractImmutableEntry.getPairFunction());
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByInt(groupBy, function);
        }
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByFloat(groupBy, function);
        }
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByLong(groupBy, function);
        }
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByDouble(groupBy, function);
        }
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1> MutableMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function)
    {
        return (MutableMultimap<V1, V>) super.<V1>groupBy(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1> MutableMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return (MutableMultimap<V1, V>) super.groupByEach(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <S> MutableCollection<Pair<V, S>> zip(Iterable<S> that)
    {
        return (MutableCollection<Pair<V, S>>) super.zip(that);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<Pair<V, Integer>> zipWithIndex()
    {
        return (MutableCollection<Pair<V, Integer>>) super.zipWithIndex();
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<V> select(Predicate<? super V> predicate)
    {
        return (MutableCollection<V>) super.select(predicate);
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
    public <P> MutableCollection<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (MutableCollection<V>) super.selectWith(predicate, parameter);
    }

    /**
     * @since 9.0
     */
    @Override
    public <P> MutableCollection<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (MutableCollection<V>) super.rejectWith(predicate, parameter);
    }

    /**
     * @since 9.0
     */
    @Override
    public PartitionMutableCollection<V> partition(Predicate<? super V> predicate)
    {
        return (PartitionMutableCollection<V>) super.partition(predicate);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1> MutableBag<V1> countBy(Function<? super V, ? extends V1> function)
    {
        return (MutableBag<V1>) super.<V1>countBy(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1, P> MutableBag<V1> countByWith(Function2<? super V, ? super P, ? extends V1> function, P parameter)
    {
        return (MutableBag<V1>) super.<V1, P>countByWith(function, parameter);
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V1> MutableBag<V1> countByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return (MutableBag<V1>) super.countByEach(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<V> reject(Predicate<? super V> predicate)
    {
        return (MutableCollection<V>) super.reject(predicate);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableMapIterable<K, V> tap(Procedure<? super V> procedure)
    {
        return (MutableMapIterable<K, V>) super.tap(procedure);
    }

    @Override
    public <V1> MutableCollection<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flatCollect(function);
        }
    }

    @Override
    public <P, V1> MutableCollection<V1> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<V1>> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flatCollectWith(function, parameter);
        }
    }

    @Override
    public <V1> MutableCollection<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectIf(predicate, function);
        }
    }

    @Override
    public <P, V1> MutableCollection<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectWith(function, parameter);
        }
    }

    @Override
    public <V1> MutableCollection<V1> collect(Function<? super V, ? extends V1> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collect(function);
        }
    }

    @Override
    public <P> PartitionMutableCollection<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().partitionWith(predicate, parameter);
        }
    }
}
