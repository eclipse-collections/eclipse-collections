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
import java.util.Collection;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.utility.Iterate;

public final class FastListMultimap<K, V>
        extends AbstractMutableListMultimap<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    // Default from FastList
    private static final int DEFAULT_CAPACITY = 1;

    private int initialListCapacity;

    public FastListMultimap()
    {
        this.initialListCapacity = DEFAULT_CAPACITY;
    }

    public FastListMultimap(int distinctKeys, int valuesPerKey)
    {
        super(Math.max(distinctKeys * 2, 16));
        if (distinctKeys < 0 || valuesPerKey < 0)
        {
            throw new IllegalArgumentException("Both arguments must be positive.");
        }
        this.initialListCapacity = valuesPerKey;
    }

    public FastListMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        this(
                multimap.keysView().size(),
                multimap instanceof FastListMultimap<?, ?> fastListMultimap
                        ? fastListMultimap.initialListCapacity
                        : DEFAULT_CAPACITY);
        this.putAll(multimap);
    }

    public FastListMultimap(Pair<K, V>... pairs)
    {
        super(pairs);
    }

    public FastListMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        super(inputIterable);
    }

    public static <K, V> FastListMultimap<K, V> newMultimap()
    {
        return new FastListMultimap<>();
    }

    public static <K, V> FastListMultimap<K, V> newMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        return new FastListMultimap<>(multimap);
    }

    public static <K, V> FastListMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return new FastListMultimap<>(pairs);
    }

    public static <K, V> FastListMultimap<K, V> newMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        return new FastListMultimap<>(inputIterable);
    }

    @Override
    protected MutableMap<K, MutableList<V>> createMap()
    {
        return Maps.mutable.empty();
    }

    @Override
    protected MutableMap<K, MutableList<V>> createMapWithKeyCount(int keyCount)
    {
        return UnifiedMap.newMap(keyCount);
    }

    @Override
    protected MutableList<V> createCollection()
    {
        return Lists.mutable.withInitialCapacity(this.initialListCapacity);
    }

    public void trimToSize()
    {
        for (Collection<V> collection : this.map.values())
        {
            FastList<V> fastList = (FastList<V>) collection;
            fastList.trimToSize();
        }
    }

    @Override
    public FastListMultimap<K, V> newEmpty()
    {
        return new FastListMultimap<>();
    }

    @Override
    public MutableBagMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public FastListMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public FastListMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public FastListMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, this.newEmpty());
    }

    @Override
    public FastListMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, this.newEmpty());
    }
}
