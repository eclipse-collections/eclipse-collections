/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.util.Iterator;

/**
 * An unmodifiable wrapper for an Iterator that prevents modification operations.
 * <p>
 * This adapter delegates all read operations to the underlying iterator but throws
 * {@link UnsupportedOperationException} for the {@code remove()} operation, making
 * the iterator effectively read-only.
 * </p>
 * <p>
 * Thread Safety: This class does not add any synchronization. Thread safety depends
 * on the thread safety of the underlying iterator.
 * </p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * Iterator<String> mutableIterator = list.iterator();
 * Iterator<String> readOnlyIterator = new UnmodifiableIteratorAdapter<>(mutableIterator);
 * // Can iterate but cannot remove
 * }</pre>
 *
 * @param <E> the type of elements returned by this iterator
 * @since 1.0
 */
public class UnmodifiableIteratorAdapter<E>
        implements Iterator<E>
{
    private final Iterator<? extends E> iterator;

    /**
     * Constructs an unmodifiable adapter for the specified iterator.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Iterator<Integer> original = list.iterator();
     * UnmodifiableIteratorAdapter<Integer> unmodifiable =
     *     new UnmodifiableIteratorAdapter<>(original);
     * }</pre>
     *
     * @param iterator the iterator to wrap
     */
    public UnmodifiableIteratorAdapter(Iterator<? extends E> iterator)
    {
        this.iterator = iterator;
    }

    /**
     * Returns {@code true} if the underlying iterator has more elements.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * UnmodifiableIteratorAdapter<String> iterator = ...;
     * while (iterator.hasNext()) {
     *     String element = iterator.next();
     *     // Process element
     * }
     * }</pre>
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext()
    {
        return this.iterator.hasNext();
    }

    /**
     * Returns the next element from the underlying iterator.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * UnmodifiableIteratorAdapter<Integer> iterator = ...;
     * if (iterator.hasNext()) {
     *     Integer value = iterator.next();
     * }
     * }</pre>
     *
     * @return the next element in the iteration
     * @throws java.util.NoSuchElementException if the iteration has no more elements
     */
    @Override
    public E next()
    {
        return this.iterator.next();
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this is an unmodifiable iterator.
     * <p>
     * This operation is not supported to prevent modification through this iterator.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * UnmodifiableIteratorAdapter<String> iterator = ...;
     * try {
     *     iterator.remove();  // throws exception
     * } catch (UnsupportedOperationException e) {
     *     // expected behavior
     * }
     * }</pre>
     *
     * @throws UnsupportedOperationException always, since this iterator is unmodifiable
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }
}
