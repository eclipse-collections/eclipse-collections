/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.internal;

import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InternalArrayIterateTest
{
    @Test
    public void collectBoolean()
    {
        Integer[] items = {1, 2, 3};

        MutableBooleanList result = InternalArrayIterate.collectBoolean(
                items, 3, anObject -> anObject % 2 == 0, BooleanLists.mutable.withInitialCapacity(3));

        assertEquals(result, BooleanLists.mutable.of(false, true, false));
    }

    @Test
    public void collectByte()
    {
        Integer[] items = {1, 2, 3};

        MutableByteList result = InternalArrayIterate.collectByte(
                items, 3, Integer::byteValue, ByteLists.mutable.withInitialCapacity(3));

        assertEquals(result, ByteLists.mutable.of((byte) 1, (byte) 2, (byte) 3));
    }

    @Test
    public void collectChar()
    {
        Character[] items = {'a', 'b', 'c'};

        MutableCharList result = InternalArrayIterate.collectChar(
                items, 3, Character::toUpperCase, CharLists.mutable.withInitialCapacity(3));

        assertEquals(result, CharLists.mutable.of('A', 'B', 'C'));
    }

    @Test
    public void collectDouble()
    {
        Integer[] items = {1, 2, 3};

        MutableDoubleList result = InternalArrayIterate.collectDouble(
                items, 3, Integer::doubleValue, DoubleLists.mutable.withInitialCapacity(3));

        assertEquals(result, DoubleLists.mutable.of(1.0, 2.0, 3.0));
    }

    @Test
    public void collectFloat()
    {
        Integer[] items = {1, 2, 3};

        MutableFloatList result = InternalArrayIterate.collectFloat(
                items, 3, Integer::floatValue, FloatLists.mutable.withInitialCapacity(3));

        assertEquals(result, FloatLists.mutable.of(1.0F, 2.0F, 3.0F));
    }

    @Test
    public void collectInt()
    {
        Integer[] items = {1, 2, 3};

        MutableIntList result = InternalArrayIterate.collectInt(
                items, 3, anObject -> anObject + 1, IntLists.mutable.withInitialCapacity(3));

        assertEquals(result, IntLists.mutable.of(2, 3, 4));
    }

    @Test
    public void collectLong()
    {
        Integer[] items = {1, 2, 3};

        MutableLongList result = InternalArrayIterate.collectLong(
                items, 3, Integer::longValue, LongLists.mutable.withInitialCapacity(3));

        assertEquals(result, LongLists.mutable.of(1L, 2L, 3L));
    }

    @Test
    public void collectShort()
    {
        Integer[] items = {1, 2, 3};

        MutableShortList result = InternalArrayIterate.collectShort(
                items, 3, Integer::shortValue, ShortLists.mutable.withInitialCapacity(3));

        assertEquals(result, ShortLists.mutable.of((short) 1, (short) 2, (short) 3));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(InternalArrayIterate.class);
    }
}
