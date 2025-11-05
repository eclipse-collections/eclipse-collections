/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.factory;

import org.eclipse.collections.api.factory.set.strategy.ImmutableHashingStrategySetFactory;
import org.eclipse.collections.api.factory.set.strategy.MutableHashingStrategySetFactory;
import org.eclipse.collections.impl.set.strategy.immutable.ImmutableHashingStrategySetFactoryImpl;
import org.eclipse.collections.impl.set.strategy.mutable.MutableHashingStrategySetFactoryImpl;

/**
 * Factory utility class for creating instances of {@link org.eclipse.collections.api.set.MutableSet}
 * and {@link org.eclipse.collections.api.set.ImmutableSet} with custom hashing strategies.
 * <p>
 * This class provides static factory instances for creating set collections that use custom
 * {@link org.eclipse.collections.api.block.HashingStrategy} implementations for element comparison
 * and hashing, rather than relying on the elements' own {@code equals()} and {@code hashCode()} methods.
 * <p>
 * Custom hashing strategies are useful when:
 * <ul>
 *   <li>You need to compare elements based on specific fields rather than their natural equality</li>
 *   <li>You're working with elements that don't override {@code equals()} and {@code hashCode()}</li>
 *   <li>You need case-insensitive string comparisons or other custom comparison logic</li>
 *   <li>You want to use identity comparison (==) instead of equals for uniqueness checks</li>
 * </ul>
 * <p>
 * This class is thread-safe as all factory fields are immutable singletons.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create a case-insensitive string set
 * HashingStrategy<String> caseInsensitive = HashingStrategies.fromFunction(String::toLowerCase);
 * MutableSet<String> set = HashingStrategySets.mutable.of(caseInsensitive);
 * set.add("Hello");
 * set.add("HELLO");
 * set.add("hello");
 * // set.size() == 1, all three strings are considered equal
 *
 * // Create a set with custom Person hashing based on ID only
 * HashingStrategy<Person> idStrategy = HashingStrategies.fromFunction(Person::getId);
 * MutableSet<Person> personSet = HashingStrategySets.mutable.with(
 *     idStrategy,
 *     new Person(1, "John"),
 *     new Person(2, "Jane")
 * );
 *
 * // Create immutable set with custom hashing strategy
 * ImmutableSet<String> immutableSet = HashingStrategySets.immutable.of(caseInsensitive);
 * }</pre>
 *
 * @see org.eclipse.collections.api.set.MutableSet
 * @see org.eclipse.collections.api.set.ImmutableSet
 * @see org.eclipse.collections.api.block.HashingStrategy
 * @see org.eclipse.collections.api.factory.set.strategy.MutableHashingStrategySetFactory
 * @see org.eclipse.collections.api.factory.set.strategy.ImmutableHashingStrategySetFactory
 * @see HashingStrategies
 */
@SuppressWarnings("ConstantNamingConvention")
public final class HashingStrategySets
{
    /**
     * Factory for creating immutable set instances with custom hashing strategies.
     * <p>
     * Sets created through this factory use the provided {@link org.eclipse.collections.api.block.HashingStrategy}
     * for element comparison and hashing. Immutable sets are thread-safe and cannot be modified after creation.
     */
    public static final ImmutableHashingStrategySetFactory immutable = ImmutableHashingStrategySetFactoryImpl.INSTANCE;

    /**
     * Factory for creating mutable set instances with custom hashing strategies.
     * <p>
     * Sets created through this factory use the provided {@link org.eclipse.collections.api.block.HashingStrategy}
     * for element comparison and hashing. Mutable sets are not thread-safe by default and can be modified after creation.
     */
    public static final MutableHashingStrategySetFactory mutable = MutableHashingStrategySetFactoryImpl.INSTANCE;

    private HashingStrategySets()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
