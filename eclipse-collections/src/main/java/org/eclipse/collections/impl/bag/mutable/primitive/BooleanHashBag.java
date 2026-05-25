/*
 * Copyright (c) 2022 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable.primitive;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.LazyBooleanIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.BooleanBag;
import org.eclipse.collections.api.bag.primitive.ImmutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.block.function.primitive.BooleanToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectBooleanToObjectFunction;
import org.eclipse.collections.api.block.predicate.primitive.BooleanPredicate;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.procedure.primitive.BooleanIntProcedure;
import org.eclipse.collections.api.block.procedure.primitive.BooleanProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.BooleanBags;
import org.eclipse.collections.api.factory.primitive.BooleanSets;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.api.iterator.MutableBooleanIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.set.primitive.BooleanSet;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.tuple.primitive.BooleanIntPair;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.lazy.primitive.LazyBooleanIterableAdapter;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.set.mutable.primitive.BooleanHashSet;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

/**
 * BooleanHashBag is similar to {@link HashBag}, and is memory-optimized for boolean primitives.
 *
 * @since 3.0.
 */
public final class BooleanHashBag implements MutableBooleanBag, Externalizable
{
    private static final long serialVersionUID = 1L;

    private int falseCount;
    private int trueCount;

    public BooleanHashBag()
    {
    }

    public BooleanHashBag(BooleanIterable iterable)
    {
        this();
        this.addAll(iterable);
    }

    public BooleanHashBag(boolean... elements)
    {
        this();
        this.addAll(elements);
    }

    public BooleanHashBag(BooleanHashBag bag)
    {
        this.falseCount = bag.falseCount;
        this.trueCount = bag.trueCount;
    }

    public static BooleanHashBag newBagWith(boolean... source)
    {
        BooleanHashBag result = new BooleanHashBag();
        result.addAll(source);
        return result;
    }

    public static BooleanHashBag newBag(BooleanIterable source)
    {
        if (source instanceof BooleanHashBag booleanHashBag)
        {
            return new BooleanHashBag(booleanHashBag);
        }

        return new BooleanHashBag(source);
    }

    private boolean containsFalse()
    {
        return this.falseCount > 0;
    }

    private boolean containsTrue()
    {
        return this.trueCount > 0;
    }

    @Override
    public boolean isEmpty()
    {
        return !this.containsFalse() && !this.containsTrue();
    }

    @Override
    public boolean notEmpty()
    {
        return this.containsFalse() || this.containsTrue();
    }

    @Override
    public int size()
    {
        return this.falseCount + this.trueCount;
    }

    @Override
    public int sizeDistinct()
    {
        return (this.containsFalse() ? 1 : 0) + (this.containsTrue() ? 1 : 0);
    }

    @Override
    public void clear()
    {
        this.falseCount = 0;
        this.trueCount = 0;
    }

    @Override
    public BooleanHashBag with(boolean element)
    {
        this.add(element);
        return this;
    }

