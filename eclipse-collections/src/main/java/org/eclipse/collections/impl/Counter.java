/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.block.function.primitive.IntFunction;

/**
 * A Counter is a mutable wrapper for an integer count value. It provides a way to increment,
 * decrement, and modify a counter value without the immutability constraints of primitive integers.
 * <p>
 * This class is particularly useful in lambda expressions and anonymous inner classes where
 * the {@code final} keyword would prevent modification of a primitive int. Since a Counter object
 * reference can be final while its internal state remains mutable, it allows counter operations
 * in functional contexts.
 * </p>
 * <p>
 * Thread Safety: This class is NOT thread-safe. External synchronization is required if
 * accessed from multiple threads.
 * </p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * Counter counter = new Counter();
 * list.forEach(element -> {
 *     if (predicate.test(element)) {
 *         counter.increment();
 *     }
 * });
 * int matchCount = counter.getCount();
 * }</pre>
 *
 * @since 1.0
 */
public final class Counter implements Externalizable
{
    /**
     * A function that extracts the count value from a Counter instance.
     */
    public static final IntFunction<Counter> TO_COUNT = Counter::getCount;

    private static final long serialVersionUID = 1L;

    private int count;

    /**
     * Constructs a Counter with the specified initial count value.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter = new Counter(10);
     * }</pre>
     *
     * @param startCount the initial count value
     */
    public Counter(int startCount)
    {
        this.count = startCount;
    }

    /**
     * Constructs a Counter with an initial count value of zero.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter = new Counter();
     * }</pre>
     */
    public Counter()
    {
        this(0);
    }

    /**
     * Increments the count by 1.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter = new Counter();
     * counter.increment();  // count is now 1
     * counter.increment();  // count is now 2
     * }</pre>
     */
    public void increment()
    {
        this.count++;
    }

    /**
     * Decrements the count by 1.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter = new Counter(5);
     * counter.decrement();  // count is now 4
     * counter.decrement();  // count is now 3
     * }</pre>
     */
    public void decrement()
    {
        this.count--;
    }

    /**
     * Adds the specified value to the current count.
     * <p>
     * The value can be positive (to increase the count) or negative (to decrease the count).
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter = new Counter(10);
     * counter.add(5);    // count is now 15
     * counter.add(-3);   // count is now 12
     * }</pre>
     *
     * @param value the value to add to the count (can be negative)
     */
    public void add(int value)
    {
        this.count += value;
    }

    /**
     * Returns the current count value.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter = new Counter(10);
     * counter.increment();
     * int value = counter.getCount();  // returns 11
     * }</pre>
     *
     * @return the current count value
     */
    public int getCount()
    {
        return this.count;
    }

    /**
     * Resets the count to zero.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter = new Counter(100);
     * counter.reset();  // count is now 0
     * }</pre>
     */
    public void reset()
    {
        this.count = 0;
    }

    /**
     * Returns a string representation of the count value.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter = new Counter(42);
     * String str = counter.toString();  // returns "42"
     * }</pre>
     *
     * @return the string representation of the count value
     */
    @Override
    public String toString()
    {
        return String.valueOf(this.count);
    }

    /**
     * Compares this Counter to the specified object for equality.
     * <p>
     * Returns {@code true} if and only if the argument is a Counter with the same count value.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter1 = new Counter(5);
     * Counter counter2 = new Counter(5);
     * boolean equal = counter1.equals(counter2);  // returns true
     * }</pre>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Counter))
        {
            return false;
        }

        Counter counter = (Counter) o;

        return this.count == counter.count;
    }

    /**
     * Returns the hash code value for this Counter.
     * <p>
     * The hash code is equal to the count value, ensuring that equal Counters have equal hash codes.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * Counter counter = new Counter(42);
     * int hash = counter.hashCode();  // returns 42
     * }</pre>
     *
     * @return the hash code value for this Counter
     */
    @Override
    public int hashCode()
    {
        return this.count;
    }

    /**
     * Writes the counter's state to the output stream for serialization.
     * <p>
     * This method is called during serialization to save the counter's count value.
     * </p>
     *
     * @param out the stream to write the object to
     * @throws IOException if an I/O error occurs during writing
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.count);
    }

    /**
     * Reads the counter's state from the input stream for deserialization.
     * <p>
     * This method is called during deserialization to restore the counter's count value.
     * </p>
     *
     * @param in the stream to read the object from
     * @throws IOException if an I/O error occurs during reading
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException
    {
        this.count = in.readInt();
    }
}
