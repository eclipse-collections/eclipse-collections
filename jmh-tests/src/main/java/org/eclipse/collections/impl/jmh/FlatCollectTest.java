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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
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
public class FlatCollectTest
{
    private static final int COUNT = 10_000;
    private static final int LIST_SIZE = 100;
    private final List<List<Integer>> integersJDK = new ArrayList<>(FastList.<List<Integer>>newWithNValues(COUNT, () -> new ArrayList<>(Interval.oneTo(LIST_SIZE))));
    private final MutableList<MutableList<Integer>> integersEC = FastList.newWithNValues(COUNT, () -> Interval.oneTo(LIST_SIZE).toList());

    @Benchmark
    public List<Integer> serial_lazy_jdk()
    {
        return this.integersJDK.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> serial_lazy_streams_ec()
    {
        return this.integersEC.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Benchmark
    public MutableList<Integer> serial_eager_ec()
    {
        return this.integersEC.flatCollect(e -> e);
    }

    @Benchmark
    public MutableList<Integer> serial_lazy_ec()
    {
        return this.integersEC.asLazy().flatCollect(e -> e).toList();
    }
}
