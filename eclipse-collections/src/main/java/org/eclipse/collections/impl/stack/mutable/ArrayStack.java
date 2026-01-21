/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.stack.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.Lists;
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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.stack.PartitionMutableStack;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.stack.ImmutableStack;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.stack.StackIterable;
import org.eclipse.collections.api.stack.primitive.MutableBooleanStack;
import org.eclipse.collections.api.stack.primitive.MutableByteStack;
import org.eclipse.collections.api.stack.primitive.MutableCharStack;
import org.eclipse.collections.api.stack.primitive.MutableDoubleStack;
import org.eclipse.collections.api.stack.primitive.MutableFloatStack;
import org.eclipse.collections.api.stack.primitive.MutableIntStack;
import org.eclipse.collections.api.stack.primitive.MutableLongStack;
import org.eclipse.collections.api.stack.primitive.MutableShortStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.stack.PartitionArrayStack;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * ArrayStack is a MutableStack which contains a FastList of data. ArrayStack iterates from top to bottom (LIFO order).
 * It behaves like FastList in terms of runtime complexity. The method push() is amortized constant time like
 * FastList.add(). The backing data structure grows and shrinks by 50% at a time, and size is constant. ArrayStack does
 * not extend Vector, as does the Java Stack, which was one of the reasons to create this data structure.
 */
public class ArrayStack<T> implements MutableStack<T>, Externalizable
{
    private static final long serialVersionUID = 1L;
    private MutableList<T> delegate;

    public ArrayStack()
    {
        this.delegate = FastList.newList();
    }

    public ArrayStack(int initialCapacity)
    {
        this.delegate = FastList.newList(initialCapacity);
    }

    public ArrayStack(Iterable<T> items)
    {
        this.delegate = FastList.newList(items);
    }

    public ArrayStack(T... items)
    {
        this.delegate = FastList.wrapCopy(items);
    }

    public static <T> ArrayStack<T> newStack()
    {
        return new ArrayStack<>();
    }

    public static <T> ArrayStack<T> newStack(Iterable<? extends T> items)
    {
        return new ArrayStack<>((Iterable<T>) items);
    }

    public static <T> ArrayStack<T> newStackWith(T... items)
    {
        return new ArrayStack<>(items);
    }

    public static <T> ArrayStack<T> newStackFromTopToBottom(T... items)
    {
        ArrayStack<T> stack = new ArrayStack<>(items.length);
        for (int i = items.length - 1; i >= 0; i--)
        {
            stack.push(items[i]);
        }
        return stack;
    }

    public static <T> ArrayStack<T> newStackFromTopToBottom(Iterable<? extends T> items)
    {
        ArrayStack<T> stack = ArrayStack.newStack();
        MutableList<T> list = FastList.newList(items);
        stack.delegate = list.reverseThis();
        return stack;
    }

    @Override
    public void push(T item)
    {
        this.delegate.add(item);
    }

    @Override
    public T pop()
    {
        this.checkEmptyStack();
        return this.delegate.remove(this.delegate.size() - 1);
    }

    private void checkEmptyStack()
    {
        if (this.delegate.isEmpty())
        {
            throw new EmptyStackException();
        }
    }

    @Override
    public ListIterable<T> pop(int count)
    {
        this.checkNegativeCount(count);
        MutableList<T> result = Lists.mutable.withInitialCapacity(count);
        if (this.checkZeroCount(count))
        {
            return result;
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        while (count > 0)
        {
            result.add(this.pop());
            count--;
        }
        return result;
    }

    @Override
    public <R extends Collection<T>> R pop(int count, R targetCollection)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return targetCollection;
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        while (count > 0)
        {
            targetCollection.add(this.pop());
            count--;
        }
        return targetCollection;
    }

