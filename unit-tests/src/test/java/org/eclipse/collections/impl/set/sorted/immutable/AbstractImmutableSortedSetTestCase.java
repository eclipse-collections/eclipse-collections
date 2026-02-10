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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.partition.set.sorted.PartitionImmutableSortedSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.NegativeIntervalFunction;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.AddToList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.set.sorted.TreeSortedSetMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractImmutableSortedSetTestCase
{
    protected abstract ImmutableSortedSet<Integer> classUnderTest();

    protected abstract ImmutableSortedSet<Integer> classUnderTest(Comparator<? super Integer> comparator);

    @Test
    public void noSupportForNull()
    {
        assertThrows(NullPointerException.class, () -> this.classUnderTest().newWith(null));
    }

    @Test
    public void equalsAndHashCode()
    {
        ImmutableSortedSet<Integer> immutable = this.classUnderTest();
        MutableSortedSet<Integer> mutable = TreeSortedSet.newSet(immutable);
        Verify.assertEqualsAndHashCode(mutable, immutable);
        Verify.assertPostSerializedEqualsAndHashCode(immutable);
        assertNotEquals(FastList.newList(mutable), immutable);
    }

    @Test
    public void newWith()
    {
        ImmutableSortedSet<Integer> immutable = this.classUnderTest();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Interval.fromTo(0, immutable.size())), immutable.newWith(0).castToSortedSet());
        assertSame(immutable, immutable.newWith(immutable.size()));

        ImmutableSortedSet<Integer> set = this.classUnderTest(Comparators.reverseNaturalOrder());
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSet(Comparators.reverseNaturalOrder(), Interval.oneTo(set.size() + 1)),
                set.newWith(set.size() + 1).castToSortedSet());
    }

    @Test
    public void newWithout()
    {
        ImmutableSortedSet<Integer> immutable = this.classUnderTest();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Interval.oneTo(immutable.size() - 1)), immutable.newWithout(immutable.size()).castToSortedSet());
        assertSame(immutable, immutable.newWithout(immutable.size() + 1));

        ImmutableSortedSet<Integer> set = this.classUnderTest(Comparators.reverseNaturalOrder());
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSet(Comparators.reverseNaturalOrder(), Interval.oneTo(set.size() - 1)),
                set.newWithout(set.size()).castToSortedSet());
    }

    @Test
    public void newWithAll()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        ImmutableSortedSet<Integer> withAll = set.newWithAll(UnifiedSet.newSet(Interval.fromTo(1, set.size() + 1)));
        assertNotEquals(set, withAll);
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSet(Comparators.reverseNaturalOrder(), Interval.fromTo(1, set.size() + 1)),
                withAll.castToSortedSet());
    }

    @Test
    public void newWithoutAll()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Integer> withoutAll = set.newWithoutAll(set);

        assertEquals(SortedSets.immutable.<Integer>of(), withoutAll);
        assertEquals(Sets.immutable.<Integer>of(), withoutAll);

        ImmutableSortedSet<Integer> largeWithoutAll = set.newWithoutAll(Interval.fromTo(101, 150));
        assertEquals(set, largeWithoutAll);

        ImmutableSortedSet<Integer> largeWithoutAll2 = set.newWithoutAll(UnifiedSet.newSet(Interval.fromTo(151, 199)));
        assertEquals(set, largeWithoutAll2);
    }

    @Test
    public void contains()
    {
        ImmutableSortedSet<Integer> set1 = this.classUnderTest();
        for (int i = 1; i <= set1.size(); i++)
        {
            Verify.assertContains(i, set1.castToSortedSet());
        }
        Verify.assertNotContains(Integer.valueOf(set1.size() + 1), set1.castToSortedSet());

        SortedSet<Integer> set2 = new TreeSet<>(set1.castToSortedSet());

        assertThrows(ClassCastException.class, () -> set1.contains(new Object()));
        assertThrows(ClassCastException.class, () -> set2.contains(new Object()));

        assertThrows(ClassCastException.class, () -> set1.contains("1"));
        assertThrows(ClassCastException.class, () -> set2.contains("1"));

        assertThrows(NullPointerException.class, () -> set1.contains(null));
        assertThrows(NullPointerException.class, () -> set2.contains(null));
    }

    @Test
    public void containsAllArray()
    {
        ImmutableSortedSet<Integer> set1 = this.classUnderTest();
        SortedSet<Integer> set2 = new TreeSet<>(set1.castToSortedSet());

        assertTrue(set1.containsAllArguments(set1.toArray()));

        assertThrows(NullPointerException.class, () -> set1.containsAllArguments(null, null));
        assertThrows(NullPointerException.class, () -> set2.containsAll(FastList.newListWith(null, null)));
    }

    @Test
    public void containsAllIterable()
    {
        ImmutableSortedSet<Integer> set1 = this.classUnderTest();
        SortedSet<Integer> set2 = new TreeSet<>(set1.castToSortedSet());

        assertTrue(set1.containsAllIterable(Interval.oneTo(set1.size())));

        assertThrows(NullPointerException.class, () -> set1.containsAllIterable(FastList.newListWith(null, null)));
        assertThrows(NullPointerException.class, () -> set2.containsAll(FastList.newListWith(null, null)));
    }

    @Test
    public void tap()
    {
        MutableList<Integer> tapResult = Lists.mutable.of();
        ImmutableSortedSet<Integer> collection = this.classUnderTest();
        assertSame(collection, collection.tap(tapResult::add));
        assertEquals(collection.toList(), tapResult);
    }

    @Test
    public void forEach()
    {
        MutableSet<Integer> result = UnifiedSet.newSet();
        ImmutableSortedSet<Integer> collection = this.classUnderTest();
        collection.forEach(CollectionAddProcedure.on(result));
        assertEquals(collection, result);
    }

    @Test
    public void forEachWith()
    {
        MutableList<Integer> result = Lists.mutable.of();
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        set.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 0);
        Verify.assertListsEqual(result, set.toList());
    }

    @Test
    public void forEachWithIndex()
    {
        MutableList<Integer> result = Lists.mutable.of();
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        set.forEachWithIndex((object, index) -> result.add(object));
        Verify.assertListsEqual(result, set.toList());
    }

    @Test
    public void select()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertIterableEmpty(integers.select(Predicates.greaterThan(integers.size())));
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSet(Comparators.reverseNaturalOrder(), Interval.oneTo(integers.size() - 1)),
                integers.select(Predicates.lessThan(integers.size())).castToSortedSet());
    }

    @Test
    public void selectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertIterableEmpty(integers.selectWith(Predicates2.greaterThan(), integers.size()));
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSet(Comparators.reverseNaturalOrder(), Interval.oneTo(integers.size() - 1)),
                integers.selectWith(Predicates2.lessThan(), integers.size()).castToSortedSet());
    }

    @Test
    public void selectToTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Verify.assertListsEqual(
                integers.toList(),
                integers.select(Predicates.lessThan(integers.size() + 1), FastList.newList()));
        Verify.assertEmpty(
                integers.select(Predicates.greaterThan(integers.size()), FastList.newList()));
    }

    @Test
    public void reject()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertEmpty(
                FastList.newList(integers.reject(Predicates.lessThan(integers.size() + 1))));
        Verify.assertSortedSetsEqual(
                integers.castToSortedSet(),
                integers.reject(Predicates.greaterThan(integers.size())).castToSortedSet());
    }

    @Test
    public void rejectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertIterableEmpty(integers.rejectWith(Predicates2.lessThanOrEqualTo(), integers.size()));
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSet(Comparators.reverseNaturalOrder(), Interval.oneTo(integers.size() - 1)),
                integers.rejectWith(Predicates2.greaterThanOrEqualTo(), integers.size()).castToSortedSet());
    }

    @Test
    public void rejectToTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Verify.assertEmpty(
                integers.reject(Predicates.lessThan(integers.size() + 1), FastList.newList()));
        Verify.assertListsEqual(
                integers.toList(),
                integers.reject(Predicates.greaterThan(integers.size()), FastList.newList()));
    }

    @Test
    public void selectInstancesOf()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        assertEquals(set, set.selectInstancesOf(Integer.class));
        Verify.assertIterableEmpty(set.selectInstancesOf(Double.class));
        assertEquals(Collections.<Integer>reverseOrder(), set.selectInstancesOf(Integer.class).comparator());
        assertEquals(Collections.<Double>reverseOrder(), set.selectInstancesOf(Double.class).comparator());
    }

    @Test
    public void partition()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        PartitionImmutableSortedSet<Integer> partition = integers.partition(Predicates.greaterThan(integers.size()));
        Verify.assertIterableEmpty(partition.getSelected());
        assertEquals(integers, partition.getRejected());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Test
    public void partitionWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        PartitionImmutableSortedSet<Integer> partition = integers.partitionWith(Predicates2.greaterThan(), integers.size());
        Verify.assertIterableEmpty(partition.getSelected());
        assertEquals(integers, partition.getRejected());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Test
    public void partitionWhile()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        PartitionImmutableSortedSet<Integer> partition1 = integers.partitionWhile(Predicates.greaterThan(integers.size()));
        Verify.assertIterableEmpty(partition1.getSelected());
        assertEquals(integers, partition1.getRejected());
        assertEquals(Collections.<Integer>reverseOrder(), partition1.getSelected().comparator());
        assertEquals(Collections.<Integer>reverseOrder(), partition1.getRejected().comparator());

        PartitionImmutableSortedSet<Integer> partition2 = integers.partitionWhile(Predicates.lessThanOrEqualTo(integers.size()));
        assertEquals(integers, partition2.getSelected());
        Verify.assertIterableEmpty(partition2.getRejected());
    }

    @Test
    public void takeWhile()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        ImmutableSortedSet<Integer> take1 = integers.takeWhile(Predicates.greaterThan(integers.size()));
        Verify.assertIterableEmpty(take1);
        assertEquals(Collections.<Integer>reverseOrder(), take1.comparator());

        ImmutableSortedSet<Integer> take2 = integers.takeWhile(Predicates.lessThanOrEqualTo(integers.size()));
        assertEquals(integers, take2);
        assertEquals(Collections.<Integer>reverseOrder(), take2.comparator());
    }

    @Test
    public void dropWhile()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        ImmutableSortedSet<Integer> drop1 = integers.dropWhile(Predicates.greaterThan(integers.size()));
        assertEquals(integers, drop1);
        assertEquals(Collections.<Integer>reverseOrder(), drop1.comparator());

        ImmutableSortedSet<Integer> drop2 = integers.dropWhile(Predicates.lessThanOrEqualTo(integers.size()));
        Verify.assertIterableEmpty(drop2);
        assertEquals(Collections.<Integer>reverseOrder(), drop2.comparator());
    }

    @Test
    public void collect()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertListsEqual(integers.toList(), integers.collect(Functions.getIntegerPassThru()).castToList());
    }

    @Test
    public void collectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertListsEqual(integers.toList(), integers.collectWith((value, parameter) -> value / parameter, 1).castToList());
    }

    /**
     * @since 9.1.
     */
    @Test
    public void collectWithIndex()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(4), 0),
                        PrimitiveTuples.pair(Integer.valueOf(3), 1),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 3)),
                integers.collectWithIndex(PrimitiveTuples::pair));
    }

    /**
     * @since 9.1.
     */
    @Test
    public void collectWithIndexWithTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(4), 0),
                        PrimitiveTuples.pair(Integer.valueOf(3), 1),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 3)),
                integers.collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty()));
    }

    /**
     * @since 11.0.
     */
    @Test
    public void selectWithIndexWithTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(
                Lists.mutable.with(4, 2),
                integers.selectWithIndex((each, index) -> index % 2 == 0, Lists.mutable.empty()));
    }

    /**
     * @since 11.0.
     */
    @Test
    public void rejectWithIndexWithTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(
                Lists.mutable.with(3, 1),
                integers.rejectWithIndex((each, index) -> index % 2 == 0, Lists.mutable.empty()));
    }

    @Test
    public void collectToTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        assertEquals(integers, integers.collect(Functions.getIntegerPassThru(), UnifiedSet.newSet()));
        Verify.assertListsEqual(
                integers.toList(),
                integers.collect(Functions.getIntegerPassThru(), FastList.newList()));
    }

    @Test
    public void flatCollect()
    {
        ImmutableList<String> actual = this.classUnderTest(Collections.reverseOrder()).flatCollect(integer -> Lists.fixedSize.of(String.valueOf(integer)));
        ImmutableList<String> expected = this.classUnderTest(Collections.reverseOrder()).collect(String::valueOf);
        assertEquals(expected, actual);
        Verify.assertListsEqual(expected.toList(), actual.toList());
    }

    @Test
    public void flatCollectWithTarget()
    {
        MutableSet<String> actual = this.classUnderTest().flatCollect(integer -> Lists.fixedSize.of(String.valueOf(integer)), UnifiedSet.newSet());

        ImmutableList<String> expected = this.classUnderTest().collect(String::valueOf);
        Verify.assertSetsEqual(expected.toSet(), actual);
    }

    @Test
    public void zip()
    {
        ImmutableSortedSet<Integer> immutableSet = this.classUnderTest(Collections.reverseOrder());
        List<Object> nulls = Collections.nCopies(immutableSet.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(immutableSet.size() + 1, null);
        List<Object> nullsMinusOne = Collections.nCopies(immutableSet.size() - 1, null);

        ImmutableList<Pair<Integer, Object>> pairs = immutableSet.zip(nulls);
        assertEquals(immutableSet.toList(), pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        Verify.assertListsEqual(FastList.newList(Interval.fromTo(immutableSet.size(), 1)), pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne).toList());
        assertEquals(FastList.newList(nulls), pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        ImmutableList<Pair<Integer, Object>> pairsPlusOne = immutableSet.zip(nullsPlusOne);
        assertEquals(immutableSet.toList(), pairsPlusOne.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        Verify.assertListsEqual(
                FastList.newList(Interval.fromTo(immutableSet.size(), 1)),
                pairsPlusOne.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne).castToList());
        assertEquals(FastList.newList(nulls), pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        ImmutableList<Pair<Integer, Object>> pairsMinusOne = immutableSet.zip(nullsMinusOne);
        Verify.assertListsEqual(
                FastList.newList(Interval.fromTo(immutableSet.size(), 2)),
                pairsMinusOne.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne).castToList());
        assertEquals(immutableSet.zip(nulls), immutableSet.zip(nulls, FastList.newList()));

        MutableList<Holder<Integer>> holders = FastList.newListWith(new Holder<>(1), new Holder<>(2), new Holder<>(3));
        ImmutableList<Pair<Integer, Holder<Integer>>> zipped = immutableSet.zip(holders);
        Verify.assertSize(3, zipped.castToList());
        Holder<Integer> two = new Holder<>(-1);
        Holder<Integer> two1 = new Holder<>(-1);
        assertEquals(Tuples.pair(10, two1), zipped.newWith(Tuples.pair(10, two)).getLast());
        assertEquals(Tuples.pair(1, new Holder<>(3)), this.classUnderTest().zip(holders.reverseThis()).getFirst());
    }

    @Test
    public void zipWithIndex()
    {
        ImmutableSortedSet<Integer> immutableSet = this.classUnderTest(Collections.reverseOrder());
        ImmutableSortedSet<Pair<Integer, Integer>> pairs = immutableSet.zipWithIndex();

        assertEquals(immutableSet.toList(), pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        assertEquals(
                Interval.zeroTo(immutableSet.size() - 1).toList(),
                pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo));
        assertEquals(
                immutableSet.zipWithIndex(),
                immutableSet.zipWithIndex(UnifiedSet.newSet()));
        Verify.assertListsEqual(
                TreeSortedSet.newSet(Collections.reverseOrder(), Interval.oneTo(immutableSet.size())).toList(),
                pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne).toList());
    }

    @Test
    public void chunk_zero_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.classUnderTest().chunk(0));
    }

    @Test
    public void chunk_large_size()
    {
        assertEquals(this.classUnderTest(), this.classUnderTest().chunk(10).getFirst());
        Verify.assertInstanceOf(ImmutableSortedSet.class, this.classUnderTest().chunk(10).getFirst());
    }

    @Test
    public void detect()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        assertEquals(Integer.valueOf(1), integers.detect(Predicates.equal(1)));
        assertNull(integers.detect(Predicates.equal(integers.size() + 1)));
    }

    @Test
    public void detectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        assertEquals(Integer.valueOf(1), integers.detectWith(Object::equals, Integer.valueOf(1)));
        assertNull(integers.detectWith(Object::equals, Integer.valueOf(integers.size() + 1)));
    }

    @Test
    public void detectWithIfNone()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Function0<Integer> function = new PassThruFunction0<>(integers.size() + 1);
        Integer sum = Integer.valueOf(integers.size() + 1);
        assertEquals(Integer.valueOf(1), integers.detectWithIfNone(Object::equals, Integer.valueOf(1), function));
        assertEquals(Integer.valueOf(integers.size() + 1), integers.detectWithIfNone(Object::equals, sum, function));
    }

    @Test
    public void detectIfNone()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Function0<Integer> function = new PassThruFunction0<>(integers.size() + 1);
        assertEquals(Integer.valueOf(1), integers.detectIfNone(Predicates.equal(1), function));
        assertEquals(Integer.valueOf(integers.size() + 1), integers.detectIfNone(Predicates.equal(integers.size() + 1), function));
    }

    @Test
    public void detectIndex()
    {
        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        assertEquals(1, integers1.detectIndex(integer -> integer % 2 == 0));
        assertEquals(0, integers1.detectIndex(integer -> integer % 2 != 0));
        assertEquals(-1, integers1.detectIndex(integer -> integer % 5 == 0));

        ImmutableSortedSet<Integer> integers2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertEquals(0, integers2.detectIndex(integer -> integer % 2 == 0));
        assertEquals(1, integers2.detectIndex(integer -> integer % 2 != 0));
        assertEquals(-1, integers2.detectIndex(integer -> integer % 5 == 0));
    }

    @Test
    public void corresponds()
    {
        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        assertFalse(integers1.corresponds(this.classUnderTest().newWith(100), Predicates2.alwaysTrue()));

        ImmutableList<Integer> integers2 = integers1.collect(integers -> integers + 1);
        assertTrue(integers1.corresponds(integers2, Predicates2.lessThan()));
        assertFalse(integers1.corresponds(integers2, Predicates2.greaterThan()));

        ImmutableSortedSet<Integer> integers3 = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertFalse(integers3.corresponds(integers1, Predicates2.equal()));
    }

    @Test
    public void allSatisfy()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        assertTrue(integers.allSatisfy(Integer.class::isInstance));
        assertFalse(integers.allSatisfy(Integer.valueOf(0)::equals));
    }

    @Test
    public void anySatisfy()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        assertFalse(integers.anySatisfy(String.class::isInstance));
        assertTrue(integers.anySatisfy(Integer.class::isInstance));
    }

    @Test
    public void count()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        assertEquals(integers.size(), integers.count(Integer.class::isInstance));
        assertEquals(0, integers.count(String.class::isInstance));
    }

    @Test
    public void collectIf()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        Verify.assertListsEqual(integers.toList(), integers.collectIf(Integer.class::isInstance, Functions.getIntegerPassThru()).toList());
    }

    @Test
    public void collectIfToTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Verify.assertSetsEqual(integers.toSet(), integers.collectIf(Integer.class::isInstance, Functions.getIntegerPassThru(), UnifiedSet.newSet()));
    }

    @Test
    public void getFirst()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        assertEquals(Integer.valueOf(1), integers.getFirst());
        ImmutableSortedSet<Integer> revInt = this.classUnderTest(Collections.reverseOrder());
        assertEquals(Integer.valueOf(revInt.size()), revInt.getFirst());
    }

    @Test
    public void getLast()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        assertEquals(Integer.valueOf(integers.size()), integers.getLast());
        ImmutableSortedSet<Integer> revInt = this.classUnderTest(Collections.reverseOrder());
        assertEquals(Integer.valueOf(1), revInt.getLast());
    }

    @Test
    public void isEmpty()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertFalse(set.isEmpty());
        assertTrue(set.notEmpty());
    }

    @Test
    public void iterator()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; iterator.hasNext(); i++)
        {
            Integer integer = iterator.next();
            assertEquals(i + 1, integer.intValue());
        }
        assertThrows(NoSuchElementException.class, iterator::next);
        Iterator<Integer> intItr = integers.iterator();
        intItr.next();
        assertThrows(UnsupportedOperationException.class, intItr::remove);
    }

    @Test
    public void injectInto()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Integer result = integers.injectInto(0, AddFunction.INTEGER);
        assertEquals(FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_INT), result.intValue());
    }

    @Test
    public void toArray()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        MutableList<Integer> copy = FastList.newList(integers);
        assertArrayEquals(integers.toArray(), copy.toArray());
        assertArrayEquals(integers.toArray(new Integer[integers.size()]), copy.toArray(new Integer[integers.size()]));
    }

    @Test
    public void testToString()
    {
        assertEquals(FastList.newList(this.classUnderTest()).toString(), this.classUnderTest().toString());
    }

    @Test
    public void makeString()
    {
        assertEquals(FastList.newList(this.classUnderTest()).makeString(), this.classUnderTest().makeString());
    }

    @Test
    public void appendString()
    {
        Appendable builder = new StringBuilder();
        this.classUnderTest().appendString(builder);
        assertEquals(FastList.newList(this.classUnderTest()).makeString(), builder.toString());
    }

    @Test
    public void toList()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        MutableList<Integer> list = integers.toList();
        Verify.assertEqualsAndHashCode(FastList.newList(integers), list);
    }

    @Test
    public void toSortedList()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        MutableList<Integer> list = integers.toSortedList(Collections.reverseOrder());
        assertEquals(copy.sortThis(Collections.reverseOrder()), list);
        MutableList<Integer> list2 = integers.toSortedList();
        Verify.assertListsEqual(copy.sortThis(), list2);
    }

    @Test
    public void toSortedListBy()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableList<Integer> list = integers.toSortedListBy(String::valueOf);
        assertEquals(integers.toList(), list);
    }

    @Test
    public void toSortedSet()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), set);
    }

    @Test
    public void toSortedSetWithComparator()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet(Collections.reverseOrder());
        assertEquals(integers.toSet(), set);
        assertEquals(integers.toSortedList(Comparators.reverseNaturalOrder()), set.toList());
    }

    @Test
    public void toSortedSetBy()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers), set);
    }

    @Test
    public void toSortedMap()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Functions.getIntegerPassThru(), String::valueOf);
        Verify.assertMapsEqual(integers.toMap(Functions.getIntegerPassThru(), String::valueOf), map);
        Verify.assertListsEqual(Interval.oneTo(integers.size()), map.keySet().toList());
    }

    @Test
    public void toSortedMap_with_comparator()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Comparators.reverseNaturalOrder(),
                Functions.getIntegerPassThru(), String::valueOf);
        Verify.assertMapsEqual(integers.toMap(Functions.getIntegerPassThru(), String::valueOf), map);
        Verify.assertListsEqual(Interval.fromTo(integers.size(), 1), map.keySet().toList());
    }

    @Test
    public void toSortedMapBy()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableSortedMap<Integer, String> map = integers.toSortedMapBy(key -> -key,
                Functions.getIntegerPassThru(), String::valueOf);
        Verify.assertMapsEqual(integers.toMap(Functions.getIntegerPassThru(), String::valueOf), map);
        Verify.assertListsEqual(Interval.fromTo(integers.size(), 1), map.keySet().toList());
    }

    @Test
    public void forLoop()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        for (Integer each : set)
        {
            assertNotNull(each);
        }
    }

    @Test
    public void iteratorRemove()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().iterator().remove());
    }

    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().castToSortedSet().add(1));
    }

    @Test
    public void remove()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().castToSortedSet().remove(Integer.valueOf(1)));
    }

    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().castToSortedSet().clear());
    }

    @Test
    public void removeAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().castToSortedSet().removeAll(Lists.fixedSize.of()));
    }

    @Test
    public void retainAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().castToSortedSet().retainAll(Lists.fixedSize.of()));
    }

    @Test
    public void addAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().castToSortedSet().addAll(Lists.fixedSize.of()));
    }

    @Test
    public void min()
    {
        assertEquals(Integer.valueOf(1), this.classUnderTest().min(Integer::compareTo));
    }

    @Test
    public void max()
    {
        assertEquals(Integer.valueOf(1), this.classUnderTest().max(Comparators.reverse(Integer::compareTo)));
    }

    @Test
    public void min_without_comparator()
    {
        assertEquals(Integer.valueOf(1), this.classUnderTest().min());
    }

    @Test
    public void max_without_comparator()
    {
        assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().max());
    }

    @Test
    public void minBy()
    {
        assertEquals(Integer.valueOf(1), this.classUnderTest().minBy(String::valueOf));
    }

    @Test
    public void maxBy()
    {
        assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().maxBy(String::valueOf));
    }

    @Test
    public void groupBy()
    {
        ImmutableSortedSet<Integer> undertest = this.classUnderTest();
        ImmutableSortedSetMultimap<Integer, Integer> actual = undertest.groupBy(Functions.getPassThru());
        ImmutableSortedSetMultimap<Integer, Integer> expected = TreeSortedSet.newSet(undertest).groupBy(Functions.getPassThru()).toImmutable();
        assertEquals(expected, actual);
    }

    @Test
    public void groupByEach()
    {
        ImmutableSortedSet<Integer> undertest = this.classUnderTest(Collections.reverseOrder());
        NegativeIntervalFunction function = new NegativeIntervalFunction();
        ImmutableSortedSetMultimap<Integer, Integer> actual = undertest.groupByEach(function);
        ImmutableSortedSetMultimap<Integer, Integer> expected = TreeSortedSet.newSet(undertest).groupByEach(function).toImmutable();
        assertEquals(expected, actual);
    }

    @Test
    public void groupByWithTarget()
    {
        ImmutableSortedSet<Integer> undertest = this.classUnderTest();
        TreeSortedSetMultimap<Integer, Integer> actual = undertest.groupBy(Functions.getPassThru(), TreeSortedSetMultimap.newMultimap());
        MutableSortedSetMultimap<Integer, Integer> expected = TreeSortedSet.newSet(undertest).groupBy(Functions.getPassThru());
        assertEquals(expected, actual);
    }

    @Test
    public void groupByEachWithTarget()
    {
        ImmutableSortedSet<Integer> undertest = this.classUnderTest();
        NegativeIntervalFunction function = new NegativeIntervalFunction();
        TreeSortedSetMultimap<Integer, Integer> actual = undertest.groupByEach(function, TreeSortedSetMultimap.newMultimap());
        MutableSortedSetMultimap<Integer, Integer> expected = TreeSortedSet.newSet(undertest).groupByEach(function);
        assertEquals(expected, actual);
    }

    @Test
    public void groupByUniqueKey()
    {
        assertEquals(
                this.classUnderTest().toBag().groupByUniqueKey(id -> id),
                this.classUnderTest().groupByUniqueKey(id -> id));
    }

    @Test
    public void groupByUniqueKey_throws()
    {
        assertThrows(IllegalStateException.class, () -> this.classUnderTest(Collections.reverseOrder()).groupByUniqueKey(Functions.getFixedValue(1)));
    }

    @Test
    public void groupByUniqueKey_target()
    {
        assertEquals(this.classUnderTest().groupByUniqueKey(id -> id), this.classUnderTest().groupByUniqueKey(id -> id, UnifiedMap.newMap()));
    }

    @Test
    public void groupByUniqueKey_target_throws()
    {
        assertThrows(IllegalStateException.class, () -> this.classUnderTest(Collections.reverseOrder()).groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(2, 2)));
    }

    @Test
    public void union()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Integer> union = set.union(UnifiedSet.newSet(Interval.fromTo(set.size(), set.size() + 3)));
        Verify.assertSize(set.size() + 3, union.castToSortedSet());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Interval.oneTo(set.size() + 3)), union.castToSortedSet());
        assertEquals(set, set.union(UnifiedSet.newSet()));
    }

    @Test
    public void unionInto()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> union = set.unionInto(UnifiedSet.newSet(Interval.fromTo(set.size(), set.size() + 3)), UnifiedSet.newSet());
        Verify.assertSize(set.size() + 3, union);
        assertTrue(union.containsAllIterable(Interval.oneTo(set.size() + 3)));
        assertEquals(set, set.unionInto(UnifiedSet.newSetWith(), UnifiedSet.newSet()));
    }

    @Test
    public void intersect()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        ImmutableSortedSet<Integer> intersect = set.intersect(UnifiedSet.newSet(Interval.oneTo(set.size() + 2)));
        Verify.assertSize(set.size(), intersect.castToSortedSet());
        Verify.assertSortedSetsEqual(set.castToSortedSet(), intersect.castToSortedSet());
    }

    @Test
    public void intersectInto()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> intersect = set.intersectInto(UnifiedSet.newSet(Interval.oneTo(set.size() + 2)), UnifiedSet.newSet());
        Verify.assertSize(set.size(), intersect);
        assertEquals(set, intersect);
        Verify.assertEmpty(set.intersectInto(UnifiedSet.newSet(Interval.fromTo(set.size() + 1, set.size() + 4)), UnifiedSet.newSet()));
    }

    @Test
    public void difference()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Integer> difference = set.difference(UnifiedSet.newSet(Interval.fromTo(2, set.size() + 1)));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1), difference.castToSortedSet());

        ImmutableSortedSet<Integer> difference2 = set.difference(UnifiedSet.newSet(Interval.fromTo(2, set.size() + 2)));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1), difference2.castToSortedSet());
    }

    @Test
    public void differenceInto()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> difference = set.differenceInto(UnifiedSet.newSet(Interval.fromTo(2, set.size() + 1)), UnifiedSet.newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1), difference);
    }

    @Test
    public void symmetricDifference()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.reverseOrder());
        ImmutableSortedSet<Integer> difference = set.symmetricDifference(UnifiedSet.newSet(Interval.fromTo(2, set.size() + 1)));
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, set.size() + 1),
                difference.castToSortedSet());
    }

    @Test
    public void symmetricDifferenceInto()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> difference = set.symmetricDifferenceInto(UnifiedSet.newSet(Interval.fromTo(2, set.size() + 1)), UnifiedSet.newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, set.size() + 1), difference);
    }

    @Test
    public void isSubsetOf()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertTrue(set.isSubsetOf(set));
    }

    @Test
    public void isProperSubsetOf()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertTrue(set.isProperSubsetOf(Interval.oneTo(set.size() + 1).toSet()));
        assertFalse(set.isProperSubsetOf(set));
    }

    @Test
    public void powerSet()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<SortedSetIterable<Integer>> powerSet = set.powerSet();
        Verify.assertSize((int) StrictMath.pow(2, set.size()), powerSet.castToSortedSet());
        Verify.assertContains(UnifiedSet.<String>newSet(), powerSet.toSet());
        Verify.assertContains(set, powerSet.toSet());
        Verify.assertInstanceOf(ImmutableSortedSet.class, powerSet);
        Verify.assertInstanceOf(ImmutableSortedSet.class, powerSet.getLast());
    }

    @Test
    public void cartesianProduct()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        LazyIterable<Pair<Integer, Integer>> cartesianProduct = set.cartesianProduct(UnifiedSet.newSet(Interval.oneTo(set.size())));
        assertEquals(set.size() * set.size(), cartesianProduct.size());
        assertEquals(set, cartesianProduct
                .select(Predicates.attributeEqual((Function<Pair<?, Integer>, Integer>) Pair::getTwo, 1))
                .collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne).toSet());
    }

    @Test
    public void distinct()
    {
        ImmutableSortedSet<Integer> set1 = this.classUnderTest();
        assertSame(set1, set1.distinct());
        ImmutableSortedSet<Integer> set2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertSame(set2, set2.distinct());
    }

    @Test
    public void indexOf()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertEquals(0, integers.indexOf(4));
        assertEquals(1, integers.indexOf(3));
        assertEquals(2, integers.indexOf(2));
        assertEquals(3, integers.indexOf(1));
        assertEquals(-1, integers.indexOf(0));
        assertEquals(-1, integers.indexOf(5));
    }

    @Test
    public void forEachFromTo()
    {
        MutableList<Integer> result = FastList.newList();
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.reverseNaturalOrder());
        integers.forEach(1, 2, result::add);
        assertEquals(Lists.immutable.with(3, 2), result);

        MutableList<Integer> result2 = FastList.newList();
        integers.forEach(0, 3, result2::add);
        assertEquals(Lists.immutable.with(4, 3, 2, 1), result2);

        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(-1, 0, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, -1, result::add));

        assertThrows(IllegalArgumentException.class, () -> integers.forEach(2, 1, result::add));
    }

    @Test
    public void forEachWithIndexWithFromTo()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.reverseNaturalOrder());
        StringBuilder builder = new StringBuilder();
        integers.forEachWithIndex(1, 2, (each, index) -> builder.append(each).append(index));
        assertEquals("3122", builder.toString());

        MutableList<Integer> result2 = Lists.mutable.of();
        integers.forEachWithIndex(0, 3, new AddToList(result2));
        assertEquals(Lists.immutable.with(4, 3, 2, 1), result2);

        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(-1, 0, new AddToList(result2)));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, -1, new AddToList(result2)));

        assertThrows(IllegalArgumentException.class, () -> integers.forEachWithIndex(2, 1, new AddToList(result2)));
    }

    @Test
    public void toStack()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertEquals(ArrayStack.newStackWith(4, 3, 2, 1), set.toStack());
    }

    @Test
    public void toImmutable()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Integer> actual = set.toImmutable();
        assertEquals(set, actual);
        assertSame(set, actual);
    }

    @Test
    public void take()
    {
        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        assertEquals(SortedSets.immutable.of(integers1.comparator()), integers1.take(0));
        assertSame(integers1.comparator(), integers1.take(0).comparator());
        assertEquals(SortedSets.immutable.of(integers1.comparator(), 1, 2, 3), integers1.take(3));
        assertSame(integers1.comparator(), integers1.take(3).comparator());
        assertEquals(SortedSets.immutable.of(integers1.comparator(), 1, 2, 3), integers1.take(integers1.size() - 1));

        ImmutableSortedSet<Integer> integers2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertSame(integers2, integers2.take(integers2.size()));
        assertSame(integers2, integers2.take(10));
        assertSame(integers2, integers2.take(Integer.MAX_VALUE));
    }

    @Test
    public void take_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.classUnderTest().take(-1));
    }

    @Test
    public void drop()
    {
        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        assertSame(integers1, integers1.drop(0));
        assertSame(integers1.comparator(), integers1.drop(0).comparator());
        assertEquals(SortedSets.immutable.of(integers1.comparator(), 4), integers1.drop(3));
        assertSame(integers1.comparator(), integers1.drop(3).comparator());
        assertEquals(SortedSets.immutable.of(integers1.comparator(), 4), integers1.drop(integers1.size() - 1));

        ImmutableSortedSet<Integer> expectedSet = SortedSets.immutable.of(Comparators.reverseNaturalOrder());
        ImmutableSortedSet<Integer> integers2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        assertEquals(expectedSet, integers2.drop(integers2.size()));
        assertEquals(expectedSet, integers2.drop(10));
        assertEquals(expectedSet, integers2.drop(Integer.MAX_VALUE));
    }

    @Test
    public void drop_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.classUnderTest().drop(-1));
    }

    @Test
    public abstract void subSet();

    @Test
    public abstract void headSet();

    @Test
    public abstract void tailSet();

    @Test
    public abstract void collectBoolean();

    @Test
    public abstract void collectByte();

    @Test
    public abstract void collectChar();

    @Test
    public abstract void collectDouble();

    @Test
    public abstract void collectFloat();

    @Test
    public abstract void collectInt();

    @Test
    public abstract void collectLong();

    @Test
    public abstract void collectShort();
}
