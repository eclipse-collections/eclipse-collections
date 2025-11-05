/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map;

import org.eclipse.collections.api.block.function.Function;
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
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.FloatList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.ShortList;
import org.eclipse.collections.api.multimap.list.ListMultimap;
import org.eclipse.collections.api.ordered.ReversibleIterable;
import org.eclipse.collections.api.partition.list.PartitionList;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A map whose keys are ordered but not necessarily sorted, for example a linked hash map.
 * The iteration order is deterministic and corresponds to the insertion order of keys.
 * OrderedMap extends ReversibleIterable, allowing iteration in reverse order.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * OrderedMap<String, Integer> map = OrderedMaps.mutable.of("A", 1, "B", 2, "C", 3);
 * OrderedMap<String, Integer> reversed = map.toReversed();
 * OrderedMap<String, Integer> firstTwo = map.take(2); // Contains "A" -> 1, "B" -> 2
 * }</pre>
 */
public interface OrderedMap<K, V>
        extends MapIterable<K, V>, ReversibleIterable<V>
{
    @Override
    OrderedMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    OrderedMap<V, K> flipUniqueValues();

    @Override
    ListMultimap<V, K> flip();

    @Override
    OrderedMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    OrderedMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <K2, V2> OrderedMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> OrderedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    ImmutableOrderedMap<K, V> toImmutable();

    @Override
    OrderedMap<K, V> toReversed();

    @Override
    OrderedMap<K, V> take(int count);

    @Override
    OrderedMap<K, V> takeWhile(Predicate<? super V> predicate);

    @Override
    OrderedMap<K, V> drop(int count);

    @Override
    OrderedMap<K, V> dropWhile(Predicate<? super V> predicate);

    @Override
    PartitionList<V> partitionWhile(Predicate<? super V> predicate);

    @Override
    ListIterable<V> distinct();

    @Override
    ListIterable<V> select(Predicate<? super V> predicate);

    @Override
    <P> ListIterable<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    ListIterable<V> reject(Predicate<? super V> predicate);

    @Override
    <P> ListIterable<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionList<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    BooleanList collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    ByteList collectByte(ByteFunction<? super V> byteFunction);

    @Override
    CharList collectChar(CharFunction<? super V> charFunction);

    @Override
    DoubleList collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    FloatList collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    IntList collectInt(IntFunction<? super V> intFunction);

    @Override
    LongList collectLong(LongFunction<? super V> longFunction);

    @Override
    ShortList collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <S> ListIterable<Pair<V, S>> zip(Iterable<S> that);

    @Override
    ListIterable<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> ListIterable<VV> collect(Function<? super V, ? extends VV> function);

    @Override
    <P, V1> ListIterable<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter);

    @Override
    <V1> ListIterable<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function);

    @Override
    <S> ListIterable<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V1> ListIterable<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V1> ListIterable<V1> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<V1>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    <V1> ListMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    @Override
    <V1> ListMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    @Override
    <V1> OrderedMap<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function);
}
