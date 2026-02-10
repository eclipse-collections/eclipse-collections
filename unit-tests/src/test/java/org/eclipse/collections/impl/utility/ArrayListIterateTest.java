/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function3;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.block.factory.ObjectIntProcedures;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.MaxSizeFunction;
import org.eclipse.collections.impl.block.function.MinSizeFunction;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.math.Sum;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iList;
import static org.eclipse.collections.impl.factory.Iterables.mList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * JUnit test for {@link ArrayListIterate}.
 */
public class ArrayListIterateTest
{
    private static final int OVER_OPTIMIZED_LIMIT = 101;

    private static final class ThisIsNotAnArrayList<T>
            extends ArrayList<T>
    {
        private static final long serialVersionUID = 1L;

        private ThisIsNotAnArrayList(Collection<? extends T> collection)
        {
            super(collection);
        }
    }

    @Test
    public void testThisIsNotAnArrayList()
    {
        ThisIsNotAnArrayList<Integer> undertest = new ThisIsNotAnArrayList<>(FastList.newListWith(1, 2, 3));
        assertNotSame(undertest.getClass(), ArrayList.class);
    }

    @Test
    public void sortOnListWithLessThan10Elements()
    {
        ArrayList<Integer> integers = this.newArrayList(2, 3, 4, 1, 5, 7, 6, 9, 8);
        Verify.assertStartsWith(ArrayListIterate.sortThis(integers), 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ArrayList<Integer> integers2 = this.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Verify.assertStartsWith(
                ArrayListIterate.sortThis(integers2, Collections.reverseOrder()),
                9, 8, 7, 6, 5, 4, 3, 2, 1);

        ArrayList<Integer> integers3 = this.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Verify.assertStartsWith(ArrayListIterate.sortThis(integers3), 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ArrayList<Integer> integers4 = this.newArrayList(9, 8, 7, 6, 5, 4, 3, 2, 1);
        Verify.assertStartsWith(ArrayListIterate.sortThis(integers4), 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ThisIsNotAnArrayList<Integer> arrayListThatIsnt = new ThisIsNotAnArrayList<>(FastList.newListWith(9, 8, 7, 6, 5, 4, 3, 2, 1));
        Verify.assertStartsWith(ArrayListIterate.sortThis(arrayListThatIsnt), 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void sortingWithoutAccessToInternalArray()
    {
        ThisIsNotAnArrayList<Integer> arrayListThatIsnt = new ThisIsNotAnArrayList<>(FastList.newListWith(5, 3, 4, 1, 2));
        Verify.assertStartsWith(ArrayListIterate.sortThis(arrayListThatIsnt, Integer::compareTo), 1, 2, 3, 4, 5);
    }

    @Test
    public void copyToArray()
    {
        ThisIsNotAnArrayList<Integer> notAnArrayList = this.newNotAnArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Integer[] target1 = {1, 2, null, null};
        ArrayListIterate.toArray(notAnArrayList, target1, 2, 2);
        assertArrayEquals(target1, new Integer[]{1, 2, 1, 2});

        ArrayList<Integer> arrayList = this.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Integer[] target2 = {1, 2, null, null};
        ArrayListIterate.toArray(arrayList, target2, 2, 2);
        assertArrayEquals(target2, new Integer[]{1, 2, 1, 2});
    }

    @Test
    public void sortOnListWithMoreThan10Elements()
    {
        ArrayList<Integer> integers = this.newArrayList(2, 3, 4, 1, 5, 7, 6, 8, 10, 9);
        Verify.assertStartsWith(ArrayListIterate.sortThis(integers), 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        ArrayList<Integer> integers2 = this.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        Verify.assertStartsWith(ArrayListIterate.sortThis(integers2, Collections.reverseOrder()), 8, 7, 6, 5, 4, 3, 2, 1);

        ArrayList<Integer> integers3 = this.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Verify.assertStartsWith(ArrayListIterate.sortThis(integers3), 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void forEachUsingFromTo()
    {
        ArrayList<Integer> integers = Interval.oneTo(5).addAllTo(new ArrayList<>());
        ArrayList<Integer> results = new ArrayList<>();
        ArrayListIterate.forEach(integers, 0, 4, results::add);
        assertEquals(integers, results);
        MutableList<Integer> reverseResults = Lists.mutable.empty();
        ArrayListIterate.forEach(integers, 4, 0, reverseResults::add);
        assertEquals(ListIterate.reverseThis(integers), reverseResults);
        assertThrows(IndexOutOfBoundsException.class, () -> ArrayListIterate.forEach(integers, 4, -1, reverseResults::add));
        assertThrows(IndexOutOfBoundsException.class, () -> ArrayListIterate.forEach(integers, -1, 4, reverseResults::add));
    }

    @Test
    public void forEachUsingFromToWithOptimisable()
    {
        ArrayList<Integer> expected = Interval.oneTo(5).addAllTo(new ArrayList<>());
        ArrayList<Integer> optimisableList = Interval.oneTo(105).addAllTo(new ArrayList<>());
        ArrayList<Integer> results = new ArrayList<>();
        ArrayListIterate.forEach(optimisableList, 0, 4, results::add);
        assertEquals(expected, results);
        MutableList<Integer> reverseResults = Lists.mutable.empty();
        ArrayListIterate.forEach(optimisableList, 4, 0, reverseResults::add);
        assertEquals(ListIterate.reverseThis(expected), reverseResults);
        assertThrows(IndexOutOfBoundsException.class, () -> ArrayListIterate.forEach(optimisableList, 104, -1, reverseResults::add));
        assertThrows(IndexOutOfBoundsException.class, () -> ArrayListIterate.forEach(optimisableList, -1, 4, reverseResults::add));
    }

    @Test
    public void forEachWithIndexUsingFromTo()
    {
        ArrayList<Integer> integers = Interval.oneTo(5).addAllTo(new ArrayList<>());
        ArrayList<Integer> results = new ArrayList<>();
        ArrayListIterate.forEachWithIndex(integers, 0, 4, ObjectIntProcedures.fromProcedure(results::add));
        assertEquals(integers, results);
        MutableList<Integer> reverseResults = Lists.mutable.empty();
        ObjectIntProcedure<Integer> objectIntProcedure = ObjectIntProcedures.fromProcedure(reverseResults::add);
        ArrayListIterate.forEachWithIndex(integers, 4, 0, objectIntProcedure);
        assertEquals(ListIterate.reverseThis(integers), reverseResults);
        assertThrows(IndexOutOfBoundsException.class, () -> ArrayListIterate.forEachWithIndex(integers, 4, -1, objectIntProcedure));
        assertThrows(IndexOutOfBoundsException.class, () -> ArrayListIterate.forEachWithIndex(integers, -1, 4, objectIntProcedure));
    }

    @Test
    public void forEachWithIndexUsingFromToWithOptimisableList()
    {
        ArrayList<Integer> optimisableList = Interval.oneTo(105).addAllTo(new ArrayList<>());
        ArrayList<Integer> expected = Interval.oneTo(105).addAllTo(new ArrayList<>());
        ArrayList<Integer> results = new ArrayList<>();
        ArrayListIterate.forEachWithIndex(optimisableList, 0, 104, ObjectIntProcedures.fromProcedure(results::add));
        assertEquals(expected, results);
        MutableList<Integer> reverseResults = Lists.mutable.empty();
        ObjectIntProcedure<Integer> objectIntProcedure = ObjectIntProcedures.fromProcedure(reverseResults::add);
        ArrayListIterate.forEachWithIndex(expected, 104, 0, objectIntProcedure);
        assertEquals(ListIterate.reverseThis(expected), reverseResults);
        assertThrows(IndexOutOfBoundsException.class, () -> ArrayListIterate.forEachWithIndex(expected, 104, -1, objectIntProcedure));
        assertThrows(IndexOutOfBoundsException.class, () -> ArrayListIterate.forEachWithIndex(expected, -1, 104, objectIntProcedure));
    }

    @Test
    public void reverseForEach()
    {
        ArrayList<Integer> integers = Interval.oneTo(5).addAllTo(new ArrayList<>());
        MutableList<Integer> reverseResults = Lists.mutable.empty();
        ArrayListIterate.reverseForEach(integers, reverseResults::add);
        assertEquals(ListIterate.reverseThis(integers), reverseResults);
    }

    @Test
    public void reverseForEach_emptyList()
    {
        ArrayList<Integer> integers = new ArrayList<>();
        MutableList<Integer> results = Lists.mutable.empty();
        ArrayListIterate.reverseForEach(integers, results::add);
        assertEquals(integers, results);
    }

    @Test
    public void reverseForEachWithIndex()
    {
        ArrayList<Integer> integers = Interval.oneTo(5).addAllTo(new ArrayList<>());
        MutableList<Integer> reverseResults = Lists.mutable.empty();
        ArrayListIterate.reverseForEachWithIndex(integers, (each, index) -> reverseResults.add(each + index));
        assertEquals(Lists.mutable.with(9, 7, 5, 3, 1), reverseResults);
    }

    @Test
    public void reverseForEachWithIndex_emptyList()
    {
        ArrayList<Integer> integers = new ArrayList<>();
        ArrayListIterate.reverseForEachWithIndex(integers, (each, index) -> fail("Should not be evaluated"));
    }

    @Test
    public void injectInto()
    {
        ArrayList<Integer> list = this.newArrayList(1, 2, 3);
        assertEquals(
                Integer.valueOf(1 + 1 + 2 + 3),
                ArrayListIterate.injectInto(1, list, AddFunction.INTEGER));
    }

    @Test
    public void injectIntoOver100()
    {
        ArrayList<Integer> list = this.oneHundredAndOneOnes();
        assertEquals(Integer.valueOf(102), ArrayListIterate.injectInto(1, list, AddFunction.INTEGER));
    }

    @Test
    public void injectIntoDoubleOver100()
    {
        ArrayList<Integer> list = this.oneHundredAndOneOnes();
        assertEquals(102.0, ArrayListIterate.injectInto(1.0d, list, AddFunction.INTEGER_TO_DOUBLE), 0.0001);
    }

    private ArrayList<Integer> oneHundredAndOneOnes()
    {
        return new ArrayList<>(Collections.nCopies(101, 1));
    }

    @Test
    public void injectIntoIntegerOver100()
    {
        ArrayList<Integer> list = this.oneHundredAndOneOnes();
        assertEquals(102, ArrayListIterate.injectInto(1, list, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLongOver100()
    {
        ArrayList<Integer> list = this.oneHundredAndOneOnes();
        assertEquals(102, ArrayListIterate.injectInto(1L, list, AddFunction.INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble()
    {
        ArrayList<Double> list = new ArrayList<>();
        list.add(1.0);
        list.add(2.0);
        list.add(3.0);
        assertEquals(
                Double.valueOf(7.0),
                ArrayListIterate.injectInto(1.0, list, AddFunction.DOUBLE));
    }

    @Test
    public void injectIntoFloat()
    {
        ArrayList<Float> list = new ArrayList<>();
        list.add(1.0f);
        list.add(2.0f);
        list.add(3.0f);
        assertEquals(
                Float.valueOf(7.0f),
                ArrayListIterate.injectInto(1.0f, list, AddFunction.FLOAT));
    }

    @Test
    public void injectIntoString()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        assertEquals("0123", ArrayListIterate.injectInto("0", list, AddFunction.STRING));
    }

    @Test
    public void injectIntoMaxString()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("12");
        list.add("123");
        assertEquals(Integer.valueOf(3), ArrayListIterate.injectInto(Integer.MIN_VALUE, list, MaxSizeFunction.STRING));
    }

    @Test
    public void injectIntoMinString()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("12");
        list.add("123");
        assertEquals(Integer.valueOf(1), ArrayListIterate.injectInto(Integer.MAX_VALUE, list, MinSizeFunction.STRING));
    }

    @Test
    public void collect()
    {
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(Boolean.TRUE);
        list.add(Boolean.FALSE);
        list.add(Boolean.TRUE);
        list.add(Boolean.TRUE);
        list.add(Boolean.FALSE);
        list.add(null);
        list.add(null);
        list.add(Boolean.FALSE);
        list.add(Boolean.TRUE);
        list.add(null);
        ArrayList<String> newCollection = ArrayListIterate.collect(list, String::valueOf);
        //List<String> newCollection = ArrayListIterate.collect(list, ArrayListIterateTest.TO_STRING_FUNCTION);
        Verify.assertSize(10, newCollection);
        Verify.assertContainsAll(newCollection, "null", "false", "true");
    }

    @Test
    public void collectBoolean()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableBooleanList actual = ArrayListIterate.collectBoolean(list, PrimitiveFunctions.integerIsPositive());
        assertEquals(BooleanArrayList.newListWith(false, false, true), actual);
    }

    @Test
    public void collectBooleanWithTarget()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableBooleanList target = new BooleanArrayList();
        MutableBooleanList actual = ArrayListIterate.collectBoolean(list, PrimitiveFunctions.integerIsPositive(), target);
        assertSame(target, actual, "Target list sent as parameter not returned");
        assertEquals(BooleanArrayList.newListWith(false, false, true), actual);
    }

    @Test
    public void collectBooleanOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableBooleanList actual = ArrayListIterate.collectBoolean(list, PrimitiveFunctions.integerIsPositive());
        MutableBooleanList expected = new BooleanArrayList(list.size());
        expected.add(false);
        for (int i = 1; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add(true);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void collectBooleanWithTargetOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableBooleanList target = new BooleanArrayList();
        MutableBooleanList actual = ArrayListIterate.collectBoolean(list, PrimitiveFunctions.integerIsPositive(), target);
        MutableBooleanList expected = new BooleanArrayList(list.size());
        expected.add(false);
        for (int i = 1; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add(true);
        }
        assertEquals(expected, actual);
        assertSame(target, actual, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectByte()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableByteList actual = ArrayListIterate.collectByte(list, PrimitiveFunctions.unboxIntegerToByte());
        assertEquals(ByteArrayList.newListWith((byte) -1, (byte) 0, (byte) 4), actual);
    }

    @Test
    public void collectByteWithTarget()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableByteList target = new ByteArrayList();
        MutableByteList actual = ArrayListIterate.collectByte(list, PrimitiveFunctions.unboxIntegerToByte(), target);
        assertSame(target, actual, "Target list sent as parameter not returned");
        assertEquals(ByteArrayList.newListWith((byte) -1, (byte) 0, (byte) 4), actual);
    }

    @Test
    public void collectByteOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableByteList actual = ArrayListIterate.collectByte(list, PrimitiveFunctions.unboxIntegerToByte());
        MutableByteList expected = new ByteArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((byte) i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void collectByteWithTargetOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableByteList target = new ByteArrayList();
        MutableByteList actual = ArrayListIterate.collectByte(list, PrimitiveFunctions.unboxIntegerToByte(), target);
        MutableByteList expected = new ByteArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((byte) i);
        }
        assertEquals(expected, actual);
        assertSame(target, actual, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectChar()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableCharList actual = ArrayListIterate.collectChar(list, PrimitiveFunctions.unboxIntegerToChar());
        assertEquals(CharArrayList.newListWith((char) -1, (char) 0, (char) 4), actual);
    }

    @Test
    public void collectCharWithTarget()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableCharList target = new CharArrayList();
        MutableCharList actual = ArrayListIterate.collectChar(list, PrimitiveFunctions.unboxIntegerToChar(), target);
        assertSame(target, actual, "Target list sent as parameter not returned");
        assertEquals(CharArrayList.newListWith((char) -1, (char) 0, (char) 4), actual);
    }

    @Test
    public void collectCharOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableCharList actual = ArrayListIterate.collectChar(list, PrimitiveFunctions.unboxIntegerToChar());
        MutableCharList expected = new CharArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((char) i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void collectCharWithTargetOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableCharList target = new CharArrayList();
        MutableCharList actual = ArrayListIterate.collectChar(list, PrimitiveFunctions.unboxIntegerToChar(), target);
        MutableCharList expected = new CharArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((char) i);
        }
        assertEquals(expected, actual);
        assertSame(target, actual, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectDouble()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableDoubleList actual = ArrayListIterate.collectDouble(list, PrimitiveFunctions.unboxIntegerToDouble());
        assertEquals(DoubleArrayList.newListWith(-1.0d, 0.0d, 4.0d), actual);
    }

    @Test
    public void collectDoubleWithTarget()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableDoubleList target = new DoubleArrayList();
        MutableDoubleList actual = ArrayListIterate.collectDouble(list, PrimitiveFunctions.unboxIntegerToDouble(), target);
        assertSame(target, actual, "Target list sent as parameter not returned");
        assertEquals(DoubleArrayList.newListWith(-1.0d, 0.0d, 4.0d), actual);
    }

    @Test
    public void collectDoubleOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableDoubleList actual = ArrayListIterate.collectDouble(list, PrimitiveFunctions.unboxIntegerToDouble());
        MutableDoubleList expected = new DoubleArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((double) i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void collectDoubleWithTargetOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableDoubleList target = new DoubleArrayList();
        MutableDoubleList actual = ArrayListIterate.collectDouble(list, PrimitiveFunctions.unboxIntegerToDouble(), target);
        MutableDoubleList expected = new DoubleArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((double) i);
        }
        assertEquals(expected, actual);
        assertSame(target, actual, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectFloat()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableFloatList actual = ArrayListIterate.collectFloat(list, PrimitiveFunctions.unboxIntegerToFloat());
        assertEquals(FloatArrayList.newListWith(-1.0f, 0.0f, 4.0f), actual);
    }

    @Test
    public void collectFloatWithTarget()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableFloatList target = new FloatArrayList();
        MutableFloatList actual = ArrayListIterate.collectFloat(list, PrimitiveFunctions.unboxIntegerToFloat(), target);
        assertSame(target, actual, "Target list sent as parameter not returned");
        assertEquals(FloatArrayList.newListWith(-1.0f, 0.0f, 4.0f), actual);
    }

    @Test
    public void collectFloatOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableFloatList actual = ArrayListIterate.collectFloat(list, PrimitiveFunctions.unboxIntegerToFloat());
        MutableFloatList expected = new FloatArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((float) i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void collectFloatWithTargetOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableFloatList target = new FloatArrayList();
        MutableFloatList actual = ArrayListIterate.collectFloat(list, PrimitiveFunctions.unboxIntegerToFloat(), target);
        MutableFloatList expected = new FloatArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((float) i);
        }
        assertEquals(expected, actual);
        assertSame(target, actual, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectInt()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableIntList actual = ArrayListIterate.collectInt(list, PrimitiveFunctions.unboxIntegerToInt());
        assertEquals(IntArrayList.newListWith(-1, 0, 4), actual);
    }

    @Test
    public void collectIntWithTarget()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableIntList target = new IntArrayList();
        MutableIntList actual = ArrayListIterate.collectInt(list, PrimitiveFunctions.unboxIntegerToInt(), target);
        assertSame(target, actual, "Target list sent as parameter not returned");
        assertEquals(IntArrayList.newListWith(-1, 0, 4), actual);
    }

    @Test
    public void collectIntOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableIntList actual = ArrayListIterate.collectInt(list, PrimitiveFunctions.unboxIntegerToInt());
        MutableIntList expected = new IntArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add(i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void collectIntWithTargetOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableIntList target = new IntArrayList();
        MutableIntList actual = ArrayListIterate.collectInt(list, PrimitiveFunctions.unboxIntegerToInt(), target);
        MutableIntList expected = new IntArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add(i);
        }
        assertEquals(expected, actual);
        assertSame(target, actual, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectLong()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableLongList actual = ArrayListIterate.collectLong(list, PrimitiveFunctions.unboxIntegerToLong());
        assertEquals(LongArrayList.newListWith(-1L, 0L, 4L), actual);
    }

    @Test
    public void collectLongWithTarget()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableLongList target = new LongArrayList();
        MutableLongList actual = ArrayListIterate.collectLong(list, PrimitiveFunctions.unboxIntegerToLong(), target);
        assertSame(target, actual, "Target list sent as parameter not returned");
        assertEquals(LongArrayList.newListWith(-1L, 0L, 4L), actual);
    }

    @Test
    public void collectLongOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableLongList actual = ArrayListIterate.collectLong(list, PrimitiveFunctions.unboxIntegerToLong());
        MutableLongList expected = new LongArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((long) i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void collectLongWithTargetOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableLongList target = new LongArrayList();
        MutableLongList actual = ArrayListIterate.collectLong(list, PrimitiveFunctions.unboxIntegerToLong(), target);
        MutableLongList expected = new LongArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((long) i);
        }
        assertEquals(expected, actual);
        assertSame(target, actual, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectShort()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableShortList actual = ArrayListIterate.collectShort(list, PrimitiveFunctions.unboxIntegerToShort());
        assertEquals(ShortArrayList.newListWith((short) -1, (short) 0, (short) 4), actual);
    }

    @Test
    public void collectShortWithTarget()
    {
        ArrayList<Integer> list = this.createIntegerList();
        MutableShortList target = new ShortArrayList();
        MutableShortList actual = ArrayListIterate.collectShort(list, PrimitiveFunctions.unboxIntegerToShort(), target);
        assertSame(target, actual, "Target list sent as parameter not returned");
        assertEquals(ShortArrayList.newListWith((short) -1, (short) 0, (short) 4), actual);
    }

    @Test
    public void collectShortOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableShortList actual = ArrayListIterate.collectShort(list, PrimitiveFunctions.unboxIntegerToShort());
        MutableShortList expected = new ShortArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((short) i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void collectShortWithTargetOverOptimizeLimit()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.zeroTo(OVER_OPTIMIZED_LIMIT));
        MutableShortList target = new ShortArrayList();
        MutableShortList actual = ArrayListIterate.collectShort(list, PrimitiveFunctions.unboxIntegerToShort(), target);
        MutableShortList expected = new ShortArrayList(list.size());
        for (int i = 0; i <= OVER_OPTIMIZED_LIMIT; i++)
        {
            expected.add((short) i);
        }
        assertEquals(expected, actual);
        assertSame(target, actual, "Target sent as parameter was not returned as result");
    }

    private ArrayList<Integer> createIntegerList()
    {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(-1);
        list.add(0);
        list.add(4);
        return list;
    }

    @Test
    public void collectOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        ArrayList<Class<?>> newCollection = ArrayListIterate.collect(list, Object::getClass);
        Verify.assertSize(101, newCollection);
        Verify.assertContains(Integer.class, newCollection);
    }

    private ArrayList<Integer> getIntegerList()
    {
        return new ArrayList<>(Interval.toReverseList(1, 5));
    }

    private ArrayList<Integer> getOver100IntegerList()
    {
        return new ArrayList<>(Interval.toReverseList(1, 105));
    }

    @Test
    public void forEachWithIndex()
    {
        ArrayList<Integer> list = this.getIntegerList();
        Iterate.sortThis(list);
        ArrayListIterate.forEachWithIndex(list, (object, index) -> assertEquals(index, object - 1));
    }

    @Test
    public void forEachWithIndexOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        Iterate.sortThis(list);
        ArrayListIterate.forEachWithIndex(list, (object, index) -> assertEquals(index, object - 1));
    }

    @Test
    public void forEach()
    {
        ArrayList<Integer> list = this.getIntegerList();
        Iterate.sortThis(list);
        MutableList<Integer> result = Lists.mutable.empty();
        ArrayListIterate.forEach(list, CollectionAddProcedure.on(result));
        Verify.assertListsEqual(list, result);
    }

    @Test
    public void forEachOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        Iterate.sortThis(list);
        FastList<Integer> result = FastList.newList(101);
        ArrayListIterate.forEach(list, CollectionAddProcedure.on(result));
        Verify.assertListsEqual(list, result);
    }

