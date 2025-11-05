/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag;

import org.eclipse.collections.api.block.procedure.Procedure;

/**
 * A MultiReaderBag provides thread-safe iteration for a bag through methods {@code withReadLockAndDelegate()}
 * and {@code withWriteLockAndDelegate()}. It uses a read-write lock to allow multiple concurrent readers
 * or a single writer, ensuring safe concurrent access to the bag.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MultiReaderBag<String> bag = MultiReaderBag.newBag();
 * bag.add("A");
 * bag.withReadLockAndDelegate(innerBag -> {
 *     // Safe concurrent read operations
 *     innerBag.forEach(System.out::println);
 * });
 * bag.withWriteLockAndDelegate(innerBag -> {
 *     // Exclusive write operations
 *     innerBag.add("B");
 * });
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 * @since 10.0.
 */
public interface MultiReaderBag<T>
        extends MutableBag<T>
{
    void withReadLockAndDelegate(Procedure<? super MutableBag<T>> procedure);

    void withWriteLockAndDelegate(Procedure<? super MutableBag<T>> procedure);

    @Override
    MultiReaderBag<T> newEmpty();

    @Override
    default MultiReaderBag<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    default MultiReaderBag<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    default MultiReaderBag<T> withOccurrences(T element, int occurrences)
    {
        this.addOccurrences(element, occurrences);
        return this;
    }

    @Override
    default MultiReaderBag<T> withoutOccurrences(T element, int occurrences)
    {
        this.removeOccurrences(element, occurrences);
        return this;
    }

    @Override
    default MultiReaderBag<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    default MultiReaderBag<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    default MultiReaderBag<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }
}
