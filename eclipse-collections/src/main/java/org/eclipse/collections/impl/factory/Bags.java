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

import org.eclipse.collections.api.factory.bag.ImmutableBagFactory;
import org.eclipse.collections.api.factory.bag.MultiReaderBagFactory;
import org.eclipse.collections.api.factory.bag.MutableBagFactory;
import org.eclipse.collections.impl.bag.immutable.ImmutableBagFactoryImpl;
import org.eclipse.collections.impl.bag.mutable.MultiReaderMutableBagFactory;
import org.eclipse.collections.impl.bag.mutable.MutableBagFactoryImpl;

/**
 * Factory utility class for creating instances of {@link org.eclipse.collections.api.bag.MutableBag}
 * and {@link org.eclipse.collections.api.bag.ImmutableBag}.
 * <p>
 * This class provides static factory instances for creating bag collections with different characteristics:
 * <ul>
 *   <li>{@link #mutable} - Creates standard mutable bags</li>
 *   <li>{@link #immutable} - Creates immutable bags</li>
 *   <li>{@link #multiReader} - Creates thread-safe multi-reader bags</li>
 * </ul>
 * <p>
 * Bags are collections that allow duplicate elements and track the number of occurrences of each element.
 * Unlike lists, bags do not maintain insertion order.
 * <p>
 * This class is thread-safe as all factory fields are immutable singletons.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Creating mutable bags
 * MutableBag<String> emptyBag = Bags.mutable.empty();
 * MutableBag<String> bagWith = Bags.mutable.with("a", "b", "c");
 * MutableBag<String> bagOf = Bags.mutable.of("a", "b", "c");
 * MutableBag<String> fromIterable = Bags.mutable.ofAll(Arrays.asList("a", "b"));
 *
 * // Creating immutable bags
 * ImmutableBag<String> immutableEmpty = Bags.immutable.empty();
 * ImmutableBag<String> immutableWith = Bags.immutable.with("a", "b", "c");
 * ImmutableBag<String> immutableOf = Bags.immutable.of("a", "b", "c");
 *
 * // Creating multi-reader bags (thread-safe)
 * MutableBag<String> multiReaderBag = Bags.multiReader.empty();
 * MutableBag<String> multiReaderWith = Bags.multiReader.with("a", "b", "c");
 * }</pre>
 *
 * @see org.eclipse.collections.api.bag.MutableBag
 * @see org.eclipse.collections.api.bag.ImmutableBag
 * @see org.eclipse.collections.api.factory.bag.MutableBagFactory
 * @see org.eclipse.collections.api.factory.bag.ImmutableBagFactory
 * @see org.eclipse.collections.api.factory.bag.MultiReaderBagFactory
 */

@SuppressWarnings("ConstantNamingConvention")
public final class Bags
{
    /**
     * Factory for creating immutable bag instances.
     * <p>
     * Immutable bags are thread-safe and cannot be modified after creation.
     */
    public static final ImmutableBagFactory immutable = ImmutableBagFactoryImpl.INSTANCE;

    /**
     * Factory for creating mutable bag instances.
     * <p>
     * Mutable bags are not thread-safe by default and can be modified after creation.
     */
    public static final MutableBagFactory mutable = MutableBagFactoryImpl.INSTANCE;

    /**
     * Factory for creating multi-reader mutable bag instances.
     * <p>
     * Multi-reader bags provide thread-safe read operations and require explicit locking for write operations.
     */
    public static final MultiReaderBagFactory multiReader = MultiReaderMutableBagFactory.INSTANCE;

    private Bags()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
