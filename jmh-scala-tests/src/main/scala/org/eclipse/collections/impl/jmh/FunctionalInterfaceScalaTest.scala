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

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.CollectionConverters._

object FunctionalInterfaceScalaTest
{
    private val SIZE: Int = 1000000
    private val integers: ArrayBuffer[Int] = new ArrayBuffer[Int]() ++ (1 to SIZE)

    def megamorphic(megamorphicWarmupLevel: Int)
    {
        val predicate1: (Int) => Boolean = each => (each + 2) % 10000 != 0
        val predicate2: (Int) => Boolean = each => (each + 3) % 10000 != 0
        val predicate3: (Int) => Boolean = each => (each + 4) % 10000 != 0
        val predicate4: (Int) => Boolean = each => (each + 5) % 10000 != 0

        val function1: (Int) => String = each =>
        {
            Assert.assertNotNull(each)
            String.valueOf(each)
        }

        val function2: (String) => Int = each =>
        {
            Assert.assertNotNull(each)
            Integer.valueOf(each)
        }

        val function3: (Int) => String = each =>
        {
            Assert.assertEquals(each, each)
            String.valueOf(each)
        }

        val function4: (String) => Int = each =>
        {
            Assert.assertEquals(each, each)
            Integer.valueOf(each)
        }

        if (megamorphicWarmupLevel > 0)
        {
            // serial, lazy, EC
            {
                val set = this.integers.view.filter(predicate1).map(function1).map(function2).filter(predicate2).toSet
                Assert.assertEquals(999800, set.size)
                val buffer = this.integers.view.filter(predicate3).map(function3).map(function4).filter(predicate4).toBuffer
                Assert.assertEquals(999800, buffer.size)
            }

            // parallel, lazy, EC
            {
                val set = this.integers.par.filter(predicate1).map(function1).map(function2).filter(predicate2).toSet
                Assert.assertEquals(999800, set.size)
                val buffer = this.integers.par.filter(predicate3).map(function3).map(function4).filter(predicate4).toBuffer
                Assert.assertEquals(999800, buffer.size)
            }

            // serial, eager, EC
            {
                val set = this.integers.filter(predicate1).map(function1).map(function2).filter(predicate2).toSet
                Assert.assertEquals(999800, set.size)
                val buffer = this.integers.filter(predicate3).map(function3).map(function4).filter(predicate4).toBuffer
                Assert.assertEquals(999800, buffer.size)
            }
        }
    }

    def serial_eager_scala(): ArrayBuffer[Integer] =
    {
        val list = this.integers
                .filter(each => each % 10000 != 0)
                .map(String.valueOf)
                .map(Integer.valueOf)
                .filter(each => (each + 1) % 10000 != 0)
        Assert.assertEquals(999800, list.size)
        list
    }

    def test_serial_eager_scala()
    {
        Assert.assertEquals(1.to(1000000, 10000).flatMap(each => each.to(each + 9997)).toBuffer, this.serial_eager_scala())
    }

    def serial_lazy_scala(): mutable.Buffer[Integer] =
    {
        val list = this.integers
                .view
                .filter(each => each % 10000 != 0)
                .map(String.valueOf)
                .map(Integer.valueOf)
                .filter(each => (each + 1) % 10000 != 0)
                .toBuffer
        Assert.assertEquals(999800, list.size)
        list
    }

    def test_serial_lazy_scala()
    {
        Assert.assertEquals(1.to(1000000, 10000).flatMap(each => each.to(each + 9997)).toBuffer, this.serial_lazy_scala())
    }

    def parallel_lazy_scala(): mutable.Buffer[Integer] =
    {
        val list = this.integers
                .par
                .filter(each => each % 10000 != 0)
                .map(String.valueOf)
                .map(Integer.valueOf)
                .filter(each => (each + 1) % 10000 != 0)
                .toBuffer
        Assert.assertEquals(999800, list.size)
        list
    }

    def test_parallel_lazy_scala()
    {
        Assert.assertEquals(1.to(1000000, 10000).flatMap(each => each.to(each + 9997)).toBuffer, this.parallel_lazy_scala())
    }

    def parallel_lazy_scala_hand_coded(): mutable.Buffer[Int] =
    {
        val list = this.integers
                .par
                .filter(integer => integer % 10000 != 0 && (Integer.valueOf(String.valueOf(integer)) + 1) % 10000 != 0)
                .toBuffer
        Assert.assertEquals(999800, list.size)
        list
    }

    def test_parallel_lazy_scala_hand_coded()
    {
        Assert.assertEquals(1.to(1000000, 10000).flatMap(each => each.to(each + 9997)).toBuffer, this.parallel_lazy_scala_hand_coded())
    }
}
