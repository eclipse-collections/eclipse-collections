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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import org.eclipse.collections.api.block.function.primitive.IntToObjectFunction;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.SpreadFunctions;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;
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
public class IntIntMapSmallStressTest
{
    private static final int LOOP_COUNT = 100;
    private static final int KEY_COUNT = 500;
    private static final int MAP_SIZE = 1_000;

    @Param({"true", "false"})
    public boolean fullyRandom;
    private IntIntMap intIntKoloboke;
    private MutableIntIntMap intIntEc;
    private Map<Integer, Integer> integerIntegerJdk;
    private int[] ecIntKeysForMap;
    private int[] kolobokeIntKeysForMap;
    private Integer[] jdkIntKeysForMap;

    private int jdkIndex(int key)
    {
        return this.mask(key ^ (key >>> 16));
    }

    private int kolobokeIndex(int key)
    {
        int h = key * 0x9e3779b9;
        return this.mask(h ^ h >> 16);
    }

    private int ecIndex(int element)
    {
        return this.mask(element);
    }

    private int ecIndexTwo(int element)
    {
        return this.mask(SpreadFunctions.intSpreadTwo(element));
    }

    private int mask(int spread)
    {
        return spread & ((1 << 11) - 1);
    }

    @Setup
    public void setUp()
    {
        this.intIntKoloboke = HashIntIntMaps.newMutableMap(MAP_SIZE);
        this.intIntEc = new IntIntHashMap(MAP_SIZE);
        this.integerIntegerJdk = new HashMap<>(MAP_SIZE);

        Random random = new Random(0x123456789ABCDL);

        int[] randomNumbersForMap = this.getRandomKeys(random).toArray();

        int number = 23;
        int lower = Integer.MIN_VALUE;
        int upper = Integer.MAX_VALUE;
        this.kolobokeIntKeysForMap = this.fullyRandom
                ? randomNumbersForMap
                : this.getKolobokeArray(number, lower, upper, random);
        this.ecIntKeysForMap = this.fullyRandom
                ? randomNumbersForMap
                : this.getECArray(number, lower, upper, random);
        this.jdkIntKeysForMap = this.fullyRandom
                ? IntIntMapSmallStressTest.boxIntArray(randomNumbersForMap)
                : this.getJDKArray(lower, upper, random);

        for (int i = 0; i < KEY_COUNT; i++)
        {
            this.intIntKoloboke.put(this.kolobokeIntKeysForMap[i], 5);
            this.intIntEc.put(this.ecIntKeysForMap[i], 5);
            this.integerIntegerJdk.put(this.jdkIntKeysForMap[i], 5);
        }

        this.shuffle(this.ecIntKeysForMap, random);
        this.shuffle(this.kolobokeIntKeysForMap, random);
        this.shuffle(this.jdkIntKeysForMap, random);
    }

    protected int[] getECArray(int number, int lower, int upper, Random random)
    {
        int[] ecCollisions = this.getECSequenceCollisions(number, lower, upper).toArray();
        this.shuffle(ecCollisions, random);
        return ecCollisions;
    }

    protected MutableIntList getECSequenceCollisions(int number, int lower, int upper)
    {
        MutableIntList ecCollidingNumbers = new IntArrayList();
        for (int i = lower; i < upper && ecCollidingNumbers.size() < KEY_COUNT; i++)
        {
            if (this.ecIndex(i) - this.ecIndex(number) >= 0
                    && this.ecIndex(i) - this.ecIndex(number) < 10
                    && (this.ecIndexTwo(i) - this.ecIndexTwo(number) >= 0)
                    && (this.ecIndexTwo(i) - this.ecIndexTwo(number) < 10))
            {
                ecCollidingNumbers.add(i);
            }
        }
        return ecCollidingNumbers;
    }

    protected Integer[] getJDKArray(int lower, int upper, Random random)
    {
        MutableList<Integer> collisions = this.getJDKSequenceCollisions(lower, upper);
        Integer[] jdkCollision = collisions.toArray(new Integer[collisions.size()]);
        this.shuffle(jdkCollision, random);
        return jdkCollision;
    }

    protected MutableList<Integer> getJDKSequenceCollisions(int lower, int upper)
    {
        MutableList<Integer> jdkCollidingNumbers = FastList.newList();
        int slots = 1; // slots = KEY_COUNT / (1 << 32) / (1 << MAP_SIZE) + 1;
        MutableIntSet indices = new IntHashSet();
        for (int i = lower; i < upper && jdkCollidingNumbers.size() < KEY_COUNT; i++)
        {
            int index = this.jdkIndex(i);
            if (indices.size() < slots)
            {
                indices.add(index);
                jdkCollidingNumbers.add(i);
            }
            else if (indices.contains(index))
            {
                jdkCollidingNumbers.add(i);
            }
        }
        return jdkCollidingNumbers;
    }

