/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.collections.test.set.SetTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface MapKeySetTestCase extends SetTestCase
{
    @Override
    default boolean allowsAdd()
    {
        return false;
    }

    @Override
    @Test
    default void Object_equalsAndHashCode()
    {
        SetTestCase.super.Object_equalsAndHashCode();

        RecursiveComparableList recursiveList = new RecursiveComparableList();
        Set<RecursiveComparableList> set = this.newWith(recursiveList);
        recursiveList.add(recursiveList);

        assertEquals(set, set);
        assertThrows(StackOverflowError.class, set::hashCode);
    }

    final class RecursiveComparableList
            extends ArrayList<Object>
            implements Comparable<RecursiveComparableList>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int compareTo(RecursiveComparableList other)
        {
            return 0;
        }
    }
}
