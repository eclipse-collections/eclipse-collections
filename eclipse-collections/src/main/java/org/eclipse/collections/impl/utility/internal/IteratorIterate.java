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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.ByteLists;
import org.eclipse.collections.api.factory.primitive.CharLists;
import org.eclipse.collections.api.factory.primitive.DoubleLists;
import org.eclipse.collections.api.factory.primitive.FloatLists;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.factory.primitive.ShortLists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.Functions0;
import org.eclipse.collections.impl.block.procedure.MutatingAggregationProcedure;
import org.eclipse.collections.impl.block.procedure.NonMutatingAggregationProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * The IteratorIterate class implementations of the various iteration patterns for use with java.util.Iterator.
 */
public final class IteratorIterate
{
    private IteratorIterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * @see Iterate#selectAndRejectWith(Iterable, Predicate2, Object)
     */
    public static <T, P> Twin<MutableList<T>> selectAndRejectWith(
            Iterator<T> iterator,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        MutableList<T> positiveResult = Lists.mutable.empty();
        MutableList<T> negativeResult = Lists.mutable.empty();
        while (iterator.hasNext())
        {
            T item = iterator.next();
            (predicate.accept(item, parameter) ? positiveResult : negativeResult).add(item);
        }
        return Tuples.twin(positiveResult, negativeResult);
    }

    /**
     * @see Iterate#partition(Iterable, Predicate)
     */
    public static <T> PartitionMutableList<T> partition(Iterator<T> iterator, Predicate<? super T> predicate)
    {
        PartitionMutableList<T> result = new PartitionFastList<>();
        MutableList<T> selected = result.getSelected();
        MutableList<T> rejected = result.getRejected();

        while (iterator.hasNext())
        {
            T each = iterator.next();
            MutableList<T> bucket = predicate.accept(each) ? selected : rejected;
            bucket.add(each);
        }
        return result;
    }

    /**
     * @see Iterate#partitionWith(Iterable, Predicate2, Object)
     * @since 5.0
     */
    public static <T, P> PartitionMutableList<T> partitionWith(
            Iterator<T> iterator,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        PartitionMutableList<T> result = new PartitionFastList<>();
        MutableList<T> selected = result.getSelected();
        MutableList<T> rejected = result.getRejected();

        while (iterator.hasNext())
        {
            T each = iterator.next();
            MutableList<T> bucket = predicate.accept(each, parameter) ? selected : rejected;
            bucket.add(each);
        }
        return result;
    }

    public static <T, R extends PartitionMutableCollection<T>> R partitionWhile(Iterator<T> iterator, Predicate<? super T> predicate, R target)
    {
        MutableCollection<T> selected = target.getSelected();
        MutableCollection<T> rejected = target.getRejected();

        boolean partitionFound = false;
        while (iterator.hasNext() && !partitionFound)
        {
            T next = iterator.next();
            if (predicate.accept(next))
            {
                selected.add(next);
            }
            else
            {
                rejected.add(next);
                partitionFound = true;
            }
        }
        while (iterator.hasNext())
        {
            rejected.add(iterator.next());
        }
        return target;
    }

    public static <T, R extends MutableCollection<T>> R takeWhile(Iterator<T> iterator, Predicate<? super T> predicate, R target)
    {
        while (iterator.hasNext())
        {
            T next = iterator.next();
            if (predicate.accept(next))
            {
                target.add(next);
            }
            else
            {
                return target;
            }
        }
        return target;
    }

    public static <T, R extends MutableCollection<T>> R dropWhile(Iterator<T> iterator, Predicate<? super T> predicate, R target)
    {
        boolean partitionFound = false;
        while (iterator.hasNext() && !partitionFound)
        {
            T next = iterator.next();
            if (!predicate.accept(next))
            {
                target.add(next);
                partitionFound = true;
            }
        }
        while (iterator.hasNext())
        {
            target.add(iterator.next());
        }
        return target;
    }

    /**
     * @see Iterate#count(Iterable, Predicate)
     */
    public static <T> int count(Iterator<T> iterator, Predicate<? super T> predicate)
    {
        int count = 0;
        while (iterator.hasNext())
        {
            if (predicate.accept(iterator.next()))
            {
                count++;
            }
        }
        return count;
    }

