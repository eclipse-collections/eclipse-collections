/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag;

import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.multimap.bag.UnsortedBagMultimap;
import org.eclipse.collections.api.set.ParallelUnsortedSetIterable;

/**
 * ParallelUnsortedBag provides parallel iteration capabilities for unsorted Bag data structures.
 * Operations are executed in parallel with no guarantees about iteration order.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ParallelUnsortedBag<String> parallelBag = bag.asParallel(executor, batchSize);
 * ParallelUnsortedBag<String> filtered = parallelBag.select(s -> s.length() > 2);
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 * @since 5.0
 */
@Beta
public interface ParallelUnsortedBag<T> extends ParallelBag<T>
{
    @Override
    ParallelUnsortedSetIterable<T> asUnique();

    /**
     * Creates a parallel iterable for selecting elements from the current iterable.
     */
    @Override
    ParallelUnsortedBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> ParallelUnsortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a parallel iterable for rejecting elements from the current iterable.
     */
    @Override
    ParallelUnsortedBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ParallelUnsortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> ParallelUnsortedBag<S> selectInstancesOf(Class<S> clazz);

    /**
     * Creates a parallel iterable for collecting elements from the current iterable.
     */
    @Override
    <V> ParallelUnsortedBag<V> collect(Function<? super T, ? extends V> function);

    @Override
    <P, V> ParallelUnsortedBag<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    /**
     * Creates a parallel iterable for selecting and collecting elements from the current iterable.
     */
    @Override
    <V> ParallelUnsortedBag<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    /**
     * Creates a parallel flattening iterable for the current iterable.
     */
    @Override
    <V> ParallelUnsortedBag<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    @Override
    <V> UnsortedBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> UnsortedBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);
}
