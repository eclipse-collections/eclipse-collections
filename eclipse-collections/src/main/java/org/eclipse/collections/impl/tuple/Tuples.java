/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.tuple;

import java.util.Map;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Triple;
import org.eclipse.collections.api.tuple.Triplet;
import org.eclipse.collections.api.tuple.Twin;

/**
 * Tuples is a factory class for creating tuple instances (Pair, Twin, Triple, Triplet).
 * Tuples are immutable containers that hold multiple related objects together.
 * <p>
 * This class provides factory methods for:
 * </p>
 * <ul>
 * <li><b>Pair:</b> A container holding two related objects of potentially different types (like Map.Entry)</li>
 * <li><b>Twin:</b> A Pair where both elements have the same type</li>
 * <li><b>Triple:</b> A container holding three related objects of potentially different types</li>
 * <li><b>Triplet:</b> A Triple where all three elements have the same type</li>
 * </ul>
 * <p>
 * Tuples are useful for:
 * </p>
 * <ul>
 * <li>Returning multiple values from methods</li>
 * <li>Grouping related data without creating custom classes</li>
 * <li>Representing key-value pairs or associations</li>
 * <li>Intermediate results in functional transformations (zip operations)</li>
 * </ul>
 * <p><b>Thread Safety:</b> All tuples created are immutable and therefore thread-safe.</p>
 * <p><b>Performance:</b> Lightweight immutable containers with minimal overhead.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create a Pair (different types)
 * Pair<String, Integer> nameAge = Tuples.pair("Alice", 30);
 * String name = nameAge.getOne(); // "Alice"
 * Integer age = nameAge.getTwo(); // 30
 *
 * // Create a Twin (same types)
 * Twin<String> coordinates = Tuples.twin("x", "y");
 *
 * // Create from Map.Entry
 * Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>("key", 42);
 * Pair<String, Integer> pair = Tuples.pairFrom(entry);
 *
 * // Create a Triple
 * Triple<String, Integer, Boolean> person = Tuples.triple("Bob", 25, true);
 *
 * // Convert Pair to List
 * Twin<String> twin = Tuples.twin("a", "b");
 * MutableList<String> list = Tuples.pairToList(twin); // ["a", "b"]
 * }</pre>
 *
 * @see org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples for primitive and object combinations
 * @since 1.0
 */
public final class Tuples
{
    private Tuples()
    {
    }

    public static <K, V> Pair<K, V> pairFrom(Map.Entry<K, V> entry)
    {
        return Tuples.pair(entry.getKey(), entry.getValue());
    }

    /**
     * @since 11.0
     */
    public static <T> MutableList<T> pairToList(Pair<T, T> pair)
    {
        return Lists.mutable.with(pair.getOne(), pair.getTwo());
    }

    /**
     * @since 11.0
     */
    public static <T> FixedSizeList<T> pairToFixedSizeList(Pair<T, T> pair)
    {
        return Lists.fixedSize.with(pair.getOne(), pair.getTwo());
    }

    /**
     * @since 11.0
     */
    public static <T> ImmutableList<T> pairToImmutableList(Pair<T, T> pair)
    {
        return Lists.immutable.with(pair.getOne(), pair.getTwo());
    }

    /**
     * @since 11.0
     */
    public static <T> MutableList<T> tripleToList(Triple<T, T, T> triple)
    {
        return Lists.mutable.with(triple.getOne(), triple.getTwo(), triple.getThree());
    }

    /**
     * @since 11.0
     */
    public static <T> FixedSizeList<T> tripleToFixedSizeList(Triple<T, T, T> triple)
    {
        return Lists.fixedSize.with(triple.getOne(), triple.getTwo(), triple.getThree());
    }

    /**
     * @since 11.0
     */
    public static <T> ImmutableList<T> tripleToImmutableList(Triple<T, T, T> triple)
    {
        return Lists.immutable.with(triple.getOne(), triple.getTwo(), triple.getThree());
    }

    public static <T1, T2> Pair<T1, T2> pair(T1 one, T2 two)
    {
        return new PairImpl<>(one, two);
    }

    public static <T> Twin<T> twin(T one, T two)
    {
        return new TwinImpl<>(one, two);
    }

    /**
     * This method returns a {@link Twin} where both elements in the {@link Twin} are the same as the input element.
     */
    public static <T> Twin<T> identicalTwin(T each)
    {
        return new TwinImpl<>(each, each);
    }

    public static <T1, T2, T3> Triple<T1, T2, T3> triple(T1 one, T2 two, T3 three)
    {
        return new TripleImpl<>(one, two, three);
    }

    public static <T> Triplet<T> triplet(T one, T two, T three)
    {
        return new TripletImpl<>(one, two, three);
    }

    /**
     * This method returns a {@link Triplet} where the three elements in the {@link Triplet} are the same as the input element.
     */
    public static <T> Triplet<T> identicalTriplet(T each)
    {
        return new TripletImpl<>(each, each, each);
    }
}
