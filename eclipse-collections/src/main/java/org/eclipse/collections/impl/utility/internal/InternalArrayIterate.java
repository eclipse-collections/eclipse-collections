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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
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
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.procedure.CountProcedure;
import org.eclipse.collections.impl.block.procedure.FastListCollectIfProcedure;
import org.eclipse.collections.impl.block.procedure.FastListCollectProcedure;
import org.eclipse.collections.impl.block.procedure.FastListRejectProcedure;
import org.eclipse.collections.impl.block.procedure.FastListSelectProcedure;
import org.eclipse.collections.impl.block.procedure.MultimapPutProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

@SuppressWarnings("TypeMayBeWeakened")
public final class InternalArrayIterate
{
    private InternalArrayIterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <T> boolean arrayEqualsList(T[] array, int size, List<?> list)
    {
        if (size != list.size())
        {
            return false;
        }
        if (list instanceof RandomAccess)
        {
            return InternalArrayIterate.randomAccessListEquals(array, size, list);
        }
        return InternalArrayIterate.nonRandomAccessListEquals(array, size, list);
    }

    private static <T> boolean randomAccessListEquals(T[] array, int size, List<?> list)
    {
        for (int i = 0; i < size; i++)
        {
            if (!Objects.equals(array[i], list.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    private static <T> boolean nonRandomAccessListEquals(T[] array, int size, List<?> list)
    {
        Iterator<?> iterator = list.iterator();
        for (int i = 0; i < size; i++)
        {
            if (!iterator.hasNext())
            {
                return false;
            }
            if (!Objects.equals(array[i], iterator.next()))
            {
                return false;
            }
        }
        return !iterator.hasNext();
    }

    public static <T> void forEachWithoutChecks(T[] objectArray, int from, int to, Procedure<? super T> procedure)
    {
        if (from <= to)
        {
            for (int i = from; i <= to; i++)
            {
                procedure.value(objectArray[i]);
            }
        }
        else
        {
            for (int i = from; i >= to; i--)
            {
                procedure.value(objectArray[i]);
            }
        }
    }

    public static <T> void forEachWithIndexWithoutChecks(T[] objectArray, int from, int to, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        if (from <= to)
        {
            for (int i = from; i <= to; i++)
            {
                objectIntProcedure.value(objectArray[i], i);
            }
        }
        else
        {
            for (int i = from; i >= to; i--)
            {
                objectIntProcedure.value(objectArray[i], i);
            }
        }
    }

    public static <T> void batchForEach(Procedure<? super T> procedure, T[] array, int size, int sectionIndex, int sectionCount)
    {
        int sectionSize = size / sectionCount;
        int start = sectionSize * sectionIndex;
        int end = sectionIndex == sectionCount - 1 ? size : start + sectionSize;
        if (procedure instanceof FastListSelectProcedure)
        {
            InternalArrayIterate.batchFastListSelect(array, start, end, (FastListSelectProcedure<T>) procedure);
        }
        else if (procedure instanceof FastListCollectProcedure)
        {
            InternalArrayIterate.batchFastListCollect(array, start, end, (FastListCollectProcedure<T, ?>) procedure);
        }
        else if (procedure instanceof FastListCollectIfProcedure)
        {
            InternalArrayIterate.batchFastListCollectIf(array, start, end, (FastListCollectIfProcedure<T, ?>) procedure);
        }
        else if (procedure instanceof CountProcedure)
        {
            InternalArrayIterate.batchCount(array, start, end, (CountProcedure<T>) procedure);
        }
        else if (procedure instanceof FastListRejectProcedure)
        {
            InternalArrayIterate.batchReject(array, start, end, (FastListRejectProcedure<T>) procedure);
        }
        else if (procedure instanceof MultimapPutProcedure)
        {
            InternalArrayIterate.batchGroupBy(array, start, end, (MultimapPutProcedure<?, T>) procedure);
        }
        else
        {
            for (int i = start; i < end; i++)
            {
                procedure.value(array[i]);
            }
        }
    }

    /**
     * Implemented to avoid megamorphic call on castProcedure.
     */
    private static <T> void batchGroupBy(T[] array, int start, int end, MultimapPutProcedure<?, T> castProcedure)
    {
        for (int i = start; i < end; i++)
        {
            castProcedure.value(array[i]);
        }
    }

    /**
     * Implemented to avoid megamorphic call on castProcedure.
     */
    private static <T> void batchReject(T[] array, int start, int end, FastListRejectProcedure<T> castProcedure)
    {
        for (int i = start; i < end; i++)
        {
            castProcedure.value(array[i]);
        }
    }

    /**
     * Implemented to avoid megamorphic call on castProcedure.
     */
    private static <T> void batchCount(T[] array, int start, int end, CountProcedure<T> castProcedure)
    {
        for (int i = start; i < end; i++)
        {
            castProcedure.value(array[i]);
        }
    }

    /**
     * Implemented to avoid megamorphic call on castProcedure.
     */
    private static <T> void batchFastListCollectIf(T[] array, int start, int end, FastListCollectIfProcedure<T, ?> castProcedure)
    {
        for (int i = start; i < end; i++)
        {
            castProcedure.value(array[i]);
        }
    }

    /**
     * Implemented to avoid megamorphic call on castProcedure.
     */
    private static <T> void batchFastListCollect(T[] array, int start, int end, FastListCollectProcedure<T, ?> castProcedure)
    {
        for (int i = start; i < end; i++)
        {
            castProcedure.value(array[i]);
        }
    }

    /**
     * Implemented to avoid megamorphic call on castProcedure.
     */
    private static <T> void batchFastListSelect(T[] array, int start, int end, FastListSelectProcedure<T> castProcedure)
    {
        for (int i = start; i < end; i++)
        {
            castProcedure.value(array[i]);
        }
    }

    public static <T, V, R extends MutableMultimap<V, T>> R groupBy(T[] array, int size, Function<? super T, ? extends V> function, R target)
    {
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            target.put(function.valueOf(item), item);
        }
        return target;
    }

    public static <T, V, R extends MutableMultimap<V, T>> R groupByEach(
            T[] array,
            int size,
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            Iterable<V> iterable = function.valueOf(item);
            for (V key : iterable)
            {
                target.put(key, item);
            }
        }
        return target;
    }

    public static <T, K, R extends MutableMapIterable<K, T>> R groupByUniqueKey(
            T[] array,
            int size,
            Function<? super T, ? extends K> function,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            T value = array[i];
            K key = function.valueOf(value);
            if (target.put(key, value) != null)
            {
                throw new IllegalStateException("Key " + key + " already exists in map!");
            }
        }
        return target;
    }

    public static <T> PartitionFastList<T> partition(T[] array, int size, Predicate<? super T> predicate)
    {
        PartitionFastList<T> partitionFastList = new PartitionFastList<>();

        for (int i = 0; i < size; i++)
        {
            T each = array[i];
            MutableList<T> bucket = predicate.accept(each)
                    ? partitionFastList.getSelected()
                    : partitionFastList.getRejected();
            bucket.add(each);
        }
        return partitionFastList;
    }

    public static <T, P> PartitionFastList<T> partitionWith(T[] array, int size, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionFastList<T> partitionFastList = new PartitionFastList<>();

        for (int i = 0; i < size; i++)
        {
            T each = array[i];
            MutableList<T> bucket = predicate.accept(each, parameter)
                    ? partitionFastList.getSelected()
                    : partitionFastList.getRejected();
            bucket.add(each);
        }
        return partitionFastList;
    }

    /**
     * @see Iterate#selectAndRejectWith(Iterable, Predicate2, Object)
     * @deprecated since 6.0 use {@link RichIterable#partitionWith(Predicate2, Object)} instead.
     */
    @Deprecated
    public static <T, P> Twin<MutableList<T>> selectAndRejectWith(T[] objectArray, int size, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        MutableList<T> positiveResult = Lists.mutable.empty();
        MutableList<T> negativeResult = Lists.mutable.empty();
        for (int i = 0; i < size; i++)
        {
            T each = objectArray[i];
            (predicate.accept(each, parameter) ? positiveResult : negativeResult).add(each);
        }
        return Tuples.twin(positiveResult, negativeResult);
    }

    public static int indexOf(Object[] array, int size, Object object)
    {
        for (int i = 0; i < size; i++)
        {
            if (Objects.equals(array[i], object))
            {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(Object[] array, int size, Object object)
    {
        for (int i = size - 1; i >= 0; i--)
        {
            if (Objects.equals(array[i], object))
            {
                return i;
            }
        }
        return -1;
    }

    public static <T, R extends Collection<T>> R select(T[] array, int size, Predicate<? super T> predicate, R target)
    {
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            if (predicate.accept(item))
            {
                target.add(item);
            }
        }
        return target;
    }

    public static <T, P, R extends Collection<T>> R selectWith(
            T[] array,
            int size,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            if (predicate.accept(item, parameter))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    public static <T, R extends Collection<T>> R reject(T[] array, int size, Predicate<? super T> predicate, R target)
    {
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            if (!predicate.accept(item))
            {
                target.add(item);
            }
        }
        return target;
    }

    public static <T, P, R extends Collection<T>> R rejectWith(
            T[] array,
            int size,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            if (!predicate.accept(item, parameter))
            {
                target.add(item);
            }
        }
        return target;
    }

    public static <T> FastList<T> selectInstancesOf(Object[] array, int size, Class<T> clazz)
    {
        FastList<T> results = FastList.newList(size);
        for (int i = 0; i < size; i++)
        {
            Object each = array[i];
            if (clazz.isInstance(each))
            {
                results.add((T) each);
            }
        }
        return results;
    }

    public static <T, V, R extends Collection<V>> R collect(
            T[] array,
            int size,
            Function<? super T, ? extends V> function,
            R target)
    {
        InternalArrayIterate.ensureCapacityForAdditionalSize(size, target);
        for (int i = 0; i < size; i++)
        {
            target.add(function.valueOf(array[i]));
        }
        return target;
    }

    /**
     * @since 9.1.
     */
    public static <T, V, R extends Collection<V>> R collectWithIndex(
            T[] array,
            int size,
            ObjectIntToObjectFunction<? super T, ? extends V> function,
            R target)
    {
        InternalArrayIterate.ensureCapacityForAdditionalSize(size, target);
        int index = 0;
        for (int i = 0; i < size; i++)
        {
            target.add(function.valueOf(array[i], index++));
        }
        return target;
    }

    private static void ensureCapacityForAdditionalSize(int size, Collection<?> target)
    {
        if (target instanceof FastList<?> fastList)
        {
            fastList.ensureCapacity(target.size() + size);
        }
        else if (target instanceof ArrayList<?> arrayList)
        {
            arrayList.ensureCapacity(target.size() + size);
        }
    }

    public static <T, V, R extends Collection<V>> R flatCollect(
            T[] array,
            int size,
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            Iterate.addAllTo(function.valueOf(array[i]), target);
        }
        return target;
    }

    public static <T, P, V, R extends Collection<V>> R collectWith(
            T[] array,
            int size,
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R target)
    {
        InternalArrayIterate.ensureCapacityForAdditionalSize(size, target);
        for (int i = 0; i < size; i++)
        {
            target.add(function.value(array[i], parameter));
        }
        return target;
    }

    public static <T, V, R extends Collection<V>> R collectIf(
            T[] array,
            int size,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            if (predicate.accept(item))
            {
                target.add(function.valueOf(item));
            }
        }
        return target;
    }

