/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.fixed;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * This class provides a MutableList wrapper around an array. All the internal iteration methods of the MutableList
 * interface as well as the JDK Collections List interface are provided. However, the pre-determined fixed-sized
 * semantics of an array are maintained and thus mutating List interface methods such as {@link #add(Object)}, {@link
 * #addAll(Collection)}, {@link #remove(Object)}, {@link #removeAll(Collection)}, etc. are not supported and will throw
 * an {@link UnsupportedOperationException}. In addition, the mutating iteration methods
 * {@link #removeIf(org.eclipse.collections.api.block.predicate.Predicate)} and {@link #removeIfWith(org.eclipse.collections.api.block.predicate.Predicate2, Object)} are not supported and will also
 * throw an {@link UnsupportedOperationException}.
 * <p>
 * The {@link #with(Object)} method is not an exception to the above restrictions, as it will create a new
 * instance of this class with the existing contents plus the new item.
 * <p>
 * To create a wrapper around an existing array, use the {@link #adapt(Object[])} factory method. To wrap the contents
 * of an existing Collection instance, use the {@link #newArray(Iterable)} or {@link #newArrayWithItem(Iterable, Object)}
 * factory methods. To wrap existing objects in a new array, use one of the {@link #newArrayWith(Object)} factory methods.
 */
public class ArrayAdapter<T>
        extends AbstractArrayAdapter<T>
        implements Serializable, FixedSizeList<T>
{
    private static final long serialVersionUID = 1L;
    private static final Object[] EMPTY_ARRAY = {};

    protected ArrayAdapter(T[] newElements)
    {
        super(newElements);
    }

    public static <E> ArrayAdapter<E> adapt(E... array)
    {
        return new ArrayAdapter<>(array);
    }

    public static <E> ArrayAdapter<E> newArray()
    {
        return ArrayAdapter.newArrayWith((E[]) EMPTY_ARRAY);
    }

    public static <E> ArrayAdapter<E> newArray(Iterable<? extends E> source)
    {
        return new ArrayAdapter<>((E[]) Iterate.toArray(source));
    }

    /**
     * @since 8.1
     */
    @Override
    public Spliterator<T> spliterator()
    {
        return Spliterators.spliterator(this.items, Spliterator.ORDERED);
    }

    public static <E> ArrayAdapter<E> newArrayWithItem(Iterable<? extends E> iterable, E itemToAdd)
    {
        int oldSize = Iterate.sizeOf(iterable);
        E[] array = (E[]) new Object[oldSize + 1];
        Iterate.toArray(iterable, array);
        array[oldSize] = itemToAdd;
        return new ArrayAdapter<>(array);
    }

    public static <E> ArrayAdapter<E> newArrayWith(E one)
    {
        return new ArrayAdapter<>((E[]) new Object[]{one});
    }

    public static <E> ArrayAdapter<E> newArrayWith(E one, E two)
    {
        return new ArrayAdapter<>((E[]) new Object[]{one, two});
    }

    public static <E> ArrayAdapter<E> newArrayWith(E one, E two, E three)
    {
        return new ArrayAdapter<>((E[]) new Object[]{one, two, three});
    }

    public static <E> ArrayAdapter<E> newArrayWith(E one, E two, E three, E four)
    {
        return new ArrayAdapter<>((E[]) new Object[]{one, two, three, four});
    }

    public static <E> ArrayAdapter<E> newArrayWith(E one, E two, E three, E four, E five)
    {
        return new ArrayAdapter<>((E[]) new Object[]{one, two, three, four, five});
    }

    public static <E> ArrayAdapter<E> newArrayWith(E one, E two, E three, E four, E five, E six)
    {
        return new ArrayAdapter<>((E[]) new Object[]{one, two, three, four, five, six});
    }

    public static <E> ArrayAdapter<E> newArrayWith(E one, E two, E three, E four, E five, E six, E seven)
    {
        return new ArrayAdapter<>((E[]) new Object[]{one, two, three, four, five, six, seven});
    }

    public static <E> ArrayAdapter<E> newArrayWith(E... elements)
    {
        return new ArrayAdapter<>(elements.clone());
    }

    @Override
    public T set(int index, T element)
    {
        T oldValue = this.items[index];
        this.items[index] = element;
        return oldValue;
    }

    @Override
    public ArrayAdapter<T> with(T value)
    {
        return ArrayAdapter.newArrayWithItem(this, value);
    }

    @Override
    public ArrayAdapter<T> without(T element)
    {
        if (this.contains(element))
        {
            return ArrayAdapter.newArray(this.toList().without(element));
        }
        return this;
    }

    @Override
    public ArrayAdapter<T> withAll(Iterable<? extends T> elements)
    {
        if (Iterate.isEmpty(elements))
        {
            return this;
        }
        return ArrayAdapter.newArray(this.toList().withAll(elements));
    }

    @Override
    public ArrayAdapter<T> withoutAll(Iterable<? extends T> elements)
    {
        if (Iterate.isEmpty(elements))
        {
            return this;
        }
        if (Iterate.anySatisfyWith(elements, Predicates2.in(), this))
        {
            return ArrayAdapter.newArray(this.toList().withoutAll(elements));
        }
        return this;
    }

    @Override
    public ArrayAdapter<T> clone()
    {
        return new ArrayAdapter<>(this.items.clone());
    }

    @Override
    public ArrayAdapter<T> sortThis(Comparator<? super T> comparator)
    {
        return (ArrayAdapter<T>) super.sortThis(comparator);
    }

    @Override
    public FixedSizeList<T> tap(Procedure<? super T> procedure)
    {
        this.each(procedure);
        return this;
    }

    @Override
    public FixedSizeList<T> toReversed()
    {
        ArrayAdapter<T> result = this.clone();
        result.reverseThis();
        return result;
    }

    private void writeObject(ObjectOutputStream objectOutputStream)
            throws IOException
    {
        T[] localItems = this.items;
        int size = localItems.length;
        objectOutputStream.writeInt(size);
        for (int i = 0; i < size; i++)
        {
            objectOutputStream.writeObject(localItems[i]);
        }
    }

    private void readObject(ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException
    {
        // Read in array length and allocate array
        int arrayLength = objectInputStream.readInt();
        this.items = (T[]) new Object[arrayLength];
        Object[] localItems = this.items;

        // Read in all elements in the proper order.
        for (int i = 0; i < arrayLength; i++)
        {
            localItems[i] = objectInputStream.readObject();
        }
    }
}
