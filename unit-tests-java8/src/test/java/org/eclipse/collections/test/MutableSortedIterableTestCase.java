/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface MutableSortedIterableTestCase extends MutableOrderedIterableTestCase
{
    @Override
    @Test
    default void Iterable_remove()
    {
        Iterable<Integer> iterable = this.newWith(3, 2, 1);
        Iterator<Integer> iterator = iterable.iterator();
        Integer expectedFirst = switch (this.getOrderingType())
        {
            case SORTED_NATURAL -> 1;
            case SORTED_REVERSE_NATURAL, INSERTION_ORDER, UNORDERED -> 3;
        };
        assertEquals(expectedFirst, iterator.next());
        iterator.remove();
        assertIterablesEqual(
                switch (this.getOrderingType())
                {
                    case SORTED_NATURAL -> this.newWith(2, 3);
                    case SORTED_REVERSE_NATURAL, INSERTION_ORDER, UNORDERED -> this.newWith(2, 1);
                },
                iterable);
    }
}
