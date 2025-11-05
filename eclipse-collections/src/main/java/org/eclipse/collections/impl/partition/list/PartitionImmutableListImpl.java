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

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;

/**
 * An immutable implementation of {@link PartitionImmutableList} created from a mutable partition or
 * existing immutable lists.
 * <p>
 * This class maintains two immutable lists: one for elements that satisfied the predicate (selected)
 * and one for elements that did not (rejected). Once created, the contents of this partition
 * cannot be modified. The order of elements is preserved from the source.
 * </p>
 * <p>
 * This implementation is thread-safe and can be safely shared between threads.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionImmutableList
 * @see PartitionFastList
 * @since 1.0
 */
public class PartitionImmutableListImpl<T> implements PartitionImmutableList<T>
{
    private final ImmutableList<T> selected;
    private final ImmutableList<T> rejected;

    /**
     * Constructs a new immutable partition from the given immutable lists.
     * <p>
     * This constructor directly uses the provided immutable lists without copying.
     * The lists must already be immutable.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableList<Integer> selected = Lists.immutable.with(1, 2, 3);
     * ImmutableList<Integer> rejected = Lists.immutable.with(4, 5, 6);
     * PartitionImmutableListImpl<Integer> partition = new PartitionImmutableListImpl<>(selected, rejected);
     * }</pre>
     *
     * @param selected the immutable list of selected elements
     * @param rejected the immutable list of rejected elements
     */
    public PartitionImmutableListImpl(ImmutableList<T> selected, ImmutableList<T> rejected)
    {
        this.selected = selected;
        this.rejected = rejected;
    }

    /**
     * Constructs a new immutable partition from the given mutable partition.
     * <p>
     * Creates immutable copies of both the selected and rejected lists from the
     * provided partition. This is a snapshot operation - subsequent changes to
     * the source partition will not affect this immutable partition.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionFastList<Integer> mutablePartition = new PartitionFastList<>();
     * mutablePartition.getSelected().add(1);
     * mutablePartition.getRejected().add(2);
     * PartitionImmutableListImpl<Integer> immutable = new PartitionImmutableListImpl<>(mutablePartition);
     * // immutable now contains a snapshot of the partition
     * }</pre>
     *
     * @param partitionFastList the mutable partition to create an immutable copy from
     */
    public PartitionImmutableListImpl(PartitionFastList<T> partitionFastList)
    {
        this(partitionFastList.getSelected().toImmutable(), partitionFastList.getRejected().toImmutable());
    }

    /**
     * Returns the immutable list containing elements that satisfied the partition predicate.
     * <p>
     * The returned list is immutable and cannot be modified. Elements maintain their order.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableList<Integer> partition = ...;
     * ImmutableList<Integer> selected = partition.getSelected();
     * // selected contains all elements that satisfied the predicate
     * int size = selected.size();
     * }</pre>
     *
     * @return the immutable list of selected elements
     */
    @Override
    public ImmutableList<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the immutable list containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned list is immutable and cannot be modified. Elements maintain their order.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableList<Integer> partition = ...;
     * ImmutableList<Integer> rejected = partition.getRejected();
     * // rejected contains all elements that did not satisfy the predicate
     * int size = rejected.size();
     * }</pre>
     *
     * @return the immutable list of rejected elements
     */
    @Override
    public ImmutableList<T> getRejected()
    {
        return this.rejected;
    }
}
