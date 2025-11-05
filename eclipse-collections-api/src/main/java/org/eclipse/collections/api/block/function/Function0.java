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
import java.util.function.Supplier;

/**
 * Function0 is a zero-argument lambda that returns a result. It can be stored in a variable or passed
 * as a parameter and executed by calling the {@link #value()} method.
 * <p>
 * This is a functional interface whose functional method is {@link #value()}.
 * It extends {@link Supplier} and adds serialization support.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Lazy initialization
 * Function0<ExpensiveObject> supplier = ExpensiveObject::new;
 * ExpensiveObject obj = supplier.value(); // Created only when needed
 *
 * // Factory method
 * Function0<MutableList<String>> listFactory = Lists.mutable::empty;
 * MutableList<String> list1 = listFactory.value();
 * MutableList<String> list2 = listFactory.value(); // Creates new list
 *
 * // Constant supplier
 * Function0<Integer> constantValue = () -> 42;
 *
 * // Use with getIfAbsentPut
 * MutableMap<String, List<String>> cache = Maps.mutable.empty();
 * List<String> value = cache.getIfAbsentPut("key", Lists.mutable::empty);
 * }</pre>
 *
 * @param <R> the type of results supplied by this function
 * @see Supplier
 */
@FunctionalInterface
public interface Function0<R>
        extends Supplier<R>, Serializable
{
    /**
     * Returns a result.
     *
     * @return a result
     */
    R value();

    /**
     * Returns a result. This method delegates to {@link #value()}
     * to provide compatibility with {@link Supplier}.
     *
     * @return a result
     */
    @Override
    default R get()
    {
        return this.value();
    }
}
