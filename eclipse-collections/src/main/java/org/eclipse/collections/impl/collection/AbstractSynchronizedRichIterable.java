/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection;

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
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.MutableBagIterable;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
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
import org.eclipse.collections.api.list.ImmutableList;
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
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * AbstractSynchronizedRichIterable provides a thread-safe wrapper around RichIterable implementations.
 * All operations are synchronized using the provided lock object to ensure thread-safety for concurrent access.
 * <p>
 * This abstract base class delegates all RichIterable operations to an underlying delegate while
 * synchronizing on a lock object. Subclasses should extend this class to create synchronized versions
 * of specific RichIterable implementations.
 * </p>
 * <p><b>Thread Safety:</b> This class is thread-safe. All operations are synchronized.</p>
 * <p><b>Performance:</b> Synchronization overhead applies to all operations. For better concurrent
 * performance with high contention, consider using concurrent collections instead.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * RichIterable<String> unsafeIterable = FastList.newListWith("a", "b", "c");
 * Object lock = new Object();
 * // Wrap with synchronized version
 * AbstractSynchronizedRichIterable<String> syncIterable = new SynchronizedMutableCollection<>(unsafeIterable, lock);
 *
 * // All operations are now thread-safe
 * syncIterable.forEach(System.out::println);
 * }</pre>
 *
 * @param <T> the type of elements in this iterable
 * @since 1.0
 */
public abstract class AbstractSynchronizedRichIterable<T> implements RichIterable<T>
{
    protected final Object lock;
    protected final RichIterable<T> delegate;

    /**
     * Constructs a new AbstractSynchronizedRichIterable wrapping the specified delegate with the given lock.
     *
     * @param delegate the RichIterable to wrap and synchronize
     * @param lock the object to use for synchronization; if null, this instance is used as the lock
     * @throws IllegalArgumentException if delegate is null
     */
    protected AbstractSynchronizedRichIterable(RichIterable<T> delegate, Object lock)
    {
        if (delegate == null)
        {
            throw new IllegalArgumentException("Cannot create a AbstractSynchronizedRichIterable on a null collection");
        }

        this.delegate = delegate;
        this.lock = lock == null ? this : lock;
    }

    /**
     * Returns the underlying delegate RichIterable.
     *
     * @return the delegate iterable being synchronized
     */
    protected RichIterable<T> getDelegate()
    {
        return this.delegate;
    }

    /**
     * Returns the lock object used for synchronization.
     *
     * @return the lock object
     */
    protected Object getLock()
    {
        return this.lock;
    }

