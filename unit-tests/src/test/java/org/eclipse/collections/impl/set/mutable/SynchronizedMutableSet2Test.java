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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link SynchronizedMutableSet}.
 */
public class SynchronizedMutableSet2Test extends AbstractMutableSetTestCase
{
    @Override
    protected <T> MutableSet<T> newWith(T... littleElements)
    {
        return new SynchronizedMutableSet<>(SetAdapter.adapt(new HashSet<>(UnifiedSet.newSetWith(littleElements))));
    }

    @Test
    public void min_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().min());
    }

    @Test
    public void max_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().max());
    }

    @Override
    @Test
    public void testToString()
    {
        MutableSet<Integer> integer = this.newWith(1);
        assertEquals("[1]", integer.toString());
    }

    @Override
    @Test
    public void makeString()
    {
        MutableSet<Integer> integer = this.newWith(1);
        assertEquals("{1}", integer.makeString("{", ",", "}"));
    }

    @Override
    @Test
    public void appendString()
    {
        Appendable stringBuilder = new StringBuilder();
        MutableSet<Integer> integer = this.newWith(1);
        integer.appendString(stringBuilder, "{", ",", "}");
        assertEquals("{1}", stringBuilder.toString());
    }

    @Override
    @Test
    public void removeIf()
    {
        MutableSet<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.remove(3);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 4), integers);
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        MutableSet<Number> numbers = new SynchronizedMutableSet<Number>(SetAdapter.adapt(new TreeSet<>((o1, o2) -> Double.compare(o1.doubleValue(), o2.doubleValue())))).withAll(FastList.newListWith(1, 2.0, 3, 4.0, 5));
        MutableSet<Integer> integers = numbers.selectInstancesOf(Integer.class);
        assertEquals(UnifiedSet.newSetWith(1, 3, 5), integers);
        assertEquals(FastList.newListWith(1, 3, 5), integers.toList());
    }

    @Test
    @Override
    public void getFirst()
    {
        assertNotNull(this.newWith(1, 2, 3).getFirst());
        assertNull(this.newWith().getFirst());
        assertEquals(Integer.valueOf(1), this.newWith(1).getFirst());
        int first = this.newWith(1, 2).getFirst().intValue();
        assertTrue(first == 1 || first == 2);
    }

    @Test
    @Override
    public void getLast()
    {
        assertNotNull(this.newWith(1, 2, 3).getLast());
        assertNull(this.newWith().getLast());
        assertEquals(Integer.valueOf(1), this.newWith(1).getLast());
        int last = this.newWith(1, 2).getLast().intValue();
        assertTrue(last == 1 || last == 2);
    }

    @Test
    @Override
    public void iterator()
    {
        MutableSet<Integer> objects = this.newWith(1, 2, 3);
        MutableBag<Integer> actual = Bags.mutable.of();

        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); i-- > 0; )
        {
            assertTrue(iterator.hasNext());
            actual.add(iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertEquals(Bags.mutable.of(1, 2, 3), actual);
    }
}
