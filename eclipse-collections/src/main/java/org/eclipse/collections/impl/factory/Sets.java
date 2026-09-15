/*
 * Copyright (c) 2026 Goldman Sachs and others.
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
 * <p>
 * Most operations are non-destructive, i.e. no input sets are modified during execution.
 * The exception is operations ending in "Into." These accept the target collection of
 * the final calculation as the first parameter.
 * <p>
 * Some effort is made to return a {@code SortedSet} if any input set is sorted, but
 * this is not guaranteed (e.g., this will not be the case for collections proxied by
 * Hibernate). When in doubt, specify the target collection explicitly with the "Into"
 * version.
 *
 * This class should be used to create instances of MutableSet, ImmutableSet and FixedSizeSet
 * <p>
 * Mutable Examples:
 *
 * <pre>
 * MutableSet&lt;String&gt; emptySet = Sets.mutable.empty();
 * MutableSet&lt;String&gt; setWith = Sets.mutable.with("a", "b", "c");
 * MutableSet&lt;String&gt; setOf = Sets.mutable.of("a", "b", "c");
 * </pre>
 *
 * Immutable Examples:
 *
 * <pre>
 * ImmutableSet&lt;String&gt; emptySet = Sets.immutable.empty();
 * ImmutableSet&lt;String&gt; setWith = Sets.immutable.with("a", "b", "c");
 * ImmutableSet&lt;String&gt; setOf = Sets.immutable.of("a", "b", "c");
 * </pre>
 *
 * FixedSize Examples:
 *
 * <pre>
 * FixedSizeSet&lt;String&gt; emptySet = Sets.fixedSize.empty();
 * FixedSizeSet&lt;String&gt; setWith = Sets.fixedSize.with("a", "b", "c");
 * FixedSizeSet&lt;String&gt; setOf = Sets.fixedSize.of("a", "b", "c");
 * </pre>
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
     * Adapts the given {@link java.util.Set} to the {@link MutableSet} interface.
     * <p>
     * The returned {@code MutableSet} is backed by the provided set, meaning
     * changes made to the returned set will affect the original set and vice versa.
     * <p>
     * This method does not create a copy of the provided set, making it a lightweight
     * way to use the Eclipse Collections API with existing JDK sets.
     *
     * @param set the set to adapt
     * @param <T> the element type
     * @return a {@code MutableSet} view of the original set
     *
     * @since 9.0.
     */
    public static <T> MutableSet<T> adapt(Set<T> set)
    {
        return SetAdapter.adapt(set);
    }

    /**
     * Returns the union of two given sets in a new {@link MutableSet}.
     * <p>
     * Union is the addition of distinct elements of two sets.
     * Common elements are taken only once. No input set is modified.
     *
     * @param <E> the element type
     * @param setA the first set
     * @param setB the second set
     * @return a new {@code MutableSet} which contains the union of {@code setA} and {@code setB}
     *
     * @see #unionInto(MutableSet, Set, Set)
     * @see #newSet(Set, Set)
     */
    public static <E> MutableSet<E> union(
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return unionInto(newSet(setA, setB), setA, setB);
    }

    /**
     * Performs the union of two given sets into the supplied {@code targetSet}.
     * <p>
     * Neither input set is modified.
     * The returned set is the same instance as {@code targetSet}.
     *
     * @param <E> the element type
     * @param <R> the mutable set type
     * @param targetSet the set that will receive the union
     * @param setA the first set
     * @param setB the second set
     * @return the {@code targetSet} containing the union of {@code setA} and {@code setB}
     *
     * @see #fillSet(Set, Procedure2, Set...)
     */
    public static <E, R extends Set<E>> R unionInto(
            R targetSet,
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return setA.size() > setB.size() ? fillSet(targetSet, Sets.addAllProcedure(), setA, setB)
                : fillSet(targetSet, Sets.addAllProcedure(), setB, setA);
    }

    /**
     * Returns the union of all given sets in a new {@link MutableSet}.
     * <p>
     * No input set is modified.
     *
     * @param <E> the element type
     * @param sets the sets to union
     * @return a new {@code MutableSet} containing distinct elements of given sets
     *
     * @see #newSet(Set, Set)
     * @see #unionAllInto(Set, Set...)
     */
    public static <E> MutableSet<E> unionAll(Set<? extends E>... sets)
    {
        return unionAllInto(newSet(sets), sets);
    }

    /**
     * Performs the union of all given sets into the supplied {@code targetSet}.
     * <p>
     * The input sets are not modified.
     * The returned set is the same instance as the {@code targetSet}.
     *
     * @param <E> the element type
     * @param <R> the mutable set type
     * @param targetSet the set that will receive the union
     * @param sets the sets to union
     * @return the {@code targetSet} containing distinct elements of all given sets
     *
     * @see #fillSet(Set, Procedure2, Set...)
     */
    public static <E, R extends Set<E>> R unionAllInto(
            R targetSet,
            Set<? extends E>... sets)
    {
        Arrays.sort(sets, 0, sets.length, Comparators.descendingCollectionSizeComparator());
        return fillSet(targetSet, Sets.addAllProcedure(), sets);
    }

    /**
     * Returns the intersection of two given sets in a new {@link MutableSet}.
     * <p>
     * Intersection contains elements common to both sets. No input set is modified.
     *
     * @param <E> the element type
     * @param setA the first set
     * @param setB the second set
     * @return a new {@code MutableSet} which contains the intersection of {@code setA} and {@code setB}
     *
     * @see #intersectInto(MutableSet, Set, Set)
     * @see #newSet(Set, Set)
     */
    public static <E> MutableSet<E> intersect(
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return intersectInto(newSet(setA, setB), setA, setB);
    }

    /**
     * Performs the intersection of two given sets into the supplied {@code targetSet}.
     * <p>
     * Neither input set is modified.
     * The returned set is the same instance as {@code targetSet}.
     *
     * @param <E> the element type
     * @param <R> the mutable set type
     * @param targetSet the set that will receive the intersection
     * @param setA the first set
     * @param setB the second set
     * @return the {@code targetSet} containing the intersection of {@code setA} and {@code setB}
     *
     * @see #fillSet(Set, Procedure2, Set...)
     */
    public static <E, R extends Set<E>> R intersectInto(
            R targetSet,
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return setA.size() < setB.size() ? fillSet(targetSet, Sets.retainAllProcedure(), setA, setB)
                : fillSet(targetSet, Sets.retainAllProcedure(), setB, setA);
    }

    /**
     * Returns the intersection of all given sets in a new {@link MutableSet}.
     * <p>
     * No input set is modified.
     *
     * @param <E> the element type
     * @param sets the sets to intersect
     * @return a new {@code MutableSet} containing the common elements of given sets
     *
     * @see #newSet(Set, Set)
     * @see #intersectAllInto(Set, Set...)
     */
    public static <E> MutableSet<E> intersectAll(Set<? extends E>... sets)
    {
        return intersectAllInto(newSet(sets), sets);
    }

    /**
     * Performs the intersection of all given sets into the supplied {@code targetSet}.
     * <p>
     * The input sets are not modified.
     * The returned set is the same instance as the {@code targetSet}.
     *
     * @param <E> the element type
     * @param <R> the mutable set type
     * @param targetSet the set that will receive the intersection
     * @param sets the sets to intersect
     * @return the {@code targetSet} containing common elements of all given sets
     *
     * @see #fillSet(Set, Procedure2, Set...)
     */
    public static <E, R extends Set<E>> R intersectAllInto(
            R targetSet,
            Set<? extends E>... sets)
    {
        Arrays.sort(sets, 0, sets.length, Comparators.ascendingCollectionSizeComparator());
        return fillSet(targetSet, Sets.retainAllProcedure(), sets);
    }

    /**
     * Returns the difference of two given sets in a new {@link MutableSet}.
     * <p>
     * Difference contains elements which are only present in the minuend set, but not in subtrahend set.
     * No input set is modified.
     *
     * @param <E> the element type
     * @param minuendSet the set to subtract from
     * @param subtrahendSet the set being subtracted
     * @return a new {@code MutableSet} which contains the difference of two given sets
     *
     * @see #differenceInto(MutableSet, Set, Set)
     * @see #newSet(Set, Set)
     */
    public static <E> MutableSet<E> difference(
            Set<? extends E> minuendSet,
            Set<? extends E> subtrahendSet)
    {
        return differenceInto(newSet(minuendSet, subtrahendSet), minuendSet, subtrahendSet);
    }

    /**
     * Performs the difference of two given sets into the supplied {@code targetSet}.
     * <p>
     * Neither input set is modified.
     * The returned set is the same instance as the {@code targetSet}.
     *
     * @param <E> the element type
     * @param <R> the mutable set type
     * @param targetSet the set that will receive the difference
     * @param minuendSet the set to subtract from
     * @param subtrahendSet the set being subtracted
     * @return the {@code targetSet} containing the difference of {@code minuendSet} and {@code subtrahendSet}
     *
     * @see #fillSet(Set, Procedure2, Set...)
     */
    public static <E, R extends Set<E>> R differenceInto(
            R targetSet,
            Set<? extends E> minuendSet,
            Set<? extends E> subtrahendSet)
    {
        return fillSet(targetSet, Sets.removeAllProcedure(), minuendSet, subtrahendSet);
    }

    /**
     * Returns the difference of first set with respect to
     * all other given sets in a new {@link MutableSet}.
     * <p>
     * No input set is modified.
     *
     * @param <E> the element type
     * @param sets the sets to difference
     * @return a new {@code MutableSet} containing the distinct elements of the first set with respect to the remaining sets
     *
     * @see #newSet(Set, Set)
     * @see #differenceAllInto(Set, Set...)
     */
    public static <E> MutableSet<E> differenceAll(Set<? extends E>... sets)
    {
        return differenceAllInto(newSet(sets), sets);
    }

    /**
     * Performs the difference of first set with respect to
     * all other given sets into the supplied {@code targetSet}.
     * <p>
     * The input sets are not modified.
     * The returned set is the same instance as the {@code targetSet}.
     *
     * @param <E> the element type
     * @param <R> the mutable set type
     * @param targetSet the set that will receive the difference
     * @param sets the sets to difference
     * @return the {@code targetSet} containing the distinct elements of the firest set with respect to the remaining sets
     *
     * @see #fillSet(Set, Procedure2, Set...)
     */
    public static <E, R extends Set<E>> R differenceAllInto(
            R targetSet,
            Set<? extends E>... sets)
    {
        return fillSet(targetSet, Sets.removeAllProcedure(), sets);
    }

    /**
     * Returns the symmetric difference of two given sets in a new {@link MutableSet}.
     * <p>
     * Symmetric difference contains elements which are uncommon to both sets. No input set is modified.
     *
     * @param <E> the element type
     * @param setA the first set
     * @param setB the second set
     * @return a new {@code MutableSet} containing the symmetric difference of the two given sets
     *
     * @see #symmetricDifferenceInto(MutableSet, Set, Set)
     * @see #newSet(Set, Set)
     */
    public static <E> MutableSet<E> symmetricDifference(
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return symmetricDifferenceInto(newSet(setA, setB), setA, setB);
    }

    /**
     * Performs the symmetric difference of two given sets into the supplied {@code targetSet}.
     * <p>
     * Neither input set is modified.
     * The returned set is the same instance as the {@code targetSet}.
     *
     * @param <E> the element type
     * @param <R> the mutable set type
     * @param targetSet the set that will receive the symmetric difference
     * @param setA the first set
     * @param setB the second set
     * @return the {@code targetSet} containing the symmetric difference of {@code setA} and {@code setB}
     *
     * @see #unionInto(MutableSet, Set, Set)
     * @see #differenceInto(MutableSet, Set, Set)
     * @see #newSet(Set, Set)
     */
    public static <E, R extends Set<E>> R symmetricDifferenceInto(
            R targetSet,
            Set<? extends E> setA,
            Set<? extends E> setB)
    {
        return unionInto(
                targetSet,
                differenceInto(newSet(setA, setB), setA, setB),
                differenceInto(newSet(setA, setB), setB, setA));
    }

    /**
     * Determines if the candidate set is the subset of the supplied set.
     * <p>
     * A set is a subset of another if all its elements are contained in the other set.
     *
     * @param <E> the element type
     * @param candidateSubset the potential subset
     * @param candidateSuperset the potential superset
     * @return {@code true} if {@code candidateSubset} is a subset of {@code candidateSuperset}, {@code false} if otherwise
     */
    public static <E> boolean isSubsetOf(
            Set<? extends E> candidateSubset,
            Set<? extends E> candidateSuperset)
    {
        return candidateSubset.size() <= candidateSuperset.size()
                && candidateSuperset.containsAll(candidateSubset);
    }

    /**
     * Determines if the given set is the proper subset of the provided set.
     * <p>
     * A proper subset must be smaller than the superset, and
     * all the elements of the subset must be contained in the superset.
     *
     * @param <E> the element type
     * @param candidateSubset the potential proper subset
     * @param candidateSuperset the potential proper superset
     * @return {@code true} if {@code candidateSubset} is a proper subset of {@code candidateSuperset}, {@code false} if otherwise
     */
    public static <E> boolean isProperSubsetOf(
            Set<? extends E> candidateSubset,
            Set<? extends E> candidateSuperset)
    {
        return candidateSubset.size() < candidateSuperset.size()
                && candidateSuperset.containsAll(candidateSubset);
    }

    private static <E> MutableSet<E> newSet(Set<? extends E>... sets)
    {
        Comparator<? super E> comparator = extractComparator(sets);
        if (comparator != null)
        {
            // TODO: this should return a SortedSetAdapter once implemented
            return SetAdapter.adapt(new TreeSet<>(comparator));
        }
        return UnifiedSet.newSet();
    }

    private static <E> Comparator<? super E> extractComparator(Set<? extends E>... sets)
    {
        Collection<Set<? extends E>> sortedSetCollection = ArrayIterate.select(sets, INSTANCE_OF_SORTED_SET_PREDICATE);
        if (sortedSetCollection.isEmpty())
        {
            return null;
        }
        SortedSet<E> sortedSetWithComparator = (SortedSet<E>) Iterate.detect(sortedSetCollection, HAS_NON_NULL_COMPARATOR);
        if (sortedSetWithComparator != null)
        {
            return sortedSetWithComparator.comparator();
        }
        return Comparators.safeNullsLow(Comparators.naturalOrder());
    }

    private static <E, R extends Set<E>> R fillSet(
            R targetSet,
            Procedure2<Set<? extends E>, R> procedure,
            Set<? extends E>... sets)
    {
        targetSet.addAll(sets[0]);
        for (int i = 1; i < sets.length; i++)
        {
            procedure.value(sets[i], targetSet);
        }
        return targetSet;
    }

    private static <E, R extends Set<E>> Procedure2<Set<? extends E>, R> addAllProcedure()
    {
        return (argumentSet, targetSet) -> targetSet.addAll(argumentSet);
    }

    private static <E, R extends Set<E>> Procedure2<Set<? extends E>, R> retainAllProcedure()
    {
        return (argumentSet, targetSet) -> targetSet.retainAll(argumentSet);
    }

    private static <E, R extends Set<E>> Procedure2<Set<? extends E>, R> removeAllProcedure()
    {
        return (argumentSet, targetSet) -> targetSet.removeAll(argumentSet);
    }

    /**
     * Returns the power set of a given set.
     * <p>
     * Power set contains all possible subsets of a given set.
     *
     * @param <T> the element type
     * @param set the set whose power set is to be computed
     * @return the power set of the supplied set
     */
    public static <T> MutableSet<MutableSet<T>> powerSet(Set<T> set)
    {
        MutableSet<MutableSet<T>> seed = UnifiedSet.newSetWith(UnifiedSet.newSet());
        return Iterate.injectInto(seed, set, (accumulator, element) -> Sets.union(accumulator, accumulator.collect(innerSet -> innerSet.toSet().with(element))));
    }

    /**
     * Returns the cartesian product of two sets as a {@link LazyIterable} of {@link Pair}s.
     *
     * @param <A> the element type of {@code set1}
     * @param <B> the element type of {@code set2}
     * @param set1 the first set
     * @param set2 the second set
     * @return the cartesian product of the two given sets
     *
     * @see #cartesianProduct(Set, Set, Function2)
     */
    public static <A, B> LazyIterable<Pair<A, B>> cartesianProduct(Set<A> set1, Set<B> set2)
    {
        return Sets.cartesianProduct(set1, set2, Tuples::pair);
    }

    /**
     * Returns the cartesian product of two sets, applying the given function to each pair.
     * <p>
     * The result is returned as a {@link LazyIterable}, which saves memory and improves performance.
     *
     * @param <A> element type of {@code set1}
     * @param <B> element type of {@code set2}
     * @param <C> the result type of the function
     * @param set1 the first set
     * @param set2 the second set
     * @param function the function to apply
     * @return the {@code LazyIterable} after the function is applied to each pair of the sequence
     */
    public static <A, B, C> LazyIterable<C> cartesianProduct(Set<A> set1, Set<B> set2, Function2<? super A, ? super B, ? extends C> function)
    {
        return LazyIterate.cartesianProduct(set1, set2, function);
    }
}
