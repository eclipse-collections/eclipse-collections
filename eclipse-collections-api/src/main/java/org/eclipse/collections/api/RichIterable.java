/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.MutableBagIterable;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.ImmutableBiMap;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.factory.SerializableComparators;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
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
import org.eclipse.collections.api.factory.SortedBags;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * RichIterable is a read-only interface which extends the InternalIterable interface and adds many internal iterator methods.
 * The basic methods were inspired by the Smalltalk Collection protocol. These methods include select, reject, detect,
 * collect, injectInto, anySatisfy, allSatisfy. The API also includes converter methods to convert a RichIterable to
 * other mutable and immutable collection types. The converter methods typically have a prefix of "to" (e.g. toList, toSet,
 * toBag, toMap, etc.).
 * <p>
 * The methods in RichIterable are organized into the following method categories via region comments which are collapsible
 * in various IDEs.
 * Links are provided below as a convenience to help discover specific methods in Javadoc.
 *
 * <ul>
 * <li><b>Iterating 🔄</b>
 * <ul><li>
 * {@link #each(Procedure)}, {@link #forEach(Procedure)}, {@link #forEachWith(Procedure2, Object)},
 * {@link #forEachWithIndex(ObjectIntProcedure)}, {@link #tap(Procedure)}, {@link #asLazy()}, {@link #iterator()},
 * {@link #spliterator()}
 * </ul></li>
 * <li><b>Counting 🔢</b>
 * <ul><li>
 * {@link #count(Predicate)} , {@link #countBy(Function)} , {@link #countByEach(Function)},
 * {@link #countByWith(Function2, Object)}, {@link #countWith(Predicate2, Object)} , {@link #size()}
 * </li></ul>
 * <li><b>Testing 🧪</b>
 * <ul><li>
 * {@link #anySatisfy(Predicate)}, {@link #anySatisfyWith(Predicate2, Object)}, {@link #noneSatisfy(Predicate)},
 * {@link #noneSatisfyWith(Predicate2, Object)}, {@link #allSatisfy(Predicate)}, {@link #allSatisfyWith(Predicate2, Object)},
 * {@link #contains(Object)} ,
 * {@link #containsAll(Collection)}, {@link #containsAllArguments(Object...)}, {@link #containsAllIterable(Iterable)},
 * {@link #containsAny(Collection)} , {@link #containsAnyIterable(Iterable)},
 * {@link #containsNone(Collection)} , {@link #containsNoneIterable(Iterable)} ,
 * {@link #containsBy(Function, Object)},
 * {@link #isEmpty()}, {@link #notEmpty()}
 * </li></ul>
 * <li><b>Finding 🔎</b>
 * <ul><li>
 * {@link #detect(Predicate)}, {@link #detectIfNone(Predicate, Function0)} ,
 * {@link #detectWith(Predicate2, Object)}, {@link #detectWithIfNone(Predicate2, Object, Function0)},
 * {@link #detectOptional(Predicate)}, {@link #detectWithOptional(Predicate2, Object)},
 * {@link #getAny()}, {@link #getFirst()}, {@link #getLast()}, {@link #getOnly()},
 * {@link #min()}, {@link #max()},
 * {@link #minBy(Function)}, {@link #minByOptional(Function)}, {@link #minOptional()}
 * {@link #maxBy(Function)}, {@link #maxByOptional(Function)}, {@link #maxOptional()}
 * </li></ul>
 * <li><b>Filtering 🚰</b>
 * <ul><li>
 * {@link #select(Predicate)}, {@link #selectInstancesOf(Class)}, {@link #selectWith(Predicate2, Object)},
 * {@link #reject(Predicate)}, {@link #rejectWith(Predicate2, Object)} ,
 * {@link #partition(Predicate)}, {@link #partitionWith(Predicate2, Object)}
 * </li></ul>
 * <li><b>Transforming 🦋</b>
 * <ul><li>
 * {@link #collect(Function)}, {@link #collectIf(Predicate, Function)}, {@link #collectWith(Function2, Object)},
 * {@link #collectBoolean(BooleanFunction)}, {@link #collectByte(ByteFunction)}, {@link #collectChar(CharFunction)},
 * {@link #collectDouble(DoubleFunction)}, {@link #collectFloat(FloatFunction)}, {@link #collectInt(IntFunction)},
 * {@link #collectLong(LongFunction)}, {@link #collectShort(ShortFunction)},
 * {@link #flatCollect(Function)}, {@link #flatCollectWith(Function2, Object)},
 * {@link #flatCollectBoolean(Function, MutableBooleanCollection)}, {@link #flatCollectByte(Function, MutableByteCollection)} ,
 * {@link #flatCollectChar(Function, MutableCharCollection)}, {@link #flatCollectDouble(Function, MutableDoubleCollection)} ,
 * {@link #flatCollectFloat(Function, MutableFloatCollection)}, {@link #flatCollectInt(Function, MutableIntCollection)} ,
 * {@link #flatCollectLong(Function, MutableLongCollection)}, {@link #flatCollectShort(Function, MutableShortCollection)} ,
 * {@link #zip(Iterable)}, {@link #zipWithIndex(Collection)}
 * </li></ul>
 * <li><b>Grouping 🏘️</b>
 * <ul><li>
 * {@link #chunk(int)} , {@link #groupBy(Function)} , {@link #groupByAndCollect(Function, Function, MutableMultimap)} ,
 * {@link #groupByEach(Function)} , {@link #groupByUniqueKey(Function)}
 * </li></ul>
 * <li><b>Aggregating 📊</b>
 * <ul><li>
 * {@link #aggregateBy(Function, Function0, Function2)} , {@link #aggregateInPlaceBy(Function, Function0, Procedure2)} ,
 * {@link #injectInto(Object, Function2)} ,
 * {@link #injectIntoDouble(double, DoubleObjectToDoubleFunction)} ,
 * {@link #injectIntoFloat(float, FloatObjectToFloatFunction)} , {@link #injectIntoInt(int, IntObjectToIntFunction)} ,
 * {@link #injectIntoLong(long, LongObjectToLongFunction)} ,
 * {@link #reduce(BinaryOperator)}, {@link #reduceBy(Function, Function2)}, {@link #reduceInPlace(Collector)} ,
 * {@link #sumByDouble(Function, DoubleFunction)} , {@link #sumByFloat(Function, FloatFunction)} ,
 * {@link #sumByInt(Function, IntFunction)} , {@link #sumByLong(Function, LongFunction)} ,
 * {@link #sumOfDouble(DoubleFunction)}, {@link #sumOfFloat(FloatFunction)}, {@link #sumOfInt(IntFunction)}, {@link #sumOfLong(LongFunction)} ,
 * {@link #summarizeDouble(DoubleFunction)}, {@link #summarizeFloat(FloatFunction)},
 * {@link #summarizeInt(IntFunction)}, {@link #summarizeLong(LongFunction)}
 * </li></ul>
 * <li><b>Converting 🔌</b>
 * <ul><li>
 * {@link #into(Collection)}, {@link #appendString(Appendable)}, {@link #makeString()}, {@link #toString()},
 * {@link #toArray()}, {@link #toBag()}, {@link #toBiMap(Function, Function)}, {@link #toList()} ,
 * {@link #toMap(Function, Function)}, {@link #toSet()}, {@link #toSortedBag()}, {@link #toSortedBagBy(Function)},
 * {@link #toSortedList()}, {@link #toSortedListBy(Function)}, {@link #toSortedMap(Function, Function)},
 * {@link #toSortedMapBy(Function, Function, Function)}, {@link #toSortedSet()}, {@link #toSortedSetBy(Function)},
 * {@link #toImmutableBag()}, {@link #toImmutableBiMap(Function, Function)}, {@link #toImmutableList()},
 * {@link #toImmutableMap(Function, Function)}, {@link #toImmutableSet()},
 * {@link #toImmutableSortedBag()}, {@link #toImmutableSortedBagBy(Function)}, {@link #toImmutableSortedList()},
 * {@link #toImmutableSortedListBy(Function)} , {@link #toImmutableSortedSet()} , {@link #toImmutableSortedSetBy(Function)}
 * </li></ul>
 * </ul>
 *
 * @since 1.0
 */
@SuppressWarnings("ClassReferencesSubclass")
public interface RichIterable<T>
        extends InternalIterable<T>
{
    //region [Category: Iterating] 🔄

    @Override
    default void forEach(Procedure<? super T> procedure)
    {
        this.each(procedure);
    }

    /**
     * The procedure is executed for each element in the iterable.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * people.each(person -&gt; LOGGER.info(person.getName()));
     * </pre>
     * This method is a variant of {@link InternalIterable#forEach(Procedure)}
     * that has a signature conflict with {@link Iterable#forEach(java.util.function.Consumer)}.
     *
     * @see InternalIterable#forEach(Procedure)
     * @see Iterable#forEach(java.util.function.Consumer)
     * @since 6.0
     */
    @SuppressWarnings("UnnecessaryFullyQualifiedName")
    void each(Procedure<? super T> procedure);

    /**
     * Executes the Procedure for each element in the iterable and returns {@code this}.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; tapped =
     *     people.<b>tap</b>(person -&gt; LOGGER.info(person.getName()));
     * </pre>
     *
     * @see #each(Procedure)
     * @see #forEach(Procedure)
     * @since 6.0
     */
    RichIterable<T> tap(Procedure<? super T> procedure);

    /**
     * Returns a lazy (deferred) iterable that defers evaluation of operations like select, reject, and collect.
     * This allows for efficient chaining of operations without creating intermediate collections.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Operations are not executed until toList() is called
     * MutableList<String> names = people.asLazy()
     *     .select(person -> person.getAge() >= 18)
     *     .collect(Person::getName)
     *     .toList();
     * }</pre>
     *
     * @return a LazyIterable view of this iterable
     * @since 1.0
     */
    LazyIterable<T> asLazy();

    //endregion [Category: Iterating] 🔄

    //region [Category: Counting] 🔢

    /**
     * Returns the number of items in this iterable.
     *
     * @since 1.0
     */
    int size();

    /**
     * Return the total number of elements that answer true to the specified predicate.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * int count =
     *     people.<b>count</b>(person -&gt; person.getAddress().getState().getName().equals("New York"));
     * </pre>
     *
     * @since 1.0
     */
    int count(Predicate<? super T> predicate);

    /**
     * Returns the total number of elements that evaluate to true for the specified predicate.
     *
     * <pre>e.g.
     * return lastNames.<b>countWith</b>(Predicates2.equal(), "Smith");
     * </pre>
     */
    <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * This method will count the number of occurrences of each value calculated by applying the
     * function to each element of the collection.
     *
     * @since 9.0
     */
    default <V> Bag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.countBy(function, Bags.mutable.empty());
    }

    /**
     * This method will count the number of occurrences of each value calculated by applying the
     * function to each element of the collection.
     *
     * @since 9.0
     */
    default <V, R extends MutableBagIterable<V>> R countBy(Function<? super T, ? extends V> function, R target)
    {
        return this.collect(function, target);
    }

    /**
     * This method will count the number of occurrences of each value calculated by applying the
     * function to each element of the collection with the specified parameter as the second argument.
     *
     * @since 9.0
     */
    default <V, P> Bag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.countByWith(function, parameter, Bags.mutable.empty());
    }

    /**
     * This method will count the number of occurrences of each value calculated by applying the
     * function to each element of the collection with the specified parameter as the second argument.
     *
     * @since 9.0
     */
    default <V, P, R extends MutableBagIterable<V>> R countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R target)
    {
        return this.collectWith(function, parameter, target);
    }

    /**
     * This method will count the number of occurrences of each value calculated by applying the
     * function to each element of the collection.
     *
     * @since 10.0.0
     */
    default <V> Bag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.asLazy().flatCollect(function).toBag();
    }

    /**
     * This method will count the number of occurrences of each value calculated by applying the
     * function to each element of the collection.
     *
     * @since 10.0.0
     */
    default <V, R extends MutableBagIterable<V>> R countByEach(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.flatCollect(function, target);
    }

    //endregion [Category: Counting] 🔢

    //region [Category: Testing] 🧪

    /**
     * Returns true if this iterable has zero items.
     *
     * @since 1.0
     */
    boolean isEmpty();

    /**
     * The English equivalent of !this.isEmpty()
     *
     * @since 1.0
     */
    default boolean notEmpty()
    {
        return !this.isEmpty();
    }

    /**
     * Returns true if the iterable has an element which responds true to element.equals(object).
     *
     * @since 1.0
     */
    boolean contains(Object object);

    /**
     * Returns true if the iterable has an element which responds true to element.equals(value)
     * after applying the specified function to the element.
     *
     * @since 10.3
     */
    default <V> boolean containsBy(
            Function<? super T, ? extends V> function,
            V value)
    {
        Objects.requireNonNull(function);
        return this.anySatisfy(each -> Objects.equals(value, function.valueOf(each)));
    }

    /**
     * Returns true if any of the elements in source is contained in this collection.
     *
     * @since 11.1
     */
    default boolean containsAny(Collection<?> source)
    {
        return source instanceof RichIterable ? this.containsAnyIterable(source)
                : source.stream().anyMatch(this::contains);
    }

    /**
     * Returns true if none of the elements in source are contained in this collection.
     *
     * @since 11.1
     */
    default boolean containsNone(Collection<?> source)
    {
        return source instanceof RichIterable ? this.containsNoneIterable(source)
                : source.stream().noneMatch(this::contains);
    }

    /**
     * Returns true if any of the elements in source is contained in this collection.
     *
     * @since 11.1
     */
    default boolean containsAnyIterable(Iterable<?> source)
    {
        if (source instanceof RichIterable)
        {
            RichIterable<?> outside = this;
            RichIterable<?> inside = (RichIterable<?>) source;
            if (this.size() < inside.size())
            {
                outside = inside;
                inside = this;
            }
            if (outside instanceof SetIterable)
            {
                RichIterable<?> temp = outside;
                outside = inside;
                inside = temp;
            }
            else if (inside.size() > 32 && !(inside instanceof SetIterable))
            {
                inside = inside.toSet();
            }
            return outside.anySatisfy(inside::contains);
        }
        return StreamSupport.stream(source.spliterator(), false).anyMatch(this::contains);
    }

    /**
     * Returns true if none of the elements in source are contained in this collection.
     *
     * @since 11.1
     */
    default boolean containsNoneIterable(Iterable<?> source)
    {
        if (source instanceof RichIterable)
        {
            RichIterable<?> outside = this;
            RichIterable<?> inside = (RichIterable<?>) source;
            if (this.size() < inside.size())
            {
                outside = inside;
                inside = this;
            }
            if (outside instanceof SetIterable)
            {
                RichIterable<?> temp = outside;
                outside = inside;
                inside = temp;
            }
            else if (inside.size() > 32 && !(inside instanceof SetIterable))
            {
                inside = inside.toSet();
            }
            return outside.noneSatisfy(inside::contains);
        }
        return StreamSupport.stream(source.spliterator(), false).noneMatch(this::contains);
    }

    /**
     * Returns true if all elements in source are contained in this collection.
     *
     * @since 1.0
     */
    boolean containsAllIterable(Iterable<?> source);

    /**
     * Returns true if all elements in source are contained in this collection.
     *
     * @see Collection#containsAll(Collection)
     * @since 1.0
     */
    boolean containsAll(Collection<?> source);

    /**
     * Returns true if all elements in the specified var arg array are contained in this collection.
     *
     * @since 1.0
     */
    boolean containsAllArguments(Object... elements);

    /**
     * Returns true if the predicate evaluates to true for any element of the iterable.
     * Returns false if the iterable is empty, or if no element returned true when evaluating the predicate.
     *
     * @since 1.0
     */
    boolean anySatisfy(Predicate<? super T> predicate);

    /**
     * Returns true if the predicate evaluates to true for any element of the collection, or return false.
     * Returns false if the collection is empty.
     *
     * @since 5.0
     */
    <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns true if the predicate evaluates to true for every element of the iterable or if the iterable is empty.
     * Otherwise, returns false.
     *
     * @since 1.0
     */
    boolean allSatisfy(Predicate<? super T> predicate);

    /**
     * Returns true if the predicate evaluates to true for every element of the collection, or returns false.
     *
     * @since 5.0
     */
    <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns true if the predicate evaluates to false for every element of the iterable or if the iterable is empty.
     * Otherwise, returns false.
     *
     * @since 3.0
     */
    boolean noneSatisfy(Predicate<? super T> predicate);

    /**
     * Returns true if the predicate evaluates to false for every element of the collection, or return false.
     * Returns true if the collection is empty.
     *
     * @since 5.0
     */
    <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter);

    //endregion [Category: Testing] 🧪

    //region [Category: Finding] 🔎

    /**
     * Returns any element of an iterable.
     *
     * @return an element of an iterable.
     * @since 10.0
     */
    default T getAny()
    {
        return this.getFirst();
    }

    /**
     * Returns the first element of an iterable. In the case of a List it is the element at the first index. In the
     * case of any other Collection, it is the first element that would be returned during an iteration. If the
     * iterable is empty, null is returned. If null is a valid element of the container, then a developer would need to
     * check to see if the iterable is empty to validate that a null result was not due to the container being empty.
     * <p>
     * The order of Sets are not guaranteed (except for TreeSets and other Ordered Set implementations), so if you use
     * this method, the first element could be any element from the Set.
     *
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#getFirst()} instead.
     */
    @Deprecated
    T getFirst();

    /**
     * Returns the last element of an iterable. In the case of a List it is the element at the last index. In the case
     * of any other Collection, it is the last element that would be returned during an iteration. If the iterable is
     * empty, null is returned. If null is a valid element of the container, then a developer would need to check to
     * see if the iterable is empty to validate that a null result was not due to the container being empty.
     * <p>
     * The order of Sets are not guaranteed (except for TreeSets and other Ordered Set implementations), so if you use
     * this method, the last element could be any element from the Set.
     *
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#getLast()} instead.
     */
    @Deprecated
    T getLast();

    /**
     * Returns the element if the iterable has exactly one element. Otherwise, throw {@link IllegalStateException}.
     *
     * @return an element of an iterable.
     * @throws IllegalStateException if iterable is empty or has multiple elements.
     * @since 8.0
     */
    default T getOnly()
    {
        if (this.size() == 1)
        {
            return this.getFirst();
        }

        throw new IllegalStateException("Size must be 1 but was " + this.size());
    }

    /**
     * Returns the first element of the iterable for which the predicate evaluates to true or null in the case where no
     * element returns true. This method is commonly called find.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * Person person =
     *     people.detect(person -&gt; person.getFirstName().equals("John") &amp;&amp; person.getLastName().equals("Smith"));
     * </pre>
     *
     * @since 1.0
     */
    T detect(Predicate<? super T> predicate);

    /**
     * Returns the first element that evaluates to true for the specified predicate2 and parameter, or null if none
     * evaluate to true.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * Person person =
     *     people.detectWith((person, fullName) -&gt; person.getFullName().equals(fullName), "John Smith");
     * </pre>
     *
     * @since 5.0
     */
    <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns the first element of the iterable for which the predicate evaluates to true as an Optional. This method is commonly called find.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * Person person =
     *     people.detectOptional(person -&gt; person.getFirstName().equals("John") &amp;&amp; person.getLastName().equals("Smith"));
     * </pre>
     * <p>
     *
     * @throws NullPointerException if the element selected is null
     * @since 8.0
     */
    Optional<T> detectOptional(Predicate<? super T> predicate);

    /**
     * Returns the first element that evaluates to true for the specified predicate2 and parameter as an Optional.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * Optional&lt;Person&gt; person =
     *     people.detectWithOptional((person, fullName) -&gt; person.getFullName().equals(fullName), "John Smith");
     * </pre>
     * <p>
     *
     * @throws NullPointerException if the element selected is null
     * @since 8.0
     */
    <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns the first element of the iterable for which the predicate evaluates to true. If no element matches
     * the predicate, then returns the value of applying the specified function.
     *
     * @since 1.0
     */
    default T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        T result = this.detect(predicate);
        return result == null ? function.value() : result;
    }

    /**
     * Returns the first element of the iterable that evaluates to true for the specified predicate2 and parameter, or
     * returns the value of evaluating the specified function.
     *
     * @since 5.0
     */
    <P> T detectWithIfNone(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Function0<? extends T> function);

    /**
     * Returns the minimum element out of this container based on the comparator.
     *
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    T min(Comparator<? super T> comparator);

    /**
     * Returns the maximum element out of this container based on the comparator.
     *
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    T max(Comparator<? super T> comparator);

    /**
     * Returns the minimum element out of this container based on the comparator as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws NullPointerException if the minimum element is null
     * @since 8.2
     */
    default Optional<T> minOptional(Comparator<? super T> comparator)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.min(comparator));
    }

    /**
     * Returns the maximum element out of this container based on the comparator as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws NullPointerException if the maximum element is null
     * @since 8.2
     */
    default Optional<T> maxOptional(Comparator<? super T> comparator)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.max(comparator));
    }

    /**
     * Returns the minimum element out of this container based on the natural order.
     *
     * @throws ClassCastException     if the elements are not {@link Comparable}
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    T min();

    /**
     * Returns the maximum element out of this container based on the natural order.
     *
     * @throws ClassCastException     if the elements are not {@link Comparable}
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    T max();

    /**
     * Returns the minimum element out of this container based on the natural order as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws ClassCastException   if the elements are not {@link Comparable}
     * @throws NullPointerException if the minimum element is null
     * @since 8.2
     */
    default Optional<T> minOptional()
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.min());
    }

    /**
     * Returns the maximum element out of this container based on the natural order as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws ClassCastException   if the elements are not {@link Comparable}
     * @throws NullPointerException if the maximum element is null
     * @since 8.2
     */
    default Optional<T> maxOptional()
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.max());
    }

    /**
     * Returns the minimum elements out of this container based on the natural order of the attribute returned by Function.
     *
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function);

    /**
     * Returns the maximum elements out of this container based on the natural order of the attribute returned by Function.
     *
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function);

    /**
     * Returns the minimum elements out of this container based on the natural order of the attribute returned by Function as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws NullPointerException if the minimum element is null
     * @since 8.2
     */
    default <V extends Comparable<? super V>> Optional<T> minByOptional(Function<? super T, ? extends V> function)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.minBy(function));
    }

    /**
     * Returns the maximum elements out of this container based on the natural order of the attribute returned by Function as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws NullPointerException if the maximum element is null
     * @since 8.2
     */
    default <V extends Comparable<? super V>> Optional<T> maxByOptional(Function<? super T, ? extends V> function)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.maxBy(function));
    }

    //endregion [Category: Finding] 🔎

    //region [Category: Filtering] 🚰

    /**
     * Returns all elements of the source collection that return true when evaluating the predicate. This method is also
     * commonly called filter.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; selected =
     *     people.<b>select</b>(person -&gt; person.getAddress().getCity().equals("London"));
     * </pre>
     *
     * @since 1.0
     */
    RichIterable<T> select(Predicate<? super T> predicate);

    /**
     * Same as the select method with one parameter but uses the specified target collection for the results.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;Person&gt; selected =
     *     people.select(person -&gt; person.person.getLastName().equals("Smith"), Lists.mutable.empty());
     * </pre>
     * <p>
     *
     * @param predicate a {@link Predicate} to use as the select criteria
     * @param target    the Collection to append to for all elements in this {@code RichIterable} that meet select criteria {@code predicate}
     * @return {@code target}, which contains appended elements as a result of the select criteria
     * @see #select(Predicate)
     * @since 1.0
     */
    <R extends Collection<T>> R select(Predicate<? super T> predicate, R target);

    /**
     * Similar to {@link #select(Predicate)}, except with an evaluation parameter for the second generic argument in {@link Predicate2}.
     * <p>
     * E.g. return a {@link Collection} of Person elements where the person has an age <b>greater than or equal to</b> 18 years
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; selected =
     *     people.selectWith((Person person, Integer age) -&gt; person.getAge()&gt;= age, Integer.valueOf(18));
     * </pre>
     *
     * @param predicate a {@link Predicate2} to use as the select criteria
     * @param parameter a parameter to pass in for evaluation of the second argument {@code P} in {@code predicate}
     * @see #select(Predicate)
     * @since 5.0
     */
    <P> RichIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Similar to {@link #select(Predicate, Collection)}, except with an evaluation parameter for the second generic argument in {@link Predicate2}.
     * <p>
     * E.g. return a {@link Collection} of Person elements where the person has an age <b>greater than or equal to</b> 18 years
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;Person&gt; selected =
     *     people.selectWith((Person person, Integer age) -&gt; person.getAge()&gt;= age, Integer.valueOf(18), Lists.mutable.empty());
     * </pre>
     *
     * @param predicate        a {@link Predicate2} to use as the select criteria
     * @param parameter        a parameter to pass in for evaluation of the second argument {@code P} in {@code predicate}
     * @param targetCollection the Collection to append to for all elements in this {@code RichIterable} that meet select criteria {@code predicate}
     * @return {@code targetCollection}, which contains appended elements as a result of the select criteria
     * @see #select(Predicate)
     * @see #select(Predicate, Collection)
     * @since 1.0
     */
    <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection);

    /**
     * Returns all elements of the source collection that return false when evaluating of the predicate. This method is also
     * sometimes called filterNot and is the equivalent of calling iterable.select(Predicates.not(predicate)).
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; rejected =
     *     people.reject(person -&gt; person.person.getLastName().equals("Smith"));
     * </pre>
     *
     * @param predicate a {@link Predicate} to use as the reject criteria
     * @return a RichIterable that contains elements that cause {@link Predicate#accept(Object)} method to evaluate to false
     * @since 1.0
     */
    RichIterable<T> reject(Predicate<? super T> predicate);

    /**
     * Similar to {@link #reject(Predicate)}, except with an evaluation parameter for the second generic argument in {@link Predicate2}.
     * <p>
     * E.g. return a {@link Collection} of Person elements where the person has an age <b>greater than or equal to</b> 18 years
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; rejected =
     *     people.rejectWith((Person person, Integer age) -&gt; person.getAge() &lt; age, Integer.valueOf(18));
     * </pre>
     *
     * @param predicate a {@link Predicate2} to use as the select criteria
     * @param parameter a parameter to pass in for evaluation of the second argument {@code P} in {@code predicate}
     * @see #select(Predicate)
     * @since 5.0
     */
    <P> RichIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Same as the reject method with one parameter but uses the specified target collection for the results.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;Person&gt; rejected =
     *     people.reject(person -&gt; person.person.getLastName().equals("Smith"), Lists.mutable.empty());
     * </pre>
     *
     * @param predicate a {@link Predicate} to use as the reject criteria
     * @param target    the Collection to append to for all elements in this {@code RichIterable} that cause {@code Predicate#accept(Object)} method to evaluate to false
     * @return {@code target}, which contains appended elements as a result of the reject criteria
     * @since 1.0
     */
    <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target);

    /**
     * Similar to {@link #reject(Predicate, Collection)}, except with an evaluation parameter for the second generic argument in {@link Predicate2}.
     * <p>
     * E.g. return a {@link Collection} of Person elements where the person has an age <b>greater than or equal to</b> 18 years
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;Person&gt; rejected =
     *     people.rejectWith((Person person, Integer age) -&gt; person.getAge() &lt; age, Integer.valueOf(18), Lists.mutable.empty());
     * </pre>
     *
     * @param predicate        a {@link Predicate2} to use as the reject criteria
     * @param parameter        a parameter to pass in for evaluation of the second argument {@code P} in {@code predicate}
     * @param targetCollection the Collection to append to for all elements in this {@code RichIterable} that cause {@code Predicate#accept(Object)} method to evaluate to false
     * @return {@code targetCollection}, which contains appended elements as a result of the reject criteria
     * @see #reject(Predicate)
     * @see #reject(Predicate, Collection)
     * @since 1.0
     */
    <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection);

    /**
     * Returns all elements of the source collection that are instances of the Class {@code clazz}.
     *
     * <pre>
     * RichIterable&lt;Integer&gt; integers =
     *     List.mutable.with(new Integer(0), new Long(0L), new Double(0.0)).selectInstancesOf(Integer.class);
     * </pre>
     *
     * @since 2.0
     */
    <S> RichIterable<S> selectInstancesOf(Class<S> clazz);

    /**
     * Filters a collection into a PartitionedIterable based on the evaluation of the predicate.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * PartitionIterable&lt;Person&gt; newYorkersAndNonNewYorkers =
     *     people.<b>partition</b>(person -&gt; person.getAddress().getState().getName().equals("New York"));
     * </pre>
     *
     * @since 1.0.
     */
    PartitionIterable<T> partition(Predicate<? super T> predicate);

    /**
     * Filters a collection into a PartitionIterable based on the evaluation of the predicate.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * PartitionIterable&lt;Person&gt; newYorkersAndNonNewYorkers =
     *     people.<b>partitionWith</b>((Person person, String state) -&gt; person.getAddress().getState().getName().equals(state), "New York");
     * </pre>
     *
     * @since 5.0.
     */
    <P> PartitionIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    //endregion [Category: Filtering] 🚰

    //region [Category: Transforming] 🦋

    /**
     * Returns a new collection with the results of applying the specified function on each element of the source
     * collection. This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;String&gt; names =
     *     people.collect(person -&gt; person.getFirstName() + " " + person.getLastName());
     * </pre>
     *
     * @since 1.0
     */
    <V> RichIterable<V> collect(Function<? super T, ? extends V> function);

    /**
     * Same as {@link #collect(Function)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;String&gt; names =
     *     people.collect(person -&gt; person.getFirstName() + " " + person.getLastName(), Lists.mutable.empty());
     * </pre>
     *
     * @param function a {@link Function} to use as the collect transformation function
     * @param target   the Collection to append to for all elements in this {@code RichIterable} that meet select criteria {@code function}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @see #collect(Function)
     * @since 1.0
     */
    <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target);

    /**
     * Returns a new primitive {@code boolean} iterable with the results of applying the specified function on each element
     * of the source collection. This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * BooleanIterable licenses =
     *     people.collectBoolean(person -&gt; person.hasDrivingLicense());
     * </pre>
     *
     * @since 4.0
     */
    BooleanIterable collectBoolean(BooleanFunction<? super T> booleanFunction);

    /**
     * Same as {@link #collectBoolean(BooleanFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * BooleanArrayList licenses =
     *     people.collectBoolean(person -&gt; person.hasDrivingLicense(), new BooleanArrayList());
     * </pre>
     *
     * @param booleanFunction a {@link BooleanFunction} to use as the collect transformation function
     * @param target          the MutableBooleanCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    default <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        this.forEach(each -> target.add(booleanFunction.booleanValueOf(each)));
        return target;
    }

    /**
     * Returns a new primitive {@code byte} iterable with the results of applying the specified function on each element
     * of the source collection. This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * ByteIterable bytes =
     *     people.collectByte(person -&gt; person.getCode());
     * </pre>
     *
     * @since 4.0
     */
    ByteIterable collectByte(ByteFunction<? super T> byteFunction);

    /**
     * Same as {@link #collectByte(ByteFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * ByteArrayList bytes =
     *     people.collectByte(person -&gt; person.getCode(), new ByteArrayList());
     * </pre>
     *
     * @param byteFunction a {@link ByteFunction} to use as the collect transformation function
     * @param target       the MutableByteCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    default <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        this.forEach(each -> target.add(byteFunction.byteValueOf(each)));
        return target;
    }

    /**
     * Returns a new primitive {@code char} iterable with the results of applying the specified function on each element
     * of the source collection. This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * CharIterable chars =
     *     people.collectChar(person -&gt; person.getMiddleInitial());
     * </pre>
     *
     * @since 4.0
     */
    CharIterable collectChar(CharFunction<? super T> charFunction);

    /**
     * Same as {@link #collectChar(CharFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * CharArrayList chars =
     *     people.collectChar(person -&gt; person.getMiddleInitial(), new CharArrayList());
     * </pre>
     *
     * @param charFunction a {@link CharFunction} to use as the collect transformation function
     * @param target       the MutableCharCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    default <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        this.forEach(each -> target.add(charFunction.charValueOf(each)));
        return target;
    }

    /**
     * Returns a new primitive {@code double} iterable with the results of applying the specified function on each element
     * of the source collection. This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * DoubleIterable doubles =
     *     people.collectDouble(person -&gt; person.getMilesFromNorthPole());
     * </pre>
     *
     * @since 4.0
     */
    DoubleIterable collectDouble(DoubleFunction<? super T> doubleFunction);

    /**
     * Same as {@link #collectDouble(DoubleFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * DoubleArrayList doubles =
     *     people.collectDouble(person -&gt; person.getMilesFromNorthPole(), new DoubleArrayList());
     * </pre>
     *
     * @param doubleFunction a {@link DoubleFunction} to use as the collect transformation function
     * @param target         the MutableDoubleCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    default <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        this.forEach(each -> target.add(doubleFunction.doubleValueOf(each)));
        return target;
    }

    /**
     * Returns a new primitive {@code float} iterable with the results of applying the specified function on each element
     * of the source collection. This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * FloatIterable floats =
     *     people.collectFloat(person -&gt; person.getHeightInInches());
     * </pre>
     *
     * @since 4.0
     */
    FloatIterable collectFloat(FloatFunction<? super T> floatFunction);

    /**
     * Same as {@link #collectFloat(FloatFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * FloatArrayList floats =
     *     people.collectFloat(person -&gt; person.getHeightInInches(), new FloatArrayList());
     * </pre>
     *
     * @param floatFunction a {@link FloatFunction} to use as the collect transformation function
     * @param target        the MutableFloatCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    default <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        this.forEach(each -> target.add(floatFunction.floatValueOf(each)));
        return target;
    }

    /**
     * Returns a new primitive {@code int} iterable with the results of applying the specified function on each element
     * of the source collection. This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * IntIterable ints =
     *     people.collectInt(person -&gt; person.getAge());
     * </pre>
     *
     * @since 4.0
     */
    IntIterable collectInt(IntFunction<? super T> intFunction);

    /**
     * Same as {@link #collectInt(IntFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * IntArrayList ints =
     *     people.collectInt(person -&gt; person.getAge(), new IntArrayList());
     * </pre>
     *
     * @param intFunction a {@link IntFunction} to use as the collect transformation function
     * @param target      the MutableIntCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    default <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        this.forEach(each -> target.add(intFunction.intValueOf(each)));
        return target;
    }

    /**
     * Returns a new primitive {@code long} iterable with the results of applying the specified function on each element
     * of the source collection. This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * LongIterable longs =
     *     people.collectLong(person -&gt; person.getGuid());
     * </pre>
     *
     * @since 4.0
     */
    LongIterable collectLong(LongFunction<? super T> longFunction);

    /**
     * Same as {@link #collectLong(LongFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * LongArrayList longs =
     *     people.collectLong(person -&gt; person.getGuid(), new LongArrayList());
     * </pre>
     *
     * @param longFunction a {@link LongFunction} to use as the collect transformation function
     * @param target       the MutableLongCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    default <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        this.forEach(each -> target.add(longFunction.longValueOf(each)));
        return target;
    }

    /**
     * Returns a new primitive {@code short} iterable with the results of applying the specified function on each element
     * of the source collection. This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * ShortIterable shorts =
     *     people.collectShort(person -&gt; person.getNumberOfJunkMailItemsReceivedPerMonth());
     * </pre>
     *
     * @since 4.0
     */
    ShortIterable collectShort(ShortFunction<? super T> shortFunction);

    /**
     * Same as {@link #collectShort(ShortFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * ShortArrayList shorts =
     *     people.collectShort(person -&gt; person.getNumberOfJunkMailItemsReceivedPerMonth, new ShortArrayList());
     * </pre>
     *
     * @param shortFunction a {@link ShortFunction} to use as the collect transformation function
     * @param target        the MutableShortCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    default <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        this.forEach(each -> target.add(shortFunction.shortValueOf(each)));
        return target;
    }

    /**
     * Same as {@link #collect(Function)} with a {@code Function2} and specified parameter which is passed to the block.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Integer&gt; integers =
     *     Lists.mutable.with(1, 2, 3).collectWith((each, parameter) -&gt; each + parameter, Integer.valueOf(1));
     * </pre>
     *
     * @param function  A {@link Function2} to use as the collect transformation function
     * @param parameter A parameter to pass in for evaluation of the second argument {@code P} in {@code function}
     * @return A new {@code RichIterable} that contains the transformed elements returned by {@link Function2#value(Object, Object)}
     * @see #collect(Function)
     * @since 5.0
     */
    <P, V> RichIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    /**
     * Same as collectWith but with a targetCollection parameter to gather the results.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableSet&lt;Integer&gt; integers =
     *     Lists.mutable.with(1, 2, 3).collectWith((each, parameter) -&gt; each + parameter, Integer.valueOf(1), Sets.mutable.empty());
     * </pre>
     *
     * @param function         a {@link Function2} to use as the collect transformation function
     * @param parameter        a parameter to pass in for evaluation of the second argument {@code P} in {@code function}
     * @param targetCollection the Collection to append to for all elements in this {@code RichIterable} that meet select criteria {@code function}
     * @return {@code targetCollection}, which contains appended elements as a result of the collect transformation
     * @since 1.0
     */
    <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R targetCollection);

    /**
     * Returns a new collection with the results of applying the specified function on each element of the source
     * collection, but only for those elements which return true upon evaluation of the predicate. This is the
     * optimized equivalent of calling iterable.select(predicate).collect(function).
     * <p>
     * Example using a Java 8 lambda and method reference:
     * <pre>
     * RichIterable&lt;String&gt; strings = Lists.mutable.with(1, 2, 3).collectIf(e -&gt; e != null, Object::toString);
     * </pre>
     * <p>
     * Example using Predicates factory:
     * <pre>
     * RichIterable&lt;String&gt; strings = Lists.mutable.with(1, 2, 3).collectIf(Predicates.notNull(), Functions.getToString());
     * </pre>
     *
     * @since 1.0
     */
    <V> RichIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    /**
     * Same as the collectIf method with two parameters but uses the specified target collection for the results.
     *
     * @param predicate a {@link Predicate} to use as the select criteria
     * @param function  a {@link Function} to use as the collect transformation function
     * @param target    the Collection to append to for all elements in this {@code RichIterable} that meet the collect criteria {@code predicate}
     * @return {@code targetCollection}, which contains appended elements as a result of the collect criteria and transformation
     * @see #collectIf(Predicate, Function)
     * @since 1.0
     */
    <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target);

    /**
     * {@code flatCollect} is a special case of {@link #collect(Function)}. With {@code collect}, when the {@link Function} returns
     * a collection, the result is a collection of collections. {@code flatCollect} outputs a single "flattened" collection
     * instead. This method is commonly called flatMap.
     * <p>
     * Consider the following example where we have a {@code Person} class, and each {@code Person} has a list of {@code Address} objects. Take the following {@link Function}:
     * <pre>
     * Function&lt;Person, List&lt;Address&gt;&gt; addressFunction = Person::getAddresses;
     * RichIterable&lt;Person&gt; people = ...;
     * </pre>
     * Using {@code collect} returns a collection of collections of addresses.
     * <pre>
     * RichIterable&lt;List&lt;Address&gt;&gt; addresses = people.collect(addressFunction);
     * </pre>
     * Using {@code flatCollect} returns a single flattened list of addresses.
     * <pre>
     * RichIterable&lt;Address&gt; addresses = people.flatCollect(addressFunction);
     * </pre>
     *
     * @param function The {@link Function} to apply
     * @return a new flattened collection produced by applying the given {@code function}
     * @since 1.0
     */
    <V> RichIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Same as flatCollect, only with a Function2 that is passed an extra parameter.
     *
     * @since 9.2
     */
    default <P, V> RichIterable<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target);

    /**
     * Same as flatCollectWith, only the results are collected into the target collection.
     *
     * @since 9.2
     */
    default <P, V, R extends Collection<V>> R flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter, R target)
    {
        return this.flatCollect(each -> function.apply(each, parameter), target);
    }

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    default <R extends MutableByteCollection> R flatCollectByte(Function<? super T, ? extends ByteIterable> function, R target)
    {
        this.forEach(each -> target.addAll(function.valueOf(each)));
        return target;
    }

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    default <R extends MutableCharCollection> R flatCollectChar(Function<? super T, ? extends CharIterable> function, R target)
    {
        this.forEach(each -> target.addAll(function.valueOf(each)));
        return target;
    }

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    default <R extends MutableIntCollection> R flatCollectInt(Function<? super T, ? extends IntIterable> function, R target)
    {
        this.forEach(each -> target.addAll(function.valueOf(each)));
        return target;
    }

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    default <R extends MutableShortCollection> R flatCollectShort(Function<? super T, ? extends ShortIterable> function, R target)
    {
        this.forEach(each -> target.addAll(function.valueOf(each)));
        return target;
    }

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    default <R extends MutableDoubleCollection> R flatCollectDouble(Function<? super T, ? extends DoubleIterable> function, R target)
    {
        this.forEach(each -> target.addAll(function.valueOf(each)));
        return target;
    }

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    default <R extends MutableFloatCollection> R flatCollectFloat(Function<? super T, ? extends FloatIterable> function, R target)
    {
        this.forEach(each -> target.addAll(function.valueOf(each)));
        return target;
    }

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    default <R extends MutableLongCollection> R flatCollectLong(Function<? super T, ? extends LongIterable> function, R target)
    {
        this.forEach(each -> target.addAll(function.valueOf(each)));
        return target;
    }

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    default <R extends MutableBooleanCollection> R flatCollectBoolean(Function<? super T, ? extends BooleanIterable> function, R target)
    {
        this.forEach(each -> target.addAll(function.valueOf(each)));
        return target;
    }

    /**
     * Returns a {@code RichIterable} formed from this {@code RichIterable} and another {@code RichIterable} by
     * combining corresponding elements in pairs. If one of the two {@code RichIterable}s is longer than the other, its
     * remaining elements are ignored.
     *
     * @param that The {@code RichIterable} providing the second half of each result pair
     * @param <S>  the type of the second half of the returned pairs
     * @return A new {@code RichIterable} containing pairs consisting of corresponding elements of this {@code
     * RichIterable} and that. The length of the returned {@code RichIterable} is the minimum of the lengths of
     * this {@code RichIterable} and that.
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Deprecated
    <S> RichIterable<Pair<T, S>> zip(Iterable<S> that);

    /**
     * Same as {@link #zip(Iterable)} but uses {@code target} for output.
     *
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable, Collection)} instead;
     */
    @Deprecated
    <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target);

    /**
     * Zips this {@code RichIterable} with its indices.
     *
     * @return A new {@code RichIterable} containing pairs consisting of all elements of this {@code RichIterable}
     * paired with their index. Indices start at 0.
     * @see #zip(Iterable)
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Deprecated
    RichIterable<Pair<T, Integer>> zipWithIndex();

    /**
     * Same as {@link #zipWithIndex()} but uses {@code target} for output.
     *
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex(Collection)} instead.
     */
    @Deprecated
    <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target);

    //endregion [Category: Transforming] 🦋

    //region [Category: Grouping] 🏘️

    /**
     * For each element of the iterable, the function is evaluated and the results of these evaluations are collected
     * into a new multimap, where the transformed value is the key and the original values are added to the same (or similar)
     * species of collection as the source iterable.
     * <p>
     * Example using a Java 8 method reference:
     * <pre>
     * Multimap&lt;String, Person&gt; peopleByLastName =
     *     people.groupBy(Person::getLastName);
     * </pre>
     *
     * @since 1.0
     */
    <V> Multimap<V, T> groupBy(Function<? super T, ? extends V> function);

    /**
     * Same as {@link #groupBy(Function)}, except that the results are gathered into the specified {@code target}
     * multimap.
     * <p>
     * Example using a Java 8 method reference:
     * <pre>
     * FastListMultimap&lt;String, Person&gt; peopleByLastName =
     *     people.groupBy(Person::getLastName, new FastListMultimap&lt;String, Person&gt;());
     * </pre>
     *
     * @since 1.0
     */
    <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target);

    /**
     * Similar to {@link #groupBy(Function)}, except the result of evaluating function will return a collection of keys
     * for each value.
     *
     * @since 1.0
     */
    <V> Multimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Same as {@link #groupByEach(Function)}, except that the results are gathered into the specified {@code target}
     * multimap.
     *
     * @since 1.0
     */
    <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target);

    /**
     * For each element of the iterable, the function is evaluated, and the results of these evaluations are collected
     * into a new map, where the transformed value is the key. The generated keys must each be unique, or else an
     * exception is thrown.
     *
     * @throws IllegalStateException if the keys returned by the function are not unique
     * @see #groupBy(Function)
     * @since 5.0
     */
    <V> MapIterable<V, T> groupByUniqueKey(Function<? super T, ? extends V> function);

    /**
     * Same as {@link #groupByUniqueKey(Function)}, except that the results are gathered into the specified {@code target}
     * map.
     *
     * @throws IllegalStateException if the keys returned by the function are not unique
     * @see #groupByUniqueKey(Function)
     * @since 6.0
     */
    <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(
            Function<? super T, ? extends V> function,
            R target);

    /**
     * Partitions elements in fixed size chunks.
     * The last chunk may contain fewer elements if the total number is not evenly divisible by the chunk size.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableList<Integer> numbers = Lists.mutable.with(1, 2, 3, 4, 5, 6, 7);
     * RichIterable<RichIterable<Integer>> chunks = numbers.chunk(3);
     * // chunks: [[1, 2, 3], [4, 5, 6], [7]]
     * }</pre>
     *
     * @param size the number of elements per chunk
     * @return A {@code RichIterable} containing {@code RichIterable}s of size {@code size}, except the last will be
     * truncated if the elements don't divide evenly
     * @since 1.0
     */
    RichIterable<RichIterable<T>> chunk(int size);

    /**
     * Applies a groupBy function over the iterable, followed by a collect function.
     *
     * @param groupByFunction a {@link Function} to use as the groupBy transformation function
     * @param collectFunction a {@link Function} to use as the collect transformation function
     *
     * @return The {@code target} collection where the key is the transformed result from applying the groupBy function
     * and the value is the transformed result from applying the collect function.
     *
     * @see #groupBy(Function)
     * @see Multimap#collectValues(Function)
     *
     * @since 10.1.0
     */
    default <K, V, R extends MutableMultimap<K, V>> R groupByAndCollect(
            Function<? super T, ? extends K> groupByFunction,
            Function<? super T, ? extends V> collectFunction,
            R target)
    {
        this.forEach(each -> target.put(groupByFunction.apply(each), collectFunction.apply(each)));
        return target;
    }

    //endregion [Category: Grouping] 🏘️

    //region [Category: Aggregating] 📊

    /**
     * Returns the final result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter. This method is commonly called fold or sometimes reduce.
     *
     * @since 1.0
     */
    <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function);

    /**
     * Returns the final int result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 1.0
     * @deprecated since 11.1 - use injectIntoInt instead
     */
    @Deprecated
    int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function);

    /**
     * Returns the final int result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 11.1
     */
    default int injectIntoInt(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        return this.injectInto(injectedValue, function);
    }

    /**
     * Returns the final long result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 1.0
     * @deprecated since 11.1 - use injectIntoLong instead
     */
    @Deprecated
    long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function);

    /**
     * Returns the final long result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 11.1
     */
    default long injectIntoLong(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        return this.injectInto(injectedValue, function);
    }

    /**
     * Returns the final float result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 2.0
     * @deprecated since 11.1 - use injectIntoFloat instead
     */
    @Deprecated
    float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function);

    /**
     * Returns the final float result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 11.1
     */
    default float injectIntoFloat(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        return this.injectInto(injectedValue, function);
    }

    /**
     * Returns the final double result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 1.0
     * @deprecated since 11.1 - use injectIntoDouble instead
     */
    @Deprecated
    double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function);

    /**
     * Returns the final double result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 11.1
     */
    default double injectIntoDouble(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        return this.injectInto(injectedValue, function);
    }

    /**
     * Returns the final long result of evaluating function for each element of the iterable and adding the results
     * together.
     *
     * @since 2.0
     */
    long sumOfInt(IntFunction<? super T> function);

    /**
     * Returns the final double result of evaluating function for each element of the iterable and adding the results
     * together. It uses Kahan summation algorithm to reduce numerical error.
     *
     * @since 2.0
     */
    double sumOfFloat(FloatFunction<? super T> function);

    /**
     * Returns the final long result of evaluating function for each element of the iterable and adding the results
     * together.
     *
     * @since 2.0
     */
    long sumOfLong(LongFunction<? super T> function);

    /**
     * Returns the final double result of evaluating function for each element of the iterable and adding the results
     * together. It uses Kahan summation algorithm to reduce numerical error.
     *
     * @since 2.0
     */
    double sumOfDouble(DoubleFunction<? super T> function);

    /**
     * Returns the result of summarizing the value returned from applying the IntFunction to
     * each element of the iterable.
     *
     * <pre>
     * IntSummaryStatistics stats =
     *     Lists.mutable.with(1, 2, 3).summarizeInt(Integer::intValue);
     * </pre>
     *
     * @since 8.0
     */
    default IntSummaryStatistics summarizeInt(IntFunction<? super T> function)
    {
        IntSummaryStatistics stats = new IntSummaryStatistics();
        this.each(each -> stats.accept(function.intValueOf(each)));
        return stats;
    }

    /**
     * Returns the result of summarizing the value returned from applying the FloatFunction to
     * each element of the iterable.
     *
     * <pre>
     * DoubleSummaryStatistics stats =
     *     Lists.mutable.with(1, 2, 3).summarizeFloat(Integer::floatValue);
     * </pre>
     *
     * @since 8.0
     */
    default DoubleSummaryStatistics summarizeFloat(FloatFunction<? super T> function)
    {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        this.each(each -> stats.accept(function.floatValueOf(each)));
        return stats;
    }

    /**
     * Returns the result of summarizing the value returned from applying the LongFunction to
     * each element of the iterable.
     *
     * <pre>
     * LongSummaryStatistics stats =
     *     Lists.mutable.with(1, 2, 3).summarizeLong(Integer::longValue);
     * </pre>
     *
     * @since 8.0
     */
    default LongSummaryStatistics summarizeLong(LongFunction<? super T> function)
    {
        LongSummaryStatistics stats = new LongSummaryStatistics();
        this.each(each -> stats.accept(function.longValueOf(each)));
        return stats;
    }

    /**
     * Returns the result of summarizing the value returned from applying the DoubleFunction to
     * each element of the iterable.
     *
     * <pre>
     * DoubleSummaryStatistics stats =
     *     Lists.mutable.with(1, 2, 3).summarizeDouble(Integer::doubleValue);
     * </pre>
     *
     * @since 8.0
     */
    default DoubleSummaryStatistics summarizeDouble(DoubleFunction<? super T> function)
    {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        this.each(each -> stats.accept(function.doubleValueOf(each)));
        return stats;
    }

    /**
     * This method produces the equivalent result as {@link Stream#collect(Collector)}.
     *
     * <pre>
     * MutableObjectLongMap&lt;Integer&gt; map2 =
     *     Lists.mutable.with(1, 2, 3, 4, 5).reduceInPlace(Collectors2.sumByInt(i -&gt; Integer.valueOf(i % 2), Integer::intValue));
     * </pre>
     *
     * @since 8.0
     */
    default <R, A> R reduceInPlace(Collector<? super T, A, R> collector)
    {
        A mutableResult = collector.supplier().get();
        BiConsumer<A, ? super T> accumulator = collector.accumulator();
        this.each(each -> accumulator.accept(mutableResult, each));
        return collector.finisher().apply(mutableResult);
    }

    /**
     * This method produces the equivalent result as {@link Stream#collect(Supplier, BiConsumer, BiConsumer)}.
     * The combiner used in collect is unnecessary in the serial case, so is not included in the API.
     *
     * @since 8.0
     */
    default <R> R reduceInPlace(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator)
    {
        R result = supplier.get();
        this.each(each -> accumulator.accept(result, each));
        return result;
    }

    /**
     * This method produces the equivalent result as {@link Stream#reduce(BinaryOperator)}.
     *
     * @since 8.0
     */
    default Optional<T> reduce(BinaryOperator<T> accumulator)
    {
        boolean[] seenOne = new boolean[1];
        T[] result = (T[]) new Object[1];
        this.each(each ->
        {
            if (seenOne[0])
            {
                result[0] = accumulator.apply(result[0], each);
            }
            else
            {
                seenOne[0] = true;
                result[0] = each;
            }
        });
        return seenOne[0] ? Optional.of(result[0]) : Optional.empty();
    }

    /**
     * Groups and sums the values using the two specified functions.
     *
     * @since 6.0
     */
    <V> ObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function);

    /**
     * Groups and sums the values using the two specified functions.
     *
     * @since 6.0
     */
    <V> ObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function);

    /**
     * Groups and sums the values using the two specified functions.
     *
     * @since 6.0
     */
    <V> ObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function);

    /**
     * Groups and sums the values using the two specified functions.
     *
     * @since 6.0
     */
    <V> ObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function);

    /**
     * Applies an aggregate procedure over the iterable grouping results into a Map based on the specific groupBy function.
     * Aggregate results are required to be mutable as they will be changed in place by the procedure. A second function
     * specifies the initial "zero" aggregate value to work with (i.e. new AtomicInteger(0)).
     *
     * @since 3.0
     */
    default <K, V> MapIterable<K, V> aggregateInPlaceBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        MutableMap<K, V> map = Maps.mutable.empty();
        this.forEach(each ->
        {
            K key = groupBy.valueOf(each);
            V value = map.getIfAbsentPut(key, zeroValueFactory);
            mutatingAggregator.value(value, each);
        });
        return map;
    }

    /**
     * Applies an aggregate function over the iterable grouping results into a map based on the specific groupBy function.
     * Aggregate results are allowed to be immutable as they will be replaced in place in the map. A second function
     * specifies the initial "zero" aggregate value to work with (i.e. Integer.valueOf(0)).
     *
     * @since 3.0
     */
    default <K, V> MapIterable<K, V> aggregateBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        return this.aggregateBy(
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                Maps.mutable.empty());
    }

    /**
     * Applies an aggregate function over the iterable grouping results into the target map based on the specific
     * groupBy function. Aggregate results are allowed to be immutable as they will be replaced in place in the map. A
     * second function specifies the initial "zero" aggregate value to work with (i.e. Integer.valueOf(0)).
     *
     * @since 10.3
     */
    default <K, V, R extends MutableMapIterable<K, V>> R aggregateBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            R target)
    {
        this.forEach(each ->
        {
            K key = groupBy.valueOf(each);
            target.updateValueWith(key, zeroValueFactory, nonMutatingAggregator, each);
        });
        return target;
    }

    /**
     * Applies an aggregate function over the iterable grouping results into a map based on the specific groupBy function.
     * Aggregate results are allowed to be immutable as they will be replaced in place in the map.
     *
     * @since 12.0
     *
     */
    default <K> MapIterable<K, T> reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction)
    {
        return this.reduceBy(
                groupBy,
                reduceFunction,
                Maps.mutable.empty());
    }

    /**
     * Applies an aggregate function over the iterable grouping results into a map based on the specific groupBy function.
     * Aggregate results are allowed to be immutable as they will be replaced in place in the map.
     *
     * @since 12.0
     */
    default <K, R extends MutableMapIterable<K, T>> R reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction,
            R target)
    {
        this.forEach(each ->
        {
            K key = groupBy.valueOf(each);
            target.merge(key, each, reduceFunction);
        });
        return target;
    }

    //endregion [Category: Aggregating] 📊

    //region [Category: Converting] 🔌

    /**
     * Adds all the elements in this iterable to the specified target Collection.
     * This is a convenient way to convert or transfer elements between collection types.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableList<Integer> list = Lists.mutable.with(1, 2, 3, 4);
     * MutableSet<Integer> set = list.into(Sets.mutable.empty());
     * // set now contains {1, 2, 3, 4}
     * }</pre>
     *
     * @param target the collection to add all elements to
     * @param <R> the type of the target collection
     * @return the target collection with all elements from this iterable added
     * @since 8.0
     */
    <R extends Collection<T>> R into(R target);

    /**
     * Converts the collection to a MutableList implementation.
     *
     * @since 1.0
     */
    MutableList<T> toList();

    /**
     * Converts the collection to a MutableList implementation and sorts it using the natural order of the elements.
     *
     * @since 1.0
     */
    default MutableList<T> toSortedList()
    {
        return this.toList().sortThis();
    }

    /**
     * Converts the collection to a MutableList implementation and sorts it using the specified comparator.
     *
     * @since 1.0
     */
    default MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.toList().sortThis(comparator);
    }

    /**
     * Converts the collection to a MutableList implementation and sorts it based on the natural order of the
     * attribute returned by {@code function}.
     *
     * @since 1.0
     */
    default <V extends Comparable<? super V>> MutableList<T> toSortedListBy(Function<? super T, ? extends V> function)
    {
        return this.toSortedList(SerializableComparators.byFunction(function));
    }

    /**
     * Converts the collection to a MutableSet implementation.
     *
     * @since 1.0
     */
    MutableSet<T> toSet();

    /**
     * Converts the collection to a MutableSortedSet implementation and sorts it using the natural order of the
     * elements.
     *
     * @since 1.0
     */
    MutableSortedSet<T> toSortedSet();

    /**
     * Converts the collection to a MutableSortedSet implementation and sorts it using the specified comparator.
     *
     * @since 1.0
     */
    MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator);

    /**
     * Converts the collection to a MutableSortedSet implementation and sorts it based on the natural order of the
     * attribute returned by {@code function}.
     *
     * @since 1.0
     */
    default <V extends Comparable<? super V>> MutableSortedSet<T> toSortedSetBy(Function<? super T, ? extends V> function)
    {
        return this.toSortedSet(SerializableComparators.byFunction(function));
    }

    /**
     * Converts the collection to the default MutableBag implementation.
     *
     * @since 1.0
     */
    MutableBag<T> toBag();

    /**
     * Converts the collection to a MutableSortedBag implementation and sorts it using the natural order of the
     * elements.
     *
     * @since 6.0
     */
    MutableSortedBag<T> toSortedBag();

    /**
     * Converts the collection to the MutableSortedBag implementation and sorts it using the specified comparator.
     *
     * @since 6.0
     */
    MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator);

    /**
     * Converts the collection to a MutableSortedBag implementation and sorts it based on the natural order of the
     * attribute returned by {@code function}.
     *
     * @since 6.0
     */
    default <V extends Comparable<? super V>> MutableSortedBag<T> toSortedBagBy(Function<? super T, ? extends V> function)
    {
        return this.toSortedBag(SerializableComparators.byFunction(function));
    }

    /**
     * Converts the collection to a MutableMap implementation using the specified key and value functions.
     *
     * @since 1.0
     */
    <NK, NV> MutableMap<NK, NV> toMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction);

    /**
     * Same as {@link #toMap(Function, Function)}, except that the results are gathered into the specified {@code target}
     * map.
     *
     * @since 10.0
     */
    default <NK, NV, R extends Map<NK, NV>> R toMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction,
            R target)
    {
        this.forEach(each -> target.put(keyFunction.apply(each), valueFunction.apply(each)));
        return target;
    }

    /**
     * Converts the collection to a MutableSortedMap implementation using the specified key and value functions
     * sorted by the key elements' natural ordering.
     *
     * @since 1.0
     */
    <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction);

    /**
     * Converts the collection to a MutableSortedMap implementation using the specified key and value functions
     * sorted by the given comparator.
     *
     * @since 1.0
     */
    <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Comparator<? super NK> comparator,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction);

    /**
     * Converts the collection to a MutableSortedMap implementation using the specified key and value functions
     * and sorts it based on the natural order of the attribute returned by {@code sortBy} function.
     */
    default <KK extends Comparable<? super KK>, NK, NV> MutableSortedMap<NK, NV> toSortedMapBy(
            Function<? super NK, KK> sortBy,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.toSortedMap(SerializableComparators.byFunction(sortBy), keyFunction, valueFunction);
    }

    /**
     * Converts the collection to a BiMap implementation using the specified key and value functions.
     *
     * @since 10.0
     */
    <NK, NV> MutableBiMap<NK, NV> toBiMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction);

    /**
     * Converts the RichIterable to the default ImmutableList implementation.
     *
     * @since 11.0
     */
    default ImmutableList<T> toImmutableList()
    {
        return Lists.immutable.withAll(this);
    }

    /**
     * Converts the RichIterable to the default ImmutableSet implementation.
     *
     * @since 11.0
     */
    default ImmutableSet<T> toImmutableSet()
    {
        return Sets.immutable.withAll(this);
    }

    /**
     * Converts the RichIterable to the default ImmutableBag implementation.
     *
     * @since 11.0
     */
    default ImmutableBag<T> toImmutableBag()
    {
        return Bags.immutable.withAll(this);
    }

    /**
     * Converts the RichIterable to the default sorted ImmutableList implementation.
     *
     * @since 11.0
     */
    default ImmutableList<T> toImmutableSortedList()
    {
        return Lists.immutable.withAllSorted(this);
    }

    /**
     * Converts the collection to an ImmutableList implementation and sorts it using the specified comparator.
     *
     * @since 11.0
     */
    default ImmutableList<T> toImmutableSortedList(Comparator<? super T> comparator)
    {
        return Lists.immutable.withAllSorted(comparator, this);
    }

    /**
     * Converts the collection to an ImmutableList implementation and sorts it based on the natural order of the
     * attribute returned by {@code function}.
     *
     * @since 11.0
     */
    default <V extends Comparable<? super V>> ImmutableList<T> toImmutableSortedListBy(Function<? super T, ? extends V> function)
    {
        return this.toImmutableSortedList(Comparator.comparing(function));
    }

    /**
     * Converts the RichIterable to the default ImmutableSortedSet implementation.
     *
     * @since 11.0
     */
    default ImmutableSortedSet<T> toImmutableSortedSet()
    {
        return SortedSets.immutable.withAll(this);
    }

    /**
     * Converts the collection to an ImmutableSortedSet implementation and sorts it using the specified comparator.
     *
     * @since 11.0
     */
    default ImmutableSortedSet<T> toImmutableSortedSet(Comparator<? super T> comparator)
    {
        return SortedSets.immutable.withAll(comparator, this);
    }

    /**
     * Converts the collection to an ImmutableSortedSet implementation and sorts it based on the natural order of the
     * attribute returned by {@code function}.
     *
     * @since 11.0
     */
    default <V extends Comparable<? super V>> ImmutableSortedSet<T> toImmutableSortedSetBy(Function<? super T, ? extends V> function)
    {
        return this.toImmutableSortedSet(Comparator.comparing(function));
    }

    /**
     * Converts the RichIterable to the default ImmutableSortedBag implementation.
     *
     * @since 11.0
     */
    default ImmutableSortedBag<T> toImmutableSortedBag()
    {
        return SortedBags.immutable.withAll(this);
    }

    /**
     * Converts the collection to an ImmutableSortedBag implementation and sorts it using the specified comparator.
     *
     * @since 11.0
     */
    default ImmutableSortedBag<T> toImmutableSortedBag(Comparator<? super T> comparator)
    {
        return SortedBags.immutable.withAll(comparator, this);
    }

    /**
     * Converts the collection to an ImmutableSortedBag implementation and sorts it based on the natural order of the
     * attribute returned by {@code function}.
     *
     * @since 11.0
     */
    default <V extends Comparable<? super V>> ImmutableSortedBag<T> toImmutableSortedBagBy(Function<? super T, ? extends V> function)
    {
        return this.toImmutableSortedBag(Comparator.comparing(function));
    }

    /**
     * Converts the collection to an ImmutableMap implementation using the specified key and value functions.
     *
     * @since 11.0
     */
    default <NK, NV> ImmutableMap<NK, NV> toImmutableMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.<NK, NV>toMap(keyFunction, valueFunction).toImmutable();
    }

    /**
     * Converts the collection to an immutable BiMap implementation using the specified key and value functions.
     *
     * @since 11.0
     */
    default <NK, NV> ImmutableBiMap<NK, NV> toImmutableBiMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.<NK, NV>toBiMap(keyFunction, valueFunction).toImmutable();
    }

    /**
     * Converts this iterable to an array.
     *
     * @see Collection#toArray()
     * @since 1.0
     */
    default Object[] toArray()
    {
        Object[] result = new Object[this.size()];
        this.forEachWithIndex((each, index) -> result[index] = each);
        return result;
    }

    /**
     * Converts this iterable to an array using the specified target array, assuming the target array is as long
     * or longer than the iterable.
     *
     * @see Collection#toArray(Object[])
     * @since 1.0
     */
    default <E> E[] toArray(E[] array)
    {
        int size = this.size();
        E[] result = array.length < size
                ? (E[]) Array.newInstance(array.getClass().getComponentType(), size)
                : array;

        this.forEachWithIndex((each, index) -> result[index] = (E) each);
        if (result.length > size)
        {
            result[size] = null;
        }
        return result;
    }

    /**
     * Returns a string with the elements of this iterable separated by commas with spaces and
     * enclosed in square brackets.
     *
     * <pre>
     * Assert.assertEquals("[]", Lists.mutable.empty().toString());
     * Assert.assertEquals("[1]", Lists.mutable.with(1).toString());
     * Assert.assertEquals("[1, 2, 3]", Lists.mutable.with(1, 2, 3).toString());
     * </pre>
     *
     * @return a string representation of this RichIterable
     * @see java.util.AbstractCollection#toString()
     * @since 1.0
     */
    @Override
    String toString();

    /**
     * Returns a string representation of this collection by delegating to {@link #makeString(String)} and defaulting
     * the separator parameter to the characters {@code ", "} (comma and space).
     *
     * @return a string representation of this collection.
     * @since 1.0
     */
    default String makeString()
    {
        return this.makeString(", ");
    }

    /**
     * Returns a string representation of this collection by delegating to {@link #makeString(String, String, String)}
     * and defaulting the start and end parameters to {@code ""} (the empty String).
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableList<Integer> numbers = Lists.mutable.with(1, 2, 3);
     * String result = numbers.makeString(" | ");  // "1 | 2 | 3"
     * }</pre>
     *
     * @param separator the separator to use between elements
     * @return a string representation of this collection
     * @since 1.0
     */
    default String makeString(String separator)
    {
        return this.makeString("", separator, "");
    }

    /**
     * Returns a string representation of this collection with the elements separated by the specified
     * separator and enclosed between the start and end strings.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableList<Integer> numbers = Lists.mutable.with(1, 2, 3);
     * String result = numbers.makeString("[", ", ", "]");  // "[1, 2, 3]"
     * String result2 = numbers.makeString("{", "; ", "}");  // "{1; 2; 3}"
     * }</pre>
     *
     * @param start the string to prepend to the result
     * @param separator the separator to use between elements
     * @param end the string to append to the result
     * @return a string representation of this collection
     * @since 1.0
     */
    default String makeString(String start, String separator, String end)
    {
        Appendable stringBuilder = new StringBuilder();
        this.appendString(stringBuilder, start, separator, end);
        return stringBuilder.toString();
    }

    /**
     * Returns a string representation of the collection, created by applying the function supplied to each element,
     * with the elements separated by the specified separator and enclosed between the start and end strings.
     *
     * @return a string representation of the mapped collection
     */
    default String makeString(Function<? super T, Object> function, String start, String separator, String end)
    {
        return this.asLazy().collect(function).makeString(start, separator, end);
    }

    /**
     * Prints a string representation of this collection onto the given {@code Appendable}. Prints the string returned
     * by {@link #makeString()}.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableList<Integer> numbers = Lists.mutable.with(1, 2, 3);
     * StringBuilder builder = new StringBuilder("Numbers: ");
     * numbers.appendString(builder);
     * // builder now contains "Numbers: 1, 2, 3"
     * }</pre>
     *
     * @param appendable the Appendable to write to
     * @since 1.0
     */
    default void appendString(Appendable appendable)
    {
        this.appendString(appendable, ", ");
    }

    /**
     * Prints a string representation of this collection onto the given {@code Appendable}. Prints the string returned
     * by {@link #makeString(String)}.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableList<Integer> numbers = Lists.mutable.with(1, 2, 3);
     * StringBuilder builder = new StringBuilder("Values: ");
     * numbers.appendString(builder, " | ");
     * // builder now contains "Values: 1 | 2 | 3"
     * }</pre>
     *
     * @param appendable the Appendable to write to
     * @param separator the separator to use between elements
     * @since 1.0
     */
    default void appendString(Appendable appendable, String separator)
    {
        this.appendString(appendable, "", separator, "");
    }

    /**
     * Prints a string representation of this collection onto the given {@code Appendable}. Prints the string returned
     * by {@link #makeString(String, String, String)}.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableList<Integer> numbers = Lists.mutable.with(1, 2, 3);
     * StringBuilder builder = new StringBuilder();
     * numbers.appendString(builder, "[", ", ", "]");
     * // builder now contains "[1, 2, 3]"
     * }</pre>
     *
     * @param appendable the Appendable to write to
     * @param start the string to prepend to the result
     * @param separator the separator to use between elements
     * @param end the string to append to the result
     * @since 1.0
     */
    void appendString(Appendable appendable, String start, String separator, String end);

    //endregion [Category: Converting] 🔌
}
