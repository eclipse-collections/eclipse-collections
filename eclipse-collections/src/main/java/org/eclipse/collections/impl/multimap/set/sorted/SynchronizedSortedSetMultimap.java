/*
 * Copyright (c) 2022 Shotaro Sano and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.set.sorted;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.multimap.AbstractSynchronizedMultimap;

public class SynchronizedSortedSetMultimap<K, V>
        extends AbstractSynchronizedMultimap<K, V>
        implements MutableSortedSetMultimap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    public SynchronizedSortedSetMultimap(MutableSortedSetMultimap<K, V> multimap)
    {
        super(multimap);
    }

    public SynchronizedSortedSetMultimap(MutableSortedSetMultimap<K, V> multimap, Object newLock)
    {
        super(multimap, newLock);
    }

    /**
     * This method will take a Multimap and wrap it directly in a SynchronizedSortedSetMultimap.
     */
    public static <K, V> SynchronizedSortedSetMultimap<K, V> of(MutableSortedSetMultimap<K, V> multimap)
    {
        if (multimap == null)
        {
            throw new IllegalArgumentException("cannot create a SynchronizedSortedSetMultimap for null");
        }

        return new SynchronizedSortedSetMultimap<>(multimap);
    }

    /**
     * This method will take a Multimap and wrap it directly in a SynchronizedSortedSetMultimap.
     * Additionally, a developer specifies which lock to use with the collection.
     */
    public static <K, V> SynchronizedSortedSetMultimap<K, V> of(MutableSortedSetMultimap<K, V> multimap, Object lock)
    {
        if (multimap == null)
        {
            throw new IllegalArgumentException("cannot create a SynchronizedSortedSetMultimap for null");
        }

        return new SynchronizedSortedSetMultimap<>(multimap, lock);
    }

    @Override
    public MutableSortedSetMultimap<K, V> withKeyValue(K key, V value)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().withKeyValue(key, value);
            return this;
        }
    }

    @Override
    public MutableSortedSetMultimap<K, V> withKeyMultiValues(K key, V... values)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().withKeyMultiValues(key, values);
            return this;
        }
    }

    @Override
    public MutableSortedSet<V> replaceValues(K key, Iterable<? extends V> values)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().replaceValues(key, values);
        }
    }

    @Override
    public MutableSortedSet<V> removeAll(Object key)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().removeAll(key);
        }
    }

    @Override
    public MutableSortedSetMultimap<K, V> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().newEmpty().asSynchronized();
        }
    }

    @Override
    public MutableSortedSet<V> get(K key)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().get(key);
        }
    }

    @Override
    public MutableSortedSet<V> getIfAbsentPutAll(K key, Iterable<? extends V> values)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().getIfAbsentPutAll(key, values);
        }
    }

    @Override
    public Comparator<? super V> comparator()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().comparator();
        }
    }

    @Override
    public MutableSortedSetMultimap<K, V> toMutable()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toMutable();
        }
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> toImmutable()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toImmutable();
        }
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().flip();
        }
    }

    @Override
    public MutableSortedSetMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectKeysValues(predicate);
        }
    }

    @Override
    public MutableSortedSetMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().rejectKeysValues(predicate);
        }
    }

    @Override
    public MutableSortedSetMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectKeysMultiValues(predicate);
        }
    }

    @Override
    public MutableSortedSetMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
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
    public MutableSortedSetMultimap<K, V> asSynchronized()
    {
        return this;
    }

    @Override
    protected MutableSortedSetMultimap<K, V> getDelegate()
    {
        return (MutableSortedSetMultimap<K, V>) super.getDelegate();
    }
}
