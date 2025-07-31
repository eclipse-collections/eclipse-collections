/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.Function3;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.MapEntryToProcedure2;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.MapIterate;
import org.eclipse.collections.impl.utility.internal.IterableIterate;
import sun.misc.Unsafe;

@SuppressWarnings("UseOfSunClasses")
public class ConcurrentHashMapUnsafe<K, V>
        extends AbstractMutableMap<K, V>
        implements ConcurrentMutableMap<K, V>, Externalizable
{
    private static final long serialVersionUID = 1L;

    private static final Object RESIZE_SENTINEL = new Object();
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    @SuppressWarnings("rawtypes")
    private static final AtomicReferenceFieldUpdater<ConcurrentHashMapUnsafe, Object[]> TABLE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(ConcurrentHashMapUnsafe.class, Object[].class, "table");
    private static final Object RESIZED = new Object();
    private static final Object RESIZING = new Object();
    private static final int PARTITIONED_SIZE_THRESHOLD = 4096; // chosen to keep size below 1% of the total size of the map

    private static final Unsafe UNSAFE;
    private static final long OBJECT_ARRAY_BASE;
    private static final int OBJECT_ARRAY_SHIFT;
    private static final long INT_ARRAY_BASE;
    private static final int INT_ARRAY_SHIFT;
    private static final long SIZE_OFFSET;
    private static final int SIZE_BUCKETS = 7;

    static
    {
        try
        {
            UNSAFE = ConcurrentHashMapUnsafe.getUnsafe();
            Class<?> objectArrayClass = Object[].class;
            OBJECT_ARRAY_BASE = UNSAFE.arrayBaseOffset(objectArrayClass);
            int objectArrayScale = UNSAFE.arrayIndexScale(objectArrayClass);
            if ((objectArrayScale & (objectArrayScale - 1)) != 0)
            {
                throw new AssertionError("data type scale not a power of two");
            }
            OBJECT_ARRAY_SHIFT = 31 - Integer.numberOfLeadingZeros(objectArrayScale);

            Class<?> intArrayClass = int[].class;
            INT_ARRAY_BASE = UNSAFE.arrayBaseOffset(intArrayClass);
            int intArrayScale = UNSAFE.arrayIndexScale(intArrayClass);
            if ((intArrayScale & (intArrayScale - 1)) != 0)
            {
                throw new AssertionError("data type scale not a power of two");
            }
            INT_ARRAY_SHIFT = 31 - Integer.numberOfLeadingZeros(intArrayScale);

            Class<?> mapClass = ConcurrentHashMapUnsafe.class;
            SIZE_OFFSET = UNSAFE.objectFieldOffset(mapClass.getDeclaredField("size"));
        }
        catch (NoSuchFieldException | SecurityException e)
        {
            throw new AssertionError(e);
        }
    }

    /**
     * The table, resized as necessary. Length MUST Always be a power of two + 1.
     */
    private volatile Object[] table;

    private int[] partitionedSize;

    @SuppressWarnings("UnusedDeclaration")
    private volatile int size; // updated via atomic field updater

    public ConcurrentHashMapUnsafe()
    {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ConcurrentHashMapUnsafe(int initialCapacity)
    {
        if (initialCapacity < 0)
        {
            throw new IllegalArgumentException("Illegal Initial Capacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY)
        {
            initialCapacity = MAXIMUM_CAPACITY;
        }

        int threshold = initialCapacity;
        threshold += threshold >> 1; // threshold = length * 0.75

        int capacity = 1;
        while (capacity < threshold)
        {
            capacity <<= 1;
        }
        if (capacity >= PARTITIONED_SIZE_THRESHOLD)
        {
            this.partitionedSize = new int[SIZE_BUCKETS * 16]; // we want 7 extra slots and 64 bytes for each slot. int is 4 bytes, so 64 bytes is 16 ints.
        }
        this.table = new Object[capacity + 1];
    }

    public static <K, V> ConcurrentHashMapUnsafe<K, V> newMap()
    {
        return new ConcurrentHashMapUnsafe<>();
    }

    public static <K, V> ConcurrentHashMapUnsafe<K, V> newMap(int newSize)
    {
        return new ConcurrentHashMapUnsafe<>(newSize);
    }

    private static Object arrayAt(Object[] array, int index)
    {
        return UNSAFE.getObjectVolatile(array, ((long) index << OBJECT_ARRAY_SHIFT) + OBJECT_ARRAY_BASE);
    }

    private static boolean casArrayAt(Object[] array, int index, Object expected, Object newValue)
    {
        return UNSAFE.compareAndSwapObject(array, ((long) index << OBJECT_ARRAY_SHIFT) + OBJECT_ARRAY_BASE, expected, newValue);
    }

    private static void setArrayAt(Object[] array, int index, Object newValue)
    {
        UNSAFE.putObjectVolatile(array, ((long) index << OBJECT_ARRAY_SHIFT) + OBJECT_ARRAY_BASE, newValue);
    }

    private static int indexFor(int h, int length)
    {
        return h & (length - 2);
    }

    @Override
    public V putIfAbsent(K key, V value)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    K candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        return e.getValue();
                    }
                    e = e.getNext();
                }
                Entry<K, V> newEntry = new Entry<>(key, value, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return null; // per the contract of putIfAbsent, we return null when the map didn't have this key before
                }
            }
        }
    }

    private void incrementSizeAndPossiblyResize(Object[] currentArray, int length, Object prev)
    {
        this.addToSize(1);
        if (prev != null)
        {
            int localSize = this.size();
            int threshold = (length >> 1) + (length >> 2); // threshold = length * 0.75
            if (localSize + 1 > threshold)
            {
                this.resize(currentArray);
            }
        }
    }

    private int hash(Object key)
    {
        int h = key.hashCode();
        h ^= (h >>> 18) ^ (h >>> 12);
        return h ^ (h >>> 10);
    }

    private Object[] helpWithResizeWhileCurrentIndex(Object[] currentArray, int index)
    {
        Object[] newArray = this.helpWithResize(currentArray);
        int helpCount = 0;
        while (ConcurrentHashMapUnsafe.arrayAt(currentArray, index) != RESIZED)
        {
            helpCount++;
            newArray = this.helpWithResize(currentArray);
            if ((helpCount & 7) == 0)
            {
                Thread.yield();
            }
        }
        return newArray;
    }

    private Object[] helpWithResize(Object[] currentArray)
    {
        ResizeContainer resizeContainer = (ResizeContainer) ConcurrentHashMapUnsafe.arrayAt(currentArray, currentArray.length - 1);
        Object[] newTable = resizeContainer.nextArray;
        if (resizeContainer.getQueuePosition() > ResizeContainer.QUEUE_INCREMENT)
        {
            resizeContainer.incrementResizer();
            this.reverseTransfer(currentArray, resizeContainer);
            resizeContainer.decrementResizerAndNotify();
        }
        return newTable;
    }

    private void resize(Object[] oldTable)
    {
        this.resize(oldTable, (oldTable.length - 1 << 1) + 1);
    }

    // newSize must be a power of 2 + 1
    @SuppressWarnings("JLM_JSR166_UTILCONCURRENT_MONITORENTER")
    private void resize(Object[] oldTable, int newSize)
    {
        int oldCapacity = oldTable.length;
        int end = oldCapacity - 1;
        Object last = ConcurrentHashMapUnsafe.arrayAt(oldTable, end);
        if (this.size() < end && last == RESIZE_SENTINEL)
        {
            return;
        }
        if (oldCapacity >= MAXIMUM_CAPACITY)
        {
            throw new RuntimeException("max capacity of map exceeded");
        }
        ResizeContainer resizeContainer = null;
        boolean ownResize = false;
        if (last == null || last == RESIZE_SENTINEL)
        {
            synchronized (oldTable) // allocating a new array is too expensive to make this an atomic operation
            {
                if (ConcurrentHashMapUnsafe.arrayAt(oldTable, end) == null)
                {
                    ConcurrentHashMapUnsafe.setArrayAt(oldTable, end, RESIZE_SENTINEL);
                    if (this.partitionedSize == null && newSize >= PARTITIONED_SIZE_THRESHOLD)
                    {
                        this.partitionedSize = new int[SIZE_BUCKETS * 16];
                    }
                    resizeContainer = new ResizeContainer(new Object[newSize], oldTable.length - 1);
                    ConcurrentHashMapUnsafe.setArrayAt(oldTable, end, resizeContainer);
                    ownResize = true;
                }
            }
        }
        if (ownResize)
        {
            this.transfer(oldTable, resizeContainer);

            Object[] src = this.table;
            while (!TABLE_UPDATER.compareAndSet(this, oldTable, resizeContainer.nextArray))
            {
                // we're in a double resize situation; we'll have to go help until it's our turn to set the table
                if (src != oldTable)
                {
                    this.helpWithResize(src);
                }
            }
        }
        else
        {
            this.helpWithResize(oldTable);
        }
    }

    /*
     * Transfer all entries from src to dest tables
     */
    private void transfer(Object[] src, ResizeContainer resizeContainer)
    {
        Object[] dest = resizeContainer.nextArray;

        for (int j = 0; j < src.length - 1; )
        {
            Object o = ConcurrentHashMapUnsafe.arrayAt(src, j);
            if (o == null)
            {
                if (ConcurrentHashMapUnsafe.casArrayAt(src, j, null, RESIZED))
                {
                    j++;
                }
            }
            else if (o == RESIZED || o == RESIZING)
            {
                j = (j & ~(ResizeContainer.QUEUE_INCREMENT - 1)) + ResizeContainer.QUEUE_INCREMENT;
                if (resizeContainer.resizers.get() == 1)
                {
                    break;
                }
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                if (ConcurrentHashMapUnsafe.casArrayAt(src, j, o, RESIZING))
                {
                    while (e != null)
                    {
                        this.unconditionalCopy(dest, e);
                        e = e.getNext();
                    }
                    ConcurrentHashMapUnsafe.setArrayAt(src, j, RESIZED);
                    j++;
                }
            }
        }
        resizeContainer.decrementResizerAndNotify();
        resizeContainer.waitForAllResizers();
    }

    private void reverseTransfer(Object[] src, ResizeContainer resizeContainer)
    {
        Object[] dest = resizeContainer.nextArray;
        while (resizeContainer.getQueuePosition() > 0)
        {
            int start = resizeContainer.subtractAndGetQueuePosition();
            int end = start + ResizeContainer.QUEUE_INCREMENT;
            if (end > 0)
            {
                if (start < 0)
                {
                    start = 0;
                }
                for (int j = end - 1; j >= start; )
                {
                    Object o = ConcurrentHashMapUnsafe.arrayAt(src, j);
                    if (o == null)
                    {
                        if (ConcurrentHashMapUnsafe.casArrayAt(src, j, null, RESIZED))
                        {
                            j--;
                        }
                    }
                    else if (o == RESIZED || o == RESIZING)
                    {
                        resizeContainer.zeroOutQueuePosition();
                        return;
                    }
                    else
                    {
                        Entry<K, V> e = (Entry<K, V>) o;
                        if (ConcurrentHashMapUnsafe.casArrayAt(src, j, o, RESIZING))
                        {
                            while (e != null)
                            {
                                this.unconditionalCopy(dest, e);
                                e = e.getNext();
                            }
                            ConcurrentHashMapUnsafe.setArrayAt(src, j, RESIZED);
                            j--;
                        }
                    }
                }
            }
        }
    }

    private void unconditionalCopy(Object[] dest, Entry<K, V> toCopyEntry)
    {
        int hash = this.hash(toCopyEntry.getKey());
        Object[] currentArray = dest;
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = ((ResizeContainer) ConcurrentHashMapUnsafe.arrayAt(currentArray, length - 1)).nextArray;
            }
            else
            {
                Entry<K, V> newEntry;
                if (o == null)
                {
                    if (toCopyEntry.getNext() == null)
                    {
                        newEntry = toCopyEntry; // no need to duplicate
                    }
                    else
                    {
                        newEntry = new Entry<>(toCopyEntry.getKey(), toCopyEntry.getValue());
                    }
                }
                else
                {
                    newEntry = new Entry<>(toCopyEntry.getKey(), toCopyEntry.getValue(), (Entry<K, V>) o);
                }
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    return;
                }
            }
        }
    }

    public int countEntries()
    {
        int count = 0;
        Object[] currentArray = this.table;
        for (int i = 0; i < currentArray.length - 1; i++)
        {
            Object o = currentArray[i];
            if (o instanceof Entry)
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    count++;
                    e = e.getNext();
                }
            }
        }
        return count;
    }

    public V getIfAbsentPut(K key, Function<? super K, ? extends V> factory)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        V newValue = null;
        boolean createdValue = false;
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate == key || candidate.equals(key))
                    {
                        return e.getValue();
                    }
                    e = e.getNext();
                }
                if (!createdValue)
                {
                    createdValue = true;
                    newValue = factory.valueOf(key);
                }
                Entry<K, V> newEntry = new Entry<>(key, newValue, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return newValue;
                }
            }
        }
    }

    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> factory)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        V newValue = null;
        boolean createdValue = false;
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        return e.getValue();
                    }
                    e = e.getNext();
                }
                if (!createdValue)
                {
                    createdValue = true;
                    newValue = factory.value();
                }
                Entry<K, V> newEntry = new Entry<>(key, newValue, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return newValue;
                }
            }
        }
    }

    @Override
    public V getIfAbsentPut(K key, V value)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        return e.getValue();
                    }
                    e = e.getNext();
                }
                Entry<K, V> newEntry = new Entry<>(key, value, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return value;
                }
            }
        }
    }

    /**
     * It puts an object into the map based on the key. It uses a copy of the key converted by transformer.
     *
     * @param key            The "mutable" key, which has the same identity/hashcode as the inserted key, only during this call
     * @param keyTransformer If the record is absent, the transformer will transform the "mutable" key into an immutable copy of the key.
     *                       Note that the transformed key must have the same identity/hashcode as the original "mutable" key.
     * @param factory        It creates an object, if it is not present in the map already.
     */
    public <P1, P2> V putIfAbsentGetIfPresent(K key, Function2<? super K, ? super V, ? extends K> keyTransformer, Function3<P1, P2, ? super K, ? extends V> factory, P1 param1, P2 param2)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        V newValue = null;
        boolean createdValue = false;
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        return e.getValue();
                    }
                    e = e.getNext();
                }
                if (!createdValue)
                {
                    createdValue = true;
                    newValue = factory.value(param1, param2, key);
                    if (newValue == null)
                    {
                        return null; // null value means no mapping is required
                    }
                    key = keyTransformer.value(key, newValue);
                }
                Entry<K, V> newEntry = new Entry<>(key, newValue, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return null;
                }
            }
        }
    }

    @Override
    public boolean remove(Object key, Object value)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        //noinspection LabeledStatement
        outer:
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key) && this.nullSafeEquals(e.getValue(), value))
                    {
                        Entry<K, V> replacement = this.createReplacementChainForRemoval((Entry<K, V>) o, e);
                        if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, replacement))
                        {
                            this.addToSize(-1);
                            return true;
                        }
                        //noinspection ContinueStatementWithLabel
                        continue outer;
                    }
                    e = e.getNext();
                }
                return false;
            }
        }
    }

    private void addToSize(int value)
    {
        if (this.partitionedSize != null)
        {
            if (this.incrementPartitionedSize(value))
            {
                return;
            }
        }
        this.incrementLocalSize(value);
    }

    private boolean incrementPartitionedSize(int value)
    {
        int h = (int) Thread.currentThread().getId();
        h ^= (h >>> 18) ^ (h >>> 12);
        h = (h ^ (h >>> 10)) & SIZE_BUCKETS;
        if (h != 0)
        {
            h = (h - 1) << 4;
            long address = ((long) h << INT_ARRAY_SHIFT) + INT_ARRAY_BASE;
            while (true)
            {
                int localSize = UNSAFE.getIntVolatile(this.partitionedSize, address);
                if (UNSAFE.compareAndSwapInt(this.partitionedSize, address, localSize, localSize + value))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void incrementLocalSize(int value)
    {
        while (true)
        {
            int localSize = this.size;
            if (UNSAFE.compareAndSwapInt(this, SIZE_OFFSET, localSize, localSize + value))
            {
                break;
            }
        }
    }

    @Override
    public int size()
    {
        int localSize = this.size;
        if (this.partitionedSize != null)
        {
            for (int i = 0; i < SIZE_BUCKETS; i++)
            {
                localSize += this.partitionedSize[i << 4];
            }
        }
        return localSize;
    }

    @Override
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.getEntry(key) != null;
    }

    @Override
    public boolean containsValue(Object value)
    {
        Object[] currentArray = this.table;
        ResizeContainer resizeContainer;
        do
        {
            resizeContainer = null;
            for (int i = 0; i < currentArray.length - 1; i++)
            {
                Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, i);
                if (o == RESIZED || o == RESIZING)
                {
                    resizeContainer = (ResizeContainer) ConcurrentHashMapUnsafe.arrayAt(currentArray, currentArray.length - 1);
                }
                else if (o != null)
                {
                    Entry<K, V> e = (Entry<K, V>) o;
                    while (e != null)
                    {
                        Object v = e.getValue();
                        if (this.nullSafeEquals(v, value))
                        {
                            return true;
                        }
                        e = e.getNext();
                    }
                }
            }
            if (resizeContainer != null)
            {
                if (resizeContainer.isNotDone())
                {
                    this.helpWithResize(currentArray);
                    resizeContainer.waitForAllResizers();
                }
                currentArray = resizeContainer.nextArray;
            }
        }
        while (resizeContainer != null);
        return false;
    }

    private boolean nullSafeEquals(Object v, Object value)
    {
        return v == value || v != null && v.equals(value);
    }

    @Override
    public V get(Object key)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        int index = ConcurrentHashMapUnsafe.indexFor(hash, currentArray.length);
        Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
        if (o == RESIZED || o == RESIZING)
        {
            return this.slowGet(key, hash, index, currentArray);
        }
        for (Entry<K, V> e = (Entry<K, V>) o; e != null; e = e.getNext())
        {
            Object k;
            if ((k = e.key) == key || key.equals(k))
            {
                return e.value;
            }
        }
        return null;
    }

    private V slowGet(Object key, int hash, int index, Object[] currentArray)
    {
        while (true)
        {
            int length = currentArray.length;
            index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        return e.getValue();
                    }
                    e = e.getNext();
                }
                return null;
            }
        }
    }

    private Entry<K, V> getEntry(Object key)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        return e;
                    }
                    e = e.getNext();
                }
                return null;
            }
        }
    }

    @Override
    public V put(K key, V value)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        int length = currentArray.length;
        int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
        Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
        if (o == null)
        {
            Entry<K, V> newEntry = new Entry<>(key, value, null);
            this.addToSize(1);
            if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, null, newEntry))
            {
                return null;
            }
            this.addToSize(-1);
        }
        return this.slowPut(key, value, hash, currentArray);
    }

    private V slowPut(K key, V value, int hash, Object[] currentArray)
    {
        //noinspection LabeledStatement
        outer:
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        V oldValue = e.getValue();
                        Entry<K, V> newEntry = new Entry<>(e.getKey(), value, this.createReplacementChainForRemoval((Entry<K, V>) o, e));
                        if (!ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                        {
                            //noinspection ContinueStatementWithLabel
                            continue outer;
                        }
                        return oldValue;
                    }
                    e = e.getNext();
                }
                Entry<K, V> newEntry = new Entry<>(key, value, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return null;
                }
            }
        }
    }

    public void putAllInParallel(Map<? extends K, ? extends V> map, int chunks, Executor executor)
    {
        if (this.size() == 0)
        {
            int threshold = map.size();
            threshold += threshold >> 1; // threshold = length * 0.75

            int capacity = 1;
            while (capacity < threshold)
            {
                capacity <<= 1;
            }
            this.resize(this.table, capacity + 1);
        }
        if (map instanceof ConcurrentHashMapUnsafe<?, ?> && chunks > 1 && map.size() > 50000)
        {
            ConcurrentHashMapUnsafe<K, V> incoming = (ConcurrentHashMapUnsafe<K, V>) map;
            Object[] currentArray = incoming.table;
            FutureTask<?>[] futures = new FutureTask<?>[chunks];
            int chunkSize = currentArray.length / chunks;
            if (currentArray.length % chunks != 0)
            {
                chunkSize++;
            }
            for (int i = 0; i < chunks; i++)
            {
                int start = i * chunkSize;
                int end = Math.min((i + 1) * chunkSize, currentArray.length);
                futures[i] = new FutureTask<>(() -> this.sequentialPutAll(currentArray, start, end), null);
                executor.execute(futures[i]);
            }
            for (int i = 0; i < chunks; i++)
            {
                try
                {
                    futures[i].get();
                }
                catch (Exception e)
                {
                    throw new RuntimeException("parallelForEachKeyValue failed", e);
                }
            }
        }
        else
        {
            this.putAll(map);
        }
    }

    private void sequentialPutAll(Object[] currentArray, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, i);
            if (o == RESIZED || o == RESIZING)
            {
                throw new ConcurrentModificationException("can't iterate while resizing!");
            }
            Entry<K, V> e = (Entry<K, V>) o;
            while (e != null)
            {
                Object key = e.getKey();
                Object value = e.getValue();
                this.put((K) key, (V) value);
                e = e.getNext();
            }
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        MapIterate.forEachKeyValue(map, this::put);
    }

    @Override
    public void clear()
    {
        Object[] currentArray = this.table;
        ResizeContainer resizeContainer;
        do
        {
            resizeContainer = null;
            for (int i = 0; i < currentArray.length - 1; i++)
            {
                Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, i);
                if (o == RESIZED || o == RESIZING)
                {
                    resizeContainer = (ResizeContainer) ConcurrentHashMapUnsafe.arrayAt(currentArray, currentArray.length - 1);
                }
                else if (o != null)
                {
                    Entry<K, V> e = (Entry<K, V>) o;
                    if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, i, o, null))
                    {
                        int removedEntries = 0;
                        while (e != null)
                        {
                            removedEntries++;
                            e = e.getNext();
                        }
                        this.addToSize(-removedEntries);
                    }
                }
            }
            if (resizeContainer != null)
            {
                if (resizeContainer.isNotDone())
                {
                    this.helpWithResize(currentArray);
                    resizeContainer.waitForAllResizers();
                }
                currentArray = resizeContainer.nextArray;
            }
        }
        while (resizeContainer != null);
    }

    @Override
    public Set<K> keySet()
    {
        return new KeySet();
    }

    @Override
    public Collection<V> values()
    {
        return new Values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return new EntrySet();
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        int length = currentArray.length;
        int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
        Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
        if (o == RESIZED || o == RESIZING)
        {
            return this.slowReplace(key, oldValue, newValue, hash, currentArray);
        }
        Entry<K, V> e = (Entry<K, V>) o;
        while (e != null)
        {
            Object candidate = e.getKey();
            if (candidate == key || candidate.equals(key))
            {
                if (oldValue == e.getValue() || (oldValue != null && oldValue.equals(e.getValue())))
                {
                    Entry<K, V> replacement = this.createReplacementChainForRemoval((Entry<K, V>) o, e);
                    Entry<K, V> newEntry = new Entry<>(key, newValue, replacement);
                    return ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry) || this.slowReplace(key, oldValue, newValue, hash, currentArray);
                }
                return false;
            }
            e = e.getNext();
        }
        return false;
    }

    private boolean slowReplace(K key, V oldValue, V newValue, int hash, Object[] currentArray)
    {
        //noinspection LabeledStatement
        outer:
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate == key || candidate.equals(key))
                    {
                        if (oldValue == e.getValue() || (oldValue != null && oldValue.equals(e.getValue())))
                        {
                            Entry<K, V> replacement = this.createReplacementChainForRemoval((Entry<K, V>) o, e);
                            Entry<K, V> newEntry = new Entry<>(key, newValue, replacement);
                            if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                            {
                                return true;
                            }
                            //noinspection ContinueStatementWithLabel
                            continue outer;
                        }
                        return false;
                    }
                    e = e.getNext();
                }
                return false;
            }
        }
    }

    @Override
    public V replace(K key, V value)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        int length = currentArray.length;
        int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
        Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
        if (o == null)
        {
            return null;
        }
        return this.slowReplace(key, value, hash, currentArray);
    }

    private V slowReplace(K key, V value, int hash, Object[] currentArray)
    {
        //noinspection LabeledStatement
        outer:
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        V oldValue = e.getValue();
                        Entry<K, V> newEntry = new Entry<>(e.getKey(), value, this.createReplacementChainForRemoval((Entry<K, V>) o, e));
                        if (!ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                        {
                            //noinspection ContinueStatementWithLabel
                            continue outer;
                        }
                        return oldValue;
                    }
                    e = e.getNext();
                }
                return null;
            }
        }
    }

    @Override
    public V remove(Object key)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        int length = currentArray.length;
        int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
        Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
        if (o == RESIZED || o == RESIZING)
        {
            return this.slowRemove(key, hash, currentArray);
        }
        Entry<K, V> e = (Entry<K, V>) o;
        while (e != null)
        {
            Object candidate = e.getKey();
            if (candidate == key || candidate.equals(key))
            {
                Entry<K, V> replacement = this.createReplacementChainForRemoval((Entry<K, V>) o, e);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, replacement))
                {
                    this.addToSize(-1);
                    return e.getValue();
                }
                return this.slowRemove(key, hash, currentArray);
            }
            e = e.getNext();
        }
        return null;
    }

    private V slowRemove(Object key, int hash, Object[] currentArray)
    {
        //noinspection LabeledStatement
        outer:
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        Entry<K, V> replacement = this.createReplacementChainForRemoval((Entry<K, V>) o, e);
                        if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, replacement))
                        {
                            this.addToSize(-1);
                            return e.getValue();
                        }
                        //noinspection ContinueStatementWithLabel
                        continue outer;
                    }
                    e = e.getNext();
                }
                return null;
            }
        }
    }

    private Entry<K, V> createReplacementChainForRemoval(Entry<K, V> original, Entry<K, V> toRemove)
    {
        if (original == toRemove)
        {
            return original.getNext();
        }
        Entry<K, V> replacement = null;
        Entry<K, V> e = original;
        while (e != null)
        {
            if (e != toRemove)
            {
                replacement = new Entry<>(e.getKey(), e.getValue(), replacement);
            }
            e = e.getNext();
        }
        return replacement;
    }

    public void parallelForEachKeyValue(List<Procedure2<K, V>> blocks, Executor executor)
    {
        Object[] currentArray = this.table;
        int chunks = blocks.size();
        if (chunks > 1)
        {
            FutureTask<?>[] futures = new FutureTask<?>[chunks];
            int chunkSize = currentArray.length / chunks;
            if (currentArray.length % chunks != 0)
            {
                chunkSize++;
            }
            for (int i = 0; i < chunks; i++)
            {
                int start = i * chunkSize;
                int end = Math.min((i + 1) * chunkSize, currentArray.length);
                Procedure2<K, V> block = blocks.get(i);
                futures[i] = new FutureTask<>(() -> this.sequentialForEachKeyValue(block, currentArray, start, end), null);
                executor.execute(futures[i]);
            }
            for (int i = 0; i < chunks; i++)
            {
                try
                {
                    futures[i].get();
                }
                catch (Exception e)
                {
                    throw new RuntimeException("parallelForEachKeyValue failed", e);
                }
            }
        }
        else
        {
            this.sequentialForEachKeyValue(blocks.get(0), currentArray, 0, currentArray.length);
        }
    }

    private void sequentialForEachKeyValue(Procedure2<? super K, ? super V> block, Object[] currentArray, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, i);
            if (o == RESIZED || o == RESIZING)
            {
                throw new ConcurrentModificationException("can't iterate while resizing!");
            }
            Entry<K, V> e = (Entry<K, V>) o;
            while (e != null)
            {
                Object key = e.getKey();
                Object value = e.getValue();
                block.value((K) key, (V) value);
                e = e.getNext();
            }
        }
    }

    public void parallelForEachValue(List<Procedure<V>> blocks, Executor executor)
    {
        Object[] currentArray = this.table;
        int chunks = blocks.size();
        if (chunks > 1)
        {
            FutureTask<?>[] futures = new FutureTask<?>[chunks];
            int chunkSize = currentArray.length / chunks;
            if (currentArray.length % chunks != 0)
            {
                chunkSize++;
            }
            for (int i = 0; i < chunks; i++)
            {
                int start = i * chunkSize;
                int end = Math.min((i + 1) * chunkSize, currentArray.length - 1);
                Procedure<V> block = blocks.get(i);
                futures[i] = new FutureTask<>(() -> this.sequentialForEachValue(block, currentArray, start, end), null);
                executor.execute(futures[i]);
            }
            for (int i = 0; i < chunks; i++)
            {
                try
                {
                    futures[i].get();
                }
                catch (Exception e)
                {
                    throw new RuntimeException("parallelForEachKeyValue failed", e);
                }
            }
        }
        else
        {
            this.sequentialForEachValue(blocks.get(0), currentArray, 0, currentArray.length);
        }
    }

    private void sequentialForEachValue(Procedure<? super V> block, Object[] currentArray, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, i);
            if (o == RESIZED || o == RESIZING)
            {
                throw new ConcurrentModificationException("can't iterate while resizing!");
            }
            Entry<K, V> e = (Entry<K, V>) o;
            while (e != null)
            {
                Object value = e.getValue();
                block.value((V) value);
                e = e.getNext();
            }
        }
    }

    @Override
    public int hashCode()
    {
        int h = 0;
        Object[] currentArray = this.table;
        for (int i = 0; i < currentArray.length - 1; i++)
        {
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, i);
            if (o == RESIZED || o == RESIZING)
            {
                throw new ConcurrentModificationException("can't compute hashcode while resizing!");
            }
            Entry<K, V> e = (Entry<K, V>) o;
            while (e != null)
            {
                Object key = e.getKey();
                Object value = e.getValue();
                h += (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
                e = e.getNext();
            }
        }
        return h;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof Map))
        {
            return false;
        }
        Map<K, V> m = (Map<K, V>) o;
        if (m.size() != this.size())
        {
            return false;
        }

        Iterator<Map.Entry<K, V>> i = this.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            if (value == null)
            {
                if (!(m.get(key) == null && m.containsKey(key)))
                {
                    return false;
                }
            }
            else
            {
                if (!value.equals(m.get(key)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString()
    {
        if (this.isEmpty())
        {
            return "{}";
        }
        Iterator<Map.Entry<K, V>> iterator = this.entrySet().iterator();

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        while (true)
        {
            Map.Entry<K, V> e = iterator.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (!iterator.hasNext())
            {
                return sb.append('}').toString();
            }
            sb.append(", ");
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int size = in.readInt();
        int capacity = 1;
        while (capacity < size)
        {
            capacity <<= 1;
        }
        this.table = new Object[capacity + 1];
        for (int i = 0; i < size; i++)
        {
            this.put((K) in.readObject(), (V) in.readObject());
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        int size = this.size();
        out.writeInt(size);
        int count = 0;
        for (int i = 0; i < this.table.length - 1; i++)
        {
            Object o = ConcurrentHashMapUnsafe.arrayAt(this.table, i);
            if (o == RESIZED || o == RESIZING)
            {
                throw new ConcurrentModificationException("Can't serialize while resizing!");
            }
            Entry<K, V> e = (Entry<K, V>) o;
            while (e != null)
            {
                count++;
                out.writeObject(e.getKey());
                out.writeObject(e.getValue());
                e = e.getNext();
            }
        }
        if (count != size)
        {
            throw new ConcurrentModificationException("Map changed while serializing");
        }
    }

    private static final class IteratorState
    {
        private Object[] currentTable;
        private int start;
        private int end;

        private IteratorState(Object[] currentTable)
        {
            this.currentTable = currentTable;
            this.end = this.currentTable.length - 1;
        }

        private IteratorState(Object[] currentTable, int start, int end)
        {
            this.currentTable = currentTable;
            this.start = start;
            this.end = end;
        }
    }

    private abstract class HashIterator<E> implements Iterator<E>
    {
        private List<IteratorState> todo;
        private IteratorState currentState;
        private Entry<K, V> next;
        private int index;
        private Entry<K, V> current;

        protected HashIterator()
        {
            this.currentState = new IteratorState(ConcurrentHashMapUnsafe.this.table);
            this.findNext();
        }

        private void findNext()
        {
            while (this.index < this.currentState.end)
            {
                Object o = ConcurrentHashMapUnsafe.arrayAt(this.currentState.currentTable, this.index);
                if (o == RESIZED || o == RESIZING)
                {
                    Object[] nextArray = ConcurrentHashMapUnsafe.this.helpWithResizeWhileCurrentIndex(this.currentState.currentTable, this.index);
                    int endResized = this.index + 1;
                    while (endResized < this.currentState.end)
                    {
                        if (ConcurrentHashMapUnsafe.arrayAt(this.currentState.currentTable, endResized) != RESIZED)
                        {
                            break;
                        }
                        endResized++;
                    }
                    if (this.todo == null)
                    {
                        this.todo = new FastList<>(4);
                    }
                    if (endResized < this.currentState.end)
                    {
                        this.todo.add(new IteratorState(this.currentState.currentTable, endResized, this.currentState.end));
                    }
                    int powerTwoLength = this.currentState.currentTable.length - 1;
                    this.todo.add(new IteratorState(nextArray, this.index + powerTwoLength, endResized + powerTwoLength));
                    this.currentState.currentTable = nextArray;
                    this.currentState.end = endResized;
                    this.currentState.start = this.index;
                }
                else if (o != null)
                {
                    this.next = (Entry<K, V>) o;
                    this.index++;
                    break;
                }
                else
                {
                    this.index++;
                }
            }
            if (this.next == null && this.index == this.currentState.end && this.todo != null && !this.todo.isEmpty())
            {
                this.currentState = this.todo.remove(this.todo.size() - 1);
                this.index = this.currentState.start;
                this.findNext();
            }
        }

        @Override
        public final boolean hasNext()
        {
            return this.next != null;
        }

        final Entry<K, V> nextEntry()
        {
            Entry<K, V> e = this.next;
            if (e == null)
            {
                throw new NoSuchElementException();
            }

            if ((this.next = e.getNext()) == null)
            {
                this.findNext();
            }
            this.current = e;
            return e;
        }

        protected void removeByKey()
        {
            if (this.current == null)
            {
                throw new IllegalStateException();
            }
            K key = this.current.key;
            this.current = null;
            ConcurrentHashMapUnsafe.this.remove(key);
        }

        protected boolean removeByKeyValue()
        {
            if (this.current == null)
            {
                throw new IllegalStateException();
            }
            K key = this.current.key;
            V val = this.current.value;
            this.current = null;
            return ConcurrentHashMapUnsafe.this.remove(key, val);
        }
    }

    private final class ValueIterator extends HashIterator<V>
    {
        @Override
        public V next()
        {
            return this.nextEntry().value;
        }

        @Override
        public void remove()
        {
            this.removeByKeyValue();
        }
    }

    private final class KeyIterator extends HashIterator<K>
    {
        @Override
        public K next()
        {
            return this.nextEntry().getKey();
        }

        @Override
        public void remove()
        {
            this.removeByKeyValue();
        }
    }

    private final class EntryIterator extends HashIterator<Map.Entry<K, V>>
    {
        @Override
        public Map.Entry<K, V> next()
        {
            return this.nextEntry();
        }

        @Override
        public void remove()
        {
            this.removeByKeyValue();
        }
    }

    private final class KeySet extends AbstractSet<K>
    {
        @Override
        public Iterator<K> iterator()
        {
            return new KeyIterator();
        }

        @Override
        public int size()
        {
            return ConcurrentHashMapUnsafe.this.size();
        }

        @Override
        public boolean contains(Object o)
        {
            return ConcurrentHashMapUnsafe.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o)
        {
            return ConcurrentHashMapUnsafe.this.remove(o) != null;
        }

        @Override
        public void clear()
        {
            ConcurrentHashMapUnsafe.this.clear();
        }
    }

    private final class Values extends AbstractCollection<V>
    {
        @Override
        public Iterator<V> iterator()
        {
            return new ValueIterator();
        }

        @Override
        public boolean removeAll(Collection<?> col)
        {
            Objects.requireNonNull(col);
            boolean removed = false;
            ValueIterator itr = new ValueIterator();
            while (itr.hasNext())
            {
                if (col.contains(itr.next()))
                {
                    removed |= itr.removeByKeyValue();
                }
            }
            return removed;
        }

        @Override
        public boolean removeIf(Predicate<? super V> filter)
        {
            Objects.requireNonNull(filter);
            boolean removed = false;
            ValueIterator itr = new ValueIterator();
            while (itr.hasNext())
            {
                if (filter.test(itr.next()))
                {
                    removed |= itr.removeByKeyValue();
                }
            }
            return removed;
        }

        @Override
        public int size()
        {
            return ConcurrentHashMapUnsafe.this.size();
        }

        @Override
        public boolean contains(Object o)
        {
            return ConcurrentHashMapUnsafe.this.containsValue(o);
        }

        @Override
        public void clear()
        {
            ConcurrentHashMapUnsafe.this.clear();
        }
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        @Override
        public Iterator<Map.Entry<K, V>> iterator()
        {
            return new EntryIterator();
        }

        @Override
        public boolean removeAll(Collection<?> col)
        {
            Objects.requireNonNull(col);
            boolean removed = false;

            if (this.size() > col.size())
            {
                for (Object o : col)
                {
                    removed |= this.remove(o);
                }
            }
            else
            {
                for (EntryIterator itr = new EntryIterator(); itr.hasNext(); )
                {
                    if (col.contains(itr.next()))
                    {
                        removed |= itr.removeByKeyValue();
                    }
                }
            }
            return removed;
        }

        @Override
        public boolean removeIf(Predicate<? super Map.Entry<K, V>> filter)
        {
            Objects.requireNonNull(filter);
            boolean removed = false;
            EntryIterator itr = new EntryIterator();
            while (itr.hasNext())
            {
                if (filter.test(itr.next()))
                {
                    removed |= itr.removeByKeyValue();
                }
            }
            return removed;
        }

        @Override
        public boolean contains(Object o)
        {
            if (!(o instanceof Map.Entry<?, ?>))
            {
                return false;
            }
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            Entry<K, V> candidate = ConcurrentHashMapUnsafe.this.getEntry(e.getKey());
            return e.equals(candidate);
        }

        @Override
        public boolean remove(Object o)
        {
            if (!(o instanceof Map.Entry<?, ?>))
            {
                return false;
            }
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            return ConcurrentHashMapUnsafe.this.remove(e.getKey(), e.getValue());
        }

        @Override
        public int size()
        {
            return ConcurrentHashMapUnsafe.this.size();
        }

        @Override
        public void clear()
        {
            ConcurrentHashMapUnsafe.this.clear();
        }
    }

    private static final class Entry<K, V> implements Map.Entry<K, V>
    {
        private final K key;
        private final V value;
        private final Entry<K, V> next;

        private Entry(K key, V value)
        {
            this.key = key;
            this.value = value;
            this.next = null;
        }

        private Entry(K key, V value, Entry<K, V> next)
        {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey()
        {
            return this.key;
        }

        @Override
        public V getValue()
        {
            return this.value;
        }

        @Override
        public V setValue(V value)
        {
            throw new RuntimeException("not implemented");
        }

        public Entry<K, V> getNext()
        {
            return this.next;
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof Map.Entry<?, ?>))
            {
                return false;
            }
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            K k1 = this.key;
            Object k2 = e.getKey();
            if (k1 == k2 || k1 != null && k1.equals(k2))
            {
                V v1 = this.value;
                Object v2 = e.getValue();
                return v1 == v2 || v1 != null && v1.equals(v2);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        @Override
        public String toString()
        {
            return this.key + "=" + this.value;
        }
    }

    private static final class ResizeContainer
    {
        private static final int QUEUE_INCREMENT = Math.min(1 << 10, Integer.highestOneBit(Runtime.getRuntime().availableProcessors()) << 4);
        private final AtomicInteger resizers = new AtomicInteger(1);
        private final Object[] nextArray;
        private final AtomicInteger queuePosition;

        private ResizeContainer(Object[] nextArray, int oldSize)
        {
            this.nextArray = nextArray;
            this.queuePosition = new AtomicInteger(oldSize);
        }

        public void incrementResizer()
        {
            this.resizers.incrementAndGet();
        }

        public void decrementResizerAndNotify()
        {
            int remaining = this.resizers.decrementAndGet();
            if (remaining == 0)
            {
                synchronized (this)
                {
                    this.notifyAll();
                }
            }
        }

        public int getQueuePosition()
        {
            return this.queuePosition.get();
        }

        public int subtractAndGetQueuePosition()
        {
            return this.queuePosition.addAndGet(-QUEUE_INCREMENT);
        }

        public void waitForAllResizers()
        {
            if (this.resizers.get() > 0)
            {
                for (int i = 0; i < 16; i++)
                {
                    if (this.resizers.get() == 0)
                    {
                        break;
                    }
                }
                for (int i = 0; i < 16; i++)
                {
                    if (this.resizers.get() == 0)
                    {
                        break;
                    }
                    Thread.yield();
                }
            }
            if (this.resizers.get() > 0)
            {
                synchronized (this)
                {
                    while (this.resizers.get() > 0)
                    {
                        try
                        {
                            this.wait();
                        }
                        catch (InterruptedException e)
                        {
                            //ginore
                        }
                    }
                }
            }
        }

        public boolean isNotDone()
        {
            return this.resizers.get() > 0;
        }

        public void zeroOutQueuePosition()
        {
            this.queuePosition.set(0);
        }
    }

    public static <NK, NV> ConcurrentHashMapUnsafe<NK, NV> newMap(Map<NK, NV> map)
    {
        ConcurrentHashMapUnsafe<NK, NV> result = new ConcurrentHashMapUnsafe<>(map.size());
        result.putAll(map);
        return result;
    }

    @Override
    public ConcurrentHashMapUnsafe<K, V> withKeyValue(K key, V value)
    {
        return (ConcurrentHashMapUnsafe<K, V>) super.withKeyValue(key, value);
    }

    @Override
    public ConcurrentHashMapUnsafe<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        return (ConcurrentHashMapUnsafe<K, V>) super.withAllKeyValues(keyValues);
    }

    @Override
    public ConcurrentHashMapUnsafe<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValues)
    {
        return (ConcurrentHashMapUnsafe<K, V>) super.withAllKeyValueArguments(keyValues);
    }

    @Override
    public ConcurrentHashMapUnsafe<K, V> withoutKey(K key)
    {
        return (ConcurrentHashMapUnsafe<K, V>) super.withoutKey(key);
    }

    @Override
    public ConcurrentHashMapUnsafe<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        return (ConcurrentHashMapUnsafe<K, V>) super.withoutAllKeys(keys);
    }

    @Override
    public MutableMap<K, V> clone()
    {
        return ConcurrentHashMapUnsafe.newMap(this);
    }

    @Override
    public <K, V> MutableMap<K, V> newEmpty(int capacity)
    {
        return ConcurrentHashMapUnsafe.newMap();
    }

    @Override
    public boolean notEmpty()
    {
        return !this.isEmpty();
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        Iterate.forEachWithIndex(this.values(), objectIntProcedure);
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.values().iterator();
    }

    @Override
    public MutableMap<K, V> newEmpty()
    {
        return ConcurrentHashMapUnsafe.newMap();
    }

    @Override
    public ConcurrentMutableMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.each(procedure);
        return this;
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        IterableIterate.forEach(this.values(), procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        IterableIterate.forEach(this.keySet(), procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        IterableIterate.forEach(this.entrySet(), new MapEntryToProcedure2<>(procedure));
    }

    @Override
    public <E> MutableMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> valueFunction)
    {
        Iterate.addToMap(iterable, keyFunction, valueFunction, this);
        return this;
    }

    @Override
    public V removeKey(K key)
    {
        return this.remove(key);
    }

    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        V newValue = null;
        boolean createdValue = false;
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        return e.getValue();
                    }
                    e = e.getNext();
                }
                if (!createdValue)
                {
                    createdValue = true;
                    newValue = function.valueOf(parameter);
                }
                Entry<K, V> newEntry = new Entry<>(key, newValue, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return newValue;
                }
            }
        }
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        V result = this.get(key);
        if (result == null)
        {
            return function.value();
        }
        return result;
    }

    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        V result = this.get(key);
        if (result == null)
        {
            return function.valueOf(parameter);
        }
        return result;
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        V result = this.get(key);
        return result == null ? null : function.valueOf(result);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        Iterate.forEachWith(this.values(), procedure, parameter);
    }

    private static Unsafe getUnsafe()
    {
        try
        {
            return Unsafe.getUnsafe();
        }
        catch (SecurityException ignored)
        {
            try
            {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>()
                {
                    public Unsafe run() throws Exception
                    {
                        Field f = Unsafe.class.getDeclaredField("theUnsafe");
                        f.setAccessible(true);
                        return (Unsafe) f.get(null);
                    }
                });
            }
            catch (PrivilegedActionException e)
            {
                throw new RuntimeException("Could not initialize intrinsics", e.getCause());
            }
        }
    }

    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        int length = currentArray.length;
        int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
        Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
        if (o == null)
        {
            V result = function.valueOf(factory.value());
            Entry<K, V> newEntry = new Entry<>(key, result, null);
            if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, null, newEntry))
            {
                this.addToSize(1);
                return result;
            }
        }
        return this.slowUpdateValue(key, factory, function, hash, currentArray);
    }

    private V slowUpdateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function, int hash, Object[] currentArray)
    {
        //noinspection LabeledStatement
        outer:
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        V oldValue = e.getValue();
                        V newValue = function.valueOf(oldValue);
                        Entry<K, V> newEntry = new Entry<>(e.getKey(), newValue, this.createReplacementChainForRemoval((Entry<K, V>) o, e));
                        if (!ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                        {
                            //noinspection ContinueStatementWithLabel
                            continue outer;
                        }

                        return newValue;
                    }
                    e = e.getNext();
                }
                V result = function.valueOf(factory.value());
                Entry<K, V> newEntry = new Entry<>(key, result, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return result;
                }
            }
        }
    }

    @Override
    public <P> V updateValueWith(K key, Function0<? extends V> factory, Function2<? super V, ? super P, ? extends V> function, P parameter)
    {
        int hash = this.hash(key);
        Object[] currentArray = this.table;
        int length = currentArray.length;
        int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
        Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
        if (o == null)
        {
            V result = function.value(factory.value(), parameter);
            Entry<K, V> newEntry = new Entry<>(key, result, null);
            if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, null, newEntry))
            {
                this.addToSize(1);
                return result;
            }
        }
        return this.slowUpdateValueWith(key, factory, function, parameter, hash, currentArray);
    }

    private <P> V slowUpdateValueWith(
            K key,
            Function0<? extends V> factory,
            Function2<? super V, ? super P, ? extends V> function,
            P parameter,
            int hash,
            Object[] currentArray)
    {
        //noinspection LabeledStatement
        outer:
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        V oldValue = e.getValue();
                        V newValue = function.value(oldValue, parameter);
                        Entry<K, V> newEntry = new Entry<>(e.getKey(), newValue, this.createReplacementChainForRemoval((Entry<K, V>) o, e));
                        if (!ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                        {
                            //noinspection ContinueStatementWithLabel
                            continue outer;
                        }
                        return newValue;
                    }
                    e = e.getNext();
                }
                V result = function.value(factory.value(), parameter);
                Entry<K, V> newEntry = new Entry<>(key, result, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return result;
                }
            }
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        Objects.requireNonNull(value, "value cannot be null");
        Objects.requireNonNull(remappingFunction, "remappingFunction cannot be null");

        int hash = this.hash(key);
        Object[] currentArray = this.table;
        int length = currentArray.length;
        int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
        Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
        if (o == null)
        {
            Entry<K, V> newEntry = new Entry<>(key, value, null);
            if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, null, newEntry))
            {
                this.addToSize(1);
                return value;
            }
        }
        return this.slowMerge(key, value, remappingFunction, hash, currentArray);
    }

    private V slowMerge(
            K key,
            V value,
            BiFunction<? super V, ? super V, ? extends V> remappingFunction,
            int hash,
            Object[] currentArray)
    {
        //noinspection LabeledStatement
        outer:
        while (true)
        {
            int length = currentArray.length;
            int index = ConcurrentHashMapUnsafe.indexFor(hash, length);
            Object o = ConcurrentHashMapUnsafe.arrayAt(currentArray, index);
            if (o == RESIZED || o == RESIZING)
            {
                currentArray = this.helpWithResizeWhileCurrentIndex(currentArray, index);
            }
            else
            {
                Entry<K, V> e = (Entry<K, V>) o;
                while (e != null)
                {
                    Object candidate = e.getKey();
                    if (candidate.equals(key))
                    {
                        V oldValue = e.getValue();
                        V newValue = oldValue == null ? value : remappingFunction.apply(oldValue, value);
                        Entry<K, V> replacementChainForRemoval = this.createReplacementChainForRemoval((Entry<K, V>) o, e);
                        Entry<K, V> newEntry = newValue == null
                                ? replacementChainForRemoval
                                : new Entry<>(e.getKey(), newValue, replacementChainForRemoval);
                        if (!ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                        {
                            //noinspection ContinueStatementWithLabel
                            continue outer;
                        }

                        if (newValue == null)
                        {
                            this.addToSize(-1);
                        }
                        return newValue;
                    }
                    e = e.getNext();
                }
                Entry<K, V> newEntry = new Entry<>(key, value, (Entry<K, V>) o);
                if (ConcurrentHashMapUnsafe.casArrayAt(currentArray, index, o, newEntry))
                {
                    this.incrementSizeAndPossiblyResize(currentArray, length, o);
                    return value;
                }
            }
        }
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        return Maps.immutable.ofMap(this);
    }
}
