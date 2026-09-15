/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.test.Verify.assertThrows;

public interface FixedSizeIterableTestCase extends IterableTestCase
{
    @Override
    default boolean allowsAdd()
    {
        return false;
    }

    @Override
    default boolean allowsRemove()
    {
        return false;
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        Iterable<Integer> collection = this.newWith(3, 2, 1);
        Iterator<Integer> iterator = collection.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
}
