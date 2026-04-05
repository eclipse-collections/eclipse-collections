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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface EmptyIterableTestTrait
        extends UnmodifiableIterableTestTrait
{
    @Test
    default void iterator_hasNext()
    {
        Iterator<String> iterator = this.getClassUnderTest().iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    default void iterator_next()
    {
        Iterator<String> iterator = this.getClassUnderTest().iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }
}
