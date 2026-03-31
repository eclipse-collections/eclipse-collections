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

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.ImmutableBooleanBag;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.function.NegativeIntervalFunction;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.factory.Iterables.iBag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImmutableHashBagTest extends ImmutableBagTestCase
{
    @Override
    protected ImmutableBag<String> newBag()
    {
        return ImmutableHashBag.newBagWith("1", "2", "2", "3", "3", "3", "4", "4", "4", "4");
    }

    @Override
    protected int numKeys()
    {
        return 4;
    }

    @Override
    @Test
    public void testSize()
    {
        Verify.assertIterableSize(10, this.newBag());
    }

    @Override
    @Test
    public void toStringOfItemToCount()
    {
        assertEquals("{}", ImmutableHashBag.newBag().toStringOfItemToCount());
        assertEquals("{1=3}", ImmutableHashBag.newBagWith("1", "1", "1").toStringOfItemToCount());
        String actual = ImmutableHashBag.newBagWith("1", "2", "2").toStringOfItemToCount();
        assertTrue("{1=1, 2=2}".equals(actual) || "{2=2, 1=1}".equals(actual));
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        super.selectInstancesOf();

        ImmutableBag<Number> numbers = ImmutableHashBag.newBagWith(1, 2.0, 2.0, 3, 3, 3, 4.0, 4.0, 4.0, 4.0);
        assertEquals(iBag(1, 3, 3, 3), numbers.selectInstancesOf(Integer.class));
        assertEquals(iBag(2.0, 2.0, 4.0, 4.0, 4.0, 4.0), numbers.selectInstancesOf(Double.class));
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableBooleanBag result = this.newBag().collectBoolean("4"::equals);
        assertEquals(2, result.sizeDistinct());
        assertEquals(4, result.occurrencesOf(true));
        assertEquals(6, result.occurrencesOf(false));
    }

    @Test
    public void testNewBag()
    {
        ImmutableHashBag<Object> immutableHashBag = ImmutableHashBag.newBagWith(HashBag.newBag().with(1, 2, 3, 4));
        Verify.assertSize(4, immutableHashBag);
        assertEquals(FastList.newListWith(1, 2, 3, 4), immutableHashBag.toSortedList());
    }

    @Override
    @Test
    public void groupByEach()
    {
        ImmutableBag<Integer> immutableBag = ImmutableHashBag.newBagWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);

        MutableMultimap<Integer, Integer> expected = HashBagMultimap.newMultimap();
        int keys = this.numKeys();
        immutableBag.forEachWithOccurrences((each, parameter) ->
        {
            HashBag<Integer> bag = HashBag.newBag();
            Interval.fromTo(each, keys).forEach((int eachInt) -> bag.addOccurrences(eachInt, eachInt));
            expected.putAll(-each, bag);
        });
        Multimap<Integer, Integer> actual =
                immutableBag.groupByEach(new NegativeIntervalFunction());
        assertEquals(expected, actual);

        Multimap<Integer, Integer> actualWithTarget =
                immutableBag.groupByEach(new NegativeIntervalFunction(), HashBagMultimap.newMultimap());
        assertEquals(expected, actualWithTarget);
    }

    @Override
    @Test
    public void groupByUniqueKey()
    {
        ImmutableBag<Integer> immutableBag = ImmutableHashBag.newBagWith(1, 2, 3);
        assertEquals(Maps.immutable.of(1, 1, 2, 2, 3, 3), immutableBag.groupByUniqueKey(id -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_target()
    {
        ImmutableBag<Integer> immutableBag = ImmutableHashBag.newBagWith(1, 2, 3);
        assertEquals(Maps.immutable.of(0, 0, 1, 1, 2, 2, 3, 3), immutableBag.groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(0, 0)));
    }

    @Test
    public void topOccurrences()
    {
        MutableBag<String> mutable = HashBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        ImmutableBag<String> strings = ImmutableHashBag.newBagWith(mutable);
        ImmutableList<ObjectIntPair<String>> top5 = strings.topOccurrences(5);
        Verify.assertIterableSize(5, top5);
        assertEquals("ten", top5.getFirst().getOne());
        assertEquals(10, top5.getFirst().getTwo());
        assertEquals("six", top5.getLast().getOne());
        assertEquals(6, top5.getLast().getTwo());
        Verify.assertIterableSize(0, ImmutableHashBag.newBagWith().topOccurrences(5));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").topOccurrences(5));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").topOccurrences(1));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").topOccurrences(2));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "three").topOccurrences(2));
        Verify.assertIterableSize(2, this.newWith("one", "one", "two", "two", "three").topOccurrences(1));
        Verify.assertIterableSize(3, this.newWith(null, "one", "two").topOccurrences(5));
        Verify.assertIterableSize(3, this.newWith(null, "one", "two").topOccurrences(1));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "two", "three", "three").topOccurrences(1));
        Verify.assertIterableSize(0, this.newWith("one").newWithout("one").topOccurrences(0));
        Verify.assertIterableSize(0, this.newWith("one").topOccurrences(0));
        assertThrows(IllegalArgumentException.class, () -> this.newWith("one").topOccurrences(-1));
    }

    @Test
    public void bottomOccurrences()
    {
        MutableBag<String> mutable = HashBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        ImmutableBag<String> strings = ImmutableHashBag.newBagWith(mutable);
        ImmutableList<ObjectIntPair<String>> bottom5 = strings.bottomOccurrences(5);
        Verify.assertIterableSize(5, bottom5);
        assertEquals("one", bottom5.getFirst().getOne());
        assertEquals(1, bottom5.getFirst().getTwo());
        assertEquals("five", bottom5.getLast().getOne());
        assertEquals(5, bottom5.getLast().getTwo());
        Verify.assertIterableSize(0, ImmutableHashBag.newBagWith().bottomOccurrences(5));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").bottomOccurrences(5));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").bottomOccurrences(1));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").bottomOccurrences(2));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "two", "three").bottomOccurrences(2));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "two", "three", "three").bottomOccurrences(1));
        Verify.assertIterableSize(3, this.newWith(null, "one", "two").bottomOccurrences(5));
        Verify.assertIterableSize(3, this.newWith(null, "one", "two").bottomOccurrences(1));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "two", "three", "three").bottomOccurrences(1));
        Verify.assertIterableSize(0, this.newWith("one").newWithout("one").bottomOccurrences(0));
        Verify.assertIterableSize(0, this.newWith("one").bottomOccurrences(0));
        assertThrows(IllegalArgumentException.class, () -> this.newWith("one").bottomOccurrences(-1));
    }

    @Override
    @Test
    public void selectUnique()
    {
        super.selectUnique();

        ImmutableBag<String> bag = this.newBag();
        ImmutableSet<String> expected = Sets.immutable.of("1");
        ImmutableSet<String> actual = bag.selectUnique();
        assertEquals(expected, actual);
    }
}
