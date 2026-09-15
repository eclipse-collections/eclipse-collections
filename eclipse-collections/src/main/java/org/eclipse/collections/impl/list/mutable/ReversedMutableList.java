/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import java.io.Serializable;
import java.util.Objects;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;

/**
 * A reverse-order view of a {@link MutableList}.
 */
public class ReversedMutableList<T>
        extends AbstractMutableList<T>
        implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final MutableList<T> delegate;

    ReversedMutableList(MutableList<T> delegate)
    {
        Objects.requireNonNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public T get(int index)
    {
        Objects.checkIndex(index, this.size());
        return this.delegate.get(this.size() - 1 - index);
    }

    @Override
    public T set(int index, T element)
    {
        Objects.checkIndex(index, this.size());
        return this.delegate.set(this.size() - 1 - index, element);
    }

    @Override
    public void add(int index, T element)
    {
        Objects.checkIndex(index, this.size() + 1);
        this.delegate.add(this.size() - index, element);
    }

    @Override
    public boolean add(T element)
    {
        this.delegate.add(0, element);
        return true;
    }

    @Override
    public boolean addAll(int index, java.util.Collection<? extends T> collection)
    {
        Objects.checkIndex(index, this.size() + 1);
        if (collection.isEmpty())
        {
            return false;
        }
        int delegateIndex = this.size() - index;
        return this.delegate.addAll(delegateIndex, Lists.mutable.withAll(collection).reverseThis());
    }

    @Override
    public T remove(int index)
    {
        Objects.checkIndex(index, this.size());
        return this.delegate.remove(this.size() - 1 - index);
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public MutableList<T> reversed()
    {
        return this.delegate;
    }

    @Override
    public MutableList<T> toReversed()
    {
        return Lists.mutable.withAll(this.delegate);
    }

    @Override
    public MutableList<T> newEmpty()
    {
        return Lists.mutable.empty();
    }

    @Override
    public MutableList<T> clone()
    {
        return Lists.mutable.withAll(this);
    }

    protected Object writeReplace()
    {
        return Lists.mutable.withAll(this);
    }
}
