/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;

import org.eclipse.collections.api.LazyBooleanIterable;
import org.eclipse.collections.api.LazyByteIterable;
import org.eclipse.collections.api.LazyCharIterable;
import org.eclipse.collections.api.LazyDoubleIterable;
import org.eclipse.collections.api.LazyFloatIterable;
import org.eclipse.collections.api.LazyIntIterable;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.LazyLongIterable;
import org.eclipse.collections.api.LazyShortIterable;
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
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.AbstractRichIterable;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.block.procedure.MaxByProcedure;
import org.eclipse.collections.impl.block.procedure.MaxComparatorProcedure;
import org.eclipse.collections.impl.block.procedure.MaxProcedure;
import org.eclipse.collections.impl.block.procedure.MinByProcedure;
import org.eclipse.collections.impl.block.procedure.MinComparatorProcedure;
import org.eclipse.collections.impl.block.procedure.MinProcedure;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.lazy.primitive.CollectBooleanIterable;
import org.eclipse.collections.impl.lazy.primitive.CollectByteIterable;
import org.eclipse.collections.impl.lazy.primitive.CollectCharIterable;
import org.eclipse.collections.impl.lazy.primitive.CollectDoubleIterable;
import org.eclipse.collections.impl.lazy.primitive.CollectFloatIterable;
import org.eclipse.collections.impl.lazy.primitive.CollectIntIterable;
import org.eclipse.collections.impl.lazy.primitive.CollectLongIterable;
import org.eclipse.collections.impl.lazy.primitive.CollectShortIterable;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * AbstractLazyIterable provides a base from which deferred iterables such as SelectIterable,
 * RejectIterable and CollectIterable can be derived.
 */
