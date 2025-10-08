/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.mutable;

import java.util.Comparator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * JUnit test for {@link SynchronizedSortedSet}.
 */
public class SynchronizedSortedSet2Test extends AbstractSortedSetTestCase
{
    @Override
    protected <T> MutableSortedSet<T> newWith(T... elements)
    {
        return new SynchronizedSortedSet<>(TreeSortedSet.newSetWith(elements));
    }

    @Override
    protected <T> MutableSortedSet<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return new SynchronizedSortedSet<>(TreeSortedSet.newSetWith(comparator, elements));
    }

    @Override
    public void asSynchronized()
    {
        MutableSortedSet<Object> synchronizedSet = this.newWith();
        assertSame(synchronizedSet, synchronizedSet.asSynchronized());
    }

    @Override
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedSet.class, this.newWith().asUnmodifiable());
    }

    @Test
    public void min_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().min());
    }

    @Test
    public void max_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().max());
    }

    @Override
    @Test
    public void detectLastIndex()
    {
        assertEquals(1, this.newWith(1, 2, 3).detectLastIndex(each -> each % 2 == 0));
        assertEquals(3, this.newWith(1, 2, 3, 4).detectLastIndex(each -> each % 2 == 0));
        assertEquals(-1, this.newWith(1, 3, 5).detectLastIndex(each -> each % 2 == 0));
    }

    @Override
    @Test
    public void reverseForEach()
    {
        MutableList<Integer> result = FastList.newList();
        this.newWith(1, 2, 3).reverseForEach(result::add);
        assertEquals(FastList.newListWith(3, 2, 1), result);
    }

    @Override
    @Test
    public void reverseForEachWithIndex()
    {
        MutableList<String> result = FastList.newList();
        this.newWith(1, 2, 3).reverseForEachWithIndex((each, index) -> result.add(each + ":" + index));
        assertEquals(FastList.newListWith("3:0", "2:1", "1:2"), result);
    }

    @Override
    @Test
    public void toReversed()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> reversed = set.toReversed();
        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), reversed.toList());
    }
}
