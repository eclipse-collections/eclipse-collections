/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map;

import java.util.Iterator;
import java.util.Map;

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
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.OrderedMaps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;

public interface MutableOrderedMap<K, V> extends OrderedMap<K, V>, MutableMapIterable<K, V>
{
    /**
     * @since 12.0
     */
    @Override
    MutableOrderedMap<K, V> newEmpty();

    @Override
    default MutableOrderedMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    default MutableOrderedMap<V, K> flipUniqueValues()
    {
        MutableOrderedMap<V, K> result = OrderedMaps.mutable.empty();
        this.forEachKeyValue((key, value) ->
        {
            K oldKey = result.put(value, key);
            if (oldKey != null)
            {
                throw new IllegalStateException(String.format(
                        "Duplicate value: %s found at key: %s and key: %s",
                        value,
                        oldKey,
                        key));
            }
        });
        return result;
    }

    @Override
    MutableListMultimap<V, K> flip();

    @Override
    default MutableOrderedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        MutableOrderedMap<K, V> result = this.newEmpty();
        this.forEachKeyValue((key, value) ->
        {
            if (predicate.accept(key, value))
            {
                result.put(key, value);
            }
        });
        return result;
    }

    @Override
    default MutableOrderedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        MutableOrderedMap<K, V> result = this.newEmpty();
        this.forEachKeyValue((key, value) ->
        {
            if (!predicate.accept(key, value))
            {
                result.put(key, value);
            }
        });
        return result;
    }

    @Override
    default <K2, V2> MutableOrderedMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        MutableOrderedMap<K2, V2> result = OrderedMaps.mutable.empty();
        this.forEachKeyValue((key, value) ->
        {
            Pair<K2, V2> pair = function.value(key, value);
            result.put(pair.getOne(), pair.getTwo());
        });
        return result;
    }

    @Override
    default <R> MutableOrderedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        MutableOrderedMap<K, R> result = OrderedMaps.mutable.empty();
        this.forEachKeyValue((key, value) -> result.put(key, function.value(key, value)));
        return result;
    }

    @Override
    default <R> MutableOrderedMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        MutableOrderedMap<R, V> result = OrderedMaps.mutable.empty();
        this.forEachKeyValue((key, value) ->
        {
            R newKey = function.value(key, value);
            if (result.put(newKey, value) != null)
            {
                throw new IllegalStateException("Key " + newKey + " already exists in map!");
            }
        });
        return result;
    }

    @Override
    MutableOrderedMap<K, V> toReversed();

    @Override
    MutableOrderedMap<K, V> take(int count);

    @Override
    MutableOrderedMap<K, V> takeWhile(Predicate<? super V> predicate);

    @Override
    MutableOrderedMap<K, V> drop(int count);

    @Override
    MutableOrderedMap<K, V> dropWhile(Predicate<? super V> predicate);

    @Override
    PartitionMutableList<V> partitionWhile(Predicate<? super V> predicate);

    @Override
    default MutableList<V> distinct()
    {
        MutableList<V> result = Lists.mutable.withInitialCapacity(this.size());
        MutableSet<V> seen = Sets.mutable.empty();
        this.forEachValue(value ->
        {
            if (seen.add(value))
            {
                result.add(value);
            }
        });
        return result;
    }

    @Override
    default int detectIndex(Predicate<? super V> predicate)
    {
        int index = 0;
        for (V value : this)
        {
            if (predicate.accept(value))
            {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    default <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super V, ? super S> predicate)
    {
        if (this.size() != other.size())
        {
            return false;
        }
        Iterator<S> otherIterator = other.iterator();
        for (V value : this)
        {
            if (!predicate.accept(value, otherIterator.next()))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    MutableList<V> select(Predicate<? super V> predicate);

    @Override
    <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    MutableList<V> reject(Predicate<? super V> predicate);

    @Override
    <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionMutableList<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionMutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    MutableByteList collectByte(ByteFunction<? super V> byteFunction);

    @Override
    MutableCharList collectChar(CharFunction<? super V> charFunction);

    @Override
    MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    MutableFloatList collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    MutableIntList collectInt(IntFunction<? super V> intFunction);

    @Override
    MutableLongList collectLong(LongFunction<? super V> longFunction);

    @Override
    MutableShortList collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <S> MutableList<Pair<V, S>> zip(Iterable<S> that);

    @Override
    MutableList<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> MutableList<VV> collect(Function<? super V, ? extends VV> function);

    @Override
    <P, V1> MutableList<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter);

    @Override
    <V1> MutableList<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function);

    @Override
    <S> MutableList<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V1> MutableList<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V1> MutableList<V1> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<V1>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    <V1> MutableListMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    @Override
    <V1> MutableListMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    @Override
    default <V1> MutableOrderedMap<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function)
    {
        return this.groupByUniqueKey(function, OrderedMaps.mutable.empty());
    }

    /**
     * @since 12.0
     */
    @Override
    default <KK, VV> MutableOrderedMap<KK, VV> aggregateInPlaceBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        MutableOrderedMap<KK, VV> result = OrderedMaps.mutable.empty();
        this.forEach(each ->
        {
            KK key = groupBy.valueOf(each);
            VV value = result.getIfAbsentPut(key, zeroValueFactory);
            mutatingAggregator.value(value, each);
        });
        return result;
    }

    /**
     * @since 12.0
     */
    @Override
    default <KK, VV> MutableOrderedMap<KK, VV> aggregateBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        return this.aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator, OrderedMaps.mutable.empty());
    }

    /**
     * @since 12.0
     */
    @Override
    default <K1, V1, V2> MutableOrderedMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        MutableOrderedMap<K1, V2> result = OrderedMaps.mutable.empty();
        this.forEachKeyValue((key, value) -> result.updateValueWith(
                keyFunction.valueOf(key),
                zeroValueFactory,
                nonMutatingAggregator,
                valueFunction.valueOf(value)));
        return result;
    }

    /**
     * @since 12.0
     */
    @Override
    default <KK> MutableOrderedMap<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        return this.reduceBy(groupBy, reduceFunction, OrderedMaps.mutable.empty());
    }

    @Override
    default MutableOrderedMap<K, V> withKeyValue(K key, V value)
    {
        this.put(key, value);
        return this;
    }

    @Override
    default MutableOrderedMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        this.putAll(map);
        return this;
    }

    @Override
    default MutableOrderedMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        this.putAllMapIterable(mapIterable);
        return this;
    }

    @Override
    default MutableOrderedMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        keyValues.forEach(keyVal -> this.put(keyVal.getOne(), keyVal.getTwo()));
        return this;
    }

    @Override
    default MutableOrderedMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        for (Pair<? extends K, ? extends V> pair : keyValuePairs)
        {
            this.put(pair.getOne(), pair.getTwo());
        }
        return this;
    }

    @Override
    default MutableOrderedMap<K, V> withoutKey(K key)
    {
        this.removeKey(key);
        return this;
    }

    @Override
    default MutableOrderedMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        keys.forEach(this::removeKey);
        return this;
    }

    @Override
    MutableOrderedMap<K, V> asUnmodifiable();
}
