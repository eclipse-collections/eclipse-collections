/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.bag.mutable.sorted;

import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bag.sorted.SortedBag;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.test.SortedIterableTestCase;
import org.eclipse.collections.test.bag.BagTestCase;
import org.eclipse.collections.test.domain.A;
import org.eclipse.collections.test.domain.B;
import org.eclipse.collections.test.domain.C;
import org.eclipse.collections.test.list.TransformsToListTrait;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO linked bag
public interface SortedBagTestCase extends SortedIterableTestCase, BagTestCase, TransformsToListTrait
{
    @Override
    <T> SortedBag<T> newWith(T... elements);

    @Override
    default boolean supportsForEachFromToReverseIteration()
    {
        return false;
    }

    @Override
    default <T> SortedBag<T> getExpectedFiltered(T... elements)
    {
        return new TreeBag<>(Comparators.reverseNaturalOrder(), Lists.immutable.with(elements));
    }

    @Override
    default <T> MutableSortedBag<T> newMutableForFilter(T... elements)
    {
        return new TreeBag<>(Comparators.reverseNaturalOrder(), Lists.immutable.with(elements));
    }

    @Override
    @Test
    default void RichIterable_selectInstancesOf()
    {
        // Must test with two classes that are mutually Comparable

        SortedBag<A> numbers = this.newWith(
                new C(4.0), new C(4.0), new C(4.0), new C(4.0),
                new B(3), new B(3), new B(3),
                new C(2.0), new C(2.0),
                new B(1));
        assertIterablesEqual(
                this.getExpectedFiltered(new B(3), new B(3), new B(3), new B(1)),
                numbers.selectInstancesOf(B.class));
        assertIterablesEqual(
                this.getExpectedFiltered(
                        new C(4.0), new C(4.0), new C(4.0), new C(4.0),
                        new B(3), new B(3), new B(3),
                        new C(2.0), new C(2.0),
                        new B(1)),
                numbers.selectInstancesOf(A.class));
    }

    @Override
    @Test
    default void Bag_sizeDistinct()
    {
        SortedBag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertEquals(3, bag.sizeDistinct());
    }

    @Override
    @Test
    default void Bag_occurrencesOf()
    {
        SortedBag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertEquals(0, bag.occurrencesOf(0));
        assertEquals(1, bag.occurrencesOf(1));
        assertEquals(2, bag.occurrencesOf(2));
        assertEquals(3, bag.occurrencesOf(3));
    }

    @Override
    @Test
    default void Bag_toStringOfItemToCount()
    {
        assertEquals("{}", this.newWith().toStringOfItemToCount());
        assertEquals("{3=3, 2=2, 1=1}", this.newWith(3, 3, 3, 2, 2, 1).toStringOfItemToCount());
    }

    @Test
    default void SortedBag_forEachWith()
    {
        SortedBag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        MutableList<Integer> result = Lists.mutable.with();
        bag.forEachWith((argument1, argument2) -> {
            result.add(argument1);
            result.add(argument2);
        }, 0);
        assertIterablesEqual(Lists.immutable.with(3, 0, 3, 0, 3, 0, 2, 0, 2, 0, 1, 0), result);
    }
}
