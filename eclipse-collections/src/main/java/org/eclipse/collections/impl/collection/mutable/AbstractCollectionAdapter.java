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

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
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
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.BiMaps;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.SortedBags;
import org.eclipse.collections.api.factory.SortedSets;
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
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.procedure.BiMapCollectProcedure;
import org.eclipse.collections.impl.block.procedure.MapCollectProcedure;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;
import org.eclipse.collections.impl.utility.internal.IterableIterate;
import org.eclipse.collections.impl.utility.internal.MutableCollectionIterate;

/**
 * AbstractCollectionAdapter is an abstract adapter that wraps a JDK {@link Collection}
 * and provides the Eclipse Collections {@link MutableCollection} API.
 * <p>
 * This class allows you to use any standard Java Collection with Eclipse Collections APIs,
 * enabling seamless integration between JDK collections and Eclipse Collections functionality.
 * All operations delegate to the wrapped collection while providing additional Eclipse Collections methods.
 * </p>
 * <p><b>Thread Safety:</b> Thread-safety depends on the wrapped collection. If the wrapped
 * collection is thread-safe, then this adapter is thread-safe.</p>
 * <p><b>Performance:</b> Performance characteristics match those of the underlying wrapped collection.
 * Eclipse Collections methods use optimized iteration patterns when possible.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Wrap a standard ArrayList with Eclipse Collections API
 * List<String> jdkList = new ArrayList<>();
 * jdkList.add("Alice");
 * jdkList.add("Bob");
 * MutableCollection<String> ecCollection = CollectionAdapter.adapt(jdkList);
 *
 * // Use Eclipse Collections APIs
 * MutableList<String> upperCase = ecCollection.collect(String::toUpperCase).toList();
 * MutableCollection<String> filtered = ecCollection.select(name -> name.startsWith("A"));
 * }</pre>
 *
 * @param <T> the type of elements in this collection
 * @since 1.0
 */
