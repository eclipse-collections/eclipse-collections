/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.list;

import org.eclipse.collections.api.ParallelIterable;
import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.multimap.list.ListMultimap;
import org.eclipse.collections.api.set.ParallelUnsortedSetIterable;

/**
 * A ParallelListIterable is a ParallelIterable specifically for lists that defers evaluation for certain methods
 * like select, reject, collect, etc. Any methods that do not return a ParallelIterable when called will cause
 * evaluation to be forced. Evaluation occurs in parallel across multiple threads. All code blocks passed in must
 * be stateless or thread-safe to ensure correct parallel execution.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ParallelListIterable<Person> people = list.asParallel(executor, batchSize);
 *
 * // Lazy parallel operations - deferred evaluation
 * ParallelListIterable<Person> adults = people.select(p -> p.getAge() >= 18);
 * ParallelListIterable<String> names = adults.collect(Person::getName);
 *
 * // Terminal operation - forces parallel evaluation
 * MutableList<String> result = names.toList();
 * }</pre>
 *
 * @since 5.0
 */
@Beta
public interface ParallelListIterable<T>
        extends ParallelIterable<T>
{
    /**
     * Returns a parallel iterable containing only the unique elements from this list.
     *
     * @return a ParallelUnsortedSetIterable with unique elements
     */
    @Override
    ParallelUnsortedSetIterable<T> asUnique();

    /**
     * Creates a parallel iterable for selecting elements from the current iterable that satisfy the predicate.
     * Evaluation is deferred until a terminal operation is called.
     *
     * @param predicate the predicate to filter elements
     * @return a ParallelListIterable for lazy parallel filtering
     */
    @Override
    ParallelListIterable<T> select(Predicate<? super T> predicate);

    /**
     * Creates a parallel iterable for selecting elements that satisfy the predicate with a parameter.
     * Evaluation is deferred until a terminal operation is called.
     *
     * @param predicate the predicate to filter elements with the parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return a ParallelListIterable for lazy parallel filtering
     */
    @Override
    <P> ParallelListIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a parallel iterable for rejecting elements from the current iterable that satisfy the predicate.
     * Evaluation is deferred until a terminal operation is called.
     *
     * @param predicate the predicate to filter elements
     * @return a ParallelListIterable for lazy parallel rejection
     */
    @Override
    ParallelListIterable<T> reject(Predicate<? super T> predicate);

    /**
     * Creates a parallel iterable for rejecting elements that satisfy the predicate with a parameter.
     * Evaluation is deferred until a terminal operation is called.
     *
     * @param predicate the predicate to filter elements with the parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return a ParallelListIterable for lazy parallel rejection
     */
    @Override
    <P> ParallelListIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a parallel iterable for selecting elements that are instances of the specified class.
     * Evaluation is deferred until a terminal operation is called.
     *
     * @param clazz the class to filter by
     * @param <S> the type to filter to
     * @return a ParallelListIterable for lazy parallel type filtering
     */
    @Override
    <S> ParallelListIterable<S> selectInstancesOf(Class<S> clazz);

    /**
     * Creates a parallel iterable for collecting (transforming) elements from the current iterable.
     * Evaluation is deferred until a terminal operation is called.
     *
     * @param function the function to transform elements
     * @param <V> the type of transformed elements
     * @return a ParallelListIterable for lazy parallel transformation
     */
    @Override
    <V> ParallelListIterable<V> collect(Function<? super T, ? extends V> function);

    /**
     * Creates a parallel iterable for collecting elements using a function with a parameter.
     * Evaluation is deferred until a terminal operation is called.
     *
     * @param function the function to transform elements with the parameter
     * @param parameter the parameter to pass to the function
     * @param <P> the type of the parameter
     * @param <V> the type of transformed elements
     * @return a ParallelListIterable for lazy parallel transformation
     */
    @Override
    <P, V> ParallelListIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    /**
     * Creates a parallel iterable for selecting and collecting elements from the current iterable.
     * Combines filtering and transformation in a single parallel operation.
     * Evaluation is deferred until a terminal operation is called.
     *
     * @param predicate the predicate to filter elements
     * @param function the function to transform selected elements
     * @param <V> the type of transformed elements
     * @return a ParallelListIterable for lazy parallel filter and transform
     */
    @Override
    <V> ParallelListIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    /**
     * Creates a parallel flattening iterable for the current iterable. Each element is transformed
     * to an Iterable and all resulting iterables are flattened into a single parallel iterable.
     * Evaluation is deferred until a terminal operation is called.
     *
     * @param function the function to transform elements to iterables
     * @param <V> the type of elements in the flattened result
     * @return a ParallelListIterable for lazy parallel flat mapping
     */
    @Override
    <V> ParallelListIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Groups elements in parallel by the result of applying the given function to each element.
     * This is a terminal operation that forces parallel evaluation.
     *
     * @param function the function to group elements
     * @param <V> the type of the group keys
     * @return a ListMultimap with group keys and their associated elements
     */
    @Override
    <V> ListMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    /**
     * Groups elements in parallel by the results of applying the given function, where each element
     * can belong to multiple groups. This is a terminal operation that forces parallel evaluation.
     *
     * @param function the function to group elements, returning multiple keys per element
     * @param <V> the type of the group keys
     * @return a ListMultimap with group keys and their associated elements
     */
    @Override
    <V> ListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);
}
