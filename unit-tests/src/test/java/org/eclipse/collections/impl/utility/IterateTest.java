/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.Function3;
import org.eclipse.collections.api.block.procedure.Procedure2;
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
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.BagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.multimap.sortedbag.MutableSortedBagMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.ObjectIntProcedures;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.factory.StringFunctions;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.MaxSizeFunction;
import org.eclipse.collections.impl.block.function.MinSizeFunction;
import org.eclipse.collections.impl.block.predicate.PairPredicate;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.math.Sum;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.multimap.bag.sorted.mutable.TreeBagMultimap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.multimap.set.sorted.TreeSortedSetMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iBag;
import static org.eclipse.collections.impl.factory.Iterables.iList;
import static org.eclipse.collections.impl.factory.Iterables.iSet;
import static org.eclipse.collections.impl.factory.Iterables.mList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IterateTest
{
    private MutableList<Iterable<Integer>> iterables;

    @BeforeEach
    public void setUp()
    {
        this.iterables = Lists.mutable.of();
        this.iterables.add(Interval.oneTo(5).toList());
        this.iterables.add(Interval.oneTo(5).toSet());
        this.iterables.add(Interval.oneTo(5).toBag());
        this.iterables.add(Interval.oneTo(5).toSortedSet());
        this.iterables.add(Interval.oneTo(5).toSortedMap(i -> i, i -> i));
        this.iterables.add(Interval.oneTo(5).toMap(i -> i, i -> i));
        this.iterables.add(Interval.oneTo(5).addAllTo(new ArrayList<>(5)));
        this.iterables.add(Interval.oneTo(5).addAllTo(new LinkedList<>()));
        this.iterables.add(Collections.unmodifiableList(new ArrayList<>(Interval.oneTo(5))));
        this.iterables.add(Collections.unmodifiableCollection(new ArrayList<>(Interval.oneTo(5))));
        this.iterables.add(Interval.oneTo(5));
        this.iterables.add(Interval.oneTo(5).asLazy());
        this.iterables.add(new IterableAdapter<>(Interval.oneTo(5)));
    }

    @Test
    public void addAllTo()
    {
        Verify.assertContainsAll(Iterate.addAllTo(FastList.newListWith(1, 2, 3), FastList.newList()), 1, 2, 3);
    }

    @Test
    public void removeAllFrom()
    {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(1, 3, 5);
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        assertEquals(Lists.immutable.with(2, 4, 6), sourceFastList);
    }

    @Test
    public void removeAllFromEmptyTest()
    {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        Verify.assertEmpty(sourceFastList);
    }

    @Test
    public void removeAllFromEmptyListTest()
    {
        MutableList<Integer> sourceFastList = FastList.newList();
        MutableList<Integer> removeFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        Verify.assertEmpty(sourceFastList);
    }

    @Test
    public void removeAllFromNoElementRemovedTest()
    {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(7, 8, 9);
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        assertEquals(Lists.immutable.with(1, 2, 3, 4, 5, 6), sourceFastList);
    }

    @Test
    public void removeAllFromEmptyTargetListTest()
    {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newList();
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        assertEquals(Lists.immutable.with(1, 2, 3, 4, 5, 6), sourceFastList);
    }

    @Test
    public void removeAllIterableFrom()
    {
        MutableList<Integer> sourceFastList1 = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList1 = FastList.newListWith(1, 3, 5);
        assertTrue(Iterate.removeAllIterable(removeFastList1, sourceFastList1));
        assertEquals(Lists.immutable.with(2, 4, 6), sourceFastList1);
        assertFalse(Iterate.removeAllIterable(FastList.newListWith(0), sourceFastList1));
        assertEquals(Lists.immutable.with(2, 4, 6), sourceFastList1);

        MutableList<Integer> sourceFastList2 = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableSetMultimap<Integer, Integer> multimap = UnifiedSetMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(1, 3), Tuples.pair(1, 5));
        assertTrue(Iterate.removeAllIterable(multimap.valuesView(), sourceFastList2));
        assertEquals(Lists.immutable.with(2, 4, 6), sourceFastList2);
        assertFalse(Iterate.removeAllIterable(UnifiedSetMultimap.newMultimap(Tuples.pair(0, 0)).valuesView(), sourceFastList2));
        assertEquals(Lists.immutable.with(2, 4, 6), sourceFastList2);
    }

    @Test
    public void removeAllIterableFromEmptyTest()
    {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        assertTrue(Iterate.removeAllIterable(removeFastList, sourceFastList));
        Verify.assertEmpty(sourceFastList);
    }

    @Test
    public void removeAllIterableFromEmptyListTest()
    {
        MutableList<Integer> sourceFastList = FastList.newList();
        MutableList<Integer> removeFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        assertFalse(Iterate.removeAllIterable(removeFastList, sourceFastList));
        Verify.assertEmpty(sourceFastList);
    }

    @Test
    public void removeAllIterableFromNoElementRemovedTest()
    {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(7, 8, 9);
        assertFalse(Iterate.removeAllIterable(removeFastList, sourceFastList));
        assertEquals(Lists.immutable.with(1, 2, 3, 4, 5, 6), sourceFastList);
    }

    @Test
    public void removeAllIterableFromEmptyTargetListTest()
    {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newList();
        assertFalse(Iterate.removeAllIterable(removeFastList, sourceFastList));
        assertEquals(Lists.immutable.with(1, 2, 3, 4, 5, 6), sourceFastList);
    }

    @Test
    public void sizeOf()
    {
        assertEquals(5, Iterate.sizeOf(Interval.oneTo(5)));
        assertEquals(5, Iterate.sizeOf(Interval.oneTo(5).toList()));
        assertEquals(5, Iterate.sizeOf(Interval.oneTo(5).asLazy()));
        assertEquals(3, Iterate.sizeOf(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3)));
        assertEquals(5, Iterate.sizeOf(new IterableAdapter<>(Interval.oneTo(5))));
    }

    @Test
    public void toArray()
    {
        Object[] expected = {1, 2, 3, 4, 5};
        assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5)));
        assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).toList()));
        assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).asLazy()));
        assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).toSortedMap(a -> a, a -> a)));
        assertArrayEquals(expected, Iterate.toArray(new IterableAdapter<>(Interval.oneTo(5))));
    }

    @Test
    public void toArray_NullParameter()
    {
        assertThrows(NullPointerException.class, () -> Iterate.toArray(null));
    }

    @Test
    public void toArray_with_array()
    {
        Object[] expected = {1, 2, 3, 4, 5};
        assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5), new Object[5]));
        assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).toList(), new Object[5]));
        assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).asLazy(), new Object[5]));
        assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).toSortedMap(a -> a, a -> a), new Object[5]));
        assertArrayEquals(expected, Iterate.toArray(new IterableAdapter<>(Interval.oneTo(5)), new Object[5]));
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7}, Iterate.toArray(new IterableAdapter<>(Interval.oneTo(7)), new Object[5]));
    }

    @Test
    public void fromToDoit()
    {
        MutableList<Integer> list = Lists.mutable.of();
        Interval.fromTo(6, 10).each(list::add);
        Verify.assertContainsAll(list, 6, 10);
    }

    @Test
    public void reduceInPlaceCollector()
    {
        this.iterables.each(each -> assertEquals(
                Iterate.toSortedList(each),
                Iterate.reduceInPlace(each, Collectors2.toSortedList())));
        this.iterables.each(each -> assertEquals(
                Iterate.sumByInt(each, value -> value % 2, value -> value),
                Iterate.reduceInPlace(each, Collectors2.sumByInt(value -> value % 2, value -> value))));
        this.iterables.each(each -> assertEquals(
                Iterate.groupBy(each, value -> value % 2, Multimaps.mutable.bag.empty()),
                Iterate.reduceInPlace(each, Collectors2.toBagMultimap(value -> value % 2))));
    }

    @Test
    public void reduceInPlace()
    {
        this.iterables.each(each -> assertEquals(
                Iterate.toSortedList(each),
                Iterate.reduceInPlace(each, FastList::new, FastList::add).sortThis()));
    }

    @Test
    public void injectInto()
    {
        this.iterables.each(each -> assertEquals(Integer.valueOf(15), Iterate.injectInto(0, each, AddFunction.INTEGER)));
    }

    @Test
    public void injectIntoInt()
    {
        this.iterables.each(each -> assertEquals(15, Iterate.injectInto(0, each, AddFunction.INTEGER_TO_INT)));
        assertThrows(IllegalArgumentException.class, () -> Iterate.injectInto(0, null, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLong()
    {
        this.iterables.each(each -> assertEquals(15L, Iterate.injectInto(0L, each, AddFunction.INTEGER_TO_LONG)));
        assertThrows(IllegalArgumentException.class, () -> Iterate.injectInto(0L, null, AddFunction.INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble()
    {
        this.iterables.each(each -> assertEquals(15.0d, Iterate.injectInto(0.0d, each, AddFunction.INTEGER_TO_DOUBLE), 0.001));
        assertThrows(IllegalArgumentException.class, () -> Iterate.injectInto(0.0d, null, AddFunction.INTEGER_TO_DOUBLE));
    }

    @Test
    public void injectIntoFloat()
    {
        this.iterables.each(each -> assertEquals(15.0d, Iterate.injectInto(0.0f, each, AddFunction.INTEGER_TO_FLOAT), 0.001));
        assertThrows(IllegalArgumentException.class, () -> Iterate.injectInto(0.0f, null, AddFunction.INTEGER_TO_FLOAT));
    }

    @Test
    public void injectInto2()
    {
        assertEquals(Double.valueOf(7.0), Iterate.injectInto(1.0, iList(1.0, 2.0, 3.0), AddFunction.DOUBLE));
    }

    @Test
    public void injectIntoString()
    {
        assertEquals("0123", Iterate.injectInto("0", iList("1", "2", "3"), AddFunction.STRING));
    }

    @Test
    public void injectIntoMaxString()
    {
        assertEquals(Integer.valueOf(3), Iterate.injectInto(Integer.MIN_VALUE, iList("1", "12", "123"), MaxSizeFunction.STRING));
    }

    @Test
    public void injectIntoMinString()
    {
        assertEquals(Integer.valueOf(1), Iterate.injectInto(Integer.MAX_VALUE, iList("1", "12", "123"), MinSizeFunction.STRING));
    }

    @Test
    public void flatCollectFromAttributes()
    {
        MutableList<ListContainer<String>> list = mList(
                new ListContainer<>(Lists.mutable.of("One", "Two")),
                new ListContainer<>(Lists.mutable.of("Two-and-a-half", "Three", "Four")),
                new ListContainer<>(Lists.mutable.of()),
                new ListContainer<>(Lists.mutable.of("Five")));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(list, ListContainer.getListFunction()));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(Collections.synchronizedList(list), ListContainer.getListFunction()));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(Collections.synchronizedCollection(list), ListContainer.getListFunction()));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(LazyIterate.adapt(list), ListContainer.getListFunction()));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(new ArrayList<>(list), ListContainer.getListFunction()));
        assertThrows(IllegalArgumentException.class, () -> Iterate.flatCollect(null, null));
    }

    @Test
    public void flatCollectFromAttributesWithTarget()
    {
        MutableList<ListContainer<String>> list = Lists.fixedSize.of(
                new ListContainer<>(Lists.mutable.of("One", "Two")),
                new ListContainer<>(Lists.mutable.of("Two-and-a-half", "Three", "Four")),
                new ListContainer<>(Lists.mutable.of()),
                new ListContainer<>(Lists.mutable.of("Five")));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(list, ListContainer.getListFunction(), FastList.newList()));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(Collections.synchronizedList(list), ListContainer.getListFunction(), FastList.newList()));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(Collections.synchronizedCollection(list), ListContainer.getListFunction(), FastList.newList()));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(LazyIterate.adapt(list), ListContainer.getListFunction(), FastList.newList()));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(new ArrayList<>(list), ListContainer.getListFunction(), FastList.newList()));
        assertEquals(
                iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"),
                Iterate.flatCollect(new IterableAdapter<>(new ArrayList<>(list)), ListContainer.getListFunction(), FastList.newList()));
    }

    @Test
    public void flatCollectFromAttributesWithTarget_NullParameter()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.flatCollect(null, null, UnifiedSet.newSet()));
    }

    @Test
    public void flatCollectFromAttributesUsingStringFunction()
    {
        MutableList<ListContainer<String>> list = Lists.mutable.of(
                new ListContainer<>(Lists.mutable.of("One", "Two")),
                new ListContainer<>(Lists.mutable.of("Two-and-a-half", "Three", "Four")),
                new ListContainer<>(Lists.mutable.of()),
                new ListContainer<>(Lists.mutable.of("Five")));
        Function<ListContainer<String>, List<String>> function = ListContainer::getList;
        Collection<String> result = Iterate.flatCollect(list, function);
        FastList<String> result2 = Iterate.flatCollect(list, function, FastList.newList());
        assertEquals(iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), result);
        assertEquals(iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), result2);
    }

    @Test
    public void flatCollectOneLevel()
    {
        MutableList<MutableList<String>> list = Lists.mutable.of(
                Lists.mutable.of("One", "Two"),
                Lists.mutable.of("Two-and-a-half", "Three", "Four"),
                Lists.mutable.of(),
                Lists.mutable.of("Five"));
        Collection<String> result = Iterate.flatten(list);
        assertEquals(iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), result);
    }

    @Test
    public void flatCollectOneLevel_target()
    {
        MutableList<MutableList<String>> list = Lists.mutable.of(
                Lists.mutable.of("One", "Two"),
                Lists.mutable.of("Two-and-a-half", "Three", "Four"),
                Lists.mutable.of(),
                Lists.mutable.of("Five"));
        MutableList<String> target = Lists.mutable.of();
        Collection<String> result = Iterate.flatten(list, target);
        assertSame(result, target);
        assertEquals(iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), result);
    }

    @Test
    public void groupBy()
    {
        FastList<String> source = FastList.newListWith("Ted", "Sally", "Mary", "Bob", "Sara");
        Multimap<Character, String> result1 = Iterate.groupBy(source, StringFunctions.firstLetter());
        Multimap<Character, String> result2 = Iterate.groupBy(Collections.synchronizedList(source), StringFunctions.firstLetter());
        Multimap<Character, String> result3 = Iterate.groupBy(Collections.synchronizedCollection(source), StringFunctions.firstLetter());
        Multimap<Character, String> result4 = Iterate.groupBy(LazyIterate.adapt(source), StringFunctions.firstLetter());
        Multimap<Character, String> result5 = Iterate.groupBy(new ArrayList<>(source), StringFunctions.firstLetter());
        MutableMultimap<Character, String> expected = FastListMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.put('S', "Sally");
        expected.put('M', "Mary");
        expected.put('B', "Bob");
        expected.put('S', "Sara");
        assertEquals(expected, result1);
        assertEquals(expected, result2);
        assertEquals(expected, result3);
        assertEquals(expected, result4);
        assertEquals(expected, result5);
        assertThrows(IllegalArgumentException.class, () -> Iterate.groupBy(null, null));
    }

    @Test
    public void groupByEach()
    {
        MutableList<String> source = FastList.newListWith("Ted", "Sally", "Sally", "Mary", "Bob", "Sara");
        Function<String, Set<Character>> uppercaseSetFunction = new WordToItsLetters();
        Multimap<Character, String> result1 = Iterate.groupByEach(source, uppercaseSetFunction);
        Multimap<Character, String> result2 = Iterate.groupByEach(Collections.synchronizedList(source), uppercaseSetFunction);
        Multimap<Character, String> result3 = Iterate.groupByEach(Collections.synchronizedCollection(source), uppercaseSetFunction);
        Multimap<Character, String> result4 = Iterate.groupByEach(LazyIterate.adapt(source), uppercaseSetFunction);
        Multimap<Character, String> result5 = Iterate.groupByEach(new ArrayList<>(source), uppercaseSetFunction);
        MutableMultimap<Character, String> expected = FastListMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.putAll('E', FastList.newListWith("Ted"));
        expected.put('D', "Ted");
        expected.putAll('S', FastList.newListWith("Sally", "Sally", "Sara"));
        expected.putAll('A', FastList.newListWith("Sally", "Sally", "Mary", "Sara"));
        expected.putAll('L', FastList.newListWith("Sally", "Sally"));
        expected.putAll('Y', FastList.newListWith("Sally", "Sally", "Mary"));
        expected.put('M', "Mary");
        expected.putAll('R', FastList.newListWith("Mary", "Sara"));
        expected.put('B', "Bob");
        expected.put('O', "Bob");
        assertEquals(expected, result1);
        assertEquals(expected, result2);
        assertEquals(expected, result3);
        assertEquals(expected, result4);
        assertEquals(expected, result5);
        assertThrows(IllegalArgumentException.class, () -> Iterate.groupByEach(null, null));
    }

    @Test
    public void groupByWithTarget()
    {
        FastList<String> source = FastList.newListWith("Ted", "Sally", "Mary", "Bob", "Sara");
        Multimap<Character, String> result1 = Iterate.groupBy(source, StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        Multimap<Character, String> result2 = Iterate.groupBy(Collections.synchronizedList(source), StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        Multimap<Character, String> result3 = Iterate.groupBy(Collections.synchronizedCollection(source), StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        Multimap<Character, String> result4 = Iterate.groupBy(LazyIterate.adapt(source), StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        Multimap<Character, String> result5 = Iterate.groupBy(new ArrayList<>(source), StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        MutableMultimap<Character, String> expected = FastListMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.put('S', "Sally");
        expected.put('M', "Mary");
        expected.put('B', "Bob");
        expected.put('S', "Sara");
        assertEquals(expected, result1);
        assertEquals(expected, result2);
        assertEquals(expected, result3);
        assertEquals(expected, result4);
        assertEquals(expected, result5);
        assertThrows(IllegalArgumentException.class, () -> Iterate.groupBy(null, null, null));
    }

    @Test
    public void groupByEachWithTarget()
    {
        MutableList<String> source = FastList.newListWith("Ted", "Sally", "Sally", "Mary", "Bob", "Sara");
        Function<String, Set<Character>> uppercaseSetFunction = StringIterate::asUppercaseSet;
        Multimap<Character, String> result1 = Iterate.groupByEach(source, uppercaseSetFunction, FastListMultimap.newMultimap());
        Multimap<Character, String> result2 = Iterate.groupByEach(Collections.synchronizedList(source), uppercaseSetFunction, FastListMultimap.newMultimap());
        Multimap<Character, String> result3 = Iterate.groupByEach(Collections.synchronizedCollection(source), uppercaseSetFunction, FastListMultimap.newMultimap());
        Multimap<Character, String> result4 = Iterate.groupByEach(LazyIterate.adapt(source), uppercaseSetFunction, FastListMultimap.newMultimap());
        Multimap<Character, String> result5 = Iterate.groupByEach(new ArrayList<>(source), uppercaseSetFunction, FastListMultimap.newMultimap());
        MutableMultimap<Character, String> expected = FastListMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.putAll('E', FastList.newListWith("Ted"));
        expected.put('D', "Ted");
        expected.putAll('S', FastList.newListWith("Sally", "Sally", "Sara"));
        expected.putAll('A', FastList.newListWith("Sally", "Sally", "Mary", "Sara"));
        expected.putAll('L', FastList.newListWith("Sally", "Sally"));
        expected.putAll('Y', FastList.newListWith("Sally", "Sally"));
        expected.put('M', "Mary");
        expected.putAll('R', FastList.newListWith("Mary", "Sara"));
        expected.put('Y', "Mary");
        expected.put('B', "Bob");
        expected.put('O', "Bob");
        assertEquals(expected, result1);
        assertEquals(expected, result2);
        assertEquals(expected, result3);
        assertEquals(expected, result4);
        assertEquals(expected, result5);
        assertThrows(IllegalArgumentException.class, () -> Iterate.groupByEach(null, null, null));
    }

    @Test
    public void groupByAndCollect()
    {
        MutableList<Integer> list = Lists.mutable.of(1, 2, 2, 3, 3, 3);

        Pair<String, String>[] expectedPairs = new Pair[]{
                Tuples.pair("Key1", "1"),
                Tuples.pair("Key2", "2"),
                Tuples.pair("Key2", "2"),
                Tuples.pair("Key3", "3"),
                Tuples.pair("Key3", "3"),
                Tuples.pair("Key3", "3"),
        };
        Function<Integer, String> keyFunction = each -> "Key" + each;
        Function<Integer, String> valueFunction = String::valueOf;
        MutableListMultimap<String, String> actualMultimap1 = Iterate.groupByAndCollect(list, keyFunction, valueFunction, Multimaps.mutable.list.empty());
        Verify.assertListMultimapsEqual(FastListMultimap.newMultimap(expectedPairs), actualMultimap1);

        MutableSetMultimap<String, String> actualMultimap2 = Iterate.groupByAndCollect(list, keyFunction, valueFunction, Multimaps.mutable.set.empty());
        Verify.assertSetMultimapsEqual(UnifiedSetMultimap.newMultimap(expectedPairs), actualMultimap2);

        MutableBagMultimap<String, String> actualMultimap3 = Iterate.groupByAndCollect(list, keyFunction, valueFunction, Multimaps.mutable.bag.empty());
        Verify.assertBagMultimapsEqual(HashBagMultimap.newMultimap(expectedPairs), actualMultimap3);

        MutableSortedSetMultimap<String, String> expectedMultimap4 = TreeSortedSetMultimap.newMultimap(Comparators.naturalOrder());
        expectedMultimap4.putAllPairs(expectedPairs);
        MutableSortedSetMultimap<String, String> actualMultimap4 = Iterate.groupByAndCollect(list, keyFunction, valueFunction, Multimaps.mutable.sortedSet.with(Comparators.naturalOrder()));
        Verify.assertSortedSetMultimapsEqual(expectedMultimap4, actualMultimap4);
        assertSame(expectedMultimap4.comparator(), actualMultimap4.comparator());

        MutableSortedSetMultimap<String, String> expectedMultimap5 = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap5.putAllPairs(expectedPairs);
        MutableSortedSetMultimap<String, String> actualMultimap5 = Iterate.groupByAndCollect(list, keyFunction, valueFunction, Multimaps.mutable.sortedSet.with(Comparators.reverseNaturalOrder()));
        Verify.assertSortedSetMultimapsEqual(expectedMultimap5, actualMultimap5);
        assertSame(expectedMultimap5.comparator(), actualMultimap5.comparator());

        MutableSortedBagMultimap<String, String> expectedMultimap6 = TreeBagMultimap.newMultimap(Comparators.naturalOrder());
        expectedMultimap6.putAllPairs(expectedPairs);
        MutableSortedBagMultimap<String, String> actualMultimap6 = Iterate.groupByAndCollect(list, keyFunction, valueFunction, TreeBagMultimap.newMultimap(Comparators.naturalOrder()));
        Verify.assertSortedBagMultimapsEqual(expectedMultimap6, actualMultimap6);
        assertSame(expectedMultimap6.comparator(), actualMultimap6.comparator());

        MutableSortedBagMultimap<String, String> expectedMultimap7 = TreeBagMultimap.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap7.putAllPairs(expectedPairs);
        MutableSortedBagMultimap<String, String> actualMultimap7 = Iterate.groupByAndCollect(list, keyFunction, valueFunction, TreeBagMultimap.newMultimap(Comparators.reverseNaturalOrder()));
        Verify.assertSortedBagMultimapsEqual(expectedMultimap7, actualMultimap7);
        assertSame(expectedMultimap7.comparator(), actualMultimap7.comparator());
    }

    @Test
    public void toList()
    {
        assertEquals(FastList.newListWith(1, 2, 3), FastList.newList(Interval.oneTo(3)));
    }

    @Test
    public void contains()
    {
        assertTrue(Iterate.contains(FastList.newListWith(1, 2, 3), 1));
        assertFalse(Iterate.contains(FastList.newListWith(1, 2, 3), 4));
        assertTrue(Iterate.contains(FastList.newListWith(1, 2, 3).asLazy(), 1));
        assertTrue(Iterate.contains(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), 1));
        assertFalse(Iterate.contains(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), 4));
        assertTrue(Iterate.contains(Interval.oneTo(3), 1));
        assertFalse(Iterate.contains(Interval.oneTo(3), 4));
    }

    public static final class ListContainer<T>
    {
        private final List<T> list;

        private ListContainer(List<T> list)
        {
            this.list = list;
        }

        private static <V> Function<ListContainer<V>, List<V>> getListFunction()
        {
            return anObject -> anObject.list;
        }

        public List<T> getList()
        {
            return this.list;
        }
    }

    @Test
    public void getFirstAndLast()
    {
        MutableList<Boolean> list = mList(Boolean.TRUE, null, Boolean.FALSE);
        assertEquals(Boolean.TRUE, Iterate.getFirst(list));
        assertEquals(Boolean.FALSE, Iterate.getLast(list));
        assertEquals(Boolean.TRUE, Iterate.getFirst(Collections.unmodifiableList(list)));
        assertEquals(Boolean.FALSE, Iterate.getLast(Collections.unmodifiableList(list)));
        assertEquals(Boolean.TRUE, Iterate.getFirst(new IterableAdapter<>(list)));
        assertEquals(Boolean.FALSE, Iterate.getLast(new IterableAdapter<>(list)));
    }

    @Test
    public void getFirst_null_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.getFirst(null));
    }

    @Test
    public void getList_null_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.getLast(null));
    }

    @Test
    public void getFirstAndLastInterval()
    {
        assertEquals(Integer.valueOf(1), Iterate.getFirst(Interval.oneTo(5)));
        assertEquals(Integer.valueOf(5), Iterate.getLast(Interval.oneTo(5)));
    }

    @Test
    public void getFirstAndLastLinkedList()
    {
        List<Boolean> list = new LinkedList<>(mList(Boolean.TRUE, null, Boolean.FALSE));
        assertEquals(Boolean.TRUE, Iterate.getFirst(list));
        assertEquals(Boolean.FALSE, Iterate.getLast(list));
    }

    @Test
    public void getFirstAndLastTreeSet()
    {
        Set<String> set = new TreeSet<>(mList("1", "2"));
        assertEquals("1", Iterate.getFirst(set));
        assertEquals("2", Iterate.getLast(set));
    }

    @Test
    public void getFirstAndLastCollection()
    {
        Collection<Boolean> list = mList(Boolean.TRUE, null, Boolean.FALSE).asSynchronized();
        assertEquals(Boolean.TRUE, Iterate.getFirst(list));
        assertEquals(Boolean.FALSE, Iterate.getLast(list));
    }

    @Test
    public void getFirstAndLastOnEmpty()
    {
        assertNull(Iterate.getFirst(mList()));
        assertNull(Iterate.getLast(mList()));
    }

    @Test
    public void getFirstAndLastForSet()
    {
        Set<Integer> orderedSet = new TreeSet<>();
        orderedSet.add(1);
        orderedSet.add(2);
        orderedSet.add(3);
        assertEquals(Integer.valueOf(1), Iterate.getFirst(orderedSet));
        assertEquals(Integer.valueOf(3), Iterate.getLast(orderedSet));
    }

    @Test
    public void getFirstAndLastOnEmptyForSet()
    {
        MutableSet<Object> set = UnifiedSet.newSet();
        assertNull(Iterate.getFirst(set));
        assertNull(Iterate.getLast(set));
    }

    private MutableSet<Integer> getIntegerSet()
    {
        return Interval.toSet(1, 5);
    }

    @Test
    public void selectDifferentTargetCollection()
    {
        MutableSet<Integer> set = this.getIntegerSet();
        List<Integer> list = Iterate.select(set, Integer.class::isInstance, new ArrayList<>());
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), list);
    }

    @Test
    public void rejectWithDifferentTargetCollection()
    {
        MutableSet<Integer> set = this.getIntegerSet();
        Collection<Integer> result = Iterate.reject(set, Integer.class::isInstance, FastList.newList());
        Verify.assertEmpty(result);
    }

    @Test
    public void count_empty()
    {
        assertEquals(0, Iterate.count(iList(), ignored -> true));
    }

    @Test
    public void count_null_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.count(null, ignored -> true));
    }

    @Test
    public void toMap()
    {
        MutableSet<Integer> set = UnifiedSet.newSet(this.getIntegerSet());
        MutableMap<String, Integer> map = Iterate.toMap(set, String::valueOf);
        Verify.assertSize(5, map);
        Object expectedValue = 1;
        Object expectedKey = "1";
        Verify.assertContainsKeyValue(expectedKey, expectedValue, map);
        Verify.assertContainsKeyValue("2", 2, map);
        Verify.assertContainsKeyValue("3", 3, map);
        Verify.assertContainsKeyValue("4", 4, map);
        Verify.assertContainsKeyValue("5", 5, map);
    }

    @Test
    public void toMapTarget()
    {
        MutableSet<Integer> set = UnifiedSet.newSet(this.getIntegerSet());
        Map<String, Integer> map = Iterate.toMap(set, String::valueOf, object -> 10 * object, new HashMap<>());
        Verify.assertSize(5, map);
        Object expectedValue = 10;
        Object expectedKey = "1";
        Verify.assertContainsKeyValue(expectedKey, expectedValue, map);
        Verify.assertContainsKeyValue("2", 20, map);
        Verify.assertContainsKeyValue("3", 30, map);
        Verify.assertContainsKeyValue("4", 40, map);
        Verify.assertContainsKeyValue("5", 50, map);
    }

    @Test
    public void addToMap()
    {
        MutableSet<Integer> set = this.getIntegerSet();
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        map.put("the answer", 42);

        Iterate.addToMap(set, String::valueOf, map);

        Verify.assertSize(6, map);
        Verify.assertContainsAllKeyValues(
                map,
                "the answer", 42,
                "1", 1,
                "2", 2,
                "3", 3,
                "4", 4,
                "5", 5);
    }

    @Test
    public void toMapSelectingKeyAndValue()
    {
        MutableSet<Integer> set = UnifiedSet.newSet(this.getIntegerSet());
        MutableMap<String, Integer> map = Iterate.toMap(set, String::valueOf, object -> 10 * object);
        Verify.assertSize(5, map);
        Verify.assertContainsKeyValue("1", 10, map);
        Verify.assertContainsKeyValue("2", 20, map);
        Verify.assertContainsKeyValue("3", 30, map);
        Verify.assertContainsKeyValue("4", 40, map);
        Verify.assertContainsKeyValue("5", 50, map);
    }

    @Test
    public void toMultimap()
    {
        MutableList<Integer> list = Lists.mutable.of(1, 2, 2, 3, 3, 3);

        Pair<String, String>[] expectedPairs = new Pair[]{
                Tuples.pair("Key1", "1"),
                Tuples.pair("Key1", "1"),
                Tuples.pair("Key2", "1"),
                Tuples.pair("Key2", "2"),
                Tuples.pair("Key2", "1"),
                Tuples.pair("Key2", "2"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "3"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "3"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "3"),
        };

        Function<Integer, String> keyFunction = each -> "Key" + each;
        Function<Integer, Iterable<String>> valuesFunction = each -> Lists.mutable.of("1", String.valueOf(each));
        MutableListMultimap<String, String> actualMultimap1 = Iterate.toMultimap(list, keyFunction, valuesFunction, Multimaps.mutable.list.empty());
        Verify.assertListMultimapsEqual(FastListMultimap.newMultimap(expectedPairs), actualMultimap1);

        MutableSetMultimap<String, String> actualMultimap2 = Iterate.toMultimap(list, keyFunction, valuesFunction, Multimaps.mutable.set.empty());
        Verify.assertSetMultimapsEqual(UnifiedSetMultimap.newMultimap(expectedPairs), actualMultimap2);

        MutableBagMultimap<String, String> actualMultimap3 = Iterate.toMultimap(list, keyFunction, valuesFunction, Multimaps.mutable.bag.empty());
        Verify.assertBagMultimapsEqual(HashBagMultimap.newMultimap(expectedPairs), actualMultimap3);

        MutableSortedSetMultimap<String, String> expectedMultimap4 = TreeSortedSetMultimap.newMultimap(Comparators.naturalOrder());
        expectedMultimap4.putAllPairs(expectedPairs);
        MutableSortedSetMultimap<String, String> actualMultimap4 = Iterate.toMultimap(list, keyFunction, valuesFunction, Multimaps.mutable.sortedSet.with(Comparators.naturalOrder()));
        Verify.assertSortedSetMultimapsEqual(expectedMultimap4, actualMultimap4);
        assertSame(expectedMultimap4.comparator(), actualMultimap4.comparator());

        MutableSortedSetMultimap<String, String> expectedMultimap5 = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap5.putAllPairs(expectedPairs);
        MutableSortedSetMultimap<String, String> actualMultimap5 = Iterate.toMultimap(list, keyFunction, valuesFunction, Multimaps.mutable.sortedSet.with(Comparators.reverseNaturalOrder()));
        Verify.assertSortedSetMultimapsEqual(expectedMultimap5, actualMultimap5);
        assertSame(expectedMultimap5.comparator(), actualMultimap5.comparator());

        MutableSortedBagMultimap<String, String> expectedMultimap6 = TreeBagMultimap.newMultimap(Comparators.naturalOrder());
        expectedMultimap6.putAllPairs(expectedPairs);
        MutableSortedBagMultimap<String, String> actualMultimap6 = Iterate.toMultimap(list, keyFunction, valuesFunction, TreeBagMultimap.newMultimap(Comparators.naturalOrder()));
        Verify.assertSortedBagMultimapsEqual(expectedMultimap6, actualMultimap6);
        assertSame(expectedMultimap6.comparator(), actualMultimap6.comparator());

        MutableSortedBagMultimap<String, String> expectedMultimap7 = TreeBagMultimap.newMultimap(Comparators.reverseNaturalOrder());
        expectedMultimap7.putAllPairs(expectedPairs);
        MutableSortedBagMultimap<String, String> actualMultimap7 = Iterate.toMultimap(list, keyFunction, valuesFunction, TreeBagMultimap.newMultimap(Comparators.reverseNaturalOrder()));
        Verify.assertSortedBagMultimapsEqual(expectedMultimap7, actualMultimap7);
        assertSame(expectedMultimap7.comparator(), actualMultimap7.comparator());

        // Below tests are for examples only. They do not add any coverage.
        BagMultimap<String, String> expectedMultimap8 = HashBagMultimap.newMultimap(
                Tuples.pair("Key1", "1"),
                Tuples.pair("Key2", "1"),
                Tuples.pair("Key2", "2"),
                Tuples.pair("Key2", "1"),
                Tuples.pair("Key2", "2"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "3"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "3"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "3"));
        MutableBagMultimap<String, String> actualMultimap8 = Iterate.toMultimap(list, keyFunction, each -> Sets.mutable.of("1", "1", String.valueOf(each)), Multimaps.mutable.bag.empty());
        Verify.assertBagMultimapsEqual(expectedMultimap8, actualMultimap8);

        MutableBagMultimap<String, String> expectedMultimap9 = HashBagMultimap.newMultimap(
                Tuples.pair("Key1", "1"),
                Tuples.pair("Key1", "1"),
                Tuples.pair("Key1", "1"),
                Tuples.pair("Key2", "1"),
                Tuples.pair("Key2", "1"),
                Tuples.pair("Key2", "2"),
                Tuples.pair("Key2", "1"),
                Tuples.pair("Key2", "1"),
                Tuples.pair("Key2", "2"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "3"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "3"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "1"),
                Tuples.pair("Key3", "3"));

        MutableBagMultimap<String, String> actualMultimap9 = Iterate.toMultimap(list, keyFunction, each -> Bags.mutable.of("1", "1", String.valueOf(each)), Multimaps.mutable.bag.empty());
        Verify.assertBagMultimapsEqual(expectedMultimap9, actualMultimap9);
    }

    @Test
    public void forEachWithIndex()
    {
        this.iterables.each(each -> {
            UnifiedSet<Integer> set = UnifiedSet.newSet();
            Iterate.forEachWithIndex(each, ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(set)));
            assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5), set);
        });
    }

    @Test
    public void selectPairs()
    {
        ImmutableList<Twin<String>> twins = iList(
                Tuples.twin("1", "2"),
                Tuples.twin("2", "1"),
                Tuples.twin("3", "3"));
        Collection<Twin<String>> results = Iterate.select(twins, new PairPredicate<String, String>()
        {
            @Override
            public boolean accept(String argument1, String argument2)
            {
                return "1".equals(argument1) || "1".equals(argument2);
            }
        });
        Verify.assertSize(2, results);
    }

    @Test
    public void detect()
    {
        this.iterables.each(each -> {
            Integer result = Iterate.detect(each, Predicates.instanceOf(Integer.class));
            assertTrue(UnifiedSet.newSet(each).contains(result));
        });
    }

    @Test
    public void detectOptional()
    {
        this.iterables.forEach(Procedures.cast(each -> {
            Optional<Integer> result = Iterate.detectOptional(each, Predicates.instanceOf(Integer.class));
            assertTrue(result.isPresent());
            Verify.assertContains(result.get(), UnifiedSet.newSet(each));

            Optional<Integer> emptyResult = Iterate.detectOptional(each, Predicates.instanceOf(String.class));
            assertFalse(emptyResult.isPresent());
        }));
    }

    @Test
    public void detectIndex()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertEquals(4, Iterate.detectIndex(list, Integer.valueOf(1)::equals));
        assertEquals(0, Iterate.detectIndex(list, Integer.valueOf(5)::equals));
        assertEquals(-1, Iterate.detectIndex(Lists.immutable.of(), Integer.valueOf(1)::equals));
        assertEquals(-1, Iterate.detectIndex(Sets.immutable.of(), Integer.valueOf(1)::equals));
    }

    @Test
    public void detectIndexWithTreeSet()
    {
        Set<Integer> treeSet = new TreeSet<>(Interval.toReverseList(1, 5));
        assertEquals(0, Iterate.detectIndex(treeSet, Integer.valueOf(1)::equals));
        assertEquals(4, Iterate.detectIndex(treeSet, Integer.valueOf(5)::equals));
    }

    @Test
    public void detectIndexWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertEquals(4, Iterate.detectIndexWith(list, Object::equals, 1));
        assertEquals(0, Iterate.detectIndexWith(list, Object::equals, 5));
        assertEquals(-1, Iterate.detectIndexWith(iList(), Object::equals, 5));
        assertEquals(-1, Iterate.detectIndexWith(iSet(), Object::equals, 5));
    }

    @Test
    public void detectIndexWithWithTreeSet()
    {
        Set<Integer> treeSet = new TreeSet<>(Interval.toReverseList(1, 5));
        assertEquals(0, Iterate.detectIndexWith(treeSet, Object::equals, 1));
        assertEquals(4, Iterate.detectIndexWith(treeSet, Object::equals, 5));
    }

    @Test
    public void detectWithIfNone()
    {
        this.iterables.each(each -> {
            Integer result = Iterate.detectWithIfNone(each, Predicates2.instanceOf(), Integer.class, 5);
            Verify.assertContains(result, UnifiedSet.newSet(each));
        });
    }

    @Test
    public void selectWith()
    {
        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.selectWith(each, Predicates2.greaterThan(), 3);
            assertTrue(result.containsAll(FastList.newListWith(4, 5)));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.selectWith(null, null, null));
    }

    @Test
    public void selectWithWithTarget()
    {
        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.selectWith(each, Predicates2.greaterThan(), 3, FastList.newList());
            assertTrue(result.containsAll(FastList.newListWith(4, 5)));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.selectWith(null, null, null, null));
    }

    @Test
    public void rejectWith()
    {
        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.rejectWith(each, Predicates2.greaterThan(), 3);
            assertTrue(result.containsAll(FastList.newListWith(1, 2, 3)));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.rejectWith(null, null, null));
    }

    @Test
    public void rejectWithTarget()
    {
        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.rejectWith(each, Predicates2.greaterThan(), 3, FastList.newList());
            assertTrue(result.containsAll(FastList.newListWith(1, 2, 3)));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.rejectWith(null, null, null, null));
    }

    @Test
    public void selectAndRejectWith()
    {
        this.iterables.each(each -> {
            Twin<MutableList<Integer>> result = Iterate.selectAndRejectWith(each, Predicates2.greaterThan(), 3);
            assertEquals(iBag(4, 5), result.getOne().toBag());
            assertEquals(iBag(1, 2, 3), result.getTwo().toBag());
        });
    }

    @Test
    public void partition()
    {
        this.iterables.each(each -> {
            PartitionIterable<Integer> result = Iterate.partition(each, Predicates.greaterThan(3));
            assertEquals(iBag(4, 5), result.getSelected().toBag());
            assertEquals(iBag(1, 2, 3), result.getRejected().toBag());
        });
    }

    @Test
    public void partitionWith()
    {
        this.iterables.each(each -> {
            PartitionIterable<Integer> result = Iterate.partitionWith(each, Predicates2.greaterThan(), 3);
            assertEquals(iBag(4, 5), result.getSelected().toBag());
            assertEquals(iBag(1, 2, 3), result.getRejected().toBag());
        });
    }

    @Test
    public void rejectTargetCollection()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = Iterate.reject(list, Integer.class::isInstance, FastList.newList());
        Verify.assertEmpty(results);
    }

    @Test
    public void rejectOnRandomAccessTargetCollection()
    {
        List<Integer> list = Collections.synchronizedList(Interval.toReverseList(1, 5));
        MutableList<Integer> results = Iterate.reject(list, Integer.class::isInstance, FastList.newList());
        Verify.assertEmpty(results);
    }

    @Test
    public void rejectWithTargetCollection()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = Iterate.rejectWith(list, Predicates2.instanceOf(), Integer.class, FastList.newList());
        Verify.assertEmpty(results);
    }

    @Test
    public void rejectWithOnRandomAccessTargetCollection()
    {
        List<Integer> list = Collections.synchronizedList(Interval.toReverseList(1, 5));
        MutableList<Integer> results = Iterate.rejectWith(list, Predicates2.instanceOf(), Integer.class, FastList.newList());
        Verify.assertEmpty(results);
    }

    @Test
    public void anySatisfy()
    {
        this.iterables.each(each -> assertTrue(Iterate.anySatisfy(each, Integer.class::isInstance)));
    }

    @Test
    public void anySatisfyWith()
    {
        this.iterables.each(each -> assertTrue(Iterate.anySatisfyWith(each, Predicates2.instanceOf(), Integer.class)));
    }

    @Test
    public void allSatisfy()
    {
        this.iterables.each(each -> assertTrue(Iterate.allSatisfy(each, Integer.class::isInstance)));
    }

    @Test
    public void allSatisfyWith()
    {
        this.iterables.each(each -> assertTrue(Iterate.allSatisfyWith(each, Predicates2.instanceOf(), Integer.class)));
    }

    @Test
    public void noneSatisfy()
    {
        this.iterables.each(each -> assertTrue(Iterate.noneSatisfy(each, String.class::isInstance)));
    }

    @Test
    public void noneSatisfyWith()
    {
        this.iterables.each(each -> assertTrue(Iterate.noneSatisfyWith(each, Predicates2.instanceOf(), String.class)));
    }

    @Test
    public void selectWithSet()
    {
        Verify.assertSize(1, Iterate.selectWith(this.getIntegerSet(), Object::equals, 1));
        Verify.assertSize(1, Iterate.selectWith(this.getIntegerSet(), Object::equals, 1, FastList.newList()));
    }

    @Test
    public void rejectWithSet()
    {
        Verify.assertSize(4, Iterate.rejectWith(this.getIntegerSet(), Object::equals, 1));
        Verify.assertSize(4, Iterate.rejectWith(this.getIntegerSet(), Object::equals, 1, FastList.newList()));
    }

    @Test
    public void detectWithSet()
    {
        assertEquals(Integer.valueOf(1), Iterate.detectWith(this.getIntegerSet(), Object::equals, 1));
    }

    @Test
    public void detectWithRandomAccess()
    {
        List<Integer> list = Collections.synchronizedList(Interval.oneTo(5));
        assertEquals(Integer.valueOf(1), Iterate.detectWith(list, Object::equals, 1));
    }

    @Test
    public void detectWithOptionalSet()
    {
        assertEquals(Optional.of(Integer.valueOf(1)), Iterate.detectWithOptional(this.getIntegerSet(), Object::equals, 1));
        assertEquals(Optional.empty(), Iterate.detectWithOptional(this.getIntegerSet(), Object::equals, 10));
    }

    @Test
    public void detectWithOptionalRandomAccess()
    {
        List<Integer> list = Collections.synchronizedList(Interval.oneTo(5));
        assertEquals(Optional.of(Integer.valueOf(1)), Iterate.detectWithOptional(list, Object::equals, 1));
        assertEquals(Optional.empty(), Iterate.detectWithOptional(list, Object::equals, 10));
    }

    @Test
    public void anySatisfyWithSet()
    {
        assertTrue(Iterate.anySatisfyWith(this.getIntegerSet(), Object::equals, 1));
    }

    @Test
    public void allSatisfyWithSet()
    {
        assertFalse(Iterate.allSatisfyWith(this.getIntegerSet(), Object::equals, 1));
        assertTrue(Iterate.allSatisfyWith(this.getIntegerSet(), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void noneSatisfyWithSet()
    {
        assertTrue(Iterate.noneSatisfyWith(this.getIntegerSet(), Object::equals, 100));
        assertFalse(Iterate.noneSatisfyWith(this.getIntegerSet(), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void selectAndRejectWithSet()
    {
        Twin<MutableList<Integer>> result = Iterate.selectAndRejectWith(this.getIntegerSet(), Predicates2.in(), iList(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(4, result.getTwo());
    }

    @Test
    public void forEach()
    {
        this.iterables.each(each -> {
            UnifiedSet<Integer> set = UnifiedSet.newSet();
            Iterate.forEach(each, Procedures.cast(set::add));
            assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5), set);
        });
    }

    @Test
    public void collectIf()
    {
        this.iterables.each(each -> {
            Collection<String> result = Iterate.collectIf(each, Predicates.greaterThan(3), String::valueOf);
            assertTrue(result.containsAll(FastList.newListWith("4", "5")));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectIf(null, null, null));
    }

    @Test
    public void collectIfTarget()
    {
        this.iterables.each(each -> {
            Collection<String> result = Iterate.collectIf(each, Predicates.greaterThan(3), String::valueOf, FastList.newList());
            assertTrue(result.containsAll(FastList.newListWith("4", "5")));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectIf(null, null, null, null));
    }

    @Test
    public void collect()
    {
        this.iterables.each(each -> {
            Collection<String> result = Iterate.collect(each, String::valueOf);
            assertTrue(result.containsAll(FastList.newListWith("1", "2", "3", "4", "5")));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collect(null, a -> a));
    }

    @Test
    public void collectBoolean()
    {
        this.iterables.each(each -> {
            MutableBooleanCollection result = Iterate.collectBoolean(each, PrimitiveFunctions.integerIsPositive());
            assertTrue(result.containsAll(true, true, true, true, true));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectBoolean(null, PrimitiveFunctions.integerIsPositive()));
    }

    @Test
    public void collectBooleanWithTarget()
    {
        this.iterables.each(each -> {
            MutableBooleanCollection expected = new BooleanArrayList();
            MutableBooleanCollection actual = Iterate.collectBoolean(each, PrimitiveFunctions.integerIsPositive(), expected);
            assertTrue(expected.containsAll(true, true, true, true, true));
            assertSame(expected, actual, "Target list sent as parameter not returned");
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectBoolean(null, PrimitiveFunctions.integerIsPositive(), new BooleanArrayList()));
    }

    @Test
    public void collectByte()
    {
        this.iterables.each(each -> {
            MutableByteCollection result = Iterate.collectByte(each, PrimitiveFunctions.unboxIntegerToByte());
            assertTrue(result.containsAll((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectByte(null, PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Test
    public void collectByteWithTarget()
    {
        this.iterables.each(each -> {
            MutableByteCollection expected = new ByteArrayList();
            MutableByteCollection actual = Iterate.collectByte(each, PrimitiveFunctions.unboxIntegerToByte(), expected);
            assertTrue(actual.containsAll((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5));
            assertSame(expected, actual, "Target list sent as parameter not returned");
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectByte(null, PrimitiveFunctions.unboxIntegerToByte(), new ByteArrayList()));
    }

    @Test
    public void collectChar()
    {
        this.iterables.each(each -> {
            MutableCharCollection result = Iterate.collectChar(each, PrimitiveFunctions.unboxIntegerToChar());
            assertTrue(result.containsAll((char) 1, (char) 2, (char) 3, (char) 4, (char) 5));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectChar(null, PrimitiveFunctions.unboxIntegerToChar()));
    }

    @Test
    public void collectCharWithTarget()
    {
        this.iterables.each(each -> {
            MutableCharCollection expected = new CharArrayList();
            MutableCharCollection actual = Iterate.collectChar(each, PrimitiveFunctions.unboxIntegerToChar(), expected);
            assertTrue(actual.containsAll((char) 1, (char) 2, (char) 3, (char) 4, (char) 5));
            assertSame(expected, actual, "Target list sent as parameter not returned");
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectChar(null, PrimitiveFunctions.unboxIntegerToChar(), new CharArrayList()));
    }

    @Test
    public void collectDouble()
    {
        this.iterables.each(each -> {
            MutableDoubleCollection result = Iterate.collectDouble(each, PrimitiveFunctions.unboxIntegerToDouble());
            assertTrue(result.containsAll(1.0d, 2.0d, 3.0d, 4.0d, 5.0d));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectDouble(null, PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Test
    public void collectDoubleWithTarget()
    {
        this.iterables.each(each -> {
            MutableDoubleCollection expected = new DoubleArrayList();
            MutableDoubleCollection actual = Iterate.collectDouble(each, PrimitiveFunctions.unboxIntegerToDouble(), expected);
            assertTrue(actual.containsAll(1.0d, 2.0d, 3.0d, 4.0d, 5.0d));
            assertSame(expected, actual, "Target list sent as parameter not returned");
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectDouble(null, PrimitiveFunctions.unboxIntegerToDouble(), new DoubleArrayList()));
    }

    @Test
    public void collectFloat()
    {
        this.iterables.each(each -> {
            MutableFloatCollection result = Iterate.collectFloat(each, PrimitiveFunctions.unboxIntegerToFloat());
            assertTrue(result.containsAll(1.0f, 2.0f, 3.0f, 4.0f, 5.0f));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectFloat(null, PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Test
    public void collectFloatWithTarget()
    {
        this.iterables.each(each -> {
            MutableFloatCollection expected = new FloatArrayList();
            MutableFloatCollection actual = Iterate.collectFloat(each, PrimitiveFunctions.unboxIntegerToFloat(), expected);
            assertTrue(actual.containsAll(1.0f, 2.0f, 3.0f, 4.0f, 5.0f));
            assertSame(expected, actual, "Target list sent as parameter not returned");
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectFloat(null, PrimitiveFunctions.unboxIntegerToFloat(), new FloatArrayList()));
    }

    @Test
    public void collectInt()
    {
        this.iterables.each(each -> {
            MutableIntCollection result = Iterate.collectInt(each, PrimitiveFunctions.unboxIntegerToInt());
            assertTrue(result.containsAll(1, 2, 3, 4, 5));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectInt(null, PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Test
    public void collectIntWithTarget()
    {
        this.iterables.each(each -> {
            MutableIntCollection expected = new IntArrayList();
            MutableIntCollection actual = Iterate.collectInt(each, PrimitiveFunctions.unboxIntegerToInt(), expected);
            assertTrue(actual.containsAll(1, 2, 3, 4, 5));
            assertSame(expected, actual, "Target list sent as parameter not returned");
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectInt(null, PrimitiveFunctions.unboxIntegerToInt(), new IntArrayList()));
    }

    @Test
    public void collectLong()
    {
        this.iterables.each(each -> {
            MutableLongCollection result = Iterate.collectLong(each, PrimitiveFunctions.unboxIntegerToLong());
            assertTrue(result.containsAll(1L, 2L, 3L, 4L, 5L));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectLong(null, PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Test
    public void collectLongWithTarget()
    {
        this.iterables.each(each -> {
            MutableLongCollection expected = new LongArrayList();
            MutableLongCollection actual = Iterate.collectLong(each, PrimitiveFunctions.unboxIntegerToLong(), expected);
            assertTrue(actual.containsAll(1L, 2L, 3L, 4L, 5L));
            assertSame(expected, actual, "Target list sent as parameter not returned");
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectLong(null, PrimitiveFunctions.unboxIntegerToLong(), new LongArrayList()));
    }

    @Test
    public void collectShort()
    {
        this.iterables.each(each -> {
            MutableShortCollection result = Iterate.collectShort(each, PrimitiveFunctions.unboxIntegerToShort());
            assertTrue(result.containsAll((short) 1, (short) 2, (short) 3, (short) 4, (short) 5));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectShort(null, PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Test
    public void collectShortWithTarget()
    {
        this.iterables.each(each -> {
            MutableShortCollection expected = new ShortArrayList();
            MutableShortCollection actual = Iterate.collectShort(each, PrimitiveFunctions.unboxIntegerToShort(), expected);
            assertTrue(actual.containsAll((short) 1, (short) 2, (short) 3, (short) 4, (short) 5));
            assertSame(expected, actual, "Target list sent as parameter not returned");
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectShort(null, PrimitiveFunctions.unboxIntegerToShort(), new ShortArrayList()));
    }

    @Test
    public void collect_sortedSetSource()
    {
        class Foo implements Comparable<Foo>
        {
            private final int value;

            Foo(int value)
            {
                this.value = value;
            }

            public int getValue()
            {
                return this.value;
            }

            @Override
            public int compareTo(Foo that)
            {
                return Comparators.naturalOrder().compare(this.value, that.value);
            }

            @Override
            public boolean equals(Object o)
            {
                if (this == o)
                {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass())
                {
                    return false;
                }

                Foo foo = (Foo) o;

                return this.value == foo.value;
            }

            @Override
            public int hashCode()
            {
                return this.value;
            }
        }

        class Bar
        {
            private final int value;

            Bar(int value)
            {
                this.value = value;
            }

            public int getValue()
            {
                return this.value;
            }

            @Override
            public boolean equals(Object o)
            {
                if (this == o)
                {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass())
                {
                    return false;
                }

                Bar bar = (Bar) o;

                return this.value == bar.value;
            }

            @Override
            public int hashCode()
            {
                return this.value;
            }
        }

        Set<Foo> foos = new TreeSet<>();
        foos.add(new Foo(1));
        foos.add(new Foo(2));
        Collection<Bar> bars = Iterate.collect(foos, foo -> new Bar(foo.getValue()));

        assertEquals(FastList.newListWith(new Bar(1), new Bar(2)), bars);
    }

    @Test
    public void collectTarget()
    {
        this.iterables.each(each -> {
            Collection<String> result = Iterate.collect(each, String::valueOf, UnifiedSet.newSet());
            assertTrue(result.containsAll(FastList.newListWith("1", "2", "3", "4", "5")));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collect(null, a -> a, null));
    }

    @Test
    public void collectWith()
    {
        this.iterables.each(each -> {
            Collection<String> result = Iterate.collectWith(each, (each1, parm) -> each1 + parm, " ");
            assertTrue(result.containsAll(FastList.newListWith("1 ", "2 ", "3 ", "4 ", "5 ")));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectWith(null, null, null));
    }

    @Test
    public void collectWithWithTarget()
    {
        this.iterables.each(each -> {
            Collection<String> result = Iterate.collectWith(each, (each1, parm) -> each1 + parm, " ", UnifiedSet.newSet());
            assertTrue(result.containsAll(FastList.newListWith("1 ", "2 ", "3 ", "4 ", "5 ")));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.collectWith(null, null, null, null));
    }

    @Test
    public void removeIf()
    {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        this.assertRemoveIfFromList(integers);

        MutableList<Integer> integers2 = Interval.oneTo(5).toList();
        this.assertRemoveIfFromList(Collections.synchronizedList(integers2));

        MutableList<Integer> integers3 = Interval.oneTo(5).toList();
        this.assertRemoveIfFromList(FastList.newList(integers3));
        assertThrows(IllegalArgumentException.class, () -> Iterate.removeIf(null, null));
    }

    @Test
    public void removeIfWith()
    {
        MutableList<Integer> objects = FastList.newList(Lists.fixedSize.of(1, 2, 3, null));
        assertTrue(Iterate.removeIfWith(objects, (each1, ignored1) -> each1 == null, null));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);

        MutableList<Integer> objects2 = FastList.newList(Lists.fixedSize.of(null, 1, 2, 3));
        assertTrue(Iterate.removeIfWith(objects2, (each, ignored) -> each == null, null));
        Verify.assertSize(3, objects2);
        Verify.assertContainsAll(objects2, 1, 2, 3);

        assertThrows(IllegalArgumentException.class, () -> Iterate.removeIfWith(null, null, null));
    }

    private void assertRemoveIfFromList(List<Integer> newIntegers)
    {
        assertTrue(Iterate.removeIf(newIntegers, IntegerPredicates.isEven()));
        assertFalse(Iterate.removeIf(FastList.newListWith(1, 3, 5), IntegerPredicates.isEven()));
        assertFalse(Iterate.removeIf(FastList.newList(), IntegerPredicates.isEven()));
        Verify.assertContainsAll(newIntegers, 1, 3, 5);
        Verify.assertSize(3, newIntegers);
    }

    @Test
    public void removeIfLinkedList()
    {
        List<Integer> integers = new LinkedList<>(Interval.oneTo(5).toList());
        this.assertRemoveIfFromList(integers);
    }

    @Test
    public void removeIfAll()
    {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        assertTrue(Iterate.removeIf(integers, ignored -> true));
        Verify.assertSize(0, integers);
    }

    @Test
    public void removeIfNone()
    {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        assertFalse(Iterate.removeIf(integers, ignored -> false));
        Verify.assertSize(5, integers);
    }

    @Test
    public void removeIfFromSet()
    {
        MutableSet<Integer> integers = Interval.toSet(1, 5);
        assertTrue(Iterate.removeIf(integers, IntegerPredicates.isEven()));
        Verify.assertContainsAll(integers, 1, 3, 5);
        Verify.assertSize(3, integers);
    }

    @Test
    public void removeIfWithFastList()
    {
        MutableList<Integer> objects = FastList.newListWith(1, 2, 3, null);
        assertTrue(Iterate.removeIfWith(objects, (each1, ignored1) -> each1 == null, null));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
        MutableList<Integer> objects1 = FastList.newListWith(null, 1, 2, 3);
        assertTrue(Iterate.removeIfWith(objects1, (each, ignored) -> each == null, null));
        Verify.assertSize(3, objects1);
        Verify.assertContainsAll(objects1, 1, 2, 3);
    }

    @Test
    public void removeIfWithRandomAccess()
    {
        List<Integer> objects = Collections.synchronizedList(new ArrayList<>(Lists.fixedSize.of(1, 2, 3, null)));
        assertTrue(Iterate.removeIfWith(objects, (each1, ignored1) -> each1 == null, null));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
        List<Integer> objects1 =
                Collections.synchronizedList(new ArrayList<>(Lists.fixedSize.of(null, 1, 2, 3)));
        assertTrue(Iterate.removeIfWith(objects1, (each, ignored) -> each == null, null));
        Verify.assertSize(3, objects1);
        Verify.assertContainsAll(objects1, 1, 2, 3);
    }

    @Test
    public void removeIfWithLinkedList()
    {
        List<Integer> objects = new LinkedList<>(Lists.fixedSize.of(1, 2, 3, null));
        assertTrue(Iterate.removeIfWith(objects, (each1, ignored1) -> each1 == null, null));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
        List<Integer> objects1 = new LinkedList<>(Lists.fixedSize.of(null, 1, 2, 3));
        assertTrue(Iterate.removeIfWith(objects1, (each, ignored) -> each == null, null));
        Verify.assertSize(3, objects1);
        Verify.assertContainsAll(objects1, 1, 2, 3);
    }

    @Test
    public void injectIntoIfProcedure()
    {
        Integer newItemToIndex = 99;
        MutableMap<String, Integer> index1 = IterateTest.createPretendIndex(1);
        MutableMap<String, Integer> index2 = IterateTest.createPretendIndex(2);
        MutableMap<String, Integer> index3 = IterateTest.createPretendIndex(3);
        MutableMap<String, Integer> index4 = IterateTest.createPretendIndex(4);
        MutableMap<String, Integer> index5 = IterateTest.createPretendIndex(5);
        MutableMap<String, MutableMap<String, Integer>> allIndexes = UnifiedMap.newMapWith(
                Tuples.pair("pretend index 1", index1),
                Tuples.pair("pretend index 2", index2),
                Tuples.pair("pretend index 3", index3),
                Tuples.pair("pretend index 4", index4),
                Tuples.pair("pretend index 5", index5));
        MutableSet<MutableMap<String, Integer>> systemIndexes = Sets.fixedSize.of(index3, index5);

        MapIterate.injectIntoIf(newItemToIndex, allIndexes, Predicates.notIn(systemIndexes), (itemToAdd, index) -> {
            index.put(itemToAdd.toString(), itemToAdd);
            return itemToAdd;
        });

        Verify.assertSize(5, allIndexes);

        Verify.assertContainsKey("pretend index 2", allIndexes);
        Verify.assertContainsKey("pretend index 3", allIndexes);

        Verify.assertContainsKeyValue("99", newItemToIndex, index1);
        Verify.assertContainsKeyValue("99", newItemToIndex, index2);
        Verify.assertNotContainsKey("99", index3);
        Verify.assertContainsKeyValue("99", newItemToIndex, index4);
        Verify.assertNotContainsKey("99", index5);
    }

    public static MutableMap<String, Integer> createPretendIndex(int initialEntry)
    {
        return UnifiedMap.newWithKeysValues(String.valueOf(initialEntry), initialEntry);
    }

    @Test
    public void isEmpty()
    {
        assertTrue(Iterate.isEmpty(null));
        assertTrue(Iterate.isEmpty(Lists.fixedSize.of()));
        assertFalse(Iterate.isEmpty(Lists.fixedSize.of("1")));
        assertTrue(Iterate.isEmpty(Maps.fixedSize.of()));
        assertFalse(Iterate.isEmpty(Maps.fixedSize.of("1", "1")));
        assertTrue(Iterate.isEmpty(new IterableAdapter<>(Lists.fixedSize.of())));
        assertFalse(Iterate.isEmpty(new IterableAdapter<>(Lists.fixedSize.of("1"))));
        assertTrue(Iterate.isEmpty(Lists.fixedSize.of().asLazy()));
        assertFalse(Iterate.isEmpty(Lists.fixedSize.of("1").asLazy()));
    }

    @Test
    public void notEmpty()
    {
        assertFalse(Iterate.notEmpty(null));
        assertFalse(Iterate.notEmpty(Lists.fixedSize.of()));
        assertTrue(Iterate.notEmpty(Lists.fixedSize.of("1")));
        assertFalse(Iterate.notEmpty(Maps.fixedSize.of()));
        assertTrue(Iterate.notEmpty(Maps.fixedSize.of("1", "1")));
        assertFalse(Iterate.notEmpty(new IterableAdapter<>(Lists.fixedSize.of())));
        assertTrue(Iterate.notEmpty(new IterableAdapter<>(Lists.fixedSize.of("1"))));
        assertFalse(Iterate.notEmpty(Lists.fixedSize.of().asLazy()));
        assertTrue(Iterate.notEmpty(Lists.fixedSize.of("1").asLazy()));
    }

    @Test
    public void toSortedList()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> sorted = Iterate.toSortedList(list);
        Verify.assertStartsWith(sorted, 1, 2, 3, 4, 5);
    }

    @Test
    public void toSortedListWithComparator()
    {
        MutableList<Integer> list = Interval.oneTo(5).toList();
        MutableList<Integer> sorted = Iterate.toSortedList(list, Collections.reverseOrder());
        Verify.assertStartsWith(sorted, 5, 4, 3, 2, 1);
    }

    @Test
    public void select()
    {
        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.select(each, Predicates.greaterThan(3));
            assertTrue(result.containsAll(FastList.newListWith(4, 5)));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.select(null, null));
    }

    @Test
    public void selectTarget()
    {
        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.select(each, Predicates.greaterThan(3), FastList.newList());
            assertTrue(result.containsAll(FastList.newListWith(4, 5)));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.select(null, null, null));
    }

    @Test
    public void reject()
    {
        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.reject(each, Predicates.greaterThan(3));
            assertTrue(result.containsAll(FastList.newListWith(1, 2, 3)));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.reject(null, null));
    }

    @Test
    public void rejectTarget()
    {
        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.reject(each, Predicates.greaterThan(3), FastList.newList());
            assertTrue(result.containsAll(FastList.newListWith(1, 2, 3)));
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.reject(null, null, null));
    }

    @Test
    public void selectInstancesOf()
    {
        Iterable<Number> numbers1 = Collections.unmodifiableList(new ArrayList<Number>(FastList.newListWith(1, 2.0, 3, 4.0, 5)));
        Iterable<Number> numbers2 = Collections.unmodifiableCollection(new ArrayList<Number>(FastList.newListWith(1, 2.0, 3, 4.0, 5)));

        Verify.assertContainsAll(Iterate.selectInstancesOf(numbers1, Integer.class), 1, 3, 5);
        Verify.assertContainsAll(Iterate.selectInstancesOf(numbers2, Integer.class), 1, 3, 5);
    }

    @Test
    public void count()
    {
        this.iterables.each(each -> {
            int result = Iterate.count(each, Predicates.greaterThan(3));
            assertEquals(2, result);
        });
    }

    @Test
    public void count_null_null_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.count(null, null));
    }

    @Test
    public void countWith()
    {
        this.iterables.each(each -> {
            int result = Iterate.countWith(each, Predicates2.greaterThan(), 3);
            assertEquals(2, result);
        });
    }

    @Test
    public void countWith_null_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.countWith(null, null, null));
    }

    @Test
    public void injectIntoWith()
    {
        Sum result = new IntegerSum(0);
        Integer parameter = 2;
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        this.basicTestDoubleSum(result, integers, parameter);
    }

    @Test
    public void injectIntoWithRandomAccess()
    {
        Sum result = new IntegerSum(0);
        Integer parameter = 2;
        MutableList<Integer> integers = Interval.oneTo(5).toList().asSynchronized();
        this.basicTestDoubleSum(result, integers, parameter);
    }

    private void basicTestDoubleSum(Sum newResult, Collection<Integer> newIntegers, Integer newParameter)
    {
        Function3<Sum, Integer, Integer, Sum> function = (sum, element, withValue) -> sum.add(element.intValue() * withValue.intValue());
        Sum sumOfDoubledValues = Iterate.injectIntoWith(newResult, newIntegers, function, newParameter);
        assertEquals(30, sumOfDoubledValues.getValue().intValue());
    }

    @Test
    public void injectIntoWithHashSet()
    {
        Sum result = new IntegerSum(0);
        Integer parameter = 2;
        MutableSet<Integer> integers = Interval.toSet(1, 5);
        this.basicTestDoubleSum(result, integers, parameter);
    }

    @Test
    public void forEachWith()
    {
        this.iterables.each(each -> {
            Sum result = new IntegerSum(0);
            Iterate.forEachWith(each, (integer, parm) -> result.add(integer.intValue() * parm.intValue()), 2);
            assertEquals(30, result.getValue().intValue());
        });
    }

    @Test
    public void forEachWithSets()
    {
        Sum result = new IntegerSum(0);
        MutableSet<Integer> integers = Interval.toSet(1, 5);
        Iterate.forEachWith(integers, (each, parm) -> result.add(each.intValue() * parm.intValue()), 2);
        assertEquals(30, result.getValue().intValue());
    }

    @Test
    public void sortThis()
    {
        MutableList<Integer> list = Interval.oneTo(5).toList().shuffleThis();
        Verify.assertStartsWith(Iterate.sortThis(list), 1, 2, 3, 4, 5);
        List<Integer> list3 = Interval.oneTo(5).addAllTo(new LinkedList<>());
        Collections.shuffle(list3);
        Verify.assertStartsWith(Iterate.sortThis(list3), 1, 2, 3, 4, 5);
        List<Integer> listOfSizeOne = new LinkedList<>();
        listOfSizeOne.add(1);
        Iterate.sortThis(listOfSizeOne);
        assertEquals(FastList.newListWith(1), listOfSizeOne);
    }

    @Test
    public void sortThisWithPredicate()
    {
        MutableList<Integer> list = Interval.oneTo(5).toList();
        Interval.oneTo(5).addAllTo(list);
        list.shuffleThis();
        Verify.assertStartsWith(Iterate.sortThis(list, Predicates2.lessThan()), 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
        MutableList<Integer> list2 = Interval.oneTo(5).toList();
        Interval.oneTo(5).addAllTo(list2);
        list2.shuffleThis();
        Verify.assertStartsWith(Iterate.sortThis(list2, Predicates2.greaterThan()), 5, 5, 4, 4, 3, 3, 2, 2, 1, 1);
        List<Integer> list3 = Interval.oneTo(5).addAllTo(new LinkedList<>());
        Interval.oneTo(5).addAllTo(list3);
        Collections.shuffle(list3);
        Verify.assertStartsWith(Iterate.sortThis(list3, Predicates2.lessThan()), 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
    }

    @Test
    public void sortThisBy()
    {
        MutableList<Integer> list = Interval.oneTo(5).toList();
        Interval.oneTo(5).addAllTo(list);
        list.shuffleThis();
        MutableList<Integer> sortedList = Iterate.sortThisBy(list, String::valueOf);
        assertSame(list, sortedList);
        assertEquals(Lists.immutable.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5), list);
    }

    @Test
    public void sortThisWithComparator()
    {
        MutableList<Integer> list = Interval.oneTo(5).toList();
        Verify.assertStartsWith(Iterate.sortThis(list, Collections.reverseOrder()), 5, 4, 3, 2, 1);
        List<Integer> list3 = Interval.oneTo(5).addAllTo(new LinkedList<>());
        Verify.assertStartsWith(Iterate.sortThis(list3, Collections.reverseOrder()), 5, 4, 3, 2, 1);
    }

    @Test
    public void take()
    {
        MutableSet<Integer> set = this.getIntegerSet();
        Verify.assertEmpty(Iterate.take(set, 0));
        Verify.assertSize(1, Iterate.take(set, 1));
        Verify.assertSize(2, Iterate.take(set, 2));
        Verify.assertSize(4, Iterate.take(set, 4));
        Verify.assertSize(5, Iterate.take(set, 5));
        Verify.assertSize(5, Iterate.take(set, 10));
        Verify.assertSize(5, Iterate.take(set, Integer.MAX_VALUE));

        MutableSet<Integer> set2 = UnifiedSet.newSet();
        Verify.assertEmpty(Iterate.take(set2, 2));

        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.take(each, 2);
            Verify.assertSize(2, result);
        });
    }

    @Test
    public void take_null_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.take(null, 1));
    }

    @Test
    public void take_negative_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.take(this.getIntegerSet(), -1));
    }

    @Test
    public void drop()
    {
        MutableSet<Integer> set = this.getIntegerSet();
        Verify.assertSize(5, Iterate.drop(set, 0));
        Verify.assertSize(4, Iterate.drop(set, 1));
        Verify.assertSize(3, Iterate.drop(set, 2));
        Verify.assertSize(1, Iterate.drop(set, 4));
        Verify.assertEmpty(Iterate.drop(set, 5));
        Verify.assertEmpty(Iterate.drop(set, 6));
        Verify.assertEmpty(Iterate.drop(set, Integer.MAX_VALUE));

        MutableSet<Integer> set2 = UnifiedSet.newSet();
        Verify.assertEmpty(Iterate.drop(set2, 2));

        this.iterables.each(each -> {
            Collection<Integer> result = Iterate.drop(each, 2);
            Verify.assertSize(3, result);
        });
    }

    @Test
    public void drop_null_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.drop(null, 1));
    }

    @Test
    public void drop_negative_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.drop(this.getIntegerSet(), -1));
    }

    @Test
    public void getOnlySingleton()
    {
        Object value = new Object();
        assertSame(value, Iterate.getOnly(Lists.fixedSize.of(value)));
    }

    @Test
    public void getOnlyEmpty()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.getOnly(Lists.fixedSize.<String>of()));
    }

    @Test
    public void getOnlyMultiple()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.getOnly(Lists.fixedSize.of(new Object(), new Object())));
    }

    private static final class IterableAdapter<E>
            implements Iterable<E>
    {
        private final Iterable<E> iterable;

        private IterableAdapter(Iterable<E> newIterable)
        {
            this.iterable = newIterable;
        }

        @Override
        public Iterator<E> iterator()
        {
            return this.iterable.iterator();
        }
    }

    @Test
    public void zip()
    {
        this.zip(FastList.newListWith("1", "2", "3", "4", "5", "6", "7"));
        this.zip(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
        this.zip(new HashSet<>(FastList.newListWith("1", "2", "3", "4", "5", "6", "7")));
        this.zip(FastList.newListWith("1", "2", "3", "4", "5", "6", "7").asLazy());
        this.zip(new ArrayList<>(Interval.oneTo(101).collect(String::valueOf).toList()));
        assertThrows(IllegalArgumentException.class, () -> Iterate.zip(null, null));
        assertThrows(IllegalArgumentException.class, () -> Iterate.zip(null, null, null));
    }

    private void zip(Iterable<String> iterable)
    {
        List<Object> nulls = Collections.nCopies(Iterate.sizeOf(iterable), null);
        Collection<Pair<String, Object>> pairs = Iterate.zip(iterable, nulls);
        assertEquals(
                UnifiedSet.newSet(iterable),
                Iterate.collect(pairs, (Function<Pair<String, ?>, String>) Pair::getOne, UnifiedSet.newSet()));
        assertEquals(
                nulls,
                Iterate.collect(pairs, (Function<Pair<?, Object>, Object>) Pair::getTwo, Lists.mutable.of()));
    }

    @Test
    public void zipWithIndex()
    {
        this.zipWithIndex(FastList.newListWith("1", "2", "3", "4", "5", "6", "7"));
        this.zipWithIndex(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
        this.zipWithIndex(new HashSet<>(FastList.newListWith("1", "2", "3", "4", "5", "6", "7")));
        this.zipWithIndex(FastList.newListWith("1", "2", "3", "4", "5", "6", "7").asLazy());
        this.zipWithIndex(Lists.immutable.of("1", "2", "3", "4", "5", "6", "7"));
        this.zipWithIndex(new ArrayList<>(Interval.oneTo(101).collect(String::valueOf).toList()));
        assertThrows(IllegalArgumentException.class, () -> Iterate.zipWithIndex(null));
        assertThrows(IllegalArgumentException.class, () -> Iterate.zipWithIndex(null, null));
    }

    private void zipWithIndex(Iterable<String> iterable)
    {
        Collection<Pair<String, Integer>> pairs = Iterate.zipWithIndex(iterable);
        assertEquals(
                UnifiedSet.newSet(iterable),
                Iterate.collect(pairs, (Function<Pair<String, ?>, String>) Pair::getOne, UnifiedSet.newSet()));
        assertEquals(
                Interval.zeroTo(Iterate.sizeOf(iterable) - 1).toSet(),
                Iterate.collect(pairs, (Function<Pair<?, Integer>, Integer>) Pair::getTwo, UnifiedSet.newSet()));
    }

    @Test
    public void chunk()
    {
        MutableList<String> fastList = FastList.newListWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<RichIterable<String>> groups1 = Iterate.chunk(fastList, 2);
        RichIterable<Integer> sizes1 = groups1.collect(Functions.getSizeOf());
        assertEquals(FastList.newListWith(2, 2, 2, 1), sizes1);

        List<String> arrayList = new ArrayList<>(fastList);
        RichIterable<RichIterable<String>> groups2 = Iterate.chunk(arrayList, 2);
        RichIterable<Integer> sizes2 = groups2.collect(Functions.getSizeOf());
        assertEquals(FastList.newListWith(2, 2, 2, 1), sizes2);

        assertThrows(IllegalArgumentException.class, () -> Iterate.chunk(null, 1));
    }

    @Test
    public void getOnly()
    {
        assertEquals("first", Iterate.getOnly(FastList.newListWith("first")));
        assertEquals("first", Iterate.getOnly(FastList.newListWith("first").asLazy()));
        assertThrows(IllegalArgumentException.class, () -> Iterate.getOnly(FastList.newListWith("first", "second")));
        assertThrows(IllegalArgumentException.class, () -> Iterate.getOnly(null));
    }

    private static class WordToItsLetters implements Function<String, Set<Character>>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Set<Character> valueOf(String name)
        {
            return StringIterate.asUppercaseSet(name);
        }
    }

    @Test
    public void makeString()
    {
        this.iterables.each(each -> {
            String result = Iterate.makeString(each);
            assertEquals("1, 2, 3, 4, 5", result);
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.makeString(null));
    }

    @Test
    public void appendString()
    {
        this.iterables.each(each -> {
            StringBuilder stringBuilder = new StringBuilder();
            Iterate.appendString(each, stringBuilder);
            String result = stringBuilder.toString();
            assertEquals("1, 2, 3, 4, 5", result);
        });
        assertThrows(IllegalArgumentException.class, () -> Iterate.appendString(null, new StringBuilder()));
    }

    @Test
    public void aggregateByMutating()
    {
        this.aggregateByMutableResult(FastList.newListWith(1, 1, 1, 2, 2, 3));
        this.aggregateByMutableResult(UnifiedSet.newSetWith(1, 1, 1, 2, 2, 3));
        this.aggregateByMutableResult(HashBag.newBagWith(1, 1, 1, 2, 2, 3));
        this.aggregateByMutableResult(new HashSet<>(UnifiedSet.newSetWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByMutableResult(new LinkedList<>(FastList.newListWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByMutableResult(new ArrayList<>(FastList.newListWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByMutableResult(Arrays.asList(1, 1, 1, 2, 2, 3));
        assertThrows(IllegalArgumentException.class, () -> this.aggregateByMutableResult(null));
    }

    private void aggregateByMutableResult(Iterable<Integer> iterable)
    {
        Function0<AtomicInteger> valueCreator = AtomicInteger::new;
        Procedure2<AtomicInteger, Integer> sumAggregator = AtomicInteger::addAndGet;
        MapIterable<String, AtomicInteger> aggregation = Iterate.aggregateInPlaceBy(iterable, String::valueOf, valueCreator, sumAggregator);
        if (iterable instanceof Set)
        {
            assertEquals(1, aggregation.get("1").intValue());
            assertEquals(2, aggregation.get("2").intValue());
            assertEquals(3, aggregation.get("3").intValue());
        }
        else
        {
            assertEquals(3, aggregation.get("1").intValue());
            assertEquals(4, aggregation.get("2").intValue());
            assertEquals(3, aggregation.get("3").intValue());
        }
    }

    @Test
    public void aggregateByImmutableResult()
    {
        this.aggregateByImmutableResult(FastList.newListWith(1, 1, 1, 2, 2, 3));
        this.aggregateByImmutableResult(UnifiedSet.newSetWith(1, 1, 1, 2, 2, 3));
        this.aggregateByImmutableResult(HashBag.newBagWith(1, 1, 1, 2, 2, 3));
        this.aggregateByImmutableResult(new HashSet<>(UnifiedSet.newSetWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByImmutableResult(new LinkedList<>(FastList.newListWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByImmutableResult(new ArrayList<>(FastList.newListWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByImmutableResult(Arrays.asList(1, 1, 1, 2, 2, 3));
        assertThrows(IllegalArgumentException.class, () -> this.aggregateByImmutableResult(null));
    }

    private void aggregateByImmutableResult(Iterable<Integer> iterable)
    {
        Function0<Integer> valueCreator = () -> 0;
        Function2<Integer, Integer, Integer> sumAggregator = (aggregate, value) -> aggregate + value;
        MapIterable<String, Integer> aggregation = Iterate.aggregateBy(iterable, String::valueOf, valueCreator, sumAggregator);
        if (iterable instanceof Set)
        {
            assertEquals(1, aggregation.get("1").intValue());
            assertEquals(2, aggregation.get("2").intValue());
            assertEquals(3, aggregation.get("3").intValue());
        }
        else
        {
            assertEquals(3, aggregation.get("1").intValue());
            assertEquals(4, aggregation.get("2").intValue());
            assertEquals(3, aggregation.get("3").intValue());
        }
    }

    @Test
    public void sumOfInt()
    {
        this.iterables.each(each -> assertEquals(15L, Iterate.sumOfLong(each, value -> value)));
        long result = Iterate.sumOfInt(FastList.newListWith(2_000_000_000, 2_000_000_000, 2_000_000_000), e -> e);
        assertEquals(6_000_000_000L, result);
        assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfInt(null, null));
    }

    @Test
    public void sumOfLong()
    {
        this.iterables.each(each -> assertEquals(15L, Iterate.sumOfLong(each, Integer::longValue)));
        assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfLong(null, null));
    }

    @Test
    public void sumOfFloat()
    {
        this.iterables.each(each -> assertEquals(15.0d, Iterate.sumOfFloat(each, Integer::floatValue), 0.0d));
        assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfFloat(null, null));
        double result = Iterate.sumOfFloat(FastList.newListWith(2_000_000_000, 2_000_000_000, 2_000_000_000), e -> e);
        assertEquals((double) 6_000_000_000L, result, 0.0d);
    }

    @Test
    public void sumOfDouble()
    {
        this.iterables.each(each -> assertEquals(15.0d, Iterate.sumOfDouble(each, Integer::doubleValue), 0.0d));
        assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfDouble(null, null));
    }

    @Test
    public void sumOfBigDecimal()
    {
        this.iterables.each(each -> assertEquals(new BigDecimal(15), Iterate.sumOfBigDecimal(each, BigDecimal::new)));
        assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfBigDecimal(null, null));
    }

    @Test
    public void sumOfBigInteger()
    {
        this.iterables.each(each -> assertEquals(new BigInteger("15"), Iterate.sumOfBigInteger(each, integer -> new BigInteger(integer.toString()))));
        assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfBigDecimal(null, null));
    }

    @Test
    public void sumByBigDecimal()
    {
        this.iterables.each(each ->
        {
            MutableMap<Integer, BigDecimal> result = Iterate.sumByBigDecimal(each, e -> e % 2, BigDecimal::new);
            assertEquals(new BigDecimal(9), result.get(1));
            assertEquals(new BigDecimal(6), result.get(0));
        });
    }

    @Test
    public void sumByBigInteger()
    {
        this.iterables.each(each ->
        {
            MutableMap<Integer, BigInteger> result = Iterate.sumByBigInteger(each, e -> e % 2, i -> new BigInteger(i.toString()));
            assertEquals(new BigInteger("9"), result.get(1));
            assertEquals(new BigInteger("6"), result.get(0));
        });
    }

    @Test
    public void sumByInt()
    {
        this.iterables.each(each ->
        {
            ObjectLongMap<Integer> result = Iterate.sumByInt(each, i -> i % 2, e -> e);
            assertEquals(9, result.get(1));
            assertEquals(6, result.get(0));
        });
        ObjectLongMap<Integer> map =
                Iterate.sumByInt(FastList.newListWith(2_000_000_000, 2_000_000_001, 2_000_000_000, 2_000_000_001, 2_000_000_000), i -> i % 2, e -> e);
        assertEquals(4_000_000_002L, map.get(1));
        assertEquals(6_000_000_000L, map.get(0));
    }

    @Test
    public void sumByFloat()
    {
        this.iterables.each(each ->
        {
            ObjectDoubleMap<Integer> result = Iterate.sumByFloat(each, f -> f % 2, e -> e);
            assertEquals(9.0d, result.get(1), 0.0);
            assertEquals(6.0d, result.get(0), 0.0);
        });
        ObjectDoubleMap<Integer> map =
                Iterate.sumByFloat(FastList.newListWith(2_000_000_000, 2_000_001, 2_000_000_000, 2_000_001, 2_000_000_000), i -> i % 2, e -> e);
        assertEquals((double) 4_000_002L, map.get(1), 0.00001d);
        assertEquals((double) 6_000_000_000L, map.get(0), 0.00001d);
    }

    @Test
    public void sumByLong()
    {
        this.iterables.each(each ->
        {
            ObjectLongMap<Integer> result = Iterate.sumByLong(each, l -> l % 2, e -> e);
            assertEquals(9, result.get(1));
            assertEquals(6, result.get(0));
        });
    }

    @Test
    public void sumByDouble()
    {
        this.iterables.each(each ->
        {
            ObjectDoubleMap<Integer> result = Iterate.sumByDouble(each, d -> d % 2, e -> e);
            assertEquals(9.0d, result.get(1), 0.0);
            assertEquals(6.0d, result.get(0), 0.0);
        });
    }

    @Test
    public void minBy()
    {
        assertEquals(Integer.valueOf(1), Iterate.minBy(FastList.newListWith(1, 2, 3), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(1), Iterate.minBy(FastList.newListWith(3, 2, 1), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(1), Iterate.minBy(FastList.newListWith(1, 2, 3).asSynchronized(), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(1), Iterate.minBy(FastList.newListWith(3, 2, 1).asSynchronized(), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(1), Iterate.minBy(Arrays.asList(1, 2, 3), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(1), Iterate.minBy(Arrays.asList(3, 2, 1), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(1), Iterate.minBy(new LinkedList<>(Arrays.asList(1, 2, 3)), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(1), Iterate.minBy(new LinkedList<>(Arrays.asList(3, 2, 1)), Functions.getIntegerPassThru()));
    }

    @Test
    public void minByThrowsOnEmpty()
    {
        assertThrows(NoSuchElementException.class, () -> Iterate.minBy(FastList.newList(), Functions.getIntegerPassThru()));
        assertThrows(NoSuchElementException.class, () -> Iterate.minBy(FastList.<Integer>newList().asSynchronized(), Functions.getIntegerPassThru()));
        assertThrows(NoSuchElementException.class, () -> Iterate.minBy(Arrays.asList(), Functions.getIntegerPassThru()));
        assertThrows(NoSuchElementException.class, () -> Iterate.minBy(new LinkedList<>(), Functions.getIntegerPassThru()));
    }

    @Test
    public void minByThrowsOnNull()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.minBy(null, null));
    }

    @Test
    public void maxBy()
    {
        assertEquals(Integer.valueOf(3), Iterate.maxBy(FastList.newListWith(1, 2, 3), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(3), Iterate.maxBy(FastList.newListWith(3, 2, 1), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(3), Iterate.maxBy(FastList.newListWith(1, 2, 3).asSynchronized(), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(3), Iterate.maxBy(FastList.newListWith(3, 2, 1).asSynchronized(), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(3), Iterate.maxBy(Arrays.asList(1, 2, 3), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(3), Iterate.maxBy(Arrays.asList(3, 2, 1), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(3), Iterate.maxBy(new LinkedList<>(Arrays.asList(1, 2, 3)), Functions.getIntegerPassThru()));
        assertEquals(Integer.valueOf(3), Iterate.maxBy(new LinkedList<>(Arrays.asList(3, 2, 1)), Functions.getIntegerPassThru()));
    }

    @Test
    public void maxByThrowsOnEmpty()
    {
        assertThrows(NoSuchElementException.class, () -> Iterate.maxBy(FastList.newList(), Functions.getIntegerPassThru()));
        assertThrows(NoSuchElementException.class, () -> Iterate.maxBy(FastList.<Integer>newList().asSynchronized(), Functions.getIntegerPassThru()));
        assertThrows(NoSuchElementException.class, () -> Iterate.maxBy(Arrays.asList(), Functions.getIntegerPassThru()));
        assertThrows(NoSuchElementException.class, () -> Iterate.maxBy(new LinkedList<>(), Functions.getIntegerPassThru()));
    }

    @Test
    public void maxByThrowsOnNull()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.maxBy(null, null));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(Iterate.class);
    }

    @Test
    public void groupByUniqueKey()
    {
        assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), Iterate.groupByUniqueKey(FastList.newListWith(1, 2, 3), id -> id));
    }

    @Test
    public void groupByUniqueKey_throws_for_null()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.groupByUniqueKey(null, id -> id));
    }

    @Test
    public void groupByUniqueKey_target()
    {
        assertEquals(UnifiedMap.newWithKeysValues(0, 0, 1, 1, 2, 2, 3, 3), Iterate.groupByUniqueKey(FastList.newListWith(1, 2, 3), id -> id, UnifiedMap.newWithKeysValues(0, 0)));
    }

    @Test
    public void groupByUniqueKey_target_throws_for_null()
    {
        assertThrows(IllegalArgumentException.class, () -> Iterate.groupByUniqueKey(null, id -> id, UnifiedMap.newMap()));
    }
}
