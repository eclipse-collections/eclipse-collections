/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import org.eclipse.collections.api.set.MultiReaderSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.MultiReaderThreadSafetyTestTrait;
import org.junit.jupiter.api.Test;

public interface MultiReaderUnifiedSetTestTrait
        extends MultiReaderThreadSafetyTestTrait
{
    @Override
    MultiReaderSet<Integer> getClassUnderTest();

    @Override
    default Thread createReadLockHolderThread(Gate gate)
    {
        return this.spawn(() -> this.getClassUnderTest().withReadLockAndDelegate(set -> this.sleep(gate)));
    }

    @Override
    default Thread createWriteLockHolderThread(Gate gate)
    {
        return this.spawn(() -> this.getClassUnderTest().withWriteLockAndDelegate(set -> this.sleep(gate)));
    }

    @Override
    @Test
    default void equals_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().equals(MultiReaderUnifiedSet.newSet()));
    }

    @Test
    default void union_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().union(new UnifiedSet<>()));
    }

    @Test
    default void unionInto_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().unionInto(new UnifiedSet<>(), new UnifiedSet<>()));
    }

    @Test
    default void intersect_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().intersect(new UnifiedSet<>()));
    }

    @Test
    default void intersectInto_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().intersectInto(new UnifiedSet<>(), new UnifiedSet<>()));
    }

    @Test
    default void difference_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().difference(new UnifiedSet<>()));
    }

    @Test
    default void differenceInto_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().differenceInto(new UnifiedSet<>(), new UnifiedSet<>()));
    }

    @Test
    default void symmetricDifference_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().symmetricDifference(new UnifiedSet<>()));
    }

    @Test
    default void symmetricDifferenceInto_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().symmetricDifferenceInto(new UnifiedSet<>(), new UnifiedSet<>()));
    }

    @Test
    default void isSubsetOf_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().isSubsetOf(new UnifiedSet<>()));
    }

    @Test
    default void isProperSubsetOf_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().isProperSubsetOf(new UnifiedSet<>()));
    }

    @Test
    default void powerSet_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::powerSet);
    }

    @Test
    default void cartesianProduct_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().cartesianProduct(new UnifiedSet<>()));
    }

    @Test
    default void clone_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::clone);
    }

    @Test
    default void iteratorWithReadLock_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () ->
                {
                    this.getClassUnderTest().withReadLockAndDelegate(MutableSet::iterator);
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
                    this.getClassUnderTest().withWriteLockAndDelegate(MutableSet::iterator);
                    return null;
                });
    }
}
