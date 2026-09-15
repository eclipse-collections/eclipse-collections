/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable.primitive;

import java.util.NoSuchElementException;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.block.function.primitive.BooleanToObjectFunction;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.api.iterator.MutableBooleanIterator;
import org.eclipse.collections.impl.block.factory.primitive.BooleanPredicates;
import org.eclipse.collections.impl.collection.mutable.primitive.AbstractMutableBooleanCollectionTestCase;
import org.eclipse.collections.impl.collection.mutable.primitive.SynchronizedBooleanCollection;
import org.eclipse.collections.impl.collection.mutable.primitive.UnmodifiableBooleanCollection;
import org.eclipse.collections.impl.factory.primitive.BooleanBags;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class ObjectBooleanHashMapValuesTestCase extends AbstractMutableBooleanCollectionTestCase
{
    @Override
    @Test
    public void booleanIterator()
    {
        MutableBooleanCollection bag = this.newWith(true, false, true, true);
        BooleanArrayList list = BooleanArrayList.newListWith(true, false, true, true);
        BooleanIterator iterator1 = bag.booleanIterator();
        for (int i = 0; i < 4; i++)
        {
            assertTrue(iterator1.hasNext());
            assertTrue(list.remove(iterator1.next()));
        }
        Verify.assertEmpty(list);
        assertFalse(iterator1.hasNext());

        assertThrows(NoSuchElementException.class, iterator1::next);

        ObjectBooleanHashMap<String> map2 = new ObjectBooleanHashMap<>();
        for (int each = 2; each < 100; each++)
        {
            map2.put(String.valueOf(each), each % 2 == 0);
        }
        MutableBooleanIterator iterator2 = map2.booleanIterator();
        while (iterator2.hasNext())
        {
            iterator2.next();
            iterator2.remove();
        }
        assertTrue(map2.isEmpty());
    }

    @Override
    @Test
    public void addAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().addAll(new BooleanArrayList()));
    }

    @Override
    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().add(true));
    }

    @Override
    @Test
    public void addAllArray()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().addAll(true, false));
    }

    @Override
    @Test
    public void with()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().with(false));
    }

    @Override
    @Test
    public void without()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().without(true));
    }

    @Override
    @Test
    public void withAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().withAll(new BooleanArrayList()));
    }

    @Override
    @Test
    public void withoutAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().withoutAll(new BooleanArrayList()));
    }

    @Override
    @Test
    public void remove()
    {
        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, 2, false, 3, true);
        MutableBooleanCollection collection = map.values();
        assertTrue(collection.remove(false));
        assertFalse(collection.contains(false));
        assertTrue(collection.contains(true));
        assertFalse(map.contains(false));
        assertTrue(map.contains(true));
    }

    @Override
    @Test
    public void removeAll()
    {
        assertFalse(this.newWith().removeAll());

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        assertFalse(collection.removeAll());

        assertTrue(collection.removeAll(false));
        assertFalse(collection.contains(false));
        assertTrue(collection.contains(true));
        assertFalse(map.contains(false));
        assertTrue(map.contains(true));

        assertTrue(collection.removeAll(true));
        assertTrue(collection.isEmpty());
        assertFalse(collection.contains(true));
        assertFalse(collection.contains(false));
        assertFalse(map.contains(true));
        assertFalse(map.contains(false));
        assertTrue(map.isEmpty());
    }

    @Override
    @Test
    public void removeAll_iterable()
    {
        assertFalse(this.newWith().removeAll(new BooleanArrayList()));

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        assertFalse(collection.removeAll());

        assertTrue(collection.removeAll(BooleanArrayList.newListWith(false)));
        assertFalse(collection.contains(false));
        assertTrue(collection.contains(true));
        assertFalse(map.contains(false));
        assertTrue(map.contains(true));

        assertTrue(collection.removeAll(BooleanArrayList.newListWith(true)));
        assertTrue(collection.isEmpty());
        assertFalse(collection.contains(true));
        assertFalse(collection.contains(false));
        assertFalse(map.contains(true));
        assertFalse(map.contains(false));
        assertTrue(map.isEmpty());

        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection1 = map1.values();
        assertTrue(collection1.removeAll(BooleanArrayList.newListWith(true, false)));
        assertTrue(collection1.isEmpty());
        assertFalse(collection1.contains(true));
        assertFalse(collection.contains(false));
        assertFalse(map1.contains(true));
        assertFalse(map1.contains(false));
        assertTrue(map1.isEmpty());
    }

    @Override
    @Test
    public void retainAll()
    {
        assertFalse(this.newWith().retainAll());

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        assertFalse(collection.retainAll(false, true));

        assertTrue(collection.retainAll(true));
        assertFalse(collection.contains(false));
        assertTrue(collection.contains(true));
        assertFalse(map.contains(false));
        assertTrue(map.contains(true));

        assertTrue(collection.retainAll(false));
        assertTrue(collection.isEmpty());
        assertFalse(collection.contains(true));
        assertFalse(collection.contains(false));
        assertFalse(map.contains(true));
        assertFalse(map.contains(false));
        assertTrue(map.isEmpty());

        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection1 = map1.values();
        assertTrue(collection1.retainAll());
        assertTrue(collection1.isEmpty());
        assertFalse(collection1.contains(true));
        assertFalse(collection.contains(false));
        assertFalse(map1.contains(true));
        assertFalse(map1.contains(false));
        assertTrue(map1.isEmpty());
    }

    @Override
    @Test
    public void retainAll_iterable()
    {
        assertFalse(this.newWith().retainAll(new BooleanArrayList()));

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        assertFalse(collection.retainAll(BooleanArrayList.newListWith(false, true)));

        assertTrue(collection.retainAll(BooleanArrayList.newListWith(true)));
        assertFalse(collection.contains(false));
        assertTrue(collection.contains(true));
        assertFalse(map.contains(false));
        assertTrue(map.contains(true));

        assertTrue(collection.retainAll(BooleanArrayList.newListWith(false)));
        assertTrue(collection.isEmpty());
        assertFalse(collection.contains(true));
        assertFalse(collection.contains(false));
        assertFalse(map.contains(true));
        assertFalse(map.contains(false));
        assertTrue(map.isEmpty());

        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection1 = map1.values();
        assertTrue(collection1.retainAll(new BooleanArrayList()));
        assertTrue(collection1.isEmpty());
        assertFalse(collection1.contains(true));
        assertFalse(collection.contains(false));
        assertFalse(map1.contains(true));
        assertFalse(map1.contains(false));
        assertTrue(map1.isEmpty());
    }

    @Override
    @Test
    public void clear()
    {
        MutableBooleanCollection emptyCollection = this.newWith();
        emptyCollection.clear();
        Verify.assertSize(0, emptyCollection);

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, 2, false, 3, true);
        MutableBooleanCollection collection = map.values();
        collection.clear();
        Verify.assertEmpty(collection);
        Verify.assertEmpty(map);
        Verify.assertSize(0, collection);
        assertFalse(collection.contains(true));
        assertFalse(collection.contains(false));
    }

    @Override
    @Test
    public void contains()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        assertTrue(collection.contains(true));
        assertTrue(collection.contains(false));
        assertTrue(collection.remove(false));
        assertFalse(collection.contains(false));
        assertTrue(collection.remove(true));
        assertFalse(collection.contains(false));
        assertTrue(collection.contains(true));
        assertTrue(collection.remove(true));
        assertFalse(collection.contains(false));
        assertFalse(collection.contains(true));
    }

    @Override
    @Test
    public void containsAllArray()
    {
        MutableBooleanCollection emptyCollection = this.newWith();
        assertFalse(emptyCollection.containsAll(true));
        assertFalse(emptyCollection.containsAll(false));
        assertFalse(emptyCollection.containsAll(false, true, false));

        MutableBooleanCollection collection = this.classUnderTest();
        assertTrue(collection.containsAll());
        assertTrue(collection.containsAll(true));
        assertTrue(collection.containsAll(false));
        assertTrue(collection.containsAll(false, true));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        MutableBooleanCollection emptyCollection1 = this.newWith();
        assertTrue(emptyCollection1.containsAll(new BooleanArrayList()));
        assertFalse(emptyCollection1.containsAll(BooleanArrayList.newListWith(true)));
        assertFalse(emptyCollection1.containsAll(BooleanArrayList.newListWith(false)));
        MutableBooleanCollection collection = this.classUnderTest();
        assertTrue(collection.containsAll(new BooleanArrayList()));
        assertTrue(collection.containsAll(BooleanArrayList.newListWith(true)));
        assertTrue(collection.containsAll(BooleanArrayList.newListWith(false)));
        assertTrue(collection.containsAll(BooleanArrayList.newListWith(false, true)));
    }

    @Override
    @Test
    public void reject()
    {
        BooleanIterable iterable = this.classUnderTest();
        Verify.assertSize(1, iterable.reject(BooleanPredicates.isTrue()));
        Verify.assertSize(2, iterable.reject(BooleanPredicates.isFalse()));
    }

    @Override
    @Test
    public void select()
    {
        BooleanIterable iterable = this.classUnderTest();
        Verify.assertSize(1, iterable.select(BooleanPredicates.isFalse()));
        Verify.assertSize(2, iterable.select(BooleanPredicates.isTrue()));
    }

    @Override
    @Test
    public void collect()
    {
        BooleanToObjectFunction<Integer> function = parameter -> parameter ? 1 : 0;
        assertEquals(this.newObjectCollectionWith(1, 0, 1).toBag(), this.newWith(true, false, true).collect(function).toBag());
        assertEquals(this.newObjectCollectionWith(), this.newWith().collect(function));
    }

    @Override
    @Test
    public void appendString()
    {
        StringBuilder appendable = new StringBuilder();
        this.newWith().appendString(appendable);
        assertEquals("", appendable.toString());
        this.newWith().appendString(appendable, "/");
        assertEquals("", appendable.toString());
        this.newWith().appendString(appendable, "[", "/", "]");
        assertEquals("[]", appendable.toString());
        StringBuilder appendable1 = new StringBuilder();
        this.newWith(true).appendString(appendable1);
        assertEquals("true", appendable1.toString());
        StringBuilder appendable2 = new StringBuilder();
        BooleanIterable iterable = this.newWith(true, false);
        iterable.appendString(appendable2);
        assertTrue("true, false".equals(appendable2.toString())
                || "false, true".equals(appendable2.toString()));
        StringBuilder appendable3 = new StringBuilder();
        iterable.appendString(appendable3, "/");
        assertTrue("true/false".equals(appendable3.toString())
                || "false/true".equals(appendable3.toString()));
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        MutableBooleanCollection unmodifiable = this.classUnderTest().asUnmodifiable();
        Verify.assertInstanceOf(UnmodifiableBooleanCollection.class, unmodifiable);
        assertTrue(unmodifiable.containsAll(this.classUnderTest()));
    }

    @Override
    @Test
    public void asSynchronized()
    {
        MutableBooleanCollection synch = this.classUnderTest().asSynchronized();
        Verify.assertInstanceOf(SynchronizedBooleanCollection.class, synch);
        assertTrue(synch.containsAll(this.classUnderTest()));
    }

    @Override
    @Test
    public void chunk()
    {
        BooleanIterable iterable1 = this.newWith(true);
        Verify.assertIterablesEqual(
                Lists.mutable.with(BooleanBags.mutable.with(true)).toSet(),
                iterable1.chunk(1).toSet());

        BooleanIterable iterable2 = this.newWith(false);
        Verify.assertIterablesEqual(
                Lists.mutable.with(BooleanBags.mutable.with(false)).toSet(),
                iterable2.chunk(1).toSet());

        BooleanIterable iterable3 = this.newWith(false, true);
        Verify.assertIterablesEqual(
                Lists.mutable.with(BooleanBags.mutable.with(false), BooleanBags.mutable.with(true)).toSet(),
                iterable3.chunk(1).toSet());

        Verify.assertIterablesEqual(
                Lists.mutable.with(BooleanBags.mutable.with(false, true)),
                iterable3.chunk(2));
        Verify.assertIterablesEqual(
                Lists.mutable.with(BooleanBags.mutable.with(false, true)),
                iterable3.chunk(3));

        assertThrows(IllegalArgumentException.class, () -> this.classUnderTest().chunk(0));
        assertThrows(IllegalArgumentException.class, () -> this.classUnderTest().chunk(-1));
    }

    @Test
    @Override
    public void testEquals()
    {
    }

    @Test
    @Override
    public void testToString()
    {
    }

    @Test
    @Override
    public void testHashCode()
    {
    }

    @Override
    @Test
    public void newCollection()
    {
    }
}
