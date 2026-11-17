/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.test.MutableUnorderedIterableTestCase;
import org.eclipse.collections.test.map.UnsortedMapIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;

public interface MutableMapTestCase extends UnsortedMapIterableTestCase, MutableUnorderedIterableTestCase, MutableMapIterableTestCase
{
    @Override
    <T> MutableMap<Object, T> newWith(T... elements);

    @Override
    <K, V> MutableMap<K, V> newWithKeysValues(Object... elements);

    @Override
    @Test
    default void Object_toString()
    {
        UnsortedMapIterableTestCase.super.Object_toString();

        MutableMap<String, Integer> map = this.newWithKeysValues("Two", 2, "One", 1);
        assertThat(map.keySet().toString(), isOneOf("[One, Two]", "[Two, One]"));
        assertThat(map.values().toString(), isOneOf("[1, 2]", "[2, 1]"));
        assertThat(map.entrySet().toString(), isOneOf("[One=1, Two=2]", "[Two=2, One=1]"));
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        MutableMapIterableTestCase.super.Iterable_remove();
    }
}
