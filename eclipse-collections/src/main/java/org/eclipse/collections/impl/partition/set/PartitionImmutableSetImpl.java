/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.partition.set;

import org.eclipse.collections.api.partition.set.PartitionImmutableSet;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.ImmutableSet;

/**
 * An immutable implementation of {@link PartitionImmutableSet} created from a mutable partition.
 * <p>
 * This class maintains two immutable sets: one for elements that satisfied the predicate (selected)
 * and one for elements that did not (rejected). Once created, the contents of this partition
 * cannot be modified.
 * </p>
 * <p>
 * This implementation is thread-safe and can be safely shared between threads.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionImmutableSet
 * @see PartitionUnifiedSet
 * @since 1.0
 */
public class PartitionImmutableSetImpl<T> implements PartitionImmutableSet<T>
{
    private final ImmutableSet<T> selected;
    private final ImmutableSet<T> rejected;

    /**
     * Constructs a new immutable partition from the given mutable partition.
     * <p>
     * Creates immutable copies of both the selected and rejected sets from the
     * provided partition. This is a snapshot operation - subsequent changes to
     * the source partition will not affect this immutable partition.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionUnifiedSet<Integer> mutablePartition = new PartitionUnifiedSet<>();
     * mutablePartition.getSelected().add(1);
     * mutablePartition.getRejected().add(2);
     * PartitionImmutableSetImpl<Integer> immutable = new PartitionImmutableSetImpl<>(mutablePartition);
     * // immutable now contains a snapshot of the partition
     * }</pre>
     *
     * @param mutablePartition the mutable partition to create an immutable copy from
     */
    public PartitionImmutableSetImpl(PartitionMutableSet<T> mutablePartition)
    {
        this.selected = mutablePartition.getSelected().toImmutable();
        this.rejected = mutablePartition.getRejected().toImmutable();
    }

    /**
     * Returns the immutable set containing elements that satisfied the partition predicate.
     * <p>
     * The returned set is immutable and cannot be modified.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableSet<Integer> partition = ...;
     * ImmutableSet<Integer> selected = partition.getSelected();
     * // selected contains all elements that satisfied the predicate
     * int size = selected.size();
     * }</pre>
     *
     * @return the immutable set of selected elements
     */
    @Override
    public ImmutableSet<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the immutable set containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned set is immutable and cannot be modified.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableSet<Integer> partition = ...;
     * ImmutableSet<Integer> rejected = partition.getRejected();
     * // rejected contains all elements that did not satisfy the predicate
     * int size = rejected.size();
     * }</pre>
     *
     * @return the immutable set of rejected elements
     */
    @Override
    public ImmutableSet<T> getRejected()
    {
        return this.rejected;
    }
}
