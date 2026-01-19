/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.factory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.factory.set.FixedSizeSetFactory;
import org.eclipse.collections.api.factory.set.ImmutableSetFactory;
import org.eclipse.collections.api.factory.set.MultiReaderSetFactory;
import org.eclipse.collections.api.factory.set.MutableSetFactory;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.set.fixed.FixedSizeSetFactoryImpl;
import org.eclipse.collections.impl.set.immutable.ImmutableSetFactoryImpl;
import org.eclipse.collections.impl.set.mutable.MultiReaderMutableSetFactory;
import org.eclipse.collections.impl.set.mutable.MutableSetFactoryImpl;
import org.eclipse.collections.impl.set.mutable.SetAdapter;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * Set algebra operations are available in this class as static utility.
 */
@SuppressWarnings("ConstantNamingConvention")
public final class Sets
{
    public static final ImmutableSetFactory immutable = ImmutableSetFactoryImpl.INSTANCE;
    public static final FixedSizeSetFactory fixedSize = FixedSizeSetFactoryImpl.INSTANCE;
    public static final MutableSetFactory mutable = MutableSetFactoryImpl.INSTANCE;
    public static final MultiReaderSetFactory multiReader = MultiReaderMutableSetFactory.INSTANCE;

    private static final Predicate<Set<?>> INSTANCE_OF_SORTED_SET_PREDICATE = SortedSet.class::isInstance;
    private static final Predicate<Set<?>> HAS_NON_NULL_COMPARATOR = set -> ((SortedSet<?>) set).comparator() != null;

    private Sets()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Adapts a standard {@link java.util.Set} into an Eclipse Collections {@link MutableSet}.
     * <p>
     * The returned set is backed by the original set, so changes to one are reflected in the other.
     *
     * @param set the {@link Set} to adapt
     * @param <T> the element type
     * @return a {@link MutableSet} backed by the given set
     * @since 9.0
     */
    public static <T> MutableSet<T> adapt(Set<T> set)
    {
        return SetAdapter.adapt(set);
    }

    /**
     * Returns a new mutable set containing the union of two sets.
     *
     * @param setA the first input set
     * @param setB the second input set
     * @param <E> the element type
     * @return a new {@link MutableSet} containing all distinct elements from both sets
     * @see #unionInto(Set, Set, Set)
     */
    public static <E> MutableSet<E> union(
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return unionInto(newSet(setA, setB), setA, setB);
    }

    /**
     * Adds the union of two sets into the given target set.
     *
     * @param targetSet the set into which results are placed
     * @param setA the first input set
     * @param setB the second input set
     * @param <E> the element type
     * @param <R> the target set type
     * @return {@code targetSet}, after modification
     * @see #union(Set, Set)
     */
    public static <E, R extends Set<E>> R unionInto(
            R targetSet,
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return setA.size() > setB.size()
                ? fillSet(targetSet, Sets.addAllProcedure(), setA, setB)
                : fillSet(targetSet, Sets.addAllProcedure(), setB, setA);
    }

    /**
     * Returns a new mutable set containing the union of all provided sets.
     *
     * @param sets the input sets
     * @param <E> the element type
     * @return a new {@link MutableSet} containing all distinct elements
     * @see #unionAllInto(Set, Set...)
     */
    @SafeVarargs
    public static <E> MutableSet<E> unionAll(Set<? extends E>... sets)
    {
        return unionAllInto(newSet(sets), sets);
    }

    /**
     * Adds the union of all provided sets into the given target set.
     *
     * @param targetSet the set into which results are placed
     * @param sets the input sets
     * @param <E> the element type
     * @param <R> the target set type
     * @return {@code targetSet}, after modification
     * @see #unionAll(Set...)
     */
    @SafeVarargs
    public static <E, R extends Set<E>> R unionAllInto(
            R targetSet,
            Set<? extends E>... sets)
    {
        Arrays.sort(sets, 0, sets.length, Comparators.descendingCollectionSizeComparator());
        return fillSet(targetSet, Sets.addAllProcedure(), sets);
    }

