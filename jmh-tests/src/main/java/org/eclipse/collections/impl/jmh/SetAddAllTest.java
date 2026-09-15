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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
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
public class SetAddAllTest
{
    private static final int SIZE = 1000;
    private final Set<Integer> integersJDK = new HashSet<>(Interval.oneTo(SIZE));
    private final MutableSet<Integer> integersEC = Interval.oneTo(SIZE).toSet();

    @Benchmark
    public void jdk()
    {
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < 1000; i++)
        {
            result.addAll(this.integersJDK);
        }
    }

    @Benchmark
    public void ec()
    {
        MutableSet<Integer> result = UnifiedSet.newSet();
        for (int i = 0; i < 1000; i++)
        {
            result.addAll(this.integersEC);
        }
    }
}
