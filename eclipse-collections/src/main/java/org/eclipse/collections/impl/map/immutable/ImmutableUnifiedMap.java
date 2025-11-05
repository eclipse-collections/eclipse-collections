/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.UnmodifiableMutableCollection;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.set.mutable.UnmodifiableMutableSet;

/**
 * ImmutableUnifiedMap is an immutable hash table-based implementation of {@link ImmutableMap}.
 * <p>
 * This implementation uses a {@link UnifiedMap} as its underlying data structure, providing
 * excellent performance for larger maps (5+ entries). It is optimized for memory efficiency
 * and fast lookups.
 * <p>
 * <b>Key Characteristics:</b>
 * <ul>
 *   <li>Immutable - cannot be modified after creation</li>
 *   <li>Thread-safe - safe for concurrent access</li>
 *   <li>O(1) average case lookup, insert (during creation), and removal operations</li>
 *   <li>Uses open addressing with quadratic probing</li>
 *   <li>Supports batch iteration for parallel processing</li>
 *   <li>Serializable for persistence</li>
 *   <li>Null-safe - supports null keys and values</li>
 * </ul>
 * <p>
 * For maps with 0-4 entries, consider using the specialized implementations (ImmutableSingletonMap,
 * ImmutableDoubletonMap, etc.) which are more memory-efficient.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableMap<String, Integer> map = new ImmutableUnifiedMap<>(
 *     Tuples.pair("a", 1),
 *     Tuples.pair("b", 2),
 *     Tuples.pair("c", 3),
 *     Tuples.pair("d", 4),
 *     Tuples.pair("e", 5)
 * );
 *
 * ImmutableMap<String, Integer> fromExisting = new ImmutableUnifiedMap<>(existingMap);
 * }</pre>
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @see ImmutableMap
 * @see UnifiedMap
 */
public class ImmutableUnifiedMap<K, V>
        extends AbstractImmutableMap<K, V> implements BatchIterable<V>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final UnifiedMap<K, V> delegate;

    public ImmutableUnifiedMap(Map<? extends K, ? extends V> delegate)
    {
        this.delegate = UnifiedMap.newMap(delegate);
    }

    public ImmutableUnifiedMap(Pair<K, V>... pairs)
    {
        this(UnifiedMap.newMapWith(pairs));
    }

    @Override
    public boolean equals(Object o)
    {
        return this.delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public String toString()
    {
        return this.delegate.toString();
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    @Override
    public V get(Object key)
    {
        return this.delegate.get(key);
    }

    @Override
    public int getBatchCount(int batchSize)
    {
        return this.delegate.getBatchCount(batchSize);
    }

    @Override
    public void batchForEach(Procedure<? super V> procedure, int sectionIndex, int sectionCount)
    {
        this.delegate.batchForEach(procedure, sectionIndex, sectionCount);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.delegate.forEachValue(procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.delegate.forEachKey(procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        this.delegate.forEachKeyValue(procedure);
    }

    @Override
    public Set<K> keySet()
    {
        return UnmodifiableMutableSet.of(this.delegate.keySet());
    }

    @Override
    public Collection<V> values()
    {
        return UnmodifiableMutableCollection.of(this.delegate.values());
    }

    @Override
    public RichIterable<K> keysView()
    {
        return this.delegate.keysView();
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return this.delegate.valuesView();
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return this.delegate.keyValuesView();
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.delegate.forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        this.delegate.forEachWith(procedure, parameter);
    }

    protected Object writeReplace()
    {
        return new ImmutableMapSerializationProxy<>(this);
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        return this.delegate.ifPresentApply(key, function);
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        return this.delegate.getIfAbsent(key, function);
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        return this.delegate.getIfAbsentValue(key, value);
    }

    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        return this.delegate.getIfAbsentWith(key, function, parameter);
    }
}
