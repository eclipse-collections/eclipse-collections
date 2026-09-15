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
import org.eclipse.collections.impl.lazy.DistinctIterable;
import org.eclipse.collections.test.LazyNoIteratorTestCase;
import org.eclipse.collections.test.RichIterableUniqueTestCase;
import org.eclipse.collections.test.list.mutable.FastListNoIterator;
import org.junit.jupiter.api.Test;

public class DistinctIterableTestNoIteratorTest implements LazyNoIteratorTestCase, RichIterableUniqueTestCase
{
    @Override
    public <T> LazyIterable<T> newWith(T... elements)
    {
        return new DistinctIterable<>(new FastListNoIterator<T>().with(elements));
    }

    @Override
    public boolean allowsDuplicates()
    {
        // DistinctIterable deduplicates its input
        return false;
    }

    @Override
    @Test
    public void Iterable_sanity_check()
    {
        // DistinctIterable wraps an instance that has duplicates and behaves like it has no duplicates,
        // so neither parent's sanity check applies; just verify construction doesn't throw.
        String s = "";
        this.newWith(s, s);
    }
}
