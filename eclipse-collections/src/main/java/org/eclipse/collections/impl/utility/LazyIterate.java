/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.lazy.ChunkIterable;
import org.eclipse.collections.impl.lazy.CollectIterable;
import org.eclipse.collections.impl.lazy.CompositeIterable;
import org.eclipse.collections.impl.lazy.DistinctIterable;
import org.eclipse.collections.impl.lazy.DropIterable;
import org.eclipse.collections.impl.lazy.DropWhileIterable;
import org.eclipse.collections.impl.lazy.FlatCollectIterable;
import org.eclipse.collections.impl.lazy.LazyIterableAdapter;
import org.eclipse.collections.impl.lazy.RejectIterable;
import org.eclipse.collections.impl.lazy.SelectInstancesOfIterable;
import org.eclipse.collections.impl.lazy.SelectIterable;
import org.eclipse.collections.impl.lazy.TakeIterable;
import org.eclipse.collections.impl.lazy.TakeWhileIterable;
import org.eclipse.collections.impl.lazy.TapIterable;
import org.eclipse.collections.impl.lazy.ZipIterable;
import org.eclipse.collections.impl.lazy.ZipWithIndexIterable;
import org.eclipse.collections.impl.tuple.Tuples;

/**
 * LazyIterate is a factory class that creates lazy (deferred) iterables for memory-efficient operations.
 * Operations like select, collect, and flatCollect are not executed immediately but are deferred until
 * the resulting iterable is actually iterated over.
 * <p>
 * Lazy evaluation provides significant benefits:
 * </p>
 * <ul>
 * <li><b>Memory Efficiency:</b> No intermediate collections are created</li>
 * <li><b>Performance:</b> Operations can short-circuit (e.g., finding first match)</li>
 * <li><b>Composability:</b> Chain multiple operations without multiple passes over data</li>
 * <li><b>Infinite Sequences:</b> Work with potentially infinite iterables</li>
 * </ul>
 * <p>
 * All operations return LazyIterable instances that compute values on-demand during iteration.
 * To materialize results into a collection, call toList(), toSet(), or similar terminal operations.
 * </p>
 * <p><b>Thread Safety:</b> Lazy iterables are not thread-safe. Each iteration creates a new iterator.</p>
 * <p><b>Performance:</b> Excellent for chained operations and large datasets. No memory overhead
 * for intermediate collections. Operations are performed in a single pass during iteration.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
 *
 * // Create a lazy iterable (no computation yet)
 * LazyIterable<Integer> lazy = LazyIterate.select(numbers, n -> n % 2 == 0)
 *                                         .collect(n -> n * n);
 *
 * // Computation happens only during iteration
 * MutableList<Integer> result = lazy.toList();
 * // Result: [4, 16, 36, 64, 100]
 *
 * // Chain multiple operations efficiently
 * LazyIterable<String> transformed =
 *     LazyIterate.select(numbers, n -> n > 3)
 *                .collect(Object::toString)
 *                .select(s -> s.length() == 1);
 *
 * // Take first 3 elements (short-circuits, doesn't process all)
 * LazyIterable<Integer> firstThree = LazyIterate.adapt(numbers).take(3);
 *
 * // Flatten nested iterables
 * List<List<Integer>> nested = Arrays.asList(
 *     Arrays.asList(1, 2),
 *     Arrays.asList(3, 4)
 * );
 * LazyIterable<Integer> flattened = LazyIterate.flatCollect(nested, list -> list);
 * }</pre>
 *
 * @since 1.0
 */
public final class LazyIterate
{
    private static final LazyIterable<?> EMPTY_ITERABLE = Lists.immutable.empty().asLazy();

    private LazyIterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Creates a deferred rich iterable for the specified iterable.
     */
    public static <T> LazyIterable<T> adapt(Iterable<T> iterable)
    {
        return new LazyIterableAdapter<>(iterable);
    }

    /**
     * Creates a deferred filtering iterable for the specified iterable.
     */
    public static <T> LazyIterable<T> select(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return new SelectIterable<>(iterable, predicate);
    }

    /**
     * Creates a deferred negative filtering iterable for the specified iterable.
     */
    public static <T> LazyIterable<T> reject(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return new RejectIterable<>(iterable, predicate);
    }

    public static <T> LazyIterable<T> selectInstancesOf(Iterable<?> iterable, Class<T> clazz)
    {
        return new SelectInstancesOfIterable<>(iterable, clazz);
    }

