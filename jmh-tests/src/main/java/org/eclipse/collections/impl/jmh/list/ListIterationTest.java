/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh.list;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.Interval;
import org.junit.After;
import org.junit.Before;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
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
public class ListIterationTest
{
    private static final int SIZE = 1_000_000;
    private static final int BATCH_SIZE = 10_000;

    private final MutableList<Integer> ecMutable = Lists.mutable.withAll(Interval.zeroTo(SIZE));
    private final ImmutableList<Integer> ecImmutable = Lists.immutable.withAll(Interval.zeroTo(SIZE));

    private ExecutorService executorService;

    @Before
    @Setup
    public void setUp()
    {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @After
    @TearDown
    public void tearDown() throws InterruptedException
    {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(1L, TimeUnit.SECONDS);
    }

    @Benchmark
    public void serial_mutable_ec()
    {
        int count = this.ecMutable
                .asLazy()
                .select(each -> each % 10_000 != 0)
                .collect(String::valueOf)
                .collect(Integer::valueOf)
                .count(each -> (each + 1) % 10_000 != 0);
        if (count != 999_800)
        {
            throw new AssertionError();
        }
    }

    @Benchmark
    public void serial_immutable_ec()
    {
        int count = this.ecImmutable
                .asLazy()
                .select(each -> each % 10_000 != 0)
                .collect(String::valueOf)
                .collect(Integer::valueOf)
                .count(each -> (each + 1) % 10_000 != 0);
        if (count != 999_800)
        {
            throw new AssertionError();
        }
    }

    @Benchmark
    public void parallel_mutable_ec()
    {
        int count = this.ecMutable
                .asParallel(this.executorService, BATCH_SIZE)
                .select(each -> each % 10_000 != 0)
                .collect(String::valueOf)
                .collect(Integer::valueOf)
                .count(each -> (each + 1) % 10_000 != 0);
        if (count != 999_800)
        {
            throw new AssertionError();
        }
    }

    @Benchmark
    public void parallel_immutable_ec()
    {
        int count = this.ecImmutable
                .asParallel(this.executorService, BATCH_SIZE)
                .select(each -> each % 10_000 != 0)
                .collect(String::valueOf)
                .collect(Integer::valueOf)
                .count(each -> (each + 1) % 10_000 != 0);
        if (count != 999_800)
        {
            throw new AssertionError();
        }
    }

    @Benchmark
    public void serial_mutable_scala()
    {
        ScalaListIterationTest.serial_mutable_scala();
    }

    @Benchmark
    public void parallel_mutable_scala()
    {
        ScalaListIterationTest.parallel_mutable_scala();
    }
}
