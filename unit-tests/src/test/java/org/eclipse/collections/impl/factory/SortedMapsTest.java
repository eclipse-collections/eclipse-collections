/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.factory;

import org.eclipse.collections.api.factory.map.sorted.MutableSortedMapFactory;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortedMapsTest
{
    @Test
    public void mutables()
    {
        MutableSortedMapFactory factory = SortedMaps.mutable;
        assertEquals(SortedMaps.mutable.empty(), factory.empty());
        assertEquals(SortedMaps.mutable.empty(Integer::compare), factory.empty(Integer::compare));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(SortedMaps.class);
    }
}
