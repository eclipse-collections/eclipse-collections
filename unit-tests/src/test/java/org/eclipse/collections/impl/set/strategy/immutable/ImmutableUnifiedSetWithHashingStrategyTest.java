/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.strategy.immutable;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.math.Sum;
import org.eclipse.collections.impl.math.SumProcedure;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.set.immutable.AbstractImmutableUnifiedSetTestCase;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link ImmutableUnifiedSetWithHashingStrategy}.
 */
public class ImmutableUnifiedSetWithHashingStrategyTest extends AbstractImmutableUnifiedSetTestCase
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
    public ImmutableSet<Integer> newSet(Integer... elements)
    {
        return ImmutableUnifiedSetWithHashingStrategy.newSetWith(HASHING_STRATEGY, elements);
    }

    @Override
    public ImmutableSet<Integer> newSetWith(int one, int two)
    {
        return ImmutableUnifiedSetWithHashingStrategy.newSetWith(HASHING_STRATEGY, one, two);
    }

    @Override
    public ImmutableSet<Integer> newSetWith(int one, int two, int three)
    {
        return ImmutableUnifiedSetWithHashingStrategy.newSetWith(HASHING_STRATEGY, one, two, three);
    }

    @Override
    public ImmutableSet<Integer> newSetWith(int... littleElements)
    {
        Integer[] bigElements = new Integer[littleElements.length];
        for (int i = 0; i < littleElements.length; i++)
        {
            bigElements[i] = littleElements[i];
        }
        return ImmutableUnifiedSetWithHashingStrategy.newSetWith(HASHING_STRATEGY, bigElements);
    }

    @Override
    @Test
    public void newCollection()
    {
        super.newCollection();
        ImmutableSet<Integer> set = ImmutableUnifiedSetWithHashingStrategy.newSet(HASHING_STRATEGY, UnifiedSet.newSet());
        assertTrue(set.isEmpty());
        Verify.assertSize(0, set);
    }

    @Test
    public void getBatchCount()
    {
        BatchIterable<Integer> integerBatchIterable = (BatchIterable<Integer>) this.newSet(1, 2, 3, 4, 5, 6);
        assertEquals(2, integerBatchIterable.getBatchCount(3));
    }

    @Test
    public void batchForEach()
    {
        Sum sum = new IntegerSum(0);
        BatchIterable<Integer> integerBatchIterable = (BatchIterable<Integer>) this.newSet(1, 2, 3, 4, 5);
        integerBatchIterable.batchForEach(new SumProcedure<>(sum), 0, 1);
        assertEquals(15, sum.getValue());
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        ImmutableSet<Integer> deserialized = SerializeTestHelper.serializeDeserialize(this.newSet(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(ImmutableUnifiedSetWithHashingStrategy.class, deserialized);
    }
}
