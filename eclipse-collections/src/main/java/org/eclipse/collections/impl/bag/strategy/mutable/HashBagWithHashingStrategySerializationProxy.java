/*
 * Copyright (c) 2021 Bhavana Hindupur.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.strategy.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.impl.block.procedure.checked.CheckedObjectIntProcedure;

class HashBagWithHashingStrategySerializationProxy<T> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private HashBagWithHashingStrategy<T> bagWithHashingStrategy;

    @SuppressWarnings("UnusedDeclaration")
    public HashBagWithHashingStrategySerializationProxy()
    {
        // Empty constructor for Externalizable class
    }

    HashBagWithHashingStrategySerializationProxy(HashBagWithHashingStrategy<T> bagWithHashingStrategy)
    {
        this.bagWithHashingStrategy = bagWithHashingStrategy;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.bagWithHashingStrategy.hashingStrategy());
        out.writeInt(this.bagWithHashingStrategy.sizeDistinct());
        try
        {
            this.bagWithHashingStrategy.forEachWithOccurrences(new CheckedObjectIntProcedure<T>()
            {
                @Override
                public void safeValue(T object, int index) throws IOException
                {
                    out.writeObject(object);
                    out.writeInt(index);
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
        HashingStrategy<T> hashingStrategy = (HashingStrategy<T>) in.readObject();
        int size = in.readInt();
        HashBagWithHashingStrategy<T> deserializedBag = new HashBagWithHashingStrategy<>(hashingStrategy, size);

        for (int i = 0; i < size; i++)
        {
            deserializedBag.addOccurrences((T) in.readObject(), in.readInt());
        }

        this.bagWithHashingStrategy = deserializedBag;
    }

    protected Object readResolve()
    {
        return this.bagWithHashingStrategy;
    }
}
