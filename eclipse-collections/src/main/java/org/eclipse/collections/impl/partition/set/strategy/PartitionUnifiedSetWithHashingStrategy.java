/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.partition.set.strategy;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.partition.set.PartitionImmutableSet;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.partition.set.PartitionImmutableSetImpl;
import org.eclipse.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

/**
 * A mutable implementation of {@link PartitionMutableSet} that uses {@link UnifiedSetWithHashingStrategy}
 * to store elements that are partitioned based on a predicate.
 * <p>
 * This class maintains two separate sets: one for elements that satisfy the predicate (selected)
 * and one for elements that do not (rejected). The underlying implementation uses
 * {@link UnifiedSetWithHashingStrategy}, which allows for custom hashing and equality strategies
 * and provides O(1) performance for add, remove, and contains operations.
 * </p>
 * <p>
 * Both selected and rejected sets use the same hashing strategy for consistent behavior.
 * Sets do not maintain insertion order and do not allow duplicate elements (as defined by the hashing strategy).
 * </p>
 * <p>
 * This implementation is not thread-safe.
 * </p>
 *
 * @param <T> the type of elements in this partition
 * @see PartitionMutableSet
 * @see UnifiedSetWithHashingStrategy
 * @see HashingStrategy
 * @since 1.0
 */
public class PartitionUnifiedSetWithHashingStrategy<T>
        implements PartitionMutableSet<T>
{
    private final MutableSet<T> selected;
    private final MutableSet<T> rejected;

    /**
     * Constructs a new partition with the specified hashing strategy for both sets.
     * <p>
     * The hashing strategy is used to determine equality and compute hash codes for elements
     * in both the selected and rejected sets. This allows for custom equality semantics beyond
     * the default {@link Object#equals(Object)} and {@link Object#hashCode()}.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Create a partition with case-insensitive string comparison
     * HashingStrategy<String> caseInsensitive = HashingStrategies.fromFunction(String::toLowerCase);
     * PartitionUnifiedSetWithHashingStrategy<String> partition =
     *     new PartitionUnifiedSetWithHashingStrategy<>(caseInsensitive);
     *
     * // Create a partition comparing persons by their ID
     * HashingStrategy<Person> byId = HashingStrategies.fromFunction(Person::getId);
     * PartitionUnifiedSetWithHashingStrategy<Person> personPartition =
     *     new PartitionUnifiedSetWithHashingStrategy<>(byId);
     * }</pre>
     *
     * @param hashingStrategy the hashing strategy used for both selected and rejected sets
     */
    public PartitionUnifiedSetWithHashingStrategy(HashingStrategy<? super T> hashingStrategy)
    {
        this.selected = UnifiedSetWithHashingStrategy.newSet(hashingStrategy);
        this.rejected = UnifiedSetWithHashingStrategy.newSet(hashingStrategy);
    }

    /**
     * Returns the mutable set containing elements that satisfied the partition predicate.
     * <p>
     * The returned set is backed by this partition, so modifications to the returned set
     * are reflected in this partition and vice-versa. The set uses the hashing strategy
     * provided during construction.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * HashingStrategy<String> caseInsensitive = HashingStrategies.fromFunction(String::toLowerCase);
     * PartitionUnifiedSetWithHashingStrategy<String> partition =
     *     new PartitionUnifiedSetWithHashingStrategy<>(caseInsensitive);
     * partition.getSelected().add("Hello");
     * partition.getSelected().add("World");
     * // partition.getSelected() now contains {"Hello", "World"}
     * }</pre>
     *
     * @return the mutable set of selected elements
     */
    @Override
    public MutableSet<T> getSelected()
    {
        return this.selected;
    }

    /**
     * Returns the mutable set containing elements that did not satisfy the partition predicate.
     * <p>
     * The returned set is backed by this partition, so modifications to the returned set
     * are reflected in this partition and vice-versa. The set uses the hashing strategy
     * provided during construction.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * HashingStrategy<String> caseInsensitive = HashingStrategies.fromFunction(String::toLowerCase);
     * PartitionUnifiedSetWithHashingStrategy<String> partition =
     *     new PartitionUnifiedSetWithHashingStrategy<>(caseInsensitive);
     * partition.getRejected().add("foo");
     * partition.getRejected().add("bar");
     * // partition.getRejected() now contains {"foo", "bar"}
     * }</pre>
     *
     * @return the mutable set of rejected elements
     */
    @Override
    public MutableSet<T> getRejected()
    {
        return this.rejected;
    }

    /**
     * Converts this partition to an immutable partition with the same selected and rejected elements.
     * <p>
     * The returned immutable partition is a snapshot of the current state. Changes to this
     * mutable partition after calling this method will not be reflected in the returned
     * immutable partition. Note that the immutable partition does not retain the hashing strategy.
     * </p>
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * HashingStrategy<String> caseInsensitive = HashingStrategies.fromFunction(String::toLowerCase);
     * PartitionUnifiedSetWithHashingStrategy<String> partition =
     *     new PartitionUnifiedSetWithHashingStrategy<>(caseInsensitive);
     * partition.getSelected().add("Hello");
     * partition.getRejected().add("World");
     * PartitionImmutableSet<String> immutable = partition.toImmutable();
     * // immutable now contains a snapshot of the partition
     * }</pre>
     *
     * @return an immutable partition containing the same elements as this partition
     */
    @Override
    public PartitionImmutableSet<T> toImmutable()
    {
        return new PartitionImmutableSetImpl<>(this);
    }
}
