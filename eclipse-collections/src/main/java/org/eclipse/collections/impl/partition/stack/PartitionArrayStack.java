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

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.stack.PartitionImmutableStack;
import org.eclipse.collections.api.partition.stack.PartitionMutableStack;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;

/**
 * A mutable implementation of {@link PartitionMutableStack} that uses {@link ArrayStack} to represent
 * elements that are partitioned based on a predicate.
 * <p>
 * This class maintains two separate lists internally to store the partitioned elements, which are then
 * converted to stacks when requested. Elements that satisfy the predicate go into the selected stack,
 * while elements that don't go into the rejected stack. The internal lists use {@link FastList} for
 * efficient sequential access during partitioning.
 * </p>
 * <p>
 * Note: The {@code add} method is not supported and will throw {@link UnsupportedOperationException}.
 * Use the provided procedure classes to partition elements instead.
 * </p>
 * <p>
 * This implementation is not thread-safe.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionMutableStack
 * @see ArrayStack
 * @since 6.0
 */
public class PartitionArrayStack<T> implements PartitionMutableStack<T>
{
    private final MutableList<T> selected = FastList.newList();
    private final MutableList<T> rejected = FastList.newList();

    /**
     * Returns a mutable stack containing elements that satisfied the partition predicate.
     * <p>
     * Creates a new {@link ArrayStack} from the internal list, maintaining LIFO (Last-In-First-Out) order.
     * The returned stack is a new instance - modifications to it will not affect this partition.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionArrayStack<Integer> partition = new PartitionArrayStack<>();
     * // Use PartitionProcedure to add elements
     * MutableStack<Integer> selected = partition.getSelected();
     * // selected now contains all elements that satisfied the predicate
     * }</pre>
     *
     * @return a new mutable stack of selected elements
     */
    @Override
    public MutableStack<T> getSelected()
    {
        return ArrayStack.newStackFromTopToBottom(this.selected);
    }

    /**
     * Returns a mutable stack containing elements that did not satisfy the partition predicate.
     * <p>
     * Creates a new {@link ArrayStack} from the internal list, maintaining LIFO (Last-In-First-Out) order.
     * The returned stack is a new instance - modifications to it will not affect this partition.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionArrayStack<Integer> partition = new PartitionArrayStack<>();
     * // Use PartitionProcedure to add elements
     * MutableStack<Integer> rejected = partition.getRejected();
     * // rejected now contains all elements that did not satisfy the predicate
     * }</pre>
     *
     * @return a new mutable stack of rejected elements
     */
    @Override
    public MutableStack<T> getRejected()
    {
        return ArrayStack.newStackFromTopToBottom(this.rejected);
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
     * PartitionArrayStack<Integer> partition = new PartitionArrayStack<>();
     * // Use PartitionProcedure to add elements
     * PartitionImmutableStack<Integer> immutable = partition.toImmutable();
     * // immutable now contains a snapshot of the partition
     * }</pre>
     *
     * @return an immutable partition containing the same elements as this partition
     */
    @Override
    public PartitionImmutableStack<T> toImmutable()
    {
        return new PartitionImmutableStackImpl<>(this);
    }

    /**
     * This operation is not supported for {@code PartitionArrayStack}.
     * <p>
     * Use {@link PartitionProcedure} or {@link PartitionPredicate2Procedure} to add elements
     * to this partition instead.
     * </p>
     *
     * @param t the element to add (not supported)
     * @throws UnsupportedOperationException always thrown as this operation is not supported
     */
    @Override
    public void add(T t)
    {
        throw new UnsupportedOperationException("add is no longer supported for PartitionArrayStack");
    }

