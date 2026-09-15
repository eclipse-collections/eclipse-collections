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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.test.Verify.assertContains;
import static org.eclipse.collections.impl.test.Verify.assertNotContains;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface CollectionTestCase extends IterableTestCase, CollisionsTestCase
{
    @Override
    <T> Collection<T> newWith(T... elements);

    @Test
    default void Collection_size()
    {
        if (this.allowsDuplicates())
        {
            Collection<Integer> collection = this.newWith(3, 3, 3, 2, 2, 1);
            assertThat(collection, hasSize(6));
        }
        else
        {
            Collection<Integer> collection = this.newWith(3, 2, 1);
            assertThat(collection, hasSize(3));
        }
        assertThat(this.newWith(), hasSize(0));
    }

    @Test
    default void Collection_contains()
    {
        Collection<Integer> collection = this.newWith(3, 2, 1);
        assertTrue(collection.contains(1));
        assertTrue(collection.contains(2));
        assertTrue(collection.contains(3));
        assertFalse(collection.contains(4));
        assertFalse(collection.contains(0));
        assertFalse(collection.contains(-1));
        assertFalse(collection.contains(Integer.MAX_VALUE));
        assertFalse(collection.contains(Integer.MIN_VALUE));
    }

    @Test
    default void Collection_add()
    {
        Collection<Integer> collection = this.newWith(3, 2, 1);

        if (!this.allowsAdd())
        {
            assertThrows(UnsupportedOperationException.class, () -> collection.add(1));
            assertThrows(UnsupportedOperationException.class, () -> collection.add(2));
            assertThrows(UnsupportedOperationException.class, () -> collection.add(3));
            assertThrows(UnsupportedOperationException.class, () -> collection.add(4));
            assertIterablesEqual(this.newWith(3, 2, 1), collection);
            return;
        }

        assertTrue(collection.add(4));
        assertTrue(collection.contains(4));
        assertTrue(collection.contains(3));
        assertTrue(collection.contains(2));
        assertTrue(collection.contains(1));
        assertEquals(this.allowsDuplicates(), collection.add(4));
        assertTrue(collection.contains(4));
        if (this.allowsDuplicates())
        {
            assertIterablesEqual(this.newWith(3, 2, 1, 4, 4), collection);
        }
        else
        {
            assertIterablesEqual(this.newWith(3, 2, 1, 4), collection);
        }

        Collection<Integer> collection2 = this.newWith();
        for (Integer each : COLLISIONS)
        {
            assertFalse(collection2.contains(each));
            assertTrue(collection2.add(each));
            assertTrue(collection2.contains(each));
            assertEquals(this.allowsDuplicates(), collection2.add(each));
            assertTrue(collection2.contains(each));
        }
    }

    @Test
    default void Collection_remove_removeAll()
    {
        if (!this.allowsRemove())
        {
            Collection<Integer> collection = this.newWith(3, 2, 1);
            assertThrows(UnsupportedOperationException.class, () -> collection.remove(1));
            assertThrows(UnsupportedOperationException.class, () -> collection.remove(4));
            assertThrows(UnsupportedOperationException.class, () -> collection.removeAll(Lists.mutable.with(1, 2)));
            assertThrows(UnsupportedOperationException.class, () -> collection.removeAll(Lists.mutable.with(4, 5)));
            assertIterablesEqual(this.newWith(3, 2, 1), collection);
            return;
        }

        {
            Collection<Integer> collection = this.newWith(3, 2, 1);
            assertFalse(collection.remove(4));
            assertIterablesEqual(this.newWith(3, 2, 1), collection);
            assertTrue(collection.remove(3));
            assertIterablesEqual(this.newWith(2, 1), collection);
            assertTrue(collection.remove(2));
            assertIterablesEqual(this.newWith(1), collection);
            assertTrue(collection.remove(1));
            assertIterablesEqual(this.newWith(), collection);
        }

        if (this.allowsDuplicates())
        {
            Collection<Integer> collection = this.newWith(3, 3, 3, 2, 2, 1);
            assertTrue(collection.remove(3));
            assertTrue(collection.remove(2));
            assertTrue(collection.remove(1));
            assertIterablesEqual(this.newWith(3, 3, 2), collection);

            assertTrue(collection.remove(3));
            assertTrue(collection.remove(2));
            assertFalse(collection.remove(1));
            assertIterablesEqual(this.newWith(3), collection);

            assertTrue(collection.remove(3));
            assertFalse(collection.remove(2));
            assertFalse(collection.remove(1));
            assertIterablesEqual(this.newWith(), collection);

            assertFalse(collection.remove(3));
            assertFalse(collection.remove(2));
            assertFalse(collection.remove(1));
            assertIterablesEqual(this.newWith(), collection);
        }

        Integer[] array = COLLISIONS.toArray(new Integer[]{});

        {
            Collection<Integer> collection = this.newWith(array);
            for (Integer each : COLLISIONS)
            {
                assertContains(each, collection);
                assertTrue(collection.remove(each));
                assertNotContains(each, collection);
                assertFalse(collection.remove(each));
                assertNotContains(each, collection);
            }
        }

        if (this.allowsDuplicates())
        {
            Collection<Integer> collection = this.newWith(CollectionTestCase.concat(array, array));
            for (Integer each : COLLISIONS)
            {
                assertContains(each, collection);
                assertTrue(collection.remove(each));
                assertTrue(collection.contains(each));
                assertTrue(collection.remove(each));
                assertNotContains(each, collection);
                assertFalse(collection.remove(each));
                assertNotContains(each, collection);
            }
        }

        {
            Collection<Integer> collection = this.newWith(3, 2, 1);
            assertTrue(collection.removeAll(Lists.mutable.with(1, 2, 4)));
            assertIterablesEqual(this.newWith(3), collection);

            Collection<Integer> collection2 = this.newWith(3, 2, 1);
            assertFalse(collection2.removeAll(Lists.mutable.with(5, 4)));
            assertIterablesEqual(this.newWith(3, 2, 1), collection2);

            Collection<Integer> collection3 = this.newWith(3, 2, 1);
            assertFalse(collection3.removeAll(Lists.mutable.with()));
            assertIterablesEqual(this.newWith(3, 2, 1), collection3);

            Collection<Integer> collection4 = this.newWith(5, 4, 3, 2, 1);
            assertTrue(collection4.removeAll(Lists.mutable.with(4, 2)));
            assertIterablesEqual(this.newWith(5, 3, 1), collection4);

            Collection<Integer> collection5 = this.newWith(3, 2, 1);
            assertTrue(collection5.removeAll(Lists.mutable.with(1, 2, 3)));
            assertIterablesEqual(this.newWith(), collection5);

            Collection<Integer> collection6 = this.newWith(3, 2, 1);
            assertTrue(collection6.removeAll(Lists.mutable.with(1, 2, 3, 4)));
            assertIterablesEqual(this.newWith(), collection6);
        }

        if (this.allowsDuplicates())
        {
            Collection<Integer> collection = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
            assertTrue(collection.removeAll(Lists.mutable.with(3, 1)));
            assertFalse(collection.removeAll(Lists.mutable.with(3, 1)));
            assertIterablesEqual(this.newWith(4, 4, 4, 4, 2, 2), collection);
        }
    }

    static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    @Test
    default void Collection_clear()
    {
        Collection<Integer> collection = this.newWith(3, 2, 1);

        if (!this.allowsRemove())
        {
            assertThrows(UnsupportedOperationException.class, () -> this.newWith().clear());
            assertThrows(UnsupportedOperationException.class, () -> this.newWith(3).clear());
            assertThrows(UnsupportedOperationException.class, () -> this.newWith(3, 2).clear());
            assertThrows(UnsupportedOperationException.class, () -> this.newWith(3, 2, 1).clear());

            assertThrows(UnsupportedOperationException.class, collection::clear);
            assertIterablesEqual(this.newWith(3, 2, 1), collection);
            assertThat(collection, is(not(empty())));
            return;
        }

        assertThat(collection, is(not(empty())));
        collection.clear();
        assertThat(collection, is(empty()));
        assertThat(collection, hasSize(0));
        assertIterablesEqual(this.newWith(), collection);
        collection.clear();
        assertThat(collection, is(empty()));
    }

    @Test
    default void Collection_toArray()
    {
        Collection<Integer> collection = this.newWith(3, 2, 1);
        Object[] array = collection.toArray();
        assertEquals(collection.size(), array.length);
        switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> assertArrayEquals(new Object[]{1, 2, 3}, array);
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertArrayEquals(new Object[]{3, 2, 1}, array);
            case UNORDERED -> assertIterablesEqual(Bags.immutable.with(3, 2, 1), HashBag.newBagWith(array));
            default -> throw new AssertionError("Unexpected ordering type: " + this.getOrderingType());
        }

        if (this.allowsDuplicates())
        {
            Collection<Integer> withDuplicates = this.newWith(3, 3, 3, 2, 2, 1);
            Object[] array2 = withDuplicates.toArray();
            assertEquals(withDuplicates.size(), array2.length);
            switch (this.getOrderingType())
            {
                case SORTED_NATURAL -> assertArrayEquals(new Object[]{1, 2, 2, 3, 3, 3}, array2);
                case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertArrayEquals(new Object[]{3, 3, 3, 2, 2, 1}, array2);
                case UNORDERED -> assertIterablesEqual(Bags.immutable.with(3, 3, 3, 2, 2, 1), HashBag.newBagWith(array2));
                default -> throw new AssertionError("Unexpected ordering type: " + this.getOrderingType());
            }
        }

        Object[] emptyArray = this.newWith().toArray();
        assertEquals(0, emptyArray.length);

        // toArray(T[]) - exact size target
        Integer[] exactSize = new Integer[collection.size()];
        Integer[] result = collection.toArray(exactSize);
        switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> assertArrayEquals(new Object[]{1, 2, 3}, result);
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertArrayEquals(new Object[]{3, 2, 1}, result);
            case UNORDERED -> assertIterablesEqual(Bags.immutable.with(3, 2, 1), HashBag.newBagWith(result));
            default -> throw new AssertionError("Unexpected ordering type: " + this.getOrderingType());
        }

        // toArray(T[]) - larger target (null-terminated, remaining untouched)
        Integer[] larger = new Integer[collection.size() + 2];
        larger[collection.size()] = 99;
        larger[collection.size() + 1] = 99;
        Integer[] result2 = collection.toArray(larger);
        assertEquals(larger, result2);
        assertNull(result2[collection.size()]);
        switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> assertArrayEquals(new Integer[]{1, 2, 3, null, 99}, result2);
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertArrayEquals(new Integer[]{3, 2, 1, null, 99}, result2);
            case UNORDERED ->
            {
                assertIterablesEqual(
                        Bags.immutable.with(3, 2, 1),
                        HashBag.newBagWith(result2[0], result2[1], result2[2]));
                assertEquals(99, result2[4]);
            }
            default -> throw new AssertionError("Unexpected ordering type: " + this.getOrderingType());
        }

        // toArray(T[]) - smaller target (new array allocated)
        Integer[] smaller = new Integer[1];
        Integer[] result3 = collection.toArray(smaller);
        assertEquals(collection.size(), result3.length);
        switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> assertArrayEquals(new Object[]{1, 2, 3}, result3);
            case INSERTION_ORDER, SORTED_REVERSE_NATURAL -> assertArrayEquals(new Object[]{3, 2, 1}, result3);
            case UNORDERED -> assertIterablesEqual(Bags.immutable.with(3, 2, 1), HashBag.newBagWith(result3));
            default -> throw new AssertionError("Unexpected ordering type: " + this.getOrderingType());
        }

        Integer[] emptyTarget = new Integer[0];
        Integer[] emptyResult = this.<Integer>newWith().toArray(emptyTarget);
        assertEquals(0, emptyResult.length);
    }

    @Test
    default void Collection_containsAll()
    {
        Collection<Integer> collection = this.newWith(3, 2, 1);

        assertTrue(collection.containsAll(Lists.mutable.with(1)));
        assertTrue(collection.containsAll(Lists.mutable.with(1, 2)));
        assertTrue(collection.containsAll(Lists.mutable.with(1, 2, 3)));
        assertTrue(collection.containsAll(Lists.mutable.with(3, 3, 3)));
        assertTrue(collection.containsAll(Lists.mutable.with()));
        assertFalse(collection.containsAll(Lists.mutable.with(4)));
        assertFalse(collection.containsAll(Lists.mutable.with(1, 2, 3, 4)));
        assertFalse(collection.containsAll(Lists.mutable.with(4, 5, 6)));

        Collection<Integer> empty = this.newWith();
        assertTrue(empty.containsAll(Lists.mutable.with()));
        assertFalse(empty.containsAll(Lists.mutable.with(1)));
    }

    @Test
    default void Collection_addAll()
    {
        Collection<Integer> collection = this.newWith(3, 2, 1);

        if (!this.allowsAdd())
        {
            assertThrows(UnsupportedOperationException.class, () -> collection.addAll(Lists.mutable.with(4, 5)));
            assertIterablesEqual(this.newWith(3, 2, 1), collection);
            return;
        }

        assertTrue(collection.addAll(Lists.mutable.with(4, 5)));
        assertTrue(collection.contains(4));
        assertTrue(collection.contains(5));
        assertTrue(collection.containsAll(Lists.mutable.with(1, 2, 3, 4, 5)));

        Collection<Integer> collection2 = this.newWith(3, 2, 1);
        assertEquals(this.allowsDuplicates(), collection2.addAll(Lists.mutable.with(1, 2)));
        if (this.allowsDuplicates())
        {
            assertIterablesEqual(this.newWith(3, 2, 1, 1, 2), collection2);
        }
        else
        {
            assertIterablesEqual(this.newWith(3, 2, 1), collection2);
        }

        Collection<Integer> collection3 = this.newWith(3, 2, 1);
        assertFalse(collection3.addAll(Lists.mutable.with()));
        assertIterablesEqual(this.newWith(3, 2, 1), collection3);
    }

    @Test
    default void Collection_retainAll()
    {
        Collection<Integer> collection = this.newWith(3, 2, 1);

        if (!this.allowsRemove())
        {
            assertThrows(UnsupportedOperationException.class, () -> collection.retainAll(Lists.mutable.with(1, 2)));
            assertIterablesEqual(this.newWith(3, 2, 1), collection);
            return;
        }

        {
            Collection<Integer> collection1 = this.newWith(3, 2, 1);
            assertTrue(collection1.retainAll(Lists.mutable.with(1, 2)));
            assertIterablesEqual(this.newWith(2, 1), collection1);
        }

        {
            Collection<Integer> collection2 = this.newWith(3, 2, 1);
            assertFalse(collection2.retainAll(Lists.mutable.with(1, 2, 3)));
            assertIterablesEqual(this.newWith(3, 2, 1), collection2);
        }

        {
            Collection<Integer> collection3 = this.newWith(3, 2, 1);
            assertFalse(collection3.retainAll(Lists.mutable.with(1, 2, 3, 4)));
            assertIterablesEqual(this.newWith(3, 2, 1), collection3);
        }

        {
            Collection<Integer> collection4 = this.newWith(3, 2, 1);
            assertTrue(collection4.retainAll(Lists.mutable.with()));
            assertIterablesEqual(this.newWith(), collection4);
        }

        {
            Collection<Integer> collection5 = this.newWith(3, 2, 1);
            assertTrue(collection5.retainAll(Lists.mutable.with(4, 5)));
            assertIterablesEqual(this.newWith(), collection5);
        }

        if (this.allowsDuplicates())
        {
            Collection<Integer> collection6 = this.newWith(3, 3, 3, 2, 2, 1);
            assertTrue(collection6.retainAll(Lists.mutable.with(1, 3)));
            assertIterablesEqual(this.newWith(3, 3, 3, 1), collection6);
        }
    }
}
