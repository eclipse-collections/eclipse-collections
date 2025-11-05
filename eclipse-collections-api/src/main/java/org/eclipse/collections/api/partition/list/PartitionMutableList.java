/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.partition.list;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.PartitionMutableCollection;

/**
 * A PartitionMutableList is the result of splitting a mutable list into two mutable lists based on a Predicate.
 * The results that answer true for the Predicate will be returned from the {@link #getSelected()} method,
 * and the results that answer false for the predicate will be returned from the {@link #getRejected()} method.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MutableList<Integer> numbers = Lists.mutable.with(1, 2, 3, 4, 5, 6);
 * PartitionMutableList<Integer> partition = numbers.partition(each -> each % 2 == 0);
 *
 * MutableList<Integer> evens = partition.getSelected();  // [2, 4, 6]
 * MutableList<Integer> odds = partition.getRejected();   // [1, 3, 5]
 *
 * // Modify the partitions
 * evens.add(8);
 * }</pre>
 *
 * @param <T> the type of elements in the partition
 */
public interface PartitionMutableList<T> extends PartitionMutableCollection<T>, PartitionList<T>
{
    /**
     * Returns the mutable list of elements that satisfied the predicate.
     *
     * @return the selected elements as a MutableList
     */
    @Override
    MutableList<T> getSelected();

    /**
     * Returns the mutable list of elements that did not satisfy the predicate.
     *
     * @return the rejected elements as a MutableList
     */
    @Override
    MutableList<T> getRejected();

    /**
     * Converts this mutable partition to an immutable partition.
     *
     * @return an immutable copy of this partition
     */
    @Override
    PartitionImmutableList<T> toImmutable();
}
