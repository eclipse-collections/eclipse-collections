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
import java.util.function.BiFunction;

/**
 * Function2 is a two-argument lambda which takes two arguments and returns a result of a transformation.
 * <p>
 * This is a functional interface whose functional method is {@link #value(Object, Object)}.
 * It extends {@link BiFunction} and adds serialization support.
 * <p>
 * A Function2 is commonly used by methods like {@code injectInto()}, {@code collectWith()}, and
 * {@code aggregateBy()}. The first argument is typically an accumulator or element from a collection,
 * while the second argument is a parameter passed to the method.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Sum using injectInto
 * Function2<Integer, Integer, Integer> sumFunction = (sum, each) -> sum + each;
 * Integer total = numbers.injectInto(0, sumFunction);
 *
 * // Collect with a parameter
 * Function2<Person, String, String> appendTitle = (person, title) ->
 *     title + " " + person.getName();
 * MutableList<String> formalNames =
 *     people.collectWith(appendTitle, "Dr.");
 *
 * // Build a string concatenation
 * Function2<String, String, String> concat = (acc, each) -> acc + ", " + each;
 * String result = words.injectInto("Start", concat);
 *
 * // Complex aggregation
 * Function2<Integer, Person, Integer> sumAges = (sum, person) ->
 *     sum + person.getAge();
 * Integer totalAge = people.injectInto(0, sumAges);
 * }</pre>
 *
 * @param <T1> the type of the first argument to the function
 * @param <T2> the type of the second argument to the function
 * @param <R> the type of the result of the function
 * @since 1.0
 * @see org.eclipse.collections.api.RichIterable#injectInto
 * @see org.eclipse.collections.api.RichIterable#collectWith
 * @see BiFunction
 */
@FunctionalInterface
public interface Function2<T1, T2, R>
        extends BiFunction<T1, T2, R>, Serializable
{
    /**
     * Applies this function to the given arguments and returns the result.
     *
     * @param argument1 the first argument
     * @param argument2 the second argument
     * @return the function result
     */
    R value(T1 argument1, T2 argument2);

    /**
     * Applies this function to the given arguments. This method delegates to {@link #value(Object, Object)}
     * to provide compatibility with {@link BiFunction}.
     *
     * @param argument1 the first argument
     * @param argument2 the second argument
     * @return the function result
     */
    @Override
    default R apply(T1 argument1, T2 argument2)
    {
        return this.value(argument1, argument2);
    }
}
