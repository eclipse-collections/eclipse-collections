/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag.sorted;

import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.bag.ParallelBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.list.ParallelListIterable;
import org.eclipse.collections.api.multimap.sortedbag.SortedBagMultimap;

/**
 * ParallelSortedBag provides parallel iteration capabilities for SortedBag data structures.
 * Operations are executed in parallel while maintaining sorted order and element occurrence counts.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ParallelSortedBag<String> parallelBag = bag.asParallel(executor, batchSize);
 * ParallelSortedBag<String> filtered = parallelBag.select(s -> s.length() > 2);
 * // Result maintains sorted order
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 * @since 5.0
 */
@Beta
public interface ParallelSortedBag<T> extends ParallelBag<T>
{
    /**
     * Creates a parallel iterable for selecting elements from the current iterable.
     */
    @Override
    ParallelSortedBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> ParallelSortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a parallel iterable for rejecting elements from the current iterable.
     */
    @Override
    ParallelSortedBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ParallelSortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> ParallelSortedBag<S> selectInstancesOf(Class<S> clazz);

    /**
     * Creates a parallel iterable for collecting elements from the current iterable.
     */
    @Override
    <V> ParallelListIterable<V> collect(Function<? super T, ? extends V> function);

    @Override
    <P, V> ParallelListIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    /**
     * Creates a parallel iterable for selecting and collecting elements from the current iterable.
     */
    @Override
    <V> ParallelListIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    /**
     * Creates a parallel flattening iterable for the current iterable.
     */
    @Override
    <V> ParallelListIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    @Override
    <V> SortedBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> SortedBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);
}
