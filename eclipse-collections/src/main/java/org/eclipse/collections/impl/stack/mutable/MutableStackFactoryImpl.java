/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.stack.mutable;

import java.util.stream.Stream;

import org.eclipse.collections.api.factory.stack.MutableStackFactory;
import org.eclipse.collections.api.stack.MutableStack;

/**
 * The default implementation of {@link MutableStackFactory} that creates {@link ArrayStack} instances.
 * <p>
 * This factory provides various methods to create mutable stacks from different sources including
 * arrays, iterables, and streams. All stacks created by this factory use {@link ArrayStack} as the
 * underlying implementation.
 * </p>
 * <p>
 * This implementation is registered as a service provider for {@link MutableStackFactory}.
 * </p>
 *
 * @see MutableStackFactory
 * @see ArrayStack
 * @since 6.0
 */
@aQute.bnd.annotation.spi.ServiceProvider(MutableStackFactory.class)
public class MutableStackFactoryImpl implements MutableStackFactory
{
    /**
     * The singleton instance of this factory.
     */
    public static final MutableStackFactory INSTANCE = new MutableStackFactoryImpl();

    /**
     * Creates an empty mutable stack.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableStack<String> stack = MutableStackFactoryImpl.INSTANCE.empty();
     * // stack is now an empty ArrayStack
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @return a new empty mutable stack
     */
    @Override
    public <T> MutableStack<T> empty()
    {
        return ArrayStack.newStack();
    }

    /**
     * Creates a new mutable stack containing the specified elements.
     * <p>
     * The elements are pushed onto the stack in the order they appear in the array,
     * so the last element will be at the top of the stack.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableStack<Integer> stack = MutableStackFactoryImpl.INSTANCE.with(1, 2, 3);
     * // stack.peek() returns 3 (top of stack)
     * // stack.pop() returns 3, then stack.peek() returns 2
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @param elements the elements to add to the stack
     * @return a new mutable stack containing the specified elements
     */
    @Override
    public <T> MutableStack<T> with(T... elements)
    {
        return ArrayStack.newStackWith(elements);
    }

    /**
     * Creates a new mutable stack containing all elements from the specified iterable.
     * <p>
     * The elements are added to the stack in the order they appear in the iterable,
     * so the last element will be at the top of the stack.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * MutableStack<String> stack = MutableStackFactoryImpl.INSTANCE.withAll(list);
     * // stack.peek() returns "c" (top of stack)
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @param elements the iterable containing elements to add to the stack
     * @return a new mutable stack containing the elements from the iterable
     */
    @Override
    public <T> MutableStack<T> withAll(Iterable<? extends T> elements)
    {
        return ArrayStack.newStack(elements);
    }

    /**
     * Creates a new mutable stack from the elements of a stream.
     * <p>
     * Elements are pushed onto the stack in the order they are encountered in the stream.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
     * MutableStack<Integer> stack = MutableStackFactoryImpl.INSTANCE.fromStream(stream);
     * // Elements are pushed in stream order
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @param stream the stream containing elements to add to the stack
     * @return a new mutable stack containing the elements from the stream
     */
    @Override
    public <T> MutableStack<T> fromStream(Stream<? extends T> stream)
    {
        MutableStack<T> stack = ArrayStack.newStack();
        stream.forEach(stack::push);
        return stack;
    }

    /**
     * Creates a new mutable stack containing the specified elements in reverse order.
     * <p>
     * The elements are pushed onto the stack in reverse order, so the first element in the
     * array will be at the top of the stack.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableStack<Integer> stack = MutableStackFactoryImpl.INSTANCE.withReversed(1, 2, 3);
     * // stack.peek() returns 1 (first element is now at top)
     * // stack.pop() returns 1, then stack.peek() returns 2
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @param elements the elements to add to the stack in reverse order
     * @return a new mutable stack containing the specified elements in reverse order
     */
    @Override
    public <T> MutableStack<T> withReversed(T... elements)
    {
        return ArrayStack.newStackFromTopToBottom(elements);
    }

    /**
     * Creates a new mutable stack containing all elements from the specified iterable in reverse order.
     * <p>
     * The elements are added to the stack in reverse order, so the first element in the iterable
     * will be at the top of the stack.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * MutableStack<String> stack = MutableStackFactoryImpl.INSTANCE.withAllReversed(list);
     * // stack.peek() returns "a" (first element is now at top)
     * }</pre>
     *
     * @param <T> the type of elements in the stack
     * @param items the iterable containing elements to add to the stack in reverse order
     * @return a new mutable stack containing the elements from the iterable in reverse order
     */
    @Override
    public <T> MutableStack<T> withAllReversed(Iterable<? extends T> items)
    {
        return ArrayStack.newStackFromTopToBottom(items);
    }
}
