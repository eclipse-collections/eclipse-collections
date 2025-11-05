/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.string.immutable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;

import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LazyIntIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.block.function.primitive.IntToIntFunction;
import org.eclipse.collections.api.block.function.primitive.IntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.procedure.primitive.IntIntProcedure;
import org.eclipse.collections.api.block.procedure.primitive.IntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.IntBags;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.IntSets;
import org.eclipse.collections.api.iterator.IntIterator;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.stack.primitive.MutableIntStack;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.api.tuple.primitive.IntObjectPair;
import org.eclipse.collections.impl.factory.primitive.IntStacks;
import org.eclipse.collections.impl.lazy.primitive.ReverseIntIterable;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.primitive.AbstractIntIterable;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * CodePointAdapter provides an immutable view of a String's Unicode code points as an {@link ImmutableIntList}.
 * This correctly handles Unicode characters outside the Basic Multilingual Plane (BMP), including emoji and
 * supplementary characters that require surrogate pairs.
 * <p>
 * Unlike CharAdapter which operates on 16-bit char values, CodePointAdapter works with full Unicode code points
 * (21-bit values). This is the correct way to handle modern Unicode text that may contain emoji,
 * mathematical symbols, or characters from ancient scripts.
 * </p>
 * <p><b>Thread Safety:</b> Immutable and thread-safe. The underlying String is immutable.</p>
 * <p><b>Performance:</b> Processes the String to extract code points. More expensive than CharAdapter but
 * necessary for correct Unicode handling. No boxing to Integer objects.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // String with emoji (supplementary characters)
 * String text = "Hello 👋 World 🌍";
 * CodePointAdapter codePoints = CodePointAdapter.adapt(text);
 *
 * // Count actual characters (not char units)
 * int characterCount = codePoints.size(); // Correctly counts emoji as single characters
 *
 * // Filter code points
 * IntList letters = codePoints.select(Character::isLetter);
 *
 * // Check for emoji (supplementary characters)
 * boolean hasEmoji = codePoints.anySatisfy(cp -> cp > 0xFFFF);
 *
 * // Transform code points
 * ImmutableIntList upperCase = codePoints.collectInt(Character::toUpperCase);
 *
 * // Create from code points array
 * CodePointAdapter fromCodePoints = CodePointAdapter.from(
 *     'H', 'i', 0x1F44B); // 'H', 'i', waving hand emoji
 *
 * // Convert back to String
 * String result = codePoints.toString();
 * }</pre>
 *
 * @since 7.0
 */