    /**
     * @see Iterate#countWith(Iterable, Predicate2, Object)
     */
    public static <T, P> int countWith(
            Iterator<T> iterator,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        int count = 0;
        while (iterator.hasNext())
        {
            if (predicate.accept(iterator.next(), parameter))
            {
                count++;
            }
        }
        return count;
    }

    /**
     * @see Iterate#select(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R select(
            Iterator<T> iterator,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        while (iterator.hasNext())
        {
            T item = iterator.next();
            if (predicate.accept(item))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#selectWith(Iterable, Predicate2, Object, Collection)
     */
    public static <T, P, R extends Collection<T>> R selectWith(
            Iterator<T> iterator,
            Predicate2<? super T, ? super P> predicate,
            P injectedValue,
            R targetCollection)
    {
        while (iterator.hasNext())
        {
            T item = iterator.next();
            if (predicate.accept(item, injectedValue))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#selectInstancesOf(Iterable, Class)
     */
    public static <T, R extends Collection<T>> R selectInstancesOf(
            Iterator<?> iterator,
            Class<T> clazz,
            R targetCollection)
    {
        while (iterator.hasNext())
        {
            Object item = iterator.next();
            if (clazz.isInstance(item))
            {
                targetCollection.add((T) item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#selectWith(Iterable, Predicate2, Object, Collection)
     */
    public static <T, V, R extends Collection<V>> R collectIf(
            Iterator<T> iterator,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R targetCollection)
    {
        while (iterator.hasNext())
        {
            T item = iterator.next();
            if (predicate.accept(item))
            {
                targetCollection.add(function.valueOf(item));
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#reject(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R reject(
            Iterator<T> iterator,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        while (iterator.hasNext())
        {
            T item = iterator.next();
            if (!predicate.accept(item))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#rejectWith(Iterable, Predicate2, Object, Collection)
     */
    public static <T, P, R extends Collection<T>> R rejectWith(
            Iterator<T> iterator,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        while (iterator.hasNext())
        {
            T item = iterator.next();
            if (!predicate.accept(item, parameter))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#collect(Iterable, Function, Collection)
     */
    public static <T, V, R extends Collection<V>> R collect(
            Iterator<T> iterator,
            Function<? super T, ? extends V> function,
            R targetCollection)
    {
        while (iterator.hasNext())
        {
            targetCollection.add(function.valueOf(iterator.next()));
        }
        return targetCollection;
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction)
     */
    public static <T> MutableBooleanCollection collectBoolean(
            Iterator<T> iterator,
            BooleanFunction<? super T> booleanFunction)
    {
        MutableBooleanCollection result = BooleanLists.mutable.empty();
        while (iterator.hasNext())
        {
            result.add(booleanFunction.booleanValueOf(iterator.next()));
        }
        return result;
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction, MutableBooleanCollection)
     */
    public static <T, R extends MutableBooleanCollection> R collectBoolean(
            Iterator<T> iterator,
            BooleanFunction<? super T> booleanFunction,
            R target)
    {
        while (iterator.hasNext())
        {
            target.add(booleanFunction.booleanValueOf(iterator.next()));
        }
        return target;
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction)
     */
    public static <T> MutableByteCollection collectByte(
            Iterator<T> iterator,
            ByteFunction<? super T> byteFunction)
    {
        MutableByteCollection result = ByteLists.mutable.empty();
        while (iterator.hasNext())
        {
            result.add(byteFunction.byteValueOf(iterator.next()));
        }
        return result;
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction, MutableByteCollection)
     */
    public static <T, R extends MutableByteCollection> R collectByte(
            Iterator<T> iterator,
            ByteFunction<? super T> byteFunction,
            R target)
    {
        while (iterator.hasNext())
        {
            target.add(byteFunction.byteValueOf(iterator.next()));
        }
        return target;
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction)
     */
    public static <T> MutableCharCollection collectChar(
            Iterator<T> iterator,
            CharFunction<? super T> charFunction)
    {
        MutableCharCollection result = CharLists.mutable.empty();
        while (iterator.hasNext())
        {
            result.add(charFunction.charValueOf(iterator.next()));
        }
        return result;
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction, MutableCharCollection)
     */
    public static <T, R extends MutableCharCollection> R collectChar(
            Iterator<T> iterator,
            CharFunction<? super T> charFunction,
            R target)
    {
        while (iterator.hasNext())
        {
            target.add(charFunction.charValueOf(iterator.next()));
        }
        return target;
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction)
     */
    public static <T> MutableDoubleCollection collectDouble(
            Iterator<T> iterator,
            DoubleFunction<? super T> doubleFunction)
    {
        MutableDoubleCollection result = DoubleLists.mutable.empty();
        while (iterator.hasNext())
        {
            result.add(doubleFunction.doubleValueOf(iterator.next()));
        }
        return result;
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction, MutableDoubleCollection)
     */
    public static <T, R extends MutableDoubleCollection> R collectDouble(
            Iterator<T> iterator,
            DoubleFunction<? super T> doubleFunction,
            R target)
    {
        while (iterator.hasNext())
        {
            target.add(doubleFunction.doubleValueOf(iterator.next()));
        }
        return target;
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction)
     */
    public static <T> MutableFloatCollection collectFloat(
            Iterator<T> iterator,
            FloatFunction<? super T> floatFunction)
    {
        MutableFloatCollection result = FloatLists.mutable.empty();
        while (iterator.hasNext())
        {
            result.add(floatFunction.floatValueOf(iterator.next()));
        }
        return result;
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction, MutableFloatCollection)
     */
    public static <T, R extends MutableFloatCollection> R collectFloat(
            Iterator<T> iterator,
            FloatFunction<? super T> floatFunction,
            R target)
    {
        while (iterator.hasNext())
        {
            target.add(floatFunction.floatValueOf(iterator.next()));
        }
        return target;
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction)
     */
    public static <T> MutableIntCollection collectInt(
            Iterator<T> iterator,
            IntFunction<? super T> intFunction)
    {
        MutableIntCollection result = IntLists.mutable.empty();
        while (iterator.hasNext())
        {
            result.add(intFunction.intValueOf(iterator.next()));
        }
        return result;
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction, MutableIntCollection)
     */
    public static <T, R extends MutableIntCollection> R collectInt(
            Iterator<T> iterator,
            IntFunction<? super T> intFunction,
            R target)
    {
        while (iterator.hasNext())
        {
            target.add(intFunction.intValueOf(iterator.next()));
        }
        return target;
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction)
     */
    public static <T> MutableLongCollection collectLong(
            Iterator<T> iterator,
            LongFunction<? super T> longFunction)
    {
        MutableLongCollection result = LongLists.mutable.empty();
        while (iterator.hasNext())
        {
            result.add(longFunction.longValueOf(iterator.next()));
        }
        return result;
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction, MutableLongCollection)
     */
    public static <T, R extends MutableLongCollection> R collectLong(
            Iterator<T> iterator,
            LongFunction<? super T> longFunction,
            R target)
    {
        while (iterator.hasNext())
        {
            target.add(longFunction.longValueOf(iterator.next()));
        }
        return target;
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction)
     */
    public static <T> MutableShortCollection collectShort(
            Iterator<T> iterator,
            ShortFunction<? super T> shortFunction)
    {
        MutableShortCollection result = ShortLists.mutable.empty();
        while (iterator.hasNext())
        {
            result.add(shortFunction.shortValueOf(iterator.next()));
        }
        return result;
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction, MutableShortCollection)
     */
    public static <T, R extends MutableShortCollection> R collectShort(
            Iterator<T> iterator,
            ShortFunction<? super T> shortFunction,
            R target)
    {
        while (iterator.hasNext())
        {
            target.add(shortFunction.shortValueOf(iterator.next()));
        }
        return target;
    }

    /**
     * @see Iterate#flatCollect(Iterable, Function, Collection)
     */
    public static <T, V, R extends Collection<V>> R flatCollect(
            Iterator<T> iterator,
            Function<? super T, ? extends Iterable<V>> function,
            R targetCollection)
    {
        while (iterator.hasNext())
        {
            Iterate.addAllTo(function.valueOf(iterator.next()), targetCollection);
        }
        return targetCollection;
    }

    /**
     * @see Iterate#forEach(Iterable, Procedure)
     */
    public static <T> void forEach(Iterator<T> iterator, Procedure<? super T> procedure)
    {
        while (iterator.hasNext())
        {
            procedure.value(iterator.next());
        }
    }

    /**
     * @see Iterate#forEachWithIndex(Iterable, ObjectIntProcedure)
     */
    public static <T> void forEachWithIndex(Iterator<T> iterator, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        int i = 0;
        while (iterator.hasNext())
        {
            objectIntProcedure.value(iterator.next(), i++);
        }
    }

    /**
     * @see Iterate#detect(Iterable, Predicate)
     */
    public static <T> T detect(Iterator<T> iterator, Predicate<? super T> predicate)
    {
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.accept(each))
            {
                return each;
            }
        }
        return null;
    }

    /**
     * @see Iterate#detectWith(Iterable, Predicate2, Object)
     */
    public static <T, P> T detectWith(Iterator<T> iterator, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.accept(each, parameter))
            {
                return each;
            }
        }
        return null;
    }

    /**
     * @see Iterate#detectOptional(Iterable, Predicate)
     */
    public static <T> Optional<T> detectOptional(Iterator<T> iterator, Predicate<? super T> predicate)
    {
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.accept(each))
            {
                return Optional.of(each);
            }
        }
        return Optional.empty();
    }

    /**
     * @see Iterate#detectWithOptional(Iterable, Predicate2, Object)
     */
    public static <T, P> Optional<T> detectWithOptional(Iterator<T> iterator, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.accept(each, parameter))
            {
                return Optional.of(each);
            }
        }
        return Optional.empty();
    }

    /**
     * @see Iterate#injectInto(Object, Iterable, Function2)
     */
    public static <T, IV> IV injectInto(
            IV injectValue,
            Iterator<T> iterator,
            Function2<? super IV, ? super T, ? extends IV> function)
    {
        IV result = injectValue;
        while (iterator.hasNext())
        {
            result = function.value(result, iterator.next());
        }
        return result;
    }

    /**
     * @see Iterate#injectInto(int, Iterable, IntObjectToIntFunction)
     */
    public static <T> int injectInto(
            int injectValue,
            Iterator<T> iterator,
            IntObjectToIntFunction<? super T> function)
    {
        int result = injectValue;
        while (iterator.hasNext())
        {
            result = function.intValueOf(result, iterator.next());
        }
        return result;
    }

    /**
     * @see Iterate#injectInto(long, Iterable, LongObjectToLongFunction)
     */
    public static <T> long injectInto(
            long injectValue,
            Iterator<T> iterator,
            LongObjectToLongFunction<? super T> function)
    {
        long result = injectValue;
        while (iterator.hasNext())
        {
            result = function.longValueOf(result, iterator.next());
        }
        return result;
    }

    /**
     * @see Iterate#injectInto(double, Iterable, DoubleObjectToDoubleFunction)
     */
    public static <T> double injectInto(
            double injectValue,
            Iterator<T> iterator,
            DoubleObjectToDoubleFunction<? super T> function)
    {
        double result = injectValue;
        while (iterator.hasNext())
        {
            result = function.doubleValueOf(result, iterator.next());
        }
        return result;
    }

    /**
     * @see Iterate#injectInto(float, Iterable, FloatObjectToFloatFunction)
     */
    public static <T> float injectInto(
            float injectValue,
            Iterator<T> iterator,
            FloatObjectToFloatFunction<? super T> function)
    {
        float result = injectValue;
        while (iterator.hasNext())
        {
            result = function.floatValueOf(result, iterator.next());
        }
        return result;
    }

    /**
     * @see Iterate#injectIntoWith(Object, Iterable, Function3, Object)
     */
    public static <T, IV, P> IV injectIntoWith(IV injectValue, Iterator<T> iterator, Function3<? super IV, ? super T, ? super P, ? extends IV> function, P parameter)
    {
        IV result = injectValue;
        while (iterator.hasNext())
        {
            result = function.value(result, iterator.next(), parameter);
        }
        return result;
    }

    public static <T> boolean shortCircuit(
            Iterator<T> iterator,
            Predicate<? super T> predicate,
            boolean expected,
            boolean onShortCircuit,
            boolean atEnd)
    {
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.accept(each) == expected)
            {
                return onShortCircuit;
            }
        }
        return atEnd;
    }

    public static <T, P> boolean shortCircuitWith(
            Iterator<T> iterator,
            Predicate2<? super T, ? super P> predicate2,
            P parameter,
            boolean expected,
            boolean onShortCircuit,
            boolean atEnd)
    {
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate2.accept(each, parameter) == expected)
            {
                return onShortCircuit;
            }
        }
        return atEnd;
    }

