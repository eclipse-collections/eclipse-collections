/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.list.mutable;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.test.IterableTestCase;
import org.eclipse.collections.test.NoIteratorTestCase;
import org.junit.jupiter.api.Test;

public class FastListNoIteratorTest implements MutableListTestCase, NoIteratorTestCase
{
    @SafeVarargs
    @Override
    public final <T> MutableList<T> newWith(T... elements)
    {
        MutableList<T> result = new FastListNoIterator<>();
        IterableTestCase.addAllTo(elements, result);
        return result;
    }

    @Override
    @Test
    public void Iterable_remove()
    {
        NoIteratorTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    public void List_subList_subList_iterator_add_remove()
    {
        // Not applicable
    }

    @Override
    @Test
    public void OrderedIterable_next()
    {
        // Not applicable
    }
}
