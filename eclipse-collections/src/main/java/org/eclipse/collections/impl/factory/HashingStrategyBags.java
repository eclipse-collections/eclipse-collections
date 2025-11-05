/*
 * Copyright (c) 2021 Bhavana Hindupur.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.factory;

import org.eclipse.collections.api.factory.bag.strategy.MutableHashingStrategyBagFactory;
import org.eclipse.collections.impl.bag.strategy.mutable.MutableHashingStrategyBagFactoryImpl;

/**
 * Factory utility class for creating instances of {@link org.eclipse.collections.api.bag.MutableBag}
 * with custom hashing strategies.
 * <p>
 * This class provides static factory instances for creating bag collections that use custom
 * {@link org.eclipse.collections.api.block.HashingStrategy} implementations for element comparison
 * and hashing, rather than relying on the elements' own {@code equals()} and {@code hashCode()} methods.
 * <p>
 * Custom hashing strategies are useful when:
 * <ul>
 *   <li>You need to compare objects based on specific fields rather than their natural equality</li>
 *   <li>You're working with objects that don't override {@code equals()} and {@code hashCode()}</li>
 *   <li>You need case-insensitive string comparisons or other custom comparison logic</li>
 * </ul>
 * <p>
 * This class is thread-safe as all factory fields are immutable singletons.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create a case-insensitive string bag
 * HashingStrategy<String> caseInsensitive = HashingStrategies.fromFunction(String::toLowerCase);
 * MutableBag<String> bag = HashingStrategyBags.mutable.of(caseInsensitive);
 * bag.add("Hello");
 * bag.add("HELLO");
 * bag.add("hello");
 * // bag.size() == 3, but all are considered the same for uniqueness purposes
 *
 * // Create a bag with custom Person hashing based on ID only
 * HashingStrategy<Person> idStrategy = HashingStrategies.fromFunction(Person::getId);
 * MutableBag<Person> personBag = HashingStrategyBags.mutable.with(
 *     idStrategy,
 *     new Person(1, "John"),
 *     new Person(2, "Jane")
 * );
 * }</pre>
 *
 * @see org.eclipse.collections.api.bag.MutableBag
 * @see org.eclipse.collections.api.block.HashingStrategy
 * @see org.eclipse.collections.api.factory.bag.strategy.MutableHashingStrategyBagFactory
 * @see HashingStrategies
 */
@SuppressWarnings("ConstantNamingConvention")
public final class HashingStrategyBags
{
    /**
     * Factory for creating mutable bag instances with custom hashing strategies.
     * <p>
     * Bags created through this factory use the provided {@link org.eclipse.collections.api.block.HashingStrategy}
     * for element comparison and hashing instead of the elements' natural {@code equals()} and {@code hashCode()}.
     */
    public static final MutableHashingStrategyBagFactory mutable = MutableHashingStrategyBagFactoryImpl.INSTANCE;

    private HashingStrategyBags()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
