/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.ParallelUnsortedSetIterable;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.UnsortedSetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.block.procedure.PartitionPredicate2Procedure;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.collection.mutable.AbstractCollectionAdapter;
import org.eclipse.collections.impl.lazy.parallel.set.NonParallelUnsortedSetIterable;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.partition.set.PartitionUnifiedSet;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.internal.SetIterables;
import org.eclipse.collections.impl.utility.internal.SetIterate;

/**
 * This class provides a MutableSet wrapper around a JDK Collections Set interface instance. All the MutableSet
 * interface methods are supported in addition to the JDK Set interface methods.
 * <p>
 * To create a new wrapper around an existing Set instance, use the {@link #adapt(Set)} factory method.
 */
public class SetAdapter<T>
        extends AbstractCollectionAdapter<T>
        implements Serializable, MutableSet<T>
{
    private static final long serialVersionUID = 1L;
    private final Set<T> delegate;

    protected SetAdapter(Set<T> newDelegate)
    {
        if (newDelegate == null)
        {
            throw new NullPointerException("SetAdapter may not wrap null");
        }
        this.delegate = newDelegate;
    }

    @Override
    protected Set<T> getDelegate()
    {
        return this.delegate;
    }

    @Override
    public MutableSet<T> asUnmodifiable()
    {
        return UnmodifiableMutableSet.of(this);
    }

    @Override
    public MutableSet<T> asSynchronized()
    {
        return SynchronizedMutableSet.of(this);
    }

    @Override
    public ImmutableSet<T> toImmutable()
    {
        return Sets.immutable.withAll(this.delegate);
    }

    public static <E> MutableSet<E> adapt(Set<E> set)
    {
        if (set instanceof MutableSet)
        {
            return (MutableSet<E>) set;
        }
        return new SetAdapter<>(set);
    }

    @Override
    public MutableSet<T> clone()
    {
        return Sets.mutable.withAll(this.delegate);
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
    public SetAdapter<T> with(T element)
    {
        this.add(element);
        return this;
    }

    public SetAdapter<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public SetAdapter<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public SetAdapter<T> with(T... elements)
    {
        ArrayIterate.forEach(elements, CollectionAddProcedure.on(this.delegate));
        return this;
    }

    @Override
    public SetAdapter<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public SetAdapter<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    public SetAdapter<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    /**
     * @deprecated use {@link UnifiedSet#newSet()} instead (inlineable)
     */
    @Override
    @Deprecated
    public MutableSet<T> newEmpty()
    {
        return Sets.mutable.empty();
    }

    @Override
    public MutableSet<T> tap(Procedure<? super T> procedure)
    {
        Iterate.forEach(this.delegate, procedure);
        return this;
    }

    @Override
    public MutableSet<T> select(Predicate<? super T> predicate)
    {
        return Iterate.select(this.delegate, predicate, Sets.mutable.empty());
    }

    @Override
    public MutableSet<T> reject(Predicate<? super T> predicate)
    {
        return Iterate.reject(this.delegate, predicate, Sets.mutable.empty());
    }

    @Override
    public PartitionMutableSet<T> partition(Predicate<? super T> predicate)
    {
        PartitionMutableSet<T> partitionMutableSet = new PartitionUnifiedSet<>();
        this.forEach(new PartitionProcedure<>(predicate, partitionMutableSet));
        return partitionMutableSet;
    }

    @Override
    public <P> PartitionMutableSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionMutableSet<T> partitionMutableSet = new PartitionUnifiedSet<>();
        this.forEach(new PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableSet));
        return partitionMutableSet;
    }

    @Override
    public <S> MutableSet<S> selectInstancesOf(Class<S> clazz)
    {
        MutableSet<S> result = Sets.mutable.empty();
        this.forEach(new SelectInstancesOfProcedure<>(clazz, result));
        return result;
    }

    @Override
    public <V> MutableSet<V> collect(Function<? super T, ? extends V> function)
    {
        return Iterate.collect(this.delegate, function, Sets.mutable.empty());
    }

    @Override
    public <V> MutableSet<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return Iterate.flatCollect(this.delegate, function, Sets.mutable.empty());
    }

    @Override
    public <V> MutableSet<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return Iterate.collectIf(this.delegate, predicate, function, Sets.mutable.empty());
    }

    @Override
    public <V> UnifiedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return Iterate.groupBy(this.delegate, function, UnifiedSetMultimap.newMultimap());
    }

    @Override
    public <V> UnifiedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return Iterate.groupByEach(this.delegate, function, UnifiedSetMultimap.newMultimap());
    }

    @Override
    public <P> MutableSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.selectWith(this.delegate, predicate, parameter, Sets.mutable.empty());
    }

    @Override
    public <P> MutableSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.rejectWith(this.delegate, predicate, parameter, Sets.mutable.empty());
    }

    @Override
    public <P, V> MutableSet<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return Iterate.collectWith(this.delegate, function, parameter, Sets.mutable.empty());
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Deprecated
    @Override
    public <S> MutableSet<Pair<T, S>> zip(Iterable<S> that)
    {
        if (that instanceof Collection || that instanceof RichIterable)
        {
            int thatSize = Iterate.sizeOf(that);
            MutableSet<Pair<T, S>> target = UnifiedSet.newSet(Math.min(this.size(), thatSize));
            return Iterate.zip(this, that, target);
        }
        return Iterate.zip(this, that, Sets.mutable.empty());
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Deprecated
    @Override
    public MutableSet<Pair<T, Integer>> zipWithIndex()
    {
        return Iterate.zipWithIndex(this, Sets.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        return SetIterate.removeAllIterable(this, iterable);
    }

    @Override
    public MutableSet<T> union(SetIterable<? extends T> set)
    {
        return SetIterables.union(this, set);
    }

    @Override
    public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.unionInto(this, set, targetSet);
    }

    @Override
    public MutableSet<T> intersect(SetIterable<? extends T> set)
    {
        return SetIterables.intersect(this, set);
    }

    @Override
    public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.intersectInto(this, set, targetSet);
    }

    @Override
    public MutableSet<T> difference(SetIterable<? extends T> subtrahendSet)
    {
        return SetIterables.difference(this, subtrahendSet);
    }

    @Override
    public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
    {
        return SetIterables.differenceInto(this, subtrahendSet, targetSet);
    }

    @Override
    public MutableSet<T> symmetricDifference(SetIterable<? extends T> setB)
    {
        return SetIterables.symmetricDifference(this, setB);
    }

    @Override
    public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.symmetricDifferenceInto(this, set, targetSet);
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
    public MutableSet<UnsortedSetIterable<T>> powerSet()
    {
        return (MutableSet<UnsortedSetIterable<T>>) (MutableSet<?>) SetIterables.powerSet(this);
    }

    @Override
    public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
    {
        return SetIterables.cartesianProduct(this, set);
    }

    @Override
    public ParallelUnsortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new NonParallelUnsortedSetIterable<>(this);
    }
}
