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

/**
 * A Predicate is a lambda or closure with a boolean result. The method {@link #accept(Object)} should be
 * implemented to indicate whether the object passed to the method meets the criteria of this Predicate.
 * <p>
 * This is a functional interface whose functional method is {@link #accept(Object)}.
 * It extends {@link java.util.function.Predicate} and adds serialization support.
 * <p>
 * A Predicate is also known as a Discriminator or Filter, and is commonly used with methods like
 * {@code select()}, {@code reject()}, {@code detect()}, and {@code partition()}.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Filter a collection
 * Predicate<Integer> isEven = each -> each % 2 == 0;
 * MutableList<Integer> evenNumbers = numbers.select(isEven);
 *
 * // Use method reference
 * Predicate<String> isEmpty = String::isEmpty;
 * MutableList<String> nonEmptyStrings = strings.reject(isEmpty);
 *
 * // Complex predicate
 * Predicate<Person> isAdult = person -> person.getAge() >= 18;
 * Person firstAdult = people.detect(isAdult);
 *
 * // Combine predicates
 * Predicate<Person> isEmployee = person -> person.isEmployee();
 * Predicate<Person> isAdultEmployee = isAdult.and(isEmployee);
 *
 * // Partition collection
 * PartitionMutableList<Integer> partition =
 *     numbers.partition(each -> each > 0);
 * MutableList<Integer> positive = partition.getSelected();
 * MutableList<Integer> nonPositive = partition.getRejected();
 * }</pre>
 *
 * @param <T> the type of the input to the predicate
 * @see java.util.function.Predicate
 */
@FunctionalInterface
public interface Predicate<T>
        extends java.util.function.Predicate<T>, Serializable
{
    /**
     * Evaluates this predicate on the given argument.
     *
     * @param each the input argument
     * @return true if the input argument matches the predicate, false otherwise
     */
    boolean accept(T each);

    /**
     * Evaluates this predicate on the given argument. This method delegates to {@link #accept(Object)}
     * to provide compatibility with {@link java.util.function.Predicate}.
     *
     * @param each the input argument
     * @return true if the input argument matches the predicate, false otherwise
     */
    @Override
    default boolean test(T each)
    {
        return this.accept(each);
    }
}
