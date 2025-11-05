/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.partition;

import org.eclipse.collections.api.collection.ImmutableCollection;

/**
 * A PartitionImmutableCollection is the result of splitting an immutable collection into two immutable
 * collections based on a Predicate. The results that answer true for the Predicate will be returned from
 * the {@link #getSelected()} method, and the results that answer false for the predicate will be returned
 * from the {@link #getRejected()} method.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Partition an immutable collection
 * ImmutableList<Integer> numbers = Lists.immutable.with(1, 2, 3, 4, 5, 6);
 * PartitionImmutableCollection<Integer> partition =
 *     numbers.partition(each -> each % 2 == 0);
 *
 * ImmutableCollection<Integer> evens = partition.getSelected();  // [2, 4, 6]
 * ImmutableCollection<Integer> odds = partition.getRejected();   // [1, 3, 5]
 *
 * // The partitions are immutable and cannot be modified
 * // evens.add(8);  // Would throw UnsupportedOperationException
 *
 * // Chain operations on partitions
 * ImmutableList<Integer> doubledEvens = partition.getSelected()
 *     .collect(each -> each * 2)
 *     .toList();
 * }</pre>
 *
 * @param <T> the type of elements in the partition
 */
public interface PartitionImmutableCollection<T> extends PartitionIterable<T>
{
    /**
     * Returns the immutable collection of elements that satisfied the predicate.
     *
     * @return the selected elements as an ImmutableCollection
     */
    @Override
    ImmutableCollection<T> getSelected();

    /**
     * Returns the immutable collection of elements that did not satisfy the predicate.
     *
     * @return the rejected elements as an ImmutableCollection
     */
    @Override
    ImmutableCollection<T> getRejected();
}
