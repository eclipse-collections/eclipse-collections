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

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public interface ListIterableTestTrait
        extends InternalIterableTestTrait
{
    @Override
    ListIterable<String> getClassUnderTest();

    @Override
    @Test
    default void forEach()
    {
        InternalIterableTestTrait.super.forEach();

        FastList<String> result = FastList.newList();
        this.getClassUnderTest().forEach(CollectionAddProcedure.on(result));
        assertEquals(FastList.newListWith("1", "2", "3"), result);
    }

    @Override
    @Test
    default void forEachWithIndex()
    {
        InternalIterableTestTrait.super.forEachWithIndex();

        int[] count = {0};
        this.getClassUnderTest().forEachWithIndex(
                (each, index) ->
                {
                    assertEquals(index, count[0]);
                    count[0]++;
                    assertEquals(String.valueOf(count[0]), each);
                });

        assertEquals(3, count[0]);
    }

    @Override
    @Test
    default void forEachWith()
    {
        InternalIterableTestTrait.super.forEachWith();

        Object unique = new Object();
        int[] count = {0};

        this.getClassUnderTest().forEachWith(
                (each, parameter) ->
                {
                    count[0]++;
                    assertEquals(String.valueOf(count[0]), each);
                    assertSame(unique, parameter);
                },
                unique);

        assertEquals(3, count[0]);
    }
}
