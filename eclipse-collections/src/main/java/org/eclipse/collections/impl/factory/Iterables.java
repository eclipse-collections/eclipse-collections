/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.factory;

import java.util.Comparator;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;

/**
 * Utility class providing convenient factory methods with short, compact names for creating Eclipse Collections.
 * <p>
 * This class serves as a convenience wrapper around the standard factory classes ({@link Lists}, {@link Sets},
 * {@link Bags}, {@link Maps}, etc.) providing shorter method names for more concise code:
 * <ul>
 *   <li>{@code mList()}, {@code mSet()}, {@code mBag()}, {@code mMap()} - Create mutable collections</li>
 *   <li>{@code iList()}, {@code iSet()}, {@code iBag()}, {@code iMap()} - Create immutable collections</li>
 *   <li>{@code mSortedSet()}, {@code mSortedMap()} - Create mutable sorted collections</li>
 *   <li>{@code iSortedSet()}, {@code iSortedMap()} - Create immutable sorted collections</li>
 * </ul>
 * <p>
 * All methods in this class are simple delegations to the underlying factory implementations.
 * <p>
 * This class is thread-safe as it only contains static utility methods.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Compare standard factory usage vs. short names:
 * MutableList<String> list1 = Lists.mutable.with("a", "b", "c");
 * MutableList<String> list2 = Iterables.mList("a", "b", "c");
 *
 * ImmutableSet<Integer> set1 = Sets.immutable.with(1, 2, 3);
 * ImmutableSet<Integer> set2 = Iterables.iSet(1, 2, 3);
 *
 * MutableMap<String, Integer> map1 = Maps.mutable.with("one", 1, "two", 2);
 * MutableMap<String, Integer> map2 = Iterables.mMap("one", 1, "two", 2);
 * }</pre>
 *
 * @see Lists
 * @see Sets
 * @see Bags
 * @see Maps
 * @see SortedSets
 * @see SortedMaps
 */
public final class Iterables
{
    private Iterables()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Creates an empty mutable list.
     *
     * @param <T> the type of elements in the list
     * @return a new empty mutable list
     * @see Lists#mutable
     */
    public static <T> MutableList<T> mList()
    {
        return Lists.mutable.empty();
    }

    /**
     * Creates a mutable list containing the specified elements.
     *
     * @param <T> the type of elements in the list
     * @param elements the elements to include in the list
     * @return a new mutable list containing the specified elements
     * @see Lists#mutable
     */
    public static <T> MutableList<T> mList(T... elements)
    {
        return Lists.mutable.with(elements);
    }

    /**
     * Creates an empty mutable set.
     *
     * @param <T> the type of elements in the set
     * @return a new empty mutable set
     * @see Sets#mutable
     */
    public static <T> MutableSet<T> mSet()
    {
        return Sets.mutable.empty();
    }

    /**
     * Creates a mutable set containing the specified elements.
     *
     * @param <T> the type of elements in the set
     * @param elements the elements to include in the set
     * @return a new mutable set containing the specified elements
     * @see Sets#mutable
     */
    public static <T> MutableSet<T> mSet(T... elements)
    {
        return Sets.mutable.with(elements);
    }

    /**
     * Creates an empty mutable bag.
     *
     * @param <T> the type of elements in the bag
     * @return a new empty mutable bag
     * @see Bags#mutable
     */
    public static <T> MutableBag<T> mBag()
    {
        return Bags.mutable.empty();
    }

    /**
     * Creates a mutable bag containing the specified elements.
     *
     * @param <T> the type of elements in the bag
     * @param elements the elements to include in the bag
     * @return a new mutable bag containing the specified elements
     * @see Bags#mutable
     */
    public static <T> MutableBag<T> mBag(T... elements)
    {
        return Bags.mutable.with(elements);
    }

    /**
     * Creates an empty mutable map.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @return a new empty mutable map
     * @see Maps#mutable
     */
    public static <K, V> MutableMap<K, V> mMap()
    {
        return Maps.mutable.empty();
    }

    /**
     * Creates a mutable map with one key-value pair.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key the key
     * @param value the value
     * @return a new mutable map with the specified key-value pair
     * @see Maps#mutable
     */
    public static <K, V> MutableMap<K, V> mMap(K key, V value)
    {
        return Maps.mutable.with(key, value);
    }

    /**
     * Creates a mutable map with two key-value pairs.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @return a new mutable map with the specified key-value pairs
     * @see Maps#mutable
     */
    public static <K, V> MutableMap<K, V> mMap(K key1, V value1, K key2, V value2)
    {
        return Maps.mutable.with(key1, value1, key2, value2);
    }

