/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;

/**
 * A synchronized view of a {@link MutableCollection}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableCollection#asSynchronized()
 */
public class SynchronizedMutableCollection<T>
        extends AbstractSynchronizedMutableCollection<T> implements Serializable
{
    private static final long serialVersionUID = 2L;

    protected SynchronizedMutableCollection(MutableCollection<T> newCollection)
    {
        this(newCollection, null);
    }

    protected SynchronizedMutableCollection(MutableCollection<T> newCollection, Object newLock)
    {
        super(newCollection, newLock);
    }

    /**
     * This method will take a MutableCollection and wrap it directly in a SynchronizedMutableCollection. It will
     * take any other non-Eclipse-Collections collection and first adapt it will a CollectionAdapter, and then return a
     * SynchronizedMutableCollection that wraps the adapter.
     */
    public static <E, C extends Collection<E>> SynchronizedMutableCollection<E> of(C collection)
    {
        return new SynchronizedMutableCollection<>(CollectionAdapter.adapt(collection));
    }

    /**
     * This method will take a MutableCollection and wrap it directly in a SynchronizedMutableCollection. It will
     * take any other non-Eclipse-Collections collection and first adapt it will a CollectionAdapter, and then return a
     * SynchronizedMutableCollection that wraps the adapter. Additionally, a developer specifies which lock to use
     * with the collection.
     */
    public static <E, C extends Collection<E>> SynchronizedMutableCollection<E> of(C collection, Object lock)
    {
        return new SynchronizedMutableCollection<>(CollectionAdapter.adapt(collection), lock);
    }

    protected Object writeReplace()
    {
        return new SynchronizedCollectionSerializationProxy<>(this.getDelegate());
    }

    @Override
    public MutableCollection<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    public MutableCollection<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public MutableCollection<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    public MutableCollection<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    public MutableCollection<T> asUnmodifiable()
    {
        synchronized (this.lock)
        {
            return new UnmodifiableMutableCollection<>(this);
        }
    }

    @Override
    public MutableCollection<T> asSynchronized()
    {
        return this;
    }

    @Override
    public ImmutableCollection<T> toImmutable()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().toImmutable();
        }
    }

    @Override
    public MutableCollection<T> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().newEmpty().asSynchronized();
        }
    }
}
