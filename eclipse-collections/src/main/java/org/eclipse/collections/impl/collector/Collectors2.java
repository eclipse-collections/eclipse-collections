/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.ImmutableBiMap;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
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
import org.eclipse.collections.api.factory.BiMaps;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.SortedBags;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.factory.Stacks;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.stack.ImmutableStack;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * Collectors2 provides a comprehensive set of {@link Collector} implementations for Eclipse Collections types.
 * This class bridges Java Streams API with Eclipse Collections, enabling seamless integration.
 * <p>
 * This utility class includes collectors for:
 * </p>
 * <ul>
 * <li><b>Collection Converters:</b> to{Immutable}{Sorted}{List/Set/Bag/Map/BiMap/Multimap/Stack}</li>
 * <li><b>Filtering:</b> select, reject, partition</li>
 * <li><b>Transformation:</b> collect, flatCollect, collectBoolean/Byte/Char/Short/Int/Float/Long/Double</li>
 * <li><b>Aggregation:</b> sumBy{Int/Float/Long/Double}, summarizing{BigDecimal/BigInteger}</li>
 * <li><b>Grouping:</b> groupBy, groupByEach, aggregateBy, countBy</li>
 * <li><b>Utility:</b> makeString, zip, chunk</li>
 * </ul>
 * <p><b>Thread Safety:</b> All collectors support parallel streams where applicable.
 * Some collectors are marked as CONCURRENT or UNORDERED for optimized parallel collection.</p>
 * <p><b>Performance:</b> Optimized for Eclipse Collections. Often more efficient than JDK collectors
 * when collecting to Eclipse Collections types.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * List<String> jdkList = Arrays.asList("a", "bb", "ccc");
 *
 * // Collect to Eclipse Collections ImmutableList
 * ImmutableList<String> immutableList = jdkList.stream()
 *     .collect(Collectors2.toImmutableList());
 *
 * // Filter while collecting
 * MutableList<String> longStrings = jdkList.stream()
 *     .collect(Collectors2.select(s -> s.length() > 1));
 *
 * // Transform while collecting
 * ImmutableSet<Integer> lengths = jdkList.stream()
 *     .collect(Collectors2.collect(String::length, Collectors2.toImmutableSet()));
 *
 * // Group by length
 * MutableListMultimap<Integer, String> byLength = jdkList.stream()
 *     .collect(Collectors2.groupBy(String::length));
 *
 * // Create summary statistics for BigDecimal
 * Stream<BigDecimal> prices = Stream.of(new BigDecimal("10.50"), new BigDecimal("20.75"));
 * BigDecimalSummaryStatistics stats = prices
 *     .collect(Collectors2.summarizingBigDecimal(Function.identity()));
 *
 * // Use with RichIterable
 * ImmutableSet<String> set = FastList.newListWith("a", "b", "c")
 *     .reduceInPlace(Collectors2.toImmutableSet());
 * }</pre>
 *
 * @see RichIterable#reduceInPlace(Collector)
 * @see Stream#collect(Collector)
 * @since 8.0
 */
public final class Collectors2
{
    private static final Collector.Characteristics[] EMPTY_CHARACTERISTICS = {};

    private static final Collector<?, ?, String> DEFAULT_MAKE_STRING = Collectors2.makeString(", ");

    private static final Collector<?, ?, ?> DEFAULT_IMMUTABLE_LIST_COLLECTOR = Collector.of(
            Lists.mutable::empty,
            MutableList::add,
            MutableList::withAll,
            MutableList::toImmutable,
            EMPTY_CHARACTERISTICS);

    private static final Collector<?, ?, ?> DEFAULT_MUTABLE_LIST_COLLECTOR = Collector.of(
            Lists.mutable::empty,
            MutableList::add,
            MutableList::withAll,
            EMPTY_CHARACTERISTICS);

    private static final Collector<?, ?, ?> DEFAULT_MUTABLE_SORTED_LIST_COLLECTOR = Collector.of(
            Lists.mutable::empty,
            MutableList::add,
            MutableList::withAll,
            MutableList::sortThis,
            EMPTY_CHARACTERISTICS);

    private static final Collector<?, ?, ?> DEFAULT_IMMUTABLE_SORTED_LIST_COLLECTOR = Collector.of(
            Lists.mutable::empty,
            MutableList::add,
            MutableList::withAll,
            list -> list.sortThis().toImmutable(),
            EMPTY_CHARACTERISTICS);


    private static final Collector<?, ?, ?> DEFAULT_MUTABLE_SET_COLLECTOR = Collector.of(
            Sets.mutable::empty,
            MutableSet::add,
            MutableSet::withAll,
            Collector.Characteristics.UNORDERED);

    private static final Collector<?, ?, ?> DEFAULT_IMMUTABLE_SET_COLLECTOR = Collector.of(
            Sets.mutable::empty,
            MutableSet::add,
            MutableSet::withAll,
            MutableSet::toImmutable,
            Collector.Characteristics.UNORDERED);

    private static final Collector<?, ?, ?> DEFAULT_MUTABLE_SORTED_SET_COLLECTOR = Collector.of(
            SortedSets.mutable::empty,
            MutableSortedSet::add,
            MutableSortedSet::withAll,
            Collector.Characteristics.UNORDERED);

    private static final Collector<?, ?, ?> DEFAULT_IMMUTABLE_SORTED_SET_COLLECTOR = Collector.of(
            SortedSets.mutable::empty,
            MutableSortedSet::add,
            MutableSortedSet::withAll,
            MutableSortedSet::toImmutable,
            Collector.Characteristics.UNORDERED);

    private static final Collector<?, ?, ?> DEFAULT_IMMUTABLE_BAG_COLLECTOR = Collector.of(
            Bags.mutable::empty,
            MutableBag::add,
            MutableBag::withAll,
            MutableBag::toImmutable,
            Collector.Characteristics.UNORDERED);

    private static final Collector<?, ?, ?> DEFAULT_MUTABLE_BAG_COLLECTOR = Collector.of(
            Bags.mutable::empty,
            MutableBag::add,
            MutableBag::withAll,
            Collector.Characteristics.UNORDERED);

    private static final Collector<?, ?, ?> DEFAULT_MUTABLE_SORTED_BAG_COLLECTOR = Collector.of(
            SortedBags.mutable::empty,
            MutableSortedBag::add,
            MutableSortedBag::withAll,
            Collector.Characteristics.UNORDERED);

    private static final Collector<?, ?, ?> DEFAULT_IMMUTABLE_SORTED_BAG_COLLECTOR = Collector.of(
            SortedBags.mutable::empty,
            MutableSortedBag::add,
            MutableSortedBag::withAll,
            MutableSortedBag::toImmutable,
            Collector.Characteristics.UNORDERED);

    private static final Collector<?, ?, ?> DEFAULT_IMMUTABLE_STACK_COLLECTOR = Collector.of(
            Lists.mutable::empty,
            MutableList::add,
            MutableList::withAll,
            Stacks.immutable::ofAll,
            EMPTY_CHARACTERISTICS);

    private static final Collector<?, ?, ?> DEFAULT_MUTABLE_STACK_COLLECTOR = Collector.of(
            Lists.mutable::empty,
            MutableList::add,
            MutableList::withAll,
            Stacks.mutable::ofAll,
            EMPTY_CHARACTERISTICS);

