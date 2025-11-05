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
 * Provides parallel processing implementations using Java's ForkJoin framework for Eclipse Collections.
 * <p>
 * This package contains the implementation classes that enable efficient parallel processing of collections
 * using the ForkJoin framework introduced in Java 7. The main entry point is {@link org.eclipse.collections.impl.forkjoin.FJIterate},
 * which provides static methods for parallel operations on collections.
 * </p>
 *
 * <h2>Key Components</h2>
 * <ul>
 * <li><b>FJIterate:</b> Main utility class providing parallel algorithms (forEach, select, collect, etc.)</li>
 * <li><b>Task Classes:</b> ForkJoinTask implementations that execute procedures on collection sections</li>
 * <li><b>Runner Classes:</b> Coordinate parallel task execution and combine results</li>
 * </ul>
 *
 * <h2>Performance Characteristics</h2>
 * <p>
 * The ForkJoin implementations divide collections into batches and process them in parallel using a shared
 * thread pool. This approach is most effective for:
 * </p>
 * <ul>
 * <li>Large collections (typically 5000+ elements)</li>
 * <li>CPU-intensive operations on each element</li>
 * <li>Operations that can benefit from parallel execution</li>
 * </ul>
 *
 * <p>
 * For small collections or I/O-bound operations, sequential processing may be more efficient due to
 * the overhead of task coordination and thread management.
 * </p>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * When using parallel operations, procedures must be either stateless or use thread-safe data structures.
 * The framework handles task coordination and result combination, but procedures are responsible for
 * their own thread safety.
 * </p>
 *
 * @see org.eclipse.collections.impl.forkjoin.FJIterate
 * @see java.util.concurrent.ForkJoinPool
 * @see java.util.concurrent.ForkJoinTask
 */
package org.eclipse.collections.impl.forkjoin;
