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

/**
 * A serialization proxy for synchronized multimap wrappers.
 * <p>
 * This class handles the serialization and deserialization of synchronized multimaps by
 * serializing the underlying mutable multimap and then re-wrapping it as a synchronized
 * multimap during deserialization. This ensures that the synchronized wrapper is properly
 * reconstructed after deserialization.
 * </p>
 * <p>
 * This implementation follows the Externalizable pattern for efficient serialization.
 * </p>
 *
 * @param <K> the type of keys in the multimap
 * @param <V> the type of values in the multimap
 * @see Externalizable
 * @see AbstractSynchronizedMultimap
 * @since 7.0
 */
public class SynchronizedMultimapSerializationProxy<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private MutableMultimap<K, V> multimap;

    /**
     * Default constructor for Externalizable deserialization.
     * <p>
     * This constructor is required by the Externalizable interface and should not be
     * called directly.
     * </p>
     */
    @SuppressWarnings("UnusedDeclaration")
    public SynchronizedMultimapSerializationProxy()
    {
        // Empty constructor for Externalizable class
    }

    /**
     * Constructs a serialization proxy for the given mutable multimap.
     *
     * @param multimap the mutable multimap to serialize
     */
    public SynchronizedMultimapSerializationProxy(MutableMultimap<K, V> multimap)
    {
        this.multimap = multimap;
    }

    /**
     * Writes the underlying mutable multimap to the output stream.
     *
     * @param out the output stream to write to
     * @throws IOException if an I/O error occurs during serialization
     */
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.multimap);
    }

    /**
     * Reads the underlying mutable multimap from the input stream.
     *
     * @param in the input stream to read from
     * @throws IOException if an I/O error occurs during deserialization
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.multimap = (MutableMultimap<K, V>) in.readObject();
    }

    /**
     * Replaces the proxy with a synchronized view of the deserialized multimap.
     * <p>
     * This method is called during deserialization to convert the underlying mutable
     * multimap back into a synchronized multimap wrapper.
     * </p>
     *
     * @return a synchronized view of the deserialized multimap
     */
    protected Object readResolve()
    {
        return this.multimap.asSynchronized();
    }
}
