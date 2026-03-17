/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.RandomAccess;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
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
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
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
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.utility.internal.IterableIterate;
import org.eclipse.collections.impl.utility.internal.RandomAccessListIterate;

/**
 * The ListIterate utility class can be useful for iterating over lists, especially if there
 * is a desire to return a MutableList from any of the iteration methods.
 *
 * @since 1.0
 */
public final class ListIterate
{
    private ListIterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static boolean equals(List<?> one, List<?> two)
    {
        if (one.size() != two.size())   // we assume that size() is a constant time operation in most lists
        {
            return false;
        }
        if (one instanceof RandomAccess)
        {
            return two instanceof RandomAccess
                    ? ListIterate.randomAccessEquals(one, two)
                    : ListIterate.oneRandomAccessEquals(one, two);
        }
        return two instanceof RandomAccess
                ? ListIterate.oneRandomAccessEquals(two, one)
                : ListIterate.nonRandomAccessEquals(one, two);
    }

    private static boolean randomAccessEquals(List<?> one, List<?> two)
    {
        int localSize = one.size();
        for (int i = 0; i < localSize; i++)
        {
            if (!Objects.equals(one.get(i), two.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean oneRandomAccessEquals(List<?> one, List<?> two)
    {
        int localSize = one.size();
        Iterator<?> twoIterator = two.iterator();
        for (int i = 0; i < localSize; i++)
        {
            if (!twoIterator.hasNext() || !Objects.equals(one.get(i), twoIterator.next()))
            {
                return false;
            }
        }
        return !twoIterator.hasNext();
    }

    private static boolean nonRandomAccessEquals(List<?> one, List<?> two)
    {
        Iterator<?> oneIterator = one.iterator();
        Iterator<?> twoIterator = two.iterator();
        while (oneIterator.hasNext())
        {
            if (!twoIterator.hasNext() || !Objects.equals(oneIterator.next(), twoIterator.next()))
            {
                return false;
            }
        }
        return !twoIterator.hasNext();
    }

    public static <T> void toArray(List<T> list, T[] target, int startIndex, int sourceSize)
    {
        if (list instanceof RandomAccess)
        {
            RandomAccessListIterate.toArray(list, target, startIndex, sourceSize);
        }
        else
        {
            int index = 0;
            ListIterator<T> iterator = list.listIterator();
            while (iterator.hasNext() && index < sourceSize)
            {
                target[startIndex + index] = iterator.next();
                index++;
            }
        }
    }

    /**
     * @see Iterate#select(Iterable, Predicate)
     */
    public static <T> MutableList<T> select(List<T> list, Predicate<? super T> predicate)
    {
        return ListIterate.select(list, predicate, FastList.newList());
    }

    /**
     * @see Iterate#selectWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> MutableList<T> selectWith(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        return ListIterate.selectWith(list, predicate, injectedValue, FastList.newList());
    }

    /**
     * @see Iterate#select(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R select(
            List<T> list,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.select(list, predicate, targetCollection);
        }
        return IterableIterate.select(list, predicate, targetCollection);
    }

    /**
     * @see Iterate#selectWith(Iterable, Predicate2, Object, Collection)
     */
    public static <T, P, R extends Collection<T>> R selectWith(
            List<T> list,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.selectWith(list, predicate, parameter, targetCollection);
        }
        return IterableIterate.selectWith(list, predicate, parameter, targetCollection);
    }

    /**
     * @see Iterate#selectInstancesOf(Iterable, Class)
     */
    public static <T, S> MutableList<S> selectInstancesOf(
            List<T> list,
            Class<S> clazz)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.selectInstancesOf(list, clazz);
        }
        return IterableIterate.selectInstancesOf(list, clazz);
    }

    /**
     * @see Iterate#count(Iterable, Predicate)
     */
    public static <T> int count(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.count(list, predicate);
        }
        return IterableIterate.count(list, predicate);
    }

    /**
     * @see Iterate#countWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> int countWith(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.countWith(list, predicate, injectedValue);
        }
        return IterableIterate.countWith(list, predicate, injectedValue);
    }

    /**
     * @see Iterate#collectIf(Iterable, Predicate, Function)
     */
    public static <T, A> MutableList<A> collectIf(
            List<T> list,
            Predicate<? super T> predicate,
            Function<? super T, ? extends A> function)
    {
        return ListIterate.collectIf(list, predicate, function, FastList.newList());
    }

