/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.immutable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.ImmutableBagIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.bag.AbstractBag;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;

public abstract class AbstractImmutableBagIterable<T>
        extends AbstractBag<T>
        implements ImmutableBagIterable<T>
{
    @Override
    public <V> ImmutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        this.forEachWithOccurrences((each, occurrences) -> result.addToValue(
                groupBy.valueOf(each),
                function.intValueOf(each) * (long) occurrences));
        return result.toImmutable();
    }

    @Override
    public <V> ImmutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function)).toImmutable();
    }

    @Override
    public <V> ImmutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        this.forEachWithOccurrences((each, occurrences) -> result.addToValue(
                groupBy.valueOf(each),
                function.longValueOf(each) * (long) occurrences));
        return result.toImmutable();
    }

    @Override
    public <V> ImmutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function)).toImmutable();
    }

    protected void removeAllFrom(Iterable<? extends T> elements, MutableCollection<T> result)
    {
        if (elements instanceof Set<?> set)
        {
            result.removeAll(set);
        }
        else if (elements instanceof List)
        {
            List<T> toBeRemoved = (List<T>) elements;
            if (this.size() * toBeRemoved.size() > 10000)
            {
                result.removeAll(Sets.mutable.withAll(elements));
            }
            else
            {
                result.removeAll(toBeRemoved);
            }
        }
        else
        {
            result.removeAll(Sets.mutable.withAll(elements));
        }
    }

    @Override
    public boolean add(T t)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot call removeAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot call retainAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> ImmutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.countBy(function, Bags.mutable.<V>empty()).toImmutable();
    }

    /**
     * @since 9.0
     */
    @Override
    public <V, P> ImmutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.countByWith(function, parameter, Bags.mutable.<V>empty()).toImmutable();
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V> ImmutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.countByEach(function, Bags.mutable.empty()).toImmutable();
    }

    /**
     * @since 9.0
     */
    @Override
    public Stream<T> stream()
    {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * @since 9.0
     */
    @Override
    public Stream<T> parallelStream()
    {
        return StreamSupport.stream(this.spliterator(), true);
    }

    /**
     * @since 9.0
     */
    @Override
    public Spliterator<T> spliterator()
    {
        return Spliterators.spliterator(this, 0);
    }

    /**
     * @since 9.0
     */
    @Override
    public Collection<T> castToCollection()
    {
        return this;
    }

    protected boolean shortCircuit(
            T[] elements,
            int[] occurrences,
            ObjectIntPredicate<? super T> predicate,
            boolean expected,
            boolean onShortCircuit,
            boolean atEnd)
    {
        for (int i = 0; i < elements.length; i++)
        {
            T each = elements[i];
            if (predicate.accept(each, occurrences[i]) == expected)
            {
                return onShortCircuit;
            }
        }
        return atEnd;
    }
}
