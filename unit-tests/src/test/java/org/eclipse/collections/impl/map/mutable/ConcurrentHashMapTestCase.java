/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable;

import java.util.Collections;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class ConcurrentHashMapTestCase extends MutableMapTestCase
{
    protected ExecutorService executor;

    @BeforeEach
    public void setUp()
    {
        this.executor = Executors.newFixedThreadPool(20);
    }

    @AfterEach
    public void tearDown()
    {
        this.executor.shutdown();
    }

    @Override
    protected abstract <K, V> ConcurrentMutableMap<K, V> newMap();

    @Override
    @Test
    public void updateValue()
    {
        super.updateValue();

        ConcurrentMutableMap<Integer, Integer> map = this.newMap();
        ParallelIterate.forEach(Interval.oneTo(100), each -> map.updateValue(each % 10, () -> 0, integer -> integer + 1), 1, this.executor);
        assertEquals(Interval.zeroTo(9).toSet(), map.keySet());
        assertEquals(FastList.newList(Collections.nCopies(10, 10)), FastList.newList(map.values()));
    }

    @Override
    @Test
    public void updateValue_collisions()
    {
        super.updateValue_collisions();

        ConcurrentMutableMap<Integer, Integer> map = this.newMap();
        MutableList<Integer> list = Interval.oneTo(100).toList().shuffleThis();
        ParallelIterate.forEach(list, each -> map.updateValue(each % 50, () -> 0, integer -> integer + 1), 1, this.executor);
        assertEquals(Interval.zeroTo(49).toSet(), map.keySet());
        assertEquals(
                FastList.newList(Collections.nCopies(50, 2)),
                FastList.newList(map.values()),
                HashBag.newBag(map.values()).toStringOfItemToCount());
    }

    @Override
    @Test
    public void updateValueWith()
    {
        super.updateValueWith();

        ConcurrentMutableMap<Integer, Integer> map = this.newMap();
        ParallelIterate.forEach(Interval.oneTo(100), each -> map.updateValueWith(each % 10, () -> 0, (integer, parameter) -> {
            assertEquals("test", parameter);
            return integer + 1;
        }, "test"), 1, this.executor);
        assertEquals(Interval.zeroTo(9).toSet(), map.keySet());
        assertEquals(FastList.newList(Collections.nCopies(10, 10)), FastList.newList(map.values()));
    }

    @Override
    @Test
    public void updateValueWith_collisions()
    {
        super.updateValueWith_collisions();

        ConcurrentMutableMap<Integer, Integer> map = this.newMap();
        MutableList<Integer> list = Interval.oneTo(200).toList().shuffleThis();
        ParallelIterate.forEach(list, each -> map.updateValueWith(each % 100, () -> 0, (integer, parameter) -> {
            assertEquals("test", parameter);
            return integer + 1;
        }, "test"), 1, this.executor);
        assertEquals(Interval.zeroTo(99).toSet(), map.keySet());
        assertEquals(
                FastList.newList(Collections.nCopies(100, 2)),
                FastList.newList(map.values()),
                HashBag.newBag(map.values()).toStringOfItemToCount());
    }

    @Test
    public void keySetValuesEntrySetSpliteratorsAreConcurrentAndNotSized()
    {
        ConcurrentMap<Integer, String> map = this.newMap();
        map.put(1, "1");

        Spliterator<Integer> ks = map.keySet().spliterator();
        assertFalse(ks.hasCharacteristics(Spliterator.ORDERED));
        assertTrue(ks.hasCharacteristics(Spliterator.DISTINCT));
        assertFalse(ks.hasCharacteristics(Spliterator.SORTED));
        assertFalse(ks.hasCharacteristics(Spliterator.SIZED));
        assertTrue(ks.hasCharacteristics(Spliterator.NONNULL));
        assertFalse(ks.hasCharacteristics(Spliterator.IMMUTABLE));
        assertTrue(ks.hasCharacteristics(Spliterator.CONCURRENT));
        assertFalse(ks.hasCharacteristics(Spliterator.SUBSIZED));

        Spliterator<String> vs = map.values().spliterator();
        assertFalse(vs.hasCharacteristics(Spliterator.ORDERED));
        assertFalse(vs.hasCharacteristics(Spliterator.DISTINCT));
        assertFalse(vs.hasCharacteristics(Spliterator.SORTED));
        assertFalse(vs.hasCharacteristics(Spliterator.SIZED));
        assertTrue(vs.hasCharacteristics(Spliterator.NONNULL));
        assertFalse(vs.hasCharacteristics(Spliterator.IMMUTABLE));
        assertTrue(vs.hasCharacteristics(Spliterator.CONCURRENT));
        assertFalse(vs.hasCharacteristics(Spliterator.SUBSIZED));

        Spliterator<Map.Entry<Integer, String>> es = map.entrySet().spliterator();
        assertFalse(es.hasCharacteristics(Spliterator.ORDERED));
        assertTrue(es.hasCharacteristics(Spliterator.DISTINCT));
        assertFalse(es.hasCharacteristics(Spliterator.SORTED));
        assertFalse(es.hasCharacteristics(Spliterator.SIZED));
        assertTrue(es.hasCharacteristics(Spliterator.NONNULL));
        assertFalse(es.hasCharacteristics(Spliterator.IMMUTABLE));
        assertTrue(es.hasCharacteristics(Spliterator.CONCURRENT));
        assertFalse(es.hasCharacteristics(Spliterator.SUBSIZED));
    }

    @Test
    public void raceConditionReproductionTest()
    {
        ConcurrentMap<Integer, String> eclipseMap = this.newMap();
        IntStream.range(0, 200000)
                .boxed()
                .forEach(i -> eclipseMap.put(i, String.valueOf(i)));

        CompletableFuture.allOf(
                CompletableFuture.runAsync(
                        () -> eclipseMap.values()
                                .stream()
                                .peek(s -> randomWait())
                                .toArray(String[]::new)),
                CompletableFuture.runAsync(
                        () -> IntStream.range(200000, 400000)
                                .boxed()
                                .peek(i -> randomWait())
                                .forEach(i -> eclipseMap.put(i, String.valueOf(i))))).join();
    }

    private static void randomWait()
    {
        try
        {
            if (ThreadLocalRandom.current().nextInt(1000) == 0)
            {
                Thread.sleep(1);
            }
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
