/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.tuple;

/**
 * A Triplet is a {@link Triple} where all three elements have the same type.
 * <p>
 * An instance of this interface can be created by calling {@code Tuples.triplet(Object, Object, Object)}.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create a triplet with same types
 * Triplet<Integer> triplet = Tuples.triplet(1, 2, 3);
 * Integer first = triplet.getOne();     // 1
 * Integer second = triplet.getTwo();    // 2
 * Integer third = triplet.getThree();   // 3
 *
 * // Reverse elements (return type is also Triplet)
 * Triplet<Integer> reversed = triplet.reverse();  // (3, 2, 1)
 *
 * // Use with collections for 3D coordinates
 * MutableList<Triplet<Double>> points = Lists.mutable.with(
 *     Tuples.triplet(0.0, 0.0, 0.0),
 *     Tuples.triplet(1.5, 2.3, 4.7)
 * );
 * }</pre>
 *
 * @param <T> the type of all three elements in the triplet
 */
public interface Triplet<T> extends Triple<T, T, T>
{
    /**
     * Returns a new triplet with the elements in reversed order. This overridden method returns
     * a Triplet instead of a Triple to maintain type consistency.
     *
     * @return a new triplet with elements in reversed order
     */
    @Override
    Triplet<T> reverse();
}
