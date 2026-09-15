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

import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.FastList;
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
public class SummarizeIntTest
{
    private static final int SIZE = 3_000_000;
    private static final int BATCH_SIZE = 10_000;
    private static final Stream<Integer> INTEGERS = new Random().ints(0, 10_000).boxed();

    private final List<Integer> integersJDK = INTEGERS.limit(SIZE).collect(Collectors.toList());
    private final MutableList<Integer> integersEC = FastList.newListWith(this.integersJDK.toArray(new Integer[SIZE]));
    private final IntList intList = this.integersEC.collectInt(Integer::intValue);

    private ExecutorService executorService;

    @Setup
    public void setUp()
    {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Collections.shuffle(this.integersJDK);
        Collections.shuffle(this.integersEC);
    }

    @TearDown
    public void tearDown() throws InterruptedException
    {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(1L, TimeUnit.SECONDS);
    }

    @Benchmark
    public IntSummaryStatistics serial_lazy_mapToIntSum_jdk()
    {
        return this.integersJDK.stream().mapToInt(Integer::intValue).summaryStatistics();
    }

    @Benchmark
    public IntSummaryStatistics serial_lazy_mapToIntSum_streams_ec()
    {
        return this.integersEC.stream().mapToInt(Integer::intValue).summaryStatistics();
    }

    @Benchmark
    public IntSummaryStatistics parallel_lazy_mapToIntSum_jdk()
    {
        return this.integersJDK.parallelStream().mapToInt(Integer::intValue).summaryStatistics();
    }

    @Benchmark
    public IntSummaryStatistics parallel_lazy_mapToIntSum_streams_ec()
    {
        return this.integersEC.parallelStream().mapToInt(Integer::intValue).summaryStatistics();
    }

    @Benchmark
    public IntSummaryStatistics serial_eager_summarizeInt_ec()
    {
        return this.integersEC.summarizeInt(Integer::intValue);
    }

    @Benchmark
    public IntSummaryStatistics serial_eager_collectIntSummaryStatistics_ec()
    {
        return this.integersEC.collectInt(Integer::intValue).summaryStatistics();
    }

    @Benchmark
    public IntSummaryStatistics serial_lazy_collectIntSummaryStatistics_ec()
    {
        return this.integersEC.asLazy().collectInt(Integer::intValue).summaryStatistics();
    }

    @Benchmark
    public IntSummaryStatistics serial_lazy_summarizeInt_ec()
    {
        return this.integersEC.asLazy().summarizeInt(Integer::intValue);
    }

    @Benchmark
    public IntSummaryStatistics serial_eager_summaryStatistics_intList()
    {
        return this.intList.summaryStatistics();
    }
}
