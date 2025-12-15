/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.ordered.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
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
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.map.ImmutableOrderedMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.map.AbstractMapIterable;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;

public class ImmutableOrderedMapAdapter<K, V>
        extends AbstractMapIterable<K, V>
        implements ImmutableOrderedMap<K, V>, Map<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    private final MutableOrderedMap<K, V> delegate;

    public ImmutableOrderedMapAdapter(Map<K, V> delegate)
    {
        this.delegate = OrderedMapAdapter.adapt(Collections.unmodifiableMap(new LinkedHashMap<>(delegate)));
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.delegate.equals(obj);
    }

    @Override
    public String toString()
    {
        return this.delegate.toString();
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action)
    {
        super.forEach(action);
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    @Override
    public V get(Object key)
    {
        return this.delegate.get(key);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.delegate.forEachValue(procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.delegate.forEachKey(procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        this.delegate.forEachKeyValue(procedure);
    }

    @Override
    public RichIterable<K> keysView()
    {
        return this.delegate.keysView();
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return this.delegate.valuesView();
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return this.delegate.keyValuesView();
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.delegate.forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        this.delegate.forEachWith(procedure, parameter);
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        return this.delegate.ifPresentApply(key, function);
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        return this.delegate.getIfAbsent(key, function);
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        return this.delegate.getIfAbsentValue(key, value);
    }

    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        return this.delegate.getIfAbsentWith(key, function, parameter);
    }

    @Override
    public Map<K, V> castToMap()
    {
        return this;
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.valuesView().iterator();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call putAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    @Override
    public ImmutableListMultimap<V, K> flip()
    {
        return this.delegate.flip().toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> newWithKeyValue(K key, V value)
    {
        MutableOrderedMap<K, V> copy = OrderedMapAdapter.adapt(new LinkedHashMap<>(this.delegate));
        copy.put(key, value);
        return copy.toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        MutableOrderedMap<K, V> copy = OrderedMapAdapter.adapt(new LinkedHashMap<>(this.delegate));
        keyValues.forEach(pair -> copy.put(pair.getOne(), pair.getTwo()));
        return copy.toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> newWithMap(Map<? extends K, ? extends V> map)
    {
        MutableOrderedMap<K, V> copy = OrderedMapAdapter.adapt(new LinkedHashMap<>(this.delegate));
        copy.putAll(map);
        return copy.toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> newWithMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        MutableOrderedMap<K, V> copy = OrderedMapAdapter.adapt(new LinkedHashMap<>(this.delegate));
        copy.putAllMapIterable(mapIterable);
        return copy.toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        MutableOrderedMap<K, V> copy = OrderedMapAdapter.adapt(new LinkedHashMap<>(this.delegate));
        for (Pair<? extends K, ? extends V> keyValuePair : keyValuePairs)
        {
            copy.put(keyValuePair.getOne(), keyValuePair.getTwo());
        }
        return copy.toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> newWithoutKey(K key)
    {
        MutableOrderedMap<K, V> copy = OrderedMapAdapter.adapt(new LinkedHashMap<>(this.delegate));
        copy.removeKey(key);
        return copy.toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys)
    {
        MutableOrderedMap<K, V> copy = OrderedMapAdapter.adapt(new LinkedHashMap<>(this.delegate));
        keys.forEach(copy::removeKey);
        return copy.toImmutable();
    }

    @Override
    public V put(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call put() on " + this.getClass().getSimpleName());
    }

    @Override
    public V remove(Object key)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call merge() on " + this.getClass().getSimpleName());
    }

    @Override
    public ImmutableOrderedMap<V, K> flipUniqueValues()
    {
        return this.delegate.flipUniqueValues().toImmutable();
    }

    @Override
    public <K2, V2> ImmutableOrderedMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.delegate.collect(function).toImmutable();
    }

    @Override
    public <R> ImmutableOrderedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.delegate.<R>collectValues(function).toImmutable();
    }

    @Override
    public <R> ImmutableOrderedMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.delegate.<R>collectKeysUnique(function).toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.select(predicate).toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.reject(predicate).toImmutable();
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        return this.delegate.detect(predicate);
    }

    @Override
    public Optional<V> detectOptional(Predicate<? super V> predicate)
    {
        return this.delegate.detectOptional(predicate);
    }

    @Override
    public <VV> ImmutableList<VV> collect(Function<? super V, ? extends VV> function)
    {
        return this.delegate.<VV>collect(function).toImmutable();
    }

    @Override
    public <P, VV> ImmutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.delegate.<P, VV>collectWith(function, parameter).toImmutable();
    }

    @Override
    public ImmutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.delegate.collectBoolean(booleanFunction).toImmutable();
    }

    @Override
    public ImmutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.delegate.collectByte(byteFunction).toImmutable();
    }

    @Override
    public ImmutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        return this.delegate.collectChar(charFunction).toImmutable();
    }

    @Override
    public ImmutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.delegate.collectDouble(doubleFunction).toImmutable();
    }

    @Override
    public ImmutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.delegate.collectFloat(floatFunction).toImmutable();
    }

    @Override
    public ImmutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        return this.delegate.collectInt(intFunction).toImmutable();
    }

    @Override
    public ImmutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        return this.delegate.collectLong(longFunction).toImmutable();
    }

    @Override
    public ImmutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.delegate.collectShort(shortFunction).toImmutable();
    }

    @Override
    public <VV> ImmutableList<VV> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends VV> function)
    {
        return this.delegate.<VV>collectIf(predicate, function).toImmutable();
    }

    @Override
    public <VV> ImmutableList<VV> flatCollect(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.<VV>flatCollect(function).toImmutable();
    }

    @Override
    public ImmutableList<V> select(Predicate<? super V> predicate)
    {
        return this.delegate.select(predicate).toImmutable();
    }

    @Override
    public <P> ImmutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.selectWith(predicate, parameter).toImmutable();
    }

    @Override
    public ImmutableList<V> reject(Predicate<? super V> predicate)
    {
        return this.delegate.reject(predicate).toImmutable();
    }

    @Override
    public <P> ImmutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.rejectWith(predicate, parameter).toImmutable();
    }

    @Override
    public PartitionImmutableList<V> partition(Predicate<? super V> predicate)
    {
        return this.delegate.partition(predicate).toImmutable();
    }

    @Override
    public <P> PartitionImmutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.partitionWith(predicate, parameter).toImmutable();
    }

    @Override
    public <S> ImmutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return this.delegate.<S>selectInstancesOf(clazz).toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public <S> ImmutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.delegate.zip(that).toImmutable();
    }

    @Override
    public ImmutableList<Pair<V, Integer>> zipWithIndex()
    {
        return this.delegate.zipWithIndex().toImmutable();
    }

    @Override
    public <VV> ImmutableListMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.<VV>groupBy(function).toImmutable();
    }

    @Override
    public <VV> ImmutableListMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.<VV>groupByEach(function).toImmutable();
    }

    @Override
    public <VV> ImmutableOrderedMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return this.delegate.<VV>groupByUniqueKey(function).toImmutable();
    }

    @Override
    public <KK, VV> ImmutableOrderedMap<KK, VV> aggregateInPlaceBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        return this.delegate.<KK, VV>aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator).toImmutable();
    }

    @Override
    public <KK, VV> ImmutableOrderedMap<KK, VV> aggregateBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        return this.delegate.<KK, VV>aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator).toImmutable();
    }

    @Override
    public <KK, VVV, VV> ImmutableOrderedMap<KK, VV> aggregateBy(
            Function<? super K, ? extends KK> keyFunction,
            Function<? super V, ? extends VVV> valueFunction,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super VVV, ? extends VV> nonMutatingAggregator)
    {
        return this.delegate.<KK, VVV, VV>aggregateBy(
                keyFunction,
                valueFunction,
                zeroValueFactory,
                nonMutatingAggregator).toImmutable();
    }

    @Override
    public <KK> ImmutableOrderedMap<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        return this.delegate.<KK>reduceBy(groupBy, reduceFunction).toImmutable();
    }

    @Override
    public <VV> ImmutableObjectLongMap<VV> sumByInt(
            Function<? super V, ? extends VV> groupBy,
            IntFunction<? super V> function)
    {
        return this.delegate.<VV>sumByInt(groupBy, function).toImmutable();
    }

    @Override
    public <VV> ImmutableObjectDoubleMap<VV> sumByFloat(
            Function<? super V, ? extends VV> groupBy,
            FloatFunction<? super V> function)
    {
        return this.delegate.<VV>sumByFloat(groupBy, function).toImmutable();
    }

    @Override
    public <VV> ImmutableObjectLongMap<VV> sumByLong(
            Function<? super V, ? extends VV> groupBy,
            LongFunction<? super V> function)
    {
        return this.delegate.<VV>sumByLong(groupBy, function).toImmutable();
    }

    @Override
    public <VV> ImmutableObjectDoubleMap<VV> sumByDouble(
            Function<? super V, ? extends VV> groupBy,
            DoubleFunction<? super V> function)
    {
        return this.delegate.<VV>sumByDouble(groupBy, function).toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> toReversed()
    {
        return this.delegate.toReversed().toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> take(int count)
    {
        return this.delegate.take(count).toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        return this.delegate.takeWhile(predicate).toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> drop(int count)
    {
        return this.delegate.drop(count).toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        return this.delegate.dropWhile(predicate).toImmutable();
    }

    @Override
    public PartitionImmutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        return this.delegate.partitionWhile(predicate).toImmutable();
    }

    @Override
    public ImmutableList<V> distinct()
    {
        return this.delegate.distinct().toImmutable();
    }

    @Override
    public ImmutableOrderedMap<K, V> toImmutable()
    {
        return this;
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.detect(predicate);
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.detectOptional(predicate);
    }

    @Override
    public int detectIndex(Predicate<? super V> predicate)
    {
        return this.delegate.detectIndex(predicate);
    }

    @Override
    public int detectLastIndex(Predicate<? super V> predicate)
    {
        return this.delegate.detectLastIndex(predicate);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super V, ? super S> predicate)
    {
        return this.delegate.corresponds(other, predicate);
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super V> procedure)
    {
        this.delegate.forEach(startIndex, endIndex, procedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.delegate.forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
    }

    // TODO: Change this to return ImmutableOrderedBag once we support it
    @Override
    public <V1> ImmutableBag<V1> countByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return this.delegate.countByEach(function).toImmutable();
    }

    @Override
    public Set<K> keySet()
    {
        return Collections.unmodifiableSet(this.delegate.keySet());
    }

    @Override
    public Collection<V> values()
    {
        return Collections.unmodifiableCollection(this.delegate.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return Collections.unmodifiableSet(this.delegate.entrySet());
    }
}
