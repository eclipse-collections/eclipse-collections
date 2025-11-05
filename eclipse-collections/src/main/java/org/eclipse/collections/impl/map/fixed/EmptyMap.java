/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.fixed;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Multimaps;

/**
 * EmptyMap is a memory-efficient implementation of a fixed-size map with zero entries.
 * <p>
 * This class represents an empty map that uses minimal memory. It is immutable in the sense that
 * all mutation operations either throw exceptions or return new map instances via copy-on-write semantics.
 * <p>
 * <b>Key Characteristics:</b>
 * <ul>
 *   <li>Size is always 0</li>
 *   <li>All query operations return empty results</li>
 *   <li>withKeyValue returns a SingletonMap</li>
 *   <li>withoutKey returns this same instance</li>
 *   <li>Serializable and uses readResolve for singleton pattern</li>
 * </ul>
 *
 * @param <K> the type of keys (never actually present)
 * @param <V> the type of values (never actually present)
 */
final class EmptyMap<K, V>
        extends AbstractMemoryEfficientMutableMap<K, V>
        implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Object readResolve()
    {
        return Maps.fixedSize.of();
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public MutableMap<K, V> withKeyValue(K addKey, V addValue)
    {
        return new SingletonMap<>(addKey, addValue);
    }

    @Override
    public MutableMap<K, V> withoutKey(K key)
    {
        return this;
    }

    // Weird implementation of clone() is ok on final classes
    @Override
    public EmptyMap<K, V> clone()
    {
        return this;
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        return Maps.immutable.empty();
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        return Multimaps.mutable.set.with();
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
    public Set<K> keySet()
    {
        return Sets.fixedSize.of();
    }

    @Override
    public Collection<V> values()
    {
        return Lists.fixedSize.of();
    }

    @Override
    public MutableSet<Entry<K, V>> entrySet()
    {
        return Sets.fixedSize.of();
    }

    @Override
    public String toString()
    {
        return "{}";
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Map))
        {
            return false;
        }
        Map<K, V> that = (Map<K, V>) other;
        return that.size() == this.size();
    }

    @Override
    public MutableMap<V, K> flipUniqueValues()
    {
        return Maps.fixedSize.with();
    }

    @Override
    public FixedSizeMap<K, V> tap(Procedure<? super V> procedure)
    {
        return this;
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
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
    public FixedSizeMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return Maps.fixedSize.of();
    }

    @Override
    public <R> FixedSizeMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return Maps.fixedSize.of();
    }

    @Override
    public <R> FixedSizeMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        return Maps.fixedSize.of();
    }

    @Override
    public <K2, V2> FixedSizeMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return Maps.fixedSize.of();
    }

    @Override
    public FixedSizeMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return Maps.fixedSize.of();
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return null;
    }

    @Override
    public V getOnly()
    {
        throw new IllegalStateException("Size must be 1 but was " + this.size());
    }
}
