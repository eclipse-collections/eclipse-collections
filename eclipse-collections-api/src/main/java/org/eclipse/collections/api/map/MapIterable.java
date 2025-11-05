/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.Function3;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A Read-only Map API, with the minor exception inherited from java.lang.Iterable. The method map.iterator().remove()
 * will throw an UnsupportedOperationException. MapIterable is the parent interface for all map types in Eclipse Collections,
 * providing a rich set of functional operations for working with key-value pairs. When iterated, it iterates over the values.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MapIterable<String, Integer> map = Maps.mutable.of("A", 1, "B", 2, "C", 3);
 * map.forEachKeyValue((key, value) -> System.out.println(key + ": " + value));
 * MapIterable<String, Integer> filtered = map.select((k, v) -> v > 1);
 * Integer value = map.getIfAbsentValue("D", 0);
 * }</pre>
 *
 * @param <K> the type of keys in the map
 * @param <V> the type of values in the map
 */
public interface MapIterable<K, V> extends RichIterable<V>
{
    /**
     * @see Map#get(Object)
     */
    V get(Object key);

    /**
     * @see Map#containsKey(Object)
     */
    boolean containsKey(Object key);

    /**
     * @see Map#containsValue(Object)
     */
    boolean containsValue(Object value);

    /**
     * Calls the procedure with each <em>value</em> of the map.
     * <pre>
     *     Set&lt;String&gt; result = UnifiedSet.newSet();
     *     MutableMap&lt;Integer, String&gt; map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
     *     map.<b>forEachValue</b>(new CollectionAddProcedure&lt;String&gt;(result));
     *     Verify.assertSetsEqual(UnifiedSet.newSetWith("One", "Two", "Three", "Four"), result);
     * </pre>
     */
    void forEachValue(Procedure<? super V> procedure);

    /**
     * Executes the Procedure for each value of the map and returns {@code this}.
     *
     * <pre>
     * return peopleByCity.<b>tap</b>(person -&gt; LOGGER.info(person.getName()));
     * </pre>
     *
     * @see #forEach(Procedure)
     * @since 6.0
     */
    @Override
    MapIterable<K, V> tap(Procedure<? super V> procedure);

    /**
     * Calls the {@code procedure} with each <em>key</em> of the map.
     * <pre>
     *     final Collection&lt;Integer&gt; result = new ArrayList&lt;Integer&gt;();
     *     MutableMap&lt;Integer, String&gt; map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
     *     map.<b>forEachKey</b>(new CollectionAddProcedure&lt;Integer&gt;(result));
     *     Verify.assertContainsAll(result, 1, 2, 3);
     * </pre>
     */
    void forEachKey(Procedure<? super K> procedure);

    /**
     * Calls the {@code procedure} with each <em>key-value</em> pair of the map.
     * <pre>
     *     final Collection&lt;String&gt; collection = new ArrayList&lt;String&gt;();
     *     MutableMap&lt;Integer, String&gt; map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three");
     *     map.<b>forEachKeyValue</b>((Integer key, String value) -&gt; collection.add(String.valueOf(key) + value));
     *     Verify.assertContainsAll(collection, "1One", "2Two", "3Three");
     * </pre>
     */
    void forEachKeyValue(Procedure2<? super K, ? super V> procedure);

    /**
     * Implements the {@code injectInto} pattern with each <em>key-value</em> pair of the map.
     * <pre>
     *     MapIterable&lt;Integer, Integer&gt; map1 = Maps.immutable.with(3, 3, 2, 2, 1, 1);
     *     Integer sum1 = map1.<b>injectIntoKeyValue</b>(0, (sum, key, value) -&gt; sum + key + value);
     *     assertEquals(12, sum1);
     * </pre>
     *
     * @since 11.1
     */
    default <IV> IV injectIntoKeyValue(
            IV injectedValue,
            Function3<? super IV, ? super K, ? super V, ? extends IV> function)
    {
        IV[] result = (IV[]) new Object[]{injectedValue};
        this.forEachKeyValue((key, value) -> result[0] = function.value(result[0], key, value));
        return result[0];
    }

