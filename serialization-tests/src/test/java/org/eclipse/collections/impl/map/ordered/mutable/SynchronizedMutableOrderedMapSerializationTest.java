/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.ordered.mutable;

import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class SynchronizedMutableOrderedMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAEpvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5tdXRhYmxlLlN5bmNocm9u\n"
                        + "aXplZE1hcFNlcmlhbGl6YXRpb25Qcm94eQAAAAAAAAABDAAAeHBzcgA/b3JnLmVjbGlwc2UuY29s\n"
                        + "bGVjdGlvbnMuaW1wbC5tYXAub3JkZXJlZC5tdXRhYmxlLk9yZGVyZWRIYXNoTWFwAAAAAAAAAAEM\n"
                        + "AAB4cHcEAAAAAHh4",
                SynchronizedMutableOrderedMap.of(OrderedHashMap.newMap()));
    }
}
