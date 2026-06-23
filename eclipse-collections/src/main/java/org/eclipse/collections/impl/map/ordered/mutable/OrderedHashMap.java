/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.ordered.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
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
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.ByteLists;
import org.eclipse.collections.api.factory.primitive.CharLists;
import org.eclipse.collections.api.factory.primitive.DoubleLists;
import org.eclipse.collections.api.factory.primitive.FloatLists;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
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
import org.eclipse.collections.api.map.ImmutableOrderedMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.procedure.PartitionPredicate2Procedure;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.collection.mutable.CollectionAdapter;
import org.eclipse.collections.impl.map.AbstractMapIterable;
import org.eclipse.collections.impl.map.ordered.immutable.ImmutableOrderedMapAdapter;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.set.mutable.SetAdapter;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.tuple.AbstractImmutableEntry;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * A memory-efficient, insertion-ordered map using a sparse index table with open addressing
 * and a dense entries array. Follows the same probing and tombstone patterns as
 * the generated {@code Object<Primitive>HashMap} classes but stores integer indices that point into a separate dense array
 * that maintains insertion order.
 *
 * @since 14.0
 */
public class OrderedHashMap<K, V>
        extends AbstractMapIterable<K, V>
        implements MutableOrderedMap<K, V>, Externalizable
{
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_INITIAL_CAPACITY = 8;
    private static final int EMPTY = -1;
    private static final int REMOVED = -2;

    private static final Object NULL_KEY = new Object()
    {
        @Override
        public boolean equals(Object obj)
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public int hashCode()
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public String toString()
        {
            return "OrderedHashMap.NULL_KEY";
        }
    };

    private static final Object REMOVED_KEY = new Object()
    {
        @Override
        public boolean equals(Object obj)
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public int hashCode()
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public String toString()
        {
            return "OrderedHashMap.REMOVED_KEY";
        }
    };

    // Sparse hash table with open addressing. Each slot holds an index into entries, EMPTY, or REMOVED.
    private int[] indices;
    // Dense array of interleaved key-value pairs: [k0, v0, k1, v1, ...] in insertion order.
    // Removed entries are tombstoned with REMOVED_KEY but not compacted until rehash.
    private Object[] entries;
    // Number of live (non-removed) entries in the map.
    private int size;
    // High-water mark in entries: total slots appended, including tombstoned entries not yet compacted.
    private int entryCount;
    // Number of REMOVED sentinel slots in the indices table, used to decide when to rehash.
    private int occupiedWithSentinels;

    public OrderedHashMap()
    {
        this.allocate(DEFAULT_INITIAL_CAPACITY << 1);
    }

    public OrderedHashMap(int initialCapacity)
    {
        if (initialCapacity < 0)
        {
            throw new IllegalArgumentException("initial capacity cannot be less than 0");
        }
        int capacity = smallestPowerOfTwoGreaterThan(Math.max(initialCapacity << 1, DEFAULT_INITIAL_CAPACITY << 1));
        this.allocate(capacity);
    }

    public OrderedHashMap(Map<? extends K, ? extends V> map)
    {
        this(Math.max(map.size(), DEFAULT_INITIAL_CAPACITY));
        this.putAll(map);
    }

    public OrderedHashMap(MapIterable<? extends K, ? extends V> map)
    {
        this(Math.max(map.size(), DEFAULT_INITIAL_CAPACITY));
        map.forEachKeyValue(this::put);
    }

    public static <K, V> OrderedHashMap<K, V> newMap()
    {
        return new OrderedHashMap<>();
    }

    public static <K, V> OrderedHashMap<K, V> newWithKeysValues(K key, V value)
    {
        OrderedHashMap<K, V> map = new OrderedHashMap<>(1);
        map.put(key, value);
        return map;
    }

    public static <K, V> OrderedHashMap<K, V> newWithKeysValues(K key1, V value1, K key2, V value2)
    {
        OrderedHashMap<K, V> map = new OrderedHashMap<>(2);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    public static <K, V> OrderedHashMap<K, V> newWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        OrderedHashMap<K, V> map = new OrderedHashMap<>(3);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    public static <K, V> OrderedHashMap<K, V> newWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        OrderedHashMap<K, V> map = new OrderedHashMap<>(4);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return map;
    }

    private void allocate(int capacity)
    {
        this.indices = new int[capacity];
        Arrays.fill(this.indices, EMPTY);
        this.entries = new Object[capacity];
        this.size = 0;
        this.entryCount = 0;
        this.occupiedWithSentinels = 0;
    }

    private int spread(Object key)
    {
        int h = key == null ? 0 : key.hashCode();
        h ^= h >>> 20 ^ h >>> 12;
        h ^= h >>> 7 ^ h >>> 4;
        return h & (this.indices.length - 1);
    }

    private int maxOccupied()
    {
        int capacity = this.indices.length;
        return Math.min(capacity - 1, capacity >> 1);
    }

    private static int smallestPowerOfTwoGreaterThan(int n)
    {
        return n > 1 ? Integer.highestOneBit(n - 1) << 1 : 1;
    }

    private static boolean isNonSentinel(Object key)
    {
        return key != null && key != REMOVED_KEY;
    }

    private static boolean isRemovedKey(Object key)
    {
        return key == REMOVED_KEY;
    }

    @SuppressWarnings("unchecked")
    private K toNonSentinel(Object key)
    {
        return key == NULL_KEY ? null : (K) key;
    }

    private static Object toSentinelIfNull(Object key)
    {
        return key == null ? NULL_KEY : key;
    }

    private boolean nullSafeKeyEquals(Object storedSentinelKey, Object searchKey)
    {
        K stored = this.toNonSentinel(storedSentinelKey);
        return stored == searchKey || (stored != null && stored.equals(searchKey));
    }

    /**
     * Probes the indices table for the given key. Returns the slot index in {@code indices[]}.
     * <p>
     * The returned slot will be one of:
     * <ul>
     *     <li>{@code indices[slot] >= 0} — key was found at this entry index</li>
     *     <li>{@code indices[slot] == EMPTY} — key not found, insert here</li>
     *     <li>{@code indices[slot] == REMOVED} — key not found, but this removed slot can be reused</li>
     * </ul>
     */
    private int probeIndex(Object key)
    {
        int slot = this.spread(key);
        int removedSlot = -1;

        int idx = this.indices[slot];
        if (idx == REMOVED)
        {
            removedSlot = slot;
        }
        else if (idx == EMPTY)
        {
            return slot;
        }
        else if (this.nullSafeKeyEquals(this.entries[idx << 1], key))
        {
            return slot;
        }

        int probe = 17;
        while (true)
        {
            slot = (slot + probe) & (this.indices.length - 1);
            probe += 17;

            idx = this.indices[slot];
            if (idx == REMOVED)
            {
                if (removedSlot == -1)
                {
                    removedSlot = slot;
                }
            }
            else if (idx == EMPTY)
            {
                return removedSlot == -1 ? slot : removedSlot;
            }
            else if (this.nullSafeKeyEquals(this.entries[idx << 1], key))
            {
                return slot;
            }
        }
    }

    private void addKeyValueAtSlot(K key, V value, int slot)
    {
        if (this.indices[slot] == REMOVED)
        {
            this.occupiedWithSentinels--;
        }

        if ((this.entryCount << 1) >= this.entries.length)
        {
            this.rehashAndGrow();
            slot = this.probeIndex(key);
        }

        this.entries[this.entryCount << 1] = toSentinelIfNull(key);
        this.entries[(this.entryCount << 1) + 1] = value;
        this.indices[slot] = this.entryCount;
        this.entryCount++;
        this.size++;

        if (this.size + this.occupiedWithSentinels > this.maxOccupied())
        {
            this.rehashAndGrow();
        }
    }

    private void rehashAndGrow()
    {
        int max = this.maxOccupied();
        int newCapacity = Math.max(max, smallestPowerOfTwoGreaterThan((this.size + 1) << 1));
        if (this.occupiedWithSentinels > 0 && (max >> 1) + (max >> 2) < this.size)
        {
            newCapacity <<= 1;
        }
        this.rehash(newCapacity);
    }

    private void rehash(int newCapacity)
    {
        Object[] oldEntries = this.entries;
        int oldEntryCount = this.entryCount;

        this.indices = new int[newCapacity];
        Arrays.fill(this.indices, EMPTY);
        this.entries = new Object[newCapacity];
        this.entryCount = 0;
        this.occupiedWithSentinels = 0;

        int newIndex = 0;
        for (int i = 0; i < (oldEntryCount << 1); i += 2)
        {
            if (isNonSentinel(oldEntries[i]))
            {
                Object key = oldEntries[i];
                Object value = oldEntries[i + 1];

                int slot = this.probeForRehash(this.toNonSentinel(key));
                this.indices[slot] = newIndex;
                this.entries[newIndex << 1] = key;
                this.entries[(newIndex << 1) + 1] = value;
                newIndex++;
            }
        }
        this.entryCount = newIndex;
    }

    private int probeForRehash(Object key)
    {
        int slot = this.spread(key);
        if (this.indices[slot] == EMPTY)
        {
            return slot;
        }
        int probe = 17;
        while (true)
        {
            slot = (slot + probe) & (this.indices.length - 1);
            probe += 17;
            if (this.indices[slot] == EMPTY)
            {
                return slot;
            }
        }
    }

    @Override
    public V get(Object key)
    {
        int slot = this.probeIndex(key);
        int idx = this.indices[slot];
        if (idx >= 0)
        {
            return (V) this.entries[(idx << 1) + 1];
        }
        return null;
    }

    @Override
    public V put(K key, V value)
    {
        int slot = this.probeIndex(key);
        int idx = this.indices[slot];

        if (idx >= 0)
        {
            V oldValue = (V) this.entries[(idx << 1) + 1];
            this.entries[(idx << 1) + 1] = value;
            return oldValue;
        }

        this.addKeyValueAtSlot(key, value, slot);
        return null;
    }

    @Override
    public V removeKey(K key)
    {
        int slot = this.probeIndex(key);
        int idx = this.indices[slot];

        if (idx >= 0)
        {
            V oldValue = (V) this.entries[(idx << 1) + 1];
            this.indices[slot] = REMOVED;
            this.entries[idx << 1] = REMOVED_KEY;
            this.entries[(idx << 1) + 1] = null;
            this.size--;
            this.occupiedWithSentinels++;
            this.reclaimTrailingTombstones();
            return oldValue;
        }

        return null;
    }

    private void reclaimTrailingTombstones()
    {
        while (this.entryCount > 0 && isRemovedKey(this.entries[(this.entryCount - 1) << 1]))
        {
            this.entryCount--;
            this.entries[this.entryCount << 1] = null;
            this.entries[(this.entryCount << 1) + 1] = null;
        }
    }

    @Override
    public V remove(Object key)
    {
        return this.removeKey((K) key);
    }

    @Override
    public boolean containsKey(Object key)
    {
        int slot = this.probeIndex(key);
        return this.indices[slot] >= 0;
    }

    @Override
    public boolean containsValue(Object value)
    {
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                Object entryValue = this.entries[i + 1];
                if (value == entryValue || (value != null && value.equals(entryValue)))
                {
                    return true;
                }
                count++;
            }
        }
        return false;
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public boolean isEmpty()
    {
        return this.size == 0;
    }

    @Override
    public void clear()
    {
        Arrays.fill(this.indices, EMPTY);
        Arrays.fill(this.entries, 0, this.entryCount << 1, null);
        this.size = 0;
        this.entryCount = 0;
        this.occupiedWithSentinels = 0;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet())
        {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean removeIf(Predicate2<? super K, ? super V> predicate)
    {
        boolean changed = false;
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                K key = this.toNonSentinel(this.entries[i]);
                V value = (V) this.entries[i + 1];
                if (predicate.accept(key, value))
                {
                    this.removeKey(key);
                    changed = true;
                }
                else
                {
                    count++;
                }
            }
        }
        return changed;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = this.get(key);
        V newValue;
        if (oldValue == null)
        {
            newValue = value;
        }
        else
        {
            newValue = remappingFunction.apply(oldValue, value);
        }
        if (newValue == null)
        {
            this.removeKey(key);
        }
        else
        {
            this.put(key, newValue);
        }
        return newValue;
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                procedure.value(this.toNonSentinel(this.entries[i]), (V) this.entries[i + 1]);
                count++;
            }
        }
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                procedure.value(this.toNonSentinel(this.entries[i]));
                count++;
            }
        }
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                procedure.value((V) this.entries[i + 1]);
                count++;
            }
        }
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                objectIntProcedure.value((V) this.entries[i + 1], count);
                count++;
            }
        }
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                procedure.value((V) this.entries[i + 1], parameter);
                count++;
            }
        }
    }

    @Override
    public Iterator<V> iterator()
    {
        return new ValuesIterator();
    }

    private class ValuesIterator implements Iterator<V>
    {
        private int position;
        private int count;
        private int lastPosition = -1;

        @Override
        public boolean hasNext()
        {
            return this.count < OrderedHashMap.this.size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }

            Object[] localEntries = OrderedHashMap.this.entries;
            while (!isNonSentinel(localEntries[this.position]))
            {
                this.position += 2;
            }
            V result = (V) localEntries[this.position + 1];
            this.lastPosition = this.position;
            this.count++;
            this.position += 2;
            return result;
        }

        @Override
        public void remove()
        {
            if (this.lastPosition == -1)
            {
                throw new IllegalStateException();
            }
            K key = OrderedHashMap.this.toNonSentinel(OrderedHashMap.this.entries[this.lastPosition]);
            OrderedHashMap.this.removeKey(key);
            this.count--;
            this.lastPosition = -1;
        }
    }

    @Override
    public MutableSet<K> keySet()
    {
        return SetAdapter.adapt(new AbstractSet<K>()
        {
            @Override
            public int size()
            {
                return OrderedHashMap.this.size;
            }

            @Override
            public boolean contains(Object o)
            {
                return OrderedHashMap.this.containsKey(o);
            }

            @Override
            public boolean remove(Object o)
            {
                int oldSize = OrderedHashMap.this.size;
                OrderedHashMap.this.removeKey((K) o);
                return OrderedHashMap.this.size != oldSize;
            }

            @Override
            public Iterator<K> iterator()
            {
                return new Iterator<K>()
                {
                    private int position;
                    private int count;
                    private int lastPosition = -1;

                    @Override
                    public boolean hasNext()
                    {
                        return this.count < OrderedHashMap.this.size;
                    }

                    @Override
                    public K next()
                    {
                        if (!this.hasNext())
                        {
                            throw new NoSuchElementException();
                        }
                        Object[] localEntries = OrderedHashMap.this.entries;
                        while (!isNonSentinel(localEntries[this.position]))
                        {
                            this.position += 2;
                        }
                        K result = OrderedHashMap.this.toNonSentinel(localEntries[this.position]);
                        this.lastPosition = this.position;
                        this.count++;
                        this.position += 2;
                        return result;
                    }

                    @Override
                    public void remove()
                    {
                        if (this.lastPosition == -1)
                        {
                            throw new IllegalStateException();
                        }
                        K key = OrderedHashMap.this.toNonSentinel(OrderedHashMap.this.entries[this.lastPosition]);
                        OrderedHashMap.this.removeKey(key);
                        this.count--;
                        this.lastPosition = -1;
                    }
                };
            }

            @Override
            public void clear()
            {
                OrderedHashMap.this.clear();
            }
        });
    }

    @Override
    public MutableSet<Map.Entry<K, V>> entrySet()
    {
        return SetAdapter.adapt(new AbstractSet<Map.Entry<K, V>>()
        {
            @Override
            public int size()
            {
                return OrderedHashMap.this.size;
            }

            @Override
            public boolean contains(Object o)
            {
                if (!(o instanceof Map.Entry))
                {
                    return false;
                }
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                Object key = entry.getKey();
                if (!OrderedHashMap.this.containsKey(key))
                {
                    return false;
                }
                V value = OrderedHashMap.this.get(key);
                return Objects.equals(value, entry.getValue());
            }

            @Override
            public boolean remove(Object o)
            {
                if (this.contains(o))
                {
                    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                    OrderedHashMap.this.removeKey((K) entry.getKey());
                    return true;
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<K, V>> iterator()
            {
                return new Iterator<Map.Entry<K, V>>()
                {
                    private int position;
                    private int count;

                    @Override
                    public boolean hasNext()
                    {
                        return this.count < OrderedHashMap.this.size;
                    }

                    @Override
                    public Map.Entry<K, V> next()
                    {
                        if (!this.hasNext())
                        {
                            throw new NoSuchElementException();
                        }
                        Object[] localEntries = OrderedHashMap.this.entries;
                        while (!isNonSentinel(localEntries[this.position]))
                        {
                            this.position += 2;
                        }
                        K key = OrderedHashMap.this.toNonSentinel(localEntries[this.position]);
                        V value = (V) localEntries[this.position + 1];
                        this.count++;
                        this.position += 2;
                        return new AbstractImmutableEntry<K, V>(key, value)
                        {
                            @Override
                            public V setValue(V newValue)
                            {
                                OrderedHashMap.this.put(this.key, newValue);
                                return value;
                            }

                            @Override
                            public boolean equals(Object object)
                            {
                                if (object instanceof Map.Entry)
                                {
                                    Map.Entry<?, ?> that = (Map.Entry<?, ?>) object;
                                    return Objects.equals(this.key, that.getKey())
                                            && Objects.equals(this.value, that.getValue());
                                }
                                return false;
                            }

                            @Override
                            public int hashCode()
                            {
                                return (this.key == null ? 0 : this.key.hashCode())
                                        ^ (this.value == null ? 0 : this.value.hashCode());
                            }
                        };
                    }
                };
            }

            @Override
            public void clear()
            {
                OrderedHashMap.this.clear();
            }
        });
    }

    @Override
    public MutableCollection<V> values()
    {
        return CollectionAdapter.adapt(new AbstractCollection<V>()
        {
            @Override
            public int size()
            {
                return OrderedHashMap.this.size;
            }

            @Override
            public boolean contains(Object o)
            {
                return OrderedHashMap.this.containsValue(o);
            }

            @Override
            public Iterator<V> iterator()
            {
                return OrderedHashMap.this.iterator();
            }

            @Override
            public void clear()
            {
                OrderedHashMap.this.clear();
            }
        });
    }

    @Override
    public RichIterable<K> keysView()
    {
        return LazyIterate.adapt(this.keySet());
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return LazyIterate.adapt(this.values());
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return LazyIterate.adapt(this.entrySet()).collect(AbstractImmutableEntry.getPairFunction());
    }

    @Override
    public MutableOrderedMap<K, V> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        MutableOrderedMap<K, V> output = this.newEmpty();
        int taken = 0;
        int i = 0;
        while (taken < count && taken < this.size)
        {
            if (isNonSentinel(this.entries[i]))
            {
                output.put(this.toNonSentinel(this.entries[i]), (V) this.entries[i + 1]);
                taken++;
            }
            i += 2;
        }
        return output;
    }

    @Override
    public MutableOrderedMap<K, V> drop(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        MutableOrderedMap<K, V> output = this.newEmpty();
        int start = Math.min(count, this.size);
        int skipped = 0;
        for (int i = 0; skipped < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                if (skipped >= start)
                {
                    output.put(this.toNonSentinel(this.entries[i]), (V) this.entries[i + 1]);
                }
                skipped++;
            }
        }
        return output;
    }

    @Override
    public MutableOrderedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        MutableOrderedMap<K, V> output = this.newEmpty();
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                V value = (V) this.entries[i + 1];
                if (!predicate.accept(value))
                {
                    break;
                }
                output.put(this.toNonSentinel(this.entries[i]), value);
                count++;
            }
        }
        return output;
    }

    @Override
    public MutableOrderedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        MutableOrderedMap<K, V> output = this.newEmpty();
        boolean dropping = true;
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                V value = (V) this.entries[i + 1];
                if (dropping)
                {
                    if (!predicate.accept(value))
                    {
                        dropping = false;
                        output.put(this.toNonSentinel(this.entries[i]), value);
                    }
                }
                else
                {
                    output.put(this.toNonSentinel(this.entries[i]), value);
                }
                count++;
            }
        }
        return output;
    }

    @Override
    public PartitionMutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        PartitionMutableList<V> result = new PartitionFastList<>();
        boolean selecting = true;
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                V value = (V) this.entries[i + 1];
                if (selecting)
                {
                    if (predicate.accept(value))
                    {
                        result.getSelected().add(value);
                    }
                    else
                    {
                        selecting = false;
                        result.getRejected().add(value);
                    }
                }
                else
                {
                    result.getRejected().add(value);
                }
                count++;
            }
        }
        return result;
    }

    @Override
    public MutableOrderedMap<K, V> toReversed()
    {
        MutableOrderedMap<K, V> result = new OrderedHashMap<>(this.size);
        int count = 0;
        for (int i = (this.entryCount - 1) << 1; count < this.size && i >= 0; i -= 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                result.put(this.toNonSentinel(this.entries[i]), (V) this.entries[i + 1]);
                count++;
            }
        }
        return result;
    }

    @Override
    public MutableList<V> distinct()
    {
        MutableList<V> result = Lists.mutable.empty();
        MutableSet<V> seen = Sets.mutable.empty();
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                V value = (V) this.entries[i + 1];
                if (seen.add(value))
                {
                    result.add(value);
                }
                count++;
            }
        }
        return result;
    }

    @Override
    public int detectIndex(Predicate<? super V> predicate)
    {
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                if (predicate.accept((V) this.entries[i + 1]))
                {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    @Override
    public int detectLastIndex(Predicate<? super V> predicate)
    {
        int lastIndex = -1;
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                if (predicate.accept((V) this.entries[i + 1]))
                {
                    lastIndex = count;
                }
                count++;
            }
        }
        return lastIndex;
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super V, ? super S> predicate)
    {
        if (this.size() != other.size())
        {
            return false;
        }
        Iterator<S> otherIterator = other.iterator();
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                if (!predicate.accept((V) this.entries[i + 1], otherIterator.next()))
                {
                    return false;
                }
                count++;
            }
        }
        return true;
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super V> procedure)
    {
        int fromIndex = Math.min(startIndex, endIndex);
        int toIndex = Math.max(startIndex, endIndex);

        if (fromIndex < 0 || toIndex >= this.size)
        {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + " toIndex: " + toIndex + " size: " + this.size);
        }

        if (startIndex <= endIndex)
        {
            int count = 0;
            for (int i = 0; count <= toIndex; i += 2)
            {
                if (isNonSentinel(this.entries[i]))
                {
                    if (count >= fromIndex)
                    {
                        procedure.value((V) this.entries[i + 1]);
                    }
                    count++;
                }
            }
        }
        else
        {
            this.forEachInReverse(startIndex, endIndex, procedure);
        }
    }

    private void forEachInReverse(int fromIndex, int toIndex, Procedure<? super V> procedure)
    {
        MutableList<V> collected = Lists.mutable.withInitialCapacity(fromIndex - toIndex + 1);
        int count = 0;
        for (int i = 0; count <= fromIndex; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                if (count >= toIndex)
                {
                    collected.add((V) this.entries[i + 1]);
                }
                count++;
            }
        }
        for (int i = collected.size() - 1; i >= 0; i--)
        {
            procedure.value(collected.get(i));
        }
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super V> objectIntProcedure)
    {
        int lower = Math.min(fromIndex, toIndex);
        int upper = Math.max(fromIndex, toIndex);

        if (lower < 0 || upper >= this.size)
        {
            throw new IndexOutOfBoundsException("fromIndex: " + lower + " toIndex: " + upper + " size: " + this.size);
        }

        if (fromIndex <= toIndex)
        {
            int count = 0;
            for (int i = 0; count <= toIndex; i += 2)
            {
                if (isNonSentinel(this.entries[i]))
                {
                    if (count >= fromIndex)
                    {
                        objectIntProcedure.value((V) this.entries[i + 1], count);
                    }
                    count++;
                }
            }
        }
        else
        {
            MutableList<V> collected = Lists.mutable.withInitialCapacity(fromIndex - toIndex + 1);
            int count = 0;
            for (int i = 0; count <= fromIndex; i += 2)
            {
                if (isNonSentinel(this.entries[i]))
                {
                    if (count >= toIndex)
                    {
                        collected.add((V) this.entries[i + 1]);
                    }
                    count++;
                }
            }
            int logicalIndex = fromIndex;
            for (int i = collected.size() - 1; i >= 0; i--)
            {
                objectIntProcedure.value(collected.get(i), logicalIndex);
                logicalIndex--;
            }
        }
    }

    @Override
    public MutableStack<V> toStack()
    {
        return ArrayStack.newStackFromTopToBottom(this);
    }

    @Override
    public MutableOrderedMap<K, V> newEmpty()
    {
        return new OrderedHashMap<>();
    }

    @Override
    public <R> MutableList<R> collect(Function<? super V, ? extends R> function)
    {
        return this.collect(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <P, VV> MutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    @Override
    public <R> MutableList<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function)
    {
        return this.collectIf(predicate, function, Lists.mutable.empty());
    }

    @Override
    public <R> MutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.flatCollect(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableList<V> select(Predicate<? super V> predicate)
    {
        return this.select(predicate, Lists.mutable.empty());
    }

    @Override
    public MutableList<V> reject(Predicate<? super V> predicate)
    {
        return this.reject(predicate, Lists.mutable.empty());
    }

    @Override
    public <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public PartitionMutableList<V> partition(Predicate<? super V> predicate)
    {
        PartitionMutableList<V> partitionMutableList = new PartitionFastList<>();
        this.forEach(new PartitionProcedure<>(predicate, partitionMutableList));
        return partitionMutableList;
    }

    @Override
    public <P> PartitionMutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        PartitionMutableList<V> partitionMutableList = new PartitionFastList<>();
        this.forEach(new PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableList));
        return partitionMutableList;
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        MutableList<S> result = Lists.mutable.withInitialCapacity(this.size());
        this.forEach(new SelectInstancesOfProcedure<>(clazz, result));
        return result;
    }

    @Override
    public <S> MutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.zip(that, Lists.mutable.withInitialCapacity(Math.min(this.size(), Iterate.sizeOf(that))));
    }

    @Override
    public MutableList<Pair<V, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.collectBoolean(booleanFunction, BooleanLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.collectByte(byteFunction, ByteLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        return this.collectChar(charFunction, CharLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.collectDouble(doubleFunction, DoubleLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.collectFloat(floatFunction, FloatLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        return this.collectInt(intFunction, IntLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        return this.collectLong(longFunction, LongLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.collectShort(shortFunction, ShortLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <VV> MutableListMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function)
    {
        return this.groupBy(function, FastListMultimap.newMultimap());
    }

    @Override
    public <VV> MutableListMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.groupByEach(function, FastListMultimap.newMultimap());
    }

    @Override
    public MutableListMultimap<V, K> flip()
    {
        MutableListMultimap<V, K> result = FastListMultimap.newMultimap();
        this.forEachKeyValue((key, value) -> result.put(value, key));
        return result;
    }

    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            result = function.value();
            this.put(key, result);
        }
        return result;
    }

    @Override
    public V getIfAbsentPut(K key, V value)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            result = value;
            this.put(key, result);
        }
        return result;
    }

    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            result = function.valueOf(parameter);
            this.put(key, result);
        }
        return result;
    }

    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        V oldValue = this.getIfAbsent(key, factory);
        V newValue = function.valueOf(oldValue);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    public <P> V updateValueWith(K key, Function0<? extends V> factory, Function2<? super V, ? super P, ? extends V> function, P parameter)
    {
        V oldValue = this.getIfAbsent(key, factory);
        V newValue = function.value(oldValue, parameter);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                K key = this.toNonSentinel(this.entries[i]);
                V value = (V) this.entries[i + 1];
                if (predicate.accept(key, value))
                {
                    return Tuples.pair(key, value);
                }
                count++;
            }
        }
        return null;
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                K key = this.toNonSentinel(this.entries[i]);
                V value = (V) this.entries[i + 1];
                if (predicate.accept(key, value))
                {
                    return Optional.of(Tuples.pair(key, value));
                }
                count++;
            }
        }
        return Optional.empty();
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        MutableObjectLongMap<V1> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function));
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        MutableObjectDoubleMap<V1> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function));
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        MutableObjectLongMap<V1> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function));
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        MutableObjectDoubleMap<V1> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function));
    }

    @Override
    public <V1> MutableBag<V1> countByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return this.flatCollect(function, Bags.mutable.empty());
    }

    @Override
    public MutableOrderedMap<K, V> asUnmodifiable()
    {
        return UnmodifiableMutableOrderedMap.of(this);
    }

    @Override
    public ImmutableOrderedMap<K, V> toImmutable()
    {
        return new ImmutableOrderedMapAdapter<>(this);
    }

    @Override
    public MutableOrderedMap<K, V> asSynchronized()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".asSynchronized() not implemented yet");
    }

    @Override
    public MutableOrderedMap<K, V> clone()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".clone() not implemented yet");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.size);
        this.forEachKeyValue((key, value) ->
        {
            try
            {
                out.writeObject(key);
                out.writeObject(value);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int deserializedSize = in.readInt();
        int capacity = smallestPowerOfTwoGreaterThan(Math.max(deserializedSize << 1, DEFAULT_INITIAL_CAPACITY << 1));
        this.allocate(capacity);
        for (int i = 0; i < deserializedSize; i++)
        {
            K key = (K) in.readObject();
            V value = (V) in.readObject();
            this.put(key, value);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Map))
        {
            return false;
        }

        Map<?, ?> other = (Map<?, ?>) o;
        if (this.size() != other.size())
        {
            return false;
        }

        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                K key = this.toNonSentinel(this.entries[i]);
                V value = (V) this.entries[i + 1];
                if (!this.keyAndValueEquals(key, value, (Map<K, V>) other))
                {
                    return false;
                }
                count++;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                K key = this.toNonSentinel(this.entries[i]);
                V value = (V) this.entries[i + 1];
                hash += this.keyAndValueHashCode(key, value);
                count++;
            }
        }
        return hash;
    }

    @Override
    public String toString()
    {
        if (this.isEmpty())
        {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        int count = 0;
        for (int i = 0; count < this.size; i += 2)
        {
            if (isNonSentinel(this.entries[i]))
            {
                if (!first)
                {
                    sb.append(", ");
                }
                K key = this.toNonSentinel(this.entries[i]);
                V value = (V) this.entries[i + 1];
                sb.append(key == this ? "(this Map)" : key);
                sb.append('=');
                sb.append(value == this ? "(this Map)" : value);
                first = false;
                count++;
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
