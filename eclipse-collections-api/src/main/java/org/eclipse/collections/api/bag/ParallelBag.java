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

import org.eclipse.collections.api.ParallelIterable;
import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.multimap.bag.BagMultimap;

/**
 * ParallelBag provides parallel iteration capabilities for Bag data structures. Operations on
 * ParallelBag are executed in parallel across multiple threads for improved performance on
 * large data sets. Element occurrences are maintained during parallel operations.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ParallelBag<String> parallelBag = bag.asParallel(executor, batchSize);
 * parallelBag.forEachWithOccurrences((element, count) ->
 *     System.out.println(element + ": " + count));
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 * @since 5.0
 */
@Beta
public interface ParallelBag<T> extends ParallelIterable<T>
{
    void forEachWithOccurrences(ObjectIntProcedure<? super T> procedure);

    /**
     * Creates a parallel iterable for selecting elements from the current iterable.
     */
    @Override
    ParallelBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> ParallelBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a parallel iterable for rejecting elements from the current iterable.
     */
    @Override
    ParallelBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ParallelBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> ParallelBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> BagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> BagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);
}
