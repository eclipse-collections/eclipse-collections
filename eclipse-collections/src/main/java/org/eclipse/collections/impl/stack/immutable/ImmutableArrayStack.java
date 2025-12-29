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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
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
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.stack.PartitionImmutableStack;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
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
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.stack.PartitionArrayStack;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * The immutable equivalent of ArrayStack. Wraps a FastList.
 *
 * @deprecated Replaced by {@link ImmutableNotEmptyStack}.
 * Use {@link org.eclipse.collections.api.factory.Stacks#immutable}.{@link ImmutableStackFactoryImpl#with(Object[]) with()}
 */
@Deprecated
final class ImmutableArrayStack<T> implements ImmutableStack<T>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final MutableList<T> delegate;

    private ImmutableArrayStack(T[] newElements)
    {
        this.delegate = FastList.newListWith(newElements);
    }

    private ImmutableArrayStack(MutableList<T> newElements)
    {
        this.delegate = newElements;
    }

    public static <T> ImmutableStack<T> newStack()
    {
        return new ImmutableArrayStack<>(FastList.newList());
    }

    public static <E> ImmutableArrayStack<E> newStack(Iterable<? extends E> iterable)
    {
        return new ImmutableArrayStack<>((E[]) Iterate.toArray(iterable));
    }

    public static <E> ImmutableArrayStack<E> newStackWith(E... elements)
    {
        return new ImmutableArrayStack<>(elements.clone());
    }

    public static <T> ImmutableArrayStack<T> newStackFromTopToBottom(Iterable<? extends T> items)
    {
        return new ImmutableArrayStack<>(FastList.<T>newList(items).reverseThis());
    }

    public static <T> ImmutableArrayStack<T> newStackFromTopToBottom(T... items)
    {
        return new ImmutableArrayStack<>(FastList.newListWith(items).reverseThis());
    }

    @Override
    public ImmutableStack<T> push(T item)
    {
        MutableList<T> newDelegate = FastList.newList(this.delegate);
        newDelegate.add(item);
        return new ImmutableArrayStack<>(newDelegate);
    }

    @Override
    public ImmutableStack<T> pop()
    {
        this.checkEmptyStack();
        MutableList<T> newDelegate = FastList.newList(this.delegate);
        newDelegate.remove(this.delegate.size() - 1);
        return new ImmutableArrayStack<>(newDelegate);
    }

    @Override
    public ImmutableStack<T> pop(int count)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return this;
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        MutableList<T> newDelegate = this.delegate.clone();
        while (count > 0)
        {
            newDelegate.remove(this.delegate.size() - 1);
            count--;
        }
        return new ImmutableArrayStack<>(newDelegate);
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
        this.checkEmptyStack();
        return this.delegate.getLast();
    }

    private void checkEmptyStack()
    {
        if (this.delegate.isEmpty())
        {
            throw new EmptyStackException();
        }
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
        return FastList.newList(this.asLazy().take(count));
    }

    private boolean checkZeroCount(int count)
    {
        return count == 0;
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
    public T peekAt(int index)
    {
        this.checkNegativeCount(index);
        this.checkEmptyStack();
        this.checkSizeLessThanOrEqualToIndex(index);
        return this.delegate.get(this.delegate.size() - 1 - index);
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
    public ImmutableStack<T> select(Predicate<? super T> predicate)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().select(predicate).toList());
    }

    @Override
    public <P> ImmutableStack<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return this.delegate.asReversed().select(predicate, target);
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public ImmutableStack<T> reject(Predicate<? super T> predicate)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().reject(predicate).toList());
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return this.delegate.asReversed().reject(predicate, target);
    }

    @Override
    public <P> ImmutableStack<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().rejectWith(predicate, parameter, targetCollection);
    }

    @Override
    public PartitionImmutableStack<T> partition(Predicate<? super T> predicate)
    {
        PartitionArrayStack<T> partitionMutableStack = new PartitionArrayStack<>();
        this.delegate.asReversed().forEach(new PartitionArrayStack.PartitionProcedure<>(predicate, partitionMutableStack));
        return partitionMutableStack.toImmutable();
    }

    @Override
    public <P> PartitionImmutableStack<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionArrayStack<T> partitionMutableStack = new PartitionArrayStack<>();
        this.delegate.asReversed().forEach(new PartitionArrayStack.PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableStack));
        return partitionMutableStack.toImmutable();
    }

    @Override
    public <S> ImmutableStack<S> selectInstancesOf(Class<S> clazz)
    {
        return Stacks.immutable.withAllReversed(this.delegate.asReversed().selectInstancesOf(clazz).toList());
    }

    @Override
    public <V> ImmutableStack<V> collect(Function<? super T, ? extends V> function)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collect(function));
    }

    @Override
    public ImmutableBooleanStack collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return BooleanStacks.immutable.withAllReversed(this.asLazy().collectBoolean(booleanFunction));
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
    public ImmutableByteStack collectByte(ByteFunction<? super T> byteFunction)
    {
        return ByteStacks.immutable.withAllReversed(this.asLazy().collectByte(byteFunction));
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
    public ImmutableCharStack collectChar(CharFunction<? super T> charFunction)
    {
        return CharStacks.immutable.withAllReversed(this.asLazy().collectChar(charFunction));
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
    public ImmutableDoubleStack collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return DoubleStacks.immutable.withAllReversed(this.asLazy().collectDouble(doubleFunction));
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
    public ImmutableFloatStack collectFloat(FloatFunction<? super T> floatFunction)
    {
        return FloatStacks.immutable.withAllReversed(this.asLazy().collectFloat(floatFunction));
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
    public ImmutableIntStack collectInt(IntFunction<? super T> intFunction)
    {
        return IntStacks.immutable.withAllReversed(this.asLazy().collectInt(intFunction));
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
    public ImmutableLongStack collectLong(LongFunction<? super T> longFunction)
    {
        return LongStacks.immutable.withAllReversed(this.asLazy().collectLong(longFunction));
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
    public ImmutableShortStack collectShort(ShortFunction<? super T> shortFunction)
    {
        return ShortStacks.immutable.withAllReversed(this.asLazy().collectShort(shortFunction));
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
    public <P, V> ImmutableStack<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectWith(function, parameter).toList());
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().collectWith(function, parameter, targetCollection);
    }

    @Override
    public <V> ImmutableStack<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectIf(predicate, function).toList());
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().collectIf(predicate, function, target);
    }

    @Override
    public <V> ImmutableStack<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().flatCollect(function).toList());
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.delegate.asReversed().flatCollect(function, target);
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
    public T min(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().min(comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().max(comparator);
    }

    @Override
    public T min()
    {
        return this.delegate.asReversed().min();
    }

    @Override
    public T max()
    {
        return this.delegate.asReversed().max();
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().toList().minBy(function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().maxBy(function);
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
    public <V> ImmutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        ObjectLongMap<V> map = this.delegate.asReversed().sumByInt(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V> ImmutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        ObjectDoubleMap<V> map = this.delegate.asReversed().sumByFloat(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V> ImmutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        ObjectLongMap<V> map = this.delegate.asReversed().sumByLong(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V> ImmutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        ObjectDoubleMap<V> map = this.delegate.asReversed().sumByDouble(groupBy, function);
        return map.toImmutable();
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
    public <V> ImmutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, FastListMultimap.<V, T>newMultimap()).toImmutable();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().groupBy(function, target);
    }

    @Override
    public <V> ImmutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, FastListMultimap.newMultimap()).toImmutable();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.delegate.asReversed().groupByEach(function, target);
    }

    @Override
    public <V> ImmutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.groupByUniqueKey(function, UnifiedMap.<V, T>newMap(this.size())).toImmutable();
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().groupByUniqueKey(function, target);
    }

    @Override
    public <S> ImmutableStack<Pair<T, S>> zip(Iterable<S> that)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().zip(that).toList());
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.delegate.asReversed().zip(that, target);
    }

    @Override
    public ImmutableStack<Pair<T, Integer>> zipWithIndex()
    {
        int maxIndex = this.delegate.size() - 1;
        Interval indices = Interval.fromTo(0, maxIndex);

        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().zip(indices).toList());
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.delegate.asReversed().zipWithIndex(target);
    }

    @Override
    public ImmutableStack<T> toImmutable()
    {
        return this;
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        return this.delegate.asReversed().chunk(size);
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
    public ImmutableStack<T> tap(Procedure<? super T> procedure)
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
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.delegate.asReversed().forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.delegate.asReversed().forEachWith(procedure, parameter);
    }

    @Override
    public ImmutableStack<T> takeWhile(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".takeWhile() not implemented yet");
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
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".distinct() not implemented yet");
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

        if (that instanceof ImmutableArrayStack<?>)
        {
            return this.delegate.equals(((ImmutableArrayStack<?>) that).delegate);
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

    private static class ImmutableStackSerializationProxy<T> implements Externalizable
    {
        private static final long serialVersionUID = 1L;

        private StackIterable<T> stack;

        public ImmutableStackSerializationProxy()
        {
            // Empty constructor for Externalizable class
        }

        protected ImmutableStackSerializationProxy(StackIterable<T> stack)
        {
            this.stack = stack;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException
        {
            out.writeInt(this.stack.size());
            try
            {
                this.stack.forEach(new CheckedProcedure<T>()
                {
                    @Override
                    public void safeValue(T object) throws IOException
                    {
                        out.writeObject(object);
                    }
                });
            }
            catch (RuntimeException e)
            {
                if (e.getCause() instanceof IOException)
                {
                    throw (IOException) e.getCause();
                }
                throw e;
            }
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
        {
            int size = in.readInt();
            MutableList<T> deserializedDelegate = new FastList<>(size);

            for (int i = 0; i < size; i++)
            {
                deserializedDelegate.add((T) in.readObject());
            }

            this.stack = ImmutableArrayStack.newStackFromTopToBottom(deserializedDelegate);
        }

        protected Object readResolve()
        {
            return this.stack;
        }
    }
}
