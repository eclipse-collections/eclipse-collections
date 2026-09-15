/*
 * Copyright (c) 2023 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import org.eclipse.collections.impl.list.primitive.LongInterval;
import org.eclipse.collections.impl.utility.ArrayListIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.ListIterate;
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
@Warmup(iterations = 10, time = 3)
@Measurement(iterations = 10, time = 3)
public class JDKListIterationTest
{
    private static final int SIZE = 100_000;

    private final List<Long> longs = LongInterval.zeroTo(SIZE).collect(Long::valueOf).castToList();
    private final ArrayList<Long> arrayList = new ArrayList<>(this.longs);
    private final List<Long> synchArrayList = Collections.synchronizedList(new ArrayList<>(this.longs));
    private final CopyOnWriteArrayList<Long> cowaList = new CopyOnWriteArrayList<>(this.longs);

    @Before
    @Setup
    public void setUp()
    {
    }

    @After
    @TearDown
    public void tearDown() throws InterruptedException
    {
    }

    @Benchmark
    public long arrayList_iterate_forEach()
    {
        LongAdder adder = new LongAdder();
        Iterate.forEach(this.arrayList, adder::add);
        return adder.longValue();
    }

    @Benchmark
    public long arrayList_arrayListIterate_forEach()
    {
        LongAdder adder = new LongAdder();
        ArrayListIterate.forEach(this.arrayList, adder::add);
        return adder.longValue();
    }

    @Benchmark
    public long cowal_iterate_forEach()
    {
        LongAdder adder = new LongAdder();
        Iterate.forEach(this.cowaList, adder::add);
        return adder.longValue();
    }

    @Benchmark
    public long cowal_listIterate_forEach()
    {
        LongAdder adder = new LongAdder();
        ListIterate.forEach(this.cowaList, adder::add);
        return adder.longValue();
    }

    @Benchmark
    public long synchArrayList_iterate_forEach()
    {
        LongAdder adder = new LongAdder();
        Iterate.forEach(this.synchArrayList, adder::add);
        return adder.longValue();
    }

    @Benchmark
    public long synchArrayList_listIterate_forEach()
    {
        LongAdder adder = new LongAdder();
        ListIterate.forEach(this.synchArrayList, adder::add);
        return adder.longValue();
    }
}
