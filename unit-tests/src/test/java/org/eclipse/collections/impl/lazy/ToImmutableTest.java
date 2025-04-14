/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy;

import java.util.List;
import java.util.Set;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ToImmutableTest {

    @Test
    public void perfIssueInLazyIterableToImmutableList()
    {
        MutableBag<Integer> visited = Bags.mutable.empty();
        LazyIterable<Integer> integers = Interval.oneTo(10);
        LazyIterable<Integer> select =
                integers.select(i -> i > 3)
                        .tap(visited::add)
                        .select(i -> i < 7)
                        .tap(visited::add);
        ImmutableList<Integer> immutableList = select.toImmutableList();

        Assertions.assertEquals(List.of(4, 5, 6), immutableList);

        // Expected: [4, 4, 5, 5, 6, 6, 7, 8, 9, 10]
        // Actual: [4, 4, 4, 4, 5, 5, 6, 6, 7, 8, 9, 10, 4, 4, 5, 5, 6, 6, 7, 8, 9, 10]
        Assertions.assertEquals(Bags.mutable.of(4, 4, 5, 5, 6, 6, 7, 8, 9, 10), visited);
    }

    @Test
    public void perfIssueInLazyIterableToImmutableBag()
    {
        MutableBag<Integer> visited = Bags.mutable.empty();
        LazyIterable<Integer> integers = Interval.oneTo(10).toBag().asLazy();
        LazyIterable<Integer> select =
                integers.select(i -> i > 3)
                        .tap(visited::add)
                        .select(i -> i < 7)
                        .tap(visited::add);
        Bag<Integer> immutableBag = select.toImmutableBag();

        Assertions.assertEquals(Bags.mutable.of(4, 5, 6), immutableBag);

        // Expected: [4, 4, 5, 5, 6, 6, 7, 8, 9, 10]
        // Actual: [4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10]
        Assertions.assertEquals(Bags.mutable.of(4, 4, 5, 5, 6, 6, 7, 8, 9, 10), visited);
    }

    @Test
    public void perfIssueInLazyIterableToImmutableSet()
    {
        MutableBag<Integer> visited = Bags.mutable.empty();
        LazyIterable<Integer> integers = Interval.oneTo(10).toBag().asLazy();
        LazyIterable<Integer> select =
                integers.select(i -> i > 3)
                        .tap(visited::add)
                        .select(i -> i < 7)
                        .tap(visited::add);
        ImmutableSet<Integer> immutableBag = select.toImmutableSet();

        Assertions.assertEquals(Set.of(4, 5, 6), immutableBag);

        // Expected: [4, 4, 5, 5, 6, 6, 7, 8, 9, 10]
        // Actual: [4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10]
        Assertions.assertEquals(Bags.mutable.of(4, 4, 5, 5, 6, 6, 7, 8, 9, 10), visited);
    }
}
