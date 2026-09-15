/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.forkjoin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Functions0;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.StringFunctions;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.ArrayListAdapter;
import org.eclipse.collections.impl.list.mutable.CompositeFastList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.multimap.bag.SynchronizedPutHashBagMultimap;
import org.eclipse.collections.impl.multimap.set.SynchronizedPutUnifiedSetMultimap;
import org.eclipse.collections.impl.parallel.AbstractProcedureCombiner;
import org.eclipse.collections.impl.parallel.PassThruCombiner;
import org.eclipse.collections.impl.parallel.PassThruObjectIntProcedureFactory;
import org.eclipse.collections.impl.parallel.PassThruProcedureFactory;
import org.eclipse.collections.impl.parallel.ProcedureFactory;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.LazyIterate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FJIterateTest
{
    private static final Procedure<Integer> EXCEPTION_PROCEDURE = value -> {
        throw new RuntimeException("Thread death on its way!");
    };

    private static final ObjectIntProcedure<Integer> EXCEPTION_OBJECT_INT_PROCEDURE = (object, index) -> {
        throw new RuntimeException("Thread death on its way!");
    };

    private static final Function<Integer, Collection<String>> INT_TO_TWO_STRINGS = integer -> Lists.fixedSize.of(integer.toString(), integer.toString());

    private static final Function0<AtomicInteger> ATOMIC_INTEGER_NEW = Functions0.zeroAtomicInteger();

    private static final Function0<Integer> INTEGER_NEW = Functions0.value(0);

    private static final Function<Integer, String> EVEN_OR_ODD = value -> value % 2 == 0 ? "Even" : "Odd";

    private ImmutableList<RichIterable<Integer>> iterables;
    private final ForkJoinPool executor = new ForkJoinPool(2);

    @BeforeEach
    public void setUp()
    {
        Interval interval = Interval.oneTo(200);
        this.iterables = Lists.immutable.of(
                interval.toList(),
                interval.toList().asUnmodifiable(),
                interval.toList().asSynchronized(),
                interval.toList().toImmutable(),
                interval.toSet(),
                interval.toSet().asUnmodifiable(),
                interval.toSet().asSynchronized(),
                interval.toSet().toImmutable(),
                interval.toBag(),
                interval.toBag().asUnmodifiable(),
                interval.toBag().asSynchronized(),
                interval.toBag().toImmutable(),
                interval.toSortedSet(),
                interval.toSortedSet().asUnmodifiable(),
                interval.toSortedSet().asSynchronized(),
                interval.toSortedSet().toImmutable(),
                interval.toMap(Functions.getPassThru(), Functions.getPassThru()),
                interval.toMap(Functions.getPassThru(), Functions.getPassThru()).asUnmodifiable(),
                interval.toMap(Functions.getPassThru(), Functions.getPassThru()).asSynchronized(),
                interval.toMap(Functions.getPassThru(), Functions.getPassThru()).toImmutable(),
                new CompositeFastList<Integer>().withAll(interval.toList()),
                new CompositeFastList<Integer>().withAll(interval.toList()).asUnmodifiable(),
                new CompositeFastList<Integer>().withAll(interval.toList()).asSynchronized(),
                ArrayListAdapter.<Integer>newList().withAll(interval),
                ArrayListAdapter.<Integer>newList().withAll(interval).asUnmodifiable(),
                ArrayListAdapter.<Integer>newList().withAll(interval).asSynchronized(),
                ListAdapter.adapt(new LinkedList<Integer>()).withAll(interval),
                ListAdapter.adapt(new LinkedList<Integer>()).withAll(interval).asUnmodifiable(),
                ListAdapter.adapt(new LinkedList<Integer>()).withAll(interval).asSynchronized(),
                UnifiedSetWithHashingStrategy.<Integer>newSet(HashingStrategies.defaultStrategy()).withAll(interval),
                UnifiedSetWithHashingStrategy.<Integer>newSet(HashingStrategies.defaultStrategy()).withAll(interval).asUnmodifiable(),
                UnifiedSetWithHashingStrategy.<Integer>newSet(HashingStrategies.defaultStrategy()).withAll(interval).asSynchronized(),
                UnifiedSetWithHashingStrategy.<Integer>newSet(HashingStrategies.defaultStrategy()).withAll(interval).toImmutable());
    }

    @AfterEach
    public void tearDown()
    {
        this.executor.shutdown();
    }

    @Test
    public void testForEachUsingSet()
    {
        //Tests the default batch size calculations
        IntegerSum sum = new IntegerSum(0);
        MutableSet<Integer> set = Interval.toSet(1, 100);
        FJIterate.forEach(set, new SumProcedure(sum), new SumCombiner(sum));
        assertEquals(5050, sum.getSum());

        //Testing batch size 1
        IntegerSum sum2 = new IntegerSum(0);
        UnifiedSet<Integer> set2 = UnifiedSet.newSet(Interval.oneTo(100));
        FJIterate.forEach(set2, new SumProcedure(sum2), new SumCombiner(sum2), 1, set2.getBatchCount(set2.size()));
        assertEquals(5050, sum2.getSum());

        //Testing an uneven batch size
        IntegerSum sum3 = new IntegerSum(0);
        UnifiedSet<Integer> set3 = UnifiedSet.newSet(Interval.oneTo(100));
        FJIterate.forEach(set3, new SumProcedure(sum3), new SumCombiner(sum3), 1, set3.getBatchCount(13));
        assertEquals(5050, sum3.getSum());

        //Testing divideByZero exception by passing 1 as batchSize
        IntegerSum sum4 = new IntegerSum(0);
        UnifiedSet<Integer> set4 = UnifiedSet.newSet(Interval.oneTo(100));
        FJIterate.forEach(set4, new SumProcedure(sum4), new SumCombiner(sum4), 1);
        assertEquals(5050, sum4.getSum());
    }

    @Test
    public void testForEachUsingMap()
    {
        //Test the default batch size calculations
        IntegerSum sum1 = new IntegerSum(0);
        MutableMap<String, Integer> map1 = Interval.fromTo(1, 100).toMap(Functions.getToString(), Functions.getIntegerPassThru());
        FJIterate.forEach(map1, new SumProcedure(sum1), new SumCombiner(sum1));
        assertEquals(5050, sum1.getSum());

        //Testing batch size 1
        IntegerSum sum2 = new IntegerSum(0);
        UnifiedMap<String, Integer> map2 = (UnifiedMap<String, Integer>) Interval.fromTo(1, 100).toMap(Functions.getToString(), Functions.getIntegerPassThru());
        FJIterate.forEach(map2, new SumProcedure(sum2), new SumCombiner(sum2), 1, map2.getBatchCount(map2.size()));
        assertEquals(5050, sum2.getSum());

        //Testing an uneven batch size
        IntegerSum sum3 = new IntegerSum(0);
        UnifiedMap<String, Integer> set3 = (UnifiedMap<String, Integer>) Interval.fromTo(1, 100).toMap(Functions.getToString(), Functions.getIntegerPassThru());
        FJIterate.forEach(set3, new SumProcedure(sum3), new SumCombiner(sum3), 1, set3.getBatchCount(13));
        assertEquals(5050, sum3.getSum());
    }

    @Test
    public void testForEach()
    {
        IntegerSum sum1 = new IntegerSum(0);
        List<Integer> list1 = FJIterateTest.createIntegerList(16);
        FJIterate.forEach(list1, new SumProcedure(sum1), new SumCombiner(sum1), 1, list1.size() / 2);
        assertEquals(16, sum1.getSum());

        IntegerSum sum2 = new IntegerSum(0);
        List<Integer> list2 = FJIterateTest.createIntegerList(7);
        FJIterate.forEach(list2, new SumProcedure(sum2), new SumCombiner(sum2));
        assertEquals(7, sum2.getSum());

        IntegerSum sum3 = new IntegerSum(0);
        List<Integer> list3 = FJIterateTest.createIntegerList(15);
        FJIterate.forEach(list3, new SumProcedure(sum3), new SumCombiner(sum3), 1, list3.size() / 2);
        assertEquals(15, sum3.getSum());

        IntegerSum sum4 = new IntegerSum(0);
        List<Integer> list4 = FJIterateTest.createIntegerList(35);
        FJIterate.forEach(list4, new SumProcedure(sum4), new SumCombiner(sum4));
        assertEquals(35, sum4.getSum());

        IntegerSum sum5 = new IntegerSum(0);
        MutableList<Integer> list5 = FastList.newList(list4);
        FJIterate.forEach(list5, new SumProcedure(sum5), new SumCombiner(sum5));
        assertEquals(35, sum5.getSum());

        IntegerSum sum6 = new IntegerSum(0);
        List<Integer> list6 = FJIterateTest.createIntegerList(40);
        FJIterate.forEach(list6, new SumProcedure(sum6), new SumCombiner(sum6), 1, list6.size() / 2);
        assertEquals(40, sum6.getSum());

        IntegerSum sum7 = new IntegerSum(0);
        MutableList<Integer> list7 = FastList.newList(list6);
        FJIterate.forEach(list7, new SumProcedure(sum7), new SumCombiner(sum7), 1, list6.size() / 2);
        assertEquals(40, sum7.getSum());
    }

    @Test
    public void testForEachImmutable()
    {
        IntegerSum sum1 = new IntegerSum(0);
        ImmutableList<Integer> list1 = Lists.immutable.ofAll(FJIterateTest.createIntegerList(16));
        FJIterate.forEach(list1, new SumProcedure(sum1), new SumCombiner(sum1), 1, list1.size() / 2);
        assertEquals(16, sum1.getSum());

        IntegerSum sum2 = new IntegerSum(0);
        ImmutableList<Integer> list2 = Lists.immutable.ofAll(FJIterateTest.createIntegerList(7));
        FJIterate.forEach(list2, new SumProcedure(sum2), new SumCombiner(sum2));
        assertEquals(7, sum2.getSum());

        IntegerSum sum3 = new IntegerSum(0);
        ImmutableList<Integer> list3 = Lists.immutable.ofAll(FJIterateTest.createIntegerList(15));
        FJIterate.forEach(list3, new SumProcedure(sum3), new SumCombiner(sum3), 1, list3.size() / 2);
        assertEquals(15, sum3.getSum());

        IntegerSum sum4 = new IntegerSum(0);
        ImmutableList<Integer> list4 = Lists.immutable.ofAll(FJIterateTest.createIntegerList(35));
        FJIterate.forEach(list4, new SumProcedure(sum4), new SumCombiner(sum4));
        assertEquals(35, sum4.getSum());

        IntegerSum sum5 = new IntegerSum(0);
        ImmutableList<Integer> list5 = FastList.newList(list4).toImmutable();
        FJIterate.forEach(list5, new SumProcedure(sum5), new SumCombiner(sum5));
        assertEquals(35, sum5.getSum());

        IntegerSum sum6 = new IntegerSum(0);
        ImmutableList<Integer> list6 = Lists.immutable.ofAll(FJIterateTest.createIntegerList(40));
        FJIterate.forEach(list6, new SumProcedure(sum6), new SumCombiner(sum6), 1, list6.size() / 2);
        assertEquals(40, sum6.getSum());

        IntegerSum sum7 = new IntegerSum(0);
        ImmutableList<Integer> list7 = FastList.newList(list6).toImmutable();
        FJIterate.forEach(list7, new SumProcedure(sum7), new SumCombiner(sum7), 1, list6.size() / 2);
        assertEquals(40, sum7.getSum());
    }

    @Test
    public void testForEachWithException()
    {
        assertThrows(RuntimeException.class, () -> FJIterate.forEach(
                FJIterateTest.createIntegerList(5),
                new PassThruProcedureFactory<>(EXCEPTION_PROCEDURE),
                new PassThruCombiner<>(),
                1,
                5));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFastListSerialPath()
    {
        Integer[] array = new Integer[200];
        MutableList<Integer> list = new FastList<>(Interval.oneTo(200));
        assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        FJIterate.forEachWithIndex(list, (each, index) -> array[index] = each);
        assertArrayEquals(array, list.toArray(new Integer[]{}));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFastList()
    {
        Integer[] array = new Integer[200];
        MutableList<Integer> list = new FastList<>(Interval.oneTo(200));
        assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        FJIterate.forEachWithIndex(list, (each, index) -> array[index] = each, 10, 10);
        assertArrayEquals(array, list.toArray(new Integer[]{}));
    }

    @Test
    public void testForEachWithIndexToArrayUsingImmutableList()
    {
        Integer[] array = new Integer[200];
        ImmutableList<Integer> list = Interval.oneTo(200).toList().toImmutable();
        assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        FJIterate.forEachWithIndex(list, (each, index) -> array[index] = each, 10, 10);
        assertArrayEquals(array, list.toArray(new Integer[]{}));
    }

    @Test
    public void testForEachWithIndexToArrayUsingArrayList()
    {
        Integer[] array = new Integer[200];
        MutableList<Integer> list = FastList.newList(Interval.oneTo(200));
        assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        FJIterate.forEachWithIndex(list, (each, index) -> array[index] = each, 10, 10);
        assertArrayEquals(array, list.toArray(new Integer[]{}));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFixedArrayList()
    {
        Integer[] array = new Integer[10];
        assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        FJIterate.forEachWithIndex(list, (each, index) -> array[index] = each, 1, 2);
        assertArrayEquals(array, list.toArray(new Integer[list.size()]));
    }

    @Test
    public void testForEachWithIndexException()
    {
        assertThrows(RuntimeException.class, () -> FJIterate.forEachWithIndex(
                FJIterateTest.createIntegerList(5),
                new PassThruObjectIntProcedureFactory<>(EXCEPTION_OBJECT_INT_PROCEDURE),
                new PassThruCombiner<>(),
                1,
                5));
    }

    @Test
    public void select()
    {
        this.iterables.each(this::basicSelect);
    }

    private void basicSelect(RichIterable<Integer> iterable)
    {
        Collection<Integer> actual1 = FJIterate.select(iterable, Predicates.greaterThan(100));
        Collection<Integer> actual2 = FJIterate.select(iterable, Predicates.greaterThan(100), HashBag.newBag(), 3, this.executor, true);
        Collection<Integer> actual3 = FJIterate.select(iterable, Predicates.greaterThan(100), true);
        RichIterable<Integer> expected = iterable.select(Predicates.greaterThan(100));
        assertEquals(expected, actual1, expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        assertEquals(expected.toBag(), actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        assertEquals(expected, actual3, expected.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    @Test
    public void selectSortedSet()
    {
        RichIterable<Integer> iterable = Interval.oneTo(200).toSortedSet();
        Collection<Integer> actual1 = FJIterate.select(iterable, Predicates.greaterThan(100));
        Collection<Integer> actual2 = FJIterate.select(iterable, Predicates.greaterThan(100), true);
        RichIterable<Integer> expected = iterable.select(Predicates.greaterThan(100));
        assertSame(expected.getClass(), actual1.getClass());
        assertSame(expected.getClass(), actual2.getClass());
        assertEquals(expected, actual1, expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        assertEquals(expected, actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
    }

    @Test
    public void count()
    {
        this.iterables.each(this::basicCount);
    }

    private void basicCount(RichIterable<Integer> iterable)
    {
        int actual1 = FJIterate.count(iterable, Predicates.greaterThan(100));
        int actual2 = FJIterate.count(iterable, Predicates.greaterThan(100), 6, this.executor);
        assertEquals(100, actual1);
        assertEquals(100, actual2);
    }

    @Test
    public void reject()
    {
        this.iterables.each(this::basicReject);
    }

    private void basicReject(RichIterable<Integer> iterable)
    {
        Collection<Integer> actual1 = FJIterate.reject(iterable, Predicates.greaterThan(100));
        Collection<Integer> actual2 = FJIterate.reject(iterable, Predicates.greaterThan(100), HashBag.newBag(), 3, this.executor, true);
        Collection<Integer> actual3 = FJIterate.reject(iterable, Predicates.greaterThan(100), true);
        RichIterable<Integer> expected = iterable.reject(Predicates.greaterThan(100));
        assertEquals(expected, actual1, expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        assertEquals(expected.toBag(), actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        assertEquals(expected, actual3, expected.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    @Test
    public void collect()
    {
        this.iterables.each(this::basicCollect);
    }

    private void basicCollect(RichIterable<Integer> iterable)
    {
        Collection<String> actual1 = FJIterate.collect(iterable, Functions.getToString());
        Collection<String> actual2 = FJIterate.collect(iterable, Functions.getToString(), HashBag.newBag(), 3, this.executor, false);
        Collection<String> actual3 = FJIterate.collect(iterable, Functions.getToString(), true);
        RichIterable<String> expected = iterable.collect(Functions.getToString());
        Verify.assertSize(200, actual1);
        Verify.assertContains(String.valueOf(200), actual1);
        assertEquals(expected, actual1, expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        assertEquals(expected.toBag(), actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        assertEquals(expected.toBag(), HashBag.newBag(actual3), expected.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    @Test
    public void collectIf()
    {
        this.iterables.each(this::basicCollectIf);
    }

    private void basicCollectIf(RichIterable<Integer> collection)
    {
        Predicate<Integer> greaterThan = Predicates.greaterThan(100);
        Collection<String> actual1 = FJIterate.collectIf(collection, greaterThan, Functions.getToString());
        Collection<String> actual2 = FJIterate.collectIf(collection, greaterThan, Functions.getToString(), HashBag.newBag(), 3, this.executor, true);
        Collection<String> actual3 = FJIterate.collectIf(collection, greaterThan, Functions.getToString(), HashBag.newBag(), 3, this.executor, true);
        Bag<String> expected = collection.collectIf(greaterThan, Functions.getToString()).toBag();
        Verify.assertSize(100, actual1);
        Verify.assertNotContains(String.valueOf(90), actual1);
        Verify.assertNotContains(String.valueOf(210), actual1);
        Verify.assertContains(String.valueOf(159), actual1);
        assertEquals(expected, HashBag.newBag(actual1), expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        assertEquals(expected, actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        assertEquals(expected, actual3, expected.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    @Test
    public void groupByWithInterval()
    {
        LazyIterable<Integer> iterable = Interval.oneTo(1000).concatenate(Interval.oneTo(1000)).concatenate(Interval.oneTo(1000));
        Multimap<String, Integer> expected = iterable.toBag().groupBy(Functions.getToString());
        Multimap<String, Integer> expectedAsSet = iterable.toSet().groupBy(Functions.getToString());
        Multimap<String, Integer> result1 = FJIterate.groupBy(iterable.toList(), Functions.getToString(), 100);
        Multimap<String, Integer> result2 = FJIterate.groupBy(iterable.toList(), Functions.getToString());
        Multimap<String, Integer> result3 = FJIterate.groupBy(iterable.toSet(), Functions.getToString(), SynchronizedPutUnifiedSetMultimap.newMultimap(), 100);
        Multimap<String, Integer> result4 = FJIterate.groupBy(iterable.toSet(), Functions.getToString(), SynchronizedPutUnifiedSetMultimap.newMultimap());
        Multimap<String, Integer> result5 = FJIterate.groupBy(iterable.toSortedSet(), Functions.getToString(), SynchronizedPutUnifiedSetMultimap.newMultimap(), 100);
        Multimap<String, Integer> result6 = FJIterate.groupBy(iterable.toSortedSet(), Functions.getToString(), SynchronizedPutUnifiedSetMultimap.newMultimap());
        Multimap<String, Integer> result7 = FJIterate.groupBy(iterable.toBag(), Functions.getToString(), SynchronizedPutHashBagMultimap.newMultimap(), 100);
        Multimap<String, Integer> result8 = FJIterate.groupBy(iterable.toBag(), Functions.getToString(), SynchronizedPutHashBagMultimap.newMultimap());
        Multimap<String, Integer> result9 = FJIterate.groupBy(iterable.toList().toImmutable(), Functions.getToString());
        assertEquals(expected, HashBagMultimap.newMultimap(result1));
        assertEquals(expected, HashBagMultimap.newMultimap(result2));
        assertEquals(expected, HashBagMultimap.newMultimap(result9));
        assertEquals(expectedAsSet, result3);
        assertEquals(expectedAsSet, result4);
        assertEquals(expectedAsSet, result5);
        assertEquals(expectedAsSet, result6);
        assertEquals(expected, result7);
        assertEquals(expected, result8);
    }

    @Test
    public void groupBy()
    {
        MutableList<String> source = FastList.newListWith("Ted", "Sally", "Mary", "Bob", "Sara");
        Multimap<Character, String> result1 = FJIterate.groupBy(source, StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result2 = FJIterate.groupBy(Collections.synchronizedList(source), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result3 = FJIterate.groupBy(Collections.synchronizedCollection(source), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result4 = FJIterate.groupBy(LazyIterate.adapt(source), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result5 = FJIterate.groupBy(new ArrayList<>(source), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result6 = FJIterate.groupBy(source.toSet(), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result7 = FJIterate.groupBy(source.toMap(Functions.getStringPassThru(), Functions.getStringPassThru()), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result8 = FJIterate.groupBy(source.toBag(), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result9 = FJIterate.groupBy(source.toImmutable(), StringFunctions.firstLetter(), 1);
        MutableMultimap<Character, String> expected = HashBagMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.put('S', "Sally");
        expected.put('M', "Mary");
        expected.put('B', "Bob");
        expected.put('S', "Sara");
        assertEquals(expected, HashBagMultimap.newMultimap(result1));
        assertEquals(expected, HashBagMultimap.newMultimap(result2));
        assertEquals(expected, HashBagMultimap.newMultimap(result3));
        assertEquals(expected, HashBagMultimap.newMultimap(result4));
        assertEquals(expected, HashBagMultimap.newMultimap(result5));
        assertEquals(expected, HashBagMultimap.newMultimap(result6));
        assertEquals(expected, HashBagMultimap.newMultimap(result7));
        assertEquals(expected, HashBagMultimap.newMultimap(result8));
        assertEquals(expected, HashBagMultimap.newMultimap(result9));
        assertThrows(IllegalArgumentException.class, () -> FJIterate.groupBy(null, null, 1));
    }

    @Test
    public void aggregateInPlaceBy()
    {
        Procedure2<AtomicInteger, Integer> countAggregator = (aggregate, value) -> aggregate.incrementAndGet();
        List<Integer> list = Interval.oneTo(2000);
        MutableMap<String, AtomicInteger> aggregation =
                FJIterate.aggregateInPlaceBy(list, EVEN_OR_ODD, ATOMIC_INTEGER_NEW, countAggregator);
        assertEquals(1000, aggregation.get("Even").intValue());
        assertEquals(1000, aggregation.get("Odd").intValue());
        FJIterate.aggregateInPlaceBy(list, EVEN_OR_ODD, ATOMIC_INTEGER_NEW, countAggregator, aggregation);
        assertEquals(2000, aggregation.get("Even").intValue());
        assertEquals(2000, aggregation.get("Odd").intValue());
    }

    @Test
    public void aggregateInPlaceByWithBatchSize()
    {
        Procedure2<AtomicInteger, Integer> sumAggregator = AtomicInteger::addAndGet;
        MutableList<Integer> list = LazyIterate.adapt(Collections.nCopies(100, 1))
                .concatenate(Collections.nCopies(200, 2))
                .concatenate(Collections.nCopies(300, 3))
                .toList()
                .shuffleThis();
        MapIterable<String, AtomicInteger> aggregation =
                FJIterate.aggregateInPlaceBy(list, Functions.getToString(), ATOMIC_INTEGER_NEW, sumAggregator, 50);
        assertEquals(100, aggregation.get("1").intValue());
        assertEquals(400, aggregation.get("2").intValue());
        assertEquals(900, aggregation.get("3").intValue());
    }

    @Test
    public void aggregateBy()
    {
        Function2<Integer, Integer, Integer> countAggregator = (aggregate, value) -> aggregate + 1;
        List<Integer> list = Interval.oneTo(20000);
        MutableMap<String, Integer> aggregation =
                FJIterate.aggregateBy(list, EVEN_OR_ODD, INTEGER_NEW, countAggregator);
        assertEquals(10000, aggregation.get("Even").intValue());
        assertEquals(10000, aggregation.get("Odd").intValue());
        FJIterate.aggregateBy(list, EVEN_OR_ODD, INTEGER_NEW, countAggregator, aggregation);
        assertEquals(20000, aggregation.get("Even").intValue());
        assertEquals(20000, aggregation.get("Odd").intValue());
    }

    @Test
    public void aggregateByWithBatchSize()
    {
        Function2<Integer, Integer, Integer> sumAggregator = (aggregate, value) -> aggregate + value;
        MutableList<Integer> list = LazyIterate.adapt(Collections.nCopies(1000, 1))
                .concatenate(Collections.nCopies(2000, 2))
                .concatenate(Collections.nCopies(3000, 3))
                .toList()
                .shuffleThis();
        MapIterable<String, Integer> aggregation =
                FJIterate.aggregateBy(list, Functions.getToString(), INTEGER_NEW, sumAggregator, 100);
        assertEquals(1000, aggregation.get("1").intValue());
        assertEquals(4000, aggregation.get("2").intValue());
        assertEquals(9000, aggregation.get("3").intValue());
    }

    private static List<Integer> createIntegerList(int size)
    {
        return Collections.nCopies(size, Integer.valueOf(1));
    }

    @Test
    public void flatCollect()
    {
        this.iterables.each(this::basicFlatCollect);
    }

    private void basicFlatCollect(RichIterable<Integer> iterable)
    {
        Collection<String> actual1 = FJIterate.flatCollect(iterable, INT_TO_TWO_STRINGS);
        Collection<String> actual2 = FJIterate.flatCollect(iterable, INT_TO_TWO_STRINGS, HashBag.newBag(), 3, this.executor, false);
        Collection<String> actual3 = FJIterate.flatCollect(iterable, INT_TO_TWO_STRINGS, true);
        RichIterable<String> expected1 = iterable.flatCollect(INT_TO_TWO_STRINGS);
        RichIterable<String> expected2 = iterable.flatCollect(INT_TO_TWO_STRINGS, HashBag.newBag());
        Verify.assertContains(String.valueOf(200), actual1);
        assertEquals(expected1, actual1, expected1.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        assertEquals(expected2, actual2, expected2.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        assertEquals(expected1, actual3, expected1.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    public static final class IntegerSum
    {
        private int sum;

        public IntegerSum(int newSum)
        {
            this.sum = newSum;
        }

        public IntegerSum add(int value)
        {
            this.sum += value;
            return this;
        }

        public int getSum()
        {
            return this.sum;
        }
    }

    public static final class SumProcedure
            implements Procedure<Integer>, Function2<IntegerSum, Integer, IntegerSum>, ProcedureFactory<SumProcedure>
    {
        private static final long serialVersionUID = 1L;

        private final IntegerSum sum;

        public SumProcedure(IntegerSum newSum)
        {
            this.sum = newSum;
        }

        @Override
        public SumProcedure create()
        {
            return new SumProcedure(new IntegerSum(0));
        }

        @Override
        public IntegerSum value(IntegerSum s1, Integer s2)
        {
            return s1.add(s2);
        }

        @Override
        public void value(Integer object)
        {
            this.sum.add(object);
        }

        public int getSum()
        {
            return this.sum.getSum();
        }
    }

    public static final class SumCombiner extends AbstractProcedureCombiner<SumProcedure>
    {
        private static final long serialVersionUID = 1L;
        private final IntegerSum sum;

        public SumCombiner(IntegerSum initialSum)
        {
            super(true);
            this.sum = initialSum;
        }

        @Override
        public void combineOne(SumProcedure sumProcedure)
        {
            this.sum.add(sumProcedure.getSum());
        }
    }
}
