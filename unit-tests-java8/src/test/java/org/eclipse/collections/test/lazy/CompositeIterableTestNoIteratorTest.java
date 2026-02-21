/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.lazy;

import java.util.Iterator;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.lazy.CompositeIterable;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.test.LazyNoIteratorTestCase;
import org.eclipse.collections.test.list.mutable.FastListNoIterator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompositeIterableTestNoIteratorTest implements LazyNoIteratorTestCase
{
    @Override
    public <T> LazyIterable<T> newWith(T... elements)
    {
        RichIterable<MutableList<T>> noIteratorLists = ArrayIterate.chunk(elements, 3).collect(chunk -> new FastListNoIterator<T>().withAll(chunk));
        Iterable<T>[] iterables = (Iterable<T>[]) noIteratorLists.toArray(new Iterable[]{});
        return CompositeIterable.with(iterables);
    }

    @Override
    @Test
    public void Iterable_remove()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        assertThrows(
                AssertionError.class, () -> {
                    Iterator<Integer> iterator = iterable.iterator();
                    iterator.next();
                    iterator.remove();
                });
    }

    @Override
    public void Iterable_hasNext()
    {
        assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator().hasNext());
    }

    @Override
    public void Iterable_next()
    {
        assertThrows(AssertionError.class, () -> this.newWith(3, 2, 1).iterator().next());
    }
}
