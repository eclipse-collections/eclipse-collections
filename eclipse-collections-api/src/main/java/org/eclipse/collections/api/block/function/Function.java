/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.block.function;

import java.io.Serializable;

/**
 * Function is a one-argument lambda which performs a transformation on the object of type {@code T}
 * passed to the {@link #valueOf(Object)} method. This transformation can return the value of calling
 * a getter, or perform some more elaborate logic to calculate a value of type {@code V}.
 * <p>
 * This is a functional interface whose functional method is {@link #valueOf(Object)}.
 * It extends {@link java.util.function.Function} and adds serialization support.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Extract a property from an object
 * Function<Person, String> getName = Person::getName;
 * MutableList<String> names = people.collect(getName);
 *
 * // Transform values
 * Function<String, Integer> stringLength = String::length;
 * MutableList<Integer> lengths = words.collect(stringLength);
 *
 * // Use with method reference
 * Function<Integer, String> toString = Object::toString;
 *
 * // Custom transformation logic
 * Function<Order, BigDecimal> calculateTotal = order -> {
 *     return order.getItems()
 *         .collect(Item::getPrice)
 *         .reduce(BigDecimal.ZERO, BigDecimal::add);
 * };
 * }</pre>
 *
 * @param <T> the type of the input to the function
 * @param <V> the type of the result of the function
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface Function<T, V>
        extends java.util.function.Function<T, V>, Serializable
{
    /**
     * Applies this function to the given argument and returns the result.
     *
     * @param each the input argument
     * @return the function result
     */
    V valueOf(T each);

    /**
     * Applies this function to the given argument. This method delegates to {@link #valueOf(Object)}
     * to provide compatibility with {@link java.util.function.Function}.
     *
     * @param each the input argument
     * @return the function result
     */
    @Override
    default V apply(T each)
    {
        return this.valueOf(each);
    }
}
