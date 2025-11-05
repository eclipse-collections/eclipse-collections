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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.ByteLists;
import org.eclipse.collections.api.factory.primitive.CharLists;
import org.eclipse.collections.api.factory.primitive.DoubleLists;
import org.eclipse.collections.api.factory.primitive.FloatLists;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.factory.primitive.ShortLists;
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
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.Functions0;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.block.procedure.MapCollectProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.internal.InternalArrayIterate;

/**
 * ArrayIterate provides optimized iteration patterns for Java arrays.
 * This utility class offers functional-style operations (map, filter, reduce, etc.) on arrays
 * without the overhead of converting to collections first.
 * <p>
 * All methods in this class are static and operate directly on arrays, providing high-performance
 * alternatives to Stream API or collection-based approaches. Operations include filtering (select/reject),
 * transformation (collect), aggregation (injectInto), and element search (detect).
 * </p>
 * <p><b>Thread Safety:</b> Methods are not thread-safe unless the array itself is thread-safe.
 * Operations do not modify the input array.</p>
 * <p><b>Performance:</b> Optimized for array iteration. Significantly faster than Stream API
 * for simple operations. Uses low-level array access for maximum performance.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * String[] names = {"Alice", "Bob", "Charlie", "David"};
 *
 * // Filter elements
 * MutableList<String> longNames = ArrayIterate.select(names, name -> name.length() > 4);
 * // Result: ["Alice", "Charlie", "David"]
 *
 * // Transform elements
 * MutableList<Integer> lengths = ArrayIterate.collect(names, String::length);
 * // Result: [5, 3, 7, 5]
 *
 * // Find first match
 * String found = ArrayIterate.detect(names, name -> name.startsWith("C"));
 * // Result: "Charlie"
 *
 * // Count matches
 * int count = ArrayIterate.count(names, name -> name.length() > 4);
 * // Result: 3
 *
 * // Apply procedure to each element
 * ArrayIterate.forEach(names, System.out::println);
 * }</pre>
 *
 * @since 1.0
 */
public final class ArrayIterate
{
    private ArrayIterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <T> void reverse(T[] array, int size)
    {
        int mid = size / 2;
        int j = size - 1;
        for (int i = 0; i < mid; i++, j--)
        {
            T one = array[i];
            T two = array[j];
            array[i] = two;
            array[j] = one;
        }
    }

    /**
     * Inline calls to appropriate Arrays.sort() method which now uses TimSort by default since Java 8.
     *
     * @deprecated in 7.0. Use {@link Arrays#sort(Object[], int, int)} or {@link Arrays#sort(Object[], int, int, Comparator)} instead.
     */
    @Deprecated
    public static <T> void sort(T[] array, int size, Comparator<? super T> comparator)
    {
        if (comparator == null)
        {
            Arrays.sort(array, 0, size); // handles case size < 2 in Java 8 ComparableTimSort
        }
        else
        {
            Arrays.sort(array, 0, size, comparator); // handles case size < 2 in Java 8 TimSort
        }
    }

    public static <T, V extends Comparable<? super V>> T minBy(T[] array, Function<? super T, ? extends V> function)
    {
        return InternalArrayIterate.minBy(array, array.length, function);
    }

    public static <T, V extends Comparable<? super V>> T maxBy(T[] array, Function<? super T, ? extends V> function)
    {
        return InternalArrayIterate.maxBy(array, array.length, function);
    }

    /**
     * @see Iterate#min(Iterable, Comparator)
     */
    public static <T> T min(T[] array, Comparator<? super T> comparator)
    {
        return InternalArrayIterate.min(array, array.length, comparator);
    }

    /**
     * @see Iterate#max(Iterable, Comparator)
     */
    public static <T> T max(T[] array, Comparator<? super T> comparator)
    {
        return InternalArrayIterate.max(array, array.length, comparator);
    }

    /**
     * @see Iterate#min(Iterable)
     */
    public static <T> T min(T... array)
    {
        return InternalArrayIterate.min(array, array.length);
    }

    /**
     * @see Iterate#max(Iterable)
     */
    public static <T> T max(T... array)
    {
        return InternalArrayIterate.max(array, array.length);
    }

