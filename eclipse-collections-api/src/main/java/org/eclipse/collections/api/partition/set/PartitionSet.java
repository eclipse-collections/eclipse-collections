/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.partition.set;

import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.api.set.SetIterable;

/**
 * A PartitionSet is the result of splitting a SetIterable into two SetIterables based on a Predicate.
 * The results that answer true for the Predicate will be returned from the {@link #getSelected()} method,
 * and the results that answer false for the predicate will be returned from the {@link #getRejected()} method.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * SetIterable<Integer> numbers = Sets.mutable.with(1, 2, 3, 4, 5, 6);
 * PartitionSet<Integer> partition = numbers.partition(each -> each % 2 == 0);
 *
 * SetIterable<Integer> evens = partition.getSelected();  // [2, 4, 6]
 * SetIterable<Integer> odds = partition.getRejected();   // [1, 3, 5]
 * }</pre>
 *
 * @param <T> the type of elements in the partition
 */
public interface PartitionSet<T> extends PartitionIterable<T>
{
    /**
     * Returns the set of elements that satisfied the predicate.
     *
     * @return the selected elements as a SetIterable
     */
    @Override
    SetIterable<T> getSelected();

    /**
     * Returns the set of elements that did not satisfy the predicate.
     *
     * @return the rejected elements as a SetIterable
     */
    @Override
    SetIterable<T> getRejected();
}
