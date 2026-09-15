/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.immutable;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.ObjectIntProcedures;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.primitive.IntInterval;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ImmutableEmptyListTest extends AbstractImmutableListTestCase
{
    @Override
    protected ImmutableList<Integer> classUnderTest()
    {
        return Lists.immutable.empty();
    }

    @Override
    @Test
    public void indexOf()
    {
        assertEquals(-1, this.classUnderTest().indexOf(1));
        assertEquals(-1, this.classUnderTest().indexOf(null));
        ImmutableList<Integer> immutableList = this.classUnderTest().newWith(null);
        assertEquals(immutableList.size() - 1, immutableList.indexOf(null));
        assertEquals(-1, this.classUnderTest().indexOf(Integer.MAX_VALUE));
    }

    @Override
    @Test
    public void lastIndexOf()
    {
        assertEquals(-1, this.classUnderTest().lastIndexOf(1));
        assertEquals(-1, this.classUnderTest().lastIndexOf(null));
        assertEquals(-1, this.classUnderTest().lastIndexOf(null));
        ImmutableList<Integer> immutableList = this.classUnderTest().newWith(null);
        assertEquals(immutableList.size() - 1, immutableList.lastIndexOf(null));
        assertEquals(-1, this.classUnderTest().lastIndexOf(Integer.MAX_VALUE));
    }

    @Test
    public void newWithout()
    {
        assertSame(Lists.immutable.empty(), Lists.immutable.empty().newWithout(1));
        assertSame(Lists.immutable.empty(), Lists.immutable.empty().newWithoutAll(Interval.oneTo(3)));
    }

    @Override
    @Test
    public void reverseForEach()
    {
        ImmutableList<Integer> list = Lists.immutable.empty();
        MutableList<Integer> result = Lists.mutable.empty();
        list.reverseForEach(CollectionAddProcedure.on(result));
        assertEquals(list, result);
    }

    @Override
    @Test
    public void reverseForEachWithIndex()
    {
        ImmutableList<Object> list = Lists.immutable.empty();
        list.reverseForEachWithIndex((each, index) -> fail());
    }

    @Override
    @Test
    public void forEachFromTo()
    {
        MutableList<Integer> result = Lists.mutable.empty();
        MutableList<Integer> reverseResult = Lists.mutable.empty();
        ImmutableList<Integer> list = this.classUnderTest();
        assertThrows(IndexOutOfBoundsException.class, () -> list.forEach(0, list.size() - 1, CollectionAddProcedure.on(result)));
        assertThrows(IndexOutOfBoundsException.class, () -> list.forEach(list.size() - 1, 0, CollectionAddProcedure.on(reverseResult)));
    }

    @Override
    @Test
    public void forEachWithIndexFromTo()
    {
        MutableList<Integer> result = Lists.mutable.empty();
        MutableList<Integer> reverseResult = Lists.mutable.empty();
        ImmutableList<Integer> list = this.classUnderTest();
        assertThrows(IndexOutOfBoundsException.class, () -> list.forEachWithIndex(0, list.size() - 1, ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(result))));
        assertThrows(IndexOutOfBoundsException.class, () -> list.forEachWithIndex(list.size() - 1, 0, ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(reverseResult))));
    }

    @Override
    @Test
    public void detect()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertNull(integers.detect(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertNull(integers.detectWith(Object::equals, Integer.valueOf(1)));
    }

    @Override
    @Test
    public void distinct()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertNotNull(integers.distinct());
        assertTrue(integers.isEmpty());
    }

    @Override
    @Test
    public void countWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertEquals(0, integers.countWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void corresponds()
    {
        //Evaluates true for all empty lists and false for all non-empty lists

        assertTrue(this.classUnderTest().corresponds(Lists.mutable.empty(), Predicates2.alwaysFalse()));

        ImmutableList<Integer> integers = this.classUnderTest().newWith(Integer.valueOf(1));
        assertFalse(this.classUnderTest().corresponds(integers, Predicates2.alwaysTrue()));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertTrue(integers.allSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertTrue(integers.allSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertTrue(integers.noneSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertTrue(integers.noneSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertFalse(integers.anySatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertFalse(integers.anySatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void getFirst()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertNull(integers.getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        assertNull(integers.getLast());
    }

    @Test
    public void getOnly()
    {
        ImmutableList<Integer> list = this.classUnderTest();
        assertThrows(IllegalStateException.class, () -> list.getOnly());
    }

    @Override
    @Test
    public void isEmpty()
    {
        ImmutableList<Integer> list = this.classUnderTest();
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
    public void subList()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> this.classUnderTest().subList(0, 1));
    }

    @Override
    @Test
    public void zip()
    {
        ImmutableList<Integer> immutableList = this.classUnderTest();
        List<Object> nulls = Collections.nCopies(immutableList.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(immutableList.size() + 1, null);

        ImmutableList<Pair<Integer, Object>> pairs = immutableList.zip(nulls);
        assertEquals(immutableList, pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        assertEquals(nulls, pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        ImmutableList<Pair<Integer, Object>> pairsPlusOne = immutableList.zip(nullsPlusOne);
        assertEquals(immutableList, pairsPlusOne.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        assertEquals(nulls, pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        assertEquals(immutableList.zip(nulls), immutableList.zip(nulls, FastList.newList()));
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        ImmutableList<Integer> immutableList = this.classUnderTest();
        ImmutableList<Pair<Integer, Integer>> pairs = immutableList.zipWithIndex();

        assertEquals(immutableList, pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        assertEquals(FastList.<Integer>newList(), pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo));

        assertEquals(immutableList.zipWithIndex(), immutableList.zipWithIndex(FastList.newList()));
    }

    @Test
    public void chunk()
    {
        assertEquals(Lists.mutable.empty(), this.classUnderTest().chunk(2));
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
        assertEquals(this.classUnderTest(), this.classUnderTest().chunk(10));
        Verify.assertInstanceOf(ImmutableList.class, this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        ImmutableList<Integer> immutable = this.classUnderTest();
        MutableList<Integer> mutable = FastList.newList(immutable);
        Verify.assertEqualsAndHashCode(immutable, mutable);
        Verify.assertPostSerializedIdentity(immutable);
        assertNotEquals(immutable, UnifiedSet.newSet(mutable));
    }

    @Override
    @Test
    public void take()
    {
        ImmutableList<Integer> immutableList = this.classUnderTest();
        assertSame(immutableList, immutableList.take(0));
        assertSame(immutableList, immutableList.take(10));
        assertSame(immutableList, immutableList.take(Integer.MAX_VALUE));
    }

    @Override
    @Test
    public void takeWhile()
    {
        assertEquals(Lists.immutable.empty(), this.classUnderTest().takeWhile(ignored -> true));
        assertEquals(Lists.immutable.empty(), this.classUnderTest().takeWhile(ignored -> false));
    }

    @Override
    @Test
    public void drop()
    {
        super.drop();

        ImmutableList<Integer> immutableList = this.classUnderTest();
        assertSame(immutableList, immutableList.drop(10));
        assertSame(immutableList, immutableList.drop(0));
        assertSame(immutableList, immutableList.drop(Integer.MAX_VALUE));
    }

    @Override
    @Test
    public void dropWhile()
    {
        super.dropWhile();

        assertEquals(Lists.immutable.empty(), this.classUnderTest().dropWhile(ignored -> true));
        assertEquals(Lists.immutable.empty(), this.classUnderTest().dropWhile(ignored -> false));
    }

    @Override
    @Test
    public void partitionWhile()
    {
        super.partitionWhile();

        PartitionImmutableList<Integer> partition1 = this.classUnderTest().partitionWhile(ignored -> true);
        assertEquals(Lists.immutable.empty(), partition1.getSelected());
        assertEquals(Lists.immutable.empty(), partition1.getRejected());

        PartitionImmutableList<Integer> partiton2 = this.classUnderTest().partitionWhile(ignored -> false);
        assertEquals(Lists.immutable.empty(), partiton2.getSelected());
        assertEquals(Lists.immutable.empty(), partiton2.getRejected());
    }

    @Override
    @Test
    public void listIterator()
    {
        ListIterator<Integer> it = this.classUnderTest().listIterator();
        assertFalse(it.hasPrevious());
        assertEquals(-1, it.previousIndex());
        assertEquals(0, it.nextIndex());

        assertThrows(NoSuchElementException.class, it::next);

        assertThrows(UnsupportedOperationException.class, it::remove);

        assertThrows(UnsupportedOperationException.class, () -> it.add(null));
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

    @Test
    public void binarySearch()
    {
        ListIterable<Integer> sortedList = this.classUnderTest();
        assertEquals(-1, sortedList.binarySearch(1));
    }

    @Test
    public void binarySearchWithComparator()
    {
        ListIterable<Integer> sortedList = this.classUnderTest();
        assertEquals(-1, sortedList.binarySearch(1, Integer::compareTo));
    }

    @Override
    @Test
    public void detectIndex()
    {
        // any predicate will result in -1
        assertEquals(-1, this.classUnderTest().detectIndex(Predicates.alwaysTrue()));
    }

    @Override
    @Test
    public void detectLastIndex()
    {
        // any predicate will result in -1
        assertEquals(-1, this.classUnderTest().detectLastIndex(Predicates.alwaysTrue()));
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    public void collectWithIndex()
    {
        Verify.assertEmpty(this.classUnderTest().collectWithIndex(PrimitiveTuples::pair));
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    public void collectWithIndexWithTarget()
    {
        Verify.assertEmpty(this.classUnderTest().collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty()));
    }

    @Override
    @Test
    public void countByEach()
    {
        assertEquals(Bags.immutable.empty(), this.classUnderTest().countByEach(each -> IntInterval.oneTo(5).collect(i -> each + i)));
    }

    @Test
    public void countByEach_target()
    {
        MutableBag<Integer> target = Bags.mutable.empty();
        assertEquals(target, this.classUnderTest().countByEach(each -> IntInterval.oneTo(5).collect(i -> each + i), target));
    }
}
