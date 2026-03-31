/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.sorted.mutable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link UnmodifiableSortedBag}.
 *
 * @since 4.2
 */
public class UnmodifiableSortedBagTest extends AbstractMutableSortedBagTestCase
{
    @Override
    protected <T> MutableSortedBag<T> newWith(T... elements)
    {
        return TreeBag.newBagWith(elements).asUnmodifiable();
    }

    @SafeVarargs
    @Override
    protected final <T> MutableSortedBag<T> newWithOccurrences(ObjectIntPair<T>... elementsWithOccurrences)
    {
        MutableSortedBag<T> bag = TreeBag.newBag();
        for (int i = 0; i < elementsWithOccurrences.length; i++)
        {
            ObjectIntPair<T> itemToAdd = elementsWithOccurrences[i];
            bag.addOccurrences(itemToAdd.getOne(), itemToAdd.getTwo());
        }
        return bag.asUnmodifiable();
    }

    @Override
    protected <T> MutableSortedBag<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return TreeBag.newBagWith(comparator, elements).asUnmodifiable();
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        Verify.assertInstanceOf(UnmodifiableSortedBag.class, SerializeTestHelper.serializeDeserialize(this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3)));
        Verify.assertInstanceOf(UnmodifiableSortedBag.class, SerializeTestHelper.serializeDeserialize(this.newWith(1, 2, 3)));
    }

    @Override
    @Test
    public void toString_with_collection_containing_self()
    {
        // Not applicable for Unmodifiable*
    }

    @Override
    @Test
    public void makeString_with_collection_containing_self()
    {
        // Not applicable for Unmodifiable*
    }

    @Override
    @Test
    public void appendString_with_collection_containing_self()
    {
        // Not applicable for Unmodifiable*
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedSortedBag.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedBag.class, this.newWith());
    }

    @Override
    @Test
    public void iterator()
    {
        MutableSortedBag<Integer> bag = this.newWith(-1, 0, 1, 1, 2);
        Iterator<Integer> iterator = bag.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(-1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(0), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertFalse(iterator.hasNext());

        MutableSortedBag<Integer> revBag = this.newWith(Comparators.reverseNaturalOrder(), -1, 0, 1, 1, 2);
        Iterator<Integer> revIterator = revBag.iterator();
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(2), revIterator.next());
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(1), revIterator.next());
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(1), revIterator.next());
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(0), revIterator.next());
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(-1), revIterator.next());
        assertFalse(revIterator.hasNext());

        Iterator<Integer> iterator3 = this.newWith(Comparators.reverseNaturalOrder(), 2, 1, 1, 0, -1).iterator();
        assertThrows(UnsupportedOperationException.class, iterator3::remove);
        assertEquals(Integer.valueOf(2), iterator3.next());
        assertThrows(UnsupportedOperationException.class, iterator3::remove);
    }

    @Override
    @Test
    public void iteratorRemove()
    {
        MutableSortedBag<Integer> bag = this.newWith(-1, 0, 1, 1, 2);
        Iterator<Integer> iterator = bag.iterator();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Override
    @Test
    public void iteratorRemove2()
    {
    }

    @Test
    public void testAsUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedBag.class, this.newWith().asUnmodifiable());
        MutableSortedBag<Object> bag = this.newWith();
        assertSame(bag, bag.asUnmodifiable());
    }

    @Override
    @Test
    public void removeIfWith()
    {
        assertThrows(UnsupportedOperationException.class, super::removeIfWith);
    }

    @Override
    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, super::clear);
    }

    @Override
    @Test
    public void addAll()
    {
        assertThrows(UnsupportedOperationException.class, super::addAll);
    }

    @Override
    @Test
    public void addAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, super::addAllIterable);
    }

    @Override
    @Test
    public void removeIf()
    {
        assertThrows(UnsupportedOperationException.class, super::removeIf);
    }

    @Override
    @Test
    public void removeAll()
    {
        assertThrows(UnsupportedOperationException.class, super::removeAll);
    }

    @Override
    @Test
    public void removeAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, super::removeAllIterable);
    }

    @Override
    @Test
    public void retainAll()
    {
        assertThrows(UnsupportedOperationException.class, super::retainAll);
    }

    @Override
    @Test
    public void retainAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, super::retainAllIterable);
    }

    @Override
    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, super::add);
    }

    @Override
    @Test
    public void addOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, super::addOccurrences);
    }

    @Override
    @Test
    public void withOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, super::withOccurrences);
    }

    @Override
    @Test
    public void addOccurrences_throws()
    {
        // Not applicable for Unmodifiable
    }

    @Override
    @Test
    public void withOccurrences_throws()
    {
        // Not applicable for Unmodifiable
    }

    @Override
    @Test
    public void removeObject()
    {
        assertThrows(UnsupportedOperationException.class, super::removeObject);
    }

    @Override
    @Test
    public void removeOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, super::removeOccurrences);
    }

    @Override
    @Test
    public void withoutOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, super::withoutOccurrences);
    }

    @Override
    @Test
    public void removeOccurrences_throws()
    {
        // Not applicable for Unmodifiable
    }

    @Override
    @Test
    public void withoutOccurrences_throws()
    {
        // Not applicable for Unmodifiable
    }

    @Override
    @Test
    public void setOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, super::setOccurrences);
    }

    @Override
    @Test
    public void setOccurrences_throws()
    {
        // Not applicable for Unmodifiable
    }

    @Override
    @Test
    public void forEachWithOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, super::forEachWithOccurrences);
    }

    @Override
    @Test
    public void with()
    {
        assertThrows(UnsupportedOperationException.class, super::with);
    }

    @Override
    @Test
    public void withAll()
    {
        assertThrows(UnsupportedOperationException.class, super::withAll);
    }

    @Override
    @Test
    public void without()
    {
        assertThrows(UnsupportedOperationException.class, super::without);
    }

    @Override
    @Test
    public void withoutAll()
    {
        assertThrows(UnsupportedOperationException.class, super::withoutAll);
    }

    @Override
    @Test
    public void testClone()
    {
        MutableSortedBag<Integer> set = this.newWith(1, 2, 3);
        MutableSortedBag<Integer> clone = set.clone();
        assertSame(set, clone);
        Verify.assertSortedBagsEqual(set, clone);
    }

    @Override
    @Test
    public void collectWithOccurrences()
    {
        Bag<Integer> bag1 = this.newWith(3, 3, 3, 2, 2, 1);
        Bag<ObjectIntPair<Integer>> actual1 =
                bag1.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
        assertEquals(
                Bags.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1)),
                actual1);
        assertEquals(
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(1), 1),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(3), 3)),
                bag1.collectWithOccurrences(PrimitiveTuples::pair));

        Set<ObjectIntPair<Integer>> actual2 =
                bag1.collectWithOccurrences(PrimitiveTuples::pair, Sets.mutable.empty());
        assertEquals(
                Sets.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1)),
                actual2);

        Bag<Integer> bag2 = this.newWith(3, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 4, 5, 7);
        assertEquals(
                Lists.mutable.with(6, 5, 8, 5, 6, 8),
                bag2.collectWithOccurrences((each, index) -> each + index));
    }
}
