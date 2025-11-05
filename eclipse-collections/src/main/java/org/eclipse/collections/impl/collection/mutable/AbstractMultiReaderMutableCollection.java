/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Stream;

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
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.Function3;
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
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.procedure.MutatingAggregationProcedure;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * AbstractMultiReaderMutableCollection provides thread-safe collection operations using a
 * {@link ReadWriteLock} for concurrent access patterns with multiple readers and occasional writers.
 * <p>
 * This abstraction uses read-write locking to allow multiple concurrent readers while ensuring
 * exclusive access for write operations. This provides better performance than traditional
 * synchronized collections when reads significantly outnumber writes.
 * </p>
 * <p>
 * Subclasses must implement {@code getDelegate()} to provide the underlying collection being protected.
 * Operations are automatically wrapped with appropriate read or write locks using try-with-resources.
 * </p>
 * <p><b>Thread Safety:</b> This class is thread-safe. Read operations acquire a read lock allowing
 * concurrent access, while write operations acquire a write lock for exclusive access.</p>
 * <p><b>Performance:</b> Better performance than synchronized collections for read-heavy workloads.
 * Multiple threads can read concurrently. Write operations block all other access.</p>
 * <p><b>Important:</b> {@link #iterator()}, {@link #stream()}, {@link #parallelStream()}, and
 * {@link #spliterator()} are not supported directly. Use {@code withReadLockAndDelegate()} or
 * {@code withWriteLockAndDelegate()} methods instead to safely access these operations.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MultiReaderFastList<String> names = MultiReaderFastList.newListWith("Alice", "Bob");
 *
 * // Multiple threads can read concurrently
 * int size = names.size(); // Acquires read lock
 * boolean contains = names.contains("Alice"); // Acquires read lock
 *
 * // Write operations acquire exclusive write lock
 * names.add("Charlie"); // Acquires write lock
 *
 * // For iterator access, use withReadLockAndDelegate
 * names.withReadLockAndDelegate(delegate -> {
 *     Iterator<String> it = delegate.iterator();
 *     while (it.hasNext()) {
 *         System.out.println(it.next());
 *     }
 * });
 * }</pre>
 *
 * @param <T> the type of elements in this collection
 * @since 1.0
 */
@SuppressWarnings({"unused", "TransientFieldInNonSerializableClass"})
public abstract class AbstractMultiReaderMutableCollection<T> implements MutableCollection<T>
{
    protected transient ReadWriteLock lock;
    protected transient ReadWriteLockWrapper lockWrapper;

    protected abstract MutableCollection<T> getDelegate();

    @Override
    public boolean contains(Object item)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().contains(item);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().containsAll(collection);
        }
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().containsAllIterable(source);
        }
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().containsAllArguments(elements);
        }
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().noneSatisfy(predicate);
        }
    }

    @Override
    public <P> boolean noneSatisfyWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().noneSatisfyWith(predicate, parameter);
        }
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().allSatisfy(predicate);
        }
    }

    @Override
    public <P> boolean allSatisfyWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().allSatisfyWith(predicate, parameter);
        }
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().anySatisfy(predicate);
        }
    }

    @Override
    public <P> boolean anySatisfyWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().anySatisfyWith(predicate, parameter);
        }
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().into(target);
        }
    }

    @Override
    public MutableList<T> toList()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toList();
        }
    }

    @Override
    public ImmutableList<T> toImmutableList()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableList();
        }
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toMap(keyFunction, valueFunction);
        }
    }

    @Override
    public <K, V, R extends Map<K, V>> R toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toMap(keyFunction, valueFunction, target);
        }
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedMap(keyFunction, valueFunction);
        }
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Comparator<? super NK> comparator,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedMap(comparator, keyFunction, valueFunction);
        }
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toBiMap(keyFunction, valueFunction);
        }
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().asLazy();
        }
    }

    @Override
    public MutableSet<T> toSet()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSet();
        }
    }

    @Override
    public ImmutableSet<T> toImmutableSet()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSet();
        }
    }

    @Override
    public MutableBag<T> toBag()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toBag();
        }
    }

    @Override
    public ImmutableBag<T> toImmutableBag()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableBag();
        }
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedBag();
        }
    }

    @Override
    public ImmutableSortedBag<T> toImmutableSortedBag()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSortedBag();
        }
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedBag(comparator);
        }
    }

    @Override
    public ImmutableSortedBag<T> toImmutableSortedBag(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSortedBag(comparator);
        }
    }

    @Override
    public <V extends Comparable<? super V>> ImmutableSortedBag<T> toImmutableSortedBagBy(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSortedBagBy(function);
        }
    }

    @Override
    public MutableList<T> toSortedList()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedList();
        }
    }

    @Override
    public ImmutableList<T> toImmutableSortedList()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSortedList();
        }
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedList(comparator);
        }
    }

    @Override
    public ImmutableList<T> toImmutableSortedList(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSortedList(comparator);
        }
    }

    @Override
    public <V extends Comparable<? super V>> MutableList<T> toSortedListBy(
            Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedListBy(function);
        }
    }

    @Override
    public <V extends Comparable<? super V>> ImmutableList<T> toImmutableSortedListBy(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSortedListBy(function);
        }
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedSet();
        }
    }

    @Override
    public ImmutableSortedSet<T> toImmutableSortedSet()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSortedSet();
        }
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedSet(comparator);
        }
    }

    @Override
    public ImmutableSortedSet<T> toImmutableSortedSet(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSortedSet(comparator);
        }
    }

    @Override
    public <V extends Comparable<? super V>> MutableSortedSet<T> toSortedSetBy(
            Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toSortedSetBy(function);
        }
    }

    @Override
    public <V extends Comparable<? super V>> ImmutableSortedSet<T> toImmutableSortedSetBy(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toImmutableSortedSetBy(function);
        }
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().count(predicate);
        }
    }

    @Override
    public <P> int countWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().countWith(predicate, parameter);
        }
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().detect(predicate);
        }
    }

    @Override
    public <P> T detectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().detectWith(predicate, parameter);
        }
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().detectOptional(predicate);
        }
    }

    @Override
    public <P> Optional<T> detectWithOptional(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().detectWithOptional(predicate, parameter);
        }
    }

    @Override
    public T detectIfNone(
            Predicate<? super T> predicate,
            Function0<? extends T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().detectIfNone(predicate, function);
        }
    }

    @Override
    public <P> T detectWithIfNone(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Function0<? extends T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().detectWithIfNone(predicate, parameter, function);
        }
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().min(comparator);
        }
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().max(comparator);
        }
    }

    @Override
    public Optional<T> minOptional(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().minOptional(comparator);
        }
    }

    @Override
    public Optional<T> maxOptional(Comparator<? super T> comparator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().maxOptional(comparator);
        }
    }

    @Override
    public T min()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().min();
        }
    }

    @Override
    public T max()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().max();
        }
    }

    @Override
    public Optional<T> minOptional()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().minOptional();
        }
    }

    @Override
    public Optional<T> maxOptional()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().maxOptional();
        }
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().minBy(function);
        }
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().maxBy(function);
        }
    }

    @Override
    public <V extends Comparable<? super V>> Optional<T> minByOptional(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().minByOptional(function);
        }
    }

    @Override
    public <V extends Comparable<? super V>> Optional<T> maxByOptional(Function<? super T, ? extends V> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().maxByOptional(function);
        }
    }

    @Override
    public T getFirst()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().getFirst();
        }
    }

    @Override
    public T getLast()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().getLast();
        }
    }

    @Override
    public T getOnly()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().getOnly();
        }
    }

    @Override
    public boolean notEmpty()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().notEmpty();
        }
    }

    @Override
    public <P> Twin<MutableList<T>> selectAndRejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().selectAndRejectWith(predicate, parameter);
        }
    }

    @Override
    public <V, R extends Collection<V>> R collect(
            Function<? super T, ? extends V> function,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collect(function, target);
        }
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectBoolean(booleanFunction, target);
        }
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().flatCollectBoolean(function, target);
        }
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectByte(byteFunction, target);
        }
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().flatCollectByte(function, target);
        }
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectChar(charFunction, target);
        }
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().flatCollectChar(function, target);
        }
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectDouble(doubleFunction, target);
        }
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().flatCollectDouble(function, target);
        }
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectFloat(floatFunction, target);
        }
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().flatCollectFloat(function, target);
        }
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectInt(intFunction, target);
        }
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().flatCollectInt(function, target);
        }
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectLong(longFunction, target);
        }
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().flatCollectLong(function, target);
        }
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectShort(shortFunction, target);
        }
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().flatCollectShort(function, target);
        }
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().flatCollect(function, target);
        }
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectIf(predicate, function, target);
        }
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R targetCollection)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().collectWith(function, parameter, targetCollection);
        }
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().selectWith(predicate, parameter, targetCollection);
        }
    }

    @Override
    public <R extends Collection<T>> R reject(
            Predicate<? super T> predicate,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().reject(predicate, target);
        }
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().rejectWith(predicate, parameter, targetCollection);
        }
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().select(predicate, target);
        }
    }

    @Override
    public <IV> IV injectInto(
            IV injectedValue,
            Function2<? super IV, ? super T, ? extends IV> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().injectInto(injectedValue, function);
        }
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().injectIntoInt(injectedValue, function);
        }
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().injectIntoLong(injectedValue, function);
        }
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().injectIntoDouble(injectedValue, function);
        }
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().injectIntoFloat(injectedValue, function);
        }
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().sumOfInt(function);
        }
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().sumOfFloat(function);
        }
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().sumOfLong(function);
        }
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().sumOfDouble(function);
        }
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function));
    }

    @Override
    public <IV, P> IV injectIntoWith(
            IV injectValue,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().injectIntoWith(injectValue, function, parameter);
        }
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().removeIf(predicate);
        }
    }

    @Override
    public <P> boolean removeIfWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().removeIfWith(predicate, parameter);
        }
    }

    @Override
    public boolean add(T item)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().add(item);
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().addAll(collection);
        }
    }

    @Override
    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().addAllIterable(iterable);
        }
    }

    @Override
    public void clear()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            this.getDelegate().clear();
        }
    }

    @Override
    public boolean isEmpty()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().isEmpty();
        }
    }

    /**
     * This method is not supported directly on MultiReader collections because it is not thread-safe. If you would like
     * to use an iterator with a MultiReader collection, then you must do the following:
     *
     * <pre>
     * multiReaderList.withReadLockAndDelegate(MutableList&lt;Person&gt; -&gt;
     *     {
     *         Iterator it = people.iterator();
     *         ....
     *     });
     * </pre>
     *
     * <pre>
     * final Collection jdkSet = new HashSet();
     * final boolean containsAll = new boolean[1];
     * multiReaderList.withReadLockAndDelegate(MutableList&lt;Person&gt; people -&gt;
     *     {
     *         set.addAll(people); // addAll uses iterator() in AbstractCollection
     *         containsAll[0] = set.containsAll(people); // containsAll uses iterator() in AbstractCollection
     *     });
     * </pre>
     */
    @Override
    public Iterator<T> iterator()
    {
        throw new UnsupportedOperationException(
                "Iterator is not supported directly on MultiReader collections.  "
                        + "If you would like to use an iterator, you must either use withReadLockAndDelegate() or withWriteLockAndDelegate().");
    }

    /**
     * This method is not supported directly on MultiReader collections because it is not thread-safe. If you would like
     * to use a spliterator with a MultiReader collection, then you must protect the calls by calling either
     * withReadLockAndDelegate or withWriteLockAndDelegate.
     */
    @Override
    public Spliterator<T> spliterator()
    {
        throw new UnsupportedOperationException(
                "Spliterator is not supported directly on MultiReader collections.  "
                        + "If you would like to use an spliterator, you must either use withReadLockAndDelegate() or withWriteLockAndDelegate().");
    }

    /**
     * This method is not supported directly on MultiReader collections because it is not thread-safe. If you would like
     * to use stream with a MultiReader collection, then you must protect the calls by calling either
     * withReadLockAndDelegate or withWriteLockAndDelegate.
     */
    @Override
    public Stream<T> stream()
    {
        throw new UnsupportedOperationException(
                "Stream is not supported directly on MultiReader collections.  "
                        + "If you would like to use stream, you must either use withReadLockAndDelegate() or withWriteLockAndDelegate().");
    }

    /**
     * This method is not supported directly on MultiReader collections because it is not thread-safe. If you would like
     * to use parallelStream with a MultiReader collection, then you must protect the calls by calling either
     * withReadLockAndDelegate or withWriteLockAndDelegate.
     */
    @Override
    public Stream<T> parallelStream()
    {
        throw new UnsupportedOperationException(
                "parallelStream is not supported directly on MultiReader collections.  "
                        + "If you would like to use parallelStream, you must either use withReadLockAndDelegate() or withWriteLockAndDelegate().");
    }

    @Override
    public boolean remove(Object item)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().remove(item);
        }
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().removeAll(collection);
        }
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().removeAllIterable(iterable);
        }
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().retainAll(collection);
        }
    }

    @Override
    public boolean retainAllIterable(Iterable<?> iterable)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireWriteLock())
        {
            return this.getDelegate().retainAllIterable(iterable);
        }
    }

    @Override
    public int size()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().size();
        }
    }

    @Override
    public Object[] toArray()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toArray();
        }
    }

    @Override
    public <E> E[] toArray(E[] a)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toArray(a);
        }
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.getDelegate().forEach(procedure);
        }
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.getDelegate().forEachWith(procedure, parameter);
        }
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.getDelegate().forEachWithIndex(objectIntProcedure);
        }
    }

    @Override
    public String toString()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().toString();
        }
    }

    @Override
    public String makeString()
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().makeString();
        }
    }

    @Override
    public String makeString(String separator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().makeString(separator);
        }
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().makeString(start, separator, end);
        }
    }

    @Override
    public String makeString(Function<? super T, Object> function, String start, String separator, String end)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().makeString(function, start, separator, end);
        }
    }

    @Override
    public void appendString(Appendable appendable)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.getDelegate().appendString(appendable);
        }
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.getDelegate().appendString(appendable, separator);
        }
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            this.getDelegate().appendString(appendable, start, separator, end);
        }
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().groupBy(function, target);
        }
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().groupByEach(function, target);
        }
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(
            Function<? super T, ? extends V> function,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().groupByUniqueKey(function, target);
        }
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().zip(that, target);
        }
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().zipWithIndex(target);
        }
    }

    @Override
    public <K> MutableMapIterable<K, T> reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().reduceBy(groupBy, reduceFunction);
        }
    }

    @Override
    public <K, R extends MutableMapIterable<K, T>> R reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction,
            R target)
    {
        try (LockWrapper wrapper = this.lockWrapper.acquireReadLock())
        {
            return this.getDelegate().reduceBy(groupBy, reduceFunction, target);
        }
    }

    @Override
    public <K, V> MutableMap<K, V> aggregateInPlaceBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        MutableMap<K, V> map = Maps.mutable.empty();
        this.forEach(new MutatingAggregationProcedure<>(map, groupBy, zeroValueFactory, mutatingAggregator));
        return map;
    }

    protected abstract static class UntouchableMutableCollection<T>
            implements MutableCollection<T>
    {
        protected MutableCollection<T> delegate;

        protected UntouchableMutableCollection(MutableCollection<T> delegate)
        {
            this.delegate = Objects.requireNonNull(delegate);
        }

        protected abstract MutableCollection<T> getDelegate();

        @Override
        public LazyIterable<T> asLazy()
        {
            return LazyIterate.adapt(this);
        }

        @Override
        public final <R extends MutableBooleanCollection> R collectBoolean(
                BooleanFunction<? super T> booleanFunction,
                R target)
        {
            return this.getDelegate().collectBoolean(booleanFunction, target);
        }

        @Override
        public final <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
        {
            return this.getDelegate().collectByte(byteFunction, target);
        }

        @Override
        public final <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
        {
            return this.getDelegate().collectChar(charFunction, target);
        }

        @Override
        public final <R extends MutableDoubleCollection> R collectDouble(
                DoubleFunction<? super T> doubleFunction,
                R target)
        {
            return this.getDelegate().collectDouble(doubleFunction, target);
        }

        @Override
        public final <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
        {
            return this.getDelegate().collectFloat(floatFunction, target);
        }

        @Override
        public final <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
        {
            return this.getDelegate().collectInt(intFunction, target);
        }

        @Override
        public final <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
        {
            return this.getDelegate().collectLong(longFunction, target);
        }

        @Override
        public final <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
        {
            return this.getDelegate().collectShort(shortFunction, target);
        }

        @Override
        public final <R extends MutableBooleanCollection> R flatCollectBoolean(
                Function<? super T, ? extends BooleanIterable> function, R target)
        {
            return this.getDelegate().flatCollectBoolean(function, target);
        }

        @Override
        public final <R extends MutableByteCollection> R flatCollectByte(
                Function<? super T, ? extends ByteIterable> function, R target)
        {
            return this.getDelegate().flatCollectByte(function, target);
        }

        @Override
        public final <R extends MutableCharCollection> R flatCollectChar(
                Function<? super T, ? extends CharIterable> function, R target)
        {
            return this.getDelegate().flatCollectChar(function, target);
        }

        @Override
        public final <R extends MutableDoubleCollection> R flatCollectDouble(
                Function<? super T, ? extends DoubleIterable> function, R target)
        {
            return this.getDelegate().flatCollectDouble(function, target);
        }

        @Override
        public final <R extends MutableFloatCollection> R flatCollectFloat(
                Function<? super T, ? extends FloatIterable> function, R target)
        {
            return this.getDelegate().flatCollectFloat(function, target);
        }

        @Override
        public final <R extends MutableIntCollection> R flatCollectInt(
                Function<? super T, ? extends IntIterable> function, R target)
        {
            return this.getDelegate().flatCollectInt(function, target);
        }

        @Override
        public final <R extends MutableLongCollection> R flatCollectLong(
                Function<? super T, ? extends LongIterable> function, R target)
        {
            return this.getDelegate().flatCollectLong(function, target);
        }

        @Override
        public final <R extends MutableShortCollection> R flatCollectShort(
                Function<? super T, ? extends ShortIterable> function, R target)
        {
            return this.getDelegate().flatCollectShort(function, target);
        }

        @Override
        public boolean allSatisfy(Predicate<? super T> predicate)
        {
            return this.delegate.allSatisfy(predicate);
        }

        @Override
        public <P> boolean allSatisfyWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.delegate.allSatisfyWith(predicate, parameter);
        }

        @Override
        public boolean noneSatisfy(Predicate<? super T> predicate)
        {
            return this.delegate.noneSatisfy(predicate);
        }

        @Override
        public <P> boolean noneSatisfyWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.delegate.noneSatisfyWith(predicate, parameter);
        }

        @Override
        public boolean anySatisfy(Predicate<? super T> predicate)
        {
            return this.delegate.anySatisfy(predicate);
        }

        @Override
        public <P> boolean anySatisfyWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.delegate.anySatisfyWith(predicate, parameter);
        }

        @Override
        public <R extends Collection<T>> R into(R target)
        {
            return this.delegate.into(target);
        }

        @Override
        public MutableList<T> toList()
        {
            return this.delegate.toList();
        }

        @Override
        public <NK, NV> MutableMap<NK, NV> toMap(
                Function<? super T, ? extends NK> keyFunction,
                Function<? super T, ? extends NV> valueFunction)
        {
            return this.delegate.toMap(keyFunction, valueFunction);
        }

        @Override
        public <NK, NV, R extends Map<NK, NV>> R toMap(
                Function<? super T, ? extends NK> keyFunction,
                Function<? super T, ? extends NV> valueFunction,
                R target)
        {
            return this.delegate.toMap(keyFunction, valueFunction, target);
        }

        @Override
        public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
                Function<? super T, ? extends NK> keyFunction,
                Function<? super T, ? extends NV> valueFunction)
        {
            return this.delegate.toSortedMap(keyFunction, valueFunction);
        }

        @Override
        public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
                Comparator<? super NK> comparator,
                Function<? super T, ? extends NK> keyFunction,
                Function<? super T, ? extends NV> valueFunction)
        {
            return this.delegate.toSortedMap(comparator, keyFunction, valueFunction);
        }

        @Override
        public <KK extends Comparable<? super KK>, NK, NV> MutableSortedMap<NK, NV> toSortedMapBy(
                Function<? super NK, KK> sortBy,
                Function<? super T, ? extends NK> keyFunction,
                Function<? super T, ? extends NV> valueFunction)
        {
            return this.delegate.toSortedMapBy(sortBy, keyFunction, valueFunction);
        }

        @Override
        public <NK, NV> MutableBiMap<NK, NV> toBiMap(
                Function<? super T, ? extends NK> keyFunction,
                Function<? super T, ? extends NV> valueFunction)
        {
            return this.delegate.toBiMap(keyFunction, valueFunction);
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
        public <V extends Comparable<? super V>> MutableSortedBag<T> toSortedBagBy(Function<? super T, ? extends V> function)
        {
            return this.delegate.toSortedBagBy(function);
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
        public <V extends Comparable<? super V>> MutableList<T> toSortedListBy(Function<? super T, ? extends V> function)
        {
            return this.delegate.toSortedListBy(function);
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
        public <V extends Comparable<? super V>> MutableSortedSet<T> toSortedSetBy(Function<? super T, ? extends V> function)
        {
            return this.delegate.toSortedSetBy(function);
        }

        @Override
        public <V, R extends Collection<V>> R collect(
                Function<? super T, ? extends V> function,
                R target)
        {
            return this.delegate.collect(function, target);
        }

        @Override
        public <V, R extends Collection<V>> R flatCollect(
                Function<? super T, ? extends Iterable<V>> function,
                R target)
        {
            return this.delegate.flatCollect(function, target);
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
        public <P, V, R extends Collection<V>> R collectWith(
                Function2<? super T, ? super P, ? extends V> function,
                P parameter,
                R targetCollection)
        {
            return this.delegate.collectWith(function, parameter, targetCollection);
        }

        @Override
        public <V, R extends MutableMultimap<V, T>> R groupBy(
                Function<? super T, ? extends V> function,
                R target)
        {
            return this.delegate.groupBy(function, target);
        }

        @Override
        public <V, R extends MutableMultimap<V, T>> R groupByEach(
                Function<? super T, ? extends Iterable<V>> function,
                R target)
        {
            return this.delegate.groupByEach(function, target);
        }

        @Override
        public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(
                Function<? super T, ? extends V> function,
                R target)
        {
            return this.delegate.groupByUniqueKey(function, target);
        }

        @Override
        public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
        {
            return this.getDelegate().groupByUniqueKey(function);
        }

        @Override
        public int count(Predicate<? super T> predicate)
        {
            return this.delegate.count(predicate);
        }

        @Override
        public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
        {
            return this.delegate.countWith(predicate, parameter);
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
        public <P> T detectWithIfNone(
                Predicate2<? super T, ? super P> predicate,
                P parameter,
                Function0<? extends T> function)
        {
            return this.delegate.detectWithIfNone(predicate, parameter, function);
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
        public <IV> IV injectInto(
                IV injectedValue,
                Function2<? super IV, ? super T, ? extends IV> function)
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
        public long sumOfInt(IntFunction<? super T> function)
        {
            return this.delegate.sumOfInt(function);
        }

        @Override
        public double sumOfFloat(FloatFunction<? super T> function)
        {
            return this.delegate.sumOfFloat(function);
        }

        @Override
        public long sumOfLong(LongFunction<? super T> function)
        {
            return this.delegate.sumOfLong(function);
        }

        @Override
        public double sumOfDouble(DoubleFunction<? super T> function)
        {
            return this.delegate.sumOfDouble(function);
        }

        @Override
        public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
        {
            return this.delegate.sumByInt(groupBy, function);
        }

        @Override
        public <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
        {
            return this.delegate.sumByFloat(groupBy, function);
        }

        @Override
        public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
        {
            return this.delegate.sumByLong(groupBy, function);
        }

        @Override
        public <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
        {
            return this.delegate.sumByDouble(groupBy, function);
        }

        @Override
        public <IV, P> IV injectIntoWith(
                IV injectValue,
                Function3<? super IV, ? super T, ? super P, ? extends IV> function,
                P parameter)
        {
            return this.delegate.injectIntoWith(injectValue, function, parameter);
        }

        @Override
        public boolean notEmpty()
        {
            return this.delegate.notEmpty();
        }

        @Override
        public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
        {
            return this.delegate.reject(predicate, target);
        }

        @Override
        public <P, R extends Collection<T>> R rejectWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter,
                R targetCollection)
        {
            return this.delegate.rejectWith(predicate, parameter, targetCollection);
        }

        @Override
        public boolean removeIf(Predicate<? super T> predicate)
        {
            return this.delegate.removeIf(predicate);
        }

        @Override
        public <P> boolean removeIfWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.delegate.removeIfWith(predicate, parameter);
        }

        @Override
        public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
        {
            return this.delegate.select(predicate, target);
        }

        @Override
        public <P> Twin<MutableList<T>> selectAndRejectWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.delegate.selectAndRejectWith(predicate, parameter);
        }

        @Override
        public <P, R extends Collection<T>> R selectWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter,
                R targetCollection)
        {
            return this.delegate.selectWith(predicate, parameter, targetCollection);
        }

        @Override
        public boolean add(T o)
        {
            return this.delegate.add(o);
        }

        @Override
        public boolean addAll(Collection<? extends T> collection)
        {
            return this.delegate.addAll(collection);
        }

        @Override
        public boolean addAllIterable(Iterable<? extends T> iterable)
        {
            return this.delegate.addAllIterable(iterable);
        }

        @Override
        public void clear()
        {
            this.delegate.clear();
        }

        @Override
        public boolean contains(Object o)
        {
            return this.delegate.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> collection)
        {
            return this.delegate.containsAll(collection);
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
        public boolean equals(Object o)
        {
            return this.delegate.equals(o);
        }

        @Override
        public int hashCode()
        {
            return this.delegate.hashCode();
        }

        @Override
        public boolean isEmpty()
        {
            return this.delegate.isEmpty();
        }

        @Override
        public boolean remove(Object o)
        {
            return this.delegate.remove(o);
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            return this.delegate.removeAll(collection);
        }

        @Override
        public boolean removeAllIterable(Iterable<?> iterable)
        {
            return this.delegate.removeAllIterable(iterable);
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            return this.delegate.retainAll(collection);
        }

        @Override
        public boolean retainAllIterable(Iterable<?> iterable)
        {
            return this.delegate.retainAllIterable(iterable);
        }

        @Override
        public int size()
        {
            return this.delegate.size();
        }

        @Override
        public Object[] toArray()
        {
            return this.delegate.toArray();
        }

        @Override
        public <E> E[] toArray(E[] a)
        {
            return this.delegate.toArray(a);
        }

        @Override
        public void each(Procedure<? super T> procedure)
        {
            this.delegate.forEach(procedure);
        }

        @Override
        public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
        {
            this.delegate.forEachWith(procedure, parameter);
        }

        @Override
        public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
        {
            this.delegate.forEachWithIndex(objectIntProcedure);
        }

        @Override
        public String toString()
        {
            return this.delegate.toString();
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

        @Override
        public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
        {
            return this.delegate.zip(that, target);
        }

        @Override
        public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
        {
            return this.delegate.zipWithIndex(target);
        }

        @Override
        public RichIterable<RichIterable<T>> chunk(int size)
        {
            return this.delegate.chunk(size);
        }
    }

    public static class ReadWriteLockWrapper
    {
        private final LockWrapper readLock;
        private final LockWrapper writeLock;

        public ReadWriteLockWrapper(ReadWriteLock readWriteLock)
        {
            this.readLock = new LockWrapper(readWriteLock.readLock());
            this.writeLock = new LockWrapper(readWriteLock.writeLock());
        }

        /**
         * This method must be wrapped in a try block.
         */
        public LockWrapper acquireReadLock()
        {
            this.readLock.lock.lock();
            return this.readLock;
        }

        /**
         * This method must be wrapped in a try block.
         */
        public LockWrapper acquireWriteLock()
        {
            this.writeLock.lock.lock();
            return this.writeLock;
        }
    }

    public static class LockWrapper implements AutoCloseable
    {
        private final Lock lock;

        public LockWrapper(Lock lock)
        {
            this.lock = lock;
        }

        @Override
        public void close()
        {
            this.lock.unlock();
        }
    }
}
