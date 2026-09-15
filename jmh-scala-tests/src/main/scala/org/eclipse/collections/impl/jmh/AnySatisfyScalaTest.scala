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

object AnySatisfyScalaTest
{
    private val SIZE = 1000000
    private val integers = new ArrayBuffer[Int](SIZE) ++ (1 to SIZE)

    def short_circuit_middle_serial_eager_scala
    {
        Assert.assertTrue(this.integers.exists(_ > SIZE / 2))
    }

    def process_all_serial_eager_scala
    {
        Assert.assertFalse(this.integers.exists(_ < 0))
    }

    def short_circuit_middle_serial_lazy_scala
    {
        Assert.assertTrue(this.integers.view.exists(_ > SIZE / 2))
    }

    def process_all_serial_lazy_scala
    {
        Assert.assertFalse(this.integers.view.exists(_ < 0))
    }

    def short_circuit_middle_parallel_lazy_scala
    {
        Assert.assertTrue(this.integers.par.exists(_ == SIZE / 2 - 1))
    }

    def process_all_parallel_lazy_scala
    {
        Assert.assertFalse(this.integers.par.exists(_ < 0))
    }
}
