/*
 * Copyright (c) 2021 Goldman Sachs.
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
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * A LazyIterableAdapter wraps any iterable with the LazyIterable interface.
 */
public class LazyIterableAdapter<T>
        extends AbstractLazyIterable<T>
{
    private final Iterable<T> adapted;

    public LazyIterableAdapter(Iterable<T> newAdapted)
    {
        this.adapted = newAdapted;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        Iterate.forEach(this.adapted, procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        Iterate.forEachWithIndex(this.adapted, objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        Iterate.forEachWith(this.adapted, procedure, parameter);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.adapted.iterator());
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        Iterate.addAllIterable(this.adapted, target);
        return target;
    }

    @Override
    public LazyIterable<T> select(Predicate<? super T> predicate)
    {
        return LazyIterate.select(this.adapted, predicate);
    }

    @Override
    public LazyIterable<T> reject(Predicate<? super T> predicate)
    {
        return LazyIterate.reject(this.adapted, predicate);
    }

    @Override
    public <V> LazyIterable<V> collect(Function<? super T, ? extends V> function)
    {
        return LazyIterate.collect(this.adapted, function);
    }

    @Override
    public <V> LazyIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return LazyIterate.flatCollect(this.adapted, function);
    }

    @Override
    public <V> LazyIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return LazyIterate.collectIf(this.adapted, predicate, function);
    }

    @Override
    public LazyIterable<T> take(int count)
    {
        return LazyIterate.take(this.adapted, count);
    }

    @Override
    public LazyIterable<T> drop(int count)
    {
        return LazyIterate.drop(this.adapted, count);
    }

    @Override
    public LazyIterable<T> takeWhile(Predicate<? super T> predicate)
    {
        return LazyIterate.takeWhile(this.adapted, predicate);
    }

    @Override
    public LazyIterable<T> dropWhile(Predicate<? super T> predicate)
    {
        return LazyIterate.dropWhile(this.adapted, predicate);
    }

    @Override
    public LazyIterable<T> distinct()
    {
        return LazyIterate.distinct(this.adapted);
    }

    @Override
    public int size()
    {
        return Iterate.sizeOf(this.adapted);
    }

    @Override
    public boolean isEmpty()
    {
        return Iterate.isEmpty(this.adapted);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return Iterate.anySatisfy(this.adapted, predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return Iterate.allSatisfy(this.adapted, predicate);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return Iterate.noneSatisfy(this.adapted, predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.anySatisfyWith(this.adapted, predicate, parameter);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.allSatisfyWith(this.adapted, predicate, parameter);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.noneSatisfyWith(this.adapted, predicate, parameter);
    }

    @Override
    public T getFirst()
    {
        return Iterate.getFirst(this.adapted);
    }

    @Override
    public T getLast()
    {
        return Iterate.getLast(this.adapted);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return Iterate.detect(this.adapted, predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.detectWith(this.adapted, predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return Iterate.detectOptional(this.adapted, predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.detectWithOptional(this.adapted, predicate, parameter);
    }
}