public abstract class AbstractCollectionAdapter<T>
        implements MutableCollection<T>
{
    protected abstract Collection<T> getDelegate();

    protected <E> MutableCollection<E> wrap(Collection<E> collection)
    {
        return CollectionAdapter.adapt(collection);
    }

    @Override
    public boolean notEmpty()
    {
        return !this.getDelegate().isEmpty();
    }

    @Override
    public T getFirst()
    {
        return Iterate.getFirst(this.getDelegate());
    }

    @Override
    public T getLast()
    {
        return Iterate.getLast(this.getDelegate());
    }

    @Override
    public T getOnly()
    {
        Iterator<T> iterator = this.getDelegate().iterator();

        if (!iterator.hasNext())
        {
            throw new IllegalStateException("Size must be 1 but was 0");
        }

        T result = iterator.next();
        if (iterator.hasNext())
        {
            throw new IllegalStateException("Size must be 1 but was greater than 1");
        }

        return result;
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
        Iterate.forEach(this.getDelegate(), procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        Iterate.forEachWithIndex(this.getDelegate(), objectIntProcedure);
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        return Iterate.removeIf(this.getDelegate(), predicate);
    }

    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.removeIfWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return Iterate.detect(this.getDelegate(), predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.detectWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return Iterate.detectOptional(this.getDelegate(), predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.detectWithOptional(this.getDelegate(), predicate, parameter);
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        T result = this.detect(predicate);
        return result == null ? function.value() : result;
    }

    @Override
    public <P> T detectWithIfNone(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Function0<? extends T> function)
    {
        T result = this.detectWith(predicate, parameter);
        return result == null ? function.value() : result;
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return Iterate.min(this, comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return Iterate.max(this, comparator);
    }

    @Override
    public T min()
    {
        return Iterate.min(this);
    }

    @Override
    public T max()
    {
        return Iterate.max(this);
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return IterableIterate.minBy(this, function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return IterableIterate.maxBy(this, function);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return Iterate.count(this.getDelegate(), predicate);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return Iterate.anySatisfy(this.getDelegate(), predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return Iterate.allSatisfy(this.getDelegate(), predicate);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return Iterate.noneSatisfy(this.getDelegate(), predicate);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return Iterate.injectInto(injectedValue, this.getDelegate(), function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        return Iterate.injectInto(injectedValue, this.getDelegate(), function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        return Iterate.injectInto(injectedValue, this.getDelegate(), function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        return Iterate.injectInto(injectedValue, this.getDelegate(), function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        return Iterate.injectInto(injectedValue, this.getDelegate(), function);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        return Iterate.sumOfInt(this.getDelegate(), function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        return Iterate.sumOfFloat(this.getDelegate(), function);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        return Iterate.sumOfLong(this.getDelegate(), function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        return Iterate.sumOfDouble(this.getDelegate(), function);
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
    public MutableCollection<T> select(Predicate<? super T> predicate)
    {
        return this.wrap(Iterate.select(this.getDelegate(), predicate));
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return Iterate.select(this.getDelegate(), predicate, target);
    }

    @Override
    public MutableCollection<T> reject(Predicate<? super T> predicate)
    {
        return this.wrap(Iterate.reject(this.getDelegate(), predicate));
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return Iterate.reject(this.getDelegate(), predicate, target);
    }

    @Override
    public <S> MutableCollection<S> selectInstancesOf(Class<S> clazz)
    {
        return this.wrap(Iterate.selectInstancesOf(this.getDelegate(), clazz));
    }

    @Override
    public <V> MutableCollection<V> collect(Function<? super T, ? extends V> function)
    {
        return this.wrap(Iterate.collect(this.getDelegate(), function));
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        return Iterate.collectBoolean(this.getDelegate(), booleanFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        return Iterate.collectByte(this.getDelegate(), byteFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        return Iterate.collectChar(this.getDelegate(), charFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        return Iterate.collectDouble(this.getDelegate(), doubleFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        return Iterate.collectFloat(this.getDelegate(), floatFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        return Iterate.collectInt(this.getDelegate(), intFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        return Iterate.collectLong(this.getDelegate(), longFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        return Iterate.collectShort(this.getDelegate(), shortFunction, target);
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return Iterate.collect(this.getDelegate(), function, target);
    }

    @Override
    public <V> MutableCollection<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.wrap(Iterate.flatCollect(this.getDelegate(), function));
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return Iterate.flatCollect(this.getDelegate(), function, target);
    }

    @Override
    public <V> MutableCollection<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return this.wrap(Iterate.collectIf(this.getDelegate(), predicate, function));
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        return Iterate.collectIf(this.getDelegate(), predicate, function, target);
    }

    @Override
    public <P> Twin<MutableList<T>> selectAndRejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return Iterate.selectAndRejectWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public PartitionMutableCollection<T> partition(Predicate<? super T> predicate)
    {
        return (PartitionMutableCollection<T>) Iterate.partition(this.getDelegate(), predicate);
    }

    @Override
    public <P> PartitionMutableCollection<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (PartitionMutableCollection<T>) Iterate.partitionWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public int size()
    {
        return this.getDelegate().size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.getDelegate().isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return this.getDelegate().contains(o);
    }

    @Override
    public Iterator<T> iterator()
    {
        return this.getDelegate().iterator();
    }

    @Override
    public Object[] toArray()
    {
        return this.getDelegate().toArray();
    }

    @Override
    public <E> E[] toArray(E[] a)
    {
        return this.getDelegate().toArray(a);
    }

    @Override
    public boolean add(T o)
    {
        return this.getDelegate().add(o);
    }

    @Override
    public boolean remove(Object o)
    {
        return this.getDelegate().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        return this.getDelegate().containsAll(collection);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return Iterate.allSatisfyWith(source, Predicates2.in(), this.getDelegate());
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return ArrayIterate.allSatisfyWith(elements, Predicates2.in(), this.getDelegate());
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        boolean result = false;
        for (T each : collection)
        {
            result |= this.add(each);
        }
        return result;
    }

    @Override
    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        return Iterate.addAllIterable(iterable, this);
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        int currentSize = this.size();
        this.removeIfWith(Predicates2.in(), collection);
        return currentSize != this.size();
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        return this.removeAll(CollectionAdapter.wrapSet(iterable));
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        int currentSize = this.size();
        this.removeIfWith(Predicates2.notIn(), collection);
        return currentSize != this.size();
    }

    @Override
    public boolean retainAllIterable(Iterable<?> iterable)
    {
        return this.retainAll(CollectionAdapter.wrapSet(iterable));
    }

    @Override
    public void clear()
    {
        this.getDelegate().clear();
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        Iterate.forEachWith(this.getDelegate(), procedure, parameter);
    }

    @Override
    public <P> MutableCollection<T> selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return this.wrap(Iterate.selectWith(this.getDelegate(), predicate, parameter));
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        return Iterate.selectWith(this.getDelegate(), predicate, parameter, targetCollection);
    }

    @Override
    public <P> MutableCollection<T> rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return this.wrap(Iterate.rejectWith(this.getDelegate(), predicate, parameter));
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        return Iterate.rejectWith(this.getDelegate(), predicate, parameter, targetCollection);
    }

    @Override
    public <P, V> MutableCollection<V> collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter)
    {
        return this.wrap(Iterate.collectWith(this.getDelegate(), function, parameter));
    }

    @Override
    public <P, A, R extends Collection<A>> R collectWith(
            Function2<? super T, ? super P, ? extends A> function,
            P parameter,
            R targetCollection)
    {
        return Iterate.collectWith(this.getDelegate(), function, parameter, targetCollection);
    }

    @Override
    public <IV, P> IV injectIntoWith(
            IV injectValue,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        return Iterate.injectIntoWith(injectValue, this.getDelegate(), function, parameter);
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        return Iterate.addAllTo(this.getDelegate(), target);
    }

    @Override
    public MutableList<T> toList()
    {
        return Lists.mutable.withAll(this.getDelegate());
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.toList().sortThis(comparator);
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        return TreeSortedSet.newSet(null, this);
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        return SortedSets.mutable.withAll(comparator, this);
    }

    @Override
    public MutableSet<T> toSet()
    {
        return Sets.mutable.withAll(this.getDelegate());
    }

    @Override
    public MutableBag<T> toBag()
    {
        return Bags.mutable.withAll(this.getDelegate());
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        return SortedBags.mutable.withAll(this.getDelegate());
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        return SortedBags.mutable.withAll(comparator, this.getDelegate());
    }

    @Override
    public <K, V> MutableMap<K, V> toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableMap<K, V> map = UnifiedMap.newMap(this.size());
        map.collectKeysAndValues(this.getDelegate(), keyFunction, valueFunction);
        return map;
    }

    @Override
    public <K, V, R extends Map<K, V>> R toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction,
            R target)
    {
        Iterate.forEach(this.getDelegate(), new MapCollectProcedure<>(target, keyFunction, valueFunction));
        return target;
    }

    @Override
    public <K, V> MutableSortedMap<K, V> toSortedMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return TreeSortedMap.<K, V>newMap().collectKeysAndValues(this.getDelegate(), keyFunction, valueFunction);
    }

    @Override
    public <K, V> MutableSortedMap<K, V> toSortedMap(
            Comparator<? super K> comparator,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return TreeSortedMap.<K, V>newMap(comparator).collectKeysAndValues(this.getDelegate(), keyFunction, valueFunction);
    }

    @Override
    public <K, V> MutableBiMap<K, V> toBiMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableBiMap<K, V> biMap = BiMaps.mutable.empty();
        Iterate.forEach(this.getDelegate(), new BiMapCollectProcedure<>(biMap, keyFunction, valueFunction));
        return biMap;
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        return LazyIterate.adapt(this);
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.countWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.anySatisfyWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.allSatisfyWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.noneSatisfyWith(this.getDelegate(), predicate, parameter);
    }

    @Override
    public String toString()
    {
        return this.makeString("[", ", ", "]");
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        IterableIterate.appendString(this, appendable, start, separator, end);
    }

    @Override
    public <V> MutableMultimap<V, T> groupBy(
            Function<? super T, ? extends V> function)
    {
        return Iterate.groupBy(this.getDelegate(), function);
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        return Iterate.groupBy(this.getDelegate(), function, target);
    }

    @Override
    public <V> MutableMultimap<V, T> groupByEach(
            Function<? super T, ? extends Iterable<V>> function)
    {
        return Iterate.groupByEach(this.getDelegate(), function);
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return Iterate.groupByEach(this.getDelegate(), function, target);
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return Iterate.groupByUniqueKey(this.getDelegate(), function);
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(
            Function<? super T, ? extends V> function,
            R target)
    {
        return Iterate.groupByUniqueKey(this.getDelegate(), function, target);
    }

    @Override
    public <S> MutableCollection<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.wrap(Iterate.zip(this.getDelegate(), that));
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return Iterate.zip(this.getDelegate(), that, target);
    }

    @Override
    public MutableCollection<Pair<T, Integer>> zipWithIndex()
    {
        return this.wrap(Iterate.zipWithIndex(this.getDelegate()));
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return Iterate.zipWithIndex(this.getDelegate(), target);
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        return MutableCollectionIterate.chunk(this, size);
    }
}
