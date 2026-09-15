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

import org.eclipse.collections.api.InternalIterable;
import org.junit.jupiter.api.Test;

public interface SynchronizedMutableIterableTestTrait
        extends SynchronizedTestTrait, InternalIterableTestTrait
{
    @Override
    InternalIterable<String> getClassUnderTest();

    @Test
    default void iterator_not_synchronized()
    {
        this.assertNotSynchronized(() ->
        {
            Iterator<String> iterator = this.getClassUnderTest().iterator();
            iterator.hasNext();
            if (iterator.hasNext())
            {
                iterator.next();
            }
        });
    }

    @Test
    default void forEach_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().forEach(x -> { }));
    }

    @Test
    default void forEachWithIndex_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().forEachWithIndex((x, index) -> { }));
    }

    @Test
    default void forEachWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().forEachWith((x, parameter) -> { }, "!"));
    }
}
