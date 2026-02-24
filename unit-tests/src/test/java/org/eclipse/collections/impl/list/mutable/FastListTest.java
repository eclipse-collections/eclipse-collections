/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.MaxSizeFunction;
import org.eclipse.collections.impl.block.function.MinSizeFunction;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.block.procedure.CountProcedure;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.math.Sum;
import org.eclipse.collections.impl.math.SumProcedure;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.ClassComparer;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.LazyIterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iList;
import static org.eclipse.collections.impl.factory.Iterables.mList;
import static org.eclipse.collections.impl.factory.Iterables.mSet;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link FastList}.
 */
public class FastListTest extends AbstractListTestCase
{
    @Override
    protected <T> FastList<T> newWith(T... littleElements)
    {
        return FastList.newListWith(littleElements);
    }

    @Override
    @Test
    public void newEmpty()
    {
        Verify.assertInstanceOf(FastList.class, FastList.newList().newEmpty());
    }

    @Test
    public void withNValues()
    {
        assertEquals(FastList.newListWith(1, 1, 1, 1, 1), FastList.newWithNValues(5, () -> 1));
        assertEquals(FastList.newListWith(null, null, null, null, null), FastList.newWithNValues(5, () -> null));
        assertEquals(
                FastList.newListWith(
                        Lists.mutable.with(),
                        Lists.mutable.with(),
                        Lists.mutable.with(),
                        Lists.mutable.with(),
                        Lists.mutable.with()),
                FastList.newWithNValues(5, FastList::new));
    }

    @Test
    public void constructorWithCollection()
    {
        List<Integer> expected = new ArrayList<>(Interval.oneTo(20));
        FastList<Integer> actual = new FastList<>(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void testFastListNewWithContainsAllItems()
    {
        assertEquals(
                Bags.mutable.of("Alice", "Bob", "Cooper", "Dio"),
                this.newWith("Alice", "Bob", "Cooper", "Dio").toBag());
    }

    @Test
    public void testAddWithZeroBasedConstructor()
    {
        MutableList<String> strings = FastList.newList(0);
        assertEquals(new ArrayList<String>(0), strings);
        strings.add("1");
        assertEquals(this.newWith("1"), strings);
    }

    @Test
    public void getBatchCount()
    {
        FastList<String> strings = FastList.newList(0);
        assertEquals(1, strings.getBatchCount(10));
        strings.with("a", "b", "c");
        assertEquals(3, strings.getBatchCount(1));
        assertEquals(1, strings.getBatchCount(2));
        assertEquals(1, strings.getBatchCount(200));
    }

    @Test
    public void remove()
    {
        MutableList<String> strings = Lists.mutable.of("a", "b", "c");
        assertTrue(strings.remove("a"));
        Verify.assertSize(2, strings);
        assertFalse(strings.remove("a"));
        assertFalse(strings.remove("z"));
        Verify.assertSize(2, strings);
        assertTrue(strings.remove("c"));
        Verify.assertSize(1, strings);
    }

    @Test
    public void wrapCopy()
    {
        Integer[] integers = {1, 2, 3, 4};
        FastList<Integer> actual = FastList.wrapCopy(integers);
        FastList<Integer> expected = this.newWith(1, 2, 3, 4);
        integers[0] = Integer.valueOf(4);
        assertEquals(expected, actual);
    }

    @Test
    public void fastListProperSuperSetOfArrayList()
    {
        assertTrue(ClassComparer.isProperSupersetOf(FastList.class, ArrayList.class));
    }

    @Override
    @Test
    public void forEach()
    {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> collection = FastList.newListWith(1, 2, 3, 4);
        collection.forEach(CollectionAddProcedure.on(result));
        assertEquals(this.newWith(1, 2, 3, 4), result);
    }

    @Override
    @Test
    public void injectInto()
    {
        FastList<Integer> list = this.newWith(1, 2, 3);
        assertEquals(Integer.valueOf(1 + 1 + 2 + 3), list.injectInto(1, AddFunction.INTEGER));
    }

    @Test
    public void testInjectIntoDouble()
    {
        FastList<Double> list = this.newWith(1.0, 2.0, 3.0);
        assertEquals(Double.valueOf(1.0 + 1.0 + 2.0 + 3.0), list.injectInto(Double.valueOf(1.0), AddFunction.DOUBLE));
    }

    @Test
    public void testInjectIntoFloat()
    {
        FastList<Float> list = this.newWith(1.0f, 2.0f, 3.0f);
        assertEquals(Float.valueOf(7.0f), list.injectInto(Float.valueOf(1.0f), AddFunction.FLOAT));
    }

    @Test
    public void testInjectIntoString()
    {
        FastList<String> list = FastList.<String>newList().with("1", "2", "3");
        assertEquals("0123", list.injectInto("0", AddFunction.STRING));
    }

    @Test
    public void testInjectIntoMaxString()
    {
        FastList<String> list = FastList.<String>newList().with("1", "12", "123");
        assertEquals(Integer.valueOf(3), list.injectInto(Integer.MIN_VALUE, MaxSizeFunction.STRING));
    }

    @Test
    public void testInjectIntoMinString()
    {
        FastList<String> list = FastList.<String>newList().with("1", "12", "123");
        assertEquals(Integer.valueOf(1), list.injectInto(Integer.MAX_VALUE, MinSizeFunction.STRING));
    }

    @Override
    @Test
    public void collect()
    {
        FastList<Boolean> list = this.newWith(Boolean.TRUE, Boolean.FALSE, null);
        MutableList<String> newCollection = list.collect(String::valueOf);
        assertEquals(this.newWith("true", "false", "null"), newCollection);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();

        MutableList<Integer> list = FastList.newList(Interval.oneTo(5));
        list.forEachWithIndex((object, index) -> assertEquals(index, object - 1));
    }

    @Test
    public void testForEachInBoth()
    {
        MutableList<Twin<String>> list = FastList.newList();
        MutableList<String> list1 = this.newWith("1", "2");
        MutableList<String> list2 = this.newWith("a", "b");
        ListIterate.forEachInBoth(list1, list2, (argument1, argument2) -> list.add(Tuples.twin(argument1, argument2)));
        assertEquals(this.newWith(Tuples.twin("1", "a"), Tuples.twin("2", "b")), list);
    }

    @Override
    @Test
    public void detect()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertEquals(Integer.valueOf(1), list.detect(Integer.valueOf(1)::equals));
        FastList<Integer> list2 = FastList.newListWith(1, 2, 2);
        assertSame(list2.get(1), list2.detect(Integer.valueOf(2)::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertEquals(Integer.valueOf(1), list.detectWith(Object::equals, 1));
        FastList<Integer> list2 = FastList.newListWith(1, 2, 2);
        assertSame(list2.get(1), list2.detectWith(Object::equals, 2));
    }

    @Test
    public void testDetectWithIfNone()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertNull(list.detectWithIfNone(Object::equals, 6, new PassThruFunction0<>(null)));
    }

    @Override
    @Test
    public void select()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = list.select(Integer.class::isInstance);
        Verify.assertSize(5, results);
    }

    @Override
    @Test
    public void selectWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = list.selectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertSize(5, results);
    }

