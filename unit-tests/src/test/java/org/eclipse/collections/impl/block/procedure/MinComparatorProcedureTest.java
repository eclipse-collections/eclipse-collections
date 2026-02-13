/*
 * Copyright (c) 2021 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure;

import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.test.domain.Holder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class MinComparatorProcedureTest
{
    @Test
    public void value()
    {
        MinComparatorProcedure<Holder<Integer>> procedure = new MinComparatorProcedure<>(Comparators.naturalOrder());
        Holder<Integer> first = new Holder<>(1);
        Holder<Integer> second = new Holder<>(1);
        Holder<Integer> third = new Holder<>(3);
        Holder<Integer> fourth = new Holder<>(0);

        procedure.value(first);
        assertSame(first, procedure.getResult());
        procedure.value(second);
        assertSame(first, procedure.getResult());
        procedure.value(third);
        assertSame(first, procedure.getResult());
        procedure.value(fourth);
        assertSame(fourth, procedure.getResult());
    }
}
