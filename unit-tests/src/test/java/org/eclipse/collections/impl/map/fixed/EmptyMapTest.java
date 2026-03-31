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

import java.util.NoSuchElementException;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link EmptyMap}.
 */
public class EmptyMapTest extends AbstractMemoryEfficientMutableMapTest
{
    @Override
    protected MutableMap<String, String> classUnderTest()
    {
        return new EmptyMap<>();
    }

    @Override
    protected MutableMap<String, Integer> mixedTypeClassUnderTest()
    {
        return new EmptyMap<>();
    }

    @Override
    @Test
    public void containsValue()
    {
        assertFalse(new EmptyMap<>().containsValue("One"));
    }

    @Test
    public void size()
    {
        Verify.assertEmpty(new EmptyMap<>());
    }

    @Test
    public void empty()
    {
        Verify.assertEmpty(new EmptyMap<>());
        assertFalse(new EmptyMap<>().notEmpty());
        Verify.assertEmpty(new EmptyMap<>());
        assertFalse(new EmptyMap<>().notEmpty());
        Verify.assertEmpty(Maps.fixedSize.of());
        assertFalse(Maps.fixedSize.of().notEmpty());
    }

    @Test
    public void viewsEmpty()
    {
        Verify.assertEmpty(new EmptyMap<>().entrySet());
        Verify.assertEmpty(new EmptyMap<>().values());
        Verify.assertEmpty(new EmptyMap<>().keySet());
    }

    @Test
    public void flipUniqueValues()
    {
        MutableMap<Object, Object> flip = new EmptyMap<>().flipUniqueValues();
        Verify.assertEmpty(flip);
        Verify.assertInstanceOf(EmptyMap.class, flip);
    }

    @Test
    public void testReadResolve()
    {
        Verify.assertInstanceOf(EmptyMap.class, Maps.fixedSize.of());
        Verify.assertPostSerializedIdentity(Maps.fixedSize.of());
    }

    @Override
    @Test
    public void testClone()
    {
        MutableMap<String, String> map = this.classUnderTest();
        assertSame(map, map.clone());
    }

    @Test
    public void iterations()
    {
        StubProcedure<Object> procedure = new StubProcedure<>();
        MutableMap<Object, Object> map = new EmptyMap<>();

        map.each(procedure);
        assertFalse(procedure.called);

        map.forEachKey(procedure);
        assertFalse(procedure.called);

        map.forEachValue(procedure);
        assertFalse(procedure.called);

        map.forEachKeyValue(procedure);
        assertFalse(procedure.called);

        map.forEachWith(procedure, new Object());
        assertFalse(procedure.called);

        map.forEachWithIndex(procedure);
        assertFalse(procedure.called);
    }

    @Override
    @Test
    public void testToString()
    {
        assertEquals("{}", new EmptyMap<Integer, String>().toString());
    }

