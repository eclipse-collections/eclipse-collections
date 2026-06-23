/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.ordered.mutable;

import java.util.Map;

import org.eclipse.collections.api.factory.map.ordered.MutableOrderedMapFactory;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;

@aQute.bnd.annotation.spi.ServiceProvider(MutableOrderedMapFactory.class)
public class MutableOrderedMapFactoryImpl implements MutableOrderedMapFactory
{
    public static final MutableOrderedMapFactory INSTANCE = new MutableOrderedMapFactoryImpl();

    @Override
    public <K, V> MutableOrderedMap<K, V> empty()
    {
        return new OrderedHashMap<>();
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> of()
    {
        return this.empty();
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> with()
    {
        return this.empty();
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> ofInitialCapacity(int capacity)
    {
        return this.withInitialCapacity(capacity);
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> withInitialCapacity(int capacity)
    {
        return new OrderedHashMap<>(capacity);
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> of(K key, V value)
    {
        return this.with(key, value);
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> with(K key, V value)
    {
        MutableOrderedMap<K, V> map = this.withInitialCapacity(1);
        map.put(key, value);
        return map;
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> of(K key1, V value1, K key2, V value2)
    {
        return this.with(key1, value1, key2, value2);
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> with(K key1, V value1, K key2, V value2)
    {
        MutableOrderedMap<K, V> map = this.withInitialCapacity(2);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return this.with(key1, value1, key2, value2, key3, value3);
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        MutableOrderedMap<K, V> map = this.withInitialCapacity(3);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return this.with(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        MutableOrderedMap<K, V> map = this.withInitialCapacity(4);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return map;
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> ofMap(Map<? extends K, ? extends V> map)
    {
        return this.withMap(map);
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        MutableOrderedMap<K, V> result = this.withInitialCapacity(map.size());
        result.putAll(map);
        return result;
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> ofMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        return this.withMapIterable(mapIterable);
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        MutableOrderedMap<K, V> result = this.withInitialCapacity(mapIterable.size());
        result.putAllMapIterable(mapIterable);
        return result;
    }
}
