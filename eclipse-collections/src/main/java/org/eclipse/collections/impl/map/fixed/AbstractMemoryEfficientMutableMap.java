/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.fixed;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.map.mutable.AbstractMutableMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

/**
 * AbstractMemoryEfficientMutableMap is a skeletal implementation of fixed-size maps that are memory-efficient
 * for small numbers of entries.
 * <p>
 * This abstract class provides default implementations for mutating operations that throw
 * {@link UnsupportedOperationException}, as fixed-size maps cannot change their size. The only supported
 * "mutation" is {@link #withKeyValue(Object, Object)} which returns a new map instance.
 * <p>
 * <b>Key Characteristics:</b>
 * <ul>
 *   <li>Fixed size - cannot add or remove entries through standard mutation methods</li>
 *   <li>Memory efficient - optimized storage for small maps (0-3 entries)</li>
 *   <li>Copy-on-write semantics - withKeyValue/withoutKey return new instances</li>
 *   <li>Null-safe - supports null keys and values</li>
 * </ul>
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
abstract class AbstractMemoryEfficientMutableMap<K, V>
        extends AbstractMutableMap<K, V>
        implements FixedSizeMap<K, V>
{
    /**
     * This operation is not supported on fixed-size maps.
     * Use {@link #withKeyValue(Object, Object)} instead to get a new map with the key-value pair.
     *
     * @throws UnsupportedOperationException always, as this map cannot change size
     */
    @Override
    public V put(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call put() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on fixed-size maps.
     * Use {@link #withoutKey(Object)} instead to get a new map without the key.
     *
     * @throws UnsupportedOperationException always, as this map cannot change size
     */
    @Override
    public V remove(Object key)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on fixed-size maps.
     *
     * @throws UnsupportedOperationException always, as this map cannot change size
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call putAll() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on fixed-size maps.
     *
     * @throws UnsupportedOperationException always, as this map cannot change size
     */
    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on fixed-size maps.
     * Use {@link #withoutKey(Object)} instead to get a new map without the key.
     *
     * @throws UnsupportedOperationException always, as this map cannot change size
     */
    @Override
    public V removeKey(K key)
    {
        throw new UnsupportedOperationException("Cannot call removeKey() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on fixed-size maps.
     *
     * @throws UnsupportedOperationException always, as this map cannot change size
     */
    @Override
    public boolean removeAllKeys(Set<? extends K> keys)
    {
        throw new UnsupportedOperationException("Cannot call removeAllKeys() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on fixed-size maps.
     * Use {@link #reject(Predicate2)} instead to get a new map without matching entries.
     *
     * @throws UnsupportedOperationException always, as this map cannot change size
     */
    @Override
    public boolean removeIf(Predicate2<? super K, ? super V> predicate)
    {
        throw new UnsupportedOperationException("Cannot call removeIf() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on fixed-size maps.
     *
     * @throws UnsupportedOperationException always, as this map cannot change size
     */
    @Override
    public <E> MutableMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> valueFunction)
    {
        throw new UnsupportedOperationException("Cannot call collectKeysAndValues() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not yet supported on fixed-size maps.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".updateValue() not implemented yet");
    }

    /**
     * This operation is not yet supported on fixed-size maps.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public <P> V updateValueWith(K key, Function0<? extends V> factory, Function2<? super V, ? super P, ? extends V> function, P parameter)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".updateValueWith() not implemented yet");
    }

    /**
     * This operation is not yet supported on fixed-size maps.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".merge() not implemented yet");
    }

    /**
     * Returns a new empty mutable map. The returned map is not fixed-size.
     *
     * @return a new empty mutable map
     */
    @Override
    public MutableMap<K, V> newEmpty()
    {
        return Maps.mutable.empty();
    }

    /**
     * Returns a new empty mutable map with the specified initial capacity.
     * The returned map is not fixed-size.
     *
     * @param capacity the initial capacity
     * @return a new empty mutable map with the specified capacity
     */
    @Override
    public MutableMap<K, V> newEmpty(int capacity)
    {
        return UnifiedMap.newMap(capacity);
    }

    /**
     * Returns a new map containing all the key-value pairs from this map and the specified key-value pairs.
     * If any keys are duplicates, the last value wins.
     * The returned map may not be fixed-size if it would exceed the fixed-size threshold.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeMap<String, Integer> map = Maps.fixedSize.of("a", 1);
     * MutableMap<String, Integer> result = map.withAllKeyValues(Lists.fixedSize.of(
     *     Tuples.pair("b", 2),
     *     Tuples.pair("c", 3)
     * ));
     * }</pre>
     *
     * @param keyValues the key-value pairs to add
     * @return a new map containing all entries
     */
    @Override
    public MutableMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        MutableMap<K, V> map = this;
        for (Pair<? extends K, ? extends V> keyVal : keyValues)
        {
            map = map.withKeyValue(keyVal.getOne(), keyVal.getTwo());
        }
        return map;
    }

    /**
     * Returns a new map containing all the key-value pairs from this map and the specified key-value pairs.
     * If any keys are duplicates, the last value wins.
     * The returned map may not be fixed-size if it would exceed the fixed-size threshold.
     *
     * @param keyValues the key-value pairs to add
     * @return a new map containing all entries
     */
    @Override
    public MutableMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValues)
    {
        return this.withAllKeyValues(ArrayAdapter.adapt(keyValues));
    }

    /**
     * Returns a new map containing all entries from this map except those with the specified keys.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeMap<String, Integer> map = Maps.fixedSize.of("a", 1, "b", 2, "c", 3);
     * MutableMap<String, Integer> result = map.withoutAllKeys(Lists.fixedSize.of("a", "c"));
     * // result is {"b" -> 2}
     * }</pre>
     *
     * @param keys the keys to remove
     * @return a new map without the specified keys
     */
    @Override
    public MutableMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        MutableMap<K, V> map = this;
        for (K key : keys)
        {
            map = map.withoutKey(key);
        }
        return map;
    }

    /**
     * Executes the procedure for each value in this map and returns this map.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeMap<String, Integer> map = Maps.fixedSize.of("a", 1, "b", 2);
     * map.tap(System.out::println); // prints 1 and 2, returns the same map
     * }</pre>
     *
     * @param procedure the procedure to execute for each value
     * @return this map
     */
    @Override
    public FixedSizeMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public abstract FixedSizeMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    public abstract <R> FixedSizeMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    public abstract <R> FixedSizeMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    @Override
    public abstract <K2, V2> FixedSizeMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    public abstract FixedSizeMap<K, V> reject(Predicate2<? super K, ? super V> predicate);
}
