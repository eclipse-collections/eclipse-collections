/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bimap.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

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
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.ImmutableBiMap;
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
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;

public class UnmodifiableBiMap<K, V> implements MutableBiMap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    protected final MutableBiMap<K, V> delegate;

    public UnmodifiableBiMap(MutableBiMap<K, V> delegate)
    {
        if (delegate == null)
        {
            throw new IllegalArgumentException("Cannot create an UnmodifiableBiMap for null");
        }
        this.delegate = delegate;
    }

    public static <K, V> UnmodifiableBiMap<K, V> of(MutableBiMap<K, V> biMap)
    {
        return new UnmodifiableBiMap<>(biMap);
    }

    public static <K, V> UnmodifiableBiMap<K, V> of(Map<K, V> map)
    {
        if (map == null)
        {
            throw new IllegalArgumentException("cannot create a UnmodifiableBiMap for null");
        }
        return new UnmodifiableBiMap<>(new HashBiMap<>(map));
    }

    @Override
    public MutableBiMap<K, V> newEmpty()
    {
        return this.delegate.newEmpty();
    }

    @Override
    public MutableBiMap<V, K> inverse()
    {
        return this.delegate.inverse().asUnmodifiable();
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
    public int size()
    {
        return this.delegate.size();
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
    public Set<Entry<K, V>> entrySet()
    {
        return Collections.unmodifiableMap(this.delegate).entrySet();
    }

    @Override
    public Set<K> keySet()
    {
        return Collections.unmodifiableSet(this.delegate.keySet());
    }

    @Override
    public Collection<V> values()
    {
        return Collections.unmodifiableCollection(this.delegate.values());
    }

    @Override
    public RichIterable<K> keysView()
    {
        return this.delegate.keysView();
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return this.delegate.valuesView();
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return this.delegate.keyValuesView();
    }

    @Override
    public Iterator<V> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.delegate.iterator());
    }

    @Override
    public V get(Object key)
    {
        return this.delegate.get(key);
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
    public MutableSetMultimap<V, K> flip()
    {
        return this.delegate.flip();
    }

    @Override
    public MutableBiMap<V, K> flipUniqueValues()
    {
        return this.delegate.flipUniqueValues();
    }

    @Override
    public MutableBiMap<K, V> clone()
    {
        return this;
    }

    @Override
    public LazyIterable<V> asLazy()
    {
        return this.delegate.asLazy();
    }

    @Override
    public MutableBiMap<K, V> asSynchronized()
    {
        return SynchronizedBiMap.of(this);
    }

    @Override
    public MutableBiMap<K, V> asUnmodifiable()
    {
        return this;
    }

    @Override
    public MutableBiMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public void each(Procedure<? super V> procedure)
    {
        this.delegate.each(procedure);
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
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.delegate.forEachKey(procedure);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.delegate.forEachValue(procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        this.delegate.forEachKeyValue(procedure);
    }

    @Override
    public MutableBiMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.select(predicate);
    }

    @Override
    public MutableSet<V> select(Predicate<? super V> predicate)
    {
        return this.delegate.select(predicate);
    }

    @Override
    public <R extends Collection<V>> R select(Predicate<? super V> predicate, R target)
    {
        return this.delegate.select(predicate, target);
    }

    @Override
    public <P> MutableSet<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.selectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<V>> R selectWith(Predicate2<? super V, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public MutableBiMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.reject(predicate);
    }

    @Override
    public MutableSet<V> reject(Predicate<? super V> predicate)
    {
        return this.delegate.reject(predicate);
    }

    @Override
    public <R extends Collection<V>> R reject(Predicate<? super V> predicate, R target)
    {
        return this.delegate.reject(predicate, target);
    }

    @Override
    public <P> MutableSet<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.rejectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<V>> R rejectWith(Predicate2<? super V, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.rejectWith(predicate, parameter, targetCollection);
    }

    @Override
    public PartitionMutableSet<V> partition(Predicate<? super V> predicate)
    {
        return this.delegate.partition(predicate);
    }

    @Override
    public <P> PartitionMutableSet<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableSet<S> selectInstancesOf(Class<S> clazz)
    {
        return this.delegate.selectInstancesOf(clazz);
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.detect(predicate);
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
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        return this.delegate.detectOptional(predicate);
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
    public V detectIfNone(Predicate<? super V> predicate, Function0<? extends V> function)
    {
        return this.delegate.detectIfNone(predicate, function);
    }

    @Override
    public <P> V detectWithIfNone(Predicate2<? super V, ? super P> predicate, P parameter, Function0<? extends V> function)
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
    public <K2, V2> MutableBiMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.delegate.collect(function);
    }

    @Override
    public <V1> MutableCollection<V1> collect(Function<? super V, ? extends V1> function)
    {
        return this.delegate.collect(function);
    }

    @Override
    public <VV, R extends Collection<VV>> R collect(Function<? super V, ? extends VV> function, R target)
    {
        return this.delegate.collect(function, target);
    }

    @Override
    public <P, V1> MutableCollection<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter)
    {
        return this.delegate.collectWith(function, parameter);
    }

    @Override
    public <P, VV, R extends Collection<VV>> R collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter, R targetCollection)
    {
        return this.delegate.collectWith(function, parameter, targetCollection);
    }

    @Override
    public <R> MutableBiMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.delegate.collectValues(function);
    }

    @Override
    public <R> MutableBiMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.delegate.collectKeysUnique(function);
    }

    @Override
    public BooleanIterable collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.delegate.collectBoolean(booleanFunction);
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
    public ByteIterable collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.delegate.collectByte(byteFunction);
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
    public CharIterable collectChar(CharFunction<? super V> charFunction)
    {
        return this.delegate.collectChar(charFunction);
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
    public DoubleIterable collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.delegate.collectDouble(doubleFunction);
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
    public FloatIterable collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.delegate.collectFloat(floatFunction);
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
    public IntIterable collectInt(IntFunction<? super V> intFunction)
    {
        return this.delegate.collectInt(intFunction);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super V> intFunction, R target)
    {
        return this.delegate.collectInt(intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super V, ? extends IntIterable> function,
            R target)
    {
        return this.delegate.flatCollectInt(function, target);
    }

    @Override
    public LongIterable collectLong(LongFunction<? super V> longFunction)
    {
        return this.delegate.collectLong(longFunction);
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
    public ShortIterable collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.delegate.collectShort(shortFunction);
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
    public <V1> MutableCollection<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function)
    {
        return this.delegate.collectIf(predicate, function);
    }

    @Override
    public <VV, R extends Collection<VV>> R collectIf(Predicate<? super V> predicate, Function<? super V, ? extends VV> function, R target)
    {
        return this.delegate.collectIf(predicate, function, target);
    }

    @Override
    public <V1> MutableCollection<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function)
    {
        return this.delegate.flatCollect(function);
    }

    @Override
    public <P, V1> MutableCollection<V1> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<V1>> function, P parameter)
    {
        return this.delegate.flatCollectWith(function, parameter);
    }

    @Override
    public <VV, R extends Collection<VV>> R flatCollect(Function<? super V, ? extends Iterable<VV>> function, R target)
    {
        return this.delegate.flatCollect(function, target);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super V, ? extends IV> function)
    {
        return this.delegate.injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super V> function)
    {
        return this.delegate.injectIntoInt(injectedValue, function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super V> function)
    {
        return this.delegate.injectIntoLong(injectedValue, function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super V> function)
    {
        return this.delegate.injectIntoFloat(injectedValue, function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super V> function)
    {
        return this.delegate.injectIntoDouble(injectedValue, function);
    }

    @Override
    public <R extends Collection<V>> R into(R target)
    {
        return this.delegate.into(target);
    }

    @Override
    public ImmutableBiMap<K, V> toImmutable()
    {
        return this.delegate.toImmutable();
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
    public <NK, NV> MutableMap<NK, NV> toMap(Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction)
    {
        return this.delegate.toMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction, R target)
    {
        return this.delegate.toMap(keyFunction, valueFunction, target);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction)
    {
        return this.delegate.toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Comparator<? super NK> comparator, Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction)
    {
        return this.delegate.toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction)
    {
        return this.delegate.toBiMap(keyFunction, valueFunction);
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
    public V min()
    {
        return this.delegate.min();
    }

    @Override
    public <VV extends Comparable<? super VV>> V minBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.minBy(function);
    }

    @Override
    public V max(Comparator<? super V> comparator)
    {
        return this.delegate.max(comparator);
    }

    @Override
    public V max()
    {
        return this.delegate.max();
    }

    @Override
    public <VV extends Comparable<? super VV>> V maxBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.maxBy(function);
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
    public <V1> MutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        return this.delegate.sumByInt(groupBy, function);
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        return this.delegate.sumByFloat(groupBy, function);
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        return this.delegate.sumByLong(groupBy, function);
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
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
    public <S> MutableSet<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.delegate.zip(that);
    }

    @Override
    public <S, R extends Collection<Pair<V, S>>> R zip(Iterable<S> that, R target)
    {
        return this.delegate.zip(that, target);
    }

    @Override
    public MutableSet<Pair<V, Integer>> zipWithIndex()
    {
        return this.delegate.zipWithIndex();
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
    public <V1> MutableSetMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function)
    {
        return this.delegate.groupBy(function);
    }

    @Override
    public <VV, R extends MutableMultimap<VV, V>> R groupBy(Function<? super V, ? extends VV> function, R target)
    {
        return this.delegate.groupBy(function, target);
    }

    @Override
    public <V1> MutableSetMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return this.delegate.groupByEach(function);
    }

    @Override
    public <VV, R extends MutableMultimap<VV, V>> R groupByEach(Function<? super V, ? extends Iterable<VV>> function, R target)
    {
        return this.delegate.groupByEach(function, target);
    }

    @Override
    public <VV> MutableBiMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return this.delegate.groupByUniqueKey(function);
    }

    @Override
    public <VV, R extends MutableMapIterable<VV, V>> R groupByUniqueKey(Function<? super V, ? extends VV> function, R target)
    {
        return this.delegate.groupByUniqueKey(function, target);
    }

    @Override
    public V put(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call put() on " + this.getClass().getSimpleName());
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        throw new UnsupportedOperationException("Cannot call putAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public V forcePut(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call forcePut() on " + this.getClass().getSimpleName());
    }

    @Override
    public V putPair(Pair<? extends K, ? extends V> keyValuePair)
    {
        throw new UnsupportedOperationException("Cannot call putPair() on " + this.getClass().getSimpleName());
    }

    @Override
    public V add(Pair<? extends K, ? extends V> keyValuePair)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }

    @Override
    public V remove(Object key)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
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
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        throw new UnsupportedOperationException("Cannot call getIfAbsentPut() on " + this.getClass().getSimpleName());
    }

    @Override
    public V getIfAbsentPut(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call getIfAbsentPut() on " + this.getClass().getSimpleName());
    }

    @Override
    public V getIfAbsentPutWithKey(K key, Function<? super K, ? extends V> function)
    {
        throw new UnsupportedOperationException("Cannot call getIfAbsentPutWithKey() on " + this.getClass().getSimpleName());
    }

    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        throw new UnsupportedOperationException("Cannot call getIfAbsentPutWith() on " + this.getClass().getSimpleName());
    }

    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        throw new UnsupportedOperationException("Cannot call updateValue() on " + this.getClass().getSimpleName());
    }

    @Override
    public <P> V updateValueWith(K key, Function0<? extends V> factory, Function2<? super V, ? super P, ? extends V> function, P parameter)
    {
        throw new UnsupportedOperationException("Cannot call updateValueWith() on " + this.getClass().getSimpleName());
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call merge() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBiMap<K, V> withKeyValue(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call withKeyValue() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBiMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call withMap() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBiMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call withMapIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public void putAllMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call putAllMapIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBiMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        throw new UnsupportedOperationException("Cannot call withAllKeyValues() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBiMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        throw new UnsupportedOperationException("Cannot call keyValuePairs() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBiMap<K, V> withoutKey(K key)
    {
        throw new UnsupportedOperationException("Cannot call withoutKey() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBiMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        throw new UnsupportedOperationException("Cannot call withoutAllKeys() on " + this.getClass().getSimpleName());
    }
}
