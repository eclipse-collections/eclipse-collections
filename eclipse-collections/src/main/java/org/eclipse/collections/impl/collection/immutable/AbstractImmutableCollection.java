/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.immutable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.AbstractRichIterable;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * AbstractImmutableCollection is an abstract base class for immutable collection implementations.
 * This class provides default implementations for ImmutableCollection operations and ensures
 * that all mutating operations throw {@link UnsupportedOperationException}.
 * <p>
 * Immutable collections cannot be modified after creation. All modification attempts will throw
 * UnsupportedOperationException. Operations that would normally modify the collection (like
 * add, remove, clear) are not supported.
 * </p>
 * <p><b>Thread Safety:</b> Immutable collections are inherently thread-safe as they cannot be modified.</p>
 * <p><b>Performance:</b> Immutable collections provide better performance in concurrent scenarios
 * as no synchronization is needed. They also enable safe sharing and caching.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create an immutable collection from existing data
 * ImmutableCollection<String> names = Lists.immutable.with("Alice", "Bob", "Charlie");
 *
 * // All read operations work normally
 * int size = names.size();
 * boolean contains = names.contains("Alice");
 *
 * // Modification attempts throw UnsupportedOperationException
 * // names.add("David"); // This would throw UnsupportedOperationException
 * }</pre>
 *
 * @param <T> the type of elements in this collection
 * @since 1.0
 */
public abstract class AbstractImmutableCollection<T>
        extends AbstractRichIterable<T>
        implements ImmutableCollection<T>, Collection<T>
{
    protected abstract MutableCollection<T> newMutable(int size);

    @Override
    public Object[] toArray()
    {
        return super.toArray();
    }

    @Override
    public <E> E[] toArray(E[] array)
    {
        return super.toArray(array);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.injectInto(null, (result, each) ->
                result == null ? each : accumulator.apply(result, each)));
    }

    @Override
    public <V> ImmutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function)).toImmutable();
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
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function)).toImmutable();
    }

    @Override
    public <V> ImmutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function)).toImmutable();
    }

    @Override
    public <V> ImmutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        MutableMap<V, T> map = Iterate.groupByUniqueKey(this, function);
        return map.toImmutable();
    }

    /**
     * This operation is not supported on immutable collections.
     *
     * @param t element to be added (not supported)
     * @return never returns normally
     * @throws UnsupportedOperationException always thrown as immutable collections cannot be modified
     */
    @Override
    public boolean add(T t)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on immutable collections.
     *
     * @param o element to be removed (not supported)
     * @return never returns normally
     * @throws UnsupportedOperationException always thrown as immutable collections cannot be modified
     */
    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on immutable collections.
     *
     * @param collection elements to be added (not supported)
     * @return never returns normally
     * @throws UnsupportedOperationException always thrown as immutable collections cannot be modified
     */
    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on immutable collections.
     *
     * @param collection elements to be removed (not supported)
     * @return never returns normally
     * @throws UnsupportedOperationException always thrown as immutable collections cannot be modified
     */
    @Override
    public boolean removeAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot call removeAll() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on immutable collections.
     *
     * @param collection elements to be retained (not supported)
     * @return never returns normally
     * @throws UnsupportedOperationException always thrown as immutable collections cannot be modified
     */
    @Override
    public boolean retainAll(Collection<?> collection)
    {
        throw new UnsupportedOperationException("Cannot call retainAll() on " + this.getClass().getSimpleName());
    }

    /**
     * This operation is not supported on immutable collections.
     *
     * @throws UnsupportedOperationException always thrown as immutable collections cannot be modified
     */
    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    protected void removeAllFrom(Iterable<? extends T> elements, MutableCollection<T> result)
    {
        if (elements instanceof Set)
        {
            result.removeAll((Set<?>) elements);
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
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }

        Iterator<T> iterator = this.iterator();
        MutableList<RichIterable<T>> result = Lists.mutable.empty();
        while (iterator.hasNext())
        {
            MutableCollection<T> batch = this.newMutable(size);
            for (int i = 0; i < size && iterator.hasNext(); i++)
            {
                batch.add(iterator.next());
            }
            result.add(batch.toImmutable());
        }
        return result.toImmutable();
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
}
