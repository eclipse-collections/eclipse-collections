/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.Pool;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.math.IntegerSum;
import org.eclipse.collections.impl.math.Sum;
import org.eclipse.collections.impl.math.SumProcedure;
import org.eclipse.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.eclipse.collections.impl.test.domain.Person;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test suite for {@link UnifiedSetWithHashingStrategy}.
 */
public class UnifiedSetWithHashingStrategyTest extends AbstractUnifiedSetTestCase
{
    //Not using the static factory method in order to have concrete types for test cases
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
    private static final HashingStrategy<Person> FIRST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_FIRST);
    private static final HashingStrategy<Person> LAST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);

    private static final Person JOHNSMITH = new Person("John", "Smith");
    private static final Person JANESMITH = new Person("Jane", "Smith");
    private static final Person JOHNDOE = new Person("John", "Doe");
    private static final Person JANEDOE = new Person("Jane", "Doe");
    private static final ImmutableList<Person> PEOPLE = Lists.immutable.of(JOHNSMITH, JANESMITH, JOHNDOE, JANEDOE);
    private static final ImmutableSet<Person> LAST_NAME_HASHED_SET = Sets.immutable.of(JOHNSMITH, JOHNDOE);

    @Override
    protected <T> MutableSet<T> newWith(T... littleElements)
    {
        return UnifiedSetWithHashingStrategy.newSetWith(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.defaultStrategy()), littleElements);
    }

    @Test
    public void newSet_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> new UnifiedSetWithHashingStrategy<>(INTEGER_HASHING_STRATEGY, -1, 0.5f));
        assertThrows(IllegalArgumentException.class, () -> new UnifiedSetWithHashingStrategy<>(INTEGER_HASHING_STRATEGY, 1, -0.5f));
        assertThrows(IllegalArgumentException.class, () -> new UnifiedSetWithHashingStrategy<>(INTEGER_HASHING_STRATEGY, 1, 1.5f));
    }

    @Override
    @Test
    public void tap()
    {
        super.tap();

        MutableList<Person> tapResult = Lists.mutable.of();
        MutableSet<Person> people = UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).withAll(PEOPLE.castToList());
        assertSame(people, people.tap(tapResult::add));
        assertEquals(people.toList(), tapResult);
    }

    @Override
    @Test
    public void select()
    {
        super.select();

        MutableSet<Person> people = UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).withAll(PEOPLE.castToList());
        Verify.assertSetsEqual(LAST_NAME_HASHED_SET.castToSet(), people);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(JOHNSMITH), people.select(each -> "Smith".equals(each.getLastName())).with(JANESMITH));
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();

        MutableSet<Person> people = UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).withAll(PEOPLE.castToList());
        Verify.assertSetsEqual(UnifiedSet.newSetWith(JOHNSMITH), people.reject(each -> "Doe".equals(each.getLastName())).with(JANESMITH));
    }

    /**
     * @deprecated since 3.0.
     */
    @Deprecated
    @Test
    public void lazyCollectForEach()
    {
        UnifiedSetWithHashingStrategy<Integer> integers =
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3, 4, 5);
        LazyIterable<String> select = integers.lazyCollect(String::valueOf);
        Procedure<String> builder = Procedures.append(new StringBuilder());
        select.forEach(builder);
        String result = builder.toString();
        Verify.assertContains("1", result);
        Verify.assertContains("2", result);
        Verify.assertContains("3", result);
        Verify.assertContains("4", result);
        Verify.assertContains("5", result);
    }

    /**
     * @deprecated since 3.0.
     */
    @Deprecated
    @Test
    public void lazyRejectForEach()
    {
        UnifiedSetWithHashingStrategy<Integer> integers =
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3, 4, 5);
        LazyIterable<Integer> select = integers.lazyReject(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure<>(sum));
        assertEquals(5L, sum.getValue().intValue());
    }

    /**
     * @deprecated since 3.0.
     */
    @Deprecated
    @Test
    public void lazySelectForEach()
    {
        UnifiedSetWithHashingStrategy<Integer> integers =
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3, 4, 5);
        LazyIterable<Integer> select = integers.lazySelect(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure<>(sum));
        assertEquals(10, sum.getValue().intValue());
    }

    @Override
    @Test
    public void with()
    {
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(STRING_HASHING_STRATEGY, "1"),
                UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("1"));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(STRING_HASHING_STRATEGY, "1", "2"),
                UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("1", "2"));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(STRING_HASHING_STRATEGY, "1", "2", "3"),
                UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("1", "2", "3"));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(STRING_HASHING_STRATEGY, "1", "2", "3", "4"),
                UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("1", "2", "3", "4"));

        MutableSet<String> list = UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("A")
                .withAll(Lists.fixedSize.of("1", "2"))
                .withAll(Lists.fixedSize.of())
                .withAll(Sets.fixedSize.of("3", "4"));
        Verify.assertEqualsAndHashCode(UnifiedSetWithHashingStrategy.newSetWith(
                STRING_HASHING_STRATEGY, "A", "1", "2", "3", "4"), list);
    }

    @Test
    public void newSetWithIterable()
    {
        //testing collection
        MutableSet<Integer> integers = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, Interval.oneTo(3));
        assertEquals(UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3), integers);

        //testing iterable
        MutableSet<Integer> set1 = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, FastList.newListWith(1, 2, 3).asLazy());
        assertEquals(UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3), set1);

        //testing null
        assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, null));
    }

    @Override
    @Test
    public void add()
    {
        super.add();

        // force rehashing at each step of adding a new colliding entry
        for (int i = 0; i < COLLISIONS.size(); i++)
        {
            MutableSet<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(
                    INTEGER_HASHING_STRATEGY, i, 0.75f).withAll(COLLISIONS.subList(0, i));
            if (i == 2)
            {
                unifiedSet.add(Integer.valueOf(1));
            }
            if (i == 4)
            {
                unifiedSet.add(Integer.valueOf(1));
                unifiedSet.add(Integer.valueOf(2));
            }
            Integer value = COLLISIONS.get(i);
            assertTrue(unifiedSet.add(value));
        }

        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the trigging element goes in the bucket
        // set up a chained bucket
        MutableSet<Integer> caseA = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 2).with(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseA.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));

        // add the colliding value back and force the rehash
        assertTrue(caseA.add(COLLISION_2));

        // Rehashing Case B: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element is not in the chain
        // set up a chained bucket
        MutableSet<Integer> caseB = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 2).with(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseB.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseB.add(Integer.valueOf(1));
        caseB.add(Integer.valueOf(2));

        // add a new value and force the rehash
        assertTrue(caseB.add(3));
    }

    @Test
    public void add_with_hashingStrategy()
    {
        HashingStrategy<Integer> hashingStrategy = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
        {
            @Override
            public int computeHashCode(Integer object)
            {
                return object % 1000;
            }

            @Override
            public boolean equals(Integer object1, Integer object2)
            {
                return object1.equals(object2);
            }
        });

        //Same as case A above except with a different hashing strategy
        MutableSet<Integer> caseA = UnifiedSetWithHashingStrategy.newSet(hashingStrategy, 2);
        //Adding an element to a slot
        assertTrue(caseA.add(COLLISION_1));
        //Setting up a chained bucked by forcing a collision
        assertTrue(caseA.add(COLLISION_1 + 1000));
        //Increasing the occupied to the thresh hold
        assertTrue(caseA.add(COLLISION_1 + 2000));
        //Forcing a rehash where the element that forced the rehash goes in the chained bucket
        assertTrue(caseA.add(null));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(COLLISION_1, COLLISION_1 + 1000, COLLISION_1 + 2000, null), caseA);

        //Same as case B above except with a different hashing strategy
        MutableSet<Integer> caseB = UnifiedSetWithHashingStrategy.newSet(hashingStrategy, 2);
        //Adding an element to a slot
        assertTrue(caseB.add(null));
        //Setting up a chained bucked by forcing a collision
        assertTrue(caseB.add(1));
        //Increasing the occupied to the threshold
        assertTrue(caseB.add(2));
        //Forcing a rehash where the element that forced the rehash does not go in the chained bucket
        assertTrue(caseB.add(3));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(null, 1, 2, 3), caseB);

        //Testing add throws NullPointerException if the hashingStrategy is not null safe
        assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).add(null));
    }

    @Test
    public void addOrReplace()
    {
        Person person1 = new Person("f1", "l1", 1);
        UnifiedSetWithHashingStrategy<Person> set1 = UnifiedSetWithHashingStrategy.newSetWith(
                HashingStrategies.fromFunction(Person::getAge),
                person1);
        Person person2 = new Person("f2", "l2", 1);

        assertEquals(person1, set1.getOnly());
        set1.add(person2);
        assertEquals(person1, set1.getOnly());
        assertEquals(person1, set1.addOrReplace(person2));
        assertEquals(person2, set1.getOnly());
        set1.remove(person1);
        Verify.assertEmpty(set1);
        assertEquals(person1, set1.addOrReplace(person1));
        assertEquals(person1, set1.getOnly());
        assertEquals(person1, set1.addOrReplace(person1));

        Person person31 = new Person("c1", "l31", COLLISION_1);
        Person person41 = new Person("c2", "l41", COLLISION_2);
        Person person51 = new Person("c3", "l51", COLLISION_3);
        Person person61 = new Person("c4", "l61", COLLISION_4);
        Person person71 = new Person("c5", "l71", COLLISION_5);
        Person person81 = new Person("c6", "l81", COLLISION_6);
        Person person91 = new Person("c7", "l91", COLLISION_7);
        Person person101 = new Person("c8", "l101", COLLISION_8);
        Person person111 = new Person("c9", "l111", COLLISION_9);
        Person person121 = new Person("c10", "l121", COLLISION_10);

        Person person32 = new Person("c1", "l32", COLLISION_1);
        Person person42 = new Person("c2", "l42", COLLISION_2);
        Person person52 = new Person("c3", "l52", COLLISION_3);
        Person person62 = new Person("c4", "l62", COLLISION_4);
        Person person72 = new Person("c5", "l72", COLLISION_5);
        Person person82 = new Person("c6", "l82", COLLISION_6);
        Person person92 = new Person("c7", "l92", COLLISION_7);
        Person person102 = new Person("c8", "l102", COLLISION_8);
        Person person112 = new Person("c9", "l112", COLLISION_9);
        Person person122 = new Person("c10", "l122", COLLISION_10);

        UnifiedSetWithHashingStrategy<Person> set2 = UnifiedSetWithHashingStrategy.newSetWith(
                HashingStrategies.fromFunction(Person::getAge));
        assertEquals(person31, set2.addOrReplace(person31));
        assertEquals(person41, set2.addOrReplace(person41));
        assertEquals(person51, set2.addOrReplace(person51));
        assertEquals(person61, set2.addOrReplace(person61));
        assertEquals(person61, set2.addOrReplace(person62));
        assertEquals(person62, set2.addOrReplace(person61));
        assertEquals(person71, set2.addOrReplace(person71));
        assertEquals(person81, set2.addOrReplace(person81));
        assertEquals(person91, set2.addOrReplace(person91));
        assertEquals(person101, set2.addOrReplace(person101));
        assertEquals(person111, set2.addOrReplace(person111));
        assertEquals(person121, set2.addOrReplace(person121));
        assertEquals(person31, set2.addOrReplace(person31));

        assertEquals(UnifiedSetWithHashingStrategy.newSetWith(
                HashingStrategies.fromFunction(Person::getAge),
                person31, person41, person51, person61, person71, person81, person91, person101, person111, person121),
                set2);

        assertEquals(person121, set2.addOrReplace(person122));
        assertEquals(person71, set2.addOrReplace(person72));
        assertEquals(UnifiedSetWithHashingStrategy.newSetWith(
                HashingStrategies.fromFunction(Person::getAge),
                person31, person41, person51, person61, person72, person81, person91, person101, person111, person122),
                set2);
        assertEquals(person31, set2.addOrReplace(person32));
        assertEquals(person41, set2.addOrReplace(person42));
        assertEquals(person51, set2.addOrReplace(person52));
        assertEquals(person61, set2.addOrReplace(person62));
        assertEquals(person72, set2.addOrReplace(person72));
        assertEquals(person81, set2.addOrReplace(person82));
        assertEquals(person91, set2.addOrReplace(person92));
        assertEquals(person101, set2.addOrReplace(person102));
        assertEquals(person111, set2.addOrReplace(person112));
        assertEquals(person122, set2.addOrReplace(person122));
        assertEquals(UnifiedSetWithHashingStrategy.newSetWith(
                HashingStrategies.fromFunction(Person::getAge),
                person32, person42, person52, person62, person72, person82, person92, person102, person112, person122),
                set2);
        assertEquals(person1, set2.addOrReplace(person1));
        Person person3 = new Person("f3", "l3", 3);
        Person person4 = new Person("f4", "l4", 4);
        assertEquals(person3, set2.addOrReplace(person3));
        assertEquals(person4, set2.addOrReplace(person4));
        assertEquals(UnifiedSetWithHashingStrategy.newSetWith(
                HashingStrategies.fromFunction(Person::getAge),
                person1, person3, person4, person32, person42, person52, person62, person72, person82, person92, person102, person112, person122),
                set2);
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();

        // test adding a fully populated chained bucket
        MutableSet<Integer> expected = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6, COLLISION_7);
        assertTrue(UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY).addAllIterable(expected));

        // add an odd-sized collection to a set with a small max to ensure that its capacity is maintained after the operation.
        MutableSet<Integer> tiny = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, 0);
        assertTrue(tiny.addAllIterable(FastList.newListWith(COLLISION_1)));

        //Testing copying set with 3rd slot in chained bucket == null
        MutableSet<Integer> integers = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY);
        integers.remove(COLLISION_4);
        assertTrue(set.addAllIterable(integers));
        assertEquals(UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_3), set);

        //Testing copying set with 2nd slot in chained bucket == null
        integers.remove(COLLISION_3);
        assertFalse(set.addAllIterable(integers));

        //Testing copying set with the 1st slot in chained bucket == null
        integers.remove(COLLISION_2);
        assertFalse(set.addAllIterable(integers));

        assertEquals(UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_3), set);
    }

    @Test
    public void addALLIterable_with_hashingStrategy()
    {
        MutableSet<Person> people = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.nullSafeHashingStrategy(LAST_NAME_HASHING_STRATEGY), 2);
        //Testing adding an iterable
        assertTrue(people.addAllIterable(PEOPLE));
        Verify.assertSetsEqual(UnifiedSet.newSet(LAST_NAME_HASHED_SET), people);

        //Testing the set uses its own hashing strategy and not the target sets
        assertFalse(people.addAllIterable(UnifiedSetWithHashingStrategy.newSet(FIRST_NAME_HASHING_STRATEGY, PEOPLE)));
        Verify.assertSize(2, people);

        //Testing adding with null where the call to addALLIterable forces a rehash
        Person notInSet = new Person("Not", "InSet");
        assertTrue(people.addAllIterable(UnifiedSet.newSetWith(notInSet, null)));
        Verify.assertSetsEqual(UnifiedSet.newSet(LAST_NAME_HASHED_SET).with(notInSet, null), people);

        //Testing addAllIterable throws NullPointerException if the hashingStrategy is not null safe
        assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).addAllIterable(UnifiedSet.newSetWith((Person) null)));
    }

    @Test
    public void get()
    {
        MutableList<Holder<Integer>> holderCollisions = COLLISIONS.collect(Holder::new);
        UnifiedSetWithHashingStrategy<Holder<Integer>> set = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.nullSafeHashingStrategy(HashingStrategies.<Holder<Integer>>defaultStrategy()), SIZE).withAll(holderCollisions);
        set.removeAll(holderCollisions);
        for (Holder<Integer> holder : holderCollisions)
        {
            assertNull(set.get(holder));
            assertNull(set.get(null));
            set.add(holder);
            assertSame(holder, set.get(new Holder<>(holder.getValue())));
        }
        assertEquals(holderCollisions.toSet(), set);

        // the pool interface supports getting null keys
        UnifiedSetWithHashingStrategy<Integer> chainedWithNull = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, null, COLLISION_1);
        Verify.assertContains(null, chainedWithNull);
        assertNull(chainedWithNull.get(null));

        // getting a non-existent from a chain with one slot should short-circuit to return null
        UnifiedSetWithHashingStrategy<Integer> chainedWithOneSlot = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2);
        chainedWithOneSlot.remove(COLLISION_2);
        assertNull(chainedWithOneSlot.get(COLLISION_2));
    }

    @Test
    public void get_with_hashingStrategy()
    {
        UnifiedSetWithHashingStrategy<Person> people = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.nullSafeHashingStrategy(LAST_NAME_HASHING_STRATEGY), 2).withAll(PEOPLE.castToList());

        //Putting null then testing geting a null
        Verify.assertSize(3, people.with((Person) null));
        assertNull(people.get(null));

        //Testing it is getting the same reference
        assertSame(JOHNSMITH, people.get(JANESMITH));
        assertSame(JOHNSMITH, people.get(JOHNSMITH));
        assertSame(JOHNDOE, people.get(JANEDOE));
        assertSame(JOHNDOE, people.get(JOHNDOE));

        assertSame(JOHNSMITH, people.get(new Person("Anything", "Smith")));
        assertNull(people.get(new Person("John", "NotHere")));

        //Testing get throws NullPointerException if the hashingStrategy is not null safe
        assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).get(null));
    }

    @Test
    public void put()
    {
        MutableList<Holder<Integer>> holderCollisions = MORE_COLLISIONS.collect(Holder::new);
        int size = holderCollisions.size();
        for (int i = 1; i <= size; i++)
        {
            Pool<Holder<Integer>> unifiedSet = UnifiedSetWithHashingStrategy.newSet(
                    HashingStrategies.nullSafeHashingStrategy(HashingStrategies.<Holder<Integer>>defaultStrategy()), 1).withAll(holderCollisions.subList(0, i - 1));
            Holder<Integer> newValue = holderCollisions.get(i - 1);

            assertSame(newValue, unifiedSet.put(newValue));
            assertSame(newValue, unifiedSet.put(new Holder<>(newValue.getValue())));
        }

        // assert that all redundant puts into each position of chain bucket return the original element added
        Pool<Integer> set = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 4).with(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        for (int i = 0; i < set.size(); i++)
        {
            Integer value = COLLISIONS.get(i);
            assertSame(value, set.put(value));
        }

        // force rehashing at each step of putting a new colliding entry
        for (int i = 0; i < COLLISIONS.size(); i++)
        {
            Pool<Integer> pool = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, i).withAll(COLLISIONS.subList(0, i));
            if (i == 2)
            {
                pool.put(Integer.valueOf(1));
            }
            if (i == 4)
            {
                pool.put(Integer.valueOf(1));
                pool.put(Integer.valueOf(2));
            }
            Integer value = COLLISIONS.get(i);
            assertSame(value, pool.put(value));
        }

        // cover one case not covered in the above: a bucket with only one entry and a low capacity forcing a rehash
        // set up a chained bucket
        Pool<Integer> pool = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, 2).with(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        pool.removeFromPool(COLLISION_2);
        // increase the occupied count to the threshold
        pool.put(Integer.valueOf(1));
        pool.put(Integer.valueOf(2));

        // put the colliding value back and force the rehash
        assertSame(COLLISION_2, pool.put(COLLISION_2));

        // put chained items into a pool without causing a rehash
        Pool<Integer> olympicPool = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY);
        assertSame(COLLISION_1, olympicPool.put(COLLISION_1));
        assertSame(COLLISION_2, olympicPool.put(COLLISION_2));
    }

    @Test
    public void put_with_hashingStrategy()
    {
        UnifiedSetWithHashingStrategy<Person> people = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.nullSafeHashingStrategy(LAST_NAME_HASHING_STRATEGY), 2).withAll(PEOPLE.castToList());
        //Testing if element already exists, returns the instance in the set
        assertSame(JOHNSMITH, people.put(new Person("Anything", "Smith")));
        Verify.assertSize(2, people);

        //Testing if the element doesn't exist, returns the element itself
        Person notInSet = new Person("Not", "inSet");
        assertSame(notInSet, people.put(notInSet));
        Verify.assertSize(3, people);

        //Testing putting a null to force a rehash
        assertNull(people.put(null));
        Verify.assertSize(4, people);

        //Testing put throws NullPointerException if the hashingStrategy is not null safe
        assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).put(null));
    }

    @Test
    public void remove_with_hashingStrategy()
    {
        HashingStrategy<Integer> hashingStrategy = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
        {
            @Override
            public int computeHashCode(Integer object)
            {
                return object % 1000;
            }

            @Override
            public boolean equals(Integer object1, Integer object2)
            {
                return object1.equals(object2);
            }
        });

        UnifiedSetWithHashingStrategy<Integer> integers = UnifiedSetWithHashingStrategy.newSet(
                hashingStrategy, 2).with(COLLISION_1, COLLISION_1 + 1000, COLLISION_1 + 2000, null);

        //Testing remove null from the end of the chain
        assertTrue(integers.remove(null));

        //Adding null back and creating a deep chain.
        integers.with(null, COLLISION_1 + 3000, COLLISION_1 + 4000, COLLISION_1 + 5000);

        //Removing null from the first position of a bucket in the deep chain
        assertTrue(integers.remove(null));
        assertFalse(integers.remove(null));

        //Removing from the end of the deep chain
        assertTrue(integers.remove(COLLISION_1 + 4000));

        //Removing from the first spot of the chain
        assertTrue(integers.remove(COLLISION_1));
        Verify.assertSize(4, integers);

        //Testing removing a non-existent element from a non bucket slot
        integers.add(2);
        integers.add(4);
        assertFalse(integers.remove(1002));

        //Testing removeIf
        assertTrue(integers.removeIf(IntegerPredicates.isEven()));
        Verify.assertEmpty(integers);
    }

    @Test
    public void removeFromPool()
    {
        Pool<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 8).withAll(COLLISIONS);
        COLLISIONS.reverseForEach(each ->
        {
            assertNull(unifiedSet.removeFromPool(null));
            assertSame(each, unifiedSet.removeFromPool(each));
            assertNull(unifiedSet.removeFromPool(each));
            assertNull(unifiedSet.removeFromPool(null));
            assertNull(unifiedSet.removeFromPool(COLLISION_10));
        });

        assertEquals(UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY), unifiedSet);

        COLLISIONS.forEach(Procedures.cast(each ->
        {
            Pool<Integer> unifiedSet2 = UnifiedSetWithHashingStrategy.newSet(
                    INTEGER_HASHING_STRATEGY, 8).withAll(COLLISIONS);

            assertNull(unifiedSet2.removeFromPool(null));
            assertSame(each, unifiedSet2.removeFromPool(each));
            assertNull(unifiedSet2.removeFromPool(each));
            assertNull(unifiedSet2.removeFromPool(null));
            assertNull(unifiedSet2.removeFromPool(COLLISION_10));
        }));

        // search a chain for a non-existent element
        Pool<Integer> chain = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        assertNull(chain.removeFromPool(COLLISION_5));

        // search a deep chain for a non-existent element
        Pool<Integer> deepChain = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6, COLLISION_7);
        assertNull(deepChain.removeFromPool(COLLISION_8));

        // search for a non-existent element
        Pool<Integer> empty = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1);
        assertNull(empty.removeFromPool(COLLISION_2));
    }

    @Test
    public void removeFromPool_with_hashingStrategy()
    {
        HashingStrategy<Integer> hashingStrategy = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
        {
            @Override
            public int computeHashCode(Integer object)
            {
                return object % 1000;
            }

            @Override
            public boolean equals(Integer object1, Integer object2)
            {
                return object1.equals(object2);
            }
        });

        UnifiedSetWithHashingStrategy<Integer> integers = UnifiedSetWithHashingStrategy.newSet(
                hashingStrategy, 2).with(COLLISION_1, COLLISION_1 + 1000, COLLISION_1 + 2000, null);

        //Testing remove null from the end of the chain
        assertNull(integers.removeFromPool(null));

        Integer collision4000 = COLLISION_1 + 4000;
        //Adding null back and creating a deep chain.
        integers.with(null, COLLISION_1 + 3000, collision4000, COLLISION_1 + 5000);

        //Removing null from the first position of a bucket in the deep chain
        assertNull(integers.removeFromPool(null));
        Verify.assertSize(6, integers);
        assertNull(integers.removeFromPool(null));
        Verify.assertSize(6, integers);

        //Removing from the end of the deep chain
        assertSame(collision4000, integers.removeFromPool(COLLISION_1 + 4000));

        //Removing from the first spot of the chain
        assertSame(COLLISION_1, integers.removeFromPool(COLLISION_1));
        Verify.assertSize(4, integers);

        //Testing removing an element that is not in a chained bucket
        assertSame(JOHNSMITH, UnifiedSetWithHashingStrategy.newSetWith(LAST_NAME_HASHING_STRATEGY, JOHNSMITH).removeFromPool(JOHNSMITH));
    }

    @Test
    public void serialization()
    {
        int size = COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSet(
                    INTEGER_HASHING_STRATEGY, SIZE).withAll(COLLISIONS.subList(0, i));
            Verify.assertPostSerializedEqualsAndHashCode(set);
            set.add(null);
            Verify.assertPostSerializedEqualsAndHashCode(set);
        }

        MutableSet<Integer> nullBucketZero = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, null, COLLISION_1, COLLISION_2);
        Verify.assertPostSerializedEqualsAndHashCode(nullBucketZero);

        MutableSet<Integer> simpleSetWithNull = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, null, 1, 2);
        Verify.assertPostSerializedEqualsAndHashCode(simpleSetWithNull);

        MutableSet<Person> people = UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY, PEOPLE);
        Verify.assertPostSerializedEqualsAndHashCode(people);
        //Testing the hashingStrategy is serialized correctly by making sure it is still hashing by last name
        Verify.assertSetsEqual(LAST_NAME_HASHED_SET.castToSet(), people.withAll(PEOPLE.castToList()));
    }

    @Test
    public void null_behavior()
    {
        UnifiedSetWithHashingStrategy<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 8).withAll(MORE_COLLISIONS);
        MORE_COLLISIONS.clone().reverseForEach(each ->
        {
            assertTrue(unifiedSet.add(null));
            assertFalse(unifiedSet.add(null));
            Verify.assertContains(null, unifiedSet);
            Verify.assertPostSerializedEqualsAndHashCode(unifiedSet);

            assertTrue(unifiedSet.remove(null));
            assertFalse(unifiedSet.remove(null));
            Verify.assertNotContains(null, unifiedSet);

            Verify.assertPostSerializedEqualsAndHashCode(unifiedSet);

            assertNull(unifiedSet.put(null));
            assertNull(unifiedSet.put(null));
            assertNull(unifiedSet.removeFromPool(null));
            assertNull(unifiedSet.removeFromPool(null));

            Verify.assertContains(each, unifiedSet);
            assertTrue(unifiedSet.remove(each));
            assertFalse(unifiedSet.remove(each));
            Verify.assertNotContains(each, unifiedSet);
        });
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();

        MutableSet<Integer> singleCollisionBucket = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2);
        singleCollisionBucket.remove(COLLISION_2);
        assertEquals(singleCollisionBucket, UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1));

        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, null, COLLISION_1, COLLISION_2, COLLISION_3),
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, null, COLLISION_1, COLLISION_2, COLLISION_3));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, null, COLLISION_2, COLLISION_3),
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, null, COLLISION_2, COLLISION_3));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, null, COLLISION_3),
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, null, COLLISION_3));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, null),
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, null));
    }

    @Test
    public void equals_with_hashingStrategy()
    {
        HashingStrategy<Person> personHashingStrategy = HashingStrategies.fromFunction(Person.TO_LAST);
        HashingStrategy<Person> personHashingStrategyCopy = HashingStrategies.fromFunction(Person.TO_LAST);

        MutableSet<Person> setA = UnifiedSetWithHashingStrategy.newSet(personHashingStrategy, PEOPLE);
        MutableSet<Person> setB = UnifiedSetWithHashingStrategy.newSet(personHashingStrategyCopy, PEOPLE);

        //Test sets with different instances of the same hashing strategy are equal symmetrically
        Verify.assertEqualsAndHashCode(setA, setB);

        //Checking that a hashing set is symmetrically equal to an identical JDK set
        Set<Person> hashSet = new HashSet<>(setA);
        assertTrue(hashSet.equals(setA) && setA.equals(hashSet));

        //Checking that a hash set is symmetrically equal to an identical Eclipse Collections set
        MutableSet<Person> unifiedSet = UnifiedSet.newSet(setA);
        assertTrue(unifiedSet.equals(setA) && setA.equals(unifiedSet));

        //Testing the asymmetry of equals
        HashingStrategy<String> firstLetterHashingStrategy = new HashingStrategy<String>()
        {
            @Override
            public int computeHashCode(String object)
            {
                return Character.valueOf(object.charAt(0));
            }

            @Override
            public boolean equals(String object1, String object2)
            {
                return object1.charAt(0) == object2.charAt(0);
            }
        };

        MutableSet<String> hashedString = UnifiedSetWithHashingStrategy.newSetWith(firstLetterHashingStrategy, "apple", "banana", "cheese");
        MutableSet<String> anotherHashedString =
                UnifiedSetWithHashingStrategy.newSetWith(firstLetterHashingStrategy, "a", "b", "c");
        MutableSet<String> normalString = UnifiedSet.newSetWith("alpha", "bravo", "charlie");

        //Testing hashedString equals normalString
        assertTrue(hashedString.equals(normalString) && hashedString.equals(hashedString));
        //Testing normalString does not equal a hashedString, note cannot use Assert.notEquals because it assumes symmetric equals behavior
        assertFalse(normalString.equals(hashedString) && hashedString.equals(normalString));
        //Testing 2 sets with same hashing strategies must obey object equals definition
        Verify.assertEqualsAndHashCode(hashedString, anotherHashedString);
        //Testing set size matters
        assertNotEquals(hashedString, normalString.remove("alpha"));
    }

    @Test
    public void constructor_from_UnifiedSet()
    {
        Verify.assertEqualsAndHashCode(
                new HashSet<>(MORE_COLLISIONS),
                UnifiedSetWithHashingStrategy.newSet(
                        INTEGER_HASHING_STRATEGY,
                        MORE_COLLISIONS));
    }

    @Test
    public void copyConstructor()
    {
        // test copying a chained bucket
        MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6, COLLISION_7);
        Verify.assertEqualsAndHashCode(set, UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, set));
    }

    @Test
    public void newSet_null()
    {
        assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet((UnifiedSetWithHashingStrategy<Object>) null));
    }

    @Test
    public void newSet_null_hashingStrategy()
    {
        assertThrows(IllegalArgumentException.class, () -> UnifiedSetWithHashingStrategy.newSet((HashingStrategy<Object>) null));
    }

    @Test
    public void batchForEach()
    {
        //Testing batch size of 1 to 16 with no chains
        UnifiedSet<Integer> set = UnifiedSet.<Integer>newSet(10).with(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (int sectionCount = 1; sectionCount <= 16; ++sectionCount)
        {
            Sum sum = new IntegerSum(0);
            for (int sectionIndex = 0; sectionIndex < sectionCount; ++sectionIndex)
            {
                set.batchForEach(new SumProcedure<>(sum), sectionIndex, sectionCount);
            }
            assertEquals(55, sum.getValue());
        }

        //Testing 1 batch with chains
        Sum sum2 = new IntegerSum(0);
        UnifiedSetWithHashingStrategy<Integer> set2 = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 3).with(COLLISION_1, COLLISION_2, COLLISION_3, 1, 2);
        int numBatches = set2.getBatchCount(100);
        for (int i = 0; i < numBatches; ++i)
        {
            set2.batchForEach(new SumProcedure<>(sum2), i, numBatches);
        }
        assertEquals(1, numBatches);
        assertEquals(54, sum2.getValue());

        //Testing batch size of 3 with chains and uneven last batch
        Sum sum3 = new IntegerSum(0);
        UnifiedSetWithHashingStrategy<Integer> set3 = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 4, 1.0F).with(COLLISION_1, COLLISION_2, 1, 2, 3, 4, 5);
        int numBatches2 = set3.getBatchCount(3);
        for (int i = 0; i < numBatches2; ++i)
        {
            set3.batchForEach(new SumProcedure<>(sum3), i, numBatches2);
        }
        assertEquals(32, sum3.getValue());

        //Test batchForEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum4 = new IntegerSum(0);
        UnifiedSetWithHashingStrategy<Integer> set4 = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY);
        set4.batchForEach(new SumProcedure<>(sum4), 0, set4.getBatchCount(1));
        assertEquals(0, sum4.getValue());
    }

    @Override
    @Test
    public void toArray()
    {
        super.toArray();

        int size = COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSet(
                    INTEGER_HASHING_STRATEGY, SIZE).withAll(COLLISIONS.subList(0, i));
            Object[] objects = set.toArray();
            assertEquals(set, UnifiedSet.newSetWith(objects));
        }

        MutableSet<Integer> deepChain = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6);
        assertArrayEquals(new Integer[]{COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6}, deepChain.toArray());

        MutableSet<Integer> minimumChain = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2);
        minimumChain.remove(COLLISION_2);
        assertArrayEquals(new Integer[]{COLLISION_1}, minimumChain.toArray());

        MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        Integer[] target = {Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1)};
        Integer[] actual = set.toArray(target);
        ArrayIterate.sort(actual, actual.length, Comparators.safeNullsHigh(Integer::compareTo));
        assertArrayEquals(new Integer[]{COLLISION_1, 1, COLLISION_2, COLLISION_3, COLLISION_4, null}, actual);
    }

    @Test
    public void iterator_remove()
    {
        int size = MORE_COLLISIONS.size();
        for (int i = 0; i < size; i++)
        {
            MutableSet<Integer> actual = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, SIZE).withAll(MORE_COLLISIONS);
            Iterator<Integer> iterator = actual.iterator();
            for (int j = 0; j <= i; j++)
            {
                assertTrue(iterator.hasNext());
                iterator.next();
            }
            iterator.remove();

            MutableSet<Integer> expected = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, MORE_COLLISIONS);
            expected.remove(MORE_COLLISIONS.get(i));
            assertEquals(expected, actual);
        }

        // remove the last element from within a 2-level long chain that is fully populated
        MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6, COLLISION_7);
        Iterator<Integer> iterator1 = set.iterator();
        for (int i = 0; i < 7; i++)
        {
            iterator1.next();
        }
        iterator1.remove();
        assertEquals(UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6), set);

        // remove the second-to-last element from a 2-level long chain that that has one empty slot
        Iterator<Integer> iterator2 = set.iterator();
        for (int i = 0; i < 6; i++)
        {
            iterator2.next();
        }
        iterator2.remove();
        assertEquals(UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5), set);

        //Testing removing the last element in a fully populated chained bucket
        MutableSet<Integer> set2 = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        Iterator<Integer> iterator3 = set2.iterator();
        for (int i = 0; i < 3; ++i)
        {
            iterator3.next();
        }
        iterator3.next();
        iterator3.remove();
        Verify.assertSetsEqual(UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_3), set2);
    }

    @Test
    public void setKeyPreservation()
    {
        Holder<String> key = new Holder<>("key");

        Holder<String> duplicateKey1 = new Holder<>("key");
        MutableSet<Holder<String>> set1 = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.<Holder<String>>defaultStrategy()).with(key, duplicateKey1);
        Verify.assertSize(1, set1);
        Verify.assertContains(key, set1);
        assertSame(key, set1.getFirst());

        Holder<String> duplicateKey2 = new Holder<>("key");
        MutableSet<Holder<String>> set2 = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.<Holder<String>>defaultStrategy()).with(key, duplicateKey1, duplicateKey2);
        Verify.assertSize(1, set2);
        Verify.assertContains(key, set2);
        assertSame(key, set2.getFirst());

        Holder<String> duplicateKey3 = new Holder<>("key");
        MutableSet<Holder<String>> set3 = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.<Holder<String>>defaultStrategy()).with(key, new Holder<>("not a dupe"), duplicateKey3);
        Verify.assertSize(2, set3);
        Verify.assertContainsAll(set3, key, new Holder<>("not a dupe"));
        assertSame(key, set3.detect(key::equals));
    }

    @Test
    public void withSameIfNotModified()
    {
        UnifiedSetWithHashingStrategy<Integer> integers = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY);
        assertEquals(UnifiedSet.newSetWith(1, 2), integers.with(1, 2));
        assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), integers.with(2, 3, 4));
        assertSame(integers, integers.with(5, 6, 7));
    }

    @Override
    @Test
    public void retainAll()
    {
        super.retainAll();

        MutableSet<Object> setWithNull = this.newWith((Object) null);
        assertFalse(setWithNull.retainAll(FastList.newListWith((Object) null)));
        assertEquals(UnifiedSet.newSetWith((Object) null), setWithNull);
    }

    @Override
    public void getFirst()
    {
        super.getFirst();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i <= size - 1; i++)
        {
            MutableSet<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, 1).withAll(MORE_COLLISIONS.subList(0, i));
            assertSame(MORE_COLLISIONS.get(0), unifiedSet.getFirst());
        }
    }

    @Override
    public void getLast()
    {
        super.getLast();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i <= size - 1; i++)
        {
            MutableSet<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, 1).withAll(MORE_COLLISIONS.subList(0, i));
            assertSame(MORE_COLLISIONS.get(i - 1), unifiedSet.getLast());
        }

        MutableSet<Integer> chainedWithOneSlot = UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2);
        chainedWithOneSlot.remove(COLLISION_2);
        assertSame(COLLISION_1, chainedWithOneSlot.getLast());
    }

    @Test
    public void trimToSize()
    {
        UnifiedSetWithHashingStrategy<String> set = UnifiedSetWithHashingStrategy.newSet(HashingStrategies.defaultStrategy());

        MutableSet<String> expected = Sets.mutable.empty();

        Interval integers = Interval.fromTo(0, 250);
        integers.each(each ->
        {
            set.add(each.toString());
            expected.add(each.toString());
        });
        ArrayIterate.forEach(FREQUENT_COLLISIONS, each ->
        {
            set.add(each);
            expected.add(each);
        });

        assertEquals(expected, set);
        assertEquals(261, set.size());

        MutableList<Integer> toRemove = Lists.mutable.withAll(Interval.evensFromTo(0, 20));

        toRemove.addAll(Interval.oddsFromTo(35, 55));
        toRemove.each(each ->
        {
            set.remove(each.toString());
            expected.remove(each.toString());
        });

        // First assertion to verify that trim does not happen since, the table is already at the smallest required power of 2.
        assertFalse(set.trimToSize());
        assertEquals(239, set.size());
        assertEquals(expected, set);

        Interval.evensFromTo(0, 250).each(each ->
        {
            set.remove(each.toString());
            expected.remove(each.toString());
        });

        // Second assertion to verify that trim happens since, the table length is less than smallest required power of 2.
        assertTrue(set.trimToSize());
        assertFalse(set.trimToSize());
        assertEquals(expected, set);
        assertEquals(124, set.size());
        expected.each(each -> assertEquals(each, set.get(each)));

        integers.each(each ->
        {
            set.remove(each.toString());
            expected.remove(each.toString());
        });
        assertTrue(set.trimToSize());
        assertFalse(set.trimToSize());
        assertEquals(expected, set);
        expected.each(each -> assertEquals(each, set.get(each)));

        set.clear();
        assertTrue(set.trimToSize());
        Interval.oneTo(4).each(each -> set.add(each.toString()));
        // Assert that trim does not happen after puts
        assertFalse(set.trimToSize());
        set.remove("1");
        set.remove("2");
        assertTrue(set.trimToSize());

        set.add("1");
        // Assert that the resized table due to put is the required size and no need to trim that.
        assertFalse(set.trimToSize());

        Interval.zeroTo(4).each(each -> set.add(each.toString()));
        Interval.oneTo(3).each(each -> set.remove(each.toString()));
        assertTrue(set.trimToSize());
        assertEquals(2, set.size());
    }
}
