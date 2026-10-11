/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.set;

import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.impl.multimap.SynchronizedMultimapTestTrait;
import org.eclipse.collections.impl.tuple.Tuples;

public class SynchronizedSetMultimapTest
        implements SynchronizedMultimapTestTrait
{
    private final MutableSetMultimap<String, String> classUnderTest = SynchronizedSetMultimap.of(UnifiedSetMultimap.newMultimap(
            Tuples.pair("One", "1"),
            Tuples.pair("Two", "2"),
            Tuples.pair("Three", "3")));

    @Override
    public MutableSetMultimap<String, String> getClassUnderTest()
    {
        return this.classUnderTest;
    }
}
