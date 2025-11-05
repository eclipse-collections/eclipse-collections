/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.eclipse.collections.api.block.procedure.Procedure;

/**
 * A ConcurrentMutableMap provides an API which combines and supports both MutableMap and ConcurrentMap.
 * It extends both interfaces to provide a thread-safe mutable map with concurrent access capabilities.
 * All operations that modify the map are thread-safe and atomic when appropriate.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ConcurrentMutableMap<String, Integer> map = ConcurrentHashMap.newMap();
 * map.put("A", 1);
 * map.getIfAbsentPut("B", () -> 2);
 * map.updateValue("A", () -> 0, value -> value + 10);
 * }</pre>
 */
public interface ConcurrentMutableMap<K, V>
        extends MutableMap<K, V>, ConcurrentMap<K, V>
{
    /**
     * Executes the given procedure for each value in the map and returns this map.
     * This method is useful for chaining method calls. The iteration order is not guaranteed
     * in concurrent maps.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ConcurrentMutableMap<String, Integer> map = ConcurrentHashMap.newMap();
     * map.put("A", 1);
     * map.put("B", 2);
     * map.tap(value -> System.out.println("Value: " + value));
     * }</pre>
     *
     * @param procedure the procedure to execute for each value
     * @return this map
     */
    @Override
    ConcurrentMutableMap<K, V> tap(Procedure<? super V> procedure);

    /**
     * Returns the value associated with the specified key, or the default value if no mapping exists.
     * This is a thread-safe operation.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ConcurrentMutableMap<String, Integer> map = ConcurrentHashMap.newMap();
     * map.put("A", 1);
     * Integer value = map.getOrDefault("B", 0); // Returns 0
     * }</pre>
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the value to return if no mapping exists
     * @return the value associated with the key, or the default value
     */
    @Override
    default V getOrDefault(Object key, V defaultValue)
    {
        return this.getIfAbsentValue((K) key, defaultValue);
    }

    /**
     * Adds all key-value pairs from the specified map to this map and returns this map.
     * This method is useful for chaining. The operation is thread-safe but not atomic
     * across all entries being added.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ConcurrentMutableMap<String, Integer> map = ConcurrentHashMap.newMap();
     * Map<String, Integer> otherMap = new HashMap<>();
     * otherMap.put("A", 1);
     * otherMap.put("B", 2);
     * map.withMap(otherMap);
     * }</pre>
     *
     * @param map the map containing key-value pairs to add
     * @return this map
     */
    @Override
    default ConcurrentMutableMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        this.putAll(map);
        return this;
    }

    /**
     * Adds all key-value pairs from the specified MapIterable to this map and returns this map.
     * This method is useful for chaining. The operation is thread-safe but not atomic
     * across all entries being added.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ConcurrentMutableMap<String, Integer> map = ConcurrentHashMap.newMap();
     * MapIterable<String, Integer> other = Maps.mutable.of("A", 1, "B", 2);
     * map.withMapIterable(other);
     * }</pre>
     *
     * @param mapIterable the map containing key-value pairs to add
     * @return this map
     */
    @Override
    default ConcurrentMutableMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        this.putAllMapIterable(mapIterable);
        return this;
    }

    /**
     * Performs the given action for each key-value pair in the map.
     * The action is performed in a thread-safe manner for each individual entry,
     * but the overall iteration is not atomic.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ConcurrentMutableMap<String, Integer> map = ConcurrentHashMap.newMap();
     * map.put("A", 1);
     * map.put("B", 2);
     * map.forEach((key, value) -> System.out.println(key + ": " + value));
     * }</pre>
     *
     * @param action the action to perform for each key-value pair
     */
    @Override
    default void forEach(BiConsumer<? super K, ? super V> action)
    {
        MutableMap.super.forEach(action);
    }

    /**
     * A concurrent implementation of {@link ConcurrentMap#merge(Object, Object, BiFunction)} and
     * {@link Map#merge(Object, Object, BiFunction)}. If the specified key is not already associated
     * with a value or is associated with null, associates it with the given non-null value. Otherwise,
     * replaces the associated value with the results of the given remapping function.
     *
     * <p>In the implementing classes, it is possible for the {@code remappingFunction} to be called
     * multiple times due to concurrent modifications. It is also possible for the {@code remappingFunction}
     * to be called one or more times, but the result is not used (because the old entry was concurrently removed).</p>
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ConcurrentMutableMap<String, Integer> map = ConcurrentHashMap.newMap();
     * map.put("A", 1);
     * map.merge("A", 5, (oldValue, newValue) -> oldValue + newValue); // Results in 6
     * map.merge("B", 10, (oldValue, newValue) -> oldValue + newValue); // Results in 10
     * }</pre>
     *
     * @param key the key with which the resulting value is to be associated
     * @param value the non-null value to be merged with the existing value
     * @param remappingFunction the function to recompute a value if present
     * @return the new value associated with the specified key
     */
    @Override
    V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction);
}
