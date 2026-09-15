/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.bimap.immutable;

import org.eclipse.collections.api.bimap.ImmutableBiMap;
import org.eclipse.collections.test.ImmutableUnorderedIterableTestCase;
import org.eclipse.collections.test.bimap.UnsortedBiMapTestCase;
import org.junit.jupiter.api.Test;

public interface ImmutableUnsortedBiMapTestCase extends UnsortedBiMapTestCase, ImmutableUnorderedIterableTestCase
{
    @Override
    <T> ImmutableBiMap<Object, T> newWith(T... elements);

    @Override
    default boolean allowsPut()
    {
        return false;
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        ImmutableUnorderedIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void RichIterable_toArray()
    {
        UnsortedBiMapTestCase.super.RichIterable_toArray();
    }
}
