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

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.ImmutableBagIterable;
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
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.sortedbag.ImmutableSortedBagMultimap;
import org.eclipse.collections.api.partition.bag.sorted.PartitionImmutableSortedBag;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * ImmutableSortedBag is the non-modifiable equivalent interface to {@link MutableSortedBag}.
 * Elements are maintained in sorted order with occurrence counts tracked for each element.
 * All operations that would modify the bag return new immutable instances instead.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableSortedBag<String> bag = SortedBags.immutable.of("C", "A", "B", "A", "C");
 * // Iteration order is A, A, B, C, C (sorted)
 * ImmutableSortedBag<String> updated = bag.newWith("D");
 * // Original bag is unchanged
 * int countOfC = bag.occurrencesOf("C"); // Returns 2
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 * @since 4.2
 */
public interface ImmutableSortedBag<T>
        extends ImmutableBagIterable<T>, SortedBag<T>
{
    @Override
    ImmutableSortedBag<T> newWith(T element);

    @Override
    ImmutableSortedBag<T> newWithout(T element);

    @Override
    ImmutableSortedBag<T> newWithAll(Iterable<? extends T> elements);

    @Override
    ImmutableSortedBag<T> newWithoutAll(Iterable<? extends T> elements);

    @Override
    ImmutableSortedBag<T> selectByOccurrences(IntPredicate predicate);

    /**
     * @since 9.2
     */
    @Override
    default ImmutableSortedBag<T> selectDuplicates()
    {
        return this.selectByOccurrences(occurrences -> occurrences > 1);
    }

    /**
     * @since 9.2
     */
    @Override
    default ImmutableSortedSet<T> selectUnique()
    {
        MutableSortedSet<T> result = SortedSets.mutable.with(this.comparator());
        this.forEachWithOccurrences((each, occurrences) ->
        {
            if (occurrences == 1)
            {
                result.add(each);
            }
        });
        return result.toImmutable();
    }

    @Override
    ImmutableSortedBag<T> tap(Procedure<? super T> procedure);

    @Override
    ImmutableSortedBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> ImmutableSortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    ImmutableSortedBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ImmutableSortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableSortedBag<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionImmutableSortedBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> ImmutableSortedBag<S> selectInstancesOf(Class<S> clazz);

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
    <V> ImmutableList<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function);

    @Override
    <V> ImmutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> ImmutableList<V> flatCollectWith(
            Function2<? super T, ? super P, ? extends Iterable<V>> function,
            P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    ImmutableSortedSet<T> distinct();

    @Override
    ImmutableSortedBag<T> takeWhile(Predicate<? super T> predicate);

    @Override
    ImmutableSortedBag<T> dropWhile(Predicate<? super T> predicate);

    /**
     * @since 9.0
     */
    @Override
    default <V> ImmutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.asLazy().<V>collect(function).toBag().toImmutable();
    }

    /**
     * @since 9.0
     */
    @Override
    default <V, P> ImmutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.asLazy().<P, V>collectWith(function, parameter).toBag().toImmutable();
    }

    /**
     * @since 10.0.0
     */
    @Override
    default <V> ImmutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.asLazy().flatCollect(function).toBag().toImmutable();
    }

    @Override
    <V> ImmutableSortedBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> ImmutableSortedBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    <S> ImmutableList<Pair<T, S>> zip(Iterable<S> that);

    @Override
    ImmutableSortedSet<Pair<T, Integer>> zipWithIndex();

    @Override
    MutableSortedMap<T, Integer> toMapOfItemToCount();

    @Override
    ImmutableSortedBag<T> toReversed();

    @Override
    ImmutableSortedBag<T> take(int count);

    @Override
    ImmutableSortedBag<T> drop(int count);

    /**
     * @since 11.0
     */
    @Override
    default ImmutableSortedBag<T> toImmutableSortedBag()
    {
        return this;
    }
}
