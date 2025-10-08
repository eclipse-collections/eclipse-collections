/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.LazyIterable;
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
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.SortedSets;
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
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.sorted.PartitionMutableSortedSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.ParallelSortedSetIterable;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.block.procedure.PartitionPredicate2Procedure;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.collection.mutable.AbstractCollectionAdapter;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.NonParallelSortedSetIterable;
import org.eclipse.collections.impl.multimap.set.sorted.TreeSortedSetMultimap;
import org.eclipse.collections.impl.partition.set.sorted.PartitionTreeSortedSet;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.eclipse.collections.impl.utility.OrderedIterate;
import org.eclipse.collections.impl.utility.internal.IterableIterate;
import org.eclipse.collections.impl.utility.internal.SetIterables;
import org.eclipse.collections.impl.utility.internal.SetIterate;
import org.eclipse.collections.impl.utility.internal.SortedSetIterables;

/**
 * This class provides a MutableSortedSet wrapper around a JDK Collections SortedSet interface instance. All the MutableSortedSet
 * interface methods are supported in addition to the JDK SortedSet interface methods.
 * <p>
 * To create a new wrapper around an existing SortedSet instance, use the {@link #adapt(SortedSet)} factory method.
 */
public final class SortedSetAdapter<T>
        extends AbstractCollectionAdapter<T>
        implements Serializable, MutableSortedSet<T>
{
    private static final long serialVersionUID = 1L;
    private final SortedSet<T> delegate;

    SortedSetAdapter(SortedSet<T> newDelegate)
    {
        if (newDelegate == null)
        {
            throw new NullPointerException("SortedSetAdapter may not wrap null");
        }
        this.delegate = newDelegate;
    }

    @Override
    protected SortedSet<T> getDelegate()
    {
        return this.delegate;
    }

    @Override
    public MutableSortedSet<T> asUnmodifiable()
    {
        return UnmodifiableSortedSet.of(this);
    }

    @Override
    public MutableSortedSet<T> asSynchronized()
    {
        return SynchronizedSortedSet.of(this);
    }

    @Override
    public ImmutableSortedSet<T> toImmutable()
    {
        return SortedSets.immutable.withSortedSet(this.delegate);
    }

    public static <T> MutableSortedSet<T> adapt(SortedSet<T> set)
    {
        if (set instanceof MutableSortedSet<?>)
        {
            return (MutableSortedSet<T>) set;
        }
        return new SortedSetAdapter<>(set);
    }

    @Override
    public MutableSortedSet<T> clone()
    {
        return SortedSets.mutable.withAll(this.delegate);
    }

    @Override
    public boolean contains(Object o)
    {
        return this.delegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        return this.delegate.containsAll(collection);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        return this.delegate.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public SortedSetAdapter<T> with(T element)
    {
        this.add(element);
        return this;
    }

    public SortedSetAdapter<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public SortedSetAdapter<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public SortedSetAdapter<T> with(T... elements)
    {
        ArrayIterate.forEach(elements, CollectionAddProcedure.on(this.delegate));
        return this;
    }

    @Override
    public SortedSetAdapter<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public SortedSetAdapter<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    public SortedSetAdapter<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    /**
     * @deprecated use {@link TreeSortedSet#newSet()} instead (inlineable)
     */
    @Override
    @Deprecated
    public MutableSortedSet<T> newEmpty()
    {
        return TreeSortedSet.newSet(this.comparator());
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        return SetIterate.removeAllIterable(this, iterable);
    }

    @Override
    public MutableSortedSet<T> tap(Procedure<? super T> procedure)
    {
        Iterate.forEach(this.delegate, procedure);
        return this;
    }

    @Override
    public MutableSortedSet<T> select(Predicate<? super T> predicate)
    {
        return Iterate.select(this.delegate, predicate, TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public MutableSortedSet<T> reject(Predicate<? super T> predicate)
    {
        return Iterate.reject(this.delegate, predicate, TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public PartitionMutableSortedSet<T> partition(Predicate<? super T> predicate)
    {
        PartitionMutableSortedSet<T> partitionMutableSortedSet = new PartitionTreeSortedSet<>(this.comparator());
        this.forEach(new PartitionProcedure<>(predicate, partitionMutableSortedSet));
        return partitionMutableSortedSet;
    }

    @Override
    public <P> PartitionMutableSortedSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionMutableSortedSet<T> partitionMutableSortedSet = new PartitionTreeSortedSet<>(this.comparator());
        this.forEach(new PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableSortedSet));
        return partitionMutableSortedSet;
    }

    @Override
    public PartitionMutableSortedSet<T> partitionWhile(Predicate<? super T> predicate)
    {
        PartitionTreeSortedSet<T> result = new PartitionTreeSortedSet<>(this.comparator());
        return IterableIterate.partitionWhile(this, predicate, result);
    }

    @Override
    public MutableSortedSet<T> takeWhile(Predicate<? super T> predicate)
    {
        MutableSortedSet<T> result = TreeSortedSet.newSet(this.comparator());
        return IterableIterate.takeWhile(this, predicate, result);
    }

    @Override
    public MutableSortedSet<T> dropWhile(Predicate<? super T> predicate)
    {
        MutableSortedSet<T> result = TreeSortedSet.newSet(this.comparator());
        return IterableIterate.dropWhile(this, predicate, result);
    }

    @Override
    public <S> MutableSortedSet<S> selectInstancesOf(Class<S> clazz)
    {
        TreeSortedSet<S> result = TreeSortedSet.newSet((Comparator<? super S>) this.comparator());
        this.forEach(new SelectInstancesOfProcedure<>(clazz, result));
        return result;
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return Iterate.collect(this.delegate, function, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.collectBoolean(booleanFunction, BooleanLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.collectByte(byteFunction, ByteLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        return this.collectChar(charFunction, CharLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.collectDouble(doubleFunction, DoubleLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.collectFloat(floatFunction, FloatLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        return this.collectInt(intFunction, IntLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        return this.collectLong(longFunction, LongLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.collectShort(shortFunction, ShortLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return Iterate.collectIf(this.delegate, predicate, function, Lists.mutable.empty());
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return Iterate.flatCollect(this.delegate, function, Lists.mutable.empty());
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return Iterate.detectIndex(this.delegate, predicate);
    }

    @Override
    public <V> TreeSortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return Iterate.groupBy(this.delegate, function, TreeSortedSetMultimap.newMultimap(this.comparator()));
    }

    @Override
    public <V> TreeSortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return Iterate.groupByEach(this.delegate, function, TreeSortedSetMultimap.newMultimap(this.comparator()));
    }

    @Override
    public <P> MutableSortedSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.selectWith(this.delegate, predicate, parameter, TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public <P> MutableSortedSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.rejectWith(this.delegate, predicate, parameter, TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return Iterate.collectWith(this.delegate, function, parameter, Lists.mutable.empty());
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        if (that instanceof Collection || that instanceof RichIterable)
        {
            int thatSize = Iterate.sizeOf(that);
            MutableList<Pair<T, S>> target = Lists.mutable.withInitialCapacity(Math.min(this.size(), thatSize));
            return Iterate.zip(this.delegate, that, target);
        }
        return Iterate.zip(this.delegate, that, Lists.mutable.empty());
    }

    @Override
    public MutableSortedSet<Pair<T, Integer>> zipWithIndex()
    {
        Comparator<? super T> comparator = this.comparator();
        if (comparator == null)
        {
            TreeSortedSet<Pair<T, Integer>> pairs = TreeSortedSet.newSet(Comparators.byFunction(Functions.firstOfPair(), Comparators.naturalOrder()));
            return Iterate.zipWithIndex(this.delegate, pairs);
        }
        return Iterate.zipWithIndex(this.delegate, TreeSortedSet.newSet(Comparators.byFirstOfPair(comparator)));
    }

    @Override
    public MutableSortedSet<T> distinct()
    {
        return SortedSets.mutable.withAll(this);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return OrderedIterate.corresponds(this, other, predicate);
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, this.size());

        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }

        Iterator<T> iterator = this.iterator();
        int i = 0;
        while (iterator.hasNext() && i <= toIndex)
        {
            T each = iterator.next();
            if (i >= fromIndex)
            {
                procedure.value(each);
            }
            i++;
        }
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, this.size());

        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }

        Iterator<T> iterator = this.iterator();
        int i = 0;
        while (iterator.hasNext() && i <= toIndex)
        {
            T each = iterator.next();
            if (i >= fromIndex)
            {
                objectIntProcedure.value(each, i);
            }
            i++;
        }
    }

    @Override
    public MutableSortedSet<T> union(SetIterable<? extends T> set)
    {
        return this.unionInto(set, TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.unionInto(this, set, targetSet);
    }

    @Override
    public MutableSortedSet<T> intersect(SetIterable<? extends T> set)
    {
        return this.intersectInto(set, TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.intersectInto(this, set, targetSet);
    }

    @Override
    public MutableSortedSet<T> difference(SetIterable<? extends T> subtrahendSet)
    {
        return this.differenceInto(subtrahendSet, TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
    {
        return SetIterables.differenceInto(this, subtrahendSet, targetSet);
    }

    @Override
    public MutableSortedSet<T> symmetricDifference(SetIterable<? extends T> setB)
    {
        return this.symmetricDifferenceInto(setB, TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.symmetricDifferenceInto(this, set, targetSet);
    }

    @Override
    public Comparator<? super T> comparator()
    {
        return this.delegate.comparator();
    }

    @Override
    public boolean isSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return SetIterables.isSubsetOf(this, candidateSuperset);
    }

    @Override
    public boolean isProperSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return SetIterables.isProperSubsetOf(this, candidateSuperset);
    }

    @Override
    public MutableSortedSet<SortedSetIterable<T>> powerSet()
    {
        return (MutableSortedSet<SortedSetIterable<T>>) (MutableSortedSet<?>) SortedSetIterables.powerSet(this);
    }

    @Override
    public Iterator<T> descendingIterator()
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".descendingIterator() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return navigable.descendingIterator();
    }

    @Override
    public MutableSortedSet<T> descendingSet()
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".descendingSet() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return SortedSetAdapter.adapt(navigable.descendingSet());
    }

    @Override
    public T lower(T e)
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".lower() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return navigable.lower(e);
    }

    @Override
    public T floor(T e)
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".floor() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return navigable.floor(e);
    }

    @Override
    public T ceiling(T e)
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".ceiling() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return navigable.ceiling(e);
    }

    @Override
    public T higher(T e)
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".higher() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return navigable.higher(e);
    }

    @Override
    public T pollFirst()
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".pollFirst() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return navigable.pollFirst();
    }

    @Override
    public T pollLast()
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".pollLast() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return navigable.pollLast();
    }

    @Override
    public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
    {
        return SetIterables.cartesianProduct(this, set);
    }

    @Override
    public MutableSortedSet<T> subSet(T fromElement, T toElement)
    {
        return SortedSetAdapter.adapt(this.delegate.subSet(fromElement, toElement));
    }

    @Override
    public MutableSortedSet<T> headSet(T toElement)
    {
        return SortedSetAdapter.adapt(this.delegate.headSet(toElement));
    }

    @Override
    public MutableSortedSet<T> tailSet(T fromElement)
    {
        return SortedSetAdapter.adapt(this.delegate.tailSet(fromElement));
    }

    @Override
    public MutableSortedSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive)
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".subSet() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return SortedSetAdapter.adapt(navigable.subSet(fromElement, fromInclusive, toElement, toInclusive));
    }

    @Override
    public MutableSortedSet<T> headSet(T toElement, boolean inclusive)
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".headSet() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return SortedSetAdapter.adapt(navigable.headSet(toElement, inclusive));
    }

    @Override
    public MutableSortedSet<T> tailSet(T fromElement, boolean inclusive)
    {
        if (!(this.delegate instanceof NavigableSet))
        {
            throw new UnsupportedOperationException(".tailSet() not supported on delegate " + this.delegate.getClass().getSimpleName());
        }
        NavigableSet<T> navigable = (NavigableSet<T>) this.delegate;
        return SortedSetAdapter.adapt(navigable.tailSet(fromElement, inclusive));
    }

    @Override
    public T first()
    {
        if (this.delegate.isEmpty())
        {
            throw new NoSuchElementException();
        }
        return this.delegate.first();
    }

    @Override
    public T last()
    {
        if (this.delegate.isEmpty())
        {
            throw new NoSuchElementException();
        }
        return this.delegate.last();
    }

    @Override
    public int indexOf(Object object)
    {
        if (this.delegate.contains(object))
        {
            return this.delegate.headSet((T) object).size();
        }
        return -1;
    }

    @Override
    public T getFirst()
    {
        return this.first();
    }

    @Override
    public T getLast()
    {
        return this.last();
    }

    @Override
    public int compareTo(SortedSetIterable<T> o)
    {
        return SortedSetIterables.compare(this, o);
    }

    @Override
    public ParallelSortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        if (executorService == null)
        {
            throw new NullPointerException();
        }
        if (batchSize < 1)
        {
            throw new IllegalArgumentException();
        }
        return new NonParallelSortedSetIterable<>(this);
    }

    @Override
    public MutableSortedSet<T> take(int count)
    {
        return IterableIterate.take(this.getDelegate(), Math.min(this.size(), count), TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public MutableSortedSet<T> drop(int count)
    {
        return IterableIterate.drop(this.getDelegate(), count, TreeSortedSet.newSet(this.comparator()));
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectLastIndex() not implemented yet");
    }
}
