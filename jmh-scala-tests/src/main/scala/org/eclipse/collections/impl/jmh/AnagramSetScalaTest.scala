/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh

import java.util
import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

import org.apache.commons.lang3.RandomStringUtils
import org.junit.Assert

import scala.collection.mutable
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.immutable.ParSet

object AnagramSetScalaTest
{
    private val SIZE = 1000000
    private val CORES = Runtime.getRuntime.availableProcessors
    private val SIZE_THRESHOLD = 10
    private val WORDS: Set[String] = Set(Seq.fill(SIZE)(RandomStringUtils.randomAlphabetic(5).toUpperCase()): _*)

    private var executorService: ExecutorService = null

    def setUp(): Unit =
    {
        this.executorService = Executors.newFixedThreadPool(CORES)
    }

    def tearDown(): Unit =
    {
        this.executorService.shutdownNow
        this.executorService.awaitTermination(1L, TimeUnit.MILLISECONDS)
    }

    def serial_eager_scala(): Unit =
    {
        WORDS
                .groupBy(Alphagram.apply(_))
                .values
                .filter(_.size >= SIZE_THRESHOLD)
                .toSeq.sortBy(_.size)
                .reverseIterator
                .map(iterable => iterable.size + ": " + iterable.toString())
                .foreach(string => Assert.assertFalse(string.isEmpty))
    }

    def serial_lazy_scala(): Unit =
    {
        WORDS.view
                .groupBy(Alphagram.apply(_))
                .values
                .filter(_.size >= SIZE_THRESHOLD)
                .toSeq.sortBy(_.size)
                .reverseIterator
                .map(iterable => iterable.size + ": " + iterable.toString())
                .foreach(string => Assert.assertFalse(string.isEmpty))
    }

    def parallel_lazy_scala(): Unit =
    {
        val toBuffer: mutable.Buffer[ParSet[String]] = WORDS.par
                .groupBy(Alphagram.apply(_))
                .values
                .filter(_.size >= SIZE_THRESHOLD)
                .toBuffer
        toBuffer.sortBy(_.size)
                .par
                .reverseMap(iterable => iterable.size + ": " + iterable.toString())
                .foreach(string => Assert.assertFalse(string.isEmpty))
    }

    object Alphagram
    {
        def apply(string: String): Alphagram =
        {
            val key = string.toCharArray
            util.Arrays.sort(key)
            new Alphagram(key)
        }
    }

    class Alphagram(private val key: Array[Char])
    {
        override def equals(o: Any): Boolean =
        {
            if (null == o || this.getClass != o.getClass)
            {
                return false
            }
            val other = o.asInstanceOf[Alphagram]
            if (this eq other)
            {
                return true
            }
            util.Arrays.equals(this.key, other.key)
        }

        override def hashCode = util.Arrays.hashCode(this.key)

        override def toString = new String(this.key)
    }
}
