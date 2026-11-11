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
 * This class provides a MutableCollection interface wrapper around a JDK Collections Collection interface instance.
 * All the MutableCollection interface methods are supported in addition to the JDK Collection interface methods.
 * <p>
 * To create a new instance that wraps a collection with the MutableSet interface, use the {@link #wrapSet(Iterable)}
 * factory method. To create a new instance that wraps a collection with the MutableList interface, use the
 * {@link #wrapList(Iterable)} factory method. To wrap a collection with the MutableCollection interface alone, use
 * the {@link #adapt(Collection)} factory method.
 */
public class CollectionAdapter<T>
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
    @SuppressWarnings("UndefinedEquals")
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
