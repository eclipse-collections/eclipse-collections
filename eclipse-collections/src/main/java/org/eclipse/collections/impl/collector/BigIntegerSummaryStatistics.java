/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Optional;

import org.eclipse.collections.api.block.procedure.Procedure;

/**
 * BigIntegerSummaryStatistics maintains rolling statistics (count, sum, min, max, average) for {@link BigInteger} values.
 * Similar to {@link java.util.IntSummaryStatistics} but for arbitrary-precision BigInteger values.
 * <p>
 * This class is designed for use with Stream API collectors and Eclipse Collections procedures.
 * It handles null values gracefully by counting them but excluding them from sum/min/max calculations.
 * The average is returned as a {@link BigDecimal} for precision.
 * </p>
 * <p><b>Thread Safety:</b> Not thread-safe. External synchronization required for concurrent updates.</p>
 * <p><b>Performance:</b> Efficient for large datasets. Uses BigInteger arithmetic which provides
 * arbitrary precision but is slower than primitive long operations.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Using with Eclipse Collections
 * MutableList<BigInteger> numbers = Lists.mutable.with(
 *     new BigInteger("1000000000000"),
 *     new BigInteger("2000000000000"),
 *     new BigInteger("3000000000000")
 * );
 * BigIntegerSummaryStatistics stats = numbers.collect(Collectors2.summarizingBigInteger(x -> x));
 * long count = stats.getCount(); // 3
 * BigInteger sum = stats.getSum(); // 6000000000000
 * BigDecimal avg = stats.getAverage(); // 2000000000000
 * BigInteger min = stats.getMin(); // 1000000000000
 * BigInteger max = stats.getMax(); // 3000000000000
 *
 * // Using with Java Streams
 * Stream<BigInteger> stream = Stream.of(
 *     BigInteger.valueOf(100),
 *     BigInteger.valueOf(200)
 * );
 * BigIntegerSummaryStatistics streamStats = stream
 *     .collect(Collectors2.summarizingBigInteger(Function.identity()));
 *
 * // Specify MathContext for average calculation
 * BigDecimal preciseAvg = stats.getAverage(MathContext.DECIMAL64);
 * }</pre>
 *
 * @see Collectors2#summarizingBigInteger(org.eclipse.collections.api.block.function.Function)
 * @since 8.1
 */
public class BigIntegerSummaryStatistics implements Procedure<BigInteger>
{
    private static final long serialVersionUID = 1L;
    private long count;
    private BigInteger sum = BigInteger.ZERO;
    private BigInteger min;
    private BigInteger max;

    @Override
    public void value(BigInteger each)
    {
        this.count++;
        if (each != null)
        {
            this.sum = this.sum.add(each);
            this.min = this.min == null ? each : this.min.min(each);
            this.max = this.max == null ? each : this.max.max(each);
        }
    }

    public long getCount()
    {
        return this.count;
    }

    public BigInteger getSum()
    {
        return this.sum;
    }

    public BigInteger getMin()
    {
        return this.min;
    }

    public Optional<BigInteger> getMinOptional()
    {
        return Optional.ofNullable(this.min);
    }

    public BigInteger getMax()
    {
        return this.max;
    }

    public Optional<BigInteger> getMaxOptional()
    {
        return Optional.ofNullable(this.max);
    }

    public BigDecimal getAverage(MathContext context)
    {
        return this.count == 0L
                ? BigDecimal.ZERO
                : new BigDecimal(this.getSum()).divide(BigDecimal.valueOf(this.count), context);
    }

    public BigDecimal getAverage()
    {
        return this.getAverage(MathContext.DECIMAL128);
    }

    public BigIntegerSummaryStatistics merge(BigIntegerSummaryStatistics summaryStatistics)
    {
        this.count += summaryStatistics.count;
        this.sum = this.sum.add(summaryStatistics.sum);
        if (summaryStatistics.min != null)
        {
            this.min = this.min == null ? summaryStatistics.min : this.min.min(summaryStatistics.min);
        }
        if (summaryStatistics.max != null)
        {
            this.max = this.max == null ? summaryStatistics.max : this.max.max(summaryStatistics.max);
        }
        return this;
    }
}
