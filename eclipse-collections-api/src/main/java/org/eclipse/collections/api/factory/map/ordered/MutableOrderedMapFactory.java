/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.factory.map.ordered;

import java.util.Map;

import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;

/**
 * @since 14.0
 */
public interface MutableOrderedMapFactory
{
    <K, V> MutableOrderedMap<K, V> empty();

    <K, V> MutableOrderedMap<K, V> of();

    <K, V> MutableOrderedMap<K, V> with();

    <K, V> MutableOrderedMap<K, V> ofInitialCapacity(int capacity);

    <K, V> MutableOrderedMap<K, V> withInitialCapacity(int capacity);

    <K, V> MutableOrderedMap<K, V> of(K key, V value);

    <K, V> MutableOrderedMap<K, V> with(K key, V value);

    <K, V> MutableOrderedMap<K, V> of(K key1, V value1, K key2, V value2);

    <K, V> MutableOrderedMap<K, V> with(K key1, V value1, K key2, V value2);

    <K, V> MutableOrderedMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3);

    <K, V> MutableOrderedMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3);

    <K, V> MutableOrderedMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4);

    <K, V> MutableOrderedMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4);

    <K, V> MutableOrderedMap<K, V> ofMap(Map<? extends K, ? extends V> map);

    <K, V> MutableOrderedMap<K, V> withMap(Map<? extends K, ? extends V> map);

    <K, V> MutableOrderedMap<K, V> ofMapIterable(MapIterable<? extends K, ? extends V> mapIterable);

    <K, V> MutableOrderedMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable);
}
