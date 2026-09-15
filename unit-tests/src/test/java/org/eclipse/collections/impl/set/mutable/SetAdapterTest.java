/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link SetAdapter}.
 */
public class SetAdapterTest extends AbstractMutableSetTestCase
{
    @Override
    protected <T> SetAdapter<T> newWith(T... littleElements)
    {
        return new SetAdapter<>(new HashSet<>(UnifiedSet.newSetWith(littleElements)));
    }

    @Override
    @Test
    public void testToString()
    {
        MutableCollection<Object> collection = this.newWith(1);
        collection.add(collection);
        String string = collection.toString();
        assertTrue(
                "[1, (this Collection)]".equals(string)
                        || "[(this Collection), 1]".equals(string));
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedMutableSet.class, SetAdapter.adapt(new HashSet<>()).asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableMutableSet.class, this.newWith().asUnmodifiable());
    }

    @Test
    public void adapt()
    {
        MutableSet<Integer> adapter1 = SetAdapter.adapt(Sets.fixedSize.of(1, 2, 3, 4));
        MutableSet<Integer> adapter2 = new SetAdapter<Integer>(new HashSet<>()).with(1, 2, 3, 4);
        MutableSet<Integer> adapter3 = Sets.adapt(Sets.fixedSize.of(1, 2, 3, 4));
        Verify.assertEqualsAndHashCode(adapter1, adapter2);
        Verify.assertEqualsAndHashCode(adapter2, adapter3);
    }

    @Override
    @Test
    public void select()
    {
        super.select();
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).select(Predicates.lessThan(3)), 1, 2);
        Verify.assertContainsAll(this.newWith(-1, 2, 3, 4, 5).select(Predicates.lessThan(3),
                FastList.newList()), -1, 2);
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3),
                FastList.newList()), 3, 4);
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).collect(String::valueOf),
                "1",
                "2",
                "3",
                "4");
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).collect(
                String::valueOf,
                FastList.newList()), "1", "2", "3", "4");
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        MutableCollection<Integer> set1 = this.newWith(1, 2, 3);
        MutableCollection<Integer> set2 = this.newWith(1, 2, 3);
        MutableCollection<Integer> set3 = this.newWith(2, 3, 4);
        MutableSet<Integer> set4 = UnifiedSet.newSet();
        set4.add(2);
        set4.add(3);
        set4.add(4);
        assertNotEquals(set1, null);
        Verify.assertEqualsAndHashCode(set1, set1);
        Verify.assertEqualsAndHashCode(set1, set2);
        assertNotEquals(set2, set3);
        Verify.assertEqualsAndHashCode(set3, set4);
    }

    @Test
    public void newListWithSize()
    {
        Collection<Integer> collection = this.newWith(1, 2, 3);
        Verify.assertContainsAll(collection, 1, 2, 3);
    }

    @Test
    public void serialization()
    {
        MutableCollection<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        MutableCollection<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        Verify.assertContainsAll(deserializedCollection, 1, 2, 3, 4, 5);
        assertEquals(collection, deserializedCollection);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        MutableList<Integer> result = Lists.mutable.of();
        MutableCollection<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEachWithIndex((object, index) -> result.add(object));
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void getFirst()
    {
        assertNotNull(this.newWith(1, 2, 3).getFirst());
        assertNull(this.newWith().getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        assertNotNull(this.newWith(1, 2, 3).getLast());
        assertNull(this.newWith().getLast());
    }

    @Override
    @Test
    public void iterator()
    {
        MutableCollection<Integer> objects = this.newWith(1, 2, 3);
        MutableList<Integer> result = Lists.mutable.of();
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); i-- > 0; )
        {
            Integer integer = iterator.next();
            result.add(integer);
        }
        Verify.assertStartsWith(result.sortThis(Collections.reverseOrder()), 3, 2, 1);
    }

    @Test
    public void withMethods()
    {
        Verify.assertContainsAll(this.newWith().with(1), 1);
        Verify.assertContainsAll(this.newWith().with(1, 2), 1, 2);
        Verify.assertContainsAll(this.newWith().with(1, 2, 3), 1, 2, 3);
        Verify.assertContainsAll(this.newWith().with(1, 2, 3, 4), 1, 2, 3, 4);
    }

    @Test
    public void returnType()
    {
        //Type HashSet is important here because it's not a MutableSet
        Set<Integer> set = new HashSet<>();
        MutableSet<Integer> integerSetAdapter = SetAdapter.adapt(set);
        Verify.assertInstanceOf(MutableSet.class, integerSetAdapter.select(ignored -> true));
    }

    @Test
    public void adaptNull()
    {
        assertThrows(NullPointerException.class, () -> new SetAdapter<>(null));

        assertThrows(NullPointerException.class, () -> SetAdapter.adapt(null));
    }
}
