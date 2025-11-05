/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.fixed;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.function.UnaryOperator;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;

/**
 * DoubletonList is a memory-efficient fixed-size list containing exactly two elements.
 * <p>
 * This class provides a space-optimized implementation for lists with exactly two elements,
 * avoiding the overhead of general-purpose list implementations. Like other fixed-size lists,
 * it can be sorted and allows element replacement via {@link #set(int, Object)}, but remains
 * fixed in size.
 * </p>
 * <p><b>Creation:</b> Typically created by calling {@code Lists.fixedSize.of(element1, element2)}.</p>
 * <p><b>Thread Safety:</b> This class is not thread-safe.</p>
 * <p><b>Performance:</b> All access operations run in O(1) constant time.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * FixedSizeList<String> list = Lists.fixedSize.of("first", "second");
 * String first = list.getFirst(); // Returns "first"
 * String last = list.getLast(); // Returns "second"
 * list.set(0, "replaced"); // Allowed
 * list.add("new"); // Throws UnsupportedOperationException
 * }</pre>
 *
 * @param <T> the type of elements maintained by this list
 */
final class DoubletonList<T>
        extends AbstractMemoryEfficientMutableList<T>
        implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private T element1;
    private T element2;

    /**
     * Public no-arg constructor for Externalizable deserialization only.
     * Do not use directly.
     */
    @SuppressWarnings("UnusedDeclaration")
    public DoubletonList()
    {
        // For Externalizable use only
    }

    /**
     * Package-private constructor to create a doubleton list with the specified elements.
     *
     * @param obj1 the first element to be stored in this list
     * @param obj2 the second element to be stored in this list
     */
    DoubletonList(T obj1, T obj2)
    {
        this.element1 = obj1;
        this.element2 = obj2;
    }

    /**
     * Returns a new {@link TripletonList} containing this list's elements plus the specified value.
     * This method allows growing a fixed-size list by returning a new list with a larger size.
     *
     * @param value the element to add to this list's elements
     * @return a new TripletonList containing all three elements
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * DoubletonList<String> list = new DoubletonList<>("a", "b");
     * TripletonList<String> larger = list.with("c");
     * // larger contains ["a", "b", "c"]
     * }</pre>
     */
    @Override
    public TripletonList<T> with(T value)
    {
        return new TripletonList<>(this.element1, this.element2, value);
    }

    /**
     * Creates and returns a shallow copy of this doubleton list.
     * The elements themselves are not cloned.
     *
     * @return a clone of this list instance
     */
    @Override
    public DoubletonList<T> clone()
    {
        return new DoubletonList<>(this.element1, this.element2);
    }

    /**
     * Returns the first element in this list.
     *
     * @return the first element
     */
    @Override
    public T getFirst()
    {
        return this.element1;
    }

    /**
     * Returns the last element in this list.
     *
     * @return the second (last) element
     */
    @Override
    public T getLast()
    {
        return this.element2;
    }

    /**
     * This method throws an exception because this list contains more than one element.
     * The {@code getOnly()} method is specifically for singleton collections.
     *
     * @return never returns normally
     * @throws IllegalStateException always, as this list contains 2 elements
     */
    @Override
    public T getOnly()
    {
        throw new IllegalStateException("Size must be 1 but was " + this.size());
    }

    /**
     * Returns the size of this list, which is always 2.
     *
     * @return 2, the fixed size of this doubleton list
     */
    @Override
    public int size()
    {
        return 2;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return (must be 0 or 1)
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is not 0 or 1
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * DoubletonList<String> list = new DoubletonList<>("first", "second");
     * String first = list.get(0); // Returns "first"
     * String second = list.get(1); // Returns "second"
     * list.get(2); // Throws IndexOutOfBoundsException
     * }</pre>
     */
    @Override
    public T get(int index)
    {
        switch (index)
        {
            case 0:
                return this.element1;
            case 1:
                return this.element2;
            default:
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size());
        }
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * an element {@code e} such that {@code Objects.equals(obj, e)}.
     *
     * @param obj element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    @Override
    public boolean contains(Object obj)
    {
        return Objects.equals(obj, this.element1) || Objects.equals(obj, this.element2);
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * <p>
     * This method is implemented purely to allow the list to be sorted, not because this list
     * should be considered truly mutable. The size remains fixed at 2.
     * </p>
     *
     * @param index index of the element to replace (must be 0 or 1)
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is not 0 or 1
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * DoubletonList<String> list = new DoubletonList<>("old1", "old2");
     * String previous = list.set(0, "new1");
     * // previous is "old1", list now contains ["new1", "old2"]
     * }</pre>
     */
    @Override
    public T set(int index, T element)
    {
        switch (index)
        {
            case 0:
                T previousElement1 = this.element1;
                this.element1 = element;
                return previousElement1;
            case 1:
                T previousElement2 = this.element2;
                this.element2 = element;
                return previousElement2;
            default:
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size());
        }
    }

    /**
     * Replaces each element in this list with the result of applying the given operator.
     * This method is overridden for efficiency.
     *
     * @param operator the operator to apply to each element
     * @since 10.0
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * DoubletonList<String> list = new DoubletonList<>("hello", "world");
     * list.replaceAll(String::toUpperCase);
     * // list now contains ["HELLO", "WORLD"]
     * }</pre>
     */
    @Override
    public void replaceAll(UnaryOperator<T> operator)
    {
        this.element1 = operator.apply(this.element1);
        this.element2 = operator.apply(this.element2);
    }

    /**
     * Executes the given procedure on each element in this list in order.
     *
     * @param procedure the procedure to execute for each element
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * DoubletonList<String> list = new DoubletonList<>("first", "second");
     * list.each(System.out::println); // Prints "first" then "second"
     * }</pre>
     */
    @Override
    public void each(Procedure<? super T> procedure)
    {
        procedure.value(this.element1);
        procedure.value(this.element2);
    }

    /**
     * Executes the given procedure on each element in this list along with its index.
     *
     * @param objectIntProcedure the procedure to execute for each element
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * DoubletonList<String> list = new DoubletonList<>("first", "second");
     * list.forEachWithIndex((element, index) ->
     *     System.out.println(index + ": " + element));
     * // Prints "0: first" then "1: second"
     * }</pre>
     */
    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        objectIntProcedure.value(this.element1, 0);
        objectIntProcedure.value(this.element2, 1);
    }

    /**
     * Executes the given procedure on each element in this list along with the specified parameter.
     *
     * @param procedure the procedure to execute for each element
     * @param parameter the parameter to pass to the procedure
     * @param <P> the type of the parameter
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * DoubletonList<String> list = new DoubletonList<>("first", "second");
     * list.forEachWith((element, param) ->
     *     System.out.println(element + param), "!");
     * // Prints "first!" then "second!"
     * }</pre>
     */
    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        procedure.value(this.element1, parameter);
        procedure.value(this.element2, parameter);
    }

    /**
     * Writes this doubleton list to an ObjectOutput stream for serialization.
     *
     * @param out the stream to write to
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.element1);
        out.writeObject(this.element2);
    }

    /**
     * Reads this doubleton list from an ObjectInput stream for deserialization.
     *
     * @param in the stream to read from
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.element1 = (T) in.readObject();
        this.element2 = (T) in.readObject();
    }
}
