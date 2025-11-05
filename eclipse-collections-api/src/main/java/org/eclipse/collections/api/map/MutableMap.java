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

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.bag.primitive.MutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.MutableFloatBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.bag.primitive.MutableShortBag;
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
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionMutableBag;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A MutableMap is similar to a JCF Map but adds additional useful internal iterator methods. The MutableMap interface
 * additionally implements some methods in the Smalltalk Dictionary protocol. It provides mutating operations that
 * modify the map in place and is the default mutable map implementation in Eclipse Collections.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MutableMap<String, Integer> map = Maps.mutable.empty();
 * map.put("A", 1);
 * map.put("B", 2);
 * map.updateValue("A", () -> 0, v -> v + 10); // Updates "A" to 11
 * map.removeKey("B");
 * MutableMap<String, Integer> filtered = map.select((k, v) -> v > 5);
 * }</pre>
 *
 * @param <K> the type of keys in the map
 * @param <V> the type of values in the map
 */
public interface MutableMap<K, V>
        extends MutableMapIterable<K, V>, UnsortedMapIterable<K, V>, Cloneable
{
    /**
     * Adds all the entries derived from {@code iterable} to {@code this}. The key and value for each entry
     * is determined by applying the {@code keyFunction} and {@code valueFunction} to each item in
     * {@code collection}. Any entry in {@code map} that has the same key as an entry in {@code this}
     * will have its value replaced by that in {@code map}.
     */
    <E> MutableMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> valueFunction);

    @Override
    MutableMap<K, V> newEmpty();

    MutableMap<K, V> clone();

    @Override
    MutableMap<K, V> asUnmodifiable();

    @Override
    MutableMap<K, V> asSynchronized();

    @Override
    MutableSetMultimap<V, K> flip();

    @Override
    MutableMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    MutableMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <R> MutableMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> MutableMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <K2, V2> MutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    MutableMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    MutableBag<V> select(Predicate<? super V> predicate);

    @Override
    <P> MutableBag<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    MutableBag<V> reject(Predicate<? super V> predicate);

    @Override
    <P> MutableBag<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionMutableBag<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionMutableBag<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <S> MutableBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <R> MutableBag<R> collect(Function<? super V, ? extends R> function);

    @Override
    <P, V1> MutableBag<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter);

    @Override
    MutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    MutableByteBag collectByte(ByteFunction<? super V> byteFunction);

    @Override
    MutableCharBag collectChar(CharFunction<? super V> charFunction);

    @Override
    MutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    MutableFloatBag collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    MutableIntBag collectInt(IntFunction<? super V> intFunction);

    @Override
    MutableLongBag collectLong(LongFunction<? super V> longFunction);

    @Override
    MutableShortBag collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <R> MutableBag<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function);

    @Override
    <R> MutableBag<R> flatCollect(Function<? super V, ? extends Iterable<R>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, R> MutableBag<R> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<R>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> MutableBag<Pair<V, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    MutableSet<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> MutableBagMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function);

    @Override
    <VV> MutableBagMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function);

    @Override
    default <V1> MutableMap<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function)
    {
        return this.groupByUniqueKey(function, Maps.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default <KK, VV> MutableMap<KK, VV> aggregateInPlaceBy(
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
        return map;
    }

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
        return this.reduceBy(
                groupBy,
                reduceFunction,
                Maps.mutable.empty());
    }

    @Override
    MutableMap<V, K> flipUniqueValues();

    @Override
    MutableMap<K, V> withKeyValue(K key, V value);

    @Override
    default MutableMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        this.putAll(map);
        return this;
    }

    @Override
    default MutableMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        this.putAllMapIterable(mapIterable);
        return this;
    }

    @Override
    MutableMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    @Override
    MutableMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    @Override
    MutableMap<K, V> withoutKey(K key);

    @Override
    MutableMap<K, V> withoutAllKeys(Iterable<? extends K> keys);
}
