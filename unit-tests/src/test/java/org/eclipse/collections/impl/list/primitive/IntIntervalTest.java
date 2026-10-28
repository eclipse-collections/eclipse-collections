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

import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LazyIntIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.procedure.primitive.IntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.iterator.IntIterator;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.api.tuple.primitive.IntObjectPair;
import org.eclipse.collections.impl.ThrowingAppendable;
import org.eclipse.collections.impl.bag.mutable.primitive.IntHashBag;
import org.eclipse.collections.impl.block.factory.primitive.IntPredicates;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.math.MutableInteger;
import org.eclipse.collections.impl.math.MutableLong;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;
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

public class IntIntervalTest
{
    private final IntInterval intInterval = IntInterval.oneTo(3);

    @Test
    public void fromAndToAndBy()
    {
        IntInterval interval = IntInterval.from(1);
        IntInterval interval2 = interval.to(10);
        IntInterval interval3 = interval2.by(2);
        Verify.assertEqualsAndHashCode(interval, IntInterval.fromTo(1, 1));
        Verify.assertEqualsAndHashCode(interval2, IntInterval.fromTo(1, 10));
        Verify.assertEqualsAndHashCode(interval3, IntInterval.fromToBy(1, 10, 2));
        Verify.assertSize(Integer.MAX_VALUE, IntInterval.fromTo(Integer.MIN_VALUE + 1, -1));
        Verify.assertSize(Integer.MAX_VALUE, IntInterval.fromTo(1, Integer.MAX_VALUE));
        Verify.assertSize(21, IntInterval.from(10).to(-10).by(-1));

        assertThrows(IllegalArgumentException.class, () -> IntInterval.fromTo(Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class, () -> IntInterval.fromTo(-1, Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class, () -> IntInterval.fromToBy(Integer.MIN_VALUE, Integer.MAX_VALUE, 2));
        assertEquals(IntInterval.fromTo(Integer.MIN_VALUE + 1, -1).size(), IntInterval.oneTo(Integer.MAX_VALUE).size());

        assertEquals(IntLists.mutable.with(0), IntInterval.fromToBy(0, 2, 3));
        assertEquals(IntLists.mutable.with(0), IntInterval.fromToBy(0, -2, -3));
        assertEquals(IntLists.mutable.with(1_000_000_000), IntInterval.fromToBy(1_000_000_000, 2_000_000_000, 1_500_000_000));
        assertEquals(IntLists.mutable.with(-1_000_000_000), IntInterval.fromToBy(-1_000_000_000, -2_000_000_000, -1_500_000_000));
        assertEquals(IntLists.mutable.with(Integer.MIN_VALUE), IntInterval.fromToBy(Integer.MIN_VALUE, Integer.MIN_VALUE + 10, 20));
        assertEquals(IntLists.mutable.with(Integer.MAX_VALUE), IntInterval.fromToBy(Integer.MAX_VALUE, Integer.MAX_VALUE - 10, -20));
    }

    @Test
    public void fromToBy_throws_step_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> IntInterval.fromToBy(0, 0, 0));
    }

    @Test
    public void fromToBy_throws_on_illegal_step()
    {
        assertThrows(IllegalArgumentException.class, () -> IntInterval.fromToBy(5, 0, 1));
    }

    @Test
    public void fromAndToAndBy_throws_step_is_incorrect()
    {
        assertThrows(IllegalArgumentException.class, () -> IntInterval.from(10).to(-10).by(1));
        assertThrows(IllegalArgumentException.class, () -> IntInterval.from(-10).to(10).by(-1));
    }

    @Test
    public void fromToBy_with_same_start_and_end_with_negative_step()
    {
        MutableIntList integers = IntInterval.fromToBy(2, 2, -2).toList();

        assertEquals(1, integers.size());
        assertEquals(2, integers.getFirst());
    }

    @Test
    public void fromToBy_with_same_start_and_end_with_negative_step2()
    {
        MutableIntList integers = IntInterval.fromToBy(2, 2, -1).toList();

        assertEquals(1, integers.size());
        assertEquals(2, integers.getFirst());
    }

