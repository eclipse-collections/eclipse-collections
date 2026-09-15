/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.function;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Junit test for {@link MinSizeFunction}.
 */
public class MinSizeFunctionTest
{
    @Test
    public void minSizeCollection()
    {
        assertEquals(Integer.valueOf(2), MinSizeFunction.COLLECTION.value(2, FastList.newListWith(1, 2, 3)));
        assertEquals(Integer.valueOf(2), MinSizeFunction.COLLECTION.value(3, FastList.newListWith(1, 2)));
    }

    @Test
    public void minSizeMap()
    {
        assertEquals(Integer.valueOf(2), MinSizeFunction.MAP.value(2, Maps.mutable.of(1, 1, 2, 2, 3, 3)));
        assertEquals(Integer.valueOf(2), MinSizeFunction.MAP.value(3, Maps.mutable.of(1, 1, 2, 2)));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(MinSizeFunction.class);
    }
}
