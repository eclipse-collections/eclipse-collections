/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.stack.mutable;

import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface UnmodifiableMutableStackTestCase extends MutableStackTestCase, FixedSizeIterableTestCase
{
    @Override
    @Test
    default void Iterable_remove()
    {
        FixedSizeIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void MutableStack_pop()
    {
        MutableStack<Integer> mutableStack = this.newWith(5, 1, 4, 2, 3);
        assertThrows(UnsupportedOperationException.class, () -> mutableStack.pop());
    }

    @Override
    @Test
    default void MutableStack_pop_throws()
    {
        MutableStack<Integer> mutableStack = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> mutableStack.pop());
    }

    @Override
    @Test
    default void RichIterable_makeString_appendString()
    {
        MutableStack<Object> stack = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> stack.push(stack));
    }
}
