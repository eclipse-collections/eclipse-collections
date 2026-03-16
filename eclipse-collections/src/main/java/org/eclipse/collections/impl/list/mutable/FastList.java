/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.ShortIterable;
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
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.ArrayListIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.eclipse.collections.impl.utility.internal.InternalArrayIterate;
import org.eclipse.collections.impl.utility.internal.RandomAccessListIterate;

/**
 * FastList is an attempt to provide the same functionality as ArrayList without the support for concurrent
 * modification exceptions. It also attempts to correct the problem with subclassing ArrayList
 * in that the data elements are protected, not private. It is this issue that caused this class
 * to be created in the first place. The intent was to provide optimized internal iterators which use direct access
 * against the array of items, which is currently not possible by subclassing ArrayList.
 * <p>
 * An empty FastList created by calling the default constructor starts with a shared reference to a static
 * empty array (DEFAULT_SIZED_EMPTY_ARRAY). This makes empty FastLists very memory efficient. The
 * first call to add will lazily create an array of size 10.
 * <p>
 * An empty FastList created by calling the pre-size constructor with a value of 0 (new FastList(0)) starts
 * with a shared reference to a static  empty array (ZERO_SIZED_ARRAY). This makes FastLists presized to 0 very
 * memory efficient as well. The first call to add will lazily create an array of size 1.
 */
