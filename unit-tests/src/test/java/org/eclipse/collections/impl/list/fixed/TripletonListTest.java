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

import java.util.ListIterator;
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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link TripletonList}.
 */
public class TripletonListTest extends AbstractMemoryEfficientMutableListTestCase
{
    @Override
    protected int getSize()
    {
        return 3;
    }

    @Override
    protected Class<?> getListType()
    {
        return TripletonList.class;
    }

    @Test
    public void testClone()
    {
        MutableList<String> growableList = this.list.clone();
        Verify.assertEqualsAndHashCode(this.list, growableList);
        Verify.assertInstanceOf(TripletonList.class, growableList);
    }

    @Test
    public void testContains()
    {
        assertTrue(this.list.contains("1"));
        assertTrue(this.list.contains("2"));
        assertTrue(this.list.contains("3"));
        assertFalse(this.list.contains("4"));
    }

    @Test
    public void testRemove()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.list.remove(0));
        this.assertUnchanged();
    }

    @Test
    public void testAddAtIndex()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.list.add(0, "1"));
        this.assertUnchanged();
    }

    @Test
    public void testAdd()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.list.add("1"));
        this.assertUnchanged();
    }

    @Test
    public void testAddingAllToOtherList()
    {
        MutableList<String> newList = FastList.newList(this.list);
        newList.add("4");
        assertEquals(FastList.newListWith("1", "2", "3", "4"), newList);
    }

    @Test
    public void testGet()
    {
        Verify.assertStartsWith(this.list, "1", "2", "3");
        assertThrows(IndexOutOfBoundsException.class, () -> this.list.get(3));
    }

    @Test
    public void testSet()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        assertEquals("1", list.set(0, "3"));
        assertEquals("2", list.set(1, "2"));
        assertEquals("3", list.set(2, "1"));
        assertEquals(FastList.newListWith("3", "2", "1"), list);
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(3, "0"));
    }

    private void assertUnchanged()
    {
        Verify.assertInstanceOf(TripletonList.class, this.list);
        Verify.assertSize(3, this.list);
        Verify.assertNotContains("4", this.list);
        assertEquals(FastList.newListWith("1", "2", "3"), this.list);
    }

    @Test
    public void testSerializableEqualsAndHashCode()
    {
        Verify.assertPostSerializedEqualsAndHashCode(this.list);
        MutableList<String> copyOfList = SerializeTestHelper.serializeDeserialize(this.list);
        assertNotSame(this.list, copyOfList);
    }

    @Test
    public void testCreate1()
    {
        MutableList<String> list = Lists.fixedSize.of("1");
        Verify.assertSize(1, list);
        Verify.assertItemAtIndex("1", 0, list);
    }

    @Test
    public void testEqualsAndHashCode()
    {
        MutableList<String> one = Lists.fixedSize.of("1", "2", "3");
        MutableList<String> oneA = FastList.newList(one);
        Verify.assertEqualsAndHashCode(one, oneA);
        Verify.assertPostSerializedEqualsAndHashCode(one);
    }

    @Test
    public void testForEach()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEach(CollectionAddProcedure.on(result));
        assertEquals(FastList.newListWith("1", "2", "3"), result);
    }

    @Test
    public void forEachFromTo()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEach(0, 2, result::add);
        assertEquals(FastList.newListWith("1", "2", "3"), result);
    }

    @Test
    public void forEachWithIndex()
    {
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEachWithIndex((each, index) ->
        {
            result.add(each);
            indexSum[0] += index;
        });
        assertEquals(FastList.newListWith("1", "2", "3"), result);
        assertEquals(3, indexSum[0]);
    }

    @Test
    public void forEachWithIndexFromTo()
    {
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEachWithIndex(0, 2, (each, index) ->
        {
            result.add(each);
            indexSum[0] += index;
        });
        assertEquals(FastList.newListWith("1", "2", "3"), result);
        assertEquals(3, indexSum[0]);
    }

    @Test
    public void testForEachWith()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEachWith(Procedures2.fromProcedure(result::add), null);
        assertEquals(FastList.newListWith("1", "2", "3"), result);
    }

    @Test
    public void testGetFirstGetLast()
    {
        MutableList<String> list3 = Lists.fixedSize.of("1", "2", "3");
        assertEquals("1", list3.getFirst());
        assertEquals("3", list3.getLast());
    }

    @Test
    public void testForLoop()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO", "THREE");
        for (String each : list)
        {
            Verify.assertContains(each.toUpperCase(Locale.ROOT), upperList);
        }
    }

    @Test
    public void testSubList()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        MutableList<String> subList = list.subList(0, 3);
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO", "THREE");
        for (String each : subList)
        {
            Verify.assertContains(each.toUpperCase(Locale.ROOT), upperList);
        }
        assertEquals("one", subList.getFirst());
        assertEquals("three", subList.getLast());
        MutableList<String> subList2 = list.subList(1, 2);
        assertEquals("two", subList2.getFirst());
        assertEquals("two", subList2.getLast());
        MutableList<String> subList3 = list.subList(0, 1);
        assertEquals("one", subList3.getFirst());
        assertEquals("one", subList3.getLast());
        MutableList<String> subList4 = subList.subList(1, 3);
        assertEquals("two", subList4.getFirst());
        assertEquals("three", subList4.getLast());
    }

    @Test
    public void testListIterator()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        ListIterator<String> iterator = list.listIterator();
        assertTrue(iterator.hasNext());
        assertFalse(iterator.hasPrevious());
        assertEquals("one", iterator.next());
        assertEquals("two", iterator.next());
        assertEquals("three", iterator.next());
        assertTrue(iterator.hasPrevious());
        assertEquals("three", iterator.previous());
        assertEquals("two", iterator.previous());
        assertEquals("one", iterator.previous());
        iterator.set("1");
        assertEquals("1", iterator.next());
        assertEquals("1", list.getFirst());
        list.subList(1, 3);
    }

    @Test
    public void testSubListListIterator()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        MutableList<String> subList = list.subList(1, 3);
        ListIterator<String> iterator = subList.listIterator();
        assertTrue(iterator.hasNext());
        assertFalse(iterator.hasPrevious());
        assertEquals("two", iterator.next());
        assertEquals("three", iterator.next());
        assertTrue(iterator.hasPrevious());
        assertEquals("three", iterator.previous());
        assertEquals("two", iterator.previous());
        iterator.set("2");
        assertEquals("2", iterator.next());
        assertEquals("2", subList.getFirst());
        assertEquals("2", list.get(1));
    }

    @Test
    public void testSubListSet()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        MutableList<String> subList = list.subList(1, 3);
        assertEquals("two", subList.set(0, "2"));
        assertEquals("2", subList.getFirst());
        assertEquals("2", list.get(1));
    }

    @Test
    public void testNewEmpty()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        Verify.assertEmpty(list.newEmpty());
    }

    @Test
    public void subListForEach()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        MutableList<String> source = list.subList(1, 3);
        MutableList<String> result = Lists.mutable.of();
        source.forEach(CollectionAddProcedure.on(result));
        assertEquals(FastList.newListWith("2", "3"), result);
    }

    @Test
    public void testSubListForEachWithIndex()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        MutableList<String> source = list.subList(1, 3);
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        source.forEachWithIndex((each, index) ->
        {
            result.add(each);
            indexSum[0] += index;
        });
        assertEquals(FastList.newListWith("2", "3"), result);
        assertEquals(1, indexSum[0]);
    }

    @Test
    public void testSubListForEachWith()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        MutableList<String> source = list.subList(1, 3);
        MutableList<String> result = Lists.mutable.of();
        source.forEachWith(Procedures2.fromProcedure(result::add), null);
        assertEquals(FastList.newListWith("2", "3"), result);
    }

    @Test
    public void testIndexOf()
    {
        MutableList<String> list = Lists.fixedSize.of("1", null, "3");
        assertEquals(0, list.indexOf("1"));
        assertEquals(1, list.indexOf(null));
        assertEquals(2, list.indexOf("3"));
        assertEquals(-1, list.indexOf("4"));
    }

    @Test
    public void testLastIndexOf()
    {
        MutableList<String> list = Lists.fixedSize.of("1", null, "1");
        assertEquals(2, list.lastIndexOf("1"));
        assertEquals(1, list.lastIndexOf(null));
        assertEquals(-1, list.lastIndexOf("4"));
    }

    @Test
    public void without()
    {
        MutableList<Integer> list = new TripletonList<>(2, 3, 2);
        assertSame(list, list.without(9));
        list = list.without(2);
        Verify.assertListsEqual(FastList.newListWith(3, 2), list);
        Verify.assertInstanceOf(DoubletonList.class, list);
    }

    @Test
    public void testGetOnly()
    {
        assertThrows(IllegalStateException.class, () -> this.list.getOnly());
    }
}
