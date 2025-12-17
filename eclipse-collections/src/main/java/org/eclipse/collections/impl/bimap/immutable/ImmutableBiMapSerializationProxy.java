/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bimap.immutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.bimap.ImmutableBiMap;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure2;

class ImmutableBiMapSerializationProxy<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private ImmutableBiMap<K, V> biMap;

    @SuppressWarnings("UnusedDeclaration")
    public ImmutableBiMapSerializationProxy()
    {
        // Empty constructor for Externalizable class
    }

    ImmutableBiMapSerializationProxy(ImmutableBiMap<K, V> biMap)
    {
        this.biMap = biMap;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.biMap.size());
        try
        {
            this.biMap.forEachKeyValue(new CheckedProcedure2<K, V>()
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
        MutableBiMap<K, V> deserializedBiMap = new HashBiMap<>(size);

        for (int i = 0; i < size; i++)
        {
            if (deserializedBiMap.put((K) in.readObject(), (V) in.readObject()) != null)
            {
                throw new IllegalStateException();
            }
        }

        this.biMap = deserializedBiMap.toImmutable();
    }

    protected Object readResolve()
    {
        return this.biMap;
    }
}
