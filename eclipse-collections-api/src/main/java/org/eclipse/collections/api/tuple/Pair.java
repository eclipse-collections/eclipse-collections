/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.tuple;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * A Pair is a container that holds two related objects. It is the equivalent of an Association in Smalltalk,
 * or an implementation of {@link Map.Entry} in the JDK.
 * <p>
 * An instance of this interface can be created by calling {@code Tuples.pair(Object, Object)} or
 * {@code Tuples.twin(Object, Object)}.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create a pair
 * Pair<String, Integer> pair = Tuples.pair("Alice", 30);
 * String name = pair.getOne();      // "Alice"
 * Integer age = pair.getTwo();      // 30
 *
 * // Use as map entry
 * MutableMap<String, Integer> map = Maps.mutable.empty();
 * pair.put(map);  // Adds "Alice" -> 30 to the map
 *
 * // Convert to Map.Entry
 * Map.Entry<String, Integer> entry = pair.toEntry();
 *
 * // Swap elements
 * Pair<Integer, String> swapped = pair.swap();  // (30, "Alice")
 *
 * // Check equality
 * Pair<String, String> twin = Tuples.pair("same", "same");
 * boolean equal = twin.isEqual();  // true
 * }</pre>
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 */
public interface Pair<T1, T2>
        extends Serializable, Comparable<Pair<T1, T2>>
{
    /**
     * Returns the first element of this pair.
     *
     * @return the first element
     */
    T1 getOne();

    /**
     * Returns the second element of this pair.
     *
     * @return the second element
     */
    T2 getTwo();

    /**
     * Puts this pair into the specified map using the first element as the key
     * and the second element as the value.
     *
     * @param map the map to add this pair to
     */
    void put(Map<? super T1, ? super T2> map);

    /**
     * Converts this pair to a {@link Map.Entry}.
     *
     * @return a Map.Entry with the first element as the key and the second element as the value
     */
    Map.Entry<T1, T2> toEntry();

    /**
     * Returns a new pair with the elements swapped. The first element becomes the second,
     * and the second element becomes the first.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Pair<String, Integer> pair = Tuples.pair("One", 1);
     * Pair<Integer, String> swappedPair = pair.swap();  // (1, "One")
     * }</pre>
     *
     * @return a new pair with elements in reversed order
     * @since 6.0
     */
    Pair<T2, T1> swap();

    /**
     * Returns true if the value of {@link #getOne()} is equal to the value of {@link #getTwo()},
     * using {@link Objects#equals(Object, Object)} for comparison.
     *
     * @return true if both elements are equal, false otherwise
     * @since 11.0
     */
    default boolean isEqual()
    {
        return Objects.equals(this.getOne(), this.getTwo());
    }

    /**
     * Returns true if the value of {@link #getOne()} is the same instance as the value of
     * {@link #getTwo()}, using reference equality (==).
     *
     * @return true if both elements refer to the same instance, false otherwise
     * @since 11.0
     */
    default boolean isSame()
    {
        return this.getOne() == this.getTwo();
    }
}
