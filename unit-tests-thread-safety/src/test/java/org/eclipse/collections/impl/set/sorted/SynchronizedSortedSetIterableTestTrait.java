/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted;

import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.impl.set.SynchronizedSetIterableTestTrait;
import org.junit.jupiter.api.Test;

public interface SynchronizedSortedSetIterableTestTrait
        extends SynchronizedSetIterableTestTrait
{
    @Override
    SortedSetIterable<String> getClassUnderTest();

    @Test
    default void powerSet_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().powerSet());
    }
}
