/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.multimap.bag.ImmutableBagIterableMultimap;
import org.eclipse.collections.api.partition.bag.PartitionImmutableBagIterable;
import org.eclipse.collections.api.set.ImmutableSetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * ImmutableBagIterable is the common parent interface for ImmutableBag and ImmutableSortedBag.
 * It combines the Bag interface with ImmutableCollection, providing read-only access to bag operations
 * with methods that return new immutable instances when modifications would normally occur.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableBagIterable<String> bag = Bags.immutable.of("A", "B", "A", "C");
 * ImmutableBagIterable<String> filtered = bag.select(s -> s.compareTo("B") >= 0);
 * int count = bag.occurrencesOf("A"); // Returns 2
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 */
public interface ImmutableBagIterable<T> extends Bag<T>, ImmutableCollection<T>
{
    @Override
    ImmutableBagIterable<T> tap(Procedure<? super T> procedure);

    @Override
    ImmutableBagIterable<T> select(Predicate<? super T> predicate);

    @Override
    <P> ImmutableBagIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    ImmutableBagIterable<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ImmutableBagIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableBagIterable<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionImmutableBagIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> ImmutableBagIterable<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> ImmutableBagIterableMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> ImmutableBagIterableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    ImmutableSetIterable<Pair<T, Integer>> zipWithIndex();

    @Override
    ImmutableBagIterable<T> selectByOccurrences(IntPredicate predicate);

    /**
     * @since 9.2
     */
    @Override
    default ImmutableBagIterable<T> selectDuplicates()
    {
        return this.selectByOccurrences(occurrences -> occurrences > 1);
    }

    /**
     * @since 9.2
     */
    @Override
    ImmutableSetIterable<T> selectUnique();

    @Override
    MutableMapIterable<T, Integer> toMapOfItemToCount();

    @Override
    <V> ImmutableCollection<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function);
}
