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

import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class OrderedHashMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAD9vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5vcmRlcmVkLm11dGFibGUu\n"
                        + "T3JkZXJlZEhhc2hNYXAAAAAAAAAAAQwAAHhwdwQAAAAAeA==",
                OrderedHashMap.newMap());
    }

    @Test
    public void keyPreservation()
    {
        MutableOrderedMap<String, Integer> map = new OrderedHashMap<>();
        map.put("Charlie", 3);
        map.put("Alpha", 1);
        map.put("Bravo", 2);

        Verify.assertPostSerializedEqualsAndHashCode(map);
    }
}
