/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.immutable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.ParallelSortedSetIterable;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.impl.lazy.AbstractLazyIterable;
import org.eclipse.collections.impl.lazy.parallel.AbstractBatch;
import org.eclipse.collections.impl.lazy.parallel.AbstractParallelIterable;
import org.eclipse.collections.impl.lazy.parallel.list.ListBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.AbstractParallelSortedSetIterable;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.CollectSortedSetBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.FlatCollectSortedSetBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.RootSortedSetBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.SelectSortedSetBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.SortedSetBatch;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * An immutable sorted set backed by an {@link ImmutableList} of elements in logical comparator order.
 * <p>
 * View operations ({@code descendingSet}, {@code subSet}, {@code headSet}, {@code tailSet},
 * {@code take}, {@code drop}) are O(1) and share the backing list via the
 * {@link org.eclipse.collections.impl.list.immutable.ReversedImmutableList} /
 * {@link org.eclipse.collections.impl.list.immutable.AbstractImmutableList.ImmutableSubList}
 * infrastructure. Wrapper depth is bounded to at most 2
 * regardless of how many view operations are chained.
 * </p>
 */
final class ImmutableTreeSet<T>
        extends AbstractImmutableSortedSet<T>
        implements Serializable
{
    private static final long serialVersionUID = 2L;

    private final ImmutableList<T> sortedElements;
    private final Comparator<? super T> comparator;

    /**
     * View constructor: wraps an already-ordered list and its associated comparator.
     */
    private ImmutableTreeSet(ImmutableList<T> sortedElements, Comparator<? super T> comparator)
    {
        this.sortedElements = Objects.requireNonNull(sortedElements, "sortedElements");
        this.comparator = comparator;
    }

    public static <T> ImmutableSortedSet<T> newSetWith(T... elements)
    {
        return newSetWithComparator(null, elements.clone());
    }

    public static <T> ImmutableSortedSet<T> newSetWith(Comparator<? super T> comparator, T... elements)
    {
        return newSetWithComparator(comparator, elements.clone());
    }

    public static <T> ImmutableSortedSet<T> newSet(SortedSet<? super T> set)
    {
        T[] array = (T[]) set.toArray();
        if (ArrayIterate.contains(array, null))
        {
            throw new NullPointerException("Input SortedSet contains nulls!");
        }
        return new ImmutableTreeSet<>(Lists.immutable.with(array), (Comparator<? super T>) set.comparator());
    }

    public static <T> ImmutableSortedSet<T> newSetFromIterable(Iterable<? extends T> iterable)
    {
        return newSetWithComparator(null, (T[]) Iterate.toArray(iterable));
    }

    public static <T> ImmutableSortedSet<T> newSetFromIterable(Comparator<? super T> comparator, Iterable<? extends T> iterable)
    {
        return newSetWithComparator(comparator, (T[]) Iterate.toArray(iterable));
    }

    private static <T> ImmutableSortedSet<T> newSetWithComparator(Comparator<? super T> comparator, T[] input)
    {
        if (input.length == 0)
        {
            return SortedSets.immutable.empty(comparator);
        }
        if (ArrayIterate.contains(input, null))
        {
            throw new NullPointerException("Input array contains nulls!");
        }
        if (comparator == null && !(input[0] instanceof Comparable))
        {
            throw new ClassCastException("Comparator is null and input does not implement Comparable!");
        }
        Arrays.sort(input, comparator);

        T[] unique = (T[]) new Object[input.length];
        unique[0] = input[0];
        int uniqueCount = 1;
        for (int i = 1; i < input.length; i++)
        {
            int cmp = comparator == null
                    ? ((Comparable<? super T>) unique[uniqueCount - 1]).compareTo(input[i])
                    : comparator.compare(unique[uniqueCount - 1], input[i]);
            if (cmp < 0)
            {
                unique[uniqueCount] = input[i];
                uniqueCount++;
            }
        }
        T[] sortedUnique = uniqueCount < input.length ? Arrays.copyOf(unique, uniqueCount) : input;
        return new ImmutableTreeSet<>(Lists.immutable.with(sortedUnique), comparator);
    }

    private int binarySearch(T key)
    {
        return this.sortedElements.binarySearch(key, this.comparator);
    }

    private int lowerBound(T key)
    {
        int idx = this.binarySearch(key);
        return idx >= 0 ? idx : -(idx + 1);
    }

    private int upperBound(T key)
    {
        int idx = this.binarySearch(key);
        return idx >= 0 ? idx + 1 : -(idx + 1);
    }

    private T getOrNull(int index)
    {
        if (index >= 0 && index < this.size())
        {
            return this.sortedElements.get(index);
        }
        return null;
    }

    @Override
    public T lower(T e)
    {
        return this.getOrNull(this.lowerBound(e) - 1);
    }

    @Override
    public T floor(T e)
    {
        return this.getOrNull(this.upperBound(e) - 1);
    }

    @Override
    public T ceiling(T e)
    {
        return this.getOrNull(this.lowerBound(e));
    }

    @Override
    public T higher(T e)
    {
        return this.getOrNull(this.upperBound(e));
    }

    @Override
    public Iterator<T> descendingIterator()
    {
        return this.sortedElements.reversed().iterator();
    }

    @Override
    public AbstractImmutableSortedSet<T> descendingSet()
    {
        return new ImmutableTreeSet<>(this.sortedElements.reversed(), Collections.reverseOrder(this.comparator));
    }

    @Override
    public AbstractImmutableSortedSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive)
    {
        int cmp = this.compare(fromElement, toElement);
        if (cmp > 0)
        {
            throw new IllegalArgumentException("fromElement must not be greater than toElement");
        }
        int fromIdx = fromInclusive ? this.lowerBound(fromElement) : this.upperBound(fromElement);
        int toIdx = toInclusive ? this.upperBound(toElement) : this.lowerBound(toElement);
        return this.viewSubSet(fromIdx, toIdx);
    }

    @Override
    public AbstractImmutableSortedSet<T> headSet(T toElement, boolean inclusive)
    {
        int toIdx = inclusive ? this.upperBound(toElement) : this.lowerBound(toElement);
        return this.viewSubSet(0, toIdx);
    }

    @Override
    public AbstractImmutableSortedSet<T> tailSet(T fromElement, boolean inclusive)
    {
        int fromIdx = inclusive ? this.lowerBound(fromElement) : this.upperBound(fromElement);
        return this.viewSubSet(fromIdx, this.size());
    }

    private AbstractImmutableSortedSet<T> viewSubSet(int fromLogical, int toLogical)
    {
        if (fromLogical >= toLogical)
        {
            return new ImmutableEmptySortedSet<>(this.comparator);
        }
        if (fromLogical == 0 && toLogical == this.size())
        {
            return this;
        }
        return new ImmutableTreeSet<>(this.sortedElements.subList(fromLogical, toLogical), this.comparator);
    }

    @Override
    public int size()
    {
        return this.sortedElements.size();
    }

    private Object writeReplace()
    {
        return new ImmutableSortedSetSerializationProxy<>(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof Set))
        {
            return false;
        }
        Set<?> otherSet = (Set<?>) obj;
        if (otherSet.size() != this.size())
        {
            return false;
        }
        try
        {
            return this.containsAll(otherSet);
        }
        catch (ClassCastException ignored)
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        for (T each : this.sortedElements)
        {
            result += each.hashCode();
        }
        return result;
    }

    @Override
    public boolean contains(Object object)
    {
        return this.indexOf(object) >= 0;
    }

    @Override
    public Iterator<T> iterator()
    {
        return this.sortedElements.iterator();
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.sortedElements.each(procedure);
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> ImmutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.sortedElements.collectWithIndex(function);
    }

    @Override
    public T first()
    {
        return this.sortedElements.getFirst();
    }

    @Override
    public T last()
    {
        return this.sortedElements.getLast();
    }

    @Override
    public Comparator<? super T> comparator()
    {
        return this.comparator;
    }

    @Override
    public int compareTo(SortedSetIterable<T> otherSet)
    {
        Iterator<T> iterator = otherSet.iterator();

        for (T eachInThis : this.sortedElements)
        {
            if (!iterator.hasNext())
            {
                return 1;
            }

            T eachInOther = iterator.next();

            int compare = this.compare(eachInThis, eachInOther);
            if (compare != 0)
            {
                return compare;
            }
        }

        return iterator.hasNext() ? -1 : 0;
    }

    private int compare(T o1, T o2)
    {
        return this.comparator == null
                ? ((Comparable<T>) o1).compareTo(o2)
                : this.comparator.compare(o1, o2);
    }

    @Override
    public ImmutableSortedSet<T> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (count == 0)
        {
            return SortedSets.immutable.empty(this.comparator);
        }
        if (count >= this.size())
        {
            return this;
        }
        return new ImmutableTreeSet<>(this.sortedElements.subList(0, count), this.comparator);
    }

    @Override
    public ImmutableSortedSet<T> drop(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (count == 0)
        {
            return this;
        }
        if (count >= this.size())
        {
            return SortedSets.immutable.empty(this.comparator);
        }
        return new ImmutableTreeSet<>(this.sortedElements.subList(count, this.size()), this.comparator);
    }

    @Override
    public ParallelSortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new SortedSetIterableParallelIterable(executorService, batchSize);
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return this.sortedElements.detectIndex(predicate);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return this.sortedElements.corresponds(other, predicate);
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        this.sortedElements.forEach(fromIndex, toIndex, procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.sortedElements.forEachWithIndex(objectIntProcedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.sortedElements.forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
    }

    /**
     * Returns the index of the specified element, or {@code -1} if not present.
     * <p>
     * Membership is determined by the set's comparator (or natural ordering when
     * the comparator is {@code null}), consistent with {@link java.util.TreeSet}.
     * If the comparator is inconsistent with {@code equals}, an element that is
     * comparator-equal but not {@code equals}-equal to a member will be found.
     * </p>
     */
    @Override
    public int indexOf(Object object)
    {
        int idx = this.binarySearch((T) object);
        return idx >= 0 ? idx : -1;
    }

    private final class SortedSetIterableParallelIterable extends AbstractParallelSortedSetIterable<T, RootSortedSetBatch<T>>
    {
        private final ExecutorService executorService;
        private final int batchSize;

        private SortedSetIterableParallelIterable(ExecutorService executorService, int batchSize)
        {
            if (executorService == null)
            {
                throw new NullPointerException();
            }
            if (batchSize < 1)
            {
                throw new IllegalArgumentException();
            }
            this.executorService = executorService;
            this.batchSize = batchSize;
        }

        @Override
        public Comparator<? super T> comparator()
        {
            return ImmutableTreeSet.this.comparator;
        }

        @Override
        public ExecutorService getExecutorService()
        {
            return this.executorService;
        }

        @Override
        public LazyIterable<RootSortedSetBatch<T>> split()
        {
            return new SortedSetIterableParallelBatchLazyIterable();
        }

        @Override
        public void forEach(Procedure<? super T> procedure)
        {
            AbstractParallelIterable.forEach(this, procedure);
        }

        @Override
        public boolean anySatisfy(Predicate<? super T> predicate)
        {
            return AbstractParallelIterable.anySatisfy(this, predicate);
        }

        @Override
        public boolean allSatisfy(Predicate<? super T> predicate)
        {
            return AbstractParallelIterable.allSatisfy(this, predicate);
        }

        @Override
        public T detect(Predicate<? super T> predicate)
        {
            return AbstractParallelIterable.detect(this, predicate);
        }

        @Override
        public Object[] toArray()
        {
            // TODO: Implement in parallel
            return ImmutableTreeSet.this.toArray();
        }

        @Override
        public <E> E[] toArray(E[] array)
        {
            // TODO: Implement in parallel
            return ImmutableTreeSet.this.toArray(array);
        }

        @Override
        public <V> ImmutableSortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
        {
            // TODO: Implement in parallel
            return ImmutableTreeSet.this.groupBy(function);
        }

        @Override
        public <V> ImmutableSortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
        {
            // TODO: Implement in parallel
            return ImmutableTreeSet.this.groupByEach(function);
        }

        @Override
        public <V> MapIterable<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
        {
            // TODO: Implement in parallel
            return ImmutableTreeSet.this.groupByUniqueKey(function);
        }

        @Override
        public int getBatchSize()
        {
            return this.batchSize;
        }

        private class SortedSetIterableParallelBatchIterator implements Iterator<RootSortedSetBatch<T>>
        {
            protected int chunkIndex;

            @Override
            public boolean hasNext()
            {
                return this.chunkIndex * SortedSetIterableParallelIterable.this.getBatchSize() < ImmutableTreeSet.this.size();
            }

            @Override
            public RootSortedSetBatch<T> next()
            {
                int chunkStartIndex = this.chunkIndex * SortedSetIterableParallelIterable.this.getBatchSize();
                int chunkEndIndex = (this.chunkIndex + 1) * SortedSetIterableParallelIterable.this.getBatchSize();
                int truncatedChunkEndIndex = Math.min(chunkEndIndex, ImmutableTreeSet.this.size());
                this.chunkIndex++;
                return new ImmutableTreeSetBatch<>(ImmutableTreeSet.this.sortedElements.subList(chunkStartIndex, truncatedChunkEndIndex));
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Cannot call remove() on " + ImmutableTreeSet.this.getClass().getSimpleName());
            }
        }

        private class SortedSetIterableParallelBatchLazyIterable
                extends AbstractLazyIterable<RootSortedSetBatch<T>>
        {
            @Override
            public void each(Procedure<? super RootSortedSetBatch<T>> procedure)
            {
                for (RootSortedSetBatch<T> chunk : this)
                {
                    procedure.value(chunk);
                }
            }

            @Override
            public Iterator<RootSortedSetBatch<T>> iterator()
            {
                return new SortedSetIterableParallelBatchIterator();
            }
        }
    }

    private static final class ImmutableTreeSetBatch<T> extends AbstractBatch<T> implements RootSortedSetBatch<T>
    {
        private final RichIterable<T> chunk;

        private ImmutableTreeSetBatch(RichIterable<T> chunk)
        {
            this.chunk = chunk;
        }

        @Override
        public void forEach(Procedure<? super T> procedure)
        {
            this.chunk.each(procedure);
        }

        @Override
        public int count(Predicate<? super T> predicate)
        {
            return this.chunk.count(predicate);
        }

        @Override
        public boolean anySatisfy(Predicate<? super T> predicate)
        {
            return this.chunk.anySatisfy(predicate);
        }

        @Override
        public boolean allSatisfy(Predicate<? super T> predicate)
        {
            return this.chunk.allSatisfy(predicate);
        }

        @Override
        public T detect(Predicate<? super T> predicate)
        {
            return this.chunk.detect(predicate);
        }

        @Override
        public SortedSetBatch<T> select(Predicate<? super T> predicate)
        {
            return new SelectSortedSetBatch<>(this, predicate);
        }

        @Override
        public <V> ListBatch<V> collect(Function<? super T, ? extends V> function)
        {
            return new CollectSortedSetBatch<>(this, function);
        }

        @Override
        public <V> ListBatch<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
        {
            return new FlatCollectSortedSetBatch<>(this, function);
        }

        @Override
        public SortedSetBatch<T> distinct(ConcurrentHashMap<T, Boolean> distinct)
        {
            return this;
        }
    }
}
