/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.immutable;

import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.test.IterableTestCase;
import org.eclipse.collections.test.set.immutable.sorted.ImmutableSortedSetIterableTestCase;
import org.eclipse.collections.test.set.sorted.NavigableSetTestCase;
import org.junit.jupiter.api.Test;

public interface ImmutableSortedSetTestCase extends ImmutableSortedSetIterableTestCase, NavigableSetTestCase
{
    @Override
    <T> AbstractImmutableSortedSet<T> newWith(T... elements);

    @Override
    default IterableTestCase.OrderingType getOrderingType()
    {
        return IterableTestCase.OrderingType.SORTED_REVERSE_NATURAL;
    }

    @Override
    default boolean allowsAdd()
    {
        return false;
    }

    @Override
    default boolean allowsRemove()
    {
        return false;
    }

    @Override
    default <T> AbstractImmutableSortedSet<T> getExpectedFiltered(T... elements)
    {
        return switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> (AbstractImmutableSortedSet<T>) SortedSets.immutable.with(elements);
            case SORTED_REVERSE_NATURAL -> (AbstractImmutableSortedSet<T>) SortedSets.immutable.with(Comparators.reverseNaturalOrder(), elements);
            default -> throw new AssertionError("Unexpected ordering type for ImmutableSortedSet: " + this.getOrderingType());
        };
    }

    @Override
    default <T> MutableSortedSet<T> newMutableForFilter(T... elements)
    {
        return switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> SortedSets.mutable.with(elements);
            case SORTED_REVERSE_NATURAL -> SortedSets.mutable.with(Comparators.reverseNaturalOrder(), elements);
            default -> throw new AssertionError("Unexpected ordering type for ImmutableSortedSet: " + this.getOrderingType());
        };
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        NavigableSetTestCase.super.Iterable_remove();
        ImmutableSortedSetIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        NavigableSetTestCase.super.Iterable_toString();
        ImmutableSortedSetIterableTestCase.super.Iterable_toString();
    }
}
