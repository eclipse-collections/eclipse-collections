/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure.checked;

import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Iterator;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultimapKeyValuesSerializingProcedureTest
{
    @Test
    public void testSerialization()
    {
        FastListMultimap<String, String> map = new FastListMultimap<>();
        map.put("A", "alpha");
        map.put("A", "beta");
        FastList<? extends Serializable> expectedWrites = FastList.newListWith("A", 2, "alpha", "beta");
        Procedure2<String, RichIterable<String>> procedure =
                new MultimapKeyValuesSerializingProcedure<>(new MockObjectOutput(expectedWrites));
        map.toMap().forEachKeyValue(procedure);
    }

    public static final class MockObjectOutput implements ObjectOutput
    {
        private final Iterator<? extends Serializable> iterator;

        public MockObjectOutput(Iterable<? extends Serializable> expectedWrites)
        {
            this.iterator = expectedWrites.iterator();
        }

        @Override
        public void writeObject(Object obj)
        {
            assertEquals(this.iterator.next(), obj);
        }

        @Override
        public void writeInt(int v)
        {
            assertEquals(this.iterator.next(), v);
        }

        @Override
        public void write(int i)
        {
            throw new RuntimeException("write not implemented");
        }

        @Override
        public void write(byte[] bs)
        {
            throw new RuntimeException("write not implemented");
        }

        @Override
        public void write(byte[] bs, int off, int len)
        {
            throw new RuntimeException("write not implemented");
        }

        @Override
        public void writeBoolean(boolean v)
        {
            throw new RuntimeException("writeBoolean not implemented");
        }

        @Override
        public void writeByte(int v)
        {
            throw new RuntimeException("writeByte not implemented");
        }

        @Override
        public void writeShort(int v)
        {
            throw new RuntimeException("writeShort not implemented");
        }

        @Override
        public void writeChar(int v)
        {
            throw new RuntimeException("writeChar not implemented");
        }

        @Override
        public void writeLong(long v)
        {
            throw new RuntimeException("writeLong not implemented");
        }

        @Override
        public void writeFloat(float v)
        {
            throw new RuntimeException("writeFloat not implemented");
        }

        @Override
        public void writeDouble(double v)
        {
            throw new RuntimeException("writeDouble not implemented");
        }

        @Override
        public void writeBytes(String s)
        {
            throw new RuntimeException("writeBytes not implemented");
        }

        @Override
        public void writeChars(String s)
        {
            throw new RuntimeException("writeChars not implemented");
        }

        @Override
        public void writeUTF(String s)
        {
            throw new RuntimeException("writeUTF not implemented");
        }

        @Override
        @Test
        public void flush()
        {
            throw new RuntimeException("flush not implemented");
        }

        @Override
        public void close()
        {
            throw new RuntimeException("close not implemented");
        }
    }
}
