/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.partition.set;

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.partition.set.PartitionImmutableSet;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.MutableSet;

/**
 * A mutable implementation of {@link PartitionMutableSet} that uses {@link org.eclipse.collections.impl.set.mutable.UnifiedSet}
 * to store elements that are partitioned based on a predicate.
 * <p>
 * This class maintains two separate sets: one for elements that satisfy the predicate (selected)
 * and one for elements that do not (rejected). The underlying implementation uses UnifiedSet,
 * which provides O(1) performance for add, remove, and contains operations.
 * </p>
 * <p>
 * Sets do not maintain insertion order and do not allow duplicate elements.
 * </p>
 * <p>
 * This implementation is not thread-safe.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionMutableSet
 * @see org.eclipse.collections.impl.set.mutable.UnifiedSet
 * @since 1.0
 */
public class PartitionUnifiedSet<T> implements PartitionMutableSet<T>
{
    private final MutableSet<T> selected = Sets.mutable.empty();
    private final MutableSet<T> rejected = Sets.mutable.empty();

    /**
     * Returns the mutable set containing elements that satisfied the partition predicate.
     * <p>
     * The returned set is backed by this partition, so modifications to the returned set
     * are reflected in this partition and vice-versa.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionUnifiedSet<Integer> partition = new PartitionUnifiedSet<>();
     * partition.getSelected().add(1);
     * partition.getSelected().add(2);
     * // partition.getSelected() now contains {1, 2}
     * }</pre>
     *
     * @return the mutable set of selected elements
     */
    @Override
    public MutableSet<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the mutable set containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned set is backed by this partition, so modifications to the returned set
     * are reflected in this partition and vice-versa.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionUnifiedSet<Integer> partition = new PartitionUnifiedSet<>();
     * partition.getRejected().add(3);
     * partition.getRejected().add(4);
     * // partition.getRejected() now contains {3, 4}
     * }</pre>
     *
     * @return the mutable set of rejected elements
     */
    @Override
    public MutableSet<T> getRejected()
    {
        return this.rejected;
    }

    /**
     * Converts this partition to an immutable partition with the same selected and rejected elements.
     * <p>
     * The returned immutable partition is a snapshot of the current state. Changes to this
     * mutable partition after calling this method will not be reflected in the returned
     * immutable partition.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionUnifiedSet<Integer> partition = new PartitionUnifiedSet<>();
     * partition.getSelected().add(1);
     * partition.getRejected().add(2);
     * PartitionImmutableSet<Integer> immutable = partition.toImmutable();
     * // immutable now contains a snapshot of the partition
     * }</pre>
     *
     * @return an immutable partition containing the same elements as this partition
     */
    @Override
    public PartitionImmutableSet<T> toImmutable()
    {
        return new PartitionImmutableSetImpl<>(this);
    }
}
