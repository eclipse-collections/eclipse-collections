/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api;

import java.util.Comparator;

import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;

/**
 * A ParallelIterable is a RichIterable that enables parallel processing of elements across multiple threads.
 * Like LazyIterable, it defers evaluation for certain methods like select, reject, collect, etc. However, when
 * evaluation is forced by a terminal operation, the processing occurs in parallel across multiple threads.
 * <p>
 * <b>Important:</b> All code blocks (predicates, functions, procedures) passed to ParallelIterable methods must be
 * stateless or thread-safe, as they will be executed concurrently by multiple threads. Mutable shared state
 * should be avoided or properly synchronized.
 * <p>
 * ParallelIterable is useful for CPU-intensive operations on large datasets where the overhead of parallelization
 * is outweighed by the benefits of concurrent processing.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Parallel filtering and counting
 * int count = largeList.asParallel(executor, batchSize)
 *     .select(person -> person.getAge() >= 18)
 *     .count(person -> person.isActive());
 *
 * // Parallel transformation to list
 * MutableList<String> names = people.asParallel(executor, batchSize)
 *     .collect(Person::getName)
 *     .toList();
 * }</pre>
 *
 * @param <T> the type of elements in the iterable
 * @since 5.0
 */
@Beta
public interface ParallelIterable<T>
{
    /**
     * Returns a ParallelIterable that contains only distinct elements from this iterable.
     * Duplicates are removed based on equals/hashCode comparison.
     * This operation may use additional memory to track unique elements during parallel processing.
     *
     * @return a new ParallelIterable containing only distinct elements
     */
    ParallelIterable<T> asUnique();

    /**
     * Creates a parallel iterable for selecting elements that satisfy the predicate.
     * The predicate will be executed in parallel across multiple threads, so it must be thread-safe.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ParallelIterable<Person> adults = people.asParallel(executor, batchSize)
     *     .select(person -> person.getAge() >= 18);
     * }</pre>
     *
     * @param predicate the thread-safe predicate to filter elements
     * @return a new ParallelIterable containing only elements that satisfy the predicate
     */
    ParallelIterable<T> select(Predicate<? super T> predicate);

    /**
     * Creates a parallel iterable for selecting elements using a predicate with an additional parameter.
     * The predicate will be executed in parallel across multiple threads, so it must be thread-safe.
     *
     * @param predicate the thread-safe predicate to filter elements, accepting an element and a parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return a new ParallelIterable containing only elements that satisfy the predicate
     */
    <P> ParallelIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a parallel iterable for selecting elements that are instances of the specified class.
     *
     * @param clazz the class to filter by
     * @param <S> the type to select
     * @return a new ParallelIterable containing only elements of the specified type
     */
    <S> ParallelIterable<S> selectInstancesOf(Class<S> clazz);

    /**
     * Creates a parallel iterable for rejecting elements that satisfy the predicate.
     * The predicate will be executed in parallel across multiple threads, so it must be thread-safe.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ParallelIterable<Person> active = people.asParallel(executor, batchSize)
     *     .reject(Person::isInactive);
     * }</pre>
     *
     * @param predicate the thread-safe predicate to reject elements
     * @return a new ParallelIterable containing only elements that do not satisfy the predicate
     */
    ParallelIterable<T> reject(Predicate<? super T> predicate);

