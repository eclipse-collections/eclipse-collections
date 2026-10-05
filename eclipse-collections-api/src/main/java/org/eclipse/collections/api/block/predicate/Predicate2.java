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
 * A Predicate2 is primarily used in methods like selectWith, detectWith, rejectWith. The first argument
 * is the element of the collection being iterated over, and the second argument is a parameter passed into
 * the predicate from the calling method.
 */
@FunctionalInterface
public interface Predicate2<T1, T2>
        extends BiPredicate<T1, T2>, Serializable
{
    /**
     * Evaluates this predicate against the two given arguments.
     *
     * @param argument1 the first argument to evaluate
     * @param argument2 the second argument to evaluate
     * @return {@code true} if the arguments meet the criteria of this predicate, {@code false} otherwise
     */
    boolean accept(T1 argument1, T2 argument2);

    @Override
    default boolean test(T1 t1, T2 t2)
    {
        return this.accept(t1, t2);
    }
}
