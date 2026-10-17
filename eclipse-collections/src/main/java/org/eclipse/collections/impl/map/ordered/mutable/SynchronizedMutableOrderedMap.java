/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.ordered.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

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
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.map.ImmutableOrderedMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.SynchronizedMutableCollection;
import org.eclipse.collections.impl.map.AbstractSynchronizedMapIterable;
import org.eclipse.collections.impl.map.mutable.SynchronizedMapSerializationProxy;
import org.eclipse.collections.impl.set.mutable.SynchronizedMutableSet;

/**
 * A synchronized view of a MutableOrderedMap.
 *
 * @see #SynchronizedMutableOrderedMap(MutableOrderedMap)
 */
public class SynchronizedMutableOrderedMap<K, V>
        extends AbstractSynchronizedMapIterable<K, V> implements MutableOrderedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    public SynchronizedMutableOrderedMap(MutableOrderedMap<K, V> newMap)
    {
        super(newMap);
    }

    public SynchronizedMutableOrderedMap(MutableOrderedMap<K, V> newMap, Object lock)
    {
        super(newMap, lock);
    }

    public static <K, V> SynchronizedMutableOrderedMap<K, V> of(MutableOrderedMap<K, V> map)
    {
        return new SynchronizedMutableOrderedMap<>(map);
    }

    public static <K, V> SynchronizedMutableOrderedMap<K, V> of(MutableOrderedMap<K, V> map, Object lock)
    {
        return new SynchronizedMutableOrderedMap<>(map, lock);
    }

    @Override
    public MutableOrderedMap<K, V> newEmpty()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().newEmpty();
        }
    }

    @Override
    public MutableOrderedMap<K, V> tap(Procedure<? super V> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().tap(procedure);
            return this;
        }
    }

    @Override
    public MutableOrderedMap<V, K> flipUniqueValues()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flipUniqueValues();
        }
    }

    @Override
    public MutableListMultimap<V, K> flip()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flip();
        }
    }

    @Override
    public MutableOrderedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().select(predicate);
        }
    }

    @Override
    public MutableOrderedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reject(predicate);
        }
    }

    @Override
    public <K2, V2> MutableOrderedMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collect(function);
        }
    }

    @Override
    public <R> MutableOrderedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectValues(function);
        }
    }

    @Override
    public <R> MutableOrderedMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectKeysUnique(function);
        }
    }

    @Override
    public MutableOrderedMap<K, V> toReversed()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().toReversed();
        }
    }

    @Override
    public MutableOrderedMap<K, V> take(int count)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().take(count);
        }
    }

    @Override
    public MutableOrderedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().takeWhile(predicate);
        }
    }

    @Override
    public MutableOrderedMap<K, V> drop(int count)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().drop(count);
        }
    }

    @Override
    public MutableOrderedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().dropWhile(predicate);
        }
    }

    @Override
    public PartitionMutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().partitionWhile(predicate);
        }
    }

    @Override
    public MutableList<V> distinct()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().distinct();
        }
    }

    @Override
    public int detectIndex(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().detectIndex(predicate);
        }
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super V, ? super S> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().corresponds(other, predicate);
        }
    }

    @Override
    public MutableList<V> select(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().select(predicate);
        }
    }

    @Override
    public <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().selectWith(predicate, parameter);
        }
    }

    @Override
    public MutableList<V> reject(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reject(predicate);
        }
    }

    @Override
    public <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().rejectWith(predicate, parameter);
        }
    }

    @Override
    public PartitionMutableList<V> partition(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().partition(predicate);
        }
    }

    @Override
    public <P> PartitionMutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().partitionWith(predicate, parameter);
        }
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectBoolean(booleanFunction);
        }
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectByte(byteFunction);
        }
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectChar(charFunction);
        }
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectDouble(doubleFunction);
        }
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectFloat(floatFunction);
        }
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectInt(intFunction);
        }
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectLong(longFunction);
        }
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectShort(shortFunction);
        }
    }

    @Override
    public <S> MutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().zip(that);
        }
    }

    @Override
    public MutableList<Pair<V, Integer>> zipWithIndex()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().zipWithIndex();
        }
    }

    @Override
    public <R> MutableList<R> collect(Function<? super V, ? extends R> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collect(function);
        }
    }

    @Override
    public <P, VV> MutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().<P, VV>collectWith(function, parameter);
        }
    }

    @Override
    public <R> MutableList<R> collectIf(
            Predicate<? super V> predicate,
            Function<? super V, ? extends R> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectIf(predicate, function);
        }
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().selectInstancesOf(clazz);
        }
    }

    @Override
    public <R> MutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flatCollect(function);
        }
    }

    @Override
    public <P, R> MutableList<R> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<R>> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flatCollectWith(function, parameter);
        }
    }

    @Override
    public <KK> MutableListMultimap<KK, V> groupBy(Function<? super V, ? extends KK> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().<KK>groupBy(function);
        }
    }

    @Override
    public <KK> MutableListMultimap<KK, V> groupByEach(Function<? super V, ? extends Iterable<KK>> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().groupByEach(function);
        }
    }

    @Override
    public <VV> MutableOrderedMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().groupByUniqueKey(function);
        }
    }

    @Override
    public <KK, VV> MutableOrderedMap<KK, VV> aggregateInPlaceBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
        }
    }

    @Override
    public <KK, VV> MutableOrderedMap<KK, VV> aggregateBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
        }
    }

    @Override
    public <K1, V1, V2> MutableOrderedMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().aggregateBy(keyFunction, valueFunction, zeroValueFactory, nonMutatingAggregator);
        }
    }

    @Override
    public <KK> MutableOrderedMap<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reduceBy(groupBy, reduceFunction);
        }
    }

    @Override
    public MutableOrderedMap<K, V> withKeyValue(K key, V value)
    {
        synchronized (this.lock)
        {
            this.getDelegate().withKeyValue(key, value);
            return this;
        }
    }

    @Override
    public MutableOrderedMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        synchronized (this.lock)
        {
            this.getDelegate().withMap(map);
            return this;
        }
    }

    @Override
    public MutableOrderedMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        synchronized (this.lock)
        {
            this.getDelegate().withMapIterable(mapIterable);
            return this;
        }
    }

    @Override
    public MutableOrderedMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        synchronized (this.lock)
        {
            this.getDelegate().withAllKeyValues(keyValues);
            return this;
        }
    }

    @Override
    public MutableOrderedMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        synchronized (this.lock)
        {
            this.getDelegate().withAllKeyValueArguments(keyValuePairs);
            return this;
        }
    }

    @Override
    public MutableOrderedMap<K, V> withoutKey(K key)
    {
        synchronized (this.lock)
        {
            this.getDelegate().withoutKey(key);
            return this;
        }
    }

    @Override
    public MutableOrderedMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        synchronized (this.lock)
        {
            this.getDelegate().withoutAllKeys(keys);
            return this;
        }
    }

    @Override
    public MutableOrderedMap<K, V> asUnmodifiable()
    {
        synchronized (this.lock)
        {
            return UnmodifiableMutableOrderedMap.of(this);
        }
    }

    @Override
    public MutableOrderedMap<K, V> asSynchronized()
    {
        return this;
    }

    @Override
    public ImmutableOrderedMap<K, V> toImmutable()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().toImmutable();
        }
    }

    @Override
    public void putAllMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        synchronized (this.lock)
        {
            this.getDelegate().putAllMapIterable(mapIterable);
        }
    }

    @Override
    public void reverseForEach(Procedure<? super V> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().reverseForEach(procedure);
        }
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super V> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().reverseForEachWithIndex(procedure);
        }
    }

    @Override
    public int detectLastIndex(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().detectLastIndex(predicate);
        }
    }

    @Override
    public <R> MutableList<R> collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends R> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectWithIndex(function);
        }
    }

    @Override
    public MutableSet<K> keySet()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableSet.of(this.getDelegate().keySet(), this.lock);
        }
    }

    @Override
    public MutableCollection<V> values()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableCollection.of(this.getDelegate().values(), this.lock);
        }
    }

    @Override
    public MutableSet<Entry<K, V>> entrySet()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableSet.of(this.getDelegate().entrySet(), this.lock);
        }
    }

    @Override
    public int indexOf(Object object)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().indexOf(object);
        }
    }

    @Override
    public Optional<V> getFirstOptional()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getFirstOptional();
        }
    }

    @Override
    public Optional<V> getLastOptional()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().getLastOptional();
        }
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super V> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().forEach(startIndex, endIndex, procedure);
        }
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super V> objectIntProcedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
        }
    }

    @Override
    public MutableStack<V> toStack()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().toStack();
        }
    }

    @Override
    public <V1, R extends Collection<V1>> R collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends V1> function, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectWithIndex(function, target);
        }
    }

    @Override
    public <R extends Collection<V>> R selectWithIndex(ObjectIntPredicate<? super V> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().selectWithIndex(predicate, target);
        }
    }

    @Override
    public <R extends Collection<V>> R rejectWithIndex(ObjectIntPredicate<? super V> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().rejectWithIndex(predicate, target);
        }
    }

    @Override
    protected MutableOrderedMap<K, V> getDelegate()
    {
        return (MutableOrderedMap<K, V>) super.getDelegate();
    }

    protected Object writeReplace()
    {
        return new SynchronizedMapSerializationProxy<>(this.getDelegate());
    }
}
