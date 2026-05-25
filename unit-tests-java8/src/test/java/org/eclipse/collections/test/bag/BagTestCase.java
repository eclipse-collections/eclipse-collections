/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.bag;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.eclipse.collections.test.RichIterableWithDuplicatesTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface BagTestCase extends RichIterableWithDuplicatesTestCase
{
    @Override
    <T> Bag<T> newWith(T... elements);

    @Override
    default MutableCollection<Integer> expectedIterationOrder()
    {
        MutableCollection<Integer> forEach = this.newMutableForFilter();
        Bag<Integer> bag = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        bag.forEachWithOccurrences((Integer each, int parameter) -> forEach.add(each));
        return forEach;
    }

    @Override
    @Test
    default void RichIterable_iterator_iterationOrder()
    {
        if (!this.allowsIterator())
        {
            assertThrows(AssertionError.class, () -> this.getInstanceUnderTest().iterator().hasNext());
            return;
        }
        MutableCollection<Integer> iterationOrder = this.newMutableForFilter();
        Iterator<Integer> iterator = this.getInstanceUnderTest().iterator();
        while (iterator.hasNext())
        {
            iterationOrder.add(iterator.next());
        }
        assertIterablesEqual(RichIterableWithDuplicatesTestCase.super.expectedIterationOrder(), iterationOrder);

        MutableCollection<Integer> forEachWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().forEachWith((each, param) -> forEachWithIterationOrder.add(each), null);
        assertIterablesEqual(RichIterableWithDuplicatesTestCase.super.expectedIterationOrder(), forEachWithIterationOrder);

        MutableCollection<Integer> forEachWithIndexIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().forEachWithIndex((each, index) -> forEachWithIndexIterationOrder.add(each));
        assertIterablesEqual(RichIterableWithDuplicatesTestCase.super.expectedIterationOrder(), forEachWithIndexIterationOrder);
    }

    @Test
    default void Bag_anySatisfyWithOccurrences()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertTrue(bag.anySatisfyWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertTrue(bag.anySatisfyWithOccurrences((object, value) -> object.equals(2) && value == 2));
        assertTrue(bag.anySatisfyWithOccurrences((object, value) -> object.equals(3)));

        assertFalse(bag.anySatisfyWithOccurrences((object, value) -> object.equals(2) && value == 5));
        assertFalse(bag.anySatisfyWithOccurrences((object, value) -> object.equals(1) && value == 7));
        assertFalse(bag.anySatisfyWithOccurrences((object, value) -> object.equals(10)));
    }

    @Test
    default void Bag_noneSatisfyWithOccurrences()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(3) && value == 1));
        assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(30)));
        assertFalse(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(1) && value == 0));
        assertFalse(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(1) && value == 1));
        assertFalse(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(2)));
    }

    @Test
    default void Bag_allSatisfyWithOccurrences()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3);
        assertTrue(bag.allSatisfyWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertTrue(bag.allSatisfyWithOccurrences((object, value) -> object.equals(3)));
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(4) && value == 3));
        bag = this.newWith(3, 3, 3, 1);
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(1) && value == 3));
        assertTrue(bag.allSatisfyWithOccurrences((object, value) -> object.equals(3) || object.equals(1)));
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(300) || object.equals(1)));
    }

    @Test
    default void Bag_detectWithOccurrences()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertEquals(Integer.valueOf(3), bag.detectWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertEquals(Integer.valueOf(3), bag.detectWithOccurrences((object, value) -> object.equals(3)));
        assertEquals(Integer.valueOf(1), bag.detectWithOccurrences((object, value) -> object.equals(1) && value == 1));
        assertNull(bag.detectWithOccurrences((object, value) -> object.equals(1) && value == 10));
        assertNull(bag.detectWithOccurrences((object, value) -> object.equals(10) && value == 5));
        assertNull(bag.detectWithOccurrences((object, value) -> object.equals(100)));
    }

    @Test
    default void Bag_sizeDistinct()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertEquals(3, bag.sizeDistinct());
    }

    @Test
    default void Bag_occurrencesOf()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertEquals(0, bag.occurrencesOf(0));
        assertEquals(1, bag.occurrencesOf(1));
        assertEquals(2, bag.occurrencesOf(2));
        assertEquals(3, bag.occurrencesOf(3));
    }

    @Test
    default void Bag_toStringOfItemToCount()
    {
        assertEquals("{}", this.newWith().toStringOfItemToCount());
        assertThat(this.newWith(2, 2, 1).toStringOfItemToCount(), isOneOf("{1=1, 2=2}", "{2=2, 1=1}"));
    }

    /**
     * @since 9.1.
     */
    @Test
    default void Bag_collectWithOccurrences()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        Bag<ObjectIntPair<Integer>> actual =
                bag.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
        Bag<ObjectIntPair<Integer>> expected =
                Bags.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1));
        assertEquals(expected, actual);

        Set<ObjectIntPair<Integer>> actual2 =
                bag.collectWithOccurrences(PrimitiveTuples::pair, Sets.mutable.empty());
        ImmutableSet<ObjectIntPair<Integer>> expected2 =
                Sets.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1));
        assertEquals(expected2, actual2);
    }
}
