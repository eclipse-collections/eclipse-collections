/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable;

import org.eclipse.collections.api.bag.MultiReaderBag;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.jupiter.api.Test;

public class MultiReaderHashBagTest
        implements MultiReaderHashBagTestTrait
{
    private final MultiReaderBag<Integer> classUnderTest = MultiReaderHashBag.newBagWith(1, 1, 2);

    @Override
    public MultiReaderBag<Integer> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void newBag_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, MultiReaderHashBag::newBag);
    }

    @Test
    public void newBagCapacity_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, () -> MultiReaderHashBag.newBag(5));
    }

    @Test
    public void newBagIterable_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, () -> MultiReaderHashBag.newBag(FastList.newListWith(1, 2)));
    }

    @Test
    public void newBagWith_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, () -> MultiReaderHashBag.newBagWith(1, 2));
    }
}
