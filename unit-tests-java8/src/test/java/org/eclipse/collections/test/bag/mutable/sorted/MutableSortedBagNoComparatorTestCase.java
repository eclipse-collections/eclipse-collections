/*
 * Copyright (c) 2022 Goldman Sachs and others.
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
import org.eclipse.collections.test.MutableSortedNaturalOrderTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.addAllTo;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface MutableSortedBagNoComparatorTestCase extends SortedBagTestCase, MutableBagIterableTestCase, MutableSortedNaturalOrderTestCase
{
    @Override
    <T> MutableSortedBag<T> newWith(T... elements);

    @Override
    default OrderingType getOrderingType()
    {
        return OrderingType.SORTED_NATURAL;
    }

    @Override
    default <T> SortedBag<T> getExpectedFiltered(T... elements)
    {
        return this.newMutableForFilter(elements);
    }

    @Override
    default <T> MutableSortedBag<T> newMutableForFilter(T... elements)
    {
        MutableSortedBag<T> result = new TreeBag<>();
        addAllTo(elements, result);
        return result;
    }

    @Override
    @Test
    default void Bag_toStringOfItemToCount()
    {
        assertEquals("{}", this.newWith().toStringOfItemToCount());
        assertEquals("{1=1, 2=2, 3=3}", this.newWith(3, 3, 3, 2, 2, 1).toStringOfItemToCount());
    }

    @Override
    @Test
    default void MutableBagIterable_addOccurrences()
    {
        MutableSortedBag<Integer> mutableSortedBag = this.newWith(1, 2, 2, 3, 3, 3);
        assertEquals(4, mutableSortedBag.addOccurrences(4, 4));
        assertIterablesEqual(TreeBag.newBagWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4), mutableSortedBag);
        assertEquals(3, mutableSortedBag.addOccurrences(1, 2));
        assertIterablesEqual(TreeBag.newBagWith(1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4, 4), mutableSortedBag);
        assertEquals(3, mutableSortedBag.addOccurrences(1, 0));
        assertIterablesEqual(TreeBag.newBagWith(1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4, 4), mutableSortedBag);
    }

    @Override
    @Test
    default void MutableBagIterable_removeOccurrences()
    {
        MutableSortedBag<Integer> mutableBag = this.newWith(1, 2, 2, 3, 3, 3);
        assertFalse(mutableBag.removeOccurrences(4, 4));
        assertIterablesEqual(TreeBag.newBagWith(1, 2, 2, 3, 3, 3), mutableBag);
        assertFalse(mutableBag.removeOccurrences(3, 0));
        assertIterablesEqual(TreeBag.newBagWith(1, 2, 2, 3, 3, 3), mutableBag);
        assertTrue(mutableBag.removeOccurrences(1, 2));
        assertIterablesEqual(TreeBag.newBagWith(2, 2, 3, 3, 3), mutableBag);
        assertTrue(mutableBag.removeOccurrences(3, 2));
        assertIterablesEqual(TreeBag.newBagWith(2, 2, 3), mutableBag);
        assertTrue(mutableBag.removeOccurrences(2, 1));
        assertIterablesEqual(TreeBag.newBagWith(2, 3), mutableBag);
        assertTrue(mutableBag.removeOccurrences(2, 2));
        assertIterablesEqual(TreeBag.newBagWith(3), mutableBag);
    }

    @Override
    @Test
    default void SortedBag_forEachWith()
    {
        SortedBag<Integer> bag = this.newWith(1, 2, 2, 3, 3, 3);
        MutableList<Integer> result = Lists.mutable.with();
        bag.forEachWith((argument1, argument2) -> {
            result.add(argument1);
            result.add(argument2);
        }, 0);
        assertIterablesEqual(Lists.immutable.with(1, 0, 2, 0, 2, 0, 3, 0, 3, 0, 3, 0), result);
    }

    @Override
    @Test
    default void SortedIterable_comparator()
    {
        MutableSortedNaturalOrderTestCase.super.SortedIterable_comparator();
    }
}
