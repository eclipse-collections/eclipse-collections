/*
 * Copyright (c) 2023 Goldman Sachs and others.
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
import java.util.Random;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.factory.SerializableComparators;
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
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.ByteLists;
import org.eclipse.collections.api.factory.primitive.CharLists;
import org.eclipse.collections.api.factory.primitive.DoubleLists;
import org.eclipse.collections.api.factory.primitive.FloatLists;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.factory.primitive.ShortLists;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A MutableList is an extension of java.util.List which provides methods matching the Smalltalk Collection protocol.
 */
public interface MutableList<T>
        extends MutableCollection<T>, List<T>, Cloneable, ListIterable<T>
{
    @Override
    default Object[] toArray()
    {
        return MutableCollection.super.toArray();
    }

    @Override
    default <T1> T1[] toArray(T1[] a)
    {
        return MutableCollection.super.toArray(a);
    }

    /**
     * This default override exists because java.util.List added a default getFirst() method in Java 21.
     * @since 12.0
     */
    @Override
    default T getFirst()
    {
        return this.isEmpty() ? null : this.get(0);
    }

    /**
     * This default override exists because java.util.List added a default getLast() method in Java 21.
     * @since 12.0
     */
    @Override
    default T getLast()
    {
        return this.isEmpty() ? null : this.get(this.size() - 1);
    }

    @Override
    default MutableList<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    default MutableList<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    default MutableList<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    default MutableList<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    MutableList<T> newEmpty();

    MutableList<T> clone();

    @Override
    default MutableList<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    default MutableList<T> select(Predicate<? super T> predicate)
    {
        return this.select(predicate, this.newEmpty());
    }

    @Override
    default <P> MutableList<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.selectWith(predicate, parameter, this.newEmpty());
    }

    @Override
    default MutableList<T> reject(Predicate<? super T> predicate)
    {
        return this.reject(predicate, this.newEmpty());
    }

    @Override
    default <P> MutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.rejectWith(predicate, parameter, this.newEmpty());
    }

    @Override
    PartitionMutableList<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> MutableList<S> selectInstancesOf(Class<S> clazz);

    @Override
    default <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return this.collect(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * @since 9.1.
     */
    @Override
    default <V> MutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

    /**
     * Returns a new MutableList with all elements of the collection that return true when evaluating the specified
     * predicate which is supplied each element and its relative index.
     *
     * @since 11.0
     */
    @Override
    default MutableList<T> selectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        int[] index = {0};
        return this.select(each -> predicate.accept(each, index[0]++));
    }

    /**
     * Returns a new MutableList with all elements of the collection that return false when evaluating the specified
     * predicate which is supplied each element and its relative index.
     *
     * @since 11.0
     */
    @Override
    default MutableList<T> rejectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        int[] index = {0};
        return this.reject(each -> predicate.accept(each, index[0]++));
    }

    @Override
    default MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.collectBoolean(booleanFunction, BooleanLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.collectByte(byteFunction, ByteLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        return this.collectChar(charFunction, CharLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.collectDouble(doubleFunction, DoubleLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.collectFloat(floatFunction, FloatLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        return this.collectInt(intFunction, IntLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        return this.collectLong(longFunction, LongLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.collectShort(shortFunction, ShortLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collectWith(function, parameter, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    default <V> MutableList<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return this.collectIf(predicate, function, Lists.mutable.empty());
    }

    @Override
    default <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.flatCollect(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * @since 9.2
     */
    @Override
    default <P, V> MutableList<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * Returns a new {@code ListIterable} containing the distinct elements in this list.
     *
     * @since 7.0
     */
    @Override
    MutableList<T> distinct();

    /**
     * Returns a new {@code ListIterable} containing the distinct elements in this list. Takes HashingStrategy.
     *
     * @since 7.0
     */
    @Override
    MutableList<T> distinct(HashingStrategy<? super T> hashingStrategy);

    /**
     * @since 9.0
     */
    @Override
    <V> MutableList<T> distinctBy(Function<? super T, ? extends V> function);

    /**
     * Sorts the internal data structure of this list and returns the list itself as a convenience.
     *
     * @since 10.0 - Added default implementation.
     */
    default MutableList<T> sortThis(Comparator<? super T> comparator)
    {
        this.sort(comparator);
        return this;
    }

    /**
     * Sorts the internal data structure of this list and returns the list itself as a convenience.
     *
     * @since 10.0 - Added default implementation.
     */
    default MutableList<T> sortThis()
    {
        return this.sortThis(null);
    }

    /**
     * Sorts the internal data structure of this list based on the natural order of the attribute returned by {@code
     * function}.
     */
    default <V extends Comparable<? super V>> MutableList<T> sortThisBy(Function<? super T, ? extends V> function)
    {
        return this.sortThis(SerializableComparators.byFunction(function));
    }

    /**
     * @since 6.0
     */
    MutableList<T> sortThisByInt(IntFunction<? super T> function);

    /**
     * @since 6.0
     */
    MutableList<T> sortThisByBoolean(BooleanFunction<? super T> function);

    /**
     * @since 6.0
     */
    MutableList<T> sortThisByChar(CharFunction<? super T> function);

    /**
     * @since 6.0
     */
    MutableList<T> sortThisByByte(ByteFunction<? super T> function);

    /**
     * @since 6.0
     */
    MutableList<T> sortThisByShort(ShortFunction<? super T> function);

    /**
     * @since 6.0
     */
    MutableList<T> sortThisByFloat(FloatFunction<? super T> function);

    /**
     * @since 6.0
     */
    MutableList<T> sortThisByLong(LongFunction<? super T> function);

    /**
     * @since 6.0
     */
    MutableList<T> sortThisByDouble(DoubleFunction<? super T> function);

    @Override
    MutableList<T> subList(int fromIndex, int toIndex);

    /**
     * Returns an unmodifiable view of the list.
     *
     * @return an unmodifiable view of this list
     */
    @Override
    MutableList<T> asUnmodifiable();

    @Override
    MutableList<T> asSynchronized();

    /**
     * Returns an immutable copy of this list. If the list is immutable, it returns itself.
     */
    @Override
    default ImmutableList<T> toImmutable()
    {
        return Lists.immutable.withAll(this);
    }

    @Override
    <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    <S> MutableList<Pair<T, S>> zip(Iterable<S> that);

    @Override
    MutableList<Pair<T, Integer>> zipWithIndex();

    @Override
    MutableList<T> take(int count);

    @Override
    MutableList<T> takeWhile(Predicate<? super T> predicate);

    @Override
    MutableList<T> drop(int count);

    @Override
    MutableList<T> dropWhile(Predicate<? super T> predicate);

    @Override
    PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate);

    /**
     * Returns a new MutableList in reverse order.
     */
    @Override
    default MutableList<T> toReversed()
    {
        return this.toList().reverseThis();
    }

    /**
     * Mutates this list by reversing its order and returns the current list as a result.
     */
    default MutableList<T> reverseThis()
    {
        Collections.reverse(this);
        return this;
    }

    /**
     * Mutates this list by shuffling its elements.
     */
    default MutableList<T> shuffleThis()
    {
        Collections.shuffle(this);
        return this;
    }

    /**
     * Mutates this list by shuffling its elements using the specified random.
     */
    default MutableList<T> shuffleThis(Random random)
    {
        Collections.shuffle(this, random);
        return this;
    }

    /**
     * Converts the MutableList to the default ImmutableList implementation.
     *
     * @since 11.0
     */
    @Override
    default ImmutableList<T> toImmutableList()
    {
        return this.toImmutable();
    }

    /**
     * Returns a reverse-order view of this list. Changes to the returned list write through to this list,
     * and vice versa.
     */
    MutableList<T> reversed();

    @Override
    default int indexOf(Object o)
    {
        return ListIterable.super.indexOf(o);
    }
}
