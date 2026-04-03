/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import org.eclipse.collections.api.ordered.OrderedIterable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface OrderedIterableNoIteratorTestCase extends NoIteratorTestCase, OrderedIterableTestCase
{
    @Override
    @Test
    default void Iterable_next()
    {
        NoIteratorTestCase.super.Iterable_next();
    }

    @Override
    @Test
    default void OrderedIterable_takeWhile()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3, 2, 1);
        // TODO Optimize takeWhile() to avoid delegating to iterator().
        assertThrows(AssertionError.class, () -> iterable.takeWhile(each -> each > 2));
    }

    @Override
    @Test
    default void OrderedIterable_dropWhile()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3, 2, 1);
        // TODO Optimize dropWhile() to avoid delegating to iterator().
        assertThrows(AssertionError.class, () -> iterable.dropWhile(each -> each > 2));
    }

    @Override
    @Test
    default void OrderedIterable_partitionWhile()
    {
        OrderedIterable<Integer> iterable = (OrderedIterable<Integer>) this.newWith(6, 5, 4, 3, 2, 1);
        // TODO Optimize partitionWhile() to avoid delegating to iterator().
        assertThrows(AssertionError.class, () -> iterable.partitionWhile(each -> each > 2));
    }
}
