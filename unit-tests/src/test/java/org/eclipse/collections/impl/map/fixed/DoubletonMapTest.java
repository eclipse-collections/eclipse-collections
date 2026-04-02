/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.fixed;

import java.util.Map;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link DoubletonMap}.
 */
public class DoubletonMapTest extends AbstractMemoryEfficientMutableMapTest
{
    @Override
    protected MutableMap<String, String> classUnderTest()
    {
        return new DoubletonMap<>("1", "One", "2", "Two");
    }

    @Override
    protected MutableMap<String, Integer> mixedTypeClassUnderTest()
    {
        return new DoubletonMap<>("1", 1, "Two", 2);
    }

    @Override
    @Test
    public void containsValue()
    {
        assertTrue(this.classUnderTest().containsValue("One"));
    }

    @Override
    @Test
    public void forEachKeyValue()
    {
        MutableList<String> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "One", 2, "Two");
        map.forEachKeyValue((key, value) -> collection.add(key + value));
        assertEquals(FastList.newListWith("1One", "2Two"), collection);
    }

    @Test
    public void flipUniqueValues()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "One", 2, "Two");
        MutableMap<String, Integer> flip = map.flipUniqueValues();
        Verify.assertInstanceOf(DoubletonMap.class, flip);
        assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2), flip);

        assertThrows(IllegalStateException.class, () -> new DoubletonMap<>(1, "One", 2, "One").flipUniqueValues());
    }

    @Override
    @Test
    public void nonUniqueWithKeyValue()
    {
        Twin<String> twin1 = Tuples.twin("1", "1");
        Twin<String> twin2 = Tuples.twin("2", "2");

        DoubletonMap<Twin<String>, Twin<String>> map = new DoubletonMap<>(twin1, twin1, twin2, twin2);
        assertSame(map.getKey1(), twin1);
        assertSame(map.getKey2(), twin2);

        Twin<String> twin3 = Tuples.twin("1", "1");
        map.withKeyValue(twin3, twin3);
        assertSame(map.get(twin1), twin3);

        Twin<String> twin4 = Tuples.twin("2", "2");
        map.withKeyValue(twin4, twin4);
        assertSame(map.get(twin2), twin4);
    }

    @Override
    @Test
    public void withKeyValue()
    {
        MutableMap<Integer, String> map1 = new DoubletonMap<>(1, "A", 2, "B").withKeyValue(3, "C");
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, "A", 2, "B", 3, "C"), map1);
        Verify.assertInstanceOf(TripletonMap.class, map1);

        MutableMap<Integer, String> map2 = new DoubletonMap<>(1, "A", 2, "B");
        MutableMap<Integer, String> map2with = map2.withKeyValue(1, "AA");
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, "AA", 2, "B"), map2with);
        assertSame(map2, map2with);
    }

    @Override
    @Test
    public void withAllKeyValueArguments()
    {
        MutableMap<Integer, String> map1 = new DoubletonMap<>(1, "A", 2, "B").withAllKeyValueArguments(
                Tuples.pair(1, "AA"), Tuples.pair(3, "C"));
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, "AA", 2, "B", 3, "C"), map1);
        Verify.assertInstanceOf(TripletonMap.class, map1);

        MutableMap<Integer, String> map2 = new DoubletonMap<>(1, "A", 2, "B");
        MutableMap<Integer, String> map2with = map2.withAllKeyValueArguments(Tuples.pair(1, "AA"));
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, "AA", 2, "B"), map2with);
        assertSame(map2, map2with);
    }

    @Override
    @Test
    public void withoutKey()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "A", 2, "B");
        MutableMap<Integer, String> mapWithout1 = map.withoutKey(3);
        assertSame(map, mapWithout1);
        MutableMap<Integer, String> mapWithout2 = map.withoutKey(1);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(2, "B"), mapWithout2);
        Verify.assertInstanceOf(SingletonMap.class, mapWithout2);
    }

    @Override
    @Test
    public void withoutAllKeys()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "A", 2, "B");
        MutableMap<Integer, String> mapWithout1 = map.withoutAllKeys(FastList.newListWith(3, 4));
        assertSame(map, mapWithout1);
        MutableMap<Integer, String> mapWithout2 = map.withoutAllKeys(FastList.newListWith(2, 3));
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, "A"), mapWithout2);
        Verify.assertInstanceOf(SingletonMap.class, mapWithout2);
    }

    @Override
    @Test
    public void forEachValue()
    {
        MutableList<String> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        map.forEachValue(CollectionAddProcedure.on(collection));
        assertEquals(FastList.newListWith("1", "2"), collection);
    }

    @Override
    @Test
    public void forEach()
    {
        MutableList<String> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        map.forEach(CollectionAddProcedure.on(collection));
        assertEquals(FastList.newListWith("1", "2"), collection);
    }

    @Override
    @Test
    public void forEachKey()
    {
        MutableList<Integer> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        map.forEachKey(CollectionAddProcedure.on(collection));
        assertEquals(FastList.newListWith(1, 2), collection);
    }

    @Override
    @Test
    public void getIfAbsentPut()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut(4, new PassThruFunction0<>("4")));
        assertEquals("1", map.getIfAbsentPut(1, new PassThruFunction0<>("1")));
    }

    @Override
    @Test
    public void getIfAbsentPutWith()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPutWith(4, String::valueOf, 4));
        assertEquals("1", map.getIfAbsentPutWith(1, String::valueOf, 1));
    }

    @Override
    @Test
    public void getIfAbsent_function()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsent(4, new PassThruFunction0<>("4")));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void getOrDefault()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        assertNull(map.get(4));
        assertEquals("1", map.getOrDefault(1, "4"));
        assertEquals("4", map.getOrDefault(4, "4"));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        assertNull(map.get(4));
        assertEquals("1", map.getIfAbsentValue(1, "4"));
        assertEquals("4", map.getIfAbsentValue(4, "4"));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsentWith()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsentWith(4, String::valueOf, 4));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void ifPresentApply()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        assertNull(map.ifPresentApply(4, Functions.getPassThru()));
        assertEquals("1", map.ifPresentApply(1, Functions.getPassThru()));
        assertEquals("2", map.ifPresentApply(2, Functions.getPassThru()));
    }

    @Override
    @Test
    public void notEmpty()
    {
        assertTrue(new DoubletonMap<>(1, "1", 2, "2").notEmpty());
    }

    @Override
    @Test
    public void forEachWith()
    {
        MutableList<Integer> result = Lists.mutable.of();
        MutableMap<Integer, Integer> map = new DoubletonMap<>(1, 1, 2, 2);
        map.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 10);
        assertEquals(FastList.newListWith(11, 12), result);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "One", 2, "Two");
        map.forEachWithIndex((value, index) ->
        {
            result.add(value);
            result.add(String.valueOf(index));
        });
        assertEquals(FastList.newListWith("One", "0", "Two", "1"), result);
    }

    @Override
    @Test
    public void entrySet()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "One", 2, "Two");
        for (Map.Entry<Integer, String> entry : map.entrySet())
        {
            result.add(entry.getValue());
        }
        assertEquals(FastList.newListWith("One", "Two"), result);
    }

    @Override
    @Test
    public void values()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "One", 2, "Two");
        for (String value : map.values())
        {
            result.add(value);
        }
        assertEquals(FastList.newListWith("One", "Two"), result);
    }

    @Override
    @Test
    public void keySet()
    {
        MutableList<Integer> result = Lists.mutable.of();
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "One", 2, "Two");
        for (Integer key : map.keySet())
        {
            result.add(key);
        }
        assertEquals(FastList.newListWith(1, 2), result);
    }

    @Override
    @Test
    public void testToString()
    {
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "One", 2, "Two");
        assertEquals("{1=One, 2=Two}", map.toString());
    }

    @Override
    @Test
    public void asLazyKeys()
    {
        MutableList<Integer> keys = Maps.fixedSize.of(1, 1, 2, 2).keysView().toSortedList();
        assertEquals(FastList.newListWith(1, 2), keys);
    }

    @Override
    @Test
    public void asLazyValues()
    {
        MutableList<Integer> values = Maps.fixedSize.of(1, 1, 2, 2).valuesView().toSortedList();
        assertEquals(FastList.newListWith(1, 2), values);
    }

    @Override
    @Test
    public void testEqualsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(
                UnifiedMap.newWithKeysValues("1", "One", "2", "Two"),
                this.classUnderTest());
    }

    @Override
    @Test
    public void select()
    {
        MutableMap<String, String> map = this.classUnderTest();

        MutableMap<String, String> empty = map.select((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(EmptyMap.class, empty);

        MutableMap<String, String> full = map.select((ignored1, ignored2) -> true);
        assertEquals(map, full);

        MutableMap<String, String> one = map.select((argument1, argument2) -> "1".equals(argument1));
        Verify.assertInstanceOf(SingletonMap.class, one);
        assertEquals(new SingletonMap<>("1", "One"), one);

        MutableMap<String, String> two = map.select((argument1, argument2) -> "2".equals(argument1));
        Verify.assertInstanceOf(SingletonMap.class, two);
        assertEquals(new SingletonMap<>("2", "Two"), two);
    }

    @Override
    @Test
    public void reject()
    {
        MutableMap<String, String> map = this.classUnderTest();

        MutableMap<String, String> empty = map.reject((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(EmptyMap.class, empty);

        MutableMap<String, String> full = map.reject((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(DoubletonMap.class, full);
        assertEquals(map, full);

        MutableMap<String, String> one = map.reject((argument1, argument2) -> "2".equals(argument1));
        Verify.assertInstanceOf(SingletonMap.class, one);
        assertEquals(new SingletonMap<>("1", "One"), one);

        MutableMap<String, String> two = map.reject((argument1, argument2) -> "1".equals(argument1));
        Verify.assertInstanceOf(SingletonMap.class, two);
        assertEquals(new SingletonMap<>("2", "Two"), two);
    }

    @Override
    @Test
    public void detect()
    {
        MutableMap<String, String> map = this.classUnderTest();

        Pair<String, String> one = map.detect((ignored1, ignored2) -> true);
        assertEquals(Tuples.pair("1", "One"), one);

        Pair<String, String> two = map.detect((argument1, argument2) -> "2".equals(argument1));
        assertEquals(Tuples.pair("2", "Two"), two);

        assertNull(map.detect((ignored1, ignored2) -> false));
    }

    @Override
    protected <K, V> FixedSizeMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return new DoubletonMap<>(key1, value1, key2, value2);
    }

    @Override
    protected <K, V> FixedSizeMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return new DoubletonMap<>(key1, value1, key2, value2);
    }

    @Override
    @Test
    public void iterator()
    {
        MutableList<String> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new DoubletonMap<>(1, "1", 2, "2");
        for (String eachValue : map)
        {
            collection.add(eachValue);
        }
        assertEquals(FastList.newListWith("1", "2"), collection);
    }

    @Test
    public void getOnly()
    {
        assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOnly());
    }
}
