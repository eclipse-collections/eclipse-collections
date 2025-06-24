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

import static org.junit.jupiter.api.Assertions.fail;

public final class SerializeTestHelper
{
    private SerializeTestHelper()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <T> T serializeDeserialize(T sourceObject)
    {
        byte[] pileOfBytes = serialize(sourceObject);
        return (T) deserialize(pileOfBytes);
    }

    public static <T> byte[] serialize(T sourceObject)
    {
        ByteArrayOutputStream baos = SerializeTestHelper.getByteArrayOutputStream(sourceObject);
        return baos.toByteArray();
    }

    public static <T> ByteArrayOutputStream getByteArrayOutputStream(T sourceObject)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            writeObjectToStream(sourceObject, baos);
        }
        catch (IOException e)
        {
            fail("Failed to marshal an object", e);
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

    public static Object deserialize(byte[] pileOfBytes)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(pileOfBytes);
        try
        {
            return readOneObject(bais);
        }
        catch (ClassNotFoundException | IOException e)
        {
            fail("Failed to unmarshal an object", e);
        }

        return null;
    }
}
