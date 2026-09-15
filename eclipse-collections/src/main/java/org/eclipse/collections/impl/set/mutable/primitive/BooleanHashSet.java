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
import java.util.NoSuchElementException;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.LazyBooleanIterable;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.block.function.primitive.BooleanToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectBooleanToObjectFunction;
import org.eclipse.collections.api.block.predicate.primitive.BooleanPredicate;
import org.eclipse.collections.api.block.procedure.primitive.BooleanProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.BooleanBags;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.api.iterator.MutableBooleanIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.BooleanSet;
import org.eclipse.collections.api.set.primitive.ImmutableBooleanSet;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.tuple.primitive.BooleanBooleanPair;
import org.eclipse.collections.impl.block.factory.primitive.BooleanPredicates;
import org.eclipse.collections.impl.factory.primitive.BooleanSets;
import org.eclipse.collections.impl.lazy.primitive.LazyBooleanIterableAdapter;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public class BooleanHashSet implements MutableBooleanSet, Externalizable
{
    private static final long serialVersionUID = 1L;

    // state = 0 ==> []
    // state = 1 ==> [F]
    // state = 2 ==> [T]
    // state = 3 ==> [T, F]
    private int state;

    public BooleanHashSet()
    {
    }

    public BooleanHashSet(boolean... elements)
    {
        this();
        this.addAll(elements);
    }

    public BooleanHashSet(BooleanHashSet set)
    {
        this.state = set.state;
    }

    private static class EmptyBooleanIterator implements MutableBooleanIterator
    {
        @Override
        public boolean next()
        {
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasNext()
        {
            return false;
        }

        @Override
        public void remove()
        {
            throw new IllegalStateException();
        }
    }

    private class FalseBooleanIterator implements MutableBooleanIterator
    {
        private int currentIndex;

        @Override
        public boolean hasNext()
        {
            return this.currentIndex == 0;
        }

        @Override
        public boolean next()
        {
            if (this.currentIndex == 0)
            {
                this.currentIndex++;
                return false;
            }
            this.currentIndex = -1;
            throw new NoSuchElementException();
        }

        @Override
        public void remove()
        {
            if (this.currentIndex == 0 || this.currentIndex == -1)
            {
                throw new IllegalStateException();
            }
            this.currentIndex = -1;
            BooleanHashSet.this.remove(false);
        }
    }

    @Override
    public MutableSet<Boolean> boxed()
    {
        return new BoxedMutableBooleanSet(this);
    }

    private class TrueBooleanIterator implements MutableBooleanIterator
    {
        private int currentIndex;

        @Override
        public boolean hasNext()
        {
            return this.currentIndex == 0;
        }

        @Override
        public boolean next()
        {
            if (this.currentIndex == 0)
            {
                this.currentIndex++;
                return true;
            }
            this.currentIndex = -1;
            throw new NoSuchElementException();
        }

        @Override
        public void remove()
        {
            if (this.currentIndex == 0 || this.currentIndex == -1)
            {
                throw new IllegalStateException();
            }
            this.currentIndex = -1;
            BooleanHashSet.this.remove(true);
        }
    }

    private class FalseTrueBooleanIterator implements MutableBooleanIterator
    {
        private int currentIndex;

        @Override
        public boolean hasNext()
        {
            return this.currentIndex < 2;
        }

        @Override
        public boolean next()
        {
            switch (this.currentIndex)
            {
                case 0:
                    this.currentIndex++;
                    return false;
                case 1:
                    this.currentIndex++;
                    return true;
                default:
                    throw new NoSuchElementException();
            }
        }

        @Override
        public void remove()
        {
            switch (this.currentIndex)
            {
                case 0:
                    throw new IllegalStateException();
                case 1:
                    if (!BooleanHashSet.this.remove(false))
                    {
                        throw new IllegalStateException();
                    }
                    return;
                case 2:
                    if (!BooleanHashSet.this.remove(true))
                    {
                        throw new IllegalStateException();
                    }
                    return;
                default:
                    throw new AssertionError();
            }
        }
    }

    public static BooleanHashSet newSetWith(boolean... source)
    {
        return new BooleanHashSet(source);
    }

    public static BooleanHashSet newSet(BooleanIterable source)
    {
        if (source instanceof BooleanHashSet booleanHashSet)
        {
            return new BooleanHashSet(booleanHashSet);
        }

        return BooleanHashSet.newSetWith(source.toArray());
    }

    /**
     * @since 11.0.
     */
    @Override
    public BooleanHashSet newEmpty()
    {
        return new BooleanHashSet();
    }

    @Override
    public boolean add(boolean element)
    {
        if (this.contains(element))
        {
            return false;
        }
        this.state |= element ? 2 : 1;
        return true;
    }

    @Override
    public boolean addAll(boolean... source)
    {
        int initialState = this.state;
        for (boolean item : source)
        {
            if (this.state == 3)
            {
                return this.state != initialState;
            }
            this.add(item);
        }
        return this.state != initialState;
    }

    @Override
    public boolean addAll(BooleanIterable source)
    {
        return this.addAll(source.toArray());
    }

    @Override
    public boolean remove(boolean value)
    {
        if (!this.contains(value))
        {
            return false;
        }
        int initialState = this.state;
        this.state &= value ? ~2 : ~1;
        return initialState != this.state;
    }

    @Override
    public boolean removeAll(BooleanIterable source)
    {
        if (this.isEmpty() || source.isEmpty())
        {
            return false;
        }
        boolean modified = false;
        BooleanIterator iterator = source.booleanIterator();
        while (iterator.hasNext())
        {
            if (this.state == 0)
            {
                return modified;
            }
            boolean item = iterator.next();
            if (this.remove(item))
            {
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
        boolean modified = false;
        for (boolean item : source)
        {
            if (this.state == 0)
            {
                return modified;
            }
            if (this.remove(item))
            {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(BooleanIterable source)
    {
        if (this.state == 0
                || (source.contains(true) && source.contains(false))
                || (this.state == 2 && source.contains(true))
                || (this.state == 1 && source.contains(false)))
        {
            return false;
        }
        if (source.contains(true))
        {
            this.state -= 1;
        }
        else if (source.contains(false))
        {
            this.state -= 2;
        }
        else
        {
            this.state = 0;
        }
        return true;
    }

    @Override
    public boolean retainAll(boolean... source)
    {
        return this.retainAll(BooleanHashSet.newSetWith(source));
    }

    @Override
    public void clear()
    {
        this.state = 0;
    }

    @Override
    public MutableBooleanIterator booleanIterator()
    {
        switch (this.state)
        {
            case 0:
                return new EmptyBooleanIterator();
            case 1:
                return new FalseBooleanIterator();
            case 2:
                return new TrueBooleanIterator();
            case 3:
                return new FalseTrueBooleanIterator();
            default:
                throw new AssertionError("Invalid state");
        }
    }

    /**
     * @since 7.0.
     */
    @Override
    public void each(BooleanProcedure procedure)
    {
        switch (this.state)
        {
            case 0:
                return;
            case 1:
                procedure.value(false);
                return;
            case 2:
                procedure.value(true);
                return;
            case 3:
                procedure.value(false);
                procedure.value(true);
                return;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public <T> T injectInto(T injectedValue, ObjectBooleanToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        switch (this.state)
        {
            case 0:
                return result;
            case 1:
                result = function.valueOf(result, false);
                return result;
            case 2:
                result = function.valueOf(result, true);
                return result;
            case 3:
                result = function.valueOf(result, false);
                result = function.valueOf(result, true);
                return result;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public RichIterable<BooleanIterable> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }
        MutableList<BooleanIterable> result = Lists.mutable.empty();
        switch (this.state)
        {
            case 0:
                return result;
            case 1:
                result.add(BooleanSets.mutable.with(false));
                return result;
            case 2:
                result.add(BooleanSets.mutable.with(true));
                return result;
            case 3:
                if (size == 1)
                {
                    result.add(BooleanSets.mutable.with(false));
                    result.add(BooleanSets.mutable.with(true));
                }
                else
                {
                    result.add(BooleanSets.mutable.with(false, true));
                }
                return result;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public int count(BooleanPredicate predicate)
    {
        switch (this.state)
        {
            case 0:
                return 0;
            case 1:
                return predicate.accept(false) ? 1 : 0;
            case 2:
                return predicate.accept(true) ? 1 : 0;
            case 3:
                int count = 0;
                if (predicate.accept(false))
                {
                    count++;
                }
                if (predicate.accept(true))
                {
                    count++;
                }
                return count;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public boolean anySatisfy(BooleanPredicate predicate)
    {
        return this.count(predicate) > 0;
    }

    @Override
    public boolean allSatisfy(BooleanPredicate predicate)
    {
        return this.count(predicate) == this.size();
    }

    @Override
    public boolean noneSatisfy(BooleanPredicate predicate)
    {
        return this.count(predicate) == 0;
    }

    @Override
    public BooleanHashSet select(BooleanPredicate predicate)
    {
        BooleanHashSet set = new BooleanHashSet();
        switch (this.state)
        {
            case 0:
                return set;
            case 1:
                if (predicate.accept(false))
                {
                    set.add(false);
                }
                return set;
            case 2:
                if (predicate.accept(true))
                {
                    set.add(true);
                }
                return set;
            case 3:
                if (predicate.accept(false))
                {
                    set.add(false);
                }
                if (predicate.accept(true))
                {
                    set.add(true);
                }
                return set;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public BooleanHashSet reject(BooleanPredicate predicate)
    {
        return this.select(BooleanPredicates.not(predicate));
    }

    @Override
    public boolean detectIfNone(BooleanPredicate predicate, boolean ifNone)
    {
        switch (this.state)
        {
            case 0:
                return ifNone;
            case 1:
                return !predicate.accept(false) && ifNone;
            case 2:
                return predicate.accept(true) || ifNone;
            case 3:
                if (predicate.accept(false))
                {
                    return false;
                }
                if (predicate.accept(true))
                {
                    return true;
                }
                return ifNone;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public <V> MutableSet<V> collect(BooleanToObjectFunction<? extends V> function)
    {
        MutableSet<V> target = UnifiedSet.newSet(this.size());
        switch (this.state)
        {
            case 0:
                return target;
            case 1:
                return target.with(function.valueOf(false));
            case 2:
                return target.with(function.valueOf(true));
            case 3:
                target.add(function.valueOf(false));
                target.add(function.valueOf(true));
                return target;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public boolean[] toArray()
    {
        switch (this.state)
        {
            case 0:
                return new boolean[0];
            case 1:
                return new boolean[]{false};
            case 2:
                return new boolean[]{true};
            case 3:
                return new boolean[]{false, true};
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public boolean[] toArray(boolean[] target)
    {
        int requiredSize = 0;
        if (this.state == 1 || this.state == 2)
        {
            requiredSize = 1;
        }
        else if (this.state == 3)
        {
            requiredSize = 2;
        }

        if (target.length < requiredSize)
        {
            target = new boolean[requiredSize];
        }

        switch (this.state)
        {
            case 0:
                return target;
            case 1:
                target[0] = false;
                return target;
            case 2:
                target[0] = true;
                return target;
            case 3:
                target[0] = false;
                target[1] = true;
                return target;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public boolean contains(boolean value)
    {
        if (this.state == 3)
        {
            return true;
        }
        if (value)
        {
            return this.state == 2;
        }
        return this.state == 1;
    }

    @Override
    public boolean containsAll(boolean... source)
    {
        if (this.state == 3)
        {
            return true;
        }
        for (boolean item : source)
        {
            if (!this.contains(item))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(BooleanIterable source)
    {
        if (this.state == 3)
        {
            return true;
        }
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
    public BooleanHashSet with(boolean element)
    {
        if (this.state == 3)
        {
            return this;
        }
        this.add(element);
        return this;
    }

    @Override
    public BooleanHashSet without(boolean element)
    {
        if (this.state == 0)
        {
            return this;
        }
        this.remove(element);
        return this;
    }

    @Override
    public BooleanHashSet withAll(BooleanIterable elements)
    {
        if (this.state == 3)
        {
            return this;
        }
        this.addAll(elements.toArray());
        return this;
    }

    @Override
    public BooleanHashSet withoutAll(BooleanIterable elements)
    {
        if (this.state == 0)
        {
            return this;
        }
        this.removeAll(elements);
        return this;
    }

    @Override
    public MutableBooleanSet asUnmodifiable()
    {
        return new UnmodifiableBooleanSet(this);
    }

    @Override
    public MutableBooleanSet asSynchronized()
    {
        return new SynchronizedBooleanSet(this);
    }

    @Override
    public BooleanSet freeze()
    {
        return this.toImmutable();
    }

    @Override
    public ImmutableBooleanSet toImmutable()
    {
        switch (this.state)
        {
            case 0:
                return BooleanSets.immutable.with();
            case 1:
                return BooleanSets.immutable.with(false);
            case 2:
                return BooleanSets.immutable.with(true);
            case 3:
                return BooleanSets.immutable.with(false, true);
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public int size()
    {
        switch (this.state)
        {
            case 0:
                return 0;
            case 1:
            case 2:
                return 1;
            case 3:
                return 2;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public boolean isEmpty()
    {
        return this.state == 0;
    }

    @Override
    public boolean notEmpty()
    {
        return this.state != 0;
    }

    @Override
    public LazyIterable<BooleanBooleanPair> cartesianProduct(
            BooleanSet set)
    {
        return BooleanSets.cartesianProduct(this, set);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof BooleanSet))
        {
            return false;
        }

        BooleanSet other = (BooleanSet) obj;
        return this.contains(false) == other.contains(false) && this.contains(true) == other.contains(true);
    }

    @Override
    public int hashCode()
    {
        switch (this.state)
        {
            case 0:
                return 0;
            case 1:
                return 1237;
            case 2:
                return 1231;
            case 3:
                return 2468;
            default:
                throw new AssertionError("Invalid state");
        }
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
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try
        {
            appendable.append(start);
            switch (this.state)
            {
                case 0:
                    break;
                case 1:
                    appendable.append(String.valueOf(false));
                    break;
                case 2:
                    appendable.append(String.valueOf(true));
                    break;
                case 3:
                    appendable.append(String.valueOf(false));
                    appendable.append(separator);
                    appendable.append(String.valueOf(true));
                    break;
                default:
                    throw new AssertionError("Invalid state");
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
        return BooleanLists.mutable.withAll(this);
    }

    @Override
    public MutableBooleanSet toSet()
    {
        return BooleanSets.mutable.withAll(this);
    }

    @Override
    public MutableBooleanBag toBag()
    {
        return BooleanBags.mutable.withAll(this);
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
        switch (this.state)
        {
            case 0:
                return;
            case 1:
                out.writeBoolean(false);
                return;
            case 2:
                out.writeBoolean(true);
                return;
            case 3:
                out.writeBoolean(false);
                out.writeBoolean(true);
                return;
            default:
                throw new AssertionError("Invalid state");
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException
    {
        int size = in.readInt();
        for (int i = 0; i < size; i++)
        {
            this.add(in.readBoolean());
        }
    }
}