    /**
     * Compares the specified object with this synchronized iterable for equality.
     * Returns true if the specified object is this instance, the delegate instance,
     * or is equal to the delegate according to its equals method.
     * <p>This method is synchronized to ensure thread-safe access to the delegate.</p>
     *
     * @param obj the object to be compared for equality with this synchronized iterable
     * @return {@code true} if the specified object is equal to this synchronized iterable
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this || obj == this.delegate)
        {
            return true;
        }

        synchronized (this.lock)
        {
            return this.delegate.equals(obj);
        }
    }

    @Override
    public int hashCode()
    {
        synchronized (this.lock)
        {
            return this.delegate.hashCode();
        }
    }

    @Override
    public String toString()
    {
        synchronized (this.lock)
        {
            return this.delegate.toString();
        }
    }

    /**
     * Must be called in a synchronized block.
     */
    @Override
    public Iterator<T> iterator()
    {
        return this.delegate.iterator();
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEachWithIndex(objectIntProcedure);
        }
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        synchronized (this.lock)
        {
            this.delegate.forEachWith(procedure, parameter);
        }
    }

    @Override
    public int size()
    {
        synchronized (this.lock)
        {
            return this.delegate.size();
        }
    }

    @Override
    public boolean isEmpty()
    {
        synchronized (this.lock)
        {
            return this.delegate.isEmpty();
        }
    }

    @Override
    public boolean notEmpty()
    {
        synchronized (this.lock)
        {
            return this.delegate.notEmpty();
        }
    }

    @Override
    public T getFirst()
    {
        synchronized (this.lock)
        {
            return this.delegate.getFirst();
        }
    }

    @Override
    public T getLast()
    {
        synchronized (this.lock)
        {
            return this.delegate.getLast();
        }
    }

    @Override
    public boolean contains(Object o)
    {
        synchronized (this.lock)
        {
            return this.delegate.contains(o);
        }
    }

    @Override
    public boolean containsAny(Collection<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAny(source);
        }
    }

    @Override
    public boolean containsNone(Collection<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsNone(source);
        }
    }

    @Override
    public boolean containsAnyIterable(Iterable<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAnyIterable(source);
        }
    }

    @Override
    public boolean containsNoneIterable(Iterable<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsNoneIterable(source);
        }
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAllIterable(source);
        }
    }

    @Override
    public boolean containsAll(Collection<?> coll)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAll(coll);
        }
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAllArguments(elements);
        }
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEach(procedure);
        }
    }

    @Override
    public RichIterable<T> select(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.select(predicate);
        }
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.select(predicate, target);
        }
    }

    @Override
    public <P> RichIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.selectWith(predicate, parameter);
        }
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        synchronized (this.lock)
        {
            return this.delegate.selectWith(predicate, parameter, targetCollection);
        }
    }

    @Override
    public RichIterable<T> reject(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.reject(predicate);
        }
    }

    @Override
    public <P> RichIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.rejectWith(predicate, parameter);
        }
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.reject(predicate, target);
        }
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        synchronized (this.lock)
        {
            return this.delegate.rejectWith(predicate, parameter, targetCollection);
        }
    }

    @Override
    public PartitionIterable<T> partition(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.partition(predicate);
        }
    }

    @Override
    public <P> PartitionIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.partitionWith(predicate, parameter);
        }
    }

    @Override
    public <S> RichIterable<S> selectInstancesOf(Class<S> clazz)
    {
        synchronized (this.lock)
        {
            return this.delegate.selectInstancesOf(clazz);
        }
    }

    @Override
    public <V> RichIterable<V> collect(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.collect(function);
        }
    }

    @Override
    public BooleanIterable collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectBoolean(booleanFunction);
        }
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectBoolean(booleanFunction, target);
        }
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectBoolean(function, target);
        }
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectByte(byteFunction, target);
        }
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectByte(function, target);
        }
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectChar(charFunction, target);
        }
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectChar(function, target);
        }
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectDouble(doubleFunction, target);
        }
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectDouble(function, target);
        }
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectFloat(floatFunction, target);
        }
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectFloat(function, target);
        }
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectInt(intFunction, target);
        }
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectInt(function, target);
        }
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectLong(longFunction, target);
        }
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectLong(function, target);
        }
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectShort(shortFunction, target);
        }
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectShort(function, target);
        }
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collect(function, target);
        }
    }

    @Override
    public <P, A, R extends Collection<A>> R collectWith(
            Function2<? super T, ? super P, ? extends A> function,
            P parameter,
            R targetCollection)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectWith(function, parameter, targetCollection);
        }
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectIf(predicate, function, target);
        }
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollect(function, target);
        }
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.detect(predicate);
        }
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectWith(predicate, parameter);
        }
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectOptional(predicate);
        }
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectWithOptional(predicate, parameter);
        }
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectIfNone(predicate, function);
        }
    }

    @Override
    public <P> T detectWithIfNone(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Function0<? extends T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectWithIfNone(predicate, parameter, function);
        }
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.count(predicate);
        }
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.countWith(predicate, parameter);
        }
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.anySatisfy(predicate);
        }
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.anySatisfyWith(predicate, parameter);
        }
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.allSatisfy(predicate);
        }
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.allSatisfyWith(predicate, parameter);
        }
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.noneSatisfy(predicate);
        }
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.noneSatisfyWith(predicate, parameter);
        }
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectInto(injectedValue, function);
        }
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectIntoInt(injectedValue, function);
        }
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectIntoLong(injectedValue, function);
        }
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectIntoFloat(injectedValue, function);
        }
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectIntoDouble(injectedValue, function);
        }
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.into(target);
        }
    }

    @Override
    public MutableList<T> toList()
    {
        synchronized (this.lock)
        {
            return this.delegate.toList();
        }
    }

    @Override
    public ImmutableList<T> toImmutableList()
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableList();
        }
    }

    @Override
    public MutableList<T> toSortedList()
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedList();
        }
    }

    @Override
    public ImmutableList<T> toImmutableSortedList()
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableSortedList();
        }
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedList(comparator);
        }
    }

    @Override
    public ImmutableList<T> toImmutableSortedList(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableSortedList(comparator);
        }
    }

    @Override
    public <V extends Comparable<? super V>> MutableList<T> toSortedListBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedListBy(function);
        }
    }

    @Override
    public MutableSet<T> toSet()
    {
        synchronized (this.lock)
        {
            return this.delegate.toSet();
        }
    }

    @Override
    public ImmutableSet<T> toImmutableSet()
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableSet();
        }
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedSet();
        }
    }

    @Override
    public ImmutableSortedSet<T> toImmutableSortedSet()
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableSortedSet();
        }
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedSet(comparator);
        }
    }

    @Override
    public ImmutableSortedSet<T> toImmutableSortedSet(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableSortedSet(comparator);
        }
    }

    @Override
    public <V extends Comparable<? super V>> ImmutableSortedSet<T> toImmutableSortedSetBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableSortedSetBy(function);
        }
    }

    @Override
    public MutableBag<T> toBag()
    {
        synchronized (this.lock)
        {
            return this.delegate.toBag();
        }
    }

    @Override
    public ImmutableBag<T> toImmutableBag()
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableBag();
        }
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedBag();
        }
    }

    @Override
    public ImmutableSortedBag<T> toImmutableSortedBag()
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableSortedBag();
        }
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedBag(comparator);
        }
    }

    @Override
    public ImmutableSortedBag<T> toImmutableSortedBag(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableSortedBag(comparator);
        }
    }

    @Override
    public <V extends Comparable<? super V>> ImmutableSortedBag<T> toImmutableSortedBagBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutableSortedBagBy(function);
        }
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.toMap(keyFunction, valueFunction);
        }
    }

    @Override
    public <K, V, R extends Map<K, V>> R toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction,
            R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.toMap(keyFunction, valueFunction, target);
        }
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedMap(keyFunction, valueFunction);
        }
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Comparator<? super NK> comparator,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedMap(comparator, keyFunction, valueFunction);
        }
    }

    @Override
    public <KK extends Comparable<? super KK>, NK, NV> MutableSortedMap<NK, NV> toSortedMapBy(
            Function<? super NK, KK> sortBy,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedMapBy(sortBy, keyFunction, valueFunction);
        }
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.toBiMap(keyFunction, valueFunction);
        }
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        return LazyIterate.adapt(this);
    }

    @Override
    public Object[] toArray()
    {
        synchronized (this.lock)
        {
            return this.delegate.toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        synchronized (this.lock)
        {
            return this.delegate.toArray(a);
        }
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.min(comparator);
        }
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.max(comparator);
        }
    }

    @Override
    public Optional<T> minOptional(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.minOptional(comparator);
        }
    }

    @Override
    public Optional<T> maxOptional(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.maxOptional(comparator);
        }
    }

    @Override
    public T min()
    {
        synchronized (this.lock)
        {
            return this.delegate.min();
        }
    }

    @Override
    public T max()
    {
        synchronized (this.lock)
        {
            return this.delegate.max();
        }
    }

    @Override
    public Optional<T> minOptional()
    {
        synchronized (this.lock)
        {
            return this.delegate.minOptional();
        }
    }

    @Override
    public Optional<T> maxOptional()
    {
        synchronized (this.lock)
        {
            return this.delegate.maxOptional();
        }
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.minBy(function);
        }
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.maxBy(function);
        }
    }

    @Override
    public <V extends Comparable<? super V>> Optional<T> minByOptional(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.minByOptional(function);
        }
    }

    @Override
    public <V extends Comparable<? super V>> Optional<T> maxByOptional(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.maxByOptional(function);
        }
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumOfInt(function);
        }
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumOfFloat(function);
        }
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumOfLong(function);
        }
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumOfDouble(function);
        }
    }

    @Override
    public String makeString()
    {
        synchronized (this.lock)
        {
            return this.delegate.makeString();
        }
    }

    @Override
    public String makeString(String separator)
    {
        synchronized (this.lock)
        {
            return this.delegate.makeString(separator);
        }
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        synchronized (this.lock)
        {
            return this.delegate.makeString(start, separator, end);
        }
    }

    @Override
    public String makeString(Function<? super T, Object> function, String start, String separator, String end)
    {
        synchronized (this.lock)
        {
            return this.delegate.makeString(function, start, separator, end);
        }
    }

    @Override
    public void appendString(Appendable appendable)
    {
        synchronized (this.lock)
        {
            this.delegate.appendString(appendable);
        }
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        synchronized (this.lock)
        {
            this.delegate.appendString(appendable, separator);
        }
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        synchronized (this.lock)
        {
            this.delegate.appendString(appendable, start, separator, end);
        }
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> Bag<V> countBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().countBy(function);
        }
    }

    /**
     * @since 9.0
     */
    @Override
    public <V, R extends MutableBagIterable<V>> R countBy(Function<? super T, ? extends V> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.countBy(function, target);
        }
    }

    /**
     * @since 9.0
     */
    @Override
    public <V, P> Bag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.countByWith(function, parameter);
        }
    }

    /**
     * @since 9.0
     */
    @Override
    public <V, P, R extends MutableBagIterable<V>> R countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.countByWith(function, parameter, target);
        }
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V> Bag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.countByEach(function);
        }
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V, R extends MutableBagIterable<V>> R countByEach(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.countByEach(function, target);
        }
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupBy(function, target);
        }
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupByEach(function, target);
        }
    }

    @Override
    public <V> MapIterable<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupByUniqueKey(function, UnifiedMap.newMap(this.size()));
        }
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(
            Function<? super T, ? extends V> function,
            R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupByUniqueKey(function, target);
        }
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.zip(that, target);
        }
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.zipWithIndex(target);
        }
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        synchronized (this.lock)
        {
            return this.delegate.chunk(size);
        }
    }

    @Override
    public T getOnly()
    {
        synchronized (this.lock)
        {
            return this.delegate.getOnly();
        }
    }

    @Override
    public ByteIterable collectByte(ByteFunction<? super T> byteFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectByte(byteFunction);
        }
    }

    @Override
    public CharIterable collectChar(CharFunction<? super T> charFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectChar(charFunction);
        }
    }

    @Override
    public DoubleIterable collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectDouble(doubleFunction);
        }
    }

    @Override
    public FloatIterable collectFloat(FloatFunction<? super T> floatFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectFloat(floatFunction);
        }
    }

    @Override
    public IntIterable collectInt(IntFunction<? super T> intFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectInt(intFunction);
        }
    }

    @Override
    public LongIterable collectLong(LongFunction<? super T> longFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectLong(longFunction);
        }
    }

    @Override
    public ShortIterable collectShort(ShortFunction<? super T> shortFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectShort(shortFunction);
        }
    }

    @Override
    public <P, V> RichIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectWith(function, parameter);
        }
    }

    @Override
    public <V> RichIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectIf(predicate, function);
        }
    }

    @Override
    public <V> RichIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollect(function);
        }
    }

    @Override
    public <V> ObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumByInt(groupBy, function);
        }
    }

    @Override
    public <V> ObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumByFloat(groupBy, function);
        }
    }

    @Override
    public <V> ObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumByLong(groupBy, function);
        }
    }

    @Override
    public <V> ObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumByDouble(groupBy, function);
        }
    }

    @Override
    public <V> Multimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupBy(function);
        }
    }

    @Override
    public <V> Multimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupByEach(function);
        }
    }

    @Override
    public <S> RichIterable<Pair<T, S>> zip(Iterable<S> that)
    {
        synchronized (this.lock)
        {
            return this.delegate.zip(that);
        }
    }

    @Override
    public RichIterable<Pair<T, Integer>> zipWithIndex()
    {
        synchronized (this.lock)
        {
            return this.delegate.zipWithIndex();
        }
    }

    @Override
    public <K, V> MapIterable<K, V> aggregateInPlaceBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Procedure2<? super V, ? super T> mutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.delegate.aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
        }
    }

    @Override
    public <K, V> MapIterable<K, V> aggregateBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.delegate.aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
        }
    }

    @Override
    public <K> MapIterable<K, T> reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.reduceBy(groupBy, reduceFunction);
        }
    }

    @Override
    public RichIterable<T> tap(Procedure<? super T> procedure)
    {
        synchronized (this.lock)
        {
            this.forEach(procedure);
            return this;
        }
    }
}
