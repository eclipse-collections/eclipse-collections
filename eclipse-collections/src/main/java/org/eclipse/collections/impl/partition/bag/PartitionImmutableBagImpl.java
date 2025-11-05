/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.partition.bag;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.partition.bag.PartitionImmutableBag;

/**
 * An immutable implementation of {@link PartitionImmutableBag} created from a mutable partition.
 * <p>
 * This class maintains two immutable bags: one for elements that satisfied the predicate (selected)
 * and one for elements that did not (rejected). Once created, the contents of this partition
 * cannot be modified.
 * </p>
 * <p>
 * This implementation is thread-safe and can be safely shared between threads.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionImmutableBag
 * @see PartitionHashBag
 * @since 6.0
 */
public class PartitionImmutableBagImpl<T> implements PartitionImmutableBag<T>
{
    private final ImmutableBag<T> selected;
    private final ImmutableBag<T> rejected;

    /**
     * Constructs a new immutable partition from the given mutable partition.
     * <p>
     * Creates immutable copies of both the selected and rejected bags from the
     * provided partition. This is a snapshot operation - subsequent changes to
     * the source partition will not affect this immutable partition.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionHashBag<Integer> mutablePartition = new PartitionHashBag<>();
     * mutablePartition.getSelected().add(1);
     * mutablePartition.getRejected().add(2);
     * PartitionImmutableBagImpl<Integer> immutable = new PartitionImmutableBagImpl<>(mutablePartition);
     * // immutable now contains a snapshot of the partition
     * }</pre>
     *
     * @param partitionHashBag the mutable partition to create an immutable copy from
     */
    public PartitionImmutableBagImpl(PartitionHashBag<T> partitionHashBag)
    {
        this.selected = partitionHashBag.getSelected().toImmutable();
        this.rejected = partitionHashBag.getRejected().toImmutable();
    }

    /**
     * Returns the immutable bag containing elements that satisfied the partition predicate.
     * <p>
     * The returned bag is immutable and cannot be modified.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableBag<Integer> partition = ...;
     * ImmutableBag<Integer> selected = partition.getSelected();
     * // selected contains all elements that satisfied the predicate
     * int size = selected.size();
     * }</pre>
     *
     * @return the immutable bag of selected elements
     */
    @Override
    public ImmutableBag<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the immutable bag containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned bag is immutable and cannot be modified.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableBag<Integer> partition = ...;
     * ImmutableBag<Integer> rejected = partition.getRejected();
     * // rejected contains all elements that did not satisfy the predicate
     * int size = rejected.size();
     * }</pre>
     *
     * @return the immutable bag of rejected elements
     */
    @Override
    public ImmutableBag<T> getRejected()
    {
        return this.rejected;
    }
}
