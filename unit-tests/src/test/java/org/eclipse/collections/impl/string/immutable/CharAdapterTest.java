/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.string.immutable;

import org.eclipse.collections.api.LazyCharIterable;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.impl.factory.Strings;
import org.eclipse.collections.impl.factory.primitive.CharBags;
import org.eclipse.collections.impl.list.immutable.primitive.AbstractImmutableCharListTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CharAdapterTest extends AbstractImmutableCharListTestCase
{
    private static final String UNICODE_STRING = "\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09";

    @Override
    protected ImmutableCharList classUnderTest()
    {
        return CharAdapter.from((char) 1, (char) 2, (char) 3);
    }

    @Override
    protected ImmutableCharList newWith(char... elements)
    {
        return CharAdapter.from(elements);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Test
    public void stringBuilder()
    {
        CharAdapter adapt = CharAdapter.adapt(UNICODE_STRING);
        assertEquals(UNICODE_STRING, new StringBuilder(adapt).toString());
    }

    @Test
    public void subSequence()
    {
        CharAdapter adapt = CharAdapter.adapt(UNICODE_STRING);
        CharSequence sequence = adapt.subSequence(1, 3);
        assertEquals(UNICODE_STRING.subSequence(1, 3), sequence);
    }

    @Override
    @Test
    public void toBag()
    {
        super.toBag();
        MutableCharBag expected = CharBags.mutable.empty();
        expected.addOccurrences('a', 3);
        expected.addOccurrences('b', 3);
        expected.addOccurrences('c', 3);
        assertEquals(expected, CharAdapter.adapt("aaabbbccc").toBag());
    }

    @Override
    @Test
    public void makeString()
    {
        ImmutableCharList list = this.classUnderTest();
        StringBuilder expectedString = new StringBuilder();
        StringBuilder expectedString1 = new StringBuilder();
        int size = list.size();
        for (char each = 0; each < size; each++)
        {
            expectedString.append((char) (each + (char) 1));
            expectedString1.append((char) (each + (char) 1));
            expectedString.append(each == size - 1 ? "" : ", ");
            expectedString1.append(each == size - 1 ? "" : "/");
        }
        assertEquals(expectedString.toString(), list.makeString());
        assertEquals(expectedString1.toString(), list.makeString("/"));
    }

    @Override
    @Test
    public void appendString()
    {
        StringBuilder expectedString = new StringBuilder();
        StringBuilder expectedString1 = new StringBuilder();
        int size = this.classUnderTest().size();
        for (char each = 0; each < size; each++)
        {
            expectedString.append((char) (each + (char) 1));
            expectedString1.append((char) (each + (char) 1));
            expectedString.append(each == size - 1 ? "" : ", ");
            expectedString1.append(each == size - 1 ? "" : "/");
        }
        ImmutableCharList list = this.classUnderTest();
        StringBuilder appendable2 = new StringBuilder();
        list.appendString(appendable2);
        assertEquals(expectedString.toString(), appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        list.appendString(appendable3, "/");
        assertEquals(expectedString1.toString(), appendable3.toString());
    }

    @Override
    @Test
    public void testToString()
    {
        StringBuilder expectedString = new StringBuilder();
        int size = this.classUnderTest().size();
        for (char each = 0; each < size; each++)
        {
            expectedString.append((char) (each + (char) 1));
        }
        assertEquals(expectedString.toString(), this.classUnderTest().toString());
    }

    @Test
    public void getCharacter()
    {
        CharAdapter adapter = Strings.asChars("123");
        assertEquals(Character.valueOf('1'), adapter.getCharacter(0));
        assertEquals(Character.valueOf('2'), adapter.getCharacter(1));
        assertEquals(Character.valueOf('3'), adapter.getCharacter(2));
    }

    @Test
    public void toImmutable()
    {
        CharAdapter adapter = Strings.asChars("123");
        ImmutableCharList immutable = adapter.toImmutable();
        assertSame(adapter, immutable);
    }

    @Test
    public void asReversed()
    {
        CharAdapter adapter = Strings.asChars("123");
        LazyCharIterable iterable = adapter.asReversed();
        String string = iterable.makeString("");
        assertEquals("321", string);
    }

    @Test
    public void dotProduct()
    {
        CharAdapter adapter = Strings.asChars("123");
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                {
                    adapter.dotProduct(adapter);
                });
    }

    @Test
    public void binarySearch()
    {
        CharAdapter adapter = Strings.asChars("123");
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                {
                    adapter.binarySearch('2');
                });
    }
}
