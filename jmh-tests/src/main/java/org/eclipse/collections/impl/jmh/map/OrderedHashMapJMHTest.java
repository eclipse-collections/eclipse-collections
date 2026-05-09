/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedHashMap;
import org.eclipse.collections.impl.map.ordered.mutable.OrderedMapAdapter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
public class OrderedHashMapJMHTest
{
    private static final long RANDOM_SEED = 123456789L;
    private static final Integer FIRST_KEY = 0;
    private static final Integer SECOND_KEY = 1;

    @State(Scope.Thread)
    public static class MapState
    {
        @Param({"1000", "10000", "100000"})
        public int size;

        private Integer[] keys;
        private OrderedHashMap<Integer, Integer> orderedHashMap;
        private LinkedHashMap<Integer, Integer> linkedHashMap;
        private MutableOrderedMap<Integer, Integer> orderedMapAdapter;
        private UnifiedMap<Integer, Integer> unifiedMap;

        @Setup
        public void setUp()
        {
            this.keys = shuffledKeys(this.size);
            this.orderedHashMap = newOrderedHashMap(this.keys);
            this.linkedHashMap = newLinkedHashMap(this.keys);
            this.orderedMapAdapter = newOrderedMapAdapter(this.keys);
            this.unifiedMap = newUnifiedMap(this.keys);
        }
    }

    @State(Scope.Thread)
    public static class RemovePutChurnState
    {
        @Param({"1000", "10000", "100000"})
        public int churnCount;

        private OrderedHashMap<Integer, Integer> orderedHashMap;
        private LinkedHashMap<Integer, Integer> linkedHashMap;
        private MutableOrderedMap<Integer, Integer> orderedMapAdapter;
        private UnifiedMap<Integer, Integer> unifiedMap;

        @Setup(Level.Invocation)
        public void setUp()
        {
            this.orderedHashMap = new OrderedHashMap<>(2);
            this.orderedHashMap.put(FIRST_KEY, FIRST_KEY);
            this.orderedHashMap.put(SECOND_KEY, SECOND_KEY);

            this.linkedHashMap = new LinkedHashMap<>(2);
            this.linkedHashMap.put(FIRST_KEY, FIRST_KEY);
            this.linkedHashMap.put(SECOND_KEY, SECOND_KEY);

            this.orderedMapAdapter = OrderedMapAdapter.adapt(new LinkedHashMap<>(2));
            this.orderedMapAdapter.put(FIRST_KEY, FIRST_KEY);
            this.orderedMapAdapter.put(SECOND_KEY, SECOND_KEY);

            this.unifiedMap = UnifiedMap.newMap(2);
            this.unifiedMap.put(FIRST_KEY, FIRST_KEY);
            this.unifiedMap.put(SECOND_KEY, SECOND_KEY);
        }
    }

    @Benchmark
    public OrderedHashMap<Integer, Integer> orderedHashMapPut(MapState state)
    {
        return newOrderedHashMap(state.keys);
    }

    @Benchmark
    public LinkedHashMap<Integer, Integer> linkedHashMapPut(MapState state)
    {
        return newLinkedHashMap(state.keys);
    }

    @Benchmark
    public MutableOrderedMap<Integer, Integer> orderedMapAdapterPut(MapState state)
    {
        return newOrderedMapAdapter(state.keys);
    }

    @Benchmark
    public UnifiedMap<Integer, Integer> unifiedMapPut(MapState state)
    {
        return newUnifiedMap(state.keys);
    }

    @Benchmark
    public long orderedHashMapGet(MapState state)
    {
        OrderedHashMap<Integer, Integer> localMap = state.orderedHashMap;
        Integer[] localKeys = state.keys;
        long result = 0L;
        for (Integer key : localKeys)
        {
            Integer value = localMap.get(key);
            if (value == null)
            {
                throw new AssertionError(key);
            }
            result += value;
        }
        return result;
    }

    @Benchmark
    public long linkedHashMapGet(MapState state)
    {
        LinkedHashMap<Integer, Integer> localMap = state.linkedHashMap;
        Integer[] localKeys = state.keys;
        long result = 0L;
        for (Integer key : localKeys)
        {
            Integer value = localMap.get(key);
            if (value == null)
            {
                throw new AssertionError(key);
            }
            result += value;
        }
        return result;
    }

    @Benchmark
    public long orderedMapAdapterGet(MapState state)
    {
        MutableOrderedMap<Integer, Integer> localMap = state.orderedMapAdapter;
        Integer[] localKeys = state.keys;
        long result = 0L;
        for (Integer key : localKeys)
        {
            Integer value = localMap.get(key);
            if (value == null)
            {
                throw new AssertionError(key);
            }
            result += value;
        }
        return result;
    }

