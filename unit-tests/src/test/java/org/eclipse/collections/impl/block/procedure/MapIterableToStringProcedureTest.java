/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapIterableToStringProcedureTest
{
    @Test
    public void serialization()
    {
        MutableMap<String, String> map = Maps.mutable.with("one", "1");
        MapIterableToStringProcedure<String, String> procedure = new MapIterableToStringProcedure<>(map);
        procedure.value("one", "1");

        MapIterableToStringProcedure<String, String> deserialized = SerializeTestHelper.serializeDeserialize(procedure);
        deserialized.value("two", "2");

        assertEquals("{one=1, two=2}", deserialized.getString());
    }
}
