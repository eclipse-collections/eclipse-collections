/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.strategy.immutable;

import java.util.Map.Entry;
import java.util.Objects;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.impl.tuple.AbstractImmutableEntry;

public final class ImmutableEntryWithHashingStrategy<K, V> extends AbstractImmutableEntry<K, V>
{
    private static final long serialVersionUID = 1L;

    private final HashingStrategy<? super K> hashingStrategy;

    public ImmutableEntryWithHashingStrategy(K key, V value, HashingStrategy<? super K> hashingStrategy)
    {
        super(key, value);
        this.hashingStrategy = hashingStrategy;
    }

    public static <T1, T2> ImmutableEntryWithHashingStrategy<T1, T2> of(T1 key, T2 value, HashingStrategy<? super T1> hashingStrategy)
    {
        return new ImmutableEntryWithHashingStrategy<>(key, value, hashingStrategy);
    }

    /**
     * Indicates whether an object equals this entry, following the behavior specified in {@link java.util.Map.Entry#equals(Object)}.
     */
    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Entry<?, ?> that)
        {
            return this.hashingStrategy.equals(this.key, (K) that.getKey())
                    && Objects.equals(this.value, that.getValue());
        }
        return false;
    }

    /**
     * Return this entry's hash code, following the behavior specified in {@link java.util.Map.Entry#hashCode()}.
     */
    @Override
    public int hashCode()
    {
        K key = this.key;
        V value = this.value;
        return this.hashingStrategy.computeHashCode(key)
                ^ (value == null ? 0 : value.hashCode());
    }
}
