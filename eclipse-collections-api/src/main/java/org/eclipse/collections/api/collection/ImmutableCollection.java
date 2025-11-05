/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.collection;

import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
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
import org.eclipse.collections.api.collection.primitive.ImmutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableByteCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableCharCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableIntCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableShortCollection;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.ImmutableMapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.api.partition.PartitionImmutableCollection;
import org.eclipse.collections.api.tuple.Pair;

/**
 * ImmutableCollection is the common interface between ImmutableList, ImmutableSet and ImmutableBag.
 * The ImmutableCollection interface is "contractually immutable" in that it does not have any mutating
 * methods available on the public interface. All modification operations return new instances rather than
 * modifying the existing collection.
 *
 * <p>This interface provides a thread-safe, immutable view of a collection. Once created, the contents
 * cannot be changed. Any method that appears to modify the collection actually returns a new immutable
 * collection with the changes applied, leaving the original collection unchanged.</p>
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableCollection<String> collection = Lists.immutable.of("A", "B", "C");
 *
 * // Creating new instances with modified elements
 * ImmutableCollection<String> withNew = collection.newWith("D");         // Returns ["A", "B", "C", "D"]
 * ImmutableCollection<String> withoutOld = collection.newWithout("A");   // Returns ["B", "C"]
 *
 * // Original collection remains unchanged
 * System.out.println(collection);  // Still prints ["A", "B", "C"]
 *
 * // Filtering and transformation operations
 * ImmutableCollection<String> filtered = collection.select(s -> s.compareTo("B") >= 0);  // Returns ["B", "C"]
 * ImmutableCollection<Integer> lengths = collection.collect(String::length);              // Returns [1, 1, 1]
 * }</pre>
 */
