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

import org.eclipse.collections.api.collection.MutableCollection;

/**
 * A PartitionMutableCollection is the result of splitting a mutable collection into two mutable collections
 * based on a Predicate. The results that answer true for the Predicate will be returned from the
 * {@link #getSelected()} method, and the results that answer false for the predicate will be returned
 * from the {@link #getRejected()} method.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Partition a mutable collection
 * MutableList<Integer> numbers = Lists.mutable.with(1, 2, 3, 4, 5, 6);
 * PartitionMutableCollection<Integer> partition =
 *     numbers.partition(each -> each % 2 == 0);
 *
 * MutableCollection<Integer> evens = partition.getSelected();    // [2, 4, 6]
 * MutableCollection<Integer> odds = partition.getRejected();     // [1, 3, 5]
 *
 * // Modify the partitions (they are mutable)
 * partition.getSelected().add(8);
 * partition.getRejected().remove(1);
 *
 * // Convert to immutable
 * PartitionImmutableCollection<Integer> immutable = partition.toImmutable();
 * }</pre>
 *
 * @param <T> the type of elements in the partition
 */
public interface PartitionMutableCollection<T> extends PartitionIterable<T>
{
    /**
     * Returns the mutable collection of elements that satisfied the predicate.
     *
     * @return the selected elements as a MutableCollection
     */
    @Override
    MutableCollection<T> getSelected();

    /**
     * Returns the mutable collection of elements that did not satisfy the predicate.
     *
     * @return the rejected elements as a MutableCollection
     */
    @Override
    MutableCollection<T> getRejected();

    /**
     * Converts this mutable partition to an immutable partition.
     *
     * @return an immutable copy of this partition
     */
    PartitionImmutableCollection<T> toImmutable();
}
