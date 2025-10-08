/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.immutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.partition.set.sorted.PartitionImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.factory.Stacks;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.AddToList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImmutableEmptySortedSetTest extends AbstractImmutableSortedSetTestCase
{
    @Override
    protected ImmutableSortedSet<Integer> classUnderTest()
    {
        return SortedSets.immutable.of();
    }

    @Override
    protected ImmutableSortedSet<Integer> classUnderTest(Comparator<? super Integer> comparator)
    {
        return SortedSets.immutable.of(comparator);
    }

    @Test
    public void testContainsAll()
    {
        assertTrue(this.classUnderTest().containsAllIterable(UnifiedSet.<Integer>newSet()));
        assertFalse(this.classUnderTest().containsAllIterable(UnifiedSet.newSetWith(1)));
    }

    @Test
    public void testNewSortedSet()
    {
        assertSame(SortedSets.immutable.of(), SortedSets.immutable.ofAll(FastList.newList()));
        assertSame(SortedSets.immutable.of(), SortedSets.immutable.ofSortedSet(TreeSortedSet.newSet()));
        assertNotSame(
                SortedSets.immutable.of(),
                SortedSets.immutable.ofSortedSet(TreeSortedSet.newSet(Comparators.reverseNaturalOrder())));
    }

    @Override
    @Test
    public void newWith()
    {
        assertEquals(UnifiedSet.newSetWith(1), this.classUnderTest().newWith(1));
        assertSame(SortedSets.immutable.empty(), SortedSets.immutable.of(FastList.newList().toArray()));
        assertEquals(
                SortedSets.immutable.empty(),
                SortedSets.immutable.of(Comparators.naturalOrder(), FastList.newList().toArray()));

        assertEquals(
                Comparators.<Integer>reverseNaturalOrder(),
                this.classUnderTest(Comparators.reverseNaturalOrder()).newWith(1).comparator());
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 2),
                this.classUnderTest(Comparators.reverseNaturalOrder()).newWith(1).newWith(2).castToSortedSet());
    }

    @Override
    @Test
    public void newWithout()
    {
        assertEquals(SortedSets.immutable.empty(), SortedSets.immutable.empty().newWithout(1));
        assertSame(SortedSets.immutable.empty(), SortedSets.immutable.empty().newWithout(1));
        assertEquals(SortedSets.immutable.empty(), SortedSets.immutable.empty().newWithoutAll(Interval.oneTo(3)));
        assertSame(SortedSets.immutable.empty(), SortedSets.immutable.empty().newWithoutAll(Interval.oneTo(3)));

        assertEquals(
                Comparators.<Integer>reverseNaturalOrder(),
                this.classUnderTest(Comparators.reverseNaturalOrder()).newWithout(1).comparator());
    }

    @Override
    @Test
    public void detect()
    {
        assertNull(this.classUnderTest().detect(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        assertNull(this.classUnderTest().detectWith(Object::equals, Integer.valueOf(1)));
    }

    @Override
    @Test
    public void detectIndex()
    {
        //Any predicate will result in -1
        assertEquals(Integer.valueOf(-1), Integer.valueOf(this.classUnderTest().detectIndex(Predicates.alwaysTrue())));
    }

    @Override
    @Test
    public void corresponds()
    {
        //Evaluates true for all empty sets and false for all non-empty sets

        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        assertTrue(integers1.corresponds(Lists.mutable.of(), Predicates2.alwaysFalse()));

        ImmutableSortedSet<Integer> integers2 = integers1.newWith(Integer.valueOf(1));
        assertFalse(integers2.corresponds(integers1, Predicates2.alwaysTrue()));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        assertTrue(this.classUnderTest().allSatisfy(Integer.class::isInstance));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        assertFalse(this.classUnderTest().anySatisfy(Integer.class::isInstance));
    }

    @Override
    @Test
    public void getFirst()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().getLast());
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
        Verify.assertIterableEmpty(this.classUnderTest());
        assertFalse(this.classUnderTest().notEmpty());
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
        ImmutableSortedSet<Integer> immutableSet = this.classUnderTest(Comparators.reverseNaturalOrder());
        ImmutableList<Pair<Integer, Integer>> pairs = immutableSet.zip(Interval.oneTo(10));
        assertEquals(FastList.<Pair<Integer, Integer>>newList(), pairs);

        assertEquals(
                UnifiedSet.<Pair<Integer, Integer>>newSet(),
                immutableSet.zip(Interval.oneTo(10), UnifiedSet.newSet()));

        ImmutableList<Pair<Integer, Integer>> pairsWithExtras = pairs.newWith(Tuples.pair(1, 5)).newWith(Tuples.pair(5, 1));
        assertEquals(FastList.newListWith(Tuples.pair(1, 5), Tuples.pair(5, 1)), pairsWithExtras.toList());
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Pair<Integer, Integer>> pairs = set.zipWithIndex();

        assertEquals(UnifiedSet.<Pair<Integer, Integer>>newSet(), pairs);

        assertEquals(
                UnifiedSet.<Pair<Integer, Integer>>newSet(),
                set.zipWithIndex(UnifiedSet.newSet()));

        assertNotNull(pairs.comparator());
        ImmutableSortedSet<Pair<Integer, Integer>> pairsWithExtras = pairs.newWith(Tuples.pair(1, 5)).newWith(Tuples.pair(5, 1));
        assertEquals(FastList.newListWith(Tuples.pair(1, 5), Tuples.pair(5, 1)), pairsWithExtras.toList());
    }

    @Test
    public void chunk()
    {
        Verify.assertIterableEmpty(this.classUnderTest().chunk(2));
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
        Verify.assertIterableEmpty(this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void union()
    {
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith("a", "b", "c"),
                SortedSets.immutable.<String>empty().union(UnifiedSet.newSetWith("a", "b", "c")).castToSortedSet());

        Verify.assertListsEqual(
                FastList.newListWith(3, 2, 1),
                this.classUnderTest(Comparators.reverseNaturalOrder()).union(UnifiedSet.newSetWith(1, 2, 3)).toList());
    }

    @Override
    @Test
    public void unionInto()
    {
        assertEquals(
                UnifiedSet.newSetWith("a", "b", "c"),
                SortedSets.immutable.<String>empty().unionInto(UnifiedSet.newSetWith("a", "b", "c"), UnifiedSet.newSet()));
    }

    @Override
    @Test
    public void intersect()
    {
        assertEquals(
                UnifiedSet.<String>newSet(),
                SortedSets.immutable.<String>empty().intersect(UnifiedSet.newSetWith("1", "2", "3")));

        assertEquals(
                Comparators.<Integer>reverseNaturalOrder(),
                this.classUnderTest(Comparators.reverseNaturalOrder()).intersect(UnifiedSet.newSetWith(1, 2, 3)).comparator());
    }

    @Override
    @Test
    public void intersectInto()
    {
        assertEquals(
                UnifiedSet.<String>newSet(),
                SortedSets.immutable.<String>empty().intersectInto(UnifiedSet.newSetWith("1", "2", "3"), UnifiedSet.newSet()));
    }

    @Override
    @Test
    public void difference()
    {
        assertEquals(
                UnifiedSet.<String>newSet(),
                SortedSets.immutable.<String>empty().difference(UnifiedSet.newSetWith("not present")));

        assertEquals(
                Comparators.<Integer>reverseNaturalOrder(),
                this.classUnderTest(Comparators.reverseNaturalOrder()).difference(UnifiedSet.newSetWith(1, 2, 3)).comparator());
    }

    @Override
    @Test
    public void differenceInto()
    {
        ImmutableSortedSet<String> set = SortedSets.immutable.empty();
        assertEquals(
                UnifiedSet.<String>newSet(),
                set.differenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.newSet()));

        Verify.assertListsEqual(
                FastList.newListWith(3, 2, 1),
                this.classUnderTest(Comparators.reverseNaturalOrder()).union(UnifiedSet.newSetWith(1, 2, 3)).toList());
    }

    @Override
    @Test
    public void symmetricDifference()
    {
        assertEquals(
                UnifiedSet.newSetWith("not present"),
                SortedSets.immutable.<String>empty().symmetricDifference(UnifiedSet.newSetWith("not present")));

        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4),
                SortedSets.immutable.of(Comparators.<Integer>reverseNaturalOrder()).symmetricDifference(UnifiedSet.newSetWith(1, 3, 2, 4)).castToSortedSet());
    }

    @Override
    @Test
    public void symmetricDifferenceInto()
    {
        assertEquals(
                UnifiedSet.newSetWith("not present"),
                SortedSets.immutable.<String>empty().symmetricDifferenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.newSet()));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        ImmutableSortedSet<Integer> immutable = this.classUnderTest();
        Verify.assertEqualsAndHashCode(UnifiedSet.newSet(), immutable);
        Verify.assertPostSerializedIdentity(immutable);
        assertNotEquals(Lists.mutable.empty(), immutable);

        ImmutableSortedSet<Integer> setWithComparator = this.classUnderTest(Comparators.reverseNaturalOrder());
        Verify.assertEqualsAndHashCode(UnifiedSet.newSet(), setWithComparator);
        Verify.assertPostSerializedEqualsAndHashCode(setWithComparator);
    }

    @Override
    @Test
    public void contains()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Verify.assertNotContains(Integer.valueOf(1), set.castToSortedSet());
        Verify.assertEmpty(set.castToSortedSet());
        assertThrows(NullPointerException.class, () -> set.contains(null));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertFalse(set.containsAllIterable(FastList.newListWith(1, 2, 3)));
        assertTrue(set.containsAllIterable(set));
    }

    @Override
    @Test
    public void iterator()
    {
        Iterator<Integer> iterator = this.classUnderTest().iterator();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Override
    @Test
    public void select()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertEmpty(set.select(Predicates.lessThan(4)).castToSortedSet());
        assertEquals(Collections.<Integer>reverseOrder(), set.select(Predicates.lessThan(3)).comparator());
    }

    @Override
    @Test
    public void selectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(integers.selectWith(Predicates2.lessThan(), 4).castToSortedSet());
        assertEquals(Collections.<Integer>reverseOrder(), integers.selectWith(Predicates2.lessThan(), 3).comparator());
    }

    @Override
    @Test
    public void reject()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertEmpty(set.reject(Predicates.lessThan(3)).castToSortedSet());
        assertEquals(Collections.<Integer>reverseOrder(), set.reject(Predicates.lessThan(3)).comparator());
    }

    @Override
    @Test
    public void rejectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(integers.rejectWith(Predicates2.greaterThanOrEqualTo(), 4).castToSortedSet());
        assertEquals(Collections.<Integer>reverseOrder(), integers.rejectWith(Predicates2.greaterThanOrEqualTo(), 4).comparator());
    }

    @Override
    @Test
    public void partition()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        PartitionImmutableSortedSet<Integer> partition = set.partition(Predicates.lessThan(4));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void partitionWith()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        PartitionImmutableSortedSet<Integer> partition = set.partitionWith(Predicates2.lessThan(), 4);
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void collect()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertIterableEmpty(integers.collect(Functions.getIntegerPassThru()));
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    public void collectWithIndex()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(
                Lists.mutable.empty(),
                integers.collectWithIndex(PrimitiveTuples::pair));
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    public void collectWithIndexWithTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(
                Lists.mutable.empty(),
                integers.collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty()));
    }

    /**
     * @since 11.0.
     */
    @Override
    @Test
    public void selectWithIndexWithTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(
                Lists.mutable.empty(),
                integers.selectWithIndex((each, index) -> index % 2 == 0, Lists.mutable.empty()));
    }

    /**
     * @since 11.0.
     */
    @Override
    @Test
    public void rejectWithIndexWithTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(
                Lists.mutable.empty(),
                integers.selectWithIndex((each, index) -> index % 2 == 0, Lists.mutable.empty()));
    }

    @Override
    @Test
    public void collectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertIterableEmpty(integers.collectWith((value, parameter) -> value / parameter, 1));
    }

    @Override
    @Test
    public void collectToTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        ImmutableSortedSet<Integer> collect = integers.collect(Functions.getIntegerPassThru(), TreeSortedSet.newSet(Collections.reverseOrder())).toImmutable();
        Verify.assertIterableEmpty(collect);
        assertEquals(Collections.<Integer>reverseOrder(), collect.comparator());
    }

    @Override
    @Test
    public void partitionWhile()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        PartitionImmutableSortedSet<Integer> partition = set.partitionWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void takeWhile()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        ImmutableSortedSet<Integer> take = set.takeWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(take);
        assertEquals(Collections.<Integer>reverseOrder(), take.comparator());
    }

    @Override
    @Test
    public void dropWhile()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        ImmutableSortedSet<Integer> drop = set.dropWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(drop);
        assertEquals(Collections.<Integer>reverseOrder(), drop.comparator());
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertEmpty(set.selectInstancesOf(Integer.class).castToSortedSet());
        assertEquals(Collections.<Double>reverseOrder(), set.selectInstancesOf(Double.class).comparator());
    }

    @Override
    @Test
    public void toSortedSet()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        assertNull(set.toSortedSet().comparator());
        Verify.assertEmpty(set.toSortedSet());
    }

    @Override
    @Test
    public void toSortedSetWithComparator()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertEquals(Collections.<Integer>reverseOrder(), set.toSortedSet(Collections.reverseOrder()).comparator());
        Verify.assertEmpty(set.toSortedSet());
    }

    @Test
    public void first()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().castToSortedSet().first());
    }

    @Test
    public void last()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().castToSortedSet().last());
    }

    @Test
    public void compareTo()
    {
        assertEquals(0, (long) this.classUnderTest().compareTo(this.classUnderTest()));
        assertEquals(-1, this.classUnderTest().compareTo(TreeSortedSet.newSetWith(1)));
        assertEquals(-4, this.classUnderTest().compareTo(TreeSortedSet.newSetWith(1, 2, 3, 4)));
    }

    @Override
    @Test
    public void cartesianProduct()
    {
        LazyIterable<Pair<Integer, Integer>> emptyProduct = this.classUnderTest().cartesianProduct(SortedSets.immutable.of(1, 2, 3));
        Verify.assertEmpty(emptyProduct.toList());

        LazyIterable<Pair<Integer, Integer>> empty2 = this.classUnderTest().cartesianProduct(TreeSortedSet.newSet());
        Verify.assertEmpty(empty2.toList());
    }

    @Override
    @Test
    public void indexOf()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertEquals(-1, integers.indexOf(4));
        assertEquals(-1, integers.indexOf(3));
        assertEquals(-1, integers.indexOf(2));
        assertEquals(-1, integers.indexOf(1));
        assertEquals(-1, integers.indexOf(0));
        assertEquals(-1, integers.indexOf(5));
    }

    @Override
    @Test
    public void forEachFromTo()
    {
        MutableSortedSet<Integer> result = TreeSortedSet.newSet(Comparators.reverseNaturalOrder());
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(-1, 0, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, -1, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, 0, result::add));
    }

    @Override
    @Test
    public void forEachWithIndexWithFromTo()
    {
        MutableList<Integer> result = Lists.mutable.empty();
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.reverseNaturalOrder());

        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(-1, 0, new AddToList(result)));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, -1, new AddToList(result)));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, 0, new AddToList(result)));
    }

    @Override
    public void toStack()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertEquals(Stacks.mutable.with(), set.toStack());
    }

    @Override
    @Test
    public void powerSet()
    {
        Verify.assertSize(1, this.classUnderTest().powerSet().castToSortedSet());
        assertEquals(SortedSets.immutable.of(SortedSets.immutable.<Integer>empty()), this.classUnderTest().powerSet());
    }

    @Override
    @Test
    public void toSortedMap()
    {
        MutableSortedMap<Integer, Integer> map = this.classUnderTest().toSortedMap(Functions.getIntegerPassThru(), Functions.getIntegerPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
    }

    @Override
    @Test
    public void toSortedMap_with_comparator()
    {
        MutableSortedMap<Integer, Integer> map = this.classUnderTest().toSortedMap(Comparators.reverseNaturalOrder(),
                Functions.getIntegerPassThru(), Functions.getIntegerPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), map.comparator());
    }

    @Override
    @Test
    public void toSortedMapBy()
    {
        MutableSortedMap<Integer, Integer> map = this.classUnderTest().toSortedMapBy(key -> -key,
                Functions.getIntegerPassThru(), Functions.getIntegerPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
    }

    @Override
    @Test
    public void subSet()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertSame(set, set.subSet(1, 4));
        assertSame(set, set.subSet(1, true, 4, false));
    }

    @Override
    @Test
    public void headSet()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertSame(set, set.headSet(4));
        assertSame(set, set.headSet(4, false));
    }

    @Override
    @Test
    public void tailSet()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertSame(set, set.tailSet(1));
        assertSame(set, set.tailSet(1, true));
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(BooleanArrayList.newListWith(), integers.collectBoolean(PrimitiveFunctions.integerIsPositive()));
    }

    @Override
    @Test
    public void collectByte()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(ByteArrayList.newListWith(), integers.collectByte(PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Override
    @Test
    public void collectChar()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(CharArrayList.newListWith(), integers.collectChar(integer -> (char) (integer.intValue() + 64)));
    }

    @Override
    @Test
    public void collectDouble()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(DoubleArrayList.newListWith(), integers.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Override
    @Test
    public void collectFloat()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(FloatArrayList.newListWith(), integers.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Override
    @Test
    public void collectInt()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(IntArrayList.newListWith(), integers.collectInt(PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Override
    @Test
    public void collectLong()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(LongArrayList.newListWith(), integers.collectLong(PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Override
    @Test
    public void collectShort()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(ShortArrayList.newListWith(), integers.collectShort(PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Override
    @Test
    public void groupByUniqueKey_throws()
    {
        // Not applicable for empty*
    }

    @Override
    @Test
    public void groupByUniqueKey_target_throws()
    {
        // Not applicable for empty*
    }

    @Override
    @Test
    public void take()
    {
        assertEquals(this.classUnderTest(), this.classUnderTest().take(2));
    }

    @Override
    @Test
    public void drop()
    {
        assertEquals(this.classUnderTest(), this.classUnderTest().drop(2));
    }
}
