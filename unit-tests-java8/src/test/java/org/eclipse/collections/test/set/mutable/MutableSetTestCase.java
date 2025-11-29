/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.test.collection.mutable.MutableCollectionUniqueTestCase;
import org.eclipse.collections.test.set.SetTestCase;
import org.eclipse.collections.test.set.UnsortedSetIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;

public interface MutableSetTestCase extends SetTestCase, UnsortedSetIterableTestCase, MutableCollectionUniqueTestCase
{
    @Override
    <T> MutableSet<T> newWith(T... elements);

    @Override
    default boolean allowsDuplicates()
    {
        return false;
    }

    @Override
    @Test
    default void Object_PostSerializedEqualsAndHashCode()
    {
        SetTestCase.super.Object_PostSerializedEqualsAndHashCode();
    }

    @Override
    @Test
    default void Object_equalsAndHashCode()
    {
        SetTestCase.super.Object_equalsAndHashCode();
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        SetTestCase.super.Iterable_toString();
    }

    @Override
    @Test
    default void Iterable_next()
    {
        UnsortedSetIterableTestCase.super.Iterable_next();
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        SetTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void equalsAndHashCode()
    {
        UnsortedSetIterableTestCase.super.equalsAndHashCode();

        if (this.allowsRemove())
        {
            MutableSet<Integer> singleCollisionBucket = this.newWith(COLLISION_1, COLLISION_2);
            singleCollisionBucket.remove(COLLISION_2);
            assertIterablesEqual(singleCollisionBucket, this.newWith(COLLISION_1));
        }
    }
}
