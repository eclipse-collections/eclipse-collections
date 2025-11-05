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
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.api.partition.PartitionImmutableCollection;
import org.eclipse.collections.api.tuple.Pair;

/**
 * ImmutableMapIterable is the common interface between ImmutableMap and ImmutableOrderedMap.
 * It provides read-only access to map operations and methods that return new immutable instances
 * when modifications would normally occur. All operations that would mutate the map return new
 * immutable copies instead, preserving the immutability guarantee.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2);
 * ImmutableMapIterable<String, Integer> updated = map.newWithKeyValue("C", 3);
 * // Original map is unchanged, updated is a new instance with three entries
 * }</pre>
 */
public interface ImmutableMapIterable<K, V> extends MapIterable<K, V>
{
    /**
     * Casts this ImmutableMapIterable to a Map. This is primarily used for interoperability
     * with code expecting java.util.Map.
     *
     * @return this map as a Map
     */
    Map<K, V> castToMap();

    /**
     * Returns a new ImmutableMapIterable with an additional key-value pair. If the key already
     * exists, the value is replaced in the new instance. The original map is not modified.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1);
     * ImmutableMapIterable<String, Integer> newMap = map.newWithKeyValue("B", 2);
     * // map still contains only "A" -> 1
     * // newMap contains "A" -> 1, "B" -> 2
     * }</pre>
     *
     * @param key the key to add
     * @param value the value to associate with the key
     * @return a new ImmutableMapIterable with the added key-value pair
     */
    ImmutableMapIterable<K, V> newWithKeyValue(K key, V value);

    /**
     * Returns a new ImmutableMapIterable with all the key-value pairs from the specified iterable
     * added. The original map is not modified.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1);
     * Iterable<Pair<String, Integer>> pairs = Lists.mutable.of(Tuples.pair("B", 2), Tuples.pair("C", 3));
     * ImmutableMapIterable<String, Integer> newMap = map.newWithAllKeyValues(pairs);
     * }</pre>
     *
     * @param keyValues the key-value pairs to add
     * @return a new ImmutableMapIterable with all the key-value pairs added
     */
    ImmutableMapIterable<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    /**
     * Returns a new ImmutableMapIterable with all the key-value pairs from the specified Map added.
     * The original map is not modified.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1);
     * Map<String, Integer> javaMap = new HashMap<>();
     * javaMap.put("B", 2);
     * javaMap.put("C", 3);
     * ImmutableMapIterable<String, Integer> newMap = map.newWithMap(javaMap);
     * }</pre>
     *
     * @param map the Map containing key-value pairs to add
     * @return a new ImmutableMapIterable with all the key-value pairs added
     */
    ImmutableMapIterable<K, V> newWithMap(Map<? extends K, ? extends V> map);

    /**
     * Returns a new ImmutableMapIterable with all the key-value pairs from the specified
     * MapIterable added. The original map is not modified.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1);
     * MapIterable<String, Integer> other = Maps.mutable.of("B", 2, "C", 3);
     * ImmutableMapIterable<String, Integer> newMap = map.newWithMapIterable(other);
     * }</pre>
     *
     * @param mapIterable the MapIterable containing key-value pairs to add
     * @return a new ImmutableMapIterable with all the key-value pairs added
     */
    ImmutableMapIterable<K, V> newWithMapIterable(MapIterable<? extends K, ? extends V> mapIterable);

    /**
     * Returns a new ImmutableMapIterable with all the specified key-value pairs added.
     * The original map is not modified.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1);
     * ImmutableMapIterable<String, Integer> newMap = map.newWithAllKeyValueArguments(
     *     Tuples.pair("B", 2),
     *     Tuples.pair("C", 3)
     * );
     * }</pre>
     *
     * @param keyValuePairs the key-value pairs to add
     * @return a new ImmutableMapIterable with all the key-value pairs added
     */
    ImmutableMapIterable<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    /**
     * Returns a new ImmutableMapIterable with the specified key removed. If the key does not exist,
     * returns this map. The original map is not modified.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2);
     * ImmutableMapIterable<String, Integer> newMap = map.newWithoutKey("A");
     * // newMap contains only "B" -> 2
     * }</pre>
     *
     * @param key the key to remove
     * @return a new ImmutableMapIterable without the specified key
     */
    ImmutableMapIterable<K, V> newWithoutKey(K key);

