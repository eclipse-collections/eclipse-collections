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

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.primitive.ImmutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.ImmutableByteBag;
import org.eclipse.collections.api.bag.primitive.ImmutableCharBag;
import org.eclipse.collections.api.bag.primitive.ImmutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.ImmutableFloatBag;
import org.eclipse.collections.api.bag.primitive.ImmutableIntBag;
import org.eclipse.collections.api.bag.primitive.ImmutableLongBag;
import org.eclipse.collections.api.bag.primitive.ImmutableShortBag;
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
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionImmutableBag;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * An ImmutableMap is different from a JCF Map because it has no mutating methods. It provides the read-only
 * protocol of a JDK Map. Unlike mutable maps, all modification operations return new immutable instances,
 * preserving the original map. ImmutableMap is an unordered collection - use ImmutableOrderedMap for
 * maintaining insertion order.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableMap<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
 * ImmutableMap<String, Integer> updated = map.newWithKeyValue("D", 4);
 * ImmutableMap<String, Integer> filtered = map.select((k, v) -> v > 1);
 * // Original map is unchanged
 * }</pre>
 */
public interface ImmutableMap<K, V>
        extends UnsortedMapIterable<K, V>, ImmutableMapIterable<K, V>
{
    /**
     * Returns a new ImmutableMap with an additional key-value pair added. If the key already exists,
     * the value is replaced in the returned map.
     *
     * @param key the key to add
     * @param value the value to associate with the key
     * @return a new ImmutableMap with the key-value pair added
     */
    @Override
    ImmutableMap<K, V> newWithKeyValue(K key, V value);

    /**
     * Returns a new ImmutableMap with all key-value pairs from the iterable added.
     *
     * @param keyValues the key-value pairs to add
     * @return a new ImmutableMap with all key-value pairs added
     */
    @Override
    ImmutableMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    /**
     * Returns a new ImmutableMap with all key-value pairs from the specified Map added.
     *
     * @param map the Map containing key-value pairs to add
     * @return a new ImmutableMap with all key-value pairs added
     */
    @Override
    ImmutableMap<K, V> newWithMap(Map<? extends K, ? extends V> map);

    /**
     * Returns a new ImmutableMap with all key-value pairs from the specified MapIterable added.
     *
     * @param mapIterable the MapIterable containing key-value pairs to add
     * @return a new ImmutableMap with all key-value pairs added
     */
    @Override
    ImmutableMap<K, V> newWithMapIterable(MapIterable<? extends K, ? extends V> mapIterable);

    /**
     * Returns a new ImmutableMap with all specified key-value pairs added.
     *
     * @param keyValuePairs the key-value pairs to add
     * @return a new ImmutableMap with all key-value pairs added
     */
    @Override
    ImmutableMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    /**
     * Returns a new ImmutableMap with the specified key removed. If the key does not exist,
     * returns this map.
     *
     * @param key the key to remove
     * @return a new ImmutableMap without the specified key
     */
    @Override
    ImmutableMap<K, V> newWithoutKey(K key);

    /**
     * Returns a new ImmutableMap with all specified keys removed.
     *
     * @param keys the keys to remove
     * @return a new ImmutableMap without the specified keys
     */
    @Override
    ImmutableMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys);

    /**
     * Converts this ImmutableMap to a MutableMap with the same key-value pairs.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMap<String, Integer> immutable = Maps.immutable.of("A", 1, "B", 2);
     * MutableMap<String, Integer> mutable = immutable.toMap();
     * mutable.put("C", 3); // Now we can modify it
     * }</pre>
     *
     * @return a new MutableMap with the same key-value pairs
     */
    MutableMap<K, V> toMap();

    @Override
    ImmutableSetMultimap<V, K> flip();

    @Override
    ImmutableMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    ImmutableMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    ImmutableMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    ImmutableBag<V> select(Predicate<? super V> predicate);

    @Override
    <P> ImmutableBag<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    ImmutableBag<V> reject(Predicate<? super V> predicate);

    @Override
    <P> ImmutableBag<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableBag<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionImmutableBag<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <S> ImmutableBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <K2, V2> ImmutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> ImmutableMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> ImmutableMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <VV> ImmutableBag<VV> collect(Function<? super V, ? extends VV> function);

    @Override
    <P, VV> ImmutableBag<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter);

    @Override
    ImmutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    ImmutableByteBag collectByte(ByteFunction<? super V> byteFunction);

    @Override
    ImmutableCharBag collectChar(CharFunction<? super V> charFunction);

    @Override
    ImmutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    ImmutableFloatBag collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    ImmutableIntBag collectInt(IntFunction<? super V> intFunction);

    @Override
    ImmutableLongBag collectLong(LongFunction<? super V> longFunction);

    @Override
    ImmutableShortBag collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <R> ImmutableBag<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function);

    @Override
    <R> ImmutableBag<R> flatCollect(Function<? super V, ? extends Iterable<R>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, R> ImmutableBag<R> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<R>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> ImmutableBag<Pair<V, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    ImmutableSet<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> ImmutableBagMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function);

    @Override
    <VV> ImmutableBagMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function);

    @Override
    default <V1> ImmutableMap<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function)
    {
        MutableMap<V1, V> target = Maps.mutable.withInitialCapacity(this.size());
        return this.groupByUniqueKey(function, target).toImmutable();
    }

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
    default <KK> ImmutableMap<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        MutableMap<KK, V> map = Maps.mutable.empty();
        return this.reduceBy(groupBy, reduceFunction, map).toImmutable();
    }

    @Override
    ImmutableMap<V, K> flipUniqueValues();
}
