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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Multimaps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
public class GroupByListTest
{
    private static final int SIZE = 1_000_000;
    private final List<Integer> integersJDK = new ArrayList<>(Interval.oneTo(SIZE));
    private final MutableList<Integer> integersEC = Interval.oneTo(SIZE).toList();

    @Benchmark
    public void groupBy_2_keys_serial_lazy_jdk()
    {
        Verify.assertSize(2, this.integersJDK.stream().collect(Collectors.groupingBy(each -> each % 2 == 0)));
    }

    @Benchmark
    public void groupBy_2_keys_serial_lazy_streams_ec()
    {
        Verify.assertSize(2, this.integersEC.stream().collect(Collectors.groupingBy(each -> each % 2 == 0)));
    }

    @Benchmark
    public void groupBy_100_keys_serial_lazy_jdk()
    {
        Verify.assertSize(100, this.integersJDK.stream().collect(Collectors.groupingBy(each -> each % 100)));
    }

    @Benchmark
    public void groupBy_100_keys_serial_lazy_streams_ec()
    {
        Verify.assertSize(100, this.integersEC.stream().collect(Collectors.groupingBy(each -> each % 100)));
    }

    @Benchmark
    public void groupBy_10000_keys_serial_lazy_jdk()
    {
        Verify.assertSize(10_000, this.integersJDK.stream().collect(Collectors.groupingBy(each -> each % 10_000)));
    }

    @Benchmark
    public void groupBy_10000_keys_serial_lazy_streams_ec()
    {
        Verify.assertSize(10_000, this.integersEC.stream().collect(Collectors.groupingBy(each -> each % 10_000)));
    }

    @Benchmark
    public void groupBy_2_keys_serial_eager_guava()
    {
        Verify.assertSize(2, Multimaps.index(this.integersJDK, each -> each % 2 == 0).asMap());
    }

    @Benchmark
    public void groupBy_100_keys_serial_eager_guava()
    {
        Verify.assertSize(100, Multimaps.index(this.integersJDK, each -> each % 100).asMap());
    }

    @Benchmark
    public void groupBy_10000_keys_serial_eager_guava()
    {
        Verify.assertSize(10_000, Multimaps.index(this.integersJDK, each -> each % 10000).asMap());
    }

    @Benchmark
    public void groupBy_2_keys_serial_eager_ec()
    {
        Assert.assertEquals(2, this.integersEC.groupBy(each -> each % 2 == 0).sizeDistinct());
    }

    @Benchmark
    public void groupBy_100_keys_serial_eager_ec()
    {
        Assert.assertEquals(100, this.integersEC.groupBy(each -> each % 100).sizeDistinct());
    }

    @Benchmark
    public void groupBy_10000_keys_serial_eager_ec()
    {
        Assert.assertEquals(10_000, this.integersEC.groupBy(each -> each % 10_000).sizeDistinct());
    }

    @Benchmark
    public void groupBy_2_keys_serial_lazy_ec()
    {
        Assert.assertEquals(2, this.integersEC.asLazy().groupBy(each -> each % 2 == 0).sizeDistinct());
    }

    @Benchmark
    public void groupBy_100_keys_serial_lazy_ec()
    {
        Assert.assertEquals(100, this.integersEC.asLazy().groupBy(each -> each % 100).sizeDistinct());
    }

    @Benchmark
    public void groupBy_10000_keys_serial_lazy_ec()
    {
        Assert.assertEquals(10_000, this.integersEC.asLazy().groupBy(each -> each % 10_000).sizeDistinct());
    }

    @Benchmark
    public void groupBy_2_keys_serial_eager_scala()
    {
        GroupByScalaTest.groupBy_2_keys_serial_eager_scala();
    }

    @Benchmark
    public void groupBy_100_keys_serial_eager_scala()
    {
        GroupByScalaTest.groupBy_100_keys_serial_eager_scala();
    }

    @Benchmark
    public void groupBy_10000_keys_serial_eager_scala()
    {
        GroupByScalaTest.groupBy_10000_keys_serial_eager_scala();
    }

    @Benchmark
    public void groupBy_2_keys_serial_lazy_scala()
    {
        GroupByScalaTest.groupBy_2_keys_serial_lazy_scala();
    }

    @Benchmark
    public void groupBy_100_keys_serial_lazy_scala()
    {
        GroupByScalaTest.groupBy_100_keys_serial_lazy_scala();
    }

    @Benchmark
    public void groupBy_10000_keys_serial_lazy_scala()
    {
        GroupByScalaTest.groupBy_10000_keys_serial_lazy_scala();
    }
}
