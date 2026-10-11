/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap;

import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.SynchronizedTestTrait;
import org.junit.jupiter.api.Test;

public interface SynchronizedMultimapTestTrait
        extends SynchronizedTestTrait
{
    @Override
    MutableMultimap<String, String> getClassUnderTest();

    @Test
    default void withKeyValue_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withKeyValue("foo", "bar"));
    }

    @Test
    default void withKeyMultiValues_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withKeyMultiValues("foo", "bar", "baz"));
    }
}