    /**
     * @see Iterate#collectIf(Iterable, Predicate, Function, Collection)
     */
    public static <T, A, R extends Collection<A>> R collectIf(
            List<T> list,
            Predicate<? super T> predicate,
            Function<? super T, ? extends A> function,
            R targetCollection)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectIf(list, predicate, function, targetCollection);
        }
        return IterableIterate.collectIf(list, predicate, function, targetCollection);
    }

    /**
     * @see Iterate#reject(Iterable, Predicate)
     */
    public static <T> MutableList<T> reject(List<T> list, Predicate<? super T> predicate)
    {
        return ListIterate.reject(list, predicate, FastList.newList());
    }

    /**
     * @see Iterate#rejectWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> MutableList<T> rejectWith(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        return ListIterate.rejectWith(list, predicate, injectedValue, FastList.newList());
    }

    /**
     * @see Iterate#reject(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R reject(
            List<T> list,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.reject(list, predicate, targetCollection);
        }
        return IterableIterate.reject(list, predicate, targetCollection);
    }

    /**
     * @see Iterate#reject(Iterable, Predicate, Collection)
     */
    public static <T, P, R extends Collection<T>> R rejectWith(
            List<T> list,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.rejectWith(list, predicate, parameter, targetCollection);
        }
        return IterableIterate.rejectWith(list, predicate, parameter, targetCollection);
    }

    /**
     * @see Iterate#collect(Iterable, Function)
     */
    public static <T, A> MutableList<A> collect(
            List<T> list,
            Function<? super T, ? extends A> function)
    {
        return ListIterate.collect(list, function, Lists.mutable.withInitialCapacity(list.size()));
    }

    /**
     * @see Iterate#collect(Iterable, Function, Collection)
     */
    public static <T, A, R extends Collection<A>> R collect(
            List<T> list,
            Function<? super T, ? extends A> function,
            R targetCollection)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collect(list, function, targetCollection);
        }
        return IterableIterate.collect(list, function, targetCollection);
    }

    /**
     * @since 9.1
     */
    public static <T, A> MutableList<A> collectWithIndex(
            List<T> list,
            ObjectIntToObjectFunction<? super T, ? extends A> function)
    {
        return ListIterate.collectWithIndex(list, function, Lists.mutable.withInitialCapacity(list.size()));
    }

    /**
     * @since 9.1
     */
    public static <T, A, R extends Collection<A>> R collectWithIndex(
            List<T> list,
            ObjectIntToObjectFunction<? super T, ? extends A> function,
            R targetCollection)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectWithIndex(list, function, targetCollection);
        }
        int index = 0;
        for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); index++)
        {
            targetCollection.add(function.valueOf(iterator.next(), index));
        }
        return targetCollection;
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction)
     */
    public static <T> MutableBooleanList collectBoolean(
            List<T> list,
            BooleanFunction<? super T> booleanFunction)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectBoolean(list, booleanFunction);
        }
        return (MutableBooleanList) IterableIterate.collectBoolean(list, booleanFunction);
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction, MutableBooleanCollection)
     */
    public static <T, R extends MutableBooleanCollection> R collectBoolean(
            List<T> list,
            BooleanFunction<? super T> booleanFunction,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectBoolean(list, booleanFunction, target);
        }
        return IterableIterate.collectBoolean(list, booleanFunction, target);
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction)
     */
    public static <T> MutableByteList collectByte(
            List<T> list,
            ByteFunction<? super T> byteFunction)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectByte(list, byteFunction);
        }
        return (MutableByteList) IterableIterate.collectByte(list, byteFunction);
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction, MutableByteCollection)
     */
    public static <T, R extends MutableByteCollection> R collectByte(
            List<T> list,
            ByteFunction<? super T> byteFunction,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectByte(list, byteFunction, target);
        }
        return IterableIterate.collectByte(list, byteFunction, target);
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction)
     */
    public static <T> MutableCharList collectChar(
            List<T> list,
            CharFunction<? super T> charFunction)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectChar(list, charFunction);
        }
        return (MutableCharList) IterableIterate.collectChar(list, charFunction);
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction, MutableCharCollection)
     */
    public static <T, R extends MutableCharCollection> R collectChar(
            List<T> list,
            CharFunction<? super T> charFunction,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectChar(list, charFunction, target);
        }
        return IterableIterate.collectChar(list, charFunction, target);
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction)
     */
    public static <T> MutableDoubleList collectDouble(
            List<T> list,
            DoubleFunction<? super T> doubleFunction)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectDouble(list, doubleFunction);
        }
        return (MutableDoubleList) IterableIterate.collectDouble(list, doubleFunction);
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction, MutableDoubleCollection)
     */
    public static <T, R extends MutableDoubleCollection> R collectDouble(
            List<T> list,
            DoubleFunction<? super T> doubleFunction,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectDouble(list, doubleFunction, target);
        }
        return IterableIterate.collectDouble(list, doubleFunction, target);
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction)
     */
    public static <T> MutableFloatList collectFloat(
            List<T> list,
            FloatFunction<? super T> floatFunction)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectFloat(list, floatFunction);
        }
        return (MutableFloatList) IterableIterate.collectFloat(list, floatFunction);
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction, MutableFloatCollection)
     */
    public static <T, R extends MutableFloatCollection> R collectFloat(
            List<T> list,
            FloatFunction<? super T> floatFunction,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectFloat(list, floatFunction, target);
        }
        return IterableIterate.collectFloat(list, floatFunction, target);
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction)
     */
    public static <T> MutableIntList collectInt(
            List<T> list,
            IntFunction<? super T> intFunction)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectInt(list, intFunction);
        }
        return (MutableIntList) IterableIterate.collectInt(list, intFunction);
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction, MutableIntCollection)
     */
    public static <T, R extends MutableIntCollection> R collectInt(
            List<T> list,
            IntFunction<? super T> intFunction,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectInt(list, intFunction, target);
        }
        return IterableIterate.collectInt(list, intFunction, target);
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction)
     */
    public static <T> MutableLongList collectLong(
            List<T> list,
            LongFunction<? super T> longFunction)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectLong(list, longFunction);
        }
        return (MutableLongList) IterableIterate.collectLong(list, longFunction);
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction, MutableLongCollection)
     */
    public static <T, R extends MutableLongCollection> R collectLong(
            List<T> list,
            LongFunction<? super T> longFunction,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectLong(list, longFunction, target);
        }
        return IterableIterate.collectLong(list, longFunction, target);
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction)
     */
    public static <T> MutableShortList collectShort(
            List<T> list,
            ShortFunction<? super T> shortFunction)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectShort(list, shortFunction);
        }
        return (MutableShortList) IterableIterate.collectShort(list, shortFunction);
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction, MutableShortCollection)
     */
    public static <T, R extends MutableShortCollection> R collectShort(
            List<T> list,
            ShortFunction<? super T> shortFunction,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectShort(list, shortFunction, target);
        }
        return IterableIterate.collectShort(list, shortFunction, target);
    }

    /**
     * @see Iterate#flatCollect(Iterable, Function)
     */
    public static <T, A> MutableList<A> flatCollect(
            List<T> list,
            Function<? super T, ? extends Iterable<A>> function)
    {
        return ListIterate.flatCollect(list, function, Lists.mutable.withInitialCapacity(list.size()));
    }

    /**
     * @see Iterate#flatCollect(Iterable, Function, Collection)
     */
    public static <T, A, R extends Collection<A>> R flatCollect(
            List<T> list,
            Function<? super T, ? extends Iterable<A>> function,
            R targetCollection)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.flatCollect(list, function, targetCollection);
        }
        return IterableIterate.flatCollect(list, function, targetCollection);
    }

    /**
     * Returns the first element of a list.
     */
    public static <T> T getFirst(List<T> collection)
    {
        return Iterate.isEmpty(collection) ? null : collection.get(0);
    }

    /**
     * Returns the last element of a list.
     */
    public static <T> T getLast(List<T> list)
    {
        if (list == null)
        {
            return null;
        }
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.getLast(list);
        }
        ListIterator<T> iterator = list.listIterator(list.size());
        if (iterator.hasPrevious())
        {
            return iterator.previous();
        }
        return null;
    }

    /**
     * @see Iterate#forEach(Iterable, Procedure)
     */
    public static <T> void forEach(List<T> list, Procedure<? super T> procedure)
    {
        if (list instanceof RandomAccess)
        {
            RandomAccessListIterate.forEach(list, procedure);
        }
        else
        {
            IterableIterate.forEach(list, procedure);
        }
    }

    /**
     * Iterates over the List in reverse order executing the Procedure for each element
     */
    public static <T> void reverseForEach(List<T> list, Procedure<? super T> procedure)
    {
        if (!list.isEmpty())
        {
            ListIterate.forEach(list, list.size() - 1, 0, procedure);
        }
    }

    /**
     * Iterates over the List in reverse order executing the Procedure for each element. The index passed into the ObjectIntProcedure is the actual index of the
     * range.
     */
    public static <T> void reverseForEachWithIndex(List<T> list, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        if (!list.isEmpty())
        {
            ListIterate.forEachWithIndex(list, list.size() - 1, 0, objectIntProcedure);
        }
    }

    /**
     * Iterates over the section of the list covered by the specified indexes. The indexes are both inclusive. If the
     * from is less than the to, the list is iterated in forward order. If the from is greater than the to, then the
     * list is iterated in the reverse order.
     *
     * <pre>e.g.
     * MutableList&lt;People&gt; people = FastList.newListWith(ted, mary, bob, sally);
     * ListIterate.forEach(people, 0, 1, new Procedure&lt;Person&gt;()
     * {
     *     public void value(Person person)
     *     {
     *          LOGGER.info(person.getName());
     *     }
     * });
     * </pre>
     * <p>
     * This code would output ted and mary's names.
     */
    public static <T> void forEach(List<T> list, int from, int to, Procedure<? super T> procedure)
    {
        ListIterate.rangeCheck(from, to, list.size());

        if (list instanceof RandomAccess)
        {
            RandomAccessListIterate.forEach(list, from, to, procedure);
        }
        else
        {
            if (from <= to)
            {
                ListIterator<T> iterator = list.listIterator(from);
                for (int i = from; i <= to; i++)
                {
                    procedure.value(iterator.next());
                }
            }
            else
            {
                ListIterator<T> iterator = list.listIterator(from + 1);
                for (int i = from; i >= to; i--)
                {
                    procedure.value(iterator.previous());
                }
            }
        }
    }

    /**
     * Iterates over the section of the list covered by the specified indexes. The indexes are both inclusive. If the
     * from is less than the to, the list is iterated in forward order. If the from is greater than the to, then the
     * list is iterated in the reverse order. The index passed into the ObjectIntProcedure is the actual index of the
     * range.
     *
     * <pre>e.g.
     * MutableList&lt;People&gt; people = FastList.newListWith(ted, mary, bob, sally);
     * ListIterate.forEachWithIndex(people, 0, 1, new ObjectIntProcedure&lt;Person&gt;()
     * {
     *     public void value(Person person, int index)
     *     {
     *          LOGGER.info(person.getName() + " at index: " + index);
     *     }
     * });
     * </pre>
     * <p>
     * This code would output ted and mary's names.
     */
    public static <T> void forEachWithIndex(List<T> list, int from, int to, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(from, to, list.size());

        if (list instanceof RandomAccess)
        {
            RandomAccessListIterate.forEachWithIndex(list, from, to, objectIntProcedure);
        }
        else
        {
            if (from <= to)
            {
                ListIterator<T> iterator = list.listIterator(from);
                for (int i = from; i <= to; i++)
                {
                    objectIntProcedure.value(iterator.next(), i);
                }
            }
            else
            {
                ListIterator<T> iterator = list.listIterator(from + 1);
                for (int i = from; i >= to; i--)
                {
                    objectIntProcedure.value(iterator.previous(), i);
                }
            }
        }
    }

    public static void rangeCheck(int from, int to, int size)
    {
        if (from < 0)
        {
            throw new IndexOutOfBoundsException("From index: " + from);
        }
        if (to < 0)
        {
            throw new IndexOutOfBoundsException("To index: " + to);
        }
        if (from >= size)
        {
            throw new IndexOutOfBoundsException("From index: " + from + " Size: " + size);
        }
        if (to >= size)
        {
            throw new IndexOutOfBoundsException("To index: " + to + " Size: " + size);
        }
    }

    /**
     * Iterates over both lists together, evaluating Procedure2 with the current element from each list.
     */
    public static <T1, T2> void forEachInBoth(List<T1> list1, List<T2> list2, Procedure2<? super T1, ? super T2> procedure)
    {
        if (list1 != null && list2 != null)
        {
            if (list1.size() == list2.size())
            {
                if (list1 instanceof RandomAccess && list2 instanceof RandomAccess)
                {
                    RandomAccessListIterate.forEachInBoth(list1, list2, procedure);
                }
                else
                {
                    Iterator<T1> iterator1 = list1.iterator();
                    Iterator<T2> iterator2 = list2.iterator();
                    int size = list2.size();
                    for (int i = 0; i < size; i++)
                    {
                        procedure.value(iterator1.next(), iterator2.next());
                    }
                }
            }
            else
            {
                throw new RuntimeException("Attempt to call forEachInBoth with two Lists of different sizes :"
                        + list1.size()
                        + ':'
                        + list2.size());
            }
        }
    }

    /**
     * @see Iterate#forEachWithIndex(Iterable, ObjectIntProcedure)
     */
    public static <T> void forEachWithIndex(List<T> list, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        if (list instanceof RandomAccess)
        {
            RandomAccessListIterate.forEachWithIndex(list, objectIntProcedure);
        }
        else
        {
            IterableIterate.forEachWithIndex(list, objectIntProcedure);
        }
    }

    /**
     * @see Iterate#detect(Iterable, Predicate)
     */
    public static <T> T detect(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.detect(list, predicate);
        }
        return IterableIterate.detect(list, predicate);
    }

    /**
     * @see Iterate#detectWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> T detectWith(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.detectWith(list, predicate, injectedValue);
        }
        return IterableIterate.detectWith(list, predicate, injectedValue);
    }

    /**
     * @see Iterate#detectOptional(Iterable, Predicate)
     */
    public static <T> Optional<T> detectOptional(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.detectOptional(list, predicate);
        }
        return IterableIterate.detectOptional(list, predicate);
    }

    /**
     * @see Iterate#detectWithOptional(Iterable, Predicate2, Object)
     */
    public static <T, IV> Optional<T> detectWithOptional(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.detectWithOptional(list, predicate, injectedValue);
        }
        return IterableIterate.detectWithOptional(list, predicate, injectedValue);
    }

    /**
     * @see Iterate#detectIfNone(Iterable, Predicate, Object)
     */
    public static <T> T detectIfNone(List<T> list, Predicate<? super T> predicate, T ifNone)
    {
        T result = ListIterate.detect(list, predicate);
        return result == null ? ifNone : result;
    }

    /**
     * @see Iterate#detectWithIfNone(Iterable, Predicate2, Object, Object)
     */
    public static <T, IV> T detectWithIfNone(List<T> list, Predicate2<? super T, ? super IV> predicate, IV injectedValue, T ifNone)
    {
        T result = ListIterate.detectWith(list, predicate, injectedValue);
        return result == null ? ifNone : result;
    }

    /**
     * @see Iterate#injectInto(Object, Iterable, Function2)
     */
    public static <T, IV> IV injectInto(IV injectValue, List<T> list, Function2<? super IV, ? super T, ? extends IV> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, list, function);
        }
        return IterableIterate.injectInto(injectValue, list, function);
    }

    /**
     * @see Iterate#injectInto(int, Iterable, IntObjectToIntFunction)
     */
    public static <T> int injectInto(int injectValue, List<T> list, IntObjectToIntFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, list, function);
        }
        return IterableIterate.injectInto(injectValue, list, function);
    }

    /**
     * @see Iterate#injectInto(long, Iterable, LongObjectToLongFunction)
     */
    public static <T> long injectInto(long injectValue, List<T> list, LongObjectToLongFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, list, function);
        }
        return IterableIterate.injectInto(injectValue, list, function);
    }

    /**
     * @see Iterate#injectInto(double, Iterable, DoubleObjectToDoubleFunction)
     */
    public static <T> double injectInto(double injectValue, List<T> list, DoubleObjectToDoubleFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, list, function);
        }
        return IterableIterate.injectInto(injectValue, list, function);
    }

    /**
     * @see Iterate#injectInto(float, Iterable, FloatObjectToFloatFunction)
     */
    public static <T> float injectInto(float injectValue, List<T> list, FloatObjectToFloatFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, list, function);
        }
        return IterableIterate.injectInto(injectValue, list, function);
    }

    public static <T> long sumOfInt(List<T> list, IntFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumOfInt(list, function);
        }
        return IterableIterate.sumOfInt(list, function);
    }

    public static <T> long sumOfLong(List<T> list, LongFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumOfLong(list, function);
        }
        return IterableIterate.sumOfLong(list, function);
    }

    public static <T> double sumOfFloat(List<T> list, FloatFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumOfFloat(list, function);
        }
        return IterableIterate.sumOfFloat(list, function);
    }

    public static <T> double sumOfDouble(List<T> list, DoubleFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumOfDouble(list, function);
        }
        return IterableIterate.sumOfDouble(list, function);
    }

    public static <T> BigDecimal sumOfBigDecimal(List<T> list, Function<? super T, BigDecimal> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumOfBigDecimal(list, function);
        }
        return IterableIterate.sumOfBigDecimal(list, function);
    }

    public static <T> BigInteger sumOfBigInteger(List<T> list, Function<? super T, BigInteger> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumOfBigInteger(list, function);
        }
        return IterableIterate.sumOfBigInteger(list, function);
    }

    public static <V, T> MutableMap<V, BigDecimal> sumByBigDecimal(List<T> list, Function<T, V> groupBy, Function<? super T, BigDecimal> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumByBigDecimal(list, groupBy, function);
        }
        return IterableIterate.sumByBigDecimal(list, groupBy, function);
    }

    public static <V, T> MutableMap<V, BigInteger> sumByBigInteger(List<T> list, Function<T, V> groupBy, Function<? super T, BigInteger> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumByBigInteger(list, groupBy, function);
        }
        return IterableIterate.sumByBigInteger(list, groupBy, function);
    }

    public static <V, T> ObjectLongMap<V> sumByInt(List<T> list, Function<T, V> groupBy, IntFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumByInt(list, groupBy, function);
        }
        return IterableIterate.sumByInt(list, groupBy, function);
    }

    public static <V, T> ObjectLongMap<V> sumByLong(List<T> list, Function<T, V> groupBy, LongFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumByLong(list, groupBy, function);
        }
        return IterableIterate.sumByLong(list, groupBy, function);
    }

    public static <V, T> ObjectDoubleMap<V> sumByFloat(List<T> list, Function<T, V> groupBy, FloatFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumByFloat(list, groupBy, function);
        }
        return IterableIterate.sumByFloat(list, groupBy, function);
    }

    public static <V, T> ObjectDoubleMap<V> sumByDouble(List<T> list, Function<T, V> groupBy, DoubleFunction<? super T> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.sumByDouble(list, groupBy, function);
        }
        return IterableIterate.sumByDouble(list, groupBy, function);
    }

    /**
     * @see Iterate#anySatisfy(Iterable, Predicate)
     */
    public static <T> boolean anySatisfy(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.anySatisfy(list, predicate);
        }
        return IterableIterate.anySatisfy(list, predicate);
    }

    /**
     * @see Iterate#anySatisfyWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> boolean anySatisfyWith(List<T> list, Predicate2<? super T, ? super IV> predicate, IV injectedValue)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.anySatisfyWith(list, predicate, injectedValue);
        }
        return IterableIterate.anySatisfyWith(list, predicate, injectedValue);
    }

    /**
     * @see Iterate#allSatisfy(Iterable, Predicate)
     */
    public static <T> boolean allSatisfy(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.allSatisfy(list, predicate);
        }
        return IterableIterate.allSatisfy(list, predicate);
    }

    /**
     * @see Iterate#allSatisfyWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> boolean allSatisfyWith(List<T> list, Predicate2<? super T, ? super IV> predicate, IV injectedValue)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.allSatisfyWith(list, predicate, injectedValue);
        }
        return IterableIterate.allSatisfyWith(list, predicate, injectedValue);
    }

    /**
     * @see Iterate#noneSatisfy(Iterable, Predicate)
     */
    public static <T> boolean noneSatisfy(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.noneSatisfy(list, predicate);
        }
        return IterableIterate.noneSatisfy(list, predicate);
    }

    /**
     * @see Iterate#noneSatisfyWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean noneSatisfyWith(List<T> list, Predicate2<? super T, ? super P> predicate, P injectedValue)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.noneSatisfyWith(list, predicate, injectedValue);
        }
        return IterableIterate.noneSatisfyWith(list, predicate, injectedValue);
    }

    /**
     * @see Iterate#selectAndRejectWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> Twin<MutableList<T>> selectAndRejectWith(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.selectAndRejectWith(list, predicate, injectedValue);
        }
        return IterableIterate.selectAndRejectWith(list, predicate, injectedValue);
    }

    /**
     * @see Iterate#partition(Iterable, Predicate)
     */
    public static <T> PartitionMutableList<T> partition(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.partition(list, predicate);
        }
        return IterableIterate.partition(list, predicate);
    }

    public static <T, P> PartitionMutableList<T> partitionWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.partitionWith(list, predicate, parameter);
        }
        return IterableIterate.partitionWith(list, predicate, parameter);
    }

    /**
     * @see Iterate#removeIf(Iterable, Predicate)
     */
    public static <T> boolean removeIf(List<T> list, Predicate<? super T> predicate)
    {
        return ListIterate.removeIf(list, (java.util.function.Predicate<? super T>) predicate);
    }

    /**
     * @see Iterate#removeIf(Iterable, java.util.function.Predicate)
     */
    public static <T> boolean removeIf(List<T> list, java.util.function.Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.removeIf(list, predicate);
        }
        return IterableIterate.removeIf(list, predicate);
    }

    /**
     * @see Iterate#removeIfWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean removeIfWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.removeIfWith(list, predicate, parameter);
        }
        return IterableIterate.removeIfWith(list, predicate, parameter);
    }

    public static <T> boolean removeIf(List<T> list, Predicate<? super T> predicate, Procedure<? super T> procedure)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.removeIf(list, predicate, procedure);
        }
        return IterableIterate.removeIf(list, predicate, procedure);
    }

    public static <T, P> boolean removeIfWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter, Procedure<? super T> procedure)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.removeIfWith(list, predicate, parameter, procedure);
        }
        return IterableIterate.removeIfWith(list, predicate, parameter, procedure);
    }

    /**
     * Searches for the first index where the predicate evaluates to true.
     */
    public static <T> int detectIndex(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.detectIndex(list, predicate);
        }
        return IterableIterate.detectIndex(list, predicate);
    }

    /**
     * Searches for the first index where the predicate2 and parameter evaluates to true.
     */
    public static <T, P> int detectIndexWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.detectIndexWith(list, predicate, parameter);
        }
        return IterableIterate.detectIndexWith(list, predicate, parameter);
    }

    /**
     * Returns the last index where the predicate evaluates to true.
     * Returns -1 for no matches.
     */
    public static <T> int detectLastIndex(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.detectLastIndex(list, predicate);
        }
        int size = list.size();
        int i = size - 1;
        ListIterator<T> reverseIterator = list.listIterator(size);
        while (reverseIterator.hasPrevious())
        {
            if (predicate.accept(reverseIterator.previous()))
            {
                return i;
            }
            i--;
        }
        return -1;
    }

    public static <T, IV, P> IV injectIntoWith(IV injectedValue, List<T> list, Function3<? super IV, ? super T, ? super P, ? extends IV> function, P parameter)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectIntoWith(injectedValue, list, function, parameter);
        }
        return IterableIterate.injectIntoWith(injectedValue, list, function, parameter);
    }

    /**
     * @see Iterate#forEachWith(Iterable, Procedure2, Object)
     */
    public static <T, P> void forEachWith(List<T> list, Procedure2<? super T, ? super P> procedure, P parameter)
    {
        if (list instanceof RandomAccess)
        {
            RandomAccessListIterate.forEachWith(list, procedure, parameter);
        }
        else
        {
            IterableIterate.forEachWith(list, procedure, parameter);
        }
    }

    /**
     * @see Iterate#collectWith(Iterable, Function2, Object)
     */
    public static <T, P, A> MutableList<A> collectWith(
            List<T> list,
            Function2<? super T, ? super P, ? extends A> function,
            P parameter)
    {
        return ListIterate.collectWith(list, function, parameter, Lists.mutable.withInitialCapacity(list.size()));
    }

    /**
     * @see Iterate#collectWith(Iterable, Function2, Object, Collection)
     */
    public static <T, P, A, R extends Collection<A>> R collectWith(
            List<T> list,
            Function2<? super T, ? super P, ? extends A> function,
            P parameter,
            R targetCollection)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectWith(list, function, parameter, targetCollection);
        }
        return IterableIterate.collectWith(list, function, parameter, targetCollection);
    }

    /**
     * @deprecated in 7.0.
     */
    @Deprecated
    public static <T, R extends List<T>> R distinct(List<T> list, R targetList)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.distinct(list, targetList);
        }
        return IterableIterate.distinct(list, targetList);
    }

    /**
     * @since 7.0.
     */
    public static <T> MutableList<T> distinct(List<T> list)
    {
        return ListIterate.distinct(list, FastList.newList());
    }

    /**
     * @since 7.0.
     */
    public static <T> MutableList<T> distinct(List<T> list, HashingStrategy<? super T> hashingStrategy)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.distinct(list, hashingStrategy);
        }
        return IterableIterate.distinct(list, hashingStrategy);
    }

    /**
     * @since 9.0
     */
    public static <T, V> MutableList<T> distinctBy(List<T> list, Function<? super T, ? extends V> function)
    {
        return ListIterate.distinct(list, HashingStrategies.fromFunction(function));
    }

    /**
     * Reverses the order of the items in the list.
     * <pre>
     *     List&lt;Integer&gt; integers = Lists.fixedSize.of(1, 3, 2);
     *     Verify.assertListsEqual(FastList.newListWith(2, 3, 1), ListIterate.reverse(integers));
     * </pre>
     *
     * @return the reversed list
     */
    public static <T> List<T> reverseThis(List<T> list)
    {
        Collections.reverse(list);
        return list;
    }

    /**
     * @see Iterate#take(Iterable, int)
     */
    public static <T> MutableList<T> take(List<T> list, int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.take(list, count, Lists.mutable.withInitialCapacity(Math.min(list.size(), count)));
        }
        return ListIterate.take(list, count, FastList.newList());
    }

    /**
     * @see Iterate#take(Iterable, int)
     */
    public static <T, R extends Collection<T>> R take(List<T> list, int count, R targetList)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.take(list, count, targetList);
        }
        return IterableIterate.take(list, count, targetList);
    }

    /**
     * @see Iterate#drop(Iterable, int)
     */
    public static <T> MutableList<T> drop(List<T> list, int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.drop(list, count, Lists.mutable.withInitialCapacity(list.size() - Math.min(list.size(), count)));
        }
        return ListIterate.drop(list, count, FastList.newList());
    }

    /**
     * @see Iterate#drop(Iterable, int)
     */
    public static <T, R extends Collection<T>> R drop(List<T> list, int count, R targetList)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.drop(list, count, targetList);
        }
        return IterableIterate.drop(list, count, targetList);
    }

    /**
     * @see RichIterable#appendString(Appendable, String, String, String)
     */
    public static <T> void appendString(
            List<T> list,
            Appendable appendable,
            String start,
            String separator,
            String end)
    {
        if (list instanceof RandomAccess)
        {
            RandomAccessListIterate.appendString(list, appendable, start, separator, end);
        }
        else
        {
            IterableIterate.appendString(list, appendable, start, separator, end);
        }
    }

    /**
     * @see Iterate#groupBy(Iterable, Function)
     */
    public static <T, V> FastListMultimap<V, T> groupBy(
            List<T> list,
            Function<? super T, ? extends V> function)
    {
        return ListIterate.groupBy(list, function, FastListMultimap.newMultimap());
    }

    /**
     * @see Iterate#groupBy(Iterable, Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupBy(
            List<T> list,
            Function<? super T, ? extends V> function,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.groupBy(list, function, target);
        }
        return IterableIterate.groupBy(list, function, target);
    }

    /**
     * @see Iterate#groupByEach(Iterable, Function)
     */
    public static <T, V> FastListMultimap<V, T> groupByEach(
            List<T> list,
            Function<? super T, ? extends Iterable<V>> function)
    {
        return ListIterate.groupByEach(list, function, FastListMultimap.newMultimap());
    }

    /**
     * @see Iterate#groupByEach(Iterable, Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupByEach(
            List<T> list,
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.groupByEach(list, function, target);
        }
        return IterableIterate.groupByEach(list, function, target);
    }

    /**
     * @see Iterate#groupByUniqueKey(Iterable, Function)
     */
    public static <K, T> MutableMap<K, T> groupByUniqueKey(
            List<T> list,
            Function<? super T, ? extends K> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.groupByUniqueKey(list, function);
        }
        return IterableIterate.groupByUniqueKey(list, function);
    }

    /**
     * @see Iterate#groupByUniqueKey(Iterable, Function, MutableMapIterable)
     */
    public static <K, T, R extends MutableMapIterable<K, T>> R groupByUniqueKey(
            List<T> list,
            Function<? super T, ? extends K> function,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.groupByUniqueKey(list, function, target);
        }
        return IterableIterate.groupByUniqueKey(list, function, target);
    }

    /**
     * @see Iterate#min(Iterable, Comparator)
     */
    public static <T> T min(List<T> list, Comparator<? super T> comparator)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.min(list, comparator);
        }
        return IterableIterate.min(list, comparator);
    }

    public static <T, V extends Comparable<? super V>> T minBy(List<T> list, Function<? super T, ? extends V> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.minBy(list, function);
        }
        return IterableIterate.minBy(list, function);
    }

    public static <T, V extends Comparable<? super V>> T maxBy(List<T> list, Function<? super T, ? extends V> function)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.maxBy(list, function);
        }
        return IterableIterate.maxBy(list, function);
    }

    /**
     * @see Iterate#max(Iterable, Comparator)
     */
    public static <T> T max(List<T> list, Comparator<? super T> comparator)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.max(list, comparator);
        }
        return IterableIterate.max(list, comparator);
    }

    /**
     * @see Iterate#min(Iterable)
     */
    public static <T> T min(List<T> list)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.min(list);
        }
        return IterableIterate.min(list);
    }

    /**
     * @see Iterate#max(Iterable)
     */
    public static <T> T max(List<T> list)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.max(list);
        }
        return IterableIterate.max(list);
    }

    /**
     * @see Iterate#chunk(Iterable, int)
     */
    public static <T> RichIterable<RichIterable<T>> chunk(List<T> list, int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }

        Iterator<T> iterator = list.iterator();
        MutableList<RichIterable<T>> result = Lists.mutable.empty();
        while (iterator.hasNext())
        {
            MutableCollection<T> batch = Lists.mutable.empty();
            for (int i = 0; i < size && iterator.hasNext(); i++)
            {
                batch.add(iterator.next());
            }
            result.add(batch);
        }
        return result;
    }

    /**
     * @see Iterate#zip(Iterable, Iterable)
     */
    public static <X, Y> MutableList<Pair<X, Y>> zip(
            List<X> list,
            Iterable<Y> iterable)
    {
        if (iterable instanceof Collection || iterable instanceof RichIterable)
        {
            int listSize = list.size();
            int iterableSize = Iterate.sizeOf(iterable);
            MutableList<Pair<X, Y>> target = FastList.newList(Math.min(listSize, iterableSize));
            return ListIterate.zip(list, iterable, target);
        }
        return ListIterate.zip(list, iterable, FastList.newList());
    }

    /**
     * @see Iterate#zip(Iterable, Iterable, Collection)
     */
    public static <X, Y, R extends Collection<Pair<X, Y>>> R zip(
            List<X> list,
            Iterable<Y> iterable,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.zip(list, iterable, target);
        }
        return IterableIterate.zip(list, iterable, target);
    }

    /**
     * @see Iterate#zipWithIndex(Iterable)
     */
    public static <T> MutableList<Pair<T, Integer>> zipWithIndex(List<T> list)
    {
        return ListIterate.zipWithIndex(list, Lists.mutable.withInitialCapacity(list.size()));
    }

    /**
     * @see Iterate#zipWithIndex(Iterable, Collection)
     */
    public static <T, R extends Collection<Pair<T, Integer>>> R zipWithIndex(
            List<T> list,
            R target)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.zipWithIndex(list, target);
        }
        return IterableIterate.zipWithIndex(list, target);
    }

    /**
     * @see ListIterable#takeWhile(Predicate)
     */
    public static <T> MutableList<T> takeWhile(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.takeWhile(list, predicate);
        }
        MutableList<T> result = FastList.newList();
        for (T t : list)
        {
            if (predicate.accept(t))
            {
                result.add(t);
            }
            else
            {
                return result;
            }
        }
        return result;
    }

    /**
     * @see ListIterable#dropWhile(Predicate)
     */
    public static <T> MutableList<T> dropWhile(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.dropWhile(list, predicate);
        }
        MutableList<T> result = FastList.newList();
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (!predicate.accept(each))
            {
                result.add(each);
                while (iterator.hasNext())
                {
                    T eachNotDropped = iterator.next();
                    result.add(eachNotDropped);
                }
                return result;
            }
        }
        return result;
    }

    /**
     * @see ListIterable#partitionWhile(Predicate)
     */
    public static <T> PartitionMutableList<T> partitionWhile(List<T> list, Predicate<? super T> predicate)
    {
        if (list instanceof RandomAccess)
        {
            return RandomAccessListIterate.partitionWhile(list, predicate);
        }
        PartitionMutableList<T> result = new PartitionFastList<>();
        MutableList<T> selected = result.getSelected();
        MutableList<T> rejected = result.getRejected();

        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext())
        {
            T each = iterator.next();
            if (predicate.accept(each))
            {
                selected.add(each);
            }
            else
            {
                rejected.add(each);
                while (iterator.hasNext())
                {
                    rejected.add(iterator.next());
                }
                return result;
            }
        }
        return result;
    }
}
