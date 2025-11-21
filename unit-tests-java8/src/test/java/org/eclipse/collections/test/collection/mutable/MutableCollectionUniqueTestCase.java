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

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.test.RichIterableUniqueTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface MutableCollectionUniqueTestCase extends MutableCollectionTestCase, RichIterableUniqueTestCase
{
    @Override
    <T> MutableCollection<T> newWith(T... elements);

    @Override
    @Test
    default void MutableCollection_removeIf()
    {
        if (!this.allowsRemove())
        {
            MutableCollectionTestCase.super.MutableCollection_removeIf();
            return;
        }

        MutableCollection<Integer> collection1 = this.newWith(5, 4, 3, 2, 1);
        assertTrue(collection1.removeIf(Predicates.cast(each -> each % 2 == 0)));
        assertIterablesEqual(this.getExpectedFiltered(5, 3, 1), collection1);

        MutableCollection<Integer> collection2 = this.newWith(1, 2, 3, 4);
        assertFalse(collection2.removeIf(Predicates.equal(5)));
        assertTrue(collection2.removeIf(Predicates.greaterThan(0)));
        assertFalse(collection2.removeIf(Predicates.greaterThan(2)));

        MutableCollection<Integer> collection3 = this.newWith();
        assertFalse(collection3.removeIf(Predicates.equal(5)));

        Predicate<Object> predicate = null;
        assertThrows(NullPointerException.class, () -> this.newWith(7, 4, 5, 1).removeIf(predicate));
    }

    @Override
    @Test
    default void MutableCollection_removeIfWith()
    {
        if (!this.allowsRemove())
        {
            MutableCollectionTestCase.super.MutableCollection_removeIfWith();
            return;
        }

        MutableCollection<Integer> collection = this.newWith(5, 4, 3, 2, 1);
        collection.removeIfWith(Predicates2.in(), Lists.immutable.with(5, 3, 1));
        assertIterablesEqual(this.getExpectedFiltered(4, 2), collection);

        MutableCollection<Integer> collection2 = this.newWith(1, 2, 3, 4);
        assertFalse(collection2.removeIf(Predicates.equal(5)));
        assertTrue(collection2.removeIf(Predicates.greaterThan(0)));
        assertFalse(collection2.removeIf(Predicates.greaterThan(2)));

        MutableCollection<Integer> collection3 = this.newWith();
        assertFalse(collection3.removeIf(Predicates.equal(5)));

        assertThrows(NullPointerException.class, () -> this.newWith(7, 4, 5, 1).removeIf(Predicates.cast(null)));
    }

    @Override
    @Test
    default void MutableCollection_injectIntoWith()
    {
        MutableCollection<Integer> collection = this.newWith(4, 3, 2, 1);
        assertEquals(Integer.valueOf(51), collection.injectIntoWith(1, (argument1, argument2, argument3) -> argument1 + argument2 + argument3, 10));
    }
}
