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
import org.eclipse.collections.api.map.primitive.ObjectBooleanMap;
import org.eclipse.collections.impl.block.factory.HashingStrategies;

/**
 * JUnit test for {@link ObjectBooleanHashMapWithHashingStrategy#keyValuesView()}.
 */
public class ObjectBooleanHashMapWithHashingStrategyKeyValuesViewTest extends AbstractObjectBooleanMapKeyValuesViewTestCase
{
    private static final HashingStrategy<Integer> INTEGER_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
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
    public <T> ObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3)
    {
        return ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1, key2, value2, key3, value3);
    }

    @Override
    public <T> ObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2)
    {
        return ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1, key2, value2);
    }

    @Override
    public <T> ObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1)
    {
        return ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1);
    }

    @Override
    public ObjectBooleanMap<Integer> newEmpty()
    {
        return ObjectBooleanHashMapWithHashingStrategy.newMap(INTEGER_HASHING_STRATEGY);
    }
}
