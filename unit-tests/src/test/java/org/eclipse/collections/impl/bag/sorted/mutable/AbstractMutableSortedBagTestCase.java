/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.sorted.mutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBagIterable;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.multimap.sortedbag.MutableSortedBagMultimap;
import org.eclipse.collections.api.partition.bag.sorted.PartitionMutableSortedBag;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.Counter;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.bag.mutable.MutableBagTestCase;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.factory.primitive.IntPredicates;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.NegativeIntervalFunction;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.factory.SortedBags;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.AddToList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Person;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract JUnit test for {@link MutableSortedBag}s.
 *
 * @since 4.2
 */
public abstract class AbstractMutableSortedBagTestCase extends MutableBagTestCase
{
    @Override
    protected abstract <T> MutableSortedBag<T> newWith(T... littleElements);

    @Override
    protected <T> MutableSortedBag<T> newWithOccurrences(ObjectIntPair<T>... elementsWithOccurrences)
    {
        MutableSortedBag<T> bag = this.newWith();
        for (ObjectIntPair<T> itemToAdd : elementsWithOccurrences)
        {
            bag.addOccurrences(itemToAdd.getOne(), itemToAdd.getTwo());
        }
        return bag;
    }

    protected abstract <T> MutableSortedBag<T> newWith(Comparator<? super T> comparator, T... elements);

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();

        Verify.assertInstanceOf(MutableSortedBag.class, this.newWith());
        Verify.assertInstanceOf(ImmutableSortedBag.class, this.newWith().toImmutable());
        assertFalse(this.newWith().toImmutable() instanceof MutableSortedBag);