    public BooleanHashBag with(boolean element1, boolean element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public BooleanHashBag with(boolean element1, boolean element2, boolean element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    @Override
    public BooleanHashBag withAll(BooleanIterable iterable)
    {
        this.addAll(iterable);
        return this;
    }

    @Override
    public BooleanHashBag without(boolean element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public BooleanHashBag withoutAll(BooleanIterable iterable)
    {
        this.removeAll(iterable);
        return this;
    }

    @Override
    public MutableBooleanBag asUnmodifiable()
    {
        return new UnmodifiableBooleanBag(this);
    }

    @Override
    public MutableBooleanBag asSynchronized()
    {
        return new SynchronizedBooleanBag(this);
    }

    @Override
    public ImmutableBooleanBag toImmutable()
    {
        return BooleanBags.immutable.withAll(this);
    }

    @Override
    public boolean contains(boolean value)
    {
        return value ? this.containsTrue() : this.containsFalse();
    }

    @Override
    public boolean containsAll(boolean... source)
    {
        for (boolean each : source)
        {
            if (!this.contains(each))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(BooleanIterable source)
    {
        return source.allSatisfy(this::contains);
    }

    @Override
    public int occurrencesOf(boolean item)
    {
        return item ? this.trueCount : this.falseCount;
    }

    @Override
    public void forEachWithOccurrences(BooleanIntProcedure procedure)
    {
        if (this.containsFalse())
        {
            procedure.value(false, this.falseCount);
        }
        if (this.containsTrue())
        {
            procedure.value(true, this.trueCount);
        }
    }

    @Override
    public boolean add(boolean item)
    {
        if (item)
        {
            this.trueCount++;
        }
        else
        {
            this.falseCount++;
        }
        return true;
    }

    @Override
    public boolean remove(boolean item)
    {
        if (item)
        {
            if (!this.containsTrue())
            {
                return false;
            }
            this.trueCount--;
        }
        else
        {
            if (!this.containsFalse())
            {
                return false;
            }
            this.falseCount--;
        }
        return true;
    }

    @Override
    public boolean addAll(boolean... source)
    {
        if (source.length < 1)
        {
            return false;
        }

        for (boolean each : source)
        {
            this.add(each);
        }
        return true;
    }

    @Override
    public boolean addAll(BooleanIterable source)
    {
        if (source.isEmpty())
        {
            return false;
        }
        if (source instanceof BooleanBag otherBag)
        {
            otherBag.forEachWithOccurrences(this::addOccurrences);
        }
        else
        {
            BooleanIterator iterator = source.booleanIterator();
            while (iterator.hasNext())
            {
                boolean each = iterator.next();
                this.add(each);
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(boolean... source)
    {
        if (source.length == 0)
        {
            return false;
        }
        int oldSize = this.size();
        for (boolean each : source)
        {
            if (each)
            {
                this.trueCount = 0;
            }
            else
            {
                this.falseCount = 0;
            }
        }
        return this.size() != oldSize;
    }

    @Override
    public boolean removeAll(BooleanIterable source)
    {
        if (source.isEmpty())
        {
            return false;
        }
        int oldSize = this.size();
        if (source instanceof BooleanBag otherBag)
        {
            otherBag.forEachWithOccurrences((each, occurrences) ->
            {
                if (each)
                {
                    this.trueCount = 0;
                }
                else
                {
                    this.falseCount = 0;
                }
            });
        }
        else
        {
            BooleanIterator iterator = source.booleanIterator();
            while (iterator.hasNext())
            {
                boolean each = iterator.next();
                if (each)
                {
                    this.trueCount = 0;
                }
                else
                {
                    this.falseCount = 0;
                }
            }
        }
        return this.size() != oldSize;
    }

    @Override
    public boolean retainAll(BooleanIterable elements)
    {
        int oldSize = this.size();
        BooleanSet set = elements instanceof BooleanSet booleanSet ? booleanSet : elements.toSet();
        if (!set.contains(true) && this.containsTrue())
        {
            this.trueCount = 0;
        }
        if (!set.contains(false) && this.containsFalse())
        {
            this.falseCount = 0;
        }
        return this.size() != oldSize;
    }

    @Override
    public boolean retainAll(boolean... source)
    {
        return this.retainAll(BooleanHashSet.newSetWith(source));
    }

    @Override
    public void addOccurrences(boolean item, int occurrences)
    {
        if (occurrences < 0)
        {
            throw new IllegalArgumentException("Cannot add a negative number of occurrences");
        }
        if (occurrences > 0)
        {
            if (item)
            {
                this.trueCount += occurrences;
            }
            else
            {
                this.falseCount += occurrences;
            }
        }
    }

    @Override
    public boolean removeOccurrences(boolean item, int occurrences)
    {
        if (occurrences < 0)
        {
            throw new IllegalArgumentException("Cannot remove a negative number of occurrences");
        }

        if (occurrences == 0)
        {
            return false;
        }

        if (item)
        {
            if (!this.containsTrue())
            {
                return false;
            }
            this.trueCount -= occurrences;
            if (this.trueCount < 0)
            {
                this.trueCount = 0;
            }
        }
        else
        {
            if (!this.containsFalse())
            {
                return false;
            }
            this.falseCount -= occurrences;
            if (this.falseCount < 0)
            {
                this.falseCount = 0;
            }
        }
        return true;
    }

    /**
     * @since 7.0.
     */
    @Override
    public void each(BooleanProcedure procedure)
    {
        for (int i = 0; i < this.falseCount; i++)
        {
            procedure.value(false);
        }
        for (int i = 0; i < this.trueCount; i++)
        {
            procedure.value(true);
        }
    }

    @Override
    public MutableBooleanBag select(BooleanPredicate predicate)
    {
        MutableBooleanBag result = new BooleanHashBag();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            if (predicate.accept(each))
            {
                result.addOccurrences(each, occurrences);
            }
        });
        return result;
    }

    @Override
    public MutableBooleanBag selectByOccurrences(IntPredicate predicate)
    {
        MutableBooleanBag result = new BooleanHashBag();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            if (predicate.accept(occurrences))
            {
                result.addOccurrences(each, occurrences);
            }
        });
        return result;
    }

    @Override
    public MutableList<BooleanIntPair> topOccurrences(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Cannot use a value of count < 0");
        }
        if (this.size() > 0)
        {
            if (count == 1)
            {
                if (this.trueCount == this.falseCount)
                {
                    return Lists.mutable.with(PrimitiveTuples.pair(true, this.trueCount), PrimitiveTuples.pair(false, this.falseCount));
                }
                if (this.trueCount > this.falseCount)
                {
                    return Lists.mutable.with(PrimitiveTuples.pair(true, this.trueCount));
                }
                return Lists.mutable.with(PrimitiveTuples.pair(false, this.falseCount));
            }
            if (count > 1)
            {
                if (this.trueCount >= this.falseCount)
                {
                    return Lists.mutable.with(
                            PrimitiveTuples.pair(true, this.trueCount),
                            PrimitiveTuples.pair(false, this.falseCount));
                }
                return Lists.mutable.with(
                        PrimitiveTuples.pair(false, this.falseCount),
                        PrimitiveTuples.pair(true, this.trueCount));
            }
        }
        return Lists.mutable.empty();
    }

    @Override
    public MutableList<BooleanIntPair> bottomOccurrences(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Cannot use a value of count < 0");
        }
        if (this.size() > 0)
        {
            if (count == 1)
            {
                if (this.trueCount == this.falseCount)
                {
                    return Lists.mutable.with(PrimitiveTuples.pair(true, this.trueCount), PrimitiveTuples.pair(false, this.falseCount));
                }
                if (this.trueCount < this.falseCount)
                {
                    return Lists.mutable.with(PrimitiveTuples.pair(true, this.trueCount));
                }
                return Lists.mutable.with(PrimitiveTuples.pair(false, this.falseCount));
            }
            if (count > 1)
            {
                if (this.trueCount <= this.falseCount)
                {
                    return Lists.mutable.with(
                            PrimitiveTuples.pair(true, this.trueCount),
                            PrimitiveTuples.pair(false, this.falseCount));
                }
                return Lists.mutable.with(
                        PrimitiveTuples.pair(false, this.falseCount),
                        PrimitiveTuples.pair(true, this.trueCount));
            }
        }
        return Lists.mutable.empty();
    }

    @Override
    public MutableBooleanBag reject(BooleanPredicate predicate)
    {
        MutableBooleanBag result = new BooleanHashBag();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            if (!predicate.accept(each))
            {
                result.addOccurrences(each, occurrences);
            }
        });
        return result;
    }