    /**
     * Returns a new ImmutableMapIterable with all the specified keys removed. Keys that do not
     * exist are ignored. The original map is not modified.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * ImmutableMapIterable<String, Integer> newMap = map.newWithoutAllKeys(Lists.mutable.of("A", "C"));
     * // newMap contains only "B" -> 2
     * }</pre>
     *
     * @param keys the keys to remove
     * @return a new ImmutableMapIterable without the specified keys
     */
    ImmutableMapIterable<K, V> newWithoutAllKeys(Iterable<? extends K> keys);

    // TODO
    // ImmutableSetIterable<K> keySet();

    /**
     * Executes the given procedure for each value in the map and returns this map.
     * This method is useful for chaining method calls while performing side effects.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2);
     * map.tap(value -> System.out.println("Value: " + value));
     * }</pre>
     *
     * @param procedure the procedure to execute for each value
     * @return this map
     */
    @Override
    ImmutableMapIterable<K, V> tap(Procedure<? super V> procedure);

    /**
     * Returns a new map with the keys and values flipped. This operation assumes all values
     * are unique. If duplicate values exist, an IllegalStateException is thrown.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2);
     * ImmutableMapIterable<Integer, String> flipped = map.flipUniqueValues();
     * // flipped contains: 1 -> "A", 2 -> "B"
     * }</pre>
     *
     * @return a new map with keys and values flipped
     * @throws IllegalStateException if the map contains duplicate values
     */
    @Override
    ImmutableMapIterable<V, K> flipUniqueValues();

    /**
     * Returns a multimap where the keys are the values from this map and the values are
     * the keys from this map. This allows for duplicate values to be properly handled.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 1, "C", 2);
     * ImmutableMultimap<Integer, String> flipped = map.flip();
     * // flipped contains: 1 -> ["A", "B"], 2 -> ["C"]
     * }</pre>
     *
     * @return a new multimap with keys and values flipped
     */
    @Override
    ImmutableMultimap<V, K> flip();

    /**
     * Returns a new map containing only the key-value pairs that match the given predicate.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * ImmutableMapIterable<String, Integer> selected = map.select((k, v) -> v > 1);
     * // selected contains: "B" -> 2, "C" -> 3
     * }</pre>
     *
     * @param predicate the predicate to evaluate each key-value pair
     * @return a new map containing only matching key-value pairs
     */
    @Override
    ImmutableMapIterable<K, V> select(Predicate2<? super K, ? super V> predicate);

    /**
     * Returns a new map containing only the key-value pairs that do not match the given predicate.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * ImmutableMapIterable<String, Integer> rejected = map.reject((k, v) -> v > 1);
     * // rejected contains: "A" -> 1
     * }</pre>
     *
     * @param predicate the predicate to evaluate each key-value pair
     * @return a new map containing only non-matching key-value pairs
     */
    @Override
    ImmutableMapIterable<K, V> reject(Predicate2<? super K, ? super V> predicate);

