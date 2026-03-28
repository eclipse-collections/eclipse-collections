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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance());
    }

    @Test
    public void forEachWithIndex()
    {
        EmptyLazyIterable.getInstance().forEachWithIndex((o, i) -> { throw new RuntimeException(); });
    }

    @Test
    public void into()
    {
        Assertions.assertEquals(Lists.immutable.with(1), EmptyLazyIterable.getInstance().into(Lists.mutable.with(1)));
    }

    @Test
    public void size()
    {
        Assertions.assertEquals(0, EmptyLazyIterable.getInstance().size());
    }

    @Test
    public void isEmpty()
    {
        Assertions.assertTrue(EmptyLazyIterable.getInstance().isEmpty());
    }

    @Test
    public void notEmpty()
    {
        Assertions.assertFalse(EmptyLazyIterable.getInstance().notEmpty());
    }

    @Test
    public void select()
    {
        Assertions.assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().select(e -> true));
    }

    @Test
    public void reject()
    {
        Assertions.assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().reject(e -> true));
    }

    @Test
    public void drop()
    {
        Assertions.assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().drop(1));
    }

    @Test
    public void take()
    {
        Assertions.assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().take(1));
    }

    @Test
    public void takeWhile()
    {
        Assertions.assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().takeWhile(e -> true));
    }

    @Test
    public void dropWhile()
    {
        Assertions.assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().dropWhile(e -> true));
    }

    @Test
    public void distinct()
    {
        Assertions.assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().distinct());
    }

    @Test
    public void detect()
    {
        Assertions.assertNull(EmptyLazyIterable.getInstance().detect(e -> true));
    }

    @Test
    public void detectWith()
    {
        Assertions.assertNull(EmptyLazyIterable.getInstance().detectWith((e, f) -> true, null));
    }

    @Test
    public void detectOptional()
    {
        Assertions.assertTrue(EmptyLazyIterable.getInstance().detectOptional(e -> true).isEmpty());
    }

    @Test
    public void detectWithOptional()
    {
        Assertions.assertTrue(EmptyLazyIterable.getInstance().detectWithOptional((e, f) -> true, null).isEmpty());
    }

    @Test
    public void anySatisfy()
    {
        Assertions.assertFalse(EmptyLazyIterable.getInstance().anySatisfy(e -> true));
    }

    @Test
    public void anySatisyWith()
    {
        Assertions.assertFalse(EmptyLazyIterable.getInstance().anySatisfyWith((e, f) -> true, null));
    }

    @Test
    public void allSatisfy()
    {
        Assertions.assertTrue(EmptyLazyIterable.getInstance().allSatisfy(e -> true));
    }

    @Test
    public void allSatisyWith()
    {
        Assertions.assertTrue(EmptyLazyIterable.getInstance().allSatisfyWith((e, f) -> true, null));
    }

    @Test
    public void noneSatisfy()
    {
        Assertions.assertTrue(EmptyLazyIterable.getInstance().noneSatisfy(e -> true));
    }

    @Test
    public void noneSatisyWith()
    {
        Assertions.assertTrue(EmptyLazyIterable.getInstance().noneSatisfyWith((e, f) -> true, null));
    }

    @Test
    public void collect()
    {
        Assertions.assertSame(EmptyLazyIterable.getInstance(), EmptyLazyIterable.getInstance().collect(e -> null));
    }

    @Test
    public void collectIf()
    {
        Assertions.assertSame(
                EmptyLazyIterable.getInstance(),
                EmptyLazyIterable.getInstance().collectIf(e -> true, e -> null));
    }
}
