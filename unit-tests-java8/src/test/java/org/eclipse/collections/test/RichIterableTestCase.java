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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.Counter;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.BooleanHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.ByteHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.CharHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.DoubleHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.FloatHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.IntHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.LongHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.ShortHashBag;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.primitive.IntInterval;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesNotEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("UnnecessaryCodeBlock")
public interface RichIterableTestCase extends IterableTestCase
{
    @Override
    <T> RichIterable<T> newWith(T... elements);

    <T> RichIterable<T> getExpectedFiltered(T... elements);

    <T> RichIterable<T> getExpectedTransformed(T... elements);

    <T> MutableCollection<T> newMutableForFilter(T... elements);

    <T> MutableCollection<T> newMutableForTransform(T... elements);

    @Test
    default void newMutable_sanity()
    {
        assertIterablesEqual(this.getExpectedFiltered(3, 2, 1), this.newMutableForFilter(3, 2, 1));
    }

    MutableBooleanCollection newBooleanForTransform(boolean... elements);

    MutableByteCollection newByteForTransform(byte... elements);

    MutableCharCollection newCharForTransform(char... elements);

    MutableDoubleCollection newDoubleForTransform(double... elements);

    MutableFloatCollection newFloatForTransform(float... elements);

    MutableIntCollection newIntForTransform(int... elements);

    MutableLongCollection newLongForTransform(long... elements);

    MutableShortCollection newShortForTransform(short... elements);

    default BooleanIterable getExpectedBoolean(boolean... elements)
    {
        return this.newBooleanForTransform(elements);
    }

    default ByteIterable getExpectedByte(byte... elements)
    {
        return this.newByteForTransform(elements);
    }

    default CharIterable getExpectedChar(char... elements)
    {
        return this.newCharForTransform(elements);
    }

    default DoubleIterable getExpectedDouble(double... elements)
    {
        return this.newDoubleForTransform(elements);
    }

    default FloatIterable getExpectedFloat(float... elements)
    {
        return this.newFloatForTransform(elements);
    }

    default IntIterable getExpectedInt(int... elements)
    {
        return this.newIntForTransform(elements);
    }

    default LongIterable getExpectedLong(long... elements)
    {
        return this.newLongForTransform(elements);
    }

    default ShortIterable getExpectedShort(short... elements)
    {
        return this.newShortForTransform(elements);
    }

    @Test
    default void InternalIterable_forEach()
    {
        {
            RichIterable<Integer> iterable = this.newWith(3, 2, 1);
            MutableCollection<Integer> result = this.newMutableForFilter();
            iterable.forEach(Procedures.cast(i -> result.add(i + 10)));
            assertIterablesEqual(this.newMutableForFilter(13, 12, 11), result);
        }

        {
            RichIterable<Integer> iterable = this.newWith(2, 1);
            MutableCollection<Integer> result = this.newMutableForFilter();
            iterable.forEach(Procedures.cast(i -> result.add(i + 10)));
            assertIterablesEqual(this.newMutableForFilter(12, 11), result);
        }

        RichIterable<Integer> iterable = this.newWith(1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.forEach(Procedures.cast(i -> result.add(i + 10)));
        assertIterablesEqual(this.newMutableForFilter(11), result);

        this.newWith().forEach(Procedures.cast(each -> fail()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        {
            RichIterable<Integer> iterable2 = this.newWith(3, 3, 3, 2, 2, 1);
            MutableCollection<Integer> result2 = this.newMutableForFilter();
            iterable2.forEach(Procedures.cast(i -> result2.add(i + 10)));
            assertIterablesEqual(this.newMutableForFilter(13, 13, 13, 12, 12, 11), result2);
        }

        {
            RichIterable<Integer> iterable3 = this.newWith(2, 2, 1);
            MutableCollection<Integer> result3 = this.newMutableForFilter();
            iterable3.forEach(Procedures.cast(i -> result3.add(i + 10)));
            assertIterablesEqual(this.newMutableForFilter(12, 12, 11), result3);
        }
    }

    @Test
    default void RichIterable_tap()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.tap(result::add).forEach(Procedures.noop());
        assertIterablesEqual(this.newMutableForFilter(3, 2, 1), result);
        this.newWith().tap(Procedures.cast(each -> fail()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(3, 3, 3, 2, 2, 1);
        MutableCollection<Integer> result2 = this.newMutableForFilter();
        iterable2.tap(result2::add).forEach(Procedures.noop());
        assertIterablesEqual(this.newMutableForFilter(3, 3, 3, 2, 2, 1), result2);
    }

    @Test
    default void InternalIterable_forEachWith()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 10);
        assertIterablesEqual(this.newMutableForFilter(13, 12, 11), result);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(3, 3, 3, 2, 2, 1);
        MutableCollection<Integer> result2 = this.newMutableForFilter();
        iterable2.forEachWith((argument1, argument2) -> result2.add(argument1 + argument2), 10);
        assertIterablesEqual(this.newMutableForFilter(13, 13, 13, 12, 12, 11), result2);
    }

    @Test
    default void RichIterable_size_empty()
    {
        assertEquals(0, this.newWith().size());
    }

    @Test
    default void RichIterable_size()
    {
        assertEquals(3, this.newWith(3, 2, 1).size());

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertEquals(6, this.newWith(3, 3, 3, 2, 2, 1).size());
    }

    @Test
    default void RichIterable_isEmpty()
    {
        assertFalse(this.newWith(3, 2, 1).isEmpty());
        assertTrue(this.newWith().isEmpty());
    }

    @Test
    default void RichIterable_notEmpty()
    {
        assertTrue(this.newWith(3, 2, 1).notEmpty());
        assertFalse(this.newWith().notEmpty());
    }

    @Test
    default void RichIterable_getFirst_empty_null()
    {
        assertNull(this.newWith().getFirst());
    }

    @Test
    default void RichIterable_getLast_empty_null()
    {
        assertNull(this.newWith().getLast());
    }

    @Test
    default void RichIterable_getFirst()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        Integer first = iterable.getFirst();
        assertThat(first, isOneOf(3, 2, 1));
        assertEquals(iterable.iterator().next(), first);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableWithDuplicates = this.newWith(3, 3, 3, 2, 2, 1);
        Integer firstWithDuplicates = iterableWithDuplicates.getFirst();
        assertThat(firstWithDuplicates, isOneOf(3, 2, 1));
        assertEquals(iterableWithDuplicates.iterator().next(), firstWithDuplicates);
    }

    @Test
    default void RichIterable_getLast()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        Integer last = iterable.getLast();
        assertThat(last, isOneOf(3, 2, 1));
        Iterator<Integer> iterator = iterable.iterator();
        Integer iteratorLast = null;
        while (iterator.hasNext())
        {
            iteratorLast = iterator.next();
        }
        assertEquals(iteratorLast, last);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableWithDuplicates = this.newWith(3, 3, 3, 2, 2, 1);
        Integer lastWithDuplicates = iterableWithDuplicates.getLast();
        assertThat(lastWithDuplicates, isOneOf(3, 2, 1));
        Iterator<Integer> iteratorWithDuplicates = iterableWithDuplicates.iterator();
        Integer iteratorLastWithDuplicates = null;
        while (iteratorWithDuplicates.hasNext())
        {
            iteratorLastWithDuplicates = iteratorWithDuplicates.next();
        }
        assertEquals(iteratorLastWithDuplicates, lastWithDuplicates);
    }

    @Test
    default void RichIterable_getOnly()
    {
        RichIterable<Integer> iterable = this.newWith(3);
        Integer only = iterable.getOnly();
        assertThat(only, is(3));

        Iterator<Integer> iterator = iterable.iterator();
        assertThat(iterator.next(), is(only));
        assertThat(iterator.hasNext(), is(false));

        assertThrows(IllegalStateException.class, () -> this.newWith().getOnly());
        assertThrows(IllegalStateException.class, () -> this.newWith(1, 2).getOnly());

        if (this.allowsDuplicates())
        {
            assertThrows(IllegalStateException.class, () -> this.newWith(1, 1).getOnly());
        }
    }

