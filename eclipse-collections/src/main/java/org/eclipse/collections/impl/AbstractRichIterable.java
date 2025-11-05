/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.BiMaps;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.SortedBags;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.block.procedure.AppendStringProcedure;
import org.eclipse.collections.impl.block.procedure.BiMapCollectProcedure;
import org.eclipse.collections.impl.block.procedure.CollectIfProcedure;
import org.eclipse.collections.impl.block.procedure.CollectProcedure;
import org.eclipse.collections.impl.block.procedure.CountProcedure;
import org.eclipse.collections.impl.block.procedure.FlatCollectProcedure;
import org.eclipse.collections.impl.block.procedure.GroupByUniqueKeyProcedure;
import org.eclipse.collections.impl.block.procedure.InjectIntoProcedure;
import org.eclipse.collections.impl.block.procedure.MapCollectProcedure;
import org.eclipse.collections.impl.block.procedure.MaxByProcedure;
import org.eclipse.collections.impl.block.procedure.MaxComparatorProcedure;
import org.eclipse.collections.impl.block.procedure.MaxProcedure;
import org.eclipse.collections.impl.block.procedure.MinByProcedure;
import org.eclipse.collections.impl.block.procedure.MinComparatorProcedure;
import org.eclipse.collections.impl.block.procedure.MinProcedure;
import org.eclipse.collections.impl.block.procedure.MultimapEachPutProcedure;
import org.eclipse.collections.impl.block.procedure.MultimapPutProcedure;
import org.eclipse.collections.impl.block.procedure.RejectProcedure;
import org.eclipse.collections.impl.block.procedure.SelectProcedure;
import org.eclipse.collections.impl.block.procedure.SumOfDoubleProcedure;
import org.eclipse.collections.impl.block.procedure.SumOfFloatProcedure;
import org.eclipse.collections.impl.block.procedure.SumOfIntProcedure;
import org.eclipse.collections.impl.block.procedure.SumOfLongProcedure;
import org.eclipse.collections.impl.block.procedure.ZipWithIndexProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.InjectIntoDoubleProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.InjectIntoFloatProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.InjectIntoIntProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.InjectIntoLongProcedure;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;
import org.eclipse.collections.impl.utility.internal.IterableIterate;

/**
 * AbstractRichIterable provides a base implementation for various operations on RichIterable collections.
 * This abstract class implements common functionality for filtering, transforming, aggregating, and converting
 * collections using functional programming patterns.
 * <p>
 * This class serves as a foundation for concrete implementations and provides default implementations
 * for most RichIterable operations using internal iteration patterns. Implementations must override
 * the abstract methods defined in RichIterable to provide collection-specific behavior.
 * </p>
 *
 * @param <T> the type of elements in this iterable
 * @since 1.0
 */
public abstract class AbstractRichIterable<T> implements RichIterable<T>
{
    /**
     * Returns {@code true} if this iterable contains the specified object.
     * <p>
     * This implementation uses equality comparison to determine containment.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> iterable = ...;
     * boolean hasApple = iterable.contains("apple");
     * }</pre>
     *
     * @param object the object to check for containment
     * @return {@code true} if this iterable contains the specified object
     */
    @Override
    public boolean contains(Object object)
    {
        return this.anySatisfyWith(Predicates2.equal(), object);
    }

