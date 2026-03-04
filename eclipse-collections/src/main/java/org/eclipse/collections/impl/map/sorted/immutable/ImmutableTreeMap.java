/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.sorted.immutable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.MapIterate;

/**
 * @see ImmutableSortedMap
 */
public class ImmutableTreeMap<K, V>
        extends AbstractImmutableSortedMap<K, V>
        implements Serializable
{
    private final K[] keys;
    private final V[] values;
    private final Comparator<? super K> comparator;

    public ImmutableTreeMap(SortedMap<K, V> sortedMap)
    {
        if (sortedMap == null)
        {
            throw new NullPointerException("Cannot convert null to ImmutableSortedMap");
        }
        this.comparator = sortedMap.comparator();
        K[] keysCopy = (K[]) new Object[sortedMap.size()];
        V[] valuesCopy = (V[]) new Object[sortedMap.size()];
        int index = 0;
        for (Entry<K, V> entry : sortedMap.entrySet())
        {
            keysCopy[index] = entry.getKey();
            valuesCopy[index] = entry.getValue();
            index++;
        }
        this.keys = keysCopy;
        this.values = valuesCopy;
    }

    public static <K, V> ImmutableSortedMap<K, V> newMap(SortedMap<K, V> sortedMap)
    {
        return new ImmutableTreeMap<>(sortedMap);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (!(object instanceof Map))
        {
            return false;
        }
        Map<?, ?> other = (Map<?, ?>) object;
        if (this.size() != other.size())
        {
            return false;
        }
        for (int index = 0; index < this.size(); index++)
        {
            if (!Objects.equals(this.values[index], other.get(this.keys[index])))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int length = this.keys.length;
        int hashCode = 0;
        for (int index = 0; index < length; index++)
        {
            K key = this.keys[index];
            V value = this.values[index];
            hashCode += (key == null ? 0 : key.hashCode())
                    ^ (value == null ? 0 : value.hashCode());
        }
        return hashCode;
    }

    @Override
    public String toString()
    {
        int length = this.keys.length;
        if (length == 0)
        {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int index = 0; index < length; index++)
        {
            sb.append(this.keys[index]);
            sb.append('=');
            sb.append(this.values[index]);
            if (index < length - 1)
            {
                sb.append(',').append(' ');
            }
        }
        return sb.append('}').toString();
    }

    @Override
    public int size()
    {
        return this.keys.length;
    }

    @Override
    public boolean containsKey(Object key)
    {
        return Arrays.binarySearch(this.keys, (K) key, this.comparator) >= 0;
    }

    @Override
    public boolean containsValue(Object value)
    {
        return ArrayIterate.contains(this.values, value);
    }

    @Override
    public V get(Object key)
    {
        int index = Arrays.binarySearch(this.keys, (K) key, this.comparator);
        if (index >= 0)
        {
            return this.values[index];
        }
        return null;
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        for (int index = 0; index < this.keys.length; index++)
        {
            procedure.value(this.keys[index], this.values[index]);
        }
    }

    @Override
    public ImmutableMap<V, K> flipUniqueValues()
    {
        return MapIterate.flipUniqueValues(this).toImmutable();
    }

    @Override
    public RichIterable<K> keysView()
    {
        return FastList.newListWith(this.keys).asLazy();
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return FastList.newListWith(this.values).asLazy();
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return FastList.newListWith(this.keys).asLazy().zip(FastList.newListWith(this.values));
    }

    @Override
    public Comparator<? super K> comparator()
    {
        return this.comparator;
    }

    @Override
    public K firstKey()
    {
        if (this.keys.length == 0)
        {
            throw new NoSuchElementException();
        }
        return this.keys[0];
    }

    @Override
    public K lastKey()
    {
        if (this.keys.length == 0)
        {
            throw new NoSuchElementException();
        }
        return this.keys[this.keys.length - 1];
    }

    @Override
    public Set<K> keySet()
    {
        return new ImmutableSortedMapKeySet();
    }

    @Override
    public Collection<V> values()
    {
        return FastList.newListWith(this.values).asUnmodifiable();
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        int length = this.keys.length;
        MutableSortedSet<Entry<K, V>> entrySet = SortedSets.mutable.with(new EntryComparator<>(this.comparator));
        for (int index = 0; index < length; index++)
        {
            entrySet.add(ImmutableEntry.of(this.keys[index], this.values[index]));
        }
        return entrySet.toImmutable().castToSortedSet();
    }

    protected Object writeReplace()
    {
        return new ImmutableSortedMapSerializationProxy<>(this);
    }

    @Override
    public ImmutableSortedMap<K, V> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (count == 0)
        {
            return SortedMaps.immutable.of(this.comparator());
        }
        if (count >= this.size())
        {
            return this;
        }

        MutableSortedMap<K, V> output = SortedMaps.mutable.of(this.comparator());
        for (int i = 0; i < count; i++)
        {
            output.put(this.keys[i], this.values[i]);
        }

        return output.toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> drop(int count)
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
            return SortedMaps.immutable.of(this.comparator());
        }

        MutableSortedMap<K, V> output = SortedMaps.mutable.of(this.comparator());
        for (int i = 0; i < this.size(); i++)
        {
            if (i >= count)
            {
                output.put(this.keys[i], this.values[i]);
            }
        }

        return output.toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        MutableSortedMap<K, V> output = SortedMaps.mutable.of(this.comparator());
        for (int i = 0; i < this.size(); i++)
        {
            if (predicate.accept(this.values[i]))
            {
                output.put(this.keys[i], this.values[i]);
            }
            else
            {
                break;
            }
        }
        return output.toImmutable();
    }

    @Override
    public ImmutableSortedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        MutableSortedMap<K, V> output = SortedMaps.mutable.of(this.comparator());
        int start = 0;
        for (; start < this.size(); start++)
        {
            if (!predicate.accept(this.values[start]))
            {
                break;
            }
        }
        for (int i = start; i < this.size(); i++)
        {
            output.put(this.keys[i], this.values[i]);
        }
        return output.toImmutable();
    }

    @Override
    public PartitionImmutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        PartitionMutableList<V> result = new PartitionFastList<>();
        int i = 0;
        for (; i < this.size(); i++)
        {
            if (!predicate.accept(this.values[i]))
            {
                break;
            }
            result.getSelected().add(this.values[i]);
        }
        for (; i < this.size(); i++)
        {
            result.getRejected().add(this.values[i]);
        }
        return result.toImmutable();
    }

    private static final class EntryComparator<K, V> implements Comparator<Entry<K, V>>
    {
        private final Comparator<? super K> comparator;

        private EntryComparator(Comparator<? super K> comparator)
        {
            if (comparator == null)
            {
                this.comparator = Comparators.naturalOrder();
            }
            else
            {
                this.comparator = comparator;
            }
        }

        @Override
        public int compare(Entry<K, V> o1, Entry<K, V> o2)
        {
            return this.comparator.compare(o1.getKey(), o2.getKey());
        }
    }

    protected class ImmutableSortedMapKeySet extends AbstractSet<K> implements Serializable
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean contains(Object o)
        {
            return ImmutableTreeMap.this.containsKey(o);
        }

        @Override
        public boolean containsAll(Collection<?> collection)
        {
            return Iterate.allSatisfyWith(collection, Predicates2.in(), this);
        }

        @Override
        public int size()
        {
            return ImmutableTreeMap.this.keys.length;
        }

        @Override
        public Iterator<K> iterator()
        {
            return FastList.newListWith(ImmutableTreeMap.this.keys).asUnmodifiable().iterator();
        }

        @Override
        public Object[] toArray()
        {
            int size = ImmutableTreeMap.this.keys.length;
            Object[] result = new Object[size];
            System.arraycopy(ImmutableTreeMap.this.keys, 0, result, 0, size);
            return result;
        }

        @Override
        public <T> T[] toArray(T[] a)
        {
            T[] result = a.length < this.size()
                    ? (T[]) Array.newInstance(a.getClass().getComponentType(), this.size())
                    : a;

            System.arraycopy(ImmutableTreeMap.this.keys, 0, result, 0, ImmutableTreeMap.this.keys.length);

            if (result.length > this.size())
            {
                result[this.size()] = null;
            }
            return result;
        }

        @Override
        public boolean add(K key)
        {
            throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean addAll(Collection<? extends K> collection)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean remove(Object key)
        {
            throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            throw new UnsupportedOperationException("Cannot call removeAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public void clear()
        {
            throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            throw new UnsupportedOperationException("Cannot call retainAll() on " + this.getClass().getSimpleName());
        }

        protected Object writeReplace()
        {
            return TreeSortedSet.newSetWith(ImmutableTreeMap.this.comparator, ImmutableTreeMap.this.keys).toImmutable();
        }
    }
}
