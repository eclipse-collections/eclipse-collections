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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure2;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

class ImmutableMapSerializationProxy<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private ImmutableMap<K, V> map;

    @SuppressWarnings("UnusedDeclaration")
    public ImmutableMapSerializationProxy()
    {
        // Empty constructor for Externalizable class
    }

    ImmutableMapSerializationProxy(ImmutableMap<K, V> map)
    {
        this.map = map;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.map.size());
        try
        {
            this.map.forEachKeyValue(new CheckedProcedure2<K, V>()
            {
                @Override
                public void safeValue(K key, V value) throws IOException
                {
                    out.writeObject(key);
                    out.writeObject(value);
                }
            });
        }
        catch (RuntimeException e)
        {
            if (e.getCause() instanceof IOException)
            {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int size = in.readInt();
        MutableMap<K, V> deserializedMap = UnifiedMap.newMap(size);

        for (int i = 0; i < size; i++)
        {
            if (deserializedMap.put((K) in.readObject(), (V) in.readObject()) != null)
            {
                throw new IllegalStateException();
            }
        }

        this.map = deserializedMap.toImmutable();
    }

    protected Object readResolve()
    {
        return this.map;
    }
}
