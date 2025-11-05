/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map;

import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;

/**
 * A FixedSizeMap is a map that may be mutated, but cannot grow or shrink in size.
 * While values associated with existing keys can be updated, operations that would
 * change the number of entries in the map are not supported.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * FixedSizeMap<String, Integer> map = Maps.fixedSize.of("A", 1, "B", 2);
 * map.put("A", 10); // Updates value - allowed
 * map.put("C", 3);  // Would add entry - throws UnsupportedOperationException
 * map.remove("A");  // Would remove entry - throws UnsupportedOperationException
 * }</pre>
 */
public interface FixedSizeMap<K, V>
        extends MutableMap<K, V>
{
    /**
     * This operation is not supported on a FixedSizeMap as it would change the size of the map.
     *
     * @throws UnsupportedOperationException the {@code clear} operation is not supported by this map.
     */
    @Override
    void clear();

    /**
     * This operation is not supported on a FixedSizeMap as it could add a new key-value pair.
     * Use {@code updateValue} or {@code getIfAbsentPut} with existing keys to modify values.
     *
     * @param key the key to associate with the value
     * @param value the value to associate with the key
     * @return never returns normally
     * @throws UnsupportedOperationException the {@code put} operation is not supported by this map.
     */
    @Override
    V put(K key, V value);

    /**
     * This operation is not supported on a FixedSizeMap as it could add new key-value pairs.
     *
     * @param map the map containing key-value pairs to add
     * @throws UnsupportedOperationException the {@code putAll} operation is not supported by this map.
     */
    @Override
    void putAll(Map<? extends K, ? extends V> map);

    /**
     * This operation is not supported on a FixedSizeMap as it would change the size of the map.
     *
     * @param key the key whose mapping is to be removed
     * @return never returns normally
     * @throws UnsupportedOperationException the {@code remove} operation is not supported by this map.
     */
    @Override
    V remove(Object key);

    /**
     * This operation is not supported on a FixedSizeMap as it would change the size of the map.
     *
     * @param key the key whose mapping is to be removed
     * @return never returns normally
     * @throws UnsupportedOperationException the {@code removeKey} operation is not supported by this map.
     */
    @Override
    V removeKey(K key);

    /**
     * This operation is not supported on a FixedSizeMap as it would change the size of the map.
     *
     * @param keys the keys whose mappings are to be removed
     * @return never returns normally
     * @throws UnsupportedOperationException the {@code removeAllKeys} operation is not supported by this map.
     */
    @Override
    boolean removeAllKeys(Set<? extends K> keys);

    /**
     * This operation is not supported on a FixedSizeMap as it would change the size of the map.
     *
     * @param predicate the predicate to evaluate each key-value pair
     * @return never returns normally
     * @throws UnsupportedOperationException the {@code removeIf} operation is not supported by this map.
     */
    @Override
    boolean removeIf(Predicate2<? super K, ? super V> predicate);

    /**
     * Executes the given procedure for each value in the map and returns this map.
     * This method is useful for chaining method calls.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeMap<String, Integer> map = Maps.fixedSize.of("A", 1, "B", 2)
     *     .tap(value -> System.out.println("Value: " + value));
     * // Prints: Value: 1
     * //         Value: 2
     * }</pre>
     *
     * @param procedure the procedure to execute for each value
     * @return this map
     */
    @Override
    FixedSizeMap<K, V> tap(Procedure<? super V> procedure);

    /**
     * This operation is not supported on a FixedSizeMap as it would add new key-value pairs.
     *
     * @param map the map containing key-value pairs to add
     * @return never returns normally
     * @throws UnsupportedOperationException the {@code withMap} operation is not supported by this map.
     */
    @Override
    default FixedSizeMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call withMap() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on a FixedSizeMap as it would add new key-value pairs.
     *
     * @param mapIterable the map containing key-value pairs to add
     * @return never returns normally
     * @throws UnsupportedOperationException the {@code withMapIterable} operation is not supported by this map.
     */
    @Override
    default FixedSizeMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call withMapIterable() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on a FixedSizeMap as it would add new key-value pairs.
     *
     * @param mapIterable the map containing key-value pairs to add
     * @throws UnsupportedOperationException the {@code putAllMapIterable} operation is not supported by this map.
     */
    @Override
    default void putAllMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call putAllMapIterable() on " + this.getClass().getSimpleName());
    }
}
