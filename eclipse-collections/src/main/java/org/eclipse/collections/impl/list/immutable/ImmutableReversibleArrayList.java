/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.immutable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.RandomAccess;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * An immutable list backed by an array that supports O(1) reversal and zero-copy sublists.
 * <p>
 * Reversed views are created by flipping the {@code ascending} flag rather than copying the array.
 * Sublists share the underlying array with adjusted {@code from} and {@code size}.
 * </p>
 */
public final class ImmutableReversibleArrayList<T>
        extends AbstractImmutableList<T>
        implements Serializable, RandomAccess
{
    private static final long serialVersionUID = 1L;

    private static final ImmutableReversibleArrayList<Object>
            EMPTY = new ImmutableReversibleArrayList<>(new Object[0], 0, 0, true);

    private final T[] array;
    private final int from;
    private final int size;
    private final boolean ascending;

    ImmutableReversibleArrayList(T[] array)
    {
        this(array, 0, array.length, true);
    }

    private ImmutableReversibleArrayList(T[] array, int from, int size, boolean ascending)
    {
        if (array == null)
        {
            throw new IllegalArgumentException("array cannot be null");
        }
        this.array = array;
        this.from = from;
        this.size = size;
        this.ascending = ascending;
    }

    public static <T> ImmutableReversibleArrayList<T> empty()
    {
        return (ImmutableReversibleArrayList<T>) EMPTY;
    }

    public static <E> ImmutableReversibleArrayList<E> newList(Iterable<? extends E> iterable)
    {
        return new ImmutableReversibleArrayList<>((E[]) Iterate.toArray(iterable));
    }

    public static <E> ImmutableReversibleArrayList<E> newListWith(E... elements)
    {
        return new ImmutableReversibleArrayList<>(elements.clone());
    }

    private int physicalIndex(int logicalIndex)
    {
        return this.ascending
                ? this.from + logicalIndex
                : this.from - logicalIndex;
    }

    private int logicalIndex(int physicalIdx)
    {
        return this.ascending
                ? physicalIdx - this.from
                : this.from - physicalIdx;
    }

    private int lastPhysicalIndex()
    {
        return this.ascending
                ? this.from + this.size - 1
                : this.from - this.size + 1;
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public T get(int index)
    {
        if (index < 0 || index >= this.size)
        {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        return this.array[this.physicalIndex(index)];
    }

    @Override
    public T getFirst()
    {
        return this.isEmpty() ? null : this.array[this.from];
    }

    @Override
    public T getLast()
    {
        return this.isEmpty() ? null : this.array[this.lastPhysicalIndex()];
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        if (this.ascending)
        {
            for (int i = this.from; i < this.physicalIndex(this.size); i++)
            {
                procedure.value(this.array[i]);
            }
        }
        else
        {
            for (int i = this.from; i > this.physicalIndex(this.size); i--)
            {
                procedure.value(this.array[i]);
            }
        }
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        int logicalIndex = 0;
        if (this.ascending)
        {
            for (int i = this.from; i < this.physicalIndex(this.size); i++)
            {
                objectIntProcedure.value(this.array[i], logicalIndex++);
            }
        }
        else
        {
            for (int i = this.from; i > this.physicalIndex(this.size); i--)
            {
                objectIntProcedure.value(this.array[i], logicalIndex++);
            }
        }
    }

    @Override
    public ImmutableReversibleArrayList<T> newWith(T newItem)
    {
        int oldSize = this.size();
        T[] newArray = (T[]) new Object[oldSize + 1];
        this.toArray(newArray);
        newArray[oldSize] = newItem;
        return new ImmutableReversibleArrayList<>(newArray);
    }

    @Override
    public ImmutableReversibleArrayList<T> toReversed()
    {
        if (this.size <= 1)
        {
            return this;
        }
        return new ImmutableReversibleArrayList<>(this.array, this.lastPhysicalIndex(), this.size, !this.ascending);
    }

    @Override
    public ImmutableReversibleArrayList<T> subList(int fromIndex, int toIndex)
    {
        if (fromIndex < 0)
        {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > this.size)
        {
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }
        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex == toIndex)
        {
            return ImmutableReversibleArrayList.empty();
        }
        if (fromIndex == 0 && toIndex == this.size)
        {
            return this;
        }
        return new ImmutableReversibleArrayList<>(
                this.array, this.physicalIndex(fromIndex), toIndex - fromIndex, this.ascending);
    }

    @Override
    public int binarySearch(T key, Comparator<? super T> comparator)
    {
        int startInclusive;
        int endExclusive;
        Comparator<? super T> actualComparator;

        if (this.ascending)
        {
            startInclusive = this.from;
            endExclusive = this.physicalIndex(this.size);
            actualComparator = comparator;
        }
        else
        {
            startInclusive = this.lastPhysicalIndex();
            endExclusive = this.from + 1;
            actualComparator = Collections.reverseOrder(comparator);
        }
        int result = Arrays.binarySearch(this.array, startInclusive, endExclusive, key, actualComparator);

        if (result >= 0)
        {
            return this.logicalIndex(result);
        }
        int physicalInsertionPoint = -(result + 1);
        int logicalInsertionPoint = this.ascending
                ? physicalInsertionPoint - startInclusive
                : endExclusive - physicalInsertionPoint;
        return -(logicalInsertionPoint + 1);
    }

    private Object writeReplace()
    {
        if (this.size == 0)
        {
            return this;
        }
        if (this.from == 0 && this.ascending && this.size == this.array.length)
        {
            return this;
        }
        T[] normalized = (T[]) this.toArray();
        return new ImmutableReversibleArrayList<>(normalized);
    }

    @Override
    public Object[] toArray()
    {
        if (this.ascending)
        {
            return Arrays.copyOfRange(this.array, this.from, this.physicalIndex(this.size));
        }
        Object[] result = new Object[this.size];
        int logicalIndex = 0;
        for (int i = this.from; i > this.physicalIndex(this.size); i--)
        {
            result[logicalIndex++] = this.array[i];
        }
        return result;
    }

    @Override
    public <E> E[] toArray(E[] a)
    {
        E[] result = a.length >= this.size
                ? a
                : (E[]) Array.newInstance(a.getClass().getComponentType(), this.size);

        if (this.ascending)
        {
            System.arraycopy(this.array, this.from, result, 0, this.size);
        }
        else
        {
            int logicalIndex = 0;
            for (int i = this.from; i > this.physicalIndex(this.size); i--)
            {
                result[logicalIndex++] = (E) this.array[i];
            }
        }
        if (result.length > this.size)
        {
            result[this.size] = null;
        }
        return result;
    }
}
