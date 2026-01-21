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
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.checked.CheckedObjectIntProcedure;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure2;
import org.eclipse.collections.impl.multimap.AbstractImmutableMultimap;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * The default ImmutableBagMultimap implementation.
 *
 * @since 1.0
 */
public final class ImmutableBagMultimapImpl<K, V>
        extends AbstractImmutableMultimap<K, V, ImmutableBag<V>>
        implements ImmutableBagMultimap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    public ImmutableBagMultimapImpl(MutableMap<K, ImmutableBag<V>> map)
    {
        super(map);
    }

    public ImmutableBagMultimapImpl(ImmutableMap<K, ImmutableBag<V>> map)
    {
        super(map);
    }

    @Override
    protected ImmutableBag<V> createCollection()
    {
        return Bags.immutable.empty();
    }

    @Override
    public void forEachKeyImmutableBag(Procedure2<? super K, ? super ImmutableBag<V>> procedure)
    {
        this.getMap().forEachKeyValue(procedure);
    }

    @Override
    public ImmutableBagMultimap<K, V> newEmpty()
    {
        return new ImmutableBagMultimapImpl<>(Maps.immutable.of());
    }

    @Override
    public MutableBagMultimap<K, V> toMutable()
    {
        return new HashBagMultimap<>(this);
    }

    @Override
    public ImmutableBagMultimap<K, V> toImmutable()
    {
        return this;
    }

    private Object writeReplace()
    {
        return new ImmutableBagMultimapSerializationProxy<>(this.map);
    }

    private static class ImmutableBagMultimapSerializationProxy<K, V>
            implements Externalizable
    {
        private static final long serialVersionUID = 1L;

        private ImmutableMap<K, ImmutableBag<V>> map;
        private MutableMultimap<K, V> multimap;

        public ImmutableBagMultimapSerializationProxy()
        {
            // For Externalizable use only
        }

        private ImmutableBagMultimapSerializationProxy(ImmutableMap<K, ImmutableBag<V>> map)
        {
            this.map = map;
        }

        protected Object readResolve()
        {
            return this.multimap.toImmutable();
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
        {
            this.multimap = new HashBagMultimap<>();
            int keyCount = in.readInt();
            for (int i = 0; i < keyCount; i++)
            {
                K key = (K) in.readObject();
                int valuesSize = in.readInt();
                MutableBag<V> bag = Bags.mutable.empty();
                for (int j = 0; j < valuesSize; j++)
                {
                    V value = (V) in.readObject();
                    int count = in.readInt();

                    bag.addOccurrences(value, count);
                }
                this.multimap.putAll(key, bag);
            }
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException
        {
            int keysCount = this.map.size();
            out.writeInt(keysCount);
            this.map.forEachKeyValue(new CheckedProcedure2<K, ImmutableBag<V>>()
            {
                @Override
                public void safeValue(K key, ImmutableBag<V> bag) throws IOException
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
    }

    @Override
    public ImmutableBagMultimap<K, V> newWith(K key, V value)
    {
        return (ImmutableBagMultimap<K, V>) super.newWith(key, value);
    }

    @Override
    public ImmutableBagMultimap<K, V> newWithout(Object key, Object value)
    {
        return (ImmutableBagMultimap<K, V>) super.newWithout(key, value);
    }

    @Override
    public ImmutableBagMultimap<K, V> newWithAll(K key, Iterable<? extends V> values)
    {
        return (ImmutableBagMultimap<K, V>) super.newWithAll(key, values);
    }

    @Override
    public ImmutableBagMultimap<K, V> newWithoutAll(Object key)
    {
        return (ImmutableBagMultimap<K, V>) super.newWithoutAll(key);
    }

    @Override
    public ImmutableBagMultimap<V, K> flip()
    {
        return Iterate.flip(this).toImmutable();
    }

    @Override
    public ImmutableBagMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, HashBagMultimap.newMultimap()).toImmutable();
    }

    @Override
    public ImmutableBagMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, HashBagMultimap.newMultimap()).toImmutable();
    }

    @Override
    public ImmutableBagMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, HashBagMultimap.newMultimap()).toImmutable();
    }

    @Override
    public ImmutableBagMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, HashBagMultimap.newMultimap()).toImmutable();
    }

    @Override
    public <K2, V2> ImmutableBagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.collectKeysValues(function, HashBagMultimap.newMultimap()).toImmutable();
    }

    @Override
    public <K2, V2> ImmutableBagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction)
    {
        MutableBagMultimap<K2, V2> collectedMultimap = this.collectKeyMultiValues(keyFunction, valueFunction, HashBagMultimap.newMultimap());
        return collectedMultimap.toImmutable();
    }

    @Override
    public <V2> ImmutableBagMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        return this.collectValues(function, HashBagMultimap.<K, V2>newMultimap()).toImmutable();
    }
}
