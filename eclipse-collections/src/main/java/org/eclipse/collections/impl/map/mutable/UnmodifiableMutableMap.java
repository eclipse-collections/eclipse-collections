/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable;

import java.util.Collection;
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
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.bag.primitive.MutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.MutableFloatBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.bag.primitive.MutableShortBag;
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
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionMutableBag;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;
import org.eclipse.collections.impl.UnmodifiableMap;
import org.eclipse.collections.impl.tuple.AbstractImmutableEntry;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * An unmodifiable view of a map.
 *
 * @see MutableMap#asUnmodifiable()
 */
public class UnmodifiableMutableMap<K, V>
        extends UnmodifiableMap<K, V>
        implements MutableMap<K, V>
{
    private static final long serialVersionUID = 1L;

    protected UnmodifiableMutableMap(MutableMap<K, V> map)
    {
        super(map);
    }

    /**
     * This method will take a MutableMap and wrap it directly in a UnmodifiableMutableMap. It will
     * take any other non-Eclipse-Collections map and first adapt it will a MapAdapter, and then return a
     * UnmodifiableMutableMap that wraps the adapter.
     */
    public static <K, V, M extends Map<K, V>> UnmodifiableMutableMap<K, V> of(M map)
    {
        if (map == null)
        {
            throw new IllegalArgumentException("cannot create a UnmodifiableMutableMap for null");
        }
        return new UnmodifiableMutableMap<>(MapAdapter.adapt(map));
    }

    @Override
    public MutableMap<K, V> newEmpty()
    {
        return this.getMutableMap().newEmpty();
    }

    @Override
    public boolean notEmpty()
    {
        return this.getMutableMap().notEmpty();
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.getMutableMap().forEachValue(procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.getMutableMap().forEachKey(procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        this.getMutableMap().forEachKeyValue(procedure);
    }

    @Override
    public MutableMap<V, K> flipUniqueValues()
    {
        return this.getMutableMap().flipUniqueValues();
    }

    @Override
    public <E> MutableMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> valueFunction)
    {
        throw new UnsupportedOperationException("Cannot call collectKeysAndValues() on " + this.getClass().getSimpleName());
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

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        return this.getMutableMap().getIfAbsent(key, function);
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        return this.getMutableMap().getIfAbsentValue(key, value);
    }

    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        return this.getMutableMap().getIfAbsentWith(key, function, parameter);
    }

    private boolean isAbsent(V result, K key)
    {
        return result == null && !this.containsKey(key);
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        return this.getMutableMap().ifPresentApply(key, function);
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
    public MutableMap<K, V> withKeyValue(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call withKeyValue() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call withMap() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call withMapIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public void putAllMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call putAllMapIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        throw new UnsupportedOperationException("Cannot call withAllKeyValues() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        throw new UnsupportedOperationException("Cannot call withAllKeyValueArguments() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableMap<K, V> withoutKey(K key)
    {
        throw new UnsupportedOperationException("Cannot call withoutKey() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        throw new UnsupportedOperationException("Cannot call withoutAllKeys() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        return this.getMutableMap().flip();
    }

    @Override
    public MutableMap<K, V> clone()
    {
        return this;
    }

    @Override
    public MutableMap<K, V> asUnmodifiable()
    {
        return this;
    }

    @Override
    public MutableMap<K, V> asSynchronized()
    {
        return SynchronizedMutableMap.of(this);
    }

    @Override
    public MutableMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public void each(Procedure<? super V> procedure)
    {
        this.getMutableMap().forEach(procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.getMutableMap().forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        this.getMutableMap().forEachWith(procedure, parameter);
    }

    @Override
    public Iterator<V> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.getMutableMap().iterator());
    }

    @Override
    public int hashCode()
    {
        return this.getMutableMap().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.getMutableMap().equals(obj);
    }

    protected MutableMap<K, V> getMutableMap()
    {
        return (MutableMap<K, V>) this.delegate;
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
    public ImmutableMap<K, V> toImmutable()
    {
        return Maps.immutable.withMap(this);
    }

    @Override
    public <R> MutableMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.getMutableMap().collectValues(function);
    }

    @Override
    public <R> MutableMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.getMutableMap().collectKeysUnique(function);
    }

    @Override
    public <K2, V2> MutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.getMutableMap().collect(function);
    }

    @Override
    public MutableMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return this.getMutableMap().select(predicate);
    }

    @Override
    public MutableMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return this.getMutableMap().reject(predicate);
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return this.getMutableMap().detect(predicate);
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        return this.getMutableMap().detectOptional(predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return this.getMutableMap().allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableMap().allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super V> predicate)
    {
        return this.getMutableMap().noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableMap().noneSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return this.getMutableMap().anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableMap().anySatisfyWith(predicate, parameter);
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.getMutableMap().appendString(appendable);
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.getMutableMap().appendString(appendable, separator);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.getMutableMap().appendString(appendable, start, separator, end);
    }

    @Override
    public MutableBag<V> toBag()
    {
        return this.getMutableMap().toBag();
    }

    @Override
    public MutableSortedBag<V> toSortedBag()
    {
        return this.getMutableMap().toSortedBag();
    }

    @Override
    public MutableSortedBag<V> toSortedBag(Comparator<? super V> comparator)
    {
        return this.getMutableMap().toSortedBag(comparator);
    }

    @Override
    public LazyIterable<V> asLazy()
    {
        return this.getMutableMap().asLazy();
    }

    @Override
    public <R extends Collection<V>> R into(R target)
    {
        return this.getMutableMap().into(target);
    }

    @Override
    public MutableList<V> toList()
    {
        return this.getMutableMap().toList();
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.getMutableMap().toMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction,
            R target)
    {
        return this.getMutableMap().toMap(keyFunction, valueFunction, target);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.getMutableMap().toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Comparator<? super NK> comparator,
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.getMutableMap().toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.getMutableMap().toBiMap(keyFunction, valueFunction);
    }

    @Override
    public MutableSet<V> toSet()
    {
        return this.getMutableMap().toSet();
    }

    @Override
    public MutableList<V> toSortedList()
    {
        return this.getMutableMap().toSortedList();
    }

    @Override
    public MutableList<V> toSortedList(Comparator<? super V> comparator)
    {
        return this.getMutableMap().toSortedList(comparator);
    }

    @Override
    public MutableSortedSet<V> toSortedSet()
    {
        return this.getMutableMap().toSortedSet();
    }

    @Override
    public MutableSortedSet<V> toSortedSet(Comparator<? super V> comparator)
    {
        return this.getMutableMap().toSortedSet(comparator);
    }

    @Override
    public RichIterable<RichIterable<V>> chunk(int size)
    {
        return this.getMutableMap().chunk(size);
    }

    @Override
    public <R, C extends Collection<R>> C collect(Function<? super V, ? extends R> function, C target)
    {
        return this.getMutableMap().collect(function, target);
    }

    @Override
    public <R, C extends Collection<R>> C collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function, C target)
    {
        return this.getMutableMap().collectIf(predicate, function, target);
    }

    @Override
    public <P, VV> MutableBag<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.getMutableMap().collectWith(function, parameter);
    }

    @Override
    public <P, R, C extends Collection<R>> C collectWith(Function2<? super V, ? super P, ? extends R> function, P parameter, C targetCollection)
    {
        return this.getMutableMap().collectWith(function, parameter, targetCollection);
    }

    @Override
    public boolean contains(Object object)
    {
        return this.containsValue(object);
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return this.getMutableMap().containsAllArguments(elements);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.getMutableMap().containsAllIterable(source);
    }

    @Override
    public boolean containsAll(Collection<?> source)
    {
        return this.containsAllIterable(source);
    }

    @Override
    public int count(Predicate<? super V> predicate)
    {
        return this.getMutableMap().count(predicate);
    }

    @Override
    public <P> int countWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableMap().countWith(predicate, parameter);
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        return this.getMutableMap().detect(predicate);
    }

    @Override
    public <P> V detectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableMap().detectWith(predicate, parameter);
    }

    @Override
    public Optional<V> detectOptional(Predicate<? super V> predicate)
    {
        return this.getMutableMap().detectOptional(predicate);
    }

    @Override
    public <P> Optional<V> detectWithOptional(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableMap().detectWithOptional(predicate, parameter);
    }

    @Override
    public V detectIfNone(Predicate<? super V> predicate, Function0<? extends V> function)
    {
        return this.getMutableMap().detectIfNone(predicate, function);
    }

    @Override
    public <P> V detectWithIfNone(Predicate2<? super V, ? super P> predicate, P parameter, Function0<? extends V> function)
    {
        return this.getMutableMap().detectWithIfNone(predicate, parameter, function);
    }

    @Override
    public <R, C extends Collection<R>> C flatCollect(Function<? super V, ? extends Iterable<R>> function, C target)
    {
        return this.getMutableMap().flatCollect(function, target);
    }

    @Override
    public V getFirst()
    {
        return this.getMutableMap().getFirst();
    }

    @Override
    public V getLast()
    {
        return this.getMutableMap().getLast();
    }

    @Override
    public V getOnly()
    {
        return this.getMutableMap().getOnly();
    }

    @Override
    public <R> MutableBagMultimap<R, V> groupBy(Function<? super V, ? extends R> function)
    {
        return this.getMutableMap().groupBy(function);
    }

    @Override
    public <R, C extends MutableMultimap<R, V>> C groupBy(Function<? super V, ? extends R> function, C target)
    {
        return this.getMutableMap().groupBy(function, target);
    }

    @Override
    public <R> MutableBagMultimap<R, V> groupByEach(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.getMutableMap().groupByEach(function);
    }

    @Override
    public <R, C extends MutableMultimap<R, V>> C groupByEach(Function<? super V, ? extends Iterable<R>> function, C target)
    {
        return this.getMutableMap().groupByEach(function, target);
    }

    @Override
    public <VV> MutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return this.getMutableMap().groupByUniqueKey(function);
    }

    @Override
    public <VV, R extends MutableMapIterable<VV, V>> R groupByUniqueKey(Function<? super V, ? extends VV> function, R target)
    {
        return this.getMutableMap().groupByUniqueKey(function, target);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super V, ? extends IV> function)
    {
        return this.getMutableMap().injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super V> function)
    {
        return this.getMutableMap().injectIntoInt(injectedValue, function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super V> function)
    {
        return this.getMutableMap().injectIntoLong(injectedValue, function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super V> function)
    {
        return this.getMutableMap().injectIntoDouble(injectedValue, function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super V> function)
    {
        return this.getMutableMap().injectIntoFloat(injectedValue, function);
    }

    @Override
    public long sumOfInt(IntFunction<? super V> function)
    {
        return this.getMutableMap().sumOfInt(function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super V> function)
    {
        return this.getMutableMap().sumOfFloat(function);
    }

    @Override
    public long sumOfLong(LongFunction<? super V> function)
    {
        return this.getMutableMap().sumOfLong(function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super V> function)
    {
        return this.getMutableMap().sumOfDouble(function);
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        return this.getMutableMap().sumByInt(groupBy, function);
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        return this.getMutableMap().sumByFloat(groupBy, function);
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        return this.getMutableMap().sumByLong(groupBy, function);
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        return this.getMutableMap().sumByDouble(groupBy, function);
    }

    @Override
    public String makeString()
    {
        return this.getMutableMap().makeString();
    }

    @Override
    public String makeString(String separator)
    {
        return this.getMutableMap().makeString(separator);
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        return this.getMutableMap().makeString(start, separator, end);
    }

    @Override
    public V max()
    {
        return this.getMutableMap().max();
    }

    @Override
    public V max(Comparator<? super V> comparator)
    {
        return this.getMutableMap().max(comparator);
    }

    @Override
    public <R extends Comparable<? super R>> V maxBy(Function<? super V, ? extends R> function)
    {
        return this.getMutableMap().maxBy(function);
    }

    @Override
    public V min()
    {
        return this.getMutableMap().min();
    }

    @Override
    public V min(Comparator<? super V> comparator)
    {
        return this.getMutableMap().min(comparator);
    }

    @Override
    public <R extends Comparable<? super R>> V minBy(Function<? super V, ? extends R> function)
    {
        return this.getMutableMap().minBy(function);
    }

    @Override
    public Object[] toArray()
    {
        return this.getMutableMap().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return this.getMutableMap().toArray(a);
    }

    @Override
    public <S, R extends Collection<Pair<V, S>>> R zip(Iterable<S> that, R target)
    {
        return this.getMutableMap().zip(that, target);
    }

    @Override
    public <R extends Collection<Pair<V, Integer>>> R zipWithIndex(R target)
    {
        return this.getMutableMap().zipWithIndex(target);
    }

    @Override
    public <R> MutableBag<R> collect(Function<? super V, ? extends R> function)
    {
        return this.getMutableMap().collect(function);
    }

    @Override
    public MutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.getMutableMap().collectBoolean(booleanFunction);
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super V> booleanFunction, R target)
    {
        return this.getMutableMap().collectBoolean(booleanFunction, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super V, ? extends BooleanIterable> function, R target)
    {
        return this.getMutableMap().flatCollectBoolean(function, target);
    }

    @Override
    public MutableByteBag collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.getMutableMap().collectByte(byteFunction);
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super V> byteFunction, R target)
    {
        return this.getMutableMap().collectByte(byteFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super V, ? extends ByteIterable> function, R target)
    {
        return this.getMutableMap().flatCollectByte(function, target);
    }

    @Override
    public MutableCharBag collectChar(CharFunction<? super V> charFunction)
    {
        return this.getMutableMap().collectChar(charFunction);
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super V> charFunction, R target)
    {
        return this.getMutableMap().collectChar(charFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super V, ? extends CharIterable> function, R target)
    {
        return this.getMutableMap().flatCollectChar(function, target);
    }

    @Override
    public MutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.getMutableMap().collectDouble(doubleFunction);
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super V> doubleFunction, R target)
    {
        return this.getMutableMap().collectDouble(doubleFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super V, ? extends DoubleIterable> function, R target)
    {
        return this.getMutableMap().flatCollectDouble(function, target);
    }

    @Override
    public MutableFloatBag collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.getMutableMap().collectFloat(floatFunction);
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super V> floatFunction, R target)
    {
        return this.getMutableMap().collectFloat(floatFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super V, ? extends FloatIterable> function, R target)
    {
        return this.getMutableMap().flatCollectFloat(function, target);
    }

    @Override
    public MutableIntBag collectInt(IntFunction<? super V> intFunction)
    {
        return this.getMutableMap().collectInt(intFunction);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super V> intFunction, R target)
    {
        return this.getMutableMap().collectInt(intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super V, ? extends IntIterable> function, R target)
    {
        return this.getMutableMap().flatCollectInt(function, target);
    }

    @Override
    public MutableLongBag collectLong(LongFunction<? super V> longFunction)
    {
        return this.getMutableMap().collectLong(longFunction);
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super V> longFunction, R target)
    {
        return this.getMutableMap().collectLong(longFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super V, ? extends LongIterable> function, R target)
    {
        return this.getMutableMap().flatCollectLong(function, target);
    }

    @Override
    public MutableShortBag collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.getMutableMap().collectShort(shortFunction);
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super V> shortFunction, R target)
    {
        return this.getMutableMap().collectShort(shortFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super V, ? extends ShortIterable> function, R target)
    {
        return this.getMutableMap().flatCollectShort(function, target);
    }

    @Override
    public <R> MutableBag<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function)
    {
        return this.getMutableMap().collectIf(predicate, function);
    }

    @Override
    public <R> MutableBag<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.getMutableMap().flatCollect(function);
    }

    @Override
    public MutableBag<V> select(Predicate<? super V> predicate)
    {
        return this.getMutableMap().select(predicate);
    }

    @Override
    public <R extends Collection<V>> R select(Predicate<? super V> predicate, R target)
    {
        return this.getMutableMap().select(predicate, target);
    }

    @Override
    public <P> MutableBag<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableMap().selectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<V>> R selectWith(Predicate2<? super V, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.getMutableMap().selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public MutableBag<V> reject(Predicate<? super V> predicate)
    {
        return this.getMutableMap().reject(predicate);
    }

    @Override
    public <R extends Collection<V>> R reject(Predicate<? super V> predicate, R target)
    {
        return this.getMutableMap().reject(predicate, target);
    }

    @Override
    public <P> MutableBag<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableMap().rejectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<V>> R rejectWith(Predicate2<? super V, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.getMutableMap().rejectWith(predicate, parameter, targetCollection);
    }

    @Override
    public <S> MutableBag<S> selectInstancesOf(Class<S> clazz)
    {
        return this.getMutableMap().selectInstancesOf(clazz);
    }

    @Override
    public PartitionMutableBag<V> partition(Predicate<? super V> predicate)
    {
        return this.getMutableMap().partition(predicate);
    }

    @Override
    public <P> PartitionMutableBag<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableMap().partitionWith(predicate, parameter);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> MutableBag<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.getMutableMap().zip(that);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public MutableSet<Pair<V, Integer>> zipWithIndex()
    {
        return this.getMutableMap().zipWithIndex();
    }
}
