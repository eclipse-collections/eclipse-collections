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

import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.CollectionConverters._

object CountScalaTest
{
    private val SIZE = 1000000
    private val integers = new ArrayBuffer[Int](SIZE) ++ (1 to SIZE)

    def megamorphic(megamorphicWarmupLevel: Int): Unit =
    {
        if (megamorphicWarmupLevel > 0)
        {
            // serial, lazy
            {
                val evens = this.integers.view.count(_ % 2 == 0)
                Assert.assertEquals(SIZE / 2, evens)
                val odds = this.integers.view.count(_ % 2 == 1)
                Assert.assertEquals(SIZE / 2, odds)
                val evens2 = this.integers.view.count(each => (each & 1) == 0)
                Assert.assertEquals(SIZE / 2, evens2)
            }

            // parallel, lazy
            {
                val evens = this.integers.par.count(_ % 2 == 0)
                Assert.assertEquals(SIZE / 2, evens)
                val odds = this.integers.par.count(_ % 2 == 1)
                Assert.assertEquals(SIZE / 2, odds)
                val evens2 = this.integers.par.count(each => (each & 1) == 0)
                Assert.assertEquals(SIZE / 2, evens2)
            }

            // serial, eager
            {
                val evens = this.integers.count(_ % 2 == 0)
                Assert.assertEquals(SIZE / 2, evens)
                val odds = this.integers.count(_ % 2 == 1)
                Assert.assertEquals(SIZE / 2, odds)
                val evens2 = this.integers.count(each => (each & 1) == 0)
                Assert.assertEquals(SIZE / 2, evens2)
            }
        }

        // deoptimize scala.collection.mutable.ResizableArray.foreach()
        // deoptimize scala.collection.IndexedSeqOptimized.foreach()
        if (megamorphicWarmupLevel > 1)
        {
            this.integers.view.foreach(Assert.assertNotNull(_))
            this.integers.view.foreach(each => Assert.assertEquals(each, each))
            this.integers.view.foreach(each => Assert.assertNotEquals(null, each))
            this.integers.par.foreach(Assert.assertNotNull(_))
            this.integers.par.foreach(each => Assert.assertEquals(each, each))
            this.integers.par.foreach(each => Assert.assertNotEquals(null, each))
        }

        // deoptimize scala.collection.mutable.ResizableArray.foreach()
        // deoptimize scala.collection.IndexedSeqOptimized.foreach()
        if (megamorphicWarmupLevel > 2)
        {
            this.integers.foreach(Assert.assertNotNull(_))
            this.integers.foreach(each => Assert.assertEquals(each, each))
            this.integers.foreach(each => Assert.assertNotEquals(null, each))
        }
    }

    def serial_eager_scala(): Unit =
    {
        val evens = this.integers.count(_ % 2 == 0)
        Assert.assertEquals(SIZE / 2, evens)
    }

    def serial_lazy_scala(): Unit =
    {
        val evens = this.integers.view.count(_ % 2 == 0)
        Assert.assertEquals(SIZE / 2, evens)
    }

    def parallel_lazy_scala(): Unit =
    {
        val evens = this.integers.par.count(_ % 2 == 0)
        Assert.assertEquals(SIZE / 2, evens)
    }
}
