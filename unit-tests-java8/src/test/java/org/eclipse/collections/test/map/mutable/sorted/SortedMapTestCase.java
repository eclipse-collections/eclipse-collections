/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable.sorted;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.test.map.mutable.MapTestCase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SortedMapTestCase extends MapTestCase
{
    @Override
    <T> SortedMap<Object, T> newWith(T... elements);

    @Override
    <K, V> SortedMap<K, V> newWithKeysValues(Object... elements);

    default boolean isNaturalOrder(Comparator<?> comparator)
    {
        return comparator == null || comparator == Comparator.naturalOrder() || comparator == Comparators.naturalOrder();
    }

    default boolean isReverseOrder(Comparator<?> comparator)
    {
        return comparator == Comparator.reverseOrder() || comparator == Comparators.reverseNaturalOrder();
    }

    @Override
    default boolean supportsNullKeys()
    {
        return false;
    }

    @Test
    default void SortedMap_comparator_isOneOfExpected()
    {
        SortedMap<Integer, String> map = this.newWithKeysValues(3, "3", 1, "1", 2, "2");
        Comparator<? super Integer> comparator = map.comparator();

        assertThat(this.isNaturalOrder(comparator) || this.isReverseOrder(comparator))
                .as("Comparator must be either null (natural order) or reverse order, but was: %s", comparator)
                .isTrue();
    }

    @Test
    default void SortedMap_comparator()
    {
        SortedMap<Integer, String> map = this.newWithKeysValues(2, "2", 4, "4", 6, "6");
        Comparator<? super Integer> comparator = map.comparator();

        if (this.isNaturalOrder(comparator))
        {
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6"), map);
        }
        if (this.isReverseOrder(comparator))
        {
            assertIterablesEqual(this.newWithKeysValues(6, "6", 4, "4", 2, "2"), map);
        }
    }

    @Test
    default void SortedMap_subMap_headMap_tailMap()
    {
        SortedMap<Integer, String> map = this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8");
        Comparator<? super Integer> comparator = map.comparator();

        if (this.isNaturalOrder(comparator))
        {
            // subMap(fromInclusive, toExclusive)
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8"), map.subMap(1, 9));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6"), map.subMap(2, 8));
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6"), map.subMap(3, 7));
            assertIterablesEqual(this.newWithKeysValues(4, "4"), map.subMap(4, 6));
            assertIterablesEqual(this.newWithKeysValues(), map.subMap(5, 5));
            assertIterablesEqual(this.newWithKeysValues(), map.subMap(6, 6));
            assertThrows(IllegalArgumentException.class, () -> map.subMap(7, 3));

            // headMap(toExclusive)
            assertIterablesEqual(this.newWithKeysValues(), map.headMap(1));
            assertIterablesEqual(this.newWithKeysValues(), map.headMap(2));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4"), map.headMap(5));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4"), map.headMap(6));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8"), map.headMap(9));

            // tailMap(fromInclusive)
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8"), map.tailMap(1));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8"), map.tailMap(2));
            assertIterablesEqual(this.newWithKeysValues(6, "6", 8, "8"), map.tailMap(5));
            assertIterablesEqual(this.newWithKeysValues(6, "6", 8, "8"), map.tailMap(6));
            assertIterablesEqual(this.newWithKeysValues(), map.tailMap(9));
        }

        if (this.isReverseOrder(comparator))
        {
            // subMap(fromInclusive, toExclusive)
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4", 2, "2"), map.subMap(9, 1));
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4"), map.subMap(8, 2));
            assertIterablesEqual(this.newWithKeysValues(6, "6", 4, "4"), map.subMap(7, 3));
            assertIterablesEqual(this.newWithKeysValues(6, "6"), map.subMap(6, 4));
            assertIterablesEqual(this.newWithKeysValues(), map.subMap(5, 5));
            assertIterablesEqual(this.newWithKeysValues(), map.subMap(6, 6));
            assertThrows(IllegalArgumentException.class, () -> map.subMap(3, 7));

            // headMap(toExclusive)
            assertIterablesEqual(this.newWithKeysValues(), map.headMap(9));
            assertIterablesEqual(this.newWithKeysValues(), map.headMap(8));
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6"), map.headMap(5));
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6"), map.headMap(4));
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4", 2, "2"), map.headMap(1));

            // tailMap(fromInclusive)
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4", 2, "2"), map.tailMap(9));
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4", 2, "2"), map.tailMap(8));
            assertIterablesEqual(this.newWithKeysValues(4, "4", 2, "2"), map.tailMap(5));
            assertIterablesEqual(this.newWithKeysValues(4, "4", 2, "2"), map.tailMap(4));
            assertIterablesEqual(this.newWithKeysValues(), map.tailMap(1));
        }
    }

    @Test
    default void SortedMap_firstKey_lastKey()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWithKeysValues().firstKey());
        assertThrows(NoSuchElementException.class, () -> this.newWithKeysValues().lastKey());

        SortedMap<Integer, String> map1 = this.newWithKeysValues(42, "42");
        assertEquals(Integer.valueOf(42), map1.firstKey());
        assertEquals(Integer.valueOf(42), map1.lastKey());

        SortedMap<Integer, String> map3 = this.newWithKeysValues(2, "2", 4, "4", 6, "6");
        Comparator<? super Integer> comparator = map3.comparator();

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Integer.valueOf(2), map3.firstKey());
            assertEquals(Integer.valueOf(6), map3.lastKey());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Integer.valueOf(6), map3.firstKey());
            assertEquals(Integer.valueOf(2), map3.lastKey());
        }
    }

    @Test
    default void SortedMap_entrySet_iterator_order()
    {
        SortedMap<Integer, String> map = this.newWithKeysValues(2, "2", 4, "4", 6, "6");
        Comparator<? super Integer> comparator = map.comparator();

        Iterator<Map.Entry<Integer, String>> iterator = map.entrySet().iterator();

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Map.entry(2, "2"), iterator.next());
            assertEquals(Map.entry(4, "4"), iterator.next());
            assertEquals(Map.entry(6, "6"), iterator.next());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Map.entry(6, "6"), iterator.next());
            assertEquals(Map.entry(4, "4"), iterator.next());
            assertEquals(Map.entry(2, "2"), iterator.next());
        }
    }

    @Test
    default void SortedMap_keySet_iterator_order()
    {
        SortedMap<Integer, String> map = this.newWithKeysValues(2, "2", 4, "4", 6, "6");
        Comparator<? super Integer> comparator = map.comparator();

        Iterator<Integer> iterator = map.keySet().iterator();

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Integer.valueOf(2), iterator.next());
            assertEquals(Integer.valueOf(4), iterator.next());
            assertEquals(Integer.valueOf(6), iterator.next());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Integer.valueOf(6), iterator.next());
            assertEquals(Integer.valueOf(4), iterator.next());
            assertEquals(Integer.valueOf(2), iterator.next());
        }
    }

    @Test
    default void SortedMap_values_iterator_order()
    {
        SortedMap<Integer, String> map = this.newWithKeysValues(2, "2", 4, "4", 6, "6");
        Comparator<? super Integer> comparator = map.comparator();

        Iterator<String> iterator = map.values().iterator();

        if (this.isNaturalOrder(comparator))
        {
            assertEquals("2", iterator.next());
            assertEquals("4", iterator.next());
            assertEquals("6", iterator.next());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals("6", iterator.next());
            assertEquals("4", iterator.next());
            assertEquals("2", iterator.next());
        }
    }
}
