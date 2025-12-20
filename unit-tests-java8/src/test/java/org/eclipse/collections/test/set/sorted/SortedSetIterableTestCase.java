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

import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.ordered.SortedIterable;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.test.SortedIterableTestCase;
import org.eclipse.collections.test.domain.A;
import org.eclipse.collections.test.domain.B;
import org.eclipse.collections.test.domain.C;
import org.eclipse.collections.test.list.TransformsToListTrait;
import org.eclipse.collections.test.set.SetIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        return SortedSets.immutable.with(Comparators.reverseNaturalOrder(), elements);
    }

    @Override
    default <T> MutableSortedSet<T> newMutableForFilter(T... elements)
    {
        return SortedSets.mutable.with(Comparators.reverseNaturalOrder(), elements);
    }

    @Test
    default void SortedSetIterable_union()
    {
        SortedSetIterable<Integer> union = this.newWith(1, 2, 3).union(this.newWith(3, 4, 5));
        assertIterablesEqual(SortedSets.immutable.with(Comparators.reverseNaturalOrder(), 5, 4, 3, 2, 1), union);
    }

    @Override
    @Test
    default void RichIterable_getFirst_empty_null()
    {
        SetIterableTestCase.super.RichIterable_getFirst_empty_null();
        SortedIterableTestCase.super.RichIterable_getFirst_empty_null();
    }

    @Override
    @Test
    default void RichIterable_getLast_empty_null()
    {
        SetIterableTestCase.super.RichIterable_getLast_empty_null();
        SortedIterableTestCase.super.RichIterable_getLast_empty_null();
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

    @Override
    @Test
    default void OrderedIterable_getFirst()
    {
        assertEquals(Integer.valueOf(3), this.newWith(3, 2, 1).getFirst());
    }

    @Override
    @Test
    default void OrderedIterable_getFirstOptional()
    {
        assertEquals(Optional.of(3), ((OrderedIterable<?>) this.newWith(3, 2, 1)).getFirstOptional());
    }

    @Override
    @Test
    default void OrderedIterable_getLast()
    {
        assertEquals(Integer.valueOf(1), this.newWith(3, 2, 1).getLast());
    }

    @Override
    @Test
    default void OrderedIterable_getLastOptional()
    {
        assertEquals(Optional.of(Integer.valueOf(1)), ((OrderedIterable<?>) this.newWith(3, 2, 1)).getLastOptional());
    }

    @Override
    @Test
    default void RichIterable_getFirst()
    {
        assertEquals(Integer.valueOf(3), this.newWith(3, 2, 1).getFirst());
    }

    @Override
    @Test
    default void RichIterable_getLast()
    {
        assertEquals(Integer.valueOf(1), this.newWith(3, 2, 1).getLast());
    }

    @Override
    @Test
    default void OrderedIterable_min()
    {
        // Cannot contain duplicates
    }

    @Override
    @Test
    default void OrderedIterable_max()
    {
        // Cannot contain duplicates
    }

    @Override
    @Test
    default void OrderedIterable_min_comparator()
    {
        // Cannot contain duplicates
    }

    @Override
    @Test
    default void OrderedIterable_max_comparator()
    {
        // Cannot contain duplicates
    }

    @Override
    @Test
    default void OrderedIterable_zipWithIndex()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        assertEquals(
                Lists.immutable.with(
                        Tuples.pair(4, 0),
                        Tuples.pair(3, 1),
                        Tuples.pair(2, 2),
                        Tuples.pair(1, 3)),
                iterable.zipWithIndex().toList());
    }

    @Override
    @Test
    default void OrderedIterable_zipWithIndex_target()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        assertEquals(
                Lists.immutable.with(
                        Tuples.pair(4, 0),
                        Tuples.pair(3, 1),
                        Tuples.pair(2, 2),
                        Tuples.pair(1, 3)),
                iterable.zipWithIndex(Lists.mutable.empty()));
    }

    @Test
    default void OrderedIterable_forEach_from_to()
    {
        SortedIterable<Integer> integers = this.newWith(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        MutableList<Integer> result = Lists.mutable.empty();
        integers.forEach(5, 7, result::add);
        assertIterablesEqual(Lists.immutable.with(4, 3, 2), result);

        MutableList<Integer> result2 = Lists.mutable.empty();
        integers.forEach(5, 5, result2::add);
        assertIterablesEqual(Lists.immutable.with(4), result2);

        MutableList<Integer> result3 = Lists.mutable.empty();
        integers.forEach(0, 9, result3::add);
        assertIterablesEqual(Lists.immutable.with(9, 8, 7, 6, 5, 4, 3, 2, 1, 0), result3);

        MutableList<Integer> result4 = Lists.mutable.empty();
        integers.forEach(0, 0, result4::add);
        assertIterablesEqual(Lists.immutable.with(9), result4);

        MutableList<Integer> result5 = Lists.mutable.empty();
        integers.forEach(9, 9, result5::add);
        assertIterablesEqual(Lists.immutable.with(0), result5);

        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(-1, 0, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, -1, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, 10, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(10, 0, result::add));
    }

    @Test
    default void OrderedIterable_forEach_from_to_reverse_order()
    {
        SortedIterable<Integer> integers = this.newWith(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        MutableList<Integer> result = Lists.mutable.empty();
        assertThrows(IllegalArgumentException.class, () -> integers.forEach(7, 5, result::add));
    }
}
