/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.internal;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.HashingStrategy;
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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * The IterableIterate class provides a few of the methods from the Smalltalk Collection Protocol for use with general
 * Collection classes. This includes do:, select:, reject:, collect:, inject:into:, detect:, detect:ifNone:, anySatisfy:
 * and allSatisfy:
 */
public final class IterableIterate
{
    private IterableIterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * @see Iterate#select(Iterable, Predicate)
     */
    public static <T> MutableList<T> select(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IterableIterate.select(iterable, predicate, FastList.newList());
    }

    /**
     * @see Iterate#selectAndRejectWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> Twin<MutableList<T>> selectAndRejectWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        return IteratorIterate.selectAndRejectWith(iterable.iterator(), predicate, injectedValue);
    }

    /**
     * @see Iterate#partition(Iterable, Predicate)
     */
    public static <T> PartitionMutableList<T> partition(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IteratorIterate.partition(iterable.iterator(), predicate);
    }

    /**
     * @see Iterate#partitionWith(Iterable, Predicate2, Object)
     * @since 5.0
     */
    public static <T, P> PartitionMutableList<T> partitionWith(Iterable<T> iterable, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IteratorIterate.partitionWith(iterable.iterator(), predicate, parameter);
    }

    public static <T, R extends PartitionMutableCollection<T>> R partitionWhile(Iterable<T> iterable, Predicate<? super T> predicate, R target)
    {
        return IteratorIterate.partitionWhile(iterable.iterator(), predicate, target);
    }

    public static <T, R extends MutableCollection<T>> R takeWhile(Iterable<T> iterable, Predicate<? super T> predicate, R target)
    {
        return IteratorIterate.takeWhile(iterable.iterator(), predicate, target);
    }

    public static <T, R extends MutableCollection<T>> R dropWhile(Iterable<T> iterable, Predicate<? super T> predicate, R target)
    {
        return IteratorIterate.dropWhile(iterable.iterator(), predicate, target);
    }

    /**
     * @see Iterate#count(Iterable, Predicate)
     */
    public static <T> int count(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IteratorIterate.count(iterable.iterator(), predicate);
    }

