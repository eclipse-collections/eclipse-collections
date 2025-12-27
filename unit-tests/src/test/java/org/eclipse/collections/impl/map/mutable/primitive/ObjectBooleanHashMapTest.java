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

public class ObjectBooleanHashMapTest extends ObjectBooleanHashMapTestCase
{
    @Override
    protected ObjectBooleanHashMap<String> classUnderTest()
    {
        return ObjectBooleanHashMap.newWithKeysValues("0", true, "1", true, "2", false);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> newWithKeysValues(T key1, boolean value1)
    {
        return ObjectBooleanHashMap.newWithKeysValues(key1, value1);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2)
    {
        return ObjectBooleanHashMap.newWithKeysValues(key1, value1, key2, value2);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3)
    {
        return ObjectBooleanHashMap.newWithKeysValues(key1, value1, key2, value2, key3, value3);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3, T key4, boolean value4)
    {
        return ObjectBooleanHashMap.newWithKeysValues(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> getEmptyMap()
    {
        return new ObjectBooleanHashMap<>();
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> newMapWithInitialCapacity(int size)
    {
        return new ObjectBooleanHashMap<>(size);
    }

    @Override
    protected Class<?> getTargetClass()
    {
        return ObjectBooleanHashMap.class;
    }
}
