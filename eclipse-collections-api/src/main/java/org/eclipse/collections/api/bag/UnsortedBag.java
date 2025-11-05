/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag;

import org.eclipse.collections.api.bag.primitive.BooleanBag;
import org.eclipse.collections.api.bag.primitive.ByteBag;
import org.eclipse.collections.api.bag.primitive.CharBag;
import org.eclipse.collections.api.bag.primitive.DoubleBag;
import org.eclipse.collections.api.bag.primitive.FloatBag;
import org.eclipse.collections.api.bag.primitive.IntBag;
import org.eclipse.collections.api.bag.primitive.LongBag;
import org.eclipse.collections.api.bag.primitive.ShortBag;
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
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.multimap.bag.UnsortedBagMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionUnsortedBag;
import org.eclipse.collections.api.set.UnsortedSetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * UnsortedBag is a Bag whose iteration order is not deterministic. This is the parent interface
 * for bags that do not maintain any specific ordering of their elements.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * UnsortedBag<String> bag = Bags.mutable.of("C", "A", "B", "A");
 * // Iteration order is not guaranteed
 * int countOfA = bag.occurrencesOf("A"); // Returns 2
 * }</pre>
 *
 * @param <T> the type of elements in the bag
 */
public interface UnsortedBag<T> extends Bag<T>
{
    @Override
    UnsortedBag<T> tap(Procedure<? super T> procedure);

    @Override
    UnsortedBag<T> selectByOccurrences(IntPredicate predicate);

    /**
     * @since 9.2
     */
    @Override
    default UnsortedBag<T> selectDuplicates()
    {
        return this.selectByOccurrences(occurrences -> occurrences > 1);
    }

    /**
     * @since 9.2
     */
    @Override
    UnsortedSetIterable<T> selectUnique();

    @Override
    UnsortedBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> UnsortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    UnsortedBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> UnsortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> UnsortedBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    PartitionUnsortedBag<T> partition(Predicate<? super T> predicate);

    @Override
    <V> UnsortedBag<V> collect(Function<? super T, ? extends V> function);

    @Override
    BooleanBag collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    ByteBag collectByte(ByteFunction<? super T> byteFunction);

    @Override
    CharBag collectChar(CharFunction<? super T> charFunction);

    @Override
    DoubleBag collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    FloatBag collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    IntBag collectInt(IntFunction<? super T> intFunction);

    @Override
    LongBag collectLong(LongFunction<? super T> longFunction);

    @Override
    ShortBag collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <P, V> UnsortedBag<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> UnsortedBag<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> UnsortedBag<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function);

    @Override
    <V> UnsortedBag<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> UnsortedBag<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> UnsortedBag<Pair<T, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    UnsortedSetIterable<Pair<T, Integer>> zipWithIndex();

    @Override
    <V> UnsortedBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> UnsortedBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);
}