    @Test
    default void RichIterable_getFirst_and_getLast()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        assertIterablesNotEqual(iterable.getFirst(), iterable.getLast());
    }

    @Test
    default void RichIterable_contains()
    {
        RichIterable<Integer> iterable3 = this.newWith(3, 2, 1);
        assertTrue(iterable3.contains(3));
        assertTrue(iterable3.contains(2));
        assertTrue(iterable3.contains(1));
        assertFalse(iterable3.contains(0));

        RichIterable<Integer> iterable2 = this.newWith(2, 1);
        assertTrue(iterable2.contains(2));
        assertTrue(iterable2.contains(1));
        assertFalse(iterable2.contains(0));

        RichIterable<Integer> iterable1 = this.newWith(1);
        assertTrue(iterable1.contains(1));
        assertFalse(iterable1.contains(0));

        RichIterable<Integer> iterable0 = this.newWith();
        assertFalse(iterable0.contains(0));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(3, 3, 3, 2, 2, 1);
        assertTrue(iterableDup.contains(3));
        assertTrue(iterableDup.contains(2));
        assertTrue(iterableDup.contains(1));
        assertFalse(iterableDup.contains(0));
    }

    @Test
    default void RichIterable_containsAllIterable()
    {
        RichIterable<Integer> iterable3 = this.newWith(3, 2, 1);

        assertTrue(iterable3.containsAllIterable(Lists.immutable.of(3)));
        assertTrue(iterable3.containsAllIterable(Lists.immutable.of(3, 2, 1)));
        assertTrue(iterable3.containsAllIterable(Lists.immutable.of(3, 3, 3)));
        assertTrue(iterable3.containsAllIterable(Lists.immutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        assertFalse(iterable3.containsAllIterable(Lists.immutable.of(4)));
        assertFalse(iterable3.containsAllIterable(Lists.immutable.of(4, 4, 5)));
        assertFalse(iterable3.containsAllIterable(Lists.immutable.of(3, 2, 1, 0)));
        assertTrue(iterable3.containsAllIterable(Lists.immutable.empty()));

        RichIterable<Integer> iterable2 = this.newWith(2, 1);

        assertTrue(iterable2.containsAllIterable(Lists.immutable.of(2)));
        assertTrue(iterable2.containsAllIterable(Lists.immutable.of(2, 1)));
        assertTrue(iterable2.containsAllIterable(Lists.immutable.of(2, 2, 2)));
        assertTrue(iterable2.containsAllIterable(Lists.immutable.of(2, 2, 2, 1, 1)));
        assertFalse(iterable2.containsAllIterable(Lists.immutable.of(4)));
        assertFalse(iterable2.containsAllIterable(Lists.immutable.of(4, 4, 5)));
        assertFalse(iterable2.containsAllIterable(Lists.immutable.of(2, 1, 0)));
        assertTrue(iterable2.containsAllIterable(Lists.immutable.empty()));

        RichIterable<Integer> iterable1 = this.newWith(1);

        assertTrue(iterable1.containsAllIterable(Lists.immutable.of(1)));
        assertTrue(iterable1.containsAllIterable(Lists.immutable.of(1, 1, 1)));
        assertFalse(iterable1.containsAllIterable(Lists.immutable.of(4)));
        assertFalse(iterable1.containsAllIterable(Lists.immutable.of(4, 4, 5)));
        assertFalse(iterable1.containsAllIterable(Lists.immutable.of(2, 1, 0)));
        assertTrue(iterable1.containsAllIterable(Lists.immutable.empty()));

        RichIterable<Integer> iterable0 = this.newWith();

        assertFalse(iterable0.containsAllIterable(Lists.immutable.of(1)));
        assertFalse(iterable0.containsAllIterable(Lists.immutable.of(1, 1, 1)));
        assertFalse(iterable0.containsAllIterable(Lists.immutable.of(4, 4, 5)));
        assertTrue(iterable0.containsAllIterable(Lists.immutable.empty()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(3, 3, 3, 2, 2, 1);

        assertTrue(iterableDup.containsAllIterable(Lists.immutable.of(3)));
        assertTrue(iterableDup.containsAllIterable(Lists.immutable.of(3, 2, 1)));
        assertTrue(iterableDup.containsAllIterable(Lists.immutable.of(3, 3, 3)));
        assertTrue(iterableDup.containsAllIterable(Lists.immutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        assertFalse(iterableDup.containsAllIterable(Lists.immutable.of(4)));
        assertFalse(iterableDup.containsAllIterable(Lists.immutable.of(4, 4, 5)));
        assertFalse(iterableDup.containsAllIterable(Lists.immutable.of(3, 2, 1, 0)));
        assertTrue(iterableDup.containsAllIterable(Lists.immutable.empty()));
    }

    @Test
    default void RichIterable_containsAll()
    {
        RichIterable<Integer> iterable3 = this.newWith(3, 2, 1);

        assertTrue(iterable3.containsAll(Lists.mutable.of(3)));
        assertTrue(iterable3.containsAll(Lists.mutable.of(3, 2, 1)));
        assertTrue(iterable3.containsAll(Lists.mutable.of(3, 3, 3)));
        assertTrue(iterable3.containsAll(Lists.mutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        assertFalse(iterable3.containsAll(Lists.mutable.of(4)));
        assertFalse(iterable3.containsAll(Lists.mutable.of(4, 4, 5)));
        assertFalse(iterable3.containsAll(Lists.mutable.of(3, 2, 1, 0)));
        assertTrue(iterable3.containsAll(Lists.mutable.empty()));

        RichIterable<Integer> iterable2 = this.newWith(2, 1);

        assertTrue(iterable2.containsAll(Lists.mutable.of(2)));
        assertTrue(iterable2.containsAll(Lists.mutable.of(2, 1)));
        assertTrue(iterable2.containsAll(Lists.mutable.of(2, 2, 2)));
        assertTrue(iterable2.containsAll(Lists.mutable.of(2, 2, 2, 1, 1)));
        assertFalse(iterable2.containsAll(Lists.mutable.of(4)));
        assertFalse(iterable2.containsAll(Lists.mutable.of(4, 4, 5)));
        assertFalse(iterable2.containsAll(Lists.mutable.of(2, 1, 0)));
        assertTrue(iterable2.containsAll(Lists.mutable.empty()));

        RichIterable<Integer> iterable1 = this.newWith(1);

        assertTrue(iterable1.containsAll(Lists.mutable.of(1)));
        assertTrue(iterable1.containsAll(Lists.mutable.of(1, 1, 1)));
        assertFalse(iterable1.containsAll(Lists.mutable.of(4)));
        assertFalse(iterable1.containsAll(Lists.mutable.of(4, 4, 5)));
        assertFalse(iterable1.containsAll(Lists.mutable.of(2, 1, 0)));
        assertTrue(iterable1.containsAll(Lists.mutable.empty()));

        RichIterable<Integer> iterable0 = this.newWith();

        assertFalse(iterable0.containsAll(Lists.mutable.of(1)));
        assertFalse(iterable0.containsAll(Lists.mutable.of(1, 1, 1)));
        assertFalse(iterable0.containsAll(Lists.mutable.of(4, 4, 5)));
        assertTrue(iterable0.containsAll(Lists.mutable.empty()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(3, 3, 3, 2, 2, 1);

        assertTrue(iterableDup.containsAll(Lists.mutable.of(3)));
        assertTrue(iterableDup.containsAll(Lists.mutable.of(3, 2, 1)));
        assertTrue(iterableDup.containsAll(Lists.mutable.of(3, 3, 3)));
        assertTrue(iterableDup.containsAll(Lists.mutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        assertFalse(iterableDup.containsAll(Lists.mutable.of(4)));
        assertFalse(iterableDup.containsAll(Lists.mutable.of(4, 4, 5)));
        assertFalse(iterableDup.containsAll(Lists.mutable.of(3, 2, 1, 0)));
        assertTrue(iterableDup.containsAll(Lists.mutable.empty()));
    }

    @Test
    default void RichIterable_containsAllArguments()
    {
        RichIterable<Integer> iterable3 = this.newWith(3, 2, 1);

        assertTrue(iterable3.containsAllArguments(3));
        assertTrue(iterable3.containsAllArguments(3, 2, 1));
        assertTrue(iterable3.containsAllArguments(3, 3, 3));
        assertTrue(iterable3.containsAllArguments(3, 3, 3, 3, 2, 2, 2, 1, 1));
        assertFalse(iterable3.containsAllArguments(4));
        assertFalse(iterable3.containsAllArguments(4, 4, 5));
        assertFalse(iterable3.containsAllArguments(3, 2, 1, 0));
        assertTrue(iterable3.containsAllArguments());

        RichIterable<Integer> iterable2 = this.newWith(2, 1);

        assertTrue(iterable2.containsAllArguments(2));
        assertTrue(iterable2.containsAllArguments(2, 1));
        assertTrue(iterable2.containsAllArguments(2, 2, 2));
        assertTrue(iterable2.containsAllArguments(2, 2, 2, 1, 1));
        assertFalse(iterable2.containsAllArguments(4));
        assertFalse(iterable2.containsAllArguments(4, 4, 5));
        assertFalse(iterable2.containsAllArguments(2, 1, 0));
        assertTrue(iterable2.containsAllArguments());

        RichIterable<Integer> iterable1 = this.newWith(1);

        assertTrue(iterable1.containsAllArguments(1));
        assertTrue(iterable1.containsAllArguments(1, 1, 1));
        assertFalse(iterable1.containsAllArguments(4));
        assertFalse(iterable1.containsAllArguments(4, 4, 5));
        assertFalse(iterable1.containsAllArguments(2, 1, 0));
        assertTrue(iterable1.containsAllArguments());

        RichIterable<Integer> iterable0 = this.newWith();

        assertFalse(iterable0.containsAllArguments(1));
        assertFalse(iterable0.containsAllArguments(1, 1, 1));
        assertFalse(iterable0.containsAllArguments(4, 4, 5));
        assertTrue(iterable0.containsAllArguments());

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(3, 3, 3, 2, 2, 1);

        assertTrue(iterableDup.containsAllArguments(3));
        assertTrue(iterableDup.containsAllArguments(3, 2, 1));
        assertTrue(iterableDup.containsAllArguments(3, 3, 3));
        assertTrue(iterableDup.containsAllArguments(3, 3, 3, 3, 2, 2, 2, 1, 1));
        assertFalse(iterableDup.containsAllArguments(4));
        assertFalse(iterableDup.containsAllArguments(4, 4, 5));
        assertFalse(iterableDup.containsAllArguments(3, 2, 1, 0));
        assertTrue(iterableDup.containsAllArguments());
    }

    @Test
    default void RichIterable_containsAny()
    {
        RichIterable<Integer> iterable3 = this.newWith(3, 2, 1);

        assertTrue(iterable3.containsAny(Lists.mutable.of(3)));
        assertTrue(iterable3.containsAny(Lists.mutable.of(3, 2, 1)));
        assertTrue(iterable3.containsAny(Lists.mutable.of(3, 3, 3)));
        assertTrue(iterable3.containsAny(Lists.mutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        assertFalse(iterable3.containsAny(Lists.mutable.of(4)));
        assertFalse(iterable3.containsAny(Lists.mutable.of(4, 4, 5)));
        assertTrue(iterable3.containsAny(Lists.mutable.of(3, 2, 1, 0)));
        assertFalse(iterable3.containsAny(Lists.mutable.empty()));

        RichIterable<Integer> iterable2 = this.newWith(2, 1);

        assertTrue(iterable2.containsAny(Lists.mutable.of(2)));
        assertTrue(iterable2.containsAny(Lists.mutable.of(2, 1)));
        assertTrue(iterable2.containsAny(Lists.mutable.of(2, 2, 2)));
        assertTrue(iterable2.containsAny(Lists.mutable.of(2, 2, 2, 1, 1)));
        assertFalse(iterable2.containsAny(Lists.mutable.of(4)));
        assertFalse(iterable2.containsAny(Lists.mutable.of(4, 4, 5)));
        assertTrue(iterable2.containsAny(Lists.mutable.of(2, 1, 0)));
        assertFalse(iterable2.containsAny(Lists.mutable.empty()));

        RichIterable<Integer> iterable1 = this.newWith(1);

        assertTrue(iterable1.containsAny(Lists.mutable.of(1)));
        assertTrue(iterable1.containsAny(Lists.mutable.of(1, 1, 1)));
        assertFalse(iterable1.containsAny(Lists.mutable.of(4)));
        assertFalse(iterable1.containsAny(Lists.mutable.of(4, 4, 5)));
        assertTrue(iterable1.containsAny(Lists.mutable.of(2, 1, 0)));
        assertFalse(iterable1.containsAny(Lists.mutable.empty()));

        RichIterable<Integer> iterable0 = this.newWith();

        assertFalse(iterable0.containsAll(Lists.mutable.of(1)));
        assertFalse(iterable0.containsAll(Lists.mutable.of(1, 1, 1)));
        assertFalse(iterable0.containsAll(Lists.mutable.of(4, 4, 5)));
        assertTrue(iterable0.containsAll(Lists.mutable.empty()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(3, 3, 3, 2, 2, 1);

        assertTrue(iterableDup.containsAny(Lists.mutable.of(3)));
        assertTrue(iterableDup.containsAny(Lists.mutable.of(3, 2, 1)));
        assertTrue(iterableDup.containsAny(Lists.mutable.of(3, 3, 3)));
        assertTrue(iterableDup.containsAny(Lists.mutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        assertFalse(iterableDup.containsAny(Lists.mutable.of(4)));
        assertFalse(iterableDup.containsAny(Lists.mutable.of(4, 4, 5)));
        assertTrue(iterableDup.containsAny(Lists.mutable.of(3, 2, 1, 0)));
        assertFalse(iterableDup.containsAny(Lists.mutable.empty()));
    }

    @Test
    default void RichIterable_containsNone()
    {
        RichIterable<Integer> iterable3 = this.newWith(3, 2, 1);

        assertFalse(iterable3.containsNone(Lists.mutable.of(3)));
        assertFalse(iterable3.containsNone(Lists.mutable.of(3, 2, 1)));
        assertFalse(iterable3.containsNone(Lists.mutable.of(3, 3, 3)));
        assertFalse(iterable3.containsNone(Lists.mutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        assertTrue(iterable3.containsNone(Lists.mutable.of(4)));
        assertTrue(iterable3.containsNone(Lists.mutable.of(4, 4, 5)));
        assertFalse(iterable3.containsNone(Lists.mutable.of(3, 2, 1, 0)));
        assertTrue(iterable3.containsNone(Lists.mutable.empty()));

        RichIterable<Integer> iterable2 = this.newWith(2, 1);

        assertFalse(iterable2.containsNone(Lists.mutable.of(2)));
        assertFalse(iterable2.containsNone(Lists.mutable.of(2, 1)));
        assertFalse(iterable2.containsNone(Lists.mutable.of(2, 2, 2)));
        assertFalse(iterable2.containsNone(Lists.mutable.of(2, 2, 2, 1, 1)));
        assertTrue(iterable2.containsNone(Lists.mutable.of(4)));
        assertTrue(iterable2.containsNone(Lists.mutable.of(4, 4, 5)));
        assertFalse(iterable2.containsNone(Lists.mutable.of(2, 1, 0)));
        assertTrue(iterable2.containsNone(Lists.mutable.empty()));

        RichIterable<Integer> iterable1 = this.newWith(1);

        assertFalse(iterable1.containsNone(Lists.mutable.of(1)));
        assertFalse(iterable1.containsNone(Lists.mutable.of(1, 1, 1)));
        assertTrue(iterable1.containsNone(Lists.mutable.of(4)));
        assertTrue(iterable1.containsNone(Lists.mutable.of(4, 4, 5)));
        assertFalse(iterable1.containsNone(Lists.mutable.of(2, 1, 0)));
        assertTrue(iterable1.containsNone(Lists.mutable.empty()));

        RichIterable<Integer> iterable0 = this.newWith();

        assertTrue(iterable0.containsNone(Lists.mutable.of(1)));
        assertTrue(iterable0.containsNone(Lists.mutable.of(1, 1, 1)));
        assertTrue(iterable0.containsNone(Lists.mutable.of(4, 4, 5)));
        assertTrue(iterable0.containsNone(Lists.mutable.empty()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(3, 3, 3, 2, 2, 1);

        assertFalse(iterableDup.containsNone(Lists.mutable.of(3)));
        assertFalse(iterableDup.containsNone(Lists.mutable.of(3, 2, 1)));
        assertFalse(iterableDup.containsNone(Lists.mutable.of(3, 3, 3)));
        assertFalse(iterableDup.containsNone(Lists.mutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        assertTrue(iterableDup.containsNone(Lists.mutable.of(4)));
        assertTrue(iterableDup.containsNone(Lists.mutable.of(4, 4, 5)));
        assertFalse(iterableDup.containsNone(Lists.mutable.of(3, 2, 1, 0)));
        assertTrue(iterableDup.containsNone(Lists.mutable.empty()));
    }

    @Test
    default void RichIterable_iterator_iterationOrder()
    {
        MutableCollection<Integer> iterationOrder = this.newMutableForFilter();
        Iterator<Integer> iterator = this.getInstanceUnderTest().iterator();
        while (iterator.hasNext())
        {
            iterationOrder.add(iterator.next());
        }
        assertIterablesEqual(this.expectedIterationOrder(), iterationOrder);

        MutableCollection<Integer> expectedIterationOrder = this.expectedIterationOrder();
        MutableCollection<Integer> forEachWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().forEachWith((each, param) -> forEachWithIterationOrder.add(each), null);
        assertIterablesEqual(expectedIterationOrder, forEachWithIterationOrder);

        MutableCollection<Integer> forEachWithIndexIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().forEachWithIndex((each, index) -> forEachWithIndexIterationOrder.add(each));
        assertIterablesEqual(expectedIterationOrder, forEachWithIndexIterationOrder);
    }

    @Test
    default void RichIterable_iterationOrder()
    {
        MutableCollection<Integer> expectedIterationOrder = this.expectedIterationOrder();

        Procedure<Object> noop = Procedures.noop();

        MutableCollection<Integer> selectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().select(selectIterationOrder::add).forEach(noop);
        assertIterablesEqual(expectedIterationOrder, selectIterationOrder);

        MutableCollection<Integer> selectTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().select(selectTargetIterationOrder::add, new HashBag<>());
        assertIterablesEqual(expectedIterationOrder, selectTargetIterationOrder);

        MutableCollection<Integer> selectWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().selectWith((each, param) -> selectWithIterationOrder.add(each), null).forEach(noop);
        assertIterablesEqual(expectedIterationOrder, selectWithIterationOrder);

        MutableCollection<Integer> selectWithTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().selectWith((each, param) -> selectWithTargetIterationOrder.add(each), null, new HashBag<>());
        assertIterablesEqual(expectedIterationOrder, selectWithTargetIterationOrder);

        MutableCollection<Integer> rejectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().reject(rejectIterationOrder::add).forEach(noop);
        assertIterablesEqual(expectedIterationOrder, rejectIterationOrder);

        MutableCollection<Integer> rejectTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().reject(rejectTargetIterationOrder::add, new HashBag<>());
        assertIterablesEqual(expectedIterationOrder, rejectTargetIterationOrder);

        MutableCollection<Integer> rejectWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().rejectWith((each, param) -> rejectWithIterationOrder.add(each), null).forEach(noop);
        assertIterablesEqual(expectedIterationOrder, rejectWithIterationOrder);

        MutableCollection<Integer> rejectWithTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().rejectWith((each, param) -> rejectWithTargetIterationOrder.add(each), null, new HashBag<>());
        assertIterablesEqual(expectedIterationOrder, rejectWithTargetIterationOrder);

        MutableCollection<Integer> partitionIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().partition(partitionIterationOrder::add);
        assertIterablesEqual(expectedIterationOrder, partitionIterationOrder);

        MutableCollection<Integer> partitionWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().partitionWith((each, param) -> partitionWithIterationOrder.add(each), null);
        assertIterablesEqual(expectedIterationOrder, partitionWithIterationOrder);

        MutableCollection<Integer> collectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collect(collectIterationOrder::add).forEach(noop);
        assertIterablesEqual(expectedIterationOrder, collectIterationOrder);

        MutableCollection<Integer> collectTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collect(collectTargetIterationOrder::add, new HashBag<>());
        assertIterablesEqual(expectedIterationOrder, collectTargetIterationOrder);

        MutableCollection<Integer> collectWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectWith((each, param) -> collectWithIterationOrder.add(each), null).forEach(noop);
        assertIterablesEqual(expectedIterationOrder, collectWithIterationOrder);

        MutableCollection<Integer> collectWithTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectWith((each, param) -> collectWithTargetIterationOrder.add(each), null, new HashBag<>());
        assertIterablesEqual(expectedIterationOrder, collectWithTargetIterationOrder);

        MutableCollection<Integer> collectIfPredicateIterationOrder = this.newMutableForFilter();
        MutableCollection<Integer> collectIfFunctionIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectIf(collectIfPredicateIterationOrder::add, collectIfFunctionIterationOrder::add).forEach(noop);
        assertIterablesEqual(expectedIterationOrder, collectIfPredicateIterationOrder);
        assertIterablesEqual(expectedIterationOrder, collectIfFunctionIterationOrder);

        MutableCollection<Integer> collectIfPredicateTargetIterationOrder = this.newMutableForFilter();
        MutableCollection<Integer> collectIfFunctionTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectIf(collectIfPredicateTargetIterationOrder::add, collectIfFunctionTargetIterationOrder::add, new HashBag<>());
        assertIterablesEqual(expectedIterationOrder, collectIfPredicateTargetIterationOrder);
        assertIterablesEqual(expectedIterationOrder, collectIfFunctionTargetIterationOrder);

        MutableCollection<Integer> collectBooleanIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectBoolean(collectBooleanIterationOrder::add).forEach(each -> {
        });
        assertIterablesEqual(expectedIterationOrder, collectBooleanIterationOrder);

        MutableCollection<Integer> collectBooleanTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectBoolean(collectBooleanTargetIterationOrder::add, new BooleanHashBag());
        assertIterablesEqual(expectedIterationOrder, collectBooleanTargetIterationOrder);

        MutableCollection<Integer> collectByteIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectByte((Integer each) ->
        {
            collectByteIterationOrder.add(each);
            return (byte) 0;
        }).forEach(each -> {
        });
        assertIterablesEqual(expectedIterationOrder, collectByteIterationOrder);

        MutableCollection<Integer> collectByteTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectByte((Integer each) ->
        {
            collectByteTargetIterationOrder.add(each);
            return (byte) 0;
        }, new ByteHashBag());
        assertIterablesEqual(expectedIterationOrder, collectByteTargetIterationOrder);

        MutableCollection<Integer> collectCharIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectChar((Integer each) ->
        {
            collectCharIterationOrder.add(each);
            return ' ';
        }).forEach(each -> {
        });
        assertIterablesEqual(expectedIterationOrder, collectCharIterationOrder);

        MutableCollection<Integer> collectCharTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectChar((Integer each) ->
        {
            collectCharTargetIterationOrder.add(each);
            return ' ';
        }, new CharHashBag());
        assertIterablesEqual(expectedIterationOrder, collectCharTargetIterationOrder);

        MutableCollection<Integer> collectDoubleIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectDouble((Integer each) ->
        {
            collectDoubleIterationOrder.add(each);
            return 0.0;
        }).forEach(each -> {
        });
        assertIterablesEqual(expectedIterationOrder, collectDoubleIterationOrder);

        MutableCollection<Integer> collectDoubleTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectDouble((Integer each) ->
        {
            collectDoubleTargetIterationOrder.add(each);
            return 0.0;
        }, new DoubleHashBag());
        assertIterablesEqual(expectedIterationOrder, collectDoubleTargetIterationOrder);

        MutableCollection<Integer> collectFloatIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectFloat((Integer each) ->
        {
            collectFloatIterationOrder.add(each);
            return 0.0f;
        }).forEach(each -> {
        });
        assertIterablesEqual(expectedIterationOrder, collectFloatIterationOrder);

        MutableCollection<Integer> collectFloatTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectFloat((Integer each) ->
        {
            collectFloatTargetIterationOrder.add(each);
            return 0.0f;
        }, new FloatHashBag());
        assertIterablesEqual(expectedIterationOrder, collectFloatTargetIterationOrder);

        MutableCollection<Integer> collectIntIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectInt((Integer each) ->
        {
            collectIntIterationOrder.add(each);
            return 0;
        }).forEach(each -> {
        });
        assertIterablesEqual(expectedIterationOrder, collectIntIterationOrder);

        MutableCollection<Integer> collectIntTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectInt((Integer each) ->
        {
            collectIntTargetIterationOrder.add(each);
            return 0;
        }, new IntHashBag());
        assertIterablesEqual(expectedIterationOrder, collectIntTargetIterationOrder);

        MutableCollection<Integer> collectLongIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectLong((Integer each) ->
        {
            collectLongIterationOrder.add(each);
            return 0L;
        }).forEach(each -> {
        });
        assertIterablesEqual(expectedIterationOrder, collectLongIterationOrder);

        MutableCollection<Integer> collectLongTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectLong((Integer each) ->
        {
            collectLongTargetIterationOrder.add(each);
            return 0L;
        }, new LongHashBag());
        assertIterablesEqual(expectedIterationOrder, collectLongTargetIterationOrder);

        MutableCollection<Integer> collectShortIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectShort((ShortFunction<Integer>) each -> {
            collectShortIterationOrder.add(each);
            return (short) 0;
        }).forEach(each -> {
        });
        assertIterablesEqual(expectedIterationOrder, collectShortIterationOrder);

        MutableCollection<Integer> collectShortTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectShort((ShortFunction<Integer>) each -> {
            collectShortTargetIterationOrder.add(each);
            return (short) 0;
        }, new ShortHashBag());
        assertIterablesEqual(expectedIterationOrder, collectShortTargetIterationOrder);

        MutableCollection<Integer> flatCollectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().flatCollect(each -> Lists.immutable.with(flatCollectIterationOrder.add(each))).forEach(noop);
        assertIterablesEqual(expectedIterationOrder, flatCollectIterationOrder);

        MutableCollection<Integer> flatCollectTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().flatCollect(each -> Lists.immutable.with(flatCollectTargetIterationOrder.add(each)), new HashBag<>());
        assertIterablesEqual(expectedIterationOrder, flatCollectTargetIterationOrder);

        MutableCollection<Integer> countIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().count(countIterationOrder::add);
        assertIterablesEqual(expectedIterationOrder, countIterationOrder);

        MutableCollection<Integer> countWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().countWith((each, param) -> countWithIterationOrder.add(each), null);
        assertIterablesEqual(expectedIterationOrder, countWithIterationOrder);

        MutableCollection<Integer> anySatisfyIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().anySatisfy(each -> {
            anySatisfyIterationOrder.add(each);
            return false;
        });
        assertIterablesEqual(expectedIterationOrder, anySatisfyIterationOrder);

        MutableCollection<Integer> anySatisfyWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().anySatisfyWith((each, param) -> {
            anySatisfyWithIterationOrder.add(each);
            return false;
        }, null);
        assertIterablesEqual(expectedIterationOrder, anySatisfyWithIterationOrder);

        MutableCollection<Integer> allSatisfyIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().allSatisfy(each -> {
            allSatisfyIterationOrder.add(each);
            return true;
        });
        assertIterablesEqual(expectedIterationOrder, allSatisfyIterationOrder);

        MutableCollection<Integer> allSatisfyWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().allSatisfyWith((each, param) -> {
            allSatisfyWithIterationOrder.add(each);
            return true;
        }, null);
        assertIterablesEqual(expectedIterationOrder, allSatisfyWithIterationOrder);

        MutableCollection<Integer> noneSatisfyIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().noneSatisfy(each -> {
            noneSatisfyIterationOrder.add(each);
            return false;
        });
        assertIterablesEqual(expectedIterationOrder, noneSatisfyIterationOrder);

        MutableCollection<Integer> noneSatisfyWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().noneSatisfyWith((each, param) -> {
            noneSatisfyWithIterationOrder.add(each);
            return false;
        }, null);
        assertIterablesEqual(expectedIterationOrder, noneSatisfyWithIterationOrder);

        MutableCollection<Integer> detectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().detect(each -> {
            detectIterationOrder.add(each);
            return false;
        });
        assertIterablesEqual(expectedIterationOrder, detectIterationOrder);

        MutableCollection<Integer> detectWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().detectWith((each, param) -> {
            detectWithIterationOrder.add(each);
            return false;
        }, null);
        assertIterablesEqual(expectedIterationOrder, detectWithIterationOrder);

        MutableCollection<Integer> detectIfNoneIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().detectIfNone(each -> {
            detectIfNoneIterationOrder.add(each);
            return false;
        }, () -> 0);
        assertIterablesEqual(expectedIterationOrder, detectIfNoneIterationOrder);

        MutableCollection<Integer> detectWithIfNoneIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().detectWithIfNone((each, param) -> {
            detectWithIfNoneIterationOrder.add(each);
            return false;
        }, null, () -> 0);
        assertIterablesEqual(expectedIterationOrder, detectWithIfNoneIterationOrder);

        MutableCollection<Integer> minComparatorIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().min((o1, o2) -> {
            if (minComparatorIterationOrder.isEmpty())
            {
                minComparatorIterationOrder.add(o2);
            }
            minComparatorIterationOrder.add(o1);
            return 0;
        });
        assertIterablesEqual(expectedIterationOrder, minComparatorIterationOrder);

        MutableCollection<Integer> maxComparatorIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().max((o1, o2) -> {
            if (maxComparatorIterationOrder.isEmpty())
            {
                maxComparatorIterationOrder.add(o2);
            }
            maxComparatorIterationOrder.add(o1);
            return 0;
        });
        assertIterablesEqual(expectedIterationOrder, maxComparatorIterationOrder);

        MutableCollection<Integer> minByIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().minBy(minByIterationOrder::add);
        assertIterablesEqual(expectedIterationOrder, minByIterationOrder);

        MutableCollection<Integer> maxByIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().maxBy(maxByIterationOrder::add);
        assertIterablesEqual(expectedIterationOrder, maxByIterationOrder);

        MutableCollection<Integer> groupByIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().groupBy(groupByIterationOrder::add);
        assertIterablesEqual(expectedIterationOrder, groupByIterationOrder);

        MutableCollection<Integer> groupByTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().groupBy(groupByTargetIterationOrder::add, new HashBagMultimap<>());
        assertIterablesEqual(expectedIterationOrder, groupByTargetIterationOrder);

        MutableCollection<Integer> groupByEachIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().groupByEach(each -> {
            groupByEachIterationOrder.add(each);
            return Lists.immutable.with(each);
        });
        assertIterablesEqual(expectedIterationOrder, groupByEachIterationOrder);

        MutableCollection<Integer> groupByEachTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().groupByEach(each -> {
            groupByEachTargetIterationOrder.add(each);
            return Lists.immutable.with(each);
        }, new HashBagMultimap<>());
        assertIterablesEqual(expectedIterationOrder, groupByEachTargetIterationOrder);

        MutableCollection<Integer> sumOfFloatIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().sumOfFloat(each -> {
            sumOfFloatIterationOrder.add(each);
            return each.floatValue();
        });
        assertIterablesEqual(expectedIterationOrder, sumOfFloatIterationOrder);

        MutableCollection<Integer> sumOfDoubleIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().sumOfDouble(each -> {
            sumOfDoubleIterationOrder.add(each);
            return each.doubleValue();
        });
        assertIterablesEqual(expectedIterationOrder, sumOfDoubleIterationOrder);

        MutableCollection<Integer> sumOfIntIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().sumOfInt(each -> {
            sumOfIntIterationOrder.add(each);
            return each.intValue();
        });
        assertIterablesEqual(expectedIterationOrder, sumOfIntIterationOrder);

        MutableCollection<Integer> sumOfLongIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().sumOfLong(each -> {
            sumOfLongIterationOrder.add(each);
            return each.longValue();
        });
        assertIterablesEqual(expectedIterationOrder, sumOfLongIterationOrder);

        /*
         * TODO: Fix sumByDouble and sumByFloat methods for bags, to only iterate once per item, not per occurrence.
        MutableCollection<Integer> sumByDoubleIterationOrder1 = this.newMutableForFilter();
        MutableCollection<Integer> sumByDoubleIterationOrder2 = this.newMutableForFilter();
        this.getInstanceUnderTest().sumByDouble(
                each -> {
                    sumByDoubleIterationOrder1.add(each);
                    return each;
                },
                each -> {
                    sumByDoubleIterationOrder2.add(each);
                    return 0.0;
                });
        assertEquals(expectedIterationOrder, sumByDoubleIterationOrder1);
        assertEquals(expectedIterationOrder, sumByDoubleIterationOrder2);

        MutableCollection<Integer> sumByFloatIterationOrder1 = this.newMutableForFilter();
        MutableCollection<Integer> sumByFloatIterationOrder2 = this.newMutableForFilter();
        this.getInstanceUnderTest().sumByFloat(
                each -> {
                    sumByFloatIterationOrder1.add(each);
                    return each;
                },
                each -> {
                    sumByFloatIterationOrder2.add(each);
                    return 0.0f;
                });
        assertEquals(expectedIterationOrder, sumByFloatIterationOrder1);
        assertEquals(expectedIterationOrder, sumByFloatIterationOrder2);
        */

        MutableCollection<Integer> sumByIntIterationOrder1 = this.newMutableForFilter();
        MutableCollection<Integer> sumByIntIterationOrder2 = this.newMutableForFilter();
        this.getInstanceUnderTest().sumByInt(
                each -> {
                    sumByIntIterationOrder1.add(each);
                    return each;
                },
                each -> {
                    sumByIntIterationOrder2.add(each);
                    return 0;
                });
        assertIterablesEqual(expectedIterationOrder, sumByIntIterationOrder1);
        assertIterablesEqual(expectedIterationOrder, sumByIntIterationOrder2);

        MutableCollection<Integer> sumByLongIterationOrder1 = this.newMutableForFilter();
        MutableCollection<Integer> sumByLongIterationOrder2 = this.newMutableForFilter();
        this.getInstanceUnderTest().sumByLong(
                each -> {
                    sumByLongIterationOrder1.add(each);
                    return each;
                },
                each -> {
                    sumByLongIterationOrder2.add(each);
                    return 0L;
                });
        assertIterablesEqual(expectedIterationOrder, sumByLongIterationOrder1);
        assertIterablesEqual(expectedIterationOrder, sumByLongIterationOrder2);

        MutableCollection<Integer> expectedInjectIntoIterationOrder = this.allowsDuplicates()
                ? this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1)
                : expectedIterationOrder;

        MutableCollection<Integer> injectIntoIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0, (Function2<Integer, Integer, Integer>) (argument1, argument2) -> {
            injectIntoIterationOrder.add(argument2);
            return argument1 + argument2;
        });
        assertIterablesEqual(expectedInjectIntoIterationOrder, injectIntoIterationOrder);

        MutableCollection<Integer> injectIntoIntIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0, (IntObjectToIntFunction<Integer>) (intParameter, objectParameter) -> {
            injectIntoIntIterationOrder.add(objectParameter);
            return intParameter + objectParameter;
        });
        assertIterablesEqual(expectedInjectIntoIterationOrder, injectIntoIntIterationOrder);

        MutableCollection<Integer> injectIntoLongIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0L, (LongObjectToLongFunction<Integer>) (longParameter, objectParameter) -> {
            injectIntoLongIterationOrder.add(objectParameter);
            return longParameter + objectParameter;
        });
        assertIterablesEqual(expectedInjectIntoIterationOrder, injectIntoLongIterationOrder);

        MutableCollection<Integer> injectIntoDoubleIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0L, (DoubleObjectToDoubleFunction<Integer>) (doubleParameter, objectParameter) -> {
            injectIntoDoubleIterationOrder.add(objectParameter);
            return doubleParameter + objectParameter;
        });
        assertIterablesEqual(expectedInjectIntoIterationOrder, injectIntoDoubleIterationOrder);

        MutableCollection<Integer> injectIntoFloatIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0L, (FloatObjectToFloatFunction<Integer>) (floatParameter, objectParameter) -> {
            injectIntoFloatIterationOrder.add(objectParameter);
            return floatParameter + objectParameter;
        });
        assertIterablesEqual(expectedInjectIntoIterationOrder, injectIntoFloatIterationOrder);

        Counter toSortedListCount = new Counter();
        this.getInstanceUnderTest().toSortedList((unused1, unused2) -> {
            toSortedListCount.increment();
            return 0;
        });
        assertEquals(expectedIterationOrder.size() - 1, toSortedListCount.getCount());

        Counter toSortedSetCount = new Counter();
        this.getInstanceUnderTest().toSortedSet((unused1, unused2) -> {
            toSortedSetCount.increment();
            return 0;
        });
        assertEquals(expectedIterationOrder.size(), toSortedSetCount.getCount());

        Counter toSortedBagCount = new Counter();
        this.getInstanceUnderTest().toSortedBag((unused1, unused2) -> {
            toSortedBagCount.increment();
            return 0;
        });
        assertEquals(expectedIterationOrder.size(), toSortedBagCount.getCount());

        MutableCollection<Integer> summarizeIntOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().summarizeInt(each -> {
            summarizeIntOrder.add(each);
            return 0;
        });
        assertIterablesEqual(expectedIterationOrder, summarizeIntOrder);

        MutableCollection<Integer> summarizeFloatOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().summarizeFloat(each -> {
            summarizeFloatOrder.add(each);
            return 0;
        });
        assertIterablesEqual(expectedIterationOrder, summarizeFloatOrder);

        MutableCollection<Integer> summarizeLongOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().summarizeLong(each -> {
            summarizeLongOrder.add(each);
            return 0;
        });
        assertIterablesEqual(expectedIterationOrder, summarizeLongOrder);

        MutableCollection<Integer> summarizeDoubleOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().summarizeDouble(each -> {
            summarizeDoubleOrder.add(each);
            return 0;
        });
        assertIterablesEqual(expectedIterationOrder, summarizeDoubleOrder);
    }

    default MutableCollection<Integer> expectedIterationOrder()
    {
        MutableCollection<Integer> forEach = this.newMutableForFilter();
        this.getInstanceUnderTest().each(forEach::add);
        return forEach;
    }

    default RichIterable<Integer> getInstanceUnderTest()
    {
        return this.allowsDuplicates()
                ? this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1)
                : this.newWith(4, 3, 2, 1);
    }

    @Test
    default void RichIterable_select_reject()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertIterablesEqual(
                this.getExpectedFiltered(4, 2),
                iterable.select(IntegerPredicates.isEven()));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.select(IntegerPredicates.isEven(), target);
            assertIterablesEqual(this.newMutableForFilter(4, 2), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFiltered(4, 3),
                iterable.selectWith(Predicates2.greaterThan(), 2));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.selectWith(Predicates2.greaterThan(), 2, target);
            assertIterablesEqual(this.newMutableForFilter(4, 3), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFiltered(4, 2),
                iterable.reject(IntegerPredicates.isOdd()));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.reject(IntegerPredicates.isOdd(), target);
            assertIterablesEqual(this.newMutableForFilter(4, 2), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFiltered(4, 3),
                iterable.rejectWith(Predicates2.lessThan(), 3));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.rejectWith(Predicates2.lessThan(), 3, target);
            assertIterablesEqual(this.newMutableForFilter(4, 3), result);
            assertSame(target, result);
        }

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableWithDuplicates = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        assertIterablesEqual(
                this.getExpectedFiltered(4, 4, 4, 4, 2, 2),
                iterableWithDuplicates.select(IntegerPredicates.isEven()));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterableWithDuplicates.select(IntegerPredicates.isEven(), target);
            assertIterablesEqual(this.newMutableForFilter(4, 4, 4, 4, 2, 2), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFiltered(4, 4, 4, 4, 3, 3, 3),
                iterableWithDuplicates.selectWith(Predicates2.greaterThan(), 2));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterableWithDuplicates.selectWith(Predicates2.greaterThan(), 2, target);
            assertIterablesEqual(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFiltered(4, 4, 4, 4, 2, 2),
                iterableWithDuplicates.reject(IntegerPredicates.isOdd()));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterableWithDuplicates.reject(IntegerPredicates.isOdd(), target);
            assertIterablesEqual(this.newMutableForFilter(4, 4, 4, 4, 2, 2), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFiltered(4, 4, 4, 4, 3, 3, 3),
                iterableWithDuplicates.rejectWith(Predicates2.lessThan(), 3));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterableWithDuplicates.rejectWith(Predicates2.lessThan(), 3, target);
            assertIterablesEqual(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3), result);
            assertSame(target, result);
        }
    }

    @Test
    default void RichIterable_partition()
    {
        RichIterable<Integer> iterable = this.newWith(-3, -2, -1, 0, 1, 2, 3);

        PartitionIterable<Integer> partition = iterable.partition(IntegerPredicates.isEven());
        assertIterablesEqual(this.getExpectedFiltered(-2, 0, 2), partition.getSelected());
        assertIterablesEqual(this.getExpectedFiltered(-3, -1, 1, 3), partition.getRejected());

        PartitionIterable<Integer> partitionWith = iterable.partitionWith(Predicates2.greaterThan(), 0);
        assertIterablesEqual(this.getExpectedFiltered(1, 2, 3), partitionWith.getSelected());
        assertIterablesEqual(this.getExpectedFiltered(-3, -2, -1, 0), partitionWith.getRejected());

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableWithDuplicates = this.newWith(-3, -3, -3, -2, -2, -1, 0, 1, 2, 2, 3, 3, 3);
        PartitionIterable<Integer> partitionWithDuplicates = iterableWithDuplicates.partition(IntegerPredicates.isEven());
        assertIterablesEqual(this.getExpectedFiltered(-2, -2, 0, 2, 2), partitionWithDuplicates.getSelected());
        assertIterablesEqual(this.getExpectedFiltered(-3, -3, -3, -1, 1, 3, 3, 3), partitionWithDuplicates.getRejected());

        PartitionIterable<Integer> partitionWithDuplicatesAndPredicate = iterableWithDuplicates.partitionWith(Predicates2.greaterThan(), 0);
        assertIterablesEqual(this.getExpectedFiltered(1, 2, 2, 3, 3, 3), partitionWithDuplicatesAndPredicate.getSelected());
        assertIterablesEqual(this.getExpectedFiltered(-3, -3, -3, -2, -2, -1, 0), partitionWithDuplicatesAndPredicate.getRejected());
    }

    @Test
    default void RichIterable_selectInstancesOf()
    {
        RichIterable<Number> iterable = this.newWith(1, 2.0, 3, 4.0);

        assertIterablesEqual(this.getExpectedFiltered(), iterable.selectInstancesOf(String.class));
        assertIterablesEqual(this.getExpectedFiltered(1, 3), iterable.selectInstancesOf(Integer.class));
        assertIterablesEqual(this.getExpectedFiltered(1, 2.0, 3, 4.0), iterable.selectInstancesOf(Number.class));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Number> iterable2 = this.newWith(1, 2.0, 2.0, 3, 3, 3, 4.0, 4.0, 4.0, 4.0);
        assertIterablesEqual(this.getExpectedFiltered(1, 3, 3, 3), iterable2.selectInstancesOf(Integer.class));
        assertIterablesEqual(this.getExpectedFiltered(1, 2.0, 2.0, 3, 3, 3, 4.0, 4.0, 4.0, 4.0), iterable2.selectInstancesOf(Number.class));
    }

    @Test
    default void RichIterable_collect()
    {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        assertIterablesEqual(
                this.getExpectedTransformed(3, 2, 1, 3, 2, 1),
                iterable.collect(i -> i % 10));

        {
            MutableCollection<Integer> target = this.newMutableForTransform();
            MutableCollection<Integer> result = iterable.collect(i -> i % 10, target);
            assertIterablesEqual(this.newMutableForTransform(3, 2, 1, 3, 2, 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedTransformed(3, 2, 1, 3, 2, 1),
                iterable.collectWith((i, mod) -> i % mod, 10));

        MutableCollection<Integer> target = this.newMutableForTransform();
        MutableCollection<Integer> result = iterable.collectWith((i, mod) -> i % mod, 10, target);
        assertIterablesEqual(this.newMutableForTransform(3, 2, 1, 3, 2, 1), result);
        assertSame(target, result);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableWithDuplicates = this.newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);

        assertIterablesEqual(
                this.getExpectedTransformed(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1),
                iterableWithDuplicates.collect(i -> i % 10));

        {
            MutableCollection<Integer> targetWithDuplicates = this.newMutableForTransform();
            MutableCollection<Integer> resultWithDuplicates = iterableWithDuplicates.collect(i -> i % 10, targetWithDuplicates);
            assertIterablesEqual(this.newMutableForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), resultWithDuplicates);
            assertSame(targetWithDuplicates, resultWithDuplicates);
        }

        assertIterablesEqual(
                this.getExpectedTransformed(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1),
                iterableWithDuplicates.collectWith((i, mod) -> i % mod, 10));

        MutableCollection<Integer> targetWithDuplicates = this.newMutableForTransform();
        MutableCollection<Integer> resultWithDuplicates = iterableWithDuplicates.collectWith((i, mod) -> i % mod, 10, targetWithDuplicates);
        assertIterablesEqual(this.newMutableForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), resultWithDuplicates);
        assertSame(targetWithDuplicates, resultWithDuplicates);
    }

    @Test
    default void RichIterable_collectIf()
    {
        assertIterablesEqual(
                this.getExpectedTransformed(3, 1, 3, 1),
                this.newWith(13, 12, 11, 3, 2, 1).collectIf(i -> i % 2 != 0, i -> i % 10));

        MutableCollection<Integer> target = this.newMutableForTransform();
        MutableCollection<Integer> result = this.newWith(13, 12, 11, 3, 2, 1).collectIf(i -> i % 2 != 0, i -> i % 10, target);
        assertIterablesEqual(this.newMutableForTransform(3, 1, 3, 1), result);
        assertSame(target, result);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable = this.newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);

        assertIterablesEqual(
                this.getExpectedTransformed(3, 3, 1, 1, 3, 3, 1, 1),
                iterable.collectIf(i -> i % 2 != 0, i -> i % 10));

        MutableCollection<Integer> targetWithDuplicates = this.newMutableForTransform();
        MutableCollection<Integer> resultWithDuplicates = iterable.collectIf(i -> i % 2 != 0, i -> i % 10, targetWithDuplicates);
        assertIterablesEqual(this.newMutableForTransform(3, 3, 1, 1, 3, 3, 1, 1), resultWithDuplicates);
        assertSame(targetWithDuplicates, resultWithDuplicates);
    }

    @Test
    default void RichIterable_collectPrimitive()
    {
        assertIterablesEqual(
                this.getExpectedBoolean(false, true, false),
                this.newWith(3, 2, 1).collectBoolean(each -> each % 2 == 0));

        {
            MutableBooleanCollection target = this.newBooleanForTransform();
            MutableBooleanCollection result = this.newWith(3, 2, 1).collectBoolean(each -> each % 2 == 0, target);
            assertIterablesEqual(this.newBooleanForTransform(false, true, false), result);
            assertSame(target, result);
        }

        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        assertIterablesEqual(
                this.getExpectedByte((byte) 3, (byte) 2, (byte) 1, (byte) 3, (byte) 2, (byte) 1),
                iterable.collectByte(each -> (byte) (each % 10)));

        {
            MutableByteCollection target = this.newByteForTransform();
            MutableByteCollection result = iterable.collectByte(each -> (byte) (each % 10), target);
            assertIterablesEqual(this.newByteForTransform((byte) 3, (byte) 2, (byte) 1, (byte) 3, (byte) 2, (byte) 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedChar((char) 3, (char) 2, (char) 1, (char) 3, (char) 2, (char) 1),
                iterable.collectChar(each -> (char) (each % 10)));

        {
            MutableCharCollection target = this.newCharForTransform();
            MutableCharCollection result = iterable.collectChar(each -> (char) (each % 10), target);
            assertIterablesEqual(this.newCharForTransform((char) 3, (char) 2, (char) 1, (char) 3, (char) 2, (char) 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedDouble(3.0, 2.0, 1.0, 3.0, 2.0, 1.0),
                iterable.collectDouble(each -> (double) (each % 10)));

        {
            MutableDoubleCollection target = this.newDoubleForTransform();
            MutableDoubleCollection result = iterable.collectDouble(each -> (double) (each % 10), target);
            assertIterablesEqual(this.newDoubleForTransform(3.0, 2.0, 1.0, 3.0, 2.0, 1.0), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFloat(3.0f, 2.0f, 1.0f, 3.0f, 2.0f, 1.0f),
                iterable.collectFloat(each -> (float) (each % 10)));

        {
            MutableFloatCollection target = this.newFloatForTransform();
            MutableFloatCollection result = iterable.collectFloat(each -> (float) (each % 10), target);
            assertIterablesEqual(this.newFloatForTransform(3.0f, 2.0f, 1.0f, 3.0f, 2.0f, 1.0f), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedInt(3, 2, 1, 3, 2, 1),
                iterable.collectInt(each -> each % 10));

        {
            MutableIntCollection target = this.newIntForTransform();
            MutableIntCollection result = iterable.collectInt(each -> each % 10, target);
            assertIterablesEqual(this.newIntForTransform(3, 2, 1, 3, 2, 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedLong(3, 2, 1, 3, 2, 1),
                iterable.collectLong(each -> each % 10));

        {
            MutableLongCollection target = this.newLongForTransform();
            MutableLongCollection result = iterable.collectLong(each -> each % 10, target);
            assertIterablesEqual(this.newLongForTransform(3, 2, 1, 3, 2, 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedShort((short) 3, (short) 2, (short) 1, (short) 3, (short) 2, (short) 1),
                iterable.collectShort(each -> (short) (each % 10)));

        MutableShortCollection target = this.newShortForTransform();
        MutableShortCollection result = iterable.collectShort(each -> (short) (each % 10), target);
        assertIterablesEqual(this.newShortForTransform((short) 3, (short) 2, (short) 1, (short) 3, (short) 2, (short) 1), result);
        assertSame(target, result);

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertIterablesEqual(
                this.getExpectedBoolean(false, false, true, true, false, false),
                this.newWith(3, 3, 2, 2, 1, 1).collectBoolean(each -> each % 2 == 0));

        {
            MutableBooleanCollection targetDup = this.newBooleanForTransform();
            MutableBooleanCollection resultDup = this.newWith(3, 3, 2, 2, 1, 1).collectBoolean(each -> each % 2 == 0, targetDup);
            assertIterablesEqual(this.newBooleanForTransform(false, false, true, true, false, false), resultDup);
            assertSame(targetDup, resultDup);
        }

        RichIterable<Integer> iterableDup = this.newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);

        assertIterablesEqual(
                this.getExpectedByte((byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 1, (byte) 1, (byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 1, (byte) 1),
                iterableDup.collectByte(each -> (byte) (each % 10)));

        {
            MutableByteCollection targetDup = this.newByteForTransform();
            MutableByteCollection resultDup = iterableDup.collectByte(each -> (byte) (each % 10), targetDup);
            assertIterablesEqual(this.newByteForTransform((byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 1, (byte) 1, (byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 1, (byte) 1), resultDup);
            assertSame(targetDup, resultDup);
        }

        assertIterablesEqual(
                this.getExpectedChar((char) 3, (char) 3, (char) 2, (char) 2, (char) 1, (char) 1, (char) 3, (char) 3, (char) 2, (char) 2, (char) 1, (char) 1),
                iterableDup.collectChar(each -> (char) (each % 10)));

        {
            MutableCharCollection targetDup = this.newCharForTransform();
            MutableCharCollection resultDup = iterableDup.collectChar(each -> (char) (each % 10), targetDup);
            assertIterablesEqual(this.newCharForTransform((char) 3, (char) 3, (char) 2, (char) 2, (char) 1, (char) 1, (char) 3, (char) 3, (char) 2, (char) 2, (char) 1, (char) 1), resultDup);
            assertSame(targetDup, resultDup);
        }

        assertIterablesEqual(
                this.getExpectedDouble(3.0, 3.0, 2.0, 2.0, 1.0, 1.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0),
                iterableDup.collectDouble(each -> (double) (each % 10)));

        {
            MutableDoubleCollection targetDup = this.newDoubleForTransform();
            MutableDoubleCollection resultDup = iterableDup.collectDouble(each -> (double) (each % 10), targetDup);
            assertIterablesEqual(this.newDoubleForTransform(3.0, 3.0, 2.0, 2.0, 1.0, 1.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0), resultDup);
            assertSame(targetDup, resultDup);
        }

        assertIterablesEqual(
                this.getExpectedFloat(3.0f, 3.0f, 2.0f, 2.0f, 1.0f, 1.0f, 3.0f, 3.0f, 2.0f, 2.0f, 1.0f, 1.0f),
                iterableDup.collectFloat(each -> (float) (each % 10)));

        {
            MutableFloatCollection targetDup = this.newFloatForTransform();
            MutableFloatCollection resultDup = iterableDup.collectFloat(each -> (float) (each % 10), targetDup);
            assertIterablesEqual(this.newFloatForTransform(3.0f, 3.0f, 2.0f, 2.0f, 1.0f, 1.0f, 3.0f, 3.0f, 2.0f, 2.0f, 1.0f, 1.0f), resultDup);
            assertSame(targetDup, resultDup);
        }

        assertIterablesEqual(
                this.getExpectedInt(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1),
                iterableDup.collectInt(each -> each % 10));

        {
            MutableIntCollection targetDup = this.newIntForTransform();
            MutableIntCollection resultDup = iterableDup.collectInt(each -> each % 10, targetDup);
            assertIterablesEqual(this.newIntForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), resultDup);
            assertSame(targetDup, resultDup);
        }

        assertIterablesEqual(
                this.getExpectedLong(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1),
                iterableDup.collectLong(each -> each % 10));

        {
            MutableLongCollection targetDup = this.newLongForTransform();
            MutableLongCollection resultDup = iterableDup.collectLong(each -> each % 10, targetDup);
            assertIterablesEqual(this.newLongForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), resultDup);
            assertSame(targetDup, resultDup);
        }

        assertIterablesEqual(
                this.getExpectedShort((short) 3, (short) 3, (short) 2, (short) 2, (short) 1, (short) 1, (short) 3, (short) 3, (short) 2, (short) 2, (short) 1, (short) 1),
                iterableDup.collectShort(each -> (short) (each % 10)));

        MutableShortCollection targetDup = this.newShortForTransform();
        MutableShortCollection resultDup = iterableDup.collectShort(each -> (short) (each % 10), targetDup);
        assertIterablesEqual(this.newShortForTransform((short) 3, (short) 3, (short) 2, (short) 2, (short) 1, (short) 1, (short) 3, (short) 3, (short) 2, (short) 2, (short) 1, (short) 1), resultDup);
        assertSame(targetDup, resultDup);
    }

    @Test
    default void RichIterable_flatCollect()
    {
        assertIterablesEqual(
                this.getExpectedTransformed(1, 2, 3, 1, 2, 1),
                this.newWith(3, 2, 1).flatCollect(Interval::oneTo));

        assertIterablesEqual(
                this.newMutableForTransform(1, 2, 3, 1, 2, 1),
                this.newWith(3, 2, 1).flatCollect(Interval::oneTo, this.newMutableForTransform()));

        assertIterablesEqual(
                this.getExpectedTransformed(3, 2, 1, 2, 1, 1),
                this.newWith(3, 2, 1).flatCollectWith(Interval::fromTo, 1));

        assertIterablesEqual(
                this.newMutableForTransform(3, 2, 1, 2, 1, 1),
                this.newWith(3, 2, 1).flatCollectWith(Interval::fromTo, 1, this.newMutableForTransform()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertIterablesEqual(
                this.getExpectedTransformed(1, 2, 3, 1, 2, 1, 2, 1),
                this.newWith(3, 2, 2, 1).flatCollect(Interval::oneTo));

        assertIterablesEqual(
                this.newMutableForTransform(1, 2, 3, 1, 2, 1, 2, 1),
                this.newWith(3, 2, 2, 1).flatCollect(Interval::oneTo, this.newMutableForTransform()));

        assertIterablesEqual(
                this.getExpectedTransformed(3, 4, 5, 2, 3, 4, 5, 2, 3, 4, 5, 1, 2, 3, 4, 5),
                this.newWith(3, 2, 2, 1).flatCollectWith(Interval::fromTo, 5));

        assertIterablesEqual(
                this.newMutableForTransform(3, 2, 1, 2, 1, 2, 1, 1),
                this.newWith(3, 2, 2, 1).flatCollectWith(Interval::fromTo, 1, this.newMutableForTransform()));
    }

    @Test
    default void RichIterable_flatCollect_primitive()
    {
        {
            MutableBooleanCollection target = this.newBooleanForTransform();
            MutableBooleanCollection result = this
                    .newWith(3, 2, 1)
                    .flatCollectBoolean(each -> BooleanLists.immutable.with(each % 2 == 0, each % 2 == 0), target);
            assertIterablesEqual(this.newBooleanForTransform(false, false, true, true, false, false), result);
            assertSame(target, result);
        }

        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        {
            MutableByteCollection target = this.newByteForTransform();
            MutableByteCollection result = iterable.flatCollectByte(
                    each -> ByteLists.immutable.with((byte) (each % 10), (byte) (each % 10)),
                    target);
            assertIterablesEqual(
                    this.newByteForTransform((byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 1, (byte) 1, (byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 1, (byte) 1),
                    result);
            assertSame(target, result);
        }

        {
            MutableCharCollection target = this.newCharForTransform();
            MutableCharCollection result = iterable.flatCollectChar(
                    each -> CharLists.immutable.with((char) (each % 10), (char) (each % 10)),
                    target);
            assertIterablesEqual(
                    this.newCharForTransform((char) 3, (char) 3, (char) 2, (char) 2, (char) 1, (char) 1, (char) 3, (char) 3, (char) 2, (char) 2, (char) 1, (char) 1),
                    result);
            assertSame(target, result);
        }

        {
            MutableDoubleCollection target = this.newDoubleForTransform();
            MutableDoubleCollection result = iterable.flatCollectDouble(each -> DoubleLists.immutable.with(
                    (double) (each % 10),
                    (double) (each % 10)), target);
            assertIterablesEqual(
                    this.newDoubleForTransform(3.0, 3.0, 2.0, 2.0, 1.0, 1.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0),
                    result);
            assertSame(target, result);
        }

        {
            MutableFloatCollection target = this.newFloatForTransform();
            MutableFloatCollection result = iterable.flatCollectFloat(each -> FloatLists.immutable.with(
                    (float) (each % 10),
                    (float) (each % 10)), target);
            assertIterablesEqual(
                    this.newFloatForTransform(3.0f, 3.0f, 2.0f, 2.0f, 1.0f, 1.0f, 3.0f, 3.0f, 2.0f, 2.0f, 1.0f, 1.0f),
                    result);
            assertSame(target, result);
        }

        {
            MutableIntCollection target = this.newIntForTransform();
            MutableIntCollection result =
                    iterable.flatCollectInt(each -> IntLists.immutable.with(each % 10, each % 10), target);
            assertIterablesEqual(
                    this.newIntForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1),
                    result);
            assertSame(target, result);
        }

        {
            MutableLongCollection target = this.newLongForTransform();
            MutableLongCollection result =
                    iterable.flatCollectLong(each -> LongLists.immutable.with(each % 10, each % 10), target);
            assertIterablesEqual(
                    this.newLongForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1),
                    result);
            assertSame(target, result);
        }

        {
            MutableShortCollection target = this.newShortForTransform();
            MutableShortCollection result = iterable.flatCollectShort(each -> ShortLists.immutable.with(
                    (short) (each % 10),
                    (short) (each % 10)), target);
            assertIterablesEqual(
                    this.newShortForTransform((short) 3, (short) 3, (short) 2, (short) 2, (short) 1, (short) 1, (short) 3, (short) 3, (short) 2, (short) 2, (short) 1, (short) 1),
                    result);
            assertSame(target, result);
        }

        if (!this.allowsDuplicates())
        {
            return;
        }

        {
            MutableBooleanCollection targetDup = this.newBooleanForTransform();
            MutableBooleanCollection resultDup = this.newWith(3, 3, 2, 2, 1, 1).flatCollectBoolean(
                    each -> BooleanLists.immutable.with(each % 2 == 0, each % 2 == 0),
                    targetDup);
            assertIterablesEqual(this.newBooleanForTransform(false, false, false, false, true, true, true, true, false, false, false, false), resultDup);
            assertSame(targetDup, resultDup);
        }

        RichIterable<Integer> iterableDup = this.newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);

        {
            MutableByteCollection targetDup = this.newByteForTransform();
            MutableByteCollection resultDup = iterableDup.flatCollectByte(
                    each -> ByteLists.immutable.with((byte) (each % 10), (byte) (each % 10)),
                    targetDup);
            assertIterablesEqual(
                    this.newByteForTransform((byte) 3, (byte) 3, (byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 2, (byte) 2, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 3, (byte) 3, (byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 2, (byte) 2, (byte) 1, (byte) 1, (byte) 1, (byte) 1),
                    resultDup);
            assertSame(targetDup, resultDup);
        }

        {
            MutableCharCollection targetDup = this.newCharForTransform();
            MutableCharCollection resultDup = iterableDup.flatCollectChar(
                    each -> CharLists.immutable.with((char) (each % 10), (char) (each % 10)),
                    targetDup);
            assertIterablesEqual(
                    this.newCharForTransform((char) 3, (char) 3, (char) 3, (char) 3, (char) 2, (char) 2, (char) 2, (char) 2, (char) 1, (char) 1, (char) 1, (char) 1, (char) 3, (char) 3, (char) 3, (char) 3, (char) 2, (char) 2, (char) 2, (char) 2, (char) 1, (char) 1, (char) 1, (char) 1),
                    resultDup);
            assertSame(targetDup, resultDup);
        }

        {
            MutableDoubleCollection targetDup = this.newDoubleForTransform();
            MutableDoubleCollection resultDup = iterableDup.flatCollectDouble(
                    each -> DoubleLists.immutable.with((double) (each % 10), (double) (each % 10)),
                    targetDup);
            assertIterablesEqual(
                    this.newDoubleForTransform(3.0, 3.0, 3.0, 3.0, 2.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0, 3.0, 3.0, 3.0, 3.0, 2.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0),
                    resultDup);
            assertSame(targetDup, resultDup);
        }

        {
            MutableFloatCollection targetDup = this.newFloatForTransform();
            MutableFloatCollection resultDup = iterableDup.flatCollectFloat(
                    each -> FloatLists.immutable.with((float) (each % 10), (float) (each % 10)),
                    targetDup);
            assertIterablesEqual(
                    this.newFloatForTransform(3.0f, 3.0f, 3.0f, 3.0f, 2.0f, 2.0f, 2.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 3.0f, 3.0f, 3.0f, 3.0f, 2.0f, 2.0f, 2.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f),
                    resultDup);
            assertSame(targetDup, resultDup);
        }

        {
            MutableIntCollection targetDup = this.newIntForTransform();
            MutableIntCollection resultDup = iterableDup.flatCollectInt(
                    each -> IntLists.immutable.with(each % 10, each % 10),
                    targetDup);
            assertIterablesEqual(
                    this.newIntForTransform(3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1),
                    resultDup);
            assertSame(targetDup, resultDup);
        }

        {
            MutableLongCollection targetDup = this.newLongForTransform();
            MutableLongCollection resultDup = iterableDup.flatCollectLong(
                    each -> LongLists.immutable.with(each % 10, each % 10),
                    targetDup);
            assertIterablesEqual(
                    this.newLongForTransform(3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1),
                    resultDup);
            assertSame(targetDup, resultDup);
        }

        {
            MutableShortCollection targetDup = this.newShortForTransform();
            MutableShortCollection resultDup = iterableDup.flatCollectShort(
                    each -> ShortLists.immutable.with((short) (each % 10), (short) (each % 10)),
                    targetDup);
            assertIterablesEqual(
                    this.newShortForTransform((short) 3, (short) 3, (short) 3, (short) 3, (short) 2, (short) 2, (short) 2, (short) 2, (short) 1, (short) 1, (short) 1, (short) 1, (short) 3, (short) 3, (short) 3, (short) 3, (short) 2, (short) 2, (short) 2, (short) 2, (short) 1, (short) 1, (short) 1, (short) 1),
                    resultDup);
            assertSame(targetDup, resultDup);
        }
    }

    @Test
    default void RichIterable_count()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);

        assertEquals(1, iterable.count(Integer.valueOf(3)::equals));
        assertEquals(1, iterable.count(Integer.valueOf(2)::equals));
        assertEquals(1, iterable.count(Integer.valueOf(1)::equals));
        assertEquals(0, iterable.count(Integer.valueOf(0)::equals));
        assertEquals(2, iterable.count(i -> i % 2 != 0));
        assertEquals(3, iterable.count(i -> i > 0));

        assertEquals(1, iterable.countWith(Object::equals, 3));
        assertEquals(1, iterable.countWith(Object::equals, 2));
        assertEquals(1, iterable.countWith(Object::equals, 1));
        assertEquals(0, iterable.countWith(Object::equals, 0));
        assertEquals(3, iterable.countWith(Predicates2.greaterThan(), 0));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(3, 3, 3, 2, 2, 1);

        assertEquals(3, iterable2.count(Integer.valueOf(3)::equals));
        assertEquals(2, iterable2.count(Integer.valueOf(2)::equals));
        assertEquals(1, iterable2.count(Integer.valueOf(1)::equals));
        assertEquals(0, iterable2.count(Integer.valueOf(0)::equals));
        assertEquals(4, iterable2.count(i -> i % 2 != 0));
        assertEquals(6, iterable2.count(i -> i > 0));

        assertEquals(3, iterable2.countWith(Object::equals, 3));
        assertEquals(2, iterable2.countWith(Object::equals, 2));
        assertEquals(1, iterable2.countWith(Object::equals, 1));
        assertEquals(0, iterable2.countWith(Object::equals, 0));
        assertEquals(6, iterable2.countWith(Predicates2.greaterThan(), 0));
    }

    @Test
    default void RichIterable_anySatisfy_allSatisfy_noneSatisfy()
    {
        {
            RichIterable<Integer> iterable = this.newWith(3, 2, 1);

            assertTrue(iterable.anySatisfy(Predicates.greaterThan(0)));
            assertTrue(iterable.anySatisfy(Predicates.greaterThan(1)));
            assertTrue(iterable.anySatisfy(Predicates.greaterThan(2)));
            assertFalse(iterable.anySatisfy(Predicates.greaterThan(3)));

            assertTrue(iterable.anySatisfyWith(Predicates2.greaterThan(), 0));
            assertTrue(iterable.anySatisfyWith(Predicates2.greaterThan(), 1));
            assertTrue(iterable.anySatisfyWith(Predicates2.greaterThan(), 2));
            assertFalse(iterable.anySatisfyWith(Predicates2.greaterThan(), 3));

            assertTrue(iterable.allSatisfy(Predicates.greaterThan(0)));
            assertFalse(iterable.allSatisfy(Predicates.greaterThan(1)));
            assertFalse(iterable.allSatisfy(Predicates.greaterThan(2)));
            assertFalse(iterable.allSatisfy(Predicates.greaterThan(3)));

            assertTrue(iterable.allSatisfyWith(Predicates2.greaterThan(), 0));
            assertFalse(iterable.allSatisfyWith(Predicates2.greaterThan(), 1));
            assertFalse(iterable.allSatisfyWith(Predicates2.greaterThan(), 2));
            assertFalse(iterable.allSatisfyWith(Predicates2.greaterThan(), 3));

            assertFalse(iterable.noneSatisfy(Predicates.greaterThan(0)));
            assertFalse(iterable.noneSatisfy(Predicates.greaterThan(1)));
            assertFalse(iterable.noneSatisfy(Predicates.greaterThan(2)));
            assertTrue(iterable.noneSatisfy(Predicates.greaterThan(3)));

            assertFalse(iterable.noneSatisfyWith(Predicates2.greaterThan(), 0));
            assertFalse(iterable.noneSatisfyWith(Predicates2.greaterThan(), 1));
            assertFalse(iterable.noneSatisfyWith(Predicates2.greaterThan(), 2));
            assertTrue(iterable.noneSatisfyWith(Predicates2.greaterThan(), 3));
        }

        {
            RichIterable<Integer> iterable = this.newWith(2, 1);

            assertTrue(iterable.anySatisfy(Predicates.greaterThan(0)));
            assertTrue(iterable.anySatisfy(Predicates.greaterThan(1)));
            assertFalse(iterable.anySatisfy(Predicates.greaterThan(2)));

            assertTrue(iterable.anySatisfyWith(Predicates2.greaterThan(), 0));
            assertTrue(iterable.anySatisfyWith(Predicates2.greaterThan(), 1));
            assertFalse(iterable.anySatisfyWith(Predicates2.greaterThan(), 2));

            assertTrue(iterable.allSatisfy(Predicates.greaterThan(0)));
            assertFalse(iterable.allSatisfy(Predicates.greaterThan(1)));
            assertFalse(iterable.allSatisfy(Predicates.greaterThan(2)));

            assertTrue(iterable.allSatisfyWith(Predicates2.greaterThan(), 0));
            assertFalse(iterable.allSatisfyWith(Predicates2.greaterThan(), 1));
            assertFalse(iterable.allSatisfyWith(Predicates2.greaterThan(), 2));

            assertFalse(iterable.noneSatisfy(Predicates.greaterThan(0)));
            assertFalse(iterable.noneSatisfy(Predicates.greaterThan(1)));
            assertTrue(iterable.noneSatisfy(Predicates.greaterThan(2)));

            assertFalse(iterable.noneSatisfyWith(Predicates2.greaterThan(), 0));
            assertFalse(iterable.noneSatisfyWith(Predicates2.greaterThan(), 1));
            assertTrue(iterable.noneSatisfyWith(Predicates2.greaterThan(), 2));
        }

        {
            RichIterable<Integer> iterable = this.newWith(1);

            assertTrue(iterable.anySatisfy(Predicates.greaterThan(0)));
            assertFalse(iterable.anySatisfy(Predicates.greaterThan(1)));

            assertTrue(iterable.anySatisfyWith(Predicates2.greaterThan(), 0));
            assertFalse(iterable.anySatisfyWith(Predicates2.greaterThan(), 1));

            assertTrue(iterable.allSatisfy(Predicates.greaterThan(0)));
            assertFalse(iterable.allSatisfy(Predicates.greaterThan(1)));

            assertTrue(iterable.allSatisfyWith(Predicates2.greaterThan(), 0));
            assertFalse(iterable.allSatisfyWith(Predicates2.greaterThan(), 1));

            assertFalse(iterable.noneSatisfy(Predicates.greaterThan(0)));
            assertTrue(iterable.noneSatisfy(Predicates.greaterThan(1)));

            assertFalse(iterable.noneSatisfyWith(Predicates2.greaterThan(), 0));
            assertTrue(iterable.noneSatisfyWith(Predicates2.greaterThan(), 1));
        }

        RichIterable<Integer> iterable = this.newWith();

        Predicate<Integer> throwPredicate = each -> {
            throw new AssertionError();
        };
        Predicate2<Integer, Object> throwPredicate2 = (each, parameter) -> {
            throw new AssertionError();
        };

        assertFalse(iterable.anySatisfy(throwPredicate));
        assertFalse(iterable.anySatisfyWith(throwPredicate2, null));

        assertTrue(iterable.allSatisfy(throwPredicate));
        assertTrue(iterable.allSatisfyWith(throwPredicate2, null));

        assertTrue(iterable.noneSatisfy(throwPredicate));
        assertTrue(iterable.noneSatisfyWith(throwPredicate2, null));
    }

    @Test
    default void RichIterable_detect()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);

        assertThat(iterable.detect(Predicates.greaterThan(0)), is(3));
        assertThat(iterable.detect(Predicates.greaterThan(1)), is(3));
        assertThat(iterable.detect(Predicates.greaterThan(2)), is(3));
        assertThat(iterable.detect(Predicates.greaterThan(3)), nullValue());

        assertThat(iterable.detect(Predicates.lessThan(1)), nullValue());
        assertThat(iterable.detect(Predicates.lessThan(2)), is(1));
        assertThat(iterable.detect(Predicates.lessThan(3)), is(2));
        assertThat(iterable.detect(Predicates.lessThan(4)), is(3));

        assertThat(iterable.detectWith(Predicates2.greaterThan(), 0), is(3));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 1), is(3));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 2), is(3));
        assertThat(iterable.detectWith(Predicates2.greaterThan(), 3), nullValue());

        assertThat(iterable.detectWith(Predicates2.lessThan(), 1), nullValue());
        assertThat(iterable.detectWith(Predicates2.lessThan(), 2), is(1));
        assertThat(iterable.detectWith(Predicates2.lessThan(), 3), is(2));
        assertThat(iterable.detectWith(Predicates2.lessThan(), 4), is(3));

        assertThat(iterable.detectIfNone(Predicates.greaterThan(0), () -> 4), is(3));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(1), () -> 4), is(3));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(2), () -> 4), is(3));
        assertThat(iterable.detectIfNone(Predicates.greaterThan(3), () -> 4), is(4));

        assertThat(iterable.detectIfNone(Predicates.lessThan(1), () -> 4), is(4));
        assertThat(iterable.detectIfNone(Predicates.lessThan(2), () -> 4), is(1));
        assertThat(iterable.detectIfNone(Predicates.lessThan(3), () -> 4), is(2));
        assertThat(iterable.detectIfNone(Predicates.lessThan(4), () -> 4), is(3));

        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 0, () -> 4), is(3));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 1, () -> 4), is(3));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 2, () -> 4), is(3));
        assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 3, () -> 4), is(4));

        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 1, () -> 4), is(4));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 2, () -> 4), is(1));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 3, () -> 4), is(2));
        assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 4, () -> 4), is(3));

        assertThat(iterable.detectOptional(Predicates.greaterThan(0)), is(Optional.of(3)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(1)), is(Optional.of(3)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(2)), is(Optional.of(3)));
        assertThat(iterable.detectOptional(Predicates.greaterThan(3)), is(Optional.empty()));

        assertThat(iterable.detectOptional(Predicates.lessThan(1)), is(Optional.empty()));
        assertThat(iterable.detectOptional(Predicates.lessThan(2)), is(Optional.of(1)));
        assertThat(iterable.detectOptional(Predicates.lessThan(3)), is(Optional.of(2)));
        assertThat(iterable.detectOptional(Predicates.lessThan(4)), is(Optional.of(3)));

        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 0), is(Optional.of(3)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 1), is(Optional.of(3)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 2), is(Optional.of(3)));
        assertThat(iterable.detectWithOptional(Predicates2.greaterThan(), 3), is(Optional.empty()));

        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 1), is(Optional.empty()));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 2), is(Optional.of(1)));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 3), is(Optional.of(2)));
        assertThat(iterable.detectWithOptional(Predicates2.lessThan(), 4), is(Optional.of(3)));
    }

    @Test
    default void RichIterable_detectOptionalNull()
    {
        RichIterable<Integer> iterable = this.newWith(1, null, 3);

        assertThrows(NullPointerException.class, () -> iterable.detectOptional(Objects::isNull));
        assertThrows(NullPointerException.class, () -> iterable.detectWithOptional((i, object) -> i == object, null));
    }

    @Test
    default void RichIterable_min_max()
    {
        assertEquals(Integer.valueOf(-1), this.newWith(-1, 0, 1).min());
        assertEquals(Integer.valueOf(-1), this.newWith(1, 0, -1).min());
        assertThrows(NoSuchElementException.class, () -> this.newWith().min());

        assertEquals(Integer.valueOf(1), this.newWith(-1, 0, 1).max());
        assertEquals(Integer.valueOf(1), this.newWith(1, 0, -1).max());
        assertThrows(NoSuchElementException.class, () -> this.newWith().max());

        assertEquals(Integer.valueOf(1), this.newWith(-1, 0, 1).min(Comparators.reverseNaturalOrder()));
        assertEquals(Integer.valueOf(1), this.newWith(1, 0, -1).min(Comparators.reverseNaturalOrder()));
        assertThrows(NoSuchElementException.class, () -> this.newWith().min(Comparators.reverseNaturalOrder()));

        assertEquals(Integer.valueOf(-1), this.newWith(-1, 0, 1).max(Comparators.reverseNaturalOrder()));
        assertEquals(Integer.valueOf(-1), this.newWith(1, 0, -1).max(Comparators.reverseNaturalOrder()));
        assertThrows(NoSuchElementException.class, () -> this.newWith().max(Comparators.reverseNaturalOrder()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertEquals(Integer.valueOf(-1), this.newWith(-1, -1, 0, 0, 1, 1).min());
        assertEquals(Integer.valueOf(-1), this.newWith(1, 1, 0, 0, -1, -1).min());

        assertEquals(Integer.valueOf(1), this.newWith(-1, -1, 0, 0, 1, 1).max());
        assertEquals(Integer.valueOf(1), this.newWith(1, 1, 0, 0, -1, -1).max());

        assertEquals(Integer.valueOf(1), this.newWith(-1, -1, 0, 0, 1, 1).min(Comparators.reverseNaturalOrder()));
        assertEquals(Integer.valueOf(1), this.newWith(1, 1, 0, 0, -1, -1).min(Comparators.reverseNaturalOrder()));

        assertEquals(Integer.valueOf(-1), this.newWith(-1, -1, 0, 0, 1, 1).max(Comparators.reverseNaturalOrder()));
        assertEquals(Integer.valueOf(-1), this.newWith(1, 1, 0, 0, -1, -1).max(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void RichIterable_min_max_non_comparable()
    {
        Object sentinel = new Object();

        assertSame(sentinel, this.newWith(sentinel).min());
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).min());

        assertSame(sentinel, this.newWith(sentinel).max());
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).max());

        assertSame(sentinel, this.newWith(sentinel).min(Comparators.reverseNaturalOrder()));
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).min(Comparators.reverseNaturalOrder()));

        assertSame(sentinel, this.newWith(sentinel).max(Comparators.reverseNaturalOrder()));
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).max(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void RichIterable_minOptional_maxOptional()
    {
        assertEquals(Optional.of(-1), this.newWith(-1, 0, 1).minOptional());
        assertEquals(Optional.of(-1), this.newWith(1, 0, -1).minOptional());
        assertSame(Optional.empty(), this.newWith().minOptional());
        assertThrows(NullPointerException.class, () -> this.newWith(new Object[]{null}).minOptional());

        assertEquals(Optional.of(1), this.newWith(-1, 0, 1).maxOptional());
        assertEquals(Optional.of(1), this.newWith(1, 0, -1).maxOptional());
        assertSame(Optional.empty(), this.newWith().maxOptional());
        assertThrows(NullPointerException.class, () -> this.newWith(new Object[]{null}).maxOptional());

        assertEquals(Optional.of(1), this.newWith(-1, 0, 1).minOptional(Comparators.reverseNaturalOrder()));
        assertEquals(Optional.of(1), this.newWith(1, 0, -1).minOptional(Comparators.reverseNaturalOrder()));
        assertSame(Optional.empty(), this.newWith().minOptional(Comparators.reverseNaturalOrder()));
        assertThrows(NullPointerException.class, () -> this.newWith(new Object[]{null}).minOptional(Comparators.reverseNaturalOrder()));

        assertEquals(Optional.of(-1), this.newWith(-1, 0, 1).maxOptional(Comparators.reverseNaturalOrder()));
        assertEquals(Optional.of(-1), this.newWith(1, 0, -1).maxOptional(Comparators.reverseNaturalOrder()));
        assertSame(Optional.empty(), this.newWith().maxOptional(Comparators.reverseNaturalOrder()));
        assertThrows(NullPointerException.class, () -> this.newWith(new Object[]{null}).maxOptional(Comparators.reverseNaturalOrder()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertEquals(Optional.of(-1), this.newWith(-1, -1, 0, 0, 1, 1).minOptional());
        assertEquals(Optional.of(-1), this.newWith(1, 1, 0, 0, -1, -1).minOptional());

        assertEquals(Optional.of(1), this.newWith(-1, -1, 0, 0, 1, 1).maxOptional());
        assertEquals(Optional.of(1), this.newWith(1, 1, 0, 0, -1, -1).maxOptional());

        assertEquals(Optional.of(1), this.newWith(-1, -1, 0, 0, 1, 1).minOptional(Comparators.reverseNaturalOrder()));
        assertEquals(Optional.of(1), this.newWith(1, 1, 0, 0, -1, -1).minOptional(Comparators.reverseNaturalOrder()));

        assertEquals(Optional.of(-1), this.newWith(-1, -1, 0, 0, 1, 1).maxOptional(Comparators.reverseNaturalOrder()));
        assertEquals(Optional.of(-1), this.newWith(1, 1, 0, 0, -1, -1).maxOptional(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void RichIterable_minOptional_maxOptional_non_comparable()
    {
        Object sentinel = new Object();

        assertEquals(Optional.of(sentinel), this.newWith(sentinel).minOptional());
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).minOptional());

        assertEquals(Optional.of(sentinel), this.newWith(sentinel).maxOptional());
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).maxOptional());

        assertEquals(Optional.of(sentinel), this.newWith(sentinel).minOptional(Comparators.reverseNaturalOrder()));
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).minOptional(Comparators.reverseNaturalOrder()));

        assertEquals(Optional.of(sentinel), this.newWith(sentinel).maxOptional(Comparators.reverseNaturalOrder()));
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).maxOptional(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void RichIterable_minBy_maxBy()
    {
        assertEquals("da", this.newWith("ed", "da", "ca", "bc", "ab").minBy(string -> string.charAt(string.length() - 1)));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().minBy(string -> string.charAt(string.length() - 1)));

        assertEquals("dz", this.newWith("ew", "dz", "cz", "bx", "ay").maxBy(string -> string.charAt(string.length() - 1)));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().maxBy(string -> string.charAt(string.length() - 1)));

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertEquals("da", this.newWith("ed", "ed", "da", "da", "ca", "ca", "bc", "bc", "ab", "ab").minBy(string -> string.charAt(string.length() - 1)));
        assertEquals("dz", this.newWith("ew", "ew", "dz", "dz", "cz", "cz", "bx", "bx", "ay", "ay").maxBy(string -> string.charAt(string.length() - 1)));
    }

    @Test
    default void RichIterable_minByOptional_maxByOptional()
    {
        assertEquals(Optional.of("da"), this.newWith("ed", "da", "ca", "bc", "ab").minByOptional(string -> string.charAt(string.length() - 1)));
        assertSame(Optional.empty(), this.<String>newWith().minByOptional(string -> string.charAt(string.length() - 1)));
        assertThrows(NullPointerException.class, () -> this.newWith(new Object[]{null}).minByOptional(Objects::isNull));

        assertEquals(Optional.of("dz"), this.newWith("ew", "dz", "cz", "bx", "ay").maxByOptional(string -> string.charAt(string.length() - 1)));
        assertSame(Optional.empty(), this.<String>newWith().maxByOptional(string -> string.charAt(string.length() - 1)));
        assertThrows(NullPointerException.class, () -> this.newWith(new Object[]{null}).maxByOptional(Objects::isNull));

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertEquals(Optional.of("da"), this.newWith("ed", "ed", "da", "da", "ca", "ca", "bc", "bc", "ab", "ab").minByOptional(string -> string.charAt(string.length() - 1)));
        assertEquals(Optional.of("dz"), this.newWith("ew", "ew", "dz", "dz", "cz", "cz", "bx", "bx", "ay", "ay").maxByOptional(string -> string.charAt(string.length() - 1)));
    }

    @Test
    default void RichIterable_groupBy()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        Function<Integer, Boolean> groupByFunction = object -> IntegerPredicates.isOdd().accept(object);

        MutableMap<Boolean, RichIterable<Integer>> groupByExpected =
                UnifiedMap.newWithKeysValues(
                        Boolean.TRUE, this.newMutableForFilter(3, 1),
                        Boolean.FALSE, this.newMutableForFilter(4, 2));

        assertIterablesEqual(groupByExpected, iterable.groupBy(groupByFunction).toMap());

        Function<Integer, Boolean> function = (Integer object) -> true;
        MutableMultimap<Boolean, Integer> target = this.<Integer>newWith().groupBy(function).toMutable();
        MutableMultimap<Boolean, Integer> multimap2 = iterable.groupBy(groupByFunction, target);
        assertIterablesEqual(groupByExpected, multimap2.toMap());
        assertSame(target, multimap2);

        Function<Integer, Iterable<Integer>> groupByEachFunction = integer -> Interval.fromTo(-1, -integer);

        MutableMap<Integer, RichIterable<Integer>> expectedGroupByEach =
                UnifiedMap.newWithKeysValues(
                        -4, this.newMutableForFilter(4),
                        -3, this.newMutableForFilter(4, 3),
                        -2, this.newMutableForFilter(4, 3, 2),
                        -1, this.newMutableForFilter(4, 3, 2, 1));

        assertIterablesEqual(expectedGroupByEach, iterable.groupByEach(groupByEachFunction).toMap());

        MutableMultimap<Integer, Integer> target2 = this.<Integer>newWith().groupByEach(groupByEachFunction).toMutable();
        Multimap<Integer, Integer> actualWithTarget = iterable.groupByEach(groupByEachFunction, target2);
        assertIterablesEqual(expectedGroupByEach, actualWithTarget.toMap());
        assertSame(target2, actualWithTarget);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        MutableMap<Boolean, RichIterable<Integer>> expectedGroupByDup =
                UnifiedMap.newWithKeysValues(
                        Boolean.TRUE, this.newMutableForFilter(3, 3, 3, 1),
                        Boolean.FALSE, this.newMutableForFilter(4, 4, 4, 4, 2, 2));

        assertIterablesEqual(expectedGroupByDup, iterableDup.groupBy(groupByFunction).toMap());

        MutableMultimap<Boolean, Integer> targetDup = this.<Integer>newWith().groupBy(function).toMutable();
        MutableMultimap<Boolean, Integer> multimap2Dup = iterableDup.groupBy(groupByFunction, targetDup);
        assertIterablesEqual(expectedGroupByDup, multimap2Dup.toMap());
        assertSame(targetDup, multimap2Dup);

        MutableMap<Integer, RichIterable<Integer>> expectedGroupByEachDup =
                UnifiedMap.newWithKeysValues(
                        -4, this.newMutableForFilter(4, 4, 4, 4),
                        -3, this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3),
                        -2, this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2),
                        -1, this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1));

        assertIterablesEqual(expectedGroupByEachDup, iterableDup.groupByEach(groupByEachFunction).toMap());

        MutableMultimap<Integer, Integer> target2Dup = this.<Integer>newWith().groupByEach(groupByEachFunction).toMutable();
        Multimap<Integer, Integer> actualWithTargetDup = iterableDup.groupByEach(groupByEachFunction, target2Dup);
        assertIterablesEqual(expectedGroupByEachDup, actualWithTargetDup.toMap());
        assertSame(target2Dup, actualWithTargetDup);
    }

    /**
     * @since 9.0
     */
    @Test
    default void RichIterable_countBy()
    {
        RichIterable<Integer> integers = this.newWith(4, 3, 2, 1);
        Bag<Integer> evensAndOdds = integers.countBy(each -> Integer.valueOf(each % 2));
        assertEquals(2, evensAndOdds.occurrencesOf(1));
        assertEquals(2, evensAndOdds.occurrencesOf(0));
        Bag<Integer> evensAndOdds2 = integers.countBy(each -> Integer.valueOf(each % 2), Bags.mutable.empty());
        assertEquals(2, evensAndOdds2.occurrencesOf(1));
        assertEquals(2, evensAndOdds2.occurrencesOf(0));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> integersDup = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        Bag<Integer> evensAndOddsDup = integersDup.countBy(each -> Integer.valueOf(each % 2));
        assertEquals(4, evensAndOddsDup.occurrencesOf(1));
        assertEquals(6, evensAndOddsDup.occurrencesOf(0));
        Bag<Integer> evensAndOdds2Dup = integersDup.countBy(each -> Integer.valueOf(each % 2), Bags.mutable.empty());
        assertEquals(4, evensAndOdds2Dup.occurrencesOf(1));
        assertEquals(6, evensAndOdds2Dup.occurrencesOf(0));
    }

    /**
     * @since 9.0
     */
    @Test
    default void RichIterable_countByWith()
    {
        RichIterable<Integer> integers = this.newWith(4, 3, 2, 1);
        Bag<Integer> evensAndOdds = integers.countByWith((each, parm) -> Integer.valueOf(each % parm), 2);
        assertEquals(2, evensAndOdds.occurrencesOf(1));
        assertEquals(2, evensAndOdds.occurrencesOf(0));
        Bag<Integer> evensAndOdds2 = integers.countByWith((each, parm) -> Integer.valueOf(each % parm), 2, Bags.mutable.empty());
        assertEquals(2, evensAndOdds2.occurrencesOf(1));
        assertEquals(2, evensAndOdds2.occurrencesOf(0));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> integersDup = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        Bag<Integer> evensAndOddsDup = integersDup.countByWith((each, parm) -> Integer.valueOf(each % parm), 2);
        assertEquals(4, evensAndOddsDup.occurrencesOf(1));
        assertEquals(6, evensAndOddsDup.occurrencesOf(0));
        Bag<Integer> evensAndOdds2Dup = integersDup.countByWith((each, parm) -> Integer.valueOf(each % parm), 2, Bags.mutable.empty());
        assertEquals(4, evensAndOdds2Dup.occurrencesOf(1));
        assertEquals(6, evensAndOdds2Dup.occurrencesOf(0));
    }

    /**
     * @since 10.0.0
     */
    @Test
    default void RichIterable_countByEach()
    {
        RichIterable<Integer> integers = this.newWith(4, 3, 2, 1);
        Bag<Integer> integerBag1 = integers.countByEach(each -> IntInterval.oneTo(5).collect(i -> each * i));
        assertEquals(1, integerBag1.occurrencesOf(1));
        assertEquals(2, integerBag1.occurrencesOf(2));
        assertEquals(2, integerBag1.occurrencesOf(3));
        assertEquals(3, integerBag1.occurrencesOf(4));
        assertEquals(1, integerBag1.occurrencesOf(5));
        assertEquals(2, integerBag1.occurrencesOf(6));
        assertEquals(2, integerBag1.occurrencesOf(8));
        assertEquals(1, integerBag1.occurrencesOf(9));
        assertEquals(1, integerBag1.occurrencesOf(10));
        assertEquals(2, integerBag1.occurrencesOf(12));
        assertEquals(1, integerBag1.occurrencesOf(15));
        assertEquals(1, integerBag1.occurrencesOf(16));
        assertEquals(1, integerBag1.occurrencesOf(20));
        Bag<Integer> integerBag2 = integers.countByEach(each -> IntInterval.oneTo(5).collect(i -> each * i), Bags.mutable.empty());
        assertEquals(1, integerBag2.occurrencesOf(1));
        assertEquals(2, integerBag2.occurrencesOf(2));
        assertEquals(2, integerBag2.occurrencesOf(3));
        assertEquals(3, integerBag2.occurrencesOf(4));
        assertEquals(1, integerBag2.occurrencesOf(5));
        assertEquals(2, integerBag2.occurrencesOf(6));
        assertEquals(2, integerBag2.occurrencesOf(8));
        assertEquals(1, integerBag2.occurrencesOf(9));
        assertEquals(1, integerBag2.occurrencesOf(10));
        assertEquals(2, integerBag2.occurrencesOf(12));
        assertEquals(1, integerBag2.occurrencesOf(15));
        assertEquals(1, integerBag2.occurrencesOf(16));
        assertEquals(1, integerBag2.occurrencesOf(20));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> integersDup = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        Bag<Integer> integerBag1Dup = integersDup.countByEach(each -> IntInterval.oneTo(5).collect(i -> each * i));
        assertEquals(1, integerBag1Dup.occurrencesOf(1));
        assertEquals(3, integerBag1Dup.occurrencesOf(2));
        assertEquals(4, integerBag1Dup.occurrencesOf(3));
        assertEquals(7, integerBag1Dup.occurrencesOf(4));
        assertEquals(1, integerBag1Dup.occurrencesOf(5));
        assertEquals(5, integerBag1Dup.occurrencesOf(6));
        assertEquals(6, integerBag1Dup.occurrencesOf(8));
        assertEquals(3, integerBag1Dup.occurrencesOf(9));
        assertEquals(2, integerBag1Dup.occurrencesOf(10));
        assertEquals(7, integerBag1Dup.occurrencesOf(12));
        assertEquals(3, integerBag1Dup.occurrencesOf(15));
        assertEquals(4, integerBag1Dup.occurrencesOf(16));
        assertEquals(4, integerBag1Dup.occurrencesOf(20));
        Bag<Integer> integerBag2Dup = integersDup.countByEach(each -> IntInterval.oneTo(5).collect(i -> each * i), Bags.mutable.empty());
        assertEquals(1, integerBag2Dup.occurrencesOf(1));
        assertEquals(3, integerBag2Dup.occurrencesOf(2));
        assertEquals(4, integerBag2Dup.occurrencesOf(3));
        assertEquals(7, integerBag2Dup.occurrencesOf(4));
        assertEquals(1, integerBag2Dup.occurrencesOf(5));
        assertEquals(5, integerBag2Dup.occurrencesOf(6));
        assertEquals(6, integerBag2Dup.occurrencesOf(8));
        assertEquals(3, integerBag2Dup.occurrencesOf(9));
        assertEquals(2, integerBag2Dup.occurrencesOf(10));
        assertEquals(7, integerBag2Dup.occurrencesOf(12));
        assertEquals(3, integerBag2Dup.occurrencesOf(15));
        assertEquals(4, integerBag2Dup.occurrencesOf(16));
        assertEquals(4, integerBag2Dup.occurrencesOf(20));
    }

    @Test
    default void RichIterable_aggregateBy_aggregateInPlaceBy_reduceBy()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        MapIterable<String, Integer> aggregateBy = iterable.aggregateBy(
                Object::toString,
                () -> 0,
                (integer1, integer2) -> integer1 + integer2);

        assertEquals(4, aggregateBy.get("4").intValue());
        assertEquals(3, aggregateBy.get("3").intValue());
        assertEquals(2, aggregateBy.get("2").intValue());
        assertEquals(1, aggregateBy.get("1").intValue());

        MapIterable<String, AtomicInteger> aggregateInPlaceBy = iterable.aggregateInPlaceBy(
                String::valueOf,
                AtomicInteger::new,
                AtomicInteger::addAndGet);
        assertEquals(4, aggregateInPlaceBy.get("4").intValue());
        assertEquals(3, aggregateInPlaceBy.get("3").intValue());
        assertEquals(2, aggregateInPlaceBy.get("2").intValue());
        assertEquals(1, aggregateInPlaceBy.get("1").intValue());

        MapIterable<String, Integer> reduceBy = iterable.reduceBy(
                Object::toString,
                (integer1, integer2) -> integer1 + integer2);

        assertEquals(4, reduceBy.get("4").intValue());
        assertEquals(3, reduceBy.get("3").intValue());
        assertEquals(2, reduceBy.get("2").intValue());
        assertEquals(1, reduceBy.get("1").intValue());

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> duplicateIterable = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        MapIterable<String, Integer> duplicateAggregateBy = duplicateIterable.aggregateBy(
                Object::toString,
                () -> 0,
                (integer1, integer2) -> integer1 + integer2);

        assertEquals(16, duplicateAggregateBy.get("4").intValue());
        assertEquals(9, duplicateAggregateBy.get("3").intValue());
        assertEquals(4, duplicateAggregateBy.get("2").intValue());
        assertEquals(1, duplicateAggregateBy.get("1").intValue());

        MapIterable<String, AtomicInteger> duplicateAggregateInPlaceBy = duplicateIterable.aggregateInPlaceBy(
                String::valueOf,
                AtomicInteger::new,
                AtomicInteger::addAndGet);
        assertEquals(16, duplicateAggregateInPlaceBy.get("4").intValue());
        assertEquals(9, duplicateAggregateInPlaceBy.get("3").intValue());
        assertEquals(4, duplicateAggregateInPlaceBy.get("2").intValue());
        assertEquals(1, duplicateAggregateInPlaceBy.get("1").intValue());

        MapIterable<String, Integer> duplicateReduceBy = duplicateIterable.reduceBy(
                Object::toString,
                (integer1, integer2) -> integer1 + integer2);

        assertEquals(16, duplicateReduceBy.get("4").intValue());
        assertEquals(9, duplicateReduceBy.get("3").intValue());
        assertEquals(4, duplicateReduceBy.get("2").intValue());
        assertEquals(1, duplicateReduceBy.get("1").intValue());
    }

    @Test
    default void RichIterable_sumOfPrimitive()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertEquals(10.0f, iterable.sumOfFloat(Integer::floatValue), 0.001);
        assertEquals(10.0, iterable.sumOfDouble(Integer::doubleValue), 0.001);
        assertEquals(10, iterable.sumOfInt(integer -> integer));
        assertEquals(10L, iterable.sumOfLong(Integer::longValue));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        assertEquals(30.0f, iterable2.sumOfFloat(Integer::floatValue), 0.001);
        assertEquals(30.0, iterable2.sumOfDouble(Integer::doubleValue), 0.001);
        assertEquals(30, iterable2.sumOfInt(Integer::intValue));
        assertEquals(30L, iterable2.sumOfLong(Integer::longValue));
    }

    @Test
    default void RichIterable_sumByPrimitive()
    {
        RichIterable<String> iterable = this.newWith("4", "3", "2", "1");

        assertIterablesEqual(
                ObjectLongMaps.immutable.with(0, 6L).newWithKeyValue(1, 4L),
                iterable.sumByInt(s -> Integer.parseInt(s) % 2, Integer::parseInt));

        assertIterablesEqual(
                ObjectLongMaps.immutable.with(0, 6L).newWithKeyValue(1, 4L),
                iterable.sumByLong(s -> Integer.parseInt(s) % 2, Long::parseLong));

        assertIterablesEqual(
                ObjectDoubleMaps.immutable.with(0, 6.0d).newWithKeyValue(1, 4.0d),
                iterable.sumByDouble(s -> Integer.parseInt(s) % 2, Double::parseDouble));

        assertIterablesEqual(
                ObjectDoubleMaps.immutable.with(0, 6.0d).newWithKeyValue(1, 4.0d),
                iterable.sumByFloat(s -> Integer.parseInt(s) % 2, Float::parseFloat));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<String> iterable2 = this.newWith("4", "4", "4", "4", "3", "3", "3", "2", "2", "1");

        assertIterablesEqual(
                ObjectLongMaps.immutable.with(0, 20L).newWithKeyValue(1, 10L),
                iterable2.sumByInt(s -> Integer.parseInt(s) % 2, Integer::parseInt));

        assertIterablesEqual(
                ObjectLongMaps.immutable.with(0, 20L).newWithKeyValue(1, 10L),
                iterable2.sumByLong(s -> Integer.parseInt(s) % 2, Long::parseLong));

        assertIterablesEqual(
                ObjectDoubleMaps.immutable.with(0, 20.0d).newWithKeyValue(1, 10.0d),
                iterable2.sumByDouble(s -> Integer.parseInt(s) % 2, Double::parseDouble));

        assertIterablesEqual(
                ObjectDoubleMaps.immutable.with(0, 20.0d).newWithKeyValue(1, 10.0d),
                iterable2.sumByFloat(s -> Integer.parseInt(s) % 2, Float::parseFloat));
    }

    @Test
    default void RichIterable_summarizePrimitive()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertEquals(10.0f, iterable.summarizeFloat(Integer::floatValue).getSum(), 0.001);
        assertEquals(10.0, iterable.summarizeDouble(Integer::doubleValue).getSum(), 0.001);
        assertEquals(10, iterable.summarizeInt(Integer::intValue).getSum());
        assertEquals(10L, iterable.summarizeLong(Integer::longValue).getSum());

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> bigIterable = this.newWith(5, 5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        assertEquals(55.0f, bigIterable.summarizeFloat(Integer::floatValue).getSum(), 0.001);
        assertEquals(55.0, bigIterable.summarizeDouble(Integer::doubleValue).getSum(), 0.001);
        assertEquals(55, bigIterable.summarizeInt(Integer::intValue).getSum());
        assertEquals(55L, bigIterable.summarizeLong(Integer::longValue).getSum());

        RichIterable<Integer> littleIterable = this.newWith(5, 4, 3, 2, 1);

        assertEquals(15.0f, littleIterable.summarizeFloat(Integer::floatValue).getSum(), 0.001);
        assertEquals(15.0, littleIterable.summarizeDouble(Integer::doubleValue).getSum(), 0.001);
        assertEquals(15, littleIterable.summarizeInt(Integer::intValue).getSum());
        assertEquals(15L, littleIterable.summarizeLong(Integer::longValue).getSum());
    }

    @Test
    default void RichIterable_reduceInPlaceCollector()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 3);
        MutableBag<Integer> result = iterable.reduceInPlace(Collectors.toCollection(Bags.mutable::empty));
        assertEquals(Bags.immutable.with(1, 2, 3), result);

        String joining = result.collect(Object::toString).reduceInPlace(Collectors.joining(","));
        assertEquals(result.collect(Object::toString).makeString(","), joining);

        String joining2 = result.toImmutable().collect(Object::toString).reduceInPlace(Collectors.joining(","));
        assertEquals(result.toImmutable().collect(Object::toString).makeString(","), joining2);

        String joining3 = result.asLazy().collect(Object::toString).reduceInPlace(Collectors.joining(","));
        assertEquals(result.asLazy().collect(Object::toString).makeString(","), joining3);

        Map<Boolean, List<Integer>> expected =
                iterable.toList().stream().collect(Collectors.partitioningBy(each -> each % 2 == 0));
        Map<Boolean, List<Integer>> actual =
                iterable.reduceInPlace(Collectors.partitioningBy(each -> each % 2 == 0));
        assertEquals(expected, actual);

        Map<String, List<Integer>> groupByJDK =
                iterable.toList().stream().collect(Collectors.groupingBy(Object::toString));
        Map<String, List<Integer>> groupByEC =
                result.reduceInPlace(Collectors.groupingBy(Object::toString));
        assertEquals(groupByJDK, groupByEC);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> littleIterable = this.newWith(1, 2, 3, 1, 2, 3);
        MutableBag<Integer> duplicateResult =
                littleIterable.reduceInPlace(Collectors.toCollection(Bags.mutable::empty));
        assertEquals(Bags.immutable.with(1, 1, 2, 2, 3, 3), duplicateResult);

        RichIterable<Integer> bigIterable = this.newWith(Interval.oneTo(20).toArray());
        MutableBag<Integer> bigResult =
                bigIterable.reduceInPlace(Collectors.toCollection(Bags.mutable::empty));
        assertEquals(Interval.oneTo(20).toBag(), bigResult);

        String duplicateJoining =
                duplicateResult.collect(Object::toString).reduceInPlace(Collectors.joining(","));
        assertEquals(duplicateResult.collect(Object::toString).makeString(","), duplicateJoining);

        ImmutableBag<Integer> immutableBag = duplicateResult.toImmutable();
        String duplicateJoining2 =
                immutableBag.collect(Object::toString).reduceInPlace(Collectors.joining(","));
        assertEquals(immutableBag.collect(Object::toString).makeString(","), duplicateJoining2);

        String duplicateJoining3 =
                duplicateResult.asLazy().collect(Object::toString).reduceInPlace(Collectors.joining(","));
        assertEquals(duplicateResult.asLazy().collect(Object::toString).makeString(","), duplicateJoining3);

        Map<Boolean, List<Integer>> duplicateExpected =
                littleIterable.toList().stream().collect(Collectors.partitioningBy(each -> each % 2 == 0));
        Map<Boolean, List<Integer>> duplicateActual =
                littleIterable.reduceInPlace(Collectors.partitioningBy(each -> each % 2 == 0));
        assertEquals(duplicateExpected, duplicateActual);

        Map<String, List<Integer>> duplicateGroupByJDK =
                littleIterable.toList().stream().collect(Collectors.groupingBy(Object::toString));
        Map<String, List<Integer>> duplicateGroupByEC =
                duplicateResult.reduceInPlace(Collectors.groupingBy(Object::toString));
        assertEquals(duplicateGroupByJDK, duplicateGroupByEC);
    }

    @Test
    default void RichIterable_reduceInPlace()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 3);
        MutableBag<Integer> result =
                iterable.reduceInPlace(Bags.mutable::empty, MutableBag::add);
        assertEquals(Bags.immutable.with(1, 2, 3), result);

        String joining =
                result.collect(Object::toString).reduceInPlace(StringBuilder::new, StringBuilder::append).toString();
        assertEquals(result.collect(Object::toString).makeString(""), joining);

        ImmutableBag<Integer> immutableBag = result.toImmutable();
        String joining2 =
                immutableBag.collect(Object::toString).reduceInPlace(StringBuilder::new, StringBuilder::append).toString();
        assertEquals(immutableBag.collect(Object::toString).makeString(""), joining2);

        String joining3 =
                result.asLazy().collect(Object::toString).reduceInPlace(StringBuilder::new, StringBuilder::append).toString();
        assertEquals(result.asLazy().collect(Object::toString).makeString(""), joining3);

        int atomicAdd = iterable.reduceInPlace(AtomicInteger::new, AtomicInteger::addAndGet).get();
        assertEquals(6, atomicAdd);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> littleIterable = this.newWith(1, 2, 3, 1, 2, 3);
        MutableBag<Integer> resultDup =
                littleIterable.reduceInPlace(Bags.mutable::empty, MutableBag::add);
        assertEquals(Bags.immutable.with(1, 1, 2, 2, 3, 3), resultDup);

        RichIterable<Integer> bigIterable = this.newWith(Interval.oneTo(20).toArray());
        MutableBag<Integer> bigResult =
                bigIterable.reduceInPlace(Bags.mutable::empty, MutableBag::add);
        assertEquals(Interval.oneTo(20).toBag(), bigResult);

        String joiningDup =
                resultDup.collect(Object::toString).reduceInPlace(StringBuilder::new, StringBuilder::append).toString();
        assertEquals(resultDup.collect(Object::toString).makeString(""), joiningDup);

        ImmutableBag<Integer> immutableBagDup = resultDup.toImmutable();
        String joining2Dup =
                immutableBagDup.collect(Object::toString).reduceInPlace(StringBuilder::new, StringBuilder::append).toString();
        assertEquals(immutableBagDup.collect(Object::toString).makeString(""), joining2Dup);

        String joining3Dup =
                resultDup.asLazy().collect(Object::toString).reduceInPlace(StringBuilder::new, StringBuilder::append).toString();
        assertEquals(resultDup.asLazy().collect(Object::toString).makeString(""), joining3Dup);

        int atomicAddDup = littleIterable.reduceInPlace(AtomicInteger::new, AtomicInteger::addAndGet).get();
        assertEquals(12, atomicAddDup);
    }

    @Test
    default void RichIterable_reduceOptional()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 3);
        Optional<Integer> result =
                iterable.reduce(Integer::sum);
        assertEquals(6, result.get().intValue());

        Optional<Integer> max =
                iterable.reduce(Integer::max);
        assertEquals(3, max.get().intValue());

        Optional<Integer> min =
                iterable.reduce(Integer::min);
        assertEquals(1, min.get().intValue());

        RichIterable<Integer> iterableEmpty = this.newWith();
        Optional<Integer> resultEmpty =
                iterableEmpty.reduce(Integer::sum);
        assertFalse(resultEmpty.isPresent());

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> littleIterable = this.newWith(1, 2, 3, 1, 2, 3);
        Optional<Integer> resultDup =
                littleIterable.reduce(Integer::sum);
        assertEquals(12, resultDup.get().intValue());

        RichIterable<Integer> bigIterable = this.newWith(Interval.oneTo(20).toArray());
        Optional<Integer> bigResult =
                bigIterable.reduce(Integer::max);
        assertEquals(20, bigResult.get().intValue());

        Optional<Integer> maxDup =
                littleIterable.reduce(Integer::max);
        assertEquals(3, maxDup.get().intValue());

        Optional<Integer> minDup =
                littleIterable.reduce(Integer::min);
        assertEquals(1, minDup.get().intValue());
    }

    @Test
    default void RichIterable_injectInto()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertEquals(Integer.valueOf(11), iterable.injectInto(1, new Function2<Integer, Integer, Integer>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer value(Integer argument1, Integer argument2)
            {
                return argument1 + argument2;
            }
        }));
        assertEquals(Integer.valueOf(10), iterable.injectInto(0, new Function2<Integer, Integer, Integer>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer value(Integer argument1, Integer argument2)
            {
                return argument1 + argument2;
            }
        }));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        assertEquals(Integer.valueOf(31), iterableDup.injectInto(1, AddFunction.INTEGER));
        assertEquals(Integer.valueOf(30), iterableDup.injectInto(0, AddFunction.INTEGER));
    }

    @Test
    default void RichIterable_injectInto_primitive()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertEquals(11, iterable.injectInto(1, AddFunction.INTEGER_TO_INT));
        assertEquals(10, iterable.injectInto(0, AddFunction.INTEGER_TO_INT));

        assertEquals(11L, iterable.injectInto(1, AddFunction.INTEGER_TO_LONG));
        assertEquals(10L, iterable.injectInto(0, AddFunction.INTEGER_TO_LONG));

        assertEquals(11.0d, iterable.injectInto(1, AddFunction.INTEGER_TO_DOUBLE), 0.001);
        assertEquals(10.0d, iterable.injectInto(0, AddFunction.INTEGER_TO_DOUBLE), 0.001);

        assertEquals(11.0f, iterable.injectInto(1, AddFunction.INTEGER_TO_FLOAT), 0.001f);
        assertEquals(10.0f, iterable.injectInto(0, AddFunction.INTEGER_TO_FLOAT), 0.001f);

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        assertEquals(31, iterableDup.injectIntoInt(1, AddFunction.INTEGER_TO_INT));
        assertEquals(30, iterableDup.injectIntoInt(0, AddFunction.INTEGER_TO_INT));

        assertEquals(31L, iterableDup.injectIntoLong(1, AddFunction.INTEGER_TO_LONG));
        assertEquals(30L, iterableDup.injectIntoLong(0, AddFunction.INTEGER_TO_LONG));

        assertEquals(31.0d, iterableDup.injectIntoDouble(1, AddFunction.INTEGER_TO_DOUBLE), 0.001);
        assertEquals(30.0d, iterableDup.injectIntoDouble(0, AddFunction.INTEGER_TO_DOUBLE), 0.001);

        assertEquals(31.0f, iterableDup.injectIntoFloat(1, AddFunction.INTEGER_TO_FLOAT), 0.001f);
        assertEquals(30.0f, iterableDup.injectIntoFloat(0, AddFunction.INTEGER_TO_FLOAT), 0.001f);
    }

    @Test
    default void RichIterable_fused_collectMakeString()
    {
        RichIterable<Integer> iterable = this.newWith(0, 1, 8);

        assertEquals(
                iterable.asLazy().collect(Integer::toUnsignedString).makeString("[", ",", "]"),
                iterable.makeString(Integer::toUnsignedString, "[", ",", "]"));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(0, 0, 1, 1, 8, 8);

        assertEquals(
                iterableDup.asLazy().collect(Integer::toUnsignedString).makeString("[", ",", "]"),
                iterableDup.makeString(Integer::toUnsignedString, "[", ",", "]"));
    }

    @Test
    default void RichIterable_makeString_appendString()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertEquals("4, 3, 2, 1", iterable.makeString());
        assertEquals("4/3/2/1", iterable.makeString("/"));
        assertEquals("[4/3/2/1]", iterable.makeString("[", "/", "]"));

        StringBuilder stringBuilder1 = new StringBuilder();
        iterable.appendString(stringBuilder1);
        assertEquals("4, 3, 2, 1", stringBuilder1.toString());

        StringBuilder stringBuilder2 = new StringBuilder();
        iterable.appendString(stringBuilder2, "/");
        assertEquals("4/3/2/1", stringBuilder2.toString());

        StringBuilder stringBuilder3 = new StringBuilder();
        iterable.appendString(stringBuilder3, "[", "/", "]");
        assertEquals("[4/3/2/1]", stringBuilder3.toString());

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        assertEquals(
                "4, 4, 4, 4, 3, 3, 3, 2, 2, 1",
                iterable2.makeString());

        assertEquals(
                iterable2.makeString(),
                iterable2.reduceInPlace(Collectors2.makeString()));

        assertEquals(
                "4/4/4/4/3/3/3/2/2/1",
                iterable2.makeString("/"));

        assertEquals(
                iterable2.makeString("/"),
                iterable2.reduceInPlace(Collectors2.makeString("/")));

        assertEquals(
                "[4/4/4/4/3/3/3/2/2/1]",
                iterable2.makeString("[", "/", "]"));

        assertEquals(
                iterable2.makeString("[", "/", "]"),
                iterable2.reduceInPlace(Collectors2.makeString("[", "/", "]")));

        StringBuilder builder1 = new StringBuilder();
        iterable2.appendString(builder1);
        assertEquals(
                "4, 4, 4, 4, 3, 3, 3, 2, 2, 1",
                builder1.toString());

        StringBuilder builder2 = new StringBuilder();
        iterable2.appendString(builder2, "/");
        assertEquals(
                "4/4/4/4/3/3/3/2/2/1",
                builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        iterable2.appendString(builder3, "[", "/", "]");
        assertEquals(
                "[4/4/4/4/3/3/3/2/2/1]",
                builder3.toString());
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        assertEquals("[3, 2, 1]", iterable.toString());
        assertEquals("[3, 2, 1]", iterable.asLazy().toString());

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        assertEquals(
                "[4, 4, 4, 4, 3, 3, 3, 2, 2, 1]",
                iterable2.toString());
        assertEquals(
                "[4, 4, 4, 4, 3, 3, 3, 2, 2, 1]",
                iterable2.asLazy().toString());
    }

    @Test
    default void RichIterable_toList()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        assertIterablesEqual(
                Lists.immutable.with(4, 3, 2, 1),
                iterable.toList());

        MutableList<Integer> target = Lists.mutable.empty();
        iterable.each(target::add);
        assertIterablesEqual(
                target,
                iterable.toList());

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> duplicateIterable = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        assertIterablesEqual(
                Lists.immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1),
                duplicateIterable.toList());

        MutableList<Integer> duplicateTarget = Lists.mutable.empty();
        duplicateIterable.each(duplicateTarget::add);
        assertIterablesEqual(
                duplicateTarget,
                duplicateIterable.toList());
    }

    @Test
    default void RichIterable_into()
    {
        assertIterablesEqual(
                Lists.immutable.with(0, 4, 3, 2, 1),
                this.newWith(4, 3, 2, 1).into(Lists.mutable.with(0)));

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertIterablesEqual(
                Lists.immutable.with(0, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1),
                this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).into(Lists.mutable.with(0)));
    }

    @Test
    default void RichIterable_toSortedList()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertIterablesEqual(
                Lists.immutable.with(1, 2, 3, 4),
                iterable.toSortedList());

        assertIterablesEqual(
                Lists.immutable.with(4, 3, 2, 1),
                iterable.toSortedList(Comparators.reverseNaturalOrder()));

        assertIterablesEqual(
                Lists.immutable.with(1, 2, 3, 4),
                iterable.toSortedListBy(Functions.identity()));

        assertIterablesEqual(
                Lists.immutable.with(4, 3, 2, 1),
                iterable.toSortedListBy(each -> each * -1));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        assertIterablesEqual(
                Lists.immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4),
                iterable2.toSortedList());

        assertIterablesEqual(
                Lists.immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1),
                iterable2.toSortedList(Comparators.reverseNaturalOrder()));

        assertIterablesEqual(
                Lists.immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4),
                iterable2.toSortedListBy(Functions.identity()));

        assertIterablesEqual(
                Lists.immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1),
                iterable2.toSortedListBy(each -> each * -1));
    }

    @Test
    default void RichIterable_toSet()
    {
        assertIterablesEqual(
                Sets.immutable.with(4, 3, 2, 1),
                this.newWith(4, 3, 2, 1).toSet());

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertIterablesEqual(
                Sets.immutable.with(4, 3, 2, 1),
                this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).toSet());
    }

    @Test
    default void RichIterable_toSortedSet()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertIterablesEqual(
                SortedSets.immutable.with(1, 2, 3, 4),
                iterable.toSortedSet());

        assertIterablesEqual(
                SortedSets.immutable.with(Comparators.reverseNaturalOrder(), 4, 3, 2, 1),
                iterable.toSortedSet(Comparators.reverseNaturalOrder()));

        assertIterablesEqual(
                SortedSets.immutable.with(Comparators.byFunction(Functions.identity()), 1, 2, 3, 4),
                iterable.toSortedSetBy(Functions.identity()));

        assertIterablesEqual(
                SortedSets.immutable.with(Comparators.byFunction((Integer each) -> each * -1), 4, 3, 2, 1),
                iterable.toSortedSetBy(each -> each * -1));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        assertIterablesEqual(
                SortedSets.immutable.with(1, 2, 3, 4),
                iterable2.toSortedSet());

        assertIterablesEqual(
                SortedSets.immutable.with(Comparators.reverseNaturalOrder(), 4, 3, 2, 1),
                iterable2.toSortedSet(Comparators.reverseNaturalOrder()));

        assertIterablesEqual(
                SortedSets.immutable.with(Comparators.byFunction(Functions.identity()), 1, 2, 3, 4),
                iterable2.toSortedSetBy(Functions.identity()));

        assertIterablesEqual(
                SortedSets.immutable.with(Comparators.byFunction((Integer each) -> each * -1), 4, 3, 2, 1),
                iterable2.toSortedSetBy(each -> each * -1));
    }

    @Test
    default void RichIterable_toBag()
    {
        assertIterablesEqual(
                Bags.immutable.with(4, 3, 2, 1),
                this.newWith(4, 3, 2, 1).toBag());

        if (!this.allowsDuplicates())
        {
            return;
        }

        assertIterablesEqual(
                Bags.immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1),
                this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).toBag());
    }

    @Test
    default void RichIterable_toSortedBag()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertIterablesEqual(
                TreeBag.newBagWith(1, 2, 3, 4),
                iterable.toSortedBag());

        assertIterablesEqual(
                TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1),
                iterable.toSortedBag(Comparators.reverseNaturalOrder()));

        assertIterablesEqual(
                TreeBag.newBagWith(Comparators.byFunction(Functions.identity()), 1, 2, 3, 4),
                iterable.toSortedBagBy(Functions.identity()));

        assertIterablesEqual(
                TreeBag.newBagWith(Comparators.byFunction((Integer each) -> each * -1), 4, 3, 2, 1),
                iterable.toSortedBagBy(each -> each * -1));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        assertIterablesEqual(
                TreeBag.newBagWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4),
                iterable2.toSortedBag());

        assertIterablesEqual(
                TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1),
                iterable2.toSortedBag(Comparators.reverseNaturalOrder()));

        assertIterablesEqual(
                TreeBag.newBagWith(Comparators.byFunction(Functions.identity()), 1, 2, 2, 3, 3, 3, 4, 4, 4, 4),
                iterable2.toSortedBagBy(Functions.identity()));

        assertIterablesEqual(
                TreeBag.newBagWith(Comparators.byFunction((Integer each) -> each * -1), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1),
                iterable2.toSortedBagBy(each -> each * -1));
    }

    @Test
    default void RichIterable_toMap()
    {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        assertIterablesEqual(
                UnifiedMap.newMapWith(
                        Tuples.pair("13", 3),
                        Tuples.pair("12", 2),
                        Tuples.pair("11", 1),
                        Tuples.pair("3", 3),
                        Tuples.pair("2", 2),
                        Tuples.pair("1", 1)),
                iterable.toMap(Object::toString, each -> each % 10));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterable2 = this.newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);

        assertIterablesEqual(
                UnifiedMap.newMapWith(
                        Tuples.pair("13", 3),
                        Tuples.pair("12", 2),
                        Tuples.pair("11", 1),
                        Tuples.pair("3", 3),
                        Tuples.pair("2", 2),
                        Tuples.pair("1", 1)),
                iterable2.toMap(Object::toString, each -> each % 10));
    }

    @Test
    default void RichIterable_toMapTarget()
    {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        Map<String, Integer> jdkMap = new HashMap<>();
        jdkMap.put("13", 3);
        jdkMap.put("12", 2);
        jdkMap.put("11", 1);
        jdkMap.put("3", 3);
        jdkMap.put("2", 2);
        jdkMap.put("1", 1);

        assertIterablesEqual(
                jdkMap,
                iterable.toMap(Object::toString, each -> each % 10, new HashMap<>()));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);

        assertIterablesEqual(
                jdkMap,
                iterableDup.toMap(Object::toString, each -> each % 10, new HashMap<>()));
    }

    @Test
    default void RichIterable_toSortedMap()
    {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        Pair<String, Integer>[] pairs = new Pair[]
                {
                        Tuples.pair("13", 3),
                        Tuples.pair("12", 2),
                        Tuples.pair("11", 1),
                        Tuples.pair("3", 3),
                        Tuples.pair("2", 2),
                        Tuples.pair("1", 1),
                };
        assertIterablesEqual(
                TreeSortedMap.newMapWith(pairs),
                iterable.toSortedMap(Object::toString, each -> each % 10));

        assertIterablesEqual(
                TreeSortedMap.newMapWith(
                        Comparators.reverseNaturalOrder(),
                        pairs),
                iterable.toSortedMap(Comparators.reverseNaturalOrder(), Object::toString, each -> each % 10));

        assertIterablesEqual(
                TreeSortedMap.newMapWith(
                        Comparators.naturalOrder(),
                        pairs),
                iterable.toSortedMapBy(Functions.getStringPassThru(), Object::toString, each -> each % 10));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> duplicateIterable = this.newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);

        Pair<String, Integer>[] duplicatePairs = new Pair[]
                {
                        Tuples.pair("13", 3),
                        Tuples.pair("12", 2),
                        Tuples.pair("11", 1),
                        Tuples.pair("3", 3),
                        Tuples.pair("2", 2),
                        Tuples.pair("1", 1),
                };
        assertIterablesEqual(
                TreeSortedMap.newMapWith(duplicatePairs),
                duplicateIterable.toSortedMap(Object::toString, each -> each % 10));

        assertIterablesEqual(
                TreeSortedMap.newMapWith(
                        Comparators.reverseNaturalOrder(),
                        duplicatePairs),
                duplicateIterable.toSortedMap(Comparators.reverseNaturalOrder(), Object::toString, each -> each % 10));

        assertIterablesEqual(
                TreeSortedMap.newMapWith(
                        Comparators.naturalOrder(),
                        duplicatePairs),
                duplicateIterable.toSortedMapBy(Functions.getStringPassThru(), Object::toString, each -> each % 10));
    }

    @Test
    default void RichIterable_toArray()
    {
        Object[] array = this.newWith(3, 2, 1).toArray();
        assertIterablesEqual(Bags.immutable.with(3, 2, 1), HashBag.newBagWith(array));

        if (!this.allowsDuplicates())
        {
            return;
        }

        Object[] array2 = this.newWith(3, 3, 3, 2, 2, 1).toArray();
        assertIterablesEqual(Bags.immutable.with(3, 3, 3, 2, 2, 1), HashBag.newBagWith(array2));
    }

    @Test
    default void RichIterable_groupByAndCollect()
    {
        RichIterable<Integer> iterable = this.newWith(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
        Function<Integer, Boolean> groupByFunction = integer -> IntegerPredicates.isOdd().accept(integer);
        Function<Integer, Integer> collectFunction = integer -> integer + 2;

        FastList<Integer> expectedOddNumberList = FastList.newListWith(3, 5, 7, 9, 11);
        FastList<Integer> expectedEvenNumberList = FastList.newListWith(4, 6, 8, 10, 12);

        MutableListMultimap<Boolean, Integer> targetResult = iterable.groupByAndCollect(groupByFunction, collectFunction, Multimaps.mutable.list.empty());

        assertTrue(expectedOddNumberList.containsAll(targetResult.get(Boolean.TRUE)));
        assertTrue(expectedEvenNumberList.containsAll(targetResult.get(Boolean.FALSE)));

        if (!this.allowsDuplicates())
        {
            return;
        }

        RichIterable<Integer> iterableDup = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        FastList<Integer> expectedOddNumberListDup = FastList.newListWith(3, 5, 5, 5, 5, 5, 5, 5);
        FastList<Integer> expectedEvenNumberListDup = FastList.newListWith(4, 4, 6, 6, 6, 6);

        MutableListMultimap<Boolean, Integer> targetResultDup = iterableDup.groupByAndCollect(groupByFunction, collectFunction, Multimaps.mutable.list.empty());

        assertTrue(expectedOddNumberListDup.containsAll(targetResultDup.get(Boolean.TRUE)));
        assertTrue(expectedEvenNumberListDup.containsAll(targetResultDup.get(Boolean.FALSE)));
    }

    class Holder<T extends Comparable<? super T>> implements Comparable<Holder<T>>
    {
        private final T field;

        Holder(T field)
        {
            this.field = field;
        }

        @Override
        public int compareTo(Holder<T> other)
        {
            return this.field.compareTo(other.field);
        }
    }
}
