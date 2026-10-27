/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.list.mutable;

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReversedMutableListTest implements MutableListTestCase
{
    @SafeVarargs
    @Override
    public final <T> MutableList<T> newWith(T... elements)
    {
        MutableList<T> list = Lists.mutable.empty();
        for (int i = elements.length - 1; i >= 0; i--)
        {
            list.add(elements[i]);
        }
        return list.reversed();
    }

    @Override
    @Test
    public void MutableList_reversed()
    {
        MutableList<Integer> original = this.newWith(3, 3, 3, 2, 2, 1);
        MutableList<Integer> reversed = original.reversed();
        assertIterablesEqual(Lists.immutable.with(1, 2, 2, 3, 3, 3), reversed);

        // original is wrapped, reversed is unwrapped, reversed.reversed() is wrapped again, reversed.reversed().reversed() is unwrapped again
        assertSame(reversed, reversed.reversed().reversed());

        original.add(4);
        assertIterablesEqual(Lists.mutable.with(4, 1, 2, 2, 3, 3, 3), reversed);

        reversed.add(0);
        assertIterablesEqual(Lists.mutable.with(0, 3, 3, 3, 2, 2, 1, 4), original);

        MutableList<Integer> empty = this.newWith();
        assertNotSame(empty, empty.reversed());
    }

    @Test
    public void ReversedMutableList_writeReplace()
    {
        MutableList<Integer> delegate = FastList.newListWith(1, 2, 3);
        MutableList<Integer> reversed = delegate.reversed();
        MutableList<Integer> deserialized = SerializeTestHelper.serializeDeserialize(reversed);
        assertEquals(reversed, deserialized);
        Verify.assertInstanceOf(FastList.class, deserialized);
    }

    @Override
    @Test
    public void MutableCollection_newEmpty()
    {
        MutableListTestCase.super.MutableCollection_newEmpty();

        Verify.assertInstanceOf(FastList.class, this.newWith().newEmpty());
    }

    @Override
    @Test
    public void List_add()
    {
        MutableListTestCase.super.List_add();

        MutableList<Integer> delegate = FastList.newListWith(1, 2, 3);
        MutableList<Integer> reversed = delegate.reversed();
        reversed.add(0, 99);
        assertEquals(Lists.mutable.with(1, 2, 3, 99), delegate);
        assertEquals(Lists.mutable.with(99, 3, 2, 1), reversed);
        assertThrows(IndexOutOfBoundsException.class, () -> reversed.add(-1, 88));
        assertThrows(IndexOutOfBoundsException.class, () -> reversed.add(reversed.size() + 1, 88));
    }

    @Override
    @Test
    public void List_remove()
    {
        MutableListTestCase.super.List_remove();

        // remove() writes through to the delegate
        MutableList<Integer> delegate = FastList.newListWith(1, 2, 3);
        delegate.reversed().remove(0);
        assertEquals(Lists.mutable.with(1, 2), delegate);
    }

    @Override
    @Test
    public void Collection_addAll()
    {
        MutableListTestCase.super.Collection_addAll();

        MutableList<Integer> delegate = FastList.newListWith(1, 2, 3);
        MutableList<Integer> reversed = delegate.reversed();
        reversed.addAll(Lists.mutable.with(7, 8));
        assertEquals(Lists.mutable.with(8, 7, 1, 2, 3), delegate);
        assertEquals(Lists.mutable.with(3, 2, 1, 7, 8), reversed);
    }

    @Test
    public void Iterable_iterator()
    {
        MutableList<Integer> delegate = FastList.newListWith(1, 2, 3);
        MutableList<Integer> reversed = delegate.reversed();
        Iterator<Integer> it = reversed.iterator();
        MutableList<Integer> collected = Lists.mutable.empty();
        while (it.hasNext())
        {
            collected.add(it.next());
        }
        assertEquals(Lists.mutable.with(3, 2, 1), collected);
    }

    @Override
    @Test
    public void List_listIterator_set()
    {
        MutableListTestCase.super.List_listIterator_set();

        // listIterator().set() writes through to the delegate
        MutableList<Integer> delegate = FastList.newListWith(1, 2, 3);
        ListIterator<Integer> it = delegate.reversed().listIterator();
        it.next(); // 3
        it.set(99);
        assertEquals(Lists.mutable.with(1, 2, 99), delegate);
    }

    @Override
    @Test
    public void Collection_remove_removeAll()
    {
        MutableListTestCase.super.Collection_remove_removeAll();

        MutableList<Integer> delegate = FastList.newListWith(1, 2, 3, 4, 5);
        MutableList<Integer> reversed = delegate.reversed();
        reversed.removeAll(Lists.mutable.with(2, 4));
        assertEquals(Lists.mutable.with(1, 3, 5), delegate);
        assertEquals(Lists.mutable.with(5, 3, 1), reversed);
    }

    @Override
    @Test
    public void Collection_retainAll()
    {
        MutableListTestCase.super.Collection_retainAll();

        MutableList<Integer> delegate = FastList.newListWith(1, 2, 3, 4, 5);
        MutableList<Integer> reversed = delegate.reversed();
        reversed.retainAll(Lists.mutable.with(2, 4));
        assertEquals(Lists.mutable.with(2, 4), delegate);
        assertEquals(Lists.mutable.with(4, 2), reversed);
    }
}
