/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import org.eclipse.collections.api.set.MultiReaderSet;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.jupiter.api.Test;

public class MultiReaderUnifiedSetTest
        implements MultiReaderUnifiedSetTestTrait
{
    private final MultiReaderSet<Integer> classUnderTest = MultiReaderUnifiedSet.newSetWith(1, 2, 3);

    @Override
    public MultiReaderSet<Integer> getClassUnderTest()
    {
        return this.classUnderTest;
    }

    @Test
    public void newSet_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, MultiReaderUnifiedSet::newSet);
    }

    @Test
    public void newSetCapacity_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, () -> MultiReaderUnifiedSet.newSet(5));
    }

    @Test
    public void newSetIterable_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, () -> MultiReaderUnifiedSet.newSet(new FastList<>()));
    }

    @Test
    public void newSetWith_safe()
    {
        this.assertReaderWriterThreadSafety(false, false, () -> MultiReaderUnifiedSet.newSetWith(1, 2));
    }
}
