/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.block.comparator;

import org.eclipse.collections.api.block.SerializableComparator;
import org.eclipse.collections.api.block.function.Function;

/**
 * A comparator that compares objects by applying a function to extract a comparison key,
 * then using a comparator to compare the extracted keys. This enables sorting objects based
 * on derived values without modifying the objects themselves.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Compare Person objects by last name
 * Function<Person, String> lastNameFunction = Person::getLastName;
 * SerializableComparator<String> stringComparator = SerializableComparators.naturalOrder();
 * FunctionComparator<Person, String> comparator =
 *     new FunctionComparator<>(lastNameFunction, stringComparator);
 *
 * MutableList<Person> people = Lists.mutable.with(person1, person2, person3);
 * people.sortThis(comparator);
 *
 * // Or use the factory method from SerializableComparators
 * SerializableComparator<Person> byLastName =
 *     SerializableComparators.byFunction(Person::getLastName);
 * }</pre>
 *
 * @param <T> the type of objects being compared
 * @param <V> the type of the comparison key extracted by the function
 */
public class FunctionComparator<T, V> implements SerializableComparator<T>
{
    private static final long serialVersionUID = 1L;
    private final Function<? super T, ? extends V> function;
    private final SerializableComparator<V> comparator;

    /**
     * Constructs a FunctionComparator that uses the provided function to extract comparison keys
     * and the provided comparator to compare those keys.
     *
     * @param function the function to extract the comparison key from each object
     * @param comparator the comparator to use for comparing the extracted keys
     */
    public FunctionComparator(Function<? super T, ? extends V> function, SerializableComparator<V> comparator)
    {
        this.function = function;
        this.comparator = comparator;
    }

    /**
     * Compares two objects by first applying the function to extract comparison keys,
     * then using the comparator to compare those keys.
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return a negative integer, zero, or a positive integer as the first argument is less than,
     *         equal to, or greater than the second
     */
    @Override
    public int compare(T o1, T o2)
    {
        V attrValue1 = this.function.valueOf(o1);
        V attrValue2 = this.function.valueOf(o2);
        return this.comparator.compare(attrValue1, attrValue2);
    }
}
