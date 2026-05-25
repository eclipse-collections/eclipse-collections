/*
 * Copyright (c) 2023 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable.primitive;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.LazyBooleanIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.block.function.primitive.BooleanToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectBooleanIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectBooleanToObjectFunction;
import org.eclipse.collections.api.block.predicate.primitive.BooleanPredicate;
import org.eclipse.collections.api.block.procedure.primitive.BooleanIntProcedure;
import org.eclipse.collections.api.block.procedure.primitive.BooleanProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.api.iterator.MutableBooleanIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.set.primitive.BooleanSet;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.stack.primitive.MutableBooleanStack;
import org.eclipse.collections.impl.bag.mutable.primitive.BooleanHashBag;
import org.eclipse.collections.impl.factory.primitive.BooleanStacks;
import org.eclipse.collections.impl.lazy.primitive.LazyBooleanIterableAdapter;
import org.eclipse.collections.impl.lazy.primitive.ReverseBooleanIterable;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.primitive.BooleanHashSet;

/**
 * BooleanArrayList is similar to {@link FastList}, and is memory-optimized for boolean primitives.
 *
 * @since 3.0.
 */
public final class BooleanArrayList
        implements MutableBooleanList, Externalizable
{
    private static final long serialVersionUID = 1L;
    private int size;
    private transient BitSet items;

    public BooleanArrayList()
    {
    }

    public BooleanArrayList(int initialCapacity)
    {
        if (initialCapacity != 0)
        {
            this.items = new BitSet(initialCapacity);
        }
    }

    public BooleanArrayList(boolean... array)
    {
        this.size = array.length;
        this.items = new BitSet(array.length);
        for (int i = 0; i < array.length; i++)
        {
            if (array[i])
            {
                this.items.set(i);
            }
        }
    }

    private BooleanArrayList(BooleanIterable booleanIterable)
    {
        this(booleanIterable.toArray());
    }

    public static BooleanArrayList newListWith(boolean... array)
    {
        return new BooleanArrayList(array);
    }

    public static BooleanArrayList newList(BooleanIterable source)
    {
        return new BooleanArrayList(source);
    }

    /**
     * @since 8.0
     */
    public static BooleanArrayList newWithNValues(int size, boolean value)
    {
        BooleanArrayList newList = new BooleanArrayList(size);
        newList.size = size;
        if (newList.items != null)
        {
            newList.items.set(0, size, value);
        }
        return newList;
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public boolean isEmpty()
    {
        return this.size == 0;
    }

    @Override
    public boolean notEmpty()
    {
        return this.size > 0;
    }

    @Override
    public void clear()
    {
        if (this.items != null)
        {
            this.items.clear();
            this.size = 0;
        }
    }

    @Override
    public boolean contains(boolean value)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (this.items.get(i) == value)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(boolean... source)
    {
        for (boolean value : source)
        {
            if (!this.contains(value))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(BooleanIterable source)
    {
        for (BooleanIterator iterator = source.booleanIterator(); iterator.hasNext(); )
        {
            if (!this.contains(iterator.next()))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean get(int index)
    {
        if (index < this.size)
        {
            return this.items.get(index);
        }
        throw this.newIndexOutOfBoundsException(index);
    }

    private IndexOutOfBoundsException newIndexOutOfBoundsException(int index)
    {
        return new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size);
    }

    @Override
    public boolean getFirst()
    {
        this.checkEmpty();
        return this.items.get(0);
    }

    @Override
    public boolean getLast()
    {
        this.checkEmpty();
        return this.items.get(this.size() - 1);
    }

    private void checkEmpty()
    {
        if (this.isEmpty())
        {
            throw this.newIndexOutOfBoundsException(0);
        }
    }

    @Override
    public int indexOf(boolean object)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (this.items.get(i) == object)
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(boolean object)
    {
        for (int i = this.size - 1; i >= 0; i--)
        {
            if (this.items.get(i) == object)
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean add(boolean newItem)
    {
        if (this.size == 0)
        {
            this.items = new BitSet();
        }
        if (newItem)
        {
            this.items.set(this.size);
        }
        this.size++;
        return true;
    }

    @Override
    public boolean addAll(boolean... source)
    {
        if (source.length < 1)
        {
            return false;
        }

        for (boolean sourceItem : source)
        {
            this.add(sourceItem);
        }
        return true;
    }

    @Override
    public boolean addAll(BooleanIterable source)
    {
        return this.addAll(source.toArray());
    }

    @Override
    public void addAtIndex(int index, boolean element)
    {
        if (index > -1 && index < this.size)
        {
            this.addAtIndexLessThanSize(index, element);
        }
        else if (index == this.size)
        {
            this.add(element);
        }
        else
        {
            throw this.newIndexOutOfBoundsException(index);
        }
    }

    private void addAtIndexLessThanSize(int index, boolean element)
    {
        for (int i = this.size + 1; i > index; i--)
        {
            this.items.set(i, this.items.get(i - 1));
        }
        this.items.set(index, element);
        this.size++;
    }

    @Override
    public boolean addAllAtIndex(int index, boolean... source)
    {
        if (index > this.size || index < 0)
        {
            throw this.newIndexOutOfBoundsException(index);
        }
        if (source.length == 0)
        {
            return false;
        }
        int sourceSize = source.length;
        int newSize = this.size + sourceSize;

        for (int i = newSize - 1; i >= index + sourceSize; i--)
        {
            this.items.set(i, this.items.get(i - sourceSize));
        }

        for (int i = 0; i < sourceSize; i++)
        {
            this.items.set(i + index, source[i]);
        }

        this.size = newSize;
        return true;
    }

    @Override
    public boolean addAllAtIndex(int index, BooleanIterable source)
    {
        return this.addAllAtIndex(index, source.toArray());
    }

    @Override
    public boolean remove(boolean value)
    {
        int index = this.indexOf(value);
        if (index >= 0)
        {
            this.removeAtIndex(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeIf(BooleanPredicate predicate)
    {
        int currentFilledIndex = 0;
        for (int i = 0; i < this.size; i++)
        {
            boolean item = this.items.get(i);
            if (!predicate.accept(item))
            {
                // keep it
                if (currentFilledIndex != i)
                {
                    this.items.set(currentFilledIndex, item);
                }
                currentFilledIndex++;
            }
        }
        boolean changed = currentFilledIndex < this.size;
        this.wipeAndResetTheEnd(currentFilledIndex);
        return changed;
    }

    private void wipeAndResetTheEnd(int newCurrentFilledIndex)
    {
        this.items.clear(newCurrentFilledIndex, this.size);
        this.size = newCurrentFilledIndex;
    }

    @Override
    public boolean removeAll(BooleanIterable source)
    {
        boolean modified = false;
        for (int i = 0; i < this.size; i++)
        {
            if (source.contains(this.items.get(i)))
            {
                this.removeAtIndex(i);
                i--;
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(boolean... source)
    {
        if (this.isEmpty() || source.length == 0)
        {
            return false;
        }
        BooleanHashSet set = BooleanHashSet.newSetWith(source);
        if (set.size() == 2)
        {
            this.items = null;
            this.size = 0;
            return true;
        }
        int oldSize = this.size;
        int trueCount = this.getTrueCount();
        if (set.contains(true))
        {
            this.size -= trueCount;
            this.items.set(0, this.size, false);
        }
        else
        {
            this.size = trueCount;
            this.items.set(0, this.size, true);
        }
        return oldSize != this.size;
    }

    @Override
    public boolean retainAll(BooleanIterable source)
    {
        int oldSize = this.size();
        BooleanSet sourceSet = source instanceof BooleanSet booleanSet ? booleanSet : source.toSet();
        BooleanArrayList retained = this.select(sourceSet::contains);

        this.size = retained.size;
        this.items = retained.items;
        return oldSize != this.size();
    }

    @Override
    public boolean retainAll(boolean... source)
    {
        return this.retainAll(BooleanHashSet.newSetWith(source));
    }

    private int getTrueCount()
    {
        int count = 0;
        for (int i = 0; i < this.size; i++)
        {
            if (this.items.get(i))
            {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean removeAtIndex(int index)
    {
        boolean previous = this.get(index);
        if (this.size - index > 1)
        {
            for (int i = index; i < this.size; i++)
            {
                this.items.set(i, this.items.get(i + 1));
            }
        }
        --this.size;
        this.items.clear(this.size);
        return previous;
    }

    @Override
    public boolean set(int index, boolean element)
    {
        boolean previous = this.get(index);
        this.items.set(index, element);
        return previous;
    }

    @Override
    public BooleanArrayList with(boolean element)
    {
        this.add(element);
        return this;
    }

    @Override
    public BooleanArrayList without(boolean element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public BooleanArrayList withAll(BooleanIterable elements)
    {
        this.addAll(elements.toArray());
        return this;
    }

    @Override
    public BooleanArrayList withoutAll(BooleanIterable elements)
    {
        this.removeAll(elements);
        return this;
    }

    public BooleanArrayList with(boolean element1, boolean element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public BooleanArrayList with(boolean element1, boolean element2, boolean element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public BooleanArrayList with(boolean element1, boolean element2, boolean element3, boolean... elements)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        this.addAll(elements);
        return this;
    }

    @Override
    public MutableBooleanIterator booleanIterator()
    {
        return new InternalBooleanIterator();
    }

    /**
     * @since 7.0.
     */
    @Override
    public void each(BooleanProcedure procedure)
    {
        for (int i = 0; i < this.size; i++)
        {
            procedure.value(this.items.get(i));
        }
    }

    @Override
    public void forEachWithIndex(BooleanIntProcedure procedure)
    {
        for (int i = 0; i < this.size; i++)
        {
            procedure.value(this.items.get(i), i);
        }
    }

    @Override
    public <T> T injectInto(T injectedValue, ObjectBooleanToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        for (int i = 0; i < this.size; i++)
        {
            result = function.valueOf(result, this.items.get(i));
        }
        return result;
    }

    @Override
    public <T> T injectIntoWithIndex(T injectedValue, ObjectBooleanIntToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        for (int i = 0; i < this.size; i++)
        {
            result = function.valueOf(result, this.items.get(i), i);
        }
        return result;
    }

    @Override
    public RichIterable<BooleanIterable> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }
        MutableList<BooleanIterable> result = Lists.mutable.empty();
        if (this.notEmpty())
        {
            if (this.size() <= size)
            {
                result.add(BooleanLists.mutable.withAll(this));
            }
            else
            {
                BooleanIterator iterator = this.booleanIterator();
                while (iterator.hasNext())
                {
                    MutableBooleanList batch = BooleanLists.mutable.empty();
                    for (int i = 0; i < size && iterator.hasNext(); i++)
                    {
                        batch.add(iterator.next());
                    }
                    result.add(batch);
                }
            }
        }
        return result;
    }

    @Override
    public int count(BooleanPredicate predicate)
    {
        int count = 0;
        for (int i = 0; i < this.size; i++)
        {
            if (predicate.accept(this.items.get(i)))
            {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean anySatisfy(BooleanPredicate predicate)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (predicate.accept(this.items.get(i)))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allSatisfy(BooleanPredicate predicate)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (!predicate.accept(this.items.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean noneSatisfy(BooleanPredicate predicate)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (predicate.accept(this.items.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public BooleanArrayList select(BooleanPredicate predicate)
    {
        BooleanArrayList result = new BooleanArrayList();
        for (int i = 0; i < this.size; i++)
        {
            boolean item = this.items.get(i);
            if (predicate.accept(item))
            {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public BooleanArrayList reject(BooleanPredicate predicate)
    {
        BooleanArrayList result = new BooleanArrayList();
        for (int i = 0; i < this.size; i++)
        {
            boolean item = this.items.get(i);
            if (!predicate.accept(item))
            {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public LazyBooleanIterable asReversed()
    {
        return new ReverseBooleanIterable(this);
    }

    @Override
    public BooleanArrayList reverseThis()
    {
        int endIndex = this.size - 1;
        for (int i = 0; i < this.size / 2; i++)
        {
            boolean tempSwapValue = this.items.get(i);
            this.items.set(i, this.items.get(endIndex - i));
            this.items.set(endIndex - i, tempSwapValue);
        }
        return this;
    }

    @Override
    public MutableBooleanList asUnmodifiable()
    {
        return new UnmodifiableBooleanList(this);
    }

    @Override
    public MutableBooleanList asSynchronized()
    {
        return new SynchronizedBooleanList(this);
    }

    @Override
    public ImmutableBooleanList toImmutable()
    {
        if (this.size == 0)
        {
            return BooleanLists.immutable.empty();
        }
        if (this.size == 1)
        {
            return BooleanLists.immutable.with(this.items.get(0));
        }
        return BooleanLists.immutable.with(this.toArray());
    }

    @Override
    public MutableBooleanList subList(int fromIndex, int toIndex)
    {
        throw new UnsupportedOperationException("subList not yet implemented!");
    }

    @Override
    public BooleanArrayList toReversed()
    {
        return new BooleanArrayList(this.asReversed());
    }

    /**
     * @since 6.0
     */
    @Override
    public MutableBooleanList distinct()
    {
        MutableBooleanList target = new BooleanArrayList();
        MutableBooleanSet seenSoFar = new BooleanHashSet();
        for (int i = 0; i < this.size; i++)
        {
            boolean each = this.get(i);
            if (seenSoFar.add(each))
            {
                target.add(each);
            }
        }
        return target;
    }

    @Override
    public boolean detectIfNone(BooleanPredicate predicate, boolean ifNone)
    {
        for (int i = 0; i < this.size; i++)
        {
            boolean item = this.items.get(i);
            if (predicate.accept(item))
            {
                return item;
            }
        }
        return ifNone;
    }

    @Override
    public <V> MutableList<V> collect(BooleanToObjectFunction<? extends V> function)
    {
        MutableList<V> target = FastList.newList(this.size);
        for (int i = 0; i < this.size; i++)
        {
            target.add(function.valueOf(this.items.get(i)));
        }
        return target;
    }

    @Override
    public boolean[] toArray()
    {
        boolean[] newItems = new boolean[this.size];
        for (int i = 0; i < this.size; i++)
        {
            newItems[i] = this.items.get(i);
        }
        return newItems;
    }

    @Override
    public boolean[] toArray(boolean[] target)
    {
        if (target.length < this.size)
        {
            target = new boolean[this.size];
        }
        System.arraycopy(this.items, 0, target, 0, this.size);
        return target;
    }

    @Override
    public boolean equals(Object otherList)
    {
        if (otherList == this)
        {
            return true;
        }
        if (!(otherList instanceof BooleanList))
        {
            return false;
        }
        BooleanList list = (BooleanList) otherList;
        if (this.size != list.size())
        {
            return false;
        }
        for (int i = 0; i < this.size; i++)
        {
            if (this.items.get(i) != list.get(i))
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
        for (int i = 0; i < this.size; i++)
        {
            boolean item = this.items.get(i);
            hashCode = 31 * hashCode + (item ? 1231 : 1237);
        }
        return hashCode;
    }

    @Override
    public String toString()
    {
        return this.makeString("[", ", ", "]");
    }

    @Override
    public String makeString()
    {
        return this.makeString(", ");
    }

    @Override
    public String makeString(String separator)
    {
        return this.makeString("", separator, "");
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        Appendable stringBuilder = new StringBuilder();
        this.appendString(stringBuilder, start, separator, end);
        return stringBuilder.toString();
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.appendString(appendable, ", ");
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.appendString(appendable, "", separator, "");
    }

    @Override
    public void appendString(
            Appendable appendable,
            String start,
            String separator,
            String end)
    {
        try
        {
            appendable.append(start);
            for (int i = 0; i < this.size; i++)
            {
                if (i > 0)
                {
                    appendable.append(separator);
                }
                boolean value = this.items.get(i);
                appendable.append(String.valueOf(value));
            }
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MutableBooleanList toList()
    {
        return BooleanArrayList.newList(this);
    }

    @Override
    public MutableBooleanSet toSet()
    {
        return BooleanHashSet.newSet(this);
    }

    @Override
    public MutableBooleanBag toBag()
    {
        return BooleanHashBag.newBag(this);
    }

    @Override
    public LazyBooleanIterable asLazy()
    {
        return new LazyBooleanIterableAdapter(this);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.size());
        for (int i = 0; i < this.size; i++)
        {
            out.writeBoolean(this.items.get(i));
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException
    {
        this.size = in.readInt();
        if (this.size > 0)
        {
            this.items = new BitSet();
            for (int i = 0; i < this.size; i++)
            {
                this.items.set(i, in.readBoolean());
            }
        }
    }

    @Override
    public MutableBooleanStack toStack()
    {
        return BooleanStacks.mutable.withAll(this);
    }

    private class InternalBooleanIterator implements MutableBooleanIterator
    {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int currentIndex;
        private int lastIndex = -1;

        @Override
        public boolean hasNext()
        {
            return this.currentIndex != BooleanArrayList.this.size();
        }

        @Override
        public boolean next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            boolean next = BooleanArrayList.this.get(this.currentIndex);
            this.lastIndex = this.currentIndex++;
            return next;
        }

        @Override
        public void remove()
        {
            if (this.lastIndex == -1)
            {
                throw new IllegalStateException();
            }
            BooleanArrayList.this.removeAtIndex(this.lastIndex);
            this.currentIndex--;
            this.lastIndex = -1;
        }
    }

    @Override
    public MutableList<Boolean> boxed()
    {
        return new BoxedMutableBooleanList(this);
    }
}
