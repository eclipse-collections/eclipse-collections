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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface MultiReaderThreadSafetyTestTrait
{
    MutableCollection<Integer> getClassUnderTest();

    Thread createReadLockHolderThread(Gate gate);

    Thread createWriteLockHolderThread(Gate gate);

    default void sleep(Gate gate)
    {
        gate.open();

        try
        {
            Thread.sleep(Long.MAX_VALUE);
        }
        catch (InterruptedException ignore)
        {
            Thread.currentThread().interrupt();
        }
    }

    default Thread spawn(Runnable code)
    {
        Thread result = new Thread(code);
        result.start();
        return result;
    }

    class Gate
    {
        private final CountDownLatch latch = new CountDownLatch(1);

        public void open()
        {
            this.latch.countDown();
        }

        public void await()
                throws InterruptedException
        {
            this.latch.await();
        }
    }

    default long time(Runnable code)
    {
        long before = System.currentTimeMillis();
        code.run();
        long after = System.currentTimeMillis();
        return after - before;
    }

    default void assertThreadSafety(
            boolean readersBlocked,
            boolean writersBlocked,
            Runnable code)
    {
        if (readersBlocked)
        {
            this.assertReadersBlocked(code);
        }
        else
        {
            this.assertReadersNotBlocked(code);
        }

        if (writersBlocked)
        {
            this.assertWritersBlocked(code);
        }
        else
        {
            this.assertWritersNotBlocked(code);
        }
    }

    default void assertReadersBlocked(Runnable code)
    {
        this.assertReadSafety(true, 10L, TimeUnit.MILLISECONDS, code);
    }

    default void assertReadersNotBlocked(Runnable code)
    {
        this.assertReadSafety(false, 60L, TimeUnit.SECONDS, code);
    }

    default void assertWritersBlocked(Runnable code)
    {
        this.assertWriteSafety(true, 10L, TimeUnit.MILLISECONDS, code);
    }

    default void assertWritersNotBlocked(Runnable code)
    {
        this.assertWriteSafety(false, 60L, TimeUnit.SECONDS, code);
    }

    default void assertReadSafety(
            boolean threadSafe,
            long timeout,
            TimeUnit timeUnit,
            Runnable code)
    {
        Gate gate = new Gate();
        this.assertThreadSafety(
                timeout,
                timeUnit,
                gate,
                code,
                threadSafe,
                this.createReadLockHolderThread(gate));
    }

    default void assertWriteSafety(
            boolean threadSafe,
            long timeout,
            TimeUnit timeUnit,
            Runnable code)
    {
        Gate gate = new Gate();
        this.assertThreadSafety(
                timeout,
                timeUnit,
                gate,
                code,
                threadSafe,
                this.createWriteLockHolderThread(gate));
    }

    default <T> void assertReaderWriterThreadSafety(
            boolean readersBlocked,
            boolean writersBlocked,
            Supplier<T> supplier)
    {
        this.assertThreadSafety(readersBlocked, writersBlocked, supplier::get);
    }

    default void assertThreadSafety(
            long timeout,
            TimeUnit timeUnit,
            Gate gate,
            Runnable code,
            boolean threadSafe,
            Thread lockHolderThread)
    {
        long millisTimeout = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
        long measuredTime = this.time(() ->
        {
            try
            {
                gate.await();
                Thread codeThread = this.spawn(code);
                codeThread.join(millisTimeout, 0);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });

        assertEquals(
                threadSafe,
                measuredTime >= millisTimeout,
                () -> "Measured %d ms but timeout was %d ms.".formatted(measuredTime, millisTimeout));

        lockHolderThread.interrupt();
        try
        {
            lockHolderThread.join();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Test
    default void newEmpty_safe()
    {
        this.assertThreadSafety(
                false,
                false,
                () -> this.getClassUnderTest().newEmpty());
    }

    @Test
    default void iterator_safe()
    {
        this.assertThreadSafety(
                false,
                false,
                () ->
                {
                    try
                    {
                        this.getClassUnderTest().iterator();
                    }
                    catch (Exception e)
                    {
                        // Ignore exceptions
                    }
                });
    }

    @Test
    default void add_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().add(4));
    }

    @Test
    default void addAll_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().addAll(new FastList<>()));
    }

    @Test
    default void addAllIterable_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().addAllIterable(new FastList<>()));
    }

    @Test
    default void remove_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().remove(Integer.valueOf(1)));
    }

    @Test
    default void removeAll_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().removeAll(new FastList<>()));
    }

    @Test
    default void removeAllIterable_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().removeAllIterable(new FastList<>()));
    }

    @Test
    default void retainAll_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().retainAll(new FastList<>()));
    }

    @Test
    default void retainAllIterable_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().retainAllIterable(new FastList<>()));
    }

    @Test
    default void removeIf_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().removeIf(x -> true));
    }

    @Test
    default void removeIfWith_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().removeIfWith((x, y) -> true, 0));
    }

    @Test
    default void with_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().with(4));
    }

    @Test
    default void without_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().without(1));
    }

    @Test
    default void withAll_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().withAll(new FastList<>()));
    }

    @Test
    default void withoutAll_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().withoutAll(new FastList<>()));
    }

    @Test
    default void clear_safe()
    {
        this.assertThreadSafety(
                true,
                true,
                () -> this.getClassUnderTest().clear());
    }

    @Test
    default void size_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().size());
    }

    @Test
    default void getFirst_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().getFirst());
    }

    @Test
    default void getLast_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().getLast());
    }

    @Test
    default void isEmpty_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().isEmpty());
    }

    @Test
    default void notEmpty_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().notEmpty());
    }

    @Test
    default void contains_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().contains(1));
    }

    @Test
    default void containsAll_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().containsAll(new FastList<>()));
    }

    @Test
    default void containsAllIterable_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().containsAll(new FastList<>()));
    }

    @Test
    default void containsAllArguments_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().containsAllArguments("1", "2"));
    }

    @Test
    default void equals_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().equals(null));
    }

    @Test
    default void hashCode_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().hashCode());
    }

    @Test
    default void forEach_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().forEach(x -> { }));
    }

    @Test
    default void forEachWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().forEachWith((x, y) -> { }, 0));
    }

    @Test
    default void collect_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().collect(x -> ""));
    }

    @Test
    default void collect_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().collect(x -> "", new FastList<>()));
    }

    @Test
    default void collectWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().collectWith((x, y) -> "", ""));
    }

    @Test
    default void collectWith_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().collectWith((x, y) -> "", "", new FastList<>()));
    }

    @Test
    default void flatCollect_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().flatCollect(x -> new FastList<>()));
    }

    @Test
    default void flatCollect_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().flatCollect(x -> new FastList<>(), new FastList<>()));
    }

    @Test
    default void collectIf_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().collectIf(x -> true, x1 -> ""));
    }

    @Test
    default void collectIf_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().collectIf(x -> true, x1 -> "", new FastList<>()));
    }

    @Test
    default void select_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().select(x -> true));
    }

    @Test
    default void select_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().select(x -> true, new FastList<>()));
    }

    @Test
    default void selectWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().selectWith((x, y) -> true, 1));
    }

    @Test
    default void selectWith_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().selectWith((x, y) -> true, 1, new FastList<>()));
    }

    @Test
    default void reject_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().reject(x -> true));
    }

    @Test
    default void reject_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().reject(x -> true, new FastList<>()));
    }

    @Test
    default void rejectWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().rejectWith((x, y) -> true, 1));
    }

    @Test
    default void rejectWith_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().rejectWith((x, y) -> true, 1, new FastList<>()));
    }

    @Test
    default void selectInstancesOf_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().selectInstancesOf(Integer.class));
    }

    @Test
    default void partition_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().partition(x -> true));
    }

    @Test
    default void partitionWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().partitionWith((x, y) -> true, 1));
    }

    @Test
    default void selectAndRejectWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().selectAndRejectWith((x, y) -> true, 1));
    }

    @Test
    default void count_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().count(x -> true));
    }

    @Test
    default void countWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().countWith((x, y) -> true, 1));
    }

    @Test
    default void min_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().min());
    }

    @Test
    default void max_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().max());
    }

    @Test
    default void min_withComparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().min((x, y) -> 0));
    }

    @Test
    default void max_withComparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().max((x, y) -> 0));
    }

    @Test
    default void minBy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().minBy(x -> ""));
    }

    @Test
    default void maxBy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().maxBy(x -> ""));
    }

    @Test
    default void injectInto_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().injectInto(0, (Function2<Integer, Integer, Integer>) (x, y) -> 0));
    }

    @Test
    default void injectIntoWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().injectIntoWith(0, (x, y, z) -> 0, 0));
    }

    @Test
    default void sumOfInt_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().sumOfInt(x -> 0));
    }

    @Test
    default void sumOfLong_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().sumOfLong(x -> 0L));
    }

    @Test
    default void sumOfDouble_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().sumOfDouble(x -> 0.0));
    }

    @Test
    default void sumOfFloat_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().sumOfFloat(x -> 0.0f));
    }

    @Test
    default void toString_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toString());
    }

    @Test
    default void makeString_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().makeString());
    }

    @Test
    default void makeString_withSeparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().makeString(", "));
    }

    @Test
    default void makeString_withStartEndSeparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().makeString("[", ", ", "]"));
    }

    @Test
    default void appendString_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().appendString(new StringBuilder()));
    }

    @Test
    default void appendString_withSeparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().appendString(new StringBuilder(), ", "));
    }

    @Test
    default void appendString_withStartEndSeparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().appendString(new StringBuilder(), "[", ", ", "]"));
    }

    @Test
    default void groupBy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().groupBy(x -> ""));
    }

    @Test
    default void groupBy_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().groupBy(x -> "", new FastListMultimap<>()));
    }

    @Test
    default void groupByEach_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().groupByEach(x -> new FastList<>()));
    }

    @Test
    default void groupByEach_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().groupByEach(x -> new FastList<>(), new FastListMultimap<>()));
    }

    @Test
    default void aggregateBy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().aggregateBy(x1 -> "", () -> 0, (x, y) -> 0));
    }

    @Test
    default void aggregateInPlaceBy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().aggregateInPlaceBy(x -> "", () -> 0, (x1, y) -> { }));
    }

    @Test
    default void zip_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().zip(FastList.newListWith("1", "1", "2")));
    }

    @Test
    default void zip_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().zip(FastList.newListWith("1", "1", "2"), new FastList<>()));
    }

    @Test
    default void zipByIndex_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().zipWithIndex());
    }

    @Test
    default void zipByIndex_withTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().zipWithIndex(new FastList<>()));
    }

    @Test
    default void chunk_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().chunk(2));
    }

    @Test
    default void anySatisfy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().anySatisfy(x -> true));
    }

    @Test
    default void anySatisfyWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().anySatisfyWith((x, y) -> true, 1));
    }

    @Test
    default void allSatisfy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().allSatisfy(x -> true));
    }

    @Test
    default void allSatisfyWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().allSatisfyWith((x, y) -> true, 1));
    }

    @Test
    default void noneSatisfy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().noneSatisfy(x -> true));
    }

    @Test
    default void noneSatisfyWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().noneSatisfyWith((x, y) -> true, 1));
    }

    @Test
    default void detect_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().detect(x -> true));
    }

    @Test
    default void detectIfNone_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().detectIfNone(x -> true, () -> 1));
    }

    @Test
    default void detectWith_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().detectWith((x, y) -> true, 1));
    }

    @Test
    default void detectWithIfNone_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().detectWithIfNone((x, y) -> true, 1, () -> 1));
    }

    @Test
    default void asLazy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().asLazy());
    }

    @Test
    default void asUnmodifiable_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().asUnmodifiable());
    }

    @Test
    default void asSynchronized_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().asSynchronized());
    }

    @Test
    default void toImmutable_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toImmutable());
    }

    @Test
    default void toList_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toList());
    }

    @Test
    default void toSortedList_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedList());
    }

    @Test
    default void toSortedList_withComparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedList((x, y) -> 0));
    }

    @Test
    default void toSortedListBy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedListBy(x -> ""));
    }

    @Test
    default void toSet_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSet());
    }

    @Test
    default void toSortedSet_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedSet());
    }

    @Test
    default void toSortedSet_withComparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedSet((x, y) -> 0));
    }

    @Test
    default void toSortedSetBy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedSetBy(x -> ""));
    }

    @Test
    default void toBag_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toBag());
    }

    @Test
    default void toSortedBag_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedBag());
    }

    @Test
    default void toSortedBag_withComparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedBag((x, y) -> 0));
    }

    @Test
    default void toSortedBagBy_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedBagBy(x -> ""));
    }

    @Test
    default void toMap_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toMap(x -> 0, x1 -> 0));
    }

    @Test
    default void toSortedMap_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedMap(x -> 0, x1 -> 0));
    }

    @Test
    default void toSortedMap_withComparator_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toSortedMap((x, y) -> 0, x2 -> 0, x1 -> 0));
    }

    @Test
    default void toSortedMap_withFunction_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () ->
                {
                    this.getClassUnderTest().toSortedMapBy(x -> 0, x -> 0, x -> 0);
                });
    }

    @Test
    default void toArray_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toArray());
    }

    @Test
    default void toArrayWithTarget_safe()
    {
        this.assertThreadSafety(
                false,
                true,
                () -> this.getClassUnderTest().toArray(new Integer[10]));
    }
}
