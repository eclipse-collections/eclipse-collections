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

import java.util.Objects;

import org.eclipse.collections.api.factory.map.FixedSizeMapFactory;
import org.eclipse.collections.api.map.FixedSizeMap;

/**
 * FixedSizeMapFactoryImpl is the implementation of {@link FixedSizeMapFactory} that creates memory-efficient fixed-size maps.
 * <p>
 * This factory creates specialized map implementations optimized for small sizes:
 * <ul>
 *   <li>0 entries: {@link EmptyMap} (singleton)</li>
 *   <li>1 entry: {@link SingletonMap}</li>
 *   <li>2 entries: {@link DoubletonMap}</li>
 *   <li>3 entries: {@link TripletonMap}</li>
 * </ul>
 * <p>
 * Maps with 4 or more entries should use UnifiedMap instead, which is not fixed-size.
 * These specialized implementations avoid hash table overhead and use minimal memory by storing
 * entries directly as fields.
 * <p>
 * <b>Duplicate Key Handling:</b> When creating maps with duplicate keys, the factory automatically
 * collapses them, keeping the last value for each key. This ensures the resulting map size matches
 * the number of unique keys.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * FixedSizeMap<String, Integer> empty = Maps.fixedSize.of();
 * FixedSizeMap<String, Integer> one = Maps.fixedSize.of("a", 1);
 * FixedSizeMap<String, Integer> two = Maps.fixedSize.of("a", 1, "b", 2);
 * FixedSizeMap<String, Integer> three = Maps.fixedSize.of("a", 1, "b", 2, "c", 3);
 * }</pre>
 */
@aQute.bnd.annotation.spi.ServiceProvider(FixedSizeMapFactory.class)
public class FixedSizeMapFactoryImpl implements FixedSizeMapFactory
{
    public static final FixedSizeMapFactory INSTANCE = new FixedSizeMapFactoryImpl();

    private static final FixedSizeMap<?, ?> EMPTY_MAP = new EmptyMap<>();

    @Override
    public <K, V> FixedSizeMap<K, V> empty()
    {
        return (FixedSizeMap<K, V>) EMPTY_MAP;
    }

    @Override
    public <K, V> FixedSizeMap<K, V> of()
    {
        return this.empty();
    }

    @Override
    public <K, V> FixedSizeMap<K, V> with()
    {
        return this.empty();
    }

    @Override
    public <K, V> FixedSizeMap<K, V> of(K key, V value)
    {
        return this.with(key, value);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> with(K key, V value)
    {
        return new SingletonMap<>(key, value);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> of(K key1, V value1, K key2, V value2)
    {
        return this.with(key1, value1, key2, value2);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> with(K key1, V value1, K key2, V value2)
    {
        if (Objects.equals(key1, key2))
        {
            return this.of(key1, value2);
        }
        return new DoubletonMap<>(key1, value1, key2, value2);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return this.with(key1, value1, key2, value2, key3, value3);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        if (Objects.equals(key1, key2) && Objects.equals(key2, key3))
        {
            return this.of(key1, value3);
        }
        if (Objects.equals(key1, key2))
        {
            return this.of(key1, value2, key3, value3);
        }
        if (Objects.equals(key1, key3))
        {
            return this.of(key2, value2, key1, value3);
        }
        if (Objects.equals(key2, key3))
        {
            return this.of(key1, value1, key2, value3);
        }
        return new TripletonMap<>(key1, value1, key2, value2, key3, value3);
    }
}
