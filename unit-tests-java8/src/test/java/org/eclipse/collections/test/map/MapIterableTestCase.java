/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.test.RichIterableWithDuplicatesTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface MapIterableTestCase extends RichIterableWithDuplicatesTestCase
{
    @Override
    <T> MapIterable<Object, T> newWith(T... elements);

    <K, V> MapIterable<K, V> newWithKeysValues(Object... elements);

    default <K, V> MapIterable<K, V> newWithTransformedKeysValues(Object... elements)
    {
        MutableMap<K, V> result = Maps.mutable.empty();
        for (int i = 0; i < elements.length; i += 2)
        {
            result.put((K) elements[i], (V) elements[i + 1]);
        }
        return result.asUnmodifiable();
    }

    default boolean supportsNullKeys()
    {
        return true;
    }

    default boolean supportsNullValues()
    {
        return true;
    }

    default boolean allowsPut()
    {
        return true;
    }

    default boolean supportsNonComparableKeys()
    {
        return true;
    }

    @Test
    default void serialization()
    {
        MapIterable<Object, String> original = this.newWith("Three", "Two", "One");
        MapIterable<Object, String> copy = SerializeTestHelper.serializeDeserialize(original);
        assertIterablesEqual(copy, original);
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        MapIterable<String, Integer> map = this.newWithKeysValues("Two", 2, "One", 1);
        assertEquals("{Two=2, One=1}", map.toString());
        assertEquals("[Two, One]", map.keysView().toString());
        assertEquals("[2, 1]", map.valuesView().toString());
        assertEquals("[Two:2, One:1]", map.keyValuesView().toString());
        assertEquals("[2, 1]", map.asLazy().toString());

        assertEquals(
                "{10=4, 9=4, 8=4, 7=4, 6=3, 5=3, 4=3, 3=2, 2=2, 1=1}",
                this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).toString());
        assertEquals(
                "[10, 9, 8, 7, 6, 5, 4, 3, 2, 1]",
                this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).keysView().toString());
        assertEquals(
                "[4, 4, 4, 4, 3, 3, 3, 2, 2, 1]",
                this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).valuesView().toString());
        assertEquals(
                "[10:4, 9:4, 8:4, 7:4, 6:3, 5:3, 4:3, 3:2, 2:2, 1:1]",
                this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).keyValuesView().toString());

        if (this.allowsPut() && this.supportsNonComparableKeys())
        {
            MapIterable<Object, Object> selfKey = this.newWithKeysValues();
            ((Map<Object, Object>) selfKey).put(selfKey, "value");
            assertEquals("{(this Map)=value}", selfKey.toString());

            MapIterable<Object, Object> selfValue = this.newWithKeysValues();
            ((Map<Object, Object>) selfValue).put("key", selfValue);
            assertEquals("{key=(this Map)}", selfValue.toString());
        }
        else if (!this.allowsPut())
        {
            MapIterable<Object, Object> selfKey = this.newWithKeysValues();
            assertThrows(UnsupportedOperationException.class, () -> ((Map<Object, Object>) selfKey).put(selfKey, "value"));

            MapIterable<Object, Object> selfValue = this.newWithKeysValues();
            assertThrows(UnsupportedOperationException.class, () -> ((Map<Object, Object>) selfValue).put("key", selfValue));
        }
        else
        {
            MapIterable<Object, Object> selfKey = this.newWithKeysValues();
            assertThrows(ClassCastException.class, () -> ((Map<Object, Object>) selfKey).put(selfKey, "value"));

            MapIterable<Object, Object> selfValue = this.newWithKeysValues();
            ((Map<Object, Object>) selfValue).put("key", selfValue);
            assertEquals("{key=(this Map)}", selfValue.toString());
        }
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        MapIterable<Object, Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);
        Iterator<Integer> iterator = iterable.iterator();
        iterator.next();
        iterator.remove();
        assertEquals(this.allowsDuplicates() ? 5 : 2, Iterate.sizeOf(iterable));
        assertThat(iterable.toBag(), isOneOf(
                this.getExpectedFiltered(3, 3, 3, 2, 2),
                this.getExpectedFiltered(3, 3, 3, 2, 1),
                this.getExpectedFiltered(3, 3, 2, 2, 1)));
    }

    @Test
    default void MapIterable_ifPresentApply()
    {
        MapIterable<String, Integer> map = this.newWithKeysValues("Three", 3, "Two", 2, "One", 1);
        assertEquals(Integer.valueOf(13), map.ifPresentApply("Three", x -> x + 10));
        assertEquals(Integer.valueOf(12), map.ifPresentApply("Two", x -> x + 10));
        assertEquals(Integer.valueOf(11), map.ifPresentApply("One", x -> x + 10));
        assertNull(map.ifPresentApply("Zero", x -> x + 10));
        assertIterablesEqual(map, this.newWithKeysValues("Three", 3, "Two", 2, "One", 1));
    }

    @Test
    default void MapIterable_aggregateBy()
    {
        String oneToFive = "oneToFive";
        String sixToNine = "sixToNine";
        String tenToFifteen = "tenToFifteen";
        String sixteenToTwenty = "sixteenToTwenty";

        MapIterable<String, Interval> map = Maps.mutable.with(oneToFive, Interval.fromTo(1, 5),
                sixToNine, Interval.fromTo(6, 9), tenToFifteen, Interval.fromTo(10, 15),
                sixteenToTwenty, Interval.fromTo(16, 20));

        String lessThanTen = "lessThanTen";
        String greaterOrEqualsToTen = "greaterOrEqualsToTen";

        MapIterable<String, Long> result = map.aggregateBy(
                eachKey -> eachKey.equals(oneToFive) || eachKey.equals(sixToNine) ? lessThanTen : greaterOrEqualsToTen,
                each -> each.sumOfInt(Integer::intValue),
                () -> 0L,
                (argument1, argument2) -> argument1 + argument2);

        MapIterable<String, Long> expected =
                Maps.mutable.with(lessThanTen, Interval.fromTo(1, 9).sumOfInt(Integer::intValue),
                        greaterOrEqualsToTen, Interval.fromTo(10, 20).sumOfInt(Integer::intValue));
        assertEquals(expected, result);
    }

    @Test
    default void MapIterable_getIfAbsent()
    {
        MapIterable<String, Integer> map = this.newWithKeysValues("Three", 3, "Two", 2, "One", 1);
        assertEquals(Integer.valueOf(3), map.getIfAbsentValue("Three", 0));
        assertEquals(Integer.valueOf(2), map.getIfAbsentValue("Two", 0));
        assertEquals(Integer.valueOf(1), map.getIfAbsentValue("One", 0));
        assertEquals(Integer.valueOf(0), map.getIfAbsentValue("Zero", 0));
        assertIterablesEqual(map, this.newWithKeysValues("Three", 3, "Two", 2, "One", 1));

        assertEquals(Integer.valueOf(3), map.getIfAbsent("Three", () -> 0));
        assertEquals(Integer.valueOf(2), map.getIfAbsent("Two", () -> 0));
        assertEquals(Integer.valueOf(1), map.getIfAbsent("One", () -> 0));
        assertEquals(Integer.valueOf(0), map.getIfAbsent("Zero", () -> 0));
        assertIterablesEqual(map, this.newWithKeysValues("Three", 3, "Two", 2, "One", 1));

        assertEquals(Integer.valueOf(3), map.getIfAbsentWith("Three", x -> x + 1, 13));
        assertEquals(Integer.valueOf(2), map.getIfAbsentWith("Two", x -> x + 1, 12));
        assertEquals(Integer.valueOf(1), map.getIfAbsentWith("One", x -> x + 1, 11));
        assertEquals(Integer.valueOf(11), map.getIfAbsentWith("Zero", x -> x + 1, 10));
        assertIterablesEqual(map, this.newWithKeysValues("Three", 3, "Two", 2, "One", 1));
    }

    @Test
    default void MapIterable_forEachKey()
    {
        UnifiedSet<Integer> result = UnifiedSet.newSet();
        MapIterable<Integer, String> map = this.newWithKeysValues(3, "3", 2, "2", 1, "1");
        map.forEachKey(CollectionAddProcedure.on(result));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), result);
    }

    @Test
    default void MapIterable_forEachValue()
    {
        UnifiedSet<String> result = UnifiedSet.newSet();
        MapIterable<Integer, String> map = this.newWithKeysValues(3, "3", 2, "2", 1, "1");
        map.forEachValue(CollectionAddProcedure.on(result));
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1", "2", "3"), result);
    }

    @Test
    default void MapIterable_forEachKeyValue()
    {
        MapIterable<Object, Integer> map = this.newWith(3, 3, 3, 2, 2, 1);
        MutableCollection<Integer> forEachKeyValue = this.newMutableForFilter();
        map.forEachKeyValue((key, value) -> forEachKeyValue.add(value + 10));
        assertIterablesEqual(this.newMutableForFilter(13, 13, 13, 12, 12, 11), forEachKeyValue);

        MapIterable<Integer, String> map2 = this.newWithKeysValues(3, "Three", 2, "Two", 1, "Three");
        MutableCollection<String> forEachKeyValue2 = this.newMutableForFilter();
        map2.forEachKeyValue((key, value) -> forEachKeyValue2.add(key + value));
        assertIterablesEqual(this.newMutableForFilter("3Three", "2Two", "1Three"), forEachKeyValue2);

        MutableCollection<Integer> forEachValue = this.newMutableForFilter();
        map.forEachValue(value -> forEachValue.add(value + 10));
        assertIterablesEqual(this.newMutableForFilter(13, 13, 13, 12, 12, 11), forEachValue);

        MutableCollection<Object> forEachKey = this.newMutableForFilter();
        map2.forEachKey(key -> forEachKey.add(key + 1));
        assertIterablesEqual(this.newMutableForFilter(4, 3, 2), forEachKey);
    }

    @Test
    default void MapIterable_injectIntoKeyValue()
    {
        MapIterable<Integer, Integer> map1 = this.newWithKeysValues(3, 3, 2, 2, 1, 1);
        Integer sum1 = map1.injectIntoKeyValue(0, (sum, key, value) -> sum + key + value);
        assertEquals(Integer.valueOf(12), sum1);

        MutableMap<Integer, String> result = map1.injectIntoKeyValue(Maps.mutable.empty(),
                (map, key, value) -> map.withKeyValue(key, value.toString()));
        assertIterablesEqual(Maps.mutable.with(3, "3", 2, "2", 1, "1"), result);
    }

    @Test
    default void MapIterable_flip()
    {
        MapIterable<String, Integer> map = this.newWithKeysValues("Three", 3, "Two", 2, "One", 1);
        Multimap<Integer, String> result = map.flip();

        MutableBag<Pair<Integer, String>> expected = Bags.mutable.with(
                Tuples.pair(3, "Three"),
                Tuples.pair(2, "Two"),
                Tuples.pair(1, "One"));
        assertEquals(expected, result.keyValuePairsView().toBag());
    }

    @Test
    default void MapIterable_flipUniqueValues()
    {
        MapIterable<String, Integer> map = this.newWithKeysValues("Three", 3, "Two", 2, "One", 1);
        MapIterable<Integer, String> result = map.flipUniqueValues();

        // TODO: Set up methods like getExpectedTransformed, but for maps. Delete overrides of this method.
        assertIterablesEqual(
                UnifiedMap.newWithKeysValues(3, "Three", 2, "Two", 1, "One"),
                result);

        assertThrows(
                IllegalStateException.class,
                () -> this.newWithKeysValues(1, "2", 2, "2").flipUniqueValues());
    }

    @Test
    default void MapIterable_collect_Function2()
    {
        MapIterable<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        MapIterable<Integer, String> actual = map.collect((key, value) -> Tuples.pair(key + 1, key + value));
        // TODO: Use IterableTestCase.assertEquals instead, after setting up methods like getExpectedTransformed, but for maps.
        assertEquals(Maps.mutable.with(4, "3Three", 3, "2Two", 2, "1One"), actual);
    }

    @Test
    default void MapIterable_collectValues()
    {
        MapIterable<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        MapIterable<Integer, String> actual = map.collectValues((argument1, argument2) -> new StringBuilder(argument2).reverse().toString());
        assertIterablesEqual(
                this.newWithKeysValues(3, "eerhT", 2, "owT", 1, "enO"),
                actual);
    }

    @Test
    default void MapIterable_collectKeysUnique()
    {
        MapIterable<String, Integer> map = this.newWithKeysValues("Two", 2, "Three", 3, "One", 1);
        MapIterable<String, Integer> actual = map.collectKeysUnique((argument1, argument2) -> new StringBuilder(argument1).reverse().toString());
        MapIterable<Object, Object> expected = this.newWithTransformedKeysValues("owT", 2, "eerhT", 3, "enO", 1);
        assertEquals(expected, actual);
        IllegalStateException illegalStateException = assertThrows(
                IllegalStateException.class,
                () -> map.collectKeysUnique((argument1, argument2) -> argument1.substring(0, 1)));
        assertEquals("Key T already exists in map!", illegalStateException.getMessage());

        MapIterable<String, Integer> map1 = this.newWithKeysValues("One", 1);
        assertEquals(
                Maps.immutable.with("collision", 1),
                map1.collectKeysUnique((argument1, argument2) -> "collision"));

        MapIterable<String, Integer> map2 = this.newWithKeysValues("One", 1, "Two", 2);
        IllegalStateException illegalStateException2 = assertThrows(
                IllegalStateException.class,
                () -> map2.collectKeysUnique((argument1, argument2) -> "collision"));
        assertEquals("Key collision already exists in map!", illegalStateException2.getMessage());
        MapIterable<String, Integer> map3 = this.newWithKeysValues("One", 1, "Two", 2, "Three", 3);
        IllegalStateException illegalStateException3 = assertThrows(
                IllegalStateException.class,
                () -> map3.collectKeysUnique((argument1, argument2) -> "collision"));
        assertEquals("Key collision already exists in map!", illegalStateException3.getMessage());
        MapIterable<String, Integer> map4 = this.newWithKeysValues("One", 1, "Two", 2, "Three", 3, "Four", 4);
        IllegalStateException illegalStateException4 = assertThrows(
                IllegalStateException.class,
                () -> map4.collectKeysUnique((argument1, argument2) -> "collision"));
        assertEquals("Key collision already exists in map!", illegalStateException4.getMessage());
        MapIterable<String, Integer> map5 = this.newWithKeysValues("One", 1, "Two", 2, "Three", 3, "Four", 4, "Five", 5);
        IllegalStateException illegalStateException5 = assertThrows(
                IllegalStateException.class,
                () -> map5.collectKeysUnique((argument1, argument2) -> "collision"));
        assertEquals("Key collision already exists in map!", illegalStateException5.getMessage());
    }

    @Test
    default void MapIterable_select_reject()
    {
        MapIterable<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        MapIterable<Object, Object> expected = this.newWithKeysValues(2, "Two", 1, "One");

        assertIterablesEqual(
                expected,
                map.select((key1, value1) -> key1.equals(1) || value1.equals("Two")));

        assertIterablesEqual(
                expected,
                map.reject((key, value) -> !key.equals(1) && !value.equals("Two")));
    }

    @Test
    default void MapIterable_detect()
    {
        MapIterable<String, String> map = this.newWithKeysValues("1", "One", "2", "Two", "3", "Three");
        assertEquals(Tuples.pair("1", "One"), map.detect((key1, value1) -> "1".equals(key1)));
        assertEquals(Tuples.pair("2", "Two"), map.detect((key1, value1) -> "Two".equals(value1)));
        assertNull(map.detect((ignored1, ignored2) -> false));

        MutableCollection<Integer> expectedIterationOrder = this.expectedIterationOrder();
        MutableCollection<Integer> detectIterationOrder = this.newMutableForFilter();
        MapIterable<Object, Integer> instanceUnderTest = (MapIterable<Object, Integer>) this.getInstanceUnderTest();
        instanceUnderTest.detect((key, value) -> {
            detectIterationOrder.add(value);
            return false;
        });
        assertIterablesEqual(expectedIterationOrder, detectIterationOrder);
    }

    @Test
    default void MapIterable_detectOptional()
    {
        MapIterable<String, String> map = this.newWithKeysValues("1", "One", "2", "Two", "3", "Three");
        assertEquals(Optional.of(Tuples.pair("1", "One")), map.detectOptional((key1, value1) -> "1".equals(key1)));
        assertEquals(Optional.of(Tuples.pair("2", "Two")), map.detectOptional((key1, value1) -> "Two".equals(value1)));
        assertSame(Optional.empty(), map.detectOptional((ignored1, ignored2) -> false));

        MutableCollection<Integer> expectedIterationOrder = this.expectedIterationOrder();
        MutableCollection<Integer> detectOptionalIterationOrder = this.newMutableForFilter();
        MapIterable<Object, Integer> instanceUnderTest = (MapIterable<Object, Integer>) this.getInstanceUnderTest();
        instanceUnderTest.detectOptional((key, value) -> {
            detectOptionalIterationOrder.add(value);
            return false;
        });
        assertIterablesEqual(expectedIterationOrder, detectOptionalIterationOrder);
    }

    @Test
    default void MapIterable_containsKey()
    {
        MapIterable<String, Integer> map = this.newWithKeysValues("Three", 3, "Two", 2, "One", 1);
        assertTrue(map.containsKey("Three"));
        assertFalse(map.containsKey("Four"));

        if (this.supportsNullKeys())
        {
            assertFalse(map.containsKey(null));

            MapIterable<String, Integer> map2 = this.newWithKeysValues("Three", 3, "Two", 2, "One", 1, null, null);
            assertTrue(map2.containsKey(null));
        }
    }

    @Test
    default void MapIterable_containsValue()
    {
        MapIterable<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        assertTrue(map.containsValue("Three"));
        assertFalse(map.containsValue("Four"));

        if (this.supportsNullValues())
        {
            assertFalse(map.containsValue(null));
            MapIterable<String, Integer> map2 = this.newWithKeysValues(3, "Three", 2, "Two", 1, null);
            assertTrue(map2.containsValue(null));
        }

        if (this.supportsNullKeys() && this.supportsNullValues())
        {
            MapIterable<String, Integer> map2 = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One", null, null);
            assertTrue(map2.containsValue(null));
        }
    }

    @Test
    default void MapIterable_forEach()
    {
        MapIterable<Object, Integer> map = this.newWith(3, 3, 3, 2, 2, 1);
        MutableCollection<Integer> forEach = this.newMutableForFilter();
        map.forEach((key, value) -> forEach.add(value + 10));
        assertIterablesEqual(this.newMutableForFilter(13, 13, 13, 12, 12, 11), forEach);

        MapIterable<Integer, String> map2 = this.newWithKeysValues(3, "Three", 2, "Two", 1, "Three");
        MutableCollection<String> forEach2 = this.newMutableForFilter();
        map2.forEach((key, value) -> forEach2.add(key + value));
        assertIterablesEqual(this.newMutableForFilter("3Three", "2Two", "1Three"), forEach2);
    }

    @Test
    default void MapIterable_keySet_equals()
    {
        Map<Integer, String> map = (Map<Integer, String>) this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        Set<Integer> expected = new HashSetNoIterator<>();
        expected.add(3);
        expected.add(2);
        expected.add(1);
        assertEquals(expected, map.keySet());
    }

    @Test
    default void MapIterable_keySet_forEach()
    {
        Map<Integer, String> map = (Map<Integer, String>) this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        MutableSet<Integer> actual = Sets.mutable.with();
        map.keySet().forEach(actual::add);
        assertEquals(Sets.immutable.with(3, 2, 1), actual);
    }

    @Test
    default void MapIterable_entrySet_equals()
    {
        Map<Integer, String> map = (Map<Integer, String>) this.newWithKeysValues(1, "One", 2, "Two", 3, "Three");
        Set<Map.Entry<Integer, String>> expected = new HashSetNoIterator<>();
        expected.add(ImmutableEntry.of(1, "One"));
        expected.add(ImmutableEntry.of(2, "Two"));
        expected.add(ImmutableEntry.of(3, "Three"));
        assertEquals(expected, map.entrySet());
    }

    @Test
    default void MapIterable_entrySet_forEach()
    {
        Map<Integer, String> map = (Map<Integer, String>) this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        MutableSet<String> actual = Sets.mutable.with();
        map.entrySet().forEach(each -> actual.add(each.getValue()));
        assertEquals(Sets.immutable.with("Three", "Two", "One"), actual);
    }

    class HashSetNoIterator<T> extends HashSet<T>
    {
        @Override
        public Iterator<T> iterator()
        {
            throw new AssertionError();
        }
    }
}
