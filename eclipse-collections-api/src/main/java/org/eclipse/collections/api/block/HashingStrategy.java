/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.block;

import java.io.Serializable;

/**
 * Interface for supporting user defined hashing strategies in Sets and Maps. This allows clients to
 * define custom equality and hash code computation logic that differs from the object's own equals()
 * and hashCode() methods.
 * <p>
 * Implementations must ensure that the {@link #equals(Object, Object)} and {@link #computeHashCode(Object)}
 * methods are consistent with each other, following the same contract as {@link Object#equals(Object)}
 * and {@link Object#hashCode()}.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Case-insensitive string hashing strategy
 * HashingStrategy<String> caseInsensitive = new HashingStrategy<String>()
 * {
 *     public int computeHashCode(String object)
 *     {
 *         return object.toLowerCase().hashCode();
 *     }
 *
 *     public boolean equals(String object1, String object2)
 *     {
 *         return object1.equalsIgnoreCase(object2);
 *     }
 * };
 *
 * // Use with UnifiedSetWithHashingStrategy
 * UnifiedSetWithHashingStrategy<String> set =
 *     UnifiedSetWithHashingStrategy.newSet(caseInsensitive);
 * set.add("Hello");
 * set.contains("HELLO"); // returns true
 * }</pre>
 */
public interface HashingStrategy<E>
        extends Serializable
{
    /**
     * Computes the hashCode of the object as defined by the user. This method must be consistent
     * with the {@link #equals(Object, Object)} method.
     *
     * @param object the object to compute the hash code for
     * @return the hash code value for the object
     */
    int computeHashCode(E object);

    /**
     * Checks two objects for equality. The equality check can use the objects' own equals() method or
     * a custom method defined by the user. It must be consistent with the {@link #computeHashCode(Object)}
     * method, such that if this method returns true for two objects, they must have the same hash code.
     *
     * @param object1 the first object to compare
     * @param object2 the second object to compare
     * @return true if the objects are considered equal by this strategy, false otherwise
     */
    boolean equals(E object1, E object2);
}
