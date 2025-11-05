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

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.partition.bag.PartitionImmutableBag;
import org.eclipse.collections.api.partition.bag.PartitionMutableBag;
import org.eclipse.collections.impl.bag.mutable.HashBag;

/**
 * A mutable implementation of {@link PartitionMutableBag} that uses {@link HashBag} to store
 * elements that are partitioned based on a predicate.
 * <p>
 * This class maintains two separate bags: one for elements that satisfy the predicate (selected)
 * and one for elements that do not (rejected). The underlying implementation uses {@link HashBag},
 * which provides O(1) performance for add, remove, and contains operations.
 * </p>
 * <p>
 * This implementation is not thread-safe.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionMutableBag
 * @see HashBag
 * @since 6.0
 */
public class PartitionHashBag<T> implements PartitionMutableBag<T>
{
    private final MutableBag<T> selected = HashBag.newBag();
    private final MutableBag<T> rejected = HashBag.newBag();

    /**
     * Returns the mutable bag containing elements that satisfied the partition predicate.
     * <p>
     * The returned bag is backed by this partition, so modifications to the returned bag
     * are reflected in this partition and vice-versa.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionHashBag<Integer> partition = new PartitionHashBag<>();
     * partition.getSelected().add(1);
     * partition.getSelected().add(2);
     * // partition.getSelected() now contains [1, 2]
     * }</pre>
     *
     * @return the mutable bag of selected elements
     */
    @Override
    public MutableBag<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the mutable bag containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned bag is backed by this partition, so modifications to the returned bag
     * are reflected in this partition and vice-versa.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionHashBag<Integer> partition = new PartitionHashBag<>();
     * partition.getRejected().add(3);
     * partition.getRejected().add(4);
     * // partition.getRejected() now contains [3, 4]
     * }</pre>
     *
     * @return the mutable bag of rejected elements
     */
    @Override
    public MutableBag<T> getRejected()
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
     * PartitionHashBag<Integer> partition = new PartitionHashBag<>();
     * partition.getSelected().add(1);
     * partition.getRejected().add(2);
     * PartitionImmutableBag<Integer> immutable = partition.toImmutable();
     * // immutable now contains a snapshot of the partition
     * }</pre>
     *
     * @return an immutable partition containing the same elements as this partition
     */
    @Override
    public PartitionImmutableBag<T> toImmutable()
    {
        return new PartitionImmutableBagImpl<>(this);
    }
}
