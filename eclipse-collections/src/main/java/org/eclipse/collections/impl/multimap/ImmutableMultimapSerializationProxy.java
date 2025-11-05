/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.impl.block.procedure.checked.MultimapKeyValuesSerializingProcedure;

/**
 * An abstract serialization proxy for immutable multimap implementations.
 * <p>
 * This class handles the serialization and deserialization of immutable multimaps by converting
 * them to a mutable form during deserialization, reading the data, and then converting back to
 * an immutable form. This approach allows immutable multimaps to be serialized efficiently.
 * </p>
 * <p>
 * Subclasses must implement {@link #createEmptyMutableMultimap()} to specify the concrete
 * mutable multimap type to use during deserialization.
 * </p>
 *
 * @param <K> the type of keys in the multimap
 * @param <V> the type of values in the multimap
 * @param <R> the type of RichIterable containing values for each key
 * @see java.io.Externalizable
 * @since 1.0
 */
public abstract class ImmutableMultimapSerializationProxy<K, V, R extends RichIterable<V>>
{
    private Multimap<K, V> multimapToReadInto;
    private ImmutableMap<K, R> mapToWrite;

    /**
     * Default constructor for deserialization.
     */
    protected ImmutableMultimapSerializationProxy()
    {
    }

    /**
     * Constructs a serialization proxy for the given immutable map.
     *
     * @param immutableMap the immutable map to serialize
     */
    protected ImmutableMultimapSerializationProxy(ImmutableMap<K, R> immutableMap)
    {
        this.mapToWrite = immutableMap;
    }

    /**
     * Creates an empty mutable multimap for deserialization.
     * <p>
     * Subclasses must implement this method to return the appropriate mutable multimap
     * implementation that will be used to read the serialized data.
     * </p>
     *
     * @return an empty mutable multimap
     */
    protected abstract AbstractMutableMultimap<K, V, ?> createEmptyMutableMultimap();

    /**
     * Converts the deserialized mutable multimap back to an immutable multimap.
     * <p>
     * This method is called during deserialization after {@link #readExternal(ObjectInput)}
     * to replace the proxy with the actual immutable multimap instance.
     * </p>
     *
     * @return the immutable multimap
     */
    protected Object readResolve()
    {
        return this.multimapToReadInto.toImmutable();
    }

    /**
     * Writes the immutable multimap to the output stream.
     * <p>
     * Serializes the number of keys followed by each key-value pair.
     * </p>
     *
     * @param out the output stream to write to
     * @throws IOException if an I/O error occurs during serialization
     */
    public void writeExternal(ObjectOutput out) throws IOException
    {
        int keysCount = this.mapToWrite.size();
        out.writeInt(keysCount);
        this.mapToWrite.forEachKeyValue(new MultimapKeyValuesSerializingProcedure<>(out));
    }

    /**
     * Reads the multimap from the input stream.
     * <p>
     * Deserializes the data into a mutable multimap which will later be converted
     * to an immutable multimap by {@link #readResolve()}.
     * </p>
     *
     * @param in the input stream to read from
     * @throws IOException if an I/O error occurs during deserialization
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        AbstractMutableMultimap<K, V, ?> toReadInto = this.createEmptyMutableMultimap();
        toReadInto.readValuesFrom(in);
        this.multimapToReadInto = toReadInto;
    }
}
