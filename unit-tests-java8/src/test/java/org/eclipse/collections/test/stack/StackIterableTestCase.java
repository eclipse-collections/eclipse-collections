/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.stack;

import java.util.EmptyStackException;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Stacks;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.stack.StackIterable;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.test.OrderedIterableWithDuplicatesTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public interface StackIterableTestCase extends OrderedIterableWithDuplicatesTestCase, TransformsToStackTrait
{
    @Override
    <T> StackIterable<T> newWith(T... elements);

    @Override
    @Test
    default void newMutable_sanity()
    {
        // Cannot treat an ArrayStack as a MutableCollection
    }

    @Override
    default <T> StackIterable<T> getExpectedFiltered(T... elements)
    {
        return Stacks.immutable.withReversed(elements);
    }

    @Override
    default <T> MutableList<T> newMutableForFilter(T... elements)
    {
        return Lists.mutable.with(elements);
    }

    @Override
    @Test
    default void InternalIterable_forEach()
    {
        RichIterable<Integer> integers = this.newWith(3, 3, 3, 2, 2, 1);
        MutableStack<Integer> result = Stacks.mutable.with();
        integers.forEach(Procedures.cast(result::push));
        assertIterablesEqual(this.newWith(1, 2, 2, 3, 3, 3), result);
    }

    @Override
    @Test
    default void RichIterable_tap()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        MutableStack<Integer> result = Stacks.mutable.with();
        iterable.tap(result::push).forEach(Procedures.noop());
        assertIterablesEqual(this.newWith(1, 2, 3), result);
        this.newWith().tap(Procedures.cast(each -> fail()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(3, 3, 3, 2, 2, 1);
        MutableStack<Integer> result2 = Stacks.mutable.with();
        iterable2.tap(result2::push).forEach(Procedures.noop());
        assertIterablesEqual(this.newWith(1, 2, 2, 3, 3, 3), result2);
    }

    @Override
    @Test
    default void InternalIterable_forEachWith()
    {
        RichIterable<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);
        MutableStack<Integer> result = Stacks.mutable.with();
        iterable.forEachWith((argument1, argument2) -> result.push(argument1 + argument2), 10);
        assertIterablesEqual(this.getExpectedFiltered(11, 12, 12, 13, 13, 13), result);
    }

    @Override
    @Test
    default void RichIterable_getFirst_empty_null()
    {
        assertThrows(EmptyStackException.class, this.newWith()::getFirst);
    }

    @Override
    @Test
    default void RichIterable_getLast_empty_null()
    {
        assertThrows(EmptyStackException.class, () -> this.newWith().getLast());
    }

    @Test
    default void StackIterable_peek()
    {
        assertEquals(Integer.valueOf(5), this.newWith(5, 1, 4, 2, 3).peek());
    }

    @Test
    default void StackIterable_peek_throws()
    {
        assertThrows(EmptyStackException.class, () -> this.newWith().peek());
    }

    @Test
    default void StackIterable_peekAt()
    {
        assertEquals(Integer.valueOf(5), this.newWith(5, 1, 4, 2, 3).peekAt(0));
        assertEquals(Integer.valueOf(1), this.newWith(5, 1, 4, 2, 3).peekAt(1));
        assertEquals(Integer.valueOf(4), this.newWith(5, 1, 4, 2, 3).peekAt(2));
        assertEquals(Integer.valueOf(2), this.newWith(5, 1, 4, 2, 3).peekAt(3));
        assertEquals(Integer.valueOf(3), this.newWith(5, 1, 4, 2, 3).peekAt(4));
    }

    @Test
    default void StackIterable_peekAt_throws()
    {
        StackIterable<Integer> stackIterable = this.newWith(5, 1, 4, 2, 3);
        assertThrows(IllegalArgumentException.class, () -> stackIterable.peekAt(-1));
        assertThrows(IllegalArgumentException.class, () -> stackIterable.peekAt(5));
    }
}
