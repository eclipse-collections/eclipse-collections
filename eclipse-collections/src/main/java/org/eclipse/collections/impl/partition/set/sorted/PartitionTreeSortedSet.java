/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.partition.set.sorted;

import java.util.Comparator;

import org.eclipse.collections.api.partition.set.sorted.PartitionImmutableSortedSet;
import org.eclipse.collections.api.partition.set.sorted.PartitionMutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;

/**
 * A mutable implementation of {@link PartitionMutableSortedSet} that uses {@link TreeSortedSet} to store
 * elements that are partitioned based on a predicate.
 * <p>
 * This class maintains two separate sorted sets: one for elements that satisfy the predicate (selected)
 * and one for elements that do not (rejected). The underlying implementation uses {@link TreeSortedSet},
 * which maintains elements in sorted order according to the provided comparator. The TreeSortedSet provides
 * O(log n) performance for add, remove, and contains operations.
 * </p>
 * <p>
 * Both selected and rejected sets use the same comparator to maintain consistent ordering.
 * Sets do not allow duplicate elements.
 * </p>
 * <p>
 * This implementation is not thread-safe.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionMutableSortedSet
 * @see TreeSortedSet
 * @since 1.0
 */
public class PartitionTreeSortedSet<T> implements PartitionMutableSortedSet<T>
{
    private final MutableSortedSet<T> selected;
    private final MutableSortedSet<T> rejected;

    /**
     * Constructs a new partition with the specified comparator for both sets.
     * <p>
     * The comparator is used to maintain the sort order of elements in both the
     * selected and rejected sets.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Create a partition with natural ordering
     * PartitionTreeSortedSet<Integer> partition = new PartitionTreeSortedSet<>(Comparator.naturalOrder());
     *
     * // Create a partition with reverse ordering
     * PartitionTreeSortedSet<String> reversePartition = new PartitionTreeSortedSet<>(Comparator.reverseOrder());
     * }</pre>
     *
     * @param comparator the comparator used to order elements in both selected and rejected sets
     */
    public PartitionTreeSortedSet(Comparator<? super T> comparator)
    {
        this.selected = TreeSortedSet.newSet(comparator);
        this.rejected = TreeSortedSet.newSet(comparator);
    }

    /**
     * Returns the mutable sorted set containing elements that satisfied the partition predicate.
     * <p>
     * The returned set is backed by this partition, so modifications to the returned set
     * are reflected in this partition and vice-versa. Elements are maintained in sorted
     * order according to the comparator provided during construction.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionTreeSortedSet<Integer> partition = new PartitionTreeSortedSet<>(Comparator.naturalOrder());
     * partition.getSelected().add(3);
     * partition.getSelected().add(1);
     * partition.getSelected().add(2);
     * // partition.getSelected() contains {1, 2, 3} in sorted order
     * }</pre>
     *
     * @return the mutable sorted set of selected elements
     */
    @Override
    public MutableSortedSet<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the mutable sorted set containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned set is backed by this partition, so modifications to the returned set
     * are reflected in this partition and vice-versa. Elements are maintained in sorted
     * order according to the comparator provided during construction.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionTreeSortedSet<Integer> partition = new PartitionTreeSortedSet<>(Comparator.naturalOrder());
     * partition.getRejected().add(6);
     * partition.getRejected().add(4);
     * partition.getRejected().add(5);
     * // partition.getRejected() contains {4, 5, 6} in sorted order
     * }</pre>
     *
     * @return the mutable sorted set of rejected elements
     */
    @Override
    public MutableSortedSet<T> getRejected()
    {
        return this.rejected;
    }

    /**
     * Converts this partition to an immutable partition with the same selected and rejected elements.
     * <p>
     * The returned immutable partition is a snapshot of the current state. Changes to this
     * mutable partition after calling this method will not be reflected in the returned
     * immutable partition. The sort order is preserved in the immutable partition.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionTreeSortedSet<Integer> partition = new PartitionTreeSortedSet<>(Comparator.naturalOrder());
     * partition.getSelected().add(1);
     * partition.getRejected().add(2);
     * PartitionImmutableSortedSet<Integer> immutable = partition.toImmutable();
     * // immutable now contains a snapshot of the sorted partition
     * }</pre>
     *
     * @return an immutable sorted partition containing the same elements as this partition
     */
    @Override
    public PartitionImmutableSortedSet<T> toImmutable()
    {
        return new PartitionImmutableSortedSetImpl<>(this);
    }
}
