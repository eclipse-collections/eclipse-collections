/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.list.immutable;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ReversedImmutableListTest implements ImmutableListTestCase
{
    @SafeVarargs
    @Override
    public final <T> ImmutableList<T> newWith(T... elements)
    {
        MutableList<T> list = Lists.mutable.empty();
        for (int i = elements.length - 1; i >= 0; i--)
        {
            list.add(elements[i]);
        }
        return list.toImmutable().reversed();
    }

    @Override
    @Test
    public void ImmutableList_reversed()
    {
        ImmutableList<Integer> immutableList = this.newWith(3, 3, 3, 2, 2, 1);
        ImmutableList<Integer> reversed = immutableList.reversed();
        assertIterablesEqual(Lists.immutable.with(1, 2, 2, 3, 3, 3), reversed);

        // The first reversed() unwraps and the second  reversed() re-wraps and returns a new instance
        assertNotSame(immutableList, reversed.reversed());

        ImmutableList<Integer> empty = Lists.immutable.empty();
        assertSame(empty, empty.reversed());
    }

    @Test
    public void ReversedImmutableList_writeReplace()
    {
        ImmutableList<Integer> delegate = Lists.immutable.of(1, 2, 3);
        ImmutableList<Integer> reversed = delegate.reversed();
        ImmutableList<Integer> deserialized = SerializeTestHelper.serializeDeserialize(reversed);
        assertEquals(reversed, deserialized);
        assertEquals(Lists.immutable.of(3, 2, 1), deserialized);
    }
}
