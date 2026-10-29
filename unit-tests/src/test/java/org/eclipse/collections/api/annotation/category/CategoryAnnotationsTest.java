/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.annotation.category;

import org.eclipse.collections.impl.test.category.CategoryIndex;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that every declared method of the categorised types carries at least one category annotation.
 */
class CategoryAnnotationsTest
{
    @ParameterizedTest
    @MethodSource("org.eclipse.collections.api.annotation.category.CategoryTestSources#all")
    void allDeclaredMethodsAreAnnotated(Class<?> type)
    {
        CategoryIndex index = CategoryIndex.fromClass(type);
        assertTrue(
                index.methodsWithoutCategory().isEmpty(),
                () -> type.getSimpleName() + " has methods without a category annotation: "
                        + index.methodsWithoutCategory());
    }
}
