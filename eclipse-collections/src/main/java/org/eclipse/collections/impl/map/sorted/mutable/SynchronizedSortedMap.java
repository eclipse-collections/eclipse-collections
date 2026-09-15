/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.sorted.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

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
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.SynchronizedMutableCollection;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.map.AbstractSynchronizedMapIterable;
import org.eclipse.collections.impl.map.mutable.SynchronizedMapSerializationProxy;
import org.eclipse.collections.impl.set.mutable.SynchronizedMutableSet;

/**
 * A synchronized view of a SortedMap.
 *
 * @see #SynchronizedSortedMap(MutableSortedMap)
 */
public class SynchronizedSortedMap<K, V>
        extends AbstractSynchronizedMapIterable<K, V> implements MutableSortedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 2L;

    public SynchronizedSortedMap(MutableSortedMap<K, V> newMap)
    {
        super(newMap);
    }

    public SynchronizedSortedMap(MutableSortedMap<K, V> newMap, Object lock)
    {
        super(newMap, lock);
    }

    /**
     * This method will take a MutableSortedMap and wrap it directly in a SynchronizedSortedMap. It will
     * take any other non-Eclipse-Collections SortedMap and first adapt it will a SortedMapAdapter, and then return a
     * SynchronizedSortedMap that wraps the adapter.
     */
    public static <K, V, M extends SortedMap<K, V>> SynchronizedSortedMap<K, V> of(M map)
    {
        return new SynchronizedSortedMap<>(SortedMapAdapter.adapt(map));
    }

    public static <K, V, M extends SortedMap<K, V>> SynchronizedSortedMap<K, V> of(M map, Object lock)
    {
        return new SynchronizedSortedMap<>(SortedMapAdapter.adapt(map), lock);
    }

    @Override
    protected MutableSortedMap<K, V> getDelegate()
    {
        return (MutableSortedMap<K, V>) super.getDelegate();
    }

    @Override
    public Comparator<? super K> comparator()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().comparator();
        }
    }

    @Override
    public MutableSortedMap<K, V> withKeyValue(K key, V value)
    {
        synchronized (this.lock)
        {
            this.put(key, value);
            return this;
        }
    }

    @Override
    public MutableSortedMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        synchronized (this.lock)
        {
            this.putAll(map);
            return this;
        }
    }

    @Override
    public MutableSortedMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        this.putAllMapIterable(mapIterable);
        return this;
    }

    @Override
    public void putAllMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        synchronized (this.lock)
        {
            mapIterable.forEachKeyValue(this.getDelegate()::put);
        }
    }

    /**
     * @deprecated in 6.0 Use {@link #withAllKeyValueArguments(Pair[])} instead. Inlineable.
     */
    @Override
    @Deprecated
    public MutableSortedMap<K, V> with(Pair<K, V>... pairs)
    {
        return this.withAllKeyValueArguments(pairs);
    }

    @Override
    public MutableSortedMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        return this.withAllKeyValues(ArrayAdapter.adapt(keyValuePairs));
    }

    @Override
    public MutableSortedMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        synchronized (this.lock)
        {
            for (Pair<? extends K, ? extends V> keyValue : keyValues)
            {
                this.getDelegate().put(keyValue.getOne(), keyValue.getTwo());
            }
            return this;
        }
    }

    @Override
    public MutableSortedMap<K, V> withoutKey(K key)
    {
        this.remove(key);
        return this;
    }

    @Override
    public MutableSortedMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        synchronized (this.lock)
        {
            for (K key : keys)
            {
                this.getDelegate().removeKey(key);
            }
            return this;
        }
    }

    @Override
    public MutableSortedMap<K, V> newEmpty()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().newEmpty();
        }
    }

    @Override
    public MutableSortedMap<K, V> clone()
    {
        synchronized (this.lock)
        {
            return SynchronizedSortedMap.of(this.getDelegate().clone());
        }
    }

    protected Object writeReplace()
    {
        return new SynchronizedMapSerializationProxy<>(this.getDelegate());
    }

    @Override
    public <E> MutableSortedMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectKeysAndValues(iterable, keyFunction, function);
        }
    }

    @Override
    public K firstKey()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().firstKey();
        }
    }

    @Override
    public K lastKey()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().lastKey();
        }
    }

    @Override
    public MutableSortedMap<K, V> tap(Procedure<? super V> procedure)
    {
        return (MutableSortedMap<K, V>) super.tap(procedure);
    }

    @Override
    public MutableList<V> select(Predicate<? super V> predicate)
    {
        return (MutableList<V>) super.select(predicate);
    }

    @Override
    public <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (MutableList<V>) super.selectWith(predicate, parameter);
    }

    @Override
    public MutableList<V> reject(Predicate<? super V> predicate)
    {
        return (MutableList<V>) super.reject(predicate);
    }

    @Override
    public <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (MutableList<V>) super.rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableList<V> partition(Predicate<? super V> predicate)
    {
        return (PartitionMutableList<V>) super.partition(predicate);
    }

    @Override
    public MutableList<Pair<V, Integer>> zipWithIndex()
    {
        return (MutableList<Pair<V, Integer>>) super.zipWithIndex();
    }

    @Override
    public <P> PartitionMutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (PartitionMutableList<V>) super.partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return (MutableList<S>) super.selectInstancesOf(clazz);
    }

    @Override
    public <R> MutableList<R> collect(Function<? super V, ? extends R> function)
    {
        return (MutableList<R>) super.collect(function);
    }

    /**
     * @since 9.1
     */
    @Override
    public <R> MutableList<R> collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends R> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWithIndex(function);
        }
    }

    /**
     * @since 9.1
     */
    @Override
    public <V1, R extends Collection<V1>> R collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends V1> function, R target)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWithIndex(function, target);
        }
    }

    /**
     * @since 11.0
     */
    @Override
    public <R extends Collection<V>> R selectWithIndex(ObjectIntPredicate<? super V> predicate, R target)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectWithIndex(predicate, target);
        }
    }

    /**
     * @since 11.0
     */
    @Override
    public <R extends Collection<V>> R rejectWithIndex(ObjectIntPredicate<? super V> predicate, R target)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().rejectWithIndex(predicate, target);
        }
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return (MutableBooleanList) super.collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        return (MutableByteList) super.collectByte(byteFunction);
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        return (MutableCharList) super.collectChar(charFunction);
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return (MutableDoubleList) super.collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        return (MutableFloatList) super.collectFloat(floatFunction);
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        return (MutableIntList) super.collectInt(intFunction);
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        return (MutableLongList) super.collectLong(longFunction);
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        return (MutableShortList) super.collectShort(shortFunction);
    }

    @Override
    public <P, VV> MutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return (MutableList<VV>) super.<P, VV>collectWith(function, parameter);
    }

    @Override
    public <R> MutableList<R> collectIf(
            Predicate<? super V> predicate,
            Function<? super V, ? extends R> function)
    {
        return (MutableList<R>) super.collectIf(predicate, function);
    }

    @Override
    public <R> MutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        return (MutableList<R>) super.flatCollect(function);
    }

    @Override
    public <P, R> MutableList<R> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<R>> function, P parameter)
    {
        return (MutableList<R>) super.flatCollectWith(function, parameter);
    }

    @Override
    public <KK> MutableListMultimap<KK, V> groupBy(Function<? super V, ? extends KK> function)
    {
        return (MutableListMultimap<KK, V>) super.<KK>groupBy(function);
    }

    @Override
    public <KK> MutableListMultimap<KK, V> groupByEach(Function<? super V, ? extends Iterable<KK>> function)
    {
        return (MutableListMultimap<KK, V>) super.groupByEach(function);
    }

    @Override
    public <S> MutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        return (MutableList<Pair<V, S>>) super.zip(that);
    }

    @Override
    public <VV> MutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return (MutableMap<VV, V>) super.groupByUniqueKey(function);
    }

    @Override
    public MutableMapIterable<V, K> flipUniqueValues()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flipUniqueValues();
        }
    }

    @Override
    public MutableSortedSetMultimap<V, K> flip()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flip();
        }
    }

    @Override
    public MutableSortedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().select(predicate);
        }
    }

    @Override
    public MutableSortedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reject(predicate);
        }
    }

    @Override
    public <K2, V2> MutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collect(function);
        }
    }

    @Override
    public <R> MutableSortedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
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
    public MutableSortedMap<K, V> asUnmodifiable()
    {
        synchronized (this.lock)
        {
            return UnmodifiableTreeMap.of(this);
        }
    }

    @Override
    public MutableSortedMap<K, V> asSynchronized()
    {
        return this;
    }

    @Override
    public ImmutableSortedMap<K, V> toImmutable()
    {
        synchronized (this.lock)
        {
            return SortedMaps.immutable.withSortedMap(this);
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
    public MutableSortedMap<K, V> headMap(K toKey)
    {
        synchronized (this.lock)
        {
            return SynchronizedSortedMap.of(this.getDelegate().headMap(toKey), this.lock);
        }
    }

    @Override
    public MutableSortedMap<K, V> tailMap(K fromKey)
    {
        synchronized (this.lock)
        {
            return SynchronizedSortedMap.of(this.getDelegate().tailMap(fromKey), this.lock);
        }
    }

    @Override
    public MutableSortedMap<K, V> subMap(K fromKey, K toKey)
    {
        synchronized (this.lock)
        {
            return SynchronizedSortedMap.of(this.getDelegate().subMap(fromKey, toKey), this.lock);
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
    public int indexOf(Object object)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().indexOf(object);
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
    public int detectIndex(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().detectIndex(predicate);
        }
    }

    @Override
    public MutableSortedMap<K, V> toReversed()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().toReversed();
        }
    }

    @Override
    public MutableSortedMap<K, V> take(int count)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().take(count);
        }
    }

    @Override
    public MutableSortedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().takeWhile(predicate);
        }
    }

    @Override
    public MutableSortedMap<K, V> drop(int count)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().drop(count);
        }
    }

    @Override
    public MutableSortedMap<K, V> dropWhile(Predicate<? super V> predicate)
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
}
