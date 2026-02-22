/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.stack.mutable;

import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Stacks;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link ArrayStack}.
 */
public class ArrayStackTest extends MutableStackTestCase
{
    @Override
    protected <T> MutableStack<T> newStackWith(T... elements)
    {
        return Stacks.mutable.of(elements);
    }

    @Override
    protected <T> MutableStack<T> newStackFromTopToBottom(T... elements)
    {
        return Stacks.mutable.ofReversed(elements);
    }

    @Override
    protected <T> MutableStack<T> newStackFromTopToBottom(Iterable<T> elements)
    {
        return Stacks.mutable.ofAllReversed(elements);
    }

    @Override
    protected <T> MutableStack<T> newStack(Iterable<T> elements)
    {
        return Stacks.mutable.ofAll(elements);
    }

    @Test
    public void dropWhile()
    {
        ArrayStack<Object> arrayStack = new ArrayStack<>();
        assertThrows(UnsupportedOperationException.class, () -> arrayStack.dropWhile(null));
    }

    @Test
    public void partitionWhile()
    {
        ArrayStack<Object> arrayStack = new ArrayStack<>();
        assertThrows(UnsupportedOperationException.class, () -> arrayStack.partitionWhile(null));
    }

    @Test
    public void indexOf()
    {
        ArrayStack<Object> arrayStack = new ArrayStack<>();
        assertThrows(UnsupportedOperationException.class, () -> arrayStack.indexOf(null));
    }

    @Test
    public void corresponds()
    {
        ArrayStack<Object> arrayStack = new ArrayStack<>();
        assertThrows(UnsupportedOperationException.class, () -> arrayStack.corresponds(null, null));
    }

    @Test
    public void hasSameElements()
    {
        ArrayStack<Object> arrayStack = new ArrayStack<>();
        assertThrows(UnsupportedOperationException.class, () -> arrayStack.hasSameElements(null));
    }

    @Test
    public void forEach_exception()
    {
        MutableStack<Object> arrayStack = new ArrayStack<>();
        assertThrows(UnsupportedOperationException.class, () -> arrayStack.forEach(0, 1, null));
    }

    @Test
    public void forEachWithIndex_exception()
    {
        MutableStack<Object> arrayStack = new ArrayStack<>();
        assertThrows(UnsupportedOperationException.class, () -> arrayStack.forEachWithIndex(0, 1, null));
    }

    @Test
    public void detectIndex()
    {
        ArrayStack<Object> arrayStack = new ArrayStack<>();
        assertThrows(UnsupportedOperationException.class, () -> arrayStack.detectIndex(null));
    }
}
