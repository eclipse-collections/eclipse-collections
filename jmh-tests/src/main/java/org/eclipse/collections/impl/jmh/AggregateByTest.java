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
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.Pool;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
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
public class AggregateByTest
{
    private static final int SIZE = 1_000_000;
    private static final int BATCH_SIZE = 10_000;
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final PrimitiveIterator.OfInt INTS = RANDOM.ints(1, 10).iterator();
    private static final PrimitiveIterator.OfDouble DOUBLES = RANDOM.doubles(1.0d, 100.0d).iterator();
    private final Pool<Account> accountPool = UnifiedSet.newSet();
    private final Pool<Product> productPool = UnifiedSet.newSet();
    private final Pool<String> categoryPool = UnifiedSet.newSet();
    private final FastList<Position> ecPositions = FastList.newWithNValues(SIZE, Position::new);
    private final ArrayList<Position> jdkPositions = new ArrayList<>(this.ecPositions);

    private ExecutorService executorService;

    @Before
    @Setup(Level.Iteration)
    public void setUp()
    {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.ecPositions.shuffleThis();
        Collections.shuffle(this.jdkPositions);
    }

    @After
    @TearDown(Level.Iteration)
    public void tearDown() throws InterruptedException
    {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(1L, TimeUnit.SECONDS);
    }

