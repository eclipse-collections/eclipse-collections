/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
public class FunctionalInterfaceSetTest
{
    private static final int SIZE = 1_000_000;
    private static final int BATCH_SIZE = 10_000;

    @Param({"0", "1", "2", "3"})
    public int megamorphicWarmupLevel;

    private final List<Integer> integersJDK = new ArrayList<>(Interval.oneTo(SIZE));
    private final FastList<Integer> integersEC = new FastList<>(Interval.oneTo(SIZE));

    private ExecutorService executorService;

    @Before
    @Setup
    public void setUp()
    {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Before
    @Setup(Level.Trial)
    public void setUp_megamorphic()
    {
        this.setUp();

        org.eclipse.collections.api.block.predicate.Predicate<Integer> predicate1 = each -> (each + 2) % 10_000 != 0;
        org.eclipse.collections.api.block.predicate.Predicate<Integer> predicate2 = each -> (each + 3) % 10_000 != 0;
        org.eclipse.collections.api.block.predicate.Predicate<Integer> predicate3 = each -> (each + 4) % 10_000 != 0;
        org.eclipse.collections.api.block.predicate.Predicate<Integer> predicate4 = each -> (each + 5) % 10_000 != 0;

        org.eclipse.collections.api.block.function.Function<Integer, String> function1 = each ->
        {
            Assert.assertNotNull(each);
            return String.valueOf(each);
        };

        org.eclipse.collections.api.block.function.Function<String, Integer> function2 = each -> {
            Assert.assertNotNull(each);
            return Integer.valueOf(each);
        };

        org.eclipse.collections.api.block.function.Function<Integer, String> function3 = each ->
        {
            Assert.assertSame(each, each);
            return String.valueOf(each);
        };

        org.eclipse.collections.api.block.function.Function<String, Integer> function4 = each -> {
            Assert.assertSame(each, each);
            return Integer.valueOf(each);
        };

        if (this.megamorphicWarmupLevel > 0)
        {
            Predicate<Integer> predicateJDK1 = each -> (each + 2) % 10_000 != 0;
            Predicate<Integer> predicateJDK2 = each -> (each + 3) % 10_000 != 0;
            Predicate<Integer> predicateJDK3 = each -> (each + 4) % 10_000 != 0;
            Predicate<Integer> predicateJDK4 = each -> (each + 5) % 10_000 != 0;

            Function<Integer, String> mapper1 = each ->
            {
                Assert.assertNotNull(each);
                return String.valueOf(each);
            };

            Function<String, Integer> mapper2 = each -> {
                Assert.assertNotNull(each);
                return Integer.valueOf(each);
            };

            Function<Integer, String> mapper3 = each ->
            {
                Assert.assertSame(each, each);
                return String.valueOf(each);
            };

            Function<String, Integer> mapper4 = each -> {
                Assert.assertSame(each, each);
                return Integer.valueOf(each);
            };

            // serial, lazy, JDK
            {
                Set<Integer> set = this.integersJDK.stream()
                        .filter(predicateJDK1)
                        .map(mapper1)
                        .map(mapper2)
                        .filter(predicateJDK2)
                        .collect(Collectors.toSet());
                Verify.assertSize(999_800, set);

                List<Integer> collection = this.integersJDK.stream()
                        .filter(predicateJDK3)
                        .map(mapper3)
                        .map(mapper4)
                        .filter(predicateJDK4)
                        .collect(Collectors.toCollection(ArrayList::new));
                Verify.assertSize(999_800, collection);
            }

            // parallel, lazy, JDK
            {
                Set<Integer> set = this.integersJDK.parallelStream()
                        .filter(predicateJDK1)
                        .map(mapper1)
                        .map(mapper2)
                        .filter(predicateJDK2)
                        .collect(Collectors.toSet());
                Verify.assertSize(999_800, set);

                List<Integer> collection = this.integersJDK.parallelStream()
                        .filter(predicateJDK3)
                        .map(mapper3)
                        .map(mapper4)
                        .filter(predicateJDK4)
                        .collect(Collectors.toCollection(ArrayList::new));
                Verify.assertSize(999_800, collection);
            }

            // serial, lazy, EC
            {
                MutableSet<Integer> set = this.integersEC.asLazy()
                        .select(predicate1)
                        .collect(function1)
                        .collect(function2)
                        .select(predicate2)
                        .toSet();
                Verify.assertSize(999_800, set);

                MutableBag<Integer> bag = this.integersEC.asLazy()
                        .select(predicate3)
                        .collect(function3)
                        .collect(function4)
                        .select(predicate4)
                        .toBag();
                Verify.assertIterableSize(999_800, bag);
            }

            // parallel, lazy, EC
            {
                MutableSet<Integer> set = this.integersEC.asParallel(this.executorService, BATCH_SIZE)
                        .select(predicate1)
                        .collect(function1)
                        .collect(function2)
                        .select(predicate2)
                        .toSet();
                Verify.assertSize(999_800, set);

                MutableBag<Integer> bag = this.integersEC.asParallel(this.executorService, BATCH_SIZE)
                        .select(predicate3)
                        .collect(function3)
                        .collect(function4)
                        .select(predicate4)
                        .toBag();
                Verify.assertIterableSize(999_800, bag);
            }

            // serial, eager, EC
            MutableSet<Integer> set = this.integersEC
                    .select(predicate1)
                    .collect(function1)
                    .collect(function2)
                    .select(predicate2)
                    .toSet();
            Verify.assertSize(999_800, set);

            MutableBag<Integer> bag = this.integersEC
                    .select(predicate3)
                    .collect(function3)
                    .collect(function4)
                    .select(predicate4)
                    .toBag();
            Verify.assertIterableSize(999_800, bag);
        }

        if (this.megamorphicWarmupLevel > 1)
        {
            // parallel, eager, EC
            Collection<Integer> select1 = ParallelIterate.select(this.integersEC, predicate1, new UnifiedSet<>(), true);
            Collection<String> collect1 = ParallelIterate.collect(select1, function1, new UnifiedSet<>(), true);
            Collection<Integer> collect2 = ParallelIterate.collect(collect1, function2, new UnifiedSet<>(), true);
            UnifiedSet<Integer> set = ParallelIterate.select(collect2, predicate2, new UnifiedSet<>(), true);
            Verify.assertSize(999_800, set);

            Collection<Integer> select3 = ParallelIterate.select(this.integersEC, predicate3, new HashBag<>(), true);
            Collection<String> collect3 = ParallelIterate.collect(select3, function3, new HashBag<>(), true);
            Collection<Integer> collect4 = ParallelIterate.collect(collect3, function4, new HashBag<>(), true);
            HashBag<Integer> bag = ParallelIterate.select(collect4, predicate4, new HashBag<>(), true);
            Verify.assertSize(999_800, bag);
        }

        if (this.megamorphicWarmupLevel > 2)
        {
            // parallel, eager, EC, executorService
            UnifiedSet<Integer> select1 = ParallelIterate.select(this.integersEC, predicate1, new UnifiedSet<>(), BATCH_SIZE, this.executorService, true);
            UnifiedSet<String> collect1 = ParallelIterate.collect(select1, function1, new UnifiedSet<>(), BATCH_SIZE, this.executorService, true);
            UnifiedSet<Integer> collect2 = ParallelIterate.collect(collect1, function2, new UnifiedSet<>(), BATCH_SIZE, this.executorService, true);
            UnifiedSet<Integer> set = ParallelIterate.select(collect2, predicate2, new UnifiedSet<>(), BATCH_SIZE, this.executorService, true);
            Verify.assertSize(999_800, set);

            HashBag<Integer> select3 = ParallelIterate.select(this.integersEC, predicate3, new HashBag<>(), BATCH_SIZE, this.executorService, true);
            HashBag<String> collect3 = ParallelIterate.collect(select3, function3, new HashBag<>(), BATCH_SIZE, this.executorService, true);
            HashBag<Integer> collect4 = ParallelIterate.collect(collect3, function4, new HashBag<>(), BATCH_SIZE, this.executorService, true);
            HashBag<Integer> bag = ParallelIterate.select(collect4, predicate4, new HashBag<>(), BATCH_SIZE, this.executorService, true);
            Verify.assertSize(999_800, bag);
        }

        FunctionalInterfaceScalaTest.megamorphic(this.megamorphicWarmupLevel);
    }

    @After
    @TearDown
    public void tearDown() throws InterruptedException
    {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(1L, TimeUnit.SECONDS);
    }

    @Warmup(iterations = 20)
    @Measurement(iterations = 10)
    @Benchmark
    public Set<Integer> serial_lazy_jdk()
    {
        Set<Integer> set = this.integersJDK.stream()
                .filter(each -> each % 10_000 != 0)
                .map(String::valueOf)
                .map(Integer::valueOf)
                .filter(each -> (each + 1) % 10_000 != 0)
                .collect(Collectors.toSet());
        Verify.assertSize(999_800, set);
        return set;
    }

    @Test
    public void test_serial_lazy_jdk()
    {
        Verify.assertSetsEqual(
                Interval.oneToBy(1_000_000, 10_000).flatCollect(each -> Interval.fromTo(each, each + 9_997)).toSet(),
                this.serial_lazy_jdk());
    }

    @Warmup(iterations = 50)
    @Measurement(iterations = 25)
    @Benchmark
    public Set<Integer> parallel_lazy_jdk()
    {
        Set<Integer> set = this.integersJDK.parallelStream()
                .filter(each -> each % 10_000 != 0)
                .map(String::valueOf)
                .map(Integer::valueOf)
                .filter(each -> (each + 1) % 10_000 != 0)
                .collect(Collectors.toSet());
        Verify.assertSize(999_800, set);
        return set;
    }

    @Warmup(iterations = 50)
    @Measurement(iterations = 25)
    @Test
    public void test_parallel_lazy_jdk()
    {
        Verify.assertSetsEqual(
                Interval.oneToBy(1_000_000, 10_000).flatCollect(each -> Interval.fromTo(each, each + 9_997)).toSet(),
                this.parallel_lazy_jdk());
    }

    @Warmup(iterations = 20)
    @Measurement(iterations = 10)
    @Benchmark
    public MutableSet<Integer> serial_eager_ec()
    {
        FastList<Integer> select1 = this.integersEC.select(each -> each % 10_000 != 0);
        FastList<String> collect1 = select1.collect(String::valueOf);
        FastList<Integer> collect2 = collect1.collect(Integer::valueOf);
        UnifiedSet<Integer> set = collect2.select(each -> (each + 1) % 10_000 != 0, UnifiedSet.newSet());
        Verify.assertSize(999_800, set);
        return set;
    }

    @Test
    public void test_serial_eager_ec()
    {
        Verify.assertSetsEqual(
                Interval.oneToBy(1_000_000, 10_000).flatCollect(each -> Interval.fromTo(each, each + 9_997)).toSet(),
                this.serial_eager_ec());
    }

    @Warmup(iterations = 20)
    @Measurement(iterations = 10)
    @Benchmark
    public MutableSet<Integer> serial_lazy_ec()
    {
        MutableSet<Integer> set = this.integersEC
                .asLazy()
                .select(each -> each % 10_000 != 0)
                .collect(String::valueOf)
                .collect(Integer::valueOf)
                .select(each -> (each + 1) % 10_000 != 0)
                .toSet();
        Verify.assertSize(999_800, set);
        return set;
    }

    @Test
    public void test_serial_lazy_ec()
    {
        Verify.assertSetsEqual(
                Interval.oneToBy(1_000_000, 10_000).flatCollect(each -> Interval.fromTo(each, each + 9_997)).toSet(),
                this.serial_lazy_ec());
    }

    @Warmup(iterations = 50)
    @Measurement(iterations = 25)
    @Benchmark
    public MutableSet<Integer> parallel_lazy_ec()
    {
        MutableSet<Integer> set = this.integersEC
                .asParallel(this.executorService, BATCH_SIZE)
                .select(each -> each % 10_000 != 0)
                .collect(String::valueOf)
                .collect(Integer::valueOf)
                .select(each -> (each + 1) % 10_000 != 0)
                .toSet();
        Verify.assertSize(999_800, set);
        return set;
    }

    @Test
    public void test_parallel_lazy_ec()
    {
        Verify.assertSetsEqual(
                Interval.oneToBy(1_000_000, 10_000).flatCollect(each -> Interval.fromTo(each, each + 9_997)).toSet(),
                this.parallel_lazy_ec());
    }
}
