/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map.primitive;

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
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionImmutableBag;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * ImmutablePrimitiveObjectMap is an immutable map with primitive keys and Object values.
 * This interface provides read-only access to primitive-to-object maps. All operations that
 * would modify the map return new immutable instances instead, preserving the original map.
 * Using primitive keys provides better memory efficiency and performance.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableIntObjectMap<String> map = IntObjectMaps.immutable.of(1, "One", 2, "Two");
 * ImmutableIntObjectMap<String> updated = map.newWithKeyValue(3, "Three");
 * // Original map is unchanged
 * String value = map.get(1); // Returns "One"
 * }</pre>
 *
 * @param <V> the type of values in the map
 * @since 8.0.
 */
public interface ImmutablePrimitiveObjectMap<V> extends PrimitiveObjectMap<V>
{
    @Override
    default <K, VV> ImmutableMap<K, VV> aggregateInPlaceBy(
            Function<? super V, ? extends K> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        MutableMap<K, VV> map = Maps.mutable.empty();
        this.forEach(each ->
        {
            K key = groupBy.valueOf(each);
            VV value = map.getIfAbsentPut(key, zeroValueFactory);
            mutatingAggregator.value(value, each);
        });
        return map.toImmutable();
    }

    @Override
    default <K, VV> ImmutableMap<K, VV> aggregateBy(
            Function<? super V, ? extends K> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        MutableMap<K, VV> map = this.aggregateBy(
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                Maps.mutable.empty());
        return map.toImmutable();
    }

    @Override
    default <K> ImmutableMap<K, V> reduceBy(
            Function<? super V, ? extends K> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        MutableMap<K, V> map = this.reduceBy(groupBy, reduceFunction, Maps.mutable.empty());
        return map.toImmutable();
    }

    @Override
    <VV> ImmutableBagMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function);

    @Override
    <VV> ImmutableBagMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function);

    @Override
    default <VV> ImmutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        MutableMap<VV, V> target = Maps.mutable.withInitialCapacity(this.size());
        return this.groupByUniqueKey(function, target).toImmutable();
    }

    @Override
    <VV> ImmutableBag<VV> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends VV> function);

    @Override
    <VV> ImmutableBag<VV> collect(Function<? super V, ? extends VV> function);

    @Override
    <VV> ImmutableBag<VV> flatCollect(Function<? super V, ? extends Iterable<VV>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, VV> ImmutableBag<VV> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<VV>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

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
    <P, VV> ImmutableBag<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter);

    @Override
    <S> ImmutableBag<S> selectInstancesOf(Class<S> clazz);

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

    /**
     * @deprecated in 7.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> ImmutableBag<Pair<V, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 7.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    ImmutableSet<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> ImmutableObjectLongMap<VV> sumByInt(Function<? super V, ? extends VV> groupBy, IntFunction<? super V> function);

    @Override
    <VV> ImmutableObjectDoubleMap<VV> sumByFloat(Function<? super V, ? extends VV> groupBy, FloatFunction<? super V> function);

    @Override
    <VV> ImmutableObjectLongMap<VV> sumByLong(Function<? super V, ? extends VV> groupBy, LongFunction<? super V> function);

    @Override
    <VV> ImmutableObjectDoubleMap<VV> sumByDouble(Function<? super V, ? extends VV> groupBy, DoubleFunction<? super V> function);
}
