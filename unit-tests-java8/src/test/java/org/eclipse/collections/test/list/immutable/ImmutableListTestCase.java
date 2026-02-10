/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.list.immutable;

import java.util.List;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.test.collection.immutable.ImmutableCollectionTestCase;
import org.eclipse.collections.test.list.ListIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertSame;

public interface ImmutableListTestCase extends ImmutableCollectionTestCase, ListIterableTestCase
{
    @Override
    <T> ImmutableList<T> newWith(T... elements);

    @Test
    default void ImmutableList_castToList()
    {
        ImmutableList<Integer> immutableList = this.newWith(3, 3, 3, 2, 2, 1);
        List<Integer> list = immutableList.castToList();
        assertSame(immutableList, list);
    }

    @Test
    default void ImmutableList_reversed()
    {
        ImmutableList<Integer> original = this.newWith(3, 3, 3, 2, 2, 1);
        ImmutableList<Integer> reversed = original.reversed();
        assertIterablesEqual(Lists.immutable.with(1, 2, 2, 3, 3, 3), reversed);
        assertSame(original, reversed.reversed());

        ImmutableList<Integer> empty = this.newWith();
        assertSame(empty, empty.reversed());
    }
}
