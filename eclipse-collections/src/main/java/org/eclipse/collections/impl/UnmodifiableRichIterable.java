/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
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
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * An unmodifiable view of a RichIterable.
 */
public class UnmodifiableRichIterable<T>
        implements RichIterable<T>, Serializable
{
    private static final long serialVersionUID = 1L;

    protected final RichIterable<T> iterable;

    protected UnmodifiableRichIterable(RichIterable<T> richIterable)
    {
        this.iterable = richIterable;
    }

    /**
     * This method will take a RichIterable and wrap it directly in a UnmodifiableRichIterable.
     */
    public static <E, RI extends RichIterable<E>> UnmodifiableRichIterable<E> of(RI iterable)
    {
        if (iterable == null)
        {
            throw new IllegalArgumentException("cannot create a UnmodifiableRichIterable for null");
        }
        return new UnmodifiableRichIterable<>(iterable);
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return this.iterable.select(predicate, target);
    }

    @Override
    public <P> RichIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.iterable.selectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.iterable.selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return this.iterable.reject(predicate, target);
    }

    @Override
    public <P> RichIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.iterable.rejectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.iterable.rejectWith(predicate, parameter, targetCollection);
    }

    @Override
    public PartitionIterable<T> partition(Predicate<? super T> predicate)
    {
        return this.iterable.partition(predicate);
    }

    @Override
    public <P> PartitionIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.iterable.partitionWith(predicate, parameter);
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return this.iterable.collect(function, target);
    }

    @Override
    public <P, V> RichIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.iterable.collectWith(function, parameter);
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R targetCollection)
    {
        return this.iterable.collectWith(function, parameter, targetCollection);
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function, R target)
    {
        return this.iterable.collectIf(predicate, function, target);
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.iterable.flatCollect(function, target);
    }

    @Override
    public boolean contains(Object object)
    {
        return this.iterable.contains(object);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.iterable.containsAllIterable(source);
    }

    @Override
    public boolean containsAll(Collection<?> source)
    {
        return this.iterable.containsAll(source);
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return this.iterable.containsAllArguments(elements);
    }

    @Override
    public Object[] toArray()
    {
        return this.iterable.toArray();
    }

    @Override
    public <T2> T2[] toArray(T2[] a)
    {
        return this.iterable.toArray(a);
    }

    @Override
    public RichIterable<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.iterable.forEach(procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.iterable.forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.iterable.forEachWith(procedure, parameter);
    }

    @Override
    public boolean notEmpty()
    {
        return this.iterable.notEmpty();
    }

    @Override
    public boolean isEmpty()
    {
        return this.iterable.isEmpty();
    }

    @Override
    public int size()
    {
        return this.iterable.size();
    }

    @Override
    public T getFirst()
    {
        return this.iterable.getFirst();
    }

    @Override
    public T getLast()
    {
        return this.iterable.getLast();
    }

    @Override
    public T getOnly()
    {
        return this.iterable.getOnly();
    }

    @Override
    public RichIterable<T> select(Predicate<? super T> predicate)
    {
        return this.iterable.select(predicate);
    }

    @Override
    public RichIterable<T> reject(Predicate<? super T> predicate)
    {
        return this.iterable.reject(predicate);
    }

    @Override
    public <S> RichIterable<S> selectInstancesOf(Class<S> clazz)
    {
        return this.iterable.selectInstancesOf(clazz);
    }

    @Override
    public <V> RichIterable<V> collect(Function<? super T, ? extends V> function)
    {
        return this.iterable.collect(function);
    }

    @Override
    public BooleanIterable collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.iterable.collectBoolean(booleanFunction);
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        return this.iterable.collectBoolean(booleanFunction, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        return this.iterable.flatCollectBoolean(function, target);
    }

    @Override
    public ByteIterable collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.iterable.collectByte(byteFunction);
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        return this.iterable.collectByte(byteFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        return this.iterable.flatCollectByte(function, target);
    }

    @Override
    public CharIterable collectChar(CharFunction<? super T> charFunction)
    {
        return this.iterable.collectChar(charFunction);
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        return this.iterable.collectChar(charFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        return this.iterable.flatCollectChar(function, target);
    }

    @Override
    public DoubleIterable collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.iterable.collectDouble(doubleFunction);
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        return this.iterable.collectDouble(doubleFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function, R target)
    {
        return this.iterable.flatCollectDouble(function, target);
    }

    @Override
    public FloatIterable collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.iterable.collectFloat(floatFunction);
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        return this.iterable.collectFloat(floatFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        return this.iterable.flatCollectFloat(function, target);
    }

    @Override
    public IntIterable collectInt(IntFunction<? super T> intFunction)
    {
        return this.iterable.collectInt(intFunction);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        return this.iterable.collectInt(intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        return this.iterable.flatCollectInt(function, target);
    }

    @Override
    public LongIterable collectLong(LongFunction<? super T> longFunction)
    {
        return this.iterable.collectLong(longFunction);
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        return this.iterable.collectLong(longFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        return this.iterable.flatCollectLong(function, target);
    }

    @Override
    public ShortIterable collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.iterable.collectShort(shortFunction);
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        return this.iterable.collectShort(shortFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        return this.iterable.flatCollectShort(function, target);
    }

    @Override
    public <V> RichIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.iterable.flatCollect(function);
    }

    @Override
    public <V> RichIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return this.iterable.collectIf(predicate, function);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return this.iterable.detect(predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.iterable.detectWith(predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return this.iterable.detectOptional(predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.iterable.detectWithOptional(predicate, parameter);
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        return this.iterable.detectIfNone(predicate, function);
    }

    @Override
    public <P> T detectWithIfNone(Predicate2<? super T, ? super P> predicate, P parameter, Function0<? extends T> function)
    {
        return this.iterable.detectWithIfNone(predicate, parameter, function);
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return this.iterable.min(comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return this.iterable.max(comparator);
    }

    @Override
    public T min()
    {
        return this.iterable.min();
    }

    @Override
    public T max()
    {
        return this.iterable.max();
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return this.iterable.minBy(function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return this.iterable.maxBy(function);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return this.iterable.count(predicate);
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.iterable.countWith(predicate, parameter);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return this.iterable.anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.iterable.anySatisfyWith(predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return this.iterable.allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.iterable.allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return this.iterable.noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.iterable.noneSatisfyWith(predicate, parameter);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return this.iterable.injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        return this.iterable.injectIntoInt(injectedValue, function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        return this.iterable.injectIntoLong(injectedValue, function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        return this.iterable.injectIntoDouble(injectedValue, function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        return this.iterable.injectIntoFloat(injectedValue, function);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        return this.iterable.sumOfInt(function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        return this.iterable.sumOfFloat(function);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        return this.iterable.sumOfLong(function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        return this.iterable.sumOfDouble(function);
    }

    @Override
    public <V> ObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        return this.iterable.sumByInt(groupBy, function);
    }

    @Override
    public <V> ObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        return this.iterable.sumByFloat(groupBy, function);
    }

    @Override
    public <V> ObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        return this.iterable.sumByLong(groupBy, function);
    }

    @Override
    public <V> ObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        return this.iterable.sumByDouble(groupBy, function);
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        return this.iterable.into(target);
    }

    @Override
    public MutableList<T> toList()
    {
        return this.iterable.toList();
    }

    @Override
    public MutableList<T> toSortedList()
    {
        return this.iterable.toSortedList();
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.iterable.toSortedList(comparator);
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        return this.iterable.toSortedSet();
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        return this.iterable.toSortedSet(comparator);
    }

    @Override
    public MutableSet<T> toSet()
    {
        return this.iterable.toSet();
    }

    @Override
    public MutableBag<T> toBag()
    {
        return this.iterable.toBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        return this.iterable.toSortedBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        return this.iterable.toSortedBag(comparator);
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.iterable.toMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction,
            R target)
    {
        return this.iterable.toMap(keyFunction, valueFunction, target);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.iterable.toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Comparator<? super NK> comparator,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.iterable.toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.iterable.toBiMap(keyFunction, valueFunction);
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        return this.iterable.asLazy();
    }

    @Override
    public Iterator<T> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.iterable.iterator());
    }

    @Override
    public String toString()
    {
        return this.iterable.toString();
    }

    @Override
    public String makeString()
    {
        return this.iterable.makeString();
    }

    @Override
    public String makeString(String separator)
    {
        return this.iterable.makeString(separator);
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        return this.iterable.makeString(start, separator, end);
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.iterable.appendString(appendable);
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.iterable.appendString(appendable, separator);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.iterable.appendString(appendable, start, separator, end);
    }

    @Override
    public <V> Multimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.iterable.groupBy(function);
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        return this.iterable.groupBy(function, target);
    }

    @Override
    public <V> Multimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.iterable.groupByEach(function);
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return this.iterable.groupByEach(function, target);
    }

    @Override
    public <V> MapIterable<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.iterable.groupByUniqueKey(function);
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(
            Function<? super T, ? extends V> function,
            R target)
    {
        return this.iterable.groupByUniqueKey(function, target);
    }

    @Override
    public <S> RichIterable<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.iterable.zip(that);
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.iterable.zip(that, target);
    }

    @Override
    public RichIterable<Pair<T, Integer>> zipWithIndex()
    {
        return this.iterable.zipWithIndex();
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.iterable.zipWithIndex(target);
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        return this.iterable.chunk(size);
    }

    @Override
    public <K, V> MapIterable<K, V> aggregateInPlaceBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Procedure2<? super V, ? super T> mutatingAggregator)
    {
        return this.iterable.aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
    }

    @Override
    public <K, V> MapIterable<K, V> aggregateBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        return this.iterable.aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
    }
}
