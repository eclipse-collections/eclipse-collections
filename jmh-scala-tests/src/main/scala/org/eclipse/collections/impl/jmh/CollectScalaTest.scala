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

object CollectScalaTest
{
    private val SIZE = 1000000
    private val integers = new ArrayBuffer[Int](SIZE) ++ (1 to SIZE)

    def serial_eager_scala()
    {
        val strings = this.integers.map(_.toString)
        Assert.assertEquals(SIZE, strings.size)
    }

    def serial_lazy_scala()
    {
        val strings = this.integers.view.map(_.toString).toBuffer
        Assert.assertEquals(SIZE, strings.size)
    }

    def parallel_lazy_scala()
    {
        val strings = this.integers.par.map(_.toString)
        Assert.assertEquals(SIZE, strings.size)
    }

}
