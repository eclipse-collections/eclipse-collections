/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.ordered;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.Function3;
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
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Stacks;
import org.eclipse.collections.api.multimap.ordered.OrderedIterableMultimap;
import org.eclipse.collections.api.ordered.primitive.OrderedBooleanIterable;
import org.eclipse.collections.api.ordered.primitive.OrderedByteIterable;
import org.eclipse.collections.api.ordered.primitive.OrderedCharIterable;
import org.eclipse.collections.api.ordered.primitive.OrderedDoubleIterable;
import org.eclipse.collections.api.ordered.primitive.OrderedFloatIterable;
import org.eclipse.collections.api.ordered.primitive.OrderedIntIterable;
import org.eclipse.collections.api.ordered.primitive.OrderedLongIterable;
import org.eclipse.collections.api.ordered.primitive.OrderedShortIterable;
import org.eclipse.collections.api.partition.ordered.PartitionOrderedIterable;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;

/**
 * An OrderedIterable is a RichIterable with some meaningful order, such as insertion order, access order, or sorted order.
 *
 * @since 6.0
 */
public interface OrderedIterable<T> extends RichIterable<T>
{
    /**
     * Returns the index of the first occurrence of the specified item
     * in this iterable, or -1 if this iterable does not contain the item.
     *
     * @see List#indexOf(Object)
     */
    default int indexOf(Object object)
    {
        return this.detectIndex(each -> Objects.equals(each, object));
    }

    /**
     * Returns the first element of an iterable. In the case of a List it is the element at the first index. In the
     * case of any other Collection, it is the first element that would be returned during an iteration. If the
     * iterable is empty, null is returned. If null is a valid element of the container, then a developer would need to
     * check to see if the iterable is empty to validate that a null result was not due to the container being empty.
     */
    @Override
    T getFirst();