    protected int[] getKolobokeArray(int number, int lower, int upper, Random random)
    {
        int[] kolobokeCollisions = this.getKolobokeSequenceCollisions(number, lower, upper).toArray();
        this.shuffle(kolobokeCollisions, random);
        return kolobokeCollisions;
    }

    protected MutableIntList getKolobokeSequenceCollisions(int number, int lower, int upper)
    {
        MutableIntList kolobokeCollidingNumbers = new IntArrayList();
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

    protected MutableIntSet getRandomKeys(Random random)
    {
        MutableIntSet set = new IntHashSet(KEY_COUNT);
        while (set.size() < KEY_COUNT)
        {
            set.add(random.nextInt());
        }

        return set;
    }

    @Benchmark
    public void jdkGet()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            for (int i = 0; i < KEY_COUNT; i++)
            {
                if (this.integerIntegerJdk.get(this.jdkIntKeysForMap[i]) == null)
                {
                    throw new AssertionError(this.jdkIntKeysForMap[i] + " not in map");
                }
            }
            if (this.integerIntegerJdk.size() != KEY_COUNT)
            {
                throw new AssertionError("size is " + this.integerIntegerJdk.size());
            }
        }
    }

    @Benchmark
    public void kolobokeGet()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            for (int i = 0; i < KEY_COUNT; i++)
            {
                if (this.intIntKoloboke.get(this.kolobokeIntKeysForMap[i]) == this.intIntKoloboke.defaultValue())
                {
                    throw new AssertionError(this.kolobokeIntKeysForMap[i] + " not in map");
                }
            }
            if (this.intIntKoloboke.size() != KEY_COUNT)
            {
                throw new AssertionError("size is " + this.intIntKoloboke.size());
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
                if (this.intIntEc.get(this.ecIntKeysForMap[i]) == 0)
                {
                    throw new AssertionError(this.ecIntKeysForMap[i] + " not in map");
                }
            }
            if (this.intIntEc.size() != KEY_COUNT)
            {
                throw new AssertionError("size is " + this.intIntEc.size());
            }
        }
    }

    @Benchmark
    public void jdkPut()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            Map<Integer, Integer> newMap = new HashMap<>(MAP_SIZE);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.put(this.jdkIntKeysForMap[i], 4);
            }
            if (newMap.size() != KEY_COUNT)
            {
                throw new AssertionError("size is " + newMap.size());
            }
        }
    }

    @Benchmark
    public void kolobokePut()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            IntIntMap newMap = HashIntIntMaps.newMutableMap(MAP_SIZE);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.put(this.kolobokeIntKeysForMap[i], 4);
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
            MutableIntIntMap newMap = new IntIntHashMap(MAP_SIZE);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.put(this.ecIntKeysForMap[i], 4);
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
            MutableIntIntMap newMap = new IntIntHashMap(this.intIntEc);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.remove(this.ecIntKeysForMap[i]);
            }
            if (newMap.notEmpty())
            {
                throw new AssertionError("size is " + newMap.size());
            }
        }
    }

    @Benchmark
    public void jdkRemove()
    {
        for (int j = 0; j < LOOP_COUNT; j++)
        {
            Map<Integer, Integer> newMap = new HashMap<>(this.integerIntegerJdk);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.remove(this.jdkIntKeysForMap[i]);
            }
            if (!newMap.isEmpty())
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
            IntIntMap newMap = HashIntIntMaps.newMutableMap(this.intIntKoloboke);
            for (int i = 0; i < KEY_COUNT; i++)
            {
                newMap.remove(this.kolobokeIntKeysForMap[i]);
            }
            if (!newMap.isEmpty())
            {
                throw new AssertionError("size is " + newMap.size());
            }
        }
    }

    public void shuffle(int[] intArray, Random rnd)
    {
        for (int i = intArray.length; i > 1; i--)
        {
            IntIntMapSmallStressTest.swap(intArray, i - 1, rnd.nextInt(i));
        }
    }

    public void shuffle(Integer[] integerArray, Random rnd)
    {
        for (int i = integerArray.length; i > 1; i--)
        {
            IntIntMapSmallStressTest.swap(integerArray, i - 1, rnd.nextInt(i));
        }
    }

    private static void swap(int[] arr, int i, int j)
    {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private static void swap(Integer[] arr, int i, int j)
    {
        Integer tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private static Integer[] boxIntArray(int[] arr)
    {
        MutableList<Integer> list = new IntArrayList(arr).collect((IntToObjectFunction<Integer>) Integer::valueOf);
        return list.toArray(new Integer[arr.length]);
    }
}
