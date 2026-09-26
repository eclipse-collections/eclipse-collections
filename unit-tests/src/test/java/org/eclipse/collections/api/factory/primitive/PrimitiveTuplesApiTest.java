/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.factory.primitive;

import org.eclipse.collections.api.tuple.primitive.BooleanIntPair;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class PrimitiveTuplesApiTest
{
    @Test
    public void createsPairs()
    {
        IntIntPair intIntPair = PrimitiveTuples.pair(1, 2);
        Verify.assertEqualsAndHashCode(PrimitiveTuples.pair(1, 2), intIntPair);

        ObjectIntPair<String> objectIntPair = PrimitiveTuples.pair("one", 1);
        Verify.assertEqualsAndHashCode(PrimitiveTuples.pair("one", 1), objectIntPair);

        BooleanIntPair booleanIntPair = PrimitiveTuples.pair(true, 1);
        Verify.assertEqualsAndHashCode(PrimitiveTuples.pair(true, 1), booleanIntPair);
    }
}
