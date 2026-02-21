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
import java.util.Optional;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.lazy.SelectInstancesOfIterable;
import org.eclipse.collections.test.LazyNoIteratorTestCase;
import org.eclipse.collections.test.NoDetectOptionalNullTestCase;
import org.eclipse.collections.test.list.mutable.FastListNoIterator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SelectInstancesOfIterableTestNoIteratorTest implements LazyNoIteratorTestCase, NoDetectOptionalNullTestCase
{
    @Override
    public <T> LazyIterable<T> newWith(T... elements)
    {
        return (LazyIterable<T>) new SelectInstancesOfIterable<>(
                new FastListNoIterator<T>().with(elements),
                Object.class)
        {
            @Override
            public Iterator<Object> iterator()
            {
                throw new UnsupportedOperationException("No iteration patterns should delegate to iterator()");
            }
        };
    }

    @Override
    @Test
    public void RichIterable_minOptional_maxOptional()
    {
        assertEquals(Optional.of(-1), this.newWith(-1, 0, 1).minOptional());
        assertEquals(Optional.of(-1), this.newWith(1, 0, -1).minOptional());
        assertSame(Optional.empty(), this.newWith().minOptional());
        assertSame(Optional.empty(), this.newWith(new Object[]{null}).minOptional());

        assertEquals(Optional.of(1), this.newWith(-1, 0, 1).maxOptional());
        assertEquals(Optional.of(1), this.newWith(1, 0, -1).maxOptional());
        assertSame(Optional.empty(), this.newWith().maxOptional());
        assertSame(Optional.empty(), this.newWith(new Object[]{null}).maxOptional());

        assertEquals(Optional.of(1), this.newWith(-1, 0, 1).minOptional(Comparators.reverseNaturalOrder()));
        assertEquals(Optional.of(1), this.newWith(1, 0, -1).minOptional(Comparators.reverseNaturalOrder()));
        assertSame(Optional.empty(), this.newWith().minOptional(Comparators.reverseNaturalOrder()));
        assertSame(Optional.empty(), this.newWith(new Object[]{null}).minOptional(Comparators.reverseNaturalOrder()));

        assertEquals(Optional.of(-1), this.newWith(-1, 0, 1).maxOptional(Comparators.reverseNaturalOrder()));
        assertEquals(Optional.of(-1), this.newWith(1, 0, -1).maxOptional(Comparators.reverseNaturalOrder()));
        assertSame(Optional.empty(), this.newWith().maxOptional(Comparators.reverseNaturalOrder()));
        assertSame(Optional.empty(), this.newWith(new Object[]{null}).maxOptional(Comparators.reverseNaturalOrder()));
    }

    @Override
    @Test
    public void RichIterable_minByOptional_maxByOptional()
    {
        assertEquals(Optional.of("da"), this.newWith("ed", "da", "ca", "bc", "ab").minByOptional(string -> string.charAt(string.length() - 1)));
        assertSame(Optional.empty(), this.<String>newWith().minByOptional(string -> string.charAt(string.length() - 1)));
        assertSame(Optional.empty(), this.newWith(new Object[]{null}).minByOptional(Objects::isNull));

        assertEquals(Optional.of("dz"), this.newWith("ew", "dz", "cz", "bx", "ay").maxByOptional(string -> string.charAt(string.length() - 1)));
        assertSame(Optional.empty(), this.<String>newWith().maxByOptional(string -> string.charAt(string.length() - 1)));
        assertSame(Optional.empty(), this.newWith(new Object[]{null}).maxByOptional(Objects::isNull));
    }
}
