/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.strategy.immutable;

import java.util.NoSuchElementException;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.map.immutable.ImmutableMemoryEfficientMapTestCase;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link ImmutableEmptyMapWithHashingStrategy}.
 */
public class ImmutableEmptyMapWithHashingStrategyTest extends ImmutableMemoryEfficientMapTestCase
{
    //Not using the static factor method in order to have concrete types for test cases
    private static final HashingStrategy<Integer> HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
    {
        @Override
        public int computeHashCode(Integer object)
        {
            return object.hashCode();
        }

        @Override
        public boolean equals(Integer object1, Integer object2)
        {
            return object1.equals(object2);
        }
    });

    @Override
    protected ImmutableMap<Integer, String> classUnderTest()
    {
        return new ImmutableEmptyMapWithHashingStrategy<>(HASHING_STRATEGY);
    }

    @Override
    protected int size()
    {
        return 0;
    }

    @Override
    @Test
    public void testToString()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();
        assertEquals("{}", map.toString());
    }

    @Override
    @Test
    public void flipUniqueValues()
    {
        Verify.assertEmpty(this.classUnderTest().flipUniqueValues());
    }

    @Override
    @Test
    public void get()
    {
        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();

        Integer absentKey = this.size() + 1;
        assertNull(classUnderTest.get(absentKey));

        String absentValue = String.valueOf(absentKey);
        assertFalse(classUnderTest.containsValue(absentValue));

        // Still unchanged
        assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsent_function()
    {
        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        assertEquals(absentValue, classUnderTest.getIfAbsent(absentKey, new PassThruFunction0<>(absentValue)));

        // Still unchanged
        assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getOrDefault()
    {
        super.getOrDefault();

        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        assertEquals(absentValue, classUnderTest.getOrDefault(absentKey, absentValue));

        // Still unchanged
        assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        assertEquals(absentValue, classUnderTest.getIfAbsentValue(absentKey, absentValue));

        // Still unchanged
        assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsentWith()
    {
        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        assertEquals(absentValue, classUnderTest.getIfAbsentWith(absentKey, String::valueOf, absentValue));

        // Still unchanged
        assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void ifPresentApply()
    {
        Integer absentKey = this.size() + 1;

        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        assertNull(classUnderTest.ifPresentApply(absentKey, Functions.getPassThru()));
    }

    @Override
    @Test
    public void notEmpty()
    {
        assertFalse(this.classUnderTest().notEmpty());
    }

    @Override
    @Test
    public void allSatisfy()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        assertTrue(map.allSatisfy(String.class::isInstance));
        assertTrue(map.allSatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        assertTrue(map.noneSatisfy(Integer.class::isInstance));
        assertTrue(map.noneSatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        assertFalse(map.anySatisfy(String.class::isInstance));
        assertFalse(map.anySatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void max()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        assertThrows(NoSuchElementException.class, () -> map.max());
    }

    @Override
    @Test
    public void maxBy()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        assertThrows(NoSuchElementException.class, () -> map.maxBy(Functions.getStringPassThru()));
    }

    @Override
    @Test
    public void min()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        assertThrows(NoSuchElementException.class, () -> map.min());
    }

    @Override
    @Test
    public void minBy()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        assertThrows(NoSuchElementException.class, () -> map.minBy(Functions.getStringPassThru()));
    }

    @Test
    public void getOnly()
    {
        assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOnly());
    }

    @Override
    public void select()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();
        ImmutableMap<Integer, String> actual = map.select((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(ImmutableEmptyMapWithHashingStrategy.class, actual);
    }

    @Override
    public void reject()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();
        ImmutableMap<Integer, String> actual = map.reject((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(ImmutableEmptyMapWithHashingStrategy.class, actual);
    }

    @Override
    public void detect()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();
        assertNull(map.detect((ignored1, ignored2) -> true));
    }

    @Override
    protected <K, V> ImmutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return new ImmutableEmptyMapWithHashingStrategy<>(HashingStrategies.nullSafeHashingStrategy(
                HashingStrategies.defaultStrategy()));
    }
}
