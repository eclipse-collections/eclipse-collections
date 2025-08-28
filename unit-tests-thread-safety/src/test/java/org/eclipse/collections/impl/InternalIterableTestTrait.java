/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import org.eclipse.collections.api.InternalIterable;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface InternalIterableTestTrait
        extends IterableTestTrait
{
    @Override
    InternalIterable<String> getClassUnderTest();

    @Test
    default void forEach()
    {
        FastList<String> result = FastList.newList();

        this.getClassUnderTest().forEach(CollectionAddProcedure.on(result));

        Verify.assertSize(3, result);
        Verify.assertContainsAll(result, "1", "2", "3");
    }

    @Test
    default void forEachWithIndex()
    {
        FastList<String> result = FastList.newList();
        FastList<Integer> indices = FastList.newList();

        int[] count = {0};
        this.getClassUnderTest().forEachWithIndex((each, index) ->
        {
            assertEquals(index, count[0]);
            count[0]++;
            result.add(each);
            indices.add(index);
        });

        Verify.assertSize(3, result);
        Verify.assertContainsAll(result, "1", "2", "3");

        Verify.assertSize(3, indices);
        Verify.assertContainsAll(
                indices,
                Integer.valueOf(0),
                Integer.valueOf(1),
                Integer.valueOf(2));
    }

    @Test
    default void forEachWith()
    {
        FastList<String> result = FastList.newList();

        this.getClassUnderTest().forEachWith(
                (each, parameter) -> result.add(each + parameter),
                "!");

        Verify.assertSize(3, result);
        Verify.assertContainsAll(result, "1!", "2!", "3!");
    }
}
