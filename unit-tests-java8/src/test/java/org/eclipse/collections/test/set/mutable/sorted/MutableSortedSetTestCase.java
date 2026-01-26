/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable.sorted;

import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.test.MutableSortedIterableTestCase;
import org.eclipse.collections.test.collection.mutable.MutableCollectionUniqueTestCase;
import org.eclipse.collections.test.set.sorted.SortedSetIterableTestCase;
import org.eclipse.collections.test.set.sorted.SortedSetTestCase;
import org.junit.jupiter.api.Test;

public interface MutableSortedSetTestCase extends SortedSetIterableTestCase, MutableCollectionUniqueTestCase, SortedSetTestCase, MutableSortedIterableTestCase
{
    @Override
    <T> MutableSortedSet<T> newWith(T... elements);

    @Override
    @Test
    default void Iterable_toString()
    {
        SortedSetTestCase.super.Iterable_toString();
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        // Both implementations are the same
        SortedSetTestCase.super.Iterable_remove();
        MutableSortedIterableTestCase.super.Iterable_remove();
    }
}
