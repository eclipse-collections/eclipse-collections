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

import java.io.Serializable;
import java.util.Collection;
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
import org.eclipse.collections.api.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
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
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;

public class UnmodifiableStack<T> implements MutableStack<T>, Serializable
{
    private static final long serialVersionUID = 1L;

    private final MutableStack<T> mutableStack;

    protected UnmodifiableStack(MutableStack<T> mutableStack)
    {
        if (mutableStack == null)
        {
            throw new IllegalArgumentException("Cannot create a UnmodifiableStack on a null stack");
        }
        this.mutableStack = mutableStack;
    }

    public static <T, S extends MutableStack<T>> UnmodifiableStack<T> of(S stack)
    {
        return new UnmodifiableStack<>(stack);
    }

    @Override
    public T pop()
    {
        throw new UnsupportedOperationException("Cannot call pop() on " + this.getClass().getSimpleName());
    }

    @Override
    public ListIterable<T> pop(int count)
    {
        throw new UnsupportedOperationException("Cannot call pop() on " + this.getClass().getSimpleName());
    }

    @Override
    public <R extends Collection<T>> R pop(int count, R targetCollection)
    {
        throw new UnsupportedOperationException("Cannot call pop() on " + this.getClass().getSimpleName());
    }

    @Override
    public <R extends MutableStack<T>> R pop(int count, R targetStack)
    {
        throw new UnsupportedOperationException("Cannot call pop() on " + this.getClass().getSimpleName());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    @Override
    public void push(T item)
    {
        throw new UnsupportedOperationException("Cannot call push() on " + this.getClass().getSimpleName());
    }

    @Override
    public Iterator<T> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.mutableStack.iterator());
    }

    @Override
    public MutableStack<T> select(Predicate<? super T> predicate)
    {
        return this.mutableStack.select(predicate);
    }

    @Override
    public <P> MutableStack<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.mutableStack.selectWith(predicate, parameter);
    }

    @Override
    public MutableStack<T> reject(Predicate<? super T> predicate)
    {
        return this.mutableStack.reject(predicate);
    }

    @Override
    public <P> MutableStack<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.mutableStack.rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableStack<T> partition(Predicate<? super T> predicate)
    {
        return this.mutableStack.partition(predicate);
    }

    @Override
    public <P> PartitionMutableStack<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.mutableStack.partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableStack<S> selectInstancesOf(Class<S> clazz)
    {
        return this.mutableStack.selectInstancesOf(clazz);
    }

    @Override
    public <V> MutableStack<V> collect(Function<? super T, ? extends V> function)
    {
        return this.mutableStack.collect(function);
    }

    @Override
    public MutableBooleanStack collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.mutableStack.collectBoolean(booleanFunction);
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        return this.mutableStack.collectBoolean(booleanFunction, target);
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        return this.mutableStack.flatCollectBoolean(function, target);
    }

    @Override
    public MutableByteStack collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.mutableStack.collectByte(byteFunction);
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        return this.mutableStack.collectByte(byteFunction, target);
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        return this.mutableStack.flatCollectByte(function, target);
    }

    @Override
    public MutableCharStack collectChar(CharFunction<? super T> charFunction)
    {
        return this.mutableStack.collectChar(charFunction);
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        return this.mutableStack.collectChar(charFunction, target);
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        return this.mutableStack.flatCollectChar(function, target);
    }

    @Override
    public MutableDoubleStack collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.mutableStack.collectDouble(doubleFunction);
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        return this.mutableStack.collectDouble(doubleFunction, target);
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function, R target)
    {
        return this.mutableStack.flatCollectDouble(function, target);
    }

    @Override
    public MutableFloatStack collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.mutableStack.collectFloat(floatFunction);
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        return this.mutableStack.collectFloat(floatFunction, target);
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        return this.mutableStack.flatCollectFloat(function, target);
    }

    @Override
    public MutableIntStack collectInt(IntFunction<? super T> intFunction)
    {
        return this.mutableStack.collectInt(intFunction);
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        return this.mutableStack.collectInt(intFunction, target);
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        return this.mutableStack.flatCollectInt(function, target);
    }

    @Override
    public MutableLongStack collectLong(LongFunction<? super T> longFunction)
    {
        return this.mutableStack.collectLong(longFunction);
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        return this.mutableStack.collectLong(longFunction, target);
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        return this.mutableStack.flatCollectLong(function, target);
    }

    @Override
    public MutableShortStack collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.mutableStack.collectShort(shortFunction);
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        return this.mutableStack.collectShort(shortFunction, target);
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        return this.mutableStack.flatCollectShort(function, target);
    }

    @Override
    public <P, V> MutableStack<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.mutableStack.collectWith(function, parameter);
    }

    @Override
    public <V> MutableStack<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return this.mutableStack.collectIf(predicate, function);
    }

    @Override
    public <V> MutableStack<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.mutableStack.flatCollect(function);
    }

    @Override
    public <S> MutableStack<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.mutableStack.zip(that);
    }

    @Override
    public MutableStack<Pair<T, Integer>> zipWithIndex()
    {
        return this.mutableStack.zipWithIndex();
    }

    @Override
    public int size()
    {
        return this.mutableStack.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.mutableStack.isEmpty();
    }

    @Override
    public boolean notEmpty()
    {
        return this.mutableStack.notEmpty();
    }

    @Override
    public T getFirst()
    {
        return this.mutableStack.getFirst();
    }

    @Override
    public T getLast()
    {
        return this.mutableStack.getLast();
    }

    @Override
    public T getOnly()
    {
        return this.mutableStack.getOnly();
    }

    @Override
    public boolean contains(Object object)
    {
        return this.mutableStack.contains(object);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.mutableStack.containsAllIterable(source);
    }

    @Override
    public boolean containsAll(Collection<?> source)
    {
        return this.mutableStack.containsAll(source);
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return this.mutableStack.containsAllArguments(elements);
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return this.mutableStack.select(predicate, target);
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.mutableStack.selectWith(predicate, parameter, targetCollection);
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return this.mutableStack.reject(predicate, target);
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.mutableStack.rejectWith(predicate, parameter, targetCollection);
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return this.mutableStack.collect(function, target);
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R targetCollection)
    {
        return this.mutableStack.collectWith(function, parameter, targetCollection);
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function, R target)
    {
        return this.mutableStack.collectIf(predicate, function, target);
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.mutableStack.flatCollect(function, target);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return this.mutableStack.detect(predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.mutableStack.detectWith(predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return this.mutableStack.detectOptional(predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.mutableStack.detectWithOptional(predicate, parameter);
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        return this.mutableStack.detectIfNone(predicate, function);
    }

    @Override
    public <P> T detectWithIfNone(Predicate2<? super T, ? super P> predicate, P parameter, Function0<? extends T> function)
    {
        return this.mutableStack.detectWithIfNone(predicate, parameter, function);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return this.mutableStack.count(predicate);
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.mutableStack.countWith(predicate, parameter);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return this.mutableStack.anySatisfy(predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.mutableStack.anySatisfyWith(predicate, parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return this.mutableStack.allSatisfy(predicate);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.mutableStack.allSatisfyWith(predicate, parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return this.mutableStack.noneSatisfy(predicate);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.mutableStack.noneSatisfyWith(predicate, parameter);
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return this.mutableStack.injectInto(injectedValue, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> intObjectToIntFunction)
    {
        return this.mutableStack.injectIntoInt(injectedValue, intObjectToIntFunction);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> longObjectToLongFunction)
    {
        return this.mutableStack.injectIntoLong(injectedValue, longObjectToLongFunction);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> floatObjectToFloatFunction)
    {
        return this.mutableStack.injectIntoFloat(injectedValue, floatObjectToFloatFunction);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> doubleObjectToDoubleFunction)
    {
        return this.mutableStack.injectIntoDouble(injectedValue, doubleObjectToDoubleFunction);
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        return this.mutableStack.into(target);
    }

    @Override
    public MutableList<T> toList()
    {
        return this.mutableStack.toList();
    }

    @Override
    public MutableList<T> toSortedList()
    {
        return this.mutableStack.toSortedList();
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.mutableStack.toSortedList(comparator);
    }

    @Override
    public MutableSet<T> toSet()
    {
        return this.mutableStack.toSet();
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        return this.mutableStack.toSortedSet();
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        return this.mutableStack.toSortedSet(comparator);
    }

    @Override
    public ImmutableStack<T> toImmutable()
    {
        return this.mutableStack.toImmutable();
    }

    @Override
    public MutableBag<T> toBag()
    {
        return this.mutableStack.toBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        return this.mutableStack.toSortedBag();
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        return this.mutableStack.toSortedBag(comparator);
    }

    @Override
    public <NK, NV> MutableMap<NK, NV> toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.mutableStack.toMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction, R target)
    {
        return this.mutableStack.toMap(keyFunction, valueFunction, target);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.mutableStack.toSortedMap(keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Comparator<? super NK> comparator, Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.mutableStack.toSortedMap(comparator, keyFunction, valueFunction);
    }

    @Override
    public <NK, NV> MutableBiMap<NK, NV> toBiMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.mutableStack.toBiMap(keyFunction, valueFunction);
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        return this.mutableStack.asLazy();
    }

    @Override
    public Object[] toArray()
    {
        return this.mutableStack.toArray();
    }

    @Override
    public <T2> T2[] toArray(T2[] a)
    {
        return this.mutableStack.toArray(a);
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return this.mutableStack.min(comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return this.mutableStack.max(comparator);
    }

    @Override
    public T min()
    {
        return this.mutableStack.min();
    }

    @Override
    public T max()
    {
        return this.mutableStack.max();
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return this.mutableStack.detectIndex(predicate);
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return this.mutableStack.minBy(function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return this.mutableStack.maxBy(function);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> intFunction)
    {
        return this.mutableStack.sumOfInt(intFunction);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> floatFunction)
    {
        return this.mutableStack.sumOfFloat(floatFunction);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> longFunction)
    {
        return this.mutableStack.sumOfLong(longFunction);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.mutableStack.sumOfDouble(doubleFunction);
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function));
    }

    @Override
    public String makeString()
    {
        return this.mutableStack.makeString();
    }

    @Override
    public String makeString(String separator)
    {
        return this.mutableStack.makeString(separator);
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        return this.mutableStack.makeString(start, separator, end);
    }

    @Override
    public void appendString(Appendable appendable)
    {
        this.mutableStack.appendString(appendable);
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        this.mutableStack.appendString(appendable, separator);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.mutableStack.appendString(appendable, start, separator, end);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.mutableStack.groupBy(function);
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target)
    {
        return this.mutableStack.groupBy(function, target);
    }

    @Override
    public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.mutableStack.groupByEach(function);
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.mutableStack.groupByEach(function, target);
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.mutableStack.groupByUniqueKey(function);
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(Function<? super T, ? extends V> function, R target)
    {
        return this.mutableStack.groupByUniqueKey(function, target);
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.mutableStack.zip(that, target);
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.mutableStack.zipWithIndex(target);
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        return this.mutableStack.chunk(size);
    }

    @Override
    public MutableStack<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.mutableStack.forEach(procedure);
    }

    @Override
    public T peek()
    {
        return this.mutableStack.peek();
    }

    @Override
    public ListIterable<T> peek(int count)
    {
        return this.mutableStack.peek(count);
    }

    @Override
    public T peekAt(int index)
    {
        return this.mutableStack.peekAt(index);
    }

    @Override
    public String toString()
    {
        return this.mutableStack.toString();
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.mutableStack.forEachWithIndex(objectIntProcedure);
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.mutableStack.forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
    }

    @Override
    public <V> MutableStack<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.mutableStack.collectWithIndex(function);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.mutableStack.forEachWith(procedure, parameter);
    }

    @Override
    public MutableStack<T> asUnmodifiable()
    {
        return this;
    }

    @Override
    public MutableStack<T> asSynchronized()
    {
        return this.mutableStack.asSynchronized();
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.mutableStack.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return this.mutableStack.hashCode();
    }

    @Override
    public int indexOf(Object object)
    {
        return this.mutableStack.indexOf(object);
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return this.mutableStack.corresponds(other, predicate);
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        this.mutableStack.forEach(startIndex, endIndex, procedure);
    }

    @Override
    public MutableStack<T> takeWhile(Predicate<? super T> predicate)
    {
        return this.mutableStack.takeWhile(predicate);
    }

    @Override
    public MutableStack<T> dropWhile(Predicate<? super T> predicate)
    {
        return this.mutableStack.dropWhile(predicate);
    }

    @Override
    public PartitionMutableStack<T> partitionWhile(Predicate<? super T> predicate)
    {
        return this.mutableStack.partitionWhile(predicate);
    }

    @Override
    public MutableStack<T> distinct()
    {
        return this.mutableStack.distinct();
    }
}
