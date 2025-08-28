/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.util.List;
import java.util.ListIterator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface UnmodifiableListIterableTestTrait
        extends UnmodifiableIterableTestTrait
{
    @Override
    List<String> getClassUnderTest();

    @Test
    default void listIterator_remove_throws()
    {
        ListIterator<String> iterator = this.getClassUnderTest().listIterator();
        if (iterator.hasNext())
        {
            iterator.next();
        }
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    default void listIterator_add_throws()
    {
        ListIterator<String> iterator = this.getClassUnderTest().listIterator();
        if (iterator.hasNext())
        {
            iterator.next();
        }
        assertThrows(UnsupportedOperationException.class, () -> iterator.add(""));
    }

    @Test
    default void listIterator_set_throws()
    {
        ListIterator<String> iterator = this.getClassUnderTest().listIterator();
        if (iterator.hasNext())
        {
            iterator.next();
        }
        assertThrows(UnsupportedOperationException.class, () -> iterator.set(""));
    }
}
