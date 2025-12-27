/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.test;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import junit.framework.AssertionFailedError;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.multimap.bag.BagMultimap;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.multimap.list.ListMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.multimap.set.SetMultimap;
import org.eclipse.collections.api.multimap.sortedbag.ImmutableSortedBagMultimap;
import org.eclipse.collections.api.multimap.sortedbag.MutableSortedBagMultimap;
import org.eclipse.collections.api.multimap.sortedbag.SortedBagMultimap;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.multimap.sortedset.SortedSetMultimap;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.multimap.bag.sorted.mutable.TreeBagMultimap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.multimap.set.sorted.TreeSortedSetMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * JUnit test for our extensions to JUnit. These tests make sure that methods in {@link Verify} really fail when they
 * ought to.
 */
public class VerifyTest
{
    @Test
    public void assertThrowsWithCause()
    {
        Verify.assertThrowsWithCause(RuntimeException.class, NullPointerException.class, (Callable<Void>) () -> {
            throw new RuntimeException(new NullPointerException());
        });
        Verify.assertError(AssertionError.class, () -> Verify.assertThrowsWithCause(RuntimeException.class, NullPointerException.class, (Callable<Void>) () -> null));
    }

    @Test
    public void assertBefore()
    {
        Verify.assertBefore("numbers", Integer.valueOf(1), Integer.valueOf(2), FastList.newListWith(1, 2));
        Verify.assertError(AssertionError.class, () -> Verify.assertBefore("numbers", Integer.valueOf(2), Integer.valueOf(1), FastList.newListWith(1, 2)));
    }

    @Test
    public void assertEndsWithArray()
    {
        Verify.assertEndsWith(new Integer[]{1, 2, 3, 4}, 3, 4);
        Verify.assertError(AssertionError.class, () -> Verify.assertEndsWith(new Integer[]{1, 2, 3, 4}, 3, 2));
    }

    @Test
    public void assertStartsWithArray()
    {
        Verify.assertStartsWith(new Integer[]{1, 2, 3, 4}, 1, 2);
        Verify.assertError(AssertionError.class, () -> Verify.assertStartsWith(new Integer[]{1, 2, 3, 4}, 3, 2));
    }

    @Test
    public void assertStartsWithList()
    {
        Verify.assertStartsWith(FastList.newListWith(1, 2, 3, 4), 1, 2);
        Verify.assertError(AssertionError.class, () -> Verify.assertStartsWith(FastList.newListWith(1, 2, 3, 4), 3, 2));
    }

    @Test
    public void assertEndsWithList()
    {
        Verify.assertEndsWith(FastList.newListWith(1, 2, 3, 4), 3, 4);
        Verify.assertError(AssertionError.class, () -> Verify.assertEndsWith(FastList.newListWith(1, 2, 3, 4), 3, 2));
    }

    @Test
    public void assertNotContainsString()
    {
        Verify.assertNotContains("1", "0");
        Verify.assertNotContains("message", "1", "0");
        Verify.assertError(AssertionError.class, () -> Verify.assertNotContains("1", "1"));
        Verify.assertError(AssertionError.class, () -> Verify.assertNotContains("message", "1", "1"));
    }

