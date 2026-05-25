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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.impl.list.Interval;
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
public class PartitionTest
{
    private static final int SIZE = 1_000_000;
    private final List<Integer> integersJDK = new ArrayList<>(Interval.oneTo(SIZE));
    private final MutableList<Integer> integersEC = Interval.oneTo(SIZE).toList();

    @Benchmark
    public Map<Boolean, List<Integer>> serial_lazy_jdk()
    {
        return this.integersJDK.stream().collect(Collectors.partitioningBy(each -> each % 2 == 0));
    }

    @Benchmark
    public Map<Boolean, List<Integer>> serial_lazy_streams_ec()
    {
        return this.integersEC.stream().collect(Collectors.partitioningBy(each -> each % 2 == 0));
    }

    @Benchmark
    public PartitionMutableList<Integer> serial_eager_ec()
    {
        return this.integersEC.partition(each -> each % 2 == 0);
    }

    @Benchmark
    public PartitionIterable<Integer> serial_lazy_ec()
    {
        return this.integersEC.asLazy().partition(each -> each % 2 == 0);
    }
}
