/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.function.UnaryOperator;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.ParallelListIterable;
import org.eclipse.collections.impl.AbstractRichIterable;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.lazy.parallel.list.NonParallelListIterable;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * CompositeFastList behaves like a list, but is composed of at least one list.
 * It is useful where you don't want the additional expense of appending several lists or allocating memory
 * for a super list to add multiple sublists to.<p>
 * <b>Note:</b> mutation operations (e.g. add and remove, sorting) will change the underlying
 * lists - so be sure to only use a composite list where it will be the only reference to the sublists
 * (for example, a composite list which contains multiple query results is OK as long
 * as it is the only thing that references the lists)
 */
public final class CompositeFastList<E>
        extends AbstractMutableList<E>
        implements BatchIterable<E>, Serializable
{
    private static final Predicate2<FastList<?>, Object> REMOVE_PREDICATE = FastList::remove;
    private static final Procedure<FastList<?>> REVERSE_LIST_PROCEDURE = FastList::reverseThis;

    private static final long serialVersionUID = 2L;
    private final FastList<FastList<E>> lists = FastList.newList();
    private int size;

    @Override
    public MutableList<E> clone()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".clone() not implemented yet");
    }

    @Override
    public int size()
    {
        return this.size;
    }

    public void resetSize()
    {
        int newSize = 0;
        for (int i = this.lists.size() - 1; i >= 0; i--)
        {
            newSize += this.lists.get(i).size();
        }
        this.size = newSize;
    }

    @Override
    public void batchForEach(Procedure<? super E> procedure, int sectionIndex, int sectionCount)
    {
        if (this.lists.size() == 1)
        {
            this.lists.get(0).batchForEach(procedure, sectionIndex, sectionCount);
        }
        else
        {
            this.lists.get(sectionIndex).batchForEach(procedure, 0, 1);
        }
    }

    @Override
    public int getBatchCount(int batchSize)
    {
        if (this.lists.size() == 1)
        {
            return this.lists.get(0).getBatchCount(batchSize);
        }
        return this.lists.size();
    }

    @Override
    public CompositeFastList<E> reverseThis()
    {
        ParallelIterate.forEach(this.lists, REVERSE_LIST_PROCEDURE);
        this.lists.reverseThis();
        return this;
    }

    @Override
    public void each(Procedure<? super E> procedure)
    {
        this.lists.each(list -> list.forEach(procedure));
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super E, ? extends IV> function)
    {
        return this.lists.injectInto(injectedValue, (Function2<IV, FastList<E>, IV>) (inject, list) -> list.injectInto(inject, function));
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super E> function)
    {
        return this.lists.injectInto(injectedValue, (IntObjectToIntFunction<FastList<E>>) (inject, list) -> list.injectInto(inject, function));
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super E> function)
    {
        return this.lists.injectInto(injectedValue, (FloatObjectToFloatFunction<FastList<E>>) (inject, list) -> list.injectInto(inject, function));
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super E> function)
    {
        return this.lists.injectInto(injectedValue, (LongObjectToLongFunction<FastList<E>>) (inject, list) -> list.injectInto(inject, function));
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super E> function)
    {
        return this.lists.injectInto(injectedValue, (DoubleObjectToDoubleFunction<FastList<E>>) (inject, list) -> list.injectInto(inject, function));
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super E> objectIntProcedure)
    {
        this.lists.forEach(new ProcedureToInnerListObjectIntProcedure<>(objectIntProcedure));
    }

    @Override
    public void reverseForEach(Procedure<? super E> procedure)
    {
        this.lists.reverseForEach(each -> each.reverseForEach(procedure));
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super E> procedure)
    {
        this.lists.reverseForEach(new ProcedureToReverseInnerListObjectIntProcedure<>(procedure, this.size - 1));
    }

    @Override
    public <P> void forEachWith(
            Procedure2<? super E, ? super P> procedure2,
            P parameter)
    {
        this.lists.each(list -> list.forEachWith(procedure2, parameter));
    }

    @Override
    public boolean isEmpty()
    {
        return this.lists.allSatisfy(AbstractRichIterable::isEmpty);
    }

    @Override
    public boolean contains(Object object)
    {
        return this.lists.anySatisfy(list -> list.contains(object));
    }

    @Override
    public Iterator<E> iterator()
    {
        if (this.lists.isEmpty())
        {
            return Collections.<E>emptyList().iterator();
        }
        return new CompositeIterator(this.lists);
    }

    @Override
    public Object[] toArray()
    {
        Object[] result = new Object[this.size()];
        this.forEachWithIndex((each, index) -> result[index] = each);
        return result;
    }

    @Override
    public boolean add(E object)
    {
        if (this.lists.isEmpty())
        {
            this.addComposited(FastList.newList());
        }
        Collection<E> list = this.lists.getLast();
        this.size++;
        return list.add(object);
    }

    @Override
    public boolean remove(Object object)
    {
        boolean removed = this.lists.anySatisfyWith(REMOVE_PREDICATE, object);
        if (removed)
        {
            this.size--;
        }
        return removed;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection)
    {
        if (collection.isEmpty())
        {
            return false;
        }
        Collection<? extends E> collectionToAdd =
                collection instanceof FastList ? collection : FastList.newList(collection);
        this.addComposited(collectionToAdd);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        return this.containsAllIterable(collection);
    }

    @Override
    public Object[] toArray(Object[] array)
    {
        int size = this.size();
        Object[] result = array.length >= size
                ? array
                : (Object[]) Array.newInstance(array.getClass().getComponentType(), size);

        this.forEachWithIndex((each, index) -> result[index] = each);

        if (result.length > size)
        {
            result[size] = null;
        }
        return result;
    }

    public void addComposited(Collection<? extends E> collection)
    {
        if (!(collection instanceof FastList))
        {
            throw new IllegalArgumentException("CompositeFastList can only add FastLists");
        }
        this.size += collection.size();
        this.lists.add((FastList<E>) collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".addAll(index, collection) not implemented yet");
    }

    @Override
    public void clear()
    {
        this.lists.each(FastList::clear);
        this.size = 0;
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        boolean changed = false;
        for (int i = this.lists.size() - 1; i >= 0; i--)
        {
            changed = this.lists.get(i).retainAll(collection) || changed;
        }
        if (changed)
        {
            this.resetSize();
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        if (collection.isEmpty())
        {
            return false;
        }
        boolean changed = false;
        for (int i = this.lists.size() - 1; i >= 0; i--)
        {
            changed = this.lists.get(i).removeAll(collection) || changed;
        }
        if (changed)
        {
            this.resetSize();
        }
        return changed;
    }

    @Override
    public E get(int index)
    {
        this.rangeCheck(index);
        int p = 0;
        int currentSize = this.lists.getFirst().size();
        while (index >= currentSize)
        {
            index -= currentSize;
            currentSize = this.lists.get(++p).size();
        }
        return this.lists.get(p).items[index];
    }

    private void rangeCheck(int index)
    {
        if (index >= this.size())
        {
            throw new IndexOutOfBoundsException("No such element " + index + " size: " + this.size());
        }
    }

    @Override
    public E set(int index, E element)
    {
        this.rangeCheck(index);
        int p = 0;
        int currentSize = this.lists.getFirst().size();
        while (index >= currentSize)
        {
            index -= currentSize;
            currentSize = this.lists.get(++p).size();
        }
        return this.lists.get(p).set(index, element);
    }

    @Override
    public void add(int index, E element)
    {
        int localSize = this.size();
        if (index > localSize || index < 0)
        {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + localSize);
        }
        int max = 0;
        for (int i = 0; i < this.lists.size(); i++)
        {
            List<E> list = this.lists.get(i);
            int previousMax = max;
            max += list.size();
            if (index <= max)
            {
                list.add(index - previousMax, element);
                this.size++;
                return;
            }
        }
    }

    @Override
    public E remove(int index)
    {
        this.rangeCheck(index);
        int p = 0;
        int currentSize = this.lists.getFirst().size();
        while (index >= currentSize)
        {
            index -= currentSize;
            currentSize = this.lists.get(++p).size();
        }
        this.size--;
        return this.lists.get(p).remove(index);
    }

    @Override
    public int indexOf(Object o)
    {
        int offset = 0;
        int listsSize = this.lists.size();
        for (int i = 0; i < listsSize; i++)
        {
            MutableList<E> list = this.lists.get(i);
            int index = list.indexOf(o);
            if (index > -1)
            {
                return index + offset;
            }
            offset += list.size();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o)
    {
        int offset = this.size();
        for (int i = this.lists.size() - 1; i >= 0; i--)
        {
            MutableList<E> list = this.lists.get(i);
            offset -= list.size();
            int index = list.lastIndexOf(o);
            if (index > -1)
            {
                return index + offset;
            }
        }
        return -1;
    }

    /**
     * @since 10.0
     */
    @Override
    public void replaceAll(UnaryOperator<E> operator)
    {
        this.lists.forEachWith(List::replaceAll, operator);
    }

    @Override
    public void sort(Comparator<? super E> comparator)
    {
        FastList<E> list = comparator == null
                ? (FastList<E>) this.toSortedList()
                : (FastList<E>) this.toSortedList(comparator);
        this.lists.clear();
        this.lists.add(list);
    }

    /**
     * a list iterator is a problem for a composite list as going back in the order of the list is an issue,
     * as are the other methods like set() and add() (and especially, remove).
     * Convert the internal lists to one list (if not already just one list)
     * and return that list's list iterator.
     * <p>
     * AFAIK list iterator is only commonly used in sorting.
     *
     * @return a ListIterator for this, with internal state converted to one list if needed.
     */
    @Override
    public ListIterator<E> listIterator()
    {
        return this.listIterator(0);
    }

    /**
     * a list iterator is a problem for a composite list as going back in the order of the list is an issue,
     * as are the other methods like set() and add() (and especially, remove).
     * Convert the internal lists to one list (if not already just one list)
     * and return that list's list iterator.
     * <p>
     * AFAIK list iterator is only commonly used in sorting.
     *
     * @return a ListIterator for this, with internal state converted to one list if needed.
     */
    @Override
    public ListIterator<E> listIterator(int index)
    {
        if (this.lists.size() > 1 || this.lists.isEmpty())
        {
            this.flattenLists();
        }
        return super.listIterator(index);
    }

    @Override
    public int count(Predicate<? super E> predicate)
    {
        int count = 0;
        int localSize = this.lists.size();
        for (int i = 0; i < localSize; i++)
        {
            count += this.lists.get(i).count(predicate);
        }
        return count;
    }

    @Override
    public <P> int countWith(Predicate2<? super E, ? super P> predicate, P parameter)
    {
        int count = 0;
        int localSize = this.lists.size();
        for (int i = 0; i < localSize; i++)
        {
            count += this.lists.get(i).countWith(predicate, parameter);
        }
        return count;
    }

    @Override
    public boolean anySatisfy(Predicate<? super E> predicate)
    {
        return this.lists.anySatisfy(each -> each.anySatisfy(predicate));
    }

    @Override
    public <R extends Collection<E>> R select(Predicate<? super E> predicate, R target)
    {
        int localSize = this.lists.size();
        for (int i = 0; i < localSize; i++)
        {
            this.lists.get(i).select(predicate, target);
        }
        return target;
    }

    @Override
    public <P, R extends Collection<E>> R selectWith(Predicate2<? super E, ? super P> predicate, P parameter, R target)
    {
        int localSize = this.lists.size();
        for (int i = 0; i < localSize; i++)
        {
            this.lists.get(i).selectWith(predicate, parameter, target);
        }
        return target;
    }

    @Override
    public <R extends Collection<E>> R reject(Predicate<? super E> predicate, R target)
    {
        int localSize = this.lists.size();
        for (int i = 0; i < localSize; i++)
        {
            this.lists.get(i).reject(predicate, target);
        }
        return target;
    }

    @Override
    public <P, R extends Collection<E>> R rejectWith(Predicate2<? super E, ? super P> predicate, P parameter, R target)
    {
        int localSize = this.lists.size();
        for (int i = 0; i < localSize; i++)
        {
            this.lists.get(i).rejectWith(predicate, parameter, target);
        }
        return target;
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super E, ? extends V> function, R target)
    {
        int localSize = this.lists.size();
        for (int i = 0; i < localSize; i++)
        {
            this.lists.get(i).collect(function, target);
        }
        return target;
    }

    @Override
    public <P, A, R extends Collection<A>> R collectWith(Function2<? super E, ? super P, ? extends A> function, P parameter, R target)
    {
        int localSize = this.lists.size();
        for (int i = 0; i < localSize; i++)
        {
            this.lists.get(i).collectWith(function, parameter, target);
        }
        return target;
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super E, ? super P> predicate, P parameter)
    {
        return this.lists.anySatisfyWith((each, parm) -> each.anySatisfyWith(predicate, parm), parameter);
    }

    @Override
    public boolean allSatisfy(Predicate<? super E> predicate)
    {
        return this.lists.allSatisfy(each -> each.allSatisfy(predicate));
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super E, ? super P> predicate, P parameter)
    {
        return this.lists.allSatisfyWith((each, param) -> each.allSatisfyWith(predicate, param), parameter);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super E> predicate)
    {
        return this.lists.allSatisfy(each -> each.noneSatisfy(predicate));
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super E, ? super P> predicate, P parameter)
    {
        return this.lists.allSatisfyWith((each, param) -> each.noneSatisfyWith(predicate, param), parameter);
    }

    /**
     * convert multiple contained lists into one list and replace the contained lists with that list.
     * Synchronize to prevent changes to this list whilst this process is happening
     */
    private void flattenLists()
    {
        FastList<E> list = (FastList<E>) this.toList();
        this.lists.clear();
        this.lists.add(list);
    }

    private final class CompositeIterator
            implements Iterator<E>
    {
        private final Iterator<E>[] iterators;
        private Iterator<E> currentIterator;
        private int currentIndex;

        private CompositeIterator(MutableList<FastList<E>> newLists)
        {
            this.iterators = new Iterator[newLists.size()];
            for (int i = 0; i < newLists.size(); ++i)
            {
                this.iterators[i] = newLists.get(i).iterator();
            }
            this.currentIterator = this.iterators[0];
            this.currentIndex = 0;
        }

        @Override
        public boolean hasNext()
        {
            if (this.currentIterator.hasNext())
            {
                return true;
            }
            if (this.currentIndex < this.iterators.length - 1)
            {
                this.currentIterator = this.iterators[++this.currentIndex];
                return this.hasNext();
            }
            return false;
        }

        @Override
        public E next()
        {
            if (this.currentIterator.hasNext())
            {
                return this.currentIterator.next();
            }
            if (this.currentIndex < this.iterators.length - 1)
            {
                this.currentIterator = this.iterators[++this.currentIndex];
                return this.next();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove()
        {
            CompositeFastList.this.size--;
            this.currentIterator.remove();
        }
    }

    private static final class ProcedureToInnerListObjectIntProcedure<E> implements Procedure<FastList<E>>
    {
        private static final long serialVersionUID = 1L;

        private int index;
        private final ObjectIntProcedure<? super E> objectIntProcedure;

        private ProcedureToInnerListObjectIntProcedure(ObjectIntProcedure<? super E> objectIntProcedure)
        {
            this.objectIntProcedure = objectIntProcedure;
        }

        @Override
        public void value(FastList<E> list)
        {
            list.each(object ->
            {
                this.objectIntProcedure.value(
                        object,
                        this.index);
                this.index++;
            });
        }
    }

    private static final class ProcedureToReverseInnerListObjectIntProcedure<E> implements Procedure<FastList<E>>
    {
        private static final long serialVersionUID = 1L;

        private int index;
        private final ObjectIntProcedure<? super E> objectIntProcedure;

        private ProcedureToReverseInnerListObjectIntProcedure(ObjectIntProcedure<? super E> objectIntProcedure, int size)
        {
            this.objectIntProcedure = objectIntProcedure;
            this.index = size;
        }

        @Override
        public void value(FastList<E> list)
        {
            list.reverseForEach(object ->
            {
                this.objectIntProcedure.value(
                        object,
                        this.index);
                this.index--;
            });
        }
    }

    @Override
    public ParallelListIterable<E> asParallel(ExecutorService executorService, int batchSize)
    {
        return new NonParallelListIterable<>(this);
    }
}
