/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.multimap.bag.strategy;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.map.strategy.mutable.UnifiedMapWithHashingStrategy;
import org.eclipse.collections.impl.multimap.bag.AbstractMutableBagMultimapTestCase;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Holder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Test of {@link HashBagMultimap}.
 */
public class HashBagMultimapWithHashingStrategyTest extends AbstractMutableBagMultimapTestCase
{
    @Override
    protected <K, V> MutableBagMultimap<K, V> newMultimap()
    {
        return HashBagMultimapWithHashingStrategy.newMultimap(HashingStrategies.defaultStrategy());
    }

    @Override
    protected <K, V> MutableBagMultimap<K, V> newMultimapWithKeyValue(K key, V value)
    {
        return this.<K, V>newMultimap()
                .withKeyValue(key, value);
    }

    @Override
    protected <K, V> MutableBagMultimap<K, V> newMultimapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return this.<K, V>newMultimap()
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2);
    }

    @Override
    protected <K, V> MutableBagMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3)
    {
        return this.<K, V>newMultimap()
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2)
                .withKeyValue(key3, value3);
    }

    @Override
    protected <K, V> MutableBagMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        return this.<K, V>newMultimap()
                .withKeyValue(key1, value1)
                .withKeyValue(key2, value2)
                .withKeyValue(key3, value3)
                .withKeyValue(key4, value4);
    }

    @Override
    protected final <K, V> MutableBagMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return HashBagMultimapWithHashingStrategy.newMultimap(HashingStrategies.defaultStrategy(), pairs);
    }

    @Override
    protected <K, V> MutableBagMultimap<K, V> newMultimapFromPairs(Iterable<Pair<K, V>> inputIterable)
    {
        return HashBagMultimapWithHashingStrategy.newMultimap(HashingStrategies.defaultStrategy(), inputIterable);
    }

    @Override
    protected final <V> MutableBag<V> createCollection(V... args)
    {
        return Bags.mutable.of(args);
    }

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();

        MutableBagMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        ImmutableMultimap<String, Integer> actual = multimap.toImmutable();
        assertNotNull(actual);
        assertEquals(multimap, actual);
        // ideally this should go back to HashBagMultimapWithHashingStrategy
        Verify.assertInstanceOf(HashBagMultimap.class, actual.toMutable());
    }

    @Override
    @Test
    public void toMutable()
    {
        super.toMutable();

        MutableBagMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        MutableMultimap<String, Integer> mutableCopy = multimap.toMutable();
        assertNotSame(multimap, mutableCopy);
        assertEquals(multimap, mutableCopy);
        Verify.assertInstanceOf(HashBagMultimapWithHashingStrategy.class, mutableCopy);
    }

    @Override
    @Test
    public void toMap()
    {
        super.toMap();

        MutableBagMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        UnifiedMapWithHashingStrategy<String, RichIterable<Integer>> expected =
                UnifiedMapWithHashingStrategy.newMap(HashingStrategies.defaultStrategy());
        expected.put("One", this.createCollection(1));
        expected.put("Two", this.createCollection(2, 2));
        MutableMap<String, RichIterable<Integer>> actual = multimap.toMap();
        assertEquals(expected, actual);
        Verify.assertInstanceOf(UnifiedMapWithHashingStrategy.class, actual);
    }

    @Override
    @Test
    public void toMapWithTarget()
    {
        super.toMapWithTarget();

        MutableBagMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        UnifiedMapWithHashingStrategy<String, RichIterable<Integer>> expected =
                UnifiedMapWithHashingStrategy.newMap(HashingStrategies.defaultStrategy());
        expected.put("One", UnifiedSet.newSetWith(1));
        expected.put("Two", UnifiedSet.newSetWith(2, 2));
        MutableMap<String, MutableSet<Integer>> actual = multimap.toMap(UnifiedSet::new);
        assertEquals(expected, actual);
        Verify.assertInstanceOf(UnifiedMapWithHashingStrategy.class, actual);
    }

    @Test
    public void testHashingStrategyConstructor()
    {
        HashBagMultimapWithHashingStrategy<Holder<Integer>, Integer> multimapWithIdentity =
                HashBagMultimapWithHashingStrategy.newMultimap(HashingStrategies.identityStrategy());

        multimapWithIdentity.put(new Holder<>(1), 1);
        multimapWithIdentity.putAll(new Holder<>(1), Lists.fixedSize.of(2, 20, 1));
        multimapWithIdentity.put(new Holder<>(1), 3);

        assertEquals(3, multimapWithIdentity.sizeDistinct());
        Verify.assertSize(5, multimapWithIdentity);

        HashBagMultimapWithHashingStrategy<Holder<Integer>, Integer> multimapWithDefault =
                HashBagMultimapWithHashingStrategy.newMultimap(
                        HashingStrategies.defaultStrategy(),
                        multimapWithIdentity);

        assertEquals(1, multimapWithDefault.sizeDistinct());
        Verify.assertSize(5, multimapWithDefault);

        Verify.assertIterablesEqual(
                multimapWithIdentity.valuesView().toBag(),
                multimapWithDefault.valuesView().toBag());

        HashBagMultimapWithHashingStrategy<Holder<Integer>, Integer> copyOfMultimapWithDefault =
                HashBagMultimapWithHashingStrategy.newMultimap(multimapWithDefault);

        Verify.assertMapsEqual(multimapWithDefault.toMap(), copyOfMultimapWithDefault.toMap());
    }

    @Test
    public void testKeyHashingStrategy()
    {
        HashBagMultimapWithHashingStrategy<Integer, Integer> multimap =
                HashBagMultimapWithHashingStrategy.newMultimap(HashingStrategies.identityStrategy());
        assertEquals(HashingStrategies.identityStrategy(), multimap.getKeyHashingStrategy());
    }
}
