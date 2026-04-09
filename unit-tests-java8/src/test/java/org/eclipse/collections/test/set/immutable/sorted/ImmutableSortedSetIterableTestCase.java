/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.immutable.sorted;

import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.test.collection.immutable.ImmutableCollectionUniqueTestCase;
import org.eclipse.collections.test.set.sorted.SortedSetIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public interface ImmutableSortedSetIterableTestCase extends SortedSetIterableTestCase, ImmutableCollectionUniqueTestCase
{
    @Override
    <T> ImmutableSortedSet<T> newWith(T... elements);

    @Override
    @Test
    default void ImmutableCollection_newWith()
    {
        ImmutableSortedSet<Integer> immutableCollection = this.newWith(3, 2, 1);
        ImmutableSortedSet<Integer> newWith = immutableCollection.newWith(4);

        assertIterablesEqual(this.newWith(4, 3, 2, 1).castToSortedSet(), newWith.castToSortedSet());
        assertNotSame(immutableCollection, newWith);
        assertThat(newWith, instanceOf(ImmutableSortedSet.class));

        ImmutableSortedSet<Integer> newWith2 = newWith.newWith(4);
        assertSame(newWith, newWith2);
        assertIterablesEqual(this.newWith(4, 3, 2, 1).castToSortedSet(), newWith2.castToSortedSet());
    }
}
