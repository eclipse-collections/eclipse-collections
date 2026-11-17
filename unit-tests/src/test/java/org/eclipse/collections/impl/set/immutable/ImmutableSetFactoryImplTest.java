/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.immutable;

import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ImmutableSetFactoryImplTest
{
    @Test
    public void withAllEmpty()
    {
        assertEquals(Sets.immutable.empty(), Sets.immutable.withAll(Lists.mutable.empty()));
    }

    @Test
    public void withAllCollapsesDuplicatesIntoSmallImmutableSets()
    {
        ImmutableSet<Integer> singleton = Sets.immutable.withAll(Lists.mutable.with(1, 1, 1));
        Verify.assertInstanceOf(ImmutableSingletonSet.class, singleton);
        assertEquals(Sets.immutable.with(1), singleton);

        ImmutableSet<Integer> doubleton = Sets.immutable.withAll(Lists.mutable.with(1, 2, 1, 2, 1));
        Verify.assertInstanceOf(ImmutableDoubletonSet.class, doubleton);
        assertEquals(Sets.immutable.with(1, 2), doubleton);

        ImmutableSet<Integer> tripleton = Sets.immutable.withAll(Lists.mutable.with(1, 2, 3, 1, 2, 3));
        Verify.assertInstanceOf(ImmutableTripletonSet.class, tripleton);
        assertEquals(Sets.immutable.with(1, 2, 3), tripleton);

        ImmutableSet<Integer> quadrupleton = Sets.immutable.withAll(Lists.mutable.with(1, 2, 3, 4, 1, 2, 3, 4));
        Verify.assertInstanceOf(ImmutableQuadrupletonSet.class, quadrupleton);
        assertEquals(Sets.immutable.with(1, 2, 3, 4), quadrupleton);

        ImmutableSet<Integer> nullSingleton = Sets.immutable.withAll(Lists.mutable.<Integer>with(null, null));
        Verify.assertInstanceOf(ImmutableSingletonSet.class, nullSingleton);
        assertEquals(Sets.immutable.with((Integer) null), nullSingleton);
    }

    @Test
    public void withAllDistinctSizes()
    {
        Verify.assertInstanceOf(ImmutableSingletonSet.class, Sets.immutable.withAll(Lists.mutable.with(1)));
        Verify.assertInstanceOf(ImmutableDoubletonSet.class, Sets.immutable.withAll(Lists.mutable.with(1, 2)));
        Verify.assertInstanceOf(ImmutableTripletonSet.class, Sets.immutable.withAll(Lists.mutable.with(1, 2, 3)));
        Verify.assertInstanceOf(ImmutableQuadrupletonSet.class, Sets.immutable.withAll(Lists.mutable.with(1, 2, 3, 4)));
        Verify.assertInstanceOf(ImmutableUnifiedSet.class, Sets.immutable.withAll(Lists.mutable.with(1, 2, 3, 4, 5)));
    }

    @Test
    public void withAllReturnsSameInstanceForImmutableSetInput()
    {
        ImmutableSet<Integer> immutable = Sets.immutable.with(1, 2, 3);
        assertSame(immutable, Sets.immutable.withAll(immutable));
    }
}
