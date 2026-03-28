/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.sorted.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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

public class TreeSortedMap<K, V>
        extends AbstractMutableSortedMap<K, V>
        implements Externalizable
{
    private static final long serialVersionUID = 1L;
    private TreeMap<K, V> treeMap;

    public TreeSortedMap()
    {
        this.treeMap = new TreeMap<>();
    }

    public TreeSortedMap(Comparator<? super K> comparator)
    {
        this.treeMap = new TreeMap<>(comparator);
    }

    public TreeSortedMap(Map<? extends K, ? extends V> map)
    {
        this.treeMap = new TreeMap<>(map);
    }

    public TreeSortedMap(Comparator<? super K> comparator, Map<? extends K, ? extends V> map)
    {
        this.treeMap = new TreeMap<>(comparator);
        this.treeMap.putAll(map);
    }

    public TreeSortedMap(SortedMap<K, ? extends V> map)
    {
        this.treeMap = new TreeMap<>(map);
    }

    public TreeSortedMap(Pair<K, V>... pairs)
    {
        this.treeMap = new TreeMap<>();
        ArrayIterate.forEach(pairs, new MapCollectProcedure<Pair<K, V>, K, V>(
                this.treeMap,
                Functions.firstOfPair(),
                Functions.secondOfPair()));
    }

    public static <K, V> TreeSortedMap<K, V> newMap()
    {
        return new TreeSortedMap<>();
    }

    public static <K, V> TreeSortedMap<K, V> newMap(Comparator<? super K> comparator)
    {
        return new TreeSortedMap<>(comparator);
    }

    public static <K, V> TreeSortedMap<K, V> newMap(Map<? extends K, ? extends V> map)
    {
        if (map instanceof SortedMap<?, ?>)
        {
            return new TreeSortedMap<>((SortedMap<K, V>) map);
        }
        return new TreeSortedMap<>(map);
    }

    public static <K, V> TreeSortedMap<K, V> newMap(Comparator<? super K> comparator, Map<? extends K, ? extends V> map)
    {
        return new TreeSortedMap<>(comparator, map);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(Pair<K, V>... pairs)
    {
        return new TreeSortedMap<>(pairs);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(Comparator<? super K> comparator, Pair<K, V>... pairs)
    {
        return new TreeSortedMap<K, V>(comparator).with(pairs);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(K key, V value)
    {
        return new TreeSortedMap<K, V>().with(key, value);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(K key1, V value1, K key2, V value2)
    {
        return new TreeSortedMap<K, V>().with(key1, value1, key2, value2);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return new TreeSortedMap<K, V>().with(key1, value1, key2, value2, key3, value3);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        return new TreeSortedMap<K, V>().with(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(Comparator<? super K> comparator, K key, V value)
    {
        return new TreeSortedMap<K, V>(comparator).with(key, value);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(Comparator<? super K> comparator, K key1, V value1, K key2, V value2)
    {
        return new TreeSortedMap<K, V>(comparator).with(key1, value1, key2, value2);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(Comparator<? super K> comparator, K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return new TreeSortedMap<K, V>(comparator).with(key1, value1, key2, value2, key3, value3);
    }

    public static <K, V> TreeSortedMap<K, V> newMapWith(
            Comparator<? super K> comparator,
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        return new TreeSortedMap<K, V>(comparator).with(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    public TreeSortedMap<K, V> with(K key, V value)
    {
        this.put(key, value);
        return this;
    }

    public TreeSortedMap<K, V> with(K key1, V value1, K key2, V value2)
    {
        this.put(key1, value1);
        this.put(key2, value2);
        return this;
    }

    public TreeSortedMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        this.put(key1, value1);
        this.put(key2, value2);
        this.put(key3, value3);
        return this;
    }

    public TreeSortedMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        this.put(key1, value1);
        this.put(key2, value2);
        this.put(key3, value3);
        this.put(key4, value4);
        return this;
    }

    @Override
    public TreeSortedMap<K, V> with(Pair<K, V>... pairs)
    {
        ArrayIterate.forEach(pairs, new MapCollectProcedure<Pair<K, V>, K, V>(this, Functions.firstOfPair(), Functions.secondOfPair()));
        return this;
    }

    @Override
    public int size()
    {
        return this.treeMap.size();
    }

    @Override
    public MutableSortedMap<K, V> newEmpty()
    {
        return new TreeSortedMap<>(this.comparator());
    }

    @Override
    public V removeKey(K key)
    {
        return this.treeMap.remove(key);
    }

    @Override
    public TreeSortedMap<K, V> clone()
    {
        return new TreeSortedMap<>(this);
    }

    @Override
    public boolean equals(Object o)
    {
        return this.treeMap.equals(o);
    }

    @Override
    public int hashCode()
    {
        return this.treeMap.hashCode();
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure2)
    {
        MapIterate.forEachKeyValue(this.treeMap, procedure2);
    }

    @Override
    public K firstKey()
    {
        return this.treeMap.firstKey();
    }

    @Override
    public K lastKey()
    {
        return this.treeMap.lastKey();
    }

    @Override
    public MutableSet<Entry<K, V>> entrySet()
    {
        return SetAdapter.adapt(this.treeMap.entrySet());
    }

    @Override
    public MutableSet<K> keySet()
    {
        return SetAdapter.adapt(this.treeMap.keySet());
    }

    @Override
    public MutableCollection<V> values()
    {
        return CollectionAdapter.adapt(this.treeMap.values());
    }

    @Override
    public Comparator<? super K> comparator()
    {
        return this.treeMap.comparator();
    }

    @Override
    public V get(Object key)
    {
        return this.treeMap.get(key);
    }

    @Override
    public V put(K key, V value)
    {
        return this.treeMap.put(key, value);
    }

    @Override
    public V remove(Object key)
    {
        return this.treeMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        this.treeMap.putAll(map);
    }

    @Override
    public void clear()
    {
        this.treeMap.clear();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.treeMap.containsKey(key);
    }

    @Override
    public MutableSortedMap<K, V> headMap(K toKey)
    {
        return SortedMapAdapter.adapt(this.treeMap.headMap(toKey));
    }

    @Override
    public MutableSortedMap<K, V> tailMap(K fromKey)
    {
        return SortedMapAdapter.adapt(this.treeMap.tailMap(fromKey));
    }

    @Override
    public MutableSortedMap<K, V> subMap(K fromKey, K toKey)
    {
        return SortedMapAdapter.adapt(this.treeMap.subMap(fromKey, toKey));
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.treeMap.containsValue(value);
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

        MutableSortedMap<K, V> output = this.newEmpty();
        Iterator<Entry<K, V>> iterator = this.treeMap.entrySet().iterator();
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
        MutableSortedMap<K, V> output = this.newEmpty();
        Iterator<Entry<K, V>> iterator = this.treeMap.entrySet().iterator();
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
        Iterator<Entry<K, V>> iterator = this.treeMap.entrySet().iterator();
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
        Iterator<Entry<K, V>> iterator = this.treeMap.entrySet().iterator();
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
        Iterator<Entry<K, V>> iterator = this.treeMap.entrySet().iterator();
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

    @Override
    public String toString()
    {
        return this.treeMap.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.comparator());
        out.writeInt(this.size());
        for (Entry<K, V> entry : this.treeMap.entrySet())
        {
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.treeMap = new TreeMap<>((Comparator<? super K>) in.readObject());
        int size = in.readInt();
        for (int i = 0; i < size; ++i)
        {
            this.treeMap.put((K) in.readObject(), (V) in.readObject());
        }
    }
}
