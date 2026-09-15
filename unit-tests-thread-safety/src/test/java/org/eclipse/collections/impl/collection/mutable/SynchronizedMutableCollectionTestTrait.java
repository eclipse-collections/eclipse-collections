/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.impl.SynchronizedCollectionTestTrait;
import org.eclipse.collections.impl.SynchronizedRichIterableTestTrait;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.jupiter.api.Test;

public interface SynchronizedMutableCollectionTestTrait
        extends SynchronizedRichIterableTestTrait, SynchronizedCollectionTestTrait, MutableCollectionTestTrait
{
    @Override
    MutableCollection<String> getClassUnderTest();

    @Test
    @Override
    default void size_synchronized()
    {
        SynchronizedRichIterableTestTrait.super.size_synchronized();
    }

    @Test
    @Override
    default void isEmpty_synchronized()
    {
        SynchronizedRichIterableTestTrait.super.isEmpty_synchronized();
    }

    @Test
    @Override
    default void iterator_not_synchronized()
    {
        SynchronizedRichIterableTestTrait.super.iterator_not_synchronized();
    }

    @Test
    @Override
    default void toArray_synchronized()
    {
        SynchronizedRichIterableTestTrait.super.toArray_synchronized();
    }

    @Test
    @Override
    default void toArray_with_target_synchronized()
    {
        SynchronizedRichIterableTestTrait.super.toArray_with_target_synchronized();
    }

    @Test
    @Override
    default void contains_synchronized()
    {
        SynchronizedRichIterableTestTrait.super.contains_synchronized();
    }

    @Test
    default void newEmpty_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().newEmpty());
    }

    @Test
    default void addAllIterable_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().addAllIterable(FastList.newList()));
    }

    @Test
    default void removeAllIterable_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().removeAllIterable(FastList.newList()));
    }

    @Test
    default void retainAllIterable_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().retainAllIterable(FastList.newList()));
    }

    @Test
    default void selectWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().selectWith((x, y) -> false, ""));
    }

    @Test
    default void selectWith_with_target_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().selectWith((x, y) -> false, "", FastList.newList()));
    }

    @Test
    default void rejectWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().rejectWith((x, y) -> true, ""));
    }

    @Test
    default void rejectWith_with_target_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().rejectWith((x, y) -> true, "", FastList.newList()));
    }

    @Test
    default void selectAndRejectWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().selectAndRejectWith((x, y) -> true, ""));
    }

    @Test
    default void removeIf_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().removeIf(x -> false));
    }

    @Test
    default void removeIfWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().removeIfWith((x, y) -> false, ""));
    }

    @Test
    default void collectWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collectWith((x, y) -> "", ""));
    }

    @Test
    default void collectWith_with_target_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collectWith((x, y) -> "", "", FastList.newList()));
    }

    @Test
    default void detectWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().detectWith((x, y) -> true, ""));
    }

    @Test
    default void detectWithIfNone_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().detectWithIfNone((x, y) -> true, "", null));
    }

    @Test
    default void countWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().countWith((x, y) -> true, ""));
    }

    @Test
    default void injectIntoWith_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().injectIntoWith("", (x, y, z) -> "", ""));
    }

    @Test
    default void asUnmodifiable_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().asUnmodifiable());
    }

    @Test
    default void asSynchronized_not_synchronized()
    {
        this.assertNotSynchronized(() -> this.getClassUnderTest().asSynchronized());
    }

    @Test
    default void toImmutable_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().toImmutable());
    }

    @Test
    default void with_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().with("4"));
    }

    @Test
    default void withAll_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withAll(FastList.newListWith("4")));
    }

    @Test
    default void without_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().without("4"));
    }

    @Test
    default void withoutAll_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().withoutAll(FastList.newListWith("4")));
    }
}
