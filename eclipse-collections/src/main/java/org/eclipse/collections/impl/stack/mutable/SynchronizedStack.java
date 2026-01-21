/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.stack.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
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
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
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
import org.eclipse.collections.api.stack.primitive.MutableBooleanStack;
import org.eclipse.collections.api.stack.primitive.MutableByteStack;
import org.eclipse.collections.api.stack.primitive.MutableCharStack;
import org.eclipse.collections.api.stack.primitive.MutableDoubleStack;
import org.eclipse.collections.api.stack.primitive.MutableFloatStack;
import org.eclipse.collections.api.stack.primitive.MutableIntStack;
import org.eclipse.collections.api.stack.primitive.MutableLongStack;
import org.eclipse.collections.api.stack.primitive.MutableShortStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;

/**
 * A synchronized view of a {@link MutableStack}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableStack#asSynchronized()
 */
public final class SynchronizedStack<T> implements MutableStack<T>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final Object lock;
    private final MutableStack<T> delegate;

    public SynchronizedStack(MutableStack<T> newStack)
    {
        this(newStack, null);
    }

    public SynchronizedStack(MutableStack<T> newStack, Object newLock)
    {
        if (newStack == null)
        {
            throw new IllegalArgumentException("Cannot create a SynchronizedStack on a null stack");
        }
        this.delegate = newStack;
        this.lock = newLock == null ? this : newLock;
    }

    /**
     * This method will take a MutableStack and wrap it directly in a SynchronizedStack.
     */
    public static <T, S extends MutableStack<T>> SynchronizedStack<T> of(S stack)
    {
        return new SynchronizedStack<>(stack);
    }

    @Override
    public T pop()
    {
        synchronized (this.lock)
        {
            return this.delegate.pop();
        }
    }

    @Override
    public ListIterable<T> pop(int count)
    {
        synchronized (this.lock)
        {
            return this.delegate.pop(count);
        }
    }

    @Override
    public <R extends Collection<T>> R pop(int count, R targetCollection)
    {
        synchronized (this.lock)
        {
            return this.delegate.pop(count, targetCollection);
        }
    }

    @Override
    public <R extends MutableStack<T>> R pop(int count, R targetStack)
    {
        synchronized (this.lock)
        {
            return this.delegate.pop(count, targetStack);
        }
    }

    @Override
    public void clear()
    {
        synchronized (this.lock)
        {
            this.delegate.clear();
        }
    }

    @Override
    public void push(T item)
    {
        synchronized (this.lock)
        {
            this.delegate.push(item);
        }
    }

    @Override
    public T peek()
    {
        synchronized (this.lock)
        {
            return this.delegate.peek();
        }
    }

    @Override
    public ListIterable<T> peek(int count)
    {
        synchronized (this.lock)
        {
            return this.delegate.peek(count);
        }
    }

    @Override
    public T peekAt(int index)
    {
        synchronized (this.lock)
        {
            return this.delegate.peekAt(index);
        }
    }

    @Override
    public MutableStack<T> select(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.select(predicate);
        }
    }

    @Override
    public <P> MutableStack<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.selectWith(predicate, parameter);
        }
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.select(predicate, target);
        }
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        synchronized (this.lock)
        {
            return this.delegate.selectWith(predicate, parameter, targetCollection);
        }
    }

    @Override
    public MutableStack<T> reject(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.reject(predicate);
        }
    }

    @Override
    public <P> MutableStack<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.rejectWith(predicate, parameter);
        }
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.reject(predicate, target);
        }
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        synchronized (this.lock)
        {
            return this.delegate.rejectWith(predicate, parameter, targetCollection);
        }
    }

    @Override
    public PartitionMutableStack<T> partition(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.partition(predicate);
        }
    }

    @Override
    public <P> PartitionMutableStack<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.partitionWith(predicate, parameter);
        }
    }

    @Override
    public <S> MutableStack<S> selectInstancesOf(Class<S> clazz)
    {
        synchronized (this.lock)
        {
            return this.delegate.selectInstancesOf(clazz);
        }
    }

    @Override
    public <V> MutableStack<V> collect(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.collect(function);
        }
    }

    @Override
    public MutableBooleanStack collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectBoolean(booleanFunction);
        }
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectBoolean(booleanFunction, target);
        }
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectBoolean(function, target);
        }
    }

    @Override
    public MutableByteStack collectByte(ByteFunction<? super T> byteFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectByte(byteFunction);
        }
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectByte(byteFunction, target);
        }
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectByte(function, target);
        }
    }

    @Override
    public MutableCharStack collectChar(CharFunction<? super T> charFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectChar(charFunction);
        }
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectChar(charFunction, target);
        }
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectChar(function, target);
        }
    }

    @Override
    public MutableDoubleStack collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectDouble(doubleFunction);
        }
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectDouble(doubleFunction, target);
        }
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectDouble(function, target);
        }
    }

    @Override
    public MutableFloatStack collectFloat(FloatFunction<? super T> floatFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectFloat(floatFunction);
        }
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectFloat(floatFunction, target);
        }
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectFloat(function, target);
        }
    }

    @Override
    public MutableIntStack collectInt(IntFunction<? super T> intFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectInt(intFunction);
        }
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectInt(intFunction, target);
        }
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectInt(function, target);
        }
    }

    @Override
    public MutableLongStack collectLong(LongFunction<? super T> longFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectLong(longFunction);
        }
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectLong(function, target);
        }
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectLong(longFunction, target);
        }
    }

    @Override
    public MutableShortStack collectShort(ShortFunction<? super T> shortFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectShort(shortFunction);
        }
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectShort(shortFunction, target);
        }
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollectShort(function, target);
        }
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collect(function, target);
        }
    }

    @Override
    public <P, V> MutableStack<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectWith(function, parameter);
        }
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R targetCollection)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectWith(function, parameter, targetCollection);
        }
    }

    @Override
    public <V> MutableStack<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectIf(predicate, function);
        }
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectIf(predicate, function, target);
        }
    }

    @Override
    public <V> MutableStack<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollect(function);
        }
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.flatCollect(function, target);
        }
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.zip(that, target);
        }
    }

    @Override
    public <S> MutableStack<Pair<T, S>> zip(Iterable<S> that)
    {
        synchronized (this.lock)
        {
            return this.delegate.zip(that);
        }
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.zipWithIndex(target);
        }
    }

    @Override
    public MutableStack<Pair<T, Integer>> zipWithIndex()
    {
        synchronized (this.lock)
        {
            return this.delegate.zipWithIndex();
        }
    }

    @Override
    public int size()
    {
        synchronized (this.lock)
        {
            return this.delegate.size();
        }
    }

    @Override
    public boolean isEmpty()
    {
        synchronized (this.lock)
        {
            return this.delegate.isEmpty();
        }
    }

    @Override
    public boolean notEmpty()
    {
        synchronized (this.lock)
        {
            return this.delegate.notEmpty();
        }
    }

    @Override
    public T getFirst()
    {
        synchronized (this.lock)
        {
            return this.delegate.getFirst();
        }
    }

    @Override
    public Optional<T> getFirstOptional()
    {
        synchronized (this.lock)
        {
            return this.delegate.getFirstOptional();
        }
    }

    @Override
    public T getLast()
    {
        return this.delegate.getLast();
    }

    @Override
    public Optional<T> getLastOptional()
    {
        synchronized (this.lock)
        {
            return this.delegate.getLastOptional();
        }
    }

    @Override
    public T getOnly()
    {
        synchronized (this.lock)
        {
            return this.delegate.getOnly();
        }
    }

    @Override
    public boolean contains(Object object)
    {
        synchronized (this.lock)
        {
            return this.delegate.contains(object);
        }
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAllIterable(source);
        }
    }

    @Override
    public boolean containsAll(Collection<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAll(source);
        }
    }

    @Override
    public boolean containsAny(Collection<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAny(source);
        }
    }

    @Override
    public boolean containsNone(Collection<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsNone(source);
        }
    }

    @Override
    public boolean containsAnyIterable(Iterable<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAnyIterable(source);
        }
    }

    @Override
    public boolean containsNoneIterable(Iterable<?> source)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsNoneIterable(source);
        }
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        synchronized (this.lock)
        {
            return this.delegate.containsAllArguments(elements);
        }
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.detect(predicate);
        }
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectWith(predicate, parameter);
        }
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectOptional(predicate);
        }
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectWithOptional(predicate, parameter);
        }
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectIfNone(predicate, function);
        }
    }

    @Override
    public <P> T detectWithIfNone(Predicate2<? super T, ? super P> predicate, P parameter, Function0<? extends T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectWithIfNone(predicate, parameter, function);
        }
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.count(predicate);
        }
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.countWith(predicate, parameter);
        }
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.anySatisfy(predicate);
        }
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.anySatisfyWith(predicate, parameter);
        }
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.allSatisfy(predicate);
        }
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.allSatisfyWith(predicate, parameter);
        }
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.noneSatisfy(predicate);
        }
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.delegate.noneSatisfyWith(predicate, parameter);
        }
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectInto(injectedValue, function);
        }
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> intObjectToIntFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectIntoInt(injectedValue, intObjectToIntFunction);
        }
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> longObjectToLongFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectIntoLong(injectedValue, longObjectToLongFunction);
        }
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> floatObjectToFloatFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectIntoFloat(injectedValue, floatObjectToFloatFunction);
        }
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> doubleObjectToDoubleFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.injectIntoDouble(injectedValue, doubleObjectToDoubleFunction);
        }
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.into(target);
        }
    }

    @Override
    public MutableList<T> toList()
    {
        synchronized (this.lock)
        {
            return this.delegate.toList();
        }
    }

    @Override
    public MutableList<T> toSortedList()
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedList();
        }
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedList(comparator);
        }
    }

    @Override
    public MutableSet<T> toSet()
    {
        synchronized (this.lock)
        {
            return this.delegate.toSet();
        }
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedSet();
        }
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedSet(comparator);
        }
    }

    @Override
    public MutableStack<T> toStack()
    {
        synchronized (this.lock)
        {
            return this.delegate.toStack();
        }
    }

    @Override
    public ImmutableStack<T> toImmutable()
    {
        synchronized (this.lock)
        {
            return this.delegate.toImmutable();
        }
    }

    @Override
    public MutableBag<T> toBag()
    {
        synchronized (this.lock)
        {
            return this.delegate.toBag();
        }
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedBag();
        }
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedBag(comparator);
        }
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.toMap(keyFunction, valueFunction);
        }
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.toMap(keyFunction, valueFunction, target);
        }
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedMap(keyFunction, valueFunction);
        }
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Comparator<? super NK> comparator, Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.toSortedMap(comparator, keyFunction, valueFunction);
        }
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.toBiMap(keyFunction, valueFunction);
        }
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        synchronized (this.lock)
        {
            return this.delegate.asLazy();
        }
    }

    @Override
    public Object[] toArray()
    {
        synchronized (this.lock)
        {
            return this.delegate.toArray();
        }
    }

    @Override
    public <T2> T2[] toArray(T2[] a)
    {
        synchronized (this.lock)
        {
            return this.delegate.toArray(a);
        }
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.min(comparator);
        }
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        synchronized (this.lock)
        {
            return this.delegate.max(comparator);
        }
    }

    @Override
    public T min()
    {
        synchronized (this.lock)
        {
            return this.delegate.min();
        }
    }

    @Override
    public T max()
    {
        synchronized (this.lock)
        {
            return this.delegate.max();
        }
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.minBy(function);
        }
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.maxBy(function);
        }
    }

    @Override
    public long sumOfInt(IntFunction<? super T> intFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumOfInt(intFunction);
        }
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> floatFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumOfFloat(floatFunction);
        }
    }

    @Override
    public long sumOfLong(LongFunction<? super T> longFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumOfLong(longFunction);
        }
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> doubleFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumOfDouble(doubleFunction);
        }
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumByInt(groupBy, function);
        }
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumByFloat(groupBy, function);
        }
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumByLong(groupBy, function);
        }
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.sumByDouble(groupBy, function);
        }
    }

    @Override
    public String makeString()
    {
        synchronized (this.lock)
        {
            return this.delegate.makeString();
        }
    }

    @Override
    public String makeString(String separator)
    {
        synchronized (this.lock)
        {
            return this.delegate.makeString(separator);
        }
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        synchronized (this.lock)
        {
            return this.delegate.makeString(start, separator, end);
        }
    }

    @Override
    public void appendString(Appendable appendable)
    {
        synchronized (this.lock)
        {
            this.delegate.appendString(appendable);
        }
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        synchronized (this.lock)
        {
            this.delegate.appendString(appendable, separator);
        }
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        synchronized (this.lock)
        {
            this.delegate.appendString(appendable, start, separator, end);
        }
    }

    @Override
    public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupBy(function);
        }
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupBy(function, target);
        }
    }

    @Override
    public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupByEach(function);
        }
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupByEach(function, target);
        }
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupByUniqueKey(function);
        }
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(Function<? super T, ? extends V> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupByUniqueKey(function, target);
        }
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        synchronized (this.lock)
        {
            return this.delegate.chunk(size);
        }
    }

    @Override
    public MutableStack<T> tap(Procedure<? super T> procedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEach(procedure);
            return this;
        }
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEach(procedure);
        }
    }

    @Override
    public String toString()
    {
        synchronized (this.lock)
        {
            return this.delegate.toString();
        }
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEachWithIndex(objectIntProcedure);
        }
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        synchronized (this.lock)
        {
            this.delegate.forEachWith(procedure, parameter);
        }
    }

    @Override
    public MutableStack<T> asUnmodifiable()
    {
        synchronized (this.lock)
        {
            return this.delegate.asUnmodifiable();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        synchronized (this.lock)
        {
            return this.delegate.equals(obj);
        }
    }

    @Override
    public int hashCode()
    {
        synchronized (this.lock)
        {
            return this.delegate.hashCode();
        }
    }

    @Override
    public MutableStack<T> asSynchronized()
    {
        synchronized (this.lock)
        {
            return this;
        }
    }

    @Override
    public Iterator<T> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.delegate.iterator());
    }

    @Override
    public <K, V> MutableMap<K, V> aggregateInPlaceBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.delegate.aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
        }
    }

    @Override
    public <K, V> MutableMap<K, V> aggregateBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        synchronized (this.lock)
        {
            return this.delegate.aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
        }
    }

    @Override
    public <K> MutableMap<K, T> reduceBy(
            Function<? super T, ? extends K> groupBy,
            Function2<? super T, ? super T, ? extends T> reduceFunction)
    {
        synchronized (this.lock)
        {
            return this.delegate.reduceBy(groupBy, reduceFunction);
        }
    }

    @Override
    public MutableStack<T> takeWhile(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.takeWhile(predicate);
        }
    }

    @Override
    public MutableStack<T> dropWhile(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.dropWhile(predicate);
        }
    }

    @Override
    public PartitionMutableStack<T> partitionWhile(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.partitionWhile(predicate);
        }
    }

    @Override
    public MutableStack<T> distinct()
    {
        synchronized (this.lock)
        {
            return this.delegate.distinct();
        }
    }

    @Override
    public <V> MutableStack<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectWithIndex(function);
        }
    }

    /**
     * @since 9.1
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.collectWithIndex(function, target);
        }
    }

    /**
     * @since 11.0
     */
    @Override
    public <R extends Collection<T>> R selectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.selectWithIndex(predicate, target);
        }
    }

    /**
     * @since 11.0
     */
    @Override
    public <R extends Collection<T>> R rejectWithIndex(ObjectIntPredicate<? super T> predicate, R target)
    {
        synchronized (this.lock)
        {
            return this.delegate.rejectWithIndex(predicate, target);
        }
    }

    @Override
    public int indexOf(Object object)
    {
        synchronized (this.lock)
        {
            return this.delegate.indexOf(object);
        }
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.corresponds(other, predicate);
        }
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEach(startIndex, endIndex, procedure);
        }
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        synchronized (this.lock)
        {
            this.delegate.forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
        }
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        synchronized (this.lock)
        {
            return this.delegate.detectIndex(predicate);
        }
    }
}
