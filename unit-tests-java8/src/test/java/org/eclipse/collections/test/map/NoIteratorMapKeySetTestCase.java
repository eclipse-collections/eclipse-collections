/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface NoIteratorMapKeySetTestCase extends MapKeySetTestCase
{
    @Override
    default boolean allowsIterator()
    {
        return false;
    }

    @Override
    default boolean allowsSerialization()
    {
        return true;
    }

    // TODO: Implement Set.equals on UnifiedMap.KeySet without delegating to iterator()
    @Override
    @Test
    default void Object_equalsAndHashCode()
    {
        AssertionError error = assertThrows(
                AssertionError.class,
                () -> this.newWith(3, 2, 1).equals(this.newWith(3, 2, 1)));
        assertEquals("No iteration patterns should delegate to iterator()", error.getMessage());
    }
}
