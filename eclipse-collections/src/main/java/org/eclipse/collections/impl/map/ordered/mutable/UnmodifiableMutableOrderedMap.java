/*
 * Copyright (c) 2022 Two Sigma and others.
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
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.MutableBagIterable;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
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
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.ordered.ReversibleIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;
import org.eclipse.collections.impl.tuple.AbstractImmutableEntry;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * An unmodifiable view of a map.
 *
 * @see MutableMap#asUnmodifiable()
 */
public class UnmodifiableMutableOrderedMap<K, V>
        implements MutableOrderedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    private final MutableOrderedMap<K, V> delegate;

    public UnmodifiableMutableOrderedMap(MutableOrderedMap<K, V> delegate)
    {
        this.delegate = delegate;
    }

    public static <K, V> UnmodifiableMutableOrderedMap<K, V> of(MutableOrderedMap<K, V> map)
    {
        if (map == null)
        {
            throw new IllegalArgumentException("cannot create a UnmodifiableMutableOrderedMap for null");
        }
        return new UnmodifiableMutableOrderedMap<>(map);
    }

    @Override
    public V put(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call put() on " + this.getClass().getSimpleName());
    }

    @Override
    public V remove(Object key)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t)
    {
        throw new UnsupportedOperationException("Cannot call putAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    @Override
    public Set<K> keySet()
    {
        return Collections.unmodifiableSet(this.delegate.keySet());
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return Collections.unmodifiableMap(this.delegate).entrySet();
    }

    @Override
    public Collection<V> values()
    {
        return Collections.unmodifiableCollection(this.delegate.values());
    }

    @Override
    public MutableOrderedMap<K, V> withKeyValue(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call withKeyValue() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableOrderedMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call withMap() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableOrderedMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call withMapIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public void putAllMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call putAllMapIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableOrderedMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        throw new UnsupportedOperationException("Cannot call withAllKeyValues() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableOrderedMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        throw new UnsupportedOperationException("Cannot call withAllKeyValueArguments() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableOrderedMap<K, V> withoutKey(K key)
    {
        throw new UnsupportedOperationException("Cannot call withoutKey() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableOrderedMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        throw new UnsupportedOperationException("Cannot call withoutAllKeys() on " + this.getClass().getSimpleName());
    }

    @Override
    public V removeKey(K key)
    {
        throw new UnsupportedOperationException("Cannot call removeKey() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean removeAllKeys(Set<? extends K> keys)
    {
        throw new UnsupportedOperationException("Cannot call removeAllKeys() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean removeIf(Predicate2<? super K, ? super V> predicate)
    {
        throw new UnsupportedOperationException("Cannot call removeIf() on " + this.getClass().getSimpleName());
    }

    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        throw new UnsupportedOperationException("Cannot call updateValue() on " + this.getClass().getSimpleName());
    }

    @Override
    public <P> V updateValueWith(
            K key,
            Function0<? extends V> factory,
            Function2<? super V, ? super P, ? extends V> function,
            P parameter)
    {
        throw new UnsupportedOperationException("Cannot call updateValueWith() on " + this.getClass().getSimpleName());
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call merge() on " + this.getClass().getSimpleName());
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call compute() on " + this.getClass().getSimpleName());
    }

    @Override
    public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> mappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call computeIfAbsent() on " + this.getClass().getSimpleName());
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call computeIfPresent() on " + this.getClass().getSimpleName());
    }

    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            throw new UnsupportedOperationException("Cannot mutate " + this.getClass().getSimpleName());
        }
        return result;
    }

    @Override
    public V getIfAbsentPut(K key, V value)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            throw new UnsupportedOperationException("Cannot mutate " + this.getClass().getSimpleName());
        }
        return result;
    }

    @Override
    public V getIfAbsentPutWithKey(K key, Function<? super K, ? extends V> function)
    {
        return this.getIfAbsentPutWith(key, function, key);
    }

    @Override
    public <P> V getIfAbsentPutWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            throw new UnsupportedOperationException("Cannot mutate " + this.getClass().getSimpleName());
        }
        return result;
    }

    private boolean isAbsent(V result, K key)
    {
        return result == null && !this.containsKey(key);
    }

    @Override
    public MutableMapIterable<K, V> asSynchronized()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".asSynchronized() not implemented yet");
    }

    @Override
    public MutableOrderedMap<K, V> asUnmodifiable()
    {
        return this;
    }

    @Override
    public Iterator<V> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.delegate.iterator());
    }

    @Override
    public RichIterable<K> keysView()
    {
        return LazyIterate.adapt(this.keySet());
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return LazyIterate.adapt(this.values());
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return LazyIterate.adapt(this.entrySet()).collect(AbstractImmutableEntry.getPairFunction());
    }

    @Override
    public MutableOrderedMap<K, V> tap(Procedure<? super V> procedure)
    {
        return this.delegate.tap(procedure);
    }

    @Override
    public MutableOrderedMap<V, K> flipUniqueValues()
    {
        return this.delegate.flipUniqueValues();
    }

    @Override
    public MutableListMultimap<V, K> flip()
    {
        return this.delegate.flip();
    }

    @Override
    public void forEach(Consumer<? super V> consumer)
    {
        this.delegate.forEach(consumer);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.delegate.forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        this.delegate.forEachWith(procedure, parameter);
    }

    @Override
    public MutableOrderedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.select(predicate);
    }

    @Override
    public MutableOrderedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.reject(predicate);
    }

    @Override
    public <K2, V2> MutableOrderedMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.delegate.collect(function);
    }

    @Override
    public <R> MutableOrderedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.delegate.collectValues(function);
    }

    @Override
    public <R> MutableOrderedMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.delegate.collectKeysUnique(function);
    }

    @Override
    public MutableOrderedMap<K, V> toReversed()
    {
        return this.delegate.toReversed();
    }

    @Override
    public MutableOrderedMap<K, V> take(int count)
    {
        return this.delegate.take(count);
    }

    @Override
    public MutableOrderedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        return this.delegate.takeWhile(predicate);
    }

    @Override
    public MutableOrderedMap<K, V> drop(int count)
    {
        return this.delegate.drop(count);
    }

    @Override
    public MutableOrderedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        return this.delegate.dropWhile(predicate);
    }

    @Override
    public PartitionMutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        return this.delegate.partitionWhile(predicate);
    }

    @Override
    public MutableList<V> distinct()
    {
        return this.delegate.distinct();
    }

    @Override
    public MutableList<V> select(Predicate<? super V> predicate)
    {
        return this.delegate.select(predicate);
    }

    @Override
    public <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.selectWith(predicate, parameter);
    }

    @Override
    public MutableList<V> reject(Predicate<? super V> predicate)
    {
        return this.delegate.reject(predicate);
    }

    @Override
    public <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableList<V> partition(Predicate<? super V> predicate)
    {
        return this.delegate.partition(predicate);
    }

    @Override
    public <P> PartitionMutableList<V> partitionWith(
            Predicate2<? super V, ? super P> predicate,
            P parameter)
    {
        return this.delegate.partitionWith(predicate, parameter);
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.delegate.collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.delegate.collectByte(byteFunction);
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        return this.delegate.collectChar(charFunction);
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.delegate.collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.delegate.collectFloat(floatFunction);
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        return this.delegate.collectInt(intFunction);
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        return this.delegate.collectLong(longFunction);
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.delegate.collectShort(shortFunction);
    }

    @Override
    public <S> MutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.delegate.zip(that);
    }

    @Override
    public MutableList<Pair<V, Integer>> zipWithIndex()
    {
        return this.delegate.zipWithIndex();
    }

    @Override
    public <VV> MutableList<VV> collect(Function<? super V, ? extends VV> function)
    {
        return this.delegate.collect(function);
    }

    @Override
    public <P, VV> MutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.delegate.collectWith(function, parameter);
    }

    @Override
    public <VV> MutableList<VV> collectIf(
            Predicate<? super V> predicate,
            Function<? super V, ? extends VV> function)
    {
        return this.delegate.collectIf(predicate, function);
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return this.delegate.selectInstancesOf(clazz);
    }

    @Override
    public <VV> MutableList<VV> flatCollect(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.flatCollect(function);
    }

    @Override
    public <P, VV> MutableList<VV> flatCollectWith(
            Function2<? super V, ? super P, ? extends Iterable<VV>> function,
            P parameter)
    {
        return this.delegate.flatCollectWith(function, parameter);
    }

    @Override
    public <VV> MutableListMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.groupBy(function);
    }

    @Override
    public <VV> MutableListMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.groupByEach(function);
    }

    @Override
    public <VV> MutableOrderedMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return this.delegate.groupByUniqueKey(function);
    }

    @Override
    public ImmutableOrderedMap<K, V> toImmutable()
    {
        return this.delegate.toImmutable();
    }

    @Override
    public V get(Object key)
    {
        return this.delegate.get(key);
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.delegate.forEachValue(procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.delegate.forEachKey(procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        this.delegate.forEachKeyValue(procedure);
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        return this.delegate.getIfAbsent(key, function);
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        return this.delegate.getIfAbsentValue(key, value);
    }

    @Override
    public <P> V getIfAbsentWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        return this.delegate.getIfAbsentWith(key, function, parameter);
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        return this.delegate.ifPresentApply(key, function);
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.detect(predicate);
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.detectOptional(predicate);
    }

    @Override
    public boolean equals(Object o)
    {
        return this.delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public String toString()
    {
        return this.delegate.toString();
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean notEmpty()
    {
        return this.delegate.notEmpty();
    }

    @Override
    public V getFirst()
    {
        return this.delegate.getFirst();
    }

    @Override
    public V getLast()
    {
        return this.delegate.getLast();
    }

    @Override
    public V getOnly()
    {
        return this.delegate.getOnly();
    }

    @Override
    public boolean contains(Object object)
    {
        return this.delegate.contains(object);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.delegate.containsAllIterable(source);
    }

    @Override
    public boolean containsAll(Collection<?> source)
    {
        return this.delegate.containsAll(source);
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return this.delegate.containsAllArguments(elements);
    }

    @Override
    public void each(Procedure<? super V> procedure)
    {
        this.delegate.each(procedure);
    }

    @Override
    public <R extends Collection<V>> R select(Predicate<? super V> predicate, R target)
    {
        return this.delegate.select(predicate, target);
    }

    @Override
    public <P, R extends Collection<V>> R selectWith(
            Predicate2<? super V, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        return this.delegate.selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public <R extends Collection<V>> R reject(Predicate<? super V> predicate, R target)
    {
        return this.delegate.reject(predicate, target);
    }

    @Override
    public <P, R extends Collection<V>> R rejectWith(
            Predicate2<? super V, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        return this.delegate.rejectWith(predicate, parameter, targetCollection);
    }

    @Override
    public <VV, R extends Collection<VV>> R collect(Function<? super V, ? extends VV> function, R target)
    {
        return this.delegate.collect(function, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super V> booleanFunction, R target)
    {
        return this.delegate.collectBoolean(booleanFunction, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super V, ? extends BooleanIterable> function, R target)
    {
        return this.delegate.flatCollectBoolean(function, target);
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super V> byteFunction, R target)
    {
        return this.delegate.collectByte(byteFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super V, ? extends ByteIterable> function, R target)
    {
        return this.delegate.flatCollectByte(function, target);
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super V> charFunction, R target)
    {
        return this.delegate.collectChar(charFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super V, ? extends CharIterable> function, R target)
    {
        return this.delegate.flatCollectChar(function, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super V> doubleFunction, R target)
    {
        return this.delegate.collectDouble(doubleFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super V, ? extends DoubleIterable> function, R target)
    {
        return this.delegate.flatCollectDouble(function, target);
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super V> floatFunction, R target)
    {
        return this.delegate.collectFloat(floatFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super V, ? extends FloatIterable> function, R target)
    {
        return this.delegate.flatCollectFloat(function, target);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super V> intFunction, R target)
    {
        return this.delegate.collectInt(intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super V, ? extends IntIterable> function, R target)
    {
        return this.delegate.flatCollectInt(function, target);
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super V> longFunction, R target)
    {
        return this.delegate.collectLong(longFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super V, ? extends LongIterable> function, R target)
    {
        return this.delegate.flatCollectLong(function, target);
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super V> shortFunction, R target)
    {
        return this.delegate.collectShort(shortFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super V, ? extends ShortIterable> function, R target)
    {
        return this.delegate.flatCollectShort(function, target);
    }

    @Override
    public <P, VV, R extends Collection<VV>> R collectWith(
            Function2<? super V, ? super P, ? extends VV> function,
            P parameter, R targetCollection)
    {
        return this.delegate.collectWith(function, parameter, targetCollection);
    }

    @Override
    public <VV, R extends Collection<VV>> R collectIf(
            Predicate<? super V> predicate,
            Function<? super V, ? extends VV> function, R target)
    {
        return this.delegate.collectIf(predicate, function, target);
    }

    @Override
    public <VV, R extends Collection<VV>> R flatCollect(Function<? super V, ? extends Iterable<VV>> function, R target)
    {
        return this.delegate.flatCollect(function, target);
    }

    @Override
    public <P, VV, R extends Collection<VV>> R flatCollectWith(
            Function2<? super V, ? super P, ? extends Iterable<VV>> function,
            P parameter, R target)
    {
        return this.delegate.flatCollectWith(function, parameter, target);
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        return this.delegate.detect(predicate);
    }

    @Override
    public <P> V detectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.detectWith(predicate, parameter);
    }

    @Override
    public Optional<V> detectOptional(Predicate<? super V> predicate)
    {
        return this.delegate.detectOptional(predicate);
    }

    @Override
    public <P> Optional<V> detectWithOptional(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.detectWithOptional(predicate, parameter);
    }

    @Override
    public V detectIfNone(
            Predicate<? super V> predicate,
            Function0<? extends V> function)
    {
        return this.delegate.detectIfNone(predicate, function);
    }

    @Override
    public <P> V detectWithIfNone(
            Predicate2<? super V, ? super P> predicate,
            P parameter,
            Function0<? extends V> function)
    {
        return this.delegate.detectWithIfNone(predicate, parameter, function);
    }

    @Override
    public int count(Predicate<? super V> predicate)
    {
        return this.delegate.count(predicate);
    }

    @Override
    public <P> int countWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.countWith(predicate, parameter);
    }

    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return this.delegate.anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.anySatisfyWith(predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return this.delegate.allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super V> predicate)
    {
        return this.delegate.noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.noneSatisfyWith(predicate, parameter);
    }

    @Override
    public <IV> IV injectInto(
            IV injectedValue,
            Function2<? super IV, ? super V, ? extends IV> function)
    {
        return this.delegate.injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(
            int injectedValue,
            IntObjectToIntFunction<? super V> function)
    {
        return this.delegate.injectIntoInt(injectedValue, function);
    }

    @Override
    public long injectInto(
            long injectedValue,
            LongObjectToLongFunction<? super V> function)
    {
        return this.delegate.injectIntoLong(injectedValue, function);
    }

    @Override
    public float injectInto(
            float injectedValue,
            FloatObjectToFloatFunction<? super V> function)
    {
        return this.delegate.injectIntoFloat(injectedValue, function);
    }

    @Override
    public double injectInto(
            double injectedValue,
            DoubleObjectToDoubleFunction<? super V> function)
    {
        return this.delegate.injectIntoDouble(injectedValue, function);
    }

    @Override
    public <R extends Collection<V>> R into(R target)
    {
        return this.delegate.into(target);
    }

    @Override
    public MutableList<V> toList()
    {
        return this.delegate.toList();
    }

    @Override
    public MutableList<V> toSortedList()
    {
        return this.delegate.toSortedList();
    }

    @Override
    public MutableList<V> toSortedList(Comparator<? super V> comparator)
    {
        return this.delegate.toSortedList(comparator);
    }

    @Override
    public MutableSet<V> toSet()
    {
        return this.delegate.toSet();
    }

    @Override
    public MutableSortedSet<V> toSortedSet()
    {
        return this.delegate.toSortedSet();
    }

    @Override
    public MutableSortedSet<V> toSortedSet(Comparator<? super V> comparator)
    {
        return this.delegate.toSortedSet(comparator);
    }

    @Override
    public MutableBag<V> toBag()
    {
        return this.delegate.toBag();
    }

    @Override
    public MutableSortedBag<V> toSortedBag()
    {
        return this.delegate.toSortedBag();
    }

    @Override
    public MutableSortedBag<V> toSortedBag(Comparator<? super V> comparator)
    {
        return this.delegate.toSortedBag(comparator);
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.delegate.toMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction,
            R target)
    {
        return this.delegate.toMap(keyFunction, valueFunction, target);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.delegate.toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Comparator<? super NK> comparator,
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.delegate.toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.delegate.toBiMap(keyFunction, valueFunction);
    }

    @Override
    public LazyIterable<V> asLazy()
    {
        return this.delegate.asLazy();
    }

    @Override
    public Object[] toArray()
    {
        return this.delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] target)
    {
        return this.delegate.toArray(target);
    }

    @Override
    public V min(Comparator<? super V> comparator)
    {
        return this.delegate.min(comparator);
    }

    @Override
    public V max(Comparator<? super V> comparator)
    {
        return this.delegate.max(comparator);
    }

    @Override
    public Optional<V> minOptional(Comparator<? super V> comparator)
    {
        return this.delegate.minOptional(comparator);
    }

    @Override
    public Optional<V> maxOptional(Comparator<? super V> comparator)
    {
        return this.delegate.maxOptional(comparator);
    }

    @Override
    public V min()
    {
        return this.delegate.min();
    }

    @Override
    public V max()
    {
        return this.delegate.max();
    }

    @Override
    public Optional<V> minOptional()
    {
        return this.delegate.minOptional();
    }

    @Override
    public Optional<V> maxOptional()
    {
        return this.delegate.maxOptional();
    }

    @Override
    public <VV extends Comparable<? super VV>> V minBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.minBy(function);
    }

    @Override
    public <VV extends Comparable<? super VV>> V maxBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.maxBy(function);
    }

    @Override
    public <VV extends Comparable<? super VV>> Optional<V> minByOptional(Function<? super V, ? extends VV> function)
    {
        return this.delegate.minByOptional(function);
    }

    @Override
    public <VV extends Comparable<? super VV>> Optional<V> maxByOptional(Function<? super V, ? extends VV> function)
    {
        return this.delegate.maxByOptional(function);
    }

    @Override
    public long sumOfInt(IntFunction<? super V> function)
    {
        return this.delegate.sumOfInt(function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super V> function)
    {
        return this.delegate.sumOfFloat(function);
    }

    @Override
    public long sumOfLong(LongFunction<? super V> function)
    {
        return this.delegate.sumOfLong(function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super V> function)
    {
        return this.delegate.sumOfDouble(function);
    }

    @Override
    public IntSummaryStatistics summarizeInt(IntFunction<? super V> function)
    {
        return this.delegate.summarizeInt(function);
    }

    @Override
    public DoubleSummaryStatistics summarizeFloat(FloatFunction<? super V> function)
    {
        return this.delegate.summarizeFloat(function);
    }

    @Override
    public LongSummaryStatistics summarizeLong(LongFunction<? super V> function)
    {
        return this.delegate.summarizeLong(function);
    }

    @Override
    public DoubleSummaryStatistics summarizeDouble(DoubleFunction<? super V> function)
    {
        return this.delegate.summarizeDouble(function);
    }

    @Override
    public <R, A> R reduceInPlace(Collector<? super V, A, R> collector)
    {
        return this.delegate.reduceInPlace(collector);
    }

    @Override
    public <R> R reduceInPlace(
            Supplier<R> supplier,
            BiConsumer<R, ? super V> accumulator)
    {
        return this.delegate.reduceInPlace(supplier, accumulator);
    }

    @Override
    public Optional<V> reduce(BinaryOperator<V> accumulator)
    {
        return this.delegate.reduce(accumulator);
    }

    @Override
    public <VV> MutableObjectLongMap<VV> sumByInt(
            Function<? super V, ? extends VV> groupBy,
            IntFunction<? super V> function)
    {
        return this.delegate.sumByInt(groupBy, function);
    }

    @Override
    public <VV> MutableObjectDoubleMap<VV> sumByFloat(
            Function<? super V, ? extends VV> groupBy,
            FloatFunction<? super V> function)
    {
        return this.delegate.sumByFloat(groupBy, function);
    }

    @Override
    public <VV> MutableObjectLongMap<VV> sumByLong(
            Function<? super V, ? extends VV> groupBy,
            LongFunction<? super V> function)
    {
        return this.delegate.sumByLong(groupBy, function);
    }

    @Override
    public <VV> MutableObjectDoubleMap<VV> sumByDouble(
            Function<? super V, ? extends VV> groupBy,
            DoubleFunction<? super V> function)
    {
        return this.delegate.sumByDouble(groupBy, function);
    }

    @Override
    public String makeString()
    {
        return this.delegate.makeString();
    }

    @Override
    public String makeString(String separator)
    {
        return this.delegate.makeString(separator);
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        return this.delegate.makeString(start, separator, end);
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.delegate.appendString(appendable);
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.delegate.appendString(appendable, separator);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.delegate.appendString(appendable, start, separator, end);
    }

    @Override
    public <VV> MutableBag<VV> countBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.countBy(function);
    }

    @Override
    public <VV, R extends MutableBagIterable<VV>> R countBy(
            Function<? super V, ? extends VV> function,
            R target)
    {
        return this.delegate.countBy(function, target);
    }

    @Override
    public <VV, P> MutableBag<VV> countByWith(
            Function2<? super V, ? super P, ? extends VV> function,
            P parameter)
    {
        return this.delegate.countByWith(function, parameter);
    }

    @Override
    public <VV, P, R extends MutableBagIterable<VV>> R countByWith(
            Function2<? super V, ? super P, ? extends VV> function,
            P parameter, R target)
    {
        return this.delegate.countByWith(function, parameter, target);
    }

    @Override
    public <VV, R extends MutableMultimap<VV, V>> R groupBy(Function<? super V, ? extends VV> function, R target)
    {
        return this.delegate.groupBy(function, target);
    }

    @Override
    public <VV, R extends MutableMultimap<VV, V>> R groupByEach(
            Function<? super V, ? extends Iterable<VV>> function,
            R target)
    {
        return this.delegate.groupByEach(function, target);
    }

    @Override
    public <VV, R extends MutableMapIterable<VV, V>> R groupByUniqueKey(
            Function<? super V, ? extends VV> function,
            R target)
    {
        return this.delegate.groupByUniqueKey(function, target);
    }

    @Override
    public <S, R extends Collection<Pair<V, S>>> R zip(Iterable<S> that, R target)
    {
        return this.delegate.zip(that, target);
    }

    @Override
    public <R extends Collection<Pair<V, Integer>>> R zipWithIndex(R target)
    {
        return this.delegate.zipWithIndex(target);
    }

    @Override
    public RichIterable<RichIterable<V>> chunk(int size)
    {
        return this.delegate.chunk(size);
    }

    @Override
    public <KK, VV> MutableOrderedMap<KK, VV> aggregateInPlaceBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        return this.delegate.aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
    }

    @Override
    public <KK, VV> MutableOrderedMap<KK, VV> aggregateBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        return this.delegate.aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
    }

    @Override
    public <K1, V1, V2> MutableOrderedMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        return this.delegate.aggregateBy(keyFunction, valueFunction, zeroValueFactory, nonMutatingAggregator);
    }

    @Override
    public <KK> MutableOrderedMap<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        return this.delegate.reduceBy(groupBy, reduceFunction);
    }

    @Override
    public MutableOrderedMap<K, V> newEmpty()
    {
        return this.delegate.newEmpty();
    }

    @Override
    public void reverseForEach(Procedure<? super V> procedure)
    {
        this.delegate.reverseForEach(procedure);
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super V> procedure)
    {
        this.delegate.reverseForEachWithIndex(procedure);
    }

    @Override
    public int detectLastIndex(Predicate<? super V> predicate)
    {
        return this.delegate.detectLastIndex(predicate);
    }

    @Override
    public <V1> ReversibleIterable<V1> collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends V1> function)
    {
        return this.delegate.collectWithIndex(function);
    }

    @Override
    public int indexOf(Object object)
    {
        return this.delegate.indexOf(object);
    }

    @Override
    public Optional<V> getFirstOptional()
    {
        return this.delegate.getFirstOptional();
    }

    @Override
    public Optional<V> getLastOptional()
    {
        return this.delegate.getLastOptional();
    }

    @Override
    public <S> boolean corresponds(
            OrderedIterable<S> other,
            Predicate2<? super V, ? super S> predicate)
    {
        return this.delegate.corresponds(other, predicate);
    }

    @Override
    public void forEach(
            int startIndex,
            int endIndex,
            Procedure<? super V> procedure)
    {
        this.delegate.forEach(startIndex, endIndex, procedure);
    }

    @Override
    public void forEachWithIndex(
            int fromIndex,
            int toIndex,
            ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.delegate.forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public <VV, R extends Collection<VV>> R collectWithIndex(
            ObjectIntToObjectFunction<? super V, ? extends VV> function,
            R target)
    {
        return this.delegate.collectWithIndex(function, target);
    }

    @Override
    public int detectIndex(Predicate<? super V> predicate)
    {
        return this.delegate.detectIndex(predicate);
    }
}
