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
import org.eclipse.collections.api.block.function.primitive.BooleanToObjectFunction;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableObjectBooleanMap;
import org.eclipse.collections.api.map.primitive.ObjectBooleanMap;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.factory.primitive.ObjectBooleanMaps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectBooleanHashMapWithHashingStrategyTest extends ObjectBooleanHashMapTestCase
{
    private static final HashingStrategy<String> STRING_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<String>()
    {
        @Override
        public int computeHashCode(String object)
        {
            return object.hashCode();
        }

        @Override
        public boolean equals(String object1, String object2)
        {
            return object1.equals(object2);
        }
    });

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

    private static final HashingStrategy<Person> FIRST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_FIRST);
    private static final HashingStrategy<Person> LAST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);
    private static final HashingStrategy<Person> CONSTANT_HASHCODE_STRATEGY = new HashingStrategy<Person>()
    {
        @Override
        public int computeHashCode(Person object)
        {
            return 0;
        }

        @Override
        public boolean equals(Person person1, Person person2)
        {
            return person1.getLastName().equals(person2.getLastName());
        }
    };

    private static final Person JOHNSMITH = new Person("John", "Smith");
    private static final Person JANESMITH = new Person("Jane", "Smith");
    private static final Person JOHNDOE = new Person("John", "Doe");
    private static final Person JANEDOE = new Person("Jane", "Doe");

    @Override
    protected ObjectBooleanHashMapWithHashingStrategy<String> classUnderTest()
    {
        return ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(STRING_HASHING_STRATEGY, "0", true, "1", true, "2", false);
    }

    @Override
    protected <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1)
    {
        return ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1);
    }

    @Override
    protected <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2)
    {
        return ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1, key2, value2);
    }

    @Override
    protected <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3)
    {
        return ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1, key2, value2, key3, value3);
    }

    @Override
    protected <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3, T key4, boolean value4)
    {
        return ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), key1, value1, key2, value2, key3, value3, key4, value4);
    }

    @Override
    protected <T> MutableObjectBooleanMap<T> getEmptyMap()
    {
        return new ObjectBooleanHashMapWithHashingStrategy<>(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()));
    }

    @Override
    protected <T> MutableObjectBooleanMap<T> newMapWithInitialCapacity(int size)
    {
        return new ObjectBooleanHashMapWithHashingStrategy<>(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), size);
    }

    @Override
    protected Class<?> getTargetClass()
    {
        return ObjectBooleanHashMapWithHashingStrategy.class;
    }

    @Override
    @Test
    public void select()
    {
        super.select();

        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, true, JANEDOE, false, JOHNSMITH, true, JANESMITH, false);
        assertEquals(ObjectBooleanHashMap.newWithKeysValues(JOHNDOE, false), map.select((argument1, argument2) -> "Doe".equals(argument1.getLastName())));
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();

        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, true, JANEDOE, false, JOHNSMITH, true, JANESMITH, false);
        assertEquals(ObjectBooleanHashMap.newWithKeysValues(JOHNDOE, false), map.reject((argument1, argument2) -> "Smith".equals(argument1.getLastName())));
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();

        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, true, JANEDOE, false, JOHNSMITH, true, JANESMITH, false);
        BooleanToObjectFunction<Boolean> f = argument1 -> argument1;
        assertEquals(FastList.newListWith(false, false), map.collect(f));
    }

    @Test
    public void contains_with_hashing_strategy()
    {
        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, true, JANEDOE, false, JOHNSMITH, true, JANESMITH, false);
        assertTrue(map.containsKey(JOHNDOE));
        assertTrue(map.containsKey(JOHNSMITH));
        assertTrue(map.containsKey(JANEDOE));
        assertTrue(map.containsKey(JANESMITH));
        assertTrue(map.containsValue(false));
        assertFalse(map.containsValue(true));
    }

    @Test
    public void remove_with_hashing_strategy()
    {
        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(
                LAST_NAME_HASHING_STRATEGY, JOHNDOE, true, JANEDOE, false, JOHNSMITH, true, JANESMITH, false);

        map.remove(JANEDOE);
        assertEquals(ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(LAST_NAME_HASHING_STRATEGY, JOHNSMITH, false), map);
        map.remove(JOHNSMITH);

        Verify.assertEmpty(map);

        MutableList<String> collidingKeys = generateCollisions();
        ObjectBooleanHashMapWithHashingStrategy<String> map2 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(
                STRING_HASHING_STRATEGY, collidingKeys.get(0), true, collidingKeys.get(1), false, collidingKeys.get(2), true, collidingKeys.get(3), false);
        map2.remove(collidingKeys.get(3));
        assertEquals(ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(STRING_HASHING_STRATEGY, collidingKeys.get(0), true, collidingKeys.get(1), false, collidingKeys.get(2), true), map2);
        map2.remove(collidingKeys.get(0));
        assertEquals(ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(STRING_HASHING_STRATEGY, collidingKeys.get(1), false, collidingKeys.get(2), true), map2);
        Verify.assertSize(2, map2);

        ObjectBooleanHashMapWithHashingStrategy<Integer> map3 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(INTEGER_HASHING_STRATEGY, 1, true, null, false, 3, true);
        map3.remove(null);
        assertEquals(ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(INTEGER_HASHING_STRATEGY, 1, true, 3, true), map3);
    }

    @Test
    public void equals_with_hashing_strategy()
    {
        ObjectBooleanHashMapWithHashingStrategy<Person> map1 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(LAST_NAME_HASHING_STRATEGY, JOHNDOE, true, JANEDOE, true, JOHNSMITH, true, JANESMITH, true);
        ObjectBooleanHashMapWithHashingStrategy<Person> map2 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(FIRST_NAME_HASHING_STRATEGY, JOHNDOE, true, JANEDOE, true, JOHNSMITH, true, JANESMITH, true);
        ObjectBooleanHashMapWithHashingStrategy<Person> mapWithConstantHashCodeStrategy = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(CONSTANT_HASHCODE_STRATEGY, JOHNDOE, true, JANEDOE, true, JOHNSMITH, true, JANESMITH, true);

        assertEquals(map1, map2);
        assertEquals(map2, map1);
        assertEquals(mapWithConstantHashCodeStrategy, map2);
        assertEquals(map2, mapWithConstantHashCodeStrategy);
        assertNotEquals(map1.hashCode(), map2.hashCode());
        assertNotEquals(map1.hashCode(), mapWithConstantHashCodeStrategy.hashCode());
        assertNotEquals(map2.hashCode(), mapWithConstantHashCodeStrategy.hashCode());

        ObjectBooleanHashMapWithHashingStrategy<Person> map3 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(LAST_NAME_HASHING_STRATEGY, JOHNDOE, true, JANEDOE, false, JOHNSMITH, true, JANESMITH, false);

        ObjectBooleanHashMapWithHashingStrategy<Person> map4 = ObjectBooleanHashMapWithHashingStrategy.newMap(map3);
        ObjectBooleanMap<Person> hashMap = ObjectBooleanMaps.mutable.withAll(map3);

        Verify.assertEqualsAndHashCode(map3, map4);
        assertTrue(map3.equals(hashMap) && hashMap.equals(map3) && map3.hashCode() != hashMap.hashCode());

        ObjectBooleanHashMap<Person> objectMap = ObjectBooleanHashMap.newWithKeysValues(JOHNDOE, true, JANEDOE, false, JOHNSMITH, true, JANESMITH, false);
        ObjectBooleanHashMapWithHashingStrategy<Person> map5 = ObjectBooleanHashMapWithHashingStrategy.newMap(LAST_NAME_HASHING_STRATEGY, objectMap);
        assertNotEquals(map5, objectMap);
    }

    @Test
    public void put_get_with_hashing_strategy()
    {
        ObjectBooleanHashMapWithHashingStrategy<String> map = this.classUnderTest();
        map.put(null, true);

        //Testing getting values from no chains
        assertEquals(true, map.get("1"));
        assertEquals(false, map.get("2"));
        assertEquals(true, map.get(null));

        ObjectBooleanHashMapWithHashingStrategy<Person> map2 = ObjectBooleanHashMapWithHashingStrategy.newMap(LAST_NAME_HASHING_STRATEGY);
        map2.put(JOHNSMITH, true);
        assertEquals(true, map2.get(JOHNSMITH));
        map2.put(JANESMITH, false);
        assertEquals(false, map2.get(JOHNSMITH));
    }
}
