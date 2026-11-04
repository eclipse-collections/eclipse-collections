/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.bimap.mutable;

import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.test.MutableUnorderedIterableTestCase;
import org.eclipse.collections.test.bimap.UnsortedBiMapTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;

public interface MutableUnsortedBiMapTestCase extends UnsortedBiMapTestCase, MutableUnorderedIterableTestCase
{
    @Override
    <T> MutableBiMap<Object, T> newWith(T... elements);

    @Override
    <K, V> MutableBiMap<K, V> newWithKeysValues(Object... elements);

    @Override
    @Test
    default void Object_toString()
    {
        UnsortedBiMapTestCase.super.Object_toString();

        MutableBiMap<String, Integer> bimap = this.newWithKeysValues("Two", 2, "One", 1);
        assertThat(bimap.keySet().toString(), isOneOf("[One, Two]", "[Two, One]"));
        assertThat(bimap.values().toString(), isOneOf("[1, 2]", "[2, 1]"));
        assertThat(bimap.entrySet().toString(), isOneOf("[One=1, Two=2]", "[Two=2, One=1]"));
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        UnsortedBiMapTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void RichIterable_toArray()
    {
        UnsortedBiMapTestCase.super.RichIterable_toArray();
    }
}
