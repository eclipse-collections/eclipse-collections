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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.set.UnsortedSetMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.UnsortedSetIterable;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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
public class GroupBySetTest
{
    private static final int SIZE = 1_000_000;
    private static final int BATCH_SIZE = 10_000;
    private final Set<Integer> integersJDK = new HashSet<>(Interval.zeroTo(SIZE - 1));
    private final UnifiedSet<Integer> integersEC = new UnifiedSet<>(Interval.zeroTo(SIZE - 1));

    private ExecutorService executorService;

    @Before
    @Setup
    public void setUp()
    {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @After
    @TearDown
    public void tearDown() throws InterruptedException
    {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(1L, TimeUnit.SECONDS);
    }

    @Benchmark
    public Map<Boolean, Set<Integer>> groupBy_2_keys_serial_lazy_jdk()
    {
        Map<Boolean, Set<Integer>> multimap = this.integersJDK.stream()
                .collect(Collectors.groupingBy(each -> each % 2 == 0, Collectors.toSet()));
        Verify.assertSize(2, multimap);
        return multimap;
    }

    @Benchmark
    public Map<Boolean, Set<Integer>> groupBy_2_keys_serial_lazy_streams_ec()
    {
        Map<Boolean, Set<Integer>> multimap = this.integersEC.stream()
                .collect(Collectors.groupingBy(each -> each % 2 == 0, Collectors.toSet()));
        Verify.assertSize(2, multimap);
        return multimap;
    }

    @Test
    public void test_groupBy_2_keys_serial_lazy_jdk()
    {
        Map<Boolean, Set<Integer>> multimap = this.groupBy_2_keys_serial_lazy_jdk();
        Set<Integer> odds = multimap.get(false);
        Set<Integer> evens = multimap.get(true);
        Verify.assertSetsEqual(Interval.fromToBy(0, 999_999, 2).toSet(), evens);
        Verify.assertSetsEqual(Interval.fromToBy(1, 999_999, 2).toSet(), odds);
    }

    @Test
    public void test_groupBy_2_keys_serial_lazy_streams_ec()
    {
        Map<Boolean, Set<Integer>> multimap = this.groupBy_2_keys_serial_lazy_streams_ec();
        Set<Integer> odds = multimap.get(false);
        Set<Integer> evens = multimap.get(true);
        Verify.assertSetsEqual(Interval.fromToBy(0, 999_999, 2).toSet(), evens);
        Verify.assertSetsEqual(Interval.fromToBy(1, 999_999, 2).toSet(), odds);
    }

    @Benchmark
    public Map<Integer, Set<Integer>> groupBy_100_keys_serial_lazy_jdk()
    {
        Map<Integer, Set<Integer>> multimap = this.integersJDK.stream().collect(Collectors.groupingBy(each -> each % 100, Collectors.toSet()));
        Verify.assertSize(100, multimap);
        return multimap;
    }

    @Benchmark
    public Map<Integer, Set<Integer>> groupBy_100_keys_serial_lazy_streams_ec()
    {
        Map<Integer, Set<Integer>> multimap = this.integersEC.stream().collect(Collectors.groupingBy(each -> each % 100, Collectors.toSet()));
        Verify.assertSize(100, multimap);
        return multimap;
    }

    @Test
    public void test_groupBy_100_keys_serial_lazy_jdk()
    {
        Map<Integer, Set<Integer>> multimap = this.groupBy_100_keys_serial_lazy_jdk();
        for (int i = 0; i < 100; i++)
        {
            Set<Integer> integers = multimap.get(i);
            Verify.assertSize(10_000, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 100).toSet(), integers);
        }
    }

