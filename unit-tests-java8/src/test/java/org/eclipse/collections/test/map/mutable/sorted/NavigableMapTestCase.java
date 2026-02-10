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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;

import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface NavigableMapTestCase extends SortedMapTestCase
{
    @Override
    <T> NavigableMap<Object, T> newWith(T... elements);

    @Override
    <K, V> NavigableMap<K, V> newWithKeysValues(Object... elements);

    @Test
    default void NavigableMap_lowerEntry_higherEntry_floorEntry_ceilingEntry()
    {
        NavigableMap<Integer, String> map1 = this.newWithKeysValues();
        assertNull(map1.lowerEntry(3));
        assertNull(map1.lowerKey(3));
        assertNull(map1.higherEntry(3));
        assertNull(map1.higherKey(3));
        assertNull(map1.floorEntry(3));
        assertNull(map1.floorKey(3));
        assertNull(map1.ceilingEntry(3));
        assertNull(map1.ceilingKey(3));

        NavigableMap<Integer, String> map2 = this.newWithKeysValues(2, "2", 4, "4");
        Comparator<? super Integer> comparator = map2.comparator();

        if (this.isNaturalOrder(comparator))
        {
            // lowerEntry/lowerKey: greatest entry strictly less than given
            assertNull(map2.lowerEntry(1));
            assertNull(map2.lowerKey(1));
            assertNull(map2.lowerEntry(2));
            assertNull(map2.lowerKey(2));
            assertEquals(Map.entry(2, "2"), map2.lowerEntry(3));
            assertEquals(Integer.valueOf(2), map2.lowerKey(3));
            assertEquals(Map.entry(2, "2"), map2.lowerEntry(4));
            assertEquals(Integer.valueOf(2), map2.lowerKey(4));
            assertEquals(Map.entry(4, "4"), map2.lowerEntry(5));
            assertEquals(Integer.valueOf(4), map2.lowerKey(5));

            // higherEntry/higherKey: least entry strictly greater than given
            assertEquals(Map.entry(2, "2"), map2.higherEntry(1));
            assertEquals(Integer.valueOf(2), map2.higherKey(1));
            assertEquals(Map.entry(4, "4"), map2.higherEntry(2));
            assertEquals(Integer.valueOf(4), map2.higherKey(2));
            assertEquals(Map.entry(4, "4"), map2.higherEntry(3));
            assertEquals(Integer.valueOf(4), map2.higherKey(3));
            assertNull(map2.higherEntry(4));
            assertNull(map2.higherKey(4));
            assertNull(map2.higherEntry(5));
            assertNull(map2.higherKey(5));

            // floorEntry/floorKey: greatest entry less than or equal to given
            assertNull(map2.floorEntry(1));
            assertNull(map2.floorKey(1));
            assertEquals(Map.entry(2, "2"), map2.floorEntry(2));
            assertEquals(Integer.valueOf(2), map2.floorKey(2));
            assertEquals(Map.entry(2, "2"), map2.floorEntry(3));
            assertEquals(Integer.valueOf(2), map2.floorKey(3));
            assertEquals(Map.entry(4, "4"), map2.floorEntry(4));
            assertEquals(Integer.valueOf(4), map2.floorKey(4));
            assertEquals(Map.entry(4, "4"), map2.floorEntry(5));
            assertEquals(Integer.valueOf(4), map2.floorKey(5));

            // ceilingEntry/ceilingKey: least entry greater than or equal to given
            assertEquals(Map.entry(2, "2"), map2.ceilingEntry(1));
            assertEquals(Integer.valueOf(2), map2.ceilingKey(1));
            assertEquals(Map.entry(2, "2"), map2.ceilingEntry(2));
            assertEquals(Integer.valueOf(2), map2.ceilingKey(2));
            assertEquals(Map.entry(4, "4"), map2.ceilingEntry(3));
            assertEquals(Integer.valueOf(4), map2.ceilingKey(3));
            assertEquals(Map.entry(4, "4"), map2.ceilingEntry(4));
            assertEquals(Integer.valueOf(4), map2.ceilingKey(4));
            assertNull(map2.ceilingEntry(5));
            assertNull(map2.ceilingKey(5));
        }
        if (this.isReverseOrder(comparator))
        {
            // lowerEntry/lowerKey: greatest entry strictly less than given
            assertNull(map2.lowerEntry(5));
            assertNull(map2.lowerKey(5));
            assertNull(map2.lowerEntry(4));
            assertNull(map2.lowerKey(4));
            assertEquals(Map.entry(4, "4"), map2.lowerEntry(3));
            assertEquals(Integer.valueOf(4), map2.lowerKey(3));
            assertEquals(Map.entry(4, "4"), map2.lowerEntry(2));
            assertEquals(Integer.valueOf(4), map2.lowerKey(2));
            assertEquals(Map.entry(2, "2"), map2.lowerEntry(1));
            assertEquals(Integer.valueOf(2), map2.lowerKey(1));

            // higherEntry/higherKey: least entry strictly greater than given
            assertEquals(Map.entry(4, "4"), map2.higherEntry(5));
            assertEquals(Integer.valueOf(4), map2.higherKey(5));
            assertEquals(Map.entry(2, "2"), map2.higherEntry(4));
            assertEquals(Integer.valueOf(2), map2.higherKey(4));
            assertEquals(Map.entry(2, "2"), map2.higherEntry(3));
            assertEquals(Integer.valueOf(2), map2.higherKey(3));
            assertNull(map2.higherEntry(2));
            assertNull(map2.higherKey(2));
            assertNull(map2.higherEntry(1));
            assertNull(map2.higherKey(1));

            // floorEntry/floorKey: greatest entry less than or equal to given
            assertNull(map2.floorEntry(5));
            assertNull(map2.floorKey(5));
            assertEquals(Map.entry(4, "4"), map2.floorEntry(4));
            assertEquals(Integer.valueOf(4), map2.floorKey(4));
            assertEquals(Map.entry(4, "4"), map2.floorEntry(3));
            assertEquals(Integer.valueOf(4), map2.floorKey(3));
            assertEquals(Map.entry(2, "2"), map2.floorEntry(2));
            assertEquals(Integer.valueOf(2), map2.floorKey(2));
            assertEquals(Map.entry(2, "2"), map2.floorEntry(1));
            assertEquals(Integer.valueOf(2), map2.floorKey(1));

            // ceilingEntry/ceilingKey: least entry greater than or equal to given
            assertEquals(Map.entry(4, "4"), map2.ceilingEntry(5));
            assertEquals(Integer.valueOf(4), map2.ceilingKey(5));
            assertEquals(Map.entry(4, "4"), map2.ceilingEntry(4));
            assertEquals(Integer.valueOf(4), map2.ceilingKey(4));
            assertEquals(Map.entry(2, "2"), map2.ceilingEntry(3));
            assertEquals(Integer.valueOf(2), map2.ceilingKey(3));
            assertEquals(Map.entry(2, "2"), map2.ceilingEntry(2));
            assertEquals(Integer.valueOf(2), map2.ceilingKey(2));
            assertNull(map2.ceilingEntry(1));
            assertNull(map2.ceilingKey(1));
        }
    }

    @Test
    default void NavigableMap_firstEntry_lastEntry_pollFirstEntry_pollLastEntry()
    {
        NavigableMap<Integer, String> map1 = this.newWithKeysValues();
        assertNull(map1.firstEntry());
        assertNull(map1.lastEntry());
        assertNull(map1.pollFirstEntry());
        assertNull(map1.pollLastEntry());

        NavigableMap<Integer, String> map2 = this.newWithKeysValues(2, "2", 4, "4");
        Comparator<? super Integer> comparator = map2.comparator();

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Map.entry(2, "2"), map2.firstEntry());
            assertEquals(Map.entry(4, "4"), map2.lastEntry());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Map.entry(4, "4"), map2.firstEntry());
            assertEquals(Map.entry(2, "2"), map2.lastEntry());
        }

        NavigableMap<Integer, String> map3 = this.newWithKeysValues(2, "2", 4, "4");

        if (this.isNaturalOrder(comparator))
        {
            assertEquals(Map.entry(2, "2"), map3.pollFirstEntry());
            assertIterablesEqual(this.newWithKeysValues(4, "4"), map3);

            assertEquals(Map.entry(4, "4"), map3.pollLastEntry());
            assertIterablesEqual(this.newWithKeysValues(), map3);

            assertNull(map3.pollFirstEntry());
            assertNull(map3.pollLastEntry());
        }
        if (this.isReverseOrder(comparator))
        {
            assertEquals(Map.entry(4, "4"), map3.pollFirstEntry());
            assertIterablesEqual(this.newWithKeysValues(2, "2"), map3);

            assertEquals(Map.entry(2, "2"), map3.pollLastEntry());
            assertIterablesEqual(this.newWithKeysValues(), map3);

            assertNull(map3.pollFirstEntry());
            assertNull(map3.pollLastEntry());
        }
    }

    @Test
    default void NavigableMap_subMap_headMap_tailMap()
    {
        NavigableMap<Integer, String> map1 = this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10");
        Comparator<? super Integer> comparator = map1.comparator();

        if (this.isNaturalOrder(comparator))
        {
            // subMap(from, fromInclusive, to, toInclusive)
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6", 8, "8"), map1.subMap(4, true, 8, true));
            assertIterablesEqual(this.newWithKeysValues(6, "6"), map1.subMap(4, false, 8, false));
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6"), map1.subMap(4, true, 8, false));
            assertIterablesEqual(this.newWithKeysValues(6, "6", 8, "8"), map1.subMap(4, false, 8, true));
            assertIterablesEqual(this.newWithKeysValues(6, "6"), map1.subMap(6, true, 6, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.subMap(6, false, 6, false));
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6"), map1.subMap(3, true, 7, true));
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6"), map1.subMap(3, false, 7, false));

            // headMap(to, inclusive) - test boundaries: 1, 2, 3, 9, 10, 11
            assertIterablesEqual(this.newWithKeysValues(), map1.headMap(1, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.headMap(1, false));
            assertIterablesEqual(this.newWithKeysValues(2, "2"), map1.headMap(2, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.headMap(2, false));
            assertIterablesEqual(this.newWithKeysValues(2, "2"), map1.headMap(3, true));
            assertIterablesEqual(this.newWithKeysValues(2, "2"), map1.headMap(3, false));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8"), map1.headMap(9, true));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8"), map1.headMap(9, false));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1.headMap(10, true));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8"), map1.headMap(10, false));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1.headMap(11, true));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1.headMap(11, false));

            // tailMap(from, inclusive) - test boundaries: 1, 2, 3, 9, 10, 11
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1.tailMap(1, true));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1.tailMap(1, false));
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1.tailMap(2, true));
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6", 8, "8", 10, "10"), map1.tailMap(2, false));
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6", 8, "8", 10, "10"), map1.tailMap(3, true));
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6", 8, "8", 10, "10"), map1.tailMap(3, false));
            assertIterablesEqual(this.newWithKeysValues(10, "10"), map1.tailMap(9, true));
            assertIterablesEqual(this.newWithKeysValues(10, "10"), map1.tailMap(9, false));
            assertIterablesEqual(this.newWithKeysValues(10, "10"), map1.tailMap(10, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.tailMap(10, false));
            assertIterablesEqual(this.newWithKeysValues(), map1.tailMap(11, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.tailMap(11, false));

            // Verify view semantics: modifications reflect in both
            NavigableMap<Integer, String> subMapView = map1.subMap(4, true, 8, true);
            subMapView.put(5, "5");
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 5, "5", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(4, "4", 5, "5", 6, "6", 8, "8"), subMapView);
            map1.remove(5);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6", 8, "8"), subMapView);
            map1.put(5, "5");
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 5, "5", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(4, "4", 5, "5", 6, "6", 8, "8"), subMapView);
            subMapView.remove(5);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(4, "4", 6, "6", 8, "8"), subMapView);

            NavigableMap<Integer, String> headMapView = map1.headMap(6, true);
            headMapView.put(3, "3");
            assertIterablesEqual(this.newWithKeysValues(2, "2", 3, "3", 4, "4", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 3, "3", 4, "4", 6, "6"), headMapView);
            map1.remove(3);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6"), headMapView);
            map1.put(3, "3");
            assertIterablesEqual(this.newWithKeysValues(2, "2", 3, "3", 4, "4", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 3, "3", 4, "4", 6, "6"), headMapView);
            headMapView.remove(3);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6"), headMapView);

            NavigableMap<Integer, String> tailMapView = map1.tailMap(6, true);
            tailMapView.put(7, "7");
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 7, "7", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(6, "6", 7, "7", 8, "8", 10, "10"), tailMapView);
            map1.remove(7);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(6, "6", 8, "8", 10, "10"), tailMapView);
            map1.put(7, "7");
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 7, "7", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(6, "6", 7, "7", 8, "8", 10, "10"), tailMapView);
            tailMapView.remove(7);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4", 6, "6", 8, "8", 10, "10"), map1);
            assertIterablesEqual(this.newWithKeysValues(6, "6", 8, "8", 10, "10"), tailMapView);

            // subMap with crossed indices (from > to) should throw
            assertThrows(IllegalArgumentException.class, () -> map1.subMap(8, true, 4, true));
            assertThrows(IllegalArgumentException.class, () -> map1.subMap(8, false, 4, false));
        }
        if (this.isReverseOrder(comparator))
        {
            // subMap(from, fromInclusive, to, toInclusive)
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4"), map1.subMap(8, true, 4, true));
            assertIterablesEqual(this.newWithKeysValues(6, "6"), map1.subMap(8, false, 4, false));
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6"), map1.subMap(8, true, 4, false));
            assertIterablesEqual(this.newWithKeysValues(6, "6", 4, "4"), map1.subMap(8, false, 4, true));
            assertIterablesEqual(this.newWithKeysValues(6, "6"), map1.subMap(6, true, 6, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.subMap(6, false, 6, false));
            assertIterablesEqual(this.newWithKeysValues(6, "6", 4, "4"), map1.subMap(7, true, 3, true));
            assertIterablesEqual(this.newWithKeysValues(6, "6", 4, "4"), map1.subMap(7, false, 3, false));

            // headMap(to, inclusive) - test boundaries: 1, 2, 3, 9, 10, 11
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1.headMap(1, true));
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1.headMap(1, false));
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1.headMap(2, true));
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4"), map1.headMap(2, false));
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4"), map1.headMap(3, true));
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4"), map1.headMap(3, false));
            assertIterablesEqual(this.newWithKeysValues(10, "10"), map1.headMap(9, true));
            assertIterablesEqual(this.newWithKeysValues(10, "10"), map1.headMap(9, false));
            assertIterablesEqual(this.newWithKeysValues(10, "10"), map1.headMap(10, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.headMap(10, false));
            assertIterablesEqual(this.newWithKeysValues(), map1.headMap(11, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.headMap(11, false));

            // tailMap(from, inclusive) - test boundaries: 1, 2, 3, 9, 10, 11
            assertIterablesEqual(this.newWithKeysValues(), map1.tailMap(1, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.tailMap(1, false));
            assertIterablesEqual(this.newWithKeysValues(2, "2"), map1.tailMap(2, true));
            assertIterablesEqual(this.newWithKeysValues(), map1.tailMap(2, false));
            assertIterablesEqual(this.newWithKeysValues(2, "2"), map1.tailMap(3, true));
            assertIterablesEqual(this.newWithKeysValues(2, "2"), map1.tailMap(3, false));
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4", 2, "2"), map1.tailMap(9, true));
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4", 2, "2"), map1.tailMap(9, false));
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1.tailMap(10, true));
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4", 2, "2"), map1.tailMap(10, false));
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1.tailMap(11, true));
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1.tailMap(11, false));

            // Verify view semantics: modifications reflect in both
            NavigableMap<Integer, String> subMapView = map1.subMap(8, true, 4, true);
            subMapView.put(5, "5");
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 5, "5", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 5, "5", 4, "4"), subMapView);
            map1.remove(5);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4"), subMapView);
            map1.put(5, "5");
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 5, "5", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 5, "5", 4, "4"), subMapView);
            subMapView.remove(5);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(8, "8", 6, "6", 4, "4"), subMapView);

            NavigableMap<Integer, String> headMapView = map1.headMap(6, true);
            headMapView.put(7, "7");
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 7, "7", 6, "6", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 7, "7", 6, "6"), headMapView);
            map1.remove(7);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6"), headMapView);
            map1.put(7, "7");
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 7, "7", 6, "6", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 7, "7", 6, "6"), headMapView);
            headMapView.remove(7);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6"), headMapView);

            NavigableMap<Integer, String> tailMapView = map1.tailMap(6, true);
            tailMapView.put(3, "3");
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 3, "3", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(6, "6", 4, "4", 3, "3", 2, "2"), tailMapView);
            map1.remove(3);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(6, "6", 4, "4", 2, "2"), tailMapView);
            map1.put(3, "3");
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 3, "3", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(6, "6", 4, "4", 3, "3", 2, "2"), tailMapView);
            tailMapView.remove(3);
            assertIterablesEqual(this.newWithKeysValues(10, "10", 8, "8", 6, "6", 4, "4", 2, "2"), map1);
            assertIterablesEqual(this.newWithKeysValues(6, "6", 4, "4", 2, "2"), tailMapView);

            // subMap with crossed indices (from > to) should throw
            assertThrows(IllegalArgumentException.class, () -> map1.subMap(4, true, 8, true));
            assertThrows(IllegalArgumentException.class, () -> map1.subMap(4, false, 8, false));
        }
    }

    @Test
    default void NavigableMap_descendingMap()
    {
        NavigableMap<Integer, String> map1 = this.newWithKeysValues();
        NavigableMap<Integer, String> descending1 = map1.descendingMap();
        assertTrue(descending1.isEmpty());

        NavigableMap<Integer, String> map2 = this.newWithKeysValues(2, "2", 4, "4");
        NavigableMap<Integer, String> descending2 = map2.descendingMap();
        Comparator<? super Integer> comparator = map2.comparator();

        if (this.isNaturalOrder(comparator))
        {
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(4, "4"), Map.entry(2, "2")), descending2.entrySet());
            assertEquals(Integer.valueOf(2), map2.firstKey());
            assertEquals(Integer.valueOf(4), map2.lastKey());
            assertEquals(Integer.valueOf(4), descending2.firstKey());
            assertEquals(Integer.valueOf(2), descending2.lastKey());

            // Verify view semantics: modifications reflect in both
            descending2.put(3, "3");
            assertIterablesEqual(this.newWithKeysValues(2, "2", 3, "3", 4, "4"), map2);
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(4, "4"), Map.entry(3, "3"), Map.entry(2, "2")), descending2.entrySet());
            map2.remove(3);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4"), map2);
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(4, "4"), Map.entry(2, "2")), descending2.entrySet());
            map2.put(3, "3");
            assertIterablesEqual(this.newWithKeysValues(2, "2", 3, "3", 4, "4"), map2);
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(4, "4"), Map.entry(3, "3"), Map.entry(2, "2")), descending2.entrySet());
            descending2.remove(3);
            assertIterablesEqual(this.newWithKeysValues(2, "2", 4, "4"), map2);
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(4, "4"), Map.entry(2, "2")), descending2.entrySet());
        }
        if (this.isReverseOrder(comparator))
        {
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(2, "2"), Map.entry(4, "4")), descending2.entrySet());
            assertEquals(Integer.valueOf(4), map2.firstKey());
            assertEquals(Integer.valueOf(2), map2.lastKey());
            assertEquals(Integer.valueOf(2), descending2.firstKey());
            assertEquals(Integer.valueOf(4), descending2.lastKey());

            // Verify view semantics: modifications reflect in both
            descending2.put(3, "3");
            assertIterablesEqual(this.newWithKeysValues(4, "4", 3, "3", 2, "2"), map2);
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(2, "2"), Map.entry(3, "3"), Map.entry(4, "4")), descending2.entrySet());
            map2.remove(3);
            assertIterablesEqual(this.newWithKeysValues(4, "4", 2, "2"), map2);
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(2, "2"), Map.entry(4, "4")), descending2.entrySet());
            map2.put(3, "3");
            assertIterablesEqual(this.newWithKeysValues(4, "4", 3, "3", 2, "2"), map2);
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(2, "2"), Map.entry(3, "3"), Map.entry(4, "4")), descending2.entrySet());
            descending2.remove(3);
            assertIterablesEqual(this.newWithKeysValues(4, "4", 2, "2"), map2);
            Verify.assertIterablesEqual(Arrays.asList(Map.entry(2, "2"), Map.entry(4, "4")), descending2.entrySet());
        }

        // Double descending returns to original order
        NavigableMap<Integer, String> doubleDescending = descending2.descendingMap();
        assertIterablesEqual(map2, doubleDescending);
    }
}
