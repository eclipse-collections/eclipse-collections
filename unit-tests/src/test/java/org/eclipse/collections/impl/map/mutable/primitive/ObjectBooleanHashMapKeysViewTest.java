/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable.primitive;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.lazy.AbstractLazyIterableTestCase;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test for {@link ObjectBooleanHashMap#keysView()}.
 */
public class ObjectBooleanHashMapKeysViewTest extends AbstractLazyIterableTestCase
{
    @Override
    protected <T> LazyIterable<T> newWith(T... elements)
    {
        ObjectBooleanHashMap<T> map = new ObjectBooleanHashMap<>();
        for (int i = 0; i < elements.length; i++)
        {
            map.put(elements[i], (i & 1) == 0);
        }
        return map.keysView();
    }

    @Override
    @Test
    public void iterator()
    {
        MutableSet<String> expected = UnifiedSet.newSetWith("zero", "thirtyOne", "thirtyTwo");
        MutableSet<String> actual = UnifiedSet.newSet();

        Iterator<String> iterator = ObjectBooleanHashMap.newWithKeysValues("zero", true, "thirtyOne", false, "thirtyTwo", true).keysView().iterator();
        assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(expected, actual);
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void iteratorWithNullKey()
    {
        Iterator<String> iterator = ObjectBooleanHashMap.newWithKeysValues((String) null, false).keysView().iterator();
        assertNull(iterator.next());
    }
}
