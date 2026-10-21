/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable;

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface UnmodifiableMutableMapIterableTestCase
        extends MutableMapIterableTestCase, UnmodifiableMapTestCase, FixedSizeIterableTestCase
{
    @Override
    default boolean allowsPut()
    {
        return false;
    }

    @Override
    @Test
    default void MutableMapIterable_removeKey()
    {
        MutableMapIterable<Object, Object> map = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> map.removeKey(2));
    }

    @Override
    @Test
    default void MutableMapIterable_removeAllKeys()
    {
        MutableMapIterable<Object, Object> map = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> map.removeAllKeys(Sets.mutable.empty()));
    }

    @Override
    @Test
    default void MutableMapIterable_removeIf()
    {
        MutableMapIterable<Object, Object> map1 = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> map1.removeIf(null));
        assertThrows(UnsupportedOperationException.class, () -> map1.removeIf(Predicates2.alwaysFalse()));

        MutableMapIterable<Object, String> map2 = this.newWith("Three", "Two", "One");
        assertThrows(UnsupportedOperationException.class, () -> map2.removeIf(Predicates2.alwaysFalse()));
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        FixedSizeIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void MutableMapIterable_getIfAbsentPut()
    {
        MutableMapIterable<String, Integer> map = this.newWithKeysValues("3", 3, "2", 2, "1", 1);
        assertEquals(3, map.getIfAbsentPut("3", () -> {
            throw new AssertionError();
        }));
        assertIterablesEqual(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);

        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut("4", () -> {
            throw new AssertionError();
        }));

        assertEquals(3, map.getIfAbsentPut("3", 4));
        assertIterablesEqual(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);

        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut("4", 4));

        assertEquals(3, map.getIfAbsentPutWithKey("3", key -> {
            throw new AssertionError();
        }));
        assertIterablesEqual(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);

        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPutWithKey("4", key -> {
            throw new AssertionError();
        }));

        assertEquals(3, map.getIfAbsentPutWith("3", x -> x + 10, 4));
        assertIterablesEqual(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);

        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPutWith("4", x -> x + 10, 4));
    }

    @Override
    @Test
    default void MutableMapIterable_updateValue()
    {
        MutableMapIterable<String, Integer> map = this.newWithKeysValues("One", 1);
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValue("One", () -> 0, ignored -> {
                    throw new AssertionError();
                }));
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValue("Two", () -> 0, ignored -> {
                    throw new AssertionError();
                }));

        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValueWith("One", () -> 0, (ignored, parameter) -> {
                    throw new AssertionError();
                }, "test"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValueWith("Two", () -> 0, (ignored, parameter) -> {
                    throw new AssertionError();
                }, "test"));
    }

    @Override
    @Test
    default void MutableMapIterable_entrySet_setValue()
    {
        this.Map_entrySet_setValue();
    }
}
