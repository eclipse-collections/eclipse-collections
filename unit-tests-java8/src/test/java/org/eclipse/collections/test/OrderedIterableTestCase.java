/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.Iterator;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.ordered.SortedIterable;
import org.eclipse.collections.api.partition.ordered.PartitionOrderedIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public interface OrderedIterableTestCase extends RichIterableTestCase
{
    /**
     * @since 9.1.
     */
    @Test
    default void OrderedIterable_collectWithIndex()
    {
        RichIterable<ObjectIntPair<Integer>> pairs = ((OrderedIterable<Integer>) this.newWith(3, 2, 1, 0))
                .collectWithIndex(PrimitiveTuples::pair);
        assertEquals(
                IntLists.mutable.with(0, 1, 2, 3),
                pairs.collectInt(ObjectIntPair::getTwo, IntLists.mutable.empty()));
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(
                    Lists.mutable.with(3, 2, 1, 0),
                    pairs.collect(ObjectIntPair::getOne, Lists.mutable.empty()));
            case SORTED_NATURAL -> assertEquals(
                    Lists.mutable.with(0, 1, 2, 3),
                    pairs.collect(ObjectIntPair::getOne, Lists.mutable.empty()));
            default -> fail("Unexpected value: " + this.getOrderingType());
        }

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<ObjectIntPair<Integer>> pairsWithDuplicates = ((OrderedIterable<Integer>) this.newWith(3, 3, 3, 2, 2, 1, 0, 0, 0, 0))
                .collectWithIndex(PrimitiveTuples::pair);
        assertEquals(
                IntLists.mutable.with(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                pairsWithDuplicates.collectInt(ObjectIntPair::getTwo, IntLists.mutable.empty()));
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(
                    Lists.mutable.with(3, 3, 3, 2, 2, 1, 0, 0, 0, 0),
                    pairsWithDuplicates.collect(ObjectIntPair::getOne, Lists.mutable.empty()));
            case SORTED_NATURAL -> assertEquals(
                    Lists.mutable.with(0, 0, 0, 0, 1, 2, 2, 3, 3, 3),
                    pairsWithDuplicates.collect(ObjectIntPair::getOne, Lists.mutable.empty()));
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }

    /**
     * @since 9.1.
     */
    @Test
    default void OrderedIterable_collectWithIndexWithTarget()
    {
        RichIterable<ObjectIntPair<Integer>> pairs = ((OrderedIterable<Integer>) this.newWith(3, 2, 1, 0))
                .collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty());
        assertEquals(
                IntLists.mutable.with(0, 1, 2, 3),
                pairs.collectInt(ObjectIntPair::getTwo, IntLists.mutable.empty()));
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(
                    Lists.mutable.with(3, 2, 1, 0),
                    pairs.collect(ObjectIntPair::getOne, Lists.mutable.empty()));
            case SORTED_NATURAL -> assertEquals(
                    Lists.mutable.with(0, 1, 2, 3),
                    pairs.collect(ObjectIntPair::getOne, Lists.mutable.empty()));
            default -> fail("Unexpected value: " + this.getOrderingType());
        }

        RichIterable<ObjectIntPair<Integer>> setOfPairs = ((OrderedIterable<Integer>) this.newWith(3, 2, 1, 0))
                .collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty());
        assertEquals(
                IntSets.mutable.with(0, 1, 2, 3),
                setOfPairs.collectInt(ObjectIntPair::getTwo, IntSets.mutable.empty()));
        assertEquals(
                Sets.mutable.with(3, 2, 1, 0),
                setOfPairs.collect(ObjectIntPair::getOne, Sets.mutable.empty()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<ObjectIntPair<Integer>> pairsWithDuplicates = ((OrderedIterable<Integer>) this.newWith(3, 3, 3, 2, 2, 1, 0, 0, 0, 0))
                .collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty());
        assertEquals(
                IntLists.mutable.with(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                pairsWithDuplicates.collectInt(ObjectIntPair::getTwo, IntLists.mutable.empty()));
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(
                    Lists.mutable.with(3, 3, 3, 2, 2, 1, 0, 0, 0, 0),
                    pairsWithDuplicates.collect(ObjectIntPair::getOne, Lists.mutable.empty()));
            case SORTED_NATURAL -> assertEquals(
                    Lists.mutable.with(0, 0, 0, 0, 1, 2, 2, 3, 3, 3),
                    pairsWithDuplicates.collect(ObjectIntPair::getOne, Lists.mutable.empty()));
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }

    @Test
    default void OrderedIterable_getFirstOptional_empty()
    {
        assertSame(Optional.empty(), ((OrderedIterable<?>) this.newWith()).getFirstOptional());
    }

    @Test
    default void OrderedIterable_getFirstOptional()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(3, 2, 1);
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(Optional.of(3), iterable.getFirstOptional());
            case SORTED_NATURAL -> assertEquals(Optional.of(1), iterable.getFirstOptional());
            default -> fail("Unexpected value: " + this.getOrderingType());
        }

        if (!this.allowsDuplicates())
        {
            return;
        }

        OrderedIterable<Integer> iterableWithDuplicates = (OrderedIterable<Integer>) this.newWith(3, 3, 3, 2, 2, 1);
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(Optional.of(3), iterableWithDuplicates.getFirstOptional());
            case SORTED_NATURAL -> assertEquals(Optional.of(1), iterableWithDuplicates.getFirstOptional());
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }

    @Test
    default void OrderedIterable_getFirstOptional_null_element()
    {
        assertThrows(NullPointerException.class, () -> ((OrderedIterable<?>) this.newWith(new Object[]{null})).getFirstOptional());
    }

    @Test
    default void OrderedIterable_getLastOptional_empty()
    {
        assertSame(Optional.empty(), ((OrderedIterable<?>) this.newWith()).getLastOptional());
    }

    @Test
    default void OrderedIterable_getLastOptional()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(3, 2, 1);
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(Optional.of(1), iterable.getLastOptional());
            case SORTED_NATURAL -> assertEquals(Optional.of(3), iterable.getLastOptional());
            default -> fail("Unexpected value: " + this.getOrderingType());
        }

        if (!this.allowsDuplicates())
        {
            return;
        }

        OrderedIterable<Integer> iterableWithDuplicates = (OrderedIterable<Integer>) this.newWith(3, 3, 3, 2, 2, 1);
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(Optional.of(1), iterableWithDuplicates.getLastOptional());
            case SORTED_NATURAL -> assertEquals(Optional.of(3), iterableWithDuplicates.getLastOptional());
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }

    @Test
    default void OrderedIterable_getLastOptional_null_element()
    {
        assertThrows(NullPointerException.class, () -> ((OrderedIterable<?>) this.newWith(new Object[]{null})).getLastOptional());
    }

    @Override
    @Test
    default void Iterable_next()
    {
        RichIterableTestCase.super.Iterable_next();

        Iterator<Integer> iterator = this.newWith(3, 2, 1).iterator();
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL ->
            {
                assertEquals(Integer.valueOf(3), iterator.next());
                assertEquals(Integer.valueOf(2), iterator.next());
                assertEquals(Integer.valueOf(1), iterator.next());
            }
            case SORTED_NATURAL ->
            {
                assertEquals(Integer.valueOf(1), iterator.next());
                assertEquals(Integer.valueOf(2), iterator.next());
                assertEquals(Integer.valueOf(3), iterator.next());
            }
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }

    @Test
    default void OrderedIterable_zipWithIndex()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(
                    Lists.immutable.with(
                            Tuples.pair(4, 0),
                            Tuples.pair(3, 1),
                            Tuples.pair(2, 2),
                            Tuples.pair(1, 3)),
                    iterable.zipWithIndex().toList());
            case SORTED_NATURAL -> assertEquals(
                    Lists.immutable.with(
                            Tuples.pair(1, 0),
                            Tuples.pair(2, 1),
                            Tuples.pair(3, 2),
                            Tuples.pair(4, 3)),
                    iterable.zipWithIndex().toList());
            default -> fail("Unexpected value: " + this.getOrderingType());
        }

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableWithDuplicates = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(
                    Lists.immutable.with(
                            Tuples.pair(4, 0),
                            Tuples.pair(4, 1),
                            Tuples.pair(4, 2),
                            Tuples.pair(4, 3),
                            Tuples.pair(3, 4),
                            Tuples.pair(3, 5),
                            Tuples.pair(3, 6),
                            Tuples.pair(2, 7),
                            Tuples.pair(2, 8),
                            Tuples.pair(1, 9)),
                    iterableWithDuplicates.zipWithIndex().toList());
            case SORTED_NATURAL -> assertEquals(
                    Lists.immutable.with(
                            Tuples.pair(1, 0),
                            Tuples.pair(2, 1),
                            Tuples.pair(2, 2),
                            Tuples.pair(3, 3),
                            Tuples.pair(3, 4),
                            Tuples.pair(3, 5),
                            Tuples.pair(4, 6),
                            Tuples.pair(4, 7),
                            Tuples.pair(4, 8),
                            Tuples.pair(4, 9)),
                    iterableWithDuplicates.zipWithIndex().toList());
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }

    @Test
    default void OrderedIterable_zipWithIndex_target()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        MutableList<Pair<Integer, Integer>> target = Lists.mutable.empty();
        MutableList<Pair<Integer, Integer>> result = iterable.zipWithIndex(target);
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(
                    Lists.immutable.with(
                            Tuples.pair(4, 0),
                            Tuples.pair(3, 1),
                            Tuples.pair(2, 2),
                            Tuples.pair(1, 3)),
                    result);
            case SORTED_NATURAL -> assertEquals(
                    Lists.immutable.with(
                            Tuples.pair(1, 0),
                            Tuples.pair(2, 1),
                            Tuples.pair(3, 2),
                            Tuples.pair(4, 3)),
                    result);
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
        assertSame(target, result);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableWithDuplicates = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        MutableList<Pair<Integer, Integer>> targetWithDuplicates = Lists.mutable.empty();
        MutableList<Pair<Integer, Integer>> resultWithDuplicates = iterableWithDuplicates.zipWithIndex(targetWithDuplicates);
        switch (this.getOrderingType())
        {
            case UNORDERED -> throw new AssertionError("OrderedIterable should not have UNORDERED ordering type");
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertEquals(
                    Lists.immutable.with(
                            Tuples.pair(4, 0),
                            Tuples.pair(4, 1),
                            Tuples.pair(4, 2),
                            Tuples.pair(4, 3),
                            Tuples.pair(3, 4),
                            Tuples.pair(3, 5),
                            Tuples.pair(3, 6),
                            Tuples.pair(2, 7),
                            Tuples.pair(2, 8),
                            Tuples.pair(1, 9)),
                    resultWithDuplicates);
            case SORTED_NATURAL -> assertEquals(
                    Lists.immutable.with(
                            Tuples.pair(1, 0),
                            Tuples.pair(2, 1),
                            Tuples.pair(2, 2),
                            Tuples.pair(3, 3),
                            Tuples.pair(3, 4),
                            Tuples.pair(3, 5),
                            Tuples.pair(4, 6),
                            Tuples.pair(4, 7),
                            Tuples.pair(4, 8),
                            Tuples.pair(4, 9)),
                    resultWithDuplicates);
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
        assertSame(targetWithDuplicates, resultWithDuplicates);
    }

    @Test
    default void OrderedIterable_forEach_from_to()
    {
        OrderedIterable<Integer> integers = (OrderedIterable<Integer>) this.newWith(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        MutableList<Integer> result = Lists.mutable.empty();
        integers.forEach(5, 7, result::add);
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(5, 6, 7);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(4, 3, 2);
                },
                result);

        MutableList<Integer> result2 = Lists.mutable.empty();
        integers.forEach(5, 5, result2::add);
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(5);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(4);
                },
                result2);

        MutableList<Integer> result3 = Lists.mutable.empty();
        integers.forEach(0, 9, result3::add);
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
                },
                result3);

        MutableList<Integer> result4 = Lists.mutable.empty();
        integers.forEach(0, 0, result4::add);
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(0);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(9);
                },
                result4);

        MutableList<Integer> result5 = Lists.mutable.empty();
        integers.forEach(9, 9, result5::add);
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(9);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(0);
                },
                result5);

        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(-1, 0, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, -1, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, 10, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(10, 0, result::add));
    }

    @Test
    default void OrderedIterable_forEach_from_to_reverse_order()
    {
        OrderedIterable<Integer> integers = (OrderedIterable<Integer>) this.newWith(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        MutableList<Integer> result = Lists.mutable.empty();
        integers.forEach(7, 5, result::add);
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> Lists.immutable.with(7, 6, 5);
                    case UNORDERED, INSERTION_ORDER, SORTED_REVERSE_NATURAL -> Lists.immutable.with(2, 3, 4);
                },
                result);
    }

    @Test
    default void OrderedIterable_partitionWhile()
    {
        // Sorted iterables reorder elements, so we need a monotonic predicate that respects the sort order.
        switch (this.getOrderingType())
        {
            case SORTED_REVERSE_NATURAL, INSERTION_ORDER ->
            {
                OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3, 2, 1);
                PartitionOrderedIterable<Integer> partition1 = iterable.partitionWhile(each -> each > 2);
                assertIterablesEqual(this.newWith(6, 5, 4, 3), partition1.getSelected());
                assertIterablesEqual(this.newWith(2, 1), partition1.getRejected());
            }
            case SORTED_NATURAL ->
            {
                OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3, 2, 1);
                PartitionOrderedIterable<Integer> partition1 = iterable.partitionWhile(each -> each < 3);
                assertIterablesEqual(this.newWith(1, 2), partition1.getSelected());
                assertIterablesEqual(this.newWith(3, 4, 5, 6), partition1.getRejected());
            }
            default -> fail("Unexpected value: " + this.getOrderingType());
        }

        OrderedIterable<Integer> iterable2 = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3, 2, 1);
        PartitionOrderedIterable<Integer> partition2 = iterable2.partitionWhile(each -> true);
        assertIterablesEqual(iterable2, partition2.getSelected());
        assertIterablesEqual(this.newWith(), partition2.getRejected());

        PartitionOrderedIterable<Integer> partition3 = iterable2.partitionWhile(each -> false);
        assertIterablesEqual(this.newWith(), partition3.getSelected());
        assertIterablesEqual(iterable2, partition3.getRejected());

        OrderedIterable<Object> empty = (OrderedIterable<Object>) this.newWith();
        PartitionOrderedIterable<Object> partition4 = empty.partitionWhile(each -> {
            fail("Should not evaluate the predicate");
            return true;
        });
        assertIterablesEqual(this.newWith(), partition4.getSelected());
        assertIterablesEqual(this.newWith(), partition4.getRejected());

        if (!this.allowsDuplicates())
        {
            return;
        }

        switch (this.getOrderingType())
        {
            case SORTED_REVERSE_NATURAL, INSERTION_ORDER ->
            {
                OrderedIterable<Integer> iterable =
                        (OrderedIterable<Integer>) this.newWith(6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1);
                PartitionOrderedIterable<Integer> partition1 = iterable.partitionWhile(each -> each > 2);
                assertIterablesEqual(this.newWith(6, 6, 5, 5, 4, 4, 3, 3), partition1.getSelected());
                assertIterablesEqual(this.newWith(2, 2, 1, 1), partition1.getRejected());
            }
            case SORTED_NATURAL ->
            {
                OrderedIterable<Integer> iterable =
                        (OrderedIterable<Integer>) this.newWith(6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1);
                PartitionOrderedIterable<Integer> partition1 = iterable.partitionWhile(each -> each < 3);
                assertIterablesEqual(this.newWith(1, 1, 2, 2), partition1.getSelected());
                assertIterablesEqual(this.newWith(3, 3, 4, 4, 5, 5, 6, 6), partition1.getRejected());
            }
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }

    @Test
    default void OrderedIterable_takeWhile()
    {
        // Sorted iterables reorder elements, so we need a monotonic predicate that respects the sort order.
        switch (this.getOrderingType())
        {
            case SORTED_REVERSE_NATURAL, INSERTION_ORDER ->
            {
                OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3);
                OrderedIterable<Integer> take1 = iterable.takeWhile(each -> each > 2);
                assertIterablesEqual(this.newWith(6, 5, 4, 3), take1);
                if (iterable instanceof SortedIterable<Integer> sortedIterable)
                {
                    assertEquals(sortedIterable.comparator(), ((SortedIterable<Integer>) take1).comparator());
                }
            }
            case SORTED_NATURAL ->
            {
                OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3);
                OrderedIterable<Integer> take1 = iterable.takeWhile(each -> each < 6);
                assertIterablesEqual(this.newWith(3, 4, 5), take1);
                if (iterable instanceof SortedIterable<Integer> sortedIterable)
                {
                    assertEquals(sortedIterable.comparator(), ((SortedIterable<Integer>) take1).comparator());
                }
            }
            default -> fail("Unexpected value: " + this.getOrderingType());
        }

        OrderedIterable<Integer> iterable2 = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3);
        OrderedIterable<Integer> take2 = iterable2.takeWhile(each -> true);
        assertIterablesEqual(iterable2, take2);
        if (iterable2 instanceof SortedIterable<Integer> sortedIterable)
        {
            assertEquals(sortedIterable.comparator(), ((SortedIterable<Integer>) take2).comparator());
        }

        OrderedIterable<Integer> take3 = iterable2.takeWhile(each -> false);
        assertIterablesEqual(this.newWith(), take3);

        OrderedIterable<Object> empty = (OrderedIterable<Object>) this.newWith();
        OrderedIterable<Object> take4 = empty.takeWhile(each -> {
            fail("Should not evaluate the predicate");
            return true;
        });
        assertIterablesEqual(this.newWith(), take4);

        if (!this.allowsDuplicates())
        {
            return;
        }

        switch (this.getOrderingType())
        {
            case SORTED_REVERSE_NATURAL, INSERTION_ORDER ->
            {
                OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 6, 5, 5, 4, 4, 3, 3);
                OrderedIterable<Integer> take1 = iterable.takeWhile(each -> each > 2);
                assertIterablesEqual(this.newWith(6, 6, 5, 5, 4, 4, 3, 3), take1);
                if (iterable instanceof SortedIterable<Integer> sortedIterable)
                {
                    assertEquals(sortedIterable.comparator(), ((SortedIterable<Integer>) take1).comparator());
                }
            }
            case SORTED_NATURAL ->
            {
                OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 6, 5, 5, 4, 4, 3, 3);
                OrderedIterable<Integer> take1 = iterable.takeWhile(each -> each < 6);
                assertIterablesEqual(this.newWith(3, 3, 4, 4, 5, 5), take1);
                if (iterable instanceof SortedIterable<Integer> sortedIterable)
                {
                    assertEquals(sortedIterable.comparator(), ((SortedIterable<Integer>) take1).comparator());
                }
            }
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }

    @Test
    default void OrderedIterable_dropWhile()
    {
        // Sorted iterables reorder elements, so we need a monotonic predicate that respects the sort order.
        switch (this.getOrderingType())
        {
            case SORTED_REVERSE_NATURAL, INSERTION_ORDER ->
            {
                OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3, 2, 1);
                OrderedIterable<Integer> drop1 = iterable.dropWhile(each -> each > 2);
                assertIterablesEqual(this.newWith(2, 1), drop1);
            }
            case SORTED_NATURAL ->
            {
                OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3, 2, 1);
                OrderedIterable<Integer> drop1 = iterable.dropWhile(each -> each < 3);
                assertIterablesEqual(this.newWith(3, 4, 5, 6), drop1);
            }
            default -> fail("Unexpected value: " + this.getOrderingType());
        }

        OrderedIterable<Integer> iterable2 = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3, 2, 1);
        OrderedIterable<Integer> drop2 = iterable2.dropWhile(each -> true);
        assertIterablesEqual(this.newWith(), drop2);

        OrderedIterable<Integer> drop3 = iterable2.dropWhile(each -> false);
        assertIterablesEqual(iterable2, drop3);

        OrderedIterable<Object> empty = (OrderedIterable<Object>) this.newWith();
        OrderedIterable<Object> drop4 = empty.dropWhile(each -> {
            fail("Should not evaluate the predicate");
            return true;
        });
        assertIterablesEqual(this.newWith(), drop4);

        if (!this.allowsDuplicates())
        {
            return;
        }

        switch (this.getOrderingType())
        {
            case SORTED_REVERSE_NATURAL, INSERTION_ORDER ->
            {
                OrderedIterable<Integer> iterable =
                        (OrderedIterable<Integer>) this.newWith(6, 6, 4, 4, 5, 5, 3, 3, 2, 2, 1, 1);
                OrderedIterable<Integer> drop1 = iterable.dropWhile(each -> each > 2);
                assertIterablesEqual(this.newWith(2, 2, 1, 1), drop1);
            }
            case SORTED_NATURAL ->
            {
                OrderedIterable<Integer> iterable =
                        (OrderedIterable<Integer>) this.newWith(6, 6, 4, 4, 5, 5, 3, 3, 2, 2, 1, 1);
                OrderedIterable<Integer> drop1 = iterable.dropWhile(each -> each < 3);
                assertIterablesEqual(this.newWith(3, 3, 4, 4, 5, 5, 6, 6), drop1);
            }
            default -> fail("Unexpected value: " + this.getOrderingType());
        }
    }
}
