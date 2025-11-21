/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.collection.mutable;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.test.CollectionTestCase;
import org.eclipse.collections.test.RichIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface MutableCollectionTestCase extends CollectionTestCase, RichIterableTestCase
{
    @Override
    <T> MutableCollection<T> newWith(T... elements);

    @Test
    default void MutableCollection_iterationOrder()
    {
        MutableCollection<Integer> injectIntoWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectIntoWith(
                0,
                (a, b, c) ->
                {
                    injectIntoWithIterationOrder.add(b);
                    return 0;
                },
                0);
        assertIterablesEqual(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), injectIntoWithIterationOrder);
    }

    // TODO try to make the return type of newWith and getInstanceUnderTest a generic parameter
    @Override
    default MutableCollection<Integer> getInstanceUnderTest()
    {
        return this.allowsDuplicates()
                ? this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1)
                : this.newWith(4, 3, 2, 1);
    }

    @Test
    default void MutableCollection_sanity_check()
    {
        String s = "";

        if (this.allowsAdd())
        {
            MutableCollection<String> collection = this.newWith();
            assertTrue(collection.add(s));
            assertEquals(this.allowsDuplicates(), collection.add(s));
            assertEquals(this.allowsDuplicates() ? 2 : 1, collection.size());
        }
        else
        {
            if (this.allowsDuplicates())
            {
                assertEquals(2, this.newWith(s, s).size());
            }
            else
            {
                assertThrows(IllegalStateException.class, () -> this.newWith(s, s));
            }
        }
    }

    @Test
    default void MutableCollection_toImmutable()
    {
        assertThat(this.newWith(), instanceOf(MutableCollection.class));
        assertThat(this.newWith().toImmutable(), instanceOf(ImmutableCollection.class));
    }

    @Test
    default void MutableCollection_removeIf()
    {
        if (!this.allowsRemove())
        {
            MutableCollection<Integer> collection = this.newWith(5, 4, 3, 2, 1);
            assertThrows(UnsupportedOperationException.class, () -> collection.removeIf(Predicates.cast(each -> each % 2 == 0)));
            assertThrows(UnsupportedOperationException.class, () -> this.newWith(7, 4, 5, 1).removeIf(Predicates.cast(null)));
            assertThrows(UnsupportedOperationException.class, () -> this.newWith(9, 5, 1).removeIf(Predicates.cast(each -> each % 2 == 0)));
            assertThrows(UnsupportedOperationException.class, () -> this.newWith(6, 4, 2).removeIf(Predicates.cast(each -> each % 2 == 0)));
            assertThrows(UnsupportedOperationException.class, () -> this.<Integer>newWith().removeIf(Predicates.cast(each -> each % 2 == 0)));
            assertIterablesEqual(this.newWith(5, 4, 3, 2, 1), collection);
            return;
        }

        MutableCollection<Integer> collection1 = this.newWith(5, 5, 4, 4, 3, 3, 2, 2, 1, 1);
        assertTrue(collection1.removeIf(Predicates.cast(each -> each % 2 == 0)));
        assertIterablesEqual(this.getExpectedFiltered(5, 5, 3, 3, 1, 1), collection1);

        MutableCollection<Integer> collection2 = this.newWith(1, 2, 3);
        assertFalse(collection2.removeIf(Predicates.cast(each -> each > 4)));
        assertIterablesEqual(this.getExpectedFiltered(1, 2, 3), collection2);
        assertTrue(collection2.removeIf(Predicates.cast(each -> each > 0)));

        MutableCollection<Integer> collection3 = this.newWith();
        assertFalse(collection3.removeIf(Predicates.cast(each -> each % 2 == 0)));
        assertIterablesEqual(this.getExpectedFiltered(), collection3);

        MutableCollection<Integer> collection4 = this.newWith(2, 2, 4, 6);
        assertTrue(collection4.removeIf(Predicates.cast(each -> each % 2 == 0)));
        assertIterablesEqual(this.getExpectedFiltered(), collection4);
        assertFalse(collection4.removeIf(Predicates.cast(each -> each % 2 == 0)));
    }

    @Test
    default void MutableCollection_removeIfWith()
    {
        if (!this.allowsRemove())
        {
            MutableCollection<Integer> collection = this.newWith(5, 4, 3, 2, 1);
            assertThrows(UnsupportedOperationException.class, () -> Boolean.valueOf(collection.removeIfWith(Predicates2.in(), Lists.immutable.with(5, 3, 1))));
            assertThrows(UnsupportedOperationException.class, () -> this.newWith(7, 4, 5, 1).removeIfWith(null, this));
            assertThrows(UnsupportedOperationException.class, () -> this.newWith(9, 5, 1).removeIfWith(Predicates2.greaterThan(), 10));
            assertThrows(UnsupportedOperationException.class, () -> this.newWith(6, 4, 2).removeIfWith(Predicates2.greaterThan(), 2));
            assertThrows(UnsupportedOperationException.class, () -> this.<Integer>newWith().removeIfWith(Predicates2.greaterThan(), 2));
            assertIterablesEqual(this.newWith(5, 4, 3, 2, 1), collection);
            return;
        }

        MutableCollection<Integer> collection1 = this.newWith(5, 5, 4, 4, 3, 3, 2, 2, 1, 1);
        assertTrue(collection1.removeIfWith(Predicates2.in(), Lists.immutable.with(5, 3, 1)));
        assertIterablesEqual(this.getExpectedFiltered(4, 4, 2, 2), collection1);

        MutableCollection<Integer> collection2 = this.newWith(1, 2, 3);
        assertFalse(collection2.removeIfWith(Predicates2.in(), Lists.immutable.with(4)));
        assertIterablesEqual(this.getExpectedFiltered(1, 2, 3), collection2);
        assertTrue(collection2.removeIfWith(Predicates2.in(), Lists.immutable.with(1, 2, 3)));

        MutableCollection<Integer> collection3 = this.newWith();
        assertFalse(collection3.removeIfWith(Predicates2.in(), Lists.immutable.with()));
        assertIterablesEqual(this.getExpectedFiltered(), collection3);

        MutableCollection<Integer> collection4 = this.newWith(2, 2, 4, 6);
        assertTrue(collection4.removeIfWith(Predicates2.greaterThan(), 1));
        assertIterablesEqual(this.getExpectedFiltered(), collection4);
        assertFalse(collection4.removeIfWith(Predicates2.greaterThan(), 1));
    }

    @Test
    default void MutableCollection_injectIntoWith()
    {
        MutableCollection<Integer> collection = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        assertEquals(Integer.valueOf(81), collection.injectIntoWith(1, (a, b, c) -> a + b + c, 5));
    }
}
