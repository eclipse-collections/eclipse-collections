/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface UnorderedIterableTestCase extends RichIterableTestCase
{
    @Override
    default OrderingType getOrderingType()
    {
        return OrderingType.UNORDERED;
    }

    @Override
    @Test
    default void Iterable_next()
    {
        Iterable<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);

        MutableCollection<Integer> mutableCollection = this.newMutableForFilter();

        Iterator<Integer> iterator = iterable.iterator();
        while (iterator.hasNext())
        {
            Integer integer = iterator.next();
            mutableCollection.add(integer);
        }

        assertIterablesEqual(this.getExpectedFiltered(3, 3, 3, 2, 2, 1), mutableCollection);
        assertFalse(iterator.hasNext());
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        assertThat(this.newWith(2, 2, 1).toString(), isOneOf("[2, 2, 1]", "[1, 2, 2]"));
        assertThat(this.newWith(2, 2, 1).asLazy().toString(), isOneOf("[2, 2, 1]", "[1, 2, 2]"));
    }

    @Override
    @Test
    default void RichIterable_getFirst()
    {
        RichIterable<Integer> integers = this.newWith(3, 2, 1);
        Integer first = integers.getFirst();
        assertThat(first, isOneOf(3, 2, 1));
        assertEquals(integers.iterator().next(), first);

        assertNotEquals(integers.getLast(), first);
    }

    @Override
    @Test
    default void RichIterable_getLast()
    {
        RichIterable<Integer> integers = this.newWith(3, 2, 1);
        Integer last = integers.getLast();
        assertThat(last, isOneOf(3, 2, 1));

        Iterator<Integer> iterator = integers.iterator();
        Integer iteratorLast = null;
        while (iterator.hasNext())
        {
            iteratorLast = iterator.next();
        }
        assertEquals(iteratorLast, last);

        assertNotEquals(integers.getFirst(), last);
    }

    @Override
    @Test
    default void RichIterable_detect()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);

        assertThat(iterable.detect(Predicates.greaterThan(0)), isOneOf(3, 2, 1));
        assertThat(iterable.detect(Predicates.greaterThan(1)), isOneOf(3, 2));
        assertThat(iterable.detect(Predicates.greaterThan(2)), is(3));
        assertThat(iterable.detect(Predicates.greaterThan(3)), nullValue());

        assertThat(iterable.detect(Predicates.lessThan(1)), nullValue());
        assertThat(iterable.detect(Predicates.lessThan(2)), is(1));
        assertThat(iterable.detect(Predicates.lessThan(3)), isOneOf(2, 1));
        assertThat(iterable.detect(Predicates.lessThan(4)), isOneOf(3, 2, 1));

        assertThat(iterable.detectWith(Predicates2.greaterThan(), 0), isOneOf(3, 2, 1));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 1), isOneOf(3, 2));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 2), is(3));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 3), nullValue());

        assertThat(iterable.detectWith(Predicates2.lessThan(), 1), nullValue());
        assertThat(iterable.detectWith(Predicates2.lessThan(), 2), is(1));
        assertThat(iterable.detectWith(Predicates2.lessThan(), 3), isOneOf(2, 1));
        assertThat(iterable.detectWith(Predicates2.lessThan(), 4), isOneOf(3, 2, 1));

        assertThat(iterable.detectIfNone(Predicates.greaterThan(0), () -> 4), isOneOf(3, 2, 1));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(1), () -> 4), isOneOf(3, 2));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(2), () -> 4), is(3));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(3), () -> 4), is(4));

        assertThat(iterable.detectIfNone(Predicates.lessThan(1), () -> 4), is(4));
        assertThat(iterable.detectIfNone(Predicates.lessThan(2), () -> 4), is(1));
        assertThat(iterable.detectIfNone(Predicates.lessThan(3), () -> 4), isOneOf(2, 1));
        assertThat(iterable.detectIfNone(Predicates.lessThan(4), () -> 4), isOneOf(3, 2, 1));

        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 0, () -> 4), isOneOf(3, 2, 1));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 1, () -> 4), isOneOf(3, 2));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 2, () -> 4), is(3));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 3, () -> 4), is(4));

        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 1, () -> 4), is(4));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 2, () -> 4), is(1));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 3, () -> 4), isOneOf(2, 1));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 4, () -> 4), isOneOf(3, 2, 1));

        assertThat(iterable.detectOptional(Predicates.greaterThan(0)), isOneOf(Optional.of(3), Optional.of(2), Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(1)), isOneOf(Optional.of(3), Optional.of(2)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(2)), is(Optional.of(3)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(3)), is(Optional.empty()));

        assertThat(iterable.detectOptional(Predicates.lessThan(1)), is(Optional.empty()));
        assertThat(iterable.detectOptional(Predicates.lessThan(2)), is(Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.lessThan(3)), isOneOf(Optional.of(2), Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.lessThan(4)), isOneOf(Optional.of(3), Optional.of(2), Optional.of(1)));

        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 0), isOneOf(Optional.of(3), Optional.of(2), Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 1), isOneOf(Optional.of(3), Optional.of(2)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 2), is(Optional.of(3)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 3), is(Optional.empty()));

        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 1), is(Optional.empty()));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 2), is(Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 3), isOneOf(Optional.of(2), Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 4), isOneOf(Optional.of(3), Optional.of(2), Optional.of(1)));
    }

    @Override
    @Test
    default void RichIterable_minBy_maxBy()
    {
        // Without an ordering, min can be either ca or da
        RichIterable<String> minIterable = this.newWith("ed", "da", "ca", "bc", "ab");
        String actualMin = minIterable.minBy(string -> string.charAt(string.length() - 1));
        assertThat(actualMin, isOneOf("ca", "da"));
        assertEquals(minIterable.detect(each -> each.equals("ca") || each.equals("da")), actualMin);

        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().minBy(string -> string.charAt(string.length() - 1)));

        // Without an ordering, max can be either ca or da
        RichIterable<String> maxIterable = this.newWith("ew", "dz", "cz", "bx", "ay");
        String actualMax = maxIterable.maxBy(string -> string.charAt(string.length() - 1));
        assertThat(actualMax, isOneOf("cz", "dz"));
        assertEquals(maxIterable.detect(each -> each.equals("cz") || each.equals("dz")), actualMax);

        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().maxBy(string -> string.charAt(string.length() - 1)));
    }

    @Override
    @Test
    default void RichIterable_minByOptional_maxByOptional()
    {
        // Without an ordering, min can be either ca or da
        RichIterable<String> minIterable = this.newWith("ed", "da", "ca", "bc", "ab");
        String actualMin = minIterable.minByOptional(string -> string.charAt(string.length() - 1)).get();
        assertThat(actualMin, isOneOf("ca", "da"));
        assertEquals(minIterable.detect(each -> each.equals("ca") || each.equals("da")), actualMin);

        assertThat(this.<String>newWith().minByOptional(string -> string.charAt(string.length() - 1)), is(Optional.empty()));

        // Without an ordering, max can be either ca or da
        RichIterable<String> maxIterable = this.newWith("ew", "dz", "cz", "bx", "ay");
        String actualMax = maxIterable.maxByOptional(string -> string.charAt(string.length() - 1)).get();
        assertThat(actualMax, isOneOf("cz", "dz"));
        assertEquals(maxIterable.detect(each -> each.equals("cz") || each.equals("dz")), actualMax);

        assertThat(this.<String>newWith().maxByOptional(string -> string.charAt(string.length() - 1)), is(Optional.empty()));
    }
}
