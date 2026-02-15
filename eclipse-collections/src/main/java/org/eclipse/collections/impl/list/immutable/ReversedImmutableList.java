/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

/**
 * A reverse-order view of an {@link ImmutableList}.
 */
class ReversedImmutableList<T>
        extends AbstractImmutableList<T>
        implements Serializable, RandomAccess
{
    private static final long serialVersionUID = 1L;

    private final AbstractImmutableList<T> delegate;

    ReversedImmutableList(AbstractImmutableList<T> delegate)
    {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public T get(int index)
    {
        Objects.checkIndex(index, this.size());
        return this.delegate.get(this.size() - 1 - index);
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.delegate.reverseForEach(procedure);
    }

    @Override
    public void forEach(Consumer<? super T> consumer)
    {
        this.delegate.reverseForEach(consumer::accept);
    }

    @Override
    public ImmutableList<T> newWith(T element)
    {
        int oldSize = this.size();
        T[] array = (T[]) new Object[oldSize + 1];
        this.toArray(array);
        array[oldSize] = element;
        return Lists.immutable.with(array);
    }

    @Override
    public AbstractImmutableList<T> reversed()
    {
        return this.delegate;
    }

    @Override
    public ImmutableList<T> toReversed()
    {
        return this.delegate;
    }

    protected Object writeReplace()
    {
        return Lists.immutable.withAll(this);
    }
}
