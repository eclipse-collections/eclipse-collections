/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.set.sorted;

import java.util.Collections;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.set.UnsortedSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.AbstractImmutableMultimapTestCase;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ImmutableSortedSetMultimapTest extends AbstractImmutableMultimapTestCase
{
    @Override
    protected <K, V> ImmutableSortedSetMultimap<K, V> classUnderTest()
    {
        return TreeSortedSetMultimap.<K, V>newMultimap().toImmutable();
    }

    @Override
    protected MutableCollection<String> mutableCollection()
    {
        return TreeSortedSet.newSet();
    }

    @Test
    public void testConstructor()
    {
        UnifiedMap<Integer, ImmutableSortedSet<Integer>> map = UnifiedMap.newWithKeysValues(1, TreeSortedSet.newSetWith(1).toImmutable());
        ImmutableSortedSetMultimap<Integer, Integer> immutableMap = new ImmutableSortedSetMultimapImpl<>(map, null);
        assertEquals(FastList.newListWith(1), immutableMap.get(1).toList());
        assertNull(immutableMap.comparator());
        Verify.assertSize(1, immutableMap);
    }

    @Test
    public void testNewEmpty()
    {
        ImmutableSortedSetMultimap<Integer, Integer> map = new ImmutableSortedSetMultimapImpl<>(UnifiedMap.newMap(), Collections.reverseOrder());
        assertEquals(Collections.<Integer>reverseOrder(), map.newEmpty().comparator());
        Verify.assertEmpty(map.newEmpty());
    }

    @Test
    public void serialization()
    {
        TreeSortedSetMultimap<Integer, Integer> map = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        map.putAll(1, FastList.newListWith(1, 2, 3, 4));
        map.putAll(2, FastList.newListWith(2, 3, 4, 5));
        ImmutableSortedSetMultimap<Integer, Integer> immutableMap = map.toImmutable();
        Verify.assertPostSerializedEqualsAndHashCode(immutableMap);

        ImmutableSortedSetMultimap<Integer, Integer> deserialized = SerializeTestHelper.serializeDeserialize(immutableMap);
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4),
                deserialized.get(1).castToSortedSet());
        Verify.assertListsEqual(
                FastList.newListWith(10, 9, 8),
                deserialized.newWithAll(3, FastList.newListWith(8, 9, 10)).get(3).toList());
    }

    @Override
    @Test
    public void allowDuplicates()
    {
        // Sets do not allow duplicates
    }

    @Test
    public void forEachKeyMultiValue()
    {
        MutableSet<Pair<String, Iterable<Integer>>> collection = UnifiedSet.newSet();
        TreeSortedSetMultimap<String, Integer> multimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        multimap.put("Two", 2);
        multimap.put("Two", 1);
        multimap.put("Three", 3);
        multimap.put("Three", 3);
        multimap.toImmutable().forEachKeyMultiValues((key, values) -> collection.add(Tuples.pair(key, values)));
        assertEquals(UnifiedSet.newSetWith(Tuples.pair("Two", TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 2, 1)), Tuples.pair("Three", TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 3, 3))), collection);
    }

    @Override
    @Test
    public void flip()
    {
        ImmutableSortedSetMultimap<String, Integer> multimap = this.<String, Integer>classUnderTest()
                .newWith("Less than 2", 1)
                .newWith("Less than 3", 1)
                .newWith("Less than 3", 2)
                .newWith("Less than 3", 2);
        UnsortedSetMultimap<Integer, String> flipped = multimap.flip();
        assertEquals(Sets.immutable.with("Less than 3"), flipped.get(2));
        assertEquals(Sets.immutable.with("Less than 2", "Less than 3"), flipped.get(1));
    }

    @Override
    @Test
    public void selectKeysValues()
    {
        MutableSortedSetMultimap<String, Integer> mutableMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        mutableMultimap.putAll("One", FastList.newListWith(4, 3, 2, 1, 1));
        mutableMultimap.putAll("Two", FastList.newListWith(5, 4, 3, 2, 2));
        ImmutableSortedSetMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableSortedSetMultimap<String, Integer> selectedMultimap = immutableMap.selectKeysValues((key, value) -> "Two".equals(key) && (value % 2 == 0));
        MutableSortedSetMultimap<String, Integer> expectedMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap.putAll("Two", FastList.newListWith(4, 2));
        ImmutableSortedSetMultimap<String, Integer> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertSortedSetMultimapsEqual(expectedImmutableMultimap, selectedMultimap);
        assertSame(expectedMultimap.comparator(), selectedMultimap.comparator());
    }

    @Override
    @Test
    public void rejectKeysValues()
    {
        MutableSortedSetMultimap<String, Integer> mutableMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        mutableMultimap.putAll("One", FastList.newListWith(4, 3, 2, 1, 1));
        mutableMultimap.putAll("Two", FastList.newListWith(5, 4, 3, 2, 2));
        ImmutableSortedSetMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableSortedSetMultimap<String, Integer> rejectedMultimap = immutableMap.rejectKeysValues((key, value) -> "Two".equals(key) || (value % 2 == 0));
        MutableSortedSetMultimap<String, Integer> expectedMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap.putAll("One", FastList.newListWith(3, 1));
        ImmutableSortedSetMultimap<String, Integer> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertSortedSetMultimapsEqual(expectedImmutableMultimap, rejectedMultimap);
        assertSame(expectedMultimap.comparator(), rejectedMultimap.comparator());
    }

    @Override
    @Test
    public void selectKeysMultiValues()
    {
        MutableSortedSetMultimap<Integer, Integer> mutableMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        mutableMultimap.putAll(1, FastList.newListWith(4, 3, 1));
        mutableMultimap.putAll(2, FastList.newListWith(5, 4, 3, 2, 2));
        mutableMultimap.putAll(3, FastList.newListWith(5, 4, 3, 2, 2));
        mutableMultimap.putAll(4, FastList.newListWith(4, 3, 1));
        ImmutableSortedSetMultimap<Integer, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableSortedSetMultimap<Integer, Integer> selectedMultimap = immutableMap.selectKeysMultiValues((key, values) -> key % 2 == 0 && Iterate.sizeOf(values) > 3);
        MutableSortedSetMultimap<Integer, Integer> expectedMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap.putAll(2, FastList.newListWith(5, 4, 3, 2, 2));
        ImmutableSortedSetMultimap<Integer, Integer> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertSortedSetMultimapsEqual(expectedImmutableMultimap, selectedMultimap);
        assertSame(expectedMultimap.comparator(), selectedMultimap.comparator());
    }

    @Override
    @Test
    public void rejectKeysMultiValues()
    {
        MutableSortedSetMultimap<Integer, Integer> mutableMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        mutableMultimap.putAll(1, FastList.newListWith(5, 4, 3, 2, 1));
        mutableMultimap.putAll(2, FastList.newListWith(5, 4, 3, 2, 2));
        mutableMultimap.putAll(3, FastList.newListWith(5, 4, 2, 2));
        mutableMultimap.putAll(4, FastList.newListWith(4, 3, 1));
        ImmutableSortedSetMultimap<Integer, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableSortedSetMultimap<Integer, Integer> selectedMultimap = immutableMap.rejectKeysMultiValues((key, values) -> key % 2 == 0 || Iterate.sizeOf(values) > 4);
        MutableSortedSetMultimap<Integer, Integer> expectedMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap.putAll(3, FastList.newListWith(5, 4, 2, 2));
        ImmutableSortedSetMultimap<Integer, Integer> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertSortedSetMultimapsEqual(expectedImmutableMultimap, selectedMultimap);
        assertSame(expectedMultimap.comparator(), selectedMultimap.comparator());
    }

    @Override
    @Test
    public void collectKeysValues()
    {
        MutableSortedSetMultimap<String, Integer> mutableMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        mutableMultimap.putAll("1", FastList.newListWith(4, 3, 2, 1, 1));
        mutableMultimap.putAll("2", FastList.newListWith(5, 4, 3, 2, 2));
        ImmutableSortedSetMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableBagMultimap<Integer, String> collectedMultimap1 = immutableMap.collectKeysValues((key, value) -> Tuples.pair(Integer.valueOf(key), value + "Value"));
        MutableBagMultimap<Integer, String> expectedMultimap1 = HashBagMultimap.newMultimap();
        expectedMultimap1.putAll(1, FastList.newListWith("4Value", "3Value", "2Value", "1Value"));
        expectedMultimap1.putAll(2, FastList.newListWith("5Value", "4Value", "3Value", "2Value"));
        ImmutableBagMultimap<Integer, String> expectedImmutableMultimap1 = expectedMultimap1.toImmutable();
        Verify.assertBagMultimapsEqual(expectedImmutableMultimap1, collectedMultimap1);

        ImmutableBagMultimap<Integer, String> collectedMultimap2 = immutableMap.collectKeysValues((key, value) -> Tuples.pair(1, value + "Value"));
        MutableBagMultimap<Integer, String> expectedMultimap2 = HashBagMultimap.newMultimap();
        expectedMultimap2.putAll(1, FastList.newListWith("4Value", "3Value", "2Value", "1Value"));
        expectedMultimap2.putAll(1, FastList.newListWith("5Value", "4Value", "3Value", "2Value"));
        ImmutableBagMultimap<Integer, String> expectedImmutableMultimap2 = expectedMultimap2.toImmutable();
        Verify.assertBagMultimapsEqual(expectedImmutableMultimap2, collectedMultimap2);
    }

    @Override
    @Test
    public void collectKeyMultiValues()
    {
        super.collectKeyMultiValues();

        MutableSortedSetMultimap<String, Integer> mutableMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        mutableMultimap.putAll("1", FastList.newListWith(4, 3, 2, 1, 1));
        mutableMultimap.putAll("2", FastList.newListWith(5, 4, 3, 2, 2));
        ImmutableSortedSetMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableBagMultimap<Integer, String> collectedMultimap1 = immutableMap.collectKeyMultiValues(Integer::valueOf, value -> value + "Value");
        MutableBagMultimap<Integer, String> expectedMultimap1 = HashBagMultimap.newMultimap();
        expectedMultimap1.putAll(1, FastList.newListWith("4Value", "3Value", "2Value", "1Value"));
        expectedMultimap1.putAll(2, FastList.newListWith("5Value", "4Value", "3Value", "2Value"));
        ImmutableBagMultimap<Integer, String> expectedImmutableMultimap1 = expectedMultimap1.toImmutable();
        Verify.assertBagMultimapsEqual(expectedImmutableMultimap1, collectedMultimap1);

        ImmutableBagMultimap<Integer, String> collectedMultimap2 = immutableMap.collectKeyMultiValues(key -> 1, value -> value + "Value");
        MutableBagMultimap<Integer, String> expectedMultimap2 = HashBagMultimap.newMultimap();
        expectedMultimap2.putAll(1, FastList.newListWith("4Value", "3Value", "2Value", "1Value"));
        expectedMultimap2.putAll(1, FastList.newListWith("5Value", "4Value", "3Value", "2Value"));
        ImmutableBagMultimap<Integer, String> expectedImmutableMultimap2 = expectedMultimap2.toImmutable();
        Verify.assertBagMultimapsEqual(expectedImmutableMultimap2, collectedMultimap2);
    }

    @Override
    @Test
    public void collectValues()
    {
        MutableSortedSetMultimap<String, Integer> mutableMultimap = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        mutableMultimap.putAll("1", FastList.newListWith(4, 3, 2, 1, 1));
        mutableMultimap.putAll("2", FastList.newListWith(5, 4, 3, 2, 2));
        ImmutableSortedSetMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableListMultimap<String, String> collectedMultimap = immutableMap.collectValues(value -> value + "Value");
        MutableListMultimap<String, String> expectedMultimap = FastListMultimap.newMultimap();
        expectedMultimap.putAll("1", FastList.newListWith("4Value", "3Value", "2Value", "1Value"));
        expectedMultimap.putAll("2", FastList.newListWith("5Value", "4Value", "3Value", "2Value"));
        ImmutableListMultimap<String, String> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertListMultimapsEqual(expectedImmutableMultimap, collectedMultimap);
    }
}
