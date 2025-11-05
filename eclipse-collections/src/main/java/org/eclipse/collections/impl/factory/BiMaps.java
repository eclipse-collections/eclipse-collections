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

import org.eclipse.collections.api.factory.bimap.ImmutableBiMapFactory;
import org.eclipse.collections.api.factory.bimap.MutableBiMapFactory;
import org.eclipse.collections.impl.bimap.immutable.ImmutableBiMapFactoryImpl;
import org.eclipse.collections.impl.bimap.mutable.MutableBiMapFactoryImpl;

/**
 * Factory utility class for creating instances of {@link org.eclipse.collections.api.bimap.MutableBiMap}
 * and {@link org.eclipse.collections.api.bimap.ImmutableBiMap}.
 * <p>
 * This class provides static factory instances for creating bidirectional map collections with different characteristics:
 * <ul>
 *   <li>{@link #mutable} - Creates standard mutable bidirectional maps</li>
 *   <li>{@link #immutable} - Creates immutable bidirectional maps</li>
 * </ul>
 * <p>
 * BiMaps maintain bidirectional mappings between keys and values, where both keys and values must be unique.
 * This allows efficient lookups in both directions. Each BiMap maintains an inverse view that swaps
 * keys and values.
 * <p>
 * This class is thread-safe as all factory fields are immutable singletons.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Creating mutable bidirectional maps
 * MutableBiMap<String, Integer> emptyBiMap = BiMaps.mutable.empty();
 * MutableBiMap<String, Integer> biMapWith = BiMaps.mutable.with("one", 1, "two", 2);
 * MutableBiMap<String, Integer> biMapOf = BiMaps.mutable.of("one", 1, "two", 2);
 *
 * // Using inverse view
 * MutableBiMap<Integer, String> inverse = biMapWith.inverse();
 * String key = inverse.get(1); // Returns "one"
 *
 * // Creating immutable bidirectional maps
 * ImmutableBiMap<String, Integer> immutableEmpty = BiMaps.immutable.empty();
 * ImmutableBiMap<String, Integer> immutableWith = BiMaps.immutable.with("one", 1, "two", 2);
 * ImmutableBiMap<String, Integer> immutableOf = BiMaps.immutable.of("one", 1, "two", 2);
 * }</pre>
 *
 * @see org.eclipse.collections.api.bimap.MutableBiMap
 * @see org.eclipse.collections.api.bimap.ImmutableBiMap
 * @see org.eclipse.collections.api.factory.bimap.MutableBiMapFactory
 * @see org.eclipse.collections.api.factory.bimap.ImmutableBiMapFactory
 * @since 6.0
 */
@SuppressWarnings("ConstantNamingConvention")
public final class BiMaps
{
    /**
     * Factory for creating immutable bidirectional map instances.
     * <p>
     * Immutable BiMaps are thread-safe and cannot be modified after creation.
     */
    public static final ImmutableBiMapFactory immutable = ImmutableBiMapFactoryImpl.INSTANCE;

    /**
     * Factory for creating mutable bidirectional map instances.
     * <p>
     * Mutable BiMaps are not thread-safe by default and can be modified after creation.
     */
    public static final MutableBiMapFactory mutable = MutableBiMapFactoryImpl.INSTANCE;

    private BiMaps()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
