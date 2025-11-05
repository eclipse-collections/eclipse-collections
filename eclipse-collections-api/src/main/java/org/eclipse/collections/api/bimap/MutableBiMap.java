/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bimap;

import java.util.Map;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.BiMaps;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A {@link BiMap} whose contents can be altered after initialization. MutableBiMap enforces uniqueness
 * on both keys and values, and provides mutating operations that modify the map in place. The inverse
 * view is also mutable and reflects changes made to either direction.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MutableBiMap<String, Integer> biMap = BiMaps.mutable.empty();
 * biMap.put("A", 1);
 * biMap.put("B", 2);
 * MutableBiMap<Integer, String> inverse = biMap.inverse();
 * inverse.put(3, "C"); // Also updates the original biMap
 * biMap.forcePut("D", 1); // Removes "A" -> 1 and adds "D" -> 1
 * }</pre>
 *
 * @param <K> the type of keys in the map
 * @param <V> the type of values in the map
 * @since 4.2
 */
public interface MutableBiMap<K, V> extends BiMap<K, V>, MutableMapIterable<K, V>, Cloneable
{
    @Override
    MutableBiMap<K, V> newEmpty();

    @Override
    MutableBiMap<V, K> inverse();

    @Override
    MutableBiMap<V, K> flipUniqueValues();

    @Override
    MutableSetMultimap<V, K> flip();

    /**
     * Similar to {@link Map#put(Object, Object)}, except that it throws on the addition of a duplicate value.
     *
     * @throws IllegalArgumentException if the value already exists in the bimap.
     */
    @Override
    V put(K key, V value);

    /**
     * Similar to {@link #put(Object, Object)}, except that it quietly removes any existing entry with the same
     * value before putting the key-value pair.
     */
    V forcePut(K key, V value);

    @Override
    MutableBiMap<K, V> asSynchronized();

    @Override
    MutableBiMap<K, V> asUnmodifiable();

    MutableBiMap<K, V> clone();

    @Override
    MutableBiMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    MutableBiMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    MutableBiMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <K2, V2> MutableBiMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> MutableBiMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> MutableBiMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    @Override
    MutableSet<V> select(Predicate<? super V> predicate);

    @Override
    <P> MutableSet<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    MutableSet<V> reject(Predicate<? super V> predicate);

    @Override
    <P> MutableSet<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionMutableSet<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionMutableSet<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <S> MutableSet<S> selectInstancesOf(Class<S> clazz);

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> MutableSet<Pair<V, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    MutableSet<Pair<V, Integer>> zipWithIndex();

    @Override
    <V1> MutableSetMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    @Override
    <V1> MutableSetMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    @Override
    default <VV> MutableBiMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return this.groupByUniqueKey(function, BiMaps.mutable.empty());
    }

    /**
     * @since 11.0
     */
    @Override
    default <KK, VV> MutableMap<KK, VV> aggregateBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        return this.aggregateBy(
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                Maps.mutable.empty());
    }

    /**
     * @since 11.0
     */
    @Override
    default <K1, V1, V2> MutableMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        MutableMap<K1, V2> map = Maps.mutable.empty();
        this.forEachKeyValue((key, value) -> map.updateValueWith(
                keyFunction.valueOf(key),
                zeroValueFactory,
                nonMutatingAggregator,
                valueFunction.valueOf(value)));
        return map;
    }

    @Override
    default <KK> MutableMap<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        return this.reduceBy(groupBy, reduceFunction, Maps.mutable.empty());
    }

    @Override
    MutableBiMap<K, V> withKeyValue(K key, V value);

    @Override
    default MutableBiMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        this.putAll(map);
        return this;
    }

    @Override
    default MutableBiMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        this.putAllMapIterable(mapIterable);
        return this;
    }

    @Override
    MutableBiMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    @Override
    MutableBiMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    @Override
    MutableBiMap<K, V> withoutKey(K key);

    @Override
    MutableBiMap<K, V> withoutAllKeys(Iterable<? extends K> keys);
}
