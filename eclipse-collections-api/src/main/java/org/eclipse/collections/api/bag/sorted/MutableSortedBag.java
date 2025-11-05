/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag.sorted;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.MutableBagIterable;
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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.sortedbag.MutableSortedBagMultimap;
import org.eclipse.collections.api.partition.bag.sorted.PartitionMutableSortedBag;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * MutableSortedBag is a mutable bag that maintains its elements in sorted order while tracking
 * occurrence counts. All mutating operations modify the bag in place while preserving sort order.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MutableSortedBag<String> bag = SortedBags.mutable.empty();
 * bag.add("C");
 * bag.add("A");
 * bag.addOccurrences("C", 2); // "C" now has 3 occurrences
 * // Iteration order is A, C, C, C (sorted)
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 * @since 4.2
 */
public interface MutableSortedBag<T>
        extends SortedBag<T>, MutableBagIterable<T>, Cloneable
{
    @Override
    MutableSortedBag<T> selectByOccurrences(IntPredicate predicate);

    /**
     * @since 9.2
     */
    @Override
    default MutableSortedBag<T> selectDuplicates()
    {
        return this.selectByOccurrences(occurrences -> occurrences > 1);
    }

    /**
     * @since 9.2
     */
    @Override
    default MutableSortedSet<T> selectUnique()
    {
        MutableSortedSet<T> result = SortedSets.mutable.with(this.comparator());
        this.forEachWithOccurrences((each, occurrences) ->
        {
            if (occurrences == 1)
            {
                result.add(each);
            }
        });
        return result;
    }

    @Override
    MutableSortedMap<T, Integer> toMapOfItemToCount();

    @Override
    default MutableSortedBag<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    default MutableSortedBag<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    default MutableSortedBag<T> withOccurrences(T element, int occurrences)
    {
        this.addOccurrences(element, occurrences);
        return this;
    }

    @Override
    default MutableSortedBag<T> withoutOccurrences(T element, int occurrences)
    {
        this.removeOccurrences(element, occurrences);
        return this;
    }

    @Override
    default MutableSortedBag<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    default MutableSortedBag<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    MutableSortedBag<T> newEmpty();

    MutableSortedBag<T> clone();

    /**
     * Returns an unmodifiable view of the set.
     *
     * @return an unmodifiable view of this set
     */
    @Override
    MutableSortedBag<T> asUnmodifiable();

    @Override
    MutableSortedBag<T> asSynchronized();

    @Override
    MutableSortedBag<T> tap(Procedure<? super T> procedure);

    @Override
    MutableSortedBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> MutableSortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    MutableSortedBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> MutableSortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionMutableSortedBag<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionMutableSortedBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionMutableSortedBag<T> partitionWhile(Predicate<? super T> predicate);

    @Override
    <S> MutableSortedBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> MutableList<V> collect(Function<? super T, ? extends V> function);

    @Override
    <V> MutableList<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function);

    /**
     * @since 9.1.
     */
    @Override
    default <V> MutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

    @Override
    MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    MutableByteList collectByte(ByteFunction<? super T> byteFunction);

    @Override
    MutableCharList collectChar(CharFunction<? super T> charFunction);

    @Override
    MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    MutableFloatList collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    MutableIntList collectInt(IntFunction<? super T> intFunction);

    @Override
    MutableLongList collectLong(LongFunction<? super T> longFunction);

    @Override
    MutableShortList collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> MutableList<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> MutableList<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    MutableSortedSet<T> distinct();

    @Override
    MutableSortedBag<T> takeWhile(Predicate<? super T> predicate);

    @Override
    MutableSortedBag<T> dropWhile(Predicate<? super T> predicate);

    @Override
    <V> MutableSortedBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> MutableSortedBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.0
     */
    @Override
    default <V> MutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.asLazy().<V>collect(function).toBag();
    }

    /**
     * @since 9.0
     */
    @Override
    default <V, P> MutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.asLazy().<P, V>collectWith(function, parameter).toBag();
    }

    /**
     * @since 10.0.0
     */
    @Override
    default <V> MutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.asLazy().flatCollect(function).toBag();
    }

    @Override
    <S> MutableList<Pair<T, S>> zip(Iterable<S> that);

    @Override
    MutableSortedSet<Pair<T, Integer>> zipWithIndex();

    @Override
    MutableSortedBag<T> toReversed();

    @Override
    MutableSortedBag<T> take(int count);

    @Override
    MutableSortedBag<T> drop(int count);

    /**
     * @since 11.0
     */
    @Override
    default ImmutableSortedBag<T> toImmutableSortedBag()
    {
        return this.toImmutable();
    }
}
