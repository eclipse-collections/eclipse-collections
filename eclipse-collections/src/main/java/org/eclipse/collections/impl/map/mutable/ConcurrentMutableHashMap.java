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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.MapEntryToProcedure2;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.internal.IterableIterate;

/**
 * A simple concurrent implementation of MutableMap which uses java.util.concurrent.ConcurrentHashMap for its underlying
 * concurrent Map implementation.
 *
 * @see org.eclipse.collections.impl.map.mutable.ConcurrentHashMap
 * @deprecated since 2.0
 */
@Deprecated
public final class ConcurrentMutableHashMap<K, V>
        extends AbstractMutableMap<K, V>
        implements ConcurrentMutableMap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    private final ConcurrentMap<K, V> delegate;

    private ConcurrentMutableHashMap()
    {
        this(new ConcurrentHashMap<>());
    }

    public ConcurrentMutableHashMap(ConcurrentMap<K, V> delegate)
    {
        this.delegate = delegate;
    }

    public static <NK, NV> ConcurrentMutableHashMap<NK, NV> newMap()
    {
        return new ConcurrentMutableHashMap<>();
    }

    public static <NK, NV> ConcurrentMutableHashMap<NK, NV> newMap(int initialCapacity)
    {
        return new ConcurrentMutableHashMap<>(new ConcurrentHashMap<>(initialCapacity));
    }

    public static <NK, NV> ConcurrentMutableHashMap<NK, NV> newMap(int initialCapacity, float loadFactor, int concurrencyLevel)
    {
        return new ConcurrentMutableHashMap<>(new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel));
    }

    public static <NK, NV> ConcurrentMutableHashMap<NK, NV> newMap(Map<NK, NV> map)
    {
        return new ConcurrentMutableHashMap<>(new ConcurrentHashMap<>(map));
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withKeyValue(K key, V value)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withKeyValue(key, value);
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withAllKeyValues(keyValues);
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValues)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withAllKeyValueArguments(keyValues);
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withoutKey(K key)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withoutKey(key);
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withoutAllKeys(keys);
    }

    @Override
    public String toString()
    {
        return this.delegate.toString();
    }

    @Override
    public MutableMap<K, V> clone()
    {
        return ConcurrentMutableHashMap.newMap(this.delegate);
    }

    @Override
    public <K2, V2> MutableMap<K2, V2> newEmpty(int capacity)
    {
        return ConcurrentMutableHashMap.newMap();
    }

    @Override
    public boolean notEmpty()
    {
        return !this.delegate.isEmpty();
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        Iterate.forEachWithIndex(this.delegate.values(), objectIntProcedure);
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
        return ConcurrentMutableHashMap.newMap();
    }

    @Override
    public ConcurrentMutableMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.each(procedure);
        return this;
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        IterableIterate.forEach(this.delegate.values(), procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        IterableIterate.forEach(this.delegate.keySet(), procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        IterableIterate.forEach(this.delegate.entrySet(), new MapEntryToProcedure2<>(procedure));
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
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            V blockValue = function.value();
            V putResult = this.delegate.putIfAbsent(key, blockValue);
            return putResult == null ? blockValue : putResult;
        }
        return result;
    }

    @Override
    public V getIfAbsentPut(K key, V value)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            V putResult = this.delegate.putIfAbsent(key, value);
            return putResult == null ? value : putResult;
        }
        return result;
    }

    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            V functionValue = function.valueOf(parameter);
            V putResult = this.delegate.putIfAbsent(key, functionValue);
            return putResult == null ? functionValue : putResult;
        }
        return result;
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            return function.value();
        }
        return result;
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            return value;
        }
        return result;
    }

    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            return function.valueOf(parameter);
        }
        return result;
    }

    public V getIfAbsentPut(K key, Function<? super K, ? extends V> factory)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".getIfAbsentPut() not implemented yet");
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        V result = this.delegate.get(key);
        return result == null ? null : function.valueOf(result);
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
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        Iterate.forEachWith(this.delegate.values(), procedure, parameter);
    }

    @Override
    public V putIfAbsent(K key, V value)
    {
        return this.delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value)
    {
        return this.delegate.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue)
    {
        return this.delegate.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value)
    {
        return this.delegate.replace(key, value);
    }

    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        while (true)
        {
            V originalValue = this.delegate.get(key);
            if (originalValue == null)
            {
                V zero = factory.value();
                V newValue = function.valueOf(zero);
                if (this.delegate.putIfAbsent(key, newValue) == null)
                {
                    return newValue;
                }
            }
            else
            {
                V newValue = function.valueOf(originalValue);
                if (this.delegate.replace(key, originalValue, newValue))
                {
                    return newValue;
                }
            }
        }
    }

    @Override
    public <P> V updateValueWith(K key, Function0<? extends V> factory, Function2<? super V, ? super P, ? extends V> function, P parameter)
    {
        while (true)
        {
            V originalValue = this.delegate.get(key);
            if (originalValue == null)
            {
                V zero = factory.value();
                V newValue = function.value(zero, parameter);
                if (this.delegate.putIfAbsent(key, newValue) == null)
                {
                    return newValue;
                }
            }
            else
            {
                V newValue = function.value(originalValue, parameter);
                if (this.delegate.replace(key, originalValue, newValue))
                {
                    return newValue;
                }
            }
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        return this.delegate.merge(key, value, remappingFunction);
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        return Maps.immutable.ofMap(this);
    }
}
