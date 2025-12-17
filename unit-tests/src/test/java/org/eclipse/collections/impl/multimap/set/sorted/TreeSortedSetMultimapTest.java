/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.set.sorted;

import java.util.Collections;
import java.util.Comparator;

import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of {@link TreeSortedSetMultimap}.
 */
public class TreeSortedSetMultimapTest extends AbstractMutableSortedSetMultimapTestCase
{
    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimap(Comparator<? super V> comparator)
    {
        return TreeSortedSetMultimap.newMultimap(comparator);
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimap()
    {
        return TreeSortedSetMultimap.newMultimap();
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimapWithKeyValue(K key, V value)
    {
        return this.<K, V>newMultimap()
                .withKeyValue(key, value);
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return this.<K, V>newMultimap()
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2);
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3)
    {
        return this.<K, V>newMultimap()
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2)
                .withKeyValue(key3, value3);
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        return this.<K, V>newMultimap()
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2)
                .withKeyValue(key3, value3)
                .withKeyValue(key4, value4);
    }

    @SafeVarargs
    @Override
    public final <K, V> MutableSortedSetMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return TreeSortedSetMultimap.newMultimap(pairs);
    }

    @Override
    protected <K, V> MutableSortedSetMultimap<K, V> newMultimapFromPairs(Iterable<Pair<K, V>> inputIterable)
    {
        return TreeSortedSetMultimap.newMultimap(inputIterable);
    }

    @SafeVarargs
    @Override
    protected final <V> TreeSortedSet<V> createCollection(V... args)
    {
        return TreeSortedSet.newSetWith(args);
    }

    @Test
    public void testEmptyConstructor()
    {
        MutableSortedSetMultimap<Integer, Integer> map = TreeSortedSetMultimap.newMultimap();
        for (int i = 1; i < 6; ++i)
        {
            for (int j = 1; j < i + 1; ++j)
            {
                map.put(i, j);
            }
        }
        Verify.assertSize(5, map.keysView().toList());
        for (int i = 1; i < 6; ++i)
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Interval.oneTo(i)), map.get(i));
        }
    }

    @Test
    public void testComparatorConstructors()
    {
        MutableSortedSetMultimap<Boolean, Integer> revMap = TreeSortedSetMultimap.newMultimap(Collections.reverseOrder());
        for (int i = 1; i < 10; ++i)
        {
            revMap.put(IntegerPredicates.isOdd().accept(i), i);
        }
        Verify.assertSize(2, revMap.keysView().toList());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 9, 7, 5, 3, 1), revMap.get(Boolean.TRUE));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 8, 6, 4, 2), revMap.get(Boolean.FALSE));
        MutableSortedSetMultimap<Boolean, Integer> revMap2 = TreeSortedSetMultimap.newMultimap(revMap);
        Verify.assertMapsEqual(revMap2.toMap(), revMap.toMap());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 9, 7, 5, 3, 1), revMap2.get(Boolean.TRUE));
    }

    @Test
    public void testMultimapConstructor()
    {
        MutableSetMultimap<Integer, Integer> map = UnifiedSetMultimap.newMultimap();
        TreeSortedSetMultimap<Integer, Integer> map2 = TreeSortedSetMultimap.newMultimap();
        for (int i = 1; i < 6; ++i)
        {
            map.putAll(i, Interval.oneTo(i));
            map2.putAll(i, Interval.oneTo(i));
        }
        TreeSortedSetMultimap<Integer, Integer> sortedMap = TreeSortedSetMultimap.newMultimap(map);
        TreeSortedSetMultimap<Integer, Integer> sortedMap2 = TreeSortedSetMultimap.newMultimap(map2);
        for (int i = 1; i < 6; ++i)
        {
            Verify.assertSortedSetsEqual(map.get(i).toSortedSet(), sortedMap.get(i));
            Verify.assertSortedSetsEqual(map.get(i).toSortedSet(), sortedMap2.get(i));
        }
    }

    @Test
    public void testCollection()
    {
        TreeSortedSetMultimap<Integer, Integer> setMultimap = TreeSortedSetMultimap.newMultimap(Collections.reverseOrder());
        MutableSortedSet<Integer> collection = setMultimap.createCollection();
        collection.addAll(FastList.newListWith(1, 4, 2, 3, 5));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 5, 4, 3, 2, 1), collection);
        setMultimap.putAll(1, collection);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 5, 4, 3, 2, 1), collection);
        setMultimap.put(1, 0);
        assertEquals(Integer.valueOf(0), setMultimap.get(1).getLast());
        setMultimap.putAll(2, FastList.newListWith(0, 1, 2, 4, 2, 1, 4, 5, 3, 4, 5));
        Verify.assertSortedSetsEqual(setMultimap.get(1), setMultimap.get(2));
    }

    @Test
    public void testNewEmpty()
    {
        TreeSortedSetMultimap<Object, Integer> expected = TreeSortedSetMultimap.newMultimap(Collections.reverseOrder());
        TreeSortedSetMultimap<Object, Integer> actual = expected.newEmpty();
        expected.putAll(1, FastList.newListWith(4, 3, 1, 2));
        expected.putAll(2, FastList.newListWith(5, 7, 6, 8));
        actual.putAll(1, FastList.newListWith(4, 3, 1, 2));
        actual.putAll(2, FastList.newListWith(5, 7, 6, 8));
        Verify.assertMapsEqual(expected.toMap(), actual.toMap());
        Verify.assertSortedSetsEqual(expected.get(1), actual.get(1));
        Verify.assertSortedSetsEqual(expected.get(2), actual.get(2));
    }
}
