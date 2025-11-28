/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.eclipse.collections.api.LazyBooleanIterable;
import org.eclipse.collections.api.LazyByteIterable;
import org.eclipse.collections.api.LazyCharIterable;
import org.eclipse.collections.api.LazyDoubleIterable;
import org.eclipse.collections.api.LazyFloatIterable;
import org.eclipse.collections.api.LazyIntIterable;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.LazyLongIterable;
import org.eclipse.collections.api.LazyShortIterable;
import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.sorted.SortedBag;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.FloatList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.ShortList;
import org.eclipse.collections.api.ordered.ReversibleIterable;
import org.eclipse.collections.api.ordered.SortedIterable;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.stack.ImmutableStack;
import org.eclipse.collections.impl.list.mutable.MultiReaderFastList;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.test.Verify.assertPostSerializedEqualsAndHashCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public interface IterableTestCase
{
    <T> Iterable<T> newWith(T... elements);

    boolean allowsDuplicates();

    default boolean allowsAdd()
    {
        return true;
    }

    default boolean allowsRemove()
    {
        return true;
    }

    static void assertIterablesEqual(Object o1, Object o2)
    {
        if (!(o1 instanceof Iterable)
                && !(o1 instanceof PrimitiveIterable)
                && !(o1 instanceof Map)
                && o1 != null)
        {
            fail("First argument is not an Iterable: " + o1);
        }
        if (!(o2 instanceof Iterable)
                && !(o2 instanceof PrimitiveIterable)
                && !(o2 instanceof Map)
                && o2 != null)
        {
            fail("Second argument is not an Iterable: " + o2);
        }
        if (o1 instanceof ListIterable<?> && o2 instanceof LazyIterable<?>)
        {
            assertIterablesEqual(o1, ((LazyIterable<?>) o2).toList());
            return;
        }
        if (o1 instanceof BooleanList && o2 instanceof LazyBooleanIterable)
        {
            assertIterablesEqual(o1, ((LazyBooleanIterable) o2).toList());
            return;
        }
        if (o1 instanceof ByteList && o2 instanceof LazyByteIterable)
        {
            assertIterablesEqual(o1, ((LazyByteIterable) o2).toList());
            return;
        }
        if (o1 instanceof CharList && o2 instanceof LazyCharIterable)
        {
            assertIterablesEqual(o1, ((LazyCharIterable) o2).toList());
            return;
        }
        if (o1 instanceof DoubleList && o2 instanceof LazyDoubleIterable)
        {
            assertIterablesEqual(o1, ((LazyDoubleIterable) o2).toList());
            return;
        }
        if (o1 instanceof FloatList && o2 instanceof LazyFloatIterable)
        {
            assertIterablesEqual(o1, ((LazyFloatIterable) o2).toList());
            return;
        }
        if (o1 instanceof IntList && o2 instanceof LazyIntIterable)
        {
            assertIterablesEqual(o1, ((LazyIntIterable) o2).toList());
            return;
        }
        if (o1 instanceof LongList && o2 instanceof LazyLongIterable)
        {
            assertIterablesEqual(o1, ((LazyLongIterable) o2).toList());
            return;
        }
        if (o1 instanceof ShortList && o2 instanceof LazyShortIterable)
        {
            assertIterablesEqual(o1, ((LazyShortIterable) o2).toList());
            return;
        }

        assertEquals(o1, o2);

        assertNotNull(o1, "Neither item should equal null");
        assertNotNull(o2, "Neither item should equal null");
        assertIterablesNotEqual("Neither item should equal new Object()", o1.equals(new Object()));
        assertIterablesNotEqual("Neither item should equal new Object()", o2.equals(new Object()));
        assertEquals(o1, o1);
        assertEquals(o2, o2);
        assertEquals(o1, o2);
        assertEquals(o2, o1);
        assertEquals(o1.hashCode(), o2.hashCode());

        checkNotSame(o1, o2);

        if (o1 instanceof MultiReaderFastList<?> || o2 instanceof MultiReaderFastList<?>)
        {
            return;
        }

        if (o1 instanceof SortedIterable<?> || o2 instanceof SortedIterable<?>
                || o1 instanceof ReversibleIterable<?> || o2 instanceof ReversibleIterable<?>
                || o1 instanceof List<?> || o2 instanceof List<?>
                || o1 instanceof SortedSet<?> || o2 instanceof SortedSet<?>)
        {
            Verify.assertIterablesEqual((Iterable<?>) o1, (Iterable<?>) o2);
            if (o1 instanceof SortedIterable<?> || o2 instanceof SortedIterable<?>)
            {
                Comparator<?> comparator1 = ((SortedIterable<?>) o1).comparator();
                Comparator<?> comparator2 = ((SortedIterable<?>) o2).comparator();
                if (comparator1 != null && comparator2 != null)
                {
                    assertSame(comparator1.getClass(), comparator2.getClass());
                }
            }
        }

        if (o1 instanceof SortedMap<?, ?> || o2 instanceof SortedMap<?, ?>)
        {
            assertIterablesEqual(((SortedMap<?, ?>) o1).keySet(), ((SortedMap<?, ?>) o2).keySet());
        }

        if (o1 instanceof Set<?> || o2 instanceof Set<?>)
        {
            Verify.assertSetsEqual((Set<?>) o1, (Set<?>) o2);
            if (o1 instanceof SortedSet<?> || o2 instanceof SortedSet<?>)
            {
                Verify.assertSortedSetsEqual((SortedSet<?>) o1, (SortedSet<?>) o2);
            }
        }

        if (o1 instanceof Bag<?> || o2 instanceof Bag<?>)
        {
            Verify.assertBagsEqual((Bag<?>) o1, (Bag<?>) o2);
            if (o1 instanceof SortedBag<?> || o2 instanceof SortedBag<?>)
            {
                Verify.assertSortedBagsEqual((SortedBag<?>) o1, (SortedBag<?>) o2);
            }
        }
    }

    static void checkNotSame(Object o1, Object o2)
    {
        if (o1 instanceof String && o2 instanceof String)
        {
            return;
        }
        if ((o1 instanceof Number && o2 instanceof Number)
                || (o1 instanceof Boolean && o2 instanceof Boolean)
                || o1 instanceof ImmutableCollection<?> && o2 instanceof ImmutableCollection<?>
                && ((ImmutableCollection<?>) o1).isEmpty() && ((ImmutableCollection<?>) o2).isEmpty()
                && !(o1 instanceof SortedIterable<?>) && !(o2 instanceof SortedIterable<?>)
                || o1 instanceof ImmutableStack<?> && o2 instanceof ImmutableStack<?>
                && ((ImmutableStack<?>) o1).isEmpty() && ((ImmutableStack<?>) o2).isEmpty())
        {
            assertSame(o1, o2);
            return;
        }
        assertNotSame(o1, o2);
    }

    static void assertIterablesNotEqual(Object o1, Object o2)
    {
        assertNotEquals(o1, o2);
        assertNotEquals(o2, o1);

        assertNotNull(o1, "Neither item should equal null");
        assertNotNull(o2, "Neither item should equal null");
        assertNotEquals("Neither item should equal new Object()", o1.equals(new Object()));
        assertNotEquals("Neither item should equal new Object()", o2.equals(new Object()));
        assertEquals(o1, o1);
        assertEquals(o2, o2);
    }

    static <T> void addAllTo(T[] elements, MutableCollection<T> result)
    {
        for (T element : elements)
        {
            if (!result.add(element))
            {
                throw new IllegalStateException();
            }
        }
    }

    @Test
    default void Object_PostSerializedEqualsAndHashCode()
    {
        Iterable<Integer> iterable = this.newWith(3, 2, 1);
        Object deserialized = SerializeTestHelper.serializeDeserialize(iterable);
        assertNotSame(iterable, deserialized);

        if (!this.allowsDuplicates())
        {
            return;
        }

        Iterable<Integer> iterable2 = this.newWith(3, 3, 3, 2, 2, 1);
        Object deserialized2 = SerializeTestHelper.serializeDeserialize(iterable2);
        assertNotSame(iterable2, deserialized2);
    }

    @Test
    default void Object_equalsAndHashCode()
    {
        assertPostSerializedEqualsAndHashCode(this.newWith(3, 2, 1));

        assertIterablesNotEqual(this.newWith(4, 3, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(4, 3, 2, 1));

        assertIterablesNotEqual(this.newWith(2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(2, 1));

        assertIterablesNotEqual(this.newWith(4, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(4, 2, 1));

        if (!this.allowsDuplicates())
        {
            return;
        }

        Verify.assertEqualsAndHashCode(this.newWith(3, 3, 3, 2, 2, 1), this.newWith(3, 3, 3, 2, 2, 1));

        assertIterablesNotEqual(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(3, 3, 2, 1));

        assertIterablesNotEqual(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 2, 1), this.newWith(3, 3, 2, 1));
    }

    @Test
    default void Iterable_hasNext()
    {
        assertTrue(this.newWith(3, 2, 1).iterator().hasNext());
        assertFalse(this.newWith().iterator().hasNext());
    }

    @Test
    default void Iterable_next()
    {
        Iterator<Integer> iterator = this.newWith(3, 2, 1).iterator();
        MutableSet<Integer> set = Sets.mutable.with();
        assertTrue(set.add(iterator.next()));
        assertTrue(set.add(iterator.next()));
        assertTrue(set.add(iterator.next()));
        assertIterablesEqual(Sets.immutable.with(3, 2, 1), set);

        assertThrows(NoSuchElementException.class, () -> this.newWith().iterator().next());

        Iterable<Integer> iterable2 = this.newWith(3, 2, 1);
        Iterator<Integer> iterator2 = iterable2.iterator();
        assertTrue(iterator2.hasNext());
        iterator2.next();
        assertTrue(iterator2.hasNext());
        iterator2.next();
        assertTrue(iterator2.hasNext());
        iterator2.next();
        assertFalse(iterator2.hasNext());
        assertThrows(NoSuchElementException.class, iterator2::next);

        Iterator<Integer> iterator3 = iterable2.iterator();
        iterator3.next();
        iterator3.next();
        iterator3.next();
        assertThrows(NoSuchElementException.class, iterator3::next);
        assertThrows(NoSuchElementException.class, iterator3::next);
    }

    @Test
    void Iterable_remove();

    @Test
    void Iterable_toString();
}
