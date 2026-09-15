/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.impl.list.mutable.FastList;

public class SynchronizedMutableCollectionTest
        implements SynchronizedMutableCollectionTestTrait
{
    private final MutableCollection<String> classUnderTest = SynchronizedMutableCollection.of(FastList.newListWith("1", "2", "3"));

    @Override
    public MutableCollection<String> getClassUnderTest()
    {
        return this.classUnderTest;
    }
}
