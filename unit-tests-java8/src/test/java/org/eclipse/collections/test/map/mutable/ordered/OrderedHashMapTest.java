/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable.ordered;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedHashMap;
import org.eclipse.collections.test.CollisionsTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class OrderedHashMapTest implements MutableOrderedMapTestCase
{
    @Override
    public <T> MutableOrderedMap<Object, T> newWith(T... elements)
    {
        int i = elements.length;
        MutableOrderedMap<Object, T> result = new OrderedHashMap<>();
        for (T each : elements)
        {
            assertNull(result.put(i, each));
            i--;
        }
        return result;
    }

    private static int getEntryCount(OrderedHashMap<?, ?> map)
    {
        try
        {
            Field field = OrderedHashMap.class.getDeclaredField("entryCount");
            field.setAccessible(true);
            return (int) field.get(map);
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Test
    public void Map_put()
    {
        MutableOrderedMapTestCase.super.Map_put();

        // Null key support
        OrderedHashMap<String, Integer> nullKeyMap = new OrderedHashMap<>();
        nullKeyMap.put("A", 1);
        nullKeyMap.put(null, 2);
        nullKeyMap.put("B", 3);

        assertEquals(Integer.valueOf(2), nullKeyMap.get(null));
        assertTrue(nullKeyMap.containsKey(null));
        assertEquals(Arrays.asList("A", null, "B"), new ArrayList<>(nullKeyMap.keySet()));

        nullKeyMap.put(null, 99);
        assertEquals(Arrays.asList("A", null, "B"), new ArrayList<>(nullKeyMap.keySet()));
        assertEquals(Integer.valueOf(99), nullKeyMap.get(null));

        assertEquals(Integer.valueOf(99), nullKeyMap.removeKey(null));
        assertFalse(nullKeyMap.containsKey(null));
        assertEquals(List.of("A", "B"), new ArrayList<>(nullKeyMap.keySet()));

        // Null value support
        OrderedHashMap<String, Integer> nullValMap = new OrderedHashMap<>();
        nullValMap.put("A", 1);
        nullValMap.put("B", null);
        nullValMap.put("C", 3);

        assertNull(nullValMap.get("B"));
        assertTrue(nullValMap.containsKey("B"));
        assertTrue(nullValMap.containsValue(null));
        assertEquals(3, nullValMap.size());

        nullValMap.put("B", 2);
        assertEquals(Integer.valueOf(2), nullValMap.get("B"));
        assertFalse(nullValMap.containsValue(null));

        nullValMap.put("A", null);
        assertNull(nullValMap.get("A"));
        assertTrue(nullValMap.containsValue(null));

        // Hash collisions preserve insertion order
        OrderedHashMap<Integer, String> collisionMap = new OrderedHashMap<>();
        for (int i = 0; i < 100; i++)
        {
            collisionMap.put(i, "v" + i);
        }
        List<Integer> keys = new ArrayList<>(collisionMap.keySet());
        for (int i = 0; i < 100; i++)
        {
            assertEquals(Integer.valueOf(i), keys.get(i));
        }

        collisionMap.removeKey(50);
        collisionMap.removeKey(25);
        collisionMap.removeKey(75);
        List<Integer> remaining = new ArrayList<>(collisionMap.keySet());
        int prev = -1;
        for (int key : remaining)
        {
            assertTrue(key > prev);
            prev = key;
        }
    }

    @Override
    @Test
    public void Map_remove()
    {
        MutableOrderedMapTestCase.super.Map_remove();

        // Probe chain: remove and reinsert, key still reachable
        OrderedHashMap<Integer, String> map = new OrderedHashMap<>();
        for (int i = 0; i < 1000; i++)
        {
            map.put(i, "v" + i);
        }
        for (int i = 100; i < 200; i++)
        {
            map.removeKey(i);
        }
        for (int i = 2000; i < 2100; i++)
        {
            map.put(i, "v" + i);
        }
        for (int i = 2000; i < 2100; i++)
        {
            map.removeKey(i);
        }
        for (int i = 0; i < 100; i++)
        {
            assertEquals("v" + i, map.get(i), "Key " + i + " should be reachable");
        }
        for (int i = 200; i < 1000; i++)
        {
            assertEquals("v" + i, map.get(i), "Key " + i + " should be reachable");
        }

        // Stress test: add/remove 4096 different keys
        OrderedHashMap<String, Integer> stressMap = new OrderedHashMap<>();
        for (int i = 0; i < 4096; i++)
        {
            stressMap.put(String.valueOf(i), i);
        }
        assertEquals(4096, stressMap.size());

        List<String> stressKeys = new ArrayList<>(stressMap.keySet());
        for (int i = 0; i < 4096; i++)
        {
            assertEquals(String.valueOf(i), stressKeys.get(i));
        }

        for (int i = 0; i < 4096; i++)
        {
            assertEquals(Integer.valueOf(i), stressMap.removeKey(String.valueOf(i)));
            if (i + 1 < 4096)
            {
                assertEquals(Integer.valueOf(i + 1), stressMap.get(String.valueOf(i + 1)));
            }
        }
        assertTrue(stressMap.isEmpty());

        // Stress test: repeated add/remove same key
        OrderedHashMap<String, Integer> repeatMap = new OrderedHashMap<>();
        for (int i = 0; i < 10_000; i++)
        {
            repeatMap.put("1", 1);
            assertEquals(1, repeatMap.size());
            assertEquals(Integer.valueOf(1), repeatMap.removeKey("1"));
            assertTrue(repeatMap.isEmpty());
        }
        assertEquals(0, getEntryCount(repeatMap));

        // Cascading removal of trailing tombstones
        OrderedHashMap<String, Integer> cascadeMap = new OrderedHashMap<>();
        cascadeMap.put("A", 1);
        cascadeMap.put("B", 2);
        cascadeMap.put("C", 3);

        cascadeMap.removeKey("C");
        assertEquals(2, getEntryCount(cascadeMap));
        assertEquals(2, cascadeMap.size());

        cascadeMap.put("D", 4);
        assertEquals(3, getEntryCount(cascadeMap));
        cascadeMap.removeKey("B");
        assertEquals(3, getEntryCount(cascadeMap));
        assertEquals(2, cascadeMap.size());

        cascadeMap.removeKey("D");
        assertEquals(1, getEntryCount(cascadeMap));
        assertEquals(1, cascadeMap.size());

        assertEquals(Integer.valueOf(1), cascadeMap.get("A"));
        assertNull(cascadeMap.get("B"));
        assertNull(cascadeMap.get("C"));
        assertNull(cascadeMap.get("D"));

        // Cascading stops at live entries
        OrderedHashMap<String, Integer> cascadeMap2 = new OrderedHashMap<>();
        cascadeMap2.put("A", 1);
        cascadeMap2.put("B", 2);
        cascadeMap2.put("C", 3);
        cascadeMap2.put("D", 4);

        cascadeMap2.removeKey("B");
        cascadeMap2.removeKey("D");
        assertEquals(3, getEntryCount(cascadeMap2));

        cascadeMap2.removeKey("C");
        assertEquals(1, getEntryCount(cascadeMap2));

        assertEquals(Integer.valueOf(1), cascadeMap2.get("A"));
        assertEquals(1, cascadeMap2.size());

        // Hash collision: remove first colliding key, second still reachable
        MutableOrderedMap<Integer, String> collisionMap = this.newWithKeysValues(
                CollisionsTestCase.COLLISION_1, "collision1",
                CollisionsTestCase.COLLISION_2, "collision2");
        assertEquals("collision1", collisionMap.get(CollisionsTestCase.COLLISION_1));
        assertEquals("collision2", collisionMap.get(CollisionsTestCase.COLLISION_2));

        collisionMap.remove(CollisionsTestCase.COLLISION_1);
        assertNull(collisionMap.get(CollisionsTestCase.COLLISION_1));
        assertEquals("collision2", collisionMap.get(CollisionsTestCase.COLLISION_2));

        collisionMap.remove(CollisionsTestCase.COLLISION_2);
        assertNull(collisionMap.get(CollisionsTestCase.COLLISION_2));
        assertTrue(collisionMap.isEmpty());

        // Hash collision: remove second colliding key, first still reachable
        MutableOrderedMap<Integer, String> collisionMap2 = this.newWithKeysValues(
                CollisionsTestCase.COLLISION_1, "collision1",
                CollisionsTestCase.COLLISION_2, "collision2",
                CollisionsTestCase.COLLISION_3, "collision3");

        collisionMap2.remove(CollisionsTestCase.COLLISION_2);
        assertEquals("collision1", collisionMap2.get(CollisionsTestCase.COLLISION_1));
        assertNull(collisionMap2.get(CollisionsTestCase.COLLISION_2));
        assertEquals("collision3", collisionMap2.get(CollisionsTestCase.COLLISION_3));

        // Hash collision chain with non-colliding key interspersed
        MutableOrderedMap<Integer, String> collisionMap3 = this.newWithKeysValues(
                CollisionsTestCase.COLLISION_1, "collision1",
                CollisionsTestCase.COLLISION_2, "collision2",
                CollisionsTestCase.COLLISION_3, "collision3",
                100, "hundred");

        collisionMap3.remove(CollisionsTestCase.COLLISION_1);
        collisionMap3.remove(CollisionsTestCase.COLLISION_3);
        assertNull(collisionMap3.get(CollisionsTestCase.COLLISION_1));
        assertEquals("collision2", collisionMap3.get(CollisionsTestCase.COLLISION_2));
        assertNull(collisionMap3.get(CollisionsTestCase.COLLISION_3));
        assertEquals("hundred", collisionMap3.get(100));
    }

    @Test
    public void OrderedHashMap_constructor()
    {
        assertThrows(IllegalArgumentException.class, () -> new OrderedHashMap<>(-1));
        new OrderedHashMap<>(0); // should not throw

        LinkedHashMap<String, Integer> source = new LinkedHashMap<>();
        source.put("C", 3);
        source.put("A", 1);
        source.put("B", 2);
        OrderedHashMap<String, Integer> fromMap = new OrderedHashMap<>(source);
        assertEquals(List.of("C", "A", "B"), new ArrayList<>(fromMap.keySet()));
        assertEquals(List.of(3, 1, 2), new ArrayList<>(fromMap.values()));

        source.put("D", 4);
        assertEquals(3, fromMap.size());

        OrderedHashMap<String, Integer> mapIterableSource = new OrderedHashMap<>();
        mapIterableSource.put("C", 3);
        mapIterableSource.put("A", 1);
        mapIterableSource.put("B", 2);
        OrderedHashMap<String, Integer> fromMapIterable = new OrderedHashMap<>((MapIterable<String, Integer>) mapIterableSource);
        assertEquals(List.of("C", "A", "B"), new ArrayList<>(fromMapIterable.keySet()));

        mapIterableSource.put("D", 4);
        assertEquals(3, fromMapIterable.size());

        OrderedHashMap<String, Integer> fromEmpty = new OrderedHashMap<>(new LinkedHashMap<>());
        assertTrue(fromEmpty.isEmpty());
    }

    @Test
    public void OrderedHashMap_newMap()
    {
        OrderedHashMap<String, Integer> map = OrderedHashMap.newMap();
        assertEquals(0, map.size());
        assertEquals(List.of(), new ArrayList<>(map.keySet()));
    }

    @Test
    public void OrderedHashMap_newWithKeysValues()
    {
        OrderedHashMap<String, Integer> one = OrderedHashMap.newWithKeysValues("A", 1);
        assertEquals(1, one.size());
        assertEquals(List.of("A"), new ArrayList<>(one.keySet()));
        assertEquals(List.of(1), new ArrayList<>(one.values()));

        OrderedHashMap<String, Integer> two = OrderedHashMap.newWithKeysValues("A", 1, "B", 2);
        assertEquals(2, two.size());
        assertEquals(List.of("A", "B"), new ArrayList<>(two.keySet()));
        assertEquals(List.of(1, 2), new ArrayList<>(two.values()));

        OrderedHashMap<String, Integer> three = OrderedHashMap.newWithKeysValues("A", 1, "B", 2, "C", 3);
        assertEquals(3, three.size());
        assertEquals(List.of("A", "B", "C"), new ArrayList<>(three.keySet()));
        assertEquals(List.of(1, 2, 3), new ArrayList<>(three.values()));

        OrderedHashMap<String, Integer> four = OrderedHashMap.newWithKeysValues("A", 1, "B", 2, "C", 3, "D", 4);
        assertEquals(4, four.size());
        assertEquals(List.of("A", "B", "C", "D"), new ArrayList<>(four.keySet()));
        assertEquals(List.of(1, 2, 3, 4), new ArrayList<>(four.values()));
    }

    @Test
    public void OrderedHashMap_serialization() throws Exception
    {
        OrderedHashMap<String, Integer> map = new OrderedHashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(map);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        @SuppressWarnings("unchecked")
        OrderedHashMap<String, Integer> deserialized = (OrderedHashMap<String, Integer>) ois.readObject();

        assertEquals(map, deserialized);
        assertEquals(List.of("A", "B", "C"), new ArrayList<>(deserialized.keySet()));
        assertEquals(List.of(1, 2, 3), new ArrayList<>(deserialized.values()));

        // Empty map
        OrderedHashMap<String, Integer> emptyMap = new OrderedHashMap<>();

        ByteArrayOutputStream emptyBaos = new ByteArrayOutputStream();
        ObjectOutputStream emptyOos = new ObjectOutputStream(emptyBaos);
        emptyOos.writeObject(emptyMap);
        emptyOos.close();

        ObjectInputStream emptyOis = new ObjectInputStream(new ByteArrayInputStream(emptyBaos.toByteArray()));
        @SuppressWarnings("unchecked")
        OrderedHashMap<String, Integer> emptyDeserialized = (OrderedHashMap<String, Integer>) emptyOis.readObject();

        assertTrue(emptyDeserialized.isEmpty());
    }

    @Test
    public void OrderedHashMap_clone()
    {
        OrderedHashMap<String, Integer> map = new OrderedHashMap<>();
        map.put("A", 1);
        assertThrows(UnsupportedOperationException.class, () -> map.clone());
    }

    @Override
    public <K, V> MutableOrderedMap<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        MutableOrderedMap<K, V> result = new OrderedHashMap<>();
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return result;
    }
}
