/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.bag;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.checked.CheckedObjectIntProcedure;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure2;
import org.eclipse.collections.impl.multimap.AbstractMutableMultimap;

public abstract class AbstractMutableBagMultimap<K, V> extends AbstractMutableMultimap<K, V, MutableBag<V>> implements MutableBagMultimap<K, V>
{
    protected AbstractMutableBagMultimap()
    {
    }

    protected AbstractMutableBagMultimap(Pair<K, V>... pairs)
    {
        super(pairs);
    }

    protected AbstractMutableBagMultimap(Iterable<Pair<K, V>> inputIterable)
    {
        super(inputIterable);
    }

    protected AbstractMutableBagMultimap(int size)
    {
        super(size);
    }

    @Override
    public MutableBagMultimap<K, V> toMutable()
    {
        MutableBagMultimap<K, V> mutableBagMultimap = this.newEmpty();
        mutableBagMultimap.putAll(this);
        return mutableBagMultimap;
    }

    @Override
    public ImmutableBagMultimap<K, V> toImmutable()
    {
        MutableMap<K, ImmutableBag<V>> result = (MutableMap<K, ImmutableBag<V>>) (MutableMap<?, ?>) this.createMapWithKeyCount(this.map.size());

        this.map.forEachKeyValue((key, bag) -> result.put(key, bag.toImmutable()));

        return new ImmutableBagMultimapImpl<>(result);
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
    public void writeExternal(ObjectOutput out) throws IOException
    {
        int keysCount = this.map.size();
        out.writeInt(keysCount);
        this.map.forEachKeyValue(new CheckedProcedure2<K, MutableBag<V>>()
        {
            @Override
            public void safeValue(K key, MutableBag<V> bag) throws IOException
            {
                out.writeObject(key);
                out.writeInt(bag.sizeDistinct());
                bag.forEachWithOccurrences(new CheckedObjectIntProcedure<V>()
                {
                    @Override
                    public void safeValue(V value, int count) throws IOException
                    {
                        out.writeObject(value);
                        out.writeInt(count);
                    }
                });
            }
        });
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int keyCount = in.readInt();
        this.map = this.createMapWithKeyCount(keyCount);
        for (int i = 0; i < keyCount; i++)
        {
            K key = (K) in.readObject();
            int valuesSize = in.readInt();
            MutableBag<V> bag = this.createCollection();
            for (int j = 0; j < valuesSize; j++)
            {
                V value = (V) in.readObject();
                int count = in.readInt();

                bag.addOccurrences(value, count);
            }
            this.putAll(key, bag);
        }
    }

    @Override
    public void forEachKeyMutableBag(Procedure2<? super K, ? super MutableBag<V>> procedure)
    {
        this.getMap().forEachKeyValue((key, value) -> procedure.value(key, value.asUnmodifiable()));
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

            bag.addOccurrences(value, occurrences);
            this.addToTotalSize(occurrences);
        }
    }

    @Override
    public MutableBagMultimap<K, V> asSynchronized()
    {
        return SynchronizedBagMultimap.of(this);
    }
}