    @Benchmark
    public Map<Product, DoubleSummaryStatistics> aggregateByProduct_serial_lazy_jdk()
    {
        Map<Product, DoubleSummaryStatistics> result =
                this.jdkPositions.stream().collect(
                        Collectors.groupingBy(
                                Position::getProduct,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public Map<Product, DoubleSummaryStatistics> aggregateByProduct_serial_lazy_streams_ec()
    {
        Map<Product, DoubleSummaryStatistics> result =
                this.ecPositions.stream().collect(
                        Collectors.groupingBy(
                                Position::getProduct,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public Map<Account, DoubleSummaryStatistics> aggregateByAccount_serial_lazy_jdk()
    {
        Map<Account, DoubleSummaryStatistics> accountDoubleMap =
                this.jdkPositions.stream().collect(
                        Collectors.groupingBy(
                                Position::getAccount,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(accountDoubleMap);
        return accountDoubleMap;
    }

    @Benchmark
    public Map<Account, DoubleSummaryStatistics> aggregateByAccount_serial_lazy_streams_ec()
    {
        Map<Account, DoubleSummaryStatistics> accountDoubleMap =
                this.ecPositions.stream().collect(
                        Collectors.groupingBy(
                                Position::getAccount,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(accountDoubleMap);
        return accountDoubleMap;
    }

    @Benchmark
    public Map<String, DoubleSummaryStatistics> aggregateByCategory_serial_lazy_jdk()
    {
        Map<String, DoubleSummaryStatistics> categoryDoubleMap =
                this.jdkPositions.stream().collect(
                        Collectors.groupingBy(
                                Position::getCategory,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(categoryDoubleMap);
        return categoryDoubleMap;
    }

    @Benchmark
    public Map<String, DoubleSummaryStatistics> aggregateByCategory_serial_lazy_streams_ec()
    {
        Map<String, DoubleSummaryStatistics> categoryDoubleMap =
                this.ecPositions.stream().collect(
                        Collectors.groupingBy(
                                Position::getCategory,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(categoryDoubleMap);
        return categoryDoubleMap;
    }

    @Benchmark
    public Map<Product, DoubleSummaryStatistics> aggregateByProduct_parallel_lazy_jdk()
    {
        Map<Product, DoubleSummaryStatistics> result =
                this.jdkPositions.parallelStream().collect(
                        Collectors.groupingBy(
                                Position::getProduct,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public Map<Product, DoubleSummaryStatistics> aggregateByProduct_parallel_lazy_streams_ec()
    {
        Map<Product, DoubleSummaryStatistics> result =
                this.ecPositions.parallelStream().collect(
                        Collectors.groupingBy(
                                Position::getProduct,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public Map<Account, DoubleSummaryStatistics> aggregateByAccount_parallel_lazy_jdk()
    {
        Map<Account, DoubleSummaryStatistics> result =
                this.jdkPositions.parallelStream().collect(
                        Collectors.groupingBy(
                                Position::getAccount,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public Map<Account, DoubleSummaryStatistics> aggregateByAccount_parallel_lazy_streams_ec()
    {
        Map<Account, DoubleSummaryStatistics> result =
                this.ecPositions.parallelStream().collect(
                        Collectors.groupingBy(
                                Position::getAccount,
                                Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public Map<String, DoubleSummaryStatistics> aggregateByCategory_parallel_lazy_jdk()
    {
        Map<String, DoubleSummaryStatistics> result =
                this.jdkPositions.parallelStream().collect(
                        Collectors.groupingBy(Position::getCategory, Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public Map<String, DoubleSummaryStatistics> aggregateByCategory_parallel_lazy_streams_ec()
    {
        Map<String, DoubleSummaryStatistics> result =
                this.ecPositions.parallelStream().collect(
                        Collectors.groupingBy(Position::getCategory, Collectors.summarizingDouble(Position::getMarketValue)));
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<Product, ImmutableMarketValueStatistics> aggregateByProduct_serial_eager_ec()
    {
        MutableMap<Product, ImmutableMarketValueStatistics> result =
                this.ecPositions.aggregateBy(
                        Position::getProduct,
                        ImmutableMarketValueStatistics::new,
                        ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<Account, ImmutableMarketValueStatistics> aggregateByAccount_serial_eager_ec()
    {
        MutableMap<Account, ImmutableMarketValueStatistics> result =
                this.ecPositions.aggregateBy(
                        Position::getAccount,
                        ImmutableMarketValueStatistics::new,
                        ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<String, ImmutableMarketValueStatistics> aggregateByCategory_serial_eager_ec()
    {
        MutableMap<String, ImmutableMarketValueStatistics> result =
                this.ecPositions.aggregateBy(
                        Position::getCategory,
                        ImmutableMarketValueStatistics::new,
                        ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<Product, ImmutableMarketValueStatistics> aggregateByProduct_parallel_eager_ec()
    {
        MutableMap<Product, ImmutableMarketValueStatistics> result =
                ParallelIterate.aggregateBy(
                        this.ecPositions,
                        Position::getProduct,
                        ImmutableMarketValueStatistics::new,
                        ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<Account, ImmutableMarketValueStatistics> aggregateByAccount_parallel_eager_ec()
    {
        MutableMap<Account, ImmutableMarketValueStatistics> result =
                ParallelIterate.aggregateBy(
                        this.ecPositions,
                        Position::getAccount,
                        ImmutableMarketValueStatistics::new,
                        ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<String, ImmutableMarketValueStatistics> aggregateByCategory_parallel_eager_ec()
    {
        MutableMap<String, ImmutableMarketValueStatistics> result =
                ParallelIterate.aggregateBy(
                        this.ecPositions,
                        Position::getCategory,
                        ImmutableMarketValueStatistics::new,
                        ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MapIterable<Product, ImmutableMarketValueStatistics> aggregateByProduct_serial_lazy_ec()
    {
        MapIterable<Product, ImmutableMarketValueStatistics> result =
                this.ecPositions.asLazy().aggregateBy(
                        Position::getProduct,
                        ImmutableMarketValueStatistics::new,
                        ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MapIterable<Account, ImmutableMarketValueStatistics> aggregateByAccount_serial_lazy_ec()
    {
        MapIterable<Account, ImmutableMarketValueStatistics> result =
                this.ecPositions.asLazy().aggregateBy(
                        Position::getAccount,
                        ImmutableMarketValueStatistics::new,
                        ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MapIterable<String, ImmutableMarketValueStatistics> aggregateByCategory_serial_lazy_ec()
    {
        MapIterable<String, ImmutableMarketValueStatistics> result =
                this.ecPositions.asLazy().aggregateBy(
                        Position::getCategory,
                        ImmutableMarketValueStatistics::new,
                        ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MapIterable<Product, ImmutableMarketValueStatistics> aggregateByProduct_parallel_lazy_ec()
    {
        MapIterable<Product, ImmutableMarketValueStatistics> result =
                this.ecPositions.asParallel(this.executorService, BATCH_SIZE)
                        .aggregateBy(
                                Position::getProduct,
                                ImmutableMarketValueStatistics::getZero,
                                ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Test
    public void test_aggregateByProduct_parallel_lazy_ec()
    {
        MapIterable<Product, ImmutableMarketValueStatistics> actual = this.aggregateByProduct_parallel_lazy_ec();
        MapIterable<Product, ImmutableMarketValueStatistics> expected = this.aggregateByProduct_serial_lazy_ec();
        Assert.assertEquals(expected, expected);
        Verify.assertMapsEqual((Map<Product, ImmutableMarketValueStatistics>) expected, (Map<Product, ImmutableMarketValueStatistics>) actual);
    }

    @Benchmark
    public MapIterable<Account, ImmutableMarketValueStatistics> aggregateByAccount_parallel_lazy_ec()
    {
        MapIterable<Account, ImmutableMarketValueStatistics> result =
                this.ecPositions.asParallel(this.executorService, BATCH_SIZE)
                        .aggregateBy(
                                Position::getAccount,
                                ImmutableMarketValueStatistics::new,
                                ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Test
    public void test_aggregateByAccount_parallel_lazy_ec()
    {
        MapIterable<Account, ImmutableMarketValueStatistics> actual = this.aggregateByAccount_parallel_lazy_ec();
        MapIterable<Account, ImmutableMarketValueStatistics> expected = this.aggregateByAccount_serial_lazy_ec();
        Assert.assertEquals(expected, expected);
        Verify.assertMapsEqual((Map<Account, ImmutableMarketValueStatistics>) expected, (Map<Account, ImmutableMarketValueStatistics>) actual);
    }

    @Benchmark
    public MapIterable<String, ImmutableMarketValueStatistics> aggregateByCategory_parallel_lazy_ec()
    {
        MapIterable<String, ImmutableMarketValueStatistics> result =
                this.ecPositions.asParallel(this.executorService, BATCH_SIZE)
                        .aggregateBy(
                                Position::getCategory,
                                ImmutableMarketValueStatistics::new,
                                ImmutableMarketValueStatistics::add);
        Assert.assertNotNull(result);
        return result;
    }

    @Test
    public void test_aggregateByCategory_parallel_lazy_ec()
    {
        MapIterable<String, ImmutableMarketValueStatistics> actual = this.aggregateByCategory_parallel_lazy_ec();
        MapIterable<String, ImmutableMarketValueStatistics> expected = this.aggregateByCategory_serial_lazy_ec();
        Assert.assertEquals(expected, expected);
        Verify.assertMapsEqual((Map<String, ImmutableMarketValueStatistics>) expected, (Map<String, ImmutableMarketValueStatistics>) actual);
    }

    @Benchmark
    public MutableMap<Product, MarketValueStatistics> aggregateInPlaceByProduct_serial_eager_ec()
    {
        MutableMap<Product, MarketValueStatistics> result =
                this.ecPositions.aggregateInPlaceBy(
                        Position::getProduct,
                        MarketValueStatistics::new,
                        MarketValueStatistics::accept);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<Account, MarketValueStatistics> aggregateInPlaceByAccount_serial_eager_ec()
    {
        MutableMap<Account, MarketValueStatistics> result =
                this.ecPositions.aggregateInPlaceBy(
                        Position::getAccount,
                        MarketValueStatistics::new,
                        MarketValueStatistics::accept);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<String, MarketValueStatistics> aggregateInPlaceByCategory_serial_eager_ec()
    {
        MutableMap<String, MarketValueStatistics> result =
                this.ecPositions.aggregateInPlaceBy(
                        Position::getCategory,
                        MarketValueStatistics::new,
                        MarketValueStatistics::accept);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<Product, MarketValueStatistics> aggregateInPlaceByProduct_parallel_eager_ec()
    {
        MutableMap<Product, MarketValueStatistics> result =
                ParallelIterate.aggregateInPlaceBy(
                        this.ecPositions,
                        Position::getProduct,
                        MarketValueStatistics::new,
                        MarketValueStatistics::syncAccept);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<Account, MarketValueStatistics> aggregateInPlaceByAccount_parallel_eager_ec()
    {
        MutableMap<Account, MarketValueStatistics> result =
                ParallelIterate.aggregateInPlaceBy(
                        this.ecPositions,
                        Position::getAccount,
                        MarketValueStatistics::new,
                        MarketValueStatistics::syncAccept);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MutableMap<String, MarketValueStatistics> aggregateInPlaceByCategory_parallel_eager_ec()
    {
        MutableMap<String, MarketValueStatistics> result =
                ParallelIterate.aggregateInPlaceBy(
                        this.ecPositions,
                        Position::getCategory,
                        MarketValueStatistics::new,
                        MarketValueStatistics::syncAccept);
        Assert.assertNotNull(result);
        return result;
    }

    @Benchmark
    public MapIterable<Product, MarketValueStatistics> aggregateInPlaceByProduct_parallel_lazy_ec()
    {
        MapIterable<Product, MarketValueStatistics> result =
                this.ecPositions.asParallel(this.executorService, BATCH_SIZE)
                        .aggregateInPlaceBy(
                                Position::getProduct,
                                MarketValueStatistics::new,
                                MarketValueStatistics::syncAccept);
        Assert.assertNotNull(result);
        return result;
    }

    @Test
    public void test_aggregateInPlaceByProduct_parallel_lazy_ec()
    {
        MapIterable<Product, MarketValueStatistics> actual = this.aggregateInPlaceByProduct_parallel_lazy_ec();
        MapIterable<Product, MarketValueStatistics> expected = this.aggregateInPlaceByProduct_serial_eager_ec();
        Assert.assertEquals(expected, expected);
        Verify.assertMapsEqual((Map<Product, MarketValueStatistics>) expected, (Map<Product, MarketValueStatistics>) actual);
    }

    @Benchmark
    public MapIterable<Account, MarketValueStatistics> aggregateInPlaceByAccount_parallel_lazy_ec()
    {
        MapIterable<Account, MarketValueStatistics> result =
                this.ecPositions.asParallel(this.executorService, BATCH_SIZE)
                        .aggregateInPlaceBy(
                                Position::getAccount,
                                MarketValueStatistics::new,
                                MarketValueStatistics::syncAccept);
        Assert.assertNotNull(result);
        return result;
    }

    @Test
    public void test_aggregateInPlaceByAccount_parallel_lazy_ec()
    {
        MapIterable<Account, MarketValueStatistics> actual = this.aggregateInPlaceByAccount_parallel_lazy_ec();
        MapIterable<Account, MarketValueStatistics> expected = this.aggregateInPlaceByAccount_serial_eager_ec();
        Assert.assertEquals(expected, expected);
        Verify.assertMapsEqual((Map<Account, MarketValueStatistics>) expected, (Map<Account, MarketValueStatistics>) actual);
    }

    @Benchmark
    public MapIterable<String, MarketValueStatistics> aggregateInPlaceByCategory_parallel_lazy_ec()
    {
        MapIterable<String, MarketValueStatistics> result =
                this.ecPositions.asParallel(this.executorService, BATCH_SIZE)
                        .aggregateInPlaceBy(
                                Position::getCategory,
                                MarketValueStatistics::new,
                                MarketValueStatistics::syncAccept);
        Assert.assertNotNull(result);
        return result;
    }

    @Test
    public void test_aggregateInPlaceByCategory_parallel_lazy_ec()
    {
        MapIterable<String, MarketValueStatistics> actual = this.aggregateInPlaceByCategory_parallel_lazy_ec();
        MapIterable<String, MarketValueStatistics> expected = this.aggregateInPlaceByCategory_serial_eager_ec();
        Assert.assertEquals(expected, expected);
        Verify.assertMapsEqual((Map<String, MarketValueStatistics>) expected, (Map<String, MarketValueStatistics>) actual);
    }

    private static boolean isCloseTo(double a, double b, double delta)
    {
        return a - b < delta || b - a < delta;
    }

    private static final class ImmutableMarketValueStatistics
    {
        private static final ImmutableMarketValueStatistics ZERO = new ImmutableMarketValueStatistics();

        private final long count;
        private final double sum;
        private final double min;
        private final double max;

        private ImmutableMarketValueStatistics()
        {
            this(0, 0.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        }

        private ImmutableMarketValueStatistics(long count, double sum, double min, double max)
        {
            this.count = count;
            this.sum = sum;
            this.min = min;
            this.max = max;
        }

        public ImmutableMarketValueStatistics add(Position position)
        {
            double marketValue = position.getMarketValue();
            return new ImmutableMarketValueStatistics(
                    this.count + 1,
                    this.sum + marketValue,
                    Math.min(this.min, marketValue),
                    Math.max(this.max, marketValue));
        }

        public static ImmutableMarketValueStatistics getZero()
        {
            return ZERO;
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

            ImmutableMarketValueStatistics that = (ImmutableMarketValueStatistics) o;

            if (this.count != that.count)
            {
                return false;
            }
            if (Double.compare(that.max, this.max) != 0)
            {
                return false;
            }
            if (Double.compare(that.min, this.min) != 0)
            {
                return false;
            }
            return AggregateByTest.isCloseTo(that.sum, this.sum, 0.0001);
        }

        @Override
        public int hashCode()
        {
            int result = (int) (this.count ^ (this.count >>> 32));
            long temp = Double.doubleToLongBits(this.sum);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(this.min);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(this.max);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString()
        {
            return "ImmutableMarketValueStatistics{"
                    + "count=" + this.count
                    + ", sum=" + this.sum
                    + ", min=" + this.min
                    + ", max=" + this.max
                    + '}';
        }
    }

    private static final class MarketValueStatistics extends DoubleSummaryStatistics
    {
        public void accept(Position position)
        {
            this.accept(position.getMarketValue());
        }

        public synchronized void syncAccept(Position position)
        {
            this.accept(position);
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

            MarketValueStatistics that = (MarketValueStatistics) o;

            if (this.getCount() != that.getCount())
            {
                return false;
            }
            if (Double.compare(that.getMax(), this.getMax()) != 0)
            {
                return false;
            }
            if (Double.compare(that.getMin(), this.getMin()) != 0)
            {
                return false;
            }
            return AggregateByTest.isCloseTo(that.getSum(), this.getSum(), 0.01);
        }

        @Override
        public int hashCode()
        {
            int result = (int) (this.getCount() ^ (this.getCount() >>> 32));
            long temp = Double.doubleToLongBits(this.getSum());
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(this.getMin());
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(this.getMax());
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    private final class Position
    {
        private final Account account = AggregateByTest.this.accountPool.put(new Account());
        private final Product product = AggregateByTest.this.productPool.put(new Product());
        private final int quantity = INTS.nextInt();

        public Account getAccount()
        {
            return this.account;
        }

        public Product getProduct()
        {
            return this.product;
        }

        public String getCategory()
        {
            return this.product.getCategory();
        }

        public int getQuantity()
        {
            return this.quantity;
        }

        public double getMarketValue()
        {
            return this.quantity * this.product.getPrice();
        }
    }

    private static final class Account
    {
        private final String name = RandomStringUtils.randomNumeric(5);

        public String getName()
        {
            return this.name;
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

            Account account = (Account) o;

            return this.name.equals(account.name);
        }

        @Override
        public int hashCode()
        {
            return this.name.hashCode();
        }
    }

    private final class Product
    {
        private final String name = RandomStringUtils.randomNumeric(3);
        private final String category = AggregateByTest.this.categoryPool.put(RandomStringUtils.randomAlphabetic(1).toUpperCase());
        private final double price = DOUBLES.nextDouble();

        public String getName()
        {
            return this.name;
        }

        public double getPrice()
        {
            return this.price;
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

            Product account = (Product) o;

            return this.name.equals(account.name);
        }

        public String getCategory()
        {
            return this.category;
        }

        @Override
        public int hashCode()
        {
            return this.name.hashCode();
        }

        @Override
        public String toString()
        {
            return "Product{"
                    + "name='" + this.name + '\''
                    + ", category='" + this.category + '\''
                    + ", price=" + this.price
                    + '}';
        }
    }
}
