/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Helper class for testing {@link Multimap}s.
 */
public abstract class AbstractMutableMultimapTestCase extends AbstractMultimapTestCase
{
    @Override
    protected abstract <K, V> MutableMultimap<K, V> newMultimap();

    @Override
    protected abstract <K, V> MutableMultimap<K, V> newMultimapWithKeyValue(
            K key, V value);

    @Override
    protected abstract <K, V> MutableMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2);

    @Override
    protected abstract <K, V> MutableMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3);

    @Override
    protected abstract <K, V> MutableMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4);

    @Test
    public void putAndGrowMultimap()
    {
        MutableMultimap<Integer, Integer> multimap = this.newMultimap();
        multimap.put(1, 1);
        multimap.put(2, 2);
        Verify.assertContainsEntry(1, 1, multimap);
        Verify.assertContainsEntry(2, 2, multimap);
    }

    @Test
    public void addAndGrowMultimap()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimap();
        Pair<Integer, String> pair1 = Tuples.pair(1, "One");
        Pair<Integer, String> pair2 = Tuples.pair(2, "Two");
        Pair<Integer, String> pair3 = Tuples.pair(3, "Three");
        Pair<Integer, String> pair4 = Tuples.pair(4, "Four");
        assertTrue(multimap.add(pair1));
        Verify.assertContainsEntry(1, "One", multimap);
        assertTrue(multimap.add(pair2));
        Verify.assertContainsEntry(2, "Two", multimap);
        assertTrue(multimap.add(pair3));
        Verify.assertContainsEntry(3, "Three", multimap);
        assertTrue(multimap.add(pair4));
        Verify.assertContainsEntry(4, "Four", multimap);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(pair1, pair2, pair3, pair4), multimap.keyValuePairsView().toSet());
    }

    @Test
    public void clear()
    {
        MutableMultimap<Integer, Object> multimap =
                this.newMultimapWithKeysValues(1, "One", 2, "Two", 3, "Three");
        Verify.assertNotEmpty(multimap);
        multimap.clear();
        Verify.assertEmpty(multimap);
    }

    @Test
    public void removeObject()
    {
        MutableMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        multimap.removeAll("Two");
        Verify.assertContainsAllEntries(multimap, "One", 1, "Three", 3);
    }

    @Override
    @Test
    public void forEachKey()
    {
        MutableBag<Integer> collection = Bags.mutable.of();
        Multimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "1", 2, "2", 3, "3");
        multimap.forEachKey(CollectionAddProcedure.on(collection));
        assertEquals(HashBag.newBagWith(1, 2, 3), collection);
    }

    @Test
    public void withKeyMultiValues()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimap();
        multimap = multimap.withKeyMultiValues(1);
        Verify.assertEmpty(multimap);
        multimap = multimap.withKeyMultiValues(1, "A", "B");
        Verify.assertSize(2, multimap);

        MutableMultimap<Integer, String> expected = this.newMultimap();
        expected.put(1, "A");
        expected.put(1, "B");
        assertEquals(expected, multimap);

        multimap.withKeyMultiValues(1, "C");
        Verify.assertSize(3, multimap);

        expected.put(1, "C");
        assertEquals(expected, multimap);

        multimap.withKeyMultiValues(2, "Z", "K", "Y");
        Verify.assertSize(6, multimap);

        expected = expected.withKeyValue(2, "Z").withKeyValue(2, "K").withKeyValue(2, "Y");
        assertEquals(expected, multimap);

        MutableMultimap<Integer, String> fluent = this.newMultimap();
        assertSame(fluent, fluent.withKeyMultiValues(1, "A"));
        assertSame(fluent, fluent.withKeyMultiValues(1));
        assertSame(fluent, fluent.withKeyValue(2, "B"));
    }

    @Test
    public void withKeyMultiValuesNullValueHandling()
    {
        assertThrows(NullPointerException.class, () -> this.newMultimap().withKeyMultiValues(1, null));
    }

    @Test
    public void putAll()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "2");
        Multimap<Integer, String> toAdd = this.newMultimapWithKeysValues(2, "Two", 3, "Three");
        Multimap<Integer, String> toAddImmutable = this.newMultimapWithKeysValues(4, "Four", 5, "Five");
        assertTrue(multimap.putAll(toAdd));
        assertTrue(multimap.putAll(toAddImmutable));
        MutableMultimap<Integer, String> expected = this.newMultimapWithKeysValues(1, "One", 2, "2", 2, "Two", 3, "Three");
        expected.put(4, "Four");
        expected.put(5, "Five");
        assertEquals(expected, multimap);
    }

    @Test
    public void putAllPairs()
    {
        MutableMultimap<Integer, String> multimap1 = this.newMultimapWithKeysValues(1, "One", 2, "2");
        MutableList<Pair<Integer, String>> pairs1 = Lists.mutable.of(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(3, "Three"));
        assertTrue(multimap1.putAllPairs(pairs1));
        MutableMultimap<Integer, String> expected1 = this.newMultimap();
        expected1.put(1, "One");
        expected1.put(1, "One");
        expected1.put(2, "2");
        expected1.put(2, "Two");
        expected1.put(3, "Three");
        assertEquals(expected1, multimap1);

        MutableMultimap<Integer, String> multimap2 = this.newMultimapWithKeysValues(1, "One", 2, "2");
        ImmutableList<Pair<Integer, String>> pairs2 = Lists.immutable.of(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(3, "Three"));
        assertTrue(multimap2.putAllPairs(pairs2));
        MutableMultimap<Integer, String> expected2 = this.newMultimap();
        expected2.put(1, "One");
        expected2.put(1, "One");
        expected2.put(2, "2");
        expected2.put(2, "Two");
        expected2.put(3, "Three");
        assertEquals(expected2, multimap2);

        MutableMultimap<String, Integer> multimap3 = this.newMultimapWithKeysValues("One", 1, "Two", 2);
        MutableSet<Pair<String, Integer>> pairs3 = Sets.mutable.of(Tuples.pair("One", 1), Tuples.pair("Two", 2), Tuples.pair("Three", 3));
        assertTrue(multimap3.putAllPairs(pairs3));
        MutableMultimap<String, Integer> expected3 = this.newMultimap();
        expected3.put("One", 1);
        expected3.put("One", 1);
        expected3.put("Two", 2);
        expected3.put("Two", 2);
        expected3.put("Three", 3);
        assertEquals(expected3, multimap3);

        MutableMultimap<Number, String> multimap4 = this.newMultimap();
        MutableList<Pair<Integer, String>> intPairs4 = Lists.mutable.of(Tuples.pair(1, "Integer1"), Tuples.pair(2, "Integer2"));
        MutableList<Pair<Long, String>> longPairs4 = Lists.mutable.of(Tuples.pair(1L, "Long1"), Tuples.pair(2L, "Long2"));
        multimap4.putAllPairs(intPairs4);
        multimap4.putAllPairs(longPairs4);
        MutableMultimap<Number, String> expected4 = this.newMultimapWithKeysValues(1, "Integer1", 2, "Integer2", 1L, "Long1", 2L, "Long2");
        assertEquals(expected4, multimap4);
    }

    @Test
    public void putAllFromCollection()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "Two");
        assertTrue(multimap.putAll(1, Lists.fixedSize.of("Three", "Four")));
        assertEquals(this.newMultimapWithKeysValues(1, "One", 2, "Two", 1, "Three", 1, "Four"), multimap);
        assertFalse(multimap.putAll(1, UnifiedSet.newSet()));
        assertEquals(this.newMultimapWithKeysValues(1, "One", 2, "Two", 1, "Three", 1, "Four"), multimap);
    }

    @Test
    public void putAllFromIterable()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "Two");
        assertTrue(multimap.putAll(1, Lists.fixedSize.of("Three", "Four").asLazy()));
        assertEquals(this.newMultimapWithKeysValues(1, "One", 2, "Two", 1, "Three", 1, "Four"), multimap);
    }

    @Test
    public void getIfAbsentPutAll()
    {
        MutableMultimap<Integer, Integer> multimap = this.newMultimap();
        assertFalse(multimap.containsKey(1));
        assertEquals(0, multimap.size());

        assertEquals(this.createCollection(), multimap.getIfAbsentPutAll(1, Lists.mutable.with()));
        assertFalse(multimap.containsKey(1));
        assertEquals(0, multimap.size());

        assertEquals(this.createCollection(1), multimap.getIfAbsentPutAll(1, Lists.mutable.with(1)));
        assertThrows(UnsupportedOperationException.class, () -> multimap.getIfAbsentPutAll(1, Lists.mutable.with(1)).add(1));

        multimap.putAll(2, Lists.mutable.with(2, 2));
        multimap.putAll(3, Lists.mutable.with(3, 3, 3));
        assertEquals(this.createCollection(1), multimap.getIfAbsentPutAll(1, Lists.mutable.empty()));
        assertEquals(this.createCollection(2, 2), multimap.getIfAbsentPutAll(2, Lists.mutable.empty()));
        assertEquals(this.createCollection(3, 3, 3), multimap.getIfAbsentPutAll(3, Lists.mutable.empty()));
        assertEquals(this.createCollection(4, 4, 4, 4), multimap.getIfAbsentPutAll(4, Lists.mutable.with(4, 4, 4, 4)));
        assertEquals(4, multimap.sizeDistinct());
        int multimapSize = this.createCollection(1).size() + this.createCollection(2, 2).size() + this.createCollection(3, 3, 3).size() + this.createCollection(4, 4, 4, 4).size();
        assertEquals(multimapSize, multimap.size());

        assertThrows(UnsupportedOperationException.class, () -> multimap.getIfAbsentPutAll(5, Lists.mutable.with(5)).add(5));
    }

    @Test
    public void removeKey()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "1", 2, "Two");

        Verify.assertSetsEqual(UnifiedSet.newSetWith("1"), UnifiedSet.newSet(multimap.removeAll(1)));
        Verify.assertSize(1, multimap);
        assertFalse(multimap.containsKey(1));

        Verify.assertIterableEmpty(multimap.removeAll(42));
        Verify.assertSize(1, multimap);

        Verify.assertSetsEqual(UnifiedSet.newSetWith("Two"), UnifiedSet.newSet(multimap.removeAll(2)));
        Verify.assertEmpty(multimap);
    }

    @Test
    public void containsValue()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "Two");
        assertTrue(multimap.containsValue("Two"));
        assertFalse(multimap.containsValue("Three"));
    }

    @Test
    public void put_createCollection()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "1", 2, "2", 3, "3");
        Verify.assertIterableEmpty(multimap.get(4));
        assertTrue(multimap.put(4, "4"));
        Verify.assertContainsEntry(4, "4", multimap);
    }

    @Test
    public void remove()
    {
        MutableMultimap<Integer, Integer> map = this.newMultimapWithKeysValues(1, 1, 1, 2, 3, 3, 4, 5);
        assertFalse(map.remove(4, 4));
        assertEquals(this.newMultimapWithKeysValues(1, 1, 1, 2, 3, 3, 4, 5), map);
        assertTrue(map.remove(4, 5));
        assertEquals(this.newMultimapWithKeysValues(1, 1, 1, 2, 3, 3), map);
        assertTrue(map.remove(1, 2));
        assertEquals(this.newMultimapWithKeysValues(1, 1, 3, 3), map);
    }

    @Test
    public void replaceValues()
    {
        MutableMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        RichIterable<Integer> oldValues2 = multimap.replaceValues("Two", UnifiedSet.newSetWith(4));
        assertEquals(Bags.mutable.of(2), oldValues2.toBag());
        Verify.assertEqualsAndHashCode(this.newMultimapWithKeysValues("One", 1, "Two", 4, "Three", 3), multimap);

        RichIterable<Integer> oldValues3 = multimap.replaceValues("Three", UnifiedSet.newSet());
        assertEquals(Bags.mutable.of(3), oldValues3.toBag());
        Verify.assertEqualsAndHashCode(this.newMultimapWithKeysValues("One", 1, "Two", 4), multimap);
    }

    @Test
    public void replaceValues_absent_key()
    {
        MutableMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        RichIterable<Integer> oldValues = multimap.replaceValues("Four", UnifiedSet.newSetWith(4));
        assertEquals(HashBag.<Integer>newBag(), oldValues.toBag());
        Verify.assertEqualsAndHashCode(this.newMultimapWithKeysValues("One", 1, "Two", 2, "Three", 3, "Four", 4), multimap);
    }

    @Test
    public void toMap()
    {
        MutableMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        MutableMap<String, RichIterable<Integer>> expected = UnifiedMap.newMap();
        expected.put("One", this.createCollection(1));
        expected.put("Two", this.createCollection(2, 2));
        MutableMap<String, RichIterable<Integer>> toMap = multimap.toMap();
        assertEquals(expected, toMap);
        MutableMap<String, RichIterable<Integer>> newToMap = multimap.toMap();
        assertEquals(toMap.get("One"), newToMap.get("One"));
        assertNotSame(toMap.get("One"), newToMap.get("One"));
    }

    @Test
    public void toImmutable()
    {
        MutableMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        ImmutableMultimap<String, Integer> actual = multimap.toImmutable();
        assertNotNull(actual);
        assertEquals(multimap, actual);
    }

    @Test
    public void toMapWithTarget()
    {
        MutableMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        MutableMap<String, RichIterable<Integer>> expected = UnifiedMap.newMap();
        expected.put("One", UnifiedSet.newSetWith(1));
        expected.put("Two", UnifiedSet.newSetWith(2, 2));
        MutableMap<String, MutableSet<Integer>> map = multimap.toMap(UnifiedSet::new);
        assertEquals(expected, map);
    }

    @Test
    public void toMutable()
    {
        MutableMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        MutableMultimap<String, Integer> mutableCopy = multimap.toMutable();
        assertNotSame(multimap, mutableCopy);
        assertEquals(multimap, mutableCopy);
    }

    @Test
    public void testToString()
    {
        MutableMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2);
        assertTrue(
                "{One=[1], Two=[2]}".equals(multimap.toString())
                        || "{Two=[2], One=[1]}".equals(multimap.toString()));
    }
}
