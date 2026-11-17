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

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.multimap.AbstractMutableMultimap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;

public abstract class AbstractMutableSetMultimap<K, V> extends AbstractMutableMultimap<K, V, MutableSet<V>> implements MutableSetMultimap<K, V>
{
    protected AbstractMutableSetMultimap()
    {
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    protected AbstractMutableSetMultimap(Pair<K, V>... pairs)
    {
        super(pairs);
    }

    protected AbstractMutableSetMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        super(inputIterable);
    }

    protected AbstractMutableSetMultimap(int size)
    {
        super(size);
    }

    @Override
    public MutableSetMultimap<K, V> toMutable()
    {
        return new UnifiedSetMultimap<>(this);
    }

    @Override
    public ImmutableSetMultimap<K, V> toImmutable()
    {
        MutableMap<K, ImmutableSet<V>> map = Maps.mutable.empty();

        this.map.forEachKeyValue((key, set) -> map.put(key, set.toImmutable()));

        return new ImmutableSetMultimapImpl<>(map);
    }

    @Override
    public void forEachKeyMutableSet(Procedure2<? super K, ? super MutableSet<V>> procedure)
    {
        this.getMap().forEachKeyValue((key, value) -> procedure.value(key, value.asUnmodifiable()));
    }

    @Override
    public <K2, V2> MutableBagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.collectKeysValues(function, HashBagMultimap.newMultimap());
    }

    @Override
    public <K2, V2> MutableBagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction)
    {
        return this.collectKeyMultiValues(keyFunction, valueFunction, HashBagMultimap.newMultimap());
    }

    @Override
    public <V2> MutableBagMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        return this.collectValues(function, HashBagMultimap.newMultimap());
    }

    @Override
    public MutableSetMultimap<K, V> asSynchronized()
    {
        return SynchronizedSetMultimap.of(this);
    }
}
