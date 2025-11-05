/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.stack.immutable;

import org.eclipse.collections.api.factory.stack.ImmutableStackFactory;
import org.eclipse.collections.api.stack.ImmutableStack;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * The default implementation of {@link ImmutableStackFactory} that creates immutable stack instances.
 * <p>
 * This factory provides various methods to create immutable stacks from different sources including
 * single elements, arrays, and iterables. Empty stacks are represented by a singleton
 * {@link ImmutableEmptyStack} instance, while non-empty stacks use {@link ImmutableNotEmptyStack}.
 * </p>
 * <p>
 * Immutable stacks are thread-safe and their contents cannot be modified after creation. Operations
 * that would modify the stack (like push) return new immutable stack instances instead.
 * </p>
 * <p>
 * This implementation is registered as a service provider for {@link ImmutableStackFactory}.
 * </p>
 *
 * @see ImmutableStackFactory
 * @see ImmutableEmptyStack
 * @see ImmutableNotEmptyStack
 * @since 1.0
 */
@aQute.bnd.annotation.spi.ServiceProvider(ImmutableStackFactory.class)
public class ImmutableStackFactoryImpl implements ImmutableStackFactory
{
    /**
     * The singleton instance of this factory.
     */
    public static final ImmutableStackFactory INSTANCE = new ImmutableStackFactoryImpl();

    /**
     * Creates an empty immutable stack.
     * <p>
     * Returns the singleton empty stack instance for efficiency.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableStack<String> stack = ImmutableStackFactoryImpl.INSTANCE.empty();
     * // stack.isEmpty() returns true
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @return an empty immutable stack
     */
    @Override
    public <T> ImmutableStack<T> empty()
    {
        return (ImmutableStack<T>) ImmutableEmptyStack.INSTANCE;
    }

    /**
     * Creates an empty immutable stack.
     * <p>
     * This is an alias for {@link #empty()}.
     * </p>
     *
     * @param <T> the type of elements in the stack
     * @return an empty immutable stack
     */
    @Override
    public <T> ImmutableStack<T> of()
    {
        return this.empty();
    }

    /**
     * Creates an empty immutable stack.
     * <p>
     * This is an alias for {@link #empty()}.
     * </p>
     *
     * @param <T> the type of elements in the stack
     * @return an empty immutable stack
     */
    @Override
    public <T> ImmutableStack<T> with()
    {
        return this.empty();
    }

    /**
     * Creates an immutable stack containing a single element.
     * <p>
     * This is an alias for {@link #with(Object)}.
     * </p>
     *
     * @param <T> the type of the element
     * @param element the element to add to the stack
     * @return an immutable stack containing the single element
     */
    @Override
    public <T> ImmutableStack<T> of(T element)
    {
        return this.with(element);
    }

    /**
     * Creates an immutable stack containing a single element.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableStack<String> stack = ImmutableStackFactoryImpl.INSTANCE.with("hello");
     * // stack.peek() returns "hello"
     * // stack.size() returns 1
     * }</pre>
     *
     * @param <T> the type of the element
     * @param element the element to add to the stack
     * @return an immutable stack containing the single element
     */
    @Override
    public <T> ImmutableStack<T> with(T element)
    {
        return new ImmutableNotEmptyStack<>(element, this.empty());
    }

    /**
     * Creates an immutable stack containing the specified elements.
     * <p>
     * This is an alias for {@link #with(Object[])}.
     * </p>
     *
     * @param <T> the type of elements in the stack
     * @param elements the elements to add to the stack
     * @return an immutable stack containing the specified elements
     */
    @Override
    public <T> ImmutableStack<T> of(T... elements)
    {
        return this.with(elements);
    }