    /**
     * @see Iterate#anySatisfy(Iterable, Predicate)
     */
    public static <T> boolean anySatisfy(Iterator<T> iterator, Predicate<? super T> predicate)
    {
        return IteratorIterate.shortCircuit(iterator, predicate, true, true, false);
    }

    /**
     * @see Iterate#anySatisfyWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean anySatisfyWith(Iterator<T> iterator, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IteratorIterate.shortCircuitWith(iterator, predicate, parameter, true, true, false);
    }

    /**
     * @see Iterate#allSatisfy(Iterable, Predicate)
     */
    public static <T> boolean allSatisfy(Iterator<T> iterator, Predicate<? super T> predicate)
    {
        return IteratorIterate.shortCircuit(iterator, predicate, false, false, true);
    }

    /**
     * @see Iterate#allSatisfyWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean allSatisfyWith(Iterator<T> iterator, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IteratorIterate.shortCircuitWith(iterator, predicate, parameter, false, false, true);
    }

    /**
     * @see Iterate#noneSatisfy(Iterable, Predicate)
     */
    public static <T> boolean noneSatisfy(Iterator<T> iterator, Predicate<? super T> predicate)
    {
        return IteratorIterate.shortCircuit(iterator, predicate, true, false, true);
    }

    /**
     * @see Iterate#noneSatisfyWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean noneSatisfyWith(Iterator<T> iterator, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IteratorIterate.shortCircuitWith(iterator, predicate, parameter, true, false, true);
    }

    /**
     * @see Iterate#removeIf(Iterable, Predicate)
     */
    public static <T> boolean removeIf(Iterator<T> iterator, Predicate<? super T> predicate)
    {
        return IteratorIterate.removeIf(iterator, (java.util.function.Predicate<? super T>) predicate);
    }

