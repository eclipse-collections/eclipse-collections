/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map.sorted;

import java.util.Comparator;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
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
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.UnsortedMapIterable;
import org.eclipse.collections.api.multimap.list.ListMultimap;
import org.eclipse.collections.api.multimap.sortedset.SortedSetMultimap;
import org.eclipse.collections.api.ordered.ReversibleIterable;
import org.eclipse.collections.api.partition.list.PartitionList;
import org.eclipse.collections.api.tuple.Pair;

/**
 * An iterable Map whose elements are sorted by their keys according to a comparator.
 * SortedMapIterable maintains keys in sorted order and extends ReversibleIterable, allowing
 * iteration in both ascending and descending key order.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * SortedMapIterable<String, Integer> map = SortedMaps.mutable.of("C", 3, "A", 1, "B", 2);
 * // Iteration order is A, B, C (sorted by keys)
 * SortedMapIterable<String, Integer> filtered = map.select((k, v) -> v > 1);
 * // filtered contains: "B" -> 2, "C" -> 3 (still sorted)
 * }</pre>
 */
public interface SortedMapIterable<K, V>
        extends MapIterable<K, V>, ReversibleIterable<V>
{
    Comparator<? super K> comparator();

    // TODO: Keys could be ordered
    @Override
    SortedSetMultimap<V, K> flip();

    // TODO: When we have implementations of linked hash maps
    // OrderedMapIterable<V, K> flipUniqueValues();

    @Override
    SortedMapIterable<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    SortedMapIterable<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <K2, V2> UnsortedMapIterable<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> SortedMapIterable<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    SortedMapIterable<K, V> tap(Procedure<? super V> procedure);

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
    <S> ListIterable<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V1> ListIterable<V1> collect(Function<? super V, ? extends V1> function);

    /**
     * @since 9.1.
     */
    @Override
    default <V1> ListIterable<V1> collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends V1> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

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
    <P, V1> ListIterable<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter);

    @Override
    <V1> ListIterable<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function);

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
    <S> ListIterable<Pair<V, S>> zip(Iterable<S> that);

    @Override
    ListIterable<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> ListMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function);

    @Override
    <VV> ListMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function);

    @Override
    SortedMapIterable<K, V> toReversed();

    @Override
    SortedMapIterable<K, V> take(int count);

    @Override
    SortedMapIterable<K, V> takeWhile(Predicate<? super V> predicate);

    @Override
    SortedMapIterable<K, V> drop(int count);

    @Override
    SortedMapIterable<K, V> dropWhile(Predicate<? super V> predicate);

    // TODO: PartitionSortedMapIterable?
    @Override
    PartitionList<V> partitionWhile(Predicate<? super V> predicate);

    @Override
    ListIterable<V> distinct();

    /**
     * Converts the SortedMapIterable to an immutable implementation. Returns this for immutable maps.
     *
     * @since 5.0
     */
    @Override
    ImmutableSortedMap<K, V> toImmutable();
}
