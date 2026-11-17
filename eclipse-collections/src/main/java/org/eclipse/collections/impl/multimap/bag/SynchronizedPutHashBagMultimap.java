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
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.multimap.AbstractSynchronizedPutMultimap;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * A Multimap that is optimized for parallel writes, but is not protected for concurrent reads.
 */
public final class SynchronizedPutHashBagMultimap<K, V>
        extends AbstractSynchronizedPutMultimap<K, V, MutableBag<V>> implements MutableBagMultimap<K, V>, Externalizable
{
    private static final long serialVersionUID = 42L;

    public SynchronizedPutHashBagMultimap()
    {
    }

    public SynchronizedPutHashBagMultimap(int initialCapacity)
    {
        super(ConcurrentHashMap.newMap(initialCapacity));
    }

    public SynchronizedPutHashBagMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        this.putAll(multimap);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public SynchronizedPutHashBagMultimap(Pair<K, V>... pairs)
    {
        ArrayIterate.forEach(pairs, pair -> this.put(pair.getOne(), pair.getTwo()));
    }

    public SynchronizedPutHashBagMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        Iterate.forEach(inputIterable, this::add);
    }

    public static <K, V> SynchronizedPutHashBagMultimap<K, V> newMultimap()
    {
        return new SynchronizedPutHashBagMultimap<>();
    }

    public static <K, V> SynchronizedPutHashBagMultimap<K, V> newMultimap(int initialCapacity, float loadFactor, int concurrencyLevel)
    {
        return new SynchronizedPutHashBagMultimap<>(initialCapacity);
    }

    public static <K, V> SynchronizedPutHashBagMultimap<K, V> newMultimap(Multimap<? extends K, ? extends V> multimap)
    {
        return new SynchronizedPutHashBagMultimap<>(multimap);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> SynchronizedPutHashBagMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return new SynchronizedPutHashBagMultimap<>(pairs);
    }

    public static <K, V> SynchronizedPutHashBagMultimap<K, V> newMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        return new SynchronizedPutHashBagMultimap<>(inputIterable);
    }

    @Override
    public SynchronizedPutHashBagMultimap<K, V> withKeyMultiValues(K key, V... values)
    {
        return (SynchronizedPutHashBagMultimap<K, V>) super.withKeyMultiValues(key, values);
    }

    @Override
    protected MutableBag<V> createCollection()
    {
        return Bags.mutable.withInitialCapacity(1);
    }

    @Override
    public void forEachKeyMutableBag(Procedure2<? super K, ? super MutableBag<V>> procedure)
    {
        this.getMap().forEachKeyValue((key, value) -> procedure.value(key, value.asUnmodifiable()));
    }

    @Override
    public SynchronizedPutHashBagMultimap<K, V> newEmpty()
    {
        return new SynchronizedPutHashBagMultimap<>();
    }

    @Override
    public MutableBagMultimap<K, V> toMutable()
    {
        return new SynchronizedPutHashBagMultimap<>(this);
    }

    @Override
    public ImmutableBagMultimap<K, V> toImmutable()
    {
        MutableMap<K, ImmutableBag<V>> map = Maps.mutable.empty();

        this.map.forEachKeyValue((key, bag) -> map.put(key, bag.toImmutable()));

        return new ImmutableBagMultimapImpl<>(map);
    }

    @Override
    public HashBagMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, HashBagMultimap.newMultimap());
    }

    @Override
    public HashBagMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, HashBagMultimap.newMultimap());
    }

    @Override
    public HashBagMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, HashBagMultimap.newMultimap());
    }

    @Override
    public HashBagMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, HashBagMultimap.newMultimap());
    }

    @Override
    public <K2, V2> HashBagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.collectKeysValues(function, HashBagMultimap.newMultimap());
    }

    @Override
    public <K2, V2> MutableBagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction)
    {
        return this.collectKeyMultiValues(keyFunction, valueFunction, HashBagMultimap.newMultimap());
    }

    @Override
    public <V2> HashBagMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        return this.collectValues(function, HashBagMultimap.newMultimap());
    }

    @Override
    public MutableBagMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public void putOccurrences(K key, V value, int occurrences)
    {
        if (occurrences < 0)
        {
            throw new IllegalArgumentException("Cannot add a negative number of occurrences");
        }

        if (occurrences > 0)
        {
            MutableBag<V> bag = this.map.getIfAbsentPutWith(key, this.createCollectionBlock(), this);

            synchronized (bag)
            {
                bag.addOccurrences(value, occurrences);
                this.addToTotalSize(occurrences);
            }
        }
    }

    @Override
    public MutableBagMultimap<K, V> asSynchronized()
    {
        throw new UnsupportedOperationException("Cannot call asSynchronized() on " + this.getClass().getSimpleName());
    }
}
