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

import org.eclipse.collections.api.factory.map.strategy.ImmutableHashingStrategyMapFactory;
import org.eclipse.collections.api.factory.map.strategy.MutableHashingStrategyMapFactory;
import org.eclipse.collections.impl.map.strategy.immutable.ImmutableHashingStrategyMapFactoryImpl;
import org.eclipse.collections.impl.map.strategy.mutable.MutableHashingStrategyMapFactoryImpl;

/**
 * Factory utility class for creating instances of {@link org.eclipse.collections.api.map.MutableMap}
 * and {@link org.eclipse.collections.api.map.ImmutableMap} with custom hashing strategies for keys.
 * <p>
 * This class provides static factory instances for creating map collections that use custom
 * {@link org.eclipse.collections.api.block.HashingStrategy} implementations for key comparison
 * and hashing, rather than relying on the keys' own {@code equals()} and {@code hashCode()} methods.
 * <p>
 * Custom hashing strategies are useful when:
 * <ul>
 *   <li>You need to compare keys based on specific fields rather than their natural equality</li>
 *   <li>You're working with keys that don't override {@code equals()} and {@code hashCode()}</li>
 *   <li>You need case-insensitive string keys or other custom comparison logic</li>
 *   <li>You want to use identity comparison (==) instead of equals</li>
 * </ul>
 * <p>
 * This class is thread-safe as all factory fields are immutable singletons.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create a case-insensitive string-keyed map
 * HashingStrategy<String> caseInsensitive = HashingStrategies.fromFunction(String::toLowerCase);
 * MutableMap<String, Integer> map = HashingStrategyMaps.mutable.of(caseInsensitive);
 * map.put("Hello", 1);
 * map.put("HELLO", 2);
 * // map.get("hello") returns 2, all three keys are considered equal
 *
 * // Create a map with identity-based key hashing
 * HashingStrategy<Person> identityStrategy = HashingStrategies.identityStrategy();
 * MutableMap<Person, String> personMap = HashingStrategyMaps.mutable.with(
 *     identityStrategy,
 *     person1, "John",
 *     person2, "Jane"
 * );
 *
 * // Create immutable map with custom hashing strategy
 * ImmutableMap<String, Integer> immutableMap = HashingStrategyMaps.immutable.of(caseInsensitive);
 * }</pre>
 *
 * @see org.eclipse.collections.api.map.MutableMap
 * @see org.eclipse.collections.api.map.ImmutableMap
 * @see org.eclipse.collections.api.block.HashingStrategy
 * @see org.eclipse.collections.api.factory.map.strategy.MutableHashingStrategyMapFactory
 * @see org.eclipse.collections.api.factory.map.strategy.ImmutableHashingStrategyMapFactory
 * @see HashingStrategies
 */
@SuppressWarnings("ConstantNamingConvention")
public final class HashingStrategyMaps
{
    /**
     * Factory for creating immutable map instances with custom hashing strategies for keys.
     * <p>
     * Maps created through this factory use the provided {@link org.eclipse.collections.api.block.HashingStrategy}
     * for key comparison and hashing. Immutable maps are thread-safe and cannot be modified after creation.
     */
    public static final ImmutableHashingStrategyMapFactory immutable = ImmutableHashingStrategyMapFactoryImpl.INSTANCE;

    /**
     * Factory for creating mutable map instances with custom hashing strategies for keys.
     * <p>
     * Maps created through this factory use the provided {@link org.eclipse.collections.api.block.HashingStrategy}
     * for key comparison and hashing. Mutable maps are not thread-safe by default and can be modified after creation.
     */
    public static final MutableHashingStrategyMapFactory mutable = MutableHashingStrategyMapFactoryImpl.INSTANCE;

    private HashingStrategyMaps()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
