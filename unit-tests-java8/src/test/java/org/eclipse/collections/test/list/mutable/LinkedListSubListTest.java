/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.list.mutable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.test.list.ListTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.test.Verify.assertNotSerializable;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesNotEqual;

public class LinkedListSubListTest implements ListTestCase
{
    @SafeVarargs
    @Override
    public final <T> List<T> newWith(T... elements)
    {
        List<T> list = new LinkedList<>();
        list.add(null);
        Collections.addAll(list, elements);
        list.add(null);
        return list.subList(1, 1 + elements.length);
    }

    @Override
    @Test
    public void Object_equalsAndHashCode()
    {
        assertIterablesNotEqual(this.newWith(4, 3, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(4, 3, 2, 1));

        assertIterablesNotEqual(this.newWith(2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(2, 1));

        assertIterablesNotEqual(this.newWith(4, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(4, 2, 1));

        Verify.assertEqualsAndHashCode(this.newWith(3, 3, 3, 2, 2, 1), this.newWith(3, 3, 3, 2, 2, 1));

        assertIterablesNotEqual(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(3, 3, 2, 1));

        assertIterablesNotEqual(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 2, 1), this.newWith(3, 3, 2, 1));
    }

    @Override
    @Test
    public void Object_PostSerializedEqualsAndHashCode()
    {
        assertNotSerializable(this.newWith());
    }
}
