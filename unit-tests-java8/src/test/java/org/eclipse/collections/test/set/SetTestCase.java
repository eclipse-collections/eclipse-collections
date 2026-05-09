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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.test.CollectionTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.test.Verify.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface SetTestCase extends CollectionTestCase
{
    @Override
    <T> Set<T> newWith(T... elements);

    @Override
    default boolean allowsDuplicates()
    {
        return false;
    }

    @Override
    default OrderingType getOrderingType()
    {
        return OrderingType.UNORDERED;
    }

    @Override
    @Test
    default void Iterable_next()
    {
        CollectionTestCase.super.Iterable_next();

        if (!this.allowsIterator())
        {
            assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator().next());
            return;
        }

        Set<Integer> iterable = this.newWith(3, 2, 1);

        MutableSet<Integer> mutableSet = Sets.mutable.with();

        Iterator<Integer> iterator = iterable.iterator();
        while (iterator.hasNext())
        {
            Integer integer = iterator.next();
            assertTrue(mutableSet.add(integer));
        }

        // Use Set semantics (order-agnostic) so this works for sorted views like TreeMap.keySet().
        Verify.assertSetsEqual(iterable, mutableSet);
        assertFalse(iterator.hasNext());
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        CollectionTestCase.super.Iterable_remove();

        if (!this.allowsIterator() || !this.allowsRemove())
        {
            return;
        }

        Set<Integer> set = this.newWith(3, 2, 1);
        Iterator<Integer> iterator = set.iterator();
        iterator.next();
        iterator.remove();
        assertThat(set, isOneOf(
                this.newWith(1, 2),
                this.newWith(1, 3),
                this.newWith(2, 3)));
    }

    @Override
    @Test
    default void Collection_add()
    {
        CollectionTestCase.super.Collection_add();

        if (!this.allowsAdd())
        {
            return;
        }

        Collection<Integer> collection = this.newWith(1, 2, 3);
        assertFalse(collection.add(3));
    }

    @Override
    @Test
    default void Collection_size()
    {
        CollectionTestCase.super.Collection_size();
        assertThat(this.newWith(3, 2, 1), hasSize(3));
        assertThat(this.newWith(), hasSize(0));
    }
}
