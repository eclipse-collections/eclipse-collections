/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set;

import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.impl.SynchronizedRichIterableTestTrait;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.junit.jupiter.api.Test;

public interface SynchronizedSetIterableTestTrait
        extends SynchronizedRichIterableTestTrait
{
    @Override
    SetIterable<String> getClassUnderTest();

    @Test
    default void union_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().union(TreeSortedSet.newSet()));
    }

    @Test
    default void unionInto_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().unionInto(TreeSortedSet.newSet(), TreeSortedSet.newSet()));
    }

    @Test
    default void intersect_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().intersect(TreeSortedSet.newSet()));
    }

    @Test
    default void intersectInto_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().intersectInto(TreeSortedSet.newSet(), TreeSortedSet.newSet()));
    }

    @Test
    default void difference_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().difference(TreeSortedSet.newSet()));
    }

    @Test
    default void differenceInto_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().differenceInto(TreeSortedSet.newSet(), TreeSortedSet.newSet()));
    }

    @Test
    default void symmetricDifference_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().symmetricDifference(TreeSortedSet.newSet()));
    }

    @Test
    default void symmetricDifferenceInto_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().symmetricDifferenceInto(TreeSortedSet.newSet(), TreeSortedSet.newSet()));
    }

    @Test
    default void isSubsetOf_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().isSubsetOf(TreeSortedSet.newSet()));
    }

    @Test
    default void isProperSubsetOf_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().isProperSubsetOf(TreeSortedSet.newSet()));
    }

    @Test
    default void cartesianProduct_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().cartesianProduct(TreeSortedSet.newSet()));
    }
}
