/*
 * Copyright (c) 2022 Goldman Sachs.
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

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.impl.AbstractRichIterable;

/**
 * AbstractMapIterable provides a skeletal implementation of the {@link MapIterable} interface
 * to minimize the effort required to implement this interface.
 * <p>
 * This abstract class extends {@link AbstractRichIterable} and delegates many operations
 * to the map's values view, providing a consistent implementation across different map types.
 * <p>
 * Implementations should provide concrete implementations of the abstract methods defined
 * in the parent classes and {@link MapIterable} interface.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public abstract class AbstractMapIterable<K, V> extends AbstractRichIterable<V> implements MapIterable<K, V>
{
    /**
     * Computes a hash code for a key-value pair using XOR combination.
     * Null keys and values are treated as having a hash code of 0.
     *
     * @param key the key
     * @param value the value
     * @return the combined hash code
     */
    protected int keyAndValueHashCode(K key, V value)
    {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    /**
     * Tests if a key-value pair from this map is equal to a corresponding entry in another map.
     * This method handles null values correctly by checking if the key exists in the target map.
     *
     * @param key the key to check
     * @param value the value to check
     * @param map the map to compare against
     * @return true if the key-value pair matches the entry in the other map
     */
    protected boolean keyAndValueEquals(K key, V value, Map<K, V> map)
    {
        if (value == null && !map.containsKey(key))
        {
            return false;
        }

        V oValue = map.get(key);
        return oValue == value || oValue != null && oValue.equals(value);
    }

    /**
     * If the specified key is present in this map, applies the function to its value and returns the result.
     * Returns null if the key is not present.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1, "b", 2);
     * String result = map.ifPresentApply("a", Object::toString); // "1"
     * String absent = map.ifPresentApply("c", Object::toString); // null
     * }</pre>
     *
     * @param <A> the type of the result
     * @param key the key whose associated value is to be transformed
     * @param function the function to apply to the value if present
     * @return the result of applying the function, or null if the key is not present
     */
    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        V result = this.get(key);
        return this.isAbsent(result, key) ? null : function.valueOf(result);
    }

    /**
     * Tests if a value is absent from the map for the given key.
     * This correctly handles the case where the value is null by checking containsKey.
     *
     * @param result the value retrieved from the map (may be null)
     * @param key the key that was queried
     * @return true if the key is not present in the map
     */
    protected boolean isAbsent(V result, K key)
    {
        return result == null && !this.containsKey(key);
    }

    /**
     * Returns the value to which the specified key is mapped, or the default value if this map contains no mapping for the key.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1);
     * int value = map.getOrDefault("a", 0);  // 1
     * int absent = map.getOrDefault("b", 0); // 0
     * }</pre>
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the value to return if the key is not present
     * @return the value associated with the key, or the default value
     */
    @Override
    public V getOrDefault(Object key, V defaultValue)
    {
        return this.getIfAbsentValue((K) key, defaultValue);
    }

    /**
     * Returns the value associated with the key if present, otherwise returns the result of evaluating the function.
     * The function is only evaluated if the key is absent.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, String> map = Maps.mutable.with("a", "apple");
     * String value = map.getIfAbsent("a", () -> "default");  // "apple"
     * String absent = map.getIfAbsent("b", () -> "default"); // "default"
     * }</pre>
     *
     * @param key the key whose associated value is to be returned
     * @param function the function to evaluate if the key is absent
     * @return the value associated with the key, or the function result
     */
    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        V result = this.get(key);
        if (!this.isAbsent(result, key))
        {
            return result;
        }
        return function.value();
    }

    /**
     * Returns the value associated with the key if present, otherwise returns the specified default value.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1);
     * int value = map.getIfAbsentValue("a", 999);  // 1
     * int absent = map.getIfAbsentValue("b", 999); // 999
     * }</pre>
     *
     * @param key the key whose associated value is to be returned
     * @param value the value to return if the key is absent
     * @return the value associated with the key, or the specified value
     */
    @Override
    public V getIfAbsentValue(K key, V value)
    {
        V result = this.get(key);
        if (!this.isAbsent(result, key))
        {
            return result;
        }
        return value;
    }

    /**
     * Returns the value associated with the key if present, otherwise returns the result of applying the function to the parameter.
     * The function is only evaluated if the key is absent.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1);
     * int value = map.getIfAbsentWith("a", x -> x * 10, 5);  // 1
     * int absent = map.getIfAbsentWith("b", x -> x * 10, 5); // 50
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param key the key whose associated value is to be returned
     * @param function the function to apply to the parameter if the key is absent
     * @param parameter the parameter to pass to the function
     * @return the value associated with the key, or the function result
     */
    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            result = function.valueOf(parameter);
        }
        return result;
    }

    /**
     * Returns true if any value in the map satisfies the predicate.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1, "b", 2, "c", 3);
     * boolean hasEven = map.anySatisfy(i -> i % 2 == 0); // true
     * }</pre>
     *
     * @param predicate the predicate to test values against
     * @return true if any value satisfies the predicate
     */
    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return this.valuesView().anySatisfy(predicate);
    }

    /**
     * Returns true if any value in the map satisfies the predicate with the given parameter.
     *
     * @param <P> the type of the parameter
     * @param predicate the predicate to test values against
     * @param parameter the parameter to pass to the predicate
     * @return true if any value satisfies the predicate
     */
    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().anySatisfyWith(predicate, parameter);
    }

    /**
     * Returns true if all values in the map satisfy the predicate.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1, "b", 2, "c", 3);
     * boolean allPositive = map.allSatisfy(i -> i > 0); // true
     * }</pre>
     *
     * @param predicate the predicate to test values against
     * @return true if all values satisfy the predicate
     */
    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return this.valuesView().allSatisfy(predicate);
    }

    /**
     * Returns true if all values in the map satisfy the predicate with the given parameter.
     *
     * @param <P> the type of the parameter
     * @param predicate the predicate to test values against
     * @param parameter the parameter to pass to the predicate
     * @return true if all values satisfy the predicate
     */
    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().allSatisfyWith(predicate, parameter);
    }

    /**
     * Returns true if no values in the map satisfy the predicate.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1, "b", 2, "c", 3);
     * boolean noNegative = map.noneSatisfy(i -> i < 0); // true
     * }</pre>
     *
     * @param predicate the predicate to test values against
     * @return true if no values satisfy the predicate
     */
    @Override
    public boolean noneSatisfy(Predicate<? super V> predicate)
    {
        return this.valuesView().noneSatisfy(predicate);
    }

    /**
     * Returns true if no values in the map satisfy the predicate with the given parameter.
     *
     * @param <P> the type of the parameter
     * @param predicate the predicate to test values against
     * @param parameter the parameter to pass to the predicate
     * @return true if no values satisfy the predicate
     */
    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().noneSatisfyWith(predicate, parameter);
    }

    /**
     * Returns a lazy iterable view of the values in this map.
     * Operations on the returned iterable are deferred until iteration.
     *
     * @return a lazy iterable of the map's values
     */
    @Override
    public LazyIterable<V> asLazy()
    {
        return this.valuesView().asLazy();
    }

    /**
     * Partitions the values into chunks of the specified size.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1, "b", 2, "c", 3, "d", 4);
     * RichIterable<RichIterable<Integer>> chunks = map.chunk(2);
     * // Result: [[1, 2], [3, 4]]
     * }</pre>
     *
     * @param size the desired size of each chunk
     * @return a collection of chunks, each containing at most size elements
     * @throws IllegalArgumentException if size is not positive
     */
    @Override
    public RichIterable<RichIterable<V>> chunk(int size)
    {
        return this.valuesView().chunk(size);
    }

    /**
     * Executes the procedure for each value in the map.
     *
     * @param procedure the procedure to execute for each value
     */
    @Override
    public void each(Procedure<? super V> procedure)
    {
        this.forEachValue(procedure);
    }

    /**
     * Executes the procedure for each value in the map with the given parameter.
     *
     * @param <P> the type of the parameter
     * @param procedure2 the procedure to execute for each value
     * @param parameter the parameter to pass to the procedure
     */
    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure2, P parameter)
    {
        this.valuesView().forEachWith(procedure2, parameter);
    }

    /**
     * Executes the procedure for each value in the map along with its iteration index.
     *
     * @param objectIntProcedure the procedure to execute for each value and its index
     */
    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.valuesView().forEachWithIndex(objectIntProcedure);
    }

    /**
     * Executes the procedure for each key in the map.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1, "b", 2);
     * map.forEachKey(System.out::println); // prints "a" and "b"
     * }</pre>
     *
     * @param procedure the procedure to execute for each key
     */
    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.keysView().forEach(procedure);
    }

    /**
     * Executes the procedure for each value in the map.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1, "b", 2);
     * map.forEachValue(System.out::println); // prints 1 and 2
     * }</pre>
     *
     * @param procedure the procedure to execute for each value
     */
    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.valuesView().forEach(procedure);
    }

    /**
     * Returns true if this map contains the specified value.
     *
     * @param object the value to search for
     * @return true if the map contains the value
     */
    @Override
    public boolean contains(Object object)
    {
        return this.containsValue(object);
    }

    /**
     * Returns the first value that satisfies the predicate, or null if none is found.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableMap<String, Integer> map = Maps.mutable.with("a", 1, "b", 2, "c", 3);
     * Integer even = map.detect(i -> i % 2 == 0); // 2
     * }</pre>
     *
     * @param predicate the predicate to test values against
     * @return the first matching value, or null if none found
     */
    @Override
    public V detect(Predicate<? super V> predicate)
    {
        return this.valuesView().detect(predicate);
    }

    /**
     * Returns the first value that satisfies the predicate with the given parameter, or null if none is found.
     *
     * @param <P> the type of the parameter
     * @param predicate the predicate to test values against
     * @param parameter the parameter to pass to the predicate
     * @return the first matching value, or null if none found
     */
    @Override
    public <P> V detectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().detectWith(predicate, parameter);
    }

    /**
     * Returns an Optional containing the first value that satisfies the predicate, or an empty Optional if none is found.
     *
     * @param predicate the predicate to test values against
     * @return an Optional containing the first matching value, or empty
     */
    @Override
    public Optional<V> detectOptional(Predicate<? super V> predicate)
    {
        return this.valuesView().detectOptional(predicate);
    }

    /**
     * Returns an Optional containing the first value that satisfies the predicate with the given parameter,
     * or an empty Optional if none is found.
     *
     * @param <P> the type of the parameter
     * @param predicate the predicate to test values against
     * @param parameter the parameter to pass to the predicate
     * @return an Optional containing the first matching value, or empty
     */
    @Override
    public <P> Optional<V> detectWithOptional(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.valuesView().detectWithOptional(predicate, parameter);
    }

    /**
     * Returns the first value that satisfies the predicate, or the result of evaluating the function if none is found.
     *
     * @param predicate the predicate to test values against
     * @param function the function to evaluate if no value matches
     * @return the first matching value, or the function result
     */
    @Override
    public V detectIfNone(Predicate<? super V> predicate, Function0<? extends V> function)
    {
        return this.valuesView().detectIfNone(predicate, function);
    }

    /**
     * Returns the first value that satisfies the predicate with the given parameter,
     * or the result of evaluating the function if none is found.
     *
     * @param <P> the type of the parameter
     * @param predicate the predicate to test values against
     * @param parameter the parameter to pass to the predicate
     * @param function the function to evaluate if no value matches
     * @return the first matching value, or the function result
     */
    @Override
    public <P> V detectWithIfNone(Predicate2<? super V, ? super P> predicate, P parameter, Function0<? extends V> function)
    {
        return this.valuesView().detectWithIfNone(predicate, parameter, function);
    }

    /**
     * Returns the first value in the map.
     * The iteration order depends on the map implementation.
     *
     * @return the first value
     * @throws java.util.NoSuchElementException if the map is empty
     */
    @Override
    public V getFirst()
    {
        return this.valuesView().getFirst();
    }

    /**
     * Returns the last value in the map.
     * The iteration order depends on the map implementation.
     *
     * @return the last value
     * @throws java.util.NoSuchElementException if the map is empty
     */
    @Override
    public V getLast()
    {
        return this.valuesView().getLast();
    }

    /**
     * Returns the only value in the map.
     *
     * @return the only value
     * @throws IllegalStateException if the map does not contain exactly one element
     */
    @Override
    public V getOnly()
    {
        return this.valuesView().getOnly();
    }

    /**
     * Returns an array containing all of the values in this map.
     *
     * @return an array containing all values
     */
    @Override
    public Object[] toArray()
    {
        return this.valuesView().toArray();
    }

    /**
     * Returns an array containing all of the values in this map; the runtime type of the returned array
     * is that of the specified array.
     *
     * @param <T> the component type of the array to contain the values
     * @param a the array into which the values are to be stored
     * @return an array containing all values
     */
    @Override
    public <T> T[] toArray(T[] a)
    {
        return this.valuesView().toArray(a);
    }
}
