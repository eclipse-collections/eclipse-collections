/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh.set.sorted

import scala.collection.{immutable, mutable}
import scala.collection.parallel.CollectionConverters._

object ScalaSortedSetIterationTest
{
    private val SIZE = 1000000

    val scalaMutable: mutable.TreeSet[Int] = new mutable.TreeSet[Int]() ++ (0 to SIZE)
    val scalaImmutable: immutable.TreeSet[Int] = immutable.TreeSet.empty[Int] ++ (0 to SIZE)

    def serial_mutable_scala(): Unit =
    {
        val count: Int = this.scalaMutable
                .view
                .filter(each => each % 10000 != 0)
                .map(String.valueOf)
                .map(Integer.valueOf)
                .count(each => (each + 1) % 10000 != 0)
        if (count != 999800)
        {
            throw new AssertionError
        }
    }

    def serial_immutable_scala(): Unit =
    {
        val count: Int = this.scalaImmutable
                .view
                .filter(each => each % 10000 != 0)
                .map(String.valueOf)
                .map(Integer.valueOf)
                .count(each => (each + 1) % 10000 != 0)
        if (count != 999800)
        {
            throw new AssertionError
        }
    }

    def parallel_mutable_scala(): Unit =
    {
        val count: Int = this.scalaMutable.par
                .filter(each => each % 10000 != 0)
                .map(String.valueOf)
                .map(Integer.valueOf)
                .count(each => (each + 1) % 10000 != 0)
        if (count != 999800)
        {
            throw new AssertionError
        }
    }

    def parallel_immutable_scala(): Unit =
    {
        val count: Int = this.scalaImmutable.par
                .filter(each => each % 10000 != 0)
                .map(String.valueOf)
                .map(Integer.valueOf)
                .count(each => (each + 1) % 10000 != 0)
        if (count != 999800)
        {
            throw new AssertionError
        }
    }
}
