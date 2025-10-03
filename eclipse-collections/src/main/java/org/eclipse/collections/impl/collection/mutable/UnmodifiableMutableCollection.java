/*
 * Copyright (c) 2021 Goldman Sachs.
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

import org.eclipse.collections.api.collection.MutableCollection;

/**
 * An unmodifiable view of a collection.
 *
 * @see MutableCollection#asUnmodifiable()
 */
public class UnmodifiableMutableCollection<T>
        extends AbstractUnmodifiableMutableCollection<T>
        implements Serializable
{
    protected UnmodifiableMutableCollection(MutableCollection<? extends T> collection)
    {
        super(collection);
    }

    /**
     * This method will take a MutableCollection and wrap it directly in a UnmodifiableMutableCollection. It will
     * take any other non-Eclipse-Collections collection and first adapt it will a CollectionAdapter, and then return a
     * UnmodifiableMutableCollection that wraps the adapter.
     */
    public static <E, C extends Collection<E>> UnmodifiableMutableCollection<E> of(C collection)
    {
        if (collection == null)
        {
            throw new IllegalArgumentException("cannot create a UnmodifiableMutableCollection for null");
        }
        return new UnmodifiableMutableCollection<>(CollectionAdapter.adapt(collection));
    }

    protected Object writeReplace()
    {
        return new UnmodifiableCollectionSerializationProxy<>(this.getMutableCollection());
    }
}
