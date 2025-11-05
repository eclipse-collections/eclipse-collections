/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map.primitive;

import java.util.Collection;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
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
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.map.UnsortedMapIterable;
import org.eclipse.collections.api.multimap.bag.BagMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionBag;
import org.eclipse.collections.api.set.UnsortedSetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * PrimitiveObjectMap is a map with primitive keys and Object values. This interface is the parent
 * of all primitive-to-object map types (IntObjectMap, LongObjectMap, etc.). Using primitive keys
 * instead of boxed objects provides better memory efficiency and performance.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * IntObjectMap<String> map = IntObjectMaps.mutable.empty();
 * map.put(1, "One");
 * map.put(2, "Two");
 * String value = map.get(1); // Returns "One"
 * }</pre>
 *
 * @param <V> the type of values in the map
 * @since 8.0.
 */
public interface PrimitiveObjectMap<V> extends RichIterable<V>
{
    boolean containsValue(Object value);

    void forEachValue(Procedure<? super V> procedure);

    /**
     * Follows the same general contract as {@link Map#equals(Object)}.
     */
    @Override
    boolean equals(Object o);

    /**
     * Follows the same general contract as {@link Map#hashCode()}.
     */
    @Override
    int hashCode();

    /**
     * Returns a string with the keys and values of this map separated by commas with spaces and
     * enclosed in curly braces. Each key and value is separated by an equals sign.
     *
     * <pre>
     * Assert.assertEquals(
     *     "{1=1, 2=2, 3=3}",
     *     IntObjectMaps.mutable.empty().withKeyValue(1, 1).withKeyValue(2, 2).withKeyValue(3, 3).toString());
     * </pre>
     *
     * @return a string representation of this PrimitiveObjectMap
     * @see java.util.AbstractMap#toString()
     */
    @Override
    String toString();

    Collection<V> values();

    @Override
    Bag<V> select(Predicate<? super V> predicate);

    @Override
    <P> Bag<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    Bag<V> reject(Predicate<? super V> predicate);

    @Override
    <P> Bag<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionBag<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionBag<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <S> Bag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <VV> Bag<VV> collect(Function<? super V, ? extends VV> function);

    @Override
    BooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    ByteBag collectByte(ByteFunction<? super V> byteFunction);

    @Override
    CharBag collectChar(CharFunction<? super V> charFunction);

    @Override
    DoubleBag collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    FloatBag collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    IntBag collectInt(IntFunction<? super V> intFunction);

    @Override
    LongBag collectLong(LongFunction<? super V> longFunction);

    @Override
    ShortBag collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <P, VV> Bag<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter);

    @Override
    <VV> Bag<VV> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends VV> function);

    @Override
    <VV> Bag<VV> flatCollect(Function<? super V, ? extends Iterable<VV>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, VV> Bag<VV> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<VV>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    <VV> BagMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function);

    @Override
    <VV> BagMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function);

    @Override
    <VV> UnsortedMapIterable<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> Bag<Pair<V, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    UnsortedSetIterable<Pair<V, Integer>> zipWithIndex();

    /**
     * @since 9.0
     */
    default Stream<V> stream()
    {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * @since 9.0
     */
    default Stream<V> parallelStream()
    {
        return StreamSupport.stream(this.spliterator(), true);
    }

    /**
     * @since 9.0
     */
    @Override
    default Spliterator<V> spliterator()
    {
        return Spliterators.spliterator(this.iterator(), (long) this.size(), 0);
    }
}
