/*
 * Copyright (c) 2022 Shotaro Sano and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap;

import java.util.Collection;
import java.util.Set;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.mutable.SynchronizedBag;
import org.eclipse.collections.impl.map.mutable.SynchronizedMutableMap;
import org.eclipse.collections.impl.set.mutable.SynchronizedMutableSet;
import org.eclipse.collections.impl.utility.LazyIterate;

public abstract class AbstractSynchronizedMultimap<K, V> implements MutableMultimap<K, V>
{
    protected final MutableMultimap<K, V> delegate;
    private final Object lock;

    protected AbstractSynchronizedMultimap(MutableMultimap<K, V> multimap, Object newLock)
    {
        if (multimap == null)
        {
            throw new IllegalArgumentException("Cannot create a AbstractSynchronizedMultimap on a null multimap");
        }

        this.delegate = multimap;
        this.lock = newLock == null ? this : newLock;
    }

    protected AbstractSynchronizedMultimap(MutableMultimap<K, V> multimap)
    {
        this(multimap, null);
    }

    protected MutableMultimap<K, V> getDelegate()
    {
        return this.delegate;
    }

    protected Object getLock()
    {
        return this.lock;
    }

    protected Object writeReplace()
    {
        return new SynchronizedMultimapSerializationProxy<>(this.getDelegate());
    }

    @Override
    public boolean equals(Object obj)
    {
        synchronized (this.lock)
        {
            return this.delegate.equals(obj);
        }
    }

    @Override
    public int hashCode()
    {
        synchronized (this.lock)
        {
            return this.delegate.hashCode();
        }
    }

    @Override
    public String toString()
    {
        synchronized (this.lock)
        {
            return this.delegate.toString();
        }
    }

    @Override
    public boolean put(K key, V value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().put(key, value);
        }
    }

    @Override
    public boolean add(Pair<? extends K, ? extends V> keyValuePair)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().add(keyValuePair);
        }
    }

    @Override
    public boolean remove(Object key, Object value)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().remove(key, value);
        }
    }

    @Override
    public MutableMultimap<K, V> withKeyValue(K key, V value)
    {
        synchronized (this.lock)
        {
            this.getDelegate().withKeyValue(key, value);
            return this;
        }
    }

    @Override
    public MutableMultimap<K, V> withKeyMultiValues(K key, V... values)
    {
        synchronized (this.lock)
        {
            this.getDelegate().withKeyMultiValues(key, values);
            return this;
        }
    }

    @Override
    public boolean putAllPairs(Pair<? extends K, ? extends V>... pairs)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().putAllPairs(pairs);
        }
    }

    @Override
    public boolean putAllPairs(Iterable<? extends Pair<? extends K, ? extends V>> pairs)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().putAllPairs(pairs);
        }
    }

    @Override
    public boolean putAll(K key, Iterable<? extends V> values)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().putAll(key, values);
        }
    }

    @Override
    public <KK extends K, VV extends V> boolean putAll(Multimap<KK, VV> multimap)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().putAll(multimap);
        }
    }

    @Override
    public void clear()
    {
        synchronized (this.lock)
        {
            this.getDelegate().clear();
        }
    }

    @Override
    public boolean isEmpty()
    {
        synchronized (this.lock)
        {
            return this.delegate.isEmpty();
        }
    }

    @Override
    public boolean notEmpty()
    {
        synchronized (this.lock)
        {
            return this.delegate.notEmpty();
        }
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEachValue(procedure);
        }
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEachKey(procedure);
        }
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEachKeyValue(procedure);
        }
    }

    @Override
    public void forEachKeyMultiValues(Procedure2<? super K, ? super RichIterable<V>> procedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEachKeyMultiValues(procedure);
        }
    }

    @Override
    public int size()
    {
        synchronized (this.lock)
        {
            return this.delegate.size();
        }
    }

    @Override
    public int sizeDistinct()
    {
        synchronized (this.lock)
        {
            return this.delegate.sizeDistinct();
        }
    }

    @Override
    public boolean containsKey(Object key)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsValue(value);
        }
    }

    @Override
    public boolean containsKeyAndValue(Object key, Object value)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsKeyAndValue(key, value);
        }
    }

    @Override
    public RichIterable<K> keysView()
    {
        return LazyIterate.adapt(this.keySet());
    }

    @Override
    public SetIterable<K> keySet()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableSet.of((Set<K>) this.getDelegate().keySet(), this.lock);
        }
    }

    @Override
    public Bag<K> keyBag()
    {
        synchronized (this.lock)
        {
            return SynchronizedBag.of(this.getDelegate().keyBag().toBag(), this.lock);
        }
    }

    @Override
    public RichIterable<RichIterable<V>> multiValuesView()
    {
        synchronized (this.lock)
        {
            return LazyIterate.adapt(this.getDelegate().multiValuesView());
        }
    }

    @Override
    public RichIterable<V> valuesView()
    {
        synchronized (this.lock)
        {
            return LazyIterate.adapt(this.getDelegate().valuesView());
        }
    }

    @Override
    public RichIterable<Pair<K, RichIterable<V>>> keyMultiValuePairsView()
    {
        synchronized (this.lock)
        {
            return LazyIterate.adapt(this.getDelegate().keyMultiValuePairsView());
        }
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuePairsView()
    {
        synchronized (this.lock)
        {
            return LazyIterate.adapt(this.getDelegate().keyValuePairsView());
        }
    }

    @Override
    public MutableMap<K, RichIterable<V>> toMap()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableMap.of(this.getDelegate().toMap(), this.lock);
        }
    }

    @Override
    public <R extends Collection<V>> MutableMap<K, R> toMap(Function0<R> collectionFactory)
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableMap.of(this.getDelegate().toMap(collectionFactory), this.lock);
        }
    }

    @Override
    public <R extends MutableMultimap<K, V>> R selectKeysValues(Predicate2<? super K, ? super V> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().selectKeysValues(predicate, target);
        }
    }

    @Override
    public <R extends MutableMultimap<K, V>> R rejectKeysValues(Predicate2<? super K, ? super V> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().rejectKeysValues(predicate, target);
        }
    }

    @Override
    public <R extends MutableMultimap<K, V>> R selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().selectKeysMultiValues(predicate, target);
        }
    }

    @Override
    public <R extends MutableMultimap<K, V>> R rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().rejectKeysMultiValues(predicate, target);
        }
    }

    @Override
    public <K2, V2, R extends MutableMultimap<K2, V2>> R collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectKeysValues(function, target);
        }
    }

    @Override
    public <K2, V2, R extends MutableMultimap<K2, V2>> R collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectKeyMultiValues(keyFunction, valueFunction, target);
        }
    }

    @Override
    public <V2, R extends MutableMultimap<K, V2>> R collectValues(Function<? super V, ? extends V2> function, R target)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectValues(function, target);
        }
    }
}
