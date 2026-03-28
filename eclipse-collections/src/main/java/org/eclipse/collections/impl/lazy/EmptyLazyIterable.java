/*
 * Copyright (c) 2026 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.impl.EmptyIterator;

/**
 * An EmptyLazyIterable is a singleton that can be returned for any empty collection. Once an EmptyLazyIterable
 * is returned, it no longer knows about the original collection that created it, so even if the underlying collection
 * changes, the EmptyLazyIterable will always behave as empty.
 */
public final class EmptyLazyIterable<T>
        extends AbstractLazyIterable<T>
{
    private static final LazyIterable<?> INSTANCE = new EmptyLazyIterable<>();

    private EmptyLazyIterable()
    {
    }

    public static <T> LazyIterable<T> getInstance()
    {
        return (LazyIterable<T>) INSTANCE;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
    }

    @Override
    public Iterator<T> iterator()
    {
        return EmptyIterator.getInstance();
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        return target;
    }

    @Override
    public LazyIterable<T> select(Predicate<? super T> predicate)
    {
        return this;
    }

    @Override
    public LazyIterable<T> reject(Predicate<? super T> predicate)
    {
        return this;
    }

    @Override
    public <V> LazyIterable<V> collect(Function<? super T, ? extends V> function)
    {
        return EmptyLazyIterable.getInstance();
    }

    @Override
    public <V> LazyIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return EmptyLazyIterable.getInstance();
    }

    @Override
    public <V> LazyIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return EmptyLazyIterable.getInstance();
    }

    @Override
    public LazyIterable<T> take(int count)
    {
        return EmptyLazyIterable.getInstance();
    }

    @Override
    public LazyIterable<T> drop(int count)
    {
        return EmptyLazyIterable.getInstance();
    }

    @Override
    public LazyIterable<T> takeWhile(Predicate<? super T> predicate)
    {
        return EmptyLazyIterable.getInstance();
    }

    @Override
    public LazyIterable<T> dropWhile(Predicate<? super T> predicate)
    {
        return EmptyLazyIterable.getInstance();
    }

    @Override
    public LazyIterable<T> distinct()
    {
        return this;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return false;
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return true;
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return true;
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return false;
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return true;
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return true;
    }

    @Override
    public T getFirst()
    {
        return null;
    }

    @Override
    public T getLast()
    {
        return null;
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return null;
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return null;
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return Optional.empty();
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Optional.empty();
    }
}
