/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag.sorted;

import java.util.Comparator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.bag.Bag;
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
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.FloatList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.ShortList;
import org.eclipse.collections.api.map.sorted.SortedMapIterable;
import org.eclipse.collections.api.multimap.sortedbag.SortedBagMultimap;
import org.eclipse.collections.api.ordered.ReversibleIterable;
import org.eclipse.collections.api.ordered.SortedIterable;
import org.eclipse.collections.api.partition.bag.sorted.PartitionSortedBag;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * An Iterable whose elements are sorted by some comparator or their natural ordering and may contain duplicate entries.
 * SortedBag maintains elements in sorted order while tracking occurrence counts for each element.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * SortedBag<String> bag = SortedBags.mutable.of("C", "A", "B", "A", "C", "C");
 * // Iteration order is A, A, B, C, C, C (sorted)
 * int countOfC = bag.occurrencesOf("C"); // Returns 3
 * SortedBag<String> reversed = bag.toReversed();
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 * @since 4.2
 */
public interface SortedBag<T>
        extends Bag<T>, Comparable<SortedBag<T>>, SortedIterable<T>, ReversibleIterable<T>
{
    @Override
    SortedBag<T> selectByOccurrences(IntPredicate predicate);

    /**
     * @since 9.2
     */
    @Override
    default SortedBag<T> selectDuplicates()
    {
        return this.selectByOccurrences(occurrences -> occurrences > 1);
    }

    /**
     * @since 9.2
     */
    @Override
    SortedSetIterable<T> selectUnique();

    @Override
    SortedMapIterable<T, Integer> toMapOfItemToCount();

    /**
     * Convert the SortedBag to an ImmutableSortedBag. If the bag is immutable, it returns itself.
     * Not yet supported.
     */
    @Override
    ImmutableSortedBag<T> toImmutable();

    /**
     * Returns the minimum element out of this container based on the natural order, not the order of this bag.
     * If you want the minimum element based on the order of this bag, use {@link #getFirst()}.
     *
     * @throws ClassCastException     if the elements are not {@link Comparable}
     * @throws NoSuchElementException if the SortedBag is empty
     * @since 1.0
     */
    @Override
    T min();

    /**
     * Returns the maximum element out of this container based on the natural order, not the order of this bag.
     * If you want the maximum element based on the order of this bag, use {@link #getLast()}.
     *
     * @throws ClassCastException     if the elements are not {@link Comparable}
     * @throws NoSuchElementException if the SortedBag is empty
     * @since 1.0
     */
    @Override
    T max();

    @Override
    SortedBag<T> tap(Procedure<? super T> procedure);

    @Override
    SortedBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> SortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    SortedBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> SortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionSortedBag<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionSortedBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionSortedBag<T> partitionWhile(Predicate<? super T> predicate);

    @Override
    <S> SortedBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> ListIterable<V> collect(Function<? super T, ? extends V> function);

    /**
     * @since 9.1.
     */
    @Override
    default <V> ListIterable<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

    @Override
    BooleanList collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    ByteList collectByte(ByteFunction<? super T> byteFunction);

    @Override
    CharList collectChar(CharFunction<? super T> charFunction);

    @Override
    DoubleList collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    FloatList collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    IntList collectInt(IntFunction<? super T> intFunction);

    @Override
    LongList collectLong(LongFunction<? super T> longFunction);

    @Override
    ShortList collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <P, V> ListIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> ListIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> ListIterable<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function);

    @Override
    <V> ListIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> ListIterable<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    SortedSetIterable<T> distinct();

    @Override
    SortedBag<T> takeWhile(Predicate<? super T> predicate);

    @Override
    SortedBag<T> dropWhile(Predicate<? super T> predicate);

    @Override
    <V> SortedBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> SortedBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Returns the comparator used to order the elements in this bag, or null if this bag uses the natural ordering of
     * its elements.
     */
    @Override
    Comparator<? super T> comparator();

    @Override
    SortedSetIterable<Pair<T, Integer>> zipWithIndex();

    @Override
    SortedBag<T> toReversed();

    @Override
    SortedBag<T> take(int count);

    @Override
    SortedBag<T> drop(int count);
}
