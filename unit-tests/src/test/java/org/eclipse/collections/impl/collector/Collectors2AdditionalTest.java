/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.FloatList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.ShortList;
import org.eclipse.collections.api.partition.bag.PartitionMutableBag;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.CompositeFastList;
import org.eclipse.collections.impl.partition.bag.PartitionHashBag;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.partition.set.PartitionUnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Do not merge this test with Collectors2Test. Doing so will fail the build. This is due to a bug in ASM.
Refer to links below for additional details:
1) JaCoCo issue: https://github.com/jacoco/jacoco/issues/462
2) ASM bug: http://forge.ow2.org/tracker/?func=detail&aid=317748&group_id=23&atid=100023
3) JaCoCo integration pull request: https://github.com/eclipse-collections/eclipse-collections/pull/166
 */
public class Collectors2AdditionalTest
{
    public static final Interval SMALL_INTERVAL = Interval.oneTo(5);
    public static final Interval LARGE_INTERVAL = Interval.oneTo(30000);
    public static final Integer HALF_SIZE = Integer.valueOf(LARGE_INTERVAL.size() / 2);
    private final List<Integer> smallData = new ArrayList<>(SMALL_INTERVAL);
    private final List<Integer> bigData = new ArrayList<>(LARGE_INTERVAL);

    @Test
    public void chunk()
    {
        MutableList<MutableList<Integer>> chunked0 = this.bigData.stream().collect(Collectors2.chunk(100));
        assertEquals(LARGE_INTERVAL.toList().chunk(100), chunked0);
        MutableList<MutableList<Integer>> chunked1 = this.bigData.stream().collect(Collectors2.chunk(333));
        assertEquals(LARGE_INTERVAL.toList().chunk(333), chunked1);
        MutableList<MutableList<Integer>> chunked2 = this.bigData.stream().collect(Collectors2.chunk(654));
        assertEquals(LARGE_INTERVAL.toList().chunk(654), chunked2);
        MutableList<MutableList<Integer>> chunked3 = this.smallData.stream().collect(Collectors2.chunk(SMALL_INTERVAL.size()));
        assertEquals(SMALL_INTERVAL.toList().chunk(SMALL_INTERVAL.size()), chunked3);
        MutableList<MutableList<Integer>> chunked4 = this.smallData.stream().collect(Collectors2.chunk(SMALL_INTERVAL.size() - 1));
        assertEquals(SMALL_INTERVAL.toList().chunk(SMALL_INTERVAL.size() - 1), chunked4);
        assertThrows(IllegalArgumentException.class, () -> Collectors2.chunk(0));
        assertThrows(IllegalArgumentException.class, () -> Collectors2.chunk(-10));
    }

    @Test
    public void chunkParallel()
    {
        MutableList<MutableList<Integer>> chunked = this.bigData.parallelStream().collect(Collectors2.chunk(100));
        assertTrue(chunked.size() > 1);
        Verify.assertAllSatisfy(chunked, each -> each.size() > 1 && each.size() <= 100);
    }

    @Test
    public void zip()
    {
        MutableList<Integer> integers1 = Interval.oneTo(10).toList();
        MutableList<Integer> integers2 = Interval.oneTo(10).toList().toReversed();
        assertEquals(
                integers1.zip(integers2),
                integers1.stream().collect(Collectors2.zip(integers2)));
        MutableList<Integer> integers3 = Interval.oneTo(9).toList().toReversed();
        assertEquals(
                integers1.zip(integers3),
                integers1.stream().collect(Collectors2.zip(integers3)));
        assertEquals(
                integers3.zip(integers1),
                integers3.stream().collect(Collectors2.zip(integers1)));
    }

    @Test
    public void zipParallel()
    {
        MutableList<Integer> integers1 = Interval.oneTo(10).toList();
        MutableList<Integer> integers2 = Interval.oneTo(10).toList().toReversed();

        assertThrows(UnsupportedOperationException.class, () -> integers1.parallelStream().collect(Collectors2.zip(integers2)));
    }

    @Test
    public void zipWithIndex()
    {
        MutableList<Integer> integers1 = Interval.oneTo(10).toList();
        assertEquals(
                integers1.zipWithIndex().collect(each -> PrimitiveTuples.pair(each.getOne(), each.getTwo().intValue())),
                integers1.stream().collect(Collectors2.zipWithIndex()));
    }

