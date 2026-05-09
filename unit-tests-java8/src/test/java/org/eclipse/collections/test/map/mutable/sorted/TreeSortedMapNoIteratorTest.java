/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable.sorted;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.test.OrderedIterableNoIteratorTestCase;
import org.eclipse.collections.test.map.MapKeySetTestCase;
import org.eclipse.collections.test.map.MapValuesCollectionTestCase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class TreeSortedMapNoIteratorTest implements MutableSortedMapIterableTestCase, OrderedIterableNoIteratorTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    public <T> MutableSortedMap<Object, T> newWith(T... elements)
    {
        int i = elements.length;
        MutableSortedMap<Object, T> result = new TreeSortedMapNoIterator<>(Comparators.reverseNaturalOrder());
        for (T each : elements)
        {
            assertNull(result.put(i, each));
            i--;
        }
        return result;
    }

    @Override
    public <K, V> MutableSortedMap<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        MutableSortedMap<K, V> result = new TreeSortedMapNoIterator<>(Comparators.reverseNaturalOrder());
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return result;
    }

    @Override
    @Test
    public void Iterable_remove()
    {
        OrderedIterableNoIteratorTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    public void OrderedIterable_takeWhile()
    {
        MutableSortedMapIterableTestCase.super.OrderedIterable_takeWhile();
    }

    @Override
    @Test
    public void OrderedIterable_dropWhile()
    {
        MutableSortedMapIterableTestCase.super.OrderedIterable_dropWhile();
    }

    @Override
    @Test
    public void OrderedIterable_partitionWhile()
    {
        MutableSortedMapIterableTestCase.super.OrderedIterable_partitionWhile();
    }

    public static class TreeSortedMapNoIterator<K, V> extends TreeSortedMap<K, V>
    {
        public TreeSortedMapNoIterator()
        {
            // For serialization
        }

        public TreeSortedMapNoIterator(Comparator<? super K> comparator)
        {
            super(comparator);
        }

        @Override
        public Iterator<V> iterator()
        {
            throw new AssertionError("No iteration patterns should delegate to iterator()");
        }
    }

    @Nested
    public class KeySetView implements MapKeySetTestCase
    {
        @SafeVarargs
        @Override
        public final <T> Set<T> newWith(T... elements)
        {
            Random random = new Random(CURRENT_TIME_MILLIS);
            MutableSortedMap<T, Object> result = new TreeSortedMapNoIterator<>(Comparators.reverseNaturalOrder());
            for (T element : elements)
            {
                assertNull(result.put(element, random.nextDouble()));
            }
            return result.keySet();
        }

        @Override
        public boolean allowsSerialization()
        {
            return false;
        }
    }

    @Nested
    public class ValuesCollectionView implements MapValuesCollectionTestCase
    {
        @Override
        public OrderingType getOrderingType()
        {
            return OrderingType.SORTED_REVERSE_NATURAL;
        }

        @SafeVarargs
        @Override
        public final <T> Collection<T> newWith(T... elements)
        {
            return TreeSortedMapNoIteratorTest.this.newWith(elements).values();
        }

        @Override
        public boolean allowsSerialization()
        {
            return false;
        }
    }
}