    /**
     * Return the MapIterable that is obtained by flipping the direction of this map and making the associations
     * from value to key.
     * <pre>
     *     MapIterable&lt;Integer, String&gt; map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
     *     MapIterable&lt;String, Integer&gt; result = map.flipUniqueValues();
     *     Assert.assertTrue(result.equals(UnifiedMap.newWithKeysValues("1", 1, "2", 2, "3", 3)));
     * </pre>
     *
     * @throws IllegalStateException if the MapIterable contains duplicate values.
     * @since 5.0
     */
    MapIterable<V, K> flipUniqueValues();

    default V getOrDefault(Object key, V defaultValue)
    {
        return this.getIfAbsentValue((K) key, defaultValue);
    }

    /**
     * Return the value in the Map that corresponds to the specified key, or if there is no value at the key, return the
     * result of evaluating the specified Function0.
     */
    V getIfAbsent(K key, Function0<? extends V> function);

    /**
     * Return the value in the Map that corresponds to the specified key, or if there is no value at the key, return {@code value}.
     */
    V getIfAbsentValue(K key, V value);

    /**
     * Return the value in the Map that corresponds to the specified key, or if there is no value at the key, return the
     * result of evaluating the specified function and parameter.
     */
    <P> V getIfAbsentWith(K key, Function<? super P, ? extends V> function, P parameter);

    /**
     * If there is a value in the Map that corresponds to the specified key return the result of applying the specified
     * Function on the value, otherwise return null.
     */
    <A> A ifPresentApply(K key, Function<? super V, ? extends A> function);

    /**
     * Returns an unmodifiable lazy iterable wrapped around the keySet for the map.
     */
    RichIterable<K> keysView();

    /**
     * Returns an unmodifiable lazy iterable wrapped around the values for the map.
     */
    RichIterable<V> valuesView();

    /**
     * Returns an unmodifiable lazy iterable of key/value pairs wrapped around the entrySet for the map.
     */
    RichIterable<Pair<K, V>> keyValuesView();

    /**
     * Given a map from Domain {@code ->} Range return a multimap from Range {@code ->} Domain. We chose the name 'flip'
     * rather than 'invert' or 'transpose' since this method does not have the property of applying twice
     * returns the original.
     * <p>
     * Since the keys in the input are unique, the values in the output are unique, so the return type should
     * be a SetMultimap. However, since SetMultimap and SortedSetMultimap don't inherit from one another, SetMultimap
     * here does not allow SortedMapIterable to have a SortedSetMultimap return. Thus, we compromise and call this
     * Multimap, even though all implementations will be a SetMultimap or SortedSetMultimap.
     *
     * @since 5.0
     */
    Multimap<V, K> flip();

    /**
     * For each key and value of the map the predicate is evaluated, if the result of the evaluation is true,
     * that key and value are returned in a new map.
     *
     * <pre>
     * MapIterable&lt;City, Person&gt; selected =
     *     peopleByCity.select((city, person) -&gt; city.getName().equals("Anytown") &amp;&amp; person.getLastName().equals("Smith"));
     * </pre>
     */
    MapIterable<K, V> select(Predicate2<? super K, ? super V> predicate);

    /**
     * For each key and value of the map the predicate is evaluated, if the result of the evaluation is false,
     * that key and value are returned in a new map.
     *
     * <pre>
     * MapIterable&lt;City, Person&gt; rejected =
     *     peopleByCity.reject((city, person) -&gt; city.getName().equals("Anytown") &amp;&amp; person.getLastName().equals("Smith"));
     * </pre>
     */
    MapIterable<K, V> reject(Predicate2<? super K, ? super V> predicate);

