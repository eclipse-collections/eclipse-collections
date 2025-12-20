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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Triple;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(
                Lists.mutable.with(3, 2, 1, 0),
                pairs.collect(ObjectIntPair::getOne, Lists.mutable.empty()));
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
        assertEquals(
                Lists.mutable.with(3, 2, 1, 0),
                pairs.collect(ObjectIntPair::getOne, Lists.mutable.empty()));

        RichIterable<ObjectIntPair<Integer>> setOfPairs = ((OrderedIterable<Integer>) this.newWith(3, 2, 1, 0))
                .collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty());
        assertEquals(
                IntSets.mutable.with(0, 1, 2, 3),
                setOfPairs.collectInt(ObjectIntPair::getTwo, IntSets.mutable.empty()));
        assertEquals(
                Sets.mutable.with(3, 2, 1, 0),
                setOfPairs.collect(ObjectIntPair::getOne, Sets.mutable.empty()));
    }

    @Test
    default void OrderedIterable_getFirst()
    {
        assertEquals(Integer.valueOf(3), this.newWith(3, 3, 3, 2, 2, 1).getFirst());
    }

    @Test
    default void OrderedIterable_getFirstOptional_empty()
    {
        assertSame(Optional.empty(), ((OrderedIterable<?>) this.newWith()).getFirstOptional());
    }

    @Test
    default void OrderedIterable_getFirstOptional()
    {
        assertEquals(Optional.of(3), ((OrderedIterable<?>) this.newWith(3, 3, 3, 2, 2, 1)).getFirstOptional());
    }

    @Test
    default void OrderedIterable_getFirstOptional_null_element()
    {
        assertThrows(NullPointerException.class, () -> ((OrderedIterable<?>) this.newWith(new Object[]{null})).getFirstOptional());
    }

    @Test
    default void OrderedIterable_getLast()
    {
        assertEquals(Integer.valueOf(1), this.newWith(3, 3, 3, 2, 2, 1).getLast());
    }

    @Test
    default void OrderedIterable_getLastOptional_empty()
    {
        assertSame(Optional.empty(), ((OrderedIterable<?>) this.newWith()).getLastOptional());
    }

    @Test
    default void OrderedIterable_getLastOptional()
    {
        assertEquals(Optional.of(1), ((OrderedIterable<?>) this.newWith(3, 3, 3, 2, 2, 1)).getLastOptional());
    }

    @Test
    default void OrderedIterable_getLastOptional_null_element()
    {
        assertThrows(NullPointerException.class, () -> ((OrderedIterable<?>) this.newWith(new Object[]{null})).getLastOptional());
    }

    @Test
    default void OrderedIterable_next()
    {
        Iterator<Integer> iterator = this.newWith(3, 2, 1).iterator();
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(1), iterator.next());
    }

    @Test
    default void OrderedIterable_min()
    {
        Holder<Integer> first = new Holder<>(-1);
        Holder<Integer> second = new Holder<>(-1);
        assertSame(first, this.newWith(new Holder<>(2), first, new Holder<>(0), second).min());
    }

    @Test
    default void OrderedIterable_max()
    {
        Holder<Integer> first = new Holder<>(1);
        Holder<Integer> second = new Holder<>(1);
        assertSame(first, this.newWith(new Holder<>(-2), first, new Holder<>(0), second).max());
    }

    @Test
    default void OrderedIterable_min_comparator()
    {
        Holder<Integer> first = new Holder<>(1);
        Holder<Integer> second = new Holder<>(1);
        assertSame(first, this.newWith(new Holder<>(-2), first, new Holder<>(0), second).min(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void OrderedIterable_max_comparator()
    {
        Holder<Integer> first = new Holder<>(-1);
        Holder<Integer> second = new Holder<>(-1);
        assertSame(first, this.newWith(new Holder<>(2), first, new Holder<>(0), second).max(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void OrderedIterable_zipWithIndex()
    {
        RichIterable<Integer> iterable = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        assertEquals(
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
                iterable.zipWithIndex().toList());
    }

    @Test
    default void OrderedIterable_zipWithIndex_target()
    {
        RichIterable<Integer> iterable = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        MutableList<Pair<Integer, Integer>> target = Lists.mutable.empty();
        MutableList<Pair<Integer, Integer>> result = iterable.zipWithIndex(target);
        assertEquals(
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
                result);
        assertSame(target, result);
    }

    @Test
    default void OrderedIterable_injectIntoWithIndex()
    {
        RichIterable<Integer> emptyIterable = this.newWith();
        String actual0 = ((OrderedIterable<Integer>)emptyIterable).injectIntoWithIndex("foo",
                (init, curr, idx) -> "bar");
        assertEquals("foo", actual0, "injectIntoWithIndex called on empty iterable should return injected value");

        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>)this.newWith(3, 2, 1, 0);

        ImmutableList<Pair<Integer, Integer>> expected = Lists.immutable.with(
                Tuples.pair(3, 0),
                Tuples.pair(2, 1),
                Tuples.pair(1, 2),
                Tuples.pair(0, 3)
        );
        MutableList<Pair<Integer, Integer>> injected = Lists.mutable.empty();
        MutableList<Pair<Integer, Integer>> actual = iterable.injectIntoWithIndex(injected, (coll, each, index) -> {
            coll.add(Tuples.pair(each, index));
            return coll;
        });

        assertEquals(expected, actual);
        assertSame(injected, actual);
    }
}
