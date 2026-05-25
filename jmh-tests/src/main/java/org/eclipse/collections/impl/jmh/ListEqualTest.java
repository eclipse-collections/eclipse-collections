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

import org.eclipse.collections.api.list.MutableList;
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
public class ListEqualTest
{
    private static final int SIZE = 1_000_000;
    private final List<Integer> integersJDK1 = new ArrayList<>(Interval.oneTo(SIZE));
    private final List<Integer> integersJDK2 = new ArrayList<>(Interval.oneTo(SIZE));
    private final List<Integer> integersJDK3 = new ArrayList<>(Interval.oneTo(SIZE / 2));
    private final MutableList<Integer> integersEC1 = Interval.oneTo(SIZE).toList();
    private final MutableList<Integer> integersEC2 = Interval.oneTo(SIZE).toList();
    private final MutableList<Integer> integersEC3 = Interval.oneTo(SIZE / 2).toList();

    @Benchmark
    public void jdk()
    {
        if (!this.integersJDK1.equals(this.integersJDK1))
        {
            throw new AssertionError();
        }
        if (!this.integersJDK1.equals(this.integersJDK2))
        {
            throw new AssertionError();
        }
        if (this.integersJDK1.equals(this.integersJDK3))
        {
            throw new AssertionError();
        }
        if (!this.integersJDK1.equals(this.integersEC1))
        {
            throw new AssertionError();
        }
        if (this.integersJDK1.equals(this.integersEC3))
        {
            throw new AssertionError();
        }
    }

    @Benchmark
    public void ec()
    {
        if (!this.integersEC1.equals(this.integersEC1))
        {
            throw new AssertionError();
        }
        if (!this.integersEC1.equals(this.integersEC2))
        {
            throw new AssertionError();
        }
        if (this.integersEC1.equals(this.integersEC3))
        {
            throw new AssertionError();
        }
        if (!this.integersEC1.equals(this.integersJDK1))
        {
            throw new AssertionError();
        }
        if (this.integersEC1.equals(this.integersJDK3))
        {
            throw new AssertionError();
        }
    }
}
