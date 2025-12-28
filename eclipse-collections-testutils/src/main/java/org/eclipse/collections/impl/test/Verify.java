/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Callable;

import org.eclipse.collections.api.InternalIterable;
import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.sorted.SortedBag;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMapIterable;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.sorted.SortedMapIterable;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.bag.BagMultimap;
import org.eclipse.collections.api.multimap.list.ListMultimap;
import org.eclipse.collections.api.multimap.set.SetMultimap;
import org.eclipse.collections.api.multimap.sortedbag.SortedBagMultimap;
import org.eclipse.collections.api.multimap.sortedset.SortedSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * An extension of the {@link Assert} class, which adds useful additional "assert" methods.
 * You can import this class instead of Assert, and use it thus, e.g.:
 * <pre>
 *     Verify.assertEquals("fred", name);  // from original Assert class
 *     Verify.assertContains("fred", nameList);  // from new extensions
 *     Verify.assertBefore("fred", "jim", orderedNamesList);  // from new extensions
 * </pre>
 */
public final class Verify extends Assert
{
    private static final int MAX_DIFFERENCES = 5;
    private static final byte[] LINE_SEPARATOR = {'\n'};
    private static final Encoder ENCODER = Base64.getMimeEncoder(76, LINE_SEPARATOR);
    private static final Decoder DECODER = Base64.getMimeDecoder();

    private Verify()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static void fail(String message, Throwable cause)
    {
        throw new AssertionError(message, cause);
    }

    /**
     * Assert that the given {@link Iterable} is empty.
     */
    public static void assertEmpty(Iterable<?> actualIterable)
    {
        Verify.assertEmpty("iterable", actualIterable);
    }

