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

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.lazy.DistinctIterable;
import org.eclipse.collections.test.NoIteratorTestCase;
import org.eclipse.collections.test.RichIterableUniqueTestCase;
import org.eclipse.collections.test.list.TransformsToListTrait;
import org.eclipse.collections.test.list.mutable.FastListNoIterator;
import org.junit.jupiter.api.Test;

public class DistinctIterableTestNoIteratorTest implements NoIteratorTestCase, RichIterableUniqueTestCase, TransformsToListTrait
{
    @Override
    public <T> LazyIterable<T> newWith(T... elements)
    {
        return new DistinctIterable<>(new FastListNoIterator<T>().with(elements));
    }

    @Override
    public <T> ListIterable<T> getExpectedFiltered(T... elements)
    {
        return Lists.immutable.with(elements);
    }

    @Override
    public <T> MutableList<T> newMutableForFilter(T... elements)
    {
        return Lists.mutable.with(elements);
    }

    @Override
    public <T> ListIterable<T> getExpectedTransformed(T... elements)
    {
        return Lists.immutable.with(elements);
    }

    @Override
    @Test
    public void Iterable_sanity_check()
    {
        // Not applicable. DistinctIterable wraps an instance that does have duplicates and behaves like it has no duplicates.
    }

    @Override
    @Test
    public void Object_PostSerializedEqualsAndHashCode()
    {
        // Not applicable
    }

    @Override
    @Test
    public void Object_equalsAndHashCode()
    {
        // Not applicable
    }
}