    /**
     * Returns a new map by transforming each key-value pair using the given function.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2);
     * ImmutableMapIterable<Integer, String> collected = map.collect((k, v) -> Tuples.pair(v, k.toLowerCase()));
     * // collected contains: 1 -> "a", 2 -> "b"
     * }</pre>
     *
     * @param <K2> the type of keys in the new map
     * @param <V2> the type of values in the new map
     * @param function the function to transform each key-value pair
     * @return a new map with transformed key-value pairs
     */
    @Override
    <K2, V2> ImmutableMapIterable<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    /**
     * Returns a new map with the same keys but with values transformed by the given function.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2);
     * ImmutableMapIterable<String, String> collected = map.collectValues((k, v) -> k + v);
     * // collected contains: "A" -> "A1", "B" -> "B2"
     * }</pre>
     *
     * @param <R> the type of values in the new map
     * @param function the function to transform each value
     * @return a new map with transformed values
     */
    @Override
    <R> ImmutableMapIterable<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    /**
     * Returns a new map with the same values but with keys transformed by the given function.
     * The function must produce unique keys for each entry.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2);
     * ImmutableMapIterable<String, Integer> collected = map.collectKeysUnique((k, v) -> k.toLowerCase());
     * // collected contains: "a" -> 1, "b" -> 2
     * }</pre>
     *
     * @param <R> the type of keys in the new map
     * @param function the function to transform each key
     * @return a new map with transformed keys
     * @throws IllegalStateException if the function produces duplicate keys
     */
    @Override
    <R> ImmutableMapIterable<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    /**
     * Returns a new collection containing only the values that match the given predicate.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * ImmutableCollection<Integer> selected = map.select(v -> v > 1);
     * // selected contains: [2, 3]
     * }</pre>
     *
     * @param predicate the predicate to evaluate each value
     * @return a new collection containing only matching values
     */
    @Override
    ImmutableCollection<V> select(Predicate<? super V> predicate);

    /**
     * Returns a new collection containing only the values that match the given predicate with parameter.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * ImmutableCollection<Integer> selected = map.selectWith((v, p) -> v > p, 1);
     * // selected contains: [2, 3]
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param predicate the predicate to evaluate each value
     * @param parameter the parameter to pass to the predicate
     * @return a new collection containing only matching values
     */
    @Override
    <P> ImmutableCollection<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    /**
     * Returns a new collection containing only the values that do not match the given predicate.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * ImmutableCollection<Integer> rejected = map.reject(v -> v > 1);
     * // rejected contains: [1]
     * }</pre>
     *
     * @param predicate the predicate to evaluate each value
     * @return a new collection containing only non-matching values
     */
    @Override
    ImmutableCollection<V> reject(Predicate<? super V> predicate);

    /**
     * Returns a new collection containing only the values that do not match the given predicate with parameter.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * ImmutableCollection<Integer> rejected = map.rejectWith((v, p) -> v > p, 1);
     * // rejected contains: [1]
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param predicate the predicate to evaluate each value
     * @param parameter the parameter to pass to the predicate
     * @return a new collection containing only non-matching values
     */
    @Override
    <P> ImmutableCollection<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    /**
     * Partitions the values into two collections based on the given predicate.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * PartitionImmutableCollection<Integer> partition = map.partition(v -> v > 1);
     * // partition.getSelected() contains: [2, 3]
     * // partition.getRejected() contains: [1]
     * }</pre>
     *
     * @param predicate the predicate to evaluate each value
     * @return a partition containing selected and rejected values
     */
    @Override
    PartitionImmutableCollection<V> partition(Predicate<? super V> predicate);

    /**
     * Returns a new collection containing only the values that are instances of the specified class.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Number> map = Maps.immutable.of("A", 1, "B", 2.0, "C", 3);
     * ImmutableCollection<Integer> integers = map.selectInstancesOf(Integer.class);
     * // integers contains: [1, 3]
     * }</pre>
     *
     * @param <S> the type to select
     * @param clazz the class of the type to select
     * @return a new collection containing only values of the specified type
     */
    @Override
    <S> ImmutableCollection<S> selectInstancesOf(Class<S> clazz);

    /**
     * Groups the values in this map by the result of applying the given function to each value,
     * and returns a bag of the group keys.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 1);
     * ImmutableBag<Integer> counts = map.countBy(v -> v);
     * // counts contains: 1 (count 2), 2 (count 1)
     * }</pre>
     *
     * @param <V1> the type of the group keys
     * @param function the function to group values by
     * @return a bag of group keys with their occurrence counts
     * @since 9.0
     */
    @Override
    default <V1> ImmutableBag<V1> countBy(Function<? super V, ? extends V1> function)
    {
        return this.asLazy().<V1>collect(function).toBag().toImmutable();
    }