    /**
     * @see Iterate#select(Iterable, Predicate)
     */
    public static <T> MutableList<T> select(T[] objectArray, Predicate<? super T> predicate)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a select on null");
        }

        return ArrayIterate.select(objectArray, predicate, FastList.newList());
    }

    /**
     * @see Iterate#selectWith(Iterable, Predicate2, Object)
     */
    public static <T, P> MutableList<T> selectWith(
            T[] objectArray,
            Predicate2<? super T, P> predicate,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a selectWith on null");
        }
        return ArrayIterate.selectWith(objectArray, predicate, parameter, FastList.newList());
    }

    /**
     * @see Iterate#selectInstancesOf(Iterable, Class)
     */
    public static <T> FastList<T> selectInstancesOf(Object[] objectArray, Class<T> clazz)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a selectInstancesOf on null");
        }

        return InternalArrayIterate.selectInstancesOf(objectArray, objectArray.length, clazz);
    }

    /**
     * @see Iterate#count(Iterable, Predicate)
     */
    public static <T> int count(T[] objectArray, Predicate<? super T> predicate)
    {
        if (objectArray == null)
        {
            return 0;
        }
        return InternalArrayIterate.count(objectArray, objectArray.length, predicate);
    }

    /**
     * @see Iterate#countWith(Iterable, Predicate2, Object)
     */
    public static <T, P> int countWith(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (objectArray == null)
        {
            return 0;
        }
        return InternalArrayIterate.countWith(objectArray, objectArray.length, predicate, parameter);
    }

    /**
     * @see Iterate#selectAndRejectWith(Iterable, Predicate2, Object)
     * @deprecated since 6.0 use {@link RichIterable#partitionWith(Predicate2, Object)} instead.
     */
    @Deprecated
    public static <T, P> Twin<MutableList<T>> selectAndRejectWith(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a selectAndRejectWith on null");
        }

        return InternalArrayIterate.selectAndRejectWith(objectArray, objectArray.length, predicate, parameter);
    }

    /**
     * @see Iterate#partition(Iterable, Predicate)
     */
    public static <T> PartitionFastList<T> partition(T[] array, Predicate<? super T> predicate)
    {
        if (array == null)
        {
            throw new IllegalArgumentException("Cannot perform a partition on null");
        }
        return InternalArrayIterate.partition(array, array.length, predicate);
    }

    public static <T, P> PartitionFastList<T> partitionWith(T[] array, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        if (array == null)
        {
            throw new IllegalArgumentException("Cannot perform a partitionWith on null");
        }
        return InternalArrayIterate.partitionWith(array, array.length, predicate, parameter);
    }

    /**
     * @see Iterate#collectIf(Iterable, Predicate, Function)
     */
    public static <T, V> MutableList<V> collectIf(
            T[] objectArray,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectIf on null");
        }

        return ArrayIterate.collectIf(
                objectArray,
                predicate,
                function,
                Lists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#select(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R select(
            T[] objectArray,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a select on null");
        }
        return InternalArrayIterate.select(objectArray, objectArray.length, predicate, targetCollection);
    }

    /**
     * @see Iterate#selectWith(Iterable, Predicate2, Object, Collection)
     */
    public static <T, P, R extends Collection<T>> R selectWith(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a selectWith on null");
        }
        return InternalArrayIterate.selectWith(objectArray, objectArray.length, predicate, parameter, targetCollection);
    }

    /**
     * @see Iterate#collectIf(Iterable, Predicate, Function, Collection)
     */
    public static <T, V, R extends Collection<V>> R collectIf(
            T[] objectArray,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R targetCollection)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectIf on null");
        }
        return InternalArrayIterate.collectIf(objectArray, objectArray.length, predicate, function, targetCollection);
    }

    /**
     * @see Iterate#reject(Iterable, Predicate)
     */
    public static <T> MutableList<T> reject(T[] objectArray, Predicate<? super T> predicate)
    {
        return ArrayIterate.reject(objectArray, predicate, FastList.newList());
    }

    /**
     * @see Iterate#rejectWith(Iterable, Predicate2, Object)
     */
    public static <T, P> MutableList<T> rejectWith(
            T[] objectArray,
            Predicate2<? super T, P> predicate,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a rejectWith on null");
        }
        return ArrayIterate.rejectWith(
                objectArray,
                predicate,
                parameter,
                FastList.newList());
    }

    /**
     * @see Iterate#reject(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R reject(
            T[] objectArray,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a reject on null");
        }
        return InternalArrayIterate.reject(objectArray, objectArray.length, predicate, targetCollection);
    }

    /**
     * @see Iterate#rejectWith(Iterable, Predicate2, Object, Collection)
     */
    public static <T, P, R extends Collection<T>> R rejectWith(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a rejectWith on null");
        }
        return InternalArrayIterate.rejectWith(objectArray, objectArray.length, predicate, parameter, targetCollection);
    }

    /**
     * @see Iterate#addAllTo(Iterable, Collection)
     */
    public static <T, R extends Collection<T>> R addAllTo(T[] objectArray, R targetCollection)
    {
        ArrayIterate.forEachWith(objectArray, Procedures2.addToCollection(), targetCollection);
        return targetCollection;
    }

    /**
     * @see Iterate#collect(Iterable, Function)
     */
    public static <T, V> MutableList<V> collect(
            T[] objectArray,
            Function<? super T, ? extends V> function)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collect on null");
        }
        return ArrayIterate.collect(objectArray, function, Lists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collect(Iterable, Function, Collection)
     */
    public static <T, V, R extends Collection<V>> R collect(
            T[] objectArray,
            Function<? super T, ? extends V> function,
            R targetCollection)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collect on null");
        }
        return InternalArrayIterate.collect(objectArray, objectArray.length, function, targetCollection);
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction)
     */
    public static <T> MutableBooleanList collectBoolean(T[] objectArray, BooleanFunction<? super T> booleanFunction)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectBoolean on null");
        }
        return ArrayIterate.collectBoolean(objectArray, booleanFunction, BooleanLists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction, MutableBooleanCollection)
     */
    public static <T, R extends MutableBooleanCollection> R collectBoolean(
            T[] objectArray,
            BooleanFunction<? super T> booleanFunction,
            R target)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectBoolean on null");
        }
        return InternalArrayIterate.collectBoolean(objectArray, objectArray.length, booleanFunction, target);
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction)
     */
    public static <T> MutableByteList collectByte(T[] objectArray, ByteFunction<? super T> byteFunction)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectByte on null");
        }
        return ArrayIterate.collectByte(objectArray, byteFunction, ByteLists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction, MutableByteCollection)
     */
    public static <T, R extends MutableByteCollection> R collectByte(
            T[] objectArray,
            ByteFunction<? super T> byteFunction,
            R target)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectByte on null");
        }
        return InternalArrayIterate.collectByte(objectArray, objectArray.length, byteFunction, target);
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction)
     */
    public static <T> MutableCharList collectChar(T[] objectArray, CharFunction<? super T> charFunction)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectChar on null");
        }
        return ArrayIterate.collectChar(objectArray, charFunction, CharLists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction, MutableCharCollection)
     */
    public static <T, R extends MutableCharCollection> R collectChar(
            T[] objectArray,
            CharFunction<? super T> charFunction,
            R target)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectChar on null");
        }
        return InternalArrayIterate.collectChar(objectArray, objectArray.length, charFunction, target);
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction)
     */
    public static <T> MutableDoubleList collectDouble(T[] objectArray, DoubleFunction<? super T> doubleFunction)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectDouble on null");
        }
        return ArrayIterate.collectDouble(objectArray, doubleFunction, DoubleLists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction, MutableDoubleCollection)
     */
    public static <T, R extends MutableDoubleCollection> R collectDouble(
            T[] objectArray,
            DoubleFunction<? super T> doubleFunction,
            R target)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectDouble on null");
        }
        return InternalArrayIterate.collectDouble(objectArray, objectArray.length, doubleFunction, target);
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction)
     */
    public static <T> MutableFloatList collectFloat(T[] objectArray, FloatFunction<? super T> floatFunction)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectFloat on null");
        }
        return ArrayIterate.collectFloat(objectArray, floatFunction, FloatLists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction, MutableFloatCollection)
     */
    public static <T, R extends MutableFloatCollection> R collectFloat(
            T[] objectArray,
            FloatFunction<? super T> floatFunction,
            R target)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectFloat on null");
        }
        return InternalArrayIterate.collectFloat(objectArray, objectArray.length, floatFunction, target);
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction)
     */
    public static <T> MutableIntList collectInt(
            T[] objectArray,
            IntFunction<? super T> intFunction)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectInt on null");
        }
        return ArrayIterate.collectInt(objectArray, intFunction, IntLists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction, MutableIntCollection)
     */
    public static <T, R extends MutableIntCollection> R collectInt(
            T[] objectArray,
            IntFunction<? super T> intFunction,
            R target)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectInt on null");
        }
        return InternalArrayIterate.collectInt(objectArray, objectArray.length, intFunction, target);
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction)
     */
    public static <T> MutableLongList collectLong(
            T[] objectArray,
            LongFunction<? super T> longFunction)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectLong on null");
        }
        return ArrayIterate.collectLong(objectArray, longFunction, LongLists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction, MutableLongCollection)
     */
    public static <T, R extends MutableLongCollection> R collectLong(
            T[] objectArray,
            LongFunction<? super T> longFunction,
            R target)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectLong on null");
        }
        return InternalArrayIterate.collectLong(objectArray, objectArray.length, longFunction, target);
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction)
     */
    public static <T> MutableShortList collectShort(
            T[] objectArray,
            ShortFunction<? super T> shortFunction)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectShort on null");
        }
        return ArrayIterate.collectShort(objectArray, shortFunction, ShortLists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction, MutableShortCollection)
     */
    public static <T, R extends MutableShortCollection> R collectShort(
            T[] objectArray,
            ShortFunction<? super T> shortFunction,
            R target)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectShort on null");
        }
        return InternalArrayIterate.collectShort(objectArray, objectArray.length, shortFunction, target);
    }

    /**
     * @see Iterate#flatCollect(Iterable, Function)
     */

    public static <T, V> MutableList<V> flatCollect(
            T[] objectArray,
            Function<? super T, ? extends Iterable<V>> function)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a flatCollect on null");
        }
        return ArrayIterate.flatCollect(objectArray, function, Lists.mutable.withInitialCapacity(objectArray.length));
    }

    public static <T, V, R extends Collection<V>> R flatCollect(
            T[] objectArray,
            Function<? super T, ? extends Iterable<V>> function,
            R targetCollection)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a flatCollect on null");
        }
        return InternalArrayIterate.flatCollect(objectArray, objectArray.length, function, targetCollection);
    }

    /**
     * Returns the first element of an array. This method is null safe.
     */
    public static <T> T getFirst(T[] objectArray)
    {
        if (ArrayIterate.notEmpty(objectArray))
        {
            return objectArray[0];
        }
        return null;
    }

    /**
     * Returns the last element of an Array. This method is null safe.
     */
    public static <T> T getLast(T[] objectArray)
    {
        if (ArrayIterate.notEmpty(objectArray))
        {
            return objectArray[objectArray.length - 1];
        }
        return null;
    }

    /**
     * @see Iterate#forEach(Iterable, Procedure)
     */
    public static <T> void forEach(T[] objectArray, Procedure<? super T> procedure)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a forEach on null");
        }
        if (ArrayIterate.notEmpty(objectArray))
        {
            for (T each : objectArray)
            {
                procedure.value(each);
            }
        }
    }

    /**
     * Iterates over the section of the list covered by the specified inclusive indexes. The indexes are
     * both inclusive.
     */
    public static <T> void forEach(T[] objectArray, int from, int to, Procedure<? super T> procedure)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a forEach on null");
        }

        ListIterate.rangeCheck(from, to, objectArray.length);
        InternalArrayIterate.forEachWithoutChecks(objectArray, from, to, procedure);
    }

    /**
     * @see ListIterate#forEachInBoth(List, List, Procedure2)
     */
    public static <T1, T2> void forEachInBoth(
            T1[] objectArray1,
            T2[] objectArray2,
            Procedure2<? super T1, ? super T2> procedure)
    {
        if (objectArray1 != null && objectArray2 != null)
        {
            if (objectArray1.length == objectArray2.length)
            {
                int size = objectArray1.length;
                for (int i = 0; i < size; i++)
                {
                    procedure.value(objectArray1[i], objectArray2[i]);
                }
            }
            else
            {
                throw new RuntimeException("Attempt to call forEachInBoth with two arrays of different sizes :"
                        + objectArray1.length + ':' + objectArray2.length);
            }
        }
    }

    /**
     * @see Iterate#forEachWithIndex(Iterable, ObjectIntProcedure)
     */
    public static <T> void forEachWithIndex(T[] objectArray, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a forEachWithIndex on null");
        }

        InternalArrayIterate.forEachWithIndex(objectArray, objectArray.length, objectIntProcedure);
    }

    /**
     * Iterates over the section of the list covered by the specified inclusive indexes. The indexes are
     * both inclusive.
     */
    public static <T> void forEachWithIndex(
            T[] objectArray,
            int from, int to,
            ObjectIntProcedure<? super T> objectIntProcedure)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a forEachWithIndex on null");
        }

        ListIterate.rangeCheck(from, to, objectArray.length);
        InternalArrayIterate.forEachWithIndexWithoutChecks(objectArray, from, to, objectIntProcedure);
    }

    /**
     * @see Iterate#detect(Iterable, Predicate)
     */
    public static <T> T detect(T[] objectArray, Predicate<? super T> predicate)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a detect on null");
        }
        return InternalArrayIterate.detect(objectArray, objectArray.length, predicate);
    }

    /**
     * @see Iterate#detectWith(Iterable, Predicate2, Object)
     */
    public static <T, P> T detectWith(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a detectWith on null");
        }
        return InternalArrayIterate.detectWith(objectArray, objectArray.length, predicate, parameter);
    }

    /**
     * @see Iterate#detectOptional(Iterable, Predicate)
     */
    public static <T> Optional<T> detectOptional(T[] objectArray, Predicate<? super T> predicate)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a detect on null");
        }
        return InternalArrayIterate.detectOptional(objectArray, objectArray.length, predicate);
    }

    /**
     * @see Iterate#detectWithOptional(Iterable, Predicate2, Object)
     */
    public static <T, P> Optional<T> detectWithOptional(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a detectWith on null");
        }
        return InternalArrayIterate.detectWithOptional(objectArray, objectArray.length, predicate, parameter);
    }

    /**
     * @see Iterate#detectIfNone(Iterable, Predicate, Object)
     */
    public static <T> T detectIfNone(
            T[] objectArray,
            Predicate<? super T> predicate,
            T ifNone)
    {
        T result = ArrayIterate.detect(objectArray, predicate);
        return result == null ? ifNone : result;
    }

    /**
     * @see Iterate#detectWithIfNone(Iterable, Predicate2, Object, Object)
     */
    public static <T, P> T detectWithIfNone(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            T ifNone)
    {
        T result = ArrayIterate.detectWith(objectArray, predicate, parameter);
        return result == null ? ifNone : result;
    }

    /**
     * @see Iterate#injectInto(Object, Iterable, Function2)
     */
    public static <T, IV> IV injectInto(
            IV injectValue,
            T[] objectArray,
            Function2<? super IV, ? super T, ? extends IV> function)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform an injectInto on null");
        }
        IV result = injectValue;
        for (T each : objectArray)
        {
            result = function.value(result, each);
        }
        return result;
    }

    /**
     * @see Iterate#injectInto(int, Iterable, IntObjectToIntFunction)
     */
    public static <T> int injectInto(
            int injectValue,
            T[] objectArray,
            IntObjectToIntFunction<? super T> function)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform an injectInto on null");
        }
        int result = injectValue;
        if (ArrayIterate.notEmpty(objectArray))
        {
            for (T each : objectArray)
            {
                result = function.intValueOf(result, each);
            }
        }
        return result;
    }

    /**
     * @see Iterate#injectInto(long, Iterable, LongObjectToLongFunction)
     */
    public static <T> long injectInto(
            long injectValue,
            T[] objectArray,
            LongObjectToLongFunction<? super T> function)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform an injectInto on null");
        }
        long result = injectValue;
        if (ArrayIterate.notEmpty(objectArray))
        {
            for (T each : objectArray)
            {
                result = function.longValueOf(result, each);
            }
        }
        return result;
    }

    /**
     * @see Iterate#injectInto(float, Iterable, FloatObjectToFloatFunction)
     * @since 6.0
     */
    public static <T> float injectInto(float injectValue, T[] objectArray, FloatObjectToFloatFunction<? super T> function)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform an injectInto on null");
        }
        float result = injectValue;
        if (ArrayIterate.notEmpty(objectArray))
        {
            for (T each : objectArray)
            {
                result = function.floatValueOf(result, each);
            }
        }
        return result;
    }

    /**
     * @see Iterate#injectInto(double, Iterable, DoubleObjectToDoubleFunction)
     */
    public static <T> double injectInto(
            double injectValue,
            T[] objectArray,
            DoubleObjectToDoubleFunction<? super T> function)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform an injectInto on null");
        }
        double result = injectValue;
        if (ArrayIterate.notEmpty(objectArray))
        {
            for (T each : objectArray)
            {
                result = function.doubleValueOf(result, each);
            }
        }
        return result;
    }

    /**
     * @deprecated in 7.0.
     */
    @Deprecated
    public static <T, R extends List<T>> R distinct(T[] objectArray, R targetList)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a distinct on null");
        }
        return InternalArrayIterate.distinct(objectArray, objectArray.length, targetList);
    }

    /**
     * @since 7.0.
     */
    public static <T> MutableList<T> distinct(T[] objectArray)
    {
        return ArrayIterate.distinct(objectArray, FastList.newList());
    }

    /**
     * @since 7.0.
     */
    public static <T> MutableList<T> distinct(T[] objectArray, HashingStrategy<? super T> hashingStrategy)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a distinct on null");
        }
        return InternalArrayIterate.distinct(objectArray, objectArray.length, hashingStrategy);
    }

    /**
     * Returns {@code true} if the specified array contains the specified element.
     */
    public static <T> boolean contains(T[] objectArray, T value)
    {
        return ArrayIterate.anySatisfyWith(objectArray, Predicates2.equal(), value);
    }

    /**
     * Returns {@code true} if the specified int array contains the specified int element.
     */
    public static boolean contains(int[] intArray, int value)
    {
        for (int each : intArray)
        {
            if (each == value)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the specified double array contains the specified double element.
     */
    public static boolean contains(double[] doubleArray, double value)
    {
        for (double each : doubleArray)
        {
            if (Double.doubleToLongBits(each) == Double.doubleToLongBits(value))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the specified long array contains the specified long element.
     */
    public static boolean contains(long[] longArray, long value)
    {
        for (long each : longArray)
        {
            if (each == value)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Searches for the first occurrence of the given argument, testing
     * for equality using the {@code equals} method.
     */
    public static <T> int indexOf(T[] objectArray, T elem)
    {
        return InternalArrayIterate.indexOf(objectArray, objectArray.length, elem);
    }

    /**
     * Returns the first index where the predicate evaluates to {@code true}. Returns -1 for no matches.
     */
    public static <T> int detectIndex(T[] objectArray, Predicate<? super T> predicate)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a detectIndex on null");
        }
        return InternalArrayIterate.detectIndex(objectArray, objectArray.length, predicate);
    }

    /**
     * Searches for the first index where the predicate evaluates to {@code true}. Returns -1 for no matches.
     */
    public static <T, IV> int detectIndexWith(
            T[] objectArray,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a detectIndexWith on null");
        }
        for (int i = 0; i < objectArray.length; i++)
        {
            if (predicate.accept(objectArray[i], injectedValue))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index where the predicate evaluates to {@code true}.
     * Returns -1 for no matches.
     */
    public static <T> int detectLastIndex(T[] objectArray, Predicate<? super T> predicate)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a detectLastIndex on null");
        }
        return InternalArrayIterate.detectLastIndex(objectArray, objectArray.length, predicate);
    }

    /**
     * A null safe form of isEmpty. Null or length of zero returns {@code true}.
     */
    public static boolean isEmpty(Object[] array)
    {
        return array == null || array.length == 0;
    }

    /**
     * A null safe form of notEmpty. The opposite of isEmpty is returned.
     */
    public static boolean notEmpty(Object[] array)
    {
        return array != null && array.length > 0;
    }

    /**
     * Return the size of the array.
     */
    public static int size(Object[] array)
    {
        return array == null ? 0 : array.length;
    }

    /**
     * @see Iterate#anySatisfy(Iterable, Predicate)
     */
    public static <T> boolean anySatisfy(T[] objectArray, Predicate<? super T> predicate)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a anySatisfy on null");
        }
        return InternalArrayIterate.anySatisfy(objectArray, objectArray.length, predicate);
    }

    /**
     * @see Iterate#anySatisfyWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean anySatisfyWith(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a anySatisfyWith on null");
        }
        return InternalArrayIterate.anySatisfyWith(objectArray, objectArray.length, predicate, parameter);
    }

    /**
     * @see Iterate#allSatisfy(Iterable, Predicate)
     */
    public static <T> boolean allSatisfy(T[] objectArray, Predicate<? super T> predicate)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a allSatisfy on null");
        }
        return InternalArrayIterate.allSatisfy(objectArray, objectArray.length, predicate);
    }

    /**
     * @see Iterate#allSatisfyWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean allSatisfyWith(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a allSatisfyWith on null");
        }
        return InternalArrayIterate.allSatisfyWith(objectArray, objectArray.length, predicate, parameter);
    }

    /**
     * @see Iterate#noneSatisfy(Iterable, Predicate)
     */
    public static <T> boolean noneSatisfy(T[] objectArray, Predicate<? super T> predicate)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a noneSatisfy on null");
        }
        return InternalArrayIterate.noneSatisfy(objectArray, objectArray.length, predicate);
    }

    /**
     * @see Iterate#noneSatisfyWith(Iterable, Predicate2, Object)
     */
    public static <T, P> boolean noneSatisfyWith(
            T[] objectArray,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a noneSatisfyWith on null");
        }
        return InternalArrayIterate.noneSatisfyWith(objectArray, objectArray.length, predicate, parameter);
    }

    /**
     * Iterate over the specified array applying the specified Function to each element to calculate a key
     * and return the results as a HashMap.
     */
    public static <K, V> MutableMap<K, V> toMap(
            V[] objectArray,
            Function<? super V, ? extends K> keyFunction)
    {
        MutableMap<K, V> map = Maps.mutable.empty();
        Procedure<V> procedure = new MapCollectProcedure<>(map, keyFunction);
        ArrayIterate.forEach(objectArray, procedure);
        return map;
    }

    /**
     * Iterate over the specified array applying the specified Functions to each element to calculate a key
     * and value, and return the results as a Map.
     */
    public static <T, K, V> MutableMap<K, V> toMap(
            T[] objectArray,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableMap<K, V> map = Maps.mutable.empty();
        Procedure<T> procedure = new MapCollectProcedure<>(map, keyFunction, valueFunction);
        ArrayIterate.forEach(objectArray, procedure);
        return map;
    }

    /**
     * @see Iterate#forEachWith(Iterable, Procedure2, Object)
     */
    public static <T, P> void forEachWith(
            T[] objectArray,
            Procedure2<? super T, ? super P> procedure,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a forEachWith on null");
        }
        if (ArrayIterate.notEmpty(objectArray))
        {
            for (T each : objectArray)
            {
                procedure.value(each, parameter);
            }
        }
    }

    /**
     * @see Iterate#collectWith(Iterable, Function2, Object)
     */
    public static <T, P, V> MutableList<V> collectWith(
            T[] objectArray,
            Function2<? super T, ? super P, ? extends V> function,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectWith on null");
        }
        return ArrayIterate.collectWith(objectArray, function, parameter, Lists.mutable.withInitialCapacity(objectArray.length));
    }

    /**
     * @see Iterate#collectWith(Iterable, Function2, Object, Collection)
     */
    public static <T, P, V, R extends Collection<V>> R collectWith(
            T[] objectArray,
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R targetCollection)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform a collectWith on null");
        }
        return InternalArrayIterate.collectWith(objectArray, objectArray.length, function, parameter, targetCollection);
    }

    /**
     * @see Iterate#injectIntoWith(Object, Iterable, Function3, Object)
     */
    public static <T, IV, P> IV injectIntoWith(
            IV injectValue,
            T[] objectArray,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Cannot perform an injectIntoWith on null");
        }
        IV result = injectValue;
        if (ArrayIterate.notEmpty(objectArray))
        {
            for (T each : objectArray)
            {
                result = function.value(result, each, parameter);
            }
        }
        return result;
    }

    /**
     * @see Iterate#take(Iterable, int)
     */
    public static <T> MutableList<T> take(T[] array, int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return ArrayIterate.take(array, count, Lists.mutable.withInitialCapacity(Math.min(array.length, count)));
    }

    /**
     * @see Iterate#take(Iterable, int)
     */
    public static <T, R extends Collection<T>> R take(T[] array, int count, R target)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        int end = Math.min(array.length, count);
        for (int i = 0; i < end; i++)
        {
            target.add(array[i]);
        }
        return target;
    }

    /**
     * @see Iterate#drop(Iterable, int)
     */
    public static <T> MutableList<T> drop(T[] array, int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return ArrayIterate.drop(array, count, Lists.mutable.withInitialCapacity(array.length - Math.min(array.length, count)));
    }

    /**
     * @see Iterate#drop(Iterable, int)
     */
    public static <T, R extends Collection<T>> R drop(T[] array, int count, R target)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        for (int i = count; i < array.length; i++)
        {
            target.add(array[i]);
        }
        return target;
    }

    /**
     * @see Iterate#groupBy(Iterable, Function)
     */
    public static <T, V> FastListMultimap<V, T> groupBy(
            T[] array,
            Function<? super T, ? extends V> function)
    {
        return ArrayIterate.groupBy(array, function, FastListMultimap.newMultimap());
    }

    /**
     * @see Iterate#groupBy(Iterable, Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupBy(
            T[] array,
            Function<? super T, ? extends V> function,
            R target)
    {
        return InternalArrayIterate.groupBy(array, array.length, function, target);
    }

    /**
     * @see Iterate#groupByEach(Iterable, Function)
     */
    public static <T, V> FastListMultimap<V, T> groupByEach(
            T[] array,
            Function<? super T, ? extends Iterable<V>> function)
    {
        return ArrayIterate.groupByEach(array, function, FastListMultimap.newMultimap());
    }

    /**
     * @see Iterate#groupByEach(Iterable, Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupByEach(
            T[] array,
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return InternalArrayIterate.groupByEach(array, array.length, function, target);
    }

    /**
     * @see Iterate#groupByUniqueKey(Iterable, Function)
     */
    public static <T, V> MutableMapIterable<V, T> groupByUniqueKey(
            T[] array,
            Function<? super T, ? extends V> function)
    {
        return ArrayIterate.groupByUniqueKey(array, function, Maps.mutable.empty());
    }

    /**
     * @see Iterate#groupByUniqueKey(Iterable, Function, MutableMapIterable)
     */
    public static <T, V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(
            T[] array,
            Function<? super T, ? extends V> function,
            R target)
    {
        if (array == null)
        {
            throw new IllegalArgumentException("Cannot perform a groupByUniqueKey on null");
        }
        return InternalArrayIterate.groupByUniqueKey(array, array.length, function, target);
    }

    /**
     * @see Iterate#chunk(Iterable, int)
     */
    public static <T> RichIterable<RichIterable<T>> chunk(T[] array, int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }
        int index = 0;
        MutableList<RichIterable<T>> result = Lists.mutable.empty();
        while (index < array.length)
        {
            MutableList<T> batch = Lists.mutable.empty();
            for (int i = 0; i < size && index < array.length; i++)
            {
                batch.add(array[index]);
                index++;
            }
            result.add(batch);
        }
        return result;
    }

    /**
     * @see Iterate#zip(Iterable, Iterable)
     */
    public static <X, Y> MutableList<Pair<X, Y>> zip(X[] xs, Y[] ys)
    {
        return ArrayIterate.zip(xs, ys, FastList.newList());
    }

    /**
     * @see Iterate#zip(Iterable, Iterable, Collection)
     */
    public static <X, Y, R extends Collection<Pair<X, Y>>> R zip(X[] xs, Y[] ys, R targetCollection)
    {
        int size = Math.min(xs.length, ys.length);
        for (int i = 0; i < size; i++)
        {
            targetCollection.add(Tuples.pair(xs[i], ys[i]));
        }
        return targetCollection;
    }

    /**
     * @see Iterate#zipWithIndex(Iterable)
     */
    public static <T> MutableList<Pair<T, Integer>> zipWithIndex(T... array)
    {
        return ArrayIterate.zipWithIndex(array, FastList.newList());
    }

    /**
     * @see Iterate#zipWithIndex(Iterable, Collection)
     */
    public static <T, R extends Collection<Pair<T, Integer>>> R zipWithIndex(T[] array, R targetCollection)
    {
        for (int i = 0; i < array.length; i++)
        {
            targetCollection.add(Tuples.pair(array[i], i));
        }
        return targetCollection;
    }

    /**
     * @see Iterate#makeString(Iterable)
     */
    public static <T> String makeString(T... array)
    {
        return ArrayIterate.makeString(array, ", ");
    }

    /**
     * @see Iterate#makeString(Iterable, String)
     */
    public static <T> String makeString(T[] array, String separator)
    {
        return ArrayIterate.makeString(array, "", separator, "");
    }

    /**
     * @see Iterate#makeString(Iterable, String, String, String)
     */
    public static <T> String makeString(T[] array, String start, String separator, String end)
    {
        Appendable stringBuilder = new StringBuilder();
        ArrayIterate.appendString(array, stringBuilder, start, separator, end);
        return stringBuilder.toString();
    }

    /**
     * @see Iterate#appendString(Iterable, Appendable)
     */
    public static <T> void appendString(T[] array, Appendable appendable)
    {
        ArrayIterate.appendString(array, appendable, ", ");
    }

    /**
     * @see Iterate#appendString(Iterable, Appendable, String)
     */
    public static <T> void appendString(T[] array, Appendable appendable, String separator)
    {
        ArrayIterate.appendString(array, appendable, "", separator, "");
    }

    /**
     * @see Iterate#appendString(Iterable, Appendable, String, String, String)
     */
    public static <T> void appendString(
            T[] array,
            Appendable appendable,
            String start,
            String separator,
            String end)
    {
        try
        {
            appendable.append(start);

            if (array.length > 0)
            {
                appendable.append(String.valueOf(array[0]));

                int size = array.length;
                for (int i = 1; i < size; i++)
                {
                    appendable.append(separator);
                    appendable.append(String.valueOf(array[i]));
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
     * @see Iterate#sumOfInt(Iterable, IntFunction)
     * @since 6.0
     */
    public static <T> long sumOfInt(T[] array, IntFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfInt(array, array.length, function);
    }

    /**
     * @see Iterate#sumOfLong(Iterable, LongFunction)
     * @since 6.0
     */
    public static <T> long sumOfLong(T[] array, LongFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfLong(array, array.length, function);
    }

    /**
     * @see Iterate#sumOfFloat(Iterable, FloatFunction)
     * @since 6.0
     */
    public static <T> double sumOfFloat(T[] array, FloatFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfFloat(array, array.length, function);
    }

    /**
     * @see Iterate#sumOfDouble(Iterable, DoubleFunction)
     * @since 6.0
     */
    public static <T> double sumOfDouble(T[] array, DoubleFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfDouble(array, array.length, function);
    }

    /**
     * @see Iterate#sumOfBigDecimal(Iterable, Function)
     * @since 6.0
     */
    public static <T> BigDecimal sumOfBigDecimal(T[] array, Function<? super T, BigDecimal> function)
    {
        BigDecimal result = BigDecimal.ZERO;
        for (T each : array)
        {
            result = result.add(function.valueOf(each));
        }
        return result;
    }

    /**
     * @see Iterate#sumOfBigInteger(Iterable, Function)
     * @since 6.0
     */
    public static <T> BigInteger sumOfBigInteger(T[] array, Function<? super T, BigInteger> function)
    {
        BigInteger result = BigInteger.ZERO;
        for (T each : array)
        {
            result = result.add(function.valueOf(each));
        }
        return result;
    }

    /**
     * @see Iterate#sumByBigDecimal(Iterable, Function, Function)
     * @since 6.0
     */
    public static <V, T> MutableMap<V, BigDecimal> sumByBigDecimal(T[] array, Function<? super T, ? extends V> groupBy, Function<? super T, BigDecimal> function)
    {
        MutableMap<V, BigDecimal> result = Maps.mutable.empty();
        for (T each : array)
        {
            result.updateValue(groupBy.valueOf(each), Functions0.zeroBigDecimal(), original -> original.add(function.valueOf(each)));
        }
        return result;
    }

    /**
     * @see Iterate#sumByBigInteger(Iterable, Function, Function)
     * @since 6.0
     */
    public static <V, T> MutableMap<V, BigInteger> sumByBigInteger(T[] array, Function<? super T, ? extends V> groupBy, Function<? super T, BigInteger> function)
    {
        MutableMap<V, BigInteger> result = Maps.mutable.empty();
        for (T each : array)
        {
            result.updateValue(groupBy.valueOf(each), Functions0.zeroBigInteger(), original -> original.add(function.valueOf(each)));
        }
        return result;
    }

    /**
     * @see Iterate#sumByInt(Iterable, Function, IntFunction)
     * @since 6.0
     */
    public static <V, T> ObjectLongMap<V> sumByInt(T[] array, Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        return InternalArrayIterate.sumByInt(array, array.length, groupBy, function);
    }

    /**
     * @see Iterate#sumByLong(Iterable, Function, LongFunction)
     * @since 6.0
     */
    public static <V, T> ObjectLongMap<V> sumByLong(T[] array, Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        return InternalArrayIterate.sumByLong(array, array.length, groupBy, function);
    }

    /**
     * @see Iterate#sumByFloat(Iterable, Function, FloatFunction)
     * @since 6.0
     */
    public static <V, T> ObjectDoubleMap<V> sumByFloat(T[] array, Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        return InternalArrayIterate.sumByFloat(array, array.length, groupBy, function);
    }

    /**
     * @see Iterate#sumByDouble(Iterable, Function, DoubleFunction)
     * @since 6.0
     */
    public static <V, T> ObjectDoubleMap<V> sumByDouble(T[] array, Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        return InternalArrayIterate.sumByDouble(array, array.length, groupBy, function);
    }
}
