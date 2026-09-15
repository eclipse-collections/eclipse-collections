/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.factory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.collections.api.factory.list.FixedSizeListFactory;
import org.eclipse.collections.api.factory.list.ImmutableListFactory;
import org.eclipse.collections.api.factory.list.MultiReaderListFactory;
import org.eclipse.collections.api.factory.list.MutableListFactory;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MultiReaderList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.MultiReaderFastList;
import org.eclipse.collections.impl.list.primitive.IntInterval;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ListsTest
{
    @Test
    public void immutables()
    {
        ImmutableListFactory listFactory = Lists.immutable;
        assertEquals(FastList.newList(), listFactory.of());
        assertEquals(FastList.newList(), listFactory.with());
        assertEquals(FastList.newList(), listFactory.empty());
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of());
        Verify.assertInstanceOf(ImmutableList.class, listFactory.with());
        Verify.assertInstanceOf(ImmutableList.class, listFactory.empty());
        assertEquals(FastList.newListWith(1), listFactory.of(1));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1));
        assertEquals(FastList.newListWith(1, 2), listFactory.of(1, 2));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2));
        assertEquals(FastList.newListWith(1, 2, 3), listFactory.of(1, 2, 3));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3));
        assertEquals(FastList.newListWith(1, 2, 3, 4), listFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), listFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6), listFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        assertEquals(FastList.newListWith(1, 2, 3), listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        assertEquals(FastList.newListWith(1, 2, 3), listFactory.fromStream(Stream.of(1, 2, 3)));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.fromStream(Stream.of(1, 2, 3)));
        assertEquals(Lists.mutable.of(1, 2, 3), listFactory.withAllSorted(Lists.mutable.<Integer>of(3, 1, 2)));
    }

    @Test
    public void mutables()
    {
        MutableListFactory listFactory = Lists.mutable;
        assertEquals(FastList.newList(), listFactory.of());
        assertEquals(FastList.newList(), listFactory.with());
        assertEquals(FastList.newList(), listFactory.empty());
        Verify.assertInstanceOf(MutableList.class, listFactory.of());
        Verify.assertInstanceOf(MutableList.class, listFactory.with());
        Verify.assertInstanceOf(MutableList.class, listFactory.empty());
        assertEquals(FastList.newListWith(1), listFactory.of(1));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1));
        assertEquals(FastList.newListWith(1, 2), listFactory.of(1, 2));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1, 2));
        assertEquals(FastList.newListWith(1, 2, 3), listFactory.of(1, 2, 3));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1, 2, 3));
        assertEquals(FastList.newListWith(1, 2, 3, 4), listFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1, 2, 3, 4));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), listFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1, 2, 3, 4, 5));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6), listFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1, 2, 3, 4, 5, 6));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(MutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        assertEquals(FastList.newListWith(1, 2, 3), listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        Verify.assertInstanceOf(MutableList.class, listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        assertEquals(FastList.newListWith(1, 2, 3), listFactory.fromStream(Stream.of(1, 2, 3)));
        Verify.assertInstanceOf(MutableList.class, listFactory.fromStream(Stream.of(1, 2, 3)));
    }

    @Test
    public void wrapCopy()
    {
        Integer[] integers = {1, 2, 3, 4};
        MutableList<Integer> actual = Lists.mutable.wrapCopy(integers);
        MutableList<Integer> expected = Lists.mutable.with(1, 2, 3, 4);
        integers[0] = Integer.valueOf(4);
        assertEquals(expected, actual);
    }

    @Test
    public void immutableWithListTest()
    {
        assertEquals(Lists.mutable.of(), Lists.mutable.of().toImmutable());
        assertEquals(Lists.mutable.of(1).without(1), Lists.mutable.of(1).without(1).toImmutable());
        for (int i = 0; i < 12; i++)
        {
            MutableList<Integer> integers = Interval.fromTo(0, i).toList();
            assertEquals(integers, integers.toImmutable());
            assertEquals(integers.toImmutable(), integers.toImmutable());
        }
    }

    @Test
    public void fixedSize()
    {
        FixedSizeListFactory listFactory = Lists.fixedSize;
        assertEquals(FastList.newList(), listFactory.of());
        assertEquals(FastList.newList(), listFactory.with());
        assertEquals(FastList.newList(), listFactory.empty());
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of());
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.with());
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.empty());
        assertEquals(FastList.newListWith(1), listFactory.of(1));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1));
        assertEquals(FastList.newListWith(1, 2), listFactory.of(1, 2));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2));
        assertEquals(FastList.newListWith(1, 2, 3), listFactory.of(1, 2, 3));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3));
        assertEquals(FastList.newListWith(1, 2, 3, 4), listFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), listFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6), listFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        assertEquals(FastList.newListWith(1, 2, 3), listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        assertEquals(FastList.newListWith(1, 2, 3), listFactory.fromStream(Stream.of(1, 2, 3)));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.fromStream(Stream.of(1, 2, 3)));
    }

    @Test
    public void multiReader()
    {
        this.testMultiReaderApi(Lists.multiReader);
        this.testMultiReaderApi(org.eclipse.collections.api.factory.Lists.multiReader);
    }

    private void testMultiReaderApi(MultiReaderListFactory listFactory)
    {
        assertEquals(MultiReaderFastList.newList(), listFactory.of());
        assertEquals(MultiReaderFastList.newList(), listFactory.with());
        assertEquals(MultiReaderFastList.newList(), listFactory.empty());
        Verify.assertInstanceOf(MultiReaderList.class, listFactory.of());
        Verify.assertInstanceOf(MultiReaderList.class, listFactory.with());
        Verify.assertInstanceOf(MultiReaderList.class, listFactory.empty());
        assertEquals(MultiReaderFastList.newListWith(1), listFactory.of(1));
        Verify.assertInstanceOf(MultiReaderList.class, listFactory.of(1));
        assertEquals(MultiReaderFastList.newListWith(1, 2, 3), listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        Verify.assertInstanceOf(MultiReaderList.class, listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        assertEquals(MultiReaderFastList.newListWith(1, 2, 3), listFactory.fromStream(Stream.of(1, 2, 3)));
        Verify.assertInstanceOf(MultiReaderList.class, listFactory.fromStream(Stream.of(1, 2, 3)));
    }

    @Test
    public void castToList()
    {
        List<Object> list = Lists.immutable.of().castToList();
        assertNotNull(list);
        assertSame(Lists.immutable.of(), list);
    }

    @Test
    public void ofAllImmutableList()
    {
        for (int i = 1; i <= 11; i++)
        {
            Interval interval = Interval.oneTo(i);
            Verify.assertEqualsAndHashCode(interval, Lists.immutable.ofAll(interval));
        }
    }

    @Test
    public void ofAllMutableList()
    {
        for (int i = 1; i <= 11; i++)
        {
            Interval interval = Interval.oneTo(i);
            Verify.assertEqualsAndHashCode(interval, Lists.mutable.ofAll(interval));
            Stream<Integer> stream = IntStream.rangeClosed(1, i).boxed();
            Verify.assertEqualsAndHashCode(interval, Lists.mutable.fromStream(stream));
        }
    }

    @Test
    public void ofAllFixedSizedList()
    {
        for (int i = 1; i <= 11; i++)
        {
            Interval interval = Interval.oneTo(i);
            Verify.assertEqualsAndHashCode(interval, Lists.fixedSize.ofAll(interval));
            Stream<Integer> stream = IntStream.rangeClosed(1, i).boxed();
            Verify.assertEqualsAndHashCode(interval, Lists.fixedSize.fromStream(stream));
        }
    }

    @Test
    public void copyList()
    {
        Verify.assertInstanceOf(ImmutableList.class, Lists.immutable.ofAll(Lists.fixedSize.of()));
        MutableList<Integer> list = Lists.fixedSize.of(1);
        ImmutableList<Integer> immutableList = list.toImmutable();
        Verify.assertInstanceOf(ImmutableList.class, Lists.immutable.ofAll(list));
        assertSame(Lists.immutable.ofAll(immutableList.castToList()), immutableList);
    }

    @Test
    public void emptyList()
    {
        assertTrue(Lists.immutable.of().isEmpty());
        assertSame(Lists.immutable.of(), Lists.immutable.of());
        Verify.assertPostSerializedIdentity(Lists.immutable.of());
    }

    @Test
    public void ofInitialCapacity()
    {
        MutableList<String> list1 = Lists.mutable.ofInitialCapacity(0);
        this.assertPresizedListEquals(0, (FastList<String>) list1);

        MutableList<String> list2 = Lists.mutable.ofInitialCapacity(5);
        this.assertPresizedListEquals(5, (FastList<String>) list2);

        MutableList<String> list3 = Lists.mutable.ofInitialCapacity(20);
        this.assertPresizedListEquals(20, (FastList<String>) list3);

        MutableList<String> list4 = Lists.mutable.ofInitialCapacity(60);
        this.assertPresizedListEquals(60, (FastList<String>) list4);

        MutableList<String> list5 = Lists.mutable.ofInitialCapacity(64);
        this.assertPresizedListEquals(64, (FastList<String>) list5);

        MutableList<String> list6 = Lists.mutable.ofInitialCapacity(65);
        this.assertPresizedListEquals(65, (FastList<String>) list6);

        assertThrows(IllegalArgumentException.class, () -> Lists.mutable.ofInitialCapacity(-12));
    }

    @Test
    public void withInitialCapacity()
    {
        MutableList<String> list1 = Lists.mutable.withInitialCapacity(0);
        this.assertPresizedListEquals(0, (FastList<String>) list1);

        MutableList<String> list2 = Lists.mutable.withInitialCapacity(14);
        this.assertPresizedListEquals(14, (FastList<String>) list2);

        MutableList<String> list3 = Lists.mutable.withInitialCapacity(17);
        this.assertPresizedListEquals(17, (FastList<String>) list3);

        MutableList<String> list4 = Lists.mutable.withInitialCapacity(25);
        this.assertPresizedListEquals(25, (FastList<String>) list4);

        MutableList<String> list5 = Lists.mutable.withInitialCapacity(32);
        this.assertPresizedListEquals(32, (FastList<String>) list5);

        assertThrows(IllegalArgumentException.class, () -> Lists.mutable.withInitialCapacity(-6));
    }

    private void assertPresizedListEquals(int initialCapacity, FastList<String> list)
    {
        try
        {
            Field itemsField = FastList.class.getDeclaredField("items");
            itemsField.setAccessible(true);
            Object[] items = (Object[]) itemsField.get(list);
            assertEquals(initialCapacity, items.length);
        }
        catch (SecurityException ignored)
        {
            fail("Unable to modify the visibility of the field 'items' on FastList");
        }
        catch (NoSuchFieldException ignored)
        {
            fail("No field named 'items' in FastList");
        }
        catch (IllegalAccessException ignored)
        {
            fail("No access to the field 'items' in FastList");
        }
    }

    @Test
    public void multiReaderOfInitialCapacity()
    {
        MutableList<String> list1 = Lists.multiReader.ofInitialCapacity(0);
        ListsTest.assertPresizedMultiReaderListEquals(0, (MultiReaderFastList<String>) list1);

        MutableList<String> list2 = Lists.multiReader.ofInitialCapacity(5);
        ListsTest.assertPresizedMultiReaderListEquals(5, (MultiReaderFastList<String>) list2);

        MutableList<String> list3 = Lists.multiReader.ofInitialCapacity(20);
        ListsTest.assertPresizedMultiReaderListEquals(20, (MultiReaderFastList<String>) list3);

        MutableList<String> list4 = Lists.multiReader.ofInitialCapacity(60);
        ListsTest.assertPresizedMultiReaderListEquals(60, (MultiReaderFastList<String>) list4);

        MutableList<String> list5 = Lists.multiReader.ofInitialCapacity(64);
        ListsTest.assertPresizedMultiReaderListEquals(64, (MultiReaderFastList<String>) list5);

        MutableList<String> list6 = Lists.multiReader.ofInitialCapacity(65);
        ListsTest.assertPresizedMultiReaderListEquals(65, (MultiReaderFastList<String>) list6);

        assertThrows(IllegalArgumentException.class, () -> Lists.multiReader.ofInitialCapacity(-12));
    }

    @Test
    public void multiReaderWithInitialCapacity()
    {
        MutableList<String> list1 = Lists.multiReader.withInitialCapacity(0);
        ListsTest.assertPresizedMultiReaderListEquals(0, (MultiReaderFastList<String>) list1);

        MutableList<String> list2 = Lists.multiReader.withInitialCapacity(14);
        ListsTest.assertPresizedMultiReaderListEquals(14, (MultiReaderFastList<String>) list2);

        MutableList<String> list3 = Lists.multiReader.withInitialCapacity(17);
        ListsTest.assertPresizedMultiReaderListEquals(17, (MultiReaderFastList<String>) list3);

        MutableList<String> list4 = Lists.multiReader.withInitialCapacity(25);
        ListsTest.assertPresizedMultiReaderListEquals(25, (MultiReaderFastList<String>) list4);

        MutableList<String> list5 = Lists.multiReader.withInitialCapacity(32);
        ListsTest.assertPresizedMultiReaderListEquals(32, (MultiReaderFastList<String>) list5);

        assertThrows(IllegalArgumentException.class, () -> Lists.multiReader.withInitialCapacity(-6));
    }

    @Test
    public void withNValues()
    {
        ImmutableList<Integer> expected =
                IntInterval.oneTo(10).collect(each -> 1);
        MutableList<Integer> mutable =
                Lists.mutable.withNValues(10, () -> 1);
        MutableList<Integer> multiReader =
                Lists.multiReader.withNValues(10, () -> 1);
        assertEquals(expected, mutable);
        assertEquals(expected, multiReader);
    }

    private static void assertPresizedMultiReaderListEquals(int initialCapacity, MultiReaderFastList<String> list)
    {
        try
        {
            Field delegateField = MultiReaderFastList.class.getDeclaredField("delegate");
            delegateField.setAccessible(true);
            FastList<String> delegate = (FastList<String>) delegateField.get(list);
            Field itemsField = FastList.class.getDeclaredField("items");
            itemsField.setAccessible(true);
            Object[] items = (Object[]) itemsField.get(delegate);
            assertEquals(initialCapacity, items.length);
        }
        catch (SecurityException | NoSuchFieldException | IllegalAccessException e)
        {
            throw new AssertionError(e);
        }
    }

    @Test
    public void newListWith()
    {
        ImmutableList<String> list = Lists.immutable.of();
        assertEquals(list, Lists.immutable.of(list.toArray()));
        assertEquals(list = list.newWith("1"), Lists.immutable.of("1"));
        assertEquals(list = list.newWith("2"), Lists.immutable.of("1", "2"));
        assertEquals(list = list.newWith("3"), Lists.immutable.of("1", "2", "3"));
        assertEquals(list = list.newWith("4"), Lists.immutable.of("1", "2", "3", "4"));
        assertEquals(list = list.newWith("5"), Lists.immutable.of("1", "2", "3", "4", "5"));
        assertEquals(list = list.newWith("6"), Lists.immutable.of("1", "2", "3", "4", "5", "6"));
        assertEquals(list = list.newWith("7"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7"));
        assertEquals(list = list.newWith("8"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8"));
        assertEquals(list = list.newWith("9"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        assertEquals(list = list.newWith("10"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        assertEquals(list = list.newWith("11"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        assertEquals(list = list.newWith("12"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
    }

    @Test
    public void newListWithArray()
    {
        ImmutableList<String> list = Lists.immutable.of();
        assertEquals(list = list.newWith("1"), Lists.immutable.of(new String[]{"1"}));
        assertEquals(list = list.newWith("2"), Lists.immutable.of(new String[]{"1", "2"}));
        assertEquals(list = list.newWith("3"), Lists.immutable.of(new String[]{"1", "2", "3"}));
        assertEquals(list = list.newWith("4"), Lists.immutable.of(new String[]{"1", "2", "3", "4"}));
        assertEquals(list = list.newWith("5"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5"}));
        assertEquals(list = list.newWith("6"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6"}));
        assertEquals(list = list.newWith("7"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7"}));
        assertEquals(list = list.newWith("8"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}));
        assertEquals(list = list.newWith("9"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"}));
        assertEquals(list = list.newWith("10"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
        assertEquals(list = list.newWith("11"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
    }

    @Test
    public void newListWithList()
    {
        ImmutableList<String> list = Lists.immutable.of();
        FastList<String> fastList = FastList.newListWith("1");
        assertEquals(list = list.newWith("1"), fastList.toImmutable());
        assertEquals(list = list.newWith("2"), fastList.with("2").toImmutable());
        assertEquals(list = list.newWith("3"), fastList.with("3").toImmutable());
        assertEquals(list = list.newWith("4"), fastList.with("4").toImmutable());
        assertEquals(list = list.newWith("5"), fastList.with("5").toImmutable());
        assertEquals(list = list.newWith("6"), fastList.with("6").toImmutable());
        assertEquals(list = list.newWith("7"), fastList.with("7").toImmutable());
        assertEquals(list = list.newWith("8"), fastList.with("8").toImmutable());
        assertEquals(list = list.newWith("9"), fastList.with("9").toImmutable());
        assertEquals(list = list.newWith("10"), fastList.with("10").toImmutable());
        assertEquals(list = list.newWith("11"), fastList.with("11").toImmutable());
    }

    @Test
    public void newListWithWithList()
    {
        assertEquals(FastList.newList(), Lists.immutable.ofAll(FastList.newList()));
        for (int i = 0; i < 12; i++)
        {
            List<Integer> list = Interval.fromTo(0, i);
            assertEquals(list, Lists.immutable.ofAll(list));
        }
    }

    @Test
    public void withAllEmptyImmutableSame()
    {
        ImmutableList<Integer> empty = Lists.immutable.withAll(Collections.emptyList());
        ImmutableList<Integer> integers = Lists.immutable.<Integer>empty().newWithAll(Lists.immutable.empty());
        ImmutableList<Integer> empty2 = Lists.immutable.withAll(integers);
        assertSame(Lists.immutable.empty(), empty);
        assertSame(Lists.immutable.empty(), empty2);
    }

    @Test
    public void withAllSortedImmutable()
    {
        assertEquals(Lists.immutable.of(1, 5, 50, 100),
                Lists.immutable.withAllSorted(Lists.mutable.of(50, 5, 100, 1)));
    }

    @Test
    public void withAllSortedImmutableWithComparator()
    {
        assertEquals(Lists.immutable.of(100, 50, 5, 1),
                Lists.immutable.withAllSorted(Comparators.reverseNaturalOrder(), Lists.mutable.of(50, 5, 100, 1)));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(Lists.class);
    }
}
