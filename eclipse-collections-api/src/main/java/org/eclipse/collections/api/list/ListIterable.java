/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.list;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.annotation.category.Aggregating;
import org.eclipse.collections.api.annotation.category.Converting;
import org.eclipse.collections.api.annotation.category.Filtering;
import org.eclipse.collections.api.annotation.category.Finding;
import org.eclipse.collections.api.annotation.category.Grouping;
import org.eclipse.collections.api.annotation.category.Iterating;
import org.eclipse.collections.api.annotation.category.Testing;
import org.eclipse.collections.api.annotation.category.Transforming;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.FloatList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.ShortList;
import org.eclipse.collections.api.multimap.list.ListMultimap;
import org.eclipse.collections.api.ordered.ReversibleIterable;
import org.eclipse.collections.api.partition.list.PartitionList;
import org.eclipse.collections.api.tuple.Pair;

/**
 * An iterable whose items are ordered and may be accessed directly by index. A reverseForEach
 * internal iterator is available iterating over the indexed iterable in reverse, starting from
 * the end and going to the beginning. Additionally, internal iterators are available for batching
 * style iteration which is useful for parallel processing.
 * <p>
 * The methods in ListIterable are organized into method categories via category annotations.
 * Links are provided below as a convenience to help discover specific methods in Javadoc.
 *
 * <ul>
 * <li><b>Aggregating 📊</b>
 * <ul><li>
 * {@link #hashCode()}
 * </li></ul>
 * <li><b>Converting 🔌</b>
 * <ul><li>
 * {@link #toImmutable()}, {@link #toReversed()}
 * </li></ul>
 * <li><b>Filtering 🚰</b>
 * <ul><li>
 * {@link #distinct()}, {@link #distinct(HashingStrategy)}, {@link #distinctBy(Function)}, {@link #drop(int)},
 * {@link #dropWhile(Predicate)}, {@link #partition(Predicate)}, {@link #partitionWhile(Predicate)},
 * {@link #partitionWith(Predicate2, Object)}, {@link #reject(Predicate)}, {@link #rejectWith(Predicate2, Object)},
 * {@link #rejectWithIndex(ObjectIntPredicate)}, {@link #select(Predicate)}, {@link #selectInstancesOf(Class)},
 * {@link #selectWith(Predicate2, Object)}, {@link #selectWithIndex(ObjectIntPredicate)}, {@link #subList(int, int)},
 * {@link #take(int)}, {@link #takeWhile(Predicate)}
 * </li></ul>
 * <li><b>Finding 🔎</b>
 * <ul><li>
 * {@link #binarySearch(Object)}, {@link #binarySearch(Object, Comparator)}, {@link #get(int)}, {@link #getFirst()},
 * {@link #getLast()}, {@link #lastIndexOf(Object)}
 * </li></ul>
 * <li><b>Grouping 🏘️</b>
 * <ul><li>
 * {@link #groupBy(Function)}, {@link #groupByEach(Function)}
 * </li></ul>
 * <li><b>Iterating 🔄</b>
 * <ul><li>
 * {@link #asParallel(ExecutorService, int)}, {@link #forEachInBoth(ListIterable, Procedure2)}, {@link #listIterator()},
 * {@link #listIterator(int)}, {@link #tap(Procedure)}
 * </li></ul>
 * <li><b>Testing 🧪</b>
 * <ul><li>
 * {@link #equals(Object)}
 * </li></ul>
 * <li><b>Transforming 🦋</b>
 * <ul><li>
 * {@link #collect(Function)}, {@link #collectBoolean(BooleanFunction)}, {@link #collectByte(ByteFunction)},
 * {@link #collectChar(CharFunction)}, {@link #collectDouble(DoubleFunction)}, {@link #collectFloat(FloatFunction)},
 * {@link #collectIf(Predicate, Function)}, {@link #collectInt(IntFunction)}, {@link #collectLong(LongFunction)},
 * {@link #collectShort(ShortFunction)}, {@link #collectWith(Function2, Object)},
 * {@link #collectWithIndex(ObjectIntToObjectFunction)}, {@link #flatCollect(Function)},
 * {@link #flatCollectWith(Function2, Object)}, {@link #zip(Iterable)}, {@link #zipWithIndex()}
 * </li></ul>
 * </ul>
 */
