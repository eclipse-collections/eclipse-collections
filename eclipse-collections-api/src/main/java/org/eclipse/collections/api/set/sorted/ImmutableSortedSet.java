/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.set.sorted;

import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

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
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import org.eclipse.collections.api.partition.set.sorted.PartitionImmutableSortedSet;
import org.eclipse.collections.api.set.ImmutableSetIterable;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * ImmutableSortedSet is the non-modifiable equivalent interface to {@link MutableSortedSet}. {@link
 * MutableSortedSet#toImmutable()} will give you an appropriately trimmed implementation of ImmutableSortedSet. All
 * ImmutableSortedSet implementations must implement the {@link SortedSet} interface in order to satisfy the
 * {@link Set#equals(Object)} contract and be compared against other Sets.
 */
public interface ImmutableSortedSet<T>
        extends SortedSetIterable<T>, ImmutableSetIterable<T>
{
    @Override
    ImmutableSortedSet<T> newWith(T element);

    @Override
    ImmutableSortedSet<T> newWithout(T element);

    @Override
    ImmutableSortedSet<T> newWithAll(Iterable<? extends T> elements);

    @Override
    ImmutableSortedSet<T> newWithoutAll(Iterable<? extends T> elements);

    @Override
    ImmutableSortedSet<T> tap(Procedure<? super T> procedure);

    @Override
    ImmutableSortedSet<T> select(Predicate<? super T> predicate);

    @Override
    <P> ImmutableSortedSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    ImmutableSortedSet<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ImmutableSortedSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableSortedSet<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionImmutableSortedSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableSortedSet<T> partitionWhile(Predicate<? super T> predicate);

    @Override
    <S> ImmutableSortedSet<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> ImmutableList<V> collect(Function<? super T, ? extends V> function);

    /**
     * @since 9.1.
     */
    @Override
    default <V> ImmutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

    @Override
    ImmutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    ImmutableByteList collectByte(ByteFunction<? super T> byteFunction);

    @Override
    ImmutableCharList collectChar(CharFunction<? super T> charFunction);

    @Override
    ImmutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    ImmutableFloatList collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    ImmutableIntList collectInt(IntFunction<? super T> intFunction);

    @Override
    ImmutableLongList collectLong(LongFunction<? super T> longFunction);

    @Override
    ImmutableShortList collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <P, V> ImmutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> ImmutableList<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> ImmutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> ImmutableList<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    ImmutableSortedSet<T> distinct();

    @Override
    ImmutableSortedSet<T> takeWhile(Predicate<? super T> predicate);

    @Override
    ImmutableSortedSet<T> dropWhile(Predicate<? super T> predicate);

    @Override
    <V> ImmutableSortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> ImmutableSortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    <S> ImmutableList<Pair<T, S>> zip(Iterable<S> that);

    @Override
    ImmutableSortedSet<Pair<T, Integer>> zipWithIndex();

    @Override
    ImmutableSortedSet<T> toReversed();

    @Override
    ImmutableSortedSet<T> take(int count);

    @Override
    ImmutableSortedSet<T> drop(int count);

    SortedSet<T> castToSortedSet();

    NavigableSet<T> castToNavigableSet();

    @Override
    ImmutableSortedSet<T> descendingSet();

    @Override
    ImmutableSortedSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive);

    @Override
    ImmutableSortedSet<T> headSet(T toElement, boolean inclusive);

    @Override
    ImmutableSortedSet<T> tailSet(T fromElement, boolean inclusive);

    @Override
    ImmutableSortedSet<T> union(SetIterable<? extends T> set);

    @Override
    ImmutableSortedSet<T> intersect(SetIterable<? extends T> set);

    @Override
    ImmutableSortedSet<T> difference(SetIterable<? extends T> subtrahendSet);

    @Override
    ImmutableSortedSet<T> symmetricDifference(SetIterable<? extends T> setB);

    @Override
    ImmutableSortedSet<SortedSetIterable<T>> powerSet();

    /**
     * @since 11.0
     */
    @Override
    default ImmutableSortedSet<T> toImmutableSortedSet()
    {
        return this;
    }
}
