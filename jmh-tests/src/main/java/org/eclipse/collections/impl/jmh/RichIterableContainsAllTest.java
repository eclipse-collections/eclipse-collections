/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh;

import java.util.concurrent.TimeUnit;

import org.eclipse.collections.api.set.MutableSet;
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
public class RichIterableContainsAllTest
{
    private static final int SIZE = 100_000;
    private static final int SOURCE_SIZE = 4_096;

    private final FastList<Integer> fullList = this.newRange(0, SIZE);
    private final FastList<Integer> almostFullList = this.newRange(0, SIZE - 1);
    private final FastList<Integer> presentTail = this.newRange(SIZE - SOURCE_SIZE, SIZE);
    private final FastList<Integer> missingTail = this.newRange(SIZE - SOURCE_SIZE, SIZE + 1);
    private final FastList<Integer> tinySource = FastList.newListWith(1, 2, 3);
    private final MutableSet<Integer> fullSet = this.fullList.toSet();

    @Benchmark
    public boolean present_tail_non_set()
    {
        return this.fullList.containsAll(this.presentTail);
    }

    @Benchmark
    public boolean missing_tail_non_set()
    {
        return this.almostFullList.containsAll(this.missingTail);
    }

    @Benchmark
    public boolean present_tail_set()
    {
        return this.fullSet.containsAll(this.presentTail);
    }

    @Benchmark
    public boolean tiny_source_non_set()
    {
        return this.fullList.containsAll(this.tinySource);
    }

    private FastList<Integer> newRange(int startInclusive, int endExclusive)
    {
        FastList<Integer> result = FastList.newList(endExclusive - startInclusive);
        for (int i = startInclusive; i < endExclusive; i++)
        {
            result.add(i);
        }
        return result;
    }
}
