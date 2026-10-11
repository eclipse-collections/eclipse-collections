/*
 * Copyright (c) 2022 Shotaro Sano and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.list;

import java.io.Serializable;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.multimap.AbstractSynchronizedMultimap;

public class SynchronizedListMultimap<K, V>
        extends AbstractSynchronizedMultimap<K, V>
        implements MutableListMultimap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    public SynchronizedListMultimap(MutableListMultimap<K, V> multimap)
    {
        super(multimap);
    }

    public SynchronizedListMultimap(MutableListMultimap<K, V> multimap, Object newLock)
    {
        super(multimap, newLock);
    }

    /**
     * This method will take a Multimap and wrap it directly in a SynchronizedListMultimap.
     */
    public static <K, V> SynchronizedListMultimap<K, V> of(MutableListMultimap<K, V> multimap)
    {
        if (multimap == null)
        {
            throw new IllegalArgumentException("cannot create a SynchronizedListMultimap for null");
        }

        return new SynchronizedListMultimap<>(multimap);
    }

    /**
     * This method will take a Multimap and wrap it directly in a SynchronizedListMultimap.
     * Additionally, a developer specifies which lock to use with the collection.
     */
    public static <K, V> SynchronizedListMultimap<K, V> of(MutableListMultimap<K, V> multimap, Object lock)
    {
        if (multimap == null)
        {
            throw new IllegalArgumentException("cannot create a SynchronizedListMultimap for null");
        }

        return new SynchronizedListMultimap<>(multimap, lock);
    }

    @Override
    public MutableListMultimap<K, V> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().newEmpty().asSynchronized();
        }
    }

    @Override
    public MutableListMultimap<K, V> toMutable()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toMutable();
        }
    }

    @Override
    public ImmutableListMultimap<K, V> toImmutable()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toImmutable();
        }
    }

    @Override
    public void forEachKeyMutableList(Procedure2<? super K, ? super MutableList<V>> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEachKeyMutableList(procedure);
        }
    }

    @Override
    public MutableList<V> replaceValues(K key, Iterable<? extends V> values)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().replaceValues(key, values);
        }
    }

    @Override
    public MutableList<V> removeAll(Object key)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().removeAll(key);
        }
    }

    @Override
    public MutableList<V> get(K key)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().get(key);
        }
    }

    @Override
    public MutableListMultimap<K, V> withKeyValue(K key, V value)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().withKeyValue(key, value);
            return this;
        }
    }

    @Override
    public MutableListMultimap<K, V> withKeyMultiValues(K key, V... values)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().withKeyMultiValues(key, values);
            return this;
        }
    }

    @Override
    public MutableList<V> getIfAbsentPutAll(K key, Iterable<? extends V> values)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().getIfAbsentPutAll(key, values);
        }
    }

    @Override
    public MutableBagMultimap<V, K> flip()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().flip();
        }
    }

    @Override
    public MutableListMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectKeysValues(predicate);
        }
    }

    @Override
    public MutableListMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().rejectKeysValues(predicate);
        }
    }

    @Override
    public MutableListMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectKeysMultiValues(predicate);
        }
    }

    @Override
    public MutableListMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().rejectKeysMultiValues(predicate);
        }
    }

    @Override
    public <K2, V2> MutableBagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectKeysValues(function);
        }
    }

    @Override
    public <K2, V2> MutableBagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectKeyMultiValues(keyFunction, valueFunction);
        }
    }

    @Override
    public <V2> MutableListMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectValues(function);
        }
    }

    @Override
    public MutableListMultimap<K, V> asSynchronized()
    {
        return this;
    }

    @Override
    protected MutableListMultimap<K, V> getDelegate()
    {
        return (MutableListMultimap<K, V>) super.getDelegate();
    }
}