    /**
     * Returns {@code true} if this iterable contains all elements from the specified iterable.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * Iterable<Integer> subset = Lists.immutable.of(1, 2, 3);
     * boolean containsAll = numbers.containsAllIterable(subset);
     * }</pre>
     *
     * @param source the iterable whose elements are to be checked for containment
     * @return {@code true} if this iterable contains all elements from the source
     */
    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return Iterate.allSatisfyWith(source, Predicates2.in(), this);
    }

    /**
     * Returns {@code true} if this iterable contains all the specified arguments.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> fruits = ...;
     * boolean hasAll = fruits.containsAllArguments("apple", "banana", "orange");
     * }</pre>
     *
     * @param elements the elements to check for containment
     * @return {@code true} if this iterable contains all the specified elements
     */
    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return ArrayIterate.allSatisfyWith(elements, Predicates2.in(), this);
    }

    /**
     * Returns {@code true} if this iterable contains no elements.
     * <p>
     * This implementation checks if the size equals zero.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> iterable = ...;
     * if (iterable.isEmpty()) {
     *     System.out.println("No elements found");
     * }
     * }</pre>
     *
     * @return {@code true} if this iterable contains no elements
     */
    @Override
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    /**
     * Converts this iterable to a mutable list containing all elements.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> iterable = ...;
     * MutableList<Integer> list = iterable.toList();
     * list.add(42); // Can modify the result
     * }</pre>
     *
     * @return a new mutable list containing all elements from this iterable
     */
    @Override
    public MutableList<T> toList()
    {
        MutableList<T> list = Lists.mutable.empty();
        this.forEachWith(Procedures2.addToCollection(), list);
        return list;
    }

    /**
     * Converts this iterable to a mutable list sorted by the values returned by the specified function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableList<Person> sortedByAge = people.toSortedListBy(Person::getAge);
     * }</pre>
     *
     * @param <V> the comparable type returned by the function
     * @param function the function to extract the comparable sort key
     * @return a new mutable list containing all elements sorted by the function
     */
    @Override
    public <V extends Comparable<? super V>> MutableList<T> toSortedListBy(Function<? super T, ? extends V> function)
    {
        return this.toSortedList(Comparators.byFunction(function));
    }

    /**
     * Converts this iterable to a mutable sorted set using natural ordering.
     * <p>
     * Elements must implement Comparable. Duplicate elements will be removed.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * MutableSortedSet<Integer> sortedSet = numbers.toSortedSet();
     * }</pre>
     *
     * @return a new mutable sorted set containing unique elements in natural order
     */
    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        MutableSortedSet<T> treeSet = SortedSets.mutable.empty();
        this.forEachWith(Procedures2.addToCollection(), treeSet);
        return treeSet;
    }

    /**
     * Converts this iterable to a mutable sorted set using the specified comparator.
     * <p>
     * Duplicate elements (as determined by the comparator) will be removed.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> strings = ...;
     * MutableSortedSet<String> caseInsensitive =
     *     strings.toSortedSet(String.CASE_INSENSITIVE_ORDER);
     * }</pre>
     *
     * @param comparator the comparator to determine element order
     * @return a new mutable sorted set containing unique elements ordered by the comparator
     */
    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        MutableSortedSet<T> treeSet = SortedSets.mutable.with(comparator);
        this.forEachWith(Procedures2.addToCollection(), treeSet);
        return treeSet;
    }

    /**
     * Converts this iterable to a mutable sorted set sorted by the values returned by the specified function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableSortedSet<Person> sortedByName = people.toSortedSetBy(Person::getName);
     * }</pre>
     *
     * @param <V> the comparable type returned by the function
     * @param function the function to extract the comparable sort key
     * @return a new mutable sorted set containing unique elements sorted by the function
     */
    @Override
    public <V extends Comparable<? super V>> MutableSortedSet<T> toSortedSetBy(Function<? super T, ? extends V> function)
    {
        return this.toSortedSet(Comparators.byFunction(function));
    }

    /**
     * Converts this iterable to a mutable set containing all unique elements.
     * <p>
     * Duplicate elements will be removed.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * MutableSet<String> uniqueWords = words.toSet();
     * }</pre>
     *
     * @return a new mutable set containing unique elements from this iterable
     */
    @Override
    public MutableSet<T> toSet()
    {
        MutableSet<T> set = Sets.mutable.empty();
        this.forEachWith(Procedures2.addToCollection(), set);
        return set;
    }

    /**
     * Converts this iterable to a mutable bag containing all elements with their occurrence counts.
     * <p>
     * A bag maintains element counts, allowing duplicate values.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * MutableBag<String> wordCounts = words.toBag();
     * int countOfApple = wordCounts.occurrencesOf("apple");
     * }</pre>
     *
     * @return a new mutable bag containing all elements with their occurrence counts
     */
    @Override
    public MutableBag<T> toBag()
    {
        MutableBag<T> bag = Bags.mutable.empty();
        this.forEachWith(Procedures2.addToCollection(), bag);
        return bag;
    }

    /**
     * Converts this iterable to a mutable sorted bag using natural ordering.
     * <p>
     * Elements must implement Comparable. The bag maintains occurrence counts and sorts elements.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * MutableSortedBag<Integer> sortedBag = numbers.toSortedBag();
     * }</pre>
     *
     * @return a new mutable sorted bag containing all elements sorted by natural order
     */
    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        MutableSortedBag<T> sortedBag = SortedBags.mutable.empty();
        this.forEachWith(Procedures2.addToCollection(), sortedBag);
        return sortedBag;
    }

    /**
     * Converts this iterable to a mutable sorted bag using the specified comparator.
     * <p>
     * The bag maintains occurrence counts and sorts elements using the comparator.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * MutableSortedBag<String> bag = words.toSortedBag(String.CASE_INSENSITIVE_ORDER);
     * }</pre>
     *
     * @param comparator the comparator to determine element order
     * @return a new mutable sorted bag containing all elements sorted by the comparator
     */
    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        MutableSortedBag<T> sortedBag = SortedBags.mutable.empty(comparator);
        this.forEachWith(Procedures2.addToCollection(), sortedBag);
        return sortedBag;
    }

    /**
     * Converts this iterable to a mutable sorted bag sorted by the values returned by the specified function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableSortedBag<Person> sortedByAge = people.toSortedBagBy(Person::getAge);
     * }</pre>
     *
     * @param <V> the comparable type returned by the function
     * @param function the function to extract the comparable sort key
     * @return a new mutable sorted bag containing all elements sorted by the function
     */
    @Override
    public <V extends Comparable<? super V>> MutableSortedBag<T> toSortedBagBy(Function<? super T, ? extends V> function)
    {
        return this.toSortedBag(Comparators.byFunction(function));
    }

    /**
     * Converts this iterable to a mutable map by applying key and value functions to each element.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableMap<String, Integer> nameToAge =
     *     people.toMap(Person::getName, Person::getAge);
     * }</pre>
     *
     * @param <K> the type of keys in the resulting map
     * @param <V> the type of values in the resulting map
     * @param keyFunction the function to extract the key from each element
     * @param valueFunction the function to extract the value from each element
     * @return a new mutable map containing key-value pairs generated from this iterable
     */
    @Override
    public <K, V> MutableMap<K, V> toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableMap<K, V> map = Maps.mutable.empty();
        this.forEach(new MapCollectProcedure<>(map, keyFunction, valueFunction));
        return map;
    }

    /**
     * Converts this iterable to a mutable sorted map by applying key and value functions to each element.
     * <p>
     * Keys must implement Comparable and will be sorted using natural ordering.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableSortedMap<String, Person> nameToPersonSorted =
     *     people.toSortedMap(Person::getName, Functions.identity());
     * }</pre>
     *
     * @param <K> the type of keys in the resulting map
     * @param <V> the type of values in the resulting map
     * @param keyFunction the function to extract the key from each element
     * @param valueFunction the function to extract the value from each element
     * @return a new mutable sorted map with keys in natural order
     */
    @Override
    public <K, V> MutableSortedMap<K, V> toSortedMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableSortedMap<K, V> sortedMap = SortedMaps.mutable.empty();
        this.forEach(new MapCollectProcedure<>(sortedMap, keyFunction, valueFunction));
        return sortedMap;
    }

    /**
     * Converts this iterable to a mutable sorted map by applying key and value functions to each element.
     * <p>
     * Keys will be sorted using the specified comparator.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableSortedMap<String, Person> caseInsensitiveMap =
     *     people.toSortedMap(String.CASE_INSENSITIVE_ORDER,
     *                        Person::getName,
     *                        Functions.identity());
     * }</pre>
     *
     * @param <K> the type of keys in the resulting map
     * @param <V> the type of values in the resulting map
     * @param comparator the comparator to determine key order
     * @param keyFunction the function to extract the key from each element
     * @param valueFunction the function to extract the value from each element
     * @return a new mutable sorted map with keys ordered by the comparator
     */
    @Override
    public <K, V> MutableSortedMap<K, V> toSortedMap(
            Comparator<? super K> comparator,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableSortedMap<K, V> sortedMap = SortedMaps.mutable.with(comparator);
        this.forEach(new MapCollectProcedure<>(sortedMap, keyFunction, valueFunction));
        return sortedMap;
    }

    /**
     * Converts this iterable to a mutable sorted map sorted by a function applied to the keys.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableSortedMap<Person, Integer> peopleByAge =
     *     people.toSortedMapBy(Person::getName,
     *                          Functions.identity(),
     *                          Person::getAge);
     * }</pre>
     *
     * @param <KK> the comparable type used for sorting
     * @param <NK> the type of keys in the resulting map
     * @param <NV> the type of values in the resulting map
     * @param sortBy the function to extract comparable values from keys for sorting
     * @param keyFunction the function to extract the key from each element
     * @param valueFunction the function to extract the value from each element
     * @return a new mutable sorted map with keys ordered by the sortBy function
     */
    @Override
    public <KK extends Comparable<? super KK>, NK, NV> MutableSortedMap<NK, NV> toSortedMapBy(
            Function<? super NK, KK> sortBy,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.toSortedMap(Comparators.byFunction(sortBy), keyFunction, valueFunction);
    }

    /**
     * Converts this iterable to a mutable bidirectional map by applying key and value functions to each element.
     * <p>
     * A BiMap maintains a one-to-one mapping between keys and values, allowing efficient inverse lookups.
     * Both keys and values must be unique.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableBiMap<String, Integer> nameToId =
     *     people.toBiMap(Person::getName, Person::getId);
     * String name = nameToId.inverse().get(42); // Lookup by ID
     * }</pre>
     *
     * @param <K> the type of keys in the resulting map
     * @param <V> the type of values in the resulting map
     * @param keyFunction the function to extract the key from each element
     * @param valueFunction the function to extract the value from each element
     * @return a new mutable bidirectional map
     */
    @Override
    public <K, V> MutableBiMap<K, V> toBiMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableBiMap<K, V> biMap = BiMaps.mutable.empty();
        this.forEach(new BiMapCollectProcedure<>(biMap, keyFunction, valueFunction));
        return biMap;
    }

    /**
     * Filters this iterable by selecting elements that satisfy the predicate and adds them to the target collection.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * MutableList<Integer> evens = numbers.select(i -> i % 2 == 0, Lists.mutable.empty());
     * }</pre>
     *
     * @param <R> the type of the target collection
     * @param predicate the predicate to evaluate each element
     * @param target the collection to add matching elements to
     * @return the target collection containing elements that satisfy the predicate
     */
    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        this.forEach(new SelectProcedure<>(predicate, target));
        return target;
    }

    /**
     * Filters this iterable by selecting elements that satisfy the two-argument predicate
     * with the specified parameter, and adds them to the target collection.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * MutableList<String> longWords =
     *     words.selectWith((word, len) -> word.length() > len, 5, Lists.mutable.empty());
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param <R> the type of the target collection
     * @param predicate the two-argument predicate to evaluate each element
     * @param parameter the parameter to pass to the predicate
     * @param target the collection to add matching elements to
     * @return the target collection containing elements that satisfy the predicate
     */
    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return this.select(Predicates.bind(predicate, parameter), target);
    }

    /**
     * Filters this iterable by rejecting elements that satisfy the predicate and adds non-matching elements to the target collection.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * MutableList<Integer> odds = numbers.reject(i -> i % 2 == 0, Lists.mutable.empty());
     * }</pre>
     *
     * @param <R> the type of the target collection
     * @param predicate the predicate to evaluate each element
     * @param target the collection to add non-matching elements to
     * @return the target collection containing elements that do not satisfy the predicate
     */
    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        this.forEach(new RejectProcedure<>(predicate, target));
        return target;
    }

    /**
     * Filters this iterable by rejecting elements that satisfy the two-argument predicate
     * with the specified parameter, and adds non-matching elements to the target collection.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * MutableList<String> shortWords =
     *     words.rejectWith((word, len) -> word.length() > len, 5, Lists.mutable.empty());
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param <R> the type of the target collection
     * @param predicate the two-argument predicate to evaluate each element
     * @param parameter the parameter to pass to the predicate
     * @param target the collection to add non-matching elements to
     * @return the target collection containing elements that do not satisfy the predicate
     */
    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return this.reject(Predicates.bind(predicate, parameter), target);
    }

    /**
     * Transforms each element of this iterable using the specified function and adds the results to the target collection.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> names = ...;
     * MutableList<Integer> lengths =
     *     names.collect(String::length, Lists.mutable.empty());
     * }</pre>
     *
     * @param <V> the type of values returned by the function
     * @param <R> the type of the target collection
     * @param function the function to apply to each element
     * @param target the collection to add transformed elements to
     * @return the target collection containing transformed elements
     */
    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        this.forEach(new CollectProcedure<>(function, target));
        return target;
    }

    /**
     * Transforms each element of this iterable using the specified two-argument function
     * with the given parameter, and adds the results to the target collection.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> names = ...;
     * MutableList<String> prefixed =
     *     names.collectWith((name, prefix) -> prefix + name, "Mr. ", Lists.mutable.empty());
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param <V> the type of values returned by the function
     * @param <R> the type of the target collection
     * @param function the two-argument function to apply to each element
     * @param parameter the parameter to pass to the function
     * @param target the collection to add transformed elements to
     * @return the target collection containing transformed elements
     */
    @Override
    public <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R target)
    {
        return this.collect(Functions.bind(function, parameter), target);
    }

    /**
     * Filters elements that satisfy the predicate, transforms them using the function,
     * and adds the results to the target collection.
     * <p>
     * This is equivalent to select(predicate).collect(function) but more efficient.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * MutableList<Integer> longWordLengths =
     *     words.collectIf(word -> word.length() > 5,
     *                     String::length,
     *                     Lists.mutable.empty());
     * }</pre>
     *
     * @param <V> the type of values returned by the function
     * @param <R> the type of the target collection
     * @param predicate the predicate to filter elements
     * @param function the function to apply to filtered elements
     * @param target the collection to add transformed elements to
     * @return the target collection containing transformed elements that passed the filter
     */
    @Override
    public <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        this.forEach(new CollectIfProcedure<>(target, function, predicate));
        return target;
    }

    /**
     * Returns the first element that satisfies the two-argument predicate with the specified parameter,
     * or returns the result of evaluating the specified function if no element is found.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * String found = words.detectWithIfNone(
     *     (word, prefix) -> word.startsWith(prefix),
     *     "pre",
     *     () -> "default");
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param predicate the two-argument predicate to evaluate each element
     * @param parameter the parameter to pass to the predicate
     * @param function the function to evaluate if no element matches
     * @return the first matching element, or the result of the function if none match
     */
    @Override
    public <P> T detectWithIfNone(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Function0<? extends T> function)
    {
        return this.detectIfNone(Predicates.bind(predicate, parameter), function);
    }

    /**
     * Returns the minimum element according to the specified comparator.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * String shortest = words.min(Comparator.comparingInt(String::length));
     * }</pre>
     *
     * @param comparator the comparator to determine the minimum element
     * @return the minimum element according to the comparator
     * @throws NoSuchElementException if the iterable is empty
     */
    @Override
    public T min(Comparator<? super T> comparator)
    {
        MinComparatorProcedure<T> procedure = new MinComparatorProcedure<>(comparator);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Returns the maximum element according to the specified comparator.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * String longest = words.max(Comparator.comparingInt(String::length));
     * }</pre>
     *
     * @param comparator the comparator to determine the maximum element
     * @return the maximum element according to the comparator
     * @throws NoSuchElementException if the iterable is empty
     */
    @Override
    public T max(Comparator<? super T> comparator)
    {
        MaxComparatorProcedure<T> procedure = new MaxComparatorProcedure<>(comparator);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Returns the minimum element using natural ordering.
     * <p>
     * Elements must implement Comparable.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * Integer smallest = numbers.min();
     * }</pre>
     *
     * @return the minimum element using natural ordering
     * @throws NoSuchElementException if the iterable is empty
     * @throws ClassCastException if elements are not Comparable
     */
    @Override
    public T min()
    {
        MinProcedure<T> procedure = new MinProcedure<>();
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Returns the maximum element using natural ordering.
     * <p>
     * Elements must implement Comparable.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * Integer largest = numbers.max();
     * }</pre>
     *
     * @return the maximum element using natural ordering
     * @throws NoSuchElementException if the iterable is empty
     * @throws ClassCastException if elements are not Comparable
     */
    @Override
    public T max()
    {
        MaxProcedure<T> procedure = new MaxProcedure<>();
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Returns the element with the minimum value returned by the specified function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * Person youngest = people.minBy(Person::getAge);
     * }</pre>
     *
     * @param <V> the comparable type returned by the function
     * @param function the function to extract comparable values from elements
     * @return the element with the minimum value according to the function
     * @throws NoSuchElementException if the iterable is empty
     */
    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        MinByProcedure<T, V> minByProcedure = new MinByProcedure<>(function);
        this.forEach(minByProcedure);
        return minByProcedure.getResult();
    }

    /**
     * Returns the element with the maximum value returned by the specified function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * Person oldest = people.maxBy(Person::getAge);
     * }</pre>
     *
     * @param <V> the comparable type returned by the function
     * @param function the function to extract comparable values from elements
     * @return the element with the maximum value according to the function
     * @throws NoSuchElementException if the iterable is empty
     */
    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        MaxByProcedure<T, V> maxByProcedure = new MaxByProcedure<>(function);
        this.forEach(maxByProcedure);
        return maxByProcedure.getResult();
    }

    /**
     * Returns a lazy (deferred) iterable view of this iterable.
     * <p>
     * Operations on the lazy iterable are not evaluated until a terminal operation is called.
     * This allows for efficient chaining of operations without creating intermediate collections.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * LazyIterable<Integer> lazy = numbers.asLazy()
     *     .select(i -> i % 2 == 0)
     *     .collect(i -> i * 2);
     * // No computation happens until a terminal operation like toList() is called
     * MutableList<Integer> result = lazy.toList();
     * }</pre>
     *
     * @return a lazy iterable view of this iterable
     */
    @Override
    public LazyIterable<T> asLazy()
    {
        return LazyIterate.adapt(this);
    }

    /**
     * Transforms each element into an iterable, flattens the results, and adds them to the target collection.
     * <p>
     * This is useful for transforming elements into collections and then flattening them into a single collection.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<List<Integer>> lists = ...;
     * MutableList<Integer> flattened =
     *     lists.flatCollect(list -> list, Lists.mutable.empty());
     * }</pre>
     *
     * @param <V> the type of elements in the resulting collection
     * @param <R> the type of the target collection
     * @param function the function to transform each element into an iterable
     * @param target the collection to add flattened elements to
     * @return the target collection containing all flattened elements
     */
    @Override
    public <V, R extends Collection<V>> R flatCollect(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        this.forEach(new FlatCollectProcedure<>(function, target));
        return target;
    }

    /**
     * Returns the first element that satisfies the predicate, or null if none is found.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * String firstLong = words.detect(word -> word.length() > 10);
     * }</pre>
     *
     * @param predicate the predicate to evaluate each element
     * @return the first element satisfying the predicate, or null if none found
     */
    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return IterableIterate.detect(this, predicate);
    }

    /**
     * Returns the first element that satisfies the two-argument predicate with the specified parameter,
     * or null if none is found.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * String found = words.detectWith((word, prefix) -> word.startsWith(prefix), "pre");
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param predicate the two-argument predicate to evaluate each element
     * @param parameter the parameter to pass to the predicate
     * @return the first element satisfying the predicate, or null if none found
     */
    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.detect(Predicates.bind(predicate, parameter));
    }

    /**
     * Returns an Optional containing the first element that satisfies the predicate,
     * or an empty Optional if none is found.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * Optional<String> firstLong = words.detectOptional(word -> word.length() > 10);
     * firstLong.ifPresent(System.out::println);
     * }</pre>
     *
     * @param predicate the predicate to evaluate each element
     * @return an Optional containing the first matching element, or empty if none found
     */
    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return IterableIterate.detectOptional(this, predicate);
    }

    /**
     * Returns an Optional containing the first element that satisfies the two-argument predicate
     * with the specified parameter, or an empty Optional if none is found.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * Optional<String> found =
     *     words.detectWithOptional((word, prefix) -> word.startsWith(prefix), "pre");
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param predicate the two-argument predicate to evaluate each element
     * @param parameter the parameter to pass to the predicate
     * @return an Optional containing the first matching element, or empty if none found
     */
    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.detectOptional(Predicates.bind(predicate, parameter));
    }

    /**
     * Returns {@code true} if any element satisfies the predicate.
     * <p>
     * Evaluation is short-circuited - returns true as soon as a matching element is found.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * boolean hasEven = numbers.anySatisfy(i -> i % 2 == 0);
     * }</pre>
     *
     * @param predicate the predicate to evaluate each element
     * @return {@code true} if any element satisfies the predicate
     */
    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return IterableIterate.anySatisfy(this, predicate);
    }

    /**
     * Returns {@code true} if all elements satisfy the predicate.
     * <p>
     * Evaluation is short-circuited - returns false as soon as a non-matching element is found.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * boolean allPositive = numbers.allSatisfy(i -> i > 0);
     * }</pre>
     *
     * @param predicate the predicate to evaluate each element
     * @return {@code true} if all elements satisfy the predicate
     */
    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return IterableIterate.allSatisfy(this, predicate);
    }

    /**
     * Returns {@code true} if no elements satisfy the predicate.
     * <p>
     * Evaluation is short-circuited - returns false as soon as a matching element is found.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * boolean noNegatives = numbers.noneSatisfy(i -> i < 0);
     * }</pre>
     *
     * @param predicate the predicate to evaluate each element
     * @return {@code true} if no elements satisfy the predicate
     */
    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return IterableIterate.noneSatisfy(this, predicate);
    }

    /**
     * Returns {@code true} if any element satisfies the two-argument predicate with the specified parameter.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * boolean hasLong = words.anySatisfyWith((word, len) -> word.length() > len, 10);
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param predicate the two-argument predicate to evaluate each element
     * @param parameter the parameter to pass to the predicate
     * @return {@code true} if any element satisfies the predicate
     */
    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.anySatisfy(Predicates.bind(predicate, parameter));
    }

    /**
     * Returns {@code true} if all elements satisfy the two-argument predicate with the specified parameter.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * boolean allLong = words.allSatisfyWith((word, len) -> word.length() > len, 3);
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param predicate the two-argument predicate to evaluate each element
     * @param parameter the parameter to pass to the predicate
     * @return {@code true} if all elements satisfy the predicate
     */
    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.allSatisfy(Predicates.bind(predicate, parameter));
    }

    /**
     * Returns {@code true} if no elements satisfy the two-argument predicate with the specified parameter.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * boolean noShort = words.noneSatisfyWith((word, len) -> word.length() < len, 3);
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param predicate the two-argument predicate to evaluate each element
     * @param parameter the parameter to pass to the predicate
     * @return {@code true} if no elements satisfy the predicate
     */
    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.noneSatisfy(Predicates.bind(predicate, parameter));
    }

    /**
     * Returns the count of elements that satisfy the predicate.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * int evenCount = numbers.count(i -> i % 2 == 0);
     * }</pre>
     *
     * @param predicate the predicate to evaluate each element
     * @return the count of elements satisfying the predicate
     */
    @Override
    public int count(Predicate<? super T> predicate)
    {
        CountProcedure<T> procedure = new CountProcedure<>(predicate);
        this.forEach(procedure);
        return procedure.getCount();
    }

    /**
     * Returns the count of elements that satisfy the two-argument predicate with the specified parameter.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * int longCount = words.countWith((word, len) -> word.length() > len, 5);
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param predicate the two-argument predicate to evaluate each element
     * @param parameter the parameter to pass to the predicate
     * @return the count of elements satisfying the predicate
     */
    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.count(Predicates.bind(predicate, parameter));
    }

    /**
     * Reduces this iterable to a single value by iteratively applying the two-argument function.
     * <p>
     * Also known as fold or reduce. The function takes the accumulated value and the current element,
     * and returns the new accumulated value.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * Integer sum = numbers.injectInto(0, (acc, num) -> acc + num);
     * String concatenated = strings.injectInto("", (acc, str) -> acc + str);
     * }</pre>
     *
     * @param <IV> the type of the injected value and result
     * @param injectedValue the initial value
     * @param function the two-argument function combining accumulated value and current element
     * @return the final accumulated value
     */
    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        InjectIntoProcedure<IV, T> procedure = new InjectIntoProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Reduces this iterable to a single int value by iteratively applying the two-argument function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * int totalLength = words.injectInto(0, (sum, word) -> sum + word.length());
     * }</pre>
     *
     * @param injectedValue the initial int value
     * @param function the function combining accumulated int value and current element
     * @return the final accumulated int value
     */
    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        InjectIntoIntProcedure<T> procedure = new InjectIntoIntProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Reduces this iterable to a single long value by iteratively applying the two-argument function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * long totalLength = words.injectInto(0L, (sum, word) -> sum + word.length());
     * }</pre>
     *
     * @param injectedValue the initial long value
     * @param function the function combining accumulated long value and current element
     * @return the final accumulated long value
     */
    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        InjectIntoLongProcedure<T> procedure = new InjectIntoLongProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Reduces this iterable to a single double value by iteratively applying the two-argument function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Product> products = ...;
     * double totalPrice = products.injectInto(0.0, (sum, p) -> sum + p.getPrice());
     * }</pre>
     *
     * @param injectedValue the initial double value
     * @param function the function combining accumulated double value and current element
     * @return the final accumulated double value
     */
    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        InjectIntoDoubleProcedure<T> procedure = new InjectIntoDoubleProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Adds all elements of this iterable into the target collection.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * Set<String> uniqueWords = words.into(new HashSet<>());
     * }</pre>
     *
     * @param <R> the type of the target collection
     * @param target the collection to add elements to
     * @return the target collection with all elements added
     */
    @Override
    public <R extends Collection<T>> R into(R target)
    {
        return Iterate.addAllTo(this, target);
    }

    /**
     * Reduces this iterable to a single float value by iteratively applying the two-argument function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Product> products = ...;
     * float totalWeight = products.injectInto(0.0f, (sum, p) -> sum + p.getWeight());
     * }</pre>
     *
     * @param injectedValue the initial float value
     * @param function the function combining accumulated float value and current element
     * @return the final accumulated float value
     */
    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        InjectIntoFloatProcedure<T> procedure = new InjectIntoFloatProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Returns the sum of int values extracted from each element by the specified function.
     * <p>
     * The result is returned as a long to avoid overflow.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * long totalLength = words.sumOfInt(String::length);
     * }</pre>
     *
     * @param function the function to extract int values from elements
     * @return the sum of all int values as a long
     */
    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        SumOfIntProcedure<T> procedure = new SumOfIntProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Returns the sum of float values extracted from each element by the specified function.
     * <p>
     * The result is returned as a double for precision.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Product> products = ...;
     * double totalWeight = products.sumOfFloat(Product::getWeight);
     * }</pre>
     *
     * @param function the function to extract float values from elements
     * @return the sum of all float values as a double
     */
    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        SumOfFloatProcedure<T> procedure = new SumOfFloatProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Returns the sum of long values extracted from each element by the specified function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Account> accounts = ...;
     * long totalBalance = accounts.sumOfLong(Account::getBalance);
     * }</pre>
     *
     * @param function the function to extract long values from elements
     * @return the sum of all long values
     */
    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        SumOfLongProcedure<T> procedure = new SumOfLongProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Returns the sum of double values extracted from each element by the specified function.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Product> products = ...;
     * double totalPrice = products.sumOfDouble(Product::getPrice);
     * }</pre>
     *
     * @param function the function to extract double values from elements
     * @return the sum of all double values
     */
    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        SumOfDoubleProcedure<T> procedure = new SumOfDoubleProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    /**
     * Applies the specified procedure to each element along with its index.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * words.forEachWithIndex((word, index) ->
     *     System.out.println(index + ": " + word));
     * }</pre>
     *
     * @param objectIntProcedure the procedure to apply to each element and its index
     */
    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        IterableIterate.forEachWithIndex(this, objectIntProcedure);
    }

    /**
     * Applies the specified procedure to each element of this iterable.
     * <p>
     * This method delegates to the each() method and is final to ensure consistent behavior.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * words.forEach(System.out::println);
     * }</pre>
     *
     * @param procedure the procedure to apply to each element
     */
    @Override
    public final void forEach(Procedure<? super T> procedure)
    {
        this.each(procedure);
    }

    /**
     * Applies the specified two-argument procedure to each element with the given parameter.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * words.forEachWith((word, prefix) ->
     *     System.out.println(prefix + word), ">> ");
     * }</pre>
     *
     * @param <P> the type of the parameter
     * @param procedure the two-argument procedure to apply to each element
     * @param parameter the parameter to pass to the procedure
     */
    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.forEach(Procedures.bind(procedure, parameter));
    }

    /**
     * Zips this iterable with another iterable by combining elements at corresponding positions into pairs,
     * and adds the pairs to the target collection.
     * <p>
     * The iteration stops when either iterable is exhausted.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> names = ...;
     * RichIterable<Integer> ages = ...;
     * MutableList<Pair<String, Integer>> pairs =
     *     names.zip(ages, Lists.mutable.empty());
     * }</pre>
     *
     * @param <S> the type of elements in the other iterable
     * @param <R> the type of the target collection
     * @param that the iterable to zip with
     * @param target the collection to add pairs to
     * @return the target collection containing pairs of corresponding elements
     */
    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return IterableIterate.zip(this, that, target);
    }

    /**
     * Pairs each element with its index and adds the pairs to the target collection.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * MutableList<Pair<String, Integer>> indexed =
     *     words.zipWithIndex(Lists.mutable.empty());
     * }</pre>
     *
     * @param <R> the type of the target collection
     * @param target the collection to add pairs to
     * @return the target collection containing pairs of elements and their indices
     */
    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        this.forEach(ZipWithIndexProcedure.create(target));
        return target;
    }

    /**
     * Returns a string representation of this iterable enclosed in square brackets.
     * <p>
     * Returns a string with the elements of the iterable separated by commas with spaces and
     * enclosed in square brackets.
     *
     * <pre>
     * Assert.assertEquals("[]", Lists.mutable.empty().toString());
     * Assert.assertEquals("[1]", Lists.mutable.with(1).toString());
     * Assert.assertEquals("[1, 2, 3]", Lists.mutable.with(1, 2, 3).toString());
     * </pre>
     *
     * @return a string representation of this collection.
     * @see java.util.AbstractCollection#toString()
     */
    @Override
    public String toString()
    {
        return this.makeString("[", ", ", "]");
    }

    /**
     * Appends a string representation of this iterable to the appendable, with elements separated by the specified separator.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * StringBuilder sb = new StringBuilder();
     * words.appendString(sb, ", ");
     * }</pre>
     *
     * @param appendable the appendable to append to
     * @param separator the separator between elements
     * @throws RuntimeException if an IOException occurs during appending
     */
    @Override
    public void appendString(Appendable appendable, String separator)
    {
        Procedure<T> appendStringProcedure = new AppendStringProcedure<>(appendable, separator);
        this.forEach(appendStringProcedure);
    }

    /**
     * Appends a string representation of this iterable to the appendable, with a start string,
     * elements separated by the specified separator, and an end string.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = ...;
     * StringBuilder sb = new StringBuilder();
     * numbers.appendString(sb, "[", ", ", "]");
     * }</pre>
     *
     * @param appendable the appendable to append to
     * @param start the string to prepend
     * @param separator the separator between elements
     * @param end the string to append
     * @throws RuntimeException if an IOException occurs during appending
     */
    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        Procedure<T> appendStringProcedure = new AppendStringProcedure<>(appendable, separator);
        try
        {
            appendable.append(start);
            this.forEach(appendStringProcedure);
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns {@code true} if this iterable contains all elements in the specified collection.
     * <p>
     * This method delegates to containsAllIterable for consistency.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> words = ...;
     * Collection<String> required = Arrays.asList("apple", "banana");
     * boolean hasAll = words.containsAll(required);
     * }</pre>
     *
     * @param collection the collection whose elements are to be checked for containment
     * @return {@code true} if this iterable contains all elements from the collection
     */
    @Override
    public boolean containsAll(Collection<?> collection)
    {
        return this.containsAllIterable(collection);
    }

    /**
     * Groups and counts elements by applying the function to each element, where the function returns
     * an iterable of group keys. Each element can belong to multiple groups.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> sentences = ...;
     * Bag<String> wordCounts = sentences.countByEach(s -> Arrays.asList(s.split(" ")));
     * }</pre>
     *
     * @param <V> the type of group keys
     * @param function the function to extract group keys from each element
     * @return a bag containing the count of elements in each group
     * @since 10.0.0
     */
    @Override
    public <V> Bag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.countByEach(function, Bags.mutable.empty());
    }

    /**
     * Groups elements by the value returned by the function and adds them to the target multimap.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableMultimap<Integer, Person> byAge =
     *     people.groupBy(Person::getAge, Multimaps.mutable.list.empty());
     * }</pre>
     *
     * @param <V> the type of group keys
     * @param <R> the type of the target multimap
     * @param function the function to extract group keys from elements
     * @param target the multimap to add grouped elements to
     * @return the target multimap containing grouped elements
     */
    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        this.forEach(MultimapPutProcedure.on(target, function));
        return target;
    }

    /**
     * Groups elements by the values returned by the function, where the function returns an iterable of group keys.
     * Each element can belong to multiple groups. Adds the elements to the target multimap.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableMultimap<String, Person> bySkills =
     *     people.groupByEach(Person::getSkills, Multimaps.mutable.list.empty());
     * }</pre>
     *
     * @param <V> the type of group keys
     * @param <R> the type of the target multimap
     * @param function the function to extract group keys from elements
     * @param target the multimap to add grouped elements to
     * @return the target multimap containing grouped elements
     */
    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        this.forEach(MultimapEachPutProcedure.on(target, function));
        return target;
    }

    /**
     * Groups elements by a unique key extracted by the function and adds them to the target map.
     * <p>
     * Each key must be unique; if duplicate keys are found, an exception is thrown.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Person> people = ...;
     * MutableMap<String, Person> byId =
     *     people.groupByUniqueKey(Person::getId, Maps.mutable.empty());
     * }</pre>
     *
     * @param <V> the type of unique keys
     * @param <R> the type of the target map
     * @param function the function to extract unique keys from elements
     * @param target the map to add keyed elements to
     * @return the target map containing elements keyed by unique values
     * @throws IllegalStateException if duplicate keys are found
     */
    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(
            Function<? super T, ? extends V> function,
            R target)
    {
        this.forEach(new GroupByUniqueKeyProcedure<>(target, function));
        return target;
    }
}
