/*
 * Copyright (c) 2024 Craig Motlin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.guavalib.map;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.SortedMapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.TestStringSortedMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMapUnsafe;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.map.strategy.mutable.UnifiedMapWithHashingStrategy;

public class MapsTest
{
    public static Test suite()
    {
        return new MapsTest().allTests();
    }

    public Test allTests()
    {
        TestSuite suite = new TestSuite("Eclipse Collections Maps");
        suite.addTest(testsForImmutableEmptyMap());
        suite.addTest(testsForEmptyMap());
        suite.addTest(testsForImmutableEmptySortedMap());
        suite.addTest(testsForImmutableSingletonMap());
        suite.addTest(testsForUnifiedMap());
        suite.addTest(testsForUnifiedMapWithHashingStrategy());
        suite.addTest(testsForTreeSortedMapNatural());
        suite.addTest(testsForTreeMapWithComparator());
        suite.addTest(testsForUnmodifiableMutableMap());
        suite.addTest(testsForUnmodifiableMutableSortedMap());
        suite.addTest(testsForConcurrentHashMap());
        suite.addTest(testsForConcurrentHashMapUnsafe());
        return suite;
    }

    public Test testsForImmutableEmptyMap()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringMapGenerator()
                        {
                            @Override
                            protected Map<String, String> create(Entry<String, String>[] entries)
                            {
                                return (Map<String, String>) Maps.immutable.empty();
                            }
                        })
                .named("ImmutableEmptyMap")
                .withFeatures(CollectionFeature.SERIALIZABLE, CollectionSize.ZERO)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForEmptyMap()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringMapGenerator()
                        {
                            @Override
                            protected Map<String, String> create(Entry<String, String>[] entries)
                            {
                                return Maps.fixedSize.empty();
                            }
                        })
                .named("ImmutableEmptyMap")
                .withFeatures(CollectionFeature.SERIALIZABLE, CollectionSize.ZERO)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }


    public Test testsForImmutableEmptySortedMap()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringSortedMapGenerator()
                        {
                            @Override
                            protected SortedMap<String, String> create(Entry<String, String>[] entries)
                            {
                                return (SortedMap<String, String>) SortedMaps.immutable.<String, String>empty();
                            }
                        })
                .named("ImmutableEmptySortedMap")
                .withFeatures(CollectionFeature.SERIALIZABLE, CollectionSize.ZERO)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForImmutableSingletonMap()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringMapGenerator()
                        {
                            @Override
                            protected Map<String, String> create(Entry<String, String>[] entries)
                            {
                                return (Map<String, String>) Maps.immutable.of(entries[0].getKey(), entries[0].getValue());
                            }
                        })
                .named("ImmutableSingletonMap")
                .withFeatures(
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ONE)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForUnifiedMap()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringMapGenerator()
                        {
                            @Override
                            protected Map<String, String> create(Entry<String, String>[] entries)
                            {
                                return populate(new UnifiedMap<>(), entries);
                            }
                        })
                .named("UnifiedMap")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .createTestSuite();
    }

    public Test testsForUnifiedMapWithHashingStrategy()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringMapGenerator()
                        {
                            @Override
                            protected Map<String, String> create(Entry<String, String>[] entries)
                            {
                                return populate(new UnifiedMapWithHashingStrategy<>(HashingStrategies.nullSafeHashingStrategy(
                                        HashingStrategies.defaultStrategy())), entries);
                            }
                        })
                .named("UnifiedMapWithHashingStrategy / nullSafeHashingStrategy")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .createTestSuite();
    }

    public Test testsForTreeSortedMapNatural()
    {
        return SortedMapTestSuiteBuilder.using(
                        new TestStringSortedMapGenerator()
                        {
                            @Override
                            protected SortedMap<String, String> create(Entry<String, String>[] entries)
                            {
                                return populate(new TreeSortedMap<>(), entries);
                            }
                        })
                .named("TreeSortedMap, natural")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.KNOWN_ORDER,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForTreeMapWithComparator()
    {
        return SortedMapTestSuiteBuilder.using(
                        new TestStringSortedMapGenerator()
                        {
                            @Override
                            protected SortedMap<String, String> create(Entry<String, String>[] entries)
                            {
                                return populate(
                                        new TreeSortedMap<>(arbitraryNullFriendlyComparator()), entries);
                            }
                        })
                .named("TreeSortedMap, with comparator")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        MapFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.KNOWN_ORDER,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForUnmodifiableMutableMap()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringMapGenerator()
                        {
                            @Override
                            protected Map<String, String> create(Entry<String, String>[] entries)
                            {
                                return populate(new UnifiedMap<>(), entries).asUnmodifiable();
                            }
                        })
                .named("UnifiedMap/asUnmodifiable")
                .withFeatures(
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForUnmodifiableMutableSortedMap()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringSortedMapGenerator()
                        {
                            @Override
                            protected SortedMap<String, String> create(Entry<String, String>[] entries)
                            {
                                return populate(new TreeSortedMap<>(), entries).asUnmodifiable();
                            }
                        })
                .named("TreeSortedMap/asUnmodifiable, natural")
                .withFeatures(
                        MapFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.KNOWN_ORDER,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForConcurrentHashMap()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringMapGenerator()
                        {
                            @Override
                            protected Map<String, String> create(Entry<String, String>[] entries)
                            {
                                return populate(new ConcurrentHashMap<>(), entries);
                            }
                        })
                .named("ConcurrentHashMap")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .createTestSuite();
    }

    public Test testsForConcurrentHashMapUnsafe()
    {
        return MapTestSuiteBuilder.using(
                        new TestStringMapGenerator()
                        {
                            @Override
                            protected Map<String, String> create(Entry<String, String>[] entries)
                            {
                                return populate(new ConcurrentHashMapUnsafe<>(), entries);
                            }
                        })
                .named("ConcurrentHashMapUnsafe")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .createTestSuite();
    }

    private static <T, M extends Map<T, String>> M populate(M map, Entry<T, String>[] entries)
    {
        for (Entry<T, String> entry : entries)
        {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    static <T> Comparator<T> arbitraryNullFriendlyComparator()
    {
        return new NullFriendlyComparator<>();
    }

    private static final class NullFriendlyComparator<T>
            implements Comparator<T>,
            Serializable
    {
        @Override
        public int compare(T left, T right)
        {
            return String.valueOf(left).compareTo(String.valueOf(right));
        }
    }
}