    @Benchmark
    public long unifiedMapGet(MapState state)
    {
        UnifiedMap<Integer, Integer> localMap = state.unifiedMap;
        Integer[] localKeys = state.keys;
        long result = 0L;
        for (Integer key : localKeys)
        {
            Integer value = localMap.get(key);
            if (value == null)
            {
                throw new AssertionError(key);
            }
            result += value;
        }
        return result;
    }

    @Benchmark
    public long orderedHashMapEntryIteration(MapState state)
    {
        long result = 0L;
        for (Map.Entry<Integer, Integer> entry : state.orderedHashMap.entrySet())
        {
            result += entry.getValue();
        }
        return result;
    }

    @Benchmark
    public long linkedHashMapEntryIteration(MapState state)
    {
        long result = 0L;
        for (Map.Entry<Integer, Integer> entry : state.linkedHashMap.entrySet())
        {
            result += entry.getValue();
        }
        return result;
    }

    @Benchmark
    public long orderedMapAdapterEntryIteration(MapState state)
    {
        long result = 0L;
        for (Map.Entry<Integer, Integer> entry : state.orderedMapAdapter.entrySet())
        {
            result += entry.getValue();
        }
        return result;
    }

    @Benchmark
    public long unifiedMapEntryIteration(MapState state)
    {
        long result = 0L;
        for (Map.Entry<Integer, Integer> entry : state.unifiedMap.entrySet())
        {
            result += entry.getValue();
        }
        return result;
    }

    @Benchmark
    public OrderedHashMap<Integer, Integer> orderedHashMapRemovePutChurn(RemovePutChurnState state)
    {
        OrderedHashMap<Integer, Integer> localMap = state.orderedHashMap;
        for (int i = 0; i < state.churnCount; i++)
        {
            localMap.removeKey(FIRST_KEY);
            localMap.put(FIRST_KEY, FIRST_KEY);
            localMap.removeKey(SECOND_KEY);
            localMap.put(SECOND_KEY, SECOND_KEY);
        }
        return localMap;
    }

    @Benchmark
    public LinkedHashMap<Integer, Integer> linkedHashMapRemovePutChurn(RemovePutChurnState state)
    {
        LinkedHashMap<Integer, Integer> localMap = state.linkedHashMap;
        for (int i = 0; i < state.churnCount; i++)
        {
            localMap.remove(FIRST_KEY);
            localMap.put(FIRST_KEY, FIRST_KEY);
            localMap.remove(SECOND_KEY);
            localMap.put(SECOND_KEY, SECOND_KEY);
        }
        return localMap;
    }

    @Benchmark
    public MutableOrderedMap<Integer, Integer> orderedMapAdapterRemovePutChurn(RemovePutChurnState state)
    {
        MutableOrderedMap<Integer, Integer> localMap = state.orderedMapAdapter;
        for (int i = 0; i < state.churnCount; i++)
        {
            localMap.removeKey(FIRST_KEY);
            localMap.put(FIRST_KEY, FIRST_KEY);
            localMap.removeKey(SECOND_KEY);
            localMap.put(SECOND_KEY, SECOND_KEY);
        }
        return localMap;
    }

    @Benchmark
    public UnifiedMap<Integer, Integer> unifiedMapRemovePutChurn(RemovePutChurnState state)
    {
        UnifiedMap<Integer, Integer> localMap = state.unifiedMap;
        for (int i = 0; i < state.churnCount; i++)
        {
            localMap.removeKey(FIRST_KEY);
            localMap.put(FIRST_KEY, FIRST_KEY);
            localMap.removeKey(SECOND_KEY);
            localMap.put(SECOND_KEY, SECOND_KEY);
        }
        return localMap;
    }

    private static OrderedHashMap<Integer, Integer> newOrderedHashMap(Integer[] keys)
    {
        OrderedHashMap<Integer, Integer> map = new OrderedHashMap<>(keys.length);
        for (Integer key : keys)
        {
            map.put(key, key);
        }
        return map;
    }

    private static LinkedHashMap<Integer, Integer> newLinkedHashMap(Integer[] keys)
    {
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>(keys.length);
        for (Integer key : keys)
        {
            map.put(key, key);
        }
        return map;
    }

    private static MutableOrderedMap<Integer, Integer> newOrderedMapAdapter(Integer[] keys)
    {
        MutableOrderedMap<Integer, Integer> map = OrderedMapAdapter.adapt(new LinkedHashMap<>(keys.length));
        for (Integer key : keys)
        {
            map.put(key, key);
        }
        return map;
    }

    private static UnifiedMap<Integer, Integer> newUnifiedMap(Integer[] keys)
    {
        UnifiedMap<Integer, Integer> map = UnifiedMap.newMap(keys.length);
        for (Integer key : keys)
        {
            map.put(key, key);
        }
        return map;
    }

    private static Integer[] shuffledKeys(int size)
    {
        Integer[] keys = new Integer[size];
        for (int i = 0; i < size; i++)
        {
            keys[i] = i;
        }
        Collections.shuffle(Arrays.asList(keys), new Random(RANDOM_SEED));
        return keys;
    }
}
