/*
 * Copyright (c) 2021 Two Sigma and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable.ordered;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.partition.ordered.PartitionOrderedIterable;
import org.eclipse.collections.impl.map.ordered.mutable.SynchronizedMutableOrderedMap;
import org.eclipse.collections.test.MutableOrderedIterableTestCase;
import org.eclipse.collections.test.map.OrderedMapIterableTestCase;
import org.eclipse.collections.test.map.mutable.MutableMapIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface MutableOrderedMapTestCase extends OrderedMapIterableTestCase, MutableMapIterableTestCase, MutableOrderedIterableTestCase
{
    @Override
    <T> MutableOrderedMap<Object, T> newWith(T... elements);

    @Override
    <K, V> MutableOrderedMap<K, V> newWithKeysValues(Object... elements);

    @Override
    @Test
    default void ReversibleIterable_take()
    {
        OrderedMapIterableTestCase.super.ReversibleIterable_take();

        if (this.allowsRemove())
        {
            MutableOrderedMap<Integer, String> withGap = this.newWithKeysValues(4, "Four", 3, "Three", 2, "Two", 1, "One");
            withGap.removeKey(3);
            assertIterablesEqual(this.newWithKeysValues(4, "Four", 2, "Two"), withGap.take(2));
            assertIterablesEqual(this.newWithKeysValues(4, "Four", 2, "Two", 1, "One"), withGap.take(4));
        }
    }

    @Override
    @Test
    default void ReversibleIterable_drop()
    {
        OrderedMapIterableTestCase.super.ReversibleIterable_drop();

        if (this.allowsRemove())
        {
            MutableOrderedMap<Integer, String> withGap = this.newWithKeysValues(4, "Four", 3, "Three", 2, "Two", 1, "One");
            withGap.removeKey(3);
            assertIterablesEqual(this.newWithKeysValues(2, "Two", 1, "One"), withGap.drop(1));
            assertIterablesEqual(this.newWithKeysValues(1, "One"), withGap.drop(2));
        }
    }

    @Override
    @Test
    default void OrderedIterable_forEachWithIndex_from_to()
    {
        OrderedMapIterableTestCase.super.OrderedIterable_forEachWithIndex_from_to();

        if (this.allowsRemove())
        {
            MutableOrderedMap<Integer, Integer> withGap = this.newWithKeysValues(1, 10, 2, 20, 3, 30, 4, 40, 5, 50);
            withGap.removeKey(3);

            List<Integer> forwardWithIndex = new ArrayList<>();
            withGap.forEachWithIndex(1, 2, (each, index) -> forwardWithIndex.add(each));
            assertIterablesEqual(Lists.immutable.with(20, 40), forwardWithIndex);

            List<Integer> reverseWithIndex = new ArrayList<>();
            withGap.forEachWithIndex(2, 1, (each, index) -> reverseWithIndex.add(each));
            assertIterablesEqual(Lists.immutable.with(40, 20), reverseWithIndex);

            List<Integer> forward = new ArrayList<>();
            withGap.forEach(1, 2, forward::add);
            assertIterablesEqual(Lists.immutable.with(20, 40), forward);

            List<Integer> reverse = new ArrayList<>();
            withGap.forEach(2, 1, reverse::add);
            assertIterablesEqual(Lists.immutable.with(40, 20), reverse);
        }
    }

    @Override
    @Test
    default void OrderedIterable_corresponds()
    {
        OrderedMapIterableTestCase.super.OrderedIterable_corresponds();

        if (this.allowsDuplicates() && this.allowsRemove())
        {
            MutableOrderedMap<Integer, Integer> withGap = this.newWithKeysValues(4, 4, 3, 3, 2, 2, 1, 1);
            withGap.removeKey(3);
            assertTrue(withGap.corresponds(Lists.mutable.with(4, 2, 1), Integer::equals));
            assertFalse(withGap.corresponds(Lists.mutable.with(4, 3, 2, 1), Integer::equals));
        }
    }

    @Override
    @Test
    default void OrderedIterable_distinct()
    {
        OrderedMapIterableTestCase.super.OrderedIterable_distinct();

        if (this.allowsDuplicates() && this.allowsRemove())
        {
            MutableOrderedMap<Integer, Integer> withGap = this.newWithKeysValues(4, 4, 3, 3, 2, 2, 1, 1);
            withGap.removeKey(3);
            assertIterablesEqual(Lists.immutable.with(4, 2, 1), withGap.distinct());
        }
    }

    @Override
    @Test
    default void OrderedIterable_detectIndex()
    {
        OrderedMapIterableTestCase.super.OrderedIterable_detectIndex();

        if (this.allowsDuplicates() && this.allowsRemove())
        {
            MutableOrderedMap<Integer, Integer> withGap = this.newWithKeysValues(4, 4, 3, 3, 2, 2, 1, 1);
            withGap.removeKey(3);
            assertEquals(0, withGap.detectIndex(each -> each == 4));
            assertEquals(1, withGap.detectIndex(each -> each == 2));
            assertEquals(2, withGap.detectIndex(each -> each == 1));
        }
    }

    @Override
    @Test
    default void ReversibleIterable_detectLastIndex()
    {
        OrderedMapIterableTestCase.super.ReversibleIterable_detectLastIndex();

        if (this.allowsRemove())
        {
            MutableOrderedMap<String, Integer> withGap = this.newWithKeysValues("A", 1, "B", 2, "C", 1, "D", 3);
            withGap.removeKey("B");
            assertEquals(1, withGap.detectLastIndex(value -> value == 1));
            assertEquals(2, withGap.detectLastIndex(value -> value == 3));
        }
    }

    @Override
    @Test
    default void ReversibleIterable_toReversed()
    {
        OrderedMapIterableTestCase.super.ReversibleIterable_toReversed();

        if (this.allowsRemove())
        {
            MutableOrderedMap<String, Integer> withGap = this.newWithKeysValues("A", 1, "B", 2, "C", 3, "D", 4);
            withGap.removeKey("B");
            assertIterablesEqual(this.newWithKeysValues("D", 4, "C", 3, "A", 1), withGap.toReversed());
        }
    }

    @Override
    @Test
    default void OrderedIterable_takeWhile()
    {
        OrderedMapIterableTestCase.super.OrderedIterable_takeWhile();

        if (this.allowsRemove())
        {
            MutableOrderedMap<Integer, Integer> withGap = this.newWithKeysValues(1, 2, 2, 4, 3, 6, 4, 8, 5, 10);
            withGap.removeKey(3);
            assertIterablesEqual(this.newWithKeysValues(1, 2, 2, 4), withGap.takeWhile(each -> each < 8));
        }
    }

    @Override
    @Test
    default void OrderedIterable_dropWhile()
    {
        OrderedMapIterableTestCase.super.OrderedIterable_dropWhile();

        if (this.allowsRemove())
        {
            MutableOrderedMap<Integer, Integer> withGap = this.newWithKeysValues(1, 2, 2, 4, 3, 6, 4, 8, 5, 10);
            withGap.removeKey(3);
            assertIterablesEqual(this.newWithKeysValues(4, 8, 5, 10), withGap.dropWhile(each -> each < 8));
        }
    }

    @Override
    @Test
    default void OrderedIterable_partitionWhile()
    {
        OrderedMapIterableTestCase.super.OrderedIterable_partitionWhile();

        if (this.allowsRemove())
        {
            MutableOrderedMap<Integer, Integer> withGap = this.newWithKeysValues(1, 2, 2, 4, 3, 6, 4, 8, 5, 10);
            withGap.removeKey(3);
            PartitionOrderedIterable<Integer> partition = withGap.partitionWhile(each -> each < 8);
            assertIterablesEqual(Lists.immutable.with(2, 4), partition.getSelected());
            assertIterablesEqual(Lists.immutable.with(8, 10), partition.getRejected());
        }
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        MutableOrderedIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void MutableMapIterable_asSynchronized()
    {
        MutableMapIterableTestCase.super.MutableMapIterable_asSynchronized();

        assertInstanceOf(SynchronizedMutableOrderedMap.class, this.newWithKeysValues("A", 1).asSynchronized());
    }

    @Override
    @Test
    default void RichIterable_getFirst()
    {
        OrderedMapIterableTestCase.super.RichIterable_getFirst();

        if (this.allowsRemove())
        {
            // getFirst() skips the removed first entry, and re-putting an existing key keeps its position
            MutableOrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2, "C", 3);
            map.removeKey("A");
            assertEquals(Integer.valueOf(2), map.getFirst());

            map.put("D", 4);
            map.put("B", 99);
            assertEquals(Integer.valueOf(99), map.getFirst());
        }
    }

    @Override
    @Test
    default void RichIterable_getLast()
    {
        OrderedMapIterableTestCase.super.RichIterable_getLast();

        if (this.allowsRemove())
        {
            // getLast() skips the removed last entry, and a newly put key becomes last
            MutableOrderedMap<String, Integer> map = this.newWithKeysValues("A", 1, "B", 2, "C", 3);
            map.removeKey("C");
            assertEquals(Integer.valueOf(2), map.getLast());

            map.put("D", 4);
            map.put("B", 99);
            assertEquals(Integer.valueOf(4), map.getLast());
        }
    }
}
