/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map.sorted;

import java.util.Map;
import java.util.SortedMap;

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
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A MutableSortedMap is similar to a JCF SortedMap but adds additional useful internal iterator methods.
 * The MutableSortedMap interface additionally implements some methods in the Smalltalk Dictionary protocol.
 * Keys are maintained in sorted order according to a comparator, and all mutating operations modify the
 * map in place while preserving the sort order.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MutableSortedMap<String, Integer> map = SortedMaps.mutable.empty();
 * map.put("C", 3);
 * map.put("A", 1);
 * map.put("B", 2);
 * // Iteration order is A, B, C (sorted by keys)
 * MutableSortedMap<String, Integer> filtered = map.select((k, v) -> v > 1);
 * }</pre>
 */
public interface MutableSortedMap<K, V>
        extends MutableMapIterable<K, V>, SortedMapIterable<K, V>, SortedMap<K, V>, Cloneable
{
    /**
     * Creates a new instance of the same type with the same internal Comparator.
     */
    @Override
    MutableSortedMap<K, V> newEmpty();

    /**
     * Adds all the entries derived from {@code iterable} to {@code this}.
     * The key and value for each entry is determined by applying the {@code keyFunction} and {@code valueFunction} to each item in {@code collection}.
     * Any entry in {@code map} that has the same key as an entry in {@code this} will have it's value replaced by that in {@code map}.
     */
    <E> MutableSortedMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> valueFunction);

    /**
     * Return the value in the Map that corresponds to the specified key, or if there is no value
     * at the key, return the result of evaluating the specified one argument Function
     * using the specified parameter, and put that value in the map at the specified key.
     */
    @Override
    <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter);

    @Override
    MutableSortedMap<K, V> asUnmodifiable();

    @Override
    MutableSortedMap<K, V> asSynchronized();

    // TODO: Keys could be ordered
    @Override
    MutableSortedSetMultimap<V, K> flip();

    @Override
    MutableSortedMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    MutableSortedMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <K2, V2> MutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> MutableSortedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> MutableOrderedMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> MutableList<R> collect(Function<? super V, ? extends R> function);

    /**
     * @since 9.1.
     */
    @Override
    default <R> MutableList<R> collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends R> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

    @Override
    MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    MutableByteList collectByte(ByteFunction<? super V> byteFunction);

    @Override
    MutableCharList collectChar(CharFunction<? super V> charFunction);

    @Override
    MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    MutableFloatList collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    MutableIntList collectInt(IntFunction<? super V> intFunction);

    @Override
    MutableLongList collectLong(LongFunction<? super V> longFunction);

    @Override
    MutableShortList collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <P, VV> MutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter);

    @Override
    <R> MutableList<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function);

    @Override
    <R> MutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, R> MutableList<R> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<R>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    MutableSortedMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    MutableList<V> select(Predicate<? super V> predicate);

    @Override
    <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    MutableList<V> reject(Predicate<? super V> predicate);

    @Override
    <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionMutableList<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionMutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <S> MutableList<S> selectInstancesOf(Class<S> clazz);

    @Override
    <S> MutableList<Pair<V, S>> zip(Iterable<S> that);

    @Override
    MutableList<Pair<V, Integer>> zipWithIndex();

    @Override
    MutableSortedMap<K, V> toReversed();

    @Override
    MutableSortedMap<K, V> take(int count);

    @Override
    MutableSortedMap<K, V> takeWhile(Predicate<? super V> predicate);

    @Override
    MutableSortedMap<K, V> drop(int count);

    @Override
    MutableSortedMap<K, V> dropWhile(Predicate<? super V> predicate);

    @Override
    PartitionMutableList<V> partitionWhile(Predicate<? super V> predicate);

    @Override
    MutableList<V> distinct();

    @Override
    MutableSet<Entry<K, V>> entrySet();

    /**
     * The underlying set for the keys is sorted in ascending order according to their natural ordering or a custom comparator.
     * However, Java 5 TreeMap returns a keySet that does not inherit from SortedSet therefore we have decided to
     * return the keySet simply as a MutableSet to maintain Java 5 compatibility.
     */
    //todo: Change return type to MutableSortedSet when we move to Java 6
    @Override
    MutableSet<K> keySet();

    @Override
    MutableSortedMap<K, V> headMap(K toKey);

    @Override
    MutableSortedMap<K, V> tailMap(K fromKey);

    @Override
    MutableSortedMap<K, V> subMap(K fromKey, K toKey);

    @Override
    MutableCollection<V> values();

    MutableSortedMap<K, V> clone();

    @Override
    <VV> MutableListMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function);

    @Override
    <VV> MutableListMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function);

    @Override
    default <VV> MutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return this.groupByUniqueKey(function, Maps.mutable.withInitialCapacity(this.size()));
    }

    // TODO: When we have implementations of linked hash maps
    // MutableOrderedMap<V, K> flipUniqueValues();

    @Override
    MutableSortedMap<K, V> withKeyValue(K key, V value);

    @Override
    default MutableSortedMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        this.putAll(map);
        return this;
    }

    @Override
    default MutableSortedMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        this.putAllMapIterable(mapIterable);
        return this;
    }

    @Override
    MutableSortedMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    @Override
    MutableSortedMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    /**
     * @deprecated in 6.0 Use {@link #withAllKeyValueArguments(Pair[])} instead. Inlineable.
     */
    @Deprecated
    default MutableSortedMap<K, V> with(Pair<K, V>... pairs)
    {
        return this.withAllKeyValueArguments(pairs);
    }

    @Override
    MutableSortedMap<K, V> withoutKey(K key);

    @Override
    MutableSortedMap<K, V> withoutAllKeys(Iterable<? extends K> keys);
}
