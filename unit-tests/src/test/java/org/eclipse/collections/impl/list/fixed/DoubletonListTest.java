/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.fixed;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link DoubletonList}.
 */
public class DoubletonListTest extends AbstractMemoryEfficientMutableListTestCase
{
    @Override
    protected int getSize()
    {
        return 2;
    }

    @Override
    protected Class<?> getListType()
    {
        return DoubletonList.class;
    }

    @Test
    public void testClone()
    {
        MutableList<String> growableList = this.list.clone();
        Verify.assertEqualsAndHashCode(this.list, growableList);
        Verify.assertInstanceOf(DoubletonList.class, growableList);
    }

    @Test
    public void testContains()
    {
        assertTrue(this.list.contains("1"));
        assertTrue(this.list.contains("2"));
        assertFalse(this.list.contains("3"));
    }

    @Test
    public void testRemove()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.list.remove(0));
    }

    @Test
    public void testAddAtIndex()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.list.add(0, "1"));
    }

    @Test
    public void testAdd()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.list.add("1"));
    }

    @Test
    public void testAddingAllToOtherList()
    {
        List<String> newList = new ArrayList<>(this.list);
        newList.add("3");
        Verify.assertStartsWith(newList, "1", "2", "3");
    }

    @Test
    public void testGet()
    {
        Verify.assertStartsWith(this.list, "1", "2");
        assertThrows(IndexOutOfBoundsException.class, () -> this.list.get(2));
    }

    @Test
    public void testSet()
    {
        assertEquals("1", this.list.set(0, "2"));
        assertEquals("2", this.list.set(1, "1"));
        assertEquals(FastList.newListWith("2", "1"), this.list);
        assertThrows(IndexOutOfBoundsException.class, () -> this.list.set(2, "0"));
    }

    @Test
    public void testSerialization()
    {
        Verify.assertPostSerializedEqualsAndHashCode(this.list);

        MutableList<String> copy = SerializeTestHelper.serializeDeserialize(this.list);
        Verify.assertInstanceOf(DoubletonList.class, copy);
        Verify.assertSize(2, copy);
        Verify.assertContainsAll(copy, "1", "2");
        Verify.assertNotContains("3", copy);
    }

    @Test
    public void testEqualsAndHashCode()
    {
        MutableList<String> one = this.classUnderTest();
        List<String> oneA = new ArrayList<>(one);
        Verify.assertEqualsAndHashCode(one, oneA);
        Verify.assertPostSerializedEqualsAndHashCode(one);
    }

    @Test
    public void testForEach()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = this.classUnderTest();
        source.forEach(CollectionAddProcedure.on(result));
        assertEquals(FastList.newListWith("1", "2"), result);
    }

    @Test
    public void testForEachWithIndex()
    {
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = this.classUnderTest();
        source.forEachWithIndex((each, index) ->
        {
            result.add(each);
            indexSum[0] += index;
        });
        assertEquals(FastList.newListWith("1", "2"), result);
        assertEquals(1, indexSum[0]);
    }

    @Test
    public void testForEachWith()
    {
        MutableList<String> result = Lists.mutable.of();
        this.list.forEachWith(Procedures2.fromProcedure(result::add), null);
        assertEquals(FastList.newListWith("1", "2"), result);
    }

    @Test
    public void testGetFirstGetLast()
    {
        assertEquals("1", this.list.getFirst());
        assertEquals("2", this.list.getLast());
    }

    @Test
    public void testForLoop()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two");
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO");
        for (String each : list)
        {
            Verify.assertContains(each.toUpperCase(Locale.ROOT), upperList);
        }
    }

    @Test
    public void testSubList()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two");
        MutableList<String> subList = list.subList(0, 2);
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO");
        for (String each : subList)
        {
            Verify.assertContains(each.toUpperCase(Locale.ROOT), upperList);
        }
        assertEquals("one", subList.getFirst());
        assertEquals("two", subList.getLast());
        MutableList<String> subList2 = list.subList(1, 2);
        assertEquals("two", subList2.getFirst());
        assertEquals("two", subList2.getLast());
        MutableList<String> subList3 = list.subList(0, 1);
        assertEquals("one", subList3.getFirst());
        assertEquals("one", subList3.getLast());
    }

    @Test
    public void without()
    {
        MutableList<Integer> list = new DoubletonList<>(2, 2);
        assertSame(list, list.without(9));
        list = list.without(2);
        Verify.assertListsEqual(FastList.newListWith(2), list);
        Verify.assertInstanceOf(SingletonList.class, list);
    }

    @Test
    public void testGetOnly()
    {
        assertThrows(IllegalStateException.class, () -> this.list.getOnly());
    }

    @Override
    @Test
    public void sort()
    {
        super.sort();
        MutableList<String> strings = this.classUnderTest().reverseThis();
        strings.sort(Comparator.naturalOrder());
        assertEquals(this.classUnderTest(), strings);
        MutableList<String> strings2 = this.classUnderTest().reverseThis();
        strings2.sort(null);
        assertEquals(this.classUnderTest(), strings2);
    }
}
