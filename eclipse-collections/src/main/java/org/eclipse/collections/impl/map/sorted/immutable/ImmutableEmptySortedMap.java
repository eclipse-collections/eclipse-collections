/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.sorted.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.partition.list.PartitionImmutableListImpl;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * This is a zero element {@link ImmutableSortedMap} which is created by calling SortedMaps.immutable.empty().
 */
final class ImmutableEmptySortedMap<K, V>
        extends AbstractImmutableSortedMap<K, V>
        implements Serializable
{
    static final ImmutableSortedMap<?, ?> INSTANCE = new ImmutableEmptySortedMap<>();
    private static final PartitionImmutableList<?> EMPTY_PARTITION =
            new PartitionImmutableListImpl<>(Lists.immutable.empty(), Lists.immutable.empty());
    private static final long serialVersionUID = 2L;

    private final Comparator<? super K> comparator;

    ImmutableEmptySortedMap()
    {
        this.comparator = null;
    }

    ImmutableEmptySortedMap(Comparator<? super K> comparator)
    {
        this.comparator = comparator;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public RichIterable<K> keysView()
    {
        return LazyIterate.empty();
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return LazyIterate.empty();
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return LazyIterate.empty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return false;
    }

    @Override
    public boolean containsValue(Object value)
    {
        return false;
    }

    @Override
    public V get(Object key)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "{}";
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Map))
        {
            return false;
        }

        return ((Map<K, V>) other).isEmpty();
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public ImmutableSortedMap<K, V> tap(Procedure<? super V> procedure)
    {
        return this;
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
    }

    @Override
    public ImmutableMap<V, K> flipUniqueValues()
    {
        return Maps.immutable.with();
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public boolean notEmpty()
    {
        return false;
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        return null;
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        return function.value();
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        return value;
    }

    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        return function.valueOf(parameter);
    }

    @Override
    public <K2, V2> ImmutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return Maps.immutable.empty();
    }

    @Override
    public <R> ImmutableSortedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return SortedMaps.immutable.with(this.comparator);
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return null;
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        return Optional.empty();
    }

    @Override
    public ImmutableSortedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return this;
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return Sets.immutable.<Entry<K, V>>with().castToSet();
    }

    @Override
    public ImmutableSortedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return this;
    }

    @Override
    public Comparator<? super K> comparator()
    {
        return this.comparator;
    }

    @Override
    public Set<K> keySet()
    {
        return Sets.immutable.<K>of().castToSet();
    }

    @Override
    public Collection<V> values()
    {
        return Lists.immutable.<V>empty().castToList();
    }

    @Override
    public K firstKey()
    {
        throw new NoSuchElementException();
    }

    @Override
    public K lastKey()
    {
        throw new NoSuchElementException();
    }

    private Object writeReplace()
    {
        return new ImmutableSortedMapSerializationProxy<>(this);
    }

    @Override
    public ImmutableSortedMap<K, V> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        return this;
    }

    @Override
    public ImmutableSortedMap<K, V> drop(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        return this;
    }

    @Override
    public ImmutableSortedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        return this;
    }

    @Override
    public ImmutableSortedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        return this;
    }

    @Override
    public PartitionImmutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        return (PartitionImmutableList<V>) EMPTY_PARTITION;
    }

    @Override
    public V getOnly()
    {
        throw new IllegalStateException("Size must be 1 but was " + this.size());
    }
}
