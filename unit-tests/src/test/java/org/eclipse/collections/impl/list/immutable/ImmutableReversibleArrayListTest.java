/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.immutable;

import org.junit.jupiter.api.Nested;

/**
 * JUnit test for {@link ImmutableArrayList}.
 */
public class ImmutableReversibleArrayListTest
{
    @Nested
    public class Ascending extends AbstractImmutableListTestCase
    {
        @Override
        protected ImmutableReversibleArrayList<Integer> classUnderTest()
        {
            return ImmutableReversibleArrayList.newListWith(1, 2, 3);
        }
    }

    @Nested
    public class Descending extends AbstractImmutableListTestCase
    {
        @Override
        protected ImmutableReversibleArrayList<Integer> classUnderTest()
        {
            return ImmutableReversibleArrayList.newListWith(3, 2, 1).toReversed();
        }
    }

    @Nested
    public class AscendingSub extends AbstractImmutableListTestCase
    {
        @Override
        protected ImmutableReversibleArrayList<Integer> classUnderTest()
        {
            return ImmutableReversibleArrayList.newListWith(0, 1, 2, 3, 4).subList(1, 4);
        }
    }

    @Nested
    public class DescendingSub extends AbstractImmutableListTestCase
    {
        @Override
        protected ImmutableReversibleArrayList<Integer> classUnderTest()
        {
            return ImmutableReversibleArrayList.newListWith(4, 3, 2, 1, 0).subList(1, 4).toReversed();
        }
    }
}
