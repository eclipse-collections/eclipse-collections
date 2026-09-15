/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable.primitive;

import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction0;
import org.eclipse.collections.api.iterator.MutableBooleanIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableObjectBooleanMap;
import org.eclipse.collections.api.tuple.primitive.ObjectBooleanPair;
import org.eclipse.collections.impl.factory.Iterables;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractMutableObjectBooleanMapTestCase extends AbstractObjectBooleanMapTestCase
{
    protected final MutableObjectBooleanMap<String> map = this.classUnderTest();

    @Override
    protected abstract MutableObjectBooleanMap<String> classUnderTest();

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1);

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2);

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3);

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3, T key4, boolean value4);

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> getEmptyMap();

    @Override
    public void get()
    {
        super.get();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.put("0", false);
        assertFalse(map1.get("0"));

        map1.put("5", true);
        assertTrue(map1.get("5"));

        map1.put(null, true);
        assertTrue(map1.get(null));
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        super.getIfAbsent();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.removeKey("0");
        assertTrue(map1.getIfAbsent("0", true));
        assertFalse(map1.getIfAbsent("0", false));

        map1.put("0", false);
        assertFalse(map1.getIfAbsent("0", true));

        map1.put("5", true);
        assertTrue(map1.getIfAbsent("5", false));

        map1.put(null, false);
        assertFalse(map1.getIfAbsent(null, true));
    }

    @Override
    @Test
    public void getOrThrow()
    {
        super.getOrThrow();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.removeKey("0");
        assertThrows(IllegalStateException.class, () -> map1.getOrThrow("0"));
        map1.put("0", false);
        assertFalse(map1.getOrThrow("0"));

        map1.put("5", true);
        assertTrue(map1.getOrThrow("5"));

        map1.put(null, false);
        assertFalse(map1.getOrThrow(null));
    }

    @Test
    public void getAndPut()
    {
        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        assertTrue(map1.getAndPut(Integer.valueOf(1), false, true));
        assertFalse(map1.getAndPut(Integer.valueOf(2), false, false));
        assertFalse(map1.getAndPut(Integer.valueOf(1), true, true));
        map1.remove(Integer.valueOf(1));
        assertFalse(map1.getAndPut(Integer.valueOf(1), true, false));
    }

    @Override
    @Test
    public void containsKey()
    {
        super.containsKey();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.removeKey("0");
        assertFalse(map1.containsKey("0"));
        assertFalse(map1.get("0"));
        map1.removeKey("0");
        assertFalse(map1.containsKey("0"));
        assertFalse(map1.get("0"));

        map1.removeKey("1");
        assertFalse(map1.containsKey("1"));
        assertFalse(map1.get("1"));

        map1.removeKey("2");
        assertFalse(map1.containsKey("2"));
        assertFalse(map1.get("2"));

        map1.removeKey("3");
        assertFalse(map1.containsKey("3"));
        assertFalse(map1.get("3"));
        map1.put(null, true);
        assertTrue(map1.containsKey(null));
        map1.removeKey(null);
        assertFalse(map1.containsKey(null));
    }

    @Override
    @Test
    public void containsValue()
    {
        super.containsValue();
        this.classUnderTest().clear();

        this.classUnderTest().put("5", true);
        assertTrue(this.classUnderTest().containsValue(true));

        this.classUnderTest().put(null, false);
        assertTrue(this.classUnderTest().containsValue(false));
    }

    @Override
    public void size()
    {
        super.size();
        MutableObjectBooleanMap<Integer> hashMap1 = this.newWithKeysValues(1, true, 0, false);
        Verify.assertSize(2, hashMap1);
        hashMap1.removeKey(1);
        Verify.assertSize(1, hashMap1);
        hashMap1.removeKey(0);
        Verify.assertSize(0, hashMap1);
    }

    @Override
    @Test
    public void contains()
    {
        super.contains();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.clear();

        map1.put("5", true);
        assertTrue(map1.contains(true));

        map1.put(null, false);
        assertTrue(map1.contains(false));

        map1.removeKey("5");
        assertFalse(map1.contains(true));
        assertTrue(map1.contains(false));

        map1.removeKey(null);
        assertFalse(map1.contains(false));
    }

    @Override
    @Test
    public void containsAll()
    {
        super.containsAll();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.clear();

        map1.put("5", true);
        assertTrue(map1.containsAll(true));
        assertFalse(map1.containsAll(true, false));
        assertFalse(map1.containsAll(false, false));

        map1.put(null, false);
        assertTrue(map1.containsAll(false));
        assertTrue(map1.containsAll(true, false));

        map1.removeKey("5");
        assertFalse(map1.containsAll(true));
        assertFalse(map1.containsAll(true, false));
        assertTrue(map1.containsAll(false, false));

        map1.removeKey(null);
        assertFalse(map1.containsAll(false, true));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        super.containsAllIterable();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.clear();

        map1.put("5", true);
        assertTrue(map1.containsAll(BooleanArrayList.newListWith(true)));
        assertFalse(map1.containsAll(BooleanArrayList.newListWith(true, false)));
        assertFalse(map1.containsAll(BooleanArrayList.newListWith(false, false)));

        map1.put(null, false);
        assertTrue(map1.containsAll(BooleanArrayList.newListWith(false)));
        assertTrue(map1.containsAll(BooleanArrayList.newListWith(true, false)));

        map1.removeKey("5");
        assertFalse(map1.containsAll(BooleanArrayList.newListWith(true)));
        assertFalse(map1.containsAll(BooleanArrayList.newListWith(true, false)));
        assertTrue(map1.containsAll(BooleanArrayList.newListWith(false, false)));

        map1.removeKey(null);
        assertFalse(map1.containsAll(BooleanArrayList.newListWith(false, true)));
    }

    protected static MutableList<String> generateCollisions()
    {
        MutableList<String> collisions = FastList.newList();
        ObjectBooleanHashMap<String> hashMap = new ObjectBooleanHashMap<>();
        for (int each = 3; collisions.size() <= 10; each++)
        {
            if (hashMap.spread(String.valueOf(each)) == hashMap.spread(String.valueOf(3)))
            {
                collisions.add(String.valueOf(each));
            }
        }
        return collisions;
    }

    @Test
    public void clear()
    {
        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();
        hashMap.put("0", true);
        hashMap.clear();
        assertEquals(ObjectBooleanHashMap.newMap(), hashMap);

        hashMap.put("1", false);
        hashMap.clear();
        assertEquals(ObjectBooleanHashMap.newMap(), hashMap);

        hashMap.put(null, true);
        hashMap.clear();
        assertEquals(ObjectBooleanHashMap.newMap(), hashMap);
    }

    @Test
    public void removeKey()
    {
        MutableObjectBooleanMap<String> map0 = this.newWithKeysValues("0", true, "1", false);
        map0.removeKey("1");
        assertEquals(this.newWithKeysValues("0", true), map0);
        map0.removeKey("0");
        assertEquals(ObjectBooleanHashMap.newMap(), map0);

        MutableObjectBooleanMap<String> map1 = this.newWithKeysValues("0", false, "1", true);
        map1.removeKey("0");
        assertEquals(this.newWithKeysValues("1", true), map1);
        map1.removeKey("1");
        assertEquals(ObjectBooleanHashMap.newMap(), map1);

        this.map.removeKey("5");
        assertEquals(this.newWithKeysValues("0", true, "1", true, "2", false), this.map);
        this.map.removeKey("0");
        assertEquals(this.newWithKeysValues("1", true, "2", false), this.map);
        this.map.removeKey("1");
        assertEquals(this.newWithKeysValues("2", false), this.map);
        this.map.removeKey("2");
        assertEquals(ObjectBooleanHashMap.newMap(), this.map);
        this.map.removeKey("0");
        this.map.removeKey("1");
        this.map.removeKey("2");
        assertEquals(ObjectBooleanHashMap.newMap(), this.map);
        Verify.assertEmpty(this.map);

        this.map.put(null, true);
        assertTrue(this.map.get(null));
        this.map.removeKey(null);
        assertFalse(this.map.get(null));
    }

    @Test
    public void put()
    {
        this.map.put("0", false);
        this.map.put("1", false);
        this.map.put("2", true);
        ObjectBooleanHashMap<String> expected = ObjectBooleanHashMap.newWithKeysValues("0", false, "1", false, "2", true);
        assertEquals(expected, this.map);

        this.map.put("5", true);
        expected.put("5", true);
        assertEquals(expected, this.map);

        this.map.put(null, false);
        expected.put(null, false);
        assertEquals(expected, this.map);
    }

    @Test
    public void putDuplicateWithRemovedSlot()
    {
        String collision1 = generateCollisions().getFirst();
        String collision2 = generateCollisions().get(1);
        String collision3 = generateCollisions().get(2);
        String collision4 = generateCollisions().get(3);

        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();
        hashMap.put(collision1, true);
        hashMap.put(collision2, false);
        hashMap.put(collision3, true);
        assertFalse(hashMap.get(collision2));
        hashMap.removeKey(collision2);
        hashMap.put(collision4, false);
        assertEquals(this.newWithKeysValues(collision1, true, collision3, true, collision4, false), hashMap);

        MutableObjectBooleanMap<String> hashMap1 = this.getEmptyMap();
        hashMap1.put(collision1, false);
        hashMap1.put(collision2, false);
        hashMap1.put(collision3, true);
        assertFalse(hashMap1.get(collision1));
        hashMap1.removeKey(collision1);
        hashMap1.put(collision4, true);
        assertEquals(this.newWithKeysValues(collision2, false, collision3, true, collision4, true), hashMap1);

        MutableObjectBooleanMap<String> hashMap2 = this.getEmptyMap();
        hashMap2.put(collision1, true);
        hashMap2.put(collision2, true);
        hashMap2.put(collision3, false);
        assertFalse(hashMap2.get(collision3));
        hashMap2.removeKey(collision3);
        hashMap2.put(collision4, false);
        assertEquals(this.newWithKeysValues(collision1, true, collision2, true, collision4, false), hashMap2);

        MutableObjectBooleanMap<String> hashMap3 = this.getEmptyMap();
        hashMap3.put(collision1, true);
        hashMap3.put(collision2, true);
        hashMap3.put(collision3, false);
        assertTrue(hashMap3.get(collision2));
        assertFalse(hashMap3.get(collision3));
        hashMap3.removeKey(collision2);
        hashMap3.removeKey(collision3);
        hashMap3.put(collision4, false);
        assertEquals(this.newWithKeysValues(collision1, true, collision4, false), hashMap3);

        MutableObjectBooleanMap<String> hashMap4 = this.getEmptyMap();
        hashMap4.put(null, false);
        assertEquals(this.newWithKeysValues(null, false), hashMap4);
        hashMap4.put(null, true);
        assertEquals(this.newWithKeysValues(null, true), hashMap4);
    }

    @Test
    public void getIfAbsentPut_Function()
    {
        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        assertTrue(map1.getIfAbsentPut(0, () -> true));
        BooleanFunction0 factoryThrows = () ->
        {
            throw new AssertionError();
        };
        assertTrue(map1.getIfAbsentPut(0, factoryThrows));
        assertEquals(this.newWithKeysValues(0, true), map1);
        assertTrue(map1.getIfAbsentPut(1, () -> true));
        assertTrue(map1.getIfAbsentPut(1, factoryThrows));
        assertEquals(this.newWithKeysValues(0, true, 1, true), map1);

        MutableObjectBooleanMap<Integer> map2 = this.getEmptyMap();
        assertFalse(map2.getIfAbsentPut(1, () -> false));
        assertFalse(map2.getIfAbsentPut(1, factoryThrows));
        assertEquals(this.newWithKeysValues(1, false), map2);
        assertFalse(map2.getIfAbsentPut(0, () -> false));
        assertFalse(map2.getIfAbsentPut(0, factoryThrows));
        assertEquals(this.newWithKeysValues(0, false, 1, false), map2);

        MutableObjectBooleanMap<Integer> map3 = this.getEmptyMap();
        assertTrue(map3.getIfAbsentPut(null, () -> true));
        assertTrue(map3.getIfAbsentPut(null, factoryThrows));
        assertEquals(this.newWithKeysValues(null, true), map3);
    }

    @Test
    public void getIfAbsentPutWith()
    {
        BooleanFunction<String> functionLengthEven = string -> (string.length() & 1) == 0;

        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        assertFalse(map1.getIfAbsentPutWith(0, functionLengthEven, "123456789"));
        BooleanFunction<String> functionThrows = string ->
        {
            throw new AssertionError();
        };
        assertFalse(map1.getIfAbsentPutWith(0, functionThrows, "unused"));
        assertEquals(this.newWithKeysValues(0, false), map1);
        assertFalse(map1.getIfAbsentPutWith(1, functionLengthEven, "123456789"));
        assertFalse(map1.getIfAbsentPutWith(1, functionThrows, "unused"));
        assertEquals(this.newWithKeysValues(0, false, 1, false), map1);

        MutableObjectBooleanMap<Integer> map2 = this.getEmptyMap();
        assertTrue(map2.getIfAbsentPutWith(1, functionLengthEven, "1234567890"));
        assertTrue(map2.getIfAbsentPutWith(1, functionThrows, "unused0"));
        assertEquals(this.newWithKeysValues(1, true), map2);
        assertTrue(map2.getIfAbsentPutWith(0, functionLengthEven, "1234567890"));
        assertTrue(map2.getIfAbsentPutWith(0, functionThrows, "unused0"));
        assertEquals(this.newWithKeysValues(0, true, 1, true), map2);

        MutableObjectBooleanMap<Integer> map3 = this.getEmptyMap();
        assertFalse(map3.getIfAbsentPutWith(null, functionLengthEven, "123456789"));
        assertFalse(map3.getIfAbsentPutWith(null, functionThrows, "unused"));
        assertEquals(this.newWithKeysValues(null, false), map3);
    }

    @Test
    public void getIfAbsentPutWithKey()
    {
        BooleanFunction<Integer> function = anObject -> anObject == null || (anObject & 1) == 0;

        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        assertTrue(map1.getIfAbsentPutWithKey(0, function));
        BooleanFunction<Integer> functionThrows = anObject ->
        {
            throw new AssertionError();
        };
        assertTrue(map1.getIfAbsentPutWithKey(0, functionThrows));
        assertEquals(this.newWithKeysValues(0, true), map1);
        assertFalse(map1.getIfAbsentPutWithKey(1, function));
        assertFalse(map1.getIfAbsentPutWithKey(1, functionThrows));
        assertEquals(this.newWithKeysValues(0, true, 1, false), map1);

        MutableObjectBooleanMap<Integer> map2 = this.getEmptyMap();
        assertFalse(map2.getIfAbsentPutWithKey(1, function));
        assertFalse(map2.getIfAbsentPutWithKey(1, functionThrows));
        assertEquals(this.newWithKeysValues(1, false), map2);
        assertTrue(map2.getIfAbsentPutWithKey(0, function));
        assertTrue(map2.getIfAbsentPutWithKey(0, functionThrows));
        assertEquals(this.newWithKeysValues(0, true, 1, false), map2);

        MutableObjectBooleanMap<Integer> map3 = this.getEmptyMap();
        assertTrue(map3.getIfAbsentPutWithKey(null, function));
        assertTrue(map3.getIfAbsentPutWithKey(null, functionThrows));
        assertEquals(this.newWithKeysValues(null, true), map3);
    }

    @Test
    public void withKeysValues()
    {
        MutableObjectBooleanMap<Integer> emptyMap = this.getEmptyMap();
        MutableObjectBooleanMap<Integer> hashMap = emptyMap.withKeyValue(1, true);
        assertEquals(this.newWithKeysValues(1, true), hashMap);
        assertSame(emptyMap, hashMap);
    }

    @Test
    public void withoutKey()
    {
        MutableObjectBooleanMap<Integer> hashMap = this.newWithKeysValues(1, true, 2, true, 3, false, 4, false);
        MutableObjectBooleanMap<Integer> actual = hashMap.withoutKey(5);
        assertSame(hashMap, actual);
        assertEquals(this.newWithKeysValues(1, true, 2, true, 3, false, 4, false), actual);
        assertEquals(this.newWithKeysValues(1, true, 2, true, 3, false), hashMap.withoutKey(4));
        assertEquals(this.newWithKeysValues(1, true, 2, true), hashMap.withoutKey(3));
        assertEquals(this.newWithKeysValues(1, true), hashMap.withoutKey(2));
        assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutKey(1));
        assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutKey(1));
    }

    @Test
    public void withoutAllKeys()
    {
        MutableObjectBooleanMap<Integer> hashMap = this.newWithKeysValues(1, true, 2, true, 3, false, 4, false);
        MutableObjectBooleanMap<Integer> actual = hashMap.withoutAllKeys(FastList.newListWith(5, 6, 7));
        assertSame(hashMap, actual);
        assertEquals(this.newWithKeysValues(1, true, 2, true, 3, false, 4, false), actual);
        assertEquals(this.newWithKeysValues(1, true, 2, true), hashMap.withoutAllKeys(FastList.newListWith(5, 4, 3)));
        assertEquals(this.newWithKeysValues(1, true), hashMap.withoutAllKeys(FastList.newListWith(2)));
        assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutAllKeys(FastList.newListWith(1)));
        assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutAllKeys(FastList.newListWith(5, 6)));
    }

    @Test
    public void withAllKeyValues()
    {
        MutableObjectBooleanMap<Integer> emptyMap = this.getEmptyMap();
        MutableObjectBooleanMap<Integer> partialMap = this.newWithKeysValues(1, true, 3, false);
        MutableObjectBooleanMap<Integer> completeMap = this.newWithKeysValues(1, true, 2, true, 3, false, 4, false);
        Iterable<ObjectBooleanPair<Integer>> emptyIterable = Iterables.iList();
        Iterable<ObjectBooleanPair<Integer>> partialIterable = Iterables.iList(
                PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(3), false));
        Iterable<ObjectBooleanPair<Integer>> completeIterable = Iterables.iList(
                PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true),
                PrimitiveTuples.pair(Integer.valueOf(3), false), PrimitiveTuples.pair(Integer.valueOf(4), false));
        assertEquals(emptyMap, emptyMap.withAllKeyValues(emptyIterable));
        assertEquals(partialMap, emptyMap.withAllKeyValues(partialIterable));
        assertEquals(completeMap, emptyMap.withAllKeyValues(completeIterable));
        assertEquals(partialMap, partialMap.withAllKeyValues(emptyIterable));
        assertEquals(partialMap, partialMap.withAllKeyValues(partialIterable));
        assertEquals(completeMap, partialMap.withAllKeyValues(completeIterable));
        assertEquals(completeMap, completeMap.withAllKeyValues(emptyIterable));
        assertEquals(completeMap, completeMap.withAllKeyValues(partialIterable));
        assertEquals(completeMap, completeMap.withAllKeyValues(completeIterable));
    }

    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableObjectBooleanMap.class, this.map.asUnmodifiable());
        assertEquals(new UnmodifiableObjectBooleanMap<>(this.map), this.map.asUnmodifiable());
    }

    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedObjectBooleanMap.class, this.map.asSynchronized());
        assertEquals(new SynchronizedObjectBooleanMap<>(this.map), this.map.asSynchronized());
    }

    @Test
    public void iterator_remove()
    {
        MutableObjectBooleanMap<String> map = this.classUnderTest();
        Verify.assertNotEmpty(map);
        MutableBooleanIterator booleanIterator = map.booleanIterator();

        while (booleanIterator.hasNext())
        {
            booleanIterator.next();
            booleanIterator.remove();
        }
        Verify.assertEmpty(map);
    }

    @Test
    public void iterator_throws_on_consecutive_invocation_of_remove()
    {
        MutableObjectBooleanMap<String> map = this.classUnderTest();
        Verify.assertNotEmpty(map);
        MutableBooleanIterator booleanIterator = map.booleanIterator();
        assertTrue(booleanIterator.hasNext());
        booleanIterator.next();
        booleanIterator.remove();
        assertThrows(IllegalStateException.class, booleanIterator::remove);
    }

    @Test
    public void iterator_throws_on_invocation_of_remove_before_next()
    {
        MutableObjectBooleanMap<String> map = this.classUnderTest();
        MutableBooleanIterator booleanIterator = map.booleanIterator();
        assertTrue(booleanIterator.hasNext());
        assertThrows(IllegalStateException.class, booleanIterator::remove);
    }
}
