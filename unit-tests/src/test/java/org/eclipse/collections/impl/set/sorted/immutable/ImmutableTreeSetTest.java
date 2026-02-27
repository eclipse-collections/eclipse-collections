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
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
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
        SortedSet<Integer> subSet = this.classUnderTest().castToSortedSet().subSet(1, 4);
        assertEquals(3, subSet.size());
        assertTrue(subSet.contains(1));
        assertTrue(subSet.contains(2));
        assertTrue(subSet.contains(3));
    }

    @Override
    @Test
    public void headSet()
    {
        SortedSet<Integer> headSet = this.classUnderTest().castToSortedSet().headSet(4);
        assertEquals(3, headSet.size());
        assertTrue(headSet.contains(1));
        assertTrue(headSet.contains(2));
        assertTrue(headSet.contains(3));
    }

    @Override
    @Test
    public void tailSet()
    {
        SortedSet<Integer> tailSet = this.classUnderTest().castToSortedSet().tailSet(2);
        assertEquals(3, tailSet.size());
        assertTrue(tailSet.contains(2));
        assertTrue(tailSet.contains(3));
        assertTrue(tailSet.contains(4));
    }

    @Test
    public void testNavigationMethods()
    {
        ImmutableSortedSet<Integer> set = ImmutableTreeSet.newSetWith(1, 3, 5, 7, 9);
        assertEquals(Integer.valueOf(3), set.lower(5));
        assertEquals(Integer.valueOf(5), set.floor(5));
        assertEquals(Integer.valueOf(5), set.ceiling(5));
        assertEquals(Integer.valueOf(7), set.higher(5));

        assertEquals(Integer.valueOf(5), set.lower(6));
        assertEquals(Integer.valueOf(5), set.floor(6));
        assertEquals(Integer.valueOf(7), set.ceiling(6));
        assertEquals(Integer.valueOf(7), set.higher(6));

        assertNull(set.lower(1));
        assertNull(set.higher(9));
    }

    @Test
    public void testDescendingSet()
    {
        ImmutableSortedSet<Integer> set = ImmutableTreeSet.newSetWith(1, 2, 3, 4, 5);
        ImmutableSortedSet<Integer> desc = set.descendingSet();
        assertEquals(Integer.valueOf(5), desc.getFirst());
        assertEquals(Integer.valueOf(1), desc.getLast());
        assertEquals(Lists.mutable.with(5, 4, 3, 2, 1), desc.toList());
    }

    @Test
    public void testDescendingIterator()
    {
        ImmutableSortedSet<Integer> set = ImmutableTreeSet.newSetWith(1, 2, 3);
        Iterator<Integer> iterator = set.descendingIterator();
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(1), iterator.next());
    }

    @Test
    public void testSubSetWithBooleans()
    {
        ImmutableSortedSet<Integer> set = ImmutableTreeSet.newSetWith(1, 2, 3, 4, 5);
        ImmutableSortedSet<Integer> sub = set.subSet(2, true, 4, true);
        assertEquals(3, sub.size());
        assertTrue(sub.contains(2));
        assertTrue(sub.contains(3));
        assertTrue(sub.contains(4));

        ImmutableSortedSet<Integer> subExcl = set.subSet(2, false, 4, false);
        assertEquals(1, subExcl.size());
        assertTrue(subExcl.contains(3));
    }

    @Test
    public void testHeadSetWithBoolean()
    {
        ImmutableSortedSet<Integer> set = ImmutableTreeSet.newSetWith(1, 2, 3, 4, 5);
        ImmutableSortedSet<Integer> head = set.headSet(3, true);
        assertEquals(3, head.size());
        ImmutableSortedSet<Integer> headExcl = set.headSet(3, false);
        assertEquals(2, headExcl.size());
    }

    @Test
    public void testTailSetWithBoolean()
    {
        ImmutableSortedSet<Integer> set = ImmutableTreeSet.newSetWith(1, 2, 3, 4, 5);
        ImmutableSortedSet<Integer> tail = set.tailSet(3, true);
        assertEquals(3, tail.size());
        ImmutableSortedSet<Integer> tailExcl = set.tailSet(3, false);
        assertEquals(2, tailExcl.size());
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
}
