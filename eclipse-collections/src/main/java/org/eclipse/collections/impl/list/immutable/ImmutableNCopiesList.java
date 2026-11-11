/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.immutable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.RandomAccess;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

/**
 * This is a single element immutable List which is created by calling
 * Immutable.newListWith(one) method.
 */
final class ImmutableNCopiesList<T>
        extends AbstractImmutableList<T>
        implements Serializable, RandomAccess
{
    private static final long serialVersionUID = 1L;

    private final T item;
    private final int copies;

    ImmutableNCopiesList(T item, int copies)
    {
        assert copies > 1;
        this.item = item;
        this.copies = copies;
    }

    @Override
    public int size()
    {
        return this.copies;
    }

    @Override
    public boolean contains(Object obj)
    {
        return Objects.equals(obj, this.item);
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        T item = this.item;
        for (int i = 0; i < this.copies; i++)
        {
            procedure.value(item);
        }
    }

    @Override
    public T get(int index)
    {
        if (0 <= index && index < this.copies)
        {
            return this.item;
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size());
    }

    @Override
    public T getFirst()
    {
        return this.item;
    }

    @Override
    public T getLast()
    {
        return this.item;
    }

    @Override
    public T getOnly()
    {
        throw new IllegalStateException("Size must be 1 but was " + this.size());
    }

    @Override
    public ImmutableList<T> toReversed()
    {
        return this;
    }

    @Override
    public ImmutableList<T> distinct()
    {
        return Lists.immutable.of(this.item);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return predicate.accept(this.item) ? this.copies : 0;
    }

    @Override
    public int indexOf(Object object)
    {
        return Objects.equals(this.item, object) ? 0 : -1;
    }

    @Override
    public int lastIndexOf(Object object)
    {
        return Objects.equals(this.item, object) ? this.copies - 1 : -1;
    }

    @Override
    public ImmutableList<T> select(Predicate<? super T> predicate)
    {
        return predicate.accept(this.item) ? this : Lists.immutable.empty();
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return predicate.accept(this.item) ? 0 : -1;
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        return predicate.accept(this.item) ? this.copies - 1 : -1;
    }

    @Override
    public ImmutableList<T> newWith(T newItem)
    {
        if (Objects.equals(this.item, newItem))
        {
            return new ImmutableNCopiesList<>(this.item, this.copies + 1);
        }
        switch (this.copies)
        {
            case 2:
                return Lists.immutable.with(this.item, this.item, newItem);
            case 3:
                return Lists.immutable.with(this.item, this.item, this.item, newItem);
            case 4:
                return Lists.immutable.with(this.item, this.item, this.item, this.item, newItem);
            case 5:
                return Lists.immutable.with(this.item, this.item, this.item, this.item, this.item, newItem);
            case 6:
                return Lists.immutable.with(this.item, this.item, this.item, this.item, this.item, this.item, newItem);
            case 7:
                return Lists.immutable.with(this.item, this.item, this.item, this.item, this.item, this.item, this.item, newItem);
            case 8:
                return Lists.immutable.with(this.item, this.item, this.item, this.item, this.item, this.item, this.item, this.item, newItem);
            case 9:
                return Lists.immutable.with(this.item, this.item, this.item, this.item, this.item, this.item, this.item, this.item, this.item, newItem);
            default:
                T[] items = (T[]) new Object[this.copies + 1];
                Arrays.fill(items, 0, this.copies, this.item);
                items[this.copies] = newItem;
                return Lists.immutable.with(items);
        }
    }
}
