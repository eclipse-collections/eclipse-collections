/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.list;

import java.util.Comparator;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.collection.FixedSizeCollection;

/**
 * A FixedSizeList is a list that may be mutated, but cannot grow or shrink in size. The typical
 * mutation allowed for a FixedSizeList implementation is a working implementation for set(int, T).
 * This will allow the FixedSizeList to be sorted. Any operations that would change the size will
 * throw an {@link UnsupportedOperationException}.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * FixedSizeList<String> list = Lists.fixedSize.of("A", "B", "C");
 *
 * // Allowed: replacing elements at specific indices
 * list.set(0, "X");  // List is now ["X", "B", "C"]
 *
 * // Allowed: sorting the list
 * list.sortThis();   // Elements are reordered but size remains same
 *
 * // Creating new instances with modified elements (size can change)
 * MutableList<String> withNew = list.with("D");     // Returns new list
 * MutableList<String> withoutOld = list.without("X"); // Returns new list
 *
 * // Not allowed: operations that would change size throw UnsupportedOperationException
 * // list.add("D");      // Throws UnsupportedOperationException
 * // list.remove("A");   // Throws UnsupportedOperationException
 * }</pre>
 */
public interface FixedSizeList<T>
        extends MutableList<T>, FixedSizeCollection<T>
{
    /**
     * Returns a new MutableList with the specified element added. The original list remains unchanged.
     *
     * @param element the element to add to the new list
     * @return a new MutableList containing all elements from this list plus the new element
     */
    @Override
    MutableList<T> with(T element);

    /**
     * Returns a new MutableList with the specified element removed. The original list remains unchanged.
     *
     * @param element the element to remove from the new list
     * @return a new MutableList containing all elements from this list except the specified element
     */
    @Override
    MutableList<T> without(T element);

    /**
     * Returns a new MutableList with all specified elements added. The original list remains unchanged.
     *
     * @param elements the elements to add to the new list
     * @return a new MutableList containing all elements from this list plus all the new elements
     */
    @Override
    MutableList<T> withAll(Iterable<? extends T> elements);

    /**
     * Returns a new MutableList with all specified elements removed. The original list remains unchanged.
     *
     * @param elements the elements to remove from the new list
     * @return a new MutableList containing all elements from this list except the specified elements
     */
    @Override
    MutableList<T> withoutAll(Iterable<? extends T> elements);

    /**
     * Returns a new FixedSizeList with the elements in reverse order.
     *
     * @return a new FixedSizeList with reversed element order
     */
    @Override
    FixedSizeList<T> toReversed();

    /**
     * Executes the given procedure on each element in this list and returns this list.
     * This method is useful for performing side effects while chaining method calls.
     *
     * @param procedure the procedure to execute on each element
     * @return this FixedSizeList to allow method chaining
     */
    @Override
    FixedSizeList<T> tap(Procedure<? super T> procedure);

    /**
     * Sorts this list in place using the given comparator and returns this list for method chaining.
     * Unlike add/remove operations, sorting is allowed on FixedSizeList since it doesn't change the size.
     *
     * @param comparator the comparator to determine the order, or null for natural ordering
     * @return this FixedSizeList to allow method chaining
     */
    @Override
    default FixedSizeList<T> sortThis(Comparator<? super T> comparator)
    {
        this.sort(comparator);
        return this;
    }

    /**
     * Sorts this list in place using natural ordering and returns this list for method chaining.
     * Unlike add/remove operations, sorting is allowed on FixedSizeList since it doesn't change the size.
     *
     * @return this FixedSizeList to allow method chaining
     */
    @Override
    default FixedSizeList<T> sortThis()
    {
        return this.sortThis(null);
    }
}