    /**
     * For each key and value of the map the function is evaluated. The results of these evaluations are returned in
     * a new map. The map returned will use the values projected from the function rather than the original values.
     *
     * <pre>
     * MapIterable&lt;String, String&gt; collected =
     *     peopleByCity.collect((City city, Person person) -&gt; Pair.of(city.getCountry(), person.getAddress().getCity()));
     * </pre>
     */
    <K2, V2> MapIterable<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    /**
     * For each key and value of the map the function is evaluated. The results of these evaluations are returned in
     * a new map. The map returned will use the values projected from the function rather than the original values.
     *
     * <pre>
     * MapIterable&lt;City, String&gt; collected =
     *     peopleByCity.collectValues((City city, Person person) -&gt; person.getFirstName() + " " + person.getLastName());
     * </pre>
     */
    <R> MapIterable<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    /**
     * For each key and value of the map the function is evaluated. The results of these evaluations are returned in
     * a new map. The map returned will use the keys projected from the function rather than the original keys.
     *
     * @throws IllegalStateException if the keys returned by the function are not unique
     *
     * @since 12.0
     */
    <R> MapIterable<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    /**
     * Return the first key and value of the map for which the predicate evaluates to true when they are given
     * as arguments. The predicate will only be evaluated until such pair is found or until all the keys and
     * values of the map have been used as arguments. That is, there may be keys and values of the map that are
     * never used as arguments to the predicate. The result is null if predicate does not evaluate to true for
     * any key/value combination.
     *
     * <pre>
     * Pair&lt;City, Person&gt; detected =
     *     peopleByCity.detect((City city, Person person) -&gt; city.getName().equals("Anytown") &amp;&amp; person.getLastName().equals("Smith"));
     * </pre>
     */
    Pair<K, V> detect(Predicate2<? super K, ? super V> predicate);

    /**
     * Return the first key and value of the map as an Optional for which the predicate evaluates to true when
     * they are given as arguments. The predicate will only be evaluated until such pair is found or until
     * all the keys and values of the map have been used as arguments. That is, there may be keys and values of
     * the map that are never used as arguments to the predicate.
     *
     * <pre>
     * Optional&lt;Pair&lt;City, Person&gt;&gt; detected =
     *     peopleByCity.detectOptional((city, person)
     *          -&gt; city.getName().equals("Anytown") &amp;&amp; person.getLastName().equals("Smith"));
     * </pre>
     */
    Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate);

    /**
     * Follows the same general contract as {@link Map#equals(Object)}.
     */
    @Override
    boolean equals(Object o);

    /**
     * Follows the same general contract as {@link Map#hashCode()}.
     */
    @Override
    int hashCode();

    /**
     * Returns a string with the keys and values of this map separated by commas with spaces and
     * enclosed in curly braces. Each key and value is separated by an equals sign.
     *
     * <pre>
     * Assert.assertEquals("{1=1, 2=2, 3=3}", Maps.mutable.with(1, 1, 2, 2, 3, 3).toString());
     * </pre>
     *
     * @return a string representation of this MapIterable
     * @see java.util.AbstractMap#toString()
     */
    @Override
    String toString();

    ImmutableMapIterable<K, V> toImmutable();

    /**
     * @since 9.0
     */
    default Stream<V> stream()
    {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * @since 9.0
     */
    default Stream<V> parallelStream()
    {
        return StreamSupport.stream(this.spliterator(), true);
    }

    /**
     * @since 9.0
     */
    @Override
    default Spliterator<V> spliterator()
    {
        return Spliterators.spliterator(this.iterator(), (long) this.size(), 0);
    }

    /**
     * Applies an aggregate function over the map grouping results into a map based on the specific key and value groupBy functions.
     * Aggregate results are allowed to be immutable as they will be replaced in place in the map. A second function
     * specifies the initial "zero" aggregate value to work with.
     *
     * <pre>
     * MapIterable&lt;String, Interval&gt; map = Maps.mutable.with("oneToFive", Interval.fromTo(1, 5), "sixToNine", Interval.fromTo(6, 9));
     *
     * MapIterable&lt;String, Long&gt; result = map.aggregateBy(
     *         eachKey -&gt; {
     *             return eachKey.equals("oneToFive")  ? "lessThanSix" : "greaterOrEqualsToSix";
     *         },
     *         each -&gt; each.sumOfInt(Integer::intValue),
     *         () -&gt; 0L,
     *         (argument1, argument2) -&gt; argument1 + argument2);
     *
     * MapIterable&lt;String, Long&gt; expected =
     *         Maps.mutable.with("lessThanSix", Interval.fromTo(1, 5).sumOfInt(Integer::intValue),
     *                 "greaterOrEqualsToSix", Interval.fromTo(6, 9).sumOfInt(Integer::intValue));
     * Assert.assertEquals(expected, result);
     * </pre>
     *
     * @since 10.3.0
     */
    default <K1, V1, V2> MapIterable<K1, V2> aggregateBy(
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

    default void forEach(BiConsumer<? super K, ? super V> action)
    {
        Objects.requireNonNull(action);
        this.forEachKeyValue(action::accept);
    }
}
