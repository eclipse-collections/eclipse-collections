/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.immutable.primitive;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.factory.primitive.BooleanSets;
import org.eclipse.collections.api.set.primitive.BooleanSet;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.impl.block.procedure.checked.primitive.CheckedBooleanProcedure;

/**
 * @since 4.0.
 */
public final class ImmutableBooleanSetSerializationProxy implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private BooleanSet set;

    @SuppressWarnings("UnusedDeclaration")
    public ImmutableBooleanSetSerializationProxy()
    {
        // Empty constructor for Externalizable class
    }

    public ImmutableBooleanSetSerializationProxy(BooleanSet set)
    {
        this.set = set;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.set.size());
        try
        {
            this.set.forEach(new CheckedBooleanProcedure()
            {
                @Override
                public void safeValue(boolean item) throws Exception
                {
                    out.writeBoolean(item);
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
    public void readExternal(ObjectInput in) throws IOException
    {
        int size = in.readInt();
        MutableBooleanSet deserializedSet = BooleanSets.mutable.empty();

        for (int i = 0; i < size; i++)
        {
            deserializedSet.add(in.readBoolean());
        }

        this.set = deserializedSet;
    }

    private Object readResolve()
    {
        return this.set.toImmutable();
    }
}