public abstract class AbstractLazyIterable<T>
        extends AbstractRichIterable<T>
        implements LazyIterable<T>
{
    @Override
    public LazyIterable<T> asLazy()
    {
        return this;
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        this.forEachWith(Procedures2.addToCollection(), target);
        return target;
    }

    @Override
    public int size()
    {
        return this.count(Predicates.alwaysTrue());
    }

    @Override
    public boolean isEmpty()
    {
        return !this.anySatisfy(Predicates.alwaysTrue());
    }

    @Override
    public T getFirst()
    {
        return this.detect(Predicates.alwaysTrue());
    }

    @Override
    public T getLast()
    {
        T[] result = (T[]) new Object[1];
        this.each(each -> result[0] = each);
        return result[0];
    }

    @Override
    public T getOnly()
    {
        Iterator<T> iterator = this.iterator();

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
    public LazyIterable<T> select(Predicate<? super T> predicate)
    {
        return LazyIterate.select(this, predicate);
    }

    @Override
    public <P> LazyIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return LazyIterate.select(this, Predicates.bind(predicate, parameter));
    }

    @Override
    public LazyIterable<T> reject(Predicate<? super T> predicate)
    {
        return LazyIterate.reject(this, predicate);
    }

    @Override
    public <P> LazyIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return LazyIterate.reject(this, Predicates.bind(predicate, parameter));
    }

    @Override
    public PartitionMutableList<T> partition(Predicate<? super T> predicate)
    {
        PartitionMutableList<T> partitionMutableList = new PartitionFastList<>();
        this.forEach(new PartitionProcedure<>(predicate, partitionMutableList));
        return partitionMutableList;
    }

    @Override
    public <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.partition(Predicates.bind(predicate, parameter));
    }

    @Override
    public <S> LazyIterable<S> selectInstancesOf(Class<S> clazz)
    {
        return LazyIterate.selectInstancesOf(this, clazz);
    }

    @Override
    public <V> LazyIterable<V> collect(Function<? super T, ? extends V> function)
    {
        return LazyIterate.collect(this, function);
    }

    @Override
    public LazyBooleanIterable collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return new CollectBooleanIterable<>(this, booleanFunction);
    }

    @Override
    public LazyByteIterable collectByte(ByteFunction<? super T> byteFunction)
    {
        return new CollectByteIterable<>(this, byteFunction);
    }

    @Override
    public LazyCharIterable collectChar(CharFunction<? super T> charFunction)
    {
        return new CollectCharIterable<>(this, charFunction);
    }

    @Override
    public LazyDoubleIterable collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return new CollectDoubleIterable<>(this, doubleFunction);
    }

    @Override
    public LazyFloatIterable collectFloat(FloatFunction<? super T> floatFunction)
    {
        return new CollectFloatIterable<>(this, floatFunction);
    }

    @Override
    public LazyIntIterable collectInt(IntFunction<? super T> intFunction)
    {
        return new CollectIntIterable<>(this, intFunction);
    }

    @Override
    public LazyLongIterable collectLong(LongFunction<? super T> longFunction)
    {
        return new CollectLongIterable<>(this, longFunction);
    }

    @Override
    public LazyShortIterable collectShort(ShortFunction<? super T> shortFunction)
    {
        return new CollectShortIterable<>(this, shortFunction);
    }

    @Override
    public <P, V> LazyIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return LazyIterate.collect(this, Functions.bind(function, parameter));
    }

    @Override
    public <V> LazyIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return LazyIterate.flatCollect(this, function);
    }

    @Override
    public LazyIterable<T> concatenate(Iterable<T> iterable)
    {
        return LazyIterate.concatenate(this, iterable);
    }

    @Override
    public <V> LazyIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return LazyIterate.collectIf(this, predicate, function);
    }

    @Override
    public LazyIterable<T> take(int count)
    {
        return LazyIterate.take(this, count);
    }

    @Override
    public LazyIterable<T> drop(int count)
    {
        return LazyIterate.drop(this, count);
    }

    @Override
    public LazyIterable<T> takeWhile(Predicate<? super T> predicate)
    {
        return LazyIterate.takeWhile(this, predicate);
    }

    @Override
    public LazyIterable<T> dropWhile(Predicate<? super T> predicate)
    {
        return LazyIterate.dropWhile(this, predicate);
    }

    @Override
    public LazyIterable<T> distinct()
    {
        return LazyIterate.distinct(this);
    }

    public MutableStack<T> toStack()
    {
        return ArrayStack.newStack(this);
    }

    @Override
    public <V> Multimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, FastListMultimap.newMultimap());
    }

    @Override
    public <V> Multimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, FastListMultimap.newMultimap());
    }

    @Override
    public <V> MapIterable<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.groupByUniqueKey(function, UnifiedMap.newMap(this.size()));
    }

    @Override
    public <S> LazyIterable<Pair<T, S>> zip(Iterable<S> that)
    {
        return LazyIterate.zip(this, that);
    }

    @Override
    public LazyIterable<Pair<T, Integer>> zipWithIndex()
    {
        return LazyIterate.zipWithIndex(this);
    }

    @Override
    public LazyIterable<RichIterable<T>> chunk(int size)
    {
        return LazyIterate.chunk(this, size);
    }

    @Override
    public LazyIterable<T> tap(Procedure<? super T> procedure)
    {
        return LazyIterate.tap(this, procedure);
    }

    @Override
    public <V> ObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function));
    }

    @Override
    public <V> ObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function));
    }

    @Override
    public <V> ObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function));
    }

    @Override
    public <V> ObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function));
    }

    @Override
    public Optional<T> minOptional(Comparator<? super T> comparator)
    {
        MinComparatorProcedure<T> minComparatorProcedure = new MinComparatorProcedure<>(comparator);
        this.forEach(minComparatorProcedure);
        return minComparatorProcedure.getResultOptional();
    }

    @Override
    public Optional<T> maxOptional(Comparator<? super T> comparator)
    {
        MaxComparatorProcedure<T> maxComparatorProcedure = new MaxComparatorProcedure<>(comparator);
        this.forEach(maxComparatorProcedure);
        return maxComparatorProcedure.getResultOptional();
    }

    @Override
    public Optional<T> minOptional()
    {
        MinProcedure<T> minProcedure = new MinProcedure<>();
        this.forEach(minProcedure);
        return minProcedure.getResultOptional();
    }

    @Override
    public Optional<T> maxOptional()
    {
        MaxProcedure<T> maxProcedure = new MaxProcedure<>();
        this.forEach(maxProcedure);
        return maxProcedure.getResultOptional();
    }

    @Override
    public <V extends Comparable<? super V>> Optional<T> minByOptional(Function<? super T, ? extends V> function)
    {
        MinByProcedure<T, V> minByProcedure = new MinByProcedure<>(function);
        this.forEach(minByProcedure);
        return minByProcedure.getResultOptional();
    }

    @Override
    public <V extends Comparable<? super V>> Optional<T> maxByOptional(Function<? super T, ? extends V> function)
    {
        MaxByProcedure<T, V> maxByProcedure = new MaxByProcedure<>(function);
        this.forEach(maxByProcedure);
        return maxByProcedure.getResultOptional();
    }
}
