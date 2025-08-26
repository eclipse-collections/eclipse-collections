/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bimap.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.primitive.ImmutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.ImmutableByteBag;
import org.eclipse.collections.api.bag.primitive.ImmutableCharBag;
import org.eclipse.collections.api.bag.primitive.ImmutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.ImmutableFloatBag;
import org.eclipse.collections.api.bag.primitive.ImmutableIntBag;
import org.eclipse.collections.api.bag.primitive.ImmutableLongBag;
import org.eclipse.collections.api.bag.primitive.ImmutableShortBag;
import org.eclipse.collections.api.bimap.ImmutableBiMap;
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
import org.eclipse.collections.api.factory.BiMaps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionImmutableSet;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bimap.AbstractBiMap;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.partition.set.PartitionUnifiedSet;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.MapIterate;

public abstract class AbstractImmutableBiMap<K, V> extends AbstractBiMap<K, V> implements ImmutableBiMap<K, V>, Map<K, V>
{
    private final ImmutableMap<K, V> delegate;
    private final AbstractImmutableBiMap<V, K> inverse;

    private AbstractImmutableBiMap(ImmutableMap<K, V> delegate, AbstractImmutableBiMap<V, K> valuesToKeys)
    {
        this.delegate = delegate;
        this.inverse = valuesToKeys;
    }

    AbstractImmutableBiMap(ImmutableMap<K, V> map, ImmutableMap<V, K> inverse)
    {
        this.delegate = map;
        this.inverse = new Inverse<>(inverse, this);
    }

    @Override
    protected ImmutableMap<K, V> getDelegate()
    {
        return this.delegate;
    }

    @Override
    protected ImmutableMap<V, K> getInverse()
    {
        return this.inverse.delegate;
    }

    @Override
    public ImmutableBiMap<K, V> newWithKeyValue(K key, V value)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        map.put(key, value);
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        for (Pair<? extends K, ? extends V> keyValuePair : keyValues)
        {
            map.put(keyValuePair.getOne(), keyValuePair.getTwo());
        }
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithMap(Map<? extends K, ? extends V> map)
    {
        HashBiMap<K, V> result = new HashBiMap<>(this.delegate.castToMap());
        result.putAll(map);
        return result.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        mapIterable.forEachKeyValue(map::put);
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        return this.newWithAllKeyValues(ArrayAdapter.adapt(keyValuePairs));
    }

    @Override
    public ImmutableBiMap<K, V> newWithoutKey(K key)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        map.removeKey(key);
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        for (K key : keys)
        {
            map.removeKey(key);
        }
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<V, K> inverse()
    {
        return this.inverse;
    }

    @Override
    public ImmutableSetMultimap<V, K> flip()
    {
        // TODO: We could optimize this since we know the values are unique
        return MapIterate.flip(this).toImmutable();
    }

    @Override
    public ImmutableBiMap<V, K> flipUniqueValues()
    {
        return this.inverse();
    }

    @Override
    public V put(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call put() on " + this.getClass().getSimpleName());
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call putAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public V remove(Object key)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call compute() on " + this.getClass().getSimpleName());
    }

    @Override
    public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> mappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call computeIfAbsent() on " + this.getClass().getSimpleName());
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call computeIfPresent() on " + this.getClass().getSimpleName());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    @Override
    public Set<K> keySet()
    {
        return this.delegate.castToMap().keySet();
    }

    @Override
    public Collection<V> values()
    {
        return this.delegate.castToMap().values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return this.delegate.castToMap().entrySet();
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.delegate.iterator();
    }

    @Override
    public ImmutableBiMap<K, V> toImmutable()
    {
        return this;
    }

    @Override
    public Map<K, V> castToMap()
    {
        return this;
    }

    public MutableMap<K, V> toMap()
    {
        return this.getDelegate().toMap();
    }

