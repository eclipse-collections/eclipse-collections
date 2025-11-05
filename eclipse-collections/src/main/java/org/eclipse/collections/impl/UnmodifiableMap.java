/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An unmodifiable wrapper for a Map that prevents all modification operations.
 * <p>
 * This class provides a read-only view of an underlying map. All query operations
 * (size, get, containsKey, etc.) are delegated to the wrapped map, but all modification
 * operations (put, remove, clear, etc.) throw {@link UnsupportedOperationException}.
 * </p>
 * <p>
 * Thread Safety: This class does not add any synchronization. Thread safety depends
 * on the thread safety of the underlying map.
 * </p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * Map<String, Integer> mutableMap = new HashMap<>();
 * mutableMap.put("one", 1);
 * Map<String, Integer> readOnlyMap = new UnmodifiableMap<>(mutableMap);
 * int value = readOnlyMap.get("one");  // OK
 * readOnlyMap.put("two", 2);  // throws UnsupportedOperationException
 * }</pre>
 *
 * @param <K> the type of keys in this map
 * @param <V> the type of values in this map
 * @since 1.0
 */
public class UnmodifiableMap<K, V> implements Map<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    protected final Map<K, V> delegate;

    /**
     * Constructs an unmodifiable wrapper for the specified map.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Map<String, Integer> original = new HashMap<>();
     * UnmodifiableMap<String, Integer> unmodifiable =
     *     new UnmodifiableMap<>(original);
     * }</pre>
     *
     * @param delegate the map to wrap
     * @throws NullPointerException if delegate is null
     */
    public UnmodifiableMap(Map<K, V> delegate)
    {
        if (delegate == null)
        {
            throw new NullPointerException();
        }
        this.delegate = delegate;
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size()
    {
        return this.delegate.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     *
     * @param key the key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     */
    @Override
    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the specified value.
     *
     * @param value the value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     */
    @Override
    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if there is no mapping
     */
    @Override
    public V get(Object key)
    {
        return this.delegate.get(key);
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this map is unmodifiable.
     *
     * @param key ignored
     * @param value ignored
     * @return never returns
     * @throws UnsupportedOperationException always, since this map is unmodifiable
     */
    @Override
    public V put(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call put() on " + this.getClass().getSimpleName());
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this map is unmodifiable.
     *
     * @param key ignored
     * @return never returns
     * @throws UnsupportedOperationException always, since this map is unmodifiable
     */
    @Override
    public V remove(Object key)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this map is unmodifiable.
     *
     * @param t ignored
     * @throws UnsupportedOperationException always, since this map is unmodifiable
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> t)
    {
        throw new UnsupportedOperationException("Cannot call putAll() on " + this.getClass().getSimpleName());
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this map is unmodifiable.
     *
     * @throws UnsupportedOperationException always, since this map is unmodifiable
     */
    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this map is unmodifiable.
     *
     * @param key ignored
     * @param remappingFunction ignored
     * @return never returns
     * @throws UnsupportedOperationException always, since this map is unmodifiable
     */
    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call compute() on " + this.getClass().getSimpleName());
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this map is unmodifiable.
     *
     * @param key ignored
     * @param mappingFunction ignored
     * @return never returns
     * @throws UnsupportedOperationException always, since this map is unmodifiable
     */
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call computeIfAbsent() on " + this.getClass().getSimpleName());
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this map is unmodifiable.
     *
     * @param key ignored
     * @param remappingFunction ignored
     * @return never returns
     * @throws UnsupportedOperationException always, since this map is unmodifiable
     */
    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call computeIfPresent() on " + this.getClass().getSimpleName());
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this map is unmodifiable.
     *
     * @param function ignored
     * @throws UnsupportedOperationException always, since this map is unmodifiable
     */
    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function)
    {
        throw new UnsupportedOperationException("Cannot call replaceAll() on " + this.getClass().getSimpleName());
    }

    /**
     * Returns an unmodifiable set view of the keys contained in this map.
     *
     * @return an unmodifiable set view of the keys contained in this map
     */
    @Override
    public Set<K> keySet()
    {
        return Collections.unmodifiableSet(this.delegate.keySet());
    }

    /**
     * Returns an unmodifiable set view of the mappings contained in this map.
     *
     * @return an unmodifiable set view of the mappings contained in this map
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return Collections.unmodifiableMap(this.delegate).entrySet();
    }

    /**
     * Returns an unmodifiable collection view of the values contained in this map.
     *
     * @return an unmodifiable collection view of the values contained in this map
     */
    @Override
    public Collection<V> values()
    {
        return Collections.unmodifiableCollection(this.delegate.values());
    }

    /**
     * Compares the specified object with this map for equality.
     * <p>
     * Returns {@code true} if the specified object is equal to this map, as defined by
     * the underlying map's equals method.
     * </p>
     *
     * @param o the object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
    @Override
    public boolean equals(Object o)
    {
        return o == this || this.delegate.equals(o);
    }

    /**
     * Returns the hash code value for this map.
     * <p>
     * The hash code is delegated to the underlying map.
     * </p>
     *
     * @return the hash code value for this map
     */
    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    /**
     * Returns a string representation of this map.
     * <p>
     * The string representation is delegated to the underlying map.
     * </p>
     *
     * @return a string representation of this map
     */
    @Override
    public String toString()
    {
        return this.delegate.toString();
    }
}
