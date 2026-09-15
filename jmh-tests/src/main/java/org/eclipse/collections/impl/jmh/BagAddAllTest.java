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

import java.util.concurrent.TimeUnit;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.impl.bag.mutable.HashBag;
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
public class BagAddAllTest
{
    private static final int SIZE = 1000;
    private final Multiset<Integer> integersGuava = HashMultiset.create(Interval.oneTo(SIZE));
    private final MutableBag<Integer> integersEC = Interval.oneTo(SIZE).toBag();

    @Benchmark
    public void guava()
    {
        Multiset<Integer> result = HashMultiset.create();
        for (int i = 0; i < 1000; i++)
        {
            result.addAll(this.integersGuava);
        }
    }

    @Benchmark
    public void ec()
    {
        MutableBag<Integer> result = HashBag.newBag();
        for (int i = 0; i < 1000; i++)
        {
            result.addAll(this.integersEC);
        }
    }
}
