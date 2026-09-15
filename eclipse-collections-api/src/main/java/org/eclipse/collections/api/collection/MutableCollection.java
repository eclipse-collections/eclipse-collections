/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.collection;

import java.util.Collection;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.Function3;
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
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.ByteLists;
import org.eclipse.collections.api.factory.primitive.CharLists;
import org.eclipse.collections.api.factory.primitive.DoubleLists;
import org.eclipse.collections.api.factory.primitive.FloatLists;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.factory.primitive.ShortLists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;

/**
 * MutableCollection is an interface which extends the base java.util.Collection interface and adds several internal
 * iterator methods, from the Smalltalk Collection protocol. These include variations of forEach, select, reject,
 * detect, collect, injectInto, anySatisfy, allSatisfy. These include count, remove, partition, collectIf. The API also
 * includes converter methods to convert a MutableCollection to a List (toList), to a sorted List (toSortedList), to a
 * Set (toSet), and to a Map (toMap).
 * <p>
 * There are several extensions to MutableCollection, including MutableList, MutableSet, and MutableBag.
 */
public interface MutableCollection<T>
        extends Collection<T>, RichIterable<T>
{
    @Override
    default Object[] toArray()
    {
        return RichIterable.super.toArray();
    }

    @Override
    default <T1> T1[] toArray(T1[] a)
    {
        return RichIterable.super.toArray(a);
    }

    /**
     * This method allows mutable and fixed size collections the ability to add elements to their existing elements.
     * In order to support fixed size a new instance of a collection would have to be returned taking the elements of
     * the original collection and appending the new element to form the new collection. In the case of mutable
     * collections, the original collection is modified, and is returned. In order to use this method properly with
     * mutable and fixed size collections the following approach must be taken:
     *
     * <pre>
     * MutableCollection&lt;String&gt; list = list.with("1");
     * list = list.with("2");
     * return list;
     * </pre>
     * In the case of {@link FixedSizeCollection} a new instance of MutableCollection will be returned by with, and any
     * variables that previously referenced the original collection will need to be redirected to reference the
     * new instance. For other MutableCollection types you will replace the reference to collection with the same
     * collection, since the instance will return "this" after calling add on itself.
     *
     * @see #add(Object)
     */
    MutableCollection<T> with(T element);

    /**
     * This method allows mutable and fixed size collections the ability to remove elements from their existing elements.
     * In order to support fixed size a new instance of a collection would have to be returned containing the elements
     * that would be left from the original collection after calling remove. In the case of mutable collections, the
     * original collection is modified, and is returned. In order to use this method properly with mutable and fixed
     * size collections the following approach must be taken:
     *
     * <pre>
     * MutableCollection&lt;String&gt; list = list.without("1");
     * list = list.without("2");
     * return list;
     * </pre>
     * In the case of {@link FixedSizeCollection} a new instance of MutableCollection will be returned by without, and
     * any variables that previously referenced the original collection will need to be redirected to reference the
     * new instance. For other MutableCollection types you will replace the reference to collection with the same
     * collection, since the instance will return "this" after calling remove on itself.
     *
     * @see #remove(Object)
     */
    MutableCollection<T> without(T element);

    /**
     * This method allows mutable and fixed size collections the ability to add multiple elements to their existing
     * elements. In order to support fixed size a new instance of a collection would have to be returned taking the
     * elements of  the original collection and appending the new elements to form the new collection. In the case of
     * mutable collections, the original collection is modified, and is returned. In order to use this method properly
     * with mutable and fixed size collections the following approach must be taken:
     *
     * <pre>
     * MutableCollection&lt;String&gt; list = list.withAll(FastList.newListWith("1", "2"));
     * </pre>
     * In the case of {@link FixedSizeCollection} a new instance of MutableCollection will be returned by withAll, and
     * any variables that previously referenced the original collection will need to be redirected to reference the
     * new instance. For other MutableCollection types you will replace the reference to collection with the same
     * collection, since the instance will return "this" after calling addAll on itself.
     *
     * @see #addAll(Collection)
     */
    MutableCollection<T> withAll(Iterable<? extends T> elements);

    /**
     * This method allows mutable and fixed size collections the ability to remove multiple elements from their existing
     * elements. In order to support fixed size a new instance of a collection would have to be returned containing the
     * elements that would be left from the original collection after calling removeAll. In the case of mutable
     * collections, the original collection is modified, and is returned. In order to use this method properly with
     * mutable and fixed size collections the following approach must be taken:
     *
     * <pre>
     * MutableCollection&lt;String&gt; list = list.withoutAll(FastList.newListWith("1", "2"));
     * </pre>
     * In the case of {@link FixedSizeCollection} a new instance of MutableCollection will be returned by withoutAll,
     * and any variables that previously referenced the original collection will need to be redirected to reference the
     * new instance. For other MutableCollection types you will replace the reference to collection with the same
     * collection, since the instance will return "this" after calling removeAll on itself.
     *
     * @see #removeAll(Collection)
     */
    MutableCollection<T> withoutAll(Iterable<? extends T> elements);

    /**
     * Creates a new empty mutable version of the same collection type. For example, if this instance is a FastList,
     * this method will return a new empty FastList. If the class of this instance is immutable or fixed size (i.e.
     * SingletonList) then a mutable alternative to the class will be provided.
     */
    MutableCollection<T> newEmpty();

    @Override
    MutableCollection<T> tap(Procedure<? super T> procedure);

    /**
     * Returns a MutableCollection with all elements that evaluate to true for the specified predicate.
     *
     * <pre>
     * MutableCollection&lt;Integer&gt; livesInLondon =
     *     people.select(person -&gt; person.getAddress().getCity().equals("London"));
     * </pre>
     */
    @Override
    MutableCollection<T> select(Predicate<? super T> predicate);

    /**
     * Returns a MutableCollection with all elements that evaluate to true for the specified predicate2 and parameter.
     *
     * <pre>
     * MutableCollection&lt;Integer&gt; fives =
     *     integers.selectWith(Predicates2.equal(), Integer.valueOf(5));
     * </pre>
     */
    @Override
    <P> MutableCollection<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns a MutableCollection with all elements that evaluate to false for the specified predicate.
     *
     * <pre>
     * MutableCollection&lt;Person&gt; notSmiths =
     *     people.reject(person -&gt; person.person.getLastName().equals("Smith"));
     * </pre>
     * Using the {@code Predicates} factory:
     *
     * <pre>
     * MutableCollection&lt;Person&gt; notSmiths = people.reject(Predicates.attributeEqual("lastName", "Smith"));
     * </pre>
     */
    @Override
    MutableCollection<T> reject(Predicate<? super T> predicate);

    /**
     * Returns a MutableCollection with all elements that evaluate to false for the specified predicate2 and parameter.
     *
     * <pre>e.g.
     * MutableCollection&lt;Integer&gt; selected =
     *     integers.rejectWith(Predicates2.equal(), Integer.valueOf(5));
     * </pre>
     */
    @Override
    <P> MutableCollection<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Filters a collection into two separate collections based on a predicate returned via a Pair.
     *
     * <pre>e.g.
     * return lastNames.<b>selectAndRejectWith</b>(Predicates2.lessThan(), "Mason");
     * </pre>
     *
     * @deprecated since 6.0 use {@link RichIterable#partitionWith(Predicate2, Object)} instead.
     */
    @Deprecated
    <P> Twin<MutableList<T>> selectAndRejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * PartitionMutableCollection&lt;Person&gt; newYorkersAndNonNewYorkers =
     *     people.partition(person -&gt; person.getAddress().getState().getName().equals("New York"));
     * </pre>
     */
    @Override
    PartitionMutableCollection<T> partition(Predicate<? super T> predicate);

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * PartitionMutableCollection&lt;Person&gt; newYorkersAndNonNewYorkers =
     *     people.partitionWith((Person person, String state) -&gt; person.getAddress().getState().getName().equals(state), "New York");
     * </pre>
     */
    @Override
    <P> PartitionMutableCollection<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableCollection&lt;Integer&gt; integers =
     *     List.mutable.with(new Integer(0), new Long(0L), new Double(0.0)).selectInstancesOf(Integer.class);
     * </pre>
     *
     * @since 2.0
     */
    @Override
    <S> MutableCollection<S> selectInstancesOf(Class<S> clazz);

    /**
     * Removes all elements in the collection that evaluate to true for the specified predicate.
     *
     * <pre>e.g.
     * return lastNames.<b>removeIf</b>(Predicates.isNull());
     * </pre>
     */
    boolean removeIf(Predicate<? super T> predicate);

    /**
     * Removes all elements in the collection that evaluate to true for the specified predicate2 and parameter.
     *
     * <pre>
     * return lastNames.<b>removeIfWith</b>(Predicates2.isNull(), null);
     * </pre>
     */
    <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns a new MutableCollection with the results of applying the specified function to each element of the source
     * collection.
     *
     * <pre>
     * MutableCollection&lt;String&gt; names =
     *     people.collect(person -&gt; person.getFirstName() + " " + person.getLastName());
     * </pre>
     */
    @Override
    <V> MutableCollection<V> collect(Function<? super T, ? extends V> function);

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableBooleanCollection licenses =
     *     people.collectBoolean(person -&gt; person.hasDrivingLicense());
     * </pre>
     */
    @Override
    default MutableBooleanCollection collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.collectBoolean(booleanFunction, BooleanLists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableByteCollection bytes =
     *     people.collectByte(person -&gt; person.getCode());
     * </pre>
     */
    @Override
    default MutableByteCollection collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.collectByte(byteFunction, ByteLists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableCharCollection chars =
     *     people.collectChar(person -&gt; person.getMiddleInitial());
     * </pre>
     */
    @Override
    default MutableCharCollection collectChar(CharFunction<? super T> charFunction)
    {
        return this.collectChar(charFunction, CharLists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableDoubleCollection doubles =
     *     people.collectDouble(person -&gt; person.getMilesFromNorthPole());
     * </pre>
     */
    @Override
    default MutableDoubleCollection collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.collectDouble(doubleFunction, DoubleLists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableFloatCollection floats =
     *     people.collectFloat(person -&gt; person.getHeightInInches());
     * </pre>
     */
    @Override
    default MutableFloatCollection collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.collectFloat(floatFunction, FloatLists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableIntCollection ints =
     *     people.collectInt(person -&gt; person.getAge());
     * </pre>
     */
    @Override
    default MutableIntCollection collectInt(IntFunction<? super T> intFunction)
    {
        return this.collectInt(intFunction, IntLists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableLongCollection longs =
     *     people.collectLong(person -&gt; person.getGuid());
     * </pre>
     */
    @Override
    default MutableLongCollection collectLong(LongFunction<? super T> longFunction)
    {
        return this.collectLong(longFunction, LongLists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableShortCollection shorts =
     *     people.collectShort(person -&gt; person.getNumberOfJunkMailItemsReceivedPerMonth());
     * </pre>
     */
    @Override
    default MutableShortCollection collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.collectShort(shortFunction, ShortLists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableCollection&lt;Integer&gt; integers =
     *     Lists.mutable.with(1, 2, 3).collectWith((each, parameter) -&gt; each + parameter, Integer.valueOf(1));
     * </pre>
     */
    @Override
    <P, V> MutableCollection<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    /**
     * Returns a new MutableCollection with the results of applying the specified function to each element of the source
     * collection, but only for elements that evaluate to true for the specified predicate.
     *
     * <pre>
     * MutableCollection&lt;String&gt; collected =
     *     Lists.mutable.of().with(1, 2, 3).collectIf(Predicates.notNull(), Functions.getToString())
     * </pre>
     */
    @Override
    <V> MutableCollection<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * Function&lt;Person, List&lt;Address&gt;&gt; addressFunction = Person::getAddresses;
     * MutableCollection&lt;Person&gt; people = ...;
     * MutableCollection&lt;List&lt;Address&gt;&gt; addresses = people.collect(addressFunction);
     * MutableCollection&lt;Address&gt; addresses = people.flatCollect(addressFunction);
     * </pre>
     *
     * @param function The {@link Function} to apply
     * @return a new flattened collection produced by applying the given {@code function}
     * @since 1.0
     */
    @Override
    <V> MutableCollection<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> MutableCollection<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * Returns the final result of evaluating function using each element of the iterable, the previous evaluation
     * result and the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter. The parameter value is always used as the third parameter
     * to the function call.
     *
     * @see #injectInto(Object, Function2)
     */
    <IV, P> IV injectIntoWith(
            IV injectValue,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter);

    /**
     * Returns an unmodifiable view of this collection. This is the equivalent of using
     * {@code Collections.unmodifiableCollection(this)} with a return type that supports the full
     * iteration protocols available on {@code MutableCollection}. Methods which would
     * mutate the underlying collection will throw UnsupportedOperationExceptions.
     *
     * @return an unmodifiable view of this collection.
     * @see java.util.Collections#unmodifiableCollection(Collection)
     * @since 1.0
     */
    MutableCollection<T> asUnmodifiable();

    /**
     * Returns a synchronized wrapper backed by this collection. This is the equivalent of using
     * {@code Collections.synchronizedCollection(this)} only with a return type that supports the full
     * iteration protocols available on {@code MutableCollection}.
     *
     * The preferred way of iterating over a synchronized collection is to use the internal iteration
     * methods which are properly synchronized internally.
     *
     * <pre>
     *  MutableCollection synchedCollection = collection.asSynchronized();
     *     ...
     *  synchedCollection.forEach(each -&gt; ... );
     *  synchedCollection.select(each -&gt; ... );
     *  synchedCollection.collect(each -&gt; ... );
     * </pre>
     *
     * If you want to iterate using an imperative style, you must protect external iterators using
     * a synchronized block. This includes explicit iterators as well as JDK 5 style for loops.
     * <p>
     *
     * @return a synchronized view of this collection.
     * @see java.util.Collections#synchronizedCollection(Collection)
     * @since 1.0
     */
    MutableCollection<T> asSynchronized();

    /**
     * Converts this {@code MutableCollection} to an {@code ImmutableCollection}.
     *
     * @since 1.0
     */
    ImmutableCollection<T> toImmutable();

    @Override
    <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function);

    @Override
    <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function);

    @Override
    <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function);

    @Override
    <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function);

    /**
     * @since 9.0
     */
    @Override
    default <V> MutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.asLazy().<V>collect(function).toBag();
    }

    /**
     * @since 9.0
     */
    @Override
    default <V, P> MutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.asLazy().<P, V>collectWith(function, parameter).toBag();
    }

    /**
     * @since 10.0.0
     */
    @Override
    default <V> MutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.asLazy().flatCollect(function).toBag();
    }

    /**
     * {@inheritDoc}
     * Co-variant example for MutableCollection:
     * <pre>
     * MutableMultimap&lt;String, Person&gt; peopleByLastName =
     *     people.groupBy(Person::getLastName);
     * </pre>
     */
    @Override
    <V> MutableMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> MutableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    default <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.groupByUniqueKey(function, Maps.mutable.withInitialCapacity(this.size()));
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> MutableCollection<Pair<T, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    MutableCollection<Pair<T, Integer>> zipWithIndex();

    /**
     * @see #addAll(Collection)
     * @since 1.0
     */
    boolean addAllIterable(Iterable<? extends T> iterable);

    /**
     * @see #removeAll(Collection)
     * @since 1.0
     */
    boolean removeAllIterable(Iterable<?> iterable);

    /**
     * @see #retainAll(Collection)
     * @since 1.0
     */
    boolean retainAllIterable(Iterable<?> iterable);

    @Override
    default <K, V> MutableMap<K, V> aggregateInPlaceBy(
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

    @Override
    default <K, V> MutableMap<K, V> aggregateBy(
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

    @Override
    default <K> MutableMapIterable<K, T> reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction)
    {
        return this.reduceBy(groupBy, reduceFunction, Maps.mutable.empty());
    }
}
