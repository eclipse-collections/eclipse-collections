/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MultiReaderBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.factory.primitive.IntPredicates;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.eclipse.collections.impl.set.mutable.MultiReaderMutableCollectionTestCase;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link MultiReaderHashBag}.
 */
public class MultiReaderHashBagTest extends MultiReaderMutableCollectionTestCase
{
    @Override
    protected <T> MultiReaderHashBag<T> newWith(T... littleElements)
    {
        return MultiReaderHashBag.newBagWith(littleElements);
    }

    @Override
    @Test
    public void largeCollectionStreamToBagMultimap()
    {
        assertThrows(UnsupportedOperationException.class, () -> super.largeCollectionStreamToBagMultimap());
    }

    @Override
    @Test
    public void newEmpty()
    {
        Verify.assertInstanceOf(MultiReaderHashBag.class, MultiReaderHashBag.newBag().newEmpty());
        Verify.assertEmpty(MultiReaderHashBag.<Integer>newBagWith(null, null).newEmpty());
    }

    @Test
    public void hashBagNewWith()
    {
        assertEquals(
                HashBag.newBagWith("Alice", "Bob", "Bob", "Bob", "Cooper", "Dio"),
                HashBag.newBagWith("Alice", "Bob", "Cooper", "Dio", "Bob", "Bob"));
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedBag.class, MultiReaderHashBag.newBag().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableBag.class, this.newWith().asUnmodifiable());
    }

    @Override
    @Test
    public void toImmutable()
    {
        Verify.assertInstanceOf(ImmutableBag.class, this.newWith().toImmutable());
    }

    @Test
    public void addOccurrences()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 3);
        assertEquals(2, bag.addOccurrences(1, 0));
        assertEquals(4, bag.addOccurrences(1, 2));
        assertEquals(0, bag.addOccurrences(4, 0));
        assertEquals(2, bag.addOccurrences(4, 2));
        assertEquals(2, bag.addOccurrences(2, 1));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 3, 4, 4), bag);
        assertEquals(3, bag.addOccurrences(2, 1));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 2, 3, 4, 4), bag);
    }

    @Test
    public void withOccurrences()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 3);
        MultiReaderBag<Integer> withOccurrences = bag.withOccurrences(1, 0);

        assertSame(bag, withOccurrences);
        assertEquals(HashBag.newBagWith(1, 1, 2, 3), withOccurrences);
        assertEquals(HashBag.newBagWith(1, 1, 1, 1, 2, 3), bag.withOccurrences(1, 2));
        assertEquals(HashBag.newBagWith(1, 1, 1, 1, 2, 3), bag.withOccurrences(4, 0));
        assertEquals(HashBag.newBagWith(1, 1, 1, 1, 2, 3, 4, 4), bag.withOccurrences(4, 2));
        assertEquals(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 3, 4, 4), bag.withOccurrences(2, 1));
        assertEquals(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 2, 3, 4, 4), bag.withOccurrences(2, 1));
    }

    @Test
    public void addOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().addOccurrences(new Object(), -1));
    }

    @Test
    public void removeOccurrences()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 1, 1, 2, 2, 3);
        assertFalse(bag.removeOccurrences(4, 2));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 3), bag);

        bag.removeOccurrences(1, 3);
        bag.removeOccurrences(3, 1);
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 2, 2), bag);
    }

    @Test
    public void withoutOccurrences()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 1, 1, 2, 2, 3);
        MultiReaderBag<Integer> withoutOccurrences = bag.withoutOccurrences(4, 2);

        assertSame(bag, withoutOccurrences);
        assertEquals(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 3), withoutOccurrences);

        bag.withoutOccurrences(1, 3);
        assertEquals(HashBag.newBagWith(1, 2, 2), bag.withoutOccurrences(3, 1));
    }

    @Test
    public void setOccurrences()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2);

        assertFalse(bag.setOccurrences(1, 2));
        assertTrue(bag.setOccurrences(3, 3));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 3, 3, 3), bag);
        assertTrue(bag.setOccurrences(2, 0));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 3, 3, 3), bag);
    }

    @Test
    public void occurrencesOf()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2);
        assertEquals(2, bag.occurrencesOf(1));
        assertEquals(1, bag.occurrencesOf(2));
    }

    @Test
    public void sizeDistinct()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 2, 3);
        assertEquals(3, bag.sizeDistinct());
    }

    @Override
    @Test
    public void collect()
    {
        MutableBag<Boolean> bag = MultiReaderHashBag.newBagWith(Boolean.TRUE, Boolean.FALSE, null);
        MutableBag<String> newCollection = bag.collect(String::valueOf);
        assertEquals(HashBag.newBagWith("true", "false", "null"), newCollection);
    }

    @Override
    @Test
    public void flatCollect()
    {
        MutableBag<Integer> collection = MultiReaderHashBag.newBagWith(1, 1, 2, 3, 4);
        Function<Integer, MutableBag<String>> function = object -> HashBag.newBagWith(String.valueOf(object));

        MutableBagTestCase.assertBagsEqual(
                HashBag.newBagWith("1", "1", "2", "3", "4"),
                collection.flatCollect(function));
    }

    @Override
    @Test
    public void collectIf()
    {
        assertEquals(
                HashBag.newBagWith("1", "1", "2", "3"),
                MultiReaderHashBag.newBagWith(1, 1, 2, 3).collectIf(
                        Integer.class::isInstance,
                        String::valueOf));
        assertEquals(
                HashBag.newBagWith("1", "1"),
                MultiReaderHashBag.newBagWith(1, 1, 2, 3).collectIf(
                        Predicates.lessThan(2),
                        String::valueOf));
    }

    @Override
    @Test
    public void collectWith()
    {
        Function2<Integer, Integer, Integer> addZeroFunction = (each, parameter) -> each + parameter;
        Verify.assertContainsAll(MultiReaderHashBag.newBagWith(1, 1, 2, 3).collectWith(addZeroFunction, 0), 1, 2, 3);
        Verify.assertContainsAll(
                MultiReaderHashBag.newBagWith(1, 1, 2, 3).collectWith(
                        addZeroFunction,
                        0,
                        HashBag.newBag()), 1, 2, 3);
    }

    @Override
    @Test
    public void reject()
    {
        Verify.assertContainsAll(MultiReaderHashBag.newBagWith(1, 1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(MultiReaderHashBag.newBagWith(1, 2, 3, 3, 4).reject(
                Predicates.lessThan(3),
                HashBag.newBag()), 3, 4);
    }

    @Override
    @Test
    public void rejectWith()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 1);
        MutableBag<Integer> results = bag.rejectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertEmpty(results);
    }

    @Override
    @Test
    public void select()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 2, 3, 4, 5, 5, 1);
        MutableBag<Integer> results = bag.select(Integer.valueOf(1)::equals);
        MutableBagTestCase.assertBagsEqual(results, MultiReaderHashBag.newBagWith(1, 1));
    }

    @Override
    @Test
    public void selectWith()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 2);
        MutableBag<Integer> results = bag.selectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertSize(4, results);
    }

    @Test
    public void selectByOccurrences()
    {
        MultiReaderHashBag<Integer> numbers = MultiReaderHashBag.newBagWith(1, 1, 2, 2, 2, 3);
        MutableBag<Integer> results = numbers.selectByOccurrences(IntPredicates.isEven());
        Verify.assertSize(2, results);
        MutableBagTestCase.assertBagsEqual(results, MultiReaderHashBag.newBagWith(1, 1));
        numbers.withReadLockAndDelegate(bag ->
        {
            MutableBag<Integer> results2 = bag.selectByOccurrences(IntPredicates.isEven());
            Verify.assertSize(2, results2);
            MutableBagTestCase.assertBagsEqual(results2, MultiReaderHashBag.newBagWith(1, 1));
        });
    }

    @Test
    public void selectDuplicates()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(0, 1, 1, 2, 2, 2, 3);
        MutableBagTestCase.assertBagsEqual(
                MultiReaderHashBag.newBagWith(1, 1, 2, 2, 2),
                bag.selectDuplicates());
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 2, 2, 3);
        MutableBagTestCase.assertBagsEqual(bag.selectInstancesOf(Integer.class), MultiReaderHashBag.newBagWith(1, 1, 2, 2, 2, 3));
    }

    @Override
    @Test
    public void partition()
    {
        MutableBag<Integer> integers = MultiReaderHashBag.newBagWith(-3, -2, -1, 0, 1, 2, 2, 2, 3, 3, 4, 5);
        PartitionMutableCollection<Integer> result = integers.partition(IntegerPredicates.isEven());
        assertEquals(MultiReaderHashBag.newBagWith(-2, 0, 2, 2, 2, 4), result.getSelected());
        assertEquals(MultiReaderHashBag.newBagWith(-3, -1, 1, 3, 3, 5), result.getRejected());
    }

    @Override
    @Test
    public void partitionWith()
    {
        MutableBag<Integer> integers = MultiReaderHashBag.newBagWith(-3, -2, -1, 0, 1, 2, 2, 2, 3, 3, 4, 5);
        PartitionMutableCollection<Integer> result = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isEven()));
        assertEquals(MultiReaderHashBag.newBagWith(-2, 0, 2, 2, 2, 4), result.getSelected());
        assertEquals(MultiReaderHashBag.newBagWith(-3, -1, 1, 3, 3, 5), result.getRejected());
    }

    @Override
    @Test
    public void with()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 3, 3);
        MutableBag<Integer> bagWith = bag.with(3);
        MutableBagTestCase.assertBagsEqual(MultiReaderHashBag.newBagWith(1, 2, 3, 3, 3), bagWith);
    }

    @Override
    @Test
    public void without()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 3, 3);
        MutableBag<Integer> bagWithout = bag.without(3);
        MutableBagTestCase.assertBagsEqual(MultiReaderHashBag.newBagWith(1, 2, 3), bagWithout);
    }

    @Override
    @Test
    public void withAll()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 3, 3);
        MutableBag<Integer> bagWith = bag.withAll(FastList.newListWith(2, 4, 4));
        MutableBagTestCase.assertBagsEqual(MultiReaderHashBag.newBagWith(1, 2, 2, 3, 3, 4, 4), bagWith);
    }

    @Override
    @Test
    public void withoutAll()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 3, 3, 4);
        MutableBag<Integer> bagWithout = bag.withoutAll(FastList.newListWith(3, 4));
        MutableBagTestCase.assertBagsEqual(MultiReaderHashBag.newBagWith(1, 2), bagWithout);
    }

    @Test
    public void toMapOfItemToCount()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 2, 3, 3, 3);
        assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), bag.toMapOfItemToCount());
    }

    @Test
    public void toStringOfItemToCount()
    {
        assertEquals("{}", MultiReaderHashBag.newBagWith().toStringOfItemToCount());
        assertEquals("{1=3}", MultiReaderHashBag.newBagWith(1, 1, 1).toStringOfItemToCount());
        String actual = MultiReaderHashBag.newBagWith(1, 2, 2).toStringOfItemToCount();
        assertTrue("{1=1, 2=2}".equals(actual) || "{2=2, 1=1}".equals(actual));
    }

    @Test
    public void forEachWithOccurrences()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 2, 3, 3, 3);
        int[] sum = new int[1];
        bag.forEachWithOccurrences((each, occurrences) ->
        {
            if (occurrences > 1)
            {
                sum[0] += each * occurrences;
            }
        });

        assertEquals(13, sum[0]);
    }

    @Test
    public void equalsAndHashCose()
    {
        MutableBag<Integer> integers = MultiReaderHashBag.newBagWith(1, 2, 3);
        MutableBag<Integer> integers2 = MultiReaderHashBag.newBagWith(1, 2, 3);
        MutableBag<Integer> integers3 = MultiReaderHashBag.newBagWith(1, null, 3, 4, 5);
        MutableBag<Integer> integers4 = MultiReaderHashBag.newBagWith(1, null, 3, 4, 5);
        MutableBag<Integer> integers5 = MultiReaderHashBag.newBagWith(1, null, 3);
        MutableBag<Integer> randomAccessList = Bags.mutable.of(1, 2, 3);
        MutableBag<Integer> randomAccessList2 = Bags.mutable.of(2, 3, 4);
        Verify.assertEqualsAndHashCode(integers, integers);
        Verify.assertPostSerializedEqualsAndHashCode(integers);
        Verify.assertEqualsAndHashCode(integers, integers2);
        Verify.assertEqualsAndHashCode(integers, randomAccessList);
        assertNotEquals(integers, integers3);
        assertNotEquals(integers, integers5);
        assertNotEquals(integers, randomAccessList2);
        assertNotEquals(integers, Sets.fixedSize.of());
        Verify.assertEqualsAndHashCode(integers3, integers4);
        assertEquals(integers, integers2);
        assertNotEquals(integers, integers3);
    }

    @Override
    @Test
    public void toSet()
    {
        super.toSet();
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(3, 3, 3, 2, 2, 1);
        assertEquals(UnifiedSet.newSetWith(1, 2, 3), bag.toSet());
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 1);
        assertEquals(FastList.newListWith(1, 1, 1), bag.toList());
    }

    @Override
    @Test
    public void injectInto()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 3);
        assertEquals(Integer.valueOf(6), bag.injectInto(1, AddFunction.INTEGER));
    }

    @Override
    @Test
    public void forEach()
    {
        MutableBag<Integer> result = HashBag.newBag();
        MutableBag<Integer> collection = MultiReaderHashBag.newBagWith(1, 2, 3, 4, 4);
        collection.forEach(CollectionAddProcedure.on(result));
        assertEquals(HashBag.newBagWith(1, 2, 3, 4, 4), result);
    }

    @Override
    @Test
    public void isEmpty()
    {
        Verify.assertEmpty(MultiReaderHashBag.newBag());
        Verify.assertNotEmpty(MultiReaderHashBag.newBagWith(1, 1));
    }

    @Test
    public void serialization()
    {
        MutableBag<Integer> collection = MultiReaderHashBag.newBagWith(1, 1, 3, 4, 5);
        MutableBag<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        assertEquals(collection, deserializedCollection);
    }

    private void verifyDelegateIsUnmodifiable(MutableBag<Integer> delegate)
    {
        assertThrows(UnsupportedOperationException.class, () -> delegate.add(2));
        assertThrows(UnsupportedOperationException.class, () -> delegate.remove(0));
    }

    @Test
    public void withReadLockAndDelegate()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1);
        Object[] result = new Object[1];
        bag.withReadLockAndDelegate(delegate ->
        {
            result[0] = delegate.getFirst();
            this.verifyDelegateIsUnmodifiable(delegate);
        });
        assertNotNull(result[0]);
    }

    @Override
    @Test
    public void makeString()
    {
        assertEquals("[1, 1, 2, 3]", MultiReaderHashBag.newBagWith(1, 1, 2, 3).toString());
    }

    @Override
    @Test
    public void appendString()
    {
        Appendable builder = new StringBuilder();
        MultiReaderHashBag.newBagWith(1, 1, 2, 3).appendString(builder);
        assertEquals("1, 1, 2, 3", builder.toString());
    }

    @Override
    @Test
    public void testToString()
    {
        assertEquals("[1, 1, 2, 3]", MultiReaderHashBag.newBagWith(1, 1, 2, 3).toString());
    }

    @Override
    @Test
    public void iterator()
    {
        MultiReaderHashBag<Integer> integers = MultiReaderHashBag.newBagWith(1, 1, 2, 3, 4);
        assertThrows(UnsupportedOperationException.class, integers::iterator);
    }

    @Test
    public void listIterator()
    {
        MultiReaderHashBag<Integer> integers = MultiReaderHashBag.newBagWith(1, 1, 2, 3, 4);
        assertThrows(UnsupportedOperationException.class, () -> integers.iterator());
    }

    @Test
    public void withWriteLockAndDelegate()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(2);
        AtomicReference<MutableBag<?>> delegateList = new AtomicReference<>();
        AtomicReference<Iterator<?>> iterator = new AtomicReference<>();
        bag.withWriteLockAndDelegate(delegate ->
        {
            delegate.add(1);
            delegate.add(2);
            delegate.add(3);
            delegate.add(4);
            delegateList.set(delegate);
            iterator.set(delegate.iterator());
        });
        assertEquals(HashBag.newBagWith(1, 2, 2, 3, 4), bag);

        assertThrows(NullPointerException.class, () -> iterator.get().hasNext());

        assertThrows(NullPointerException.class, () -> delegateList.get().iterator());
    }

    @Test
    public void concurrentWrite()
    {
        MultiReaderHashBag<Integer> numbers = this.newWith();
        Interval interval = Interval.oneTo(100);
        ParallelIterate.forEach(interval, each ->
        {
            numbers.add(each);
            Verify.assertSize(1, numbers.select(each::equals));
            numbers.add(each);
            assertEquals(2, numbers.count(each::equals));
            numbers.add(each);
            Integer[] removed = new Integer[1];
            numbers.withWriteLockAndDelegate(bag ->
            {
                Iterator<Integer> iterator = bag.iterator();
                removed[0] = iterator.next();
                bag.remove(removed[0]);
                bag.add(removed[0]);
            });
            numbers.add(each);
            assertEquals(4, numbers.count(each::equals));
        }, 1);

        interval.forEach(Procedures.cast(each -> assertEquals(4, numbers.occurrencesOf(each))));
    }

    @Test
    public void parallelCollect()
    {
        MultiReaderHashBag<String> numbers = this.newWith();
        Interval interval = Interval.oneTo(50000);
        ParallelIterate.collect(interval, String::valueOf, numbers, true);
        assertEquals(numbers, interval.collect(String::valueOf).toBag());
    }

    @Test
    public void selectUnique()
    {
        MutableBag<String> bag = this.newWith("0", "1", "1", "1", "1", "2", "2", "2", "3", "3", "4", "5");
        MutableSet<String> expected = Sets.mutable.with("0", "4", "5");
        MutableSet<String> actual = bag.selectUnique();
        assertEquals(expected, actual);
    }

    @Test
    public void topOccurrences()
    {
        MultiReaderHashBag<Integer> numbers = this.newWith(1, 1, 1, 2, 2, 3, 3);
        MutableList<ObjectIntPair<Integer>> pairs = numbers.topOccurrences(1);
        assertEquals(Integer.valueOf(1), pairs.getFirst().getOne());
        assertEquals(3, pairs.getFirst().getTwo());
        numbers.withReadLockAndDelegate(bag ->
        {
            assertEquals(Integer.valueOf(1), bag.topOccurrences(1).getFirst().getOne());
            assertEquals(3, bag.topOccurrences(1).getFirst().getTwo());
        });
    }

    @Test
    public void bottomOccurrences()
    {
        MultiReaderHashBag<Integer> numbers = this.newWith(1, 1, 1, 2, 2, 3, 3);
        MutableList<ObjectIntPair<Integer>> pairs = numbers.bottomOccurrences(1);
        Verify.assertSize(2, pairs);
        Verify.assertAnySatisfy(pairs, pair -> pair.getOne().equals(2));
        Verify.assertAnySatisfy(pairs, pair -> pair.getOne().equals(3));
        numbers.withReadLockAndDelegate(bag ->
        {
            Verify.assertSize(2, bag.bottomOccurrences(1));
            Verify.assertAnySatisfy(
                    bag.bottomOccurrences(1), pair -> pair.getOne().equals(2));
            Verify.assertAnySatisfy(
                    bag.bottomOccurrences(1), pair -> pair.getOne().equals(3));
        });
    }

    @Test
    public void collectWithOccurrences()
    {
        MultiReaderHashBag<Integer> numbers = this.newWith(1, 1, 1, 2, 2, 3, 3);
        MutableBag<ObjectIntPair<Integer>> pairs =
                numbers.collectWithOccurrences(PrimitiveTuples::pair);
        Verify.assertAnySatisfy(pairs, pair -> pair.getOne().equals(1)
                && pair.getTwo() == 3);
        Verify.assertAnySatisfy(pairs, pair -> pair.getOne().equals(2)
                && pair.getTwo() == 2);
        Verify.assertAnySatisfy(pairs, pair -> pair.getOne().equals(3)
                && pair.getTwo() == 2);
        numbers.withReadLockAndDelegate(bag ->
        {
            MutableBag<ObjectIntPair<Integer>> pairs2 =
                    bag.collectWithOccurrences(PrimitiveTuples::pair);
            Verify.assertAnySatisfy(pairs2, pair -> pair.getOne().equals(1)
                    && pair.getTwo() == 3);
            Verify.assertAnySatisfy(pairs2, pair -> pair.getOne().equals(2)
                    && pair.getTwo() == 2);
            Verify.assertAnySatisfy(pairs2, pair -> pair.getOne().equals(3)
                    && pair.getTwo() == 2);
        });
    }

    @Test
    public void collectWithOccurrencesWithTarget()
    {
        MultiReaderHashBag<Integer> numbers = this.newWith(1, 1, 1, 2, 2, 3, 3);
        MutableBag<ObjectIntPair<Integer>> pairs =
                numbers.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
        Verify.assertAnySatisfy(pairs, pair -> pair.getOne().equals(1)
                && pair.getTwo() == 3);
        Verify.assertAnySatisfy(pairs, pair -> pair.getOne().equals(2)
                && pair.getTwo() == 2);
        Verify.assertAnySatisfy(pairs, pair -> pair.getOne().equals(3)
                && pair.getTwo() == 2);
        numbers.withReadLockAndDelegate(bag ->
        {
            MutableBag<ObjectIntPair<Integer>> pairs2 =
                    bag.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
            Verify.assertAnySatisfy(pairs2, pair -> pair.getOne().equals(1)
                    && pair.getTwo() == 3);
            Verify.assertAnySatisfy(pairs2, pair -> pair.getOne().equals(2)
                    && pair.getTwo() == 2);
            Verify.assertAnySatisfy(pairs2, pair -> pair.getOne().equals(3)
                    && pair.getTwo() == 2);
        });
    }
}
