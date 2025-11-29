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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.ParallelSortedSetIterable;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.impl.lazy.AbstractLazyIterable;
import org.eclipse.collections.impl.lazy.ReverseIterable;
import org.eclipse.collections.impl.lazy.parallel.AbstractBatch;
import org.eclipse.collections.impl.lazy.parallel.AbstractParallelIterable;
import org.eclipse.collections.impl.lazy.parallel.list.ListBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.AbstractParallelSortedSetIterable;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.CollectSortedSetBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.FlatCollectSortedSetBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.RootSortedSetBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.SelectSortedSetBatch;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.SortedSetBatch;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.UnmodifiableMutableList;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.ListIterate;

final class ImmutableTreeSet<T>
        extends AbstractImmutableSortedSet<T>
        implements Serializable
{
    private static final long serialVersionUID = 2L;

    private final UnmodifiableMutableList<T> delegate;
    private final Comparator<? super T> comparator;

    private ImmutableTreeSet(T[] input, Comparator<? super T> inputComparator, boolean isSortedAndUnique)
    {
        if (ArrayIterate.contains(input, null))
        {
            throw new NullPointerException("Input array contains nulls!");
        }

        if (isSortedAndUnique)
        {
            for (int i = input.length - 1; i > 0; i--)
            {
                int compare = inputComparator == null
                        ? ((Comparable<? super T>) input[i - 1]).compareTo(input[i])
                        : inputComparator.compare(input[i - 1], input[i]);
                if (compare >= 0)
                {
                    throw new ConcurrentModificationException("Input Array expected to be sorted, but was not!");
                }
            }
        }
        else
        {
            if (input.length > 0)
            {
                Arrays.sort(input, inputComparator);
                T[] unique = (T[]) new Object[input.length];
                unique[0] = input[0];

                if (inputComparator == null && !(input[0] instanceof Comparable))
                {
                    throw new ClassCastException("Comparator is null and input does not implement Comparable!");
                }

                int uniqueCount = 1;
                for (int i = 1; i < input.length; i++)
                {
                    int compare = inputComparator == null
                            ? ((Comparable<? super T>) unique[uniqueCount - 1]).compareTo(input[i])
                            : inputComparator.compare(unique[uniqueCount - 1], input[i]);
                    if (compare < 0)
                    {
                        unique[uniqueCount] = input[i];
                        uniqueCount++;
                    }
                }
                if (uniqueCount < input.length)
                {
                    input = Arrays.copyOf(unique, uniqueCount);
                }
            }
        }

        this.delegate = UnmodifiableMutableList.of(FastList.newListWith(input));
        this.comparator = inputComparator;
    }

    private ImmutableTreeSet(UnmodifiableMutableList<T> delegate, Comparator<? super T> comparator)
    {
        this.delegate = delegate;
        this.comparator = comparator;
    }

    public static <T> ImmutableSortedSet<T> newSetWith(T... elements)
    {
        return new ImmutableTreeSet<>(elements.clone(), null, false);
    }

    public static <T> ImmutableSortedSet<T> newSetWith(Comparator<? super T> comparator, T... elements)
    {
        return new ImmutableTreeSet<>(elements.clone(), comparator, false);
    }

    public static <T> ImmutableSortedSet<T> newSet(SortedSet<? super T> set)
    {
        return new ImmutableTreeSet<>((T[]) set.toArray(), set.comparator(), true);
    }

    public static <T> ImmutableSortedSet<T> newSetFromIterable(Iterable<? extends T> iterable)
    {
        return new ImmutableTreeSet<>((T[]) Iterate.toArray(iterable), null, false);
    }

    public static <T> ImmutableSortedSet<T> newSetFromIterable(Comparator<? super T> comparator, Iterable<? extends T> iterable)
    {
        return new ImmutableTreeSet<>((T[]) Iterate.toArray(iterable), comparator, false);
    }

    @Override
    public int size()
    {
        return this.delegate.size();
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
        for (T each : this.delegate)
        {
            result += each.hashCode();
        }

        return result;
    }

    @Override
    public boolean contains(Object object)
    {
        return this.delegate.binarySearch((T) object, this.comparator) >= 0;
    }

    @Override
    public Iterator<T> iterator()
    {
        return this.delegate.iterator();
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        for (T t : this.delegate)
        {
            procedure.value(t);
        }
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> ImmutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        MutableList<V> result = FastList.newList(this.size());
        int index = 0;
        for (T t : this.delegate)
        {
            result.add(function.valueOf(t, index++));
        }
        return result.toImmutable();
    }

    @Override
    public T first()
    {
        return this.delegate.getFirst();
    }

    @Override
    public T last()
    {
        return this.delegate.getLast();
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

        for (T eachInThis : this.delegate)
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
    public ParallelSortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new SortedSetIterableParallelIterable(executorService, batchSize);
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
                return new ImmutableTreeSetBatch(chunkStartIndex, truncatedChunkEndIndex);
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

    private final class ImmutableTreeSetBatch extends AbstractBatch<T> implements RootSortedSetBatch<T>
    {
        private final int chunkStartIndex;
        private final int chunkEndIndex;

        private ImmutableTreeSetBatch(int chunkStartIndex, int chunkEndIndex)
        {
            this.chunkStartIndex = chunkStartIndex;
            this.chunkEndIndex = chunkEndIndex;
        }

        @Override
        public void forEach(Procedure<? super T> procedure)
        {
            for (int i = this.chunkStartIndex; i < this.chunkEndIndex; i++)
            {
                procedure.value(ImmutableTreeSet.this.delegate.get(i));
            }
        }

        @Override
        public int count(Predicate<? super T> predicate)
        {
            int count = 0;
            for (int i = this.chunkStartIndex; i < this.chunkEndIndex; i++)
            {
                if (predicate.accept(ImmutableTreeSet.this.delegate.get(i)))
                {
                    count++;
                }
            }
            return count;
        }

        @Override
        public boolean anySatisfy(Predicate<? super T> predicate)
        {
            for (int i = this.chunkStartIndex; i < this.chunkEndIndex; i++)
            {
                if (predicate.accept(ImmutableTreeSet.this.delegate.get(i)))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean allSatisfy(Predicate<? super T> predicate)
        {
            for (int i = this.chunkStartIndex; i < this.chunkEndIndex; i++)
            {
                if (!predicate.accept(ImmutableTreeSet.this.delegate.get(i)))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public T detect(Predicate<? super T> predicate)
        {
            for (int i = this.chunkStartIndex; i < this.chunkEndIndex; i++)
            {
                if (predicate.accept(ImmutableTreeSet.this.delegate.get(i)))
                {
                    return ImmutableTreeSet.this.delegate.get(i);
                }
            }
            return null;
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

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return this.delegate.detectIndex(predicate);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return this.delegate.corresponds(other, predicate);
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, this.size());

        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }
        this.delegate.forEach(fromIndex, toIndex, procedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, this.size());

        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }
        this.delegate.forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public int indexOf(Object object)
    {
        int idx = this.delegate.binarySearch((T) object, this.comparator);

        if (idx < 0)
        {
            return -1;
        }
        return idx;
    }

    @Override
    public ImmutableSortedSet<T> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (count >= this.size())
        {
            return this;
        }
        if (count == 0)
        {
            return SortedSets.immutable.empty(this.comparator());
        }

        return SortedSets.immutable.ofSortedSet(this.subSet(0, count));
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
            return SortedSets.immutable.empty(this.comparator());
        }

        return SortedSets.immutable.ofSortedSet(this.subSet(count, this.size()));
    }

    @Override
    public T lower(T e)
    {
        int idx = this.delegate.binarySearch(e, this.comparator());

        if (idx < 0)
        {
            idx = -(idx + 1);
        }
        if (idx == 0)
        {
            return null;
        }
        return this.delegate.get(idx - 1);
    }

    @Override
    public T floor(T e)
    {
        int idx = this.delegate.binarySearch(e, this.comparator());

        if (idx >= 0)
        {
            return this.delegate.get(idx);
        }

        idx = -(idx + 1);

        if (idx == 0)
        {
            return null;
        }
        return this.delegate.get(idx - 1);
    }

    @Override
    public T ceiling(T e)
    {
        int idx = this.delegate.binarySearch(e, this.comparator());

        if (idx >= 0)
        {
            return this.delegate.get(idx);
        }

        idx = -(idx + 1);

        if (idx >= this.delegate.size())
        {
            return null;
        }
        return this.delegate.get(idx);
    }

    @Override
    public T higher(T e)
    {
        int idx = this.delegate.binarySearch(e, this.comparator());

        if (idx >= 0)
        {
            idx++;
        }
        else
        {
            idx = -(idx + 1);
        }

        if (idx >= this.delegate.size())
        {
            return null;
        }
        return this.delegate.get(idx);
    }

    @Override
    public Iterator<T> descendingIterator()
    {
        return ReverseIterable.adapt(this.delegate).iterator();
    }

    @Override
    public ImmutableTreeSet<T> descendingSet()
    {
        if (this.isEmpty())
        {
            return this;
        }
        Comparator<? super T> reversedComparator = this.comparator() == null
                ? Collections.reverseOrder()
                : Collections.reverseOrder(this.comparator());
        return new ImmutableTreeSet<>(this.delegate.asReversedList(), reversedComparator);
    }

    @Override
    public ImmutableTreeSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive)
    {
        if (this.isEmpty())
        {
            return this;
        }
        int fromIdx = this.delegate.binarySearch(fromElement, this.comparator);
        int toIdx = this.delegate.binarySearch(toElement, this.comparator);

        if (fromIdx < 0)
        {
            fromIdx = -(fromIdx + 1);
        }
        else if (!fromInclusive)
        {
            ++fromIdx;
        }

        if (toIdx < 0)
        {
            toIdx = -(toIdx + 1);
        }
        else if (toInclusive)
        {
            ++toIdx;
        }

        return this.subSet(fromIdx, toIdx);
    }

    @Override
    public ImmutableTreeSet<T> headSet(T toElement, boolean inclusive)
    {
        if (this.isEmpty())
        {
            return this;
        }
        int toIdx = Collections.binarySearch(this.delegate, toElement, this.comparator);

        if (toIdx < 0)
        {
            toIdx = -(toIdx + 1);
        }
        else if (inclusive)
        {
            ++toIdx;
        }

        return this.subSet(0, toIdx);
    }

    @Override
    public ImmutableTreeSet<T> tailSet(T fromElement, boolean inclusive)
    {
        if (this.isEmpty())
        {
            return this;
        }
        int fromIdx = this.delegate.binarySearch(fromElement, this.comparator);

        if (fromIdx < 0)
        {
            fromIdx = -(fromIdx + 1);
        }
        else if (!inclusive)
        {
            ++fromIdx;
        }

        return this.subSet(fromIdx, this.size());
    }

    private ImmutableTreeSet<T> subSet(int fromIdx, int toIdx)
    {
        if (fromIdx == 0 && toIdx == this.delegate.size())
        {
            return this;
        }
        UnmodifiableMutableList<T> view = this.delegate.subList(fromIdx, toIdx);
        return new ImmutableTreeSet<>(view, this.comparator);
    }
}
