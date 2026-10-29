/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;

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
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionMutableBag;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.collection.mutable.AbstractUnmodifiableMutableCollection;

/**
 * An unmodifiable view of a bag.
 *
 * @see MutableBag#asUnmodifiable()
 * @since 1.0
 */
public class UnmodifiableBag<T>
        extends AbstractUnmodifiableMutableCollection<T>
        implements MutableBag<T>, Serializable
{
    protected UnmodifiableBag(MutableBag<? extends T> mutableBag)
    {
        super(mutableBag);
    }

    /**
     * This method will take a MutableBag and wrap it directly in a UnmodifiableBag.
     */
    public static <E, B extends MutableBag<E>> UnmodifiableBag<E> of(B bag)
    {
        if (bag == null)
        {
            throw new IllegalArgumentException("cannot create an UnmodifiableBag for null");
        }
        return new UnmodifiableBag<>(bag);
    }

    protected MutableBag<T> getMutableBag()
    {
        return (MutableBag<T>) this.getMutableCollection();
    }

    @Override
    public MutableBag<T> asUnmodifiable()
    {
        return this;
    }

    @Override
    public MutableBag<T> asSynchronized()
    {
        return SynchronizedBag.of(this);
    }

    @Override
    public ImmutableBag<T> toImmutable()
    {
        return Bags.immutable.withAll(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.getMutableBag().equals(obj);
    }

    @Override
    public int hashCode()
    {
        return this.getMutableBag().hashCode();
    }

    @Override
    public String toStringOfItemToCount()
    {
        return this.getMutableBag().toStringOfItemToCount();
    }

    @Override
    public MutableBag<T> newEmpty()
    {
        return this.getMutableBag().newEmpty();
    }

    @Override
    public MutableBag<T> selectByOccurrences(IntPredicate predicate)
    {
        return this.getMutableBag().selectByOccurrences(predicate);
    }

    @Override
    public MutableBag<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public MutableBag<T> select(Predicate<? super T> predicate)
    {
        return this.getMutableBag().select(predicate);
    }

    @Override
    public <P> MutableBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableBag().selectWith(predicate, parameter);
    }

    @Override
    public MutableBag<T> reject(Predicate<? super T> predicate)
    {
        return this.getMutableBag().reject(predicate);
    }

    @Override
    public <P> MutableBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableBag().rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableBag<T> partition(Predicate<? super T> predicate)
    {
        return this.getMutableBag().partition(predicate);
    }

    @Override
    public <P> PartitionMutableBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableBag().partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableBag<S> selectInstancesOf(Class<S> clazz)
    {
        return this.getMutableBag().selectInstancesOf(clazz);
    }

    @Override
    public <V> MutableBag<V> collect(Function<? super T, ? extends V> function)
    {
        return this.getMutableBag().collect(function);
    }

    @Override
    public MutableBooleanBag collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.getMutableBag().collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteBag collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.getMutableBag().collectByte(byteFunction);
    }

    @Override
    public MutableCharBag collectChar(CharFunction<? super T> charFunction)
    {
        return this.getMutableBag().collectChar(charFunction);
    }

    @Override
    public MutableDoubleBag collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.getMutableBag().collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatBag collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.getMutableBag().collectFloat(floatFunction);
    }

    @Override
    public MutableIntBag collectInt(IntFunction<? super T> intFunction)
    {
        return this.getMutableBag().collectInt(intFunction);
    }

    @Override
    public MutableLongBag collectLong(LongFunction<? super T> longFunction)
    {
        return this.getMutableBag().collectLong(longFunction);
    }

    @Override
    public MutableShortBag collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.getMutableBag().collectShort(shortFunction);
    }

    @Override
    public <V> MutableBag<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getMutableBag().flatCollect(function);
    }

    @Override
    public MutableList<ObjectIntPair<T>> topOccurrences(int count)
    {
        return this.getMutableBag().topOccurrences(count);
    }

    @Override
    public MutableList<ObjectIntPair<T>> bottomOccurrences(int count)
    {
        return this.getMutableBag().bottomOccurrences(count);
    }

    @Override
    public <V> MutableBag<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.getMutableBag().collectWithOccurrences(function, Bags.mutable.empty());
    }

    @Override
    public <P, A> MutableBag<A> collectWith(Function2<? super T, ? super P, ? extends A> function, P parameter)
    {
        return this.getMutableBag().collectWith(function, parameter);
    }

    @Override
    public <V> MutableBag<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return this.getMutableBag().collectIf(predicate, function);
    }

    @Override
    public <V> MutableBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.getMutableBag().groupBy(function);
    }

    @Override
    public <V> MutableBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getMutableBag().groupByEach(function);
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
    public int sizeDistinct()
    {
        return this.getMutableBag().sizeDistinct();
    }

    @Override
    public int occurrencesOf(Object item)
    {
        return this.getMutableBag().occurrencesOf(item);
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.getMutableBag().forEachWithOccurrences(objectIntProcedure);
    }

    @Override
    public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.getMutableBag().anySatisfyWithOccurrences(predicate);
    }

    @Override
    public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.getMutableBag().allSatisfyWithOccurrences(predicate);
    }

    @Override
    public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.getMutableBag().noneSatisfyWithOccurrences(predicate);
    }

    @Override
    public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.getMutableBag().detectWithOccurrences(predicate);
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        return this.getMutableBag().collectWithOccurrences(function, target);
    }

    @Override
    public MutableMap<T, Integer> toMapOfItemToCount()
    {
        return this.getMutableBag().toMapOfItemToCount();
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Deprecated
    @Override
    public <S> MutableBag<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.getMutableBag().zip(that);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Deprecated
    @Override
    public MutableSet<Pair<T, Integer>> zipWithIndex()
    {
        return this.getMutableBag().zipWithIndex();
    }

    @Override
    public MutableBag<T> with(T element)
    {
        throw new UnsupportedOperationException("Cannot call with() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBag<T> without(T element)
    {
        throw new UnsupportedOperationException("Cannot call without() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBag<T> withOccurrences(T element, int occurrences)
    {
        throw new UnsupportedOperationException("Cannot call withOccurrences() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBag<T> withoutOccurrences(T element, int occurrences)
    {
        throw new UnsupportedOperationException("Cannot call withoutOccurrences() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBag<T> withAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableBag<T> withoutAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withoutAll() on " + this.getClass().getSimpleName());
    }

    protected Object writeReplace()
    {
        return new UnmodifiableBagSerializationProxy<>(this.getMutableBag());
    }

    private static class UnmodifiableBagSerializationProxy<T> implements Externalizable
    {
        private static final long serialVersionUID = 1L;

        private MutableBag<T> mutableBag;

        public UnmodifiableBagSerializationProxy()
        {
            // Empty constructor for Externalizable class
        }

        private UnmodifiableBagSerializationProxy(MutableBag<T> bag)
        {
            this.mutableBag = bag;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException
        {
            try
            {
                out.writeObject(this.mutableBag);
            }
            catch (RuntimeException e)
            {
                if (e.getCause() instanceof IOException)
                {
                    throw (IOException) e.getCause();
                }
                throw e;
            }
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
        {
            this.mutableBag = (MutableBag<T>) in.readObject();
        }

        protected Object readResolve()
        {
            return this.mutableBag.asUnmodifiable();
        }
    }

    @Override
    public MutableSet<T> selectUnique()
    {
        return this.getMutableBag().selectUnique();
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        this.forEachWithOccurrences((each, occurrences) -> result.addToValue(
                groupBy.valueOf(each),
                function.intValueOf(each) * (long) occurrences));
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
        return this.getMutableBag().distinctView();
    }
}
