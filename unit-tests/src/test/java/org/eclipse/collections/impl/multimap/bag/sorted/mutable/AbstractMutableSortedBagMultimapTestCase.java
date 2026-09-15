/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.bag.sorted.mutable;

import java.util.Comparator;

import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.BagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.sortedbag.ImmutableSortedBagMultimap;
import org.eclipse.collections.api.multimap.sortedbag.MutableSortedBagMultimap;
import org.eclipse.collections.api.multimap.sortedbag.SortedBagMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.AbstractMutableMultimapTestCase;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Test of {@link TreeBagMultimap}.
 */
public abstract class AbstractMutableSortedBagMultimapTestCase extends AbstractMutableMultimapTestCase
{
    protected abstract <K, V> MutableSortedBagMultimap<K, V> newMultimap(Comparator<V> comparator);

    @Override
    protected abstract <K, V> MutableSortedBagMultimap<K, V> newMultimap();

    @Override
    protected abstract <K, V> MutableSortedBagMultimap<K, V> newMultimapWithKeyValue(K key, V value);

    @Override
    protected abstract <K, V> MutableSortedBagMultimap<K, V> newMultimapWithKeysValues(K key1, V value1, K key2, V value2);

    @Override
    protected abstract <K, V> MutableSortedBagMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3);

    @Override
    protected abstract <K, V> MutableSortedBagMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4);

    @Override
    protected abstract <K, V> MutableSortedBagMultimap<K, V> newMultimap(Pair<K, V>... pairs);

    @Override
    protected abstract <K, V> MutableSortedBagMultimap<K, V> newMultimapFromPairs(Iterable<Pair<K, V>> inputIterable);

    @Override
    protected abstract <V> MutableSortedBag<V> createCollection(V... args);

    @Override
    @Test
    public void flip()
    {
        SortedBagMultimap<String, Integer> multimap = this.newMultimapWithKeysValues("Less than 2", 1, "Less than 3", 1, "Less than 3", 2, "Less than 3", 2);
        BagMultimap<Integer, String> flipped = multimap.flip();
        assertEquals(Bags.immutable.with("Less than 3", "Less than 3"), flipped.get(2));
        assertEquals(Bags.immutable.with("Less than 2", "Less than 3"), flipped.get(1));
    }

    @Override
    @Test
    public void toImmutable()
    {
        Verify.assertInstanceOf(MutableSortedBagMultimap.class, this.newMultimap());
        Verify.assertInstanceOf(ImmutableSortedBagMultimap.class, this.newMultimap().toImmutable());
        assertFalse(this.newMultimap().toImmutable() instanceof MutableSortedBagMultimap);
    }

    @Override
    @Test
    public void serialization()
    {
        MutableSortedBagMultimap<Integer, Integer> map = this.newMultimap(Comparators.reverseNaturalOrder());
        map.putAll(1, FastList.newListWith(1, 2, 3, 4));
        map.putAll(2, FastList.newListWith(2, 3, 4, 5));
        Verify.assertPostSerializedEqualsAndHashCode(map);

        MutableSortedBagMultimap<Integer, Integer> deserialized = SerializeTestHelper.serializeDeserialize(map);
        Verify.assertSortedBagsEqual(
                TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4),
                deserialized.get(1));

        deserialized.putAll(3, FastList.newListWith(8, 9, 10));
        Verify.assertListsEqual(FastList.newListWith(10, 9, 8), deserialized.get(3).toList());
    }

    @Override
    @Test
    public void clear()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "Two", 3, "Three");
        multimap.clear();
        Verify.assertEmpty(multimap);
    }

    @Override
    @Test
    public void selectKeysValues()
    {
        MutableSortedBagMultimap<String, Integer> multimap = this.newMultimap(Comparators.reverseNaturalOrder());
        multimap.putAll("One", FastList.newListWith(4, 3, 2, 1, 1));
        multimap.putAll("Two", FastList.newListWith(5, 4, 3, 2, 2));
        MutableSortedBagMultimap<String, Integer> selectedMultimap = multimap.selectKeysValues((key, value) -> "Two".equals(key) && (value % 2 == 0));
        MutableSortedBagMultimap<String, Integer> expectedMultimap = this.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap.putAll("Two", FastList.newListWith(4, 2, 2));
        Verify.assertSortedBagMultimapsEqual(expectedMultimap, selectedMultimap);
        assertSame(expectedMultimap.comparator(), selectedMultimap.comparator());
    }

    @Override
    @Test
    public void rejectKeysValues()
    {
        MutableSortedBagMultimap<String, Integer> multimap = this.newMultimap(Comparators.reverseNaturalOrder());
        multimap.putAll("One", FastList.newListWith(4, 3, 2, 1, 1));
        multimap.putAll("Two", FastList.newListWith(5, 4, 3, 2, 2));
        MutableSortedBagMultimap<String, Integer> rejectedMultimap = multimap.rejectKeysValues((key, value) -> "Two".equals(key) || (value % 2 == 0));
        MutableSortedBagMultimap<String, Integer> expectedMultimap = this.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap.putAll("One", FastList.newListWith(3, 1, 1));
        Verify.assertSortedBagMultimapsEqual(expectedMultimap, rejectedMultimap);
        assertSame(expectedMultimap.comparator(), rejectedMultimap.comparator());
    }

    @Override
    @Test
    public void selectKeysMultiValues()
    {
        MutableSortedBagMultimap<Integer, Integer> multimap = this.newMultimap(Comparators.reverseNaturalOrder());
        multimap.putAll(1, FastList.newListWith(4, 3, 1));
        multimap.putAll(2, FastList.newListWith(5, 4, 3, 2, 2));
        multimap.putAll(3, FastList.newListWith(5, 4, 3, 2, 2));
        multimap.putAll(4, FastList.newListWith(4, 3, 1));
        MutableSortedBagMultimap<Integer, Integer> selectedMultimap = multimap.selectKeysMultiValues((key, values) -> key % 2 == 0 && Iterate.sizeOf(values) > 3);
        MutableSortedBagMultimap<Integer, Integer> expectedMultimap = this.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap.putAll(2, FastList.newListWith(5, 4, 3, 2, 2));
        Verify.assertSortedBagMultimapsEqual(expectedMultimap, selectedMultimap);
        assertSame(expectedMultimap.comparator(), selectedMultimap.comparator());
    }

    @Override
    @Test
    public void rejectKeysMultiValues()
    {
        MutableSortedBagMultimap<Integer, Integer> multimap = this.newMultimap(Comparators.reverseNaturalOrder());
        multimap.putAll(1, FastList.newListWith(4, 3, 2, 1, 1));
        multimap.putAll(2, FastList.newListWith(5, 4, 3, 2, 2));
        multimap.putAll(3, FastList.newListWith(4, 3, 1, 1));
        multimap.putAll(4, FastList.newListWith(4, 3, 1));
        MutableSortedBagMultimap<Integer, Integer> selectedMultimap = multimap.rejectKeysMultiValues((key, values) -> key % 2 == 0 || Iterate.sizeOf(values) > 4);
        MutableSortedBagMultimap<Integer, Integer> expectedMultimap = this.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap.putAll(3, FastList.newListWith(4, 3, 1, 1));
        Verify.assertSortedBagMultimapsEqual(expectedMultimap, selectedMultimap);
        assertSame(expectedMultimap.comparator(), selectedMultimap.comparator());
    }

    @Override
    @Test
    public void collectKeysValues()
    {
        MutableSortedBagMultimap<String, Integer> multimap = this.newMultimap(Comparators.reverseNaturalOrder());
        multimap.putAll("1", FastList.newListWith(4, 3, 2, 1, 1));
        multimap.putAll("2", FastList.newListWith(5, 4, 3, 2, 2));
        MutableBagMultimap<Integer, String> collectedMultimap1 = multimap.collectKeysValues((key, value) -> Tuples.pair(Integer.valueOf(key), value + "Value"));
        MutableBagMultimap<Integer, String> expectedMultimap1 = HashBagMultimap.newMultimap();
        expectedMultimap1.putAll(1, FastList.newListWith("4Value", "3Value", "2Value", "1Value", "1Value"));
        expectedMultimap1.putAll(2, FastList.newListWith("5Value", "4Value", "3Value", "2Value", "2Value"));
        Verify.assertBagMultimapsEqual(expectedMultimap1, collectedMultimap1);

        MutableBagMultimap<Integer, String> collectedMultimap2 = multimap.collectKeysValues((key, value) -> Tuples.pair(1, value + "Value"));
        MutableBagMultimap<Integer, String> expectedMultimap2 = HashBagMultimap.newMultimap();
        expectedMultimap2.putAll(1, FastList.newListWith("4Value", "3Value", "2Value", "1Value", "1Value"));
        expectedMultimap2.putAll(1, FastList.newListWith("5Value", "4Value", "3Value", "2Value", "2Value"));
        Verify.assertBagMultimapsEqual(expectedMultimap2, collectedMultimap2);
    }

    @Override
    @Test
    public void collectKeyMultiValues()
    {
        super.collectKeyMultiValues();

        MutableSortedBagMultimap<String, Integer> multimap = this.newMultimap(Comparators.reverseNaturalOrder());
        multimap.putAll("1", FastList.newListWith(4, 3, 2, 1, 1));
        multimap.putAll("2", FastList.newListWith(5, 4, 3, 2, 2));
        MutableBagMultimap<Integer, String> collectedMultimap1 = multimap.collectKeyMultiValues(Integer::valueOf, value -> value + "Value");
        MutableBagMultimap<Integer, String> expectedMultimap1 = HashBagMultimap.newMultimap();
        expectedMultimap1.putAll(1, FastList.newListWith("4Value", "3Value", "2Value", "1Value", "1Value"));
        expectedMultimap1.putAll(2, FastList.newListWith("5Value", "4Value", "3Value", "2Value", "2Value"));
        Verify.assertBagMultimapsEqual(expectedMultimap1, collectedMultimap1);

        MutableBagMultimap<Integer, String> collectedMultimap2 = multimap.collectKeyMultiValues(key -> 1, value -> value + "Value");
        MutableBagMultimap<Integer, String> expectedMultimap2 = HashBagMultimap.newMultimap();
        expectedMultimap2.putAll(1, FastList.newListWith("4Value", "3Value", "2Value", "1Value", "1Value"));
        expectedMultimap2.putAll(1, FastList.newListWith("5Value", "4Value", "3Value", "2Value", "2Value"));
        Verify.assertBagMultimapsEqual(expectedMultimap2, collectedMultimap2);
    }

    @Override
    @Test
    public void collectValues()
    {
        MutableSortedBagMultimap<String, Integer> multimap = this.newMultimap(Comparators.reverseNaturalOrder());
        multimap.putAll("1", FastList.newListWith(4, 3, 2, 1, 1));
        multimap.putAll("2", FastList.newListWith(5, 4, 3, 2, 2));
        MutableListMultimap<String, String> collectedMultimap = multimap.collectValues(value -> value + "Value");
        MutableListMultimap<String, String> expectedMultimap = FastListMultimap.newMultimap();
        expectedMultimap.putAll("1", FastList.newListWith("4Value", "3Value", "2Value", "1Value", "1Value"));
        expectedMultimap.putAll("2", FastList.newListWith("5Value", "4Value", "3Value", "2Value", "2Value"));
        Verify.assertListMultimapsEqual(expectedMultimap, collectedMultimap);
    }
}