    @Test
    public void zipWithIndexParallel()
    {
        MutableList<Integer> integers1 = Interval.oneTo(10).toList();
        assertThrows(UnsupportedOperationException.class, () -> integers1.parallelStream().collect(Collectors2.zipWithIndex()));
    }

    @Test
    public void sumByInt()
    {
        assertEquals(
                SMALL_INTERVAL.sumByInt(each -> Integer.valueOf(each.intValue() % 2), Integer::intValue),
                SMALL_INTERVAL.stream().collect(Collectors2.sumByInt(each -> Integer.valueOf(each.intValue() % 2), Integer::intValue)));

        assertEquals(
                LARGE_INTERVAL.sumByInt(each -> Integer.valueOf(each.intValue() % 2), Integer::intValue),
                LARGE_INTERVAL.stream().collect(Collectors2.sumByInt(each -> Integer.valueOf(each.intValue() % 2), Integer::intValue)));
    }

    @Test
    public void sumByLong()
    {
        MutableList<Long> smallLongs = SMALL_INTERVAL.collect(Long::valueOf).toList();
        MutableList<Long> largeLongs = LARGE_INTERVAL.collect(Long::valueOf).toList();
        assertEquals(
                smallLongs.sumByLong(each -> Integer.valueOf(each.intValue() % 2), Long::longValue),
                smallLongs.stream().collect(Collectors2.sumByLong(each -> Integer.valueOf(each.intValue() % 2), Long::longValue)));

        assertEquals(
                largeLongs.sumByLong(each -> Integer.valueOf(each.intValue() % 2), Long::longValue),
                largeLongs.stream().collect(Collectors2.sumByLong(each -> Integer.valueOf(each.intValue() % 2), Long::longValue)));
    }

    @Test
    public void sumByFloat()
    {
        MutableList<Float> smallLongs = SMALL_INTERVAL.collect(Float::valueOf).toList();
        MutableList<Float> largeLongs = LARGE_INTERVAL.collect(Float::valueOf).toList();
        assertEquals(
                smallLongs.sumByFloat(each -> Integer.valueOf(each.intValue() % 2), Float::floatValue),
                smallLongs.stream().collect(Collectors2.sumByFloat(each -> Integer.valueOf(each.intValue() % 2), Float::floatValue)));

        assertEquals(
                largeLongs.sumByFloat(each -> Integer.valueOf(each.intValue() % 2), Float::floatValue),
                largeLongs.stream().collect(Collectors2.sumByFloat(each -> Integer.valueOf(each.intValue() % 2), Float::floatValue)));
    }

    @Test
    public void sumByDouble()
    {
        MutableList<Double> smallLongs = SMALL_INTERVAL.collect(Double::valueOf).toList();
        MutableList<Double> largeLongs = LARGE_INTERVAL.collect(Double::valueOf).toList();
        assertEquals(
                smallLongs.sumByDouble(each -> Integer.valueOf(each.intValue() % 2), Double::doubleValue),
                smallLongs.stream().collect(Collectors2.sumByDouble(each -> Integer.valueOf(each.intValue() % 2), Double::doubleValue)));

        assertEquals(
                largeLongs.sumByDouble(each -> Integer.valueOf(each.intValue() % 2), Double::doubleValue),
                largeLongs.stream().collect(Collectors2.sumByDouble(each -> Integer.valueOf(each.intValue() % 2), Double::doubleValue)));
    }

    @Test
    public void sumByBigInteger()
    {
        assertEquals(
                Iterate.sumByBigInteger(SMALL_INTERVAL, each -> Integer.valueOf(each.intValue() % 2), each -> BigInteger.valueOf(each.longValue())),
                SMALL_INTERVAL.stream().collect(Collectors2.sumByBigInteger(each -> Integer.valueOf(each.intValue() % 2), each -> BigInteger.valueOf(each.longValue()))));

        assertEquals(
                Iterate.sumByBigInteger(LARGE_INTERVAL, each -> Integer.valueOf(each.intValue() % 2), each -> BigInteger.valueOf(each.longValue())),
                LARGE_INTERVAL.stream().collect(Collectors2.sumByBigInteger(each -> Integer.valueOf(each.intValue() % 2), each -> BigInteger.valueOf(each.longValue()))));
    }