    /**
     * Groups the values in this map by the result of applying the given function with parameter
     * to each value, and returns a bag of the group keys.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * ImmutableBag<Boolean> counts = map.countByWith((v, p) -> v > p, 1);
     * // counts contains: true (count 2), false (count 1)
     * }</pre>
     *
     * @param <V1> the type of the group keys
     * @param <P> the type of the parameter
     * @param function the function to group values by
     * @param parameter the parameter to pass to the function
     * @return a bag of group keys with their occurrence counts
     * @since 9.0
     */
    @Override
    default <V1, P> ImmutableBag<V1> countByWith(Function2<? super V, ? super P, ? extends V1> function, P parameter)
    {
        return this.asLazy().<P, V1>collectWith(function, parameter).toBag().toImmutable();
    }

    /**
     * Groups the values in this map by the result of applying the given function to each value,
     * where the function returns an iterable of group keys. Returns a bag of all group keys.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, String> map = Maps.immutable.of("A", "ab", "B", "cd");
     * ImmutableBag<Character> counts = map.countByEach(s -> Lists.mutable.with(s.charAt(0), s.charAt(1)));
     * // counts contains character frequencies
     * }</pre>
     *
     * @param <V1> the type of the group keys
     * @param function the function returning an iterable of group keys for each value
     * @return a bag of all group keys with their occurrence counts
     * @since 10.0.0
     */
    @Override
    default <V1> ImmutableBag<V1> countByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return this.asLazy().flatCollect(function).toBag().toImmutable();
    }

    /**
     * Groups the values in this map by the result of applying the given function to each value,
     * and returns a multimap from group keys to values.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 1);
     * ImmutableMultimap<Integer, Integer> grouped = map.groupBy(v -> v);
     * // grouped contains: 1 -> [1, 1], 2 -> [2]
     * }</pre>
     *
     * @param <V1> the type of the group keys
     * @param function the function to group values by
     * @return a multimap from group keys to values
     */
    @Override
    <V1> ImmutableMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    /**
     * Groups the values in this map by the result of applying the given function to each value,
     * where the function returns an iterable of group keys.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, String> map = Maps.immutable.of("A", "ab", "B", "cd");
     * ImmutableMultimap<Character, String> grouped = map.groupByEach(s -> Lists.mutable.with(s.charAt(0), s.charAt(1)));
     * }</pre>
     *
     * @param <V1> the type of the group keys
     * @param function the function returning an iterable of group keys for each value
     * @return a multimap from group keys to values
     */
    @Override
    <V1> ImmutableMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    /**
     * Groups the values in this map by the result of applying the given function to each value,
     * where each value must produce a unique key. Returns a map from group keys to values.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Person> map = Maps.immutable.of("A", person1, "B", person2);
     * ImmutableMapIterable<Integer, Person> grouped = map.groupByUniqueKey(Person::getId);
     * }</pre>
     *
     * @param <V1> the type of the group keys
     * @param function the function to extract a unique key from each value
     * @return a map from group keys to values
     * @throws IllegalStateException if the function does not produce unique keys
     */
    @Override
    <V1> ImmutableMapIterable<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function);

    /**
     * Zips the values in this map with the elements from the specified iterable, creating pairs.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2);
     * ImmutableCollection<Pair<Integer, String>> zipped = map.zip(Lists.mutable.of("X", "Y"));
     * // zipped contains: (1, "X"), (2, "Y")
     * }</pre>
     *
     * @param <S> the type of elements in the iterable to zip with
     * @param that the iterable to zip with
     * @return a collection of pairs
     */
    @Override
    <S> ImmutableCollection<Pair<V, S>> zip(Iterable<S> that);

    /**
     * Zips the values in this map with their indices, creating pairs of values and integers.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, String> map = Maps.immutable.of("A", "X", "B", "Y");
     * ImmutableCollection<Pair<String, Integer>> zipped = map.zipWithIndex();
     * // zipped contains: ("X", 0), ("Y", 1)
     * }</pre>
     *
     * @return a collection of pairs of values and their indices
     */
    @Override
    ImmutableCollection<Pair<V, Integer>> zipWithIndex();

    /**
     * Aggregates the values in this map by grouping them according to the groupBy function,
     * and applying the mutating aggregator to accumulate values in each group. The zero value
     * factory creates the initial aggregated value for each group.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 1);
     * ImmutableMapIterable<Integer, MutableList<Integer>> aggregated =
     *     map.aggregateInPlaceBy(v -> v, () -> Lists.mutable.empty(), MutableList::add);
     * // aggregated contains: 1 -> [1, 1], 2 -> [2]
     * }</pre>
     *
     * @param <KK> the type of the group keys
     * @param <VV> the type of the aggregated values
     * @param groupBy the function to group values by
     * @param zeroValueFactory the factory to create initial values for each group
     * @param mutatingAggregator the procedure to aggregate values in place
     * @return a new map from group keys to aggregated values
     */
    @Override
    default <KK, VV> ImmutableMapIterable<KK, VV> aggregateInPlaceBy(
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
     * Aggregates the values in this map by grouping them according to the groupBy function,
     * and applying the non-mutating aggregator to combine values in each group. The zero value
     * factory creates the initial aggregated value for each group.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 1);
     * ImmutableMapIterable<Integer, Integer> aggregated =
     *     map.aggregateBy(v -> v, () -> 0, (sum, value) -> sum + value);
     * // aggregated contains: 1 -> 2, 2 -> 2
     * }</pre>
     *
     * @param <KK> the type of the group keys
     * @param <VV> the type of the aggregated values
     * @param groupBy the function to group values by
     * @param zeroValueFactory the factory to create initial values for each group
     * @param nonMutatingAggregator the function to combine values
     * @return a new map from group keys to aggregated values
     */
    @Override
    default <KK, VV> ImmutableMapIterable<KK, VV> aggregateBy(
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
     * Aggregates this map by transforming both keys and values, then grouping the transformed
     * values by the transformed keys and aggregating them using the non-mutating aggregator.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2);
     * ImmutableMapIterable<String, Integer> aggregated =
     *     map.aggregateBy(k -> k.toLowerCase(), v -> v * 2, () -> 0, (sum, v) -> sum + v);
     * }</pre>
     *
     * @param <K1> the type of the transformed keys
     * @param <V1> the type of the transformed values
     * @param <V2> the type of the aggregated values
     * @param keyFunction the function to transform keys
     * @param valueFunction the function to transform values
     * @param zeroValueFactory the factory to create initial aggregated values
     * @param nonMutatingAggregator the function to combine values
     * @return a new map from transformed keys to aggregated values
     */
    @Override
    default <K1, V1, V2> ImmutableMapIterable<K1, V2> aggregateBy(
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

    /**
     * Reduces the values in this map by grouping them according to the groupBy function,
     * and applying the reduce function to combine values in each group.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableMapIterable<String, Integer> map = Maps.immutable.of("A", 1, "B", 2, "C", 3);
     * ImmutableMapIterable<Boolean, Integer> reduced =
     *     map.reduceBy(v -> v % 2 == 0, Integer::sum);
     * // reduced contains: false -> 4 (1+3), true -> 2
     * }</pre>
     *
     * @param <KK> the type of the group keys
     * @param groupBy the function to group values by
     * @param reduceFunction the function to combine values
     * @return a new map from group keys to reduced values
     */
    @Override
    default <KK> ImmutableMapIterable<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        MutableMap<KK, V> map = this.reduceBy(
                groupBy,
                reduceFunction,
                Maps.mutable.empty());
        return map.toImmutable();
    }
}
