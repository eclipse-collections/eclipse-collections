/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.sorted;

import java.util.NoSuchElementException;

import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.test.SortedIterableTestCase;
import org.eclipse.collections.test.domain.A;
import org.eclipse.collections.test.domain.B;
import org.eclipse.collections.test.domain.C;
import org.eclipse.collections.test.list.TransformsToListTrait;
import org.eclipse.collections.test.set.SetIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SortedSetIterableTestCase extends SetIterableTestCase, SortedIterableTestCase, TransformsToListTrait
{
    @Override
    <T> SortedSetIterable<T> newWith(T... elements);

    @Override
    default boolean allowsNull()
    {
        return false;
    }

    @Override
    default <T> SortedSetIterable<T> getExpectedFiltered(T... elements)
    {
        return switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> SortedSets.immutable.with(elements);
            case SORTED_REVERSE_NATURAL -> SortedSets.immutable.with(Comparators.reverseNaturalOrder(), elements);
            default -> throw new AssertionError("Unexpected ordering type for SortedSetIterable: " + this.getOrderingType());
        };
    }

    @Override
    default <T> MutableSortedSet<T> newMutableForFilter(T... elements)
    {
        return switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> SortedSets.mutable.with(elements);
            case SORTED_REVERSE_NATURAL -> SortedSets.mutable.with(Comparators.reverseNaturalOrder(), elements);
            default -> throw new AssertionError("Unexpected ordering type for SortedSetIterable: " + this.getOrderingType());
        };
    }

    @Test
    default void SortedSetIterable_union()
    {
        SortedSetIterable<Integer> union = this.newWith(1, 2, 3).union(this.newWith(3, 4, 5));
        assertIterablesEqual(this.getExpectedFiltered(5, 4, 3, 2, 1), union);
    }

    @Override
    @Test
    default void SortedIterable_comparator()
    {
        switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> assertNull(this.newWith().comparator());
            case SORTED_REVERSE_NATURAL -> assertSame(Comparators.reverseNaturalOrder(), this.newWith().comparator());
            default -> throw new AssertionError("Unexpected ordering type for SortedSetIterable: " + this.getOrderingType());
        }
    }

    @Override
    @Test
    default void RichIterable_getFirst_empty_null()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().getFirst());
    }

    @Override
    @Test
    default void RichIterable_getLast_empty_null()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().getLast());
    }

    @Override
    @Test
    default void RichIterable_selectInstancesOf()
    {
        // Must test with two classes that are mutually Comparable

        SortedSetIterable<A> numbers = this.newWith(new C(4.0), new B(3), new C(2.0), new B(1));
        assertIterablesEqual(this.getExpectedFiltered(new B(3), new B(1)), numbers.selectInstancesOf(B.class));
        assertIterablesEqual(this.getExpectedFiltered(new C(4.0), new B(3), new C(2.0), new B(1)), numbers.selectInstancesOf(A.class));
    }
}
