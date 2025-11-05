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
import java.util.Comparator;
import java.util.Objects;
import java.util.function.UnaryOperator;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.MutableList;

/**
 * SingletonList is a memory-efficient fixed-size list containing exactly one element.
 * <p>
 * Unlike {@link java.util.Collections#singletonList(Object)}, this implementation can be sorted
 * and allows element replacement via {@link #set(int, Object)}, though it remains fixed in size.
 * </p>
 * <p><b>Creation:</b> Typically created by calling {@code Lists.fixedSize.of(element)}.</p>
 * <p><b>Thread Safety:</b> This class is not thread-safe.</p>
 * <p><b>Performance:</b> All access operations run in O(1) constant time.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * FixedSizeList<String> list = Lists.fixedSize.of("singleton");
 * String element = list.getOnly(); // Returns "singleton"
 * list.set(0, "replaced"); // Allowed
 * list.add("new"); // Throws UnsupportedOperationException
 * }</pre>
 *
 * @param <T> the type of the element maintained by this list
 */
final class SingletonList<T>
        extends AbstractMemoryEfficientMutableList<T>
        implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private T element1;

    /**
     * Public no-arg constructor for Externalizable deserialization only.
     * Do not use directly.
     */
    @SuppressWarnings("UnusedDeclaration")
    public SingletonList()
    {
        // For Externalizable use only
    }

    /**
     * Package-private constructor to create a singleton list with the specified element.
     *
     * @param obj1 the single element to be stored in this list
     */
    SingletonList(T obj1)
    {
        this.element1 = obj1;
    }

    /**
     * Returns a new {@link DoubletonList} containing this list's element plus the specified value.
     * This method allows growing a fixed-size list by returning a new list with a larger size.
     *
     * @param value the element to add to this list's element
     * @return a new DoubletonList containing both elements
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SingletonList<String> list = new SingletonList<>("a");
     * DoubletonList<String> larger = list.with("b");
     * // larger contains ["a", "b"]
     * }</pre>
     */
    @Override
    public DoubletonList<T> with(T value)
    {
        return new DoubletonList<>(this.element1, value);
    }

    /**
     * Creates and returns a shallow copy of this singleton list.
     * The element itself is not cloned.
     *
     * @return a clone of this list instance
     */
    @Override
    public SingletonList<T> clone()
    {
        return new SingletonList<>(this.element1);
    }

    /**
     * Returns the size of this list, which is always 1.
     *
     * @return 1, the fixed size of this singleton list
     */
    @Override
    public int size()
    {
        return 1;
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
        return Objects.equals(obj, this.element1);
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return (must be 0)
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is not 0
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SingletonList<String> list = new SingletonList<>("value");
     * String element = list.get(0); // Returns "value"
     * list.get(1); // Throws IndexOutOfBoundsException
     * }</pre>
     */
    @Override
    public T get(int index)
    {
        if (index == 0)
        {
            return this.element1;
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size());
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * <p>
     * This method is implemented purely to allow the list to be sorted, not because this list
     * should be considered truly mutable. The size remains fixed at 1.
     * </p>
     *
     * @param index index of the element to replace (must be 0)
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is not 0
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SingletonList<String> list = new SingletonList<>("old");
     * String previous = list.set(0, "new");
     * // previous is "old", list now contains "new"
     * }</pre>
     */
    @Override
    public T set(int index, T element)
    {
        if (index == 0)
        {
            T previousElement = this.element1;
            this.element1 = element;
            return previousElement;
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size());
    }

    /**
     * Replaces the element in this list with the result of applying the given operator.
     * This method is overridden for efficiency.
     *
     * @param operator the operator to apply to the element
     * @since 10.0
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SingletonList<String> list = new SingletonList<>("hello");
     * list.replaceAll(String::toUpperCase);
     * // list now contains "HELLO"
     * }</pre>
     */
    @Override
    public void replaceAll(UnaryOperator<T> operator)
    {
        this.element1 = operator.apply(this.element1);
    }

    /**
     * Sorts this list using the specified comparator. Since this list contains only one element,
     * this is a no-op. This method is overridden for efficiency to avoid unnecessary work.
     *
     * @param comparator the comparator to determine the order (ignored for single element)
     * @since 10.0
     */
    @Override
    public void sort(Comparator<? super T> comparator)
    {
    }

    /**
     * Sorts this list using the specified comparator and returns this list.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param comparator the comparator to determine the order (ignored for single element)
     * @return this list
     */
    @Override
    public SingletonList<T> sortThis(Comparator<? super T> comparator)
    {
        return this;
    }

    /**
     * Sorts this list by comparing the values returned by applying the function to each element.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param function the function to extract the comparable sort key
     * @param <V> the type of the comparable sort key
     * @return this list
     */
    @Override
    public <V extends Comparable<? super V>> MutableList<T> sortThisBy(Function<? super T, ? extends V> function)
    {
        return this;
    }

    /**
     * Sorts this list by comparing the int values returned by applying the function to each element.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param function the function to extract the int sort key
     * @return this list
     */
    @Override
    public MutableList<T> sortThisByInt(IntFunction<? super T> function)
    {
        return this;
    }

    /**
     * Sorts this list by comparing the boolean values returned by applying the function to each element.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param function the function to extract the boolean sort key
     * @return this list
     */
    @Override
    public MutableList<T> sortThisByBoolean(BooleanFunction<? super T> function)
    {
        return this;
    }

    /**
     * Sorts this list by comparing the char values returned by applying the function to each element.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param function the function to extract the char sort key
     * @return this list
     */
    @Override
    public MutableList<T> sortThisByChar(CharFunction<? super T> function)
    {
        return this;
    }

    /**
     * Sorts this list by comparing the byte values returned by applying the function to each element.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param function the function to extract the byte sort key
     * @return this list
     */
    @Override
    public MutableList<T> sortThisByByte(ByteFunction<? super T> function)
    {
        return this;
    }

    /**
     * Sorts this list by comparing the short values returned by applying the function to each element.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param function the function to extract the short sort key
     * @return this list
     */
    @Override
    public MutableList<T> sortThisByShort(ShortFunction<? super T> function)
    {
        return this;
    }

    /**
     * Sorts this list by comparing the float values returned by applying the function to each element.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param function the function to extract the float sort key
     * @return this list
     */
    @Override
    public MutableList<T> sortThisByFloat(FloatFunction<? super T> function)
    {
        return this;
    }

    /**
     * Sorts this list by comparing the long values returned by applying the function to each element.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param function the function to extract the long sort key
     * @return this list
     */
    @Override
    public MutableList<T> sortThisByLong(LongFunction<? super T> function)
    {
        return this;
    }

    /**
     * Sorts this list by comparing the double values returned by applying the function to each element.
     * Since this list contains only one element, this simply returns this list unchanged.
     *
     * @param function the function to extract the double sort key
     * @return this list
     */
    @Override
    public MutableList<T> sortThisByDouble(DoubleFunction<? super T> function)
    {
        return this;
    }

    /**
     * Returns the first element in this list, which is the only element.
     *
     * @return the single element in this list
     */
    @Override
    public T getFirst()
    {
        return this.element1;
    }

    /**
     * Returns the last element in this list, which is the only element.
     *
     * @return the single element in this list
     */
    @Override
    public T getLast()
    {
        return this.element1;
    }

    /**
     * Returns the only element in this list. This method is specifically for singleton collections.
     *
     * @return the single element in this list
     */
    @Override
    public T getOnly()
    {
        return this.element1;
    }

    /**
     * Executes the given procedure on the single element in this list.
     *
     * @param procedure the procedure to execute
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SingletonList<String> list = new SingletonList<>("value");
     * list.each(System.out::println); // Prints "value"
     * }</pre>
     */
    @Override
    public void each(Procedure<? super T> procedure)
    {
        procedure.value(this.element1);
    }

    /**
     * Executes the given procedure on the single element in this list along with its index (0).
     *
     * @param objectIntProcedure the procedure to execute
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SingletonList<String> list = new SingletonList<>("value");
     * list.forEachWithIndex((element, index) ->
     *     System.out.println(index + ": " + element));
     * // Prints "0: value"
     * }</pre>
     */
    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        objectIntProcedure.value(this.element1, 0);
    }

    /**
     * Executes the given procedure on the single element in this list along with the specified parameter.
     *
     * @param procedure the procedure to execute
     * @param parameter the parameter to pass to the procedure
     * @param <P> the type of the parameter
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * SingletonList<String> list = new SingletonList<>("value");
     * list.forEachWith((element, param) ->
     *     System.out.println(element + param), "!");
     * // Prints "value!"
     * }</pre>
     */
    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        procedure.value(this.element1, parameter);
    }

    /**
     * Writes this singleton list to an ObjectOutput stream for serialization.
     *
     * @param out the stream to write to
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.element1);
    }

    /**
     * Reads this singleton list from an ObjectInput stream for deserialization.
     *
     * @param in the stream to read from
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.element1 = (T) in.readObject();
    }
}