    /**
     * @see Iterate#countWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> int countWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        return IteratorIterate.countWith(iterable.iterator(), predicate, injectedValue);
    }

    /**
     * @see Iterate#collectIf(Iterable, Predicate, Function)
     */
    public static <T, V> MutableList<V> collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return IterableIterate.collectIf(iterable, predicate, function, FastList.newList());
    }

    public static boolean isEmpty(Iterable<?> iterable)
    {
        return !iterable.iterator().hasNext();
    }

    public static boolean notEmpty(Iterable<?> iterable)
    {
        return !IterableIterate.isEmpty(iterable);
    }

    public static <T> T getFirst(Iterable<T> iterable)
    {
        Iterator<T> iterator = iterable.iterator();
        if (iterator.hasNext())
        {
            return iterator.next();
        }
        return null;
    }

    public static <T> T getLast(Iterable<T> iterable)
    {
        T last = null;
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext())
        {
            last = iterator.next();
        }
        return last;
    }

    /**
     * @see Iterate#select(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R select(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        return IteratorIterate.select(iterable.iterator(), predicate, targetCollection);
    }

    /**
     * @see Iterate#selectWith(Iterable, Predicate2, Object, Collection)
     */
    public static <T, P, R extends Collection<T>> R selectWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P injectedValue,
            R targetCollection)
    {
        return IteratorIterate.selectWith(iterable.iterator(), predicate, injectedValue, targetCollection);
    }

    /**
     * @see Iterate#selectInstancesOf(Iterable, Class)
     */
    public static <T> MutableList<T> selectInstancesOf(
            Iterable<?> iterable,
            Class<T> clazz)
    {
        FastList<T> result = FastList.newList();
        IterableIterate.selectInstancesOf(iterable, clazz, result);
        result.trimToSize();
        return result;
    }

    /**
     * @see Iterate#selectInstancesOf(Iterable, Class)
     */
    public static <T, R extends Collection<T>> R selectInstancesOf(
            Iterable<?> iterable,
            Class<T> clazz,
            R targetCollection)
    {
        return IteratorIterate.selectInstancesOf(iterable.iterator(), clazz, targetCollection);
    }

    /**
     * @see Iterate#collectIf(Iterable, Predicate, Function, Collection)
     */
    public static <T, V, R extends Collection<V>> R collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R targetCollection)
    {
        return IteratorIterate.collectIf(iterable.iterator(), predicate, function, targetCollection);
    }

    /**
     * @see Iterate#reject(Iterable, Predicate)
     */
    public static <T> MutableList<T> reject(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IterableIterate.reject(iterable, predicate, FastList.newList());
    }

    /**
     * @see Iterate#reject(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        return IteratorIterate.reject(iterable.iterator(), predicate, targetCollection);
    }

    /**
     * @see Iterate#rejectWith(Iterable, Predicate2, Object, Collection)
     */
    public static <T, P, R extends Collection<T>> R rejectWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        return IteratorIterate.rejectWith(iterable.iterator(), predicate, parameter, targetCollection);
    }

    /**
     * @see Iterate#collect(Iterable, Function)
     */
    public static <T, V> MutableList<V> collect(
            Iterable<T> iterable,
            Function<? super T, ? extends V> function)
    {
        return IterableIterate.collect(iterable, function, FastList.newList());
    }

    /**
     * @see Iterate#collect(Iterable, Function, Collection)
     */
    public static <T, V, R extends Collection<V>> R collect(
            Iterable<T> iterable,
            Function<? super T, ? extends V> function,
            R targetCollection)
    {
        return IteratorIterate.collect(iterable.iterator(), function, targetCollection);
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction)
     */
    public static <T> MutableBooleanCollection collectBoolean(
            Iterable<T> iterable,
            BooleanFunction<? super T> booleanFunction)
    {
        return IteratorIterate.collectBoolean(iterable.iterator(), booleanFunction);
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction, MutableBooleanCollection)
     */
    public static <T, R extends MutableBooleanCollection> R collectBoolean(Iterable<T> iterable, BooleanFunction<? super T> booleanFunction, R target)
    {
        return IteratorIterate.collectBoolean(iterable.iterator(), booleanFunction, target);
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction)
     */
    public static <T> MutableByteCollection collectByte(
            Iterable<T> iterable,
            ByteFunction<? super T> byteFunction)
    {
        return IteratorIterate.collectByte(iterable.iterator(), byteFunction);
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction, MutableByteCollection)
     */
    public static <T, R extends MutableByteCollection> R collectByte(Iterable<T> iterable, ByteFunction<? super T> byteFunction, R target)
    {
        return IteratorIterate.collectByte(iterable.iterator(), byteFunction, target);
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction)
     */
    public static <T> MutableCharCollection collectChar(
            Iterable<T> iterable,
            CharFunction<? super T> charFunction)
    {
        return IteratorIterate.collectChar(iterable.iterator(), charFunction);
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction, MutableCharCollection)
     */
    public static <T, R extends MutableCharCollection> R collectChar(Iterable<T> iterable, CharFunction<? super T> charFunction, R target)
    {
        return IteratorIterate.collectChar(iterable.iterator(), charFunction, target);
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction)
     */
    public static <T> MutableDoubleCollection collectDouble(
            Iterable<T> iterable,
            DoubleFunction<? super T> doubleFunction)
    {
        return IteratorIterate.collectDouble(iterable.iterator(), doubleFunction);
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction, MutableDoubleCollection)
     */
    public static <T, R extends MutableDoubleCollection> R collectDouble(Iterable<T> iterable, DoubleFunction<? super T> doubleFunction, R target)
    {
        return IteratorIterate.collectDouble(iterable.iterator(), doubleFunction, target);
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction)
     */
    public static <T> MutableFloatCollection collectFloat(
            Iterable<T> iterable,
            FloatFunction<? super T> floatFunction)
    {
        return IteratorIterate.collectFloat(iterable.iterator(), floatFunction);
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction, MutableFloatCollection)
     */
    public static <T, R extends MutableFloatCollection> R collectFloat(Iterable<T> iterable, FloatFunction<? super T> floatFunction, R target)
    {
        return IteratorIterate.collectFloat(iterable.iterator(), floatFunction, target);
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction)
     */
    public static <T> MutableIntCollection collectInt(
            Iterable<T> iterable,
            IntFunction<? super T> intFunction)
    {
        return IteratorIterate.collectInt(iterable.iterator(), intFunction);
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction, MutableIntCollection)
     */
    public static <T, R extends MutableIntCollection> R collectInt(Iterable<T> iterable, IntFunction<? super T> intFunction, R target)
    {
        return IteratorIterate.collectInt(iterable.iterator(), intFunction, target);
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction)
     */
    public static <T> MutableLongCollection collectLong(
            Iterable<T> iterable,
            LongFunction<? super T> longFunction)
    {
        return IteratorIterate.collectLong(iterable.iterator(), longFunction);
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction, MutableLongCollection)
     */
    public static <T, R extends MutableLongCollection> R collectLong(Iterable<T> iterable, LongFunction<? super T> longFunction, R target)
    {
        return IteratorIterate.collectLong(iterable.iterator(), longFunction, target);
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction)
     */
    public static <T> MutableShortCollection collectShort(
            Iterable<T> iterable,
            ShortFunction<? super T> shortFunction)
    {
        return IteratorIterate.collectShort(iterable.iterator(), shortFunction);
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction, MutableShortCollection)
     */
    public static <T, R extends MutableShortCollection> R collectShort(Iterable<T> iterable, ShortFunction<? super T> shortFunction, R target)
    {
        return IteratorIterate.collectShort(iterable.iterator(), shortFunction, target);
    }

    /**
     * @see Iterate#flatCollect(Iterable, Function)
     */
    public static <T, V> MutableList<V> flatCollect(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function)
    {
        return IterableIterate.flatCollect(iterable, function, FastList.newList());
    }

    /**
     * @see Iterate#flatCollect(Iterable, Function, Collection)
     */
    public static <T, V, R extends Collection<V>> R flatCollect(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function,
            R targetCollection)
    {
        return IteratorIterate.flatCollect(iterable.iterator(), function, targetCollection);
    }

    /**
     * @see Iterate#forEach(Iterable, Procedure)
     */
    public static <T> void forEach(Iterable<T> iterable, Procedure<? super T> procedure)
    {
        IteratorIterate.forEach(iterable.iterator(), procedure);
    }

    /**
     * @see Iterate#forEachWithIndex(Iterable, ObjectIntProcedure)
     */
    public static <T> void forEachWithIndex(Iterable<T> iterable, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        IteratorIterate.forEachWithIndex(iterable.iterator(), objectIntProcedure);
    }

    /**
     * @see Iterate#injectInto(Object, Iterable, Function2)
     */
    public static <T, IV> IV injectInto(
            IV injectValue,
            Iterable<T> iterable,
            Function2<? super IV, ? super T, ? extends IV> function)
    {
        return IteratorIterate.injectInto(injectValue, iterable.iterator(), function);
    }

    /**
     * @see Iterate#injectInto(int, Iterable, IntObjectToIntFunction)
     */
    public static <T> int injectInto(
            int injectValue,
            Iterable<T> iterable,
            IntObjectToIntFunction<? super T> function)
    {
        return IteratorIterate.injectInto(injectValue, iterable.iterator(), function);
    }

    /**
     * @see Iterate#injectInto(long, Iterable, LongObjectToLongFunction)
     */
    public static <T> long injectInto(
            long injectValue,
            Iterable<T> iterable,
            LongObjectToLongFunction<? super T> function)
    {
        return IteratorIterate.injectInto(injectValue, iterable.iterator(), function);
    }

    /**
     * @see Iterate#injectInto(double, Iterable, DoubleObjectToDoubleFunction)
     */
    public static <T> double injectInto(
            double injectValue,
            Iterable<T> iterable,
            DoubleObjectToDoubleFunction<? super T> function)
    {
        return IteratorIterate.injectInto(injectValue, iterable.iterator(), function);
    }

    /**
     * @see Iterate#injectInto(float, Iterable, FloatObjectToFloatFunction)
     */
    public static <T> float injectInto(
            float injectValue,
            Iterable<T> iterable,
            FloatObjectToFloatFunction<? super T> function)
    {
        return IteratorIterate.injectInto(injectValue, iterable.iterator(), function);
    }

    /**
     * @deprecated in 7.0.
     */
    @Deprecated
    public static <T, R extends List<T>> R distinct(
            Iterable<T> iterable,
            R targetCollection)
    {
        return IteratorIterate.distinct(iterable.iterator(), targetCollection);
    }

    /**
     * @since 7.0.
     */
    public static <T> MutableList<T> distinct(Iterable<T> iterable)
    {
        return IteratorIterate.distinct(iterable.iterator());
    }

    /**
     * @since 7.0.
     */
    public static <T> MutableList<T> distinct(Iterable<T> iterable, HashingStrategy<? super T> hashingStrategy)
    {
        return IteratorIterate.distinct(iterable.iterator(), hashingStrategy);
    }

    /**
     * @see Iterate#sumOfInt(Iterable, IntFunction)
     */
    public static <T> long sumOfInt(Iterable<T> iterable, IntFunction<? super T> function)
    {
        return IteratorIterate.sumOfInt(iterable.iterator(), function);
    }

    /**
     * @see Iterate#sumOfLong(Iterable, LongFunction)
     */
    public static <T> long sumOfLong(Iterable<T> iterable, LongFunction<? super T> function)
    {
        return IteratorIterate.sumOfLong(iterable.iterator(), function);
    }

    /**
     * @see Iterate#sumOfFloat(Iterable, FloatFunction)
     */
    public static <T> double sumOfFloat(Iterable<T> iterable, FloatFunction<? super T> function)
    {
        return IteratorIterate.sumOfFloat(iterable.iterator(), function);
    }

    /**
     * @see Iterate#sumOfDouble(Iterable, DoubleFunction)
     */
    public static <T> double sumOfDouble(Iterable<T> iterable, DoubleFunction<? super T> function)
    {
        return IteratorIterate.sumOfDouble(iterable.iterator(), function);
    }

    public static <T> BigDecimal sumOfBigDecimal(Iterable<T> iterable, Function<? super T, BigDecimal> function)
    {
        return IteratorIterate.sumOfBigDecimal(iterable.iterator(), function);
    }

    public static <T> BigInteger sumOfBigInteger(Iterable<T> iterable, Function<? super T, BigInteger> function)
    {
        return IteratorIterate.sumOfBigInteger(iterable.iterator(), function);
    }

    public static <V, T> MutableMap<V, BigDecimal> sumByBigDecimal(
            Iterable<T> iterable,
            Function<? super T, ? extends V> groupBy,
            Function<? super T, BigDecimal> function)
    {
        return IteratorIterate.sumByBigDecimal(iterable.iterator(), groupBy, function);
    }

    public static <V, T> MutableMap<V, BigInteger> sumByBigInteger(
            Iterable<T> iterable,
            Function<? super T, ? extends V> groupBy,
            Function<? super T, BigInteger> function)
    {
        return IteratorIterate.sumByBigInteger(iterable.iterator(), groupBy, function);
    }

    public static <V, T> ObjectLongMap<V> sumByInt(
            Iterable<T> iterable,
            Function<? super T, ? extends V> groupBy,
            IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return IterableIterate.injectInto(result, iterable, PrimitiveFunctions.sumByIntFunction(groupBy, function));
    }

    public static <V, T> ObjectLongMap<V> sumByLong(
            Iterable<T> iterable,
            Function<? super T, ? extends V> groupBy,
            LongFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return IterableIterate.injectInto(result, iterable, PrimitiveFunctions.sumByLongFunction(groupBy, function));
    }

    public static <V, T> ObjectDoubleMap<V> sumByFloat(
            Iterable<T> iterable,
            Function<? super T, ? extends V> groupBy,
            FloatFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return IterableIterate.injectInto(result, iterable, PrimitiveFunctions.sumByFloatFunction(groupBy, function));
    }

    public static <V, T> ObjectDoubleMap<V> sumByDouble(
            Iterable<T> iterable,
            Function<? super T, ? extends V> groupBy,
            DoubleFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return IterableIterate.injectInto(result, iterable, PrimitiveFunctions.sumByDoubleFunction(groupBy, function));
    }

    /**
     * @see Iterate#injectIntoWith(Object, Iterable, Function3, Object)
     */
    public static <T, IV, P> IV injectIntoWith(
            IV injectValue,
            Iterable<T> iterable,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        return IteratorIterate.injectIntoWith(injectValue, iterable.iterator(), function, parameter);
    }

    public static <T> boolean anySatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IteratorIterate.anySatisfy(iterable.iterator(), predicate);
    }

    public static <T, P> boolean anySatisfyWith(Iterable<T> iterable, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IteratorIterate.anySatisfyWith(iterable.iterator(), predicate, parameter);
    }

    public static <T> boolean allSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IteratorIterate.allSatisfy(iterable.iterator(), predicate);
    }

    public static <T, P> boolean allSatisfyWith(Iterable<T> iterable, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IteratorIterate.allSatisfyWith(iterable.iterator(), predicate, parameter);
    }

    public static <T> boolean noneSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IteratorIterate.noneSatisfy(iterable.iterator(), predicate);
    }

    public static <T, P> boolean noneSatisfyWith(Iterable<T> iterable, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IteratorIterate.noneSatisfyWith(iterable.iterator(), predicate, parameter);
    }

    public static <T> T detect(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IteratorIterate.detect(iterable.iterator(), predicate);
    }

    public static <T, P> T detectWith(Iterable<T> iterable, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IteratorIterate.detectWith(iterable.iterator(), predicate, parameter);
    }

    public static <T> Optional<T> detectOptional(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IteratorIterate.detectOptional(iterable.iterator(), predicate);
    }

    public static <T, P> Optional<T> detectWithOptional(Iterable<T> iterable, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IteratorIterate.detectWithOptional(iterable.iterator(), predicate, parameter);
    }

    /**
     * @see Iterate#removeIf(Iterable, Predicate)
     */
    public static <T> boolean removeIf(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IterableIterate.removeIf(iterable, (java.util.function.Predicate<? super T>) predicate);
    }

    /**
     * @see Iterate#removeIf(Iterable, java.util.function.Predicate)
     */
    public static <T> boolean removeIf(Iterable<T> iterable, java.util.function.Predicate<? super T> predicate)
    {
        return IteratorIterate.removeIf(iterable.iterator(), predicate);
    }

    /**
     * @see Iterate#removeIfWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean removeIfWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return IteratorIterate.removeIfWith(iterable.iterator(), predicate, parameter);
    }

    public static <T> boolean removeIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Procedure<? super T> procedure)
    {
        return IteratorIterate.removeIf(iterable.iterator(), predicate, procedure);
    }

    public static <T, P> boolean removeIfWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Procedure<? super T> procedure)
    {
        return IteratorIterate.removeIfWith(iterable.iterator(), predicate, parameter, procedure);
    }

    /**
     * @see Iterate#detectIndex(Iterable, Predicate)
     */
    public static <T> int detectIndex(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return IteratorIterate.detectIndex(iterable.iterator(), predicate);
    }

    /**
     * @see Iterate#detectIndexWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> int detectIndexWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        return IteratorIterate.detectIndexWith(iterable.iterator(), predicate, injectedValue);
    }

    /**
     * @see Iterate#forEachWith(Iterable, Procedure2, Object)
     */
    public static <T, P> void forEachWith(
            Iterable<T> iterable,
            Procedure2<? super T, ? super P> procedure,
            P parameter)
    {
        IteratorIterate.forEachWith(iterable.iterator(), procedure, parameter);
    }

    /**
     * @see Iterate#collectWith(Iterable, Function2, Object)
     */
    public static <T, P, V> MutableList<V> collectWith(
            Iterable<T> iterable,
            Function2<? super T, ? super P, ? extends V> function,
            P parameter)
    {
        return IterableIterate.collectWith(iterable, function, parameter, FastList.newList());
    }

    /**
     * @see Iterate#collectWith(Iterable, Function2, Object, Collection)
     */
    public static <T, P, A, R extends Collection<A>> R collectWith(
            Iterable<T> iterable,
            Function2<? super T, ? super P, ? extends A> function,
            P parameter,
            R targetCollection)
    {
        return IteratorIterate.collectWith(iterable.iterator(), function, parameter, targetCollection);
    }

    /**
     * @see Iterate#take(Iterable, int)
     */
    public static <T> Collection<T> take(Iterable<T> iterable, int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return IterableIterate.take(iterable, count, FastList.newList());
    }

    /**
     * @see Iterate#take(Iterable, int)
     */
    public static <T, R extends Collection<T>> R take(Iterable<T> iterable, int count, R targetCollection)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        int countCopy = count;
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext() && countCopy-- > 0)
        {
            targetCollection.add(iterator.next());
        }
        return targetCollection;
    }

    /**
     * @see Iterate#drop(Iterable, int)
     */
    public static <T> Collection<T> drop(Iterable<T> list, int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return IterableIterate.drop(list, count, FastList.newList());
    }

    /**
     * @see Iterate#drop(Iterable, int)
     */
    public static <T, R extends Collection<T>> R drop(Iterable<T> iterable, int count, R targetCollection)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        Iterator<T> iterator = iterable.iterator();
        for (int i = 0; iterator.hasNext(); i++)
        {
            T element = iterator.next();
            if (i >= count)
            {
                targetCollection.add(element);
            }
        }
        return targetCollection;
    }

    /**
     * @see RichIterable#appendString(Appendable, String, String, String)
     */
    public static <T> void appendString(
            Iterable<T> iterable,
            Appendable appendable,
            String start,
            String separator,
            String end)
    {
        try
        {
            appendable.append(start);

            Iterator<T> iterator = iterable.iterator();
            if (iterator.hasNext())
            {
                appendable.append(IterableIterate.stringValueOfItem(iterable, iterator.next()));
                while (iterator.hasNext())
                {
                    appendable.append(separator);
                    appendable.append(IterableIterate.stringValueOfItem(iterable, iterator.next()));
                }
            }

            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see Iterate#groupBy(Iterable, Function)
     */
    public static <T, V> FastListMultimap<V, T> groupBy(
            Iterable<T> iterable,
            Function<? super T, ? extends V> function)
    {
        return IterableIterate.groupBy(iterable, function, new FastListMultimap<>());
    }

    /**
     * @see Iterate#groupBy(Iterable, Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupBy(
            Iterable<T> iterable,
            Function<? super T, ? extends V> function,
            R target)
    {
        return IteratorIterate.groupBy(iterable.iterator(), function, target);
    }

    /**
     * @see Iterate#groupByEach(Iterable, Function)
     */
    public static <T, V> FastListMultimap<V, T> groupByEach(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function)
    {
        return IterableIterate.groupByEach(iterable, function, new FastListMultimap<>());
    }

    /**
     * @see Iterate#groupByEach(Iterable, Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupByEach(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return IteratorIterate.groupByEach(iterable.iterator(), function, target);
    }

    /**
     * @see Iterate#groupByUniqueKey(Iterable, Function)
     */
    public static <K, T> MutableMap<K, T> groupByUniqueKey(
            Iterable<T> iterable,
            Function<? super T, ? extends K> function)
    {
        return IterableIterate.groupByUniqueKey(iterable, function, Maps.mutable.empty());
    }

    /**
     * @see Iterate#groupByUniqueKey(Iterable, Function, MutableMapIterable)
     */
    public static <K, T, R extends MutableMapIterable<K, T>> R groupByUniqueKey(
            Iterable<T> iterable,
            Function<? super T, ? extends K> function,
            R target)
    {
        return IteratorIterate.groupByUniqueKey(iterable.iterator(), function, target);
    }

    public static <T> T getOnly(Iterable<T> iterable)
    {
        Iterator<? extends T> iterator = iterable.iterator();
        if (!iterator.hasNext())
        {
            throw new IllegalArgumentException("Iterable is empty.");
        }

        T result = iterator.next();
        if (iterator.hasNext())
        {
            throw new IllegalArgumentException("Iterable has multiple values");
        }

        return result;
    }

    /**
     * @see Iterate#zip(Iterable, Iterable)
     */
    public static <X, Y> MutableList<Pair<X, Y>> zip(
            Iterable<X> xs,
            Iterable<Y> ys)
    {
        return IterableIterate.zip(xs, ys, FastList.newList());
    }

    /**
     * @see Iterate#zip(Iterable, Iterable, Collection)
     */
    public static <X, Y, R extends Collection<Pair<X, Y>>> R zip(
            Iterable<X> xs,
            Iterable<Y> ys,
            R target)
    {
        return IteratorIterate.zip(xs.iterator(), ys.iterator(), target);
    }

    /**
     * @see Iterate#zipWithIndex(Iterable)
     */
    public static <T> MutableList<Pair<T, Integer>> zipWithIndex(Iterable<T> iterable)
    {
        return IterableIterate.zipWithIndex(iterable, FastList.newList());
    }

    /**
     * @see Iterate#zipWithIndex(Iterable, Collection)
     */
    public static <T, R extends Collection<Pair<T, Integer>>> R zipWithIndex(Iterable<T> iterable, R target)
    {
        return IteratorIterate.zipWithIndex(iterable.iterator(), target);
    }

    /**
     * @see Iterate#chunk(Iterable, int)
     */
    public static <T> RichIterable<RichIterable<T>> chunk(Iterable<T> iterable, int size)
    {
        return IteratorIterate.chunk(iterable.iterator(), size);
    }

    public static <T, V extends Comparable<? super V>> T maxBy(Iterable<T> iterable, Function<? super T, ? extends V> function)
    {
        return IteratorIterate.maxBy(iterable.iterator(), function);
    }

    public static <T, V extends Comparable<? super V>> T minBy(Iterable<T> iterable, Function<? super T, ? extends V> function)
    {
        return IteratorIterate.minBy(iterable.iterator(), function);
    }

    /**
     * @see Iterate#min(Iterable, Comparator)
     */
    public static <T> T min(Iterable<T> iterable, Comparator<? super T> comparator)
    {
        return IteratorIterate.min(iterable.iterator(), comparator);
    }

    /**
     * @see Iterate#max(Iterable, Comparator)
     */
    public static <T> T max(Iterable<T> iterable, Comparator<? super T> comparator)
    {
        return IteratorIterate.max(iterable.iterator(), comparator);
    }

    /**
     * @see Iterate#min(Iterable)
     */
    public static <T> T min(Iterable<T> iterable)
    {
        return IteratorIterate.min(iterable.iterator(), Comparators.naturalOrder());
    }

    /**
     * @see Iterate#max(Iterable)
     */
    public static <T> T max(Iterable<T> iterable)
    {
        return IteratorIterate.max(iterable.iterator(), Comparators.naturalOrder());
    }

    public static <T> void forEach(Iterable<T> iterable, int from, int to, Procedure<? super T> procedure)
    {
        if (from < 0 || to < 0)
        {
            throw new IllegalArgumentException("Neither from nor to may be negative.");
        }

        Iterator<T> iterator = IteratorIterate.advanceIteratorTo(iterable.iterator(), from);
        int i = from;
        while (iterator.hasNext() && i <= to)
        {
            procedure.value(iterator.next());
            i++;
        }
    }

    public static <T> void forEachWithIndex(List<T> iterable, int from, int to, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        if (from < 0 || to < 0)
        {
            throw new IllegalArgumentException("Neither from nor to may be negative.");
        }

        Iterator<T> iterator = IteratorIterate.advanceIteratorTo(iterable.iterator(), from);
        int i = from;
        while (iterator.hasNext() && i <= to)
        {
            objectIntProcedure.value(iterator.next(), i++);
        }
    }

    public static <T> String stringValueOfItem(Iterable<T> iterable, T item)
    {
        return item == iterable
                ? "(this " + iterable.getClass().getSimpleName() + ')'
                : String.valueOf(item);
    }

    public static <T, K, V> MutableMap<K, V> aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        return IteratorIterate.aggregateBy(iterable.iterator(), groupBy, zeroValueFactory, mutatingAggregator);
    }

    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        return IteratorIterate.aggregateBy(iterable.iterator(), groupBy, zeroValueFactory, nonMutatingAggregator);
    }
}
