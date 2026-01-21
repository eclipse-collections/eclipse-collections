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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link QuadrupletonList}.
 */
public class QuadrupletonListTest extends AbstractMemoryEfficientMutableListTestCase
{
    @Override
    protected int getSize()
    {
        return 4;
    }

    @Override
    protected Class<?> getListType()
    {
        return QuadrupletonList.class;
    }

    @Test
    public void testClone()
    {
        MutableList<String> growableList = this.list.clone();
        Verify.assertEqualsAndHashCode(this.list, growableList);
        Verify.assertInstanceOf(QuadrupletonList.class, growableList);
    }

    @Test
    public void testContains()
    {
        Verify.assertContains("1", this.list);
        Verify.assertContains("2", this.list);
        Verify.assertContains("3", this.list);
        Verify.assertContains("4", this.list);
        Verify.assertNotContains("5", this.list);
    }

    @Test
    public void testEqualsAndHashCode()
    {
        MutableList<String> one = Lists.fixedSize.of("1", "2", "3", "4");
        List<String> oneA = new ArrayList<>(one);
        Verify.assertEqualsAndHashCode(one, oneA);
        Verify.assertPostSerializedEqualsAndHashCode(one);
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
        List<String> newList = new ArrayList<>(this.list);
        newList.add("5");
        Verify.assertStartsWith(newList, "1", "2", "3", "4", "5");
    }

    @Test
    public void testGet()
    {
        Verify.assertStartsWith(this.list, "1", "2", "3", "4");
        assertThrows(IndexOutOfBoundsException.class, () -> this.list.get(4));
    }

    @Test
    public void testSet()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3", "4");
        assertEquals("1", list.set(0, "4"));
        assertEquals("2", list.set(1, "3"));
        assertEquals("3", list.set(2, "2"));
        assertEquals("4", list.set(3, "1"));
        assertEquals(FastList.newListWith("4", "3", "2", "1"), list);
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(4, "0"));
    }

    private void assertUnchanged()
    {
        Verify.assertSize(4, this.list);
        Verify.assertNotContains("5", this.list);
        assertEquals(FastList.newListWith("1", "2", "3", "4"), this.list);
    }

    @Test
    public void testSerializableEqualsAndHashCode()
    {
        Verify.assertPostSerializedEqualsAndHashCode(this.list);
        MutableList<String> copyOfList = SerializeTestHelper.serializeDeserialize(this.list);
        assertNotSame(this.list, copyOfList);
    }

    @Test
    public void testGetFirstGetLast()
    {
        MutableList<String> list4 = Lists.fixedSize.of("1", "2", "3", "4");
        assertEquals("1", list4.getFirst());
        assertEquals("4", list4.getLast());
    }

    @Test
    public void testForEach()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3", "4");
        source.forEach(CollectionAddProcedure.on(result));
        assertEquals(FastList.newListWith("1", "2", "3", "4"), result);
    }

    @Test
    public void testForEachWithIndex()
    {
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3", "4");
        source.forEachWithIndex((each, index) ->
        {
            result.add(each);
            indexSum[0] += index;
        });
        assertEquals(FastList.newListWith("1", "2", "3", "4"), result);
        assertEquals(6, indexSum[0]);
    }

    @Test
    public void testForEachWith()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3", "4");
        source.forEachWith(Procedures2.fromProcedure(result::add), null);
        assertEquals(FastList.newListWith("1", "2", "3", "4"), result);
    }

    @Test
    public void testForLoop()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three", "four");
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO", "THREE", "FOUR");
        for (String each : list)
        {
            Verify.assertContains(each.toUpperCase(Locale.ROOT), upperList);
        }
    }

    @Test
    public void testSubList()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three", "four");
        MutableList<String> subList = list.subList(0, 3);
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO", "THREE", "FOUR");
        for (String each : subList)
        {
            Verify.assertContains(each.toUpperCase(Locale.ROOT), upperList);
        }
    }

    @Test
    public void without()
    {
        MutableList<Integer> list = new QuadrupletonList<>(1, 2, 3, 2);
        assertSame(list, list.without(9));
        list = list.without(2);
        Verify.assertListsEqual(FastList.newListWith(1, 3, 2), list);
        Verify.assertInstanceOf(TripletonList.class, list);
    }

    @Test
    public void testGetOnly()
    {
        assertThrows(IllegalStateException.class, () -> this.list.getOnly());
    }
}
