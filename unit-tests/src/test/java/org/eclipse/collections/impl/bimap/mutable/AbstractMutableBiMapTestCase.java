/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bimap.mutable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.bimap.BiMap;
import org.eclipse.collections.api.bimap.ImmutableBiMap;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.IntegerWithCast;
import org.eclipse.collections.impl.factory.BiMaps;
import org.eclipse.collections.impl.map.mutable.MutableMapIterableTestCase;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractMutableBiMapTestCase extends MutableMapIterableTestCase
{
    public abstract MutableBiMap<Integer, Character> classUnderTest();

    public abstract MutableBiMap<Integer, Character> getEmptyMap();

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMap();

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMapWithKeyValue(K key, V value);

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2);

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3);

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4);

    public static void assertBiMapsEqual(BiMap<?, ?> expected, BiMap<?, ?> actual)
    {
        assertEquals(expected, actual);
        assertEquals(expected.inverse(), actual.inverse());
    }

    @Test
    @Override
    public void flip()
    {
        Verify.assertEmpty(this.newMap().flip());

        MutableSetMultimap<Integer, String> expected = UnifiedSetMultimap.newMultimap();
        expected.put(1, "One");
        expected.put(2, "Two");
        expected.put(3, "Three");
        expected.put(4, "Four");

        assertEquals(
                expected,
                this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3, "Four", 4).flip());
    }

    @Test
    public void size()
    {
        Verify.assertSize(3, this.classUnderTest());
        Verify.assertSize(0, this.getEmptyMap());
    }

    @Test
    public void forcePut()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        assertNull(biMap.forcePut(4, 'd'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);
        assertEquals(UnifiedMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);

        assertNull(biMap.forcePut(1, null));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);

        assertNull(biMap.forcePut(1, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        assertNull(biMap.forcePut(5, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(5, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        assertEquals(Character.valueOf('d'), biMap.forcePut(4, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(4, 'e', null, 'b', 3, 'c'), biMap);

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        actual.forcePut(1, null);
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null), actual);
    }

    @Test
    public void put()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        assertNull(biMap.put(4, 'd'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);
        assertEquals(UnifiedMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);

        assertNull(biMap.put(1, null));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);

        assertNull(biMap.put(1, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        assertThrows(IllegalArgumentException.class, () -> biMap.put(5, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        assertThrows(IllegalArgumentException.class, () -> biMap.put(4, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        actual.put(1, null);
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null), actual);
    }

    @Override
    @Test
    public void flipUniqueValues()
    {
        MutableBiMap<Integer, Character> map = this.classUnderTest();
        MutableBiMap<Character, Integer> result = map.flipUniqueValues();
        assertEquals(map.inverse(), result);
        assertNotSame(map.inverse(), result);
        result.put('d', 4);
        assertEquals(this.classUnderTest(), map);
    }

    @Test
    public void get()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        assertNull(biMap.get(1));
        assertEquals(Character.valueOf('b'), biMap.get(null));
        assertEquals(Character.valueOf('c'), biMap.get(3));
        assertNull(biMap.get(4));

        assertNull(biMap.put(4, 'd'));
        assertNull(biMap.get(1));
        assertEquals(Character.valueOf('b'), biMap.get(null));
        assertEquals(Character.valueOf('c'), biMap.get(3));
        assertEquals(Character.valueOf('d'), biMap.get(4));

        assertNull(biMap.put(1, null));
        assertNull(biMap.get(1));
        assertEquals(Character.valueOf('b'), biMap.get(null));
        assertEquals(Character.valueOf('c'), biMap.get(3));
        assertEquals(Character.valueOf('d'), biMap.get(4));

        assertNull(biMap.forcePut(1, 'e'));
        assertEquals(Character.valueOf('e'), biMap.get(1));
        assertEquals(Character.valueOf('b'), biMap.get(null));
        assertEquals(Character.valueOf('c'), biMap.get(3));
        assertEquals(Character.valueOf('d'), biMap.get(4));

        assertNull(biMap.forcePut(5, 'e'));
        assertNull(biMap.get(1));
        assertEquals(Character.valueOf('e'), biMap.get(5));
        assertEquals(Character.valueOf('b'), biMap.get(null));
        assertEquals(Character.valueOf('c'), biMap.get(3));
        assertEquals(Character.valueOf('d'), biMap.get(4));

        assertEquals(Character.valueOf('d'), biMap.forcePut(4, 'e'));
        assertNull(biMap.get(1));
        assertNull(biMap.get(5));
        assertEquals(Character.valueOf('b'), biMap.get(null));
        assertEquals(Character.valueOf('c'), biMap.get(3));
        assertEquals(Character.valueOf('e'), biMap.get(4));

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        assertNull(actual.get(1));
        actual.put(1, null);
        assertNull(actual.get(1));
    }

    @Override
    @Test
    public void containsKey()
    {
        super.containsKey();

        MutableBiMap<Integer, Character> biMap = this.classUnderTest();

        assertTrue(biMap.containsKey(1));
        assertTrue(biMap.containsKey(null));
        assertTrue(biMap.containsKey(3));
        assertFalse(biMap.containsKey(4));

        assertNull(biMap.put(4, 'd'));
        assertTrue(biMap.containsKey(1));
        assertTrue(biMap.containsKey(null));
        assertTrue(biMap.containsKey(3));
        assertTrue(biMap.containsKey(4));

        assertNull(biMap.put(1, null));
        assertTrue(biMap.containsKey(1));
        assertTrue(biMap.containsKey(null));
        assertTrue(biMap.containsKey(3));
        assertTrue(biMap.containsKey(4));

        assertNull(biMap.forcePut(1, 'e'));
        assertTrue(biMap.containsKey(1));
        assertTrue(biMap.containsKey(null));
        assertTrue(biMap.containsKey(3));
        assertTrue(biMap.containsKey(4));

        assertNull(biMap.forcePut(5, 'e'));
        assertFalse(biMap.containsKey(1));
        assertTrue(biMap.containsKey(5));
        assertTrue(biMap.containsKey(null));
        assertTrue(biMap.containsKey(3));
        assertTrue(biMap.containsKey(4));

        assertEquals(Character.valueOf('d'), biMap.forcePut(4, 'e'));
        assertFalse(biMap.containsKey(1));
        assertTrue(biMap.containsKey(null));
        assertTrue(biMap.containsKey(3));
        assertTrue(biMap.containsKey(4));
        assertFalse(biMap.containsKey(5));

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        actual.put(1, null);
        assertTrue(actual.containsKey(1));
        assertFalse(actual.containsKey(0));
    }

    @Override
    @Test
    public void containsValue()
    {
        super.containsValue();

        MutableBiMap<Integer, Character> biMap = this.classUnderTest();

        assertTrue(biMap.containsValue(null));
        assertTrue(biMap.containsValue('b'));
        assertTrue(biMap.containsValue('c'));
        assertFalse(biMap.containsValue('d'));

        assertNull(biMap.put(4, 'd'));
        assertTrue(biMap.containsValue(null));
        assertTrue(biMap.containsValue('b'));
        assertTrue(biMap.containsValue('c'));
        assertTrue(biMap.containsValue('d'));

        assertNull(biMap.put(1, null));
        assertTrue(biMap.containsValue(null));
        assertTrue(biMap.containsValue('b'));
        assertTrue(biMap.containsValue('c'));
        assertTrue(biMap.containsValue('d'));

        assertNull(biMap.forcePut(1, 'e'));
        assertTrue(biMap.containsValue('e'));
        assertFalse(biMap.containsValue(null));
        assertTrue(biMap.containsValue('b'));
        assertTrue(biMap.containsValue('c'));
        assertTrue(biMap.containsValue('d'));

        assertNull(biMap.forcePut(5, 'e'));
        assertFalse(biMap.containsValue(null));
        assertTrue(biMap.containsValue('e'));
        assertTrue(biMap.containsValue('b'));
        assertTrue(biMap.containsValue('c'));
        assertTrue(biMap.containsValue('d'));

        assertEquals(Character.valueOf('d'), biMap.forcePut(4, 'e'));
        assertFalse(biMap.containsValue(null));
        assertTrue(biMap.containsValue('e'));
        assertTrue(biMap.containsValue('b'));
        assertTrue(biMap.containsValue('c'));
        assertFalse(biMap.containsValue('d'));

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        actual.put(1, null);
        assertTrue(actual.containsValue(null));
        assertFalse(actual.containsValue('\0'));
    }

    @Override
    @Test
    public void putAll()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        biMap.putAll(UnifiedMap.newMap());
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c'), biMap);

        biMap.putAll(UnifiedMap.newWithKeysValues(1, null, null, 'b', 3, 'c'));
        HashBiMap<Integer, Character> expected = HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c');
        assertEquals(expected, biMap);

        biMap.putAll(UnifiedMap.newWithKeysValues(4, 'd', 5, 'e', 6, 'f'));
        expected.put(4, 'd');
        expected.put(5, 'e');
        expected.put(6, 'f');
        assertEquals(expected, biMap);
    }

    @Test
    public void remove()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        assertNull(biMap.remove(4));
        Verify.assertSize(3, biMap);
        assertNull(biMap.remove(1));
        assertNull(biMap.get(1));
        assertNull(biMap.inverse().get(null));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(null, 'b', 3, 'c'), biMap);

        assertEquals(Character.valueOf('b'), biMap.remove(null));
        assertNull(biMap.get(null));
        assertNull(biMap.inverse().get('b'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(3, 'c'), biMap);

        assertEquals(Character.valueOf('c'), biMap.remove(3));
        assertNull(biMap.get(3));
        assertNull(biMap.inverse().get('c'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newMap(), biMap);
        Verify.assertEmpty(biMap);

        assertNull(HashBiMap.newMap().remove(1));
    }

    @Override
    @Test
    public void clear()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        biMap.clear();
        Verify.assertEmpty(biMap);
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newMap(), biMap);
    }

    @Test
    public void testToString()
    {
        assertEquals("{}", this.getEmptyMap().toString());
        String actualString = HashBiMap.newWithKeysValues(1, null, 2, 'b').toString();
        assertTrue("{1=null, 2=b}".equals(actualString) || "{2=b, 1=null}".equals(actualString));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();

        MutableBiMap<Integer, Character> emptyMap = this.getEmptyMap();
        Verify.assertEqualsAndHashCode(UnifiedMap.newMap(), emptyMap);
        assertEquals(emptyMap, emptyMap);
        Verify.assertEqualsAndHashCode(UnifiedMap.newWithKeysValues(1, null, null, 'b', 3, 'c'), this.classUnderTest());
        Verify.assertEqualsAndHashCode(UnifiedMap.newWithKeysValues(null, 'b', 1, null, 3, 'c'), this.classUnderTest());
        assertNotEquals(HashBiMap.newWithKeysValues(null, 1, 'b', null, 'c', 3), this.classUnderTest());
        Verify.assertEqualsAndHashCode(HashBiMap.newWithKeysValues(null, 1, 'b', null, 'c', 3), this.classUnderTest().inverse());
    }

    @Override
    @Test
    public void nullCollisionWithCastInEquals()
    {
        MutableBiMap<IntegerWithCast, String> mutableMap = this.newMap();
        mutableMap.put(new IntegerWithCast(0), "Test 2");
        mutableMap.forcePut(new IntegerWithCast(0), "Test 3");
        mutableMap.put(null, "Test 1");
        assertEquals(
                this.newMapWithKeysValues(
                        new IntegerWithCast(0), "Test 3",
                        null, "Test 1"),
                mutableMap);
        assertEquals("Test 3", mutableMap.get(new IntegerWithCast(0)));
        assertEquals("Test 1", mutableMap.get(null));
    }

    @Override
    @Test
    public void iterator()
    {
        MutableSet<Character> expected = UnifiedSet.newSetWith(null, 'b', 'c');
        MutableSet<Character> actual = UnifiedSet.newSet();
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        Iterator<Character> iterator = biMap.iterator();
        assertTrue(iterator.hasNext());
        assertThrows(IllegalStateException.class, iterator::remove);
        Verify.assertSize(3, biMap);
        Verify.assertSize(3, biMap.inverse());
        for (int i = 0; i < 3; i++)
        {
            assertTrue(iterator.hasNext());
            actual.add(iterator.next());
        }
        assertEquals(expected, actual);
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);

        Iterator<Character> iteratorRemove = biMap.iterator();

        assertTrue(iteratorRemove.hasNext());
        Character first = iteratorRemove.next();
        iteratorRemove.remove();
        MutableBiMap<Integer, Character> expectedMap = this.classUnderTest();
        expectedMap.inverse().remove(first);
        assertEquals(expectedMap, biMap);
        assertEquals(expectedMap.inverse(), biMap.inverse());
        Verify.assertSize(2, biMap);
        Verify.assertSize(2, biMap.inverse());

        assertTrue(iteratorRemove.hasNext());
        Character second = iteratorRemove.next();
        iteratorRemove.remove();
        expectedMap.inverse().remove(second);
        assertEquals(expectedMap, biMap);
        assertEquals(expectedMap.inverse(), biMap.inverse());
        Verify.assertSize(1, biMap);
        Verify.assertSize(1, biMap.inverse());

        assertTrue(iteratorRemove.hasNext());
        Character third = iteratorRemove.next();
        iteratorRemove.remove();
        expectedMap.inverse().remove(third);
        assertEquals(expectedMap, biMap);
        assertEquals(expectedMap.inverse(), biMap.inverse());
        Verify.assertEmpty(biMap);
        Verify.assertEmpty(biMap.inverse());

        assertFalse(iteratorRemove.hasNext());
        assertThrows(NoSuchElementException.class, iteratorRemove::next);
    }

    @Override
    @Test
    public void withMapNull()
    {
        assertThrows(NullPointerException.class, () -> this.newMap().withMap(null));
    }

    @Override
    @Test
    public void updateValueWith()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        Function2<Character, Boolean, Character> toUpperOrLowerCase = (character, parameter) -> parameter
                ? Character.toUpperCase(character)
                : Character.toLowerCase(character);
        assertEquals(Character.valueOf('D'), biMap.updateValueWith(4, () -> 'd', toUpperOrLowerCase, true));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'D'), biMap);
        assertEquals(Character.valueOf('B'), biMap.updateValueWith(null, () -> 'd', toUpperOrLowerCase, true));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'B', 3, 'c', 4, 'D'), biMap);
        assertEquals(Character.valueOf('d'), biMap.updateValueWith(4, () -> 'x', toUpperOrLowerCase, false));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'B', 3, 'c', 4, 'd'), biMap);
    }

    @Override
    @Test
    public void updateValue()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        assertEquals(Character.valueOf('D'), biMap.updateValue(4, () -> 'd', Character::toUpperCase));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'D'), biMap);
        assertEquals(Character.valueOf('B'), biMap.updateValue(null, () -> 'd', Character::toUpperCase));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'B', 3, 'c', 4, 'D'), biMap);
    }

    @Override
    @Test
    public void updateValue_collisions()
    {
        // testing collisions not applicable here
    }

    @Override
    @Test
    public void updateValueWith_collisions()
    {
        // testing collisions not applicable here
    }

    @Override
    @Test
    public void toImmutable()
    {
        ImmutableBiMap<Integer, Character> expectedImmutableBiMap = BiMaps.immutable.of(null, 'b', 1, null, 3, 'c');
        ImmutableBiMap<Integer, Character> characters = this.classUnderTest().toImmutable();
        assertEquals(expectedImmutableBiMap, characters);
    }

    @Test
    public void testClone()
    {
        MutableBiMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        MutableBiMap<Integer, String> clone = map.clone();
        assertNotSame(map, clone);
        Verify.assertEqualsAndHashCode(map, clone);
    }

    @Test
    public void into()
    {
        MutableBiMap<Integer, Character> map = this.newMapWithKeysValues(1, 'a', 2, 'b');
        MutableSet<Character> target = Sets.mutable.of('c');
        map.into(target);
        Verify.assertSetsEqual(Sets.mutable.of('a', 'b', 'c'), target);
    }
}
