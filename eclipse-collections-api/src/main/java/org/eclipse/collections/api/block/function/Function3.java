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
 * Function3 is a three-argument lambda which takes three arguments and returns a result of a transformation.
 * <p>
 * This is a functional interface whose functional method is {@link #value(Object, Object, Object)}.
 * <p>
 * A Function3 is commonly used by {@code injectIntoWith()} methods where three parameters are needed:
 * an accumulator, the current element, and an additional parameter.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Calculate weighted sum with injectIntoWith
 * Function3<Double, Integer, Double, Double> weightedSum =
 *     (sum, value, weight) -> sum + (value * weight);
 * Double result = numbers.injectIntoWith(0.0, weightedSum, 1.5);
 *
 * // String formatting with context
 * Function3<String, Person, String, String> formatPerson =
 *     (acc, person, prefix) -> acc + prefix + person.getName() + "\n";
 * String formatted = people.injectIntoWith("", formatPerson, "-> ");
 *
 * // Complex accumulation with parameter
 * Function3<MutableList<String>, Person, Integer, MutableList<String>> collectIfAgeOver =
 *     (list, person, minAge) -> {
 *         if (person.getAge() > minAge) {
 *             list.add(person.getName());
 *         }
 *         return list;
 *     };
 * MutableList<String> adults =
 *     people.injectIntoWith(Lists.mutable.empty(), collectIfAgeOver, 18);
 * }</pre>
 *
 * @param <T1> the type of the first argument to the function
 * @param <T2> the type of the second argument to the function
 * @param <T3> the type of the third argument to the function
 * @param <R> the type of the result of the function
 * @see org.eclipse.collections.api.collection.MutableCollection#injectIntoWith
 */
@FunctionalInterface
public interface Function3<T1, T2, T3, R>
        extends Serializable
{
    /**
     * Applies this function to the given arguments and returns the result.
     *
     * @param argument1 the first argument
     * @param argument2 the second argument
     * @param argument3 the third argument
     * @return the function result
     */
    R value(T1 argument1, T2 argument2, T3 argument3);
}
