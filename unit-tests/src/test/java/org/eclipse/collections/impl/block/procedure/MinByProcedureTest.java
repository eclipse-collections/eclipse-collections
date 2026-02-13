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

import java.util.Optional;

import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.test.domain.Holder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinByProcedureTest
{
    @Test
    public void visitedAtLeastOnce()
    {
        MinByProcedure<Integer, Integer> procedure = new MinByProcedure<>(Functions.getPassThru());
        assertFalse(procedure.isVisitedAtLeastOnce());
        procedure.value(1);
        assertTrue(procedure.isVisitedAtLeastOnce());
        procedure.value(2);
        assertTrue(procedure.isVisitedAtLeastOnce());
    }

    @Test
    public void getResultOptional()
    {
        MinByProcedure<Integer, Integer> procedure = new MinByProcedure<>(Functions.getPassThru());
        assertFalse(procedure.getResultOptional().isPresent());
        procedure.value(2);
        Optional<Integer> resultOptional = procedure.getResultOptional();
        assertTrue(resultOptional.isPresent());
        assertEquals((Integer) 2, resultOptional.get());

        procedure.value(1);
        Optional<Integer> resultOptional2 = procedure.getResultOptional();
        assertTrue(resultOptional2.isPresent());
        assertEquals((Integer) 1, resultOptional2.get());
    }

    @Test
    public void value()
    {
        MinByProcedure<Holder<Integer>, Holder<Integer>> procedure = new MinByProcedure<>(Functions.getPassThru());
        Holder<Integer> first = new Holder<>(1);
        procedure.value(first);
        assertSame(first, procedure.getResult());
        Holder<Integer> second = new Holder<>(1);
        procedure.value(second);
        assertSame(first, procedure.getResult());
        Holder<Integer> third = new Holder<>(3);
        procedure.value(third);
        assertSame(first, procedure.getResult());
        Holder<Integer> fourth = new Holder<>(0);
        procedure.value(fourth);
        assertSame(fourth, procedure.getResult());
    }
}