public interface ImmutableCollection<T>
        extends RichIterable<T>
{
    /**
     * Returns a new ImmutableCollection with the specified element added. The original collection remains unchanged.
     * This method is similar to the {@code with} method in {@code MutableCollection}, but returns a new instance
     * rather than modifying the existing collection.
     *
     * @param element the element to add to the new collection
     * @return a new ImmutableCollection containing all elements from this collection plus the new element
     */
    ImmutableCollection<T> newWith(T element);

    /**
     * Returns a new ImmutableCollection with the specified element removed. The original collection remains unchanged.
     * This method is similar to the {@code without} method in {@code MutableCollection}, but returns a new instance
     * rather than modifying the existing collection.
     *
     * @param element the element to remove from the new collection
     * @return a new ImmutableCollection containing all elements from this collection except the specified element
     */
    ImmutableCollection<T> newWithout(T element);

    /**
     * Returns a new ImmutableCollection with all specified elements added. The original collection remains unchanged.
     * This method is similar to the {@code withAll} method in {@code MutableCollection}, but returns a new instance
     * rather than modifying the existing collection.
     *
     * @param elements the elements to add to the new collection
     * @return a new ImmutableCollection containing all elements from this collection plus all the new elements
     */
    ImmutableCollection<T> newWithAll(Iterable<? extends T> elements);

    /**
     * Returns a new ImmutableCollection with all specified elements removed. The original collection remains unchanged.
     * This method is similar to the {@code withoutAll} method in {@code MutableCollection}, but returns a new instance
     * rather than modifying the existing collection.
     *
     * @param elements the elements to remove from the new collection
     * @return a new ImmutableCollection containing all elements from this collection except the specified elements
     */
    ImmutableCollection<T> newWithoutAll(Iterable<? extends T> elements);

    /**
     * Executes the given procedure on each element in this collection and returns this collection unchanged.
     * This method is useful for performing side effects while chaining method calls.
     *
     * @param procedure the procedure to execute on each element
     * @return this ImmutableCollection to allow method chaining
     */
    @Override
    ImmutableCollection<T> tap(Procedure<? super T> procedure);

    /**
     * Returns a new ImmutableCollection containing only the elements that satisfy the given predicate.
     * The original collection remains unchanged.
     *
     * @param predicate the predicate to evaluate each element
     * @return a new ImmutableCollection containing only elements for which the predicate returns true
     */
    @Override
    ImmutableCollection<T> select(Predicate<? super T> predicate);

    /**
     * Returns a new ImmutableCollection containing only the elements that satisfy the given predicate with a parameter.
     * The original collection remains unchanged.
     *
     * @param predicate the predicate to evaluate each element with the parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return a new ImmutableCollection containing only elements for which the predicate returns true
     */
    @Override
    <P> ImmutableCollection<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns a new ImmutableCollection containing only the elements that do not satisfy the given predicate.
     * This is the logical inverse of {@link #select(Predicate)}. The original collection remains unchanged.
     *
     * @param predicate the predicate to evaluate each element
     * @return a new ImmutableCollection containing only elements for which the predicate returns false
     */
    @Override
    ImmutableCollection<T> reject(Predicate<? super T> predicate);

    /**
     * Returns a new ImmutableCollection containing only the elements that do not satisfy the given predicate with a parameter.
     * This is the logical inverse of {@link #selectWith(Predicate2, Object)}. The original collection remains unchanged.
     *
     * @param predicate the predicate to evaluate each element with the parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return a new ImmutableCollection containing only elements for which the predicate returns false
     */
    @Override
    <P> ImmutableCollection<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Partitions this collection into two new ImmutableCollections based on the given predicate.
     * Elements that satisfy the predicate are placed in the selected partition, while elements
     * that do not satisfy the predicate are placed in the rejected partition.
     *
     * @param predicate the predicate to evaluate each element
     * @return a PartitionImmutableCollection containing both the selected and rejected elements
     */
    @Override
    PartitionImmutableCollection<T> partition(Predicate<? super T> predicate);

    /**
     * Partitions this collection into two new ImmutableCollections based on the given predicate with a parameter.
     * Elements that satisfy the predicate are placed in the selected partition, while elements
     * that do not satisfy the predicate are placed in the rejected partition.
     *
     * @param predicate the predicate to evaluate each element with the parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return a PartitionImmutableCollection containing both the selected and rejected elements
     */
    @Override
    <P> PartitionImmutableCollection<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns a new ImmutableCollection containing only the elements that are instances of the specified class.
     * This is a type-safe alternative to filtering by instanceof checks.
     *
     * @param clazz the class to filter by
     * @param <S> the type to filter to
     * @return a new ImmutableCollection containing only elements of the specified type
     */
    @Override
    <S> ImmutableCollection<S> selectInstancesOf(Class<S> clazz);

    /**
     * Transforms each element of this collection using the given function and returns a new ImmutableCollection
     * containing the transformed elements. The original collection remains unchanged.
     *
     * @param function the function to transform each element
     * @param <V> the type of elements in the resulting collection
     * @return a new ImmutableCollection containing the transformed elements
     */
    @Override
    <V> ImmutableCollection<V> collect(Function<? super T, ? extends V> function);

    /**
     * Transforms each element of this collection to a boolean value using the given function and returns
     * a new ImmutableBooleanCollection containing the transformed values.
     *
     * @param booleanFunction the function to transform each element to a boolean
     * @return a new ImmutableBooleanCollection containing the transformed boolean values
     */
    @Override
    ImmutableBooleanCollection collectBoolean(BooleanFunction<? super T> booleanFunction);

    /**
     * Transforms each element of this collection to a byte value using the given function and returns
     * a new ImmutableByteCollection containing the transformed values.
     *
     * @param byteFunction the function to transform each element to a byte
     * @return a new ImmutableByteCollection containing the transformed byte values
     */
    @Override
    ImmutableByteCollection collectByte(ByteFunction<? super T> byteFunction);

    /**
     * Transforms each element of this collection to a char value using the given function and returns
     * a new ImmutableCharCollection containing the transformed values.
     *
     * @param charFunction the function to transform each element to a char
     * @return a new ImmutableCharCollection containing the transformed char values
     */
    @Override
    ImmutableCharCollection collectChar(CharFunction<? super T> charFunction);

    /**
     * Transforms each element of this collection to a double value using the given function and returns
     * a new ImmutableDoubleCollection containing the transformed values.
     *
     * @param doubleFunction the function to transform each element to a double
     * @return a new ImmutableDoubleCollection containing the transformed double values
     */
    @Override
    ImmutableDoubleCollection collectDouble(DoubleFunction<? super T> doubleFunction);

    /**
     * Transforms each element of this collection to a float value using the given function and returns
     * a new ImmutableFloatCollection containing the transformed values.
     *
     * @param floatFunction the function to transform each element to a float
     * @return a new ImmutableFloatCollection containing the transformed float values
     */
    @Override
    ImmutableFloatCollection collectFloat(FloatFunction<? super T> floatFunction);

    /**
     * Transforms each element of this collection to an int value using the given function and returns
     * a new ImmutableIntCollection containing the transformed values.
     *
     * @param intFunction the function to transform each element to an int
     * @return a new ImmutableIntCollection containing the transformed int values
     */
    @Override
    ImmutableIntCollection collectInt(IntFunction<? super T> intFunction);

    /**
     * Transforms each element of this collection to a long value using the given function and returns
     * a new ImmutableLongCollection containing the transformed values.
     *
     * @param longFunction the function to transform each element to a long
     * @return a new ImmutableLongCollection containing the transformed long values
     */
    @Override
    ImmutableLongCollection collectLong(LongFunction<? super T> longFunction);

    /**
     * Transforms each element of this collection to a short value using the given function and returns
     * a new ImmutableShortCollection containing the transformed values.
     *
     * @param shortFunction the function to transform each element to a short
     * @return a new ImmutableShortCollection containing the transformed short values
     */
    @Override
    ImmutableShortCollection collectShort(ShortFunction<? super T> shortFunction);

    /**
     * Transforms each element of this collection using the given function with a parameter and returns
     * a new ImmutableCollection containing the transformed elements. The original collection remains unchanged.
     *
     * @param function the function to transform each element with the parameter
     * @param parameter the parameter to pass to the function
     * @param <P> the type of the parameter
     * @param <V> the type of elements in the resulting collection
     * @return a new ImmutableCollection containing the transformed elements
     */
    @Override
    <P, V> ImmutableCollection<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    /**
     * Filters elements using the given predicate and transforms the selected elements using the given function.
     * Returns a new ImmutableCollection containing only the transformed elements that satisfied the predicate.
     *
     * @param predicate the predicate to filter elements
     * @param function the function to transform selected elements
     * @param <V> the type of elements in the resulting collection
     * @return a new ImmutableCollection containing the transformed filtered elements
     */
    @Override
    <V> ImmutableCollection<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    /**
     * Transforms each element of this collection to an Iterable using the given function, then flattens
     * all resulting iterables into a single new ImmutableCollection. This is useful for one-to-many
     * transformations. The original collection remains unchanged.
     *
     * @param function the function to transform each element to an Iterable
     * @param <V> the type of elements in the resulting collection
     * @return a new ImmutableCollection containing all elements from all resulting iterables
     */
    @Override
    <V> ImmutableCollection<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Transforms each element of this collection to an Iterable using the given function with a parameter,
     * then flattens all resulting iterables into a single new ImmutableCollection. This is useful for
     * one-to-many transformations that require an additional parameter. The original collection remains unchanged.
     *
     * @param function the function to transform each element to an Iterable with the parameter
     * @param parameter the parameter to pass to the function
     * @param <P> the type of the parameter
     * @param <V> the type of elements in the resulting collection
     * @return a new ImmutableCollection containing all elements from all resulting iterables
     * @since 9.2
     */
    @Override
    default <P, V> ImmutableCollection<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * Groups elements by the groupBy function and sums the int values returned by the function for each group.
     * Returns an ImmutableObjectLongMap where keys are the group keys and values are the sums.
     *
     * @param groupBy the function to group elements
     * @param function the function to extract int values to sum
     * @param <V> the type of the group keys
     * @return an ImmutableObjectLongMap with group keys and their corresponding sums
     */
    @Override
    <V> ImmutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function);

    /**
     * Groups elements by the groupBy function and sums the float values returned by the function for each group.
     * Returns an ImmutableObjectDoubleMap where keys are the group keys and values are the sums.
     *
     * @param groupBy the function to group elements
     * @param function the function to extract float values to sum
     * @param <V> the type of the group keys
     * @return an ImmutableObjectDoubleMap with group keys and their corresponding sums
     */
    @Override
    <V> ImmutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function);

    /**
     * Groups elements by the groupBy function and sums the long values returned by the function for each group.
     * Returns an ImmutableObjectLongMap where keys are the group keys and values are the sums.
     *
     * @param groupBy the function to group elements
     * @param function the function to extract long values to sum
     * @param <V> the type of the group keys
     * @return an ImmutableObjectLongMap with group keys and their corresponding sums
     */
    @Override
    <V> ImmutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function);

    /**
     * Groups elements by the groupBy function and sums the double values returned by the function for each group.
     * Returns an ImmutableObjectDoubleMap where keys are the group keys and values are the sums.
     *
     * @param groupBy the function to group elements
     * @param function the function to extract double values to sum
     * @param <V> the type of the group keys
     * @return an ImmutableObjectDoubleMap with group keys and their corresponding sums
     */
    @Override
    <V> ImmutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function);

    /**
     * Groups each element by the result of applying the function and returns an ImmutableBag containing
     * the count of occurrences for each group key. This is useful for computing frequency distributions.
     *
     * @param function the function to group elements
     * @param <V> the type of the group keys
     * @return an ImmutableBag containing the group keys with their occurrence counts
     * @since 9.0
     */
    @Override
    default <V> ImmutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.asLazy().<V>collect(function).toBag().toImmutable();
    }

    /**
     * Groups each element by the result of applying the function with a parameter and returns an ImmutableBag
     * containing the count of occurrences for each group key. This is useful for computing frequency distributions
     * with parameterized grouping logic.
     *
     * @param function the function to group elements with the parameter
     * @param parameter the parameter to pass to the function
     * @param <V> the type of the group keys
     * @param <P> the type of the parameter
     * @return an ImmutableBag containing the group keys with their occurrence counts
     * @since 9.0
     */
    @Override
    default <V, P> ImmutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.asLazy().<P, V>collectWith(function, parameter).toBag().toImmutable();
    }

    /**
     * Groups each element by the results of applying the function (which returns an Iterable of keys) and returns
     * an ImmutableBag containing the count of occurrences for each group key. Each element can contribute to
     * multiple groups. This is useful for computing frequency distributions where elements belong to multiple categories.
     *
     * @param function the function to group elements, returning multiple keys per element
     * @param <V> the type of the group keys
     * @return an ImmutableBag containing the group keys with their occurrence counts
     * @since 10.0.0
     */
    @Override
    default <V> ImmutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.asLazy().flatCollect(function).toBag().toImmutable();
    }

    /**
     * Groups elements of this collection by the result of applying the given function to each element.
     * Returns an ImmutableMultimap where each key is a group key and each value is a collection of elements
     * that map to that key.
     *
     * @param function the function to group elements
     * @param <V> the type of the group keys
     * @return an ImmutableMultimap with group keys and their associated elements
     */
    @Override
    <V> ImmutableMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    /**
     * Groups elements of this collection by the results of applying the given function to each element, where
     * the function returns an Iterable of keys. Each element can belong to multiple groups. Returns an
     * ImmutableMultimap where each key is a group key and each value is a collection of elements that map to that key.
     *
     * @param function the function to group elements, returning multiple keys per element
     * @param <V> the type of the group keys
     * @return an ImmutableMultimap with group keys and their associated elements
     */
    @Override
    <V> ImmutableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Groups elements of this collection by the result of applying the given function to each element, where
     * each element maps to a unique key. Returns an ImmutableMap where each key is a group key and each value
     * is the single element that maps to that key. Throws an exception if multiple elements map to the same key.
     *
     * @param function the function to extract unique keys from elements
     * @param <V> the type of the group keys
     * @return an ImmutableMap with unique keys and their associated elements
     * @throws IllegalStateException if multiple elements map to the same key
     */
    @Override
    default <V> ImmutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        MutableMap<V, T> target = Maps.mutable.withInitialCapacity(this.size());
        return this.groupByUniqueKey(function, target).toImmutable();
    }

    /**
     * Combines elements from this collection with elements from the given Iterable by pairing them at the same index.
     * Returns a new ImmutableCollection of Pairs. The resulting collection will have a size equal to the minimum
     * of the two collections' sizes.
     *
     * @param that the Iterable to zip with this collection
     * @param <S> the type of elements in the other Iterable
     * @return a new ImmutableCollection of Pairs combining elements at the same indices
     */
    @Override
    <S> ImmutableCollection<Pair<T, S>> zip(Iterable<S> that);

    /**
     * Pairs each element in this collection with its index (starting from 0).
     * Returns a new ImmutableCollection of Pairs where the first element is from this collection
     * and the second element is the Integer index.
     *
     * @return a new ImmutableCollection of Pairs containing elements and their indices
     */
    @Override
    ImmutableCollection<Pair<T, Integer>> zipWithIndex();

    /**
     * Groups elements and aggregates values for each group using in-place mutation of the aggregation values.
     * For each element, computes a group key using the groupBy function, creates an initial value using
     * zeroValueFactory if needed, and mutates that value using the mutatingAggregator. Returns an ImmutableMap
     * with group keys and their aggregated values.
     *
     * @param groupBy the function to determine the group key for each element
     * @param zeroValueFactory the function to create initial aggregation values
     * @param mutatingAggregator the procedure to mutate the aggregation value with each element
     * @param <K> the type of the group keys
     * @param <V> the type of the aggregated values
     * @return an ImmutableMap with group keys and their aggregated values
     */
    @Override
    default <K, V> ImmutableMap<K, V> aggregateInPlaceBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        MutableMap<K, V> map = Maps.mutable.empty();
        this.forEach(each ->
        {
            K key = groupBy.valueOf(each);
            V value = map.getIfAbsentPut(key, zeroValueFactory);
            mutatingAggregator.value(value, each);
        });
        return map.toImmutable();
    }

    /**
     * Groups elements and aggregates values for each group using non-mutating aggregation functions.
     * For each element, computes a group key using the groupBy function, creates an initial value using
     * zeroValueFactory if needed, and combines that value with the element using the nonMutatingAggregator
     * to produce a new aggregated value. Returns an ImmutableMap with group keys and their aggregated values.
     *
     * @param groupBy the function to determine the group key for each element
     * @param zeroValueFactory the function to create initial aggregation values
     * @param nonMutatingAggregator the function to combine the current aggregated value with each element
     * @param <K> the type of the group keys
     * @param <V> the type of the aggregated values
     * @return an ImmutableMap with group keys and their aggregated values
     */
    @Override
    default <K, V> ImmutableMap<K, V> aggregateBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        MutableMap<K, V> map = this.aggregateBy(
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                Maps.mutable.empty());
        return map.toImmutable();
    }

    /**
     * Groups elements and reduces each group to a single value using the given reduce function.
     * For each group, the first element becomes the initial value, and subsequent elements are combined
     * with it using the reduceFunction. Returns an ImmutableMapIterable with group keys and their reduced values.
     *
     * @param groupBy the function to determine the group key for each element
     * @param reduceFunction the function to combine two elements into one
     * @param <K> the type of the group keys
     * @return an ImmutableMapIterable with group keys and their reduced values
     */
    @Override
    default <K> ImmutableMapIterable<K, T> reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction)
    {
        MutableMapIterable<K, T> map = this.reduceBy(groupBy, reduceFunction, Maps.mutable.empty());
        return map.toImmutable();
    }

    /**
     * Returns a sequential Stream with this collection as its source.
     * This method provides integration with Java 8 Streams API.
     *
     * @return a sequential Stream over the elements in this collection
     * @since 9.0
     */
    default Stream<T> stream()
    {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * Returns a possibly parallel Stream with this collection as its source.
     * This method provides integration with Java 8 Streams API for parallel processing.
     *
     * @return a possibly parallel Stream over the elements in this collection
     * @since 9.0
     */
    default Stream<T> parallelStream()
    {
        return StreamSupport.stream(this.spliterator(), true);
    }

    /**
     * Returns a Spliterator over the elements in this collection.
     * This method provides integration with Java 8 Streams API.
     *
     * @return a Spliterator over the elements in this collection
     * @since 9.0
     */
    @Override
    default Spliterator<T> spliterator()
    {
        return Spliterators.spliterator(this.iterator(), (long) this.size(), 0);
    }

    /**
     * Casts this ImmutableCollection to a java.util.Collection. This method allows interoperability
     * with code that requires a standard Java Collection. This can be overridden in most implementations
     * to just return this.
     *
     * @return this collection as a java.util.Collection
     * @since 9.0
     */
    Collection<T> castToCollection();
}
