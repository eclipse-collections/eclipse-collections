/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.sorted.mutable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.BiFunction;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.procedure.MapCollectProcedure;
import org.eclipse.collections.impl.collection.mutable.CollectionAdapter;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.set.mutable.SetAdapter;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.MapIterate;

/**
 * This class provides a MutableSortedMap wrapper around a JDK Collections SortedMap interface instance. All the MutableSortedMap
 * interface methods are supported in addition to the JDK SortedMap interface methods.
 * <p>
 * To create a new wrapper around an existing SortedMap instance, use the {@link #adapt(SortedMap)} factory method.
 */
public class SortedMapAdapter<K, V>
        extends AbstractMutableSortedMap<K, V>
        implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final SortedMap<K, V> delegate;

    protected SortedMapAdapter(SortedMap<K, V> newDelegate)
    {
        if (newDelegate == null)
        {
            throw new NullPointerException("SortedMapAdapter may not wrap null");
        }
        this.delegate = newDelegate;
    }

    public static <K, V> MutableSortedMap<K, V> adapt(SortedMap<K, V> map)
    {
        return map instanceof MutableSortedMap<?, ?> ? (MutableSortedMap<K, V>) map : new SortedMapAdapter<>(map);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        MapIterate.forEachKeyValue(this.delegate, procedure);
    }

    /**
     * @deprecated use {@link TreeSortedMap#newEmpty()} instead (inlineable)
     */
    @Override
    @Deprecated
    public MutableSortedMap<K, V> newEmpty()
    {
        return TreeSortedMap.newMap(this.delegate.comparator());
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    @Override
    public Comparator<? super K> comparator()
    {
        return this.delegate.comparator();
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.delegate.values().iterator();
    }

    @Override
    public V remove(Object key)
    {
        return this.delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        this.delegate.putAll(map);
    }

    @Override
    public MutableCollection<V> values()
    {
        return CollectionAdapter.adapt(this.delegate.values());
    }

    @Override
    public MutableSet<Entry<K, V>> entrySet()
    {
        return SetAdapter.adapt(this.delegate.entrySet());
    }

    @Override
    public MutableSet<K> keySet()
    {
        return SetAdapter.adapt(this.delegate.keySet());
    }

    @Override
    public K firstKey()
    {
        return this.delegate.firstKey();
    }

    @Override
    public K lastKey()
    {
        return this.delegate.lastKey();
    }

    @Override
    public MutableSortedMap<K, V> headMap(K toKey)
    {
        return SortedMapAdapter.adapt(this.delegate.headMap(toKey));
    }

    @Override
    public MutableSortedMap<K, V> tailMap(K fromKey)
    {
        return SortedMapAdapter.adapt(this.delegate.tailMap(fromKey));
    }

    @Override
    public MutableSortedMap<K, V> subMap(K fromKey, K toKey)
    {
        return SortedMapAdapter.adapt(this.delegate.subMap(fromKey, toKey));
    }

    @Override
    public void clear()
    {
        this.delegate.clear();
    }

    @Override
    public V get(Object key)
    {
        return this.delegate.get(key);
    }

    @Override
    public V put(K key, V value)
    {
        return this.delegate.put(key, value);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        return this.delegate.merge(key, value, remappingFunction);
    }

    @Override
    public V removeKey(K key)
    {
        return this.delegate.remove(key);
    }

    @Override
    public MutableSortedMap<K, V> with(Pair<K, V>... pairs)
    {
        ArrayIterate.forEach(pairs, new MapCollectProcedure<Pair<K, V>, K, V>(this, Functions.firstOfPair(), Functions.secondOfPair()));
        return this;
    }

    @Override
    public String toString()
    {
        return this.delegate.toString();
    }

    @Override
    public MutableSortedMap<K, V> clone()
    {
        return TreeSortedMap.newMap(this.delegate);
    }

    @Override
    public boolean equals(Object o)
    {
        return this.delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public MutableSortedMap<K, V> toReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toReversed() not implemented yet");
    }

    @Override
    public MutableSortedMap<K, V> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        MutableSortedMap<K, V> output = SortedMaps.mutable.of(this.comparator());
        Iterator<Entry<K, V>> iterator = this.delegate.entrySet().iterator();
        int countCopy = count;
        while (iterator.hasNext() && countCopy-- > 0)
        {
            Entry<K, V> next = iterator.next();
            output.put(next.getKey(), next.getValue());
        }
        return output;
    }

    @Override
    public MutableSortedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        MutableSortedMap<K, V> output = SortedMaps.mutable.of(this.comparator());
        Iterator<Entry<K, V>> iterator = this.delegate.entrySet().iterator();
        while (iterator.hasNext())
        {
            Entry<K, V> next = iterator.next();
            if (predicate.accept(next.getValue()))
            {
                output.put(next.getKey(), next.getValue());
            }
            else
            {
                break;
            }
        }
        return output;
    }

    @Override
    public MutableSortedMap<K, V> drop(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        MutableSortedMap<K, V> output = SortedMaps.mutable.of(this.comparator());
        Iterator<Entry<K, V>> iterator = this.delegate.entrySet().iterator();
        int start = Math.min(count, this.size());
        if (start == this.size())
        {
            return output;
        }
        int i = 0;
        while (iterator.hasNext())
        {
            if (i >= start)
            {
                Entry<K, V> next = iterator.next();
                output.put(next.getKey(), next.getValue());
            }
            else
            {
                iterator.next();
            }
            i++;
        }
        return output;
    }

    @Override
    public MutableSortedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        MutableSortedMap<K, V> output = SortedMaps.mutable.of(this.comparator());
        Iterator<Entry<K, V>> iterator = this.delegate.entrySet().iterator();
        while (iterator.hasNext())
        {
            Entry<K, V> next = iterator.next();
            if (!predicate.accept(next.getValue()))
            {
                output.put(next.getKey(), next.getValue());
                break;
            }
        }
        while (iterator.hasNext())
        {
            Entry<K, V> next = iterator.next();
            output.put(next.getKey(), next.getValue());
        }
        return output;
    }

    @Override
    public PartitionMutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        PartitionMutableList<V> result = new PartitionFastList<>();
        Iterator<Entry<K, V>> iterator = this.delegate.entrySet().iterator();
        while (iterator.hasNext())
        {
            V value = iterator.next().getValue();
            if (!predicate.accept(value))
            {
                result.getRejected().add(value);
                break;
            }
            result.getSelected().add(value);
        }
        while (iterator.hasNext())
        {
            result.getRejected().add(iterator.next().getValue());
        }
        return result;
    }

    @Override
    public MutableList<V> distinct()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".distinct() not implemented yet");
    }
}
