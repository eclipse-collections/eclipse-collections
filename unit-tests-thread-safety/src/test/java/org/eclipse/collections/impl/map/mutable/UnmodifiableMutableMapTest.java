/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.impl.UnmodifiableIterableTestTrait;

public class UnmodifiableMutableMapTest
        implements UnmodifiableIterableTestTrait
{
    @Override
    public RichIterable<String> getClassUnderTest()
    {
        return new UnmodifiableMutableMap<>(UnifiedMap.newWithKeysValues(
                1,
                "1",
                2,
                "2",
                3,
                "3")).valuesView();
    }
}