    private Collectors2()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * <p>Returns a String composed of elements separated by ", ".</p>
     * <p>Examples:</p>
     * {@code System.out.println(Interval.oneTo(5).stream().collect(Collectors2.makeString()));}<br>
     * {@code System.out.println(Interval.oneTo(5).reduceInPlace(Collectors2.makeString()));}
     * <p>Prints:</p>
     * <pre>
     * 1, 2, 3, 4, 5
     * 1, 2, 3, 4, 5
     * </pre>
     * <p>
     * Equivalent to using @{@link RichIterable#makeString()}
     * </p>
     * {@code System.out.println(Interval.oneTo(5).makeString());}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, String> makeString()
    {
        return (Collector<T, ?, String>) DEFAULT_MAKE_STRING;
    }

    /**
     * <p>Returns a String composed of elements separated by the specified separator.</p>
     * <p>Examples:</p>
     * {@code System.out.println(Interval.oneTo(5).stream().collect(Collectors2.makeString("")));}<br>
     * {@code System.out.println(Interval.oneTo(5).reduceInPlace(Collectors2.makeString("")));}
     * <p>Prints:</p>
     * <pre>
     * 12345
     * 12345
     * </pre>
     * <p>
     * Equivalent to using @{@link RichIterable#makeString(String)}
     * </p>
     * {@code System.out.println(Interval.oneTo(5).makeString(""));}
     */
    public static <T> Collector<T, ?, String> makeString(CharSequence separator)
    {
        return Collectors2.makeString("", separator, "");
    }

    /**
     * <p>Returns a String composed of elements separated by the specified separator and beginning with start
     * String and ending with end String.</p>
     * <p>Examples:</p>
     * {@code System.out.println(Interval.oneTo(5).stream().collect(Collectors2.makeString("[", ":", "]")));}<br>
     * {@code System.out.println(Interval.oneTo(5).reduceInPlace(Collectors2.makeString("[", ":", "]")));}
     * <p>Prints:</p>
     * <pre>
     * [1:2:3:4:5]
     * [1:2:3:4:5]
     * </pre>
     * <p>
     * Equivalent to using @{@link RichIterable#makeString(String, String, String)}}
     * </p>
     * {@code System.out.println(Interval.oneTo(5).makeString("[", ":", "]"));}
     */
    public static <T> Collector<T, ?, String> makeString(CharSequence start, CharSequence separator, CharSequence end)
    {
        return Collector.of(
                () -> new StringJoiner(separator, start, end),
                (joiner, each) -> joiner.add(String.valueOf(each)),
                StringJoiner::merge,
                StringJoiner::toString,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as a MutableList.</p>
     * <p>Examples:</p>
     * {@code MutableList<Integer> numbers1 = Interval.oneTo(5).stream().collect(Collectors2.toList());}<br>
     * {@code MutableList<Integer> numbers2 = Interval.oneTo(5).reduceInPlace(Collectors2.toList());}
     * <p>
     * Equivalent to using @{@link RichIterable#toList()}}
     * </p>
     * {@code MutableList<Integer> numbers = Interval.oneTo(5).toList();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, MutableList<T>> toList()
    {
        return (Collector<T, ?, MutableList<T>>) DEFAULT_MUTABLE_LIST_COLLECTOR;
    }

    /**
     * <p>Returns the elements as an ImmutableList.</p>
     * <p>Examples:</p>
     * {@code ImmutableList<Integer> numbers1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableList());}<br>
     * {@code ImmutableList<Integer> numbers2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableList());}
     * <p>
     * Equivalent to using @{@link RichIterable#toList()} followed by: @{@link MutableList#toImmutable()}.
     * </p>
     * {@code ImmutableList<Integer> numbers = Interval.oneTo(5).toList().toImmutable();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList()
    {
        return (Collector<T, ?, ImmutableList<T>>) DEFAULT_IMMUTABLE_LIST_COLLECTOR;
    }

    /**
     * <p>Returns the elements as a MutableSet.</p>
     * <p>Examples:</p>
     * {@code MutableSet<Integer> set1 = Interval.oneTo(5).stream().collect(Collectors2.toSet());}<br>
     * {@code MutableSet<Integer> set2 =Interval.oneTo(5).reduceInPlace(Collectors2.toSet());}
     * <p>
     * Equivalent to using @{@link RichIterable#toSet()}}
     * </p>
     * {@code MutableSet<Integer> set = Interval.oneTo(5).toSet();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, MutableSet<T>> toSet()
    {
        return (Collector<T, ?, MutableSet<T>>) DEFAULT_MUTABLE_SET_COLLECTOR;
    }

    /**
     * <p>Returns the elements as an ImmutableSet.</p>
     * <p>Examples:</p>
     * {@code ImmutableSet<Integer> set1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSet());}<br>
     * {@code ImmutableSet<Integer> set2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSet());}
     * <p>
     * Equivalent to using @{@link RichIterable#toSet()} followed by: @{@link MutableSet#toImmutable()}.
     * </p>
     * {@code ImmutableSet<Integer> set = Interval.oneTo(5).toSet().toImmutable();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet()
    {
        return (Collector<T, ?, ImmutableSet<T>>) DEFAULT_IMMUTABLE_SET_COLLECTOR;
    }

    /**
     * <p>Returns the elements as a MutableSortedSet.</p>
     * <p>Examples:</p>
     * {@code MutableSortedSet<Integer> set1 = Interval.oneTo(5).stream().collect(Collectors2.toSortedSet());}<br>
     * {@code MutableSortedSet<Integer> set2 = Interval.oneTo(5).reduceInPlace(Collectors2.toSortedSet());}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedSet()}}.
     * </p>
     * {@code MutableSortedSet<Integer> set = Interval.oneTo(5).toSortedSet();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, MutableSortedSet<T>> toSortedSet()
    {
        return (Collector<T, ?, MutableSortedSet<T>>) DEFAULT_MUTABLE_SORTED_SET_COLLECTOR;
    }

    /**
     * <p>Returns the elements as an ImmutableSortedSet.</p>
     * <p>Examples:</p>
     * {@code ImmutableSortedSet<Integer> set1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedSet());}<br>
     * {@code ImmutableSortedSet<Integer> set2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedSet());}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedSet()} followed by: @{@link MutableSortedSet#toImmutable()}.
     * </p>
     * {@code ImmutableSortedSet<Integer> set = Interval.oneTo(5).toSortedSet().toImmutable();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, ImmutableSortedSet<T>> toImmutableSortedSet()
    {
        return (Collector<T, ?, ImmutableSortedSet<T>>) DEFAULT_IMMUTABLE_SORTED_SET_COLLECTOR;
    }

    /**
     * <p>Returns the elements as a MutableSortedSet using the specified comparator.</p>
     * <p>Examples:</p>
     * {@code MutableSortedSet<Integer> set1 = Interval.oneTo(5).stream().collect(Collectors2.toSortedSet(Comparator.naturalOrder()));}<br>
     * {@code MutableSortedSet<Integer> set2 = Interval.oneTo(5).reduceInPlace(Collectors2.toSortedSet(Comparator.naturalOrder()));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedSet(Comparator)}.
     * </p>
     * {@code MutableSortedSet<Integer> set = Interval.oneTo(5).toSortedSet(Comparator.naturalOrder());}
     */
    public static <T> Collector<T, ?, MutableSortedSet<T>> toSortedSet(Comparator<? super T> comparator)
    {
        return Collector.of(
                () -> SortedSets.mutable.with(comparator),
                MutableSortedSet::add,
                MutableSortedSet::withAll,
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Returns the elements as a MutableSortedSet using the specified function to compare each element.</p>
     * <p>Examples:</p>
     * {@code MutableSortedSet<Integer> set1 = Interval.oneTo(5).stream().collect(Collectors2.toSortedSetBy(Object::toString));}<br>
     * {@code MutableSortedSet<Integer> set2 = Interval.oneTo(5).reduceInPlace(Collectors2.toSortedSetBy(Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedSetBy(Function)}.
     * </p>
     * {@code MutableSortedSet<Integer> set = Interval.oneTo(5).toSortedSetBy(Object::toString);}
     */
    public static <T, V extends Comparable<? super V>> Collector<T, ?, MutableSortedSet<T>> toSortedSetBy(Function<? super T, ? extends V> function)
    {
        return Collectors2.toSortedSet(Comparators.byFunction(function));
    }

    /**
     * <p>Returns the elements as an ImmutableSortedSet using the specified comparator.</p>
     * <p>Examples:</p>
     * {@code ImmutableSortedSet<Integer> set1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedSet(Comparator.naturalOrder()));}<br>
     * {@code ImmutableSortedSet<Integer> set2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedSet(Comparator.naturalOrder()));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedSet(Comparator)} followed by: @{@link MutableSortedSet#toImmutable()}.
     * </p>
     * {@code ImmutableSortedSet<Integer> set = Interval.oneTo(5).toSortedSet(Comparator.naturalOrder()).toImmutable();}
     */
    public static <T> Collector<T, ?, ImmutableSortedSet<T>> toImmutableSortedSet(Comparator<? super T> comparator)
    {
        return Collector.<T, MutableSortedSet<T>, ImmutableSortedSet<T>>of(
                () -> SortedSets.mutable.with(comparator),
                MutableSortedSet::add,
                MutableSortedSet::withAll,
                MutableSortedSet::toImmutable,
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Returns the elements as a ImmutableSortedSet using the specified function to compare each element.</p>
     * <p>Examples:</p>
     * {@code ImmutableSortedSet<Integer> set1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedSetBy(Object::toString));}<br>
     * {@code ImmutableSortedSet<Integer> set2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedSetBy(Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toImmutableSortedSetBy(Function)}.
     * </p>
     * {@code ImmutableSortedSet<Integer> set = Interval.oneTo(5).toImmutableSortedSetBy(Object::toString);}
     */
    public static <T, V extends Comparable<? super V>> Collector<T, ?, ImmutableSortedSet<T>> toImmutableSortedSetBy(Function<? super T, ? extends V> function)
    {
        return Collectors2.toImmutableSortedSet(Comparators.byFunction(function));
    }

    /**
     * <p>Returns the elements as a MutableBag.</p>
     * <p>Examples:</p>
     * {@code MutableBag<Integer> bag1 = Interval.oneTo(5).stream().collect(Collectors2.toBag());}<br>
     * {@code MutableBag<Integer> bag2 = Interval.oneTo(5).reduceInPlace(Collectors2.toBag());}
     * <p>
     * Equivalent to using @{@link RichIterable#toBag()}}
     * </p>
     * {@code MutableBag<Integer> bag = Interval.oneTo(5).toBag();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, MutableBag<T>> toBag()
    {
        return (Collector<T, ?, MutableBag<T>>) Collectors2.DEFAULT_MUTABLE_BAG_COLLECTOR;
    }

    /**
     * <p>Returns the elements as an ImmutableBag.</p>
     * <p>Examples:</p>
     * {@code ImmutableBag<Integer> bag1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableBag());}<br>
     * {@code ImmutableBag<Integer> bag2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableBag());}
     * <p>
     * Equivalent to using @{@link RichIterable#toBag()} followed by: @{@link MutableBag#toImmutable()}.
     * </p>
     * {@code ImmutableBag<Integer> bag = Interval.oneTo(5).toBag().toImmutable();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, ImmutableBag<T>> toImmutableBag()
    {
        return (Collector<T, ?, ImmutableBag<T>>) DEFAULT_IMMUTABLE_BAG_COLLECTOR;
    }

    /**
     * <p>Returns the elements as a MutableList that has been sorted.</p>
     * <p>Examples:</p>
     * {@code MutableList<Integer> list1 = Interval.oneTo(5).stream().collect(Collectors2.toSortedList());}<br>
     * {@code MutableList<Integer> list2 = Interval.oneTo(5).reduceInPlace(Collectors2.toSortedList());}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedList()}}
     * </p>
     * {@code MutableList<Integer> list = Interval.oneTo(5).toSortedList();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, MutableList<T>> toSortedList()
    {
        return (Collector<T, ?, MutableList<T>>) DEFAULT_MUTABLE_SORTED_LIST_COLLECTOR;
    }

    /**
     * <p>Returns the elements as an ImmutableList that has been sorted.</p>
     * <p>Examples:</p>
     * {@code ImmutableList<Integer> list1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedList());}<br>
     * {@code ImmutableList<Integer> list2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedList());}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedList()} followed by: @{@link MutableList#toImmutable()}.
     * </p>
     * {@code ImmutableList<Integer> list = Interval.oneTo(5).toSortedList().toImmutable();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableSortedList()
    {
        return (Collector<T, ?, ImmutableList<T>>) DEFAULT_IMMUTABLE_SORTED_LIST_COLLECTOR;
    }

    /**
     * <p>Returns the elements as a MutableList that has been sorted using the specified comparator.</p>
     * <p>Examples:</p>
     * {@code MutableList<Integer> list1 = Interval.oneTo(5).stream().collect(Collectors2.toSortedList(Comparators.naturalOrder()));}<br>
     * {@code MutableList<Integer> list2 = Interval.oneTo(5).reduceInPlace(Collectors2.toSortedList(Comparators.naturalOrder()));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedList(Comparator)}}
     * </p>
     * {@code MutableList<Integer> list = Interval.oneTo(5).toSortedList(Comparators.naturalOrder());}
     */
    public static <T> Collector<T, ?, MutableList<T>> toSortedList(Comparator<? super T> comparator)
    {
        return Collector.<T, MutableList<T>, MutableList<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                list -> list.sortThis(comparator),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as a MutableList that has been sorted using the specified comparator.</p>
     * <p>Examples:</p>
     * {@code MutableList<Integer> list1 = Interval.oneTo(5).stream().collect(Collectors2.toSortedListBy(Object::toString));}<br>
     * {@code MutableList<Integer> list2 = Interval.oneTo(5).reduceInPlace(Collectors2.toSortedListBy(Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedListBy(Function)}}
     * </p>
     * {@code MutableList<Integer> list = Interval.oneTo(5).toSortedListBy(Object::toString);}
     */
    public static <T, V extends Comparable<? super V>> Collector<T, ?, MutableList<T>> toSortedListBy(Function<? super T, ? extends V> function)
    {
        return Collectors2.toSortedList(Comparators.byFunction(function));
    }

    /**
     * <p>Returns the elements as an ImmutableList that has been sorted using the specified comparator.</p>
     * <p>Examples:</p>
     * {@code ImmutableList<Integer> list1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedList(Comparator.naturalOrder()));}<br>
     * {@code ImmutableList<Integer> list2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedList(Comparator.naturalOrder()));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedList(Comparator)} followed by: @{@link MutableList#toImmutable()}.
     * </p>
     * {@code ImmutableList<Integer> list = Interval.oneTo(5).toSortedList(Comparator.naturalOrder()).toImmutable();}
     */
    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableSortedList(Comparator<? super T> comparator)
    {
        return Collector.<T, MutableList<T>, ImmutableList<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                list -> list.sortThis(comparator).toImmutable(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as a ImmutableList that has been sorted using the specified comparator.</p>
     * <p>Examples:</p>
     * {@code ImmutableList<Integer> list1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedListBy(Object::toString));}<br>
     * {@code ImmutableList<Integer> list2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedListBy(Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toImmutableSortedListBy(Function)}}
     * </p>
     * {@code ImmutableList<Integer> list = Interval.oneTo(5).toImmutableSortedListBy(Object::toString);}
     */
    public static <T, V extends Comparable<? super V>> Collector<T, ?, ImmutableList<T>> toImmutableSortedListBy(Function<? super T, ? extends V> function)
    {
        return Collectors2.toImmutableSortedList(Comparators.byFunction(function));
    }

    /**
     * <p>Returns the elements as a MutableSortedBag.</p>
     * <p>Examples:</p>
     * {@code MutableSortedBag<Integer> bag1 = Interval.oneTo(5).stream().collect(Collectors2.toSortedBag());}<br>
     * {@code MutableSortedBag<Integer> bag2 = Interval.oneTo(5).reduceInPlace(Collectors2.toSortedBag());}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedBag()}}
     * </p>
     * {@code MutableSortedBag<Integer> bag = Interval.oneTo(5).toSortedBag();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, MutableSortedBag<T>> toSortedBag()
    {
        return (Collector<T, ?, MutableSortedBag<T>>) DEFAULT_MUTABLE_SORTED_BAG_COLLECTOR;
    }

    /**
     * <p>Returns the elements as an ImmutableSortedBag.</p>
     * <p>Examples:</p>
     * {@code ImmutableSortedBag<Integer> bag1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedBag());}<br>
     * {@code ImmutableSortedBag<Integer> bag2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedBag());}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedBag()} followed by: @{@link MutableList#toImmutable()}.
     * </p>
     * {@code ImmutableSortedBag<Integer> bag = Interval.oneTo(5).toSortedBag().toImmutable();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, ImmutableSortedBag<T>> toImmutableSortedBag()
    {
        return (Collector<T, ?, ImmutableSortedBag<T>>) DEFAULT_IMMUTABLE_SORTED_BAG_COLLECTOR;
    }

    /**
     * <p>Returns the elements as a MutableSortedBag using the specified comparator.</p>
     * <p>Examples:</p>
     * {@code MutableSortedBag<Integer> bag1 = Interval.oneTo(5).stream().collect(Collectors2.toSortedBag(Comparators.naturalOrder()));}<br>
     * {@code MutableSortedBag<Integer> bag2 = Interval.oneTo(5).reduceInPlace(Collectors2.toSortedBag(Comparators.naturalOrder()));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedBag(Comparator)}
     * </p>
     * {@code MutableSortedBag<Integer> bag = Interval.oneTo(5).toSortedBag(Comparators.naturalOrder());}
     */
    public static <T> Collector<T, ?, MutableSortedBag<T>> toSortedBag(Comparator<? super T> comparator)
    {
        return Collector.of(
                () -> SortedBags.mutable.with(comparator),
                MutableSortedBag::add,
                MutableSortedBag::withAll,
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Returns the elements as a MutableSortedBag using the specified function.</p>
     * <p>Examples:</p>
     * {@code MutableSortedBag<Integer> bag1 = Interval.oneTo(5).stream().collect(Collectors2.toSortedBagBy(Object::toString));}<br>
     * {@code MutableSortedBag<Integer> bag2 = Interval.oneTo(5).reduceInPlace(Collectors2.toSortedBagBy(Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedBagBy(Function)}}
     * </p>
     * {@code MutableSortedBag<Integer> bag = Interval.oneTo(5).toSortedBagBy(Object::toString);}
     */
    public static <T, V extends Comparable<? super V>> Collector<T, ?, MutableSortedBag<T>> toSortedBagBy(Function<? super T, ? extends V> function)
    {
        return Collectors2.toSortedBag(Comparators.byFunction(function));
    }

    /**
     * <p>Returns the elements as an ImmutableSortedBag using the specified comparator.</p>
     * <p>Examples:</p>
     * {@code ImmutableSortedBag<Integer> bag1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedBag(Comparator.naturalOrder()));}<br>
     * {@code ImmutableSortedBag<Integer> bag1 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedBag(Comparator.naturalOrder()));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedBag(Comparator)} followed by: @{@link MutableBag#toImmutable()}.
     * </p>
     * {@code ImmutableSortedBag<Integer> bag = Interval.oneTo(5).toSortedBag(Comparator.naturalOrder()).toImmutable();}
     */
    public static <T> Collector<T, ?, ImmutableSortedBag<T>> toImmutableSortedBag(Comparator<? super T> comparator)
    {
        return Collector.<T, MutableSortedBag<T>, ImmutableSortedBag<T>>of(
                () -> SortedBags.mutable.with(comparator),
                MutableSortedBag::add,
                MutableSortedBag::withAll,
                MutableSortedBag::toImmutable,
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Returns the elements as a ImmutableSortedBag using the specified function.</p>
     * <p>Examples:</p>
     * {@code ImmutableSortedBag<Integer> bag1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedBagBy(Object::toString));}<br>
     * {@code ImmutableSortedBag<Integer> bag2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedBagBy(Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toImmutableSortedBagBy(Function)}}
     * </p>
     * {@code ImmutableSortedBag<Integer> bag = Interval.oneTo(5).toImmutableSortedBagBy(Object::toString);}
     */
    public static <T, V extends Comparable<? super V>> Collector<T, ?, ImmutableSortedBag<T>> toImmutableSortedBagBy(Function<? super T, ? extends V> function)
    {
        return Collectors2.toImmutableSortedBag(Comparators.byFunction(function));
    }

    /**
     * <p>Returns the elements as a MutableStack.</p>
     * <p>Examples:</p>
     * {@code MutableStack<Integer> stack1 = Interval.oneTo(5).stream().collect(Collectors2.toStack());}<br>
     * {@code MutableStack<Integer> stack2 = Interval.oneTo(5).reduceInPlace(Collectors2.toStack());}
     * <p>
     * Equivalent to using @{@link OrderedIterable#toStack()}}
     * </p>
     * {@code MutableStack<Integer> stack = Interval.oneTo(5).toList().toStack();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, MutableStack<T>> toStack()
    {
        return (Collector<T, ?, MutableStack<T>>) DEFAULT_MUTABLE_STACK_COLLECTOR;
    }

    /**
     * <p>Returns the elements as an ImmutableStack.</p>
     * <p>Examples:</p>
     * {@code ImmutableStack<Integer> stack1 = Interval.oneTo(5).stream().collect(Collectors2.toImmutableStack());}<br>
     * {@code ImmutableStack<Integer> stack2 = Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableStack());}
     * <p>
     * Equivalent to using @{@link OrderedIterable#toStack()} followed by: @{@link MutableStack#toImmutable()}}
     * </p>
     * {@code ImmutableStack<Integer> stack = Interval.oneTo(5).toList().toStack().toImmutable();}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, ImmutableStack<T>> toImmutableStack()
    {
        return (Collector<T, ?, ImmutableStack<T>>) DEFAULT_IMMUTABLE_STACK_COLLECTOR;
    }

    /**
     * <p>Returns the elements as a MutableBiMap applying the keyFunction and valueFunction to each element.</p>
     * <p>Examples:</p>
     * {@code BiMap<Integer, String> biMap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toBiMap(Functions.identity(), Object::toString));}<br>
     * {@code BiMap<Integer, String> biMap1 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toBiMap(Functions.identity(), Object::toString));}
     */
    public static <T, K, V> Collector<T, ?, MutableBiMap<K, V>> toBiMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.of(
                BiMaps.mutable::empty,
                (mbm, each) -> mbm.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as an ImmutableBiMap applying the keyFunction and valueFunction to each element.</p>
     * <p>Examples:</p>
     * {@code MutableBiMap<Integer, String> biMap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableBiMap(Functions.identity(), Object::toString));}<br>
     * {@code MutableBiMap<Integer, String> biMap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableBiMap(Functions.identity(), Object::toString));}
     */
    public static <T, K, V> Collector<T, ?, ImmutableBiMap<K, V>> toImmutableBiMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.<T, MutableBiMap<K, V>, ImmutableBiMap<K, V>>of(
                BiMaps.mutable::empty,
                (mbm, each) -> mbm.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                MutableBiMap::toImmutable,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as a MutableMap applying the keyFunction and valueFunction to each element.</p>
     * <p>Examples:</p>
     * {@code MutableMap<Integer, String> map1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toMap(Functions.identity(), Object::toString));}<br>
     * {@code MutableMap<Integer, String> map2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toMap(Functions.identity(), Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toMap(Function, Function)}
     * </p>
     * {@code MutableMap<Integer, String> map = Interval.oneTo(5).toMap(Functions.identity(), Object::toString);}
     */
    public static <T, K, V> Collector<T, ?, MutableMap<K, V>> toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.of(
                Maps.mutable::empty,
                (map, each) -> map.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as a MutableSortedMap that has been sorted after applying the keyFunction and valueFunction to each element.</p>
     * <p>Examples:</p>
     * {@code MutableSortedMap<Integer, String> map1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toSortedMap(Functions.identity(), Object::toString));}<br>
     * {@code MutableSortedMap<Integer, String> map2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toSortedMap(Functions.identity(), Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedMap(Function, Function)}}
     * </p>
     * {@code MutableSortedMap<Integer, String> map = Interval.oneTo(5).toSortedMap(Functions.identity(), Object::toString);}
     *
     * @since 11.0
     */
    public static <T, K, V> Collector<T, ?, MutableSortedMap<K, V>> toSortedMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.of(
                SortedMaps.mutable::empty,
                (map, each) -> map.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as a MutableSortedMap that has been sorted using the specified comparator after applying the keyFunction and valueFunction to each element.</p>
     * <p>Examples:</p>
     * {@code MutableSortedMap<Integer, String> map1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toSortedMap(Comparators.naturalOrder(), Functions.identity(), Object::toString));}<br>
     * {@code MutableSortedMap<Integer, String> map2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toSortedMap(Comparators.naturalOrder(), Functions.identity(), Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedMap(Comparator, Function, Function)} (Comparator)}}
     * </p>
     * {@code MutableSortedMap<Integer, String> map = Interval.oneTo(5).toSortedMap(Comparators.naturalOrder(), Functions.identity(), Object::toString);}
     *
     * @since 11.0
     */
    public static <T, K, V> Collector<T, ?, MutableSortedMap<K, V>> toSortedMap(
            Comparator<? super K> comparator,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.of(
                () -> SortedMaps.mutable.with(comparator),
                (map, each) -> map.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Returns the elements as a MutableSortedMap that has been sorted using the specified comparator after applying the keyFunction and valueFunction to each element.</p>
     * <p>Examples:</p>
     * {@code MutableSortedMap<Integer, String> map1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toSortedMap(Object::toString, Functions.identity(), Object::toString));}<br>
     * {@code MutableSortedMap<Integer, String> map2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toSortedMap(Object::toString, Functions.identity(), Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedMapBy(Function, Function, Function)}
     * </p>
     * {@code MutableSortedMap<Integer, String> map = Interval.oneTo(5).toSortedMapBy(Object::toString, Functions.identity(), Object::toString);}
     *
     * @since 11.0
     */
    public static <T, KK extends Comparable<? super KK>, K, V> Collector<T, ?, MutableSortedMap<K, V>> toSortedMapBy(
            Function<? super K, KK> sortBy,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.toSortedMap(Comparators.byFunction(sortBy), keyFunction, valueFunction);
    }

    /**
     * <p>Returns the elements as an ImmutableMap applying the keyFunction and valueFunction to each element.</p>
     * <p>Examples:</p>
     * {@code ImmutableMap<Integer, String> map1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableMap(Functions.identity(), Object::toString));}<br>
     * {@code ImmutableMap<Integer, String> map2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableMap(Functions.identity(), Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toMap(Function, Function)}
     * </p>
     * {@code ImmutableMap<Integer, String> map = Interval.oneTo(5).toMap(Functions.identity(), Object::toString).toImmutable();}
     */
    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.<T, MutableMap<K, V>, ImmutableMap<K, V>>of(
                Maps.mutable::empty,
                (map, each) -> map.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                MutableMap::toImmutable,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as a ImmutableSortedMap that has been sorted after applying the keyFunction and valueFunction to each element.</p>
     * <p>Examples:</p>
     * {@code ImmutableSortedMap<Integer, String> map1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedMap(Functions.identity(), Object::toString));}<br>
     * {@code ImmutableSortedMap<Integer, String> map2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedMap(Functions.identity(), Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedMap(Function, Function)}} followed by: @{@link MutableSortedMap#toImmutable()}.
     * </p>
     * {@code ImmutableSortedMap<Integer, String> map = Interval.oneTo(5).toSortedMap(Functions.identity(), Object::toString).toImmutable();}
     *
     * @since 11.0
     */
    public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.<T, MutableSortedMap<K, V>, ImmutableSortedMap<K, V>>of(
                SortedMaps.mutable::empty,
                (map, each) -> map.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                MutableSortedMap::toImmutable,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as a ImmutableSortedMap that has been sorted using the specified comparator after applying the keyFunction and valueFunction to each element.</p>
     * <p>Examples:</p>
     * {@code ImmutableSortedMap<Integer, String> map1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedMap(Comparators.naturalOrder(), Functions.identity(), Object::toString));}<br>
     * {@code ImmutableSortedMap<Integer, String> map2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedMap(Comparators.naturalOrder(), Functions.identity(), Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedMap(Comparator, Function, Function)}} followed by: @{@link MutableSortedMap#toImmutable()}.
     * </p>
     * {@code ImmutableSortedMap<Integer, String> map = Interval.oneTo(5).toSortedMap(Comparators.naturalOrder(), Functions.identity(), Object::toString).toImmutable();}
     *
     * @since 11.0
     */
    public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(
            Comparator<? super K> comparator,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.<T, MutableSortedMap<K, V>, ImmutableSortedMap<K, V>>of(
                () -> SortedMaps.mutable.with(comparator),
                (map, each) -> map.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                MutableSortedMap::toImmutable,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as a ImmutableSortedMap that has been sorted using the specified comparator.</p>
     * <p>Examples:</p>
     * {@code ImmutableSortedMap<Integer, String> map1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableSortedMap(Object::toString, Functions.identity(), Object::toString));}<br>
     * {@code ImmutableSortedMap<Integer, String> map2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSortedMap(Object::toString, Functions.identity(), Object::toString));}
     * <p>
     * Equivalent to using @{@link RichIterable#toSortedMapBy(Function, Function, Function)} followed by: @{@link MutableSortedMap#toImmutable()}.
     * </p>
     * {@code ImmutableSortedMap<Integer, String> map = Interval.oneTo(5).toSortedMap(Object::toString, Functions.identity(), Object::toString).toImmutable();}
     *
     * @since 11.0
     */
    public static <T, KK extends Comparable<? super KK>, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMapBy(
            Function<? super K, KK> sortBy,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.toImmutableSortedMap(Comparators.byFunction(sortBy), keyFunction, valueFunction);
    }

    /**
     * Returns the counts of all the values returned by applying the specified function to each
     * item of the Stream.
     *
     * @since 9.1
     */
    public static <T, K> Collector<T, ?, MutableBag<K>> countBy(Function<? super T, ? extends K> function)
    {
        return Collector.of(
                Bags.mutable::empty,
                (bag, each) -> bag.with(function.valueOf(each)),
                MutableBag::withAll,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Same as {@link #countBy(Function)}, except the result of applying the specified function will return a
     * collection of keys for each value.</p>
     *
     * @since 9.2
     */
    public static <T, K> Collector<T, ?, MutableBag<K>> countByEach(Function<? super T, ? extends Iterable<K>> function)
    {
        return Collector.of(
                Bags.mutable::empty,
                (bag, each) -> bag.withAll(function.valueOf(each)),
                MutableBag::withAll,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as an MutableMultimap grouping each element using the specified groupBy Function.</p>
     * <p>Examples:</p>
     * {@code MutableListMultimap<String, Integer> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.groupBy(Object::toString, Multimaps.mutable.list::empty));}<br>
     * {@code MutableListMultimap<String, Integer> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.groupBy(Object::toString, Multimaps.mutable.list::empty));}
     * <p>
     * Equivalent to using @{@link RichIterable#groupBy(Function, MutableMultimap)}
     * </p>
     * {@code MutableListMultimap<String, Integer> multimap = Interval.oneTo(5).groupBy(Object::toString, Multimaps.mutable.list.empty());}
     */
    public static <T, K, R extends MutableMultimap<K, T>> Collector<T, ?, R> groupBy(
            Function<? super T, ? extends K> groupBy,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (map, each) -> map.put(groupBy.valueOf(each), each),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Same as {@link #groupBy(Function, Supplier)}, except the result of evaluating groupBy function will return a
     * collection of keys for each value.</p>
     *
     * <p>Equivalent to using @{@link RichIterable#groupByEach(Function, MutableMultimap)}</p>
     */
    public static <T, K, R extends MutableMultimap<K, T>> Collector<T, ?, R> groupByEach(
            Function<? super T, ? extends Iterable<K>> groupBy,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (map, each) -> groupBy.valueOf(each).forEach(k -> map.put(k, each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Same as {@link #groupBy(Function, Supplier)}, except the result of evaluating groupBy function should return a
     * unique key, or else an exception is thrown.</p>
     *
     * <p>Equivalent to using {@link RichIterable#groupByUniqueKey(Function, MutableMapIterable)}</p>
     */
    public static <T, K, R extends MutableMapIterable<K, T>> Collector<T, ?, R> groupByUniqueKey(
            Function<? super T, ? extends K> groupBy,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (map, each) ->
                {
                    K key = groupBy.valueOf(each);
                    if (map.put(key, each) != null)
                    {
                        throw new IllegalStateException("Key " + key + " already exists in map!");
                    }
                },
                (r1, r2) ->
                {
                    r2.forEachKeyValue((key, value) ->
                    {
                        if (r1.put(key, value) != null)
                        {
                            throw new IllegalStateException("Key " + key + " already exists in map!");
                        }
                    });
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    private static <T, K, A extends MutableMultimap<K, T>, R extends ImmutableMultimap<K, T>> Collector<T, ?, R> groupByImmutable(
            Function<? super T, ? extends K> groupBy,
            Supplier<A> supplier,
            java.util.function.Function<A, R> finisher)
    {
        return Collector.of(
                supplier,
                (map, each) -> map.put(groupBy.valueOf(each), each),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                finisher,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as an MutableMultimap grouping each element using the specified groupBy Function and
     * converting each element to the value returned by applying the specified Function valueFunction.</p>
     * <p>Examples:</p>
     * {@code MutableListMultimap<String, String> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.groupByAndCollect(Object::toString, Object::toString, Multimaps.mutable.list::empty));}<br>
     * {@code MutableListMultimap<String, String> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.groupByAndCollect(Object::toString, Object::toString, Multimaps.mutable.list::empty));}
     */
    public static <T, K, V, R extends MutableMultimap<K, V>> Collector<T, ?, R> groupByAndCollect(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (map, each) -> map.put(groupBy.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    private static <T, K, V, A extends MutableMultimap<K, V>, R extends ImmutableMultimap<K, V>> Collector<T, ?, R> groupByAndCollectImmutable(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction,
            Supplier<A> supplier,
            java.util.function.Function<A, R> finisher)
    {
        return Collector.of(
                supplier,
                (map, each) -> map.put(groupBy.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                finisher,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * Groups the elements using the {@code groupBy} function and all the elements that map to the same key are
     * aggregated together using the {@code aggregator} function. The second parameter, the {@code zeroValueFactory}
     * function, creates the initial value in each aggregation. Aggregate results are allowed to be immutable as they
     * will be replaced in the map.
     */
    public static <T, K, R extends MutableMapIterable<K, T>> Collector<T, ?, R> aggregateBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends T> zeroValueFactory,
            Function2<? super T, ? super T, ? extends T> aggregator,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (map, each) -> map.updateValueWith(groupBy.valueOf(each), zeroValueFactory, aggregator, each),
                (r1, r2) ->
                {
                    r2.forEachKeyValue((key, value) -> r1.updateValueWith(key, zeroValueFactory, aggregator, value));
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns the elements as an MutableListMultimap grouping each element using the specified groupBy Function.</p>
     * <p>Examples:</p>
     * {@code MutableListMultimap<String, Integer> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toListMultimap(Object::toString));}<br>
     * {@code MutableListMultimap<String, Integer> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toListMultimap(Object::toString));}
     */
    public static <T, K> Collector<T, ?, MutableListMultimap<K, T>> toListMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupBy(groupBy, Multimaps.mutable.list::empty);
    }

    /**
     * <p>Returns the elements as an MutableListMultimap grouping each element using the specified groupBy Function and
     * converting each element to the value returned by applying the specified Function valueFunction.</p>
     * <p>Examples:</p>
     * {@code MutableListMultimap<String, String> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toListMultimap(Object::toString, Object::toString));}<br>
     * {@code MutableListMultimap<String, String> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toListMultimap(Object::toString, Object::toString));}
     */
    public static <T, K, V> Collector<T, ?, MutableListMultimap<K, V>> toListMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupByAndCollect(groupBy, valueFunction, Multimaps.mutable.list::empty);
    }

    /**
     * <p>Returns the elements as an MutableSetMultimap grouping each element using the specified groupBy Function.</p>
     * <p>Examples:</p>
     * {@code MutableSetMultimap<String, Integer> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toSetMultimap(Object::toString));}<br>
     * {@code MutableSetMultimap<String, Integer> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toSetMultimap(Object::toString));}
     */
    public static <T, K> Collector<T, ?, MutableSetMultimap<K, T>> toSetMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupBy(groupBy, Multimaps.mutable.set::empty);
    }

    /**
     * <p>Returns the elements as an MutableSetMultimap grouping each element using the specified groupBy Function and
     * converting each element to the value returned by applying the specified Function valueFunction.</p>
     * <p>Examples:</p>
     * {@code MutableSetMultimap<String, String> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toSetMultimap(Object::toString, Object::toString));}<br>
     * {@code MutableSetMultimap<String, String> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toSetMultimap(Object::toString, Object::toString));}
     */
    public static <T, K, V> Collector<T, ?, MutableSetMultimap<K, V>> toSetMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupByAndCollect(groupBy, valueFunction, Multimaps.mutable.set::empty);
    }

    /**
     * <p>Returns the elements as an MutableBagMultimap grouping each element using the specified groupBy Function.</p>
     * <p>Examples:</p>
     * {@code MutableBagMultimap<String, Integer> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toBagMultimap(Object::toString));}<br>
     * {@code MutableBagMultimap<String, Integer> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toBagMultimap(Object::toString));}
     */
    public static <T, K> Collector<T, ?, MutableBagMultimap<K, T>> toBagMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupBy(groupBy, Multimaps.mutable.bag::empty);
    }

    /**
     * <p>Returns the elements as an MutableBagMultimap grouping each element using the specified groupBy Function and
     * converting each element to the value returned by applying the specified Function valueFunction.</p>
     * <p>Examples:</p>
     * {@code MutableBagMultimap<String, String> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toBagMultimap(Object::toString, Object::toString));}<br>
     * {@code MutableBagMultimap<String, String> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toBagMultimap(Object::toString, Object::toString));}
     */
    public static <T, K, V> Collector<T, ?, MutableBagMultimap<K, V>> toBagMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupByAndCollect(groupBy, valueFunction, Multimaps.mutable.bag::empty);
    }

    /**
     * <p>Returns the elements as an ImmutableListMultimap grouping each element using the specified groupBy Function.</p>
     * <p>Examples:</p>
     * {@code ImmutableListMultimap<String, Integer> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableListMultimap(Object::toString));}<br>
     * {@code ImmutableListMultimap<String, Integer> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableListMultimap(Object::toString));}
     */
    public static <T, K> Collector<T, ?, ImmutableListMultimap<K, T>> toImmutableListMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupByImmutable(groupBy, Multimaps.mutable.list::empty, MutableListMultimap::toImmutable);
    }

    /**
     * <p>Returns the elements as an ImmutableListMultimap grouping each element using the specified groupBy Function and
     * converting each element to the value returned by applying the specified Function valueFunction.</p>
     * <p>Examples:</p>
     * {@code ImmutableListMultimap<String, String> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableListMultimap(Object::toString, Object::toString));}<br>
     * {@code ImmutableListMultimap<String, String> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableListMultimap(Object::toString, Object::toString));}
     */
    public static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> toImmutableListMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupByAndCollectImmutable(groupBy, valueFunction, Multimaps.mutable.list::empty, MutableListMultimap::toImmutable);
    }

    /**
     * <p>Returns the elements as an ImmutableSetMultimap grouping each element using the specified groupBy Function.</p>
     * <p>Examples:</p>
     * {@code ImmutableSetMultimap<String, Integer> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableSetMultimap(Object::toString));}<br>
     * {@code ImmutableSetMultimap<String, Integer> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSetMultimap(Object::toString));}
     */
    public static <T, K> Collector<T, ?, ImmutableSetMultimap<K, T>> toImmutableSetMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupByImmutable(groupBy, Multimaps.mutable.set::empty, MutableSetMultimap::toImmutable);
    }

    /**
     * <p>Returns the elements as an ImmutableSetMultimap grouping each element using the specified groupBy Function and
     * converting each element to the value returned by applying the specified Function valueFunction.</p>
     * <p>Examples:</p>
     * {@code ImmutableSetMultimap<String, String> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableSetMultimap(Object::toString, Object::toString));}<br>
     * {@code ImmutableSetMultimap<String, String> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableSetMultimap(Object::toString, Object::toString));}
     */
    public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupByAndCollectImmutable(groupBy, valueFunction, Multimaps.mutable.set::empty, MutableSetMultimap::toImmutable);
    }

    /**
     * <p>Returns the elements as an ImmutableBagMultimap grouping each element using the specified groupBy Function.</p>
     * <p>Examples:</p>
     * {@code ImmutableBagMultimap<String, Integer> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableBagMultimap(Object::toString));}<br>
     * {@code ImmutableBagMultimap<String, Integer> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableBagMultimap(Object::toString));}
     */
    public static <T, K> Collector<T, ?, ImmutableBagMultimap<K, T>> toImmutableBagMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupByImmutable(groupBy, Multimaps.mutable.bag::empty, MutableBagMultimap::toImmutable);
    }

    /**
     * <p>Returns the elements as an ImmutableBagMultimap grouping each element using the specified groupBy Function and
     * converting each element to the value returned by applying the specified Function valueFunction.</p>
     * <p>Examples:</p>
     * {@code ImmutableBagMultimap<String, String> multimap1 =
     * Interval.oneTo(5).stream().collect(Collectors2.toImmutableBagMultimap(Object::toString, Object::toString));}<br>
     * {@code ImmutableBagMultimap<String, String> multimap2 =
     * Interval.oneTo(5).reduceInPlace(Collectors2.toImmutableBagMultimap(Object::toString, Object::toString));}
     */
    public static <T, K, V> Collector<T, ?, ImmutableBagMultimap<K, V>> toImmutableBagMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupByAndCollectImmutable(groupBy, valueFunction, Multimaps.mutable.bag::empty, MutableBagMultimap::toImmutable);
    }

    /**
     * <p>Partitions elements in fixed size chunks.</p>
     * <p>Examples:</p>
     * {@code MutableList<MutableList<Integer>> chunks1 = Interval.oneTo(10).stream().collect(Collectors2.chunk(2));}<br>
     * {@code MutableList<MutableList<Integer>> chunks2 = Interval.oneTo(10).reduceInPlace(Collectors2.chunk(2));}
     * <p>
     * Equivalent to using @{@link RichIterable#chunk(int)}
     * </p>
     * {@code LazyIterable<RichIterable<Integer>> chunks = Interval.oneTo(10).chunk(2);}
     */
    public static <T> Collector<T, ?, MutableList<MutableList<T>>> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }
        return Collector.of(
                Lists.mutable::empty,
                (MutableList<MutableList<T>> batches, T each) ->
                {
                    MutableList<T> batch = batches.getLast();
                    if (batch == null || batch.size() == size)
                    {
                        batch = Lists.mutable.empty();
                        batches.add(batch);
                    }
                    batch.add(each);
                },
                MutableList::withAll,
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a {@code MutableList} formed from this stream of elements and another {@code Iterable} by
     * combining corresponding elements in pairs.</p>
     * <p>If one of the two {@code Iterable}s is longer than the other, its remaining elements are ignored.</p>
     * <p>Examples:</p>
     * {@code MutableList<Pair<Integer, Integer>> zipped1 = Interval.oneTo(10).stream().collect(Collectors2.zip(Interval.oneTo(10)));}<br>
     * {@code MutableList<Pair<Integer, Integer>> zipped2 = Interval.oneTo(10).reduceInPlace(Collectors2.zip(Interval.oneTo(10)));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#zip(Iterable)}
     * </p>
     * {@code LazyIterable<Pair<Integer, Integer>> zip = Interval.oneTo(10).zip(Interval.oneTo(10));}
     */
    public static <T, S> Collector<T, ?, MutableList<Pair<T, S>>> zip(Iterable<S> other)
    {
        Iterator<S> iterator = other.iterator();
        return Collector.of(
                Lists.mutable::empty,
                (list, each) ->
                {
                    if (iterator.hasNext())
                    {
                        list.add(Tuples.pair(each, iterator.next()));
                    }
                },
                (l, r) ->
                {
                    throw new UnsupportedOperationException("Zip not supported in parallel.");
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a {@code MutableList} of pairs formed from this stream of elements its indices.</p>
     * <p>Examples:</p>
     * {@code MutableList<ObjectIntPair<Integer>> zipWithIndex1 = Interval.oneTo(10).stream().collect(Collectors2.zipWithIndex());}<br>
     * {@code MutableList<ObjectIntPair<Integer>> zipWithIndex2 = Interval.oneTo(10).reduceInPlace(Collectors2.zipWithIndex());}
     * <p>
     * Equivalent to using @{@link RichIterable#zipWithIndex()}
     * </p>
     * {@code LazyIterable<Pair<Integer, Integer>> zipWithIndex = Interval.oneTo(10).zipWithIndex();}
     */
    public static <T> Collector<T, ?, MutableList<ObjectIntPair<T>>> zipWithIndex()
    {
        AtomicInteger index = new AtomicInteger(0);
        return Collector.of(
                Lists.mutable::empty,
                (list, each) -> list.add(PrimitiveTuples.pair(each, index.getAndAdd(1))),
                (l, r) ->
                {
                    throw new UnsupportedOperationException("ZipWithIndex not supported in parallel.");
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Groups and sums the values using the two specified functions.</p>
     * <p>Examples:</p>
     * {@code MutableObjectLongMap<Integer> sumBy1 =
     * Interval.oneTo(10).stream().collect(Collectors2.sumByInt(each -> Integer.valueOf(each % 2), Integer::intValue));}<br>
     * {@code MutableObjectLongMap<Integer> sumBy2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.sumByInt(each -> Integer.valueOf(each % 2), Integer::intValue));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#sumByInt(Function, IntFunction)}
     * </p>
     * {@code ObjectLongMap<Integer> sumBy =
     * Interval.oneTo(10).sumByInt(each -> Integer.valueOf(each % 2), Integer::intValue));}<br>
     */
    public static <T, V> Collector<T, ?, MutableObjectLongMap<V>> sumByInt(
            Function<? super T, ? extends V> groupBy,
            IntFunction<? super T> function)
    {
        Function2<MutableObjectLongMap<V>, T, MutableObjectLongMap<V>> accumulator =
                PrimitiveFunctions.sumByIntFunction(groupBy, function);
        return Collector.of(
                ObjectLongMaps.mutable::empty,
                accumulator::value,
                (map1, map2) ->
                {
                    map2.forEachKeyValue(map1::addToValue);
                    return map1;
                },
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Groups and sums the values using the two specified functions.</p>
     * <p>Examples:</p>
     * {@code MutableObjectLongMap<Long> sumBy1 =
     * Interval.oneTo(10).stream().collect(Collectors2.sumByLong(each -> Long.valueOf(each % 2), Integer::longValue));}<br>
     * {@code MutableObjectLongMap<Long> sumBy2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.sumByLong(each -> Long.valueOf(each % 2), Integer::longValue));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#sumByLong(Function, LongFunction)}
     * </p>
     * {@code ObjectLongMap<Long> sumBy =
     * Interval.oneTo(10).sumByLong(each -> Long.valueOf(each % 2), Integer::longValue));}<br>
     */
    public static <T, V> Collector<T, ?, MutableObjectLongMap<V>> sumByLong(
            Function<? super T, ? extends V> groupBy,
            LongFunction<? super T> function)
    {
        Function2<MutableObjectLongMap<V>, T, MutableObjectLongMap<V>> accumulator =
                PrimitiveFunctions.sumByLongFunction(groupBy, function);
        return Collector.of(
                ObjectLongMaps.mutable::empty,
                accumulator::value,
                (map1, map2) ->
                {
                    map2.forEachKeyValue(map1::addToValue);
                    return map1;
                },
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Groups and sums the values using the two specified functions.</p>
     * <p>Examples:</p>
     * {@code MutableObjectDoubleMap<Integer> sumBy1 =
     * Interval.oneTo(10).stream().collect(Collectors2.sumByFloat(each -> ((int)each % 2), Integer::floatValue));}<br>
     * {@code MutableObjectDoubleMap<Integer> sumBy2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.sumByFloat(each -> ((int)each % 2), Integer::floatValue));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#sumByFloat(Function, FloatFunction)}
     * </p>
     * {@code ObjectDoubleMap<Integer> sumBy =
     * Interval.oneTo(10).sumByFloat(each -> ((int)each % 2), Integer::floatValue));}<br>
     */
    public static <T, V> Collector<T, ?, MutableObjectDoubleMap<V>> sumByFloat(
            Function<? super T, ? extends V> groupBy,
            FloatFunction<? super T> function)
    {
        Function2<MutableObjectDoubleMap<V>, T, MutableObjectDoubleMap<V>> accumulator =
                PrimitiveFunctions.sumByFloatFunction(groupBy, function);
        return Collector.of(
                ObjectDoubleMaps.mutable::empty,
                accumulator::value,
                (map1, map2) ->
                {
                    map2.forEachKeyValue(map1::addToValue);
                    return map1;
                },
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Groups and sums the values using the two specified functions.</p>
     * <p>Examples:</p>
     * {@code MutableObjectDoubleMap<Integer> sumBy1 =
     * Interval.oneTo(10).stream().collect(Collectors2.sumByDouble(each -> ((int)each % 2), Integer::doubleValue));}<br>
     * {@code MutableObjectDoubleMap<Integer> sumBy2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.sumByDouble(each -> ((int)each % 2), Integer::doubleValue));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#sumByDouble(Function, DoubleFunction)}
     * </p>
     * {@code ObjectDoubleMap<Integer> sumBy =
     * Interval.oneTo(10).sumByDouble(each -> ((int)each % 2), Integer::doubleValue));}<br>
     */
    public static <T, V> Collector<T, ?, MutableObjectDoubleMap<V>> sumByDouble(
            Function<? super T, ? extends V> groupBy,
            DoubleFunction<? super T> function)
    {
        Function2<MutableObjectDoubleMap<V>, T, MutableObjectDoubleMap<V>> accumulator =
                PrimitiveFunctions.sumByDoubleFunction(groupBy, function);
        return Collector.of(
                ObjectDoubleMaps.mutable::empty,
                accumulator::value,
                (map1, map2) ->
                {
                    map2.forEachKeyValue(map1::addToValue);
                    return map1;
                },
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Groups and sums the values using the two specified functions.</p>
     * <p>Examples:</p>
     * {@code MutableMap<Integer, BigDecimal> sumBy1 =
     * Interval.oneTo(10).stream().collect(Collectors2.sumByBigDecimal(each -> (each.intValue() % 2), BigDecimal::new));}<br>
     * {@code MutableMap<Integer, BigDecimal> sumBy2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.sumByBigDecimal(each -> (each.intValue() % 2), BigDecimal::new));}<br>
     * <p>
     * Equivalent to using @{@link Iterate#sumByBigDecimal(Iterable, Function, Function)}
     * </p>
     * {@code MutableMap<Integer, BigDecimal> sumBy =
     * Iterate.sumByBigDecimal(Interval.oneTo(10), each -> (each.intValue() % 2), BigDecimal::new));}<br>
     *
     * @since 8.1
     */
    public static <T, V> Collector<T, ?, MutableMap<V, BigDecimal>> sumByBigDecimal(
            Function<? super T, ? extends V> groupBy,
            Function<? super T, BigDecimal> function)
    {
        return Collector.of(
                Maps.mutable::empty,
                (map, each) ->
                {
                    V key = groupBy.apply(each);
                    BigDecimal oldValue = map.get(key);
                    BigDecimal valueToAdd = function.valueOf(each);
                    map.put(key, oldValue == null ? valueToAdd : oldValue.add(valueToAdd));
                },
                (map1, map2) ->
                {
                    map2.forEachKeyValue((key, value) ->
                    {
                        BigDecimal oldValue = map1.get(key);
                        map1.put(key, oldValue == null ? value : oldValue.add(value));
                    });
                    return map1;
                },
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Groups and sums the values using the two specified functions.</p>
     * <p>Examples:</p>
     * {@code MutableMap<Integer, BigInteger> sumBy1 =
     * Interval.oneTo(10).stream().collect(Collectors2.sumByBigInteger(each -> (each.intValue() % 2), each -> BigInteger.valueOf(each.longValue())));}<br>
     * {@code MutableMap<Integer, BigInteger> sumBy2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.sumByBigInteger(each -> (each.intValue() % 2), each -> BigInteger.valueOf(each.longValue())));}<br>
     * <p>
     * Equivalent to using @{@link Iterate#sumByBigInteger(Iterable, Function, Function)}
     * </p>
     * {@code MutableMap<Integer, BigInteger> sumBy =
     * Iterate.sumByBigInteger(Interval.oneTo(10), each -> (each.intValue() % 2), each -> BigInteger.valueOf(each.longValue())));}<br>
     *
     * @since 8.1
     */
    public static <T, V> Collector<T, ?, MutableMap<V, BigInteger>> sumByBigInteger(
            Function<? super T, ? extends V> groupBy,
            Function<? super T, BigInteger> function)
    {
        return Collector.of(
                Maps.mutable::empty,
                (map, each) ->
                {
                    V key = groupBy.apply(each);
                    BigInteger oldValue = map.get(key);
                    BigInteger valueToAdd = function.valueOf(each);
                    map.put(key, oldValue == null ? valueToAdd : oldValue.add(valueToAdd));
                },
                (map1, map2) ->
                {
                    map2.forEachKeyValue((key, value) ->
                    {
                        BigInteger oldValue = map1.get(key);
                        map1.put(key, oldValue == null ? value : oldValue.add(value));
                    });
                    return map1;
                },
                Collector.Characteristics.UNORDERED);
    }

    /**
     * <p>Returns all elements of the stream that return true when evaluating the predicate. This method is also
     * commonly called filter. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableList<Integer> evens1 =
     * Interval.oneTo(10).stream().collect(Collectors2.select(e -> e % 2 == 0, Lists.mutable::empty));}<br>
     * {@code MutableList<Integer> evens2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.select(e -> e % 2 == 0, Lists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#select(Predicate, Collection)}
     * </p>
     * {@code MutableList<Integer> evens = Interval.oneTo(10).select(e -> e % 2 == 0, Lists.mutable.empty());}
     */
    public static <T, R extends Collection<T>> Collector<T, ?, R> select(Predicate<? super T> predicate, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) ->
                {
                    if (predicate.accept(each))
                    {
                        collection.add(each);
                    }
                },
                Collectors2.mergeCollections(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns all elements of the stream that return true when evaluating the predicate with the parameter.
     * The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableList<Integer> evens1 =
     * Interval.oneTo(10).stream().collect(Collectors2.selectWith((e, p) -> e % p == 0, 2, Lists.mutable::empty));}<br>
     * {@code MutableList<Integer> evens2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.selectWith((e, p) -> e % p == 0, 2, Lists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#selectWith(Predicate2, Object, Collection)}
     * </p>
     * {@code MutableList<Integer> evens = Interval.oneTo(10).selectWith((e, p) -> e % p == 0, 2, Lists.mutable.empty());}
     */
    public static <T, P, R extends Collection<T>> Collector<T, ?, R> selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) ->
                {
                    if (predicate.accept(each, parameter))
                    {
                        collection.add(each);
                    }
                },
                Collectors2.mergeCollections(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns all elements of the stream that return false when evaluating the predicate. This method is also
     * commonly called filterNot. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableList<Integer> odds1 =
     * Interval.oneTo(10).stream().collect(Collectors2.reject(e -> e % 2 == 0, Lists.mutable::empty));}<br>
     * {@code MutableList<Integer> odds2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.reject(e -> e % 2 == 0, Lists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#reject(Predicate, Collection)}
     * </p>
     * {@code MutableList<Integer> odds = Interval.oneTo(10).reject(e -> e % 2 == 0, Lists.mutable.empty());}
     */
    public static <T, R extends Collection<T>> Collector<T, ?, R> reject(Predicate<? super T> predicate, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) ->
                {
                    if (!predicate.accept(each))
                    {
                        collection.add(each);
                    }
                },
                Collectors2.mergeCollections(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns all elements of the stream that return false when evaluating the predicate with the parameter.
     * The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableList<Integer> odds1 =
     * Interval.oneTo(10).stream().collect(Collectors2.rejectWith((e, p) -> e % p == 0, 2, Lists.mutable::empty));}<br>
     * {@code MutableList<Integer> odds2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.rejectWith((e, p) -> e % p == 0, 2, Lists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#rejectWith(Predicate2, Object, Collection)}
     * </p>
     * {@code MutableList<Integer> odds = Interval.oneTo(10).rejectWith((e, p) -> e % p == 0, 2, Lists.mutable.empty());}
     */
    public static <T, P, R extends Collection<T>> Collector<T, ?, R> rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) ->
                {
                    if (!predicate.accept(each, parameter))
                    {
                        collection.add(each);
                    }
                },
                Collectors2.mergeCollections(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns all elements of the stream split into a PartitionMutableCollection after evaluating the predicate.
     * The new PartitionMutableCollection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code PartitionMutableList<Integer> evensAndOdds1 =
     * Interval.oneTo(10).stream().collect(Collectors2.partition(e -> e % 2 == 0, PartitionFastList::new));}<br>
     * {@code PartitionMutableList<Integer> evensAndOdds2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.partition(e -> e % 2 == 0, PartitionFastList::new));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#partition(Predicate)}
     * </p>
     * {@code PartitionMutableList<Integer> evensAndOdds = Interval.oneTo(10).partition(e -> e % 2 == 0);}
     */
    public static <T, R extends PartitionMutableCollection<T>> Collector<T, ?, R> partition(
            Predicate<? super T> predicate,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (partition, each) ->
                {
                    MutableCollection<T> bucket = predicate.accept(each)
                            ? partition.getSelected()
                            : partition.getRejected();
                    bucket.add(each);
                },
                Collectors2.mergePartitions(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns all elements of the stream split into a PartitionMutableCollection after evaluating the predicate.
     * The new PartitionMutableCollection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code PartitionMutableList<Integer> evensAndOdds1 =
     * Interval.oneTo(10).stream().collect(Collectors2.partitionWith((e, p) -> e % p == 0, 2, PartitionFastList::new));}<br>
     * {@code PartitionMutableList<Integer> evensAndOdds2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.partitionWith((e, p) -> e % p == 0, 2, PartitionFastList::new));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#partitionWith(Predicate2, Object)}
     * </p>
     * {@code PartitionMutableList<Integer> evensAndOdds = Interval.oneTo(10).partitionWith((e, p) -> e % p == 0, 2);}
     */
    public static <T, P, R extends PartitionMutableCollection<T>> Collector<T, ?, R> partitionWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (partition, each) ->
                {
                    MutableCollection<T> bucket =
                            predicate.accept(each, parameter) ? partition.getSelected() : partition.getRejected();
                    bucket.add(each);
                },
                Collectors2.mergePartitions(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new collection with the results of applying the specified function on each element of the source
     * collection. This method is also commonly called transform or map. The new collection is created as the result
     * of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableList<String> collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collect(Object::toString, Lists.mutable::empty));}<br>
     * {@code MutableList<String> collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collect(Object::toString, Lists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collect(Function, Collection)}
     * </p>
     * {@code MutableList<String> collect = Interval.oneTo(10).collect(Object::toString, Lists.mutable.empty());}
     */
    public static <T, V, R extends Collection<V>> Collector<T, ?, R> collect(
            Function<? super T, ? extends V> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.valueOf(each)),
                Collectors2.mergeCollections(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * The method {@code flatCollect} is a special case of {@link #collect(Function, Supplier)}. With {@code collect},
     * when the {@link Function} returns a collection, the result is a collection of collections. {@code flatCollect} outputs
     * a single "flattened" collection instead. This method is commonly called flatMap.
     * <p>Example:</p>
     * <pre>{@code
     * List<MutableList<String>> lists =
     *     Lists.mutable.with(
     *         Lists.mutable.with("a", "b"),
     *         Lists.mutable.with("c", "d"),
     *         Lists.mutable.with("e"));
     *
     * MutableList<String> flattened =
     *     lists.stream().collect(Collectors2.flatCollect(l -> l, Lists.mutable::empty));
     *
     * Assert.assertEquals(Lists.mutable.with("a", "b", "c", "d", "e"), flattened);}</pre>
     */
    public static <T, V, R extends Collection<V>> Collector<T, ?, R> flatCollect(
            Function<? super T, ? extends Iterable<V>> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> Iterate.addAllTo(function.valueOf(each), collection),
                Collectors2.mergeCollections(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new collection with the results of applying the specified function on each element of the source
     * collection with the specified parameter. This method is also commonly called transform or map. The new collection
     * is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableList<Integer> collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collectWith(Integer::sum, Integer.valueOf(10), Lists.mutable::empty));}<br>
     * {@code MutableList<Integer> collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collectWith(Integer::sum, Integer.valueOf(10), Lists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collectWith(Function2, Object, Collection)}
     * </p>
     * {@code MutableList<Integer> collect = Interval.oneTo(10).collectWith(Integer::sum, Integer.valueOf(10), Lists.mutable.empty());}
     */
    public static <T, P, V, R extends Collection<V>> Collector<T, ?, R> collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.value(each, parameter)),
                Collectors2.mergeCollections(),
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new MutableBooleanCollection with the results of applying the specified BooleanFunction on each element
     * of the source. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableBooleanList collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collectBoolean(each -> each % 2 == 0, BooleanLists.mutable::empty));}<br>
     * {@code MutableBooleanList collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collectBoolean(each -> each % 2 == 0, BooleanLists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collectBoolean(BooleanFunction, MutableBooleanCollection)}
     * </p>
     * {@code MutableBooleanList collect =
     * Interval.oneTo(10).collectBoolean(each -> each % 2 == 0, BooleanLists.mutable.empty());}
     */
    public static <T, R extends MutableBooleanCollection> Collector<T, ?, R> collectBoolean(
            BooleanFunction<? super T> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.booleanValueOf(each)),
                (collection1, collection2) ->
                {
                    collection1.addAll(collection2);
                    return collection1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new MutableByteCollection with the results of applying the specified ByteFunction on each element
     * of the source. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableByteList collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collectByte(each -> (byte) (each % Byte.MAX_VALUE), ByteLists.mutable::empty));}<br>
     * {@code MutableByteList collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collectByte(each -> (byte) (each % Byte.MAX_VALUE), ByteLists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collectByte(ByteFunction, MutableByteCollection)}
     * </p>
     * {@code MutableByteList collect =
     * Interval.oneTo(10).collectByte(each -> (byte) (each % Byte.MAX_VALUE), ByteLists.mutable.empty());}
     */
    public static <T, R extends MutableByteCollection> Collector<T, ?, R> collectByte(
            ByteFunction<? super T> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.byteValueOf(each)),
                (collection1, collection2) ->
                {
                    collection1.addAll(collection2);
                    return collection1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new MutableCharCollection with the results of applying the specified CharFunction on each element
     * of the source. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableCharList collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collectChar(each -> (char) (each % Character.MAX_VALUE), CharLists.mutable::empty));}<br>
     * {@code MutableCharList collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collectChar(each -> (char) (each % Character.MAX_VALUE), CharLists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collectChar(CharFunction, MutableCharCollection)}
     * </p>
     * {@code MutableCharList collect =
     * Interval.oneTo(10).collectChar(each -> (char) (each % Character.MAX_VALUE), CharLists.mutable.empty());}
     */
    public static <T, R extends MutableCharCollection> Collector<T, ?, R> collectChar(
            CharFunction<? super T> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.charValueOf(each)),
                (collection1, collection2) ->
                {
                    collection1.addAll(collection2);
                    return collection1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new MutableShortCollection with the results of applying the specified ShortFunction on each element
     * of the source. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableShortList collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collectShort(each -> (short) (each % Short.MAX_VALUE), ShortLists.mutable::empty));}<br>
     * {@code MutableShortList collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collectShort(each -> (short) (each % Short.MAX_VALUE), ShortLists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collectShort(ShortFunction, MutableShortCollection)}
     * </p>
     * {@code MutableShortList collect =
     * Interval.oneTo(10).collectShort(each -> (short) (each % Short.MAX_VALUE), ShortLists.mutable.empty());}
     */
    public static <T, R extends MutableShortCollection> Collector<T, ?, R> collectShort(
            ShortFunction<? super T> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.shortValueOf(each)),
                (collection1, collection2) ->
                {
                    collection1.addAll(collection2);
                    return collection1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new MutableIntCollection with the results of applying the specified IntFunction on each element
     * of the source. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableIntList collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collectInt(each -> each, IntLists.mutable::empty));}<br>
     * {@code MutableIntList collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collectInt(each -> each, IntLists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collectInt(IntFunction, MutableIntCollection)}
     * </p>
     * {@code MutableIntList collect =
     * Interval.oneTo(10).collectInt(each -> each, IntLists.mutable.empty());}
     */
    public static <T, R extends MutableIntCollection> Collector<T, ?, R> collectInt(
            IntFunction<? super T> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.intValueOf(each)),
                (collection1, collection2) ->
                {
                    collection1.addAll(collection2);
                    return collection1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new MutableFloatCollection with the results of applying the specified FloatFunction on each element
     * of the source. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableFloatList collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collectFloat(each -> (float) each, FloatLists.mutable::empty));}<br>
     * {@code MutableFloatList collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collectFloat(each -> (float) each, FloatLists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collectFloat(FloatFunction, MutableFloatCollection)}
     * </p>
     * {@code MutableFloatList collect =
     * Interval.oneTo(10).collectFloat(each -> (float) each, FloatLists.mutable.empty());}
     */
    public static <T, R extends MutableFloatCollection> Collector<T, ?, R> collectFloat(
            FloatFunction<? super T> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.floatValueOf(each)),
                (collection1, collection2) ->
                {
                    collection1.addAll(collection2);
                    return collection1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new MutableLongCollection with the results of applying the specified LongFunction on each element
     * of the source. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableLongList collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collectLong(each -> (long) each, LongLists.mutable::empty));}<br>
     * {@code MutableLongList collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collectLong(each -> (long) each, LongLists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collectLong(LongFunction, MutableLongCollection)}
     * </p>
     * {@code MutableLongList collect =
     * Interval.oneTo(10).collectLong(each -> (long) each, LongLists.mutable.empty());}
     */
    public static <T, R extends MutableLongCollection> Collector<T, ?, R> collectLong(
            LongFunction<? super T> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.longValueOf(each)),
                (collection1, collection2) ->
                {
                    collection1.addAll(collection2);
                    return collection1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * <p>Returns a new MutableDoubleCollection with the results of applying the specified DoubleFunction on each element
     * of the source. The new collection is created as the result of evaluating the provided Supplier.</p>
     * <p>Examples:</p>
     * {@code MutableDoubleList collect1 =
     * Interval.oneTo(10).stream().collect(Collectors2.collectDouble(each -> (double) each, DoubleLists.mutable::empty));}<br>
     * {@code MutableDoubleList collect2 =
     * Interval.oneTo(10).reduceInPlace(Collectors2.collectDouble(each -> (double) each, DoubleLists.mutable::empty));}<br>
     * <p>
     * Equivalent to using @{@link RichIterable#collectDouble(DoubleFunction, MutableDoubleCollection)}
     * </p>
     * {@code MutableDoubleList collect =
     * Interval.oneTo(10).collectDouble(each -> (double) each, DoubleLists.mutable.empty());}
     */
    public static <T, R extends MutableDoubleCollection> Collector<T, ?, R> collectDouble(
            DoubleFunction<? super T> function, Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (collection, each) -> collection.add(function.doubleValueOf(each)),
                (collection1, collection2) ->
                {
                    collection1.addAll(collection2);
                    return collection1;
                },
                EMPTY_CHARACTERISTICS);
    }

    /**
     * Returns a BigDecimalSummaryStatistics applying the specified function to each element of the stream or collection.
     *
     * @since 8.1
     */
    public static <T> Collector<T, ?, BigDecimalSummaryStatistics> summarizingBigDecimal(Function<? super T, BigDecimal> function)
    {
        return Collector.of(
                BigDecimalSummaryStatistics::new,
                (stats, each) -> stats.value(function.apply(each)),
                BigDecimalSummaryStatistics::merge,
                Collector.Characteristics.UNORDERED);
    }

    /**
     * Returns a BigIntegerSummaryStatistics applying the specified function to each element of the stream or collection.
     *
     * @since 8.1
     */
    public static <T> Collector<T, ?, BigIntegerSummaryStatistics> summarizingBigInteger(Function<? super T, BigInteger> function)
    {
        return Collector.of(
                BigIntegerSummaryStatistics::new,
                (stats, each) -> stats.value(function.apply(each)),
                BigIntegerSummaryStatistics::merge,
                Collector.Characteristics.UNORDERED);
    }

    /**
     * Returns a BigDecimal sum applying the specified function to each element of the stream or collection.
     *
     * @since 8.1
     */
    public static <T> Collector<T, ?, BigDecimal> summingBigDecimal(Function<? super T, BigDecimal> function)
    {
        return Collectors.reducing(BigDecimal.ZERO, function, BigDecimal::add);
    }

    /**
     * Returns a BigInteger sum applying the specified function to each element of the stream or collection.
     *
     * @since 8.1
     */
    public static <T> Collector<T, ?, BigInteger> summingBigInteger(Function<? super T, BigInteger> function)
    {
        return Collectors.reducing(BigInteger.ZERO, function, BigInteger::add);
    }

    private static <T, R extends Collection<T>> BinaryOperator<R> mergeCollections()
    {
        return (collection1, collection2) ->
        {
            collection1.addAll(collection2);
            return collection1;
        };
    }

    private static <T, R extends PartitionMutableCollection<T>> BinaryOperator<R> mergePartitions()
    {
        return (partition1, partition2) ->
        {
            partition1.getSelected().addAll(partition2.getSelected());
            partition1.getRejected().addAll(partition2.getRejected());
            return partition1;
        };
    }
}
