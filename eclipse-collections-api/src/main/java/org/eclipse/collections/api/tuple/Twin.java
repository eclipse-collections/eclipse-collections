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
 * A Twin is a {@link Pair} where both elements have the same type.
 * <p>
 * An instance of this interface can be created by calling {@code Tuples.twin(Object, Object)}.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create a twin with same types
 * Twin<String> twin = Tuples.twin("first", "second");
 * String first = twin.getOne();   // "first"
 * String second = twin.getTwo();  // "second"
 *
 * // Swap elements (return type is also Twin)
 * Twin<String> swapped = twin.swap();  // ("second", "first")
 *
 * // Use with collections
 * MutableList<Twin<Integer>> coordinates = Lists.mutable.with(
 *     Tuples.twin(0, 0),
 *     Tuples.twin(1, 2),
 *     Tuples.twin(3, 4)
 * );
 * }</pre>
 *
 * @param <T> the type of both elements in the twin
 */
public interface Twin<T>
        extends Pair<T, T>
{
    /**
     * Returns a new twin with the elements swapped. This overridden method returns a Twin
     * instead of a Pair to maintain type consistency.
     *
     * @return a new twin with elements in reversed order
     */
    @Override
    Twin<T> swap();
}
