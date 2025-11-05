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
 * UnmodifiableMutableCollection is a concrete implementation that provides an unmodifiable
 * view of a collection. It prevents any modifications to the wrapped collection.
 * <p>
 * This class extends AbstractUnmodifiableMutableCollection and adds serialization support.
 * Use the {@link #of(Collection)} factory method to create instances, which will adapt
 * JDK collections or wrap Eclipse Collections appropriately.
 * </p>
 * <p><b>Thread Safety:</b> Not inherently thread-safe. Thread-safety depends on the wrapped collection.</p>
 * <p><b>Serialization:</b> Serializable. Uses a custom serialization proxy.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * List<String> jdkList = new ArrayList<>(Arrays.asList("a", "b", "c"));
 * MutableCollection<String> unmodifiable = UnmodifiableMutableCollection.of(jdkList);
 *
 * // Read operations work normally
 * unmodifiable.forEach(System.out::println);
 * int size = unmodifiable.size();
 *
 * // Modification attempts throw UnsupportedOperationException
 * // unmodifiable.add("d"); // Throws UnsupportedOperationException
 * }</pre>
 *
 * @param <T> the type of elements in this collection
 * @see MutableCollection#asUnmodifiable()
 * @since 1.0
 */
public class UnmodifiableMutableCollection<T>
        extends AbstractUnmodifiableMutableCollection<T>
        implements Serializable
{
    UnmodifiableMutableCollection(MutableCollection<? extends T> collection)
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