    @Test
    public void oneToBy_throws_step_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> IntInterval.oneToBy(1, 0));
    }

    @Test
    public void oneToBy_throws_count_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> IntInterval.oneToBy(0, 1));
    }

    @Test
    public void zeroToBy_throws_step_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> IntInterval.zeroToBy(0, 0));
    }

    @Test
    public void equalsAndHashCode()
    {
        IntInterval interval1 = IntInterval.oneTo(5);
        IntInterval interval2 = IntInterval.oneTo(5);
        IntInterval interval3 = IntInterval.zeroTo(5);
        Verify.assertPostSerializedEqualsAndHashCode(interval1);
        Verify.assertEqualsAndHashCode(interval1, interval2);
        assertNotEquals(interval1, interval3);
        assertNotEquals(interval3, interval1);

        Verify.assertEqualsAndHashCode(IntInterval.fromToBy(1, 5, 2), IntInterval.fromToBy(1, 6, 2));
        Verify.assertEqualsAndHashCode(IntArrayList.newListWith(1, 2, 3), IntInterval.fromTo(1, 3));
        Verify.assertEqualsAndHashCode(IntArrayList.newListWith(3, 2, 1), IntInterval.fromTo(3, 1));

        assertNotEquals(IntArrayList.newListWith(1, 2, 3, 4), IntInterval.fromTo(1, 3));
        assertNotEquals(IntArrayList.newListWith(1, 2, 4), IntInterval.fromTo(1, 3));
        assertNotEquals(IntArrayList.newListWith(3, 2, 0), IntInterval.fromTo(3, 1));

        Verify.assertEqualsAndHashCode(IntArrayList.newListWith(-1, -2, -3), IntInterval.fromTo(-1, -3));

        Verify.assertEqualsAndHashCode(IntArrayList.newListWith(1), IntInterval.fromToBy(1, 1, 1));
        Verify.assertEqualsAndHashCode(IntArrayList.newListWith(1), IntInterval.fromToBy(1, 1, 2));
        Verify.assertEqualsAndHashCode(IntArrayList.newListWith(-1), IntInterval.fromToBy(-1, -1, -1));
        Verify.assertEqualsAndHashCode(IntArrayList.newListWith(-1), IntInterval.fromToBy(-1, -1, -2));

        IntInterval interval4 = IntInterval.fromTo(-1, -1_000);
        IntInterval interval5 = IntInterval.fromTo(-1, -1_000);
        IntInterval interval6 = IntInterval.fromTo(0, -999);
        Verify.assertPostSerializedEqualsAndHashCode(interval4);
        Verify.assertEqualsAndHashCode(interval4, interval5);
        assertNotEquals(interval4, interval6);
        assertNotEquals(interval6, interval4);
    }

    @Test
    public void sumIntInterval()
    {
        assertEquals(15, (int) IntInterval.oneTo(5).sum());
    }

    @Test
    public void maxIntInterval()
    {
        int value = IntInterval.oneTo(5).max();
        assertEquals(5, value);
    }

    @Test
    public void iterator()
    {
        IntIterator iterator = this.intInterval.intIterator();
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
        IntIterator iterator = this.intInterval.intIterator();
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
        this.intInterval.forEach(each -> sum[0] += each);

        assertEquals(6L, sum[0]);
    }

    @Test
    public void each()
    {
        MutableIntList list1 = IntLists.mutable.empty();
        IntInterval interval1 = IntInterval.oneTo(5);
        interval1.each(list1::add);
        assertEquals(list1, interval1);
        IntInterval interval2 = IntInterval.fromTo(5, 1);
        MutableIntList list2 = IntLists.mutable.empty();
        interval2.each(list2::add);
        assertEquals(list2, interval2);
    }

    @Test
    public void injectInto()
    {
        IntInterval intInterval1 = IntInterval.oneTo(3);
        MutableInteger result = intInterval1.injectInto(new MutableInteger(0), MutableInteger::add);
        assertEquals(new MutableInteger(6), result);
        IntInterval intInterval2 = IntInterval.fromTo(3, 1);
        MutableInteger result2 = intInterval2.injectInto(new MutableInteger(0), MutableInteger::add);
        assertEquals(new MutableInteger(6), result2);
    }

    @Test
    public void injectIntoWithIndex()
    {
        IntInterval interval1 = IntInterval.oneTo(3);
        MutableInteger result1 = this.intInterval.injectIntoWithIndex(new MutableInteger(0), (object, value, index) -> object.add(value * interval1.get(index)));
        assertEquals(new MutableInteger(14), result1);
        IntInterval interval2 = IntInterval.fromTo(3, 1);
        MutableInteger result2 = interval2.injectIntoWithIndex(new MutableInteger(0), (object, value, index) -> object.add(value * this.intInterval.get(index)));
        assertEquals(new MutableInteger(10), result2);
    }

    @Test
    public void injectIntoOnFromToBySameStartEndNegativeStepInterval()
    {
        IntInterval interval = IntInterval.fromToBy(2, 2, -2);

        assertEquals(new MutableInteger(0), interval.injectInto(new MutableInteger(-2), MutableInteger::add));
    }

    @Test
    public void chunk()
    {
        IntInterval interval1 = IntInterval.fromToBy(0, 5, 1);
        MutableList<IntInterval> expected1 = Lists.mutable.with(
                IntInterval.fromToBy(0, 1, 1),
                IntInterval.fromToBy(2, 3, 1),
                IntInterval.fromToBy(4, 5, 1));
        assertEquals(expected1, interval1.chunk(2));

        IntInterval interval2 = IntInterval.fromToBy(0, -5, -1);
        MutableList<IntInterval> expected2 = Lists.mutable.with(
                IntInterval.fromToBy(0, -1, -1),
                IntInterval.fromToBy(-2, -3, -1),
                IntInterval.fromToBy(-4, -5, -1));
        assertEquals(expected2, interval2.chunk(2));

        IntInterval interval3 = IntInterval.fromToBy(0, 6, 1);
        MutableList<IntInterval> expected3 = Lists.mutable.with(
                IntInterval.fromToBy(0, 1, 1),
                IntInterval.fromToBy(2, 3, 1),
                IntInterval.fromToBy(4, 5, 1),
                IntInterval.fromToBy(6, 6, 1));
        assertEquals(expected3, interval3.chunk(2));

        IntInterval interval4 = IntInterval.fromToBy(0, -6, -1);
        MutableList<IntInterval> expected4 = Lists.mutable.with(
                IntInterval.fromToBy(0, -1, -1),
                IntInterval.fromToBy(-2, -3, -1),
                IntInterval.fromToBy(-4, -5, -1),
                IntInterval.fromToBy(-6, -6, -1));
        RichIterable<IntIterable> actual4 = interval4.chunk(2);
        assertEquals(expected4, actual4);

        IntInterval interval5 = IntInterval.fromToBy(0, 6, 1);
        MutableList<IntInterval> expected5 = Lists.mutable.with(IntInterval.fromToBy(0, 6, 1));
        assertEquals(expected5, interval5.chunk(7));

        IntInterval interval6 = IntInterval.fromToBy(0, -6, -1);
        MutableList<IntInterval> expected6 = Lists.mutable.with(IntInterval.fromToBy(0, -6, -1));
        assertEquals(expected6, interval6.chunk(7));

        IntInterval interval7 = IntInterval.fromToBy(0, 6, 1);
        MutableList<IntInterval> expected7 = Lists.mutable.with(IntInterval.fromToBy(0, 6, 1));
        assertEquals(expected7, interval7.chunk(8));

        IntInterval interval8 = IntInterval.fromToBy(0, -6, -1);
        MutableList<IntInterval> expected8 = Lists.mutable.with(IntInterval.fromToBy(0, -6, -1));
        assertEquals(expected8, interval8.chunk(8));

        IntInterval interval9 = IntInterval.fromToBy(0, 9, 4);
        MutableList<IntIterable> expected9 = Lists.mutable.with(
                IntLists.mutable.with(0, 4),
                IntLists.mutable.with(8));
        assertEquals(expected9, interval9.chunk(2));

        IntInterval interval10 = IntInterval.fromToBy(0, -9, -4);
        MutableList<IntIterable> expected10 = Lists.mutable.with(
                IntLists.mutable.with(0, -4),
                IntLists.mutable.with(-8));
        assertEquals(expected10, interval10.chunk(2));

        IntInterval interval11 = IntInterval.fromToBy(0, 5, 3);
        MutableList<IntIterable> expected11 = Lists.mutable.with(IntLists.mutable.with(0, 3));
        assertEquals(expected11, interval11.chunk(3));

        IntInterval interval12 = IntInterval.fromToBy(0, -5, -3);
        MutableList<IntIterable> expected12 = Lists.mutable.with(IntLists.mutable.with(0, -3));
        assertEquals(expected12, interval12.chunk(3));

        assertThrows(IllegalArgumentException.class, () -> interval12.chunk(0));
        assertThrows(IllegalArgumentException.class, () -> interval12.chunk(-1));
    }

    @Test
    public void chunkOnSingleValueInterval()
    {
        MutableList<IntIterable> expected = Lists.mutable.with(IntLists.mutable.with(1));
        assertEquals(expected, IntInterval.oneTo(1).chunk(1));
        assertEquals(expected, IntInterval.oneTo(1).chunk(2));
        assertEquals(expected, IntInterval.oneTo(1).chunk(Integer.MAX_VALUE));

        assertEquals(Lists.mutable.with(IntLists.mutable.with(0)), IntInterval.fromToBy(0, 4, 5).chunk(2));
        assertEquals(Lists.mutable.with(IntLists.mutable.with(7)), IntInterval.fromTo(7, 7).chunk(3));

        assertEquals(IntLists.mutable.with(1).chunk(2), IntInterval.oneTo(1).chunk(2));
    }

    @Test
    public void spliteratorOfSingleValueIntervalWithNegativeStep()
    {
        IntInterval interval = IntInterval.fromToBy(5, 5, -1);
        assertArrayEquals(new int[]{5}, take(interval.spliterator(), 4));
        assertEquals(Comparator.reverseOrder(), interval.spliterator().getComparator());

        assertArrayEquals(new int[]{5, 4, 3, 2, 1}, take(IntInterval.fromToBy(5, 1, -1).spliterator(), 10));
        assertArrayEquals(new int[]{1, 4, 7, 10}, take(IntInterval.fromToBy(1, 10, 3).spliterator(), 10));
    }

    private static int[] take(Spliterator.OfInt spliterator, int limit)
    {
        MutableIntList values = IntLists.mutable.empty();
        boolean advanced = true;
        while (advanced && values.size() < limit)
        {
            advanced = spliterator.tryAdvance((int each) -> values.add(each));
        }
        return values.toArray();
    }

    @Test
    public void intIteratorOnSingleValueIntervalWithNegativeStep()
    {
        IntInterval interval = IntInterval.fromToBy(5, 5, -1);
        assertEquals(1, interval.size());

        IntIterator iterator = interval.intIterator();
        assertTrue(iterator.hasNext());
        assertEquals(5, iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(Lists.mutable.with(IntLists.mutable.with(5)), interval.chunk(2));
        assertEquals(IntLists.mutable.with(5).chunk(2), interval.chunk(2));
    }

    @Test
    public void size()
    {
        Verify.assertSize(3, this.intInterval);
        // Positive Ranges
        Verify.assertSize(10, IntInterval.zeroTo(9));
        Verify.assertSize(2_000_000_000, IntInterval.oneTo(2_000_000_000));
        Verify.assertSize(200_000_000, IntInterval.oneTo(2_000_000_000).by(10));
        Verify.assertSize(2_000_000_000, IntInterval.fromTo(2_000_000_000, 1).by(-1));
        Verify.assertSize(500_000_000, IntInterval.oneTo(2_000_000_000).by(4));
        Verify.assertSize(222_222_223, IntInterval.oneTo(2_000_000_000).by(9));
        Verify.assertSize(2, IntInterval.fromToBy(Integer.MAX_VALUE - 10, Integer.MAX_VALUE, 8));

        // Negative Ranges
        Verify.assertSize(10, IntInterval.zeroTo(-9));
        Verify.assertSize(11, IntInterval.oneTo(-9));
        Verify.assertSize(10, IntInterval.fromTo(0, -9));
        Verify.assertSize(2_000_000_000, IntInterval.fromTo(-1, -2_000_000_000));
        Verify.assertSize(200_000_000, IntInterval.fromTo(-1, -2_000_000_000).by(-10));
        Verify.assertSize(2_000_000_000, IntInterval.fromTo(-2_000_000_000, -1).by(1));
        Verify.assertSize(500_000_000, IntInterval.fromTo(-1, -2_000_000_000).by(-4));
        Verify.assertSize(222_222_223, IntInterval.fromTo(-1, -2_000_000_000).by(-9));
        // Overlapping Ranges
        Verify.assertSize(21, IntInterval.fromTo(10, -10));
        Verify.assertSize(5, IntInterval.fromTo(10, -10).by(-5));
        Verify.assertSize(5, IntInterval.fromTo(-10, 10).by(5));
        Verify.assertSize(2_000_000_001, IntInterval.fromTo(1_000_000_000, -1_000_000_000));
        Verify.assertSize(200_000_001, IntInterval.fromTo(1_000_000_000, -1_000_000_000).by(-10));
    }

    @Test
    public void subList()
    {
        IntInterval interval = IntInterval.fromToBy(1, 10, 2);
        assertEquals(IntLists.immutable.with(3, 5, 7), interval.subList(1, 4));
    }

    @Test
    public void dotProduct()
    {
        IntInterval interval = IntInterval.oneTo(3);
        assertEquals(14, this.intInterval.dotProduct(interval));
    }

    @Test
    public void dotProduct_throwsOnListsOfDifferentSizes()
    {
        IntInterval interval = IntInterval.oneTo(4);
        assertThrows(IllegalArgumentException.class, () -> this.intInterval.dotProduct(interval));
    }

    @Test
    public void empty()
    {
        assertTrue(this.intInterval.notEmpty());
        Verify.assertNotEmpty(this.intInterval);
    }

    @Test
    public void count()
    {
        assertEquals(2L, IntInterval.zeroTo(2).count(IntPredicates.greaterThan(0)));

        int count = IntInterval.fromToBy(Integer.MAX_VALUE - 10, Integer.MAX_VALUE, 8).count(IntPredicates.greaterThan(0));
        assertEquals(2, count);
    }

    @Test
    public void anySatisfy()
    {
        assertTrue(IntInterval.fromTo(-1, 2).anySatisfy(IntPredicates.greaterThan(0)));
        assertFalse(IntInterval.oneTo(2).anySatisfy(IntPredicates.equal(0)));
    }

    @Test
    public void allSatisfy()
    {
        assertFalse(IntInterval.zeroTo(2).allSatisfy(IntPredicates.greaterThan(0)));
        assertTrue(IntInterval.oneTo(3).allSatisfy(IntPredicates.greaterThan(0)));
    }

    @Test
    public void noneSatisfy()
    {
        assertFalse(IntInterval.zeroTo(2).noneSatisfy(IntPredicates.isEven()));
        assertTrue(IntInterval.evensFromTo(2, 10).noneSatisfy(IntPredicates.isOdd()));
    }

    @Test
    public void select()
    {
        Verify.assertSize(3, this.intInterval.select(IntPredicates.lessThan(4)));
        Verify.assertSize(2, this.intInterval.select(IntPredicates.lessThan(3)));
    }

    @Test
    public void reject()
    {
        Verify.assertSize(0, this.intInterval.reject(IntPredicates.lessThan(4)));
        Verify.assertSize(1, this.intInterval.reject(IntPredicates.lessThan(3)));
    }

    @Test
    public void detectIfNone()
    {
        assertEquals(1L, this.intInterval.detectIfNone(IntPredicates.lessThan(4), 0));
        assertEquals(0L, this.intInterval.detectIfNone(IntPredicates.greaterThan(3), 0));
    }

    @Test
    public void collect()
    {
        assertEquals(FastList.newListWith(0, 1, 2), this.intInterval.collect(parameter -> parameter - 1).toList());
    }

    @Test
    public void lazyCollectPrimitives()
    {
        assertEquals(BooleanLists.immutable.of(false, true, false), IntInterval.oneTo(3).asLazy().collectBoolean(e -> e % 2 == 0).toList());
        assertEquals(CharLists.immutable.of((char) 2, (char) 3, (char) 4), IntInterval.oneTo(3).asLazy().collectChar(e -> (char) (e + 1)).toList());
        assertEquals(ByteLists.immutable.of((byte) 2, (byte) 3, (byte) 4), IntInterval.oneTo(3).asLazy().collectByte(e -> (byte) (e + 1)).toList());
        assertEquals(ShortLists.immutable.of((short) 2, (short) 3, (short) 4), IntInterval.oneTo(3).asLazy().collectShort(e -> (short) (e + 1)).toList());
        assertEquals(IntLists.immutable.of(2, 3, 4), IntInterval.oneTo(3).asLazy().collectInt(e -> e + 1).toList());
        assertEquals(FloatLists.immutable.of(2.0f, 3.0f, 4.0f), IntInterval.oneTo(3).asLazy().collectFloat(e -> (float) (e + 1)).toList());
        assertEquals(LongLists.immutable.of(2L, 3L, 4L), IntInterval.oneTo(3).asLazy().collectLong(e -> (long) (e + 1)).toList());
        assertEquals(DoubleLists.immutable.of(2.0, 3.0, 4.0), IntInterval.oneTo(3).asLazy().collectDouble(e -> (double) (e + 1)).toList());
    }

    @Test
    public void binarySearch()
    {
        IntInterval interval1 = IntInterval.oneTo(3);
        assertEquals(-1, interval1.binarySearch(-1));
        assertEquals(-1, interval1.binarySearch(0));
        assertEquals(0, interval1.binarySearch(1));
        assertEquals(1, interval1.binarySearch(2));
        assertEquals(2, interval1.binarySearch(3));
        assertEquals(-4, interval1.binarySearch(4));
        assertEquals(-4, interval1.binarySearch(5));

        IntInterval interval2 = IntInterval.fromTo(7, 17).by(3);
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

        IntInterval interval3 = IntInterval.fromTo(-21, -11).by(5);
        assertEquals(-1, interval3.binarySearch(-22));
        assertEquals(0, interval3.binarySearch(-21));
        assertEquals(-2, interval3.binarySearch(-17));
        assertEquals(1, interval3.binarySearch(-16));
        assertEquals(-3, interval3.binarySearch(-15));
        assertEquals(2, interval3.binarySearch(-11));
        assertEquals(-4, interval3.binarySearch(-9));

        IntInterval interval4 = IntInterval.fromTo(50, 30).by(-10);
        assertEquals(-1, interval4.binarySearch(60));
        assertEquals(0, interval4.binarySearch(50));
        assertEquals(-2, interval4.binarySearch(45));
        assertEquals(1, interval4.binarySearch(40));
        assertEquals(-3, interval4.binarySearch(35));
        assertEquals(2, interval4.binarySearch(30));
        assertEquals(-4, interval4.binarySearch(25));

        IntInterval interval5 = IntInterval.fromTo(-30, -50).by(-10);
        assertEquals(-1, interval5.binarySearch(-20));
        assertEquals(0, interval5.binarySearch(-30));
        assertEquals(-2, interval5.binarySearch(-35));
        assertEquals(1, interval5.binarySearch(-40));
        assertEquals(-3, interval5.binarySearch(-47));
        assertEquals(2, interval5.binarySearch(-50));
        assertEquals(-4, interval5.binarySearch(-65));

        IntInterval interval6 = IntInterval.fromTo(27, -30).by(-9);
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

        IntInterval interval7 = IntInterval.fromTo(-1, 1).by(1);
        assertEquals(-1, interval7.binarySearch(-2));
        assertEquals(0, interval7.binarySearch(-1));
        assertEquals(1, interval7.binarySearch(0));
        assertEquals(2, interval7.binarySearch(1));
        assertEquals(-4, interval7.binarySearch(2));
    }

    @Test
    public void max()
    {
        assertEquals(9, IntInterval.oneTo(9).max());
        assertEquals(5, IntInterval.fromTo(5, 1).max());
        assertEquals(1, IntInterval.fromTo(-5, 1).max());
        assertEquals(1, IntInterval.fromTo(1, -5).max());
    }

    @Test
    public void min()
    {
        assertEquals(1, IntInterval.oneTo(9).min());
        assertEquals(1, IntInterval.fromTo(5, 1).min());
        assertEquals(-5, IntInterval.fromTo(-5, 1).min());
        assertEquals(-5, IntInterval.fromTo(1, -5).min());
    }

    @Test
    public void minIfEmpty()
    {
        assertEquals(1, IntInterval.oneTo(9).minIfEmpty(0));
    }

    @Test
    public void maxIfEmpty()
    {
        assertEquals(9, IntInterval.oneTo(9).maxIfEmpty(0));
    }

    @Test
    public void sum()
    {
        assertEquals(10L, IntInterval.oneTo(4).sum());
        assertEquals(5L, IntInterval.oneToBy(4, 3).sum());
        assertEquals(4L, IntInterval.oneToBy(4, 2).sum());
        assertEquals(-10L, IntInterval.fromTo(-1, -4).sum());
        assertEquals(-15L, IntInterval.fromToBy(-2, -10, -3).sum());

        assertEquals(-7L, IntInterval.fromToBy(-10, 10, 3).sum());

        assertEquals(
                3L * ((long) Integer.MAX_VALUE * 2L - 2L) / 2L,
                IntInterval.fromTo(Integer.MAX_VALUE - 2, Integer.MAX_VALUE).sum());
    }

    @Test
    public void average()
    {
        assertEquals(2.5, IntInterval.oneTo(4).average(), 0.0);
        assertEquals(5.0, IntInterval.oneToBy(9, 2).average(), 0.0);
        assertEquals(5.0, IntInterval.oneToBy(10, 2).average(), 0.0);
        assertEquals(-5.0, IntInterval.fromToBy(-1, -9, -2).average(), 0.0);

        assertEquals((double) Integer.MAX_VALUE - 1.5,
                IntInterval.fromTo(Integer.MAX_VALUE - 3, Integer.MAX_VALUE).average(), 0.0);
    }

    @Test
    public void median()
    {
        assertEquals(2.5, IntInterval.oneTo(4).median(), 0.0);
        assertEquals(5.0, IntInterval.oneToBy(9, 2).median(), 0.0);
        assertEquals(5.0, IntInterval.oneToBy(10, 2).median(), 0.0);
        assertEquals(-5.0, IntInterval.fromToBy(-1, -9, -2).median(), 0.0);

        assertEquals((double) Integer.MAX_VALUE - 1.5,
                IntInterval.fromTo(Integer.MAX_VALUE - 3, Integer.MAX_VALUE).median(), 0.0);
    }

    @Test
    public void toArray()
    {
        assertArrayEquals(new int[]{1, 2, 3, 4}, IntInterval.oneTo(4).toArray());
    }

    @Test
    public void toList()
    {
        assertEquals(IntArrayList.newListWith(1, 2, 3, 4), IntInterval.oneTo(4).toList());
    }

    @Test
    public void toSortedList()
    {
        assertEquals(IntArrayList.newListWith(1, 2, 3, 4), IntInterval.oneTo(4).toReversed().toSortedList());
    }

    @Test
    public void toSet()
    {
        assertEquals(IntHashSet.newSetWith(1, 2, 3, 4), IntInterval.oneTo(4).toSet());
    }

    @Test
    public void toBag()
    {
        assertEquals(IntHashBag.newBagWith(1, 2, 3, 4), IntInterval.oneTo(4).toBag());
    }

    @Test
    public void asLazy()
    {
        assertEquals(IntInterval.oneTo(5).toSet(), IntInterval.oneTo(5).asLazy().toSet());
        Verify.assertInstanceOf(LazyIntIterable.class, IntInterval.oneTo(5).asLazy());
    }

    @Test
    public void toSortedArray()
    {
        assertArrayEquals(new int[]{1, 2, 3, 4}, IntInterval.fromTo(4, 1).toSortedArray());
    }

    @Test
    public void testEquals()
    {
        IntInterval list1 = IntInterval.oneTo(4);
        IntInterval list2 = IntInterval.oneTo(4);
        IntInterval list3 = IntInterval.fromTo(4, 1);
        IntInterval list4 = IntInterval.fromTo(5, 8);
        IntInterval list5 = IntInterval.fromTo(5, 7);

        Verify.assertEqualsAndHashCode(list1, list2);
        Verify.assertPostSerializedEqualsAndHashCode(list1);
        assertNotEquals(list1, list3);
        assertNotEquals(list1, list4);
        assertNotEquals(list1, list5);
    }

    @Test
    public void testHashCode()
    {
        assertEquals(FastList.newListWith(1, 2, 3).hashCode(), IntInterval.oneTo(3).hashCode());
    }

    @Test
    public void testToString()
    {
        assertEquals("[1, 2, 3]", this.intInterval.toString());
    }

    @Test
    public void makeString()
    {
        assertEquals("1, 2, 3", this.intInterval.makeString());
        assertEquals("1/2/3", this.intInterval.makeString("/"));
        assertEquals(this.intInterval.toString(), this.intInterval.makeString("[", ", ", "]"));
    }

    @Test
    public void appendString()
    {
        StringBuilder appendable2 = new StringBuilder();
        this.intInterval.appendString(appendable2);
        assertEquals("1, 2, 3", appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        this.intInterval.appendString(appendable3, "/");
        assertEquals("1/2/3", appendable3.toString());
        StringBuilder appendable4 = new StringBuilder();
        this.intInterval.appendString(appendable4, "[", ", ", "]");
        assertEquals(this.intInterval.toString(), appendable4.toString());
    }

    @Test
    public void appendStringThrows()
    {
        assertThrows(
                RuntimeException.class,
                () -> this.intInterval.appendString(new ThrowingAppendable()));
        assertThrows(
                RuntimeException.class,
                () -> this.intInterval
                        .appendString(new ThrowingAppendable(), ", "));
        assertThrows(
                RuntimeException.class,
                () -> this.intInterval
                        .appendString(new ThrowingAppendable(), "[", ", ", "]"));
    }

    @Test
    public void toReversed()
    {
        IntInterval forward = IntInterval.oneTo(5);
        IntInterval reverse = forward.toReversed();
        assertEquals(IntArrayList.newListWith(5, 4, 3, 2, 1), reverse);
    }

    @Test
    public void evens()
    {
        IntInterval interval = IntInterval.evensFromTo(0, 10);
        int[] evens = {0, 2, 4, 6, 8, 10};
        int[] odds = {1, 3, 5, 7, 9};
        this.assertIntIntervalContainsAll(interval, evens);
        this.denyIntIntervalContainsAny(interval, odds);
        assertEquals(6, interval.size());

        IntInterval reverseIntInterval = IntInterval.evensFromTo(10, 0);
        this.assertIntIntervalContainsAll(reverseIntInterval, evens);
        this.denyIntIntervalContainsAny(reverseIntInterval, odds);
        assertEquals(6, reverseIntInterval.size());

        IntInterval negativeIntInterval = IntInterval.evensFromTo(-5, 5);
        int[] negativeEvens = {-4, -2, 0, 2, 4};
        int[] negativeOdds = {-3, -1, 1, 3};
        this.assertIntIntervalContainsAll(negativeIntInterval, negativeEvens);
        this.denyIntIntervalContainsAny(negativeIntInterval, negativeOdds);
        assertEquals(5, negativeIntInterval.size());

        IntInterval reverseNegativeIntInterval = IntInterval.evensFromTo(5, -5);
        this.assertIntIntervalContainsAll(reverseNegativeIntInterval, negativeEvens);
        this.denyIntIntervalContainsAny(reverseNegativeIntInterval, negativeOdds);
        assertEquals(5, reverseNegativeIntInterval.size());
    }

    private void assertIntIntervalContainsAll(IntInterval interval, int[] expectedValues)
    {
        for (int value : expectedValues)
        {
            assertTrue(interval.contains(value));
        }
    }

    private void denyIntIntervalContainsAny(IntInterval interval, int[] expectedValues)
    {
        for (int value : expectedValues)
        {
            assertFalse(interval.contains(value));
        }
    }

    @Test
    public void odds()
    {
        IntInterval interval1 = IntInterval.oddsFromTo(0, 10);
        assertTrue(interval1.containsAll(1, 3, 5, 7, 9));
        assertTrue(interval1.containsNone(2, 4, 6, 8));
        assertEquals(5, interval1.size());

        IntInterval reverseIntInterval1 = IntInterval.oddsFromTo(10, 0);
        assertTrue(reverseIntInterval1.containsAll(1, 3, 5, 7, 9));
        assertTrue(reverseIntInterval1.containsNone(0, 2, 4, 6, 8, 10));
        assertEquals(5, reverseIntInterval1.size());

        IntInterval interval2 = IntInterval.oddsFromTo(-5, 5);
        assertTrue(interval2.containsAll(-5, -3, -1, 1, 3, 5));
        assertTrue(interval2.containsNone(-4, -2, 0, 2, 4));
        assertEquals(6, interval2.size());

        IntInterval reverseIntInterval2 = IntInterval.oddsFromTo(5, -5);
        assertTrue(reverseIntInterval2.containsAll(-5, -3, -1, 1, 3, 5));
        assertTrue(reverseIntInterval2.containsNone(-4, -2, 0, 2, 4));
        assertEquals(6, reverseIntInterval2.size());
    }

    @Test
    public void intervalSize()
    {
        assertEquals(100, IntInterval.fromTo(1, 100).size());
        assertEquals(50, IntInterval.fromToBy(1, 100, 2).size());
        assertEquals(34, IntInterval.fromToBy(1, 100, 3).size());
        assertEquals(25, IntInterval.fromToBy(1, 100, 4).size());
        assertEquals(20, IntInterval.fromToBy(1, 100, 5).size());
        assertEquals(17, IntInterval.fromToBy(1, 100, 6).size());
        assertEquals(15, IntInterval.fromToBy(1, 100, 7).size());
        assertEquals(13, IntInterval.fromToBy(1, 100, 8).size());
        assertEquals(12, IntInterval.fromToBy(1, 100, 9).size());
        assertEquals(10, IntInterval.fromToBy(1, 100, 10).size());
        assertEquals(11, IntInterval.fromTo(0, 10).size());
        assertEquals(1, IntInterval.zero().size());
        assertEquals(11, IntInterval.fromTo(0, -10).size());
        assertEquals(3, IntInterval.evensFromTo(2, -2).size());
        assertEquals(2, IntInterval.oddsFromTo(2, -2).size());
        assertEquals(1, IntInterval.fromToBy(1_000_000_000, 2_000_000_000, 1_500_000_000).size());
        assertEquals(1, IntInterval.fromToBy(-1_000_000_000, -2_000_000_000, -1_500_000_000).size());
    }

    @Test
    public void contains()
    {
        assertTrue(IntInterval.zero().contains(0));
        assertTrue(IntInterval.oneTo(5).containsAll(1, 5));
        assertTrue(IntInterval.oneTo(5).containsNone(6, 7));
        assertFalse(IntInterval.oneTo(5).containsAll(1, 6));
        assertFalse(IntInterval.oneTo(5).containsNone(1, 6));
        assertFalse(IntInterval.oneTo(5).contains(0));
        assertTrue(IntInterval.fromTo(-1, -5).containsAll(-1, -5));
        assertFalse(IntInterval.fromTo(-1, -5).contains(1));

        assertTrue(IntInterval.zero().contains(Integer.valueOf(0)));
        assertFalse(IntInterval.oneTo(5).contains(Integer.valueOf(0)));
        assertFalse(IntInterval.fromTo(-1, -5).contains(Integer.valueOf(1)));

        IntInterval bigIntInterval = IntInterval.fromToBy(Integer.MIN_VALUE, Integer.MAX_VALUE, 1_000_000);
        assertTrue(bigIntInterval.contains(Integer.MIN_VALUE + 1_000_000));
        assertFalse(bigIntInterval.contains(Integer.MIN_VALUE + 1_000_001));
        assertTrue(bigIntInterval.contains(Integer.MIN_VALUE + (1_000_000 * 10)));
        assertFalse(bigIntInterval.contains(Integer.MIN_VALUE + (1_000_001 * 10)));
        assertTrue(bigIntInterval.contains(Integer.MIN_VALUE + (1_000_000 * 100)));
        assertFalse(bigIntInterval.contains(Integer.MIN_VALUE + (1_000_001 * 100)));
        assertTrue(
                IntInterval.fromToBy(1_000_000_000, 2_000_000_000, 1_500_000_000)
                        .contains(1_000_000_000));
        assertTrue(
                IntInterval.fromToBy(-1_000_000_000, -2_000_000_000, -1_500_000_000)
                        .contains(-1_000_000_000));

        int minValue = -1_000_000_000;
        int maxValue = 1_000_000_000;
        IntInterval largeInterval = IntInterval.fromToBy(minValue, maxValue, 10);

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
        IntInterval reverse = IntInterval.fromToBy(Integer.MAX_VALUE, Integer.MIN_VALUE + 10, -10);
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
        IntInterval interval = IntInterval.fromToBy(from, Integer.MAX_VALUE, 8);
        assertEquals(2, interval.size());
        assertEquals(IntLists.mutable.with(from, second), interval);
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
        IntInterval interval = IntInterval.fromToBy(from, Integer.MIN_VALUE, -8);
        assertEquals(2, interval.size());
        assertEquals(IntLists.mutable.with(from, second), interval);
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
        IntInterval zero = IntInterval.zero();
        IntIterator zeroIterator = zero.intIterator();
        assertTrue(zeroIterator.hasNext());
        assertEquals(0, zeroIterator.next());
        assertFalse(zeroIterator.hasNext());
        IntInterval oneToFive = IntInterval.oneTo(5);
        IntIterator oneToFiveIterator = oneToFive.intIterator();
        for (int i = 1; i < 6; i++)
        {
            assertTrue(oneToFiveIterator.hasNext());
            assertEquals(i, oneToFiveIterator.next());
        }
        assertThrows(NoSuchElementException.class, oneToFiveIterator::next);
        IntInterval threeToNegativeThree = IntInterval.fromTo(3, -3);
        IntIterator threeToNegativeThreeIterator = threeToNegativeThree.intIterator();
        for (int i = 3; i > -4; i--)
        {
            assertTrue(threeToNegativeThreeIterator.hasNext());
            assertEquals(i, threeToNegativeThreeIterator.next());
        }
        assertThrows(NoSuchElementException.class, threeToNegativeThreeIterator::next);
    }

    @Test
    public void forEachWithIndex()
    {
        IntegerSum sum = new IntegerSum(0);
        IntInterval.oneTo(5).forEachWithIndex((each, index) -> sum.add(each + index));
        assertEquals(25, sum.getIntSum());
        IntegerSum zeroSum = new IntegerSum(0);
        IntInterval.fromTo(0, -4).forEachWithIndex((each, index) -> zeroSum.add(each + index));
        assertEquals(0, zeroSum.getIntSum());
    }

    @Test
    public void forEach_with_same_start_and_end_with_negative_step()
    {
        MutableInteger counter = new MutableInteger(0);

        IntInterval interval = IntInterval.fromToBy(2, 2, -2);
        interval.forEach((IntProcedure) each -> counter.add(1));

        assertEquals(1, counter.toInteger().intValue());
    }

    @Test
    public void getFirst()
    {
        assertEquals(10, IntInterval.fromTo(10, -10).by(-5).getFirst());
        assertEquals(-10, IntInterval.fromTo(-10, 10).by(5).getFirst());
        assertEquals(0, IntInterval.zero().getFirst());
    }

    @Test
    public void getLast()
    {
        assertEquals(-10, IntInterval.fromTo(10, -10).by(-5).getLast());
        assertEquals(-10, IntInterval.fromTo(10, -12).by(-5).getLast());
        assertEquals(10, IntInterval.fromTo(-10, 10).by(5).getLast());
        assertEquals(10, IntInterval.fromTo(-10, 12).by(5).getLast());
        assertEquals(0, IntInterval.zero().getLast());
    }

    @Test
    public void indexOf()
    {
        IntInterval interval = IntInterval.fromTo(-10, 12).by(5);
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

        IntInterval backwardsIntInterval = IntInterval.fromTo(10, -12).by(-5);
        assertEquals(0, backwardsIntInterval.indexOf(10));
        assertEquals(1, backwardsIntInterval.indexOf(5));
        assertEquals(2, backwardsIntInterval.indexOf(0));
        assertEquals(3, backwardsIntInterval.indexOf(-5));
        assertEquals(4, backwardsIntInterval.indexOf(-10));

        assertEquals(-1, backwardsIntInterval.indexOf(15));
        assertEquals(-1, backwardsIntInterval.indexOf(11));
        assertEquals(-1, backwardsIntInterval.indexOf(9));
        assertEquals(-1, backwardsIntInterval.indexOf(-11));
        assertEquals(-1, backwardsIntInterval.indexOf(-15));
    }

    @Test
    public void lastIndexOf()
    {
        IntInterval interval = IntInterval.fromTo(-10, 12).by(5);
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

        IntInterval backwardsIntInterval = IntInterval.fromTo(10, -12).by(-5);
        assertEquals(0, backwardsIntInterval.lastIndexOf(10));
        assertEquals(1, backwardsIntInterval.lastIndexOf(5));
        assertEquals(2, backwardsIntInterval.lastIndexOf(0));
        assertEquals(3, backwardsIntInterval.lastIndexOf(-5));
        assertEquals(4, backwardsIntInterval.lastIndexOf(-10));

        assertEquals(-1, backwardsIntInterval.lastIndexOf(15));
        assertEquals(-1, backwardsIntInterval.lastIndexOf(11));
        assertEquals(-1, backwardsIntInterval.lastIndexOf(9));
        assertEquals(-1, backwardsIntInterval.lastIndexOf(-11));
        assertEquals(-1, backwardsIntInterval.lastIndexOf(-15));
    }

    @Test
    public void get()
    {
        IntInterval interval = IntInterval.fromTo(-10, 12).by(5);
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
        assertTrue(IntInterval.fromTo(1, 3).containsAll(1, 2, 3));
        assertFalse(IntInterval.fromTo(1, 3).containsAll(1, 2, 4));
    }

    @Test
    public void containsAllIterable()
    {
        assertTrue(IntInterval.fromTo(1, 3).containsAll(IntInterval.fromTo(1, 3)));
        assertFalse(IntInterval.fromTo(1, 3).containsAll(IntInterval.fromTo(1, 4)));
    }

    @Test
    public void distinct()
    {
        assertSame(this.intInterval, this.intInterval.distinct());
    }

    @Test
    public void asReversed()
    {
        MutableIntList list = IntLists.mutable.empty();
        list.addAll(this.intInterval.asReversed());
        assertEquals(IntLists.mutable.with(3, 2, 1), list);
    }

    @Test
    public void zip()
    {
        IntInterval interval = IntInterval.oneTo(3);
        ImmutableList<IntObjectPair<String>> zipped = interval.zip(interval.collect(Integer::toString));
        ImmutableList<IntObjectPair<String>> zippedLazy = interval.zip(interval.asLazy().collect(Integer::toString));
        ImmutableList<IntObjectPair<String>> expected = Lists.immutable.with(
                PrimitiveTuples.pair(1, "1"),
                PrimitiveTuples.pair(2, "2"),
                PrimitiveTuples.pair(3, "3"));
        assertEquals(expected, zipped);
        assertEquals(expected, zippedLazy);
        Verify.assertEmpty(interval.zip(Lists.mutable.empty()));
        assertEquals(Lists.immutable.with(PrimitiveTuples.pair(1, "1")), interval.zip(Lists.mutable.with("1")));
    }

    @Test
    public void zipInt()
    {
        IntInterval interval = IntInterval.oneTo(3);
        ImmutableList<IntIntPair> zipped = interval.zipInt(interval.toReversed());
        ImmutableList<IntIntPair> zippedLazy = interval.zipInt(interval.asReversed());
        ImmutableList<IntIntPair> expected = Lists.immutable.with(
                PrimitiveTuples.pair(1, 3),
                PrimitiveTuples.pair(2, 2),
                PrimitiveTuples.pair(3, 1));
        assertEquals(expected, zipped);
        assertEquals(expected, zippedLazy);
        Verify.assertEmpty(interval.zipInt(IntLists.mutable.empty()));
        assertEquals(Lists.immutable.with(PrimitiveTuples.pair(1, 3)), interval.zipInt(IntLists.mutable.with(3)));
    }

    @Test
    public void primitiveStream()
    {
        assertEquals(Lists.mutable.of(1, 2, 3, 4), IntInterval.oneTo(4).primitiveStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(0, 2, 4), IntInterval.fromToBy(0, 5, 2).primitiveStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(5, 3, 1), IntInterval.fromToBy(5, 0, -2).primitiveStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(10, 15, 20, 25, 30), IntInterval.fromToBy(10, 30, 5).primitiveStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(30, 25, 20, 15, 10), IntInterval.fromToBy(30, 10, -5).primitiveStream().boxed().collect(Collectors.toList()));
    }

    @Test
    public void primitiveParallelStream()
    {
        assertEquals(Lists.mutable.of(1, 2, 3, 4), IntInterval.oneTo(4).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(0, 2, 4), IntInterval.fromToBy(0, 5, 2).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(5, 3, 1, -1, -3), IntInterval.fromToBy(5, -4, -2).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(10, 15, 20, 25, 30), IntInterval.fromToBy(10, 30, 5).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(30, 25, 20, 15, 10), IntInterval.fromToBy(30, 10, -5).primitiveParallelStream().boxed().collect(Collectors.toList()));
        assertEquals(Lists.mutable.of(-1, 10, 21, 32, 43, 54, 65, 76, 87, 98), IntInterval.fromToBy(-1, 100, 11).primitiveParallelStream().boxed().collect(Collectors.toList()));
    }

    @Test
    public void toImmutable()
    {
        IntInterval interval = IntInterval.oneTo(5);
        assertSame(interval, interval.toImmutable());
    }

    @Test
    public void newWith()
    {
        IntInterval interval = IntInterval.oneTo(4);
        ImmutableIntList list = interval.newWith(5);
        assertNotSame(interval, list);
        assertEquals(IntInterval.oneTo(5), list);
    }

    @Test
    public void newWithout()
    {
        IntInterval interval = IntInterval.oneTo(5);
        ImmutableIntList list = interval.newWithout(5);
        assertNotSame(interval, list);
        assertEquals(IntInterval.oneTo(4), list);
    }

    @Test
    public void newWithAll()
    {
        IntInterval interval = IntInterval.oneTo(2);
        ImmutableIntList list = interval.newWithAll(IntInterval.fromTo(3, 5));
        assertNotSame(interval, list);
        assertEquals(IntInterval.oneTo(5), list);
    }

    @Test
    public void newWithoutAll()
    {
        IntInterval interval = IntInterval.oneTo(5);
        ImmutableIntList list = interval.newWithoutAll(IntInterval.fromTo(3, 5));
        assertNotSame(interval, list);
        assertEquals(IntInterval.oneTo(2), list);
    }
}
