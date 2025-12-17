/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.strategy.mutable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMapTestCase;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.math.Sum;
import org.eclipse.collections.impl.math.SumProcedure;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Person;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class UnifiedMapWithHashingStrategyTest extends UnifiedMapTestCase
{
    //Not using the static factor method in order to have concrete types for test cases
    private static final HashingStrategy<Integer> INTEGER_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
    {
        @Override
        public int computeHashCode(Integer object)
        {
            return object.hashCode();
        }

        @Override
        public boolean equals(Integer object1, Integer object2)
        {
            return object1.equals(object2);
        }
    });
    private static final HashingStrategy<String> STRING_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<String>()
    {
        @Override
        public int computeHashCode(String object)
        {
            return object.hashCode();
        }

        @Override
        public boolean equals(String object1, String object2)
        {
            return object1.equals(object2);
        }
    });

    private static final HashingStrategy<Person> FIRST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_FIRST);
    private static final HashingStrategy<Person> LAST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);

    private static final Person JOHNSMITH = new Person("John", "Smith");
    private static final Person JANESMITH = new Person("Jane", "Smith");
    private static final Person JOHNDOE = new Person("John", "Doe");
    private static final Person JANEDOE = new Person("Jane", "Doe");

    @Override
    public <K, V> MutableMap<K, V> newMap()
    {
        return UnifiedMapWithHashingStrategy.newMap(
                HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()));
    }

    @Override
    public <K, V> MutableMap<K, V> newMapWithKeyValue(K key, V value)
    {
        return UnifiedMapWithHashingStrategy.newWithKeysValues(
                HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key, value);
    }

    @Override
    public <K, V> MutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return UnifiedMapWithHashingStrategy.newWithKeysValues(
                HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1, key2, value2);
    }

    @Override
    public <K, V> MutableMap<K, V> newMapWithKeysValues(
            K key1, V value1, K key2, V value2, K key3,
            V value3)
    {
        return UnifiedMapWithHashingStrategy.newWithKeysValues(
                HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1, key2, value2, key3, value3);
    }

    @Override
    public <K, V> MutableMap<K, V> newMapWithKeysValues(
            K key1, V value1, K key2, V value2, K key3,
            V value3, K key4, V value4)
    {
        return UnifiedMapWithHashingStrategy.newWithKeysValues(
                HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1, key2, value2, key3, value3, key4, value4);
    }

    @Test
    public void constructorOfPairs()
    {
        assertEquals(
                UnifiedMapWithHashingStrategy.newWithKeysValues(INTEGER_HASHING_STRATEGY, 1, "one", 2, "two", 3, "three"),
                UnifiedMapWithHashingStrategy.newMapWith(INTEGER_HASHING_STRATEGY, Tuples.pair(1, "one"), Tuples.pair(2, "two"), Tuples.pair(3, "three")));
    }

    @Test
    public void constructorOfIterableOfPairs()
    {
        Pair<Integer, String> pair1 = Tuples.pair(1, "One");
        Pair<Integer, String> pair2 = Tuples.pair(2, "Two");
        Pair<Integer, String> pair3 = Tuples.pair(3, "Three");
        Pair<Integer, String> pair4 = Tuples.pair(4, "Four");
        UnifiedMapWithHashingStrategy<Integer, String> expected = UnifiedMapWithHashingStrategy.newMapWith(INTEGER_HASHING_STRATEGY, pair1, pair2, pair3, pair4);
        UnifiedMapWithHashingStrategy<Integer, String> actual1 = UnifiedMapWithHashingStrategy.newMapWith(INTEGER_HASHING_STRATEGY, FastList.newListWith(pair1, pair2, pair3, pair4));
        assertEquals(expected, actual1);
        assertEquals(expected.hashingStrategy(), actual1.hashingStrategy());

        UnifiedMapWithHashingStrategy<Integer, String> actual2 = UnifiedMapWithHashingStrategy.newMapWith(INTEGER_HASHING_STRATEGY, UnifiedSet.newSetWith(pair1, pair2, pair3, pair4));
        assertEquals(expected, actual2);
        assertEquals(expected.hashingStrategy(), actual2.hashingStrategy());
    }

    @Test
    public void newMap_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> new UnifiedMapWithHashingStrategy<Integer, Integer>(INTEGER_HASHING_STRATEGY, -1, 0.5f));
        assertThrows(IllegalArgumentException.class, () -> new UnifiedMapWithHashingStrategy<Integer, Integer>(INTEGER_HASHING_STRATEGY, 1, -0.5f));
        assertThrows(IllegalArgumentException.class, () -> new UnifiedMapWithHashingStrategy<Integer, Integer>(INTEGER_HASHING_STRATEGY, 1, 1.5f));
    }

    @Override
    @Test
    public void selectMap()
    {
        super.selectMap();

        MutableMap<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JANEDOE, 2, JOHNSMITH, 3, JANESMITH, 4);
        assertEquals(UnifiedMap.newWithKeysValues(JOHNDOE, 2), map.select((argument1, argument2) -> "Doe".equals(argument1.getLastName())));
    }

    @Override
    @Test
    public void rejectMap()
    {
        super.rejectMap();

        MutableMap<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JANEDOE, 2, JOHNSMITH, 3, JANESMITH, 4);
        assertEquals(UnifiedMap.newWithKeysValues(JOHNDOE, 2), map.reject((argument1, argument2) -> "Smith".equals(argument1.getLastName())));
    }

    @Override
    @Test
    public void collectMap()
    {
        super.collectMap();

        MutableMap<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JOHNSMITH, 2, JANEDOE, 3, JANESMITH, 4);
        Function2<Person, Integer, Pair<Integer, Person>> function = (Person argument1, Integer argument2) -> Tuples.pair(argument2, argument1);
        MutableMap<Integer, Person> collect = map.collect(function);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(3, 4), collect.keySet());
        Verify.assertContainsAll(collect.values(), JOHNDOE, JOHNSMITH);
    }

    @Test
    public void contains_with_hashing_strategy()
    {
        MutableMap<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JANEDOE, 2, JOHNSMITH, 3, JANESMITH, 4);
        assertTrue(map.containsKey(JOHNDOE));
        assertTrue(map.containsValue(2));
        assertTrue(map.containsKey(JOHNSMITH));
        assertTrue(map.containsValue(4));
        assertTrue(map.containsKey(JANEDOE));
        assertTrue(map.containsKey(JANESMITH));

        assertFalse(map.containsValue(1));
        assertFalse(map.containsValue(3));
    }

    @Test
    public void remove_with_hashing_strategy()
    {
        MutableMap<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JANEDOE, 2, JOHNSMITH, 3, JANESMITH, 4);

        //Testing removing people
        assertEquals(2, map.remove(JANEDOE).intValue());
        assertEquals(4, map.remove(JOHNSMITH).intValue());

        Verify.assertEmpty(map);

        //Testing removing from a chain
        MutableMap<Integer, Integer> map2 =
                UnifiedMapWithHashingStrategy.newWithKeysValues(INTEGER_HASHING_STRATEGY, COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, COLLISION_4, 4);
        assertEquals(4, map2.remove(COLLISION_4).intValue());
        assertEquals(1, map2.remove(COLLISION_1).intValue());
        Verify.assertSize(2, map2);

        //Testing removing null from a chain
        MutableMap<Integer, Integer> map3 =
                UnifiedMapWithHashingStrategy.newWithKeysValues(INTEGER_HASHING_STRATEGY, COLLISION_1, 1, null, 2, 3, 3, 4, null);
        assertEquals(2, map3.remove(null).intValue());
        Verify.assertSize(3, map3);
        assertNull(map3.remove(4));
        Verify.assertSize(2, map3);
    }

    @Test
    public void keySet_isEmpty()
    {
        Set<Integer> keySet = UnifiedMapWithHashingStrategy.newWithKeysValues(INTEGER_HASHING_STRATEGY, 1, 1, 2, 2).keySet();
        assertFalse(keySet.isEmpty());
        keySet.clear();
        Verify.assertEmpty(keySet);
    }

    @Test
    public void keySet_with_hashing_strategy()
    {
        Set<Person> people = UnifiedMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JANEDOE, 2, JOHNSMITH, 3, JANESMITH, 4).keySet();

        Verify.assertSize(2, people);

        Verify.assertContains(JANEDOE, people);
        Verify.assertContains(JOHNDOE, people);
        Verify.assertContains(JANESMITH, people);
        Verify.assertContains(JOHNSMITH, people);
    }

    @Test
    public void keySet_Iterator_removeFromNonChain()
    {
        Set<Integer> keys = UnifiedMapWithHashingStrategy.newWithKeysValues(INTEGER_HASHING_STRATEGY, 1, 1, 2, 2, 3, 3, 4, 4).keySet();
        Iterator<Integer> keysIterator = keys.iterator();
        keysIterator.next();
        keysIterator.remove();
        Verify.assertSetsEqual(UnifiedSet.newSetWith(2, 3, 4), keys);
    }

    @Test
    public void weakEntryToString()
    {
        Iterator<Map.Entry<Integer, Integer>> iterator = UnifiedMapWithHashingStrategy.newWithKeysValues(INTEGER_HASHING_STRATEGY, 1, 1).entrySet().iterator();
        Map.Entry<Integer, Integer> element = iterator.next();
        assertEquals("1=1", element.toString());
    }

    @Override
    @Test
    public void valuesCollection_Iterator_remove()
    {
        // a map with a chain, remove one
        MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(3);
        Iterator<Integer> iterator = map.iterator();
        iterator.next();
        iterator.remove();
        Verify.assertSize(2, map);

        // remove all values in chain
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.remove();
        Verify.assertEmpty(map);
    }

    @Test
    public void entry_equals_with_hashingStrategy()
    {
        MutableMap<Integer, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(
                INTEGER_HASHING_STRATEGY, 1, 1, 2, 2, 3, 3, 4, 4);
        Iterator<Map.Entry<Integer, Integer>> entryIterator = map.entrySet().iterator();
        Map.Entry<Integer, Integer> entry = entryIterator.next();
        ImmutableEntry<Integer, Integer> immutableEntry = ImmutableEntry.of(entry.getKey(), entry.getValue());
        Verify.assertEqualsAndHashCode(immutableEntry, entry);
    }

    @Test
    public void entrySet_with_hashing_strategy()
    {
        Set<Map.Entry<Person, Integer>> entries = UnifiedMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JANEDOE, 2, JOHNSMITH, 3, JANESMITH, 4).entrySet();

        Verify.assertSize(2, entries);

        Verify.assertContains(ImmutableEntry.of(JOHNDOE, 2), entries);
        Verify.assertContains(ImmutableEntry.of(JANEDOE, 2), entries);
        Verify.assertContains(ImmutableEntry.of(JOHNSMITH, 4), entries);
        Verify.assertContains(ImmutableEntry.of(JANESMITH, 4), entries);
        Verify.assertNotContains(ImmutableEntry.of(JOHNDOE, 1), entries);
        Verify.assertNotContains(ImmutableEntry.of(JANESMITH, 3), entries);

        assertTrue(entries.remove(ImmutableEntry.of(JANESMITH, 4)));
        Verify.assertNotContains(ImmutableEntry.of(JOHNSMITH, 4), entries);

        assertTrue(entries.remove(ImmutableEntry.of(JOHNDOE, 2)));
        Verify.assertEmpty(entries);
    }

    @Test
    public void valuesCollection_containsAll()
    {
        Collection<Integer> values = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3, 4, 4).values();
        assertTrue(values.containsAll(FastList.newListWith(1, 2)));
        assertTrue(values.containsAll(FastList.newListWith(1, 2, 3, 4)));
        assertFalse(values.containsAll(FastList.newListWith(1, 2, 3, 4, 5)));
    }

    @Test
    public void batchForEach()
    {
        UnifiedMapWithHashingStrategy<String, Integer> map = UnifiedMapWithHashingStrategy.<String, Integer>newMap(
                STRING_HASHING_STRATEGY, 5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        this.batchForEachTestCases(map, 10);

        UnifiedMapWithHashingStrategy<Integer, Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 5).withKeysValues(COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6);
        this.batchForEachChains(collisions, 21);

        UnifiedMapWithHashingStrategy<Integer, Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null);
        this.batchForEachNullHandling(nulls, 36);

        this.batchForEachEmptyBatchIterable(UnifiedMapWithHashingStrategy.newMap(INTEGER_HASHING_STRATEGY));
    }

    @Test
    public void batchForEachKey()
    {
        Set<Integer> keys = UnifiedMapWithHashingStrategy.<Integer, String>newMap(
                INTEGER_HASHING_STRATEGY, 5).withKeysValues(1, "1", 2, "2", 3, "3", 4, "4").keySet();
        this.batchForEachTestCases((BatchIterable<Integer>) keys, 10);

        Set<Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 5).withKeysValues(COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).keySet();
        this.batchForEachChains((BatchIterable<Integer>) collisions, 57);

        Set<Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).keySet();
        this.batchForEachNullHandling((BatchIterable<Integer>) nulls, 16);

        this.batchForEachEmptyBatchIterable((BatchIterable<Integer>) UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(INTEGER_HASHING_STRATEGY).keySet());
    }

    @Test
    public void batchForEachValue()
    {
        Collection<Integer> values = UnifiedMapWithHashingStrategy.<String, Integer>newMap(
                STRING_HASHING_STRATEGY, 5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4).values();
        this.batchForEachTestCases((BatchIterable<Integer>) values, 10);

        Collection<Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 5).withKeysValues(COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).values();
        this.batchForEachChains((BatchIterable<Integer>) collisions, 21);

        Collection<Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).values();
        this.batchForEachNullHandling((BatchIterable<Integer>) nulls, 36);

        this.batchForEachEmptyBatchIterable(
                (BatchIterable<Integer>) UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(INTEGER_HASHING_STRATEGY).values());
    }

    @Test
    public void batchForEachEntry()
    {
        //Testing batch size of 1 to 16 with no chains
        BatchIterable<Map.Entry<Integer, Integer>> entries =
                (BatchIterable<Map.Entry<Integer, Integer>>) UnifiedMapWithHashingStrategy.newWithKeysValues(
                        INTEGER_HASHING_STRATEGY, 1, 1, 2, 2, 3, 3, 4, 4).entrySet();
        for (int sectionCount = 1; sectionCount <= 16; ++sectionCount)
        {
            Sum sum = new IntegerSum(0);
            for (int sectionIndex = 0; sectionIndex < sectionCount; ++sectionIndex)
            {
                entries.batchForEach(new EntrySumProcedure(sum), sectionIndex, sectionCount);
            }
            assertEquals(20, sum.getValue());
        }
    }

    @Test
    public void batchForEachEntry_chains()
    {
        BatchIterable<Map.Entry<Integer, Integer>> collisions =
                (BatchIterable<Map.Entry<Integer, Integer>>) UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                        INTEGER_HASHING_STRATEGY, 5).withKeysValues(
                        COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).entrySet();
        //Testing 1 batch with chains
        Sum sum2 = new IntegerSum(0);
        //testing getBatchCount returns 1
        int batchCount = collisions.getBatchCount(100000);
        for (int i = 0; i < batchCount; ++i)
        {
            collisions.batchForEach(new EntrySumProcedure(sum2), i, batchCount);
        }
        assertEquals(1, batchCount);
        assertEquals(78, sum2.getValue());

        //Testing 3 batches with chains and uneven last batch
        Sum sum3 = new IntegerSum(0);
        for (int i = 0; i < 5; ++i)
        {
            collisions.batchForEach(new EntrySumProcedure(sum3), i, 5);
        }
        assertEquals(78, sum3.getValue());
    }

    @Test
    public void batchForEachEntry_null_handling()
    {
        //Testing batchForEach handling null keys and null values
        Sum sum4 = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> nulls =
                (BatchIterable<Map.Entry<Integer, Integer>>) UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                        INTEGER_HASHING_STRATEGY, 100).withKeysValues(
                        null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).entrySet();
        int numBatches = nulls.getBatchCount(7);
        for (int i = 0; i < numBatches; ++i)
        {
            nulls.batchForEach(each ->
            {
                sum4.add(each.getKey() == null ? 1 : each.getKey());
                sum4.add(each.getValue() == null ? 1 : each.getValue());
            }, i, numBatches);
        }
        assertEquals(52, sum4.getValue());
    }

    @Test
    public void batchForEachEntry_emptySet()
    {
        //Test batchForEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum5 = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> empty =
                (BatchIterable<Map.Entry<Integer, Integer>>) UnifiedMapWithHashingStrategy.newMap(INTEGER_HASHING_STRATEGY).entrySet();
        empty.batchForEach(new EntrySumProcedure(sum5), 0, empty.getBatchCount(1));
        assertEquals(0, sum5.getValue());
    }

    private void batchForEachTestCases(BatchIterable<Integer> batchIterable, int expectedValue)
    {
        //Testing batch size of 1 to 16 with no chains
        for (int sectionCount = 1; sectionCount <= 16; ++sectionCount)
        {
            Sum sum = new IntegerSum(0);
            for (int sectionIndex = 0; sectionIndex < sectionCount; ++sectionIndex)
            {
                batchIterable.batchForEach(new SumProcedure<>(sum), sectionIndex, sectionCount);
            }
            assertEquals(expectedValue, sum.getValue());
        }
    }

    private void batchForEachChains(BatchIterable<Integer> batchIterable, int expectedValue)
    {
        //Testing 1 batch with chains
        Sum sum = new IntegerSum(0);
        //testing getBatchCount returns 1
        int numBatches = batchIterable.getBatchCount(100000);
        for (int i = 0; i < numBatches; ++i)
        {
            batchIterable.batchForEach(new SumProcedure<>(sum), i, numBatches);
        }
        assertEquals(1, numBatches);
        assertEquals(expectedValue, sum.getValue());

        //Testing 3 batches with chains and uneven last batch
        Sum sum2 = new IntegerSum(0);
        for (int i = 0; i < 5; ++i)
        {
            batchIterable.batchForEach(new SumProcedure<>(sum2), i, 5);
        }
        assertEquals(expectedValue, sum2.getValue());
    }

    private void batchForEachNullHandling(BatchIterable<Integer> batchIterable, int expectedValue)
    {
        //Testing batchForEach handling null keys and null values
        Sum sum = new IntegerSum(0);

        for (int i = 0; i < batchIterable.getBatchCount(7); ++i)
        {
            batchIterable.batchForEach(each -> sum.add(each == null ? 1 : each), i, batchIterable.getBatchCount(7));
        }
        assertEquals(expectedValue, sum.getValue());
    }

    private void batchForEachEmptyBatchIterable(BatchIterable<Integer> batchIterable)
    {
        //Test batchForEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum = new IntegerSum(0);
        batchIterable.batchForEach(new SumProcedure<>(sum), 0, batchIterable.getBatchCount(1));
        assertEquals(0, sum.getValue());
    }

    @Test
    public void batchIterable_forEach()
    {
        UnifiedMapWithHashingStrategy<String, Integer> map = UnifiedMapWithHashingStrategy.<String, Integer>newMap(
                STRING_HASHING_STRATEGY, 5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        this.batchIterable_forEach(map, 10);

        UnifiedMapWithHashingStrategy<Integer, Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 5).withKeysValues(COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6);
        this.batchIterable_forEach(collisions, 21);

        UnifiedMapWithHashingStrategy<Integer, Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null);
        this.batchIterable_forEachNullHandling(nulls, 33);

        this.batchIterable_forEachEmptyBatchIterable(UnifiedMapWithHashingStrategy.newMap(INTEGER_HASHING_STRATEGY));
    }

    @Test
    public void batchIterable_forEachKey()
    {
        Set<Integer> keys = UnifiedMapWithHashingStrategy.<Integer, String>newMap(
                INTEGER_HASHING_STRATEGY, 5).withKeysValues(1, "1", 2, "2", 3, "3", 4, "4").keySet();
        this.batchIterable_forEach((BatchIterable<Integer>) keys, 10);

        Set<Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 5).withKeysValues(COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).keySet();
        this.batchIterable_forEach((BatchIterable<Integer>) collisions, 57);

        Set<Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).keySet();
        this.batchIterable_forEachNullHandling((BatchIterable<Integer>) nulls, 15);

        this.batchIterable_forEachEmptyBatchIterable((BatchIterable<Integer>) UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(INTEGER_HASHING_STRATEGY).keySet());
    }

    @Test
    public void batchIterable_forEachValue()
    {
        Collection<Integer> values = UnifiedMapWithHashingStrategy.<String, Integer>newMap(
                STRING_HASHING_STRATEGY, 5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4).values();
        this.batchIterable_forEach((BatchIterable<Integer>) values, 10);

        Collection<Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 5).withKeysValues(COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).values();
        this.batchIterable_forEach((BatchIterable<Integer>) collisions, 21);

        Collection<Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).values();
        this.batchIterable_forEachNullHandling((BatchIterable<Integer>) nulls, 33);

        this.batchIterable_forEachEmptyBatchIterable(
                (BatchIterable<Integer>) UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(INTEGER_HASHING_STRATEGY).values());
    }

    @Test
    public void batchIterable_forEachEntry()
    {
        BatchIterable<Map.Entry<Integer, Integer>> entries =
                (BatchIterable<Map.Entry<Integer, Integer>>) UnifiedMapWithHashingStrategy.newWithKeysValues(
                        INTEGER_HASHING_STRATEGY, 1, 1, 2, 2, 3, 3, 4, 4).entrySet();
        Sum sum = new IntegerSum(0);
        entries.forEach(new EntrySumProcedure(sum));
        assertEquals(20, sum.getValue());
    }

    @Test
    public void batchIterable_forEachEntry_chains()
    {
        BatchIterable<Map.Entry<Integer, Integer>> collisions =
                (BatchIterable<Map.Entry<Integer, Integer>>) UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                        INTEGER_HASHING_STRATEGY, 5).withKeysValues(
                        COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).entrySet();

        Sum sum = new IntegerSum(0);
        collisions.forEach(new EntrySumProcedure(sum));
        assertEquals(78, sum.getValue());
    }

    @Test
    public void batchIterable_forEachEntry_null_handling()
    {
        //Testing batchForEach handling null keys and null values
        Sum sum = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> nulls =
                (BatchIterable<Map.Entry<Integer, Integer>>) UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                        INTEGER_HASHING_STRATEGY, 100).withKeysValues(
                        null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).entrySet();

        nulls.forEach(each ->
        {
            sum.add(each.getKey() == null ? 0 : each.getKey());
            sum.add(each.getValue() == null ? 0 : each.getValue());
        });

        assertEquals(48, sum.getValue());
    }

    @Test
    public void batchIterable_forEachEntry_emptySet()
    {
        //Test forEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> empty =
                (BatchIterable<Map.Entry<Integer, Integer>>) UnifiedMapWithHashingStrategy.newMap(INTEGER_HASHING_STRATEGY).entrySet();
        empty.forEach(new EntrySumProcedure(sum));
        assertEquals(0, sum.getValue());
    }

    private void batchIterable_forEach(BatchIterable<Integer> batchIterable, int expectedValue)
    {
        IntegerSum sum = new IntegerSum(0);
        batchIterable.forEach(new SumProcedure<>(sum));
        assertEquals(expectedValue, sum.getValue());
    }

    private void batchIterable_forEachNullHandling(BatchIterable<Integer> batchIterable, int expectedValue)
    {
        //Testing forEach handling null keys and null values
        Sum sum = new IntegerSum(0);
        batchIterable.forEach(each -> sum.add(each == null ? 0 : each));
        assertEquals(expectedValue, sum.getValue());
    }

    private void batchIterable_forEachEmptyBatchIterable(BatchIterable<Integer> batchIterable)
    {
        //Test forEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum = new IntegerSum(0);
        batchIterable.batchForEach(new SumProcedure<>(sum), 0, batchIterable.getBatchCount(1));
        assertEquals(0, sum.getValue());
    }

    @Override
    @Test
    public void forEachKeyValue()
    {
        super.forEachKeyValue();

        // Testing full chain
        MutableSet<Integer> keys = UnifiedSet.newSet();
        MutableSet<Integer> values = UnifiedSet.newSet();
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(
                INTEGER_HASHING_STRATEGY, COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, COLLISION_4, 4).withKeysValues(1, 5);

        map.forEachKeyValue((argument1, argument2) ->
        {
            keys.add(argument1);
            values.add(argument2);
        });
        Verify.assertSetsEqual(UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, 1), keys);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3, 4, 5), values);

        // Testing when chain contains null
        MutableSet<Integer> keys2 = UnifiedSet.newSet();
        MutableSet<Integer> values2 = UnifiedSet.newSet();
        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(
                INTEGER_HASHING_STRATEGY, COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, 1, 4);

        map2.forEachKeyValue((argument1, argument2) ->
        {
            keys2.add(argument1);
            values2.add(argument2);
        });
        Verify.assertSetsEqual(UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_3, 1), keys2);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3, 4), values2);
    }

    @Test
    public void getMapMemoryUsedInWords()
    {
        UnifiedMapWithHashingStrategy<String, String> map = UnifiedMapWithHashingStrategy.newMap(STRING_HASHING_STRATEGY);
        assertEquals(34, map.getMapMemoryUsedInWords());
        map.put("1", "1");
        assertEquals(34, map.getMapMemoryUsedInWords());

        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = this.mapWithCollisionsOfSize(2);
        assertEquals(16, map2.getMapMemoryUsedInWords());
    }

    @Test
    public void getHashingStrategy()
    {
        UnifiedMapWithHashingStrategy<Integer, Object> map = UnifiedMapWithHashingStrategy.newMap(INTEGER_HASHING_STRATEGY);
        assertSame(INTEGER_HASHING_STRATEGY, map.hashingStrategy());
    }

    @Test
    public void getCollidingBuckets()
    {
        UnifiedMapWithHashingStrategy<Object, Object> map = UnifiedMapWithHashingStrategy.newMap(HashingStrategies.defaultStrategy());
        assertEquals(0, map.getCollidingBuckets());

        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = this.mapWithCollisionsOfSize(2);
        assertEquals(1, map2.getCollidingBuckets());

        map2.put(42, 42);
        assertEquals(1, map2.getCollidingBuckets());

        UnifiedMapWithHashingStrategy<String, String> map3 = UnifiedMapWithHashingStrategy.newWithKeysValues(
                STRING_HASHING_STRATEGY, "Six", "6", "Bar", "-", "Three", "3", "Five", "5");
        assertEquals(2, map3.getCollidingBuckets());
    }

    @Override
    @Test
    public void getIfAbsentPut()
    {
        super.getIfAbsentPut();

        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        MutableMap<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(
                INTEGER_HASHING_STRATEGY, 2, 0.75f);
        MORE_COLLISIONS.forEach(Procedures.cast(each -> map.getIfAbsentPut(each, new PassThruFunction0<>(each))));

        assertEquals(this.mapWithCollisionsOfSize(9), map);

        //Testing getting element present in chain
        MutableMap<Integer, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(
                INTEGER_HASHING_STRATEGY, COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, COLLISION_4, 4);
        assertEquals(2, map2.getIfAbsentPut(COLLISION_2, () ->
        {
            fail();
            return null;
        }).intValue());

        //Testing rehashing while creating a new chained key
        UnifiedMapWithHashingStrategy<Integer, Integer> map3 = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(
                INTEGER_HASHING_STRATEGY, 2, 0.75f).withKeysValues(COLLISION_1, 1, 2, 2, 3, 3);
        assertEquals(4, map3.getIfAbsentPut(COLLISION_2, new PassThruFunction0<>(4)).intValue());
    }

    @Override
    @Test
    public void getIfAbsentPutValue()
    {
        super.getIfAbsentPutValue();

        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        MutableMap<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(
                INTEGER_HASHING_STRATEGY, 2, 0.75f);
        MORE_COLLISIONS.forEach(Procedures.cast(each -> map.getIfAbsentPut(each, each)));

        assertEquals(this.mapWithCollisionsOfSize(9), map);

        //Testing getting element present in chain
        MutableMap<Integer, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(
                INTEGER_HASHING_STRATEGY, COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, COLLISION_4, 4);
        assertEquals(Integer.valueOf(2), map2.getIfAbsentPut(COLLISION_2, Integer.valueOf(5)));
        assertEquals(Integer.valueOf(5), map2.getIfAbsentPut(5, Integer.valueOf(5)));
    }

    @Override
    @Test
    public void getIfAbsentPutWith()
    {
        super.getIfAbsentPutWith();

        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        MutableMap<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(
                INTEGER_HASHING_STRATEGY, 2, 0.75f);
        MORE_COLLISIONS.forEach(Procedures.cast(each -> map.getIfAbsentPutWith(each, Functions.getIntegerPassThru(), each)));

        assertEquals(this.mapWithCollisionsOfSize(9), map);

        //Testing getting element present in chain
        MutableMap<Integer, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(
                INTEGER_HASHING_STRATEGY, COLLISION_1, 1, COLLISION_2, 2, COLLISION_3, 3, COLLISION_4, 4);
        assertEquals(Integer.valueOf(2), map2.getIfAbsentPutWith(COLLISION_2, Functions.getIntegerPassThru(), Integer.valueOf(5)));
        assertEquals(Integer.valueOf(5), map2.getIfAbsentPutWith(5, Functions.getIntegerPassThru(), Integer.valueOf(5)));
    }

    @Test
    public void equals_with_hashing_strategy()
    {
        MutableMap<Person, Integer> map1 = UnifiedMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JANEDOE, 1, JOHNSMITH, 1, JANESMITH, 1);
        MutableMap<Person, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(
                FIRST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JANEDOE, 1, JOHNSMITH, 1, JANESMITH, 1);

        assertEquals(map1, map2);
        assertEquals(map2, map1);
        assertNotEquals(map1.hashCode(), map2.hashCode());

        UnifiedMapWithHashingStrategy<Person, Integer> map3 = UnifiedMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, 1, JANEDOE, 2, JOHNSMITH, 3, JANESMITH, 4);
        MutableMap<Person, Integer> map4 = UnifiedMapWithHashingStrategy.newMap(map3);
        Map<Person, Integer> hashMap = new HashMap<>(map3);

        Verify.assertEqualsAndHashCode(map3, map4);
        assertTrue(map3.equals(hashMap) && hashMap.equals(map3) && map3.hashCode() != hashMap.hashCode());

        MutableMap<Person, Integer> unifiedMap = UnifiedMap.newWithKeysValues(JOHNDOE, 1, JANEDOE, 1, JOHNSMITH, 1, JANESMITH, 1);
        MutableMap<Person, Integer> map5 = UnifiedMapWithHashingStrategy.newMap(LAST_NAME_HASHING_STRATEGY, unifiedMap);
        assertNotEquals(map5, unifiedMap);
    }

    @Test
    public void put()
    {
        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        MutableMap<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(
                INTEGER_HASHING_STRATEGY, 2, 0.75f);
        COLLISIONS.forEach(0, 4, each -> assertNull(map.put(each, each)));

        assertEquals(this.mapWithCollisionsOfSize(5), map);
    }

    @Test
    public void put_get_with_hashing_strategy()
    {
        MutableMap<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(INTEGER_HASHING_STRATEGY);

        //Testing putting values in non chains
        assertNull(map.put(1, 1));
        assertNull(map.put(2, 2));
        assertNull(map.put(3, 3));
        assertNull(map.put(4, 4));
        assertNull(map.put(5, null));

        //Testing getting values from no chains
        assertEquals(1, map.get(1).intValue());
        assertEquals(2, map.get(2).intValue());
        assertNull(map.get(5));

        //Testing putting and getting elements in a chain
        assertNull(map.put(COLLISION_1, 1));
        assertNull(map.get(COLLISION_2));

        assertNull(map.put(COLLISION_2, 2));
        assertNull(map.get(COLLISION_3));

        assertNull(map.put(COLLISION_3, null));
        assertNull(map.put(COLLISION_4, 4));
        assertNull(map.put(COLLISION_5, 5));

        assertEquals(1, map.get(COLLISION_1).intValue());
        assertEquals(5, map.get(COLLISION_5).intValue());
        assertNull(map.get(COLLISION_3));

        map.remove(COLLISION_2);
        assertNull(map.get(COLLISION_2));

        //Testing for casting exceptions
        HashingStrategy<Person> lastName = new HashingStrategy<Person>()
        {
            @Override
            public int computeHashCode(Person object)
            {
                return object.getLastName().hashCode();
            }

            @Override
            public boolean equals(Person object1, Person object2)
            {
                return object1.equals(object2);
            }
        };

        MutableMap<Person, Integer> map2 = UnifiedMapWithHashingStrategy.newMap(lastName);
        assertNull(map2.put(new Person("abe", "smith"), 1));
        assertNull(map2.put(new Person("brad", "smith"), 2));
        assertNull(map2.put(new Person("charlie", "smith"), 3));
    }

    @Test
    public void hashingStrategy()
    {
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(INTEGER_HASHING_STRATEGY, 1, 1, 2, 2);
        assertSame(INTEGER_HASHING_STRATEGY, map.hashingStrategy());
    }

    @Test
    public void trimToSize()
    {
        UnifiedMapWithHashingStrategy<String, String> map = UnifiedMapWithHashingStrategy.newMap(STRING_HASHING_STRATEGY);
        MutableMap<String, String> expected = Maps.mutable.empty();

        Interval integers = Interval.fromTo(0, 250);
        integers.each(each ->
        {
            map.put(each.toString(), each.toString());
            expected.put(each.toString(), each.toString());
        });
        ArrayIterate.forEach(FREQUENT_COLLISIONS, each ->
        {
            map.put(each, each);
            expected.put(each, each);
        });

        assertEquals(expected, map);
        assertEquals(261, map.size());

        MutableList<Integer> toRemove = Lists.mutable.withAll(Interval.evensFromTo(0, 20));

        toRemove.addAll(Interval.oddsFromTo(35, 55));
        toRemove.each(each ->
        {
            map.remove(each.toString());
            expected.remove(each.toString());
        });

        // First assertion to verify that trim does not happen since, the table is already at the smallest required power of 2.
        assertFalse(map.trimToSize());
        assertEquals(expected, map);
        assertEquals(239, map.size());

        Interval.evensFromTo(0, 250).each(each ->
        {
            map.remove(each.toString());
            expected.remove(each.toString());
        });

        // Second assertion to verify that trim happens since, the table length is less than smallest required power of 2.
        assertTrue(map.trimToSize());
        assertFalse(map.trimToSize());
        assertEquals(expected, map);
        assertEquals(124, map.size());
        expected.forEachKey(each -> assertEquals(each, map.get(each)));

        integers.each(each ->
        {
            map.remove(each.toString());
            expected.remove(each.toString());
        });
        assertTrue(map.trimToSize());
        assertFalse(map.trimToSize());
        assertEquals(expected, map);
        expected.forEachKey(each -> assertEquals(each, map.get(each)));

        map.clear();
        expected.clear();
        assertTrue(map.trimToSize());

        Interval.zeroTo(20).each(each ->
        {
            map.put(each.toString(), each.toString());
            expected.put(each.toString(), each.toString());
        });
        assertFalse(map.trimToSize());
        Interval.fromTo(9, 18).each(each ->
        {
            map.remove(each.toString());
            expected.remove(each.toString());
        });
        assertTrue(map.trimToSize());
        assertFalse(map.trimToSize());
        assertEquals(expected, map);
        expected.forEachKey(each -> assertEquals(each, map.get(each)));

        map.clear();
        assertTrue(map.trimToSize());
        assertTrue(map.isEmpty());
        Interval.zeroTo(6).each(each -> map.put(each.toString(), each.toString()));
        // Assert that trim does not happen as long as table.size is already as smaller than required
        assertFalse(map.trimToSize());
        map.put("7", "7");

        map.removeKey("2");
        map.removeKey("3");
        // Assert that trim does not happen as long as table.size is as small as required
        assertFalse(map.trimToSize());
        map.removeKey("5");
        map.removeKey("7");
        assertTrue(map.trimToSize());
        // Inflate the map so that table.length increases to next power of 2 and check that trim does not happen
        map.put("2", "2");
        map.put("5", "5");
        map.put("7", "7");
        // Assert that the resized table due to put is the required size and no need to trim that.
        assertFalse(map.trimToSize());

        Interval.zeroTo(4).each(each -> map.put(each.toString(), each.toString()));
        Interval.oneTo(3).each(each -> map.removeKey(each.toString()));
        assertTrue(map.trimToSize());
        assertEquals(5, map.size());
    }

    @Override
    protected UnifiedMapWithHashingStrategy<Integer, Integer> mapWithCollisionsOfSize(int size)
    {
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(
                INTEGER_HASHING_STRATEGY, size);
        return this.populateMapWithCollisionsOfSize(size, map);
    }

    private static final class EntrySumProcedure implements Procedure<Map.Entry<Integer, Integer>>
    {
        private final Sum sum;

        private EntrySumProcedure(Sum sum)
        {
            this.sum = sum;
        }

        @Override
        public void value(Map.Entry<Integer, Integer> each)
        {
            this.sum.add(each.getKey());
            this.sum.add(each.getValue());
        }
    }
}
