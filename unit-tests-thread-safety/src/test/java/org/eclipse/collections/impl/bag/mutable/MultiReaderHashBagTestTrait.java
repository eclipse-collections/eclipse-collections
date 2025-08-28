/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable;

import org.eclipse.collections.api.bag.MultiReaderBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.impl.MultiReaderThreadSafetyTestTrait;
import org.eclipse.collections.impl.block.factory.primitive.IntPredicates;
import org.junit.jupiter.api.Test;

public interface MultiReaderHashBagTestTrait
        extends MultiReaderThreadSafetyTestTrait
{
    @Override
    MultiReaderBag<Integer> getClassUnderTest();

    @Override
    default Thread createReadLockHolderThread(Gate gate)
    {
        return this.spawn(() -> this.getClassUnderTest().withReadLockAndDelegate(bag -> this.sleep(gate)));
    }

    @Override
    default Thread createWriteLockHolderThread(Gate gate)
    {
        return this.spawn(() -> this.getClassUnderTest().withWriteLockAndDelegate(bag -> this.sleep(gate)));
    }

    @Test
    default void addOccurrences_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().addOccurrences(1, 2));
    }

    @Test
    default void removeOccurrences_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().removeOccurrences(1, 1));
    }

    @Test
    default void occurrencesOf_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().occurrencesOf(1));
    }

    @Test
    default void sizeDistinct_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::sizeDistinct);
    }

    @Test
    default void toMapOfItemToCount_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::toMapOfItemToCount);
    }

    @Test
    default void toStringOfItemToCount_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::toStringOfItemToCount);
    }

    @Test
    default void iteratorWithReadLock_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () ->
                {
                    this.getClassUnderTest().withReadLockAndDelegate(MutableBag::iterator);
                    return null;
                });
    }

    @Test
    default void iteratorWithWriteLock_safe()
    {
        this.assertReaderWriterThreadSafety(
                true,
                true,
                () ->
                {
                    this.getClassUnderTest().withWriteLockAndDelegate(MutableBag::iterator);
                    return null;
                });
    }

    @Test
    default void forEachWithOccurrences_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () ->
                {
                    this.getClassUnderTest().forEachWithOccurrences((object, occurrences) -> { });
                    return null;
                });
    }

    @Test
    default void selectByOccurrences_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().selectByOccurrences(IntPredicates.isOdd()));
    }
}
