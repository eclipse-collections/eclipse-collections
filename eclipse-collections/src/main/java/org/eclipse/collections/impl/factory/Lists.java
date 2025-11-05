/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.factory;

import java.util.List;

import org.eclipse.collections.api.factory.list.FixedSizeListFactory;
import org.eclipse.collections.api.factory.list.ImmutableListFactory;
import org.eclipse.collections.api.factory.list.MultiReaderListFactory;
import org.eclipse.collections.api.factory.list.MutableListFactory;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.fixed.FixedSizeListFactoryImpl;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;
import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.eclipse.collections.impl.list.mutable.MultiReaderMutableListFactory;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

/**
 * Factory utility class for creating instances of {@link org.eclipse.collections.api.list.MutableList},
 * {@link org.eclipse.collections.api.list.ImmutableList}, and {@link org.eclipse.collections.api.list.FixedSizeList}.
 * <p>
 * This class provides static factory instances for creating list collections with different characteristics:
 * <ul>
 *   <li>{@link #mutable} - Creates standard mutable lists</li>
 *   <li>{@link #immutable} - Creates immutable lists</li>
 *   <li>{@link #fixedSize} - Creates fixed-size lists that cannot grow or shrink but allow element replacement</li>
 *   <li>{@link #multiReader} - Creates thread-safe multi-reader lists</li>
 * </ul>
 * <p>
 * Lists maintain insertion order and allow duplicate elements.
 * <p>
 * This class is thread-safe as all factory fields are immutable singletons.
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Creating mutable lists
 * MutableList<String> emptyList = Lists.mutable.empty();
 * MutableList<String> listWith = Lists.mutable.with("a", "b", "c");
 * MutableList<String> listOf = Lists.mutable.of("a", "b", "c");
 *
 * // Creating immutable lists
 * ImmutableList<String> immutableEmpty = Lists.immutable.empty();
 * ImmutableList<String> immutableWith = Lists.immutable.with("a", "b", "c");
 * ImmutableList<String> immutableOf = Lists.immutable.of("a", "b", "c");
 *
 * // Creating fixed-size lists
 * FixedSizeList<String> fixedEmpty = Lists.fixedSize.empty();
 * FixedSizeList<String> fixedWith = Lists.fixedSize.with("a", "b", "c");
 * FixedSizeList<String> fixedOf = Lists.fixedSize.of("a", "b", "c");
 *
 * // Creating multi-reader lists (thread-safe)
 * MutableList<String> multiReaderList = Lists.multiReader.empty();
 *
 * // Adapting JDK lists
 * List<String> jdkList = new ArrayList<>();
 * MutableList<String> adapted = Lists.adapt(jdkList);
 * }</pre>
 *
 * @see org.eclipse.collections.api.list.MutableList
 * @see org.eclipse.collections.api.list.ImmutableList
 * @see org.eclipse.collections.api.list.FixedSizeList
 * @see org.eclipse.collections.api.factory.list.MutableListFactory
 * @see org.eclipse.collections.api.factory.list.ImmutableListFactory
 * @see org.eclipse.collections.api.factory.list.FixedSizeListFactory
 * @see org.eclipse.collections.api.factory.list.MultiReaderListFactory
 */
@SuppressWarnings("ConstantNamingConvention")
public final class Lists
{
    /**
     * Factory for creating immutable list instances.
     * <p>
     * Immutable lists are thread-safe and cannot be modified after creation.
     */
    public static final ImmutableListFactory immutable = ImmutableListFactoryImpl.INSTANCE;

    /**
     * Factory for creating mutable list instances.
     * <p>
     * Mutable lists are not thread-safe by default and can be modified after creation.
     */
    public static final MutableListFactory mutable = MutableListFactoryImpl.INSTANCE;

    /**
     * Factory for creating fixed-size list instances.
     * <p>
     * Fixed-size lists cannot grow or shrink, but elements can be replaced. Operations like
     * {@code add()} and {@code remove()} will throw {@code UnsupportedOperationException}.
     */
    public static final FixedSizeListFactory fixedSize = FixedSizeListFactoryImpl.INSTANCE;

    /**
     * Factory for creating multi-reader mutable list instances.
     * <p>
     * Multi-reader lists provide thread-safe read operations and require explicit locking for write operations.
     */
    public static final MultiReaderListFactory multiReader = MultiReaderMutableListFactory.INSTANCE;

    private Lists()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Adapts a JDK {@link List} to a {@link MutableList}, providing a view that supports
     * Eclipse Collections APIs.
     * <p>
     * The returned list is a wrapper around the original list. Changes to either list
     * are visible in the other. This method is useful for interoperability with JDK collections.
     * <p>
     * The adapted list is not synchronized. If the original list is synchronized or
     * thread-safe, the adapted list will maintain those characteristics.
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * List<String> jdkList = new ArrayList<>(Arrays.asList("a", "b", "c"));
     * MutableList<String> ecList = Lists.adapt(jdkList);
     *
     * // Changes are reflected in both
     * ecList.add("d");
     * assert jdkList.size() == 4;
     *
     * // Eclipse Collections methods work
     * MutableList<String> filtered = ecList.select(s -> s.length() > 1);
     * }</pre>
     *
     * @param <T> the type of elements in the list
     * @param list the JDK list to adapt
     * @return a mutable list that wraps the provided JDK list
     * @throws NullPointerException if list is null
     * @see org.eclipse.collections.impl.list.mutable.ListAdapter
     * @since 9.0
     */
    public static <T> MutableList<T> adapt(List<T> list)
    {
        return ListAdapter.adapt(list);
    }
}
