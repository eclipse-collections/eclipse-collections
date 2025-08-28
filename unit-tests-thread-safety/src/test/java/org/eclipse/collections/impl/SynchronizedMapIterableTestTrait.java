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

import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

public interface SynchronizedMapIterableTestTrait
        extends SynchronizedRichIterableTestTrait
{
    @Override
    MapIterable<String, String> getClassUnderTest();

    @Test
    default void get_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().get("1"));
    }

    @Test
    default void containsKey_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().containsKey("One"));
    }

    @Test
    default void containsValue_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().containsValue("2"));
    }

    @Test
    default void forEachKey_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().forEachKey(x -> { }));
    }

    @Test
    default void forEachValue_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().forEachValue(x -> { }));
    }

    @Test
    default void forEachKeyValue_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().forEachKeyValue((x, y) -> { }));
    }

    @Test
    default void getIfAbsent_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().getIfAbsent("Nine", () -> "foo"));
    }

    @Test
    default void getIfAbsentWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().getIfAbsentWith("Nine", x -> "", "foo"));
    }

    @Test
    default void ifPresentApply_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().ifPresentApply("1", x -> "foo"));
    }

    @Test
    default void keysView_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().keysView());
    }

    @Test
    default void valuesView_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().valuesView());
    }

    @Test
    default void mapSelect_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().select((x, y) -> false));
    }

    @Test
    default void mapCollectValues_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collectValues((x, y) -> "foo"));
    }

    @Test
    default void mapCollect_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collect((x, y) -> Tuples.pair("foo", "bar")));
    }

    @Test
    default void mapReject_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().reject((x, y) -> false));
    }

    @Test
    default void mapDetect_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().detect((x, y) -> false));
    }
}
