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

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.jmh.domain.Account;
import org.eclipse.collections.impl.jmh.domain.Position;
import org.eclipse.collections.impl.jmh.domain.Positions;
import org.eclipse.collections.impl.jmh.domain.Product;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.Assert;
import org.junit.Test;
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
public class SumByBigDecimalTest
{
    private final Positions positions = new Positions().shuffle();

    @Benchmark
    public MutableMap<Product, BigDecimal> sumByBigDecimalProduct_serial_eager_ec()
    {
        return Iterate.sumByBigDecimal(this.positions.getEcPositions(), Position::getProduct, Position::getPreciseMarketValue);
    }

    @Benchmark
    public MutableMap<Product, BigDecimal> sumByBigDecimalProduct_parallel_eager_ec()
    {
        return ParallelIterate.sumByBigDecimal(this.positions.getEcPositions(), Position::getProduct, Position::getPreciseMarketValue);
    }

    @Test
    public void sumByProduct_ec()
    {
        Assert.assertEquals(
                this.sumByBigDecimalProduct_parallel_eager_ec(),
                this.sumByBigDecimalProduct_serial_eager_ec());
    }

    @Benchmark
    public MutableMap<Account, BigDecimal> sumByBigDecimalAccount_serial_eager_ec()
    {
        return Iterate.sumByBigDecimal(this.positions.getEcPositions(), Position::getAccount, Position::getPreciseMarketValue);
    }

    @Benchmark
    public MutableMap<Account, BigDecimal> sumByBigDecimalAccount_parallel_eager_ec()
    {
        return ParallelIterate.sumByBigDecimal(this.positions.getEcPositions(), Position::getAccount, Position::getPreciseMarketValue);
    }

    @Test
    public void sumByAccount_ec()
    {
        Assert.assertEquals(
                this.sumByBigDecimalAccount_parallel_eager_ec(),
                this.sumByBigDecimalAccount_serial_eager_ec());
    }

    @Benchmark
    public MutableMap<String, BigDecimal> sumByBigDecimalCategory_serial_eager_ec()
    {
        return Iterate.sumByBigDecimal(this.positions.getEcPositions(), Position::getCategory, Position::getPreciseMarketValue);
    }

    @Benchmark
    public MutableMap<String, BigDecimal> sumByBigDecimalCategory_parallel_eager_ec()
    {
        return ParallelIterate.sumByBigDecimal(this.positions.getEcPositions(), Position::getCategory, Position::getPreciseMarketValue);
    }

    @Test
    public void sumByCategory_ec()
    {
        Assert.assertEquals(
                this.sumByBigDecimalCategory_parallel_eager_ec(),
                this.sumByBigDecimalCategory_serial_eager_ec());
    }
}
