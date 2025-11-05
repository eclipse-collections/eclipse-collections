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
import java.math.MathContext;
import java.util.Optional;

import org.eclipse.collections.api.block.procedure.Procedure;

/**
 * BigDecimalSummaryStatistics maintains rolling statistics (count, sum, min, max, average) for {@link BigDecimal} values.
 * Similar to {@link java.util.DoubleSummaryStatistics} but for arbitrary-precision BigDecimal values.
 * <p>
 * This class is designed for use with Stream API collectors and Eclipse Collections procedures.
 * It handles null values gracefully by counting them but excluding them from sum/min/max calculations.
 * </p>
 * <p><b>Thread Safety:</b> Not thread-safe. External synchronization required for concurrent updates.</p>
 * <p><b>Performance:</b> Efficient for large datasets. Uses BigDecimal arithmetic which is slower
 * than primitive operations but provides arbitrary precision.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Using with Eclipse Collections
 * MutableList<BigDecimal> prices = Lists.mutable.with(
 *     new BigDecimal("10.50"),
 *     new BigDecimal("20.75"),
 *     new BigDecimal("15.25")
 * );
 * BigDecimalSummaryStatistics stats = prices.collect(Collectors2.summarizingBigDecimal(x -> x));
 * long count = stats.getCount(); // 3
 * BigDecimal sum = stats.getSum(); // 46.50
 * BigDecimal avg = stats.getAverage(); // 15.50
 * BigDecimal min = stats.getMin(); // 10.50
 * BigDecimal max = stats.getMax(); // 20.75
 *
 * // Using with Java Streams
 * Stream<BigDecimal> stream = Stream.of(
 *     new BigDecimal("100"),
 *     new BigDecimal("200")
 * );
 * BigDecimalSummaryStatistics streamStats = stream
 *     .collect(Collectors2.summarizingBigDecimal(Function.identity()));
 *
 * // Merging statistics from multiple sources
 * BigDecimalSummaryStatistics stats1 = new BigDecimalSummaryStatistics();
 * BigDecimalSummaryStatistics stats2 = new BigDecimalSummaryStatistics();
 * // ... populate stats1 and stats2 ...
 * stats1.merge(stats2); // Combines both statistics
 * }</pre>
 *
 * @see Collectors2#summarizingBigDecimal(org.eclipse.collections.api.block.function.Function)
 * @since 8.1
 */
public class BigDecimalSummaryStatistics implements Procedure<BigDecimal>
{
    private static final long serialVersionUID = 1L;
    private long count;
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal min;
    private BigDecimal max;

    @Override
    public void value(BigDecimal each)
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

    public BigDecimal getSum()
    {
        return this.sum;
    }

    public BigDecimal getMin()
    {
        return this.min;
    }

    public Optional<BigDecimal> getMinOptional()
    {
        return Optional.ofNullable(this.min);
    }

    public BigDecimal getMax()
    {
        return this.max;
    }

    public Optional<BigDecimal> getMaxOptional()
    {
        return Optional.ofNullable(this.max);
    }

    public BigDecimal getAverage(MathContext context)
    {
        return this.count == 0 ? BigDecimal.ZERO : this.getSum().divide(BigDecimal.valueOf(this.count), context);
    }

    public BigDecimal getAverage()
    {
        return this.getAverage(MathContext.DECIMAL128);
    }

    public BigDecimalSummaryStatistics merge(BigDecimalSummaryStatistics summaryStatistics)
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
