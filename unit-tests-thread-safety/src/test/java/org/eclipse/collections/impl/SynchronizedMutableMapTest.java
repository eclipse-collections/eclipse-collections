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

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

public class SynchronizedMutableMapTest
        implements SynchronizedMapIterableTestTrait
{
    private final MutableMap<String, String> classUnderTest = UnifiedMap.newWithKeysValues(
            "One",
            "1",
            "Two",
            "2",
            "Three",
            "3").asSynchronized();

    @Override
    public MutableMap<String, String> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void newEmpty_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::newEmpty);
    }

    @Test
    public void removeKey_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().remove("1"));
    }

    @Test
    public void getIfAbsentPut_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().getIfAbsentPut("Nine", () -> "foo"));
    }

    @Test
    public void getIfAbsentPutWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().getIfAbsentPutWith("Nine", Functions.getPassThru(), "foo"));
    }

    @Test
    public void withKeyValue_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withKeyValue("foo", "bar"));
    }

    @Test
    public void withAllKeyValues_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withAllKeyValues(FastList.newListWith(Tuples.pair("foo", "bar"))));
    }

    @Test
    public void withAllKeyValueArguments_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withAllKeyValueArguments(Tuples.pair("foo", "bar")));
    }

    @Test
    public void withoutKey_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withoutKey("foo"));
    }

    @Test
    public void withoutAllKeys_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withoutAllKeys(FastList.newListWith("foo")));
    }

    @Test
    public void asUnmodifiable_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::asUnmodifiable);
    }

    @Test
    public void toImmutable_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::toImmutable);
    }

    @Test
    public void collectKeysAndValues_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collectKeysAndValues(
                FastList.newListWith(4, 5, 6),
                x -> "",
                x1 -> ""));
    }

    @Test
    public void keySet_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::keySet);
    }

    @Test
    public void entrySet_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::entrySet);
    }
}