    @Override
    public <K2, V2> ImmutableBiMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        ImmutableMap<K2, V2> result = this.delegate.collect(function);
        return BiMaps.immutable.withAll(result);
    }

    @Override
    public <VV> ImmutableBag<VV> collect(Function<? super V, ? extends VV> function)
    {
        return this.delegate.collect(function);
    }

    @Override
    public <R> ImmutableBiMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        ImmutableMap<K, R> result = this.delegate.collectValues(function);
        return BiMaps.immutable.withAll(result);
    }

    @Override
    public <R> ImmutableBiMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        ImmutableMap<R, V> result = this.delegate.collectKeysUnique(function);
        return BiMaps.immutable.withAll(result);
    }

    @Override
    public ImmutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.delegate.collectBoolean(booleanFunction);
    }

    @Override
    public ImmutableByteBag collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.delegate.collectByte(byteFunction);
    }

    @Override
    public ImmutableCharBag collectChar(CharFunction<? super V> charFunction)
    {
        return this.delegate.collectChar(charFunction);
    }

    @Override
    public ImmutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.delegate.collectDouble(doubleFunction);
    }

    @Override
    public ImmutableFloatBag collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.delegate.collectFloat(floatFunction);
    }

    @Override
    public ImmutableIntBag collectInt(IntFunction<? super V> intFunction)
    {
        return this.delegate.collectInt(intFunction);
    }

    @Override
    public ImmutableLongBag collectLong(LongFunction<? super V> longFunction)
    {
        return this.delegate.collectLong(longFunction);
    }

    @Override
    public ImmutableShortBag collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.delegate.collectShort(shortFunction);
    }

    @Override
    public <P, VV> ImmutableBag<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.delegate.collectWith(function, parameter);
    }

    @Override
    public <VV> ImmutableBag<VV> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends VV> function)
    {
        return this.delegate.collectIf(predicate, function);
    }

    @Override
    public <VV> ImmutableBag<VV> flatCollect(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.flatCollect(function);
    }

    @Override
    public ImmutableBiMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.selectMapOnEntry(this, predicate, HashBiMap.newMap()).toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public ImmutableSet<V> select(Predicate<? super V> predicate)
    {
        return this.delegate.select(predicate, Sets.mutable.empty()).toImmutable();
    }

    @Override
    public <P> ImmutableSet<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.selectWith(predicate, parameter, Sets.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.rejectMapOnEntry(this, predicate, HashBiMap.newMap()).toImmutable();
    }

    @Override
    public ImmutableSet<V> reject(Predicate<? super V> predicate)
    {
        return this.delegate.reject(predicate, Sets.mutable.empty()).toImmutable();
    }

    @Override
    public <P> ImmutableSet<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.rejectWith(predicate, parameter, Sets.mutable.empty()).toImmutable();
    }

    @Override
    public PartitionImmutableSet<V> partition(Predicate<? super V> predicate)
    {
        PartitionMutableSet<V> result = new PartitionUnifiedSet<>();
        this.inverse.forEachKey(new PartitionProcedure<>(predicate, result));
        return result.toImmutable();
    }

    @Override
    public <P> PartitionImmutableSet<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.partition(Predicates.bind(predicate, parameter));
    }

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> ImmutableSet<Pair<V, S>> zip(Iterable<S> that)
    {
        if (that instanceof Collection || that instanceof RichIterable)
        {
            int thatSize = Iterate.sizeOf(that);
            MutableSet<Pair<V, S>> target = UnifiedSet.newSet(Math.min(this.size(), thatSize));
            return this.delegate.zip(that, target).toImmutable();
        }
        return this.delegate.zip(that, Sets.mutable.empty()).toImmutable();
    }

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public ImmutableSet<Pair<V, Integer>> zipWithIndex()
    {
        return this.delegate.zipWithIndex(Sets.mutable.withInitialCapacity(this.size())).toImmutable();
    }

    @Override
    public <V1> ImmutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        ObjectLongMap<V1> map = this.delegate.sumByInt(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V1> ImmutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        ObjectDoubleMap<V1> map = this.delegate.sumByFloat(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V1> ImmutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        ObjectLongMap<V1> map = this.delegate.sumByLong(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V1> ImmutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        ObjectDoubleMap<V1> map = this.delegate.sumByDouble(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <VV> ImmutableSetMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.groupBy(function, new UnifiedSetMultimap<VV, V>()).toImmutable();
    }

    @Override
    public <VV> ImmutableSetMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.groupByEach(function, new UnifiedSetMultimap<>()).toImmutable();
    }

    @Override
    public <VV> ImmutableBiMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return BiMaps.immutable.withAll(this.delegate.groupByUniqueKey(function));
    }

    @Override
    public <K2, V2> ImmutableMap<K2, V2> aggregateBy(Function<? super V, ? extends K2> groupBy, Function0<? extends V2> zeroValueFactory, Function2<? super V2, ? super V, ? extends V2> nonMutatingAggregator)
    {
        return this.delegate.aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
    }

    @Override
    public <K1, V1, V2> ImmutableMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        return this.delegate.aggregateBy(keyFunction, valueFunction, zeroValueFactory, nonMutatingAggregator);
    }

    @Override
    public <K2, V2> ImmutableMap<K2, V2> aggregateInPlaceBy(Function<? super V, ? extends K2> groupBy, Function0<? extends V2> zeroValueFactory, Procedure2<? super V2, ? super V> mutatingAggregator)
    {
        return this.delegate.aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
    }

    @Override
    public <K2> ImmutableMap<K2, V> reduceBy(
            Function<? super V, ? extends K2> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        return this.delegate.reduceBy(groupBy, reduceFunction);
    }

    @Override
    public <S> ImmutableSet<S> selectInstancesOf(Class<S> clazz)
    {
        MutableSet<S> result = new UnifiedSet<>();
        this.inverse.forEachKey(new SelectInstancesOfProcedure<>(clazz, result));
        return result.toImmutable();
    }

    private static class Inverse<K, V> extends AbstractImmutableBiMap<K, V> implements Serializable
    {
        Inverse(ImmutableMap<K, V> delegate, AbstractImmutableBiMap<V, K> inverse)
        {
            super(delegate, inverse);
        }

        protected Object writeReplace()
        {
            return new ImmutableBiMapSerializationProxy<>(this);
        }
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action)
    {
        super.forEach(action);
    }
}
