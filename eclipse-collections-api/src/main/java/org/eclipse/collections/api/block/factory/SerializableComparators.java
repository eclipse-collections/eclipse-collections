/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.block.factory;

import org.eclipse.collections.api.block.SerializableComparator;
import org.eclipse.collections.api.block.comparator.FunctionComparator;
import org.eclipse.collections.api.block.function.Function;

/**
 * This class provides a minimal set of SerializableComparator methods for use in the API module.
 *
 * @since 11.1
 */
public final class SerializableComparators
{
    private static final SerializableComparator<?> NATURAL_ORDER_COMPARATOR = new NaturalOrderComparator<>();
    private static final SerializableComparator<?> REVERSE_NATURAL_ORDER_COMPARATOR = new ReverseComparator<>(NATURAL_ORDER_COMPARATOR);

    private SerializableComparators()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Returns a comparator that uses the natural ordering of the objects based on their
     * {@link Comparable#compareTo(Object)} method. This comparator will throw a
     * {@link NullPointerException} if any null values are encountered.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SerializableComparator<String> comparator = SerializableComparators.naturalOrder();
     * MutableSortedSet<String> names = SortedSets.mutable.with(comparator);
     * names.addAll(Lists.mutable.with("Charlie", "Alice", "Bob"));
     * // names contains: ["Alice", "Bob", "Charlie"]
     * }</pre>
     *
     * @param <T> the type of elements to be compared (must implement Comparable)
     * @return a comparator that imposes natural ordering
     * @throws NullPointerException if null values are compared
     */
    public static <T> SerializableComparator<T> naturalOrder()
    {
        return (SerializableComparator<T>) NATURAL_ORDER_COMPARATOR;
    }

    /**
     * Returns a comparator that uses the reverse of natural ordering. This comparator will throw a
     * {@link NullPointerException} if any null values are encountered.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SerializableComparator<Integer> comparator = SerializableComparators.reverseNaturalOrder();
     * MutableSortedSet<Integer> numbers = SortedSets.mutable.with(comparator);
     * numbers.addAll(Lists.mutable.with(1, 3, 2));
     * // numbers contains: [3, 2, 1]
     * }</pre>
     *
     * @param <T> the type of elements to be compared (must implement Comparable)
     * @return a comparator that imposes the reverse of natural ordering
     * @throws NullPointerException if null values are compared
     */
    public static <T> SerializableComparator<T> reverseNaturalOrder()
    {
        return (SerializableComparator<T>) REVERSE_NATURAL_ORDER_COMPARATOR;
    }

    /**
     * Returns a comparator that reverses the order of the specified comparator.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SerializableComparator<Person> byAge = SerializableComparators.byFunction(Person::getAge);
     * SerializableComparator<Person> byAgeDescending = SerializableComparators.reverse(byAge);
     *
     * MutableList<Person> people = Lists.mutable.with(person1, person2);
     * people.sortThis(byAgeDescending); // Sorts oldest to youngest
     * }</pre>
     *
     * @param <T> the type of elements to be compared
     * @param comparator original comparator whose order will be reversed
     * @return a comparator that reverses the order of the specified comparator
     * @throws NullPointerException if comparator is null
     */
    public static <T> SerializableComparator<T> reverse(SerializableComparator<T> comparator)
    {
        if (comparator == null)
        {
            throw new NullPointerException();
        }
        return new ReverseComparator<>(comparator);
    }

    private static final class NaturalOrderComparator<T extends Comparable<T>> implements SerializableComparator<T>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(T o1, T o2)
        {
            if (o1 == null || o2 == null)
            {
                throw new NullPointerException();
            }
            return o1.compareTo(o2);
        }
    }

    private static final class ReverseComparator<T> implements SerializableComparator<T>
    {
        private static final long serialVersionUID = 1L;

        private final SerializableComparator<T> comparator;

        private ReverseComparator(SerializableComparator<T> comparator)
        {
            this.comparator = comparator;
        }

        @Override
        public int compare(T o1, T o2)
        {
            return this.comparator.compare(o2, o1);
        }
    }

    /**
     * Returns a comparator that compares objects by applying the specified function to extract
     * a comparison key, then comparing those keys using natural ordering.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Compare Person objects by last name
     * SerializableComparator<Person> byLastName =
     *     SerializableComparators.byFunction(Person::getLastName);
     *
     * MutableSortedSet<Person> people = SortedSets.mutable.with(byLastName);
     *
     * // Compare strings by length
     * SerializableComparator<String> byLength =
     *     SerializableComparators.byFunction(String::length);
     * }</pre>
     *
     * @param <T> the type of objects being compared
     * @param <V> the type of the comparison key (must implement Comparable)
     * @param function the function to extract the comparison key from each object
     * @return a comparator that compares objects by the extracted key
     */
    public static <T, V extends Comparable<? super V>> SerializableComparator<T> byFunction(Function<? super T, ? extends V> function)
    {
        return SerializableComparators.byFunction(function, SerializableComparators.naturalOrder());
    }

    /**
     * Returns a comparator that compares objects by applying the specified function to extract
     * a comparison key, then comparing those keys using the specified comparator.
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Compare Person by age in descending order
     * SerializableComparator<Person> byAgeDescending =
     *     SerializableComparators.byFunction(
     *         Person::getAge,
     *         SerializableComparators.reverseNaturalOrder());
     *
     * // Custom comparator for extracted values
     * SerializableComparator<String> caseInsensitive =
     *     (s1, s2) -> s1.compareToIgnoreCase(s2);
     * SerializableComparator<Person> byLastNameIgnoreCase =
     *     SerializableComparators.byFunction(Person::getLastName, caseInsensitive);
     * }</pre>
     *
     * @param <T> the type of objects being compared
     * @param <V> the type of the comparison key
     * @param function the function to extract the comparison key from each object
     * @param comparator the comparator to use for comparing the extracted keys
     * @return a comparator that compares objects by the extracted key using the specified comparator
     */
    public static <T, V> SerializableComparator<T> byFunction(Function<? super T, ? extends V> function, SerializableComparator<V> comparator)
    {
        return new FunctionComparator<>(function, comparator);
    }
}
