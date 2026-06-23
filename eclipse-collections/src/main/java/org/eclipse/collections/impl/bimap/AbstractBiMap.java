/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bimap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

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
import org.eclipse.collections.api.bimap.BiMap;
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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;

public abstract class AbstractBiMap<K, V> implements BiMap<K, V>
{
    protected abstract MapIterable<K, V> getDelegate();

    protected abstract MapIterable<V, K> getInverse();

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof Map))
        {
            return false;
        }

        Map<?, ?> map = (Map<?, ?>) obj;

        return this.getDelegate().equals(map);
    }

    @Override
    public int hashCode()
    {
        return this.getDelegate().hashCode();
    }

    @Override
    public int size()
    {
        return this.getDelegate().size();
    }

    @Override
    public V get(Object key)
    {
        return this.getDelegate().get(key);
    }

    @Override
    public V getFirst()
    {
        return this.getDelegate().getFirst();
    }

    @Override
    public V getLast()
    {
        return this.getDelegate().getLast();
    }

    @Override
    public V getOnly()
    {
        return this.getDelegate().getOnly();
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        return this.getDelegate().getIfAbsent(key, function);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue)
    {
        return this.getIfAbsentValue((K) key, defaultValue);
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        return this.getDelegate().getIfAbsentValue(key, value);
    }

    @Override
    public <P> V getIfAbsentWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        return this.getDelegate().getIfAbsentWith(key, function, parameter);
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        return this.getDelegate().ifPresentApply(key, function);
    }

    @Override
    public boolean isEmpty()
    {
        return this.getDelegate().isEmpty();
    }

    @Override
    public boolean notEmpty()
    {
        return this.getDelegate().notEmpty();
    }

    @Override
    public boolean contains(Object object)
    {
        return this.getInverse().containsKey(object);
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.getDelegate().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.getInverse().containsKey(value);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.getInverse().keysView().containsAllIterable(source);
    }

    @Override
    public boolean containsAll(Collection<?> source)
    {
        return this.getInverse().keysView().containsAllIterable(source);
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return this.getInverse().keysView().containsAllArguments(elements);
    }

    @Override
    public RichIterable<K> keysView()
    {
        return this.getDelegate().keysView();
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return this.getDelegate().valuesView();
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return this.getDelegate().keyValuesView();
    }

    @Override
    public <R extends Collection<V>> R into(R target)
    {
        return this.getDelegate().into(target);
    }

    @Override
    public MutableList<V> toList()
    {
        return this.getDelegate().toList();
    }

    @Override
    public MutableList<V> toSortedList()
    {
        return this.getDelegate().toSortedList();
    }

    @Override
    public MutableList<V> toSortedList(Comparator<? super V> comparator)
    {
        return this.getDelegate().toSortedList(comparator);
    }

    @Override
    public MutableSet<V> toSet()
    {
        return this.getDelegate().toSet();
    }

    @Override
    public MutableSortedSet<V> toSortedSet()
    {
        return this.getDelegate().toSortedSet();
    }

    @Override
    public MutableSortedSet<V> toSortedSet(Comparator<? super V> comparator)
    {
        return this.getDelegate().toSortedSet(comparator);
    }

    @Override
    public MutableBag<V> toBag()
    {
        return this.getDelegate().toBag();
    }

    @Override
    public MutableSortedBag<V> toSortedBag()
    {
        return this.getDelegate().toSortedBag();
    }

    @Override
    public MutableSortedBag<V> toSortedBag(Comparator<? super V> comparator)
    {
        return this.getDelegate().toSortedBag(comparator);
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction)
    {
        return this.getDelegate().toMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction, R target)
    {
        return this.getDelegate().toMap(keyFunction, valueFunction, target);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction)
    {
        return this.getDelegate().toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Comparator<? super NK> comparator, Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction)
    {
        return this.getDelegate().toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(Function<? super V, ? extends NK> keyFunction, Function<? super V, ? extends NV> valueFunction)
    {
        return this.getDelegate().toBiMap(keyFunction, valueFunction);
    }

    @Override
    public Object[] toArray()
    {
        return this.getDelegate().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return this.getDelegate().toArray(a);
    }

    @Override
    public String toString()
    {
        return this.getDelegate().toString();
    }

    @Override
    public String makeString()
    {
        return this.getDelegate().makeString();
    }

    @Override
    public String makeString(String separator)
    {
        return this.getDelegate().makeString(separator);
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        return this.getDelegate().makeString(start, separator, end);
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.getDelegate().appendString(appendable);
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.getDelegate().appendString(appendable, separator);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.getDelegate().appendString(appendable, start, separator, end);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.getDelegate().forEachValue(procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.getDelegate().forEachKey(procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        this.getDelegate().forEachKeyValue(procedure);
    }

    @Override
    public void each(Procedure<? super V> procedure)
    {
        this.getDelegate().forEachValue(procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.getDelegate().forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        this.getDelegate().forEachWith(procedure, parameter);
    }

    @Override
    public LazyIterable<V> asLazy()
    {
        return this.getDelegate().asLazy();
    }

    @Override
    public int count(Predicate<? super V> predicate)
    {
        return this.getDelegate().count(predicate);
    }

    @Override
    public <P> int countWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getDelegate().countWith(predicate, parameter);
    }

    @Override
    public V min(Comparator<? super V> comparator)
    {
        return this.getDelegate().min(comparator);
    }

    @Override
    public V min()
    {
        return this.getDelegate().min();
    }

    @Override
    public <VV extends Comparable<? super VV>> V minBy(Function<? super V, ? extends VV> function)
    {
        return this.getDelegate().minBy(function);
    }

    @Override
    public V max(Comparator<? super V> comparator)
    {
        return this.getDelegate().max(comparator);
    }

    @Override
    public V max()
    {
        return this.getDelegate().max();
    }

    @Override
    public <VV extends Comparable<? super VV>> V maxBy(Function<? super V, ? extends VV> function)
    {
        return this.getDelegate().maxBy(function);
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return this.getDelegate().detect(predicate);
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        return this.getDelegate().detect(predicate);
    }

    @Override
    public <P> V detectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getDelegate().detectWith(predicate, parameter);
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        return this.getDelegate().detectOptional(predicate);
    }

    @Override
    public Optional<V> detectOptional(Predicate<? super V> predicate)
    {
        return this.getDelegate().detectOptional(predicate);
    }

    @Override
    public <P> Optional<V> detectWithOptional(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getDelegate().detectWithOptional(predicate, parameter);
    }

    @Override
    public V detectIfNone(Predicate<? super V> predicate, Function0<? extends V> function)
    {
        return this.getDelegate().detectIfNone(predicate, function);
    }

    @Override
    public <P> V detectWithIfNone(Predicate2<? super V, ? super P> predicate, P parameter, Function0<? extends V> function)
    {
        return this.getDelegate().detectWithIfNone(predicate, parameter, function);
    }

    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return this.getDelegate().anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getDelegate().anySatisfyWith(predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return this.getDelegate().allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getDelegate().allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super V> predicate)
    {
        return this.getDelegate().noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.getDelegate().noneSatisfyWith(predicate, parameter);
    }

    @Override
    public <VV, R extends Collection<VV>> R collect(Function<? super V, ? extends VV> function, R target)
    {
        return this.getDelegate().collect(function, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super V> booleanFunction, R target)
    {
        return this.getDelegate().collectBoolean(booleanFunction, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super V, ? extends BooleanIterable> function, R target)
    {
        return this.getDelegate().flatCollectBoolean(function, target);
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super V> byteFunction, R target)
    {
        return this.getDelegate().collectByte(byteFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super V, ? extends ByteIterable> function, R target)
    {
        return this.getDelegate().flatCollectByte(function, target);
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super V> charFunction, R target)
    {
        return this.getDelegate().collectChar(charFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super V, ? extends CharIterable> function, R target)
    {
        return this.getDelegate().flatCollectChar(function, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super V> doubleFunction, R target)
    {
        return this.getDelegate().collectDouble(doubleFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super V, ? extends DoubleIterable> function,
            R target)
    {
        return this.getDelegate().flatCollectDouble(function, target);
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super V> floatFunction, R target)
    {
        return this.getDelegate().collectFloat(floatFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super V, ? extends FloatIterable> function,
            R target)
    {
        return this.getDelegate().flatCollectFloat(function, target);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super V> intFunction, R target)
    {
        return this.getDelegate().collectInt(intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super V, ? extends IntIterable> function,
            R target)
    {
        return this.getDelegate().flatCollectInt(function, target);
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super V> longFunction, R target)
    {
        return this.getDelegate().collectLong(longFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super V, ? extends LongIterable> function,
            R target)
    {
        return this.getDelegate().flatCollectLong(function, target);
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super V> shortFunction, R target)
    {
        return this.getDelegate().collectShort(shortFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super V, ? extends ShortIterable> function,
            R target)
    {
        return this.getDelegate().flatCollectShort(function, target);
    }

    @Override
    public <P, VV, R extends Collection<VV>> R collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter, R targetCollection)
    {
        return this.getDelegate().collectWith(function, parameter, targetCollection);
    }

    @Override
    public <VV, R extends Collection<VV>> R collectIf(Predicate<? super V> predicate, Function<? super V, ? extends VV> function, R target)
    {
        return this.getDelegate().collectIf(predicate, function, target);
    }

    @Override
    public <VV, R extends Collection<VV>> R flatCollect(Function<? super V, ? extends Iterable<VV>> function, R target)
    {
        return this.getDelegate().flatCollect(function, target);
    }

    @Override
    public <R extends Collection<V>> R select(Predicate<? super V> predicate, R target)
    {
        return this.getDelegate().select(predicate, target);
    }

    @Override
    public <P, R extends Collection<V>> R selectWith(Predicate2<? super V, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.getDelegate().selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public <R extends Collection<V>> R reject(Predicate<? super V> predicate, R target)
    {
        return this.getDelegate().reject(predicate, target);
    }

    @Override
    public <P, R extends Collection<V>> R rejectWith(Predicate2<? super V, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.getDelegate().rejectWith(predicate, parameter, targetCollection);
    }

    /**
     * @deprecated in 8.0. Use {@link org.eclipse.collections.api.ordered.OrderedIterable#zip(Iterable, Collection)} instead.
     */
    @Override
    @Deprecated
    public <S, R extends Collection<Pair<V, S>>> R zip(Iterable<S> that, R target)
    {
        return this.getDelegate().zip(that, target);
    }

    /**
     * @deprecated in 8.0. Use {@link org.eclipse.collections.api.ordered.OrderedIterable#zipWithIndex(Collection)} instead.
     */
    @Override
    @Deprecated
    public <R extends Collection<Pair<V, Integer>>> R zipWithIndex(R target)
    {
        return this.getDelegate().zipWithIndex(target);
    }

    @Override
    public RichIterable<RichIterable<V>> chunk(int size)
    {
        return this.getDelegate().chunk(size);
    }

    @Override
    public <VV, R extends MutableMultimap<VV, V>> R groupBy(Function<? super V, ? extends VV> function, R target)
    {
        return this.getDelegate().groupBy(function, target);
    }

    @Override
    public <VV, R extends MutableMultimap<VV, V>> R groupByEach(Function<? super V, ? extends Iterable<VV>> function, R target)
    {
        return this.getDelegate().groupByEach(function, target);
    }

    @Override
    public <VV, R extends MutableMapIterable<VV, V>> R groupByUniqueKey(Function<? super V, ? extends VV> function, R target)
    {
        return this.getDelegate().groupByUniqueKey(function, target);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super V, ? extends IV> function)
    {
        return this.getDelegate().injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super V> function)
    {
        return this.getDelegate().injectIntoInt(injectedValue, function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super V> function)
    {
        return this.getDelegate().injectIntoLong(injectedValue, function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super V> function)
    {
        return this.getDelegate().injectIntoFloat(injectedValue, function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super V> function)
    {
        return this.getDelegate().injectIntoDouble(injectedValue, function);
    }

    @Override
    public long sumOfInt(IntFunction<? super V> function)
    {
        return this.getDelegate().sumOfInt(function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super V> function)
    {
        return this.getDelegate().sumOfFloat(function);
    }

    @Override
    public long sumOfLong(LongFunction<? super V> function)
    {
        return this.getDelegate().sumOfLong(function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super V> function)
    {
        return this.getDelegate().sumOfDouble(function);
    }
}
