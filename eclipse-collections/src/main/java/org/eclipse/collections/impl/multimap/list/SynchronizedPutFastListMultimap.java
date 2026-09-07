/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.list;

import java.io.Externalizable;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.multimap.AbstractSynchronizedPutMultimap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * A Multimap that is optimized for parallel writes, but is not protected for concurrent reads.
 */
public class SynchronizedPutFastListMultimap<K, V>
        extends AbstractSynchronizedPutMultimap<K, V, MutableList<V>>
        implements MutableListMultimap<K, V>, Externalizable
{
    private static final long serialVersionUID = 42L;

    public SynchronizedPutFastListMultimap()
    {
    }

    public SynchronizedPutFastListMultimap(int initialCapacity)
    {
        super(ConcurrentHashMap.newMap(initialCapacity));
    }

    public SynchronizedPutFastListMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        this.putAll(multimap);
    }

    public SynchronizedPutFastListMultimap(Pair<K, V>... pairs)
    {
        this();
        ArrayIterate.forEach(pairs, pair -> this.put(pair.getOne(), pair.getTwo()));
    }

    public SynchronizedPutFastListMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        this();
        Iterate.forEach(inputIterable, this::add);
    }

    public static <K, V> SynchronizedPutFastListMultimap<K, V> newMultimap()
    {
        return new SynchronizedPutFastListMultimap<>();
    }

    public static <K, V> SynchronizedPutFastListMultimap<K, V> newMultimap(int initialCapacity, float loadFactor, int concurrencyLevel)
    {
        return new SynchronizedPutFastListMultimap<>(initialCapacity);
    }

    public static <K, V> SynchronizedPutFastListMultimap<K, V> newMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        return new SynchronizedPutFastListMultimap<>(multimap);
    }

    public static <K, V> SynchronizedPutFastListMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return new SynchronizedPutFastListMultimap<>(pairs);
    }

    public static <K, V> SynchronizedPutFastListMultimap<K, V> newMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        return new SynchronizedPutFastListMultimap<>(inputIterable);
    }

    @Override
    public SynchronizedPutFastListMultimap<K, V> withKeyMultiValues(K key, V... values)
    {
        return (SynchronizedPutFastListMultimap<K, V>) super.withKeyMultiValues(key, values);
    }

    @Override
    protected MutableList<V> createCollection()
    {
        return Lists.mutable.withInitialCapacity(1);
    }

    @Override
    public void forEachKeyMutableList(Procedure2<? super K, ? super MutableList<V>> procedure)
    {
        this.getMap().forEachKeyValue((key, value) -> procedure.value(key, value.asUnmodifiable()));
    }

    @Override
    public SynchronizedPutFastListMultimap<K, V> newEmpty()
    {
        return new SynchronizedPutFastListMultimap<>();
    }

    @Override
    public MutableListMultimap<K, V> toMutable()
    {
        return new SynchronizedPutFastListMultimap<>(this);
    }

    @Override
    public ImmutableListMultimap<K, V> toImmutable()
    {
        MutableMap<K, ImmutableList<V>> map = Maps.mutable.empty();

        this.map.forEachKeyValue((key, list) -> map.put(key, list.toImmutable()));

        return new ImmutableListMultimapImpl<>(map);
    }

    @Override
    public MutableBagMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public FastListMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, FastListMultimap.newMultimap());
    }

    @Override
    public FastListMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, FastListMultimap.newMultimap());
    }

    @Override
    public FastListMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, FastListMultimap.newMultimap());
    }

    @Override
    public FastListMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, FastListMultimap.newMultimap());
    }

    @Override
    public <K2, V2> HashBagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.collectKeysValues(function, HashBagMultimap.newMultimap());
    }

    @Override
    public <K2, V2> HashBagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction)
    {
        return this.collectKeyMultiValues(keyFunction, valueFunction, HashBagMultimap.newMultimap());
    }

    @Override
    public <V2> FastListMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        return this.collectValues(function, FastListMultimap.newMultimap());
    }

    @Override
    public MutableListMultimap<K, V> asSynchronized()
    {
        throw new UnsupportedOperationException("Cannot call asSynchronized() on " + this.getClass().getSimpleName());
    }
}
