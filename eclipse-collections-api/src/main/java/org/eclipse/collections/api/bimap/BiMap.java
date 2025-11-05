/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bimap;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.multimap.set.SetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionUnsortedSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A map that allows users to look up key-value pairs from either direction. Uniqueness is enforced on both the keys and values.
 * BiMap provides a bidirectional view where both keys and values must be unique, allowing efficient lookup in both directions
 * through the {@link #inverse()} method.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * BiMap<String, Integer> biMap = BiMaps.mutable.of("A", 1, "B", 2, "C", 3);
 * BiMap<Integer, String> inverse = biMap.inverse();
 * String key = inverse.get(2); // Returns "B"
 * Integer value = biMap.get("A"); // Returns 1
 * }</pre>
 *
 * @param <K> the type of keys in the map
 * @param <V> the type of values in the map
 * @since 4.2
 */
public interface BiMap<K, V> extends MapIterable<K, V>
{
    /**
     * Returns an inversed view of this BiMap, where the associations are in the direction of this bimap's values to keys.
     */
    BiMap<V, K> inverse();

    @Override
    SetMultimap<V, K> flip();

    @Override
    BiMap<V, K> flipUniqueValues();

    /**
     * Converts the BiMap to an ImmutableBiMap. If the bimap is immutable, it returns itself.
     */
    @Override
    ImmutableBiMap<K, V> toImmutable();

    @Override
    BiMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    BiMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    BiMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    /**
     * {@inheritDoc}
     *
     * Implementations are expected to delegate to {@link MutableBiMap#put(Object, Object)},
     * {@link ImmutableBiMap#newWithKeyValue(Object, Object)}, or equivalent, not {@link MutableBiMap#forcePut(Object, Object)}.
     *
     * @throws RuntimeException when {@code function} returns colliding keys or values.
     */
    @Override
    <K2, V2> BiMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    /**
     * {@inheritDoc}
     *
     * Implementations are expected to delegate to {@link MutableBiMap#put(Object, Object)},
     * {@link ImmutableBiMap#newWithKeyValue(Object, Object)}, or equivalent, not {@link MutableBiMap#forcePut(Object, Object)}.
     *
     * @throws RuntimeException when {@code function} returns colliding values.
     */
    @Override
    <R> BiMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> BiMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    @Override
    SetIterable<V> select(Predicate<? super V> predicate);

    @Override
    <P> SetIterable<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    SetIterable<V> reject(Predicate<? super V> predicate);

    @Override
    <P> SetIterable<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionUnsortedSet<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionUnsortedSet<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <S> SetIterable<S> selectInstancesOf(Class<S> clazz);

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> SetIterable<Pair<V, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    SetIterable<Pair<V, Integer>> zipWithIndex();

    @Override
    <V1> SetMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    @Override
    <V1> SetMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    @Override
    <VV> BiMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function);
}
