/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import org.eclipse.collections.api.list.MultiReaderList;
import org.junit.jupiter.api.Test;

public class MultiReaderFastListTest
        implements MultiReaderFastListTestTrait
{
    private final MultiReaderList<Integer> classUnderTest = MultiReaderFastList.newListWith(1, 2, 3);

    @Override
    public MultiReaderList<Integer> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void newList_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, MultiReaderFastList::newList);
    }

    @Test
    public void newListCapacity_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, () -> MultiReaderFastList.newList(5));
    }

    @Test
    public void newListIterable_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, () -> MultiReaderFastList.newList(new FastList<>()));
    }

    @Test
    public void newListWith_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, () -> MultiReaderFastList.newListWith(1, 2));
    }
}
