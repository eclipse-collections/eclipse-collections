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
 * This package contains category annotations for classifying methods in the Eclipse Collections API.
 * <p>
 * These annotations mirror the foldable regions in {@link org.eclipse.collections.api.RichIterable}
 * and serve as a machine-readable classification system for its methods. Each category annotation is
 * itself annotated with {@link org.eclipse.collections.api.annotation.category.Category}, which carries
 * the category name and icon.
 * </p>
 * <p>
 * The following categories are defined, with links to all annotated methods (class-use pages):
 * </p>
 * <ul>
 *     <li><a href="class-use/Aggregating.html">Aggregating 📊</a>
 *         - Methods that reduce or fold elements into a single result</li>
 *     <li><a href="class-use/Converting.html">Converting 🔌</a>
 *         - Methods that convert a collection to another type or representation</li>
 *     <li><a href="class-use/Counting.html">Counting 🔢</a>
 *         - Methods that count elements or occurrences</li>
 *     <li><a href="class-use/Filtering.html">Filtering 🚰</a>
 *         - Methods that select, reject, or partition elements based on a predicate</li>
 *     <li><a href="class-use/Finding.html">Finding 🔎</a>
 *         - Methods that locate specific elements such as first, last, min, or max</li>
 *     <li><a href="class-use/Grouping.html">Grouping 🏘️</a>
 *         - Methods that organize elements into groups by a key function</li>
 *     <li><a href="class-use/Iterating.html">Iterating 🔄</a>
 *         - Methods that traverse elements of a collection</li>
 *     <li><a href="class-use/Mutating.html">Mutating 🧬</a>
 *         - Methods that modify the state or contents of a collection</li>
 *     <li><a href="class-use/Testing.html">Testing 🧪</a>
 *         - Methods that evaluate boolean conditions against elements</li>
 *     <li><a href="class-use/Transforming.html">Transforming 🦋</a>
 *         - Methods that apply a function to each element, producing a new collection</li>
 * </ul>
 */
package org.eclipse.collections.api.annotation.category;
