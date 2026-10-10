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

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

public interface SynchronizedMutableMapIterableTestTrait
        extends SynchronizedMapIterableTestTrait
{
    @Override
    MutableMapIterable<String, String> getClassUnderTest();

    @Test
    default void getIfAbsentPut_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().getIfAbsentPut("Nine", () -> "foo"));
    }

    @Test
    default void getIfAbsentPutWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().getIfAbsentPutWith("Nine", Functions.getPassThru(), "foo"));
    }

    @Test
    default void withKeyValue_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withKeyValue("foo", "bar"));
    }

    @Test
    default void withMap_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withMap(Maps.mutable.with("foo", "bar")));
    }

    @Test
    default void withMapIterable_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withMapIterable(Maps.mutable.with("foo", "bar")));
    }

    @Test
    default void putAllMapIterable_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().putAllMapIterable(Maps.mutable.with("foo", "bar")));
    }

    @Test
    default void withAllKeyValues_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withAllKeyValues(FastList.newListWith(Tuples.pair("foo", "bar"))));
    }

    @Test
    default void withAllKeyValueArguments_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withAllKeyValueArguments(Tuples.pair("foo", "bar")));
    }

    @Test
    default void withoutKey_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withoutKey("foo"));
    }

    @Test
    default void withoutAllKeys_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withoutAllKeys(FastList.newListWith("foo")));
    }

    @Test
    default void newEmpty_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::newEmpty);
    }

    @Test
    default void asUnmodifiable_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::asUnmodifiable);
    }

    @Test
    default void remove_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().remove("1"));
    }

    @Test
    default void keySet_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::keySet);
    }

    @Test
    default void values_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::values);
    }

    @Test
    default void entrySet_synchronized()
    {
        this.assertSynchronized(this.getClassUnderTest()::entrySet);
    }
}
