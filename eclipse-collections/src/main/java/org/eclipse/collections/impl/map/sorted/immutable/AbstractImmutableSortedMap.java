/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.sorted.immutable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
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
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.ByteLists;
import org.eclipse.collections.api.factory.primitive.CharLists;
import org.eclipse.collections.api.factory.primitive.DoubleLists;
import org.eclipse.collections.api.factory.primitive.FloatLists;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.factory.primitive.ShortLists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.ImmutableOrderedMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.procedure.PartitionPredicate2Procedure;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.AbstractMapIterable;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.utility.MapIterate;

public abstract class AbstractImmutableSortedMap<K, V>
        extends AbstractMapIterable<K, V>
        implements ImmutableSortedMap<K, V>, SortedMap<K, V>
{
    @Override
    public SortedMap<K, V> castToMap()
    {
        return this;
    }

    @Override
    public SortedMap<K, V> castToSortedMap()
    {
        return this;
    }

    @Override
    public MutableSortedMap<K, V> toSortedMap()
    {
        return TreeSortedMap.newMap(this);
    }

    @Override
    public ImmutableSortedMap<K, V> toImmutable()
    {
        return this;
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.valuesView().iterator();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call putAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function)
    {
        throw new UnsupportedOperationException("Cannot call replaceAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public ImmutableSortedMap<K, V> newWithKeyValue(K key, V value)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        sortedMap.put(key, value);
        return sortedMap.toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        for (Pair<? extends K, ? extends V> keyValuePair : keyValues)
        {
            sortedMap.put(keyValuePair.getOne(), keyValuePair.getTwo());
        }
        return sortedMap.toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> newWithMap(Map<? extends K, ? extends V> map)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        sortedMap.putAll(map);
        return sortedMap.toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> newWithMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        mapIterable.forEachKeyValue(sortedMap::put);
        return sortedMap.toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        for (Pair<? extends K, ? extends V> keyValuePair : keyValuePairs)
        {
            sortedMap.put(keyValuePair.getOne(), keyValuePair.getTwo());
        }
        return sortedMap.toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> newWithoutKey(K key)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        sortedMap.removeKey(key);
        return sortedMap.toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        for (K key : keys)
        {
            sortedMap.removeKey(key);
        }
        return sortedMap.toImmutable();
    }

    @Override
    public V put(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call put() on " + this.getClass().getSimpleName());
    }

    @Override
    public V remove(Object key)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call compute() on " + this.getClass().getSimpleName());
    }

    @Override
    public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> mappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call computeIfAbsent() on " + this.getClass().getSimpleName());
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        throw new UnsupportedOperationException("Cannot call computeIfPresent() on " + this.getClass().getSimpleName());
    }

    @Override
    public ImmutableSortedSetMultimap<V, K> flip()
    {
        return MapIterate.flip(this).toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public ImmutableList<V> select(Predicate<? super V> predicate)
    {
        return this.select(predicate, Lists.mutable.withInitialCapacity(this.size())).toImmutable();
    }

    @Override
    public <P> ImmutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public ImmutableSortedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        MutableSortedMap<K, V> selectedMap = SortedMaps.mutable.with(this.comparator());
        this.forEachKeyValue((key, value) ->
        {
            if (predicate.accept(key, value))
            {
                selectedMap.put(key, value);
            }
        });
        return selectedMap.toImmutable();
    }

    @Override
    public ImmutableList<V> reject(Predicate<? super V> predicate)
    {
        return this.reject(predicate, Lists.mutable.withInitialCapacity(this.size())).toImmutable();
    }

    @Override
    public <P> ImmutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public ImmutableSortedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        MutableSortedMap<K, V> rejectedMap = SortedMaps.mutable.with(this.comparator());
        this.forEachKeyValue((key, value) ->
        {
            if (!predicate.accept(key, value))
            {
                rejectedMap.put(key, value);
            }
        });
        return rejectedMap.toImmutable();
    }

    @Override
    public PartitionImmutableList<V> partition(Predicate<? super V> predicate)
    {
        PartitionMutableList<V> partitionMutableList = new PartitionFastList<>();
        this.forEach(new PartitionProcedure<>(predicate, partitionMutableList));
        return partitionMutableList.toImmutable();
    }

    @Override
    public <P> PartitionImmutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        PartitionMutableList<V> partitionMutableList = new PartitionFastList<>();
        this.forEach(new PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableList));
        return partitionMutableList.toImmutable();
    }

    @Override
    public <S> ImmutableList<S> selectInstancesOf(Class<S> clazz)
    {
        MutableList<S> result = FastList.newList(this.size());
        this.forEach(new SelectInstancesOfProcedure<>(clazz, result));
        return result.toImmutable();
    }

    @Override
    public <R> ImmutableList<R> collect(Function<? super V, ? extends R> function)
    {
        return this.collect(function, FastList.<R>newList(this.size())).toImmutable();
    }

    @Override
    public ImmutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        MutableBooleanList result = BooleanLists.mutable.withInitialCapacity(this.size());
        return this.collectBoolean(booleanFunction, result).toImmutable();
    }

    @Override
    public ImmutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        MutableByteList result = ByteLists.mutable.withInitialCapacity(this.size());
        return this.collectByte(byteFunction, result).toImmutable();
    }

    @Override
    public ImmutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        MutableCharList result = CharLists.mutable.withInitialCapacity(this.size());
        return this.collectChar(charFunction, result).toImmutable();
    }

    @Override
    public ImmutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        MutableDoubleList result = DoubleLists.mutable.withInitialCapacity(this.size());
        return this.collectDouble(doubleFunction, result).toImmutable();
    }

    @Override
    public ImmutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        MutableFloatList result = FloatLists.mutable.withInitialCapacity(this.size());
        return this.collectFloat(floatFunction, result).toImmutable();
    }

    @Override
    public ImmutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        MutableIntList result = IntLists.mutable.withInitialCapacity(this.size());
        return this.collectInt(intFunction, result).toImmutable();
    }

    @Override
    public ImmutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        MutableLongList result = LongLists.mutable.withInitialCapacity(this.size());
        return this.collectLong(longFunction, result).toImmutable();
    }

    @Override
    public ImmutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        MutableShortList result = ShortLists.mutable.withInitialCapacity(this.size());
        return this.collectShort(shortFunction, result).toImmutable();
    }

    @Override
    public <K2, V2> ImmutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        MutableMap<K2, V2> collectedMap = UnifiedMap.newMap(this.size());
        this.forEachKeyValue((key, value) -> collectedMap.add(function.value(key, value)));
        return collectedMap.toImmutable();
    }

    @Override
    public <P, VV> ImmutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    @Override
    public <R> ImmutableList<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function)
    {
        return this.collectIf(predicate, function, FastList.<R>newList(this.size())).toImmutable();
    }

    @Override
    public <R> ImmutableSortedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        MutableSortedMap<K, R> collectedMap = SortedMaps.mutable.with(this.comparator());
        this.forEachKeyValue((key, value) -> collectedMap.put(key, function.value(key, value)));
        return collectedMap.toImmutable();
    }

    @Override
    public <R> ImmutableOrderedMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        MutableOrderedMap<R, V> result = OrderedMapAdapter.adapt(new LinkedHashMap<>(this.size()));
        return MapIterate
                .collectKeysUnique(this, function, result)
                .toImmutable();
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return this.keyValuesView().detect(each -> predicate.accept(each.getOne(), each.getTwo()));
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        return this.keyValuesView().detectOptional(each -> predicate.accept(each.getOne(), each.getTwo()));
    }

    @Override
    public <R> ImmutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.flatCollect(function, Lists.mutable.withInitialCapacity(this.size())).toImmutable();
    }

    @Override
    public <S> ImmutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.zip(that, Lists.mutable.withInitialCapacity(this.size())).toImmutable();
    }

    @Override
    public ImmutableList<Pair<V, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(Lists.mutable.withInitialCapacity(this.size())).toImmutable();
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".subMap() not implemented yet");
    }

    @Override
    public SortedMap<K, V> headMap(K toKey)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".headMap() not implemented yet");
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".tailMap() not implemented yet");
    }

    @Override
    public <R> ImmutableListMultimap<R, V> groupBy(Function<? super V, ? extends R> function)
    {
        return this.groupBy(function, FastListMultimap.<R, V>newMultimap()).toImmutable();
    }

    @Override
    public <R> ImmutableListMultimap<R, V> groupByEach(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.groupByEach(function, FastListMultimap.newMultimap()).toImmutable();
    }

    @Override
    public <V1> ImmutableMap<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function)
    {
        return this.groupByUniqueKey(function, UnifiedMap.<V1, V>newMap(this.size())).toImmutable();
    }

    @Override
    public <K1, V1, V2> ImmutableMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        MutableMap<K1, V2> map = Maps.mutable.empty();
        this.forEachKeyValue((key, value) -> map.updateValueWith(
                keyFunction.valueOf(key),
                zeroValueFactory,
                nonMutatingAggregator,
                valueFunction.valueOf(value)));
        return map.toImmutable();
    }

    @Override
    public <V1> ImmutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        MutableObjectLongMap<V1> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function)).toImmutable();
    }

    @Override
    public <V1> ImmutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        MutableObjectDoubleMap<V1> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function)).toImmutable();
    }

    @Override
    public <V1> ImmutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        MutableObjectLongMap<V1> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function)).toImmutable();
    }

    @Override
    public <V1> ImmutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        MutableObjectDoubleMap<V1> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function)).toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> toReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toReversed() not implemented yet");
    }

    @Override
    public int detectLastIndex(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectLastIndex() not implemented yet");
    }

    @Override
    public ImmutableSortedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".takeWhile() not implemented yet");
    }

    @Override
    public ImmutableSortedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".dropWhile() not implemented yet");
    }

    @Override
    public PartitionImmutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".partitionWhile() not implemented yet");
    }

    @Override
    public ImmutableList<V> distinct()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".distinct() not implemented yet");
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super V, ? super S> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".corresponds() not implemented yet");
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super V> procedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEach() not implemented yet");
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super V> objectIntProcedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEachWithIndex() not implemented yet");
    }

    @Override
    public MutableStack<V> toStack()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toStack() not implemented yet");
    }

    @Override
    public int detectIndex(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectIndex() not implemented yet");
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1> ImmutableBag<V1> countBy(Function<? super V, ? extends V1> function)
    {
        return this.collect(function, Bags.mutable.<V1>empty()).toImmutable();
    }

    /**
     * @since 9.0
     */
    @Override
    public <V1, P> ImmutableBag<V1> countByWith(Function2<? super V, ? super P, ? extends V1> function, P parameter)
    {
        return this.collectWith(function, parameter, Bags.mutable.<V1>empty()).toImmutable();
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V1> ImmutableBag<V1> countByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return this.countByEach(function, Bags.mutable.empty()).toImmutable();
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action)
    {
        super.forEach(action);
    }
}
