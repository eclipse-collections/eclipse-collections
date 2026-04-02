/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.list;

import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.AbstractImmutableMultimapTestCase;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImmutableListMultimapTest extends AbstractImmutableMultimapTestCase
{
    @Override
    protected <K, V> ImmutableListMultimap<K, V> classUnderTest()
    {
        return FastListMultimap.<K, V>newMultimap().toImmutable();
    }

    @Override
    protected MutableList<String> mutableCollection()
    {
        return Lists.mutable.of();
    }

    @Override
    @Test
    public void noDuplicates()
    {
        // Lists allow duplicates
    }

    @Test
    public void forEachKeyMultiValue()
    {
        MutableSet<Pair<String, Iterable<Integer>>> collection = UnifiedSet.newSet();
        FastListMultimap<String, Integer> multimap = FastListMultimap.newMultimap();
        multimap.put("Two", 2);
        multimap.put("Two", 1);
        multimap.put("Three", 3);
        multimap.put("Three", 3);
        multimap.toImmutable().forEachKeyMultiValues((key, values) -> collection.add(Tuples.pair(key, values)));
        MutableSet<Pair<String, MutableList<Integer>>> expected = Sets.mutable.with(
                Tuples.pair("Two", Lists.mutable.with(2, 1)),
                Tuples.pair("Three", Lists.mutable.with(3, 3)));
        assertEquals(expected, collection);
    }

    @Test
    public void forEachKeyImmutableList()
    {
        MutableSet<Pair<String, ImmutableList<Integer>>> collection = UnifiedSet.newSet();
        MutableListMultimap<String, Integer> multimap = Multimaps.mutable.list.empty();
        multimap.put("Two", 2);
        multimap.put("Two", 1);
        multimap.put("Three", 3);
        multimap.put("Three", 3);
        multimap.toImmutable().forEachKeyImmutableList((key, values) -> collection.add(Tuples.pair(key, values)));
        MutableSet<Pair<String, ImmutableList<Integer>>> exptected = Sets.mutable.with(
                Tuples.pair("Two", Lists.immutable.with(2, 1)),
                Tuples.pair("Three", Lists.immutable.with(3, 3)));
        assertEquals(exptected, collection);
    }

    @Override
    @Test
    public void flip()
    {
        ImmutableListMultimap<String, Integer> multimap = this.<String, Integer>classUnderTest()
                .newWith("Less than 2", 1)
                .newWith("Less than 3", 1)
                .newWith("Less than 3", 2)
                .newWith("Less than 3", 2);
        ImmutableBagMultimap<Integer, String> flipped = multimap.flip();
        assertEquals(Bags.immutable.with("Less than 3", "Less than 3"), flipped.get(2));
        assertEquals(Bags.immutable.with("Less than 2", "Less than 3"), flipped.get(1));
    }

    @Override
    @Test
    public void selectKeysValues()
    {
        MutableListMultimap<String, Integer> mutableMultimap = FastListMultimap.newMultimap();
        mutableMultimap.putAll("One", FastList.newListWith(1, 2, 3, 4, 2));
        mutableMultimap.putAll("Two", FastList.newListWith(2, 3, 4, 5, 2));
        ImmutableListMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableListMultimap<String, Integer> selectedMultimap = immutableMap.selectKeysValues((key, value) -> "Two".equals(key) && (value % 2 == 0));
        MutableListMultimap<String, Integer> expectedMultimap = FastListMultimap.newMultimap();
        expectedMultimap.putAll("Two", FastList.newListWith(2, 4, 2));
        ImmutableListMultimap<String, Integer> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertListMultimapsEqual(expectedImmutableMultimap, selectedMultimap);
    }

    @Override
    @Test
    public void rejectKeysValues()
    {
        MutableListMultimap<String, Integer> mutableMultimap = FastListMultimap.newMultimap();
        mutableMultimap.putAll("One", FastList.newListWith(1, 2, 3, 4, 1));
        mutableMultimap.putAll("Two", FastList.newListWith(2, 3, 4, 5, 1));
        ImmutableListMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableListMultimap<String, Integer> rejectedMultimap = immutableMap.rejectKeysValues((key, value) -> "Two".equals(key) || (value % 2 == 0));
        MutableListMultimap<String, Integer> expectedMultimap = FastListMultimap.newMultimap();
        expectedMultimap.putAll("One", FastList.newListWith(1, 3, 1));
        ImmutableListMultimap<String, Integer> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertListMultimapsEqual(expectedImmutableMultimap, rejectedMultimap);
    }

    @Override
    @Test
    public void selectKeysMultiValues()
    {
        MutableListMultimap<Integer, String> mutableMultimap = FastListMultimap.newMultimap();
        mutableMultimap.putAll(1, FastList.newListWith("1", "3", "4"));
        mutableMultimap.putAll(2, FastList.newListWith("2", "3", "4", "5", "2"));
        mutableMultimap.putAll(3, FastList.newListWith("2", "3", "4", "5", "2"));
        mutableMultimap.putAll(4, FastList.newListWith("1", "3", "4"));
        ImmutableListMultimap<Integer, String> immutableMap = mutableMultimap.toImmutable();
        ImmutableListMultimap<Integer, String> selectedMultimap = immutableMap.selectKeysMultiValues((key, values) -> key % 2 == 0 && Iterate.sizeOf(values) > 3);
        MutableListMultimap<Integer, String> expectedMultimap = FastListMultimap.newMultimap();
        expectedMultimap.putAll(2, FastList.newListWith("2", "3", "4", "5", "2"));
        ImmutableListMultimap<Integer, String> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertListMultimapsEqual(expectedImmutableMultimap, selectedMultimap);
    }

    @Override
    @Test
    public void rejectKeysMultiValues()
    {
        MutableListMultimap<Integer, String> mutableMultimap = FastListMultimap.newMultimap();
        mutableMultimap.putAll(1, FastList.newListWith("1", "2", "3", "4", "1"));
        mutableMultimap.putAll(2, FastList.newListWith("2", "3", "4", "5", "1"));
        mutableMultimap.putAll(3, FastList.newListWith("2", "3", "4", "2"));
        mutableMultimap.putAll(4, FastList.newListWith("1", "3", "4", "5"));
        ImmutableListMultimap<Integer, String> immutableMap = mutableMultimap.toImmutable();
        ImmutableListMultimap<Integer, String> rejectedMultimap = immutableMap.rejectKeysMultiValues((key, values) -> key % 2 == 0 || Iterate.sizeOf(values) > 4);
        MutableListMultimap<Integer, String> expectedMultimap = FastListMultimap.newMultimap();
        expectedMultimap.putAll(3, FastList.newListWith("2", "3", "4", "2"));
        ImmutableListMultimap<Integer, String> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertListMultimapsEqual(expectedImmutableMultimap, rejectedMultimap);
    }

    @Override
    @Test
    public void collectKeysValues()
    {
        MutableListMultimap<String, Integer> mutableMultimap = FastListMultimap.newMultimap();
        mutableMultimap.putAll("1", FastList.newListWith(1, 2, 3, 4, 1));
        mutableMultimap.putAll("2", FastList.newListWith(2, 3, 4, 5, 2));
        ImmutableListMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableBagMultimap<Integer, String> collectedMultimap1 = immutableMap.collectKeysValues((key, value) -> Tuples.pair(Integer.valueOf(key), value + "Value"));
        MutableBagMultimap<Integer, String> expectedMultimap1 = HashBagMultimap.newMultimap();
        expectedMultimap1.putAll(1, FastList.newListWith("1Value", "2Value", "3Value", "4Value", "1Value"));
        expectedMultimap1.putAll(2, FastList.newListWith("2Value", "3Value", "4Value", "5Value", "2Value"));
        ImmutableBagMultimap<Integer, String> expectedImmutableMultimap1 = expectedMultimap1.toImmutable();
        Verify.assertBagMultimapsEqual(expectedImmutableMultimap1, collectedMultimap1);

        ImmutableBagMultimap<Integer, String> collectedMultimap2 = immutableMap.collectKeysValues((key, value) -> Tuples.pair(1, value + "Value"));
        MutableBagMultimap<Integer, String> expectedMultimap2 = HashBagMultimap.newMultimap();
        expectedMultimap2.putAll(1, FastList.newListWith("1Value", "2Value", "3Value", "4Value", "1Value"));
        expectedMultimap2.putAll(1, FastList.newListWith("2Value", "3Value", "4Value", "5Value", "2Value"));
        ImmutableBagMultimap<Integer, String> expectedImmutableMultimap2 = expectedMultimap2.toImmutable();
        Verify.assertBagMultimapsEqual(expectedImmutableMultimap2, collectedMultimap2);
    }

    @Override
    @Test
    public void collectKeyMultiValues()
    {
        super.collectKeyMultiValues();

        MutableListMultimap<String, Integer> mutableMultimap = FastListMultimap.newMultimap();
        mutableMultimap.putAll("1", FastList.newListWith(1, 2, 3, 4, 1));
        mutableMultimap.putAll("2", FastList.newListWith(2, 3, 4, 5, 2));
        ImmutableListMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableBagMultimap<Integer, String> collectedMultimap1 = immutableMap.collectKeyMultiValues(Integer::valueOf, value -> value + "Value");
        MutableBagMultimap<Integer, String> expectedMultimap1 = HashBagMultimap.newMultimap();
        expectedMultimap1.putAll(1, FastList.newListWith("1Value", "2Value", "3Value", "4Value", "1Value"));
        expectedMultimap1.putAll(2, FastList.newListWith("2Value", "3Value", "4Value", "5Value", "2Value"));
        ImmutableBagMultimap<Integer, String> expectedImmutableMultimap1 = expectedMultimap1.toImmutable();
        Verify.assertBagMultimapsEqual(expectedImmutableMultimap1, collectedMultimap1);

        ImmutableBagMultimap<Integer, String> collectedMultimap2 = immutableMap.collectKeyMultiValues(key -> 1, value -> value + "Value");
        MutableBagMultimap<Integer, String> expectedMultimap2 = HashBagMultimap.newMultimap();
        expectedMultimap2.putAll(1, FastList.newListWith("1Value", "2Value", "3Value", "4Value", "1Value"));
        expectedMultimap2.putAll(1, FastList.newListWith("2Value", "3Value", "4Value", "5Value", "2Value"));
        ImmutableBagMultimap<Integer, String> expectedImmutableMultimap2 = expectedMultimap2.toImmutable();
        Verify.assertBagMultimapsEqual(expectedImmutableMultimap2, collectedMultimap2);
    }

    @Override
    @Test
    public void collectValues()
    {
        MutableListMultimap<String, Integer> mutableMultimap = FastListMultimap.newMultimap();
        mutableMultimap.putAll("1", FastList.newListWith(1, 2, 3, 4, 1));
        mutableMultimap.putAll("2", FastList.newListWith(2, 3, 4, 5, 2));
        ImmutableListMultimap<String, Integer> immutableMap = mutableMultimap.toImmutable();
        ImmutableListMultimap<String, String> collectedMultimap = immutableMap.collectValues(value -> value + "Value");
        MutableListMultimap<String, String> expectedMultimap = FastListMultimap.newMultimap();
        expectedMultimap.putAll("1", FastList.newListWith("1Value", "2Value", "3Value", "4Value", "1Value"));
        expectedMultimap.putAll("2", FastList.newListWith("2Value", "3Value", "4Value", "5Value", "2Value"));
        ImmutableListMultimap<String, String> expectedImmutableMultimap = expectedMultimap.toImmutable();
        Verify.assertListMultimapsEqual(expectedImmutableMultimap, collectedMultimap);
    }
}
