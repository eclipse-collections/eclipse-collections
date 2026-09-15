/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.util.Collection;

import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.jupiter.api.Test;

public interface SynchronizedCollectionTestTrait
        extends SynchronizedTestTrait, IterableTestTrait
{
    @Override
    Collection<String> getClassUnderTest();

    @Test
    default void size_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().size());
    }

    @Test
    default void isEmpty_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().isEmpty());
    }

    @Test
    default void contains_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().contains(null));
    }

    @Test
    default void iterator_not_synchronized()
    {
        this.assertNotSynchronized(() -> this.getClassUnderTest().iterator());
    }

    @Test
    default void toArray_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().toArray());
    }

    @Test
    default void toArray_with_target_synchronized()
    {
        this.assertSynchronized(() ->
        {
            String[] array = null;
            this.getClassUnderTest().toArray(array);
        });
    }

    @Test
    default void add_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().add(""));
    }

    @Test
    default void remove_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().remove(""));
    }

    @Test
    default void containsAll_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().containsAll(FastList.newList()));
    }

    @Test
    default void addAll_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().addAll(FastList.newList()));
    }

    @Test
    default void removeAll_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().removeAll(FastList.newList()));
    }

    @Test
    default void retainAll_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().retainAll(FastList.newList()));
    }

    @Test
    default void clear_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().clear());
    }

    @Test
    default void equals_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().equals(null));
    }

    @Test
    default void hashCode_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().hashCode());
    }
}
