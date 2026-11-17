/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.bag;

import java.io.Externalizable;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.utility.Iterate;

public final class HashBagMultimap<K, V>
        extends AbstractMutableBagMultimap<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    public HashBagMultimap()
    {
    }

    public HashBagMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        super(Math.max(multimap.keysView().size() * 2, 16));
        this.putAll(multimap);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public HashBagMultimap(Pair<K, V>... pairs)
    {
        super(pairs);
    }

    public HashBagMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        super(inputIterable);
    }

    public static <K, V> HashBagMultimap<K, V> newMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        return new HashBagMultimap<>(multimap);
    }

    public static <K, V> HashBagMultimap<K, V> newMultimap()
    {
        return new HashBagMultimap<>();
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> HashBagMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return new HashBagMultimap<>(pairs);
    }

    public static <K, V> HashBagMultimap<K, V> newMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        return new HashBagMultimap<>(inputIterable);
    }

    @Override
    protected MutableMap<K, MutableBag<V>> createMap()
    {
        return Maps.mutable.empty();
    }

    @Override
    protected MutableMap<K, MutableBag<V>> createMapWithKeyCount(int keyCount)
    {
        return UnifiedMap.newMap(keyCount);
    }

    @Override
    protected MutableBag<V> createCollection()
    {
        return HashBag.newBag();
    }

    @Override
    public HashBagMultimap<K, V> newEmpty()
    {
        return new HashBagMultimap<>();
    }

    @Override
    public MutableBagMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public <V2> HashBagMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        return this.collectValues(function, HashBagMultimap.newMultimap());
    }

    @Override
    public HashBagMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public HashBagMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public HashBagMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, this.newEmpty());
    }

    @Override
    public HashBagMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, this.newEmpty());
    }
}