    public static <T> T min(T[] array, int size, Comparator<? super T> comparator)
    {
        if (size == 0)
        {
            throw new NoSuchElementException();
        }

        T min = array[0];
        for (int i = 1; i < size; i++)
        {
            T item = array[i];
            if (comparator.compare(item, min) < 0)
            {
                min = item;
            }
        }
        return min;
    }

    public static <T> T max(T[] array, int size, Comparator<? super T> comparator)
    {
        if (size == 0)
        {
            throw new NoSuchElementException();
        }

        T max = array[0];
        for (int i = 1; i < size; i++)
        {
            T item = array[i];
            if (comparator.compare(item, max) > 0)
            {
                max = item;
            }
        }
        return max;
    }

    public static <T> T min(T[] array, int size)
    {
        if (size == 0)
        {
            throw new NoSuchElementException();
        }

        T min = array[0];
        for (int i = 1; i < size; i++)
        {
            T item = array[i];
            if (((Comparable<? super T>) item).compareTo(min) < 0)
            {
                min = item;
            }
        }
        return min;
    }

    public static <T> T max(T[] array, int size)
    {
        if (size == 0)
        {
            throw new NoSuchElementException();
        }

        T max = array[0];
        for (int i = 1; i < size; i++)
        {
            T item = array[i];
            if (((Comparable<T>) item).compareTo(max) > 0)
            {
                max = item;
            }
        }
        return max;
    }