    @Test
    public void test_groupBy_100_keys_serial_lazy_streams_ec()
    {
        Map<Integer, Set<Integer>> multimap = this.groupBy_100_keys_serial_lazy_streams_ec();
        for (int i = 0; i < 100; i++)
        {
            Set<Integer> integers = multimap.get(i);
            Verify.assertSize(10_000, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 100).toSet(), integers);
        }
    }

    @Benchmark
    public Map<Integer, Set<Integer>> groupBy_10000_keys_serial_lazy_jdk()
    {
        Map<Integer, Set<Integer>> multimap = this.integersJDK.stream().collect(Collectors.groupingBy(each -> each % 10_000, Collectors.toSet()));
        Verify.assertSize(10_000, multimap);
        return multimap;
    }

    @Benchmark
    public Map<Integer, Set<Integer>> groupBy_10000_keys_serial_lazy_streams_ec()
    {
        Map<Integer, Set<Integer>> multimap = this.integersEC.stream().collect(Collectors.groupingBy(each -> each % 10_000, Collectors.toSet()));
        Verify.assertSize(10_000, multimap);
        return multimap;
    }

    @Test
    public void test_groupBy_10000_keys_serial_lazy_jdk()
    {
        Map<Integer, Set<Integer>> multimap = this.groupBy_10000_keys_serial_lazy_jdk();
        for (int i = 0; i < 10_000; i++)
        {
            Set<Integer> integers = multimap.get(i);
            Verify.assertSize(100, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 10_000).toSet(), integers);
        }
    }

    @Test
    public void test_groupBy_10000_keys_serial_lazy_streams_ec()
    {
        Map<Integer, Set<Integer>> multimap = this.groupBy_10000_keys_serial_lazy_streams_ec();
        for (int i = 0; i < 10_000; i++)
        {
            Set<Integer> integers = multimap.get(i);
            Verify.assertSize(100, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 10_000).toSet(), integers);
        }
    }

    @Benchmark
    public Map<Boolean, Set<Integer>> groupBy_2_keys_parallel_lazy_jdk()
    {
        Map<Boolean, Set<Integer>> multimap = this.integersJDK.parallelStream().collect(Collectors.groupingBy(each -> each % 2 == 0, Collectors.toSet()));
        Verify.assertSize(2, multimap);
        return multimap;
    }

    @Benchmark
    public Map<Boolean, Set<Integer>> groupBy_2_keys_parallel_lazy_streams_ec()
    {
        Map<Boolean, Set<Integer>> multimap = this.integersEC.parallelStream().collect(Collectors.groupingBy(each -> each % 2 == 0, Collectors.toSet()));
        Verify.assertSize(2, multimap);
        return multimap;
    }

    @Test
    public void test_groupBy_2_keys_parallel_lazy_jdk()
    {
        Map<Boolean, Set<Integer>> multimap = this.groupBy_2_keys_parallel_lazy_jdk();
        Set<Integer> odds = multimap.get(false);
        Set<Integer> evens = multimap.get(true);
        Verify.assertSetsEqual(Interval.fromToBy(0, 999_999, 2).toSet(), evens);
        Verify.assertSetsEqual(Interval.fromToBy(1, 999_999, 2).toSet(), odds);
    }

    @Test
    public void test_groupBy_2_keys_parallel_lazy_streams_ec()
    {
        Map<Boolean, Set<Integer>> multimap = this.groupBy_2_keys_parallel_lazy_streams_ec();
        Set<Integer> odds = multimap.get(false);
        Set<Integer> evens = multimap.get(true);
        Verify.assertSetsEqual(Interval.fromToBy(0, 999_999, 2).toSet(), evens);
        Verify.assertSetsEqual(Interval.fromToBy(1, 999_999, 2).toSet(), odds);
    }

    @Benchmark
    public Map<Integer, Set<Integer>> groupBy_100_keys_parallel_lazy_jdk()
    {
        Map<Integer, Set<Integer>> multimap = this.integersJDK.parallelStream().collect(Collectors.groupingBy(each -> each % 100, Collectors.toSet()));
        Verify.assertSize(100, multimap);
        return multimap;
    }

    @Benchmark
    public Map<Integer, Set<Integer>> groupBy_100_keys_parallel_lazy_streams_ec()
    {
        Map<Integer, Set<Integer>> multimap = this.integersEC.parallelStream().collect(Collectors.groupingBy(each -> each % 100, Collectors.toSet()));
        Verify.assertSize(100, multimap);
        return multimap;
    }

    @Test
    public void test_groupBy_100_keys_parallel_lazy_jdk()
    {
        Map<Integer, Set<Integer>> multimap = this.groupBy_100_keys_parallel_lazy_jdk();
        for (int i = 0; i < 100; i++)
        {
            Set<Integer> integers = multimap.get(i);
            Verify.assertSize(10_000, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 100).toSet(), integers);
        }
    }

    @Test
    public void test_groupBy_100_keys_parallel_lazy_streams_ec()
    {
        Map<Integer, Set<Integer>> multimap = this.groupBy_100_keys_parallel_lazy_streams_ec();
        for (int i = 0; i < 100; i++)
        {
            Set<Integer> integers = multimap.get(i);
            Verify.assertSize(10_000, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 100).toSet(), integers);
        }
    }

    @Benchmark
    public Map<Integer, Set<Integer>> groupBy_10000_keys_parallel_lazy_jdk()
    {
        Map<Integer, Set<Integer>> multimap = this.integersJDK.parallelStream().collect(Collectors.groupingBy(each -> each % 10_000, Collectors.toSet()));
        Verify.assertSize(10_000, multimap);
        return multimap;
    }

    @Benchmark
    public Map<Integer, Set<Integer>> groupBy_10000_keys_parallel_lazy_streams_ec()
    {
        Map<Integer, Set<Integer>> multimap = this.integersEC.parallelStream().collect(Collectors.groupingBy(each -> each % 10_000, Collectors.toSet()));
        Verify.assertSize(10_000, multimap);
        return multimap;
    }

    @Test
    public void test_groupBy_10000_keys_parallel_lazy_jdk()
    {
        Map<Integer, Set<Integer>> multimap = this.groupBy_10000_keys_parallel_lazy_jdk();
        for (int i = 0; i < 10_000; i++)
        {
            Set<Integer> integers = multimap.get(i);
            Verify.assertSize(100, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 10_000).toSet(), integers);
        }
    }

    @Test
    public void test_groupBy_10000_keys_parallel_lazy_streams_ec()
    {
        Map<Integer, Set<Integer>> multimap = this.groupBy_10000_keys_parallel_lazy_streams_ec();
        for (int i = 0; i < 10_000; i++)
        {
            Set<Integer> integers = multimap.get(i);
            Verify.assertSize(100, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 10_000).toSet(), integers);
        }
    }

    @Benchmark
    public ImmutableListMultimap<Boolean, Integer> groupBy_unordered_lists_2_keys_serial_eager_guava()
    {
        ImmutableListMultimap<Boolean, Integer> multimap = Multimaps.index(this.integersJDK, each -> each % 2 == 0);
        Verify.assertSize(2, multimap.asMap());
        return multimap;
    }

    @Ignore("Why is Guava reordering values?")
    @Test
    public void test_groupBy_unordered_lists_2_keys_serial_eager_guava()
    {
        ImmutableListMultimap<Boolean, Integer> multimap = this.groupBy_unordered_lists_2_keys_serial_eager_guava();
        ImmutableList<Integer> odds = multimap.get(false);
        ImmutableList<Integer> evens = multimap.get(true);
        Verify.assertListsEqual(Interval.fromToBy(0, 999_999, 2), evens);
        Verify.assertListsEqual(Interval.fromToBy(1, 999_999, 2), odds);
    }

    @Benchmark
    public ImmutableListMultimap<Integer, Integer> groupBy_unordered_lists_100_keys_serial_eager_guava()
    {
        ImmutableListMultimap<Integer, Integer> multimap = Multimaps.index(this.integersJDK, each -> each % 100);
        Verify.assertSize(100, multimap.asMap());
        return multimap;
    }

    @Test
    public void test_groupBy_unordered_lists_100_keys_serial_eager_guava()
    {
        ImmutableListMultimap<Integer, Integer> multimap = this.groupBy_unordered_lists_100_keys_serial_eager_guava();
        for (int i = 0; i < 100; i++)
        {
            ImmutableList<Integer> integers = multimap.get(i);
            Verify.assertSize(10_000, integers);
            Assert.assertEquals(Interval.fromToBy(i, 999_999, 100), integers);
        }
    }

    @Benchmark
    public ImmutableListMultimap<Integer, Integer> groupBy_unordered_lists_10000_keys_serial_eager_guava()
    {
        ImmutableListMultimap<Integer, Integer> multimap = Multimaps.index(this.integersJDK, each -> each % 10000);
        Verify.assertSize(10_000, multimap.asMap());
        return multimap;
    }

    @Test
    public void test_groupBy_unordered_lists_10000_keys_serial_eager_guava()
    {
        ImmutableListMultimap<Integer, Integer> multimap = this.groupBy_unordered_lists_10000_keys_serial_eager_guava();
        for (int i = 0; i < 10_000; i++)
        {
            ImmutableList<Integer> integers = multimap.get(i);
            Verify.assertSize(100, integers);
            Assert.assertEquals(Interval.fromToBy(i, 999_999, 10_000), integers);
        }
    }

    @Benchmark
    public UnifiedSetMultimap<Boolean, Integer> groupBy_2_keys_serial_eager_ec()
    {
        UnifiedSetMultimap<Boolean, Integer> multimap = this.integersEC.groupBy(each -> each % 2 == 0);
        Assert.assertEquals(2, multimap.sizeDistinct());
        return multimap;
    }

    @Test
    public void test_groupBy_2_keys_serial_eager_ec()
    {
        UnifiedSetMultimap<Boolean, Integer> multimap = this.groupBy_2_keys_serial_eager_ec();
        Set<Integer> odds = multimap.get(false);
        Set<Integer> evens = multimap.get(true);
        Verify.assertSetsEqual(Interval.fromToBy(0, 999_999, 2).toSet(), evens);
        Verify.assertSetsEqual(Interval.fromToBy(1, 999_999, 2).toSet(), odds);
    }

    @Benchmark
    public UnifiedSetMultimap<Integer, Integer> groupBy_100_keys_serial_eager_ec()
    {
        UnifiedSetMultimap<Integer, Integer> multimap = this.integersEC.groupBy(each -> each % 100);
        Assert.assertEquals(100, multimap.sizeDistinct());
        return multimap;
    }

    @Test
    public void test_groupBy_100_keys_serial_eager_ec()
    {
        UnifiedSetMultimap<Integer, Integer> multimap = this.groupBy_100_keys_serial_eager_ec();
        for (int i = 0; i < 100; i++)
        {
            MutableSet<Integer> integers = multimap.get(i);
            Verify.assertSize(10_000, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 100).toSet(), integers);
        }
    }

    @Benchmark
    public UnifiedSetMultimap<Integer, Integer> groupBy_10000_keys_serial_eager_ec()
    {
        UnifiedSetMultimap<Integer, Integer> multimap = this.integersEC.groupBy(each -> each % 10_000);
        Assert.assertEquals(10_000, multimap.sizeDistinct());
        return multimap;
    }

    @Test
    public void test_groupBy_10000_keys_serial_eager_ec()
    {
        UnifiedSetMultimap<Integer, Integer> multimap = this.groupBy_10000_keys_serial_eager_ec();
        for (int i = 0; i < 10_000; i++)
        {
            Set<Integer> integers = multimap.get(i);
            Verify.assertSize(100, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 10_000).toSet(), integers);
        }
    }

    @Benchmark
    public Multimap<Boolean, Integer> groupBy_unordered_lists_2_keys_serial_lazy_ec()
    {
        Multimap<Boolean, Integer> multimap = this.integersEC.asLazy().groupBy(each -> each % 2 == 0);
        Assert.assertEquals(2, multimap.sizeDistinct());
        return multimap;
    }

    @Test
    public void test_groupBy_unordered_lists_2_keys_serial_lazy_ec()
    {
        Multimap<Boolean, Integer> multimap = this.groupBy_unordered_lists_2_keys_serial_lazy_ec();
        RichIterable<Integer> odds = multimap.get(false);
        RichIterable<Integer> evens = multimap.get(true);
        Verify.assertSetsEqual(Interval.fromToBy(0, 999_999, 2).toSet(), evens.toSet());
        Verify.assertSetsEqual(Interval.fromToBy(1, 999_999, 2).toSet(), odds.toSet());
    }

    @Benchmark
    public Multimap<Integer, Integer> groupBy_unordered_lists_100_keys_serial_lazy_ec()
    {
        Multimap<Integer, Integer> multimap = this.integersEC.asLazy().groupBy(each -> each % 100);
        Assert.assertEquals(100, multimap.sizeDistinct());
        return multimap;
    }

    @Test
    public void test_groupBy_unordered_lists_100_keys_serial_lazy_ec()
    {
        Multimap<Integer, Integer> multimap = this.groupBy_unordered_lists_100_keys_serial_lazy_ec();
        for (int i = 0; i < 100; i++)
        {
            RichIterable<Integer> integers = multimap.get(i);
            Verify.assertIterableSize(10_000, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 100).toSet(), integers.toSet());
        }
    }

    @Benchmark
    public Multimap<Integer, Integer> groupBy_unordered_lists_10000_keys_serial_lazy_ec()
    {
        Multimap<Integer, Integer> multimap = this.integersEC.asLazy().groupBy(each -> each % 10_000);
        Assert.assertEquals(10_000, multimap.sizeDistinct());
        return multimap;
    }

    @Test
    public void test_groupBy_unordered_lists_10000_keys_serial_lazy_ec()
    {
        Multimap<Integer, Integer> multimap = this.groupBy_unordered_lists_10000_keys_serial_lazy_ec();
        for (int i = 0; i < 10_000; i++)
        {
            RichIterable<Integer> integers = multimap.get(i);
            Verify.assertIterableSize(100, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 10_000).toSet(), integers.toSet());
        }
    }

    @Benchmark
    public UnsortedSetMultimap<Boolean, Integer> groupBy_2_keys_parallel_lazy_ec()
    {
        UnsortedSetMultimap<Boolean, Integer> multimap = this.integersEC.asParallel(this.executorService, BATCH_SIZE).groupBy(each -> each % 2 == 0);
        Assert.assertEquals(2, multimap.sizeDistinct());
        return multimap;
    }

    @Test
    public void test_groupBy_2_keys_parallel_lazy_ec()
    {
        UnsortedSetMultimap<Boolean, Integer> multimap = this.groupBy_2_keys_parallel_lazy_ec();
        UnsortedSetIterable<Integer> odds = multimap.get(false);
        UnsortedSetIterable<Integer> evens = multimap.get(true);
        Verify.assertSetsEqual(Interval.fromToBy(0, 999_999, 2).toSet(), (Set<?>) evens);
        Verify.assertSetsEqual(Interval.fromToBy(1, 999_999, 2).toSet(), (Set<?>) odds);
    }

    @Benchmark
    public UnsortedSetMultimap<Integer, Integer> groupBy_100_keys_parallel_lazy_ec()
    {
        UnsortedSetMultimap<Integer, Integer> multimap = this.integersEC.asParallel(this.executorService, BATCH_SIZE).groupBy(each -> each % 100);
        Assert.assertEquals(100, multimap.sizeDistinct());
        return multimap;
    }

    @Test
    public void test_groupBy_100_keys_parallel_lazy_ec()
    {
        UnsortedSetMultimap<Integer, Integer> multimap = this.groupBy_100_keys_parallel_lazy_ec();
        for (int i = 0; i < 100; i++)
        {
            UnsortedSetIterable<Integer> integers = multimap.get(i);
            Verify.assertIterableSize(10_000, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 100).toSet(), (Set<?>) integers);
        }
    }

    @Benchmark
    public UnsortedSetMultimap<Integer, Integer> groupBy_10000_keys_parallel_lazy_ec()
    {
        UnsortedSetMultimap<Integer, Integer> multimap = this.integersEC.asParallel(this.executorService, BATCH_SIZE).groupBy(each -> each % 10_000);
        Assert.assertEquals(10_000, multimap.sizeDistinct());
        return multimap;
    }

    @Test
    public void test_groupBy_10000_keys_parallel_lazy_ec()
    {
        UnsortedSetMultimap<Integer, Integer> multimap = this.groupBy_10000_keys_parallel_lazy_ec();
        for (int i = 0; i < 10_000; i++)
        {
            UnsortedSetIterable<Integer> integers = multimap.get(i);
            Verify.assertIterableSize(100, integers);
            Verify.assertSetsEqual(Interval.fromToBy(i, 999_999, 10_000).toSet(), (Set<?>) integers);
        }
    }

    @Benchmark
    public void groupBy_2_keys_serial_eager_scala()
    {
        GroupBySetScalaTest.groupBy_2_keys_serial_eager_scala();
    }

    @Test
    public void test_groupBy_2_keys_serial_eager_scala()
    {
        GroupBySetScalaTest.test_groupBy_2_keys_serial_eager_scala();
    }

    @Benchmark
    public void groupBy_100_keys_serial_eager_scala()
    {
        GroupBySetScalaTest.groupBy_100_keys_serial_eager_scala();
    }

    @Test
    public void test_groupBy_100_keys_serial_eager_scala()
    {
        GroupBySetScalaTest.test_groupBy_100_keys_serial_eager_scala();
    }

    @Benchmark
    public void groupBy_10000_keys_serial_eager_scala()
    {
        GroupBySetScalaTest.groupBy_10000_keys_serial_eager_scala();
    }

    @Test
    public void test_groupBy_10000_keys_serial_eager_scala()
    {
        GroupBySetScalaTest.test_groupBy_10000_keys_serial_eager_scala();
    }

    @Benchmark
    public void groupBy_2_keys_serial_lazy_scala()
    {
        GroupBySetScalaTest.groupBy_unordered_lists_2_keys_serial_lazy_scala();
    }

    @Test
    public void test_groupBy_unordered_lists_2_keys_serial_lazy_scala()
    {
        GroupBySetScalaTest.test_groupBy_unordered_lists_2_keys_serial_lazy_scala();
    }

    @Benchmark
    public void groupBy_100_keys_serial_lazy_scala()
    {
        GroupBySetScalaTest.groupBy_unordered_lists_100_keys_serial_lazy_scala();
    }

    @Test
    public void test_groupBy_unordered_lists_100_keys_serial_lazy_scala()
    {
        GroupBySetScalaTest.test_groupBy_unordered_lists_100_keys_serial_lazy_scala();
    }

    @Benchmark
    public void groupBy_10000_keys_serial_lazy_scala()
    {
        GroupBySetScalaTest.groupBy_unordered_lists_10000_keys_serial_lazy_scala();
    }

    @Test
    public void test_groupBy_unordered_lists_10000_keys_serial_lazy_scala()
    {
        GroupBySetScalaTest.test_groupBy_unordered_lists_10000_keys_serial_lazy_scala();
    }

    @Benchmark
    public void groupBy_2_keys_parallel_lazy_scala()
    {
        GroupBySetScalaTest.groupBy_2_keys_parallel_lazy_scala();
    }

    @Test
    public void test_groupBy_2_keys_parallel_lazy_scala()
    {
        GroupBySetScalaTest.test_groupBy_2_keys_parallel_lazy_scala();
    }

    @Benchmark
    public void groupBy_100_keys_parallel_lazy_scala()
    {
        GroupBySetScalaTest.groupBy_100_keys_parallel_lazy_scala();
    }

    @Test
    public void test_groupBy_100_keys_parallel_lazy_scala()
    {
        GroupBySetScalaTest.test_groupBy_100_keys_parallel_lazy_scala();
    }

    @Benchmark
    public void groupBy_10000_keys_parallel_lazy_scala()
    {
        GroupBySetScalaTest.groupBy_10000_keys_parallel_lazy_scala();
    }

    @Test
    public void test_groupBy_10000_keys_parallel_lazy_scala()
    {
        GroupBySetScalaTest.test_groupBy_10000_keys_parallel_lazy_scala();
    }
}
