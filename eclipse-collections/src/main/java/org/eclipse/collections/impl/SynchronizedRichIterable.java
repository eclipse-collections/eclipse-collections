/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.impl.collection.AbstractSynchronizedRichIterable;

/**
 * A synchronized view of a RichIterable that provides thread-safe access to an underlying iterable.
 * <p>
 * All methods that access the underlying iterable are synchronized, making this wrapper safe for
 * concurrent access from multiple threads. This class extends {@link AbstractSynchronizedRichIterable}
 * and provides additional factory methods and serialization support.
 * </p>
 * <p>
 * Thread Safety: All operations on this iterable are synchronized using either an internal lock
 * or a user-provided lock object. However, iterators returned by this class are NOT automatically
 * synchronized and require external synchronization if accessed concurrently.
 * </p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * RichIterable<String> unsafeIterable = ...;
 * RichIterable<String> safeIterable = SynchronizedRichIterable.of(unsafeIterable);
 * // Now safe for concurrent access
 * }</pre>
 *
 * @param <T> the type of elements in this iterable
 * @since 5.0
 */
public class SynchronizedRichIterable<T>
        extends AbstractSynchronizedRichIterable<T>
        implements Serializable
{
    private static final long serialVersionUID = 2L;

    /**
     * Constructs a synchronized view of the specified iterable using an internal lock.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<String> iterable = ...;
     * SynchronizedRichIterable<String> synchronized = new SynchronizedRichIterable<>(iterable);
     * }</pre>
     *
     * @param iterable the iterable to wrap with synchronization
     */
    protected SynchronizedRichIterable(RichIterable<T> iterable)
    {
        this(iterable, null);
    }

    /**
     * Constructs a synchronized view of the specified iterable using the provided lock object.
     * <p>
     * Using a custom lock allows coordination with other synchronized blocks in your code.
     * If newLock is null, an internal lock is used instead.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Object sharedLock = new Object();
     * RichIterable<String> iterable = ...;
     * SynchronizedRichIterable<String> synced =
     *     new SynchronizedRichIterable<>(iterable, sharedLock);
     * }</pre>
     *
     * @param iterable the iterable to wrap with synchronization
     * @param newLock the lock object to use for synchronization, or null for an internal lock
     */
    protected SynchronizedRichIterable(RichIterable<T> iterable, Object newLock)
    {
        super(iterable, newLock);
    }

    /**
     * Creates a synchronized view of the specified iterable.
     * <p>
     * This factory method wraps the given iterable in a SynchronizedRichIterable,
     * providing thread-safe access using an internal lock.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * RichIterable<Integer> numbers = Lists.mutable.with(1, 2, 3);
     * SynchronizedRichIterable<Integer> synced = SynchronizedRichIterable.of(numbers);
     * }</pre>
     *
     * @param <E> the type of elements in the iterable
     * @param iterable the iterable to wrap with synchronization
     * @return a synchronized view of the iterable
     */
    public static <E> SynchronizedRichIterable<E> of(RichIterable<E> iterable)
    {
        return new SynchronizedRichIterable<>(iterable);
    }

    /**
     * Returns a serialization proxy for this synchronized iterable.
     * <p>
     * This method is called during serialization to replace the synchronized wrapper
     * with a proxy that only serializes the underlying iterable, not the lock.
     * </p>
     *
     * @return a serialization proxy
     */
    protected Object writeReplace()
    {
        return new SynchronizedRichIterableSerializationProxy<>(this.getDelegate());
    }

    /**
     * Creates a synchronized view of the specified iterable using the provided lock.
     * <p>
     * This factory method allows you to specify a custom lock object for synchronization,
     * enabling coordination with other synchronized code that uses the same lock.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Object sharedLock = new Object();
     * RichIterable<String> iterable = ...;
     * SynchronizedRichIterable<String> synced =
     *     SynchronizedRichIterable.of(iterable, sharedLock);
     * }</pre>
     *
     * @param <E> the type of elements in the iterable
     * @param iterable the iterable to wrap with synchronization
     * @param lock the lock object to use for synchronization
     * @return a synchronized view of the iterable using the specified lock
     */
    public static <E> SynchronizedRichIterable<E> of(RichIterable<E> iterable, Object lock)
    {
        return new SynchronizedRichIterable<>(iterable, lock);
    }

    /**
     * A serialization proxy for SynchronizedRichIterable.
     * <p>
     * This proxy ensures that only the underlying iterable is serialized, not the synchronization
     * lock, which cannot be meaningfully serialized.
     * </p>
     *
     * @param <T> the type of elements in the iterable
     */
    public static class SynchronizedRichIterableSerializationProxy<T> implements Externalizable
    {
        private static final long serialVersionUID = 1L;

        private RichIterable<T> richIterable;

        /**
         * Default constructor for Externalizable.
         * <p>
         * This constructor is required for deserialization but should not be called directly.
         * </p>
         */
        public SynchronizedRichIterableSerializationProxy()
        {
            // Empty constructor for Externalizable class
        }

        /**
         * Constructs a serialization proxy for the specified iterable.
         *
         * @param iterable the iterable to serialize
         */
        public SynchronizedRichIterableSerializationProxy(RichIterable<T> iterable)
        {
            this.richIterable = iterable;
        }

        /**
         * Writes the underlying iterable to the output stream.
         *
         * @param out the stream to write to
         * @throws IOException if an I/O error occurs
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException
        {
            out.writeObject(this.richIterable);
        }

        /**
         * Reads the underlying iterable from the input stream.
         *
         * @param in the stream to read from
         * @throws IOException if an I/O error occurs
         * @throws ClassNotFoundException if the class cannot be found
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
        {
            this.richIterable = (RichIterable<T>) in.readObject();
        }

        /**
         * Returns a new SynchronizedRichIterable wrapping the deserialized iterable.
         * <p>
         * This method is called after deserialization to replace the proxy with the actual
         * synchronized wrapper.
         * </p>
         *
         * @return a new SynchronizedRichIterable
         */
        protected Object readResolve()
        {
            return new SynchronizedRichIterable<>(this.richIterable);
        }
    }
}