    @Override
    @Test
    public void testEqualsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(
                UnifiedMap.<String, String>newMap(),
                this.classUnderTest());
    }

    @Override
    @Test
    public void select()
    {
        MutableMap<String, String> map = this.classUnderTest();
        MutableMap<String, String> actual = map.select((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(EmptyMap.class, actual);
    }

    @Override
    @Test
    public void reject()
    {
        MutableMap<String, String> map = this.classUnderTest();
        MutableMap<String, String> actual = map.reject((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(EmptyMap.class, actual);
    }

    @Override
    @Test
    public void detect()
    {
        MutableMap<String, String> map = this.classUnderTest();
        assertNull(map.detect((ignored1, ignored2) -> true));
    }

    @Override
    protected <K, V> FixedSizeMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return new EmptyMap<>();
    }

    @Override
    protected <K, V> FixedSizeMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return new EmptyMap<>();
    }

    @Override
    @Test
    public void allSatisfy()
    {
        MutableMap<String, String> map = this.classUnderTest();
        assertTrue(map.allSatisfy(ignored -> true));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        MutableMap<String, String> map = this.classUnderTest();
        assertFalse(map.anySatisfy(ignored -> true));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        MutableMap<String, String> map = this.classUnderTest();
        assertTrue(map.noneSatisfy(ignored -> true));
    }

    @Override
    @Test
    public void max()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().max());
    }

    @Override
    @Test
    public void maxBy()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().maxBy(Functions.getStringPassThru()));
    }

    @Override
    @Test
    public void min()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().min());
    }

    @Override
    @Test
    public void minBy()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().minBy(Functions.getStringPassThru()));
    }

    private static class StubProcedure<T>
            implements Procedure<T>, Procedure2<T, T>, ObjectIntProcedure<T>
    {
        private static final long serialVersionUID = 1L;
        private boolean called;

        @Override
        public void value(T each)
        {
            this.called = true;
        }

        @Override
        public void value(T argument1, T argument2)
        {
            this.called = true;
        }

        @Override
        public void value(T each, int index)
        {
            this.called = true;
        }
    }

    @Override
    @Test
    public void forEachValue()
    {
        MutableList<String> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new EmptyMap<>();
        map.forEachValue(CollectionAddProcedure.on(collection));
        Verify.assertEmpty(collection);
    }

    @Override
    @Test
    public void forEach()
    {
        MutableList<String> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new EmptyMap<>();
        map.forEach(CollectionAddProcedure.on(collection));
        Verify.assertEmpty(collection);
    }

    @Override
    @Test
    public void forEachKey()
    {
        MutableList<Integer> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new EmptyMap<>();
        map.forEachKey(CollectionAddProcedure.on(collection));
        Verify.assertEmpty(collection);
    }

    @Override
    @Test
    public void forEachWith()
    {
        MutableList<Integer> result = Lists.mutable.of();
        MutableMap<Integer, Integer> map = new EmptyMap<>();
        map.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 10);
        Verify.assertEmpty(result);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableMap<Integer, String> map = new EmptyMap<>();
        map.forEachWithIndex((value, index) ->
        {
            result.add(value);
            result.add(String.valueOf(index));
        });
        Verify.assertEmpty(result);
    }

    @Override
    @Test
    public void forEachKeyValue()
    {
        MutableList<String> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new EmptyMap<>();
        map.forEachKeyValue((key, value) -> collection.add(key + value));
        Verify.assertEmpty(collection);
    }

    @Override
    @Test
    public void asLazyKeys()
    {
        Verify.assertIterableEmpty(this.classUnderTest().keysView());
    }

    @Override
    @Test
    public void asLazyValues()
    {
        Verify.assertIterableEmpty(this.classUnderTest().valuesView());
    }

    @Override
    @Test
    public void getIfAbsentPut()
    {
        MutableMap<Integer, String> map = new EmptyMap<>();
        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut(4, new PassThruFunction0<>("4")));
    }

    @Override
    @Test
    public void getIfAbsentPutWith()
    {
        MutableMap<Integer, String> map = new EmptyMap<>();
        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPutWith(4, String::valueOf, 4));
    }

    @Override
    @Test
    public void getIfAbsent_function()
    {
        MutableMap<Integer, String> map = new EmptyMap<>();
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsent(4, new PassThruFunction0<>("4")));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void getOrDefault()
    {
        MutableMap<Integer, String> map = new EmptyMap<>();
        assertNull(map.get(4));
        assertEquals("4", map.getOrDefault(4, "4"));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        MutableMap<Integer, String> map = new EmptyMap<>();
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsentValue(4, "4"));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsentWith()
    {
        MutableMap<Integer, String> map = new EmptyMap<>();
        assertNull(map.get(4));
        assertEquals("4", map.getIfAbsentWith(4, String::valueOf, 4));
        assertNull(map.get(4));
    }

    @Override
    @Test
    public void ifPresentApply()
    {
        MutableMap<Integer, String> map = new EmptyMap<>();
        assertNull(map.ifPresentApply(4, Functions.getPassThru()));
    }

    @Override
    @Test
    public void notEmpty()
    {
        assertFalse(new EmptyMap<Integer, String>().notEmpty());
    }

    @Override
    @Test
    public void entrySet()
    {
        Verify.assertEmpty(new EmptyMap<Integer, String>().entrySet());
    }

    @Override
    @Test
    public void values()
    {
        Verify.assertEmpty(new EmptyMap<Integer, String>().values());
    }

    @Override
    @Test
    public void keySet()
    {
        Verify.assertEmpty(new EmptyMap<Integer, String>().keySet());
    }

    @Override
    @Test
    public void nonUniqueWithKeyValue()
    {
        // Not applicable for EmptyMap
    }

    @Override
    @Test
    public void withKeyValue()
    {
        MutableMap<Integer, String> map = new EmptyMap<Integer, String>().withKeyValue(1, "A");
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, "A"), map);
        Verify.assertInstanceOf(SingletonMap.class, map);
    }

    @Override
    @Test
    public void withAllKeyValueArguments()
    {
        MutableMap<Integer, String> map1 = new EmptyMap<Integer, String>().withAllKeyValueArguments(Tuples.pair(1, "A"));
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, "A"), map1);
        Verify.assertInstanceOf(SingletonMap.class, map1);

        MutableMap<Integer, String> map2 = new EmptyMap<Integer, String>().withAllKeyValueArguments(Tuples.pair(1, "A"), Tuples.pair(2, "B"));
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, "A", 2, "B"), map2);
        Verify.assertInstanceOf(DoubletonMap.class, map2);
    }

    @Override
    @Test
    public void withoutKey()
    {
        MutableMap<Integer, String> map = new EmptyMap<>();
        MutableMap<Integer, String> mapWithout = map.withoutKey(1);
        assertSame(map, mapWithout);
    }

    @Override
    @Test
    public void withoutAllKeys()
    {
        MutableMap<Integer, String> map = new EmptyMap<>();
        MutableMap<Integer, String> mapWithout = map.withoutAllKeys(FastList.newListWith(1, 2));
        assertSame(map, mapWithout);
    }

    @Override
    @Test
    public void iterator()
    {
        MutableList<String> collection = Lists.mutable.of();
        MutableMap<Integer, String> map = new EmptyMap<>();
        for (String eachValue : map)
        {
            collection.add(eachValue);
        }
        assertEquals(FastList.newListWith(), collection);
    }

    @Test
    public void getOnly()
    {
        assertThrows(IllegalStateException.class, () -> new EmptyMap<>().getOnly());
    }
}
