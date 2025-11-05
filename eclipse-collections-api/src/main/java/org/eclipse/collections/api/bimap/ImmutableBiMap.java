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

import org.eclipse.collections.api.bag.ImmutableBagIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.BiMaps;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.ImmutableMapIterable;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionImmutableSet;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A {@link BiMap} whose contents cannot be altered after initialization. ImmutableBiMap enforces uniqueness
 * on both keys and values, and all operations that would modify the map return new immutable instances instead.
 * The inverse view is also immutable.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableBiMap<String, Integer> biMap = BiMaps.immutable.of("A", 1, "B", 2, "C", 3);
 * ImmutableBiMap<Integer, String> inverse = biMap.inverse();
 * String key = inverse.get(2); // Returns "B"
 * ImmutableBiMap<String, Integer> updated = biMap.newWithKeyValue("D", 4);
 * // Original biMap is unchanged
 * }</pre>
 *
 * @param <K> the type of keys in the map
 * @param <V> the type of values in the map
 * @since 4.2
 */
public interface ImmutableBiMap<K, V> extends BiMap<K, V>, ImmutableMapIterable<K, V>
{
    @Override
    ImmutableBiMap<K, V> newWithKeyValue(K key, V value);

    @Override
    ImmutableBiMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    @Override
    ImmutableBiMap<K, V> newWithMap(Map<? extends K, ? extends V> map);

    @Override
    ImmutableBiMap<K, V> newWithMapIterable(MapIterable<? extends K, ? extends V> mapIterable);

    @Override
    ImmutableBiMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    @Override
    ImmutableBiMap<K, V> newWithoutKey(K key);

    @Override
    ImmutableBiMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys);

    @Override
    ImmutableBiMap<V, K> inverse();

    @Override
    ImmutableSetMultimap<V, K> flip();

    @Override
    ImmutableBiMap<V, K> flipUniqueValues();

    @Override
    ImmutableBiMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    ImmutableBiMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    ImmutableBiMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <K2, V2> ImmutableBiMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> ImmutableBiMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> ImmutableBiMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    @Override
    ImmutableSet<V> select(Predicate<? super V> predicate);

    @Override
    <P> ImmutableSet<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    ImmutableSet<V> reject(Predicate<? super V> predicate);

    @Override
    <P> ImmutableSet<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableSet<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionImmutableSet<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <S> ImmutableSet<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V1> ImmutableBagIterable<V1> collect(Function<? super V, ? extends V1> function);

    @Override
    <P, V1> ImmutableBagIterable<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter);

    @Override
    <V1> ImmutableBagIterable<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function);

    @Override
    <V1> ImmutableBagIterable<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V1> ImmutableBagIterable<V1> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<V1>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    <V1> ImmutableSetMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    @Override
    <V1> ImmutableSetMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    @Override
    default <VV> ImmutableBiMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        MutableBiMap<VV, V> target = BiMaps.mutable.empty();
        return this.groupByUniqueKey(function, target).toImmutable();
    }

    /**
     * @since 11.0
     */
    @Override
    default <KK, VV> ImmutableMap<KK, VV> aggregateInPlaceBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        MutableMap<KK, VV> map = Maps.mutable.empty();
        this.forEach(each ->
        {
            KK key = groupBy.valueOf(each);
            VV value = map.getIfAbsentPut(key, zeroValueFactory);
            mutatingAggregator.value(value, each);
        });
        return map.toImmutable();
    }

    /**
     * @since 11.0
     */
    @Override
    default <KK, VV> ImmutableMap<KK, VV> aggregateBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        MutableMap<KK, VV> map = this.aggregateBy(
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                Maps.mutable.empty());
        return map.toImmutable();
    }

    /**
     * @since 11.0
     */
    @Override
    default <K1, V1, V2> ImmutableMap<K1, V2> aggregateBy(
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
        return map.toImmutable();
    }

    @Override
    default <KK> ImmutableMapIterable<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        MutableMap<KK, V> map = this.reduceBy(groupBy, reduceFunction, Maps.mutable.empty());
        return map.toImmutable();
    }

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> ImmutableSet<Pair<V, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    ImmutableSet<Pair<V, Integer>> zipWithIndex();
}
