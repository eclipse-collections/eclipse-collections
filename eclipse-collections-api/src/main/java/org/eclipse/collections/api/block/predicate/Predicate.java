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
 * A Predicate is a lambda or closure with a boolean result. The method accept should be implemented to indicate the object
 * passed to the method meets the criteria of this Predicate. A Predicate is also known as a Discriminator or Filter.
 */
@FunctionalInterface
public interface Predicate<T>
        extends java.util.function.Predicate<T>, Serializable
{
    /**
     * Evaluates this predicate against the given object.
     *
     * @param each the object to evaluate
     * @return {@code true} if the object meets the criteria of this predicate, {@code false} otherwise
     */
    boolean accept(T each);

    @Override
    default boolean test(T each)
    {
        return this.accept(each);
    }
}
