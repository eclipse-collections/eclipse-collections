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
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * A utility class providing helper methods for testing Java object serialization and deserialization.
 * This class contains methods to serialize objects to byte arrays and deserialize them back to objects,
 * useful for validating that custom objects properly implement {@link java.io.Serializable}.
 * <p>
 * This class is designed for use in unit tests to verify that objects can be correctly serialized
 * and deserialized, maintaining their state and structure through the serialization process.
 * </p>
 *
 * @since 1.0
 */
public final class SerializeTestHelper
{
    private SerializeTestHelper()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Serializes and then deserializes the given object, returning the deserialized copy.
     * This method is useful for testing that an object can survive a complete serialization
     * round-trip without losing data or structure.
     *
     * @param sourceObject the object to serialize and deserialize
     * @param <T>          the type of the object
     * @return a deserialized copy of the source object
     * @throws AssertionError if serialization or deserialization fails
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Test that a list survives serialization
     * MutableList<String> original = Lists.mutable.of("a", "b", "c");
     * MutableList<String> copy = SerializeTestHelper.serializeDeserialize(original);
     * Verify.assertListsEqual(original, copy);
     *
     * // Test that a map survives serialization
     * MutableMap<String, Integer> originalMap = Maps.mutable.of("one", 1, "two", 2);
     * MutableMap<String, Integer> copyMap = SerializeTestHelper.serializeDeserialize(originalMap);
     * Verify.assertMapsEqual(originalMap, copyMap);
     * }</pre>
     */
    public static <T> T serializeDeserialize(T sourceObject)
    {
        byte[] pileOfBytes = serialize(sourceObject);
        return (T) deserialize(pileOfBytes);
    }

    /**
     * Serializes the given object to a byte array using Java object serialization.
     * This method converts the object into its serialized byte representation, which can
     * be stored or transmitted and later deserialized back into an object.
     *
     * @param sourceObject the object to serialize
     * @param <T>          the type of the object
     * @return a byte array containing the serialized object data
     * @throws AssertionError if serialization fails (e.g., if the object is not serializable)
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Serialize a collection to bytes
     * MutableSet<Integer> set = Sets.mutable.of(1, 2, 3);
     * byte[] serialized = SerializeTestHelper.serialize(set);
     * Verify.assertNotEmpty(serialized);
     *
     * // Serialize and check byte array size
     * String text = "Hello World";
     * byte[] bytes = SerializeTestHelper.serialize(text);
     * Assert.assertTrue(bytes.length > 0);
     * }</pre>
     */
    public static <T> byte[] serialize(T sourceObject)
    {
        ByteArrayOutputStream baos = SerializeTestHelper.getByteArrayOutputStream(sourceObject);
        return baos.toByteArray();
    }

    /**
     * Serializes the given object and returns a ByteArrayOutputStream containing the serialized data.
     * This method provides direct access to the output stream, which can be useful for advanced
     * serialization testing scenarios where stream manipulation is needed.
     *
     * @param sourceObject the object to serialize
     * @param <T>          the type of the object
     * @return a ByteArrayOutputStream containing the serialized object data
     * @throws AssertionError if serialization fails (e.g., if the object is not serializable)
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Get the output stream for custom processing
     * MutableList<String> list = Lists.mutable.of("x", "y", "z");
     * ByteArrayOutputStream stream = SerializeTestHelper.getByteArrayOutputStream(list);
     * byte[] bytes = stream.toByteArray();
     * Assert.assertTrue(bytes.length > 0);
     *
     * // Check stream size
     * Integer value = 42;
     * ByteArrayOutputStream output = SerializeTestHelper.getByteArrayOutputStream(value);
     * Assert.assertTrue(output.size() > 0);
     * }</pre>
     */
    public static <T> ByteArrayOutputStream getByteArrayOutputStream(T sourceObject)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            writeObjectToStream(sourceObject, baos);
        }
        catch (IOException e)
        {
            Verify.fail("Failed to marshal an object", e);
        }
        return baos;
    }

    private static <T> void writeObjectToStream(Object sourceObject, ByteArrayOutputStream baos) throws IOException
    {
        try (ObjectOutput objectOutputStream = new ObjectOutputStream(baos))
        {
            objectOutputStream.writeObject(sourceObject);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
    }

    private static Object readOneObject(ByteArrayInputStream bais)
            throws IOException, ClassNotFoundException
    {
        try (ObjectInput objectStream = new ObjectInputStream(bais))
        {
            return objectStream.readObject();
        }
    }

    /**
     * Deserializes an object from the given byte array using Java object deserialization.
     * This method takes a byte array containing serialized object data and reconstructs
     * the original object from it.
     *
     * @param pileOfBytes the byte array containing the serialized object data
     * @return the deserialized object
     * @throws AssertionError if deserialization fails (e.g., if the data is corrupted or incompatible)
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Deserialize a previously serialized object
     * MutableList<String> original = Lists.mutable.of("a", "b", "c");
     * byte[] bytes = SerializeTestHelper.serialize(original);
     * MutableList<String> restored = (MutableList<String>) SerializeTestHelper.deserialize(bytes);
     * Verify.assertListsEqual(original, restored);
     *
     * // Test round-trip serialization
     * MutableMap<Integer, String> map = Maps.mutable.of(1, "one", 2, "two");
     * byte[] serialized = SerializeTestHelper.serialize(map);
     * MutableMap<Integer, String> deserialized = (MutableMap<Integer, String>) SerializeTestHelper.deserialize(serialized);
     * Assert.assertEquals(map, deserialized);
     * }</pre>
     */
    public static Object deserialize(byte[] pileOfBytes)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(pileOfBytes);
        try
        {
            return readOneObject(bais);
        }
        catch (ClassNotFoundException | IOException e)
        {
            Verify.fail("Failed to unmarshal an object", e);
        }

        return null;
    }
}
