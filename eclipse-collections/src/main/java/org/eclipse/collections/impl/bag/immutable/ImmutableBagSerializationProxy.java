/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.immutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.UnsortedBag;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.procedure.checked.CheckedObjectIntProcedure;

class ImmutableBagSerializationProxy<T> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private UnsortedBag<T> bag;

    @SuppressWarnings("UnusedDeclaration")
    public ImmutableBagSerializationProxy()
    {
        // Empty constructor for Externalizable class
    }

    ImmutableBagSerializationProxy(UnsortedBag<T> bag)
    {
        this.bag = bag;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.bag.sizeDistinct());
        try
        {
            this.bag.forEachWithOccurrences(new CheckedObjectIntProcedure<T>()
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
        int size = in.readInt();
        MutableBag<T> deserializedBag = new HashBag<>(size);

        for (int i = 0; i < size; i++)
        {
            deserializedBag.addOccurrences((T) in.readObject(), in.readInt());
        }

        this.bag = deserializedBag;
    }

    protected Object readResolve()
    {
        return this.bag.toImmutable();
    }
}