    /**
     * Returns a new mutable set containing the intersection of two sets.
     *
     * @param setA the first input set
     * @param setB the second input set
     * @param <E> the element type
     * @return a new {@link MutableSet} containing elements common to both sets
     * @see #intersectInto(Set, Set, Set)
     */
    public static <E> MutableSet<E> intersect(
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return intersectInto(newSet(setA, setB), setA, setB);
    }

    /**
     * Retains only elements common to both input sets in the target set.
     *
     * @param targetSet the set into which results are placed
     * @param setA the first input set
     * @param setB the second input set
     * @param <E> the element type
     * @param <R> the target set type
     * @return {@code targetSet}, after modification
     * @see #intersect(Set, Set)
     */
    public static <E, R extends Set<E>> R intersectInto(
            R targetSet,
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return setA.size() < setB.size()
                ? fillSet(targetSet, Sets.retainAllProcedure(), setA, setB)
                : fillSet(targetSet, Sets.retainAllProcedure(), setB, setA);
    }

    /**
     * Returns {@code true} if the first set is a subset of the second set.
     *
     * @param candidateSubset the potential subset
     * @param candidateSuperset the potential superset
     * @param <E> the element type
     * @return {@code true} if all elements of the subset exist in the superset
     */
    public static <E> boolean isSubsetOf(
            Set<? extends E> candidateSubset,
            Set<? extends E> candidateSuperset)
    {
        return candidateSubset.size() <= candidateSuperset.size()
                && candidateSuperset.containsAll(candidateSubset);
    }

    /**
     * Returns {@code true} if the first set is a proper subset of the second set.
     *
     * @param candidateSubset the potential subset
     * @param candidateSuperset the potential superset
     * @param <E> the element type
     * @return {@code true} if the subset is strictly smaller and fully contained
     */
    public static <E> boolean isProperSubsetOf(
            Set<? extends E> candidateSubset,
            Set<? extends E> candidateSuperset)
    {
        return candidateSubset.size() < candidateSuperset.size()
                && candidateSuperset.containsAll(candidateSubset);
    }

    /**
     * Returns the power set of the given set.
     * <p>
     * The power set contains all possible subsets of the input set, including the empty set.
     *
     * @param set the input set
     * @param <T> the element type
     * @return a mutable set containing all subsets of the input set
     */
    public static <T> MutableSet<MutableSet<T>> powerSet(Set<T> set)
    {
        MutableSet<MutableSet<T>> seed = UnifiedSet.newSetWith(UnifiedSet.newSet());
        return Iterate.injectInto(
                seed,
                set,
                (accumulator, element) ->
                        Sets.union(
                                accumulator,
                                accumulator.collect(innerSet -> innerSet.toSet().with(element))));
    }

    /**
     * Returns a lazy iterable representing the Cartesian product of two sets.
     *
     * @param set1 the first input set
     * @param set2 the second input set
     * @param <A> the first element type
     * @param <B> the second element type
     * @return a lazy iterable of pairs representing the Cartesian product
     * @see #cartesianProduct(Set, Set, Function2)
     */
    public static <A, B> LazyIterable<Pair<A, B>> cartesianProduct(Set<A> set1, Set<B> set2)
    {
        return Sets.cartesianProduct(set1, set2, Tuples::pair);
    }

    /**
     * Returns a lazy iterable representing the Cartesian product of two sets,
     * transformed by the given function.
     *
     * @param set1 the first input set
     * @param set2 the second input set
     * @param function a function applied to each pair of elements
     * @param <A> the first element type
     * @param <B> the second element type
     * @param <C> the result type
     * @return a lazy iterable of transformed Cartesian product results
     */
    public static <A, B, C> LazyIterable<C> cartesianProduct(
            Set<A> set1,
            Set<B> set2,
            Function2<? super A, ? super B, ? extends C> function)
    {
        return LazyIterate.cartesianProduct(set1, set2, function);
    }
}
