/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.immutable;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link ImmutableDoubletonMap}.
 */
public class ImmutableDoubletonMapTest extends ImmutableMemoryEfficientMapTestCase
{
    @Override
    protected ImmutableMap<Integer, String> classUnderTest()
    {
        return new ImmutableDoubletonMap<>(1, "1", 2, "2");
    }

    @Override
    protected int size()
    {
        return 2;
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        ImmutableMap<Integer, String> map1 = new ImmutableDoubletonMap<>(1, "One", 2, "Two");
        ImmutableMap<Integer, String> map2 = new ImmutableDoubletonMap<>(1, "One", 2, "Two");
        Verify.assertEqualsAndHashCode(map1, map2);
    }

    @Override
    @Test
    public void forEachValue()
    {
        super.forEachValue();
        MutableList<String> collection = Lists.mutable.of();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        map.forEachValue(CollectionAddProcedure.on(collection));
        assertEquals(FastList.newListWith("1", "2"), collection);
    }

    @Override
    @Test
    public void forEachKey()
    {
        super.forEachKey();
        MutableList<Integer> collection = Lists.mutable.of();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        map.forEachKey(CollectionAddProcedure.on(collection));
        assertEquals(FastList.newListWith(1, 2), collection);
    }

    @Override
    @Test
    public void getIfAbsent_function()
    {
        super.getIfAbsent_function();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsent(4, new PassThruFunction0<>("4")));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void getOrDefault()
    {
        super.getOrDefault();

        ImmutableMap<Integer, String> map = this.classUnderTest();
        assertNull(map.get(4));
        assertEquals("4", map.getOrDefault(4, "4"));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        super.getIfAbsent();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsentValue(4, "4"));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void ifPresentApply()
    {
        super.ifPresentApply();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        assertNull(map.ifPresentApply(4, Functions.getPassThru()));
        assertEquals("1", map.ifPresentApply(1, Functions.getPassThru()));
        assertEquals("2", map.ifPresentApply(2, Functions.getPassThru()));
    }

    @Override
    @Test
    public void notEmpty()
    {
        super.notEmpty();
        assertTrue(this.classUnderTest().notEmpty());
    }

    @Override
    @Test
    public void forEachWith()
    {
        super.forEachWith();
        MutableList<Integer> result = Lists.mutable.of();
        ImmutableMap<Integer, Integer> map = new ImmutableDoubletonMap<>(1, 1, 2, 2);
        map.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 10);
        assertEquals(FastList.newListWith(11, 12), result);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();
        MutableList<String> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap<>(1, "One", 2, "Two");
        map.forEachWithIndex((value, index) ->
        {
            result.add(value);
            result.add(String.valueOf(index));
        });
        assertEquals(FastList.newListWith("One", "0", "Two", "1"), result);
    }

    @Override
    @Test
    public void keyValuesView()
    {
        super.keyValuesView();
        MutableList<String> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap<>(1, "One", 2, "Two");
        for (Pair<Integer, String> entry : map.keyValuesView())
        {
            result.add(entry.getTwo());
        }
        assertEquals(FastList.newListWith("One", "Two"), result);
    }

    @Override
    @Test
    public void valuesView()
    {
        super.valuesView();
        MutableList<String> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap<>(1, "One", 2, "Two");
        for (String value : map.valuesView())
        {
            result.add(value);
        }
        assertEquals(FastList.newListWith("One", "Two"), result);
    }

    @Override
    @Test
    public void keysView()
    {
        super.keysView();
        MutableList<Integer> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap<>(1, "One", 2, "Two");
        for (Integer key : map.keysView())
        {
            result.add(key);
        }
        assertEquals(FastList.newListWith(1, 2), result);
    }

    @Override
    @Test
    public void testToString()
    {
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap<>(1, "One", 2, "Two");
        assertEquals("{1=One, 2=Two}", map.toString());
    }

    @Test
    public void asLazyKeys()
    {
        MutableList<Integer> keys = new ImmutableDoubletonMap<>(1, 1, 2, 2).keysView().toSortedList();
        assertEquals(FastList.newListWith(1, 2), keys);
    }

    @Test
    public void asLazyValues()
    {
        MutableList<Integer> values = new ImmutableDoubletonMap<>(1, 1, 2, 2).valuesView().toSortedList();
        assertEquals(FastList.newListWith(1, 2), values);
    }

    @Test
    public void asLazyEntries()
    {
        MutableList<Pair<Integer, Integer>> values =
                new ImmutableDoubletonMap<>(1, 1, 2, 2)
                        .keyValuesView()
                        .toSortedList(Comparators.byFunction((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        assertEquals(FastList.newListWith(Tuples.pair(1, 1), Tuples.pair(2, 2)), values);
    }

    @Test
    public void getOnly()
    {
        assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOnly());
    }

    @Override
    @Test
    public void select()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        ImmutableMap<Integer, String> empty = map.select((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(ImmutableEmptyMap.class, empty);

        ImmutableMap<Integer, String> full = map.select((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, full);
        assertEquals(map, full);

        ImmutableMap<Integer, String> one = map.select((argument1, argument2) -> "1".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, one);
        assertEquals(new ImmutableSingletonMap<>(1, "1"), one);

        ImmutableMap<Integer, String> two = map.select((argument1, argument2) -> "2".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, two);
        assertEquals(new ImmutableSingletonMap<>(2, "2"), two);
    }

    @Override
    @Test
    public void reject()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        ImmutableMap<Integer, String> empty = map.reject((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(ImmutableEmptyMap.class, empty);

        ImmutableMap<Integer, String> full = map.reject((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, full);
        assertEquals(map, full);

        ImmutableMap<Integer, String> one = map.reject((argument1, argument2) -> "2".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, one);
        assertEquals(new ImmutableSingletonMap<>(1, "1"), one);

        ImmutableMap<Integer, String> two = map.reject((argument1, argument2) -> "1".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, two);
        assertEquals(new ImmutableSingletonMap<>(2, "2"), two);
    }

    @Override
    @Test
    public void detect()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        Pair<Integer, String> one = map.detect((ignored1, ignored2) -> true);
        assertEquals(Tuples.pair(1, "1"), one);

        Pair<Integer, String> two = map.detect((argument1, argument2) -> "2".equals(argument2));
        assertEquals(Tuples.pair(2, "2"), two);

        assertNull(map.detect((ignored1, ignored2) -> false));
    }

    @Override
    protected <K, V> ImmutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return new ImmutableDoubletonMap<>(key1, value1, key2, value2);
    }
}
