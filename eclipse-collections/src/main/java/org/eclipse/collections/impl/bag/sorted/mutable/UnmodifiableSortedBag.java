/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.sorted.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bag.sorted.SortedBag;
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
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
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
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.sortedbag.MutableSortedBagMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.sorted.PartitionMutableSortedBag;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.collection.mutable.AbstractUnmodifiableMutableCollection;
import org.eclipse.collections.impl.collection.mutable.UnmodifiableCollectionSerializationProxy;

/**
 * An unmodifiable view of a SortedBag.
 *
 * @see MutableSortedBag#asUnmodifiable()
 * @since 4.2
 */
public class UnmodifiableSortedBag<T>
        extends AbstractUnmodifiableMutableCollection<T>
        implements MutableSortedBag<T>, Serializable
{
    protected UnmodifiableSortedBag(MutableSortedBag<? extends T> sortedBag)
    {
        super(sortedBag);
    }

    /**
     * This method will take a MutableSortedBag and wrap it directly in a UnmodifiableSortedBag.
     */
    public static <E, S extends MutableSortedBag<E>> UnmodifiableSortedBag<E> of(S bag)
    {
        if (bag == null)
        {
            throw new IllegalArgumentException("cannot create an UnmodifiableSortedBag for null");
        }
        return new UnmodifiableSortedBag<>(bag);
    }

    protected MutableSortedBag<T> getSortedBag()
    {
        return (MutableSortedBag<T>) this.getMutableCollection();
    }

    @Override
    public MutableSortedBag<T> asUnmodifiable()
    {
        return this;
    }

    @Override
    public MutableSortedBag<T> asSynchronized()
    {
        return SynchronizedSortedBag.of(this);
    }

    @Override
    public ImmutableSortedBag<T> toImmutable()
    {
        return this.getSortedBag().toImmutable();
    }

    @Override
    public UnmodifiableSortedBag<T> clone()
    {
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.getSortedBag().equals(obj);
    }

    @Override
    public int hashCode()
    {
        return this.getSortedBag().hashCode();
    }

    @Override
    public MutableSortedBag<T> newEmpty()
    {
        return this.getSortedBag().newEmpty();
    }

    @Override
    public int addOccurrences(T item, int occurrences)
    {
        throw new UnsupportedOperationException("Cannot call addOccurrences() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean removeOccurrences(Object item, int occurrences)
    {
        throw new UnsupportedOperationException("Cannot call removeOccurrences() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean setOccurrences(T item, int occurrences)
    {
        throw new UnsupportedOperationException("Cannot call setOccurrences() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedBag<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public MutableSortedBag<T> select(Predicate<? super T> predicate)
    {
        return this.getSortedBag().select(predicate);
    }

    @Override
    public <P> MutableSortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getSortedBag().selectWith(predicate, parameter);
    }

    @Override
    public MutableSortedBag<T> reject(Predicate<? super T> predicate)
    {
        return this.getSortedBag().reject(predicate);
    }

    @Override
    public <P> MutableSortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getSortedBag().rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableSortedBag<T> partition(Predicate<? super T> predicate)
    {
        return this.getSortedBag().partition(predicate);
    }

    @Override
    public <P> PartitionMutableSortedBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getSortedBag().partitionWith(predicate, parameter);
    }

    @Override
    public PartitionMutableSortedBag<T> partitionWhile(Predicate<? super T> predicate)
    {
        return this.getSortedBag().partition(predicate);
    }

    @Override
    public int sizeDistinct()
    {
        return this.getSortedBag().sizeDistinct();
    }

    @Override
    public int occurrencesOf(Object item)
    {
        return this.getSortedBag().occurrencesOf(item);
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> procedure)
    {
        this.getSortedBag().forEachWithOccurrences(procedure);
    }

    @Override
    public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.getSortedBag().anySatisfyWithOccurrences(predicate);
    }

    @Override
    public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.getSortedBag().allSatisfyWithOccurrences(predicate);
    }

    @Override
    public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.getSortedBag().noneSatisfyWithOccurrences(predicate);
    }

    @Override
    public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.getSortedBag().detectWithOccurrences(predicate);
    }

    @Override
    public <V> MutableList<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.getSortedBag().collectWithOccurrences(function, Lists.mutable.empty());
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        return this.getSortedBag().collectWithOccurrences(function, target);
    }

    @Override
    public MutableSortedMap<T, Integer> toMapOfItemToCount()
    {
        return this.getSortedBag().toMapOfItemToCount();
    }

    @Override
    public String toStringOfItemToCount()
    {
        return this.getSortedBag().toStringOfItemToCount();
    }

    @Override
    public MutableSortedBag<T> selectByOccurrences(IntPredicate predicate)
    {
        return this.getSortedBag().selectByOccurrences(predicate);
    }

    @Override
    public MutableList<ObjectIntPair<T>> topOccurrences(int count)
    {
        return this.getSortedBag().topOccurrences(count);
    }

    @Override
    public MutableList<ObjectIntPair<T>> bottomOccurrences(int count)
    {
        return this.getSortedBag().bottomOccurrences(count);
    }

    @Override
    public <S> MutableSortedBag<S> selectInstancesOf(Class<S> clazz)
    {
        return this.getSortedBag().selectInstancesOf(clazz);
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return this.getSortedBag().collect(function);
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> MutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.getSortedBag().collectWithIndex(function);
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        return this.getSortedBag().collectWithIndex(function, target);
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.getSortedBag().collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.getSortedBag().collectByte(byteFunction);
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        return this.getSortedBag().collectChar(charFunction);
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.getSortedBag().collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.getSortedBag().collectFloat(floatFunction);
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        return this.getSortedBag().collectInt(intFunction);
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        return this.getSortedBag().collectLong(longFunction);
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.getSortedBag().collectShort(shortFunction);
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getSortedBag().flatCollect(function);
    }

    @Override
    public MutableSortedSet<T> distinct()
    {
        return this.getSortedBag().distinct();
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return this.getSortedBag().corresponds(other, predicate);
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        this.getSortedBag().forEach(startIndex, endIndex, procedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.getSortedBag().forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public MutableStack<T> toStack()
    {
        return this.getSortedBag().toStack();
    }

    @Override
    public int indexOf(Object object)
    {
        return this.getSortedBag().indexOf(object);
    }

    @Override
    public MutableSortedBag<T> takeWhile(Predicate<? super T> predicate)
    {
        return this.getSortedBag().takeWhile(predicate);
    }

    @Override
    public MutableSortedBag<T> dropWhile(Predicate<? super T> predicate)
    {
        return this.getSortedBag().dropWhile(predicate);
    }

    @Override
    public <P, A> MutableList<A> collectWith(Function2<? super T, ? super P, ? extends A> function, P parameter)
    {
        return this.getSortedBag().collectWith(function, parameter);
    }

    @Override
    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return this.getSortedBag().collectIf(predicate, function);
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return this.getSortedBag().detectIndex(predicate);
    }

    @Override
    public <V> MutableSortedBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.getSortedBag().groupBy(function);
    }

    @Override
    public <V> MutableSortedBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getSortedBag().groupByEach(function);
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.getSortedBag().zip(that);
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.getSortedBag().zip(that, target);
    }

    @Override
    public MutableSortedSet<Pair<T, Integer>> zipWithIndex()
    {
        return this.getSortedBag().zipWithIndex();
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.getSortedBag().zipWithIndex(target);
    }

    @Override
    public MutableSortedBag<T> toReversed()
    {
        return this.getSortedBag().toReversed();
    }

    @Override
    public MutableSortedBag<T> take(int count)
    {
        return this.getSortedBag().take(count);
    }

    @Override
    public MutableSortedBag<T> drop(int count)
    {
        return this.getSortedBag().drop(count);
    }

    @Override
    public void reverseForEach(Procedure<? super T> procedure)
    {
        this.getSortedBag().reverseForEach(procedure);
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        this.getSortedBag().reverseForEachWithIndex(procedure);
    }

    @Override
    public LazyIterable<T> asReversed()
    {
        return this.getSortedBag().asReversed();
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        return this.getSortedBag().detectLastIndex(predicate);
    }

    @Override
    public Comparator<? super T> comparator()
    {
        return this.getSortedBag().comparator();
    }

    @Override
    public MutableSortedBag<T> with(T element)
    {
        throw new UnsupportedOperationException("Cannot call with() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedBag<T> without(T element)
    {
        throw new UnsupportedOperationException("Cannot call without() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedBag<T> withAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedBag<T> withoutAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withoutAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public int compareTo(SortedBag<T> o)
    {
        return this.getSortedBag().compareTo(o);
    }

    protected Object writeReplace()
    {
        return new UnmodifiableCollectionSerializationProxy<>(this.getSortedBag());
    }

    @Override
    public MutableSortedSet<T> selectUnique()
    {
        return this.getSortedBag().selectUnique();
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        this.forEachWithOccurrences((each, occurrences) -> result.addToValue(groupBy.valueOf(each), function.intValueOf(each) * (long) occurrences));
        return result;
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        this.forEachWithOccurrences((each, occurrences) -> result.addToValue(
                groupBy.valueOf(each),
                function.longValueOf(each) * (long) occurrences));
        return result;
    }

    @Override
    public RichIterable<T> distinctView()
    {
        return this.getSortedBag().distinctView();
    }
}
