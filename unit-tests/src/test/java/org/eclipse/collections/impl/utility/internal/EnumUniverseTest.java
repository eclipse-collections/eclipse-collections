/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EnumUniverseTest
{
    private enum Color
    {
        RED, GREEN, BLUE
    }

    private enum Singleton
    {
        INSTANCE
    }

    private enum WithBody
    {
        VALUE
        {
            @Override
            public String toString()
            {
                return "custom";
            }
        }
    }

    @Test
    public void size()
    {
        assertEquals(3, EnumUniverse.forClass(Color.class).size());
        assertEquals(1, EnumUniverse.forClass(Singleton.class).size());
        assertEquals(1, EnumUniverse.forClass(WithBody.class).size());
    }

    @Test
    public void fromOrdinal()
    {
        EnumUniverse<Color> universe = EnumUniverse.forClass(Color.class);
        assertEquals(Color.RED, universe.fromOrdinal(0));
        assertEquals(Color.GREEN, universe.fromOrdinal(1));
        assertEquals(Color.BLUE, universe.fromOrdinal(2));
    }

    @Test
    public void fromOrdinalOutOfBounds()
    {
        EnumUniverse<Color> universe = EnumUniverse.forClass(Color.class);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> universe.fromOrdinal(-1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> universe.fromOrdinal(3));
    }

    @Test
    public void caching()
    {
        assertSame(
                EnumUniverse.forClass(Color.class),
                EnumUniverse.forClass(Color.class));
    }

    @Test
    public void rejectsAnonymousEnumSubclass()
    {
        @SuppressWarnings("unchecked")
        Class<WithBody> anonymousClass = (Class<WithBody>) WithBody.VALUE.getClass();
        assertThrows(IllegalArgumentException.class, () -> EnumUniverse.forClass(anonymousClass));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void rejectsNonEnumClass()
    {
        Class notAnEnum = Object.class;
        assertThrows(IllegalArgumentException.class, () -> EnumUniverse.forClass(notAnEnum));
    }
}
