/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.test.CollisionsTestCase;
import org.eclipse.collections.test.RichIterableUniqueTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesNotEqual;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface SetIterableTestCase extends RichIterableUniqueTestCase, CollisionsTestCase
{
    default boolean allowsNull()
    {
        return true;
    }

    @Override
    <T> SetIterable<T> newWith(T... elements);

    @Test
    default void equalsAndHashCode()
    {
        MutableList<Integer> list = Lists.mutable.empty();
        for (Integer collision : COLLISIONS)
        {
            list.add(collision);
            assertIterablesEqual(
                    this.newWith(list.toArray()),
                    this.newWith(list.toArray()));
        }

        Verify.assertEqualsAndHashCode(this.newWith(), this.newWith());

        assertIterablesNotEqual(this.newWith(1, 2, 3, 4, 5), this.newWith(1, 2, 3, 4));
        assertIterablesNotEqual(this.newWith(1, 2, 3, 4), this.newWith(1, 2, 3));
        assertIterablesNotEqual(this.newWith(1, 2, 3), this.newWith(1, 2));
        assertIterablesNotEqual(this.newWith(1, 2), this.newWith(1));
        assertIterablesNotEqual(this.newWith(1), this.newWith());

        SetIterable<Integer> expected = Sets.mutable.with(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        assertIterablesNotEqual(expected, this.newWith(COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5));
        assertIterablesNotEqual(expected, this.newWith(COLLISION_1, COLLISION_3, COLLISION_4, COLLISION_5));
        assertIterablesNotEqual(expected, this.newWith(COLLISION_1, COLLISION_2, COLLISION_4, COLLISION_5));
        assertIterablesNotEqual(expected, this.newWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_5));

        assertEquals(expected, this.newWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4));

        if (this.allowsNull())
        {
            assertIterablesEqual(this.newWith(null, COLLISION_1, COLLISION_2, COLLISION_3), this.newWith(null, COLLISION_1, COLLISION_2, COLLISION_3));
            assertIterablesEqual(this.newWith(COLLISION_1, null, COLLISION_2, COLLISION_3), this.newWith(COLLISION_1, null, COLLISION_2, COLLISION_3));
            assertIterablesEqual(this.newWith(COLLISION_1, COLLISION_2, null, COLLISION_3), this.newWith(COLLISION_1, COLLISION_2, null, COLLISION_3));
            assertIterablesEqual(this.newWith(COLLISION_1, COLLISION_2, COLLISION_3, null), this.newWith(COLLISION_1, COLLISION_2, COLLISION_3, null));
        }
    }

    @Override
    @Test
    default void RichIterable_toArray()
    {
        Object[] array = this.newWith(3, 2, 1).toArray();
        assertArrayEquals(new Object[]{3, 2, 1}, array);
    }

    @Test
    default void SetIterable_union()
    {
        SetIterable<Integer> union = this.newWith(3, 2, 1).union(this.newWith(5, 4, 3));
        assertIterablesEqual(this.newWith(5, 4, 3, 2, 1), union);
    }
}
