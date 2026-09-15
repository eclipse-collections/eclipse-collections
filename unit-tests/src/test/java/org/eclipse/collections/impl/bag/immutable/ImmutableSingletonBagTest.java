/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.immutable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.ImmutableBooleanBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.bag.mutable.primitive.BooleanHashBag;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iBag;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImmutableSingletonBagTest extends ImmutableBagTestCase
{
    private static final String VAL = "1";
    private static final String NOT_VAL = "2";

    @Override
    protected ImmutableBag<String> newBag()
    {
        return new ImmutableSingletonBag<>(VAL);
    }

    private ImmutableBag<String> newBagWithNull()
    {
        return new ImmutableSingletonBag<>(null);
    }

    @Override
    protected int numKeys()
    {
        return 1;
    }

    @Override
    @Test
    public void toStringOfItemToCount()
    {
        assertEquals("{1=1}", new ImmutableSingletonBag<>(VAL).toStringOfItemToCount());
    }

    @Override
    @Test
    public void selectDuplicates()
    {
        assertEquals(
                Bags.immutable.empty(),
                this.newBag().selectDuplicates());
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        ImmutableBag<Integer> immutable = new ImmutableSingletonBag<>(1);
        Bag<Integer> mutable = Bags.mutable.of(1);
        Verify.assertEqualsAndHashCode(immutable, mutable);
        assertNotEquals(immutable, FastList.newList(mutable));
        assertNotEquals(immutable, Bags.mutable.of(1, 1));
        Verify.assertEqualsAndHashCode(UnifiedMap.newWithKeysValues(1, 1), immutable.toMapOfItemToCount());
    }

    @Override
    @Test
    public void anySatisfyWithOccurrences()
    {
        ImmutableBag<String> bag = this.newBag();
        assertTrue(bag.anySatisfyWithOccurrences((object, value) -> object.equals(VAL)));
        assertTrue(bag.anySatisfyWithOccurrences((object, value) -> object.equals(VAL) && value == 1));
        assertFalse(bag.anySatisfyWithOccurrences((object, value) -> object.equals(VAL) && value == 10));
        assertFalse(bag.anySatisfyWithOccurrences((object, value) -> object.equals(NOT_VAL) && value == 10));
        assertFalse(bag.anySatisfyWithOccurrences((object, value) -> object.equals(NOT_VAL)));
    }

    @Override
    @Test
    public void allSatisfyWithOccurrences()
    {
        ImmutableBag<String> bag = this.newBag();
        assertTrue(bag.allSatisfyWithOccurrences((object, value) -> object.equals(VAL)));
        assertTrue(bag.allSatisfyWithOccurrences((object, value) -> object.equals(VAL) && value == 1));
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(VAL) && value == 10));
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(NOT_VAL) && value == 10));
        assertFalse(bag.allSatisfyWithOccurrences((object, value) -> object.equals(NOT_VAL)));
    }

    @Override
    @Test
    public void noneSatisfyWithOccurrences()
    {
        ImmutableBag<String> bag = this.newBag();
        assertFalse(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(VAL)));
        assertFalse(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(VAL) && value == 1));
        assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(NOT_VAL)));
        assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> object.equals(NOT_VAL) && value == 1));
    }

    @Override
    @Test
    public void detectWithOccurrences()
    {
        ImmutableBag<String> bag = this.newBag();
        assertEquals(VAL, bag.detectWithOccurrences((object, value) -> object.equals(VAL)));
        assertEquals(VAL, bag.detectWithOccurrences((object, value) -> object.equals(VAL) && value == 1));
        assertNull(bag.detectWithOccurrences((object, value) -> object.equals(NOT_VAL)));
        assertNull(bag.detectWithOccurrences((object, value) -> object.equals(NOT_VAL) && value == 1));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();
        assertTrue(this.newBag().allSatisfy(ignored -> true));
        assertFalse(this.newBag().allSatisfy(ignored -> false));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        super.noneSatisfy();
        assertFalse(this.newBag().noneSatisfy(ignored -> true));
        assertTrue(this.newBag().noneSatisfy(ignored -> false));
    }

    @Override
    @Test
    public void injectInto()
    {
        super.injectInto();
        assertEquals(1, new ImmutableSingletonBag<>(1).injectInto(0, AddFunction.INTEGER).intValue());
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        assertEquals(FastList.newListWith(VAL), this.newBag().toList());
    }

    @Override
    @Test
    public void toSortedList()
    {
        super.toSortedList();

        assertEquals(FastList.newListWith(VAL), this.newBag().toSortedList());
    }

    @Test
    public void toSortedListWithComparator()
    {
        assertEquals(FastList.newListWith(VAL), this.newBag().toSortedList(null));
    }

    @Override
    @Test
    public void toSet()
    {
        super.toSet();

        assertEquals(UnifiedSet.newSetWith(VAL), this.newBag().toSet());
    }

    @Override
    @Test
    public void toBag()
    {
        super.toBag();

        assertEquals(Bags.mutable.of(VAL), this.newBag().toBag());
    }

    @Override
    @Test
    public void toMap()
    {
        super.toMap();

        assertEquals(
                Maps.fixedSize.of(String.class, VAL),
                this.newBag().toMap(Object::getClass, String::valueOf));
    }

    @Test
    public void toArrayGivenArray()
    {
        assertArrayEquals(new String[]{VAL}, this.newBag().toArray(new String[1]));
        assertArrayEquals(new String[]{VAL}, this.newBag().toArray(new String[0]));
        assertArrayEquals(new String[]{VAL, null}, this.newBag().toArray(new String[2]));
    }

    @Test
    @Override
    public void min_null_throws()
    {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.newBagWithNull().min(String::compareTo);
    }

    @Test
    @Override
    public void max_null_throws()
    {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.newBagWithNull().max(String::compareTo);
    }

    @Test
    @Override
    public void max_null_throws_without_comparator()
    {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.newBagWithNull().max();
    }

    @Test
    @Override
    public void min_null_throws_without_comparator()
    {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.newBagWithNull().min();
    }

    @Override
    @Test
    public void newWith()
    {
        super.newWith();
        assertEquals(Bags.immutable.of(VAL, NOT_VAL), this.newBag().newWith(NOT_VAL));
    }

    @Override
    @Test
    public void newWithout()
    {
        super.newWithout();
        assertEquals(Bags.immutable.of(VAL), this.newBag().newWithout(NOT_VAL));
        assertEquals(Bags.immutable.of(), this.newBag().newWithout(VAL));
    }

    @Override
    @Test
    public void newWithAll()
    {
        super.newWithAll();
        assertEquals(Bags.immutable.of(VAL, NOT_VAL, "c"), this.newBag().newWithAll(FastList.newListWith(NOT_VAL, "c")));
    }

    @Override
    @Test
    public void newWithoutAll()
    {
        super.newWithoutAll();
        assertEquals(Bags.immutable.of(VAL), this.newBag().newWithoutAll(FastList.newListWith(NOT_VAL)));
        assertEquals(Bags.immutable.of(), this.newBag().newWithoutAll(FastList.newListWith(VAL, NOT_VAL)));
        assertEquals(Bags.immutable.of(), this.newBag().newWithoutAll(FastList.newListWith(VAL)));
    }

    @Override
    @Test
    public void testSize()
    {
        Verify.assertIterableSize(1, this.newBag());
    }

    @Override
    @Test
    public void isEmpty()
    {
        super.isEmpty();
        assertFalse(this.newBag().isEmpty());
    }

    @Test
    public void testNotEmpty()
    {
        assertTrue(this.newBag().notEmpty());
    }

    @Override
    @Test
    public void getFirst()
    {
        super.getFirst();
        assertEquals(VAL, this.newBag().getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        super.getLast();
        assertEquals(VAL, this.newBag().getLast());
    }

    @Override
    @Test
    public void getOnly()
    {
        super.getOnly();
        assertEquals(VAL, this.newBag().getOnly());
    }

    @Override
    @Test
    public void contains()
    {
        super.contains();
        assertTrue(this.newBag().contains(VAL));
        assertFalse(this.newBag().contains(NOT_VAL));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        super.containsAllIterable();
        assertTrue(this.newBag().containsAllIterable(FastList.newListWith()));
        assertTrue(this.newBag().containsAllIterable(FastList.newListWith(VAL)));
        assertFalse(this.newBag().containsAllIterable(FastList.newListWith(NOT_VAL)));
        assertFalse(this.newBag().containsAllIterable(FastList.newListWith(42)));
        assertFalse(this.newBag().containsAllIterable(FastList.newListWith(VAL, NOT_VAL)));
    }

    @Test
    public void testContainsAllArguments()
    {
        assertTrue(this.newBag().containsAllArguments());
        assertTrue(this.newBag().containsAllArguments(VAL));
        assertFalse(this.newBag().containsAllArguments(NOT_VAL));
        assertFalse(this.newBag().containsAllArguments(42));
        assertFalse(this.newBag().containsAllArguments(VAL, NOT_VAL));
    }

    @Override
    @Test
    public void selectToTarget()
    {
        super.selectToTarget();
        MutableList<String> target = Lists.mutable.of();
        this.newBag().select(ignored1 -> false, target);
        Verify.assertEmpty(target);
        this.newBag().select(ignored -> true, target);
        Verify.assertContains(VAL, target);
    }

    @Override
    @Test
    public void rejectToTarget()
    {
        super.rejectToTarget();
        MutableList<String> target = Lists.mutable.of();
        this.newBag().reject(ignored -> true, target);
        Verify.assertEmpty(target);
        this.newBag().reject(ignored -> false, target);
        Verify.assertContains(VAL, target);
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        assertEquals(Bags.immutable.of(VAL), this.newBag().collect(String::valueOf));
    }

    @Override
    @Test
    public void collect_target()
    {
        super.collect_target();
        MutableList<Class<?>> target = Lists.mutable.of();
        this.newBag().collect(Object::getClass, target);
        Verify.assertContains(String.class, target);
    }

    @Override
    @Test
    public void collectIf()
    {
        super.collectIf();
        assertEquals(Bags.immutable.of(String.class), this.newBag().collectIf(ignored -> true, Object::getClass));
        assertEquals(Bags.immutable.of(), this.newBag().collectIf(ignored -> false, Object::getClass));
    }

    @Override
    @Test
    public void collectIfWithTarget()
    {
        super.collectIfWithTarget();
        MutableList<Class<?>> target = Lists.mutable.of();
        this.newBag().collectIf(ignored1 -> false, Object::getClass, target);
        Verify.assertEmpty(target);
        this.newBag().collectIf(ignored -> true, Object::getClass, target);
        Verify.assertContains(String.class, target);
    }

    @Override
    @Test
    public void flatCollect()
    {
        super.flatCollect();
        ImmutableBag<Integer> result = this.newBag().flatCollect(object -> Bags.mutable.of(1, 2, 3, 4, 5));
        assertEquals(Bags.immutable.of(1, 2, 3, 4, 5), result);
    }

    @Override
    @Test
    public void flatCollectWithTarget()
    {
        super.flatCollectWithTarget();
        MutableBag<Integer> target = Bags.mutable.of();
        MutableBag<Integer> result = this.newBag().flatCollect(object -> Bags.mutable.of(1, 2, 3, 4, 5), target);
        assertEquals(Bags.mutable.of(1, 2, 3, 4, 5), result);
    }

    @Override
    @Test
    public void detect()
    {
        super.detect();
        assertEquals(VAL, this.newBag().detect(ignored -> true));
        assertNull(this.newBag().detect(ignored -> false));
    }

    @Override
    @Test
    public void detectOptional()
    {
        assertEquals("1", this.newBag().detectOptional("1"::equals).get());
        assertNotNull(this.newBag().detectOptional("2"::equals));
        assertThrows(NoSuchElementException.class, () -> this.newBag().detectOptional("2"::equals).get());
    }

    @Override
    @Test
    public void detectWith()
    {
        super.detectWith();

        assertEquals(VAL, this.newBag().detectWith(Object::equals, "1"));
    }

    @Override
    @Test
    public void detectWithOptional()
    {
        assertEquals("1", this.newBag().detectWithOptional(Object::equals, "1").get());
        assertNotNull(this.newBag().detectWithOptional(Object::equals, "2"));
        assertThrows(NoSuchElementException.class, () -> this.newBag().detectWithOptional(Object::equals, "2").get());
    }

    @Override
    @Test
    public void detectWithIfNone()
    {
        super.detectWithIfNone();

        assertEquals(VAL, this.newBag().detectWithIfNone(Object::equals, "1", new PassThruFunction0<>("Not Found")));
        assertEquals("Not Found", this.newBag().detectWithIfNone(Object::equals, "10000", new PassThruFunction0<>("Not Found")));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        super.detectIfNone();

        assertEquals(VAL, this.newBag().detectIfNone(ignored -> true, new PassThruFunction0<>(NOT_VAL)));
        assertEquals(NOT_VAL, this.newBag().detectIfNone(ignored -> false, new PassThruFunction0<>(NOT_VAL)));
    }

    @Override
    @Test
    public void count()
    {
        super.count();
        assertEquals(1, this.newBag().count(ignored -> true));
        assertEquals(0, this.newBag().count(ignored -> false));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        super.anySatisfy();
        assertTrue(this.newBag().anySatisfy(ignored -> true));
        assertFalse(this.newBag().anySatisfy(ignored -> false));
    }

    @Test
    public void testGroupBy()
    {
        ImmutableBagMultimap<Class<?>, String> result = this.newBag().groupBy(Object::getClass);
        assertEquals(VAL, result.get(String.class).getFirst());
    }

    @Test
    public void testGroupByWithTarget()
    {
        HashBagMultimap<Class<?>, String> target = HashBagMultimap.newMultimap();
        this.newBag().groupBy(Object::getClass, target);
        assertEquals(VAL, target.get(String.class).getFirst());
    }

    @Override
    @Test
    public void groupByUniqueKey()
    {
        assertEquals(UnifiedMap.newWithKeysValues("1", "1").toImmutable(), this.newBag().groupByUniqueKey(id -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_throws()
    {
        assertEquals(UnifiedMap.newWithKeysValues("1", "1").toImmutable(), this.newBag().groupByUniqueKey(id -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_target()
    {
        assertEquals(
                UnifiedMap.newWithKeysValues("0", "0", "1", "1"),
                this.newBag().groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues("0", "0")));
    }

    @Test
    public void testOccurrencesOf()
    {
        assertEquals(1, this.newBag().occurrencesOf(VAL));
        assertEquals(0, this.newBag().occurrencesOf(NOT_VAL));
    }

    @Test
    public void testForEachWithOccurrences()
    {
        Object[] results = new Object[2];
        this.newBag().forEachWithOccurrences((each, index) ->
        {
            results[0] = each;
            results[1] = index;
        });
        assertEquals(VAL, results[0]);
        assertEquals(1, results[1]);
    }

    @Override
    @Test
    public void toMapOfItemToCount()
    {
        super.toMapOfItemToCount();

        assertEquals(Maps.fixedSize.of(VAL, 1), this.newBag().toMapOfItemToCount());
    }

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();

        ImmutableBag<String> immutableBag = this.newBag();
        assertSame(immutableBag, immutableBag.toImmutable());
    }

    @Override
    @Test
    public void forEach()
    {
        super.forEach();
        Object[] results = new Object[1];
        this.newBag().forEach(Procedures.cast(each -> results[0] = each));
        assertEquals(VAL, results[0]);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();
        Object[] results = new Object[2];
        this.newBag().forEachWithIndex((each, index) ->
        {
            results[0] = each;
            results[1] = index;
        });
        assertEquals(VAL, results[0]);
        assertEquals(0, results[1]);
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    public void collectWithOccurrences()
    {
        Bag<String> bag = this.newBag();
        Bag<ObjectIntPair<String>> actual =
                bag.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
        Bag<ObjectIntPair<String>> expected =
                Bags.immutable.with(PrimitiveTuples.pair(VAL, 1));
        assertEquals(expected, actual);

        Set<ObjectIntPair<String>> actual2 =
                bag.collectWithOccurrences(PrimitiveTuples::pair, Sets.mutable.empty());
        ImmutableSet<ObjectIntPair<String>> expected2 =
                Sets.immutable.with(PrimitiveTuples.pair(VAL, 1));
        assertEquals(expected2, actual2);
    }

    @Override
    @Test
    public void forEachWith()
    {
        super.forEachWith();
        Object[] results = new Object[2];
        this.newBag().forEachWith((each, index) ->
        {
            results[0] = each;
            results[1] = index;
        }, "second");
        assertEquals(VAL, results[0]);
        assertEquals("second", results[1]);
    }

    @Override
    @Test
    public void iterator()
    {
        super.iterator();
        Iterator<String> iterator = this.newBag().iterator();
        assertTrue(iterator.hasNext());
        assertEquals(VAL, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSizeDistinct()
    {
        assertEquals(1, this.newBag().sizeDistinct());
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        ImmutableBag<Number> numbers = new ImmutableSingletonBag<>(1);
        assertEquals(iBag(1), numbers.selectInstancesOf(Integer.class));
        assertEquals(iBag(), numbers.selectInstancesOf(Double.class));
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableBooleanBag result = this.newBag().collectBoolean("4"::equals);
        assertEquals(1, result.sizeDistinct());
        assertEquals(0, result.occurrencesOf(true));
        assertEquals(1, result.occurrencesOf(false));
    }

    @Override
    @Test
    public void collectBooleanWithTarget()
    {
        BooleanHashBag target = new BooleanHashBag();
        BooleanHashBag result = this.newBag().collectBoolean("4"::equals, target);
        assertSame(target, result, "Target sent as parameter not returned");
        assertEquals(1, result.sizeDistinct());
        assertEquals(0, result.occurrencesOf(true));
        assertEquals(1, result.occurrencesOf(false));
    }

    @Override
    @Test
    public void toSortedBag()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        MutableSortedBag<String> sortedBag = immutableBag.toSortedBag();

        Verify.assertSortedBagsEqual(TreeBag.newBagWith("1"), sortedBag);

        MutableSortedBag<String> reverse = immutableBag.toSortedBag(Comparator.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparator.reverseOrder(), "1"), reverse);
    }

    @Override
    @Test
    public void toSortedBagBy()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        MutableSortedBag<String> sortedBag = immutableBag.toSortedBagBy(String::valueOf);

        Verify.assertSortedBagsEqual(TreeBag.newBagWith("1"), sortedBag);
    }

    @Override
    @Test
    public void selectUnique()
    {
        super.selectUnique();

        ImmutableBag<String> bag = this.newBag();
        ImmutableSet<String> expected = Sets.immutable.of(VAL);
        ImmutableSet<String> actual = bag.selectUnique();
        assertEquals(expected, actual);
    }
}
