/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.MapIterate;

/**
 * This class provides a MutableMap wrapper around a JDK Collections Map interface instance. All the MutableMap
 * interface methods are supported in addition to the JDK Map interface methods.
 * <p>
 * To create a new wrapper around an existing Map instance, use the {@link #adapt(Map)} factory method.
 */
public class MapAdapter<K, V>
        extends AbstractMutableMap<K, V>
        implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Map<K, V> delegate;

    protected MapAdapter(Map<K, V> newDelegate)
    {
        if (newDelegate == null)
        {
            throw new NullPointerException("MapAdapter may not wrap null");
        }
        this.delegate = newDelegate;
    }

    public static <K, V> MutableMap<K, V> adapt(Map<K, V> map)
    {
        return map instanceof MutableMap<?, ?> ? (MutableMap<K, V>) map : new MapAdapter<>(map);
    }

    @Override
    public String toString()
    {
        return this.delegate.toString();
    }

    @Override
    public MutableMap<K, V> clone()
    {
        return UnifiedMap.newMap(this.delegate);
    }

    @Override
    public <K2, V2> MutableMap<K2, V2> newEmpty(int capacity)
    {
        return UnifiedMap.newMap(capacity);
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.delegate.values().iterator();
    }

    @Override
    public V remove(Object key)
    {
        return this.delegate.remove(key);
    }

    @Override
    public Set<K> keySet()
    {
        return this.delegate.keySet();
    }

    @Override
    public Collection<V> values()
    {
        return this.delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return this.delegate.entrySet();
    }

    @Override
    public void clear()
    {
        this.delegate.clear();
    }

    @Override
    public MutableMap<K, V> newEmpty()
    {
        return Maps.mutable.empty();
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        MapIterate.forEachKeyValue(this.delegate, procedure);
    }

    @Override
    public V get(Object key)
    {
        return this.delegate.get(key);
    }

    @Override
    public V put(K key, V value)
    {
        return this.delegate.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        this.delegate.putAll(map);
    }

    @Override
    public <E> MutableMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> valueFunction)
    {
        Iterate.addToMap(iterable, keyFunction, valueFunction, this.delegate);
        return this;
    }

    @Override
    public V removeKey(K key)
    {
        return this.delegate.remove(key);
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        return this.delegate.merge(key, value, remappingFunction);
    }

    @Override
    public boolean equals(Object o)
    {
        return this.delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        return Maps.immutable.ofMap(this);
    }
}
