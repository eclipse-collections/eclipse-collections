/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.Counter;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.ObjectIntProcedures;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.MaxSizeFunction;
import org.eclipse.collections.impl.block.function.MinSizeFunction;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.block.procedure.FastListSelectProcedure;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iList;
import static org.eclipse.collections.impl.factory.Iterables.iSet;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ListIterateTest
{
    @Test
    public void injectInto()
    {
        MutableList<Integer> list = Lists.fixedSize.of(1, 2, 3);
        List<Integer> linked = new LinkedList<>(list);
        assertEquals(Integer.valueOf(7), ListIterate.injectInto(1, list, AddFunction.INTEGER));
        assertEquals(Integer.valueOf(7), ListIterate.injectInto(1, linked, AddFunction.INTEGER));
    }

    @Test
    public void toArray()
    {
        List<Integer> notAnArrayList = new LinkedList<>(Interval.oneTo(10));
        Integer[] target1 = {1, 2, null, null};
        ListIterate.toArray(notAnArrayList, target1, 2, 2);
        assertArrayEquals(target1, new Integer[]{1, 2, 1, 2});

        ArrayList<Integer> arrayList = new ArrayList<>(Interval.oneTo(10));
        Integer[] target2 = {1, 2, null, null};
        ListIterate.toArray(arrayList, target2, 2, 2);
        assertArrayEquals(target2, new Integer[]{1, 2, 1, 2});
    }

    @Test
    public void toArray_throws()
    {
        List<Integer> notAnArrayList = new LinkedList<>(Interval.oneTo(10));
        Integer[] target1 = {1, 2, null, null};
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> ListIterate.toArray(notAnArrayList, target1, 2, 10));
    }

    @Test
    public void detectIndex()
    {
        MutableList<Integer> integerList = this.getIntegerList();
        this.assertDetectIndex(integerList);
        this.assertDetectIndex(new LinkedList<>(integerList));
    }

    private void assertDetectIndex(List<Integer> integerList)
    {
        assertEquals(3, ListIterate.detectIndex(integerList, Predicates.equal(2)));
        assertEquals(-1, ListIterate.detectIndex(integerList, Predicates.equal(20)));
    }

    @Test
    public void detectLastIndex()
    {
        MutableList<Integer> integerList = this.getIntegerList();
        this.assertDetectLastIndex(integerList);
        this.assertDetectLastIndex(new LinkedList<>(integerList));
    }

    private void assertDetectLastIndex(List<Integer> integerList)
    {
        assertEquals(4, ListIterate.detectLastIndex(integerList, IntegerPredicates.isPositive()));
        assertEquals(-1, ListIterate.detectLastIndex(integerList, IntegerPredicates.isNegative()));
    }

    @Test
    public void detectIndexWith()
    {
        List<Integer> list = Interval.fromTo(5, 1);
        this.assertDetectWithIndex(list);
        this.assertDetectWithIndex(new LinkedList<>(list));
    }

    private void assertDetectWithIndex(List<Integer> list)
    {
        assertEquals(4, Iterate.detectIndexWith(list, Object::equals, 1));
        assertEquals(0, Iterate.detectIndexWith(list, Object::equals, 5));
        assertEquals(-1, Iterate.detectIndexWith(iList(), Object::equals, 5));
        assertEquals(-1, Iterate.detectIndexWith(iSet(), Object::equals, 5));
    }

    @Test
    public void forEachWith()
    {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> list = FastList.newListWith(1, 2, 3, 4);
        ListIterate.forEachWith(list, (argument1, argument2) -> result.add(argument1 + argument2), 1);
        assertEquals(FastList.newListWith(2, 3, 4, 5), result);

        List<Integer> result2 = new LinkedList<>();
        List<Integer> linkedList = new LinkedList<>(FastList.newListWith(1, 2, 3, 4));
        ListIterate.forEachWith(linkedList, (argument1, argument2) -> result2.add(argument1 + argument2), 1);
        assertEquals(FastList.newListWith(2, 3, 4, 5), result2);
    }

    @Test
    public void injectIntoInt()
    {
        MutableList<Integer> list = Lists.fixedSize.of(1, 2, 3);
        List<Integer> linked = new LinkedList<>(list);
        assertEquals(7, ListIterate.injectInto(1, list, AddFunction.INTEGER_TO_INT));
        assertEquals(7, ListIterate.injectInto(1, linked, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLong()
    {
        MutableList<Integer> list = Lists.fixedSize.of(1, 2, 3);
        List<Integer> linked = new LinkedList<>(list);
        assertEquals(7, ListIterate.injectInto(1, list, AddFunction.INTEGER_TO_LONG));
        assertEquals(7, ListIterate.injectInto(1, linked, AddFunction.INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble()
    {
        MutableList<Double> list = Lists.fixedSize.of(1.0d, 2.0d, 3.0d);
        List<Double> linked = new LinkedList<>(list);
        assertEquals(7.0d, ListIterate.injectInto(1.0, list, AddFunction.DOUBLE), 0.001);
        assertEquals(7.0d, ListIterate.injectInto(1.0, linked, AddFunction.DOUBLE), 0.001);

        assertEquals(7.0d, ListIterate.injectInto(1.0, list, AddFunction.DOUBLE_TO_DOUBLE), 0.001);
        assertEquals(7.0d, ListIterate.injectInto(1.0, linked, AddFunction.DOUBLE_TO_DOUBLE), 0.001);
    }

    @Test
    public void injectIntoFloat()
    {
        MutableList<Float> list = Lists.fixedSize.of(1.0f, 2.0f, 3.0f);
        List<Float> linked = new LinkedList<>(list);
        assertEquals(7.0f, ListIterate.injectInto(1.0f, list, AddFunction.FLOAT), 0.001);
        assertEquals(7.0f, ListIterate.injectInto(1.0f, linked, AddFunction.FLOAT), 0.001);

        assertEquals(7.0f, ListIterate.injectInto(1.0f, list, AddFunction.FLOAT_TO_FLOAT), 0.001);
        assertEquals(7.0f, ListIterate.injectInto(1.0f, linked, AddFunction.FLOAT_TO_FLOAT), 0.001);
    }

    @Test
    public void injectIntoString()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        List<String> linked = new LinkedList<>(list);
        assertEquals("0123", ListIterate.injectInto("0", list, AddFunction.STRING));
        assertEquals("0123", ListIterate.injectInto("0", linked, AddFunction.STRING));
    }

    @Test
    public void injectIntoMaxString()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "12", "123");
        List<String> linked = new LinkedList<>(list);
        Function2<Integer, String, Integer> function = MaxSizeFunction.STRING;
        assertEquals(Integer.valueOf(3), ListIterate.injectInto(Integer.MIN_VALUE, list, function));
        assertEquals(Integer.valueOf(3), ListIterate.injectInto(Integer.MIN_VALUE, linked, function));
    }

    @Test
    public void injectIntoMinString()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "12", "123");
        List<String> linked = new LinkedList<>(list);
        Function2<Integer, String, Integer> function = MinSizeFunction.STRING;
        assertEquals(Integer.valueOf(1), ListIterate.injectInto(Integer.MAX_VALUE, list, function));
        assertEquals(Integer.valueOf(1), ListIterate.injectInto(Integer.MAX_VALUE, linked, function));
    }

    @Test
    public void collect()
    {
        MutableList<Boolean> list = Lists.fixedSize.of(true, false, null);
        List<Boolean> linked = new LinkedList<>(list);
        this.assertCollect(list);
        this.assertCollect(list.asSynchronized());
        this.assertCollect(list.asUnmodifiable());
        this.assertCollect(linked);
    }

    private void assertCollect(List<Boolean> list)
    {
        MutableList<String> newCollection = ListIterate.collect(list, String::valueOf);
        Verify.assertListsEqual(newCollection, FastList.newListWith("true", "false", "null"));

        List<String> newCollection2 = ListIterate.collect(list, String::valueOf, new ArrayList<>());
        Verify.assertListsEqual(newCollection2, FastList.newListWith("true", "false", "null"));
    }

    /**
     * @since 9.1.
     */
    @Test
    public void collectWithIndex()
    {
        MutableList<Boolean> list = Lists.fixedSize.of(true, false, null);
        List<Boolean> linked = new LinkedList<>(list);
        this.assertCollectWithIndex(list);
        this.assertCollectWithIndex(linked);
    }

    /**
     * @since 9.1.
     */
    private void assertCollectWithIndex(List<Boolean> list)
    {
        MutableList<ObjectIntPair<Boolean>> newCollection = ListIterate.collectWithIndex(list, PrimitiveTuples::pair);
        Verify.assertListsEqual(newCollection, Lists.mutable.with(PrimitiveTuples.pair(Boolean.TRUE, 0), PrimitiveTuples.pair(Boolean.FALSE, 1), PrimitiveTuples.pair(null, 2)));

        List<ObjectIntPair<Boolean>> newCollection2 = ListIterate.collectWithIndex(list, PrimitiveTuples::pair, new ArrayList<>());
        Verify.assertListsEqual(newCollection2, Lists.mutable.with(PrimitiveTuples.pair(Boolean.TRUE, 0), PrimitiveTuples.pair(Boolean.FALSE, 1), PrimitiveTuples.pair(null, 2)));
    }

    @Test
    public void flatten()
    {
        MutableList<MutableList<Boolean>> list = Lists.fixedSize.of(
                Lists.fixedSize.of(true, false),
                Lists.fixedSize.of(true, null));
        List<MutableList<Boolean>> linked = new LinkedList<>(list);
        this.assertFlatten(list);
        this.assertFlatten(list.asSynchronized());
        this.assertFlatten(list.asUnmodifiable());
        this.assertFlatten(linked);
    }

    private void assertFlatten(List<MutableList<Boolean>> list)
    {
        MutableList<Boolean> newList = ListIterate.flatCollect(list, RichIterable::toList);
        Verify.assertListsEqual(
                FastList.newListWith(true, false, true, null),
                newList);

        MutableSet<Boolean> newSet = ListIterate.flatCollect(list, RichIterable::toSet, UnifiedSet.newSet());
        Verify.assertSetsEqual(
                UnifiedSet.newSetWith(true, false, null),
                newSet);
    }

    @Test
    public void getFirstAndLast()
    {
        assertNull(ListIterate.getFirst(null));
        assertNull(ListIterate.getLast(null));

        MutableList<Boolean> list = Lists.fixedSize.of(true, null, false);
        assertEquals(Boolean.TRUE, ListIterate.getFirst(list));
        assertEquals(Boolean.FALSE, ListIterate.getLast(list));

        List<Boolean> linked = new LinkedList<>(list);
        assertEquals(Boolean.TRUE, ListIterate.getFirst(linked));
        assertEquals(Boolean.FALSE, ListIterate.getLast(linked));

        List<Boolean> arrayList = new ArrayList<>(list);
        assertEquals(Boolean.TRUE, ListIterate.getFirst(arrayList));
        assertEquals(Boolean.FALSE, ListIterate.getLast(arrayList));
    }

    @Test
    public void getFirstAndLastOnEmpty()
    {
        List<?> list = new ArrayList<>();
        assertNull(ListIterate.getFirst(list));
        assertNull(ListIterate.getLast(list));

        List<?> linked = new LinkedList<>();
        assertNull(ListIterate.getFirst(linked));
        assertNull(ListIterate.getLast(linked));

        List<?> synchronizedList = Collections.synchronizedList(linked);
        assertNull(ListIterate.getFirst(synchronizedList));
        assertNull(ListIterate.getLast(synchronizedList));
    }

    @Test
    public void occurrencesOfAttributeNamedOnList()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertOccurrencesOfAttributeNamedOnList(list);
        this.assertOccurrencesOfAttributeNamedOnList(new LinkedList<>(list));
    }

    private void assertOccurrencesOfAttributeNamedOnList(List<Integer> list)
    {
        int result = ListIterate.count(list, Predicates.attributeEqual(Number::intValue, 3));
        assertEquals(1, result);
        int result2 = ListIterate.count(list, Predicates.attributeEqual(Number::intValue, 6));
        assertEquals(0, result2);
    }

    private MutableList<Integer> getIntegerList()
    {
        return Interval.toReverseList(1, 5);
    }

    @Test
    public void forEachWithIndex()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertForEachWithIndex(list);
        this.assertForEachWithIndex(new LinkedList<>(list));
    }

    private void assertForEachWithIndex(List<Integer> list)
    {
        Iterate.sortThis(list);
        ListIterate.forEachWithIndex(list, (object, index) -> assertEquals(index, object - 1));
    }

    @Test
    public void forEachUsingFromTo()
    {
        MutableList<Integer> integers = Interval.oneTo(5).toList();

        this.assertForEachUsingFromTo(integers);
        MutableList<Integer> reverseResults = Lists.mutable.of();
        this.assertReverseForEachUsingFromTo(integers, reverseResults, reverseResults::add);
        this.assertForEachUsingFromTo(new LinkedList<>(integers));
    }

    private void assertForEachUsingFromTo(List<Integer> integers)
    {
        MutableList<Integer> results = Lists.mutable.of();
        ListIterate.forEach(integers, 0, 4, results::add);
        assertEquals(integers, results);
        MutableList<Integer> reverseResults = Lists.mutable.of();

        assertThrows(IndexOutOfBoundsException.class, () -> ListIterate.forEach(integers, 4, -1, reverseResults::add));
        assertThrows(IndexOutOfBoundsException.class, () -> ListIterate.forEach(integers, -1, 4, reverseResults::add));
        assertThrows(IndexOutOfBoundsException.class, () -> ListIterate.forEach(integers, 0, 5, reverseResults::add));
    }

    private void assertReverseForEachUsingFromTo(List<Integer> integers, MutableList<Integer> reverseResults, Procedure<Integer> procedure)
    {
        ListIterate.forEach(integers, 4, 0, procedure);
        assertEquals(ListIterate.reverseThis(integers), reverseResults);
    }

    @Test
    public void forEachWithIndexUsingFromTo()
    {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        this.assertForEachWithIndexUsingFromTo(integers);
        this.assertForEachWithIndexUsingFromTo(new LinkedList<>(integers));
        MutableList<Integer> reverseResults = Lists.mutable.of();
        ObjectIntProcedure<Integer> objectIntProcedure = ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(reverseResults));
        this.assertReverseForEachIndexUsingFromTo(integers, reverseResults, objectIntProcedure);
    }

    private void assertForEachWithIndexUsingFromTo(List<Integer> integers)
    {
        MutableList<Integer> results = Lists.mutable.of();
        ListIterate.forEachWithIndex(integers, 0, 4, ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(results)));
        assertEquals(integers, results);
        MutableList<Integer> reverseResults = Lists.mutable.of();
        ObjectIntProcedure<Integer> objectIntProcedure = ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(reverseResults));
        assertThrows(IndexOutOfBoundsException.class, () -> ListIterate.forEachWithIndex(integers, 4, -1, objectIntProcedure));
        assertThrows(IndexOutOfBoundsException.class, () -> ListIterate.forEachWithIndex(integers, -1, 4, objectIntProcedure));
    }

    private void assertReverseForEachIndexUsingFromTo(List<Integer> integers, MutableList<Integer> reverseResults, ObjectIntProcedure<Integer> objectIntProcedure)
    {
        ListIterate.forEachWithIndex(integers, 4, 0, objectIntProcedure);
        assertEquals(ListIterate.reverseThis(integers), reverseResults);
    }

    @Test
    public void reverseForEach()
    {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        MutableList<Integer> reverseResults = Lists.mutable.of();
        ListIterate.reverseForEach(integers, CollectionAddProcedure.on(reverseResults));
        assertEquals(ListIterate.reverseThis(integers), reverseResults);
    }

    @Test
    public void reverseForEach_emptyList()
    {
        MutableList<Integer> integers = Lists.mutable.of();
        MutableList<Integer> results = Lists.mutable.of();
        ListIterate.reverseForEach(integers, CollectionAddProcedure.on(results));
        assertEquals(integers, results);
    }

    @Test
    public void reverseForEachWithIndex()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertReverseForEachWithIndex(list);
        this.assertReverseForEachWithIndex(new LinkedList<>(list));
        ListIterate.reverseForEachWithIndex(Lists.mutable.empty(), (ignored1, index) -> fail());

        MutableList<Twin<Integer>> result = Lists.mutable.empty();
        ListIterate.reverseForEachWithIndex(list, (each, parameter) -> result.add(Tuples.twin(each, parameter)));
        assertEquals(Lists.mutable.of(
                Tuples.twin(1, 4),
                Tuples.twin(2, 3),
                Tuples.twin(3, 2),
                Tuples.twin(4, 1),
                Tuples.twin(5, 0)), result);
    }

    @Test
    public void detectOptional()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertDetectOptional(list);
        this.assertDetectOptional(new LinkedList<>(list));
    }

    @Test
    public void sumOfInt()
    {
        List<Integer> list = this.getIntegerList();
        this.assertSumOfInt(list);
        this.assertSumOfInt(new LinkedList<>(list));
    }

    private void assertSumOfInt(List<Integer> list)
    {
        assertEquals(150, ListIterate.sumOfInt(list, anObject -> anObject * 10));
        assertEquals(150, ListIterate.sumOfInt(new LinkedList<>(list), anObject -> anObject * 10));
    }

    @Test
    public void groupByEach()
    {
        List<Integer> list = Interval.toReverseList(1, 3);
        this.assertGroupByEach(list);
        this.assertGroupByEachWithTarget(list);
        this.assertGroupByEach(new LinkedList<>(list));
        this.assertGroupByEachWithTarget(new LinkedList<>(list));
    }

    @Test
    public void min()
    {
        assertEquals((Integer) 3,
                ListIterate.min(Lists.mutable.of(2, 1, 3), Comparators.reverseNaturalOrder()));

        assertEquals((Integer) 1,
                ListIterate.min(Lists.mutable.of(2, 1, 3)));

        assertEquals((Integer) 3,
                ListIterate.min(new LinkedList<>(Lists.mutable.of(2, 1, 3)), Comparators.reverseNaturalOrder()));

        assertEquals((Integer) 1,
                ListIterate.min(new LinkedList<>(Lists.mutable.of(2, 1, 3))));
    }

    @Test
    public void minBy()
    {
        Twin<Integer> twinOne = Tuples.twin(3, 1);
        Twin<Integer> twinTwo = Tuples.twin(2, 5);
        MutableList<Twin<Integer>> list = Lists.mutable.of(twinOne, twinTwo);
        assertEquals(twinTwo, ListIterate.minBy(list, Functions.firstOfPair()));
        assertEquals(twinOne, ListIterate.minBy(list, Functions.secondOfPair()));

        List<Twin<Integer>> linkedList = new LinkedList<>(Lists.mutable.of(twinOne, twinTwo));
        assertEquals(twinTwo, ListIterate.minBy(linkedList, Functions.firstOfPair()));
        assertEquals(twinOne, ListIterate.minBy(linkedList, Functions.secondOfPair()));
    }

    @Test
    public void max()
    {
        assertEquals((Integer) 1,
                ListIterate.max(Lists.mutable.of(2, 1, 3), Comparators.reverseNaturalOrder()));

        assertEquals((Integer) 3,
                ListIterate.max(Lists.mutable.of(2, 1, 3)));

        assertEquals((Integer) 1,
                ListIterate.max(new LinkedList<>(Lists.mutable.of(2, 1, 3)), Comparators.reverseNaturalOrder()));

        assertEquals((Integer) 3,
                ListIterate.max(new LinkedList<>(Lists.mutable.of(2, 1, 3))));
    }

    @Test
    public void maxBy()
    {
        Twin<Integer> twinOne = Tuples.twin(3, 1);
        Twin<Integer> twinTwo = Tuples.twin(2, 5);
        MutableList<Twin<Integer>> list = Lists.mutable.of(twinOne, twinTwo);
        assertEquals(twinOne, ListIterate.maxBy(list, Functions.firstOfPair()));
        assertEquals(twinTwo, ListIterate.maxBy(list, Functions.secondOfPair()));

        List<Twin<Integer>> linkedList = new LinkedList<>(Lists.mutable.of(twinOne, twinTwo));
        assertEquals(twinOne, ListIterate.maxBy(linkedList, Functions.firstOfPair()));
        assertEquals(twinTwo, ListIterate.maxBy(linkedList, Functions.secondOfPair()));
    }

    private void assertGroupByEach(List<Integer> list)
    {
        FastListMultimap<Integer, Integer> multimap =
                ListIterate.groupByEach(list, each -> Lists.mutable.of(each, each));

        assertEquals(FastListMultimap.newMultimap(
                Tuples.pair(1, 1),
                Tuples.pair(1, 1),
                Tuples.pair(2, 2),
                Tuples.pair(2, 2),
                Tuples.pair(3, 3),
                Tuples.pair(3, 3)), multimap);
    }

    private void assertGroupByEachWithTarget(List<Integer> list)
    {
        FastListMultimap<Integer, Integer> multimap =
                ListIterate.groupByEach(list, each -> Lists.mutable.of(each, each), FastListMultimap.newMultimap());

        assertEquals(FastListMultimap.newMultimap(
                Tuples.pair(1, 1),
                Tuples.pair(1, 1),
                Tuples.pair(2, 2),
                Tuples.pair(2, 2),
                Tuples.pair(3, 3),
                Tuples.pair(3, 3)), multimap);
    }

    @Test
    public void allSatisfy()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertAllSatisfy(list);
        this.assertAllSatisfy(new LinkedList<>(list));
    }

    private void assertAllSatisfy(List<Integer> list)
    {
        assertFalse(ListIterate.allSatisfy(list, IntegerPredicates.isOdd()));
        assertTrue(ListIterate.allSatisfy(list, IntegerPredicates.isPositive()));
    }

    @Test
    public void detectWithOptional()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.validateDetectWithOptional(list);
        this.validateDetectWithOptional(new LinkedList<>(list));
    }

    private void validateDetectWithOptional(List<Integer> list)
    {
        Optional<Integer> integerOptional =
                ListIterate.detectWithOptional(list, Predicates2.attributeEqual(Functions.getPassThru()), 3);
        assertTrue(integerOptional.isPresent());
        assertEquals((Integer) 3, integerOptional.get());
        assertFalse(ListIterate.detectWithOptional(
                list,
                Predicates2.attributeEqual(Functions.getPassThru()), 7).isPresent());
    }

    private void assertDetectOptional(List<Integer> list)
    {
        Optional<Integer> integerOptional = ListIterate.detectOptional(list, Predicates.equal(2));
        assertTrue(integerOptional.isPresent());
        assertEquals((Integer) 2, integerOptional.get());
        assertFalse(ListIterate.detectOptional(list, Predicates.alwaysFalse()).isPresent());
    }

    private void assertReverseForEachWithIndex(List<Integer> list)
    {
        Counter counter = new Counter();
        ListIterate.reverseForEachWithIndex(list, (object, index) ->
        {
            assertEquals(counter.getCount() + 1, object.longValue());
            assertEquals(4 - counter.getCount(), index);
            counter.increment();
        });
    }

    @Test
    public void forEachInRange()
    {
        List<Integer> list = this.getIntegerList();
        this.assertForEach(list);
        this.assertForEach(new LinkedList<>(list));
    }

    private void assertForEach(List<Integer> list)
    {
        FastList<Integer> target = FastList.newList();
        ListIterate.forEach(list, 1, 3, new FastListSelectProcedure<>(Predicates.alwaysTrue(), target));
        assertEquals(Lists.mutable.of(4, 3, 2), target);

        target.clear();
        ListIterate.forEach(list, 3, 1, new FastListSelectProcedure<>(Predicates.alwaysTrue(), target));
        assertEquals(Lists.mutable.of(2, 3, 4), target);
    }

    @Test
    public void forEachInBoth()
    {
        MutableList<String> list1 = Lists.fixedSize.of("1", "2");
        MutableList<String> list2 = Lists.fixedSize.of("a", "b");
        this.assertForEachInBoth(list1, list2);
        this.assertForEachInBoth(list1, new LinkedList<>(list2));
        this.assertForEachInBoth(new LinkedList<>(list1), list2);
        this.assertForEachInBoth(new LinkedList<>(list1), new LinkedList<>(list2));

        ListIterate.forEachInBoth(null, null, (argument1, argument2) -> fail());
    }

    @Test
    public void forEachInBothThrowsOnDifferentLengthLists()
    {
        assertThrows(RuntimeException.class, () -> ListIterate.forEachInBoth(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2), (argument1, argument2) -> fail()));
    }

    private void assertForEachInBoth(List<String> list1, List<String> list2)
    {
        List<Pair<String, String>> list = new ArrayList<>();
        ListIterate.forEachInBoth(list1, list2, (argument1, argument2) -> list.add(Tuples.twin(argument1, argument2)));
        assertEquals(FastList.newListWith(Tuples.twin("1", "a"), Tuples.twin("2", "b")), list);
    }

    @Test
    public void detectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertDetectWith(list);
        this.assertDetectWith(new LinkedList<>(list));
    }

    private void assertDetectWith(List<Integer> list)
    {
        assertEquals(Integer.valueOf(1), ListIterate.detectWith(list, Object::equals, 1));
        MutableList<Integer> list2 = Lists.fixedSize.of(1, 2, 2);
        assertSame(list2.get(1), ListIterate.detectWith(list2, Object::equals, 2));
    }

    @Test
    public void detectIfNone()
    {
        MutableList<Integer> list = this.getIntegerList();
        assertNull(ListIterate.detectIfNone(list, Integer.valueOf(6)::equals, null));
        assertEquals(Integer.valueOf(5), ListIterate.detectIfNone(list, Integer.valueOf(5)::equals, 0));
        assertNull(ListIterate.detectIfNone(new LinkedList<>(list), Integer.valueOf(6)::equals, null));
    }

    @Test
    public void detectWithIfNone()
    {
        MutableList<Integer> list = this.getIntegerList();
        assertNull(ListIterate.detectWithIfNone(list, Object::equals, 6, null));
        assertEquals(Integer.valueOf(5), ListIterate.detectWithIfNone(list, Object::equals, 5, 0));
        assertNull(ListIterate.detectWithIfNone(new LinkedList<>(list), Object::equals, 6, null));
    }

    @Test
    public void select()
    {
        MutableList<Integer> list = this.getIntegerList();
        MutableList<Integer> expected = Lists.mutable.of(4, 2);
        assertEquals(expected, ListIterate.select(list, IntegerPredicates.isEven(), Lists.mutable.empty()));
        assertEquals(expected,
                ListIterate.select(new LinkedList<>(list), IntegerPredicates.isEven(), Lists.mutable.empty()));
    }

    @Test
    public void selectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Verify.assertSize(5, ListIterate.selectWith(list, Predicates2.instanceOf(), Integer.class));
        Verify.assertSize(
                5,
                ListIterate.selectWith(new LinkedList<>(list), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void selectInstanceOf()
    {
        String abc = "abc";
        String def = "def";
        MutableList<? extends Serializable> list = Lists.mutable.of(1, abc, 2L, def);
        MutableList<String> expected = Lists.mutable.of(abc, def);
        assertEquals(expected, ListIterate.selectInstancesOf(list, String.class));
        assertEquals(expected, ListIterate.selectInstancesOf(new LinkedList<>(list), String.class));
    }

    @Test
    public void distinct()
    {
        List<Integer> list = Lists.mutable.with(5, 2, 3, 5, 4, 2);
        List<Integer> integerList = this.getIntegerList();

        this.assertDistinct(list, integerList);
        this.assertDistinct(new LinkedList<>(list), new LinkedList<>(integerList));
    }

    private void assertDistinct(List<Integer> list, List<Integer> integerList)
    {
        List<Integer> expectedList = Lists.mutable.with(5, 2, 3, 4);
        List<Integer> actualList = Lists.mutable.empty();
        Verify.assertListsEqual(expectedList, ListIterate.distinct(list, actualList));
        Verify.assertListsEqual(expectedList, ListIterate.distinct(list));
        Verify.assertListsEqual(expectedList, actualList);
        actualList.clear();
        Verify.assertListsEqual(integerList, ListIterate.distinct(integerList, actualList));
        Verify.assertListsEqual(integerList, ListIterate.distinct(integerList));
        Verify.assertListsEqual(integerList, actualList);
    }

    @Test
    public void distinctBy()
    {
        MutableList<Integer> list = Lists.mutable.with(5, 2, 3, 5, 4, 2);
        MutableList<Integer> expectedList = Lists.mutable.with(5, 2, 3, 4);
        Verify.assertListsEqual(expectedList, ListIterate.distinctBy(list, Object::toString));
    }

    @Test
    public void reject()
    {
        MutableList<Integer> list = this.getIntegerList();
        assertEquals(Lists.mutable.of(4, 2),
                ListIterate.reject(list, IntegerPredicates.isOdd(), Lists.mutable.empty()));
        assertEquals(Lists.mutable.of(4, 2),
                ListIterate.reject(new LinkedList<>(list), IntegerPredicates.isOdd(), Lists.mutable.empty()));
    }

    @Test
    public void rejectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Verify.assertEmpty(ListIterate.rejectWith(list, Predicates2.instanceOf(), Integer.class));
        Verify.assertEmpty(ListIterate.rejectWith(new LinkedList<>(list), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void selectAndRejectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertSelectAndRejectWith(list);
        this.assertSelectAndRejectWith(new LinkedList<>(list));
    }

    private void assertSelectAndRejectWith(List<Integer> list)
    {
        Twin<MutableList<Integer>> result =
                ListIterate.selectAndRejectWith(list, Predicates2.in(), Lists.fixedSize.of(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(4, result.getTwo());
    }

    @Test
    public void partitionWith()
    {
        List<Integer> list = new LinkedList<>(Interval.oneTo(101));
        PartitionMutableList<Integer> partition = ListIterate.partitionWith(list, Predicates2.in(), Interval.oneToBy(101, 2));
        assertEquals(Interval.oneToBy(101, 2), partition.getSelected());
        assertEquals(Interval.fromToBy(2, 100, 2), partition.getRejected());
    }

    @Test
    public void anySatisfyWith()
    {
        MutableList<Integer> undertest = this.getIntegerList();
        this.assertAnySatisyWith(undertest);
        this.assertAnySatisyWith(new LinkedList<>(undertest));
    }

    @Test
    public void anySatisfy()
    {
        List<Integer> list = this.getIntegerList();
        this.assertAnySatisfy(list);
        this.assertAnySatisfy(new LinkedList<>(list));
    }

    private void assertAnySatisfy(List<Integer> list)
    {
        assertTrue(ListIterate.anySatisfy(list, IntegerPredicates.isPositive()));
        assertFalse(ListIterate.anySatisfy(list, IntegerPredicates.isNegative()));
    }

    private void assertAnySatisyWith(List<Integer> undertest)
    {
        assertTrue(ListIterate.anySatisfyWith(undertest, Predicates2.instanceOf(), Integer.class));
        assertFalse(ListIterate.anySatisfyWith(undertest, Predicates2.instanceOf(), Double.class));
    }

    @Test
    public void allSatisfyWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertAllSatisfyWith(list);
        this.assertAllSatisfyWith(new LinkedList<>(list));
    }

    private void assertAllSatisfyWith(List<Integer> list)
    {
        assertTrue(ListIterate.allSatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        assertFalse(ListIterate.allSatisfyWith(list, greaterThanPredicate, 2));
    }

    @Test
    public void noneSatisfy()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertNoneSatisfy(list);
        this.assertNoneSatisfy(new LinkedList<>(list));
    }

    private void assertNoneSatisfy(List<Integer> list)
    {
        assertTrue(ListIterate.noneSatisfy(list, IntegerPredicates.isZero()));
        assertFalse(ListIterate.noneSatisfy(list, IntegerPredicates.isEven()));
    }

    @Test
    public void noneSatisfyWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        this.assertNoneSatisfyWith(list);
        this.assertNoneSatisfyWith(new LinkedList<>(list));
    }

    private void assertNoneSatisfyWith(List<Integer> list)
    {
        assertTrue(ListIterate.noneSatisfyWith(list, Predicates2.instanceOf(), String.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        assertTrue(ListIterate.noneSatisfyWith(list, greaterThanPredicate, 6));
    }

    @Test
    public void countWith()
    {
        assertEquals(5, ListIterate.countWith(this.getIntegerList(), Predicates2.instanceOf(), Integer.class));
        assertEquals(5, ListIterate.countWith(
                new LinkedList<>(this.getIntegerList()),
                Predicates2.instanceOf(),
                Integer.class));
    }

    @Test
    public void count()
    {
        MutableList<Integer> list = this.getIntegerList();
        assertEquals(3, ListIterate.count(list, IntegerPredicates.isOdd()));
        assertEquals(3, ListIterate.count(new LinkedList<>(list), IntegerPredicates.isOdd()));
    }

    @Test
    public void collectIf()
    {
        MutableList<Integer> integers = Lists.fixedSize.of(1, 2, 3);
        this.assertCollectIf(integers);
        this.assertCollectIf(new LinkedList<>(integers));
    }

    private void assertCollectIf(List<Integer> integers)
    {
        Verify.assertContainsAll(ListIterate.collectIf(integers, Integer.class::isInstance, String::valueOf), "1", "2", "3");
        Verify.assertContainsAll(ListIterate.collectIf(integers, Integer.class::isInstance, String::valueOf, new ArrayList<>()), "1", "2", "3");
    }

    @Test
    public void reverseThis()
    {
        assertEquals(FastList.newListWith(2, 3, 1), ListIterate.reverseThis(Lists.fixedSize.of(1, 3, 2)));
        assertEquals(FastList.newListWith(2, 3, 1), ListIterate.reverseThis(new LinkedList<>(Lists.fixedSize.of(1, 3, 2))));
    }

    @Test
    public void take()
    {
        MutableList<Integer> integers = this.getIntegerList();
        this.assertTake(integers);
        this.assertTake(new LinkedList<>(integers));

        Verify.assertSize(0, ListIterate.take(Lists.fixedSize.of(), 2));
        Verify.assertSize(0, ListIterate.take(new LinkedList<>(), 2));
        Verify.assertSize(0, ListIterate.take(new LinkedList<>(), Integer.MAX_VALUE));

        assertThrows(IllegalArgumentException.class, () -> ListIterate.take(this.getIntegerList(), -1));
        assertThrows(IllegalArgumentException.class, () -> ListIterate.take(this.getIntegerList(), -1, FastList.newList()));
    }

    private void assertTake(List<Integer> integers)
    {
        Verify.assertEmpty(ListIterate.take(integers, 0));
        Verify.assertListsEqual(FastList.newListWith(5), ListIterate.take(integers, 1));
        Verify.assertListsEqual(FastList.newListWith(5, 4), ListIterate.take(integers, 2));
        Verify.assertListsEqual(FastList.newListWith(5, 4, 3, 2, 1), ListIterate.take(integers, 5));
        Verify.assertListsEqual(FastList.newListWith(5, 4, 3, 2, 1), ListIterate.take(integers, 10));
        Verify.assertListsEqual(FastList.newListWith(5, 4, 3, 2, 1), ListIterate.take(integers, 10, FastList.newList()));
        Verify.assertListsEqual(FastList.newListWith(5, 4, 3, 2), ListIterate.take(integers, integers.size() - 1));
        Verify.assertListsEqual(FastList.newListWith(5, 4, 3, 2, 1), ListIterate.take(integers, integers.size()));
        assertNotSame(integers, ListIterate.take(integers, integers.size()));

        Verify.assertEmpty(ListIterate.take(Lists.fixedSize.of(), 2));
    }

    @Test
    public void take_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> ListIterate.take(this.getIntegerList(), -1));
        assertThrows(IllegalArgumentException.class, () -> ListIterate.take(this.getIntegerList(), -1, FastList.newList()));
    }

    @Test
    public void drop()
    {
        MutableList<Integer> integers = this.getIntegerList();
        this.assertDrop(integers);
        this.assertDrop(new LinkedList<>(integers));

        Verify.assertSize(0, ListIterate.drop(Lists.fixedSize.<Integer>of(), 2));
        Verify.assertSize(0, ListIterate.drop(new LinkedList<>(), 2));
        Verify.assertSize(0, ListIterate.drop(new LinkedList<>(), Integer.MAX_VALUE));

        assertThrows(IllegalArgumentException.class, () -> ListIterate.drop(FastList.newList(), -1));
        assertThrows(IllegalArgumentException.class, () -> ListIterate.drop(FastList.newList(), -1, FastList.newList()));
    }

    private void assertDrop(List<Integer> integers)
    {
        Verify.assertListsEqual(FastList.newListWith(5, 4, 3, 2, 1), ListIterate.drop(integers, 0));
        assertNotSame(integers, ListIterate.drop(integers, 0));
        Verify.assertListsEqual(FastList.newListWith(3, 2, 1), ListIterate.drop(integers, 2));
        Verify.assertListsEqual(FastList.newListWith(1), ListIterate.drop(integers, integers.size() - 1));
        Verify.assertEmpty(ListIterate.drop(integers, 5));
        Verify.assertEmpty(ListIterate.drop(integers, 6));
        Verify.assertEmpty(ListIterate.drop(integers, 6, FastList.newList()));
        Verify.assertEmpty(ListIterate.drop(integers, integers.size()));

        Verify.assertSize(0, ListIterate.drop(Lists.fixedSize.of(), 2));
    }

    @Test
    public void drop_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> ListIterate.drop(this.getIntegerList(), -1));
        assertThrows(IllegalArgumentException.class, () -> ListIterate.drop(this.getIntegerList(), -1, FastList.newList()));
    }

    @Test
    public void chunk()
    {
        MutableList<String> list = FastList.newListWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<RichIterable<String>> groups = ListIterate.chunk(list, 2);
        RichIterable<Integer> sizes = groups.collect(RichIterable::size);
        assertEquals(FastList.newListWith(2, 2, 2, 1), sizes);
    }

    @Test
    public void chunkWithIllegalSize()
    {
        assertThrows(IllegalArgumentException.class, () -> ListIterate.chunk(FastList.newList(), 0));
    }

    @Test
    public void removeIfWithProcedure()
    {
        MutableList<Integer> list1 = FastList.newListWith(1, 2, 3, 4, 5);
        MutableList<Integer> resultList1 = Lists.mutable.of();
        List<Integer> list2 = new LinkedList<>(list1);
        MutableList<Integer> resultList2 = Lists.mutable.of();

        assertTrue(ListIterate.removeIf(list1,
                IntegerPredicates.isEven(), CollectionAddProcedure.on(resultList1)));
        FastList<Integer> expected = FastList.newListWith(1, 3, 5);
        assertEquals(expected, list1);
        FastList<Integer> expectedToCollect = FastList.newListWith(2, 4);
        assertEquals(expectedToCollect, resultList1);

        assertFalse(ListIterate.removeIf(list1,
                IntegerPredicates.isNegative(), CollectionAddProcedure.on(resultList1)));
        assertEquals(expected, list1);
        assertEquals(expectedToCollect, resultList1);

        assertTrue(ListIterate.removeIf(list2, IntegerPredicates.isEven(),
                CollectionAddProcedure.on(resultList2)));
        assertEquals(expected, list2);
        assertEquals(expectedToCollect, resultList2);

        assertFalse(ListIterate.removeIf(list2,
                IntegerPredicates.isNegative(), CollectionAddProcedure.on(resultList2)));
        assertEquals(expected, list2);
        assertEquals(expectedToCollect, resultList2);
    }

    @Test
    public void removeIfWithProcedureAndParameter()
    {
        List<Integer> list = FastList.newListWith(1, 2, 3, 4, 5);
        this.assertRemoveIfWithProcedureAndParameter(Lists.mutable.withAll(list));
        this.assertRemoveIfWithProcedureAndParameter(new LinkedList<>(list));
    }

    private void assertRemoveIfWithProcedureAndParameter(List<Integer> list)
    {
        MutableList<Integer> target = Lists.mutable.empty();
        assertTrue(
                ListIterate.removeIfWith(list, Predicates2.equal(), 3, CollectionAddProcedure.on(target)));
        MutableList<Integer> expected = Lists.mutable.of(1, 2, 4, 5);
        assertEquals(expected, list);
        assertEquals(Lists.mutable.of(3), target);

        target.clear();
        assertFalse(
                ListIterate.removeIfWith(list, Predicates2.equal(), 3, CollectionAddProcedure.on(target)));
        assertEquals(expected, list);
        Verify.assertEmpty(target);
    }

    @Test
    public void removeIf()
    {
        MutableList<Integer> list1 = FastList.newListWith(1, 2, 3, 4, 5);
        List<Integer> list2 = new LinkedList<>(list1);

        assertTrue(ListIterate.removeIf(list1, IntegerPredicates.isEven()));
        MutableList<Integer> expected = Lists.mutable.of(1, 3, 5);
        assertEquals(expected, list1);

        assertTrue(ListIterate.removeIf(list2, IntegerPredicates.isEven()));
        assertEquals(expected, list2);

        assertFalse(ListIterate.removeIf(list1, IntegerPredicates.isZero()));
        assertEquals(expected, list1);

        assertFalse(ListIterate.removeIf(list2, IntegerPredicates.isZero()));
        assertEquals(expected, list2);
    }

    @Test
    public void removeIfWith()
    {
        MutableList<Integer> objects = FastList.newListWith(1, 2, 3, 4);
        ListIterate.removeIfWith(objects, Predicates2.lessThan(), 3);
        assertEquals(FastList.newListWith(3, 4), objects);
    }

    @Test
    public void zip()
    {
        List<Integer> integers = Interval.oneTo(3);
        List<Twin<Integer>> expected = Lists.mutable.with(
                Tuples.twin(1, 1),
                Tuples.twin(2, 2),
                Tuples.twin(3, 3));
        assertEquals(expected, ListIterate.zip(integers, integers));
        assertEquals(expected, ListIterate.zip(integers, integers::iterator));

        List<Integer> linkedList = new LinkedList<>(Interval.oneTo(3));
        assertEquals(expected, ListIterate.zip(linkedList, linkedList));
        assertEquals(expected, ListIterate.zip(linkedList, linkedList::iterator));
    }

    @Test
    public void zipWithIndex()
    {
        List<Integer> integers = Interval.oneTo(3);
        List<Twin<Integer>> expected = Lists.mutable.with(
                Tuples.twin(1, 0),
                Tuples.twin(2, 1),
                Tuples.twin(3, 2));

        assertEquals(expected, ListIterate.zipWithIndex(integers, Lists.mutable.empty()));
        assertEquals(expected, ListIterate.zipWithIndex(new LinkedList<>(integers), Lists.mutable.empty()));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(ListIterate.class);
    }

    @Test
    public void takeWhile()
    {
        List<Integer> integers = this.getIntegerList();
        MutableList<Integer> expected = Lists.mutable.of(5, 4);
        assertEquals(expected, ListIterate.takeWhile(integers, Predicates.notEqual(3)));
        assertEquals(expected, ListIterate.takeWhile(new LinkedList<>(integers), Predicates.notEqual(3)));
    }

    @Test
    public void dropWhile()
    {
        List<Integer> integers = this.getIntegerList();
        MutableList<Integer> expected = Lists.mutable.of(3, 2, 1);
        assertEquals(expected, ListIterate.dropWhile(integers, Predicates.notEqual(3)));
        assertEquals(expected, ListIterate.dropWhile(new LinkedList<>(integers), Predicates.notEqual(3)));
    }

    @Test
    public void partitionWhile()
    {
        List<Integer> integers = this.getIntegerList();
        this.assertPartitionWhile(integers);
        this.assertPartitionWhile(new LinkedList<>(integers));
    }

    private void assertPartitionWhile(List<Integer> integers)
    {
        PartitionMutableList<Integer> actual = ListIterate.partitionWhile(integers, Predicates.notEqual(3));
        assertEquals(Lists.mutable.of(5, 4), actual.getSelected());
        assertEquals(Lists.mutable.of(3, 2, 1), actual.getRejected());
    }
}
