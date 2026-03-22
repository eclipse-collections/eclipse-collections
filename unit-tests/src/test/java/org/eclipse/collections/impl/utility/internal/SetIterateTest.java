/*
 * Copyright (c) 2022 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.internal;

import java.util.Set;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SetIterateTest
{
    @Test
    public void removeAllIterableOne()
    {
        Set<Integer> set = this.newSet();
        assertTrue(SetIterate.removeAllIterable(set, Sets.mutable.of(1, 2, 3, 4, 5, 6, 7, 8, 9)));
        assertEquals(Sets.mutable.of(25), set);
    }

    @Test
    public void removeAllIterableTwo()
    {
        Set<Integer> set = this.newSet();
        assertFalse(SetIterate.removeAllIterable(set, Sets.mutable.of(31, 32, 33, 34, 35, 36, 37)));
        assertEquals(this.newSet(), set);
    }

    private MutableSet<Integer> newSet()
    {
        return Sets.mutable.of(5, 9, 25);
    }

    @Test
    public void removeAllIterableThree()
    {
        Set<Integer> set = this.newSet();
        assertTrue(SetIterate.removeAllIterable(set, Sets.mutable.of(25)));
        assertEquals(Sets.mutable.of(5, 9), set);
    }

    @Test
    public void removeAllIterableFour()
    {
        Set<Integer> set = this.newSet();
        assertFalse(SetIterate.removeAllIterable(set, Sets.mutable.of(250)));
        assertEquals(this.newSet(), set);
    }

    @Test
    public void removeAllIterableFive()
    {
        Set<Integer> set = this.newSet();
        assertTrue(SetIterate.removeAllIterable(set, Lists.mutable.of(5, 5, 5, 9, 9, 100, 200, 300)));
        assertEquals(Sets.mutable.of(25), set);
    }

    @Test
    public void removeAllIterableSix()
    {
        Set<Integer> set = this.newSet();
        assertFalse(SetIterate.removeAllIterable(set, Lists.mutable.of(90)));
        assertEquals(this.newSet(), set);
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(SetIterate.class);
    }
}
