/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.partition;

import org.eclipse.collections.api.RichIterable;

/**
 * A PartitionIterable is the result of splitting an iterable into two iterables based on a Predicate.
 * The results that answer true for the Predicate will be returned from the {@link #getSelected()} method,
 * and the results that answer false for the predicate will be returned from the {@link #getRejected()} method.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Partition a collection by a predicate
 * RichIterable<Integer> numbers = Lists.mutable.with(1, 2, 3, 4, 5, 6);
 * PartitionIterable<Integer> partition = numbers.partition(each -> each % 2 == 0);
 *
 * RichIterable<Integer> evens = partition.getSelected();     // [2, 4, 6]
 * RichIterable<Integer> odds = partition.getRejected();      // [1, 3, 5]
 *
 * // Process selected and rejected separately
 * partition.getSelected().forEach(System.out::println);
 * partition.getRejected().forEach(System.out::println);
 * }</pre>
 *
 * @param <T> the type of elements in the partition
 */
public interface PartitionIterable<T>
{
    /**
     * Returns the elements that satisfied the predicate (returned true).
     *
     * @return the selected elements as a RichIterable
     */
    RichIterable<T> getSelected();

    /**
     * Returns the elements that did not satisfy the predicate (returned false).
     *
     * @return the rejected elements as a RichIterable
     */
    RichIterable<T> getRejected();
}
