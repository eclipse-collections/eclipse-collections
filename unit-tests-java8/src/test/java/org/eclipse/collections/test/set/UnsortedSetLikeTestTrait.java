/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set;

import java.util.Iterator;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.UnsortedSetIterable;
import org.eclipse.collections.test.RichIterableUniqueTestCase;
import org.eclipse.collections.test.UnorderedIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface UnsortedSetLikeTestTrait extends RichIterableUniqueTestCase, UnorderedIterableTestCase
{
    @Override
    default <T> UnsortedSetIterable<T> getExpectedFiltered(T... elements)
    {
        return Sets.immutable.with(elements);
    }

    @Override
    default <T> MutableSet<T> newMutableForFilter(T... elements)
    {
        return Sets.mutable.with(elements);
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        assertThat(this.newWith(2, 1).toString(), isOneOf("[1, 2]", "[2, 1]"));
        assertThat(this.newWith(2, 1).asLazy().toString(), isOneOf("[1, 2]", "[2, 1]"));
    }

    @Override
    @Test
    default void Iterable_next()
    {
        if (!this.allowsIterator())
        {
            assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator().next());
            return;
        }
        Iterable<Integer> iterable = this.newWith(3, 2, 1);

        MutableCollection<Integer> mutableCollection = this.newMutableForFilter();

        Iterator<Integer> iterator = iterable.iterator();
        while (iterator.hasNext())
        {
            Integer integer = iterator.next();
            mutableCollection.add(integer);
        }

        assertIterablesEqual(this.getExpectedFiltered(3, 2, 1), mutableCollection);
        assertFalse(iterator.hasNext());
    }

    @Override
    @Test
    default void RichIterable_getFirst()
    {
        RichIterable<Integer> integers = this.newWith(3, 2, 1);
        Integer first = integers.getFirst();
        assertThat(first, isOneOf(3, 2, 1));
        if (this.allowsIterator())
        {
            assertThat(integers.iterator().next(), equalTo(first));
        }
        else
        {
            assertThrows(AssertionError.class, () -> integers.iterator().next());
        }
    }

    @Override
    @Test
    default void RichIterable_getLast()
    {
        RichIterable<Integer> integers = this.newWith(3, 2, 1);
        Integer last = integers.getLast();
        assertThat(last, isOneOf(3, 2, 1));
    }

    @Override
    @Test
    default void RichIterable_toArray()
    {
        Object[] array = this.newWith(3, 2, 1).toArray();
        assertThat(array, anyOf(
                equalTo(new Object[]{1, 2, 3}),
                equalTo(new Object[]{1, 3, 2}),
                equalTo(new Object[]{2, 1, 3}),
                equalTo(new Object[]{2, 3, 1}),
                equalTo(new Object[]{3, 1, 2}),
                equalTo(new Object[]{3, 2, 1})));
    }

    @Override
    @Test
    default void RichIterable_makeString_appendString()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        assertThat(iterable.makeString(), isOneOf(
                "3, 2, 1",
                "3, 1, 2",
                "2, 3, 1",
                "2, 1, 3",
                "1, 3, 2",
                "1, 2, 3"));

        assertThat(iterable.makeString("/"), isOneOf(
                "3/2/1",
                "3/1/2",
                "2/3/1",
                "2/1/3",
                "1/3/2",
                "1/2/3"));

        assertThat(iterable.makeString("[", "/", "]"), isOneOf(
                "[3/2/1]",
                "[3/1/2]",
                "[2/3/1]",
                "[2/1/3]",
                "[1/3/2]",
                "[1/2/3]"));

        StringBuilder stringBuilder1 = new StringBuilder();
        iterable.appendString(stringBuilder1);
        assertThat(stringBuilder1.toString(), isOneOf(
                "3, 2, 1",
                "3, 1, 2",
                "2, 3, 1",
                "2, 1, 3",
                "1, 3, 2",
                "1, 2, 3"));

        StringBuilder stringBuilder2 = new StringBuilder();
        iterable.appendString(stringBuilder2, "/");
        assertThat(stringBuilder2.toString(), isOneOf(
                "3/2/1",
                "3/1/2",
                "2/3/1",
                "2/1/3",
                "1/3/2",
                "1/2/3"));

        StringBuilder stringBuilder3 = new StringBuilder();
        iterable.appendString(stringBuilder3, "[", "/", "]");
        assertThat(stringBuilder3.toString(), isOneOf(
                "[3/2/1]",
                "[3/1/2]",
                "[2/3/1]",
                "[2/1/3]",
                "[1/3/2]",
                "[1/2/3]"));
    }

    @Override
    @Test
    default void RichIterable_toList()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        assertThat(iterable.toList(), isOneOf(
                Lists.immutable.with(3, 2, 1),
                Lists.immutable.with(3, 1, 2),
                Lists.immutable.with(2, 3, 1),
                Lists.immutable.with(2, 1, 3),
                Lists.immutable.with(1, 3, 2),
                Lists.immutable.with(1, 2, 3)));

        MutableList<Integer> target = Lists.mutable.empty();
        iterable.each(target::add);
        assertIterablesEqual(
                target,
                iterable.toList());
    }

    @Override
    @Test
    default void RichIterable_into()
    {
        assertThat(this.newWith(3, 2, 1).into(Lists.mutable.empty()), isOneOf(
                Lists.immutable.with(3, 2, 1),
                Lists.immutable.with(3, 1, 2),
                Lists.immutable.with(2, 3, 1),
                Lists.immutable.with(2, 1, 3),
                Lists.immutable.with(1, 3, 2),
                Lists.immutable.with(1, 2, 3)));
    }
}