    @Override
    public <T> T injectInto(T injectedValue, ObjectBooleanToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        BooleanIterator it = this.booleanIterator();
        while (it.hasNext())
        {
            result = function.valueOf(result, it.next());
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
                result.add(BooleanBags.mutable.withAll(this));
            }
            else
            {
                BooleanIterator iterator = this.booleanIterator();
                while (iterator.hasNext())
                {
                    MutableBooleanBag batch = BooleanBags.mutable.empty();
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
    public boolean equals(Object otherBag)
    {
        if (otherBag == this)
        {
            return true;
        }
        if (!(otherBag instanceof BooleanBag))
        {
            return false;
        }
        BooleanBag bag = (BooleanBag) otherBag;
        if (this.sizeDistinct() != bag.sizeDistinct())
        {
            return false;
        }

        return this.falseCount == bag.occurrencesOf(false) && this.trueCount == bag.occurrencesOf(true);
    }

    @Override
    public int hashCode()
    {
        int result = 0;

        if (this.containsFalse())
        {
            result = 1237 ^ this.falseCount;
        }
        if (this.containsTrue())
        {
            result += 1231 ^ this.trueCount;
        }

        return result;
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
            boolean firstItem = true;
            for (int i = 0; i < this.falseCount; i++)
            {
                if (!firstItem)
                {
                    appendable.append(separator);
                }
                appendable.append("false");
                firstItem = false;
            }
            for (int i = 0; i < this.trueCount; i++)
            {
                if (!firstItem)
                {
                    appendable.append(separator);
                }
                appendable.append("true");
                firstItem = false;
            }
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int count(BooleanPredicate predicate)
    {
        int result = 0;
        if (this.containsFalse() && predicate.accept(false))
        {
            result = this.falseCount;
        }
        if (this.containsTrue() && predicate.accept(true))
        {
            result += this.trueCount;
        }

        return result;
    }

    @Override
    public boolean anySatisfy(BooleanPredicate predicate)
    {
        return this.containsFalse() && predicate.accept(false) || this.containsTrue() && predicate.accept(true);
    }

    @Override
    public boolean allSatisfy(BooleanPredicate predicate)
    {
        return (!this.containsFalse() || predicate.accept(false)) && (!this.containsTrue() || predicate.accept(true));
    }

    @Override
    public boolean noneSatisfy(BooleanPredicate predicate)
    {
        return (!this.containsFalse() || !predicate.accept(false)) && (!this.containsTrue() || !predicate.accept(true));
    }

    @Override
    public boolean detectIfNone(BooleanPredicate predicate, boolean ifNone)
    {
        if (this.containsFalse() && predicate.accept(false))
        {
            return false;
        }

        if (this.containsTrue() && predicate.accept(true))
        {
            return true;
        }

        return ifNone;
    }

    @Override
    public <V> MutableBag<V> collect(BooleanToObjectFunction<? extends V> function)
    {
        MutableBag<V> result = HashBag.newBag();
        if (this.containsFalse())
        {
            result.addOccurrences(function.valueOf(false), this.falseCount);
        }
        if (this.containsTrue())
        {
            result.addOccurrences(function.valueOf(true), this.trueCount);
        }
        return result;
    }

    @Override
    public boolean[] toArray()
    {
        boolean[] array = new boolean[this.size()];
        int[] index = {0};

        this.forEachWithOccurrences((each, occurrences) ->
        {
            for (int i = 0; i < occurrences; i++)
            {
                array[index[0]] = each;
                index[0]++;
            }
        });
        return array;
    }

    @Override
    public boolean[] toArray(boolean[] array)
    {
        if (array.length < this.size())
        {
            array = new boolean[this.size()];
        }
        int[] index = {0};

        boolean[] finalBypass = array;

        this.forEachWithOccurrences((each, occurrences) ->
        {
            for (int i = 0; i < occurrences; i++)
            {
                finalBypass[index[0]] = each;
                index[0]++;
            }
        });
        return array;
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
    public MutableBooleanIterator booleanIterator()
    {
        return new InternalIterator(this.falseCount, this.trueCount);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.falseCount);
        out.writeInt(this.trueCount);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException
    {
        this.falseCount = in.readInt();
        this.trueCount = in.readInt();
    }

    private final class InternalIterator implements MutableBooleanIterator
    {
        private int internalFalseCount;
        private int internalTrueCount;
        private boolean lastBooleanValue;
        private boolean removedAlready = true;

        private InternalIterator(int falseCount, int trueCount)
        {
            this.internalFalseCount = falseCount;
            this.internalTrueCount = trueCount;
        }

        @Override
        public boolean hasNext()
        {
            return this.internalFalseCount > 0 || this.internalTrueCount > 0;
        }

        @Override
        public boolean next()
        {
            this.removedAlready = false;
            if (this.internalFalseCount > 0)
            {
                this.internalFalseCount--;
                this.lastBooleanValue = false;
                return false;
            }
            if (this.internalTrueCount > 0)
            {
                this.internalTrueCount--;
                this.lastBooleanValue = true;
                return true;
            }
            throw new NoSuchElementException("next() called, but the iterator is exhausted");
        }

        @Override
        public void remove()
        {
            if (this.removedAlready)
            {
                throw new IllegalStateException();
            }
            if (this.lastBooleanValue)
            {
                BooleanHashBag.this.trueCount--;
            }
            else
            {
                BooleanHashBag.this.falseCount--;
            }
            this.removedAlready = true;
        }
    }

    @Override
    public MutableBooleanSet selectUnique()
    {
        MutableBooleanSet result = BooleanSets.mutable.empty();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            if (occurrences == 1)
            {
                result.add(each);
            }
        });
        return result;
    }
}
