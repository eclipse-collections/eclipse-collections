/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.block;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A serializable version of {@link Comparator}. This interface allows comparators to be serialized,
 * which is useful in distributed computing scenarios or when persisting comparator implementations.
 * <p>
 * This is a functional interface whose functional method is {@link #compare(Object, Object)}.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>{@code
 * // Create a serializable comparator for Person by age
 * SerializableComparator<Person> byAge = (p1, p2) ->
 *     Integer.compare(p1.getAge(), p2.getAge());
 *
 * // Use with Eclipse Collections sorted structures
 * MutableSortedSet<Person> people = SortedSets.mutable.with(byAge);
 *
 * // Can be serialized and deserialized
 * byte[] bytes = SerializeUtils.serialize(byAge);
 * SerializableComparator<Person> restored = SerializeUtils.deserialize(bytes);
 * }</pre>
 *
 * @param <T> the type of objects that may be compared by this comparator
 * @see Comparator
 * @see SerializableComparators
 */
@FunctionalInterface
public interface SerializableComparator<T>
        extends Comparator<T>, Serializable
{
}
