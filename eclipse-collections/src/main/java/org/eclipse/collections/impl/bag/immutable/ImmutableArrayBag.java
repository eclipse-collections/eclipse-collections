/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * @since 1.0
 */
public class ImmutableArrayBag<T>
        extends AbstractImmutableBag<T>
        implements Serializable
{
    static final int MAXIMUM_USEFUL_ARRAY_BAG_SIZE = 20;

    private static final long serialVersionUID = 1L;

    private final T[] keys;
    private final int[] counts;

    ImmutableArrayBag(T[] keys, int[] counts)
    {
        this.keys = keys;
        this.counts = counts;

        if (this.keys.length != this.counts.length)
        {
            throw new IllegalArgumentException();
        }
    }

    public static <T> ImmutableArrayBag<T> newBagWith(T... elements)
    {
        return ImmutableArrayBag.copyFrom(Bags.mutable.with(elements));
    }

    public static <T> ImmutableArrayBag<T> copyFrom(Bag<? extends T> bag)
    {
        int distinctItemCount = bag.sizeDistinct();
        T[] newKeys = (T[]) new Object[distinctItemCount];
        int[] newCounts = new int[distinctItemCount];
        bag.forEachWithOccurrences(new ObjectIntProcedure<T>()
        {
            private int index;

            @Override
            public void value(T each, int count)
            {
                newKeys[this.index] = each;
                newCounts[this.index] = count;
                this.index++;
            }
        });
        return new ImmutableArrayBag<>(newKeys, newCounts);
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            objectIntProcedure.value(this.keys[i], this.counts[i]);
        }
    }

    @Override
    public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.shortCircuit(this.keys, this.counts, predicate, true, true, false);
    }

    @Override
    public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.shortCircuit(this.keys, this.counts, predicate, false, false, true);
    }

    @Override
    public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.shortCircuit(this.keys, this.counts, predicate, true, false, true);
    }

    @Override
    public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            T key = this.keys[i];
            if (predicate.accept(key, this.counts[i]))
            {
                return key;
            }
        }
        return null;
    }

    @Override
    public int sizeDistinct()
    {
        return this.keys.length;
    }

    @Override
    public int size()
    {
        int sum = 0;
        for (int value : this.counts)
        {
            sum += value;
        }
        return sum;
    }

    @Override
    public int occurrencesOf(Object item)
    {
        int index = ArrayIterate.detectIndexWith(this.keys, Predicates2.equal(), item);
        if (index > -1)
        {
            return this.counts[index];
        }
        return 0;
    }

    @Override
    public ImmutableBag<T> newWith(T element)
    {
        int elementIndex = ArrayIterate.detectIndexWith(this.keys, Predicates2.equal(), element);
        int distinctItemCount = this.sizeDistinct() + (elementIndex == -1 ? 1 : 0);
        if (distinctItemCount > MAXIMUM_USEFUL_ARRAY_BAG_SIZE)
        {
            return Bags.mutable.withAll(this).with(element).toImmutable();
        }
        return this.newArrayBagWith(element, elementIndex, distinctItemCount);
    }

    private ImmutableBag<T> newArrayBagWith(T element, int elementIndex, int distinctItemCount)
    {
        T[] newKeys = (T[]) new Object[distinctItemCount];
        int[] newCounts = new int[distinctItemCount];
        System.arraycopy(this.keys, 0, newKeys, 0, this.keys.length);
        System.arraycopy(this.counts, 0, newCounts, 0, this.counts.length);
        if (elementIndex == -1)
        {
            newKeys[distinctItemCount - 1] = element;
            newCounts[distinctItemCount - 1] = 1;
        }
        else
        {
            newCounts[elementIndex]++;
        }
        return new ImmutableArrayBag<>(newKeys, newCounts);
    }

    @Override
    public ImmutableBag<T> newWithout(T element)
    {
        int elementIndex = ArrayIterate.detectIndexWith(this.keys, Predicates2.equal(), element);
        if (elementIndex > -1)
        {
            int distinctItemCount = this.sizeDistinct() - (this.counts[elementIndex] == 1 ? 1 : 0);
            T[] newKeys = (T[]) new Object[distinctItemCount];
            int[] newCounts = new int[distinctItemCount];
            if (distinctItemCount == this.sizeDistinct())
            {
                System.arraycopy(this.keys, 0, newKeys, 0, distinctItemCount);
                System.arraycopy(this.counts, 0, newCounts, 0, distinctItemCount);
                newCounts[elementIndex]--;
            }
            else
            {
                System.arraycopy(this.keys, 0, newKeys, 0, elementIndex);
                System.arraycopy(this.counts, 0, newCounts, 0, elementIndex);
                System.arraycopy(this.keys, elementIndex + 1, newKeys, elementIndex, newKeys.length - elementIndex);
                System.arraycopy(this.counts, elementIndex + 1, newCounts, elementIndex, newCounts.length - elementIndex);
            }
            return new ImmutableArrayBag<>(newKeys, newCounts);
        }
        return this;
    }

    @Override
    public MutableMap<T, Integer> toMapOfItemToCount()
    {
        MutableMap<T, Integer> map = UnifiedMap.newMap(this.size());
        this.forEachWithOccurrences(map::put);
        return map;
    }

    @Override
    public ImmutableBag<T> newWithAll(Iterable<? extends T> elements)
    {
        return Bags.immutable.withAll(Iterate.addAllTo(elements, Bags.mutable.withAll(this)));
    }

    @Override
    public ImmutableBag<T> selectByOccurrences(IntPredicate predicate)
    {
        MutableBag<T> result = HashBag.newBag();
        this.forEachWithOccurrences((each, occurrences) -> {
            if (predicate.accept(occurrences))
            {
                result.addOccurrences(each, occurrences);
            }
        });
        return result.toImmutable();
    }

    @Override
    public <S> ImmutableBag<S> selectInstancesOf(Class<S> clazz)
    {
        MutableBag<S> result = HashBag.newBag();
        this.forEachWithOccurrences((each, index) -> {
            if (clazz.isInstance(each))
            {
                result.addOccurrences((S) each, index);
            }
        });
        return result.toImmutable();
    }

    @Override
    public <V> ImmutableBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, HashBagMultimap.<V, T>newMultimap()).toImmutable();
    }

    @Override
    public <V> ImmutableBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, HashBagMultimap.newMultimap()).toImmutable();
    }

    @Override
    public T getFirst()
    {
        return ArrayIterate.getFirst(this.keys);
    }

    @Override
    public T getLast()
    {
        return ArrayIterate.getLast(this.keys);
    }

    @Override
    public T getOnly()
    {
        if (this.counts.length == 0)
        {
            throw new IllegalStateException("Size must be 1 but was 0");
        }

        if (this.counts.length > 1 || this.counts[0] > 1)
        {
            throw new IllegalStateException("Size must be 1 but was greater than 1");
        }

        return this.getFirst();
    }

    @Override
    public ImmutableBag<T> select(Predicate<? super T> predicate)
    {
        return this.select(predicate, HashBag.newBag()).toImmutable();
    }

    @Override
    public ImmutableBag<T> reject(Predicate<? super T> predicate)
    {
        return this.reject(predicate, HashBag.newBag()).toImmutable();
    }

    @Override
    public <V> ImmutableBag<V> collect(Function<? super T, ? extends V> function)
    {
        MutableBag<V> result = this.collect(function, HashBag.newBag());
        return result.toImmutable();
    }

    @Override
    public <V> ImmutableBag<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        MutableBag<V> result = this.collectIf(predicate, function, HashBag.newBag());
        return result.toImmutable();
    }

    @Override
    public <V> ImmutableBag<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.flatCollect(function, HashBag.newBag()).toImmutable();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof Bag))
        {
            return false;
        }
        Bag<?> bag = (Bag<?>) other;
        if (this.size() != bag.size())
        {
            return false;
        }
        for (int i = 0; i < this.keys.length; i++)
        {
            if (this.counts[i] != bag.occurrencesOf(this.keys[i]))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int sum = 0;
        for (int i = 0; i < this.keys.length; i++)
        {
            T each = this.keys[i];
            sum += (each == null ? 0 : each.hashCode()) ^ this.counts[i];
        }
        return sum;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            T key = this.keys[i];
            for (int j = 1; j <= this.counts[i]; j++)
            {
                procedure.value(key);
            }
        }
    }

    @Override
    public Iterator<T> iterator()
    {
        return new ArrayBagIterator();
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return ArrayIterate.anySatisfy(this.keys, predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ArrayIterate.anySatisfyWith(this.keys, predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return ArrayIterate.allSatisfy(this.keys, predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ArrayIterate.allSatisfyWith(this.keys, predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return ArrayIterate.noneSatisfy(this.keys, predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ArrayIterate.noneSatisfyWith(this.keys, predicate, parameter);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return ArrayIterate.detect(this.keys, predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ArrayIterate.detectWith(this.keys, predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return ArrayIterate.detectOptional(this.keys, predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ArrayIterate.detectWithOptional(this.keys, predicate, parameter);
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return ArrayIterate.min(this.keys, comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return ArrayIterate.max(this.keys, comparator);
    }

    @Override
    public T min()
    {
        return ArrayIterate.min(this.keys);
    }

    @Override
    public T max()
    {
        return ArrayIterate.max(this.keys);
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return ArrayIterate.minBy(this.keys, function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return ArrayIterate.maxBy(this.keys, function);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> ImmutableBag<Pair<T, S>> zip(Iterable<S> that)
    {
        if (that instanceof Collection || that instanceof RichIterable)
        {
            int thatSize = Iterate.sizeOf(that);
            MutableBag<Pair<T, S>> target = HashBag.newBag(Math.min(this.size(), thatSize));
            return this.zip(that, target).toImmutable();
        }
        return this.zip(that, HashBag.newBag()).toImmutable();
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public ImmutableSet<Pair<T, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(Sets.mutable.withInitialCapacity(this.size())).toImmutable();
    }

    protected Object writeReplace()
    {
        return new ImmutableBagSerializationProxy<>(this);
    }

    @Override
    public RichIterable<T> distinctView()
    {
        return ArrayAdapter.adapt(this.keys).asUnmodifiable();
    }

    private final class ArrayBagIterator
            implements Iterator<T>
    {
        private int position;
        private int remainingOccurrences = -1;

        private ArrayBagIterator()
        {
            this.remainingOccurrences = ImmutableArrayBag.this.sizeDistinct() > 0
                    ? ImmutableArrayBag.this.counts[0]
                    : 0;
        }

        @Override
        public boolean hasNext()
        {
            return this.position != ImmutableArrayBag.this.keys.length
                    && !(this.position == ImmutableArrayBag.this.keys.length - 1 && this.remainingOccurrences == 0);
        }

        @Override
        public T next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }

            T result = ImmutableArrayBag.this.keys[this.position];

            this.remainingOccurrences--;
            if (this.remainingOccurrences == 0)
            {
                this.position++;
                if (this.position != ImmutableArrayBag.this.keys.length)
                {
                    this.remainingOccurrences = ImmutableArrayBag.this.counts[this.position];
                }
            }
            return result;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Cannot remove from an ImmutableArrayBag");
        }
    }
}
