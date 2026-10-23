/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.internal;

import java.util.Objects;

/**
 * A cached, GC-aware accessor for the universe (array of all constants) of an enum type.
 * <p>
 * {@code Class.getEnumConstants()} allocates a defensive copy on every call. This class
 * uses a {@link ClassValue}-based cache to eliminate that overhead for hot-path operations
 * in enum-optimized collections ({@code MutableEnumSet}, {@code MutableEnumMap}, etc.).
 * <p>
 * The backing array is never exposed; all access goes through {@link #size()} and
 * {@link #fromOrdinal(int)}.
 *
 * @since 14.0
 */
public final class EnumUniverse<E extends Enum<E>>
{
    @SuppressWarnings("rawtypes")
    private static final ClassValue<EnumUniverse<?>> CACHE = new ClassValue<>()
    {
        @Override
        protected EnumUniverse<?> computeValue(Class<?> type)
        {
            return new EnumUniverse<>(type.asSubclass(Enum.class).getEnumConstants());
        }
    };

    private final E[] constants;

    private EnumUniverse(E[] constants)
    {
        this.constants = Objects.requireNonNull(constants);
    }

    /**
     * Returns the cached {@code EnumUniverse} for the given enum type.
     *
     * @param enumClass the enum class; must be a direct enum type, not an anonymous subclass
     * @throws IllegalArgumentException if {@code enumClass} is not a direct enum type
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> EnumUniverse<E> forClass(Class<E> enumClass)
    {
        if (!enumClass.isEnum())
        {
            throw new IllegalArgumentException(enumClass + " is not a direct enum type");
        }
        return (EnumUniverse<E>) CACHE.get(enumClass);
    }

    /**
     * Returns the enum constant with the given ordinal.
     */
    public E fromOrdinal(int ordinal)
    {
        return this.constants[ordinal];
    }

    /**
     * Returns the number of constants in the enum type.
     */
    public int size()
    {
        return this.constants.length;
    }
}
