/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.stack.mutable;

import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.SynchronizedRichIterableTestTrait;
import org.junit.jupiter.api.Test;

public class SynchronizedStackTest
        implements SynchronizedRichIterableTestTrait
{
    private final MutableStack<String> classUnderTest = ArrayStack.newStackFromTopToBottom("1", "2", "3").asSynchronized();

    @Override
    public MutableStack<String> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void push()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().push("4"));
    }

    @Test
    public void pop()
    {
        this.assertSynchronized(this.getClassUnderTest()::pop);
    }

    @Test
    public void pop_int()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().pop(2));
    }

    @Test
    public void peek()
    {
        this.assertSynchronized(this.getClassUnderTest()::peek);
    }

    @Test
    public void peek_int()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().peek(2));
    }

    @Test
    public void clear()
    {
        this.assertSynchronized(this.getClassUnderTest()::clear);
    }

    @Test
    public void toImmutable()
    {
        this.assertSynchronized(this.getClassUnderTest()::toImmutable);
    }
}
