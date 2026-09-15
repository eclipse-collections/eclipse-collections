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
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MultiReaderBag;
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
import org.eclipse.collections.impl.collection.mutable.AbstractMultiReaderMutableCollection;
import org.eclipse.collections.impl.factory.Iterables;

/**
 * MultiReaderHashBag provides a thread-safe wrapper around a HashBag, using a ReentrantReadWriteLock. In order to
 * provide true thread-safety, MultiReaderHashBag does <em>not</em> implement {@code iterator()} as this method requires an external lock
 * to be taken to provide thread-safe iteration. All of these methods are available however, if you use the
 * {@code withReadLockAndDelegate()} or {@code withWriteLockAndDelegate()} methods. Both of these methods take a parameter of type
 * {@code Procedure<MutableBag>}, and a wrapped version of the underlying HashBag is returned. This wrapper guarantees that
 * no external pointer can ever reference the underlying HashBag outside a locked procedure. In the case of the
 * read lock method, an Unmodifiable version of the collection is offered, which will throw UnsupportedOperationExceptions
 * on any write methods like add or remove.
 */
public final class MultiReaderHashBag<T>
        extends AbstractMultiReaderMutableCollection<T>
        implements Externalizable, MultiReaderBag<T>
{
    private static final long serialVersionUID = 1L;

    private HashBag<T> delegate;

    /**
     * @deprecated Empty default constructor used for serialization.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public MultiReaderHashBag()
    {
        // For Externalizable use only
    }

    private MultiReaderHashBag(HashBag<T> newDelegate)
    {
        this(newDelegate, new ReentrantReadWriteLock());
    }

    private MultiReaderHashBag(HashBag<T> newDelegate, ReadWriteLock newLock)
    {
        this.lock = newLock;
        this.lockWrapper = new ReadWriteLockWrapper(newLock);
        this.delegate = newDelegate;
    }

    public static <T> MultiReaderHashBag<T> newBag()
    {
        return new MultiReaderHashBag<>(HashBag.newBag());
    }

    public static <T> MultiReaderHashBag<T> newBag(int capacity)
    {
        return new MultiReaderHashBag<>(HashBag.newBag(capacity));
    }

    public static <T> MultiReaderHashBag<T> newBag(Iterable<T> iterable)
    {
        return new MultiReaderHashBag<>(HashBag.newBag(iterable));
    }

    public static <T> MultiReaderHashBag<T> newBagWith(T... elements)
    {
        return new MultiReaderHashBag<>(HashBag.newBagWith(elements));
    }

    /**
     * Rehashes every element in the set into a new backing table of the smallest possible size and eliminating removed sentinels.
     *
     * @since 12.0
     */
    public void trimToSize()
    {
        this.delegate.trimToSize();
    }

    @Override
    protected MutableBag<T> getDelegate()
    {
        return this.delegate;
    }

    UntouchableMutableBag<T> asReadUntouchable()
    {
        return new UntouchableMutableBag<>(this.delegate.asUnmodifiable());
    }

    UntouchableMutableBag<T> asWriteUntouchable()
    {
        return new UntouchableMutableBag<>(this.delegate);
    }

    @Override
    public void withReadLockAndDelegate(Procedure<? super MutableBag<T>> procedure)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            UntouchableMutableBag<T> bag = this.asReadUntouchable();
            procedure.value(bag);
            bag.becomeUseless();
        }
    }

    @Override
    public void withWriteLockAndDelegate(Procedure<? super MutableBag<T>> procedure)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            UntouchableMutableBag<T> bag = this.asWriteUntouchable();
            procedure.value(bag);
            bag.becomeUseless();
        }
    }

    @Override
    public MutableBag<T> asSynchronized()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return SynchronizedBag.of(this);
        }
    }

    @Override
    public MutableBag<T> asUnmodifiable()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return UnmodifiableBag.of(this);
        }
    }

    @Override
    public ImmutableBag<T> toImmutable()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return Bags.immutable.withAll(this.delegate);
        }
    }

    @Override
    public int addOccurrences(T item, int occurrences)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.delegate.addOccurrences(item, occurrences);
        }
    }

    @Override
    public boolean removeOccurrences(Object item, int occurrences)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.delegate.removeOccurrences(item, occurrences);
        }
    }

    @Override
    public boolean setOccurrences(T item, int occurrences)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.delegate.setOccurrences(item, occurrences);
        }
    }

    @Override
    public int occurrencesOf(Object item)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.occurrencesOf(item);
        }
    }

    @Override
    public int sizeDistinct()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.sizeDistinct();
        }
    }

    @Override
    public <V> MutableBag<V> collect(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collect(function);
        }
    }

    @Override
    public MutableBooleanBag collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectBoolean(booleanFunction);
        }
    }

    @Override
    public MutableByteBag collectByte(ByteFunction<? super T> byteFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectByte(byteFunction);
        }
    }

    @Override
    public MutableCharBag collectChar(CharFunction<? super T> charFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectChar(charFunction);
        }
    }

    @Override
    public MutableDoubleBag collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectDouble(doubleFunction);
        }
    }

    @Override
    public MutableFloatBag collectFloat(FloatFunction<? super T> floatFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectFloat(floatFunction);
        }
    }

    @Override
    public MutableIntBag collectInt(IntFunction<? super T> intFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectInt(intFunction);
        }
    }

    @Override
    public MutableLongBag collectLong(LongFunction<? super T> longFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectLong(longFunction);
        }
    }

    @Override
    public MutableShortBag collectShort(ShortFunction<? super T> shortFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectShort(shortFunction);
        }
    }

    @Override
    public <V> MutableBag<V> flatCollect(
            Function<? super T, ? extends Iterable<V>> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.flatCollect(function);
        }
    }

    @Override
    public MutableList<ObjectIntPair<T>> topOccurrences(int count)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.topOccurrences(count);
        }
    }

    @Override
    public MutableList<ObjectIntPair<T>> bottomOccurrences(int count)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.bottomOccurrences(count);
        }
    }

    @Override
    public <V> MutableBag<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectIf(predicate, function);
        }
    }

    @Override
    public <V> MutableBag<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.collectWithOccurrences(function, Bags.mutable.empty());
        }
    }

    @Override
    public <V, R extends Collection<V>> R collectWithOccurrences(
            ObjectIntToObjectFunction<? super T, ? extends V> function,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.forEachWithOccurrences((each, occurrences) -> target.add(function.valueOf(each, occurrences)));
            return target;
        }
    }

    @Override
    public <P, V> MutableBag<V> collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectWith(function, parameter);
        }
    }

    @Override
    public MultiReaderBag<T> newEmpty()
    {
        return MultiReaderHashBag.newBag();
    }

    @Override
    public MutableBag<T> reject(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.reject(predicate);
        }
    }

    @Override
    public <P> MutableBag<T> rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.rejectWith(predicate, parameter);
        }
    }

    @Override
    public MultiReaderBag<T> tap(Procedure<? super T> procedure)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.forEach(procedure);
            return this;
        }
    }

    @Override
    public MutableBag<T> select(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.select(predicate);
        }
    }

    @Override
    public <P> MutableBag<T> selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.selectWith(predicate, parameter);
        }
    }

    @Override
    public MutableBag<T> selectByOccurrences(IntPredicate predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.selectByOccurrences(predicate);
        }
    }

    @Override
    public <S> MutableBag<S> selectInstancesOf(Class<S> clazz)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.selectInstancesOf(clazz);
        }
    }

    @Override
    public PartitionMutableBag<T> partition(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.partition(predicate);
        }
    }

    @Override
    public <P> PartitionMutableBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.partitionWith(predicate, parameter);
        }
    }

    @Override
    public MutableMap<T, Integer> toMapOfItemToCount()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.toMapOfItemToCount();
        }
    }

    @Override
    public String toStringOfItemToCount()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.toStringOfItemToCount();
        }
    }

    @Override
    public <V> MutableBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.groupBy(function);
        }
    }

    @Override
    public <V> MutableBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.groupByEach(function);
        }
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.groupByUniqueKey(function);
        }
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> MutableBag<Pair<T, S>> zip(Iterable<S> that)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.zip(that);
        }
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public MutableSet<Pair<T, Integer>> zipWithIndex()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.zipWithIndex();
        }
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.chunk(size);
        }
    }

    @Override
    public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.anySatisfyWithOccurrences(predicate);
        }
    }

    @Override
    public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.allSatisfyWithOccurrences(predicate);
        }
    }

    @Override
    public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.noneSatisfyWithOccurrences(predicate);
        }
    }

    @Override
    public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.detectWithOccurrences(predicate);
        }
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
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> procedure)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.delegate.forEachWithOccurrences(procedure);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.equals(o);
        }
    }

    @Override
    public int hashCode()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.hashCode();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.delegate);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.delegate = (HashBag<T>) in.readObject();
        this.lock = new ReentrantReadWriteLock();
        this.lockWrapper = new ReadWriteLockWrapper(this.lock);
    }

    @Override
    public MutableSet<T> selectUnique()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().selectUnique();
        }
    }

    @Override
    public RichIterable<T> distinctView()
    {
        throw new UnsupportedOperationException(
                "distinctView is not supported directly on MultiReader collections.  "
                        + "If you would like to use such as view, you must either use withReadLockAndDelegate() or withWriteLockAndDelegate().");
    }

    //Exposed for testing

    static final class UntouchableMutableBag<T>
            extends UntouchableMutableCollection<T>
            implements MutableBag<T>
    {
        private final MutableList<UntouchableIterator<T>> requestedIterators = Iterables.mList();

        private UntouchableMutableBag(MutableBag<T> newDelegate)
        {
            super(newDelegate);
        }

        @Override
        protected MutableBag<T> getDelegate()
        {
            return (MutableBag<T>) this.delegate;
        }

        public void becomeUseless()
        {
            this.delegate = null;
            this.requestedIterators.each(UntouchableIterator::becomeUseless);
        }

        @Override
        public MutableBag<T> with(T element)
        {
            this.add(element);
            return this;
        }

        @Override
        public MutableBag<T> without(T element)
        {
            this.remove(element);
            return this;
        }

        @Override
        public MutableBag<T> withAll(Iterable<? extends T> elements)
        {
            this.addAllIterable(elements);
            return this;
        }

        @Override
        public MutableBag<T> withoutAll(Iterable<? extends T> elements)
        {
            this.removeAllIterable(elements);
            return this;
        }

        @Override
        public MutableBag<T> asSynchronized()
        {
            throw new UnsupportedOperationException("cannot wrap an UntouchableMutableBag");
        }

        @Override
        public MutableBag<T> asUnmodifiable()
        {
            throw new UnsupportedOperationException("cannot wrap an UntouchableMutableBag");
        }

        @Override
        public RichIterable<T> distinctView()
        {
            throw new UnsupportedOperationException("cannot wrap an UntouchableMutableBag");
        }

        @Override
        public ImmutableBag<T> toImmutable()
        {
            return Bags.immutable.withAll(this.getDelegate());
        }

        @Override
        public Iterator<T> iterator()
        {
            UntouchableIterator<T> iterator = new UntouchableIterator<>(this.delegate.iterator());
            this.requestedIterators.add(iterator);
            return iterator;
        }

        @Override
        public int addOccurrences(T item, int occurrences)
        {
            return this.getDelegate().addOccurrences(item, occurrences);
        }

        @Override
        public boolean removeOccurrences(Object item, int occurrences)
        {
            return this.getDelegate().removeOccurrences(item, occurrences);
        }

        @Override
        public MutableBag<T> withOccurrences(T item, int occurrences)
        {
            this.addOccurrences(item, occurrences);
            return this;
        }

        @Override
        public MutableBag<T> withoutOccurrences(Object item, int occurrences)
        {
            this.removeOccurrences(item, occurrences);
            return this;
        }

        @Override
        public boolean setOccurrences(T item, int occurrences)
        {
            return this.getDelegate().setOccurrences(item, occurrences);
        }

        @Override
        public int occurrencesOf(Object item)
        {
            return this.getDelegate().occurrencesOf(item);
        }

        @Override
        public int sizeDistinct()
        {
            return this.getDelegate().sizeDistinct();
        }

        @Override
        public <V> MutableBag<V> collect(Function<? super T, ? extends V> function)
        {
            return this.getDelegate().collect(function);
        }

        @Override
        public MutableBooleanBag collectBoolean(BooleanFunction<? super T> booleanFunction)
        {
            return this.getDelegate().collectBoolean(booleanFunction);
        }

        @Override
        public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
        {
            return this.getDelegate().anySatisfyWithOccurrences(predicate);
        }

        @Override
        public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
        {
            return this.getDelegate().allSatisfyWithOccurrences(predicate);
        }

        @Override
        public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
        {
            return this.getDelegate().noneSatisfyWithOccurrences(predicate);
        }

        @Override
        public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
        {
            return this.getDelegate().detectWithOccurrences(predicate);
        }

        @Override
        public MutableByteBag collectByte(ByteFunction<? super T> byteFunction)
        {
            return this.getDelegate().collectByte(byteFunction);
        }

        @Override
        public MutableCharBag collectChar(CharFunction<? super T> charFunction)
        {
            return this.getDelegate().collectChar(charFunction);
        }

        @Override
        public MutableDoubleBag collectDouble(DoubleFunction<? super T> doubleFunction)
        {
            return this.getDelegate().collectDouble(doubleFunction);
        }

        @Override
        public MutableFloatBag collectFloat(FloatFunction<? super T> floatFunction)
        {
            return this.getDelegate().collectFloat(floatFunction);
        }

        @Override
        public MutableIntBag collectInt(IntFunction<? super T> intFunction)
        {
            return this.getDelegate().collectInt(intFunction);
        }

        @Override
        public MutableLongBag collectLong(LongFunction<? super T> longFunction)
        {
            return this.getDelegate().collectLong(longFunction);
        }

        @Override
        public MutableShortBag collectShort(ShortFunction<? super T> shortFunction)
        {
            return this.getDelegate().collectShort(shortFunction);
        }

        @Override
        public <V> MutableBag<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
        {
            return this.getDelegate().flatCollect(function);
        }

        @Override
        public MutableList<ObjectIntPair<T>> topOccurrences(int count)
        {
            return this.getDelegate().topOccurrences(count);
        }

        @Override
        public MutableList<ObjectIntPair<T>> bottomOccurrences(int count)
        {
            return this.getDelegate().bottomOccurrences(count);
        }

        @Override
        public <V> MutableBag<V> collectIf(
                Predicate<? super T> predicate,
                Function<? super T, ? extends V> function)
        {
            return this.getDelegate().collectIf(predicate, function);
        }

        @Override
        public <V> MutableBag<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function)
        {
            return this.getDelegate().collectWithOccurrences(function);
        }

        @Override
        public <V, R extends Collection<V>> R collectWithOccurrences(
                ObjectIntToObjectFunction<? super T, ? extends V> function,
                R target)
        {
            return this.getDelegate().collectWithOccurrences(function, target);
        }

        @Override
        public <P, V> MutableBag<V> collectWith(
                Function2<? super T, ? super P, ? extends V> function,
                P parameter)
        {
            return this.getDelegate().collectWith(function, parameter);
        }

        @Override
        public <V> MutableBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
        {
            return this.getDelegate().groupBy(function);
        }

        @Override
        public <V> MutableBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
        {
            return this.getDelegate().groupByEach(function);
        }

        @Override
        public MutableBag<T> newEmpty()
        {
            return this.getDelegate().newEmpty();
        }

        @Override
        public MutableBag<T> reject(Predicate<? super T> predicate)
        {
            return this.getDelegate().reject(predicate);
        }

        @Override
        public <P> MutableBag<T> rejectWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.getDelegate().rejectWith(predicate, parameter);
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
            return this.getDelegate().select(predicate);
        }

        @Override
        public <P> MutableBag<T> selectWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.getDelegate().selectWith(predicate, parameter);
        }

        @Override
        public MutableBag<T> selectByOccurrences(IntPredicate predicate)
        {
            return this.getDelegate().selectByOccurrences(predicate);
        }

        @Override
        public <S> MutableBag<S> selectInstancesOf(Class<S> clazz)
        {
            return this.getDelegate().selectInstancesOf(clazz);
        }

        @Override
        public void forEachWithOccurrences(ObjectIntProcedure<? super T> procedure)
        {
            this.getDelegate().forEachWithOccurrences(procedure);
        }

        @Override
        public PartitionMutableBag<T> partition(Predicate<? super T> predicate)
        {
            return this.getDelegate().partition(predicate);
        }

        @Override
        public <P> PartitionMutableBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
        {
            return this.getDelegate().partitionWith(predicate, parameter);
        }

        /**
         * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
         */
        @Override
        @Deprecated
        public <S> MutableBag<Pair<T, S>> zip(Iterable<S> that)
        {
            return this.getDelegate().zip(that);
        }

        /**
         * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
         */
        @Override
        @Deprecated
        public MutableSet<Pair<T, Integer>> zipWithIndex()
        {
            return this.getDelegate().zipWithIndex();
        }

        @Override
        public MutableMap<T, Integer> toMapOfItemToCount()
        {
            return this.getDelegate().toMapOfItemToCount();
        }

        @Override
        public String toStringOfItemToCount()
        {
            return this.getDelegate().toStringOfItemToCount();
        }

        @Override
        public MutableSet<T> selectUnique()
        {
            return this.getDelegate().selectUnique();
        }
    }

    private static final class UntouchableIterator<T>
            implements Iterator<T>
    {
        private Iterator<T> delegate;

        private UntouchableIterator(Iterator<T> newDelegate)
        {
            this.delegate = newDelegate;
        }

        @Override
        public boolean hasNext()
        {
            return this.delegate.hasNext();
        }

        @Override
        public T next()
        {
            return this.delegate.next();
        }

        @Override
        public void remove()
        {
            this.delegate.remove();
        }

        public void becomeUseless()
        {
            this.delegate = null;
        }
    }
}