    @Test
    public void forEachWith()
    {
        ArrayList<Integer> list = this.getIntegerList();
        Iterate.sortThis(list);
        MutableList<Integer> result = Lists.mutable.empty();
        ArrayListIterate.forEachWith(
                list,
                Procedures2.fromProcedure(CollectionAddProcedure.on(result)),
                null);
        Verify.assertListsEqual(list, result);
    }

    @Test
    public void forEachWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        Iterate.sortThis(list);
        MutableList<Integer> result = FastList.newList(101);
        ArrayListIterate.forEachWith(
                list,
                Procedures2.fromProcedure(CollectionAddProcedure.on(result)),
                null);
        Verify.assertListsEqual(list, result);
    }

    @Test
    public void forEachInBoth()
    {
        MutableList<Pair<String, String>> list = Lists.mutable.empty();
        ArrayList<String> list1 = new ArrayList<>(mList("1", "2"));
        ArrayList<String> list2 = new ArrayList<>(mList("a", "b"));
        ArrayListIterate.forEachInBoth(list1, list2, (argument1, argument2) -> list.add(Tuples.twin(argument1, argument2)));

        assertEquals(FastList.newListWith(Tuples.twin("1", "a"), Tuples.twin("2", "b")), list);
    }

    @Test
    public void detect()
    {
        ArrayList<Integer> list = this.getIntegerList();
        assertEquals(Integer.valueOf(1), ArrayListIterate.detect(list, Integer.valueOf(1)::equals));
        ArrayList<Holder<Integer>> list2 =
                new ArrayList<>(Lists.mutable.with(new Holder<>(1), new Holder<>(2), new Holder<>(2)));
        assertSame(list2.get(1), ArrayListIterate.detect(list2, new Holder<>(2)::equals));
    }

    @Test
    public void detectOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertEquals(Integer.valueOf(1), ArrayListIterate.detect(list, Integer.valueOf(1)::equals));
    }

    @Test
    public void detectWith()
    {
        ArrayList<Integer> list = this.getIntegerList();
        assertEquals(Integer.valueOf(1), ArrayListIterate.detectWith(list, Object::equals, 1));
        ArrayList<Holder<Integer>> list2 =
                new ArrayList<>(Lists.mutable.with(new Holder<>(1), new Holder<>(2), new Holder<>(2)));
        assertSame(list2.get(1), ArrayListIterate.detectWith(list2, Object::equals, new Holder<>(2)));
    }

    @Test
    public void detectWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertEquals(Integer.valueOf(1), ArrayListIterate.detectWith(list, Object::equals, 1));
    }

    @Test
    public void detectIfNone()
    {
        ArrayList<Integer> list = this.getIntegerList();
        assertEquals(Integer.valueOf(7), ArrayListIterate.detectIfNone(list, Integer.valueOf(6)::equals, 7));
        assertEquals(Integer.valueOf(2), ArrayListIterate.detectIfNone(list, Integer.valueOf(2)::equals, 7));
    }

    @Test
    public void detectIfNoneOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertNull(ArrayListIterate.detectIfNone(list, Integer.valueOf(102)::equals, null));
    }

    @Test
    public void detectWithIfNone()
    {
        ArrayList<Integer> list = this.getIntegerList();
        assertEquals(Integer.valueOf(7), ArrayListIterate.detectWithIfNone(list, Object::equals, 6, 7));
        assertEquals(Integer.valueOf(2), ArrayListIterate.detectWithIfNone(list, Object::equals, 2, 7));
    }

    @Test
    public void detectWithIfNoneOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertNull(ArrayListIterate.detectWithIfNone(list, Object::equals, 102, null));
    }

    @Test
    public void select()
    {
        ArrayList<Integer> list = this.getIntegerList();
        ArrayList<Integer> results = ArrayListIterate.select(list, Integer.class::isInstance);
        Verify.assertSize(5, results);
    }

    @Test
    public void selectOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        ArrayList<Integer> results = ArrayListIterate.select(list, Integer.class::isInstance);
        Verify.assertSize(101, results);
    }

    @Test
    public void selectWith()
    {
        ArrayList<Integer> list = this.getIntegerList();
        ArrayList<Integer> results = ArrayListIterate.selectWith(list, Predicates2.instanceOf(), Integer.class);
        Verify.assertSize(5, results);
    }

    @Test
    public void selectWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        ArrayList<Integer> results = ArrayListIterate.selectWith(list, Predicates2.instanceOf(), Integer.class);
        Verify.assertSize(101, results);
    }

    @Test
    public void reject()
    {
        ArrayList<Integer> list = this.getIntegerList();
        ArrayList<Integer> results = ArrayListIterate.reject(list, Integer.class::isInstance);
        Verify.assertEmpty(results);
    }

    @Test
    public void rejectOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        List<Integer> results = ArrayListIterate.reject(list, Integer.class::isInstance);
        Verify.assertEmpty(results);
    }

    @Test
    public void distinct()
    {
        ArrayList<Integer> list = new ArrayList<>();
        list.addAll(FastList.newListWith(9, 4, 7, 7, 5, 6, 2, 4));
        List<Integer> result = ArrayListIterate.distinct(list);
        Verify.assertListsEqual(FastList.newListWith(9, 4, 7, 5, 6, 2), result);
        ArrayList<Integer> target = new ArrayList<>();
        ArrayListIterate.distinct(list, target);
        Verify.assertListsEqual(FastList.newListWith(9, 4, 7, 5, 6, 2), target);
        assertEquals(FastList.newListWith(9, 4, 7, 7, 5, 6, 2, 4), list);

        ArrayList<Integer> list2 = new ArrayList<>(Interval.oneTo(103));
        list2.add(103);
        ArrayList<Integer> target2 = new ArrayList<>();
        List<Integer> result2 = ArrayListIterate.distinct(list2, target2);
        assertEquals(Interval.fromTo(1, 103), result2);
    }

    @Test
    public void distinctWithHashingStrategy()
    {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(FastList.newListWith("A", "a", "b", "c", "B", "D", "e", "e", "E", "D"));
        list = ArrayListIterate.distinct(list, HashingStrategies.fromFunction(String::toLowerCase));
        assertEquals(FastList.newListWith("A", "b", "c", "D", "e"), list);

        ArrayList<Integer> list2 = new ArrayList<>(Interval.oneTo(103));
        list2.add(103);
        List<Integer> result2 = ArrayListIterate.distinct(list2, HashingStrategies.fromFunction(String::valueOf));
        assertEquals(Interval.fromTo(1, 103), result2);
    }

    @Test
    public void selectInstancesOfOver100()
    {
        ArrayList<Number> list = new ArrayList<>(Interval.oneTo(101));
        list.add(102.0);
        MutableList<Double> results = ArrayListIterate.selectInstancesOf(list, Double.class);
        assertEquals(iList(102.0), results);
    }

    public static final class CollectionCreator
    {
        private final int data;

        private CollectionCreator(int data)
        {
            this.data = data;
        }

        public Collection<Integer> getCollection()
        {
            return FastList.newListWith(this.data, this.data);
        }
    }

    @Test
    public void flatCollect()
    {
        ArrayList<CollectionCreator> list = new ArrayList<>();
        list.add(new CollectionCreator(1));
        list.add(new CollectionCreator(2));

        List<Integer> results1 = ArrayListIterate.flatCollect(list, CollectionCreator::getCollection);
        Verify.assertListsEqual(FastList.newListWith(1, 1, 2, 2), results1);

        MutableList<Integer> target1 = Lists.mutable.empty();
        MutableList<Integer> results2 = ArrayListIterate.flatCollect(list, CollectionCreator::getCollection, target1);
        assertSame(results2, target1);

        Verify.assertListsEqual(FastList.newListWith(1, 1, 2, 2), results2);
    }

    @Test
    public void rejectWith()
    {
        ArrayList<Integer> list = this.getIntegerList();
        ArrayList<Integer> results = ArrayListIterate.rejectWith(list, Predicates2.instanceOf(), Integer.class);
        Verify.assertEmpty(results);
    }

    @Test
    public void rejectWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        ArrayList<Integer> results = ArrayListIterate.rejectWith(list, Predicates2.instanceOf(), Integer.class);
        Verify.assertEmpty(results);
    }

    @Test
    public void selectAndRejectWith()
    {
        ArrayList<Integer> list = this.getIntegerList();
        Twin<MutableList<Integer>> result =
                ArrayListIterate.selectAndRejectWith(list, Predicates2.in(), Lists.immutable.of(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(4, result.getTwo());
    }

    @Test
    public void selectAndRejectWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        Twin<MutableList<Integer>> result =
                ArrayListIterate.selectAndRejectWith(list, Predicates2.in(), Lists.immutable.of(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(100, result.getTwo());
    }

    @Test
    public void partition()
    {
        ArrayList<Integer> smallList = new ArrayList<>(Interval.oneTo(99));
        PartitionMutableList<Integer> smallPartition = ArrayListIterate.partition(smallList, Predicates.in(Interval.oneToBy(99, 2)));
        assertEquals(Interval.oneToBy(99, 2), smallPartition.getSelected());
        assertEquals(Interval.fromToBy(2, 98, 2), smallPartition.getRejected());

        ArrayList<Integer> bigList = new ArrayList<>(Interval.oneTo(101));
        PartitionMutableList<Integer> bigPartition = ArrayListIterate.partition(bigList, Predicates.in(Interval.oneToBy(101, 2)));
        assertEquals(Interval.oneToBy(101, 2), bigPartition.getSelected());
        assertEquals(Interval.fromToBy(2, 100, 2), bigPartition.getRejected());
    }

    @Test
    public void partitionWith()
    {
        ArrayList<Integer> smallList = new ArrayList<>(Interval.oneTo(99));
        PartitionMutableList<Integer> smallPartition = ArrayListIterate.partitionWith(smallList, Predicates2.in(), Interval.oneToBy(99, 2));
        assertEquals(Interval.oneToBy(99, 2), smallPartition.getSelected());
        assertEquals(Interval.fromToBy(2, 98, 2), smallPartition.getRejected());

        ArrayList<Integer> bigList = new ArrayList<>(Interval.oneTo(101));
        PartitionMutableList<Integer> bigPartition = ArrayListIterate.partitionWith(bigList, Predicates2.in(), Interval.oneToBy(101, 2));
        assertEquals(Interval.oneToBy(101, 2), bigPartition.getSelected());
        assertEquals(Interval.fromToBy(2, 100, 2), bigPartition.getRejected());
    }

    @Test
    public void anySatisfyWith()
    {
        ArrayList<Integer> list = this.getIntegerList();
        assertTrue(ArrayListIterate.anySatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        assertFalse(ArrayListIterate.anySatisfyWith(list, Predicates2.instanceOf(), Double.class));
    }

    @Test
    public void anySatisfy()
    {
        ArrayList<Integer> list = this.getIntegerList();
        assertTrue(ArrayListIterate.anySatisfy(list, Integer.class::isInstance));
        assertFalse(ArrayListIterate.anySatisfy(list, Double.class::isInstance));
    }

    @Test
    public void anySatisfyWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertTrue(ArrayListIterate.anySatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        assertFalse(ArrayListIterate.anySatisfyWith(list, Predicates2.instanceOf(), Double.class));
    }

    @Test
    public void anySatisfyOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertTrue(ArrayListIterate.anySatisfy(list, Integer.class::isInstance));
        assertFalse(ArrayListIterate.anySatisfy(list, Double.class::isInstance));
    }

    @Test
    public void allSatisfyWith()
    {
        ArrayList<Integer> list = this.getIntegerList();
        assertTrue(ArrayListIterate.allSatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        assertFalse(ArrayListIterate.allSatisfyWith(list, Predicates2.greaterThan(), 2));
    }

    @Test
    public void allSatisfy()
    {
        ArrayList<Integer> list = this.getIntegerList();
        assertTrue(ArrayListIterate.allSatisfy(list, Integer.class::isInstance));
        assertFalse(ArrayListIterate.allSatisfy(list, Predicates.greaterThan(2)));
    }

    @Test
    public void allSatisfyWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertTrue(ArrayListIterate.allSatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        assertFalse(ArrayListIterate.allSatisfyWith(list, Predicates2.greaterThan(), 2));
    }

    @Test
    public void allSatisfyOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertTrue(ArrayListIterate.allSatisfy(list, Integer.class::isInstance));
        assertFalse(ArrayListIterate.allSatisfy(list, Predicates.greaterThan(2)));
    }

    @Test
    public void noneSatisfyOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertFalse(ArrayListIterate.noneSatisfy(list, Integer.class::isInstance));
        assertTrue(ArrayListIterate.noneSatisfy(list, Predicates.greaterThan(150)));
    }

    @Test
    public void noneSatisfyWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertFalse(ArrayListIterate.noneSatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        assertTrue(ArrayListIterate.noneSatisfyWith(list, Predicates2.greaterThan(), 150));
    }

    @Test
    public void countWith()
    {
        ArrayList<Integer> list = this.getIntegerList();
        assertEquals(5, ArrayListIterate.countWith(list, Predicates2.instanceOf(), Integer.class));
        assertEquals(0, ArrayListIterate.countWith(list, Predicates2.instanceOf(), Double.class));
    }

    @Test
    public void countWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        assertEquals(101, ArrayListIterate.countWith(list, Predicates2.instanceOf(), Integer.class));
        assertEquals(0, ArrayListIterate.countWith(list, Predicates2.instanceOf(), Double.class));
    }

    @Test
    public void collectIfOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        ArrayList<Class<?>> result = ArrayListIterate.collectIf(list, Integer.valueOf(101)::equals, Object::getClass);
        assertEquals(FastList.newListWith(Integer.class), result);
    }

    @Test
    public void collectWithOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.oneTo(101));
        ArrayList<String> result = ArrayListIterate.collectWith(list, (argument1, argument2) -> argument1.equals(argument2) ? "101" : null, 101);
        Verify.assertSize(101, result);
        Verify.assertContainsAll(result, null, "101");
        assertEquals(100, Iterate.count(result, Predicates.isNull()));
    }

    @Test
    public void detectIndexOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.toReverseList(1, 101));
        list.add(3);
        list.add(2);
        assertEquals(100, ArrayListIterate.detectIndex(list, Integer.valueOf(1)::equals));
        assertEquals(99, ArrayListIterate.detectIndex(list, Integer.valueOf(2)::equals));
        assertEquals(98, ArrayListIterate.detectIndex(list, Integer.valueOf(3)::equals));
        assertEquals(0, Iterate.detectIndex(list, Integer.valueOf(101)::equals));
        assertEquals(-1, Iterate.detectIndex(list, Integer.valueOf(200)::equals));
    }

    @Test
    public void detectIndexSmallList()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.toReverseList(1, 5));
        list.add(3);
        list.add(2);
        assertEquals(4, ArrayListIterate.detectIndex(list, Integer.valueOf(1)::equals));
        assertEquals(3, ArrayListIterate.detectIndex(list, Integer.valueOf(2)::equals));
        assertEquals(2, ArrayListIterate.detectIndex(list, Integer.valueOf(3)::equals));
        assertEquals(0, Iterate.detectIndex(list, Integer.valueOf(5)::equals));
        assertEquals(-1, Iterate.detectIndex(list, Integer.valueOf(10)::equals));
    }

    @Test
    public void detectLastIndexOver100()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.toReverseList(1, 101));
        list.add(3);
        list.add(2);
        assertEquals(100, ArrayListIterate.detectLastIndex(list, Integer.valueOf(1)::equals));
        assertEquals(102, ArrayListIterate.detectLastIndex(list, Integer.valueOf(2)::equals));
        assertEquals(101, ArrayListIterate.detectLastIndex(list, Integer.valueOf(3)::equals));
        assertEquals(0, ArrayListIterate.detectLastIndex(list, Integer.valueOf(101)::equals));
        assertEquals(-1, ArrayListIterate.detectLastIndex(list, Integer.valueOf(200)::equals));
    }

    @Test
    public void detectLastIndexSmallList()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.toReverseList(1, 5));
        list.add(3);
        list.add(2);
        assertEquals(4, ArrayListIterate.detectLastIndex(list, Integer.valueOf(1)::equals));
        assertEquals(6, ArrayListIterate.detectLastIndex(list, Integer.valueOf(2)::equals));
        assertEquals(5, ArrayListIterate.detectLastIndex(list, Integer.valueOf(3)::equals));
        assertEquals(0, ArrayListIterate.detectLastIndex(list, Integer.valueOf(5)::equals));
        assertEquals(-1, ArrayListIterate.detectLastIndex(list, Integer.valueOf(10)::equals));
    }

    @Test
    public void detectIndexWithOver100()
    {
        List<Integer> list = new ArrayList<>(Interval.toReverseList(1, 101));
        assertEquals(100, Iterate.detectIndexWith(list, Object::equals, 1));
        assertEquals(0, Iterate.detectIndexWith(list, Object::equals, 101));
        assertEquals(-1, Iterate.detectIndexWith(list, Object::equals, 200));
    }

    @Test
    public void detectIndexWithSmallList()
    {
        List<Integer> list = new ArrayList<>(Interval.toReverseList(1, 5));
        assertEquals(4, Iterate.detectIndexWith(list, Object::equals, 1));
        assertEquals(0, Iterate.detectIndexWith(list, Object::equals, 5));
        assertEquals(-1, Iterate.detectIndexWith(list, Object::equals, 10));
    }

    @Test
    public void injectIntoWithOver100()
    {
        Sum result = new IntegerSum(0);
        Integer parameter = 2;
        Function3<Sum, Integer, Integer, Sum> function = (sum, element, withValue) -> sum.add((element.intValue() - element.intValue()) * withValue.intValue());
        Sum sumOfDoubledValues = ArrayListIterate.injectIntoWith(result, this.getOver100IntegerList(), function, parameter);
        assertEquals(0, sumOfDoubledValues.getValue().intValue());
    }

    @Test
    public void removeIf()
    {
        ArrayList<Integer> objects = this.newArrayList(1, 2, 3, null);
        ArrayListIterate.removeIf(objects, Predicates.isNull());
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);

        ArrayList<Integer> objects5 = this.newArrayList(null, 1, 2, 3);
        ArrayListIterate.removeIf(objects5, Predicates.isNull());
        Verify.assertSize(3, objects5);
        Verify.assertContainsAll(objects5, 1, 2, 3);

        ArrayList<Integer> objects4 = this.newArrayList(1, null, 2, 3);
        ArrayListIterate.removeIf(objects4, Predicates.isNull());
        Verify.assertSize(3, objects4);
        Verify.assertContainsAll(objects4, 1, 2, 3);

        ArrayList<Integer> objects3 = this.newArrayList(null, null, null, null);
        ArrayListIterate.removeIf(objects3, Predicates.isNull());
        Verify.assertEmpty(objects3);

        ArrayList<Integer> objects2 = this.newArrayList(null, 1, 2, 3, null);
        ArrayListIterate.removeIf(objects2, Predicates.isNull());
        Verify.assertSize(3, objects2);
        Verify.assertContainsAll(objects2, 1, 2, 3);

        ArrayList<Integer> objects1 = this.newArrayList(1, 2, 3);
        ArrayListIterate.removeIf(objects1, Predicates.isNull());
        Verify.assertSize(3, objects1);
        Verify.assertContainsAll(objects1, 1, 2, 3);

        ThisIsNotAnArrayList<Integer> objects6 = this.newNotAnArrayList(1, 2, 3);
        ArrayListIterate.removeIf(objects6, Predicates.isNull());
        Verify.assertSize(3, objects6);
        Verify.assertContainsAll(objects6, 1, 2, 3);
    }

    @Test
    public void removeIfWith()
    {
        ArrayList<Integer> objects = this.newArrayList(1, 2, 3, null);
        ArrayListIterate.removeIfWith(objects, (each6, ignored6) -> each6 == null, null);
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);

        ArrayList<Integer> objects5 = this.newArrayList(null, 1, 2, 3);
        ArrayListIterate.removeIfWith(objects5, (each5, ignored5) -> each5 == null, null);
        Verify.assertSize(3, objects5);
        Verify.assertContainsAll(objects5, 1, 2, 3);

        ArrayList<Integer> objects4 = this.newArrayList(1, null, 2, 3);
        ArrayListIterate.removeIfWith(objects4, (each4, ignored4) -> each4 == null, null);
        Verify.assertSize(3, objects4);
        Verify.assertContainsAll(objects4, 1, 2, 3);

        ArrayList<Integer> objects3 = this.newArrayList(null, null, null, null);
        ArrayListIterate.removeIfWith(objects3, (each3, ignored3) -> each3 == null, null);
        Verify.assertEmpty(objects3);

        ArrayList<Integer> objects2 = this.newArrayList(null, 1, 2, 3, null);
        ArrayListIterate.removeIfWith(objects2, (each2, ignored2) -> each2 == null, null);
        Verify.assertSize(3, objects2);
        Verify.assertContainsAll(objects2, 1, 2, 3);

        ArrayList<Integer> objects1 = this.newArrayList(1, 2, 3);
        ArrayListIterate.removeIfWith(objects1, (each1, ignored1) -> each1 == null, null);
        Verify.assertSize(3, objects1);
        Verify.assertContainsAll(objects1, 1, 2, 3);

        ThisIsNotAnArrayList<Integer> objects6 = this.newNotAnArrayList(1, 2, 3);
        ArrayListIterate.removeIfWith(objects6, (each, ignored) -> each == null, null);
        Verify.assertSize(3, objects6);
        Verify.assertContainsAll(objects6, 1, 2, 3);
    }

    @Test
    public void take()
    {
        ArrayList<Integer> list = this.getIntegerList();
        Verify.assertListsEqual(FastList.newList(list).take(0), ArrayListIterate.take(list, 0));
        Verify.assertListsEqual(FastList.newList(list).take(1), ArrayListIterate.take(list, 1));
        Verify.assertListsEqual(FastList.newList(list).take(2), ArrayListIterate.take(list, 2));
        Verify.assertListsEqual(FastList.newList(list).take(5), ArrayListIterate.take(list, 5));
        Verify.assertListsEqual(FastList.newList(list).take(list.size() - 1), ArrayListIterate.take(list, list.size() - 1));
        Verify.assertListsEqual(FastList.newList(list).take(list.size()), ArrayListIterate.take(list, list.size()));
        Verify.assertListsEqual(FastList.newList().take(2), ArrayListIterate.take(new ArrayList<>(), 2));
        ArrayList<Integer> list1 = new ArrayList<>();
        list1.addAll(Interval.oneTo(120));
        Verify.assertListsEqual(FastList.newList(list1).take(125), ArrayListIterate.take(list1, 125));
        Verify.assertListsEqual(FastList.newList(list1).take(Integer.MAX_VALUE), ArrayListIterate.take(list1, Integer.MAX_VALUE));
    }

    @Test
    public void take_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> ArrayListIterate.take(this.getIntegerList(), -1));
    }

    @Test
    public void take_target()
    {
        ArrayList<Integer> list1 = this.getIntegerList();

        MutableList<Integer> expected1 = FastList.newListWith(-1);
        expected1.addAll(FastList.newList(list1).take(2));
        Verify.assertListsEqual(expected1, ArrayListIterate.take(list1, 2, FastList.newListWith(-1)));

        MutableList<Integer> expected2 = FastList.newListWith(-1);
        expected2.addAll(FastList.newList(list1).take(0));
        Verify.assertListsEqual(expected2, ArrayListIterate.take(list1, 0, FastList.newListWith(-1)));

        MutableList<Integer> expected3 = FastList.newListWith(-1);
        expected3.addAll(FastList.newList(list1).take(5));
        Verify.assertListsEqual(expected3, ArrayListIterate.take(list1, 5, FastList.newListWith(-1)));

        Verify.assertListsEqual(FastList.newListWith(-1), ArrayListIterate.take(new ArrayList<>(), 2, FastList.newListWith(-1)));

        ArrayList<Integer> list2 = new ArrayList<>();
        list2.addAll(Interval.oneTo(120));
        FastList<Integer> integers = FastList.newList(list2);

        MutableList<Integer> expected4 = FastList.newListWith(-1);
        expected4.addAll(integers.take(125));
        Verify.assertListsEqual(expected4, ArrayListIterate.take(list2, 125, FastList.newListWith(-1)));

        MutableList<Integer> expected5 = FastList.newListWith(-1);
        expected5.addAll(integers.take(Integer.MAX_VALUE));
        Verify.assertListsEqual(expected5, ArrayListIterate.take(list2, Integer.MAX_VALUE, FastList.newListWith(-1)));
    }

    @Test
    public void take_target_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> ArrayListIterate.take(this.getIntegerList(), -1, FastList.newList()));
    }

    @Test
    public void takeWhile_small()
    {
        ArrayList<Integer> arrayList = new ArrayList<>(Interval.oneTo(100));

        assertEquals(
                iList(1, 2, 3),
                ArrayListIterate.takeWhile(arrayList, Predicates.lessThan(4)));

        assertEquals(
                Interval.fromTo(1, 100),
                ArrayListIterate.takeWhile(arrayList, Predicates.lessThan(1000)));

        assertEquals(
                iList(),
                ArrayListIterate.takeWhile(arrayList, Predicates.lessThan(0)));
    }

    @Test
    public void drop()
    {
        ArrayList<Integer> list = this.getIntegerList();
        MutableList<Integer> expected = FastList.newList(list);
        Verify.assertListsEqual(expected.drop(0), ArrayListIterate.drop(list, 0));
        Verify.assertListsEqual(expected.drop(1), ArrayListIterate.drop(list, 1));
        Verify.assertListsEqual(expected.drop(2), ArrayListIterate.drop(list, 2));
        Verify.assertListsEqual(expected.drop(5), ArrayListIterate.drop(list, 5));
        Verify.assertListsEqual(expected.drop(6), ArrayListIterate.drop(list, 6));
        Verify.assertListsEqual(expected.drop(list.size() - 1), ArrayListIterate.drop(list, list.size() - 1));
        Verify.assertListsEqual(expected.drop(list.size()), ArrayListIterate.drop(list, list.size()));
        Verify.assertListsEqual(FastList.newList(), ArrayListIterate.drop(new ArrayList<>(), 2));
        Verify.assertListsEqual(expected.drop(Integer.MAX_VALUE), ArrayListIterate.drop(list, Integer.MAX_VALUE));

        ArrayList<Integer> list1 = new ArrayList<>();
        list1.addAll(Interval.oneTo(120));
        Verify.assertListsEqual(FastList.newList(list1).drop(100), ArrayListIterate.drop(list1, 100));
        Verify.assertListsEqual(FastList.newList(list1).drop(125), ArrayListIterate.drop(list1, 125));
        Verify.assertListsEqual(FastList.newList(list1).drop(Integer.MAX_VALUE), ArrayListIterate.drop(list1, Integer.MAX_VALUE));
    }

    @Test
    public void drop_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> ArrayListIterate.drop(this.getIntegerList(), -1));
    }

    @Test
    public void drop_target()
    {
        ArrayList<Integer> list = this.getIntegerList();
        MutableList<Integer> integers1 = FastList.newList(list);

        MutableList<Integer> expected1 = FastList.newListWith(-1);
        expected1.addAll(integers1.drop(2));
        Verify.assertListsEqual(expected1, ArrayListIterate.drop(list, 2, FastList.newListWith(-1)));

        MutableList<Integer> expected2 = FastList.newListWith(-1);
        expected2.addAll(integers1.drop(5));
        Verify.assertListsEqual(expected2, ArrayListIterate.drop(list, 5, FastList.newListWith(-1)));

        MutableList<Integer> expected3 = FastList.newListWith(-1);
        expected3.addAll(integers1.drop(6));
        Verify.assertListsEqual(expected3, ArrayListIterate.drop(list, 6, FastList.newListWith(-1)));

        MutableList<Integer> expected4 = FastList.newListWith(-1);
        expected4.addAll(integers1.drop(0));
        Verify.assertListsEqual(expected4, ArrayListIterate.drop(list, 0, FastList.newListWith(-1)));

        MutableList<Integer> expected5 = FastList.newListWith(-1);
        expected5.addAll(integers1.drop(Integer.MAX_VALUE));
        Verify.assertListsEqual(expected5, ArrayListIterate.drop(list, Integer.MAX_VALUE, FastList.newListWith(-1)));

        Verify.assertListsEqual(FastList.newListWith(-1), ArrayListIterate.drop(new ArrayList<>(), 2, FastList.newListWith(-1)));

        ArrayList<Integer> list2 = new ArrayList<>();
        list2.addAll(Interval.oneTo(125));
        FastList<Integer> integers2 = FastList.newList(list2);

        MutableList<Integer> expected6 = FastList.newListWith(-1);
        expected6.addAll(integers2.drop(120));
        Verify.assertListsEqual(expected6, ArrayListIterate.drop(list2, 120, FastList.newListWith(-1)));

        MutableList<Integer> expected7 = FastList.newListWith(-1);
        expected7.addAll(integers2.drop(125));
        Verify.assertListsEqual(expected7, ArrayListIterate.drop(list2, 125, FastList.newListWith(-1)));
        Verify.assertListsEqual(FastList.newListWith(-1), ArrayListIterate.drop(list2, Integer.MAX_VALUE, FastList.newListWith(-1)));
    }

    @Test
    public void drop_target_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> ArrayListIterate.drop(this.getIntegerList(), -1, FastList.newList()));
    }

    @Test
    public void dropWhile_small()
    {
        ArrayList<Integer> arrayList = new ArrayList<>(Interval.oneTo(100));

        assertEquals(
                Interval.fromTo(4, 100),
                ArrayListIterate.dropWhile(arrayList, Predicates.lessThan(4)));

        assertEquals(
                iList(),
                ArrayListIterate.dropWhile(arrayList, Predicates.lessThan(1000)));

        assertEquals(
                Interval.fromTo(1, 100),
                ArrayListIterate.dropWhile(arrayList, Predicates.lessThan(0)));
    }

    @Test
    public void partitionWhile_small()
    {
        ArrayList<Integer> arrayList = new ArrayList<>(Interval.oneTo(100));

        PartitionMutableList<Integer> partition1 = ArrayListIterate.partitionWhile(arrayList, Predicates.lessThan(4));
        assertEquals(iList(1, 2, 3), partition1.getSelected());
        assertEquals(Interval.fromTo(4, 100), partition1.getRejected());

        PartitionMutableList<Integer> partition2 = ArrayListIterate.partitionWhile(arrayList, Predicates.lessThan(0));
        assertEquals(iList(), partition2.getSelected());
        assertEquals(Interval.fromTo(1, 100), partition2.getRejected());

        PartitionMutableList<Integer> partition3 = ArrayListIterate.partitionWhile(arrayList, Predicates.lessThan(1000));
        assertEquals(Interval.fromTo(1, 100), partition3.getSelected());
        assertEquals(iList(), partition3.getRejected());
    }

    @Test
    public void takeWhile()
    {
        ArrayList<Integer> arrayList = new ArrayList<>(Interval.oneTo(101));

        assertEquals(
                iList(1, 2, 3),
                ArrayListIterate.takeWhile(arrayList, Predicates.lessThan(4)));

        assertEquals(
                Interval.fromTo(1, 101),
                ArrayListIterate.takeWhile(arrayList, Predicates.lessThan(1000)));

        assertEquals(
                iList(),
                ArrayListIterate.takeWhile(arrayList, Predicates.lessThan(0)));
    }

    @Test
    public void dropWhile()
    {
        ArrayList<Integer> arrayList = new ArrayList<>(Interval.oneTo(101));

        assertEquals(
                Interval.fromTo(4, 101),
                ArrayListIterate.dropWhile(arrayList, Predicates.lessThan(4)));

        assertEquals(
                iList(),
                ArrayListIterate.dropWhile(arrayList, Predicates.lessThan(1000)));

        assertEquals(
                Interval.fromTo(1, 101),
                ArrayListIterate.dropWhile(arrayList, Predicates.lessThan(0)));
    }

    @Test
    public void partitionWhile()
    {
        ArrayList<Integer> arrayList = new ArrayList<>(Interval.oneTo(101));

        PartitionMutableList<Integer> partition1 = ArrayListIterate.partitionWhile(arrayList, Predicates.lessThan(4));
        assertEquals(iList(1, 2, 3), partition1.getSelected());
        assertEquals(Interval.fromTo(4, 101), partition1.getRejected());

        PartitionMutableList<Integer> partition2 = ArrayListIterate.partitionWhile(arrayList, Predicates.lessThan(0));
        assertEquals(iList(), partition2.getSelected());
        assertEquals(Interval.fromTo(1, 101), partition2.getRejected());

        PartitionMutableList<Integer> partition3 = ArrayListIterate.partitionWhile(arrayList, Predicates.lessThan(1000));
        assertEquals(Interval.fromTo(1, 101), partition3.getSelected());
        assertEquals(iList(), partition3.getRejected());
    }

    private ArrayList<Integer> newArrayList(Integer... items)
    {
        return new ArrayList<>(mList(items));
    }

    private ThisIsNotAnArrayList<Integer> newNotAnArrayList(Integer... items)
    {
        return new ThisIsNotAnArrayList<>(mList(items));
    }

    @Test
    public void groupByWithOptimisedList()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.toReverseList(1, 105));
        MutableMultimap<String, Integer> target = new FastListMultimap<>();
        MutableMultimap<String, Integer> result = ArrayListIterate.groupBy(list, String::valueOf, target);
        assertEquals(result.get("105"), FastList.newListWith(105));
    }

    @Test
    public void groupByEachWithOptimisedList()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.toReverseList(1, 105));
        Function<Integer, Iterable<String>> function = object -> FastList.newListWith(object.toString(), object.toString() + '*');
        MutableMultimap<String, Integer> target = new FastListMultimap<>();
        MutableMultimap<String, Integer> result = ArrayListIterate.groupByEach(list, function, target);
        assertEquals(result.get("105"), FastList.newListWith(105));
        assertEquals(result.get("105*"), FastList.newListWith(105));
    }

    @Test
    public void groupByUniqueKeyWithOptimisedList()
    {
        ArrayList<Integer> list1 = new ArrayList<>(Interval.toReverseList(1, 3));
        assertEquals(
                UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3),
                ArrayListIterate.groupByUniqueKey(list1, id -> id));
        ArrayList<Integer> list2 = new ArrayList<>(Interval.toReverseList(1, 105));
        assertEquals(
                Lists.mutable.ofAll(list2).groupByUniqueKey(id -> id),
                ArrayListIterate.groupByUniqueKey(list2, id -> id));
    }

    @Test
    public void groupByUniqueKey_throws_for_null()
    {
        assertThrows(IllegalArgumentException.class, () -> ArrayListIterate.groupByUniqueKey(null, id -> id));
    }

    @Test
    public void groupByUniqueKeyUniqueKey_throws_for_duplicate()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.toReverseList(1, 105));
        list.add(2);
        assertThrows(IllegalStateException.class, () -> ArrayListIterate.groupByUniqueKey(list, id -> id));
    }

    @Test
    public void groupByUniqueKeyWithOptimisedList_target()
    {
        ArrayList<Integer> list1 = new ArrayList<>(Interval.toReverseList(1, 3));
        assertEquals(
                UnifiedMap.newWithKeysValues(0, 0, 1, 1, 2, 2, 3, 3),
                ArrayListIterate.groupByUniqueKey(list1, id -> id, UnifiedMap.newWithKeysValues(0, 0)));

        ArrayList<Integer> list2 = new ArrayList<>(Interval.toReverseList(1, 105));
        assertEquals(
                Lists.mutable.ofAll(list2).groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(0, 0)),
                ArrayListIterate.groupByUniqueKey(list2, id -> id, UnifiedMap.newWithKeysValues(0, 0)));
    }

    @Test
    public void groupByUniqueKey_target_throws_for_null()
    {
        assertThrows(IllegalArgumentException.class, () -> ArrayListIterate.groupByUniqueKey(null, id -> id, UnifiedMap.newWithKeysValues(0, 0)));
    }

    @Test
    public void groupByUniqueKeyUniqueKey_target_throws_for_duplicate()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.toReverseList(1, 105));
        assertThrows(IllegalStateException.class, () -> ArrayListIterate.groupByUniqueKey(list, id -> id, UnifiedMap.newWithKeysValues(2, 2)));
    }

    @Test
    public void flattenWithOptimisedArrays()
    {
        ArrayList<Integer> list = new ArrayList<>(Interval.toReverseList(1, 105));

        ArrayList<Integer> result = ArrayListIterate.flatCollect(list, new CollectionWrappingFunction<>(),
                new ArrayList<>());
        assertEquals(105, result.get(0).intValue());
    }

    private static class CollectionWrappingFunction<T> implements Function<T, Collection<T>>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Collection<T> valueOf(T value)
        {
            return FastList.newListWith(value);
        }
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(ArrayListIterate.class);
    }

    @Test
    public void zip()
    {
        ArrayList<Integer> integers = new ArrayList<>(Interval.oneTo(3));
        List<Twin<Integer>> expected = Lists.mutable.with(
                Tuples.twin(1, 1),
                Tuples.twin(2, 2),
                Tuples.twin(3, 3));
        assertEquals(expected, ArrayListIterate.zip(integers, integers));
        assertEquals(expected, ArrayListIterate.zip(integers, integers::iterator));
    }
}
