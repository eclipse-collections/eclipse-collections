/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.immutable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionImmutableBag;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * @since 1.0
 */
public class ImmutableHashBag<T>
        extends AbstractImmutableBag<T>
        implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final MutableBag<T> delegate;

    public ImmutableHashBag()
    {
        this.delegate = Bags.mutable.empty();
    }

    public ImmutableHashBag(Iterable<? extends T> source)
    {
        this.delegate = Bags.mutable.withAll(source);
    }

    public ImmutableHashBag(Bag<? extends T> source)
    {
        this.delegate = Bags.mutable.withAll(source);
    }

    public static <T> ImmutableHashBag<T> newBag()
    {
        return new ImmutableHashBag<>();
    }

    public static <T> ImmutableHashBag<T> newBag(Iterable<? extends T> source)
    {
        return new ImmutableHashBag<>(source);
    }

    public static <T> ImmutableHashBag<T> newBagWith(T... elements)
    {
        return ImmutableHashBag.newBag(Arrays.asList(elements));
    }

    public static <T> ImmutableHashBag<T> newBagWith(Bag<? extends T> bag)
    {
        return new ImmutableHashBag<>(bag);
    }

    @Override
    public ImmutableBag<T> newWith(T element)
    {
        return Bags.mutable.withAll(this.delegate).with(element).toImmutable();
    }

    @Override
    public ImmutableBag<T> newWithout(T element)
    {
        MutableBag<T> hashBag = HashBag.newBag(this.delegate);
        hashBag.remove(element);
        return hashBag.toImmutable();
    }

    @Override
    public ImmutableBag<T> newWithAll(Iterable<? extends T> elements)
    {
        return Iterate.addAllTo(elements, Bags.mutable.withAll(this.delegate)).toImmutable();
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public <V> ImmutableBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        MutableBagMultimap<V, T> bagMultimap = this.delegate.groupBy(function);
        return bagMultimap.toImmutable();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        return this.delegate.groupBy(function, target);
    }

    @Override
    public <V> ImmutableBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.delegate.groupByEach(function).toImmutable();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return this.delegate.groupByEach(function, target);
    }

    @Override
    public <V> ImmutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        MutableMap<V, T> map = this.delegate.groupByUniqueKey(function);
        return map.toImmutable();
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
    public T getFirst()
    {
        return this.delegate.getFirst();
    }

    @Override
    public T getLast()
    {
        return this.delegate.getLast();
    }

    @Override
    public T getOnly()
    {
        return this.delegate.getOnly();
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return this.delegate.min(comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return this.delegate.max(comparator);
    }

    @Override
    public T min()
    {
        return this.delegate.min();
    }

    @Override
    public T max()
    {
        return this.delegate.max();
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.minBy(function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.maxBy(function);
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
    public boolean containsAllArguments(Object... elements)
    {
        return this.delegate.containsAllArguments(elements);
    }

    @Override
    public <K, V> MutableMap<K, V> toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return this.delegate.toMap(keyFunction, valueFunction);
    }

    @Override
    public <K, V, R extends Map<K, V>> R toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction,
            R target)
    {
        return this.delegate.toMap(keyFunction, valueFunction, target);
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.delegate.forEach(procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.delegate.forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <S> ImmutableBag<S> selectInstancesOf(Class<S> clazz)
    {
        return this.delegate.selectInstancesOf(clazz).toImmutable();
    }

    @Override
    public ImmutableBag<T> selectByOccurrences(IntPredicate predicate)
    {
        return this.delegate.selectByOccurrences(predicate).toImmutable();
    }

    @Override
    public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.delegate.anySatisfyWithOccurrences(predicate);
    }

    @Override
    public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.delegate.allSatisfyWithOccurrences(predicate);
    }

    @Override
    public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.delegate.noneSatisfyWithOccurrences(predicate);
    }

    @Override
    public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.delegate.detectWithOccurrences(predicate);
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.delegate.forEachWithOccurrences(objectIntProcedure);
    }

    @Override
    public int sizeDistinct()
    {
        return this.delegate.sizeDistinct();
    }

    @Override
    public int occurrencesOf(Object item)
    {
        return this.delegate.occurrencesOf(item);
    }

    @Override
    public MutableList<T> toList()
    {
        return this.delegate.toList();
    }

    @Override
    public MutableList<T> toSortedList()
    {
        return this.delegate.toSortedList();
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.delegate.toSortedList(comparator);
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        return this.delegate.toSortedSet();
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        return this.delegate.toSortedSet(comparator);
    }

    @Override
    public <K, V> MutableSortedMap<K, V> toSortedMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction)
    {
        return this.delegate.toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <K, V> MutableSortedMap<K, V> toSortedMap(Comparator<? super K> comparator, Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction)
    {
        return this.delegate.toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public ImmutableBag<T> select(Predicate<? super T> predicate)
    {
        return this.delegate.select(predicate).toImmutable();
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return this.delegate.select(predicate, target);
    }

    @Override
    public ImmutableBag<T> reject(Predicate<? super T> predicate)
    {
        return this.delegate.reject(predicate).toImmutable();
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return this.delegate.reject(predicate, target);
    }

    @Override
    public PartitionImmutableBag<T> partition(Predicate<? super T> predicate)
    {
        return this.delegate.partition(predicate).toImmutable();
    }

    @Override
    public <V> ImmutableBag<V> collect(Function<? super T, ? extends V> function)
    {
        MutableBag<V> bag = this.delegate.collect(function);
        return bag.toImmutable();
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.collect(function, target);
    }

    @Override
    public <V> ImmutableBag<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        MutableBag<V> bag = this.delegate.collectIf(predicate, function);
        return bag.toImmutable();
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        return this.delegate.collectIf(predicate, function, target);
    }

    @Override
    public <V> ImmutableBag<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.delegate.flatCollect(function).toImmutable();
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.delegate.flatCollect(function, target);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return this.delegate.detect(predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.detectWith(predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return this.delegate.detectOptional(predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.detectWithOptional(predicate, parameter);
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        return this.delegate.detectIfNone(predicate, function);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return this.delegate.count(predicate);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.anySatisfyWith(predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.noneSatisfyWith(predicate, parameter);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return this.delegate.injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        return this.delegate.injectIntoInt(injectedValue, function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        return this.delegate.injectIntoLong(injectedValue, function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        return this.delegate.injectIntoDouble(injectedValue, function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        return this.delegate.injectIntoFloat(injectedValue, function);
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.delegate.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public MutableMap<T, Integer> toMapOfItemToCount()
    {
        return this.delegate.toMapOfItemToCount();
    }

    @Override
    public MutableSet<T> toSet()
    {
        return this.delegate.toSet();
    }

    @Override
    public MutableBag<T> toBag()
    {
        return this.delegate.toBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        return this.delegate.toSortedBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        return this.delegate.toSortedBag(comparator);
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        return this.delegate.asLazy();
    }

    @Override
    public Object[] toArray()
    {
        return this.delegate.toArray();
    }

    @Override
    public <T2> T2[] toArray(T2[] a)
    {
        return this.delegate.toArray(a);
    }

    @Override
    public String toString()
    {
        return this.makeString("[", ", ", "]");
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

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> ImmutableBag<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.delegate.zip(that).toImmutable();
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.delegate.zip(that, target);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public ImmutableSet<Pair<T, Integer>> zipWithIndex()
    {
        return this.delegate.zipWithIndex().toImmutable();
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.delegate.zipWithIndex(target);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.delegate.iterator());
    }

    protected Object writeReplace()
    {
        return new ImmutableBagSerializationProxy<>(this);
    }

    @Override
    public RichIterable<T> distinctView()
    {
        return this.delegate.distinctView();
    }
}
