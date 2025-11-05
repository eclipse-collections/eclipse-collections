/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.list;

import java.util.Comparator;
import java.util.Random;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.procedure.Procedure;

/**
 * A MultiReaderList provides thread-safe iteration for a list through methods {@code withReadLockAndDelegate()} and {@code withWriteLockAndDelegate()}.
 * This interface uses a read-write lock to allow multiple concurrent readers or a single writer, providing better performance
 * than simple synchronization when reads are more frequent than writes.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MultiReaderList<String> list = MultiReaderFastList.newListWith("A", "B", "C");
 *
 * // Safe read access - multiple threads can read concurrently
 * list.withReadLockAndDelegate(delegate -> {
 *     delegate.forEach(System.out::println);
 *     int size = delegate.size();
 * });
 *
 * // Safe write access - exclusive lock for modifications
 * list.withWriteLockAndDelegate(delegate -> {
 *     delegate.add("D");
 *     delegate.remove("A");
 * });
 * }</pre>
 *
 * @since 10.0.
 */
public interface MultiReaderList<T>
        extends MutableList<T>
{
    /**
     * Executes the given procedure with a read lock, allowing multiple concurrent readers.
     * The procedure receives a delegate MutableList that should be used for all read operations.
     *
     * @param procedure the procedure to execute with read access to the list
     */
    void withReadLockAndDelegate(Procedure<? super MutableList<T>> procedure);

    /**
     * Executes the given procedure with a write lock, providing exclusive access for modifications.
     * The procedure receives a delegate MutableList that should be used for all write operations.
     *
     * @param procedure the procedure to execute with write access to the list
     */
    void withWriteLockAndDelegate(Procedure<? super MutableList<T>> procedure);

    /**
     * Creates a new empty MultiReaderList of the same type.
     *
     * @return a new empty MultiReaderList
     */
    @Override
    MultiReaderList<T> newEmpty();

    /**
     * Creates and returns a copy of this MultiReaderList.
     *
     * @return a clone of this MultiReaderList
     */
    @Override
    MultiReaderList<T> clone();

    /**
     * Returns a view of the portion of this list between the specified fromIndex (inclusive) and toIndex (exclusive).
     *
     * @param fromIndex low endpoint (inclusive) of the sublist
     * @param toIndex high endpoint (exclusive) of the sublist
     * @return a view of the specified range within this list
     */
    @Override
    MultiReaderList<T> subList(int fromIndex, int toIndex);

    @Override
    default MultiReaderList<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    default MultiReaderList<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    default MultiReaderList<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    default MultiReaderList<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    default MultiReaderList<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    default MultiReaderList<T> sortThis(Comparator<? super T> comparator)
    {
        this.sort(comparator);
        return this;
    }

    @Override
    default MultiReaderList<T> sortThis()
    {
        return this.sortThis(null);
    }

    @Override
    <V extends Comparable<? super V>> MultiReaderList<T> sortThisBy(Function<? super T, ? extends V> function);

    @Override
    MultiReaderList<T> sortThisByInt(IntFunction<? super T> function);

    @Override
    MultiReaderList<T> sortThisByBoolean(BooleanFunction<? super T> function);

    @Override
    MultiReaderList<T> sortThisByChar(CharFunction<? super T> function);

    @Override
    MultiReaderList<T> sortThisByByte(ByteFunction<? super T> function);

    @Override
    MultiReaderList<T> sortThisByShort(ShortFunction<? super T> function);

    @Override
    MultiReaderList<T> sortThisByFloat(FloatFunction<? super T> function);

    @Override
    MultiReaderList<T> sortThisByLong(LongFunction<? super T> function);

    @Override
    MultiReaderList<T> sortThisByDouble(DoubleFunction<? super T> function);

    @Override
    MultiReaderList<T> reverseThis();

    @Override
    MultiReaderList<T> shuffleThis();

    @Override
    MultiReaderList<T> shuffleThis(Random random);
}
