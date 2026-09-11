/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

/**
 * This package contains static utilities that provide internal iteration pattern implementations which work with JCF collections.
 * <p>
 *     All the iteration patterns in this package are internal. It is used by iterators specialized for various collections.
 * <p>
 *     This package contains the following internal utilities:
 * <ul>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.DefaultSpeciesNewStrategy} - creates a new instance of a collection based on the class type of collection.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.EnumUniverse} - a cached, GC-aware accessor for the universe (array of all constants) of an enum type.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.InternalArrayIterate} -  a final class with static iterator methods.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.IterableIterate} - provides a few of the methods from the Smalltalk Collection Protocol.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.IteratorIterate} - provides various iteration patterns for use with {@link java.util.Iterator}.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.MutableCollectionIterate} - a final class used to chunk {@link org.eclipse.collections.api.collection.MutableCollection}.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.RandomAccessListIterate} - provides methods from the Smalltalk Collection Protocol for use with {@link java.util.ArrayList}.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.ReflectionHelper} - a utility/helper class for working with Classes and Reflection.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.SetIterables} - a class provides for set algebra operations.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.SetIterate} - a final class used for internal purposes to iterate over Set.
 *     </li>
 *     <li>
 *          {@link org.eclipse.collections.impl.utility.internal.SortedSetIterables} - a class provides for sortedSet algebra operations.
 *     </li>
 * </ul>
 */
package org.eclipse.collections.impl.utility.internal;
