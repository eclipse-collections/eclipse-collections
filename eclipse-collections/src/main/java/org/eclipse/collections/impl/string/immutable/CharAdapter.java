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

import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.LazyCharIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.block.function.primitive.CharToCharFunction;
import org.eclipse.collections.api.block.function.primitive.CharToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectCharIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectCharToObjectFunction;
import org.eclipse.collections.api.block.predicate.primitive.CharPredicate;
import org.eclipse.collections.api.block.procedure.primitive.CharIntProcedure;
import org.eclipse.collections.api.block.procedure.primitive.CharProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.CharBags;
import org.eclipse.collections.api.factory.primitive.CharLists;
import org.eclipse.collections.api.factory.primitive.CharSets;
import org.eclipse.collections.api.iterator.CharIterator;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.set.primitive.MutableCharSet;
import org.eclipse.collections.api.stack.primitive.MutableCharStack;
import org.eclipse.collections.api.tuple.primitive.CharCharPair;
import org.eclipse.collections.api.tuple.primitive.CharObjectPair;
import org.eclipse.collections.impl.factory.primitive.CharStacks;
import org.eclipse.collections.impl.lazy.primitive.ReverseCharIterable;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.primitive.AbstractCharIterable;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.StringIterate;

/**
 * CharAdapter provides an immutable view of a String as an {@link ImmutableCharList}.
 * This allows treating String char sequences as primitive char collections with Eclipse Collections' rich API.
 * <p>
 * This adapter bridges the gap between Java Strings and Eclipse Collections, enabling functional operations
 * (select, collect, detect, etc.) on String characters without boxing to Character objects. All operations
 * return views or new instances without modifying the underlying String.
 * </p>
 * <p><b>Thread Safety:</b> Immutable and thread-safe. The underlying String is immutable.</p>
 * <p><b>Performance:</b> No boxing overhead for char primitives. Lazy operations available via asLazy().
 * Memory-efficient as it doesn't copy the underlying String's char array.</p>
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * String text = "Hello World";
 * CharAdapter chars = CharAdapter.adapt(text);
 *
 * // Filter characters
 * CharList vowels = chars.select(c -> "aeiouAEIOU".indexOf(c) >= 0);
 * // Result: ['e', 'o', 'o']
 *
 * // Transform characters
 * ImmutableCharList upperChars = chars.collectChar(Character::toUpperCase);
 *
 * // Count matching characters
 * int spaceCount = chars.count(c -> c == ' '); // Returns 1
 *
 * // Check if any/all satisfy predicate
 * boolean hasDigits = chars.anySatisfy(Character::isDigit); // false
 * boolean allLettersOrSpaces = chars.allSatisfy(c ->
 *     Character.isLetter(c) || Character.isWhitespace(c)); // true
 *
 * // Convert back to String
 * String result = chars.makeString(""); // "Hello World"
 *
 * // Create from char array
 * CharAdapter fromChars = CharAdapter.from('a', 'b', 'c');
 * }</pre>
 *
 * @since 7.0
 */
