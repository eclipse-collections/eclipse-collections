/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.stack.immutable;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Stacks;
import org.eclipse.collections.api.factory.primitive.BooleanStacks;
import org.eclipse.collections.api.factory.primitive.ByteStacks;
import org.eclipse.collections.api.factory.primitive.CharStacks;
import org.eclipse.collections.api.factory.primitive.DoubleStacks;
import org.eclipse.collections.api.factory.primitive.FloatStacks;
import org.eclipse.collections.api.factory.primitive.IntStacks;
import org.eclipse.collections.api.factory.primitive.LongStacks;
import org.eclipse.collections.api.factory.primitive.ShortStacks;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.stack.PartitionImmutableStack;
import org.eclipse.collections.api.stack.ImmutableStack;
import org.eclipse.collections.api.stack.StackIterable;
import org.eclipse.collections.api.stack.primitive.ImmutableBooleanStack;
import org.eclipse.collections.api.stack.primitive.ImmutableByteStack;
import org.eclipse.collections.api.stack.primitive.ImmutableCharStack;
import org.eclipse.collections.api.stack.primitive.ImmutableDoubleStack;
import org.eclipse.collections.api.stack.primitive.ImmutableFloatStack;
import org.eclipse.collections.api.stack.primitive.ImmutableIntStack;
import org.eclipse.collections.api.stack.primitive.ImmutableLongStack;
import org.eclipse.collections.api.stack.primitive.ImmutableShortStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.AbstractRichIterable;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.lazy.ReverseIterable;
import org.eclipse.collections.impl.partition.stack.PartitionArrayStack;
import org.eclipse.collections.impl.partition.stack.PartitionArrayStack.PartitionPredicate2Procedure;
import org.eclipse.collections.impl.partition.stack.PartitionArrayStack.PartitionProcedure;
import org.eclipse.collections.impl.tuple.Tuples;

