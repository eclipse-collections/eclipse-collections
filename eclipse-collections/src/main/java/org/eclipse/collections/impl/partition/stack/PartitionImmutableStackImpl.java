/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.partition.stack;

import org.eclipse.collections.api.partition.stack.PartitionImmutableStack;
import org.eclipse.collections.api.stack.ImmutableStack;

/**
 * An immutable implementation of {@link PartitionImmutableStack} created from a mutable partition.
 * <p>
 * This class maintains two immutable stacks: one for elements that satisfied the predicate (selected)
 * and one for elements that did not (rejected). Once created, the contents of this partition
 * cannot be modified. Stacks maintain LIFO (Last-In-First-Out) ordering.
 * </p>
 * <p>
 * This implementation is thread-safe and can be safely shared between threads.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionImmutableStack
 * @see PartitionArrayStack
 * @since 6.0
 */
public class PartitionImmutableStackImpl<T> implements PartitionImmutableStack<T>
{
    private final ImmutableStack<T> selected;
    private final ImmutableStack<T> rejected;

    /**
     * Constructs a new immutable partition from the given mutable partition.
     * <p>
     * Creates immutable copies of both the selected and rejected stacks from the
     * provided partition. This is a snapshot operation - subsequent changes to
     * the source partition will not affect this immutable partition.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionArrayStack<Integer> mutablePartition = new PartitionArrayStack<>();
     * // Use PartitionProcedure to add elements
     * PartitionImmutableStackImpl<Integer> immutable = new PartitionImmutableStackImpl<>(mutablePartition);
     * // immutable now contains a snapshot of the partition
     * }</pre>
     *
     * @param partitionArrayStack the mutable partition to create an immutable copy from
     */
    public PartitionImmutableStackImpl(PartitionArrayStack<T> partitionArrayStack)
    {
        this.selected = partitionArrayStack.getSelected().toImmutable();
        this.rejected = partitionArrayStack.getRejected().toImmutable();
    }

    /**
     * Returns the immutable stack containing elements that satisfied the partition predicate.
     * <p>
     * The returned stack is immutable and cannot be modified. Elements are in LIFO order.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableStack<Integer> partition = ...;
     * ImmutableStack<Integer> selected = partition.getSelected();
     * // selected contains all elements that satisfied the predicate
     * int size = selected.size();
     * }</pre>
     *
     * @return the immutable stack of selected elements
     */
    @Override
    public ImmutableStack<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the immutable stack containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned stack is immutable and cannot be modified. Elements are in LIFO order.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionImmutableStack<Integer> partition = ...;
     * ImmutableStack<Integer> rejected = partition.getRejected();
     * // rejected contains all elements that did not satisfy the predicate
     * int size = rejected.size();
     * }</pre>
     *
     * @return the immutable stack of rejected elements
     */
    @Override
    public ImmutableStack<T> getRejected()
    {
        return this.rejected;
    }
}
