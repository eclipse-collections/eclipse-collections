/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.immutable.primitive;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.map.primitive.ImmutableObjectBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableObjectBooleanMap;
import org.eclipse.collections.impl.factory.primitive.ObjectBooleanMaps;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectBooleanHashMap;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectBooleanMapFactoryTest
{
    @Test
    public void of()
    {
        assertEquals(new ObjectBooleanHashMap<>(), ObjectBooleanMaps.mutable.of());
        assertEquals(ObjectBooleanMaps.mutable.of(), ObjectBooleanMaps.mutable.empty());
        assertEquals(ObjectBooleanMaps.mutable.empty().toImmutable(), ObjectBooleanMaps.immutable.empty());
        assertEquals(ObjectBooleanMaps.mutable.empty().toImmutable(), ObjectBooleanMaps.immutable.of());
        assertTrue(ObjectBooleanMaps.immutable.empty() instanceof ImmutableObjectBooleanEmptyMap);
        assertEquals(ObjectBooleanHashMap.newWithKeysValues("2", true).toImmutable(), ObjectBooleanMaps.immutable.of("2", true));
        assertTrue(ObjectBooleanMaps.immutable.of("2", true) instanceof ImmutableObjectBooleanSingletonMap);

        assertEquals(ObjectBooleanMaps.mutable.of("2", true), ObjectBooleanHashMap.newWithKeysValues("2", true));
        assertEquals(ObjectBooleanMaps.mutable.of("2", true, 3, false),
                ObjectBooleanHashMap.newWithKeysValues("2", true, 3, false));
        assertEquals(ObjectBooleanMaps.mutable.of("2", true, 3, false, 4, false),
                ObjectBooleanHashMap.newWithKeysValues("2", true, 3, false, 4, false));
        assertEquals(ObjectBooleanMaps.mutable.of("2", true, 3, false, 4, false, 5, true),
                ObjectBooleanHashMap.newWithKeysValues("2", true, 3, false, 4, false, 5, true));
    }

    @Test
    public void with()
    {
        assertEquals(ObjectBooleanMaps.mutable.with(), ObjectBooleanMaps.mutable.empty());
        assertEquals(ObjectBooleanMaps.mutable.with("2", false), ObjectBooleanHashMap.newWithKeysValues("2", false));
        assertEquals(ObjectBooleanMaps.mutable.with("2", true), ObjectBooleanHashMap.newWithKeysValues("2", true));
        assertEquals(ObjectBooleanMaps.mutable.with("2", true, 3, false),
                ObjectBooleanHashMap.newWithKeysValues("2", true, 3, false));
        assertEquals(ObjectBooleanMaps.mutable.with("2", true, 3, false, 4, false),
                ObjectBooleanHashMap.newWithKeysValues("2", true, 3, false, 4, false));
        assertEquals(ObjectBooleanMaps.mutable.with("2", true, 3, false, 4, false, 5, true),
                ObjectBooleanHashMap.newWithKeysValues("2", true, 3, false, 4, false, 5, true));
    }

    @Test
    public void ofAll()
    {
        assertEquals(ObjectBooleanMaps.mutable.empty(), ObjectBooleanMaps.mutable.ofAll(ObjectBooleanMaps.mutable.empty()));
        assertEquals(ObjectBooleanMaps.mutable.empty().toImmutable(), ObjectBooleanMaps.immutable.ofAll(ObjectBooleanMaps.mutable.empty()));
        assertSame(ObjectBooleanMaps.immutable.empty(), ObjectBooleanMaps.immutable.ofAll(ObjectBooleanMaps.immutable.empty()));
        assertEquals(ObjectBooleanHashMap.newWithKeysValues("2", true), ObjectBooleanMaps.mutable.ofAll(ObjectBooleanHashMap.newWithKeysValues("2", true)));
        assertEquals(ObjectBooleanHashMap.newWithKeysValues("2", true).toImmutable(), ObjectBooleanMaps.immutable.ofAll(ObjectBooleanHashMap.newWithKeysValues("2", true)));
        assertEquals(ObjectBooleanHashMap.newWithKeysValues("2", true, "3", false).toImmutable(), ObjectBooleanMaps.immutable.ofAll(ObjectBooleanHashMap.newWithKeysValues("2", true, "3", false)));
    }

    @Test
    public void from()
    {
        Iterable<String> iterable = Lists.mutable.with("1", "2", "3");
        assertEquals(
                ObjectBooleanHashMap.newWithKeysValues("1", false, "2", true, "3", false),
                ObjectBooleanMaps.mutable.from(iterable, each -> each, each -> Integer.valueOf(each) % 2 == 0));
        assertTrue(ObjectBooleanMaps.mutable.from(iterable, each -> each, each -> Integer.valueOf(each) % 2 == 0) instanceof MutableObjectBooleanMap);
        assertEquals(
                ObjectBooleanHashMap.newWithKeysValues("1", false, "2", true, "3", false),
                ObjectBooleanMaps.immutable.from(iterable, each -> each, each -> Integer.valueOf(each) % 2 == 0));
        assertTrue(ObjectBooleanMaps.immutable.from(iterable, each -> each, each -> Integer.valueOf(each) % 2 == 0) instanceof ImmutableObjectBooleanMap);
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(ObjectBooleanMaps.class);
    }
}
