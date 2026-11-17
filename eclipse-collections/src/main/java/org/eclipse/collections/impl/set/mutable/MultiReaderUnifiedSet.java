/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MultiReaderSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.ParallelUnsortedSetIterable;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.UnsortedSetIterable;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.set.primitive.MutableByteSet;
import org.eclipse.collections.api.set.primitive.MutableCharSet;
import org.eclipse.collections.api.set.primitive.MutableDoubleSet;
import org.eclipse.collections.api.set.primitive.MutableFloatSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.api.set.primitive.MutableShortSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.AbstractMultiReaderMutableCollection;
import org.eclipse.collections.impl.lazy.parallel.set.MultiReaderParallelUnsortedSetIterable;

import static org.eclipse.collections.impl.factory.Iterables.mList;

/**
 * MultiReadUnifiedSet provides a thread-safe wrapper around a UnifiedSet, using a ReentrantReadWriteLock. In order to
 * provide true thread-safety, MultiReaderUnifiedSet does <em>not</em> implement {@code iterator()} as this method requires an external
 * lock to be taken to provide thread-safe iteration. You can use an {@code iterator()} if you use the
 * {@code withReadLockAndDelegate()} or {@code withWriteLockAndDelegate()} methods. Both of these methods take a parameter of type
 * {@code Procedure<MutableSet>}, and a wrapped version of the underlying Unified is returned. This wrapper guarantees that no
 * external pointer can ever reference the underlying UnifiedSet outside a locked procedure. In the case of the read
 * lock method, an Unmodifiable version of the collection is offered, which will throw UnsupportedOperationExceptions on
 * any write methods like add or remove.
 */
