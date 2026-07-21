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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.eclipse.collections.api.InternalIterable;
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
import org.eclipse.collections.api.factory.Bags;
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

import static org.eclipse.collections.impl.test.Verify.assertNotSerializable;
import static org.eclipse.collections.impl.test.Verify.assertPostSerializedEqualsAndHashCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;
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

    default boolean allowsSerialization()
    {
        return true;
    }

    default boolean allowsIterator()
    {
        return true;
    }

    static boolean allowsIterator(Object o)
    {
        return o == null || !o.getClass().getSimpleName().endsWith("NoIterator");
    }

    enum OrderingType
    {
        UNORDERED,
        INSERTION_ORDER,
        SORTED_NATURAL,
        SORTED_REVERSE_NATURAL,
    }

    default OrderingType getOrderingType()
    {
        return OrderingType.INSERTION_ORDER;
    }

    static void assertIterablesEqual(Object o1, Object o2)
    {
        assertNotNull(o1, "Neither item should equal null");
        assertNotNull(o2, "Neither item should equal null");
        assertIterablesNotEqual("Neither item should equal new Object()", o1.equals(new Object()));
        assertIterablesNotEqual("Neither item should equal new Object()", o2.equals(new Object()));

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

        if (o1 instanceof ListIterable<?> && o2 instanceof LazyIterable<?> lazyIterable)
        {
            assertIterablesEqual(o1, lazyIterable.toList());
            return;
        }
        if (o1 instanceof LazyIterable<?> lazy1 && o2 instanceof LazyIterable<?> lazy2)
        {
            assertIterablesEqual(lazy1.toList(), lazy2.toList());
            return;
        }
        if (o1 instanceof BooleanList && o2 instanceof LazyBooleanIterable lazyBooleanIterable)
        {
            assertIterablesEqual(o1, lazyBooleanIterable.toList());
            return;
        }
        if (o1 instanceof ByteList && o2 instanceof LazyByteIterable lazyByteIterable)
        {
            assertIterablesEqual(o1, lazyByteIterable.toList());
            return;
        }
        if (o1 instanceof CharList && o2 instanceof LazyCharIterable lazyCharIterable)
        {
            assertIterablesEqual(o1, lazyCharIterable.toList());
            return;
        }
        if (o1 instanceof DoubleList && o2 instanceof LazyDoubleIterable lazyDoubleIterable)
        {
            assertIterablesEqual(o1, lazyDoubleIterable.toList());
            return;
        }
        if (o1 instanceof FloatList && o2 instanceof LazyFloatIterable lazyFloatIterable)
        {
            assertIterablesEqual(o1, lazyFloatIterable.toList());
            return;
        }
        if (o1 instanceof IntList && o2 instanceof LazyIntIterable lazyIntIterable)
        {
            assertIterablesEqual(o1, lazyIntIterable.toList());
            return;
        }
        if (o1 instanceof LongList && o2 instanceof LazyLongIterable lazyLongIterable)
        {
            assertIterablesEqual(o1, lazyLongIterable.toList());
            return;
        }
        if (o1 instanceof ShortList && o2 instanceof LazyShortIterable lazyShortIterable)
        {
            assertIterablesEqual(o1, lazyShortIterable.toList());
            return;
        }

        if (!allowsIterator(o1))
        {
            assertThrows(AssertionError.class, () -> ((Iterable<?>) o1).iterator());
        }
        if (!allowsIterator(o2))
        {
            assertThrows(AssertionError.class, () -> ((Iterable<?>) o2).iterator());
        }

        if (!allowsEquals(o1) || !allowsEquals(o2))
        {
            if (allowsIterator(o1))
            {
                assertEquals(
                        Bags.mutable.withAll((Collection<?>) o1),
                        Bags.mutable.withAll((Collection<?>) o2));
            }

            if (allowsIterator(o2))
            {
                assertEquals(
                        Bags.mutable.withAll((Collection<?>) o2),
                        Bags.mutable.withAll((Collection<?>) o1));
            }

            if (!allowsEquals(o1) && !allowsEquals(o2))
            {
                return;
            }
        }

        if (allowsIterator(o1))
        {
            assertEquals(o1, o1);
            assertEquals(o1, o2);
        }

        if (allowsIterator(o2))
        {
            assertEquals(o2, o2);
            assertEquals(o2, o1);
        }

        assertEquals(o1.hashCode(), o2.hashCode());
        checkNotSame(o1, o2);

        if (o1 instanceof MultiReaderFastList<?> || o2 instanceof MultiReaderFastList<?>)
        {
            return;
        }

        // Verify.assertIterablesEqual uses forEach for InternalIterable types, otherwise iterator.
        boolean bothInternal = o1 instanceof InternalIterable<?> && o2 instanceof InternalIterable<?>;

        if (o1 instanceof SortedIterable<?> || o2 instanceof SortedIterable<?>
                || o1 instanceof ReversibleIterable<?> || o2 instanceof ReversibleIterable<?>
                || o1 instanceof List<?> || o2 instanceof List<?>
                || o1 instanceof SortedSet<?> || o2 instanceof SortedSet<?>)
        {
            if (allowsIterator(o1) || bothInternal)
            {
                Verify.assertIterablesEqual((Iterable<?>) o1, (Iterable<?>) o2);
                if (o1 instanceof SortedIterable<?> || o2 instanceof SortedIterable<?>)
                {
                    assertTrue(IterableTestCase.haveCompatibleComparators(o1, o2));
                }
            }

            if (allowsIterator(o2) || bothInternal)
            {
                Verify.assertIterablesEqual((Iterable<?>) o2, (Iterable<?>) o1);
                if (o1 instanceof SortedIterable<?> || o2 instanceof SortedIterable<?>)
                {
                    assertTrue(IterableTestCase.haveCompatibleComparators(o2, o1));
                }
            }
        }

        if (o1 instanceof SortedMap<?, ?> || o2 instanceof SortedMap<?, ?>)
        {
            // TODO: Change UnifiedMap.KeySet.equals and AbstractMutableBiMap.EntrySet.equals to iterate over `this` instead of `other` and then these assertions can be made on all Maps.
            assertIterablesEqual(((Map<?, ?>) o1).keySet(), ((Map<?, ?>) o2).keySet());
            assertIterablesEqual(((Map<?, ?>) o1).entrySet(), ((Map<?, ?>) o2).entrySet());
            assertIterablesEqual(((SortedMap<?, ?>) o1).values(), ((SortedMap<?, ?>) o2).values());
        }

        if (o1 instanceof Set<?> || o2 instanceof Set<?>)
        {
            if (allowsIterator(o1) || bothInternal)
            {
                Verify.assertSetsEqual((Set<?>) o1, (Set<?>) o2);
                if (o1 instanceof SortedSet<?> || o2 instanceof SortedSet<?>)
                {
                    Verify.assertSortedSetsEqual((SortedSet<?>) o1, (SortedSet<?>) o2);
                }
            }

            if (allowsIterator(o2) || bothInternal)
            {
                Verify.assertSetsEqual((Set<?>) o2, (Set<?>) o1);
                if (o1 instanceof SortedSet<?> || o2 instanceof SortedSet<?>)
                {
                    Verify.assertSortedSetsEqual((SortedSet<?>) o2, (SortedSet<?>) o1);
                }
            }
        }

        if (o1 instanceof Bag<?> || o2 instanceof Bag<?>)
        {
            if (allowsIterator(o1) || bothInternal)
            {
                Verify.assertBagsEqual((Bag<?>) o1, (Bag<?>) o2);
                if (o1 instanceof SortedBag<?> || o2 instanceof SortedBag<?>)
                {
                    Verify.assertSortedBagsEqual((SortedBag<?>) o1, (SortedBag<?>) o2);
                }
            }

            if (allowsIterator(o2) || bothInternal)
            {
                Verify.assertBagsEqual((Bag<?>) o2, (Bag<?>) o1);
                if (o1 instanceof SortedBag<?> || o2 instanceof SortedBag<?>)
                {
                    Verify.assertSortedBagsEqual((SortedBag<?>) o2, (SortedBag<?>) o1);
                }
            }
        }
    }

    static void checkNotSame(Object expected, Object actual)
    {
        if (expected instanceof String && actual instanceof String)
        {
            return;
        }
        if ((expected instanceof Number && actual instanceof Number)
                || (expected instanceof Boolean && actual instanceof Boolean)
                || expected instanceof ImmutableCollection<?> immutableCollection1 && actual instanceof ImmutableCollection<?> immutableCollection2
                && immutableCollection1.isEmpty() && immutableCollection2.isEmpty()
                && !(expected instanceof SortedIterable<?>) && !(actual instanceof SortedIterable<?>)
                || expected instanceof ImmutableStack<?> immutableStack1 && actual instanceof ImmutableStack<?> immutableStack2
                && immutableStack1.isEmpty() && immutableStack2.isEmpty())
        {
            assertSame(expected, actual);
            return;
        }
        if (IterableTestCase.areEquivalentEmptyCollections(expected, actual))
        {
            return;
        }
        assertNotSame(expected, actual);
    }

    private static boolean haveCompatibleComparators(Object expected, Object actual)
    {
        if (!(expected instanceof SortedIterable<?>) && !(actual instanceof SortedIterable<?>))
        {
            return true;
        }
        if (!(expected instanceof SortedIterable<?> sortedIterable1) || !(actual instanceof SortedIterable<?> sortedIterable2))
        {
            return false;
        }

        Comparator<?> comparator1 = sortedIterable1.comparator();
        Comparator<?> comparator2 = sortedIterable2.comparator();
        if (comparator1 == comparator2)
        {
            return true;
        }

        // Only check class compatibility if both comparators are non-null
        // (matching the original logic)
        if (comparator1 != null && comparator2 != null)
        {
            return comparator1.getClass() == comparator2.getClass();
        }

        // If either is null, consider them compatible
        return true;
    }

    static boolean allowsEquals(Object o)
    {
        return !(o instanceof Collection<?>)
                || o instanceof Set<?>
                || o instanceof List<?>
                || o instanceof Bag<?>;
    }

    private static boolean areEquivalentEmptyCollections(Object expected, Object actual)
    {
        return expected instanceof ImmutableCollection<?> immutableCollection1 && actual instanceof ImmutableCollection<?> immutableCollection2
                && immutableCollection1.isEmpty() && immutableCollection2.isEmpty()
                && (expected instanceof SortedIterable<?> || actual instanceof SortedIterable<?>);
    }

    static void assertIterablesNotEqual(Object expected, Object actual)
    {
        assertNotEquals(expected, actual);
        assertNotEquals(actual, expected);

        assertNotNull(expected, "Neither item should equal null");
        assertNotNull(actual, "Neither item should equal null");
        assertNotEquals("Neither item should equal new Object()", expected.equals(new Object()));
        assertNotEquals("Neither item should equal new Object()", actual.equals(new Object()));
        assertEquals(expected, expected);
        assertEquals(actual, actual);
    }

    static <T> void addAllTo(T[] elements, MutableCollection<T> result)
    {
        for (T element : elements)
        {
            if (!result.add(element))
            {
                throw new IllegalStateException("Failed to add element: " + element + " to collection: " + result);
            }
        }
    }

    @Test
    default void Object_equalsAndHashCode()
    {
        Iterable<Integer> iterable = this.newWith(3, 2, 1);

        if (!this.allowsSerialization())
        {
            assertNotSerializable(iterable);
        }
        else if (allowsEquals(iterable))
        {
            assertPostSerializedEqualsAndHashCode(iterable);
        }
        else
        {
            // Map view collections (keySet, values) are serializable but inherit
            // identity-based equals from AbstractCollection — sometimes the round-trip
            // even changes the runtime type (UnifiedMap.values() serializes as FastList),
            // so we can't use equals() to verify. Compare contents as bags (order- and
            // type-agnostic). When the view also lacks iterator() support, fall back to
            // size + contains checks against the known elements (3, 2, 1).
            Collection<?> roundTripped = (Collection<?>) SerializeTestHelper.serializeDeserialize(iterable);
            if (this.allowsIterator())
            {
                assertEquals(Bags.mutable.withAll(iterable), Bags.mutable.withAll(roundTripped));
            }
            else
            {
                assertEquals(3, roundTripped.size());
                assertTrue(roundTripped.contains(3));
                assertTrue(roundTripped.contains(2));
                assertTrue(roundTripped.contains(1));
            }
        }

        if (!this.allowsIterator())
        {
            // Without iterator(), all the structural inequality assertions below would
            // delegate through .equals() to iterator() and throw AssertionError.
            return;
        }

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

        if (this.allowsSerialization() && allowsEquals(this.newWith(3, 3, 3, 2, 2, 1)))
        {
            assertPostSerializedEqualsAndHashCode(this.newWith(3, 3, 3, 2, 2, 1));
        }

        if (allowsEquals(this.newWith(3, 3, 3, 2, 2, 1)))
        {
            Verify.assertEqualsAndHashCode(this.newWith(3, 3, 3, 2, 2, 1), this.newWith(3, 3, 3, 2, 2, 1));
        }
        else
        {
            // Without structural equals, two instances with the same content are not equal
            assertNotEquals(this.newWith(3, 3, 3, 2, 2, 1), this.newWith(3, 3, 3, 2, 2, 1));
        }

        assertIterablesNotEqual(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(3, 3, 2, 1));

        assertIterablesNotEqual(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 2, 1), this.newWith(3, 3, 2, 1));
    }

    @Test
    default void Iterable_hasNext()
    {
        if (!this.allowsIterator())
        {
            assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator().hasNext());
            return;
        }
        assertTrue(this.newWith(3, 2, 1).iterator().hasNext());
        assertFalse(this.newWith().iterator().hasNext());
    }

    @Test
    default void Iterable_next()
    {
        if (!this.allowsIterator())
        {
            assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator().next());
            return;
        }
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
    default void Iterable_remove()
    {
        if (!this.allowsIterator())
        {
            assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator().next());
            return;
        }

        if (!this.allowsRemove())
        {
            Iterable<Integer> iterable = this.newWith(3, 2, 1);
            Iterator<Integer> iterator = iterable.iterator();
            iterator.next();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
            return;
        }

        Iterator<Integer> freshIterator = this.newWith(3, 2, 1).iterator();
        assertThrows(IllegalStateException.class, freshIterator::remove);

        Iterable<Integer> iterable = this.newWith(3, 2, 1);
        Iterator<Integer> iterator = iterable.iterator();
        iterator.next();
        iterator.remove();
        assertThat(Bags.mutable.withAll(iterable), isOneOf(
                Bags.mutable.with(1, 2),
                Bags.mutable.with(1, 3),
                Bags.mutable.with(2, 3)));
    }

    @Test
    default void Iterable_toString()
    {
        assertThat(this.newWith(2, 1).toString(), isOneOf("[1, 2]", "[2, 1]"));
        assertThat(this.newWith(3, 2, 1).toString(), isOneOf(
                "[3, 2, 1]", "[3, 1, 2]", "[2, 3, 1]", "[2, 1, 3]", "[1, 3, 2]", "[1, 2, 3]"));
    }
}
