/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import java.util.List;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.list.MultiReaderList;
import org.eclipse.collections.impl.MultiReaderThreadSafetyTestTrait;
import org.junit.jupiter.api.Test;

public interface MultiReaderFastListTestTrait
        extends MultiReaderThreadSafetyTestTrait
{
    @Override
    MultiReaderList<Integer> getClassUnderTest();

    @Override
    default Thread createReadLockHolderThread(Gate gate)
    {
        return this.spawn(() -> this.getClassUnderTest().withReadLockAndDelegate(list -> this.sleep(gate)));
    }

    @Override
    default Thread createWriteLockHolderThread(Gate gate)
    {
        return this.spawn(() -> this.getClassUnderTest().withWriteLockAndDelegate(list -> this.sleep(gate)));
    }

    @Test
    default void listIterator_safe()
    {
        this.assertReaderWriterThreadSafety(
                false, false, () ->
                {
                    try
                    {
                        this.getClassUnderTest().listIterator();
                        return null;
                    }
                    catch (Exception e)
                    {
                        return null;
                    }
                });
    }

    @Test
    default void listIteratorIndex_safe()
    {
        this.assertReaderWriterThreadSafety(
                false, false, () ->
                {
                    try
                    {
                        this.getClassUnderTest().listIterator(1);
                        return null;
                    }
                    catch (Exception e)
                    {
                        return null;
                    }
                });
    }

    @Test
    default void iteratorWithReadLock_safe()
    {
        this.assertReaderWriterThreadSafety(
                false, true, () ->
                {
                    this.getClassUnderTest().withReadLockAndDelegate(List::iterator);
                    return null;
                });
    }

    @Test
    default void iteratorWithWriteLock_safe()
    {
        this.assertReaderWriterThreadSafety(
                true, true, () ->
                {
                    this.getClassUnderTest().withWriteLockAndDelegate(List::iterator);
                    return null;
                });
    }

    @Test
    default void clone_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::clone);
    }

    @Test
    default void addWithIndex_safe()
    {
        this.assertReaderWriterThreadSafety(
                true, true, () ->
                {
                    this.getClassUnderTest().add(1, 4);
                    return null;
                });
    }

    @Test
    default void addAllWithIndex_safe()
    {
        this.assertReaderWriterThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().addAll(1, FastList.newListWith(3, 4, 5)));
    }

    @Test
    default void removeWithIndex_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().remove(1));
    }

    @Test
    default void set_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().set(1, 4));
    }

    @Test
    default void reverseThis_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, this.getClassUnderTest()::reverseThis);
    }

    @Test
    default void sort_safe()
    {
        this.assertReaderWriterThreadSafety(
                true,
                true,
                () ->
                {
                    this.getClassUnderTest().sort(null);
                    return null;
                });
    }

    @Test
    default void sortThis_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, this.getClassUnderTest()::sortThis);
    }

    @Test
    default void sortThis_withComparator_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThis(null));
    }

    @Test
    default void sortThisBy_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThisBy(x -> ""));
    }

    @Test
    default void sortThisByInt_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThisByInt(x -> 0));
    }

    @Test
    default void sortThisByChar_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThisByChar(x -> (char) 0));
    }

    @Test
    default void sortThisByByte_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThisByByte(x -> (byte) 0));
    }

    @Test
    default void sortThisByBoolean_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThisByBoolean(x -> true));
    }

    @Test
    default void sortThisByShort_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThisByShort(x -> (short) 0));
    }

    @Test
    default void sortThisByFloat_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThisByFloat(x -> 0.0f));
    }

    @Test
    default void sortThisByLong_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThisByLong(x -> 0L));
    }

    @Test
    default void sortThisByDouble_safe()
    {
        this.assertReaderWriterThreadSafety(true, true, () -> this.getClassUnderTest().sortThisByDouble(x -> 0.0d));
    }

    @Test
    default void distinct_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::distinct);
    }

    @Test
    default void subList_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().subList(0, 1));
    }

    @Test
    default void get_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().get(1));
    }

    @Test
    default void indexOf_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().indexOf(1));
    }

    @Test
    default void lastIndexOf_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().lastIndexOf(1));
    }

    @Test
    default void reverseForEach_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () ->
                {
                    this.getClassUnderTest().reverseForEach(x -> { });
                    return null;
                });
    }

    @Test
    default void asReversed_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::asReversed);

        LazyIterable<Integer> reverseIterable = this.getClassUnderTest().asReversed();
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () ->
                {
                    reverseIterable.each(x -> { });
                    return null;
                });
    }

    @Test
    default void forEachWithIndex_safe()
    {
        this.assertReaderWriterThreadSafety(
                false,
                true,
                () ->
                {
                    this.getClassUnderTest().forEachWithIndex(0, 2, (x, index) -> { });
                    return null;
                });
    }

    @Test
    default void toReversed_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::toReversed);
    }

    @Test
    default void toStack_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, this.getClassUnderTest()::toStack);
    }

    @Test
    default void takeWhile_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().takeWhile(x -> true));
    }

    @Test
    default void dropWhile_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().dropWhile(x -> true));
    }

    @Test
    default void partitionWhile_safe()
    {
        this.assertReaderWriterThreadSafety(false, true, () -> this.getClassUnderTest().partitionWhile(x -> true));
    }
}
