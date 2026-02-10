/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iMap;
import static org.eclipse.collections.impl.factory.Iterables.mList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class UnifiedMapTestCase extends MutableMapTestCase
{
    protected static final Integer COLLISION_1 = 0;
    protected static final Integer COLLISION_2 = 17;
    protected static final Integer COLLISION_3 = 34;
    protected static final Integer COLLISION_4 = 51;
    protected static final Integer COLLISION_5 = 68;
    protected static final Integer COLLISION_6 = 85;
    protected static final Integer COLLISION_7 = 102;
    protected static final Integer COLLISION_8 = 119;
    protected static final Integer COLLISION_9 = 136;
    protected static final Integer COLLISION_10 = 152;
    protected static final MutableList<Integer> COLLISIONS =
            Lists.mutable.of(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5);
    protected static final MutableList<Integer> MORE_COLLISIONS = FastList.newList(COLLISIONS)
            .with(COLLISION_6, COLLISION_7, COLLISION_8, COLLISION_9);
    protected static final String[] FREQUENT_COLLISIONS = {"\u9103\ufffe", "\u9104\uffdf",
            "\u9105\uffc0", "\u9106\uffa1", "\u9107\uff82", "\u9108\uff63", "\u9109\uff44",
            "\u910a\uff25", "\u910b\uff06", "\u910c\ufee7"};

    @Test
    public void valuesCollection_toArray()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One").asUnmodifiable();
        Object[] values = map.values().toArray();
        Verify.assertItemAtIndex("One", 0, values);

        // map containing chain
        MutableMap<Integer, Integer> chainedMap = this.mapWithCollisionsOfSize(2);
        Object[] chainedValues = chainedMap.values().toArray();
        Arrays.sort(chainedValues);
        assertArrayEquals(new Integer[]{COLLISION_1, COLLISION_2}, chainedValues);

        // map containing chain with empty slots
        MutableMap<Integer, Integer> chainedMapWithEmpties = this.mapWithCollisionsOfSize(3);
        Object[] chainedValuesWithEmpties = chainedMapWithEmpties.values().toArray();
        Arrays.sort(chainedValuesWithEmpties);
        assertArrayEquals(new Integer[]{COLLISION_1, COLLISION_2, COLLISION_3}, chainedValuesWithEmpties);
    }

    @Test
    public void valuesCollection_toArray_WithEmptyTarget()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        String[] values = map.values().toArray(new String[0]);
        assertArrayEquals(new String[]{"One"}, values);

        Object[] objects = map.values().toArray(new Object[0]);
        assertArrayEquals(new String[]{"One"}, objects);
    }

    @Test
    public void valuesCollection_toArray_withPreSizedTarget()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        String[] values = map.values().toArray(new String[2]);
        Arrays.sort(values);
        assertArrayEquals(new String[]{"One", "Two"}, values);

        String[] target = new String[3];
        target[0] = "HERE";
        target[1] = "HERE";
        target[2] = "HERE";
        String[] array = this.newMapWithKeyValue(1, "One").values().toArray(target);
        assertArrayEquals(new String[]{"One", null, "HERE"}, array);
    }

    @Test
    public void valuesCollection_toArray_withLargeTarget()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        String[] target = new String[3];
        target[2] = "yow!";
        String[] values = map.values().toArray(target);
        ArrayIterate.sort(values, values.length, Comparators.safeNullsHigh(String::compareTo));
        assertArrayEquals(new String[]{"One", "Two", null}, values);
    }

    @Test
    public void entrySet_clear()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        Set<Map.Entry<Integer, String>> entries = map.entrySet();
        entries.clear();
        Verify.assertEmpty(entries);
        Verify.assertEmpty(map);
    }

    @Test
    public void valuesCollection_clear()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three");
        Collection<String> values = map.values();
        values.clear();
        Verify.assertEmpty(values);
        Verify.assertEmpty(map);
    }

    @Test
    public void keySet_toArray_withSmallTarget()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        Integer[] destination = new Integer[2]; // deliberately to small to force the method to allocate one of the correct size
        Integer[] result = map.keySet().toArray(destination);
        Arrays.sort(result);
        assertArrayEquals(new Integer[]{1, 2, 3, 4}, result);
    }

    @Test
    public void keySet_ToArray_withLargeTarget()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        Integer[] target = new Integer[6]; // deliberately large to force the extra to be set to null
        target[4] = 42;
        target[5] = 42;
        Integer[] result = map.keySet().toArray(target);
        ArrayIterate.sort(result, result.length, Comparators.safeNullsHigh(Integer::compareTo));
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 42, null}, result);
    }

    @Test
    public void noInstanceOfEquals()
    {
        MutableMap<NoInstanceOfInEquals, Integer> map = this.newMap();

        map.put(new NoInstanceOfInEquals(10), 12);
        map.put(new NoInstanceOfInEquals(12), 15);
        map.put(new NoInstanceOfInEquals(14), 18);

        assertEquals(3, map.size());
    }

    @Test
    public void keySet_hashCode()
    {
        // a map with a null key
        MutableMap<Integer, Integer> map1 = this.newMapWithKeyValue(null, 0);
        MutableSet<Object> set = UnifiedSet.newSet();
        set.add(null);
        Verify.assertEqualsAndHashCode(set, map1.keySet());

        // a map with a chain containing empty slots
        MutableMap<Integer, Integer> map2 = this.mapWithCollisionsOfSize(5);
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith(0, 17, 34, 51, 68), map2.keySet());

        // a map with a chain containing empty slots and a null key
        MutableMap<Integer, Integer> map3 = this.mapWithCollisionsOfSize(5);
        map3.put(null, 42);
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith(0, 17, 34, 51, 68, null), map3.keySet());
    }

    @Test
    public void keySet_copyKeys()
    {
        // a map with a null key
        MutableMap<Integer, Integer> map1 = this.newMapWithKeyValue(null, 0);
        assertArrayEquals(new Object[]{null}, map1.keySet().toArray());

        // a map with a chain containing empty slots
        MutableMap<Integer, Integer> map2 = this.mapWithCollisionsOfSize(5);
        assertArrayEquals(new Object[]{0, 17, 34, 51, 68}, map2.keySet().toArray());

        // a map with a chain containing empty slots and null key
        MutableMap<Integer, Integer> map3 = this.mapWithCollisionsOfSize(5);
        map3.put(null, 42);
        Integer[] array = map3.keySet().toArray(new Integer[map3.size()]);
        ArrayIterate.sort(array, array.length, Comparators.safeNullsHigh(Integer::compareTo));
        assertArrayEquals(new Object[]{0, 17, 34, 51, 68, null}, array);
    }

    @Test
    public void entrySet_toArray()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Object[] entries = map.entrySet().toArray();
        assertArrayEquals(new Map.Entry[]{ImmutableEntry.of(1, "One")}, entries);
    }

    @Test
    public void entrySet_toArray_withEmptyTarget()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Map.Entry<Integer, String>[] entries = map.entrySet().toArray(new Map.Entry[0]);
        assertArrayEquals(new Map.Entry[]{ImmutableEntry.of(1, "One")}, entries);

        Object[] objects = map.entrySet().toArray(new Object[0]);
        assertArrayEquals(new Map.Entry[]{ImmutableEntry.of(1, "One")}, objects);
    }

    @Test
    public void entrySet_toArray_withPreSizedTarget()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Map.Entry<Integer, String>[] entries = map.entrySet().toArray(new Map.Entry[map.size()]);
        assertArrayEquals(new Map.Entry[]{ImmutableEntry.of(1, "One")}, entries);
    }

    @Test
    public void entrySet_toArray_withLargeTarget()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Map.Entry<Integer, String>[] target = new Map.Entry[4];
        Map.Entry<Integer, String> immutableEntry = new ImmutableEntry<>(null, null);
        target[1] = immutableEntry;
        target[2] = immutableEntry;
        target[3] = immutableEntry;
        Map.Entry<Integer, String>[] entries = map.entrySet().toArray(target);
        assertArrayEquals(new Map.Entry[]{ImmutableEntry.of(1, "One"), null, immutableEntry, immutableEntry}, entries);
    }

    protected MutableMap<Integer, Integer> mapWithCollisionsOfSize(int size)
    {
        MutableMap<Integer, Integer> map = this.newMap();
        return this.populateMapWithCollisionsOfSize(size, map);
    }

    protected <M extends MutableMap<Integer, Integer>> M populateMapWithCollisionsOfSize(int size, M map)
    {
        MORE_COLLISIONS.subList(0, size).forEach(Procedures.cast(each -> map.put(each, each)));
        return map;
    }

    @Test
    public void entrySet_PostSerializedEqualsAndHashCode()
    {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Verify.assertPostSerializedEqualsAndHashCode(map.entrySet());
    }

    @Test
    public void keySet_PostSerializedEqualsAndHashCode()
    {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Verify.assertPostSerializedEqualsAndHashCode(map.keySet());
    }

    @Test
    public void keySet_PostSerializedEqualsAndHashCode_chainWithEmptySlot()
    {
        MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(5);
        Verify.assertPostSerializedEqualsAndHashCode(map.keySet());
    }

    @Test
    public void valuesCollection_PostSerializedEquality()
    {
        MutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        Collection<Integer> values = map.values();
        // This test is not using Verify.assertPostSerializedEqualsAndHashCode b/c the deserialized form of the values view is a FastList, which will not be equals to the original view (a Collection).
        Collection<Integer> revived = SerializeTestHelper.serializeDeserialize(values);
        assertNotNull(revived);
        Verify.assertSize(values.size(), revived);
        assertTrue(revived.containsAll(values));
    }

    @Test
    public void valuesCollection_PostSerializedEquality_chainedMap()
    {
        MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(2);
        Collection<Integer> values = map.values();
        Collection<Integer> revived = SerializeTestHelper.serializeDeserialize(values);
        assertNotNull(revived);
        Verify.assertSize(values.size(), revived);
        assertTrue(revived.containsAll(values));
    }

    @Test
    public void valuesCollection_PostSerializedEquality_chainedMapWithEmptySlot()
    {
        MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(4);
        map.put(42, 42); // add non-colliding key
        Collection<Integer> values = map.values();
        // This test is not using Verify.assertPostSerializedEqualsAndHashCode b/c the deserialized form of the values view is a FastList, which will not be equals to the orginal view (a Collection).
        Collection<Integer> revived = SerializeTestHelper.serializeDeserialize(values);
        assertNotNull(revived);
        Verify.assertSize(values.size(), revived);
        assertTrue(revived.containsAll(values));
    }

    @Test
    public void contains_key_and_value()
    {
        for (int i = 1; i < COLLISIONS.size(); i++)
        {
            MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(i);

            assertTrue(map.containsKey(COLLISIONS.get(i - 1)));
            assertTrue(map.containsValue(COLLISIONS.get(i - 1)));
            assertFalse(map.containsKey(COLLISION_10));
            assertFalse(map.containsValue(COLLISION_10));
            assertFalse(map.containsKey(null));
            assertFalse(map.containsValue(null));

            map.put(null, null);
            assertTrue(map.containsKey(null));
            assertTrue(map.containsValue(null));
        }
    }

    @Test
    public void remove()
    {
        for (int i = 1; i < COLLISIONS.size(); i++)
        {
            MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(i);
            assertNull(map.put(null, null));
            assertNull(map.remove(null));
            assertNull(map.remove(COLLISION_10));
            Integer biggestValue = COLLISIONS.get(i - 1);
            assertEquals(biggestValue, map.remove(biggestValue));
        }
    }

    @Test
    public void getIfAbsentPutValueWithCollisions()
    {
        MutableMap<Integer, Object> map = this.newMapWithKeyValue(COLLISION_1, null);
        assertNull(map.getIfAbsentPut(COLLISION_1, 5));
        assertNull(map.getIfAbsentPut(COLLISION_3, (Integer) null));
        assertNull(map.getIfAbsentPut(COLLISION_3, 7));
        assertEquals(Integer.valueOf(9), map.getIfAbsentPut(COLLISION_2, 9));
        assertEquals(Integer.valueOf(10), map.getIfAbsentPut(COLLISION_4, 10));
        assertEquals(UnifiedMap.newWithKeysValues(COLLISION_1, null, COLLISION_2, 9, COLLISION_3, null, COLLISION_4, 10), map);
    }

    @Test
    public void getIfAbsentPutWithWithCollisions()
    {
        MutableMap<Integer, Object> map = this.newMapWithKeyValue(COLLISION_1, null);
        assertNull(map.getIfAbsentPutWith(COLLISION_1, String::valueOf, 5));
        assertNull(map.getIfAbsentPutWith(COLLISION_3, Functions.getPassThru(), null));
        assertNull(map.getIfAbsentPutWith(COLLISION_3, String::valueOf, 7));
        assertEquals("9", map.getIfAbsentPutWith(COLLISION_2, String::valueOf, 9));
        assertEquals(Integer.valueOf(10), map.getIfAbsentPutWith(COLLISION_4, Functions.getIntegerPassThru(), 10));
        assertEquals(UnifiedMap.newWithKeysValues(COLLISION_1, null, COLLISION_2, "9", COLLISION_3, null, COLLISION_4, 10), map);
    }

    @Override
    @Test
    public void removeFromEntrySet()
    {
        super.removeFromEntrySet();

        for (int i = 1; i < COLLISIONS.size(); i++)
        {
            MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(i);

            Integer biggestValue = COLLISIONS.get(i - 1);

            assertTrue(map.entrySet().remove(ImmutableEntry.of(biggestValue, biggestValue)));
            assertEquals(this.mapWithCollisionsOfSize(i - 1), map);

            assertFalse(map.entrySet().remove(ImmutableEntry.of(COLLISION_10, COLLISION_10)));
            assertEquals(this.mapWithCollisionsOfSize(i - 1), map);

            assertFalse(map.entrySet().remove(null));
        }
    }

    @Override
    @Test
    public void retainAllFromEntrySet()
    {
        super.retainAllFromEntrySet();

        for (int i = 1; i < COLLISIONS.size(); i++)
        {
            MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(i);

            assertFalse(map.entrySet().retainAll(
                    FastList.newList(map.entrySet()).with(ImmutableEntry.of(COLLISION_10, COLLISION_10))));

            assertTrue(map.entrySet().retainAll(
                    this.mapWithCollisionsOfSize(i - 1).entrySet()));
            assertEquals(this.mapWithCollisionsOfSize(i - 1), map);
        }

        MutableList<Holder<Integer>> holderCollisions = MORE_COLLISIONS.collect(Holder::new);
        for (Holder<Integer> item : holderCollisions)
        {
            MutableMap<Holder<Integer>, Holder<Integer>> holders = this.<Holder<Integer>, Holder<Integer>>newMap();
            holderCollisions.each(collision -> holders.put(collision, collision));
            Holder<Integer> keyCopy = new Holder<>(item.getValue());
            assertTrue(holders.entrySet().retainAll(mList(ImmutableEntry.of(keyCopy, keyCopy))));
            assertEquals(iMap(keyCopy, keyCopy), holders);
            assertNotSame(keyCopy, Iterate.getOnly(holders.entrySet()).getKey());
        }

        // simple map, collection to retain contains non-entry element
        MutableMap<Integer, String> map4 = this.newMapWithKeysValues(1, "One", 2, "Two");
        MutableList<Object> toRetain = FastList.newListWith(ImmutableEntry.of(1, "One"), "explosion!", ImmutableEntry.of(2, "Two"));
        assertFalse(map4.entrySet().retainAll(toRetain));
    }

    @Override
    @Test
    public void forEachWith()
    {
        super.forEachWith();

        for (int i = 1; i < COLLISIONS.size(); i++)
        {
            MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(i);
            Object sentinel = new Object();
            MutableSet<Integer> result = UnifiedSet.newSet();
            map.forEachWith((argument1, argument2) -> {
                assertSame(sentinel, argument2);
                result.add(argument1);
            }, sentinel);
            assertEquals(map.keySet(), result);
        }
    }

    @Test
    public void keySet_retainAll()
    {
        // a map with a null key
        MutableMap<Integer, Integer> map = this.newMapWithKeyValue(null, 0);

        MutableList<Object> retained = Lists.mutable.of();
        retained.add(null);
        assertFalse(map.keySet().retainAll(retained));
        Verify.assertContains(null, map.keySet());

        // a map with a chain containing empty slots
        MutableMap<Integer, Integer> map2 = this.mapWithCollisionsOfSize(5);
        assertFalse(map2.keySet().retainAll(FastList.newListWith(0, 17, 34, 51, 68)));
        Verify.assertContainsAll(map2.keySet(), 0, 17, 34, 51, 68);

        // a map with no chaining, nothing retained
        MutableMap<Integer, String> map3 = this.newMapWithKeyValue(1, "One");
        assertTrue(map3.keySet().retainAll(FastList.newListWith(9)));
        Verify.assertEmpty(map3);

        Set<Integer> keys = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four").keySet();
        assertTrue(keys.retainAll(FastList.newListWith(1, 2, 3)));
        Verify.assertContainsAll(keys, 1, 2, 3);
    }

    @Test
    public void keySet_containsAll()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        assertFalse(map.keySet().containsAll(FastList.newListWith(5)));
        assertTrue(map.keySet().containsAll(FastList.newListWith(1, 2, 4)));
    }

    @Test
    public void keySet_equals()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        assertNotEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5), map.keySet());
    }

    @Test
    public void keySet_add()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().add(5));
    }

    @Test
    public void keySet_addAll()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().addAll(UnifiedSet.newSetWith(5, 6)));
    }

    @Test
    public void keySet_Iterator()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Iterator<Integer> iterator = map.keySet().iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void entrySet_Iterator_incrementPastEnd()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Iterator<Map.Entry<Integer, String>> iterator = map.entrySet().iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void keySet_Iterator_removeBeforeIncrement()
    {
        // remove w/o incrementing
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Iterator<Integer> iterator = map.keySet().iterator();
        assertThrows(IllegalStateException.class, () -> iterator.remove());
    }

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
    public void iterator_many_collisions()
    {
        Iterator<Integer> iterator = this.mapWithCollisionsOfSize(9).iterator();
        for (Integer collision : MORE_COLLISIONS)
        {
            assertTrue(iterator.hasNext());
            assertEquals(collision, iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void entry_setValue()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Map.Entry<Integer, String> entry = Iterate.getFirst(map.entrySet());
        String value = "Ninety-Nine";
        assertEquals("One", entry.setValue(value));
        assertEquals(value, entry.getValue());
        Verify.assertContainsKeyValue(1, value, map);

        map.remove(1);
        Verify.assertEmpty(map);
        assertNull(entry.setValue("Ignored"));
    }

    @Test
    public void entry_hashCodeForNullKeyAndValue()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(null, null);
        Map.Entry<Integer, String> entry = Iterate.getFirst(map.entrySet());

        assertEquals(0, entry.hashCode());
    }

    @Test
    public void entry_equalsWithNonEntry()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(null, null);
        Map.Entry<Integer, String> entry = Iterate.getFirst(map.entrySet());

        assertNotEquals(entry, new Object());
    }

    @Test
    public void entrySet_remove()
    {
        // map with chaining, attempt to remove non-existent entry
        MutableMap<Integer, Integer> chainedMap = this.mapWithCollisionsOfSize(3);
        Set<Map.Entry<Integer, Integer>> chainedEntries = chainedMap.entrySet();
        assertFalse(chainedEntries.remove(ImmutableEntry.of(5, 5)));

        // map with chaining, attempt to remove non-existent collding entry
        MutableMap<Integer, Integer> chainedMap2 = this.mapWithCollisionsOfSize(2);
        Set<Map.Entry<Integer, Integer>> chainedEntries2 = chainedMap2.entrySet();
        assertFalse(chainedEntries2.remove(ImmutableEntry.of(COLLISION_4, COLLISION_4)));

        // map with chaining, attempt to remove non-existent colliding entry (key exists, but value does not)
        MutableMap<Integer, Integer> chainedMap3 = this.mapWithCollisionsOfSize(3);
        Set<Map.Entry<Integer, Integer>> chainedEntries3 = chainedMap3.entrySet();
        assertFalse(chainedEntries3.remove(ImmutableEntry.of(COLLISION_2, COLLISION_4)));

        // map with no chaining, attempt to remove non-existent entry
        MutableMap<Integer, String> unchainedMap = this.newMapWithKeyValue(1, "One");
        Set<Map.Entry<Integer, String>> unchainedEntries = unchainedMap.entrySet();
        assertFalse(unchainedEntries.remove(ImmutableEntry.of(5, "Five")));
    }

    @Test
    public void entrySet_contains()
    {
        // simple map, test for null key
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Set<Map.Entry<Integer, String>> entries = map.entrySet();
        Verify.assertNotContains(ImmutableEntry.of(null, "Null"), entries);

        // chained map, test for null value
        MutableMap<Integer, Integer> chainedMap = this.newMapWithKeysValues(COLLISION_1, COLLISION_1, COLLISION_2, null);
        Set<Map.Entry<Integer, Integer>> chainedEntries = chainedMap.entrySet();
        Verify.assertContains(ImmutableEntry.of(COLLISION_2, null), chainedEntries);
    }

    @Test
    public void entrySet_containsAll()
    {
        // simple map, test for non-existent entries
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 3, "Three");
        Set<Map.Entry<Integer, String>> entries = map.entrySet();
        assertFalse(entries.containsAll(FastList.newListWith(ImmutableEntry.of(2, "Two"))));

        assertTrue(entries.containsAll(FastList.newListWith(ImmutableEntry.of(1, "One"), ImmutableEntry.of(3, "Three"))));
    }

    @Test
    public void entrySet_add()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Set<Map.Entry<Integer, String>> entries = map.entrySet();
        assertThrows(UnsupportedOperationException.class, () -> entries.add(ImmutableEntry.of(2, "Two")));
    }

    @Test
    public void entrySet_addAll()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Set<Map.Entry<Integer, String>> entries = map.entrySet();
        assertThrows(UnsupportedOperationException.class, () -> entries.addAll(FastList.newListWith(ImmutableEntry.of(2, "Two"))));
    }

    @Test
    public void entrySet_equals()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", null, null);
        assertNotEquals(UnifiedSet.newSetWith(ImmutableEntry.of(5, "Five")), map.entrySet());

        MutableSet<ImmutableEntry<Integer, String>> expected = UnifiedSet.newSetWith(
                ImmutableEntry.of(1, "One"),
                ImmutableEntry.of(2, "Two"),
                ImmutableEntry.of(3, "Three"),
                ImmutableEntry.of(null, null));
        Verify.assertEqualsAndHashCode(expected, map.entrySet());
    }

    @Test
    public void valuesCollection_add()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        assertThrows(UnsupportedOperationException.class, () -> map.values().add("explosion!"));
    }

    @Test
    public void valuesCollection_addAll()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        assertThrows(UnsupportedOperationException.class, () -> map.values().addAll(UnifiedSet.newSetWith("explosion!", "kaboom!")));
    }

    @Test
    public void valueCollection_Iterator()
    {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Iterator<String> iterator = map.values().iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void valueCollection_equals()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", null, null);
        assertNotEquals(UnifiedSet.newSetWith("One", "Two", "Three", null), map.values());
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();

        MutableSet<String> set = UnifiedSet.newSet();

        // map with a chain and no empty slots
        MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(2);
        map.forEachWithIndex((each, index) -> set.add(index + ":" + each));
        assertEquals(UnifiedSet.newSetWith("0:0", "1:17"), set);

        set.clear();

        // map with a chain and empty slots
        MutableMap<Integer, Integer> map2 = this.mapWithCollisionsOfSize(5);
        map2.forEachWithIndex((each, index) -> set.add(index + ":" + each));
        assertEquals(UnifiedSet.newSetWith("0:0", "1:17", "2:34", "3:51", "4:68"), set);
    }

    @Override
    @Test
    public void forEachKey()
    {
        super.forEachKey();

        MutableSet<String> set = UnifiedSet.newSet(5);

        // map with a chain and empty slots
        MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(5);
        map.forEachKey(each -> set.add(each.toString()));
        assertEquals(UnifiedSet.newSetWith("0", "17", "34", "51", "68"), set);
    }

    @Override
    @Test
    public void forEachValue()
    {
        super.forEachValue();

        MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(9).withKeyValue(null, null);
        MutableSet<Integer> result = UnifiedSet.newSet();
        map.forEachValue(each -> {
            assertTrue(each == null || each.getClass() == Integer.class);
            result.add(each);
        });
        assertEquals(MORE_COLLISIONS.toSet().with(null), result);
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();

        for (int i = 1; i < COLLISIONS.size(); i++)
        {
            MutableMap<Integer, Integer> map = this.mapWithCollisionsOfSize(i);
            Map<Integer, Integer> expectedMap = new HashMap<>(map);

            Verify.assertEqualsAndHashCode(expectedMap, map);
            MutableMap<Integer, Integer> clone1 = map.clone();
            clone1.put(COLLISION_10, COLLISION_10);
            assertNotEquals(expectedMap, clone1);
            MutableMap<Integer, Integer> clone2 = map.clone();
            clone2.put(null, null);
            assertNotEquals(expectedMap, clone2);

            expectedMap.put(null, null);
            assertNotEquals(expectedMap, map);
            expectedMap.remove(null);

            expectedMap.put(COLLISION_10, COLLISION_10);
            assertNotEquals(expectedMap, map);
        }

        MutableMap<Integer, Integer> mapA = this.mapWithCollisionsOfSize(3);
        MutableMap<Integer, Integer> mapB = this.mapWithCollisionsOfSize(3);

        // map with a chain, compare the null key (and value) with a non-null key
        mapA.put(null, null);
        mapB.put(42, 84);
        assertNotEquals(mapA, mapB);
        assertNotEquals(mapA.hashCode(), mapB.hashCode());

        // map with a chain, compare the two null keys with different values (one null, one not)
        mapB.remove(42);
        mapB.put(null, 42);
        assertNotEquals(mapA, mapB);

        // map with a chain, compare a non-null key (null value) with a non-null key and value
        mapB.remove(null);
        mapB.remove(42);
        mapA.remove(null);
        mapA.put(17, null);
        assertNotEquals(mapA, mapB);

        MutableMap<Integer, String> mapC = this.newMapWithKeysValues(1, "One", 2, "Two", null, null);
        MutableMap<Integer, String> mapD = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three");

        // compare the null key (and value) with a non-null key
        assertNotEquals(mapC, mapD);

        // compare a non-null key (and null value) with a non-null key
        mapC.remove(null);
        mapC.put(3, null);
        assertNotEquals(mapC, mapD);

        // reset
        mapC.remove(3);
        mapC.put(null, null);

        // compare the null key (and null value) with a null key (and non-null value)
        mapD.remove(3);
        mapD.put(null, "Three");

        // compare the two null keys with different values (one null, one not)
        assertNotEquals(mapC, mapD);

        assertEquals(0, this.newMapWithKeyValue(null, null).hashCode());
    }

    @Test
    public void frequentCollision()
    {
        String[] expected = ArrayAdapter.adapt(FREQUENT_COLLISIONS)
                .subList(0, FREQUENT_COLLISIONS.length - 2)
                .toArray(new String[FREQUENT_COLLISIONS.length - 2]);
        MutableMap<String, String> map = this.newMap();
        MutableSet<String> set = Sets.mutable.of(expected);

        ArrayIterate.forEach(FREQUENT_COLLISIONS, each -> map.put(each, each));

        Iterator<String> itr = map.iterator();
        while (itr.hasNext())
        {
            if (!set.contains(itr.next()))
            {
                itr.remove();
            }
        }

        assertArrayEquals(expected, map.keysView().toArray());
    }

    @Override
    public void getFirst()
    {
        super.getFirst();

        MutableMap<String, String> map = this.newMap();
        map.collectKeysAndValues(Arrays.asList(FREQUENT_COLLISIONS), Functions.identity(), Functions.identity());
        assertEquals(FREQUENT_COLLISIONS[0], map.getFirst());
    }

    private static final class NoInstanceOfInEquals
    {
        private final int value;

        private NoInstanceOfInEquals(int value)
        {
            this.value = value;
        }

        @Override
        public boolean equals(Object o)
        {
            NoInstanceOfInEquals that = (NoInstanceOfInEquals) o;
            return this.value == that.value;
        }

        @Override
        public int hashCode()
        {
            return 12;
        }
    }
}