    /**
     * A procedure that partitions elements based on a predicate and adds them to the appropriate
     * bucket (selected or rejected) in a {@link PartitionArrayStack}.
     * <p>
     * This procedure evaluates each element against the predicate. If the predicate accepts the element,
     * it's added to the selected bucket; otherwise, it's added to the rejected bucket.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionArrayStack<Integer> partition = new PartitionArrayStack<>();
     * Predicate<Integer> isEven = i -> i % 2 == 0;
     * PartitionProcedure<Integer> procedure = new PartitionProcedure<>(isEven, partition);
     *
     * // Apply the procedure to elements
     * procedure.value(1);  // Goes to rejected
     * procedure.value(2);  // Goes to selected
     * procedure.value(3);  // Goes to rejected
     * }</pre>
     *
     * @param <T> the type of elements being partitioned
     */
    public static final class PartitionProcedure<T> implements Procedure<T>
    {
        private static final long serialVersionUID = 1L;

        private final Predicate<? super T> predicate;
        private final PartitionArrayStack<T> partitionMutableStack;

        /**
         * Constructs a new partition procedure with the given predicate and partition.
         *
         * @param predicate the predicate to evaluate elements against
         * @param partitionMutableStack the partition to add elements to
         */
        public PartitionProcedure(Predicate<? super T> predicate, PartitionArrayStack<T> partitionMutableStack)
        {
            this.predicate = predicate;
            this.partitionMutableStack = partitionMutableStack;
        }

        /**
         * Evaluates the element against the predicate and adds it to the appropriate bucket.
         *
         * @param each the element to partition
         */
        @Override
        public void value(T each)
        {
            MutableList<T> bucket = this.predicate.accept(each)
                    ? this.partitionMutableStack.selected
                    : this.partitionMutableStack.rejected;
            bucket.add(each);
        }
    }

    /**
     * A procedure that partitions elements based on a two-argument predicate and adds them to the appropriate
     * bucket (selected or rejected) in a {@link PartitionArrayStack}.
     * <p>
     * This procedure evaluates each element along with a fixed parameter against the predicate.
     * If the predicate accepts the element and parameter, the element is added to the selected bucket;
     * otherwise, it's added to the rejected bucket.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * PartitionArrayStack<Integer> partition = new PartitionArrayStack<>();
     * Predicate2<Integer, Integer> isGreaterThan = (i, threshold) -> i > threshold;
     * int threshold = 5;
     * PartitionPredicate2Procedure<Integer, Integer> procedure =
     *     new PartitionPredicate2Procedure<>(isGreaterThan, threshold, partition);
     *
     * // Apply the procedure to elements
     * procedure.value(3);  // Goes to rejected (3 <= 5)
     * procedure.value(7);  // Goes to selected (7 > 5)
     * procedure.value(5);  // Goes to rejected (5 <= 5)
     * }</pre>
     *
     * @param <T> the type of elements being partitioned
     * @param <P> the type of the parameter to the predicate
     */
    public static final class PartitionPredicate2Procedure<T, P> implements Procedure<T>
    {
        private static final long serialVersionUID = 1L;

        private final Predicate2<? super T, ? super P> predicate;
        private final P parameter;
        private final PartitionArrayStack<T> partitionMutableStack;

        /**
         * Constructs a new partition procedure with the given predicate, parameter, and partition.
         *
         * @param predicate the two-argument predicate to evaluate elements against
         * @param parameter the fixed parameter to pass to the predicate
         * @param partitionMutableStack the partition to add elements to
         */
        public PartitionPredicate2Procedure(Predicate2<? super T, ? super P> predicate, P parameter, PartitionArrayStack<T> partitionMutableStack)
        {
            this.predicate = predicate;
            this.parameter = parameter;
            this.partitionMutableStack = partitionMutableStack;
        }

        /**
         * Evaluates the element with the parameter against the predicate and adds it to the appropriate bucket.
         *
         * @param each the element to partition
         */
        @Override
        public void value(T each)
        {
            MutableList<T> bucket = this.predicate.accept(each, this.parameter)
                    ? this.partitionMutableStack.selected
                    : this.partitionMutableStack.rejected;
            bucket.add(each);
        }
    }
}
