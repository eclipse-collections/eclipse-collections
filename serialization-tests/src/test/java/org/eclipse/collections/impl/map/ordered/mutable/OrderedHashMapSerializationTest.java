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
    public static final String ORDERED_HASH_MAP_KEY_SET =
            "rO0ABXNyAEZvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5vcmRlcmVkLm11dGFibGUu\n"
                    + "T3JkZXJlZEhhc2hNYXAkS2V5U2V0AAAAAAAAAAECAAFMAAZ0aGlzJDB0AEFMb3JnL2VjbGlwc2Uv\n"
                    + "Y29sbGVjdGlvbnMvaW1wbC9tYXAvb3JkZXJlZC9tdXRhYmxlL09yZGVyZWRIYXNoTWFwO3hwc3IA\n"
                    + "P29yZy5lY2xpcHNlLmNvbGxlY3Rpb25zLmltcGwubWFwLm9yZGVyZWQubXV0YWJsZS5PcmRlcmVk\n"
                    + "SGFzaE1hcAAAAAAAAAABDAAAeHB3BAAAAAB4";
    public static final String ORDERED_HASH_MAP_ENTRY_SET =
            "rO0ABXNyAEhvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5vcmRlcmVkLm11dGFibGUu\n"
                    + "T3JkZXJlZEhhc2hNYXAkRW50cnlTZXQAAAAAAAAAAQIAAUwABnRoaXMkMHQAQUxvcmcvZWNsaXBz\n"
                    + "ZS9jb2xsZWN0aW9ucy9pbXBsL21hcC9vcmRlcmVkL211dGFibGUvT3JkZXJlZEhhc2hNYXA7eHBz\n"
                    + "cgA/b3JnLmVjbGlwc2UuY29sbGVjdGlvbnMuaW1wbC5tYXAub3JkZXJlZC5tdXRhYmxlLk9yZGVy\n"
                    + "ZWRIYXNoTWFwAAAAAAAAAAEMAAB4cHcEAAAAAHg=";
    public static final String ORDERED_HASH_MAP_VALUES =
            "rO0ABXNyAFBvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5vcmRlcmVkLm11dGFibGUu\n"
                    + "T3JkZXJlZEhhc2hNYXAkVmFsdWVzQ29sbGVjdGlvbgAAAAAAAAABAgABTAAGdGhpcyQwdABBTG9y\n"
                    + "Zy9lY2xpcHNlL2NvbGxlY3Rpb25zL2ltcGwvbWFwL29yZGVyZWQvbXV0YWJsZS9PcmRlcmVkSGFz\n"
                    + "aE1hcDt4cHNyAD9vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5vcmRlcmVkLm11dGFi\n"
                    + "bGUuT3JkZXJlZEhhc2hNYXAAAAAAAAAAAQwAAHhwdwQAAAAAeA==";

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
    public void keySet()
    {
        Verify.assertSerializedForm(
                1L,
                ORDERED_HASH_MAP_KEY_SET,
                OrderedHashMap.newMap().keySet());
    }

    @Test
    public void entrySet()
    {
        Verify.assertSerializedForm(
                1L,
                ORDERED_HASH_MAP_ENTRY_SET,
                OrderedHashMap.newMap().entrySet());
    }

    @Test
    public void values()
    {
        Verify.assertSerializedForm(
                1L,
                ORDERED_HASH_MAP_VALUES,
                OrderedHashMap.newMap().values());
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
