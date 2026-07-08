/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure;

import java.util.Objects;

import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.map.MapIterable;

public class MapIterableToStringProcedure<K, V> implements Procedure2<K, V>
{
    private static final long serialVersionUID = 1L;
    private final StringBuilder builder = new StringBuilder();
    private final MapIterable<?, ?> map;
    private boolean first = true;

    public MapIterableToStringProcedure(MapIterable<?, ?> map)
    {
        this.map = Objects.requireNonNull(map);
    }

    @Override
    public void value(K key, V value)
    {
        if (this.first)
        {
            this.first = false;
        }
        else
        {
            this.builder.append(", ");
        }

        this.builder.append(key == this.map ? "(this Map)" : key);
        this.builder.append('=');
        this.builder.append(value == this.map ? "(this Map)" : value);
    }

    public String getString()
    {
        return '{' + this.builder.toString() + '}';
    }
}
