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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.procedure.CountProcedure;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.junit.Assert;
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

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
public class CountTest
{
    private static final int SIZE = 1_000_000;
    private static final int BATCH_SIZE = 10_000;

    @Param({"0", "1", "2", "3"})
    public int megamorphicWarmupLevel;

    private final List<Integer> integersJDK = new ArrayList<>(Interval.oneTo(SIZE));
    private final FastList<Integer> integersEC = new FastList<>(Interval.oneTo(SIZE));

    private ExecutorService executorService;

    @Setup
    public void setUp()
    {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @TearDown
    public void tearDown() throws InterruptedException
    {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(1L, TimeUnit.SECONDS);
    }

    @Setup(Level.Trial)
    public void setUp_megamorphic()
    {
        if (this.megamorphicWarmupLevel > 0)
        {
            // serial, lazy, JDK
            {
                long evens = this.integersJDK.stream().filter(each -> each % 2 == 0).count();
                Assert.assertEquals(SIZE / 2, evens);
                long odds = this.integersJDK.stream().filter(each -> each % 2 == 1).count();
                Assert.assertEquals(SIZE / 2, odds);
                long evens2 = this.integersJDK.stream().filter(each -> (each & 1) == 0).count();
                Assert.assertEquals(SIZE / 2, evens2);
            }

            // parallel, lazy, JDK
            {
                long evens = this.integersJDK.parallelStream().filter(each -> each % 2 == 0).count();
                Assert.assertEquals(SIZE / 2, evens);
                long odds = this.integersJDK.parallelStream().filter(each -> each % 2 == 1).count();
                Assert.assertEquals(SIZE / 2, odds);
                long evens2 = this.integersJDK.parallelStream().filter(each -> (each & 1) == 0).count();
                Assert.assertEquals(SIZE / 2, evens2);
            }

            // serial, lazy, EC
            {
                long evens = this.integersEC.asLazy().count(each -> each % 2 == 0);
                Assert.assertEquals(SIZE / 2, evens);
                long odds = this.integersEC.asLazy().count(each -> each % 2 == 1);
                Assert.assertEquals(SIZE / 2, odds);
                long evens2 = this.integersEC.asLazy().count(each -> (each & 1) == 0);
                Assert.assertEquals(SIZE / 2, evens2);
            }

            // parallel, lazy, EC
            {
                long evens = this.integersEC.asParallel(this.executorService, BATCH_SIZE).count(each -> each % 2 == 0);
                Assert.assertEquals(SIZE / 2, evens);
                long odds = this.integersEC.asParallel(this.executorService, BATCH_SIZE).count(each -> each % 2 == 1);
                Assert.assertEquals(SIZE / 2, odds);
                long evens2 = this.integersEC.asParallel(this.executorService, BATCH_SIZE).count(each -> (each & 1) == 0);
                Assert.assertEquals(SIZE / 2, evens2);
            }

            // serial, eager, EC
            {
                long evens = this.integersEC.count(each -> each % 2 == 0);
                Assert.assertEquals(SIZE / 2, evens);
                long odds = this.integersEC.count(each -> each % 2 == 1);
                Assert.assertEquals(SIZE / 2, odds);
                long evens2 = this.integersEC.count(each -> (each & 1) == 0);
                Assert.assertEquals(SIZE / 2, evens2);
            }

            // parallel, eager, EC
            long evens = ParallelIterate.count(this.integersEC, each -> each % 2 == 0);
            Assert.assertEquals(SIZE / 2, evens);
            long odds = ParallelIterate.count(this.integersEC, each -> each % 2 == 1);
            Assert.assertEquals(SIZE / 2, odds);
            long evens2 = ParallelIterate.count(this.integersEC, each -> (each & 1) == 0);
            Assert.assertEquals(SIZE / 2, evens2);
        }

        if (this.megamorphicWarmupLevel > 1)
        {
            // stream().mapToLong().reduce()
            Assert.assertEquals(
                    500001500000L,
                    this.integersJDK.stream().mapToLong(each -> each + 1).reduce(0, (accum, each) -> accum + each));

            Assert.assertEquals(
                    500002500000L,
                    this.integersJDK.stream().mapToLong(each -> each + 2).reduce(0, (accum, each) -> {
                        Assert.assertTrue(each >= 0);
                        return accum + each;
                    }));

            Assert.assertEquals(
                    500003500000L,
                    this.integersJDK.stream().mapToLong(each -> each + 3).reduce(0, (accum, each) -> {
                        Assert.assertTrue(each >= 0);
                        return accum + each;
                    }));

            // parallelStream().mapToLong().reduce()
            Assert.assertEquals(
                    500001500000L,
                    this.integersJDK.parallelStream().mapToLong(each -> each + 1).reduce(0, (accum, each) -> accum + each));

            Assert.assertEquals(
                    500002500000L,
                    this.integersJDK.parallelStream().mapToLong(each -> each + 2).reduce(0, (accum, each) -> {
                        Assert.assertTrue(each >= 0);
                        return accum + each;
                    }));

            Assert.assertEquals(
                    500003500000L,
                    this.integersJDK.parallelStream().mapToLong(each -> each + 3).reduce(0, (accum, each) -> {
                        Assert.assertTrue(each >= 0);
                        return accum + each;
                    }));
        }

        if (this.megamorphicWarmupLevel > 2)
        {
            this.integersEC.asLazy().forEach(Procedures.cast(Assert::assertNotNull));
            this.integersEC.asLazy().forEach(Procedures.cast(each -> Assert.assertEquals(each, each)));
            this.integersEC.asLazy().forEach(new CountProcedure<>());

            this.integersEC.asParallel(this.executorService, BATCH_SIZE).forEach(Assert::assertNotNull);
            this.integersEC.asParallel(this.executorService, BATCH_SIZE).forEach(each -> Assert.assertEquals(each, each));
            this.integersEC.asParallel(this.executorService, BATCH_SIZE).forEach(new CountProcedure<>());

            this.integersJDK.stream().forEach(Assert::assertNotNull);
            this.integersJDK.stream().forEach(each -> Assert.assertEquals(each, each));

            this.integersJDK.parallelStream().forEach(Assert::assertNotNull);
            this.integersJDK.parallelStream().forEach(each -> Assert.assertEquals(each, each));
        }

        CountScalaTest.megamorphic(this.megamorphicWarmupLevel);
    }

    @Benchmark
    public void serial_lazy_jdk()
    {
        long evens = this.integersJDK.stream().filter(each -> each % 2 == 0).count();
        Assert.assertEquals(SIZE / 2, evens);
    }

    @Benchmark
    public void serial_lazy_streams_ec()
    {
        long evens = this.integersEC.stream().filter(each -> each % 2 == 0).count();
        Assert.assertEquals(SIZE / 2, evens);
    }

    @Benchmark
    public void parallel_lazy_jdk()
    {
        long evens = this.integersJDK.parallelStream().filter(each -> each % 2 == 0).count();
        Assert.assertEquals(SIZE / 2, evens);
    }

    @Benchmark
    public void parallel_lazy_streams_ec()
    {
        long evens = this.integersEC.parallelStream().filter(each -> each % 2 == 0).count();
        Assert.assertEquals(SIZE / 2, evens);
    }

    @Benchmark
    public void serial_eager_ec()
    {
        int evens = this.integersEC.count(each -> each % 2 == 0);
        Assert.assertEquals(SIZE / 2, evens);
    }

    @Benchmark
    public void serial_lazy_ec()
    {
        int evens = this.integersEC.asLazy().count(each -> each % 2 == 0);
        Assert.assertEquals(SIZE / 2, evens);
    }

    @Benchmark
    public void parallel_eager_ec()
    {
        int evens = ParallelIterate.count(this.integersEC, each -> each % 2 == 0, BATCH_SIZE, this.executorService);
        Assert.assertEquals(SIZE / 2, evens);
    }

    @Benchmark
    public void parallel_lazy_ec()
    {
        int evens = this.integersEC.asParallel(this.executorService, BATCH_SIZE).count(each -> each % 2 == 0);
        Assert.assertEquals(SIZE / 2, evens);
    }

    @Benchmark
    public void serial_eager_scala()
    {
        CountScalaTest.serial_eager_scala();
    }

    @Benchmark
    public void serial_lazy_scala()
    {
        CountScalaTest.serial_lazy_scala();
    }

    @Benchmark
    public void parallel_lazy_scala()
    {
        CountScalaTest.parallel_lazy_scala();
    }
}
