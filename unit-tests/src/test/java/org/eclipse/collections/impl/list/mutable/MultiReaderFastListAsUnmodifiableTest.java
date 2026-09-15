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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiReaderFastListAsUnmodifiableTest extends UnmodifiableMutableListTestCase
{
    @Override
    protected MutableList<Integer> getCollection()
    {
        return MultiReaderFastList.newListWith(1).asUnmodifiable();
    }

    @Override
    @Test
    public void listIterator()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.getCollection().listIterator());
    }

    @Override
    @Test
    public void subListListIterator()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.getCollection().subList(0, 1).listIterator());
    }

    @Override
    @Test
    public void iteratorRemove()
    {
        // Not applicable
    }
}
