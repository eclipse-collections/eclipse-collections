/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable.primitive;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.list.mutable.FastList;

public class ObjectBooleanHashMapWithHashingStrategyValuesTest extends ObjectBooleanHashMapValuesTestCase
{
    private static final HashingStrategy<Integer> INT_MOD_10_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
    {
        @Override
        public int computeHashCode(Integer object)
        {
            return object.intValue() % 10;
        }

        @Override
        public boolean equals(Integer object1, Integer object2)
        {
            return this.computeHashCode(object1) == this.computeHashCode(object2);
        }
    });

    @Override
    protected MutableBooleanCollection classUnderTest()
    {
        return ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(INT_MOD_10_STRATEGY, 1, true, 2, false, 3, true).values();
    }

    @Override
    protected MutableBooleanCollection newWith(boolean... elements)
    {
        ObjectBooleanHashMapWithHashingStrategy<Integer> map = new ObjectBooleanHashMapWithHashingStrategy<>(INT_MOD_10_STRATEGY);
        for (int i = 0; i < elements.length; i++)
        {
            map.put(i, elements[i]);
        }
        return map.values();
    }

    @Override
    protected MutableBooleanCollection newMutableCollectionWith(boolean... elements)
    {
        return this.newWith(elements);
    }

    @Override
    protected MutableList<Object> newObjectCollectionWith(Object... elements)
    {
        return FastList.newListWith(elements);
    }
}
