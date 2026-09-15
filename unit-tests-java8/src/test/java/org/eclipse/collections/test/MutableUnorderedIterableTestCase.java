/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.Iterator;

import org.eclipse.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.impl.test.Verify.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface MutableUnorderedIterableTestCase extends UnorderedIterableTestCase
{
    @Override
    @Test
    default void Iterable_remove()
    {
        if (!this.allowsRemove())
        {
            Iterable<Integer> iterable = this.newWith(3, 2, 1);
            Iterator<Integer> iterator = iterable.iterator();
            iterator.next();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
            return;
        }

        Iterable<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);
        Iterator<Integer> iterator = iterable.iterator();
        iterator.next();
        iterator.remove();
        assertEquals(this.allowsDuplicates() ? 5 : 2, Iterate.sizeOf(iterable));
        assertThat(
                iterable,
                isOneOf(
                        this.newWith(3, 3, 3, 2, 2),
                        this.newWith(3, 3, 3, 2, 1),
                        this.newWith(3, 3, 2, 2, 1)));
    }
}
