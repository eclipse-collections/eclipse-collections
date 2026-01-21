/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.sorted.immutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;

import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure2;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

class ImmutableSortedMapSerializationProxy<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;
    private ImmutableSortedMap<K, V> map;

    @SuppressWarnings("UnusedDeclaration")
    public ImmutableSortedMapSerializationProxy()
    {
        // Empty constructor for Externalizable class
    }

    ImmutableSortedMapSerializationProxy(ImmutableSortedMap<K, V> map)
    {
        this.map = map;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.map.comparator());
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
        Comparator<? super K> comparator = (Comparator<? super K>) in.readObject();
        int size = in.readInt();
        MutableSortedMap<K, V> deserializedMap = TreeSortedMap.newMap(comparator);

        for (int i = 0; i < size; i++)
        {
            deserializedMap.put((K) in.readObject(), (V) in.readObject());
        }

        this.map = deserializedMap.toImmutable();
    }

    protected Object readResolve()
    {
        return this.map;
    }
}
