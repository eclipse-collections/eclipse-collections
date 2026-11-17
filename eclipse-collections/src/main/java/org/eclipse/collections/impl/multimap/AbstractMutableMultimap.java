/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.checked.MultimapKeyValuesSerializingProcedure;
import org.eclipse.collections.impl.set.mutable.UnmodifiableMutableSet;
import org.eclipse.collections.impl.utility.Iterate;

public abstract class AbstractMutableMultimap<K, V, C extends MutableCollection<V>>
        extends AbstractMultimap<K, V, C>
        implements MutableMultimap<K, V>
{
    protected MutableMap<K, C> map;

    protected int totalSize;

    protected AbstractMutableMultimap()
    {
        this.map = this.createMap();
    }

    protected AbstractMutableMultimap(MutableMap<K, C> newMap)
    {
        this.map = newMap;
    }

    protected AbstractMutableMultimap(int size)
    {
        this.map = this.createMapWithKeyCount(size);
    }

    /**
     * Constructs a {@link Multimap} containing all the {@link Pair}s.
     *
     * @param pairs the mappings to initialize the multimap.
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    protected AbstractMutableMultimap(Pair<K, V>... pairs)
    {
        this(pairs.length);
        this.putAllPairs(pairs);
    }

    /**
     * Constructs a {@link Multimap} containing  {@link Iterable}.
     *
     * @param inputIterable the mappings to initialize the multimap.
     */
    protected AbstractMutableMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        this();
        for (Pair<K, V> single : inputIterable)
        {
            this.put(single.getOne(), single.getTwo());
        }
    }

    protected abstract MutableMap<K, C> createMap();

    protected abstract MutableMap<K, C> createMapWithKeyCount(int keyCount);

    @Override
    protected MutableMap<K, C> getMap()
    {
        return this.map;
    }

    // Query Operations

    /**
     * Use the size method directly instead of totalSize internally so subclasses can override if necessary.
     */
    @Override
    public int size()
    {
        return this.totalSize;
    }

    /**
     * This method is provided to allow for subclasses to provide the behavior. It should add 1 to the value that is
     * returned by calling size().
     */
    protected void incrementTotalSize()
    {
        this.totalSize++;
    }

    /**
     * This method is provided to allow for subclasses to provide the behavior. It should remove 1 from the value that is
     * returned by calling size().
     */
    protected void decrementTotalSize()
    {
        this.totalSize--;
    }

    /**
     * This method is provided to allow for subclasses to provide the behavior. It should add the specified amount to
     * the value that is returned by calling size().
     */
    protected void addToTotalSize(int value)
    {
        this.totalSize += value;
    }

    /**
     * This method is provided to allow for subclasses to provide the behavior. It should subtract the specified amount from
     * the value that is returned by calling size().
     */
    protected void subtractFromTotalSize(int value)
    {
        this.totalSize -= value;
    }

    /**
     * This method is provided to allow for subclasses to provide the behavior. It should set the value returned by
     * size() to 0.
     */
    protected void clearTotalSize()
    {
        this.totalSize = 0;
    }

    @Override
    public int sizeDistinct()
    {
        return this.map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    // Modification Operations

    @Override
    public boolean put(K key, V value)
    {
        C collection = this.getIfAbsentPutCollection(key);

        if (collection.add(value))
        {
            this.incrementTotalSize();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object key, Object value)
    {
        C collection = this.map.get(key);
        if (collection == null)
        {
            return false;
        }

        boolean changed = collection.remove(value);
        if (changed)
        {
            this.decrementTotalSize();
            if (collection.isEmpty())
            {
                this.map.remove(key);
            }
        }
        return changed;
    }

    @Override
    public boolean putAll(K key, Iterable<? extends V> values)
    {
        return Iterate.notEmpty(values) && this.putAllNotEmpty(key, values);
    }

    private boolean putAllNotEmpty(K key, Iterable<? extends V> values)
    {
        C collection = this.getIfAbsentPutCollection(key);
        int oldSize = collection.size();
        int newSize = Iterate.addAllTo(values, collection).size();
        this.addToTotalSize(newSize - oldSize);
        return newSize > oldSize;
    }

    @Override
    public <KK extends K, VV extends V> boolean putAll(Multimap<KK, VV> multimap)
    {
        if (multimap instanceof AbstractMutableMultimap)
        {
            return this.putAllAbstractMutableMultimap((AbstractMutableMultimap<KK, VV, MutableCollection<VV>>) multimap);
        }
        return this.putAllReadOnlyMultimap(multimap);
    }

    private <KK extends K, VV extends V> boolean putAllReadOnlyMultimap(Multimap<KK, VV> multimap)
    {
        class PutProcedure implements Procedure<Pair<KK, RichIterable<VV>>>
        {
            private static final long serialVersionUID = 1L;
            private boolean changed;

            @Override
            public void value(Pair<KK, RichIterable<VV>> each)
            {
                this.changed |= AbstractMutableMultimap.this.putAll(each.getOne(), each.getTwo());
            }
        }

        PutProcedure putProcedure = new PutProcedure();
        multimap.keyMultiValuePairsView().forEach(putProcedure);
        return putProcedure.changed;
    }

    private <KK extends K, VV extends V> boolean putAllAbstractMutableMultimap(AbstractMutableMultimap<KK, VV, MutableCollection<VV>> other)
    {
        class PutProcedure implements Procedure2<KK, MutableCollection<VV>>
        {
            private static final long serialVersionUID = 1L;

            private boolean changed;

            @Override
            public void value(KK key, MutableCollection<VV> value)
            {
                this.changed |= AbstractMutableMultimap.this.putAll(key, value);
            }
        }

        PutProcedure putProcedure = new PutProcedure();
        other.map.forEachKeyValue(putProcedure);
        return putProcedure.changed;
    }

    @Override
    public C replaceValues(K key, Iterable<? extends V> values)
    {
        if (Iterate.isEmpty(values))
        {
            return this.removeAll(key);
        }

        C newValues = Iterate.addAllTo(values, this.createCollection());
        C oldValues = this.map.put(key, newValues);
        oldValues = oldValues == null ? this.createCollection() : oldValues;
        this.addToTotalSize(newValues.size() - oldValues.size());
        return (C) oldValues.asUnmodifiable();
    }

    @Override
    public C removeAll(Object key)
    {
        C collection = this.map.remove(key);
        collection = collection == null ? this.createCollection() : collection;
        this.subtractFromTotalSize(collection.size());
        return (C) collection.asUnmodifiable();
    }

    @Override
    public void clear()
    {
        // Clear each collection, to make previously returned collections empty.
        for (C collection : this.map.values())
        {
            collection.clear();
        }
        this.map.clear();
        this.clearTotalSize();
    }

    // Views

    @Override
    public SetIterable<K> keySet()
    {
        return UnmodifiableMutableSet.of(this.getMap().keySet());
    }

    @Override
    public C get(K key)
    {
        return (C) this.map.getIfAbsentWith(key, this.createCollectionBlock(), this).asUnmodifiable();
    }

    @Override
    public C getIfAbsentPutAll(K key, Iterable<? extends V> values)
    {
        if (Iterate.isEmpty(values))
        {
            return this.get(key);
        }

        C existingValues = this.getIfAbsentPutCollection(key);
        if (existingValues.isEmpty())
        {
            int newSize = Iterate.addAllTo(values, existingValues).size();
            this.addToTotalSize(newSize);
        }

        return (C) existingValues.asUnmodifiable();
    }

    private C getIfAbsentPutCollection(K key)
    {
        return this.map.getIfAbsentPutWith(key, this.createCollectionBlock(), this);
    }

    @Override
    public MutableMap<K, RichIterable<V>> toMap()
    {
        MutableMap<K, RichIterable<V>> result = (MutableMap<K, RichIterable<V>>) (MutableMap<?, ?>) this.map.newEmpty();
        this.map.forEachKeyValue((key, collection) -> {
            MutableCollection<V> mutableCollection = collection.newEmpty();
            mutableCollection.addAll(collection);
            result.put(key, mutableCollection);
        });
        return result;
    }

    @Override
    public <R extends Collection<V>> MutableMap<K, R> toMap(Function0<R> collectionFactory)
    {
        MutableMap<K, R> result = (MutableMap<K, R>) this.createMapWithKeyCount(this.map.size());
        this.map.forEachKeyValue((key, collection) -> {
            R mutableCollection = collectionFactory.value();
            mutableCollection.addAll(collection);
            result.put(key, mutableCollection);
        });
        return result;
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.map.size());
        this.map.forEachKeyValue(new MultimapKeyValuesSerializingProcedure<>(out));
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.readValuesFrom(in);
    }

    void readValuesFrom(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int keyCount = in.readInt();
        this.map = this.createMapWithKeyCount(keyCount);
        for (int k = 0; k < keyCount; k++)
        {
            K key = (K) in.readObject();
            int valuesSize = in.readInt();
            C values = this.createCollection();
            for (int v = 0; v < valuesSize; v++)
            {
                values.add((V) in.readObject());
            }
            this.addToTotalSize(valuesSize);
            this.map.put(key, values);
        }
    }
}
