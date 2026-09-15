/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable;

import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.set.sorted.SynchronizedSortedSetIterableTestTrait;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.junit.jupiter.api.Test;

public class SynchronizedSortedSetTest
        implements SynchronizedSortedSetIterableTestTrait
{
    private final MutableSortedSet<String> classUnderTest = TreeSortedSet.newSetWith("1", "2", "3").asSynchronized();

    @Override
    public MutableSortedSet<String> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void comparator_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::comparator);
    }

    @Test
    public void headSet_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().headSet("2"));
        this.assertSynchronized(() -> this.getClassUnderTest().headSet("2").add("1"));
    }

    @Test
    public void tailSet_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().tailSet("2"));
        this.assertSynchronized(() -> this.getClassUnderTest().tailSet("2").add("4"));
    }

    @Test
    public void subSet_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().subSet("1", "3"));
        this.assertSynchronized(() -> this.getClassUnderTest().subSet("1", "3").add("1"));
    }

    @Test
    public void first_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::first);
    }

    @Test
    public void last_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::last);
    }
}
