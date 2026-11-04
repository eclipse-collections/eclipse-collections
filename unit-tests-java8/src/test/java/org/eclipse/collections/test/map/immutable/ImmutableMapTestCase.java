/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.immutable;

import java.util.Map;

import org.eclipse.collections.impl.map.immutable.AbstractImmutableMap;
import org.eclipse.collections.test.ImmutableUnorderedIterableTestCase;
import org.eclipse.collections.test.map.UnsortedMapIterableTestCase;
import org.eclipse.collections.test.map.mutable.UnmodifiableMapTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public interface ImmutableMapTestCase extends UnsortedMapIterableTestCase, ImmutableUnorderedIterableTestCase, UnmodifiableMapTestCase
{
    @Override
    <T> AbstractImmutableMap<Object, T> newWith(T... elements);

    @Override
    <K, V> AbstractImmutableMap<K, V> newWithKeysValues(Object... elements);

    @Override
    default boolean allowsPut()
    {
        return false;
    }

    @Override
    default boolean supportsNullKeys()
    {
        return UnsortedMapIterableTestCase.super.supportsNullKeys();
    }

    @Override
    default boolean supportsNullValues()
    {
        return UnsortedMapIterableTestCase.super.supportsNullValues();
    }

    @Override
    default boolean supportsNonComparableKeys()
    {
        return UnsortedMapIterableTestCase.super.supportsNonComparableKeys();
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        ImmutableUnorderedIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void Object_equals_hashCode()
    {
        UnsortedMapIterableTestCase.super.Object_equals_hashCode();
        UnmodifiableMapTestCase.super.Object_equals_hashCode();
    }

    @Override
    @Test
    default void Object_toString()
    {
        // MapTestCase.Object_toString() assumes insertion order
        UnsortedMapIterableTestCase.super.Object_toString();
    }

    @Override
    @Test
    default void Map_putIfAbsent()
    {
        // TODO: Throw immediately instead of returning the existing value; AbstractImmutableMap inherits the default java.util.Map.putIfAbsent().
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertEquals("1", map.putIfAbsent(1, "One"));
        assertThrows(UnsupportedOperationException.class, () -> map.putIfAbsent(4, "4"));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    @Test
    default void Map_remove_key_value()
    {
        // TODO: Throw immediately instead of returning false for an absent entry; AbstractImmutableMap inherits the default java.util.Map.remove(Object, Object).
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.remove(1, "1"));
        assertFalse(map.remove(2, "wrong"));
        assertFalse(map.remove(4, "4"));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    @Test
    default void Map_replace()
    {
        // TODO: Throw immediately instead of returning for an absent key or a mismatched value; AbstractImmutableMap inherits the default java.util.Map.replace() methods.
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.replace(1, "One"));
        assertNull(map.replace(4, "Four"));
        assertThrows(UnsupportedOperationException.class, () -> map.replace(2, "2", "Two"));
        assertFalse(map.replace(3, "wrong", "Three"));
        assertFalse(map.replace(4, "4", "Four"));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    @Test
    default void Map_merge()
    {
        // TODO: Throw before calling the function, like the unmodifiable maps do; AbstractImmutableMap inherits the default java.util.Map.merge(), which calls the function before put() throws.
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.merge(3, "4", (v1, v2) -> v1 + v2));
        assertThrows(UnsupportedOperationException.class, () -> map.merge(4, "4", (v1, v2) -> {
            fail("Expected lambda not to be called for an absent key");
            return null;
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }
}
