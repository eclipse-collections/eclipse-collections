/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.immutable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.partition.list.PartitionImmutableListImpl;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.eclipse.collections.impl.utility.internal.InternalArrayIterate;
import org.eclipse.collections.impl.utility.internal.RandomAccessListIterate;

/**
 * An ImmutableArrayList wraps a Java array, but it cannot be modified after creation.
 */
final class ImmutableArrayList<T>
        extends AbstractImmutableList<T>
        implements Serializable, RandomAccess, BatchIterable<T>
{
    private static final long serialVersionUID = 1L;
    private final T[] items;

    ImmutableArrayList(T[] newElements)
    {
        if (newElements == null)
        {
            throw new IllegalArgumentException("items cannot be null");
        }
        this.items = newElements;
    }

    public static <E> ImmutableArrayList<E> newList(Iterable<? extends E> iterable)
    {
        return new ImmutableArrayList<>((E[]) Iterate.toArray(iterable));
    }

    public static <E> ImmutableArrayList<E> newListWith(E... elements)
    {
        return new ImmutableArrayList<>(elements.clone());
    }

    @Override
    public ImmutableList<T> newWith(T newItem)
    {
        int oldSize = this.size();
        T[] array = (T[]) new Object[oldSize + 1];
        this.toArray(array);
        array[oldSize] = newItem;
        return new ImmutableArrayList<>(array);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(this.items);
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
        if (that instanceof ImmutableArrayList)
        {
            return this.immutableArrayListEquals((ImmutableArrayList<?>) that);
        }
        return InternalArrayIterate.arrayEqualsList(this.items, this.items.length, (List<?>) that);
    }

    public boolean immutableArrayListEquals(ImmutableArrayList<?> otherList)
    {
        return Arrays.equals(this.items, otherList.items);
    }

    @Override
    public boolean notEmpty()
    {
        return this.items.length > 0;
    }

    @Override
    public T getFirst()
    {
        return this.isEmpty() ? null : this.items[0];
    }

    @Override
    public T getLast()
    {
        return this.isEmpty() ? null : this.items[this.items.length - 1];
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        for (T each : this.items)
        {
            procedure.value(each);
        }
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        InternalArrayIterate.forEachWithIndex(this.items, this.items.length, objectIntProcedure);
    }

    @Override
    public void batchForEach(Procedure<? super T> procedure, int sectionIndex, int sectionCount)
    {
        InternalArrayIterate.batchForEach(procedure, this.items, this.items.length, sectionIndex, sectionCount);
    }

    @Override
    public int getBatchCount(int batchSize)
    {
        return Math.max(1, this.size() / batchSize);
    }

    @Override
    public void forEachWithIndex(int from, int to, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(from, to, this.items.length);
        InternalArrayIterate.forEachWithIndexWithoutChecks(this.items, from, to, objectIntProcedure);
    }

    @Override
    public ImmutableList<T> select(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.select(this.items, this.items.length, predicate, FastList.newList()).toImmutable();
    }

    @Override
    public <P> ImmutableList<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.selectWith(this.items, this.items.length, predicate, parameter, FastList.newList()).toImmutable();
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return InternalArrayIterate.selectWith(this.items, this.items.length, predicate, parameter, target);
    }

    @Override
    public ImmutableList<T> reject(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.reject(this.items, this.items.length, predicate, FastList.newList()).toImmutable();
    }

    @Override
    public <P> ImmutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.rejectWith(this.items, this.items.length, predicate, parameter, FastList.newList()).toImmutable();
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return InternalArrayIterate.rejectWith(this.items, this.items.length, predicate, parameter, target);
    }

    @Override
    public PartitionImmutableList<T> partition(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.partition(this.items, this.items.length, predicate).toImmutable();
    }

    @Override
    public <P> PartitionImmutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.partitionWith(this.items, this.items.length, predicate, parameter).toImmutable();
    }

    @Override
    public <S> ImmutableList<S> selectInstancesOf(Class<S> clazz)
    {
        return InternalArrayIterate.selectInstancesOf(this.items, this.items.length, clazz).toImmutable();
    }

    @Override
    public <V> ImmutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return InternalArrayIterate.collect(this.items, this.items.length, function, FastList.<V>newList(this.items.length)).toImmutable();
    }

    @Override
    public <P, V> ImmutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return InternalArrayIterate.collectWith(this.items, this.items.length, function, parameter, FastList.<V>newList(this.items.length)).toImmutable();
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> ImmutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.collectWithIndex(function, FastList.<V>newList(this.items.length)).toImmutable();
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        return InternalArrayIterate.collectWithIndex(this.items, this.items.length, function, target);
    }

    /**
     * @since 11.0
     */
    @Override
    public ImmutableList<T> selectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        return this.selectWithIndex(predicate, FastList.newList()).toImmutable();
    }

    /**
     * @since 11.0
     */
    @Override
    public ImmutableList<T> rejectWithIndex(ObjectIntPredicate<? super T> predicate)
    {
        return this.rejectWithIndex(predicate, FastList.newList()).toImmutable();
    }

    /**
     * @since 11.0
     */
    @Override
    public <R extends Collection<T>> R selectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        return InternalArrayIterate.selectWithIndex(this.items, this.items.length, predicate, target);
    }

    /**
     * @since 11.0
     */
    @Override
    public <R extends Collection<T>> R rejectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        return InternalArrayIterate.rejectWithIndex(this.items, this.items.length, predicate, target);
    }

    @Override
    public <V> ImmutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return InternalArrayIterate.collectIf(this.items, this.items.length, predicate, function, FastList.<V>newList(this.items.length)).toImmutable();
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R target)
    {
        return InternalArrayIterate.collectWith(this.items, this.items.length, function, parameter, target);
    }

    @Override
    public <V> ImmutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return InternalArrayIterate.flatCollect(this.items, this.items.length, function, Lists.mutable.withInitialCapacity(this.items.length)).toImmutable();
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return InternalArrayIterate.flatCollect(this.items, this.items.length, function, target);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.detect(this.items, this.items.length, predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.detectWith(this.items, this.items.length, predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.detectOptional(this.items, this.items.length, predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return InternalArrayIterate.detectWithOptional(this.items, this.items.length, predicate, parameter);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.count(this.items, this.items.length, predicate);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return InternalArrayIterate.corresponds(this.items, this.items.length, other, predicate);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.anySatisfy(this.items, this.items.length, predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.allSatisfy(this.items, this.items.length, predicate);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return InternalArrayIterate.noneSatisfy(this.items, this.items.length, predicate);
    }

    /**
     * @since 8.0
     */
    @Override
    public IntSummaryStatistics summarizeInt(IntFunction<? super T> function)
    {
        return InternalArrayIterate.summarizeInt(this.items, this.items.length, function);
    }

    /**
     * @since 8.0
     */
    @Override
    public DoubleSummaryStatistics summarizeFloat(FloatFunction<? super T> function)
    {
        return InternalArrayIterate.summarizeFloat(this.items, this.items.length, function);
    }

    /**
     * @since 8.0
     */
    @Override
    public LongSummaryStatistics summarizeLong(LongFunction<? super T> function)
    {
        return InternalArrayIterate.summarizeLong(this.items, this.items.length, function);
    }

    /**
     * @since 8.0
     */
    @Override
    public DoubleSummaryStatistics summarizeDouble(DoubleFunction<? super T> function)
    {
        return InternalArrayIterate.summarizeDouble(this.items, this.items.length, function);
    }

    /**
     * @since 8.0
     */
    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator)
    {
        return InternalArrayIterate.reduce(this.items, this.items.length, accumulator);
    }

    /**
     * @since 8.0
     */
    @Override
    public <R, A> R reduceInPlace(Collector<? super T, A, R> collector)
    {
        return InternalArrayIterate.reduceInPlace(this.items, this.items.length, collector);
    }

    /**
     * @since 8.0
     */
    @Override
    public <R> R reduceInPlace(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator)
    {
        return InternalArrayIterate.reduceInPlace(this.items, this.items.length, supplier, accumulator);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfInt(this.items, this.items.length, function);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfLong(this.items, this.items.length, function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfFloat(this.items, this.items.length, function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        return InternalArrayIterate.sumOfDouble(this.items, this.items.length, function);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        IV result = injectedValue;
        for (T each : this.items)
        {
            result = function.value(result, each);
        }
        return result;
    }

    @Override
    public int size()
    {
        return this.items.length;
    }

    @Override
    public boolean isEmpty()
    {
        return this.items.length == 0;
    }

    @Override
    public boolean contains(Object o)
    {
        return InternalArrayIterate.anySatisfyWith(this.items, this.items.length, Predicates2.equal(), o);
    }

    @Override
    public Iterator<T> iterator()
    {
        return Arrays.asList(this.items).iterator();
    }

    @Override
    public Object[] toArray()
    {
        return this.items.clone();
    }

    @Override
    public <E> E[] toArray(E[] a)
    {
        int size = this.size();
        if (a.length < size)
        {
            a = (E[]) Array.newInstance(a.getClass().getComponentType(), size);
        }
        System.arraycopy(this.items, 0, a, 0, size);
        if (a.length > size)
        {
            a[size] = null;
        }
        return a;
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        InternalArrayIterate.appendString(this, this.items, this.items.length, appendable, start, separator, end);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append('[');

        int localSize = this.items.length;
        for (int i = 0; i < localSize; i++)
        {
            T item = this.items[i];
            if (i > 0)
            {
                buf.append(", ");
            }
            buf.append(item == this ? "(this ImmutableArrayList)" : String.valueOf(item));
        }

        buf.append(']');
        return buf.toString();
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        return this.containsAllIterable(collection);
    }

    @Override
    public T get(int index)
    {
        return this.items[index];
    }

    @Override
    public int indexOf(Object item)
    {
        return InternalArrayIterate.indexOf(this.items, this.items.length, item);
    }

    @Override
    public int lastIndexOf(Object item)
    {
        return InternalArrayIterate.lastIndexOf(this.items, this.items.length, item);
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return InternalArrayIterate.minBy(this.items, this.items.length, function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return InternalArrayIterate.maxBy(this.items, this.items.length, function);
    }

    @Override
    public ImmutableList<T> take(int count)
    {
        if (count >= this.size())
        {
            return this;
        }
        return RandomAccessListIterate.take(this, count).toImmutable();
    }

    @Override
    public ImmutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        int endIndex = this.detectNotIndex(predicate);
        T[] result = (T[]) new Object[endIndex];
        System.arraycopy(this.items, 0, result, 0, endIndex);
        return Lists.immutable.with(result);
    }

    @Override
    public ImmutableList<T> drop(int count)
    {
        if (count == 0)
        {
            return this;
        }
        return RandomAccessListIterate.drop(this, count).toImmutable();
    }

    @Override
    public ImmutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        int startIndex = this.detectNotIndex(predicate);
        int resultSize = this.size() - startIndex;
        T[] result = (T[]) new Object[resultSize];
        System.arraycopy(this.items, startIndex, result, 0, resultSize);
        return Lists.immutable.with(result);
    }

    @Override
    public PartitionImmutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        int partitionIndex = this.detectNotIndex(predicate);
        int rejectedSize = this.size() - partitionIndex;
        T[] selectedArray = (T[]) new Object[partitionIndex];
        T[] rejectedArray = (T[]) new Object[rejectedSize];
        System.arraycopy(this.items, 0, selectedArray, 0, partitionIndex);
        System.arraycopy(this.items, partitionIndex, rejectedArray, 0, rejectedSize);
        ImmutableArrayList<T> selected = new ImmutableArrayList<>(selectedArray);
        ImmutableArrayList<T> rejected = new ImmutableArrayList<>(rejectedArray);
        return new PartitionImmutableListImpl<>(selected, rejected);
    }

    private int detectNotIndex(Predicate<? super T> predicate)
    {
        for (int index = 0; index < this.size(); index++)
        {
            if (!predicate.accept(this.items[index]))
            {
                return index;
            }
        }
        return this.size();
    }

    /**
     * @since 9.0
     */
    @Override
    public Spliterator<T> spliterator()
    {
        return Spliterators.spliterator(this.items, 0, this.items.length, Spliterator.ORDERED);
    }
}
