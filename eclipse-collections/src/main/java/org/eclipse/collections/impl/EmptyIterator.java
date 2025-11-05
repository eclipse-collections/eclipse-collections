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

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A Singleton iterator that is always empty and can be shared by all empty collections.
 * <p>
 * This immutable iterator implements {@link ListIterator} but always reports no elements
 * and throws exceptions for mutating operations. It provides a memory-efficient way to
 * return an iterator for empty collections without creating new instances.
 * </p>
 * <p>
 * Thread Safety: This class is thread-safe as it is immutable and stateless.
 * </p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * Iterator<String> emptyIterator = EmptyIterator.getInstance();
 * boolean hasNext = emptyIterator.hasNext();  // always returns false
 * }</pre>
 *
 * @param <T> the type of elements (though there are no elements)
 * @since 1.0
 */
public final class EmptyIterator<T>
        implements ListIterator<T>
{
    private static final EmptyIterator<?> INSTANCE = new EmptyIterator<>();

    private EmptyIterator()
    {
    }

    /**
     * Returns the singleton instance of EmptyIterator.
     * <p>
     * This method returns a shared instance that can be used by any empty collection,
     * regardless of the element type. The same instance is safely reused as the iterator
     * has no mutable state.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> stringIterator = EmptyIterator.getInstance();
     * EmptyIterator<Integer> intIterator = EmptyIterator.getInstance();
     * }</pre>
     *
     * @param <T> the type of elements for the iterator
     * @return the singleton empty iterator instance
     */
    public static <T> EmptyIterator<T> getInstance()
    {
        return (EmptyIterator<T>) INSTANCE;
    }

    /**
     * Always returns {@code false} since this iterator has no elements.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> iterator = EmptyIterator.getInstance();
     * while (iterator.hasNext()) {  // never executes
     *     // unreachable code
     * }
     * }</pre>
     *
     * @return {@code false}
     */
    @Override
    public boolean hasNext()
    {
        return false;
    }

    /**
     * Always throws {@link NoSuchElementException} since there are no elements to return.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> iterator = EmptyIterator.getInstance();
     * try {
     *     String element = iterator.next();  // throws exception
     * } catch (NoSuchElementException e) {
     *     // expected behavior
     * }
     * }</pre>
     *
     * @return never returns
     * @throws NoSuchElementException always, since the iterator is empty
     */
    @Override
    public T next()
    {
        throw new NoSuchElementException();
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this is an unmodifiable iterator.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> iterator = EmptyIterator.getInstance();
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

    /**
     * Always returns {@code false} since this iterator has no elements to iterate backwards.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> iterator = EmptyIterator.getInstance();
     * boolean hasPrev = iterator.hasPrevious();  // always returns false
     * }</pre>
     *
     * @return {@code false}
     */
    @Override
    public boolean hasPrevious()
    {
        return false;
    }

    /**
     * Always throws {@link NoSuchElementException} since there are no previous elements.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> iterator = EmptyIterator.getInstance();
     * try {
     *     String element = iterator.previous();  // throws exception
     * } catch (NoSuchElementException e) {
     *     // expected behavior
     * }
     * }</pre>
     *
     * @return never returns
     * @throws NoSuchElementException always, since the iterator is empty
     */
    @Override
    public T previous()
    {
        throw new NoSuchElementException();
    }

    /**
     * Always returns 0 since there are no elements and the iterator position is at the start.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> iterator = EmptyIterator.getInstance();
     * int index = iterator.nextIndex();  // always returns 0
     * }</pre>
     *
     * @return 0
     */
    @Override
    public int nextIndex()
    {
        return 0;
    }

    /**
     * Always returns -1 since there are no elements before the current position.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> iterator = EmptyIterator.getInstance();
     * int index = iterator.previousIndex();  // always returns -1
     * }</pre>
     *
     * @return -1
     */
    @Override
    public int previousIndex()
    {
        return -1;
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this is an unmodifiable iterator.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> iterator = EmptyIterator.getInstance();
     * try {
     *     iterator.set("value");  // throws exception
     * } catch (UnsupportedOperationException e) {
     *     // expected behavior
     * }
     * }</pre>
     *
     * @param t the element (ignored)
     * @throws UnsupportedOperationException always, since this iterator is unmodifiable
     */
    @Override
    public void set(T t)
    {
        throw new UnsupportedOperationException("Cannot call set() on " + this.getClass().getSimpleName());
    }

    /**
     * Always throws {@link UnsupportedOperationException} since this is an unmodifiable iterator.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * EmptyIterator<String> iterator = EmptyIterator.getInstance();
     * try {
     *     iterator.add("value");  // throws exception
     * } catch (UnsupportedOperationException e) {
     *     // expected behavior
     * }
     * }</pre>
     *
     * @param t the element (ignored)
     * @throws UnsupportedOperationException always, since this iterator is unmodifiable
     */
    @Override
    public void add(T t)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }
}