    public static <T, V extends Comparable<? super V>> T minBy(T[] array, int size, Function<? super T, ? extends V> function)
    {
        if (ArrayIterate.isEmpty(array))
        {
            throw new NoSuchElementException();
        }

        T min = array[0];
        V minValue = function.valueOf(min);
        for (int i = 1; i < size; i++)
        {
            T next = array[i];
            V nextValue = function.valueOf(next);
            if (nextValue.compareTo(minValue) < 0)
            {
                min = next;
                minValue = nextValue;
            }
        }
        return min;
    }

    public static <T, V extends Comparable<? super V>> T maxBy(T[] array, int size, Function<? super T, ? extends V> function)
    {
        if (ArrayIterate.isEmpty(array))
        {
            throw new NoSuchElementException();
        }

        T max = array[0];
        V maxValue = function.valueOf(max);
        for (int i = 1; i < size; i++)
        {
            T next = array[i];
            V nextValue = function.valueOf(next);
            if (nextValue.compareTo(maxValue) > 0)
            {
                max = next;
                maxValue = nextValue;
            }
        }
        return max;
    }

    public static <T> int count(T[] array, int size, Predicate<? super T> predicate)
    {
        int count = 0;
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(array[i]))
            {
                count++;
            }
        }
        return count;
    }

    public static <T, P> int countWith(T[] array, int size, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        int count = 0;
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(array[i], parameter))
            {
                count++;
            }
        }
        return count;
    }

    public static <T> boolean shortCircuit(
            T[] array,
            int size,
            Predicate<? super T> predicate,
            boolean expected,
            boolean onShortCircuit,
            boolean atEnd)
    {
        for (int i = 0; i < size; i++)
        {
            T each = array[i];
            if (predicate.accept(each) == expected)
            {
                return onShortCircuit;
            }
        }
        return atEnd;
    }

    public static <T, P> boolean shortCircuitWith(
            T[] array,
            int size,
            Predicate2<? super T, ? super P> predicate2,
            P parameter,
            boolean expected,
            boolean onShortCircuit,
            boolean atEnd)
    {
        for (int i = 0; i < size; i++)
        {
            T each = array[i];
            if (predicate2.accept(each, parameter) == expected)
            {
                return onShortCircuit;
            }
        }
        return atEnd;
    }

    public static <T, P> boolean corresponds(T[] array, int size, OrderedIterable<P> other, Predicate2<? super T, ? super P> predicate)
    {
        if (size != other.size())
        {
            return false;
        }

        if (other instanceof RandomAccess)
        {
            List<P> otherList = (List<P>) other;
            for (int index = 0; index < size; index++)
            {
                if (!predicate.accept(array[index], otherList.get(index)))
                {
                    return false;
                }
            }
            return true;
        }

        Iterator<P> otherIterator = other.iterator();
        for (int index = 0; index < size; index++)
        {
            if (!predicate.accept(array[index], otherIterator.next()))
            {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean anySatisfy(T[] array, int size, Predicate<? super T> predicate)
    {
        return InternalArrayIterate.shortCircuit(array, size, predicate, true, true, false);
    }

    public static <T, P> boolean anySatisfyWith(T[] array, int size, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.shortCircuitWith(array, size, predicate, parameter, true, true, false);
    }

    public static <T> boolean allSatisfy(T[] array, int size, Predicate<? super T> predicate)
    {
        return InternalArrayIterate.shortCircuit(array, size, predicate, false, false, true);
    }

    public static <T, P> boolean allSatisfyWith(T[] array, int size, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.shortCircuitWith(array, size, predicate, parameter, false, false, true);
    }

    public static <T> boolean noneSatisfy(T[] array, int size, Predicate<? super T> predicate)
    {
        return InternalArrayIterate.shortCircuit(array, size, predicate, true, false, true);
    }

    public static <T, P> boolean noneSatisfyWith(T[] array, int size, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.shortCircuitWith(array, size, predicate, parameter, true, false, true);
    }

    public static <T> T detect(T[] array, int size, Predicate<? super T> predicate)
    {
        for (int i = 0; i < size; i++)
        {
            T each = array[i];
            if (predicate.accept(each))
            {
                return each;
            }
        }
        return null;
    }

    public static <T, P> T detectWith(T[] array, int size, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        for (int i = 0; i < size; i++)
        {
            T each = array[i];
            if (predicate.accept(each, parameter))
            {
                return each;
            }
        }
        return null;
    }

    public static <T> Optional<T> detectOptional(T[] array, int size, Predicate<? super T> predicate)
    {
        for (int i = 0; i < size; i++)
        {
            T each = array[i];
            if (predicate.accept(each))
            {
                return Optional.of(each);
            }
        }
        return Optional.empty();
    }

    public static <T, P> Optional<T> detectWithOptional(T[] array, int size, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        for (int i = 0; i < size; i++)
        {
            T each = array[i];
            if (predicate.accept(each, parameter))
            {
                return Optional.of(each);
            }
        }
        return Optional.empty();
    }

    public static <T> void appendString(
            ListIterable<T> iterable,
            T[] array,
            int size,
            Appendable appendable,
            String start,
            String separator,
            String end)
    {
        try
        {
            appendable.append(start);

            if (size > 0)
            {
                appendable.append(IterableIterate.stringValueOfItem(iterable, array[0]));

                for (int i = 1; i < size; i++)
                {
                    appendable.append(separator);
                    appendable.append(IterableIterate.stringValueOfItem(iterable, array[i]));
                }
            }

            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <T> int detectIndex(T[] objectArray, int size, Predicate<? super T> predicate)
    {
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(objectArray[i]))
            {
                return i;
            }
        }
        return -1;
    }

    public static <T> int detectLastIndex(T[] objectArray, int size, Predicate<? super T> predicate)
    {
        for (int i = size - 1; i >= 0; i--)
        {
            if (predicate.accept(objectArray[i]))
            {
                return i;
            }
        }
        return -1;
    }

    public static <T> void forEachWithIndex(T[] objectArray, int size, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        for (int i = 0; i < size; i++)
        {
            objectIntProcedure.value(objectArray[i], i);
        }
    }

    /**
     * @deprecated in 7.0.
     */
    @Deprecated
    public static <T, R extends List<T>> R distinct(T[] objectArray, int size, R targetList)
    {
        MutableSet<T> seenSoFar = Sets.mutable.empty();
        return InternalArrayIterate.select(objectArray, size, seenSoFar::add, targetList);
    }

    /**
     * @since 7.0.
     */
    public static <T> FastList<T> distinct(T[] objectArray, int size)
    {
        return InternalArrayIterate.distinct(objectArray, size, FastList.newList());
    }

    /**
     * @since 7.0.
     */
    public static <T> FastList<T> distinct(T[] objectArray, int size, HashingStrategy<? super T> hashingStrategy)
    {
        MutableSet<T> seenSoFar = UnifiedSetWithHashingStrategy.newSet(hashingStrategy);
        return InternalArrayIterate.select(objectArray, size, seenSoFar::add, FastList.newList());
    }

    public static <T> long sumOfInt(T[] array, int size, IntFunction<? super T> function)
    {
        long result = 0L;
        for (int i = 0; i < size; i++)
        {
            result += (long) function.intValueOf(array[i]);
        }
        return result;
    }

    public static <T> long sumOfLong(T[] array, int size, LongFunction<? super T> function)
    {
        long result = 0L;
        for (int i = 0; i < size; i++)
        {
            result += function.longValueOf(array[i]);
        }
        return result;
    }

    public static <T> double sumOfFloat(T[] array, int size, FloatFunction<? super T> function)
    {
        double sum = 0.0d;
        double compensation = 0.0d;
        for (int i = 0; i < size; i++)
        {
            double adjustedValue = (double) function.floatValueOf(array[i]) - compensation;
            double nextSum = sum + adjustedValue;
            compensation = nextSum - sum - adjustedValue;
            sum = nextSum;
        }
        return sum;
    }

    public static <T> double sumOfDouble(T[] array, int size, DoubleFunction<? super T> function)
    {
        double sum = 0.0d;
        double compensation = 0.0d;
        for (int i = 0; i < size; i++)
        {
            double adjustedValue = function.doubleValueOf(array[i]) - compensation;
            double nextSum = sum + adjustedValue;
            compensation = nextSum - sum - adjustedValue;
            sum = nextSum;
        }
        return sum;
    }

    public static <V, T> MutableObjectLongMap<V> sumByInt(T[] array, int size, Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            result.addToValue(groupBy.valueOf(item), function.intValueOf(item));
        }
        return result;
    }

    public static <V, T> MutableObjectLongMap<V> sumByLong(T[] array, int size, Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            result.addToValue(groupBy.valueOf(item), function.longValueOf(item));
        }
        return result;
    }

    public static <V, T> MutableObjectDoubleMap<V> sumByFloat(T[] array, int size, Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        MutableObjectDoubleMap<V> groupKeyToCompensation = ObjectDoubleMaps.mutable.empty();
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            V groupByKey = groupBy.valueOf(item);
            double compensation = groupKeyToCompensation.getIfAbsentPut(groupByKey, 0.0d);
            double adjustedValue = function.floatValueOf(item) - compensation;
            double nextSum = result.get(groupByKey) + adjustedValue;
            groupKeyToCompensation.put(groupByKey, nextSum - result.get(groupByKey) - adjustedValue);
            result.put(groupByKey, nextSum);
        }
        return result;
    }

    public static <V, T> MutableObjectDoubleMap<V> sumByDouble(T[] array, int size, Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        MutableObjectDoubleMap<V> groupKeyToCompensation = ObjectDoubleMaps.mutable.empty();
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            V groupByKey = groupBy.valueOf(item);
            double compensation = groupKeyToCompensation.getIfAbsentPut(groupByKey, 0.0d);
            double adjustedValue = function.doubleValueOf(item) - compensation;
            double nextSum = result.get(groupByKey) + adjustedValue;
            groupKeyToCompensation.put(groupByKey, nextSum - result.get(groupByKey) - adjustedValue);
            result.put(groupByKey, nextSum);
        }
        return result;
    }

    /**
     * @since 8.0
     */
    public static <T> IntSummaryStatistics summarizeInt(T[] items, int size, IntFunction<? super T> function)
    {
        IntSummaryStatistics stats = new IntSummaryStatistics();
        for (int i = 0; i < size; i++)
        {
            T item = items[i];
            stats.accept(function.intValueOf(item));
        }
        return stats;
    }

    /**
     * @since 8.0
     */
    public static <T> DoubleSummaryStatistics summarizeFloat(T[] items, int size, FloatFunction<? super T> function)
    {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        for (int i = 0; i < size; i++)
        {
            T item = items[i];
            stats.accept((double) function.floatValueOf(item));
        }
        return stats;
    }

    /**
     * @since 8.0
     */
    public static <T> LongSummaryStatistics summarizeLong(T[] items, int size, LongFunction<? super T> function)
    {
        LongSummaryStatistics stats = new LongSummaryStatistics();
        for (int i = 0; i < size; i++)
        {
            T item = items[i];
            stats.accept(function.longValueOf(item));
        }
        return stats;
    }

    /**
     * @since 8.0
     */
    public static <T> DoubleSummaryStatistics summarizeDouble(T[] items, int size, DoubleFunction<? super T> function)
    {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        for (int i = 0; i < size; i++)
        {
            T item = items[i];
            stats.accept(function.doubleValueOf(item));
        }
        return stats;
    }

    /**
     * @since 8.0
     */
    public static <T> Optional<T> reduce(T[] items, int size, BinaryOperator<T> accumulator)
    {
        if (size == 0)
        {
            return Optional.empty();
        }
        T result = items[0];
        for (int i = 1; i < size; i++)
        {
            result = accumulator.apply(result, items[i]);
        }
        return Optional.of(result);
    }

    /**
     * @since 8.0
     */
    public static <R, A, T> R reduceInPlace(T[] items, int size, Collector<? super T, A, R> collector)
    {
        A mutableResult = collector.supplier().get();
        BiConsumer<A, ? super T> accumulator = collector.accumulator();
        for (int i = 0; i < size; i++)
        {
            T item = items[i];
            accumulator.accept(mutableResult, item);
        }
        return collector.finisher().apply(mutableResult);
    }

    /**
     * @since 8.0
     */
    public static <R, T> R reduceInPlace(T[] items, int size, Supplier<R> supplier, BiConsumer<R, ? super T> accumulator)
    {
        R mutableResult = supplier.get();
        for (int i = 0; i < size; i++)
        {
            T item = items[i];
            accumulator.accept(mutableResult, item);
        }
        return mutableResult;
    }

    /**
     * @since 10.0 - Provided for convenience for array based containers
     */
    public static <T> void replaceAll(T[] items, int size, UnaryOperator<T> operator)
    {
        for (int i = 0; i < size; i++)
        {
            T item = items[i];
            items[i] = operator.apply(item);
        }
    }

    /**
     * Adds all array elements to the target Collection that return true when evaluating the specified predicate which is
     * supplied each element and its relative index.
     *
     * @since 11.0
     */
    public static <R extends Collection<T>, T> R selectWithIndex(
            T[] array,
            int size,
            ObjectIntPredicate<? super T> predicate,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            if (predicate.accept(item, i))
            {
                target.add(item);
            }
        }
        return target;
    }

    /**
     * Adds all array elements to the target Collection that return false when evaluating the specified predicate which is
     * supplied each element and its relative index.
     *
     * @since 11.0
     */
    public static <R extends Collection<T>, T> R rejectWithIndex(
            T[] array,
            int size,
            ObjectIntPredicate<? super T> predicate,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            T item = array[i];
            if (!predicate.accept(item, i))
            {
                target.add(item);
            }
        }
        return target;
    }

    /**
     * Adds all array elements to the target MutableBooleanCollection after using the function supplied to convert
     * each source element to the appropriate type
     *
     * @since 12.0
     */
    public static <R extends MutableBooleanCollection, T> R collectBoolean(
            T[] items,
            int size,
            BooleanFunction<? super T> booleanFunction,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            target.add(booleanFunction.booleanValueOf(items[i]));
        }
        return target;
    }

    /**
     * Adds all array elements to the target MutableByteCollection after using the function supplied to convert
     * each source element to the appropriate type
     *
     * @since 12.0
     */
    public static <R extends MutableByteCollection, T> R collectByte(
            T[] items,
            int size,
            ByteFunction<? super T> byteFunction,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            target.add(byteFunction.byteValueOf(items[i]));
        }
        return target;
    }

    /**
     * Adds all array elements to the target MutableCharCollection after using the function supplied to convert
     * each source element to the appropriate type
     *
     * @since 12.0
     */
    public static <R extends MutableCharCollection, T> R collectChar(
            T[] items,
            int size,
            CharFunction<? super T> charFunction,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            target.add(charFunction.charValueOf(items[i]));
        }
        return target;
    }

    /**
     * Adds all array elements to the target MutableDoubleCollection after using the function supplied to convert
     * each source element to the appropriate type
     *
     * @since 12.0
     */
    public static <R extends MutableDoubleCollection, T> R collectDouble(
            T[] items,
            int size,
            DoubleFunction<? super T> doubleFunction,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            target.add(doubleFunction.doubleValueOf(items[i]));
        }
        return target;
    }

    /**
     * Adds all array elements to the target MutableFloatCollection after using the function supplied to convert
     * each source element to the appropriate type
     *
     * @since 12.0
     */
    public static <R extends MutableFloatCollection, T> R collectFloat(
            T[] items,
            int size,
            FloatFunction<? super T> floatFunction,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            target.add(floatFunction.floatValueOf(items[i]));
        }
        return target;
    }

    /**
     * Adds all array elements to the target MutableIntCollection after using the function supplied to convert
     * each source element to the appropriate type
     *
     * @since 12.0
     */
    public static <R extends MutableIntCollection, T> R collectInt(
            T[] items,
            int size,
            IntFunction<? super T> intFunction,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            target.add(intFunction.intValueOf(items[i]));
        }
        return target;
    }

    /**
     * Adds all array elements to the target MutableLongCollection after using the function supplied to convert
     * each source element to the appropriate type
     *
     * @since 12.0
     */
    public static <R extends MutableLongCollection, T> R collectLong(
            T[] items,
            int size,
            LongFunction<? super T> longFunction,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            target.add(longFunction.longValueOf(items[i]));
        }
        return target;
    }

    /**
     * Adds all array elements to the target MutableShortCollection after using the function supplied to convert
     * each source element to the appropriate type
     *
     * @since 12.0
     */
    public static <R extends MutableShortCollection, T> R collectShort(
            T[] items,
            int size,
            ShortFunction<? super T> shortFunction,
            R target)
    {
        for (int i = 0; i < size; i++)
        {
            target.add(shortFunction.shortValueOf(items[i]));
        }
        return target;
    }
}
