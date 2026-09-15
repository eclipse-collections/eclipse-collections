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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.CompositeFastList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.eclipse.collections.impl.test.Verify;
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
public class CollectTest
{
    private static final int SIZE = 1_000_000;
    private static final int BATCH_SIZE = 10_000;
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

    @Benchmark
    public void serial_lazy_jdk()
    {
        List<String> strings = this.integersJDK.stream().map(Object::toString).collect(Collectors.toList());
        Verify.assertSize(SIZE, strings);
    }

    @Benchmark
    public void serial_lazy_streams_ec()
    {
        List<String> strings = this.integersEC.stream().map(Object::toString).collect(Collectors.toList());
        Verify.assertSize(SIZE, strings);
    }

    @Benchmark
    public void parallel_lazy_jdk()
    {
        List<String> strings = this.integersJDK.parallelStream().map(Object::toString).collect(Collectors.toList());
        Verify.assertSize(SIZE, strings);
    }

    @Benchmark
    public void parallel_lazy_streams_ec()
    {
        List<String> strings = this.integersEC.parallelStream().map(Object::toString).collect(Collectors.toList());
        Verify.assertSize(SIZE, strings);
    }

    @Benchmark
    public void serial_eager_scala()
    {
        CollectScalaTest.serial_eager_scala();
    }

    @Benchmark
    public void serial_lazy_scala()
    {
        CollectScalaTest.serial_lazy_scala();
    }

    @Benchmark
    public void parallel_lazy_scala()
    {
        CollectScalaTest.parallel_lazy_scala();
    }

    @Benchmark
    public void serial_eager_ec()
    {
        MutableList<String> strings = this.integersEC.collect(Object::toString);
        Verify.assertSize(SIZE, strings);
    }

    @Benchmark
    public void parallel_eager_ec()
    {
        Collection<String> strings = ParallelIterate.collect(this.integersEC, Object::toString);
        Verify.assertSize(SIZE, strings);
    }

    @Benchmark
    public void parallel_eager_fixed_pool_ec()
    {
        Collection<String> strings = ParallelIterate.collect(
                this.integersEC,
                Object::toString,
                new CompositeFastList<>(),
                BATCH_SIZE,
                this.executorService,
                false);
        Verify.assertSize(SIZE, strings);
    }

    @Benchmark
    public void serial_lazy_ec()
    {
        MutableList<String> strings = this.integersEC.asLazy().collect(Object::toString).toList();
        Verify.assertSize(SIZE, strings);
    }

    @Benchmark
    public void parallel_lazy_ec()
    {
        MutableList<String> strings = this.integersEC.asParallel(this.executorService, BATCH_SIZE).collect(Object::toString).toList();
        Verify.assertSize(SIZE, strings);
    }
}
