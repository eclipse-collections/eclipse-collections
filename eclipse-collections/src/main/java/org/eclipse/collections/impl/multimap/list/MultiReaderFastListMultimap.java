/*
 * Copyright (c) 2021 Goldman Sachs.
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
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.mutable.MultiReaderFastList;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.utility.Iterate;

public final class MultiReaderFastListMultimap<K, V>
        extends AbstractMutableListMultimap<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    // Default from FastList
    private static final int DEFAULT_CAPACITY = 1;

    private int initialListCapacity;

    public MultiReaderFastListMultimap()
    {
        this.initialListCapacity = DEFAULT_CAPACITY;
    }

    public MultiReaderFastListMultimap(int distinctKeys, int valuesPerKey)
    {
        super(Math.max(distinctKeys * 2, 16));
        if (distinctKeys < 0 || valuesPerKey < 0)
        {
            throw new IllegalArgumentException("Both arguments must be positive.");
        }
        this.initialListCapacity = valuesPerKey;
    }

    public MultiReaderFastListMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        this(
                multimap.keysView().size(),
                multimap instanceof MultiReaderFastListMultimap<?, ?> multiReaderFastListMultimap
                        ? multiReaderFastListMultimap.initialListCapacity
                        : DEFAULT_CAPACITY);
        this.putAll(multimap);
    }

    public MultiReaderFastListMultimap(Pair<K, V>... pairs)
    {
        super(pairs);
    }

    public MultiReaderFastListMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        super(inputIterable);
    }

    public static <K, V> MultiReaderFastListMultimap<K, V> newMultimap()
    {
        return new MultiReaderFastListMultimap<>();
    }

    public static <K, V> MultiReaderFastListMultimap<K, V> newMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        return new MultiReaderFastListMultimap<>(multimap);
    }

    public static <K, V> MultiReaderFastListMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return new MultiReaderFastListMultimap<>(pairs);
    }

    public static <K, V> MultiReaderFastListMultimap<K, V> newMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        return new MultiReaderFastListMultimap<>(inputIterable);
    }

    @Override
    protected MutableMap<K, MutableList<V>> createMap()
    {
        return ConcurrentHashMap.newMap();
    }

    @Override
    protected MutableMap<K, MutableList<V>> createMapWithKeyCount(int keyCount)
    {
        return ConcurrentHashMap.newMap(keyCount);
    }

    @Override
    protected MutableList<V> createCollection()
    {
        return MultiReaderFastList.newList(this.initialListCapacity);
    }

    @Override
    public MultiReaderFastListMultimap<K, V> newEmpty()
    {
        return new MultiReaderFastListMultimap<>();
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
}