    /**
     * Creates a mutable map with three key-value pairs.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @return a new mutable map with the specified key-value pairs
     * @see Maps#mutable
     */
    public static <K, V> MutableMap<K, V> mMap(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return Maps.mutable.with(key1, value1, key2, value2, key3, value3);
    }

    /**
     * Creates a mutable map with four key-value pairs.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @param key4 the fourth key
     * @param value4 the fourth value
     * @return a new mutable map with the specified key-value pairs
     * @see Maps#mutable
     */
    public static <K, V> MutableMap<K, V> mMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return Maps.mutable.with(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    /**
     * Creates an empty mutable sorted set using natural ordering.
     *
     * @param <T> the type of elements in the sorted set
     * @return a new empty mutable sorted set
     * @see SortedSets#mutable
     */
    public static <T> MutableSortedSet<T> mSortedSet()
    {
        return SortedSets.mutable.empty();
    }

    /**
     * Creates a mutable sorted set containing the specified elements using natural ordering.
     *
     * @param <T> the type of elements in the sorted set
     * @param elements the elements to include in the sorted set
     * @return a new mutable sorted set containing the specified elements
     * @see SortedSets#mutable
     */
    public static <T> MutableSortedSet<T> mSortedSet(T... elements)
    {
        return SortedSets.mutable.with(elements);
    }

    /**
     * Creates an empty mutable sorted set using the specified comparator.
     *
     * @param <T> the type of elements in the sorted set
     * @param comparator the comparator to use for ordering elements
     * @return a new empty mutable sorted set using the specified comparator
     * @see SortedSets#mutable
     */
    public static <T> MutableSortedSet<T> mSortedSet(Comparator<? super T> comparator)
    {
        return SortedSets.mutable.with(comparator);
    }

    /**
     * Creates a mutable sorted set containing the specified elements using the specified comparator.
     *
     * @param <T> the type of elements in the sorted set
     * @param comparator the comparator to use for ordering elements
     * @param elements the elements to include in the sorted set
     * @return a new mutable sorted set containing the specified elements
     * @see SortedSets#mutable
     */
    public static <T> MutableSortedSet<T> mSortedSet(Comparator<? super T> comparator, T... elements)
    {
        return SortedSets.mutable.with(comparator, elements);
    }

    /**
     * Creates an empty mutable sorted map using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @return a new empty mutable sorted map
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap()
    {
        return SortedMaps.mutable.empty();
    }

    /**
     * Creates a mutable sorted map with one key-value pair using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param key the key
     * @param value the value
     * @return a new mutable sorted map with the specified key-value pair
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap(K key, V value)
    {
        return SortedMaps.mutable.with(key, value);
    }

    /**
     * Creates a mutable sorted map with two key-value pairs using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @return a new mutable sorted map with the specified key-value pairs
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap(K key1, V value1, K key2, V value2)
    {
        return SortedMaps.mutable.with(key1, value1, key2, value2);
    }

    /**
     * Creates a mutable sorted map with three key-value pairs using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @return a new mutable sorted map with the specified key-value pairs
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return SortedMaps.mutable.with(key1, value1, key2, value2, key3, value3);
    }

    /**
     * Creates a mutable sorted map with four key-value pairs using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @param key4 the fourth key
     * @param value4 the fourth value
     * @return a new mutable sorted map with the specified key-value pairs
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return SortedMaps.mutable.with(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    /**
     * Creates an empty mutable sorted map using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @return a new empty mutable sorted map using the specified comparator
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap(Comparator<? super K> comparator)
    {
        return SortedMaps.mutable.with(comparator);
    }

    /**
     * Creates a mutable sorted map with one key-value pair using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @param key the key
     * @param value the value
     * @return a new mutable sorted map with the specified key-value pair
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap(Comparator<? super K> comparator, K key, V value)
    {
        return SortedMaps.mutable.with(comparator, key, value);
    }

    /**
     * Creates a mutable sorted map with two key-value pairs using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @return a new mutable sorted map with the specified key-value pairs
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap(
            Comparator<? super K> comparator,
            K key1, V value1,
            K key2, V value2)
    {
        return SortedMaps.mutable.with(comparator, key1, value1, key2, value2);
    }

    /**
     * Creates a mutable sorted map with three key-value pairs using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @return a new mutable sorted map with the specified key-value pairs
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap(
            Comparator<? super K> comparator,
            K key1, V value1,
            K key2, V value2,
            K key3, V value3)
    {
        return SortedMaps.mutable.with(comparator, key1, value1, key2, value2, key3, value3);
    }

    /**
     * Creates a mutable sorted map with four key-value pairs using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @param key4 the fourth key
     * @param value4 the fourth value
     * @return a new mutable sorted map with the specified key-value pairs
     * @see SortedMaps#mutable
     */
    public static <K, V> MutableSortedMap<K, V> mSortedMap(
            Comparator<? super K> comparator,
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        return SortedMaps.mutable.with(comparator, key1, value1, key2, value2, key3, value3, key4, value4);
    }

    /**
     * Creates an empty immutable list.
     *
     * @param <T> the type of elements in the list
     * @return a new empty immutable list
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList()
    {
        return Lists.immutable.empty();
    }

    /**
     * Creates an immutable list containing one element.
     *
     * @param <T> the type of elements in the list
     * @param one the element
     * @return a new immutable list containing the specified element
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one)
    {
        return Lists.immutable.with(one);
    }

    /**
     * Creates an immutable list containing two elements.
     *
     * @param <T> the type of elements in the list
     * @param one the first element
     * @param two the second element
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one, T two)
    {
        return Lists.immutable.with(one, two);
    }

    /**
     * Creates an immutable list containing three elements.
     *
     * @param <T> the type of elements in the list
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one, T two, T three)
    {
        return Lists.immutable.with(one, two, three);
    }

    /**
     * Creates an immutable list containing four elements.
     *
     * @param <T> the type of elements in the list
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @param four the fourth element
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one, T two, T three, T four)
    {
        return Lists.immutable.with(one, two, three, four);
    }

    /**
     * Creates an immutable list containing five elements.
     *
     * @param <T> the type of elements in the list
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @param four the fourth element
     * @param five the fifth element
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one, T two, T three, T four, T five)
    {
        return Lists.immutable.with(one, two, three, four, five);
    }

    /**
     * Creates an immutable list containing six elements.
     *
     * @param <T> the type of elements in the list
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @param four the fourth element
     * @param five the fifth element
     * @param six the sixth element
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one, T two, T three, T four, T five, T six)
    {
        return Lists.immutable.with(one, two, three, four, five, six);
    }

    /**
     * Creates an immutable list containing seven elements.
     *
     * @param <T> the type of elements in the list
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @param four the fourth element
     * @param five the fifth element
     * @param six the sixth element
     * @param seven the seventh element
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one, T two, T three, T four, T five, T six, T seven)
    {
        return Lists.immutable.with(one, two, three, four, five, six, seven);
    }

    /**
     * Creates an immutable list containing eight elements.
     *
     * @param <T> the type of elements in the list
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @param four the fourth element
     * @param five the fifth element
     * @param six the sixth element
     * @param seven the seventh element
     * @param eight the eighth element
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one, T two, T three, T four, T five, T six, T seven, T eight)
    {
        return Lists.immutable.with(one, two, three, four, five, six, seven, eight);
    }

    /**
     * Creates an immutable list containing nine elements.
     *
     * @param <T> the type of elements in the list
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @param four the fourth element
     * @param five the fifth element
     * @param six the sixth element
     * @param seven the seventh element
     * @param eight the eighth element
     * @param nine the ninth element
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine)
    {
        return Lists.immutable.with(one, two, three, four, five, six, seven, eight, nine);
    }

    /**
     * Creates an immutable list containing ten elements.
     *
     * @param <T> the type of elements in the list
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @param four the fourth element
     * @param five the fifth element
     * @param six the sixth element
     * @param seven the seventh element
     * @param eight the eighth element
     * @param nine the ninth element
     * @param ten the tenth element
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine, T ten)
    {
        return Lists.immutable.with(one, two, three, four, five, six, seven, eight, nine, ten);
    }

    /**
     * Creates an immutable list containing the specified elements.
     *
     * @param <T> the type of elements in the list
     * @param elements the elements to include in the list
     * @return a new immutable list containing the specified elements
     * @see Lists#immutable
     */
    public static <T> ImmutableList<T> iList(T... elements)
    {
        return Lists.immutable.with(elements);
    }

    /**
     * Creates an empty immutable set.
     *
     * @param <T> the type of elements in the set
     * @return a new empty immutable set
     * @see Sets#immutable
     */
    public static <T> ImmutableSet<T> iSet()
    {
        return Sets.immutable.empty();
    }

    /**
     * Creates an immutable set containing one element.
     *
     * @param <T> the type of elements in the set
     * @param one the element
     * @return a new immutable set containing the specified element
     * @see Sets#immutable
     */
    public static <T> ImmutableSet<T> iSet(T one)
    {
        return Sets.immutable.with(one);
    }

    /**
     * Creates an immutable set containing two elements.
     *
     * @param <T> the type of elements in the set
     * @param one the first element
     * @param two the second element
     * @return a new immutable set containing the specified elements
     * @see Sets#immutable
     */
    public static <T> ImmutableSet<T> iSet(T one, T two)
    {
        return Sets.immutable.with(one, two);
    }

    /**
     * Creates an immutable set containing three elements.
     *
     * @param <T> the type of elements in the set
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @return a new immutable set containing the specified elements
     * @see Sets#immutable
     */
    public static <T> ImmutableSet<T> iSet(T one, T two, T three)
    {
        return Sets.immutable.with(one, two, three);
    }

    /**
     * Creates an immutable set containing four elements.
     *
     * @param <T> the type of elements in the set
     * @param one the first element
     * @param two the second element
     * @param three the third element
     * @param four the fourth element
     * @return a new immutable set containing the specified elements
     * @see Sets#immutable
     */
    public static <T> ImmutableSet<T> iSet(T one, T two, T three, T four)
    {
        return Sets.immutable.with(one, two, three, four);
    }

    /**
     * Creates an immutable set containing the specified elements.
     *
     * @param <T> the type of elements in the set
     * @param elements the elements to include in the set
     * @return a new immutable set containing the specified elements
     * @see Sets#immutable
     */
    public static <T> ImmutableSet<T> iSet(T... elements)
    {
        return Sets.immutable.with(elements);
    }

    /**
     * Creates an empty immutable bag.
     *
     * @param <T> the type of elements in the bag
     * @return a new empty immutable bag
     * @see Bags#immutable
     */
    public static <T> ImmutableBag<T> iBag()
    {
        return Bags.immutable.empty();
    }

    /**
     * Creates an immutable bag containing one element.
     *
     * @param <T> the type of elements in the bag
     * @param one the element
     * @return a new immutable bag containing the specified element
     * @see Bags#immutable
     */
    public static <T> ImmutableBag<T> iBag(T one)
    {
        return Bags.immutable.with(one);
    }

    /**
     * Creates an immutable bag containing the specified elements.
     *
     * @param <T> the type of elements in the bag
     * @param elements the elements to include in the bag
     * @return a new immutable bag containing the specified elements
     * @see Bags#immutable
     */
    public static <T> ImmutableBag<T> iBag(T... elements)
    {
        return Bags.immutable.with(elements);
    }

    /**
     * Creates an empty immutable map.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @return a new empty immutable map
     * @see Maps#immutable
     */
    public static <K, V> ImmutableMap<K, V> iMap()
    {
        return Maps.immutable.empty();
    }

    /**
     * Creates an immutable map with one key-value pair.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key the key
     * @param value the value
     * @return a new immutable map with the specified key-value pair
     * @see Maps#immutable
     */
    public static <K, V> ImmutableMap<K, V> iMap(K key, V value)
    {
        return Maps.immutable.with(key, value);
    }

    /**
     * Creates an immutable map with two key-value pairs.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @return a new immutable map with the specified key-value pairs
     * @see Maps#immutable
     */
    public static <K, V> ImmutableMap<K, V> iMap(K key1, V value1, K key2, V value2)
    {
        return Maps.immutable.with(key1, value1, key2, value2);
    }

    /**
     * Creates an immutable map with three key-value pairs.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @return a new immutable map with the specified key-value pairs
     * @see Maps#immutable
     */
    public static <K, V> ImmutableMap<K, V> iMap(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return Maps.immutable.with(key1, value1, key2, value2, key3, value3);
    }

    /**
     * Creates an immutable map with four key-value pairs.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @param key4 the fourth key
     * @param value4 the fourth value
     * @return a new immutable map with the specified key-value pairs
     * @see Maps#immutable
     */
    public static <K, V> ImmutableMap<K, V> iMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return Maps.immutable.with(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    /**
     * Creates an empty immutable sorted set using natural ordering.
     *
     * @param <T> the type of elements in the sorted set
     * @return a new empty immutable sorted set
     * @see SortedSets#immutable
     */
    public static <T> ImmutableSortedSet<T> iSortedSet()
    {
        return SortedSets.immutable.empty();
    }

    /**
     * Creates an immutable sorted set containing the specified elements using natural ordering.
     *
     * @param <T> the type of elements in the sorted set
     * @param elements the elements to include in the sorted set
     * @return a new immutable sorted set containing the specified elements
     * @see SortedSets#immutable
     */
    public static <T> ImmutableSortedSet<T> iSortedSet(T... elements)
    {
        return SortedSets.immutable.with(elements);
    }

    /**
     * Creates an empty immutable sorted set using the specified comparator.
     *
     * @param <T> the type of elements in the sorted set
     * @param comparator the comparator to use for ordering elements
     * @return a new empty immutable sorted set using the specified comparator
     * @see SortedSets#immutable
     */
    public static <T> ImmutableSortedSet<T> iSortedSet(Comparator<? super T> comparator)
    {
        return SortedSets.immutable.with(comparator);
    }

    /**
     * Creates an immutable sorted set containing the specified elements using the specified comparator.
     *
     * @param <T> the type of elements in the sorted set
     * @param comparator the comparator to use for ordering elements
     * @param elements the elements to include in the sorted set
     * @return a new immutable sorted set containing the specified elements
     * @see SortedSets#immutable
     */
    public static <T> ImmutableSortedSet<T> iSortedSet(Comparator<? super T> comparator, T... elements)
    {
        return SortedSets.immutable.with(comparator, elements);
    }

    /**
     * Creates an empty immutable sorted map using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @return a new empty immutable sorted map
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap()
    {
        return SortedMaps.immutable.empty();
    }

    /**
     * Creates an immutable sorted map with one key-value pair using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param key the key
     * @param value the value
     * @return a new immutable sorted map with the specified key-value pair
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap(K key, V value)
    {
        return SortedMaps.immutable.with(key, value);
    }

    /**
     * Creates an immutable sorted map with two key-value pairs using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @return a new immutable sorted map with the specified key-value pairs
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap(K key1, V value1, K key2, V value2)
    {
        return SortedMaps.immutable.with(key1, value1, key2, value2);
    }

    /**
     * Creates an immutable sorted map with three key-value pairs using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @return a new immutable sorted map with the specified key-value pairs
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return SortedMaps.immutable.with(key1, value1, key2, value2, key3, value3);
    }

    /**
     * Creates an immutable sorted map with four key-value pairs using natural ordering for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @param key4 the fourth key
     * @param value4 the fourth value
     * @return a new immutable sorted map with the specified key-value pairs
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return SortedMaps.immutable.with(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    /**
     * Creates an empty immutable sorted map using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @return a new empty immutable sorted map using the specified comparator
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap(Comparator<? super K> comparator)
    {
        return SortedMaps.immutable.with(comparator);
    }

    /**
     * Creates an immutable sorted map with one key-value pair using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @param key the key
     * @param value the value
     * @return a new immutable sorted map with the specified key-value pair
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap(Comparator<? super K> comparator, K key, V value)
    {
        return SortedMaps.immutable.with(comparator, key, value);
    }

    /**
     * Creates an immutable sorted map with two key-value pairs using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @return a new immutable sorted map with the specified key-value pairs
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap(
            Comparator<? super K> comparator,
            K key1, V value1,
            K key2, V value2)
    {
        return SortedMaps.immutable.with(comparator, key1, value1, key2, value2);
    }

    /**
     * Creates an immutable sorted map with three key-value pairs using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @return a new immutable sorted map with the specified key-value pairs
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap(
            Comparator<? super K> comparator,
            K key1, V value1,
            K key2, V value2,
            K key3, V value3)
    {
        return SortedMaps.immutable.with(comparator, key1, value1, key2, value2, key3, value3);
    }

    /**
     * Creates an immutable sorted map with four key-value pairs using the specified comparator for keys.
     *
     * @param <K> the type of keys in the sorted map
     * @param <V> the type of values in the sorted map
     * @param comparator the comparator to use for ordering keys
     * @param key1 the first key
     * @param value1 the first value
     * @param key2 the second key
     * @param value2 the second value
     * @param key3 the third key
     * @param value3 the third value
     * @param key4 the fourth key
     * @param value4 the fourth value
     * @return a new immutable sorted map with the specified key-value pairs
     * @see SortedMaps#immutable
     */
    public static <K, V> ImmutableSortedMap<K, V> iSortedMap(
            Comparator<? super K> comparator,
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        return SortedMaps.immutable.with(comparator, key1, value1, key2, value2, key3, value3, key4, value4);
    }
}
