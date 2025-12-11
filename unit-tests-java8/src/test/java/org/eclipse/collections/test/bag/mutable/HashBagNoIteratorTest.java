/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.bag.mutable;

import java.util.Iterator;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.test.IterableTestCase;
import org.eclipse.collections.test.NoIteratorTestCase;
import org.junit.jupiter.api.Test;

public class HashBagNoIteratorTest implements MutableBagTestCase, NoIteratorTestCase
{
    @SafeVarargs
    @Override
    public final <T> MutableBag<T> newWith(T... elements)
    {
        MutableBag<T> result = new HashBagNoIterator<>();
        IterableTestCase.addAllTo(elements, result);
        return result;
    }

    @Override
    @Test
    public void Iterable_next()
    {
        NoIteratorTestCase.super.Iterable_next();
    }

    @Override
    @Test
    public void Iterable_remove()
    {
        NoIteratorTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    public void RichIterable_iterator_iterationOrder()
    {
        NoIteratorTestCase.super.RichIterable_iterator_iterationOrder();
    }

    @Override
    @Test
    public void RichIterable_getFirst()
    {
        NoIteratorTestCase.super.RichIterable_getFirst();
    }

    @Override
    @Test
    public void RichIterable_getLast()
    {
        NoIteratorTestCase.super.RichIterable_getLast();
    }

    public static class HashBagNoIterator<T> extends HashBag<T>
    {
        @Override
        public Iterator<T> iterator()
        {
            throw new AssertionError("No iteration patterns should delegate to iterator()");
        }
    }
}