final class ImmutableNotEmptyStack<T>
        extends AbstractRichIterable<T>
        implements ImmutableStack<T>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final T element;
    private final ImmutableStack<T> next;
    private final int size;

    ImmutableNotEmptyStack(T element, ImmutableStack<T> next)
    {
        this.element = element;
        this.next = Objects.requireNonNull(next);
        this.size = next.size() + 1;
    }

    @Override
    public ImmutableStack<T> push(T item)
    {
        return new ImmutableNotEmptyStack<>(item, this);
    }

    @Override
    public ImmutableStack<T> pop()
    {
        return this.next;
    }

    @Override
    public ImmutableStack<T> pop(int count)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return this;
        }
        this.checkSizeLessThanCount(count);

        ImmutableStack<T> pointer = this;
        for (int i = 0; i < count; i++)
        {
            pointer = pointer.pop();
        }
        return pointer;
    }

    @Override
    public Pair<T, ImmutableStack<T>> peekAndPop()
    {
        return Tuples.pair(this.peek(), this.pop());
    }

    @Override
    public Pair<ListIterable<T>, ImmutableStack<T>> peekAndPop(int count)
    {
        return Tuples.pair(this.peek(count), this.pop(count));
    }

    @Override
    public T peek()
    {
        return this.element;
    }

    @Override
    public ListIterable<T> peek(int count)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return Lists.mutable.empty();
        }
        this.checkSizeLessThanCount(count);
        return this.asLazy().take(count).toList();
    }

    private boolean checkZeroCount(int count)
    {
        return count == 0;
    }

    private void checkSizeLessThanCount(int count)
    {
        if (this.size() < count)
        {
            throw new IllegalArgumentException("Count must be less than size: Count = " + count + " Size = " + this.size());
        }
    }

    private void checkSizeLessThanOrEqualToIndex(int index)
    {
        if (this.size() <= index)
        {
            throw new IllegalArgumentException("Count must be less than size: Count = " + index + " Size = " + this.size());
        }
    }

    private void checkNegativeCount(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be positive but was " + count);
        }
    }

    @Override
    public T peekAt(int index)
    {
        this.checkNegativeCount(index);
        this.checkSizeLessThanOrEqualToIndex(index);
        return this.pop(index).peek();
    }

    @Override
    public T getFirst()
    {
        return this.peek();
    }

    @Override
    public T getLast()
    {
        return this.peekAt(this.size() - 1);
    }

    @Override
    public ImmutableStack<T> select(Predicate<? super T> predicate)
    {
        return Stacks.immutable.withAllReversed(this.asLazy().select(predicate).toList());
    }

    @Override
    public <P> ImmutableStack<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public ImmutableStack<T> reject(Predicate<? super T> predicate)
    {
        return Stacks.immutable.withAllReversed(this.asLazy().reject(predicate).toList());
    }

    @Override
    public <P> ImmutableStack<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public PartitionImmutableStack<T> partition(Predicate<? super T> predicate)
    {
        PartitionArrayStack<T> partitionMutableStack = new PartitionArrayStack<>();
        this.asLazy().forEach(new PartitionProcedure<>(predicate, partitionMutableStack));
        return partitionMutableStack.toImmutable();
    }

    @Override
    public <P> PartitionImmutableStack<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionArrayStack<T> partitionMutableStack = new PartitionArrayStack<>();
        this.asLazy().forEach(new PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableStack));
        return partitionMutableStack.toImmutable();
    }

    @Override
    public <S> ImmutableStack<S> selectInstancesOf(Class<S> clazz)
    {
        return Stacks.immutable.withAllReversed(this.asLazy().selectInstancesOf(clazz).toList());
    }

    @Override
    public <V> ImmutableStack<V> collect(Function<? super T, ? extends V> function)
    {
        return Stacks.immutable.withAllReversed(this.asLazy().collect(function).toList());
    }

    @Override
    public ImmutableBooleanStack collectBoolean(BooleanFunction<? super T> function)
    {
        return BooleanStacks.immutable.withAllReversed(this.asLazy().collectBoolean(function));
    }

    @Override
    public ImmutableByteStack collectByte(ByteFunction<? super T> function)
    {
        return ByteStacks.immutable.withAllReversed(this.asLazy().collectByte(function));
    }

    @Override
    public ImmutableCharStack collectChar(CharFunction<? super T> function)
    {
        return CharStacks.immutable.withAllReversed(this.asLazy().collectChar(function));
    }

    @Override
    public ImmutableDoubleStack collectDouble(DoubleFunction<? super T> function)
    {
        return DoubleStacks.immutable.withAllReversed(this.asLazy().collectDouble(function));
    }

    @Override
    public ImmutableFloatStack collectFloat(FloatFunction<? super T> function)
    {
        return FloatStacks.immutable.withAllReversed(this.asLazy().collectFloat(function));
    }

    @Override
    public ImmutableIntStack collectInt(IntFunction<? super T> function)
    {
        return IntStacks.immutable.withAllReversed(this.asLazy().collectInt(function));
    }

    @Override
    public ImmutableLongStack collectLong(LongFunction<? super T> function)
    {
        return LongStacks.immutable.withAllReversed(this.asLazy().collectLong(function));
    }

    @Override
    public ImmutableShortStack collectShort(ShortFunction<? super T> function)
    {
        return ShortStacks.immutable.withAllReversed(this.asLazy().collectShort(function));
    }

    @Override
    public <P, V> ImmutableStack<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return Stacks.immutable.withAllReversed(this.asLazy().collectWith(function, parameter).toList());
    }

    @Override
    public <V> ImmutableStack<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return Stacks.immutable.withAllReversed(this.asLazy().collectIf(predicate, function).toList());
    }

    @Override
    public <V> ImmutableStack<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return Stacks.immutable.withAllReversed(this.asLazy().flatCollect(function).toList());
    }

    @Override
    public <V> ImmutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        ObjectLongMap<V> map = this.asLazy().sumByInt(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V> ImmutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        ObjectDoubleMap<V> map = this.asLazy().sumByFloat(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V> ImmutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        ObjectLongMap<V> map = this.asLazy().sumByLong(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V> ImmutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        ObjectDoubleMap<V> map = this.asLazy().sumByDouble(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V> ImmutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, Multimaps.mutable.list.<V, T>empty()).toImmutable();
    }

    @Override
    public <V> ImmutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, Multimaps.mutable.list.empty()).toImmutable();
    }

    @Override
    public <V> ImmutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.groupByUniqueKey(function, Maps.mutable.<V, T>withInitialCapacity(this.size)).toImmutable();
    }

    @Override
    public <S> ImmutableStack<Pair<T, S>> zip(Iterable<S> that)
    {
        return Stacks.immutable.withAllReversed(this.asLazy().zip(that).toList());
    }

    @Override
    public ImmutableStack<Pair<T, Integer>> zipWithIndex()
    {
        return Stacks.immutable.withAllReversed(this.asLazy().zipWithIndex().toList());
    }

    @Override
    public ImmutableStack<T> toImmutable()
    {
        return this;
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        return this.asLazy().chunk(size);
    }

    @Override
    public <V> ImmutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.countByEach(function, Bags.mutable.empty()).toImmutable();
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean notEmpty()
    {
        return true;
    }

    @Override
    public ImmutableStack<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        ImmutableStack<T> pointer = this;
        while (pointer.notEmpty())
        {
            procedure.accept(pointer.peek());
            pointer = pointer.pop();
        }
    }

    @Override
    public ImmutableStack<T> takeWhile(Predicate<? super T> predicate)
    {
        return Stacks.immutable.withAll(this.asReversed().takeWhile(predicate));
    }

    public ReverseIterable<T> asReversed()
    {
        return ReverseIterable.adapt(this.toList());
    }

    @Override
    public ImmutableStack<T> dropWhile(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".dropWhile() not implemented yet");
    }

    @Override
    public PartitionImmutableStack<T> partitionWhile(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".partitionWhile() not implemented yet");
    }

    @Override
    public ImmutableStack<T> distinct()
    {
        return Stacks.immutable.withAllReversed(this.asLazy().distinct().toList());
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".corresponds() not implemented yet");
    }

    public boolean hasSameElements(OrderedIterable<T> other)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".hasSameElements() not implemented yet");
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEach() not implemented yet");
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEachWithIndex() not implemented yet");
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectIndex() not implemented yet");
    }

    @Override
    public Iterator<T> iterator()
    {
        return new ImmutableStackIterator<>(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof StackIterable<?>))
        {
            return false;
        }

        StackIterable<?> that = (StackIterable<?>) o;

        Iterator<T> thisIterator = this.iterator();
        Iterator<?> thatIterator = that.iterator();
        while (thisIterator.hasNext() && thatIterator.hasNext())
        {
            if (!Comparators.nullSafeEquals(thisIterator.next(), thatIterator.next()))
            {
                return false;
            }
        }
        return !thisIterator.hasNext() && !thatIterator.hasNext();
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (T each : this)
        {
            hashCode = 31 * hashCode + (each == null ? 0 : each.hashCode());
        }
        return hashCode;
    }

    private Object writeReplace()
    {
        return new ImmutableStackSerializationProxy<>(this);
    }
}
