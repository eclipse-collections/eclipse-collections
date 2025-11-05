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
import java.util.Objects;

/**
 * A Triple is a container that holds three related objects.
 * <p>
 * An instance of this interface can be created by calling {@code Tuples.triple(Object, Object, Object)}
 * or {@code Tuples.triplet(Object, Object, Object)}.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create a triple
 * Triple<String, Integer, Boolean> triple = Tuples.triple("Alice", 30, true);
 * String name = triple.getOne();        // "Alice"
 * Integer age = triple.getTwo();        // 30
 * Boolean active = triple.getThree();   // true
 *
 * // Reverse the triple
 * Triple<Boolean, Integer, String> reversed = triple.reverse();
 * // reversed: (true, 30, "Alice")
 *
 * // Check if all elements are equal
 * Triple<String, String, String> triplet = Tuples.triple("same", "same", "same");
 * boolean allEqual = triplet.isEqual();  // true
 * }</pre>
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 */
public interface Triple<T1, T2, T3>
        extends Serializable, Comparable<Triple<T1, T2, T3>>
{
    /**
     * Returns the first element of this triple.
     *
     * @return the first element
     */
    T1 getOne();

    /**
     * Returns the second element of this triple.
     *
     * @return the second element
     */
    T2 getTwo();

    /**
     * Returns the third element of this triple.
     *
     * @return the third element
     */
    T3 getThree();

    /**
     * Returns a new triple with the elements in reversed order. The first element becomes the third,
     * the second element stays in the middle, and the third element becomes the first.
     *
     * @return a new triple with elements in reversed order
     */
    Triple<T3, T2, T1> reverse();

    /**
     * Returns true if the value of {@link #getOne()} is equal to both the value of {@link #getTwo()}
     * and the value of {@link #getThree()}, using {@link Objects#equals(Object, Object)} for comparison.
     *
     * @return true if all three elements are equal, false otherwise
     * @since 11.0
     */
    default boolean isEqual()
    {
        return Objects.equals(this.getOne(), this.getTwo()) && Objects.equals(this.getOne(), this.getThree());
    }

    /**
     * Returns true if the value of {@link #getOne()} is the same instance as both the value of
     * {@link #getTwo()} and the value of {@link #getThree()}, using reference equality (==).
     *
     * @return true if all three elements refer to the same instance, false otherwise
     * @since 11.0
     */
    default boolean isSame()
    {
        return this.getOne() == this.getTwo() && this.getOne() == this.getThree();
    }
}
