/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.ordered.SortedIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("UnnecessaryCodeBlock")
public interface SortedNaturalOrderTestCase extends OrderedIterableTestCase
{
    @Override
    default OrderingType getOrderingType()
    {
        return OrderingType.SORTED_NATURAL;
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        RichIterable<Integer> iterable = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        assertEquals(
                "[1, 2, 2, 3, 3, 3, 4, 4, 4, 4]",
                iterable.toString());
        assertEquals(
                "[1, 2, 2, 3, 3, 3, 4, 4, 4, 4]",
                iterable.asLazy().toString());
    }

    @Override
    @Test
    default void RichIterable_collect()
    {
        RichIterable<Integer> iterable = this.newWith(1, 1, 2, 2, 3, 3, 11, 11, 12, 12, 13, 13);

        assertIterablesEqual(
                this.getExpectedTransformed(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3),
                iterable.collect(i -> i % 10));

        {
            MutableCollection<Integer> target = this.newMutableForTransform();
            MutableCollection<Integer> result = iterable.collect(i -> i % 10, target);
            assertIterablesEqual(this.newMutableForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedTransformed(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3),
                iterable.collectWith((i, mod) -> i % mod, 10));

        MutableCollection<Integer> target = this.newMutableForTransform();
        MutableCollection<Integer> result = iterable.collectWith((i, mod) -> i % mod, 10, target);
        assertIterablesEqual(this.newMutableForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), result);
        assertSame(target, result);
    }

    @Override
    @Test
    default void RichIterable_collectIf()
    {
        RichIterable<Integer> iterable = this.newWith(1, 1, 2, 2, 3, 3, 11, 11, 12, 12, 13, 13);

        assertIterablesEqual(
                this.getExpectedTransformed(1, 1, 3, 3, 1, 1, 3, 3),
                iterable.collectIf(i -> i % 2 != 0, i -> i % 10));

        MutableCollection<Integer> target = this.newMutableForTransform();
        MutableCollection<Integer> result = iterable.collectIf(i -> i % 2 != 0, i -> i % 10, target);
        assertIterablesEqual(this.newMutableForTransform(1, 1, 3, 3, 1, 1, 3, 3), result);
        assertSame(target, result);
    }


