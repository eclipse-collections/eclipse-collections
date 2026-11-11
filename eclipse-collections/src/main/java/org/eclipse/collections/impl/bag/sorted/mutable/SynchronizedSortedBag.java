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
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ParallelBag;
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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.sortedbag.MutableSortedBagMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.sorted.PartitionMutableSortedBag;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.SynchronizedRichIterable;
import org.eclipse.collections.impl.collection.mutable.AbstractSynchronizedMutableCollection;
import org.eclipse.collections.impl.collection.mutable.SynchronizedCollectionSerializationProxy;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;

/**
 * A synchronized view of a {@link MutableSortedBag}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableSortedBag#asSynchronized()
 */
public class SynchronizedSortedBag<T>
        extends AbstractSynchronizedMutableCollection<T>
        implements MutableSortedBag<T>, Serializable
{
    private static final long serialVersionUID = 2L;

    protected SynchronizedSortedBag(MutableSortedBag<T> bag)
    {
        super(bag);
    }

    protected SynchronizedSortedBag(MutableSortedBag<T> bag, Object newLock)
    {
        super(bag, newLock);
    }

    public static <E> SynchronizedSortedBag<E> of(MutableSortedBag<E> bag)
    {
        return new SynchronizedSortedBag<>(bag);
    }

    public static <E> MutableSortedBag<E> of(MutableSortedBag<E> bag, Object lock)
    {
        return new SynchronizedSortedBag<>(bag, lock);
    }

    @Override
    protected MutableSortedBag<T> getDelegate()
    {
        return (MutableSortedBag<T>) super.getDelegate();
    }

    @Override
    public MutableSortedBag<T> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().newEmpty().asSynchronized();
        }
    }

    @Override
    public MutableSortedBag<T> clone()
    {
        synchronized (this.getLock())
        {
            return SynchronizedSortedBag.of(this.getDelegate().clone());
        }
    }

    @Override
    public Comparator<? super T> comparator()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().comparator();
        }
    }

    @Override
    public int compareTo(SortedBag<T> o)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().compareTo(o);
        }
    }

    protected Object writeReplace()
    {
        return new SynchronizedCollectionSerializationProxy<>(this.getDelegate());
    }

    @Override
    public int addOccurrences(T item, int occurrences)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().addOccurrences(item, occurrences);
        }
    }

    @Override
    public boolean removeOccurrences(Object item, int occurrences)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().removeOccurrences(item, occurrences);
        }
    }

    @Override
    public boolean setOccurrences(T item, int occurrences)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().setOccurrences(item, occurrences);
        }
    }

    @Override
    public MutableList<ObjectIntPair<T>> topOccurrences(int count)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().topOccurrences(count);
        }
    }

    @Override
    public MutableSortedBag<T> selectByOccurrences(IntPredicate predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectByOccurrences(predicate);
        }
    }

    @Override
    public MutableList<ObjectIntPair<T>> bottomOccurrences(int count)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().bottomOccurrences(count);
        }
    }

    @Override
    public MutableSortedMap<T, Integer> toMapOfItemToCount()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toMapOfItemToCount();
        }
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEachWithOccurrences(procedure);
        }
    }

    @Override
    public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().anySatisfyWithOccurrences(predicate);
        }
    }

    @Override
    public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().allSatisfyWithOccurrences(predicate);
        }
    }

    @Override
    public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().noneSatisfyWithOccurrences(predicate);
        }
    }

    @Override
    public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().detectWithOccurrences(predicate);
        }
    }

    @Override
    public <V> MutableList<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWithOccurrences(function);
        }
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWithOccurrences(function, target);
        }
    }

    @Override
    public int occurrencesOf(Object item)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().occurrencesOf(item);
        }
    }

    @Override
    public int sizeDistinct()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().sizeDistinct();
        }
    }

    @Override
    public String toStringOfItemToCount()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toStringOfItemToCount();
        }
    }

    @Override
    public int indexOf(Object object)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().indexOf(object);
        }
    }

    @Override
    public MutableStack<T> toStack()
    {
        synchronized (this.getLock())
        {
            return ArrayStack.newStack(this);
        }
    }

    @Override
    public PartitionMutableSortedBag<T> partitionWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partitionWhile(predicate);
        }
    }

    @Override
    public MutableSortedSet<T> distinct()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().distinct();
        }
    }

    @Override
    public MutableSortedBag<T> takeWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().takeWhile(predicate);
        }
    }

    @Override
    public MutableSortedBag<T> dropWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().dropWhile(predicate);
        }
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().corresponds(other, predicate);
        }
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEach(startIndex, endIndex, procedure);
        }
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
        }
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().detectIndex(predicate);
        }
    }

    @Override
    public MutableSortedBag<T> tap(Procedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.forEach(procedure);
            return this;
        }
    }

    @Override
    public MutableSortedBag<T> select(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().select(predicate);
        }
    }

    @Override
    public <P> MutableSortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectWith(predicate, parameter);
        }
    }

    @Override
    public MutableSortedBag<T> reject(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().reject(predicate);
        }
    }

    @Override
    public <P> MutableSortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().rejectWith(predicate, parameter);
        }
    }

    @Override
    public PartitionMutableSortedBag<T> partition(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partition(predicate);
        }
    }

    @Override
    public <P> PartitionMutableSortedBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partitionWith(predicate, parameter);
        }
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectBoolean(booleanFunction);
        }
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectByte(byteFunction);
        }
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectChar(charFunction);
        }
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectDouble(doubleFunction);
        }
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectFloat(floatFunction);
        }
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectInt(intFunction);
        }
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectLong(longFunction);
        }
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectShort(shortFunction);
        }
    }

    @Override
    public <S> MutableSortedBag<S> selectInstancesOf(Class<S> clazz)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectInstancesOf(clazz);
        }
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collect(function);
        }
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> MutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWithIndex(function);
        }
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWithIndex(function, target);
        }
    }

    @Override
    public MutableSortedSet<Pair<T, Integer>> zipWithIndex()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().zipWithIndex();
        }
    }

    @Override
    public <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWith(function, parameter);
        }
    }

    @Override
    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectIf(predicate, function);
        }
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().flatCollect(function);
        }
    }

    @Override
    public <V> MutableSortedBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().groupBy(function);
        }
    }

    @Override
    public <V> MutableSortedBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().groupByEach(function);
        }
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().zip(that);
        }
    }

    @Override
    public MutableSortedBag<T> asUnmodifiable()
    {
        synchronized (this.getLock())
        {
            return UnmodifiableSortedBag.of(this);
        }
    }

    @Override
    public MutableSortedBag<T> asSynchronized()
    {
        return this;
    }

    @Override
    public ImmutableSortedBag<T> toImmutable()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toImmutable();
        }
    }

    @Override
    public MutableSortedBag<T> drop(int count)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().drop(count);
        }
    }

    @Override
    public MutableSortedBag<T> take(int count)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().take(count);
        }
    }

    @Override
    public MutableSortedBag<T> toReversed()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().toReversed();
        }
    }

    @Override
    public void reverseForEach(Procedure<? super T> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().reverseForEach(procedure);
        }
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        synchronized (this.lock)
        {
            this.getDelegate().reverseForEachWithIndex(procedure);
        }
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().detectLastIndex(predicate);
        }
    }

    public ParallelBag<T> asParallel(ExecutorService executorService, int batchSize)
    {
        throw new UnsupportedOperationException("asParallel() method is not supported for " + this.getClass().getSimpleName() + '.');
    }

    @Override
    public Optional<T> getFirstOptional()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().getFirstOptional();
        }
    }

    @Override
    public Optional<T> getLastOptional()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().getLastOptional();
        }
    }

    @Override
    public MutableSortedSet<T> selectUnique()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectUnique();
        }
    }

    @Override
    public RichIterable<T> distinctView()
    {
        synchronized (this.lock)
        {
            return SynchronizedRichIterable.of(this.getDelegate().distinctView(), this.lock);
        }
    }
}
