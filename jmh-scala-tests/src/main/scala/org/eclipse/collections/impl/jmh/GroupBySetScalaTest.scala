/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh

import org.junit.Assert

import scala.collection.View
import scala.collection.mutable.HashSet
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.immutable.ParMap
import scala.collection.parallel.mutable.ParHashSet

object GroupBySetScalaTest
{
    private val SIZE = 1000000
    private val integers = new HashSet[Int]() ++ (0 to SIZE - 1)

    def groupBy_2_keys_serial_eager_scala(): Map[Boolean, HashSet[Int]] =
    {
        val multimap: Map[Boolean, HashSet[Int]] = this.integers.groupBy((each: Int) => each % 2 == 0)
        Assert.assertEquals(2, multimap.size)
        multimap
    }

    def test_groupBy_2_keys_serial_eager_scala(): Unit =
    {
        val multimap: Map[Boolean, HashSet[Int]] = groupBy_2_keys_serial_eager_scala()
        val odds: HashSet[Int] = multimap(false)
        val evens: HashSet[Int] = multimap(true)
        Assert.assertEquals(0.to(999999, 2).toSet, evens)
        Assert.assertEquals(1.to(999999, 2).toSet, odds)
    }

    def groupBy_100_keys_serial_eager_scala(): Map[Int, HashSet[Int]] =
    {
        val multimap: Map[Int, HashSet[Int]] = this.integers.groupBy((each: Int) => each % 100)
        Assert.assertEquals(100, multimap.size)
        multimap
    }

    def test_groupBy_100_keys_serial_eager_scala(): Unit =
    {
        val multimap: Map[Int, HashSet[Int]] = groupBy_100_keys_serial_eager_scala()
        for (i <- 0 to 99)
        {
            val integers: HashSet[Int] = multimap(i)
            Assert.assertEquals(i.to(999999, 100).toSet, integers)
        }
    }

    def groupBy_10000_keys_serial_eager_scala(): Map[Int, HashSet[Int]] =
    {
        val multimap: Map[Int, HashSet[Int]] = this.integers.groupBy((each: Int) => each % 10000)
        Assert.assertEquals(10000, multimap.size)
        multimap
    }

    def test_groupBy_10000_keys_serial_eager_scala(): Unit =
    {
        val multimap: Map[Int, HashSet[Int]] = groupBy_10000_keys_serial_eager_scala()
        for (i <- 0 to 9999)
        {
            val integers: HashSet[Int] = multimap(i)
            Assert.assertEquals(i.to(999999, 10000).toSet, integers)
        }
    }

    def groupBy_unordered_lists_2_keys_serial_lazy_scala(): Map[Boolean, View[Int]] =
    {
        val multimap: Map[Boolean, View[Int]] = this.integers.view.groupBy((each: Int) => each % 2 == 0)
        Assert.assertEquals(2, multimap.size)
        multimap
    }

    def test_groupBy_unordered_lists_2_keys_serial_lazy_scala(): Unit =
    {
        val multimap: Map[Boolean, View[Int]] = groupBy_unordered_lists_2_keys_serial_lazy_scala()
        val evens: View[Int] = multimap(true)
        val odds: View[Int] = multimap(false)
        Assert.assertEquals(0.to(999999, 2).toSet, evens.toSet)
        Assert.assertEquals(1.to(999999, 2).toSet, odds.toSet)
    }

    def groupBy_unordered_lists_100_keys_serial_lazy_scala(): Map[Int, View[Int]] =
    {
        val multimap: Map[Int, View[Int]] = this.integers.view.groupBy((each: Int) => each % 100)
        Assert.assertEquals(100, multimap.size)
        multimap
    }

    def test_groupBy_unordered_lists_100_keys_serial_lazy_scala(): Unit =
    {
        val multimap: Map[Int, View[Int]] = groupBy_unordered_lists_100_keys_serial_lazy_scala()
        for (i <- 0 to 99)
        {
            val integers: View[Int] = multimap(i)
            Assert.assertEquals(i.to(999999, 100).toSet, integers.toSet)
        }
    }

    def groupBy_unordered_lists_10000_keys_serial_lazy_scala(): Map[Int, View[Int]] =
    {
        val multimap: Map[Int, View[Int]] = this.integers.view.groupBy((each: Int) => each % 10000)
        Assert.assertEquals(10000, multimap.size)
        multimap
    }

    def test_groupBy_unordered_lists_10000_keys_serial_lazy_scala(): Unit =
    {
        val multimap: Map[Int, View[Int]] = groupBy_unordered_lists_10000_keys_serial_lazy_scala()
        for (i <- 0 to 9999)
        {
            val integers: View[Int] = multimap(i)
            Assert.assertEquals(i.to(999999, 10000).toSet, integers.toSet)
        }
    }

    def groupBy_2_keys_parallel_lazy_scala(): ParMap[Boolean, ParHashSet[Int]] =
    {
        val multimap: ParMap[Boolean, ParHashSet[Int]] = this.integers.par.groupBy((each: Int) => each % 2 == 0)
        Assert.assertEquals(2, multimap.size)
        multimap
    }

    def test_groupBy_2_keys_parallel_lazy_scala(): Unit =
    {
        val multimap: ParMap[Boolean, ParHashSet[Int]] = groupBy_2_keys_parallel_lazy_scala()
        val evens: ParHashSet[Int] = multimap(true)
        val odds: ParHashSet[Int] = multimap(false)
        Assert.assertEquals(0.to(999999, 2).toSet, evens)
        Assert.assertEquals(1.to(999999, 2).toSet, odds)
    }

    def groupBy_100_keys_parallel_lazy_scala(): ParMap[Int, ParHashSet[Int]] =
    {
        val multimap: ParMap[Int, ParHashSet[Int]] = this.integers.par.groupBy((each: Int) => each % 100)
        Assert.assertEquals(100, multimap.size)
        multimap
    }

    def test_groupBy_100_keys_parallel_lazy_scala(): Unit =
    {
        val multimap: ParMap[Int, ParHashSet[Int]] = groupBy_100_keys_parallel_lazy_scala()
        for (i <- 0 to 99)
        {
            val integers: ParHashSet[Int] = multimap(i)
            Assert.assertEquals(i.to(999999, 100).toSet, integers)
        }
    }

    def groupBy_10000_keys_parallel_lazy_scala(): ParMap[Int, ParHashSet[Int]] =
    {
        val multimap: ParMap[Int, ParHashSet[Int]] = this.integers.par.groupBy((each: Int) => each % 10000)
        Assert.assertEquals(10000, multimap.size)
        multimap
    }

    def test_groupBy_10000_keys_parallel_lazy_scala(): Unit =
    {
        val multimap: ParMap[Int, ParHashSet[Int]] = groupBy_10000_keys_parallel_lazy_scala()
        for (i <- 0 to 9999)
        {
            val integers: ParHashSet[Int] = multimap(i)
            Assert.assertEquals(i.to(999999, 10000).toSet, integers)
        }
    }
}