    /**
     * Creates a deferred transforming iterable for the specified iterable.
     */
    public static <T, V> LazyIterable<V> collect(
            Iterable<T> iterable,
            Function<? super T, ? extends V> function)
    {
        return new CollectIterable<>(iterable, function);
    }

    /**
     * Creates a deferred flattening iterable for the specified iterable.
     */
    public static <T, V> LazyIterable<V> flatCollect(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function)
    {
        return new FlatCollectIterable<>(iterable, function);
    }

    /**
     * Creates a deferred filtering and transforming iterable for the specified iterable.
     */
    public static <T, V> LazyIterable<V> collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return LazyIterate.select(iterable, predicate).collect(function);
    }

    /**
     * Creates a deferred take iterable for the specified iterable using the specified count as the limit.
     */
    public static <T> LazyIterable<T> take(Iterable<T> iterable, int count)
    {
        return new TakeIterable<>(iterable, count);
    }

    /**
     * Creates a deferred drop iterable for the specified iterable using the specified count as the size to drop.
     */
    public static <T> LazyIterable<T> drop(Iterable<T> iterable, int count)
    {
        return new DropIterable<>(iterable, count);
    }

    /**
     * Creates a deferred takeWhile iterable for the specified iterable using the specified predicate.
     * Short circuits at the first element which does not satisfy the Predicate.
     *
     * @since 8.0
     */
    public static <T> LazyIterable<T> takeWhile(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return new TakeWhileIterable<>(iterable, predicate);
    }

    /**
     * Creates a deferred dropWhile iterable for the specified iterable using the specified count as the size to drop.
     * Short circuits at the first element which satisfies the Predicate.
     *
     * @since 8.0
     */
    public static <T> LazyIterable<T> dropWhile(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return new DropWhileIterable<>(iterable, predicate);
    }

    /**
     * Creates a deferred distinct iterable for the specified iterable.
     *
     * @since 5.0
     */
    public static <T> LazyIterable<T> distinct(Iterable<T> iterable)
    {
        return new DistinctIterable<>(iterable);
    }

    /**
     * Combines iterables into a deferred composite iterable.
     */
    public static <T> LazyIterable<T> concatenate(Iterable<T>... iterables)
    {
        return CompositeIterable.with(iterables);
    }

    public static <T> LazyIterable<T> empty()
    {
        return (LazyIterable<T>) EMPTY_ITERABLE;
    }

    public static <A, B> LazyIterable<Pair<A, B>> zip(Iterable<A> as, Iterable<B> bs)
    {
        return new ZipIterable<>(as, bs);
    }

    public static <T> LazyIterable<Pair<T, Integer>> zipWithIndex(Iterable<T> iterable)
    {
        return new ZipWithIndexIterable<>(iterable);
    }

    public static <T> LazyIterable<RichIterable<T>> chunk(Iterable<T> iterable, int size)
    {
        return new ChunkIterable<>(iterable, size);
    }

    /**
     * Creates a deferred tap iterable for the specified iterable.
     *
     * @since 6.0
     */
    public static <T> LazyIterable<T> tap(Iterable<T> iterable, Procedure<? super T> procedure)
    {
        return new TapIterable<>(iterable, procedure);
    }

    /**
     * Create a deferred cartesian product of the two specified iterables.
     *
     * See {@link LazyIterate#cartesianProduct(Iterable, Iterable, Function2)} about performance and presence of duplicates.
     *
     * @since 10.0
     */
    public static <A, B> LazyIterable<Pair<A, B>> cartesianProduct(Iterable<A> iterable1, Iterable<B> iterable2)
    {
        return LazyIterate.cartesianProduct(iterable1, iterable2, Tuples::pair);
    }

    /**
     * Create a deferred cartesian product of the two specified iterables.
     *
     * This operation has O(n^2) performance.
     *
     * The presence of duplicates in the resulting iterable is both dependent on the
     * presence of duplicates in the two specified iterables, and on the behaviour
     * of the terminating operation that is applied to the resulting lazy iterable.
     *
     * @since 10.0
     */
    public static <A, B, C> LazyIterable<C> cartesianProduct(Iterable<A> iterable1, Iterable<B> iterable2, Function2<? super A, ? super B, ? extends C> function)
    {
        return LazyIterate.flatCollect(iterable1, first -> LazyIterate.collect(iterable2, second -> function.value(first, second)));
    }
}