        assertEquals(SortedBags.immutable.with(2, 2, 3), this.newWith(2, 2, 3).toImmutable());
    }

    @Test
    public void toString_with_collection_containing_self()
    {
        MutableCollection<Object> collection = this.newWith(1);
        assertThrows(ClassCastException.class, () -> collection.add(collection));
    }

    @Test
    public void makeString_with_collection_containing_self()
    {
        MutableCollection<Object> collection = this.newWith(1, 2, 3);
        assertThrows(ClassCastException.class, () -> collection.add(collection));
    }

    @Test
    public void appendString_with_collection_containing_self()
    {
        MutableCollection<Object> collection = this.newWith(1, 2, 3);
        assertThrows(ClassCastException.class, () -> collection.add(collection));
    }

    @Override
    @Test
    public void addAll()
    {
        super.addAll();

        TreeBag<Integer> expected = TreeBag.newBag(Comparators.reverseNaturalOrder(), FastList.newListWith(1, 1, 2, 3));
        MutableSortedBag<Integer> collection = this.newWith(Comparators.reverseNaturalOrder());

        assertTrue(collection.addAll(FastList.newListWith(3, 2, 1, 1)));
        Verify.assertSortedBagsEqual(expected, collection);
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();

        TreeBag<Integer> expected = TreeBag.newBag(Collections.reverseOrder(), FastList.newListWith(2, 1, 2, 3));
        MutableSortedBag<Integer> collection = this.newWith(Collections.reverseOrder());

        assertTrue(collection.addAllIterable(FastList.newListWith(3, 2, 1, 2)));
        Verify.assertSortedBagsEqual(expected, collection);
    }

    @Test
    public void testClone()
    {
        MutableSortedBag<Integer> set = this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3);
        MutableSortedBag<Integer> clone = set.clone();
        assertNotSame(set, clone);
        Verify.assertSortedBagsEqual(set, clone);
    }

    @Override
    @Test
    public void testToString()
    {
        super.testToString();

        assertEquals("[2, 1, 1]", this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2).toString());
        assertEquals("[3, 2, 1, 1]", this.newWith(Collections.reverseOrder(), 3, 1, 1, 2).toString());
        assertEquals("[-1, 2, 3]", this.newWith(3, -1, 2).toString());
    }

    @Override
    @Test
    public void makeString()
    {
        super.makeString();

        assertEquals("3, 3, 2", this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 3).makeString());
    }

    @Override
    @Test
    public void makeStringWithSeparator()
    {
        super.makeStringWithSeparator();

        assertEquals("3!2!-3", this.newWith(Comparators.reverseNaturalOrder(), 3, 2, -3).makeString("!"));
    }

    @Override
    @Test
    public void makeStringWithSeparatorAndStartAndEnd()
    {
        super.makeStringWithSeparatorAndStartAndEnd();

        assertEquals("<1,2,3>", this.newWith(1, 2, 3).makeString("<", ",", ">"));
    }

    @Override
    @Test
    public void appendString()
    {
        super.appendString();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 5, 5, 1, 2, 3);
        Appendable builder = new StringBuilder();
        bag.appendString(builder);
        assertEquals(bag.toString(), "[" + builder + "]");
        assertEquals("5, 5, 3, 2, 1", builder.toString());
    }

    @Override
    @Test
    public void appendStringWithSeparator()
    {
        super.appendStringWithSeparator();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 5, 5, 1, 2, 3);
        Appendable builder = new StringBuilder();
        bag.appendString(builder, ", ");
        assertEquals(bag.toString(), "[" + builder + "]");
        assertEquals("5, 5, 3, 2, 1", builder.toString());
    }

    @Override
    @Test
    public void appendStringWithSeparatorAndStartAndEnd()
    {
        super.appendStringWithSeparatorAndStartAndEnd();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 5, 5, 1, 2, 3);
        Appendable builder = new StringBuilder();
        bag.appendString(builder, "[", ", ", "]");
        assertEquals(bag.toString(), builder.toString());
        assertEquals("[5, 5, 3, 2, 1]", builder.toString());
    }

    @Override
    @Test
    public void removeObject()
    {
        super.removeObject();

        MutableSortedBag<String> bag = this.newWith(Collections.reverseOrder(), "5", "1", "2", "2", "3");
        assertFalse(bag.remove("7"));
        assertTrue(bag.remove("1"));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), "5", "3", "2", "2"), bag);
        assertTrue(bag.remove("2"));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), "5", "3", "2"), bag);
    }

    @Override
    @Test
    public void removeIf()
    {
        // Sorted containers don't support null

        MutableSortedBag<Integer> objects = this.newWith(Comparators.reverseNaturalOrder(), 4, 1, 3, 3, 2);
        assertTrue(objects.removeIf(Predicates.equal(2)));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 3, 3, 4), objects);
        assertTrue(objects.removeIf(Predicates.equal(3)));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 4), objects);
    }

    @Override
    @Test
    public void removeIfWith()
    {
        super.removeIfWith();

        MutableSortedBag<Integer> objects = this.newWith(Comparators.reverseNaturalOrder(), 4, 1, 3, 3, 2);
        assertTrue(objects.removeIfWith(Object::equals, 2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 3, 3, 4), objects);
        assertTrue(objects.removeIfWith(Object::equals, 3));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 4), objects);
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        // Sorted containers don't support null

        MutableSortedBag<Integer> sortedBag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 4);
        Verify.assertPostSerializedEqualsAndHashCode(sortedBag);

        Verify.assertEqualsAndHashCode(HashBag.newBag(sortedBag), sortedBag);

        assertNotEquals(HashBag.newBagWith(1, 1, 1, 2, 3, 4), sortedBag);
        assertNotEquals(HashBag.newBagWith(1, 1, 2, 3), sortedBag);
        assertNotEquals(HashBag.newBagWith(1, 2, 3, 4), sortedBag);
        assertNotEquals(HashBag.newBagWith(1, 2, 3, 4, 5), sortedBag);

        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 4), sortedBag);
    }

    @Override
    @Test
    public void select()
    {
        super.select();
        MutableSortedBag<Integer> integers = this.newWith(3, 3, 2, 1, 4, 5);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(3, 3, 2, 1), integers.select(Predicates.lessThanOrEqualTo(3)));
        Verify.assertEmpty(integers.select(Predicates.greaterThan(6)));
        MutableSortedBag<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 4, 3, 3, 5);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 3, 3, 2, 1), revInt.select(Predicates.lessThan(4)));
    }

    @Override
    @Test
    public void selectWith()
    {
        super.selectWith();
        Verify.assertSortedBagsEqual(
                TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 3),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 3, 1, 2, 5, 3, 6, 6).selectWith(Predicates2.lessThan(), 4));
    }

    @Override
    @Test
    public void selectWith_target()
    {
        super.selectWith_target();
        Verify.assertSortedBagsEqual(
                TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 3),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 3, 1, 2, 5, 3, 6, 6)
                        .selectWith(
                                Predicates2.lessThan(),
                                4,
                                TreeBag.newBag(Comparators.reverseNaturalOrder())));
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();
        MutableSortedBag<Integer> integers = this.newWith(4, 4, 2, 1, 3, 5, 6, 6);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4, 4), integers.reject(Predicates.greaterThan(4)));
        Verify.assertEmpty(integers.reject(Predicates.greaterThan(0)));
        MutableSortedBag<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 2, 4, 3, 5);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 3, 2, 2, 1), revInt.reject(Predicates.greaterThan(3)));
    }

    @Override
    @Test
    public void rejectWith()
    {
        super.rejectWith();
        Verify.assertSortedBagsEqual(
                TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 5, 4, 5)
                        .rejectWith(Predicates2.greaterThan(), 3));
    }

    @Override
    @Test
    public void rejectWith_target()
    {
        super.rejectWith_target();
        Verify.assertSortedBagsEqual(
                TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 5, 4, 5)
                        .rejectWith(Predicates2.greaterThan(), 3, TreeBag.newBag(Comparators.reverseNaturalOrder())));
    }

    @Override
    @Test
    public void partition()
    {
        super.partition();

        MutableSortedBag<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        PartitionMutableSortedBag<Integer> result = integers.partition(IntegerPredicates.isEven());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 2, 2), result.getSelected());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 1), result.getRejected());
    }

    @Override
    @Test
    public void partitionWith()
    {
        super.partitionWith();

        MutableSortedBag<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        PartitionMutableSortedBag<Integer> result = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isEven()));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 2, 2, 4, 4, 4, 4), result.getSelected());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 3, 3, 3), result.getRejected());
    }

    @Test
    public void partitionWhile()
    {
        MutableSortedBag<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        PartitionMutableSortedBag<Integer> partition = integers.partitionWhile(Predicates.greaterThan(3));
        Verify.assertSortedBagsEqual(this.newWith(Comparators.reverseNaturalOrder(), 6, 5, 4), partition.getSelected());
        Verify.assertSortedBagsEqual(this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 1), partition.getRejected());
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getSelected().comparator());
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void selectAndRejectWith()
    {
        super.selectAndRejectWith();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 1, 1, 1, 1, 2);
        Twin<MutableList<Integer>> result = bag.selectAndRejectWith(Object::equals, 1);
        Verify.assertSize(4, result.getOne());
        Verify.assertSize(1, result.getTwo());
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();

        MutableSortedBag<Integer> integers = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 3, 1, 1);
        MutableList<Holder> holders = integers.collect(Holder::new);
        assertEquals(FastList.newListWith(new Holder(4), new Holder(3), new Holder(1), new Holder(1)), holders);
    }

    /**
     * @since 9.1.
     */
    @Test
    public void collectWithIndex()
    {
        MutableSortedBag<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 1, 2);
        MutableList<ObjectIntPair<Integer>> expected =
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(2), 0),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1),
                        PrimitiveTuples.pair(Integer.valueOf(1), 2));
        Verify.assertListsEqual(
                expected,
                integers.collectWithIndex(PrimitiveTuples::pair));
    }

    /**
     * @since 9.1.
     */
    @Test
    public void collectWithIndexWithTarget()
    {
        MutableSortedBag<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 1, 2);
        MutableList<ObjectIntPair<Integer>> expected =
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(2), 0),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1),
                        PrimitiveTuples.pair(Integer.valueOf(1), 2));
        Verify.assertListsEqual(
                expected,
                integers.collectWithIndex(PrimitiveTuples::pair, Lists.mutable.empty()));
    }

    @Override
    @Test
    public void flatCollect()
    {
        super.flatCollect();
        MutableSortedBag<Integer> integers = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 3, 1, 1);
        MutableList<Holder> holders = integers.flatCollect(Holder.FROM_LIST);
        assertEquals(FastList.newListWith(new Holder(4), new Holder(4), new Holder(3), new Holder(3), new Holder(1), new Holder(1), new Holder(1), new Holder(1)), holders);
    }

    @Test
    public void distinct()
    {
        MutableSortedBag<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2, 3, 3, 4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1), integers.distinct());
    }

    @Test
    public void takeWhile()
    {
        MutableSortedBag<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2, 3, 3, 4, 4);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 4), integers.takeWhile(IntegerPredicates.isEven()));
        Verify.assertSortedBagsEqual(TreeBag.newBag(Comparators.reverseNaturalOrder()), integers.takeWhile(IntegerPredicates.isOdd()));
        MutableSortedBag<Integer> take = integers.takeWhile(Predicates.greaterThan(2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 4, 3, 3), take);
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), take.comparator());
    }

    @Test
    public void dropWhile()
    {
        MutableSortedBag<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2, 3, 3, 4, 4);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 2, 2, 1, 1), integers.dropWhile(IntegerPredicates.isEven()));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 4, 3, 3, 2, 2, 1, 1), integers.dropWhile(IntegerPredicates.isOdd()));
        MutableSortedBag<Integer> drop = integers.dropWhile(Predicates.greaterThan(2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 2, 2, 1, 1), drop);
        assertEquals(Comparators.<Integer>reverseNaturalOrder(), drop.comparator());
    }

    @Override
    @Test
    public void collectIf()
    {
        super.collectIf();

        assertEquals(
                this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3, 4, 5, 5).collectIf(
                        Predicates.lessThan(4),
                        Holder::new),
                FastList.newListWith(new Holder(3), new Holder(2), new Holder(1), new Holder(1), new Holder(1)));
    }

    @Override
    @Test
    public void collectWith()
    {
        super.collectWith();
        MutableSortedBag<Integer> integers = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 3, 1, 1);
        MutableList<Holder> holders = integers.collectWith(Holder.FROM_INT_INT, 10);
        assertEquals(FastList.newListWith(new Holder(14), new Holder(13), new Holder(11), new Holder(11)), holders);
    }

    @Override
    @Test
    public void collectWith_target()
    {
        super.collectWith_target();
        MutableSortedBag<Integer> integers = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 3, 1, 1);
        MutableSortedBag<Holder> holders = integers.collectWith(Holder.FROM_INT_INT, 10, TreeBag.newBag(Functions.toIntComparator(Holder.TO_NUMBER)));
        assertEquals(TreeBag.newBagWith(Functions.toIntComparator(Holder.TO_NUMBER), new Holder(14), new Holder(13), new Holder(11), new Holder(11)), holders);
    }

    @Override
    @Test
    public void groupBy()
    {
        super.groupBy();
        MutableSortedBag<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Function<Integer, Boolean> isOddFunction = object -> IntegerPredicates.isOdd().accept(object);
        MutableSortedBagMultimap<Boolean, Integer> map = integers.groupBy(isOddFunction);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 9, 7, 5, 3, 1, 1, 1), map.get(Boolean.TRUE));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 8, 6, 4, 2), map.get(Boolean.FALSE));
        Verify.assertSize(2, map.keysView().toList());
    }

    @Override
    @Test
    public void groupByEach()
    {
        super.groupByEach();
        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        NegativeIntervalFunction function = new NegativeIntervalFunction();
        MutableSortedBagMultimap<Integer, Integer> expected =
                this.newWith(Collections.<Integer>reverseOrder()).groupByEach(function);

        for (int i = 1; i < 10; i++)
        {
            expected.putAll(-i, Interval.fromTo(i, 9));
        }

        expected.put(-1, 1);
        expected.put(-1, 1);

        MutableSortedBagMultimap<Integer, Integer> actual = bag.groupByEach(function);
        assertEquals(expected, actual);
        MutableSortedBagMultimap<Integer, Integer> actualWithTarget =
                bag.groupByEach(function, this.<Integer>newWith().groupByEach(function));
        assertEquals(expected, actualWithTarget);
        for (int i = 1; i < 10; ++i)
        {
            Verify.assertSortedBagsEqual(expected.get(-i), actual.get(-i));
        }
        Verify.assertSize(9, actual.keysView().toList());
        Verify.assertSize(9, actualWithTarget.keysView().toList());
    }

    @Override
    @Test
    public void zip()
    {
        super.zip();
        MutableSortedBag<Integer> revInt = this.newWith(Collections.reverseOrder(), 2, 2, 3, 5, 1, 4);
        MutableSortedBag<Integer> integers = this.newWith(1, 3, 2, 2, 4, 5);
        MutableList<Pair<Integer, Integer>> zip = integers.zip(revInt);
        Verify.assertSize(6, zip);

        assertEquals(
                FastList.newListWith(
                        Tuples.pair(1, 5), Tuples.pair(2, 4), Tuples.pair(2, 3), Tuples.pair(3, 2), Tuples.pair(4, 2), Tuples.pair(5, 1)),
                zip);

        MutableList<Pair<Integer, Integer>> revZip = revInt.zip(integers);
        Verify.assertSize(6, revZip);

        assertEquals(
                FastList.newListWith(
                        Tuples.pair(5, 1), Tuples.pair(4, 2), Tuples.pair(3, 2), Tuples.pair(2, 3), Tuples.pair(2, 4), Tuples.pair(1, 5)),
                revZip);

        Person john = new Person("John", "Smith");
        Person johnDoe = new Person("John", "Doe");
        MutableSortedBag<Person> people = this.newWith(john, johnDoe);
        MutableList<Integer> list = FastList.newListWith(1, 2, 3);
        MutableList<Pair<Person, Integer>> pairs = people.zip(list);
        assertEquals(
                FastList.newListWith(Tuples.pair(johnDoe, 1), Tuples.pair(john, 2)),
                pairs);
        assertTrue(pairs.add(Tuples.pair(new Person("Jack", "Baker"), 3)));
        assertEquals(Tuples.pair(new Person("Jack", "Baker"), 3), pairs.getLast());
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        super.zipWithIndex();
        MutableSortedBag<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 3, 5, 5, 5, 2, 4);
        assertEquals(
                UnifiedSet.newSetWith(
                        Tuples.pair(5, 0),
                        Tuples.pair(5, 1),
                        Tuples.pair(5, 2),
                        Tuples.pair(4, 3),
                        Tuples.pair(3, 4),
                        Tuples.pair(2, 5),
                        Tuples.pair(1, 6)),
                integers.zipWithIndex());
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        MutableSortedBag<Number> numbers = this.newWith((Number o1, Number o2) -> Double.compare(o2.doubleValue(), o1.doubleValue()), 5, 4.0, 3, 2.0, 1, 1);
        MutableSortedBag<Integer> integers = numbers.selectInstancesOf(Integer.class);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 5, 3, 1, 1), integers);
    }

    @Override
    @Test
    public void toStringOfItemToCount()
    {
        super.toStringOfItemToCount();

        assertEquals("{}", this.newWith().toStringOfItemToCount());
        assertEquals("{}", this.newWith(Comparators.reverseNaturalOrder()).toStringOfItemToCount());

        assertEquals("{1=1, 2=2}", this.newWith(1, 2, 2).toStringOfItemToCount());
        assertEquals("{2=2, 1=1}", this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 2).toStringOfItemToCount());
    }

    @Override
    @Test
    public void add()
    {
        super.add();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder());
        assertTrue(bag.add(1));
        Verify.assertSortedBagsEqual(this.newWith(1), bag);
        assertTrue(bag.add(3));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 3, 1), bag);
        Verify.assertSize(2, bag);
        assertTrue(bag.add(2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 3, 2, 1), bag);
        Verify.assertSize(3, bag);
        assertTrue(bag.add(2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 3, 2, 2, 1), bag);
        Verify.assertSize(4, bag);
    }

    @Override
    @Test
    public void iterator()
    {
        super.iterator();

        MutableSortedBag<Integer> bag = this.newWith(-1, 0, 1, 1, 2);
        Iterator<Integer> iterator = bag.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(-1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(0), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertFalse(iterator.hasNext());

        MutableSortedBag<Integer> revBag = this.newWith(Comparators.reverseNaturalOrder(), -1, 0, 1, 1, 2);
        Iterator<Integer> revIterator = revBag.iterator();
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(2), revIterator.next());
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(1), revIterator.next());
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(1), revIterator.next());
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(0), revIterator.next());
        assertTrue(revIterator.hasNext());
        assertEquals(Integer.valueOf(-1), revIterator.next());
        assertFalse(revIterator.hasNext());

        MutableSortedBag<Integer> sortedBag = this.newWith(Collections.reverseOrder(), 1, 1, 1, 1, 2);
        MutableList<Integer> validate = Lists.mutable.empty();
        for (Integer each : sortedBag)
        {
            validate.add(each);
        }
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 1, 1, 1, 1, 2), TreeBag.newBag(Collections.reverseOrder(), validate));

        Iterator<Integer> sortedBagIterator = sortedBag.iterator();
        MutableSortedBag<Integer> expected = this.newWith(Collections.reverseOrder(), 1, 1, 1, 1, 2);
        assertThrows(IllegalStateException.class, sortedBagIterator::remove);

        this.assertIteratorRemove(sortedBag, sortedBagIterator, expected);
        this.assertIteratorRemove(sortedBag, sortedBagIterator, expected);
        this.assertIteratorRemove(sortedBag, sortedBagIterator, expected);
        this.assertIteratorRemove(sortedBag, sortedBagIterator, expected);
        this.assertIteratorRemove(sortedBag, sortedBagIterator, expected);
        Verify.assertEmpty(sortedBag);
        assertFalse(sortedBagIterator.hasNext());
        assertThrows(NoSuchElementException.class, sortedBagIterator::next);
    }

    private void assertIteratorRemove(MutableSortedBag<Integer> bag, Iterator<Integer> iterator, MutableSortedBag<Integer> expected)
    {
        assertTrue(iterator.hasNext());
        Integer first = iterator.next();
        iterator.remove();
        expected.remove(first);
        Verify.assertSortedBagsEqual(expected, bag);
        assertThrows(IllegalStateException.class, iterator::remove);
    }

    @Override
    @Test
    public void iterator_throws()
    {
        super.iterator_throws();

        MutableSortedBag<Integer> revBag = this.newWith(Comparators.reverseNaturalOrder(), -1, 0, 1, 1, 2);
        Iterator<Integer> revIterator = revBag.iterator();
        assertTrue(revIterator.hasNext());
        revIterator.next();
        assertTrue(revIterator.hasNext());
        revIterator.next();
        assertTrue(revIterator.hasNext());
        revIterator.next();
        assertTrue(revIterator.hasNext());
        revIterator.next();
        assertTrue(revIterator.hasNext());
        revIterator.next();
        assertFalse(revIterator.hasNext());
        assertThrows(NoSuchElementException.class, () -> revIterator.next());
    }

    @Override
    @Test
    public void tap()
    {
        super.tap();

        MutableList<Integer> tapResult = FastList.newList();
        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 1, 1, 2);
        assertSame(bag, bag.tap(tapResult::add));
        assertEquals(bag.toList(), tapResult);
    }

    @Override
    @Test
    public void forEach()
    {
        super.forEach();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 1, 1, 2);
        MutableList<Integer> actual = FastList.newList();
        bag.forEach(Procedures.cast(actual::add));
        assertEquals(FastList.newListWith(2, 1, 1), actual);
    }

    @Test
    public void forEachFromTo()
    {
        MutableSortedBag<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        MutableList<Integer> result = Lists.mutable.empty();
        integers.forEach(5, 7, result::add);
        assertEquals(Lists.immutable.with(3, 3, 2), result);

        MutableList<Integer> result2 = Lists.mutable.empty();
        integers.forEach(5, 5, result2::add);
        assertEquals(Lists.immutable.with(3), result2);

        MutableList<Integer> result3 = Lists.mutable.empty();
        integers.forEach(0, 9, result3::add);
        assertEquals(Lists.immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), result3);

        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(-1, 0, result::add));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, -1, result::add));
        assertThrows(IllegalArgumentException.class, () -> integers.forEach(7, 5, result::add));
    }

    @Test
    public void forEachWithIndexWithFromTo()
    {
        MutableSortedBag<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        StringBuilder builder = new StringBuilder();
        integers.forEachWithIndex(5, 7, (each, index) -> builder.append(each).append(index));
        assertEquals("353627", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        integers.forEachWithIndex(5, 5, (each, index) -> builder2.append(each).append(index));
        assertEquals("35", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        integers.forEachWithIndex(0, 9, (each, index) -> builder3.append(each).append(index));
        assertEquals("40414243343536272819", builder3.toString());

        MutableList<Integer> result = Lists.mutable.empty();
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(-1, 0, new AddToList(result)));
        assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, -1, new AddToList(result)));
        assertThrows(IllegalArgumentException.class, () -> integers.forEachWithIndex(7, 5, new AddToList(result)));
    }

    @Override
    @Test
    public void forEachWithOccurrences()
    {
        super.forEachWithOccurrences();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 3, 3, 3, 2, 2, 1);
        MutableList<Integer> actualItems = FastList.newList();
        MutableList<Integer> actualIndexes = FastList.newList();
        bag.forEachWithOccurrences((each, index) ->
        {
            actualItems.add(each);
            actualIndexes.add(index);
        });
        assertEquals(FastList.newListWith(3, 2, 1), actualItems);
        assertEquals(FastList.newListWith(3, 2, 1), actualIndexes);

        MutableSortedBag<Integer> bag2 = this.newWith();
        bag2.addOccurrences(1, 10);
        bag2.addOccurrences(2, 10);
        bag2.addOccurrences(3, 10);
        IntegerSum sum = new IntegerSum(0);
        Counter counter = new Counter();
        bag2.forEachWithOccurrences((each, occurrences) ->
        {
            counter.increment();
            sum.add(each * occurrences * counter.getCount());
        });
        assertEquals(140, sum.getIntSum());
        bag2.removeOccurrences(2, 1);
        IntegerSum sum2 = new IntegerSum(0);
        bag2.forEachWithOccurrences((each, occurrences) -> sum2.add(each * occurrences));
        assertEquals(58, sum2.getIntSum());
        bag2.removeOccurrences(1, 3);
        IntegerSum sum3 = new IntegerSum(0);
        bag2.forEachWithOccurrences((each, occurrences) -> sum3.add(each * occurrences));
        assertEquals(55, sum3.getIntSum());
    }

    @Test
    @Override
    public void getFirst()
    {
        super.getFirst();

        assertEquals(Integer.valueOf(0), this.newWith(0, 0, 1, 1).getFirst());
        assertEquals(Integer.valueOf(1), this.newWith(1, 1, 2, 3).getFirst());
        assertEquals(Integer.valueOf(1), this.newWith(2, 1, 3, 2, 3).getFirst());
        assertEquals(Integer.valueOf(3), this.newWith(Collections.reverseOrder(), 2, 2, 1, 3).getFirst());
    }

    @Test
    @Override
    public void getLast()
    {
        super.getLast();

        assertEquals(Integer.valueOf(1), this.newWith(0, 0, 1, 1).getLast());
        assertEquals(Integer.valueOf(3), this.newWith(1, 1, 2, 3).getLast());
        assertEquals(Integer.valueOf(3), this.newWith(3, 2, 3, 2, 3).getLast());
        assertEquals(Integer.valueOf(1), this.newWith(Collections.reverseOrder(), 2, 2, 1, 3).getLast());
    }

    @Test
    public void indexOf()
    {
        MutableSortedBag<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        assertEquals(0, integers.indexOf(4));
        assertEquals(4, integers.indexOf(3));
        assertEquals(7, integers.indexOf(2));
        assertEquals(9, integers.indexOf(1));
        assertEquals(-1, integers.indexOf(0));
    }

    @Override
    @Test
    public void occurrencesOf()
    {
        super.occurrencesOf();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 1, 1, 2);
        assertEquals(2, bag.occurrencesOf(1));
        assertEquals(1, bag.occurrencesOf(2));
    }

    @Override
    @Test
    public void addOccurrences()
    {
        super.addOccurrences();

        MutableSortedBag<Integer> bag = this.newWith();
        assertEquals(3, bag.addOccurrences(0, 3));
        assertEquals(0, bag.addOccurrences(2, 0));
        assertEquals(2, bag.addOccurrences(1, 2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(0, 0, 0, 1, 1), bag);
        assertEquals(6, bag.addOccurrences(0, 3));
        assertEquals(4, bag.addOccurrences(1, 2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(0, 0, 0, 0, 0, 0, 1, 1, 1, 1), bag);

        MutableSortedBag<Integer> revBag = this.newWith(Collections.reverseOrder());
        assertEquals(3, revBag.addOccurrences(2, 3));
        assertEquals(2, revBag.addOccurrences(3, 2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 3, 3, 2, 2, 2), revBag);
        assertEquals(6, revBag.addOccurrences(2, 3));
        assertEquals(4, revBag.addOccurrences(3, 2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 3, 3, 3, 3, 2, 2, 2, 2, 2, 2), revBag);
    }

    @Override
    @Test
    public void removeOccurrences()
    {
        super.removeOccurrences();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 2, 2, 1);
        MutableSortedBag<Integer> expected = TreeBag.newBag(bag);

        assertFalse(bag.removeOccurrences(4, 2));
        assertFalse(bag.removeOccurrences(4, 0));
        assertFalse(bag.removeOccurrences(2, 0));
        Verify.assertSortedBagsEqual(expected, bag);

        assertTrue(bag.removeOccurrences(2, 2));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1), bag);

        assertTrue(bag.removeOccurrences(1, 100));
        Verify.assertSortedBagsEqual(TreeBag.<String>newBag(), bag);
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 3, 3, 2);
        assertEquals(FastList.newListWith(3, 3, 2, 1), bag.toList());
    }

    @Override
    @Test
    public void toSet()
    {
        super.toSet();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 3, 3, 3, 2, 2, 1);
        assertEquals(UnifiedSet.newSetWith(1, 2, 3), bag.toSet());
    }

    @Override
    @Test
    public void toBag()
    {
        super.toBag();

        assertEquals(
                Bags.mutable.of("C", "C", "B", "A"),
                this.newWith(Comparators.reverseNaturalOrder(), "C", "C", "B", "A").toBag());
    }

    @Test
    public void toStack()
    {
        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 3, 3, 2);
        assertEquals(ArrayStack.newStackFromTopToBottom(1, 2, 3, 3), bag.toStack());
    }

    @Override
    @Test
    public void toSortedList_natural_ordering()
    {
        super.toSortedList_natural_ordering();

        assertEquals(
                FastList.newListWith(1, 2, 3, 4, 4),
                this.newWith(Comparators.reverseNaturalOrder(), 4, 4, 3, 1, 2).toSortedList());
    }

    @Override
    @Test
    public void toSortedList_with_comparator()
    {
        super.toSortedList_with_comparator();

        assertEquals(
                FastList.newListWith(4, 4, 3, 2, 1),
                this.newWith(Comparators.reverseNaturalOrder(), 4, 4, 3, 1, 2).toSortedList(Comparators.reverseNaturalOrder()));
    }

    @Override
    @Test
    public void toSortedListBy()
    {
        super.toSortedListBy();

        MutableSortedBag<Integer> sortedBag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 10, 2, 3);
        MutableList<Integer> sortedListBy = sortedBag.toSortedListBy(String::valueOf);
        assertEquals(FastList.newListWith(1, 1, 1, 10, 2, 3), sortedListBy);
    }

    @Override
    @Test
    public void toSortedSet_natural_ordering()
    {
        super.toSortedSet_natural_ordering();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 2, 2, 1, 5, 4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 4, 5), bag.toSortedSet());
    }

    @Override
    @Test
    public void toSortedSet_with_comparator()
    {
        super.toSortedSet_with_comparator();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 2, 2, 1, 5, 4);
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 5, 4, 2, 1),
                bag.toSortedSet(Comparators.reverseNaturalOrder()));
    }

    @Override
    @Test
    public void toSortedSetBy()
    {
        super.toSortedSetBy();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 2, 5, 2, 4, 3, 1, 6, 7, 8, 9, 10);
        assertEquals(
                UnifiedSet.newSetWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                bag.toSortedSetBy(String::valueOf));
        assertEquals(
                FastList.newListWith(1, 10, 2, 3, 4, 5, 6, 7, 8, 9),
                bag.toSortedSetBy(String::valueOf).toList());
    }

    @Override
    @Test
    public void toMap()
    {
        super.toMap();

        assertEquals(
                UnifiedMap.newWithKeysValues("4", "4", "3", "3", "2", "2", "1", "1"),
                this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1).toMap(String::valueOf, String::valueOf));
    }

    @Override
    @Test
    public void toSortedMap()
    {
        super.toSortedMap();

        Verify.assertSortedMapsEqual(
                TreeSortedMap.newMapWith(3, "3", 2, "2", 1, "1"),
                this.newWith(3, 2, 1).toSortedMap(Functions.getIntegerPassThru(), String::valueOf));
    }

    @Override
    @Test
    public void toSortedMap_with_comparator()
    {
        super.toSortedMap_with_comparator();

        Verify.assertSortedMapsEqual(
                TreeSortedMap.newMapWith(Comparators.reverseNaturalOrder(), 3, "3", 2, "2", 1, "1"),
                this.newWith(3, 2, 1, 1).toSortedMap(
                        Comparators.reverseNaturalOrder(),
                        Functions.getIntegerPassThru(),
                        String::valueOf));
    }

    @Override
    @Test
    public void toSortedMapBy()
    {
        super.toSortedMapBy();

        Verify.assertSortedMapsEqual(
                TreeSortedMap.newMapWith(Comparators.reverseNaturalOrder(), 3, "3", 2, "2", 1, "1"),
                this.newWith(3, 2, 1, 1).toSortedMapBy(
                        key -> -key,
                        Functions.getIntegerPassThru(),
                        String::valueOf));
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedBag.class, this.newWith().asUnmodifiable());
        Verify.assertSortedBagsEqual(this.newWith(), this.newWith().asUnmodifiable());
    }

    @Override
    @Test
    public void serialization()
    {
        super.serialization();

        MutableSortedBag<String> bag = this.newWith(Comparators.reverseNaturalOrder(), "One", "Two", "Two", "Three", "Three", "Three");
        Verify.assertPostSerializedEqualsAndHashCode(bag);
        assertNotNull(bag.comparator());
    }

    @Override
    @Test
    public void selectByOccurrences()
    {
        super.selectByOccurrences();

        MutableSortedBag<Integer> integers = this.newWith(Collections.reverseOrder(), 4, 3, 3, 2, 2, 2, 1, 1, 1, 1);
        Verify.assertSortedBagsEqual(
                TreeBag.newBagWith(Collections.reverseOrder(), 3, 3, 1, 1, 1, 1),
                integers.selectByOccurrences(IntPredicates.isEven()));
    }

    @Override
    @Test
    public void selectDuplicates()
    {
        super.selectDuplicates();

        MutableSortedBag<Integer> integers = this.newWith(Collections.reverseOrder(), 5, 4, 3, 3, 2, 2, 2, 1, 1, 1, 1, 0);
        Verify.assertSortedBagsEqual(
                TreeBag.newBagWith(Collections.reverseOrder(), 3, 3, 2, 2, 2, 1, 1, 1, 1),
                integers.selectDuplicates());
    }

    @Override
    @Test
    public void toMapOfItemToCount()
    {
        super.toMapOfItemToCount();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 1, 2, 2, 3, 3, 3);
        assertEquals(TreeSortedMap.newMapWith(Collections.reverseOrder(), 1, 1, 2, 2, 3, 3), bag.toMapOfItemToCount());
    }

    @Test
    public void compareTo()
    {
        assertEquals(-1, this.newWith(1, 1, 2, 2).compareTo(this.newWith(1, 1, 2, 2, 2)));
        assertEquals(0, this.newWith(1, 1, 2, 2).compareTo(this.newWith(1, 1, 2, 2)));
        assertEquals(1, this.newWith(1, 1, 2, 2, 2).compareTo(this.newWith(1, 1, 2, 2)));

        assertEquals(-1, this.newWith(1, 1, 2, 2).compareTo(this.newWith(1, 1, 3, 3)));
        assertEquals(1, this.newWith(1, 1, 3, 3).compareTo(this.newWith(1, 1, 2, 2)));

        assertEquals(1, this.newWith(Comparators.reverseNaturalOrder(), 2, 2, 1, 1, 1).compareTo(this.newWith(Comparators.reverseNaturalOrder(), 2, 2, 1, 1)));
        assertEquals(1, this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2).compareTo(this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2, 2)));
        assertEquals(0, this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2).compareTo(this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2)));
        assertEquals(-1, this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2, 2).compareTo(this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2)));

        assertEquals(1, this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2).compareTo(this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 3, 3)));
        assertEquals(-1, this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 3, 3).compareTo(this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 2)));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        super.containsAllIterable();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 4);
        assertTrue(bag.containsAllIterable(FastList.newListWith(1, 2)));
        assertFalse(bag.containsAllIterable(FastList.newListWith(1, 5)));
        assertTrue(bag.containsAllIterable(FastList.newListWith()));
    }

    @Override
    @Test
    public void containsAllArray()
    {
        super.containsAllArray();
        MutableSortedBag<Integer> collection = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3, 4);
        assertTrue(collection.containsAllArguments(1, 2));
        assertFalse(collection.containsAllArguments(1, 5));
        assertTrue(collection.containsAllArguments());
    }

    @Override
    @Test
    public void forEachWith()
    {
        super.forEachWith();

        MutableSortedBag<String> bag = this.newWith(Collections.reverseOrder(), "1", "2", "2", "3", "4");
        StringBuilder builder = new StringBuilder();
        bag.forEachWith((argument1, argument2) -> builder.append(argument1).append(argument2), 0);
        assertEquals("4030202010", builder.toString());
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();

        MutableSortedBag<String> bag = this.newWith(Collections.reverseOrder(), "1", "2", "2", "3", "4");
        StringBuilder builder = new StringBuilder();
        bag.forEachWithIndex((each, index) -> builder.append(each).append(index));
        assertEquals("4031222314", builder.toString());
    }

    @Override
    @Test
    public void collectBoolean()
    {
        super.collectBoolean();

        MutableSortedBag<String> bag = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), "true", "nah", "TrUe");
        assertEquals(
                BooleanArrayList.newListWith(true, false, true),
                bag.collectBoolean(Boolean::parseBoolean));
    }

    @Override
    @Test
    public void collectByte()
    {
        super.collectByte();
        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3);
        assertEquals(
                ByteArrayList.newListWith((byte) 3, (byte) 2, (byte) 1, (byte) 1, (byte) 1),
                bag.collectByte(PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Override
    @Test
    public void collectChar()
    {
        super.collectChar();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3);
        assertEquals(
                CharArrayList.newListWith((char) 3, (char) 2, (char) 1, (char) 1, (char) 1),
                bag.collectChar(PrimitiveFunctions.unboxIntegerToChar()));
    }

    @Override
    @Test
    public void collectDouble()
    {
        super.collectDouble();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3);
        assertEquals(
                DoubleArrayList.newListWith(3, 2, 1, 1, 1),
                bag.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Override
    @Test
    public void collectFloat()
    {
        super.collectFloat();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3);
        assertEquals(
                FloatArrayList.newListWith(3, 2, 1, 1, 1),
                bag.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Override
    @Test
    public void collectInt()
    {
        super.collectInt();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3);
        assertEquals(
                IntArrayList.newListWith(3, 2, 1, 1, 1),
                bag.collectInt(PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Override
    @Test
    public void collectLong()
    {
        super.collectLong();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3);
        assertEquals(
                LongArrayList.newListWith(3, 2, 1, 1, 1),
                bag.collectLong(PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Override
    @Test
    public void collectShort()
    {
        super.collectShort();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3);
        assertEquals(
                ShortArrayList.newListWith((short) 3, (short) 2, (short) 1, (short) 1, (short) 1),
                bag.collectShort(PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Override
    @Test
    public void detect()
    {
        super.detect();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3);
        assertEquals(Integer.valueOf(2), bag.detect(Predicates.lessThan(3)));
        assertNull(bag.detect(Integer.valueOf(4)::equals));
    }

    @Override
    @Test
    public void min()
    {
        super.min();

        assertEquals(
                Integer.valueOf(1),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 4).min());
        assertEquals(
                Integer.valueOf(4),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4).min(Comparators.reverseNaturalOrder()));
    }

    @Override
    @Test
    public void max()
    {
        super.max();

        assertEquals(
                Integer.valueOf(1),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 4).min());
        assertEquals(
                Integer.valueOf(4),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3, 4).min(Comparators.reverseNaturalOrder()));
    }

    @Override
    @Test
    public void minBy()
    {
        super.minBy();

        assertEquals(
                Integer.valueOf(1),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 3).minBy(String::valueOf));
    }

    @Override
    @Test
    public void maxBy()
    {
        super.maxBy();

        assertEquals(
                Integer.valueOf(3),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3).maxBy(String::valueOf));
    }

    @Override
    @Test
    public void detectWith()
    {
        super.detectWith();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4, 4, 5);
        assertEquals(Integer.valueOf(5), bag.detectWith(Predicates2.greaterThan(), 3));
        assertEquals(Integer.valueOf(2), bag.detectWith(Predicates2.lessThan(), 3));
        assertNull(this.newWith(1, 2, 3, 4, 5).detectWith(Object::equals, 6));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        super.detectIfNone();

        Function0<Integer> function = new PassThruFunction0<>(6);
        assertEquals(Integer.valueOf(3), this.newWith(Comparators.reverseNaturalOrder(), 2, 3, 4, 5).detectIfNone(Integer.valueOf(3)::equals, function));
        assertEquals(Integer.valueOf(3), this.newWith(Comparators.reverseNaturalOrder(), 2, 3, 4, 5).detectIfNone(Integer.valueOf(3)::equals, null));
        assertEquals(Integer.valueOf(6), this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4, 5).detectIfNone(Integer.valueOf(6)::equals, function));
    }

    @Override
    @Test
    public void detectWithIfNoneBlock()
    {
        super.detectWithIfNoneBlock();

        Function0<Integer> function = new PassThruFunction0<>(-42);
        assertEquals(
                Integer.valueOf(5),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 1, 2, 3, 4, 5).detectWithIfNone(
                        Predicates2.greaterThan(),
                        4,
                        function));
        assertEquals(
                Integer.valueOf(-42),
                this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 2, 2, 3, 4, 5).detectWithIfNone(
                        Predicates2.lessThan(),
                        0,
                        function));
    }

    @Test
    public void corresponds()
    {
        assertFalse(this.newWith(1, 2, 3, 4, 5).corresponds(this.newWith(1, 2, 3, 4), Predicates2.alwaysTrue()));

        MutableSortedBag<Integer> integers1 = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        MutableSortedBag<Integer> integers2 = this.newWith(2, 3, 3, 4, 4, 4, 5, 5, 5, 5);
        assertTrue(integers1.corresponds(integers2, Predicates2.lessThan()));
        assertFalse(integers1.corresponds(integers2, Predicates2.greaterThan()));

        MutableSortedBag<Integer> integers3 = this.newWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        assertFalse(integers3.corresponds(integers1, Predicates2.equal()));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();

        MutableSortedBag<Integer> bag = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1);
        assertTrue(bag.allSatisfy(Predicates.lessThan(4)));
        assertFalse(bag.allSatisfy(Integer.valueOf(2)::equals));
        assertFalse(bag.allSatisfy(Predicates.greaterThan(4)));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        super.allSatisfyWith();

        MutableSortedBag<Integer> bag = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1);
        assertTrue(bag.allSatisfyWith(Predicates2.lessThan(), 4));
        assertFalse(bag.allSatisfyWith(Object::equals, 2));
        assertFalse(bag.allSatisfyWith(Predicates2.greaterThan(), 4));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        super.noneSatisfy();

        MutableSortedBag<Integer> bag = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1);

        assertFalse(bag.noneSatisfy(Predicates.lessThan(4)));
        assertFalse(bag.noneSatisfy(Integer.valueOf(2)::equals));
        assertTrue(bag.noneSatisfy(Predicates.greaterThan(4)));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        super.noneSatisfyWith();

        MutableSortedBag<Integer> bag = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1);

        assertFalse(bag.noneSatisfyWith(Predicates2.lessThan(), 4));
        assertFalse(bag.noneSatisfyWith(Object::equals, 2));
        assertTrue(bag.noneSatisfyWith(Predicates2.greaterThan(), 4));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        super.anySatisfy();

        MutableSortedBag<Integer> bag = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1);
        assertTrue(bag.anySatisfy(Predicates.lessThan(4)));
        assertTrue(bag.anySatisfy(Integer.valueOf(2)::equals));
        assertFalse(bag.anySatisfy(Predicates.greaterThan(4)));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        super.anySatisfyWith();

        MutableSortedBag<Integer> bag = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1);
        assertTrue(bag.anySatisfyWith(Predicates2.lessThan(), 4));
        assertTrue(bag.anySatisfyWith(Object::equals, 2));
        assertFalse(bag.anySatisfyWith(Predicates2.greaterThan(), 4));
    }

    @Override
    @Test
    public void count()
    {
        super.count();

        MutableSortedBag<Integer> sortedBag = this.newWith(Collections.reverseOrder(), 3, 2, 2, 2, 1);
        assertEquals(1, sortedBag.count(Predicates.greaterThan(2)));
        assertEquals(4, sortedBag.count(Predicates.greaterThan(1)));
        assertEquals(0, sortedBag.count(Predicates.greaterThan(3)));
    }

    @Override
    @Test
    public void countWith()
    {
        super.countWith();

        MutableSortedBag<Integer> sortedBag = this.newWith(Collections.reverseOrder(), 3, 2, 2, 2, 1);
        assertEquals(1, sortedBag.countWith(Predicates2.greaterThan(), 2));
        assertEquals(4, sortedBag.countWith(Predicates2.greaterThan(), 1));
        assertEquals(0, sortedBag.countWith(Predicates2.greaterThan(), 3));
    }

    @Override
    @Test
    public void removeAllIterable()
    {
        super.removeAllIterable();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 5, 5, 3, 2, 2, 2, 1);
        assertTrue(bag.removeAllIterable(FastList.newListWith(1, 2, 4)));
        assertFalse(bag.removeAllIterable(FastList.newListWith(1, 2, 4)));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 5, 5, 3), bag);
    }

    @Override
    @Test
    public void retainAll()
    {
        super.retainAll();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 5, 5, 3, 2, 1, 1, 1);
        assertTrue(bag.retainAll(FastList.newListWith(1, 2)));
        assertFalse(bag.retainAll(FastList.newListWith(1, 2)));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 2, 1, 1, 1), bag);
    }

    @Override
    @Test
    public void retainAllIterable()
    {
        super.retainAllIterable();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 5, 5, 3, 2, 1, 1, 1);
        assertTrue(bag.retainAllIterable(FastList.newListWith(1, 2)));
        assertFalse(bag.retainAllIterable(FastList.newListWith(1, 2)));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 2, 1, 1, 1), bag);
    }

    @Override
    @Test
    public void injectInto()
    {
        super.injectInto();

        assertEquals(
                Integer.valueOf(11),
                this.newWith(Collections.reverseOrder(), 1, 1, 2, 3, 4).injectInto(Integer.valueOf(0), AddFunction.INTEGER));
    }

    @Override
    @Test
    public void injectIntoWith()
    {
        super.injectIntoWith();

        MutableSortedBag<Integer> bag = this.newWith(Collections.reverseOrder(), 1, 1, 2, 3);
        Integer result = bag.injectIntoWith(1, (injectedValued, item, parameter) -> injectedValued + item + parameter, 0);
        assertEquals(Integer.valueOf(8), result);
    }

    @Override
    @Test
    public void injectIntoInt()
    {
        super.injectIntoInt();

        assertEquals(
                11,
                this.newWith(Collections.reverseOrder(), 1, 1, 2, 3, 4).injectInto(0, AddFunction.INTEGER_TO_INT));
    }

    @Override
    @Test
    public void injectIntoLong()
    {
        super.injectIntoLong();

        assertEquals(
                8,
                this.newWith(Collections.reverseOrder(), 1, 1, 2, 3).injectInto(1L, AddFunction.INTEGER_TO_LONG));
    }

    @Override
    @Test
    public void injectIntoDouble()
    {
        super.injectIntoDouble();

        assertEquals(
                8.0,
                this.newWith(Collections.reverseOrder(), 1.0, 1.0, 2.0, 3.0).injectInto(1.0d, AddFunction.DOUBLE_TO_DOUBLE), 0.001);
    }

    @Override
    @Test
    public void injectIntoFloat()
    {
        super.injectIntoFloat();

        assertEquals(
                8.0,
                this.newWith(Collections.reverseOrder(), 1, 1, 2, 3).injectInto(1.0f, AddFunction.INTEGER_TO_FLOAT), 0.001);
    }

    @Override
    @Test
    public void sumFloat()
    {
        super.sumFloat();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 4, 5);
        assertEquals(16.0f, bag.sumOfFloat(Integer::floatValue), 0.001);
    }

    @Override
    @Test
    public void sumDouble()
    {
        super.sumDouble();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 4, 5);
        assertEquals(16.0d, bag.sumOfDouble(Integer::doubleValue), 0.001);
    }

    @Override
    @Test
    public void sumInteger()
    {
        super.sumInteger();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 4, 5);
        assertEquals(16, bag.sumOfLong(Integer::longValue));
    }

    @Override
    @Test
    public void sumLong()
    {
        super.sumLong();

        MutableSortedBag<Integer> bag = this.newWith(Comparators.reverseNaturalOrder(), 1, 1, 2, 3, 4, 5);
        assertEquals(16, bag.sumOfLong(Integer::longValue));
    }

    @Override
    @Test
    public void toArray()
    {
        super.toArray();

        assertArrayEquals(new Object[]{4, 4, 3, 2, 1}, this.newWith(Collections.reverseOrder(), 4, 4, 3, 2, 1).toArray());
        assertArrayEquals(new Integer[]{4, 4, 3, 2, 1}, this.newWith(Collections.reverseOrder(), 4, 4, 3, 2, 1).toArray(new Integer[0]));
        assertArrayEquals(new Integer[]{4, 4, 3, 2, 1, null, null}, this.newWith(Collections.reverseOrder(), 4, 4, 3, 2, 1).toArray(new Integer[7]));
    }

    @Override
    @Test
    public void chunk()
    {
        super.chunk();

        MutableSortedBag<String> bag = this.newWith(Comparators.reverseNaturalOrder(), "6", "5", "4", "3", "2", "1", "1");
        RichIterable<RichIterable<String>> groups = bag.chunk(2);
        assertEquals(
                FastList.newListWith(
                        TreeBag.newBagWith(Comparators.reverseNaturalOrder(), "6", "5"),
                        TreeBag.newBagWith(Comparators.reverseNaturalOrder(), "4", "3"),
                        TreeBag.newBagWith(Comparators.reverseNaturalOrder(), "2", "1"),
                        TreeBag.newBagWith(Comparators.reverseNaturalOrder(), "1")),
                groups);
    }

    @Override
    @Test
    public void aggregateByMutating()
    {
        super.aggregateByMutating();

        Function0<AtomicInteger> zeroValueFactory = AtomicInteger::new;
        MutableSortedBag<Integer> sortedBag = this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 2, 1, 1, 1);
        MapIterable<String, AtomicInteger> aggregation = sortedBag.aggregateInPlaceBy(String::valueOf, zeroValueFactory, AtomicInteger::addAndGet);
        assertEquals(3, aggregation.get("1").intValue());
        assertEquals(4, aggregation.get("2").intValue());
        assertEquals(3, aggregation.get("3").intValue());
    }

    @Override
    @Test
    public void aggregateByNonMutating()
    {
        super.aggregateByNonMutating();

        Function0<Integer> zeroValueFactory = () -> 0;
        Function2<Integer, Integer, Integer> sumAggregator = (integer1, integer2) -> integer1 + integer2;
        MutableSortedBag<Integer> sortedBag = this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 2, 1, 1, 1);
        MapIterable<String, Integer> aggregation = sortedBag.aggregateBy(String::valueOf, zeroValueFactory, sumAggregator);
        assertEquals(3, aggregation.get("1").intValue());
        assertEquals(4, aggregation.get("2").intValue());
        assertEquals(3, aggregation.get("3").intValue());
    }

    @Override
    @Test
    public void toSortedBag_natural_ordering()
    {
        super.toSortedBag_natural_ordering();
        RichIterable<Integer> integers = this.newWith(Comparator.reverseOrder(), 2, 2, 2, 1, 1);
        MutableSortedBag<Integer> bag = integers.toSortedBag();
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 1, 2, 2, 2), bag);
    }

    @Override
    @Test
    public void toSortedBag_with_comparator()
    {
        super.toSortedList_with_comparator();
        RichIterable<Integer> integers = this.newWith(2, 4, 4, 1);
        MutableSortedBag<Integer> bag = integers.toSortedBag(Collections.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 4, 4, 2, 1), bag);
    }

    @Override
    @Test
    public void toSortedBagBy()
    {
        super.toSortedBagBy();
        RichIterable<Integer> integers = this.newWith(2, 2, 1, 1);
        MutableSortedBag<Integer> bag = integers.toSortedBagBy(String::valueOf);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 2, 1), bag);
    }

    @Override
    @Test
    public void topOccurrences()
    {
        // Sorted containers don't support null

        MutableSortedBag<String> strings = this.newWithOccurrences(
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
        Verify.assertSize(0, this.newWith().topOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(2));
        Verify.assertSize(3, this.newWith("one", "one", "two", "three").topOccurrences(2));
        Verify.assertSize(2, this.newWith("one", "one", "two", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "one", "two", "two", "three", "three").topOccurrences(1));
        Verify.assertSize(0, this.newWith().topOccurrences(0));
        Verify.assertSize(0, this.newWith("one").topOccurrences(0));
        assertThrows(IllegalArgumentException.class, () -> this.newWith().topOccurrences(-1));
    }

    @Override
    @Test
    public void bottomOccurrences()
    {
        // Sorted containers don't support null

        MutableSortedBag<String> strings = this.newWithOccurrences(
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
        Verify.assertSize(0, this.newWith().bottomOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(2));
        Verify.assertSize(3, this.newWith("one", "one", "two", "three").topOccurrences(2));
        Verify.assertSize(2, this.newWith("one", "one", "two", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "one", "two", "two", "three", "three").bottomOccurrences(1));
        Verify.assertSize(0, this.newWith().bottomOccurrences(0));
        Verify.assertSize(0, this.newWith("one").bottomOccurrences(0));
        assertThrows(IllegalArgumentException.class, () -> this.newWith().bottomOccurrences(-1));
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
    public void detectIndex()
    {
        MutableSortedBag<Integer> integers1 = this.newWith(1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        assertEquals(2, integers1.detectIndex(integer -> integer % 2 == 0));
        assertEquals(5, integers1.detectIndex(integer -> integer % 3 == 0));
        assertEquals(0, integers1.detectIndex(integer -> integer % 2 != 0));
        assertEquals(-1, integers1.detectIndex(integer -> integer % 5 == 0));

        MutableSortedBag<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1, 1);
        assertEquals(0, integers2.detectIndex(integer -> integer % 2 == 0));
        assertEquals(4, integers2.detectIndex(integer -> integer % 3 == 0));
        assertEquals(9, integers2.detectIndex(integer -> integer == 1));
        assertEquals(-1, integers2.detectIndex(integer -> integer % 5 == 0));
    }

    @Test
    public void take()
    {
        MutableSortedBag<Integer> integers1 = this.newWith(1, 1, 1, 2);
        assertEquals(SortedBags.mutable.empty(integers1.comparator()), integers1.take(0));
        assertSame(integers1.comparator(), integers1.take(0).comparator());
        assertEquals(this.newWith(integers1.comparator(), 1, 1, 1), integers1.take(3));
        assertSame(integers1.comparator(), integers1.take(3).comparator());
        assertEquals(this.newWith(integers1.comparator(), 1, 1, 1), integers1.take(integers1.size() - 1));

        MutableSortedBag<Integer> expectedBag = this.newWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1);
        MutableSortedBag<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1);
        assertEquals(expectedBag, integers2.take(integers2.size()));
        assertEquals(expectedBag, integers2.take(10));
        assertEquals(expectedBag, integers2.take(Integer.MAX_VALUE));
    }

    @Test
    public void take_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith(1, 2, 3).take(-1));
    }

    @Test
    public void drop()
    {
        MutableSortedBag<Integer> integers1 = this.newWith(1, 1, 1, 2);
        assertEquals(integers1, integers1.drop(0));
        assertNotSame(integers1, integers1.drop(0));
        assertEquals(this.newWith(integers1.comparator(), 2), integers1.drop(3));
        assertEquals(this.newWith(integers1.comparator(), 2), integers1.drop(integers1.size() - 1));
        assertEquals(SortedBags.mutable.empty(integers1.comparator()), integers1.drop(integers1.size()));
        assertEquals(SortedBags.mutable.empty(integers1.comparator()), integers1.drop(10));
        assertEquals(SortedBags.mutable.empty(integers1.comparator()), integers1.drop(Integer.MAX_VALUE));
    }

    @Test
    public void drop_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith(1, 2, 3).drop(-1));
    }

    @Override
    @Test
    public void selectUnique()
    {
        super.selectUnique();

        Comparator<Integer> comparator = Collections.reverseOrder();
        MutableSortedBag<Integer> integers = this.newWith(comparator, 5, 4, 3, 3, 2, 2, 2, 1, 1, 1, 1, 0);
        MutableSortedSet<Integer> expected = SortedSets.mutable.of(comparator, 5, 4, 0);
        MutableSortedSet<Integer> actual = integers.selectUnique();
        assertEquals(expected, actual);
        assertEquals(expected.comparator(), actual.comparator());
    }

    @Override
    @Test
    public void distinctView()
    {
        Comparator<String> comparator = Collections.reverseOrder();
        MutableBagIterable<String> bag = this.newWith(comparator, "1", "2", "2", "3", "3", "3", "3", "4", "5", "5", "6");
        RichIterable<String> expected = bag.toSortedSet(comparator);
        RichIterable<String> actual = bag.distinctView();
        // test content/type
        assertEquals(expected, actual);
        // test sorting
        Verify.assertIterablesEqual(expected, actual);
    }

    // Like Integer, but not Comparable
    public static final class Holder
    {
        private static final Function2<Integer, Integer, Holder> FROM_INT_INT = (each, each2) -> new Holder(each + each2);
        private static final Function<Integer, MutableList<Holder>> FROM_LIST = object -> FastList.newListWith(new Holder(object), new Holder(object));
        private static final IntFunction<Holder> TO_NUMBER = holder -> holder.number;
        private final int number;

        private Holder(int i)
        {
            this.number = i;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || this.getClass() != o.getClass())
            {
                return false;
            }

            Holder holder = (Holder) o;

            return this.number == holder.number;
        }

        @Override
        public int hashCode()
        {
            return this.number;
        }

        @Override
        public String toString()
        {
            return String.valueOf(this.number);
        }
    }
}
