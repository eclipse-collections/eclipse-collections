/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.set.sorted;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.annotation.Beta;
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
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.FloatList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.ShortList;
import org.eclipse.collections.api.multimap.sortedset.SortedSetMultimap;
import org.eclipse.collections.api.ordered.ReversibleIterable;
import org.eclipse.collections.api.ordered.SortedIterable;
import org.eclipse.collections.api.partition.set.sorted.PartitionSortedSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * An iterable whose items are unique and sorted by some comparator or their natural ordering.
 */
public interface SortedSetIterable<T>
        extends SetIterable<T>, Comparable<SortedSetIterable<T>>, SortedIterable<T>, ReversibleIterable<T>
{
    /**
     * Returns the comparator used to order the elements in this set, or null if this set uses the natural ordering of
     * its elements.
     */
    @Override
    Comparator<? super T> comparator();

    /**
     * Returns the set of all objects that are a member of {@code this} or {@code set} or both. The union of [1, 2, 3]
     * and [2, 3, 4] is the set [1, 2, 3, 4]. If equal elements appear in both sets, then the output will contain the
     * copy from {@code this}.
     */
    @Override
    SortedSetIterable<T> union(SetIterable<? extends T> set);

    /**
     * Returns the set of all objects that are members of both {@code this} and {@code set}. The intersection of
     * [1, 2, 3] and [2, 3, 4] is the set [2, 3]. The output will contain instances from the smaller of the two sets
     * ({@code this} or {@code set}) based on the size. If the sizes are equal, the output will contain instances from
     * {@code this}.
     * <p>Note: If you need to ensure that the memory references from {@code this} are preserved in the resulting set,
     * consider using {@code this.select(that::contains)} instead.</p>
     */
    @Override
    SortedSetIterable<T> intersect(SetIterable<? extends T> set);

    /**
     * Returns the set of all members of {@code this} that are not members of {@code subtrahendSet}. The difference of
     * [1, 2, 3] and [2, 3, 4] is [1].
     */
    @Override
    SortedSetIterable<T> difference(SetIterable<? extends T> subtrahendSet);

    /**
     * Returns the set of all objects that are a member of exactly one of {@code this} and {@code setB} (elements which
     * are in one of the sets, but not in both). For instance, for the sets [1, 2, 3] and [2, 3, 4], the symmetric
     * difference set is [1, 4] . It is the set difference of the union and the intersection.
     */
    @Override
    SortedSetIterable<T> symmetricDifference(SetIterable<? extends T> setB);

    /**
     * Returns the set whose members are all possible subsets of {@code this}. For example, the powerset of [1, 2] is
     * [[], [1], [2], [1, 2]].
     */
    SortedSetIterable<SortedSetIterable<T>> powerSet();

    @Override
    SortedSetIterable<T> tap(Procedure<? super T> procedure);

    @Override
    SortedSetIterable<T> select(Predicate<? super T> predicate);

    @Override
    <P> SortedSetIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    SortedSetIterable<T> reject(Predicate<? super T> predicate);

    @Override
    <P> SortedSetIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionSortedSet<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionSortedSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionSortedSet<T> partitionWhile(Predicate<? super T> predicate);

    @Override
    <S> SortedSetIterable<S> selectInstancesOf(Class<S> clazz);

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
    SortedSetIterable<T> takeWhile(Predicate<? super T> predicate);

    @Override
    SortedSetIterable<T> dropWhile(Predicate<? super T> predicate);

    @Override
    <V> SortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> SortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    SortedSetIterable<Pair<T, Integer>> zipWithIndex();

    @Override
    SortedSetIterable<T> toReversed();

    @Override
    SortedSetIterable<T> take(int count);

    @Override
    SortedSetIterable<T> drop(int count);

    /**
     * Converts the SortedSetIterable to an immutable implementation. Returns this for immutable sets.
     *
     * @since 5.0
     */
    @Override
    ImmutableSortedSet<T> toImmutable();

    /**
     * Returns a parallel iterable of this SortedSetIterable.
     *
     * @since 6.0
     */
    @Override
    @Beta
    ParallelSortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize);

    /**
     * Returns the greatest element in this set strictly less than the given element, or {@code null} if there is no
     * such element.
     */
    T lower(T e);

    /**
     * Returns the greatest element in this set less than or equal to the given element, or {@code null} if there is no
     * such element.
     */
    T floor(T e);

    /**
     * Returns the least element in this set greater than or equal to the given element, or {@code null} if there is no
     * such element.
     */
    T ceiling(T e);

    /**
     * Returns the least element in this set strictly greater than the given element, or {@code null} if there is no
     * such element.
     */
    T higher(T e);

    /**
     * Returns an iterator over the elements in this set in descending order.
     */
    Iterator<T> descendingIterator();

    /**
     * Returns a reverse-order view of the elements contained in this set.
     */
    SortedSetIterable<T> descendingSet();

    SortedSetIterable<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive);

    SortedSetIterable<T> headSet(T toElement, boolean inclusive);

    SortedSetIterable<T> tailSet(T fromElement, boolean inclusive);

    @Override
    default void reverseForEach(Procedure<? super T> procedure)
    {
        this.descendingSet().forEach(procedure);
    }

    @Override
    default void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        int size = this.size();
        this.descendingSet().forEachWithIndex((each, index) -> procedure.value(each, size - 1 - index));
    }

    @Override
    default int detectLastIndex(Predicate<? super T> predicate)
    {
        int size = this.size();
        int index = this.descendingSet().detectIndex(predicate);
        return index >= 0 ? size - 1 - index : -1;
    }
}
