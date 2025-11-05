/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.block.procedure.CollectionRemoveProcedure;
import org.eclipse.collections.impl.list.mutable.ArrayListAdapter;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.eclipse.collections.impl.list.mutable.RandomAccessListAdapter;
import org.eclipse.collections.impl.set.mutable.SetAdapter;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * CollectionAdapter provides a MutableCollection wrapper around a JDK {@link Collection}.
 * This adapter allows JDK collections to be used with the Eclipse Collections API seamlessly.
 * <p>
 * The adapter delegates all operations to the wrapped collection while providing Eclipse Collections
 * methods. This enables JDK collections to benefit from Eclipse Collections' rich API without copying data.
 * </p>
 * <p><b>Factory Methods:</b></p>
 * <ul>
 * <li>{@link #adapt(Collection)} - Wraps a collection with MutableCollection interface</li>
 * <li>{@link #wrapSet(Iterable)} - Wraps an iterable as a MutableSet</li>
 * <li>{@link #wrapList(Iterable)} - Wraps an iterable as a MutableList</li>
 * </ul>
 * <p><b>Thread Safety:</b> Not thread-safe. Thread-safety depends on the wrapped collection.
 * Use {@link #asSynchronized()} to create a thread-safe wrapper.</p>
 * <p><b>Performance:</b> Delegates to the wrapped collection. Performance matches the underlying collection.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Adapt a JDK ArrayList
 * ArrayList<String> jdkList = new ArrayList<>();
 * jdkList.add("a");
 * jdkList.add("b");
 * MutableCollection<String> adapted = CollectionAdapter.adapt(jdkList);
 *
 * // Use Eclipse Collections APIs
 * MutableList<String> filtered = adapted.select(s -> s.length() > 1).toList();
 *
 * // Wrap as specific type
 * MutableSet<Integer> set = CollectionAdapter.wrapSet(Arrays.asList(1, 2, 3));
 * }</pre>
 *
 * @param <T> the type of elements in this collection
 * @since 1.0
 */
public final class CollectionAdapter<T>
        extends AbstractCollectionAdapter<T>
        implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Collection<T> delegate;

    public CollectionAdapter(Collection<T> newDelegate)
    {
        if (newDelegate == null)
        {
            throw new NullPointerException("CollectionAdapter may not wrap null");
        }
        this.delegate = newDelegate;
    }

    @Override
    protected Collection<T> getDelegate()
    {
        return this.delegate;
    }

    @Override
    public MutableCollection<T> asUnmodifiable()
    {
        return UnmodifiableMutableCollection.of(this);
    }

    @Override
    public MutableCollection<T> asSynchronized()
    {
        return SynchronizedMutableCollection.of(this);
    }

    @Override
    public ImmutableCollection<T> toImmutable()
    {
        return this.delegate instanceof Set
                ? Sets.immutable.withAll(this.delegate)
                : Lists.immutable.withAll(this.delegate);
    }

    public static <E> MutableSet<E> wrapSet(Iterable<E> iterable)
    {
        if (iterable instanceof MutableSet)
        {
            return (MutableSet<E>) iterable;
        }
        if (iterable instanceof Set)
        {
            return SetAdapter.adapt((Set<E>) iterable);
        }
        return Sets.mutable.withAll(iterable);
    }

    public static <E> MutableList<E> wrapList(Iterable<E> iterable)
    {
        if (iterable instanceof MutableList)
        {
            return (MutableList<E>) iterable;
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListAdapter.adapt((ArrayList<E>) iterable);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListAdapter.adapt((List<E>) iterable);
        }
        if (iterable instanceof List)
        {
            return ListAdapter.adapt((List<E>) iterable);
        }
        return Lists.mutable.withAll(iterable);
    }

    public static <E> MutableCollection<E> adapt(Collection<E> collection)
    {
        if (collection instanceof MutableCollection)
        {
            return (MutableCollection<E>) collection;
        }
        if (collection instanceof List)
        {
            return CollectionAdapter.wrapList(collection);
        }
        if (collection instanceof Set)
        {
            return SetAdapter.adapt((Set<E>) collection);
        }
        return new CollectionAdapter<>(collection);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        CollectionAdapter<?> that = (CollectionAdapter<?>) o;
        return Objects.equals(this.delegate, that.delegate);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    public CollectionAdapter<T> with(T... elements)
    {
        ArrayIterate.forEach(elements, new CollectionAddProcedure<>(this.delegate));
        return this;
    }

    @Override
    public CollectionAdapter<T> with(T element)
    {
        this.delegate.add(element);
        return this;
    }

    @Override
    public CollectionAdapter<T> without(T element)
    {
        this.delegate.remove(element);
        return this;
    }

    @Override
    public CollectionAdapter<T> withAll(Iterable<? extends T> elements)
    {
        Iterate.forEach(elements, new CollectionAddProcedure<>(this.delegate));
        return this;
    }

    @Override
    public CollectionAdapter<T> withoutAll(Iterable<? extends T> elements)
    {
        Iterate.forEach(elements, new CollectionRemoveProcedure<>(this.delegate));
        return this;
    }

    /**
     * @deprecated use {@link FastList#newList()} or {@link UnifiedSet#newSet()} instead
     */
    @Override
    @Deprecated
    public MutableCollection<T> newEmpty()
    {
        if (this.delegate instanceof Set)
        {
            return Sets.mutable.empty();
        }
        return Lists.mutable.empty();
    }
}
