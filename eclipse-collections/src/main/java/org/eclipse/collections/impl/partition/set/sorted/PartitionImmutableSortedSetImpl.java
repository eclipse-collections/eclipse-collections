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

import org.eclipse.collections.api.partition.set.sorted.PartitionImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;

/**
 * An immutable implementation of {@link PartitionImmutableSortedSet} created from a mutable sorted partition.
 * <p>
 * This class maintains two immutable sorted sets: one for elements that satisfied the predicate (selected)
 * and one for elements that did not (rejected). Once created, the contents of this partition
 * cannot be modified. Both sets maintain their sort order from the source partition.
 * </p>
 * <p>
 * This implementation is thread-safe and can be safely shared between threads.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionImmutableSortedSet
 * @see PartitionTreeSortedSet
 * @since 1.0
 */
public class PartitionImmutableSortedSetImpl<T> implements PartitionImmutableSortedSet<T>
{
    private final ImmutableSortedSet<T> selected;
    private final ImmutableSortedSet<T> rejected;

    /**
     * Constructs a new immutable sorted partition from the given mutable sorted partition.
     * <p>
     * Creates immutable copies of both the selected and rejected sorted sets from the
     * provided partition. This is a snapshot operation - subsequent changes to
     * the source partition will not affect this immutable partition. The sort order
     * is preserved from the source partition.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionTreeSortedSet<Integer> mutablePartition = new PartitionTreeSortedSet<>(Comparator.naturalOrder());
     * mutablePartition.getSelected().add(1);
     * mutablePartition.getRejected().add(2);
     * PartitionImmutableSortedSetImpl<Integer> immutable = new PartitionImmutableSortedSetImpl<>(mutablePartition);
     * // immutable now contains a snapshot of the sorted partition
     * }</pre>
     *
     * @param partitionTreeSortedSet the mutable sorted partition to create an immutable copy from
     */
    public PartitionImmutableSortedSetImpl(PartitionTreeSortedSet<T> partitionTreeSortedSet)
    {
        this.selected = partitionTreeSortedSet.getSelected().toImmutable();
        this.rejected = partitionTreeSortedSet.getRejected().toImmutable();
    }

    /**
     * Returns the immutable sorted set containing elements that satisfied the partition predicate.
     * <p>
     * The returned set is immutable and cannot be modified. Elements are maintained in sorted order.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableSortedSet<Integer> partition = ...;
     * ImmutableSortedSet<Integer> selected = partition.getSelected();
     * // selected contains all elements that satisfied the predicate in sorted order
     * int size = selected.size();
     * }</pre>
     *
     * @return the immutable sorted set of selected elements
     */
    @Override
    public ImmutableSortedSet<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the immutable sorted set containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned set is immutable and cannot be modified. Elements are maintained in sorted order.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableSortedSet<Integer> partition = ...;
     * ImmutableSortedSet<Integer> rejected = partition.getRejected();
     * // rejected contains all elements that did not satisfy the predicate in sorted order
     * int size = rejected.size();
     * }</pre>
     *
     * @return the immutable sorted set of rejected elements
     */
    @Override
    public ImmutableSortedSet<T> getRejected()
    {
        return this.rejected;
    }
}
