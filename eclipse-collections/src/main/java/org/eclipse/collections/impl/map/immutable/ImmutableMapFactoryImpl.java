/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.immutable;

import java.util.Map;
import java.util.Objects;

import org.eclipse.collections.api.factory.map.ImmutableMapFactory;
import org.eclipse.collections.api.map.ImmutableMap;

/**
 * ImmutableMapFactoryImpl is the implementation of {@link ImmutableMapFactory} that creates immutable map instances.
 * <p>
 * This factory creates specialized map implementations optimized for different sizes:
 * <ul>
 *   <li>0 entries: {@link ImmutableEmptyMap} (singleton)</li>
 *   <li>1 entry: {@link ImmutableSingletonMap}</li>
 *   <li>2 entries: {@link ImmutableDoubletonMap}</li>
 *   <li>3 entries: {@link ImmutableTripletonMap}</li>
 *   <li>4 entries: {@link ImmutableQuadrupletonMap}</li>
 *   <li>5+ entries: {@link ImmutableUnifiedMap}</li>
 * </ul>
 * <p>
 * Maps with 0-4 entries use memory-efficient implementations that store entries directly as fields.
 * Larger maps use a hash table-based implementation.
 * <p>
 * <b>Duplicate Key Handling:</b> When creating maps with duplicate keys, the factory automatically
 * collapses them, keeping the last value for each key. This ensures the resulting map size matches
 * the number of unique keys.
 * <p>
 * All created maps are immutable and thread-safe by design.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableMap<String, Integer> empty = Maps.immutable.of();
 * ImmutableMap<String, Integer> one = Maps.immutable.of("a", 1);
 * ImmutableMap<String, Integer> two = Maps.immutable.of("a", 1, "b", 2);
 * ImmutableMap<String, Integer> fromMap = Maps.immutable.ofAll(existingMap);
 * }</pre>
 */
@aQute.bnd.annotation.spi.ServiceProvider(ImmutableMapFactory.class)
public class ImmutableMapFactoryImpl implements ImmutableMapFactory
{
    public static final ImmutableMapFactory INSTANCE = new ImmutableMapFactoryImpl();

    @Override
    public <K, V> ImmutableMap<K, V> empty()
    {
        return (ImmutableMap<K, V>) ImmutableEmptyMap.INSTANCE;
    }

    @Override
    public <K, V> ImmutableMap<K, V> of()
    {
        return this.empty();
    }

    @Override
    public <K, V> ImmutableMap<K, V> with()
    {
        return this.empty();
    }

    @Override
    public <K, V> ImmutableMap<K, V> of(K key, V value)
    {
        return this.with(key, value);
    }

    @Override
    public <K, V> ImmutableMap<K, V> with(K key, V value)
    {
        return new ImmutableSingletonMap<>(key, value);
    }

    @Override
    public <K, V> ImmutableMap<K, V> of(K key1, V value1, K key2, V value2)
    {
        return this.with(key1, value1, key2, value2);
    }

    @Override
    public <K, V> ImmutableMap<K, V> with(K key1, V value1, K key2, V value2)
    {
        if (Objects.equals(key1, key2))
        {
            return this.of(key1, value2);
        }
        return new ImmutableDoubletonMap<>(key1, value1, key2, value2);
    }

    @Override
    public <K, V> ImmutableMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return this.with(key1, value1, key2, value2, key3, value3);
    }

    @Override
    public <K, V> ImmutableMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3)
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

        return new ImmutableTripletonMap<>(key1, value1, key2, value2, key3, value3);
    }

    @Override
    public <K, V> ImmutableMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return this.with(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    @Override
    public <K, V> ImmutableMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        if (Objects.equals(key1, key2))
        {
            return this.of(key1, value2, key3, value3, key4, value4);
        }
        if (Objects.equals(key1, key3))
        {
            return this.of(key2, value2, key1, value3, key4, value4);
        }
        if (Objects.equals(key1, key4))
        {
            return this.of(key2, value2, key3, value3, key1, value4);
        }
        if (Objects.equals(key2, key3))
        {
            return this.of(key1, value1, key2, value3, key4, value4);
        }
        if (Objects.equals(key2, key4))
        {
            return this.of(key1, value1, key3, value3, key2, value4);
        }
        if (Objects.equals(key3, key4))
        {
            return this.of(key1, value1, key2, value2, key3, value4);
        }
        return new ImmutableQuadrupletonMap<>(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    /**
     * @deprecated use {@link #ofAll(Map)} instead (inlineable)
     */
    @Override
    @Deprecated
    public <K, V> ImmutableMap<K, V> ofMap(Map<? extends K, ? extends V> map)
    {
        return this.ofAll(map);
    }

    @Override
    public <K, V> ImmutableMap<K, V> ofAll(Map<? extends K, ? extends V> map)
    {
        return this.withAll(map);
    }

    @Override
    public <K, V> ImmutableMap<K, V> withAll(Map<? extends K, ? extends V> map)
    {
        if (map.isEmpty())
        {
            return this.empty();
        }

        if (map.size() > 4)
        {
            return new ImmutableUnifiedMap<>(map);
        }

        Map.Entry<K, V>[] entries = map.entrySet().toArray(new Map.Entry[map.entrySet().size()]);
        switch (entries.length)
        {
            case 1:
                return this.of(entries[0].getKey(), entries[0].getValue());
            case 2:
                return this.of(
                        entries[0].getKey(), entries[0].getValue(),
                        entries[1].getKey(), entries[1].getValue());
            case 3:
                return this.of(
                        entries[0].getKey(), entries[0].getValue(),
                        entries[1].getKey(), entries[1].getValue(),
                        entries[2].getKey(), entries[2].getValue());
            case 4:
                return this.of(
                        entries[0].getKey(), entries[0].getValue(),
                        entries[1].getKey(), entries[1].getValue(),
                        entries[2].getKey(), entries[2].getValue(),
                        entries[3].getKey(), entries[3].getValue());
            default:
                throw new AssertionError();
        }
    }
}
