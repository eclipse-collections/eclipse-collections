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

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.impl.factory.Bags;
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
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
public class BagEqualsTest
{
    private final MutableBag<String> bag = Bags.mutable.withOccurrences("1", 1, "2", 2, "3", 3, "4", 4);
    private final MutableBag<String> equalBag = Bags.mutable.withOccurrences("1", 1, "2", 2, "3", 3, "4", 4);
    private final MutableBag<String> unequalBag1 = Bags.mutable.withOccurrences("1", 4, "2", 3, "3", 2, "4", 1);
    private final MutableBag<String> unequalBag2 = Bags.mutable.withOccurrences("1", 1, "2", 2, "3", 3, "4", 1);
    private final Multiset<String> multiset = HashMultiset.create(this.bag);
    private final Multiset<String> equalMultiset = HashMultiset.create(this.bag);
    private final Multiset<String> unequalMultiset1 = HashMultiset.create(this.unequalBag1);
    private final Multiset<String> unequalMultiset2 = HashMultiset.create(this.unequalBag2);
    private final Map<String, Long> map =
            this.bag.stream().collect(Collectors.groupingBy(each -> each, Collectors.counting()));
    private final Map<String, Long> equalMap =
            this.bag.stream().collect(Collectors.groupingBy(each -> each, Collectors.counting()));
    private final Map<String, Long> unequalMap1 =
            this.unequalBag1.stream().collect(Collectors.groupingBy(each -> each, Collectors.counting()));
    private final Map<String, Long> unequalMap2 =
            this.unequalBag2.stream().collect(Collectors.groupingBy(each -> each, Collectors.counting()));

    @Benchmark
    public boolean[] multisetEquals()
    {
        boolean equals = this.multiset.equals(this.equalMultiset);
        boolean unequals1 = this.multiset.equals(this.unequalMultiset1);
        boolean unequals2 = this.multiset.equals(this.unequalMultiset2);
        return new boolean[]{equals, unequals1, unequals2};
    }

    @Benchmark
    public boolean[] bagEquals()
    {
        boolean equals = this.bag.equals(this.equalBag);
        boolean unequals1 = this.bag.equals(this.unequalBag1);
        boolean unequals2 = this.bag.equals(this.unequalBag2);
        return new boolean[]{equals, unequals1, unequals2};
    }

    @Benchmark
    public boolean[] mapEquals()
    {
        boolean equals = this.map.equals(this.equalMap);
        boolean unequals1 = this.map.equals(this.unequalMap1);
        boolean unequals2 = this.map.equals(this.unequalMap2);
        return new boolean[]{equals, unequals1, unequals2};
    }
}
