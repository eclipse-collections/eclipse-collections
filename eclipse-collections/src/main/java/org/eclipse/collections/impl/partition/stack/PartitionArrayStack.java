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

import java.util.Objects;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.stack.PartitionImmutableStack;
import org.eclipse.collections.api.partition.stack.PartitionMutableStack;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;

public class PartitionArrayStack<T> implements PartitionMutableStack<T>
{
    private final MutableList<T> selected = FastList.newList();
    private final MutableList<T> rejected = FastList.newList();

    @Override
    public MutableStack<T> getSelected()
    {
        return ArrayStack.newStackFromTopToBottom(this.selected);
    }

    @Override
    public MutableStack<T> getRejected()
    {
        return ArrayStack.newStackFromTopToBottom(this.rejected);
    }

    @Override
    public PartitionImmutableStack<T> toImmutable()
    {
        return new PartitionImmutableStackImpl<>(this);
    }

    @Override
    public void add(T t)
    {
        throw new UnsupportedOperationException("add is no longer supported for PartitionArrayStack");
    }

    public static final class PartitionProcedure<T> implements Procedure<T>
    {
        private static final long serialVersionUID = 1L;

        private final Predicate<? super T> predicate;
        private final PartitionArrayStack<T> partitionMutableStack;

        public PartitionProcedure(Predicate<? super T> predicate, PartitionArrayStack<T> partitionMutableStack)
        {
            this.predicate = predicate;
            this.partitionMutableStack = partitionMutableStack;
        }

        @Override
        public void value(T each)
        {
            MutableList<T> bucket = this.predicate.accept(each)
                    ? this.partitionMutableStack.selected
                    : this.partitionMutableStack.rejected;
            bucket.add(each);
        }
    }

    public static final class PartitionWhileProcedure<T> implements Procedure<T>
    {
        private static final long serialVersionUID = 1L;

        private final Predicate<? super T> predicate;
        private final PartitionArrayStack<T> partitionMutableStack;
        private boolean stillSelecting = true;

        public PartitionWhileProcedure(Predicate<? super T> predicate, PartitionArrayStack<T> partitionMutableStack)
        {
            this.predicate = Objects.requireNonNull(predicate);
            this.partitionMutableStack = Objects.requireNonNull(partitionMutableStack);
        }

        @Override
        public void value(T each)
        {
            if (this.stillSelecting && this.predicate.accept(each))
            {
                this.partitionMutableStack.selected.add(each);
            }
            else
            {
                this.stillSelecting = false;
                this.partitionMutableStack.rejected.add(each);
            }
        }
    }

    public static final class PartitionPredicate2Procedure<T, P> implements Procedure<T>
    {
        private static final long serialVersionUID = 1L;

        private final Predicate2<? super T, ? super P> predicate;
        private final P parameter;
        private final PartitionArrayStack<T> partitionMutableStack;

        public PartitionPredicate2Procedure(Predicate2<? super T, ? super P> predicate, P parameter, PartitionArrayStack<T> partitionMutableStack)
        {
            this.predicate = predicate;
            this.parameter = parameter;
            this.partitionMutableStack = partitionMutableStack;
        }

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
