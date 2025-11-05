/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.partition.bag;

import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.partition.PartitionIterable;

/**
 * A PartitionBag is the result of splitting a bag into two Bags based on a Predicate.
 * The results that answer true for the Predicate will be returned from the {@link #getSelected()} method,
 * and the results that answer false for the predicate will be returned from the {@link #getRejected()} method.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * Bag<Integer> numbers = Bags.mutable.with(1, 2, 2, 3, 4, 4, 4, 5);
 * PartitionBag<Integer> partition = numbers.partition(each -> each % 2 == 0);
 *
 * Bag<Integer> evens = partition.getSelected();     // [2, 2, 4, 4, 4]
 * Bag<Integer> odds = partition.getRejected();      // [1, 3, 5]
 * }</pre>
 *
 * @param <T> the type of elements in the partition
 */
public interface PartitionBag<T> extends PartitionIterable<T>
{
    /**
     * Returns the bag of elements that satisfied the predicate.
     *
     * @return the selected elements as a Bag
     */
    @Override
    Bag<T> getSelected();

    /**
     * Returns the bag of elements that did not satisfy the predicate.
     *
     * @return the rejected elements as a Bag
     */
    @Override
    Bag<T> getRejected();
}
