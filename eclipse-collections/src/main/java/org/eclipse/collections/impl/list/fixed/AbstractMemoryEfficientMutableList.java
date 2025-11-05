/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.fixed;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.RandomAccess;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.list.mutable.AbstractMutableList;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * AbstractMemoryEfficientMutableList is an abstract base class for memory-efficient fixed-size lists.
 * This class provides a skeletal implementation of a list with a fixed size that cannot be changed.
 * <p>
 * This implementation provides random access capability and throws {@link UnsupportedOperationException}
 * for all mutating operations that would change the size of the list (add, remove, clear).
 * However, individual elements can be modified via {@link #set(int, Object)} to support sorting.
 * </p>
 * <p><b>Thread Safety:</b> This class is not thread-safe and does not provide synchronization.</p>
 * <p><b>Performance:</b> Random access operations run in O(1) time.</p>
 *
 * @param <T> the type of elements maintained by this list
 */
public abstract class AbstractMemoryEfficientMutableList<T>
        extends AbstractMutableList<T>
        implements FixedSizeList<T>, RandomAccess
{
    /**
     * Creates and returns a shallow copy of this fixed-size list.
     * The elements themselves are not cloned.
     *
     * @return a clone of this list instance
     * @throws CloneNotSupportedException if the object's class does not support cloning
     */
    @Override
    public FixedSizeList<T> clone()
    {
        return (FixedSizeList<T>) super.clone();
    }

    /**
     * Executes the given procedure on each element in the list and returns this list.
     * This method is useful for chaining operations.
     *
     * @param procedure the procedure to execute for each element
     * @return this list to allow method chaining
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeList<String> list = Lists.fixedSize.of("a", "b", "c")
     *     .tap(System.out::println);
     * }</pre>
     */
    @Override
    public FixedSizeList<T> tap(Procedure<? super T> procedure)
    {
        this.each(procedure);
        return this;
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param o element whose presence in this collection is to be ensured
     * @return never returns normally
     * @throws UnsupportedOperationException always, as adding elements would change the list size
     */
    @Override
    public boolean add(T o)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws UnsupportedOperationException always, as adding elements would change the list size
     */
    @Override
    public void add(int index, T element)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param collection collection containing elements to be added to this list
     * @return never returns normally
     * @throws UnsupportedOperationException always, as adding elements would change the list size
     */
    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param index index at which to insert the first element from the specified collection
     * @param collection collection containing elements to be added to this list
     * @return never returns normally
     * @throws UnsupportedOperationException always, as adding elements would change the list size
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param iterable iterable containing elements to be added to this list
     * @return never returns normally
     * @throws UnsupportedOperationException always, as adding elements would change the list size
     */
    @Override
    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param o element to be removed from this list, if present
     * @return never returns normally
     * @throws UnsupportedOperationException always, as removing elements would change the list size
     */
    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param index the index of the element to be removed
     * @return never returns normally
     * @throws UnsupportedOperationException always, as removing elements would change the list size
     */
    @Override
    public T remove(int index)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param collection collection containing elements to be removed from this list
     * @return never returns normally
     * @throws UnsupportedOperationException always, as removing elements would change the list size
     */
    @Override
    public boolean removeAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param iterable iterable containing elements to be removed from this list
     * @return never returns normally
     * @throws UnsupportedOperationException always, as removing elements would change the list size
     */
    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param predicate predicate to evaluate elements against
     * @return never returns normally
     * @throws UnsupportedOperationException always, as removing elements would change the list size
     */
    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param predicate predicate to evaluate elements against
     * @param parameter parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return never returns normally
     * @throws UnsupportedOperationException always, as removing elements would change the list size
     */
    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        throw new UnsupportedOperationException("Cannot removeIfWith from a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param collection collection containing elements to be retained in this list
     * @return never returns normally
     * @throws UnsupportedOperationException always, as removing elements would change the list size
     */
    @Override
    public boolean retainAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @param iterable iterable containing elements to be retained in this list
     * @return never returns normally
     * @throws UnsupportedOperationException always, as removing elements would change the list size
     */
    @Override
    public boolean retainAllIterable(Iterable<?> iterable)
    {
        throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
    }

    /**
     * This operation is not supported on a fixed-size list.
     *
     * @throws UnsupportedOperationException always, as clearing would change the list size
     */
    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot clear a fixed size list: " + this.getClass());
    }

    /**
     * Sorts this list in place using the specified comparator. If the comparator is null,
     * a natural ordering comparator is used. This method uses insertion sort, which is efficient
     * for small fixed-size lists.
     * <p>
     * This is one of the few mutating operations allowed on a fixed-size list because it doesn't
     * change the list size, only the order of elements.
     * </p>
     * <p><b>Time Complexity:</b> O(n²) where n is the size of the list.</p>
     *
     * @param comparator the comparator to determine the order of elements, or null for natural ordering
     * @since 10.0
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeList<String> list = Lists.fixedSize.of("c", "a", "b");
     * list.sort(Comparator.naturalOrder());
     * // list is now ["a", "b", "c"]
     * }</pre>
     */
    @Override
    public void sort(Comparator<? super T> comparator)
    {
        this.insertionSort(Comparators.comparableComparatorIfNull(comparator));
    }

    private void insertionSort(Comparator<? super T> comparator)
    {
        for (int i = 0; i < this.size(); i++)
        {
            for (int j = i; this.isPreviousGreaterThanCurrent(comparator, j); j--)
            {
                Collections.swap(this, j - 1, j);
            }
        }
    }

    private boolean isPreviousGreaterThanCurrent(Comparator<? super T> comparator, int index)
    {
        return index > 0 && comparator.compare(this.get(index - 1), this.get(index)) > 0;
    }

    /**
     * Returns a new fixed-size list with the elements of this list in reverse order.
     * This method does not modify the original list.
     *
     * @return a new fixed-size list containing the elements in reverse order
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeList<String> list = Lists.fixedSize.of("a", "b", "c");
     * FixedSizeList<String> reversed = list.toReversed();
     * // reversed is ["c", "b", "a"], original list unchanged
     * }</pre>
     */
    @Override
    public FixedSizeList<T> toReversed()
    {
        FixedSizeList<T> result = Lists.fixedSize.withAll(this);
        result.reverseThis();
        return result;
    }

    /**
     * Returns a view of the portion of this list between the specified fromIndex (inclusive)
     * and toIndex (exclusive). The returned sublist is backed by this list, so changes in the
     * sublist are reflected in this list, and vice-versa.
     * <p>
     * The returned sublist maintains the fixed-size semantics: it will throw
     * {@link UnsupportedOperationException} for operations that would change its size.
     * </p>
     *
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException if fromIndex or toIndex is out of range
     * @throws IllegalArgumentException if fromIndex > toIndex
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeList<String> list = Lists.fixedSize.of("a", "b", "c", "d");
     * MutableList<String> sub = list.subList(1, 3);
     * // sub is ["b", "c"]
     * }</pre>
     */
    @Override
    public MutableList<T> subList(int fromIndex, int toIndex)
    {
        return new SubList<>(this, fromIndex, toIndex);
    }

    /**
     * Returns a new list with the specified element removed. If this list does not contain
     * the element, returns this list unchanged. Since this is a fixed-size list, a new list
     * with a different size is created if the element is present.
     *
     * @param element element to be removed from this list, if present
     * @return a new list without the specified element, or this list if element not found
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeList<String> list = Lists.fixedSize.of("a", "b", "c");
     * MutableList<String> result = list.without("b");
     * // result is a new list ["a", "c"] with size 2
     * }</pre>
     */
    @Override
    public MutableList<T> without(T element)
    {
        if (this.contains(element))
        {
            return Lists.fixedSize.ofAll(this.toList().without(element));
        }
        return this;
    }

    /**
     * Returns a new list with all elements from this list plus all elements from the specified iterable.
     * If the iterable is empty, returns this list unchanged. Since this is a fixed-size list,
     * a new list with a different size is created if elements are added.
     *
     * @param elements iterable containing elements to be added to this list
     * @return a new list with additional elements, or this list if no elements to add
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeList<String> list = Lists.fixedSize.of("a", "b");
     * MutableList<String> result = list.withAll(Lists.mutable.of("c", "d"));
     * // result is a new list ["a", "b", "c", "d"] with size 4
     * }</pre>
     */
    @Override
    public MutableList<T> withAll(Iterable<? extends T> elements)
    {
        if (Iterate.isEmpty(elements))
        {
            return this;
        }
        return Lists.fixedSize.ofAll(this.toList().withAll(elements));
    }

    /**
     * Returns a new list with all elements from this list except those in the specified iterable.
     * If the iterable is empty, returns this list unchanged. Since this is a fixed-size list,
     * a new list with a different size is created if elements are removed.
     *
     * @param elements iterable containing elements to be removed from this list
     * @return a new list without the specified elements, or this list if no elements to remove
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * FixedSizeList<String> list = Lists.fixedSize.of("a", "b", "c", "d");
     * MutableList<String> result = list.withoutAll(Lists.mutable.of("b", "d"));
     * // result is a new list ["a", "c"] with size 2
     * }</pre>
     */
    @Override
    public MutableList<T> withoutAll(Iterable<? extends T> elements)
    {
        if (Iterate.isEmpty(elements))
        {
            return this;
        }
        return Lists.fixedSize.ofAll(this.toList().withoutAll(elements));
    }

    private static class SubList<T>
            extends AbstractMutableList.SubList<T>
    {
        // Not important since it uses writeReplace()
        private static final long serialVersionUID = 1L;

        protected SubList(AbstractMutableList<T> list, int fromIndex, int toIndex)
        {
            super(list, fromIndex, toIndex);
        }

        @Override
        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean removeAllIterable(Iterable<?> iterable)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean retainAllIterable(Iterable<?> iterable)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean removeIf(Predicate<? super T> predicate)
        {
            throw new UnsupportedOperationException("Cannot remove from a fixed size list: " + this.getClass());
        }

        @Override
        public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
        {
            throw new UnsupportedOperationException("Cannot removeIfWith from a fixed size list: " + this.getClass());
        }

        @Override
        public boolean addAll(Collection<? extends T> collection)
        {
            throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
        }

        @Override
        public boolean addAllIterable(Iterable<? extends T> iterable)
        {
            throw new UnsupportedOperationException("Cannot add to a fixed size list: " + this.getClass());
        }
    }

    /**
     * Returns a list iterator over the elements in this list (in proper sequence),
     * starting at the specified position in the list. The returned iterator will throw
     * {@link UnsupportedOperationException} for operations that would change the list size.
     *
     * @param index index of the first element to be returned from the list iterator
     * @return a list iterator over the elements in this list starting at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public ListIterator<T> listIterator(int index)
    {
        return new FixedSizeListIteratorAdapter<>(super.listIterator(index));
    }

    /**
     * Returns a list iterator over the elements in this list (in proper sequence).
     * The returned iterator will throw {@link UnsupportedOperationException} for operations
     * that would change the list size.
     *
     * @return a list iterator over the elements in this list
     */
    @Override
    public ListIterator<T> listIterator()
    {
        return new FixedSizeListIteratorAdapter<>(super.listIterator());
    }
}
