/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.test.IterableTestCase;
import org.eclipse.collections.test.list.mutable.MutableListTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.test.Verify.assertNotSerializable;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesNotEqual;

public class MultiReaderFastListAsWriteUntouchableTest implements MutableListTestCase
{
    @SafeVarargs
    @Override
    public final <T> MutableList<T> newWith(T... elements)
    {
        MultiReaderFastList<T> result = MultiReaderFastList.newList();
        IterableTestCase.addAllTo(elements, result);
        return result.asWriteUntouchable();
    }

    @Override
    @Test
    public void Object_PostSerializedEqualsAndHashCode()
    {
        assertNotSerializable(this.newWith());
    }

    @Override
    @Test
    public void Object_equalsAndHashCode()
    {
        assertNotSerializable(this.newWith(3, 2, 1));

        assertIterablesNotEqual(this.newWith(4, 3, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(4, 3, 2, 1));

        assertIterablesNotEqual(this.newWith(2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(2, 1));

        assertIterablesNotEqual(this.newWith(4, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(4, 2, 1));
    }
}