    @Override
    @Test
    public void rejectWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = list.rejectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertEmpty(results);
    }

    @Override
    @Test
    public void selectAndRejectWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Twin<MutableList<Integer>> result =
                list.selectAndRejectWith(Predicates2.in(), Lists.fixedSize.of(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(4, result.getTwo());
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertTrue(list.anySatisfyWith(Predicates2.instanceOf(), Integer.class));
        assertFalse(list.anySatisfyWith(Predicates2.instanceOf(), Double.class));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertTrue(Predicates.<Integer>anySatisfy(Integer.class::isInstance).accept(list));
        assertFalse(Predicates.<Integer>anySatisfy(Double.class::isInstance).accept(list));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertTrue(list.allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        assertFalse(list.allSatisfyWith(greaterThanPredicate, 2));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertTrue(Predicates.<Integer>allSatisfy(Integer.class::isInstance).accept(list));
        assertFalse(Predicates.allSatisfy(Predicates.greaterThan(2)).accept(list));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertTrue(Predicates.<Integer>noneSatisfy(String.class::isInstance).accept(list));
        assertFalse(Predicates.noneSatisfy(Predicates.greaterThan(0)).accept(list));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertTrue(list.noneSatisfyWith(Predicates2.instanceOf(), String.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        assertFalse(list.noneSatisfyWith(greaterThanPredicate, 0));
    }

    @Override
    @Test
    public void count()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertEquals(5, list.count(Integer.class::isInstance));
        assertEquals(0, list.count(Double.class::isInstance));
    }

    @Override
    @Test
    public void countWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        assertEquals(5, list.countWith(Predicates2.instanceOf(), Integer.class));
        assertEquals(0, list.countWith(Predicates2.instanceOf(), Double.class));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        Function0<Integer> defaultResultFunction = new PassThruFunction0<>(6);
        assertEquals(
                Integer.valueOf(3),
                FastList.newListWith(1, 2, 3, 4, 5).detectIfNone(Integer.valueOf(3)::equals, defaultResultFunction));
        assertEquals(
                Integer.valueOf(6),
                FastList.newListWith(1, 2, 3, 4, 5).detectIfNone(Integer.valueOf(6)::equals, defaultResultFunction));
    }

    @Override
    @Test
    public void forEachWith()
    {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> list = FastList.newListWith(1, 2, 3, 4);
        list.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 0);
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Test
    public void testForEachIf()
    {
        FastList<Integer> collection = FastList.newListWith(1, 2, 3, 4);
        CountProcedure<Integer> countProcedure = new CountProcedure<>(ignored -> true);
        collection.forEachIf(Predicates.lessThan(4), countProcedure);
        assertEquals(3, countProcedure.getCount());
    }

    @Override
    @Test
    public void isEmpty()
    {
        Verify.assertEmpty(FastList.<Integer>newList());
        Verify.assertNotEmpty(FastList.newListWith(1, 2));
        assertTrue(FastList.newListWith(1, 2).notEmpty());
    }

    @Override
    @Test
    public void collectIf()
    {
        Verify.assertContainsAll(FastList.newListWith(1, 2, 3).collectIf(
                Integer.class::isInstance,
                String::valueOf), "1", "2", "3");
        Verify.assertContainsAll(FastList.newListWith(1, 2, 3).collectIf(
                Integer.class::isInstance,
                String::valueOf,
                new ArrayList<>()), "1", "2", "3");
    }

    @Override
    @Test
    public void collectWith()
    {
        assertEquals(
                FastList.newListWith(2, 3, 4),
                FastList.newListWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1));
        assertEquals(
                FastList.newListWith(2, 3, 4),
                FastList.newListWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1, FastList.newList()));
    }

    @Override
    @Test
    public void injectIntoWith()
    {
        MutableList<Integer> objects = FastList.newListWith(1, 2, 3);
        Integer result = objects.injectIntoWith(1, (injectedValued, item, parameter) -> injectedValued + item + parameter, 0);
        assertEquals(Integer.valueOf(7), result);
    }

    @Test
    public void testRemoveUsingPredicate()
    {
        MutableList<Integer> objects = FastList.newListWith(1, 2, 3, null);
        assertTrue(objects.removeIf(Predicates.isNull()));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
    }

    @Override
    @Test
    public void removeAllIterable()
    {
        FastList<Integer> objects = FastList.newListWith(1, 2, 3);
        objects.removeAllIterable(iList(1, 2));
        Verify.assertSize(1, objects);
        Verify.assertContains(3, objects);
        FastList<Integer> objects2 = FastList.newListWith(1, 2, 3);
        objects2.removeAllIterable(iList(1));
        Verify.assertSize(2, objects2);
        Verify.assertContainsAll(objects2, 2, 3);
        FastList<Integer> objects3 = FastList.newListWith(1, 2, 3);
        objects3.removeAllIterable(iList(3));
        Verify.assertSize(2, objects3);
        Verify.assertContainsAll(objects3, 1, 2);
        FastList<Integer> objects4 = FastList.newListWith(1, 2, 3);
        objects4.removeAllIterable(iList());
        Verify.assertSize(3, objects4);
        Verify.assertContainsAll(objects4, 1, 2, 3);
        FastList<Integer> objects5 = FastList.newListWith(1, 2, 3);
        objects5.removeAllIterable(iList(1, 2, 3));
        Verify.assertEmpty(objects5);
        FastList<Integer> objects6 = FastList.newListWith(1, 2, 3);
        objects6.removeAllIterable(iList(2));
        Verify.assertSize(2, objects6);
        Verify.assertContainsAll(objects6, 1, 3);
    }

    @SuppressWarnings("StringOperationCanBeSimplified")
    @Test
    public void testRemoveAllWithWeakReference()
    {
        String fred = new String("Fred");    // Deliberate String copy for unit test purpose
        String wilma = new String("Wilma");  // Deliberate String copy for unit test purpose
        FastList<String> objects = FastList.<String>newList().with(fred, wilma);
        objects.removeAll(mList("Fred"));
        objects.remove(0);
        Verify.assertSize(0, objects);
        WeakReference<String> ref = new WeakReference<>(wilma);
        //noinspection ReuseOfLocalVariable
        fred = null;   // Deliberate null of a local variable for unit test purpose
        //noinspection ReuseOfLocalVariable
        wilma = null;  // Deliberate null of a local variable for unit test purpose
        System.gc();
        Thread.yield();
        System.gc();
        Thread.yield();
        assertNull(ref.get());
    }

    @Override
    @Test
    public void retainAll()
    {
        super.retainAll();

        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.retainAll(mList(1, 2));
        Verify.assertSize(2, objects);
        Verify.assertContainsAll(objects, 1, 2);
        MutableList<Integer> objects2 = this.newWith(1, 2, 3);
        objects2.retainAll(mList(1));
        Verify.assertSize(1, objects2);
        Verify.assertContainsAll(objects2, 1);
        MutableList<Integer> objects3 = this.newWith(1, 2, 3);
        objects3.retainAll(mList(3));
        Verify.assertSize(1, objects3);
        Verify.assertContainsAll(objects3, 3);
        MutableList<Integer> objects4 = this.newWith(1, 2, 3);
        objects4.retainAll(mList(2));
        Verify.assertSize(1, objects4);
        Verify.assertContainsAll(objects4, 2);
        MutableList<Integer> objects5 = this.newWith(1, 2, 3);
        objects5.retainAll(mList());
        Verify.assertEmpty(objects5);
        MutableList<Integer> objects6 = this.newWith(1, 2, 3);
        objects6.retainAll(mList(1, 2, 3));
        Verify.assertSize(3, objects6);
        Verify.assertContainsAll(objects6, 1, 2, 3);
    }

    @Override
    @Test
    public void retainAllIterable()
    {
        super.retainAllIterable();

        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.retainAllIterable(iList(1, 2));
        Verify.assertSize(2, objects);
        Verify.assertContainsAll(objects, 1, 2);
        MutableList<Integer> objects2 = this.newWith(1, 2, 3);
        objects2.retainAllIterable(iList(1));
        Verify.assertSize(1, objects2);
        Verify.assertContainsAll(objects2, 1);
        MutableList<Integer> objects3 = this.newWith(1, 2, 3);
        objects3.retainAllIterable(iList(3));
        Verify.assertSize(1, objects3);
        Verify.assertContainsAll(objects3, 3);
        MutableList<Integer> objects4 = this.newWith(1, 2, 3);
        objects4.retainAllIterable(iList(2));
        Verify.assertSize(1, objects4);
        Verify.assertContainsAll(objects4, 2);
        MutableList<Integer> objects5 = this.newWith(1, 2, 3);
        objects5.retainAllIterable(iList());
        Verify.assertEmpty(objects5);
        MutableList<Integer> objects6 = this.newWith(1, 2, 3);
        objects6.retainAllIterable(iList(1, 2, 3));
        Verify.assertSize(3, objects6);
        Verify.assertContainsAll(objects6, 1, 2, 3);
    }

    @Override
    @Test
    public void reject()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(
                Predicates.lessThan(3),
                UnifiedSet.newSet()), 3, 4);
    }

    @Override
    @Test
    public void distinct()
    {
        super.distinct();
        Verify.assertListsEqual(this.newWith(5, 2, 3, 5, 4, 2).distinct(), this.newWith(5, 2, 3, 4));
        Verify.assertListsEqual(Interval.fromTo(1, 5).toList().distinct(), this.newWith(1, 2, 3, 4, 5));
    }

    @Override
    @Test
    public void serialization()
    {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        Verify.assertStartsWith(deserializedCollection, 1, 2, 3, 4, 5);
        Verify.assertListsEqual(collection, deserializedCollection);
    }

    @Test
    public void testSerializationOfEmpty()
    {
        MutableList<Integer> collection = FastList.newList();
        Verify.assertPostSerializedEqualsAndHashCode(collection);
    }

    @Test
    public void testSerializationOfSublist()
    {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection.subList(0, 2));
        Verify.assertSize(2, deserializedCollection);
        Verify.assertStartsWith(deserializedCollection, 1, 2);
        Verify.assertListsEqual(collection.subList(0, 2), deserializedCollection);
    }

    @Test
    public void testSubList()
    {
        MutableList<String> collection = FastList.newListWith("1", "2", "3", "4", "5");
        MutableList<String> subList = collection.subList(1, 3);
        Verify.assertContainsAll(subList, "2", "3");
        subList.add("6");
        Verify.assertItemAtIndex("6", 2, subList);
        Verify.assertSize(6, collection);
        Verify.assertSize(3, subList);
        Verify.assertItemAtIndex("6", 3, collection);
        subList.remove("6");
        Verify.assertSize(5, collection);
        Verify.assertSize(2, subList);
    }

    @Test
    public void testBAOSSize()
    {
        MutableList<MutableList<Object>> mutableArrayList = FastList.<MutableList<Object>>newList()
                .with(
                        FastList.newList(),
                        FastList.newList(),
                        FastList.newList(),
                        FastList.newList())
                .with(
                        FastList.newList(),
                        FastList.newList(),
                        FastList.newList(),
                        FastList.newList());

        List<List<Object>> arrayList = new ArrayList<>();
        Interval.oneTo(8).forEach(Procedures.cast(object -> arrayList.add(new ArrayList<>())));
        ByteArrayOutputStream stream2 = SerializeTestHelper.getByteArrayOutputStream(arrayList);
        assertEquals(194L, stream2.size());

        ByteArrayOutputStream stream1 = SerializeTestHelper.getByteArrayOutputStream(mutableArrayList);
        assertEquals(182L, stream1.size());
    }

    @Override
    @Test
    public void addAll()
    {
        super.addAll();

        MutableList<Integer> integers1 = FastList.newList();
        assertTrue(integers1.addAll(mList(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4), integers1);
        assertTrue(integers1.addAll(FastList.<Integer>newList(4).with(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 1, 2, 3, 4), integers1);
        assertTrue(integers1.addAll(mSet(5)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 1, 2, 3, 4, 5), integers1);

        MutableList<Integer> integers2 = FastList.newListWith(0);
        assertTrue(integers2.addAll(mList(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(0, 1, 2, 3, 4), integers2);
        assertTrue(integers2.addAll(FastList.<Integer>newList(4).with(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(0, 1, 2, 3, 4, 1, 2, 3, 4), integers2);
        assertTrue(integers2.addAll(mSet(5)));
        Verify.assertListsEqual(FastList.newListWith(0, 1, 2, 3, 4, 1, 2, 3, 4, 5), integers2);

        assertThrows(IndexOutOfBoundsException.class, () -> FastList.newList().addAll(1, null));
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();

        FastList<Integer> integers = FastList.newList();
        assertTrue(integers.addAllIterable(iList(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4), integers);
        assertTrue(integers.addAllIterable(FastList.<Integer>newList(4).with(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 1, 2, 3, 4), integers);
        assertTrue(integers.addAllIterable(mSet(5)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 1, 2, 3, 4, 5), integers);
    }

    @Test
    public void testAddAllEmpty()
    {
        FastList<Integer> integers = FastList.newList();
        integers.addAll(Lists.mutable.of());
        Verify.assertEmpty(integers);
        integers.addAll(Sets.mutable.of());
        Verify.assertEmpty(integers);
        integers.addAll(FastList.newList());
        Verify.assertEmpty(integers);
        integers.addAll(ArrayAdapter.newArray());
        Verify.assertEmpty(integers);
    }

    @Test
    public void addAllWithMultipleTypes()
    {
        FastList<Integer> list = FastList.newList();
        list.addAll(mList(1, 2, 3, 4));
        list.addAll(mSet(5, 6));
        list.addAll(new ArrayList<>(mList(7, 8)));
        list.addAll(this.newWith(9, 10));
        assertFalse(list.addAll(Lists.mutable.of()));
        assertEquals(this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), list);
    }

    @Override
    @Test
    public void addAllAtIndex()
    {
        super.addAllAtIndex();
        FastList<Integer> integers = this.newWith(5);
        integers.addAll(0, mList(1, 2, 3, 4));
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 5);
        integers.addAll(0, FastList.<Integer>newList(4).with(-3, -2, -1, 0));
        Verify.assertStartsWith(integers, -3, -2, -1, 0, 1, 2, 3, 4, 5);
    }

    @Test
    public void testAddAllAtIndexEmpty()
    {
        FastList<Integer> integers = this.newWith(5);
        integers.addAll(0, Lists.mutable.of());
        Verify.assertSize(1, integers);
        Verify.assertStartsWith(integers, 5);
        integers.addAll(0, FastList.newList(4));
        Verify.assertSize(1, integers);
        Verify.assertStartsWith(integers, 5);
        integers.addAll(0, Sets.mutable.of());
        Verify.assertSize(1, integers);
        Verify.assertStartsWith(integers, 5);
        FastList<String> zeroSizedList = FastList.newList(0);
        zeroSizedList.addAll(0, this.newWith("1", "2"));
    }

    @Override
    @Test
    public void addAtIndex()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 5);
        integers.add(3, 4);
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 5);
        integers.add(5, 6);
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 5, 6);
        integers.add(0, 0);
        Verify.assertStartsWith(integers, 0, 1, 2, 3, 4, 5, 6);
        FastList<String> zeroSizedList = FastList.newList(0);
        zeroSizedList.add(0, "1");
        Verify.assertStartsWith(zeroSizedList, "1");
        zeroSizedList.add(1, "3");
        Verify.assertStartsWith(zeroSizedList, "1", "3");
        zeroSizedList.add(1, "2");
        Verify.assertStartsWith(zeroSizedList, "1", "2", "3");
        FastList<Integer> midList = FastList.<Integer>newList(2).with(1, 3);
        midList.add(1, 2);
        Verify.assertStartsWith(midList, 1, 2, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> midList.add(-1, -1));
    }

    @Test
    public void testSubListSort()
    {
        MutableList<Integer> list = new FastList<>(Interval.from(0).to(20));
        MutableList<Integer> sorted = list.subList(2, 18).sortThis();
        Verify.assertListsEqual(sorted, Interval.from(2).to(17));
    }

    @Test
    public void testSubListOfSubList()
    {
        MutableList<String> list = this.newWith("A", "B", "C", "D");
        MutableList<String> sublist = list.subList(0, 3);
        MutableList<String> sublist2 = sublist.subList(0, 2);
        Verify.assertSize(2, sublist2);
        Verify.assertContainsAll(sublist, "A", "B");
        sublist2.add("X");
        Verify.assertSize(3, sublist2);
        Verify.assertStartsWith(sublist2, "A", "B", "X");
        Verify.assertContainsAll(sublist, "A", "B", "C", "X");
        assertEquals("X", sublist2.remove(2));
        Verify.assertSize(2, sublist2);
        Verify.assertContainsNone(sublist, "X");
        Verify.assertContainsNone(sublist2, "X");
    }

    @Test
    public void testSubListListIterator()
    {
        MutableList<String> list = this.newWith("A", "B", "C", "D");
        MutableList<String> subList = list.subList(0, 3);
        ListIterator<String> iterator = subList.listIterator();
        MutableList<String> newList = FastList.newList();
        while (iterator.hasNext())
        {
            newList.add(iterator.next());
        }
        Verify.assertSize(3, newList);
        while (iterator.hasPrevious())
        {
            newList.remove(iterator.previous());
        }
        Verify.assertEmpty(newList);
        iterator.add("X");
        Verify.assertSize(4, subList);
    }

    @Test
    public void testSetAtIndex()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 5);
        assertEquals(Integer.valueOf(5), integers.set(3, 4));
        Verify.assertStartsWith(integers, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void indexOf()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        assertEquals(2, integers.indexOf(3));
        assertEquals(-1, integers.indexOf(0));
        assertEquals(-1, integers.indexOf(null));
        FastList<Integer> integers2 = FastList.<Integer>newList(4).with(null, 2, 3, 4);
        assertEquals(0, integers2.indexOf(null));
    }

    @Override
    @Test
    public void lastIndexOf()
    {
        FastList<Integer> integers = FastList.<Integer>newList(4).with(1, 2, 3, 4);
        assertEquals(2, integers.lastIndexOf(3));
        assertEquals(-1, integers.lastIndexOf(0));
        assertEquals(-1, integers.lastIndexOf(null));
        FastList<Integer> integers2 = FastList.<Integer>newList(4).with(null, 2, 3, 4);
        assertEquals(0, integers2.lastIndexOf(null));
    }

    @Test
    public void testOutOfBoundsCondition()
    {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        assertThrows(IndexOutOfBoundsException.class, () -> integers.get(4));
    }

    @Override
    @Test
    public void clear()
    {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        Verify.assertNotEmpty(integers);
        integers.clear();
        Verify.assertEmpty(integers);
    }

    @Override
    @Test
    public void testClone()
    {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableList<Integer> clone = integers.clone();
        Verify.assertListsEqual(integers, clone);
        Verify.assertInstanceOf(FastList.class, clone);
        assertEquals(FastList.newList(), FastList.newList().clone());
        assertEquals(FastList.newList(0), FastList.newList().clone());
    }

    @Override
    @Test
    public void toArray()
    {
        Object[] typelessArray = this.newWith(1, 2, 3, 4).toArray();
        assertArrayEquals(typelessArray, new Object[]{1, 2, 3, 4});
        Integer[] typedArray = this.newWith(1, 2, 3, 4).toArray(new Integer[0]);
        assertArrayEquals(typedArray, new Integer[]{1, 2, 3, 4});
        Integer[] typedArray2 = this.newWith(1, 2, 3, 4).toArray(new Integer[5]);
        assertArrayEquals(typedArray2, new Integer[]{1, 2, 3, 4, null});
        Integer[] typedArray3 = this.newWith(1, 2, 3, 4).toTypedArray(Integer.class);
        assertArrayEquals(typedArray3, new Integer[]{1, 2, 3, 4});
    }

    @Override
    @Test
    public void testToString()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        assertNotNull(integers.toString());
        assertEquals("[1, 2, 3, 4]", integers.toString());
    }

    @Test
    public void toStringRecursion()
    {
        MutableList<Object> list = FastList.newListWith(1, 2, 3);
        list.add(list);
        assertEquals("[1, 2, 3, (this FastList)]", list.toString());
    }

    @Test
    public void makeStringRecursion()
    {
        MutableList<Object> list = FastList.newListWith(1, 2, 3);
        list.add(list);
        assertEquals("1, 2, 3, (this FastList)", list.makeString());
    }

    @Test
    public void testTrimToSize()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.trimToSize();
        assertEquals("[1, 2, 3, 4]", integers.toString());
    }

    @Test
    public void testTrimToSizeWithLoadFactory()
    {
        FastList<Integer> integers = FastList.<Integer>newList(10).with(1, 2, 3, 4);
        assertFalse(integers.trimToSizeIfGreaterThanPercent(0.70));
        assertTrue(integers.trimToSizeIfGreaterThanPercent(0.10));
        assertEquals("[1, 2, 3, 4]", integers.toString());
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        FastList<Integer> integers = FastList.<Integer>newList(3).with(1, 2, 3);
        FastList<Integer> integers2 = this.newWith(1, 2, 3);
        FastList<Integer> integers3 = this.newWith(1, null, 3, 4, 5);
        FastList<Integer> integers4 = this.newWith(1, null, 3, 4, 5);
        FastList<Integer> integers5 = this.newWith(1, null, 3);
        List<Integer> linkedList = new LinkedList<>(integers);
        List<Integer> linkedList2 = new LinkedList<>(integers3);
        List<Integer> linkedList3 = new LinkedList<>(integers5);
        Verify.assertEqualsAndHashCode(integers, integers);
        Verify.assertPostSerializedEqualsAndHashCode(integers);
        Verify.assertEqualsAndHashCode(integers, integers2);
        Verify.assertEqualsAndHashCode(integers, iList(1, 2, 3));
        Verify.assertEqualsAndHashCode(integers, linkedList);
        assertNotEquals(integers, integers3);
        assertNotEquals(integers, integers5);
        assertNotEquals(integers, iList(2, 3, 4));
        assertNotEquals(integers, linkedList2);
        assertNotEquals(integers, linkedList3);
        assertNotEquals(integers, mSet());
        Verify.assertEqualsAndHashCode(integers3, integers4);
        Verify.assertEqualsAndHashCode(integers3, new ArrayList<>(integers3));
        Verify.assertEqualsAndHashCode(integers3, new LinkedList<>(integers3));
        Verify.assertEqualsAndHashCode(integers3, ArrayAdapter.newArrayWith(1, null, 3, 4, 5));
        assertNotEquals(integers3, ArrayAdapter.newArrayWith(1, null, 3, 4, 6));
        Verify.assertEqualsAndHashCode(integers3, ArrayListAdapter.<Integer>newList().with(1, null, 3, 4, 5));
        assertEquals(integers, integers2);
        assertNotEquals(integers, integers3);
    }

    @Override
    @Test
    public void iterator()
    {
        int sum = 0;
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        for (Integer each : integers)
        {
            sum += each.intValue();
        }
        assertEquals(10, sum);
    }

    @Override
    @Test
    public void removeObject()
    {
        super.removeObject();

        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.remove(Integer.valueOf(1));
        Verify.assertStartsWith(integers, 2, 3, 4);
        assertFalse(integers.remove(Integer.valueOf(5)));
    }

    @Test
    public void testIteratorRemove()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        Iterator<Integer> iterator = integers.iterator();
        iterator.next();
        iterator.remove();
        Verify.assertStartsWith(integers, 2, 3, 4);
    }

    @Override
    @Test
    public void toList()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableList<Integer> list = integers.toList();
        Verify.assertStartsWith(list, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void toSet()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableSet<Integer> set = integers.toSet();
        Verify.assertContainsAll(set, 1, 2, 3, 4);
    }

    @Test
    public void testSortOnListWithLessThan10Elements()
    {
        FastList<Integer> integers = this.newWith(2, 3, 4, 1, 7, 9, 6, 8, 5);
        Verify.assertStartsWith(integers.sortThis(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
        FastList<Integer> integers2 = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Verify.assertStartsWith(integers2.sortThis(Collections.reverseOrder()), 9, 8, 7, 6, 5, 4, 3, 2, 1);
        FastList<Integer> integers3 = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Verify.assertStartsWith(integers3.sortThis(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void testSort()
    {
        for (int i = 1; i < 25; i++)
        {
            FastList<Integer> actual = FastList.newList(Interval.oneTo(i));
            for (int j = 0; j < 3; j++)
            {
                actual.shuffleThis();
                assertEquals(Interval.oneTo(i), actual.sortThis());
                assertEquals(Interval.oneTo(i).reverseThis(), actual.sortThis(Collections.reverseOrder()));
            }
        }
    }

    @Test
    public void testNewListWithCollection()
    {
        Verify.assertEmpty(FastList.newList(iList()));
        Verify.assertEmpty(FastList.newList(mSet()));
        Verify.assertEmpty(FastList.newList(FastList.newList()));
        Verify.assertEmpty(FastList.newList(FastList.newList(4)));

        FastList<Integer> setToList = FastList.newList(mSet(1, 2, 3, 4, 5));
        Verify.assertNotEmpty(setToList);
        Verify.assertSize(5, setToList);
        Verify.assertContainsAll(setToList, 1, 2, 3, 4, 5);

        FastList<Integer> arrayListToList = FastList.newList(iList(1, 2, 3, 4, 5));
        Verify.assertNotEmpty(arrayListToList);
        Verify.assertSize(5, arrayListToList);
        Verify.assertStartsWith(arrayListToList, 1, 2, 3, 4, 5);

        FastList<Integer> fastListToList = FastList.newList(this.newWith(1, 2, 3, 4, 5));
        Verify.assertNotEmpty(fastListToList);
        Verify.assertSize(5, fastListToList);
        Verify.assertStartsWith(fastListToList, 1, 2, 3, 4, 5);
    }

    @Test
    public void testNewListWithIterable()
    {
        FastList<Integer> integers = FastList.newList(Interval.oneTo(3));
        assertEquals(this.newWith(1, 2, 3), integers);
    }

    @Test
    public void testContainsAll()
    {
        FastList<Integer> list = this.newWith(1, 2, 3, 4, 5, null);
        assertTrue(list.containsAll(mList(1, 3, 5, null)));
        assertFalse(list.containsAll(mList(2, null, 6)));
        assertTrue(list.containsAll(this.newWith(1, 3, 5, null)));
        assertFalse(list.containsAll(this.newWith(2, null, 6)));
    }

    @Test
    public void testToArrayFromTo()
    {
        assertArrayEquals(new Integer[]{1, 2, 3}, this.newWith(1, 2, 3, 4).toArray(0, 2));
        assertArrayEquals(new Integer[]{2, 3, 4}, this.newWith(1, 2, 3, 4).toArray(1, 3));
    }

    @Test
    public void testLazyCollectForEach()
    {
        LazyIterable<String> select =
                FastList.newList(Interval.oneTo(5)).asLazy().collect(Object::toString);
        Procedure<String> builder = Procedures.append(new StringBuilder());
        select.forEach(builder);
        assertEquals("12345", builder.toString());
    }

    @Test
    public void testLazyFlattenForEach()
    {
        MutableList<Integer> list = new FastList<>(Interval.oneTo(5));
        LazyIterable<String> select = LazyIterate.flatCollect(
                list,
                object -> this.newWith(String.valueOf(object)));
        Appendable builder = new StringBuilder();
        Procedure<String> appendProcedure = Procedures.append(builder);
        select.forEach(appendProcedure);
        assertEquals("12345", builder.toString());
    }

    /**
     * @deprecated since 3.0. Use {@code asLazy().reject(Predicate)} instead.
     */
    @Deprecated
    @Test
    public void testLazyRejectForEach()
    {
        LazyIterable<Integer> select = FastList.newList(Interval.oneTo(5)).asLazy().reject(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure<>(sum));
        assertEquals(5, sum.getValue().intValue());
    }

    /**
     * @deprecated since 3.0. Use {@code asLazy().select(Predicate)} instead.
     */
    @Deprecated
    @Test
    public void testLazySelectForEach()
    {
        LazyIterable<Integer> select = FastList.newList(Interval.oneTo(5)).asLazy().select(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure<>(sum));
        assertEquals(10, sum.getValue().intValue());
    }

    @Test
    public void testWith()
    {
        assertEquals(
                FastList.newListWith("1"),
                FastList.<String>newList().with("1"));
        assertEquals(
                FastList.newListWith("1", "2"),
                FastList.<String>newList().with("1", "2"));
        assertEquals(
                FastList.newListWith("1", "2", "3"),
                FastList.<String>newList().with("1", "2", "3"));
        assertEquals(
                FastList.newListWith("1", "2", "3", "4"),
                FastList.<String>newList().with("1", "2", "3", "4"));
        assertEquals(
                FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8"),
                FastList.<String>newList().with("1", "2", "3", "4").with("5", "6", "7", "8"));

        MutableList<String> list = FastList.newListWith("A")
                .withAll(Lists.immutable.of("1", "2"))
                .withAll(Lists.immutable.of())
                .withAll(Sets.immutable.of("3", "4"));
        assertEquals(
                Bags.mutable.of("A", "1", "2", "3", "4"),
                list.toBag());
        Verify.assertStartsWith(list, "A", "1", "2");  // "3" and "4" are from a set, so may not be in order

        assertEquals(
                FastList.newListWith(42, 10, 11, 12),
                FastList.newListWith(42).withAll(Interval.from(10).to(12).toList()));
    }

    @Test
    public void unoptimizedListToImmutable()
    {
        FastList<String> list = FastList.newListWith(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15");
        ImmutableList<String> immutableList = list.toImmutable();
        Verify.assertIterablesEqual(immutableList, list);
    }

    @Test
    public void min_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().min());
    }

    @Test
    public void max_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.newWith().max());
    }

    @Test
    public void testNegativeInitialCapacity()
    {
        assertThrows(IllegalArgumentException.class, () -> new FastList<>(-1));
    }
}