public interface ListIterable<T>
        extends ReversibleIterable<T>
{
    /**
     * Returns the item at the specified position in this list iterable.
     */
    @Finding
    T get(int index);

    /**
     * Returns the index of the last occurrence of the specified item
     * in this list, or -1 if this list does not contain the item.
     */
    @Finding
    int lastIndexOf(Object o);

    /**
     * Returns the item at index 0 of the container. If the container is empty, null is returned. If null
     * is a valid item of the container, then a developer will need to check to see if the container is
     * empty first.
     */
    @Override
    @Finding
    T getFirst();

    /**
     * Returns the item at index (size() - 1) of the container. If the container is empty, null is returned. If null
     * is a valid item of the container, then a developer will need to check to see if the container is
     * empty first.
     */
    @Override
    @Finding
    T getLast();

    /**
     * @see List#listIterator()
     * @since 1.0.
     */
    @Iterating
    ListIterator<T> listIterator();

    /**
     * @see List#listIterator(int)
     * @since 1.0.
     */
    @Iterating
    ListIterator<T> listIterator(int index);

    /**
     * Converts the ListIterable to an immutable implementation. Returns this for immutable lists.
     *
     * @since 5.0
     */
    @Converting
    ImmutableList<T> toImmutable();

    @Override
    @Iterating
    ListIterable<T> tap(Procedure<? super T> procedure);

    @Override
    @Filtering
    ListIterable<T> select(Predicate<? super T> predicate);

    @Override
    @Filtering
    <P> ListIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    @Filtering
    ListIterable<T> reject(Predicate<? super T> predicate);

    @Override
    @Filtering
    <P> ListIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    @Filtering
    PartitionList<T> partition(Predicate<? super T> predicate);

    @Override
    @Filtering
    <P> PartitionList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    @Filtering
    <S> ListIterable<S> selectInstancesOf(Class<S> clazz);

    @Override
    @Transforming
    <V> ListIterable<V> collect(Function<? super T, ? extends V> function);

    /**
     * @since 9.1.
     */
    @Override
    @Transforming
    default <V> ListIterable<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

    /**
     * Returns a new ListIterable with all elements of the collection that return true when evaluating the specified
     * predicate which is supplied each element and its relative index.
     *
     * @since 11.0
     */
    @Filtering
    default ListIterable<T> selectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        int[] index = {0};
        return this.select(each -> predicate.accept(each, index[0]++));
    }

    /**
     * Returns a new ListIterable with all elements of the collection that return false when evaluating the specified
     * predicate which is supplied each element and its relative index.
     *
     * @since 11.0
     */
    @Filtering
    default ListIterable<T> rejectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        int[] index = {0};
        return this.select(each -> predicate.accept(each, index[0]++));
    }

    @Override
    @Transforming
    BooleanList collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    @Transforming
    ByteList collectByte(ByteFunction<? super T> byteFunction);

    @Override
    @Transforming
    CharList collectChar(CharFunction<? super T> charFunction);

    @Override
    @Transforming
    DoubleList collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    @Transforming
    FloatList collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    @Transforming
    IntList collectInt(IntFunction<? super T> intFunction);

    @Override
    @Transforming
    LongList collectLong(LongFunction<? super T> longFunction);

    @Override
    @Transforming
    ShortList collectShort(ShortFunction<? super T> shortFunction);

    @Override
    @Transforming
    <P, V> ListIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    @Transforming
    <V> ListIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    @Transforming
    <V> ListIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    @Transforming
    default <P, V> ListIterable<V> flatCollectWith(
            Function2<? super T, ? super P, ? extends Iterable<V>> function,
            P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    @Grouping
    <V> ListMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    @Grouping
    <V> ListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Returns a new {@code ListIterable} containing the distinct elements in this list.
     * <p>
     * Conceptually similar to {@link #toSet()}.{@link #toList()} but retains the original order. If an element appears
     * multiple times in this list, the first one will be copied into the result.
     *
     * @return {@code ListIterable} of distinct elements
     * @since 3.0
     */
    @Override
    @Filtering
    ListIterable<T> distinct();

    /**
     * Returns a new {@code ListIterable} containing the distinct elements in this list. Takes a HashingStrategy.
     *
     * @return {@code ListIterable} of distinct elements
     * @since 7.0
     */
    @Filtering
    ListIterable<T> distinct(HashingStrategy<? super T> hashingStrategy);

    /**
     * Returns a new {@code ListIterable} containing the distinct elements in this list.
     * The specified function will be used to create a HashingStrategy to unique the elements.
     *
     * @see ListIterable#distinct(HashingStrategy)
     * @since 9.0
     */
    @Filtering
    <V> ListIterable<T> distinctBy(Function<? super T, ? extends V> function);

    @Override
    @Transforming
    <S> ListIterable<Pair<T, S>> zip(Iterable<S> that);

    @Override
    @Transforming
    ListIterable<Pair<T, Integer>> zipWithIndex();

    @Override
    @Filtering
    ListIterable<T> take(int count);

    /**
     * Returns the initial elements that satisfy the Predicate. Short circuits at the first element which does not
     * satisfy the Predicate.
     *
     * @since 3.0
     */
    @Override
    @Filtering
    ListIterable<T> takeWhile(Predicate<? super T> predicate);

    @Override
    @Filtering
    ListIterable<T> drop(int count);

    /**
     * Returns the final elements that do not satisfy the Predicate. Short circuits at the first element which does
     * satisfy the Predicate.
     *
     * @since 3.0
     */
    @Override
    @Filtering
    ListIterable<T> dropWhile(Predicate<? super T> predicate);

    /**
     * Returns a Partition of the initial elements that satisfy the Predicate and the remaining elements. Short circuits at the first element which does
     * satisfy the Predicate.
     *
     * @since 3.0
     */
    @Override
    @Filtering
    PartitionList<T> partitionWhile(Predicate<? super T> predicate);

    @Override
    @Converting
    ListIterable<T> toReversed();

    /**
     * Returns a parallel iterable of this ListIterable.
     *
     * @since 6.0
     */
    @Beta
    @Iterating
    ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize);

    /**
     * Searches for the specified object using the binary search algorithm. The list must be sorted into ascending
     * order according to the specified comparator.
     *
     * @see Collections#binarySearch(List, Object, Comparator)
     */
    @Finding
    default int binarySearch(T key, Comparator<? super T> comparator)
    {
        return Collections.binarySearch((List<? extends T>) this, key, comparator);
    }

    /**
     * Searches for the specified object using the binary search algorithm. The elements in this list must implement
     * Comparable and the list must be sorted into ascending order.
     *
     * @see Collections#binarySearch(List, Object)
     */
    @Finding
    default int binarySearch(T key)
    {
        return Collections.binarySearch((List<? extends Comparable<? super T>>) this, key);
    }

    /**
     * Follows the same general contract as {@link List#equals(Object)}.
     */
    @Override
    @Testing
    boolean equals(Object o);

    /**
     * Follows the same general contract as {@link List#hashCode()}.
     */
    @Override
    @Aggregating
    int hashCode();

    /**
     * @see List#subList(int, int)
     * @since 6.0
     */
    @Filtering
    ListIterable<T> subList(int fromIndex, int toIndex);

    /**
     * Iterates over this ListIterable and the other ListIterable together passing
     * the elements of each list as parameters to the specified procedure.
     *
     * @since 10.3
     */
    @Iterating
    default <T2> void forEachInBoth(ListIterable<T2> other, Procedure2<? super T, ? super T2> procedure)
    {
        Objects.requireNonNull(other);
        if (this.size() == other.size())
        {
            this.forEachWithIndex((each, index) -> procedure.value(each, other.get(index)));
        }
        else
        {
            throw new IllegalArgumentException("Attempt to call forEachInBoth with two Lists of different sizes :"
                    + this.size()
                    + ':'
                    + other.size());
        }
    }
}