public class FastList<T>
        extends AbstractMutableList<T>
        implements Externalizable, RandomAccess, BatchIterable<T>
{
    private static final long serialVersionUID = 1L;

    @Override
    public boolean removeIf(java.util.function.Predicate<? super T> filter)
    {
        int currentIndex = 0;
        for(int i = 0 ; i < this.size ; i++)
        {
            T val = this.items[i];
            if(!filter.test(val))
            {
                this.items[currentIndex] = val;
                currentIndex++;
            }
        }
        boolean changed = currentIndex != this.size;
        this.wipeAndResetTheEnd(currentIndex);
        return changed;
    }

    private static final Object[] DEFAULT_SIZED_EMPTY_ARRAY = {};
    private static final Object[] ZERO_SIZED_ARRAY = {};
    private static final int MAXIMUM_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    protected int size;
    protected transient T[] items = (T[]) DEFAULT_SIZED_EMPTY_ARRAY;

    public FastList()
    {
    }

    public FastList(int initialCapacity)
    {
        if (initialCapacity < 0)
        {
            throw new IllegalArgumentException("Expected initial capacity is greater than or equal to 0. Was: " + initialCapacity);
        }
        this.items = initialCapacity == 0 ? (T[]) ZERO_SIZED_ARRAY : (T[]) new Object[initialCapacity];
    }

    protected FastList(T[] array)
    {
        this(array.length, array);
    }

    protected FastList(int size, T[] array)
    {
        this.size = size;
        this.items = array;
    }

    public FastList(Collection<? extends T> source)
    {
        this.items = (T[]) source.toArray();
        this.size = this.items.length;
    }

    public static <E> FastList<E> newList()
    {
        return new FastList<>();
    }

    public static <E> FastList<E> wrapCopy(E... array)
    {
        E[] newArray = (E[]) new Object[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return new FastList<>(newArray);
    }

    public static <E> FastList<E> newList(int initialCapacity)
    {
        return new FastList<>(initialCapacity);
    }

    public static <E> FastList<E> newList(Iterable<? extends E> source)
    {
        return FastList.newListWith((E[]) Iterate.toArray(source));
    }

    /**
     * Creates a new list using the passed {@code elements} argument as the backing store.
     * <p>
     * !!! WARNING: This method uses the passed in array, so can be very unsafe if the original
     * array is held onto anywhere else. !!!
     */
    public static <E> FastList<E> newListWith(E... elements)
    {
        return new FastList<>(elements);
    }

    /**
     * Creates a new FastList pre-sized to the specified size filled with default values generated by the specified function.
     *
     * @since 3.0
     */
    public static <E> FastList<E> newWithNValues(int size, Function0<? extends E> factory)
    {
        FastList<E> newFastList = FastList.newList(size);
        for (int i = 0; i < size; i++)
        {
            newFastList.add(factory.value());
        }
        return newFastList;
    }

    @Override
    public FastList<T> clone()
    {
        FastList<T> result = (FastList<T>) super.clone();
        if (this.items.length > 0)
        {
            result.items = this.items.clone();
        }
        return result;
    }

    /**
     * Removes from this list all the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     * @param fromIndex inclusive
     * @param toIndex exclusive
     */
    @Override
    public void removeRange(int fromIndex, int toIndex)
    {
        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ')');
        }
        System.arraycopy(this.items, toIndex, this.items, fromIndex, this.size - toIndex);
        int newSize = this.size - (toIndex - fromIndex);
        this.wipeAndResetTheEnd(newSize);
    }

    @Override
    public void clear()
    {
        Arrays.fill(this.items, 0, this.size, null);
        this.size = 0;
    }

    @Override
    public void forEach(int from, int to, Procedure<? super T> procedure)
    {
        ListIterate.rangeCheck(from, to, this.size);
        InternalArrayIterate.forEachWithoutChecks(this.items, from, to, procedure);
    }

    @Override
    public void forEachWithIndex(int from, int to, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(from, to, this.size);
        InternalArrayIterate.forEachWithIndexWithoutChecks(this.items, from, to, objectIntProcedure);
    }

    @Override
    public void batchForEach(Procedure<? super T> procedure, int sectionIndex, int sectionCount)
    {
        InternalArrayIterate.batchForEach(procedure, this.items, this.size, sectionIndex, sectionCount);
    }

    @Override
    public int getBatchCount(int batchSize)
    {
        return Math.max(1, this.size() / batchSize);
    }

    public <E> E[] toArray(E[] array, int sourceFromIndex, int sourceToIndex, int destinationIndex)
    {
        System.arraycopy(this.items, sourceFromIndex, array, destinationIndex, sourceToIndex - sourceFromIndex + 1);
        return array;
    }

    public <E> E[] toArray(int sourceFromIndex, int sourceToIndex)
    {
        return this.toArray((E[]) new Object[sourceToIndex - sourceFromIndex + 1], sourceFromIndex, sourceToIndex, 0);
    }

    /**
     * Overrides default method from List.
     *
     * @since 10.0 - Overridden for efficiency
     */
    @Override
    public void sort(Comparator<? super T> comparator)
    {
        Arrays.sort(this.items, 0, this.size, comparator);
    }

    @Override
    public FastList<T> sortThis(Comparator<? super T> comparator)
    {
        return (FastList<T>) super.sortThis(comparator);
    }

    @Override
    public FastList<T> sortThis()
    {
        return (FastList<T>) super.sortThis();
    }

    @Override
    public FastList<T> reverseThis()
    {
        ArrayIterate.reverse(this.items, this.size);
        return this;
    }

    @Override
    public boolean addAll(Collection<? extends T> source)
    {
        if (source.isEmpty())
        {
            return false;
        }

        if (source.getClass() == FastList.class)
        {
            this.addAllFastList((FastList<T>) source);
        }
        else if (source.getClass() == ArrayList.class)
        {
            this.addAllArrayList((ArrayList<T>) source);
        }
        else if (source instanceof List && source instanceof RandomAccess)
        {
            this.addAllRandomAccessList((List<T>) source);
        }
        else
        {
            this.addAllCollection(source);
        }

        return true;
    }

    private void addAllFastList(FastList<T> source)
    {
        int newSize = this.ensureCapacityForAddAll(source);
        System.arraycopy(source.items, 0, this.items, this.size, source.size());
        this.size = newSize;
    }

    private void addAllArrayList(ArrayList<T> source)
    {
        int newSize = this.ensureCapacityForAddAll(source);
        ArrayListIterate.toArray(source, this.items, this.size, source.size());
        this.size = newSize;
    }

    private void addAllRandomAccessList(List<T> source)
    {
        int newSize = this.ensureCapacityForAddAll(source);
        RandomAccessListIterate.toArray(source, this.items, this.size, source.size());
        this.size = newSize;
    }

    private void addAllCollection(Collection<? extends T> source)
    {
        this.ensureCapacity(this.size + source.size());
        Iterate.forEachWith(source, Procedures2.addToCollection(), this);
    }

    @Override
    public <E> E[] toArray(E[] array)
    {
        if (array.length < this.size)
        {
            array = (E[]) Array.newInstance(array.getClass().getComponentType(), this.size);
        }
        System.arraycopy(this.items, 0, array, 0, this.size);
        if (array.length > this.size)
        {
            array[this.size] = null;
        }
        return array;
    }

    @Override
    public Object[] toArray()
    {
        return this.copyItemsWithNewCapacity(this.size);
    }

    public T[] toTypedArray(Class<T> clazz)
    {
        T[] array = (T[]) Array.newInstance(clazz, this.size);
        System.arraycopy(this.items, 0, array, 0, this.size);
        return array;
    }

    private void throwOutOfBounds(int index)
    {
        throw this.newIndexOutOfBoundsException(index);
    }

    @Override
    public T set(int index, T element)
    {
        T previous = this.get(index);
        this.items[index] = element;
        return previous;
    }

    @Override
    public int indexOf(Object object)
    {
        return InternalArrayIterate.indexOf(this.items, this.size, object);
    }

    @Override
    public int lastIndexOf(Object object)
    {
        return InternalArrayIterate.lastIndexOf(this.items, this.size, object);
    }

    /**
     * @since 8.1
     */
    @Override
    public Spliterator<T> spliterator()
    {
        return Spliterators.spliterator(this.items, 0, this.size, Spliterator.ORDERED);
    }

    public void trimToSize()
    {
        if (this.size < this.items.length)
        {
            this.transferItemsToNewArrayWithCapacity(this.size);
        }
    }

    /**
     * Express load factor as 0.25 to trim a collection with more than 25% excess capacity
     */
    public boolean trimToSizeIfGreaterThanPercent(double loadFactor)
    {
        double excessCapacity = 1.0 - (double) this.size / (double) this.items.length;
        if (excessCapacity > loadFactor)
        {
            this.trimToSize();
            return true;
        }
        return false;
    }

    private void ensureCapacityForAdd()
    {
        if (this.items == DEFAULT_SIZED_EMPTY_ARRAY)
        {
            this.items = (T[]) new Object[10];
        }
        else
        {
            this.transferItemsToNewArrayWithCapacity(this.sizePlusFiftyPercent(this.size));
        }
    }

    private int ensureCapacityForAddAll(Collection<T> source)
    {
        int sourceSize = source.size();
        int newSize = this.size + sourceSize;
        this.ensureCapacity(newSize);
        return newSize;
    }

    public void ensureCapacity(int minCapacity)
    {
        int oldCapacity = this.items.length;
        if (minCapacity > oldCapacity)
        {
            int newCapacity = Math.max(this.sizePlusFiftyPercent(oldCapacity), minCapacity);
            this.transferItemsToNewArrayWithCapacity(newCapacity);
        }
    }

    private void transferItemsToNewArrayWithCapacity(int newCapacity)
    {
        this.items = (T[]) this.copyItemsWithNewCapacity(newCapacity);
    }

    private Object[] copyItemsWithNewCapacity(int newCapacity)
    {
        Object[] newItems = new Object[newCapacity];
        System.arraycopy(this.items, 0, newItems, 0, Math.min(this.size, newCapacity));
        return newItems;
    }

    public FastList<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public FastList<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public FastList<T> with(T... elements)
    {
        return this.withArrayCopy(elements, 0, elements.length);
    }

    public FastList<T> withArrayCopy(T[] elements, int begin, int length)
    {
        this.ensureCapacity(this.size + length);
        System.arraycopy(elements, begin, this.items, this.size, length);
        this.size += length;
        return this;
    }

    @Override
    public T getFirst()
    {
        return this.isEmpty() ? null : this.items[0];
    }

    @Override
    public T getLast()
    {
        return this.isEmpty() ? null : this.items[this.size() - 1];
    }

    @Override
    public <V> FastListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, FastListMultimap.newMultimap());
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target)
    {
        return InternalArrayIterate.groupBy(this.items, this.size, function, target);
    }

    @Override
    public <V> FastListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, FastListMultimap.newMultimap());
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return InternalArrayIterate.groupByEach(this.items, this.size, function, target);
    }

    @Override
    public <K> MutableMap<K, T> groupByUniqueKey(Function<? super T, ? extends K> function)
    {
        return this.groupByUniqueKey(function, UnifiedMap.newMap(this.size()));
    }

    @Override
    public <K, R extends MutableMapIterable<K, T>> R groupByUniqueKey(Function<? super T, ? extends K> function, R target)
    {
        return InternalArrayIterate.groupByUniqueKey(this.items, this.size, function, target);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        InternalArrayIterate.appendString(this, this.items, this.size, appendable, start, separator, end);
    }

    @Override
    public MutableList<T> take(int count)
    {
        return RandomAccessListIterate.take(this, count);
    }

    @Override
    public MutableList<T> drop(int count)
    {
        return RandomAccessListIterate.drop(this, count);
    }

    @Override
    public PartitionFastList<T> partition(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.partition(this.items, this.size, predicate);
    }

    @Override
    public <P> PartitionFastList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.partitionWith(this.items, this.size, predicate, parameter);
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        for (int i = 0; i < this.size; i++)
        {
            procedure.value(this.items[i]);
        }
    }

    public void forEachIf(Predicate<? super T> predicate, Procedure<? super T> procedure)
    {
        for (int i = 0; i < this.size; i++)
        {
            T item = this.items[i];
            if (predicate.accept(item))
            {
                procedure.value(item);
            }
        }
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        InternalArrayIterate.forEachWithIndex(this.items, this.size, objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        for (int i = 0; i < this.size; i++)
        {
            procedure.value(this.items[i], parameter);
        }
    }

    @Override
    public FastList<T> select(Predicate<? super T> predicate)
    {
        return this.select(predicate, FastList.newList());
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return InternalArrayIterate.select(this.items, this.size, predicate, target);
    }

    @Override
    public <P> FastList<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.selectWith(predicate, parameter, FastList.newList());
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return InternalArrayIterate.selectWith(this.items, this.size, predicate, parameter, target);
    }

    @Override
    public FastList<T> reject(Predicate<? super T> predicate)
    {
        return this.reject(predicate, FastList.newList());
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return InternalArrayIterate.reject(this.items, this.size, predicate, target);
    }

    @Override
    public <P> FastList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.rejectWith(predicate, parameter, FastList.newList());
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return InternalArrayIterate.rejectWith(this.items, this.size, predicate, parameter, target);
    }

    @Override
    public <P> Twin<MutableList<T>> selectAndRejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return InternalArrayIterate.selectAndRejectWith(this.items, this.size, predicate, parameter);
    }

    @Override
    public <S> FastList<S> selectInstancesOf(Class<S> clazz)
    {
        return InternalArrayIterate.selectInstancesOf(this.items, this.size, clazz);
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        int currentFilledIndex = 0;
        for (int i = 0; i < this.size; i++)
        {
            T item = this.items[i];
            if (!predicate.accept(item))
            {
                // keep it
                if (currentFilledIndex != i)
                {
                    this.items[currentFilledIndex] = item;
                }
                currentFilledIndex++;
            }
        }
        boolean changed = currentFilledIndex < this.size;
        this.wipeAndResetTheEnd(currentFilledIndex);
        return changed;
    }

    private void wipeAndResetTheEnd(int newCurrentFilledIndex)
    {
        for (int i = newCurrentFilledIndex; i < this.size; i++)
        {
            this.items[i] = null;
        }
        this.size = newCurrentFilledIndex;
    }

    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        int currentFilledIndex = 0;
        for (int i = 0; i < this.size; i++)
        {
            T item = this.items[i];
            if (!predicate.accept(item, parameter))
            {
                // keep it
                if (currentFilledIndex != i)
                {
                    this.items[currentFilledIndex] = item;
                }
                currentFilledIndex++;
            }
        }
        boolean changed = currentFilledIndex < this.size;
        this.wipeAndResetTheEnd(currentFilledIndex);
        return changed;
    }

    @Override
    public <V> FastList<V> collect(Function<? super T, ? extends V> function)
    {
        return this.collect(function, FastList.newList(this.size()));
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        return InternalArrayIterate.collectBoolean(this.items, this.size, booleanFunction, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        for (int i = 0; i < this.size; i++)
        {
            target.addAll(function.valueOf(this.items[i]));
        }
        return target;
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        return InternalArrayIterate.collectByte(this.items, this.size, byteFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        for (int i = 0; i < this.size; i++)
        {
            target.addAll(function.valueOf(this.items[i]));
        }
        return target;
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        return InternalArrayIterate.collectChar(this.items, this.size, charFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        for (int i = 0; i < this.size; i++)
        {
            target.addAll(function.valueOf(this.items[i]));
        }
        return target;
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        return InternalArrayIterate.collectDouble(this.items, this.size, doubleFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function, R target)
    {
        for (int i = 0; i < this.size; i++)
        {
            target.addAll(function.valueOf(this.items[i]));
        }
        return target;
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        return InternalArrayIterate.collectFloat(this.items, this.size, floatFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        for (int i = 0; i < this.size; i++)
        {
            target.addAll(function.valueOf(this.items[i]));
        }
        return target;
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        return InternalArrayIterate.collectInt(this.items, this.size, intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        for (int i = 0; i < this.size; i++)
        {
            target.addAll(function.valueOf(this.items[i]));
        }
        return target;
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        return InternalArrayIterate.collectLong(this.items, this.size, longFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        for (int i = 0; i < this.size; i++)
        {
            target.addAll(function.valueOf(this.items[i]));
        }
        return target;
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        return InternalArrayIterate.collectShort(this.items, this.size, shortFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        for (int i = 0; i < this.size; i++)
        {
            target.addAll(function.valueOf(this.items[i]));
        }
        return target;
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return InternalArrayIterate.collect(this.items, this.size, function, target);
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> MutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.collectWithIndex(function, FastList.newList(this.size));
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        return InternalArrayIterate.collectWithIndex(this.items, this.size, function, target);
    }

    /**
     * @since 11.0
     */
    @Override
    public MutableList<T> selectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        return this.selectWithIndex(predicate, FastList.newList());
    }

    /**
     * @since 11.0
     */
    @Override
    public MutableList<T> rejectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        return this.rejectWithIndex(predicate, FastList.newList());
    }

    /**
     * @since 11.0
     */
    @Override
    public <R extends Collection<T>> R selectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        return InternalArrayIterate.selectWithIndex(this.items, this.size, predicate, target);
    }

    /**
     * @since 11.0
     */
    @Override
    public <R extends Collection<T>> R rejectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        return InternalArrayIterate.rejectWithIndex(this.items, this.size, predicate, target);
    }

    @Override
    public <V> FastList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.flatCollect(function, FastList.newList(this.size()));
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return InternalArrayIterate.flatCollect(this.items, this.size, function, target);
    }

    @Override
    public <P, V> FastList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collectWith(function, parameter, FastList.newList(this.size()));
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R target)
    {
        return InternalArrayIterate.collectWith(this.items, this.size, function, parameter, target);
    }

    @Override
    public <V> FastList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return this.collectIf(predicate, function, FastList.newList());
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        return InternalArrayIterate.collectIf(this.items, this.size, predicate, function, target);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.detect(this.items, this.size, predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.detectWith(this.items, this.size, predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.detectOptional(this.items, this.size, predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.detectWithOptional(this.items, this.size, predicate, parameter);
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.detectIndex(this.items, this.size, predicate);
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.detectLastIndex(this.items, this.size, predicate);
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return InternalArrayIterate.min(this.items, this.size, comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return InternalArrayIterate.max(this.items, this.size, comparator);
    }

    @Override
    public T min()
    {
        return InternalArrayIterate.min(this.items, this.size);
    }

    @Override
    public T max()
    {
        return InternalArrayIterate.max(this.items, this.size);
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return InternalArrayIterate.minBy(this.items, this.size, function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return InternalArrayIterate.maxBy(this.items, this.size, function);
    }

    @Override
    public T get(int index)
    {
        if (index < this.size)
        {
            return this.items[index];
        }
        throw this.newIndexOutOfBoundsException(index);
    }

    private IndexOutOfBoundsException newIndexOutOfBoundsException(int index)
    {
        return new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size);
    }

    @Override
    public boolean add(T newItem)
    {
        if (this.items.length == this.size)
        {
            this.ensureCapacityForAdd();
        }
        this.items[this.size++] = newItem;
        return true;
    }

    @Override
    public void add(int index, T element)
    {
        if (index > -1 && index < this.size)
        {
            this.addAtIndex(index, element);
        }
        else if (index == this.size)
        {
            this.add(element);
        }
        else
        {
            this.throwOutOfBounds(index);
        }
    }

    private void addAtIndex(int index, T element)
    {
        int oldSize = this.size++;
        if (this.items.length == oldSize)
        {
            T[] newItems = (T[]) new Object[this.sizePlusFiftyPercent(oldSize)];
            if (index > 0)
            {
                System.arraycopy(this.items, 0, newItems, 0, index);
            }
            System.arraycopy(this.items, index, newItems, index + 1, oldSize - index);
            this.items = newItems;
        }
        else
        {
            System.arraycopy(this.items, index, this.items, index + 1, oldSize - index);
        }
        this.items[index] = element;
    }

    private int sizePlusFiftyPercent(int oldSize)
    {
        int result = oldSize + (oldSize >> 1) + 1;
        return result < oldSize ? MAXIMUM_ARRAY_SIZE : result;
    }

    @Override
    public T remove(int index)
    {
        T previous = this.get(index);
        int totalOffset = this.size - index - 1;
        if (totalOffset > 0)
        {
            System.arraycopy(this.items, index + 1, this.items, index, totalOffset);
        }
        this.items[--this.size] = null;
        return previous;
    }

    @Override
    public boolean remove(Object object)
    {
        int index = this.indexOf(object);
        if (index >= 0)
        {
            this.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> source)
    {
        if (index > this.size || index < 0)
        {
            this.throwOutOfBounds(index);
        }
        if (source.isEmpty())
        {
            return false;
        }

        if (source.getClass() == FastList.class)
        {
            this.addAllFastListAtIndex((FastList<T>) source, index);
        }
        else if (source.getClass() == ArrayList.class)
        {
            this.addAllArrayListAtIndex((ArrayList<T>) source, index);
        }
        else if (source instanceof List && source instanceof RandomAccess)
        {
            this.addAllRandomAccessListAtIndex((List<T>) source, index);
        }
        else
        {
            this.addAllCollectionAtIndex(source, index);
        }
        return true;
    }

    private void addAllFastListAtIndex(FastList<T> source, int index)
    {
        int sourceSize = source.size();
        int newSize = this.size + sourceSize;
        this.ensureCapacity(newSize);
        this.shiftElementsAtIndex(index, sourceSize);
        System.arraycopy(source.items, 0, this.items, index, sourceSize);
        this.size = newSize;
    }

    private void addAllArrayListAtIndex(ArrayList<T> source, int index)
    {
        int sourceSize = source.size();
        int newSize = this.size + sourceSize;
        this.ensureCapacity(newSize);
        this.shiftElementsAtIndex(index, sourceSize);
        ArrayListIterate.toArray(source, this.items, index, sourceSize);
        this.size = newSize;
    }

    private void addAllRandomAccessListAtIndex(List<T> source, int index)
    {
        int sourceSize = source.size();
        int newSize = this.size + sourceSize;
        this.ensureCapacity(newSize);
        this.shiftElementsAtIndex(index, sourceSize);
        RandomAccessListIterate.toArray(source, this.items, index, sourceSize);
        this.size = newSize;
    }

    private void addAllCollectionAtIndex(Collection<? extends T> source, int index)
    {
        Object[] newItems = source.toArray();
        int sourceSize = newItems.length;
        int newSize = this.size + sourceSize;
        this.ensureCapacity(newSize);
        this.shiftElementsAtIndex(index, sourceSize);
        this.size = newSize;
        System.arraycopy(newItems, 0, this.items, index, sourceSize);
    }

    private void shiftElementsAtIndex(int index, int sourceSize)
    {
        int numberToMove = this.size - index;
        if (numberToMove > 0)
        {
            System.arraycopy(this.items, index, this.items, index + sourceSize, numberToMove);
        }
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.count(this.items, this.size, predicate);
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.countWith(this.items, this.size, predicate, parameter);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return InternalArrayIterate.corresponds(this.items, this.size, other, predicate);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.anySatisfy(this.items, this.size, predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.anySatisfyWith(this.items, this.size, predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.allSatisfy(this.items, this.size, predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.allSatisfyWith(this.items, this.size, predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.noneSatisfy(this.items, this.size, predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.noneSatisfyWith(this.items, this.size, predicate, parameter);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        IV result = injectedValue;
        for (int i = 0; i < this.size; i++)
        {
            result = function.value(result, this.items[i]);
        }
        return result;
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        int result = injectedValue;
        for (int i = 0; i < this.size; i++)
        {
            result = function.intValueOf(result, this.items[i]);
        }
        return result;
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        long result = injectedValue;
        for (int i = 0; i < this.size; i++)
        {
            result = function.longValueOf(result, this.items[i]);
        }
        return result;
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        double result = injectedValue;
        for (int i = 0; i < this.size; i++)
        {
            result = function.doubleValueOf(result, this.items[i]);
        }
        return result;
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        float result = injectedValue;
        for (int i = 0; i < this.size; i++)
        {
            result = function.floatValueOf(result, this.items[i]);
        }
        return result;
    }

    @Override
    public MutableList<T> distinct()
    {
        return InternalArrayIterate.distinct(this.items, this.size);
    }

    @Override
    public MutableList<T> distinct(HashingStrategy<? super T> hashingStrategy)
    {
        return InternalArrayIterate.distinct(this.items, this.size, hashingStrategy);
    }

    /**
     * @since 8.0
     */
    @Override
    public IntSummaryStatistics summarizeInt(IntFunction<? super T> function)
    {
        return InternalArrayIterate.summarizeInt(this.items, this.size, function);
    }

    /**
     * @since 8.0
     */
    @Override
    public DoubleSummaryStatistics summarizeFloat(FloatFunction<? super T> function)
    {
        return InternalArrayIterate.summarizeFloat(this.items, this.size, function);
    }

    /**
     * @since 8.0
     */
    @Override
    public LongSummaryStatistics summarizeLong(LongFunction<? super T> function)
    {
        return InternalArrayIterate.summarizeLong(this.items, this.size, function);
    }

    /**
     * @since 8.0
     */
    @Override
    public DoubleSummaryStatistics summarizeDouble(DoubleFunction<? super T> function)
    {
        return InternalArrayIterate.summarizeDouble(this.items, this.size, function);
    }

    /**
     * @since 8.0
     */
    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator)
    {
        return InternalArrayIterate.reduce(this.items, this.size, accumulator);
    }

    /**
     * @since 8.0
     */
    @Override
    public <R, A> R reduceInPlace(Collector<? super T, A, R> collector)
    {
        return InternalArrayIterate.reduceInPlace(this.items, this.size, collector);
    }

    /**
     * @since 8.0
     */
    @Override
    public <R> R reduceInPlace(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator)
    {
        return InternalArrayIterate.reduceInPlace(this.items, this.size, supplier, accumulator);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfInt(this.items, this.size, function);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfLong(this.items, this.size, function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfFloat(this.items, this.size, function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfDouble(this.items, this.size, function);
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        return InternalArrayIterate.sumByInt(this.items, this.size, groupBy, function);
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        return InternalArrayIterate.sumByLong(this.items, this.size, groupBy, function);
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        return InternalArrayIterate.sumByFloat(this.items, this.size, groupBy, function);
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        return InternalArrayIterate.sumByDouble(this.items, this.size, groupBy, function);
    }

    @Override
    public <IV, P> IV injectIntoWith(
            IV injectValue,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        IV result = injectValue;
        for (int i = 0; i < this.size; i++)
        {
            result = function.value(result, this.items[i], parameter);
        }
        return result;
    }

    @Override
    public FastList<T> toList()
    {
        return FastList.newList(this);
    }

    @Override
    public FastList<T> toSortedList()
    {
        return this.toSortedList(Comparators.naturalOrder());
    }

    @Override
    public FastList<T> toSortedList(Comparator<? super T> comparator)
    {
        return FastList.newList(this).sortThis(comparator);
    }

    @Override
    public MutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        int endIndex = this.detectNotIndex(predicate);
        T[] result = (T[]) new Object[endIndex];
        System.arraycopy(this.items, 0, result, 0, endIndex);
        return FastList.newListWith(result);
    }

    @Override
    public MutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        int startIndex = this.detectNotIndex(predicate);
        int resultSize = this.size() - startIndex;
        T[] result = (T[]) new Object[resultSize];
        System.arraycopy(this.items, startIndex, result, 0, resultSize);
        return FastList.newListWith(result);
    }

    @Override
    public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        PartitionMutableList<T> result = new PartitionFastList<>();
        FastList<T> selected = (FastList<T>) result.getSelected();
        FastList<T> rejected = (FastList<T>) result.getRejected();
        int partitionIndex = this.detectNotIndex(predicate);
        int rejectedSize = this.size() - partitionIndex;
        selected.withArrayCopy(this.items, 0, partitionIndex);
        rejected.withArrayCopy(this.items, partitionIndex, rejectedSize);
        return result;
    }

    private int detectNotIndex(Predicate<? super T> predicate)
    {
        for (int index = 0; index < this.size; index++)
        {
            if (!predicate.accept(this.items[index]))
            {
                return index;
            }
        }
        return this.size;
    }

    @Override
    public boolean equals(Object that)
    {
        if (that == this)
        {
            return true;
        }
        if (!(that instanceof List))
        {
            return false;
        }
        if (that instanceof FastList)
        {
            return this.fastListEquals((FastList<?>) that);
        }
        return InternalArrayIterate.arrayEqualsList(this.items, this.size, (List<?>) that);
    }

    public boolean fastListEquals(FastList<?> that)
    {
        if (this.size != that.size)
        {
            return false;
        }
        for (int i = 0; i < this.size; i++)
        {
            if (!Objects.equals(this.items[i], that.items[i]))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (int i = 0; i < this.size; i++)
        {
            T item = this.items[i];
            hashCode = 31 * hashCode + (item == null ? 0 : item.hashCode());
        }
        return hashCode;
    }

    /**
     * @since 10.0
     */
    @Override
    public void replaceAll(UnaryOperator<T> operator)
    {
        InternalArrayIterate.replaceAll(this.items, this.size, operator);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.size());
        for (int i = 0; i < this.size; i++)
        {
            out.writeObject(this.items[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.size = in.readInt();
        this.items = (T[]) new Object[this.size];
        for (int i = 0; i < this.size; i++)
        {
            this.items[i] = (T) in.readObject();
        }
    }
}
