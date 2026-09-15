/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.list;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.test.OrderedIterableWithDuplicatesTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface ListIterableTestCase extends OrderedIterableWithDuplicatesTestCase, TransformsToListTrait
{
    @Override
    <T> ListIterable<T> newWith(T... elements);

    @Override
    default <T> ListIterable<T> getExpectedFiltered(T... elements)
    {
        return Lists.immutable.with(elements);
    }

    @Override
    default <T> MutableList<T> newMutableForFilter(T... elements)
    {
        return Lists.mutable.with(elements);
    }

    @Override
    default <T> ListIterable<T> getExpectedTransformed(T... elements)
    {
        return Lists.immutable.with(elements);
    }

    @Test
    default void ListIterable_forEachWithIndex()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 3);
        MutableCollection<Pair<Integer, Integer>> result = Lists.mutable.with();
        integers.forEachWithIndex((each, index) -> result.add(Tuples.pair(each, index)));
        assertIterablesEqual(
                Lists.immutable.with(Tuples.pair(1, 0), Tuples.pair(2, 1), Tuples.pair(3, 2)),
                result);
    }

    @Test
    default void ListIterable_forEachInBoth()
    {
        MutableList<Pair<Integer, String>> result = Lists.mutable.empty();
        ListIterable<Integer> integers = this.newWith(1, 2, 3);
        ImmutableList<String> strings = this.newWith("1", "2", "3").toImmutable();
        integers.forEachInBoth(
                strings,
                (integer, string) -> result.add(Tuples.pair(integer, string)));
        assertIterablesEqual(
                Lists.immutable.with(Tuples.pair(1, "1"), Tuples.pair(2, "2"), Tuples.pair(3, "3")),
                result);
    }

    @Test
    default void ListIterable_indexOf()
    {
        ListIterable<Integer> integers = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(3, integers.indexOf(3));
        assertEquals(-1, integers.indexOf(0));
        assertEquals(-1, integers.indexOf(null));
        ListIterable<Integer> integers2 = this.newWith(1, 2, 2, null, 3, 3, 3, null, 4, 4, 4, 4);
        assertEquals(3, integers2.indexOf(null));
    }

    @Test
    default void ListIterable_lastIndexOf()
    {
        ListIterable<Integer> integers = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(5, integers.lastIndexOf(3));
        assertEquals(-1, integers.lastIndexOf(0));
        assertEquals(-1, integers.lastIndexOf(null));
        ListIterable<Integer> integers2 = this.newWith(1, 2, 2, null, 3, 3, 3, null, 4, 4, 4, 4);
        assertEquals(7, integers2.lastIndexOf(null));
    }

    @Test
    default void ListIterable_distinct()
    {
        ListIterable<String> letters = this.newWith("A", "a", "b", "c", "B", "D", "e", "e", "E", "D").distinct(HashingStrategies.fromFunction(String::toLowerCase));
        ListIterable<String> expected = FastList.newListWith("A", "b", "c", "D", "e");
        assertIterablesEqual(letters, expected);

        ListIterable<String> empty = this.<String>newWith().distinct(HashingStrategies.fromFunction(String::toLowerCase));
        assertIterablesEqual(empty, this.newWith());
    }
}
