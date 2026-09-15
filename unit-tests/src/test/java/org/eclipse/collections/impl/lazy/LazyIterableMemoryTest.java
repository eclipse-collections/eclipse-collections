/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.JolMemoryTestUtil;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.jupiter.api.Test;

public class LazyIterableMemoryTest
{
    private static final MutableList<?> MUTABLE_LIST = Lists.mutable.empty();

    @Test
    public void listSize()
    {
        JolMemoryTestUtil.assertGraphMemoryEquals(40L, 32L, MUTABLE_LIST);
    }

    @Test
    public void asLazy()
    {
        var asLazy = MUTABLE_LIST.asLazy();
        JolMemoryTestUtil.assertGraphMemoryEquals(56L, 48L, asLazy);
    }

    @Test
    public void select()
    {
        var select = MUTABLE_LIST.asLazy().select(each -> true);
        JolMemoryTestUtil.assertGraphMemoryEquals(80L, 56L, select);
    }

    @Test
    public void reject()
    {
        var reject = MUTABLE_LIST.asLazy().reject(each -> true);
        JolMemoryTestUtil.assertGraphMemoryEquals(96L, 72L, reject);
    }

    @Test
    public void collect()
    {
        var collect = MUTABLE_LIST.asLazy().collect(each -> null);
        JolMemoryTestUtil.assertGraphMemoryEquals(80L, 56L, collect);
    }

    @Test
    public void tap()
    {
        var tap = MUTABLE_LIST.asLazy().tap(each -> { });
        JolMemoryTestUtil.assertGraphMemoryEquals(80L, 56L, tap);
    }

    @Test
    public void chunk()
    {
        var chunk = MUTABLE_LIST.asLazy().chunk(2);
        JolMemoryTestUtil.assertGraphMemoryEquals(64L, 48L, chunk);
    }

    @Test
    public void collectBoolean()
    {
        var collectBoolean = MUTABLE_LIST.asLazy().collectBoolean(each -> true);
        JolMemoryTestUtil.assertGraphMemoryEquals(96L, 80L, collectBoolean);
    }

    @Test
    public void collectLong()
    {
        var collectLong = MUTABLE_LIST.asLazy().collectLong(each -> 1L);
        JolMemoryTestUtil.assertGraphMemoryEquals(96L, 80L, collectLong);
    }

    @Test
    public void collectInt()
    {
        var collectInt = MUTABLE_LIST.asLazy().collectInt(each -> 1);
        JolMemoryTestUtil.assertGraphMemoryEquals(96L, 80L, collectInt);
    }

    @Test
    public void collectDouble()
    {
        var collectDouble = MUTABLE_LIST.asLazy().collectDouble(each -> 1.0);
        JolMemoryTestUtil.assertGraphMemoryEquals(96L, 80L, collectDouble);
    }
}
