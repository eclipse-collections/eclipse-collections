/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.multimap.bag.MutableBagIterableMultimap;
import org.eclipse.collections.api.partition.bag.PartitionMutableBagIterable;
import org.eclipse.collections.api.set.MutableSetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;

/**
 * MutableBagIterable is the common parent interface for MutableBag and MutableSortedBag.
 * It combines Bag with MutableCollection, providing mutating operations for managing element
 * occurrences in a bag. All modifications alter the bag in place.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MutableBagIterable<String> bag = Bags.mutable.empty();
 * bag.add("A");
 * bag.addOccurrences("A", 2); // "A" now has 3 occurrences
 * bag.removeOccurrences("A", 1); // "A" now has 2 occurrences
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 */
public interface MutableBagIterable<T> extends Bag<T>, MutableCollection<T>
{
    /**
     * Add number of {@code occurrences} for an {@code item}. If the {@code item} does not exist, then the {@code item} is added to the bag.
     *
     * <p>
     * For Example:
     * <pre>
     * MutableBagIterable&lt;String&gt; names = Bags.mutable.of("A", "B", "B");
     * Assert.assertEquals(4, names.<b>addOccurrences</b>("A", 3));
     * </pre>
     *
     * @return updated number of occurrences.
     * @throws IllegalArgumentException if {@code occurrences} are less than 0.
     */
    int addOccurrences(T item, int occurrences);

    boolean removeOccurrences(Object item, int occurrences);

    boolean setOccurrences(T item, int occurrences);

    @Override
    MutableBagIterable<T> tap(Procedure<? super T> procedure);

    @Override
    MutableBagIterable<T> select(Predicate<? super T> predicate);

    @Override
    <P> MutableBagIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    MutableBagIterable<T> reject(Predicate<? super T> predicate);

    @Override
    <P> MutableBagIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionMutableBagIterable<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionMutableBagIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> MutableBagIterable<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> MutableBagIterableMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> MutableBagIterableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    MutableSetIterable<Pair<T, Integer>> zipWithIndex();

    @Override
    MutableBagIterable<T> selectByOccurrences(IntPredicate predicate);

    /**
     * @since 9.2
     */
    @Override
    default MutableBagIterable<T> selectDuplicates()
    {
        return this.selectByOccurrences(occurrences -> occurrences > 1);
    }

    /**
     * @since 9.2
     */
    @Override
    MutableSetIterable<T> selectUnique();

    @Override
    MutableMapIterable<T, Integer> toMapOfItemToCount();

    /**
     * @since 6.0
     */
    @Override
    MutableList<ObjectIntPair<T>> topOccurrences(int count);

    /**
     * @since 6.0
     */
    @Override
    MutableList<ObjectIntPair<T>> bottomOccurrences(int count);

    @Override
    default MutableBagIterable<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    default MutableBagIterable<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    default MutableBagIterable<T> withOccurrences(T element, int occurrences)
    {
        this.addOccurrences(element, occurrences);
        return this;
    }

    default MutableBagIterable<T> withoutOccurrences(T element, int occurrences)
    {
        this.removeOccurrences(element, occurrences);
        return this;
    }

    @Override
    default MutableBagIterable<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    default MutableBagIterable<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    <V> RichIterable<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function);
}