public class CodePointAdapter
        extends AbstractIntIterable
        implements CharSequence, ImmutableIntList, Serializable
{
    private static final long serialVersionUID = 1L;

    private final String adapted;

    public CodePointAdapter(String value)
    {
        this.adapted = value;
    }

    public static CodePointAdapter adapt(String value)
    {
        return new CodePointAdapter(value);
    }

    public static CodePointAdapter from(int... codePoints)
    {
        StringBuilder builder = new StringBuilder();
        for (int codePoint : codePoints)
        {
            builder.appendCodePoint(codePoint);
        }
        return new CodePointAdapter(builder.toString());
    }

    public static CodePointAdapter from(IntIterable iterable)
    {
        if (iterable instanceof CodePointAdapter)
        {
            return new CodePointAdapter(iterable.toString());
        }
        StringBuilder builder = iterable.injectInto(new StringBuilder(), StringBuilder::appendCodePoint);
        return new CodePointAdapter(builder.toString());
    }

    @Override
    public char charAt(int index)
    {
        return this.adapted.charAt(index);
    }

    @Override
    public int length()
    {
        return this.adapted.length();
    }

    @Override
    public boolean isEmpty()
    {
        return this.length() == 0;
    }

    @Override
    public String subSequence(int start, int end)
    {
        return this.adapted.substring(start, end);
    }

    public StringBuilder toStringBuilder()
    {
        return new StringBuilder(this.adapted);
    }

    @Override
    public String toString()
    {
        return this.adapted;
    }

    @Override
    public IntIterator intIterator()
    {
        return new InternalIntIterator();
    }

    @Override
    public int[] toArray()
    {
        return this.toList().toArray();
    }

    @Override
    public int[] toArray(int[] target)
    {
        return this.toList().toArray(target);
    }

    @Override
    public boolean contains(int expected)
    {
        int length = this.adapted.length();
        for (int i = 0; i < length; )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (expected == codePoint)
            {
                return true;
            }
            i += Character.charCount(codePoint);
        }
        return false;
    }

    @Override
    public void each(IntProcedure procedure)
    {
        int length = this.adapted.length();
        for (int i = 0; i < length; )
        {
            int codePoint = this.adapted.codePointAt(i);
            procedure.value(codePoint);
            i += Character.charCount(codePoint);
        }
    }

    @Override
    public CodePointAdapter distinct()
    {
        MutableIntSet seenSoFar = IntSets.mutable.empty();
        return this.select(seenSoFar::add);
    }

    @Override
    public CodePointAdapter newWith(int element)
    {
        StringBuilder builder = new StringBuilder(this.adapted);
        builder.appendCodePoint(element);
        return new CodePointAdapter(builder.toString());
    }

    @Override
    public CodePointAdapter newWithout(int element)
    {
        int indexToRemove = this.indexOf(element);
        if (indexToRemove < 0)
        {
            return this;
        }
        int currentIndex = 0;
        int length = this.adapted.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (currentIndex++ != indexToRemove)
            {
                builder.appendCodePoint(codePoint);
            }
            i += Character.charCount(codePoint);
        }
        return new CodePointAdapter(builder.toString());
    }

    @Override
    public CodePointAdapter newWithAll(IntIterable elements)
    {
        StringBuilder builder = new StringBuilder(this.adapted);
        elements.each(builder::appendCodePoint);
        return new CodePointAdapter(builder.toString());
    }

    @Override
    public CodePointAdapter newWithoutAll(IntIterable elements)
    {
        MutableIntList mutableIntList = this.toList();
        mutableIntList.removeAll(elements);
        return CodePointAdapter.from(mutableIntList.toArray());
    }

    @Override
    public CodePointAdapter toReversed()
    {
        StringBuilder builder = new StringBuilder();
        LazyIntIterable reversed = this.asReversed();
        reversed.each(builder::appendCodePoint);
        return new CodePointAdapter(builder.toString());
    }

    @Override
    public ImmutableIntList subList(int fromIndex, int toIndex)
    {
        throw new UnsupportedOperationException("SubList is not implemented on CodePointAdapter");
    }

    @Override
    public int get(int index)
    {
        int currentIndex = 0;
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (index == currentIndex)
            {
                return codePoint;
            }
            i += Character.charCount(codePoint);
            currentIndex++;
        }
        throw new IndexOutOfBoundsException("Index out of bounds");
    }

    @Override
    public long dotProduct(IntList list)
    {
        throw new UnsupportedOperationException("DotProduct is not implemented on CodePointAdapter");
    }

    @Override
    public int binarySearch(int value)
    {
        throw new UnsupportedOperationException("BinarySearch is not implemented on CodePointAdapter");
    }

    @Override
    public int lastIndexOf(int value)
    {
        for (int i = this.size() - 1; i >= 0; i--)
        {
            int codePoint = this.get(i);
            if (codePoint == value)
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ImmutableIntList toImmutable()
    {
        return this;
    }

    @Override
    public int getLast()
    {
        return this.get(this.size() - 1);
    }

    @Override
    public LazyIntIterable asReversed()
    {
        return ReverseIntIterable.adapt(this);
    }

    @Override
    public <T> T injectIntoWithIndex(T injectedValue, ObjectIntIntToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            result = function.valueOf(result, codePoint, i);
            i += Character.charCount(codePoint);
        }
        return result;
    }

    @Override
    public int getFirst()
    {
        return this.get(0);
    }

    @Override
    public int indexOf(int value)
    {
        int currentIndex = 0;
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (codePoint == value)
            {
                return currentIndex;
            }
            i += Character.charCount(codePoint);
            currentIndex++;
        }
        return -1;
    }

    @Override
    public void forEachWithIndex(IntIntProcedure procedure)
    {
        int currentIndex = 0;
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            procedure.value(codePoint, currentIndex);
            i += Character.charCount(codePoint);
            currentIndex++;
        }
    }

    @Override
    public CodePointAdapter select(IntPredicate predicate)
    {
        StringBuilder selected = new StringBuilder();
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (predicate.accept(codePoint))
            {
                selected.appendCodePoint(codePoint);
            }
            i += Character.charCount(codePoint);
        }
        return new CodePointAdapter(selected.toString());
    }

    @Override
    public CodePointAdapter reject(IntPredicate predicate)
    {
        StringBuilder rejected = new StringBuilder();
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (!predicate.accept(codePoint))
            {
                rejected.appendCodePoint(codePoint);
            }
            i += Character.charCount(codePoint);
        }
        return new CodePointAdapter(rejected.toString());
    }

    @Override
    public <V> ImmutableList<V> collect(IntToObjectFunction<? extends V> function)
    {
        MutableList<V> list = FastList.newList(this.adapted.length());
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            list.add(function.valueOf(codePoint));
            i += Character.charCount(codePoint);
        }
        return list.toImmutable();
    }

    public CodePointAdapter collectInt(IntToIntFunction function)
    {
        StringBuilder collected = new StringBuilder(this.length());
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            collected.appendCodePoint(function.valueOf(codePoint));
            i += Character.charCount(codePoint);
        }
        return CodePointAdapter.adapt(collected.toString());
    }

    @Override
    public int detectIfNone(IntPredicate predicate, int ifNone)
    {
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (predicate.accept(codePoint))
            {
                return codePoint;
            }
            i += Character.charCount(codePoint);
        }
        return ifNone;
    }

    @Override
    public int count(IntPredicate predicate)
    {
        int count = 0;
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (predicate.accept(codePoint))
            {
                count++;
            }
            i += Character.charCount(codePoint);
        }
        return count;
    }

    @Override
    public boolean anySatisfy(IntPredicate predicate)
    {
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (predicate.accept(codePoint))
            {
                return true;
            }
            i += Character.charCount(codePoint);
        }
        return false;
    }

    @Override
    public boolean allSatisfy(IntPredicate predicate)
    {
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (!predicate.accept(codePoint))
            {
                return false;
            }
            i += Character.charCount(codePoint);
        }
        return true;
    }

    @Override
    public boolean noneSatisfy(IntPredicate predicate)
    {
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (predicate.accept(codePoint))
            {
                return false;
            }
            i += Character.charCount(codePoint);
        }
        return true;
    }

    @Override
    public MutableIntList toList()
    {
        MutableIntList list = IntLists.mutable.withInitialCapacity(this.adapted.length());
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            list.add(codePoint);
            i += Character.charCount(codePoint);
        }
        return list;
    }

    @Override
    public MutableIntSet toSet()
    {
        MutableIntSet set = IntSets.mutable.empty();
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            set.add(codePoint);
            i += Character.charCount(codePoint);
        }
        return set;
    }

    @Override
    public MutableIntBag toBag()
    {
        MutableIntBag bag = IntBags.mutable.empty();
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            bag.add(codePoint);
            i += Character.charCount(codePoint);
        }
        return bag;
    }

    @Override
    public <T> T injectInto(T injectedValue, ObjectIntToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            result = function.valueOf(result, codePoint);
            i += Character.charCount(codePoint);
        }
        return result;
    }

    @Override
    public RichIterable<IntIterable> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }
        MutableList<IntIterable> result = Lists.mutable.empty();
        if (this.notEmpty())
        {
            if (this.size() <= size)
            {
                result.add(IntLists.immutable.withAll(this));
            }
            else
            {
                IntIterator iterator = this.intIterator();
                while (iterator.hasNext())
                {
                    MutableIntList batch = IntLists.mutable.empty();
                    for (int i = 0; i < size && iterator.hasNext(); i++)
                    {
                        batch.add(iterator.next());
                    }
                    result.add(CodePointList.from(batch));
                }
            }
        }
        return result.toImmutable();
    }

    @Override
    public long sum()
    {
        long sum = 0;
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            sum += codePoint;
            i += Character.charCount(codePoint);
        }
        return sum;
    }

    @Override
    public int max()
    {
        if (this.isEmpty())
        {
            throw new NoSuchElementException();
        }
        int max = this.get(0);
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (max < codePoint)
            {
                max = codePoint;
            }
            i += Character.charCount(codePoint);
        }
        return max;
    }

    @Override
    public int min()
    {
        if (this.isEmpty())
        {
            throw new NoSuchElementException();
        }
        int min = this.get(0);
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (codePoint < min)
            {
                min = codePoint;
            }
            i += Character.charCount(codePoint);
        }
        return min;
    }

    @Override
    public int size()
    {
        int size = 0;
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            i += Character.charCount(codePoint);
            size++;
        }
        return size;
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try
        {
            appendable.append(start);
            for (int i = 0; i < this.adapted.length(); )
            {
                if (i > 0)
                {
                    appendable.append(separator);
                }
                int codePoint = this.adapted.codePointAt(i);
                if (appendable instanceof StringBuilder)
                {
                    ((StringBuilder) appendable).appendCodePoint(codePoint);
                }
                else if (appendable instanceof StringBuffer)
                {
                    ((StringBuffer) appendable).appendCodePoint(codePoint);
                }
                else
                {
                    char[] chars = Character.toChars(codePoint);
                    for (char aChar : chars)
                    {
                        appendable.append(aChar);
                    }
                }
                i += Character.charCount(codePoint);
            }
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object otherList)
    {
        if (otherList == this)
        {
            return true;
        }
        if (otherList instanceof CodePointAdapter)
        {
            return this.equalsCodePointAdapter((CodePointAdapter) otherList);
        }
        if (otherList instanceof IntList)
        {
            return this.equalsIntList((IntList) otherList);
        }
        return false;
    }

    public boolean equalsIntList(IntList list)
    {
        int size = 0;
        for (int i = 0; i < this.adapted.length(); )
        {
            size++;
            int codePoint = this.adapted.codePointAt(i);
            if (size > list.size() || codePoint != list.get(size - 1))
            {
                return false;
            }
            i += Character.charCount(codePoint);
        }
        return size >= list.size();
    }

    private boolean equalsCodePointAdapter(CodePointAdapter adapter)
    {
        if (this.adapted.length() != adapter.adapted.length())
        {
            return false;
        }
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            if (codePoint != adapter.adapted.codePointAt(i))
            {
                return false;
            }
            i += Character.charCount(codePoint);
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (int i = 0; i < this.adapted.length(); )
        {
            int codePoint = this.adapted.codePointAt(i);
            hashCode = 31 * hashCode + codePoint;
            i += Character.charCount(codePoint);
        }
        return hashCode;
    }

    /**
     * @since 9.1.
     */
    @Override
    public ImmutableList<IntIntPair> zipInt(IntIterable iterable)
    {
        int size = this.size();
        int othersize = iterable.size();
        MutableList<IntIntPair> target = Lists.mutable.withInitialCapacity(Math.min(size, othersize));
        IntIterator iterator = iterable.intIterator();
        for (int i = 0; i < size && i < othersize; i++)
        {
            target.add(PrimitiveTuples.pair(this.get(i), iterator.next()));
        }
        return target.toImmutable();
    }

    /**
     * @since 9.1.
     */
    @Override
    public <T> ImmutableList<IntObjectPair<T>> zip(Iterable<T> iterable)
    {
        int size = this.size();
        int othersize = Iterate.sizeOf(iterable);
        MutableList<IntObjectPair<T>> target = Lists.mutable.withInitialCapacity(Math.min(size, othersize));
        Iterator<T> iterator = iterable.iterator();
        for (int i = 0; i < size && iterator.hasNext(); i++)
        {
            target.add(PrimitiveTuples.pair(this.get(i), iterator.next()));
        }
        return target.toImmutable();
    }

    @Override
    public Spliterator.OfInt spliterator()
    {
        return this.adapted.codePoints().spliterator();
    }

    @Override
    public MutableIntStack toStack()
    {
        return IntStacks.mutable.withAll(this);
    }

    private class InternalIntIterator implements IntIterator
    {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int currentIndex;

        @Override
        public boolean hasNext()
        {
            return this.currentIndex != CodePointAdapter.this.adapted.length();
        }

        @Override
        public int next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            int next = CodePointAdapter.this.adapted.codePointAt(this.currentIndex);
            this.currentIndex += Character.charCount(next);
            return next;
        }
    }
}