    @Override
    public <R extends MutableStack<T>> R pop(int count, R targetStack)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return targetStack;
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        while (count > 0)
        {
            targetStack.push(this.pop());
            count--;
        }
        return targetStack;
    }

    @Override
    public void clear()
    {
        this.delegate.clear();
    }

    private boolean checkZeroCount(int count)
    {
        return count == 0;
    }

    @Override
    public T peek()
    {
        this.checkEmptyStack();
        return this.delegate.getLast();
    }

    @Override
    public ListIterable<T> peek(int count)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return FastList.newList();
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);

        MutableList<T> result = FastList.newList(count);
        for (int i = 0; i < count; i++)
        {
            result.add(this.delegate.get(this.delegate.size() - (i + 1)));
        }
        return result;
    }

    @Override
    public T peekAt(int index)
    {
        this.checkNegativeCount(index);
        this.checkEmptyStack();
        this.checkSizeLessThanOrEqualToIndex(index);
        return this.delegate.get(this.delegate.size() - 1 - index);
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean notEmpty()
    {
        return this.delegate.notEmpty();
    }

    @Override
    public T getFirst()
    {
        return this.peek();
    }

    @Override
    public T getLast()
    {
        this.checkEmptyStack();
        return this.delegate.getFirst();
    }

    @Override
    public T getOnly()
    {
        return this.delegate.getOnly();
    }

    @Override
    public boolean contains(Object object)
    {
        return this.delegate.asReversed().contains(object);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.delegate.asReversed().containsAllIterable(source);
    }

    @Override
    public boolean containsAll(Collection<?> source)
    {
        return this.delegate.asReversed().containsAll(source);
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return this.delegate.asReversed().containsAllArguments(elements);
    }

    @Override
    public <V> ArrayStack<V> collect(Function<? super T, ? extends V> function)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collect(function));
    }

    @Override
    public MutableBooleanStack collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return BooleanStacks.mutable.withAllReversed(this.delegate.asReversed().collectBoolean(booleanFunction));
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        return this.delegate.asReversed().collectBoolean(booleanFunction, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        return this.delegate.asReversed().flatCollectBoolean(function, target);
    }

    @Override
    public MutableByteStack collectByte(ByteFunction<? super T> byteFunction)
    {
        return ByteStacks.mutable.withAllReversed(this.delegate.asReversed().collectByte(byteFunction));
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        return this.delegate.asReversed().collectByte(byteFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        return this.delegate.asReversed().flatCollectByte(function, target);
    }

    @Override
    public MutableCharStack collectChar(CharFunction<? super T> charFunction)
    {
        return CharStacks.mutable.withAllReversed(this.delegate.asReversed().collectChar(charFunction));
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        return this.delegate.asReversed().collectChar(charFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        return this.delegate.asReversed().flatCollectChar(function, target);
    }

    @Override
    public MutableDoubleStack collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return DoubleStacks.mutable.withAllReversed(this.delegate.asReversed().collectDouble(doubleFunction));
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        return this.delegate.asReversed().collectDouble(doubleFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function, R target)
    {
        return this.delegate.asReversed().flatCollectDouble(function, target);
    }

    @Override
    public MutableFloatStack collectFloat(FloatFunction<? super T> floatFunction)
    {
        return FloatStacks.mutable.withAllReversed(this.delegate.asReversed().collectFloat(floatFunction));
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        return this.delegate.asReversed().collectFloat(floatFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        return this.delegate.asReversed().flatCollectFloat(function, target);
    }

    @Override
    public MutableIntStack collectInt(IntFunction<? super T> intFunction)
    {
        return IntStacks.mutable.withAllReversed(this.delegate.asReversed().collectInt(intFunction));
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        return this.delegate.asReversed().collectInt(intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        return this.delegate.asReversed().flatCollectInt(function, target);
    }

    @Override
    public MutableLongStack collectLong(LongFunction<? super T> longFunction)
    {
        return LongStacks.mutable.withAllReversed(this.delegate.asReversed().collectLong(longFunction));
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        return this.delegate.asReversed().collectLong(longFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        return this.delegate.asReversed().flatCollectLong(function, target);
    }

    @Override
    public MutableShortStack collectShort(ShortFunction<? super T> shortFunction)
    {
        return ShortStacks.mutable.withAllReversed(this.delegate.asReversed().collectShort(shortFunction));
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        return this.delegate.asReversed().collectShort(shortFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        return this.delegate.asReversed().flatCollectShort(function, target);
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().collect(function, target);
    }

    @Override
    public <P, V> ArrayStack<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectWith(function, parameter).toList());
    }

    @Override
    public <V> ArrayStack<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectIf(predicate, function).toList());
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().collectIf(predicate, function, target);
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().collectWith(function, parameter, targetCollection);
    }

    @Override
    public <V> ArrayStack<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().flatCollect(function).toList());
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.delegate.asReversed().flatCollect(function, target);
    }

    @Override
    public ArrayStack<T> select(Predicate<? super T> predicate)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().select(predicate).toList());
    }

    @Override
    public <P> ArrayStack<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return this.delegate.asReversed().select(predicate, target);
    }

    @Override
    public <S> ArrayStack<S> selectInstancesOf(Class<S> clazz)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().selectInstancesOf(clazz).toList());
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public ArrayStack<T> reject(Predicate<? super T> predicate)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().reject(predicate).toList());
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return this.delegate.asReversed().reject(predicate, target);
    }

    @Override
    public <P> ArrayStack<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().rejectWith(predicate, parameter, targetCollection);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().detect(predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().detectWith(predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().detectOptional(predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().detectWithOptional(predicate, parameter);
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        return this.delegate.asReversed().detectIfNone(predicate, function);
    }

    @Override
    public <P> T detectWithIfNone(Predicate2<? super T, ? super P> predicate, P parameter, Function0<? extends T> function)
    {
        return this.delegate.asReversed().detectWithIfNone(predicate, parameter, function);
    }

    @Override
    public PartitionMutableStack<T> partition(Predicate<? super T> predicate)
    {
        PartitionArrayStack<T> partitionMutableStack = new PartitionArrayStack<>();
        this.delegate.asReversed().forEach(new PartitionArrayStack.PartitionProcedure<>(predicate, partitionMutableStack));
        return partitionMutableStack;
    }

    @Override
    public <P> PartitionMutableStack<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionArrayStack<T> partitionMutableStack = new PartitionArrayStack<>();
        this.delegate.asReversed().forEach(new PartitionArrayStack.PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableStack));
        return partitionMutableStack;
    }

    @Override
    public <S> ArrayStack<Pair<T, S>> zip(Iterable<S> that)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().zip(that).toList());
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.delegate.asReversed().zip(that, target);
    }

    @Override
    public ArrayStack<Pair<T, Integer>> zipWithIndex()
    {
        int maxIndex = this.delegate.size() - 1;
        Interval indices = Interval.fromTo(0, maxIndex);

        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().zip(indices).toList());
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.delegate.asReversed().zipWithIndex(target);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().count(predicate);
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().countWith(predicate, parameter);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().anySatisfyWith(predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().noneSatisfyWith(predicate, parameter);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return this.delegate.asReversed().injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> intObjectToIntFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, intObjectToIntFunction);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> longObjectToLongFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, longObjectToLongFunction);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> doubleObjectToDoubleFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, doubleObjectToDoubleFunction);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> floatObjectToFloatFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, floatObjectToFloatFunction);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> intFunction)
    {
        return this.delegate.asReversed().sumOfInt(intFunction);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> floatFunction)
    {
        return this.delegate.asReversed().sumOfFloat(floatFunction);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> longFunction)
    {
        return this.delegate.asReversed().sumOfLong(longFunction);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.delegate.asReversed().sumOfDouble(doubleFunction);
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        return (MutableObjectLongMap<V>) this.delegate.asReversed().sumByInt(groupBy, function);
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        return (MutableObjectDoubleMap<V>) this.delegate.asReversed().sumByFloat(groupBy, function);
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        return (MutableObjectLongMap<V>) this.delegate.asReversed().sumByLong(groupBy, function);
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        return (MutableObjectDoubleMap<V>) this.delegate.asReversed().sumByDouble(groupBy, function);
    }

    @Override
    public T max()
    {
        return this.delegate.asReversed().max();
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().max(comparator);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().maxBy(function);
    }

    @Override
    public T min()
    {
        return this.delegate.asReversed().min();
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().min(comparator);
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().minBy(function);
    }

    @Override
    public String makeString()
    {
        return this.delegate.asReversed().makeString();
    }

    @Override
    public String makeString(String separator)
    {
        return this.delegate.asReversed().makeString(separator);
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        return this.delegate.asReversed().makeString(start, separator, end);
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.delegate.asReversed().appendString(appendable);
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.delegate.asReversed().appendString(appendable, separator);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.delegate.asReversed().appendString(appendable, start, separator, end);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, FastListMultimap.newMultimap());
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().groupBy(function, target);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, FastListMultimap.newMultimap());
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.delegate.asReversed().groupByEach(function, target);
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.groupByUniqueKey(function, UnifiedMap.newMap(this.size()));
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().groupByUniqueKey(function, target);
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        return this.delegate.asReversed().chunk(size);
    }

    @Override
    public ArrayStack<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.delegate.reverseForEach(procedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.delegate.asReversed().forEachWith(procedure, parameter);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.delegate.asReversed().forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        return this.delegate.asReversed().into(target);
    }

    @Override
    public MutableList<T> toList()
    {
        return this.delegate.asReversed().toList();
    }

    @Override
    public MutableList<T> toSortedList()
    {
        return this.delegate.asReversed().toSortedList();
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().toSortedList(comparator);
    }

    @Override
    public MutableSet<T> toSet()
    {
        return this.delegate.asReversed().toSet();
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        return this.delegate.asReversed().toSortedSet();
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().toSortedSet(comparator);
    }

    @Override
    public ImmutableStack<T> toImmutable()
    {
        return Stacks.immutable.withAll(this.delegate);
    }

    @Override
    public MutableBag<T> toBag()
    {
        return this.delegate.asReversed().toBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        return this.delegate.asReversed().toSortedBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().toSortedBag(comparator);
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction, R target)
    {
        return this.delegate.asReversed().toMap(keyFunction, valueFunction, target);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Comparator<? super NK> comparator, Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public <KK extends Comparable<? super KK>, NK, NV> MutableSortedMap<NK, NV> toSortedMapBy(Function<? super NK, KK> sortBy, Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toSortedMapBy(sortBy, keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toBiMap(keyFunction, valueFunction);
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        return LazyIterate.adapt(this);
    }

    @Override
    public MutableStack<T> asUnmodifiable()
    {
        return UnmodifiableStack.of(this);
    }

    @Override
    public MutableStack<T> asSynchronized()
    {
        return SynchronizedStack.of(this);
    }

    @Override
    public Object[] toArray()
    {
        return this.delegate.asReversed().toArray();
    }

    @Override
    public <T2> T2[] toArray(T2[] a)
    {
        return this.delegate.asReversed().toArray(a);
    }

    @Override
    public Iterator<T> iterator()
    {
        return this.delegate.asReversed().iterator();
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

        if (that instanceof ArrayStack<?>)
        {
            return this.delegate.equals(((ArrayStack<?>) that).delegate);
        }
        Iterator<T> thisIterator = this.iterator();
        Iterator<?> thatIterator = that.iterator();
        while (thisIterator.hasNext() && thatIterator.hasNext())
        {
            if (!Objects.equals(thisIterator.next(), thatIterator.next()))
            {
                return false;
            }
        }
        return !thisIterator.hasNext() && !thatIterator.hasNext();
    }

    @Override
    public String toString()
    {
        return this.delegate.asReversed().makeString("[", ", ", "]");
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (int i = this.delegate.size() - 1; i >= 0; i--)
        {
            T each = this.delegate.get(i);
            hashCode = 31 * hashCode + (each == null ? 0 : each.hashCode());
        }
        return hashCode;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.size());
        for (T each : this.delegate.asReversed())
        {
            out.writeObject(each);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int size = in.readInt();
        T[] array = (T[]) new Object[size];
        for (int i = size - 1; i >= 0; i--)
        {
            array[i] = (T) in.readObject();
        }
        this.delegate = FastList.newListWith(array);
    }

    private void checkSizeLessThanCount(int count)
    {
        if (this.delegate.size() < count)
        {
            throw new IllegalArgumentException("Count must be less than size: Count = " + count + " Size = " + this.delegate.size());
        }
    }

    private void checkSizeLessThanOrEqualToIndex(int index)
    {
        if (this.delegate.size() <= index)
        {
            throw new IllegalArgumentException("Count must be less than size: Count = " + index + " Size = " + this.delegate.size());
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
    public MutableStack<T> takeWhile(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".takeWhile() not implemented yet");
    }

    @Override
    public MutableStack<T> dropWhile(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".dropWhile() not implemented yet");
    }

    @Override
    public PartitionMutableStack<T> partitionWhile(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".partitionWhile() not implemented yet");
    }

    @Override
    public MutableStack<T> distinct()
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().distinct());
    }

    @Override
    public int indexOf(Object object)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".indexOf() not implemented yet");
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
}
