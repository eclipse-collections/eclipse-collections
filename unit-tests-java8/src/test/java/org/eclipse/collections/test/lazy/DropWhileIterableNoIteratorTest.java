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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.lazy.DropWhileIterable;
import org.eclipse.collections.test.IterableTestCase;
import org.eclipse.collections.test.LazyNoIteratorTestCase;
import org.eclipse.collections.test.list.mutable.FastListNoIterator;

public class DropWhileIterableNoIteratorTest implements LazyNoIteratorTestCase
{
    @Override
    public <T> LazyIterable<T> newWith(T... elements)
    {
        MutableList<T> list = new FastListNoIterator<>();
        T object = (T) new Object();
        list.add(object);
        IterableTestCase.addAllTo(elements, list);
        return new DropWhileIterable<>(list, each -> each == object)
        {
            @Override
            public Iterator<T> iterator()
            {
                throw new UnsupportedOperationException("No iteration patterns should delegate to iterator()");
            }
        };
    }
}
