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
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.tuple.Tuples;

/**
 * TripletonMap is a memory-efficient implementation of a fixed-size map with exactly three entries.
 * <p>
 * This class uses minimal memory by storing three key-value pairs directly as fields,
 * avoiding the overhead of hash table structures. This is the largest fixed-size map implementation;
 * maps with four or more entries use UnifiedMap. It follows copy-on-write semantics for modifications.
 * <p>
 * <b>Key Characteristics:</b>
 * <ul>
 *   <li>Size is always 3</li>
 *   <li>Stores three key-value pairs directly as fields</li>
 *   <li>O(1) lookup with maximum 3 comparisons</li>
 *   <li>withKeyValue replaces value if key matches, otherwise returns UnifiedMap with 4 entries</li>
 *   <li>withoutKey returns DoubletonMap with the remaining two entries</li>
 *   <li>Supports null keys and values</li>
 *   <li>Externalizable for efficient serialization</li>
 * </ul>
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
final class TripletonMap<K, V>
        extends AbstractMemoryEfficientMutableMap<K, V>
        implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private K key1;
    private V value1;
    private K key2;
    private V value2;
    private K key3;
    private V value3;

    @SuppressWarnings("UnusedDeclaration")
    public TripletonMap()
    {
        // For Externalizable use only
    }

    TripletonMap(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        this.key1 = key1;
        this.value1 = value1;
        this.key2 = key2;
        this.value2 = value2;
        this.key3 = key3;
        this.value3 = value3;
    }

    K getKey1()
    {
        return this.key1;
    }

    K getKey2()
    {
        return this.key2;
    }

    K getKey3()
    {
        return this.key3;
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
        if (Objects.equals(this.key2, addKey))
        {
            this.value2 = addValue;
            return this;
        }
        if (Objects.equals(this.key3, addKey))
        {
            this.value3 = addValue;
            return this;
        }
        return UnifiedMap.newWithKeysValues(
                this.key1,
                this.value1,
                this.key2,
                this.value2,
                this.key3,
                this.value3,
                addKey,
                addValue);
    }

    @Override
    public MutableMap<K, V> withoutKey(K key)
    {
        if (Objects.equals(key, this.key1))
        {
            return new DoubletonMap<>(this.key2, this.value2, this.key3, this.value3);
        }
        if (Objects.equals(key, this.key2))
        {
            return new DoubletonMap<>(this.key1, this.value1, this.key3, this.value3);
        }
        if (Objects.equals(key, this.key3))
        {
            return new DoubletonMap<>(this.key1, this.value1, this.key2, this.value2);
        }
        return this;
    }

    // Weird implementation of clone() is ok on final classes
    @Override
    public TripletonMap<K, V> clone()
    {
        return new TripletonMap<>(this.key1, this.value1, this.key2, this.value2, this.key3, this.value3);
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        return Maps.immutable.with(this.key1, this.value1, this.key2, this.value2, this.key3, this.value3);
    }

    @Override
    public int size()
    {
        return 3;
    }

    @Override
    public boolean containsKey(Object key)
    {
        return Objects.equals(this.key3, key)
                || Objects.equals(this.key2, key)
                || Objects.equals(this.key1, key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return Objects.equals(this.value3, value)
                || Objects.equals(this.value2, value)
                || Objects.equals(this.value1, value);
    }

    @Override
    public V get(Object key)
    {
        if (Objects.equals(this.key3, key))
        {
            return this.value3;
        }
        if (Objects.equals(this.key2, key))
        {
            return this.value2;
        }
        if (Objects.equals(this.key1, key))
        {
            return this.value1;
        }
        return null;
    }

    @Override
    public Set<K> keySet()
    {
        return Sets.fixedSize.of(this.key1, this.key2, this.key3);
    }

    @Override
    public Collection<V> values()
    {
        return Lists.fixedSize.of(this.value1, this.value2, this.value3);
    }

    @Override
    public MutableSet<Entry<K, V>> entrySet()
    {
        return Sets.fixedSize.of(
                new ImmutableEntry<>(this.key1, this.value1),
                new ImmutableEntry<>(this.key2, this.value2),
                new ImmutableEntry<>(this.key3, this.value3));
    }

    @Override
    public int hashCode()
    {
        return this.keyAndValueHashCode(this.key1, this.value1)
                + this.keyAndValueHashCode(this.key2, this.value2)
                + this.keyAndValueHashCode(this.key3, this.value3);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Map))
        {
            return false;
        }
        Map<K, V> that = (Map<K, V>) other;
        return that.size() == this.size()
                && this.keyAndValueEquals(this.key1, this.value1, that)
                && this.keyAndValueEquals(this.key2, this.value2, that)
                && this.keyAndValueEquals(this.key3, this.value3, that);
    }

    @Override
    public String toString()
    {
        return "{"
                + this.key1 + '=' + this.value1 + ", "
                + this.key2 + '=' + this.value2 + ", "
                + this.key3 + '=' + this.value3 + '}';
    }

    @Override
    public MutableMap<V, K> flipUniqueValues()
    {
        if (Objects.equals(this.value1, this.value2))
        {
            throw new IllegalStateException("Duplicate value: " + this.value1 + " found at key: " + this.key1 + " and key: " + this.key2);
        }
        if (Objects.equals(this.value2, this.value3))
        {
            throw new IllegalStateException("Duplicate value: " + this.value2 + " found at key: " + this.key2 + " and key: " + this.key3);
        }
        if (Objects.equals(this.value3, this.value1))
        {
            throw new IllegalStateException("Duplicate value: " + this.value3 + " found at key: " + this.key3 + " and key: " + this.key3);
        }
        return new TripletonMap<>(this.value1, this.key1, this.value2, this.key2, this.value3, this.key3);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        procedure.value(this.key1, this.value1);
        procedure.value(this.key2, this.value2);
        procedure.value(this.key3, this.value3);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        procedure.value(this.key1);
        procedure.value(this.key2);
        procedure.value(this.key3);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        procedure.value(this.value1);
        procedure.value(this.value2);
        procedure.value(this.value3);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        objectIntProcedure.value(this.value1, 0);
        objectIntProcedure.value(this.value2, 1);
        objectIntProcedure.value(this.value3, 2);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        procedure.value(this.value1, parameter);
        procedure.value(this.value2, parameter);
        procedure.value(this.value3, parameter);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.key1);
        out.writeObject(this.value1);
        out.writeObject(this.key2);
        out.writeObject(this.value2);
        out.writeObject(this.key3);
        out.writeObject(this.value3);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.key1 = (K) in.readObject();
        this.value1 = (V) in.readObject();
        this.key2 = (K) in.readObject();
        this.value2 = (V) in.readObject();
        this.key3 = (K) in.readObject();
        this.value3 = (V) in.readObject();
    }

    @Override
    public FixedSizeMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return this.filter(predicate);
    }

    @Override
    public <R> FixedSizeMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return Maps.fixedSize.of(this.key1, function.value(this.key1, this.value1), this.key2, function.value(this.key2, this.value2), this.key3, function.value(this.key3, this.value3));
    }

    @Override
    public <R> FixedSizeMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function)
    {
        R key1 = function.value(this.key1, this.value1);
        R key2 = function.value(this.key2, this.value2);
        if (Objects.equals(key1, key2))
        {
            throw new IllegalStateException("Key " + key1 + " already exists in map!");
        }
        R key3 = function.value(this.key3, this.value3);
        if (Objects.equals(key1, key3))
        {
            throw new IllegalStateException("Key " + key1 + " already exists in map!");
        }
        if (Objects.equals(key2, key3))
        {
            throw new IllegalStateException("Key " + key2 + " already exists in map!");
        }
        return Maps.fixedSize.of(
                key1,
                this.value1,
                key2,
                this.value2,
                key3,
                this.value3);
    }

    @Override
    public <K2, V2> FixedSizeMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        Pair<K2, V2> pair1 = function.value(this.key1, this.value1);
        Pair<K2, V2> pair2 = function.value(this.key2, this.value2);
        Pair<K2, V2> pair3 = function.value(this.key3, this.value3);
        return Maps.fixedSize.of(pair1.getOne(), pair1.getTwo(), pair2.getOne(), pair2.getTwo(), pair3.getOne(), pair3.getTwo());
    }

    @Override
    public FixedSizeMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return this.filter(Predicates2.not(predicate));
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        if (predicate.accept(this.key1, this.value1))
        {
            return Tuples.pair(this.key1, this.value1);
        }
        if (predicate.accept(this.key2, this.value2))
        {
            return Tuples.pair(this.key2, this.value2);
        }
        if (predicate.accept(this.key3, this.value3))
        {
            return Tuples.pair(this.key3, this.value3);
        }
        return null;
    }

    private FixedSizeMap<K, V> filter(Predicate2<? super K, ? super V> predicate)
    {
        int result = 0;

        if (predicate.accept(this.key1, this.value1))
        {
            result |= 1;
        }
        if (predicate.accept(this.key2, this.value2))
        {
            result |= 2;
        }
        if (predicate.accept(this.key3, this.value3))
        {
            result |= 4;
        }

        switch (result)
        {
            case 1:
                return Maps.fixedSize.of(this.key1, this.value1);
            case 2:
                return Maps.fixedSize.of(this.key2, this.value2);
            case 3:
                return Maps.fixedSize.of(this.key1, this.value1, this.key2, this.value2);
            case 4:
                return Maps.fixedSize.of(this.key3, this.value3);
            case 5:
                return Maps.fixedSize.of(this.key1, this.value1, this.key3, this.value3);
            case 6:
                return Maps.fixedSize.of(this.key2, this.value2, this.key3, this.value3);
            case 7:
                return Maps.fixedSize.of(this.key1, this.value1, this.key2, this.value2, this.key3, this.value3);
            default:
                return Maps.fixedSize.of();
        }
    }

    @Override
    public V getOnly()
    {
        throw new IllegalStateException("Size must be 1 but was " + this.size());
    }
}
