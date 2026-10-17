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
    public void collectWithIndex_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collectWithIndex((value, index) -> value + index));
    }

    @Test
    public void take_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().take(2));
        this.assertSynchronized(() -> this.getClassUnderTest().takeWhile(value -> true));
    }

    @Test
    public void drop_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().drop(2));
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
        this.assertSynchronized(() -> this.getClassUnderTest().reverseForEachWithIndex((value, index) -> { }));
    }

    @Test
    public void forEachFromTo_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().forEach(0, 1, value -> { }));
        this.assertSynchronized(() -> this.getClassUnderTest().forEachWithIndex(0, 1, (value, index) -> { }));
    }

    @Test
    public void getFirstOptional_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::getFirstOptional);
        this.assertSynchronized(this.getClassUnderTest()::getLastOptional);
    }

    @Test
    public void toStack_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::toStack);
    }
}
