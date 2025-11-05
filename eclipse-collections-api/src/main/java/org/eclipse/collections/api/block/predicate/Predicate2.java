/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.block.predicate;

import java.io.Serializable;
import java.util.function.BiPredicate;

/**
 * A Predicate2 is a two-argument predicate primarily used in methods like {@code selectWith()},
 * {@code detectWith()}, {@code rejectWith()}, and {@code partitionWith()}. The first argument is the
 * element of the collection being iterated over, and the second argument is a parameter passed into
 * the predicate from the calling method.
 * <p>
 * This is a functional interface whose functional method is {@link #accept(Object, Object)}.
 * It extends {@link BiPredicate} and adds serialization support.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Filter with a parameter
 * Predicate2<Person, Integer> olderThan = (person, age) ->
 *     person.getAge() > age;
 * MutableList<Person> adults = people.selectWith(olderThan, 18);
 *
 * // Detect with a parameter
 * Predicate2<String, String> startsWith = (str, prefix) ->
 *     str.startsWith(prefix);
 * String firstHttpUrl = urls.detectWith(startsWith, "http://");
 *
 * // Reject with a parameter
 * Predicate2<Product, Double> priceGreaterThan =
 *     (product, maxPrice) -> product.getPrice() > maxPrice;
 * MutableList<Product> affordable =
 *     products.rejectWith(priceGreaterThan, 100.0);
 *
 * // Partition with a parameter
 * PartitionMutableList<Person> partition =
 *     people.partitionWith((person, city) ->
 *         person.getCity().equals(city), "New York");
 * }</pre>
 *
 * @param <T1> the type of the first argument to the predicate (usually the collection element)
 * @param <T2> the type of the second argument to the predicate (usually the parameter)
 * @see BiPredicate
 */
@FunctionalInterface
public interface Predicate2<T1, T2>
        extends BiPredicate<T1, T2>, Serializable
{
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param argument1 the first argument (usually the collection element)
     * @param argument2 the second argument (usually the parameter)
     * @return true if the input arguments match the predicate, false otherwise
     */
    boolean accept(T1 argument1, T2 argument2);

    /**
     * Evaluates this predicate on the given arguments. This method delegates to
     * {@link #accept(Object, Object)} to provide compatibility with {@link BiPredicate}.
     *
     * @param t1 the first input argument
     * @param t2 the second input argument
     * @return true if the input arguments match the predicate, false otherwise
     */
    @Override
    default boolean test(T1 t1, T2 t2)
    {
        return this.accept(t1, t2);
    }
}
