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
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
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
import com.google.common.collect.testing.testers.CollectionSpliteratorTester;
import com.google.common.collect.testing.testers.MapComputeIfAbsentTester;
import com.google.common.collect.testing.testers.MapComputeIfPresentTester;
import com.google.common.collect.testing.testers.MapPutIfAbsentTester;
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
        TestStringMapGenerator generator = new TestStringMapGenerator()
        {
            @Override
            protected Map<String, String> create(Entry<String, String>[] entries)
            {
                return (Map<String, String>) Maps.immutable.empty();
            }
        };
        return MapTestSuiteBuilder.using(generator)
                .named("ImmutableEmptyMap")
                .withFeatures(CollectionFeature.SERIALIZABLE, CollectionSize.ZERO)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForEmptyMap()
    {
        TestStringMapGenerator generator = new TestStringMapGenerator()
        {
            @Override
            protected Map<String, String> create(Entry<String, String>[] entries)
            {
                return Maps.fixedSize.empty();
            }
        };
        return MapTestSuiteBuilder.using(generator)
                .named("FixedSizeEmptyMap")
                .withFeatures(CollectionFeature.SERIALIZABLE, CollectionSize.ZERO)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForImmutableEmptySortedMap()
    {
        TestStringSortedMapGenerator generator = new TestStringSortedMapGenerator()
        {
            @Override
            protected SortedMap<String, String> create(Entry<String, String>[] entries)
            {
                return (SortedMap<String, String>) SortedMaps.immutable.<String, String>empty();
            }
        };
        return MapTestSuiteBuilder.using(generator)
                .named("ImmutableEmptySortedMap")
                .withFeatures(CollectionFeature.SERIALIZABLE, CollectionSize.ZERO)
                .suppressing(Collections.emptySet())
                .createTestSuite();
    }

    public Test testsForImmutableSingletonMap()
    {
        TestStringMapGenerator generator = new TestStringMapGenerator()
        {
            @Override
            protected Map<String, String> create(Entry<String, String>[] entries)
            {
                return (Map<String, String>) Maps.immutable.of(entries[0].getKey(), entries[0].getValue());
            }
        };
        return MapTestSuiteBuilder.using(generator)
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
        TestStringMapGenerator generator = new TestStringMapGenerator()
        {
            @Override
            protected Map<String, String> create(Entry<String, String>[] entries)
            {
                return populate(new UnifiedMap<>(), entries);
            }
        };
        return MapTestSuiteBuilder.using(generator)
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
        TestStringMapGenerator generator = new TestStringMapGenerator()
        {
            @Override
            protected Map<String, String> create(Entry<String, String>[] entries)
            {
                return populate(new UnifiedMapWithHashingStrategy<>(HashingStrategies.nullSafeHashingStrategy(
                        HashingStrategies.defaultStrategy())), entries);
            }
        };
        return MapTestSuiteBuilder.using(generator)
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
        TestStringSortedMapGenerator generator = new TestStringSortedMapGenerator()
        {
            @Override
            protected SortedMap<String, String> create(Entry<String, String>[] entries)
            {
                return populate(new TreeSortedMap<>(), entries);
            }
        };
        return SortedMapTestSuiteBuilder.using(generator)
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
        TestStringSortedMapGenerator generator = new TestStringSortedMapGenerator()
        {
            @Override
            protected SortedMap<String, String> create(Entry<String, String>[] entries)
            {
                return populate(
                        new TreeSortedMap<>(arbitraryNullFriendlyComparator()), entries);
            }
        };
        return SortedMapTestSuiteBuilder.using(generator)
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
        TestStringMapGenerator generator = new TestStringMapGenerator()
        {
            @Override
            protected Map<String, String> create(Entry<String, String>[] entries)
            {
                return populate(new UnifiedMap<>(), entries).asUnmodifiable();
            }
        };
        return MapTestSuiteBuilder.using(generator)
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
        TestStringSortedMapGenerator generator = new TestStringSortedMapGenerator()
        {
            @Override
            protected SortedMap<String, String> create(Entry<String, String>[] entries)
            {
                return populate(new TreeSortedMap<>(), entries).asUnmodifiable();
            }
        };
        return MapTestSuiteBuilder.using(generator)
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
        TestStringMapGenerator generator = new TestStringMapGenerator()
        {
            @Override
            protected Map<String, String> create(Entry<String, String>[] entries)
            {
                return populate(new ConcurrentHashMap<>(), entries);
            }
        };
        return MapTestSuiteBuilder.using(generator)
                .named("ConcurrentHashMap")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .suppressing(concurrentHashMapNullValueBugMethods())
                .createTestSuite();
    }

    public Test testsForConcurrentHashMapUnsafe()
    {
        TestStringMapGenerator generator = new TestStringMapGenerator()
        {
            @Override
            protected Map<String, String> create(Entry<String, String>[] entries)
            {
                return populate(new ConcurrentHashMapUnsafe<>(), entries);
            }
        };
        return MapTestSuiteBuilder.using(generator)
                .named("ConcurrentHashMapUnsafe")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .suppressing(concurrentHashMapNullValueBugMethods())
                .createTestSuite();
    }

    // TODO: Fix null-value-as-absent bugs in ConcurrentHashMap/ConcurrentHashMapUnsafe and remove this suppression.
    private static Collection<Method> concurrentHashMapNullValueBugMethods()
    {
        try
        {
            return Arrays.asList(
                    CollectionSpliteratorTester.class.getDeclaredMethod("testSpliteratorNullable"),
                    MapComputeIfAbsentTester.class.getDeclaredMethod("testComputeIfAbsent_nullTreatedAsAbsent"),
                    MapComputeIfPresentTester.class.getDeclaredMethod("testComputeIfPresent_nullTreatedAsAbsent"),
                    MapPutIfAbsentTester.class.getDeclaredMethod("testPutIfAbsent_replacesNullValue"));
        }
        catch (NoSuchMethodException exception)
        {
            throw new RuntimeException(exception);
        }
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
