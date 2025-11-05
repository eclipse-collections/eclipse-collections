/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.AbstractSynchronizedRichIterable;
import org.eclipse.collections.impl.tuple.AbstractImmutableEntry;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * AbstractSynchronizedMapIterable provides a skeletal implementation of a thread-safe view of a {@link MutableMapIterable}.
 * <p>
 * All operations are synchronized using an internal lock to ensure thread-safety. This class wraps a delegate
 * map and synchronizes all access to it. The lock can be provided explicitly or will be defaulted to the instance itself.
 * <p>
 * <b>Thread-Safety:</b> All methods in this class are thread-safe and provide happens-before guarantees
 * through proper synchronization. However, compound operations may still require external synchronization.
 * <p>
 * <b>Performance:</b> All operations acquire a lock, which may become a contention point under high concurrency.
 * Consider using concurrent collections for better scalability in highly concurrent scenarios.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public abstract class AbstractSynchronizedMapIterable<K, V>
        extends AbstractSynchronizedRichIterable<V>
        implements MutableMapIterable<K, V>
{
    /**
     * Creates a new synchronized view of the specified map using the instance as the lock.
     *
     * @param delegate the map to wrap with synchronized access
     */
    protected AbstractSynchronizedMapIterable(MutableMapIterable<K, V> delegate)
    {
        super(delegate, null);
    }

    /**
     * Creates a new synchronized view of the specified map using the provided lock object.
     *
     * @param delegate the map to wrap with synchronized access
     * @param lock the object to use for synchronization
     */
    protected AbstractSynchronizedMapIterable(MutableMapIterable<K, V> delegate, Object lock)
    {
        super(delegate, lock);
    }

    /**
     * Returns the underlying delegate map.
     * <p>
     * <b>Warning:</b> Direct access to the delegate bypasses synchronization.
     * This method should only be called while holding the lock.
     *
     * @return the underlying map
     */
    @Override
    protected MutableMapIterable<K, V> getDelegate()
    {
        return (MutableMapIterable<K, V>) super.getDelegate();
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     * <p>
     * This method is thread-safe.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the key, or null if not present
     */
    @Override
    public V get(Object key)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().get(key);
        }
    }

    /**
     * Returns the value associated with the key if present, otherwise returns the result of evaluating the function.
     * <p>
     * This method is thread-safe. The function is only evaluated if the key is absent.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, String> map = Maps.mutable.of("a", "apple").asSynchronized();
     * String value = map.getIfAbsent("b", () -> "banana"); // "banana"
     * }</pre>
     *
     * @param key the key whose associated value is to be returned
     * @param function the function to evaluate if the key is absent
     * @return the value associated with the key, or the function result
     */
    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsent(key, function);
        }
    }

    /**
     * Returns the value associated with the key if present, otherwise returns the specified default value.
     * <p>
     * This method is thread-safe.
     *
     * @param key the key whose associated value is to be returned
     * @param value the value to return if the key is absent
     * @return the value associated with the key, or the specified value
     */
    @Override
    public V getIfAbsentValue(K key, V value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentValue(key, value);
        }
    }

    /**
     * Returns the value associated with the key if present, otherwise returns the result of applying the function to the parameter.
     * <p>
     * This method is thread-safe. The function is only evaluated if the key is absent.
     *
     * @param <P> the type of the parameter
     * @param key the key whose associated value is to be returned
     * @param function the function to apply to the parameter if the key is absent
     * @param parameter the parameter to pass to the function
     * @return the value associated with the key, or the function result
     */
    @Override
    public <P> V getIfAbsentWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentWith(key, function, parameter);
        }
    }

    /**
     * If the specified key is present in this map, applies the function to its value and returns the result.
     * Returns null if the key is not present.
     * <p>
     * This method is thread-safe.
     *
     * @param <A> the type of the result
     * @param key the key whose associated value is to be transformed
     * @param function the function to apply to the value if present
     * @return the result of applying the function, or null if the key is not present
     */
    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().ifPresentApply(key, function);
        }
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     * <p>
     * This method is thread-safe.
     *
     * @param key the key to test
     * @return true if this map contains a mapping for the key
     */
    @Override
    public boolean containsKey(Object key)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().containsKey(key);
        }
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     * <p>
     * This method is thread-safe.
     *
     * @param value the value to test
     * @return true if this map contains one or more mappings to the value
     */
    @Override
    public boolean containsValue(Object value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().containsValue(value);
        }
    }

    /**
     * Executes the procedure for each value in the map.
     * <p>
     * This method is thread-safe. The lock is held for the duration of the iteration.
     *
     * @param procedure the procedure to execute for each value
     */
    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().forEachValue(procedure);
        }
    }

    /**
     * Executes the procedure for each key in the map.
     * <p>
     * This method is thread-safe. The lock is held for the duration of the iteration.
     *
     * @param procedure the procedure to execute for each key
     */
    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().forEachKey(procedure);
        }
    }

    /**
     * Executes the procedure for each key-value pair in the map.
     * <p>
     * This method is thread-safe. The lock is held for the duration of the iteration.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.of("a", 1, "b", 2).asSynchronized();
     * map.forEachKeyValue((k, v) -> System.out.println(k + "=" + v));
     * }</pre>
     *
     * @param procedure2 the procedure to execute for each key-value pair
     */
    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure2)
    {
        synchronized (this.lock)
        {
            this.getDelegate().forEachKeyValue(procedure2);
        }
    }

    /**
     * Returns the first key-value pair that satisfies the predicate, or null if none is found.
     * <p>
     * This method is thread-safe.
     *
     * @param predicate the predicate to test key-value pairs against
     * @return the first matching key-value pair, or null if none found
     */
    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().detect(predicate);
        }
    }

    /**
     * Returns an Optional containing the first key-value pair that satisfies the predicate, or an empty Optional if none is found.
     * <p>
     * This method is thread-safe.
     *
     * @param predicate the predicate to test key-value pairs against
     * @return an Optional containing the first matching key-value pair, or empty
     */
    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().detectOptional(predicate);
        }
    }

    /**
     * Get and return the value if present, otherwise compute and put a new value, then return it.
     * <p>
     * This method is thread-safe and atomic. The function is only evaluated if the key is absent.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, List<String>> map = Maps.mutable.empty().asSynchronized();
     * List<String> list = map.getIfAbsentPut("key", Lists.mutable::empty);
     * }</pre>
     *
     * @param key the key to look up or create
     * @param function the function to evaluate if the key is absent
     * @return the value associated with the key (existing or newly created)
     */
    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentPut(key, function);
        }
    }

    /**
     * Get and return the value if present, otherwise put the specified value, then return it.
     * <p>
     * This method is thread-safe and atomic.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.empty().asSynchronized();
     * int count = map.getIfAbsentPut("key", 0); // 0
     * }</pre>
     *
     * @param key the key to look up or create
     * @param value the value to put if the key is absent
     * @return the value associated with the key (existing or newly put)
     */
    @Override
    public V getIfAbsentPut(K key, V value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentPut(key, value);
        }
    }

    /**
     * Get and return the value if present, otherwise compute a new value from the key, put it, then return it.
     * <p>
     * This method is thread-safe and atomic. The function is only evaluated if the key is absent.
     *
     * @param key the key to look up or create
     * @param function the function to apply to the key if absent
     * @return the value associated with the key (existing or newly created)
     */
    @Override
    public V getIfAbsentPutWithKey(K key, Function<? super K, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentPutWithKey(key, function);
        }
    }

    /**
     * Get and return the value if present, otherwise compute a new value from the parameter, put it, then return it.
     * <p>
     * This method is thread-safe and atomic. The function is only evaluated if the key is absent.
     *
     * @param <P> the type of the parameter
     * @param key the key to look up or create
     * @param function the function to apply to the parameter if the key is absent
     * @param parameter the parameter to pass to the function
     * @return the value associated with the key (existing or newly created)
     */
    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getIfAbsentPutWith(key, function, parameter);
        }
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     * <p>
     * This method is thread-safe and atomic.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.empty().asSynchronized();
     * Integer previous = map.put("a", 1); // null
     * Integer old = map.put("a", 2);      // 1
     * }</pre>
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or null if there was no mapping
     */
    @Override
    public V put(K key, V value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().put(key, value);
        }
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * <p>
     * This method is thread-safe and atomic.
     *
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with the key, or null if there was no mapping
     */
    @Override
    public V remove(Object key)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().remove(key);
        }
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * <p>
     * This method is thread-safe and atomic. This is an alias for {@link #remove(Object)}.
     *
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with the key, or null if there was no mapping
     */
    @Override
    public V removeKey(K key)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().removeKey(key);
        }
    }

    /**
     * Removes all of the specified keys from this map if they are present.
     * <p>
     * This method is thread-safe and atomic.
     *
     * @param keys the keys whose mappings are to be removed from the map
     * @return true if any keys were removed
     */
    @Override
    public boolean removeAllKeys(Set<? extends K> keys)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().removeAllKeys(keys);
        }
    }

    /**
     * Removes all key-value pairs that satisfy the predicate.
     * <p>
     * This method is thread-safe and atomic.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.of("a", 1, "b", 2, "c", 3).asSynchronized();
     * boolean removed = map.removeIf((k, v) -> v % 2 == 0); // true, removes "b"
     * }</pre>
     *
     * @param predicate the predicate to test key-value pairs against
     * @return true if any entries were removed
     */
    @Override
    public boolean removeIf(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().removeIf(predicate);
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * <p>
     * This method is thread-safe and atomic.
     *
     * @param map the map whose mappings are to be added to this map
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        synchronized (this.lock)
        {
            this.getDelegate().putAll(map);
        }
    }

    /**
     * Removes all of the mappings from this map.
     * <p>
     * This method is thread-safe and atomic.
     */
    @Override
    public void clear()
    {
        synchronized (this.lock)
        {
            this.getDelegate().clear();
        }
    }

    /**
     * Associates the key-value pair in this map. This is equivalent to {@code put(pair.getOne(), pair.getTwo())}.
     * <p>
     * This method is thread-safe and atomic.
     *
     * @param keyValuePair the key-value pair to add
     * @return the previous value associated with the key, or null if there was no mapping
     */
    @Override
    public V putPair(Pair<? extends K, ? extends V> keyValuePair)
    {
        synchronized (this.lock)
        {
            return this.put(keyValuePair.getOne(), keyValuePair.getTwo());
        }
    }

    /**
     * Adds the key-value pair to this map. This is an alias for {@link #putPair(Pair)}.
     * <p>
     * This method is thread-safe and atomic.
     *
     * @param keyValuePair the key-value pair to add
     * @return the previous value associated with the key, or null if there was no mapping
     */
    @Override
    public V add(Pair<? extends K, ? extends V> keyValuePair)
    {
        synchronized (this.lock)
        {
            return this.putPair(keyValuePair);
        }
    }

    /**
     * Updates the value at a key by applying a function to the existing value, or initializes it using the factory if absent.
     * <p>
     * This method is thread-safe and atomic.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.of("a", 1).asSynchronized();
     * int newValue = map.updateValue("a", () -> 0, v -> v + 1); // 2
     * }</pre>
     *
     * @param key the key to update
     * @param factory the factory to create an initial value if the key is absent
     * @param function the function to apply to the existing value
     * @return the new value associated with the key
     */
    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().updateValue(key, factory, function);
        }
    }

    /**
     * Updates the value at a key by applying a function to the existing value and a parameter, or initializes it using the factory if absent.
     * <p>
     * This method is thread-safe and atomic.
     *
     * @param <P> the type of the parameter
     * @param key the key to update
     * @param factory the factory to create an initial value if the key is absent
     * @param function the function to apply to the existing value and parameter
     * @param parameter the parameter to pass to the function
     * @return the new value associated with the key
     */
    @Override
    public <P> V updateValueWith(
            K key,
            Function0<? extends V> factory,
            Function2<? super V, ? super P, ? extends V> function,
            P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().updateValueWith(key, factory, function, parameter);
        }
    }

    /**
     * Merges the specified value with the existing value for the key using the remapping function.
     * If the key is absent, puts the value. If the remapping function returns null, removes the key.
     * <p>
     * This method is thread-safe and atomic.
     *
     * @param key the key with which the resulting value is to be associated
     * @param value the value to be merged with the existing value
     * @param remappingFunction the function to merge the values
     * @return the new value associated with the key, or null if the key was removed
     */
    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().merge(key, value, remappingFunction);
        }
    }

    /**
     * Transforms each value in this map into a new key-value pair and returns a map where each new key is unique.
     * <p>
     * This method is thread-safe.
     *
     * @param <VV> the type of the new keys
     * @param function the function to transform values into keys
     * @return a new map with unique keys derived from values
     * @throws IllegalStateException if the function produces duplicate keys
     */
    @Override
    public <VV> MutableMapIterable<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().groupByUniqueKey(function);
        }
    }

    /**
     * Groups values by a key function and aggregates them in-place using a mutating aggregator.
     * <p>
     * This method is thread-safe.
     *
     * @param <KK> the type of the grouping keys
     * @param <VV> the type of the aggregated values
     * @param groupBy the function to determine grouping keys
     * @param zeroValueFactory the factory to create initial aggregate values
     * @param mutatingAggregator the procedure to aggregate values in-place
     * @return a map from grouping keys to aggregated values
     */
    @Override
    public <KK, VV> MutableMapIterable<KK, VV> aggregateInPlaceBy(Function<? super V, ? extends KK> groupBy, Function0<? extends VV> zeroValueFactory, Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
        }
    }

    /**
     * Groups values by a key function and aggregates them using a non-mutating aggregator.
     * <p>
     * This method is thread-safe.
     *
     * @param <KK> the type of the grouping keys
     * @param <VV> the type of the aggregated values
     * @param groupBy the function to determine grouping keys
     * @param zeroValueFactory the factory to create initial aggregate values
     * @param nonMutatingAggregator the function to aggregate values immutably
     * @return a map from grouping keys to aggregated values
     */
    @Override
    public <KK, VV> MutableMapIterable<KK, VV> aggregateBy(Function<? super V, ? extends KK> groupBy, Function0<? extends VV> zeroValueFactory, Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
        }
    }

    /**
     * Groups key-value pairs by transforming both keys and values, then aggregates the transformed values.
     * <p>
     * This method is thread-safe.
     *
     * @param <K1> the type of the new grouping keys
     * @param <V1> the type of the transformed values
     * @param <V2> the type of the aggregated values
     * @param keyFunction the function to transform keys
     * @param valueFunction the function to transform values
     * @param zeroValueFactory the factory to create initial aggregate values
     * @param nonMutatingAggregator the function to aggregate values immutably
     * @return a map from new keys to aggregated values
     */
    @Override
    public <K1, V1, V2> MutableMapIterable<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().aggregateBy(keyFunction, valueFunction, zeroValueFactory, nonMutatingAggregator);
        }
    }

    /**
     * Groups values by a key function and reduces them using a reduction function.
     * <p>
     * This method is thread-safe.
     *
     * @param <KK> the type of the grouping keys
     * @param groupBy the function to determine grouping keys
     * @param reduceFunction the function to reduce two values into one
     * @return a map from grouping keys to reduced values
     */
    @Override
    public <KK> MutableMapIterable<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reduceBy(groupBy, reduceFunction);
        }
    }

    /**
     * Returns a lazy iterable view of the keys in this map.
     * <p>
     * The returned view reflects the current state of the map but does not synchronize access.
     * External synchronization is required if the map is modified during iteration.
     *
     * @return a lazy iterable of the map's keys
     */
    @Override
    public RichIterable<K> keysView()
    {
        return LazyIterate.adapt(this.keySet());
    }

    /**
     * Returns a lazy iterable view of the values in this map.
     * <p>
     * The returned view reflects the current state of the map but does not synchronize access.
     * External synchronization is required if the map is modified during iteration.
     *
     * @return a lazy iterable of the map's values
     */
    @Override
    public RichIterable<V> valuesView()
    {
        return LazyIterate.adapt(this.values());
    }

    /**
     * Returns a lazy iterable view of the key-value pairs in this map as Pair objects.
     * <p>
     * The returned view reflects the current state of the map but does not synchronize access.
     * External synchronization is required if the map is modified during iteration.
     *
     * @return a lazy iterable of key-value pairs
     */
    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return LazyIterate.adapt(this.entrySet()).collect(AbstractImmutableEntry.getPairFunction());
    }

    /**
     * Groups values and sums their int values for each group.
     * <p>
     * This method is thread-safe.
     *
     * @param <V1> the type of the grouping keys
     * @param groupBy the function to determine grouping keys
     * @param function the function to extract int values from values
     * @return a map from grouping keys to summed long values
     */
    @Override
    public <V1> MutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByInt(groupBy, function);
        }
    }

    /**
     * Groups values and sums their float values for each group.
     * <p>
     * This method is thread-safe.
     *
     * @param <V1> the type of the grouping keys
     * @param groupBy the function to determine grouping keys
     * @param function the function to extract float values from values
     * @return a map from grouping keys to summed double values
     */
    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByFloat(groupBy, function);
        }
    }

    /**
     * Groups values and sums their long values for each group.
     * <p>
     * This method is thread-safe.
     *
     * @param <V1> the type of the grouping keys
     * @param groupBy the function to determine grouping keys
     * @param function the function to extract long values from values
     * @return a map from grouping keys to summed long values
     */
    @Override
    public <V1> MutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByLong(groupBy, function);
        }
    }

    /**
     * Groups values and sums their double values for each group.
     * <p>
     * This method is thread-safe.
     *
     * @param <V1> the type of the grouping keys
     * @param groupBy the function to determine grouping keys
     * @param function the function to extract double values from values
     * @return a map from grouping keys to summed double values
     */
    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().sumByDouble(groupBy, function);
        }
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1> MutableMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function)
    {
        return (MutableMultimap<V1, V>) super.<V1>groupBy(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1> MutableMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return (MutableMultimap<V1, V>) super.groupByEach(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <S> MutableCollection<Pair<V, S>> zip(Iterable<S> that)
    {
        return (MutableCollection<Pair<V, S>>) super.zip(that);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<Pair<V, Integer>> zipWithIndex()
    {
        return (MutableCollection<Pair<V, Integer>>) super.zipWithIndex();
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<V> select(Predicate<? super V> predicate)
    {
        return (MutableCollection<V>) super.select(predicate);
    }

    /**
     * @since 9.0
     */
    @Override
    public <S> MutableCollection<S> selectInstancesOf(Class<S> clazz)
    {
        return (MutableCollection<S>) super.selectInstancesOf(clazz);
    }

    /**
     * @since 9.0
     */
    @Override
    public <P> MutableCollection<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (MutableCollection<V>) super.selectWith(predicate, parameter);
    }

    /**
     * @since 9.0
     */
    @Override
    public <P> MutableCollection<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (MutableCollection<V>) super.rejectWith(predicate, parameter);
    }

    /**
     * @since 9.0
     */
    @Override
    public PartitionMutableCollection<V> partition(Predicate<? super V> predicate)
    {
        return (PartitionMutableCollection<V>) super.partition(predicate);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1> MutableBag<V1> countBy(Function<? super V, ? extends V1> function)
    {
        return (MutableBag<V1>) super.<V1>countBy(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1, P> MutableBag<V1> countByWith(Function2<? super V, ? super P, ? extends V1> function, P parameter)
    {
        return (MutableBag<V1>) super.<V1, P>countByWith(function, parameter);
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V1> MutableBag<V1> countByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return (MutableBag<V1>) super.countByEach(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableCollection<V> reject(Predicate<? super V> predicate)
    {
        return (MutableCollection<V>) super.reject(predicate);
    }

    /**
     * @since 9.0
     */
    @Override
    public MutableMapIterable<K, V> tap(Procedure<? super V> procedure)
    {
        return (MutableMapIterable<K, V>) super.tap(procedure);
    }
}
