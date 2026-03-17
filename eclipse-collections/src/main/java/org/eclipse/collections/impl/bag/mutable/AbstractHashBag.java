/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable;

import java.util.Iterator;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.Counter;
import org.eclipse.collections.impl.block.factory.primitive.IntToIntFunctions;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.set.mutable.SetAdapter;

public abstract class AbstractHashBag<T> extends AbstractMutableBag<T>
{
    protected MutableObjectIntMap<T> items;
    protected int size;

    @Override
    public int addOccurrences(T item, int occurrences)
    {
        if (occurrences < 0)
        {
            throw new IllegalArgumentException("Cannot add a negative number of occurrences");
        }
        if (occurrences > 0)
        {
            int updatedOccurrences = this.items.updateValue(item, 0, IntToIntFunctions.add(occurrences));
            this.size += occurrences;
            return updatedOccurrences;
        }
        return this.occurrencesOf(item);
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
        return this.items.allSatisfyKeyValue((key, count) -> bag.occurrencesOf(key) == count);
    }

    @Override
    public int hashCode()
    {
        Counter counter = new Counter();
        this.items.forEachKeyValue((item, count) -> counter.add((item == null ? 0 : this.computeHashCode(item)) ^ count));
        return counter.getCount();
    }

    protected abstract int computeHashCode(T item);

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
    public int occurrencesOf(Object item)
    {
        return this.items.get(item);
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.items.forEachKeyValue(objectIntProcedure);
    }

    @Override
    public boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.items.keyValuesView().anySatisfy(each -> predicate.accept(each.getOne(), each.getTwo()));
    }

    @Override
    public boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.items.keyValuesView().allSatisfy(each -> predicate.accept(each.getOne(), each.getTwo()));
    }

    @Override
    public boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        return this.items.keyValuesView().noneSatisfy(each -> predicate.accept(each.getOne(), each.getTwo()));
    }

    @Override
    public T detectWithOccurrences(ObjectIntPredicate<? super T> predicate)
    {
        ObjectIntPair<T> pair = this.items.keyValuesView().detect(each -> predicate.accept(each.getOne(), each.getTwo()));
        return pair == null ? null : pair.getOne();
    }

    @Override
    public MutableMap<T, Integer> toMapOfItemToCount()
    {
        MutableMap<T, Integer> map = UnifiedMap.newMap(this.items.size());
        this.forEachWithOccurrences(map::put);
        return map;
    }

    @Override
    public boolean add(T item)
    {
        this.items.updateValue(item, 0, IntToIntFunctions.increment());
        this.size++;
        return true;
    }

    @Override
    public boolean remove(Object item)
    {
        int newValue = this.items.updateValue((T) item, 0, IntToIntFunctions.decrement());
        if (newValue <= 0)
        {
            this.items.removeKey((T) item);
            if (newValue == -1)
            {
                return false;
            }
        }
        this.size--;
        return true;
    }

    @Override
    public void clear()
    {
        this.items.clear();
        this.size = 0;
    }

    @Override
    public boolean isEmpty()
    {
        return this.items.isEmpty();
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.items.forEachKeyValue((key, count) ->
        {
            for (int i = 0; i < count; i++)
            {
                procedure.value(key);
            }
        });
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        Counter index = new Counter();
        this.items.forEachKeyValue((key, count) ->
        {
            for (int i = 0; i < count; i++)
            {
                objectIntProcedure.value(key, index.getCount());
                index.increment();
            }
        });
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.items.forEachKeyValue((key, count) ->
        {
            for (int i = 0; i < count; i++)
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

        int newValue = this.items.updateValue((T) item, 0, IntToIntFunctions.subtract(occurrences));

        if (newValue <= 0)
        {
            this.size -= occurrences + newValue;
            this.items.remove(item);
            return newValue + occurrences != 0;
        }

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

        int originalOccurrences = this.items.get(item);

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
            this.items.put(item, occurrences);
        }

        this.size -= originalOccurrences - occurrences;
        return true;
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        return this.removeIf((java.util.function.Predicate<? super T>) predicate);
    }

    @Override
    public boolean removeIf(java.util.function.Predicate<? super T> predicate)
    {
        boolean changed = false;
        for (Iterator<T> iterator = this.items.keySet().iterator(); iterator.hasNext(); )
        {
            T key = iterator.next();
            if (predicate.test(key))
            {
                this.size -= this.items.get(key);
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
        for (Iterator<T> iterator = this.items.keySet().iterator(); iterator.hasNext(); )
        {
            T key = iterator.next();
            if (predicate.accept(key, parameter))
            {
                this.size -= this.items.get(key);
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
        if (iterable instanceof Bag)
        {
            Bag<?> source = (Bag<?>) iterable;
            source.forEachWithOccurrences((each, parameter) ->
            {
                int removed = this.items.removeKeyIfAbsent((T) each, 0);
                this.size -= removed;
            });
        }
        else
        {
            for (Object each : iterable)
            {
                int removed = this.items.removeKeyIfAbsent((T) each, 0);
                this.size -= removed;
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
    public boolean contains(Object o)
    {
        return this.items.containsKey(o);
    }

    @Override
    public <V> HashBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, HashBagMultimap.newMultimap());
    }

    @Override
    public <V> HashBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, HashBagMultimap.newMultimap());
    }

    @Override
    public RichIterable<T> distinctView()
    {
        return SetAdapter.adapt(this.items.keySet()).asUnmodifiable();
    }

    private class InternalIterator implements Iterator<T>
    {
        private final Iterator<T> iterator = AbstractHashBag.this.items.keySet().iterator();

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
                this.occurrences = AbstractHashBag.this.occurrencesOf(this.currentItem);
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
                AbstractHashBag.this.size--;
            }
            else
            {
                AbstractHashBag.this.remove(this.currentItem);
            }
            this.canRemove = false;
        }
    }
}
