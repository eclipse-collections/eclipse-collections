/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.fixed;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.tuple.Tuples;

/**
 * SingletonMap is a memory-efficient implementation of a fixed-size map with exactly one entry.
 * <p>
 * This class uses minimal memory by storing the single key-value pair directly as fields,
 * avoiding the overhead of hash table structures. It follows copy-on-write semantics for modifications.
 * <p>
 * <b>Key Characteristics:</b>
 * <ul>
 *   <li>Size is always 1</li>
 *   <li>Stores one key-value pair directly as fields</li>
 *   <li>O(1) lookup, always comparing against the single key</li>
 *   <li>withKeyValue replaces the value if key matches, otherwise returns DoubletonMap</li>
 *   <li>withoutKey returns EmptyMap if key matches, otherwise returns this</li>
 *   <li>Supports null keys and values</li>
 *   <li>Externalizable for efficient serialization</li>
 * </ul>
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
final class SingletonMap<K, V>
        extends AbstractMemoryEfficientMutableMap<K, V>
        implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private K key1;
    private V value1;

    @SuppressWarnings("UnusedDeclaration")
    public SingletonMap()
    {
        // For Externalizable use only
    }

    SingletonMap(K key1, V value1)
    {
        this.key1 = key1;
        this.value1 = value1;
    }

    @Override
    public int size()
    {
        return 1;
    }

    K getKey1()
    {
        return this.key1;
    }

    @Override
    public MutableMap<K, V> withKeyValue(K addKey, V addValue)
    {
        // Map behavior specifies that if you put in a duplicate key, you replace the value
        if (Objects.equals(this.key1, addKey))
        {
            this.value1 = addValue;
            return this;
        }
        return new DoubletonMap<>(this.key1, this.value1, addKey, addValue);
    }

    @Override
    public MutableMap<K, V> withoutKey(K key)
    {
        if (Objects.equals(key, this.key1))
        {
            return new EmptyMap<>();
        }
        return this;
    }

    // Weird implementation of clone() is ok on final classes
    @Override
    public SingletonMap<K, V> clone()
    {
        return new SingletonMap<>(this.key1, this.value1);
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        return Maps.immutable.with(this.key1, this.value1);
    }

    @Override
    public boolean containsKey(Object key)
    {
        return Objects.equals(this.key1, key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return Objects.equals(this.value1, value);
    }

    @Override
    public V get(Object key)
    {
        if (Objects.equals(this.key1, key))
        {
            return this.value1;
        }

        return null;
    }

    @Override
    public Set<K> keySet()
    {
        return Sets.fixedSize.of(this.key1);
    }

    @Override
    public Collection<V> values()
    {
        return Lists.fixedSize.of(this.value1);
    }

    @Override
    public MutableSet<Entry<K, V>> entrySet()
    {
        return Sets.fixedSize.of(new ImmutableEntry<>(this.key1, this.value1));
    }

    @Override
    public String toString()
    {
        return '{' + String.valueOf(this.key1) + '=' + this.value1 + '}';
    }

    @Override
    public int hashCode()
    {
        return this.keyAndValueHashCode(this.key1, this.value1);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Map))
        {
            return false;
        }
        Map<K, V> that = (Map<K, V>) other;
        return that.size() == this.size() && this.keyAndValueEquals(this.key1, this.value1, that);
    }

    @Override
    public MutableMap<V, K> flipUniqueValues()
    {
        return Maps.fixedSize.with(this.value1, this.key1);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        procedure.value(this.key1, this.value1);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        procedure.value(this.key1);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        procedure.value(this.value1);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        objectIntProcedure.value(this.value1, 0);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        procedure.value(this.value1, parameter);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.key1);
        out.writeObject(this.value1);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.key1 = (K) in.readObject();
        this.value1 = (V) in.readObject();
    }

    @Override
    public FixedSizeMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        if (predicate.accept(this.key1, this.value1))
        {
            return this.clone();
        }
        return Maps.fixedSize.of();
    }

    @Override
    public <R> FixedSizeMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return Maps.fixedSize.of(this.key1, function.value(this.key1, this.value1));
    }

    @Override
    public <R> FixedSizeMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        return Maps.fixedSize.of(function.value(this.key1, this.value1), this.value1);
    }

    @Override
    public <K2, V2> FixedSizeMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        Pair<K2, V2> pair1 = function.value(this.key1, this.value1);
        return Maps.fixedSize.of(pair1.getOne(), pair1.getTwo());
    }

    @Override
    public FixedSizeMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        if (predicate.accept(this.key1, this.value1))
        {
            return Maps.fixedSize.of();
        }
        return this.clone();
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        if (predicate.accept(this.key1, this.value1))
        {
            return Tuples.pair(this.key1, this.value1);
        }
        return null;
    }

    @Override
    public V getOnly()
    {
        return this.value1;
    }
}
