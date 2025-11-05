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
import org.eclipse.collections.api.bag.MutableBag;
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
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * AbstractUnmodifiableMutableCollection provides an unmodifiable view of a MutableCollection.
 * This class prevents modifications while maintaining the MutableCollection interface.
 * <p>
 * All read operations are delegated to the wrapped collection. All write operations
 * (add, remove, clear, etc.) throw {@link UnsupportedOperationException}. The iterator
 * returned is also unmodifiable.
 * </p>
 * <p><b>Thread Safety:</b> Thread-safety depends on the wrapped collection. This wrapper
 * does not add synchronization.</p>
 * <p><b>Performance:</b> Read operations have the same performance as the wrapped collection.
 * Write operations fail immediately.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * MutableList<String> mutableList = FastList.newListWith("a", "b", "c");
 * MutableCollection<String> unmodifiable = mutableList.asUnmodifiable();
 *
 * // Read operations work
 * int size = unmodifiable.size(); // Returns 3
 * boolean contains = unmodifiable.contains("a"); // Returns true
 *
 * // Write operations throw UnsupportedOperationException
 * // unmodifiable.add("d"); // Throws UnsupportedOperationException
 * // unmodifiable.remove("a"); // Throws UnsupportedOperationException
 * }</pre>
 *
 * @param <T> the type of elements in this collection
 * @since 1.0
 */
public class AbstractUnmodifiableMutableCollection<T> implements MutableCollection<T>
{
    private final MutableCollection<? extends T> collection;

    protected AbstractUnmodifiableMutableCollection(MutableCollection<? extends T> mutableCollection)
    {
        if (mutableCollection == null)
        {
            throw new NullPointerException("cannot create a UnmodifiableMutableCollection for null");
        }
        this.collection = mutableCollection;
    }

