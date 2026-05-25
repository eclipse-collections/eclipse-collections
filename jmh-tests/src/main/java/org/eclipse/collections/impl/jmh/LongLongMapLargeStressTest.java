/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
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

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
public class LongLongMapLargeStressTest
{
    private static final int LOOP_COUNT = 1;
    private static final int KEY_COUNT = 400_000;
    private static final int MAP_SIZE = 1_000_000;

    @Param({"true", "false"})
    public boolean fullyRandom;
    private LongLongMap longLongKoloboke;
    private MutableLongLongMap longLongEc;
    private long[] ecLongKeysForMap;
    private long[] kolobokeLongKeysForMap;

    private int kolobokeIndex(int key)
    {
        long h = key * 0x9e3779b97f4a7c15L;
        h ^= h >> 32;
        return this.mask((int) (h ^ (h >> 16)));
    }

    private int ecIndex(int element)
    {
        return this.mask(element);
    }

    private int mask(int spread)
    {
        return spread & ((1 << 20) - 1);
    }

    @Setup
    public void setUp()
    {
        this.longLongKoloboke = HashLongLongMaps.newMutableMap(MAP_SIZE);
        this.longLongEc = new LongLongHashMap(MAP_SIZE);

        Random random = new Random(0x123456789ABCDL);

        int number = 23;
        int lower = Integer.MIN_VALUE;
        int upper = Integer.MAX_VALUE;

        long[] randomNumbersForMap = this.getRandomKeys(random).toArray();

        this.ecLongKeysForMap = this.fullyRandom
                ? randomNumbersForMap
                : this.getECArray(number, lower, upper, random);
        this.kolobokeLongKeysForMap = this.fullyRandom
                ? randomNumbersForMap
                : this.getKolobokeArray(number, lower, upper, random);

        for (int i = 0; i < KEY_COUNT; i++)
        {
            this.longLongKoloboke.put(this.kolobokeLongKeysForMap[i], 5);
            this.longLongEc.put(this.ecLongKeysForMap[i], 5);
        }

        this.shuffle(this.ecLongKeysForMap, random);
        this.shuffle(this.kolobokeLongKeysForMap, random);
    }

    private MutableLongSet getRandomKeys(Random random)
    {
        MutableLongSet set = new LongHashSet(KEY_COUNT);
        while (set.size() < KEY_COUNT)
        {
            set.add(random.nextLong());
        }
        return set;
    }

    protected long[] getECArray(int number, int lower, int upper, Random random)
    {
        long[] ecCollisions = this.getECSequenceCollisions(number, lower, upper).toArray();
        this.shuffle(ecCollisions, random);
        return ecCollisions;
    }

    private MutableLongList getECSequenceCollisions(int number, int lower, int upper)
    {
        MutableLongList ecCollidingNumbers = new LongArrayList();
        for (int i = lower; i < upper && ecCollidingNumbers.size() < KEY_COUNT; i++)
        {
            int index = this.ecIndex(i);
            if (index >= number && index <= number + 100)
            {
                ecCollidingNumbers.add(i);
            }
        }
        return ecCollidingNumbers;
    }

    protected long[] getKolobokeArray(int number, int lower, int upper, Random random)
    {
        long[] kolobokeCollisions = this.getKolobokeSequenceCollisions(number, lower, upper).toArray();
        this.shuffle(kolobokeCollisions, random);
        return kolobokeCollisions;
    }

    private MutableLongList getKolobokeSequenceCollisions(int number, int lower, int upper)
    {
        MutableLongList kolobokeCollidingNumbers = new LongArrayList();
        for (int i = lower; i < upper && kolobokeCollidingNumbers.size() < KEY_COUNT; i++)
        {
            int index = this.kolobokeIndex(i);
            if (index >= number && index <= number + 100)
            {
                kolobokeCollidingNumbers.add(i);
            }
        }
        return kolobokeCollidingNumbers;
    }

    @Benchmark
    public void kolobokeGet()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            for (int i = 0; i < KEY_COUNT; i++)
            {
                if (this.longLongKoloboke.get(this.kolobokeLongKeysForMap[i]) == this.longLongKoloboke.defaultValue())
                {
                    throw new AssertionError(this.kolobokeLongKeysForMap[i] + " not in map");
                }
            }
            if (this.longLongKoloboke.size() != KEY_COUNT)
            {
                throw new AssertionError("size is " + this.longLongKoloboke.size());
            }
        }
    }

    @Benchmark
    public void ecGet()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            for (int i = 0; i < KEY_COUNT; i++)
            {
                if (this.longLongEc.get(this.ecLongKeysForMap[i]) == 0)
                {
                    throw new AssertionError(this.ecLongKeysForMap[i] + " not in map");
                }
            }
            if (this.longLongEc.size() != KEY_COUNT)
            {
                throw new AssertionError("size is " + this.longLongEc.size());
            }
        }
    }

    @Benchmark
    public void kolobokePut()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            LongLongMap newMap = HashLongLongMaps.newMutableMap(MAP_SIZE);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.put(this.kolobokeLongKeysForMap[i], 4);
            }
            if (newMap.size() != KEY_COUNT)
            {
                throw new AssertionError("size is " + newMap.size());
            }
        }
    }

    @Benchmark
    public void ecPut()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            MutableLongLongMap newMap = new LongLongHashMap(MAP_SIZE);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.put(this.ecLongKeysForMap[i], 4);
            }
            if (newMap.size() != KEY_COUNT)
            {
                throw new AssertionError("size is " + newMap.size());
            }
        }
    }

    @Benchmark
    public void ecRemove()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            MutableLongLongMap newMap = new LongLongHashMap(this.longLongEc);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.remove(this.ecLongKeysForMap[i]);
            }
            if (newMap.notEmpty())
            {
                throw new AssertionError("size is " + newMap.size());
            }
        }
    }

    @Benchmark
    public void kolobokeRemove()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            LongLongMap newMap = HashLongLongMaps.newMutableMap(this.longLongKoloboke);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.remove(this.kolobokeLongKeysForMap[i]);
            }
            if (!newMap.isEmpty())
            {
                throw new AssertionError("size is " + newMap.size());
            }
        }
    }

    public void shuffle(long[] array, Random rnd)
    {
        for (int i = array.length; i > 1; i--)
        {
            LongLongMapLargeStressTest.swap(array, i - 1, rnd.nextInt(i));
        }
    }

    private static void swap(long[] arr, int i, int j)
    {
        long tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
