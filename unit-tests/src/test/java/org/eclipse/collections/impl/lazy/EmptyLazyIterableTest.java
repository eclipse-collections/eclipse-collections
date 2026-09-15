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

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.impl.JolMemoryTestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmptyLazyIterableTest
{
    @Test
    public void memoryRequired()
    {
        JolMemoryTestUtil.assertClassMemoryEquals(16, 8, EmptyLazyIterable.getInstance());
        JolMemoryTestUtil.assertGraphMemoryEquals(16, 8, EmptyLazyIterable.getInstance());
    }

    @Test
    public void instance()
    {
        assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance());
    }

    @Test
    public void forEachWithIndex()
    {
        EmptyLazyIterable.getInstance().forEachWithIndex((o, i) -> { throw new RuntimeException(); });
    }

    @Test
    public void into()
    {
        assertEquals(Lists.immutable.with(1), EmptyLazyIterable.getInstance().into(Lists.mutable.with(1)));
    }

    @Test
    public void size()
    {
        assertEquals(0, EmptyLazyIterable.getInstance().size());
    }

    @Test
    public void isEmpty()
    {
        assertTrue(EmptyLazyIterable.getInstance().isEmpty());
    }

    @Test
    public void notEmpty()
    {
        assertFalse(EmptyLazyIterable.getInstance().notEmpty());
    }

    @Test
    public void select()
    {
        assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().select(e -> true));
    }

    @Test
    public void reject()
    {
        assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().reject(e -> true));
    }

    @Test
    public void drop()
    {
        assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().drop(1));
    }

    @Test
    public void take()
    {
        assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().take(1));
    }

    @Test
    public void takeWhile()
    {
        assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().takeWhile(e -> true));
    }

    @Test
    public void dropWhile()
    {
        assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().dropWhile(e -> true));
    }

    @Test
    public void distinct()
    {
        assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().distinct());
    }

    @Test
    public void detect()
    {
        assertNull(EmptyLazyIterable.getInstance().detect(e -> true));
    }

    @Test
    public void detectWith()
    {
        assertNull(EmptyLazyIterable.getInstance().detectWith((e, f) -> true, null));
    }

    @Test
    public void detectOptional()
    {
        assertTrue(EmptyLazyIterable.getInstance().detectOptional(e -> true).isEmpty());
    }

    @Test
    public void detectWithOptional()
    {
        assertTrue(EmptyLazyIterable.getInstance().detectWithOptional((e, f) -> true, null).isEmpty());
    }

    @Test
    public void anySatisfy()
    {
        assertFalse(EmptyLazyIterable.getInstance().anySatisfy(e -> true));
    }

    @Test
    public void anySatisyWith()
    {
        assertFalse(EmptyLazyIterable.getInstance().anySatisfyWith((e, f) -> true, null));
    }

    @Test
    public void allSatisfy()
    {
        assertTrue(EmptyLazyIterable.getInstance().allSatisfy(e -> true));
    }

    @Test
    public void allSatisyWith()
    {
        assertTrue(EmptyLazyIterable.getInstance().allSatisfyWith((e, f) -> true, null));
    }

    @Test
    public void noneSatisfy()
    {
        assertTrue(EmptyLazyIterable.getInstance().noneSatisfy(e -> true));
    }

    @Test
    public void noneSatisyWith()
    {
        assertTrue(EmptyLazyIterable.getInstance().noneSatisfyWith((e, f) -> true, null));
    }

    @Test
    public void collect()
    {
        assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().collect(e -> null));
    }

    @Test
    public void collectIf()
    {
        assertSame(
                EmptyLazyIterable.getInstance(),
                EmptyLazyIterable.getInstance().collectIf(e -> true, e -> null));
    }
}
