/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.immutable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableByteCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableCharCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableIntCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableShortCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.partition.PartitionImmutableCollection;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.Counter;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.factory.SortedBags;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.primitive.IntInterval;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractImmutableCollectionTestCase
{
    public static final Predicate<Integer> ERROR_THROWING_PREDICATE = each -> {
        throw new AssertionError();
    };

    public static final Predicates2<Integer, Class<Integer>> ERROR_THROWING_PREDICATE_2 = new Predicates2<Integer, Class<Integer>>()
    {
        @Override
        public boolean accept(Integer argument1, Class<Integer> argument2)
        {
            throw new AssertionError();
        }
    };

    protected abstract ImmutableCollection<Integer> classUnderTest();

    protected abstract <T> MutableCollection<T> newMutable();

    /**
     * @since 9.0
     */
    @Test
    public void stream()
    {
        assertEquals(
                this.classUnderTest().stream().collect(Collectors2.toBag()),
                this.classUnderTest().reduceInPlace(Collectors2.toBag()));
        Supplier<MutableBag<Integer>> supplier = Bags.mutable::empty;
        assertEquals(
                this.classUnderTest().stream().collect(supplier, MutableBag::add, MutableBag::withAll),
                this.classUnderTest().reduceInPlace(supplier, MutableBag::add));
        Optional<Integer> expectedReduce = this.classUnderTest().reduce(Integer::sum);
        if (expectedReduce.isPresent())
        {
            assertEquals(
                    this.classUnderTest().stream().reduce(Integer::sum).get(),
                    expectedReduce.get());
        }
        long count = this.classUnderTest().stream().filter(integer -> integer % 2 == 0).count();
        if (count > 0)
        {
            assertTrue(this.classUnderTest().stream().anyMatch(integer -> integer % 2 == 0));
        }
        else
        {
            assertTrue(this.classUnderTest().stream().noneMatch(integer -> integer % 2 == 0));
        }
    }

    /**
     * @since 9.0
     */
    @Test
    public void parallelStream()
    {
        assertEquals(
                this.classUnderTest().parallelStream().collect(Collectors2.toBag()),
                this.classUnderTest().reduceInPlace(Collectors2.toBag()));
        Supplier<MutableBag<Integer>> supplier = Bags.mutable::empty;
        assertEquals(
                this.classUnderTest().parallelStream().collect(supplier, MutableBag::add, MutableBag::withAll),
                this.classUnderTest().reduceInPlace(supplier, MutableBag::add));
        Optional<Integer> expectedReduce = this.classUnderTest().reduce(Integer::sum);
        if (expectedReduce.isPresent())
        {
            assertEquals(
                    this.classUnderTest().parallelStream().reduce(Integer::sum).get(),
                    expectedReduce.get());
        }
        long count = this.classUnderTest().parallelStream().filter(integer -> integer % 2 == 0).count();
        if (count > 0)
        {
            assertTrue(this.classUnderTest().parallelStream().anyMatch(integer -> integer % 2 == 0));
        }
        else
        {
            assertTrue(this.classUnderTest().parallelStream().noneMatch(integer -> integer % 2 == 0));
        }
    }

    /**
     * @since 9.0
     */
    @Test
    public void castToCollection()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().castToCollection().add(0));
    }

    /**
     * @since 9.0
     */
    @Test
    public void countBy()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Bag<Integer> results = integers.countBy(each -> each);
        Verify.assertSize(integers.size(), results);
        Bag<Integer> results2 = integers.countBy(each -> each, SortedBags.mutable.empty());
        Verify.assertSize(integers.size(), results2);
    }

    /**
     * @since 9.0
     */
    @Test
    public void countByWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Bag<Integer> results = integers.countByWith((each, parm) -> each, null);
        Verify.assertSize(integers.size(), results);
        Bag<Integer> results2 = integers.countByWith((each, parm) -> each, null, SortedBags.mutable.empty());
        Verify.assertSize(integers.size(), results2);
    }

    /**
     * @since 10.0.0
     */
    @Test
    public void countByEach()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Bag<Integer> results = integers.countByEach(each -> IntInterval.oneTo(5).collect(i -> each * i));
        Verify.assertSize(integers.size() * 5, results);
        Bag<Integer> results2 = integers.countByEach(each -> IntInterval.oneTo(5).collect(i -> each * i), SortedBags.mutable.empty());
        Verify.assertSize(integers.size() * 5, results2);
    }

    @Test
    public void selectWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(
                this.<Integer>newMutable().withAll(integers).select(IntegerPredicates.isOdd()),
                integers.selectWith(Predicates2.in(), iList(1, 3, 5, 7, 9)));
    }

    @Test
    public void selectWith_target()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(
                this.<Integer>newMutable().with(101).withAll(integers).select(IntegerPredicates.isOdd()),
                integers.selectWith(Predicates2.in(), iList(1, 3, 5, 7, 9), this.<Integer>newMutable().with(101)));
    }

    @Test
    public void rejectWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(
                this.<Integer>newMutable().withAll(integers).reject(IntegerPredicates.isOdd()),
                integers.rejectWith(Predicates2.in(), iList(1, 3, 5, 7, 9)));
    }

    @Test
    public void rejectWith_target()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(
                this.<Integer>newMutable().with(100).withAll(integers).reject(IntegerPredicates.isOdd()),
                integers.rejectWith(Predicates2.in(), iList(1, 3, 5, 7, 9), this.<Integer>newMutable().with(100)));
    }

    @Test
    public void partition()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        PartitionImmutableCollection<Integer> partition = integers.partition(IntegerPredicates.isOdd());
        assertEquals(integers.select(IntegerPredicates.isOdd()), partition.getSelected());
        assertEquals(integers.select(IntegerPredicates.isEven()), partition.getRejected());
    }

    @Test
    public void partitionWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        PartitionImmutableCollection<Integer> partition = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isOdd()));
        assertEquals(integers.select(IntegerPredicates.isOdd()), partition.getSelected());
        assertEquals(integers.select(IntegerPredicates.isEven()), partition.getRejected());
    }

    @Test
    public void collectWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableCollection<String> expected = integers.collect(Functions.chain(String::valueOf, string -> string + "!"));
        ImmutableCollection<String> actual = integers.collectWith((argument1, argument2) -> argument1 + argument2, "!");

        assertEquals(expected, actual);
    }

    @Test
    public void collect_target()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableCollection<String> strings = this.newMutable();
        integers.forEach(Procedures.cast(each -> strings.add(each.toString())));
        MutableCollection<String> target = this.newMutable();
        MutableCollection<String> actual = integers.collect(String::valueOf, target);
        assertEquals(strings, actual);
        assertSame(target, actual);
    }

    @Test
    public void collectWith_target()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableCollection<String> expected = this.<String>newMutable().with("?").withAll(integers.collect(Functions.chain(String::valueOf, string -> string + "!")));
        MutableCollection<String> targetCollection = this.<String>newMutable().with("?");
        MutableCollection<String> actual = integers.collectWith((argument1, argument2) -> argument1 + argument2, "!", targetCollection);

        assertEquals(expected, actual);
        assertSame(targetCollection, actual);
    }

    @Test
    public void injectInto()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Integer result = integers.injectInto(0, AddFunction.INTEGER);
        assertEquals(FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_INT), result.intValue());
    }

    @Test
    public void injectIntoInt()
    {
        assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER).longValue(),
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLong()
    {
        assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER).longValue(),
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble()
    {
        assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER).doubleValue(),
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_DOUBLE),
                0.0);
    }

    @Test
    public void injectIntoFloat()
    {
        assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER).floatValue(),
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_FLOAT),
                0.0);
    }

    @Test
    public void sumFloat()
    {
        assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_FLOAT),
                this.classUnderTest().sumOfFloat(Integer::floatValue),
                0.0);
    }

    @Test
    public void sumDouble()
    {
        assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_DOUBLE),
                this.classUnderTest().sumOfDouble(Integer::doubleValue),
                0.0);
    }

    @Test
    public void sumInteger()
    {
        assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_INT),
                this.classUnderTest().sumOfInt(integer -> integer));
    }

    @Test
    public void sumLong()
    {
        assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_LONG),
                this.classUnderTest().sumOfLong(Integer::longValue));
    }

    @Test
    public void sumByInt()
    {
        MutableCollection<Integer> integers = this.newMutable();
        integers.addAllIterable(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ImmutableCollection<Integer> values = integers.toImmutable();
        ImmutableObjectLongMap<Integer> result = values.sumByInt(i -> i % 2, e -> e);
        assertEquals(25, result.get(1));
        assertEquals(30, result.get(0));
    }

    @Test
    public void sumByFloat()
    {
        MutableCollection<Integer> integers = this.newMutable();
        integers.addAllIterable(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ImmutableCollection<Integer> values = integers.toImmutable();
        ImmutableObjectDoubleMap<Integer> result = values.sumByFloat(f -> f % 2, e -> e);
        assertEquals(25.0f, result.get(1), 0.0);
        assertEquals(30.0f, result.get(0), 0.0);
    }

    @Test
    public void sumByFloatConsistentRounding()
    {
        MutableList<Integer> group1 = Interval.oneTo(100_000).toList().shuffleThis();
        MutableList<Integer> group2 = Interval.fromTo(100_001, 200_000).toList().shuffleThis();
        MutableList<Integer> integers = Lists.mutable.withAll(group1);
        integers.addAll(group2);
        ImmutableCollection<Integer> values = integers.toImmutable();

        ImmutableObjectDoubleMap<Integer> result = values.sumByFloat(
                integer -> integer > 100_000 ? 2 : 1,
                integer -> {
                    Integer i = integer > 100_000 ? integer - 100_000 : integer;
                    return 1.0f / (i.floatValue() * i.floatValue() * i.floatValue() * i.floatValue());
                });

        // The test only ensures the consistency/stability of rounding. This is not meant to test the "correctness" of the float calculation result.
        // Indeed, the lower bits of this calculation result are always incorrect due to the information loss of original float values.
        assertEquals(
                1.082323233761663,
                result.get(1),
                1.0e-15);
        assertEquals(
                1.082323233761663,
                result.get(2),
                1.0e-15);
    }

    @Test
    public void sumByLong()
    {
        MutableCollection<Integer> integers = this.newMutable();
        integers.addAllIterable(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ImmutableCollection<Integer> values = integers.toImmutable();
        ImmutableObjectLongMap<Integer> result = values.sumByLong(l -> l % 2, e -> e);
        assertEquals(25, result.get(1));
        assertEquals(30, result.get(0));
    }

    @Test
    public void sumByDouble()
    {
        MutableCollection<Integer> integers = this.newMutable();
        integers.addAllIterable(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ImmutableCollection<Integer> values = integers.toImmutable();
        ImmutableObjectDoubleMap<Integer> result = values.sumByDouble(d -> d % 2, e -> e);
        assertEquals(25.0d, result.get(1), 0.0);
        assertEquals(30.0d, result.get(0), 0.0);
    }

    @Test
    public void sumByDoubleConsistentRounding()
    {
        MutableList<Integer> group1 = Interval.oneTo(100_000).toList().shuffleThis();
        MutableList<Integer> group2 = Interval.fromTo(100_001, 200_000).toList().shuffleThis();
        MutableList<Integer> integers = Lists.mutable.withAll(group1);
        integers.addAll(group2);
        ImmutableCollection<Integer> values = integers.toImmutable();
        ImmutableObjectDoubleMap<Integer> result = values.sumByDouble(
                integer -> integer > 100_000 ? 2 : 1,
                integer -> {
                    Integer i = integer > 100_000 ? integer - 100_000 : integer;
                    return 1.0d / (i.doubleValue() * i.doubleValue() * i.doubleValue() * i.doubleValue());
                });

        assertEquals(
                1.082323233711138,
                result.get(1),
                1.0e-15);
        assertEquals(
                1.082323233711138,
                result.get(2),
                1.0e-15);
    }

    @Test
    public void makeString()
    {
        assertEquals(FastList.newList(this.classUnderTest()).toString(), '[' + this.classUnderTest().makeString() + ']');
        assertEquals(FastList.newList(this.classUnderTest()).toString(), '[' + this.classUnderTest().makeString(", ") + ']');
        assertEquals(FastList.newList(this.classUnderTest()).toString(), this.classUnderTest().makeString("[", ", ", "]"));
    }

    @Test
    public void appendString()
    {
        Appendable builder1 = new StringBuilder();
        this.classUnderTest().appendString(builder1);
        assertEquals(FastList.newList(this.classUnderTest()).toString(), '[' + builder1.toString() + ']');

        Appendable builder2 = new StringBuilder();
        this.classUnderTest().appendString(builder2, ", ");
        assertEquals(FastList.newList(this.classUnderTest()).toString(), '[' + builder2.toString() + ']');

        Appendable builder3 = new StringBuilder();
        this.classUnderTest().appendString(builder3, "[", ", ", "]");
        assertEquals(FastList.newList(this.classUnderTest()).toString(), builder3.toString());
    }

    @Test
    public void testToString()
    {
        assertEquals(FastList.newList(this.classUnderTest()).toString(), this.classUnderTest().toString());
    }

    @Test
    public void select()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(integers, integers.select(Predicates.lessThan(integers.size() + 1)));
        Verify.assertIterableEmpty(integers.select(Predicates.greaterThan(integers.size())));
    }

    @Test
    public void selectInstancesOf()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableCollection<Integer> result = integers.selectInstancesOf(Integer.class);
        assertEquals(integers, result);
    }

    @Test
    public void reject()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Verify.assertIterableEmpty(integers.reject(Predicates.lessThan(integers.size() + 1)));
        assertEquals(integers, integers.reject(Predicates.greaterThan(integers.size())));
    }

    @Test
    public void collect()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(integers, integers.collect(Functions.getIntegerPassThru()));
    }

    @Test
    public void collectBoolean()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableBooleanCollection immutableCollection = integers.collectBoolean(PrimitiveFunctions.integerIsPositive());
        Verify.assertSize(1, immutableCollection);
    }

    @Test
    public void collectByte()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableByteCollection immutableCollection = integers.collectByte(PrimitiveFunctions.unboxIntegerToByte());
        Verify.assertSize(integers.size(), immutableCollection);
        assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void collectChar()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableCharCollection immutableCollection = integers.collectChar(PrimitiveFunctions.unboxIntegerToChar());
        Verify.assertSize(integers.size(), immutableCollection);
        assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void collectDouble()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableDoubleCollection immutableCollection = integers.collectDouble(PrimitiveFunctions.unboxIntegerToDouble());
        Verify.assertSize(integers.size(), immutableCollection);
        assertEquals(integers, immutableCollection.collect(doubleParameter -> Integer.valueOf((int) doubleParameter)));
    }

    @Test
    public void collectFloat()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableFloatCollection immutableCollection = integers.collectFloat(PrimitiveFunctions.unboxIntegerToFloat());
        Verify.assertSize(integers.size(), immutableCollection);
        assertEquals(integers, immutableCollection.collect(floatParameter -> Integer.valueOf((int) floatParameter)));
    }

    @Test
    public void collectInt()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableIntCollection immutableCollection = integers.collectInt(PrimitiveFunctions.unboxIntegerToInt());
        Verify.assertSize(integers.size(), immutableCollection);
        assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void collectLong()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableLongCollection immutableCollection = integers.collectLong(PrimitiveFunctions.unboxIntegerToLong());
        Verify.assertSize(integers.size(), immutableCollection);
        assertEquals(integers, immutableCollection.collect(longParameter -> Integer.valueOf((int) longParameter)));
    }

    @Test
    public void collectShort()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableShortCollection immutableCollection = integers.collectShort(PrimitiveFunctions.unboxIntegerToShort());
        Verify.assertSize(integers.size(), immutableCollection);
        assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void flatCollect()
    {
        RichIterable<String> actual = this.classUnderTest().flatCollect(integer -> Lists.fixedSize.of(String.valueOf(integer)));

        ImmutableCollection<String> expected = this.classUnderTest().collect(String::valueOf);

        assertEquals(expected, actual);
    }

    @Test
    public void flatCollectWith()
    {
        RichIterable<String> actual = this.classUnderTest().flatCollectWith((integer, factory) -> factory.of(String.valueOf(integer)), Lists.fixedSize);

        ImmutableCollection<String> expected = this.classUnderTest().collect(String::valueOf);

        assertEquals(expected, actual);
    }

    @Test
    public void chunk_zero_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.classUnderTest().chunk(0));
    }

    @Test
    public void detect()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(Integer.valueOf(1), integers.detect(Predicates.equal(1)));
        assertNull(integers.detect(Predicates.equal(integers.size() + 1)));
    }

    @Test
    public void detectWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(Integer.valueOf(1), integers.detectWith(Object::equals, Integer.valueOf(1)));
        assertNull(integers.detectWith(Object::equals, Integer.valueOf(integers.size() + 1)));

        MutableList<String> strings = FastList.newListWith("1", "2", "3");
        assertEquals("1", strings.detectWith(Object::equals, "1"));
    }

    @Test
    public void detectIfNone()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Function0<Integer> function = new PassThruFunction0<>(integers.size() + 1);
        assertEquals(Integer.valueOf(1), integers.detectIfNone(Predicates.equal(1), function));
        assertEquals(Integer.valueOf(integers.size() + 1), integers.detectIfNone(Predicates.equal(integers.size() + 1), function));
    }

    @Test
    public void detectWithIfNone()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Integer sum = Integer.valueOf(integers.size() + 1);
        Function0<Integer> function = new PassThruFunction0<>(sum);
        assertEquals(Integer.valueOf(1), integers.detectWithIfNone(Object::equals, Integer.valueOf(1), function));
        assertEquals(sum, integers.detectWithIfNone(Object::equals, sum, function));
    }

    @Test
    public void allSatisfy()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertTrue(integers.allSatisfy(Integer.class::isInstance));
        assertFalse(integers.allSatisfy(Integer.valueOf(0)::equals));
    }

    @Test
    public void allSatisfyWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertTrue(integers.allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        assertFalse(integers.allSatisfyWith(Object::equals, 0));
    }

    @Test
    public void noneSatisfy()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertTrue(integers.noneSatisfy(String.class::isInstance));
        assertFalse(integers.noneSatisfy(Integer.valueOf(1)::equals));
    }

    @Test
    public void noneSatisfyWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertTrue(integers.noneSatisfyWith(Predicates2.instanceOf(), String.class));
        assertFalse(integers.noneSatisfyWith(Object::equals, 1));
    }

    @Test
    public void anySatisfy()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertFalse(integers.anySatisfy(String.class::isInstance));
        assertTrue(integers.anySatisfy(Integer.class::isInstance));
    }

    @Test
    public void anySatisfyWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertFalse(integers.anySatisfyWith(Predicates2.instanceOf(), String.class));
        assertTrue(integers.anySatisfyWith(Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void count()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(integers.size(), integers.count(Integer.class::isInstance));
        assertEquals(0, integers.count(String.class::isInstance));
    }

    @Test
    public void countWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(integers.size(), integers.countWith(Predicates2.instanceOf(), Integer.class));
        assertEquals(0, integers.countWith(Predicates2.instanceOf(), String.class));
    }

    @Test
    public void collectIf()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(integers, integers.collectIf(Integer.class::isInstance, Functions.getIntegerPassThru()));
    }

    @Test
    public void getFirst()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(Integer.valueOf(1), integers.getFirst());
    }

    @Test
    public void getLast()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        assertEquals(Integer.valueOf(integers.size()), integers.getLast());
    }

    @Test
    public void isEmpty()
    {
        ImmutableCollection<Integer> immutableCollection = this.classUnderTest();
        assertFalse(immutableCollection.isEmpty());
        assertTrue(immutableCollection.notEmpty());
    }

    @Test
    public void iterator()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; iterator.hasNext(); i++)
        {
            Integer integer = iterator.next();
            assertEquals(i + 1, integer.intValue());
        }
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void toArray()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        assertArrayEquals(integers.toArray(), copy.toArray());
        assertArrayEquals(integers.toArray(new Integer[integers.size()]), copy.toArray(new Integer[integers.size()]));
    }

    @Test
    public void toSortedList()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        MutableList<Integer> list = integers.toSortedList(Collections.reverseOrder());
        assertEquals(copy.sortThis(Collections.reverseOrder()), list);
        MutableList<Integer> list2 = integers.toSortedList();
        assertEquals(copy.sortThis(), list2);
    }

    @Test
    public void toSortedSet()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Verify.assertListsEqual(integers.toSortedList(), set.toList());
    }

    @Test
    public void toSortedSetWithComparator()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet(Comparators.reverseNaturalOrder());
        assertEquals(integers.toSet(), set);
        assertEquals(integers.toSortedList(Comparators.reverseNaturalOrder()), set.toList());
    }

    @Test
    public void toSortedSetBy()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers), set);
    }

    @Test
    public void forLoop()
    {
        ImmutableCollection<Integer> immutableCollection = this.classUnderTest();
        for (Integer each : immutableCollection)
        {
            assertNotNull(each);
        }
    }

    protected ImmutableCollection<Integer> classUnderTestWithNull()
    {
        return this.classUnderTest().reject(Integer.valueOf(1)::equals).newWith(null);
    }

    @Test
    public void min_null_throws()
    {
        assertThrows(NullPointerException.class, () -> this.classUnderTestWithNull().min(Integer::compareTo));
    }

    @Test
    public void max_null_throws()
    {
        assertThrows(NullPointerException.class, () -> this.classUnderTestWithNull().max(Integer::compareTo));
    }

    @Test
    public void min()
    {
        assertEquals(Integer.valueOf(1), this.classUnderTest().min(Integer::compareTo));
    }

    @Test
    public void max()
    {
        assertEquals(Integer.valueOf(1), this.classUnderTest().max(Comparators.reverse(Integer::compareTo)));
    }

    @Test
    public void min_null_throws_without_comparator()
    {
        assertThrows(NullPointerException.class, () -> this.classUnderTestWithNull().min());
    }

    @Test
    public void max_null_throws_without_comparator()
    {
        assertThrows(NullPointerException.class, () -> this.classUnderTestWithNull().max());
    }

    @Test
    public void min_without_comparator()
    {
        assertEquals(Integer.valueOf(1), this.classUnderTest().min());
    }

    @Test
    public void max_without_comparator()
    {
        assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().max());
    }

    @Test
    public void minBy()
    {
        assertEquals(Integer.valueOf(1), this.classUnderTest().minBy(String::valueOf));
    }

    @Test
    public void maxBy()
    {
        assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().maxBy(Functions.getIntegerPassThru()));
    }

    @Test
    public void iteratorRemove()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().iterator().remove());
    }

    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).add(1));
    }

    @Test
    public void remove()
    {
        assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).remove(Integer.valueOf(1)));
    }

    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).clear());
    }

    @Test
    public void removeAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).removeAll(Lists.fixedSize.of()));
    }

    @Test
    public void retainAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).retainAll(Lists.fixedSize.of()));
    }

    @Test
    public void addAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).addAll(Lists.fixedSize.of()));
    }

    @Test
    public void aggregateByMutating()
    {
        Procedure2<Counter, Integer> sumAggregator = Counter::add;
        MapIterable<String, Counter> actual = this.classUnderTest().aggregateInPlaceBy(String::valueOf, Counter::new, sumAggregator);
        MapIterable<String, Counter> expected = this.classUnderTest().toBag().aggregateInPlaceBy(String::valueOf, Counter::new, sumAggregator);
        assertEquals(expected, actual);
    }

    @Test
    public void aggregateByNonMutating()
    {
        Function2<Integer, Integer, Integer> sumAggregator = (integer1, integer2) -> integer1 + integer2;
        MapIterable<String, Integer> actual = this.classUnderTest().aggregateBy(String::valueOf, () -> 0, sumAggregator);
        MapIterable<String, Integer> expected = this.classUnderTest().toBag().aggregateBy(String::valueOf, () -> 0, sumAggregator);
        assertEquals(expected, actual);
    }

    @Test
    public void tap()
    {
        MutableList<Integer> tapResult = Lists.mutable.of();
        ImmutableCollection<Integer> collection = this.classUnderTest();
        assertSame(collection, collection.tap(tapResult::add));
        assertEquals(collection.toList(), tapResult);
    }
}
