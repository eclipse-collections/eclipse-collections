/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api;

/**
 * PrimitiveIterable is the root interface for all primitive collection types in Eclipse Collections.
 * It defines common operations that apply to all primitive collections, including size checking,
 * emptiness testing, and string representation.
 * <p>
 * Primitive collections provide memory-efficient alternatives to their object-based counterparts by storing
 * primitive values directly without boxing. This results in lower memory footprint and better performance for
 * numeric operations.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * IntList numbers = IntLists.mutable.with(1, 2, 3, 4, 5);
 * System.out.println(numbers.size());        // 5
 * System.out.println(numbers.isEmpty());     // false
 * System.out.println(numbers.makeString());  // "1, 2, 3, 4, 5"
 * }</pre>
 *
 * @since 3.0
 */
public interface PrimitiveIterable
{
    /**
     * Returns the number of items in this iterable.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * IntList numbers = IntLists.mutable.with(1, 2, 3);
     * int count = numbers.size();  // 3
     * }</pre>
     *
     * @return the number of items in this iterable
     * @since 3.0
     */
    int size();

    /**
     * Returns true if this iterable has zero items.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * IntList empty = IntLists.mutable.empty();
     * boolean result = empty.isEmpty();  // true
     *
     * IntList nonEmpty = IntLists.mutable.with(1, 2);
     * boolean result2 = nonEmpty.isEmpty();  // false
     * }</pre>
     *
     * @return true if this iterable is empty, false otherwise
     * @since 3.0
     */
    default boolean isEmpty()
    {
        return this.size() == 0;
    }

    /**
     * Returns true if this iterable has one or more items.
     * This is the English equivalent of {@code !this.isEmpty()}.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * IntList numbers = IntLists.mutable.with(1, 2, 3);
     * if (numbers.notEmpty()) {
     *     System.out.println("Has elements");
     * }
     * }</pre>
     *
     * @return true if this iterable is not empty, false otherwise
     * @since 3.0
     */
    default boolean notEmpty()
    {
        return this.size() != 0;
    }

    /**
     * Returns a string with the elements of this iterable separated by commas with spaces and
     * enclosed in square brackets.
     *
     * <pre>
     * Assert.assertEquals("[]", IntLists.mutable.empty().toString());
     * Assert.assertEquals("[1]", IntLists.mutable.with(1).toString());
     * Assert.assertEquals("[1, 2, 3]", IntLists.mutable.with(1, 2, 3).toString());
     * </pre>
     *
     * @return a string representation of this PrimitiveIterable
     * @see java.util.AbstractCollection#toString()
     */
    @Override
    String toString();

    /**
     * Returns a string representation of this collection by delegating to {@link #makeString(String)} and defaulting
     * the separator parameter to the characters {@code ", "} (comma and space).
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * IntList numbers = IntLists.mutable.with(1, 2, 3);
     * String result = numbers.makeString();  // "1, 2, 3"
     * }</pre>
     *
     * @return a string representation of this collection
     * @since 3.0
     */
    default String makeString()
    {
        return this.makeString(", ");
    }

    /**
     * Returns a string representation of this collection by delegating to {@link #makeString(String, String, String)}
     * and defaulting the start and end parameters to {@code ""} (the empty String).
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * IntList numbers = IntLists.mutable.with(1, 2, 3);
     * String result = numbers.makeString(" | ");  // "1 | 2 | 3"
     * }</pre>
     *
     * @param separator the separator to use between elements
     * @return a string representation of this collection
     * @since 3.0
     */
    default String makeString(String separator)
    {
        return this.makeString("", separator, "");
    }

    /**
     * Returns a string representation of this collection with the elements separated by the specified
     * separator and enclosed between the start and end strings.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * IntList numbers = IntLists.mutable.with(1, 2, 3);
     * String result = numbers.makeString("[", ", ", "]");  // "[1, 2, 3]"
     * String result2 = numbers.makeString("{", "; ", "}");  // "{1; 2; 3}"
     * }</pre>
     *
     * @param start the string to prepend to the result
     * @param separator the separator to use between elements
     * @param end the string to append to the result
     * @return a string representation of this collection
     * @since 3.0
     */
    default String makeString(String start, String separator, String end)
    {
        Appendable stringBuilder = new StringBuilder();
        this.appendString(stringBuilder, start, separator, end);
        return stringBuilder.toString();
    }

    /**
     * Prints a string representation of this collection onto the given {@code Appendable}. Prints the string returned
     * by {@link #makeString()}.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * IntList numbers = IntLists.mutable.with(1, 2, 3);
     * StringBuilder builder = new StringBuilder();
     * numbers.appendString(builder);
     * // builder now contains "1, 2, 3"
     * }</pre>
     *
     * @param appendable the Appendable to write to
     * @since 3.0
     */
    default void appendString(Appendable appendable)
    {
        this.appendString(appendable, ", ");
    }

    /**
     * Prints a string representation of this collection onto the given {@code Appendable}. Prints the string returned
     * by {@link #makeString(String)}.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * IntList numbers = IntLists.mutable.with(1, 2, 3);
     * StringBuilder builder = new StringBuilder("Numbers: ");
     * numbers.appendString(builder, " | ");
     * // builder now contains "Numbers: 1 | 2 | 3"
     * }</pre>
     *
     * @param appendable the Appendable to write to
     * @param separator the separator to use between elements
     * @since 3.0
     */
    default void appendString(Appendable appendable, String separator)
    {
        this.appendString(appendable, "", separator, "");
    }

    /**
     * Prints a string representation of this collection onto the given {@code Appendable}. Prints the string returned
     * by {@link #makeString(String, String, String)}.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * IntList numbers = IntLists.mutable.with(1, 2, 3);
     * StringBuilder builder = new StringBuilder();
     * numbers.appendString(builder, "[", ", ", "]");
     * // builder now contains "[1, 2, 3]"
     * }</pre>
     *
     * @param appendable the Appendable to write to
     * @param start the string to prepend to the result
     * @param separator the separator to use between elements
     * @param end the string to append to the result
     * @since 3.0
     */
    void appendString(Appendable appendable, String start, String separator, String end);
}
