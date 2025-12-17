/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.sorted.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bag.sorted.SortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.Counter;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure2;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.multimap.bag.sorted.mutable.TreeBagMultimap;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.eclipse.collections.impl.utility.OrderedIterate;
import org.eclipse.collections.impl.utility.internal.IterableIterate;
import org.eclipse.collections.impl.utility.internal.SortedBagIterables;

/**
 * A TreeBag is a MutableSortedBag which uses a SortedMap as its underlying data store. Each key in the SortedMap represents some item,
 * and the value in the map represents the current number of occurrences of that item.
 *
 * @since 4.2
 */
public class TreeBag<T>
        extends AbstractMutableSortedBag<T>
        implements Externalizable
{
    private static final long serialVersionUID = 1L;
    private MutableSortedMap<T, Counter> items;
    private int size;

    public TreeBag()
    {
        this.items = SortedMaps.mutable.empty();
    }

    private TreeBag(MutableSortedMap<T, Counter> map)
    {
        this.items = map;
        this.size = (int) map.valuesView().sumOfInt(Counter.TO_COUNT);
    }

    public TreeBag(Comparator<? super T> comparator)
    {
        this.items = TreeSortedMap.newMap(comparator);
    }

    public TreeBag(SortedBag<T> sortedBag)
    {
        this(sortedBag.comparator(), sortedBag);
    }

    public TreeBag(Comparator<? super T> comparator, Iterable<? extends T> iterable)
    {
        this(comparator);
        this.addAllIterable(iterable);
    }

    public static <E> TreeBag<E> newBag()
    {
        return new TreeBag<>();
    }

    public static <E> TreeBag<E> newBag(Comparator<? super E> comparator)
    {
        return new TreeBag<>(comparator);
    }

    public static <E> TreeBag<E> newBag(Iterable<? extends E> source)
    {
        if (source instanceof SortedBag<?>)
        {
            return new TreeBag<>((SortedBag<E>) source);
        }
        return Iterate.addAllTo(source, TreeBag.newBag());
    }

    public static <E> TreeBag<E> newBag(Comparator<? super E> comparator, Iterable<? extends E> iterable)
    {
        return new TreeBag<>(comparator, iterable);
    }

    public static <E> TreeBag<E> newBagWith(E... elements)
    {
        //noinspection SSBasedInspection
        return TreeBag.newBag(Arrays.asList(elements));
    }

    public static <E> TreeBag<E> newBagWith(Comparator<? super E> comparator, E... elements)
    {
        //noinspection SSBasedInspection
        return TreeBag.newBag(comparator, Arrays.asList(elements));
    }

    @Override
    public TreeBag<T> clone()
    {
        return new TreeBag<>(this);
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

        return this.items.keyValuesView().allSatisfy(each -> bag.occurrencesOf(each.getOne()) == each.getTwo().getCount());
    }

    @Override
    public int hashCode()
    {
        Counter counter = new Counter();
        this.forEachWithOccurrences((each, count) -> counter.add((each == null ? 0 : each.hashCode()) ^ count));
        return counter.getCount();
    }

    @Override
    protected RichIterable<T> getKeysView()
    {
        return this.items.keysView();
    }

    @Override
    public int sizeDistinct()
    {
        return this.items.size();
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> procedure)
    {
        this.items.forEachKeyValue((item, count) -> procedure.value(item, count.getCount()));
    }

    @Override
    public MutableSortedBag<T> selectByOccurrences(IntPredicate predicate)
    {
        MutableSortedMap<T, Counter> map =
                this.items.select((each, occurrences) -> predicate.accept(occurrences.getCount()));
        return new TreeBag<>(map);
    }

    @Override
    public int occurrencesOf(Object item)
    {
        Counter counter = this.items.get(item);
        return counter == null ? 0 : counter.getCount();
    }

    @Override
    public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.items.keyValuesView().anySatisfy(each -> predicate.accept(each.getOne(), each.getTwo().getCount()));
    }

    @Override
    public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.items.keyValuesView().allSatisfy(each -> predicate.accept(each.getOne(), each.getTwo().getCount()));
    }

    @Override
    public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.items.keyValuesView().noneSatisfy(each -> predicate.accept(each.getOne(), each.getTwo().getCount()));
    }

    @Override
    public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        Pair<T, Counter> pair = this.items.keyValuesView().detect(each -> predicate.accept(each.getOne(), each.getTwo().getCount()));
        return pair == null ? null : pair.getOne();
    }

    @Override
    public boolean isEmpty()
    {
        return this.items.isEmpty();
    }

    @Override
    public boolean remove(Object item)
    {
        Counter counter = this.items.get(item);
        if (counter != null)
        {
            if (counter.getCount() > 1)
            {
                counter.decrement();
            }
            else
            {
                this.items.remove(item);
            }
            this.size--;
            return true;
        }
        return false;
    }

    @Override
    public void clear()
    {
        this.items.clear();
        this.size = 0;
    }

    @Override
    public boolean contains(Object o)
    {
        return this.items.containsKey(o);
    }

    @Override
    public int compareTo(SortedBag<T> otherBag)
    {
        return SortedBagIterables.compare(this, otherBag);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.comparator());
        out.writeInt(this.items.size());
        try
        {
            this.items.forEachKeyValue(new CheckedProcedure2<T, Counter>()
            {
                @Override
                public void safeValue(T object, Counter parameter) throws Exception
                {
                    out.writeObject(object);
                    out.writeInt(parameter.getCount());
                }
            });
        }
        catch (RuntimeException e)
        {
            if (e.getCause() instanceof IOException)
            {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.items = new TreeSortedMap<>((Comparator<T>) in.readObject());
        int size = in.readInt();
        for (int i = 0; i < size; i++)
        {
            this.addOccurrences((T) in.readObject(), in.readInt());
        }
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.items.forEachKeyValue((key, value) -> {
            for (int i = 0; i < value.getCount(); i++)
            {
                procedure.value(key);
            }
        });
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        Counter index = new Counter();
        this.items.forEachKeyValue((key, value) -> {
            for (int i = 0; i < value.getCount(); i++)
            {
                objectIntProcedure.value(key, index.getCount());
                index.increment();
            }
        });
    }

    @Override
    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, this.size);
        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }

        Iterator<Map.Entry<T, Counter>> iterator = this.items.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext() && i < fromIndex)
        {
            Map.Entry<T, Counter> entry = iterator.next();
            Counter value = entry.getValue();
            int count = value.getCount();
            if (i + count < fromIndex)
            {
                i += count;
            }
            else
            {
                for (int j = 0; j < count; j++)
                {
                    if (i >= fromIndex && i <= toIndex)
                    {
                        procedure.value(entry.getKey());
                    }
                    i++;
                }
            }
        }
        while (iterator.hasNext() && i <= toIndex)
        {
            Map.Entry<T, Counter> entry = iterator.next();
            Counter value = entry.getValue();
            int count = value.getCount();

            for (int j = 0; j < count; j++)
            {
                if (i <= toIndex)
                {
                    procedure.value(entry.getKey());
                }
                i++;
            }
        }
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, this.size);
        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }

        Iterator<Map.Entry<T, Counter>> iterator = this.items.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext() && i < fromIndex)
        {
            Map.Entry<T, Counter> entry = iterator.next();
            Counter value = entry.getValue();
            int count = value.getCount();
            if (i + count < fromIndex)
            {
                i += count;
            }
            else
            {
                for (int j = 0; j < count; j++)
                {
                    if (i >= fromIndex && i <= toIndex)
                    {
                        objectIntProcedure.value(entry.getKey(), i);
                    }
                    i++;
                }
            }
        }
        while (iterator.hasNext() && i <= toIndex)
        {
            Map.Entry<T, Counter> entry = iterator.next();
            Counter value = entry.getValue();
            int count = value.getCount();

            for (int j = 0; j < count; j++)
            {
                if (i <= toIndex)
                {
                    objectIntProcedure.value(entry.getKey(), i);
                }
                i++;
            }
        }
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.items.forEachKeyValue((key, value) -> {
            for (int i = 0; i < value.getCount(); i++)
            {
                procedure.value(key, parameter);
            }
        });
    }

    @Override
    public Iterator<T> iterator()
    {
        return new InternalIterator();
    }

    @Override
    public int addOccurrences(T item, int occurrences)
    {
        if (occurrences < 0)
        {
            throw new IllegalArgumentException("Cannot add a negative number of occurrences");
        }
        if (occurrences > 0)
        {
            Counter counter = this.items.getIfAbsentPut(item, Counter::new);
            counter.add(occurrences);
            this.size += occurrences;
            return counter.getCount();
        }
        return this.occurrencesOf(item);
    }

    @Override
    public boolean removeOccurrences(Object item, int occurrences)
    {
        if (occurrences < 0)
        {
            throw new IllegalArgumentException("Cannot remove a negative number of occurrences");
        }

        if (occurrences == 0)
        {
            return false;
        }

        Counter counter = this.items.get(item);
        if (counter == null)
        {
            return false;
        }
        int startCount = counter.getCount();

        if (occurrences >= startCount)
        {
            this.items.remove(item);
            this.size -= startCount;
            return true;
        }

        counter.add(occurrences * -1);
        this.size -= occurrences;
        return true;
    }

    @Override
    public boolean setOccurrences(T item, int occurrences)
    {
        if (occurrences < 0)
        {
            throw new IllegalArgumentException("Cannot set a negative number of occurrences");
        }

        int originalOccurrences = this.occurrencesOf(item);

        if (originalOccurrences == occurrences)
        {
            return false;
        }

        if (occurrences == 0)
        {
            this.items.remove(item);
        }
        else
        {
            this.items.put(item, new Counter(occurrences));
        }

        this.size -= originalOccurrences - occurrences;
        return true;
    }

    @Override
    public TreeBag<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public TreeBag<T> withAll(Iterable<? extends T> iterable)
    {
        this.addAllIterable(iterable);
        return this;
    }

    @Override
    public TreeBag<T> withoutAll(Iterable<? extends T> iterable)
    {
        this.removeAllIterable(iterable);
        return this;
    }

    @Override
    public TreeBag<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    public MutableSortedBag<T> newEmpty()
    {
        return TreeBag.newBag(this.items.comparator());
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        boolean changed = false;
        Set<Map.Entry<T, Counter>> entries = this.items.entrySet();
        for (Iterator<Map.Entry<T, Counter>> iterator = entries.iterator(); iterator.hasNext(); )
        {
            Map.Entry<T, Counter> entry = iterator.next();
            if (predicate.accept(entry.getKey()))
            {
                this.size -= entry.getValue().getCount();
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        boolean changed = false;
        Set<Map.Entry<T, Counter>> entries = this.items.entrySet();
        for (Iterator<Map.Entry<T, Counter>> iterator = entries.iterator(); iterator.hasNext(); )
        {
            Map.Entry<T, Counter> entry = iterator.next();
            if (predicate.accept(entry.getKey(), parameter))
            {
                this.size -= entry.getValue().getCount();
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        int oldSize = this.size;
        for (Object each : iterable)
        {
            Counter removed = this.items.remove(each);
            if (removed != null)
            {
                this.size -= removed.getCount();
            }
        }
        return this.size != oldSize;
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public int indexOf(Object object)
    {
        if (this.items.containsKey(object))
        {
            long result = this.items.headMap((T) object).values().sumOfInt(Counter.TO_COUNT);
            if (result > Integer.MAX_VALUE)
            {
                throw new IllegalStateException();
            }
            return (int) result;
        }
        return -1;
    }

    @Override
    public MutableSortedSet<Pair<T, Integer>> zipWithIndex()
    {
        Comparator<? super T> comparator = this.items.comparator();
        return this.zipWithIndex(TreeSortedSet.newSet((o1, o2) ->
        {
            int compare = comparator == null
                    ? Comparators.nullSafeCompare(o1, o2)
                    : comparator.compare(o1.getOne(), o2.getOne());
            if (compare != 0)
            {
                return compare;
            }
            return o1.getTwo().compareTo(o2.getTwo());
        }));
    }

    @Override
    public MutableSortedSet<T> distinct()
    {
        return SortedSets.mutable.withAll(this.comparator(), this.items.keySet());
    }

    @Override
    public <V> TreeBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, TreeBagMultimap.newMultimap(this.comparator()));
    }

    @Override
    public <V> TreeBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, TreeBagMultimap.newMultimap(this.comparator()));
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return Iterate.detectIndex(this, predicate);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return OrderedIterate.corresponds(this, other, predicate);
    }

    @Override
    public MutableStack<T> toStack()
    {
        return ArrayStack.newStack(this);
    }

    @Override
    public MutableSortedBag<T> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        return IterableIterate.take(this, Math.min(this.size(), count), this.newEmpty());
    }

    @Override
    public MutableSortedBag<T> drop(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        return IterableIterate.drop(this, count, this.newEmpty());
    }

    @Override
    public Comparator<? super T> comparator()
    {
        return this.items.comparator();
    }

    public TreeBag<T> with(T... elements)
    {
        this.addAll(Arrays.asList(elements));
        return this;
    }

    public TreeBag<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    @Override
    public boolean add(T item)
    {
        Counter counter = this.items.getIfAbsentPut(item, Counter::new);
        counter.increment();
        this.size++;
        return true;
    }

    public TreeBag<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    @Override
    public RichIterable<T> distinctView()
    {
        return this.items.keySet().asUnmodifiable();
    }

    private class InternalIterator implements Iterator<T>
    {
        private final Iterator<T> iterator = TreeBag.this.items.keySet().iterator();

        private T currentItem;
        private int occurrences;
        private boolean canRemove;

        @Override
        public boolean hasNext()
        {
            return this.occurrences > 0 || this.iterator.hasNext();
        }

        @Override
        public T next()
        {
            if (this.occurrences == 0)
            {
                this.currentItem = this.iterator.next();
                this.occurrences = TreeBag.this.occurrencesOf(this.currentItem);
            }
            this.occurrences--;
            this.canRemove = true;
            return this.currentItem;
        }

        @Override
        public void remove()
        {
            if (!this.canRemove)
            {
                throw new IllegalStateException();
            }
            if (this.occurrences == 0)
            {
                this.iterator.remove();
                TreeBag.this.size--;
            }
            else
            {
                TreeBag.this.remove(this.currentItem);
            }
            this.canRemove = false;
        }
    }
}
