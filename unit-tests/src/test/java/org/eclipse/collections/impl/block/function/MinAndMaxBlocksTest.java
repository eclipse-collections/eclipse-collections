/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class MinAndMaxBlocksTest
{
    private static final Double FORTY_TWO_DOUBLE = 42.0;
    private static final Integer FORTY_TWO_INTEGER = 42;
    private static final Long FORTY_TWO_LONG = 42L;

    @Test
    public void minBlocks()
    {
        assertEquals(Double.valueOf(1.0), MinFunction.DOUBLE.value(1.0, 2.0));
        assertEquals(Double.valueOf(0.0), MinFunction.DOUBLE.value(0.0, 1.0));
        assertEquals(Double.valueOf(-1.0), MinFunction.DOUBLE.value(1.0, -1.0));

        assertEquals(Integer.valueOf(1), MinFunction.INTEGER.value(1, 2));
        assertEquals(Integer.valueOf(0), MinFunction.INTEGER.value(0, 1));
        assertEquals(Integer.valueOf(-1), MinFunction.INTEGER.value(1, -1));

        assertEquals(Long.valueOf(1L), MinFunction.LONG.value(1L, 2L));
        assertEquals(Long.valueOf(0L), MinFunction.LONG.value(0L, 1L));
        assertEquals(Long.valueOf(-1L), MinFunction.LONG.value(1L, -1L));
    }

    @Test
    public void minBlocksNull()
    {
        assertSame(FORTY_TWO_DOUBLE, MinFunction.DOUBLE.value(null, FORTY_TWO_DOUBLE));
        assertSame(FORTY_TWO_DOUBLE, MinFunction.DOUBLE.value(FORTY_TWO_DOUBLE, null));
        assertSame(null, MinFunction.DOUBLE.value(null, null));

        assertSame(FORTY_TWO_INTEGER, MinFunction.INTEGER.value(null, FORTY_TWO_INTEGER));
        assertSame(FORTY_TWO_INTEGER, MinFunction.INTEGER.value(FORTY_TWO_INTEGER, null));
        assertSame(null, MinFunction.INTEGER.value(null, null));

        assertSame(FORTY_TWO_LONG, MinFunction.LONG.value(null, FORTY_TWO_LONG));
        assertSame(FORTY_TWO_LONG, MinFunction.LONG.value(FORTY_TWO_LONG, null));
        assertSame(null, MinFunction.LONG.value(null, null));
    }

    @Test
    public void maxBlocks()
    {
        assertEquals(Double.valueOf(2.0), MaxFunction.DOUBLE.value(1.0, 2.0));
        assertEquals(Double.valueOf(1.0), MaxFunction.DOUBLE.value(0.0, 1.0));
        assertEquals(Double.valueOf(1.0), MaxFunction.DOUBLE.value(1.0, -1.0));

        assertEquals(Integer.valueOf(2), MaxFunction.INTEGER.value(1, 2));
        assertEquals(Integer.valueOf(1), MaxFunction.INTEGER.value(0, 1));
        assertEquals(Integer.valueOf(1), MaxFunction.INTEGER.value(1, -1));

        assertEquals(Long.valueOf(2L), MaxFunction.LONG.value(1L, 2L));
        assertEquals(Long.valueOf(1L), MaxFunction.LONG.value(0L, 1L));
        assertEquals(Long.valueOf(1L), MaxFunction.LONG.value(1L, -1L));
    }

    @Test
    public void maxBlocksNull()
    {
        assertSame(FORTY_TWO_DOUBLE, MaxFunction.DOUBLE.value(null, FORTY_TWO_DOUBLE));
        assertSame(FORTY_TWO_DOUBLE, MaxFunction.DOUBLE.value(FORTY_TWO_DOUBLE, null));
        assertSame(null, MaxFunction.DOUBLE.value(null, null));

        assertSame(FORTY_TWO_INTEGER, MaxFunction.INTEGER.value(null, FORTY_TWO_INTEGER));
        assertSame(FORTY_TWO_INTEGER, MaxFunction.INTEGER.value(FORTY_TWO_INTEGER, null));
        assertSame(null, MaxFunction.INTEGER.value(null, null));

        assertSame(FORTY_TWO_LONG, MaxFunction.LONG.value(null, FORTY_TWO_LONG));
        assertSame(FORTY_TWO_LONG, MaxFunction.LONG.value(FORTY_TWO_LONG, null));
        assertSame(null, MaxFunction.LONG.value(null, null));
    }
}
