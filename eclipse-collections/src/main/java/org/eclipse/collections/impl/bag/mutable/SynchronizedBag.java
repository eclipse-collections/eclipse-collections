/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.bag.primitive.MutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.MutableFloatBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.bag.primitive.MutableShortBag;
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
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionMutableBag;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.SynchronizedRichIterable;
import org.eclipse.collections.impl.collection.mutable.AbstractSynchronizedMutableCollection;
import org.eclipse.collections.impl.collection.mutable.SynchronizedCollectionSerializationProxy;

/**
 * A synchronized view of a {@link MutableBag}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableBag#asSynchronized()
 * @since 1.0
 */
public class SynchronizedBag<T>
        extends AbstractSynchronizedMutableCollection<T>
        implements MutableBag<T>, Serializable
{
    private static final long serialVersionUID = 2L;

    protected SynchronizedBag(MutableBag<T> bag)
    {
        super(bag);
    }

    public SynchronizedBag(MutableBag<T> bag, Object newLock)
    {
        super(bag, newLock);
    }

    /**
     * This method will take a MutableBag and wrap it directly in a SynchronizedBag.
     */
    public static <E, B extends MutableBag<E>> SynchronizedBag<E> of(B bag)
    {
        return new SynchronizedBag<>(bag);
    }

    /**
     * This method will take a MutableBag and wrap it directly in a SynchronizedBag. Additionally,
     * a developer specifies which lock to use with the collection.
     */
    public static <E, B extends MutableBag<E>> SynchronizedBag<E> of(B bag, Object lock)
    {
        return new SynchronizedBag<>(bag, lock);
    }

    @Override
    protected MutableBag<T> getDelegate()
    {
        return (MutableBag<T>) super.getDelegate();
    }

    @Override
    public MutableBag<T> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().newEmpty().asSynchronized();
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
    public MutableMap<T, Integer> toMapOfItemToCount()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toMapOfItemToCount();
        }
    }

    @Override
    public MutableBag<T> selectByOccurrences(IntPredicate predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectByOccurrences(predicate);
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
    public MutableList<ObjectIntPair<T>> bottomOccurrences(int count)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().bottomOccurrences(count);
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
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEachWithOccurrences(objectIntProcedure);
        }
    }

    @Override
    public <V> MutableBag<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function)
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
    public MutableBag<T> tap(Procedure<? super T> procedure)
    {
        return (MutableBag<T>) super.tap(procedure);
    }

    @Override
    public MutableBag<T> select(Predicate<? super T> predicate)
    {
        return (MutableBag<T>) super.select(predicate);
    }

    @Override
    public <P> MutableBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (MutableBag<T>) super.selectWith(predicate, parameter);
    }

    @Override
    public MutableBag<T> reject(Predicate<? super T> predicate)
    {
        return (MutableBag<T>) super.reject(predicate);
    }

    @Override
    public <P> MutableBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (MutableBag<T>) super.rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableBag<T> partition(Predicate<? super T> predicate)
    {
        return (PartitionMutableBag<T>) super.partition(predicate);
    }

    @Override
    public <P> PartitionMutableBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (PartitionMutableBag<T>) super.partitionWith(predicate, parameter);
    }

    @Override
    public MutableBooleanBag collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return (MutableBooleanBag) super.collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteBag collectByte(ByteFunction<? super T> byteFunction)
    {
        return (MutableByteBag) super.collectByte(byteFunction);
    }

    @Override
    public MutableCharBag collectChar(CharFunction<? super T> charFunction)
    {
        return (MutableCharBag) super.collectChar(charFunction);
    }

    @Override
    public MutableDoubleBag collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return (MutableDoubleBag) super.collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatBag collectFloat(FloatFunction<? super T> floatFunction)
    {
        return (MutableFloatBag) super.collectFloat(floatFunction);
    }

    @Override
    public MutableIntBag collectInt(IntFunction<? super T> intFunction)
    {
        return (MutableIntBag) super.collectInt(intFunction);
    }

    @Override
    public MutableLongBag collectLong(LongFunction<? super T> longFunction)
    {
        return (MutableLongBag) super.collectLong(longFunction);
    }

    @Override
    public MutableShortBag collectShort(ShortFunction<? super T> shortFunction)
    {
        return (MutableShortBag) super.collectShort(shortFunction);
    }

    @Override
    public <S> MutableBag<S> selectInstancesOf(Class<S> clazz)
    {
        return (MutableBag<S>) super.selectInstancesOf(clazz);
    }

    @Override
    public <V> MutableBag<V> collect(Function<? super T, ? extends V> function)
    {
        return (MutableBag<V>) super.<V>collect(function);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public MutableSet<Pair<T, Integer>> zipWithIndex()
    {
        return (MutableSet<Pair<T, Integer>>) super.zipWithIndex();
    }

    @Override
    public <P, V> MutableBag<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return (MutableBag<V>) super.collectWith(function, parameter);
    }

    @Override
    public <V> MutableBag<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return (MutableBag<V>) super.<V>collectIf(predicate, function);
    }

    @Override
    public <V> MutableBag<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableBag<V>) super.flatCollect(function);
    }

    @Override
    public <V> MutableBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return (MutableBagMultimap<V, T>) super.<V>groupBy(function);
    }

    @Override
    public <V> MutableBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return (MutableBagMultimap<V, T>) super.groupByEach(function);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> MutableBag<Pair<T, S>> zip(Iterable<S> that)
    {
        return (MutableBag<Pair<T, S>>) super.zip(that);
    }

    @Override
    public MutableBag<T> asUnmodifiable()
    {
        synchronized (this.getLock())
        {
            return UnmodifiableBag.of(this);
        }
    }

    @Override
    public MutableBag<T> asSynchronized()
    {
        return this;
    }

    @Override
    public ImmutableBag<T> toImmutable()
    {
        return Bags.immutable.withAll(this);
    }

    @Override
    public MutableSet<T> selectUnique()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectUnique();
        }
    }

    @Override
    public RichIterable<T> distinctView()
    {
        synchronized (this.getLock())
        {
            return SynchronizedRichIterable.of(this.getDelegate().distinctView(), this.lock);
        }
    }
}