    /**
     * Returns the first element of an iterable as an Optional. In the case of a List it is the element at the first index.
     * In the case of any other Collection, it is the first element that would be returned during an iteration. If the
     * iterable is empty, {@link Optional#empty} is returned.
     *
     * @throws NullPointerException if the element is null
     * @since 8.2
     */
    default Optional<T> getFirstOptional()
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.getFirst());
    }

    /**
     * Returns the last element of an iterable. In the case of a List it is the element at the last index. In the case
     * of any other Collection, it is the last element that would be returned during an iteration. If the iterable is
     * empty, null is returned. If null is a valid element of the container, then a developer would need to check to
     * see if the iterable is empty to validate that a null result was not due to the container being empty.
     */
    @Override
    T getLast();

    /**
     * Returns the last element of an iterable as an Optional. In the case of a List it is the element at the last index.
     * In the case of any other Collection, it is the last element that would be returned during an iteration. If the
     * iterable is empty, {@link Optional#empty} is returned.
     *
     * @throws NullPointerException if the element is null
     * @since 8.2
     */
    default Optional<T> getLastOptional()
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.getLast());
    }

    @Override
    OrderedIterable<T> tap(Procedure<? super T> procedure);

    /**
     * Returns the initial elements that satisfy the Predicate. Short circuits at the first element which does not
     * satisfy the Predicate.
     */
    OrderedIterable<T> takeWhile(Predicate<? super T> predicate);

    /**
     * Returns the final elements that do not satisfy the Predicate. Short circuits at the first element which does
     * satisfy the Predicate.
     */
    OrderedIterable<T> dropWhile(Predicate<? super T> predicate);

    /**
     * Returns a Partition of the initial elements that satisfy the Predicate and the remaining elements. Short circuits at the first element which does
     * satisfy the Predicate.
     */
    PartitionOrderedIterable<T> partitionWhile(Predicate<? super T> predicate);

    /**
     * Returns a new {@code OrderedIterable} containing the distinct elements in this iterable.
     * <p>
     * Conceptually similar to {@link #toSet()}.{@link #toList()} but retains the original order. If an element appears
     * multiple times in this iterable, the first one will be copied into the result.
     *
     * @return {@code OrderedIterable} of distinct elements
     */
    OrderedIterable<T> distinct();

    /**
     * Returns true if both OrderedIterables have the same length
     * and {@code predicate} returns true for all corresponding elements e1 of
     * this {@code OrderedIterable} and e2 of {@code other}.
     * The {@code predicate} is evaluated for each element at the same position of each {@code OrderedIterable} in a forward iteration order.
     * This is a short circuit pattern.
     *
     * @since 6.0
     */
    <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate);

    /**
     * Iterates over the section of the iterable covered by the specified inclusive indexes. The indexes are
     * both inclusive.
     *
     * <pre>e.g.
     * OrderedIterable&lt;Person&gt; people = FastList.newListWith(ted, mary, bob, sally)
     * people.forEach(0, 1, person -&gt; LOGGER.info(person.getName()));
     * </pre>
     * <p>
     * This code would output ted and mary's names.
     */
    void forEach(int startIndex, int endIndex, Procedure<? super T> procedure);

    /**
     * Iterates over the iterable passing each element and the current relative int index to the specified instance of
     * ObjectIntProcedure
     * <pre>e.g.
     * people.forEachWithIndex((person, index) -&gt; LOGGER.info("Index: " + index + " person: " + person.getName()));
     * </pre>
     */
    @Override
    void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure);

    /**
     * Iterates over the section of the iterable covered by the specified inclusive indexes. The indexes are
     * both inclusive.
     *
     * <pre>e.g.
     * OrderedIterable&lt;People&gt; people = FastList.newListWith(ted, mary, bob, sally)
     * people.forEachWithIndex(0, 1, (person, index) -&gt; LOGGER.info(person.getName()));
     * </pre>
     * <p>
     * This code would output ted and mary's names.
     */
    void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure);

    /**
     * Converts the OrderedIterable to a mutable MutableStack implementation.
     */
    default MutableStack<T> toStack()
    {
        return Stacks.mutable.withAll(this);
    }

    /**
     * Returns the minimum element out of this container based on the natural order, not the order of this container.
     * If you want the minimum element based on the order of this container, use {@link #getFirst()}.
     *
     * @throws ClassCastException     if the elements are not {@link Comparable}
     * @throws NoSuchElementException if the OrderedIterable is empty
     */
    @Override
    T min();

    /**
     * Returns the maximum element out of this container based on the natural order, not the order of this container.
     * If you want the maximum element based on the order of this container, use {@link #getLast()}.
     *
     * @throws ClassCastException     if the elements are not {@link Comparable}
     * @throws NoSuchElementException if the OrderedIterable is empty
     */
    @Override
    T max();

    @Override
    OrderedIterable<T> select(Predicate<? super T> predicate);

    @Override
    <P> OrderedIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    OrderedIterable<T> reject(Predicate<? super T> predicate);

    @Override
    <P> OrderedIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionOrderedIterable<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionOrderedIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> OrderedIterable<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> OrderedIterable<V> collect(Function<? super T, ? extends V> function);

    /**
     * Returns a new OrderedIterable using results obtained by applying the specified function to each element
     * and its corresponding index.
     *
     * @since 9.1.
     */
    default <V> OrderedIterable<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

    /**
     * Adds elements to the target Collection using results obtained by applying the specified function to each element
     * and its corresponding index.
     *
     * @since 9.1.
     */
    default <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++), target);
    }

    /**
     * Adds all elements to the target Collection that return true when evaluating the specified predicate which is
     * supplied each element and its relative index.
     *
     * @since 11.0
     */
    default <R extends Collection<T>> R selectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        int[] index = {0};
        return this.select(each -> predicate.accept(each, index[0]++), target);
    }

    /**
     * Adds all elements to the target Collection that return false when evaluating the specified predicate which is
     * supplied each element and its relative index.
     *
     * @since 11.0
     */
    default <R extends Collection<T>> R rejectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        int[] index = {0};
        return this.reject(each -> predicate.accept(each, index[0]++), target);
    }

    @Override
    <P, V> OrderedIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> OrderedIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> OrderedIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> OrderedIterable<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    OrderedBooleanIterable collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    OrderedByteIterable collectByte(ByteFunction<? super T> byteFunction);

    @Override
    OrderedCharIterable collectChar(CharFunction<? super T> charFunction);

    @Override
    OrderedDoubleIterable collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    OrderedFloatIterable collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    OrderedIntIterable collectInt(IntFunction<? super T> intFunction);

    @Override
    OrderedLongIterable collectLong(LongFunction<? super T> longFunction);

    @Override
    OrderedShortIterable collectShort(ShortFunction<? super T> shortFunction);

    /**
     * Returns the index of the first element of the {@code OrderedIterable} for which the {@code predicate} evaluates to true.
     * Returns -1 if no element evaluates true for the {@code predicate}.
     *
     * @since 6.0
     */
    int detectIndex(Predicate<? super T> predicate);

    @Override
    <V> OrderedIterableMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> OrderedIterableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Returns a {@code OrderedIterable} formed from this {@code OrderedIterable} and another {@code Iterable} by
     * combining corresponding elements in pairs. The second {@code Iterable} should also be ordered.
     * If one of the two {@code Iterable}s is longer than the other, its
     * remaining elements are ignored.
     *
     * @param that The {@code Iterable} providing the second half of each result pair
     * @param <S>  the type of the second half of the returned pairs
     * @return A new {@code OrderedIterable} containing pairs consisting of corresponding elements of this {@code
     * OrderedIterable} and that. The length of the returned {@code OrderedIterable} is the minimum of the lengths of
     * this {@code OrderedIterable} and that.
     */
    @Override
    <S> OrderedIterable<Pair<T, S>> zip(Iterable<S> that);

    /**
     * Same as {@link #zip(Iterable)} but uses {@code target} for output.
     */
    @Override
    <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target);

    @Override
    OrderedIterable<Pair<T, Integer>> zipWithIndex();

    /**
     * Same as {@link #zipWithIndex()} but uses {@code target} for output.
     */
    @Override
    <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target);

    /**
     * Returns the final result of evaluating function using each element of the iterable, the index of that element,
     * and the previous evaluation result as the parameters. The injected value is used for the first parameter of the
     * first evaluation, and the current item in the iterable is used as the second parameter, and the current item's
     * index as the third parameter. This method is commonly called foldIndexed or sometimes reduce.
     *
     * @since 14.0
     * @see {@link #injectInto(Object, Function2)}
     * @param injectedValue first parameter of first evaluation
     * @param function function invoked with arguments: injectedValue or previous result, element, and index of element
     */

    default <IV> IV injectIntoWithIndex(IV injectedValue, Function3<? super IV, ? super T, Integer, ? extends IV> function) {
        int[] index = {0};
        return this.injectInto(injectedValue, (prev, each) -> function.value(prev, each, index[0]++));
    }
}
