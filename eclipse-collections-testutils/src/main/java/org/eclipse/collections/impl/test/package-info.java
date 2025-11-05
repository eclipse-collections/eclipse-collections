/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

/**
 * Provides comprehensive testing utilities for Eclipse Collections.
 * <p>
 * This package contains specialized testing tools designed to simplify and enhance unit testing
 * of Eclipse Collections and standard Java collections. The utilities cover serialization testing,
 * enhanced assertions, and API comparison capabilities.
 * </p>
 *
 * <h2>Main Classes:</h2>
 * <ul>
 *   <li>
 *       {@link org.eclipse.collections.impl.test.Verify} - A comprehensive assertion utility that extends
 *       JUnit's Assert class with specialized assertions for collections, maps, iterables, serialization,
 *       and more. This is the primary testing utility in this package.
 *   </li>
 *   <li>
 *       {@link org.eclipse.collections.impl.test.SerializeTestHelper} - Utilities for testing Java object
 *       serialization and deserialization. Provides methods to serialize objects to byte arrays and
 *       deserialize them back, useful for verifying proper implementation of Serializable.
 *   </li>
 *   <li>
 *       {@link org.eclipse.collections.impl.test.ClassComparer} - A utility for comparing method signatures
 *       between classes. Useful for ensuring API compatibility, tracking changes, and validating class
 *       hierarchies in test scenarios.
 *   </li>
 * </ul>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Using Verify for collection assertions
 * MutableList<String> list = Lists.mutable.of("a", "b", "c");
 * Verify.assertSize(3, list);
 * Verify.assertContains("b", list);
 * Verify.assertAllSatisfy(list, s -> !s.isEmpty());
 *
 * // Testing serialization
 * MutableSet<Integer> set = Sets.mutable.of(1, 2, 3);
 * MutableSet<Integer> copy = SerializeTestHelper.serializeDeserialize(set);
 * Verify.assertEquals(set, copy);
 *
 * // Comparing class APIs
 * ClassComparer comparer = new ClassComparer();
 * MutableSortedSet<String> methods = comparer.getMethodNames(ArrayList.class);
 * }</pre>
 *
 * @see org.eclipse.collections.impl.test.Verify
 * @see org.eclipse.collections.impl.test.SerializeTestHelper
 * @see org.eclipse.collections.impl.test.ClassComparer
 */
package org.eclipse.collections.impl.test;
