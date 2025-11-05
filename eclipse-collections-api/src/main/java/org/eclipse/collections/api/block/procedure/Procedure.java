/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.block.procedure;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * A Procedure is a single-argument lambda which has no return value. It represents an operation
 * that accepts a single input argument and returns no result, typically performing side effects.
 * <p>
 * This is a functional interface whose functional method is {@link #value(Object)}.
 * It extends {@link Consumer} and adds serialization support.
 * <p>
 * Procedures are commonly used with methods like {@code each()}, {@code forEach()}, and {@code tap()}.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Print each element
 * Procedure<String> print = System.out::println;
 * names.forEach(print);
 *
 * // Add to another collection
 * MutableList<String> target = Lists.mutable.empty();
 * Procedure<String> addToTarget = target::add;
 * source.forEach(addToTarget);
 *
 * // Side effect operations
 * Procedure<Person> sendEmail = person -> emailService.send(person.getEmail());
 * customers.forEach(sendEmail);
 *
 * // Modify state
 * AtomicInteger counter = new AtomicInteger(0);
 * Procedure<String> countAndPrint = each -> {
 *     System.out.println(counter.incrementAndGet() + ": " + each);
 * };
 * items.forEach(countAndPrint);
 *
 * // Use with tap for debugging
 * MutableList<String> result = names
 *     .select(name -> name.length() > 3)
 *     .tap(System.out::println)  // Print intermediate results
 *     .collect(String::toUpperCase);
 * }</pre>
 *
 * @param <T> the type of the input to the procedure
 * @see Consumer
 */
@FunctionalInterface
public interface Procedure<T>
        extends Consumer<T>, Serializable
{
    /**
     * Performs this operation on the given argument.
     *
     * @param each the input argument
     */
    void value(T each);

    /**
     * Performs this operation on the given argument. This method delegates to {@link #value(Object)}
     * to provide compatibility with {@link Consumer}.
     *
     * @param each the input argument
     */
    @Override
    default void accept(T each)
    {
        this.value(each);
    }
}
