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

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.junit.jupiter.api.Test;

public interface SynchronizedRichIterableTestTrait
        extends SynchronizedMutableIterableTestTrait
{
    @Override
    RichIterable<String> getClassUnderTest();

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
    default void notEmpty_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().notEmpty());
    }

    @Test
    default void getFirst_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().getFirst());
    }

    @Test
    default void contains_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().contains(null));
    }

    @Test
    default void containsAllIterable_synchronized()
    {
        this.assertSynchronized(() ->
        {
            Iterable<?> iterable = FastList.newList();
            this.getClassUnderTest().containsAllIterable(iterable);
        });
    }

    @Test
    default void containsAllArguments_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().containsAllArguments("", "", ""));
    }

    @Test
    default void select_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().select(x -> false));
    }

    @Test
    default void select_with_target_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().select(
                        x ->
                                false,
                        FastList.newList()));
    }

    @Test
    default void reject_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().reject(x -> true));
    }

    @Test
    default void reject_with_target_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().reject(
                        x ->
                                true,
                        null));
    }

    @Test
    default void partition_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().partition(x -> true));
    }

    @Test
    default void partitionWith_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().partitionWith(
                        (x, y) -> true,
                        null));
    }

    @Test
    default void collect_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().collect(x -> null));
    }

    @Test
    default void collect_with_target_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().collect(
                        x -> "",
                        FastList.newList()));
    }

    @Test
    default void collectIf_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().collectIf(
                        x ->
                                false,
                        x1 -> ""));
    }

    @Test
    default void collectIf_with_target_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().collectIf(
                        x ->
                                false,
                        x1 -> "",
                        FastList.newList()));
    }

    @Test
    default void flatCollect_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().flatCollect(
                        x ->
                                FastList.newList()));
    }

    @Test
    default void flatCollect_with_target_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().flatCollect(
                        x ->
                                FastList.newList(),
                        FastList.newList()));
    }

    @Test
    default void detect_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().detect(x -> false));
    }

    @Test
    default void detectIfNone_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().detectIfNone(
                        x -> false,
                        () -> ""));
    }

    @Test
    default void count_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().count(x -> false));
    }

    @Test
    default void anySatisfy_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().anySatisfy(x -> true));
    }

    @Test
    default void anySatisfyWith_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().anySatisfyWith(
                        (x, y) -> true,
                        null));
    }

    @Test
    default void allSatisfy_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().allSatisfy(x -> false));
    }

    @Test
    default void allSatisfyWith_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().allSatisfyWith(
                        (x, y) -> false,
                        null));
    }

    @Test
    default void noneSatisfy_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().noneSatisfy(x -> false));
    }

    @Test
    default void noneSatisfyWith_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().noneSatisfyWith(
                        (x, y) -> false,
                        null));
    }

    @Test
    default void injectInto_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().injectInto(
                        "",
                        (x, y) -> ""));
    }

    @Test
    default void toList_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().toList());
    }

    @Test
    default void toSortedList_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().toSortedList());
    }

    @Test
    default void toSortedList_with_comparator_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().toSortedList(null));
    }

    @Test
    default void toSortedListBy_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().toSortedListBy(x -> x));
    }

    @Test
    default void toSet_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().toSet());
    }

    @Test
    default void toMap_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().toMap(
                        x -> "",
                        x1 -> ""));
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
            String[] array = new String[this.getClassUnderTest().size()];
            this.getClassUnderTest().toArray(array);
        });
    }

    @Test
    default void max_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().max(
                        (x, y) -> 0));
    }

    @Test
    default void min_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().min(
                        (x, y) -> 0));
    }

    @Test
    default void max_without_comparator_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().max());
    }

    @Test
    default void min_without_comparator_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().min());
    }

    @Test
    default void maxBy_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().maxBy(x -> x));
    }

    @Test
    default void minBy_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().minBy(x -> x));
    }

    @Test
    default void makeString_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().makeString());
    }

    @Test
    default void makeString_with_separator_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().makeString(", "));
    }

    @Test
    default void makeString_with_start_separator_end_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().makeString("[", ", ", "]"));
    }

    @Test
    default void appendString_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().appendString(new StringBuilder()));
    }

    @Test
    default void appendString_with_separator_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().appendString(new StringBuilder(), ", "));
    }

    @Test
    default void appendString_with_start_separator_end_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().appendString(
                        new StringBuilder(),
                        "[",
                        ", ",
                        "]"));
    }

    @Test
    default void groupBy_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().groupBy(x -> ""));
    }

    @Test
    default void groupBy_with_target_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().groupBy(
                        x -> "",
                        FastListMultimap.newMultimap()));
    }

    @Test
    default void toString_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().toString());
    }

    @Test
    default void zip_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().zip(FastList.newList()));
    }

    @Test
    default void zip_with_target_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().zip(FastList.newList(), FastList.newList()));
    }

    @Test
    default void zipWithIndex_synchronized()
    {
        this.assertSynchronized(() -> this.getClassUnderTest().zipWithIndex());
    }

    @Test
    default void zipWithIndex_with_target_synchronized()
    {
        this.assertSynchronized(() ->
                this.getClassUnderTest().zipWithIndex(FastList.newList()));
    }
}
