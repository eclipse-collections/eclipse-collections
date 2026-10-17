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

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.junit.jupiter.api.Test;

public class SynchronizedMutableMapTest
        implements SynchronizedMutableMapIterableTestTrait
{
    private final MutableMap<String, String> classUnderTest = UnifiedMap.newWithKeysValues(
            "One",
            "1",
            "Two",
            "2",
            "Three",
            "3").asSynchronized();

    @Override
    public MutableMap<String, String> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void collectKeysAndValues_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collectKeysAndValues(
                FastList.newListWith(4, 5, 6),
                x -> "",
                x1 -> ""));
    }
}