    @Override
    @Test
    default void RichIterable_detect()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 3);

        assertThat(iterable.detect(Predicates.greaterThan(0)), is(1));
        assertThat(iterable.detect(Predicates.greaterThan(1)), is(2));
        assertThat(iterable.detect(Predicates.greaterThan(2)), is(3));
        assertThat(iterable.detect(Predicates.greaterThan(3)), nullValue());

        assertThat(iterable.detect(Predicates.lessThan(1)), nullValue());
        assertThat(iterable.detect(Predicates.lessThan(2)), is(1));
        assertThat(iterable.detect(Predicates.lessThan(3)), is(1));
        assertThat(iterable.detect(Predicates.lessThan(4)), is(1));

        assertThat(iterable.detectWith(Predicates2.greaterThan(), 0), is(1));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 1), is(2));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 2), is(3));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 3), nullValue());

        assertThat(iterable.detectWith(Predicates2.lessThan(), 1), nullValue());
        assertThat(iterable.detectWith(Predicates2.lessThan(), 2), is(1));
        assertThat(iterable.detectWith(Predicates2.lessThan(), 3), is(1));
        assertThat(iterable.detectWith(Predicates2.lessThan(), 4), is(1));

        assertThat(iterable.detectIfNone(Predicates.greaterThan(0), () -> 4), is(1));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(1), () -> 4), is(2));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(2), () -> 4), is(3));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(3), () -> 4), is(4));

        assertThat(iterable.detectIfNone(Predicates.lessThan(1), () -> 4), is(4));
        assertThat(iterable.detectIfNone(Predicates.lessThan(2), () -> 4), is(1));
        assertThat(iterable.detectIfNone(Predicates.lessThan(3), () -> 4), is(1));
        assertThat(iterable.detectIfNone(Predicates.lessThan(4), () -> 4), is(1));

        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 0, () -> 4), is(1));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 1, () -> 4), is(2));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 2, () -> 4), is(3));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 3, () -> 4), is(4));

        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 1, () -> 4), is(4));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 2, () -> 4), is(1));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 3, () -> 4), is(1));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 4, () -> 4), is(1));

        assertThat(iterable.detectOptional(Predicates.greaterThan(0)), is(Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(1)), is(Optional.of(2)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(2)), is(Optional.of(3)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(3)), is(Optional.empty()));

        assertThat(iterable.detectOptional(Predicates.lessThan(1)), is(Optional.empty()));
        assertThat(iterable.detectOptional(Predicates.lessThan(2)), is(Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.lessThan(3)), is(Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.lessThan(4)), is(Optional.of(1)));

        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 0), is(Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 1), is(Optional.of(2)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 2), is(Optional.of(3)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 3), is(Optional.empty()));

        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 1), is(Optional.empty()));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 2), is(Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 3), is(Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 4), is(Optional.of(1)));
    }

    @Override
    @Test
    default void RichIterable_minBy_maxBy()
    {
        assertEquals("ca", this.newWith("ab", "bc", "ca", "da", "ed").minBy(string -> string.charAt(string.length() - 1)));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().minBy(string -> string.charAt(string.length() - 1)));

        assertEquals("cz", this.newWith("ew", "dz", "cz", "bx", "ay").maxBy(string -> string.charAt(string.length() - 1)));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().maxBy(string -> string.charAt(string.length() - 1)));
    }

    @Override
    @Test
    default void RichIterable_makeString_appendString()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(
                "1, 2, 2, 3, 3, 3, 4, 4, 4, 4",
                iterable.makeString());

        assertEquals(
                "1/2/2/3/3/3/4/4/4/4",
                iterable.makeString("/"));

        assertEquals(
                "[1/2/2/3/3/3/4/4/4/4]",
                iterable.makeString("[", "/", "]"));

        StringBuilder builder1 = new StringBuilder();
        iterable.appendString(builder1);
        assertEquals(
                "1, 2, 2, 3, 3, 3, 4, 4, 4, 4",
                builder1.toString());

        StringBuilder builder2 = new StringBuilder();
        iterable.appendString(builder2, "/");
        assertEquals(
                "1/2/2/3/3/3/4/4/4/4",
                builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        iterable.appendString(builder3, "[", "/", "]");
        assertEquals(
                "[1/2/2/3/3/3/4/4/4/4]",
                builder3.toString());
    }

    @Override
    @Test
    default void RichIterable_toList()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertIterablesEqual(
                Lists.immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4),
                iterable.toList());

        MutableList<Integer> target = Lists.mutable.empty();
        iterable.each(target::add);
        assertIterablesEqual(
                target,
                iterable.toList());
    }

    @Override
    @Test
    default void RichIterable_into()
    {
        assertIterablesEqual(
                Lists.immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4),
                this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4).into(Lists.mutable.empty()));
    }

    @Override
    @Test
    default void OrderedIterable_getFirst()
    {
        assertEquals(Integer.valueOf(1), this.newWith(1, 2, 2, 3, 3, 3).getFirst());
    }

    @Override
    @Test
    default void OrderedIterable_getLast()
    {
        assertEquals(Integer.valueOf(3), this.newWith(1, 2, 2, 3, 3, 3).getLast());
    }

    @Override
    @Test
    default void OrderedIterable_next()
    {
        Iterator<Integer> iterator = this.newWith(1, 2, 3).iterator();
        assertEquals(Integer.valueOf(1), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(3), iterator.next());
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    default void OrderedIterable_collectWithIndex()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(
                Lists.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(1), 0),
                        PrimitiveTuples.pair(Integer.valueOf(2), 1),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(3), 4),
                        PrimitiveTuples.pair(Integer.valueOf(3), 5),
                        PrimitiveTuples.pair(Integer.valueOf(4), 6),
                        PrimitiveTuples.pair(Integer.valueOf(4), 7),
                        PrimitiveTuples.pair(Integer.valueOf(4), 8),
                        PrimitiveTuples.pair(Integer.valueOf(4), 9)),
                iterable.collectWithIndex(PrimitiveTuples::pair).toList());
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    default void OrderedIterable_collectWithIndexWithTarget()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(
                Lists.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(1), 0),
                        PrimitiveTuples.pair(Integer.valueOf(2), 1),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(3), 4),
                        PrimitiveTuples.pair(Integer.valueOf(3), 5),
                        PrimitiveTuples.pair(Integer.valueOf(4), 6),
                        PrimitiveTuples.pair(Integer.valueOf(4), 7),
                        PrimitiveTuples.pair(Integer.valueOf(4), 8),
                        PrimitiveTuples.pair(Integer.valueOf(4), 9)),
                iterable.collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty()));
    }

    @Override
    @Test
    default void OrderedIterable_zipWithIndex()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(
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
                iterable.zipWithIndex().toList());
    }

    @Override
    @Test
    default void OrderedIterable_zipWithIndex_target()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        MutableList<Pair<Integer, Integer>> target = Lists.mutable.empty();
        MutableList<Pair<Integer, Integer>> result = iterable.zipWithIndex(target);
        assertEquals(
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
                result);
        assertSame(target, result);
    }

    @Override
    @Test
    default void OrderedIterable_injectIntoWithIndex()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);

        // Weighted sum (element * index): 1*0 + 2*1 + 2*2 + 3*3 + 3*4 + 3*5 + 4*6 + 4*7 + 4*8 + 4*9 = 162
        assertEquals(Integer.valueOf(163), iterable.injectIntoWithIndex(
                1,
                (sum, each, index) -> sum + each * index));
        assertEquals(Integer.valueOf(162), iterable.injectIntoWithIndex(
                0,
                (sum, each, index) -> sum + each * index));
    }

    @Test
    default void SortedIterable_comparator()
    {
        SortedIterable<?> iterable = (SortedIterable<?>) this.newWith();
        assertNull(iterable.comparator());
    }
}
