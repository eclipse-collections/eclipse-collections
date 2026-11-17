/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.set;

import java.io.Externalizable;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.utility.Iterate;

public final class UnifiedSetMultimap<K, V>
        extends AbstractMutableSetMultimap<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    public UnifiedSetMultimap()
    {
    }

    public UnifiedSetMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        super(Math.max(multimap.sizeDistinct() * 2, 16));
        this.putAll(multimap);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public UnifiedSetMultimap(Pair<K, V>... pairs)
    {
        super(pairs);
    }

    public UnifiedSetMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        super(inputIterable);
    }

    public static <K, V> UnifiedSetMultimap<K, V> newMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        return new UnifiedSetMultimap<>(multimap);
    }

    public static <K, V> UnifiedSetMultimap<K, V> newMultimap()
    {
        return new UnifiedSetMultimap<>();
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> UnifiedSetMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return new UnifiedSetMultimap<>(pairs);
    }

    public static <K, V> UnifiedSetMultimap<K, V> newMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        return new UnifiedSetMultimap<>(inputIterable);
    }

    @Override
    protected MutableMap<K, MutableSet<V>> createMap()
    {
        return Maps.mutable.empty();
    }

    @Override
    protected MutableMap<K, MutableSet<V>> createMapWithKeyCount(int keyCount)
    {
        return UnifiedMap.newMap(keyCount);
    }

    @Override
    protected MutableSet<V> createCollection()
    {
        return new UnifiedSet<>();
    }

    @Override
    public UnifiedSetMultimap<K, V> newEmpty()
    {
        return new UnifiedSetMultimap<>();
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public UnifiedSetMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public UnifiedSetMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public UnifiedSetMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, this.newEmpty());
    }

    @Override
    public UnifiedSetMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, this.newEmpty());
    }
}
