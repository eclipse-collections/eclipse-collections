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

import java.util.Map.Entry;
import java.util.Objects;

public final class ImmutableEntry<K, V> extends AbstractImmutableEntry<K, V>
{
    private static final long serialVersionUID = 1L;

    public ImmutableEntry(K key, V value)
    {
        super(key, value);
    }

    public static <T1, T2> ImmutableEntry<T1, T2> of(T1 key, T2 value)
    {
        return new ImmutableEntry<>(key, value);
    }

    /**
     * Indicates whether an object equals this entry, following the behavior specified in {@link java.util.Map.Entry#equals(Object)}.
     */
    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Entry<?, ?> that)
        {
            return Objects.equals(this.key, that.getKey())
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
        return (key == null ? 0 : key.hashCode())
                ^ (value == null ? 0 : value.hashCode());
    }
}