public class CharAdapter
        extends AbstractCharIterable
        implements CharSequence, ImmutableCharList, Serializable
{
    private static final long serialVersionUID = 1L;

    private final String adapted;

    public CharAdapter(String value)
    {
        this.adapted = value;
    }

    public static CharAdapter adapt(String value)
    {
        return new CharAdapter(value);
    }

    public static CharAdapter from(char... chars)
    {
        return new CharAdapter(new String(chars));
    }

    public static CharAdapter from(CharIterable iterable)
    {
        if (iterable instanceof CharAdapter)
        {
            return new CharAdapter(iterable.toString());
        }
        return new CharAdapter(iterable.makeString(""));
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

    /**
     * The value of toString must be strictly implemented as defined in CharSequence.
     */
    @Override
    public String toString()
    {
        return this.adapted;
    }

    @Override
    public CharIterator charIterator()
    {
        return new InternalCharIterator();
    }

    @Override
    public char[] toArray()
    {
        return this.adapted.toCharArray();
    }

    @Override
    public char[] toArray(char[] target)
    {
        int size = this.adapted.length();
        if (target.length < size)
        {
            target = new char[size];
        }

        for (int i = 0; i < size; i++)
        {
            target[i] = this.adapted.charAt(i);
        }

        return target;
    }

    @Override
    public boolean contains(char expected)
    {
        return StringIterate.anySatisfyChar(this.adapted, value -> expected == value);
    }

    @Override
    public void each(CharProcedure procedure)
    {
        StringIterate.forEachChar(this.adapted, procedure);
    }

    @Override
    public CharAdapter distinct()
    {
        MutableCharSet seenSoFar = CharSets.mutable.empty();
        return this.select(seenSoFar::add);
    }

    @Override
    public CharAdapter newWith(char element)
    {
        return new CharAdapter(this.adapted + element);
    }

    @Override
    public CharAdapter newWithout(char element)
    {
        StringBuilder builder = new StringBuilder(this.adapted);
        int indexToRemove = this.indexOf(element);
        if (indexToRemove < 0)
        {
            return this;
        }
        builder.deleteCharAt(indexToRemove);
        return new CharAdapter(builder.toString());
    }

    @Override
    public CharAdapter newWithAll(CharIterable elements)
    {
        MutableCharList mutableCharList = this.toList();
        mutableCharList.addAll(elements);
        return new CharAdapter(new String(mutableCharList.toArray()));
    }

    @Override
    public CharAdapter newWithoutAll(CharIterable elements)
    {
        MutableCharList mutableCharList = this.toList();
        mutableCharList.removeAll(elements);
        return new CharAdapter(new String(mutableCharList.toArray()));
    }

    @Override
    public CharAdapter toReversed()
    {
        StringBuilder builder = new StringBuilder(this.adapted);
        return new CharAdapter(builder.reverse().toString());
    }

    @Override
    public ImmutableCharList subList(int fromIndex, int toIndex)
    {
        throw new UnsupportedOperationException("SubList is not implemented on CharAdapter");
    }

    @Override
    public char get(int index)
    {
        return this.adapted.charAt(index);
    }

    public Character getCharacter(int index)
    {
        return Character.valueOf(this.get(index));
    }

    @Override
    public long dotProduct(CharList list)
    {
        throw new UnsupportedOperationException("DotProduct is not implemented on CharAdapter");
    }

    @Override
    public int binarySearch(char value)
    {
        throw new UnsupportedOperationException("BinarySearch is not implemented on CharAdapter");
    }

    @Override
    public int lastIndexOf(char value)
    {
        for (int i = this.size() - 1; i >= 0; i--)
        {
            if (this.get(i) == value)
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ImmutableCharList toImmutable()
    {
        return this;
    }

    @Override
    public char getLast()
    {
        return this.get(this.size() - 1);
    }

    @Override
    public LazyCharIterable asReversed()
    {
        return ReverseCharIterable.adapt(this);
    }

    @Override
    public <T> T injectIntoWithIndex(T injectedValue, ObjectCharIntToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            result = function.valueOf(result, this.get(i), i);
        }
        return result;
    }

    @Override
    public char getFirst()
    {
        return this.get(0);
    }

    @Override
    public int indexOf(char value)
    {
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            if (this.get(i) == value)
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void forEachWithIndex(CharIntProcedure procedure)
    {
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            procedure.value(this.get(i), i);
        }
    }

    @Override
    public CharAdapter select(CharPredicate predicate)
    {
        return new CharAdapter(StringIterate.selectChar(this.adapted, predicate));
    }

    @Override
    public CharAdapter reject(CharPredicate predicate)
    {
        return new CharAdapter(StringIterate.rejectChar(this.adapted, predicate));
    }

    @Override
    public <V> ImmutableList<V> collect(CharToObjectFunction<? extends V> function)
    {
        int size = this.size();
        MutableList<V> list = FastList.newList(size);
        for (int i = 0; i < size; i++)
        {
            list.add(function.valueOf(this.get(i)));
        }
        return list.toImmutable();
    }

    public CharAdapter collectChar(CharToCharFunction function)
    {
        StringBuilder builder = new StringBuilder(this.length());
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            builder.append(function.valueOf(this.get(i)));
        }
        return new CharAdapter(builder.toString());
    }

    @Override
    public char detectIfNone(CharPredicate predicate, char ifNone)
    {
        return StringIterate.detectCharIfNone(this.adapted, predicate, ifNone);
    }

    @Override
    public int count(CharPredicate predicate)
    {
        return StringIterate.countChar(this.adapted, predicate);
    }

    @Override
    public boolean anySatisfy(CharPredicate predicate)
    {
        return StringIterate.anySatisfyChar(this.adapted, predicate);
    }

    @Override
    public boolean allSatisfy(CharPredicate predicate)
    {
        return StringIterate.allSatisfyChar(this.adapted, predicate);
    }

    @Override
    public boolean noneSatisfy(CharPredicate predicate)
    {
        return StringIterate.noneSatisfyChar(this.adapted, predicate);
    }

    @Override
    public MutableCharList toList()
    {
        int size = this.size();
        MutableCharList list = CharLists.mutable.withInitialCapacity(size);
        for (int i = 0; i < size; i++)
        {
            list.add(this.get(i));
        }
        return list;
    }

    @Override
    public MutableCharSet toSet()
    {
        int size = this.size();
        MutableCharSet set = CharSets.mutable.empty();
        for (int i = 0; i < size; i++)
        {
            set.add(this.get(i));
        }
        return set;
    }

    @Override
    public MutableCharBag toBag()
    {
        int size = this.size();
        MutableCharBag bag = CharBags.mutable.empty();
        for (int i = 0; i < size; i++)
        {
            bag.add(this.get(i));
        }
        return bag;
    }

    @Override
    public <T> T injectInto(T injectedValue, ObjectCharToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            result = function.valueOf(result, this.get(i));
        }
        return result;
    }

    @Override
    public RichIterable<CharIterable> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }
        MutableList<CharIterable> result = Lists.mutable.empty();
        if (this.notEmpty())
        {
            CharIterator iterator = this.charIterator();
            while (iterator.hasNext())
            {
                MutableCharList batch = CharLists.mutable.empty();
                for (int i = 0; i < size && iterator.hasNext(); i++)
                {
                    batch.add(iterator.next());
                }
                result.add(batch);
            }
        }
        return result;
    }

    @Override
    public long sum()
    {
        long sum = 0;
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            sum += this.get(i);
        }
        return sum;
    }

    @Override
    public char max()
    {
        if (this.isEmpty())
        {
            throw new NoSuchElementException();
        }
        char max = this.get(0);
        int size = this.size();
        for (int i = 1; i < size; i++)
        {
            char value = this.get(i);
            if (max < value)
            {
                max = value;
            }
        }
        return max;
    }

    @Override
    public char min()
    {
        if (this.isEmpty())
        {
            throw new NoSuchElementException();
        }
        char min = this.get(0);
        int size = this.size();
        for (int i = 1; i < size; i++)
        {
            char value = this.get(i);
            if (value < min)
            {
                min = value;
            }
        }
        return min;
    }

    @Override
    public int size()
    {
        return this.adapted.length();
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try
        {
            appendable.append(start);
            int size = this.size();
            for (int i = 0; i < size; i++)
            {
                if (i > 0)
                {
                    appendable.append(separator);
                }
                char value = this.get(i);
                appendable.append(value);
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
        if (!(otherList instanceof CharList))
        {
            return false;
        }
        CharList list = (CharList) otherList;
        if (this.size() != list.size())
        {
            return false;
        }
        for (int i = 0; i < this.size(); i++)
        {
            if (this.get(i) != list.get(i))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (int i = 0; i < this.size(); i++)
        {
            char item = this.get(i);
            hashCode = 31 * hashCode + (int) item;
        }
        return hashCode;
    }

    /**
     * @since 9.1.
     */
    @Override
    public ImmutableList<CharCharPair> zipChar(CharIterable iterable)
    {
        int size = this.size();
        int othersize = iterable.size();
        MutableList<CharCharPair> target = Lists.mutable.withInitialCapacity(Math.min(size, othersize));
        CharIterator iterator = iterable.charIterator();
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
    public <T> ImmutableList<CharObjectPair<T>> zip(Iterable<T> iterable)
    {
        int size = this.size();
        int othersize = Iterate.sizeOf(iterable);
        MutableList<CharObjectPair<T>> target = Lists.mutable.withInitialCapacity(Math.min(size, othersize));
        Iterator<T> iterator = iterable.iterator();
        for (int i = 0; i < size && i < othersize; i++)
        {
            target.add(PrimitiveTuples.pair(this.get(i), iterator.next()));
        }
        return target.toImmutable();
    }

    @Override
    public MutableCharStack toStack()
    {
        return CharStacks.mutable.withAll(this);
    }

    private class InternalCharIterator implements CharIterator
    {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int currentIndex;

        @Override
        public boolean hasNext()
        {
            return this.currentIndex != CharAdapter.this.adapted.length();
        }

        @Override
        public char next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            char next = CharAdapter.this.adapted.charAt(this.currentIndex);
            this.currentIndex++;
            return next;
        }
    }
}