    /**
     * Creates an immutable stack containing the specified elements.
     * <p>
     * Elements are pushed onto the stack in the order they appear in the array,
     * so the last element will be at the top of the stack.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableStack<Integer> stack = ImmutableStackFactoryImpl.INSTANCE.with(1, 2, 3);
     * // stack.peek() returns 3 (top of stack)
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @param elements the elements to add to the stack
     * @return an immutable stack containing the specified elements
     */
    @Override
    public <T> ImmutableStack<T> with(T... elements)
    {
        ImmutableStack<T> result = this.empty();
        for (T element : elements)
        {
            result = result.push(element);
        }
        return result;
    }

    /**
     * Creates an immutable stack containing all elements from the specified iterable.
     * <p>
     * This is an alias for {@link #withAll(Iterable)}.
     * </p>
     *
     * @param <T> the type of elements in the stack
     * @param items the iterable containing elements to add to the stack
     * @return an immutable stack containing the elements from the iterable
     */
    @Override
    public <T> ImmutableStack<T> ofAll(Iterable<? extends T> items)
    {
        return this.withAll(items);
    }

    /**
     * Creates an immutable stack containing all elements from the specified iterable.
     * <p>
     * Elements are pushed onto the stack in the order they appear in the iterable,
     * so the last element will be at the top of the stack.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * ImmutableStack<String> stack = ImmutableStackFactoryImpl.INSTANCE.withAll(list);
     * // stack.peek() returns "c" (top of stack)
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @param items the iterable containing elements to add to the stack
     * @return an immutable stack containing the elements from the iterable
     */
    @Override
    public <T> ImmutableStack<T> withAll(Iterable<? extends T> items)
    {
        ImmutableStack<T> result = this.empty();
        for (T item : items)
        {
            result = result.push(item);
        }
        return result;
    }

    /**
     * Creates an immutable stack containing the specified elements in reverse order.
     * <p>
     * This is an alias for {@link #withReversed(Object[])}.
     * </p>
     *
     * @param <T> the type of elements in the stack
     * @param elements the elements to add to the stack in reverse order
     * @return an immutable stack containing the specified elements in reverse order
     */
    @Override
    public <T> ImmutableStack<T> ofReversed(T... elements)
    {
        return this.withReversed(elements);
    }

    /**
     * Creates an immutable stack containing the specified elements in reverse order.
     * <p>
     * Elements are pushed onto the stack in reverse order, so the first element in the
     * array will be at the top of the stack.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * ImmutableStack<Integer> stack = ImmutableStackFactoryImpl.INSTANCE.withReversed(1, 2, 3);
     * // stack.peek() returns 1 (first element is now at top)
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @param elements the elements to add to the stack in reverse order
     * @return an immutable stack containing the specified elements in reverse order
     */
    @Override
    public <T> ImmutableStack<T> withReversed(T... elements)
    {
        ImmutableStack<T> result = this.empty();
        for (int i = elements.length - 1; i >= 0; i--)
        {
            T element = elements[i];
            result = result.push(element);
        }
        return result;
    }

    /**
     * Creates an immutable stack containing all elements from the specified iterable in reverse order.
     * <p>
     * This is an alias for {@link #withAllReversed(Iterable)}.
     * </p>
     *
     * @param <T> the type of elements in the stack
     * @param items the iterable containing elements to add to the stack in reverse order
     * @return an immutable stack containing the elements from the iterable in reverse order
     */
    @Override
    public <T> ImmutableStack<T> ofAllReversed(Iterable<? extends T> items)
    {
        return this.withAllReversed(items);
    }

    /**
     * Creates an immutable stack containing all elements from the specified iterable in reverse order.
     * <p>
     * Elements are pushed onto the stack in reverse order, so the first element in the iterable
     * will be at the top of the stack.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * ImmutableStack<String> stack = ImmutableStackFactoryImpl.INSTANCE.withAllReversed(list);
     * // stack.peek() returns "a" (first element is now at top)
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @param items the iterable containing elements to add to the stack in reverse order
     * @return an immutable stack containing the elements from the iterable in reverse order
     */
    @Override
    public <T> ImmutableStack<T> withAllReversed(Iterable<? extends T> items)
    {
        return this.withReversed((T[]) Iterate.toArray(items));
    }
}
