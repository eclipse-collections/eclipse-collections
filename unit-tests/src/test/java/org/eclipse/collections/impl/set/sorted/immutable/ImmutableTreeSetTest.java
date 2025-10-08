/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.immutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImmutableTreeSetTest
        extends AbstractImmutableSortedSetTestCase
{
    @Override
    protected ImmutableSortedSet<Integer> classUnderTest()
    {
        return ImmutableTreeSet.newSetWith(1, 2, 3, 4);
    }

    @Override
    protected ImmutableSortedSet<Integer> classUnderTest(Comparator<? super Integer> comparator)
    {
        return ImmutableTreeSet.newSetWith(comparator, 1, 2, 3, 4);
    }

    @Test
    public void constructWithNull()
    {
        assertThrows(ClassCastException.class, () -> new TreeSet<>(Arrays.asList(new Object())));
        assertThrows(NullPointerException.class, () -> new TreeSet<>(Arrays.asList(null, null)));
        assertThrows(NullPointerException.class, () -> new TreeSet<>(Arrays.asList((Object) null)));

        assertThrows(ClassCastException.class, () -> SortedSets.immutable.of(new Object()));
        assertThrows(NullPointerException.class, () -> SortedSets.immutable.of((Object) null, null));
        assertThrows(NullPointerException.class, () -> SortedSets.immutable.of((Object) null));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();

        assertNotEquals(
                new TreeSet<>(Arrays.asList("1", "2", "3")),
                new TreeSet<>(Arrays.asList(1, 2, 3)));

        assertNotEquals(
                new TreeSet<>(Arrays.asList("1", "2", "3")),
                Sets.immutable.of("1", "2", null));

        assertNotEquals(
                SortedSets.immutable.of("1", "2", "3"),
                SortedSets.immutable.of(1, 2, 3));
    }

    @Test
    public void serialization()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Verify.assertPostSerializedEqualsAndHashCode(set);
    }

    @Override
    @Test
    public void subSet()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertEquals(FastList.newListWith(2, 3), set.subSet(2, 4).toList());
        assertEquals(FastList.newListWith(1, 2, 3), set.subSet(1, 4).toList());
    }

    @Override
    @Test
    public void headSet()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertEquals(FastList.newListWith(1, 2, 3), set.headSet(4).toList());
        assertEquals(FastList.newListWith(1, 2), set.headSet(3).toList());
    }

    @Override
    @Test
    public void tailSet()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        assertEquals(FastList.newListWith(1, 2, 3, 4), set.tailSet(1).toList());
        assertEquals(FastList.newListWith(2, 3, 4), set.tailSet(2).toList());
    }

    @Override
    @Test
    public void powerSet()
    {
        ImmutableSortedSet<SortedSetIterable<Integer>> intPowerSet = SortedSets.immutable.of(1, 2, 3).powerSet();
        ImmutableSortedSet<SortedSetIterable<Integer>> revPowerSet = SortedSets.immutable.of(Comparators.reverseNaturalOrder(), 1, 2, 3).powerSet();

        FastList<TreeSortedSet<Integer>> expectedSortedSet = FastList.newListWith(TreeSortedSet.newSet(), TreeSortedSet.newSetWith(1), TreeSortedSet.newSetWith(2),
                TreeSortedSet.newSetWith(3), TreeSortedSet.newSetWith(1, 2), TreeSortedSet.newSetWith(1, 3), TreeSortedSet.newSetWith(2, 3), TreeSortedSet.newSetWith(1, 2, 3));
        FastList<TreeSortedSet<Integer>> expectedRevSortedSet = FastList.newListWith(TreeSortedSet.newSet(), TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 3),
                TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 2), TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1),
                TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 2, 3), TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 3),
                TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 2), TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 2, 3));

        Verify.assertListsEqual(expectedSortedSet, intPowerSet.toList());
        Verify.assertListsEqual(expectedRevSortedSet, revPowerSet.toList());
    }

    @Test
    public void compareTo()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3);
        assertEquals(0, set.compareTo(set));
        assertEquals(-1, set.compareTo(SortedSets.immutable.of(1, 2, 3, 4)));
        assertEquals(1, set.compareTo(SortedSets.immutable.of(1, 2)));

        assertEquals(-1, set.compareTo(SortedSets.immutable.of(1, 2, 4)));
        assertEquals(1, set.compareTo(SortedSets.immutable.of(1, 2, 2)));
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(BooleanArrayList.newListWith(true, true, true, true), integers.collectBoolean(PrimitiveFunctions.integerIsPositive()));
    }

    @Override
    @Test
    public void collectByte()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(ByteArrayList.newListWith((byte) 4, (byte) 3, (byte) 2, (byte) 1), integers.collectByte(PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Override
    @Test
    public void collectChar()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(CharArrayList.newListWith('D', 'C', 'B', 'A'), integers.collectChar(integer -> (char) (integer.intValue() + 64)));
    }

    @Override
    @Test
    public void collectDouble()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(DoubleArrayList.newListWith(4.0d, 3.0d, 2.0d, 1.0d), integers.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Override
    @Test
    public void collectFloat()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(FloatArrayList.newListWith(4.0f, 3.0f, 2.0f, 1.0f), integers.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Override
    @Test
    public void collectInt()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(IntArrayList.newListWith(4, 3, 2, 1), integers.collectInt(PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Override
    @Test
    public void collectLong()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(LongArrayList.newListWith(4, 3, 2, 1), integers.collectLong(PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Override
    @Test
    public void collectShort()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        assertEquals(ShortArrayList.newListWith((short) 4, (short) 3, (short) 2, (short) 1), integers.collectShort(PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Test
    public void testNavigationMethods()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 3, 5, 7, 9);

        // lower
        assertNull(set.lower(1));
        assertEquals(Integer.valueOf(1), set.lower(2));
        assertEquals(Integer.valueOf(1), set.lower(3));
        assertEquals(Integer.valueOf(7), set.lower(9));

        // floor
        assertEquals(Integer.valueOf(1), set.floor(1));
        assertEquals(Integer.valueOf(1), set.floor(2));
        assertEquals(Integer.valueOf(3), set.floor(3));
        assertEquals(Integer.valueOf(9), set.floor(9));
        assertEquals(Integer.valueOf(9), set.floor(10));

        // ceiling
        assertEquals(Integer.valueOf(1), set.ceiling(0));
        assertEquals(Integer.valueOf(1), set.ceiling(1));
        assertEquals(Integer.valueOf(3), set.ceiling(2));
        assertEquals(Integer.valueOf(9), set.ceiling(9));
        assertNull(set.ceiling(10));

        // higher
        assertEquals(Integer.valueOf(1), set.higher(0));
        assertEquals(Integer.valueOf(3), set.higher(1));
        assertEquals(Integer.valueOf(3), set.higher(2));
        assertNull(set.higher(9));
    }

    @Test
    public void testDescendingIterator()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3, 4, 5);
        Iterator<Integer> desc = set.descendingIterator();

        MutableList<Integer> result = FastList.newList();
        desc.forEachRemaining(result::add);
        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), result);
    }

    @Test
    public void testDescendingSet()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3, 4, 5);
        ImmutableSortedSet<Integer> descending = set.descendingSet();

        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), descending.toList());
        assertEquals(5, descending.size());
    }

    @Test
    public void testDoubleDescendingSet()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3, 4, 5);
        ImmutableSortedSet<Integer> descending = set.descendingSet();
        ImmutableSortedSet<Integer> doubleDescending = descending.descendingSet();

        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), doubleDescending.toList());
        assertEquals(set.toList(), doubleDescending.toList());
    }

    @Test
    public void testSubSetWithBooleans()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Inclusive both
        ImmutableSortedSet<Integer> sub1 = set.subSet(3, true, 7, true);
        assertEquals(FastList.newListWith(3, 4, 5, 6, 7), sub1.toList());

        // Exclusive from
        ImmutableSortedSet<Integer> sub2 = set.subSet(3, false, 7, true);
        assertEquals(FastList.newListWith(4, 5, 6, 7), sub2.toList());

        // Exclusive to
        ImmutableSortedSet<Integer> sub3 = set.subSet(3, true, 7, false);
        assertEquals(FastList.newListWith(3, 4, 5, 6), sub3.toList());

        // Exclusive both
        ImmutableSortedSet<Integer> sub4 = set.subSet(3, false, 7, false);
        assertEquals(FastList.newListWith(4, 5, 6), sub4.toList());
    }

    @Test
    public void testHeadSetWithBoolean()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3, 4, 5);

        // Exclusive (default)
        ImmutableSortedSet<Integer> head1 = set.headSet(3, false);
        assertEquals(FastList.newListWith(1, 2), head1.toList());

        // Inclusive
        ImmutableSortedSet<Integer> head2 = set.headSet(3, true);
        assertEquals(FastList.newListWith(1, 2, 3), head2.toList());
    }

    @Test
    public void testTailSetWithBoolean()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3, 4, 5);

        // Inclusive (default)
        ImmutableSortedSet<Integer> tail1 = set.tailSet(3, true);
        assertEquals(FastList.newListWith(3, 4, 5), tail1.toList());

        // Exclusive
        ImmutableSortedSet<Integer> tail2 = set.tailSet(3, false);
        assertEquals(FastList.newListWith(4, 5), tail2.toList());
    }

    @Test
    public void testEmptySetNavigableOperations()
    {
        ImmutableSortedSet<Integer> empty = SortedSets.immutable.empty();

        assertNull(empty.lower(5));
        assertNull(empty.floor(5));
        assertNull(empty.ceiling(5));
        assertNull(empty.higher(5));

        assertSame(empty, empty.descendingSet());
        assertSame(empty, empty.subSet(1, true, 5, true));
        assertSame(empty, empty.headSet(5, true));
        assertSame(empty, empty.tailSet(1, true));
    }

    @Test
    public void testToReversed()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3, 4, 5);
        ImmutableSortedSet<Integer> reversed = set.toReversed();
        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), reversed.toList());

        // Test with custom comparator
        ImmutableSortedSet<Integer> reverseSet = SortedSets.immutable.of(Comparators.reverseNaturalOrder(), 1, 2, 3, 4, 5);
        ImmutableSortedSet<Integer> doubleReversed = reverseSet.toReversed();
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), doubleReversed.toList());
    }

    @Test
    public void testDoubleToReversed()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3, 4, 5);
        ImmutableSortedSet<Integer> reversed = set.toReversed();
        ImmutableSortedSet<Integer> doubleReversed = reversed.toReversed();

        assertEquals(FastList.newListWith(5, 4, 3, 2, 1), reversed.toList());
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), doubleReversed.toList());

        // Verify the order is correct
        assertEquals(Integer.valueOf(1), doubleReversed.getFirst());
        assertEquals(Integer.valueOf(5), doubleReversed.getLast());
    }

    @Test
    public void testToReversedOperations()
    {
        ImmutableSortedSet<Integer> set = SortedSets.immutable.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ImmutableSortedSet<Integer> reversed = set.toReversed();

        // Verify reversed order
        assertEquals(FastList.newListWith(10, 9, 8, 7, 6, 5, 4, 3, 2, 1), reversed.toList());

        // Test basic operations on reversed set
        assertEquals(10, reversed.size());
        assertTrue(reversed.contains(5));

        // Verify iteration order is correct
        assertEquals(Integer.valueOf(10), reversed.iterator().next());
    }
}
