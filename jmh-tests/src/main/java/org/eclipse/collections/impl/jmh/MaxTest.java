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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.Interval;
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
public class MaxTest
{
    private static final int SIZE = 3_000_000;
    private static final int BATCH_SIZE = 10_000;

    private final List<Integer> integersJDK = new ArrayList<>(Interval.oneTo(SIZE));
    private final MutableList<Integer> integersEC = Interval.oneTo(SIZE).toList();

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

    @Benchmark
    public int serial_lazy_jdk()
    {
        return this.integersJDK.stream().max(Comparator.naturalOrder()).get();
    }

    @Benchmark
    public int serial_lazy_reverse_jdk()
    {
        return this.integersJDK.stream().max(Comparator.reverseOrder()).get();
    }

    @Benchmark
    public int serial_lazy_reverse_streams_ec()
    {
        return this.integersEC.stream().max(Comparator.reverseOrder()).get();
    }

    @Benchmark
    public int serial_lazy_intstream_jdk()
    {
        return this.integersJDK.stream().mapToInt(Integer::intValue).max().getAsInt();
    }

    @Benchmark
    public int serial_lazy_intstream_streams_ec()
    {
        return this.integersEC.stream().mapToInt(Integer::intValue).max().getAsInt();
    }

    @Benchmark
    public int parallel_lazy_jdk()
    {
        return this.integersJDK.parallelStream().max(Comparator.naturalOrder()).get();
    }

    @Benchmark
    public int parallel_lazy_streams_ec()
    {
        return this.integersEC.parallelStream().max(Comparator.naturalOrder()).get();
    }

    @Benchmark
    public int parallel_lazy_reverse_jdk()
    {
        return this.integersJDK.parallelStream().max(Comparator.reverseOrder()).get();
    }

    @Benchmark
    public int parallel_lazy_reverse_streams_ec()
    {
        return this.integersEC.parallelStream().max(Comparator.reverseOrder()).get();
    }

    @Benchmark
    public int parallel_lazy_intstream_jdk()
    {
        return this.integersJDK.parallelStream().mapToInt(Integer::intValue).max().getAsInt();
    }

    @Benchmark
    public int parallel_lazy_intstream_streams_ec()
    {
        return this.integersEC.parallelStream().mapToInt(Integer::intValue).max().getAsInt();
    }

    @Benchmark
    public int serial_eager_ec()
    {
        return this.integersEC.max(Comparator.naturalOrder());
    }

    @Benchmark
    public int serial_eager_reverse_ec()
    {
        return this.integersEC.max(Comparator.reverseOrder());
    }

    @Benchmark
    public int serial_lazy_ec()
    {
        return this.integersEC.asLazy().max(Comparator.naturalOrder());
    }

    @Benchmark
    public int serial_lazy_reverse_ec()
    {
        return this.integersEC.asLazy().max(Comparator.reverseOrder());
    }

    @Benchmark
    public int parallel_lazy_ec()
    {
        return this.integersEC.asParallel(this.executorService, BATCH_SIZE).max(Comparator.naturalOrder());
    }

    @Benchmark
    public int parallel_lazy_reverse_ec()
    {
        return this.integersEC.asParallel(this.executorService, BATCH_SIZE).max(Comparator.reverseOrder());
    }
}
