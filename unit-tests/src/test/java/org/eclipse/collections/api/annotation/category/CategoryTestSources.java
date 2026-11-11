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

import java.nio.file.Path;
import java.util.stream.Stream;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Argument sources for the method-category tests. Each argument is a {@code (Class, Path)} pair: the type under test and
 * the source file holding its Javadoc category index.
 * <p>
 * "Automatic" types are expected to have their Javadoc index match reflection exactly, while "manual" types are only
 * expected to contain every reflected method and no unknown categories.
 */
final class CategoryTestSources
{
    private static final Path API_SOURCE_ROOT = Path.of("..", "eclipse-collections-api", "src", "main", "java");
    private static final Path COLLECTIONS_SOURCE_ROOT = Path.of("..", "eclipse-collections", "src", "main", "java");

    private CategoryTestSources()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    static Stream<Arguments> all()
    {
        return Stream.concat(automatic(), manual());
    }

    static Stream<Arguments> automatic()
    {
        return Stream.of(
                Arguments.of(MutableList.class, sourcePath(API_SOURCE_ROOT, MutableList.class)),
                Arguments.of(ImmutableList.class, sourcePath(API_SOURCE_ROOT, ImmutableList.class)),
                Arguments.of(ListIterable.class, sourcePath(API_SOURCE_ROOT, ListIterable.class)),
                Arguments.of(Iterate.class, sourcePath(COLLECTIONS_SOURCE_ROOT, Iterate.class)));
    }

    static Stream<Arguments> manual()
    {
        return Stream.of(
                Arguments.of(RichIterable.class, sourcePath(API_SOURCE_ROOT, RichIterable.class)));
    }

    private static Path sourcePath(Path sourceRoot, Class<?> type)
    {
        return sourceRoot.resolve(type.getName().replace('.', '/') + ".java");
    }
}
