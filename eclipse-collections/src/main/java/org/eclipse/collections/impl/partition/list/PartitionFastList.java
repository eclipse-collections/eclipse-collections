/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.partition.list;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.impl.list.mutable.FastList;

/**
 * A mutable implementation of {@link PartitionMutableList} that uses {@link FastList} to store
 * elements that are partitioned based on a predicate.
 * <p>
 * This class maintains two separate lists: one for elements that satisfy the predicate (selected)
 * and one for elements that do not (rejected). The underlying implementation uses {@link FastList},
 * which provides O(1) performance for add and get operations, and maintains insertion order.
 * </p>
 * <p>
 * Elements in both selected and rejected lists maintain their relative order from the original
 * collection when partitioned.
 * </p>
 * <p>
 * This implementation is not thread-safe.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionMutableList
 * @see FastList
 * @since 1.0
 */
public class PartitionFastList<T> implements PartitionMutableList<T>
{
    private final MutableList<T> selected = FastList.newList();
    private final MutableList<T> rejected = FastList.newList();

    /**
     * Returns the mutable list containing elements that satisfied the partition predicate.
     * <p>
     * The returned list is backed by this partition, so modifications to the returned list
     * are reflected in this partition and vice-versa. The list maintains insertion order.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionFastList<Integer> partition = new PartitionFastList<>();
     * partition.getSelected().add(1);
     * partition.getSelected().add(2);
     * // partition.getSelected() now contains [1, 2] in insertion order
     * }</pre>
     *
     * @return the mutable list of selected elements
     */
    @Override
    public MutableList<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the mutable list containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned list is backed by this partition, so modifications to the returned list
     * are reflected in this partition and vice-versa. The list maintains insertion order.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionFastList<Integer> partition = new PartitionFastList<>();
     * partition.getRejected().add(3);
     * partition.getRejected().add(4);
     * // partition.getRejected() now contains [3, 4] in insertion order
     * }</pre>
     *
     * @return the mutable list of rejected elements
     */
    @Override
    public MutableList<T> getRejected()
    {
        return this.rejected;
    }

    /**
     * Converts this partition to an immutable partition with the same selected and rejected elements.
     * <p>
     * The returned immutable partition is a snapshot of the current state. Changes to this
     * mutable partition after calling this method will not be reflected in the returned
     * immutable partition. The order of elements is preserved.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionFastList<Integer> partition = new PartitionFastList<>();
     * partition.getSelected().add(1);
     * partition.getRejected().add(2);
     * PartitionImmutableList<Integer> immutable = partition.toImmutable();
     * // immutable now contains a snapshot of the partition
     * }</pre>
     *
     * @return an immutable partition containing the same elements as this partition
     */
    @Override
    public PartitionImmutableList<T> toImmutable()
    {
        return new PartitionImmutableListImpl<>(this);
    }
}
