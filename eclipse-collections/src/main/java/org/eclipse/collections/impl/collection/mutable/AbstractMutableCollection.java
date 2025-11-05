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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.function.BinaryOperator;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.Function3;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.AbstractRichIterable;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.internal.IterableIterate;

/**
 * AbstractMutableCollection is an abstract base class for mutable collection implementations
 * in Eclipse Collections. It provides default implementations of common MutableCollection methods.
 * <p>
 * This class extends AbstractRichIterable and implements MutableCollection, providing reusable
 * implementations for operations like removeIf, addAll, removeAll, chunk, and various aggregation
 * methods. Subclasses can inherit these implementations to reduce code duplication.
 * </p>
 * <p><b>Thread Safety:</b> Not thread-safe. Subclasses are responsible for their own synchronization if needed.</p>
 * <p><b>Performance:</b> Uses optimized iteration patterns from Eclipse Collections utilities.
 * Operations delegate to efficient internal implementations when possible.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Typically extended by concrete collection classes
 * public class MyCollection<T> extends AbstractMutableCollection<T> {
 *     // Implement required abstract methods
 *     // Inherit default implementations for many operations
 * }
 *
 * MutableCollection<Integer> numbers = new MyCollection<>();
 * numbers.addAll(Lists.mutable.with(1, 2, 3, 4, 5));
 * numbers.removeIf(n -> n % 2 == 0); // Uses inherited implementation
 * }</pre>
 *
 * @param <T> the type of elements in this collection
 * @since 1.0
 */
public abstract class AbstractMutableCollection<T>
        extends AbstractRichIterable<T>
        implements MutableCollection<T>
{
    @Override
    public <P> Twin<MutableList<T>> selectAndRejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return IterableIterate.selectAndRejectWith(this, predicate, parameter);
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        return IterableIterate.removeIf(this, predicate);
    }

    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IterableIterate.removeIfWith(this, predicate, parameter);
    }

    @Override
    public <IV, P> IV injectIntoWith(
            IV injectValue,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        return IterableIterate.injectIntoWith(injectValue, this, function, parameter);
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
    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        int oldSize = this.size();

        if (iterable instanceof List && iterable instanceof RandomAccess)
        {
            List<T> list = (List<T>) iterable;
            int size = list.size();
            for (int i = 0; i < size; i++)
            {
                this.add(list.get(i));
            }
        }
        else
        {
            Iterate.forEachWith(iterable, Procedures2.addToCollection(), this);
        }
        return oldSize != this.size();
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        return this.removeAll(CollectionAdapter.wrapSet(iterable));
    }

    @Override
    public boolean retainAllIterable(Iterable<?> iterable)
    {
        return this.retainAll(CollectionAdapter.wrapSet(iterable));
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
            MutableCollection<T> batch = this.newEmpty();
            for (int i = 0; i < size && iterator.hasNext(); i++)
            {
                batch.add(iterator.next());
            }
            result.add(batch);
        }
        return result;
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function));
    }

    @Override
    public boolean add(T element)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean remove(Object o)
    {
        Iterator<T> iterator = this.iterator();
        while (iterator.hasNext())
        {
            if (Objects.equals(o, iterator.next()))
            {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> source)
    {
        return this.addAllIterable(source);
    }

    @Override
    public boolean removeAll(Collection<?> source)
    {
        return this.removeAllIterable(source);
    }

    @Override
    public boolean retainAll(Collection<?> source)
    {
        int oldSize = this.size();
        Iterator<?> iterator = this.iterator();
        while (iterator.hasNext())
        {
            if (!source.contains(iterator.next()))
            {
                iterator.remove();
            }
        }
        return this.size() != oldSize;
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return Iterate.groupByUniqueKey(this, function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> MutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.countBy(function, Bags.mutable.empty());
    }

    /**
     * @since 9.0
     */
    @Override
    public <V, P> MutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.countByWith(function, parameter, Bags.mutable.empty());
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V> MutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.countByEach(function, Bags.mutable.empty());
    }
}