    @Test
    public void assertListsEqual()
    {
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2, 3));
        Verify.assertListsEqual("message", FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2, 3));
        Verify.assertError(AssertionError.class, () -> Verify.assertListsEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2)));
        Verify.assertError(AssertionError.class, () -> Verify.assertListsEqual("message", FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2)));
    }

    @Test
    public void assertBagsEqual()
    {
        Verify.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 2), HashBag.newBagWith(1, 2, 2, 1));
        Verify.assertBagsEqual("message", HashBag.newBagWith(1, 1, 2, 2), HashBag.newBagWith(1, 1, 2, 2));
        Verify.assertError(AssertionError.class, () -> Verify.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 2), HashBag.newBagWith(1, 1, 2, 2, 3, 3)));
        Verify.assertError(AssertionError.class, () -> Verify.assertBagsEqual("message", HashBag.newBagWith(1, 1, 2, 2, 3, 3), HashBag.newBagWith(1, 1, 2, 2)));
        Verify.assertError(AssertionError.class, () -> Verify.assertBagsEqual("message", HashBag.newBagWith(1, 1, 2, 2, 4, 4), HashBag.newBagWith(1, 1, 2, 2, 3, 3)));
    }

    @Test
    public void assertListMultimapsEquals()
    {
        ListMultimap<Integer, String> nullMultimap = null;
        Verify.assertListMultimapsEqual(null, nullMultimap);
        ListMultimap<Integer, String> emptyMultimap = FastListMultimap.newMultimap();
        Verify.assertListMultimapsEqual(FastListMultimap.newMultimap(), emptyMultimap);
        ListMultimap<Integer, String> multimap1 = FastListMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"));
        MutableListMultimap<Integer, String> multimap2 = FastListMultimap.newMultimap(Tuples.pair(2, "Two"), Tuples.pair(1, "One"), Tuples.pair(2, "TwoTwo"));
        Verify.assertListMultimapsEqual(multimap1, multimap2);
        MutableListMultimap<Integer, String> multimap3 = FastListMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"));
        Verify.assertListMultimapsEqual(multimap1, multimap3);
        multimap2.put(2, "TwoTwo");
        multimap3.put(2, "TwoTwo");
        Verify.assertListMultimapsEqual(multimap3, multimap2);
        ImmutableListMultimap<Integer, String> multimap4 = FastListMultimap.newMultimap(Tuples.pair(2, "Two"), Tuples.pair(1, "One"), Tuples.pair(2, "TwoTwo"), Tuples.pair(2, "TwoTwo")).toImmutable();
        Verify.assertListMultimapsEqual(multimap3, multimap4);
        Verify.assertListMultimapsEqual("message", multimap3.toImmutable(), multimap4);

        Verify.assertError(AssertionError.class, () -> Verify.assertListMultimapsEqual(multimap1, null));
        Verify.assertError(AssertionError.class, () -> Verify.assertListMultimapsEqual(multimap1, multimap3));
        Verify.assertError(AssertionError.class, () -> Verify.assertListMultimapsEqual(multimap1, FastListMultimap.newMultimap()));
        Verify.assertError(AssertionFailedError.class, () -> Verify.assertListMultimapsEqual("message", multimap1, FastListMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "TwoTwo"), Tuples.pair(2, "Two"))));
        Verify.assertError(AssertionFailedError.class, () -> Verify.assertListMultimapsEqual(multimap1, FastListMultimap.newMultimap(Tuples.pair(1, "OneOne"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"))));
        Verify.assertError(AssertionError.class, () -> Verify.assertListMultimapsEqual(multimap1, FastListMultimap.newMultimap(Tuples.pair(3, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"))));
        Verify.assertError(AssertionFailedError.class, () -> Verify.assertListMultimapsEqual(multimap1, FastListMultimap.newMultimap(Tuples.pair(1, "OneOne"), Tuples.pair(2, "One"), Tuples.pair(2, "TwoTwo"))));
        Verify.assertError(AssertionError.class, () -> Verify.assertListMultimapsEqual(multimap1, FastListMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"), Tuples.pair(3, "Three"))));
    }

    @Test
    public void assertSetMultimapsEquals()
    {
        SetMultimap<Integer, String> nullMultimap = null;
        Verify.assertSetMultimapsEqual(null, nullMultimap);
        SetMultimap<Integer, String> emptyMultimap = UnifiedSetMultimap.newMultimap();
        Verify.assertSetMultimapsEqual(UnifiedSetMultimap.newMultimap(), emptyMultimap);
        SetMultimap<Integer, String> multimap1 = UnifiedSetMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"));
        MutableSetMultimap<Integer, String> multimap2 = UnifiedSetMultimap.newMultimap(Tuples.pair(2, "Two"), Tuples.pair(1, "One"), Tuples.pair(2, "TwoTwo"));
        Verify.assertSetMultimapsEqual(multimap1, multimap2);
        MutableSetMultimap<Integer, String> multimap3 = UnifiedSetMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"), Tuples.pair(2, "TwoTwo"));
        Verify.assertSetMultimapsEqual(multimap1, multimap3);
        Verify.assertSetMultimapsEqual(multimap3, multimap2);
        ImmutableSetMultimap<Integer, String> multimap4 = UnifiedSetMultimap.newMultimap(Tuples.pair(2, "Two"), Tuples.pair(1, "One"), Tuples.pair(2, "TwoTwo")).toImmutable();
        Verify.assertSetMultimapsEqual(multimap3, multimap4);
        Verify.assertSetMultimapsEqual("message", multimap3.toImmutable(), multimap4);

        multimap3.put(2, "TwoTwoTwo");
        Verify.assertError(AssertionError.class, () -> Verify.assertSetMultimapsEqual(multimap1, null));
        Verify.assertError(AssertionError.class, () -> Verify.assertSetMultimapsEqual(multimap1, multimap3));
        Verify.assertError(AssertionError.class, () -> Verify.assertSetMultimapsEqual(multimap1, UnifiedSetMultimap.newMultimap()));
        Verify.assertError(AssertionFailedError.class, () -> Verify.assertSetMultimapsEqual("message", multimap1, UnifiedSetMultimap.newMultimap(Tuples.pair(1, "OneOne"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"))));
        Verify.assertError(AssertionError.class, () -> Verify.assertSetMultimapsEqual(multimap1, UnifiedSetMultimap.newMultimap(Tuples.pair(3, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"))));
        Verify.assertError(AssertionFailedError.class, () -> Verify.assertSetMultimapsEqual(multimap1, UnifiedSetMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "One"), Tuples.pair(2, "TwoTwo"))));
        Verify.assertError(AssertionError.class, () -> Verify.assertSetMultimapsEqual(multimap1, UnifiedSetMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"), Tuples.pair(3, "Three"))));
    }

    @Test
    public void assertBagMultimapsEquals()
    {
        BagMultimap<Integer, String> nullMultimap = null;
        Verify.assertBagMultimapsEqual(null, nullMultimap);
        BagMultimap<Integer, String> emptyMultimap = HashBagMultimap.newMultimap();
        Verify.assertBagMultimapsEqual(HashBagMultimap.newMultimap(), emptyMultimap);
        BagMultimap<Integer, String> multimap1 = HashBagMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"));
        MutableBagMultimap<Integer, String> multimap2 = HashBagMultimap.newMultimap(Tuples.pair(2, "Two"), Tuples.pair(1, "One"), Tuples.pair(2, "TwoTwo"));
        Verify.assertBagMultimapsEqual(multimap1, multimap2);
        MutableBagMultimap<Integer, String> multimap3 = HashBagMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"));
        Verify.assertBagMultimapsEqual(multimap1, multimap3);
        multimap2.put(2, "TwoTwo");
        multimap3.put(2, "TwoTwo");
        Verify.assertBagMultimapsEqual(multimap3, multimap2);
        ImmutableBagMultimap<Integer, String> multimap4 = HashBagMultimap.newMultimap(Tuples.pair(2, "Two"), Tuples.pair(1, "One"), Tuples.pair(2, "TwoTwo"), Tuples.pair(2, "TwoTwo")).toImmutable();
        Verify.assertBagMultimapsEqual(multimap3, multimap4);
        Verify.assertBagMultimapsEqual("message", multimap3.toImmutable(), multimap4);

        Verify.assertError(AssertionError.class, () -> Verify.assertBagMultimapsEqual(multimap1, null));
        Verify.assertError(AssertionError.class, () -> Verify.assertBagMultimapsEqual(multimap1, multimap3));
        Verify.assertError(AssertionError.class, () -> Verify.assertBagMultimapsEqual(multimap1, HashBagMultimap.newMultimap()));

        multimap3.put(2, "TwoTwoTwo");
        Verify.assertError(AssertionError.class, () -> Verify.assertBagMultimapsEqual(multimap3, multimap4));
        Verify.assertError(AssertionError.class, () -> Verify.assertBagMultimapsEqual("message", multimap1, HashBagMultimap.newMultimap(Tuples.pair(1, "OneOne"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"))));
        Verify.assertError(AssertionError.class, () -> Verify.assertBagMultimapsEqual(multimap1, HashBagMultimap.newMultimap(Tuples.pair(3, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"))));
        Verify.assertError(AssertionError.class, () -> Verify.assertBagMultimapsEqual(multimap1, HashBagMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "One"), Tuples.pair(2, "TwoTwo"))));
        Verify.assertError(AssertionError.class, () -> Verify.assertBagMultimapsEqual(multimap1, HashBagMultimap.newMultimap(Tuples.pair(1, "One"), Tuples.pair(2, "Two"), Tuples.pair(2, "TwoTwo"), Tuples.pair(3, "Three"))));
    }

    @Test
    public void assertSortedSetMultimapsEquals()
    {
        SortedSetMultimap<Integer, Integer> nullMultimap = null;
        Verify.assertSortedSetMultimapsEqual(null, nullMultimap);
        SortedSetMultimap<Integer, Integer> emptyMultimap = TreeSortedSetMultimap.newMultimap();
        Verify.assertSortedSetMultimapsEqual(TreeSortedSetMultimap.newMultimap(), emptyMultimap);
        SortedSetMultimap<Integer, Integer> multimap1 = TreeSortedSetMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(2, 1), Tuples.pair(2, 2));
        SortedSetMultimap<Integer, Integer> multimap2 = TreeSortedSetMultimap.newMultimap(Tuples.pair(2, 1), Tuples.pair(1, 1), Tuples.pair(2, 2));
        Verify.assertSortedSetMultimapsEqual(multimap1, multimap2);
        MutableSortedSetMultimap<Integer, Integer> multimap3 = TreeSortedSetMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(2, 1), Tuples.pair(2, 2), Tuples.pair(2, 2));
        Verify.assertSortedSetMultimapsEqual(multimap1, multimap3);
        Verify.assertSortedSetMultimapsEqual(multimap3, multimap2);
        SortedSetMultimap<Integer, Integer> multimap4 = TreeSortedSetMultimap.newMultimap(Tuples.pair(2, 1), Tuples.pair(1, 1), Tuples.pair(2, 2)).toImmutable();
        Verify.assertSortedSetMultimapsEqual(multimap3, multimap4);
        Verify.assertSortedSetMultimapsEqual("message", multimap3.toImmutable(), multimap4);
        MutableSortedSetMultimap<Integer, Integer> multimap5 = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        multimap5.putAllPairs(Tuples.pair(2, 1), Tuples.pair(1, 1), Tuples.pair(2, 2));
        MutableSortedSetMultimap<Integer, Integer> multimap6 = TreeSortedSetMultimap.newMultimap(Comparators.reverseNaturalOrder());
        multimap6.putAllPairs(Tuples.pair(2, 1), Tuples.pair(1, 1), Tuples.pair(2, 2));
        Verify.assertSortedSetMultimapsEqual(multimap5, multimap6);
        Verify.assertSortedSetMultimapsEqual(multimap5, multimap6.toImmutable());
        Verify.assertSortedSetMultimapsEqual(multimap5.toImmutable(), multimap6.toImmutable());

        multimap3.put(2, 3);
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedSetMultimapsEqual(multimap1, null));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedSetMultimapsEqual(multimap1, multimap3));
        Verify.assertError(AssertionFailedError.class, () -> Verify.assertSortedSetMultimapsEqual(multimap1, multimap5));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedSetMultimapsEqual(multimap1, TreeSortedSetMultimap.newMultimap()));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedSetMultimapsEqual("message", multimap1, TreeSortedSetMultimap.newMultimap(Tuples.pair(1, 2), Tuples.pair(2, 1), Tuples.pair(2, 2))));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedSetMultimapsEqual(multimap1, TreeSortedSetMultimap.newMultimap(Tuples.pair(3, 1), Tuples.pair(2, 1), Tuples.pair(2, 2))));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedSetMultimapsEqual(multimap1, TreeSortedSetMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(2, 2), Tuples.pair(2, 3))));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedSetMultimapsEqual(multimap1, TreeSortedSetMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(2, 1), Tuples.pair(2, 2), Tuples.pair(3, 1))));
    }

    @Test
    public void assertSortedBagMultimapsEquals()
    {
        SortedBagMultimap<Integer, Integer> nullMultimap = null;
        Verify.assertSortedBagMultimapsEqual(null, nullMultimap);
        SortedBagMultimap<Integer, Integer> blankMultimap = TreeBagMultimap.newMultimap();
        Verify.assertSortedBagMultimapsEqual(TreeBagMultimap.newMultimap(), blankMultimap);
        SortedBagMultimap<Integer, Integer> multimap1 = TreeBagMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(2, 1), Tuples.pair(2, 2));
        MutableSortedBagMultimap<Integer, Integer> multimap2 = TreeBagMultimap.newMultimap(Tuples.pair(2, 1), Tuples.pair(1, 1), Tuples.pair(2, 2));
        Verify.assertSortedBagMultimapsEqual(multimap1, multimap2);
        MutableSortedBagMultimap<Integer, Integer> multimap3 = TreeBagMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(2, 1), Tuples.pair(2, 2));
        Verify.assertSortedBagMultimapsEqual(multimap1, multimap3);
        multimap2.put(2, 2);
        multimap3.put(2, 2);
        Verify.assertSortedBagMultimapsEqual(multimap3, multimap2);
        ImmutableSortedBagMultimap<Integer, Integer> multimap4 = TreeBagMultimap.newMultimap(Tuples.pair(2, 1), Tuples.pair(1, 1), Tuples.pair(2, 2), Tuples.pair(2, 2)).toImmutable();
        Verify.assertSortedBagMultimapsEqual(multimap3, multimap4);
        Verify.assertSortedBagMultimapsEqual("message", multimap3.toImmutable(), multimap4);
        MutableSortedBagMultimap<Integer, Integer> multimap5 = TreeBagMultimap.newMultimap(Comparators.reverseNaturalOrder());
        multimap5.putAllPairs(Tuples.pair(2, 1), Tuples.pair(1, 1), Tuples.pair(2, 2));
        MutableSortedBagMultimap<Integer, Integer> multimap6 = TreeBagMultimap.newMultimap(Comparators.reverseNaturalOrder());
        multimap6.putAllPairs(Tuples.pair(2, 1), Tuples.pair(1, 1), Tuples.pair(2, 2));
        Verify.assertSortedBagMultimapsEqual(multimap5, multimap6);
        Verify.assertSortedBagMultimapsEqual(multimap5, multimap6.toImmutable());
        Verify.assertSortedBagMultimapsEqual(multimap5.toImmutable(), multimap6.toImmutable());

        Verify.assertError(AssertionError.class, () -> Verify.assertSortedBagMultimapsEqual(multimap1, null));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedBagMultimapsEqual(multimap1, multimap3));
        Verify.assertError(AssertionFailedError.class, () -> Verify.assertSortedBagMultimapsEqual(multimap1, multimap5));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedBagMultimapsEqual(multimap1, TreeBagMultimap.newMultimap()));
        multimap3.put(2, 3);
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedBagMultimapsEqual(multimap3, multimap4));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedBagMultimapsEqual("message", multimap1, TreeBagMultimap.newMultimap(Tuples.pair(1, 2), Tuples.pair(2, 1), Tuples.pair(2, 2))));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedBagMultimapsEqual(multimap1, TreeBagMultimap.newMultimap(Tuples.pair(3, 1), Tuples.pair(2, 1), Tuples.pair(2, 2))));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedBagMultimapsEqual(multimap1, TreeBagMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(2, 2), Tuples.pair(2, 2))));
        Verify.assertError(AssertionError.class, () -> Verify.assertSortedBagMultimapsEqual(multimap1, TreeBagMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(2, 1), Tuples.pair(2, 2), Tuples.pair(3, 1))));
    }

    @Test
    public void assertSetsEqual()
    {
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2, 3));
        Verify.assertSetsEqual("message", UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2, 3));
        Verify.assertError(AssertionError.class, () -> Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2)));
        Verify.assertError(AssertionError.class, () -> Verify.assertSetsEqual("message", UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2)));
    }

    @Test
    public void assertMapsEqual()
    {
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2));
        Verify.assertMapsEqual("message", UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2));
        Verify.assertError(AssertionError.class, () -> Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3)));
        Verify.assertError(AssertionError.class, () -> Verify.assertMapsEqual("message", UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3)));
    }

    @Test
    public void assertIterablesEqual()
    {
        Verify.assertIterablesEqual(FastList.newListWith(1, 2, 3), TreeSortedSet.newSetWith(1, 2, 3));
        Verify.assertIterablesEqual("message", FastList.newListWith(1, 2, 3), TreeSortedSet.newSetWith(1, 2, 3));
        Verify.assertError(AssertionError.class, () -> Verify.assertIterablesEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2)));
        Verify.assertError(AssertionError.class, () -> Verify.assertIterablesEqual("message", FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2)));
    }

    @Test
    public void assertError()
    {
        Verify.assertError(AssertionError.class, () -> {
            throw new AssertionError();
        });
        Verify.assertError(AssertionError.class, () -> Verify.assertError(AssertionError.class, () -> {
            // do nothing
        }));
    }

    @Test
    public void shallowClone1()
    {
        try
        {
            Cloneable unclonable = new Cloneable()
            {
            };
            Verify.assertShallowClone(unclonable);
            fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[6].toString());
        }
    }

    @Test
    public void shallowClone2()
    {
        Cloneable simpleCloneable = new SimpleCloneable();
        Verify.assertShallowClone(simpleCloneable);
    }

    private static class SimpleCloneable implements Cloneable
    {
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            return !(obj == null || this.getClass() != obj.getClass());
        }

        @Override
        public int hashCode()
        {
            return 0;
        }
    }

    @Test
    public void assertEqualsAndHashCode()
    {
        try
        {
            Verify.assertEqualsAndHashCode(new ConstantHashCode(), new ConstantHashCode());
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertEqualsAndHashCode(new AlwaysEqualWithHashCodeOf(1), new AlwaysEqualWithHashCodeOf(2));
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[5].toString());
        }
    }

    private static class ConstantHashCode
    {
        @Override
        public int hashCode()
        {
            return 1;
        }
    }

    private static final class AlwaysEqualWithHashCodeOf
    {
        private final int hashcode;

        private AlwaysEqualWithHashCodeOf(int hashcode)
        {
            this.hashcode = hashcode;
        }

        @Override
        public int hashCode()
        {
            return this.hashcode;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj != null;
        }
    }

    @Test
    public void assertContainsAllEntries()
    {
        try
        {
            MutableListMultimap<String, Integer> multimap = FastListMultimap.newMultimap(Tuples.pair("one", 1), Tuples.pair("two", 2));
            Verify.assertContainsAllEntries(multimap, "one", 1, "three", 3);
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContainsAllEntries_OddArgumentCount()
    {
        try
        {
            MutableListMultimap<String, Integer> multimap = FastListMultimap.newMultimap(Tuples.pair("one", 1), Tuples.pair("two", 2));
            Verify.assertContainsAllEntries(multimap, "one", 1, "three");
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContainsAll()
    {
        try
        {
            Collection<String> list = FastList.newListWith("One", "Two", "Three");
            Verify.assertContainsAll(list, "Foo", "Bar", "Baz");
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains("these items", e.getMessage());
        }
    }

    @Test
    public void assertInstanceOf()
    {
        try
        {
            Verify.assertInstanceOf(Integer.class, 1L);
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertNotInstanceOf()
    {
        Verify.assertNotInstanceOf(Integer.class, 1L);

        try
        {
            Verify.assertNotInstanceOf(Integer.class, 1);
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertSortedSetsEqual()
    {
        MutableSortedSet<Integer> integers = TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4);
        Verify.assertSortedSetsEqual(null, null);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(), new TreeSet<>());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), new TreeSet<>(FastList.newListWith(1, 2, 3)));
        Verify.assertSortedSetsEqual(new TreeSet<>(integers), integers);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers), integers);

        try
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), new TreeSet<>(FastList.newListWith()));
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), integers);
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4, 5), integers);
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[5].toString());
        }

        try
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 3, 4), integers);
            fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertEmpty()
    {
        try
        {
            Verify.assertEmpty(FastList.newListWith("foo"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertEmpty_PrimitiveIterable()
    {
        try
        {
            Verify.assertEmpty(IntArrayList.newListWith(1));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertEmpty_Iterable()
    {
        try
        {
            Verify.assertIterableEmpty(FastList.newListWith("foo"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertEmpty_Map()
    {
        try
        {
            Verify.assertEmpty(UnifiedMap.newWithKeysValues("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertEmpty_ImmutableMap()
    {
        try
        {
            Verify.assertEmpty(Maps.immutable.of("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertEmpty_Multimap()
    {
        try
        {
            Verify.assertEmpty(FastListMultimap.newMultimap(Tuples.pair("foo", "1"), Tuples.pair("foo", "2")));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("actual size:<2>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertNotEmpty()
    {
        try
        {
            Verify.assertNotEmpty(Lists.mutable.of());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertNotEmpty_PrimitiveIterable()
    {
        try
        {
            Verify.assertNotEmpty(new IntArrayList());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertNotEmpty_Iterable()
    {
        try
        {
            Verify.assertIterableNotEmpty(Lists.mutable.of());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertNotEmpty_Map()
    {
        try
        {
            Verify.assertNotEmpty(UnifiedMap.newMap());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertNotEmpty_Multimap()
    {
        try
        {
            Verify.assertNotEmpty(FastListMultimap.newMultimap());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[4].toString());
        }
    }

    @Test
    public void assertNotEmpty_Array()
    {
        Verify.assertNotEmpty(new Object[]{new Object()});
        try
        {
            Verify.assertNotEmpty(new Object[0]);
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("items", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertSize()
    {
        try
        {
            Verify.assertSize(3, FastList.newListWith("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertCount()
    {
        try
        {
            Verify.assertSize(3, FastList.newListWith("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertSize_Array()
    {
        try
        {
            Verify.assertSize(3, new Object[]{new Object()});
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertSize_Iterable()
    {
        try
        {
            Verify.assertIterableSize(3, FastList.newListWith("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertSize_PrimitiveIterable()
    {
        try
        {
            Verify.assertSize(3, IntArrayList.newListWith(1, 2));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertSize_Map()
    {
        try
        {
            Verify.assertSize(3, UnifiedMap.newWithKeysValues("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertSize_Multimap()
    {
        try
        {
            Verify.assertSize(3, FastListMultimap.newMultimap());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertSize_ImmutableMap()
    {
        try
        {
            Verify.assertSize(3, Maps.immutable.of("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertSize_ImmutableSet()
    {
        try
        {
            Verify.assertSize(3, Sets.immutable.of("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContains_String()
    {
        try
        {
            Verify.assertContains("foo", "bar");
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertAllSatisfy()
    {
        try
        {
            Verify.assertAllSatisfy(FastList.newListWith(1, 3), IntegerPredicates.isEven());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("failed to satisfy the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertAllSatisfy_Map()
    {
        try
        {
            Verify.assertAllSatisfy((Map<?, Integer>) UnifiedMap.newWithKeysValues(1, 1, 3, 3), IntegerPredicates.isEven());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("failed to satisfy the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[4].toString());
        }
    }

    @Test
    public void assertNoneSatisfy()
    {
        try
        {
            Verify.assertNoneSatisfy(FastList.newListWith(1, 3), IntegerPredicates.isOdd());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("satisfied the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertNoneSatisfy_Map()
    {
        try
        {
            Verify.assertNoneSatisfy((Map<?, Integer>) UnifiedMap.newWithKeysValues(1, 1, 3, 3), IntegerPredicates.isOdd());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("satisfied the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[4].toString());
        }
    }

    @Test
    public void assertAnySatisfy()
    {
        try
        {
            Verify.assertAnySatisfy(FastList.newListWith(1, 3), IntegerPredicates.isEven());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("No items satisfied the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[4].toString());
        }
    }

    @Test
    public void assertAnySatisfy_Map()
    {
        try
        {
            Verify.assertAnySatisfy((Map<?, Integer>) UnifiedMap.newWithKeysValues(1, 1, 3, 3), IntegerPredicates.isEven());
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("No items satisfied the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_MissingKeys()
    {
        try
        {
            Verify.assertContainsAllKeyValues(UnifiedMap.newWithKeysValues("foo", "bar"), "baz", "quaz");
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_MissingValues()
    {
        try
        {
            Verify.assertContainsAllKeyValues(UnifiedMap.newWithKeysValues("foo", "bar"), "foo", "quaz");
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("map.valuesView() did not contain these items:<[quaz]>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_OddVarArgCount()
    {
        try
        {
            Verify.assertContainsAllKeyValues(UnifiedMap.newWithKeysValues("foo", "bar"), "baz");
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Odd number of keys and values", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_ImmutableMap_MissingKey()
    {
        try
        {
            Verify.assertContainsAllKeyValues(Maps.immutable.of("foo", "bar"), "baz", "quaz");
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain these items", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_ImmutableMap_MissingValue()
    {
        try
        {
            Verify.assertContainsAllKeyValues(Maps.immutable.of("foo", "bar"), "foo", "quaz");
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain these items", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_ImmutableMap_OddVarArgCount()
    {
        try
        {
            Verify.assertContainsAllKeyValues(Maps.immutable.of("foo", "bar"), "baz");
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("Odd number of keys and values", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContainsNone()
    {
        try
        {
            Verify.assertContainsNone(FastList.newListWith("foo", "bar"), "foo", "bar");
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("has an intersection with", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void denyContainsAny()
    {
        try
        {
            Verify.denyContainsAny(FastList.newListWith("foo", "bar"), "foo", "bar");
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("has an intersection with", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContains_Collection()
    {
        try
        {
            Verify.assertContains("baz", FastList.newListWith("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContains_ImmutableSet()
    {
        try
        {
            Verify.assertContains("bar", Sets.immutable.of("foo"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContainsEntry()
    {
        try
        {
            Verify.assertContainsEntry("foo", "bar", FastListMultimap.newMultimap(Tuples.pair("foo", "baz")));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContainsKey()
    {
        try
        {
            Verify.assertContainsKey("foo", UnifiedMap.newWithKeysValues("foozle", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContainsKey_ImmutableMap()
    {
        try
        {
            Verify.assertContainsKey("foo", Maps.immutable.of("foozle", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void denyContainsKey()
    {
        try
        {
            Verify.denyContainsKey("foo", UnifiedMap.newWithKeysValues("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("contained unexpected", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContainsKeyValue_MissingKey()
    {
        try
        {
            Verify.assertContainsKeyValue("foo", "bar", UnifiedMap.newWithKeysValues("baz", "quaz"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[4].toString());
        }
    }

    @Test
    public void assertContainsKeyValue_MissingValue()
    {
        try
        {
            Verify.assertContainsKeyValue("foo", "bar", UnifiedMap.newWithKeysValues("foo", "quaz"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertContainsKeyValue_ImmutableMap_MissingKey()
    {
        try
        {
            Verify.assertContainsKeyValue("foo", "bar", Maps.immutable.of("baz", "quaz"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[4].toString());
        }
    }

    @Test
    public void assertContainsKeyValue_ImmutableMap_MissingValue()
    {
        try
        {
            Verify.assertContainsKeyValue("foo", "bar", Maps.immutable.of("baz", "quaz"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[4].toString());
        }
    }

    @Test
    public void assertNotContains_Collection()
    {
        try
        {
            Verify.assertNotContains("foo", FastList.newListWith("foo"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("should not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertNotContains_Iterable()
    {
        try
        {
            Verify.assertNotContains("foo", (Iterable<?>) FastList.newListWith("foo"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("should not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertNotContainsKey()
    {
        try
        {
            Verify.assertNotContainsKey("foo", UnifiedMap.newWithKeysValues("foo", "bar"));
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("should not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertClassNonInstantiable()
    {
        Verify.assertClassNonInstantiable(SerializeTestHelper.class);

        try
        {
            Verify.assertClassNonInstantiable(VerifyTest.class);
            fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains("to be non-instantiable", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[2].toString());
        }
    }

    @Test
    public void assertNotSerializable()
    {
        Verify.assertNotSerializable(new Object());

        try
        {
            Verify.assertNotSerializable("serializable");
        }
        catch (AssertionError ex)
        {
            assertEquals("Block did not throw an exception of type java.io.NotSerializableException", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    @Test
    public void assertPostSerializedEqualsAndHashCode()
    {
        Verify.assertPostSerializedEqualsAndHashCode(Tuples.pair("1", "2"));
        try
        {
            Verify.assertPostSerializedEqualsAndHashCode(new Object());
        }
        catch (AssertionError ex)
        {
            assertEquals("Failed to marshal an object", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
    }

    @Test
    public void assertPostSerializedEqualsHashCodeAndToString()
    {
        Verify.assertPostSerializedEqualsHashCodeAndToString(Tuples.pair("1", "2"));
        try
        {
            Verify.assertPostSerializedEqualsHashCodeAndToString(new Object());
        }
        catch (AssertionError ex)
        {
            assertEquals("Failed to marshal an object", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[5].toString());
        }
        try
        {
            Verify.assertPostSerializedEqualsHashCodeAndToString(new TestClass());
        }
        catch (AssertionError ex)
        {
            assertEquals("not same toString", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[3].toString());
        }
    }

    private static class TestClass implements Serializable
    {
        @Override
        public boolean equals(Object o)
        {
            return o != null;
        }

        @Override
        public int hashCode()
        {
            return 0;
        }
    }
}
