/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.concurrent.ExecutorService;
import java.util.function.UnaryOperator;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
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
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.factory.primitive.ByteLists;
import org.eclipse.collections.api.factory.primitive.CharLists;
import org.eclipse.collections.api.factory.primitive.DoubleLists;
import org.eclipse.collections.api.factory.primitive.FloatLists;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.factory.primitive.ShortLists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.ParallelListIterable;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.block.procedure.CollectIfProcedure;
import org.eclipse.collections.impl.block.procedure.CollectProcedure;
import org.eclipse.collections.impl.block.procedure.FlatCollectProcedure;
import org.eclipse.collections.impl.block.procedure.RejectProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.block.procedure.SelectProcedure;
import org.eclipse.collections.impl.collection.immutable.AbstractImmutableCollection;
import org.eclipse.collections.impl.lazy.ReverseIterable;
import org.eclipse.collections.impl.lazy.parallel.list.ListIterableParallelIterable;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.eclipse.collections.impl.utility.OrderedIterate;

/**
 * This class is the parent class for all ImmutableLists. All implementations of ImmutableList must implement the List
 * interface so anArrayList.equals(anImmutableList) can return true when the contents and order are the same.
 */
abstract class AbstractImmutableList<T>
        extends AbstractImmutableCollection<T>
        implements ImmutableList<T>, List<T>
{
    @Override
    public List<T> castToList()
    {
        return this;
    }

    @Override
    public boolean equals(Object that)
    {
        if (that == this)
        {
            return true;
        }
        if (!(that instanceof List))
        {
            return false;
        }
        List<?> list = (List<?>) that;
        if (this.size() != list.size())
        {
            return false;
        }
        if (list instanceof RandomAccess)
        {
            return this.randomAccessListEquals(list);
        }
        return this.regularListEquals(list);
    }

    private boolean randomAccessListEquals(List<?> list)
    {
        int localSize = this.size();
        for (int i = 0; i < localSize; i++)
        {
            if (!Objects.equals(this.get(i), list.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    protected boolean regularListEquals(List<?> list)
    {
        Iterator<?> iterator = list.iterator();
        int localSize = this.size();
        for (int i = 0; i < localSize; i++)
        {
            if (!iterator.hasNext())
            {
                return false;
            }
            if (!Objects.equals(this.get(i), iterator.next()))
            {
                return false;
            }
        }
        return !iterator.hasNext();
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        int localSize = this.size();
        for (int i = 0; i < localSize; i++)
        {
            T item = this.get(i);
            hashCode = 31 * hashCode + (item == null ? 0 : item.hashCode());
        }
        return hashCode;
    }

    @Override
    public ImmutableList<T> newWithout(T element)
    {
        int indexToRemove = this.indexOf(element);
        if (indexToRemove < 0)
        {
            return this;
        }
        T[] results = (T[]) new Object[this.size() - 1];
        int currentIndex = 0;
        for (int i = 0; i < this.size(); i++)
        {
            T item = this.get(i);
            if (i != indexToRemove)
            {
                results[currentIndex++] = item;
            }
        }
        return Lists.immutable.with(results);
    }

    @Override
    public ImmutableList<T> newWithAll(Iterable<? extends T> elements)
    {
        int oldSize = this.size();
        int newSize = Iterate.sizeOf(elements);
        T[] array = (T[]) new Object[oldSize + newSize];
        this.toArray(array);
        Iterate.forEachWithIndex(elements, (each, index) -> array[oldSize + index] = each);
        return Lists.immutable.with(array);
    }

    @Override
    public ImmutableList<T> newWithoutAll(Iterable<? extends T> elements)
    {
        MutableList<T> result = FastList.newListWith((T[]) this.toArray());
        this.removeAllFrom(elements, result);
        return result.toImmutable();
    }

    @Override
    public T getFirst()
    {
        return this.isEmpty() ? null : this.get(0);
    }

    @Override
    public T getLast()
    {
        return this.isEmpty() ? null : this.get(this.size() - 1);
    }

    @Override
    public ImmutableList<T> select(Predicate<? super T> predicate)
    {
        MutableList<T> result = Lists.mutable.empty();
        this.forEach(new SelectProcedure<>(predicate, result));
        return result.toImmutable();
    }

    @Override
    public <P> ImmutableList<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.selectWith(this, predicate, parameter, FastList.newList()).toImmutable();
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return ListIterate.selectWith(this, predicate, parameter, target);
    }

    @Override
    public ImmutableList<T> reject(Predicate<? super T> predicate)
    {
        MutableList<T> result = Lists.mutable.empty();
        this.forEach(new RejectProcedure<>(predicate, result));
        return result.toImmutable();
    }

    @Override
    public <P> ImmutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.rejectWith(this, predicate, parameter, FastList.newList()).toImmutable();
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return ListIterate.rejectWith(this, predicate, parameter, target);
    }

    @Override
    public PartitionImmutableList<T> partition(Predicate<? super T> predicate)
    {
        return ListIterate.partition(this, predicate).toImmutable();
    }

    @Override
    public <P> PartitionImmutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.partitionWith(this, predicate, parameter).toImmutable();
    }

    @Override
    public <S> ImmutableList<S> selectInstancesOf(Class<S> clazz)
    {
        MutableList<S> result = FastList.newList(this.size());
        this.forEach(new SelectInstancesOfProcedure<>(clazz, result));
        return result.toImmutable();
    }

    @Override
    public <V> ImmutableList<V> collect(Function<? super T, ? extends V> function)
    {
        MutableList<V> result = Lists.mutable.empty();
        this.forEach(new CollectProcedure<>(function, result));
        return result.toImmutable();
    }

    @Override
    public ImmutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        MutableBooleanList result = BooleanLists.mutable.withInitialCapacity(this.size());
        return this.collectBoolean(booleanFunction, result).toImmutable();
    }

    @Override
    public ImmutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        MutableByteList result = ByteLists.mutable.withInitialCapacity(this.size());
        return this.collectByte(byteFunction, result).toImmutable();
    }

    @Override
    public ImmutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        MutableCharList result = CharLists.mutable.withInitialCapacity(this.size());
        return this.collectChar(charFunction, result).toImmutable();
    }

    @Override
    public ImmutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        MutableDoubleList result = DoubleLists.mutable.withInitialCapacity(this.size());
        return this.collectDouble(doubleFunction, result).toImmutable();
    }

    @Override
    public ImmutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        MutableFloatList result = FloatLists.mutable.withInitialCapacity(this.size());
        return this.collectFloat(floatFunction, result).toImmutable();
    }

    @Override
    public ImmutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        MutableIntList result = IntLists.mutable.withInitialCapacity(this.size());
        return this.collectInt(intFunction, result).toImmutable();
    }

    @Override
    public ImmutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        MutableLongList result = LongLists.mutable.withInitialCapacity(this.size());
        return this.collectLong(longFunction, result).toImmutable();
    }

    @Override
    public ImmutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        MutableShortList result = ShortLists.mutable.withInitialCapacity(this.size());
        return this.collectShort(shortFunction, result).toImmutable();
    }

    @Override
    public <P, V> ImmutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    @Override
    public <V> ImmutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        MutableList<V> result = Lists.mutable.empty();
        this.forEach(new CollectIfProcedure<>(result, function, predicate));
        return result.toImmutable();
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function, P parameter, R target)
    {
        return ListIterate.collectWith(this, function, parameter, target);
    }

    @Override
    public <V> ImmutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        MutableList<V> result = Lists.mutable.empty();
        this.forEach(new FlatCollectProcedure<>(function, result));
        return result.toImmutable();
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(
            Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return ListIterate.flatCollect(this, function, target);
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return ListIterate.detectIndex(this, predicate);
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        return ListIterate.detectLastIndex(this, predicate);
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        int count = 0;
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(this.get(i)))
            {
                count++;
            }
        }
        return count;
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        return ListIterate.sumOfInt(this, function);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        return ListIterate.sumOfLong(this, function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        return ListIterate.sumOfFloat(this, function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        return ListIterate.sumOfDouble(this, function);
    }

    @Override
    public ImmutableList<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return OrderedIterate.corresponds(this, other, predicate);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        int localSize = this.size();
        for (int i = 0; i < localSize; i++)
        {
            T each = this.get(i);
            objectIntProcedure.value(each, i);
        }
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        int localSize = this.size();
        for (int i = 0; i < localSize; i++)
        {
            T each = this.get(i);
            procedure.value(each, parameter);
        }
    }

    @Override
    public void forEach(
            int from, int to, Procedure<? super T> procedure)
    {
        ListIterate.rangeCheck(from, to, this.size());

        if (from <= to)
        {
            for (int i = from; i <= to; i++)
            {
                procedure.value(this.get(i));
            }
        }
        else
        {
            for (int i = from; i >= to; i--)
            {
                procedure.value(this.get(i));
            }
        }
    }

    @Override
    public void forEachWithIndex(
            int from, int to, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(from, to, this.size());

        if (from <= to)
        {
            for (int i = from; i <= to; i++)
            {
                objectIntProcedure.value(this.get(i), i);
            }
        }
        else
        {
            for (int i = from; i >= to; i--)
            {
                objectIntProcedure.value(this.get(i), i);
            }
        }
    }

    @Override
    public int indexOf(Object object)
    {
        int n = this.size();
        if (object == null)
        {
            for (int i = 0; i < n; i++)
            {
                if (this.get(i) == null)
                {
                    return i;
                }
            }
        }
        else
        {
            for (int i = 0; i < n; i++)
            {
                if (object.equals(this.get(i)))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object object)
    {
        int n = this.size() - 1;
        if (object == null)
        {
            for (int i = n; i >= 0; i--)
            {
                if (this.get(i) == null)
                {
                    return i;
                }
            }
        }
        else
        {
            for (int i = n; i >= 0; i--)
            {
                if (object.equals(this.get(i)))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public Iterator<T> iterator()
    {
        return this.listIterator(0);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public T set(int index, T element)
    {
        throw new UnsupportedOperationException("Cannot call set() on " + this.getClass().getSimpleName());
    }

    @Override
    public void add(int index, T element)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }

    @Override
    public T remove(int index)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    /**
     * @since 10.0 - Override for correctness
     */
    @Override
    public void replaceAll(UnaryOperator<T> operator)
    {
        throw new UnsupportedOperationException("Cannot call replaceAll() on " + this.getClass().getSimpleName());
    }

    /**
     * @since 10.0 - Override for correctness
     */
    @Override
    public void sort(Comparator<? super T> c)
    {
        throw new UnsupportedOperationException("Cannot call sort() on " + this.getClass().getSimpleName());
    }

    @Override
    public ListIterator<T> listIterator()
    {
        return new ImmutableListIterator<>(this, 0);
    }

    @Override
    public ListIterator<T> listIterator(int index)
    {
        if (index < 0 || index > this.size())
        {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        return new ImmutableListIterator<>(this, index);
    }

    @Override
    public AbstractImmutableList<T> subList(int fromIndex, int toIndex)
    {
        return new ImmutableSubList<>(this, fromIndex, toIndex);
    }

    @Override
    public ImmutableList<T> distinct()
    {
        return ListIterate.distinct(this.castToList()).toImmutable();
    }

    @Override
    public ImmutableList<T> distinct(HashingStrategy<? super T> hashingStrategy)
    {
        return ListIterate.distinct(this.castToList(), hashingStrategy).toImmutable();
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> ImmutableList<T> distinctBy(Function<? super T, ? extends V> function)
    {
        return this.distinct(HashingStrategies.fromFunction(function));
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        ListIterate.appendString(this, appendable, start, separator, end);
    }

    @Override
    public <V> ImmutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, FastListMultimap.<V, T>newMultimap()).toImmutable();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        return ListIterate.groupBy(this, function, target);
    }

    @Override
    public <V> ImmutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, FastListMultimap.newMultimap()).toImmutable();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return ListIterate.groupByEach(this, function, target);
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return ListIterate.min(this, comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return ListIterate.max(this, comparator);
    }

    @Override
    public T min()
    {
        return ListIterate.min(this);
    }

    @Override
    public T max()
    {
        return ListIterate.max(this);
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return ListIterate.minBy(this, function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return ListIterate.maxBy(this, function);
    }

    @Override
    public <S> ImmutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        if (that instanceof Collection || that instanceof RichIterable)
        {
            int thatSize = Iterate.sizeOf(that);
            MutableList<Pair<T, S>> target = FastList.newList(Math.min(this.size(), thatSize));
            return this.zip(that, target).toImmutable();
        }
        return this.zip(that, FastList.newList()).toImmutable();
    }

    @Override
    public ImmutableList<Pair<T, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(Lists.mutable.withInitialCapacity(this.size())).toImmutable();
    }

    @Override
    public ImmutableList<T> take(int count)
    {
        if (count >= this.size())
        {
            return this;
        }
        return ListIterate.take(this, count).toImmutable();
    }

    @Override
    public ImmutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        return ListIterate.takeWhile(this, predicate).toImmutable();
    }

    @Override
    public ImmutableList<T> drop(int count)
    {
        if (count == 0)
        {
            return this;
        }
        return ListIterate.drop(this, count).toImmutable();
    }

    @Override
    public ImmutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        return ListIterate.dropWhile(this, predicate).toImmutable();
    }

    @Override
    public PartitionImmutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        return ListIterate.partitionWhile(this, predicate).toImmutable();
    }

    @Override
    protected MutableCollection<T> newMutable(int size)
    {
        return Lists.mutable.withInitialCapacity(size);
    }

    @Override
    public AbstractImmutableList<T> reversed()
    {
        if (this.isEmpty())
        {
            return this;
        }
        return new ReversedImmutableList<>(this);
    }

    @Override
    public ReverseIterable<T> asReversed()
    {
        return ReverseIterable.adapt(this);
    }

    @Override
    public ImmutableList<T> toReversed()
    {
        return Lists.immutable.withAll(this.asReversed());
    }

    @Override
    public ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new ListIterableParallelIterable<>(this, executorService, batchSize);
    }

    @Override
    public ImmutableList<T> toImmutable()
    {
        return this;
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }

        if (this.isEmpty())
        {
            return Lists.immutable.empty();
        }
        MutableList<RichIterable<T>> result = Lists.mutable.empty();
        if (this.size() <= size)
        {
            result.add(this);
        }
        else
        {
            int endIndex = size;
            for (int startIndex = 0;
                    endIndex <= this.size() && startIndex < this.size();
                    startIndex += size, endIndex += Math.min(size, this.size() - endIndex))
            {
                result.add(new ImmutableSubList<>(this, startIndex, endIndex));
            }
        }

        return result.toImmutable();
    }

    protected static class ImmutableSubList<T>
            extends AbstractImmutableList<T>
            implements Serializable, RandomAccess
    {
        // Not important since it uses writeReplace()
        private static final long serialVersionUID = 1L;

        private final ImmutableList<T> original;
        private final int offset;
        private final int size;

        protected ImmutableSubList(ImmutableList<T> list, int fromIndex, int toIndex)
        {
            if (fromIndex < 0)
            {
                throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
            }
            if (toIndex > list.size())
            {
                throw new IndexOutOfBoundsException("toIndex = " + toIndex);
            }
            if (fromIndex > toIndex)
            {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ')');
            }
            this.original = list;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
        }

        @Override
        public T get(int index)
        {
            this.checkIfOutOfBounds(index);
            return this.original.get(index + this.offset);
        }

        @Override
        public int size()
        {
            return this.size;
        }

        @Override
        public ImmutableList<T> newWith(T newItem)
        {
            int oldSize = this.size();
            T[] array = (T[]) new Object[oldSize + 1];
            this.toArray(array);
            array[oldSize] = newItem;
            return Lists.immutable.with(array);
        }

        protected Object writeReplace()
        {
            return Lists.immutable.withAll(this);
        }

        @Override
        public ImmutableSubList<T> subList(int fromIndex, int toIndex)
        {
            if (fromIndex < 0)
            {
                throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
            }
            if (toIndex > this.size())
            {
                throw new IndexOutOfBoundsException("toIndex = " + toIndex);
            }
            if (fromIndex > toIndex)
            {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ')');
            }

            return new ImmutableSubList<>(this.original, this.offset + fromIndex, this.offset + toIndex);
        }

        private void checkIfOutOfBounds(int index)
        {
            if (index >= this.size || index < 0)
            {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size);
            }
        }

        @Override
        public T getFirst()
        {
            return this.isEmpty() ? null : this.original.get(this.offset);
        }

        @Override
        public T getLast()
        {
            return this.isEmpty() ? null : this.original.get(this.offset + this.size - 1);
        }

        @Override
        public MutableStack<T> toStack()
        {
            return ArrayStack.newStack(this);
        }

        @Override
        public void each(Procedure<? super T> procedure)
        {
            ListIterate.forEach(this, procedure);
        }

        @Override
        public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
        {
            ListIterate.forEachWithIndex(this, objectIntProcedure);
        }

        @Override
        public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
        {
            ListIterate.forEachWith(this, procedure, parameter);
        }
    }
}