    /**
     * Creates a parallel iterable for rejecting elements using a predicate with an additional parameter.
     * The predicate will be executed in parallel across multiple threads, so it must be thread-safe.
     *
     * @param predicate the thread-safe predicate to reject elements, accepting an element and a parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return a new ParallelIterable containing only elements that do not satisfy the predicate
     */
    <P> ParallelIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a parallel iterable for transforming elements using the specified function.
     * The function will be executed in parallel across multiple threads, so it must be thread-safe.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ParallelIterable<String> names = people.asParallel(executor, batchSize)
     *     .collect(Person::getName);
     * }</pre>
     *
     * @param function the thread-safe transformation function to apply to each element
     * @param <V> the type of the transformed elements
     * @return a new ParallelIterable containing the transformed elements
     */
    <V> ParallelIterable<V> collect(Function<? super T, ? extends V> function);

    /**
     * Creates a parallel iterable for transforming elements using a function with an additional parameter.
     * The function will be executed in parallel across multiple threads, so it must be thread-safe.
     *
     * @param function the thread-safe transformation function to apply to each element and parameter
     * @param parameter the parameter to pass to the function
     * @param <P> the type of the parameter
     * @param <V> the type of the transformed elements
     * @return a new ParallelIterable containing the transformed elements
     */
    <P, V> ParallelIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    /**
     * Creates a parallel iterable for selecting and transforming elements from the current iterable.
     * This combines filtering and transformation in a single operation for better performance.
     * Both the predicate and function will be executed in parallel, so they must be thread-safe.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ParallelIterable<String> adultNames = people.asParallel(executor, batchSize)
     *     .collectIf(person -> person.getAge() >= 18, Person::getName);
     * }</pre>
     *
     * @param predicate the thread-safe predicate to filter elements
     * @param function the thread-safe transformation function to apply to selected elements
     * @param <V> the type of the transformed elements
     * @return a new ParallelIterable containing the transformed elements that satisfied the predicate
     */
    <V> ParallelIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    /**
     * Creates a parallel flattening iterable for the current iterable.
     * Each element is transformed to an Iterable in parallel, and all results are flattened into a single iterable.
     * The function will be executed in parallel across multiple threads, so it must be thread-safe.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ParallelIterable<Address> allAddresses = people.asParallel(executor, batchSize)
     *     .flatCollect(Person::getAddresses);
     * }</pre>
     *
     * @param function the thread-safe function that transforms each element to an Iterable
     * @param <V> the type of elements in the resulting iterable
     * @return a new ParallelIterable containing all elements from the flattened iterables
     */
    <V> ParallelIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Executes the procedure for each element in parallel.
     * This is a terminal operation that forces evaluation. The procedure must be thread-safe.
     *
     * @param procedure the thread-safe procedure to execute for each element
     */
    void forEach(Procedure<? super T> procedure);

    /**
     * Executes the procedure for each element with the specified parameter in parallel.
     * This is a terminal operation that forces evaluation. The procedure must be thread-safe.
     *
     * @param procedure the thread-safe procedure to execute for each element and parameter
     * @param parameter the parameter to pass to the procedure
     * @param <P> the type of the parameter
     */
    <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter);

    /**
     * Returns the first element that satisfies the predicate, processing elements in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     * The returned element may be any of the matching elements due to parallel processing.
     *
     * @param predicate the thread-safe predicate to test elements
     * @return the first element found that satisfies the predicate, or null if none found
     */
    T detect(Predicate<? super T> predicate);

    /**
     * Returns the first element that satisfies the predicate with the specified parameter, processing in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements with a parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return the first element found that satisfies the predicate, or null if none found
     */
    <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns the first element that satisfies the predicate, or the result of evaluating the function if none found.
     * Processing occurs in parallel. Both the predicate and function must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements
     * @param function the thread-safe function to provide a default value
     * @return the first element found that satisfies the predicate, or the result of the function
     */
    T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function);

    /**
     * Returns the first element that satisfies the predicate with parameter, or the result of evaluating the function.
     * Processing occurs in parallel. Both the predicate and function must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements with a parameter
     * @param parameter the parameter to pass to the predicate
     * @param function the thread-safe function to provide a default value
     * @param <P> the type of the parameter
     * @return the first element found that satisfies the predicate, or the result of the function
     */
    <P> T detectWithIfNone(Predicate2<? super T, ? super P> predicate, P parameter, Function0<? extends T> function);

    /**
     * Returns the count of elements that satisfy the predicate, processing in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements
     * @return the number of elements that satisfy the predicate
     */
    int count(Predicate<? super T> predicate);

    /**
     * Returns the count of elements that satisfy the predicate with the specified parameter, processing in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements with a parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return the number of elements that satisfy the predicate
     */
    <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns true if any element satisfies the predicate, processing in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements
     * @return true if any element satisfies the predicate, false otherwise
     */
    boolean anySatisfy(Predicate<? super T> predicate);

    /**
     * Returns true if any element satisfies the predicate with the specified parameter, processing in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements with a parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return true if any element satisfies the predicate, false otherwise
     */
    <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns true if all elements satisfy the predicate, processing in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements
     * @return true if all elements satisfy the predicate, false otherwise
     */
    boolean allSatisfy(Predicate<? super T> predicate);

    /**
     * Returns true if all elements satisfy the predicate with the specified parameter, processing in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements with a parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return true if all elements satisfy the predicate, false otherwise
     */
    <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns true if no elements satisfy the predicate, processing in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements
     * @return true if no elements satisfy the predicate, false otherwise
     */
    boolean noneSatisfy(Predicate<? super T> predicate);

    /**
     * Returns true if no elements satisfy the predicate with the specified parameter, processing in parallel.
     * This is a terminal operation that forces evaluation. The predicate must be thread-safe.
     *
     * @param predicate the thread-safe predicate to test elements with a parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return true if no elements satisfy the predicate, false otherwise
     */
    <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Converts this ParallelIterable to a MutableList by processing elements in parallel.
     * This is a terminal operation that forces evaluation.
     *
     * @return a MutableList containing all elements
     */
    MutableList<T> toList();

    /**
     * Converts this ParallelIterable to a sorted MutableList using natural ordering.
     * This is a terminal operation that forces evaluation.
     *
     * @return a sorted MutableList containing all elements
     */
    default MutableList<T> toSortedList()
    {
        return this.toList().toSortedList();
    }

    /**
     * Converts this ParallelIterable to a MutableList sorted using the specified comparator.
     * This is a terminal operation that forces evaluation.
     *
     * @param comparator the comparator to determine the order
     * @return a sorted MutableList containing all elements
     */
    MutableList<T> toSortedList(Comparator<? super T> comparator);

    /**
     * Converts this ParallelIterable to a MutableList sorted by the attribute returned by the function.
     * This is a terminal operation that forces evaluation.
     *
     * @param function the function to extract the comparable attribute
     * @param <V> the type of the comparable attribute
     * @return a sorted MutableList containing all elements
     */
    <V extends Comparable<? super V>> MutableList<T> toSortedListBy(Function<? super T, ? extends V> function);

    /**
     * Converts this ParallelIterable to a MutableSet by processing elements in parallel.
     * This is a terminal operation that forces evaluation.
     *
     * @return a MutableSet containing all unique elements
     */
    MutableSet<T> toSet();

    /**
     * Converts this ParallelIterable to a MutableSortedSet using natural ordering.
     * This is a terminal operation that forces evaluation.
     *
     * @return a MutableSortedSet containing all unique elements
     */
    MutableSortedSet<T> toSortedSet();

    /**
     * Converts this ParallelIterable to a MutableSortedSet sorted using the specified comparator.
     * This is a terminal operation that forces evaluation.
     *
     * @param comparator the comparator to determine the order
     * @return a MutableSortedSet containing all unique elements
     */
    MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator);

    /**
     * Converts this ParallelIterable to a MutableSortedSet sorted by the attribute returned by the function.
     * This is a terminal operation that forces evaluation.
     *
     * @param function the function to extract the comparable attribute
     * @param <V> the type of the comparable attribute
     * @return a MutableSortedSet containing all unique elements
     */
    <V extends Comparable<? super V>> MutableSortedSet<T> toSortedSetBy(Function<? super T, ? extends V> function);

    /**
     * Converts this ParallelIterable to a MutableBag by processing elements in parallel.
     * This is a terminal operation that forces evaluation.
     *
     * @return a MutableBag containing all elements with their occurrence counts
     */
    MutableBag<T> toBag();

    /**
     * Converts this ParallelIterable to a MutableSortedBag using natural ordering.
     * This is a terminal operation that forces evaluation.
     *
     * @return a MutableSortedBag containing all elements with their occurrence counts
     */
    MutableSortedBag<T> toSortedBag();

    /**
     * Converts this ParallelIterable to a MutableSortedBag sorted using the specified comparator.
     * This is a terminal operation that forces evaluation.
     *
     * @param comparator the comparator to determine the order
     * @return a MutableSortedBag containing all elements with their occurrence counts
     */
    MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator);

    /**
     * Converts this ParallelIterable to a MutableSortedBag sorted by the attribute returned by the function.
     * This is a terminal operation that forces evaluation.
     *
     * @param function the function to extract the comparable attribute
     * @param <V> the type of the comparable attribute
     * @return a MutableSortedBag containing all elements with their occurrence counts
     */
    <V extends Comparable<? super V>> MutableSortedBag<T> toSortedBagBy(Function<? super T, ? extends V> function);

    /**
     * Converts this ParallelIterable to a MutableMap using the specified key and value functions.
     * This is a terminal operation that forces evaluation. Both functions must be thread-safe.
     *
     * @param keyFunction the thread-safe function to extract keys
     * @param valueFunction the thread-safe function to extract values
     * @param <NK> the type of keys
     * @param <NV> the type of values
     * @return a MutableMap containing the key-value pairs
     */
    <NK, NV> MutableMap<NK, NV> toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction);

    /**
     * Converts this ParallelIterable to a MutableSortedMap using the specified key and value functions.
     * Keys are sorted using natural ordering. This is a terminal operation that forces evaluation.
     * Both functions must be thread-safe.
     *
     * @param keyFunction the thread-safe function to extract keys
     * @param valueFunction the thread-safe function to extract values
     * @param <NK> the type of keys
     * @param <NV> the type of values
     * @return a MutableSortedMap containing the key-value pairs
     */
    <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction);

    /**
     * Converts this ParallelIterable to a MutableSortedMap using the specified comparator, key and value functions.
     * This is a terminal operation that forces evaluation. The functions must be thread-safe.
     *
     * @param comparator the comparator to determine the order of keys
     * @param keyFunction the thread-safe function to extract keys
     * @param valueFunction the thread-safe function to extract values
     * @param <NK> the type of keys
     * @param <NV> the type of values
     * @return a MutableSortedMap containing the key-value pairs
     */
    <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Comparator<? super NK> comparator, Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction);

    /**
     * Converts this ParallelIterable to an array.
     * This is a terminal operation that forces evaluation.
     *
     * @return an array containing all elements
     */
    default Object[] toArray()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toArray() not implemented yet");
    }

    /**
     * Converts this ParallelIterable to an array of the specified type.
     * This is a terminal operation that forces evaluation.
     *
     * @param target the array to use, or a new array if too small
     * @param <T1> the type of elements in the array
     * @return an array containing all elements
     */
    <T1> T1[] toArray(T1[] target);

    /**
     * Returns the minimum element using the specified comparator, processing in parallel.
     * This is a terminal operation that forces evaluation.
     *
     * @param comparator the comparator to determine order
     * @return the minimum element
     */
    T min(Comparator<? super T> comparator);

    /**
     * Returns the maximum element using the specified comparator, processing in parallel.
     * This is a terminal operation that forces evaluation.
     *
     * @param comparator the comparator to determine order
     * @return the maximum element
     */
    T max(Comparator<? super T> comparator);

    /**
     * Returns the minimum element using natural ordering, processing in parallel.
     * This is a terminal operation that forces evaluation.
     *
     * @return the minimum element
     */
    T min();

    /**
     * Returns the maximum element using natural ordering, processing in parallel.
     * This is a terminal operation that forces evaluation.
     *
     * @return the maximum element
     */
    T max();

    /**
     * Returns the element with the minimum value of the attribute returned by the function, processing in parallel.
     * This is a terminal operation that forces evaluation. The function must be thread-safe.
     *
     * @param function the thread-safe function to extract the comparable attribute
     * @param <V> the type of the comparable attribute
     * @return the element with the minimum attribute value
     */
    <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function);

    /**
     * Returns the element with the maximum value of the attribute returned by the function, processing in parallel.
     * This is a terminal operation that forces evaluation. The function must be thread-safe.
     *
     * @param function the thread-safe function to extract the comparable attribute
     * @param <V> the type of the comparable attribute
     * @return the element with the maximum attribute value
     */
    <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function);

    /**
     * Returns the final long result of evaluating function for each element of the iterable in parallel
     * and adding the results together.
     *
     * @since 6.0
     */
    long sumOfInt(IntFunction<? super T> function);

    /**
     * Returns the final double result of evaluating function for each element of the iterable in parallel
     * and adding the results together. It uses Kahan summation algorithm to reduce numerical error.
     *
     * @since 6.0
     */
    double sumOfFloat(FloatFunction<? super T> function);

    /**
     * Returns the final long result of evaluating function for each element of the iterable in parallel
     * and adding the results together.
     *
     * @since 6.0
     */
    long sumOfLong(LongFunction<? super T> function);

    /**
     * Returns the final double result of evaluating function for each element of the iterable in parallel
     * and adding the results together. It uses Kahan summation algorithm to reduce numerical error.
     *
     * @since 6.0
     */
    double sumOfDouble(DoubleFunction<? super T> function);

    default String makeString()
    {
        return this.makeString(", ");
    }

    default String makeString(String separator)
    {
        return this.makeString("", separator, "");
    }

    default String makeString(String start, String separator, String end)
    {
        Appendable stringBuilder = new StringBuilder();
        this.appendString(stringBuilder, start, separator, end);
        return stringBuilder.toString();
    }

    default String makeString(Function<? super T, Object> function, String start, String separator, String end)
    {
        return this.collect(function).makeString(start, separator, end);
    }

    default void appendString(Appendable appendable)
    {
        this.appendString(appendable, ", ");
    }

    default void appendString(Appendable appendable, String separator)
    {
        this.appendString(appendable, "", separator, "");
    }

    void appendString(Appendable appendable, String start, String separator, String end);

    /**
     * Groups elements by the value returned by the function, processing in parallel.
     * This is a terminal operation that forces evaluation. The function must be thread-safe.
     *
     * @param function the thread-safe function to extract grouping keys
     * @param <V> the type of the grouping key
     * @return a Multimap where keys are grouping values and values are elements
     */
    <V> Multimap<V, T> groupBy(Function<? super T, ? extends V> function);

    /**
     * Groups elements by multiple values returned by the function, processing in parallel.
     * Each element can be associated with multiple groups. The function must be thread-safe.
     *
     * @param function the thread-safe function to extract grouping keys
     * @param <V> the type of the grouping key
     * @return a Multimap where keys are grouping values and values are elements
     */
    <V> Multimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Groups elements by the value returned by the function, ensuring each key is unique.
     * This is a terminal operation that forces evaluation. The function must be thread-safe.
     *
     * @param function the thread-safe function to extract unique keys
     * @param <V> the type of the unique key
     * @return a MapIterable where keys are unique grouping values and values are elements
     * @throws IllegalStateException if duplicate keys are found
     */
    <V> MapIterable<V, T> groupByUniqueKey(Function<? super T, ? extends V> function);

    /**
     * Aggregates elements by group using a mutating aggregator, processing in parallel.
     * The aggregator mutates the aggregate value in place. All functions and procedures must be thread-safe,
     * and the aggregator must handle concurrent mutations safely.
     *
     * @param groupBy the thread-safe function to extract grouping keys
     * @param zeroValueFactory the thread-safe factory to create initial aggregate values
     * @param mutatingAggregator the thread-safe procedure to mutate aggregate values
     * @param <K> the type of the grouping key
     * @param <V> the type of the aggregate value
     * @return a MapIterable containing aggregated results by group
     */
    <K, V> MapIterable<K, V> aggregateInPlaceBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Procedure2<? super V, ? super T> mutatingAggregator);

    /**
     * Aggregates elements by group using a non-mutating aggregator, processing in parallel.
     * The aggregator returns a new aggregate value rather than mutating the existing one.
     * All functions must be thread-safe.
     *
     * @param groupBy the thread-safe function to extract grouping keys
     * @param zeroValueFactory the thread-safe factory to create initial aggregate values
     * @param nonMutatingAggregator the thread-safe function to produce new aggregate values
     * @param <K> the type of the grouping key
     * @param <V> the type of the aggregate value
     * @return a MapIterable containing aggregated results by group
     */
    <K, V> MapIterable<K, V> aggregateBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Function2<? super V, ? super T, ? extends V> nonMutatingAggregator);
}
