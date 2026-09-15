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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface IterableTestTrait
{
    // Should contain "1", "2", "3" in order
    Iterable<String> getClassUnderTest();

    @Test
    default void iterator()
    {
        int counter = 0;
        Iterator<String> iterator = this.getClassUnderTest().iterator();
        while (iterator.hasNext())
        {
            counter++;
            assertEquals(String.valueOf(counter), iterator.next());
        }
    }
}
