/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.Containers;
import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import scala.collection.mutable.HashTable;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
public class ChainMapPutTest
{
    private static final int RANDOM_COUNT = 9;

    @Param({"250000", "500000", "750000", "1000000", "1250000", "1500000", "1750000", "2000000", "2250000", "2500000", "2750000", "3000000",
            "3250000", "3500000", "3750000", "4000000", "4250000", "4500000", "4750000", "5000000", "5250000", "5500000", "5750000", "6000000",
            "6250000", "6500000", "6750000", "7000000", "7250000", "7500000", "7750000", "8000000", "8250000", "8500000", "8750000", "9000000",
            "9250000", "9500000", "9750000", "10000000"})
    public int size;
    @Param({"true", "false"})
    public boolean isPresized;
    @Param({"0.70f", "0.75f", "0.80f"})
    public float loadFactor;
    private String[] elements;

    @Setup
    public void setUp()
    {
        this.elements = new String[this.size];

        Random random = new Random(123456789012345L);
        for (int i = 0; i < this.size; i++)
        {
            this.elements[i] = RandomStringUtils.random(RANDOM_COUNT, 0, 0, false, true, null, random);
        }
    }

    @Benchmark
    public MutableMap<String, String> ec()
    {
        int localSize = this.size;
        float localLoadFactor = this.loadFactor;
        String[] localElements = this.elements;
        /**
         * @see UnifiedMap#DEFAULT_INITIAL_CAPACITY
         */
        int defaultInitialCapacity = 8;

        MutableMap<String, String> ec = this.isPresized
                ? UnifiedMap.newMap(localSize, localLoadFactor)
                : UnifiedMap.newMap(defaultInitialCapacity, localLoadFactor);

        for (int i = 0; i < localSize; i++)
        {
            ec.put(localElements[i], "dummy");
        }
        return ec;
    }

    @Benchmark
    public ObjectObjectMap<String, String> hppc()
    {
        int localSize = this.size;
        float localLoadFactor = this.loadFactor;
        String[] localElements = this.elements;
        int defaultInitialCapacity = Containers.DEFAULT_EXPECTED_ELEMENTS;

        ObjectObjectMap<String, String> hppc = this.isPresized
                ? new ObjectObjectHashMap<>(localSize, localLoadFactor)
                : new ObjectObjectHashMap<>(defaultInitialCapacity, localLoadFactor);

        for (int i = 0; i < localSize; i++)
        {
            hppc.put(localElements[i], "dummy");
        }
        return hppc;
    }

    @Benchmark
    public Map<String, String> jdk()
    {
        int localSize = this.size;
        float localLoadFactor = this.loadFactor;
        String[] localElements = this.elements;

        /**
         * @see HashMap#DEFAULT_INITIAL_CAPACITY
         */
        int defaultInitialCapacity = 16;

        Map<String, String> jdk = this.isPresized
                ? new HashMap<>(localSize, localLoadFactor)
                : new HashMap<>(defaultInitialCapacity, localLoadFactor);

        for (int i = 0; i < localSize; i++)
        {
            jdk.put(localElements[i], "dummy");
        }
        return jdk;
    }

    @Benchmark
    public scala.collection.mutable.HashMap<String, String> scala()
    {
        int localSize = this.size;
        if (Float.compare(this.loadFactor, 0.75f) != 0)
        {
            throw new IllegalArgumentException();
        }
        String[] localElements = this.elements;

        /**
         * @see HashTable#initialSize()
         */
        int defaultInitialSize = 16;

        scala.collection.mutable.HashMap<String, String> scala = this.isPresized
                ? new PresizableHashMap<>(localSize)
                : new PresizableHashMap<>(defaultInitialSize);

        for (int i = 0; i < localSize; i++)
        {
            scala.put(localElements[i], "dummy");
        }
        return scala;
    }
}
