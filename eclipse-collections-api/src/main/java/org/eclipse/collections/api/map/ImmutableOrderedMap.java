/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map;

import java.util.Map;

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
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.tuple.Pair;

/**
 * ImmutableOrderedMap is an ordered map that cannot be modified after initialization.
 * It maintains the insertion order of keys and all operations that would modify the map
 * return new immutable instances instead, preserving the original map.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableOrderedMap<String, Integer> map = OrderedMaps.immutable.of("A", 1, "B", 2, "C", 3);
 * // Iteration order is A, B, C (insertion order)
 * ImmutableOrderedMap<String, Integer> updated = map.newWithKeyValue("D", 4);
 * // Original map is unchanged
 * ImmutableOrderedMap<String, Integer> reversed = map.toReversed();
 * // reversed maintains order: C, B, A
 * }</pre>
 *
 * @param <K> the type of keys in the map
 * @param <V> the type of values in the map
 */
public interface ImmutableOrderedMap<K, V> extends OrderedMap<K, V>, ImmutableMapIterable<K, V>
{
    @Override
    ImmutableOrderedMap<K, V> newWithKeyValue(K key, V value);

    @Override
    ImmutableOrderedMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    @Override
    ImmutableOrderedMap<K, V> newWithMap(Map<? extends K, ? extends V> map);

    @Override
    ImmutableOrderedMap<K, V> newWithMapIterable(MapIterable<? extends K, ? extends V> mapIterable);

    @Override
    ImmutableOrderedMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    @Override
    ImmutableOrderedMap<K, V> newWithoutKey(K key);

    @Override
    ImmutableOrderedMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys);

    @Override
    ImmutableOrderedMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    ImmutableOrderedMap<V, K> flipUniqueValues();

    @Override
    ImmutableListMultimap<V, K> flip();

    @Override
    ImmutableOrderedMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    ImmutableOrderedMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <K2, V2> ImmutableOrderedMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> ImmutableOrderedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> ImmutableOrderedMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    @Override
    ImmutableOrderedMap<K, V> toReversed();

    @Override
    ImmutableOrderedMap<K, V> take(int count);

    @Override
    ImmutableOrderedMap<K, V> takeWhile(Predicate<? super V> predicate);

    @Override
    ImmutableOrderedMap<K, V> drop(int count);

    @Override
    ImmutableOrderedMap<K, V> dropWhile(Predicate<? super V> predicate);

    @Override
    PartitionImmutableList<V> partitionWhile(Predicate<? super V> predicate);

    @Override
    ImmutableList<V> distinct();

    @Override
    ImmutableList<V> select(Predicate<? super V> predicate);

    @Override
    <P> ImmutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    ImmutableList<V> reject(Predicate<? super V> predicate);

    @Override
    <P> ImmutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableList<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionImmutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    ImmutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    ImmutableByteList collectByte(ByteFunction<? super V> byteFunction);

    @Override
    ImmutableCharList collectChar(CharFunction<? super V> charFunction);

    @Override
    ImmutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    ImmutableFloatList collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    ImmutableIntList collectInt(IntFunction<? super V> intFunction);

    @Override
    ImmutableLongList collectLong(LongFunction<? super V> longFunction);

    @Override
    ImmutableShortList collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <S> ImmutableList<Pair<V, S>> zip(Iterable<S> that);

    @Override
    ImmutableList<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> ImmutableList<VV> collect(Function<? super V, ? extends VV> function);

    @Override
    <P, V1> ImmutableList<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter);

    @Override
    <V1> ImmutableList<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function);

    @Override
    <S> ImmutableList<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V1> ImmutableList<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V1> ImmutableList<V1> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<V1>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    <V1> ImmutableListMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    @Override
    <V1> ImmutableListMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    @Override
    <V1> ImmutableOrderedMap<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function);

    @Override
    <KK, VV> ImmutableOrderedMap<KK, VV> aggregateInPlaceBy(Function<? super V, ? extends KK> groupBy, Function0<? extends VV> zeroValueFactory, Procedure2<? super VV, ? super V> mutatingAggregator);

    @Override
    <KK, VV> ImmutableOrderedMap<KK, VV> aggregateBy(Function<? super V, ? extends KK> groupBy, Function0<? extends VV> zeroValueFactory, Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator);

    @Override
    default <K1, V1, V2> ImmutableOrderedMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    <KK> ImmutableOrderedMap<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction);

    @Override
    <VV> ImmutableObjectLongMap<VV> sumByInt(
            Function<? super V, ? extends VV> groupBy,
            IntFunction<? super V> function);

    @Override
    <VV> ImmutableObjectDoubleMap<VV> sumByFloat(Function<? super V, ? extends VV> groupBy, FloatFunction<? super V> function);

    @Override
    <VV> ImmutableObjectLongMap<VV> sumByLong(Function<? super V, ? extends VV> groupBy, LongFunction<? super V> function);

    @Override
    <VV> ImmutableObjectDoubleMap<VV> sumByDouble(Function<? super V, ? extends VV> groupBy, DoubleFunction<? super V> function);
}
