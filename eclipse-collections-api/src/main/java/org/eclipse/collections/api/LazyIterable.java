/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api;

import java.util.Collection;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
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
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A LazyIterable is a RichIterable that defers evaluation for certain methods like select, reject, collect, etc.
 * This allows for efficient chaining of operations without creating intermediate collections. Evaluation is forced
 * only when a terminal operation (a method that does not return a LazyIterable) is called.
 * <p>
 * Lazy evaluation can significantly improve performance when working with large datasets or when only a subset
 * of results is needed, as elements are processed on-demand rather than all at once.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * LazyIterable<Person> adults = people.asLazy()
 *     .select(person -> person.getAge() >= 18);
 *
 * // No evaluation has occurred yet. Evaluation happens when we call toList()
 * MutableList<Person> adultList = adults.toList();
 *
 * // Chaining multiple lazy operations
 * LazyIterable<String> names = people.asLazy()
 *     .select(person -> person.getAge() >= 18)
 *     .collect(Person::getName);
 * }</pre>
 *
 * @param <T> the type of elements in the iterable
 * @since 1.0
 */
public interface LazyIterable<T>
        extends RichIterable<T>
{
    /**
     * Returns the first element of this LazyIterable, or null if the iterable is empty.
     * This is a terminal operation that forces evaluation of the lazy iterable.
     *
     * @return the first element, or null if empty
     */
    @Override
    T getFirst();

    @Override
    default ImmutableList<T> toImmutableList()
    {
        MutableList<T> mutableList = Lists.mutable.empty();
        this.forEach(mutableList::add);
        return mutableList.toImmutable();
    }

    @Override
    default ImmutableSet<T> toImmutableSet()
    {
        MutableSet<T> mutableSet = Sets.mutable.empty();
        this.forEach(mutableSet::add);
        return mutableSet.toImmutable();
    }

    @Override
    default ImmutableBag<T> toImmutableBag()
    {
        MutableBag<T> mutableBag = Bags.mutable.empty();
        this.forEach(mutableBag::add);
        return mutableBag.toImmutable();
    }

    @Override
    default Object[] toArray()
    {
        MutableList<T> mutableList = Lists.mutable.empty();
        this.forEach(mutableList::add);
        return mutableList.toArray();
    }

    @Override
    default <E> E[] toArray(E[] array)
    {
        MutableList<T> mutableList = Lists.mutable.empty();
        this.forEach(mutableList::add);
        return mutableList.toArray(array);
    }

    /**
     * Creates a deferred iterable for selecting elements from the current iterable that satisfy the predicate.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<Integer> evens = numbers.asLazy()
     *     .select(n -> n % 2 == 0);
     * }</pre>
     *
     * @param predicate the predicate to filter elements
     * @return a new LazyIterable containing only elements that satisfy the predicate
     */
    @Override
    LazyIterable<T> select(Predicate<? super T> predicate);

    /**
     * Creates a deferred iterable for selecting elements using a predicate with an additional parameter.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     *
     * @param predicate the predicate to filter elements, accepting an element and a parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return a new LazyIterable containing only elements that satisfy the predicate
     */
    @Override
    <P> LazyIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a deferred iterable for selecting elements that are instances of the specified class.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     *
     * @param clazz the class to filter by
     * @param <S> the type to select
     * @return a new LazyIterable containing only elements of the specified type
     */
    @Override
    <S> LazyIterable<S> selectInstancesOf(Class<S> clazz);

    /**
     * Creates a deferred iterable for rejecting elements from the current iterable that satisfy the predicate.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<Integer> nonZeros = numbers.asLazy()
     *     .reject(n -> n == 0);
     * }</pre>
     *
     * @param predicate the predicate to reject elements
     * @return a new LazyIterable containing only elements that do not satisfy the predicate
     */
    @Override
    LazyIterable<T> reject(Predicate<? super T> predicate);

    /**
     * Creates a deferred iterable for rejecting elements using a predicate with an additional parameter.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     *
     * @param predicate the predicate to reject elements, accepting an element and a parameter
     * @param parameter the parameter to pass to the predicate
     * @param <P> the type of the parameter
     * @return a new LazyIterable containing only elements that do not satisfy the predicate
     */
    @Override
    <P> LazyIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a deferred iterable for transforming elements from the current iterable using the specified function.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<String> names = people.asLazy()
     *     .collect(Person::getName);
     * }</pre>
     *
     * @param function the transformation function to apply to each element
     * @param <V> the type of the transformed elements
     * @return a new LazyIterable containing the transformed elements
     */
    @Override
    <V> LazyIterable<V> collect(Function<? super T, ? extends V> function);

    /**
     * Creates a deferred iterable for transforming elements using a function with an additional parameter.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     *
     * @param function the transformation function to apply to each element and parameter
     * @param parameter the parameter to pass to the function
     * @param <P> the type of the parameter
     * @param <V> the type of the transformed elements
     * @return a new LazyIterable containing the transformed elements
     */
    @Override
    <P, V> LazyIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    /**
     * Creates a deferred iterable for selecting and transforming elements from the current iterable.
     * This is the lazy equivalent of calling select followed by collect, and is more efficient
     * as it combines both operations.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<String> adultNames = people.asLazy()
     *     .collectIf(person -> person.getAge() >= 18, Person::getName);
     * }</pre>
     *
     * @param predicate the predicate to filter elements
     * @param function the transformation function to apply to selected elements
     * @param <V> the type of the transformed elements
     * @return a new LazyIterable containing the transformed elements that satisfied the predicate
     */
    @Override
    <V> LazyIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    /**
     * Creates a deferred take iterable that returns at most the first {@code count} elements.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<Integer> firstFive = numbers.asLazy().take(5);
     * }</pre>
     *
     * @param count the maximum number of elements to take
     * @return a new LazyIterable containing at most the first {@code count} elements
     */
    LazyIterable<T> take(int count);

    /**
     * Creates a deferred drop iterable that skips the first {@code count} elements.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<Integer> afterFirstTen = numbers.asLazy().drop(10);
     * }</pre>
     *
     * @param count the number of elements to skip
     * @return a new LazyIterable containing elements after skipping the first {@code count} elements
     */
    LazyIterable<T> drop(int count);

    /**
     * Creates a deferred iterable that takes elements while the predicate is satisfied.
     * Once an element fails the predicate, no more elements are taken.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     *
     * @param predicate the condition to test each element
     * @return a new LazyIterable containing elements taken while the predicate is satisfied
     * @see OrderedIterable#takeWhile(Predicate)
     * @since 8.0
     */
    LazyIterable<T> takeWhile(Predicate<? super T> predicate);

    /**
     * Creates a deferred iterable that drops elements while the predicate is satisfied.
     * Once an element fails the predicate, all remaining elements (including that element) are kept.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     *
     * @param predicate the condition to test each element
     * @return a new LazyIterable containing elements after dropping while the predicate is satisfied
     * @see OrderedIterable#dropWhile(Predicate)
     * @since 8.0
     */
    LazyIterable<T> dropWhile(Predicate<? super T> predicate);

    /**
     * Creates a deferred distinct iterable to get unique elements from the current iterable.
     * Duplicates are removed based on equals/hashCode comparison.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<Integer> uniqueNumbers = numbers.asLazy().distinct();
     * }</pre>
     *
     * @return a new LazyIterable containing only distinct elements
     * @since 5.0
     */
    LazyIterable<T> distinct();

    /**
     * Creates a deferred flattening iterable for the current iterable.
     * Each element is transformed to an Iterable, and all results are flattened into a single iterable.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<Address> allAddresses = people.asLazy()
     *     .flatCollect(Person::getAddresses);
     * }</pre>
     *
     * @param function the function that transforms each element to an Iterable
     * @param <V> the type of elements in the resulting iterable
     * @return a new LazyIterable containing all elements from the flattened iterables
     */
    @Override
    <V> LazyIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> LazyIterable<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * Creates a deferred iterable that concatenates this iterable with the specified iterable.
     * Elements from this iterable are returned first, followed by elements from the other iterable.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<Integer> combined = list1.asLazy().concatenate(list2);
     * }</pre>
     *
     * @param iterable the iterable to concatenate with this iterable
     * @return a new LazyIterable containing elements from both iterables
     */
    LazyIterable<T> concatenate(Iterable<T> iterable);

    /**
     * Creates a deferred zip iterable that pairs elements from this iterable with elements from the specified iterable.
     * If the iterables have different lengths, the resulting iterable will have the length of the shorter one.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     *
     * @param that the iterable to zip with this iterable
     * @param <S> the type of elements in the other iterable
     * @return a new LazyIterable containing pairs of corresponding elements
     */
    @Override
    <S> LazyIterable<Pair<T, S>> zip(Iterable<S> that);

    /**
     * Creates a deferred zipWithIndex iterable that pairs each element with its zero-based index.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     *
     * @return a new LazyIterable containing pairs of elements and their indices
     */
    @Override
    LazyIterable<Pair<T, Integer>> zipWithIndex();

    /**
     * Creates a deferred chunk iterable that partitions elements into fixed-size chunks.
     * The last chunk may contain fewer elements if the total number of elements is not evenly divisible.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called.
     *
     * @param size the number of elements per chunk
     * @return a new LazyIterable containing chunks of elements
     */
    @Override
    LazyIterable<RichIterable<T>> chunk(int size);

    /**
     * Creates a deferred tap iterable that executes the procedure for each element as it passes through.
     * This is useful for debugging or performing side effects without terminating the lazy chain.
     * This is a lazy operation and the procedure is executed only when elements are evaluated.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * LazyIterable<Person> logged = people.asLazy()
     *     .tap(person -> System.out.println("Processing: " + person.getName()))
     *     .select(person -> person.getAge() >= 18);
     * }</pre>
     *
     * @param procedure the procedure to execute for each element
     * @return this LazyIterable with the tap operation applied
     */
    @Override
    LazyIterable<T> tap(Procedure<? super T> procedure);

    /**
     * Iterates over this iterable adding all elements into the target collection.
     * This is a terminal operation that forces evaluation of the lazy iterable.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * MutableSet<Integer> evens = numbers.asLazy()
     *     .select(n -> n % 2 == 0)
     *     .into(Sets.mutable.empty());
     * }</pre>
     *
     * @param target the collection to add elements to
     * @param <R> the type of the target collection
     * @return the target collection with all elements added
     */
    @Override
    <R extends Collection<T>> R into(R target);

    /**
     * Returns a lazy BooleanIterable that transforms each element to a boolean value using the specified function.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called on the resulting iterable.
     *
     * @param booleanFunction the function that transforms each element to a boolean
     * @return a new LazyBooleanIterable containing the transformed boolean values
     */
    @Override
    LazyBooleanIterable collectBoolean(BooleanFunction<? super T> booleanFunction);

    /**
     * Returns a lazy ByteIterable that transforms each element to a byte value using the specified function.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called on the resulting iterable.
     *
     * @param byteFunction the function that transforms each element to a byte
     * @return a new LazyByteIterable containing the transformed byte values
     */
    @Override
    LazyByteIterable collectByte(ByteFunction<? super T> byteFunction);

    /**
     * Returns a lazy CharIterable that transforms each element to a char value using the specified function.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called on the resulting iterable.
     *
     * @param charFunction the function that transforms each element to a char
     * @return a new LazyCharIterable containing the transformed char values
     */
    @Override
    LazyCharIterable collectChar(CharFunction<? super T> charFunction);

    /**
     * Returns a lazy DoubleIterable that transforms each element to a double value using the specified function.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called on the resulting iterable.
     *
     * @param doubleFunction the function that transforms each element to a double
     * @return a new LazyDoubleIterable containing the transformed double values
     */
    @Override
    LazyDoubleIterable collectDouble(DoubleFunction<? super T> doubleFunction);

    /**
     * Returns a lazy FloatIterable that transforms each element to a float value using the specified function.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called on the resulting iterable.
     *
     * @param floatFunction the function that transforms each element to a float
     * @return a new LazyFloatIterable containing the transformed float values
     */
    @Override
    LazyFloatIterable collectFloat(FloatFunction<? super T> floatFunction);

    /**
     * Returns a lazy IntIterable that transforms each element to an int value using the specified function.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called on the resulting iterable.
     *
     * @param intFunction the function that transforms each element to an int
     * @return a new LazyIntIterable containing the transformed int values
     */
    @Override
    LazyIntIterable collectInt(IntFunction<? super T> intFunction);

    /**
     * Returns a lazy LongIterable that transforms each element to a long value using the specified function.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called on the resulting iterable.
     *
     * @param longFunction the function that transforms each element to a long
     * @return a new LazyLongIterable containing the transformed long values
     */
    @Override
    LazyLongIterable collectLong(LongFunction<? super T> longFunction);

    /**
     * Returns a lazy ShortIterable that transforms each element to a short value using the specified function.
     * This is a lazy operation and evaluation is deferred until a terminal operation is called on the resulting iterable.
     *
     * @param shortFunction the function that transforms each element to a short
     * @return a new LazyShortIterable containing the transformed short values
     */
    @Override
    LazyShortIterable collectShort(ShortFunction<? super T> shortFunction);
}
