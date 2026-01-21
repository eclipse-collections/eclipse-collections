/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.tuple;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;

public class AbstractImmutableEntry<K, V> implements Map.Entry<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final PairFunction<?, ?> TO_PAIR = new PairFunction<>();

    protected final K key;
    protected final V value;

    public AbstractImmutableEntry(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * @deprecated Since 6.2 - Use {@link Functions#getKeyFunction()} instead.
     */
    @Deprecated
    public static <K> Function<Map.Entry<K, ?>, K> getKeyFunction()
    {
        return Functions.getKeyFunction();
    }

    /**
     * @deprecated Since 6.2 - Use {@link Functions#getValueFunction()} instead.
     */
    @Deprecated
    public static <V> Function<Map.Entry<?, V>, V> getValueFunction()
    {
        return Functions.getValueFunction();
    }

    public static <K, V> Function<Map.Entry<K, V>, Pair<K, V>> getPairFunction()
    {
        return (Function<Map.Entry<K, V>, Pair<K, V>>) (Function<?, ?>) TO_PAIR;
    }

    @Override
    public K getKey()
    {
        return this.key;
    }

    @Override
    public V getValue()
    {
        return this.value;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation throws an {@link UnsupportedOperationException}. Override this method to support mutable
     * map entries.
     */
    @Override
    public V setValue(V value)
    {
        throw new UnsupportedOperationException("Cannot call setValue() on " + this.getClass().getSimpleName());
    }

    /**
     * Returns a string representation of the form {@code {key}={value}}.
     */
    @Override
    public String toString()
    {
        return this.key + "=" + this.value;
    }

    private static class PairFunction<K, V> implements Function<Map.Entry<K, V>, Pair<K, V>>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Pair<K, V> valueOf(Map.Entry<K, V> entry)
        {
            return Tuples.pairFrom(entry);
        }
    }
}
