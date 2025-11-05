/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.partition.list;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.partition.PartitionImmutableCollection;

/**
 * A PartitionImmutableList is the result of splitting an immutable list into two immutable lists based on a Predicate.
 * The results that answer true for the Predicate will be returned from the {@link #getSelected()} method,
 * and the results that answer false for the predicate will be returned from the {@link #getRejected()} method.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * ImmutableList<Integer> numbers = Lists.immutable.with(1, 2, 3, 4, 5, 6);
 * PartitionImmutableList<Integer> partition = numbers.partition(each -> each % 2 == 0);
 *
 * ImmutableList<Integer> evens = partition.getSelected();  // [2, 4, 6]
 * ImmutableList<Integer> odds = partition.getRejected();   // [1, 3, 5]
 * }</pre>
 *
 * @param <T> the type of elements in the partition
 */
public interface PartitionImmutableList<T> extends PartitionImmutableCollection<T>, PartitionList<T>
{
    /**
     * Returns the immutable list of elements that satisfied the predicate.
     *
     * @return the selected elements as an ImmutableList
     */
    @Override
    ImmutableList<T> getSelected();

    /**
     * Returns the immutable list of elements that did not satisfy the predicate.
     *
     * @return the rejected elements as an ImmutableList
     */
    @Override
    ImmutableList<T> getRejected();
}
