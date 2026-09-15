/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.internal.primitive;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.impl.block.factory.primitive.BooleanPredicates;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanIterableIterateTest
{
    private final BooleanIterable iterable = BooleanArrayList.newListWith(true, false, true);

    @Test
    public void forEach()
    {
        String[] sum = new String[2];
        sum[0] = "";
        sum[1] = "";
        BooleanIterableIterate.forEach(this.iterable, each -> sum[0] += each + " ");
        assertEquals("true false true ", sum[0]);
    }

    @Test
    public void select_target()
    {
        Verify.assertSize(2, BooleanIterableIterate.select(this.iterable, BooleanPredicates.equal(true), new BooleanArrayList(2)));
        Verify.assertSize(1, BooleanIterableIterate.select(this.iterable, BooleanPredicates.equal(false), new BooleanArrayList(3)));
    }

    @Test
    public void reject_target()
    {
        Verify.assertSize(1, BooleanIterableIterate.reject(this.iterable, BooleanPredicates.equal(true), new BooleanArrayList(1)));
        Verify.assertSize(2, BooleanIterableIterate.reject(this.iterable, BooleanPredicates.equal(false), new BooleanArrayList(1)));
    }

    @Test
    public void collect_target()
    {
        Verify.assertIterableSize(3, BooleanIterableIterate.collect(this.iterable, booleanParameter -> !booleanParameter, FastList.newList()));
    }

    @Test
    public void isEmpty()
    {
        assertFalse(BooleanIterableIterate.isEmpty(this.iterable));
    }

    @Test
    public void notEmpty()
    {
        assertTrue(BooleanIterableIterate.notEmpty(this.iterable));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(BooleanIterableIterate.class);
    }
}
