/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.immutable.sorted;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.impl.map.sorted.immutable.AbstractImmutableSortedMap;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.eclipse.collections.test.map.SortedMapIterableTestCase;
import org.eclipse.collections.test.map.mutable.UnmodifiableMapTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public interface ImmutableSortedMapIterableTestCase extends SortedMapIterableTestCase, FixedSizeIterableTestCase, UnmodifiableMapTestCase
{
    @Override
    <T> AbstractImmutableSortedMap<Object, T> newWith(T... elements);

    @Override
    <K, V> AbstractImmutableSortedMap<K, V> newWithKeysValues(Object... elements);

    @Override
    default boolean allowsPut()
    {
        return false;
    }

    @Override
    default boolean supportsNullKeys()
    {
        return SortedMapIterableTestCase.super.supportsNullKeys();
    }

    @Override
    default boolean supportsNullValues()
    {
        return SortedMapIterableTestCase.super.supportsNullValues();
    }

    @Override
    default boolean supportsNonComparableKeys()
    {
        return false;
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        FixedSizeIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void MapIterable_entrySet_iterator()
    {
        SortedMapIterableTestCase.super.MapIterable_entrySet_iterator();

        // The entrySet iterator does not support remove(), before or after next()
        Map<Integer, String> map = this.<Integer, String>newWithKeysValues(3, "Three", 2, "Two", 1, "One").castToMap();
        Iterator<Map.Entry<Integer, String>> iterator = map.entrySet().iterator();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Test
    default void Map_entrySet_add()
    {
        Map<Integer, String> map = this.<Integer, String>newWithKeysValues(3, "Three", 2, "Two", 1, "One").castToMap();
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        assertThrows(UnsupportedOperationException.class, () -> entrySet.add(ImmutableEntry.of(4, "Four")));
        assertThrows(UnsupportedOperationException.class, () -> entrySet.add(ImmutableEntry.of(1, "One")));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Test
    default void Map_entrySet_addAll()
    {
        Map<Integer, String> map = this.<Integer, String>newWithKeysValues(3, "Three", 2, "Two", 1, "One").castToMap();
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        assertThrows(UnsupportedOperationException.class, () -> entrySet.addAll(Lists.mutable.with(ImmutableEntry.of(4, "Four"))));
        assertThrows(UnsupportedOperationException.class, () -> entrySet.addAll(Lists.mutable.empty()));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Override
    @Test
    default void Map_entrySet_remove()
    {
        Map<Integer, String> map = this.<Integer, String>newWithKeysValues(3, "Three", 2, "Two", 1, "One").castToMap();
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        assertThrows(UnsupportedOperationException.class, () -> entrySet.remove(ImmutableEntry.of(2, "Two")));
        assertThrows(UnsupportedOperationException.class, () -> entrySet.remove(ImmutableEntry.of(4, "Four")));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Test
    default void Map_entrySet_removeAll()
    {
        Map<Integer, String> map = this.<Integer, String>newWithKeysValues(3, "Three", 2, "Two", 1, "One").castToMap();
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        assertThrows(UnsupportedOperationException.class, () -> entrySet.removeAll(Lists.mutable.with(ImmutableEntry.of(2, "Two"))));
        assertThrows(UnsupportedOperationException.class, () -> entrySet.removeAll(Lists.mutable.empty()));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Test
    default void Map_entrySet_clear()
    {
        Map<Integer, String> map = this.<Integer, String>newWithKeysValues(3, "Three", 2, "Two", 1, "One").castToMap();
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().clear());
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Test
    default void Map_entrySet_retainAll()
    {
        Map<Integer, String> map = this.<Integer, String>newWithKeysValues(3, "Three", 2, "Two", 1, "One").castToMap();
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        assertThrows(UnsupportedOperationException.class, () -> entrySet.retainAll(Lists.mutable.with(ImmutableEntry.of(2, "Two"))));
        assertThrows(UnsupportedOperationException.class, () -> entrySet.retainAll(Sets.mutable.withAll(entrySet)));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Override
    @Test
    default void Object_equalsAndHashCode()
    {
        SortedMapIterableTestCase.super.Object_equalsAndHashCode();
        UnmodifiableMapTestCase.super.Object_equalsAndHashCode();
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        // MapTestCase.Iterable_toString() assumes insertion order
        SortedMapIterableTestCase.super.Iterable_toString();
    }

    @Override
    @Test
    default void Map_putIfAbsent()
    {
        // TODO: Throw immediately instead of returning the existing value; AbstractImmutableSortedMap inherits the default java.util.Map.putIfAbsent().
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertEquals("1", map.putIfAbsent(1, "One"));
        assertThrows(UnsupportedOperationException.class, () -> map.putIfAbsent(4, "4"));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Override
    @Test
    default void Map_remove_key_value()
    {
        // TODO: Throw immediately instead of returning false for an absent entry; AbstractImmutableSortedMap inherits the default java.util.Map.remove(Object, Object).
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
        // TODO: Throw immediately instead of returning for an absent key or a mismatched value; AbstractImmutableSortedMap inherits the default java.util.Map.replace() methods.
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
        // TODO: Throw before calling the function, like the unmodifiable maps do; AbstractImmutableSortedMap inherits the default java.util.Map.merge(), which calls the function before put() throws.
        Map<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.merge(3, "4", (v1, v2) -> v1 + v2));
        assertThrows(UnsupportedOperationException.class, () -> map.merge(4, "4", (v1, v2) -> {
            fail("Expected lambda not to be called for an absent key");
            return null;
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }
}
