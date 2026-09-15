/*
 * Copyright (c) 2026 The Eclipse Collections Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.primitive;

import org.eclipse.collections.api.block.comparator.primitive.IntComparator;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.list.primitive.IntInterval;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntQuickSortRegressionTest
{
    @Test
    public void sortThisWithCustomComparatorLargeSortedInputDoesNotOverflow()
    {
        // Regression test for Issue #1685 (StackOverflowError in QuickSort)
        // We verify that the new Tail Recursion implementation handles deep recursion safely.

        int size = 100_000;
        MutableIntList list = IntInterval.oneTo(size).toList();

        // Sorting a pre-sorted list with a reverse comparator triggers the worst-case recursion
        list.sortThis(new IntComparator()
        {
            @Override
            public int compare(int value1, int value2)
            {
                return value2 - value1;
            }
        });

        assertEquals(size, list.get(0));
        assertEquals(1, list.get(size - 1));
        assertEquals(IntInterval.oneTo(size).toList(), list.reverseThis());
    }
}
