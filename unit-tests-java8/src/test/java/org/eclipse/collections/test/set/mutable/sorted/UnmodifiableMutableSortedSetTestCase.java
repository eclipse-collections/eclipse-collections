/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.set.mutable.sorted;

import org.eclipse.collections.test.UnmodifiableMutableCollectionTestCase;
import org.eclipse.collections.test.set.sorted.UnmodifiableNavigableSetTestCase;
import org.junit.jupiter.api.Test;

public interface UnmodifiableMutableSortedSetTestCase extends UnmodifiableMutableCollectionTestCase, UnmodifiableNavigableSetTestCase, MutableSortedSetTestCase
{
    @Override
    @Test
    default void Iterable_remove()
    {
        UnmodifiableNavigableSetTestCase.super.Iterable_remove();
    }
}
