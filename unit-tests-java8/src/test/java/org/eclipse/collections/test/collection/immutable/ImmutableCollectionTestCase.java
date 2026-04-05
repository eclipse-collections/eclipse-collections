/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.collection.immutable;

import java.util.Iterator;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.test.RichIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface ImmutableCollectionTestCase extends RichIterableTestCase
{
    @Override
    <T> ImmutableCollection<T> newWith(T... elements);

    @Override
    default boolean allowsAdd()
    {
        return false;
    }

    @Override
    default boolean allowsRemove()
    {
        return false;
    }

    @Test
    default void ImmutableCollection_sanity_check()
    {
        String s = "";
        if (this.allowsDuplicates())
        {
            assertEquals(2, this.newWith(s, s).size());
        }
        else
        {
            assertThrows(IllegalStateException.class, () -> this.newWith(s, s));
        }

        ImmutableCollection<String> collection = this.newWith(s);
        ImmutableCollection<String> newCollection = collection.newWith(s);
        if (this.allowsDuplicates())
        {
            assertEquals(2, newCollection.size());
            assertIterablesEqual(this.newWith(s, s), newCollection);
        }
        else
        {
            assertEquals(1, newCollection.size());
            assertSame(collection, newCollection);
        }
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        ImmutableCollection<Integer> collection = this.newWith(3, 2, 1);
        Iterator<Integer> iterator = collection.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    default void ImmutableCollection_newWith()
    {
        ImmutableCollection<Integer> immutableCollection = this.newWith(3, 3, 3, 2, 2, 1);
        ImmutableCollection<Integer> newWith = immutableCollection.newWith(4);

        assertIterablesEqual(this.newWith(3, 3, 3, 2, 2, 1, 4), newWith);
        assertNotSame(immutableCollection, newWith);
        assertThat(newWith, instanceOf(ImmutableCollection.class));

        ImmutableCollection<Integer> newWith2 = newWith.newWith(4);

        assertIterablesEqual(this.newWith(3, 3, 3, 2, 2, 1, 4, 4), newWith2);
    }
}
