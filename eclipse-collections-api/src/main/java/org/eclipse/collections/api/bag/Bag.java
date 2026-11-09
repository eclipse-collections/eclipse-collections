/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag;

import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.bag.primitive.MutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.MutableFloatBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.bag.primitive.MutableShortBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.multimap.bag.BagMultimap;
import org.eclipse.collections.api.partition.bag.PartitionBag;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;

/**
 * A Bag is a Collection whose elements are unordered and may contain duplicate entries. It varies from
 * MutableCollection in that it adds a protocol for determining, adding, and removing the number of occurrences for an
 * item.
 *
 * @since 1.0
 */
public interface Bag<T>
        extends RichIterable<T>
{
    /**
     * Two bags {@code b1} and {@code b2} are equal if {@code m1.toMapOfItemToCount().equals(m2.toMapOfItemToCount())}.
     *
     * @see Map#equals(Object)
     */
    @Override
    boolean equals(Object object);

    /**
     * Returns the hash code for this Bag, defined as <em>this.{@link #toMapOfItemToCount()}.hashCode()</em>.
     *
     * @see Map#hashCode()
     */
    @Override
    int hashCode();

    @Override
    Bag<T> tap(Procedure<? super T> procedure);

    @Override
    Bag<T> select(Predicate<? super T> predicate);

    @Override
    <P> Bag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    Bag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> Bag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionBag<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> Bag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> BagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> BagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    SetIterable<Pair<T, Integer>> zipWithIndex();

    /**
     * For each distinct item, with the number of occurrences, execute the specified procedure.
     */
    void forEachWithOccurrences(ObjectIntProcedure<? super T> procedure);

    /**
     * Returns true if the predicate evaluates to true for any element of the Bag.
     * Returns false if the Bag is empty or if no element returns true for the predicate.
     *
     * @since 11.0
     */
    boolean anySatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate);

    /**
     * Returns true if the predicate evaluates to true for all elements of the Bag.
     * Returns false if the Bag is empty or if not all elements return true for the predicate.
     *
     * @since 11.0
     */
    boolean allSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate);

    /**
     * Returns true if the Bag is empty or if the predicate evaluates to false for all elements of the Bag.
     * Returns false if the predicate evaluates to true for at least one element of the Bag.
     *
     * @since 11.0
     */
    boolean noneSatisfyWithOccurrences(ObjectIntPredicate<? super T> predicate);

    /**
     * Returns an element of the Bag that satisfies the predicate or null if such an element does not exist
     *
     * @since 11.0
     */
    T detectWithOccurrences(ObjectIntPredicate<? super T> predicate);

    /**
     * The occurrences of a distinct item in the bag.
     */
    int occurrencesOf(Object item);

    /**
     * Returns all elements of the bag that have a number of occurrences that satisfy the predicate.
     *
     * @since 3.0
     */
    Bag<T> selectByOccurrences(IntPredicate predicate);

    /**
     * Returns all elements of the bag that have more than one occurrence.
     *
     * @since 9.2
     */
    default Bag<T> selectDuplicates()
    {
        return this.selectByOccurrences(occurrences -> occurrences > 1);
    }

    /**
     * Returns a set containing all elements of the bag that have exactly one occurrence.
     *
     * @since 9.2
     */
    SetIterable<T> selectUnique();

    /**
     * Returns the {@code count} most frequently occurring items.
     *
     * In the event of a tie, all the items with the number of occurrences that match the occurrences of the last
     * item will be returned.
     *
     * @since 6.0
     */
    ListIterable<ObjectIntPair<T>> topOccurrences(int count);

    /**
     * Returns the {@code count} least frequently occurring items.
     *
     * In the event of a tie, all of the items with the number of occurrences that match the occurrences of the last
     * item will be returned.
     *
     * @since 6.0
     */
    ListIterable<ObjectIntPair<T>> bottomOccurrences(int count);

    /**
     * The size of the Bag when counting only distinct elements.
     */
    int sizeDistinct();

    /**
     * Converts the Bag to a Map of the Item type to its count as an Integer.
     */
    MapIterable<T, Integer> toMapOfItemToCount();

    /**
     * Returns a string representation of this bag. The string representation consists of a list of element-count mappings.
     *
     * <pre>
     * Assert.assertEquals("{1=1, 2=2, 3=3}", Bags.mutable.with(1, 2, 2, 3, 3, 3).toStringOfItemToCount());
     * </pre>
     * This string representation is similar to {@link java.util.AbstractMap#toString()}, not {@link RichIterable#toString()},
     * whereas the {@code toString()} implementation for a Bag is consistent with {@link RichIterable#toString()}.
     *
     * @return a string representation of this bag
     * @since 3.0
     */
    String toStringOfItemToCount();

    ImmutableBagIterable<T> toImmutable();

    /**
     * Returns an unmodifiable view on the distinct elements with the same complexity as the Bag implementation.
     *
     * @return an unmodifiable view on the distinct elements of the Bag.
     * @since 11.1
     */
    default RichIterable<T> distinctView()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".distinctView() not implemented yet");
    }

    /**
     * @since 8.0
     */
    @Override
    default IntSummaryStatistics summarizeInt(IntFunction<? super T> function)
    {
        IntSummaryStatistics stats = new IntSummaryStatistics();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            int result = function.intValueOf(each);
            for (int i = 0; i < occurrences; i++)
            {
                stats.accept(result);
            }
        });
        return stats;
    }

    /**
     * @since 8.0
     */
    @Override
    default DoubleSummaryStatistics summarizeFloat(FloatFunction<? super T> function)
    {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            float result = function.floatValueOf(each);
            for (int i = 0; i < occurrences; i++)
            {
                stats.accept(result);
            }
        });
        return stats;
    }

    /**
     * @since 8.0
     */
    @Override
    default LongSummaryStatistics summarizeLong(LongFunction<? super T> function)
    {
        LongSummaryStatistics stats = new LongSummaryStatistics();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            long result = function.longValueOf(each);
            for (int i = 0; i < occurrences; i++)
            {
                stats.accept(result);
            }
        });
        return stats;
    }

    /**
     * @since 8.0
     */
    @Override
    default DoubleSummaryStatistics summarizeDouble(DoubleFunction<? super T> function)
    {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            double result = function.doubleValueOf(each);
            for (int i = 0; i < occurrences; i++)
            {
                stats.accept(result);
            }
        });
        return stats;
    }

    /**
     * This method produces the equivalent result as {@link Stream#collect(Collector)}.
     *
     * @since 8.0
     */
    @Override
    default <R, A> R reduceInPlace(Collector<? super T, A, R> collector)
    {
        A mutableResult = collector.supplier().get();
        BiConsumer<A, ? super T> accumulator = collector.accumulator();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            for (int i = 0; i < occurrences; i++)
            {
                accumulator.accept(mutableResult, each);
            }
        });
        return collector.finisher().apply(mutableResult);
    }

    /**
     * This method produces the equivalent result as {@link Stream#collect(Supplier, BiConsumer, BiConsumer)}.
     *
     * @since 8.0
     */
    @Override
    default <R> R reduceInPlace(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator)
    {
        R mutableResult = supplier.get();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            for (int i = 0; i < occurrences; i++)
            {
                accumulator.accept(mutableResult, each);
            }
        });
        return mutableResult;
    }

    /**
     * Iterates over the unique elements and their occurrences and collects the results of applying the specified function.
     *
     * @since 10.0
     */
    <V> RichIterable<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function);

    /**
     * Iterates over the unique elements and their occurrences and collects the results of applying the
     * specified function into the target collection.
     *
     * @since 9.1.
     */
    default <V, R extends Collection<V>> R collectWithOccurrences(
            ObjectIntToObjectFunction<? super T, ? extends V> function,
            R target)
    {
        this.forEachWithOccurrences((each, occurrences) -> target.add(function.valueOf(each, occurrences)));
        return target;
    }

    /**
     * Applies the function once for each distinct element and adds the result with the same number of occurrences
     * to the target. Unlike {@link #collect(Function, Collection)}, the function is not applied once per occurrence.
     * Prefer this method when the function is pure (stable and repeatable for the same input) and especially when the
     * target is a {@link MutableBagIterable}.
     *
     * @since 14.0
     */
    default <V> Bag<V> collectEachOccurrences(Function<? super T, ? extends V> function)
    {
        return this.collectEachOccurrences(function, Bags.mutable.empty());
    }

    /**
     * Same as {@link #collectEachOccurrences(Function)} but adds results into the target collection.
     *
     * @since 14.0
     */
    default <V, R extends Collection<V>> R collectEachOccurrences(
            Function<? super T, ? extends V> function,
            R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<V> targetBag = (MutableBagIterable<V>) target;
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(function.valueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                V value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * Same as {@link #collectEachOccurrences(Function, Collection)} using a two-argument function and parameter.
     *
     * @since 14.0
     */
    default <P, V, R extends Collection<V>> R collectWithEachOccurrences(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<V> targetBag = (MutableBagIterable<V>) target;
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(function.value(each, parameter), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                V value = function.value(each, parameter);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * Applies the function once per distinct element and multiplies occurrences into the target.
     *
     * @since 14.0
     */
    default <R extends MutableBooleanCollection> R collectEachOccurrencesBoolean(
            BooleanFunction<? super T> booleanFunction,
            R target)
    {
        if (target instanceof MutableBooleanBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(booleanFunction.booleanValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                boolean value = booleanFunction.booleanValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * @since 14.0
     */
    default <R extends MutableByteCollection> R collectEachOccurrencesByte(
            ByteFunction<? super T> byteFunction,
            R target)
    {
        if (target instanceof MutableByteBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(byteFunction.byteValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                byte value = byteFunction.byteValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * @since 14.0
     */
    default <R extends MutableCharCollection> R collectEachOccurrencesChar(
            CharFunction<? super T> charFunction,
            R target)
    {
        if (target instanceof MutableCharBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(charFunction.charValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                char value = charFunction.charValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * @since 14.0
     */
    default <R extends MutableDoubleCollection> R collectEachOccurrencesDouble(
            DoubleFunction<? super T> doubleFunction,
            R target)
    {
        if (target instanceof MutableDoubleBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(doubleFunction.doubleValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                double value = doubleFunction.doubleValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * @since 14.0
     */
    default <R extends MutableFloatCollection> R collectEachOccurrencesFloat(
            FloatFunction<? super T> floatFunction,
            R target)
    {
        if (target instanceof MutableFloatBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(floatFunction.floatValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                float value = floatFunction.floatValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * @since 14.0
     */
    default <R extends MutableIntCollection> R collectEachOccurrencesInt(
            IntFunction<? super T> intFunction,
            R target)
    {
        if (target instanceof MutableIntBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(intFunction.intValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                int value = intFunction.intValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * @since 14.0
     */
    default <R extends MutableLongCollection> R collectEachOccurrencesLong(
            LongFunction<? super T> longFunction,
            R target)
    {
        if (target instanceof MutableLongBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(longFunction.longValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                long value = longFunction.longValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * @since 14.0
     */
    default <R extends MutableShortCollection> R collectEachOccurrencesShort(
            ShortFunction<? super T> shortFunction,
            R target)
    {
        if (target instanceof MutableShortBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) ->
                    targetBag.addOccurrences(shortFunction.shortValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) ->
            {
                short value = shortFunction.shortValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    /**
     * {@inheritDoc}
     * <p>
     * On a {@code Bag}, the function is applied once per distinct element and the bag occurrence count is preserved
     * (see {@link #collectEachOccurrences(Function, Collection)}). Use {@link #collect(Function, Collection)} when the
     * function is stateful and must run once per occurrence.
     *
     * @since 14.0
     */
    @Override
    default <V, R extends MutableBagIterable<V>> R countBy(Function<? super T, ? extends V> function, R target)
    {
        return this.collectEachOccurrences(function, target);
    }

    /**
     * {@inheritDoc}
     * <p>
     * On a {@code Bag}, the function is applied once per distinct element and the bag occurrence count is preserved.
     *
     * @since 14.0
     */
    @Override
    default <V, P, R extends MutableBagIterable<V>> R countByWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R target)
    {
        return this.collectWithEachOccurrences(function, parameter, target);
    }

    /**
     * Applies an aggregate function over the iterable grouping results into the target map based on the specific
     * groupBy function. Aggregate results are allowed to be immutable as they will be replaced in place in the map. A
     * second function specifies the initial "zero" aggregate value to work with (i.e. Integer.valueOf(0)).
     *
     * This method is overridden and optimized for Bag to use forEachWithOccurrences instead of forEach.
     *
     * @since 10.3
     */
    @Override
    default <K, V, R extends MutableMapIterable<K, V>> R aggregateBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            R target)
    {
        this.forEachWithOccurrences((each, occurrences) ->
        {
            K key = groupBy.valueOf(each);
            for (int i = 0; i < occurrences; i++)
            {
                target.updateValueWith(key, zeroValueFactory, nonMutatingAggregator, each);
            }
        });
        return target;
    }

    @Override
    default <K, R extends MutableMapIterable<K, T>> R reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction,
            R target)
    {
        this.forEachWithOccurrences((each, occurrences) ->
        {
            K key = groupBy.valueOf(each);
            for (int i = 0; i < occurrences; i++)
            {
                target.merge(key, each, reduceFunction);
            }
        });
        return target;
    }
}
