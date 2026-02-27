/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.LazyIterable;
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
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.sorted.PartitionMutableSortedSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.ParallelSortedSetIterable;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;
import org.eclipse.collections.impl.collection.mutable.AbstractUnmodifiableMutableCollection;
import org.eclipse.collections.impl.collection.mutable.UnmodifiableCollectionSerializationProxy;

/**
 * An unmodifiable view of a SortedSet.
 *
 * @see MutableSortedSet#asUnmodifiable()
 */
public class UnmodifiableSortedSet<T>
        extends AbstractUnmodifiableMutableCollection<T>
        implements MutableSortedSet<T>, Serializable
{
    UnmodifiableSortedSet(MutableSortedSet<? extends T> sortedSet)
    {
        super(sortedSet);
    }

    /**
     * This method will take a MutableSortedSet and wrap it directly in a UnmodifiableSortedSet. It will
     * take any other non-Eclipse-Collections SortedSet and first adapt it will a SortedSetAdapter, and then return a
     * UnmodifiableSortedSet that wraps the adapter.
     */
    public static <E, S extends SortedSet<E>> UnmodifiableSortedSet<E> of(S set)
    {
        if (set == null)
        {
            throw new IllegalArgumentException("cannot create an UnmodifiableSortedSet for null");
        }
        return new UnmodifiableSortedSet<>(SortedSetAdapter.adapt(set));
    }

    protected MutableSortedSet<T> getSortedSet()
    {
        return (MutableSortedSet<T>) this.getMutableCollection();
    }

    @Override
    public MutableSortedSet<T> asUnmodifiable()
    {
        return this;
    }

    @Override
    public MutableSortedSet<T> asSynchronized()
    {
        return SynchronizedSortedSet.of(this);
    }

    @Override
    public ImmutableSortedSet<T> toImmutable()
    {
        return SortedSets.immutable.withSortedSet(this.getSortedSet());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        return this.getSortedSet().equals(obj);
    }

    @Override
    public int hashCode()
    {
        return this.getSortedSet().hashCode();
    }

    @Override
    public UnmodifiableSortedSet<T> clone()
    {
        return this;
    }

    @Override
    public MutableSortedSet<T> newEmpty()
    {
        return this.getSortedSet().newEmpty();
    }

    @Override
    public MutableSortedSet<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public MutableSortedSet<T> select(Predicate<? super T> predicate)
    {
        return this.getSortedSet().select(predicate);
    }

    @Override
    public <P> MutableSortedSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getSortedSet().selectWith(predicate, parameter);
    }

    @Override
    public MutableSortedSet<T> reject(Predicate<? super T> predicate)
    {
        return this.getSortedSet().reject(predicate);
    }

    @Override
    public <P> MutableSortedSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getSortedSet().rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableSortedSet<T> partition(Predicate<? super T> predicate)
    {
        return this.getSortedSet().partition(predicate);
    }

    @Override
    public <P> PartitionMutableSortedSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getSortedSet().partitionWith(predicate, parameter);
    }

    @Override
    public PartitionMutableSortedSet<T> partitionWhile(Predicate<? super T> predicate)
    {
        return this.getSortedSet().partitionWhile(predicate);
    }

    @Override
    public <S> MutableSortedSet<S> selectInstancesOf(Class<S> clazz)
    {
        return this.getSortedSet().selectInstancesOf(clazz);
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return this.getSortedSet().collect(function);
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.getSortedSet().collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.getSortedSet().collectByte(byteFunction);
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        return this.getSortedSet().collectChar(charFunction);
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.getSortedSet().collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.getSortedSet().collectFloat(floatFunction);
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        return this.getSortedSet().collectInt(intFunction);
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        return this.getSortedSet().collectLong(longFunction);
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.getSortedSet().collectShort(shortFunction);
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getSortedSet().flatCollect(function);
    }

    @Override
    public <P, A> MutableList<A> collectWith(Function2<? super T, ? super P, ? extends A> function, P parameter)
    {
        return this.getSortedSet().collectWith(function, parameter);
    }

    /**
     * @since 9.1
     */
    @Override
    public <V> MutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.getSortedSet().collectWithIndex(function);
    }

    /**
     * @since 9.1
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        return this.getSortedSet().collectWithIndex(function, target);
    }

    @Override
    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return this.getSortedSet().collectIf(predicate, function);
    }

    @Override
    public MutableSortedSet<T> distinct()
    {
        return this.getSortedSet().distinct();
    }

    @Override
    public MutableSortedSet<T> takeWhile(Predicate<? super T> predicate)
    {
        return this.getSortedSet().takeWhile(predicate);
    }

    @Override
    public MutableSortedSet<T> dropWhile(Predicate<? super T> predicate)
    {
        return this.getSortedSet().dropWhile(predicate);
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return this.getSortedSet().detectIndex(predicate);
    }

    @Override
    public <V> MutableSortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.getSortedSet().groupBy(function);
    }

    @Override
    public <V> MutableSortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getSortedSet().groupByEach(function);
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.getSortedSet().zip(that);
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.getSortedSet().zip(that, target);
    }

    @Override
    public MutableSortedSet<Pair<T, Integer>> zipWithIndex()
    {
        return this.getSortedSet().zipWithIndex();
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.getSortedSet().zipWithIndex(target);
    }

    @Override
    public Comparator<? super T> comparator()
    {
        return this.getSortedSet().comparator();
    }

    @Override
    public MutableSortedSet<T> union(SetIterable<? extends T> set)
    {
        return this.getSortedSet().union(set);
    }

    @Override
    public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
    {
        return this.getSortedSet().unionInto(set, targetSet);
    }

    @Override
    public MutableSortedSet<T> intersect(SetIterable<? extends T> set)
    {
        return this.getSortedSet().intersect(set);
    }

    @Override
    public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
    {
        return this.getSortedSet().intersectInto(set, targetSet);
    }

    @Override
    public MutableSortedSet<T> difference(SetIterable<? extends T> subtrahendSet)
    {
        return this.getSortedSet().difference(subtrahendSet);
    }

    @Override
    public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
    {
        return this.getSortedSet().differenceInto(subtrahendSet, targetSet);
    }

    @Override
    public MutableSortedSet<T> symmetricDifference(SetIterable<? extends T> setB)
    {
        return this.getSortedSet().symmetricDifference(setB);
    }

    @Override
    public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
    {
        return this.getSortedSet().symmetricDifferenceInto(set, targetSet);
    }

    @Override
    public boolean isSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return this.getSortedSet().isSubsetOf(candidateSuperset);
    }

    @Override
    public boolean isProperSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return this.getSortedSet().isProperSubsetOf(candidateSuperset);
    }

    @Override
    public MutableSortedSet<SortedSetIterable<T>> powerSet()
    {
        return this.getSortedSet().powerSet();
    }

    @Override
    public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
    {
        return this.getSortedSet().cartesianProduct(set);
    }

    @Override
    public T lower(T e)
    {
        return this.getSortedSet().lower(e);
    }

    @Override
    public T floor(T e)
    {
        return this.getSortedSet().floor(e);
    }

    @Override
    public T ceiling(T e)
    {
        return this.getSortedSet().ceiling(e);
    }

    @Override
    public T higher(T e)
    {
        return this.getSortedSet().higher(e);
    }

    @Override
    public T pollFirst()
    {
        throw new UnsupportedOperationException("Cannot call pollFirst() on " + this.getClass().getSimpleName());
    }

    @Override
    public T pollLast()
    {
        throw new UnsupportedOperationException("Cannot call pollLast() on " + this.getClass().getSimpleName());
    }

    @Override
    public Iterator<T> descendingIterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.getSortedSet().descendingIterator());
    }

    @Override
    public MutableSortedSet<T> descendingSet()
    {
        return this.getSortedSet().descendingSet().asUnmodifiable();
    }

    @Override
    public MutableSortedSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive)
    {
        return this.getSortedSet().subSet(fromElement, fromInclusive, toElement, toInclusive).asUnmodifiable();
    }

    @Override
    public MutableSortedSet<T> headSet(T toElement, boolean inclusive)
    {
        return this.getSortedSet().headSet(toElement, inclusive).asUnmodifiable();
    }

    @Override
    public MutableSortedSet<T> tailSet(T fromElement, boolean inclusive)
    {
        return this.getSortedSet().tailSet(fromElement, inclusive).asUnmodifiable();
    }

    @Override
    public MutableSortedSet<T> subSet(T fromElement, T toElement)
    {
        return this.getSortedSet().subSet(fromElement, toElement).asUnmodifiable();
    }

    @Override
    public MutableSortedSet<T> headSet(T toElement)
    {
        return this.getSortedSet().headSet(toElement).asUnmodifiable();
    }

    @Override
    public MutableSortedSet<T> tailSet(T fromElement)
    {
        return this.getSortedSet().tailSet(fromElement).asUnmodifiable();
    }

    @Override
    public T first()
    {
        return this.getSortedSet().first();
    }

    @Override
    public T last()
    {
        return this.getSortedSet().last();
    }

    @Override
    public int compareTo(SortedSetIterable<T> o)
    {
        return this.getSortedSet().compareTo(o);
    }

    @Override
    public int indexOf(Object object)
    {
        return this.getSortedSet().indexOf(object);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return this.getSortedSet().corresponds(other, predicate);
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        this.getSortedSet().forEach(startIndex, endIndex, procedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.getSortedSet().forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public MutableSortedSet<T> toReversed()
    {
        return this.getSortedSet().toReversed();
    }

    @Override
    public MutableSortedSet<T> take(int count)
    {
        return this.getSortedSet().take(count);
    }

    @Override
    public MutableSortedSet<T> drop(int count)
    {
        return this.getSortedSet().drop(count);
    }

    @Override
    public void reverseForEach(Procedure<? super T> procedure)
    {
        this.getSortedSet().reverseForEach(procedure);
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        this.getSortedSet().reverseForEachWithIndex(procedure);
    }

    @Override
    public LazyIterable<T> asReversed()
    {
        return this.getSortedSet().asReversed();
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        return this.getSortedSet().detectLastIndex(predicate);
    }

    @Override
    public MutableSortedSet<T> with(T element)
    {
        throw new UnsupportedOperationException("Cannot call with() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedSet<T> without(T element)
    {
        throw new UnsupportedOperationException("Cannot call without() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedSet<T> withAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableSortedSet<T> withoutAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withoutAll() on " + this.getClass().getSimpleName());
    }

    protected Object writeReplace()
    {
        return new UnmodifiableCollectionSerializationProxy<>(this.getSortedSet());
    }

    @Override
    public ParallelSortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return this.getSortedSet().asParallel(executorService, batchSize);
    }
}
