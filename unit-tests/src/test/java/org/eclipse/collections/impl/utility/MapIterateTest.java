/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility;

import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.BooleanHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.ByteHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.CharHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.DoubleHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.FloatHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.IntHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.LongHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.ShortHashBag;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.predicate.MapEntryPredicate;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.block.procedure.MapPutProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapIterateTest
{
    @Test
    public void occurrencesOf()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        assertEquals(0, MapIterate.occurrencesOf(map, -1));
        assertEquals(1, MapIterate.occurrencesOf(map, 1));
        assertEquals(1, MapIterate.occurrencesOf(map, 2));
    }

    @Test
    public void occurrencesOfAttribute()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        assertEquals(0, MapIterate.occurrencesOfAttribute(map, String::valueOf, "-1"));
        assertEquals(1, MapIterate.occurrencesOfAttribute(map, String::valueOf, "1"));
        assertEquals(1, MapIterate.occurrencesOfAttribute(map, String::valueOf, "2"));
    }

    @Test
    public void toListOfPairs()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        MutableList<Pair<String, Integer>> pairs = MapIterate.toListOfPairs(map);
        Verify.assertSize(5, pairs);
        Verify.assertContains(Tuples.pair("1", 1), pairs);
        Verify.assertContains(Tuples.pair("2", 2), pairs);
        Verify.assertContains(Tuples.pair("3", 3), pairs);
        Verify.assertContains(Tuples.pair("4", 4), pairs);
        Verify.assertContains(Tuples.pair("5", 5), pairs);
    }

    @Test
    public void injectInto()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        assertEquals(Integer.valueOf(1 + 2 + 3 + 4 + 5), MapIterate.injectInto(0, map, AddFunction.INTEGER));
    }

    @Test
    public void functionMapTransformation()
    {
        MutableMap<Integer, Integer> input = Maps.fixedSize.of(1, 10, 2, 20);

        MutableMap<String, String> result = MapIterate.collect(input, Functions.getToString(), Functions.getToString());

        Verify.assertContainsKeyValue("1", "10", result);
        Verify.assertContainsKeyValue("2", "20", result);
        Verify.assertSize(2, result);
    }

    @Test
    public void simpleMapTransformation()
    {
        MutableMap<Locale, Currency> input = Maps.fixedSize.of(Locale.UK, Currency.getInstance(Locale.UK), Locale.JAPAN, Currency.getInstance(Locale.JAPAN));

        Function<Locale, String> getCountry = Locale::getCountry;
        Function<Currency, String> getCurrencyCode = Currency::getCurrencyCode;
        MutableMap<String, String> result = MapIterate.collect(input, getCountry, getCurrencyCode);
        Verify.assertContainsKeyValue("GB", "GBP", result);
        Verify.assertContainsKeyValue("JP", "JPY", result);
        Verify.assertSize(2, result);
    }

    @Test
    public void complexMapTransformation()
    {
        MutableMap<Locale, Currency> input = Maps.fixedSize.of(Locale.UK, Currency.getInstance(Locale.UK), Locale.JAPAN, Currency.getInstance(Locale.JAPAN));

        Function2<Locale, Currency, Pair<String, String>> function = (locale, currency) -> Tuples.pair(locale.getDisplayCountry(Locale.ENGLISH) + ':' + currency.getCurrencyCode(), currency.getCurrencyCode());
        MutableMap<String, String> result = MapIterate.collect(input, function);

        Verify.assertContainsKeyValue("United Kingdom:GBP", "GBP", result);
        Verify.assertContainsKeyValue("Japan:JPY", "JPY", result);
        Verify.assertSize(2, result);
    }

    @Test
    public void conditionalMapTransformation()
    {
        MutableMap<Locale, Currency> input = UnifiedMap.newWithKeysValues(Locale.UK, Currency.getInstance(Locale.UK), Locale.JAPAN, Currency.getInstance(Locale.JAPAN), Locale.CHINA, Currency.getInstance(Locale.GERMANY), Locale.GERMANY, Currency.getInstance(Locale.CHINA));

        MutableMap<String, String> result = MapIterate.collectIf(input, (locale, currency) -> Tuples.pair(locale.getDisplayCountry(Locale.ENGLISH) + ':' + currency.getCurrencyCode(), currency.getCurrencyCode()), (locale, currency) -> Currency.getInstance(locale).equals(currency));

        Verify.assertContainsKeyValue("United Kingdom:GBP", "GBP", result);
        Verify.assertContainsKeyValue("Japan:JPY", "JPY", result);
        Verify.assertSize(2, result);
    }

    @Test
    public void reverseMapping()
    {
        MutableMap<Integer, Integer> input = Maps.fixedSize.of(1, 10, 2, 20);

        MutableMap<Integer, Integer> result = MapIterate.reverseMapping(input);
        Verify.assertContainsKeyValue(10, 1, result);
        Verify.assertContainsKeyValue(20, 2, result);
        Verify.assertSize(2, result);
    }

    @Test
    public void toSortedList_with_comparator()
    {
        MutableMap<String, Integer> integers = this.getIntegerMap();
        MutableList<Integer> list = MapIterate.toSortedList(integers, Collections.reverseOrder());
        MutableList<Integer> expected = FastList.newList(integers.values()).sortThis(Collections.reverseOrder());
        assertEquals(expected, list);
    }

    @Test
    public void toSortedSetBy()
    {
        MutableMap<String, Integer> integers = this.getIntegerMap();
        MutableSortedSet<Integer> set = integers.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers.values()), set);
    }

    private MutableMap<String, Integer> getIntegerMap()
    {
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        this.populateIntegerMap(map);
        return map;
    }

    private void populateIntegerMap(Map<String, Integer> map)
    {
        map.put("5", 5);
        map.put("4", 4);
        map.put("3", 3);
        map.put("2", 2);
        map.put("1", 1);
    }

    @Test
    public void selectWithDifferentTargetCollection()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        Collection<Integer> results = MapIterate.select(map, Integer.class::isInstance, FastList.newList());
        assertEquals(Bags.mutable.of(1, 2, 3, 4, 5), HashBag.newBag(results));
    }

    @Test
    public void count()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        assertEquals(5, MapIterate.count(map, Integer.class::isInstance));
    }

    @Test
    public void rejectWithDifferentTargetCollection()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        MutableList<Integer> list = MapIterate.reject(map, Integer.class::isInstance, FastList.newList());
        Verify.assertEmpty(list);
    }

    @Test
    public void forEachValue()
    {
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        map.putAll(this.getIntegerMap());
        MutableList<Integer> list = Lists.mutable.of();
        MapIterate.forEachValue(map, CollectionAddProcedure.on(list));
        MapIterate.forEachValue(new HashMap<>(map), CollectionAddProcedure.on(list));
        MapIterate.forEachValue(new HashMap<>(), CollectionAddProcedure.on(list));
        Verify.assertSize(10, list);
        assertEquals(30, list.injectInto(0, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void forEachValueThrowsOnNull()
    {
        assertThrows(IllegalArgumentException.class, () -> MapIterate.forEachValue(null, null));
    }

    @Test
    public void forEachKeyThrowsOnNull()
    {
        assertThrows(IllegalArgumentException.class, () -> MapIterate.forEachKey(null, null));
    }

    @Test
    public void forEachKeyValueThrowsOnNull()
    {
        assertThrows(NullPointerException.class, () -> MapIterate.forEachKeyValue(null, null));
    }

    @Test
    public void forEachKeyWithEmpty()
    {
        MapIterate.forEachKey(UnifiedMap.newMap(), null);
        //the implicit assertion is that it doesn't blow up with a NPE
    }

    @Test
    public void forEachKey()
    {
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        map.putAll(this.getIntegerMap());
        MutableBag<String> bag = Bags.mutable.of();
        MapIterate.forEachKey(map, CollectionAddProcedure.on(bag));
        MapIterate.forEachKey(new HashMap<>(map), CollectionAddProcedure.on(bag));
        MapIterate.forEachKey(new HashMap<>(), CollectionAddProcedure.on(bag));
        assertEquals(HashBag.newBagWith("1", "1", "2", "2", "3", "3", "4", "4", "5", "5"), bag);
    }

    @Test
    public void forEachKeyValueWithEmpty()
    {
        MapIterate.forEachKeyValue(UnifiedMap.newMap(), null);
        //the implicit assertion is that it doesn't blow up with a NPE
    }

    @Test
    public void forEachKeyValue()
    {
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        map.putAll(UnifiedMap.newMap(this.getIntegerMap()));
        MutableMap<String, Integer> newMap = UnifiedMap.newMap();
        Procedure2<String, Integer> procedure = new MapPutProcedure<>(newMap);
        MapIterate.forEachKeyValue(map, procedure);
        Verify.assertMapsEqual(map, newMap);
    }

    @Test
    public void getIfAbsentPut()
    {
        MutableMap<String, String> unifiedMap = UnifiedMap.newMap();
        Map<String, String> hashMap = new HashMap<>();
        String value = "value";
        String value1 = MapIterate.getIfAbsentPut(unifiedMap, "key", () -> value);
        String value2 = MapIterate.getIfAbsentPut(unifiedMap, "key", () -> value);
        assertEquals("value", value1);
        assertSame(value1, value2);
        String value3 = MapIterate.getIfAbsentPut(hashMap, "key", () -> value);
        String value4 = MapIterate.getIfAbsentPut(hashMap, "key", () -> value);
        assertEquals("value", value3);
        assertSame(value3, value4);
    }

    @Test
    public void getIfAbsentPutWith()
    {
        MutableMap<String, String> map = UnifiedMap.newMap();
        Function<String, String> function = object -> "value:" + object;
        String value1 = MapIterate.getIfAbsentPutWith(map, "key", function, "1");
        String value2 = MapIterate.getIfAbsentPutWith(map, "key", function, "2");
        assertSame(value1, value2);
    }

    @Test
    public void getIfAbsentPutWithNullValue()
    {
        MutableMap<String, String> map = UnifiedMap.newMap();
        map.put("nullValueKey", null);
        assertNull(MapIterate.getIfAbsentPut(map, "nullValueKey", () -> "aValue"));
    }

    @SuppressWarnings("StringOperationCanBeSimplified")
    @Test
    public void getIfAbsent()
    {
        MutableMap<String, String> unifiedMap = UnifiedMap.newMapWith(Tuples.pair("key1", "key1Value"));
        Map<String, String> hashMap = new HashMap<>(unifiedMap);
        String value1 = MapIterate.getIfAbsent(unifiedMap, "key", () -> new String("value"));
        String value2 = MapIterate.getIfAbsent(unifiedMap, "key", () -> new String("value"));
        String value3 = MapIterate.getIfAbsent(hashMap, "key", () -> new String("value"));
        assertEquals("value", value1);
        assertEquals("value", value2);
        assertEquals("value", value3);
        assertNotSame(value1, value2);
        assertNotSame(value1, value3);
        assertEquals("key1Value", MapIterate.getIfAbsent(hashMap, "key1", () -> "value"));
        assertEquals("key1Value", MapIterate.getIfAbsent(unifiedMap, "key1", () -> "value"));
    }

    @Test
    public void getIfAbsentDefault()
    {
        MutableMap<String, String> map = UnifiedMap.<String, String>newMap().withKeysValues("key", "value");
        assertEquals("value", MapIterate.getIfAbsentDefault(map, "key", "defaultValue1"));
        assertEquals("defaultValue2", MapIterate.getIfAbsentDefault(map, "noKey", "defaultValue2"));
        Verify.assertNotContainsKey("noKey", map);
        Verify.assertSize(1, map);
    }

    @Test
    public void getIfAbsentWith()
    {
        MutableMap<String, Integer> unifiedMap = UnifiedMap.newMap();
        this.populateIntegerMap(unifiedMap);
        Map<String, Integer> hashMap = new HashMap<>(unifiedMap);
        Function<Integer, Integer> function = Functions.getPassThru();
        Integer ifAbsentValue = Integer.valueOf(6);
        assertEquals(ifAbsentValue, MapIterate.getIfAbsentWith(unifiedMap, "six", function, ifAbsentValue));
        assertEquals(Integer.valueOf(5), MapIterate.getIfAbsentWith(unifiedMap, "5", function, ifAbsentValue));
        assertEquals(ifAbsentValue, MapIterate.getIfAbsentWith(hashMap, "six", function, ifAbsentValue));
        assertEquals(Integer.valueOf(5), MapIterate.getIfAbsentWith(hashMap, "5", function, ifAbsentValue));
    }

    @Test
    public void withNullValue()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues("key", null);
        String value = "value";
        assertNull(MapIterate.getIfAbsent(map, "key", () -> value));
        assertNull(MapIterate.getIfAbsentPut(map, "key", () -> value));
        assertEquals("result", MapIterate.ifPresentApply(map, "key", object -> "result"));
    }

    @Test
    public void ifPresentApply()
    {
        MutableMap<String, String> unifiedMap = UnifiedMap.newWithKeysValues("testKey", "testValue");
        Map<String, String> hashMap = new HashMap<>(unifiedMap);
        assertEquals("TESTVALUE", MapIterate.ifPresentApply(unifiedMap, "testKey", String::toUpperCase));
        assertEquals("TESTVALUE", MapIterate.ifPresentApply(hashMap, "testKey", String::toUpperCase));
    }

    @Test
    public void selectMapOnEntry()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        MutableMap<String, String> resultMap = MapIterate.selectMapOnEntry(map, (argument1, argument2) -> "1".equals(argument1) || "1".equals(argument2));
        Verify.assertSize(2, resultMap);
        Verify.assertContainsKeyValue("1", "2", resultMap);
        Verify.assertContainsKeyValue("2", "1", resultMap);
    }

    @Test
    public void rejectMapOnEntry()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        MutableMap<String, String> resultMap = MapIterate.rejectMapOnEntry(map, (argument1, argument2) -> "1".equals(argument1) || "1".equals(argument2));
        Verify.assertSize(1, resultMap);
        Verify.assertContainsKeyValue("3", "3", resultMap);
    }

    @Test
    public void select()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        assertEquals(FastList.newListWith("1"), MapIterate.select(map, "1"::equals));
    }

    @Test
    public void selectMapEntries()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        Collection<Map.Entry<String, String>> results = Iterate.select(map.entrySet(), new MapEntryPredicate<String, String>()
        {
            @Override
            public boolean accept(String argument1, String argument2)
            {
                return "1".equals(argument1) || "1".equals(argument2);
            }
        });
        Verify.assertSize(2, results);
    }

    @Test
    public void selectMapOnKey()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        MutableMap<String, String> resultMap = MapIterate.selectMapOnKey(map, "1"::equals);
        assertEquals(UnifiedMap.newWithKeysValues("1", "2"), resultMap);
    }

    @Test
    public void selectMapOnValue()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        MutableMap<String, String> resultMap = MapIterate.selectMapOnValue(map, "1"::equals);
        assertEquals(UnifiedMap.newWithKeysValues("2", "1"), resultMap);
    }

    @Test
    public void detect()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        String resultFound = MapIterate.detect(map, "1"::equals);
        assertEquals("1", resultFound);
        String resultNotFound = MapIterate.detect(map, "4"::equals);
        assertNull(resultNotFound);
    }

    @Test
    public void detectThrowsOnNull()
    {
        assertThrows(IllegalArgumentException.class, () -> MapIterate.detect(null, (Predicate2<? super Object, ? super Object>) null));
    }

    @Test
    public void detectIfNone()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        String resultNotFound = MapIterate.detectIfNone(map, "4"::equals, "0");
        assertEquals("0", resultNotFound);
        String resultFound = MapIterate.detectIfNone(map, "1"::equals, "0");
        assertEquals("1", resultFound);
    }

    @Test
    public void anySatisfy()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        assertTrue(MapIterate.anySatisfy(map, "1"::equals));
        assertTrue(MapIterate.anySatisfy(map, "3"::equals));
        assertFalse(MapIterate.anySatisfy(map, "4"::equals));
    }

    @Test
    public void allSatisfy()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        assertFalse(MapIterate.allSatisfy(map, Predicates.notEqual("1")));
        assertFalse(MapIterate.allSatisfy(map, Predicates.notEqual("3")));
        assertTrue(MapIterate.anySatisfy(map, Predicates.notEqual("4")));
    }

    @Test
    public void noneSatisfy()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        assertFalse(MapIterate.noneSatisfy(map, "1"::equals));
        assertFalse(MapIterate.noneSatisfy(map, "3"::equals));
        assertTrue(MapIterate.noneSatisfy(map, "4"::equals));
    }

    @Test
    public void isEmpty()
    {
        assertTrue(MapIterate.isEmpty(null));
        assertTrue(MapIterate.isEmpty(UnifiedMap.newMap()));
        assertFalse(MapIterate.isEmpty(Maps.fixedSize.of("1", "1")));
    }

    @Test
    public void notEmpty()
    {
        assertFalse(MapIterate.notEmpty(null));
        assertFalse(MapIterate.notEmpty(UnifiedMap.newMap()));
        assertTrue(MapIterate.notEmpty(Maps.fixedSize.of("1", "1")));
    }

    @Test
    public void reject()
    {
        MutableList<Integer> result = MapIterate.reject(
                newLittleMap(),
                Predicates.greaterThanOrEqualTo(2));
        assertEquals(FastList.newListWith(1), result);
    }

    private static MutableMap<Character, Integer> newLittleMap()
    {
        return UnifiedMap.<Character, Integer>newMap().withKeysValues('a', 1).withKeysValues('b', 2);
    }

    @Test
    public void addAllKeysToCollection()
    {
        MutableList<Character> target = Lists.mutable.of();
        MapIterate.addAllKeysTo(newLittleMap(), target);
        assertEquals(FastList.newListWith('a', 'b').toBag(), target.toBag());
    }

    @Test
    public void addAllValuesToCollection()
    {
        MutableList<Integer> target = Lists.mutable.of();
        MapIterate.addAllValuesTo(newLittleMap(), target);
        assertEquals(FastList.newListWith(1, 2).toBag(), target.toBag());
    }

    @Test
    public void collect()
    {
        MutableList<String> result = MapIterate.collect(newLittleMap(), Functions.getToString());
        assertEquals(FastList.newListWith("1", "2").toBag(), result.toBag());
    }

    @Test
    public void collectBoolean()
    {
        MutableBooleanCollection result = MapIterate.collectBoolean(MapIterateTest.newLittleMap(), PrimitiveFunctions.integerIsPositive());
        assertEquals(BooleanHashBag.newBagWith(true, true), result.toBag());
    }

    @Test
    public void collectBooleanWithTarget()
    {
        BooleanHashBag target = new BooleanHashBag();
        BooleanHashBag result = MapIterate.collectBoolean(MapIterateTest.newLittleMap(), PrimitiveFunctions.integerIsPositive(), target);
        assertEquals(BooleanHashBag.newBagWith(true, true), result.toBag());
        assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectByte()
    {
        MutableByteCollection result = MapIterate.collectByte(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToByte());
        assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2), result.toBag());
    }

    @Test
    public void collectByteWithTarget()
    {
        ByteHashBag target = new ByteHashBag();
        ByteHashBag result = MapIterate.collectByte(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToByte(), target);
        assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2), result.toBag());
        assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectChar()
    {
        MutableCharCollection result = MapIterate.collectChar(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToChar());
        assertEquals(CharHashBag.newBagWith((char) 1, (char) 2), result.toBag());
    }

    @Test
    public void collectCharWithTarget()
    {
        CharHashBag target = new CharHashBag();
        CharHashBag result = MapIterate.collectChar(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToChar(), target);
        assertEquals(CharHashBag.newBagWith((char) 1, (char) 2), result.toBag());
        assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectDouble()
    {
        MutableDoubleCollection result = MapIterate.collectDouble(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToDouble());
        assertEquals(DoubleHashBag.newBagWith(1, 2), result.toBag());
    }

    @Test
    public void collectDoubleWithTarget()
    {
        DoubleHashBag target = new DoubleHashBag();
        DoubleHashBag result = MapIterate.collectDouble(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToDouble(), target);
        assertEquals(DoubleHashBag.newBagWith(1, 2), result.toBag());
        assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectFloat()
    {
        MutableFloatCollection result = MapIterate.collectFloat(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToFloat());
        assertEquals(FloatHashBag.newBagWith(1, 2), result.toBag());
    }

    @Test
    public void collectFloatWithTarget()
    {
        FloatHashBag target = new FloatHashBag();
        FloatHashBag result = MapIterate.collectFloat(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToFloat(), target);
        assertEquals(FloatHashBag.newBagWith(1, 2), result.toBag());
        assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectInt()
    {
        MutableIntCollection result = MapIterate.collectInt(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToInt());
        assertEquals(IntHashBag.newBagWith(1, 2), result.toBag());
    }

    @Test
    public void collectIntWithTarget()
    {
        IntHashBag target = new IntHashBag();
        IntHashBag result = MapIterate.collectInt(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToInt(), target);
        assertEquals(IntHashBag.newBagWith(1, 2), result.toBag());
        assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectLong()
    {
        MutableLongCollection result = MapIterate.collectLong(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToLong());
        assertEquals(LongHashBag.newBagWith(1L, 2L), result.toBag());
    }

    @Test
    public void collectLongWithTarget()
    {
        LongHashBag target = new LongHashBag();
        LongHashBag result = MapIterate.collectLong(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToLong(), target);
        assertEquals(LongHashBag.newBagWith(1L, 2L), result.toBag());
        assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectShort()
    {
        MutableShortCollection result = MapIterate.collectShort(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToShort());
        assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2), result.toBag());
    }

    @Test
    public void collectShortWithTarget()
    {
        ShortHashBag target = new ShortHashBag();
        MutableShortCollection result = MapIterate.collectShort(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToShort(), target);
        assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2), result.toBag());
        assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectValues()
    {
        MutableMap<Character, String> result = MapIterate.collectValues(newLittleMap(), (argument1, argument2) -> argument2.toString());
        assertEquals(UnifiedMap.newWithKeysValues('a', "1", 'b', "2").toBag(), result.toBag());
    }

    @Test
    public void collectIntoTarget()
    {
        MutableList<String> target = Lists.mutable.of();
        MutableList<String> result = MapIterate.collect(newLittleMap(), String::valueOf, target);
        assertEquals(FastList.newListWith("1", "2").toBag(), result.toBag());
        assertSame(target, result);
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(MapIterate.class);
    }
}
