/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.bag.strategy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.map.strategy.mutable.UnifiedMapWithHashingStrategy;
import org.eclipse.collections.impl.multimap.bag.AbstractMutableBagMultimap;
import org.eclipse.collections.impl.utility.Iterate;

public final class HashBagMultimapWithHashingStrategy<K, V>
        extends AbstractMutableBagMultimap<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;
    private HashingStrategy<? super K> hashingStrategy;

    /**
     * @deprecated Empty default constructor used for serialization. Instantiating an HashBagMultimapWithHashingStrategy with
     * this constructor will have a null multimapHashingStrategy, and throw NullPointerException when used.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public HashBagMultimapWithHashingStrategy()
    {
        // For Externalizable use only
    }

    public HashBagMultimapWithHashingStrategy(HashingStrategy<? super K> hashingStrategy)
    {
        this.hashingStrategy = hashingStrategy;
        this.map = this.createMap();
    }

    public HashBagMultimapWithHashingStrategy(HashBagMultimapWithHashingStrategy<K, V> multimap)
    {
        this(multimap.hashingStrategy, multimap);
    }

    public HashBagMultimapWithHashingStrategy(HashingStrategy<? super K> hashingStrategy, Multimap<? extends K, ? extends V> multimap)
    {
        this.hashingStrategy = hashingStrategy;
        this.map = this.createMapWithKeyCount(Math.max(multimap.sizeDistinct() * 2, 16));
        this.putAll(multimap);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public HashBagMultimapWithHashingStrategy(HashingStrategy<? super K> hashingStrategy, Pair<K, V>... pairs)
    {
        this(hashingStrategy);
        this.putAllPairs(pairs);
    }

    public HashBagMultimapWithHashingStrategy(HashingStrategy<? super K> hashingStrategy, Iterable<Pair<K, V>> inputIterable)
    {
        this(hashingStrategy);
        for (Pair<K, V> single : inputIterable)
        {
            this.add(single);
        }
    }

    public static <K, V> HashBagMultimapWithHashingStrategy<K, V> newMultimap(HashBagMultimapWithHashingStrategy<K, V> multimap)
    {
        return new HashBagMultimapWithHashingStrategy<>(multimap);
    }

    public static <K, V> HashBagMultimapWithHashingStrategy<K, V> newMultimap(HashingStrategy<? super K> multimapHashingStrategy, Multimap<? extends K, ? extends V> multimap)
    {
        return new HashBagMultimapWithHashingStrategy<>(multimapHashingStrategy, multimap);
    }

    public static <K, V> HashBagMultimapWithHashingStrategy<K, V> newMultimap(HashingStrategy<? super K> multimapHashingStrategy)
    {
        return new HashBagMultimapWithHashingStrategy<>(multimapHashingStrategy);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> HashBagMultimapWithHashingStrategy<K, V> newMultimap(HashingStrategy<? super K> multimapHashingStrategy, Pair<K, V>... pairs)
    {
        return new HashBagMultimapWithHashingStrategy<>(multimapHashingStrategy, pairs);
    }

    public static <K, V> HashBagMultimapWithHashingStrategy<K, V> newMultimap(HashingStrategy<? super K> multimapHashingStrategy, Iterable<Pair<K, V>> inputIterable)
    {
        return new HashBagMultimapWithHashingStrategy<>(multimapHashingStrategy, inputIterable);
    }

    @Override
    protected MutableMap<K, MutableBag<V>> createMap()
    {
        return UnifiedMapWithHashingStrategy.newMap(this.hashingStrategy);
    }

    @Override
    protected MutableMap<K, MutableBag<V>> createMapWithKeyCount(int keyCount)
    {
        return UnifiedMapWithHashingStrategy.newMap(this.hashingStrategy, keyCount);
    }

    @Override
    protected MutableBag<V> createCollection()
    {
        return HashBag.newBag();
    }

    public HashingStrategy<? super K> getKeyHashingStrategy()
    {
        return this.hashingStrategy;
    }

    @Override
    public HashBagMultimapWithHashingStrategy<K, V> newEmpty()
    {
        return new HashBagMultimapWithHashingStrategy<>(this.hashingStrategy);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.hashingStrategy);
        super.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.hashingStrategy = (HashingStrategy<? super K>) in.readObject();
        super.readExternal(in);
    }

    // Currently, this returns a HashBagMultimap.
    // In a future release, it will return HashBagWithHashingStrategyMultimap, where the HashBag collection hashing strategy
    // will be the hashing strategy of this multimap
    @Override
    public MutableBagMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public <V2> HashBagMultimapWithHashingStrategy<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        return this.collectValues(function, HashBagMultimapWithHashingStrategy.newMultimap(this.hashingStrategy));
    }

    @Override
    public HashBagMultimapWithHashingStrategy<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public HashBagMultimapWithHashingStrategy<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public HashBagMultimapWithHashingStrategy<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, this.newEmpty());
    }

    @Override
    public HashBagMultimapWithHashingStrategy<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, this.newEmpty());
    }
}
