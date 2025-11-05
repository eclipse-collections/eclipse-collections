/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.factory;

import java.util.Map;

import org.eclipse.collections.api.factory.map.FixedSizeMapFactory;
import org.eclipse.collections.api.factory.map.ImmutableMapFactory;
import org.eclipse.collections.api.factory.map.MutableMapFactory;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.fixed.FixedSizeMapFactoryImpl;
import org.eclipse.collections.impl.map.immutable.ImmutableMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.MapAdapter;
import org.eclipse.collections.impl.map.mutable.MutableMapFactoryImpl;

/**
 * Factory utility class for creating instances of {@link org.eclipse.collections.api.map.MutableMap},
 * {@link org.eclipse.collections.api.map.ImmutableMap}, and {@link org.eclipse.collections.api.map.FixedSizeMap}.
 * <p>
 * This class provides static factory instances for creating map collections with different characteristics:
 * <ul>
 *   <li>{@link #mutable} - Creates standard mutable maps</li>
 *   <li>{@link #immutable} - Creates immutable maps</li>
 *   <li>{@link #fixedSize} - Creates fixed-size maps that cannot grow or shrink but allow value replacement</li>
 * </ul>
 * <p>
 * Maps associate keys with values, where each key can map to at most one value.
 * <p>
 * This class is thread-safe as all factory fields are immutable singletons.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Creating mutable maps
 * MutableMap<String, String> emptyMap = Maps.mutable.empty();
 * MutableMap<String, String> mapWith = Maps.mutable.with("a", "A", "b", "B", "c", "C");
 * MutableMap<String, String> mapOf = Maps.mutable.of("a", "A", "b", "B", "c", "C");
 *
 * // Creating immutable maps
 * ImmutableMap<String, String> immutableEmpty = Maps.immutable.empty();
 * ImmutableMap<String, String> immutableWith = Maps.immutable.with("a", "A", "b", "B");
 * ImmutableMap<String, String> immutableOf = Maps.immutable.of("a", "A", "b", "B");
 *
 * // Creating fixed-size maps
 * FixedSizeMap<String, String> fixedEmpty = Maps.fixedSize.empty();
 * FixedSizeMap<String, String> fixedWith = Maps.fixedSize.with("a", "A", "b", "B");
 * FixedSizeMap<String, String> fixedOf = Maps.fixedSize.of("a", "A", "b", "B");
 *
 * // Adapting JDK maps
 * Map<String, Integer> jdkMap = new HashMap<>();
 * MutableMap<String, Integer> adapted = Maps.adapt(jdkMap);
 * }</pre>
 *
 * @see org.eclipse.collections.api.map.MutableMap
 * @see org.eclipse.collections.api.map.ImmutableMap
 * @see org.eclipse.collections.api.map.FixedSizeMap
 * @see org.eclipse.collections.api.factory.map.MutableMapFactory
 * @see org.eclipse.collections.api.factory.map.ImmutableMapFactory
 * @see org.eclipse.collections.api.factory.map.FixedSizeMapFactory
 */
@SuppressWarnings("ConstantNamingConvention")
public final class Maps
{
    /**
     * Factory for creating immutable map instances.
     * <p>
     * Immutable maps are thread-safe and cannot be modified after creation.
     */
    public static final ImmutableMapFactory immutable = ImmutableMapFactoryImpl.INSTANCE;

    /**
     * Factory for creating fixed-size map instances.
     * <p>
     * Fixed-size maps cannot grow or shrink, but values can be replaced. Operations like
     * {@code put()} with a new key or {@code remove()} will throw {@code UnsupportedOperationException}.
     */
    public static final FixedSizeMapFactory fixedSize = FixedSizeMapFactoryImpl.INSTANCE;

    /**
     * Factory for creating mutable map instances.
     * <p>
     * Mutable maps are not thread-safe by default and can be modified after creation.
     */
    public static final MutableMapFactory mutable = MutableMapFactoryImpl.INSTANCE;

    private Maps()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Adapts a JDK {@link Map} to a {@link MutableMap}, providing a view that supports
     * Eclipse Collections APIs.
     * <p>
     * The returned map is a wrapper around the original map. Changes to either map
     * are visible in the other. This method is useful for interoperability with JDK collections.
     * <p>
     * The adapted map is not synchronized. If the original map is synchronized or
     * thread-safe, the adapted map will maintain those characteristics.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Map<String, Integer> jdkMap = new HashMap<>();
     * jdkMap.put("one", 1);
     * jdkMap.put("two", 2);
     * MutableMap<String, Integer> ecMap = Maps.adapt(jdkMap);
     *
     * // Changes are reflected in both
     * ecMap.put("three", 3);
     * assert jdkMap.size() == 3;
     *
     * // Eclipse Collections methods work
     * MutableMap<String, Integer> filtered = ecMap.select((k, v) -> v > 1);
     * }</pre>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param map the JDK map to adapt
     * @return a mutable map that wraps the provided JDK map
     * @throws NullPointerException if map is null
     * @see org.eclipse.collections.impl.map.mutable.MapAdapter
     * @since 9.0
     */
    public static <K, V> MutableMap<K, V> adapt(Map<K, V> map)
    {
        return MapAdapter.adapt(map);
    }
}
