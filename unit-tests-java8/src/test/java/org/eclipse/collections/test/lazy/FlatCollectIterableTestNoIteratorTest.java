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
import java.util.Objects;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.impl.lazy.FlatCollectIterable;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.test.LazyNoIteratorTestCase;
import org.eclipse.collections.test.list.mutable.FastListNoIterator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FlatCollectIterableTestNoIteratorTest implements LazyNoIteratorTestCase
{
    @Override
    public <T> LazyIterable<T> newWith(T... elements)
    {
        return new FlatCollectIterable<>(new FastListNoIterator<T>().with(elements), FastList::newListWith)
        {
            @Override
            public Iterator<T> iterator()
            {
                throw new UnsupportedOperationException("No iteration patterns should delegate to iterator()");
            }
        };
    }

    @Override
    @Test
    public void RichIterable_detectOptionalNull()
    {
        RichIterable<Integer> iterable1 = this.newWith(1, null, 3);
        assertThrows(NullPointerException.class, () -> iterable1.detectOptional(Objects::isNull));
        assertThrows(NullPointerException.class, () -> iterable1.detectWithOptional((i, object) -> i == object, null));

        RichIterable<Integer> iterable2 =
                new FlatCollectIterable<>(
                        new FastListNoIterator<Integer>().with(1, null, 3),
                        each -> (each == null) ? null : FastList.newListWith(each));
        assertThrows(NullPointerException.class, () -> iterable2.detectOptional(Objects::isNull));
        assertThrows(NullPointerException.class, () -> iterable2.detectWithOptional((i, object) -> i == object, null));

        RichIterable<Integer> iterable3 =
                new FlatCollectIterable<>(
                        new FastListNoIterator<String>().with("1", "null", "3"),
                        each -> FastList.newListWith("null".equals(each) ? null : Integer.valueOf(each)));
        assertThrows(NullPointerException.class, () -> iterable3.detectOptional(Objects::isNull));
        assertThrows(NullPointerException.class, () -> iterable3.detectWithOptional((i, object) -> i == object, null));
    }
}
