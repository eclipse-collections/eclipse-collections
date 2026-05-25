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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ParallelUnsortedBag;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.UnsortedBagMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.forkjoin.FJIterate;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.junit.Assert;
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
public class AnagramBagTest
{
    private static final int SIZE = 1_000_000;
    private static final int BATCH_SIZE = 10_000;

    private static final int SIZE_THRESHOLD = 10;
    private final HashBag<String> ecWords = HashBag.newBag(FastList.newWithNValues(SIZE, () -> RandomStringUtils.randomAlphabetic(5).toUpperCase()));
    private final Multiset<String> guavaWords = HashMultiset.create(this.ecWords);

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
    public void serial_eager_ec()
    {
        MutableListMultimap<Alphagram, String> groupBy = this.ecWords.groupBy(Alphagram::new, FastListMultimap.newMultimap());
        groupBy.multiValuesView()
                .select(iterable -> iterable.size() >= SIZE_THRESHOLD)
                .toSortedList(Comparators.byIntFunction(RichIterable::size))
                .asReversed()
                .collect(iterable -> iterable.size() + ": " + iterable)
                .forEach(Procedures.cast(e -> Assert.assertFalse(e.isEmpty())));
    }

    @Benchmark
    public void parallel_eager_ec()
    {
        MutableMultimap<Alphagram, String> groupBy = ParallelIterate.groupBy(this.ecWords, Alphagram::new);
        groupBy.multiValuesView()
                .select(iterable -> iterable.size() >= SIZE_THRESHOLD)
                .toSortedList(Comparators.byIntFunction(RichIterable::size))
                .asReversed()
                .collect(iterable -> iterable.size() + ": " + iterable)
                .forEach(Procedures.cast(e -> Assert.assertFalse(e.isEmpty())));
    }

    @Benchmark
    public void parallel_lazy_ec()
    {
        ParallelUnsortedBag<String> parallelUnsortedBag = this.ecWords.asParallel(this.executorService, BATCH_SIZE);
        UnsortedBagMultimap<Alphagram, String> groupBy = parallelUnsortedBag.groupBy(Alphagram::new);
        groupBy.multiValuesView()
                .select(iterable -> iterable.size() >= SIZE_THRESHOLD)
                .toSortedList(Comparators.byIntFunction(RichIterable::size))
                .asReversed()
                .collect(iterable -> iterable.size() + ": " + iterable)
                .forEach(Procedures.cast(e -> Assert.assertFalse(e.isEmpty())));
    }

    @Benchmark
    public void parallel_eager_forkjoin_ec()
    {
        MutableMultimap<Alphagram, String> groupBy = FJIterate.groupBy(this.ecWords, Alphagram::new);
        groupBy.multiValuesView()
                .select(iterable -> iterable.size() >= SIZE_THRESHOLD)
                .toSortedList(Comparators.byIntFunction(RichIterable::size))
                .asReversed()
                .collect(iterable -> iterable.size() + ": " + iterable)
                .forEach(Procedures.cast(e -> Assert.assertFalse(e.isEmpty())));
    }

    @Benchmark
    public void serial_lazy_jdk()
    {
        Map<Alphagram, List<String>> groupBy = this.guavaWords.stream().collect(Collectors.groupingBy(Alphagram::new));
        groupBy.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(list -> list.size() >= SIZE_THRESHOLD)
                .sorted(Comparator.<List<String>>comparingInt(List::size).reversed())
                .map(list -> list.size() + ": " + list)
                .forEach(e -> Assert.assertFalse(e.isEmpty()));
    }

    @Benchmark
    public void serial_lazy_streams_ec()
    {
        Map<Alphagram, List<String>> groupBy = this.ecWords.stream().collect(Collectors.groupingBy(Alphagram::new));
        groupBy.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(list -> list.size() >= SIZE_THRESHOLD)
                .sorted(Comparator.<List<String>>comparingInt(List::size).reversed())
                .map(list -> list.size() + ": " + list)
                .forEach(e -> Assert.assertFalse(e.isEmpty()));
    }

    @Benchmark
    public void parallel_lazy_jdk()
    {
        Map<Alphagram, List<String>> groupBy = this.guavaWords.parallelStream().collect(Collectors.groupingBy(Alphagram::new));
        groupBy.entrySet()
                .parallelStream()
                .map(Map.Entry::getValue)
                .filter(list -> list.size() >= SIZE_THRESHOLD)
                .sorted(Comparator.<List<String>>comparingInt(List::size).reversed())
                .map(list -> list.size() + ": " + list)
                .forEach(e -> Assert.assertFalse(e.isEmpty()));
    }

    @Benchmark
    public void parallel_lazy_streams_ec()
    {
        Map<Alphagram, List<String>> groupBy = this.ecWords.parallelStream().collect(Collectors.groupingBy(Alphagram::new));
        groupBy.entrySet()
                .parallelStream()
                .map(Map.Entry::getValue)
                .filter(list -> list.size() >= SIZE_THRESHOLD)
                .sorted(Comparator.<List<String>>comparingInt(List::size).reversed())
                .map(list -> list.size() + ": " + list)
                .forEach(e -> Assert.assertFalse(e.isEmpty()));
    }

    private static final class Alphagram
    {
        private final char[] key;

        private Alphagram(String string)
        {
            this.key = string.toCharArray();
            Arrays.sort(this.key);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || this.getClass() != o.getClass())
            {
                return false;
            }
            Alphagram alphagram = (Alphagram) o;
            return Arrays.equals(this.key, alphagram.key);
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(this.key);
        }

        @Override
        public String toString()
        {
            return new String(this.key);
        }
    }
}
