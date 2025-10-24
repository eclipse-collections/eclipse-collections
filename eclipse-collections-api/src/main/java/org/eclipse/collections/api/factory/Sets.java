/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.factory;

import org.eclipse.collections.api.factory.set.FixedSizeSetFactory;
import org.eclipse.collections.api.factory.set.ImmutableSetFactory;
import org.eclipse.collections.api.factory.set.MultiReaderSetFactory;
import org.eclipse.collections.api.factory.set.MutableSetFactory;

/**
 * A factory class providing convenient static access to create various
 * types of {@link org.eclipse.collections.api.set.Set} and related set factories.
 * <p>
 * This class serves as the main entry point for creating instances of:
 * <ul>
 * <li>{@link org.eclipse.collections.api.set.MutableSet} (via {@link #mutable})</li>
 * <li>{@link org.eclipse.collections.api.set.ImmutableSet} (via {@link #immutable})</li>
 * <li>Fixed-size sets (via {@link #fixedSize})</li>
 * <li>Multi-reader sets (via {@link #multiReader})</li>
 * </ul>
 * <p>
 * The factories support creating empty sets, singleton sets, and pre-populated sets
 * with optimized data structures from the Eclipse Collections framework.
 * </p>
 *
 * Example Usage:
 * <pre>{@code
 * // Creating a mutable set
 * MutableSet<String> names = Sets.mutable.with("Alice", "Bob", "Charlie");
 *
 * // Creating an immutable set
 * ImmutableSet<Integer> numbers = Sets.immutable.of(1, 2, 3);
 * }</pre>
 *
 * @see org.eclipse.collections.api.factory.set.MutableSetFactory
 * @see org.eclipse.collections.api.factory.set.ImmutableSetFactory
 * @see org.eclipse.collections.api.factory.set.FixedSizeSetFactory
 * @see org.eclipse.collections.api.factory.set.MultiReaderSetFactory
 *
 * @since 1.0
 */
@SuppressWarnings("ConstantNamingConvention")
@aQute.bnd.annotation.spi.ServiceConsumer(value = ImmutableSetFactory.class)
@aQute.bnd.annotation.spi.ServiceConsumer(value = MutableSetFactory.class)
@aQute.bnd.annotation.spi.ServiceConsumer(value = FixedSizeSetFactory.class)
@aQute.bnd.annotation.spi.ServiceConsumer(value = MultiReaderSetFactory.class)
public final class Sets
{
    public static final ImmutableSetFactory immutable =
            ServiceLoaderUtils.loadServiceClass(ImmutableSetFactory.class);
    public static final MutableSetFactory mutable =
            ServiceLoaderUtils.loadServiceClass(MutableSetFactory.class);
    public static final FixedSizeSetFactory fixedSize =
            ServiceLoaderUtils.loadServiceClass(FixedSizeSetFactory.class);
    public static final MultiReaderSetFactory multiReader =
            ServiceLoaderUtils.loadServiceClass(MultiReaderSetFactory.class);

    private Sets()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}