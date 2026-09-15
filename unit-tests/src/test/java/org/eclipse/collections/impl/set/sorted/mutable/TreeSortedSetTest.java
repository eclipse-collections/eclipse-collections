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

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeSortedSetTest extends AbstractSortedSetTestCase
{
    @Override
    protected <T> TreeSortedSet<T> newWith(T... elements)
    {
        return TreeSortedSet.newSetWith(elements);
    }

    @Override
    protected <T> TreeSortedSet<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return TreeSortedSet.newSetWith(comparator, elements);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedSortedSet.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedSet.class, this.newWith().asUnmodifiable());
    }

    @Test
    public void sortedSetIterableConstructor()
    {
        TreeSortedSet<Integer> sortedSetA = TreeSortedSet.newSet(Collections.reverseOrder());
        TreeSortedSet<Integer> sortedSetB = TreeSortedSet.newSet(sortedSetA.with(1).with(2, 3).with(4, 5, 6));
        Verify.assertSortedSetsEqual(sortedSetA, sortedSetB);
        assertTrue(sortedSetA.first().equals(sortedSetB.first()) && sortedSetB.first() == 6);
        Verify.assertSortedSetsEqual(sortedSetB, new TreeSortedSet<>(sortedSetB));
    }

    @Test
    public void sortedSetConstructor()
    {
        SortedSet<String> setA = new TreeSet<>(FastList.newListWith("a", "c", "b", "d"));
        Verify.assertSortedSetsEqual(setA, TreeSortedSet.newSet(setA));
        Verify.assertSortedSetsEqual(setA, new TreeSortedSet<>(setA));
    }

    @Test
    public void iterableConstructor()
    {
        LazyIterable<Integer> integerLazyIterable = FastList.newListWith(2, 4, 1, 3).asLazy();
        TreeSortedSet<Integer> sortedSet = TreeSortedSet.newSet(integerLazyIterable);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), sortedSet);
    }

    @Test
    public void serialization()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        Verify.assertPostSerializedEqualsAndHashCode(set);
    }
}
