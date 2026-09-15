/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy.parallel.set.sorted;

import java.util.NoSuchElementException;

import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.set.sorted.ParallelSortedSetIterable;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImmutableEmptySortedSetParallelTest extends NonParallelSortedSetIterableTestCase
{
    @Override
    protected SortedSetIterable<Integer> getExpected()
    {
        return TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder());
    }

    @Override
    protected SortedSetIterable<Integer> getExpectedWith(Integer... littleElements)
    {
        return TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder());
    }

    @Override
    protected ParallelSortedSetIterable<Integer> classUnderTest()
    {
        return this.newWith();
    }

    @Override
    protected ParallelSortedSetIterable<Integer> newWith(Integer... littleElements)
    {
        return SortedSets.immutable.with(Comparators.<Integer>reverseNaturalOrder()).asParallel(this.executorService, this.batchSize);
    }

    @Test
    public void asParallel_small_batch()
    {
        assertThrows(IllegalArgumentException.class, () -> SortedSets.immutable.with(Comparators.reverseNaturalOrder()).asParallel(this.executorService, 0));
    }

    @Test
    public void asParallel_null_executorService()
    {
        assertThrows(NullPointerException.class, () -> SortedSets.immutable.with(Comparators.reverseNaturalOrder()).asParallel(null, 2));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        assertTrue(this.classUnderTest().allSatisfy(Predicates.lessThan(0)));
        assertTrue(this.classUnderTest().allSatisfy(Predicates.greaterThanOrEqualTo(0)));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        assertTrue(this.classUnderTest().allSatisfyWith(Predicates2.lessThan(), 0));
        assertTrue(this.classUnderTest().allSatisfyWith(Predicates2.greaterThanOrEqualTo(), 0));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        assertFalse(this.classUnderTest().anySatisfy(Predicates.lessThan(0)));
        assertFalse(this.classUnderTest().anySatisfy(Predicates.greaterThanOrEqualTo(0)));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        assertFalse(this.classUnderTest().anySatisfyWith(Predicates2.lessThan(), 0));
        assertFalse(this.classUnderTest().anySatisfyWith(Predicates2.greaterThanOrEqualTo(), 0));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        assertTrue(this.classUnderTest().noneSatisfy(Predicates.lessThan(0)));
        assertTrue(this.classUnderTest().noneSatisfy(Predicates.greaterThanOrEqualTo(0)));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        assertTrue(this.classUnderTest().noneSatisfyWith(Predicates2.lessThan(), 0));
        assertTrue(this.classUnderTest().noneSatisfyWith(Predicates2.greaterThanOrEqualTo(), 0));
    }

    @Override
    @Test
    public void appendString_throws()
    {
        // Not applicable for empty collections
    }

    @Override
    @Test
    public void detect()
    {
        assertNull(this.classUnderTest().detect(Integer.valueOf(0)::equals));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        assertEquals(Integer.valueOf(10), this.classUnderTest().detectIfNone(Integer.valueOf(0)::equals, () -> 10));
    }

    @Override
    @Test
    public void detectWith()
    {
        assertNull(this.classUnderTest().detectWith(Object::equals, Integer.valueOf(0)));
    }

    @Override
    @Test
    public void detectWithIfNone()
    {
        Function0<Integer> function = new PassThruFunction0<>(Integer.valueOf(1000));
        assertEquals(Integer.valueOf(1000), this.classUnderTest().detectWithIfNone(Object::equals, Integer.valueOf(0), function));
    }

    @Override
    @Test
    public void min()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().min(Integer::compareTo));
    }

    @Override
    @Test
    public void max()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().max(Integer::compareTo));
    }

    @Override
    @Test
    public void minBy()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().minBy(String::valueOf));
    }

    @Override
    @Test
    public void maxBy()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().maxBy(String::valueOf));
    }

    @Override
    @Test
    public void min_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().min());
    }

    @Override
    @Test
    public void max_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().max());
    }

    @Override
    @Test
    public void minWithEmptyBatch()
    {
        assertThrows(NoSuchElementException.class, () -> super.minWithEmptyBatch());
    }

    @Override
    @Test
    public void maxWithEmptyBatch()
    {
        assertThrows(NoSuchElementException.class, () -> super.minWithEmptyBatch());
    }

    @Override
    @Test
    public void min_null_throws()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().min(Integer::compareTo));
    }

    @Override
    @Test
    public void max_null_throws()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().max(Integer::compareTo));
    }

    @Override
    @Test
    public void minBy_null_throws()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().minBy(Integer::valueOf));
    }

    @Override
    @Test
    public void maxBy_null_throws()
    {
        assertThrows(NoSuchElementException.class, () -> this.classUnderTest().maxBy(Integer::valueOf));
    }
}
