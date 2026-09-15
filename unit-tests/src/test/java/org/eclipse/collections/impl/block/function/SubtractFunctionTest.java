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

import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Junit test for {@link SubtractFunction}.
 */
public class SubtractFunctionTest
{
    @Test
    public void subtractIntegerFunction()
    {
        assertEquals(Integer.valueOf(1), SubtractFunction.INTEGER.value(2, 1));
        assertEquals(Integer.valueOf(0), SubtractFunction.INTEGER.value(1, 1));
        assertEquals(Integer.valueOf(-1), SubtractFunction.INTEGER.value(1, 2));
    }

    @Test
    public void subtractDoubleFunction()
    {
        assertEquals(Double.valueOf(0.5), SubtractFunction.DOUBLE.value(2.0, 1.5));
        assertEquals(Double.valueOf(0), SubtractFunction.DOUBLE.value(2.0, 2.0));
        assertEquals(Double.valueOf(-0.5), SubtractFunction.DOUBLE.value(1.5, 2.0));
    }

    @Test
    public void subtractLongFunction()
    {
        assertEquals(Long.valueOf(1L), SubtractFunction.LONG.value(2L, 1L));
        assertEquals(Long.valueOf(0L), SubtractFunction.LONG.value(1L, 1L));
        assertEquals(Long.valueOf(-1L), SubtractFunction.LONG.value(1L, 2L));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(SubtractFunction.class);
    }
}
