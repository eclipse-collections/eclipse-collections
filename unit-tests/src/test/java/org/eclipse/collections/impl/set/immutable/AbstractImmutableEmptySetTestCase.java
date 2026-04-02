/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.immutable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.ImmutableBooleanSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractImmutableEmptySetTestCase extends AbstractImmutableSetTestCase
{
    @Test
    public void containsAll()
    {
        assertTrue(this.classUnderTest().castToSet().containsAll(new HashSet<>()));
        assertFalse(this.classUnderTest().castToSet().containsAll(UnifiedSet.newSetWith(1)));
    }

    @Override
    @Test
    public void newWith()
    {
        ImmutableSet<Integer> immutable = this.classUnderTest();
        Verify.assertSize(1, immutable.newWith(1).castToSet());
    }

    @Override
    @Test
    public void detect()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertNull(integers.detect(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertNull(integers.detectWith(Object::equals, Integer.valueOf(1)));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertFalse(integers.anySatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertFalse(integers.anySatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertTrue(integers.allSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertTrue(integers.allSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertTrue(integers.noneSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertTrue(integers.noneSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void getFirst()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertNull(integers.getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        assertNull(integers.getLast());
    }

    @Test
    public void getOnly()
    {
        assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOnly());
    }

    @Override
    @Test
    public void isEmpty()
    {
        ImmutableSet<Integer> list = this.classUnderTest();
        assertTrue(list.isEmpty());
        assertFalse(list.notEmpty());
    }

    @Override
    @Test
    public void min()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().min(Integer::compareTo));
    }

    @Override
    @Test
    public void max()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().max(Integer::compareTo));
    }

    @Test
    @Override
    public void min_null_throws()
    {
        // Not applicable for empty collections
    }

    @Test
    @Override
    public void max_null_throws()
    {
        // Not applicable for empty collections
    }

    @Override
    @Test
    public void min_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().min());
    }

    @Override
    @Test
    public void max_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().max());
    }

    @Test
    @Override
    public void min_null_throws_without_comparator()
    {
        // Not applicable for empty collections
    }

    @Test
    @Override
    public void max_null_throws_without_comparator()
    {
        // Not applicable for empty collections
    }

    @Override
    @Test
    public void minBy()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().minBy(String::valueOf));
    }

    @Override
    @Test
    public void maxBy()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().maxBy(String::valueOf));
    }

    @Override
    @Test
    public void zip()
    {
        ImmutableSet<Integer> immutableSet = this.classUnderTest();
        List<Object> nulls = Collections.nCopies(immutableSet.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(immutableSet.size() + 1, null);

        ImmutableSet<Pair<Integer, Object>> pairs = immutableSet.zip(nulls);
        assertEquals(immutableSet, pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        assertEquals(UnifiedSet.newSet(nulls), pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        ImmutableSet<Pair<Integer, Object>> pairsPlusOne = immutableSet.zip(nullsPlusOne);
        assertEquals(immutableSet, pairsPlusOne.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        assertEquals(UnifiedSet.newSet(nulls), pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        assertEquals(immutableSet.zip(nulls), immutableSet.zip(nulls, UnifiedSet.newSet()));
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        ImmutableSet<Integer> immutableSet = this.classUnderTest();
        ImmutableSet<Pair<Integer, Integer>> pairs = immutableSet.zipWithIndex();

        assertEquals(immutableSet, pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        assertEquals(
                UnifiedSet.<Integer>newSet(),
                pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo));

        assertEquals(
                immutableSet.zipWithIndex(),
                immutableSet.zipWithIndex(UnifiedSet.newSet()));
    }

    @Test
    public void chunk()
    {
        assertEquals(Lists.mutable.of(), this.classUnderTest().chunk(2));
    }

    @Override
    @Test
    public void chunk_zero_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.classUnderTest().chunk(0));
    }

    @Override
    @Test
    public void chunk_large_size()
    {
        assertEquals(Lists.mutable.of(), this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void union()
    {
        assertEquals(
                UnifiedSet.newSetWith(1, 2, 3),
                this.classUnderTest().union(UnifiedSet.newSetWith(1, 2, 3)));
    }

    @Override
    @Test
    public void unionInto()
    {
        assertEquals(
                UnifiedSet.newSetWith(1, 2, 3),
                this.classUnderTest().unionInto(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSet()));
    }

    @Override
    @Test
    public void intersect()
    {
        assertEquals(
                UnifiedSet.<String>newSet(),
                this.classUnderTest().intersect(UnifiedSet.newSetWith(1, 2, 3)));
    }

    @Override
    @Test
    public void intersectInto()
    {
        assertEquals(
                UnifiedSet.<String>newSet(),
                this.classUnderTest().intersectInto(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSet()));
    }

    @Override
    @Test
    public void difference()
    {
        ImmutableSet<Integer> set = this.classUnderTest();
        ImmutableSet<Integer> difference = set.difference(UnifiedSet.newSetWith(1, 2, 3, 999));
        assertEquals(UnifiedSet.<Integer>newSet(), difference);
        assertEquals(set, set.difference(UnifiedSet.newSetWith(999)));
    }

    @Override
    @Test
    public void differenceInto()
    {
        ImmutableSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> difference = set.differenceInto(UnifiedSet.newSetWith(1, 2, 3, 999), UnifiedSet.newSet());
        assertEquals(UnifiedSet.<Integer>newSet(), difference);
        assertEquals(set, set.differenceInto(UnifiedSet.newSetWith(99), UnifiedSet.newSet()));
    }

    @Override
    @Test
    public void symmetricDifference()
    {
        assertEquals(
                UnifiedSet.newSetWith(999),
                this.classUnderTest().symmetricDifference(UnifiedSet.newSetWith(999)));
    }

    @Override
    @Test
    public void symmetricDifferenceInto()
    {
        assertEquals(
                UnifiedSet.newSetWith(999),
                this.classUnderTest().symmetricDifferenceInto(UnifiedSet.newSetWith(999), UnifiedSet.newSet()));
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        ImmutableBooleanSet actual = integers.collectBoolean(PrimitiveFunctions.integerIsPositive());
        Verify.assertEmpty(actual);
    }

    @Override
    @Test
    public void collect_target()
    {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.classUnderTest().collect(object ->
        {
            throw new AssertionError();
        }, targetCollection);
        assertEquals(targetCollection, actual);
        assertSame(targetCollection, actual);
    }

    @Override
    @Test
    public void collectWith_target()
    {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.classUnderTest().collectWith((argument1, argument2) ->
        {
            throw new AssertionError();
        }, 1, targetCollection);
        assertEquals(targetCollection, actual);
        assertSame(targetCollection, actual);
    }
}
