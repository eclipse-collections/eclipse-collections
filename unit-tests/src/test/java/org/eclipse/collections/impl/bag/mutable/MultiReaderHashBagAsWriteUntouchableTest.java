/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.collection.mutable.AbstractCollectionTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiReaderHashBagAsWriteUntouchableTest extends AbstractCollectionTestCase
{
    @Override
    protected <T> MutableBag<T> newWith(T... littleElements)
    {
        return MultiReaderHashBag.newBagWith(littleElements).asWriteUntouchable();
    }

    @Override
    @Test
    public void asSynchronized()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().asUnmodifiable());
    }

    @Test
    public void addOccurrences()
    {
        MutableBag<Integer> bag = this.newWith(1, 1);
        assertEquals(4, bag.addOccurrences(1, 2));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 1, 1), bag);
        assertEquals(0, bag.addOccurrences(2, 0));
        assertEquals(2, bag.addOccurrences(2, 2));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 1, 1, 2, 2), bag);
    }

    @Override
    @Test
    public void makeString()
    {
        assertEquals("[1, 1, 2, 3]", MultiReaderHashBag.newBagWith(1, 1, 2, 3).toString());
    }

    @Override
    @Test
    public void appendString()
    {
        Appendable builder = new StringBuilder();
        MultiReaderHashBag.newBagWith(1, 1, 2, 3).appendString(builder);
        assertEquals("1, 1, 2, 3", builder.toString());
    }

    @Override
    @Test
    public void testToString()
    {
        assertEquals("[1, 1, 2, 3]", MultiReaderHashBag.newBagWith(1, 1, 2, 3).toString());
    }

    @Test
    public void selectUnique()
    {
        MutableBag<String> bag = this.newWith("0", "1", "1", "1", "1", "2", "2", "2", "3", "3", "4", "5");
        MutableSet<String> expected = Sets.mutable.with("0", "4", "5");
        MutableSet<String> actual = bag.selectUnique();
        assertEquals(expected, actual);
    }

    @Test
    public void distinctView()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.newWith().distinctView());
    }
}
