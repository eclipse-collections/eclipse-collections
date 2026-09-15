/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.collection.mutable.SynchronizedMutableCollectionTestTrait;
import org.junit.jupiter.api.Test;

public class SynchronizedMutableListTest
        implements SynchronizedMutableCollectionTestTrait
{
    private final MutableList<String> classUnderTest = FastList.newListWith("1", "2", "3").asSynchronized();

    @Override
    public MutableList<String> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void sort_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().sort(null));
    }

    @Test
    public void sortThis_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::sortThis);
    }

    @Test
    public void sortThisWithComparator_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().sortThis(null));
    }

    @Test
    public void asReversed_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::asReversed);
        LazyIterable<String> reverseIterable = this.getClassUnderTest().asReversed();
        this.assertSynchronized(() -> reverseIterable.forEach(x -> { }));
    }
}
