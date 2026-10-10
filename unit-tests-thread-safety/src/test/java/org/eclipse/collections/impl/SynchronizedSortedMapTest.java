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

import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

public class SynchronizedSortedMapTest
        implements SynchronizedMutableMapIterableTestTrait
{
    private final MutableSortedMap<String, String> classUnderTest = TreeSortedMap.newMapWith("A", "1", "B", "2", "C", "3").asSynchronized();

    @Override
    public MutableSortedMap<String, String> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void collectKeysAndValues_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().collectKeysAndValues(
                        FastList.newListWith(4, 5, 6),
                        x -> "",
                        x1 -> ""));
    }

    @Test
    public void comparator_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::comparator);
    }

    @Test
    public void headMap_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().headMap("B"));
    }

    @Test
    public void tailMap_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().tailMap("B"));
    }

    @Test
    public void subMap_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().subMap("A", "C"));
    }

    @Test
    public void firstKey_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::firstKey);
    }

    @Test
    public void lastKey_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::lastKey);
    }

    @Test
    public void with_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().with(Tuples.pair("D", "4")));
    }
}