public final class MultiReaderUnifiedSet<T>
        extends AbstractMultiReaderMutableCollection<T>
        implements RandomAccess, Externalizable, MultiReaderSet<T>
{
    private static final long serialVersionUID = 1L;

    private MutableSet<T> delegate;

    /**
     * @deprecated Empty default constructor used for serialization.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public MultiReaderUnifiedSet()
    {
        // For Externalizable use only
    }

    private MultiReaderUnifiedSet(MutableSet<T> newDelegate)
    {
        this(newDelegate, new ReentrantReadWriteLock());
    }

    private MultiReaderUnifiedSet(MutableSet<T> newDelegate, ReadWriteLock newLock)
    {
        this.lock = newLock;
        this.lockWrapper = new ReadWriteLockWrapper(newLock);
        this.delegate = newDelegate;
    }

    public static <T> MultiReaderUnifiedSet<T> newSet()
    {
        return new MultiReaderUnifiedSet<>(Sets.mutable.empty());
    }

    public static <T> MultiReaderUnifiedSet<T> newSet(int capacity)
    {
        return new MultiReaderUnifiedSet<>(UnifiedSet.newSet(capacity));
    }

    public static <T> MultiReaderUnifiedSet<T> newSet(Iterable<T> iterable)
    {
        return new MultiReaderUnifiedSet<>(UnifiedSet.newSet(iterable));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> MultiReaderUnifiedSet<T> newSetWith(T... elements)
    {
        return new MultiReaderUnifiedSet<>(UnifiedSet.newSetWith(elements));
    }

    @Override
    protected MutableSet<T> getDelegate()
    {
        return this.delegate;
    }

    // Exposed for testing

    UntouchableMutableSet<T> asReadUntouchable()
    {
        return new UntouchableMutableSet<>(this.delegate.asUnmodifiable());
    }

    // Exposed for testing

    UntouchableMutableSet<T> asWriteUntouchable()
    {
        return new UntouchableMutableSet<>(this.delegate);
    }

    @Override
    public void withReadLockAndDelegate(Procedure<? super MutableSet<T>> procedure)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            UntouchableMutableSet<T> untouchableSet = this.asReadUntouchable();
            procedure.value(untouchableSet);
            untouchableSet.becomeUseless();
        }
    }

    @Override
    public void withWriteLockAndDelegate(Procedure<? super MutableSet<T>> procedure)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            UntouchableMutableSet<T> untouchableSet = this.asWriteUntouchable();
            procedure.value(untouchableSet);
            untouchableSet.becomeUseless();
        }
    }

    @Override
    public MutableSet<T> asSynchronized()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return SynchronizedMutableSet.of(this);
        }
    }

    @Override
    public ImmutableSet<T> toImmutable()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return Sets.immutable.withAll(this.delegate);
        }
    }

    @Override
    public MutableSet<T> asUnmodifiable()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return UnmodifiableMutableSet.of(this);
        }
    }

    @Override
    public MultiReaderSet<T> clone()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return new MultiReaderUnifiedSet<>(this.delegate.clone());
        }
    }

    @Override
    public <V> MutableSet<V> collect(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collect(function);
        }
    }

    @Override
    public MutableBooleanSet collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectBoolean(booleanFunction);
        }
    }

    @Override
    public MutableByteSet collectByte(ByteFunction<? super T> byteFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectByte(byteFunction);
        }
    }

    @Override
    public MutableCharSet collectChar(CharFunction<? super T> charFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectChar(charFunction);
        }
    }

    @Override
    public MutableDoubleSet collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectDouble(doubleFunction);
        }
    }

    @Override
    public MutableFloatSet collectFloat(FloatFunction<? super T> floatFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectFloat(floatFunction);
        }
    }

    @Override
    public MutableIntSet collectInt(IntFunction<? super T> intFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectInt(intFunction);
        }
    }

    @Override
    public MutableLongSet collectLong(LongFunction<? super T> longFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectLong(longFunction);
        }
    }

    @Override
    public MutableShortSet collectShort(ShortFunction<? super T> shortFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectShort(shortFunction);
        }
    }

    @Override
    public <V> MutableSet<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.flatCollect(function);
        }
    }

    @Override
    public <V> MutableSet<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectIf(predicate, function);
        }
    }

    @Override
    public <P, V> MutableSet<V> collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.collectWith(function, parameter);
        }
    }

    @Override
    public MultiReaderSet<T> newEmpty()
    {
        return MultiReaderUnifiedSet.newSet();
    }

    @Override
    public MutableSet<T> reject(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.reject(predicate);
        }
    }

    @Override
    public <P> MutableSet<T> rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.rejectWith(predicate, parameter);
        }
    }

    @Override
    public MultiReaderSet<T> tap(Procedure<? super T> procedure)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.forEach(procedure);
            return this;
        }
    }

    @Override
    public MutableSet<T> select(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.select(predicate);
        }
    }

    @Override
    public <P> MutableSet<T> selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.selectWith(predicate, parameter);
        }
    }

    @Override
    public PartitionMutableSet<T> partition(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.partition(predicate);
        }
    }

    @Override
    public <P> PartitionMutableSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.partitionWith(predicate, parameter);
        }
    }

    @Override
    public <S> MutableSet<S> selectInstancesOf(Class<S> clazz)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.selectInstancesOf(clazz);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this || o == this.delegate)
        {
            return true;
        }

        if (!(o instanceof Set))
        {
            return false;
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
        this.delegate = (MutableSet<T>) in.readObject();
        this.lock = new ReentrantReadWriteLock();
        this.lockWrapper = new ReadWriteLockWrapper(this.lock);
    }

    // Exposed for testing

    static final class UntouchableMutableSet<T>
            extends UntouchableMutableCollection<T>
            implements MutableSet<T>
    {
        private final MutableList<UntouchableIterator<T>> requestedIterators = mList();

        private UntouchableMutableSet(MutableSet<T> newDelegate)
        {
            super(newDelegate);
        }

        @Override
        protected MutableSet<T> getDelegate()
        {
            return (MutableSet<T>) this.delegate;
        }

        public void becomeUseless()
        {
            this.delegate = null;
            this.requestedIterators.each(UntouchableIterator::becomeUseless);
        }

        /**
         * This must be implemented this way to guarantee a reference to the delegate doesn't escape.
         */
        @Override
        public MutableSet<T> with(T element)
        {
            this.add(element);
            return this;
        }

        @Override
        public MutableSet<T> without(T element)
        {
            this.remove(element);
            return this;
        }

        @Override
        public MutableSet<T> withAll(Iterable<? extends T> elements)
        {
            this.addAllIterable(elements);
            return this;
        }

        @Override
        public MutableSet<T> withoutAll(Iterable<? extends T> elements)
        {
            this.removeAllIterable(elements);
            return this;
        }

        @Override
        public MutableSet<T> asSynchronized()
        {
            throw new UnsupportedOperationException("Cannot call asSynchronized() on " + this.getClass().getSimpleName());
        }

        @Override
        public MutableSet<T> asUnmodifiable()
        {
            throw new UnsupportedOperationException("Cannot call asUnmodifiable() on " + this.getClass().getSimpleName());
        }

        @Override
        public ImmutableSet<T> toImmutable()
        {
            return Sets.immutable.withAll(this.getDelegate());
        }

        @Override
        public MutableSet<T> clone()
        {
            return this.getDelegate().clone();
        }

        @Override
        public <V> MutableSet<V> collect(Function<? super T, ? extends V> function)
        {
            return this.getDelegate().collect(function);
        }

        @Override
        public MutableBooleanSet collectBoolean(BooleanFunction<? super T> booleanFunction)
        {
            return this.getDelegate().collectBoolean(booleanFunction);
        }

        @Override
        public MutableByteSet collectByte(ByteFunction<? super T> byteFunction)
        {
            return this.getDelegate().collectByte(byteFunction);
        }

        @Override
        public MutableCharSet collectChar(CharFunction<? super T> charFunction)
        {
            return this.getDelegate().collectChar(charFunction);
        }

        @Override
        public MutableDoubleSet collectDouble(DoubleFunction<? super T> doubleFunction)
        {
            return this.getDelegate().collectDouble(doubleFunction);
        }

        @Override
        public MutableFloatSet collectFloat(FloatFunction<? super T> floatFunction)
        {
            return this.getDelegate().collectFloat(floatFunction);
        }

        @Override
        public MutableIntSet collectInt(IntFunction<? super T> intFunction)
        {
            return this.getDelegate().collectInt(intFunction);
        }

        @Override
        public MutableLongSet collectLong(LongFunction<? super T> longFunction)
        {
            return this.getDelegate().collectLong(longFunction);
        }

        @Override
        public MutableShortSet collectShort(ShortFunction<? super T> shortFunction)
        {
            return this.getDelegate().collectShort(shortFunction);
        }

        @Override
        public <V> MutableSet<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
        {
            return this.getDelegate().flatCollect(function);
        }

        @Override
        public <V> MutableSet<V> collectIf(
                Predicate<? super T> predicate,
                Function<? super T, ? extends V> function)
        {
            return this.getDelegate().collectIf(predicate, function);
        }

        @Override
        public <P, V> MutableSet<V> collectWith(
                Function2<? super T, ? super P, ? extends V> function,
                P parameter)
        {
            return this.getDelegate().collectWith(function, parameter);
        }

        @Override
        public <V> MutableSetMultimap<V, T> groupBy(
                Function<? super T, ? extends V> function)
        {
            return this.getDelegate().groupBy(function);
        }

        @Override
        public <V> MutableSetMultimap<V, T> groupByEach(
                Function<? super T, ? extends Iterable<V>> function)
        {
            return this.getDelegate().groupByEach(function);
        }

        @Override
        public MutableSet<T> newEmpty()
        {
            return this.getDelegate().newEmpty();
        }

        @Override
        public MutableSet<T> reject(Predicate<? super T> predicate)
        {
            return this.getDelegate().reject(predicate);
        }

        @Override
        public <P> MutableSet<T> rejectWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.getDelegate().rejectWith(predicate, parameter);
        }

        @Override
        public MutableSet<T> tap(Procedure<? super T> procedure)
        {
            this.forEach(procedure);
            return this;
        }

        @Override
        public MutableSet<T> select(Predicate<? super T> predicate)
        {
            return this.getDelegate().select(predicate);
        }

        @Override
        public <P> MutableSet<T> selectWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.getDelegate().selectWith(predicate, parameter);
        }

        @Override
        public PartitionMutableSet<T> partition(Predicate<? super T> predicate)
        {
            return this.getDelegate().partition(predicate);
        }

        @Override
        public <P> PartitionMutableSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
        {
            return this.getDelegate().partitionWith(predicate, parameter);
        }

        @Override
        public <S> MutableSet<S> selectInstancesOf(Class<S> clazz)
        {
            return this.getDelegate().selectInstancesOf(clazz);
        }

        @Override
        public Iterator<T> iterator()
        {
            UntouchableIterator<T> iterator = new UntouchableIterator<>(this.delegate.iterator());
            this.requestedIterators.add(iterator);
            return iterator;
        }

        /**
         * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
         */
        @Override
        @Deprecated
        public <S> MutableSet<Pair<T, S>> zip(Iterable<S> that)
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
        public MutableSet<T> union(SetIterable<? extends T> set)
        {
            return this.getDelegate().union(set);
        }

        @Override
        public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
        {
            return this.getDelegate().unionInto(set, targetSet);
        }

        @Override
        public MutableSet<T> intersect(SetIterable<? extends T> set)
        {
            return this.getDelegate().intersect(set);
        }

        @Override
        public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
        {
            return this.getDelegate().intersectInto(set, targetSet);
        }

        @Override
        public MutableSet<T> difference(SetIterable<? extends T> subtrahendSet)
        {
            return this.getDelegate().difference(subtrahendSet);
        }

        @Override
        public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
        {
            return this.getDelegate().differenceInto(subtrahendSet, targetSet);
        }

        @Override
        public MutableSet<T> symmetricDifference(SetIterable<? extends T> setB)
        {
            return this.getDelegate().symmetricDifference(setB);
        }

        @Override
        public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
        {
            return this.getDelegate().symmetricDifferenceInto(set, targetSet);
        }

        @Override
        public boolean isSubsetOf(SetIterable<? extends T> candidateSuperset)
        {
            return this.getDelegate().isSubsetOf(candidateSuperset);
        }

        @Override
        public boolean isProperSubsetOf(SetIterable<? extends T> candidateSuperset)
        {
            return this.getDelegate().isProperSubsetOf(candidateSuperset);
        }

        @Override
        public MutableSet<UnsortedSetIterable<T>> powerSet()
        {
            return this.getDelegate().powerSet();
        }

        @Override
        public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
        {
            return this.getDelegate().cartesianProduct(set);
        }

        @Override
        public ParallelUnsortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
        {
            return this.getDelegate().asParallel(executorService, batchSize);
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

    @Override
    public <V> MutableSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.groupBy(function);
        }
    }

    @Override
    public <V> MutableSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
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
    public <S> MutableSet<Pair<T, S>> zip(Iterable<S> that)
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
    public MutableSet<T> union(SetIterable<? extends T> set)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.union(set);
        }
    }

    @Override
    public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.unionInto(set, targetSet);
        }
    }

    @Override
    public MutableSet<T> intersect(SetIterable<? extends T> set)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.intersect(set);
        }
    }

    @Override
    public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.intersectInto(set, targetSet);
        }
    }

    @Override
    public MutableSet<T> difference(SetIterable<? extends T> subtrahendSet)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.difference(subtrahendSet);
        }
    }

    @Override
    public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.differenceInto(subtrahendSet, targetSet);
        }
    }

    @Override
    public MutableSet<T> symmetricDifference(SetIterable<? extends T> setB)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.symmetricDifference(setB);
        }
    }

    @Override
    public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.symmetricDifferenceInto(set, targetSet);
        }
    }

    @Override
    public boolean isSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.isSubsetOf(candidateSuperset);
        }
    }

    @Override
    public boolean isProperSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.isProperSubsetOf(candidateSuperset);
        }
    }

    @Override
    public MutableSet<UnsortedSetIterable<T>> powerSet()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.powerSet();
        }
    }

    @Override
    public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.delegate.cartesianProduct(set);
        }
    }

    @Override
    public ParallelUnsortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return new MultiReaderParallelUnsortedSetIterable<>(this.delegate.asParallel(executorService, batchSize), this.lock);
        }
    }
}
