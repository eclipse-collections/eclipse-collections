/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.set;

import java.util.Set;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.multimap.set.MutableSetIterableMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionMutableSetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * @since 6.0
 */
public interface MutableSetIterable<T> extends SetIterable<T>, MutableCollection<T>, Set<T>
{
    @Override
    default Object[] toArray()
    {
        return MutableCollection.super.toArray();
    }

    @Override
    default <T1> T1[] toArray(T1[] a)
    {
        return MutableCollection.super.toArray(a);
    }

    @Override
    MutableSetIterable<T> tap(Procedure<? super T> procedure);

    @Override
    MutableSetIterable<T> select(Predicate<? super T> predicate);

    @Override
    <P> MutableSetIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    MutableSetIterable<T> reject(Predicate<? super T> predicate);

    @Override
    <P> MutableSetIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionMutableSetIterable<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionMutableSetIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> MutableSetIterable<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> MutableSetIterableMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> MutableSetIterableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> MutableCollection<Pair<T, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    MutableSetIterable<Pair<T, Integer>> zipWithIndex();

    @Override
    default MutableSetIterable<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    default MutableSetIterable<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    default MutableSetIterable<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    default MutableSetIterable<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }
}