    @Override
    public int size()
    {
        return this.collection.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.collection.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return this.collection.contains(o);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.collection.iterator());
    }

    @Override
    public Object[] toArray()
    {
        return this.collection.toArray();
    }

    @Override
    public <S> S[] toArray(S[] a)
    {
        return this.collection.toArray(a);
    }

    @Override
    public boolean add(T o)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return this.collection.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException("Cannot call retainAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException("Cannot call removeAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    protected MutableCollection<T> getMutableCollection()
    {
        return (MutableCollection<T>) this.collection;
    }

    @Override
    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        throw new UnsupportedOperationException("Cannot call addAllIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        throw new UnsupportedOperationException("Cannot call removeAllIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean retainAllIterable(Iterable<?> iterable)
    {
        throw new UnsupportedOperationException("Cannot call retainAllIterable() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableCollection<T> asUnmodifiable()
    {
        return this;
    }

    @Override
    public MutableCollection<T> asSynchronized()
    {
        return SynchronizedMutableCollection.of(this);
    }

    @Override
    public ImmutableCollection<T> toImmutable()
    {
        return this.getMutableCollection().toImmutable();
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        return LazyIterate.adapt(this);
    }

    @Override
    public MutableCollection<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.getMutableCollection().forEach(procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.getMutableCollection().forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.getMutableCollection().forEachWith(procedure, parameter);
    }

    @Override
    public boolean containsAny(Collection<?> source)
    {
        return this.getMutableCollection().containsAny(source);
    }

    @Override
    public boolean containsNone(Collection<?> source)
    {
        return this.getMutableCollection().containsNone(source);
    }

    @Override
    public boolean containsAnyIterable(Iterable<?> source)
    {
        return this.getMutableCollection().containsAnyIterable(source);
    }

    @Override
    public boolean containsNoneIterable(Iterable<?> source)
    {
        return this.getMutableCollection().containsNoneIterable(source);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.getMutableCollection().containsAllIterable(source);
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return this.getMutableCollection().containsAllArguments(elements);
    }

    @Override
    public boolean notEmpty()
    {
        return this.getMutableCollection().notEmpty();
    }

    @Override
    public MutableCollection<T> newEmpty()
    {
        return this.getMutableCollection().newEmpty();
    }

    @Override
    public T getFirst()
    {
        return this.getMutableCollection().getFirst();
    }

    @Override
    public T getLast()
    {
        return this.getMutableCollection().getLast();
    }

    @Override
    public T getOnly()
    {
        return this.getMutableCollection().getOnly();
    }

    @Override
    public MutableCollection<T> select(Predicate<? super T> predicate)
    {
        return this.getMutableCollection().select(predicate);
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return this.getMutableCollection().select(predicate, target);
    }

    @Override
    public <P> MutableCollection<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableCollection().selectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        return this.getMutableCollection().selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public MutableCollection<T> reject(Predicate<? super T> predicate)
    {
        return this.getMutableCollection().reject(predicate);
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return this.getMutableCollection().reject(predicate, target);
    }

    @Override
    public <P> MutableCollection<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableCollection().rejectWith(predicate, parameter);
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        return this.getMutableCollection().rejectWith(predicate, parameter, targetCollection);
    }

    @Override
    public <P> Twin<MutableList<T>> selectAndRejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return this.getMutableCollection().selectAndRejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableCollection<T> partition(Predicate<? super T> predicate)
    {
        return this.getMutableCollection().partition(predicate);
    }

    @Override
    public <P> PartitionMutableCollection<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableCollection().partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableCollection<S> selectInstancesOf(Class<S> clazz)
    {
        return this.getMutableCollection().selectInstancesOf(clazz);
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException("Cannot call removeIf() on " + this.getClass().getSimpleName());
    }

    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        throw new UnsupportedOperationException("Cannot call removeIfWith() on " + this.getClass().getSimpleName());
    }

    @Override
    public <V> MutableCollection<V> collect(Function<? super T, ? extends V> function)
    {
        return this.getMutableCollection().collect(function);
    }

    @Override
    public MutableBooleanCollection collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.getMutableCollection().collectBoolean(booleanFunction);
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        return this.getMutableCollection().collectBoolean(booleanFunction, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        return this.getMutableCollection().flatCollectBoolean(function, target);
    }

    @Override
    public MutableByteCollection collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.getMutableCollection().collectByte(byteFunction);
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        return this.getMutableCollection().collectByte(byteFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        return this.getMutableCollection().flatCollectByte(function, target);
    }

    @Override
    public MutableCharCollection collectChar(CharFunction<? super T> charFunction)
    {
        return this.getMutableCollection().collectChar(charFunction);
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        return this.getMutableCollection().collectChar(charFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        return this.getMutableCollection().flatCollectChar(function, target);
    }

    @Override
    public MutableDoubleCollection collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.getMutableCollection().collectDouble(doubleFunction);
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        return this.getMutableCollection().collectDouble(doubleFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function, R target)
    {
        return this.getMutableCollection().flatCollectDouble(function, target);
    }

    @Override
    public MutableFloatCollection collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.getMutableCollection().collectFloat(floatFunction);
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        return this.getMutableCollection().collectFloat(floatFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        return this.getMutableCollection().flatCollectFloat(function, target);
    }

    @Override
    public MutableIntCollection collectInt(IntFunction<? super T> intFunction)
    {
        return this.getMutableCollection().collectInt(intFunction);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        return this.getMutableCollection().collectInt(intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        return this.getMutableCollection().flatCollectInt(function, target);
    }

    @Override
    public MutableLongCollection collectLong(LongFunction<? super T> longFunction)
    {
        return this.getMutableCollection().collectLong(longFunction);
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        return this.getMutableCollection().collectLong(longFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        return this.getMutableCollection().flatCollectLong(function, target);
    }

    @Override
    public MutableShortCollection collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.getMutableCollection().collectShort(shortFunction);
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        return this.getMutableCollection().collectShort(shortFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        return this.getMutableCollection().flatCollectShort(function, target);
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return this.getMutableCollection().collect(function, target);
    }

    @Override
    public <V> MutableCollection<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getMutableCollection().flatCollect(function);
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.getMutableCollection().flatCollect(function, target);
    }

    @Override
    public <P, A> MutableCollection<A> collectWith(Function2<? super T, ? super P, ? extends A> function, P parameter)
    {
        return this.getMutableCollection().collectWith(function, parameter);
    }

    @Override
    public <P, A, R extends Collection<A>> R collectWith(
            Function2<? super T, ? super P, ? extends A> function,
            P parameter,
            R targetCollection)
    {
        return this.getMutableCollection().collectWith(function, parameter, targetCollection);
    }

    @Override
    public <V> MutableCollection<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return this.getMutableCollection().collectIf(predicate, function);
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        return this.getMutableCollection().collectIf(predicate, function, target);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return this.getMutableCollection().detect(predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableCollection().detectWith(predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return this.getMutableCollection().detectOptional(predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableCollection().detectWithOptional(predicate, parameter);
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        return this.getMutableCollection().detectIfNone(predicate, function);
    }

    @Override
    public <P> T detectWithIfNone(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Function0<? extends T> function)
    {
        return this.getMutableCollection().detectWithIfNone(predicate, parameter, function);
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return this.getMutableCollection().min(comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return this.getMutableCollection().max(comparator);
    }

    @Override
    public T min()
    {
        return this.getMutableCollection().min();
    }

    @Override
    public T max()
    {
        return this.getMutableCollection().max();
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return this.getMutableCollection().minBy(function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return this.getMutableCollection().maxBy(function);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return this.getMutableCollection().count(predicate);
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableCollection().countWith(predicate, parameter);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return this.getMutableCollection().anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableCollection().anySatisfyWith(predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return this.getMutableCollection().allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableCollection().allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return this.getMutableCollection().noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.getMutableCollection().noneSatisfyWith(predicate, parameter);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return this.getMutableCollection().injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        return this.getMutableCollection().injectIntoInt(injectedValue, function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        return this.getMutableCollection().injectIntoLong(injectedValue, function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        return this.getMutableCollection().injectIntoDouble(injectedValue, function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        return this.getMutableCollection().injectIntoFloat(injectedValue, function);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        return this.getMutableCollection().sumOfInt(function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        return this.getMutableCollection().sumOfFloat(function);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        return this.getMutableCollection().sumOfLong(function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        return this.getMutableCollection().sumOfDouble(function);
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
        return this.getMutableCollection().injectIntoWith(injectValue, function, parameter);
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        return this.getMutableCollection().into(target);
    }

    @Override
    public MutableList<T> toList()
    {
        return this.getMutableCollection().toList();
    }

    @Override
    public MutableList<T> toSortedList()
    {
        return this.getMutableCollection().toSortedList();
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.getMutableCollection().toSortedList(comparator);
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        return this.getMutableCollection().toSortedSet();
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        return this.getMutableCollection().toSortedSet(comparator);
    }

    @Override
    public MutableSet<T> toSet()
    {
        return this.getMutableCollection().toSet();
    }

    @Override
    public MutableBag<T> toBag()
    {
        return this.getMutableCollection().toBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        return this.getMutableCollection().toSortedBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        return this.getMutableCollection().toSortedBag(comparator);
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.getMutableCollection().toMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction,
            R target)
    {
        return this.getMutableCollection().toMap(keyFunction, valueFunction, target);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.getMutableCollection().toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Comparator<? super NK> comparator,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.getMutableCollection().toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public <KK extends Comparable<? super KK>, NK, NV> MutableSortedMap<NK, NV> toSortedMapBy(
            Function<? super NK, KK> sortBy,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction)
    {
        return this.getMutableCollection().toSortedMapBy(sortBy, keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.getMutableCollection().toBiMap(keyFunction, valueFunction);
    }

    @Override
    public String toString()
    {
        return this.getMutableCollection().toString();
    }

    @Override
    public String makeString()
    {
        return this.getMutableCollection().makeString();
    }

    @Override
    public String makeString(String separator)
    {
        return this.getMutableCollection().makeString(separator);
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        return this.getMutableCollection().makeString(start, separator, end);
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.getMutableCollection().appendString(appendable);
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.getMutableCollection().appendString(appendable, separator);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.getMutableCollection().appendString(appendable, start, separator, end);
    }

    @Override
    public <V> MutableMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.getMutableCollection().groupBy(function);
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        return this.getMutableCollection().groupBy(function, target);
    }

    @Override
    public <V> MutableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.getMutableCollection().groupByEach(function);
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return this.getMutableCollection().groupByEach(function, target);
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.getMutableCollection().groupByUniqueKey(function);
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(
            Function<? super T, ? extends V> function,
            R target)
    {
        return this.getMutableCollection().groupByUniqueKey(function, target);
    }

    @Override
    public <S> MutableCollection<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.getMutableCollection().zip(that);
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.getMutableCollection().zip(that, target);
    }

    @Override
    public MutableCollection<Pair<T, Integer>> zipWithIndex()
    {
        return this.getMutableCollection().zipWithIndex();
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.getMutableCollection().zipWithIndex(target);
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        return this.getMutableCollection().chunk(size);
    }

    @Override
    public MutableCollection<T> with(T element)
    {
        throw new UnsupportedOperationException("Cannot call with() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableCollection<T> without(T element)
    {
        throw new UnsupportedOperationException("Cannot call without() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableCollection<T> withAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableCollection<T> withoutAll(Iterable<? extends T> elements)
    {
        throw new UnsupportedOperationException("Cannot call withoutAll() on " + this.getClass().getSimpleName());
    }
}
