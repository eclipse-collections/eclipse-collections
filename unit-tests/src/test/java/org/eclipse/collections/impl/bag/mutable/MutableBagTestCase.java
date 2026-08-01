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
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBagIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.MutableBagIterable;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.primitive.IntPredicates;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.collection.mutable.AbstractCollectionTestCase;
import org.eclipse.collections.impl.factory.Iterables;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class MutableBagTestCase extends AbstractCollectionTestCase
{
    @Override
    protected abstract <T> MutableBagIterable<T> newWith(T... littleElements);

    protected abstract <T> MutableBagIterable<T> newWithOccurrences(ObjectIntPair<T>... elementsWithOccurrences);

    @Test
    @Override
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        assertNotEquals(this.newWith(1, 1, 2, 3), this.newWith(1, 2, 2, 3));
        Verify.assertEqualsAndHashCode(this.newWith(null, null, 2, 3), this.newWith(null, 2, null, 3));
        assertEquals(this.newWith(1, 1, 2, 3).toMapOfItemToCount().hashCode(), this.newWith(1, 1, 2, 3).hashCode());
        assertEquals(this.newWith(null, null, 2, 3).toMapOfItemToCount().hashCode(), this.newWith(null, null, 2, 3).hashCode());
    }

    @Test
    public void toStringOfItemToCount()
    {
        assertEquals("{}", this.newWith().toStringOfItemToCount());
        assertEquals("{1=3}", this.newWith(1, 1, 1).toStringOfItemToCount());
        String actual = this.newWith(1, 2, 2).toStringOfItemToCount();
        assertTrue("{1=1, 2=2}".equals(actual) || "{2=2, 1=1}".equals(actual));
    }

    @Test
    public void toMapOfItemToCount()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 2, 2, 3, 3, 3);
        assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), bag.toMapOfItemToCount());
    }

    @Test
    public void add()
    {
        MutableBagIterable<Integer> bag = this.newWith();
        bag.add(1);
        bag.add(1);
        Verify.assertSize(2, bag);
        bag.add(1);
        Verify.assertSize(3, bag);
    }

    @Override
    @Test
    public void iterator()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 2);
        MutableList<Integer> validate = Lists.mutable.of();
        for (Integer each : bag)
        {
            validate.add(each);
        }
        assertEquals(HashBag.newBagWith(1, 1, 2), HashBag.newBag(validate));

        Iterator<Integer> iterator = bag.iterator();
        MutableBagIterable<Integer> expected = this.newWith(1, 1, 2);
        assertThrows(IllegalStateException.class, iterator::remove);

        this.assertIteratorRemove(bag, iterator, expected);
        this.assertIteratorRemove(bag, iterator, expected);
        this.assertIteratorRemove(bag, iterator, expected);
        Verify.assertEmpty(bag);
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    private void assertIteratorRemove(MutableBagIterable<Integer> bag, Iterator<Integer> iterator, MutableBagIterable<Integer> expected)
    {
        assertTrue(iterator.hasNext());
        Integer first = iterator.next();
        iterator.remove();
        expected.remove(first);
        assertEquals(expected, bag);
        assertThrows(IllegalStateException.class, iterator::remove);
    }

    @Test
    public void iteratorRemove()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4);
        Iterator<Integer> iterator = bag.iterator();
        iterator.next();
        iterator.next();
        Integer value = iterator.next();
        Integer value2 = iterator.next();
        assertNotEquals(value, value2);
        iterator.remove();
        Integer value3 = iterator.next();
        assertNotEquals(value, value3);
        iterator.remove();
        Integer value4 = iterator.next();
        assertNotEquals(value, value4);
        iterator.remove();
        Integer value5 = iterator.next();
        assertNotEquals(value, value5);
    }

    @Test
    public void iteratorRemove2()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4);
        Iterator<Integer> iterator = bag.iterator();
        iterator.next();
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.next();
        iterator.remove();
        assertEquals(4, bag.sizeDistinct());
        assertEquals(8, bag.size());
    }

    @Override
    @Test
    public void removeIf()
    {
        super.removeIf();

        MutableBagIterable<Integer> objects = this.newWith(4, 1, 3, 3, 2);
        assertTrue(objects.removeIf(Predicates.equal(2)));
        assertEquals(HashBag.newBagWith(1, 3, 3, 4), objects);
        assertTrue(objects.removeIf(Predicates.equal(3)));
        assertEquals(HashBag.newBagWith(1, 4), objects);
    }

    @Override
    @Test
    public void forEach()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 2);
        MutableList<Integer> validate = Lists.mutable.of();
        bag.forEach(CollectionAddProcedure.on(validate));
        assertEquals(HashBag.newBagWith(1, 1, 2), HashBag.newBag(validate));
    }

    @Test
    public void forEachWithOccurrences()
    {
        MutableBagIterable<Integer> bag = this.newWith();
        bag.addOccurrences(1, 3);
        bag.addOccurrences(2, 2);
        bag.addOccurrences(3, 1);
        IntegerSum sum = new IntegerSum(0);
        bag.forEachWithOccurrences((each, index) -> sum.add(each * index));
        assertEquals(10, sum.getIntSum());
        bag.removeOccurrences(2, 1);
        IntegerSum sum2 = new IntegerSum(0);
        bag.forEachWithOccurrences((each, index) -> sum2.add(each * index));
        assertEquals(8, sum2.getIntSum());
        bag.removeOccurrences(1, 3);
        IntegerSum sum3 = new IntegerSum(0);
        bag.forEachWithOccurrences((each, index) -> sum3.add(each * index));
        assertEquals(5, sum3.getIntSum());
    }

    @Test
    public void collectWithOccurrences()
    {
        Bag<Integer> bag1 = this.newWith(3, 3, 3, 2, 2, 1);
        Bag<ObjectIntPair<Integer>> actual1 =
                bag1.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
        Bag<ObjectIntPair<Integer>> expected1 =
                Bags.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1));
        assertEquals(expected1, actual1);
        assertEquals(expected1, bag1.collectWithOccurrences(PrimitiveTuples::pair));

        Set<ObjectIntPair<Integer>> actual2 =
                bag1.collectWithOccurrences(PrimitiveTuples::pair, Sets.mutable.empty());
        ImmutableSet<ObjectIntPair<Integer>> expected2 =
                Sets.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1));
        assertEquals(expected2, actual2);

        Bag<Integer> bag2 = this.newWith(3, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 4, 5, 7);
        assertEquals(
                this.newWith(8, 5, 6, 5, 6, 8),
                bag2.collectWithOccurrences((each, index) -> each + index));
    }

    @Test
    public void collectEachOccurrences()
    {
        Bag<Integer> bag = this.newWith(1, 1, 1, 2, 2);
        MutableBag<String> target = Bags.mutable.empty();
        int[] calls = {0};
        bag.collectEachOccurrences(each ->
        {
            calls[0]++;
            return String.valueOf(each);
        }, target);
        assertEquals(2, calls[0]);
        assertEquals(Bags.mutable.with("1", "1", "1", "2", "2"), target);
        assertEquals(3, target.occurrencesOf("1"));
        assertEquals(2, target.occurrencesOf("2"));
    }

    @Test
    public void collect_appliesFunctionOncePerOccurrence()
    {
        Bag<Integer> bag = this.newWith(1, 1, 1);
        int[] calls = {0};
        MutableBag<String> target = Bags.mutable.empty();
        bag.collect(each ->
        {
            calls[0]++;
            return each + "-" + calls[0];
        }, target);
        assertEquals(3, calls[0]);
        assertEquals(1, target.occurrencesOf("1-1"));
        assertEquals(1, target.occurrencesOf("1-2"));
        assertEquals(1, target.occurrencesOf("1-3"));
    }

    @Test
    public void countBy_appliesFunctionOncePerDistinct()
    {
        Bag<Integer> bag = this.newWith(1, 1, 1, 2, 2);
        int[] calls = {0};
        Bag<Integer> counts = bag.countBy(each ->
        {
            calls[0]++;
            return each % 2;
        });
        assertEquals(2, calls[0]);
        assertEquals(3, counts.occurrencesOf(1));
        assertEquals(2, counts.occurrencesOf(0));
    }

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();
        Verify.assertInstanceOf(MutableBagIterable.class, this.newWith());
        Verify.assertInstanceOf(ImmutableBagIterable.class, this.newWith().toImmutable());
        assertFalse(this.newWith().toImmutable() instanceof MutableBagIterable);
    }

    @Test
    @Override
    public void getLast()
    {
        assertEquals(Integer.valueOf(1), this.newWith(1).getLast());
        assertEquals(Integer.valueOf(3), this.newWith(3).getLast());
    }

    @Test
    public void occurrencesOf()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 2);
        assertEquals(2, bag.occurrencesOf(1));
        assertEquals(1, bag.occurrencesOf(2));
    }

    @Test
    public void addOccurrences()
    {
        MutableBagIterable<String> bag = this.newWith();
        assertEquals(0, bag.addOccurrences("0", 0));
        assertEquals(1, bag.addOccurrences("1", 1));
        assertEquals(1, bag.addOccurrences("1", 0));
        assertEquals(2, bag.addOccurrences("2", 2));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith("1", "2", "2"), bag);
        assertEquals(1, bag.addOccurrences("1", 0));
        assertEquals(6, bag.addOccurrences("2", 4));
        assertEquals(1, bag.addOccurrences("3", 1));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith("1", "2", "2", "2", "2", "2", "2", "3"), bag);
    }

    @Test
    public void withOccurrences()
    {
        MutableBagIterable<String> bag = this.newWith();
        MutableBagIterable<String> withOccurrences = bag.withOccurrences("0", 0);

        assertEquals(HashBag.newBag(), withOccurrences);
        assertSame(bag, withOccurrences);
        assertEquals(HashBag.newBagWith("1"), bag.withOccurrences("1", 1));
        assertEquals(HashBag.newBagWith("1"), bag.withOccurrences("1", 0));
        assertEquals(HashBag.newBagWith("1", "2", "2"), bag.withOccurrences("2", 2));
        assertEquals(HashBag.newBagWith("1", "2", "2"), bag.withOccurrences("1", 0));
        assertEquals(HashBag.newBagWith("1", "2", "2", "2", "2", "2", "2"), bag.withOccurrences("2", 4));
        assertEquals(HashBag.newBagWith("1", "2", "2", "2", "2", "2", "2", "3"), bag.withOccurrences("3", 1));
    }

    @Test
    public void addOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().addOccurrences(new Object(), -1));
    }

    @Test
    public void withOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().withOccurrences(new Object(), -1));
    }

    @Test
    public void removeOccurrences()
    {
        MutableBagIterable<String> bag = this.newWith("betamax-tape", "betamax-tape");
        MutableBagIterable<String> expected = HashBag.newBag(bag);

        assertFalse(bag.removeOccurrences("dvd", 2));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        assertFalse(bag.removeOccurrences("dvd", 0));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        assertFalse(bag.removeOccurrences("betamax-tape", 0));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        assertTrue(bag.removeOccurrences("betamax-tape", 1));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith("betamax-tape"), bag);

        assertTrue(bag.removeOccurrences("betamax-tape", 10));
        MutableBagTestCase.assertBagsEqual(HashBag.<String>newBag(), bag);
    }

    @Test
    public void withoutOccurrences()
    {
        MutableBagIterable<String> bag = this.newWith("betamax-tape", "betamax-tape");
        MutableBagIterable<String> expected = HashBag.newBag(bag);

        MutableBagTestCase.assertBagsEqual(expected, bag.withoutOccurrences("dvd", 2));
        assertSame(bag, bag.withoutOccurrences("dvd", 2));

        MutableBagTestCase.assertBagsEqual(expected, bag.withoutOccurrences("dvd", 0));

        MutableBagTestCase.assertBagsEqual(expected, bag.withoutOccurrences("betamax-tape", 0));

        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith("betamax-tape"), bag.withoutOccurrences("betamax-tape", 1));

        MutableBagTestCase.assertBagsEqual(HashBag.<String>newBag(), bag.withoutOccurrences("betamax-tape", 10));
    }

    @Test
    public void removeOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().removeOccurrences(new Object(), -1));
    }

    @Test
    public void withoutOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().withoutOccurrences(new Object(), -1));
    }

    @Test
    public void setOccurrences()
    {
        MutableBagIterable<String> bag = this.newWith();
        MutableBagIterable<String> expected = this.newWith("betamax-tape", "betamax-tape");

        assertTrue(bag.setOccurrences("betamax-tape", 2));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        assertFalse(bag.setOccurrences("betamax-tape", 2));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        assertFalse(bag.setOccurrences("dvd", 0));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        assertTrue(bag.setOccurrences("betamax-tape", 3));
        MutableBagTestCase.assertBagsEqual(expected.with("betamax-tape"), bag);

        assertTrue(bag.setOccurrences("betamax-tape", 0));
        MutableBagTestCase.assertBagsEqual(HashBag.<String>newBag(), bag);
    }

    @Test
    public void setOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().setOccurrences(new Object(), -1));
    }

    protected static void assertBagsEqual(Bag<?> expected, Bag<?> actual)
    {
        assertEquals(expected.toMapOfItemToCount(), actual.toMapOfItemToCount());
        assertEquals(expected.sizeDistinct(), actual.sizeDistinct());
        assertEquals(expected.size(), actual.size());
        Verify.assertEqualsAndHashCode(expected, actual);
    }

    @Test
    public void toSortedListWith()
    {
        assertEquals(
                FastList.newListWith(1, 2, 2, 3, 3, 3),
                this.newWith(3, 3, 3, 2, 2, 1).toSortedList());
    }

    @Override
    @Test
    public void toSet()
    {
        super.toSet();
        MutableBagIterable<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertEquals(UnifiedSet.newSetWith(1, 2, 3), bag.toSet());
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 1);
        assertEquals(FastList.newListWith(1, 1, 1), bag.toList());
    }

    @Override
    @Test
    public void removeObject()
    {
        super.removeObject();

        MutableBagIterable<String> bag = this.newWith("dakimakura", "dakimakura");
        assertFalse(bag.remove("Mr. T"));
        assertTrue(bag.remove("dakimakura"));
        assertTrue(bag.remove("dakimakura"));
        assertFalse(bag.remove("dakimakura"));
        MutableBagTestCase.assertBagsEqual(Bags.mutable.of(), bag);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedBag.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableBag.class, this.newWith().asUnmodifiable());
    }

    @Test
    public void serialization()
    {
        MutableBagIterable<String> bag = this.newWith("One", "Two", "Two", "Three", "Three", "Three");
        Verify.assertPostSerializedEqualsAndHashCode(bag);
    }

    @Override
    @Test
    public void partition()
    {
        super.partition();

        MutableBagIterable<Integer> integers = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        PartitionMutableCollection<Integer> result = integers.partition(IntegerPredicates.isEven());
        assertEquals(Iterables.iBag(2, 2, 4, 4, 4, 4), result.getSelected());
        assertEquals(Iterables.iBag(1, 3, 3, 3), result.getRejected());
    }

    @Override
    @Test
    public void partitionWith()
    {
        super.partitionWith();

        MutableBagIterable<Integer> integers = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        PartitionMutableCollection<Integer> result = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isEven()));
        assertEquals(Iterables.iBag(2, 2, 4, 4, 4, 4), result.getSelected());
        assertEquals(Iterables.iBag(1, 3, 3, 3), result.getRejected());
    }

    @Test
    public void selectByOccurrences()
    {
        MutableBagIterable<Integer> integers = this.newWith(1, 1, 1, 1, 2, 2, 2, 3, 3, 4);
        assertEquals(Iterables.iBag(1, 1, 1, 1, 3, 3), integers.selectByOccurrences(IntPredicates.isEven()));
    }

    @Test
    public void selectDuplicates()
    {
        MutableBagIterable<Integer> integers = this.newWith(0, 1, 1, 1, 1, 2, 2, 2, 3, 3, 4, 5);
        assertEquals(Iterables.iBag(1, 1, 1, 1, 2, 2, 2, 3, 3), integers.selectDuplicates());
    }

    @Test
    public void topOccurrences()
    {
        MutableBagIterable<String> strings = Bags.mutable.withOccurrences(
                PrimitiveTuples.pair("one", 1),
                PrimitiveTuples.pair("two", 2),
                PrimitiveTuples.pair("three", 3),
                PrimitiveTuples.pair("four", 4),
                PrimitiveTuples.pair("five", 5),
                PrimitiveTuples.pair("six", 6),
                PrimitiveTuples.pair("seven", 7),
                PrimitiveTuples.pair("eight", 8),
                PrimitiveTuples.pair("nine", 9),
                PrimitiveTuples.pair("ten", 10));
        MutableList<ObjectIntPair<String>> top5 = strings.topOccurrences(5);
        Verify.assertSize(5, top5);
        assertEquals("ten", top5.getFirst().getOne());
        assertEquals(10, top5.getFirst().getTwo());
        assertEquals("six", top5.getLast().getOne());
        assertEquals(6, top5.getLast().getTwo());
        Verify.assertSize(0, this.newWith("one").topOccurrences(0));
        Verify.assertSize(0, this.newWith().topOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(2));
        Verify.assertSize(3, this.newWith("one", "one", "two", "three").topOccurrences(2));
        Verify.assertSize(2, this.newWith("one", "one", "two", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith(null, "one", "two").topOccurrences(5));
        Verify.assertSize(3, this.newWith(null, "one", "two").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "one", "two", "two", "three", "three").topOccurrences(1));
        Verify.assertSize(0, this.newWith().topOccurrences(0));
        Verify.assertSize(0, this.newWith("one").topOccurrences(0));
        assertThrows(IllegalArgumentException.class, () -> this.newWith().topOccurrences(-1));
    }

    @Test
    public void anySatisfyWithOccurrences()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertTrue(bag.anySatisfyWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertTrue(bag.anySatisfyWithOccurrences((object, value) -> object.equals(2) && value == 2));
        assertTrue(bag.anySatisfyWithOccurrences((object, value) -> object.equals(3)));

        assertFalse(bag.anySatisfyWithOccurrences((object, value) -> object.equals(2) && value == 5));
        assertFalse(bag.anySatisfyWithOccurrences((object, value) -> object.equals(1) && value == 7));
        assertFalse(bag.anySatisfyWithOccurrences((object, value) -> object.equals(10)));
    }

    @Test
    public void noneSatisfyWithOccurrences()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(3) && value == 1));
        assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(30)));
        assertFalse(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(1) && value == 0));
        assertFalse(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(1) && value == 1));
        assertFalse(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(2)));
    }

    @Test
    public void allSatisfyWithOccurrences()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3);
        assertTrue(bag.allSatisfyWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertTrue(bag.allSatisfyWithOccurrences((object, value) -> object.equals(3)));
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(4) && value == 3));
        bag = this.newWith(3, 3, 3, 1);
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(1) && value == 3));
        assertTrue(bag.allSatisfyWithOccurrences((object, value) -> object.equals(3) || object == 1));
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(300) || object == 1));
    }

    @Test
    public void detectWithOccurrences()
    {
        Bag<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        assertEquals((Integer) 3, bag.detectWithOccurrences((object, value) -> object.equals(3) && value == 3));
        assertEquals((Integer) 3, bag.detectWithOccurrences((object, value) -> object.equals(3)));
        assertEquals((Integer) 1, bag.detectWithOccurrences((object, value) -> object.equals(1) && value == 1));
        assertNull(bag.detectWithOccurrences((object, value) -> object.equals(1) && value == 10));
        assertNull(bag.detectWithOccurrences((object, value) -> object.equals(10) && value == 5));
        assertNull(bag.detectWithOccurrences((object, value) -> object.equals(100)));
    }

    @Test
    public void bottomOccurrences()
    {
        MutableBagIterable<String> strings = Bags.mutable.ofOccurrences(
                PrimitiveTuples.pair("one", 1),
                PrimitiveTuples.pair("two", 2),
                PrimitiveTuples.pair("three", 3),
                PrimitiveTuples.pair("four", 4),
                PrimitiveTuples.pair("five", 5),
                PrimitiveTuples.pair("six", 6),
                PrimitiveTuples.pair("seven", 7),
                PrimitiveTuples.pair("eight", 8),
                PrimitiveTuples.pair("nine", 9),
                PrimitiveTuples.pair("ten", 10));
        MutableList<ObjectIntPair<String>> bottom5 = strings.bottomOccurrences(5);
        Verify.assertSize(5, bottom5);
        assertEquals("one", bottom5.getFirst().getOne());
        assertEquals(1, bottom5.getFirst().getTwo());
        assertEquals("five", bottom5.getLast().getOne());
        assertEquals(5, bottom5.getLast().getTwo());
        Verify.assertSize(0, this.newWith("one").bottomOccurrences(0));
        Verify.assertSize(0, this.newWith().bottomOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").bottomOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").bottomOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "two", "three").bottomOccurrences(2));
        Verify.assertSize(2, this.newWith("one", "one", "two", "three").bottomOccurrences(2));
        Verify.assertSize(3, this.newWith("one", "one", "two", "two", "three").bottomOccurrences(2));
        Verify.assertSize(3, this.newWith(null, "one", "two").bottomOccurrences(5));
        Verify.assertSize(3, this.newWith(null, "one", "two").bottomOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "one", "two", "two", "three", "three").bottomOccurrences(1));
        Verify.assertSize(0, this.newWith().bottomOccurrences(0));
        Verify.assertSize(0, this.newWith("one").bottomOccurrences(0));
        assertThrows(IllegalArgumentException.class, () -> this.newWith().bottomOccurrences(-1));
    }

    @Test
    public void selectUnique()
    {
        MutableBag<String> bag = Bags.mutable.with("0", "1", "1", "1", "1", "2", "2", "2", "3", "3", "4", "5");
        MutableSet<String> expected = Sets.mutable.with("0", "4", "5");
        MutableSet<String> actual = bag.selectUnique();
        assertEquals(expected, actual);
    }

    @Override
    @Test
    public void zip()
    {
        super.zip();
        RichIterable<String> bag = this.newWith("1", "2", "3");
        Bag<Pair<String, String>> expected = this.newWith(
                Tuples.pair("1", "1"),
                Tuples.pair("2", "2"),
                Tuples.pair("3", "3"));
        assertEquals(expected, bag.zip(bag::iterator).toBag());
    }

    @Test
    public void distinctView()
    {
        MutableBagIterable<String> bag = this.newWith("1", "2", "2", "3", "3", "3", "3", "4", "5", "5", "6");
        RichIterable<String> expected = bag.toSet();
        RichIterable<String> actual = bag.distinctView();
        assertEquals(expected, actual);
    }
}