    /**
     * Assert that the given {@link Collection} is empty.
     */
    public static void assertEmpty(String iterableName, Iterable<?> actualIterable)
    {
        Verify.assertObjectNotNull(iterableName, actualIterable);

        if (Iterate.notEmpty(actualIterable))
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualIterable) + '>');
        }
        if (!Iterate.isEmpty(actualIterable))
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualIterable) + '>');
        }
        if (Iterate.sizeOf(actualIterable) != 0)
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualIterable) + '>');
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} is empty.
     */
    public static void assertEmpty(MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        Verify.assertEmpty("mutableMapIterable", actualMutableMapIterable);
    }

    /**
     * Assert that the given {@link Collection} is empty.
     */
    public static void assertEmpty(String mutableMapIterableName, MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        Verify.assertObjectNotNull(mutableMapIterableName, actualMutableMapIterable);

        if (Iterate.notEmpty(actualMutableMapIterable))
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
        }
        if (!Iterate.isEmpty(actualMutableMapIterable))
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
        }
        if (!actualMutableMapIterable.isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
        }
        if (actualMutableMapIterable.notEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
        }
        if (!actualMutableMapIterable.isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.size() + '>');
        }
        if (!actualMutableMapIterable.keySet().isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.keySet().size() + '>');
        }
        if (!actualMutableMapIterable.values().isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.values().size() + '>');
        }
        if (!actualMutableMapIterable.entrySet().isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.entrySet().size() + '>');
        }
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is empty.
     */
    public static void assertEmpty(PrimitiveIterable primitiveIterable)
    {
        Verify.assertEmpty("primitiveIterable", primitiveIterable);
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is empty.
     */
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public static void assertEmpty(String iterableName, PrimitiveIterable primitiveIterable)
    {
        Verify.assertObjectNotNull(iterableName, primitiveIterable);

        if (primitiveIterable.notEmpty())
        {
            fail(iterableName + " should be empty; actual size:<" + primitiveIterable.size() + '>');
        }
        if (!primitiveIterable.isEmpty())
        {
            fail(iterableName + " should be empty; actual size:<" + primitiveIterable.size() + '>');
        }
        if (primitiveIterable.size() != 0)
        {
            fail(iterableName + " should be empty; actual size:<" + primitiveIterable.size() + '>');
        }
    }

    /**
     * Assert that the given {@link Iterable} is empty.
     */
    public static void assertIterableEmpty(Iterable<?> iterable)
    {
        Verify.assertIterableEmpty("iterable", iterable);
    }

    /**
     * Assert that the given {@link Iterable} is empty.
     */
    public static void assertIterableEmpty(String iterableName, Iterable<?> iterable)
    {
        Verify.assertObjectNotNull(iterableName, iterable);

        if (Iterate.notEmpty(iterable))
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(iterable) + '>');
        }
        if (!Iterate.isEmpty(iterable))
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(iterable) + '>');
        }
        if (Iterate.sizeOf(iterable) != 0)
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(iterable) + '>');
        }
    }

    /**
     * Assert that the given object is an instanceof expectedClassType.
     */
    public static void assertInstanceOf(Class<?> expectedClassType, Object actualObject)
    {
        Verify.assertInstanceOf(actualObject.getClass().getName(), expectedClassType, actualObject);
    }

    /**
     * Assert that the given object is an instanceof expectedClassType.
     */
    public static void assertInstanceOf(String objectName, Class<?> expectedClassType, Object actualObject)
    {
        if (!expectedClassType.isInstance(actualObject))
        {
            fail(objectName + " is not an instance of " + expectedClassType.getName());
        }
    }

    /**
     * Assert that the given object is not an instanceof expectedClassType.
     */
    public static void assertNotInstanceOf(Class<?> expectedClassType, Object actualObject)
    {
        Verify.assertNotInstanceOf(actualObject.getClass().getName(), expectedClassType, actualObject);
    }

    /**
     * Assert that the given object is not an instanceof expectedClassType.
     */
    public static void assertNotInstanceOf(String objectName, Class<?> expectedClassType, Object actualObject)
    {
        if (expectedClassType.isInstance(actualObject))
        {
            fail(objectName + " is an instance of " + expectedClassType.getName());
        }
    }

    /**
     * Assert that the given {@link Map} is empty.
     */
    public static void assertEmpty(Map<?, ?> actualMap)
    {
        Verify.assertEmpty("map", actualMap);
    }

    /**
     * Assert that the given {@link Multimap} is empty.
     */
    public static void assertEmpty(Multimap<?, ?> actualMultimap)
    {
        Verify.assertEmpty("multimap", actualMultimap);
    }

    /**
     * Assert that the given {@link Multimap} is empty.
     */
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public static void assertEmpty(String multimapName, Multimap<?, ?> actualMultimap)
    {
        Verify.assertObjectNotNull(multimapName, actualMultimap);

        if (actualMultimap.notEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
        }
        if (!actualMultimap.isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
        }
        if (actualMultimap.size() != 0)
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
        }
        if (actualMultimap.sizeDistinct() != 0)
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
        }
        if (!actualMultimap.keyBag().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.keyBag().size() + '>');
        }
        if (!actualMultimap.keysView().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.keysView().size() + '>');
        }
        if (!actualMultimap.valuesView().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.valuesView().size() + '>');
        }
        if (!actualMultimap.keyValuePairsView().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.keyValuePairsView().size() + '>');
        }
        if (!actualMultimap.keyMultiValuePairsView().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.keyMultiValuePairsView().size() + '>');
        }
    }

    /**
     * Assert that the given {@link Map} is empty.
     */
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public static void assertEmpty(String mapName, Map<?, ?> actualMap)
    {
        Verify.assertObjectNotNull(mapName, actualMap);

        if (!actualMap.isEmpty())
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.size() + '>');
        }
        if (actualMap.size() != 0)
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.size() + '>');
        }
        if (!actualMap.keySet().isEmpty())
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.keySet().size() + '>');
        }
        if (!actualMap.values().isEmpty())
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.values().size() + '>');
        }
        if (!actualMap.entrySet().isEmpty())
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.entrySet().size() + '>');
        }
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(Iterable<?> actualIterable)
    {
        Verify.assertNotEmpty("iterable", actualIterable);
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String iterableName, Iterable<?> actualIterable)
    {
        Verify.assertObjectNotNull(iterableName, actualIterable);
        assertFalse(iterableName + " should be non-empty, but was empty", Iterate.isEmpty(actualIterable));
        assertTrue(iterableName + " should be non-empty, but was empty", Iterate.notEmpty(actualIterable));
        assertNotEquals(iterableName + " should be non-empty, but was empty", 0, Iterate.sizeOf(actualIterable));
    }

    /**
     * Assert that the given {@link MutableMapIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        Verify.assertNotEmpty("mutableMapIterable", actualMutableMapIterable);
    }

    /**
     * Assert that the given {@link MutableMapIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String mutableMapIterableName, MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        Verify.assertObjectNotNull(mutableMapIterableName, actualMutableMapIterable);
        assertFalse(mutableMapIterableName + " should be non-empty, but was empty", Iterate.isEmpty(actualMutableMapIterable));
        assertTrue(mutableMapIterableName + " should be non-empty, but was empty", Iterate.notEmpty(actualMutableMapIterable));
        assertTrue(mutableMapIterableName + " should be non-empty, but was empty", actualMutableMapIterable.notEmpty());
        assertNotEquals(mutableMapIterableName + " should be non-empty, but was empty", 0, actualMutableMapIterable.size());
        assertNotEquals(mutableMapIterableName + " should be non-empty, but was empty", 0, actualMutableMapIterable.keySet().size());
        assertNotEquals(mutableMapIterableName + " should be non-empty, but was empty", 0, actualMutableMapIterable.values().size());
        assertNotEquals(mutableMapIterableName + " should be non-empty, but was empty", 0, actualMutableMapIterable.entrySet().size());
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(PrimitiveIterable primitiveIterable)
    {
        Verify.assertNotEmpty("primitiveIterable", primitiveIterable);
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String iterableName, PrimitiveIterable primitiveIterable)
    {
        Verify.assertObjectNotNull(iterableName, primitiveIterable);
        assertFalse(iterableName + " should be non-empty, but was empty", primitiveIterable.isEmpty());
        assertTrue(iterableName + " should be non-empty, but was empty", primitiveIterable.notEmpty());
        assertNotEquals(iterableName + " should be non-empty, but was empty", 0, primitiveIterable.size());
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertIterableNotEmpty(Iterable<?> iterable)
    {
        Verify.assertIterableNotEmpty("iterable", iterable);
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertIterableNotEmpty(String iterableName, Iterable<?> iterable)
    {
        Verify.assertObjectNotNull(iterableName, iterable);
        assertFalse(iterableName + " should be non-empty, but was empty", Iterate.isEmpty(iterable));
        assertTrue(iterableName + " should be non-empty, but was empty", Iterate.notEmpty(iterable));
        assertNotEquals(iterableName + " should be non-empty, but was empty", 0, Iterate.sizeOf(iterable));
    }

    /**
     * Assert that the given {@link Map} is <em>not</em> empty.
     */
    public static void assertNotEmpty(Map<?, ?> actualMap)
    {
        Verify.assertNotEmpty("map", actualMap);
    }

    /**
     * Assert that the given {@link Map} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String mapName, Map<?, ?> actualMap)
    {
        Verify.assertObjectNotNull(mapName, actualMap);
        assertFalse(mapName + " should be non-empty, but was empty", actualMap.isEmpty());
        assertNotEquals(mapName + " should be non-empty, but was empty", 0, actualMap.size());
        assertNotEquals(mapName + " should be non-empty, but was empty", 0, actualMap.keySet().size());
        assertNotEquals(mapName + " should be non-empty, but was empty", 0, actualMap.values().size());
        assertNotEquals(mapName + " should be non-empty, but was empty", 0, actualMap.entrySet().size());
    }

    /**
     * Assert that the given {@link Multimap} is <em>not</em> empty.
     */
    public static void assertNotEmpty(Multimap<?, ?> actualMultimap)
    {
        Verify.assertNotEmpty("multimap", actualMultimap);
    }

    /**
     * Assert that the given {@link Multimap} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String multimapName, Multimap<?, ?> actualMultimap)
    {
        Verify.assertObjectNotNull(multimapName, actualMultimap);
        assertTrue(multimapName + " should be non-empty, but was empty", actualMultimap.notEmpty());
        assertFalse(multimapName + " should be non-empty, but was empty", actualMultimap.isEmpty());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.sizeDistinct());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.keyBag().size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.keysView().size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.valuesView().size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.keyValuePairsView().size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.keyMultiValuePairsView().size());
    }

    public static <T> void assertNotEmpty(String itemsName, T[] items)
    {
        Verify.assertObjectNotNull(itemsName, items);
        assertNotEquals(itemsName, 0, items.length);
    }

    public static <T> void assertNotEmpty(T[] items)
    {
        Verify.assertNotEmpty("items", items);
    }

    /**
     * Assert the size of the given array.
     */
    public static void assertSize(int expectedSize, Object[] actualArray)
    {
        Verify.assertSize("array", expectedSize, actualArray);
    }

    /**
     * Assert the size of the given array.
     */
    public static void assertSize(String arrayName, int expectedSize, Object[] actualArray)
    {
        assertNotNull(arrayName + " should not be null", actualArray);

        int actualSize = actualArray.length;
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + arrayName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertSize(int expectedSize, Iterable<?> actualIterable)
    {
        Verify.assertSize("iterable", expectedSize, actualIterable);
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertSize(
            String iterableName,
            int expectedSize,
            Iterable<?> actualIterable)
    {
        Verify.assertObjectNotNull(iterableName, actualIterable);

        int actualSize = Iterate.sizeOf(actualIterable);
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + iterableName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link PrimitiveIterable}.
     */
    public static void assertSize(int expectedSize, PrimitiveIterable primitiveIterable)
    {
        Verify.assertSize("primitiveIterable", expectedSize, primitiveIterable);
    }

    /**
     * Assert the size of the given {@link PrimitiveIterable}.
     */
    public static void assertSize(
            String primitiveIterableName,
            int expectedSize,
            PrimitiveIterable actualPrimitiveIterable)
    {
        Verify.assertObjectNotNull(primitiveIterableName, actualPrimitiveIterable);

        int actualSize = actualPrimitiveIterable.size();
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + primitiveIterableName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertIterableSize(int expectedSize, Iterable<?> actualIterable)
    {
        Verify.assertIterableSize("iterable", expectedSize, actualIterable);
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertIterableSize(
            String iterableName,
            int expectedSize,
            Iterable<?> actualIterable)
    {
        Verify.assertObjectNotNull(iterableName, actualIterable);

        int actualSize = Iterate.sizeOf(actualIterable);
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + iterableName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link Map}.
     */
    public static void assertSize(String mapName, int expectedSize, Map<?, ?> actualMap)
    {
        Verify.assertSize(mapName, expectedSize, actualMap.keySet());
    }

    /**
     * Assert the size of the given {@link Map}.
     */
    public static void assertSize(int expectedSize, Map<?, ?> actualMap)
    {
        Verify.assertSize("map", expectedSize, actualMap);
    }

    /**
     * Assert the size of the given {@link Multimap}.
     */
    public static void assertSize(int expectedSize, Multimap<?, ?> actualMultimap)
    {
        Verify.assertSize("multimap", expectedSize, actualMultimap);
    }

    /**
     * Assert the size of the given {@link Multimap}.
     */
    public static void assertSize(String multimapName, int expectedSize, Multimap<?, ?> actualMultimap)
    {
        int actualSize = actualMultimap.size();
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + multimapName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link MutableMapIterable}.
     */
    public static void assertSize(int expectedSize, MutableMapIterable<?, ?> mutableMapIterable)
    {
        Verify.assertSize("map", expectedSize, mutableMapIterable);
    }

    /**
     * Assert the size of the given {@link MutableMapIterable}.
     */
    public static void assertSize(String mapName, int expectedSize, MutableMapIterable<?, ?> mutableMapIterable)
    {
        int actualSize = mutableMapIterable.size();
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for " + mapName + "; expected:<" + expectedSize + "> but was:<" + actualSize + '>');
        }
        int keySetSize = mutableMapIterable.keySet().size();
        if (keySetSize != expectedSize)
        {
            fail("Incorrect size for " + mapName + ".keySet(); expected:<" + expectedSize + "> but was:<" + actualSize + '>');
        }
        int valuesSize = mutableMapIterable.values().size();
        if (valuesSize != expectedSize)
        {
            fail("Incorrect size for " + mapName + ".values(); expected:<" + expectedSize + "> but was:<" + actualSize + '>');
        }
        int entrySetSize = mutableMapIterable.entrySet().size();
        if (entrySetSize != expectedSize)
        {
            fail("Incorrect size for " + mapName + ".entrySet(); expected:<" + expectedSize + "> but was:<" + actualSize + '>');
        }
    }

    /**
     * Assert the size of the given {@link ImmutableSet}.
     */
    public static void assertSize(int expectedSize, ImmutableSet<?> actualImmutableSet)
    {
        Verify.assertSize("immutable set", expectedSize, actualImmutableSet);
    }

    /**
     * Assert the size of the given {@link ImmutableSet}.
     */
    public static void assertSize(String immutableSetName, int expectedSize, ImmutableSet<?> actualImmutableSet)
    {
        int actualSize = actualImmutableSet.size();
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + immutableSetName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert that the given {@code stringToFind} is contained within the {@code stringToSearch}.
     */
    public static void assertContains(String stringToFind, String stringToSearch)
    {
        Verify.assertContains("string", stringToFind, stringToSearch);
    }

    /**
     * Assert that the given {@code unexpectedString} is <em>not</em> contained within the {@code stringToSearch}.
     */
    public static void assertNotContains(String unexpectedString, String stringToSearch)
    {
        Verify.assertNotContains("string", unexpectedString, stringToSearch);
    }

    /**
     * Assert that the given {@code stringToFind} is contained within the {@code stringToSearch}.
     */
    public static void assertContains(String stringName, String stringToFind, String stringToSearch)
    {
        assertNotNull("stringToFind should not be null", stringToFind);
        assertNotNull("stringToSearch should not be null", stringToSearch);

        if (!stringToSearch.contains(stringToFind))
        {
            fail(stringName
                    + " did not contain stringToFind:<"
                    + stringToFind
                    + "> in stringToSearch:<"
                    + stringToSearch
                    + '>');
        }
    }

    /**
     * Assert that the given {@code unexpectedString} is <em>not</em> contained within the {@code stringToSearch}.
     */
    public static void assertNotContains(String stringName, String unexpectedString, String stringToSearch)
    {
        assertNotNull("unexpectedString should not be null", unexpectedString);
        assertNotNull("stringToSearch should not be null", stringToSearch);

        if (stringToSearch.contains(unexpectedString))
        {
            fail(stringName
                    + " contains unexpectedString:<"
                    + unexpectedString
                    + "> in stringToSearch:<"
                    + stringToSearch
                    + '>');
        }
    }

    public static <T> void assertCount(
            int expectedCount,
            Iterable<T> iterable,
            Predicate<? super T> predicate)
    {
        assertEquals(expectedCount, Iterate.count(iterable, predicate));
    }

    public static <T> void assertAllSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        Verify.assertAllSatisfy("The following items failed to satisfy the condition", iterable, predicate);
    }

    public static <K, V> void assertAllSatisfy(Map<K, V> map, Predicate<? super V> predicate)
    {
        Verify.assertAllSatisfy(map.values(), predicate);
    }

    public static <T> void assertAllSatisfy(String message, Iterable<T> iterable, Predicate<? super T> predicate)
    {
        MutableList<T> unacceptable = Iterate.reject(iterable, predicate, Lists.mutable.of());
        if (unacceptable.notEmpty())
        {
            fail(message + " <" + unacceptable + '>');
        }
    }

    public static <T> void assertAnySatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        Verify.assertAnySatisfy("No items satisfied the condition", iterable, predicate);
    }

    public static <K, V> void assertAnySatisfy(Map<K, V> map, Predicate<? super V> predicate)
    {
        Verify.assertAnySatisfy(map.values(), predicate);
    }

    public static <T> void assertAnySatisfy(String message, Iterable<T> iterable, Predicate<? super T> predicate)
    {
        assertTrue(message, Predicates.<T>anySatisfy(predicate).accept(iterable));
    }

    public static <T> void assertNoneSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        Verify.assertNoneSatisfy("The following items satisfied the condition", iterable, predicate);
    }

    public static <K, V> void assertNoneSatisfy(Map<K, V> map, Predicate<? super V> predicate)
    {
        Verify.assertNoneSatisfy(map.values(), predicate);
    }

    public static <T> void assertNoneSatisfy(String message, Iterable<T> iterable, Predicate<? super T> predicate)
    {
        MutableList<T> unacceptable = Iterate.select(iterable, predicate, Lists.mutable.empty());
        if (unacceptable.notEmpty())
        {
            fail(message + " <" + unacceptable + '>');
        }
    }

    /**
     * Assert that the given {@link Map} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(Map<?, ?> actualMap, Object... keyValues)
    {
        Verify.assertContainsAllKeyValues("map", actualMap, keyValues);
    }

    /**
     * Assert that the given {@link Map} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String mapName,
            Map<?, ?> actualMap,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(mapName, actualMap);
        Verify.assertMapContainsKeys(mapName, actualMap, expectedKeyValues);
        Verify.assertMapContainsValues(mapName, actualMap, expectedKeyValues);
    }

    /**
     * Assert that the given {@link MapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(MapIterable<?, ?> mapIterable, Object... keyValues)
    {
        Verify.assertContainsAllKeyValues("map", mapIterable, keyValues);
    }

    /**
     * Assert that the given {@link MapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String mapIterableName,
            MapIterable<?, ?> mapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(mapIterableName, mapIterable);
        Verify.assertMapContainsKeys(mapIterableName, mapIterable, expectedKeyValues);
        Verify.assertMapContainsValues(mapIterableName, mapIterable, expectedKeyValues);
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(MutableMapIterable<?, ?> mutableMapIterable, Object... keyValues)
    {
        Verify.assertContainsAllKeyValues("map", mutableMapIterable, keyValues);
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String mutableMapIterableName,
            MutableMapIterable<?, ?> mutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(mutableMapIterableName, mutableMapIterable);
        Verify.assertMapContainsKeys(mutableMapIterableName, mutableMapIterable, expectedKeyValues);
        Verify.assertMapContainsValues(mutableMapIterableName, mutableMapIterable, expectedKeyValues);
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(ImmutableMapIterable<?, ?> immutableMapIterable, Object... keyValues)
    {
        Verify.assertContainsAllKeyValues("map", immutableMapIterable, keyValues);
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String immutableMapIterableName,
            ImmutableMapIterable<?, ?> immutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(immutableMapIterableName, immutableMapIterable);
        Verify.assertMapContainsKeys(immutableMapIterableName, immutableMapIterable, expectedKeyValues);
        Verify.assertMapContainsValues(immutableMapIterableName, immutableMapIterable, expectedKeyValues);
    }

    public static void denyContainsAny(Collection<?> actualCollection, Object... items)
    {
        Verify.denyContainsAny("collection", actualCollection, items);
    }

    public static void assertContainsNone(Collection<?> actualCollection, Object... items)
    {
        Verify.denyContainsAny("collection", actualCollection, items);
    }

    /**
     * Assert that the given {@link Collection} contains the given item.
     */
    public static void assertContains(Object expectedItem, Collection<?> actualCollection)
    {
        Verify.assertContains("collection", expectedItem, actualCollection);
    }

    /**
     * Assert that the given {@link Collection} contains the given item.
     */
    public static void assertContains(
            String collectionName,
            Object expectedItem,
            Collection<?> actualCollection)
    {
        Verify.assertObjectNotNull(collectionName, actualCollection);

        if (!actualCollection.contains(expectedItem))
        {
            fail(collectionName + " did not contain expectedItem:<" + expectedItem + '>');
        }
    }

    /**
     * Assert that the given {@link ImmutableCollection} contains the given item.
     */
    public static void assertContains(Object expectedItem, ImmutableCollection<?> actualImmutableCollection)
    {
        Verify.assertContains("ImmutableCollection", expectedItem, actualImmutableCollection);
    }

    /**
     * Assert that the given {@link ImmutableCollection} contains the given item.
     */
    public static void assertContains(
            String immutableCollectionName,
            Object expectedItem,
            ImmutableCollection<?> actualImmutableCollection)
    {
        Verify.assertObjectNotNull(immutableCollectionName, actualImmutableCollection);

        if (!actualImmutableCollection.contains(expectedItem))
        {
            fail(immutableCollectionName + " did not contain expectedItem:<" + expectedItem + '>');
        }
    }

    public static void assertContainsAll(
            Iterable<?> iterable,
            Object... items)
    {
        Verify.assertContainsAll("iterable", iterable, items);
    }

    public static void assertContainsAll(
            String collectionName,
            Iterable<?> iterable,
            Object... items)
    {
        Verify.assertObjectNotNull(collectionName, iterable);

        Verify.assertNotEmpty("Expected items in assertion", items);

        Predicate<Object> containsPredicate = each -> Iterate.contains(iterable, each);

        if (!ArrayIterate.allSatisfy(items, containsPredicate))
        {
            ImmutableList<Object> result = Lists.immutable.of(items).newWithoutAll(iterable);
            fail(collectionName + " did not contain these items" + ":<" + result + '>');
        }
    }

    public static void assertListsEqual(List<?> expectedList, List<?> actualList)
    {
        Verify.assertListsEqual("list", expectedList, actualList);
    }

    public static void assertListsEqual(String listName, List<?> expectedList, List<?> actualList)
    {
        if (expectedList == null && actualList == null)
        {
            return;
        }
        assertNotNull(expectedList);
        assertNotNull(actualList);
        assertEquals(listName + " size", expectedList.size(), actualList.size());
        for (int index = 0; index < actualList.size(); index++)
        {
            Object eachExpected = expectedList.get(index);
            Object eachActual = actualList.get(index);
            if (!Objects.equals(eachExpected, eachActual))
            {
                junit.framework.Assert.failNotEquals(listName + " first differed at element [" + index + "];", eachExpected, eachActual);
            }
        }
    }

    public static void assertSetsEqual(Set<?> expectedSet, Set<?> actualSet)
    {
        Verify.assertSetsEqual("set", expectedSet, actualSet);
    }

    public static void assertSetsEqual(String setName, Set<?> expectedSet, Set<?> actualSet)
    {
        if (expectedSet == null)
        {
            assertNull(setName + " should be null", actualSet);
            return;
        }

        Verify.assertObjectNotNull(setName, actualSet);
        Verify.assertSize(setName, expectedSet.size(), actualSet);

        if (!actualSet.equals(expectedSet))
        {
            MutableSet<?> inExpectedOnlySet = UnifiedSet.newSet(expectedSet);
            inExpectedOnlySet.removeAll(actualSet);

            int numberDifferences = inExpectedOnlySet.size();
            String message = setName + ": " + numberDifferences + " elements different.";

            if (numberDifferences > MAX_DIFFERENCES)
            {
                fail(message);
            }

            MutableSet<?> inActualOnlySet = UnifiedSet.newSet(actualSet);
            inActualOnlySet.removeAll(expectedSet);

            //noinspection UseOfObsoleteAssert
            junit.framework.Assert.failNotEquals(message, inExpectedOnlySet, inActualOnlySet);
        }
    }

    public static void assertSortedSetsEqual(SortedSet<?> expectedSet, SortedSet<?> actualSet)
    {
        Verify.assertSortedSetsEqual("sortedSets", expectedSet, actualSet);
    }

    public static void assertSortedSetsEqual(String setName, SortedSet<?> expectedSet, SortedSet<?> actualSet)
    {
        assertEquals(setName, expectedSet, actualSet);
        Verify.assertIterablesEqual(setName, expectedSet, actualSet);
    }

    public static void assertSortedBagsEqual(SortedBag<?> expectedBag, SortedBag<?> actualBag)
    {
        Verify.assertSortedBagsEqual("sortedBags", expectedBag, actualBag);
    }

    public static void assertSortedBagsEqual(String bagName, SortedBag<?> expectedBag, SortedBag<?> actualBag)
    {
        assertEquals(bagName, expectedBag, actualBag);
        Verify.assertIterablesEqual(bagName, expectedBag, actualBag);
    }

    public static void assertSortedMapsEqual(SortedMapIterable<?, ?> expectedMap, SortedMapIterable<?, ?> actualMap)
    {
        Verify.assertSortedMapsEqual("sortedMaps", expectedMap, actualMap);
    }

    public static void assertSortedMapsEqual(String mapName, SortedMapIterable<?, ?> expectedMap, SortedMapIterable<?, ?> actualMap)
    {
        assertEquals(mapName, expectedMap, actualMap);
        Verify.assertIterablesEqual(mapName, expectedMap, actualMap);
    }

    public static void assertIterablesEqual(Iterable<?> expectedIterable, Iterable<?> actualIterable)
    {
        Verify.assertIterablesEqual("iterables", expectedIterable, actualIterable);
    }

    public static void assertIterablesEqual(String iterableName, Iterable<?> expectedIterable, Iterable<?> actualIterable)
    {
        if (expectedIterable == null)
        {
            assertNull(iterableName + " should be null", actualIterable);
            return;
        }

        Verify.assertObjectNotNull(iterableName, actualIterable);

        if (expectedIterable instanceof InternalIterable<?> && actualIterable instanceof InternalIterable<?>)
        {
            MutableList<Object> expectedList = FastList.newList();
            MutableList<Object> actualList = FastList.newList();
            ((InternalIterable<?>) expectedIterable).forEach(CollectionAddProcedure.on(expectedList));
            ((InternalIterable<?>) actualIterable).forEach(CollectionAddProcedure.on(actualList));
            Verify.assertListsEqual(iterableName, expectedList, actualList);
        }
        else
        {
            Iterator<?> expectedIterator = expectedIterable.iterator();
            Iterator<?> actualIterator = actualIterable.iterator();
            int index = 0;

            while (expectedIterator.hasNext() && actualIterator.hasNext())
            {
                Object eachExpected = expectedIterator.next();
                Object eachActual = actualIterator.next();

                if (!Objects.equals(eachExpected, eachActual))
                {
                    //noinspection UseOfObsoleteAssert
                    junit.framework.Assert.failNotEquals(iterableName + " first differed at element [" + index + "];", eachExpected, eachActual);
                }
                index++;
            }

            assertFalse("Actual " + iterableName + " had " + index + " elements but expected " + iterableName + " had more.", expectedIterator.hasNext());
            assertFalse("Expected " + iterableName + " had " + index + " elements but actual " + iterableName + " had more.", actualIterator.hasNext());
        }
    }

    public static void assertMapsEqual(Map<?, ?> expectedMap, Map<?, ?> actualMap)
    {
        Verify.assertMapsEqual("map", expectedMap, actualMap);
    }

    public static void assertMapsEqual(String mapName, Map<?, ?> expectedMap, Map<?, ?> actualMap)
    {
        if (expectedMap == null)
        {
            assertNull(mapName + " should be null", actualMap);
            return;
        }

        assertNotNull(mapName + " should not be null", actualMap);

        Set<? extends Map.Entry<?, ?>> expectedEntries = expectedMap.entrySet();
        expectedEntries.forEach(expectedEntry ->
        {
            Object expectedKey = expectedEntry.getKey();
            Object expectedValue = expectedEntry.getValue();
            Object actualValue = actualMap.get(expectedKey);
            if (!Objects.equals(actualValue, expectedValue))
            {
                fail("Values differ at key " + expectedKey + " expected " + expectedValue + " but was " + actualValue);
            }
        });
        Verify.assertSetsEqual(mapName + " keys", expectedMap.keySet(), actualMap.keySet());
        Verify.assertSetsEqual(mapName + " entries", expectedMap.entrySet(), actualMap.entrySet());
    }

    public static void assertBagsEqual(Bag<?> expectedBag, Bag<?> actualBag)
    {
        Verify.assertBagsEqual("bag", expectedBag, actualBag);
    }

    public static void assertBagsEqual(String bagName, Bag<?> expectedBag, Bag<?> actualBag)
    {
        if (expectedBag == null)
        {
            assertNull(bagName + " should be null", actualBag);
            return;
        }

        assertNotNull(bagName + " should not be null", actualBag);

        assertEquals(bagName + " size", expectedBag.size(), actualBag.size());
        assertEquals(bagName + " sizeDistinct", expectedBag.sizeDistinct(), actualBag.sizeDistinct());

        expectedBag.forEachWithOccurrences((expectedKey, expectedValue) ->
        {
            int actualValue = actualBag.occurrencesOf(expectedKey);
            assertEquals("Occurrences of " + expectedKey, expectedValue, actualValue);
        });
    }

    public static <K, V> void assertListMultimapsEqual(ListMultimap<K, V> expectedListMultimap, ListMultimap<K, V> actualListMultimap)
    {
        Verify.assertListMultimapsEqual("ListMultimap", expectedListMultimap, actualListMultimap);
    }

    public static <K, V> void assertListMultimapsEqual(String multimapName, ListMultimap<K, V> expectedListMultimap, ListMultimap<K, V> actualListMultimap)
    {
        if (expectedListMultimap == null)
        {
            assertNull(multimapName + " should be null", actualListMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualListMultimap);

        assertEquals(multimapName + " size", expectedListMultimap.size(), actualListMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedListMultimap.keyBag(), actualListMultimap.keyBag());

        for (K key : expectedListMultimap.keysView())
        {
            Verify.assertListsEqual(multimapName + " value list for key:" + key, (List<V>) expectedListMultimap.get(key), (List<V>) actualListMultimap.get(key));
        }
        assertEquals(multimapName, expectedListMultimap, actualListMultimap);
    }

    public static <K, V> void assertSetMultimapsEqual(SetMultimap<K, V> expectedSetMultimap, SetMultimap<K, V> actualSetMultimap)
    {
        Verify.assertSetMultimapsEqual("SetMultimap", expectedSetMultimap, actualSetMultimap);
    }

    public static <K, V> void assertSetMultimapsEqual(String multimapName, SetMultimap<K, V> expectedSetMultimap, SetMultimap<K, V> actualSetMultimap)
    {
        if (expectedSetMultimap == null)
        {
            assertNull(multimapName + " should be null", actualSetMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualSetMultimap);

        assertEquals(multimapName + " size", expectedSetMultimap.size(), actualSetMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedSetMultimap.keyBag(), actualSetMultimap.keyBag());

        for (K key : expectedSetMultimap.keysView())
        {
            Verify.assertSetsEqual(multimapName + " value set for key:" + key, (Set<V>) expectedSetMultimap.get(key), (Set<V>) actualSetMultimap.get(key));
        }
        assertEquals(multimapName, expectedSetMultimap, actualSetMultimap);
    }

    public static <K, V> void assertBagMultimapsEqual(BagMultimap<K, V> expectedBagMultimap, BagMultimap<K, V> actualBagMultimap)
    {
        Verify.assertBagMultimapsEqual("BagMultimap", expectedBagMultimap, actualBagMultimap);
    }

    public static <K, V> void assertBagMultimapsEqual(String multimapName, BagMultimap<K, V> expectedBagMultimap, BagMultimap<K, V> actualBagMultimap)
    {
        if (expectedBagMultimap == null)
        {
            assertNull(multimapName + " should be null", actualBagMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualBagMultimap);

        assertEquals(multimapName + " size", expectedBagMultimap.size(), actualBagMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedBagMultimap.keyBag(), actualBagMultimap.keyBag());

        for (K key : expectedBagMultimap.keysView())
        {
            Verify.assertBagsEqual(multimapName + " value bag for key:" + key, expectedBagMultimap.get(key), actualBagMultimap.get(key));
        }
        assertEquals(multimapName, expectedBagMultimap, actualBagMultimap);
    }

    public static <K, V> void assertSortedSetMultimapsEqual(SortedSetMultimap<K, V> expectedSortedSetMultimap, SortedSetMultimap<K, V> actualSortedSetMultimap)
    {
        Verify.assertSortedSetMultimapsEqual("SortedSetMultimap", expectedSortedSetMultimap, actualSortedSetMultimap);
    }

    public static <K, V> void assertSortedSetMultimapsEqual(String multimapName, SortedSetMultimap<K, V> expectedSortedSetMultimap, SortedSetMultimap<K, V> actualSortedSetMultimap)
    {
        if (expectedSortedSetMultimap == null)
        {
            assertNull(multimapName + " should be null", actualSortedSetMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualSortedSetMultimap);

        assertEquals(multimapName + " size", expectedSortedSetMultimap.size(), actualSortedSetMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedSortedSetMultimap.keyBag(), actualSortedSetMultimap.keyBag());

        for (K key : expectedSortedSetMultimap.keysView())
        {
            Verify.assertSortedSetsEqual(multimapName + " value set for key:" + key, (SortedSet<V>) expectedSortedSetMultimap.get(key), (SortedSet<V>) actualSortedSetMultimap.get(key));
        }
        assertEquals(multimapName, expectedSortedSetMultimap, actualSortedSetMultimap);
    }

    public static <K, V> void assertSortedBagMultimapsEqual(SortedBagMultimap<K, V> expectedSortedBagMultimap, SortedBagMultimap<K, V> actualSortedBagMultimap)
    {
        Verify.assertSortedBagMultimapsEqual("SortedBagMultimap", expectedSortedBagMultimap, actualSortedBagMultimap);
    }

    public static <K, V> void assertSortedBagMultimapsEqual(String multimapName, SortedBagMultimap<K, V> expectedSortedBagMultimap, SortedBagMultimap<K, V> actualSortedBagMultimap)
    {
        if (expectedSortedBagMultimap == null)
        {
            assertNull(multimapName + " should be null", actualSortedBagMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualSortedBagMultimap);

        assertEquals(multimapName + " size", expectedSortedBagMultimap.size(), actualSortedBagMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedSortedBagMultimap.keyBag(), actualSortedBagMultimap.keyBag());

        for (K key : expectedSortedBagMultimap.keysView())
        {
            Verify.assertSortedBagsEqual(multimapName + " value set for key:" + key, expectedSortedBagMultimap.get(key), actualSortedBagMultimap.get(key));
        }
        assertEquals(multimapName, expectedSortedBagMultimap, actualSortedBagMultimap);
    }

    private static void assertMapContainsKeys(
            String mapName,
            Map<?, ?> actualMap,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedKeys = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            expectedKeys.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mapName + ".keySet()", actualMap.keySet(), expectedKeys.toArray());
    }

    private static void assertMapContainsValues(
            String mapName,
            Map<?, ?> actualMap,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableMap<Object, String> missingEntries = Maps.mutable.empty();
        int i = 0;
        while (i < expectedKeyValues.length)
        {
            Object expectedKey = expectedKeyValues[i++];
            Object expectedValue = expectedKeyValues[i++];
            Object actualValue = actualMap.get(expectedKey);
            if (!Objects.equals(expectedValue, actualValue))
            {
                missingEntries.put(
                        expectedKey,
                        "expectedValue:<" + expectedValue + ">, actualValue:<" + actualValue + '>');
            }
        }
        if (!missingEntries.isEmpty())
        {
            StringBuilder buf = new StringBuilder(mapName + " has incorrect values for keys:[");
            for (Map.Entry<Object, String> expectedEntry : missingEntries.entrySet())
            {
                buf.append("key:<")
                        .append(expectedEntry.getKey())
                        .append(',')
                        .append(expectedEntry.getValue())
                        .append("> ");
            }
            buf.append(']');
            fail(buf.toString());
        }
    }

    private static void assertMapContainsKeys(
            String mapIterableName,
            MapIterable<?, ?> mapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedKeys = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            expectedKeys.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mapIterableName + ".keysView()", mapIterable.keysView(), expectedKeys.toArray());
    }

    private static void assertMapContainsValues(
            String mapIterableName,
            MapIterable<?, ?> mapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedValues = Lists.mutable.of();
        for (int i = 1; i < expectedKeyValues.length; i += 2)
        {
            expectedValues.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mapIterableName + ".valuesView()", mapIterable.valuesView(), expectedValues.toArray());
    }

    private static void assertMapContainsKeys(
            String mutableMapIterableName,
            MutableMapIterable<?, ?> mutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedKeys = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            expectedKeys.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mutableMapIterableName + ".keysView()", mutableMapIterable.keysView(), expectedKeys.toArray());
    }

    private static void assertMapContainsValues(
            String mutableMapIterableName,
            MutableMapIterable<?, ?> mutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedValues = Lists.mutable.of();
        for (int i = 1; i < expectedKeyValues.length; i += 2)
        {
            expectedValues.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mutableMapIterableName + ".valuesView()", mutableMapIterable.valuesView(), expectedValues.toArray());
    }

    private static void assertMapContainsKeys(
            String immutableMapIterableName,
            ImmutableMapIterable<?, ?> immutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedKeys = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            expectedKeys.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(immutableMapIterableName + ".keysView()", immutableMapIterable.keysView(), expectedKeys.toArray());
    }

    private static void assertMapContainsValues(
            String immutableMapIterableName,
            ImmutableMapIterable<?, ?> immutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedValues = Lists.mutable.of();
        for (int i = 1; i < expectedKeyValues.length; i += 2)
        {
            expectedValues.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(immutableMapIterableName + ".valuesView()", immutableMapIterable.valuesView(), expectedValues.toArray());
    }

    /**
     * Assert that the given {@link Multimap} contains an entry with the given key and value.
     */
    public static <K, V> void assertContainsEntry(
            K expectedKey,
            V expectedValue,
            Multimap<K, V> actualMultimap)
    {
        Verify.assertContainsEntry("multimap", expectedKey, expectedValue, actualMultimap);
    }

    /**
     * Assert that the given {@link Multimap} contains an entry with the given key and value.
     */
    public static <K, V> void assertContainsEntry(
            String multimapName,
            K expectedKey,
            V expectedValue,
            Multimap<K, V> actualMultimap)
    {
        assertNotNull(multimapName, actualMultimap);

        if (!actualMultimap.containsKeyAndValue(expectedKey, expectedValue))
        {
            fail(multimapName + " did not contain entry: <" + expectedKey + ", " + expectedValue + '>');
        }
    }

    /**
     * Assert the given {@link Multimap} contains all the given keys and values.
     */
    public static void assertContainsAllEntries(Multimap<?, ?> actualMultimap, Object... keyValues)
    {
        Verify.assertContainsAllEntries("multimap", actualMultimap, keyValues);
    }

    /**
     * Assert the given {@link Multimap} contains all the given keys and values.
     */
    public static void assertContainsAllEntries(
            String multimapName,
            Multimap<?, ?> actualMultimap,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(multimapName, actualMultimap);

        MutableList<Map.Entry<?, ?>> missingEntries = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            Object expectedKey = expectedKeyValues[i];
            Object expectedValue = expectedKeyValues[i + 1];

            if (!actualMultimap.containsKeyAndValue(expectedKey, expectedValue))
            {
                missingEntries.add(new ImmutableEntry<>(expectedKey, expectedValue));
            }
        }

        if (!missingEntries.isEmpty())
        {
            fail(multimapName + " is missing entries: " + missingEntries);
        }
    }

    public static void denyContainsAny(
            String collectionName,
            Collection<?> actualCollection,
            Object... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        Verify.assertObjectNotNull(collectionName, actualCollection);

        MutableSet<Object> intersection = Sets.intersect(UnifiedSet.newSet(actualCollection), UnifiedSet.newSetWith(items));
        if (intersection.notEmpty())
        {
            fail(collectionName
                    + " has an intersection with these items and should not :<" + intersection + '>');
        }
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, Map<?, ?> actualMap)
    {
        Verify.assertContainsKey("map", expectedKey, actualMap);
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key.
     */
    public static void assertContainsKey(String mapName, Object expectedKey, Map<?, ?> actualMap)
    {
        assertNotNull(mapName, actualMap);

        if (!actualMap.containsKey(expectedKey))
        {
            fail(mapName + " did not contain expectedKey:<" + expectedKey + '>');
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, MapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKey("map", expectedKey, mapIterable);
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(
            String mapIterableName,
            Object expectedKey,
            MapIterable<?, ?> mapIterable)
    {
        assertNotNull(mapIterableName, mapIterable);

        if (!mapIterable.containsKey(expectedKey))
        {
            fail(mapIterableName + " did not contain expectedKey:<" + expectedKey + '>');
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, MutableMapIterable<?, ?> mutableMapIterable)
    {
        Verify.assertContainsKey("map", expectedKey, mutableMapIterable);
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(
            String mutableMapIterableName,
            Object expectedKey,
            MutableMapIterable<?, ?> mutableMapIterable)
    {
        assertNotNull(mutableMapIterableName, mutableMapIterable);

        if (!mutableMapIterable.containsKey(expectedKey))
        {
            fail(mutableMapIterableName + " did not contain expectedKey:<" + expectedKey + '>');
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, ImmutableMapIterable<?, ?> immutableMapIterable)
    {
        Verify.assertContainsKey("map", expectedKey, immutableMapIterable);
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(
            String immutableMapIterableName,
            Object expectedKey,
            ImmutableMapIterable<?, ?> immutableMapIterable)
    {
        assertNotNull(immutableMapIterableName, immutableMapIterable);

        if (!immutableMapIterable.containsKey(expectedKey))
        {
            fail(immutableMapIterableName + " did not contain expectedKey:<" + expectedKey + '>');
        }
    }

    /**
     * Deny that the given {@link Map} contains an entry with the given key.
     */
    public static void denyContainsKey(Object unexpectedKey, Map<?, ?> actualMap)
    {
        Verify.denyContainsKey("map", unexpectedKey, actualMap);
    }

    /**
     * Deny that the given {@link Map} contains an entry with the given key.
     */
    public static void denyContainsKey(String mapName, Object unexpectedKey, Map<?, ?> actualMap)
    {
        assertNotNull(mapName, actualMap);

        if (actualMap.containsKey(unexpectedKey))
        {
            fail(mapName + " contained unexpectedKey:<" + unexpectedKey + '>');
        }
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            Map<?, ?> actualMap)
    {
        Verify.assertContainsKeyValue("map", expectedKey, expectedValue, actualMap);
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapName,
            Object expectedKey,
            Object expectedValue,
            Map<?, ?> actualMap)
    {
        Verify.assertContainsKey(mapName, expectedKey, actualMap);

        Object actualValue = actualMap.get(expectedKey);
        if (!Objects.equals(actualValue, expectedValue))
        {
            fail(
                    mapName
                            + " entry with expectedKey:<"
                            + expectedKey
                            + "> "
                            + "did not contain expectedValue:<"
                            + expectedValue
                            + ">, "
                            + "but had actualValue:<"
                            + actualValue
                            + '>');
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            MapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKeyValue("map", expectedKey, expectedValue, mapIterable);
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapIterableName,
            Object expectedKey,
            Object expectedValue,
            MapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKey(mapIterableName, expectedKey, mapIterable);

        Object actualValue = mapIterable.get(expectedKey);
        if (!Objects.equals(actualValue, expectedValue))
        {
            fail(
                    mapIterableName
                            + " entry with expectedKey:<"
                            + expectedKey
                            + "> "
                            + "did not contain expectedValue:<"
                            + expectedValue
                            + ">, "
                            + "but had actualValue:<"
                            + actualValue
                            + '>');
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            MutableMapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKeyValue("map", expectedKey, expectedValue, mapIterable);
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapIterableName,
            Object expectedKey,
            Object expectedValue,
            MutableMapIterable<?, ?> mutableMapIterable)
    {
        Verify.assertContainsKey(mapIterableName, expectedKey, mutableMapIterable);

        Object actualValue = mutableMapIterable.get(expectedKey);
        if (!Objects.equals(actualValue, expectedValue))
        {
            fail(
                    mapIterableName
                            + " entry with expectedKey:<"
                            + expectedKey
                            + "> "
                            + "did not contain expectedValue:<"
                            + expectedValue
                            + ">, "
                            + "but had actualValue:<"
                            + actualValue
                            + '>');
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            ImmutableMapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKeyValue("map", expectedKey, expectedValue, mapIterable);
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapIterableName,
            Object expectedKey,
            Object expectedValue,
            ImmutableMapIterable<?, ?> immutableMapIterable)
    {
        Verify.assertContainsKey(mapIterableName, expectedKey, immutableMapIterable);

        Object actualValue = immutableMapIterable.get(expectedKey);
        if (!Objects.equals(actualValue, expectedValue))
        {
            fail(
                    mapIterableName
                            + " entry with expectedKey:<"
                            + expectedKey
                            + "> "
                            + "did not contain expectedValue:<"
                            + expectedValue
                            + ">, "
                            + "but had actualValue:<"
                            + actualValue
                            + '>');
        }
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(Object unexpectedItem, Collection<?> actualCollection)
    {
        Verify.assertNotContains("collection", unexpectedItem, actualCollection);
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(
            String collectionName,
            Object unexpectedItem,
            Collection<?> actualCollection)
    {
        Verify.assertObjectNotNull(collectionName, actualCollection);

        if (actualCollection.contains(unexpectedItem))
        {
            fail(collectionName + " should not contain unexpectedItem:<" + unexpectedItem + '>');
        }
    }

    /**
     * Assert that the given {@link Iterable} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(Object unexpectedItem, Iterable<?> iterable)
    {
        Verify.assertNotContains("iterable", unexpectedItem, iterable);
    }

    /**
     * Assert that the given {@link Iterable} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(
            String collectionName,
            Object unexpectedItem,
            Iterable<?> iterable)
    {
        Verify.assertObjectNotNull(collectionName, iterable);

        if (Iterate.contains(iterable, unexpectedItem))
        {
            fail(collectionName + " should not contain unexpectedItem:<" + unexpectedItem + '>');
        }
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContainsKey(Object unexpectedKey, Map<?, ?> actualMap)
    {
        Verify.assertNotContainsKey("map", unexpectedKey, actualMap);
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContainsKey(String mapName, Object unexpectedKey, Map<?, ?> actualMap)
    {
        Verify.assertObjectNotNull(mapName, actualMap);

        if (actualMap.containsKey(unexpectedKey))
        {
            fail(mapName + " should not contain unexpectedItem:<" + unexpectedKey + '>');
        }
    }

    /**
     * Assert that the formerItem appears before the latterItem in the given {@link Collection}.
     * Both the formerItem and the latterItem must appear in the collection, or this assert will fail.
     */
    public static void assertBefore(Object formerItem, Object latterItem, List<?> actualList)
    {
        Verify.assertBefore("list", formerItem, latterItem, actualList);
    }

    /**
     * Assert that the formerItem appears before the latterItem in the given {@link Collection}.
     * {@link #assertContains(String, Object, Collection)} will be called for both the formerItem and the
     * latterItem, prior to the "before" assertion.
     */
    public static void assertBefore(
            String listName,
            Object formerItem,
            Object latterItem,
            List<?> actualList)
    {
        Verify.assertObjectNotNull(listName, actualList);
        assertNotEquals(
                "Bad test, formerItem and latterItem are equal, listName:<" + listName + '>',
                formerItem,
                latterItem);
        Verify.assertContainsAll(listName, actualList, formerItem, latterItem);
        int formerPosition = actualList.indexOf(formerItem);
        int latterPosition = actualList.indexOf(latterItem);
        if (latterPosition < formerPosition)
        {
            fail("Items in "
                    + listName
                    + " are in incorrect order; "
                    + "expected formerItem:<"
                    + formerItem
                    + "> "
                    + "to appear before latterItem:<"
                    + latterItem
                    + ">, but didn't");
        }
    }

    public static void assertObjectNotNull(String objectName, Object actualObject)
    {
        assertNotNull(objectName + " should not be null", actualObject);
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@link List}.
     */
    public static void assertItemAtIndex(Object expectedItem, int index, List<?> list)
    {
        Verify.assertItemAtIndex("list", expectedItem, index, list);
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@code array}.
     */
    public static void assertItemAtIndex(Object expectedItem, int index, Object[] array)
    {
        Verify.assertItemAtIndex("array", expectedItem, index, array);
    }

    public static <T> void assertStartsWith(T[] array, T... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        for (int i = 0; i < items.length; i++)
        {
            T item = items[i];
            Verify.assertItemAtIndex("array", item, i, array);
        }
    }

    public static <T> void assertStartsWith(List<T> list, T... items)
    {
        Verify.assertStartsWith("list", list, items);
    }

    public static <T> void assertStartsWith(String listName, List<T> list, T... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        for (int i = 0; i < items.length; i++)
        {
            T item = items[i];
            Verify.assertItemAtIndex(listName, item, i, list);
        }
    }

    public static <T> void assertEndsWith(List<T> list, T... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        for (int i = 0; i < items.length; i++)
        {
            T item = items[i];
            Verify.assertItemAtIndex("list", item, list.size() - items.length + i, list);
        }
    }

    public static <T> void assertEndsWith(T[] array, T... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        for (int i = 0; i < items.length; i++)
        {
            T item = items[i];
            Verify.assertItemAtIndex("array", item, array.length - items.length + i, array);
        }
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@link List}.
     */
    public static void assertItemAtIndex(
            String listName,
            Object expectedItem,
            int index,
            List<?> list)
    {
        Verify.assertObjectNotNull(listName, list);

        Object actualItem = list.get(index);
        if (!Objects.equals(expectedItem, actualItem))
        {
            assertEquals(
                    listName + " has incorrect element at index:<" + index + '>',
                    expectedItem,
                    actualItem);
        }
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@link List}.
     */
    public static void assertItemAtIndex(
            String arrayName,
            Object expectedItem,
            int index,
            Object[] array)
    {
        assertNotNull(array);
        Object actualItem = array[index];
        if (!Objects.equals(expectedItem, actualItem))
        {
            assertEquals(
                    arrayName + " has incorrect element at index:<" + index + '>',
                    expectedItem,
                    actualItem);
        }
    }

    public static void assertPostSerializedEqualsAndHashCode(Object object)
    {
        Object deserialized = SerializeTestHelper.serializeDeserialize(object);
        Verify.assertEqualsAndHashCode("objects", object, deserialized);
        assertNotSame("not same object", object, deserialized);
    }

    public static void assertPostSerializedEqualsHashCodeAndToString(Object object)
    {
        Object deserialized = SerializeTestHelper.serializeDeserialize(object);
        Verify.assertEqualsAndHashCode("objects", object, deserialized);
        assertNotSame("not same object", object, deserialized);
        assertEquals("not same toString", object.toString(), deserialized.toString());
    }

    public static void assertPostSerializedIdentity(Object object)
    {
        Object deserialized = SerializeTestHelper.serializeDeserialize(object);
        Verify.assertEqualsAndHashCode("objects", object, deserialized);
        assertSame("same object", object, deserialized);
    }

    public static void assertSerializedForm(String expectedBase64Form, Object actualObject)
    {
        Verify.assertInstanceOf(Serializable.class, actualObject);
        assertEquals(
                "Serialization was broken.",
                expectedBase64Form,
                Verify.encodeObject(actualObject));
    }

    public static void assertSerializedForm(
            long expectedSerialVersionUID,
            String expectedBase64Form,
            Object actualObject)
    {
        Verify.assertInstanceOf(Serializable.class, actualObject);

        assertEquals(
                "Serialization was broken.",
                expectedBase64Form,
                Verify.encodeObject(actualObject));

        Object decodeToObject = Verify.decodeObject(expectedBase64Form);

        assertEquals(
                "serialVersionUID's differ",
                expectedSerialVersionUID,
                ObjectStreamClass.lookup(decodeToObject.getClass()).getSerialVersionUID());
    }

    public static void assertDeserializedForm(String expectedBase64Form, Object actualObject)
    {
        Verify.assertInstanceOf(Serializable.class, actualObject);

        Object decodeToObject = Verify.decodeObject(expectedBase64Form);
        assertEquals("Serialization was broken.", decodeToObject, actualObject);
    }

    private static Object decodeObject(String expectedBase64Form)
    {
        try
        {
            byte[] bytes = DECODER.decode(expectedBase64Form);
            return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new AssertionError(e);
        }
    }

    private static String encodeObject(Object actualObject)
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutput objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(actualObject);
            objectOutputStream.flush();
            objectOutputStream.close();

            String string = ENCODER.encodeToString(byteArrayOutputStream.toByteArray());
            return Verify.addFinalNewline(string);
        }
        catch (IOException e)
        {
            throw new AssertionError(e);
        }
    }

    private static String addFinalNewline(String string)
    {
        if (string.length() % 77 == 76)
        {
            return string + '\n';
        }
        return string;
    }

    public static void assertNotSerializable(Object actualObject)
    {
        Verify.assertThrows(NotSerializableException.class, () ->
        {
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(actualObject);
            return null;
        });
    }

    /**
     * Assert that {@code objectA} and {@code objectB} are equal via the {@link Object#equals(Object)} method,
     * and that they both return the same {@link Object#hashCode()}.
     */
    public static void assertEqualsAndHashCode(Object objectA, Object objectB)
    {
        Verify.assertEqualsAndHashCode("objects", objectA, objectB);
    }

    /**
     * Asserts that a value is negative.
     */
    public static void assertNegative(int value)
    {
        assertTrue(value + " is not negative", value < 0);
    }

    /**
     * Asserts that a value is positive.
     */
    public static void assertPositive(int value)
    {
        assertTrue(value + " is not positive", value > 0);
    }

    /**
     * Asserts that a value is positive.
     */
    public static void assertZero(int value)
    {
        assertEquals(0, value);
    }

    /**
     * Assert that {@code objectA} and {@code objectB} are equal (via the {@link Object#equals(Object)} method,
     * and that they both return the same {@link Object#hashCode()}.
     */
    public static void assertEqualsAndHashCode(String itemNames, Object objectA, Object objectB)
    {
        if (objectA == null || objectB == null)
        {
            fail("Neither item should be null: <" + objectA + "> <" + objectB + '>');
        }

        assertFalse("Neither item should equal null", objectA.equals(null));
        assertFalse("Neither item should equal null", objectB.equals(null));
        assertFalse("Neither item should equal new Object()", objectA.equals(new Object()));
        assertFalse("Neither item should equal new Object()", objectB.equals(new Object()));
        assertEquals("Expected " + itemNames + " to be equal.", objectA, objectA);
        assertEquals("Expected " + itemNames + " to be equal.", objectB, objectB);
        assertEquals("Expected " + itemNames + " to be equal.", objectA, objectB);
        assertEquals("Expected " + itemNames + " to be equal.", objectB, objectA);
        assertEquals(
                "Expected " + itemNames + " to have the same hashCode().",
                objectA.hashCode(),
                objectB.hashCode());
    }

    /**
     * @deprecated since 8.2.0 as will not work with Java 9
     */
    @Deprecated
    public static void assertShallowClone(Cloneable object)
    {
        Verify.assertShallowClone("object", object);
    }

    /**
     * @deprecated since 8.2.0 as will not work with Java 9
     */
    @Deprecated
    public static void assertShallowClone(String itemName, Cloneable object)
    {
        try
        {
            Method method = Object.class.getDeclaredMethod("clone", (Class<?>[]) null);
            method.setAccessible(true);
            Object clone = method.invoke(object);
            String prefix = itemName + " and its clone";
            assertNotSame(prefix, object, clone);
            Verify.assertEqualsAndHashCode(prefix, object, clone);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e)
        {
            throw new AssertionError(e.getLocalizedMessage());
        }
    }

    public static <T> void assertClassNonInstantiable(Class<T> aClass)
    {
        try
        {
            Constructor<T> declaredConstructor = aClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            declaredConstructor.newInstance();
            fail("Expected class '" + aClass + "' to be non-instantiable");
        }
        catch (InstantiationException | NoSuchMethodException e)
        {
            // pass
        }
        catch (IllegalAccessException e)
        {
            if (Verify.canInstantiateThroughReflection(aClass))
            {
                fail("Expected constructor of non-instantiable class '" + aClass + "' to throw an exception, but didn't");
            }
        }
        catch (InvocationTargetException e)
        {
            // pass - constructor threw an exception, which is expected for non-instantiable classes
        }
    }

    private static <T> boolean canInstantiateThroughReflection(Class<T> aClass)
    {
        try
        {
            Constructor<T> declaredConstructor = aClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            declaredConstructor.newInstance();
            return true;
        }
        catch (NoSuchMethodException | AssertionError | IllegalAccessException | InstantiationException | InvocationTargetException e)
        {
            return false;
        }
    }

    public static void assertError(Class<? extends Error> expectedErrorClass, Runnable code)
    {
        try
        {
            code.run();
        }
        catch (Error ex)
        {
            assertSame(
                    "Caught error of type <"
                            + ex.getClass().getName()
                            + ">, expected one of type <"
                            + expectedErrorClass.getName()
                            + '>',
                    expectedErrorClass,
                    ex.getClass());
            return;
        }

        fail("Block did not throw an error of type " + expectedErrorClass.getName());
    }

    /**
     * Runs the {@link Callable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}.
     * <p>
     * {@code Callable} is most appropriate when a checked exception will be thrown.
     * If a subclass of {@link RuntimeException} will be thrown, the form
     * {@link Assert#assertThrows(Class, org.junit.function.ThrowingRunnable)} may be more convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.<b>assertThrows</b>(StringIndexOutOfBoundsException.class, new Callable&lt;String&gt;()
     * {
     *    public String call() throws Exception
     *    {
     *        return "Craig".substring(42, 3);
     *    }
     * });
     * </pre>
     *
     * @see Assert#assertThrows(Class, org.junit.function.ThrowingRunnable)
     */
    public static void assertThrows(
            Class<? extends Exception> expectedExceptionClass,
            Callable<?> code)
    {
        try
        {
            code.call();
        }
        catch (Exception ex)
        {
            assertSame(
                    "Caught exception of type <"
                            + ex.getClass().getName()
                            + ">, expected one of type <"
                            + expectedExceptionClass.getName()
                            + '>'
                            + '\n'
                            + "Exception Message: " + ex.getMessage()
                            + '\n',
                    expectedExceptionClass,
                    ex.getClass());
            return;
        }

        fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
    }

    /**
     * Runs the {@link Callable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}, which contains a cause of type expectedCauseClass.
     * <p>
     * {@code Callable} is most appropriate when a checked exception will be thrown.
     * If a subclass of {@link RuntimeException} will be thrown, the form
     * {@link #assertThrowsWithCause(Class, Class, Runnable)} may be more convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, new Callable&lt;Void&gt;()
     * {
     *    public Void call() throws Exception
     *    {
     *        try
     *        {
     *            new File("").createNewFile();
     *        }
     *        catch (final IOException e)
     *        {
     *            throw new RuntimeException("Uh oh!", e);
     *        }
     *        return null;
     *    }
     * });
     * </pre>
     *
     * @see #assertThrowsWithCause(Class, Class, Runnable)
     */
    public static void assertThrowsWithCause(
            Class<? extends Exception> expectedExceptionClass,
            Class<? extends Throwable> expectedCauseClass,
            Callable<?> code)
    {
        try
        {
            code.call();
        }
        catch (Exception ex)
        {
            assertSame(
                    "Caught exception of type <"
                            + ex.getClass().getName()
                            + ">, expected one of type <"
                            + expectedExceptionClass.getName()
                            + '>',
                    expectedExceptionClass,
                    ex.getClass());
            Throwable actualCauseClass = ex.getCause();
            assertNotNull(
                    "Caught exception with null cause, expected cause of type <"
                            + expectedCauseClass.getName()
                            + '>',
                    actualCauseClass);
            assertSame(
                    "Caught exception with cause of type<"
                            + actualCauseClass.getClass().getName()
                            + ">, expected cause of type <"
                            + expectedCauseClass.getName()
                            + '>',
                    expectedCauseClass,
                    actualCauseClass.getClass());
            return;
        }

        fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
    }

    /**
     * Runs the {@link Runnable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}, which contains a cause of type expectedCauseClass.
     * <p>
     * {@code Runnable} is most appropriate when a subclass of {@link RuntimeException} will be thrown.
     * If a checked exception will be thrown, the form {@link #assertThrowsWithCause(Class, Class, Callable)}
     * may be more convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.assertThrowsWithCause(RuntimeException.class, StringIndexOutOfBoundsException.class, new Runnable()
     * {
     *    public void run()
     *    {
     *        try
     *        {
     *            LOGGER.info("Craig".substring(42, 3));
     *        }
     *        catch (final StringIndexOutOfBoundsException e)
     *        {
     *            throw new RuntimeException("Uh oh!", e);
     *        }
     *    }
     * });
     * </pre>
     *
     * @see #assertThrowsWithCause(Class, Class, Callable)
     */
    public static void assertThrowsWithCause(
            Class<? extends Exception> expectedExceptionClass,
            Class<? extends Throwable> expectedCauseClass,
            Runnable code)
    {
        try
        {
            code.run();
        }
        catch (RuntimeException ex)
        {
            assertSame(
                    "Caught exception of type <"
                            + ex.getClass().getName()
                            + ">, expected one of type <"
                            + expectedExceptionClass.getName()
                            + '>',
                    expectedExceptionClass,
                    ex.getClass());
            Throwable actualCauseClass = ex.getCause();
            assertNotNull(
                    "Caught exception with null cause, expected cause of type <"
                            + expectedCauseClass.getName()
                            + '>',
                    actualCauseClass);
            assertSame(
                    "Caught exception with cause of type<"
                            + actualCauseClass.getClass().getName()
                            + ">, expected cause of type <"
                            + expectedCauseClass.getName()
                            + '>',
                    expectedCauseClass,
                    actualCauseClass.getClass());
            return;
        }

        fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
    }
}
