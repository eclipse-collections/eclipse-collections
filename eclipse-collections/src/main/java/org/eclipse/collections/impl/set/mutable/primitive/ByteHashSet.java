/*
 * Copyright (c) 2023 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable.primitive;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.LazyByteIterable;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.block.function.primitive.ByteToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectByteToObjectFunction;
import org.eclipse.collections.api.block.predicate.primitive.BytePredicate;
import org.eclipse.collections.api.block.procedure.primitive.ByteProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.primitive.ByteBags;
import org.eclipse.collections.api.factory.primitive.ByteLists;
import org.eclipse.collections.api.iterator.ByteIterator;
import org.eclipse.collections.api.iterator.MutableByteIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.ByteSet;
import org.eclipse.collections.api.set.primitive.ImmutableByteSet;
import org.eclipse.collections.api.set.primitive.MutableByteSet;
import org.eclipse.collections.api.tuple.primitive.ByteBytePair;
import org.eclipse.collections.impl.block.procedure.checked.primitive.CheckedByteProcedure;
import org.eclipse.collections.impl.factory.primitive.ByteSets;
import org.eclipse.collections.impl.lazy.primitive.LazyByteIterableAdapter;
import org.eclipse.collections.impl.set.immutable.primitive.ImmutableByteSetSerializationProxy;

@SuppressWarnings("NarrowingCompoundAssignment")
public final class ByteHashSet implements MutableByteSet, Externalizable
{
    private static final long serialVersionUID = 1L;
    private static final byte MAX_BYTE_GROUP_1 = -65;
    private static final byte MAX_BYTE_GROUP_2 = -1;
    private static final byte MAX_BYTE_GROUP_3 = 63;
    private long bitGroup1; // -128 to -65
    private long bitGroup2; //-64 to -1
    private long bitGroup3; //0 to 63
    private long bitGroup4; // 64 to 127
    private short size;

    public ByteHashSet()
    {
    }

    /**
     * Use {@link ByteHashSet#ByteHashSet()} instead.
     *
     * @deprecated since 5.0.
     */
    @Deprecated
    public ByteHashSet(int initialCapacity)
    {
        if (initialCapacity < 0)
        {
            throw new IllegalArgumentException("initial capacity cannot be less than 0");
        }
    }

    public ByteHashSet(byte... elements)
    {
        this();
        this.addAll(elements);
    }

    public ByteHashSet(ByteHashSet set)
    {
        this.size = set.size;
        this.bitGroup3 = set.bitGroup3;
        this.bitGroup4 = set.bitGroup4;
        this.bitGroup1 = set.bitGroup1;
        this.bitGroup2 = set.bitGroup2;
    }

    @Override
    public MutableSet<Byte> boxed()
    {
        return new BoxedMutableByteSet(this);
    }

    public static ByteHashSet newSet(ByteIterable source)
    {
        if (source instanceof ByteHashSet)
        {
            return new ByteHashSet((ByteHashSet) source);
        }

        return ByteHashSet.newSetWith(source.toArray());
    }

    public static ByteHashSet newSetWith(byte... source)
    {
        return new ByteHashSet(source);
    }

    @Override
    public boolean add(byte element)
    {
        if (element <= MAX_BYTE_GROUP_1)
        {
            long initial = this.bitGroup1;

            this.bitGroup1 |= 1L << (byte) ((element + 1) * -1);

            if (this.bitGroup1 != initial)
            {
                this.size++;
                return true;
            }
        }
        else if (element <= MAX_BYTE_GROUP_2)
        {
            long initial = this.bitGroup2;

            this.bitGroup2 |= 1L << (byte) ((element + 1) * -1);

            if (this.bitGroup2 != initial)
            {
                this.size++;
                return true;
            }
        }
        else if (element <= MAX_BYTE_GROUP_3)
        {
            long initial = this.bitGroup3;

            this.bitGroup3 |= 1L << element;

            if (this.bitGroup3 != initial)
            {
                this.size++;
                return true;
            }
        }
        else
        {
            long initial = this.bitGroup4;

            this.bitGroup4 |= 1L << element;

            if (this.bitGroup4 != initial)
            {
                this.size++;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean remove(byte value)
    {
        if (value <= MAX_BYTE_GROUP_1)
        {
            long initial = this.bitGroup1;
            this.bitGroup1 &= ~(1L << (byte) ((value + 1) * -1));
            if (this.bitGroup1 == initial)
            {
                return false;
            }
            this.size--;
            return true;
        }
        if (value <= MAX_BYTE_GROUP_2)
        {
            long initial = this.bitGroup2;
            this.bitGroup2 &= ~(1L << (byte) ((value + 1) * -1));

            if (this.bitGroup2 == initial)
            {
                return false;
            }
            this.size--;
            return true;
        }
        if (value <= MAX_BYTE_GROUP_3)
        {
            long initial = this.bitGroup3;
            this.bitGroup3 &= ~(1L << value);
            if (this.bitGroup3 == initial)
            {
                return false;
            }
            this.size--;
            return true;
        }

        long initial = this.bitGroup4;
        this.bitGroup4 &= ~(1L << value);
        if (this.bitGroup4 == initial)
        {
            return false;
        }
        this.size--;
        return true;
    }

    @Override
    public boolean contains(byte value)
    {
        if (value <= MAX_BYTE_GROUP_1)
        {
            return ((this.bitGroup1 >>> (byte) ((value + 1) * -1)) & 1L) != 0;
        }
        if (value <= MAX_BYTE_GROUP_2)
        {
            return ((this.bitGroup2 >>> (byte) ((value + 1) * -1)) & 1L) != 0;
        }
        if (value <= MAX_BYTE_GROUP_3)
        {
            return ((this.bitGroup3 >>> value) & 1L) != 0;
        }

        return ((this.bitGroup4 >>> value) & 1L) != 0;
    }

    @Override
    public LazyIterable<ByteBytePair> cartesianProduct(
            ByteSet set)
    {
        return ByteSets.cartesianProduct(this, set);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof ByteSet))
        {
            return false;
        }

        ByteSet other = (ByteSet) obj;
        return this.size() == other.size() && this.containsAll(other.toArray());
    }

    @Override
    public int hashCode()
    {
        return (int) this.sum();
    }

    @Override
    public String toString()
    {
        return this.makeString("[", ", ", "]");
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    @Override
    public boolean notEmpty()
    {
        return this.size() != 0;
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
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try
        {
            appendable.append(start);
            int count = 0;
            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                if (count > 0)
                {
                    appendable.append(separator);
                }

                count++;
                appendable.append(String.valueOf(iterator.next()));
            }

            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addAll(byte... source)
    {
        int oldSize = this.size();
        for (byte item : source)
        {
            this.add(item);
        }
        return this.size() != oldSize;
    }

    @Override
    public boolean addAll(ByteIterable source)
    {
        if (source.isEmpty())
        {
            return false;
        }
        int oldSize = this.size();

        if (source instanceof ByteHashSet)
        {
            ByteHashSet hashSet = (ByteHashSet) source;

            this.size = 0;
            this.bitGroup3 |= hashSet.bitGroup3;
            this.size += Long.bitCount(this.bitGroup3);

            this.bitGroup4 |= hashSet.bitGroup4;
            this.size += Long.bitCount(this.bitGroup4);

            this.bitGroup2 |= hashSet.bitGroup2;
            this.size += Long.bitCount(this.bitGroup2);

            this.bitGroup1 |= hashSet.bitGroup1;
            this.size += Long.bitCount(this.bitGroup1);
        }
        else
        {
            ByteIterator iterator = source.byteIterator();
            while (iterator.hasNext())
            {
                byte item = iterator.next();
                this.add(item);
            }
        }
        return this.size() != oldSize;
    }

    @Override
    public boolean removeAll(ByteIterable source)
    {
        if (source.isEmpty())
        {
            return false;
        }
        int oldSize = this.size();
        if (source instanceof ByteHashSet)
        {
            this.size = 0;
            ByteHashSet hashSet = (ByteHashSet) source;
            this.bitGroup3 &= ~hashSet.bitGroup3;
            this.size += Long.bitCount(this.bitGroup3);

            this.bitGroup4 &= ~hashSet.bitGroup4;
            this.size += Long.bitCount(this.bitGroup4);

            this.bitGroup2 &= ~hashSet.bitGroup2;
            this.size += Long.bitCount(this.bitGroup2);

            this.bitGroup1 &= ~hashSet.bitGroup1;
            this.size += Long.bitCount(this.bitGroup1);
        }
        else
        {
            ByteIterator iterator = source.byteIterator();
            while (iterator.hasNext())
            {
                byte item = iterator.next();
                this.remove(item);
            }
        }
        return this.size() != oldSize;
    }

    @Override
    public boolean removeAll(byte... source)
    {
        if (source.length == 0)
        {
            return false;
        }
        int oldSize = this.size();
        for (byte item : source)
        {
            this.remove(item);
        }
        return this.size() != oldSize;
    }

    @Override
    public boolean retainAll(ByteIterable source)
    {
        int oldSize = this.size();
        ByteSet sourceSet = source instanceof ByteSet ? (ByteSet) source : source.toSet();

        ByteHashSet retained = this.select(sourceSet::contains);
        if (retained.size() != oldSize)
        {
            this.bitGroup3 = retained.bitGroup3;
            this.bitGroup4 = retained.bitGroup4;
            this.bitGroup1 = retained.bitGroup1;
            this.bitGroup2 = retained.bitGroup2;
            this.size = retained.size;
            return true;
        }

        return false;
    }

    @Override
    public boolean retainAll(byte... source)
    {
        return this.retainAll(ByteHashSet.newSetWith(source));
    }

    @Override
    public void clear()
    {
        this.size = 0;
        this.bitGroup3 = 0L;
        this.bitGroup4 = 0L;
        this.bitGroup1 = 0L;
        this.bitGroup2 = 0L;
    }

    @Override
    public ByteHashSet with(byte element)
    {
        this.add(element);
        return this;
    }

    @Override
    public ByteHashSet without(byte element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public ByteHashSet withAll(ByteIterable elements)
    {
        this.addAll(elements.toArray());
        return this;
    }

    @Override
    public ByteHashSet withoutAll(ByteIterable elements)
    {
        this.removeAll(elements);
        return this;
    }

    @Override
    public MutableByteSet asUnmodifiable()
    {
        return new UnmodifiableByteSet(this);
    }

    @Override
    public MutableByteSet asSynchronized()
    {
        return new SynchronizedByteSet(this);
    }

    @Override
    public ImmutableByteSet toImmutable()
    {
        if (this.size() == 0)
        {
            return ByteSets.immutable.with();
        }
        if (this.size() == 1)
        {
            return ByteSets.immutable.with(this.byteIterator().next());
        }
        ByteHashSet mutableSet = ByteHashSet.newSetWith(this.toArray());
        return new ImmutableByteHashSet(mutableSet.bitGroup3, mutableSet.bitGroup4,
                mutableSet.bitGroup1, mutableSet.bitGroup2, mutableSet.size);
    }

    /**
     * @since 9.2.
     */
    @Override
    public ByteHashSet newEmpty()
    {
        return new ByteHashSet();
    }

    @Override
    public MutableByteIterator byteIterator()
    {
        return new MutableInternalByteIterator();
    }

    @Override
    public byte[] toArray()
    {
        byte[] array = new byte[this.size()];
        int index = 0;

        ByteIterator iterator = this.byteIterator();

        while (iterator.hasNext())
        {
            byte nextByte = iterator.next();
            array[index] = nextByte;
            index++;
        }

        return array;
    }

    @Override
    public byte[] toArray(byte[] array)
    {
        if (array.length < this.size())
        {
            array = new byte[this.size()];
        }
        int index = 0;

        ByteIterator iterator = this.byteIterator();

        while (iterator.hasNext())
        {
            byte nextByte = iterator.next();
            array[index] = nextByte;
            index++;
        }

        return array;
    }

    @Override
    public boolean containsAll(byte... source)
    {
        for (byte item : source)
        {
            if (!this.contains(item))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(ByteIterable source)
    {
        for (ByteIterator iterator = source.byteIterator(); iterator.hasNext(); )
        {
            if (!this.contains(iterator.next()))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void forEach(ByteProcedure procedure)
    {
        this.each(procedure);
    }

    /**
     * @since 7.0.
     */
    @Override
    public void each(ByteProcedure procedure)
    {
        long bitGroup1 = this.bitGroup1;
        while (bitGroup1 != 0L)
        {
            byte value = (byte) Long.numberOfTrailingZeros(bitGroup1);
            procedure.value((byte) ((value + 65) * -1));
            bitGroup1 &= ~(1L << (byte) (value + 64));
        }

        long bitGroup2 = this.bitGroup2;
        while (bitGroup2 != 0L)
        {
            byte value = (byte) Long.numberOfTrailingZeros(bitGroup2);
            procedure.value((byte) ((value + 1) * -1));
            bitGroup2 &= ~(1L << value);
        }

        long bitGroup3 = this.bitGroup3;
        while (bitGroup3 != 0L)
        {
            byte value = (byte) Long.numberOfTrailingZeros(bitGroup3);
            procedure.value(value);
            bitGroup3 &= ~(1L << value);
        }

        long bitGroup4 = this.bitGroup4;
        while (bitGroup4 != 0L)
        {
            byte value = (byte) Long.numberOfTrailingZeros(bitGroup4);
            procedure.value((byte) (value + 64));
            bitGroup4 &= ~(1L << (byte) (value + 64));
        }
    }

    @Override
    public ByteHashSet select(BytePredicate predicate)
    {
        ByteHashSet result = new ByteHashSet();

        this.forEach(value -> {
            if (predicate.accept(value))
            {
                result.add(value);
            }
        });

        return result;
    }

    @Override
    public MutableByteSet reject(BytePredicate predicate)
    {
        MutableByteSet result = new ByteHashSet();

        this.forEach(value -> {
            if (!predicate.accept(value))
            {
                result.add(value);
            }
        });

        return result;
    }

    @Override
    public <V> MutableSet<V> collect(ByteToObjectFunction<? extends V> function)
    {
        MutableSet<V> target = Sets.mutable.withInitialCapacity(this.size());

        this.forEach(each -> target.add(function.valueOf(each)));

        return target;
    }

    @Override
    public byte detectIfNone(BytePredicate predicate, byte ifNone)
    {
        ByteIterator iterator = this.byteIterator();

        while (iterator.hasNext())
        {
            byte nextByte = iterator.next();

            if (predicate.accept(nextByte))
            {
                return nextByte;
            }
        }

        return ifNone;
    }

    @Override
    public int count(BytePredicate predicate)
    {
        int count = 0;
        ByteIterator iterator = this.byteIterator();

        while (iterator.hasNext())
        {
            if (predicate.accept(iterator.next()))
            {
                count++;
            }
        }

        return count;
    }

    @Override
    public boolean anySatisfy(BytePredicate predicate)
    {
        ByteIterator iterator = this.byteIterator();

        while (iterator.hasNext())
        {
            if (predicate.accept(iterator.next()))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean allSatisfy(BytePredicate predicate)
    {
        ByteIterator iterator = this.byteIterator();

        while (iterator.hasNext())
        {
            if (!predicate.accept(iterator.next()))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean noneSatisfy(BytePredicate predicate)
    {
        ByteIterator iterator = this.byteIterator();

        while (iterator.hasNext())
        {
            if (predicate.accept(iterator.next()))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public MutableByteList toList()
    {
        return ByteLists.mutable.withAll(this);
    }

    @Override
    public MutableByteSet toSet()
    {
        return ByteSets.mutable.withAll(this);
    }

    @Override
    public MutableByteBag toBag()
    {
        return ByteBags.mutable.withAll(this);
    }

    @Override
    public LazyByteIterable asLazy()
    {
        return new LazyByteIterableAdapter(this);
    }

    @Override
    public long sum()
    {
        long result = 0L;

        ByteIterator iterator = this.byteIterator();

        while (iterator.hasNext())
        {
            result += iterator.next();
        }

        return result;
    }

    @Override
    public byte max()
    {
        if (this.isEmpty())
        {
            throw new NoSuchElementException();
        }
        byte max = 0;

        if (this.bitGroup4 != 0L)
        {
            //the highest has to be from this
            max = (byte) (127 - Long.numberOfLeadingZeros(this.bitGroup4));
        }
        else if (this.bitGroup3 != 0L)
        {
            max = (byte) (63 - Long.numberOfLeadingZeros(this.bitGroup3));
        }
        else if (this.bitGroup2 != 0L)
        {
            max = (byte) ((Long.numberOfTrailingZeros(this.bitGroup2) + 1) * -1);
        }
        else if (this.bitGroup1 != 0L)
        {
            max = (byte) ((Long.numberOfTrailingZeros(this.bitGroup1) + 65) * -1);
        }

        return max;
    }

    @Override
    public byte maxIfEmpty(byte defaultValue)
    {
        if (this.isEmpty())
        {
            return defaultValue;
        }
        return this.max();
    }

    @Override
    public byte min()
    {
        if (this.isEmpty())
        {
            throw new NoSuchElementException();
        }

        byte min = 0;

        if (this.bitGroup1 != 0L)
        {
            //the minimum has to be from this
            min = (byte) (128 - Long.numberOfLeadingZeros(this.bitGroup1));
            min *= -1;
        }
        else if (this.bitGroup2 != 0L)
        {
            min = (byte) ((64 - Long.numberOfLeadingZeros(this.bitGroup2)) * -1);
        }
        else if (this.bitGroup3 != 0L)
        {
            min = (byte) Long.numberOfTrailingZeros(this.bitGroup3);
        }
        else if (this.bitGroup4 != 0L)
        {
            min = (byte) (Long.numberOfTrailingZeros(this.bitGroup4) + 64);
        }

        return min;
    }

    @Override
    public byte minIfEmpty(byte defaultValue)
    {
        if (this.isEmpty())
        {
            return defaultValue;
        }
        return this.min();
    }

    @Override
    public double average()
    {
        if (this.isEmpty())
        {
            throw new ArithmeticException();
        }
        return (double) this.sum() / (double) this.size();
    }

    @Override
    public double median()
    {
        if (this.isEmpty())
        {
            throw new ArithmeticException();
        }
        byte[] sortedArray = this.toSortedArray();
        int middleIndex = sortedArray.length >> 1;
        if (sortedArray.length > 1 && (sortedArray.length & 1) == 0)
        {
            byte first = sortedArray[middleIndex];
            byte second = sortedArray[middleIndex - 1];
            return ((double) first + (double) second) / 2.0;
        }
        return (double) sortedArray[middleIndex];
    }

    @Override
    public byte[] toSortedArray()
    {
        byte[] array = this.toArray();
        Arrays.sort(array);
        return array;
    }

    @Override
    public MutableByteList toSortedList()
    {
        return ByteLists.mutable.withAll(this).sortThis();
    }

    @Override
    public ByteSet freeze()
    {
        if (this.size() == 0)
        {
            return ByteSets.immutable.with();
        }
        if (this.size() == 1)
        {
            return ByteSets.immutable.with(this.byteIterator().next());
        }

        return new ImmutableByteHashSet(this.bitGroup3, this.bitGroup4,
                this.bitGroup1, this.bitGroup2, this.size);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.size());

        this.forEach(new CheckedByteProcedure()
        {
            @Override
            public void safeValue(byte each) throws IOException
            {
                out.writeByte(each);
            }
        });
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException
    {
        int size = in.readInt();

        for (int i = 0; i < size; i++)
        {
            this.add(in.readByte());
        }
    }

    @Override
    public <T> T injectInto(T injectedValue, ObjectByteToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;

        ByteIterator iterator = this.byteIterator();

        while (iterator.hasNext())
        {
            result = function.valueOf(result, iterator.next());
        }

        return result;
    }

    @Override
    public RichIterable<ByteIterable> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }
        MutableList<ByteIterable> result = Lists.mutable.empty();
        if (this.notEmpty())
        {
            if (this.size() <= size)
            {
                result.add(ByteSets.mutable.withAll(this));
            }
            else
            {
                ByteIterator iterator = this.byteIterator();
                while (iterator.hasNext())
                {
                    MutableByteSet batch = ByteSets.mutable.empty();
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

    private static final class ImmutableByteHashSet implements ImmutableByteSet, Serializable
    {
        private static final long serialVersionUID = 1L;
        private final long bitGroup1; // -128 to -65
        private final long bitGroup2; //-64 to -1
        private final long bitGroup3; //0 to 63
        private final long bitGroup4; // 64 to 127
        private final short size;

        private ImmutableByteHashSet(
                long bitGroup3,
                long bitGroup4,
                long bitGroup1,
                long bitGroup2,
                short size)
        {
            this.bitGroup3 = bitGroup3;
            this.bitGroup4 = bitGroup4;
            this.bitGroup1 = bitGroup1;
            this.bitGroup2 = bitGroup2;
            this.size = size;
        }

        @Override
        public LazyIterable<ByteBytePair> cartesianProduct(ByteSet set)
        {
            return ByteSets.cartesianProduct(this, set);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }

            if (!(obj instanceof ByteSet))
            {
                return false;
            }

            ByteSet other = (ByteSet) obj;
            return this.size() == other.size() && this.containsAll(other.toArray());
        }

        @Override
        public int hashCode()
        {
            return (int) this.sum();
        }

        @Override
        public String toString()
        {
            return this.makeString("[", ", ", "]");
        }

        @Override
        public ImmutableByteSet newWith(byte element)
        {
            return ByteHashSet.newSet(this).with(element).toImmutable();
        }

        @Override
        public ImmutableByteSet newWithout(byte element)
        {
            return ByteHashSet.newSet(this).without(element).toImmutable();
        }

        @Override
        public ImmutableByteSet newWithAll(ByteIterable elements)
        {
            return ByteHashSet.newSet(this).withAll(elements).toImmutable();
        }

        @Override
        public ImmutableByteSet newWithoutAll(ByteIterable elements)
        {
            return ByteHashSet.newSet(this).withoutAll(elements).toImmutable();
        }

        @Override
        public int size()
        {
            return this.size;
        }

        @Override
        public boolean isEmpty()
        {
            return this.size() == 0;
        }

        @Override
        public boolean notEmpty()
        {
            return this.size() != 0;
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
        public void appendString(Appendable appendable, String start, String separator, String end)
        {
            try
            {
                appendable.append(start);
                int count = 0;
                ByteIterator iterator = this.byteIterator();

                while (iterator.hasNext())
                {
                    byte nextByte = iterator.next();

                    if (count > 0)
                    {
                        appendable.append(separator);
                    }

                    count++;
                    appendable.append(String.valueOf(nextByte));
                }

                appendable.append(end);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public ByteIterator byteIterator()
        {
            return new InternalByteIterator();
        }

        @Override
        public byte[] toArray()
        {
            byte[] array = new byte[this.size()];
            int index = 0;

            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                byte nextByte = iterator.next();
                array[index] = nextByte;
                index++;
            }

            return array;
        }

        @Override
        public byte[] toArray(byte[] array)
        {
            if (array.length < this.size())
            {
                array = new byte[this.size()];
            }
            int index = 0;

            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                byte nextByte = iterator.next();
                array[index] = nextByte;
                index++;
            }

            return array;
        }

        @Override
        public boolean contains(byte value)
        {
            if (value <= MAX_BYTE_GROUP_1)
            {
                return ((this.bitGroup1 >>> (byte) ((value + 1) * -1)) & 1L) != 0;
            }
            if (value <= MAX_BYTE_GROUP_2)
            {
                return ((this.bitGroup2 >>> (byte) ((value + 1) * -1)) & 1L) != 0;
            }
            if (value <= MAX_BYTE_GROUP_3)
            {
                return ((this.bitGroup3 >>> value) & 1L) != 0;
            }

            return ((this.bitGroup4 >>> value) & 1L) != 0;
        }

        @Override
        public boolean containsAll(byte... source)
        {
            for (byte item : source)
            {
                if (!this.contains(item))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean containsAll(ByteIterable source)
        {
            for (ByteIterator iterator = source.byteIterator(); iterator.hasNext(); )
            {
                if (!this.contains(iterator.next()))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void forEach(ByteProcedure procedure)
        {
            this.each(procedure);
        }

        @Override
        public void each(ByteProcedure procedure)
        {
            long bitGroup1 = this.bitGroup1;
            while (bitGroup1 != 0L)
            {
                byte value = (byte) Long.numberOfTrailingZeros(bitGroup1);
                procedure.value((byte) ((value + 65) * -1));
                bitGroup1 &= ~(1L << (byte) (value + 64));
            }

            long bitGroup2 = this.bitGroup2;
            while (bitGroup2 != 0L)
            {
                byte value = (byte) Long.numberOfTrailingZeros(bitGroup2);
                procedure.value((byte) ((value + 1) * -1));
                bitGroup2 &= ~(1L << value);
            }

            long bitGroup3 = this.bitGroup3;
            while (bitGroup3 != 0L)
            {
                byte value = (byte) Long.numberOfTrailingZeros(bitGroup3);
                procedure.value(value);
                bitGroup3 &= ~(1L << value);
            }

            long bitGroup4 = this.bitGroup4;
            while (bitGroup4 != 0L)
            {
                byte value = (byte) Long.numberOfTrailingZeros(bitGroup4);
                procedure.value((byte) (value + 64));
                bitGroup4 &= ~(1L << (byte) (value + 64));
            }
        }

        @Override
        public ImmutableByteSet select(BytePredicate predicate)
        {
            MutableByteSet result = new ByteHashSet();

            this.forEach(value -> {
                if (predicate.accept(value))
                {
                    result.add(value);
                }
            });

            return result.toImmutable();
        }

        @Override
        public ImmutableByteSet reject(BytePredicate predicate)
        {
            MutableByteSet result = new ByteHashSet();

            this.forEach(value -> {
                if (!predicate.accept(value))
                {
                    result.add(value);
                }
            });

            return result.toImmutable();
        }

        @Override
        public <V> ImmutableSet<V> collect(ByteToObjectFunction<? extends V> function)
        {
            MutableSet<V> target = Sets.mutable.withInitialCapacity(this.size());

            this.forEach(each -> target.add(function.valueOf(each)));

            return target.toImmutable();
        }

        @Override
        public byte detectIfNone(BytePredicate predicate, byte ifNone)
        {
            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                byte nextByte = iterator.next();

                if (predicate.accept(nextByte))
                {
                    return nextByte;
                }
            }

            return ifNone;
        }

        @Override
        public int count(BytePredicate predicate)
        {
            int count = 0;
            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                if (predicate.accept(iterator.next()))
                {
                    count++;
                }
            }

            return count;
        }

        @Override
        public boolean anySatisfy(BytePredicate predicate)
        {
            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                if (predicate.accept(iterator.next()))
                {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean allSatisfy(BytePredicate predicate)
        {
            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                if (!predicate.accept(iterator.next()))
                {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean noneSatisfy(BytePredicate predicate)
        {
            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                if (predicate.accept(iterator.next()))
                {
                    return false;
                }
            }

            return true;
        }

        @Override
        public MutableByteList toList()
        {
            return ByteLists.mutable.withAll(this);
        }

        @Override
        public MutableByteSet toSet()
        {
            return ByteSets.mutable.withAll(this);
        }

        @Override
        public MutableByteBag toBag()
        {
            return ByteBags.mutable.withAll(this);
        }

        @Override
        public LazyByteIterable asLazy()
        {
            return new LazyByteIterableAdapter(this);
        }

        @Override
        public long sum()
        {
            long result = 0L;

            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                result += iterator.next();
            }

            return result;
        }

        @Override
        public byte max()
        {
            byte max = 0;

            if (this.bitGroup4 != 0L)
            {
                //the highest has to be from this
                max = (byte) (127 - Long.numberOfLeadingZeros(this.bitGroup4));
            }
            else if (this.bitGroup3 != 0L)
            {
                max = (byte) (63 - Long.numberOfLeadingZeros(this.bitGroup3));
            }
            else if (this.bitGroup2 != 0L)
            {
                max = (byte) ((Long.numberOfTrailingZeros(this.bitGroup2) + 1) * -1);
            }
            else if (this.bitGroup1 != 0L)
            {
                max = (byte) ((Long.numberOfTrailingZeros(this.bitGroup1) + 65) * -1);
            }

            return max;
        }

        @Override
        public byte maxIfEmpty(byte defaultValue)
        {
            return this.max();
        }

        @Override
        public byte min()
        {
            byte min = 0;

            if (this.bitGroup1 != 0L)
            {
                //the minimum has to be from this
                min = (byte) (128 - Long.numberOfLeadingZeros(this.bitGroup1));
                min *= -1;
            }
            else if (this.bitGroup2 != 0L)
            {
                min = (byte) ((64 - Long.numberOfLeadingZeros(this.bitGroup2)) * -1);
            }
            else if (this.bitGroup3 != 0L)
            {
                min = (byte) Long.numberOfTrailingZeros(this.bitGroup3);
            }
            else if (this.bitGroup4 != 0L)
            {
                min = (byte) (Long.numberOfTrailingZeros(this.bitGroup4) + 64);
            }

            return min;
        }

        @Override
        public byte minIfEmpty(byte defaultValue)
        {
            return this.min();
        }

        @Override
        public double average()
        {
            return (double) this.sum() / (double) this.size();
        }

        @Override
        public double median()
        {
            byte[] sortedArray = this.toSortedArray();
            int middleIndex = sortedArray.length >> 1;
            if (sortedArray.length > 1 && (sortedArray.length & 1) == 0)
            {
                byte first = sortedArray[middleIndex];
                byte second = sortedArray[middleIndex - 1];
                return ((double) first + (double) second) / 2.0;
            }
            return (double) sortedArray[middleIndex];
        }

        @Override
        public byte[] toSortedArray()
        {
            byte[] array = this.toArray();
            Arrays.sort(array);
            return array;
        }

        @Override
        public MutableByteList toSortedList()
        {
            return ByteLists.mutable.withAll(this).sortThis();
        }

        @Override
        public <T> T injectInto(T injectedValue, ObjectByteToObjectFunction<? super T, ? extends T> function)
        {
            T result = injectedValue;

            ByteIterator iterator = this.byteIterator();

            while (iterator.hasNext())
            {
                result = function.valueOf(result, iterator.next());
            }

            return result;
        }

        @Override
        public RichIterable<ByteIterable> chunk(int size)
        {
            if (size <= 0)
            {
                throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
            }
            if (this.isEmpty())
            {
                return Lists.mutable.empty();
            }

            ByteIterator iterator = this.byteIterator();
            MutableList<ByteIterable> result = Lists.mutable.empty();
            while (iterator.hasNext())
            {
                MutableByteSet batch = ByteSets.mutable.empty();
                for (int i = 0; i < size && iterator.hasNext(); i++)
                {
                    batch.add(iterator.next());
                }
                result.add(batch);
            }
            return result;
        }

        @Override
        public ByteSet freeze()
        {
            return this;
        }

        @Override
        public ImmutableByteSet toImmutable()
        {
            return this;
        }

        private Object writeReplace()
        {
            return new ImmutableByteSetSerializationProxy(this);
        }

        private class InternalByteIterator implements ByteIterator
        {
            private int count;
            private byte minusOneTwentyEightToPlusOneTwentySeven = -128;

            @Override
            public boolean hasNext()
            {
                return this.count < ImmutableByteHashSet.this.size();
            }

            @Override
            public byte next()
            {
                if (!this.hasNext())
                {
                    throw new NoSuchElementException("next() called, but the iterator is exhausted");
                }
                this.count++;

                while (this.minusOneTwentyEightToPlusOneTwentySeven <= 127)
                {
                    if (ImmutableByteHashSet.this.contains(this.minusOneTwentyEightToPlusOneTwentySeven))
                    {
                        byte result = this.minusOneTwentyEightToPlusOneTwentySeven;
                        this.minusOneTwentyEightToPlusOneTwentySeven++;
                        return result;
                    }
                    this.minusOneTwentyEightToPlusOneTwentySeven++;
                }

                throw new NoSuchElementException("no more element, unexpected situation");
            }
        }
    }

    private class MutableInternalByteIterator implements MutableByteIterator
    {
        private int count;
        private byte minusOneTwentyEightToPlusOneTwentySeven = -128;

        @Override
        public boolean hasNext()
        {
            return this.count < ByteHashSet.this.size();
        }

        @Override
        public byte next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException("next() called, but the iterator is exhausted");
            }

            this.count++;

            while (this.minusOneTwentyEightToPlusOneTwentySeven <= 127)
            {
                if (ByteHashSet.this.contains(this.minusOneTwentyEightToPlusOneTwentySeven))
                {
                    byte result = this.minusOneTwentyEightToPlusOneTwentySeven;
                    this.minusOneTwentyEightToPlusOneTwentySeven++;
                    return result;
                }
                this.minusOneTwentyEightToPlusOneTwentySeven++;
            }

            throw new NoSuchElementException("no more element, unexpected situation");
        }

        @Override
        public void remove()
        {
            if (this.count == 0 || !ByteHashSet.this.remove((byte) (this.minusOneTwentyEightToPlusOneTwentySeven - 1)))
            {
                throw new IllegalStateException();
            }
            this.count--;
        }
    }
}
