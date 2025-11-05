/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.block.procedure;

import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * A Procedure2 is a two-argument procedure that accepts two input arguments and returns no result,
 * typically performing side effects.
 * <p>
 * This is a functional interface whose functional method is {@link #value(Object, Object)}.
 * It extends {@link BiConsumer} and adds serialization support.
 * <p>
 * Procedure2 is commonly used by {@code forEachWith()} methods and {@code forEachKeyValue()}.
 * In {@code forEachKeyValue()}, the procedure takes the key as the first argument and the value
 * as the second. In {@code forEachWith()}, the procedure takes the element of the collection as
 * the first argument and the specified parameter as the second argument.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Iterate over map entries
 * Procedure2<String, Integer> printEntry = (key, value) ->
 *     System.out.println(key + " = " + value);
 * map.forEachKeyValue(printEntry);
 *
 * // ForEachWith to compare with a parameter
 * Procedure2<Person, Integer> printIfOlder = (person, minAge) -> {
 *     if (person.getAge() > minAge) {
 *         System.out.println(person.getName());
 *     }
 * };
 * people.forEachWith(printIfOlder, 18);
 *
 * // Accumulate into a map
 * MutableMap<String, MutableList<Person>> grouping = Maps.mutable.empty();
 * Procedure2<Person, String> groupBy = (person, attribute) -> {
 *     String key = person.getCity();
 *     grouping.getIfAbsentPut(key, Lists.mutable::empty).add(person);
 * };
 * people.forEachWith(groupBy, "city");
 *
 * // Process pairs
 * Procedure2<String, String> concatenateAndPrint = (first, second) ->
 *     System.out.println(first + " " + second);
 * firstNames.forEachWith(concatenateAndPrint, lastName);
 * }</pre>
 *
 * @param <T1> the type of the first argument to the procedure
 * @param <T2> the type of the second argument to the procedure
 * @see BiConsumer
 */
@FunctionalInterface
public interface Procedure2<T1, T2>
        extends BiConsumer<T1, T2>, Serializable
{
    /**
     * Performs this operation on the given arguments.
     *
     * @param argument1 the first argument
     * @param argument2 the second argument
     */
    void value(T1 argument1, T2 argument2);

    /**
     * Performs this operation on the given arguments. This method delegates to
     * {@link #value(Object, Object)} to provide compatibility with {@link BiConsumer}.
     *
     * @param argument1 the first argument
     * @param argument2 the second argument
     */
    @Override
    default void accept(T1 argument1, T2 argument2)
    {
        this.value(argument1, argument2);
    }
}