    /**
     * @see Iterate#removeIf(Iterable, java.util.function.Predicate)
     */
    public static <T> boolean removeIf(Iterator<T> iterator, java.util.function.Predicate<? super T> predicate)
    {
        boolean changed = false;
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.test(each))
            {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    /**
     * @see Iterate#removeIfWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean removeIfWith(Iterator<T> iterator, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        boolean changed = false;
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.accept(each, parameter))
            {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    public static <T> boolean removeIf(Iterator<T> iterator, Predicate<? super T> predicate, Procedure<? super T> procedure)
    {
        boolean changed = false;
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.accept(each))
            {
                procedure.value(each);
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    public static <T, P> boolean removeIfWith(Iterator<T> iterator, Predicate2<? super T, ? super P> predicate, P parameter, Procedure<? super T> procedure)
    {
        boolean changed = false;
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.accept(each, parameter))
            {
                procedure.value(each);
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    /**
     * @see Iterate#detectIndex(Iterable, Predicate)
     */
    public static <T> int detectIndex(Iterator<T> iterator, Predicate<? super T> predicate)
    {
        int i = 0;
        while (iterator.hasNext())
        {
            if (predicate.accept(iterator.next()))
            {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * @see Iterate#detectIndexWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> int detectIndexWith(Iterator<T> iterator, Predicate2<? super T, ? super IV> predicate, IV injectedValue)
    {
        int i = 0;
        while (iterator.hasNext())
        {
            if (predicate.accept(iterator.next(), injectedValue))
            {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * @see Iterate#forEachWith(Iterable, Procedure2, Object)
     */
    public static <T, P> void forEachWith(Iterator<T> iterator, Procedure2<? super T, ? super P> procedure, P parameter)
    {
        while (iterator.hasNext())
        {
            procedure.value(iterator.next(), parameter);
        }
    }

    /**
     * @see Iterate#collectWith(Iterable, Function2, Object, Collection)
     */
    public static <T, P, A, R extends Collection<A>> R collectWith(
            Iterator<T> iterator,
            Function2<? super T, ? super P, ? extends A> function,
            P parameter,
            R targetCollection)
    {
        while (iterator.hasNext())
        {
            targetCollection.add(function.value(iterator.next(), parameter));
        }
        return targetCollection;
    }

    /**
     * @see Iterate#groupBy(Iterable, Function)
     */
    public static <T, V> ImmutableMultimap<V, T> groupBy(
            Iterator<T> iterator,
            Function<? super T, ? extends V> function)
    {
        return IteratorIterate.groupBy(iterator, function, new FastListMultimap<V, T>()).toImmutable();
    }

    /**
     * @see Iterate#groupBy(Iterable, Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupBy(
            Iterator<T> iterator,
            Function<? super T, ? extends V> function,
            R target)
    {
        while (iterator.hasNext())
        {
            T item = iterator.next();
            target.put(function.valueOf(item), item);
        }
        return target;
    }

    /**
     * @see Iterate#groupByEach(Iterable, Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupByEach(
            Iterator<T> iterator,
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        while (iterator.hasNext())
        {
            T item = iterator.next();
            Iterable<V> iterable = function.valueOf(item);
            for (V key : iterable)
            {
                target.put(key, item);
            }
        }
        return target;
    }

    /**
     * @see Iterate#groupByUniqueKey(Iterable, Function, MutableMapIterable)
     */
    public static <K, T, R extends MutableMapIterable<K, T>> R groupByUniqueKey(
            Iterator<T> iterator,
            Function<? super T, ? extends K> function,
            R target)
    {
        while (iterator.hasNext())
        {
            T value = iterator.next();
            K key = function.valueOf(value);
            if (target.put(key, value) != null)
            {
                throw new IllegalStateException("Key " + key + " already exists in map!");
            }
        }
        return target;
    }

    /**
     * @deprecated in 7.0.
     */
    @Deprecated
    public static <T, R extends List<T>> R distinct(Iterator<T> iterator, R targetCollection)
    {
        Set<T> seenSoFar = Sets.mutable.empty();
        return IteratorIterate.select(iterator, seenSoFar::add, targetCollection);
    }

    /**
     * @since 7.0.
     */
    public static <T> MutableList<T> distinct(Iterator<T> iterator)
    {
        return IteratorIterate.distinct(iterator, FastList.newList());
    }

    /**
     * @since 7.0.
     */
    public static <T> MutableList<T> distinct(Iterator<T> iterator, HashingStrategy<? super T> hashingStrategy)
    {
        Set<T> seenSoFar = UnifiedSetWithHashingStrategy.newSet(hashingStrategy);
        MutableList<T> result = FastList.newList();
        while (iterator.hasNext())
        {
            T item = iterator.next();
            if (seenSoFar.add(item))
            {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * @see Iterate#zip(Iterable, Iterable, Collection)
     */
    public static <X, Y, R extends Collection<Pair<X, Y>>> R zip(
            Iterator<X> xs,
            Iterator<Y> ys,
            R target)
    {
        while (xs.hasNext() && ys.hasNext())
        {
            target.add(Tuples.pair(xs.next(), ys.next()));
        }
        return target;
    }

    /**
     * @see Iterate#zipWithIndex(Iterable, Collection)
     */
    public static <T, R extends Collection<Pair<T, Integer>>> R zipWithIndex(Iterator<T> iterator, R target)
    {
        int index = 0;
        while (iterator.hasNext())
        {
            target.add(Tuples.pair(iterator.next(), index));
            index += 1;
        }
        return target;
    }

    /**
     * @see Iterate#chunk(Iterable, int)
     */
    public static <T> RichIterable<RichIterable<T>> chunk(Iterator<T> iterator, int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }

        MutableList<RichIterable<T>> result = Lists.mutable.empty();
        while (iterator.hasNext())
        {
            MutableList<T> batch = Lists.mutable.empty();
            for (int i = 0; i < size && iterator.hasNext(); i++)
            {
                batch.add(iterator.next());
            }
            result.add(batch);
        }
        return result;
    }

    /**
     * @see Iterate#min(Iterable, Comparator)
     */
    public static <T> T min(Iterator<T> iterator, Comparator<? super T> comparator)
    {
        T min = iterator.next();
        while (iterator.hasNext())
        {
            T next = iterator.next();
            if (comparator.compare(next, min) < 0)
            {
                min = next;
            }
        }
        return min;
    }

    /**
     * @see Iterate#max(Iterable, Comparator)
     */
    public static <T> T max(Iterator<T> iterator, Comparator<? super T> comparator)
    {
        T max = iterator.next();
        while (iterator.hasNext())
        {
            T next = iterator.next();
            if (comparator.compare(next, max) > 0)
            {
                max = next;
            }
        }
        return max;
    }

    public static <T> Iterator<T> advanceIteratorTo(Iterator<T> iterator, int from)
    {
        for (int i = 0; i < from; i++)
        {
            iterator.next();
        }
        return iterator;
    }

    public static <T> long sumOfInt(Iterator<T> iterator, IntFunction<? super T> function)
    {
        long sum = 0L;
        while (iterator.hasNext())
        {
            sum += (long) function.intValueOf(iterator.next());
        }
        return sum;
    }

    public static <T> long sumOfLong(Iterator<T> iterator, LongFunction<? super T> function)
    {
        long sum = 0L;
        while (iterator.hasNext())
        {
            sum += function.longValueOf(iterator.next());
        }
        return sum;
    }

    public static <T> double sumOfFloat(Iterator<T> iterator, FloatFunction<? super T> function)
    {
        double sum = 0.0d;
        double compensation = 0.0d;
        while (iterator.hasNext())
        {
            double adjustedValue = (double) function.floatValueOf(iterator.next()) - compensation;
            double nextSum = sum + adjustedValue;
            compensation = nextSum - sum - adjustedValue;
            sum = nextSum;
        }
        return sum;
    }

    public static <T> double sumOfDouble(Iterator<T> iterator, DoubleFunction<? super T> function)
    {
        double sum = 0.0d;
        double compensation = 0.0d;
        while (iterator.hasNext())
        {
            double adjustedValue = function.doubleValueOf(iterator.next()) - compensation;
            double nextSum = sum + adjustedValue;
            compensation = nextSum - sum - adjustedValue;
            sum = nextSum;
        }
        return sum;
    }

    public static <T> BigDecimal sumOfBigDecimal(Iterator<T> iterator, Function<? super T, BigDecimal> function)
    {
        BigDecimal result = BigDecimal.ZERO;
        while (iterator.hasNext())
        {
            result = result.add(function.valueOf(iterator.next()));
        }
        return result;
    }

    public static <T> BigInteger sumOfBigInteger(Iterator<T> iterator, Function<? super T, BigInteger> function)
    {
        BigInteger result = BigInteger.ZERO;
        while (iterator.hasNext())
        {
            result = result.add(function.valueOf(iterator.next()));
        }
        return result;
    }

    public static <V, T> MutableMap<V, BigDecimal> sumByBigDecimal(
            Iterator<T> iterator,
            Function<? super T, ? extends V> groupBy,
            Function<? super T, BigDecimal> function)
    {
        MutableMap<V, BigDecimal> result = Maps.mutable.empty();
        while (iterator.hasNext())
        {
            T item = iterator.next();
            result.updateValue(groupBy.valueOf(item), Functions0.zeroBigDecimal(), original -> original.add(function.valueOf(item)));
        }
        return result;
    }

    public static <V, T> MutableMap<V, BigInteger> sumByBigInteger(
            Iterator<T> iterator,
            Function<? super T, ? extends V> groupBy,
            Function<? super T, BigInteger> function)
    {
        MutableMap<V, BigInteger> result = Maps.mutable.empty();
        while (iterator.hasNext())
        {
            T item = iterator.next();
            result.updateValue(groupBy.valueOf(item), Functions0.zeroBigInteger(), original -> original.add(function.valueOf(item)));
        }
        return result;
    }

    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterator<T> iterator,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        MutableMap<K, V> map = Maps.mutable.empty();
        IteratorIterate.forEach(iterator, new MutatingAggregationProcedure<>(map, groupBy, zeroValueFactory, mutatingAggregator));
        return map;
    }

    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterator<T> iterator,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        MutableMap<K, V> map = Maps.mutable.empty();
        IteratorIterate.forEach(iterator, new NonMutatingAggregationProcedure<>(map, groupBy, zeroValueFactory, nonMutatingAggregator));
        return map;
    }

    public static <T, V extends Comparable<? super V>> T minBy(Iterator<T> iterator, Function<? super T, ? extends V> function)
    {
        T min = iterator.next();
        V minValue = function.valueOf(min);
        while (iterator.hasNext())
        {
            T next = iterator.next();
            V nextValue = function.valueOf(next);
            if (nextValue.compareTo(minValue) < 0)
            {
                min = next;
                minValue = nextValue;
            }
        }
        return min;
    }

    public static <T, V extends Comparable<? super V>> T maxBy(Iterator<T> iterator, Function<? super T, ? extends V> function)
    {
        T max = iterator.next();
        V maxValue = function.valueOf(max);
        while (iterator.hasNext())
        {
            T next = iterator.next();
            V nextValue = function.valueOf(next);
            if (nextValue.compareTo(maxValue) > 0)
            {
                max = next;
                maxValue = nextValue;
            }
        }
        return max;
    }
}
