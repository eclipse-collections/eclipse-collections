/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedHashMap;
import org.eclipse.collections.impl.map.ordered.mutable.SynchronizedMutableOrderedMap;
import org.junit.jupiter.api.Test;

public class SynchronizedMutableOrderedMapTest
        implements SynchronizedMutableMapIterableTestTrait
{
    private final MutableOrderedMap<String, String> classUnderTest = OrderedHashMap.newWithKeysValues("A", "1", "B", "2", "C", "3").asSynchronized();

    @Override
    public MutableOrderedMap<String, String> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void of_with_lock_synchronized()
    {
        MutableOrderedMap<String, String> map = SynchronizedMutableOrderedMap.of(OrderedHashMap.newMap(), this.getClassUnderTest());
        this.assertSynchronized(map::size);
    }

    @Test
    public void collectWithIndex_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collectWithIndex((value, index) -> value + index));
    }

    @Test
    public void collectWithIndex_with_target_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collectWithIndex((value, index) -> value + index, FastList.newList()));
    }

    @Test
    public void selectWithIndex_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().selectWithIndex((value, index) -> true, FastList.newList()));
    }

    @Test
    public void rejectWithIndex_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().rejectWithIndex((value, index) -> true, FastList.newList()));
    }

    @Test
    public void take_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().take(2));
    }

    @Test
    public void takeWhile_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().takeWhile(value -> true));
    }

    @Test
    public void drop_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().drop(2));
    }

    @Test
    public void dropWhile_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().dropWhile(value -> true));
    }

    @Test
    public void partitionWhile_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().partitionWhile(value -> true));
    }

    @Test
    public void distinct_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::distinct);
    }

    @Test
    public void toReversed_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::toReversed);
    }

    @Test
    public void detectIndex_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().detectIndex(value -> false));
    }

    @Test
    public void detectLastIndex_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().detectLastIndex(value -> false));
    }

    @Test
    public void indexOf_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().indexOf("1"));
    }

    @Test
    public void corresponds_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().corresponds(FastList.newListWith("1", "2", "3"), String::equals));
    }

    @Test
    public void reverseForEach_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().reverseForEach(value -> { }));
    }

    @Test
    public void reverseForEachWithIndex_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().reverseForEachWithIndex((value, index) -> { }));
    }

    @Test
    public void forEach_with_from_to_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().forEach(0, 1, value -> { }));
    }

    @Test
    public void forEachWithIndex_with_from_to_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().forEachWithIndex(0, 1, (value, index) -> { }));
    }

    @Test
    public void getFirstOptional_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::getFirstOptional);
    }

    @Test
    public void getLastOptional_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::getLastOptional);
    }

    @Test
    public void toStack_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::toStack);
    }
}
