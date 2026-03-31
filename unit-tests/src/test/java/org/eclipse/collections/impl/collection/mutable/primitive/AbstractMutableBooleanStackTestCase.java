/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable.primitive;

import java.util.EmptyStackException;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.stack.primitive.MutableBooleanStack;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.stack.mutable.primitive.SynchronizedBooleanStack;
import org.eclipse.collections.impl.stack.mutable.primitive.UnmodifiableBooleanStack;
import org.eclipse.collections.impl.stack.primitive.AbstractBooleanStackTestCase;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract JUnit test for {@link MutableBooleanStack}.
 */
public abstract class AbstractMutableBooleanStackTestCase extends AbstractBooleanStackTestCase
{
    @Override
    protected abstract MutableBooleanStack classUnderTest();

    @Override
    protected abstract MutableBooleanStack newWith(boolean... elements);

    @Override
    protected abstract MutableBooleanStack newMutableCollectionWith(boolean... elements);

    @Override
    protected abstract MutableBooleanStack newWithTopToBottom(boolean... elements);

    protected abstract MutableBooleanStack newWithIterableTopToBottom(BooleanIterable iterable);

    protected abstract MutableBooleanStack newWithIterable(BooleanIterable iterable);

    @Override
    @Test
    public void peekAtIndex()
    {
        super.peekAtIndex();
        MutableBooleanStack stack = this.classUnderTest();
        stack.pop(2);
        assertEquals((this.classUnderTest().size() & 1) != 0, stack.peekAt(0));
    }

    @Override
    @Test
    public void peek()
    {
        super.peek();
        MutableBooleanStack stack = this.classUnderTest();
        int size = this.classUnderTest().size();
        for (int i = 0; i < size; i++)
        {
            assertEquals((i & 1) != 0, stack.peek());
            stack.pop();
        }
    }

    @Test
    public void peekWithCount()
    {
        MutableBooleanStack stack = this.classUnderTest();
        assertEquals(BooleanArrayList.newListWith(false, true), stack.peek(2));
        stack.pop(2);
        assertEquals(BooleanArrayList.newListWith(false), stack.peek(1));
    }

    @Test
    public void peek_empty_stack_throws_exception()
    {
        assertThrows(EmptyStackException.class, () -> this.newWith().peek());
    }

    @Test
    public void testNewStackWithOrder()
    {
        MutableBooleanStack stack = this.newWith(true, false, true, true);
        assertTrue(stack.pop());
        assertTrue(stack.pop());
        assertFalse(stack.pop());
        assertTrue(stack.pop());
    }

    @Test
    public void testNewStackIterableOrder()
    {
        MutableBooleanStack stack = this.newWithIterable(BooleanArrayList.newListWith(true, false, true, true));
        assertTrue(stack.pop());
        assertTrue(stack.pop());
        assertFalse(stack.pop());
        assertTrue(stack.pop());
    }

    @Test
    public void testNewStackFromTopToBottomOrder()
    {
        MutableBooleanStack stack = this.newWithTopToBottom(false, true, true);
        assertFalse(stack.pop());
        assertTrue(stack.pop());
        assertTrue(stack.pop());
    }

    @Test
    public void testNewStackFromTopToBottomIterableOrder()
    {
        MutableBooleanStack stack = this.newWithIterableTopToBottom(BooleanArrayList.newListWith(false, true, true));
        assertFalse(stack.pop());
        assertTrue(stack.pop());
        assertTrue(stack.pop());
    }

    @Test
    public void push()
    {
        MutableBooleanStack stack = this.classUnderTest();
        int size = stack.size();
        stack.push(true);
        Verify.assertSize(size + 1, stack);
        stack.pop();
        Verify.assertSize(size, stack);
        assertEquals(BooleanArrayList.newListWith(false, true), stack.peek(2));
    }

    @Test
    public void pop()
    {
        MutableBooleanStack stack = this.classUnderTest();
        int size = stack.size();
        for (int i = 0; i < size; i++)
        {
            assertEquals((i & 1) != 0, stack.pop());
            Verify.assertSize(size - i - 1, stack);
        }
    }

    @Test
    public void popWithCount()
    {
        MutableBooleanStack stack = this.classUnderTest();
        int size = this.classUnderTest().size();
        assertEquals(BooleanArrayList.newListWith((size & 1) != 0, (size & 1) == 0), stack.pop(2));
        Verify.assertSize(size - 2, stack);
    }

    @Test
    public void clear()
    {
        MutableBooleanStack stack = this.classUnderTest();
        stack.clear();
        Verify.assertSize(0, stack);
        MutableBooleanStack stack1 = this.newWith();
        Verify.assertSize(0, stack1);
        stack1.clear();
        Verify.assertSize(0, stack1);
    }

    @Test
    public void pop_empty_stack_throws_exception()
    {
        assertThrows(EmptyStackException.class, () -> this.newWith().pop());
    }

    @Test
    public void pop_with_negative_count_throws_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith(true).pop(-1));
    }

    @Test
    public void pop_with_count_greater_than_stack_size_throws_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith(false).pop(2));
    }

    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedBooleanStack.class, this.classUnderTest().asSynchronized());
        assertEquals(this.classUnderTest(), this.classUnderTest().asSynchronized());
    }

    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableBooleanStack.class, this.classUnderTest().asUnmodifiable());
        assertEquals(this.classUnderTest(), this.classUnderTest().asUnmodifiable());
    }
}
