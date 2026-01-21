/*
 * Copyright (c) 2021 Shotaro Sano.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.multimap.MutableMultimap;

public class SynchronizedMultimapSerializationProxy<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private MutableMultimap<K, V> multimap;

    @SuppressWarnings("UnusedDeclaration")
    public SynchronizedMultimapSerializationProxy()
    {
        // Empty constructor for Externalizable class
    }

    public SynchronizedMultimapSerializationProxy(MutableMultimap<K, V> multimap)
    {
        this.multimap = multimap;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.multimap);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.multimap = (MutableMultimap<K, V>) in.readObject();
    }

    protected Object readResolve()
    {
        return this.multimap.asSynchronized();
    }
}
