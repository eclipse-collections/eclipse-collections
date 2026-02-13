/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.mutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.primitive.ObjectIntPredicate;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.partition.set.sorted.PartitionMutableSortedSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.function.NegativeIntervalFunction;
import org.eclipse.collections.impl.collection.mutable.AbstractCollectionTestCase;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.factory.Stacks;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.AddToList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.eclipse.collections.impl.test.domain.Person;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract JUnit test for {@link MutableSortedSet}s.
 */
public abstract class AbstractSortedSetTestCase extends AbstractCollectionTestCase
{
    @Override
    protected abstract <T> MutableSortedSet<T> newWith(T... littleElements);

    protected abstract <T> MutableSortedSet<T> newWith(Comparator<? super T> comparator, T... elements);

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();
        Verify.assertInstanceOf(ImmutableSortedSet.class, this.newWith().toImmutable());
    }

    @Override
    @Test
    public void addAll()
    {
        super.addAll();

        MutableSortedSet<Integer> expected = TreeSortedSet.newSet(Lists.mutable.with(1, 2, 3));
        MutableSortedSet<Integer> collection = this.newWith();

        assertTrue(collection.addAll(Lists.mutable.with(3, 2, 1)));
        assertEquals(expected, collection);

        assertFalse(collection.addAll(Lists.mutable.with(1, 2, 3)));
        assertEquals(expected, collection);
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();

        MutableSortedSet<Integer> expected = TreeSortedSet.newSet(Lists.mutable.with(1, 2, 3));
        MutableSortedSet<Integer> collection = this.newWith();

        assertTrue(collection.addAllIterable(Lists.mutable.with(1, 2, 3)));
        assertEquals(expected, collection);

        assertFalse(collection.addAllIterable(Lists.mutable.with(1, 2, 3)));
        assertEquals(expected, collection);
    }

    @Override
    @Test
    public void testToString()
    {
        assertEquals("[1, 2]", this.newWith(1, 2).toString());
    }

    @Override
    @Test
    public void makeString()
    {
        assertEquals(this.newWith(1, 2, 3).toString(), '[' + this.newWith(1, 2, 3).makeString() + ']');
    }

    @Override
    @Test
    public void appendString()
    {
        MutableCollection<Integer> mutableCollection = this.newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        mutableCollection.appendString(builder);
        assertEquals(mutableCollection.toString(), '[' + builder.toString() + ']');
    }

    @Override
    @Test
    public void removeIf()
    {
        MutableSortedSet<Integer> objects = this.newWith(4, 1, 3, 2);
        assertTrue(objects.removeIf(Predicates.equal(2)));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 3, 4), objects);
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        MutableSortedSet<Integer> sortedSet = this.newWith(1, 2, 3, 4);
        MutableSet<Integer> hashSet = UnifiedSet.newSet(sortedSet);
        Verify.assertEqualsAndHashCode(hashSet, sortedSet);
    }

    @Override
    @Test
    public void tap()
    {
        super.tap();

        MutableList<Integer> tapRevResult = Lists.mutable.of();
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 4, 3, 5);
        assertSame(revInt, revInt.tap(tapRevResult::add));
        assertEquals(revInt.toList(), tapRevResult);
    }

    @Test
    public void corresponds()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        assertFalse(integers1.corresponds(this.newWith(1, 2, 3, 4, 5), Predicates2.alwaysTrue()));

        MutableList<Integer> integers2 = integers1.collect(integer -> integer + 1, FastList.newList());
        assertTrue(integers1.corresponds(integers2, Predicates2.lessThan()));
        assertFalse(integers1.corresponds(integers2, Predicates2.greaterThan()));

        MutableSortedSet<Integer> integers3 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        assertFalse(integers3.corresponds(integers1, Predicates2.equal()));
    }

    @Override
    @Test
    public void select()
    {
        super.select();
        MutableSortedSet<Integer> integers = this.newWith(3, 2, 1, 4, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(3, 2, 1), integers.select(Predicates.lessThanOrEqualTo(3)));
        Verify.assertEmpty(integers.select(Predicates.greaterThan(6)));
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 4, 3, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 3, 2, 1), revInt.select(Predicates.lessThan(4)));
    }

    @Override
    @Test
    public void selectWith()
    {
        super.selectWith();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), this.newWith(3, 1, 2, 5, 3).selectWith(Predicates2.lessThan(), 4));
    }

    @Override
    @Test
    public void selectWith_target()
    {
        super.selectWith_target();
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith(1, 2, 3),
                this.newWith(3, 1, 2, 5, 3).selectWith(Predicates2.lessThan(), 4, TreeSortedSet.newSet()));
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();
        MutableSortedSet<Integer> integers = this.newWith(4, 2, 1, 3, 5, 6);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), integers.reject(Predicates.greaterThan(4)));
        Verify.assertEmpty(integers.reject(Predicates.greaterThan(0)));
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 4, 3, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 3, 2, 1), revInt.reject(Predicates.greaterThan(3)));
    }

    @Override
    @Test
    public void rejectWith()
    {
        super.rejectWith();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), this.newWith(3, 1, 2, 5, 4).rejectWith(Predicates2.greaterThan(), 3));
    }

    @Override
    @Test
    public void rejectWith_target()
    {
        super.rejectWith();
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith(1, 2, 3),
                this.newWith(3, 1, 2, 5, 4).rejectWith(Predicates2.greaterThan(), 3, TreeSortedSet.newSet()));
    }

    @Override
    @Test
    public void partition()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        PartitionMutableSortedSet<Integer> partition = integers.partition(IntegerPredicates.isEven());
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 6, 4, 2), partition.getSelected());
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 5, 3, 1), partition.getRejected());
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getSelected().comparator());
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void partitionWith()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        PartitionMutableSortedSet<Integer> partition = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isEven()));
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 6, 4, 2), partition.getSelected());
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 5, 3, 1), partition.getRejected());
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getSelected().comparator());
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getRejected().comparator());
    }

    @Test
    public void partitionWhile()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        PartitionMutableSortedSet<Integer> partition = integers.partitionWhile(Predicates.greaterThan(3));
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 6, 5, 4), partition.getSelected());
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 1), partition.getRejected());
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getSelected().comparator());
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getRejected().comparator());
    }

    @Test
    public void takeWhile()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        MutableSortedSet<Integer> take = integers.takeWhile(Predicates.greaterThan(3));
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 6, 5, 4), take);
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), take.comparator());
    }

    @Test
    public void dropWhile()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        MutableSortedSet<Integer> drop = integers.dropWhile(Predicates.greaterThan(3));
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 1), drop);
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), drop.comparator());
    }

    @Test
    public void distinct()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        MutableSortedSet<Integer> distinct = integers.distinct();
        assertEquals(integers, distinct);
        assertNotSame(integers, distinct);
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        MutableSortedSet<Integer> integers = this.newWith(4, 3, 1, 6, 5, 2);
        Verify.assertListsEqual(Lists.mutable.with("1", "2", "3", "4", "5", "6"), integers.collect(String::valueOf));
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 4, 3, 5);
        Verify.assertListsEqual(Lists.mutable.with("5", "4", "3", "2", "1"), revInt.collect(String::valueOf));
    }

    /**
     * @since 9.1.
     */
    @Test
    public void collectWithIndex()
    {
        MutableSortedSet<Integer> integers = this.newWith(4, 5, 6);
        MutableList<ObjectIntPair<Integer>> expected =
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(4), 0),
                        PrimitiveTuples.pair(Integer.valueOf(5), 1),
                        PrimitiveTuples.pair(Integer.valueOf(6), 2));
        Verify.assertListsEqual(
                expected,
                integers.collectWithIndex(PrimitiveTuples::pair));
    }

    /**
     * @since 11.0.
     */
    @Test
    public void selectWithIndex()
    {
        assertEquals(
                Lists.mutable.of(5, 6),
                this.newWith(6, 4, 5).selectWithIndex((object, value) -> value > 0, Lists.mutable.empty()));
    }

    /**
     * @since 11.0.
     */
    @Test
    public void selectWithIndexAllRejected()
    {
        MutableSortedSet<Integer> integers = this.newWith(6, 4, 5);
        Verify.assertEmpty(
                integers.selectWithIndex(new ObjectIntPredicate<Integer>()
                {
                    int index = 0;
                    @Override
                    public boolean accept(Integer object, int value)
                    {
                        assertEquals(this.index++, value);
                        return false;
                    }
                }, Lists.mutable.empty()));
    }

    /**
     * @since 11.0.
     */
    @Test
    public void rejectWithIndexNoneRejected()
    {
        MutableSortedSet<Integer> integers = this.newWith(6, 4, 5);
        assertEquals(
                Lists.mutable.of(4, 5, 6),
                integers.rejectWithIndex(new ObjectIntPredicate<Integer>()
                {
                    int index = 0;
                    @Override
                    public boolean accept(Integer object, int value)
                    {
                        assertEquals(this.index++, value);
                        return false;
                    }
                }, Lists.mutable.empty()));
    }

    /**
     * @since 11.0.
     */
    @Test
    public void rejectWithIndex()
    {
        MutableSortedSet<Integer> integers = this.newWith(6, 4, 5);
        assertEquals(Lists.mutable.of(5, 6),
                integers.rejectWithIndex((object, value) -> value < 1, Lists.mutable.empty()));
    }

    /**
     * @since 9.1.
     */
    @Test
    public void collectWithIndexWithTarget()
    {
        MutableSortedSet<Integer> integers = this.newWith(4, 5, 6);
        MutableList<ObjectIntPair<Integer>> expected =
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(4), 0),
                        PrimitiveTuples.pair(Integer.valueOf(5), 1),
                        PrimitiveTuples.pair(Integer.valueOf(6), 2));
        Verify.assertListsEqual(
                expected,
                integers.collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty()));
    }

    /**
     * @since 11.0.
     */
    @Test
    public void selectWithIndexWithTarget()
    {
        MutableSortedSet<Integer> integers = this.newWith(4, 5, 6);
        MutableList<Integer> expected = Lists.mutable.with(4, 6);
        assertEquals(
                expected,
                integers.selectWithIndex((each, index) -> index % 2 == 0, Lists.mutable.empty()));
    }

    /**
     * @since 11.0.
     */
    @Test
    public void rejectWithIndexWithTarget()
    {
        MutableSortedSet<Integer> integers = this.newWith(4, 5, 6);
        MutableList<Integer> expected = Lists.mutable.with(5);
        assertEquals(
                expected,
                integers.rejectWithIndex((each, index) -> index % 2 == 0, Lists.mutable.empty()));
    }

    @Override
    @Test
    public void collectWith()
    {
        super.collectWith();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5);
        Function2<Integer, Integer, String> addParamFunction =
                (each, parameter) -> ((Integer) (each + parameter)).toString();
        Verify.assertListsEqual(Lists.mutable.with("4", "3", "2", "1", "0"), integers.collectWith(addParamFunction, -1));
    }

    @Override
    @Test
    public void collectWith_target()
    {
        super.collectWith_target();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5);
        Function2<Integer, Integer, String> addParamFunction =
                (each, parameter) -> ((Integer) (each + parameter)).toString();
        Verify.assertIterablesEqual(TreeSortedSet.newSetWith("4", "3", "2", "1", "0"), integers.collectWith(addParamFunction, -1, TreeSortedSet.newSet()));
    }

    @Override
    @Test
    public void flatCollect()
    {
        super.flatCollect();
        MutableSortedSet<Integer> collection = this.newWith(Collections.reverseOrder(), 2, 4, 2, 1, 3);
        Function<Integer, MutableList<String>> function =
                object -> Lists.mutable.with(String.valueOf(object));
        Verify.assertListsEqual(
                Lists.mutable.with("4", "3", "2", "1"),
                collection.flatCollect(function));
    }

    @Override
    @Test
    public void groupBy()
    {
        super.groupBy();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Function<Integer, Boolean> isOddFunction = object -> IntegerPredicates.isOdd().accept(object);
        MutableSortedSetMultimap<Boolean, Integer> map = integers.groupBy(isOddFunction);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 9, 7, 5, 3, 1), map.get(Boolean.TRUE));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 8, 6, 4, 2), map.get(Boolean.FALSE));
        Verify.assertSize(2, map.keysView().toList());
    }

    @Override
    @Test
    public void groupByEach()
    {
        super.groupByEach();
        MutableSortedSet<Integer> set = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5, 6, 7);
        NegativeIntervalFunction function = new NegativeIntervalFunction();
        MutableSortedSetMultimap<Integer, Integer> expected = this.newWith(Collections.<Integer>reverseOrder()).groupByEach(function);
        for (int i = 1; i < 8; i++)
        {
            expected.putAll(-i, Interval.fromTo(i, 7));
        }
        MutableSortedSetMultimap<Integer, Integer> actual =
                set.groupByEach(function);
        assertEquals(expected, actual);
        MutableSortedSetMultimap<Integer, Integer> actualWithTarget =
                set.groupByEach(function, this.<Integer>newWith().groupByEach(function));
        assertEquals(expected, actualWithTarget);
        for (int i = 1; i < 8; ++i)
        {
            Verify.assertSortedSetsEqual(expected.get(-i), actual.get(-i));
            Verify.assertSortedSetsEqual(expected.get(-i), actualWithTarget.get(-i).toSortedSet(Collections.reverseOrder()));
        }
        Verify.assertSize(7, actual.keysView().toList());
        Verify.assertSize(7, actualWithTarget.keysView().toList());
    }

    @Override
    @Test
    public void zip()
    {
        super.zip();
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 2, 3, 5, 1, 4);
        MutableSortedSet<Integer> integers = this.newWith(1, 3, 2, 4, 5);
        MutableList<Pair<Integer, Integer>> zip = integers.zip(revInt);
        MutableList<Pair<Integer, Integer>> revZip = revInt.zip(integers);
        Verify.assertSize(5, zip);
        Verify.assertSize(5, revZip);
        Iterator<Pair<Integer, Integer>> zipItr = zip.iterator();
        Iterator<Pair<Integer, Integer>> revZipItr = revZip.iterator();

        for (int i = 1; i < 6; ++i)
        {
            assertEquals(Tuples.pair(i, 6 - i), zipItr.next());
            assertEquals(Tuples.pair(6 - i, i), revZipItr.next());
        }

        Person john = new Person("John", "Smith");
        Person johnDoe = new Person("John", "Doe");
        MutableSortedSet<Person> people = this.newWith(john, johnDoe);
        MutableList<Holder<Integer>> list = Lists.mutable.with(new Holder<>(1), new Holder<>(2), new Holder<>(3));
        MutableList<Pair<Person, Holder<Integer>>> pairs = people.zip(list);
        assertEquals(
                Lists.mutable.with(Tuples.pair(johnDoe, new Holder<>(1)), Tuples.pair(john, new Holder<>(2))),
                pairs.toList());
        assertTrue(pairs.add(Tuples.pair(new Person("Jack", "Baker"), new Holder<>(3))));
        assertEquals(Tuples.pair(new Person("Jack", "Baker"), new Holder<>(3)), pairs.getLast());
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        super.zipWithIndex();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 3, 5, 2, 4);
        Iterator<Pair<Integer, Integer>> zip = integers.zipWithIndex().iterator();
        for (int i = 5; i > 0; --i)
        {
            assertEquals(Tuples.pair(i, 5 - i), zip.next());
        }
        Person john = new Person("John", "Smith");
        Person jane = new Person("Jane", "Smith");
        Person johnDoe = new Person("John", "Doe");
        MutableSortedSet<Person> people = this.newWith(Collections.reverseOrder(), john, johnDoe, jane);
        MutableSortedSet<Pair<Person, Integer>> pairs = people.zipWithIndex();
        Verify.assertListsEqual(Lists.mutable.with(Tuples.pair(john, 0), Tuples.pair(johnDoe, 1)), pairs.toList());
    }

    @Test
    public void union()
    {
        MutableSortedSet<String> set = this.newWith("d", "c", "b", "a");
        MutableSortedSet<String> union = set.union(UnifiedSet.newSetWith("a", "e", "g", "b", "f"));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("a", "b", "c", "d", "e", "f", "g"), union);
    }

    @Test
    public void unionInto()
    {
        MutableSortedSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> union = set.unionInto(UnifiedSet.newSetWith("1", "2", "5", "3"), UnifiedSet.newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1", "2", "3", "4", "5"), union);
        assertEquals(set, set.unionInto(UnifiedSet.newSetWith("1"), UnifiedSet.newSet()));
        MutableSortedSet<String> sortedUnion = set.unionInto(TreeSortedSet.newSetWith("5", "1", "6"), TreeSortedSet.newSet());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("1", "2", "3", "4", "5", "6"), sortedUnion);
    }

    @Test
    public void intersect()
    {
        MutableSortedSet<String> set = this.newWith("4", "2", "1", "3");
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("1", "2"), set.intersect(UnifiedSet.newSetWith("7", "5", "6", "1", "8", "2")));
        Verify.assertEmpty(set.intersect(UnifiedSet.newSetWith("5", "6", "8", "9")));
    }

    @Test
    public void intersectInto()
    {
        MutableSortedSet<String> set = this.newWith("2", "4", "3", "1");
        MutableSet<String> intersect = set.intersectInto(UnifiedSet.newSetWith("a", "b", "4", "c", "1"), UnifiedSet.newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1", "4"), intersect);
        Verify.assertEmpty(set.intersectInto(UnifiedSet.newSetWith("not present"), UnifiedSet.newSet()));
    }

    @Test
    public void difference()
    {
        MutableSortedSet<String> set = this.newWith("5", "2", "3", "4", "1");
        MutableSortedSet<String> difference = set.difference(UnifiedSet.newSetWith("2", "3", "4", "not present"));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("1", "5"), difference);
        assertEquals(set, set.difference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void differenceInto()
    {
        MutableSortedSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> difference = set.differenceInto(UnifiedSet.newSetWith("2", "3", "4", "not present"), UnifiedSet.newSet());
        assertEquals(UnifiedSet.newSetWith("1"), difference);
        assertEquals(set, set.differenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.newSet()));
    }

    @Test
    public void symmetricDifference()
    {
        MutableSortedSet<String> set = this.newWith("3", "4", "1", "2");
        MutableSortedSet<String> difference = set.symmetricDifference(UnifiedSet.newSetWith("6", "3", "4", "5"));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("1", "2", "5", "6"), difference);
        Verify.assertSize(set.size() + 1, set.symmetricDifference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void symmetricDifferenceInto()
    {
        MutableSortedSet<String> set = this.newWith("2", "1", "3", "4");
        MutableSet<String> difference = set.symmetricDifferenceInto(
                UnifiedSet.newSetWith("4", "2", "3", "5"),
                UnifiedSet.newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1", "5"), difference);
        Verify.assertSetsEqual(
                UnifiedSet.newSet(set).with("not present"),
                set.symmetricDifferenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.newSet()));
    }

    @Test
    public void isSubsetOf()
    {
        MutableSortedSet<String> set = this.newWith("3", "4", "1", "2");
        assertTrue(set.isSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
    }

    @Test
    public void isProperSubsetOf()
    {
        MutableSortedSet<String> set = this.newWith("3", "1", "4", "1", "2", "3");
        assertTrue(set.isProperSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
        assertFalse(set.isProperSubsetOf(set));
    }

    @Test
    public void powerSet()
    {
        MutableSortedSet<String> set = this.newWith("1", "2", "3", "4", "5", "test");
        MutableSortedSet<SortedSetIterable<String>> powerSet = set.powerSet();

        Verify.assertSize((int) StrictMath.pow(2, set.size()), powerSet);
        Verify.assertContains(TreeSortedSet.<String>newSet(), powerSet);
        Verify.assertContains(set, powerSet);

        MutableSortedSet<SortedSetIterable<Integer>> intPowerSet = this.newWith(1, 2, 3).powerSet();
        MutableSortedSet<SortedSetIterable<Integer>> revPowerSet = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5, 6).powerSet();

        MutableList<MutableSortedSet<Integer>> expectedSortedSet =
                Lists.mutable.with(
                        TreeSortedSet.newSet(),
                        TreeSortedSet.newSetWith(1),
                        TreeSortedSet.newSetWith(2),
                        TreeSortedSet.newSetWith(3),
                        TreeSortedSet.newSetWith(1, 2),
                        TreeSortedSet.newSetWith(1, 3),
                        TreeSortedSet.newSetWith(2, 3),
                        TreeSortedSet.newSetWith(1, 2, 3));

        Verify.assertListsEqual(expectedSortedSet, intPowerSet.toList());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 1, 2, 3, 4, 5, 6), (SortedSet<Integer>) revPowerSet.getLast());
        Verify.assertInstanceOf(TreeSortedSet.class, intPowerSet);
        Verify.assertInstanceOf(TreeSortedSet.class, intPowerSet.getFirst());
    }

    @Test
    public void cartesianProduct()
    {
        MutableSortedSet<String> set = this.newWith("1", "2", "3", "4", "1", "2", "3", "4");
        LazyIterable<Pair<String, String>> cartesianProduct = set.cartesianProduct(UnifiedSet.newSetWith("One", "Two"));
        Verify.assertIterableSize(set.size() * 2, cartesianProduct);
        assertEquals(
                set,
                cartesianProduct
                        .select(Predicates.attributeEqual((Function<Pair<?, String>, String>) Pair::getTwo, "One"))
                        .collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
    }

    @Test
    public void firstLast()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        assertEquals(Integer.valueOf(1), set.first());
        assertEquals(Integer.valueOf(5), set.last());
        MutableSortedSet<Integer> set2 = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5);
        assertEquals(Integer.valueOf(5), set2.first());
        assertEquals(Integer.valueOf(1), set2.last());
    }

    @Test
    public void testClone()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3);
        MutableSortedSet<Integer> clone = set.clone();
        assertNotSame(set, clone);
        Verify.assertSortedSetsEqual(set, clone);
    }

    @Override
    @Test
    public void toSortedSet_natural_ordering()
    {
        super.toSortedSet_natural_ordering();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4, 5), integers.toSortedSet());
    }

    @Test
    public void subSet()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> subSet = set.subSet(1, 4);

        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), subSet);

        subSet.clear();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(4, 5), set);

        subSet.addAll(Lists.mutable.with(1, 2));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 4, 5), set);

        subSet.remove(1);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(2, 4, 5), set);

        set.clear();
        Verify.assertEmpty(subSet);

        assertThrows(IllegalArgumentException.class, () -> subSet.add(5));
        assertThrows(IllegalArgumentException.class, () -> subSet.add(0));
    }

    @Test
    public void headSet()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> subSet = set.headSet(4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), subSet);

        subSet.clear();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(4, 5), set);

        subSet.addAll(Lists.mutable.with(1, 2));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 4, 5), set);

        subSet.remove(1);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(2, 4, 5), set);

        set.clear();
        Verify.assertEmpty(subSet);

        assertThrows(IllegalArgumentException.class, () -> subSet.add(6));
        assertTrue(subSet.add(0));
    }

    @Test
    public void tailSet()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> subSet = set.tailSet(2);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(2, 3, 4, 5), subSet);

        subSet.clear();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1), set);

        subSet.addAll(Lists.mutable.with(2, 3));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), set);

        subSet.remove(3);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2), set);

        set.clear();
        Verify.assertEmpty(subSet);

        assertThrows(IllegalArgumentException.class, () -> subSet.add(1));
        assertTrue(subSet.add(10));
    }

    @Override
    public void selectInstancesOf()
    {
        MutableSortedSet<Number> numbers = this.newWith((o1, o2) -> Double.compare(o1.doubleValue(), o2.doubleValue()), 1, 2.0, 3, 4.0, 5);
        MutableSortedSet<Integer> integers = numbers.selectInstancesOf(Integer.class);
        assertEquals(UnifiedSet.newSetWith(1, 3, 5), integers);
        assertEquals(Lists.mutable.with(1, 3, 5), integers.toList());
    }

    @Test
    public void toStack()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        MutableStack<Integer> stack = integers.toStack();
        assertEquals(Stacks.immutable.with(6, 5, 4, 3, 2, 1), stack);
    }

    @Override
    @Test
    public void toSortedBag_natural_ordering()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 5, 3, 4);
        MutableSortedBag<Integer> bag = integers.toSortedBag();
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4, 5), bag);
    }

    @Override
    @Test
    public void toSortedBag_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBag(Collections.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 4, 3, 2, 1), bag);
    }

    @Override
    @Test
    public void toImmutableSortedBag_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        ImmutableSortedBag<Integer> bag = integers.toImmutableSortedBag(Collections.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 4, 3, 2, 1), bag);
    }

    @Override
    @Test
    public void toSortedBag_with_null()
    {
        assertThrows(NullPointerException.class, () -> this.newWith(3, 4, null, 1, 2).toSortedBag());
    }

    @Override
    @Test
    public void toSortedBagBy()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBagBy(String::valueOf);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4), bag);
    }

    @Override
    @Test
    public void min_null_safe()
    {
        assertThrows(NullPointerException.class, () -> super.min_null_safe());
    }

    @Override
    @Test
    public void max_null_safe()
    {
        assertThrows(NullPointerException.class, () -> super.max_null_safe());
    }

    @Test
    public void forEachWithIndexWithFromTo()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        StringBuilder builder = new StringBuilder();
        integers.forEachWithIndex(5, 7, (each, index) -> builder.append(each).append(index));
        assertEquals("453627", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        integers.forEachWithIndex(5, 5, (each, index) -> builder2.append(each).append(index));
        assertEquals("45", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        integers.forEachWithIndex(0, 9, (each, index) -> builder3.append(each).append(index));
        assertEquals("90817263544536271809", builder3.toString());

        MutableList<Integer> result = Lists.mutable.of();
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(-1, 0, new AddToList(result)));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, -1, new AddToList(result)));
        assertThrows(IllegalArgumentException.class, () -> integers.forEachWithIndex(7, 5, new AddToList(result)));
    }

    @Test
    public void forEachWithIndexOnRange()
    {
        MutableSortedSet<Integer> set = this.newWith(Comparators.reverseNaturalOrder(), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        this.validateForEachWithIndexOnRange(set, 0, 0, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 9));
        this.validateForEachWithIndexOnRange(set, 3, 5, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 6, 5, 4));
        this.validateForEachWithIndexOnRange(set, 9, 9, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 0));
        this.validateForEachWithIndexOnRange(set, 0, 9, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> this.validateForEachWithIndexOnRange(set, 10, 10, SortedSets.mutable.with(Comparators.reverseNaturalOrder())));

        assertThrows(
                IllegalArgumentException.class,
                () -> this.validateForEachWithIndexOnRange(set, 9, 0, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)));
    }

    protected void validateForEachWithIndexOnRange(
            MutableSortedSet<Integer> set,
            int from,
            int to,
            MutableSortedSet<Integer> expectedOutput)
    {
        MutableSortedSet<Integer> outputSet = SortedSets.mutable.of(Comparators.reverseNaturalOrder());
        set.forEach(from, to, outputSet::add);
        Verify.assertSortedSetsEqual(expectedOutput, outputSet);
    }

    @Test
    public void indexOf()
    {
        MutableSortedSet<Integer> objects = this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 1);
        assertEquals(0, objects.indexOf(3));
        assertEquals(1, objects.indexOf(2));
        assertEquals(2, objects.indexOf(1));
        assertEquals(-1, objects.indexOf(0));
    }

    @Test
    public void detectIndex()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        assertEquals(1, integers1.detectIndex(integer -> integer % 2 == 0));
        assertEquals(0, integers1.detectIndex(integer -> integer % 2 != 0));
        assertEquals(-1, integers1.detectIndex(integer -> integer % 5 == 0));

        MutableSortedSet<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        assertEquals(0, integers2.detectIndex(integer -> integer % 2 == 0));
        assertEquals(1, integers2.detectIndex(integer -> integer % 2 != 0));
        assertEquals(-1, integers2.detectIndex(integer -> integer % 5 == 0));
    }

    @Test
    public void detectLastIndex()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        assertEquals(3, integers1.detectLastIndex(integer -> integer % 2 == 0));
        assertEquals(2, integers1.detectLastIndex(integer -> integer % 2 != 0));
        assertEquals(-1, integers1.detectLastIndex(integer -> integer % 5 == 0));

        MutableSortedSet<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        assertEquals(3, integers2.detectLastIndex(integer -> integer % 2 == 0));
        assertEquals(2, integers2.detectLastIndex(integer -> integer % 2 != 0));
        assertEquals(-1, integers2.detectLastIndex(integer -> integer % 5 == 0));
    }

    @Test
    public void toReversed()
    {
        MutableSortedSet<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableSortedSet<Integer> reversed = integers.toReversed();
        assertEquals(
                SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 3, 2, 1),
                reversed);
    }

    @Test
    public void reverseForEach()
    {
        MutableList<Integer> list = Lists.mutable.empty();
        MutableSortedSet<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.reverseForEach(list::add);
        assertEquals(Lists.mutable.with(4, 3, 2, 1), list);
    }

    @Test
    public void reverseForEachWithIndex()
    {
        MutableList<Integer> list = Lists.mutable.empty();
        MutableSortedSet<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.reverseForEachWithIndex((each, index) -> list.add(each + index));
        assertEquals(Lists.mutable.with(7, 6, 5, 4), list);
    }

    @Test
    public void take()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        assertEquals(SortedSets.mutable.of(integers1.comparator()), integers1.take(0));
        assertSame(integers1.comparator(), integers1.take(0).comparator());
        assertEquals(SortedSets.mutable.of(integers1.comparator(), 1, 2, 3), integers1.take(3));
        assertSame(integers1.comparator(), integers1.take(3).comparator());
        assertEquals(SortedSets.mutable.of(integers1.comparator(), 1, 2, 3), integers1.take(integers1.size() - 1));

        MutableSortedSet<Integer> expectedSet = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        MutableSortedSet<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        assertEquals(expectedSet, integers2.take(integers2.size()));
        assertEquals(expectedSet, integers2.take(10));
        assertEquals(expectedSet, integers2.take(Integer.MAX_VALUE));
    }

    @Test
    public void take_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith(1, 2, 3).take(-1));
    }

    @Test
    public void drop()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        assertEquals(integers1, integers1.drop(0));
        assertSame(integers1.comparator(), integers1.drop(0).comparator());
        assertNotSame(integers1, integers1.drop(0));
        assertEquals(SortedSets.mutable.of(integers1.comparator(), 4), integers1.drop(3));
        assertSame(integers1.comparator(), integers1.drop(3).comparator());
        assertEquals(SortedSets.mutable.of(integers1.comparator(), 4), integers1.drop(integers1.size() - 1));

        MutableSortedSet<Integer> expectedSet = SortedSets.mutable.of(Comparators.reverseNaturalOrder());
        MutableSortedSet<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        assertEquals(expectedSet, integers2.drop(integers2.size()));
        assertEquals(expectedSet, integers2.drop(10));
        assertEquals(expectedSet, integers2.drop(Integer.MAX_VALUE));
    }

    @Test
    public void drop_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith(1, 2, 3).drop(-1));
    }

    @Test
    public void getFirstOptional()
    {
        assertEquals(Integer.valueOf(1), this.newWith(1, 2).getFirstOptional().get());
        assertTrue(this.newWith(1, 2).getFirstOptional().isPresent());
        assertFalse(this.newWith().getFirstOptional().isPresent());
    }

    @Test
    public void getLastOptional()
    {
        assertEquals(Integer.valueOf(2), this.newWith(1, 2).getLastOptional().get());
        assertTrue(this.newWith(1, 2).getLastOptional().isPresent());
        assertFalse(this.newWith().getLastOptional().isPresent());
    }
}
