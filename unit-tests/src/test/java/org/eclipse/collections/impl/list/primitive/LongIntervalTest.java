/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.primitive;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.stream.Collectors;

import org.eclipse.collections.api.LazyLongIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.procedure.primitive.LongProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.iterator.LongIterator;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.tuple.primitive.LongLongPair;
import org.eclipse.collections.api.tuple.primitive.LongObjectPair;
import org.eclipse.collections.impl.ThrowingAppendable;
import org.eclipse.collections.impl.bag.mutable.primitive.LongHashBag;
import org.eclipse.collections.impl.block.factory.primitive.LongPredicates;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.math.MutableLong;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LongIntervalTest
{
    private final LongInterval longInterval = LongInterval.oneTo(3);

    @Test
    public void fromAndToAndBy()
    {
        LongInterval interval = LongInterval.from(1);
        LongInterval interval2 = interval.to(10);
        LongInterval interval3 = interval2.by(2);
        Verify.assertEqualsAndHashCode(interval, LongInterval.fromTo(1, 1));
        Verify.assertEqualsAndHashCode(interval2, LongInterval.fromTo(1, 10));
        Verify.assertEqualsAndHashCode(interval3, LongInterval.fromToBy(1, 10, 2));
        Verify.assertSize(Integer.MAX_VALUE, LongInterval.fromTo(Integer.MIN_VALUE + 1, -1));
        Verify.assertSize(Integer.MAX_VALUE, LongInterval.fromTo(1, Integer.MAX_VALUE));
        Verify.assertSize(21, LongInterval.from(10L).to(-10L).by(-1L));

        assertThrows(IllegalArgumentException.class, () -> LongInterval.fromTo(Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class, () -> LongInterval.fromTo(-1, Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class, () -> LongInterval.fromToBy(Integer.MIN_VALUE, Integer.MAX_VALUE, 2));
        assertEquals(LongInterval.fromTo(Integer.MIN_VALUE + 1, -1).size(), LongInterval.oneTo(Integer.MAX_VALUE).size());

        assertEquals(LongLists.mutable.with(0), LongInterval.fromToBy(0, 2, 3));
        assertEquals(LongLists.mutable.with(0), LongInterval.fromToBy(0, -2, -3));
        assertEquals(LongLists.mutable.with(1_000_000_000), LongInterval.fromToBy(1_000_000_000, 2_000_000_000, 1_500_000_000));
        assertEquals(LongLists.mutable.with(-1_000_000_000), LongInterval.fromToBy(-1_000_000_000, -2_000_000_000, -1_500_000_000));
        assertEquals(LongLists.mutable.with(Integer.MIN_VALUE), LongInterval.fromToBy(Integer.MIN_VALUE, Integer.MIN_VALUE + 10, 20));
        assertEquals(LongLists.mutable.with(Integer.MAX_VALUE), LongInterval.fromToBy(Integer.MAX_VALUE, Integer.MAX_VALUE - 10, -20));
    }

    @Test
    public void fromAndToAndByWithLongs()
    {
        long maxint = Integer.MAX_VALUE;
        long minint = Integer.MIN_VALUE;
        LongInterval interval = LongInterval.from(maxint + 1);
        LongInterval interval2 = interval.to(maxint + 10);
        LongInterval interval3 = interval2.by(2);
        Verify.assertEqualsAndHashCode(interval, LongInterval.fromTo(maxint + 1, maxint + 1));
        Verify.assertEqualsAndHashCode(interval2, LongInterval.fromTo(maxint + 1, maxint + 10));
        Verify.assertEqualsAndHashCode(interval3, LongInterval.fromToBy(maxint + 1, maxint + 10, 2));

        assertEquals(LongLists.mutable.with(maxint, maxint * 2, maxint * 3), LongInterval.fromToBy(maxint, maxint * 3L, maxint));
        assertEquals(
                LongLists.mutable.with(minint, minint + maxint, minint + maxint * 2, minint + maxint * 3),
                LongInterval.fromToBy(minint, maxint * 2, maxint));
    }

    @Test
    public void fromToBy_throws_step_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> LongInterval.fromToBy(0, 0, 0));
    }

    @Test
    public void fromToBy_throws_on_illegal_step()
    {
        assertThrows(IllegalArgumentException.class, () -> LongInterval.fromToBy(5, 0, 1));
    }

    @Test
    public void fromAndToAndBy_throws_step_is_incorrect()
    {
        assertThrows(IllegalArgumentException.class, () -> LongInterval.from(10L).to(-10L).by(1L));
        assertThrows(IllegalArgumentException.class, () -> LongInterval.from(-10L).to(10L).by(-1L));
    }

    @Test
    public void fromToBy_with_same_start_and_end_with_negative_step()
    {
        MutableLongList integers = LongInterval.fromToBy(2, 2, -2).toList();

        assertEquals(1, integers.size());
        assertEquals(2, integers.getFirst());
    }

    @Test
    public void fromToBy_with_same_start_and_end_with_negative_step2()
    {
        MutableLongList integers = LongInterval.fromToBy(2, 2, -1).toList();

        assertEquals(1, integers.size());
        assertEquals(2, integers.getFirst());
    }

    @Test
    public void oneToBy_throws_step_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> LongInterval.oneToBy(1, 0));
    }

    @Test
    public void oneToBy_throws_count_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> LongInterval.oneToBy(0, 1));
    }

    @Test
    public void zeroToBy_throws_step_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> LongInterval.zeroToBy(0, 0));
    }

    @Test
    public void equalsAndHashCode()
    {
        LongInterval interval1 = LongInterval.oneTo(5);
        LongInterval interval2 = LongInterval.oneTo(5);
        LongInterval interval3 = LongInterval.zeroTo(5);
        Verify.assertPostSerializedEqualsAndHashCode(interval1);
        Verify.assertEqualsAndHashCode(interval1, interval2);
        assertNotEquals(interval1, interval3);
        assertNotEquals(interval3, interval1);

        Verify.assertEqualsAndHashCode(LongInterval.fromToBy(1, 5, 2), LongInterval.fromToBy(1, 6, 2));
        Verify.assertEqualsAndHashCode(LongArrayList.newListWith(1, 2, 3), LongInterval.fromTo(1, 3));
        Verify.assertEqualsAndHashCode(LongArrayList.newListWith(3, 2, 1), LongInterval.fromTo(3, 1));

        assertNotEquals(LongArrayList.newListWith(1, 2, 3, 4), LongInterval.fromTo(1, 3));
        assertNotEquals(LongArrayList.newListWith(1, 2, 4), LongInterval.fromTo(1, 3));
        assertNotEquals(LongArrayList.newListWith(3, 2, 0), LongInterval.fromTo(3, 1));

        Verify.assertEqualsAndHashCode(LongArrayList.newListWith(-1, -2, -3), LongInterval.fromTo(-1, -3));

        Verify.assertEqualsAndHashCode(LongArrayList.newListWith(1), LongInterval.fromToBy(1, 1, 1));
        Verify.assertEqualsAndHashCode(LongArrayList.newListWith(1), LongInterval.fromToBy(1, 1, 2));
        Verify.assertEqualsAndHashCode(LongArrayList.newListWith(-1), LongInterval.fromToBy(-1, -1, -1));
        Verify.assertEqualsAndHashCode(LongArrayList.newListWith(-1), LongInterval.fromToBy(-1, -1, -2));

        LongInterval interval4 = LongInterval.fromTo(-1, -1_000);
        LongInterval interval5 = LongInterval.fromTo(-1, -1_000);
        LongInterval interval6 = LongInterval.fromTo(0, -999);
        Verify.assertPostSerializedEqualsAndHashCode(interval4);
        Verify.assertEqualsAndHashCode(interval4, interval5);
        assertNotEquals(interval4, interval6);
        assertNotEquals(interval6, interval4);
    }

    @Test
    public void sumLongInterval()
    {
        assertEquals(15, (int) LongInterval.oneTo(5).sum());
    }

    @Test
    public void maxLongInterval()
    {
        long value = LongInterval.oneTo(5).max();
        assertEquals(5L, value);
    }

    @Test
    public void iterator()
    {
        LongIterator iterator = this.longInterval.longIterator();
        assertTrue(iterator.hasNext());
        assertEquals(1L, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(2L, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(3L, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void iterator_throws()
    {
        LongIterator iterator = this.longInterval.longIterator();
        while (iterator.hasNext())
        {
            iterator.next();
        }

        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void forEach()
    {
        long[] sum = new long[1];
        this.longInterval.forEach(each -> sum[0] += each);

        assertEquals(6L, sum[0]);
    }

    @Test
    public void each()
    {
        MutableLongList list1 = LongLists.mutable.empty();
        LongInterval interval1 = LongInterval.oneTo(5);
        interval1.each(list1::add);
        assertEquals(list1, interval1);
        LongInterval interval2 = LongInterval.fromTo(5, 1);
        MutableLongList list2 = LongLists.mutable.empty();
        interval2.each(list2::add);
        assertEquals(list2, interval2);
    }

    @Test
    public void eachForLargeValues()
    {
        var values1 = LongLists.mutable.empty();

        LongInterval.zeroToBy(9_999_999_999L, 1_000_000_000L).forEach(values1::add);

        assertEquals(
                LongLists.immutable.of(
                        0, 1_000_000_000L, 2_000_000_000L, 3_000_000_000L, 4_000_000_000L,
                        5_000_000_000L, 6_000_000_000L, 7_000_000_000L, 8_000_000_000L, 9_000_000_000L),
                values1);

        var values2 = LongLists.mutable.empty();

        LongInterval.zeroToBy(-9_999_999_999L, -1_000_000_000L).forEach(values2::add);

        assertEquals(
                LongLists.immutable.of(
                        0, -1_000_000_000L, -2_000_000_000L, -3_000_000_000L, -4_000_000_000L,
                        -5_000_000_000L, -6_000_000_000L, -7_000_000_000L, -8_000_000_000L, -9_000_000_000L),
                values2);
    }

    @Test
    public void forEachWithIndexForLargeValues()
    {
        var values1 = LongLists.mutable.empty();
        var indices1 = IntLists.mutable.empty();

        LongInterval.zeroToBy(9_999_999_999L, 1_000_000_000L)
                .forEachWithIndex((each, index) -> {
                    values1.add(each + index);
                    indices1.add(index);
                });

        assertEquals(IntLists.immutable.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), indices1);

        assertEquals(
                LongLists.immutable.of(0, 1_000_000_001L, 2_000_000_002L, 3_000_000_003L, 4_000_000_004L,
                        5_000_000_005L, 6_000_000_006L, 7_000_000_007L, 8_000_000_008L, 9_000_000_009L),
                values1);

        var values2 = LongLists.mutable.empty();
        var indices2 = IntLists.mutable.empty();

        LongInterval.zeroToBy(-9_999_999_999L, -1_000_000_000L)
                .forEachWithIndex((each, index) -> {
                    values2.add(each - index);
                    indices2.add(index);
                });

        assertEquals(IntLists.immutable.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), indices2);

        assertEquals(
                LongLists.immutable.of(0, -1_000_000_001L, -2_000_000_002L, -3_000_000_003L, -4_000_000_004L,
                        -5_000_000_005L, -6_000_000_006L, -7_000_000_007L, -8_000_000_008L, -9_000_000_009L),
                values2);
    }

    @Test
    public void injectInto()
    {
        LongInterval longInterval1 = LongInterval.oneTo(3);
        MutableLong result = longInterval1.injectInto(new MutableLong(0), MutableLong::add);
        assertEquals(new MutableLong(6), result);
        LongInterval longInterval2 = LongInterval.fromTo(3, 1);
        MutableLong result2 = longInterval2.injectInto(new MutableLong(0), MutableLong::add);
        assertEquals(new MutableLong(6), result2);
    }

    @Test
    public void injectIntoWithIndex()
    {
        LongInterval interval1 = LongInterval.oneTo(3);
        MutableLong result1 = this.longInterval.injectIntoWithIndex(new MutableLong(0), (object, value, index) -> object.add(value * interval1.get(index)));
        assertEquals(new MutableLong(14), result1);
        LongInterval interval2 = LongInterval.fromTo(3, 1);
        MutableLong result2 = interval2.injectIntoWithIndex(new MutableLong(0), (object, value, index) -> object.add(value * this.longInterval.get(index)));
        assertEquals(new MutableLong(10), result2);
    }

    @Test
    public void injectIntoForLargeValues()
    {
        long largeNumber = (long) Integer.MAX_VALUE * 2L;
        LongInterval interval1 = LongInterval.fromTo(largeNumber, largeNumber + 10L);
        MutableLong result1 = interval1.injectInto(new MutableLong(0), MutableLong::add);
        long expected1 = 47244640289L; // largeNumber * 11 + 55;
        assertEquals(new MutableLong(expected1), result1);

        long largeNegativeNumber = (long) Integer.MIN_VALUE * 2L;
        LongInterval interval2 = LongInterval.fromToBy(largeNegativeNumber, largeNegativeNumber - 10L, -1L);
        MutableLong result2 = interval2.injectInto(new MutableLong(0), MutableLong::add);
        long expected2 = -47244640311L; // (largeNegativeNumber * 11L) - 55L;
        assertEquals(new MutableLong(expected2), result2);
    }

    @Test
    public void injectIntoWithIndexForLargeValues()
    {
        long largeNumber = (long) Integer.MAX_VALUE * 2L;

        LongInterval interval1 = LongInterval.fromTo(largeNumber, largeNumber + 3L);

        MutableLong result1 = interval1.injectIntoWithIndex(
                new MutableLong(0),
                (object, value, index) -> {
                    assertEquals(interval1.get(index), value);
                    return object.add(value);
                });

        assertEquals(new MutableLong(17179869182L), result1);

        long largeNegativeNumber = (long) Integer.MIN_VALUE * 2L;
        LongInterval interval2 = LongInterval.fromToBy(largeNegativeNumber, largeNegativeNumber - 3L, -1L);

        MutableLong result2 = interval2.injectIntoWithIndex(
                new MutableLong(0),
                (object, value, index) -> {
                    assertEquals(interval2.get(index), value);
                    return object.add(value);
                });

        assertEquals(new MutableLong(-17179869190L), result2);
    }

    @Test
    public void injectIntoOnFromToBySameStartEndNegativeStepInterval()
    {
        LongInterval interval = LongInterval.fromToBy(2, 2, -2);

        assertEquals(new MutableLong(0), interval.injectInto(new MutableLong(-2), MutableLong::add));
    }

    @Test
    public void chunk()
    {
        LongInterval interval1 = LongInterval.fromToBy(0, 5, 1);
        MutableList<LongInterval> expected1 = Lists.mutable.with(
                LongInterval.fromToBy(0, 1, 1),
                LongInterval.fromToBy(2, 3, 1),
                LongInterval.fromToBy(4, 5, 1));
        assertEquals(expected1, interval1.chunk(2));

        LongInterval interval2 = LongInterval.fromToBy(0, -5, -1);
        MutableList<LongInterval> expected2 = Lists.mutable.with(
                LongInterval.fromToBy(0, -1, -1),
                LongInterval.fromToBy(-2, -3, -1),
                LongInterval.fromToBy(-4, -5, -1));
        assertEquals(expected2, interval2.chunk(2));

        LongInterval interval3 = LongInterval.fromToBy(0, 6, 1);
        MutableList<LongInterval> expected3 = Lists.mutable.with(
                LongInterval.fromToBy(0, 1, 1),
                LongInterval.fromToBy(2, 3, 1),
                LongInterval.fromToBy(4, 5, 1),
                LongInterval.fromToBy(6, 6, 1));
        assertEquals(expected3, interval3.chunk(2));

        LongInterval interval4 = LongInterval.fromToBy(0, -6, -1);
        MutableList<LongInterval> expected4 = Lists.mutable.with(
                LongInterval.fromToBy(0, -1, -1),
                LongInterval.fromToBy(-2, -3, -1),
                LongInterval.fromToBy(-4, -5, -1),
                LongInterval.fromToBy(-6, -6, -1));
        RichIterable<LongIterable> actual4 = interval4.chunk(2);
        assertEquals(expected4, actual4);

        LongInterval interval5 = LongInterval.fromToBy(0, 6, 1);
        MutableList<LongInterval> expected5 = Lists.mutable.with(LongInterval.fromToBy(0, 6, 1));
        assertEquals(expected5, interval5.chunk(7));

        LongInterval interval6 = LongInterval.fromToBy(0, -6, -1);
        MutableList<LongInterval> expected6 = Lists.mutable.with(LongInterval.fromToBy(0, -6, -1));
        assertEquals(expected6, interval6.chunk(7));

        LongInterval interval7 = LongInterval.fromToBy(0, 6, 1);
        MutableList<LongInterval> expected7 = Lists.mutable.with(LongInterval.fromToBy(0, 6, 1));
        assertEquals(expected7, interval7.chunk(8));

        LongInterval interval8 = LongInterval.fromToBy(0, -6, -1);
        MutableList<LongInterval> expected8 = Lists.mutable.with(LongInterval.fromToBy(0, -6, -1));
        assertEquals(expected8, interval8.chunk(8));

        LongInterval interval9 = LongInterval.fromToBy(0, 9, 4);
        MutableList<LongIterable> expected9 = Lists.mutable.with(
                LongLists.mutable.with(0, 4),
                LongLists.mutable.with(8));
        assertEquals(expected9, interval9.chunk(2));

        LongInterval interval10 = LongInterval.fromToBy(0, -9, -4);
        MutableList<LongIterable> expected10 = Lists.mutable.with(
                LongLists.mutable.with(0, -4),
                LongLists.mutable.with(-8));
        assertEquals(expected10, interval10.chunk(2));

        LongInterval interval11 = LongInterval.fromToBy(0, 5, 3);
        MutableList<LongIterable> expected11 = Lists.mutable.with(LongLists.mutable.with(0, 3));
        assertEquals(expected11, interval11.chunk(3));

        LongInterval interval12 = LongInterval.fromToBy(0, -5, -3);
        MutableList<LongIterable> expected12 = Lists.mutable.with(LongLists.mutable.with(0, -3));
        assertEquals(expected12, interval12.chunk(3));

        assertThrows(IllegalArgumentException.class, () -> interval12.chunk(0));
        assertThrows(IllegalArgumentException.class, () -> interval12.chunk(-1));
    }

    @Test
    public void chunkOnSingleValueInterval()
    {
        MutableList<LongIterable> expected = Lists.mutable.with(LongLists.mutable.with(1L));
        assertEquals(expected, LongInterval.oneTo(1).chunk(1));
        assertEquals(expected, LongInterval.oneTo(1).chunk(2));
        assertEquals(expected, LongInterval.oneTo(1).chunk(Integer.MAX_VALUE));

        assertEquals(Lists.mutable.with(LongLists.mutable.with(0L)), LongInterval.fromToBy(0, 4, 5).chunk(2));
        assertEquals(Lists.mutable.with(LongLists.mutable.with(7L)), LongInterval.fromTo(7, 7).chunk(3));

        assertEquals(LongLists.mutable.with(1L).chunk(2), LongInterval.oneTo(1).chunk(2));
    }

    @Test
    public void longIteratorOnSingleValueIntervalWithNegativeStep()
    {
        LongInterval interval = LongInterval.fromToBy(5, 5, -1);
        assertEquals(1, interval.size());

        LongIterator iterator = interval.longIterator();
        assertTrue(iterator.hasNext());
        assertEquals(5L, iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(Lists.mutable.with(LongLists.mutable.with(5L)), interval.chunk(2));
        assertEquals(LongLists.mutable.with(5L).chunk(2), interval.chunk(2));
    }

    @Test
    public void spliteratorOfSingleValueIntervalWithNegativeStep()
    {
        LongInterval interval = LongInterval.fromToBy(5, 5, -1);
        assertArrayEquals(new long[]{5}, take(interval.spliterator(), 4));
        assertEquals(Comparator.reverseOrder(), interval.spliterator().getComparator());

        assertArrayEquals(new long[]{5, 4, 3, 2, 1}, take(LongInterval.fromToBy(5, 1, -1).spliterator(), 10));
        assertArrayEquals(new long[]{1, 4, 7, 10}, take(LongInterval.fromToBy(1, 10, 3).spliterator(), 10));
    }

    private static long[] take(Spliterator.OfLong spliterator, int limit)
    {
        MutableLongList values = LongLists.mutable.empty();
        boolean advanced = true;
        while (advanced && values.size() < limit)
        {
            advanced = spliterator.tryAdvance((long each) -> values.add(each));
        }
        return values.toArray();
    }

    @Test
    public void chunkForLargeValues()
    {
        LongInterval interval1 = LongInterval.fromToBy(0, 9_999_999_999L, 1_000_000_000L);
        MutableList<LongInterval> expected1 = Lists.mutable.with(
                LongInterval.fromToBy(0, 2_000_000_000L, 1_000_000_000L),
                LongInterval.fromToBy(3_000_000_000L, 5_000_000_000L, 1_000_000_000L),
                LongInterval.fromToBy(6_000_000_000L, 8_000_000_000L, 1_000_000_000L),
                LongInterval.fromToBy(9_000_000_000L, 9_000_000_000L, 1_000_000_000L));
        assertEquals(expected1, interval1.chunk(3));

        LongInterval interval2 = LongInterval.fromToBy(0, -9_999_999_999L, -1_000_000_000L);

        MutableList<LongInterval> expected2 = Lists.mutable.with(
                LongInterval.fromToBy(0, -2_000_000_000L, -1_000_000_000L),
                LongInterval.fromToBy(-3_000_000_000L, -5_000_000_000L, -1_000_000_000L),
                LongInterval.fromToBy(-6_000_000_000L, -8_000_000_000L, -1_000_000_000L),
                LongInterval.fromToBy(-9_000_000_000L, -9_000_000_000L, -1_000_000_000L));

        assertEquals(expected2, interval2.chunk(3));
    }

    @Test
    public void size()
    {
        Verify.assertSize(3, this.longInterval);
        // Positive Ranges
        Verify.assertSize(10, LongInterval.zeroTo(9));
        Verify.assertSize(2_000_000_000, LongInterval.oneTo(2_000_000_000));
        Verify.assertSize(200_000_000, LongInterval.oneTo(2_000_000_000).by(10));
        Verify.assertSize(2_000_000_000, LongInterval.fromTo(2_000_000_000, 1).by(-1));
        Verify.assertSize(500_000_000, LongInterval.oneTo(2_000_000_000).by(4));
        Verify.assertSize(222_222_223, LongInterval.oneTo(2_000_000_000).by(9));
        Verify.assertSize(2, LongInterval.fromToBy(Integer.MAX_VALUE - 10, Integer.MAX_VALUE, 8));

        // Negative Ranges
        Verify.assertSize(10, LongInterval.zeroTo(-9));
        Verify.assertSize(11, LongInterval.oneTo(-9));
        Verify.assertSize(10, LongInterval.fromTo(0, -9));
        Verify.assertSize(2_000_000_000, LongInterval.fromTo(-1, -2_000_000_000));
        Verify.assertSize(200_000_000, LongInterval.fromTo(-1, -2_000_000_000).by(-10));
        Verify.assertSize(2_000_000_000, LongInterval.fromTo(-2_000_000_000, -1).by(1));
        Verify.assertSize(500_000_000, LongInterval.fromTo(-1, -2_000_000_000).by(-4));
        Verify.assertSize(222_222_223, LongInterval.fromTo(-1, -2_000_000_000).by(-9));
        // Overlapping Ranges
        Verify.assertSize(21, LongInterval.fromTo(10, -10));
        Verify.assertSize(5, LongInterval.fromTo(10, -10).by(-5));
        Verify.assertSize(5, LongInterval.fromTo(-10, 10).by(5));
        Verify.assertSize(2_000_000_001, LongInterval.fromTo(1_000_000_000, -1_000_000_000));
        Verify.assertSize(200_000_001, LongInterval.fromTo(1_000_000_000, -1_000_000_000).by(-10));
    }

    @Test
    public void subList()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.longInterval.subList(0, 1));
    }

    @Test
    public void dotProduct()
    {
        LongInterval interval = LongInterval.oneTo(3);
        assertEquals(14, this.longInterval.dotProduct(interval));
    }

    @Test
    public void dotProduct_throwsOnListsOfDifferentSizes()
    {
        LongInterval interval = LongInterval.oneTo(4);
        assertThrows(IllegalArgumentException.class, () -> this.longInterval.dotProduct(interval));
    }

    @Test
    public void empty()
    {
        assertTrue(this.longInterval.notEmpty());
        assertFalse(this.longInterval.isEmpty());
        Verify.assertNotEmpty(this.longInterval);
    }

    @Test
    public void count()
    {
        assertEquals(2L, LongInterval.zeroTo(2).count(LongPredicates.greaterThan(0)));

        int count = LongInterval.fromToBy(Integer.MAX_VALUE - 10, Integer.MAX_VALUE, 8).count(LongPredicates.greaterThan(0));
        assertEquals(2, count);
    }

    @Test
    public void anySatisfy()
    {
        assertTrue(LongInterval.fromTo(-1, 2).anySatisfy(LongPredicates.greaterThan(0)));
        assertFalse(LongInterval.oneTo(2).anySatisfy(LongPredicates.equal(0)));
    }

    @Test
    public void allSatisfy()
    {
        assertFalse(LongInterval.zeroTo(2).allSatisfy(LongPredicates.greaterThan(0)));
        assertTrue(LongInterval.oneTo(3).allSatisfy(LongPredicates.greaterThan(0)));
    }

    @Test
    public void noneSatisfy()
    {
        assertFalse(LongInterval.zeroTo(2).noneSatisfy(LongPredicates.isEven()));
        assertTrue(LongInterval.evensFromTo(2, 10).noneSatisfy(LongPredicates.isOdd()));
    }

    @Test
    public void select()
    {
        Verify.assertSize(3, this.longInterval.select(LongPredicates.lessThan(4)));
        Verify.assertSize(2, this.longInterval.select(LongPredicates.lessThan(3)));
    }

    @Test
    public void reject()
    {
        Verify.assertSize(0, this.longInterval.reject(LongPredicates.lessThan(4)));
        Verify.assertSize(1, this.longInterval.reject(LongPredicates.lessThan(3)));
    }

    @Test
    public void detectIfNone()
    {
        assertEquals(1L, this.longInterval.detectIfNone(LongPredicates.lessThan(4), 0));
        assertEquals(0L, this.longInterval.detectIfNone(LongPredicates.greaterThan(3), 0));
    }

    @Test
    public void collect()
    {
        assertEquals(FastList.newListWith(0L, 1L, 2L), this.longInterval.collect(parameter -> parameter - 1).toList());
    }

    @Test
    public void lazyCollectPrimitives()
    {
        assertEquals(BooleanLists.immutable.of(false, true, false), LongInterval.oneTo(3).asLazy().collectBoolean(e -> e % 2 == 0).toList());
        assertEquals(CharLists.immutable.of((char) 2, (char) 3, (char) 4), LongInterval.oneTo(3).asLazy().collectChar(e -> (char) (e + 1)).toList());
        assertEquals(ByteLists.immutable.of((byte) 2, (byte) 3, (byte) 4), LongInterval.oneTo(3).asLazy().collectByte(e -> (byte) (e + 1)).toList());
        assertEquals(ShortLists.immutable.of((short) 2, (short) 3, (short) 4), LongInterval.oneTo(3).asLazy().collectShort(e -> (short) (e + 1)).toList());
        assertEquals(LongLists.immutable.of(2, 3, 4), LongInterval.oneTo(3).asLazy().collectLong(e -> e + 1).toList());
        assertEquals(FloatLists.immutable.of(2.0f, 3.0f, 4.0f), LongInterval.oneTo(3).asLazy().collectFloat(e -> (float) (e + 1)).toList());
        assertEquals(LongLists.immutable.of(2L, 3L, 4L), LongInterval.oneTo(3).asLazy().collectLong(e -> (long) (e + 1)).toList());
        assertEquals(DoubleLists.immutable.of(2.0, 3.0, 4.0), LongInterval.oneTo(3).asLazy().collectDouble(e -> (double) (e + 1)).toList());
    }

    @Test
    public void binarySearch()
    {
        LongInterval interval1 = LongInterval.oneTo(3);
        assertEquals(-1, interval1.binarySearch(-1));
        assertEquals(-1, interval1.binarySearch(0));
        assertEquals(0, interval1.binarySearch(1));
        assertEquals(1, interval1.binarySearch(2));
        assertEquals(2, interval1.binarySearch(3));
        assertEquals(-4, interval1.binarySearch(4));
        assertEquals(-4, interval1.binarySearch(5));

        LongInterval interval2 = LongInterval.fromTo(7, 17).by(3);
        assertEquals(0, interval2.binarySearch(7));
        assertEquals(1, interval2.binarySearch(10));
        assertEquals(2, interval2.binarySearch(13));
        assertEquals(3, interval2.binarySearch(16));
        assertEquals(-1, interval2.binarySearch(6));
        assertEquals(-2, interval2.binarySearch(8));
        assertEquals(-2, interval2.binarySearch(9));
        assertEquals(-3, interval2.binarySearch(12));
        assertEquals(-4, interval2.binarySearch(15));
        assertEquals(-5, interval2.binarySearch(17));
        assertEquals(-5, interval2.binarySearch(19));

        LongInterval interval3 = LongInterval.fromTo(-21, -11).by(5);
        assertEquals(-1, interval3.binarySearch(-22));
        assertEquals(0, interval3.binarySearch(-21));
        assertEquals(-2, interval3.binarySearch(-17));
        assertEquals(1, interval3.binarySearch(-16));
        assertEquals(-3, interval3.binarySearch(-15));
        assertEquals(2, interval3.binarySearch(-11));
        assertEquals(-4, interval3.binarySearch(-9));

        LongInterval interval4 = LongInterval.fromTo(50, 30).by(-10);
        assertEquals(-1, interval4.binarySearch(60));
        assertEquals(0, interval4.binarySearch(50));
        assertEquals(-2, interval4.binarySearch(45));
        assertEquals(1, interval4.binarySearch(40));
        assertEquals(-3, interval4.binarySearch(35));
        assertEquals(2, interval4.binarySearch(30));
        assertEquals(-4, interval4.binarySearch(25));

        LongInterval interval5 = LongInterval.fromTo(-30, -50).by(-10);
        assertEquals(-1, interval5.binarySearch(-20));
        assertEquals(0, interval5.binarySearch(-30));
        assertEquals(-2, interval5.binarySearch(-35));
        assertEquals(1, interval5.binarySearch(-40));
        assertEquals(-3, interval5.binarySearch(-47));
        assertEquals(2, interval5.binarySearch(-50));
        assertEquals(-4, interval5.binarySearch(-65));

        LongInterval interval6 = LongInterval.fromTo(27, -30).by(-9);
        assertEquals(-1, interval6.binarySearch(30));
        assertEquals(0, interval6.binarySearch(27));
        assertEquals(-2, interval6.binarySearch(20));
        assertEquals(1, interval6.binarySearch(18));
        assertEquals(-3, interval6.binarySearch(15));
        assertEquals(2, interval6.binarySearch(9));
        assertEquals(-4, interval6.binarySearch(2));
        assertEquals(3, interval6.binarySearch(0));
        assertEquals(-5, interval6.binarySearch(-7));
        assertEquals(4, interval6.binarySearch(-9));
        assertEquals(-6, interval6.binarySearch(-12));
        assertEquals(5, interval6.binarySearch(-18));
        assertEquals(-7, interval6.binarySearch(-23));
        assertEquals(6, interval6.binarySearch(-27));
        assertEquals(-8, interval6.binarySearch(-28));
        assertEquals(-8, interval6.binarySearch(-30));

        LongInterval interval7 = LongInterval.fromTo(-1, 1).by(1);
        assertEquals(-1, interval7.binarySearch(-2));
        assertEquals(0, interval7.binarySearch(-1));
        assertEquals(1, interval7.binarySearch(0));
        assertEquals(2, interval7.binarySearch(1));
        assertEquals(-4, interval7.binarySearch(2));
    }

    @Test
    public void max()
    {
        assertEquals(9, LongInterval.oneTo(9).max());
        assertEquals(5, LongInterval.fromTo(5, 1).max());
        assertEquals(1, LongInterval.fromTo(-5, 1).max());
        assertEquals(1, LongInterval.fromTo(1, -5).max());
    }

    @Test
    public void min()
    {
        assertEquals(1, LongInterval.oneTo(9).min());
        assertEquals(1, LongInterval.fromTo(5, 1).min());
        assertEquals(-5, LongInterval.fromTo(-5, 1).min());
        assertEquals(-5, LongInterval.fromTo(1, -5).min());
    }

    @Test
    public void minIfEmpty()
    {
        assertEquals(1, LongInterval.oneTo(9).minIfEmpty(0));
    }

    @Test
    public void maxIfEmpty()
    {
        assertEquals(9, LongInterval.oneTo(9).maxIfEmpty(0));
    }

    @Test
    public void sum()
    {
        assertEquals(10L, LongInterval.oneTo(4).sum());
    }

    @Test
    public void sumLong()
    {
        // checks there is no overflow the during calculation of sum() as long as the final result is <= Long.MAX_VALUE

        assertEquals(Long.MAX_VALUE, LongInterval.fromTo(Long.MAX_VALUE, Long.MAX_VALUE).sum());

        long l = Long.MAX_VALUE / 5L;
        long expectedSum = 0;

        for (long i = 0L; i < 4L; i++)
        {
            expectedSum += l + i;

            long actualSum = LongInterval.fromTo(l, l + i).sum();

            assertEquals(expectedSum, actualSum, "interval size : " + (i + 1));
            assertTrue(actualSum > 0L);
        }
    }

    @Test
    public void average()
    {
        assertEquals(2.5, LongInterval.oneTo(4).average(), 0.0);
    }

    @Test
    public void median()
    {
        assertEquals(2.5, LongInterval.oneTo(4).median(), 0.0);
        assertEquals(3.0, LongInterval.oneTo(5).median(), 0.0);
    }

    @Test
    public void toArray()
    {
        assertArrayEquals(new long[]{1, 2, 3, 4}, LongInterval.oneTo(4).toArray());
    }

    @Test
    public void toList()
    {
        assertEquals(LongArrayList.newListWith(1, 2, 3, 4), LongInterval.oneTo(4).toList());
    }

    @Test
    public void toSortedList()
    {
        assertEquals(LongArrayList.newListWith(1, 2, 3, 4), LongInterval.oneTo(4).toReversed().toSortedList());
    }

    @Test
    public void toSet()
    {
        assertEquals(LongHashSet.newSetWith(1, 2, 3, 4), LongInterval.oneTo(4).toSet());
    }

    @Test
    public void toBag()
    {
        assertEquals(LongHashBag.newBagWith(1, 2, 3, 4), LongInterval.oneTo(4).toBag());
    }

    @Test
    public void asLazy()
    {
        assertEquals(LongInterval.oneTo(5).toSet(), LongInterval.oneTo(5).asLazy().toSet());
        Verify.assertInstanceOf(LazyLongIterable.class, LongInterval.oneTo(5).asLazy());
    }

    @Test
    public void toSortedArray()
    {
        assertArrayEquals(new long[]{1, 2, 3, 4}, LongInterval.fromTo(4, 1).toSortedArray());
    }

    @Test
    public void testEquals()
    {
        LongInterval list1 = LongInterval.oneTo(4);
        LongInterval list2 = LongInterval.oneTo(4);
        LongInterval list3 = LongInterval.fromTo(4, 1);
        LongInterval list4 = LongInterval.fromTo(5, 8);
        LongInterval list5 = LongInterval.fromTo(5, 7);

        Verify.assertEqualsAndHashCode(list1, list2);
        Verify.assertPostSerializedEqualsAndHashCode(list1);
        assertNotEquals(list1, list3);
        assertNotEquals(list1, list4);
        assertNotEquals(list1, list5);
    }

    @Test
    public void testHashCode()
    {
        assertEquals(FastList.newListWith(1, 2, 3).hashCode(), LongInterval.oneTo(3).hashCode());
    }

    @Test
    public void testToString()
    {
        assertEquals("[1, 2, 3]", this.longInterval.toString());
    }

    @Test
    public void makeString()
    {
        assertEquals("1, 2, 3", this.longInterval.makeString());
        assertEquals("1/2/3", this.longInterval.makeString("/"));
        assertEquals(this.longInterval.toString(), this.longInterval.makeString("[", ", ", "]"));
    }

    @Test
    public void appendString()
    {
        StringBuilder appendable2 = new StringBuilder();
        this.longInterval.appendString(appendable2);
        assertEquals("1, 2, 3", appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        this.longInterval.appendString(appendable3, "/");
        assertEquals("1/2/3", appendable3.toString());
        StringBuilder appendable4 = new StringBuilder();
        this.longInterval.appendString(appendable4, "[", ", ", "]");
        assertEquals(this.longInterval.toString(), appendable4.toString());
    }

    @Test
    public void appendStringThrows()
    {
        assertThrows(
                RuntimeException.class,
                () -> this.longInterval.appendString(new ThrowingAppendable()));
        assertThrows(
                RuntimeException.class,
                () -> this.longInterval
                        .appendString(new ThrowingAppendable(), ", "));
        assertThrows(
                RuntimeException.class,
                () -> this.longInterval
                        .appendString(new ThrowingAppendable(), "[", ", ", "]"));
    }

    @Test
    public void toReversed()
    {
        LongInterval forward = LongInterval.oneTo(5);
        LongInterval reverse = forward.toReversed();
        assertEquals(LongArrayList.newListWith(5, 4, 3, 2, 1), reverse);
    }

    @Test
    public void evens()
    {
        LongInterval interval = LongInterval.evensFromTo(0, 10);
        int[] evens = {0, 2, 4, 6, 8, 10};
        int[] odds = {1, 3, 5, 7, 9};
        this.assertLongIntervalContainsAll(interval, evens);
        this.denyLongIntervalContainsAny(interval, odds);
        assertEquals(6, interval.size());

        LongInterval reverseLongInterval = LongInterval.evensFromTo(10, 0);
        this.assertLongIntervalContainsAll(reverseLongInterval, evens);
        this.denyLongIntervalContainsAny(reverseLongInterval, odds);
        assertEquals(6, reverseLongInterval.size());

        LongInterval negativeLongInterval = LongInterval.evensFromTo(-5, 5);
        int[] negativeEvens = {-4, -2, 0, 2, 4};
        int[] negativeOdds = {-3, -1, 1, 3};
        this.assertLongIntervalContainsAll(negativeLongInterval, negativeEvens);
        this.denyLongIntervalContainsAny(negativeLongInterval, negativeOdds);
        assertEquals(5, negativeLongInterval.size());

        LongInterval reverseNegativeLongInterval = LongInterval.evensFromTo(5, -5);
        this.assertLongIntervalContainsAll(reverseNegativeLongInterval, negativeEvens);
        this.denyLongIntervalContainsAny(reverseNegativeLongInterval, negativeOdds);
        assertEquals(5, reverseNegativeLongInterval.size());
    }

    private void assertLongIntervalContainsAll(LongInterval interval, int[] expectedValues)
    {
        for (int value : expectedValues)
        {
            assertTrue(interval.contains(value));
        }
    }

    private void denyLongIntervalContainsAny(LongInterval interval, int[] expectedValues)
    {
        for (int value : expectedValues)
        {
            assertFalse(interval.contains(value));
        }
    }

    @Test
    public void odds()
    {
        LongInterval interval1 = LongInterval.oddsFromTo(0, 10);
        assertTrue(interval1.containsAll(1, 3, 5, 7, 9));
        assertTrue(interval1.containsNone(2, 4, 6, 8));
        assertEquals(5, interval1.size());

        LongInterval reverseLongInterval1 = LongInterval.oddsFromTo(10, 0);
        assertTrue(reverseLongInterval1.containsAll(1, 3, 5, 7, 9));
        assertTrue(reverseLongInterval1.containsNone(0, 2, 4, 6, 8, 10));
        assertEquals(5, reverseLongInterval1.size());

        LongInterval interval2 = LongInterval.oddsFromTo(-5, 5);
        assertTrue(interval2.containsAll(-5, -3, -1, 1, 3, 5));
        assertTrue(interval2.containsNone(-4, -2, 0, 2, 4));
        assertEquals(6, interval2.size());

        LongInterval reverseLongInterval2 = LongInterval.oddsFromTo(5, -5);
        assertTrue(reverseLongInterval2.containsAll(-5, -3, -1, 1, 3, 5));
        assertTrue(reverseLongInterval2.containsNone(-4, -2, 0, 2, 4));
        assertEquals(6, reverseLongInterval2.size());
    }

    @Test
    public void intervalSize()
    {
        assertEquals(100, LongInterval.fromTo(1, 100).size());
        assertEquals(50, LongInterval.fromToBy(1, 100, 2).size());
        assertEquals(34, LongInterval.fromToBy(1, 100, 3).size());
        assertEquals(25, LongInterval.fromToBy(1, 100, 4).size());
        assertEquals(20, LongInterval.fromToBy(1, 100, 5).size());
        assertEquals(17, LongInterval.fromToBy(1, 100, 6).size());
        assertEquals(15, LongInterval.fromToBy(1, 100, 7).size());
        assertEquals(13, LongInterval.fromToBy(1, 100, 8).size());
        assertEquals(12, LongInterval.fromToBy(1, 100, 9).size());
        assertEquals(10, LongInterval.fromToBy(1, 100, 10).size());
        assertEquals(11, LongInterval.fromTo(0, 10).size());
        assertEquals(1, LongInterval.zero().size());
        assertEquals(11, LongInterval.fromTo(0, -10).size());
        assertEquals(3, LongInterval.evensFromTo(2, -2).size());
        assertEquals(2, LongInterval.oddsFromTo(2, -2).size());
        assertEquals(1, LongInterval.fromToBy(1_000_000_000, 2_000_000_000, 1_500_000_000).size());
        assertEquals(1, LongInterval.fromToBy(-1_000_000_000, -2_000_000_000, -1_500_000_000).size());
    }

    @Test
    public void contains()
    {
        assertTrue(LongInterval.zero().contains(0));
        assertTrue(LongInterval.oneTo(5).containsAll(1, 5));
        assertTrue(LongInterval.oneTo(5).containsNone(6, 7));
        assertFalse(LongInterval.oneTo(5).containsAll(1, 6));
        assertFalse(LongInterval.oneTo(5).containsNone(1, 6));
        assertFalse(LongInterval.oneTo(5).contains(0));
        assertTrue(LongInterval.fromTo(-1, -5).containsAll(-1, -5));
        assertFalse(LongInterval.fromTo(-1, -5).contains(1));

        assertTrue(LongInterval.zero().contains(Integer.valueOf(0)));
        assertFalse(LongInterval.oneTo(5).contains(Integer.valueOf(0)));
        assertFalse(LongInterval.fromTo(-1, -5).contains(Integer.valueOf(1)));

        LongInterval bigLongInterval = LongInterval.fromToBy(Integer.MIN_VALUE, Integer.MAX_VALUE, 1_000_000);
        assertTrue(bigLongInterval.contains(Integer.MIN_VALUE + 1_000_000));
        assertFalse(bigLongInterval.contains(Integer.MIN_VALUE + 1_000_001));
        assertTrue(bigLongInterval.contains(Integer.MIN_VALUE + (1_000_000 * 10)));
        assertFalse(bigLongInterval.contains(Integer.MIN_VALUE + (1_000_001 * 10)));
        assertTrue(bigLongInterval.contains(Integer.MIN_VALUE + (1_000_000 * 100)));
        assertFalse(bigLongInterval.contains(Integer.MIN_VALUE + (1_000_001 * 100)));
        assertTrue(
                LongInterval.fromToBy(1_000_000_000, 2_000_000_000, 1_500_000_000)
                        .contains(1_000_000_000));
        assertTrue(
                LongInterval.fromToBy(-1_000_000_000, -2_000_000_000, -1_500_000_000)
                        .contains(-1_000_000_000));

        int minValue = -1_000_000_000;
        int maxValue = 1_000_000_000;
        LongInterval largeInterval = LongInterval.fromToBy(minValue, maxValue, 10);

        assertTrue(largeInterval.containsAll(
                maxValue - 10,
                maxValue - 100,
                maxValue - 1000,
                maxValue - 10000));
        assertTrue(largeInterval.contains(minValue + 10));
    }

    @Test
    public void largeReverseUnderflowTest()
    {
        LongInterval reverse = LongInterval.fromToBy(Integer.MAX_VALUE, Integer.MIN_VALUE + 10, -10);
        assertFalse(reverse.contains(Integer.MIN_VALUE + 10));
        assertEquals(Integer.MAX_VALUE, reverse.getFirst());
        int expectedLast = -2_147_483_633;
        assertEquals(expectedLast, reverse.getLast());
        assertTrue(reverse.contains(Integer.MAX_VALUE));
        assertTrue(reverse.contains(7));
        assertTrue(reverse.contains(-3));
        assertTrue(reverse.contains(expectedLast));
        assertTrue(reverse.contains(expectedLast + 1000));
        assertEquals(214_748_364, reverse.indexOf(7));
        assertEquals(214_748_365, reverse.indexOf(-3));
        assertEquals(429_496_728, reverse.indexOf(expectedLast));
        assertEquals(429_496_728, reverse.lastIndexOf(expectedLast));
        assertEquals(429_496_728, reverse.binarySearch(expectedLast));
        int expectedAtIndex300Million = -852_516_353;
        assertTrue(reverse.contains(expectedAtIndex300Million));
        assertEquals(300_000_000, reverse.indexOf(expectedAtIndex300Million));
        assertEquals(300_000_000, reverse.lastIndexOf(expectedAtIndex300Million));
        assertEquals(300_000_000, reverse.binarySearch(expectedAtIndex300Million));
        int expectedAtIndex400Million = -1_852_516_353;
        assertTrue(reverse.contains(expectedAtIndex400Million));
        assertEquals(400_000_000, reverse.indexOf(expectedAtIndex400Million));
        assertEquals(400_000_000, reverse.lastIndexOf(expectedAtIndex400Million));
        assertEquals(400_000_000, reverse.binarySearch(expectedAtIndex400Million));
    }

    @Test
    public void forwardOverflowTest()
    {
        int from = Integer.MAX_VALUE - 10;
        int second = Integer.MAX_VALUE - 2;
        long expected = (long) from + (long) second;
        LongInterval interval = LongInterval.fromToBy(from, Integer.MAX_VALUE, 8);
        assertEquals(2, interval.size());
        assertEquals(LongLists.mutable.with(from, second), interval);
        assertEquals(1, interval.count(each -> each == second));
        MutableLong result = new MutableLong();
        interval.forEach(result::add);
        assertEquals(expected, result.longValue());
        result.clear();
        interval.forEachWithIndex((each, index) -> result.add(each + index));
        assertEquals(expected + 1L, result.longValue());
        assertEquals(expected, interval.injectInto(new MutableLong(), MutableLong::add).longValue());
        assertEquals(
                expected + 1L,
                interval
                        .injectIntoWithIndex(new MutableLong(), (value, each, index) -> value.add(each + index))
                        .longValue());
    }

    @Test
    public void reverseOverflowTest()
    {
        int from = Integer.MIN_VALUE + 10;
        int second = Integer.MIN_VALUE + 2;
        long expected = (long) from + (long) second;
        LongInterval interval = LongInterval.fromToBy(from, Integer.MIN_VALUE, -8);
        assertEquals(2, interval.size());
        assertEquals(LongLists.mutable.with(from, second), interval);
        assertEquals(1, interval.count(each -> each == second));
        assertEquals(expected, interval.sum());
        MutableLong result = new MutableLong();
        interval.forEach(result::add);
        assertEquals(expected, result.longValue());
        result.clear();
        interval.forEachWithIndex((each, index) -> result.add(each + index));
        assertEquals(expected + 1L, result.longValue());
        assertEquals(expected, interval.injectInto(new MutableLong(), MutableLong::add).longValue());
        assertEquals(
                expected + 1L,
                interval
                        .injectIntoWithIndex(new MutableLong(), (value, each, index) -> value.add(each + index))
                        .longValue());
    }

    @Test
    public void intervalIterator()
    {
        LongInterval zero = LongInterval.zero();
        LongIterator zeroIterator = zero.longIterator();
        assertTrue(zeroIterator.hasNext());
        assertEquals(0, zeroIterator.next());
        assertFalse(zeroIterator.hasNext());
        LongInterval oneToFive = LongInterval.oneTo(5);
        LongIterator oneToFiveIterator = oneToFive.longIterator();
        for (int i = 1; i < 6; i++)
        {
            assertTrue(oneToFiveIterator.hasNext());
            assertEquals(i, oneToFiveIterator.next());
        }
        assertThrows(NoSuchElementException.class, oneToFiveIterator::next);
        LongInterval threeToNegativeThree = LongInterval.fromTo(3, -3);
        LongIterator threeToNegativeThreeIterator = threeToNegativeThree.longIterator();
        for (int i = 3; i > -4; i--)
        {
            assertTrue(threeToNegativeThreeIterator.hasNext());
            assertEquals(i, threeToNegativeThreeIterator.next());
        }
        assertThrows(NoSuchElementException.class, threeToNegativeThreeIterator::next);
    }

    @Test
    public void intervalIteratorForLargeValues()
    {
        var values1 = LongLists.mutable.empty();
        LongIterator iterator1 = LongInterval.zeroToBy(9_999_999_999L, 1_000_000_000L).longIterator();
        while (iterator1.hasNext())
        {
            values1.add(iterator1.next());
        }

        assertEquals(
                LongLists.immutable.of(
                        0, 1_000_000_000L, 2_000_000_000L, 3_000_000_000L, 4_000_000_000L,
                        5_000_000_000L, 6_000_000_000L, 7_000_000_000L, 8_000_000_000L, 9_000_000_000L),
                values1);

        var values2 = LongLists.mutable.empty();

        LongIterator iterator2 = LongInterval.zeroToBy(-9_999_999_999L, -1_000_000_000L).longIterator();
        while (iterator2.hasNext())
        {
            values2.add(iterator2.next());
        }

        assertEquals(
                LongLists.immutable.of(
                        0, -1_000_000_000L, -2_000_000_000L, -3_000_000_000L, -4_000_000_000L,
                        -5_000_000_000L, -6_000_000_000L, -7_000_000_000L, -8_000_000_000L, -9_000_000_000L),
                values2);
    }

    @Test
    public void forEachWithIndex()
    {
        IntegerSum sum = new IntegerSum(0);
        LongInterval.oneTo(5).forEachWithIndex((each, index) -> sum.add(each + index));
        assertEquals(25, sum.getIntSum());
        IntegerSum zeroSum = new IntegerSum(0);
        LongInterval.fromTo(0, -4).forEachWithIndex((each, index) -> zeroSum.add(each + index));
        assertEquals(0, zeroSum.getIntSum());
    }

    @Test
    public void forEach_with_same_start_and_end_with_negative_step()
    {
        MutableLong counter = new MutableLong(0);

        LongInterval interval = LongInterval.fromToBy(2, 2, -2);
        interval.forEach((LongProcedure) each -> counter.add(1));

        assertEquals(1, counter.toLong().intValue());
    }

    @Test
    public void getFirst()
    {
        assertEquals(10, LongInterval.fromTo(10, -10).by(-5).getFirst());
        assertEquals(-10, LongInterval.fromTo(-10, 10).by(5).getFirst());
        assertEquals(0, LongInterval.zero().getFirst());
    }

    @Test
    public void getLast()
    {
        assertEquals(-10, LongInterval.fromTo(10, -10).by(-5).getLast());
        assertEquals(-10, LongInterval.fromTo(10, -12).by(-5).getLast());
        assertEquals(10, LongInterval.fromTo(-10, 10).by(5).getLast());
        assertEquals(10, LongInterval.fromTo(-10, 12).by(5).getLast());
        assertEquals(0, LongInterval.zero().getLast());
    }

    @Test
    public void indexOf()
    {
        LongInterval interval = LongInterval.fromTo(-10, 12).by(5);
        assertEquals(0, interval.indexOf(-10));
        assertEquals(1, interval.indexOf(-5));
        assertEquals(2, interval.indexOf(0));
        assertEquals(3, interval.indexOf(5));
        assertEquals(4, interval.indexOf(10));

        assertEquals(-1, interval.indexOf(-15));
        assertEquals(-1, interval.indexOf(-11));
        assertEquals(-1, interval.indexOf(-9));
        assertEquals(-1, interval.indexOf(11));
        assertEquals(-1, interval.indexOf(15));

        LongInterval backwardsLongInterval = LongInterval.fromTo(10, -12).by(-5);
        assertEquals(0, backwardsLongInterval.indexOf(10));
        assertEquals(1, backwardsLongInterval.indexOf(5));
        assertEquals(2, backwardsLongInterval.indexOf(0));
        assertEquals(3, backwardsLongInterval.indexOf(-5));
        assertEquals(4, backwardsLongInterval.indexOf(-10));

        assertEquals(-1, backwardsLongInterval.indexOf(15));
        assertEquals(-1, backwardsLongInterval.indexOf(11));
        assertEquals(-1, backwardsLongInterval.indexOf(9));
        assertEquals(-1, backwardsLongInterval.indexOf(-11));
        assertEquals(-1, backwardsLongInterval.indexOf(-15));
    }

    @Test
    public void lastIndexOf()
    {
        LongInterval interval = LongInterval.fromTo(-10, 12).by(5);
        assertEquals(0, interval.lastIndexOf(-10));
        assertEquals(1, interval.lastIndexOf(-5));
        assertEquals(2, interval.lastIndexOf(0));
        assertEquals(3, interval.lastIndexOf(5));
        assertEquals(4, interval.lastIndexOf(10));

        assertEquals(-1, interval.lastIndexOf(-15));
        assertEquals(-1, interval.lastIndexOf(-11));
        assertEquals(-1, interval.lastIndexOf(-9));
        assertEquals(-1, interval.lastIndexOf(11));
        assertEquals(-1, interval.lastIndexOf(15));

        LongInterval backwardsLongInterval = LongInterval.fromTo(10, -12).by(-5);
        assertEquals(0, backwardsLongInterval.lastIndexOf(10));
        assertEquals(1, backwardsLongInterval.lastIndexOf(5));
        assertEquals(2, backwardsLongInterval.lastIndexOf(0));
        assertEquals(3, backwardsLongInterval.lastIndexOf(-5));
        assertEquals(4, backwardsLongInterval.lastIndexOf(-10));

        assertEquals(-1, backwardsLongInterval.lastIndexOf(15));
        assertEquals(-1, backwardsLongInterval.lastIndexOf(11));
        assertEquals(-1, backwardsLongInterval.lastIndexOf(9));
        assertEquals(-1, backwardsLongInterval.lastIndexOf(-11));
        assertEquals(-1, backwardsLongInterval.lastIndexOf(-15));
    }

    @Test
    public void get()
    {
        LongInterval interval = LongInterval.fromTo(-10, 12).by(5);
        assertEquals(-10, interval.get(0));
        assertEquals(-5, interval.get(1));
        assertEquals(0, interval.get(2));
        assertEquals(5, interval.get(3));
        assertEquals(10, interval.get(4));

        assertThrows(IndexOutOfBoundsException.class, () -> interval.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> interval.get(5));
    }

    @Test
    public void containsAll()
    {
        assertTrue(LongInterval.fromTo(1, 3).containsAll(1, 2, 3));
        assertFalse(LongInterval.fromTo(1, 3).containsAll(1, 2, 4));
    }

    @Test
    public void containsAllIterable()
    {
        assertTrue(LongInterval.fromTo(1, 3).containsAll(LongInterval.fromTo(1, 3)));
        assertFalse(LongInterval.fromTo(1, 3).containsAll(LongInterval.fromTo(1, 4)));
    }

    @Test
    public void distinct()
    {
        assertSame(this.longInterval, this.longInterval.distinct());
    }

    @Test
    public void asReversed()
    {
        MutableLongList list = LongLists.mutable.empty();
        list.addAll(this.longInterval.asReversed());
        assertEquals(LongLists.mutable.with(3, 2, 1), list);
    }

    @Test
    public void zip()
    {
        LongInterval interval = LongInterval.oneTo(3);
        ImmutableList<LongObjectPair<String>> zipped = interval.zip(interval.collect(Long::toString));
        ImmutableList<LongObjectPair<String>> zippedLazy = interval.zip(interval.asLazy().collect(Long::toString));
        ImmutableList<LongObjectPair<String>> expected = Lists.immutable.with(
                PrimitiveTuples.pair(1L, "1"),
                PrimitiveTuples.pair(2L, "2"),
                PrimitiveTuples.pair(3L, "3"));
        assertEquals(expected, zipped);
        assertEquals(expected, zippedLazy);
        Verify.assertEmpty(interval.zip(Lists.mutable.empty()));
        assertEquals(Lists.immutable.with(PrimitiveTuples.pair(1L, "1")), interval.zip(Lists.mutable.with("1")));
    }

    @Test
    public void zipInt()
    {
        LongInterval interval = LongInterval.oneTo(3);
        ImmutableList<LongLongPair> zipped = interval.zipLong(interval.toReversed());
        ImmutableList<LongLongPair> zippedLazy = interval.zipLong(interval.asReversed());
        ImmutableList<LongLongPair> expected = Lists.immutable.with(
                PrimitiveTuples.pair(1L, 3L),
                PrimitiveTuples.pair(2L, 2L),
                PrimitiveTuples.pair(3L, 1L));
        assertEquals(expected, zipped);
        assertEquals(expected, zippedLazy);
        Verify.assertEmpty(interval.zipLong(LongLists.mutable.empty()));
        assertEquals(Lists.immutable.with(PrimitiveTuples.pair(1L, 3L)), interval.zipLong(LongLists.mutable.with(3)));
    }

    @Test
    public void primitiveStream()
    {
        assertEquals(Lists.mutable.of(1L, 2L, 3L, 4L), LongInterval.oneTo(4L).primitiveStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(0L, 2L, 4L), LongInterval.fromToBy(0L, 5L, 2L).primitiveStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(5L, 3L, 1L), LongInterval.fromToBy(5L, 0L, -2L).primitiveStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(10L, 15L, 20L, 25L, 30L), LongInterval.fromToBy(10L, 30L, 5L).primitiveStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(30L, 25L, 20L, 15L, 10L), LongInterval.fromToBy(30L, 10L, -5L).primitiveStream().boxed().collect(Collectors.toList()));
    }

    @Test
    public void primitiveParallelStream()
    {
        assertEquals(Lists.mutable.of(1L, 2L, 3L, 4L), LongInterval.oneTo(4).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(0L, 2L, 4L), LongInterval.fromToBy(0, 5, 2).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(5L, 3L, 1L, -1L, -3L), LongInterval.fromToBy(5, -4, -2).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(10L, 15L, 20L, 25L, 30L), LongInterval.fromToBy(10, 30, 5).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(30L, 25L, 20L, 15L, 10L), LongInterval.fromToBy(30, 10, -5).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(-1L, 10L, 21L, 32L, 43L, 54L, 65L, 76L, 87L, 98L), LongInterval.fromToBy(-1, 100, 11).primitiveParallelStream().boxed().collect(Collectors.toList()));
    }

    @Test
    public void toImmutable()
    {
        LongInterval interval = LongInterval.oneTo(5);
        assertSame(interval, interval.toImmutable());
    }

    @Test
    public void newWith()
    {
        LongInterval interval = LongInterval.oneTo(4);
        ImmutableLongList list = interval.newWith(5);
        assertNotSame(interval, list);
        assertEquals(LongInterval.oneTo(5), list);
    }

    @Test
    public void newWithout()
    {
        LongInterval interval = LongInterval.oneTo(5);
        ImmutableLongList list = interval.newWithout(5);
        assertNotSame(interval, list);
        assertEquals(LongInterval.oneTo(4), list);
    }

    @Test
    public void newWithAll()
    {
        LongInterval interval = LongInterval.oneTo(2);
        ImmutableLongList list = interval.newWithAll(LongInterval.fromTo(3, 5));
        assertNotSame(interval, list);
        assertEquals(LongInterval.oneTo(5), list);
    }

    @Test
    public void newWithoutAll()
    {
        LongInterval interval = LongInterval.oneTo(5);
        ImmutableLongList list = interval.newWithoutAll(LongInterval.fromTo(3, 5));
        assertNotSame(interval, list);
        assertEquals(LongInterval.oneTo(2), list);
    }
}
