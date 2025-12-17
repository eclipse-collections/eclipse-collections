/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.sorted.immutable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.RandomAccess;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bag.sorted.SortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.SortedBags;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.sorted.PartitionImmutableSortedBag;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.Counter;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.partition.bag.sorted.PartitionImmutableSortedBagImpl;
import org.eclipse.collections.impl.partition.bag.sorted.PartitionTreeBag;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.eclipse.collections.impl.utility.internal.SortedBagIterables;

class ImmutableSortedBagImpl<T>
        extends AbstractImmutableSortedBag<T>
        implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final T[] elements;
    private final int[] occurrences;
    private final Comparator<? super T> comparator;
    private final int size;

    ImmutableSortedBagImpl(SortedBag<T> sortedBag)
    {
        if (sortedBag.isEmpty())
        {
            throw new IllegalArgumentException();
        }
        this.comparator = sortedBag.comparator();
        this.elements = (T[]) new Object[sortedBag.sizeDistinct()];
        this.occurrences = new int[sortedBag.sizeDistinct()];
        this.size = sortedBag.size();

        sortedBag.forEachWithOccurrences(new ObjectIntProcedure<T>()
        {
            private int i;

            @Override
            public void value(T each, int occurrencesOfEach)
            {
                ImmutableSortedBagImpl.this.elements[this.i] = each;
                ImmutableSortedBagImpl.this.occurrences[this.i] = occurrencesOfEach;
                this.i++;
            }
        });
    }

    private ImmutableSortedBagImpl(T[] elements, int[] occurrences, Comparator<? super T> comparator)
    {
        if (elements.length != occurrences.length)
        {
            throw new IllegalArgumentException();
        }

        this.comparator = comparator;
        this.elements = elements;
        this.occurrences = occurrences;

        int size = 0;
        for (int occurrence : occurrences)
        {
            size += occurrence;
        }

        this.size = size;
    }

    @Override
    public ImmutableSortedBag<T> newWith(T element)
    {
        int index = Arrays.binarySearch(this.elements, element, this.comparator);

        if (index >= 0)
        {
            int[] occurrences = this.occurrences.clone();
            occurrences[index] += 1;
            return new ImmutableSortedBagImpl<>(this.elements.clone(), occurrences, this.comparator);
        }

        int insertionPoint = (index + 1) * -1;

        T[] elements = (T[]) new Object[this.elements.length + 1];
        int[] occurrences = new int[this.occurrences.length + 1];

        System.arraycopy(this.elements, 0, elements, 0, insertionPoint);
        System.arraycopy(this.occurrences, 0, occurrences, 0, insertionPoint);

        elements[insertionPoint] = element;
        occurrences[insertionPoint] = 1;

        System.arraycopy(this.elements, insertionPoint, elements, insertionPoint + 1, this.elements.length - insertionPoint);
        System.arraycopy(this.occurrences, insertionPoint, occurrences, insertionPoint + 1, this.occurrences.length - insertionPoint);

        return new ImmutableSortedBagImpl<>(elements, occurrences, this.comparator);
    }

    @Override
    public ImmutableSortedBag<T> newWithout(T element)
    {
        int index = Arrays.binarySearch(this.elements, element, this.comparator);

        if (index < 0)
        {
            return this;
        }

        if (this.occurrences[index] > 1)
        {
            int[] occurrences = this.occurrences.clone();
            occurrences[index] -= 1;
            return new ImmutableSortedBagImpl<>(this.elements.clone(), occurrences, this.comparator);
        }

        T[] elements = (T[]) new Object[this.elements.length - 1];
        int[] occurrences = new int[this.occurrences.length - 1];

        System.arraycopy(this.elements, 0, elements, 0, index);
        System.arraycopy(this.occurrences, 0, occurrences, 0, index);

        System.arraycopy(this.elements, index + 1, elements, index, elements.length - index);
        System.arraycopy(this.occurrences, index + 1, occurrences, index, occurrences.length - index);

        return new ImmutableSortedBagImpl<>(elements, occurrences, this.comparator);
    }

    @Override
    public ImmutableSortedBag<T> newWithAll(Iterable<? extends T> elements)
    {
        MutableSortedBag<T> result = SortedBags.mutable.withAll(this);
        result.addAllIterable(elements);
        return result.toImmutable();
    }

    @Override
    public Comparator<? super T> comparator()
    {
        return this.comparator;
    }

    @Override
    public T min()
    {
        return ArrayIterate.min(this.elements);
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return ArrayIterate.min(this.elements, comparator);
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return ArrayIterate.minBy(this.elements, function);
    }

    @Override
    public T max()
    {
        return ArrayIterate.max(this.elements);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return ArrayIterate.max(this.elements, comparator);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return ArrayIterate.maxBy(this.elements, function);
    }

    @Override
    public ImmutableSortedBag<T> takeWhile(Predicate<? super T> predicate)
    {
        MutableSortedBag<T> bag = SortedBags.mutable.empty(this.comparator);
        for (int i = 0; i < this.elements.length; i++)
        {
            if (predicate.accept(this.elements[i]))
            {
                bag.addOccurrences(this.elements[i], this.occurrences[i]);
            }
            else
            {
                return bag.toImmutable();
            }
        }
        return bag.toImmutable();
    }

    @Override
    public ImmutableSortedBag<T> dropWhile(Predicate<? super T> predicate)
    {
        MutableSortedBag<T> bag = SortedBags.mutable.empty(this.comparator);
        int startIndex = this.detectNotIndex(predicate);
        for (int i = startIndex; i < this.elements.length; i++)
        {
            bag.addOccurrences(this.elements[i], this.occurrences[i]);
        }
        return bag.toImmutable();
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        int result = 0;
        for (int i = 0; i < this.elements.length; i++)
        {
            if (predicate.accept(this.elements[i]))
            {
                return result;
            }
            result += this.occurrences[i];
        }
        return -1;
    }

    private int detectNotIndex(Predicate<? super T> predicate)
    {
        for (int index = 0; index < this.elements.length; index++)
        {
            if (!predicate.accept(this.elements[index]))
            {
                return index;
            }
        }
        return this.elements.length;
    }

    @Override
    public PartitionImmutableSortedBag<T> partitionWhile(Predicate<? super T> predicate)
    {
        PartitionTreeBag<T> result = new PartitionTreeBag<>(this.comparator());
        MutableSortedBag<T> selected = result.getSelected();
        MutableSortedBag<T> rejected = result.getRejected();

        int partitionIndex = this.detectNotIndex(predicate);
        for (int i = 0; i < partitionIndex; i++)
        {
            selected.addOccurrences(this.elements[i], this.occurrences[i]);
        }
        for (int j = partitionIndex; j < this.elements.length; j++)
        {
            rejected.addOccurrences(this.elements[j], this.occurrences[j]);
        }
        return new PartitionImmutableSortedBagImpl<>(result);
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> procedure)
    {
        for (int i = 0; i < this.occurrences.length; i++)
        {
            procedure.value(this.elements[i], this.occurrences[i]);
        }
    }

    @Override
    public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.shortCircuit(this.elements, this.occurrences, predicate, true, true, false);
    }

    @Override
    public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.shortCircuit(this.elements, this.occurrences, predicate, false, false, true);
    }

    @Override
    public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.shortCircuit(this.elements, this.occurrences, predicate, true, false, true);
    }

    @Override
    public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        for (int i = 0; i < this.elements.length; i++)
        {
            T key = this.elements[i];
            if (predicate.accept(key, this.occurrences[i]))
            {
                return key;
            }
        }
        return null;
    }

    @Override
    public int sizeDistinct()
    {
        return this.elements.length;
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public int indexOf(Object object)
    {
        int result = 0;
        for (int i = 0; i < this.elements.length; i++)
        {
            if (object.equals(this.elements[i]))
            {
                return result;
            }
            result += this.occurrences[i];
        }
        return -1;
    }

    @Override
    public T getFirst()
    {
        return ArrayIterate.getFirst(this.elements);
    }

    @Override
    public T getLast()
    {
        return ArrayIterate.getLast(this.elements);
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        int index = fromIndex;
        ListIterate.rangeCheck(index, toIndex, this.size());

        if (index > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }
        int i = 0;
        int beginningIndex = 0;

        while (beginningIndex <= index)
        {
            beginningIndex += this.occurrences[i];
            if (beginningIndex <= index)
            {
                i++;
            }
        }
        int numberOfIterations = beginningIndex - index;

        for (int j = 0; j < numberOfIterations && index <= toIndex; j++)
        {
            procedure.value(this.elements[i]);
            index++;
        }

        while (index <= toIndex)
        {
            i++;
            for (int j = 0; j < this.occurrences[i] && index <= toIndex; j++)
            {
                procedure.value(this.elements[i]);
                index++;
            }
        }
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        for (int i = 0; i < this.elements.length; i++)
        {
            T element = this.elements[i];
            int occurrences = this.occurrences[i];
            for (int j = 0; j < occurrences; j++)
            {
                procedure.value(element);
            }
        }
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        int index = 0;
        for (int i = 0; i < this.elements.length; i++)
        {
            for (int j = 0; j < this.occurrences[i]; j++)
            {
                objectIntProcedure.value(this.elements[i], index);
                index++;
            }
        }
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> ImmutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.collectWithIndex(function, FastList.<V>newList(this.size())).toImmutable();
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        int index = 0;
        for (int i = 0; i < this.elements.length; i++)
        {
            for (int j = 0; j < this.occurrences[i]; j++)
            {
                target.add(function.valueOf(this.elements[i], index));
                index++;
            }
        }
        return target;
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, this.size());

        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }

        int arrayIndex = 0;
        int index = fromIndex;
        int i = 0;
        while (i <= index)
        {
            i += this.occurrences[arrayIndex];
            if (i <= index)
            {
                arrayIndex++;
            }
        }
        int numberOfIterations = i - index;

        for (int j = 0; j < numberOfIterations && index <= toIndex; j++)
        {
            objectIntProcedure.value(this.elements[arrayIndex], index);
            index++;
        }

        while (index <= toIndex)
        {
            arrayIndex++;
            for (int j = 0; j < this.occurrences[arrayIndex] && index <= toIndex; j++)
            {
                objectIntProcedure.value(this.elements[arrayIndex], index);
                index++;
            }
        }
    }

    @Override
    public int occurrencesOf(Object item)
    {
        int index = Arrays.binarySearch(this.elements, (T) item, this.comparator);
        if (index > -1)
        {
            return this.occurrences[index];
        }
        return 0;
    }

    @Override
    public ImmutableSortedSet<T> distinct()
    {
        return SortedSets.immutable.with(this.comparator(), this.elements);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        if (this.size != other.size())
        {
            return false;
        }

        if (other instanceof RandomAccess)
        {
            List<S> otherList = (List<S>) other;
            int otherListIndex = 0;
            for (int i = 0; i < this.elements.length; i++)
            {
                for (int j = 0; j < this.occurrences[i]; j++)
                {
                    if (!predicate.accept(this.elements[i], otherList.get(otherListIndex)))
                    {
                        return false;
                    }
                    otherListIndex++;
                }
            }
            return true;
        }

        Iterator<S> otherIterator = other.iterator();
        for (int i = 0; i < this.elements.length; i++)
        {
            for (int j = 0; j < this.occurrences[i]; j++)
            {
                if (!predicate.accept(this.elements[i], otherIterator.next()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public MutableList<T> toSortedList()
    {
        return this.toSortedList(Comparators.naturalOrder());
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        return SortedSets.mutable.with(this.elements);
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        return SortedSets.mutable.with(comparator, this.elements);
    }

    @Override
    public Object[] toArray()
    {
        Object[] result = new Object[this.size()];
        this.each(new Procedure<T>()
        {
            private int i;

            @Override
            public void value(T each)
            {
                result[this.i] = each;
                this.i++;
            }
        });

        return result;
    }

    @Override
    public <E> E[] toArray(E[] array)
    {
        E[] array1 = array;
        if (array1.length < this.size)
        {
            array1 = (E[]) Array.newInstance(array1.getClass().getComponentType(), this.size);
        }
        T[] items = (T[]) this.toArray();
        System.arraycopy(items, 0, array1, 0, this.size);
        if (array1.length > this.size)
        {
            array1[this.size] = null;
        }
        return array1;
    }

    @Override
    public MutableSortedMap<T, Integer> toMapOfItemToCount()
    {
        MutableSortedMap<T, Integer> map = TreeSortedMap.newMap(this.comparator());
        this.forEachWithOccurrences(map::put);
        return map;
    }

    @Override
    public int compareTo(SortedBag<T> otherBag)
    {
        return SortedBagIterables.compare(this, otherBag);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return ArrayIterate.allSatisfy(this.elements, predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ArrayIterate.allSatisfyWith(this.elements, predicate, parameter);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return ArrayIterate.anySatisfy(this.elements, predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ArrayIterate.anySatisfyWith(this.elements, predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return ArrayIterate.noneSatisfy(this.elements, predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ArrayIterate.noneSatisfyWith(this.elements, predicate, parameter);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return ArrayIterate.detect(this.elements, predicate);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return ArrayIterate.detectOptional(this.elements, predicate);
    }

    @Override
    public boolean contains(Object object)
    {
        return Arrays.binarySearch(this.elements, (T) object, this.comparator) >= 0;
    }

    @Override
    public Iterator<T> iterator()
    {
        return new InternalIterator();
    }

    @Override
    public ImmutableSortedBag<T> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (count == 0)
        {
            return SortedBags.immutable.empty(this.comparator());
        }
        if (count >= this.size())
        {
            return this;
        }

        MutableSortedBag<T> output = SortedBags.mutable.empty(this.comparator());
        int index = 0;
        for (int i = 0; i < this.elements.length; i++)
        {
            for (int j = 0; j < this.occurrences[i]; j++)
            {
                output.add(this.elements[i]);
                index++;
                if (index >= count)
                {
                    return output.toImmutable();
                }
            }
        }

        throw new AssertionError();
    }

    @Override
    public ImmutableSortedBag<T> drop(int count)
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
            return SortedBags.immutable.empty(this.comparator());
        }

        MutableSortedBag<T> output = SortedBags.mutable.empty(this.comparator());
        int index = 0;
        for (int i = 0; i < this.elements.length; i++)
        {
            for (int j = 0; j < this.occurrences[i]; j++)
            {
                if (index >= count)
                {
                    output.add(this.elements[i]);
                }
                index++;
            }
        }

        return output.toImmutable();
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
        if (this.sizeDistinct() != bag.sizeDistinct())
        {
            return false;
        }

        for (int i = 0; i < this.elements.length; i++)
        {
            if (bag.occurrencesOf(this.elements[i]) != this.occurrences[i])
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        Counter counter = new Counter();
        this.forEachWithOccurrences((each, count) -> counter.add((each == null ? 0 : each.hashCode()) ^ count));
        return counter.getCount();
    }

    @Override
    public RichIterable<T> distinctView()
    {
        // TODO use an implementation taking advantage of the sorting of the array
        return ArrayAdapter.adapt(this.elements).asUnmodifiable();
    }

    private class InternalIterator implements Iterator<T>
    {
        private int position;
        private int occurrencesRemaining = ImmutableSortedBagImpl.this.isEmpty()
                ? 0
                : ImmutableSortedBagImpl.this.occurrences[0];

        @Override
        public boolean hasNext()
        {
            return this.position < ImmutableSortedBagImpl.this.elements.length - 1 || this.occurrencesRemaining != 0;
        }

        @Override
        public T next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            if (this.occurrencesRemaining == 0)
            {
                this.position++;
                this.occurrencesRemaining = ImmutableSortedBagImpl.this.occurrences[this.position];
            }
            this.occurrencesRemaining--;
            return ImmutableSortedBagImpl.this.elements[this.position];
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
        }
    }
}
