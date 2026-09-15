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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

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
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
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
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.collection.mutable.UnmodifiableMutableCollection;
import org.eclipse.collections.impl.set.mutable.UnmodifiableMutableSet;
import org.eclipse.collections.impl.tuple.AbstractImmutableEntry;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * An unmodifiable view of a map.
 *
 * @see MutableSortedMap#asUnmodifiable()
 */
public class UnmodifiableTreeMap<K, V>
        extends UnmodifiableSortedMap<K, V>
        implements MutableSortedMap<K, V>
{
    private static final long serialVersionUID = 1L;

    protected UnmodifiableTreeMap(MutableSortedMap<K, V> map)
    {
        super(map);
    }

    /**
     * This method will take a MutableSortedMap and wrap it directly in a UnmodifiableMutableMap. It will
     * take any other non-Eclipse-Collections SortedMap and first adapt it will a SortedMapAdapter, and then return a
     * UnmodifiableSortedMap that wraps the adapter.
     */
    public static <K, V, M extends SortedMap<K, V>> UnmodifiableTreeMap<K, V> of(M map)
    {
        if (map == null)
        {
            throw new IllegalArgumentException("cannot create a UnmodifiableSortedMap for null");
        }
        return new UnmodifiableTreeMap<>(SortedMapAdapter.adapt(map));
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
    public MutableSortedMap<K, V> with(Pair<K, V>... pairs)
    {
        throw new UnsupportedOperationException("Cannot call with() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedMap<K, V> withKeyValue(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call withKeyValue() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call withMap() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call withMapIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public void putAllMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        throw new UnsupportedOperationException("Cannot call putAllMapIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        throw new UnsupportedOperationException("Cannot call withAllKeyValues() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        throw new UnsupportedOperationException("Cannot call withAllKeyValueArguments() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedMap<K, V> withoutKey(K key)
    {
        throw new UnsupportedOperationException("Cannot call withoutKey() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        throw new UnsupportedOperationException("Cannot call withoutAllKeys() on " + this.getClass().getSimpleName());
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
    public <E> MutableSortedMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> valueFunction)
    {
        throw new UnsupportedOperationException("Cannot call collectKeysAndValues() on " + this.getClass().getSimpleName());
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
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            return function.value();
        }
        return result;
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            return value;
        }
        return result;
    }

    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            return function.valueOf(parameter);
        }
        return result;
    }

    @Override
    public MutableSortedSetMultimap<V, K> flip()
    {
        return this.getMutableSortedMap().flip();
    }

    @Override
    public MutableSortedMap<K, V> clone()
    {
        return this;
    }

    @Override
    public MutableSortedMap<K, V> asUnmodifiable()
    {
        return this;
    }

    @Override
    public MutableSortedMap<K, V> asSynchronized()
    {
        return SynchronizedSortedMap.of(this);
    }

    @Override
    public ImmutableSortedMap<K, V> toImmutable()
    {
        return SortedMaps.immutable.withSortedMap(this);
    }

    @Override
    public Iterator<V> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.getMutableSortedMap().iterator());
    }

    @Override
    public int hashCode()
    {
        return this.getMutableSortedMap().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.getMutableSortedMap().equals(obj);
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

    protected MutableSortedMap<K, V> getMutableSortedMap()
    {
        return (MutableSortedMap<K, V>) this.delegate;
    }

    @Override
    public MutableSortedMap<K, V> newEmpty()
    {
        return this.getMutableSortedMap().newEmpty();
    }

    @Override
    public boolean notEmpty()
    {
        return this.getMutableSortedMap().notEmpty();
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.getMutableSortedMap().forEachValue(procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.getMutableSortedMap().forEachKey(procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        this.getMutableSortedMap().forEachKeyValue(procedure);
    }

    @Override
    public MutableMapIterable<V, K> flipUniqueValues()
    {
        return this.getMutableSortedMap().flipUniqueValues();
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        return this.getMutableSortedMap().ifPresentApply(key, function);
    }

    @Override
    public MutableSortedMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public void each(Procedure<? super V> procedure)
    {
        this.getMutableSortedMap().forEach(procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.getMutableSortedMap().forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        this.getMutableSortedMap().forEachWith(procedure, parameter);
    }

    @Override
    public <R> MutableSortedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.getMutableSortedMap().collectValues(function);
    }

    @Override
    public <R> MutableOrderedMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        return this.getMutableSortedMap().collectKeysUnique(function);
    }

    @Override
    public <K2, V2> MutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.getMutableSortedMap().collect(function);
    }

    @Override
    public MutableSortedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return this.getMutableSortedMap().select(predicate);
    }

    @Override
    public MutableSortedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return this.getMutableSortedMap().reject(predicate);
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return this.getMutableSortedMap().detect(predicate);
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        return this.getMutableSortedMap().detectOptional(predicate);
    }

    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableSortedMap().anySatisfyWith(predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableSortedMap().allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableSortedMap().noneSatisfyWith(predicate, parameter);
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.getMutableSortedMap().appendString(appendable);
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.getMutableSortedMap().appendString(appendable, separator);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.getMutableSortedMap().appendString(appendable, start, separator, end);
    }

    @Override
    public MutableBag<V> toBag()
    {
        return this.getMutableSortedMap().toBag();
    }

    @Override
    public MutableSortedBag<V> toSortedBag()
    {
        return this.getMutableSortedMap().toSortedBag();
    }

    @Override
    public MutableSortedBag<V> toSortedBag(Comparator<? super V> comparator)
    {
        return this.getMutableSortedMap().toSortedBag(comparator);
    }

    @Override
    public LazyIterable<V> asLazy()
    {
        return this.getMutableSortedMap().asLazy();
    }

    @Override
    public <R extends Collection<V>> R into(R target)
    {
        return this.getMutableSortedMap().into(target);
    }

    @Override
    public MutableList<V> toList()
    {
        return this.getMutableSortedMap().toList();
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.getMutableSortedMap().toMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction,
            R target)
    {
        return this.getMutableSortedMap().toMap(keyFunction, valueFunction, target);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.getMutableSortedMap().toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Comparator<? super NK> comparator,
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.getMutableSortedMap().toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(
            Function<? super V, ? extends NK> keyFunction,
            Function<? super V, ? extends NV> valueFunction)
    {
        return this.getMutableSortedMap().toBiMap(keyFunction, valueFunction);
    }

    @Override
    public MutableSet<V> toSet()
    {
        return this.getMutableSortedMap().toSet();
    }

    @Override
    public MutableList<V> toSortedList()
    {
        return this.getMutableSortedMap().toSortedList();
    }

    @Override
    public MutableList<V> toSortedList(Comparator<? super V> comparator)
    {
        return this.getMutableSortedMap().toSortedList(comparator);
    }

    @Override
    public MutableSortedSet<V> toSortedSet()
    {
        return this.getMutableSortedMap().toSortedSet();
    }

    @Override
    public MutableSortedSet<V> toSortedSet(Comparator<? super V> comparator)
    {
        return this.getMutableSortedMap().toSortedSet(comparator);
    }

    @Override
    public RichIterable<RichIterable<V>> chunk(int size)
    {
        return this.getMutableSortedMap().chunk(size);
    }

    @Override
    public <R, C extends Collection<R>> C collect(Function<? super V, ? extends R> function, C target)
    {
        return this.getMutableSortedMap().collect(function, target);
    }

    @Override
    public <R, C extends Collection<R>> C collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function, C target)
    {
        return this.getMutableSortedMap().collectIf(predicate, function, target);
    }

    @Override
    public <P, VV> MutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.getMutableSortedMap().collectWith(function, parameter);
    }

    @Override
    public <P, R, C extends Collection<R>> C collectWith(Function2<? super V, ? super P, ? extends R> function, P parameter, C targetCollection)
    {
        return this.getMutableSortedMap().collectWith(function, parameter, targetCollection);
    }

    /**
     * @since 9.1
     */
    @Override
    public <R> MutableList<R> collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends R> function)
    {
        return this.getMutableSortedMap().collectWithIndex(function);
    }

    /**
     * @since 9.1
     */
    @Override
    public <V1, R extends Collection<V1>> R collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends V1> function, R target)
    {
        return this.getMutableSortedMap().collectWithIndex(function, target);
    }

    @Override
    public boolean contains(Object object)
    {
        return this.getMutableSortedMap().contains(object);
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return this.getMutableSortedMap().containsAllArguments(elements);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.getMutableSortedMap().containsAllIterable(source);
    }

    @Override
    public boolean containsAll(Collection<?> source)
    {
        return this.getMutableSortedMap().containsAll(source);
    }

    @Override
    public int count(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().count(predicate);
    }

    @Override
    public <P> int countWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableSortedMap().countWith(predicate, parameter);
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().detect(predicate);
    }

    @Override
    public <P> V detectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableSortedMap().detectWith(predicate, parameter);
    }

    @Override
    public Optional<V> detectOptional(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().detectOptional(predicate);
    }

    @Override
    public <P> Optional<V> detectWithOptional(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableSortedMap().detectWithOptional(predicate, parameter);
    }

    @Override
    public V detectIfNone(Predicate<? super V> predicate, Function0<? extends V> function)
    {
        return this.getMutableSortedMap().detectIfNone(predicate, function);
    }

    @Override
    public <P> V detectWithIfNone(Predicate2<? super V, ? super P> predicate, P parameter, Function0<? extends V> function)
    {
        return this.getMutableSortedMap().detectWithIfNone(predicate, parameter, function);
    }

    @Override
    public <R, C extends Collection<R>> C flatCollect(Function<? super V, ? extends Iterable<R>> function, C target)
    {
        return this.getMutableSortedMap().flatCollect(function, target);
    }

    @Override
    public V getFirst()
    {
        return this.getMutableSortedMap().getFirst();
    }

    @Override
    public V getLast()
    {
        return this.getMutableSortedMap().getLast();
    }

    @Override
    public V getOnly()
    {
        return this.getMutableSortedMap().getOnly();
    }

    @Override
    public <R> MutableListMultimap<R, V> groupBy(Function<? super V, ? extends R> function)
    {
        return this.getMutableSortedMap().groupBy(function);
    }

    @Override
    public <R, C extends MutableMultimap<R, V>> C groupBy(Function<? super V, ? extends R> function, C target)
    {
        return this.getMutableSortedMap().groupBy(function, target);
    }

    @Override
    public <R> MutableListMultimap<R, V> groupByEach(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.getMutableSortedMap().groupByEach(function);
    }

    @Override
    public <R, C extends MutableMultimap<R, V>> C groupByEach(Function<? super V, ? extends Iterable<R>> function, C target)
    {
        return this.getMutableSortedMap().groupByEach(function, target);
    }

    @Override
    public <VV> MutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return this.getMutableSortedMap().groupByUniqueKey(function);
    }

    @Override
    public <VV, R extends MutableMapIterable<VV, V>> R groupByUniqueKey(Function<? super V, ? extends VV> function, R target)
    {
        return this.getMutableSortedMap().groupByUniqueKey(function, target);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super V, ? extends IV> function)
    {
        return this.getMutableSortedMap().injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super V> function)
    {
        return this.getMutableSortedMap().injectIntoInt(injectedValue, function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super V> function)
    {
        return this.getMutableSortedMap().injectIntoLong(injectedValue, function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super V> function)
    {
        return this.getMutableSortedMap().injectIntoDouble(injectedValue, function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super V> function)
    {
        return this.getMutableSortedMap().injectIntoFloat(injectedValue, function);
    }

    @Override
    public long sumOfInt(IntFunction<? super V> function)
    {
        return this.getMutableSortedMap().sumOfInt(function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super V> function)
    {
        return this.getMutableSortedMap().sumOfFloat(function);
    }

    @Override
    public long sumOfLong(LongFunction<? super V> function)
    {
        return this.getMutableSortedMap().sumOfLong(function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super V> function)
    {
        return this.getMutableSortedMap().sumOfDouble(function);
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        MutableObjectLongMap<V1> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function));
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        MutableObjectDoubleMap<V1> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function));
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        MutableObjectLongMap<V1> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function));
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        MutableObjectDoubleMap<V1> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function));
    }

    @Override
    public String makeString()
    {
        return this.getMutableSortedMap().makeString();
    }

    @Override
    public String makeString(String separator)
    {
        return this.getMutableSortedMap().makeString(separator);
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        return this.getMutableSortedMap().makeString(start, separator, end);
    }

    @Override
    public V max()
    {
        return this.getMutableSortedMap().max();
    }

    @Override
    public V max(Comparator<? super V> comparator)
    {
        return this.getMutableSortedMap().max(comparator);
    }

    @Override
    public <R extends Comparable<? super R>> V maxBy(Function<? super V, ? extends R> function)
    {
        return this.getMutableSortedMap().maxBy(function);
    }

    @Override
    public V min()
    {
        return this.getMutableSortedMap().min();
    }

    @Override
    public V min(Comparator<? super V> comparator)
    {
        return this.getMutableSortedMap().min(comparator);
    }

    @Override
    public <R extends Comparable<? super R>> V minBy(Function<? super V, ? extends R> function)
    {
        return this.getMutableSortedMap().minBy(function);
    }

    @Override
    public MutableList<V> select(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().select(predicate);
    }

    @Override
    public <R extends Collection<V>> R select(Predicate<? super V> predicate, R target)
    {
        return this.getMutableSortedMap().select(predicate, target);
    }

    @Override
    public <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableSortedMap().selectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<V>> R selectWith(Predicate2<? super V, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.getMutableSortedMap().selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public <R extends Collection<V>> R reject(Predicate<? super V> predicate, R target)
    {
        return this.getMutableSortedMap().reject(predicate, target);
    }

    @Override
    public MutableList<V> reject(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().reject(predicate);
    }

    @Override
    public <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableSortedMap().rejectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<V>> R rejectWith(Predicate2<? super V, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.getMutableSortedMap().rejectWith(predicate, parameter, targetCollection);
    }

    @Override
    public Object[] toArray()
    {
        return this.getMutableSortedMap().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return this.getMutableSortedMap().toArray(a);
    }

    @Override
    public <S, R extends Collection<Pair<V, S>>> R zip(Iterable<S> that, R target)
    {
        return this.getMutableSortedMap().zip(that, target);
    }

    @Override
    public <R extends Collection<Pair<V, Integer>>> R zipWithIndex(R target)
    {
        return this.getMutableSortedMap().zipWithIndex(target);
    }

    @Override
    public <R> MutableList<R> collect(Function<? super V, ? extends R> function)
    {
        return this.getMutableSortedMap().collect(function);
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.getMutableSortedMap().collectBoolean(booleanFunction);
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super V> booleanFunction, R target)
    {
        return this.getMutableSortedMap().collectBoolean(booleanFunction, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super V, ? extends BooleanIterable> function, R target)
    {
        return this.getMutableSortedMap().flatCollectBoolean(function, target);
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.getMutableSortedMap().collectByte(byteFunction);
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super V> byteFunction, R target)
    {
        return this.getMutableSortedMap().collectByte(byteFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super V, ? extends ByteIterable> function, R target)
    {
        return this.getMutableSortedMap().flatCollectByte(function, target);
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        return this.getMutableSortedMap().collectChar(charFunction);
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super V> charFunction, R target)
    {
        return this.getMutableSortedMap().collectChar(charFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super V, ? extends CharIterable> function, R target)
    {
        return this.getMutableSortedMap().flatCollectChar(function, target);
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.getMutableSortedMap().collectDouble(doubleFunction);
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super V> doubleFunction, R target)
    {
        return this.getMutableSortedMap().collectDouble(doubleFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super V, ? extends DoubleIterable> function, R target)
    {
        return this.getMutableSortedMap().flatCollectDouble(function, target);
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.getMutableSortedMap().collectFloat(floatFunction);
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super V> floatFunction, R target)
    {
        return this.getMutableSortedMap().collectFloat(floatFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super V, ? extends FloatIterable> function, R target)
    {
        return this.getMutableSortedMap().flatCollectFloat(function, target);
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        return this.getMutableSortedMap().collectInt(intFunction);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super V> intFunction, R target)
    {
        return this.getMutableSortedMap().collectInt(intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super V, ? extends IntIterable> function, R target)
    {
        return this.getMutableSortedMap().flatCollectInt(function, target);
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        return this.getMutableSortedMap().collectLong(longFunction);
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super V> longFunction, R target)
    {
        return this.getMutableSortedMap().collectLong(longFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super V, ? extends LongIterable> function, R target)
    {
        return this.getMutableSortedMap().flatCollectLong(function, target);
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.getMutableSortedMap().collectShort(shortFunction);
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super V> shortFunction, R target)
    {
        return this.getMutableSortedMap().collectShort(shortFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super V, ? extends ShortIterable> function, R target)
    {
        return this.getMutableSortedMap().flatCollectShort(function, target);
    }

    @Override
    public <R> MutableList<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function)
    {
        return this.getMutableSortedMap().collectIf(predicate, function);
    }

    @Override
    public <R> MutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.getMutableSortedMap().flatCollect(function);
    }

    @Override
    public PartitionMutableList<V> partition(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().partition(predicate);
    }

    @Override
    public <P> PartitionMutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getMutableSortedMap().partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return this.getMutableSortedMap().selectInstancesOf(clazz);
    }

    @Override
    public <S> MutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.getMutableSortedMap().zip(that);
    }

    @Override
    public MutableList<Pair<V, Integer>> zipWithIndex()
    {
        return this.getMutableSortedMap().zipWithIndex();
    }

    @Override
    public MutableSet<K> keySet()
    {
        return UnmodifiableMutableSet.of(this.getMutableSortedMap().keySet());
    }

    @Override
    public MutableCollection<V> values()
    {
        return UnmodifiableMutableCollection.of(this.getMutableSortedMap().values());
    }

    @Override
    public MutableSortedMap<K, V> headMap(K toKey)
    {
        return UnmodifiableTreeMap.of(this.getMutableSortedMap().headMap(toKey));
    }

    @Override
    public MutableSortedMap<K, V> tailMap(K fromKey)
    {
        return UnmodifiableTreeMap.of(this.getMutableSortedMap().tailMap(fromKey));
    }

    @Override
    public MutableSortedMap<K, V> subMap(K fromKey, K toKey)
    {
        return UnmodifiableTreeMap.of(this.getMutableSortedMap().subMap(fromKey, toKey));
    }

    @Override
    public MutableSet<Entry<K, V>> entrySet()
    {
        return UnmodifiableMutableSet.of(super.entrySet());
    }

    @Override
    public MutableSortedMap<K, V> toReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toReversed() not implemented yet");
    }

    @Override
    public MutableSortedMap<K, V> take(int count)
    {
        return this.getMutableSortedMap().take(count);
    }

    @Override
    public MutableSortedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().takeWhile(predicate);
    }

    @Override
    public MutableSortedMap<K, V> drop(int count)
    {
        return this.getMutableSortedMap().drop(count);
    }

    @Override
    public MutableSortedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().dropWhile(predicate);
    }

    @Override
    public PartitionMutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        return this.getMutableSortedMap().partitionWhile(predicate);
    }

    @Override
    public MutableList<V> distinct()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".distinct() not implemented yet");
    }

    @Override
    public int detectLastIndex(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectLastIndex() not implemented yet");
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super V, ? super S> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".corresponds() not implemented yet");
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super V> procedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEach() not implemented yet");
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super V> objectIntProcedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEachWithIndex() not implemented yet");
    }

    @Override
    public MutableStack<V> toStack()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toStack() not implemented yet");
    }

    @Override
    public int detectIndex(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectIndex() not implemented yet");
    }
}
