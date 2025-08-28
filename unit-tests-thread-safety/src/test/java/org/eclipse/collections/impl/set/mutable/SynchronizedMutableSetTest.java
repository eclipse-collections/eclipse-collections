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

import java.util.TreeSet;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.collection.mutable.SynchronizedMutableCollectionTestTrait;
import org.eclipse.collections.impl.list.mutable.FastList;

public class SynchronizedMutableSetTest
        implements SynchronizedMutableCollectionTestTrait, SynchronizedUnsortedSetIterableTestTrait
{
    private final MutableSet<String> classUnderTest = SetAdapter.adapt(new TreeSet<>(FastList.newListWith("1", "2", "3"))).asSynchronized();

    @Override
    public MutableSet<String> getClassUnderTest()
    {
        return this.classUnderTest;
    }
}