    @Test
    public void sumByBigDecimal()
    {
        assertEquals(
                Iterate.sumByBigDecimal(SMALL_INTERVAL, each -> Integer.valueOf(each.intValue() % 2), BigDecimal::new),
                SMALL_INTERVAL.stream().collect(Collectors2.sumByBigDecimal(each -> Integer.valueOf(each.intValue() % 2), BigDecimal::new)));

        assertEquals(
                Iterate.sumByBigDecimal(LARGE_INTERVAL, each -> Integer.valueOf(each.intValue() % 2), BigDecimal::new),
                LARGE_INTERVAL.stream().collect(Collectors2.sumByBigDecimal(each -> Integer.valueOf(each.intValue() % 2), BigDecimal::new)));
    }

    @Test
    public void select()
    {
        assertEquals(
                LARGE_INTERVAL.toList().select(IntegerPredicates.isEven()),
                this.bigData.stream().collect(Collectors2.select(IntegerPredicates.isEven(), Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().select(IntegerPredicates.isEven()),
                this.bigData.stream().collect(Collectors2.select(IntegerPredicates.isEven(), Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().select(IntegerPredicates.isEven()),
                this.bigData.stream().collect(Collectors2.select(IntegerPredicates.isEven(), Bags.mutable::empty)));
    }

    @Test
    public void selectParallel()
    {
        assertEquals(
                LARGE_INTERVAL.toList().select(IntegerPredicates.isEven()),
                this.bigData.parallelStream().collect(Collectors2.select(IntegerPredicates.isEven(), Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().select(IntegerPredicates.isEven()),
                this.bigData.parallelStream().collect(Collectors2.select(IntegerPredicates.isEven(), Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().select(IntegerPredicates.isEven()),
                this.bigData.parallelStream().collect(Collectors2.select(IntegerPredicates.isEven(), Bags.mutable::empty)));
    }

    @Test
    public void selectWith()
    {
        assertEquals(
                LARGE_INTERVAL.toList().selectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.stream()
                        .collect(Collectors2.selectWith(Predicates2.greaterThan(), HALF_SIZE, Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().selectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.stream()
                        .collect(Collectors2.selectWith(Predicates2.greaterThan(), HALF_SIZE, Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().selectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.stream()
                        .collect(Collectors2.selectWith(Predicates2.greaterThan(), HALF_SIZE, Bags.mutable::empty)));
    }

    @Test
    public void selectWithParallel()
    {
        assertEquals(
                LARGE_INTERVAL.toList().selectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.parallelStream()
                        .collect(Collectors2.selectWith(Predicates2.greaterThan(), HALF_SIZE, Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().selectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.parallelStream()
                        .collect(Collectors2.selectWith(Predicates2.greaterThan(), HALF_SIZE, Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().selectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.parallelStream()
                        .collect(Collectors2.selectWith(Predicates2.greaterThan(), HALF_SIZE, Bags.mutable::empty)));
    }

    @Test
    public void reject()
    {
        assertEquals(
                LARGE_INTERVAL.toList().reject(IntegerPredicates.isEven()),
                this.bigData.stream().collect(Collectors2.reject(IntegerPredicates.isEven(), Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().reject(IntegerPredicates.isEven()),
                this.bigData.stream().collect(Collectors2.reject(IntegerPredicates.isEven(), Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().reject(IntegerPredicates.isEven()),
                this.bigData.stream().collect(Collectors2.reject(IntegerPredicates.isEven(), Bags.mutable::empty)));
    }

    @Test
    public void rejectParallel()
    {
        assertEquals(
                LARGE_INTERVAL.toList().reject(IntegerPredicates.isEven()),
                this.bigData.parallelStream().collect(Collectors2.reject(IntegerPredicates.isEven(), Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().reject(IntegerPredicates.isEven()),
                this.bigData.parallelStream().collect(Collectors2.reject(IntegerPredicates.isEven(), Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().reject(IntegerPredicates.isEven()),
                this.bigData.parallelStream().collect(Collectors2.reject(IntegerPredicates.isEven(), Bags.mutable::empty)));
    }

    @Test
    public void rejectWith()
    {
        assertEquals(
                LARGE_INTERVAL.toList().rejectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.stream()
                        .collect(Collectors2.rejectWith(Predicates2.greaterThan(), HALF_SIZE, Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().rejectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.stream()
                        .collect(Collectors2.rejectWith(Predicates2.greaterThan(), HALF_SIZE, Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().rejectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.stream()
                        .collect(Collectors2.rejectWith(Predicates2.greaterThan(), HALF_SIZE, Bags.mutable::empty)));
    }

    @Test
    public void rejectWithParallel()
    {
        assertEquals(
                LARGE_INTERVAL.toList().rejectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.parallelStream()
                        .collect(Collectors2.rejectWith(Predicates2.greaterThan(), HALF_SIZE, Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().rejectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.parallelStream()
                        .collect(Collectors2.rejectWith(Predicates2.greaterThan(), HALF_SIZE, Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().rejectWith(Predicates2.greaterThan(), HALF_SIZE),
                this.bigData.parallelStream()
                        .collect(Collectors2.rejectWith(Predicates2.greaterThan(), HALF_SIZE, Bags.mutable::empty)));
    }

    @Test
    public void partition()
    {
        PartitionMutableList<Integer> expectedList = LARGE_INTERVAL.toList().partition(IntegerPredicates.isEven());
        PartitionMutableList<Integer> actualList = this.bigData.stream()
                .collect(Collectors2.partition(IntegerPredicates.isEven(), PartitionFastList::new));
        assertEquals(expectedList.getSelected(), actualList.getSelected());
        assertEquals(expectedList.getRejected(), actualList.getRejected());
        PartitionMutableSet<Integer> expectedSet = LARGE_INTERVAL.toSet().partition(IntegerPredicates.isEven());
        PartitionMutableSet<Integer> actualSet = this.bigData.stream()
                .collect(Collectors2.partition(IntegerPredicates.isEven(), PartitionUnifiedSet::new));
        assertEquals(expectedSet.getSelected(), actualSet.getSelected());
        assertEquals(expectedSet.getRejected(), actualSet.getRejected());
        PartitionMutableBag<Integer> expectedBag = LARGE_INTERVAL.toBag().partition(IntegerPredicates.isEven());
        PartitionMutableBag<Integer> actualBag = this.bigData.stream()
                .collect(Collectors2.partition(IntegerPredicates.isEven(), PartitionHashBag::new));
        assertEquals(expectedBag.getSelected(), actualBag.getSelected());
        assertEquals(expectedBag.getRejected(), actualBag.getRejected());
    }

    @Test
    public void partitionParallel()
    {
        PartitionMutableList<Integer> expectedList = LARGE_INTERVAL.toList().partition(IntegerPredicates.isEven());
        PartitionMutableList<Integer> actualList = this.bigData.parallelStream()
                .collect(Collectors2.partition(IntegerPredicates.isEven(), PartitionFastList::new));
        assertEquals(expectedList.getSelected(), actualList.getSelected());
        assertEquals(expectedList.getRejected(), actualList.getRejected());
        PartitionMutableSet<Integer> expectedSet = LARGE_INTERVAL.toSet().partition(IntegerPredicates.isEven());
        PartitionMutableSet<Integer> actualSet = this.bigData.parallelStream()
                .collect(Collectors2.partition(IntegerPredicates.isEven(), PartitionUnifiedSet::new));
        assertEquals(expectedSet.getSelected(), actualSet.getSelected());
        assertEquals(expectedSet.getRejected(), actualSet.getRejected());
        PartitionMutableBag<Integer> expectedBag = LARGE_INTERVAL.toBag().partition(IntegerPredicates.isEven());
        PartitionMutableBag<Integer> actualBag = this.bigData.parallelStream()
                .collect(Collectors2.partition(IntegerPredicates.isEven(), PartitionHashBag::new));
        assertEquals(expectedBag.getSelected(), actualBag.getSelected());
        assertEquals(expectedBag.getRejected(), actualBag.getRejected());
    }

    @Test
    public void partitionWith()
    {
        PartitionMutableList<Integer> expectedList = LARGE_INTERVAL.toList()
                .partitionWith(Predicates2.greaterThan(), HALF_SIZE);
        PartitionMutableList<Integer> actualList = this.bigData.stream()
                .collect(Collectors2.partitionWith(Predicates2.greaterThan(), HALF_SIZE, PartitionFastList::new));
        assertEquals(expectedList.getSelected(), actualList.getSelected());
        assertEquals(expectedList.getRejected(), actualList.getRejected());
        PartitionMutableSet<Integer> expectedSet = LARGE_INTERVAL.toSet()
                .partitionWith(Predicates2.greaterThan(), HALF_SIZE);
        PartitionMutableSet<Integer> actualSet = this.bigData.stream()
                .collect(Collectors2.partitionWith(Predicates2.greaterThan(), HALF_SIZE, PartitionUnifiedSet::new));
        assertEquals(expectedSet.getSelected(), actualSet.getSelected());
        assertEquals(expectedSet.getRejected(), actualSet.getRejected());
        PartitionMutableBag<Integer> expectedBag = LARGE_INTERVAL.toBag()
                .partitionWith(Predicates2.greaterThan(), HALF_SIZE);
        PartitionMutableBag<Integer> actualBag = this.bigData.stream()
                .collect(Collectors2.partitionWith(Predicates2.greaterThan(), HALF_SIZE, PartitionHashBag::new));
        assertEquals(expectedBag.getSelected(), actualBag.getSelected());
        assertEquals(expectedBag.getRejected(), actualBag.getRejected());
    }

    @Test
    public void partitionWithParallel()
    {
        PartitionMutableList<Integer> expectedList = LARGE_INTERVAL.toList()
                .partitionWith(Predicates2.greaterThan(), HALF_SIZE);
        PartitionMutableList<Integer> actualList = this.bigData.parallelStream()
                .collect(Collectors2.partitionWith(Predicates2.greaterThan(), HALF_SIZE, PartitionFastList::new));
        assertEquals(expectedList.getSelected(), actualList.getSelected());
        assertEquals(expectedList.getRejected(), actualList.getRejected());
        PartitionMutableSet<Integer> expectedSet = LARGE_INTERVAL.toSet()
                .partitionWith(Predicates2.greaterThan(), HALF_SIZE);
        PartitionMutableSet<Integer> actualSet = this.bigData.parallelStream()
                .collect(Collectors2.partitionWith(Predicates2.greaterThan(), HALF_SIZE, PartitionUnifiedSet::new));
        assertEquals(expectedSet.getSelected(), actualSet.getSelected());
        assertEquals(expectedSet.getRejected(), actualSet.getRejected());
        PartitionMutableBag<Integer> expectedBag = LARGE_INTERVAL.toBag()
                .partitionWith(Predicates2.greaterThan(), HALF_SIZE);
        PartitionMutableBag<Integer> actualBag = this.bigData.parallelStream()
                .collect(Collectors2.partitionWith(Predicates2.greaterThan(), HALF_SIZE, PartitionHashBag::new));
        assertEquals(expectedBag.getSelected(), actualBag.getSelected());
        assertEquals(expectedBag.getRejected(), actualBag.getRejected());
    }

    @Test
    public void collect()
    {
        assertEquals(
                LARGE_INTERVAL.toList().collect(Functions.getToString()),
                this.bigData.stream().collect(Collectors2.collect(Functions.getToString(), Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().collect(Functions.getToString()),
                this.bigData.stream().collect(Collectors2.collect(Functions.getToString(), Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().collect(Functions.getToString()),
                this.bigData.stream().collect(Collectors2.collect(Functions.getToString(), Bags.mutable::empty)));
    }

    @Test
    public void collectParallel()
    {
        assertEquals(
                LARGE_INTERVAL.toList().collect(Functions.getToString()),
                this.bigData.parallelStream().collect(Collectors2.collect(Functions.getToString(), Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().collect(Functions.getToString()),
                this.bigData.parallelStream().collect(Collectors2.collect(Functions.getToString(), Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().collect(Functions.getToString()),
                this.bigData.parallelStream().collect(Collectors2.collect(Functions.getToString(), Bags.mutable::empty)));
    }

    @Test
    public void flatCollect()
    {
        MutableList<Interval> list = Lists.mutable.with(SMALL_INTERVAL, SMALL_INTERVAL, SMALL_INTERVAL);
        assertEquals(
                list.flatCollect(Functions.identity()),
                list.stream().collect(Collectors2.flatCollect(Functions.identity(), Lists.mutable::empty)));
        assertEquals(
                list.flatCollect(Functions.identity()),
                list.stream().collect(Collectors2.flatCollect(Functions.identity(), CompositeFastList::new)));
        assertEquals(
                list.toSet().flatCollect(Functions.identity()),
                list.stream().collect(Collectors2.flatCollect(Functions.identity(), Sets.mutable::empty)));
        assertEquals(
                list.toBag().flatCollect(Functions.identity()),
                list.stream().collect(Collectors2.flatCollect(Functions.identity(), Bags.mutable::empty)));
        List<MutableList<String>> lists = Lists.mutable.with(
                Lists.mutable.with("a", "b"),
                Lists.mutable.with("c", "d"),
                Lists.mutable.with("e"));
        MutableList<String> flattened =
                lists.stream().collect(Collectors2.flatCollect(l -> l, Lists.mutable::empty));
        assertEquals(Lists.mutable.with("a", "b", "c", "d", "e"), flattened);
    }

    @Test
    public void flatCollectParallel()
    {
        MutableList<Interval> list = Lists.mutable.withNValues(20000, () -> SMALL_INTERVAL);
        assertEquals(
                list.flatCollect(Functions.identity()),
                list.parallelStream().collect(Collectors2.flatCollect(Functions.identity(), Lists.mutable::empty)));
        assertEquals(
                list.flatCollect(Functions.identity()),
                list.parallelStream().collect(Collectors2.flatCollect(Functions.identity(), CompositeFastList::new)));
        assertEquals(
                list.toSet().flatCollect(Functions.identity()),
                list.parallelStream().collect(Collectors2.flatCollect(Functions.identity(), Sets.mutable::empty)));
        assertEquals(
                list.toBag().flatCollect(Functions.identity()),
                list.parallelStream().collect(Collectors2.flatCollect(Functions.identity(), Bags.mutable::empty)));
    }

    @Test
    public void collectWith()
    {
        assertEquals(
                LARGE_INTERVAL.toList().collectWith(Integer::sum, Integer.valueOf(10)),
                this.bigData.stream()
                        .collect(Collectors2.collectWith(Integer::sum, Integer.valueOf(10), Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().collectWith(Integer::sum, Integer.valueOf(10)),
                this.bigData.stream()
                        .collect(Collectors2.collectWith(Integer::sum, Integer.valueOf(10), Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().collectWith(Integer::sum, Integer.valueOf(10)),
                this.bigData.stream()
                        .collect(Collectors2.collectWith(Integer::sum, Integer.valueOf(10), Bags.mutable::empty)));
    }

    @Test
    public void collectWithParallel()
    {
        assertEquals(
                LARGE_INTERVAL.toList().collectWith(Integer::sum, Integer.valueOf(10)),
                this.bigData.parallelStream()
                        .collect(Collectors2.collectWith(Integer::sum, Integer.valueOf(10), Lists.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toSet().collectWith(Integer::sum, Integer.valueOf(10)),
                this.bigData.parallelStream()
                        .collect(Collectors2.collectWith(Integer::sum, Integer.valueOf(10), Sets.mutable::empty)));
        assertEquals(
                LARGE_INTERVAL.toBag().collectWith(Integer::sum, Integer.valueOf(10)),
                this.bigData.parallelStream()
                        .collect(Collectors2.collectWith(Integer::sum, Integer.valueOf(10), Bags.mutable::empty)));
    }

    @Test
    public void collectBoolean()
    {
        BooleanList expected =
                SMALL_INTERVAL.collectBoolean(each -> each % 2 == 0, BooleanLists.mutable.empty());
        BooleanList actual =
                this.smallData.stream().collect(Collectors2.collectBoolean(each -> each % 2 == 0, BooleanLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectBooleanParallel()
    {
        BooleanList expected =
                LARGE_INTERVAL.collectBoolean(each -> each % 2 == 0, BooleanLists.mutable.empty());
        BooleanList actual =
                this.bigData.parallelStream().collect(Collectors2.collectBoolean(each -> each % 2 == 0, BooleanLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectByte()
    {
        ByteList expected =
                SMALL_INTERVAL.collectByte(each -> (byte) (each % Byte.MAX_VALUE), ByteLists.mutable.empty());
        ByteList actual =
                this.smallData.stream().collect(Collectors2.collectByte(each -> (byte) (each % Byte.MAX_VALUE), ByteLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectByteParallel()
    {
        ByteList expected =
                LARGE_INTERVAL.collectByte(each -> (byte) (each % Byte.MAX_VALUE), ByteLists.mutable.empty());
        ByteList actual =
                this.bigData.parallelStream().collect(Collectors2.collectByte(each -> (byte) (each % Byte.MAX_VALUE), ByteLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectChar()
    {
        CharList expected =
                SMALL_INTERVAL.collectChar(each -> (char) (each % Character.MAX_VALUE), CharLists.mutable.empty());
        CharList actual =
                this.smallData.stream().collect(Collectors2.collectChar(each -> (char) (each % Character.MAX_VALUE), CharLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectCharParallel()
    {
        CharList expected =
                LARGE_INTERVAL.collectChar(each -> (char) (each % Character.MAX_VALUE), CharLists.mutable.empty());
        CharList actual =
                this.bigData.parallelStream().collect(Collectors2.collectChar(each -> (char) (each % Character.MAX_VALUE), CharLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectShort()
    {
        ShortList expected =
                SMALL_INTERVAL.collectShort(each -> (short) (each % Short.MAX_VALUE), ShortLists.mutable.empty());
        ShortList actual =
                this.smallData.stream().collect(Collectors2.collectShort(each -> (short) (each % Short.MAX_VALUE), ShortLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectShortParallel()
    {
        ShortList expected =
                LARGE_INTERVAL.collectShort(each -> (short) (each % Short.MAX_VALUE), ShortLists.mutable.empty());
        ShortList actual =
                this.bigData.parallelStream().collect(Collectors2.collectShort(each -> (short) (each % Short.MAX_VALUE), ShortLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectInt()
    {
        IntList expected =
                SMALL_INTERVAL.collectInt(Integer::intValue, IntLists.mutable.empty());
        IntList actual =
                this.smallData.stream().collect(Collectors2.collectInt(each -> each, IntLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectIntParallel()
    {
        IntList expected =
                LARGE_INTERVAL.collectInt(Integer::intValue, IntLists.mutable.empty());
        IntList actual =
                this.bigData.parallelStream().collect(Collectors2.collectInt(each -> each, IntLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectFloat()
    {
        FloatList expected =
                SMALL_INTERVAL.collectFloat(Integer::floatValue, FloatLists.mutable.empty());
        FloatList actual =
                this.smallData.stream().collect(Collectors2.collectFloat(each -> (float) each, FloatLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectFloatParallel()
    {
        FloatList expected =
                LARGE_INTERVAL.collectFloat(Integer::floatValue, FloatLists.mutable.empty());
        FloatList actual =
                this.bigData.parallelStream().collect(Collectors2.collectFloat(each -> (float) each, FloatLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectLong()
    {
        LongList expected =
                SMALL_INTERVAL.collectLong(Integer::longValue, LongLists.mutable.empty());
        LongList actual =
                this.smallData.stream().collect(Collectors2.collectLong(each -> (long) each, LongLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectLongParallel()
    {
        LongList expected =
                LARGE_INTERVAL.collectLong(Integer::longValue, LongLists.mutable.empty());
        LongList actual =
                this.bigData.parallelStream().collect(Collectors2.collectLong(each -> (long) each, LongLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectDouble()
    {
        DoubleList expected =
                SMALL_INTERVAL.collectDouble(Integer::doubleValue, DoubleLists.mutable.empty());
        DoubleList actual =
                this.smallData.stream().collect(Collectors2.collectDouble(each -> (double) each, DoubleLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void collectDoubleParallel()
    {
        DoubleList expected =
                LARGE_INTERVAL.collectDouble(Integer::doubleValue, DoubleLists.mutable.empty());
        DoubleList actual =
                this.bigData.parallelStream().collect(Collectors2.collectDouble(each -> (double) each, DoubleLists.mutable::empty));
        assertEquals(expected, actual);
    }

    @Test
    public void summarizingBigDecimal()
    {
        ValueHolder valueHolder = new ValueHolder(5, 100, 10.0);
        BigDecimalSummaryStatistics summaryStatistics =
                Lists.mutable.withNValues(25_000, () -> valueHolder)
                        .stream()
                        .collect(Collectors2.summarizingBigDecimal(vh -> BigDecimal.valueOf(vh.getLongValue())));
        assertEquals(BigDecimal.valueOf(2_500_000L), summaryStatistics.getSum());
        assertEquals(BigDecimal.valueOf(100L), summaryStatistics.getMin());
        assertEquals(BigDecimal.valueOf(100L), summaryStatistics.getMax());
    }

    @Test
    public void summarizingBigDecimalParallel()
    {
        ValueHolder valueHolder = new ValueHolder(5, 100, 10.0);
        BigDecimalSummaryStatistics summaryStatistics =
                Lists.mutable.withNValues(25_000, () -> valueHolder)
                        .parallelStream()
                        .collect(Collectors2.summarizingBigDecimal(vh -> BigDecimal.valueOf(vh.getLongValue())));
        assertEquals(BigDecimal.valueOf(2_500_000L), summaryStatistics.getSum());
        assertEquals(BigDecimal.valueOf(100L), summaryStatistics.getMin());
        assertEquals(BigDecimal.valueOf(100L), summaryStatistics.getMax());
    }

    @Test
    public void summingBigDecimal()
    {
        ValueHolder valueHolder = new ValueHolder(5, 100, 10.0);
        BigDecimal sum =
                Lists.mutable.withNValues(25_000, () -> valueHolder)
                        .stream()
                        .collect(Collectors2.summingBigDecimal(vh -> BigDecimal.valueOf(vh.getLongValue())));
        assertEquals(BigDecimal.valueOf(2_500_000L), sum);
    }

    @Test
    public void summingBigDecimalParallel()
    {
        ValueHolder valueHolder = new ValueHolder(5, 100, 10.0);
        BigDecimal sum =
                Lists.mutable.withNValues(25_000, () -> valueHolder)
                        .parallelStream()
                        .collect(Collectors2.summingBigDecimal(vh -> BigDecimal.valueOf(vh.getLongValue())));
        assertEquals(BigDecimal.valueOf(2_500_000L), sum);
    }

    @Test
    public void summarizingBigInteger()
    {
        ValueHolder valueHolder = new ValueHolder(5, 100, 10.0);
        BigIntegerSummaryStatistics summaryStatistics =
                Lists.mutable.withNValues(25_000, () -> valueHolder)
                        .stream()
                        .collect(Collectors2.summarizingBigInteger(vh -> BigInteger.valueOf(vh.getLongValue())));
        assertEquals(BigInteger.valueOf(2_500_000L), summaryStatistics.getSum());
        assertEquals(BigInteger.valueOf(100L), summaryStatistics.getMin());
        assertEquals(BigInteger.valueOf(100L), summaryStatistics.getMax());
    }

    @Test
    public void summarizingBigIntegerParallel()
    {
        ValueHolder valueHolder = new ValueHolder(5, 100, 10.0);
        BigIntegerSummaryStatistics summaryStatistics =
                Lists.mutable.withNValues(25_000, () -> valueHolder)
                        .parallelStream()
                        .collect(Collectors2.summarizingBigInteger(vh -> BigInteger.valueOf(vh.getLongValue())));
        assertEquals(BigInteger.valueOf(2_500_000L), summaryStatistics.getSum());
        assertEquals(BigInteger.valueOf(100L), summaryStatistics.getMin());
        assertEquals(BigInteger.valueOf(100L), summaryStatistics.getMax());
    }

    @Test
    public void summingBigInteger()
    {
        ValueHolder valueHolder = new ValueHolder(5, 100, 10.0);
        BigInteger sum =
                Lists.mutable.withNValues(25_000, () -> valueHolder)
                        .stream()
                        .collect(Collectors2.summingBigInteger(vh -> BigInteger.valueOf(vh.getLongValue())));
        assertEquals(BigInteger.valueOf(2_500_000L), sum);
    }

    @Test
    public void summingBigIntegerParallel()
    {
        ValueHolder valueHolder = new ValueHolder(5, 100, 10.0);
        BigInteger sum =
                Lists.mutable.withNValues(25_000, () -> valueHolder)
                        .parallelStream()
                        .collect(Collectors2.summingBigInteger(vh -> BigInteger.valueOf(vh.getLongValue())));
        assertEquals(BigInteger.valueOf(2_500_000L), sum);
    }
}
